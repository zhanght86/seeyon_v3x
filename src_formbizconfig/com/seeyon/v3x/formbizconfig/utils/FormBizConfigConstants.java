package com.seeyon.v3x.formbizconfig.utils;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * 表单业务配置常量设定
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public abstract class FormBizConfigConstants {
	/**
	 * 表单业务配置挂接方式：0 - 栏目挂接（新建时默认选中）
	 */
	public static final int CONFIG_TYPE_COLUMN = 0;
	/**
	 * 表单业务配置挂接方式：1 - 菜单挂接
	 */
	public static final int CONFIG_TYPE_MENU = 1;
	/**
	 * 表单业务配置挂接方式：2 - 栏目挂接、菜单挂接
	 */
	public static final int CONFIG_TYPE_COLUMN_MENU = 2;
	/**
	 * 表单业务配置挂接方式：3 - 未挂接
	 */
	public static final int CONFIG_TYPE_NO = 3;
	
	/**
	 * 栏目挂接项各项所属分类：表单流程
	 */
	public static final int COLUMN_FORM_FLOW = 1;  		 			
	/**
	 * 栏目挂接项各项所属分类：表单流程 - 待办事项
	 */
	public static final int COLUMN_FORM_FLOW_WAIT = 11;  		
	/**
	 * 栏目挂接项各项所属分类：表单流程 - 跟踪事项
	 */
	public static final int COLUMN_FORM_FLOW_TRACK = 12; 		
	/**
	 * 栏目挂接项各项所属分类：表单流程 - 督办事项
	 */
	public static final int COLUMN_FORM_FLOW_SUPERWISE = 13; 
	/**
	 * 栏目挂接项各项所属分类：表单上报
	 */
	public static final int COLUMN_FORM_REPORT = 2;
	/**
	 * 栏目挂接项各项所属分类：表单查询
	 */
	public static final int COLUMN_FORM_QUERY = 3;
	/**
	 * 栏目挂接项各项所属分类：表单统计
	 */
	public static final int COLUMN_FORM_STATISTIC = 4;
	/**
	 * 栏目挂接项各项所属分类：表单查询或统计下的子节点（表单查询或统计模板），其自身不像表单流程下的子节点有特定分类，保存时设定为默认值0
	 */
	public static final int QUERY_OR_STATISTIC_CHILD = 0; 
	/**
	 * 栏目挂接项各项所属分类：一级节点的父节点（也即根节点，其分类设定为0）
	 */
	public static final int PARENT_IS_ROOT = 0;	
	
	/**
	 * 菜单挂接项各项所属分类：新建事项
	 */
	public static final int MENU_NEW_AFFAIRS = 7;
	/**
	 * 菜单挂接项各项所属分类：待发事项
	 */
	public static final int MENU_TO_SEND_AFFAIRS = 8; 
	/**
	 * 菜单挂接项各项所属分类：已发事项
	 */
	public static final int MENU_SENT_AFFAIRS = 9; 
	/**
	 * 菜单挂接项各项所属分类：待办事项
	 */
	public static final int MENU_TO_DEAL_AFFAIRS = 10;
	/**
	 * 菜单挂接项各项所属分类：已办事项
	 */
	public static final int MENU_DEALT_AFFAIRS = 20;
	/**
	 * 菜单挂接项各项所属分类：督办事项
	 */
	public static final int MENU_SUPERWISE_AFFAIRS = 30;
	/**
	 * 菜单挂接项各项所属分类：表单查询
	 */
	public static final int MENU_FORM_QUERY = 40;
	/**
	 * 菜单挂接项各项所属分类：表单统计
	 */
	public static final int MENU_FORM_STATISTIC = 50;
	/**
	 * 菜单挂接项各项所属分类：信息中心
	 */
	public static final int MENU_INFO_CENTER = 60;	
	/**
	 * 菜单挂接项各项所属分类：解析结果不属以上任何一种时，返回默认值
	 */
	public static final int MENU_INVALID = 0;
	
	/**
	 * 表单业务配置对应国际化资源路径
	 */
	public static final String FORM_BIZ_CONFIG_RESOURCE = "com.seeyon.v3x.formbizconfig.resources.i18n.FormBizConfigResources";
	/**
	 * 公用国际化资源路径
	 */
	public static final String COMMON_RESOURCE = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	
	/**
	 * 标识：操作地点来自信息中心页面、来自用户、来自表单管理员
	 */
	public static final String FLAG_FROM_INFOCENTER = "InfoCenter";
	/**
	 * 标识：在选择显示表单模板时，用户选中的是"我的模板"，此时显示其有权使用的全部表单模板
	 */
	public static final String FLAG_FROM_USER = "User";
	/**
	 * 标识：在选择显示表单模板时，用户以表单管理员身份选中"所有模板"，此时显示本单位下的全部表单模板
	 */
	public static final String FLAG_FROM_ADMIN = "Admin";
	/**
	 * 分隔符：用于拼接多个名称，比如显示表单业务配置所选表单模板名称中间的分隔符，中文环境下为顿号"、"，英文环境下为","
	 */
	public static final String SEPARATOR = ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE , "common.separator.label");
	
	/**
	 * 当通过选中的表单模板解析不出可用的表单查询或表单统计模板时，返回此字符标识，以便将此前已有的查询或统计模板节点清空
	 */
	public static final String FLAG_REMOVE_ORGINAL = "NULL";
	
	/**
	 * 菜单挂接项链接地址：新建事项（某一表单模板对应二级菜单），使用时加上业务配置相关参数及对应表单模板id
	 */
	public static final String URL_NEW_AFFAIRS = "/collaboration.do?method=newColl";
	/**
	 * 菜单挂接项链接地址：待发事项。使用时再加上业务配置相关参数
	 */
	public static final String URL_TO_SEND_AFFAIRS = "/collaboration.do?method=collaborationFrame&from=WaitSend";
	/**
	 * 菜单挂接项链接地址：已发事项。使用时再加上业务配置相关参数
	 */
	public static final String URL_SENT_AFFAIRS = "/collaboration.do?method=collaborationFrame&from=Sent";
	/**
	 * 菜单挂接项链接地址：待办事项。使用时再加上业务配置相关参数
	 */
	public static final String URL_TO_DEAL_AFFAIRS = "/collaboration.do?method=collaborationFrame&from=Pending";
	/**
	 * 菜单挂接项链接地址：已办事项。使用时再加上业务配置相关参数
	 */
	public static final String URL_DEALT_AFFAIRS = "/collaboration.do?method=collaborationFrame&from=Done";
	/**
	 * 菜单挂接项链接地址：督办事项。使用时再加上业务配置相关参数
	 */
	public static final String URL_SUPERWISE_AFFAIRS = "/colSupervise.do?method=mainEntry&status=0";
	/**
	 * 菜单挂接项链接地址：表单查询。使用时再加上业务配置相关参数
	 */
	public static final String URL_FORM_QUERY = "/formquery.do?method=formQueryShow";
	/**
	 * 菜单挂接项链接地址：表单统计。使用时再加上业务配置相关参数
	 */
	public static final String URL_FORM_STATISTIC = "/formreport.do?method=formReportShow";
	/**
	 * 菜单挂接项链接地址：信息中心。使用时再加上业务配置相关参数
	 */
	public static final String URL_INFO_CENTER = "/formBizConfig.do?method=enterManagerCenter";
	
	
	/**
	 * 业务配置管理来源类型
	 */
	public static final int SOURCE_TYPE_FLOWTEMPLATE = 1;//流程模板
	public static final int SOURCE_TYPE_INFOMANAGE = 2;//信息管理应用绑定  
	public static final int SOURCE_TYPE_BASEDATA = 3;//基础数据应用绑定  
	public static final int SOURCE_TYPE_QUERY = 4;//查询  
	public static final int SOURCE_TYPE_REPORT = 5;//统计  
	public static final int SOURCE_TYPE_DOCUMENT = 6;//文档  
	
	public static final int SOURCE_TYPE_ADMIN_BUL = 7;//单位公告
	public static final int SOURCE_TYPE_ADMIN_NEWS = 8;//单位新闻
	public static final int SOURCE_TYPE_ADMIN_BBS = 9;//单位讨论  
	public static final int SOURCE_TYPE_ADMIN_INQUIRY = 10;//单位调查
	public static final int SOURCE_TYPE_GROUP_BUL = 11;//集团公告
	public static final int SOURCE_TYPE_GROUP_NEWS = 12;//集团新闻
	public static final int SOURCE_TYPE_GROUP_BBS = 13;//集团讨论
	public static final int SOURCE_TYPE_GROUP_INQUIRY = 14;//集团调查	
}