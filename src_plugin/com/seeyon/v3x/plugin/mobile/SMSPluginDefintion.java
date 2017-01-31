package com.seeyon.v3x.plugin.mobile;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

public class SMSPluginDefintion extends PluginDefintion {
    public String getId() {
        return ProductInfo.PluginNoMapper.sms.name();
    }
}
