package com.seeyon.v3x.plugin.form;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;

public class FormPluginDefintion extends PluginDefintion{
	public FormPluginDefintion() {
		super();
	}
	
	public boolean isAllowStartup(ServletContext servletContext){	
		return super.isAllowStartup(servletContext);
	}
}
