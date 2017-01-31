/**
 * 
 */
package com.seeyon.v3x.portal.taglibs.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.seeyon.v3x.portal.util.PortalConstants;

/**
 * @author dongyj
 *
 */
public class PortalFooterSuppert extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4414895872277035279L;

	public PortalFooterSuppert(){
		init();
	}
	private void init(){
		needPageJs = true;
		departmentId = "";
	}
	protected boolean needPageJs = true;
	
	protected String departmentId;
	
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	
	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		boolean isEdit = PortalConstants.isEdit(request);
		
		JspWriter out = pageContext.getOut();
		try {
			if(!isEdit){
				if(needPageJs){
					String isManage = request.getParameter("isManage");
					isManage = isManage == null ? "false" : isManage;
					out.println(PortalConstants.getString("portal.footer.js",departmentId,isManage));
				}
				out.println(PortalConstants.getString("portal.footer"));
			}
		} catch (IOException e) {
			throw new JspTagException(e.toString(), e);
		}
		return super.doEndTag();
	}
}
