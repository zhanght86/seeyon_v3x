package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_surveydiscuss database table.
 * 调查评论
 * @author BEA Workshop Studio
 */
public class InquirySurveydiscuss extends com.seeyon.v3x.common.domain.BaseModel implements Serializable, Comparable<InquirySurveydiscuss> {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String discussContent; //评论内容
	private java.sql.Timestamp discussDate; //评论时间
	private long subsurveyId; //调查问题项ID
	private Long userId; //人员ID
	private InquirySurveybasic inquirySurveybasic;

    public InquirySurveydiscuss() {
    }

	public String getDiscussContent() {
		return this.discussContent;
	}
	public void setDiscussContent(String discussContent) {
		this.discussContent = discussContent;
	}

	public java.sql.Timestamp getDiscussDate() {
		return this.discussDate;
	}
	public void setDiscussDate(java.sql.Timestamp discussDate) {
		this.discussDate = discussDate;
	}

	public long getSubsurveyId() {
		return this.subsurveyId;
	}
	public void setSubsurveyId(long subsurveyId) {
		this.subsurveyId = subsurveyId;
	}

	public Long getUserId() {
		return this.userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
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

	public int compareTo(InquirySurveydiscuss o) {
		return -this.getDiscussDate().compareTo(o.getDiscussDate());
	}
}