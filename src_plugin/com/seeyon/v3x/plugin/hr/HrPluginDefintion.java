/**
 * 
 */
package com.seeyon.v3x.plugin.hr;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author Administrator
 *
 */
public final class HrPluginDefintion extends PluginDefintion {

	/**
	 * 
	 */
	public HrPluginDefintion() {
		super();
	}

	public String getId() {
		return ProductInfo.PluginNoMapper.hr.name();
	}
}
