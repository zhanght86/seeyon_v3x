package com.seeyon.v3x.meetingroom.util;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */
public class MavUtil{
	
	/**
	 * 封装公用的创建ModelAndView对象方法，把全局常量放到ModelAndView中
	 * @return ModelAndView对象
	 */
	public static ModelAndView getModelAndViewInstance(){
		ModelAndView mav = new ModelAndView();
		mav.addObject("MRConstants", com.seeyon.v3x.meetingroom.util.Constants.getMeetingRoomConstantsInstance());
		return mav;
	}
	
	
	/**
	 * 封装公用的创建ModelAndView对象方法，把全局常量放到ModelAndView中
	 * @param view 转到页面
	 * @return ModelAndView对象
	 */
	public static ModelAndView getModelAndViewInstance(String view){
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("MRConstants", com.seeyon.v3x.meetingroom.util.Constants.getMeetingRoomConstantsInstance());
		return mav;
	}
	
}
