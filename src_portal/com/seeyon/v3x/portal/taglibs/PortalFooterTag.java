/**
 * 
 */
package com.seeyon.v3x.portal.taglibs;

import com.seeyon.v3x.portal.taglibs.support.PortalFooterSuppert;

/**
 * @author dongyj
 *
 */
public class PortalFooterTag extends PortalFooterSuppert {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3479438020027566908L;

	public PortalFooterTag(){
		super();
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	
	public boolean isNeedPageJs() {
		return needPageJs;
	}
	public void setNeedPageJs(boolean needPageJs) {
		this.needPageJs = needPageJs;
	}
}
