package com.seeyon.v3x.hr.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStatus;
import net.joinwork.bpm.definition.BPMTransition;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.controller.HrRecordApplicationController;
import com.seeyon.v3x.hr.dao.DynamicFormDao;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;

public class DynamicFormManagerImpl implements DynamicFormManager {
	
	private DynamicFormDao dynamicFormDao;
	
	public DynamicFormDao getDynamicFormDao() {
		return dynamicFormDao;
	}
	public void setDynamicFormDao(DynamicFormDao dynamicFormDao) {
		this.dynamicFormDao = dynamicFormDao;
	}
	public List findFormByTabelName(String tableName)throws Exception{
		return this.dynamicFormDao.getDynamicFormbyName(tableName);
	}
	
	public List findOverTimeByTableName(String tableName)throws Exception{
		return this.dynamicFormDao.getOverTimeFormbyName(tableName);
	}
	
	public void updateLeaveAndEvectionForm(String tableName, List<Long> ids)throws Exception{
		if(ids != null && !ids.isEmpty()){
			for(Long id : ids){
				this.dynamicFormDao.updateLeaveAndEvectionForm(tableName, id);
			}
		}
	}
	
	public void updateOverTimeForm(String tableName, List<Long> ids)throws Exception{
		if(ids != null && !ids.isEmpty()){
			for(Long id : ids){
				this.dynamicFormDao.updateOverTimeForm(tableName, id);
			}
		}
	}
	
	public String getDynamicFormXML(Long formId, String tableName)throws Exception{
		ColSummary summary = dynamicFormDao.getColSummaryByFormId(formId,tableName);
		Long caseId = summary.getCaseId();
		BPMProcess process = ColHelper.getRunningProcessByCaseId(caseId);
		List endList = process.getEnds();
		BPMStatus end = (BPMStatus)endList.get(0);
		List trans = end.getUpTransitions();
		BPMAbstractNode activity = ((BPMTransition)trans.get(0)).getFrom();
		BPMSeeyonPolicy policy = activity.getSeeyonPolicy();

		User user = CurrentUser.get();
		String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), policy.getFormApp(), policy.getForm(), policy.getOperationName(), formId.toString(), summary.getId().toString(), "111", policy.getName(),true);
		
		return formContent;
	}
	
	public List searchLeaveFormByMemberName(String name, String tableName)throws Exception{
		List list = this.dynamicFormDao.getDynamicFormbyName(tableName);
		List<Object> results = new ArrayList<Object>();
		for(Object object : list){
			Object[] obj = (Object[])object;
			String memberName = obj[5].toString();
			if(memberName.contains(name))
				results.add(obj);
		}
		return results;
		
	}
	
	public List searchEvectionFormByMemberName(String name, String tableName)throws Exception{
		List list = this.dynamicFormDao.getDynamicFormbyName(tableName);
		List<Object> results = new ArrayList<Object>();
		for(Object object : list){
			Object[] obj = (Object[])object;
			String memberName = obj[3].toString();
			if(memberName.contains(name))
				results.add(obj);
		}
		return results;
	}
	
	public List searchOverTimeFormByMemberName(String name, String tableName)throws Exception{
		List list = this.dynamicFormDao.getOverTimeFormbyName(tableName);
		List<Object> results = new ArrayList<Object>();
		for(Object object : list){
			Object[] obj = (Object[])object;
			String memberName = obj[3].toString();
			if(memberName.contains(name))
				results.add(obj);
		}
		return results;
	}
	
	public List getLeaveFormByDate(Date fromTime, Date toTime, String tableName)throws Exception{
		return this.dynamicFormDao.findLeaveFormByDate(fromTime, toTime, tableName);
	}
	
	public List getOverTimeFormByDate(Date fromTime, Date toTime, String tableName)throws Exception{
		return this.dynamicFormDao.findOverTimeFormByDate(fromTime, toTime, tableName);
	}
	
	public List getEvectionFormByDate(Date fromTime, Date toTime, String tableName)throws Exception{
		return this.dynamicFormDao.findEvectionFormByDate(fromTime, toTime, tableName);
	}
	
}
