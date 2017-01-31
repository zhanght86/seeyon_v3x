package com.seeyon.v3x.plugin.dee;

import javax.servlet.ServletContext;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.plugin.PluginMainMenu;

public final class DEEPluginDefintion extends PluginDefintion {
	public DEEPluginDefintion() {
		super();
	}

	public boolean isAllowStartup(ServletContext servletContext) {
		if( "true".equals(this.getPluginProperty("dee.enable"))){
			init();
			return true;
		}
		return false;
	}
	
	public void setAdminMenus(PluginMainMenu[] menus)
	{
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());
		if(isGroupVer)
		{
			setGroupMenus(menus);
		}
		else
		{
			setAccountMenus(menus);
		}
	}
	
	private void init() {
		String[] arr = { "dee.meta.datasource.driver",
				"dee.meta.datasource.url", "dee.meta.datasource.userName",
				"dee.meta.datasource.password" };

		// 设置元数据数据库连接信息
		// 以A8配置的为准，如果配置工具独立启动，以系统环境变量为准
		for (String name : arr) {
			String value = this.getPluginProperty(name);
			if(value==null) continue;
			//System.setProperty(name, value);
		}
		// 跨上下文传递配置，设置环境变量
		// 设置DEE_HOME为base/dee
		//System.setProperty("DEE_HOME", SystemEnvironment.getA8BaseFolder()+ File.pathSeparator + "dee" +File.pathSeparator);
	}
}
