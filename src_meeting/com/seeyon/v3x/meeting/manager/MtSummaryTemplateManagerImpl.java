package com.seeyon.v3x.meeting.manager;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtSummaryTemplateDao;
import com.seeyon.v3x.meeting.domain.MtSummaryTemplate;

/**
 * 会议总结版面的Manager的实现类
 * @author wolf
 *
 */
public class MtSummaryTemplateManagerImpl extends BaseMeetingManager implements MtSummaryTemplateManager {
	private MtSummaryTemplateDao MtSummaryTemplateDao;
		
	private FileManager fileManager;

	public MtSummaryTemplateDao getMtSummaryTemplateDao() {
		return MtSummaryTemplateDao;
	}
	
	public void setMtSummaryTemplateDao(MtSummaryTemplateDao MtSummaryTemplateDao) {
		this.MtSummaryTemplateDao = MtSummaryTemplateDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#delete(java.lang.Long)
	 */
	@SuppressWarnings("deprecation")
	public void delete(Long id) throws BusinessException {
		MtSummaryTemplate template=this.getById(id);		
		//如果会议总结版面格式为Word和Excel，则需要删除正文附件
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(template.getTemplateFormat())	
			){
			try {
				//attachmentManager.deleteByReference(template.getId(), template.getId());
				fileManager.deleteFile(template.getId(),template.getCreateDate(), true);
			} catch (BusinessException e) {
				throw e;
			}
		}

		MtSummaryTemplateDao.getSessionFactory().getCurrentSession().flush();
		
		MtSummaryTemplateDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 初始化会议总结版面列表
	 * @param list
	 */
	private void initList(List<MtSummaryTemplate> list){
		for(MtSummaryTemplate template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化会议总结版面 1、初始化创建用户姓名 
	 * @param template
	 */
	private void initTemplate(MtSummaryTemplate template) {
		template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtSummaryTemplate> findAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(MtSummaryTemplate.class);
		dc.addOrder(Order.asc("createDate"));
		List<MtSummaryTemplate> list=MtSummaryTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<MtSummaryTemplate> findByProperty(String property, Object value) {
		List<MtSummaryTemplate> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtSummaryTemplate> findByPropertyNoInit(String property, Object value) {
		List<MtSummaryTemplate> list;
		DetachedCriteria dc=DetachedCriteria.forClass(MtSummaryTemplate.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		dc.addOrder(Order.asc("createDate"));
		list=MtSummaryTemplateDao.executeCriteria(dc);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#getById(java.lang.Long)
	 */
	public MtSummaryTemplate getById(Long id) {
		MtSummaryTemplate template= MtSummaryTemplateDao.get(id);
		template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtSummaryTemplateManager#save(com.seeyon.v3x.news.domain.MtSummaryTemplate)
	 */
	public MtSummaryTemplate save(MtSummaryTemplate template) throws MeetingException {
		//检查该名称的会议总结类型是否已经存在，如果存在，则提示用户
//		会议总结判断重名没什么价值   去掉
//		if(MtSummaryTemplateDao.isNotUnique(template, "templateName")){
//			throw new MeetingException("news.alreay_exists",template.getTemplateName());
//		}
			
		if(template.isNew()){
			template.setIdIfNew();
			MtSummaryTemplateDao.save(template);
		}else{
			MtSummaryTemplateDao.update(template);
		}
		return template;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	
	/**
	 * 判断会议总结是否存在   总结转发协同时调用
	 * @param id
	 * @return
	 */
	public boolean isMeetingSummaryExist(Long id){
		List<MtSummaryTemplate> list = this.findByProperty("meetingId", id);
		return list.size() > 0 ? true : false;
	}

	
	
}
