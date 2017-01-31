package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_authorities database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquiryAuthority extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String authDesc;
	private Long authId;
	private InquirySurveytype inquirySurveytype;

    public InquiryAuthority() {
    }


	/**
	 * @return the authDesc
	 */
	public String getAuthDesc() {
		return authDesc;
	}


	/**
	 * @param authDesc the authDesc to set
	 */
	public void setAuthDesc(String authDesc) {
		this.authDesc = authDesc;
	}


	public Long getAuthId() {
		return this.authId;
	}
	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	//bi-directional many-to-one association to InquirySurveytype
	public InquirySurveytype getInquirySurveytype() {
		return this.inquirySurveytype;
	}
	public void setInquirySurveytype(InquirySurveytype inquirySurveytype) {
		this.inquirySurveytype = inquirySurveytype;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}