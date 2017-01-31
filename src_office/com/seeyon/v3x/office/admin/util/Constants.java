package com.seeyon.v3x.office.admin.util;

public class Constants {

	/**
	 * 删除标记
	 */
	public static final int Del_Flag_Normal = 0;    // 正常（默认）

	public static final int Del_Flag_Delete = 1;	// 删除
	
	public static final String Total_Count_Field = "myTotalCount";
	
	/**
	 * 查询条件
	 */
	public static final int Search_Condition_Model = 1;		//按管理模块查询
	public static final int Search_Condition_Admin = 2;		//按管理人查询
	public static final int Search_Condition_Depart = 3;	//按管理范围查询
	
    public  static final String ADMIN_RESOURCE_NAME = "com.seeyon.v3x.office.admin.resources.i18n.AdminResources";

}
