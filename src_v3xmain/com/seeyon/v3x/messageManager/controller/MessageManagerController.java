package com.seeyon.v3x.messageManager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.messageManager.manager.MessageDelsetManager;

@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class MessageManagerController extends BaseController {
	
	private UserMessageManager userMessageManager;
	private MessageDelsetManager messageDelsetManager;

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView initHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView modelAndView = new ModelAndView("v3xmain/messageManager/manager");
		//查询系统消息
//		Map countMap = userMessageManager.countMessage();
//		modelAndView.addObject("countMap", countMap);
		//查询系统自动清除设置
		modelAndView.addObject("messageDelset", messageDelsetManager.getMessageDelset());
		
		return modelAndView;
	}
	
	public ModelAndView handRemove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		String startTime = RequestUtils.getStringParameter(request,"startTime");
		String endTime = RequestUtils.getStringParameter(request,"endTime");
		
		userMessageManager.removeMessage(startTime, endTime);

		return super.redirectModelAndView("/messageManager.do?method=initHome");
	}
	
	public ModelAndView autoRemove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int count = RequestUtils.getIntParameter(request, "count");
		int day = RequestUtils.getIntParameter(request, "day");
	
		messageDelsetManager.updateMessageDelset(count, day);
		
		return super.redirectModelAndView("/messageManager.do?method=initHome");		
	}  

	public void setMessageDelsetManager(MessageDelsetManager messageDelsetManager) {
		this.messageDelsetManager = messageDelsetManager;
	}


	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
}
