package com.seeyon.v3x.doc.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.dao.DocAlertLatestDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.domain.DocAlert;
import com.seeyon.v3x.doc.domain.DocAlertLatest;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;

public class DocAlertLatestManagerImpl implements DocAlertLatestManager {
	
	private static final Log log = LogFactory.getLog(DocAlertLatestManagerImpl.class);

	private DocAlertManager docAlertManager;
	private DocResourceDao docResourceDao;
	private DocAlertLatestDao docAlertLatestDao;
	private OrgManager orgManager;
	private UserMessageManager userMessageManager;
	private DocLibManager	docLibManager;

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setDocAlertLatestDao(DocAlertLatestDao docAlertLatestDao) {
		this.docAlertLatestDao = docAlertLatestDao;
	}

	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}

	public void setDocAlertManager(DocAlertManager docAlertManager) {
		this.docAlertManager = docAlertManager;
	}

	public void addAlertLatest(Long drId, byte alertOprType, Long lastUserId, Timestamp lastUpdate, String msgType, String oldName) {
		DocResource dr = this.docResourceDao.get(drId);
		this.addAlertLatest(dr, alertOprType, lastUserId, lastUpdate, msgType, oldName);
	}
	
	/**
	 * 更新订阅的文档
	 */
	public void addAlertLatest(DocResource dr, byte alertOprType, Long lastUserId, Timestamp lastUpdate, String msgType, String oldName) {	
		Long docResourceId = dr.getId();
		List<DocAlert> alerts = docAlertManager.findAlertsByDocResourceId(dr, alertOprType);
		// 所有需要推送链接的用户
		Set<Long> alertMemberIds = new HashSet<Long>();
		// 所有需要在线消息提醒的用户
		Set<Long> msgMemberIds = new HashSet<Long>();
		byte status = Constants.DOC_ALERT_STATUS_MYSELF;
		
        DocLib personLib = getDocLibManager().getPersonalLibOfUser(lastUserId);
		//排除个人文档库的信息发送
        if(!personLib.getId().equals(dr.getDocLibId())){
            //获取文档库管理员id
            Map<Long,List<Long>> lib2OwnerMap = getDocLibManager().getDocLibOwnersByIds(CommonTools.newArrayList(dr.getDocLibId()));
            List<Long> libOwenrIds = lib2OwnerMap.get(dr.getDocLibId());
            alertMemberIds.addAll(libOwenrIds);
            msgMemberIds.addAll(libOwenrIds);
        }
		
		if(CollectionUtils.isNotEmpty(alerts)) {
			for(DocAlert a : alerts){
				String userType = a.getAlertUserType();
			    if(a.getIsFromAcl()) 
			    	status = Constants.DOC_ALERT_STATUS_OTHER;
	
				List<Long> memberIds = FormBizConfigUtils.getMemberIdsByTypeAndId(userType + "|" + a.getAlertUserId(), orgManager);
				FormBizConfigUtils.addAllIgnoreEmpty(alertMemberIds, memberIds);
				
				boolean sendMessage = this.sendMessage(a, alertOprType);
				if(sendMessage)
					FormBizConfigUtils.addAllIgnoreEmpty(msgMemberIds, memberIds);
			}
		}
		
		if(!dr.getIsFolder()){
			// 清除最后的同id同类型操作记录, 增加新的
			if(alertOprType != Constants.ALERT_OPR_TYPE_DELETE) {
				String delhql = "delete from DocAlertLatest where docResourceId=? and changeType=?";
				docAlertLatestDao.bulkUpdate(delhql, null, docResourceId, alertOprType);		
	
				// 逐个推送最新文档
				for(Long id : alertMemberIds) {
					DocAlertLatest dal = new DocAlertLatest();
					dal.setIdIfNew();
					dal.setAlertUserId(id);
					dal.setChangeType(alertOprType);
					dal.setDocResourceId(docResourceId);
					dal.setDocResourceName(dr.getFrName());
					dal.setLastUpdate(lastUpdate);
					dal.setLastUserId(lastUserId);
					dal.setMimeTypeId(dr.getMimeTypeId());
					dal.setStatus(status);
					
					docAlertLatestDao.save(dal);
				}
			}
		}
		// 4. 发送消息
		//多个接收者，没有链接
		List<Long> receiverIds = new ArrayList<Long>();
		
		// 公文消息过滤掉外部人员
		for (Long msgMemberId : msgMemberIds) {
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(msgMemberId);
			} catch (BusinessException e) {
				log.error("", e);
			}

			if (dr.getPigeonholeType() != null && (dr.getPigeonholeType() == 0 || dr.getPigeonholeType() == 1)) {
				if (member != null && !member.getIsInternal()) {
					continue;
				}
			}

			receiverIds.add(msgMemberId);
		}
		
		if(receiverIds.size() > 0) {
			Collection<MessageReceiver> msgReceiver = null;
			if(dr.getIsFolder())
				msgReceiver = MessageReceiver.get(docResourceId, receiverIds);
			else{
				if(alertOprType != Constants.ALERT_OPR_TYPE_DELETE)
				msgReceiver = MessageReceiver.get(docResourceId,
					receiverIds, "message.link.doc.open", docResourceId.toString());
				else
					msgReceiver = MessageReceiver.get(docResourceId, receiverIds);	
			}

			try {
				userMessageManager.sendSystemMessage(MessageContent.get(msgType, dr.getFrName(),oldName), 
						ApplicationCategoryEnum.doc, CurrentUser.get().getId(), msgReceiver );
			} catch (MessageException e) {
				log.error("发送最新订阅消息", e);
			}
		}
	}
	
	/**
	 * 是否发送消息
	 */
	private boolean sendMessage(DocAlert alert, byte changeType) {
		boolean flag = alert.getSendMessage() && alert.getChangeType() == changeType;
		if (flag)
			return true;
		flag = alert.getSendMessage() && alert.getChangeType() == Constants.ALERT_OPR_TYPE_ALL;
		if (flag)
			return true;
		if (changeType == Constants.ALERT_OPR_TYPE_DELETE)
			if (alert.getChangeType() == Constants.ALERT_OPR_TYPE_DELETE || alert.getChangeType() == Constants.ALERT_OPR_TYPE_ALL)
				flag = true;
		return flag;
	}
	
	/**
	 * 查询最新修改的订阅文档
	 * 不同首页对当前用户进行权限过滤
	 */
	public List<DocAlertLatest> findAlertLatestsByUser(long alertUserId ,byte status)  {
		return this.findAlertLatestsByUser(alertUserId, status, false);		
	}
	
	@SuppressWarnings("unchecked")
	private List<DocAlertLatest> findAlertLatestsByUser(long alertUserId ,byte status, boolean pagination) {
		List<Object> objects = new ArrayList<Object>();
		String hql = null;
		if (status == Constants.DOC_ALERT_STATUS_ALL) {
			hql = "from DocAlertLatest as dal where dal.alertUserId = ? order by dal.lastUpdate desc";
			objects.add(alertUserId);
		} else {
			hql = "from DocAlertLatest as dal where dal.alertUserId = ? and status = ? order by dal.lastUpdate desc";
			objects.add(alertUserId);
			objects.add(status);
		}
		if(pagination)
			return docAlertLatestDao.find(hql, null,objects);
		else
			return docAlertLatestDao.find(hql, objects.toArray());
	}
	
	public List<DocAlertLatest> findAlertLatestsByUserPaged(long alertUserId ,byte status)  {
		return this.findAlertLatestsByUser(alertUserId, status, true);	
	}

	/**
	 * 查询条件值的查询
	 */
	public List<DocAlertLatest> findAlertLatestsByUserPaged(long alertUserId ,byte status,String type, String value)  {
		List<Object> objects = new ArrayList<Object>();
		String hql = null;
		hql = "select dal from DocAlertLatest dal,DocResource res where res.id = dal.docResourceId and dal.alertUserId = ? ";
		objects.add(alertUserId);
		if (status != Constants.DOC_ALERT_STATUS_ALL) {
			hql += " and dal.status = ? ";
			objects.add(status);
		}
		if(value!=null && !"".equals(value)){
			if("name".equals(type)){
				hql += " and res.frName like ? ";
				objects.add("%"+value+"%");
			}else if("category".equals(type)){
				hql += " and res.frType = ? ";
				objects.add(Long.valueOf(value));
			}else if("keywords".equals(type)){
				hql += " and res.keyWords like ? ";
				objects.add("%"+value+"%");
			}else if("creator".equals(type)){
				hql += " and dal.lastUserId = ? ";
				objects.add(Long.valueOf(value)); 
			}else if("createDate".equals(type)){
				String[] arr = value.split("#"); 
				if(!"".equals(arr[0].trim())){
					hql += " and dal.lastUpdate >= ? ";
					objects.add(Datetimes.parse(arr[0].trim()));
				} 
				if(!"".equals(arr[1].trim())){
					hql += " and dal.lastUpdate <= ? ";
					objects.add(Datetimes.parse(arr[1].trim()));
				}
			} 
		}

		hql += " order by dal.lastUpdate desc";
		return docAlertLatestDao.find(hql, null,objects);	
	}
	/**
	 * 查询特定条数最新修改的订阅文档
	 */
	public List<DocAlertLatest> findAlertLatestsByUserByCount(long alertUserId, byte status, int count) {
		List<DocAlertLatest> list = this.findAlertLatestsByUser(alertUserId, status);
		List<DocAlertLatest> ret = new ArrayList<DocAlertLatest>();
		if (list.size() > 0) {
			for (int i = 0; i < count; i++) {
				if (i == list.size())
					break;
				ret.add(list.get(i));
			}
		}
		return ret;
	}

	/**
	 * 整理存储，删除规定条数以外的记录，保留最新的
	 * @param maxNum 每个订阅用户允许保留的最多记录数 
	 */
	@SuppressWarnings("unchecked")
	public void tidyAlertLatests(int maxNum) throws DocException {
		String hql = "select alertUserId from DocAlertLatest group by alertUserId";
		List<Object[]> dals = docAlertLatestDao.getHibernateTemplate().find(hql);
		for(Object[] dal:dals) {
			String thql = "from DocAlertLatest as d where d.alertUserId = ? order by d.lastUpdate asc";
			List<DocAlertLatest> list = docAlertLatestDao.find(thql, (Long)dal[0]);
			if(list != null) {
				int size = list.size();
				if(size > maxNum) {
					for(int i = 0; i < size - maxNum; i++) {
						docAlertLatestDao.delete(list.get(i));
					}
				}
			}
		}
		
	}
	
	/**
	 * 根据DocResource删除所有最新订阅
	 */
	@SuppressWarnings("unchecked")
	public void deleteAlertLatestsByDoc(DocResource dr)  {
		String hql = "delete from DocAlertLatest as a where a.docResourceId in (:ids)";
		List<Long> ids = new ArrayList<Long>();
		ids.add(dr.getId());
		// 处理文档夹的订阅
		if(dr.getIsFolder()) {
			String hql2 = "from DocResource where logicalPath like :lp or id=:aid";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lp", dr.getLogicalPath() + ".%");
			map.put("aid", dr.getId());
			List<DocResource> list = docResourceDao.find(hql2, -1, -1, map);
			for(DocResource d : list) {
				ids.add(d.getId());
			}
		}
		
		Map<String, Object> namedParameter = new HashMap<String, Object>();
		namedParameter.put("ids", ids);
		
		docAlertLatestDao.bulkUpdate(hql, namedParameter);
	}
	
	public void deleteLatestByIds(String ids) {
		String hql = "delete from DocAlertLatest where id in (:ids)";
		Map<String, Object> namedParameter = new HashMap<String, Object>();
		namedParameter.put("ids", Constants.parseStrings2Longs(ids, ","));
		
		docAlertLatestDao.bulkUpdate(hql, namedParameter);
	}
	
	public void deleteAlertLatestByDrIdAndOprTypeOfCurrentUser(long docResId, Set<Byte> oprType){
		String hql = "delete from DocAlertLatest where docResourceId = ? and alertUserId = ? and changeType in (:types)";

		Map<String, Object> namedParameter = new HashMap<String, Object>();
		namedParameter.put("types", oprType);
		
		docAlertLatestDao.bulkUpdate(hql, namedParameter, docResId, CurrentUser.get().getId());
	}
	
	/**
	 * 分页查找最新订阅
	 */
	public List<DocAlertLatest> pagedFindAlertLatest(long alertUserId, byte status) {
		List<DocAlertLatest> list = this.findAlertLatestsByUser(alertUserId, status);
		return FormBizConfigUtils.pagenate(list, false);
	}
	
	public int findAlertLatestTotal(long alertUserId, byte status) {
		List<DocAlertLatest> list = this.findAlertLatestsByUser(alertUserId, status);
		return list == null ? 0 : list.size();
	}

	private DocLibManager getDocLibManager() {
		if(docLibManager==null){
			return (DocLibManager)ApplicationContextHolder.getBean("docLibManager");
		}else{
			return docLibManager;
		}
	}
	
}