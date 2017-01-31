package com.seeyon.v3x.meeting.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.dao.MtTemplateDao;
import com.seeyon.v3x.meeting.domain.MtResources;
import com.seeyon.v3x.meeting.domain.MtTemplate;
import com.seeyon.v3x.meeting.domain.MtTemplateUser;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

/**
 * 会议的Manager的实现类
 * @author wolf
 *
 */
public class MtTemplateManagerImpl extends BaseMeetingManager implements MtTemplateManager {
	private MtTemplateDao MtTemplateDao;
	private AttachmentManager attachmentManager;	
	private FileManager fileManager;
	private MtResourcesManager resourcesManager;
	private OrgManager orgManager;
	private MtTemplateUserManager mtTemplateUserManager;
	
	public void setMtTemplateUserManager(MtTemplateUserManager mtTemplateUserManager){
		this.mtTemplateUserManager = mtTemplateUserManager;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public MtTemplateDao getMtTemplateDao() {
		return MtTemplateDao;
	}
	
	public void setMtTemplateDao(MtTemplateDao MtTemplateDao) {
		this.MtTemplateDao = MtTemplateDao;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#delete(java.lang.Long)
	 */
	@SuppressWarnings("deprecation")
	public void delete(Long id) throws BusinessException {
		MtTemplate template=this.getById(id);		
		//如果会议格式为Word和Excel，则需要删除正文附件
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(template.getDataFormat())
			|| com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(template.getDataFormat())	
			){
			try {
				//attachmentManager.deleteByReference(template.getId(), template.getId());
				fileManager.deleteFile(template.getId(),template.getCreateDate(), true);
			} catch (BusinessException e) {
				throw e;
			}
		}
		
		//删除附件
		
		try {
			attachmentManager.deleteByReference(template.getId(), template.getId());
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		}

		MtTemplateDao.getSessionFactory().getCurrentSession().flush();
		MtTemplateDao.deleteObject(template);
		MtTemplateDao.bulkUpdate("delete from MtResources mr where mr.meetingId=?", null, id);
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#deletes(java.util.List)
	 */
	public void deletes(List<Long> ids) throws BusinessException {
		for(Long id:ids){
			delete(id);
		}
	}

	/**
	 * 初始化会议列表
	 * @param list
	 */
	private void initList(List<MtTemplate> list){
		for(MtTemplate template:list){
			initTemplate(template);
		}
	}
	
	/**
	 * 初始化与会人员的中文显示名称
	 * @param data
	 */
	private void initPublishScope(MtTemplate data) {
		//初始化发布范围
		String ids=data.getConferees();
		String names="";
		if(StringUtils.isNotBlank(ids)){
			String[] idA=ids.split(",");		
			
			for(String idStr:idA){
				if(Constants.IS_TEST){
					if(idStr.indexOf("|")>-1) idStr=idStr.substring(idStr.indexOf("|")+1);
				}
				names=names+","+this.getMeetingUtils().getMemberNameByUserId(Long.valueOf(idStr));				
			}
			data.setConfereesNames(ids.length()>0?names.substring(1):"");
		}
	}

	/**
	 * 初始化会议 1、初始化创建用户姓名 
	 * @param template
	 */
	private void initTemplate(MtTemplate template) {
		template.setAttachmentsFlag(attachmentManager.hasAttachments(template.getId(), template.getId()));
		
		template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
		template.setEmceeName(this.getMeetingUtils().getMemberNameByUserId(template.getEmceeId()));
		template.setRecorderName(this.getMeetingUtils().getMemberNameByUserId(template.getRecorderId()));
		this.initPublishScope(template);
		for(MtTemplateUser tu:template.getTemplateUsers()){
			tu.setOrgEntity(this.getMeetingUtils().getOrgEntityById(tu.getAuthType()+"|"+String.valueOf(tu.getAuthId())));
		}
		/*
		//判断会议状态
		if(template.getState()<Constants.DATA_STATE_SUMMARY && template.getState()>Constants.DATA_STATE_SAVE){
			Calendar cal=Calendar.getInstance();
			cal.setTime(new Date());
			Calendar beginCal=Calendar.getInstance();
			beginCal.setTime(template.getBeginDate());
			Calendar endCal=Calendar.getInstance();
			endCal.setTime(template.getEndDate());
			
			if(cal.after(beginCal) && cal.before(endCal) && template.getState()!=Constants.DATA_STATE_START){
				template.setState(Constants.DATA_STATE_START);
				MtTemplateDao.update(template);
			}else if(cal.after(endCal) && template.getState()!=Constants.DATA_STATE_FINISH){
				template.setState(Constants.DATA_STATE_FINISH);
				MtTemplateDao.update(template);
			}
		}
		*/
		//设置与会资源
		String resourceIds="";
		String resourceNames="";
		List<MtResources> resourceList=resourcesManager.findByProperty("meetingId", template.getId());
		for(MtResources resource:resourceList){
			resourceIds=resourceIds+resource.getResourceId()+",";
			resourceNames=resourceNames+this.getMeetingUtils().getMeetingProjectName(resource.getResourceId())+",";
		}
		template.setResourcesId(resourceIds);
		template.setResourcesName(resourceNames);
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#findAll()
	 * type 0 代表个人模版 
	 */
	@SuppressWarnings("unchecked")
	public List<MtTemplate> findAll(String type) {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplate.class);
		dc.add(Restrictions.eq("templateType", type));
		if(type.equals(Constants.MEETING_TEMPLATE_TYPE_PERSON)){
			dc.add(Restrictions.eq("createUser", Long.valueOf(CurrentUser.get().getId())));
		}else{
			dc.add(Restrictions.eq("accountId", accountId));
		}
		dc.addOrder(Order.asc("createDate"));
		List<MtTemplate> list=MtTemplateDao.executeCriteria(dc);
		initList(list);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<MtTemplate> findAllWithoutInit(String type) {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplate.class);
		dc.add(Restrictions.eq("templateType", type));
		if(type.equals(Constants.MEETING_TEMPLATE_TYPE_PERSON)){
			dc.add(Restrictions.eq("createUser", Long.valueOf(CurrentUser.get().getId())));
		}else{
			dc.add(Restrictions.eq("accountId", accountId));
		}
		dc.addOrder(Order.asc("createDate"));
		List<MtTemplate> list=MtTemplateDao.executeCriteria(dc);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.meeting.manager.MtTemplateManager#findAllNoPaginate(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<MtTemplate> findAllNoPaginate(String type) {
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplate.class);
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		String authStr = CurrentUser.get().getId()+",";
		
		try {
			String authId = orgManager.getUserIDDomain(user.getId() , "Department" );
			authStr+=authId;
		} catch (BusinessException e1) {
		}
		
		       
//		增加授权过滤，系统模板根据授权过滤，个人模板根据当前用户过滤
		dc.add(Restrictions.eq("templateType", type));
		if(type.equals(Constants.MEETING_TEMPLATE_TYPE_PERSON)){
			dc.add(Restrictions.eq("createUser", CurrentUser.get().getId()));
		}else{
			dc.add(Restrictions.eq("accountId", accountId));
		}
		dc.addOrder(Order.asc("createDate"));
		List<MtTemplate> list=MtTemplateDao.getHibernateTemplate().findByCriteria(dc);
		if(type.equals(Constants.MEETING_TEMPLATE_TYPE_SYSTEM)){
			for(int i = 0 ; i < list.size() ; i++){
				Long tempId = list.get(i).getId();
//				判断当前人员是否在授权范围内，通过人员id,和部门id判断
				StringBuilder hql = new StringBuilder("from MtTemplateUser mtu where ( mtu.authId in ( ");
				hql.append(authStr);
				hql.append(" ) ) and mtu.template.id = ? ");
				
				List<MtTemplateUser> mtu = MtTemplateDao.getHibernateTemplate().find(hql.toString(),new Object[]{tempId});
				
				
				if(mtu==null||mtu.size()==0){
					list.remove(i);
					i--;
				}
			}
		}
		initList(list);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List<MtTemplate> findByProperty(String type,String property, Object value) {
		List<MtTemplate> list;
		list=findByPropertyNoInit(type,property, value);
		initList(list);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#findByPropertyNoInit(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtTemplate> findByPropertyNoInit(String type,String property, Object value) {
		List<MtTemplate> list;
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplate.class);
		dc.add(Restrictions.eq("templateType", type));
		if(type.equals(Constants.MEETING_TEMPLATE_TYPE_PERSON)){
			dc.add(Restrictions.eq("createUser", Long.valueOf(CurrentUser.get().getId())));
		}else{
			dc.add(Restrictions.eq("accountId", accountId));
		}
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		dc.addOrder(Order.asc("createDate"));
		list=MtTemplateDao.executeCriteria(dc);
		return list;
	}
	
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.meeting.manager.MtTemplateManager#findByPropertyNoInitNoPaginate(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public List<MtTemplate> findByPropertyNoInitNoPaginate(String type,String property, Object value) {
		List<MtTemplate> list;
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplate.class);
		dc.add(Restrictions.eq("templateType", type));
		
		if(!type.equals("1")){
			dc.add(Restrictions.eq("createUser", CurrentUser.get().getId()));
		}
		if(value instanceof String){			
			dc.add(Restrictions.like(property, (String)value, MatchMode.ANYWHERE));
		}else{
			dc.add(Restrictions.eq(property, value));
		}
		dc.addOrder(Order.asc("createDate"));
		list=MtTemplateDao.getHibernateTemplate().findByCriteria(dc);
		return list;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#getById(java.lang.Long)
	 */
	public MtTemplate getById(Long id) {
		MtTemplate template= MtTemplateDao.get(id);
		if(template==null){
			return null;
		}
		//template.setCreateUserName(this.getMeetingUtils().getMemberNameByUserId(template.getCreateUser()));
		this.initTemplate(template);
		return template;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.news.manager.MtTemplateManager#save(com.seeyon.v3x.news.domain.MtTemplate)
	 */
	public MtTemplate save(MtTemplate template) throws MeetingException {
//		检查该名称的会议总结类型是否已经存在，如果存在，则提示用户     2008 3-13  放到前台用ajax校验
//		if(MtTemplateDao.isNotUnique(template, "title,templateType,createUser")){
//			throw new MeetingException("news_alreay_exists",template.getTitle());
//		}
			
		if(template.isNew()){
			template.setIdIfNew();
			MtTemplateDao.save(template);
		}else{
			MtTemplateDao.update(template);
		}
		MtTemplateDao.getHibernateTemplate().flush();
		
		List<MtResources> resourceList=resourcesManager.findByProperty("meetingId", template.getId());
		for(MtResources resource:resourceList){
			try {
				resourcesManager.delete(resource.getId());
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
				
		String resourceIds=template.getResourcesId();
		if(resourceIds!=null){
			String[] ids=resourceIds.split(",");
			for(String id:ids){
				if(id!=null && !id.trim().equals("")){
					MtResources resource=new MtResources();
					resource.setBeginDate(template.getBeginDate());
					resource.setEndDate(template.getEndDate());
					resource.setMeetingId(template.getId());
					resource.setResourceId(Long.valueOf(id));
					resource.setReserveFlag(false);
					resource.setUserId(CurrentUser.get().getId());
					try {
						resourcesManager.save(resource);
					} catch (BusinessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return template;
	}

	public MtTemplate save(MtTemplate template,String authInfo) throws MeetingException {
//		检查该名称的会议总结类型是否已经存在，如果存在，则提示用户     2008 3-13  放到前台用ajax校验
//		if(MtTemplateDao.isNotUnique(template, "title,templateType,createUser")){
//			throw new MeetingException("news_alreay_exists",template.getTitle());
//		}
			
		// 保存模板信息
		if(template.isNew()){
			template.setIdIfNew();
			MtTemplateDao.save(template);
		}else{
			MtTemplateDao.update(template);
		}
		MtTemplateDao.getHibernateTemplate().flush();
		
		List<MtResources> resourceList=resourcesManager.findByProperty("meetingId", template.getId());
		for(MtResources resource:resourceList){
			try {
				resourcesManager.delete(resource.getId());
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
				
		String resourceIds=template.getResourcesId();
		if(resourceIds!=null){
			String[] ids=resourceIds.split(",");
			for(String id:ids){
				if(id!=null && !id.trim().equals("")){
					MtResources resource=new MtResources();
					resource.setBeginDate(template.getBeginDate());
					resource.setEndDate(template.getEndDate());
					resource.setMeetingId(template.getId());
					resource.setResourceId(Long.valueOf(id));
					resource.setReserveFlag(false);
					resource.setUserId(CurrentUser.get().getId());
					try {
						resourcesManager.save(resource);
					} catch (BusinessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// 保存模板授权信息----------------------------------------
		ArrayList<MtTemplate> templates = new ArrayList<MtTemplate>();
		templates.add(template);
		mtTemplateUserManager.configUser(templates, authInfo);
		return template;
	}

	@SuppressWarnings("unchecked")
	public List<MtTemplate> findAllTempNoPaginate(String type){
		DetachedCriteria dc=DetachedCriteria.forClass(MtTemplate.class);
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		dc.add(Restrictions.eq("templateType", type));
		dc.add(Restrictions.eq("accountId", accountId));
		List<MtTemplate> list = MtTemplateDao.getHibernateTemplate().findByCriteria(dc);
		return list;
	}
	
	public boolean isMeetTempUnique(String tempName,Long tempId){
//		boolean isUnique = false;
		//yangzd  解决个人模板名称重复的问题。
		StringBuilder hql = new StringBuilder(" from MtTemplate temp where temp.title= ? ");
		hql.append(" and temp.createUser = ? ");
		List<MtTemplate> mtTempList = MtTemplateDao.find(hql.toString(), new Object[]{tempName,CurrentUser.get().getId()});
	//	List<MtTemplate> mtTempList = MtTemplateDao.find("from MtTemplate temp where temp.title='"+tempName+"' and temp.createUser="+CurrentUser.get().getId());
//		List<MtTemplate> mtTempList = MtTemplateDao.find("from MtTemplate temp where temp.title='"+tempName+"' and temp.id!="+tempId+" and temp.createUser="+CurrentUser.get().getId());
		return mtTempList.size() == 0 ? true : false;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setResourcesManager(MtResourcesManager resourcesManager) {
		this.resourcesManager = resourcesManager;
	}

	public void update(long templateId, Map<String, Object> colums) {
		this.MtTemplateDao.update(templateId, colums);
	}

	
	
}
