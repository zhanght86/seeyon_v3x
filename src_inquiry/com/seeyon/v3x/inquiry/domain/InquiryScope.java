package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_scope database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquiryScope extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String scopeDesc;
	private Long scopeId;
	
	private Integer  sort = 0;
	
	private InquirySurveybasic inquirySurveybasic;

    public InquiryScope() {
    }

	/**
	 * @return the sort
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}



	public String getScopeDesc() {
		return this.scopeDesc;
	}
	public void setScopeDesc(String scopeDesc) {
		this.scopeDesc = scopeDesc;
	}

	public Long getScopeId() {
		return this.scopeId;
	}
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
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