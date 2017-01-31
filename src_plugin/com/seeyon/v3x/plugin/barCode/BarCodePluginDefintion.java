package com.seeyon.v3x.plugin.barCode;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;

public class BarCodePluginDefintion extends PluginDefintion {
	public BarCodePluginDefintion() {
		super();
	}
	
	public boolean isAllowStartup(ServletContext context) {
		return super.isAllowStartup(context);
	}
}
