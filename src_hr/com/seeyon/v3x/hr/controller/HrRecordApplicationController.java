package com.seeyon.v3x.hr.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.SeeyonDataDefine;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.manager.DynamicFormManager;
import com.seeyon.v3x.hr.manager.RecordManager;
import com.seeyon.v3x.hr.util.TempleteHelper;
import com.seeyon.v3x.hr.webmodel.WebEvectionForm;
import com.seeyon.v3x.hr.webmodel.WebLeaveForm;
import com.seeyon.v3x.hr.webmodel.WebOverTimeForm;
import com.seeyon.v3x.hr.webmodel.WebRecord;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.util.Datetimes;

public class HrRecordApplicationController extends BaseController {
	private transient static final Log LOG = LogFactory
		.getLog(HrRecordApplicationController.class);
	
	private RecordManager recordManager;
	private OrgManagerDirect orgManagerDirect;
	private DynamicFormManager dynamicFormManager;
	
	public DynamicFormManager getDynamicFormManager() {
		return dynamicFormManager;
	}

	public void setDynamicFormManager(DynamicFormManager dynamicFormManager) {
		this.dynamicFormManager = dynamicFormManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public RecordManager getRecordManager() {
		return recordManager;
	}

	public void setRecordManager(RecordManager recordManager) {
		this.recordManager = recordManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initRecordType(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String key = request.getParameter("key");
		if(key.equals("1"))
			return recordLeaveForm(request, response);
		else if(key.equals("2"))
			return recordOvertime(request,response);
		else if(key.equals("3"))
			return recordEvection(request,response);
		else 
			return staffRecordList(request,response);
	}
	
	public ModelAndView recordLeaveForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/recordLeaveForm");
		String aAppName = TempleteHelper.getInstance().getFName("1");
		String tableName = getTableName(aAppName);
		List list = this.dynamicFormManager.findFormByTabelName(tableName);
        //Object obj[] = (Object[])list.get(0);
		if(null == list)
			list = new ArrayList();
		mav.addObject("leaveForm", pagenate(toWebLeaveForm(list)));
		mav.addObject("resultCount", list.size());
		mav.addObject("tableName", tableName);
		return mav;
	}
	public ModelAndView recordOvertime(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/recordOvertime");
		String aAppName = TempleteHelper.getInstance().getFName("2");
		String tableName = getTableName(aAppName);
		List list = this.dynamicFormManager.findOverTimeByTableName(tableName);
		if(null == list)
			list = new ArrayList();
		mav.addObject("overTimeForm", pagenate(toWebOverTimeForm(list)));
		mav.addObject("resultCount", list.size());
		mav.addObject("tableName", tableName);
		return mav;
	}
	public ModelAndView recordEvection(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/recordEvection");
		String aAppName = TempleteHelper.getInstance().getFName("3");
		String tableName = getTableName(aAppName);
		List list = this.dynamicFormManager.findFormByTabelName(tableName);
		if(null == list)
			list = new ArrayList();
		mav.addObject("evectionForm", pagenate(toWebEvectionForm(list)));
		mav.addObject("resultCount", list.size());
		mav.addObject("tableName", tableName);
		return mav;
	}
	
	public ModelAndView deleteForms(HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		String dynamicForm = request.getParameter("dynamicForm");
		String tableName = request.getParameter("tableName");
		List<Long> ids = toLongList(request.getParameter("ids"));
		if(dynamicForm.equals("1") || dynamicForm.equals("3")){
			this.dynamicFormManager.updateLeaveAndEvectionForm(tableName, ids);
		}else if(dynamicForm.equals("2")){
			this.dynamicFormManager.updateOverTimeForm(tableName, ids);
		}
		return super.redirectModelAndView("/hrRecord.do?method=recordManagerHomeListEntry&recordType="+dynamicForm,"parent");
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView staffRecordList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/initStaffRecordList");
		List<Record> records = this.recordManager.getAllStaffRecords(new Date());
		List<WebRecord> webRecords = new ArrayList<WebRecord>();
		for(Record record : records){
			WebRecord webRecord = new WebRecord();
			String staffName = orgManagerDirect.getMemberById(record.getStaffer_id()).getName();
			String staffDeptName = orgManagerDirect.getDepartmentById(record.getDep_id()).getName();
			webRecord.setDepartment(staffDeptName);
			webRecord.setName(staffName);
			webRecord.setRecord(record);
			webRecords.add(webRecord);
		}
		mav.addObject("records", webRecords);
		mav.addObject("resultCount", webRecords.size());
		return mav;
	}
	
	public ModelAndView viewDynamicForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/detailApplication");
		Long id = RequestUtils.getLongParameter(request, "id");
		String tableName = request.getParameter("tableName");
		String xml = dynamicFormManager.getDynamicFormXML(id,tableName);
		mav.addObject("formXML", xml);
		return mav;
	}
	
	public ModelAndView searchFormByName(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = null;
		String type = request.getParameter("type");
		String tableName = request.getParameter("tableName");
		String staffName = request.getParameter("staffName");
		List list = new ArrayList();
		if(type.equals("1")){
			mav = new ModelAndView("hr/record/recordLeaveForm");
			list = this.dynamicFormManager.searchLeaveFormByMemberName(staffName, tableName);
			mav.addObject("leaveForm", pagenate(toWebLeaveForm(list)));
			mav.addObject("resultCount", list.size());
			mav.addObject("tableName", tableName);
		}else if(type.equals("2")){
			mav = new ModelAndView("hr/record/recordOvertime");
			list = this.dynamicFormManager.searchOverTimeFormByMemberName(staffName, tableName);
			mav.addObject("overTimeForm", pagenate(toWebOverTimeForm(list)));
			mav.addObject("resultCount", list.size());
			mav.addObject("tableName", tableName);
		}else if(type.equals("3")){
			mav = new ModelAndView("hr/record/recordEvection");
			list = this.dynamicFormManager.searchEvectionFormByMemberName(staffName, tableName);
			mav.addObject("evectionForm", pagenate(toWebEvectionForm(list)));
			mav.addObject("resultCount", list.size());
			mav.addObject("tableName", tableName);
		}
		return mav;
	}
	
	public ModelAndView searchFormByDate(HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ModelAndView mav = null;
		String type = request.getParameter("type");
		String fTime = request.getParameter("fromTime");
		String tTime = request.getParameter("toTime");
         try{
         	Date fromTime = Datetimes.parseDate(fTime);
         	Date toTime = Datetimes.parseDate(tTime);
         
		String tableName = request.getParameter("tableName");
		List list = new ArrayList();
		if(type.equals("1")){
			mav = new ModelAndView("hr/record/recordLeaveForm");
			list = this.dynamicFormManager.getLeaveFormByDate(fromTime, fromTime, tableName);
			mav.addObject("leaveForm", pagenate(toWebLeaveForm(list)));
			mav.addObject("resultCount", list.size());
			mav.addObject("tableName", tableName);
		}else if(type.equals("2")){
			mav = new ModelAndView("hr/record/recordOvertime");
			list = this.dynamicFormManager.getOverTimeFormByDate(fromTime, fromTime, tableName);
			mav.addObject("overTimeForm", pagenate(toWebOverTimeForm(list)));
			mav.addObject("resultCount", list.size());
			mav.addObject("tableName", tableName);
		}else if(type.equals("3")){
			mav = new ModelAndView("hr/record/recordEvection");
			list = this.dynamicFormManager.getEvectionFormByDate(fromTime, fromTime, tableName);
			mav.addObject("evectionForm", pagenate(toWebEvectionForm(list)));
			mav.addObject("resultCount", list.size());
			mav.addObject("tableName", tableName);
		}
         }
         catch (Exception e) {
        	 LOG.error("", e);
         } 
		return mav;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		LOG.debug("first: " + first + ", pageSize: " + pageSize + ", size: "
				+ list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	private String getTableName(String name){
		String tableName = "";
		if(name != null && !name.equals("")){
			ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findByName(name);
			SeeyonForm_ApplicationImpl sapp = (SeeyonForm_ApplicationImpl)afapp;
			if(sapp != null){
				SeeyonDataDefine seedade = (SeeyonDataDefine) sapp.getDataDefine();
				if(seedade != null){
					tableName=seedade.getDataDefine().getTableLst().get(0).getName();
				}
			}
		}
		return tableName;
	}
	
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
	
	private List<WebLeaveForm> toWebLeaveForm(List list)throws Exception{
		List<WebLeaveForm> leaveFormList = new ArrayList<WebLeaveForm>();
		if(list != null && !list.isEmpty()){
			for(Object object : list){
				Object[] obj = (Object[])object;
				WebLeaveForm form = new WebLeaveForm();
				form.setId(Long.parseLong(obj[0].toString()));
				form.setName(obj[5].toString());
				form.setApplicationDate(obj[2].toString());
				form.setLeaveType(obj[6].toString());
				if(obj[9]!=null){
					form.setFromDate(obj[9].toString());
				}
				if(obj[10]!=null){
					form.setEndDate(obj[10].toString());
				}
				if(obj[7]!=null){
					form.setDays(obj[7].toString());
				}
				if(obj[8]!=null){
					form.setReason(obj[8].toString());
				}				
				leaveFormList.add(form);
			}
		}
		return leaveFormList;
	}
	
	private List<WebEvectionForm> toWebEvectionForm(List list)throws Exception{
		List<WebEvectionForm> evectionFormList = new ArrayList<WebEvectionForm>();
		if(list != null && !list.isEmpty()){
			for(Object object : list){
				Object[] obj = (Object[])object;
				WebEvectionForm form = new WebEvectionForm();
				form.setId(Long.parseLong(obj[0].toString()));
				form.setName(obj[3].toString());
				form.setApplicationDate(obj[2].toString());
				if(obj[10]!=null){
					form.setSite(obj[10].toString());
				}
				if(obj[6]!=null){
					form.setFromDate(obj[6].toString());
				}
				if(obj[8]!=null){
					form.setEndDate(obj[8].toString());
				}
				if(obj[7]!=null){
				    form.setReason(obj[7].toString());
				}
				evectionFormList.add(form);
			}
		}
		return evectionFormList;
	}
	
	private List<WebOverTimeForm> toWebOverTimeForm(List list)throws Exception{
		List<WebOverTimeForm> overTimeFormList = new ArrayList<WebOverTimeForm>();
		if(list != null && !list.isEmpty()){
			for(Object object : list){
				Object[] obj = (Object[])object;
				WebOverTimeForm form = new WebOverTimeForm();
				form.setId(Long.parseLong(obj[0].toString()));
				form.setName(obj[3].toString());
				form.setApplicationDate(obj[2].toString());
				if(obj[6]!=null){
					form.setFromDate(obj[6].toString());
				}
				if(obj[7]!=null){
					form.setEndDate(obj[7].toString());
				}
				if(obj[8]!=null){
					form.setPeriod(obj[8].toString());
				}
				if(obj[9]!=null){
					form.setReason(obj[9].toString());	
				}
				
				overTimeFormList.add(form);
			}
		}
		return overTimeFormList;
	}
}
