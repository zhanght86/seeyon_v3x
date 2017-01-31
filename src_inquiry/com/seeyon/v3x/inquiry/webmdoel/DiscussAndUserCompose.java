/**
 * 
 */
package com.seeyon.v3x.inquiry.webmdoel;

import com.seeyon.v3x.inquiry.domain.InquirySurveydiscuss;
import com.seeyon.v3x.organization.domain.V3xOrgMember;


/**
 * @author lin tian
 * 2007-2-17 
 */
public class DiscussAndUserCompose {
	private InquirySurveydiscuss dcs;
	
	private V3xOrgMember user; // 发布人

	/**
	 * @return the dcs
	 */
	public InquirySurveydiscuss getDcs() {
		return dcs;
	}

	/**
	 * @param dcs the dcs to set
	 */
	public void setDcs(InquirySurveydiscuss dcs) {
		this.dcs = dcs;
	}

	/**
	 * @return the user
	 */
	public V3xOrgMember getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(V3xOrgMember user) {
		this.user = user;
	}
	
	
	
	
	

}
