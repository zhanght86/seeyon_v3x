package com.seeyon.v3x.plugin.ldap.manager;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.ldap.config.LDAPProperties;
import com.seeyon.v3x.common.ldap.dao.AbstractLdapDao;
import com.seeyon.v3x.common.ldap.dao.AdDaoImp;
import com.seeyon.v3x.plugin.ldap.domain.V3xLdapSwitchBean;
import com.seeyon.v3x.util.TextEncoder;

public class VerifyConnection extends AdDaoImp {
	 
	/**
	 * 验证目录服务器设置
	 * 
	 * @return
	 */
	public boolean verify(V3xLdapSwitchBean ldapSwitchBean) throws Exception{
		DirContext ctx = null;
		boolean verificationResults = true;
		try{
			Hashtable<Object, Object> env = new Hashtable<Object, Object>();
			env.put(Context.SECURITY_AUTHENTICATION, LDAPProperties.LDAP_SIMPLE);
			env.put(Context.PROVIDER_URL, LDAPConfig.getInstance().createUrlString(ldapSwitchBean.getLdapUrl(), Integer.parseInt(ldapSwitchBean.getLdapPort())));
			env.put(Context.SECURITY_PRINCIPAL, ldapSwitchBean.getLdapAdmin());
			env.put(Context.SECURITY_CREDENTIALS, ldapSwitchBean.getLdapPassword());
			env.put(Context.INITIAL_CONTEXT_FACTORY, SUN_JNDI_PROVIDER);
			if("1".equals(ldapSwitchBean.getLdapSSLEnabled())){//SSl连接验证
				System.setProperty("javax.net.ssl.trustStore", KEYSTORE);
		        env.put(Context.SECURITY_PROTOCOL, "ssl");
			}
			ctx = new InitialDirContext(env);
		}catch(Throwable e){
			getLog().error("目录服务器配置错误", e);
			verificationResults =  false;
		}finally{
			closeCtx(ctx);
		}
		return verificationResults;
	}
}
