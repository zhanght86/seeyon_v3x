/**
 * 
 */
package com.seeyon.v3x.plugin.luceneIndex;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author Administrator
 *
 */
public final class LuceneIndexPluginDefintion extends PluginDefintion {

	/**
	 * 
	 */
	public LuceneIndexPluginDefintion() {
		super();
	}

	public String getId() {
		return ProductInfo.PluginNoMapper.luceneIndex.name();
	}
}
