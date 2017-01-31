package com.seeyon.v3x.plan.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.plan.domain.PlanUserScope;

/**
 * Data access object (DAO) for domain model class PlanUserScope.
 * 
 * @see com.seeyon.v3x.plan.domain.PlanUserScope
 * @author MyEclipse - Hibernate Tools
 */
public class PlanUserScopeDao extends BaseHibernateDao<PlanUserScope> {

	public List list() {
		return (List) getHibernateTemplate().find("from PlanUserScope");
	}
	
	public List listUserScopeByPage(){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
		       String counthql = "select count(*) from PlanUserScope ps where ps.refAccountId = :accountId";
               Query countquery = session.createQuery(counthql);
               countquery.setLong("accountId", CurrentUser.get().getLoginAccount());
	           int count = ((Integer) countquery.uniqueResult()).intValue();
	           Pagination.setRowCount(count);
	           StringBuffer hql = new StringBuffer();
	           hql.append("select distinct ps ");
	           hql.append(" from PlanUserScope ps ");
	           hql.append(" where ps.refAccountId = :accountId");
	           Query query = session.createQuery(hql.toString());
	           query.setLong("accountId", CurrentUser.get().getLoginAccount());
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

	public PlanUserScope findByPrimaryKey(Long id) {
		return (PlanUserScope) getHibernateTemplate().load(PlanUserScope.class,
				new Long(id));
	}

	public void saveOrUpdate(PlanUserScope planUserScope) {
		getHibernateTemplate().saveOrUpdate(planUserScope);
	}

	public PlanUserScope findByRefUserId(Long refUserId) {
		List list = (List) getHibernateTemplate().find(
				"from PlanUserScope where refUserId = ?",
				new Object[] { refUserId });
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (PlanUserScope) list.get(0);
		}

	}
	public PlanUserScope findByRefUserIdAndLoginAccount(Long refUserId) {
//		List list = (List) getHibernateTemplate().find(
//				"from PlanUserScope where refUserId = ?",
//				new Object[] { refUserId });

		String hql = "from PlanUserScope where refUserId = ?  and refAccountId = ? "; //+ refUserId  //+ CurrentUser.get().getLoginAccount();
		List list = super.find(hql,new Object[]{refUserId,CurrentUser.get().getLoginAccount()});
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (PlanUserScope) list.get(0);
		}

	}



	/**
	 * 通过用户id查询该用户可查看的用户集合
	 * 
	 * @param refUserId
	 * @return
	 */
	public List getPlanUserIdListByRefUserId(Long refUserId) {
		PlanUserScope pus = this.findByRefUserId(refUserId);
		if(null==pus) return ListUtils.EMPTY_LIST;
		String temp = pus.getScopeUserIds();
		String[] arg = StringUtils.split(temp, ",");

		List list = new ArrayList();
		if (arg != null) {
			for (int i = 0; i < arg.length;i++) {
				list.add(new Long(arg[i]));
			}
		}
		return list;
	}

}