package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalEventTran;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 对事件的委托、安排、撤销进行操作
 * 
 * @author wolf
 * 
 */
public interface CalEventTranManager {
	
	/**
	 * 基础删除
	 * 
	 * @param eventTranId
	 */
	public void delete(Long eventTranId);
	
	/**
	 * 删除 ---根据事件id
	 * 
	 * @param eventId
	 */
	public void deleteByEventId(Long eventId);
	
	/**
	 * 删除---根据事件ID和事件类型
	 */
	public void deleteByEventId(Long eventId, Integer type);

	/**
	 * 删除 ---根据人员id和事件id
	 * 
	 * @param eventId
	 * @param userId
	 */
	public void deleteByEventAndUserId(Long eventId, Long userId);

	/**
	 * 基础保存
	 * 
	 * @param eventTran
	 */
	public void save(CalEventTran eventTran);

	/**
	 * 保存 ---事件的公开类型接受者 并 事件的委托安排的接受者 同时存在 公开类型为其他
	 * 
	 * @param event
	 * @param userIds  
	 * @param type
	 * @param ids
	 */
	public void saveTranEvents(CalEvent event, Long[] userIds, int type,
			Long[] ids);

	/**
	 * 保存 ---事件的公开类型接受者 并 事件的委托安排的接受者 同时存在 公开类型为项目
	 * 
	 * @param event
	 * @param userIds        
	 * @param type   
	 * @param ids        
	 */
	public void saveTranEvents(CalEvent event, Long userIds, int type,
			Long[] ids);

	/**
	 * 保存 ---只有事件的公开类型接受者存在
	 * 
	 * @param event
	 * @param userIds     
	 * @param type
	 */
	public void saveTranEvents(CalEvent event, Long[] userIds, int type);

	public void saveProjectTranEvents(CalEvent event, Long projectId, int type, String [] userIds);
	/**
	 * 保存 ---只有事件的委托和安排接受者存在
	 * 
	 * @param event
	 * @param userIds        
	 * @param type           
	 */
	public void saveTranEvents1(CalEvent event, Long[] userIds, int type);

	/**
	 * 保存 ---只有事件的公开类型为项目（从关联项目只能得到单纯的 id ）存在
	 * 
	 * @param event
	 * @param userIds    
	 * @param type
	 */
	public void saveTranEvents(CalEvent event, Long userIds, int type);

	/**
	 * 保存 ---事件的公开类型接受者 并 事件的委托安排的接受者 同时存在
	 * 
	 * @param event
	 * @param userIds      
	 * @param type
	 * @param reIds
	 */
	public void saveTranEvents(CalEvent event, String typeIds, int type,
			String reIds);

	/**
	 * 保存 ---事件的公开接受者 或是 事件委托安排接受者，只存在其中之一
	 * 
	 * @param event
	 * @param userIds     
	 * @param type
	 */
	public void saveTranEvents(CalEvent event, String typeIds, int type, boolean isFromTask);
	
	/**
	 * 根据事件id得到委托，安排，公开项目，公开个人，公开部门事件列表
	 * 
	 * @param eventId
	 */
	public List<CalEventTran> getEventTranListByEventId(Long eventId);

	/**
	 * 根据人员id得到委托，安排，公开项目，公开个人，公开部门事件列表
	 * 
	 * @param userId
	 */
	public List<CalEventTran> getEventTranListByUserId(Long userId);

	/**
	 * 根据事件id和人员id得到转发事件列表
	 * 
	 * @param eventId
	 * @param destUserId
	 */
	public List<CalEventTran> getEventTranListByEventAndUserId(Long eventId,
			Long destUserId);

	/**
	 * 获取总数
	 * 
	 */
	public int getTotal();
	
	/**
	 * 根据 接受者和创建者得到 CalEventTran List
	 * @param receiverId
	 * @param creatureId
	 * @return
	 */
	public List<CalEventTran> getEventTranListByRecIdAndCreatureId(Long receiverId,Long creatureId);
	
	public List<CalEventTran> getEventTranListByEntityIdAndCreatureId(Long creatureId, Long entityId);
	
	/**
	 * 根据 接受者的Id 得到 CalEventTran List
	 * @param recId
	 * @return
	 */
	public List<CalEventTran> getEventTranListByRecId(Long recId);
	
	/**
	 * 根据 实体Id 得到 CalEventTran List
	 * @param entityId
	 * @return
	 */
	public List<CalEventTran> getEventTranListByEntityId(Long entityId);

	/**
	 * 判断当前用户是否具有查看该事件的权限
	 * 
	 * @param eventId
	 * @param entitys
	 * @return
	 */
	public boolean validateCurrentUserIsCanViewEvent(Long eventId,List<V3xOrgEntity> entitys);
	
	
	/**
	 * 得到 公开类型有当前登录用户，并且被点击的用户为该事项的所属人
	 * @param currentId
	 * @param relationId
	 * @return
	 */
	List<CalEventTran> getEventTranListByCidAndByRid(Long currentId,Long relationId);
}