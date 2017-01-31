/**
 * 
 */
package com.seeyon.v3x.common.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通用Servlet的接口，详细文档参阅{@link GenericServlet}
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-9-28
 */
public interface GenericServletInterface {
	
	public void doGet(ServletConfig servletConfig, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	public void doPost(ServletConfig servletConfig, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
