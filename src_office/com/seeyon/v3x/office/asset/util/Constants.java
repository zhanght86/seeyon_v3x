package com.seeyon.v3x.office.asset.util;

public class Constants
{

    /**
     * 办公设备使用状态
     */
    public static final int Asset_Status_Allow = 0;// 可申请（默认）

    public static final int Asset_Status_NotAllow = 1;// 不可申请

    /**
     * 删除标记
     */
    public static final int Del_Flag_Normal = 0;// 正常（默认）

    public static final int Del_Flag_Delete = 1;// 删除

    public static final int Del_TYPE_APPLY = 2; // 申请者删除标记

    public static final String Total_Count_Field = "myTotalCount";

    /**
     * 查询条件
     */
    public static final int Search_Condition_AssetName = 1; // 按设备名称查询

    public static final int Search_Condition_AssetType = 2; // 按设备类型查询

    public static final int Search_Condition_AssetStat = 3; // 按设备状态查询

    public static final int Search_Condition_AssetModel = 4; // 按设备规格查询

    public static final int Search_Condition_Department = 5; // 按部门查询

    public static final int Search_Condition_Member = 6; // 按人员查询

    public static final int Search_Condition_ApplyStat = 7; // 按审批状态查询

    public static final int Search_Condition_SorageStat = 8; // 按出库入库状态查询

    public static final int Search_Condition_AssetCode = 9; // 按设备编号查询

    public static final String ASSET_RESOURCE_NAME = "com.seeyon.v3x.office.asset.resources.i18n.AssetResources";
}
