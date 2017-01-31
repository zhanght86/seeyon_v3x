/**
 * 
 */
package com.seeyon.v3x.inquiry.domain;

import java.io.Serializable;

/**
 * @author lin tian
 * 2007-3-14 
 */
public class InquiryClick  extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private long userId;
	private long surveybasicId;
	/**
	 * @return the surveybasicId
	 */
	public long getSurveybasicId() {
		return surveybasicId;
	}
	/**
	 * @param surveybasicId the surveybasicId to set
	 */
	public void setSurveybasicId(long surveybasicId) {
		this.surveybasicId = surveybasicId;
	}
	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	
}
