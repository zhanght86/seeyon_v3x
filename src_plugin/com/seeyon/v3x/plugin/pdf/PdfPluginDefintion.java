/**
 * 
 */
package com.seeyon.v3x.plugin.pdf;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 *
 * 2010-10-13
 */
public class PdfPluginDefintion extends PluginDefintion {
	
	public String getId() {
		return ProductInfo.PluginNoMapper.pdf.name();
	}
}
