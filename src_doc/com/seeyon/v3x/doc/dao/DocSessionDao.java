package com.seeyon.v3x.doc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.doc.domain.DocSession;

@Deprecated
public class DocSessionDao extends BaseHibernateDao<DocSession> {

	// 读取当前用户最近文档总数
	public int getDocSessionCount(final long currentUserId) {	
//		String hsql = "select count(*) from DocSession as a where a.accessUserId=?";
//		Session session = getSession();
//		Query query = session.createQuery(hsql);
//		query.setLong(1, currentUserId);
//		int ret = 0;
//		if (query.uniqueResult() != null) {
//			ret =  (Integer)query.uniqueResult();
//		}
//		super.releaseSession(session);
//		
//		return ret;
		
		return (Integer)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "select count(*) from DocSession as a where a.accessUserId=?";
				return (Integer)session.createQuery(hsql).setLong(1, currentUserId).uniqueResult();
			}
    	});
	}
	
	public DocSession getDocSession(long docResId, long curUserId) {
		String hsql = "from DocSession as a where a.docResourceId=? and a.accessUserId=?";
		Object[] values = {docResId, curUserId};
		List<DocSession> docSessions = super.find(hsql, values);
		if (docSessions == null || docSessions.size() == 0) {
			return null;
		}
		else {
			return docSessions.get(0);
		}
	}
	
	/**
	 * 取得某个用户的最近文档
	 */
	public List<DocSession> getDocSessions(final long currentUserId) {
		if (Pagination.isNeedCount()) {
			int total = this.getDocSessionCount(currentUserId);
			Pagination.setRowCount(total);
		}
		
		
//		Session session = getSession();
//		Query query = session.createQuery(hsql);
//		query.setLong(1, currentUserId);
//		query.setFirstResult(Pagination.getFirstResult());
//		query.setMaxResults(Pagination.getMaxResults());
		List<DocSession> ret = (List<DocSession>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "from DocSession as a where a.accessUserId=? order by accessTime desc";
				return (List<DocSession>)session.createQuery(hsql).setLong(1, currentUserId)
					.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults()).list();
			}
    	});
		
		
		
		return ret;
	}
//	public List<DocSession> getAllDocSessions(long currentUserId) {
//		String hsql = "from DocSession as a where a.accessUserId=? order by accessTime desc";
//
//		return this.find(hsql, currentUserId);
//	}
	
}
