package com.seeyon.v3x.common.servicemanager;


/*
 * 本类提供两种方式的获得系统服务的方法：
 * 1、在运行环境下获得系统提供的服务。这个时候JetSpeed管理系统的服务，通过传入当前的request对象和服务的字符串名称就可以获得服务对象。返回的值需要访问者自己做cast。
 * 2、在调试环境中获得服务。这个时候应用没有运行在JetSpeed环境中。它获得服务的方式是通过Spring访问实体Bean。
 * 为了保持兼容，这个类只提供一个方法。在这个方法中，如果不能从JetSpeed中获得服务，则自动去寻找本地Classpath中定义为spring-service.xml的定义文件获得服务。
 * 注意：
 * JetSpeed中对服务的名称定义为：
 * 		"cps:SeeyonOrganization"，即在前面添加了"cps:"
 * 在调试环境中，可以不加"cps:"，但是Spring配置文件中的Bean的名字需要与“cps:”后面的名字相同。
 * JetSpeed管理的所有服务的名字可以从包jetspeed-api.jar中的文件CommonPortletServices.class中找到（以常量的方式定义）。
 */

public class ServiceManager {

	/**
	 * 获得系统提供的服务，如果没有在JetSpeed的运行环境下,则自动从spring-service.xml文件中加载
	 * 
	 * @param request:
	 * @param serviceName
	 * @return
	 */
	public static Object getSystemService(String serviceName) {
//		HttpServletRequest request = WebUtil.getRequest();
//		if (request == null) {
//			return null;
//		}
//
//		PortletConfig portletConfig = (PortletConfig) request.getAttribute(org.apache.pluto.Constants.PORTLET_CONFIG);
//
//		if (portletConfig == null) {
//			return null;
//		}
//
//		return portletConfig.getPortletContext().getAttribute(serviceName);
		return null;
	}

}
