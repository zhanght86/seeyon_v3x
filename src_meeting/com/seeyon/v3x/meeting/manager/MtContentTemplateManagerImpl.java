package com.seeyon.v3x.meeting.manager;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtContentTemplateDao;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;

/**
 * 会议正文版面的Manager的实现类
 * @author wolf
 *
 */
public class MtContentTemplateManagerImpl extends BaseMeetingManager implements MtContentTemplateManager {
	private MtContentTemplateDao MtContentTemplateDao;
		
	private FileManager fileManager;

	public MtContentTemplateDao getMtContentTemplateDao() {
		return MtContentTemplateDao;
	}
	
	public void setMtContentTemplateDao(MtContentTemplateDao MtContentTemplateDao) {
		this.MtContentTemplateDao = MtContentTemplateDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#delete(java.lang.Long)
	 */
	@SuppressWarnings("deprecation")
	public void delete(Long id) throws BusinessException {
		MtContentTemplate template=this.getById(id);		
		//如果会议正文版面格式为Word和Excel，则需要删除正文附件
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

	MtContentTemplateDao.getSessionFactory().getCurrentSession().flush();
		
		MtContentTemplateDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 初始化会议正文版面列表
	 * @param list
	 */
	private void initList(List<MtContentTemplate> list){
		for(MtContentTemplate template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化会议正文版面 1、初始化创建用户姓名 
	 * @param template
	 */
	private void initTemplate(MtContentTemplate template) {
		template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtContentTemplate> findAll() {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();//区分集团和单位  
		DetachedCriteria dc=DetachedCriteria.forClass(MtContentTemplate.class);
		dc.add(Restrictions.eq("accountId", accountId));
		dc.addOrder(Order.asc("createDate"));
		List<MtContentTemplate> list=MtContentTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * 查询所有正文版面----按分类（会议计划公告新闻），支持分页
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtContentTemplate> findTypeAll(String type) throws BusinessException {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtContentTemplate.class);
		dc.add(Restrictions.eq("accountId", accountId));
		dc.add(Restrictions.eq("ext1", type));//1会议2计划3公告4新闻			
		dc.addOrder(Order.asc("createDate"));
		List<MtContentTemplate> list=MtContentTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * 查询所有正文版面----按分类（会议计划公告新闻），不支持分页
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtContentTemplate> findTypeAllNoPage(String type) throws BusinessException {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtContentTemplate.class);
		dc.add(Restrictions.eq("accountId", accountId));
		dc.add(Restrictions.eq("ext1", type));//1会议2计划3公告4新闻			
		dc.addOrder(Order.asc("createDate"));
		List<MtContentTemplate> list=MtContentTemplateDao.executeCriteria(dc,-1,-1);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * 查询所有正文版面----按分类（会议计划公告新闻），支持分页
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtContentTemplate> findGroupTypeAll(String type) throws BusinessException {
		DetachedCriteria dc=DetachedCriteria.forClass(MtContentTemplate.class);
		dc.add(Restrictions.eq("ext2", type));//3公告4新闻			
		dc.addOrder(Order.asc("createDate"));
		List<MtContentTemplate> list=MtContentTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<MtContentTemplate> findByProperty(String property, Object value) {
		List<MtContentTemplate> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtContentTemplate> findByPropertyNoInit(String property, Object value) {
		List<MtContentTemplate> list;
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtContentTemplate.class);
		dc.add(Restrictions.eq("accountId", accountId));
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		dc.addOrder(Order.asc("createDate"));
		list=MtContentTemplateDao.executeCriteria(dc);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#getById(java.lang.Long)
	 */
	public MtContentTemplate getById(Long id) {
		MtContentTemplate template= MtContentTemplateDao.get(id);
		if(template!=null){
			template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
		}
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#save(com.seeyon.v3x.news.domain.MtContentTemplate)
	 */
	public MtContentTemplate save(MtContentTemplate template) throws MeetingException {
		//检查该名称的会议正文类型是否已经存在，如果存在，则提示用户
		//同一类型名字相同的话给提示.
		MtContentTemplate mt=MtContentTemplateDao.findMttembyTemplateName(template.getTemplateName(),template.getExt1(),template.getExt2());
		if(mt!=null)
		{
			throw new MeetingException("news_alreay_exists",template.getTemplateName());
		}
		/*if(MtContentTemplateDao.isNotUnique(template, "templateName")){
		}*/
			
		if(template.isNew()){
			template.setIdIfNew();
			MtContentTemplateDao.save(template);
		}else{
			MtContentTemplateDao.update(template);
		}
		return template;
	}
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtContentTemplateManager#save(com.seeyon.v3x.news.domain.MtContentTemplate)
	 */
	public MtContentTemplate saveTemplate(MtContentTemplate template) throws MeetingException {
		if(template.isNew()){
			template.setIdIfNew();
			MtContentTemplateDao.save(template);
		}else{
			MtContentTemplateDao.update(template);
		}
		return template;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public String checkDupleName(String tName)throws MeetingException{
//		检查该名称的会议正文类型是否已经存在，如果存在，则提示用户
		
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtContentTemplate.class);
		dc.add(Restrictions.eq("accountId", accountId));	
		List<MtContentTemplate>list=MtContentTemplateDao.executeCriteria(dc);
		for(MtContentTemplate temp : list){
			if(temp.getTemplateName().equals(tName)){
				throw new MeetingException("news_alreay_exists", tName);
			}
		}
		return "";
	}
	
	public void saveAll(List<MtContentTemplate> mcts) {
		MtContentTemplateDao.savePatchAll(mcts);
	}
	
}