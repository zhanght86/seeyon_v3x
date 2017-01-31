package com.seeyon.v3x.common.taglibs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import DBstep.iMsgServer2000;

import com.seeyon.v3x.common.web.login.CurrentUser;

public class HtmlSignOcxTag extends BodyTagSupport {

	private static final long serialVersionUID = -5559101459684750113L;

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
		
		JspWriter out = pageContext.getOut();

		String basePath = request.getScheme() + "://" + request.getServerName() + ":" +request.getServerPort() + request.getContextPath();
		
		String userName=CurrentUser.get().getName();
		try{
			StringBuffer szTemp=new StringBuffer();
			szTemp.append("<script>");			
			szTemp.append("var webRoot=\"").append(basePath).append("\";").append("\r\n");			
			szTemp.append("var htmOcxUserName=\"").append(userName).append("\";").append("\r\n");
			szTemp.append("var hwVer=\"").append(iMsgServer2000.Version("iWebSignature")).append("\";").append("\r\n");
			szTemp.append("</script>");
			out.println(szTemp.toString());
		}catch(Exception e)
		{
			
		}
		return super.doEndTag();
	}

}