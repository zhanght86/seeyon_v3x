package com.seeyon.v3x.meeting.manager;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtTemplateDao;
import com.seeyon.v3x.meeting.dao.MtTemplateUserDao;
import com.seeyon.v3x.meeting.domain.MtTemplate;
import com.seeyon.v3x.meeting.domain.MtTemplateUser;
import com.seeyon.v3x.util.Strings;

/**
 * 会议模板权限的Manager的实现类
 * @author wolf
 *
 */
public class MtTemplateUserManagerImpl extends BaseMeetingManager implements MtTemplateUserManager {
	
	private MtTemplateUserDao MtTemplateUserDao;
	
	private MtTemplateDao mtTemplateDao;
		
	public void setMtTemplateDao(MtTemplateDao mtTemplateDao) {
		this.mtTemplateDao = mtTemplateDao;
	}

	public MtTemplateUserDao getMtTemplateUserDao() {
		return MtTemplateUserDao;
	}
	
	public void setMtTemplateUserDao(MtTemplateUserDao MtTemplateUserDao) {
		this.MtTemplateUserDao = MtTemplateUserDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#delete(java.lang.Long)
	 */
	@SuppressWarnings("deprecation")
	public void delete(Long id) throws BusinessException {
		MtTemplateUserDao.delete(id);		
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 初始化会议模板权限列表
	 * @param list
	 */
	private void initList(List<MtTemplateUser> list){
		for(MtTemplateUser template:list){
			initTemplate(template);
		}
	}

	/**
	 * 初始化会议模板权限 1、初始化创建用户姓名 
	 * @param template
	 */
	private void initTemplate(MtTemplateUser template) {
		template.setOrgEntity(this.getMeetingUtils().getOrgEntityById(template.getAuthType()+"|"+String.valueOf(template.getAuthId())));
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<MtTemplateUser> findAll() {
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplateUser.class);
		List<MtTemplateUser> list=this.MtTemplateUserDao.getHibernateTemplate().findByCriteria(dc);
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<MtTemplateUser> findByProperty(String property, Object value) {
		List<MtTemplateUser> list;
		list=findByPropertyNoInit(property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtTemplateUser> findByPropertyNoInit(String property, Object value) {
		List<MtTemplateUser> list;
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplateUser.class);
		
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		list=this.getMtTemplateUserDao().getHibernateTemplate().findByCriteria(dc);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#getById(java.lang.Long)
	 */
	public MtTemplateUser getById(Long id) {
		MtTemplateUser template= MtTemplateUserDao.get(id);
		initTemplate(template);
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateUserManager#save(com.seeyon.v3x.news.domain.MtTemplateUser)
	 */
	public MtTemplateUser save(MtTemplateUser template) throws MeetingException {
		if(template.isNew()){
			template.setIdIfNew();
			MtTemplateUserDao.save(template);
		}else{
			MtTemplateUserDao.update(template);
		}
		return template;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void configUser(List<MtTemplate> templates,String authInfo) throws MeetingException{
		String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
		if(authInfos != null && authInfos.length > 0){
			for(MtTemplate template:templates){
				this.MtTemplateUserDao.delete(new Object[][]{{"template", template}});
			}			
			for(MtTemplate template:templates){
				int i = 0;
				for(String[] strings : authInfos){
					MtTemplateUser tu=new MtTemplateUser();
					tu.setAuthType(strings[0]);
					tu.setAuthId(Long.parseLong(strings[1]));
					tu.setSort(i);
					tu.setTemplate(template);
					this.save(tu);
				}
			}
			this.MtTemplateUserDao.getHibernateTemplate().flush();
		}else{
			for(MtTemplate template:templates){
				Set<MtTemplateUser> mtu = new HashSet<MtTemplateUser>();
				mtu = template.getTemplateUsers();
				if(mtu != null && mtu.size() > 0) {
					Iterator it = mtu.iterator();
					while(it.hasNext()){
						it.next();
						it.remove();
					}
				}
				template.setTemplateUsers(mtu);
				mtTemplateDao.save(template);
			}
		}
	}
}
