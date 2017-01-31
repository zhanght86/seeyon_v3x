/**
 * $Id: HrPrintController.java,v 1.2 2007/08/15 05:49:47 wangj Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * HrPrintController.java created by paul at 2007-8-15 上午11:53:38
 *
 */
package com.seeyon.v3x.hr.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;

/**
 * <tt>HrPrintController</tt>全面支持打印功能,主要包括预览
 * @author paul
 *
 */
public class HrPrintController extends BaseController {

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ModelAndView preview(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/printPreview");
	}

}
