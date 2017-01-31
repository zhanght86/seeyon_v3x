package com.seeyon.v3x.plugin.menu;

import java.util.List;

import com.seeyon.v3x.plugin.menu.permission.MenuItemAccessCheck;

/**
 * 第三方加载项菜单。<br/>
 * 添加到页面工具栏的加载项菜单的子菜单项。
 * 
 * @author wangwy
 * 
 */
public class ThirdpartyAddinMenu implements ThirdpartyMenu {
	private List<String> module;
	private String label;
	private String url;
	private String icon;
	private String script;
	private int index;
	private String i18NResource;
	private List<String> roles;
	private MenuItemAccessCheck accessCheck;
	private String pluginId;

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public List<String> getModule() {
		return module;
	}

	public void setModule(List<String> module) {
		this.module = module;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getI18NResource() {
		return i18NResource;
	}

	public void setI18NResource(String i18nResource) {
		i18NResource = i18nResource;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public MenuItemAccessCheck getAccessCheck() {
		return accessCheck;
	}

	public void setAccessCheck(MenuItemAccessCheck accessCheck) {
		this.accessCheck = accessCheck;
	}

	/**
	 * 初始化
	 */
	public final void init() {

		ThirdpartyMenuManager.getInstance().addMenu(this);
	}
}
