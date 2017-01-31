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

import com.seeyon.v3x.portal.SectionPortletFunction;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.portal.util.PortalConstants.EditModel;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.SpaceFix;

/**
 * @author dongyj
 *
 */
public class PortalSectionSupport extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6849073526336396215L;

	protected Fragment fragment;
	
	/**
	 * 整个页面分成10份。
	 * 2的为窄栏目
	 */
	protected int width;
	
	protected int x;
	
	protected int y;
	/**
	 * 是否支持一个fragment支持多个栏目
	 */
	protected Boolean multiple = false;
	
	public PortalSectionSupport(){
		init();
	}
	private void init(){
		fragment = null;
		width = 0;
		multiple = false;
	}
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	
	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		JspWriter out = pageContext.getOut();
		String pagePath =  PortalConstants.getString(request,PortalConstants.PAGEPATH);
		try {
			EditModel model = PortalConstants.getEditModel(request);
			Long spaceId = null;
			SpaceFix fix = (SpaceFix)request.getAttribute(PortalConstants.SPACE_FIX);
			if(fix != null){
				spaceId = fix.getId();
			}
			String showState = (String) request.getAttribute("showState");
			switch(model){
			case view:
				if(fragment != null&&SectionPortletFunction.isAllowedUserUsed(fragment)){
					out.println("<div class=\"portal-layout-cell\">");
					out.println(PortalConstants.getString("portal.section.title", String.valueOf(fragment.getId()).replace('-', '_'), y+"", x+""));
					out.println("<input type='hidden' id='X_"+String.valueOf(fragment.getId())+"' value='"+x+"' />");
					out.println("<input type='hidden' id='Y_"+String.valueOf(fragment.getId())+"' value='"+y+"' />");
					out.println("<input type='hidden' id='S_"+String.valueOf(fragment.getId())+"' value='"+String.valueOf(fragment.getId())+"' />");
					out.println("<input type='hidden' id='PanelId_"+String.valueOf(fragment.getId())+"' value='' />");
					out.println(SectionPortletFunction.parseFragment(fragment, pagePath, width, spaceId, showState));
					out.println("</div>");
				}
				break;
			case show:
				//TODO 展示修改页面
			case edit:
				/**
				 * 这里要给出的信息:
				 *   y x startY
				 *   
				 */
				//out.println("<div class='fragment' x='"+x+"' y='"+y+"' id=\"fragment_"+y+"_"+x+"\" sWidth="+width+"></div>");
				//TODO 修改页面
				break;
			}
		} catch (IOException e) {
			throw new JspTagException(e.toString(), e);
		}
		return super.doEndTag();
	}
	public Boolean getMultiple() {
		return multiple;
	}
	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}
	
}
