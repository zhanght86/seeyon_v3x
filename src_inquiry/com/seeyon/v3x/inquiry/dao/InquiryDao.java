/**
 * 
 */
package com.seeyon.v3x.inquiry.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveydiscuss;

/**
 * @author lin tian
 * 
 * 2007-2-27
 */
public class InquiryDao extends BaseHibernateDao {
	/**
	 * 根据调查ID 调查问题ID获取调查列表
	 * @param pid
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveydiscuss> getDiscussList(final long pid, final long bid)
			throws Exception {
		return (List<InquirySurveydiscuss>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String hqlcount = "SELECT count(DISTINCT d) From "
				+ InquirySurveydiscuss.class.getName()
				+ " d,"
				+ InquirySurveybasic.class.getName()
				+ " b"
				+ " Where d.subsurveyId=:pid AND d.inquirySurveybasic.id=b.id AND b.id=:bid";
		Query countquery = session.createQuery(hqlcount).setLong(
				"pid", pid).setLong("bid", bid);
		int bcount = ((Integer) countquery.uniqueResult()).intValue();
		Pagination.setRowCount(bcount);

		String hql = "SELECT DISTINCT d From "
				+ InquirySurveydiscuss.class.getName()
				+ " d,"
				+ InquirySurveybasic.class.getName()
				+ " b"
				+ " Where d.subsurveyId=:pid AND d.inquirySurveybasic.id=b.id AND b.id=:bid";
		Query query = session.createQuery(hql).setLong("pid",
				pid).setLong("bid", bid).setFirstResult(
				Pagination.getFirstResult()).setMaxResults(
				Pagination.getMaxResults());
		return query.list();
			}
	});
	}
	
	/**
	 * 根据调查ID 调查问题ID获取调查列表---不分页
	 * @param pid
	 * @param bid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveydiscuss> getDiscussListNotPage(final long pid, final long bid)
			throws Exception {
		return (List<InquirySurveydiscuss>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "SELECT DISTINCT d From "
					+ InquirySurveydiscuss.class.getName()
					+ " d,"
					+ InquirySurveybasic.class.getName()
					+ " b"
					+ " Where d.subsurveyId=:pid AND d.inquirySurveybasic.id=b.id AND b.id=:bid";
				Query query = session.createQuery(hql).setLong("pid",
						pid).setLong("bid", bid);
				return query.list();
			}
	});
	}
	
	

}
