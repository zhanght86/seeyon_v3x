package com.seeyon.v3x.plan.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.plan.domain.PlanReply;

/**
 * Data access object (DAO) for domain model class PlanReply.
 * 
 * @see com.seeyon.v3x.plan.domain.PlanReply
 * @author MyEclipse - Hibernate Tools
 */
public class PlanReplyDao extends BaseHibernateDao<PlanReply> {

	// private static final Log log = LogFactory.getLog(PlanReplyDAO.class);
	public void deleteByPlanId(final Long planId) {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				int i = session.createQuery(
						"delete PlanReply where plan.id = :id").setLong("id",
						planId).executeUpdate();
				return new Integer(i);
			}
		};
		getHibernateTemplate().execute(callback);
	}
	
	public PlanReply findByPrimaryKey(Long id){
		return (PlanReply) getHibernateTemplate().load(PlanReply.class, new Long(id));		
	}
    
    @SuppressWarnings("unchecked")
	public  List<PlanReply>  findreplyByPlanid(final Long id)
    {
        return (List<PlanReply>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                
                StringBuffer countHql = new StringBuffer();
                countHql.append(" from PlanReply p ");
                countHql.append(" where p.plan.id=:planId");
                
                Query query = session.createQuery(countHql.toString());
                query.setLong("planId", id);
                return query.list();
            }
        });
        
    }
}