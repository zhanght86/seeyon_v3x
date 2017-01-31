package com.seeyon.v3x.doc.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocFavorite;

public class DocFavoriteDao extends BaseHibernateDao<DocFavorite> {
	/**
	 * 获取排列的序号
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public int getOrderNum(final long orgId, final String orgType) {
		return (Integer)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final List<Type> parameterTypes = new ArrayList<Type>();
				final List<Object> parameterValues = new ArrayList<Object>();
				
				parameterTypes.add(Hibernate.LONG);
				parameterValues.add(orgId);
				parameterTypes.add(Hibernate.STRING);
				parameterValues.add(orgType);
				
				String hsql = "select max(orderNum) from DocFavorite where orgId=? and orgType=?";
				Object ret = session.createQuery(hsql)
				.setParameters(parameterValues.toArray(new Object[parameterValues.size()]), parameterTypes.toArray(new Type[parameterTypes.size()]))
				.uniqueResult();
				if(ret == null)
					return 1;
				else
					return (Integer)ret + 1;
				
			}
    	});
		
		

//		Session session = getSession();
//		Query query = session.createQuery(hsql);
//		if (query.uniqueResult() != null) {
//			return (Integer) query.uniqueResult() + 1;
//		}
//		super.releaseSession(session);
//		return 1;
	}
	
//	public Session getASession() {
//		return super.getSession();
//	}
//	public void releaseTheSession(Session session){
//		super.releaseSession(session);
//	}

}
