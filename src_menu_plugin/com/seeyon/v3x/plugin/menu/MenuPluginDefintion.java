package com.seeyon.v3x.plugin.menu;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.plugin.PluginDefintion;

public class MenuPluginDefintion extends PluginDefintion {
	private static Log log = LogFactory.getLog(MenuPluginDefintion.class);

	public MenuPluginDefintion() {
	}

	public String getId() {
		return "menu";
	}

	public boolean isAllowStartup(ServletContext servletContext) {

		return true;// "1".equals(this.getPluginProperty("a8.plugin.nc.enabled"));
	}

}
