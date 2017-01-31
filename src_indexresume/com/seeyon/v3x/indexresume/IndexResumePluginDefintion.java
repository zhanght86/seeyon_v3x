package com.seeyon.v3x.indexresume;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

public class IndexResumePluginDefintion extends PluginDefintion {
	public String getId() {
		return "indexResume";
	}

	@Override
	public boolean isAllowStartup(ServletContext servletContext) {
		return ProductInfo.hasPlugin(ProductInfo.PluginNoMapper.luceneIndex.name());
	}
	
}
