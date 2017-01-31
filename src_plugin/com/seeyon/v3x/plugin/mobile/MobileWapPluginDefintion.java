package com.seeyon.v3x.plugin.mobile;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

public class MobileWapPluginDefintion extends PluginDefintion
{
    public String getId() {
        return ProductInfo.PluginNoMapper.mobileWap.name();
    }
}
