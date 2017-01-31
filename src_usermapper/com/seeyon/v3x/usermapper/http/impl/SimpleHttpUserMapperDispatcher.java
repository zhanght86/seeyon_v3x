package com.seeyon.v3x.usermapper.http.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.usermapper.NoMethodException;
import com.seeyon.v3x.usermapper.common.action.UserMapperAction;
import com.seeyon.v3x.usermapper.http.HttpUserMapperDispatcher;

public class SimpleHttpUserMapperDispatcher implements HttpUserMapperDispatcher {
	
	UserMapperAction action=null;

	public ModelAndView doUserMapperList(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public ModelAndView doUserMapperSingleLoginListExlogin(
			HttpServletRequest request, HttpServletResponse response)
			throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public ModelAndView fileUserMapperList(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public ModelAndView fileUserMapperMapLoginExlogin(
			HttpServletRequest request, HttpServletResponse response)
			throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public UserMapperAction getAction() {
		// TODO Auto-generated method stub
		return this.action;
	}

	public ModelAndView saveImportReport(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

	public void setAction(UserMapperAction val) {
		// TODO Auto-generated method stub
		this.action=val;
	}

	public ModelAndView singleUserMapper(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		throw new NoMethodException();
	}

}//end class
