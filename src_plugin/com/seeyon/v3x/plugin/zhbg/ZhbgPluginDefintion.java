/**
 * 
 */
package com.seeyon.v3x.plugin.zhbg;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-7
 */
public final class ZhbgPluginDefintion extends PluginDefintion {

	public String getId() {
		return ProductInfo.PluginNoMapper.zhbg.name();
	}
}
