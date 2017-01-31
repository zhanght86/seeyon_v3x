package com.seeyon.v3x.plugin.ldap;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.product.ProductInfo;

/**
 * @author Yong Zhang
 * @version 2009-08-08
 *
 */
public class LDAPPluginDefintion extends PluginDefintion
{
	public LDAPPluginDefintion()
	{
		super();
	}

	public String getId()
	{
		return ProductInfo.PluginNoMapper.LDAP_AD.name();
	}

//	public boolean isAllowStartup(ServletContext servletContext)
//	{
////		return "1".equals(this.getPluginProperty("ldap.enabled"));
//	}
}
