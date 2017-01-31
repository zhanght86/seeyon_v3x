package com.seeyon.v3x.plugin.menu.permission;
/**
 * 菜单权限检查。
 * @author wangwy
 *
 */
public interface MenuItemAccessCheck {
	/**
	 * 判断当前登录人是否有菜单的权限。
	 * @param memberId 人员Id
	 * @param params 第一个参数为HttpServletRequest
	 * @return 有权限返回true，否则返回false
	 */
	boolean check(long memberId, Object... params);
}
