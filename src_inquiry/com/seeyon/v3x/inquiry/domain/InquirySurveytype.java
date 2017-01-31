package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_surveytype database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquirySurveytype extends com.seeyon.v3x.common.domain.BaseModel implements Serializable,Comparator<InquirySurveytype> {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	
	public static final Integer FLAG_NORMAL = 0;// 正常状态

	public static final Integer FLAG_DELETE = 1;// 删除状态

	public static final Integer CENSOR_NO_PASS = 0;// 需要审核审核

	public static final Integer CENSOR_PASS = 1;//不需要审核
	
	public static final Integer Space_Type_Group = 1;	//空间类型集团
	public static final Integer Space_Type_Account = 2;	//空间类型单位
	public static final Integer Space_Type_Department = 3;	//空间类型部门
	public static final Integer Space_Type_Custom = 4;	//空间类型团队
	public static final Integer Space_Type_Public_Custom = 5;	//空间类型自定义单位
	public static final Integer Space_Type_Public_Custom_Group = 6;	//空间类型自定义集团
	private Integer censorDesc;
	//删除标记,0为正常,1为删除
	private Integer flag;
	private String surveyDesc;
	private String typeName;
	private Integer spaceType;
	private Long accountId;
	
//	排序字段
	private Integer  sort = 0;
	
	private java.util.Set<InquiryAuthority> inquiryAuthorities;
//	private java.util.Set<InquirySurveybasic> inquirySurveybasics;
	private java.util.Set<InquirySurveytypeextend> inquirySurveytypeextends;

	public InquirySurveytype() {
		
	}
	
    public InquirySurveytype(Integer sort) {
    	this.sort = sort;
    }

	public Integer getCensorDesc() {
		return this.censorDesc;
	}
	public void setCensorDesc(Integer censorDesc) {
		this.censorDesc = censorDesc;
	}

	public Integer getFlag() {
		return this.flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getSurveyDesc() {
		return this.surveyDesc;
	}
	public void setSurveyDesc(String surveyDesc) {
		this.surveyDesc = surveyDesc;
	}

	public String getTypeName() {
		return this.typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	//bi-directional many-to-one association to InquiryAuthority
	public java.util.Set<InquiryAuthority> getInquiryAuthorities() {
		return this.inquiryAuthorities;
	}
	public void setInquiryAuthorities(java.util.Set<InquiryAuthority> inquiryAuthorities) {
		this.inquiryAuthorities = inquiryAuthorities;
	}

	//bi-directional many-to-one association to InquirySurveybasic
//	public java.util.Set<InquirySurveybasic> getInquirySurveybasics() {
//		return this.inquirySurveybasics;
//	}
//	public void setInquirySurveybasics(java.util.Set<InquirySurveybasic> inquirySurveybasics) {
//		this.inquirySurveybasics = inquirySurveybasics;
//	}

	//bi-directional many-to-one association to InquirySurveytypeextend
	public java.util.Set<InquirySurveytypeextend> getInquirySurveytypeextends() {
		return this.inquirySurveytypeextends;
	}
	public void setInquirySurveytypeextends(java.util.Set<InquirySurveytypeextend> inquirySurveytypeextends) {
		this.inquirySurveytypeextends = inquirySurveytypeextends;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public Integer getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(Integer spaceType) {
		this.spaceType = spaceType;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
//	实现排序   按sort字段
	public int compare(InquirySurveytype type1,InquirySurveytype type2) {
		InquirySurveytype p1=(InquirySurveytype)type1;
		InquirySurveytype p2=(InquirySurveytype)type2;
		if((p1!=null&&p2!=null)&&(p1.sort!=null&&p2.sort!=null)&&p1.sort.intValue()>p2.sort.intValue())
			return 1;
		else
			return 0;
	}

}