/**
 * 
 */
package com.seeyon.v3x.plugin.formBiz;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-5-9
 */
public final class FormBizPluginDefintion extends PluginDefintion {
	public FormBizPluginDefintion() {
		super();
	}

	public String getId() {
		return ProductInfo.PluginNoMapper.formBiz.name();
	}
}
