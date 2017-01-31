package com.seeyon.v3x.plan.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.plan.PlanRelevantUserType;
import com.seeyon.v3x.plan.domain.PlanStyle;

/**
 * Data access object (DAO) for domain model class PlanStyle.
 * 
 * @see com.seeyon.v3x.plan.domain.PlanStyle
 * @author MyEclipse - Hibernate Tools
 */
public class PlanStyleDao extends BaseHibernateDao<PlanStyle> {
	
	private transient static final Log LOG = LogFactory
	.getLog(PlanStyleDao.class);
	
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
	// private static final Log log = LogFactory.getLog(PlanStyleDAO.class);
	public PlanStyle findByPrimaryKey(Long id) {
		return (PlanStyle) getHibernateTemplate().load(PlanStyle.class,
				new Long(id));
	}
	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PlanStyle> find() {
		return (List<PlanStyle>) getHibernateTemplate().find("from PlanStyle");
	}

	public List list() {
		return (List) getHibernateTemplate().find("from PlanStyle");
	}

	public void delete(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
	}
	
	public List listPlanStyleByPage(){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Long accountId = CurrentUser.get().getLoginAccount();
		       String counthql = "select count(*) from PlanStyle ps where accountId=:accountId";
               Query countquery = session.createQuery(counthql).setLong("accountId", accountId);
	           int count = ((Integer) countquery.uniqueResult()).intValue();
	           Pagination.setRowCount(count);
	           StringBuffer hql = new StringBuffer();
	           hql.append("select distinct planStyle ");
	           hql.append(" from PlanStyle planStyle where accountId=:accountId");
	           Query query = session.createQuery(hql.toString()).setLong("accountId", accountId);
	           query.setFirstResult(Pagination.getFirstResult());
	           query.setMaxResults(Pagination.getMaxResults());
		       return query.list();
			}
		});		
	}
	
	public List listPlanStyleByType(final String type){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
	           StringBuffer hql = new StringBuffer();
	           hql.append("select distinct planStyle ");
	           hql.append(" from PlanStyle planStyle ");
	           hql.append(" where planStyle.type = :type");
	           Query query = session.createQuery(hql.toString());
			   query.setString("type", type);
		       return query.list();
			}
		});		
	}
	
	public List listPlanStyleByTypeAndAccount(final String type,final Long accountId){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
	           StringBuffer hql = new StringBuffer();
	           hql.append("select distinct planStyle ");
	           hql.append(" from PlanStyle planStyle ");
	           hql.append(" where planStyle.type = :type");
	           hql.append(" and planStyle.accountId = :accountId");
	           Query query = session.createQuery(hql.toString());
			   query.setString("type", type);
			   query.setString("accountId", String.valueOf(accountId));
		       return query.list();
			}
		});		
	}
	
	public List listPlanStyleByPageAndAccount(final Long accountId){
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
		       String counthql = "select count(*) from PlanStyle ps where accountId= :accountId";
               Query countquery = session.createQuery(counthql);
               countquery.setLong("accountId", accountId);
	           int count = ((Integer) countquery.uniqueResult()).intValue();
	           Pagination.setRowCount(count);
	           StringBuffer hql = new StringBuffer();
	           hql.append("select distinct planStyle ");
	           hql.append(" from PlanStyle planStyle where accountId=:accountId");
	           Query query = session.createQuery(hql.toString()).setLong("accountId", accountId);
	           query.setFirstResult(Pagination.getFirstResult());
	           query.setMaxResults(Pagination.getMaxResults());
		       return query.list();
			}
		});		
	}
}