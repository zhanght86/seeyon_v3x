package com.seeyon.v3x.doc.manager;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocForum;
import com.seeyon.v3x.doc.domain.DocResource;

public interface DocForumManager {
	/**
	 * 发布评论
	 * 
	 * @param docResourceId
	 * @param parentForumId
	 * @param subject
	 * @param body
	 * @param createUserId
	 */
	public DocForum pubDocForum(Long docResourceId, Long parentForumId,
			String subject, String body, Long createUserId);

	/**
	 * 修改评论
	 * 
	 * @param id
	 * @param subject
	 * @param body
	 */
	public void updateDocForum(Long id, String subject, String body);

	/**
	 * 根据评论id删除评论
	 * 
	 * @param id
	 */
	public void deleteDocForumById(Long id);

	/**
	 * 根据文档id删除所有评论
	 * 如果是文档夹，删除所有下级
	 * @param docResourceId
	 */
	public void deleteDocForumByDocId(DocResource dr);

	/**
	 * 根据文档id获取所有评论
	 * 
	 * @param docResourceId
	 * @return
	 */
	public List<DocForum> findDocForumsByDocId(Long docResourceId);
	
	/**
	 * 根据文档id获取所有一级评论
	 */
	public List<DocForum> findFirstForumsByDocId(Long docResourceId);
	
	/**
	 * 根据评论id获取所有回复
	 */
	public List<DocForum> findReplyByForumId(Long forumId);
	/**
	 * 根据文档id获取所有回复
	 */
	public List<DocForum> findReplyByDocId(Long docResId);
	
	/**
	 * 删除文档评论及回复
	 * @param forumId 文档评论id
	 */
	public void deleteDocForumAndReply(long forumId);
	
	/**
	 * 删除文档评论回复
	 * 
	 * @param replyId 回复id
	 */
	public void deleteReply(long replyId);

//	/**
//	 * 根据评论id获取评论对象
//	 * 
//	 * @param id
//	 * @return
//	 */
	// public DocForum findDocForumsById(Long id);
}
