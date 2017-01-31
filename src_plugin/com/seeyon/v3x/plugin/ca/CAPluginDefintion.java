/**
 * 
 */
package com.seeyon.v3x.plugin.ca;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author Administrator
 *
 */
public final class CAPluginDefintion extends PluginDefintion {

	/**
	 * 
	 */
	public CAPluginDefintion() {
		super();
	}

	public boolean isAllowStartup(ServletContext servletContext) {
		return "true".equals(this.getPluginProperty("ca.enable"));
	}
}
