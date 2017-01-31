package com.seeyon.v3x.plan.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.plan.domain.PlanBody;

/**
 * Data access object (DAO) for domain model class PlanBody.
 * 
 * @see com.seeyon.v3x.plan.domain.PlanBody
 * @author MyEclipse - Hibernate Tools
 */
public class PlanBodyDao extends BaseHibernateDao<PlanBody> {

	public List list() {
		return (List) getHibernateTemplate().find("from PlanBody");
	}

	public void delete(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
	}

	public PlanBody findByPrimaryKey(Long id) {
		return (PlanBody) getHibernateTemplate().load(PlanBody.class,
				new Long(id));
	}

	public void saveOrUpdate(PlanBody planBody) {
		getHibernateTemplate().saveOrUpdate(planBody);
	}

	public void deleteByPlanId(final Long planId) {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				int i = session.createQuery(
						"delete PlanBody where plan.id = :id").setLong("id",
						planId).executeUpdate();
				return new Integer(i);
			}
		};
		getHibernateTemplate().execute(callback);
	}
}