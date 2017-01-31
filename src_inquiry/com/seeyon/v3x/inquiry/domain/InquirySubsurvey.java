package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_subsurvey database table.
 * 
 * @author BEA Workshop Studio
 */
public class InquirySubsurvey extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	

	public static final Integer SINGLE = 0;// 单选
	public static final Integer MANY = 1;// 多选
	public static final Integer Q_A = 2;// 问答式
	public static final Integer DISCUSS = 0;// 允许评论
	public static final Integer NO_DISCUSS = 1;//不允许评论
	public static final Integer OTHER=0;//允许其他
	public static final Integer NO_OTHER=1;//不允许其他
	
	public  Integer  maxSelect=0; 
	private Integer discuss;
	private Integer otheritem;
	private Integer singleMany;
	private String subsurveyDesc;
	private String title;
	private Integer sort;
	
	private InquirySurveybasic inquirySurveybasic;
	
    private List<InquirySurveydiscuss> isds;

    public List<InquirySurveydiscuss> getIsds() {
		return isds;
	}
	public void setIsds(List<InquirySurveydiscuss> isds) {
		this.isds = isds;
	}
	public InquirySubsurvey() {
    }
	/**
	 * @return the maxSelect
	 */
	public Integer getMaxSelect() {
		return maxSelect;
	}

	/**
	 * @param maxSelect the maxSelect to set
	 */
	public void setMaxSelect(Integer maxSelect) {
		this.maxSelect = maxSelect;
	}

	public Integer getDiscuss() {
		return this.discuss;
	}
	public void setDiscuss(Integer discuss) {
		this.discuss = discuss;
	}

	public Integer getOtheritem() {
		return this.otheritem;
	}
	public void setOtheritem(Integer otheritem) {
		this.otheritem = otheritem;
	}

	public Integer getSingleMany() {
		return this.singleMany;
	}
	public void setSingleMany(Integer singleMany) {
		this.singleMany = singleMany;
	}

	public String getSubsurveyDesc() {
		return this.subsurveyDesc;
	}
	public void setSubsurveyDesc(String subsurveyDesc) {
		this.subsurveyDesc = subsurveyDesc;
	}

	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
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
}