/**
 * 
 */
package com.seeyon.v3x.plugin.officeOcx;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author Administrator
 *
 */
public final class OfficeOcxPluginDefintion extends PluginDefintion {

	/**
	 * 
	 */
	public OfficeOcxPluginDefintion() {
		super();
	}
	
	public String getId() {
		return ProductInfo.PluginNoMapper.officeOcx.name();
	}
}
