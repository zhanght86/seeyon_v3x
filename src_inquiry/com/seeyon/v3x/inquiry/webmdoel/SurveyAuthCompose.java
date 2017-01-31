package com.seeyon.v3x.inquiry.webmdoel;

import java.util.List;


/**
 * @author lin tian
 * 2007-3-8 
 */
public class SurveyAuthCompose {
    private List<AuthUserCompose> auths;
    
    private String authlist ;

	/**
	 * @return the authlist
	 */
	public String getAuthlist() {
		return authlist;
	}

	/**
	 * @param authlist the authlist to set
	 */
	public void setAuthlist(String authlist) {
		this.authlist = authlist;
	}

	/**
	 * @return the auths
	 */
	public List<AuthUserCompose> getAuths() {
		return auths;
	}

	/**
	 * @param auths the auths to set
	 */
	public void setAuths(List<AuthUserCompose> auths) {
		this.auths = auths;
	}
    



   
}
