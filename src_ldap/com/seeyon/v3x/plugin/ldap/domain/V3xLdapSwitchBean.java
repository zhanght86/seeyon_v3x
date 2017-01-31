package com.seeyon.v3x.plugin.ldap.domain;

import java.io.Serializable;
import com.seeyon.v3x.plugin.ldap.manager.LdapServerMap;
/**
 * LDAP/AD目录服务属性类
 * @author <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2009-1-21
 */
public class V3xLdapSwitchBean implements Serializable
{
    private static final long serialVersionUID = -2890402241309937638L;

    private String ldapUrl="128.2.3.123";

    private String ldapPort="389";//最好保持不变

    private String ldapBasedn="dc=seeyon,dc=com";

    private String ldapAdmin="cn=Manager";

    private String ldapPassword="";

    private String ldapEnabled="0";//o不开启1开启

    private String ldapAdEnabled="ldap";
    
    private String ldapSSLEnabled = "0";//0不支持1支持

    private String hostName;
    
    private String principal;
    
    private String domainName;
    public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getLdapSSLEnabled() {
		return ldapSSLEnabled;
	}

	public void setLdapSSLEnabled(String ldapSSLEnabled) {
		this.ldapSSLEnabled = ldapSSLEnabled;
	}

	//    private String ldapLocalAuth="system,group-admin";
    private String ldapServerType=LdapServerMap.getSUN();
    public String getLdapServerType() {
		return ldapServerType;
	}

	public void setLdapServerType(String ldapServerType) {
		this.ldapServerType = ldapServerType;
	}

	public String getLdapAdEnabled()
    {
        return ldapAdEnabled;
    }

    public void setLdapAdEnabled(String ldapAdEnabled)
    {
        this.ldapAdEnabled = ldapAdEnabled;
    }

    public String getLdapAdmin()
    {
        return ldapAdmin;
    }

    public void setLdapAdmin(String ldapAdmin)
    {
        this.ldapAdmin = ldapAdmin;
    }

    public String getLdapBasedn()
    {
        return ldapBasedn;
    }

    public void setLdapBasedn(String ldapBasedn)
    {
        this.ldapBasedn = ldapBasedn;
    }

    public String getLdapEnabled()
    {
        return ldapEnabled;
    }

    public void setLdapEnabled(String ldapEnabled)
    {
        this.ldapEnabled = ldapEnabled;
    }

//    public String getLdapLocalAuth()
//    {
//        return ldapLocalAuth;
//    }
//
//    public void setLdapLocalAuth(String ldapLocalAuth)
//    {
//        this.ldapLocalAuth = ldapLocalAuth;
//    }

    public String getLdapPassword()
    {
        return ldapPassword;
    }

    public void setLdapPassword(String ldapPassword)
    {
        this.ldapPassword = ldapPassword;
    }

    public String getLdapPort()
    {
        return ldapPort;
    }

    public void setLdapPort(String ldapPort)
    {
        this.ldapPort = ldapPort;
    }

    public String getLdapUrl()
    {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl)
    {
        this.ldapUrl = ldapUrl;
    }

}
