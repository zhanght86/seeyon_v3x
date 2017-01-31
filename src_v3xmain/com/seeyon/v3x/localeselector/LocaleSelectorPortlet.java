/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seeyon.v3x.localeselector;

//import static org.apache.jetspeed.PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE;
//import static org.apache.jetspeed.PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE;
//
//import java.io.IOException;
//import java.util.Locale;
//
//import javax.portlet.ActionRequest;
//import javax.portlet.ActionResponse;
//import javax.portlet.GenericPortlet;
//import javax.portlet.PortletConfig;
//import javax.portlet.PortletException;
//import javax.portlet.PortletRequest;
//import javax.portlet.PortletSession;
//import javax.portlet.RenderRequest;
//import javax.portlet.RenderResponse;
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.jetspeed.login.LoginConstants;
//import org.apache.jetspeed.portlet.ServletContextProviderImpl;
//import org.apache.jetspeed.request.RequestContext;
//import org.apache.portals.bridges.common.GenericServletPortlet;
//import org.apache.portals.bridges.common.ServletContextProvider;
//
//import com.seeyon.v3x.common.ServerState;
//import com.seeyon.v3x.common.i18n.LocaleContext;
//import com.seeyon.v3x.common.util.Cookies;
//import com.seeyon.v3x.util.Strings;

/**
 * This is the portlet to select user's preferred locale.
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id: LocaleSelectorPortlet.java,v 1.14 2010/12/02 05:08:37 tanmf Exp $
 */
/**
 * Seeyon注：本文件是从JetSpeed中的原型更改过来的，因此，没有完全按照3X的MVC结构实现。
 * 
 * @author maokai
 */
public class LocaleSelectorPortlet{ // extends GenericServletPortlet 
//	public static final String PREFERED_LOCALE_SESSION_KEY = "org.apache.jetspeed.prefered.locale";
//	
//	private static final String Session_key_noReadCookieOfLocale = "noReadCookieOfLocale";
//
//	private ServletContextProvider servletContextProvider;
//	
//	// private UserManager userManager;
//	
//	protected HttpServletRequest getHttpServletRequest(GenericPortlet portlet,
//			PortletRequest request) {
//		return servletContextProvider.getHttpServletRequest(portlet, request);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
//	 */
//	public void init(PortletConfig config) throws PortletException {
//		servletContextProvider = new ServletContextProviderImpl();
//		super.init(config);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
//	 *      javax.portlet.RenderResponse)
//	 */
//	public void doView(RenderRequest request, RenderResponse response)
//			throws PortletException, IOException {
//		PortletSession session = request.getPortletSession();
//		
//		Locale locale = request.getLocale();
//		
//		Boolean noReadCookieOfLocale = (Boolean)session.getAttribute(Session_key_noReadCookieOfLocale);
//		if(locale == null || !Boolean.TRUE.equals(noReadCookieOfLocale)){
//			HttpServletRequest req = getHttpServletRequest(this, request);
//			String orginalLanguage = Cookies.get(req, LoginConstants.LOCALE);
//			
//			if(Strings.isNotBlank(orginalLanguage)){
//				locale = LocaleContext.parseLocale(orginalLanguage);
//			}
//			
//			session.setAttribute(Session_key_noReadCookieOfLocale, true);
//		}
//		
//		locale = LocaleContext.merge(locale);
//		
//		setLocale(locale, session, request);
//
//		request.setAttribute("ServerState", ServerState.getInstance().isShutdown());
//		request.setAttribute("currentLocale", locale.toString());
////		session.removeAttribute(Session_key_noReadCookieOfLocale);
//		
//		super.doView(request, response);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
//	 *      javax.portlet.ActionResponse)
//	 */
//	public void processAction(ActionRequest request, ActionResponse response)
//			throws PortletException, IOException {
//
//		PortletSession session = request.getPortletSession();
//		String language = request.getParameter(PREFERED_LOCALE_SESSION_KEY);
//
//		if (language != null) {
//			Locale locale = LocaleContext.parseLocale(language);
//			if (locale == null) {
//				locale = Locale.getDefault();
//			}
//
//			setLocale(locale, session, request);
//		}
//		
//		session.setAttribute(Session_key_noReadCookieOfLocale, true);
//		
//		return;
//	}
//	
//	private void setLocale(Locale locale, PortletSession session, PortletRequest request){
//		session.setAttribute(PREFERED_LOCALE_ATTRIBUTE, locale, PortletSession.APPLICATION_SCOPE);
//		RequestContext requestContext = (RequestContext) request.getAttribute(REQUEST_CONTEXT_ATTRIBUTE);
//		requestContext.setLocale(locale);
//		requestContext.setSessionAttribute(PREFERED_LOCALE_ATTRIBUTE, locale);
//		
//		HttpServletRequest req = getHttpServletRequest(this, request);
//		
//		LocaleContext.setLocale(req, locale);
//	}

}
