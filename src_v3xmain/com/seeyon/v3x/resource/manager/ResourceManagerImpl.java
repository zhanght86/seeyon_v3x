package com.seeyon.v3x.resource.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.resource.dao.ResourceDao;
import com.seeyon.v3x.resource.dao.ResourceIppDao;
import com.seeyon.v3x.resource.domain.Resource;
import com.seeyon.v3x.resource.domain.ResourceIpp;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class ResourceManagerImpl implements ResourceManager { 
	
	private final static Log log = LogFactory.getLog(ResourceManagerImpl.class);
	
	private ResourceDao resourceDao;
	private ResourceIppDao resourceIppDao;

	public boolean isResourcesImpropriated(Long id, Date startTime, Date endTime) {
		boolean isIpp = false;
		Resource resource = getResourceByPk(id);
		Hibernate.initialize(resource.getResourceIpp());
		List rsIppLst = resource.getResourceIpp();
		if(ListUtils.EMPTY_LIST.equals(rsIppLst)){
			return false;
		}else{
			Iterator rsIpp = rsIppLst.iterator();
			while(rsIpp.hasNext()){
				ResourceIpp resourceIpp = (ResourceIpp)rsIpp.next();
				if(Datetimes.checkOverup(startTime, endTime, 
						resourceIpp.getStartTime(), resourceIpp.getEndTime(), true)) isIpp=true;
			}
		}
		return isIpp;
	}

	public boolean isResourcesImpropriated(Long id, Long meetingId, Date startTime, Date endTime) {
		boolean isIpp = false;
		Resource resource = getResourceByPk(id);
		Hibernate.initialize(resource.getResourceIpp());
		List rsIppLst = resource.getResourceIpp();
		if(ListUtils.EMPTY_LIST.equals(rsIppLst)){
			return false;
		}else{
			for(int i = 0; i<rsIppLst.size(); i++){
				ResourceIpp resourceIpp = (ResourceIpp)rsIppLst.get(i);
				// 是否是当前会议自己占用的资源
				boolean isMySelf = (resourceIpp.getRefAppId().equals(meetingId));
				if(!isMySelf) {
					// 时间段是否冲突
					boolean isCheckOverup = (Datetimes.checkOverup(startTime, endTime,resourceIpp.getStartTime(), resourceIpp.getEndTime(), true)) ;
					// 时间段内有这个资源，并且这个资源的所有者（会议ID） 和 当前修改的会议ID 不同，则认为这是一个被占用了的资源				
					if(isCheckOverup){
						isIpp=true;	
					}
				}
			}
		}
		return isIpp;
	}

	public String isResourcesImpropriated(String ids,Long meetingId, Date startTime, Date endTime) {
		// 返回结果 格式 “（del1或deln）|被占用的资源名字们” 如 “deln|会议室1\n会议室2\n” 表示选择的公共资源中有多个在刚才被管理员删除 并且 会议室1 会议室2 在该时间段内有人占用 
		String result = "|";
		
		// result 字符串中 “|” 前面的部分
		String resourcesDelNum = "";
		
		// result 字符串中 “|” 后面的部分
		String occupiedResourcesName = "";
		
		// 缺少的资源数目
		int delCount = 0;
		
		// 判断有没有选取 ids
		if(ids==null || "".equals(ids)){
			return result;
		}
		
		// 被占用的资源列表
		ArrayList<Resource> resourceList = new  ArrayList<Resource> ();
		
		// 拆分字符串成数组
		String [] arr_ids = {};
		if(ids!=null)
			arr_ids = ids.split(",");
		
		// 循环
		for (int i = 0; i < arr_ids.length; i++) {
			Long id = Long.parseLong(arr_ids[i]);
			Resource resource = null;
			// 因为使用的是 hibernate 的 load 方法， 按ID取，没有的时候会报 ObjectNotFoundException
			try{
				resource = getResourceByPk(id);
			}catch (Exception e) {
				resource = null;
			}
			// 如果改资源不存在，则说明就在刚刚被管理员删除了
			if(resource==null){
				delCount++; // 缺少的资源数目++
			}else{
				// 排除可能是自己占用的资源 排除后如果还被占用则添加到占用列表
				if(isResourcesImpropriated(id,meetingId,startTime,endTime)){
					resourceList.add(resource);
				}
			}
		}
		// 处理就在刚刚编辑的时候，被管理员删除的资源
		if(delCount==0){
		} else if(delCount==1){
			resourcesDelNum="del1";
		} else if(delCount>1){
			resourcesDelNum="deln";
		}
		// 被占用的资源的名字们
		for (int i = 0 ; i < resourceList.size() ; i++){
			occupiedResourcesName += (resourceList.get(i).getName() + "\n");
		}
		
		return resourcesDelNum+result+occupiedResourcesName;
	}
	public void addResource(Resource resource) {
		resource.setIdIfNew();
		resourceDao.save(resource);
	}

	public void deleteResource(Long id) {
		resourceDao.delete(id.longValue());
	}

	public void deleteResources(Long[] ids) {
		resourceDao.delete(ids);
	}

	public Resource getResourceByPk(Long id) {		
		return resourceDao.findByPrimaryKey(id);
	}

	public List listResource() {
		return resourceDao.list();
	}

	public void updateResource(Resource resource) {
		resourceDao.update(resource);
	}
	
	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	public void impropriateResources(Long resourceid, Long appId, Date startTime, Date endTime) {
		Resource resource = getResourceByPk(resourceid);
		ResourceIpp resourceIpp = new ResourceIpp();
		resourceIpp.setStartTime(startTime);
		resourceIpp.setEndTime(endTime);
		resourceIpp.setResource(resource);
		resourceIpp.setRefAppId(appId);
		resourceIpp.setIdIfNew();
		resourceIppDao.save(resourceIpp);
	}

	public void setResourceIppDao(ResourceIppDao resourceIppDao) {
		this.resourceIppDao = resourceIppDao;
	}

	public List findResourcesByType(String type) {
		return resourceDao.findByType(type);
	}

	public void delResourceIppByAppId(Long appId) {
		resourceIppDao.delByAppId(appId);
	}

	public void updateImpropriateResources(Long resourceId, Long appId, Date date, Date date2) {
		Resource resource = getResourceByPk(resourceId);
		List rsIpps = resource.getResourceIpp();
		Iterator it = rsIpps.iterator();
		while(it.hasNext()){
			ResourceIpp rsIpp = (ResourceIpp)it.next();
			if(rsIpp.getRefAppId().equals(appId)){
				resourceIppDao.delete(rsIpp.getId().longValue());
			}
		}
		ResourceIpp resourceIpp = new ResourceIpp();
		resourceIpp.setStartTime(date);
		resourceIpp.setEndTime(date2);
		resourceIpp.setResource(resource);
		resourceIpp.setRefAppId(appId);
		resourceIpp.setIdIfNew();
		resourceIppDao.save(resourceIpp);		
	}

	public List listResourceForPage() {
		return resourceDao.listByDomainId();
	}

	public boolean isNowResourcesImpropriated(Long id, Date nowTime) {
		boolean isIpp = false;
		Resource resource = getResourceByPk(id);
		Hibernate.initialize(resource.getResourceIpp());
		List rsIppLst = resource.getResourceIpp();
		if(ListUtils.EMPTY_LIST.equals(rsIppLst)){
			return false;
		}else{
			Iterator rsIpp = rsIppLst.iterator();
			while(rsIpp.hasNext()){
				ResourceIpp resourceIpp = (ResourceIpp)rsIpp.next();
				if(Datetimes.between(nowTime,resourceIpp.getStartTime(), resourceIpp.getEndTime(), false)) isIpp=true;
			}
		}
		return isIpp;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isResourcesUsed(Long resourceId) {
		String hql = " from MtResources m where m.resourceId=?";
		List l = resourceDao.find(hql, -1, -1, null, resourceId);
		return l.size() > 0 ? true : false;
	}
	
	/**
	 * 该方法只对集团版而言，每生成一个单位，复制一套公共资源
	 *
	 */
	public void generateResource(long accountId){
	
		log.info("开始为新单位初始化公共资源...");
		try{
		DetachedCriteria criteria = DetachedCriteria.forClass(Resource.class);
		criteria.add(Expression.eq("accountId", V3xOrgEntity.VIRTUAL_ACCOUNT_ID));
	
		List<Resource> list = (List<Resource>)resourceDao.executeCriteria(criteria, -1, -1);

		for(Resource resource : list){
			Resource newResource = new Resource();
			newResource.setIdIfNew();
			newResource.setAccountId(accountId);
			newResource.setDescription(resource.getDescription());
			newResource.setName(resource.getName());
			newResource.setResource(resource.getResource());
			newResource.setType(resource.getType());
			
			resourceDao.save(newResource);
		}	
		}catch(Exception e){
			log.error("初始化公共资源失败!",e);
		}
		log.info("成功为新单位初始化公共资源!");
	}
	
	/**
	 * 检查是否有重复名称的公共资源
	 * @param name : 公共资源的名称
	 * @param type : 公共资源的类型
	 * @return boolean : 返回是否重名 true-yes;false-no
	 */
	public boolean checkDuplicatedName(String name, String type){
		User user = CurrentUser.get();
		boolean bool = false;		
		DetachedCriteria criteria = DetachedCriteria.forClass(Resource.class);
		criteria.add(Expression.eq("name", name));
		criteria.add(Expression.eq("accountId", user.getLoginAccount()));
		if(!Strings.isBlank(type)){
			criteria.add(Expression.eq("type", type));
		}
		List<Resource> list = (List<Resource>)resourceDao.executeCriteria(criteria);
		if(null!=list && list.size()>0){
			bool = true;
		}
		return bool;
	}
	
	/**
	 * 更新与会资源占用情况
	 */
	public void saveOrUpdateImpropriateResources4Meeting(MtMeetingCAP meeting, String oper) {
		String resourceIds = meeting.getResourcesId();
		if (resourceIds != null) {
			String[] ids = resourceIds.split(",");
			for (String id : ids) {
				if (Strings.isNotBlank(id)) {
					if("save".equals(oper)){
						this.impropriateResources(Long.parseLong(id), meeting.getId(), 
								Datetimes.parseDatetime(Datetimes.format(meeting.getBeginDate(), "yyyy-MM-dd HH:mm:ss")), 
								Datetimes.parseDatetime(Datetimes.format(meeting.getEndDate(), "yyyy-MM-dd HH:mm:ss")));
					}else{
						this.updateImpropriateResources(Long.parseLong(id), meeting.getId(), 
								Datetimes.parseDatetime(Datetimes.format(meeting.getBeginDate(), "yyyy-MM-dd HH:mm:ss")), 
								Datetimes.parseDatetime(Datetimes.format(meeting.getEndDate(), "yyyy-MM-dd HH:mm:ss")));
					}
					
				}
			}
		}
	}

}
