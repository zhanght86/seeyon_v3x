package com.seeyon.v3x.doc.manager;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocSession;

@Deprecated
public interface DocSessionManager {

	/**
	 * 记录文档会话信息。
	 * @param docResourceId 文档id
	 * @param docMimeTypeId 文档类型id
	 * @param name 文档名称
	 * @param accessUserId 当前用户id
	 * @throws Exception
	 */
	public void setDocSession(long docResourceId,
			long docMimeTypeId,
			String name,
			long accessUserId);
	
	/**
	 * 返回用户文档会话记录总数。
	 * @param currentUserId 用户id
	 * @return int
	 */
//	public int getDocSessionCount(long currentUserId);
	
//	/**
//	 * 返回用户文档会话列表。
//	 * @param currentUserId 用户id
//	 * @return List
//	 */
//	public List<DocSession> pagedGetDocSessions(long currentUserId);
	/**
	 * 返回用户文档会话列表。
	 * @param currentUserId 用户id
	 * @return List
	 */
	public List<DocSession> getDocSessions(long currentUserId);
	/**
	 * 返回用户文档会话列表。
	 * @param currentUserId 用户id
	 * @return List
	 */
//	public List<DocSession> getDocSessionsByCount(long currentUserId, int count);
	
	/**
	 * 删除最近文档记录
	 */
	public void deleteDocSessionsByDocId(long docResourceId);
	
	/**
	 * 取得某个用户的所有最近文档
	 */
	public List<DocSession> getAllDocSessions(long userId);
	/**
	 * 分页取得某个用户的最近文档
	 */
	public List<DocSession> pagedGetDocSessions(long userId);
	/**
	 * 取得某个用户的最近文档总数
	 */
	public int getAllDocSessionsTotal(long userId);
	
}
