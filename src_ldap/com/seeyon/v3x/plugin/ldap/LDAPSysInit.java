package com.seeyon.v3x.plugin.ldap;

import javax.servlet.ServletContextEvent;

import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;

/**
 * @author YongZhang
 * @version 2009-08-07
 */
public class LDAPSysInit implements SystemInitialitionInterface
{
    public void destroyed(ServletContextEvent arg0)
    {

    }

    public void initialized(ServletContextEvent arg0)
    {
        LDAPConfig.getInstance().init();
    }
}
