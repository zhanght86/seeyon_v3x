package com.seeyon.v3x.bbs.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.util.IdentifierUtil;

/**
 * The persistent class for the v3x_bbs_article database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xBbsArticle extends BaseModel implements Serializable {

	private static final long serialVersionUID = -1819335717103987958L;

	protected static final int INENTIFIER_SIZE = 20;

	/**
	 * 标志位, 共100位，采用枚举的自然顺序
	 */
	protected static enum INENTIFIER_INDEX {
		HAS_ATTACHMENTS, // 是否有附件
	};

	private String articleName;

	private Integer clickNumber = 0;
	
	private Integer replyNumber = 0;

	// private String issueArea;
	private String content;

	private Long department;

	private Boolean anonymousFlag;
	
	//讨论主题增加一项设定：是否允许匿名回复 added by Meng Yang 2009-05-11
	private Boolean anonymousReplyFlag;

	private Boolean eliteFlag;

	private java.sql.Timestamp issueTime;
	
	private java.sql.Timestamp modifyTime;

	private Long issueUserId;

	private Boolean messageNotifyFlag;

	private Long post;

	private Byte resourceFlag;

	private Integer topSequence;

	private Byte state;

	private Long boardId;

	private String identifier;
	
	private Long accountId;

	public V3xBbsArticle() {
	}

	public String getArticleName() {
		return this.articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public Integer getClickNumber() {
		return this.clickNumber;
	}

	public void setClickNumber(Integer clickNumber) {
		this.clickNumber = clickNumber;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getDepartment() {
		return this.department;
	}

	public void setDepartment(Long department) {
		this.department = department;
	}

	public Boolean getEliteFlag() {
		return this.eliteFlag;
	}

	public void setEliteFlag(Boolean eliteFlag) {
		this.eliteFlag = eliteFlag;
	}

	public java.sql.Timestamp getIssueTime() {
		return this.issueTime;
	}

	public void setIssueTime(java.sql.Timestamp issueTime) {
		this.issueTime = issueTime;
	}

	/**
	 * @return the issueUserId
	 */
	public Long getIssueUserId() {
		return issueUserId;
	}

	/**
	 * @param issueUserId
	 *            the issueUserId to set
	 */
	public void setIssueUserId(Long issueUserId) {
		this.issueUserId = issueUserId;
	}

	public Boolean getMessageNotifyFlag() {
		return this.messageNotifyFlag;
	}

	public void setMessageNotifyFlag(Boolean messageNotifyFlag) {
		this.messageNotifyFlag = messageNotifyFlag;
	}

	public Long getPost() {
		return this.post;
	}

	public void setPost(Long post) {
		this.post = post;
	}

	public Byte getResourceFlag() {
		return this.resourceFlag;
	}

	public void setResourceFlag(Byte resourceFlag) {
		this.resourceFlag = resourceFlag;
	}

	public Integer getTopSequence() {
		return this.topSequence;
	}

	public void setTopSequence(Integer topSequence) {
		this.topSequence = topSequence;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	/**
	 * @return the anonymousFlag
	 */
	public Boolean getAnonymousFlag() {
		return anonymousFlag;
	}

	/**
	 * @param anonymousFlag
	 *            the anonymousFlag to set
	 */
	public void setAnonymousFlag(Boolean anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public String getIdentifier() {
		return IdentifierUtil.newIdentifier(this.identifier, INENTIFIER_SIZE,
				'0');
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isHasAttachments() {
		return IdentifierUtil.lookupInner(identifier,
				INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(), '1');
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.identifier = IdentifierUtil.update(this.getIdentifier(),
				INENTIFIER_INDEX.HAS_ATTACHMENTS.ordinal(),
				hasAttachments ? '1' : '0');
	}

	public Integer getReplyNumber() {
		return replyNumber;
	}

	public void setReplyNumber(Integer replyNumber) {
		this.replyNumber = replyNumber;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Boolean getAnonymousReplyFlag() {
		return anonymousReplyFlag;
	}

	public void setAnonymousReplyFlag(Boolean anonymousReplyFlag) {
		this.anonymousReplyFlag = anonymousReplyFlag;
	}

	public java.sql.Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(java.sql.Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	/**
	 * 发布范围
	 */
	private String issueArea;

	public String getIssueArea() {
		return issueArea;
	}

	public void setIssueArea(String issueArea) {
		this.issueArea = issueArea;
	}

}