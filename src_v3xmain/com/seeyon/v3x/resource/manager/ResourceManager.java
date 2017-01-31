package com.seeyon.v3x.resource.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.v3x.resource.dao.ResourceDao;
import com.seeyon.v3x.resource.domain.Resource;

public interface ResourceManager {

	/**
	 * 添加资源
	 * 
	 * @param template
	 *            资源对象
	 */
	public abstract void addResource(Resource resource);	
	
	/**
	 * 取回资源列表
	 * 
	 * @return 资源列表
	 */
	public abstract List listResource();
	
	/**
	 * 取回分页资源列表
	 * 
	 * @return 资源列表
	 */
	public abstract List listResourceForPage();
	
	/**
	 * 修改资源
	 * 
	 * @param template
	 *            资源对象
	 */
	public abstract void updateResource(Resource resource);	
	
	
	/**
	 * 通过主键取资源
	 * 
	 * @param id
	 *            主键
	 * @return 资源
	 */
	public abstract Resource getResourceByPk(Long id);
	
	/**
	 * 通过主键删除资源
	 * 
	 * @param id
	 *            主键
	 */
	public abstract void deleteResource(Long id);
	
	/**
	 * 通过主键数组删除资源
	 * 
	 * @param ids
	 *            主键数组
	 */
	public abstract void deleteResources(Long[] ids);
	
	/**
	 * 查询资源是否被占用
	 * 
	 * @param id
	 * @param startTime 使用开始时间
	 * @param endTime 使用结束时间           
	 */
	public abstract boolean isResourcesImpropriated(Long id, Date startTime, Date endTime);
	
	/**
	 * 编辑会议时，除了自己占用外，查询资源是否被其他会议占用
	 * 
	 * @param id 资源ID
	 * @param meetingId 当前编辑的会议ID
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public abstract boolean isResourcesImpropriated(Long id, Long meetingId, Date startTime, Date endTime);
	/**
	 * 查询资源是否被占用（编辑会议时，除了自己占用外，查询资源是否被其他会议占用）
	 * 
	 * @param ids ( 形如 11111,2222,3333,4444,5555 的多条ID拼成的字符串)
	 * @param startTime 使用开始时间
	 * @param endTime 使用结束时间           
	 */
	public abstract String isResourcesImpropriated(String ids,Long meetingId, Date startTime, Date endTime);
	
	/**
	 * 查询当前时间资源是否被占用
	 * 
	 * @param id
	 * @param nowTime 当前时间          
	 */
	public abstract boolean isNowResourcesImpropriated(Long id, Date nowTime);


	/**
	 * 占用资源
	 * 
	 * @param resourceId 资源id
	 * @param appId 占用者id
	 * @param date 使用开始时间
	 * @param date2 使用结束时间           
	 */
	public abstract void impropriateResources(Long resourceId,Long appId, Date date, Date date2);
	
	/**
	 * 更新资源占用
	 * 
	 * @param resourceId 资源id
	 * @param appId 占用者id
	 * @param date 使用开始时间
	 * @param date2 使用结束时间           
	 */
	public abstract void updateImpropriateResources(Long resourceId,Long appId, Date date, Date date2); 

	
	/**
	 * 根据资源类型查询资源列表
	 * 
	 * @param type 资源类型 office 办公场所 acc 办公用品 equipment 设备 car 车辆 data 资料  
	 *      
	 */	
	public abstract List findResourcesByType(String type);
	
	/**
	 * 根据占用者id删除资源占用时间
	 * 
	 * @param appId 占用者id          
	 */
	public abstract void delResourceIppByAppId(Long appId);
	
	public abstract void setResourceDao(ResourceDao resourceDao);
	
	
//	判断会议室和与会资源是否被占用
	public abstract boolean isResourcesUsed(Long resourceId);
	
	public void generateResource(long accountId);
	
	/**
	 * 判断同一类别下，资源名称是否被占用
	 * @param name
	 * @param type
	 * @return
	 */
	public boolean checkDuplicatedName(String name, String type);
	
		
	/**
	 * 更新与会资源占用情况
	 */
	public void saveOrUpdateImpropriateResources4Meeting(MtMeetingCAP meeting, String oper);
	
}
