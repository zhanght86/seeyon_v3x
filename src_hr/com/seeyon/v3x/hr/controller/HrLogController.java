package com.seeyon.v3x.hr.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.hr.log.StaffTransferLog;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.HqlSearchHelper;
import com.seeyon.v3x.hr.util.OperationLogHelper;
import com.seeyon.v3x.hr.webmodel.WebOperationLog;
import com.seeyon.v3x.organization.dao.InvalidEntityDAO;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Datetimes;

public class HrLogController extends BaseController {
	private transient static final Log LOG = LogFactory
	.getLog(HrLogController.class); 
	
	private OperationlogManager operationlogManager;
	private OrgManagerDirect orgManagerDirect;
	private InvalidEntityDAO invalidEntityDAO;
	private SearchManager searchManager;
	private FileToExcelManager fileToExcelManager;
	private FileManager fileManager;

	
	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView initLog(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/log");
		String ids = request.getParameter("ids");
		String model = RequestUtils.getStringParameter(request, "model");
		mav.addObject("model", model);
		mav.addObject("ids", ids);
		return mav;
	}
	
	/**
	 * 查看\查询 --日志方法
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView viewLog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/log");
		List<OperationLog> operationLogs = new ArrayList<OperationLog>();
		List<WebOperationLog> webOperationLogs = new ArrayList<WebOperationLog>();
		Long accountId = CurrentUser.get().getLoginAccount();
		String model = RequestUtils.getStringParameter(request, "model");
		String ids = request.getParameter("ids");
		String condition = RequestUtils.getStringParameter(request, "condition");
		String textfield = request.getParameter("textfield");
		String textfield1 = request.getParameter("textfield1");
		if(condition!=null && (condition.equals("actionTime")||condition.equals("actionName")||condition.equals("actionType"))){
			if(model.equals("transfer")){
				operationLogs = HqlSearchHelper.searchLog(condition,textfield,textfield1,Constants.MODULE_TRANSFER,searchManager);
			}
			else if(model.equals("staff")){
				if(textfield!=null&&!StringUtils.isBlank(textfield)){
					operationLogs = HqlSearchHelper.searchLog(condition,textfield,textfield1,Constants.MODULE_STAFF,searchManager);
				}else{
					operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId,Constants.MODULE_STAFF, true);
				}
				
			}
			mav.addObject("isLoad", "unLoad");			
		}else{
			if(ids != null && !ids.equals("")){
				List<Long> objectIds = this.toLongList(ids);
				operationLogs = this.operationlogManager.getAllOperationLog(objectIds, true);
			}else{
				if(model.equals("transfer"))
					operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId,Constants.MODULE_TRANSFER, true);
				else if(model.equals("staff"))
					operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId,Constants.MODULE_STAFF, true);
			}
			mav.addObject("isLoad", "load");
		}
		
		
		webOperationLogs = this.toWebOperationLogList(operationLogs,model);
		int size=webOperationLogs.size();
		
		mav.addObject("size", size);
		mav.addObject("webOperationLogs", webOperationLogs);
		mav.addObject("model", model);		
		mav.addObject("ids", ids);
		return mav;
	}
	
	//方法废弃了
	public ModelAndView searchLog(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/log");
		List<OperationLog> operationLogs = new ArrayList<OperationLog>();
		String model = RequestUtils.getStringParameter(request, "model");
		String condition = RequestUtils.getStringParameter(request, "condition");
		String textfield = request.getParameter("textfield");
		String textfield1 = request.getParameter("textfield1");
		if(model.equals("transfer"))
			operationLogs = HqlSearchHelper.searchLog(condition,textfield,textfield1,Constants.MODULE_TRANSFER,searchManager);
		else if(model.equals("staff"))
			operationLogs = HqlSearchHelper.searchLog(condition,textfield,textfield1,Constants.MODULE_STAFF,searchManager);
		mav.addObject("webOperationLogs",this.toWebOperationLogList(operationLogs,model));
		mav.addObject("model", model);
		mav.addObject("size", operationLogs.size());
		mav.addObject("condition", condition);
		mav.addObject("textfield", textfield);
		mav.addObject("isLoad", "unLoad");
		return mav;
	}
	
	private List<WebOperationLog> toWebOperationLogList(List<OperationLog> operationLogs, String model)throws BusinessException{
		List<WebOperationLog> webOperationLogs = new ArrayList<WebOperationLog>();
		if(operationLogs.size() != 0){
			for(OperationLog operationLog : operationLogs){
				WebOperationLog webOperationLog = new WebOperationLog();
				String staffName = "";
				V3xOrgMember orgMember = this.orgManagerDirect.getMemberById(operationLog.getMemberId());
				if(orgMember != null){
					staffName = orgMember.getName();
					if(model.equals("transfer")){
						StaffTransferLog staffTransferLog = (StaffTransferLog)OperationLogHelper.decoder(operationLog.getContentParameters());
						webOperationLog.setStaffTransferLog(staffTransferLog);
					}else if(model.equals("staff")){
						String operation = (String)OperationLogHelper.decoder(operationLog.getContentParameters());
						webOperationLog.setOperation(operation);
					}
					webOperationLog.setStaffName(staffName);
					webOperationLog.setOperationLog(operationLog);
					webOperationLogs.add(webOperationLog);
				}else{
					orgMember = invalidEntityDAO.findMemberById(operationLog.getMemberId());
					if(orgMember!=null){
						staffName = orgMember.getName();
						if(model.equals("transfer")){
							StaffTransferLog staffTransferLog = (StaffTransferLog)OperationLogHelper.decoder(operationLog.getContentParameters());
							webOperationLog.setStaffTransferLog(staffTransferLog);
						}else if(model.equals("staff")){
							String operation = (String)OperationLogHelper.decoder(operationLog.getContentParameters());
							webOperationLog.setOperation(operation);
						}
						webOperationLog.setStaffName(staffName);
						webOperationLog.setOperationLog(operationLog);
						webOperationLogs.add(webOperationLog);						
					}
				}
			}
		}
		return webOperationLogs;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public SearchManager getSearchManager() {
		return searchManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
	
	/**
	 * 转换
	 * @author lucx
	 *
	 */
	private List<Long> toLongList(String sIdStr) throws BusinessException {
		List<Long> sIds = new ArrayList<Long>();
		if (null != sIdStr && !sIdStr.equals("")) {
			String[] salaryIds = sIdStr.split(",");
			for (String strId : salaryIds) {
				if (null == strId || "".equals(strId))
					continue;
				Long id = Long.parseLong(strId);
				LOG.debug("mIdStr: " + id);
				sIds.add(id);
			}
		}
		return sIds;
	}
	
	/**
	 * 导出excel
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView exportExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		    List<OperationLog> operationLogs =new ArrayList<OperationLog>();
		    Long accountId = CurrentUser.get().getLoginAccount();
		    String condition = "";
			String textfield = "";
			String textfield1 = "";
			String model =request.getParameter("model");
			String isLoad = request.getParameter("isLoad");

			if(model.equals("transfer")){
				if(isLoad.equals("load")){
					String ids = request.getParameter("ids");
					if(ids != null && !ids.equals(""))
						operationLogs = this.operationlogManager.getAllOperationLog(this.toLongList(ids), false);
					else
						operationLogs = this.operationlogManager.queryBySubObjectId(Constants.MODULE_TRANSFER, false);
				}
				else if(isLoad.equals("unLoad")){
					condition = request.getParameter("condition");
					textfield = request.getParameter("textfield");String ids = request.getParameter("ids");
					textfield1 = request.getParameter("textfield1");
					if(ids != null && !ids.equals("")){
						operationLogs = this.operationlogManager.getAllOperationLog(this.toLongList(ids), false);
					}else
						operationLogs = HqlSearchHelper.searchLog(condition,textfield,textfield1,Constants.MODULE_TRANSFER,searchManager);
				}
			}else if(model.equals("staff")){
				if(isLoad.equals("load")){
//					operationLogs = this.operationlogManager.queryBySubObjectId(Constants.MODULE_STAFF, true);//fei qi 
					operationLogs = this.operationlogManager.queryBySubObjectIdAndObjectId(accountId,Constants.MODULE_STAFF,false);
				}else if(isLoad.equals("unLoad")){
					condition = request.getParameter("condition");
					textfield = request.getParameter("textfield");
					textfield1 = request.getParameter("textfield1");
					operationLogs = HqlSearchHelper.searchLog(condition,textfield,textfield1,Constants.MODULE_STAFF,searchManager);
				}
			}
			
			Locale local = LocaleContext.getLocale(request);
			String resource = Constants.RESOURCE_HR;
            String form=ResourceBundleUtil.getString(resource, local, "hr.log.form.label");
            String userName=ResourceBundleUtil.getString(resource, local, "hr.log.userName.label");
            String type11=ResourceBundleUtil.getString(resource, local, "hr.log.operation.type.label");
            String operationTime=ResourceBundleUtil.getString(resource,local,"hr.log.operationTime.label");
            String ip=ResourceBundleUtil.getString(resource, local, "hr.log.ip.label");
            String note=ResourceBundleUtil.getString(resource, local,"hr.log.operation.note.label");
            List<WebOperationLog> webOperationLogs=this.toWebOperationLogList(operationLogs, model);
			DataRecord record = new DataRecord();
			record.setSheetName(form);
			record.setTitle(form);
			String[] columnNames = {userName,type11,operationTime,ip,note};
			record.setColumnName(columnNames);
		for(WebOperationLog webOperationLog : webOperationLogs){	
			DataRow row = new DataRow();	
			row.addDataCell(webOperationLog.getStaffName(),DataCell.DATA_TYPE_TEXT);
			row.addDataCell(ResourceBundleUtil.getString(resource, local, webOperationLog.getOperationLog().getActionType()),DataCell.DATA_TYPE_TEXT);
			row.addDataCell(Datetimes.formatDatetime(webOperationLog.getOperationLog().getActionTime()), DataCell.DATA_TYPE_DATE);
			row.addDataCell(webOperationLog.getOperationLog().getRemoteIp(),DataCell.DATA_TYPE_TEXT);
			if(model.equals("transfer")){
				row.addDataCell(ResourceBundleUtil.getString(resource, local, webOperationLog.getStaffTransferLog().getStaffName())+"  "+ResourceBundleUtil.getString(resource, local, webOperationLog.getStaffTransferLog().getStaffTransferType().getType_name()),DataCell.DATA_TYPE_TEXT);
			}else if(model.equals("staff")){
				row.addDataCell(ResourceBundleUtil.getString(resource, local, webOperationLog.getOperationLog().getActionType())+"  "+ResourceBundleUtil.getString(resource, local, webOperationLog.getOperation()),DataCell.DATA_TYPE_TEXT);
			}
			record.addDataRow(row);
		}
		if(model.equals("transfer"))
			this.fileToExcelManager.save(request,response, "transferLog", "location.href",record);
		else if(model.equals("staff"))
			this.fileToExcelManager.save(request,response, "staffLog", "location.href",record);
		
		return null;
	}

	public InvalidEntityDAO getInvalidEntityDAO() {
		return invalidEntityDAO;
	}

	public void setInvalidEntityDAO(InvalidEntityDAO invalidEntityDAO) {
		this.invalidEntityDAO = invalidEntityDAO;
	}				
	
}
