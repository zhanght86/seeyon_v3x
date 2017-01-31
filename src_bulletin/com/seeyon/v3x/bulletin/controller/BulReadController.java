package com.seeyon.v3x.bulletin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bulletin.manager.BulReadManager;
import com.seeyon.v3x.common.web.BaseController;

public class BulReadController extends BaseController {
	@SuppressWarnings("unused")
	private BulReadManager bulReadManager;

	public void setBulReadManager(BulReadManager bulReadManager) {
		this.bulReadManager = bulReadManager;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}


}
