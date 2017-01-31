package com.seeyon.v3x.bbs.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_bbs_article_reply database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xBbsArticleReply  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String content;
	private String replyName;
	private Long replyUserId;
//	private String useReplyName;
//	private String useReplyContent;
	private Byte useReplyFlag;
	
	//讨论回复增加一项设定：是否允许匿名发布回复 added by Meng Yang 2009-05-11
	private Boolean anonymousFlag;
	
	private Long useReplyId;
	private Byte state;
//	private V3xBbsBoard v3xBbsBoard;
	private Long articleId;
	private java.sql.Timestamp replyTime;
	private java.sql.Timestamp modifyTime;

    public V3xBbsArticleReply() {
    	
    }

	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getReplyName() {
		return this.replyName;
	}
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}

	public Long getReplyUserId() {
		return this.replyUserId;
	}
	public void setReplyUserId(Long replyUserId) {
		this.replyUserId = replyUserId;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public java.sql.Timestamp getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(java.sql.Timestamp replyTime) {
		this.replyTime = replyTime;
	}

	public Byte getUseReplyFlag() {
		return useReplyFlag;
	}

	public void setUseReplyFlag(Byte useReplyFlag) {
		this.useReplyFlag = useReplyFlag;
	}

//	public String getUseReplyContent() {
//		return useReplyContent;
//	}
//
//	public void setUseReplyContent(String useReplyContent) {
//		this.useReplyContent = useReplyContent;
//	}
//
//	public String getUseReplyName() {
//		return useReplyName;
//	}
//
//	public void setUseReplyName(String useReplyName) {
//		this.useReplyName = useReplyName;
//	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
	}

	/**
	 * @return the useReplyId
	 */
	public Long getUseReplyId() {
		return useReplyId;
	}

	/**
	 * @param useReplyId the useReplyId to set
	 */
	public void setUseReplyId(Long useReplyId) {
		this.useReplyId = useReplyId;
	}

	public Boolean getAnonymousFlag() {
		return anonymousFlag;
	}

	public void setAnonymousFlag(Boolean anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public java.sql.Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(java.sql.Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

//	public V3xBbsBoard getV3xBbsBoard() {
//		return v3xBbsBoard;
//	}
//
//	public void setV3xBbsBoard(V3xBbsBoard bbsBoard) {
//		v3xBbsBoard = bbsBoard;
//	}
}