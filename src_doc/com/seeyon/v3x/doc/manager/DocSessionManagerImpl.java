package com.seeyon.v3x.doc.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.doc.dao.DocSessionDao;
import com.seeyon.v3x.doc.domain.DocSession;
@Deprecated
public class DocSessionManagerImpl implements DocSessionManager {
	
	private DocSessionDao docSessionDao;
	private DocMimeTypeManager docMimeTypeManager;
	
	public DocSessionDao getDocSessionDao() {
		return docSessionDao;
	}
	
	public void setDocSessionDao(DocSessionDao docSessionDao) {
		this.docSessionDao = docSessionDao;
	}
	
	public DocMimeTypeManager getDocMimeTypeManager() {		
		return docMimeTypeManager;		
	}
	
	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}
	
	public void setDocSession(long docResourceId,
			long docMimeTypeId,
			String name,
			long accessUserId)  {
		DocSession docSession = this.getDocSession(docResourceId, accessUserId);
		if (docSession == null) {
			this.createDocSession(docResourceId, docMimeTypeId, name, accessUserId);
		}
		else {
			this.updateDocSession(docSession);
		}
	}
	
	private void createDocSession(long docResourceId,
			long docMimeTypeId,
			String name,
			long accessUserId) {	
		DocSession docSession = new DocSession();
		docSession.setIdIfNew();
		docSession.setName(name);
		docSession.setDocResourceId(docResourceId);
		docSession.setDocMimeType(docMimeTypeManager.getDocMimeTypeById(docMimeTypeId));
		docSession.setAccessUserId(accessUserId);
		long l = System.currentTimeMillis();
		docSession.setAccessTime(new Timestamp(l));
		docSessionDao.save(docSession);
	}
	
	private void updateDocSession(DocSession docSession) {
		long l = System.currentTimeMillis();
		docSession.setAccessTime(new Timestamp(l));
		docSessionDao.update(docSession);
	}
	
	private DocSession getDocSession(long docResId, long currentUserId) {
		return docSessionDao.getDocSession(docResId, currentUserId);
	}
	
	public int getDocSessionCount(long currentUserId) {
		return docSessionDao.getDocSessionCount(currentUserId);
	}
	
	public List<DocSession> getDocSessions(long currentUserId) {			
		return docSessionDao.getDocSessions(currentUserId);
	}

	
//	/**
//	 * 返回用户文档会话列表。
//	 * @param currentUserId 用户id
//	 * @return List
//	 */
//	public List<DocSession> getDocSessionsByCount(long currentUserId, int count) {
//		List<DocSession> ret = new ArrayList<DocSession>();
//		List<DocSession> list = docSessionDao.getAllDocSessions(currentUserId);
//		if(list == null || list.size() == 0)
//			return ret;
//		for(int i = 0; i < count; i++) {
//			if(i == list.size())
//				break;
//			ret.add(list.get(i));
//		}
//		return ret;
//	}
	
	/**
	 * 删除最近文档记录
	 */
	public void deleteDocSessionsByDocId(long docResourceId) {
		String hql = "delete from DocSession where docResourceId = ?" ;
	//	List list = docSessionDao.find(hql, docResourceId);
	//	if(list == null  || list.size() == 0)
	//		return;
		docSessionDao.bulkUpdate(hql, null, docResourceId);
	}
	
	/**
	 * 查找
	 */
	public List<DocSession> getAllDocSessions(long userId){
		String hql = "from DocSession where accessUserId = ? order by accessTime desc";
		return docSessionDao.find(hql, userId);
	}
	public List<DocSession> pagedGetDocSessions(long userId){
		List<DocSession> ret = new ArrayList<DocSession>();
		List<DocSession> list = this.getAllDocSessions(userId);
		
		if(list == null)
			return ret;

		int first = Pagination.getFirstResult();
		int pageSize = Pagination.getMaxResults();
		int end1 = first + pageSize;
		int end2 = list.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		for(int i = first; i < end;  i++) {
			ret.add(list.get(i));
		}	
		
		return ret;
	}
	public int getAllDocSessionsTotal(long userId) {
		List list = this.getAllDocSessions(userId);
		if(list == null)
			return 0;
		else
			return list.size();
	}
	
}
