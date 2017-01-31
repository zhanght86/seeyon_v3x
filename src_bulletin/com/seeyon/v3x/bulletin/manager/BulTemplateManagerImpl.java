package com.seeyon.v3x.bulletin.manager;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.dao.BulTemplateDao;
import com.seeyon.v3x.bulletin.dao.BulTypeDao;
import com.seeyon.v3x.bulletin.domain.BulTemplate;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.news.domain.NewsTemplate;

/**
 * 公告版面的Manager的实现类
 * @author wolf
 *
 */
public class BulTemplateManagerImpl extends BaseBulletinManager implements BulTemplateManager {
	private BulTemplateDao bulTemplateDao;
	private BulTypeDao bulTypeDao;
	
	private FileManager fileManager;

	public BulTemplateDao getBulTemplateDao() {
		return bulTemplateDao;
	}
	
	public void setBulTemplateDao(BulTemplateDao bulTemplateDao) {
		this.bulTemplateDao = bulTemplateDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#delete(java.lang.Long)
	 */
	public void delete(Long id) throws BusinessException {
		BulTemplate template=this.getById(id);		
		//如果公告版面格式为Word和Excel，则需要删除正文附件
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(template.getTemplateFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(template.getTemplateFormat())
			){
			try {
				fileManager.deleteFile(template.getId(),template.getCreateDate(), true);
			} catch (BusinessException e) {
				throw e;
			}
		}
		//删除其他公告类型对该版面的引用
		for(BulType type:template.getBulTypes()){
			type.setDefaultTemplate(null);
			bulTypeDao.save(type);
		}
		bulTemplateDao.getSessionFactory().getCurrentSession().flush();
		
		bulTemplateDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 初始化公告版面列表
	 * @param list
	 */
	private void initList(List<BulTemplate> list){
		for(BulTemplate template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化公告版面 1、初始化创建用户姓名 2、初始化引用该公告版面的公告类型列表
	 * @param template
	 */
	private void initTemplate(BulTemplate template) {
		template.setCreateUserName(this.getBulletinUtils().getMemberNameByUserId(template.getCreateUser()));
		
		if(template.getBulTypes()==null || template.getBulTypes().size()==0) {  
			template.setTypeNames("");
		} else {
			StringBuffer typeNamesSb = new StringBuffer();
			for(BulType type:template.getBulTypes()) {
				typeNamesSb.append(","+type.getTypeName());
			}			
			template.setTypeNames(typeNamesSb.toString().substring(1));
		}
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<BulTemplate> findAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(BulTemplate.class);
		//添加集团化处理
		dc.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
		dc.addOrder(Order.desc("createDate"));		
		List<BulTemplate> list=this.bulTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}
	/* (non-Javadoc)集团公告版面所有
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<BulTemplate> findGroupAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(BulTemplate.class);
		//添加集团化处理
		dc.add(Restrictions.eq("ext1", "0"));
//		List<BulTemplate> list=bulTemplateDao.paginate(dc.getExecutableCriteria(bulTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		dc.addOrder(Order.desc("createDate"));
		
		List<BulTemplate> list=this.bulTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<BulTemplate> findByProperty(String property, Object value) {
		List<BulTemplate> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<BulTemplate> findGroupByProperty(String property, Object value) {
		List<BulTemplate> list;
		list=findGroupByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<BulTemplate> findByPropertyNoInit(String property, Object value) {
		List<BulTemplate> list;
		DetachedCriteria dc=DetachedCriteria.forClass(BulTemplate.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		//添加集团化处理
		dc.add(Restrictions.eq("accountId", CurrentUser.get().getLoginAccount()));
//		list=bulTemplateDao.paginate(dc.getExecutableCriteria(bulTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		dc.addOrder(Order.desc("createDate"));
		
		list=this.bulTemplateDao.executeCriteria(dc);
		return list;
	}
	/* (non-Javadoc)集团版面查询
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<BulTemplate> findGroupByPropertyNoInit(String property, Object value) {
		List<BulTemplate> list;
		DetachedCriteria dc=DetachedCriteria.forClass(BulTemplate.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		//添加集团化处理
		dc.add(Restrictions.eq("ext1", "0"));
//		list=bulTemplateDao.paginate(dc.getExecutableCriteria(bulTemplateDao.getSessionFactory().getCurrentSession()),Order.asc("createDate"));
		dc.addOrder(Order.desc("createDate"));
		
		list=this.bulTemplateDao.executeCriteria(dc);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#getById(java.lang.Long)
	 */
	public BulTemplate getById(Long id) {
		BulTemplate template= bulTemplateDao.get(id);
		template.setCreateUserName(this.getBulletinUtils().getMemberNameByUserId(template.getCreateUser()));
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.bulletin.manager.BulTemplateManager#save(com.seeyon.v3x.bulletin.domain.BulTemplate)
	 */
	public BulTemplate save(BulTemplate template) throws BulletinException {

			
		if(template.isNew()){
			template.setIdIfNew();
			template.setAccountId(CurrentUser.get().getLoginAccount());
			bulTemplateDao.save(template);
		}else{
			bulTemplateDao.update(template);
		}
		return template;
	}

	public BulTypeDao getBulTypeDao() {
		return bulTypeDao;
	}

	public void setBulTypeDao(BulTypeDao bulTypeDao) {
		this.bulTypeDao = bulTypeDao;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	
	
}
