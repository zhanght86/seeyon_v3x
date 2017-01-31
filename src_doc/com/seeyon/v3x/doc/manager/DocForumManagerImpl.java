package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.doc.dao.DocForumDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocForum;
import com.seeyon.v3x.doc.domain.DocResource;

public class DocForumManagerImpl implements DocForumManager {
	private DocForumDao docForumDao;
	private DocResourceDao docResourceDao;

	public DocResourceDao getDocResourceDao() {
		return docResourceDao;
	}

	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}

	public DocForumDao getDocForumDao() {
		return docForumDao;
	}

	public void setDocForumDao(DocForumDao docForumDao) {
		this.docForumDao = docForumDao;
	}

	public void deleteDocForumByDocId(DocResource dr) {
		List<DocResource> dlist = new ArrayList<DocResource>();
		dlist.add(dr);
		if(dr.getIsFolder()) {		
			String hsql = "from DocResource as a where a.logicalPath like :lp or a.id = :aid";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lp", dr.getLogicalPath() + ".%");
			map.put("aid", dr.getId());
			dlist = docResourceDao.find(hsql, -1, -1, map);
		}
		
		List<Long> in = new ArrayList<Long>();
		for (int i = 0; i < dlist.size(); i++) {
			in.add(dlist.get(i).getId());
		}

		String hsql = "delete from DocForum as a where a.docResourceId in (:in)";
		
		Map<String, Object> namedParatmeter = new HashMap<String, Object>();
		namedParatmeter.put("in", in);
		
		docForumDao.bulkUpdate(hsql, namedParatmeter);
	}

	public void deleteDocForumById(Long id) {
		String hsql = "delete from DocForum as a where a.id=? or a.parentForumId=?";
		docForumDao.bulkUpdate(hsql, null, id, id);
	}

	public List<DocForum> findDocForumsByDocId(Long docResourceId) {
		String hsql = "from DocForum as a where a.docResourceId=? order by a.createTime";
		return docForumDao.find(hsql, docResourceId);
	}

	// public DocForum findDocForumsById(Long id) {
	// return docForumDao.get(id);
	// }

	public DocForum pubDocForum(Long docResourceId, Long parentForumId,
			String subject, String body, Long createUserId) {
		DocForum docForum = new DocForum();
		docForum.setBody(body);
		docForum.setCreateTime(new java.sql.Timestamp(System
				.currentTimeMillis()));
		docForum.setCreateUserId(createUserId);
		docForum.setDocResourceId(docResourceId);
		docForum.setIdIfNew();
		docForum.setParentForumId(parentForumId);
		docForum.setSubject(subject);
		docForumDao.save(docForum);
		return docForum;
	}

	public void updateDocForum(Long id, String subject, String body) {
		DocForum docForum = docForumDao.get(id);
		docForum.setBody(body);
		docForum.setSubject(subject);
		docForumDao.update(docForum);
	}
	
	/**
	 * 根据文档id获取所有一级评论
	 */
	public List<DocForum> findFirstForumsByDocId(Long docResourceId) {
		String hsql = "from DocForum as a where a.docResourceId=? and parentForumId=0 order by a.createTime";
		return docForumDao.find(hsql, docResourceId);
	}
	
	/**
	 * 根据评论id获取所有回复
	 */
	public List<DocForum> findReplyByForumId(Long forumId) {
		String hsql = "from DocForum as a where parentForumId=? order by a.createTime";
		return docForumDao.find(hsql, forumId);
	}
	
	/**
	 * 根据文档id获取所有回复
	 */
	public List<DocForum> findReplyByDocId(Long docResId) {
		String hsql = "from DocForum as a where a.docResourceId=? and parentForumId!=0 order by a.createTime";
		return docForumDao.find(hsql, docResId);
	}
	
	public void deleteDocForumAndReply(long forumId) {
		String hsql = "delete from DocForum as a where a.id=? or a.parentForumId=?";
		Object[] values = {forumId, forumId};
		docForumDao.bulkUpdate(hsql, null, values);
	}
	
	public void deleteReply(long replyId) {
		docForumDao.delete(replyId);
	}

}
