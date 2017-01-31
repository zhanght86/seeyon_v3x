package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_surveytypeextend database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquirySurveytypeextend extends com.seeyon.v3x.common.domain.BaseModel implements Serializable, Comparable<InquirySurveytypeextend> {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	public static final Integer MANAGER_SYSTEM = 0;//调查管理员

	public static final Integer MANAGER_CHECK = 1;//调查审核员

	private Integer managerDesc;
	private Long managerId;
	private InquirySurveytype inquirySurveytype;
	
	private Integer  sort =0;

    public InquirySurveytypeextend() {
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



	public Integer getManagerDesc() {
		return this.managerDesc;
	}
	public void setManagerDesc(Integer managerDesc) {
		this.managerDesc = managerDesc;
	}

	public Long getManagerId() {
		return this.managerId;
	}
	public void setManagerId(Long managerId) {
		this.managerId = managerId;
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


	/**
	 * 实现排序接口用于保持管理员的排列顺序
	 */
	public int compareTo(InquirySurveytypeextend o) {
		if(this.getSort().intValue() > o.getSort().intValue()) {
			return 1;
		} else if(this.getSort().intValue() < o.getSort().intValue()) {
			return -1;
		}
		return 0;
	}
}