package com.seeyon.v3x.inquiry.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**调查基本信息
 * The persistent class for the inquiry_surveybasic database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquirySurveybasic extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	public static final Integer FLAG_NORMAL = 0;// 正常状态

	public static final Integer FLAG_DELETE = 1;// 删除状态

	public static final Integer FLAG_TEM = 2;// 模板状态

	public static final Integer CENSOR_CLOSE = 5;// 终止状态

	public static final Integer CENSOR_PASS = 8;// 发布状态   12-17将发布状态从0改为8   抽取时状态  为0 or 5 时用不上索引
	public static final Integer CENSOR_PASS_NO_BEGIN = -1;// 发布但还未开始   发布时间大于当前时间   此调查还未开始

	public static final Integer CENSOR_NO_PASS = 1;// 审核未通过

	public static final Integer CENSOR_PASS_NO_SEND = 2;// 审核通过但未发布状态

	public static final Integer CENSOR_DRAUGHT = 3;// 保存待发

	public static final Integer CENSOR_FILING_YES = 10; // 归档  12-17将状态由6改为10  查询条件方便

	public static final Integer CENSOR_NO = 4;// 未审核
	
	public static final Integer CENSOR_GROUP_TEM = 7;// 集团模板
	
	public static final Integer CENSOR_ACC_TEM = 6;// 单位模板
	
	public static final Integer CENSOR_CUSTOM_TEM = 11;// 自定义团队空间模板
	
	public static final Integer CENSOR_PUBLIC_CUSTOM_TEM = 12;// 自定义单位模板
	
	public static final Integer CENSOR_PUBLIC_CUSTOM_GROUP_TEM = 13;// 自定义集团模板
	
	private Integer totals=0;
	
	//调查状态
	private Integer censor;

	private Long censorId= Long.valueOf(0);

	private Integer clickCount=0;

	private java.sql.Timestamp closeDate;
	
	//创建者id
	private Long createrId;
	
	//发布者id
	private Long issuerId;

	private Integer cryptonym;

	private Long departmentId;

	private Integer flag;

	private java.sql.Timestamp sendDate;

	private String surveyName;

	private String surveydesc;

	private Integer voteCount =0;
	
	private String checkMind;
	
	private Long surveyTypeId;
	
	private Boolean attachmentsFlag = false;
	
	/**
	 * 是否允许查看调查结果
	 */
	private boolean allowViewResult;
	/**
	 * 是否允许提交前看调查结果
	 */
	private boolean allowViewResultAhead;

	private java.util.Set<InquiryScope> inquiryScopes;

	private java.util.Set<InquirySubsurvey> inquirySubsurveys;

	private java.util.Set<InquirySubsurveyitem> inquirySubsurveyitems;

	private InquirySurveytype inquirySurveytype;

	private java.util.Set<InquirySurveydiscuss> inquirySurveydiscusses;

	private java.util.Set<InquiryVotedefinite> inquiryVotedefinites;

	public InquirySurveybasic() {
	}

	/**
	 * @return the totals
	 */
	public Integer getTotals() {
		return totals;
	}
	/**
	 * @param totals the totals to set
	 */
	public void setTotals(Integer totals) {
		this.totals = totals;
	}
	
	public Integer getCensor() {
		return this.censor;
	}

	public void setCensor(Integer censor) {
		this.censor = censor;
	}

	public Long getCensorId() {
		return this.censorId;
	}

	public void setCensorId(Long censorId) {
		this.censorId = censorId;
	}

	public Integer getClickCount() {
		return this.clickCount;
	}

	public void setClickCount(Integer clickCount) {
		this.clickCount = clickCount;
	}

	public java.sql.Timestamp getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(java.sql.Timestamp closeDate) {
		this.closeDate = closeDate;
	}

	public Long getCreaterId() {
		return this.createrId;
	}

	public void setCreaterId(Long createrId) {
		this.createrId = createrId;
	}

	public Integer getCryptonym() {
		return this.cryptonym;
	}

	public void setCryptonym(Integer cryptonym) {
		this.cryptonym = cryptonym;
	}

	public Long getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getFlag() {
		return this.flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public java.sql.Timestamp getSendDate() {
		return this.sendDate;
	}

	public void setSendDate(java.sql.Timestamp sendDate) {
		this.sendDate = sendDate;
	}

	public String getSurveyName() {
		return this.surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public String getSurveydesc() {
		return this.surveydesc;
	}

	public void setSurveydesc(String surveydesc) {
		this.surveydesc = surveydesc;
	}

	public Integer getVoteCount() {
		return this.voteCount;
	}

	public void setVoteCount(Integer voteCount) {
		this.voteCount = voteCount;
	}

	// bi-directional many-to-one association to InquiryScope
	public java.util.Set<InquiryScope> getInquiryScopes() {
		return this.inquiryScopes;
	}

	public void setInquiryScopes(java.util.Set<InquiryScope> inquiryScopes) {
		this.inquiryScopes = inquiryScopes;
	}

	// bi-directional many-to-one association to InquirySubsurvey
	public java.util.Set<InquirySubsurvey> getInquirySubsurveys() {
		return this.inquirySubsurveys;
	}

	public void setInquirySubsurveys(
			java.util.Set<InquirySubsurvey> inquirySubsurveys) {
		this.inquirySubsurveys = inquirySubsurveys;
	}

	// bi-directional many-to-one association to InquirySubsurveyitem
	public java.util.Set<InquirySubsurveyitem> getInquirySubsurveyitems() {
		return this.inquirySubsurveyitems;
	}

	public void setInquirySubsurveyitems(
			java.util.Set<InquirySubsurveyitem> inquirySubsurveyitems) {
		this.inquirySubsurveyitems = inquirySubsurveyitems;
	}

	// bi-directional many-to-one association to InquirySurveytype
	public InquirySurveytype getInquirySurveytype() {
		return this.inquirySurveytype;
	}

	public void setInquirySurveytype(InquirySurveytype inquirySurveytype) {
		this.inquirySurveytype = inquirySurveytype;
	}

	// bi-directional many-to-one association to InquirySurveydiscuss
	public java.util.Set<InquirySurveydiscuss> getInquirySurveydiscusses() {
		return this.inquirySurveydiscusses;
	}

	public void setInquirySurveydiscusses(
			java.util.Set<InquirySurveydiscuss> inquirySurveydiscusses) {
		this.inquirySurveydiscusses = inquirySurveydiscusses;
	}

	// bi-directional many-to-one association to InquiryVotedefinite
	public java.util.Set<InquiryVotedefinite> getInquiryVotedefinites() {
		return this.inquiryVotedefinites;
	}

	public void setInquiryVotedefinites(
			java.util.Set<InquiryVotedefinite> inquiryVotedefinites) {
		this.inquiryVotedefinites = inquiryVotedefinites;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	public String getCheckMind() {
		return checkMind;
	}

	public void setCheckMind(String checkMind) {
		this.checkMind = checkMind;
	}

	public Long getIssuerId() {
		return issuerId;
	}

	public void setIssuerId(Long issuerId) {
		this.issuerId = issuerId;
	}

	public Long getSurveyTypeId() {
		return surveyTypeId;
	}

	public void setSurveyTypeId(Long surveyTypeId) {
		this.surveyTypeId = surveyTypeId;
	}

	public Boolean getAttachmentsFlag() {
		return attachmentsFlag;
	}

	public void setAttachmentsFlag(Boolean attachmentsFlag) {
		this.attachmentsFlag = attachmentsFlag;
	}

	public boolean isAllowViewResult() {
		return allowViewResult;
	}

	public void setAllowViewResult(boolean allowViewResult) {
		this.allowViewResult = allowViewResult;
	}

	public boolean isAllowViewResultAhead() {
		return allowViewResultAhead;
	}

	public void setAllowViewResultAhead(boolean allowViewResultAhead) {
		this.allowViewResultAhead = allowViewResultAhead;
	}
}