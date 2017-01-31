package com.seeyon.v3x.link.util;

public class Constants {
	public static final byte IS_SYSTEM=1;		//是否是系统默认
	public static final byte LINK_STATUS=1;		//状态
	//表示对公司所有人员进行授权
	public static final byte ALL_USER_TYPE=5;	
	public static final long ALL_USER_ID=5;
	
	public static final long LINK_COMMON=1;		//常用链接
	public static final long LINK_IN=2;			//内部系统
	public static final long LINK_OUT=3;		//外部系统
	
	public static final byte PARAM_TRUE=1;		//参数是默认的
	public static final byte PARAM_FALSE=0;		//非默认的
	
	
	public static final byte LINK_USER=1;		//人员
	public static final byte LINK_DEPT=2;		//部门
	public static final byte LINK_POST=3;		//岗位
	public static final byte LINK_TEAM=4;		//组
	
	public static final String LINK_CATEGORY_COMMON_KEY = "link.category.common";
	public static final String LINK_CATEGORY_IN_KEY = "link.category.in";
	public static final String LINK_CATEGORY_OUT_KEY = "link.category.out";
	
	public static final long LINK_CATEGORY_COMMON_ID = 1L;
	public static final long LINK_CATEGORY_IN_ID = 2L;
	public static final long LINK_CATEGORY_OUT_ID = 3L;
	
	public static final String LINK_RESOURCE_BASENAME = "com.seeyon.v3x.link.i18n.LinkResource";
	
	
//	public static String getKeyByCategory(long category){
//		if(category == Constants.LINK_COMMON){
//			return "link.category.common";
//		}else if(category == Constants.LINK_IN){
//			return "link.category.in";
//		}else{
//			return "link.category.out";
//		}
//	}
}
