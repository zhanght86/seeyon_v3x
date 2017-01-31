package com.seeyon.v3x.doc.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocStorageSpace;

public class DocSpaceDao extends BaseHibernateDao<DocStorageSpace> {
//	public Session getASession() {
//		return super.getSession();
//	}
//	public void releaseTheSession(Session session){
//		super.releaseSession(session);
//	}
	
//	/**
//	 * 
//	 */
//	public List<DocStorageSpace> findByDeptId(final long deptId) {
////		String hsql = "select a from DocStorageSpace a,V3xOrgMember b "
////			+ "where a.userId=b.id and b.orgDepartmentId=? order by b.code asc";		
////		return super.find(hsql, deptId);
//		
//        if (Pagination.isNeedCount()) {
//    		String hql2 = "from DocStorageSpace a,V3xOrgMember b "
//    			+ "where a.userId=b.id and b.orgDepartmentId="+ deptId +" order by b.code asc";	
//            int rowCount = super.getQueryCount(hql2, null, null);
//            Pagination.setRowCount(rowCount);
//        }
//        
////        Session session = docSpaceDao.getASession();
////        Query q = session.createQuery(hql).setFirstResult(Pagination.getFirstResult())
////        		.setMaxResults(Pagination.getMaxResults());
//        List<DocStorageSpace> ret = (List<DocStorageSpace>)super.getHibernateTemplate().execute(new HibernateCallback(){
//			public Object doInHibernate(Session session) throws HibernateException, SQLException {
//	    		String hql = "select a from DocStorageSpace a,V3xOrgMember b "
//	    			+ "where a.userId=b.id and b.orgDepartmentId="+ deptId +" order by b.code asc";	
//				return (List<DocStorageSpace>)session.createQuery(hql).setFirstResult(Pagination.getFirstResult())
//        		.setMaxResults(Pagination.getMaxResults()).list();
//			}
//    	});
//        
//        
//		return ret;
//	}

}
