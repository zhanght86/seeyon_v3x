/**
 * 
 */
package com.seeyon.v3x.plugin.edoc;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author Administrator
 *
 */
public final class EdocPluginDefintion extends PluginDefintion {

	/**
	 * 
	 */
	public EdocPluginDefintion() {
		super();
	}

	public String getId() {
		return ProductInfo.PluginNoMapper.edoc.name();
	}
	
}
