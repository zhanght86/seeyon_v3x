/**
 * 
 */
package com.seeyon.v3x.common.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.web.BaseController;

/**
 * 通用的Controller，以实现将JSP快速实现MVC
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-16
 */
public class GenericController extends BaseController {
	protected static final Log LOG = LogFactory.getLog(GenericController.class);

	public static final String Parameter_Name_ViewPage = "ViewPage";

	/**
	 * 将ViewPage通过参数传入，构造ModelAndView，本方法不负责任何业务逻辑
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ViewPage = request.getParameter(Parameter_Name_ViewPage);
		String from = request.getParameter("from");
		LOG.debug("ViewPage : " + ViewPage);

		if (ViewPage == null || "".equals(ViewPage)) {
			throw new java.lang.IllegalArgumentException(
					"Parameter 'ViewPage' is not available.");
		}
		
		File file = new File(SystemEnvironment.getA8ApplicationFolder() + "/WEB-INF/jsp/" + ViewPage + ".jsp");
		if(!file.exists()){
			response.sendError(404);
			return null;
		}

		ModelAndView modelAndView = new ModelAndView(ViewPage);
		modelAndView.addObject("from", from);
		return modelAndView;
	}

}
