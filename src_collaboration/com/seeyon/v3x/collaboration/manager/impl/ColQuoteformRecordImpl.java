package com.seeyon.v3x.collaboration.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_DataSource;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.define.data.inf.ISeeyonDataSource.IDataArea;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.dao.ColQuoteformRecordDao;
import com.seeyon.v3x.collaboration.domain.ColQuoteformRecord;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColQuoteformRecordManger;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.util.Strings;


public class ColQuoteformRecordImpl implements ColQuoteformRecordManger {
	
	private final static Log log = LogFactory.getLog(ColQuoteformRecordImpl.class);
	
	private ColQuoteformRecordDao colQuoteformRecordDao ;
	private ColManager colManager ;
    private AffairManager affairManager;

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }
	private final String ColSummayIdStr = "colSummaryId" ;
	private final String FieldState = "fieldState" ;
	private final String FieldName = "fieldName" ;
	private final String FormType = "formType";
	
	public void setColQuoteformRecordDao(ColQuoteformRecordDao colQuoteformRecordDao) {
		this.colQuoteformRecordDao = colQuoteformRecordDao;
	}

	public boolean create(HttpServletRequest request,Long summaryId, Long formAppId, Long masterId) throws Exception {
		List<ColQuoteformRecord> colQuoteformRecords = getColQuoteFromRequset(request,summaryId,formAppId,masterId) ;
		try{
			saveColQuoteformRecords(colQuoteformRecords) ;
		}catch(Exception e){
			log.error("保存对象出现问题", e) ;
		}
		
		return true ;
	}
	
	public void saveColQuoteformRecords(List<ColQuoteformRecord> colQuoteformRecords){
		if(colQuoteformRecords == null){
			return ;
		}
		for(ColQuoteformRecord colQuoteformRecord : colQuoteformRecords){
			 saveColQuoteformRecord(colQuoteformRecord) ;
		}
	}
	
	public void saveColQuoteformRecord(ColQuoteformRecord colQuoteformRecord){
		if(colQuoteformRecord == null){
			return ;
		}
		colQuoteformRecordDao.create(colQuoteformRecord) ;
	}
	
	private List<ColQuoteformRecord> getColQuoteFromRequset(HttpServletRequest request,Long summaryId, Long formAppId, Long masterId) throws SeeyonFormException{
		String[] colSummaryIds = request.getParameterValues(ColSummayIdStr) ;
		String[] fieldStates = request.getParameterValues(FieldState) ;
		String[] fieldName = request.getParameterValues(FieldName) ;
		String[] formType = request.getParameterValues(FormType);
		String[] sort = request.getParameterValues("r_sort");
		return getColQuoteFromRequset(fieldName,colSummaryIds,fieldStates, summaryId,formType,sort,formAppId,masterId) ;
	}
	
	private List<ColQuoteformRecord> getColQuoteFromRequset(String[] fieldName ,String[] colSummaryIds,String[] fieldStates,Long summaryId,String[] formType,String[] sort, Long formAppId, Long masterId) throws SeeyonFormException{
		List<ColQuoteformRecord> colQuoteformRecords = new ArrayList<ColQuoteformRecord>();
		if (colSummaryIds == null || fieldStates == null || summaryId == null || fieldName == null || masterId == null) {
			return colQuoteformRecords;
		}
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(formAppId);
		SeeyonDataDefine fdatadefine = (SeeyonDataDefine)fapp.getDataDefine();		  
		InfoPath_DataSource dataSource = (InfoPath_DataSource) fdatadefine.getDataSource() ;
		String masterTableName = dataSource.getMasterTableName();
		Map<String, List<Map<String, String>>> fieldMap = FormHelper.loadFormPojoById(masterId, formAppId);
		//Map<String, List<String>> allDataMap = FormHelper.getDataMapFromMap(fieldMap.values(),null);
		for(int i = 0 ; i < colSummaryIds.length ; i++){
			IDataArea fdataArea = dataSource.findDataAreaByName(FormHelper.getfileNameName(fieldName[i]));
			String dbTableName = fdataArea.getDBTableName();
			Long subRecordId = null;
			if(dbTableName.startsWith("formson")){
				//List<String> subIdList = allDataMap.get(dbTableName + "_id");
				//List<String> sortList /Map.get("sort");
				List<Map<String, String>> dataMapList = fieldMap.get(masterTableName + "_" + dbTableName);
				if(dataMapList != null){
					for (Map<String, String> map : dataMapList) {
						String tSort = map.get("sort");
						if(Strings.isNotBlank(tSort) && tSort.equals(sort[i])){
							String subRecordIdStr = map.get(dbTableName + "_id");
							if(Strings.isNotBlank(subRecordIdStr)){
								subRecordId = Long.parseLong(subRecordIdStr);
							}
							break;
						}
					}
				}
			}
			if(ColQuoteformRecord.State_ADD.equals(fieldStates[i])){
				colQuoteformRecordDao.delChangedColQuoteRecord(summaryId, fieldName[i], subRecordId);
				ColQuoteformRecord colQuoteformRecord = new ColQuoteformRecord();
				colQuoteformRecord.setIdIfNew();
				colQuoteformRecord.setColSummaryId(summaryId);
				colQuoteformRecord.setRefColSummaryId(Long.valueOf(colSummaryIds[i]));
				colQuoteformRecord.setFieldName(fieldName[i]);
				colQuoteformRecord.setSubRecordId(subRecordId);
				User u =CurrentUser.get();
				colQuoteformRecord.setType(Integer.valueOf(formType[i]));
				colQuoteformRecord.setMemberId(u.getId());
				colQuoteformRecords.add(colQuoteformRecord);
			}else if(ColQuoteformRecord.State_DEL.equals(fieldStates[i])){
				//colQuoteformRecordDao.delQuoteformRecord(summaryId,Long.valueOf(colSummaryIds[i]),fieldName[i]);
				colQuoteformRecordDao.delChangedColQuoteRecord(summaryId, fieldName[i], subRecordId);
			}			
		}
		return colQuoteformRecords ;
	}
	
	public boolean delAll(Long colSummayId) throws Exception {
		if(colSummayId == null){
			return false;
		}
		
		colQuoteformRecordDao.delAllQuoteformRecord(colSummayId) ;
		
		return true ;
	}
	
	public String ajaxGetQuote(Long colSummayId,String _selfColSummary,boolean showTree,Integer width) throws Exception {
		List<Long> quoteIdList = new ArrayList<Long>() ;
		quoteIdList.add(colSummayId) ;
		List<Long> queryList = this.getQuoteIdListBySummayId(colSummayId) ;
		if(queryList != null && showTree){
			quoteIdList.addAll(queryList) ;
		}
		StringBuffer str = new StringBuffer();
		int i = 0;
		for(Long colId : quoteIdList){
			if(Strings.isNotBlank(_selfColSummary) && _selfColSummary.equals(colId +"")){
				continue ;
			}
			//ColSummary colSummary = colManager.getSimpleColSummaryById(colId) ;
			ColSummary colSummary = colManager.getColSummaryById(colId, false);
			if(colSummary != null){
				if(showTree && i >= 1){
					str.append("<br>") ;
					str.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;") ;
				}
				String showValue = colSummary.getSubject();
				String affairId = colManager.getSenderAffairIdBysummaryId(colId);
				Affair affair = affairManager.getCollaborationSenderAffair(colId);
				if(affair!=null && affair.getSubState() == SubStateEnum.col_waitSend_cancel.getKey()){
					String contextPath = WebUtil.getRequest().getContextPath();
					str.append("<img src='").append(contextPath).append("/common/images/space.gif").append("' border='0' height='16' width='16' align='absmiddle' class='toolbar-button-icon' style='background-position: -112px -32px; margin-right: 3px;'>");
				}
				str.append("<a class=\"like-a\" style='font-size:12px' title=\"" + showValue + "\" onclick=\"openDetail('','from=Sent&affairId="+affairId+"&baseObjectId=" + _selfColSummary + "&refColSummaryId=" + colSummayId + "')\">") ; ;
				//str.append("<img src='/seeyon/common/images/attachmentICON/collaboration.gif' border='0' height='16' width='16' align='absmiddle' style='margin-right: 3px;'>") ;
				str.append("<span class='flowState_" + colSummary.getState() + "'></span>");
				str.append((width != null && width != 0) ? Functions.getLimitLengthString(showValue, width/7, "...") : showValue) ;
				str.append("</a>") ;
				i++;
			}
		}
		return str.toString();
	}
	
	
	public List<Long> getQuoteIdListBySummayId(Long colSummayId)throws Exception{
		return colQuoteformRecordDao.getQuoteIdList(colSummayId) ;
	}
	

	public ColQuoteformRecord getColQuoteformRecord(Long summaryId, String fieldName) throws Exception {
		return colQuoteformRecordDao.getColQuoteformRecord(summaryId, fieldName);
	}
	public ColQuoteformRecord getColQuoteformRecord(Long summaryId, Long subRecordId, String fieldName) throws Exception{
		return colQuoteformRecordDao.getColQuoteformRecord(summaryId,subRecordId,fieldName);
	}
	public String getRefColSummaryId(Long summaryId, Long subRecordId, String fieldName) throws Exception {
		ColQuoteformRecord colQuoteformRecord = colQuoteformRecordDao.getColQuoteformRecord(summaryId,subRecordId,fieldName);
		String refColSummaryId = null;
		if(colQuoteformRecord != null){
			refColSummaryId = colQuoteformRecord.getRefColSummaryId().toString();
		}
		return refColSummaryId;
	}
	public void delChangedColQuoteRecord(Long summaryId, String fieldName) throws Exception {
		colQuoteformRecordDao.delChangedColQuoteRecord(summaryId, fieldName);
	}
	
	public void delChangedColQuoteRecord(Long summaryId, String fieldName,Long subRecordId) throws Exception {
		colQuoteformRecordDao.delChangedColQuoteRecord(summaryId, fieldName, subRecordId);
	}
}
