package com.seeyon.v3x.hr.util;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

public class Constants {
	public final static String AJAX_JSON = "json";
	
	// 用于日志的subObjectId
	public final static Long MODULE_STAFF = 1702L; // 个人信息日志

	public final static Long MODULE_TRANSFER = 1704L;// 调配日志

	public final static String RESOURCE_HR = "com.seeyon.v3x.hr.resource.i18n.HRResources";
	public final static String RESOURCE_ORGANIZATION = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

	public final static String FORM_TEMPLETE_CONFIG_FILE = "templete_config.xml"; // 模板配置文件
	
	public final static String FORM_TEMPLETE_CONFIG_FILE_PATH = "/WEB-INF/jsp/hr/conf/"; // 模板配置文件相对路径

	// 表单类型
	public static enum FORM_TYPE {
		none, 
		leave, 
		evection, 
		overtime, 
		transfer
	}

	// 操作类型：配合集群缓存同步之用 
	public static enum ActionType {
		create, 
		read, 
		update, 
		delete
	}

	public static final String CARD_ENABLED = "enable";
	public static final long SYSTEM_VIRTUAL_ACCOUNT_ID = 1l;

	public static final int HR_NOCARD_STATE = 3;

	//导入工资
	public static final int IMPORT_REPORT_ERROR_1 = 1;
	public static final int IMPORT_REPORT_ERROR_2 = 2;
	public static final int IMPORT_REPORT_ERROR_3 = 3;

	public static String getI18N(String key, Object... parameters) {
		return ResourceBundleUtil.getString(RESOURCE_HR, key, parameters);
	}
	
}