package www.seeyon.com.v3x.form.controller;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import www.seeyon.com.v3x.form.base.hibernate.SeeyonFormPojo;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
public class Constantform {
	
	//表单输入扩展控件名称 
	public final static String EXTEND_SELECT_USER_LABEL = "选择人员";
	public final static String EXTEND_SELECT_DEPARTMENT_LABEL = "选择部门";
	public final static String EXTEND_SELECT_POST_LABEL = "选择岗位";
	public final static String EXTEND_SELECT_LEVEL_LABEL = "选择职务级别";
	public final static String EXTEND_SELECT_ACCOUNT_LABEL = "选择单位";
	public final static String EXTEND_SELECT_DATE_LABEL = "日期选取器";
	public final static String EXTEND_SELECT_DATETIME_LABEL = "日期时间选取器";
	public final static String EXTEND_SELECT_FILE_LABEL = "插入附件";
	public final static String EXTEND_SELECT_IMAGE_LABEL = "插入图片";
	public final static String EXTEND_SELECT_RELATED_FORM_LABEL = "选择关联表单...";
	public final static String EXTEND_SELECT_DEE_TASK_LABEL = "选择数据交换任务";
	public final static String EXTEND_SEARCH_DEE_TASK_LABEL = "查询控件交换引擎任务";
	public final static String EXTEND_SELECT_HR_STAFFINFO_LABEL = "form.input.relation.hr.staffinfo.name.label";
	
	//表单系统定义变量名称
	public final static String SYSTEM_VALUE_SYSTEMDATE_LABEL = "系统日期";
	public final static String SYSTEM_VALUE_SYSTEMDATETIME_LABEL = "系统日期时间";
	public final static String SYSTEM_VALUE_SYSTEMTIME_LABEL = "系统时间";
	public final static String SYSTEM_VALUE_MONTHFIRSTDAY_LABEL = "本月初日期";
	public final static String SYSTEM_VALUE_MONTHLASTDAY_LABEL = "本月末日期";
	public final static String SYSTEM_VALUE_USERNAME_LABEL = "登录人员姓名";
	public final static String SYSTEM_VALUE_LOGINNAME_LABEL = "登录人员登录名";
	public final static String SYSTEM_VALUE_USERSTATE_LABEL = "登录用户的人员状态";
	public final static String SYSTEM_VALUE_USERTYPE_LABEL = "登录用户的人员类别";
	public final static String SYSTEM_VALUE_USERDEPTNAME_LABEL = "登录人员所在部门";
	public final static String SYSTEM_VALUE_USERPOSTNAME_LABEL = "登录人员岗位";
	public final static String SYSTEM_VALUE_USERLEVELNAME_LABEL = "登录人员职务级别";
	public final static String SYSTEM_VALUE_USERACCOUNTNAME_LABEL = "登录人员所在单位";
	public final static String SYSTEM_VALUE_LOGINID_LABEL = "登录人员ID";
	public final static String SYSTEM_VALUE_USERDEPTID_LABEL = "登录人员所在部门ID";
	public final static String SYSTEM_VALUE_USERPOSTID_LABEL = "登录人员岗位ID";
	public final static String SYSTEM_VALUE_USERLEVELID_LABEL = "登录人员职务级别ID";
	public final static String SYSTEM_VALUE_USERACCOUNTID_LABEL = "登录人员所在单位ID";
	//表单格式化名称
	public final static String DATAFORMAT_THOUSANDS_LABEL = "千分位";
	
	//关联录入表单流程名称定义
	public final static String RELATED_FORM_FLOW_NAME_LABEL = "_formFlowNameLabel";
	
	//表字段列
	public final static String TABLE_FORM_STATUS_LABEL = SeeyonFormPojo.C_sFieldName_State;
//	public final static String TABLE_FORM_START_MEMBER_LABEL = SeeyonFormPojo.C_sFieldName_Start_member_id;
	public final static String TABLE_FORM_CREATOR_LABEL = SeeyonFormPojo.C_sFieldName_Start_member_id;
//	public final static String TABLE_FORM_START_TIME_LABEL = SeeyonFormPojo.C_sFieldName_Start_date;
	public final static String TABLE_FORM_CREATE_TIME_LABEL = SeeyonFormPojo.C_sFieldName_Start_date;
	public final static String TABLE_FORM_APPROVE_MEMBER_LABEL = SeeyonFormPojo.C_sFieldName_Approve_member_id;
	public final static String TABLE_FORM_APPROVE_TIME_LABEL = SeeyonFormPojo.C_sFieldName_Approve_date;
	public final static String TABLE_FORM_FLOW_FINISHED_LABEL = SeeyonFormPojo.C_sFieldName_finishedflag;
	public final static String TABLE_FORM_RATIFY_FLAG_LABEL = SeeyonFormPojo.C_sFieldName_ratifyflag;
	
	private final static Map<String,String> resourcesMap = new HashMap<String, String>(); 
	static{
		resourcesMap.put(EXTEND_SELECT_USER_LABEL, "form.input.extend.selectuser.label");
		resourcesMap.put(EXTEND_SELECT_DEPARTMENT_LABEL, "form.input.extend.selectdepartment.label");
		resourcesMap.put(EXTEND_SELECT_POST_LABEL, "form.input.extend.selectpost.label");
		resourcesMap.put(EXTEND_SELECT_LEVEL_LABEL, "form.input.extend.selectlevel.label"+Functions.suffix());
		resourcesMap.put(EXTEND_SELECT_ACCOUNT_LABEL, "form.input.extend.selectaccount.label");
		resourcesMap.put(EXTEND_SELECT_DATE_LABEL, "form.input.extend.selectdate.label");
		resourcesMap.put(EXTEND_SELECT_DATETIME_LABEL, "form.input.extend.selectdatetime.label");
		resourcesMap.put(EXTEND_SELECT_FILE_LABEL, "form.input.extend.selectfile.label");
		resourcesMap.put(EXTEND_SELECT_IMAGE_LABEL, "form.input.extend.selectimage.label");
		resourcesMap.put(EXTEND_SELECT_RELATED_FORM_LABEL, "form.input.extend.selectrelatedform.label");
		resourcesMap.put(EXTEND_SEARCH_DEE_TASK_LABEL, "form.input.extend.searchdeetask.label");
		resourcesMap.put(EXTEND_SELECT_DEE_TASK_LABEL, "form.input.extend.selectdeetask.label");
		resourcesMap.put(RELATED_FORM_FLOW_NAME_LABEL, "form.input.relation.form.flow.name.label");
		
		resourcesMap.put(TABLE_FORM_STATUS_LABEL, "form.query.sheetstatus.label");
//		resourcesMap.put(TABLE_FORM_START_MEMBER_LABEL, "form.system.start.member.field.label");
		resourcesMap.put(TABLE_FORM_CREATOR_LABEL, "form.system.creator.field.label");
//		resourcesMap.put(TABLE_FORM_START_TIME_LABEL, "form.system.start.time.field.label");
		resourcesMap.put(TABLE_FORM_CREATE_TIME_LABEL, "form.system.createdate.field.label");
		resourcesMap.put(TABLE_FORM_APPROVE_MEMBER_LABEL, "form.system.approve.member.field.label");
		resourcesMap.put(TABLE_FORM_APPROVE_TIME_LABEL, "form.system.approve.time.field.label");
		resourcesMap.put(TABLE_FORM_FLOW_FINISHED_LABEL, "formquery_sheetfinished.label");
		resourcesMap.put(TABLE_FORM_RATIFY_FLAG_LABEL, "form.query.sheetstatus.label");
	}
	public static enum SendType {
		normal, resend, forward
	}

	public static enum ConfigCategory {
		action_to_col_definition, col_flow_perm_policy
	}

	private static final String resource_baseName = "www.seeyon.com.v3x.form.resources.i18n.FormResources";

	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(resource_baseName);

	public static String getString(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resourceBundle, key, parameters);
	}

	public static String getString4CurrentUser(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, key, parameters);
	}
	
	public static String getString4OtherKey(String key, Object... parameters) {
		if(resourcesMap.get(key) != null){
			return ResourceBundleUtil.getString(resource_baseName, resourcesMap.get(key), parameters);			
		}
		return ResourceBundleUtil.getString(resource_baseName, key, parameters);
	}

	public static String getString(String key, Locale locale,
			Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, locale, key,
				parameters);
	}
	
	public static Map<String,String> getResourcesMap(){
		return new HashMap<String, String>(resourcesMap); 
	}
}
