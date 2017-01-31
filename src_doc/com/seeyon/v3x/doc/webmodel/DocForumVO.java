package com.seeyon.v3x.doc.webmodel;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocForum;

/**
 * 文档评论vo
 */
public class DocForumVO {
	private DocForum forum;

	// 评论人
	private String name;
	// 内容
	private String body;

	private java.sql.Timestamp time;
	// 回复集合
	private List<DocForumReplyVO> replys;

	public DocForumVO(DocForum forum) {
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

	public List<DocForumReplyVO> getReplys() {
		return replys;
	}

	public void setReplys(List<DocForumReplyVO> replys) {
		this.replys = replys;
	}
}
