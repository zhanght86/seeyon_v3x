/** Copyright (c) 2000-2007, 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 * AjaxJsonView.java created by paul at 2007-7-29 下午06:05:00
 *
 */
package com.seeyon.v3x.hr.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.view.AbstractView;

/**
 *
 * @author paul
 */
public class AjaxJsonView extends AbstractView {
	private transient static final Log LOG = LogFactory
	.getLog(AjaxJsonView.class);
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void renderMergedOutputModel(Map map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		boolean isArray = false;
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Starting JSON rendering of " + this.getBeanName());
		}
//		 get the model from the map passed created by the controller
		
		Object model = null;
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Received Object");
		}  
		
		model = map.get(Constants.AJAX_JSON);
		
//		 if the model is null, we have an exception
		JSONObject jsonObject = null;
		JSONArray  jsonArray =  null;
		
		if (model != null) {
			if (model instanceof JSONArray) {
				isArray = true;
				jsonArray = (JSONArray)model;   
			}
			else if (model instanceof JSONObject) {
				jsonObject = (JSONObject)model ;   
			}
		}

		// -------------------------------- write the bytes to the response
//		response.getOutputStream().write(xml.getBytes());
		// -------------------------------- set header info default to text/xml
		request.setCharacterEncoding("UTF-8");
		if (StringUtils.isEmpty(getContentType())) {
			setContentType("text/html; charset=UTF-8");
		}
		response.setContentType(getContentType());
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control",
				"no-store, max-age=0, no-cache, must-revalidate");

		// Set IE extended HTTP/1.1 no-cache headers.
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");

		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		
		if (isArray) {
			response.getWriter().write(jsonArray.toString());
			if (LOG.isDebugEnabled()) {
				LOG.debug("JSON ARRAY !!!!!!");
				LOG.debug("json=" + jsonArray.toString());
				LOG.debug("content type : " + response.getContentType());
			}
		}
		else {
			response.getWriter().write("[" + jsonObject.toString() + "]");
			if (LOG.isDebugEnabled()) {
				LOG.debug("JSON OBJECT !!!!!!");
				LOG.debug("json=" + jsonObject.toString());
				LOG.debug("content type : " + response.getContentType());
			}
		}
	}

}
