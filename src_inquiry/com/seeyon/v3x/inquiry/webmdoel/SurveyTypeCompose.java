/**
 * 
 */
package com.seeyon.v3x.inquiry.webmdoel;

import java.util.List;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.organization.domain.V3xOrgMember;


/**
 * @author lin tian
 * 
 * 2007-3-1
 */
public class SurveyTypeCompose {
	private InquirySurveytype inquirySurveytype;

	private List<V3xOrgMember> managers;

	private V3xOrgMember checker;
	
	private Integer count;//调查数
	
	private boolean hasPublicAuth = false;    //当前登录者是否有发布权限
	
	private boolean hasManageAuth = false;    //当前登录者是否有管理权限

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return Returns the inquirySurveytype.
	 */
	public InquirySurveytype getInquirySurveytype() {
		return inquirySurveytype;
	}

	/**
	 * @param inquirySurveytype
	 *            The inquirySurveytype to set.
	 */
	public void setInquirySurveytype(InquirySurveytype inquirySurveytype) {
		this.inquirySurveytype = inquirySurveytype;
	}

	/**
	 * @return Returns the managers.
	 */
	public List<V3xOrgMember> getManagers() {
		return managers;
	}

	/**
	 * @param managers
	 *            The managers to set.
	 */
	public void setManagers(List<V3xOrgMember> managers) {
		this.managers = managers;
	}

	/**
	 * @return Returns the checker.
	 */
	public V3xOrgMember getChecker() {
		return checker;
	}

	/**
	 * @param checker
	 *            The checker to set.
	 */
	public void setChecker(V3xOrgMember checker) {
		this.checker = checker;
	}

	public boolean isHasManageAuth() {
		return hasManageAuth;
	}

	public void setHasManageAuth(boolean hasManageAuth) {
		this.hasManageAuth = hasManageAuth;
	}

	public boolean isHasPublicAuth() {
		return hasPublicAuth;
	}

	public void setHasPublicAuth(boolean hasPublicAuth) {
		this.hasPublicAuth = hasPublicAuth;
	}

}
