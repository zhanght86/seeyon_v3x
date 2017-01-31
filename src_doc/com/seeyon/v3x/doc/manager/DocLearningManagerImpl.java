package com.seeyon.v3x.doc.manager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.dao.DocLearningDao;
import com.seeyon.v3x.doc.dao.DocLearningHistoryDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocLearningHistory;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.Datetimes;

public class DocLearningManagerImpl implements DocLearningManager {
	private DocLearningDao docLearningDao;
	private DocLearningHistoryDao docLearningHistoryDao;
	private DocResourceDao docResourceDao;
	private UserMessageManager userMessageManager;

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setDocLearningDao(DocLearningDao docLearningDao) {
		this.docLearningDao = docLearningDao;
	}
	public void setDocLearningHistoryDao(DocLearningHistoryDao docLearningHistoryDao) {
		this.docLearningHistoryDao = docLearningHistoryDao;
	}
	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}

	/**
	 * 推送到学习区
	 */
	public void sendToLearnCenter(long docResourceId, String orgType, long orgId){
		
	}
	
	public void sendToLearnCenter(long docResourceId, String orgType, List<Long> orgIds){
		List<DocLearning> dls = new ArrayList<DocLearning>();
		Timestamp currentTime = new Timestamp(new Date().getTime());
		long currentUserId = CurrentUser.get().getId();
		
		List<DocLearning> list = this.getDocLearningsByOrgTypeAndDocId(orgType, docResourceId);
		Set<Long> ids = new HashSet<Long>();
		for(DocLearning d : list){
			ids.add(d.getOrgId());
		} 
		
		Set<Long> orgIdsSet = new HashSet<Long>();
		
		for(Long uid : orgIds){
			if(ids.contains(uid))
				continue;
			
			DocLearning dl = new DocLearning();
			dl.setCreateTime(currentTime);
			dl.setCreateUserId(currentUserId);
			DocResource dr = new DocResource();
			dr.setId(docResourceId);
			dl.setDocResource(dr);
			dl.setIdIfNew();
			dl.setOrgId(uid);
			dl.setOrgType(orgType);
			
			dls.add(dl);
			orgIdsSet.add(uid);
		}
		
		if(dls.size() > 0)
			docLearningDao.savePatchAll(dls);
				
		this.sendMessage(docResourceId, orgType, orgIdsSet);
	}
	
	/** 向需要学习的用户发送在线消息
	 * 	2007.09.26 暂时只在个人学习推送时发送
	 */
	private void sendMessage(long docResourceId, String orgType, Set<Long> orgIds){
		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)){
			DocResource dr = docResourceDao.get(docResourceId);			
		
			if(CollectionUtils.isNotEmpty(orgIds)) {
				List<Long> receiverIds = new ArrayList<Long>(orgIds);
				if(receiverIds.size() > 0) {
					Collection<MessageReceiver> msgReceiver = MessageReceiver.get(docResourceId,
							receiverIds, "message.link.doc.open", docResourceId + "");					
					try {
						userMessageManager.sendSystemMessage(MessageContent.get(Constants.getLearingAddKey(orgType), dr.getFrName()), 
								ApplicationCategoryEnum.doc, CurrentUser.get().getId(), msgReceiver);
					} catch (MessageException e) {
						
					}			
				}	
			}
		}
		
	}
	
	public void sendToLearnCenter(List<Long> docResourceIds, String orgType, long orgId) {
		
	}
	
	public void sendToLearnCenter(List<Long> docResourceIds, String orgType, List<Long> orgIds) {
		List<DocLearning> dls = new ArrayList<DocLearning>();
		Timestamp currentTime = new Timestamp(new Date().getTime());
		long currentUserId = CurrentUser.get().getId();
		// Map<docId, List<DocLearning>>
		Map<Long, List<DocLearning>> map = this.getDocLearningsByOrgTypeAndDocIds(orgType, docResourceIds);
		Set<Long> keyset = map.keySet();
		// Map<docResId, Set<orgId>>
		Map<Long, Set<Long>> idsmap = new HashMap<Long, Set<Long>>();
		for(long docid : keyset){
			List<DocLearning> list = map.get(docid);
			for(DocLearning dl : list){
				Set<Long> set = idsmap.get(docid);
				if(set == null){
					set = new HashSet<Long>();
					set.add(dl.getOrgId());
					idsmap.put(docid, set);
				}else{
					set.add(dl.getOrgId());
				}
			}			
		} 
		
		Set<Long> orgIdsSet = new HashSet<Long>();
		for(Long docid : docResourceIds){
			Set<Long> uids = idsmap.get(docid);
			orgIdsSet.clear();
			for(Long uid : orgIds){
				if(uids != null)
					if(uids.contains(uid))
						continue;
				
				DocLearning dl = new DocLearning();
				dl.setCreateTime(currentTime);
				dl.setCreateUserId(currentUserId);
				DocResource dr = new DocResource();
				dr.setId(docid);
				dl.setDocResource(dr);
				dl.setIdIfNew();
				dl.setOrgId(uid);
				dl.setOrgType(orgType);
				
				dls.add(dl);	
				orgIdsSet.add(uid);
			}
			
			this.sendMessage(docid, orgType, orgIdsSet);
		}
		
		if(dls.size() > 0)
			docLearningDao.savePatchAll(dls);
	}
	
	/**
	 * 查询学习文档
	 */
	@SuppressWarnings("unchecked")
	public List<DocLearning> getDocLearningsByCount(final String orgType, final long orgId, final int count){
		String hql = "from DocLearning where orgId = ? and orgType = ? order by createTime desc";
		Pagination.setNeedCount(false);
		return this.docLearningDao.find(hql, 0, count, null, orgId, orgType);
	}
	
	@SuppressWarnings("unchecked")
	public List getDocLearningsByPage(String orgType, long orgId) {
		List list = new ArrayList();
		if (orgType.equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT) && !CurrentUser.get().isInternal()) {
			list.add(0);
			list.add(new ArrayList<DocLearning>());
			return list;
		}

		final List<Type> parameterTypes = new ArrayList<Type>();
		final List<Object> parameterValues = new ArrayList<Object>();

		String hql = "";
		if (!Constants.ORGENT_TYPE_GROUP.equals(orgType)) {
			hql = "from DocLearning where orgId=? and orgType=? ";
			parameterTypes.add(Hibernate.LONG);
			parameterValues.add(orgId);
			parameterTypes.add(Hibernate.STRING);
			parameterValues.add(orgType);
		} else {
			hql = "from DocLearning where orgType=? ";
			parameterTypes.add(Hibernate.STRING);
			parameterValues.add(orgType);
		}

		int rowCount = docLearningDao.getQueryCount(hql, parameterValues.toArray(new Object[parameterValues.size()]),
														 parameterTypes.toArray(new Type[parameterTypes.size()]));
		Pagination.setRowCount(rowCount);
		hql += " order by createTime desc";
		list.add(rowCount);

		final String hqlf = hql;
		List<DocLearning> ret = (List<DocLearning>) docLearningDao.getHibernateTemplate().execute(new HibernateCallback() {
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
	@SuppressWarnings("unchecked")
	public List getDocLearningsByPage(String orgType, long orgId,String type,String value) {
		List list = new ArrayList();
		if (orgType.equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT) && !CurrentUser.get().isInternal()) {
			list.add(0);
			list.add(new ArrayList<DocLearning>());
			return list;
		}

		final List<Type> parameterTypes = new ArrayList<Type>();
		final List<Object> parameterValues = new ArrayList<Object>();

		String hql = "";
		if(type==null || "".equals(type)){
			hql = "from DocLearning learning where learning.orgType=? ";
			parameterTypes.add(Hibernate.STRING);
			parameterValues.add(orgType);
			if (!Constants.ORGENT_TYPE_GROUP.equals(orgType)) {
				hql += " and learning.orgId=? "; 
				parameterTypes.add(Hibernate.LONG);
				parameterValues.add(orgId); 
			} 
		}else{ 
			hql = "select learning from DocLearning learning,DocResource resource where resource.id = learning.docResource.id and learning.orgType=? ";
			parameterTypes.add(Hibernate.STRING);
			parameterValues.add(orgType);
			if (!Constants.ORGENT_TYPE_GROUP.equals(orgType)) {
				hql += " and learning.orgId=? ";
				parameterTypes.add(Hibernate.LONG);
				parameterValues.add(orgId);
			}  
			if("name".equals(type)){
				if(value!=null && !"".equals(value)){
					hql += " and resource.frName like ? ";
					parameterTypes.add(Hibernate.STRING);
					parameterValues.add("%"+value+"%");
				}
			}else if("category".equals(type)){
				if(value!=null && !"".equals(value)){
					hql += " and resource.frType = ? ";
					parameterTypes.add(Hibernate.LONG);
					parameterValues.add(Long.valueOf(value));
				}
			}else if("keywords".equals(type)){
				if(value!=null && !"".equals(value)){
					hql += " and resource.keyWords like ? ";
					parameterTypes.add(Hibernate.STRING);
					parameterValues.add("%"+value+"%");
				}
			}else if("creator".equals(type)){
				if(value!=null && !"".equals(value)){
					hql += " and learning.createUserId = ? ";
					parameterTypes.add(Hibernate.LONG);
					parameterValues.add(Long.valueOf(value)); 
				}
			}else if("createDate".equals(type)){
				if(value!=null && !"#".equals(value)){
					String[] arr = value.split("#"); 
					if(!"".equals(arr[0].trim())){
						hql += " and learning.createTime >= ? ";
						parameterTypes.add(Hibernate.TIMESTAMP);
						parameterValues.add(Datetimes.parse(arr[0].trim()));
					} 
					if(!"".equals(arr[1].trim())){
						hql += " and learning.createTime <= ? ";
						parameterTypes.add(Hibernate.TIMESTAMP);
						parameterValues.add(Datetimes.parse(arr[1].trim()));
					}
				}
			}
		} 
		int rowCount = docLearningDao.getQueryCount(hql.substring(hql.indexOf("from")), parameterValues.toArray(new Object[parameterValues.size()]),
														 parameterTypes.toArray(new Type[parameterTypes.size()]));
		Pagination.setRowCount(rowCount);
		hql += " order by learning.createTime desc";
		list.add(rowCount);

		final String hqlf = hql;
		List<DocLearning> ret = (List<DocLearning>) docLearningDao.getHibernateTemplate().execute(new HibernateCallback() {
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
	
	public List<DocLearning> getDocLearningsByOrgTypeAndDocId(String orgType, long docResourceId){
		String hql = "from DocLearning where docResource = ? and orgType = ? order by createTime desc";
		return docLearningDao.find(hql, new DocResource(docResourceId), orgType);
	}
	
	public Map<Long, List<DocLearning>> getDocLearningsByOrgTypeAndDocIds(String orgType, 
			List<Long> docResourceIds){
		Map<Long, List<DocLearning>> ret = new HashMap<Long, List<DocLearning>>();
		
		if(docResourceIds == null || docResourceIds.size() == 0)
			return ret;
			
		String docids = "";
		for(Long id : docResourceIds){
			docids += "," + id;
		}
		docids = docids.substring(1, docids.length());
		
		String hql = "from DocLearning where orgType = :ot and docResource in(:ids)";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("ot", orgType);
		amap.put("ids", Constants.getDocsByIds(docids, ","));
		List<DocLearning> list = docLearningDao.find(hql, -1, -1, amap);
		for(DocLearning dl : list){
			List<DocLearning> dll = ret.get(dl.getDocResource().getId());
			if(dll == null){
				dll = new ArrayList<DocLearning>();
				dll.add(dl);
				ret.put(dl.getDocResource().getId(), dll);
			}else{
				dll.add(dl);
			}
		}
		
		return ret;
	}

	
	/**
	 * 取消学习
	 */
	public void cancelLearn(List<Long> learnIds){
		
	}
	
	public void cancelLearn(long learnId){
		
	}
	
	public void cancelLearn(String learnIds){
		String hql = "from DocLearning where id in(:ids)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", Constants.parseStrings2Longs(learnIds, ","));
		List<DocLearning> list = docLearningDao.find(hql, -1, -1, map);
		Set<DocResource> drs = new HashSet<DocResource>();
		for(DocLearning dl : list){
			drs.add(dl.getDocResource());
		}
		
		Map<String, Object> namedParameter = new HashMap<String, Object>();
		namedParameter.put("learnIds",Constants.parseStrings2Longs(learnIds, ","));
		
		docLearningDao.bulkUpdate("delete from DocLearning where id in(:learnIds)", namedParameter);
		
		String docids = "";
		for(DocResource d : drs){
			String hql2 = "from DocLearning where docResource = ?";
			List dls = docLearningDao.find(hql2, d);
			if(dls == null || dls.size() == 0){
				d.setIsLearningDoc(false);
				docResourceDao.update(d);
				
				docids += "," + d.getId();
			}
		}
		
		if(!docids.equals(""))
			this.deleteLearningHistorys(docids.substring(1, docids.length()));
	}
	
	public void deleteLearnByDocId(long docId){
		String hql2 = "delete from DocLearningHistory where docResourceId=?";
		docLearningHistoryDao.bulkUpdate(hql2, null, docId);
		
		String hql = "delete from DocLearning where docResource.id=?";
		docLearningDao.bulkUpdate(hql, null, docId);
	}
	
	/**
	 * 记录学习记录(当前用户)
	 */
	public void learnTheDoc(long docResourceId){
		this.deleteLearningHistoryOfCurrentUser(docResourceId);
		
		DocLearningHistory his = new DocLearningHistory();
		his.setIdIfNew();
		his.setAccessMemberId(CurrentUser.get().getId());
		his.setDepartmentId(CurrentUser.get().getDepartmentId());
		his.setAccessTime(new Timestamp(new Date().getTime()));
		his.setDocResourceId(docResourceId);
		
		docLearningHistoryDao.save(his);
	}
	
	/**
	 * 查看学习记录
	 */
	public List<DocLearningHistory> getTheLearnHistory(long docResourceId){
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<DocLearningHistory> getTheLearnHistoryByPage(long docResourceId){
		final List<Type> parameterTypes = new ArrayList<Type>();
		final List<Object> parameterValues = new ArrayList<Object>();
		
		parameterTypes.add(Hibernate.LONG);
		parameterValues.add(docResourceId);
		
		List<DocLearningHistory> ret = new ArrayList<DocLearningHistory>();
		String hql = "from DocLearningHistory where docResourceId=?";

        if (Pagination.isNeedCount()) {
            int rowCount = docLearningHistoryDao.getQueryCount(hql, parameterValues.toArray(new Object[parameterValues.size()]), parameterTypes.toArray(new Type[parameterTypes.size()]));
            Pagination.setRowCount(rowCount);
        }
        
        hql += " order by accessTime desc";
        final String hqlf = hql;
		
		ret = (List<DocLearningHistory>)docLearningHistoryDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hqlf)
				.setParameters(parameterValues.toArray(new Object[parameterValues.size()]), parameterTypes.toArray(new Type[parameterTypes.size()]))
				.setFirstResult(Pagination.getFirstResult())
        		.setMaxResults(Pagination.getMaxResults())
        		.list();
			}
    	});
		
		return ret;
	}
	@SuppressWarnings("unchecked")
	public List<DocLearningHistory> getTheLearnHistoryByDeptByPage(long docResourceId, long deptId){
		final List<Type> parameterTypes = new ArrayList<Type>();
		final List<Object> parameterValues = new ArrayList<Object>();
		
		parameterTypes.add(Hibernate.LONG);
		parameterValues.add(docResourceId);
		parameterTypes.add(Hibernate.LONG);
		parameterValues.add(deptId);
		
		List<DocLearningHistory> ret = new ArrayList<DocLearningHistory>();
		String hql = "from DocLearningHistory where docResourceId=? and departmentId=?";

        if (Pagination.isNeedCount()) {
            int rowCount = docLearningHistoryDao.getQueryCount(hql, parameterValues.toArray(new Object[parameterValues.size()]), parameterTypes.toArray(new Type[parameterTypes.size()]));
            Pagination.setRowCount(rowCount);
        }
        hql += " order by accessTime desc";  
        
        final String hqlf = hql;
		
		ret = (List<DocLearningHistory>)docLearningHistoryDao.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hqlf)
				.setParameters(parameterValues.toArray(new Object[parameterValues.size()]), parameterTypes.toArray(new Type[parameterTypes.size()]))
				.setFirstResult(Pagination.getFirstResult())
        		.setMaxResults(Pagination.getMaxResults())
        		.list();
			}
    	});
		
		return ret;
	}
	
	public DocLearningHistory getTheLearnHistoryOfCurrentUser(long docResourceId){
		String hql = "from DocLearningHistory where accessMemberId=? and docResourceId=?";
		List<DocLearningHistory> list =  docLearningHistoryDao.find(hql, CurrentUser.get().getId(), docResourceId);
		if(list == null || list.size() == 0)
			return null;
		else
			return list.get(0); 
	}
	
	/**
	 * 删除学习记录
	 */
	public void deleteLearningHistorys(String docResourceIds){
		String hql = "delete from DocLearningHistory where docResourceId in(:docResourceIds)";
		
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("docResourceIds", Constants.parseStrings2Longs(docResourceIds, ","));
		
		docLearningHistoryDao.bulkUpdate(hql, namedParameters);
	}
	
	public void deleteLearningHistoryOfCurrentUser(long docResourceId){
		String hql = "delete from DocLearningHistory where accessMemberId=? and docResourceId=?";
		docLearningHistoryDao.bulkUpdate(hql, null, CurrentUser.get().getId(), docResourceId);
	}
	
	@SuppressWarnings("unchecked")
	public boolean isLearnDoc(long docId){
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		String hql = "from DocLearning where docResource.id = :id and orgId in (:orgs)";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("orgs", Constants.parseStrings2Longs(orgIds, ","));
		namedParameters.put("id", docId);
		List<DocLearning> list =  docLearningDao.find(hql, namedParameters);
		return CollectionUtils.isNotEmpty(list);	
	}
}
