/**
 * 
 */
package com.seeyon.v3x.batch;

import com.seeyon.v3x.common.domain.BaseModel;


/**
 * @author dongyj
 *
 */
public class BatchData {
	
	private Long affairId;
	private Long summaryId;
	private Integer category;
	
	private BaseModel opinion;
	
	public BatchData(Long affairId,Long summaryId,int category){
		this.affairId = affairId;
		this.summaryId = summaryId;
		this.category = category;
	}
	
	public BatchData(Long affairId,Long summaryId,Integer category,BaseModel opinion){
		this(affairId,summaryId,category);
		this.opinion = opinion;
	}
	public BatchData(){}
	
	public Long getAffairId() {
		return affairId;
	}
	public void setAffairId(Long affairId) {
		this.affairId = affairId;
	}
	public BaseModel getOpinion() {
		return opinion;
	}
	public void setOpinion(BaseModel opinion) {
		this.opinion = opinion;
	}
	public Long getSummaryId() {
		return summaryId;
	}
	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}
	
	
}
