package com.seeyon.v3x.meeting.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtResourcesDao;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtResources;
import com.seeyon.v3x.util.Strings;

/**
 * 会议资源的Manager的实现类
 * @author wolf
 *
 */
public class MtResourcesManagerImpl extends BaseMeetingManager implements MtResourcesManager {
	private MtResourcesDao MtResourcesDao;
		
	public MtResourcesDao getMtResourcesDao() {
		return MtResourcesDao;
	}
	
	public void setMtResourcesDao(MtResourcesDao MtResourcesDao) {
		this.MtResourcesDao = MtResourcesDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#delete(java.lang.Long)
	 */
	@SuppressWarnings("deprecation")
	public void delete(Long id) throws BusinessException {
		MtResourcesDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		String hql = "delete from " + MtResources.class.getName() + " as r where r.id in (:ids)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", ids);
		this.MtResourcesDao.bulkUpdate(hql, params);
	}
	
	/**
	 * 删除会议对应的会议资源
	 * @param meetingId
	 */
	public void deleteByMeetingId(Long meetingId) {
		this.MtResourcesDao.delete(new Object[][]{{"meetingId", meetingId}});
	}

	/**
	 * 初始化会议资源列表
	 * @param list
	 */
	private void initList(List<MtResources> list){
		for(MtResources template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化会议资源 1、初始化创建用户姓名 
	 * @param template
	 */
	private void initTemplate(MtResources template) {
		template.setUserName(this.getMeetingUtils().getMemberNameByUserId(template.getUserId()));
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtResources> findAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(MtResources.class);
		dc.addOrder(Order.asc("createDate"));
		List<MtResources> list=MtResourcesDao.executeCriteria(dc);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<MtResources> findByProperty(String property, Object value) {
		List<MtResources> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtResources> findByPropertyNoInit(String property, Object value) {
		List<MtResources> list;
		DetachedCriteria dc=DetachedCriteria.forClass(MtResources.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		list=MtResourcesDao.executeCriteria(dc,-1,-1);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#getById(java.lang.Long)
	 */
	public MtResources getById(Long id) {
		MtResources template= MtResourcesDao.get(id);
		template.setUserName(this.getMeetingUtils().getMemberNameByUserId(template.getUserId()));
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtResourcesManager#save(com.seeyon.v3x.news.domain.MtResources)
	 */
	public MtResources save(MtResources template) throws MeetingException {
		if(template.isNew()){
			template.setIdIfNew();
			MtResourcesDao.save(template);
		}else{
			MtResourcesDao.update(template);
		}
		return template;
	}
	
	/**
	 * 保存会议对应的与会资源
	 * @param meeting
	 */
	public void saveMtResources4Meeting(MtMeeting meeting) {
		String resourceIds = meeting.getResourcesId();
		if (Strings.isNotBlank(resourceIds)) {
			String[] ids = resourceIds.split(",");
			List<MtResources> resources = new ArrayList<MtResources>();
			for (String id : ids) {
				MtResources res = new MtResources();
				res.setNewId();
				res.setBeginDate(meeting.getBeginDate());
				res.setEndDate(meeting.getEndDate());
				res.setMeetingId(meeting.getId());
				res.setResourceId(Long.valueOf(id));
				res.setReserveFlag(false);
				res.setUserId(meeting.getCreateUser());
				
				resources.add(res);
			}
			this.MtResourcesDao.savePatchAll(resources);
		}
	}


}
