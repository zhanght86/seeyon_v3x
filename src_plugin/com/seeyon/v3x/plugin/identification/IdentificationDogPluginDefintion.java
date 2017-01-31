/**
 * 
 */
package com.seeyon.v3x.plugin.identification;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-3-5
 */
public final class IdentificationDogPluginDefintion extends PluginDefintion {
	
	public String getId() {
		return ProductInfo.PluginNoMapper.identificationDog.name();
	}
	
}
