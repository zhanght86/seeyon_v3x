package com.seeyon.v3x.office.myapply.util;

public class Constants {
    
    public  static final String MYAPPLY_RESOURCE_NAME = "com.seeyon.v3x.office.myapply.resources.i18n.MyApplyResources";
	/**
	 * 申请状态
	 */
	public static final int ApplyStatus_Wait = 1;			//等待审核
	public static final int ApplyStatus_Allow = 2;			//通过审核
	public static final int ApplyStatus_NotAllow = 3;		//未通过审核
	public static final int ApplyStatus_Depart = 4;			//已出库
	public static final int ApplyStatus_Back = 5;			//已入库
	public static final int ApplyStatus_Finish = 6;         //完成
	
	/**
	 * 申请类型
	 */
	public static final int ApplyType_Auto = 1;			//车辆
	public static final int ApplyType_Asset = 2;		//设备
	public static final int ApplyType_Stock = 3;		//办公用品
	public static final int ApplyType_Book = 4;			//图书
	
	/**
	 * 查询条件
	 */
	public static final int Search_Condition_apply_type = 1;		//按申请类型称查询
	public static final int Search_Condition_apply_stat = 2;		//按申请状态称查询
	public static final int Search_Condition_apply_date = 3;		//按申请时间查询
	
	/**
	 * 删除标记
	 */
	public static final int Del_Flag_Normal = 0;// 正常（默认）

	public static final int Del_Flag_Delete = 1;// 删除
    
    public static final int Del_TYPE_APP = 2;// 申请者删除
	
	public static final String Total_Count_Field = "myTotalCount";
    
}
