package com.seeyon.v3x.news.util;

import java.util.HashSet;
import java.util.Set;

public class Constants {
	public static final String COMMON_RESOURCE_BASENAME = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	public static final String NEWS_RESOURCE_BASENAME = "com.seeyon.v3x.news.resources.i18n.NewsResources";
	
	/**
	 * 新闻板块的类型
	 */
	public static enum NewsTypeSpaceType {
		group, //集团新闻
		corporation, //单位新闻
		department, //部门新闻
		none, //全部新闻(暂时无用)
		custom, //自定义团队新闻
		public_custom, //自定义单位新闻
		public_custom_group //自定义集团新闻
	}
	
	
	public static enum NewsTypeAclType {
		manager, 
		audit,
		writer
	}
	
	/**
	 * 根据key得到枚举类型
	 * 
	 * @param key
	 * @return
	 */
	public static NewsTypeSpaceType valueOfSpaceType(int key) {
		NewsTypeSpaceType[] enums = NewsTypeSpaceType.values();

		if (enums != null) {
			for (NewsTypeSpaceType enum1 : enums) {
				if (enum1.ordinal() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
	
	/**
	 * 尚未提交，也就是暂存
	 */
	public static final int DATA_STATE_NO_SUBMIT=0;
	/**
	 * 已经提交，但是没有审核
	 */
	public static final int DATA_STATE_ALREADY_CREATE=10;
	/**
	 * 已经审核，但是没有发布
	 */
	public static final int DATA_STATE_ALREADY_AUDIT=20;
	/**
	 * 已经发布，但是没有归档
	 */
	public static final int DATA_STATE_ALREADY_PUBLISH=30;
	/**
	 * 审核没通过
	 */
	public static final int DATA_STATE_NOPASS_AUDIT=40;
	/**
	 * 已经归档
	 */
	public static final int DATA_STATE_ALREADY_PIGEONHOLE=100;
	
	/**
	 * 得到所有没有发布的列表
	 */
	public static Set<Integer> getDataStatesNoPublish(){
		Set<Integer> states = new HashSet<Integer>();
		
		states.add(Constants.DATA_STATE_ALREADY_AUDIT);
		states.add(Constants.DATA_STATE_ALREADY_CREATE);
		states.add(Constants.DATA_STATE_NO_SUBMIT);
		states.add(Constants.DATA_STATE_NOPASS_AUDIT);
		states.add(Constants.DATA_STATE_ALREADY_PUBLISH);
		
		return states;
	}
	
	/**
	 * 得到所有
	 */
	public static Set<Integer> getDataStatesCanManage(){
		Set<Integer> states = new HashSet<Integer>();

		states.add(Constants.DATA_STATE_ALREADY_PUBLISH);
//		states.add(Constants.DATA_STATE_NOPASS_AUDIT);
//		states.add(Constants.DATA_STATE_ALREADY_PIGEONHOLE);
		
		return states;
	}
	public static String getDataStatesCanManageString(){
		Set<Integer> states = Constants.getDataStatesCanManage();
		if(states == null || states.size() == 0)
			return "";
		String ret = "";
		for(int state : states){
			ret += ",?";
		}
		return ret.substring(1, ret.length());
	}
	
	/**
	 * 日期格式 2008-01-01
	 */
	public static final String FORMAT_DATE="yyyy-MM-dd";
	/**
	 * 时间格式 14:12:03
	 */
	public static final String FORMAT_TIME="HH:mm:ss";
	/**
	 * 完整的日期时间格式 2008-01-01 14:12:03
	 */
	public static final String FORMAT_DATETIME="yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 新闻发起员标志，在新闻类型-新闻管理员、新闻发起员关联表中使用，保存在ext1字段中
	 */
	public static final String WRITE_FALG="write";
	/**
	 * 新闻管理员标志，在新闻类型-新闻管理员、新闻发起员关联表中使用，保存在ext1字段中
	 */
	public static final String MANAGER_FALG="manager";
	
	/**
	 * 操作成功标志
	 */
	public static final String RESULT_SUCCESS="success";
	/**
	 * 操作失败标志
	 */
	public static final String RESULT_FAILURE="failure";
	
	/**
	 * 是否处于开发测试阶段
	 */
	public static final Boolean IS_TEST=true;
	
	/**
	 * 新闻的CATEGORY = 7
	 */
	public static final int BULLETIN_CATEGORY=7;
	/**
	 * 新闻的CATEGORY = 8
	 */
	public static final int NEWS_CATEGORY=8;
	
	public static final int NEWS_HOMEPAGE_TABLE_COLUMNS = 6;
	
	/**
	 * 记录一条新闻最终发布前的审核状态，分为3种情况，保存在ext3扩展字段中：
	 * 1.无审核员，新闻发送后即"直接发布"   对应值  0                 
	 * 2.有审核员，其审核操作为"审核通过"   对应值  1  
	 * 3.有审核员，其审核操作为"直接发布"   对应值  2 
	 * 新闻管理员进入管理界面所查看到的全部已发布新闻，由ext3扩展字段来决定"审核意见"处显示内容
	 */
	public static final int AUDIT_RECORD_NO = 0;
	public static final int AUDIT_RECORD_PASS = 1;
	public static final int AUDIT_RECORD_PUBLISH = 2;
	/**
	 * 图片新闻
	 */
	public static final int ImageNews = 0;
	/**
	 * 焦点新闻
	 */
	public static final int FocusNews = 1;
}
