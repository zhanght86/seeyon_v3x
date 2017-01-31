package com.seeyon.v3x.plan.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.plan.domain.PlanStyleBody;

/**
 * Data access object (DAO) for domain model class PlanStyleBody.
 * 
 * @see com.seeyon.v3x.plan.domain.PlanStyleBody
 * @author MyEclipse - Hibernate Tools
 */
public class PlanStyleBodyDao extends BaseHibernateDao<PlanStyleBody> {
	
	private transient static final Log LOG = LogFactory
	.getLog(PlanStyleBodyDao.class);
	
	public void fushSave(Object o){
		save(o);
		Session session = super.getSession();
		try{
			session.flush();
		}catch(Exception ex){
			LOG.error("", ex);;
		}finally{
			super.releaseSession(session);
		}
	}

	public List list() {
		return (List) getHibernateTemplate().find("from PlanStyleBody");
	}

	public void delete(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
	}

	public PlanStyleBody findByPrimaryKey(Long id) {
		return (PlanStyleBody) getHibernateTemplate().load(PlanStyleBody.class,
				new Long(id));
	}

	public void saveOrUpdate(PlanStyleBody planStyleBody) {
		getHibernateTemplate().saveOrUpdate(planStyleBody);
	}

	public void deleteByPlanStyleId(final Long planStyleId) {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				int i = session.createQuery(
						"delete PlanStyleBody where planStyle.id = :id")
						.setLong("id", planStyleId).executeUpdate();
				return new Integer(i);
			}
		};
		getHibernateTemplate().execute(callback);
	}
}