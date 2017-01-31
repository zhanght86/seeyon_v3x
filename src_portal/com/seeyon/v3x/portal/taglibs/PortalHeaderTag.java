package com.seeyon.v3x.portal.taglibs;

import javax.servlet.jsp.JspException;

import com.seeyon.v3x.portal.decorations.PortalDecoration;
import com.seeyon.v3x.portal.taglibs.support.PortalHeaderSupport;


public class PortalHeaderTag extends PortalHeaderSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7063711779047226775L;

	public PortalDecoration getDecoration() {
		return decoration;
	}

	public void setDecoration(PortalDecoration decoration) {
		this.decoration = decoration;
	}

	public PortalHeaderTag(){
		super();
	}
	
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	
	@Override
	public int doEndTag() throws JspException {
		return super.doEndTag();
	}
}
