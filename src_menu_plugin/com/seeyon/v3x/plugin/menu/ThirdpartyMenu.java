package com.seeyon.v3x.plugin.menu;

import java.util.List;

/**
 * 第三方自定义菜单项。
 * 
 * @author wangwy
 * 
 */
public interface ThirdpartyMenu {
	/**
	 * 返回显示菜单的模块列表。
	 * 
	 * @return 模块名称列表。对于加载项菜单，模块名称为Controller的ModelAndView的viewName。
	 */
	List<String> getModule();

	/**
	 * 菜单显示的文本。
	 * 
	 * @return 菜单文本。
	 */
	String getLabel();

	/**
	 * 菜单链接。
	 * 
	 * @return 点击菜单转向的链接或执行的javascript。
	 */
	String getUrl();

	/**
	 * 菜单图标。
	 * 
	 * @return 菜单图标文件地址。
	 */
	String getIcon();

	/**
	 * 菜单执行需要导入的javascript文件地址。
	 * 
	 * @return
	 */
	String getScript();
}
