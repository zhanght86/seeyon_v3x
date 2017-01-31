package com.seeyon.v3x.usermapper.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.usermapper.NoMethodException;
import com.seeyon.v3x.usermapper.common.action.UserMapperAction;

public interface HttpUserMapperDispatcher {
	static public String HTTP_PARA_TYPE="_umap_type";
	static public String HTTP_PARA_LOGIN="_umap_login";
	static public String HTTP_PARA_EXLOGIN="_umap_exlogin";
	static public String HTTP_PARA_EXUSERID="_umap_exuserid";
	
	static public String HTTP_PARA_EXECEL="_um_execel";
	static public String HTTP_PARA_POLICE="_umap_police";
	
	static public String HTTP_PARA_ACTION_REPORT="_umap_action_report";
	
	public UserMapperAction  getAction();
	public void setAction(UserMapperAction val);
	
	public ModelAndView singleUserMapper(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception ;
	
	public ModelAndView doUserMapperList(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception ;
	public ModelAndView doUserMapperSingleLoginListExlogin(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception ;
	
	public ModelAndView fileUserMapperList(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception ;
	/*
	public ModelAndView fileUserMapperMapLoginExlogin(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception ;
	*/
	public ModelAndView saveImportReport(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException,Exception;
}//end class
