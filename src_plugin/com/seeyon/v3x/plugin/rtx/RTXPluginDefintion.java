package com.seeyon.v3x.plugin.rtx;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;
import com.seeyon.v3x.util.Strings;

public class RTXPluginDefintion extends PluginDefintion {

	public RTXPluginDefintion() {
	}

	public String getId() {
		return ProductInfo.PluginNoMapper.rtx.name();
	}

	/*
	 * 若没有配置RTX服务器的地址则不启动RTX插件
	 * 
	 * @see com.seeyon.v3x.plugin.PluginDefintion#isAllowStartup(javax.servlet.ServletContext)
	 */
	public boolean isAllowStartup(ServletContext servletContext) {
		return Strings.isNotBlank(this.getPluginProperty("rtx.remoteAddr"));
	}
	
}
