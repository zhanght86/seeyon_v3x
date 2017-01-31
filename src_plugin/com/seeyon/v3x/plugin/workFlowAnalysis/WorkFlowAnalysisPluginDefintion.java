package com.seeyon.v3x.plugin.workFlowAnalysis;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;

public class WorkFlowAnalysisPluginDefintion extends PluginDefintion{
	public WorkFlowAnalysisPluginDefintion() {
		super();
	}
	
	public boolean isAllowStartup(ServletContext servletContext) {
		return super.isAllowStartup(servletContext);
	}
}
