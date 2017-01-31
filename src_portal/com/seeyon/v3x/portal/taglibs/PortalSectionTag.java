/**
 * 
 */
package com.seeyon.v3x.portal.taglibs;

import javax.servlet.jsp.JspException;

import com.seeyon.v3x.portal.taglibs.support.PortalSectionSupport;
import com.seeyon.v3x.space.domain.Fragment;

/**
 * @author dongyj
 *
 */
public class PortalSectionTag extends PortalSectionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5662210633164274277L;

	public PortalSectionTag() {
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
	
	public Fragment getFragment() {
		return fragment;
	}
	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
}
