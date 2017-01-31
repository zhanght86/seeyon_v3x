package com.seeyon.v3x.news.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.news.manager.NewsReadManager;
import com.seeyon.v3x.common.web.BaseController;

public class NewsReadController extends BaseController {
	@SuppressWarnings("unused")
	private NewsReadManager newsReadManager;

	public void setNewsReadManager(NewsReadManager newsReadManager) {
		this.newsReadManager = newsReadManager;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}


}
