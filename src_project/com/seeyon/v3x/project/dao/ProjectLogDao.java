/**
 * 
 */
package com.seeyon.v3x.project.dao;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.project.domain.ProjectLog;

/**
 * @author tian lin
 * 
 */
public class ProjectLogDao extends BaseHibernateDao<ProjectLog> {
	/**
	 * 根据项目ID获取操作日志列表
	 * 
	 * @param project_id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectLog> getLogList( final long project_id) throws Exception {
		return (List<ProjectLog>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
		String count ="SELECT Count(DISTINCT log) FROM " + ProjectLog.class.getName() + " log"
		            + " WHERE log.projectId = :projectId";
		Query queryCount = session.createQuery(count).setLong("projectId",project_id);
		int projectCount = ((Integer) queryCount.uniqueResult()).intValue();
	    Pagination.setRowCount(projectCount);
		String hql = "FROM " + ProjectLog.class.getName() + " log"
				+ " WHERE log.projectId = :projectId ORDER BY log.optionDate DESC";
		Query query = session.createQuery(hql).setLong("projectId",
				project_id).setMaxResults(Pagination.getMaxResults()).setFirstResult(Pagination.getFirstResult());
		return query.list();
			}
		});
	}
}
