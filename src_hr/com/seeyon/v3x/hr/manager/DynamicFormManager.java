package com.seeyon.v3x.hr.manager;

import java.util.Date;
import java.util.List;

public interface DynamicFormManager {
	public List findFormByTabelName(String tableName)throws Exception;
	
	public List findOverTimeByTableName(String tableName)throws Exception;
	
	public void updateLeaveAndEvectionForm(String tableName, List<Long> ids)throws Exception;
	
	public void updateOverTimeForm(String tableName, List<Long> ids)throws Exception;
	
	public String getDynamicFormXML(Long formId, String tableName)throws Exception;
	
	public List searchLeaveFormByMemberName(String name, String tableName)throws Exception;
	
	public List searchEvectionFormByMemberName(String name, String tableName)throws Exception;
	
	public List searchOverTimeFormByMemberName(String name, String tableName)throws Exception;
	
	public List getLeaveFormByDate(Date fromTime, Date toTime, String tableName)throws Exception;
	
	public List getOverTimeFormByDate(Date fromTime, Date toTime, String tableName)throws Exception;
	
	public List getEvectionFormByDate(Date fromTime, Date toTime, String tableName)throws Exception;
}
