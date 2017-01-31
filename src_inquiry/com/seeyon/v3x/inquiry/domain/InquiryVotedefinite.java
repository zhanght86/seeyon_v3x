package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_votedefinite database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquiryVotedefinite extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long surveyitemId;
	private Long userId;
	private java.sql.Timestamp voteDate;
	private InquirySurveybasic inquirySurveybasic;

    public InquiryVotedefinite() {
    }

	public long getSurveyitemId() {
		return this.surveyitemId;
	}
	public void setSurveyitemId(long surveyitemId) {
		this.surveyitemId = surveyitemId;
	}

	public Long getUserId() {
		return this.userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public java.sql.Timestamp getVoteDate() {
		return this.voteDate;
	}
	public void setVoteDate(java.sql.Timestamp voteDate) {
		this.voteDate = voteDate;
	}

	//bi-directional many-to-one association to InquirySurveybasic
	public InquirySurveybasic getInquirySurveybasic() {
		return this.inquirySurveybasic;
	}
	public void setInquirySurveybasic(InquirySurveybasic inquirySurveybasic) {
		this.inquirySurveybasic = inquirySurveybasic;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}