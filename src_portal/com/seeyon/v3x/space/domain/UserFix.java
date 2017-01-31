/**
 * 
 */
package com.seeyon.v3x.space.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author dongyj
 *
 */
public class UserFix extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	/**
	 * 人员id
	 */
	private Long memberId;
	/**
	 * 键
	 */
	private String proKey;
	private String value;
	
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getProKey() {
		return proKey;
	}
	public void setProKey(String proKey) {
		this.proKey = proKey;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
