package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;


/**
 * 记录表单协同关联的协同的记录
 * @author Administrator
 *
 */
public class ColQuoteformRecord extends BaseModel  implements Serializable{

	private static final long serialVersionUID = 2733288415279626233L;
	
	public static final String State_ADD = "add" ;
	public static final String State_DEL = "delete" ;
	
	private Long colSummaryId ; 
	private Long refColSummaryId ;
	private Integer state = 1 ;
	private String fieldName = "" ;
	private Long memberId;
	private Integer type;
	private Long subRecordId;
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getColSummaryId() {
		return colSummaryId;
	}

	public void setColSummaryId(Long colSummaryId) {
		this.colSummaryId = colSummaryId;
	}

	public Long getRefColSummaryId() {
		return refColSummaryId;
	}

	public void setRefColSummaryId(Long refColSummaryId) {
		this.refColSummaryId = refColSummaryId;
	}

	public Long getSubRecordId() {
		return subRecordId;
	}

	public void setSubRecordId(Long subRecordId) {
		this.subRecordId = subRecordId;
	}

}
