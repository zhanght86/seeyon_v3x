package com.seeyon.v3x.common.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.SettableHttpServletRequest;
import com.seeyon.v3x.product.util.QSEncoder;
import com.seeyon.v3x.util.Strings;

/**
 * Servlet Filter implementation class GenericFilter
 */
public class GenericFilter implements Filter {
	private static final Log logger = LogFactory.getLog(GenericFilter.class);
	
	private GenericFilterBean bean;
	
    public GenericFilter() {
    }
    
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if(bean == null){
			bean = (GenericFilterBean)ApplicationContextHolder.getBean("GenericFilterBean");
		}
		
		String uid = request.getParameter(QSEncoder.UID_KEY);
    	if(Strings.isNotBlank(uid)){
    		SettableHttpServletRequest req = null;
    		if(request instanceof SettableHttpServletRequest){
    			req = (SettableHttpServletRequest)(request);
    		}
    		else{
    			req = new com.seeyon.v3x.common.web.util.SettableHttpServletRequest((HttpServletRequest)request);
    		}
    		
    		request = req;
    	}
		
		boolean r = true;
		try {
			r = bean.doFilter((HttpServletRequest)request, (HttpServletResponse)response);
		}
		catch (Exception e) {
			logger.error("", e);
		}
		
		if(r){
			chain.doFilter(request, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
