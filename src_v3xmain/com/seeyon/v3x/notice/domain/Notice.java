package com.seeyon.v3x.notice.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 公示板内容
 */
public class Notice extends BaseModel implements Serializable {

	private static final long serialVersionUID = -4707684560303690410L;

	private String paramName;// 废弃
	
	private String paramValue;// 公示板内容
	private Date createDate;// 创建时间
	private Date updateDate;// 修改时间
	private Long boardId;// 栏目singleBoardId

	public Notice() {

	}

	public Notice(String paramValue, Long boardId, Date createDate, Date updateDate) {
		this.paramValue = paramValue;
		this.boardId = boardId;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

}