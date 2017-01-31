package com.seeyon.v3x.plugin.ca.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itrus.cert.Names;
import com.itrus.cert.X509Certificate;
import com.itrus.svm.SVM;
import com.itrus.cvm.CVM;

import com.seeyon.v3x.common.constants.LoginResult;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.login.LoginAuthenticationException;
import com.seeyon.v3x.plugin.ca.IcaManager;

public class IcaManager4iTrus implements IcaManager {
	private static final Log log = LogFactory.getLog(IcaManager4iTrus.class);
	private String caBundtype = SystemProperties.getInstance().getProperty("ca.bundtype");
	private String nameinsub = SystemProperties.getInstance().getProperty("ca.nameinsub");
	private String caFactory = SystemProperties.getInstance().getProperty("ca.factory");
	private String CVMConfigFile;
	

	public void init(){
		if(caFactory.equals("iTrus")){
			String classPath = getClass().getResource(
					getClass().getSimpleName() + ".class").getPath();
			String configPath = classPath
					.substring(0, classPath.indexOf("classes")).replaceAll("%20", " ");
			
//			DOMConfigurator.configure(configPath + config.getInitParameter("Log4jConfigFile"));		
			CVM.config(configPath +CVMConfigFile);		
			System.out.println("CVM INITAIL FINISHED!");
		}
	}
	
	@Override	
	public String[] validateCA(HttpServletRequest request) throws LoginAuthenticationException {
		String toSign = request.getParameter("toSign");
		String signedData = request.getParameter("SignedData");
		String oriToSign = (String) request.getSession().getAttribute("ToSign"); //从session中取原始数据

		if (toSign==null || signedData==null || oriToSign==null|| oriToSign.equals("") || signedData.equals("")) {
			return null;
		}
		if (toSign.startsWith("LOGONDATA:")) //PTA2.0版本
			oriToSign = "LOGONDATA:" + oriToSign;

		X509Certificate cert = null;
		String[] commonName = new String[2];
		int ret =0;
		try {
			java.security.cert.X509Certificate x509Cert = SVM.verifySignature(oriToSign, signedData);
			cert = X509Certificate.getInstance(x509Cert);
			ret = CVM.verifyCertificate(cert);						
		} catch (Exception e) {
			log.error("证书验证时发生错误："+e);
			throw new LoginAuthenticationException(LoginResult.ERROR_CA_SERVERERROR);				
		}
		if (ret != CVM.VALID) {
			switch (ret) {
			case CVM.EXPIRED:
				throw new LoginAuthenticationException(LoginResult.ERROR_KEY_OVERDUE);
			case CVM.REVOKED:
				throw new LoginAuthenticationException(LoginResult.ERROR_KEY_FORBIDDEN);
			case CVM.REVOKED_AND_EXPIRED:
				throw new LoginAuthenticationException(LoginResult.ERROR_KEY_FORBIDDENOVERDUE);
			default:
				log.error("证书验证时发生错误，返回值为："+ret);
				throw new LoginAuthenticationException(LoginResult.ERROR_CA_SERVERERROR);
			}			
		}else{//根据证书获取约定标识
			if(caBundtype==null || caBundtype.equals("")){
				log.error("证书验证的证书绑定类型配置错误，当前配置为："+caBundtype);
				throw new LoginAuthenticationException(LoginResult.ERROR_CA_SERVERERROR);
			}
			
			//获取证书信息	
				commonName[0] =cert.getSubjectDNString();
	
			//从主题中获取账号信息
				if(nameinsub==null || nameinsub.equals("")){
					log.error("证书验证的账号所在证书中的域名称配置错误，当前配置为："+nameinsub);
					throw new LoginAuthenticationException(LoginResult.ERROR_CA_SERVERERROR);
				}	
			Names certNames = cert.getSubjectNames();
			commonName[1] = certNames.getItem(nameinsub);

			
		}
		
		return commonName;
	}

	public String getCVMConfigFile() {
		return CVMConfigFile;
	}

	public void setCVMConfigFile(String cVMConfigFile) {
		CVMConfigFile = cVMConfigFile;
	}
	
	
}
