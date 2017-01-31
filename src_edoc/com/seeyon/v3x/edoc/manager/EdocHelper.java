package com.seeyon.v3x.edoc.manager;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;
import net.joinwork.bpm.engine.wapi.WorkItemManager;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocDocTemplate;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormAcl;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;
import com.seeyon.v3x.edoc.domain.EdocFormFlowPermBound;
import com.seeyon.v3x.edoc.domain.EdocInnerMarkDefinition;
import com.seeyon.v3x.edoc.domain.EdocStat;
import com.seeyon.v3x.edoc.domain.EdocStatCondObj;
import com.seeyon.v3x.edoc.domain.EdocStatDisObj;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.domain.WebEdocStat;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.edoc.webmodel.FormBoundPerm;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.webmail.util.FileUtil;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

public class EdocHelper extends ColHelper {

	 private static final Log log = LogFactory.getLog(EdocHelper.class);
	 private static String baseFileFolder = SystemProperties.getInstance().getProperty("edoc.folder");
	 private static String formFolder = "/form";
	 private static String templateFolder = "/template";

	public static List<SeeyonPolicy> getAllSeeyonPolicy(Long caseId) throws BPMException,EdocException
	{
		List<SeeyonPolicy>  ls=new ArrayList<SeeyonPolicy>();
		List<String>  lsKey=new ArrayList<String>();
		BPMProcess process=null;
		BPMSeeyonPolicy tempPolicy=null;
		String id,name;
		try{
			process = getRunningProcessByCaseId(caseId);
		}catch(ColException e){
			throw new EdocException(e);
		}
		List list=process.getActivitiesList();
        for (int i = 0; i < list.size(); i++) {
            BPMAbstractNode node = (BPMAbstractNode) list.get(i);
            if (node.getNodeType()==BPMAbstractNode.NodeType.humen)
            {
            	tempPolicy=node.getSeeyonPolicy();
            	id=tempPolicy.getId();
            	name=tempPolicy.getName();
            	if(lsKey.contains(id)==false)
            	{
            		lsKey.add(id);
            		ls.add(new SeeyonPolicy(id,name));
            	}
            }
        }
		return ls;
	}

	public static String getWorkFlowInfoScript(Long summaryId,EdocManager edocManager) throws Exception
	{
		StringBuffer sb=new StringBuffer();
        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);

        String caseLogXML = null;
        String caseProcessXML = null;
        String caseWorkItemLogXML = null;

        if (summary != null){
            if (summary.getCaseId() != null) {
                long caseId = summary.getCaseId();
                caseLogXML = edocManager.getCaseLogXML(caseId);
                caseProcessXML = edocManager.getCaseProcessXML(caseId);
                caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
            }
            else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
                String processId = summary.getProcessId();
                caseProcessXML = edocManager.getProcessXML(processId);
            }
        }

        caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);
        caseLogXML = StringEscapeUtils.escapeJavaScript(caseLogXML);
        caseWorkItemLogXML = StringEscapeUtils.escapeJavaScript(caseWorkItemLogXML);

        sb.append("<script>");
        sb.append("parent.caseProcessXML = \"" + caseProcessXML + "\";");
        sb.append("parent.caseLogXML = \"" + caseLogXML + "\";");
        sb.append("parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
        sb.append("parent.selectInsertPeopleOK();");
        sb.append("</script>");

		return sb.toString();
	}
	/**
	 * 检查策略中是否包含“归档”，如果不是封发，去掉归档
	 * @param list
	 * @param permKey
	 */
	public static List<String> checkPerm(List <String> list,String permKey)
	{
		if(list==null){return list;}
		List <String> tempList=new ArrayList<String>();
		if(!"fengfa".equals(permKey))
		{
			if(list.contains("Archive")==false){return list;}
			for(String item:list)
			{
				if(!"Archive".equals(item))
				{
					tempList.add(item);
				}
			}
			return tempList;
		}
		else
		{
			return list;
		}
	}

	public static DataRecord exportStat(HttpServletRequest request,List<EdocStatDisObj> results, EdocStatCondObj esco,String stat_title){

		DataRecord dataRecord = new DataRecord();

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
//		导出excel文件的国际化

		String level_receive = ResourceBundleUtil.getString(resource, local, "edoc.docmark.inner.receive");//收文
		String level_send = ResourceBundleUtil.getString(resource, local, "edoc.docmark.inner.send");//发文
		String stat_signandreport = ResourceBundleUtil.getString(resource, local, "edoc.docmark.inner.signandreport");//签报
		String stat_sum = ResourceBundleUtil.getString(resource, local, "edoc.stat.sum.label");//合计
		String stat_dep = ResourceBundleUtil.getString(resource, local, "edoc.stat.group.dept.label");//发起部门
		String stat_type = ResourceBundleUtil.getString(resource, local, "edoc.stat.group.doctype.label");//公文种类

		String stat_groupType = "";
		if(esco.getGroupType() == Constants.EDOC_STAT_GROUPBY_DOCTYPE){
			stat_groupType = stat_type;
		}else if(esco.getGroupType() == Constants.EDOC_STAT_GROUPBY_DEPT){
			stat_groupType = stat_dep;
		}

		Pagination.setNeedCount(false);

		if (null != results && results.size() > 0) {
			DataRow[] datarow = new DataRow[results.size()];
			for (int i = 0; i < results.size(); i++) {
				EdocStatDisObj stat = results.get(i);
				DataRow row = new DataRow();

				//--start-- 如果行为最后一行或使用公文类型来进行查询,显示的行名国际化
				if(i==results.size()-1 || esco.getGroupType() == Constants.EDOC_STAT_GROUPBY_DOCTYPE){
					String columnName = ResourceBundleUtil.getString(resource, stat.getColumnName());
					row.addDataCell(columnName, 1);
				}else{
					row.addDataCell(stat.getColumnName(), 1);
				}
				//--end--

				row.addDataCell(String.valueOf(stat.getRecieveNum()), 1);
				row.addDataCell(String.valueOf(stat.getSendNum()), 1);
				row.addDataCell(String.valueOf(stat.getSignNum()), 1);
				row.addDataCell(String.valueOf(stat.getTotalNum()), 1);

				datarow[i] = row;
			}
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		String[] columnName = { stat_groupType,level_receive,level_send, stat_signandreport,stat_sum};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(stat_title);
		dataRecord.setSheetName(stat_title);

		return dataRecord;
	}
	//公文查询组装成页面要显示的excel数据
	public static DataRecord exportQueryToWebModel(HttpServletRequest request,List<EdocSummaryModel> results,String excel_title,Integer edocType){

		DataRecord dataRecord = new DataRecord();

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		String com_resouce = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";

		//列名
		String[] columnName=new String[9];
		columnName[0] = ResourceBundleUtil.getString(resource, local, "edoc.element.secretlevel.simple");//文件密级
		columnName[1] = ResourceBundleUtil.getString(com_resouce, local, "common.subject.label");//标题
		columnName[2] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordno.label");//文号
		columnName[6] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.ispig.label");//抄送单位
		columnName[7] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.pigeonholePath.label");//归档路径
		columnName[8] = ResourceBundleUtil.getString(resource, local, "edoc.element.copies");//抄送份数
		String mainToDep="";
		String signPerson="";
		String date ="";
		String datePattern=ResourceBundleUtil.getString(com_resouce, local, "common.datetime.pattern");
		if(edocType.intValue() == EdocEnum.edocType.sendEdoc.ordinal()){
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.element.sendtounit");//主送单位
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.element.issuer");//建文人
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.element.sendingdate");//发文时间
			if (null != results && results.size() > 0) {
				DataRow[] datarow = new DataRow[results.size()];
				for (int i = 0; i < results.size(); i++) {
					EdocSummaryModel summaryModel = (EdocSummaryModel)results.get(i);
					EdocSummary summary=summaryModel.getSummary();
					String hasArchive="";
					if(summary.getHasArchive()){
						hasArchive = ResourceBundleUtil.getString(com_resouce, local, "common.true");
					}else if(!summary.getHasArchive()){
						hasArchive = ResourceBundleUtil.getString(com_resouce, local, "common.false");
					}
					DataRow row = new DataRow();
					row.addDataCell(null!=summary.getSecretLevel() ? String.valueOf(summary.getSecretLevel()) : "", 1);
					row.addDataCell(null!=summary.getSubject() ? String.valueOf(summary.getSubject()) : "", 1);
					row.addDataCell(null!=summary.getDocMark() ? String.valueOf(summary.getDocMark()) : "", 1);
					row.addDataCell(null!=summaryModel.getSendToUnit()? String.valueOf(summaryModel.getSendToUnit()) : "", 1);
					row.addDataCell(null!=summary.getIssuer()? String.valueOf(summary.getIssuer()) : "", 1);
					row.addDataCell(null!=summary.getSigningDate()? String.valueOf(Datetimes.formatDate(summary.getSigningDate())) : "", 1);
					row.addDataCell(null!=hasArchive ? String.valueOf(hasArchive) : "", 1);
					row.addDataCell(null!=summaryModel.getArchiveName()? String.valueOf(summaryModel.getArchiveName()) : "", 1);
					row.addDataCell(null!=summary.getCopies() ? String.valueOf(summary.getCopies()) : "", 1);
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
					log.error(e.getMessage(), e);;
				}
			}

		}else if(edocType.intValue() == EdocEnum.edocType.recEdoc.ordinal()){
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.fromUnit.label");//主送单位
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.regPerson.label");//建文人
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.regDate.label");//发文时间
			if (null != results && results.size() > 0) {
				DataRow[] datarow = new DataRow[results.size()];
				for (int i = 0; i < results.size(); i++) {
					EdocSummaryModel summaryModel = (EdocSummaryModel)results.get(i);
					EdocSummary summary=summaryModel.getSummary();
					String hasArchive="";
					if(summary.getHasArchive()){
						hasArchive = ResourceBundleUtil.getString(com_resouce, local, "common.true");
					}else if(!summary.getHasArchive()){
						hasArchive = ResourceBundleUtil.getString(com_resouce, local, "common.false");
					}
					DataRow row = new DataRow();
					row.addDataCell(null!=summary.getSecretLevel() ? String.valueOf(summary.getSecretLevel()) : "", 1);
					row.addDataCell(null!=summary.getSubject() ? String.valueOf(summary.getSubject()) : "", 1);
					row.addDataCell(null!=summary.getDocMark() ? String.valueOf(summary.getDocMark()) : "", 1);
					row.addDataCell(null!=summary.getSendUnit()? String.valueOf(summary.getSendUnit()) : "", 1);
					row.addDataCell(null!=summary.getCreatePerson()? String.valueOf(summary.getCreatePerson()) : "", 1);
					row.addDataCell(null!=summary.getCreateTime()? String.valueOf(Datetimes.format(summary.getCreateTime(), datePattern)) : "", 1);
					row.addDataCell(null!=hasArchive ? String.valueOf(hasArchive) : "", 1);
					row.addDataCell(null!=summaryModel.getArchiveName()? String.valueOf(summaryModel.getArchiveName()) : "", 1);
					row.addDataCell(null!=summary.getCopies() ? String.valueOf(summary.getCopies()) : "", 1);
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
								log.error(e.getMessage(), e);;
				}
			}
		}else if(edocType.intValue() == EdocEnum.edocType.signReport.ordinal()){
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.element.sendtounit");//主送单位
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.createPerson.label");//建文人
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.createDate.label");//发文时间
			if (null != results && results.size() > 0) {
				DataRow[] datarow = new DataRow[results.size()];
				for (int i = 0; i < results.size(); i++) {
					EdocSummaryModel summaryModel = (EdocSummaryModel)results.get(i);
					EdocSummary summary=summaryModel.getSummary();
					String hasArchive="";
					if(summary.getHasArchive()){
						hasArchive = ResourceBundleUtil.getString(com_resouce, local, "common.true");
					}else if(!summary.getHasArchive()){
						hasArchive = ResourceBundleUtil.getString(com_resouce, local, "common.false");
					}
					DataRow row = new DataRow();
					row.addDataCell(null!=summary.getSecretLevel() ? String.valueOf(summary.getSecretLevel()) : "", 1);
					row.addDataCell(null!=summary.getSubject() ? String.valueOf(summary.getSubject()) : "", 1);
					row.addDataCell(null!=summary.getDocMark() ? String.valueOf(summary.getDocMark()) : "", 1);
					row.addDataCell(null!=summaryModel.getSendToUnit()? String.valueOf(summaryModel.getSendToUnit()) : "", 1);
					row.addDataCell(null!=summary.getCreatePerson()? String.valueOf(summary.getCreatePerson()) : "", 1);
					row.addDataCell(null!=summary.getCreateTime()? String.valueOf(Datetimes.format(summary.getCreateTime(), datePattern)) : "", 1);
					row.addDataCell(null!=hasArchive ? String.valueOf(hasArchive) : "", 1);
					row.addDataCell(null!=summaryModel.getArchiveName()? String.valueOf(summaryModel.getArchiveName()) : "", 1);
					row.addDataCell(null!=summary.getCopies() ? String.valueOf(summary.getCopies()) : "", 1);
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
								log.error(e.getMessage(), e);;
				}
			}
		}
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(excel_title);
		dataRecord.setSheetName(excel_title);
		return dataRecord;
	}

	public static DataRecord exportQuery(HttpServletRequest request,List<EdocStat> results,String excel_title){

		DataRecord dataRecord = new DataRecord();

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		String com_resouce = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
//		导出excel文件的国际化

		String title = ResourceBundleUtil.getString(com_resouce, local, "common.subject.label");//主题
		String wordNo = ResourceBundleUtil.getString(resource, local, "edoc.element.wordno.label");//文号
		String date = ResourceBundleUtil.getString(com_resouce, local, "common.date.sendtime.label");//发文时间
		String signPerson = ResourceBundleUtil.getString(resource, local, "edoc.element.issuer");//签发人
		String mainToDep = ResourceBundleUtil.getString(resource, local, "edoc.element.sendtounit");//主送单位
		String copyToDep = ResourceBundleUtil.getString(resource, local, "edoc.element.copytounit");//抄送单位
		String copies = ResourceBundleUtil.getString(resource, local, "edoc.element.copies");//抄送份数
		String remark = ResourceBundleUtil.getString(resource, local, "edoc.stat.remark.label");//备考

		//Pagination.setNeedCount(false);
		if (null != results && results.size() > 0) {
			DataRow[] datarow = new DataRow[results.size()];
			for (int i = 0; i < results.size(); i++) {
				EdocStat stat = results.get(i);
				DataRow row = new DataRow();

				row.addDataCell(null!=stat.getSubject() ? String.valueOf(stat.getSubject()) : "", 1);
				row.addDataCell(null!=stat.getDocMark() ? String.valueOf(stat.getDocMark()) : "", 1);
				row.addDataCell(null!=stat.getCreateDate() ? String.valueOf(Datetimes.formatDate(stat.getCreateDate())) : "", 1);
				row.addDataCell(null!=stat.getIssuer() ? String.valueOf(stat.getIssuer()) : "", 1);
				row.addDataCell(null!=stat.getSendTo() ? String.valueOf(stat.getSendTo()) : "", 1);
				row.addDataCell(null!=stat.getCopyTo() ? String.valueOf(stat.getCopyTo()) : "", 1);
				row.addDataCell(null!=stat.getCopies() ? String.valueOf(stat.getCopies()) : "", 1);
				row.addDataCell(null!=stat.getRemark() ? String.valueOf(stat.getRemark()) : "", 1);
				datarow[i] = row;
			}
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		String[] columnName = { title,wordNo, date,signPerson, mainToDep, copyToDep, copies,remark};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(excel_title);
		dataRecord.setSheetName(excel_title);

		return dataRecord;
	}
	/**
	 * xgghen
	 * @param request
	 * @param results
	 * @param excel_title
	 * @param edocType
	 * @return
	 */
	public static DataRecord exportQuery(HttpServletRequest request,List<WebEdocStat> results,String excel_title,Integer edocType){

		DataRecord dataRecord = new DataRecord();

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		String com_resouce = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
//		导出excel文件的国际化
		String[] columnName = null ;

		if(edocType.intValue() == EdocEnum.edocType.sendEdoc.ordinal()) {
			columnName = new String[9] ;
			columnName[0] = ResourceBundleUtil.getString(resource, local, "edoc.element.doctype");//公文的种类
			columnName[1] = ResourceBundleUtil.getString(resource, local, "edoc.element.subject");//公文的标题
			columnName[2] = ResourceBundleUtil.getString(resource, local, "edoc.element.secretlevel.simple");//公文的密级
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordno.label");//公文的文号
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.createDate.label");//公文的建文日期
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.element.issuer");//公文的签发人
			columnName[6] = ResourceBundleUtil.getString(resource, local, "edoc.element.sendtounit");//公文主送单位
			columnName[7] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.pigeonholePath.label");//归档路径
			columnName[8] = ResourceBundleUtil.getString(resource, local, "edoc.stat.remark.label");//备考
			if(null != results  && results.size() > 0){
				DataRow[] datarow = new DataRow[results.size()];
				for(int i = 0 ; i < results.size() ; i ++ ) {
					WebEdocStat webEdocStat = results.get(i) ;
					DataRow row = new DataRow();
					row.addDataCell(null!=webEdocStat.getDocType() ? String.valueOf(webEdocStat.getDocType()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSubject() ? String.valueOf(webEdocStat.getSubject()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSecretLevel() ? String.valueOf(webEdocStat.getSecretLevel()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getDocMark() ? String.valueOf(webEdocStat.getDocMark()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getCreateDate() ? String.valueOf(Datetimes.formatDate(webEdocStat.getCreateDate())) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getIssUser() ? String.valueOf(webEdocStat.getIssUser()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSendTo() ? String.valueOf(webEdocStat.getSendTo()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getArchiveName() ? String.valueOf(webEdocStat.getArchiveName() ) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getRemark() ? String.valueOf(webEdocStat.getRemark()) : "", 1) ;
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
					log.error(e.getMessage(), e);;
				}
			}
		}else if(edocType.intValue() == EdocEnum.edocType.recEdoc.ordinal()){
			columnName = new String[9] ;
			columnName[0] = ResourceBundleUtil.getString(resource, local, "edoc.element.doctype");//公文的种类
			columnName[1] = ResourceBundleUtil.getString(resource, local, "edoc.element.subject");//公文的标题
			columnName[2] = ResourceBundleUtil.getString(resource, local, "edoc.element.secretlevel.simple");//公文的密级
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordno.label");//公文的文号
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordinno.label");//内部文号
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.regDate.label");//登记日期
			columnName[6] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.fromUnit.label");//公文来文单位
			columnName[7] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.pigeonholePath.label");//归档路径
			columnName[8] = ResourceBundleUtil.getString(resource, local, "edoc.stat.remark.label");//备考
			if(null != results  && results.size() > 0){
				DataRow[] datarow = new DataRow[results.size()];
				for(int i = 0 ; i < results.size() ; i ++ ) {
					WebEdocStat webEdocStat = results.get(i) ;
					DataRow row = new DataRow();
					row.addDataCell(null!=webEdocStat.getDocType() ? String.valueOf(webEdocStat.getDocType()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSubject() ? String.valueOf(webEdocStat.getSubject()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSecretLevel() ? String.valueOf(webEdocStat.getSecretLevel()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getDocMark() ? String.valueOf(webEdocStat.getDocMark()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSerialNo() ? String.valueOf(webEdocStat.getSerialNo()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getRecviverDate() ? String.valueOf(Datetimes.formatDate(webEdocStat.getRecviverDate())) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getAccount() ? String.valueOf(webEdocStat.getAccount()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getArchiveName() ? String.valueOf(webEdocStat.getArchiveName() ) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getRemark() ? String.valueOf(webEdocStat.getRemark()) : "", 1) ;
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
								log.error(e.getMessage(), e);;
				}
			}
		}else if(edocType.intValue() == EdocEnum.edocType.signReport.ordinal()){
			columnName = new String[9] ;
			columnName[0] = ResourceBundleUtil.getString(resource, local, "edoc.element.doctype");//公文的种类
			columnName[1] = ResourceBundleUtil.getString(resource, local, "edoc.element.subject");//公文的标题
			columnName[2] = ResourceBundleUtil.getString(resource, local, "edoc.element.secretlevel.simple");//公文的密级
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordinno.label");//内部文号
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.createPerson.label");//建文人
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.createDate.label");//建文日期
			columnName[6] = ResourceBundleUtil.getString(resource, local, "edoc.element.sendunit");//建文单位
			columnName[7] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.pigeonholePath.label");//归档路径
			columnName[8] = ResourceBundleUtil.getString(resource, local, "edoc.stat.remark.label");//备考
			if(null != results  && results.size() > 0){
				DataRow[] datarow = new DataRow[results.size()];
				for(int i = 0 ; i < results.size() ; i ++ ) {
					WebEdocStat webEdocStat = results.get(i) ;
					DataRow row = new DataRow();
					row.addDataCell(null!=webEdocStat.getDocType() ? String.valueOf(webEdocStat.getDocType()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSubject() ? String.valueOf(webEdocStat.getSubject()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSecretLevel() ? String.valueOf(webEdocStat.getSecretLevel()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSerialNo() ? String.valueOf(webEdocStat.getSerialNo()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getCreateUser() ? String.valueOf(webEdocStat.getCreateUser()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getCreateDate() ? String.valueOf(Datetimes.formatDate(webEdocStat.getCreateDate())) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getAccount() ? String.valueOf(webEdocStat.getAccount()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getArchiveName() ? String.valueOf(webEdocStat.getArchiveName() ) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getRemark() ? String.valueOf(webEdocStat.getRemark()) : "", 1) ;
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
								log.error(e.getMessage(), e);;
				}
			}
		}else if (edocType == 999) { //查询归档公文
			columnName = new String[10] ;
			columnName[0] = ResourceBundleUtil.getString(resource, local, "edoc.element.doctype");//公文的种类
			columnName[1] = ResourceBundleUtil.getString(resource, local, "edoc.element.subject");//公文的标题
			columnName[2] = ResourceBundleUtil.getString(resource, local, "edoc.element.secretlevel.simple");//公文的密级
			columnName[3] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordno.label");//公文的文号
			columnName[4] = ResourceBundleUtil.getString(resource, local, "edoc.element.wordinno.label");//内部文号
			columnName[5] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.createPerson.label") ;//建文人
			columnName[6] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.pigeonhole.label");//归档日期
			columnName[7] = ResourceBundleUtil.getString(resource, local, "edoc.form.sort");//类型
			columnName[8] = ResourceBundleUtil.getString(resource, local, "edoc.edoctitle.pigeonholePath.label");//归档路径
			columnName[9] = ResourceBundleUtil.getString(resource, local, "edoc.stat.remark.label");//备考
			if(null != results  && results.size() > 0){
				DataRow[] datarow = new DataRow[results.size()];
				for(int i = 0 ; i < results.size() ; i ++ ) {
					WebEdocStat webEdocStat = results.get(i) ;
					DataRow row = new DataRow();
					row.addDataCell(null!=webEdocStat.getDocType() ? String.valueOf(webEdocStat.getDocType()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSubject() ? String.valueOf(webEdocStat.getSubject()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSecretLevel() ? String.valueOf(webEdocStat.getSecretLevel()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getDocMark() ? String.valueOf(webEdocStat.getDocMark()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getSerialNo() ? String.valueOf(webEdocStat.getSerialNo()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getCreateUser() ? String.valueOf(webEdocStat.getCreateUser()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getArchivedTime() ? String.valueOf(Datetimes.formatDate(webEdocStat.getArchivedTime())) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getEdocType() ? String.valueOf(webEdocStat.getEdocType()) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getArchiveName() ? String.valueOf(webEdocStat.getArchiveName() ) : "", 1) ;
					row.addDataCell(null!=webEdocStat.getRemark() ? String.valueOf(webEdocStat.getRemark()) : "", 1) ;
					datarow[i] = row;
				}
				try {
					dataRecord.addDataRow(datarow);
				} catch (Exception e) {
								log.error(e.getMessage(), e);;
				}
			}
		}
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(excel_title);
		dataRecord.setSheetName(excel_title);
		return dataRecord;
	}


	public static DataRecord exportEdocElement(HttpServletRequest request,List<EdocElement> elementList,String element_title){

		//MetadataManager metadataManager= (MetadataManager)ApplicationContextHolder.getBean("metadataManager");

		DataRecord dataRecord = new DataRecord();

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		//String com_resouce = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
//		导出excel文件的国际化

		String elementName = ResourceBundleUtil.getString(resource, local, "edoc.element.elementName");//元素名称
		String elementCode = ResourceBundleUtil.getString(resource, local, "edoc.element.elementfieldName");//元素代码
		String dataType = ResourceBundleUtil.getString(resource, local, "edoc.element.elementType");//数据类型
		String elementType = ResourceBundleUtil.getString(resource, local, "edoc.element.elementIsSystem"); //元素类型
		String state = ResourceBundleUtil.getString(resource, local, "edoc.element.elementStatus"); //元素状态
		String disabled = ResourceBundleUtil.getString(resource, local, "edoc.element.disabled");//元素状态-停用
		String enabled = ResourceBundleUtil.getString(resource, local, "edoc.element.enabled");//元素状态-启用

		//Pagination.setNeedCount(false);

		if (null != elementList && elementList.size() > 0) {
			DataRow[] datarow = new DataRow[elementList.size()];
			for (int i = 0; i < elementList.size(); i++) {
				EdocElement element = elementList.get(i);
				DataRow row = new DataRow();
				String name = element.getName();
				String dType = "";
				String dTypeLable = "";
				String eType = ResourceBundleUtil.getString(resource, local, "edoc.element.userType");;

				boolean isSystem = element.getIsSystem();
				if(isSystem){
					if(null!=name && !"".equals(name)){
						name = ResourceBundleUtil.getString(resource, local, name);
						eType = ResourceBundleUtil.getString(resource, local, "edoc.element.systemType");
					}
				}

				switch(element.getType()){

					case EdocElement.C_iElementType_Comment : dTypeLable = "edoc.element.comment";break;
					case EdocElement.C_iElementType_Date : dTypeLable = "edoc.element.date";break;
					case EdocElement.C_iElementType_Decimal : dTypeLable = "edoc.element.decimal";break;
					case EdocElement.C_iElementType_Integer : dTypeLable = "edoc.element.integer";break;
					case EdocElement.C_iElementType_List : dTypeLable = "edoc.element.list";break;
					case EdocElement.C_iElementType_LogoImg : dTypeLable = "edoc.element.img";break;
					case EdocElement.C_iElementType_String : dTypeLable = "edoc.element.string";break;
					case EdocElement.C_iElementType_Text : dTypeLable = "edoc.element.text";break;
				}

				dType = ResourceBundleUtil.getString(resource, local, dTypeLable);

				row.addDataCell(null!= name ? String.valueOf(name) : "", 1);
				row.addDataCell(null!= element.getFieldName() ? String.valueOf(element.getFieldName()) : "", 1);
				row.addDataCell(null!= dType ? String.valueOf(dType) : "", 1);
				row.addDataCell(null!= eType ? String.valueOf(eType) : "", 1);
				row.addDataCell(element.getStatus() ==1? enabled : disabled, 1);

				datarow[i] = row;
			}
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		String[] columnName = { elementName , elementCode , dataType , elementType,state};
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(element_title);
		dataRecord.setSheetName(element_title);

		return dataRecord;
	}

	/**
	 * 公文年度编号变更
	 * @return
	 */
	private static String currentEdocMarkYear="";
	private static String edocMarkCat="edoc_mark_year";
	private static String edocMarkCatItem="edoc_mark_year_cuurent";
	private static ConfigManager configMgr=(ConfigManager)ApplicationContextHolder.getBean("configManager");
	public static void checkDocmarkByYear()
	{
		try
		{
			Calendar cal=Calendar.getInstance();
			int iYear=cal.get(Calendar.YEAR);

			ConfigItem cf=null;
			if("".equals(currentEdocMarkYear))
			{//启动后没有进行初始化时候,config中读取
				cf=configMgr.getConfigItem(edocMarkCat,edocMarkCatItem,1L);
				if(cf==null)
				{//数据库中未记录
					cf=new ConfigItem();
					cf.setIdIfNew();
					cf.setConfigCategory(edocMarkCat);
					cf.setConfigItem(edocMarkCatItem);
					cf.setConfigValue(Integer.toString(iYear));
					configMgr.addConfigItem(cf);
				}
				currentEdocMarkYear=cf.getConfigValue();
			}
			if(!currentEdocMarkYear.equals(Integer.toString(iYear)))
			{
				EdocMarkManager edocMarkManager= (EdocMarkManager)ApplicationContextHolder.getBean("edocMarksManager");
				edocMarkManager.turnoverCurrentNoAnnual();
				cf=configMgr.getConfigItem(edocMarkCat,edocMarkCatItem,1L);
				cf.setConfigValue(Integer.toString(iYear));
				configMgr.updateConfigItem(cf);
				currentEdocMarkYear=Integer.toString(iYear);
			}
		}catch(Exception e)
		{
		}
	}

	/**
	 * 根据公文类型返回该公文单所包含的处理意见列表
	 * @param elementList 公文单的元素列表
	 * @param edocType 公文类型
	 * @return
	 */
	public static List<FormBoundPerm> getProcessOpinionFromEdocForm(List<String> elementList, int edocType)throws Exception{

		/*
		User user = CurrentUser.get();

		String category = "";

		if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
			category = MetadataNameEnum.edoc_send_permission_policy.name();
		}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
			category = MetadataNameEnum.edoc_rec_permission_policy.name();
		}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
			category =  MetadataNameEnum.edoc_qianbao_permission_policy.name();
		}*/

		//List<FormBoundPerm> returnList = new ArrayList<FormBoundPerm>();
		List<FormBoundPerm> boundPermList = new ArrayList<FormBoundPerm>();

		//FlowPermManager flowPermManager= (FlowPermManager)ApplicationContextHolder.getBean("flowPermManager");
		//MetadataManager metadataManager= (MetadataManager)ApplicationContextHolder.getBean("metadataManager");

		EdocElementManager edocElementManager = (EdocElementManager)ApplicationContextHolder.getBean("edocElementManager");
		//得到所有启用的公文意见元素列表
		//List<EdocElement> tempList = edocElementManager.getByStatusAndType(EdocElement.C_iStatus_Active, EdocElement.C_iElementType_Comment);
		//List<String> processList = new ArrayList<String>();
		//for(EdocElement element : tempList){
		//	processList.add(element.getFieldName());
		//}

		//根据类别查处所有该类别下的节点权限，再将他们的名字保存到一个LIST集合中
		//*自定义的权限保存的是国际化资源的KEY值
		//String label = "";
		String value = "";
		String processName = "";
		String processItemName = "";
		ResourceBundle r = null;
		//List<FlowPerm> flowList = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, user.getLoginAccount());
		FormBoundPerm otherOpinionPerm = null;
		for(String fieldName : elementList){
			EdocElement ele = edocElementManager.getByFieldName(fieldName);

			if(null!=ele && ele.getType() != EdocElement.C_iElementType_Comment)continue;

			if(null!=ele && null!=ele.getName()){
				boolean isNotBound = false;
				if("niban".equals(ele.getFieldName())&& edocType == 2) isNotBound = true;
				FormBoundPerm formBoundPerm = new FormBoundPerm();
				formBoundPerm.setPermItem(ele.getFieldName());
				if(ele.getIsSystem() == true){
					r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
					value = ResourceBundleUtil.getString(r, ele.getName());
					if(!isNotBound){
						processName = value;
						processItemName = ele.getFieldName();
					}else{
						processName = "";
						processItemName = "";
					}
				}else{
					value = ele.getName();
					processName = "";
					processItemName = "";
				}
				formBoundPerm.setPermName(value);
				if(fieldName.equalsIgnoreCase("otherOpinion")){
					otherOpinionPerm = formBoundPerm;
					continue;
				}
				formBoundPerm.setProcessName(processName);
				formBoundPerm.setPermItemName(processItemName);
				boundPermList.add(formBoundPerm);
			}

		}
		if(null!=otherOpinionPerm){
			boundPermList.add(otherOpinionPerm);//把处理意见加到最后
		}

		/*
		for(String str : elementList){
				if(null!=label && !"".equals(label)){
				r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
				value = ResourceBundleUtil.getString(r, label);
						}
					}else{
						value = perm.getName();
					}
				FormBoundPerm formBoundPerm = new FormBoundPerm();
				formBoundPerm.setPermItem(perm.getName());
				formBoundPerm.setPermName(value);
				boundPermList.add(formBoundPerm);
			}
		}

		//循环比较处理意见的名称，如果元素列表中包含处理意见，放入返回列表中
		for(FormBoundPerm perm:boundPermList){
			if(elementList.contains(perm.getPermItem())){
				returnList.add(perm);
			}
		}
		*/

		return boundPermList;
	}
	public static Long getFlowPermAccountId(Long defaultAccountId, EdocSummary summary, TempleteManager templeteManager){
		Long flowPermAccountId = defaultAccountId;
    	if(summary != null){
    		if(summary.getTempleteId() != null){
    			Templete templete = templeteManager.get(summary.getTempleteId());
    			if(templete != null){
    				flowPermAccountId = templete.getOrgAccountId();
    			}
    		}
    		else{
    			if(summary.getOrgAccountId() != null){
    				flowPermAccountId = summary.getOrgAccountId();
    			}
    		}
    	}
    	return flowPermAccountId;
	}
	public static String getCategoryName(int edocType){
		String category = "";
		if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
			category = MetadataNameEnum.edoc_send_permission_policy.name();
		}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
			category = MetadataNameEnum.edoc_rec_permission_policy.name();
		}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
			category =  MetadataNameEnum.edoc_qianbao_permission_policy.name();
		}
		return category;
	}
	/**
	 * 查找节点权限的备选操作，用于初始化节点权限选择池
	 * @param elementList
	 * @param edocType
	 * @return
	 * @throws Exception
	 */
	public static String getProcessOpinionFromEdocFormOperation(List<String> elementList, int edocType)throws Exception{

		User user = CurrentUser.get();

		String category = getCategoryName(edocType);

		String returnString = "";
		List<FormBoundPerm> boundPermList = new ArrayList<FormBoundPerm>();

		FlowPermManager flowPermManager= (FlowPermManager)ApplicationContextHolder.getBean("flowPermManager");
		MetadataManager metadataManager= (MetadataManager)ApplicationContextHolder.getBean("metadataManager");

		//根据类别查处所有该类别下的节点权限，再将他们的名字保存到一个LIST集合中
		//*自定义的权限保存的是国际化资源的KEY值
		String label = "";
		String value = "";
		ResourceBundle r = null;
		List<FlowPerm> flowList = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, user.getLoginAccount());
		for(FlowPerm perm:flowList){
			if(elementList.contains(perm.getName())){
				if(perm.getType().intValue() == FlowPerm.Node_Type_System.intValue()){
						label = metadataManager.getMetadataItemLabel(perm.getCategory(), perm.getName());
						if(null!=label && !"".equals(label)){
						r = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",CurrentUser.get().getLocale());
							value = ResourceBundleUtil.getString(r, label);
						}
					}else{
						value = perm.getName();
					}
				FormBoundPerm formBoundPerm = new FormBoundPerm();
				formBoundPerm.setPermItem(perm.getName());
				formBoundPerm.setPermName(value);
				boundPermList.add(formBoundPerm);
			}
		}

		//循环比较处理意见的名称，如果元素列表中包含处理意见，放入返回列表中
		for(FormBoundPerm perm:boundPermList){
			if(elementList.contains(perm.getPermItem())){
				returnString += "("+ perm.getPermItem() +")";
			}
		}

		return returnString;
	}


	public static List<FormBoundPerm> getProcessOpinionByEdocFormId(List<String> elementList,long edocFormId, int edocType,long accountId)throws Exception{

		EdocFormManager edocFormManager= (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		EdocElementManager edocElementManager = (EdocElementManager)ApplicationContextHolder.getBean("edocElementManager");

		List<FormBoundPerm> boundPermList = new ArrayList<FormBoundPerm>();

		String value = "";
		ResourceBundle r = null;

		FormBoundPerm otherOpinionPerm = null;

		for(String fieldName : elementList){

			EdocElement ele = edocElementManager.getByFieldName(fieldName);
			if(null!=ele && ele.getType() != EdocElement.C_iElementType_Comment)continue;

			if(null!=ele && null!=ele.getName()){
			FormBoundPerm formBoundPerm = new FormBoundPerm();
			formBoundPerm.setPermItem(ele.getFieldName());
			if(ele.getIsSystem() == true){
				r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
				value = ResourceBundleUtil.getString(r, ele.getName());
			}else{
				value = ele.getName();
			}
			formBoundPerm.setPermName(value);
			if(fieldName.equalsIgnoreCase("otherOpinion")){
				otherOpinionPerm = formBoundPerm;
				continue;
			}
			boundPermList.add(formBoundPerm);
			}
		}
		if(null!=otherOpinionPerm){
			List<EdocFormFlowPermBound> list = edocFormManager
					.findBoundByFormId(edocFormId, otherOpinionPerm
							.getPermItem());
			for (EdocFormFlowPermBound bound : list) {
				otherOpinionPerm.setSortType(bound.getSortType());
			}
			boundPermList.add(otherOpinionPerm);// 把处理意见加到最后
		}


		for(FormBoundPerm perm : boundPermList){
			if (null != perm.getPermName()
					&& perm.getPermName().equalsIgnoreCase("otherOpinion"))
				continue;
			List<EdocFormFlowPermBound> list = edocFormManager.findBoundByFormId(edocFormId, perm.getPermItem(),accountId);
			String str_temp = "";
			String str_temp_b = "";
			for(EdocFormFlowPermBound bound : list){
				str_temp += bound.getFlowPermNameLabel();
				str_temp += ",";
				EdocElement element = null;
				element = edocElementManager.getByFieldName(bound.getFlowPermName());
				if(null!=element && element.getIsSystem()){//查找元素，如果非空证明是系统预置的fuhe,shenpi....节点权限和系统预置的处理意见
					str_temp_b += bound.getFlowPermName();
				}else if(element == null){//如果element为空，那么
					element = edocElementManager.getByFieldName(bound.getProcessName());//在用processName查
					if(null!=element && element.getIsSystem()){//节点权限为自定义但处理意见元素为系统的
						str_temp_b += bound.getFlowPermName();
					}else if(null!=element && (element.getIsSystem()==false)){//节点权限为自定义而且处理意见元素也是扩展的
						str_temp_b += bound.getFlowPermName();
					}
				}
				str_temp_b += ",";
				perm.setSortType(bound.getSortType());
			}
			if(str_temp.endsWith(",") && str_temp_b.endsWith(",")){
				str_temp = str_temp.substring(0, str_temp.length()-1);
				str_temp_b = str_temp_b.substring(0, str_temp_b.length()-1);
			}
			perm.setPermItemName(str_temp);
			perm.setPermItemList(str_temp_b);
		}

		return boundPermList;

	}

	public static String getLogoURL(){

			User user = CurrentUser.get();
			Long accountId = user.getLoginAccount();
			return getLogoURL(accountId);
			//在修改方法中,首先进行一次替换,如果之前设置的为默认logo, 那么将logo置空
			//String url = MainDataLoader.getInstance().getLogoImagePath(accountId);
			//return "<img src='/seeyon"+url+"' />";

	}
	public static String getLogoURL(long accountId){
		//在修改方法中,首先进行一次替换,如果之前设置的为默认logo, 那么将logo置空
		String url = MainDataLoader.getInstance().getLogoImagePath(accountId);
		return "<img src='/seeyon"+url+"' />";
	}

	/**
	 * 检查是否有公文单及套红模板文件存，如果不存在复制一份到指定分区
	 * @throws Exception
	 */
	public static void copyEdocFile()throws Exception{

		String[] t_FileIds = new String[1];

		t_FileIds[0] = "-6001972826857714844"; //套红模板文件压缩包


		String[] f_FileIds = new String[3];
		// -- 公文单（签报，收文，发文）
		f_FileIds[0] = "-1766191165740134579";
		f_FileIds[1] = "-2921628185995099164";
		f_FileIds[2] = "6071519916662539448";

		copyFile(t_FileIds, Constants.EDOC_FILE_TYPE_TEMPLATE);
		copyFile(f_FileIds, Constants.EDOC_FILE_TYPE_EDOCFORM);

	}

	public static void copyFile(String[] fileIds, int type)throws Exception{

		String fileFolder = baseFileFolder;
		if(type == Constants.EDOC_FILE_TYPE_EDOCFORM){
			fileFolder += formFolder;
		}
		else if(type == Constants.EDOC_FILE_TYPE_TEMPLATE){
			fileFolder += templateFolder;
		}




		FileManager fileManager = (FileManager)ApplicationContextHolder.getBean("fileManager");

		for(String id : fileIds){
			V3XFile v3xFile=fileManager.getV3XFile(Long.valueOf(id));
			if(null != v3xFile){
				File file = fileManager.getFile(v3xFile.getId());
					if(null == file){
						File tempFile = new File(fileFolder+"\\/" + id);
						if(null!=tempFile){
							String folder = fileManager.getFolder(v3xFile.getCreateDate(), true);
					    	FileUtil.copy(fileFolder+"\\/" + id, folder + "\\/" + id);
						}
					}
			}
		}

	}

	/**
	 * 为新建单位复制内部公文文号
	 * @param accountId
	 */
	public static void generateEdocInnerMarkByAccountId(long accountId){
		try{
		log.info("开始为新建单位复制内部公文文号...");
		EdocInnerMarkDefinitionManager edocInnerMarkDefinitionManager = (EdocInnerMarkDefinitionManager)ApplicationContextHolder.getBean("edocInnerMarkDefinitionManager");
		List<EdocInnerMarkDefinition> list = edocInnerMarkDefinitionManager.getEdocInnerMarkDefsList(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if(list == null) return;
		for(EdocInnerMarkDefinition def : list){
			EdocInnerMarkDefinition newDef = new EdocInnerMarkDefinition();
			newDef.setIdIfNew();
			newDef.setCurrentNo(def.getCurrentNo());
			newDef.setDomainId(accountId);
			newDef.setExpression(def.getExpression());
			newDef.setLength(def.getLength());
			newDef.setMaxNo(def.getMaxNo());
			newDef.setMinNo(def.getMinNo());
			newDef.setType(def.getType());
			newDef.setWordNo(def.getWordNo());
			newDef.setYearEnabled(def.getYearEnabled());
			edocInnerMarkDefinitionManager.create(newDef);
		}
		log.info("内部公文文号复制完毕");
		}catch(Exception e){
			log.error("!为新建单位复制文号失败",e);
		}
	}

	/**
	 * 为新建单位复制公文套红模板
	 * @param accountId
	 * @deprecated
	 */
	public static void generateEdocTemplateForm(long accountId){

		User user = CurrentUser.get();
		try{
			log.info("开始为新建单位复制公文套红模板...");
			EdocDocTemplateManager edocDocTemplateManager = (EdocDocTemplateManager)ApplicationContextHolder.getBean("edocDocTemplateManager");
			List<EdocDocTemplate> list = edocDocTemplateManager.getAllTemplateByAccountId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			for(EdocDocTemplate template : list){
				EdocDocTemplate newTemplate = new EdocDocTemplate();
				newTemplate.setIdIfNew();
				newTemplate.setCreateTime(new Timestamp(System.currentTimeMillis()));
				newTemplate.setAclEntity(null);
				newTemplate.setCreateUserId(user.getId());
				newTemplate.setDescription(template.getDescription());
				newTemplate.setDomainId(accountId);
				newTemplate.setGrantNames(template.getGrantNames());
				newTemplate.setLastUpdate(new Timestamp(System.currentTimeMillis()));
				newTemplate.setLastUserId(user.getId());
				newTemplate.setStatus(template.getStatus());
				newTemplate.setTemplateAcls(null);
				newTemplate.setTemplateFileId(new Long(1));
				newTemplate.setTextType(template.getTextType());
				newTemplate.setType(template.getType());
				newTemplate.setFileUrl(null);

			}
			log.info("内部公文文号复制完毕");
			}catch(Exception e){
				log.error("!为新建单位复制文号失败",e);
			}

	}

	/**
	 * 另一套复制方法，直接调用manager中的方法
	 * @param accountId
	 */
	public static void generateEdocTemplateFormOringinal(long accountId){

		try{
			log.info("开始为新建单位复制公文套红模板...");
			EdocDocTemplateManager edocDocTemplateManager = (EdocDocTemplateManager)ApplicationContextHolder.getBean("edocDocTemplateManager");
			edocDocTemplateManager.addEdocTemplate(accountId);

			log.info("内部公文文号复制完毕");
			}catch(Exception e){
				log.error("!为新建单位复制文号失败",e);
			}
	}

	/**
	 * 每建一个单位，为新单位生成一套公文单，一套套红模板，一套内部文号
	 * @param accountId
	 */
	public static void generateZipperFleet(long accountId){
		generateEdocFormByAccountId(accountId);
		//generateEdocTemplateFormOringinal(accountId);
		generateEdocInnerMarkByAccountId(accountId);
		//公文元素
		generateEdocElementByAccountId(accountId);
		//公文开关
		generateEdocSwitchKeyByAccountId(accountId);
	}
	/**
	 * 复制公文开关数据
	 * @param accountId
	 */
	private static void generateEdocSwitchKeyByAccountId(long accountId) {
		log.info("开始为新建单位复制公文开关...");
		try{
			ConfigManager configManager = (ConfigManager)ApplicationContextHolder.getBean("configManager");
			configManager.saveInitCmpConfigData(IConfigPublicKey.EDOC_SWITCH_KEY,accountId);
		}catch(Exception e){
			log.error("新建单位的时候复制公文开关异常",e);
		}
		log.info("复制系统公文开关结束。");

	}

	/**
	 * 新建单位复制公文元素
	 * @param accountId
	 */
	private static void generateEdocElementByAccountId(long accountId) {
		log.info("开始为新建单位复制系统公文元素...");
		try{
			EdocElementManager edocElementManager = (EdocElementManager)ApplicationContextHolder.getBean("edocElementManager");
			edocElementManager.initCmpElement();
		}catch(Exception e){
			log.error("新建单位的时候复制系统公文元素异常",e);
		}
		log.info("复制系统公文元素结束。");
	}

	/**
	 * 为新建单位复制公文单
	 * @param accountId
	 */
	public static void generateEdocFormByAccountId(long accountId){

		log.info("开始为新建单位复制公文单...");

		try{
		User user = CurrentUser.get();


		FileManager fileManager = (FileManager)ApplicationContextHolder.getBean("fileManager");
		AttachmentManager attachmentManager = (AttachmentManager)ApplicationContextHolder.getBean("attachmentManager");

		EdocFormManager  edocFormManager = (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		V3xOrgAccount account = orgManager.getRootAccount();
		List<EdocForm> forms = edocFormManager.getEdocFormByAcl(String.valueOf(account.getId()));
		for(EdocForm form : forms){
			if(form.getIsSystem()) continue;
			EdocFormExtendInfo info = new EdocFormExtendInfo();
			info.setIdIfNew();
			info.setAccountId(accountId);
			info.setStatus(Constants.EDOC_USELESS);
			info.setIsDefault(false);
			info.setEdocForm(form);
			info.setOptionFormatSet("0,0,0");
			edocFormManager.saveEdocFormExtendInfo(info);
		}

		List<EdocForm> accountList= edocFormManager.getAllEdocForms(V3xOrgEntity.VIRTUAL_ACCOUNT_ID); // 查出预置的公文单数据
		for(EdocForm form : accountList){
			EdocForm newForm = new EdocForm();
			Set<EdocFormAcl> edocFormAcls = new HashSet<EdocFormAcl>();
			newForm.setIdIfNew();
			newForm.setContent(form.getContent());
			newForm.setCreateTime(new Timestamp(System.currentTimeMillis()));
			newForm.setCreateUserId(user.getId());
			newForm.setDescription(form.getDescription());
			newForm.setName(form.getName());
			newForm.setDomainId(accountId);
			newForm.setShowLog(false);
			newForm.setStatus(form.getStatus());
			newForm.setType(form.getType());
			newForm.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			newForm.setEdocFormAcls(edocFormAcls);
			newForm.setIsDefault(true);
			newForm.setIsSystem(true);

			//复制扩展信息
			Set<EdocFormExtendInfo> infos = new HashSet<EdocFormExtendInfo>();
			EdocFormExtendInfo info = new EdocFormExtendInfo();
			info.setIdIfNew();
			info.setAccountId(newForm.getDomainId());
			info.setEdocForm(newForm);
			info.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_USEED);
			info.setIsDefault(true);
			info.setOptionFormatSet("0,0,0");

			infos.add(info);
			newForm.setEdocFormExtendInfo(infos);


			//复制授权信息
			Set<EdocFormAcl> acls = new HashSet<EdocFormAcl>();
			EdocFormAcl acl = new EdocFormAcl();
			acl.setIdIfNew();
			acl.setDomainId(newForm.getDomainId());
			acl.setFormId(newForm.getId());
			acl.setEntityType(V3xOrgEntity.ORGENT_TYPE_ACCOUNT);

			acls.add(acl);
			newForm.setEdocFormAcls(acls);

			// -- 复制新公文元素 -- start --
			Set<EdocFormElement> oldFormElements = form.getEdocFormElements();
	    	Set<EdocFormElement> newFormElements = new HashSet<EdocFormElement>();
			for(EdocFormElement oldElement : oldFormElements){
	    		EdocFormElement newElement = new EdocFormElement();
	    		newElement.setIdIfNew();
	    		newElement.setElementId(oldElement.getElementId());
	    		newElement.setFormId(newForm.getId());
	    		newFormElements.add(newElement);
	    	}
			newForm.setEdocFormElements(newFormElements);
			// -- end --


			// -- 复制新公文节点权限绑定  -- end --
			Set<EdocFormFlowPermBound> oldFormBounds = form.getEdocFormFlowPermBound();
			Set<EdocFormFlowPermBound> newFormBounds = new HashSet<EdocFormFlowPermBound>();

			for(EdocFormFlowPermBound oldBound : oldFormBounds){
				EdocFormFlowPermBound newBound = new EdocFormFlowPermBound();
				newBound.setIdIfNew();
				newBound.setEdocFormId(newForm.getId());
				newBound.setFlowPermName(oldBound.getFlowPermName());
				newBound.setFlowPermNameLabel(oldBound.getFlowPermNameLabel());
				newBound.setProcessName(oldBound.getProcessName());
				newBound.setDomainId(accountId);
				newFormBounds.add(newBound);
			}
			newForm.setEdocFormFlowPermBound(newFormBounds);
			// -- end --

			//Long fileId=form.getFileId();
			//if(fileId==null){fileId=0L;}
			//try{
			// v3xfile=fileManager.getV3XFile(fileId);
			//if(v3xfile!=null){
				//V3XFile newFile = fileManager.clone(v3xfile.getId(), false);
				//newFile.setAccountId(accountId);
				//List list = new ArrayList();
				//list.add(newFile);
				//attachmentManager.create(list, ApplicationCategoryEnum.edoc, newForm.getId(), newForm.getId());
				//newForm.setFileId(newFile.getId());
			//}
			newForm.setFileId(form.getFileId());
//			}catch(Exception e){
//				log.error(e.getMessage(), e);;
//			}


			edocFormManager.saveEdocForm(newForm);
		}
		}catch(Exception e){
			log.error("为新建单位复制公文单失败", e);
		}
		log.info("公文单复制完毕");
	}

	public static void exportToExcel(HttpServletRequest request,
			HttpServletResponse response,FileToExcelManager fileToExcelManager
			,String title,DataRecord dataRecord) throws Exception{
		try {
			fileToExcelManager.save(request, response, title, dataRecord);
			//fileToExcelManager.save(request, response,title, "location.href", dataRecord);
		} catch (Exception e) {
			log.error(e.getMessage(), e);;
		}
	}

	public static boolean hasNodeExist(Affair affair,Map<String,String> conditions){
		if(conditions == null || conditions.size()==0)
			return true;
		try{
			if(affair != null && affair.getSubObjectId() != null){
	            WorkItem workitem = ColHelper.getWorkItemById(affair.getSubObjectId());
	            long caseId = workitem.getCaseId();
	            BPMProcess process = getRunningProcessByCaseId(caseId);
	            for(String nodeId:conditions.keySet()){
	            	if(process.getActivityById(nodeId)==null){
	            		return false;
	            	}
	            }
			}
		}catch(Exception e){
			log.error("校验分支节点发生错误", e);
		}
		return true;
	}


	/**
	 *
	 * @param summary
	 * @param senderAccountId 发起者所在单位Id
	 * @param templeteManager
	 * @return
	 */
    public static Long getFlowPermAccountId(EdocSummary summary,Long senderAccountId,TempleteManager templeteManager ) {
    	return getFlowPermAccountId(senderAccountId,summary.getTempleteId(),summary.getOrgAccountId(),templeteManager);
    }

    public static void createQuartzJobOfSummary(EdocSummary summary, WorkTimeManager workTimeManager){
    	createQuartzJob(ApplicationCategoryEnum.edoc, summary.getId(), summary.getCreateTime(),
    			summary.getDeadline(), summary.getAdvanceRemind(), summary.getOrgAccountId(), workTimeManager);
    }

    public static void deleteQuartzJobOfSummary(EdocSummary summary){
    	deleteQuartzJob(summary.getId());
    }
	public static String getName(String ids,String seperator) throws Exception{
		 String[] sids = ids.split("[,]");
		 String name = "";
		 for(String sid : sids){
			 String[] std =  sid.split("[|]");
			 Long acccountId = Long.valueOf(std[1]);
			 if("".equals(name)){
				 name = EdocRoleHelper.getAccountById(acccountId).getName();
			 }else{
				 name += seperator+EdocRoleHelper.getAccountById(acccountId).getName();
			 }
		 }
		 return name;
	}

	public static String getI18nSeperator(HttpServletRequest request){
		String seperator = "、";
		try{
			Locale locale =Functions.getLocale(request);
			String	sep   = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", locale,"common.separator.label");
			if(Strings.isNotBlank(sep)) seperator = sep;
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}
		return seperator;
	}
	 public static void reLoadAccountName(EdocSummary summary,String seperator){
			try{
				//主送单位s
				if(Strings.isNotBlank(summary.getSendUnit()) && Strings.isNotBlank(summary.getSendUnitId())){
					 String name = getName(summary.getSendUnitId(),seperator);
					 if(Strings.isNotBlank(name))  summary.setSendUnit(name);
				}
				//send_to
				if(Strings.isNotBlank(summary.getSendTo()) && Strings.isNotBlank(summary.getSendToId())){
					 String name = getName(summary.getSendToId(),seperator);
					 if(Strings.isNotBlank(name))  summary.setSendTo(name);
				}

				//reportto
				if(Strings.isNotBlank(summary.getReportTo()) && Strings.isNotBlank(summary.getReportToId())){
					 String name = getName(summary.getReportToId(),seperator);
					 if(Strings.isNotBlank(name))  summary.setReportTo(name);
				}

				//copyTo
				if(Strings.isNotBlank(summary.getCopyTo()) && Strings.isNotBlank(summary.getCopyToId())){
					 String name = getName(summary.getCopyToId(),seperator);
					 if(Strings.isNotBlank(name))  summary.setCopyTo(name);
				}

				if(Strings.isNotBlank(summary.getPrintUnitId()) && Strings.isNotBlank(summary.getPrintUnit())){
					String name = getName(summary.getPrintUnitId(),seperator);
					if(Strings.isNotBlank(name)) summary.setPrintUnit(name);
				}
			}catch(Exception e){
				log.error(e);
			}
		}

	 	/**
	 	 * 获取归档的archiveId
	 	 * @param templeteId
	 	 * @param manager
	 	 * @return
	 	 */
	 	public static Long getTempletePrePigholePath(Long templeteId, TempleteManager manager){
		 if(templeteId == null){
			 return null;
		 }
		 Templete templete = manager.get(templeteId);
		 if(templete != null){
			 EdocSummary summary = (EdocSummary) XMLCoder.decoder(templete.getSummary());
			 if(summary != null){
				 Long archiveId = summary.getArchiveId();
				 if(archiveId != null){
					 return archiveId;
				 }
			 }
		 }
		 return null;
	 }
}