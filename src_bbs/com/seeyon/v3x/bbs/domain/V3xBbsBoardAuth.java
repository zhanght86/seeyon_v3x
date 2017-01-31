package com.seeyon.v3x.bbs.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_bbs_board_auth database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xBbsBoardAuth extends BaseModel implements Serializable {

	private static final long serialVersionUID = 6853979360520412870L;

	private Byte issueFlag;

	private Long moduleId;

	private String moduleType;

	private Byte queryFlag;

	private Long boardId;

	private int authType;

	public V3xBbsBoardAuth(Long moduleId, String moduleType, Long boardId, BbsConstants.BBS_AUTH_TYPE authType) {
		super.setIdIfNew();
		this.moduleId = moduleId;
		this.moduleType = moduleType;
		this.boardId = boardId;
		this.authType = authType.ordinal();
	}

	public int getAuthType() {
		return authType;
	}

	public void setAuthType(int authType) {
		this.authType = authType;
	}

	public V3xBbsBoardAuth() {
	}

	public Byte getIssueFlag() {
		return this.issueFlag;
	}

	public void setIssueFlag(Byte issueFlag) {
		this.issueFlag = issueFlag;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public Byte getQueryFlag() {
		return this.queryFlag;
	}

	public void setQueryFlag(Byte queryFlag) {
		this.queryFlag = queryFlag;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

}