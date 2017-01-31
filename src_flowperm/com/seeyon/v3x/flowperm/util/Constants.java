package com.seeyon.v3x.flowperm.util;

/**
 * 节点权限状态定义
 * @author Administrator lindb
 * @version 1.1
 */

public class Constants {
	
	/**
	 * 节点权限停止使用
	 */
	public final static int F_status_disabled = 0;
	
	/**&
	 * 节点权限启用
	 */
	public final static int F_status_enabled = 1;
	
	/**
	 * 节点权限系统预置
	 */
	public final static int F_type_system = 0;
	
	/**
	 * 节点权限自定义 
	 */
	public final static int F_type_custom = 1;
	
	/**
	 * 节点权限：协同
	 */
	public final static int F_TYPE_COLLABORATION = 0;

	/**
	 * 节点权限：公文
	 */
	public final static int F_TYPE_EDOC = 1;
	
	
	public final static int F_ELEMENT_TYPE_READONLY = 1;
	public final static int F_ELEMENT_TYPE_EDIT = 0;
	/**
	 * 节点权限操作集合类型
	 * basic基础操作
	 * advanced高级操作
	 * common基础操作
	 */
	public final static String basic = "basic";
	public final static String advanced = "advanced";
	public final static String common = "common";
}
