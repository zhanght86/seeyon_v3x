package com.seeyon.v3x.plan;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;



public class Constant {

	/**
	 * 要求提醒
	 */
	public final static String REMIND_TRUE = "-1";

	/**
	 * 下拉框“无“标识
	 */
	public final static String SELECT_NONE_FLAG="-1";
	
	
	/**
	 * 下拉框“无“名称
	 */
	public final static String SELECT_NONE_NAME="无";
	
	/**
	 * 计划回复，第一级
	 */
	public final static String FIRST_LEVEL_PLANREPLY = "firstReply";
	
	/**
	 * 获取计划中的国际化值
	 */
	public static String getPlanI18NValue(String key, Object... values) {
		return ResourceBundleUtil.getString("com.seeyon.v3x.plan.resource.i18n.PlanResources", key, values);
	}
	
	public static void main(String[] args) {
		System.out.println(getPlanI18NValue("plan.canceled.label"));
	}
	
	public static enum PlanOperAction {
		/**
		 * 删除操作
		 */
		Delete,
		/**
		 * 更新操作
		 */
		Update,
		/**
		 * 保存操作
		 */
		Save
	}
}
