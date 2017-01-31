package com.seeyon.v3x.resource.dao;

import java.util.*;


import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.resource.domain.Resource;

public class ResourceDao extends BaseHibernateDao<Resource> {

	public List list() {
		return (List) getHibernateTemplate().find("from Resource");
	}

	public List listByDomainId() {
		User user = CurrentUser.get();
		Long domainId = user.getLoginAccount();
		return (List) super.find("from Resource as a where a.accountId=?", domainId);
	}
	
	public List listForPage() {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
//				增加单位过滤--lucx
				User user = CurrentUser.get();		
				Long accountId = user.getAccountId();
				
				StringBuffer countHql = new StringBuffer();
				countHql.append("select count(distinct rs)");
				countHql.append(" from Resource rs ");
				countHql.append(" where " + "rs.accountId="+accountId);//条件
				Query countQuery = session.createQuery(countHql.toString());
				int count = ((Integer) countQuery.uniqueResult()).intValue();
				Pagination.setRowCount(count);
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct rs");
				hql.append(" from Resource rs ");
				hql.append(" where " + "rs.accountId="+accountId);//条件
				Query query = session.createQuery(hql.toString());
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		});
	}
	
	public void delete(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
	}
	
	public Resource findByPrimaryKey(Long id){
		return (Resource) getHibernateTemplate().load(Resource.class, new Long(id));
	}
	
	public void update(Resource rs){
		getHibernateTemplate().update(rs);
	}
	
	public List findByType(final String type) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
//				增加单位过滤--lucx
				User user = CurrentUser.get();		
				Long accountId = user.getAccountId();
				
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct rs");
				hql.append(" from Resource rs ");
				hql.append(" where rs.type = :type");
				hql.append(" and " + "rs.accountId = :accountId");//条件
				Query query = session.createQuery(hql.toString());
				query.setString("type", type);
				query.setLong("accountId", accountId);
				return query.list();
			}
		});
	}
}
