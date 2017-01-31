package com.seeyon.v3x.plan.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.plan.domain.PlanSummary;

/**
 * Data access object (DAO) for domain model class PlanSummary.
 * 
 * @see com.seeyon.v3x.plan.domain.PlanSummary
 * @author MyEclipse - Hibernate Tools
 */
public class PlanSummaryDao extends BaseHibernateDao<PlanSummary> {
	public void deleteByPlanId(final Long planId) {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				int i = session.createQuery(
						"delete PlanSummary where plan.id = :id").setLong("id",
						planId).executeUpdate();
				return new Integer(i);
			}
		};
		getHibernateTemplate().execute(callback);
	}
}