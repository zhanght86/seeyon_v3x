package com.seeyon.v3x.doc.manager;

import java.util.List;

import com.seeyon.v3x.doc.dao.DocAclDao;
import com.seeyon.v3x.doc.dao.DocBorrowHistoryDao;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocBorrowHistory;
import com.seeyon.v3x.doc.util.Constants;

public class DocBorrowHistoryManagerImpl implements DocBorrowHistoryManager {
	private DocBorrowHistoryDao docBorrowHistoryDao;

	private DocAclDao docAclDao;

	public DocAclDao getDocAclDao() {
		return docAclDao;
	}

	public void setDocAclDao(DocAclDao docAclDao) {
		this.docAclDao = docAclDao;
	}

	public DocBorrowHistoryDao getDocBorrowHistoryDao() {
		return docBorrowHistoryDao;
	}

	public void setDocBorrowHistoryDao(DocBorrowHistoryDao docBorrowHistoryDao) {
		this.docBorrowHistoryDao = docBorrowHistoryDao;
	}
//借阅中
	public DocBorrowHistory getBorrowingByuserAndDocId(Long userId,
			String userType, Long docResourceId) {
		String hsql = "from DocBorrowHistory as a where a.userId=? and a.userType=? "
				+ "and a.docResourceId=?" + " and a.sdate <=? and a.edate>=?";
		List<DocBorrowHistory> list = docBorrowHistoryDao.find(hsql, userId,
				userType, docResourceId, new java.sql.Timestamp(System
						.currentTimeMillis()), new java.sql.Timestamp(System
						.currentTimeMillis()));
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<DocBorrowHistory> getHistoryByDocId(Long docResourceId) {
		String hsql = "from DocBorrowHistory as a where a.docResourceId=?";
		return docBorrowHistoryDao.find(hsql, docResourceId);
	}

	public List<DocBorrowHistory> getHistoryByUser(Long userId,
			String userType, String beginTime, String endTime) {
//		String hsql = " from DocBorrowHistory ";
//		String where = "";
//		if (userId != null) {
//			where = where + " and userId = " + userId;
//		}
//		if (userType != null) {
//			where = where + " and userType = '" + userType + "' ";
//		}
//		if (beginTime != null && beginTime.length() > 0) {
//			where = where + " and sdate >= ' " + beginTime + "' ";
//		}
//		if (endTime != null && endTime.length() > 0) {
//			where = where + " and sdate <=' " + endTime + "' ";
//		}
//		if (where.length() > 0) {
//			where = where.substring(4, where.length());
//		}
//		hsql = hsql + where;
//		return docBorrowHistoryDao.find(hsql);
		return null;
	}

	public void saveHistory(Long userId, String userType, Long docResourceId,
			java.sql.Timestamp sdate, java.sql.Timestamp edate) {
		DocBorrowHistory d = new DocBorrowHistory();
		d.setBorrowType(Constants.SHARETYPE_DEPTBORROW);
		d.setDocResourceId(docResourceId);
		d.setEdate(edate);
		d.setIdIfNew();
		d.setSdate(sdate);
		d.setUserId(userId);
		d.setUserType(userType);
		docBorrowHistoryDao.save(d);
	}

//	public void updateHistory(Long userId, String userType, Long docResourceId,
//			java.sql.Timestamp edate) {
//		DocBorrowHistory d = this.getBorrowingByuserAndDocId(userId, userType,
//				docResourceId);
//		if (d != null) {
//			d.setEdate(edate);
//			docBorrowHistoryDao.update(edate);
//		}
//
//	}

	public void borrowTask() {
		java.sql.Timestamp time = new java.sql.Timestamp(System
				.currentTimeMillis());
		String hsql1 = "from DocAcl as a where  a.potenttype="
				+ Constants.DEPTBORROW + " and  a.sharetype="
				+ Constants.SHARETYPE_DEPTBORROW + " and a.edate<=?";
		List<DocAcl> acl = docAclDao.find(hsql1, time);//查找单位借阅权限结束小于等于当前时间的记录
		for (DocAcl a : acl) {
			DocBorrowHistory d = new DocBorrowHistory();
			d.setBorrowType(Constants.SHARETYPE_DEPTBORROW);
			d.setDocResourceId(a.getDocResourceId());
			d.setEdate(a.getEdate());
			d.setIdIfNew();
			d.setSdate(a.getSdate());
			d.setUserId(a.getUserId());
			d.setUserType(a.getUserType());
			docBorrowHistoryDao.save(d);//保存
		}
		String hsql2 = "delete from DocAcl as a where  ( a.potenttype="
				+ Constants.DEPTBORROW + " and  a.sharetype="
				+ Constants.SHARETYPE_DEPTBORROW + ") or (a.potenttype="
				+ Constants.PERSONALBORROW + " and  a.sharetype="
				+ Constants.SHARETYPE_PERSBORROW + " )" + " and a.edate<="
				+ time;
		docAclDao.bulkUpdate(hsql2, null);//删除权限记录
	}

	public int getHistoryByDocIdCount(Long docResourceId) {
		String hsql = "from DocBorrowHistory as a where a.docResourceId=?";
		return docBorrowHistoryDao.find(hsql, docResourceId).size();
	}

	@SuppressWarnings("unchecked")
	public List<DocBorrowHistory> getHistoryByDocIdPage(Long docResourceId, int pageNo, int pageSize) {
		String hsql = "from DocBorrowHistory as a where a.docResourceId=?";
		return (List<DocBorrowHistory>)docBorrowHistoryDao.pagedQuery(hsql, pageNo, pageSize).getResult();
	}

	public int getHistoryByUserCount(Long userId, String userType, String beginTime, String endTime) {
//		String hsql = " from DocBorrowHistory ";
//		String where = "";
//		if (userId != null) {
//			where = where + " and userId = " + userId;
//		}
//		if (userType != null) {
//			where = where + " and userType = '" + userType + "' ";
//		}
//		if (beginTime != null && beginTime.length() > 0) {
//			where = where + " and sdate >= ' " + beginTime + "' ";
//		}
//		if (endTime != null && endTime.length() > 0) {
//			where = where + " and sdate <=' " + endTime + "' ";
//		}
//		if (where.length() > 0) {
//			where = where.substring(4, where.length());
//		}
//		hsql = hsql + where;
//		return docBorrowHistoryDao.find(hsql).size();
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<DocBorrowHistory> getHistoryByUserPage(Long userId, String userType, String beginTime, String endTime, int pageNo, int pageSize) {
		String hsql = " from DocBorrowHistory ";
		String where = "";
		if (userId != null) {
			where = where + " and userId = " + userId;
		}
		if (userType != null) {
			where = where + " and userType = '" + userType + "' ";
		}
		if (beginTime != null && beginTime.length() > 0) {
			where = where + " and sdate >= ' " + beginTime + "' ";
		}
		if (endTime != null && endTime.length() > 0) {
			where = where + " and sdate <=' " + endTime + "' ";
		}
		if (where.length() > 0) {
			where = where.substring(4, where.length());
		}
		hsql = hsql + where;
		return (List<DocBorrowHistory>)docBorrowHistoryDao.pagedQuery(hsql, pageNo, pageSize).getResult();
	}

}
