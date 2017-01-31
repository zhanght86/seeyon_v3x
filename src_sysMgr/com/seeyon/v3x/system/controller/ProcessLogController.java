package com.seeyon.v3x.system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.common.processlog.his.manager.HisProcessLogManager;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class ProcessLogController extends BaseController {
	
	private ProcessLogManager processLogManager;
	
	private HisProcessLogManager hisProcessLogManager;
	
	private FileToExcelManager fileToExcelManager;
	
	private OrgManager orgManager;	

	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}
	
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setHisProcessLogManager(HisProcessLogManager hisProcessLogManager) {
		this.hisProcessLogManager = hisProcessLogManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
    public ModelAndView processLogIframe(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("sysMgr/processLog/listProcessIframe");
    	return mav;
    }
    /**
     * 流程日志导出excel
     * @author
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    public ModelAndView exportExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String processId = request.getParameter("processId");
    	List<ProcessLog> processLogList = new ArrayList<ProcessLog>();
    	if(Strings.isNotBlank(processId)){
    		processLogList = processLogManager.getLogsByProcessId(Long.valueOf(processId),false);
    		if(processLogList == null || processLogList.isEmpty()){
    			processLogList = hisProcessLogManager.getLogsByProcessId(Long.valueOf(processId), false);
    		}
    	}
    	
    	String processLog = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.title.label") ;
    	
    	String number = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.number.label") ;
    	String actionuser = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.actionuser.label") ;
    	String date = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.date.label") ;
    	String content = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.content.label") ;
    	String description = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.description.label") ;
    	String opinion = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.opinion.label") ;
    	String[] columnName = {number,actionuser,date,content,description,opinion} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(processLog) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	for (int i = 0 ; i < processLogList.size() ; i ++) {
    		ProcessLog data = processLogList.get(i) ;
    		DataRow dataRow = new DataRow();
    		dataRow.addDataCell(String.valueOf(i+1), DataCell.DATA_TYPE_INTEGER) ; 
    		dataRow.addDataCell(orgManager.getMemberById(data.getActionUserId()).getName(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getActionTime() != null ? Datetimes.format(data.getActionTime(), Datetimes.datetimeWithoutSecondStyle).toString() : "-", DataCell.DATA_TYPE_DATE) ;
    		dataRow.addDataCell(data.getActionName(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getActionDesc(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getParam5() == null ? "" : data.getParam5(), DataCell.DATA_TYPE_TEXT) ;
    		dataRecord.addDataRow(dataRow);
    	}
    	fileToExcelManager.saveAsCSV(request,response,processLog,dataRecord);
    	
    	return null ;
    }
    
    /**
     * 导出流程日志 vsv 格式
     * @author macj 2009-8-24
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
    public ModelAndView processLogList(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("sysMgr/processLog/listProcessLog");
    	String processId = request.getParameter("processId");
    	//lijl添加,用来区别是从协同中进入的还是从公文中进入的页面是showFlowNodeDetailFrame.jsp
    	String appName=request.getParameter("appName");
    	List<ProcessLog> processLogList = new ArrayList<ProcessLog>();
    	if(Strings.isNotBlank(processId)){
    		processLogList = processLogManager.getLogsByProcessId(Long.valueOf(processId));
    		if(processLogList == null || processLogList.isEmpty()){
    			processLogList = hisProcessLogManager.getLogsByProcessId(Long.valueOf(processId));
    		}
    	}
    	mav.addObject("processLogList", processLogList);
    	mav.addObject("appName", appName);
    	return mav;
    }
    
    /**
     * 导出流程日志--公文
     * @author macj 2009-8-24
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    public ModelAndView exportExcelEdoc(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("sysMgr/processLog/listProcessLog");
    	String processId = request.getParameter("processId");
    	List<ProcessLog> processLogList = new ArrayList<ProcessLog>();
    	if(Strings.isNotBlank(processId)){
    		processLogList = processLogManager.getLogsByProcessId(Long.valueOf(processId));
    	}
    	String processLog = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.title.label") ;
    	
    	String number = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.number.label") ;
    	String actionuser = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.actionuser.label") ;
    	String date = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.date.label") ;
    	String content = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.content.label") ;
    	String description = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.description.label") ;
    	String opinion = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.opinion.label") ;
    	String[] columnName = {number,actionuser,date,content,description,opinion} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(processLog) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	for (int i = 0 ; i < processLogList.size() ; i ++) {
    		ProcessLog data = processLogList.get(i) ;
    		DataRow dataRow = new DataRow();
    		dataRow.addDataCell(String.valueOf(i+1), DataCell.DATA_TYPE_INTEGER) ; 
    		dataRow.addDataCell(orgManager.getMemberById(data.getActionUserId()).getName(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getActionTime() != null ? Datetimes.format(data.getActionTime(), Datetimes.datetimeWithoutSecondStyle).toString() : "-", DataCell.DATA_TYPE_DATE) ;
    		//dataRow.addDataCell(data.getActionTime().toString(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getActionName(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getActionDesc(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getParam5() == null ? "" : data.getParam5(), DataCell.DATA_TYPE_TEXT) ;
    		dataRecord.addDataRow(dataRow);
    	}
    	fileToExcelManager.saveAsCSV(request,response,processLog,dataRecord);
    	
    	return null;
    }
    
}
