package com.seeyon.v3x.doc.manager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.dao.DocAclDao;
import com.seeyon.v3x.doc.dao.DocFavoriteDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocFavorite;
import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.Datetimes;

public class DocFavoriteManagerImpl implements DocFavoriteManager {
	private DocFavoriteDao docFavoriteDao;

	private DocResourceDao docResourceDao;

	private DocAclDao docAclDao;

	public DocAclDao getDocAclDao() {
		return docAclDao;
	}

	public void setDocAclDao(DocAclDao docAclDao) {
		this.docAclDao = docAclDao;
	}

	public DocResourceDao getDocResourceDao() {
		return docResourceDao;
	}

	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}

	public DocFavoriteDao getDocFavoriteDao() {
		return docFavoriteDao;
	}

	public void setDocFavoriteDao(DocFavoriteDao docFavoriteDao) {
		this.docFavoriteDao = docFavoriteDao;
	}

	public void deleteFavoriteDocByDoc(Long docResourceId, Long orgId,
			String orgType) {
		String hsql = "delete from DocFavorite as a where a.docResource=? and a.orgId=? and a.orgType=?";
		docFavoriteDao.bulkUpdate(hsql,null, docResourceId, orgId, orgType);
	}
	/**
	 * 根据文档id删除收藏
	 * 
	 */
	public void deleteFavoriteDocByDoc(DocResource dr) {
		List<DocResource> dlist = new ArrayList<DocResource>();
		dlist.add(dr);
		if(dr.getIsFolder()) {		
			String hsql = "from DocResource as a where a.logicalPath like :lp or a.id = :aid";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lp", dr.getLogicalPath() + ".%");
			map.put("aid", dr.getId());
			dlist = docResourceDao.find(hsql, -1, -1, map);
		}
		
		String in = "";
		for (int i = 0; i < dlist.size(); i++) {
			in = in + dlist.get(i).getId() + ",";
		}
	
		in = in.substring(0, in.length() - 1);
		Map<String, Object> namedParatmeter = new HashMap<String, Object>();
		namedParatmeter.put("in",Constants.parseStrings2Longs(in, ","));
		
		String hsql = "delete from DocFavorite as a where a.docResource.id in (:in)";
		docFavoriteDao.bulkUpdate(hsql, namedParatmeter);
		

//	String in = "";
//	for (int i = 0; i < dlist.size(); i++) {
//		in = in + dlist.get(i).getId() + ",";
//	}
//
//	in = in.substring(0, in.length() - 1);
//	
//	String hsql = "from DocFavorite as a where a.docResource in ("
//		+ in + ")";
//	docFavoriteDao.delete(hsql);
}
		



	public void deleteFavoriteDocById(Long id) {
		docFavoriteDao.delete(id.longValue());
	}
	/**
	 * 根据收藏id删除收藏
	 * 
	 * @param id
	 */
	public void deleteFavoriteDocByIds(String ids) {
		String hql = "delete from DocFavorite where id in (:ids)";
		
		Map<String, Object> namedParatmeter = new HashMap<String, Object>();
		namedParatmeter.put("ids", Constants.parseStrings2Longs(ids, ","));
		
		docFavoriteDao.bulkUpdate(hql, namedParatmeter);
	}
	
	/**
	 * 删除收藏
	 * 
	 * @param docResourceId
	 * @param siteId
	 * @param siteType
	 */
	public void deleteFavorites(String docResIds, Long orgId, String orgType) {
		String hql = "delete from DocFavorite where docResource in (ids) and orgId = :orgId and orgType = :orgType";
		
		Map<String, Object> namedParatmeter = new HashMap<String, Object>();
		namedParatmeter.put("ids", Constants.parseStrings2Longs(docResIds, ","));
		namedParatmeter.put("orgId", orgId);
		namedParatmeter.put("orgType", orgType);
		
		docFavoriteDao.bulkUpdate(hql, namedParatmeter);
	}
	
	public void setFavoriteDoc(long docResourceId, List<Long> orgIds, String orgType){
		List<DocFavorite> dfs = new ArrayList<DocFavorite>();
		Timestamp currentTime = new Timestamp(new Date().getTime());
		long currentUserId = CurrentUser.get().getId();
		
		List<DocFavorite> list = this.getDocFavoritesByOrgTypeAndDocId(orgType, docResourceId);
		Set<Long> ids = new HashSet<Long>();
		for(DocFavorite d : list){
			ids.add(d.getOrgId());
		} 
		
		for(Long uid : orgIds){
			if(ids.contains(uid))
				continue;
			
			DocFavorite df = new DocFavorite();
			df.setCreateTime(currentTime);
			df.setCreateUserId(currentUserId);
			DocResource dr = new DocResource();
			dr.setId(docResourceId);
			df.setDocResource(dr);
			df.setIdIfNew();
			df.setOrgId(uid);
			df.setOrgType(orgType);
			df.setOrderNum(docFavoriteDao.getOrderNum(uid, orgType));
			
			dfs.add(df);
		}
		
		if(dfs.size() > 0)
			docFavoriteDao.saveAll(dfs);	
	}
	public void setFavoriteDoc(List<Long> docResourceIds, List<Long> orgIds, String orgType){
		List<DocFavorite> dfs = new ArrayList<DocFavorite>();
		Timestamp currentTime = new Timestamp(new Date().getTime());
		long currentUserId = CurrentUser.get().getId();
		// Map<docId, List<DocLearning>>
		Map<Long, List<DocFavorite>> map = this.getDocFavoritesByOrgTypeAndDocIds(orgType, docResourceIds);
		Set<Long> keyset = map.keySet();
		// Map<docResId, Set<orgId>>
		Map<Long, Set<Long>> idsmap = new HashMap<Long, Set<Long>>();
		for(long docid : keyset){
			List<DocFavorite> list = map.get(docid);
			for(DocFavorite df : list){
				Set<Long> set = idsmap.get(docid);
				if(set == null){
					set = new HashSet<Long>();
					set.add(df.getOrgId());
					idsmap.put(docid, set);
				}else{
					set.add(df.getOrgId());
				}
			}			
		} 
		
		for(Long docid : docResourceIds){
			Set<Long> uids = idsmap.get(docid);
			for(Long uid : orgIds){
				if(uids != null)
					if(uids.contains(uid))
						continue;
				
				DocFavorite df = new DocFavorite();
				df.setCreateTime(currentTime);
				df.setCreateUserId(currentUserId);
				DocResource dr = new DocResource();
				dr.setId(docid);
				df.setDocResource(dr);
				df.setIdIfNew();
				df.setOrgId(uid);
				df.setOrgType(orgType);
				df.setOrderNum(docFavoriteDao.getOrderNum(uid, orgType));
				
				dfs.add(df);				
			}
		}
		
		if(dfs.size() > 0)
			docFavoriteDao.saveAll(dfs);
	}
	
	/**
	 * 
	 */
	public List<DocFavorite> getDocFavoritesByOrgTypeAndDocId(String orgType, long docResourceId){
		String hql = "from DocFavorite where docResource = ? and orgType = ? order by createTime desc";
		return docFavoriteDao.find(hql, new DocResource(docResourceId), orgType);
	}
	public Map<Long, List<DocFavorite>> getDocFavoritesByOrgTypeAndDocIds(String orgType, 
			List<Long> docResourceIds){
		Map<Long, List<DocFavorite>> ret = new HashMap<Long, List<DocFavorite>>();
		
		if(docResourceIds == null || docResourceIds.size() == 0)
			return ret;
			
		String docids = "";
		for(Long id : docResourceIds){
			docids += "," + id;
		}
		docids = docids.substring(1, docids.length());
		
		String hql = "from DocFavorite where orgType = :ot and docResource in(:ids)";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("ot", orgType);
		amap.put("ids", Constants.getDocsByIds(docids, ","));
		List<DocFavorite> list = docFavoriteDao.find(hql, -1, -1, amap);
		for(DocFavorite df : list){
			List<DocFavorite> dfl = ret.get(df.getDocResource().getId());
			if(dfl == null){
				dfl = new ArrayList<DocFavorite>();
				dfl.add(df);
				ret.put(df.getDocResource().getId(), dfl);
			}else{
				dfl.add(df);
			}
		}
		
		
		return ret;
	}
	public List<DocFavorite> getFavoritesByCount(final String orgType, final long orgId, final int count){
		List<DocFavorite> ret = (List<DocFavorite>)docFavoriteDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql = "from DocFavorite where orgId = :oid and orgType = :ot order by orderNum desc";
				return session.createQuery(hql).setLong("oid", orgId).setString("ot", orgType)
					.setFirstResult(0).setMaxResults(count).list();
			}
    	});
		
		

//		Session session = docFavoriteDao.getASession();
//        Query q = ;
//        docFavoriteDao.releaseTheSession(session);
		return ret;
	}
	
	
	public List getFavoritesByPage(final String orgType, final long orgId){
	List list = new ArrayList();
	if(orgType.equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT) && !CurrentUser.get().isInternal()){
		list.add(0);
		list.add(new ArrayList<DocFavorite>());
		return list;
	}	
	
	List<DocFavorite> ret = null;	
	List<DocFavorite> docFavs = null;	
	String hql="";
	int rowCount = 0;
	
	if(Constants.ORGENT_TYPE_GROUP.equals(orgType)){
		hql = "from DocFavorite where orgType =?";
		docFavs = docFavoriteDao.find(hql, orgType);
		rowCount = docFavs.size();
		 Pagination.setRowCount(rowCount);
		 list.add(rowCount);
		 
		 final String hqlf = "from DocFavorite where orgType =:orgType order by orderNum desc";

			ret = (List<DocFavorite>)docFavoriteDao.getHibernateTemplate().execute(new HibernateCallback(){
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					return session.createQuery(hqlf)
					.setString("orgType", orgType)
					.setFirstResult(Pagination.getFirstResult())
		    		.setMaxResults(Pagination.getMaxResults()).list();
				}
			});
			
			list.add(ret);
			
			return list;
	}else{
	
		hql = "from DocFavorite where orgType = ? and orgId = ?";
        docFavs = docFavoriteDao.find(hql, orgType, orgId);
        rowCount = docFavs.size();
        Pagination.setRowCount(rowCount);


        list.add(rowCount);
        
        final String hqlf = "from DocFavorite where orgType =:orgType and orgId =:orgId order by orderNum desc";

	     ret = (List<DocFavorite>)docFavoriteDao.getHibernateTemplate().execute(new HibernateCallback(){
		     public Object doInHibernate(Session session) throws HibernateException, SQLException {
			    return session.createQuery(hqlf)
			     .setString("orgType", orgType)
			     .setLong("orgId", orgId)
			     .setFirstResult(Pagination.getFirstResult())
    		     .setMaxResults(Pagination.getMaxResults()).list();
		       }
	        });
	
	     list.add(ret);
	
	     return list;
	    }
    }


	@SuppressWarnings("unchecked")
	public List getFavoritesByPage(final String orgType, final long orgId, final String type, final String value){
		List list = new ArrayList();
		if(orgType.equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT) && !CurrentUser.get().isInternal()){
			list.add(0);
			list.add(new ArrayList<DocFavorite>());
			return list;
		}	

		final List<Type> parameterTypes = new ArrayList<Type>();
		final List<Object> parameterValues = new ArrayList<Object>();
		List<DocFavorite> ret = null;	
		List<DocFavorite> docFavs = null;	
		String hql="";
		
		hql = "select docfavorite from DocFavorite docfavorite,DocResource docresource where docresource.id = docfavorite.docResource.id and docfavorite.orgType =?";
		parameterTypes.add(Hibernate.STRING);
		parameterValues.add(orgType);
		if (!Constants.ORGENT_TYPE_GROUP.equals(orgType)) {
			hql += " and docfavorite.orgId=? ";
			parameterTypes.add(Hibernate.LONG);
			parameterValues.add(orgId);
		} 
		if("name".equals(type)){
			if(value!=null && !"".equals(value)){ 
				hql += " and docresource.frName like ? ";
				parameterTypes.add(Hibernate.STRING);
				parameterValues.add("%"+value+"%");
			}
		}else if("category".equals(type)){
			if(value!=null && !"".equals(value)){
				hql += " and docresource.frType = ? ";
				parameterTypes.add(Hibernate.LONG);
				parameterValues.add(Long.valueOf(value));
			}
		}else if("keywords".equals(type)){
			if(value!=null && !"".equals(value)){
				hql += " and docresource.keyWords like ? ";
				parameterTypes.add(Hibernate.STRING);
				parameterValues.add("%"+value+"%");
			}
		}else if("creator".equals(type)){
			if(value!=null && !"".equals(value)){
				hql += " and docresource.createUserId = ? ";
				parameterTypes.add(Hibernate.LONG);
				parameterValues.add(Long.valueOf(value)); 
			}
		}else if("createDate".equals(type)){
			if(value!=null && !"#".equals(value)){
				String[] arr = value.split("#"); 
				if(!"".equals(arr[0].trim())){
					hql += " and docresource.lastUpdate >= ? ";
					parameterTypes.add(Hibernate.TIMESTAMP);
					parameterValues.add(Datetimes.parse(arr[0].trim()));
				} 
				if(!"".equals(arr[1].trim())){
					hql += " and docresource.lastUpdate <= ? ";
					parameterTypes.add(Hibernate.TIMESTAMP);
					parameterValues.add(Datetimes.parse(arr[1].trim()));
				}
			} 
		}

		int rowCount = docFavoriteDao.getQueryCount(hql.substring(hql.indexOf("from")), 
				                                    parameterValues.toArray(new Object[parameterValues.size()]),
													parameterTypes.toArray(new Type[parameterTypes.size()]));
		Pagination.setRowCount(rowCount);
		list.add(rowCount);

		final String hqlf = hql + " order by docfavorite.orderNum desc";
		ret = (List<DocFavorite>)docFavoriteDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hqlf)
				.setFirstResult(Pagination.getFirstResult())
	    		.setMaxResults(Pagination.getMaxResults())
				.setParameters(parameterValues.toArray(new Object[parameterValues.size()]),
						   parameterTypes.toArray(new Type[parameterTypes.size()])).list();
			}
		});
		
		list.add(ret);
	    return list;
    }

	public List<DocFavorite> findFavoriteByOrg(Long orgId, String orgType) {
		String hsql = "from DocFavorite as a where a.orgId=? and a.orgType=? order by a.createTime desc";
		return docFavoriteDao.find(hsql, orgId, orgType);
	}
	/**
	 * 根据首页id和类型查找收藏对象列表
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<DocFavorite> findFavoriteByOrgByCount(Long orgId, String orgType, int count) {
		List<DocFavorite> ret = new ArrayList<DocFavorite>();
		List<DocFavorite> list = this.findFavoriteByOrg(orgId, orgType);
		if(list == null || list.size() == 0)
			return ret;
		for(int i = 0; i < count; i++) {
			if(i == list.size())
				break;
			ret.add(list.get(i));
		}
		return ret;
	}

	public DocFavorite getDocFavoriteById(Long id) {
		return docFavoriteDao.get(id);
	}

	public void updateDocFavoriteOrderDown(Long id, Long nextId) {
		DocFavorite dfavorite = docFavoriteDao.get(id);
		dfavorite.setOrderNum(dfavorite.getOrderNum() - 1);
		docFavoriteDao.update(dfavorite);
		DocFavorite dfavorite2 = docFavoriteDao.get(nextId);
		dfavorite2.setOrderNum(dfavorite2.getOrderNum() + 1);
		docFavoriteDao.update(dfavorite2);

	}

	public void updateDocFavoriteOrderUp(Long id, Long prevId) {
		DocFavorite dfavorite = docFavoriteDao.get(id);
		dfavorite.setOrderNum(dfavorite.getOrderNum() + 1);
		docFavoriteDao.update(dfavorite);
		DocFavorite dfavorite2 = docFavoriteDao.get(prevId);
		dfavorite2.setOrderNum(dfavorite2.getOrderNum() - 1);
		docFavoriteDao.update(dfavorite2);

	}

//	public List<DocFavorite> findFavoriteCommonDocsByOrg(Long orgId,
//			String orgType, String userIds) {
//		List<DocFavorite> list = new ArrayList<DocFavorite>();
//		String hsql = "from DocFavorite as b where b.orgId=? " +
//				"and b.orgType=? order by b.orderNum desc";
//		List<DocFavorite> flist = docFavoriteDao.find(hsql, orgId, orgType);
//
//			for (int i = 0; i < flist.size(); i++) {
//				if (docAclDao.showInTable(flist.get(i).getDocResource(), userIds)) {
//					list.add(flist.get(i));
//				}
//			}
//
//		return list;
//	}

//	/**
//	 * 根据首页id和首页类型查找公共文档对象列表
//	 * 
//	 * @param siteId
//	 * @param siteType
//	 * @return
//	 */
//	public List<DocFavorite> findFavoriteCommonDocsByOrgByCount(Long orgId,
//			String orgType, String userIds, int count) {
//		List<DocFavorite> ret = new ArrayList<DocFavorite>();
//		List<DocFavorite> list = this.findFavoriteCommonDocsByOrg(orgId, orgType, userIds);
//		if(list != null && list.size() > 0)
//			ret.addAll(list.subList(0, count < list.size() ? count : list.size()));
//		return ret;
//	}

	public List<DocFavorite> findFavoritePersonalDocsByOrg(Long userId) {
		String hsql = "from DocFavorite as b where b.orgId=? " +
				"and b.orgType=? order by b.orderNum desc";
		List<DocFavorite> flist = docFavoriteDao.find(hsql, userId, V3xOrgEntity.ORGENT_TYPE_MEMBER);

		return flist;
	}

	/**
	 * 根据用户id查找个人首页文档对象列表
	 * 
	 * @param siteId
	 * @param siteType
	 * @return
	 */
	public List<DocFavorite> findFavoritePersonalDocsByOrgByCount(Long userId, int count) {
		List<DocFavorite> ret = new ArrayList<DocFavorite>();
		List<DocFavorite> list = this.findFavoritePersonalDocsByOrg(userId);
		if(list != null && list.size() > 0)
			ret.addAll(list.subList(0, count < list.size() ? count : list.size()));
		return ret;
	}
	
	public boolean isFavorite(long docId){
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		String hql = "from DocFavorite where docResource.id = :id and orgId in (:orgs)";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("orgs", Constants.parseStrings2Longs(orgIds, ","));
		namedParameters.put("id", docId);
		List<DocFavorite> list =  docFavoriteDao.find(hql, namedParameters);
		if(list.size()>0) return true;
		else return false;		
	}
//	/**
//	 * 分页查找收藏
//	 */
//	public List<DocFavorite> pagedFindFavorite(Long orgId, String orgType, String aclIds) throws Exception{
//		List<DocFavorite> list = null;
//		List<DocFavorite> ret = new ArrayList<DocFavorite>();
//		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)){
//			list = this.findFavoritePersonalDocsByOrg(orgId);
//		}else {
//			list = this.findFavoriteCommonDocsByOrg(orgId, orgType, aclIds);
//		} 
//		if(list == null)
//			return ret;
//
//		int first = Pagination.getFirstResult();
//		int pageSize = Pagination.getMaxResults();
//		int end1 = first + pageSize;
//		int end2 = list.size();
//		int end = 0;
//		if (end1 > end2)
//			end = end2;
//		else
//			end = end1;
//		for(int i = first; i < end;  i++) {
//			ret.add(list.get(i));
//		}		
//		
//		return ret;
//	}
	
//	public int findFavoriteTotal(Long orgId, String orgType, String aclIds) throws Exception{
//		List list = null;
//		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)){
//			list = this.findFavoritePersonalDocsByOrg(orgId);
//		}else {
//
//			list = this.findFavoriteCommonDocsByOrg(orgId, orgType, aclIds);
//		} 
//		if(list == null)
//			return 0;
//		else
//			return list.size();
//	}


}
