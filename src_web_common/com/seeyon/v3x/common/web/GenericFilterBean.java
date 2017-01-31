/**
 * 
 */
package com.seeyon.v3x.common.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seeyon.v3x.common.SystemEnvironment;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-4-7
 */
public final class GenericFilterBean {
	
	private List<GenericFilterProxy> genericFilterProxies;
	
	public void setGenericFilterProxies(List<GenericFilterProxy> genericFilterProxies) {
		this.genericFilterProxies = genericFilterProxies;
	}
	
	public boolean doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(genericFilterProxies == null || genericFilterProxies.isEmpty()){
			return true;
		}
		
		for (int i = 0; i < genericFilterProxies.size(); i++) {
			GenericFilterProxy proxy = genericFilterProxies.get(i);
			String[] urlPattern = proxy.getUrlPattern();
			
			if(this.isMap(request, urlPattern)){
				if(!proxy.doFilter(request, response)){
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean isMap(HttpServletRequest request, String[] urlPatterns){
		String uri = request.getRequestURI();
		if(uri.startsWith(SystemEnvironment.getA8ContextPath())){
			uri = uri.substring(SystemEnvironment.getA8ContextPath().length());
		}
		
		for (String urlPattern : urlPatterns) {
			if("/*".equals(urlPattern)){
				return true;
			}
			
			if(urlPattern.equals(uri)){
				return true;
			}
			
			if(urlPattern.endsWith("*")){
				urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
				if(uri.startsWith(urlPattern)){
					return true;
				}
			}
			if(urlPattern.startsWith("*")){
				urlPattern = urlPattern.substring(1);
				if(uri.endsWith(urlPattern)){
					return true;
				}
			}
		}
		
		return false;
	}

}
