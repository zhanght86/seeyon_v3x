package com.seeyon.v3x.inquiry.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the inquiry_subsurveyitem database table.
 * 调查项信息
 * @author BEA Workshop Studio
 */
public class InquirySubsurveyitem extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String content;//调查项内容
	private long subsurveyId;//调查问题项ID
	private Integer voteCount = 0;//投票数
	private InquirySurveybasic inquirySurveybasic;
    private Integer sort;//排序
    //加一个当前用户是否选择该问题的标志.
    private boolean curUserFlag=false;
    //用于区分是否是其他选项：0：否；1：是
    private Integer otherOption;
    
    public Integer getOtherOption() {
		return otherOption;
	}

	public void setOtherOption(Integer otherOption) {
		this.otherOption = otherOption;
	}
	
    public InquirySubsurveyitem() {
    }

    
	public boolean isCurUserFlag()
	{
		return curUserFlag;
	}


	public void setCurUserFlag(boolean curUserFlag)
	{
		this.curUserFlag = curUserFlag;
	}


	/**
	 * @return the sort
	 */
	public Integer getSort() {
		return sort;
	}


	/**
	 * @return the voteCount
	 */
	public Integer getVoteCount() {
		return voteCount;
	}


	/**
	 * @param voteCount the voteCount to set
	 */
	public void setVoteCount(Integer voteCount) {
		this.voteCount = voteCount;
	}


	/**
	 * @param sort the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}


	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public long getSubsurveyId() {
		return this.subsurveyId;
	}
	public void setSubsurveyId(long subsurveyId) {
		this.subsurveyId = subsurveyId;
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