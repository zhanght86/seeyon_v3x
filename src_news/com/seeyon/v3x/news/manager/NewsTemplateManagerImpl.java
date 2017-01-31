package com.seeyon.v3x.news.manager;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.news.NewsException;
import com.seeyon.v3x.news.dao.NewsTemplateDao;
import com.seeyon.v3x.news.dao.NewsTypeDao;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsTemplate;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.login.CurrentUser;

/**
 * 新闻版面的Manager的实现类
 * @author wolf
 *
 */
public class NewsTemplateManagerImpl extends BaseNewsManager implements NewsTemplateManager {
	private NewsTemplateDao newsTemplateDao;
	private NewsTypeDao newsTypeDao;
	
	private FileManager fileManager;

	public NewsTemplateDao getNewsTemplateDao() {
		return newsTemplateDao;
	}
	
	public void setNewsTemplateDao(NewsTemplateDao newsTemplateDao) {
		this.newsTemplateDao = newsTemplateDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#delete(java.lang.Long)
	 */
	public void delete(Long id) throws BusinessException {
		NewsTemplate template=this.getById(id);		
		//如果新闻版面格式为Word和Excel，则需要删除正文附件
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(template.getTemplateFormat())
			){
			try {
				//attachmentManager.deleteByReference(template.getId(), template.getId());
				fileManager.deleteFile(template.getId(),template.getCreateDate(), true);
			} catch (BusinessException e) {
				throw e;
			}
		}
		//删除其他新闻类型对该版面的引用
		for(NewsType type:template.getNewsTypes()){
			type.setDefaultTemplate(null);
			newsTypeDao.save(type);
		}
		newsTemplateDao.getSessionFactory().getCurrentSession().flush();
		
		newsTemplateDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 初始化新闻版面列表
	 * @param list
	 */
	private void initList(List<NewsTemplate> list){
		for(NewsTemplate template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化新闻版面 1、初始化创建用户姓名 2、初始化引用该新闻版面的新闻类型列表
	 * @param template
	 */
	private void initTemplate(NewsTemplate template) {
		template.setCreateUserName(this.getNewsUtils().getMemberNameByUserId(template.getCreateUser()));
		
		if(template.getNewsTypes()==null || template.getNewsTypes().size()==0) template.setTypeNames("");
		else{
			String typeNames="";
			for(NewsType type:template.getNewsTypes()){
				typeNames=typeNames+","+type.getTypeName();
			}
			
			template.setTypeNames(typeNames.substring(1));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<NewsTemplate> findAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(NewsTemplate.class);
//		添加集团化处理
		dc.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
		dc.addOrder(Order.desc("createDate"));
//		List<NewsTemplate> list=newsTemplateDao.paginate(dc.getExecutableCriteria(newsTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		List<NewsTemplate> list=this.newsTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}
	@SuppressWarnings("unchecked")
	public List<NewsTemplate> findGroupAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(NewsTemplate.class);
//		添加集团化处理
		dc.add(Restrictions.eq("ext1", "0"));
//		List<NewsTemplate> list=newsTemplateDao.paginate(dc.getExecutableCriteria(newsTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		
		dc.addOrder(Order.desc("createDate"));
		
		List<NewsTemplate> list=this.newsTemplateDao.executeCriteria(dc);
		
		initList(list);
		return list;
	}
	

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<NewsTemplate> findByProperty(String property, Object value) {
		List<NewsTemplate> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<NewsTemplate> findGroupByProperty(String property, Object value) {
		List<NewsTemplate> list;
		list=findGroupByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<NewsTemplate> findByPropertyNoInit(String property, Object value) {
		List<NewsTemplate> list;
		DetachedCriteria dc=DetachedCriteria.forClass(NewsTemplate.class);
		
//		添加集团化处理
		dc.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}

//		list=newsTemplateDao.paginate(dc.getExecutableCriteria(newsTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		dc.addOrder(Order.desc("createDate"));
		
		list=this.newsTemplateDao.executeCriteria(dc);
		return list;
	}
	/* (non-Javadoc)集团版面查询
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<NewsTemplate> findGroupByPropertyNoInit(String property, Object value) {
		List<NewsTemplate> list;
		DetachedCriteria dc=DetachedCriteria.forClass(NewsTemplate.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
//		添加集团化处理
		dc.add(Restrictions.eq("ext1", "0"));//集团标志
//		list=newsTemplateDao.paginate(dc.getExecutableCriteria(newsTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		dc.addOrder(Order.desc("createDate"));
		
		list=this.newsTemplateDao.executeCriteria(dc);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#getById(java.lang.Long)
	 */
	public NewsTemplate getById(Long id) {
		NewsTemplate template= newsTemplateDao.get(id);
		template.setCreateUserName(this.getNewsUtils().getMemberNameByUserId(template.getCreateUser()));
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.NewsTemplateManager#save(com.seeyon.v3x.news.domain.NewsTemplate)
	 */
	public NewsTemplate save(NewsTemplate template) throws NewsException {
			
		if(template.isNew()){
			template.setIdIfNew();
			template.setAccountId(CurrentUser.get().getLoginAccount());
			newsTemplateDao.save(template);
		}else{
			newsTemplateDao.update(template);
		}
		return template;
	}

	public NewsTypeDao getNewsTypeDao() {
		return newsTypeDao;
	}

	public void setNewsTypeDao(NewsTypeDao newsTypeDao) {
		this.newsTypeDao = newsTypeDao;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	
	
}
