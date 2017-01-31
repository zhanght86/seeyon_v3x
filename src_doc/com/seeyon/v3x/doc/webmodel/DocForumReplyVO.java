package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocForum;

/**
 * 评论回复vo
 */
public class DocForumReplyVO {
	private DocForum forum;

	// 评论人
	private String name;
	// 内容
	private String body;
	// 时间
	private java.sql.Timestamp time;

	public DocForumReplyVO(DocForum forum) {
		this.forum = forum;
		body = forum.getBody();
		time = forum.getCreateTime();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public DocForum getForum() {
		return forum;
	}

	public void setForum(DocForum forum) {
		this.forum = forum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.sql.Timestamp getTime() {
		return time;
	}

	public void setTime(java.sql.Timestamp time) {
		this.time = time;
	}
}
