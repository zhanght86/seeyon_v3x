package com.seeyon.v3x.plugin.https;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-3-6
 */
public final class HttpsPluginDefintion extends PluginDefintion {
	
	public String getId() {
		return ProductInfo.PluginNoMapper.https.name();
	}
	
}
