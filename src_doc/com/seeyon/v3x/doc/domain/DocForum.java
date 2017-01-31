package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档评论
 */
public class DocForum extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	// 评论内容
	private String body;
	private java.sql.Timestamp createTime;
	private long createUserId;
	private long docResourceId;
	// 父评论id， 没有时候为 0
	private Long parentForumId;
	// 标题，暂未使用
	private String subject;

    public DocForum() {
    }


	public String getBody() {
		return this.body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public long getCreateUserId() {
		return this.createUserId;
	}
	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public long getDocResourceId() {
		return this.docResourceId;
	}
	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}

	public Long getParentForumId() {
		return this.parentForumId;
	}
	public void setParentForumId(Long parentForumId) {
		this.parentForumId = parentForumId;
	}

	public String getSubject() {
		return this.subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}