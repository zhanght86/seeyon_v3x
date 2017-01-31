/**
 * 
 */
package com.seeyon.v3x.plugin.https;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-3-6
 */
public class HttpsController extends BaseController {

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plugin/https/getSessionList");
	}

}
