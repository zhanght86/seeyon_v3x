package com.seeyon.v3x.doc.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.dao.DocAlertDao;
import com.seeyon.v3x.doc.dao.DocAlertLatestDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocAlert;
import com.seeyon.v3x.doc.domain.DocAlertLatest;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

public class DocAlertManagerImpl implements DocAlertManager {
	private static final Log log = LogFactory.getLog(DocAlertManagerImpl.class);
	
	private DocAlertDao docAlertDao;
	private DocResourceDao docResourceDao;
	private DocAlertLatestDao docAlertLatestDao;
	private OrgManager orgManager;
	private DocAclManager docAclManager;

	public DocAclManager getDocAclManager() {
		return docAclManager;
	}

	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public DocAlertLatestDao getDocAlertLatestDao() {
		return docAlertLatestDao;
	}

	public void setDocAlertLatestDao(DocAlertLatestDao docAlertLatestDao) {
		this.docAlertLatestDao = docAlertLatestDao;
	}

	public DocAlertDao getDocAlertDao() {
		return docAlertDao;
	}

	public void setDocAlertDao(DocAlertDao docAlertDao) {
		this.docAlertDao = docAlertDao;
	}
	
	/**
	 * 订阅文档
	 * @param alertOprType    订阅操作类型 Constants.SUBSCRIBE_OPR_TYPE_XXX
	 * @param alertUserType  	订阅用户类型 Constants.SUBSCRIBE_USER_TYPE_XXX
	 * @param alertUserId		订阅用户id
	 * @param createUserId	创建订阅的用户id
	 * @param docAclId		个人共享的doc_acl记录id，非个人共享时直接传null
	 */
	public long addAlert(Long docResourceId, boolean isFolder, byte alertOprType, 
			String alertUserType, Long alertUserId, Long createUserId, boolean sendMessage, boolean setSubFolder,boolean isFromAcl)  {
		DocAlert alert = new DocAlert();
		alert.setIdIfNew();
		alert.setAlertUserId(alertUserId);
		alert.setAlertUserType(alertUserType);
		alert.setChangeType(alertOprType);
		alert.setCreateTime(new Timestamp(new Date().getTime()));
		alert.setCreateUserId(createUserId);
//		if(docAclId != null)
//			alert.setDocAclId(docAclId);
		alert.setDocResourceId(docResourceId);
		alert.setIsFolder(isFolder);
		alert.setLastUpdate(new Timestamp(new Date().getTime()));
		alert.setLastUserId(createUserId);
		alert.setSendMessage(sendMessage);
		alert.setSetSubFolder(setSubFolder);
		alert.setIsFromAcl(isFromAcl);
		
		docAlertDao.save(alert);		
		return alert.getId();
	}
	
	/**
	 * 更新文档订阅到最新列表
	 */
	public void addToLatest(Long docResourceId, Long alertUserId){
		// 文档订阅时同步到最新列表
		
		DocResource dr = this.docResourceDao.get(docResourceId);
		DocAlertLatest dal = new DocAlertLatest();
		dal.setIdIfNew();
		dal.setAlertUserId(alertUserId);
		dal.setChangeType(Constants.ALERT_OPR_TYPE_ADD);
		dal.setDocResourceId(docResourceId);
		dal.setDocResourceName(dr.getFrName());
		dal.setLastUpdate(dr.getCreateTime());
		dal.setLastUserId(dr.getCreateUserId());
		dal.setMimeTypeId(dr.getMimeTypeId());
		
		docAlertLatestDao.save(dal);
		
	}

	/**
	 * 修改文档订阅类型
	 */
	public void updateAlertOprType(Long alertId, byte newAlertOprType, boolean sendMessage) throws DocException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("changeType", newAlertOprType);
		data.put("sendMessage", sendMessage);
		docAlertDao.update(alertId, data);
	}
	
	public void deleteAlertByDrIdFromAcl(Long docResId) {
		this.docAlertDao.bulkUpdate("delete from " + DocAlert.class.getCanonicalName() + " where docResourceId=? and isFromAcl=true", null, docResId);
	}
	
	/**
	 * 根据文档订阅id删除订阅
	 */
	public void deleteAlertById(Long alertId) {
		docAlertDao.delete(alertId.longValue());
		// 清除权限表里的记录
		docAclManager.cancelAlertByAlertIds(alertId.toString());
	}
	public void deleteAlertsByIds(String alertIds)  {
		if(Strings.isBlank(alertIds))
			return;
		
		Set<Long> alertIds1 = Constants.parseStrings2Longs(alertIds, ",");
		
		// 删除对应的最新订阅记录
		String hqla = "delete from DocAlertLatest where docResourceId in (select docResourceId from DocAlert where id in(:aids)) and alertUserId = :uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", CurrentUser.get().getId());
		map.put("aids", alertIds1);
		docAlertLatestDao.bulkUpdate(hqla, map);
		
		String hql = "delete from DocAlert where id in (:alertIds) ";
		
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("alertIds", alertIds1);
		
		docAlertDao.bulkUpdate(hql, nameParameters);
		
		// 清除权限表里的记录
		docAclManager.cancelAlertByAlertIds(alertIds);
	}
	
	/**
	 * 根据DocResourceId删除该文档所有订阅
	 * 如果是文档夹，则删除它下面所有内容的订阅，包括自己
	 */
	public void deleteAlertByDocResourceId(DocResource dr) {
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("path", dr.getLogicalPath() + "%");
		
		String hql = "delete from DocAlert as a where a.docResourceId in (select id from DocResource where logicalPath like :path)";
		docAlertDao.bulkUpdate(hql, nameParameters);
	}

	private List<Long> getAllIdsByLogicalPath(DocResource dr) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(dr.getId());
		// 处理文档夹的订阅
		if(dr.getIsFolder()) {
			String hql2 = "select id from DocResource where logicalPath like ?";
			List<Long> list = docResourceDao.find(hql2, -1, -1, null, dr.getLogicalPath() + ".%");
			FormBizConfigUtils.addAllIgnoreEmpty(ids, list);
		}
		return ids;
	}
	
	public void deleteProjectFolderAlert(DocResource projectFolder, List<Long> oldProjectMemberIds) {
		
		String hql = "delete from DocAlert as a where a.docResourceId in (select id from DocResource where logicalPath like :path)" +
				" and a.alertUserId in (:userIds) and a.alertUserType=:member";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("path", projectFolder.getLogicalPath() + ".%");
		nameParameters.put("userIds", oldProjectMemberIds);
		nameParameters.put("member", V3xOrgEntity.ORGENT_TYPE_MEMBER);
		docAlertDao.bulkUpdate(hql, nameParameters);
	}
	
	public void deleteAlertByDocResourceIdOfCurrentUesr(long docResId){
		String hql = "from DocAlert where docResourceId = ? and alertUserId = ?";
		
		List<DocAlert> list = docAlertDao.find(hql, docResId, CurrentUser.get().getId());
	
		
		if(list != null && list.size() > 0){
			String ids = "";
			for(DocAlert da : list){
				if(!da.getIsFromAcl())
					break;
				ids += "," + da.getId();					
			}
			if(ids.length() > 0)
				docAclManager.cancelAlertByAlertIds(ids.substring(1, ids.length()));			
	            
			String hql2 = "delete from DocAlert where docResourceId =? and alertUserId = ?";
			docAlertDao.bulkUpdate(hql2, null, docResId, CurrentUser.get().getId());
		}
	}
	public void deleteAllAlertByDocResourceIdAndOrg(DocResource doc, String orgType, long orgId){
		String hql = "delete from DocAlert where docResourceId in(select doc.id from DocResource doc where doc.id = :id  or (doc.logicalPath like :lp )) and alertUserId = :uid and alertUserType = :te";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", doc.getId());
		map.put("lp", "%"+doc.getId()+"%");		
		map.put("uid",orgId);
		map.put("te",orgType);
		docAlertDao.bulkUpdate(hql,map);
	}
	
	public boolean  hasAlert(DocResource doc, long userId){
		String hql = "from DocAlert where docResourceId =:did and alertUserId = :uid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("did", doc.getId());
		map.put("uid",userId);
		 List<DocAlert> alert = docAlertDao.find(hql, -1,-1,map);
		 if(alert.size()>0) return true;
		 else return false;
		
		
	}
	public void deleteAlertByDocResourceIdAndOrg(long docResId, String orgType, long orgId){
		String hql = "delete from DocAlert where docResourceId = ? and alertUserId = ? and alertUserType = ?";
		docAlertDao.bulkUpdate(hql, null, docResId, orgId, orgType);
	}
	public void deleteAlertByDocResourceIdAndOrgByAcl(long docResId, String orgType, long orgId){
		String hql = "delete from DocAlert where docResourceId = ? and alertUserId = ? and alertUserType = ? and isFromAcl = " + Constants.IS_FROM_ACL;
		docAlertDao.bulkUpdate(hql, null, docResId, orgId, orgType);
	}
	
	public void deleteAlertByDocResourceIdAndOrgByAclForBatch(List<Long> lists, String orgType, long orgId){
		StringBuffer hql = new StringBuffer("delete from DocAlert where  " +
				" alertUserId =:alertUserId and alertUserType =:alertUserType and isFromAcl = "+Constants.IS_FROM_ACL+" and docResourceId ");
		Map<String, Object> param = new HashMap<String, Object>();
		int len = lists.size();
		// in中的条件每次最多900
		int count = 900;
		int size = len % count;
		if(size == 0) {
			size = len / count;
		} else {
			size = (len / count) + 1;
		}
		for(int i = 0; i < size; i++) {
			int fromIndex = i * count;
			int toIndex = Math.min(fromIndex + count, len);
			hql.append("in (:ids) ");
			param.put("ids", lists.subList(fromIndex, toIndex));
			param.put("alertUserId", orgId);
			param.put("alertUserType", orgType);
			docAlertDao.bulkUpdate(hql.toString(), param);
		}
	}
	public void deleteAlertByDocResourceIdAndAlertType(long docResId ){
		String hql = "delete from DocAlert where docResourceId = ? and changeType = " + Constants.ALERT_OPR_TYPE_FORUM ;
		docAlertDao.bulkUpdate(hql, null, docResId);
	}
	
	/**
	 * 根据DocResourceId删除该文档所有个人共享产生的订阅
	 */
	public void deleteShareAlertByDocResourceId(Long docResourceId) {
		DocResource dr = docResourceDao.get(docResourceId);
		String hql = "delete from DocAlertLatest as a where a.docResourceId in (select id from DocResource where logicalPath like :path)";
		
		
		
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("path", dr.getLogicalPath() + ".%");
		
		docAlertLatestDao.bulkUpdate(hql, nameParameters);
		
		String hql2 = "delete from DocAlert as a where a.docResourceId = ? and a.isFromAcl = true";
		docAlertDao.bulkUpdate(hql2, null, docResourceId);
	}
	
	/**
	 * 根据DocAlertId查询文档订阅 
	 */
	public DocAlert findAlertById(Long alertId) {
		return docAlertDao.get(alertId);
	}
	public List<DocAlert> findAlertsByIds(String alertIds) {
		String hql = "from DocAlert where id in (:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", Constants.parseStrings2Longs(alertIds, ","));
		return docAlertDao.find(hql, -1, -1, map);
	}
	public boolean hasAlert(String alertIds) {
		String hql = "from DocAlert where id in (:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", Constants.parseStrings2Longs(alertIds, ","));
		if( docAlertDao.find(hql, -1, -1, map).size()>0) 
			return true;
		else
			return false;
	}
	/**
	 * 根据docResourceId, userId查询文档订阅 
	 */
	public List<DocAlert> findPersonalAlertByDrIdOfCurrentUser(Long docResId){
		String hql = "from DocAlert where docResourceId = ? and alertUserId = ?";
		List<DocAlert> list = docAlertDao.find(hql, docResId, CurrentUser.get().getId());
		return list;
	}
	/**
	 * 查询当前用户的所有订阅，包含部门订阅
	 * @return Map<组织模型id， List<DocAlert>>
	 * @throws DocException 
	 */
	public Map<Long, List<DocAlert>> findAllAlertsByDrIdOfCurrentUser(Long docResId) throws DocException{
		Map<Long, List<DocAlert>> ret = new HashMap<Long, List<DocAlert>>();
		
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		String hql = "from DocAlert where docResourceId = :docid and alertUserId in (:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("docid", docResId);
		map.put("ids", Constants.parseStrings2Longs(orgIds, ","));
		List<DocAlert> list = docAlertDao.find(hql, -1, -1, map);
		
		for(DocAlert da : list){
			List<DocAlert> teamList = ret.get(da.getAlertUserId());
			if(teamList != null){
				teamList.add(da);
			}else{
				teamList = new ArrayList<DocAlert>();
				teamList.add(da);
				ret.put(da.getAlertUserId(), teamList);
			}
		}
		
		return ret;
	}
	
	/**
	 * 根据DocResourceId查询文档订阅
	 * 当alertOprTypes不传时，默认为查询所有类型的订阅
	 * 
	 * 修改：逐级查询订阅信息 2014-03-20
	 */
	@SuppressWarnings("unchecked")
	public List<DocAlert> findAlertsByDocResourceId(DocResource dr, Byte... alertOprTypes) {
		List<Byte> types = FormBizConfigUtils.parseArr2List(alertOprTypes);
		if(CollectionUtils.isNotEmpty(types)) {
			types.add(Constants.ALERT_OPR_TYPE_ALL);
		}
		else {
			types = FormBizConfigUtils.newArrayList(Constants.ALERT_OPR_TYPE_ALL);
		}
		Long docResourceId = this.getDrId4Alert(dr.getLogicalPath(), types);
		return findByDocResourceIdAndType(docResourceId,types);
	}
	
	/**
	 *根据docResourceId查询DocAlert
	 */
	private List<DocAlert> findByDocResourceIdAndType(Long docResourceId , List<Byte> types){
		List<DocAlert> docAlerts = new ArrayList<DocAlert>();
        if (docResourceId != null) {
    		String hql = "from DocAlert where docResourceId =:drId and changeType in (:types)";
    		List<Long> drIds = FormBizConfigUtils.parseStr2Ids(docResourceDao.get(docResourceId).getLogicalPath(), "[.]");
    		if(CollectionUtils.isNotEmpty(drIds)){
    			Collections.reverse(drIds);
    			for(Long l:drIds){
    				Set<Long> users = new HashSet<Long>();
    				for(DocAlert da0 : docAlerts){
    					users.add(da0.getAlertUserId());
    				}
    				Map<String, Object> nameParameters = new HashMap<String, Object>();
    				nameParameters.put("types", types);
    				nameParameters.put("drId", l);
    				List<DocAlert> tempDocAlerts = docAlertDao.find(hql, -1, -1, nameParameters);
    				for(DocAlert da1 : tempDocAlerts){
    					if(!users.contains(da1.getAlertUserId())){
    						docAlerts.add(da1);
    					}
    				}
    			}
    		}
		}
		return docAlerts;
	}
	
	/**
	 * 获取订阅记录，如果当前文档(夹)无订阅记录，则向上追溯通过继承而来的订阅记录
	 * @param logicalPath	逻辑路径
	 * @param alertOprTypes	订阅操作类型
	 * @return	作为订阅记录依据的文档(夹)ID
	 */
	@SuppressWarnings("unchecked")
	private Long getDrId4Alert(String logicalPath, List<Byte> alertOprTypes) {
//		String hql = "select docResourceId, count(1) from DocAlert where docResourceId in (:drIds) and changeType in (:types) group by docResourceId";
		String hql = "select docResourceId, changeType from DocAlert where docResourceId in (:drIds)";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
//		nameParameters.put("types", alertOprTypes);
		List<Long> drIds = FormBizConfigUtils.parseStr2Ids(logicalPath, "[.]");
//		if (drIds.size() > 2) {
//			drIds.remove(0);
//		}
		nameParameters.put("drIds", drIds);
		List<Object[]> result = this.docAlertDao.find(hql, -1, -1, nameParameters);
		
		Long ret = null;
		if(CollectionUtils.isNotEmpty(result)) {
			Map<Long, String> map = new HashMap<Long, String>();
			for(Object[] arr : result) {
				Long key = (Long)arr[0];
				if(map.containsKey(key)){
					map.put(key,map.get(key)+((Byte)arr[1]).toString());
				}else{
					map.put(key, ((Byte)arr[1]).toString());
				}
			}
			
			Collections.reverse(drIds);
			for(Long drId : drIds) {
				if(map.get(drId) != null ){
					if(map.get(drId).indexOf("0")!=-1){
						ret = drId;
					}else{
						for(Byte b: alertOprTypes){
							if(map.get(drId).indexOf(String.valueOf(b))!=-1){
								ret = drId;
								break;
							}
						}
					}
					break;
				}
			}
		}
		if(log.isDebugEnabled()) {
			log.debug(ret == null ? "哥，翻遍祖宗十八代都找不到订阅记录，洗洗睡吧..." : "哥，找到订阅记录了!年谱中，这位爷的身份证号是[" + ret + "]");
		}
		return ret;
	}
	

	public DocAlert getAlertByDocIdAndOrgOfShare(long docResourceId, String orgType, long orgId){
		String hql = "from DocAlert where alertUserType = ? and alertUserId = ? and docResourceId = ? and isFromAcl = true";
		List<DocAlert> list = docAlertDao.find(hql, orgType, orgId, docResourceId);
		if(list == null || list.size() == 0)
			return null;
		else
			return list.get(0);
	}
	
	/**
	 * 查询用户的所有订阅
	 * 当alertOprTypes不传时，默认为查询所有类型的订阅
	 */
	public List<DocAlert> findAlertsByUserId(String userType, Long userId, byte... alertOprTypes) throws DocException {
		String hql = "from DocAlert as a where a.alertUserType = ? and a.alertUserId = ? and changeType in (:types)";
		List<Byte> types = new ArrayList<Byte>();
		if(alertOprTypes != null){
			if(alertOprTypes.length > 0) {
				for(byte b:alertOprTypes) {
					types.add(b);
				}			
				types.add(Constants.ALERT_OPR_TYPE_ALL);
			}
		}
		
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		nameParameters.put("types", types);
		
		return docAlertDao.find(hql, nameParameters, userType, userId);
	}
	
	public List<DocAlert> findAlertsByUserIdAndDocResId(String userType,
			Long userId, Long docResId) throws DocException {
		List<DocAlert> ret = null;
		
		String hql = "from DocAlert where alertUserType = ? and alertUserId = ? and docResourceId = ?";
		ret = docAlertDao.find(hql, userType, userId, docResId);
		
		return ret;
	}
	
//	public List<DocAlert> findAlertsByUserIdByPage(String userType, Long userId, 
//			byte... alertOprTypes) throws DocException {
//		List<DocAlert> ret = null;
//		String hql = "from DocAlert as a where a.alertUserType = " + userType + " and a.alertUserId = " + userId;
//		String types = "";
//		if(alertOprTypes != null){
//			if(alertOprTypes.length > 0) {
//				for(byte b:alertOprTypes) {
//					types += "," + b;
//				}			
//				types += "," + Constants.ALERT_OPR_TYPE_ALL;
//				hql += " and changeType in (" + types.substring(1, types.length()) + ")";
//			}
//		}
//		final String fhql = hql;
//        if (Pagination.isNeedCount()) {
//            int rowCount = docAlertDao.getQueryCount(hql, null, null);
//            Pagination.setRowCount(rowCount);
//        }
//        
////        Session session = docAlertDao.getASession();
////        Query q = session.createQuery(hql).setFirstResult(Pagination.getFirstResult())
////        		.setMaxResults(Pagination.getMaxResults());
////       	
////		ret = q.list();
////		docAlertDao.releaseTheSession(session);
//		
//		ret = (List<DocAlert>)docAlertDao.getHibernateTemplate().execute(new HibernateCallback(){
//			public Object doInHibernate(Session session) throws HibernateException, SQLException {
//				return (List<DocAlert>)session.createQuery(fhql).setFirstResult(Pagination.getFirstResult())
//        			.setMaxResults(Pagination.getMaxResults()).list();
//			}
//    	});
//		
//		return ret;
//	}
	
	@SuppressWarnings("unchecked")
	public List<List<DocAlert>> findAllAlertsOfCurrentUserByPage() {
		Map<Long, Map<Long, List<DocAlert>>> ret = new HashMap<Long, Map<Long, List<DocAlert>>>();
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		String hql = "from DocAlert where alertUserId in (:ids)";
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(orgIds, ","));

		List<DocAlert> list = docAlertDao.find(hql, -1, -1, nmap);
		for (DocAlert da : list) {
			Map<Long, List<DocAlert>> map = ret.get(da.getDocResourceId());
			if (map != null) {
				List<DocAlert> dalist = map.get(da.getAlertUserId());
				if (dalist != null) {
					dalist.add(da);
				} else {
					dalist = new ArrayList<DocAlert>();
					dalist.add(da);
					map.put(da.getAlertUserId(), dalist);
					ret.put(da.getDocResourceId(), map);
				}
			} else {
				map = new HashMap<Long, List<DocAlert>>();
				List<DocAlert> dalist = new ArrayList<DocAlert>();
				dalist.add(da);
				map.put(da.getAlertUserId(), dalist);
				ret.put(da.getDocResourceId(), map);
			}
		}

		List<List<DocAlert>> groupedList = new ArrayList<List<DocAlert>>();

		for (Long docid : ret.keySet()) {
			Map<Long, List<DocAlert>> docAlertMap = ret.get(docid);
			for (Long orgid : docAlertMap.keySet()) {
				groupedList.add(docAlertMap.get(orgid));
			}
		}

		Pagination.setRowCount(groupedList.size());
		int first = Pagination.getFirstResult();
		int pageSize = Pagination.getMaxResults();
		int end1 = first + pageSize;
		int end2 = groupedList.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		
		return groupedList.subList(first, end);
	}

	
	/** 
	 * 找到所有需要消息提醒的用户
	 * @throws DocException 
	 */
	public List<DocAlert> getMsgAlert(Long docResourceId) throws DocException {
		DocResource dr = docResourceDao.get(docResourceId);
		String lp = dr.getLogicalPath();
		String[] ss = lp.split("\\.");
		String paths = "";
		for(String s : ss) {
			paths += "," + s;
		}
		String hql = "from DocAlert as a where a.sendMessage = true and a.docResourceId in (:ids)";
		Map<String, Object> nmap = new HashMap<String, Object>();
		nmap.put("ids", Constants.parseStrings2Longs(paths.substring(1, paths.length()), ","));
		return docAlertDao.find(hql, -1, -1, nmap);
	}

	public DocResourceDao getDocResourceDao() {
		return docResourceDao;
	}

	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}
	
	/**
	 * 
	 * 
	 */
//	public void init(){
//		// 清除没有权限的订阅
//		String hql = "from DocAlert where isFromAcl = true";
//		List<DocAlert> list = this.docAlertDao.find(hql);
//		if(list != null && list.size() > 0){
//			List<Long> ids = new ArrayList<Long>();
//			Map<Long, DocAlert> map = new HashMap<Long, DocAlert>();
//			for(DocAlert da : list){
//				ids.add(da.getId());
//				map.put(da.getId(), da);
//			}
//			List<DocAcl> acls = this.docAclManager.getAclListByAlertId(ids);
//			if(acls == null || acls.size() == 0){
////				Map<String, Object> nameParameters = new HashMap<String, Object>();
////				nameParameters.put("ids", ids);
//				this.docAlertDao.update("delete from DocAlert where id in ( select id from DocAlert ale where ale.isFromAcl = true)");
//			}else{
//				for(DocAcl da : acls){
//					map.remove(da.getDocAlertId());
//				}
//				if(map.size() > 0){
//					// 没有权限的订阅
//					for(DocAlert da : map.values()){
//						this.docAlertDao.delete(da);
//					}
//				}
//			}				
//		}
//	}
	public void init(){
		
	       this.docAlertDao.deleteAlerts("delete from DocAlert da where da.isFromAcl = true and da.id not in (select ac.docAlertId from DocAcl ac)");
	}
	
	
	public List<DocResource> getSubFolderIds(Long docResId ,DocResource dr) {
		  String hql = "from DocResource where isFolder = true and logicalPath like ?" ;
				List<DocResource> list = docResourceDao.find(hql, dr.getLogicalPath() + ".%");
	 	  // List<DocResource> list = docAlertManager.getSubFolderIds(docResId);
	 	   return list;
	}
	
	

}