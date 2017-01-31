package com.seeyon.v3x.meeting.manager;

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtResources;

/**
 * 会议资源的Manager接口
 * @author wolf
 *
 */
public interface MtResourcesManager {

	/**
	 * 保存会议资源
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtResources save(MtResources template) throws BusinessException;
	
	/**
	 * 保存会议对应的与会资源
	 * @param meeting
	 */
	public void saveMtResources4Meeting(MtMeeting meeting);
	
	/**
	 * 删除会议资源
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除会议资源
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 删除会议对应的会议资源
	 * @param meetingId
	 */
	public void deleteByMeetingId(Long meetingId);
	
	/**
	 * 查询所有会议资源，支持分页
	 * @return
	 */
	public List<MtResources> findAll();
	
	/**
	 * 查询符合条件的会议资源列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtResources> findByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的会议资源列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtResources> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 根据版面Id获取会议资源
	 * @param id
	 * @return
	 */
	public MtResources getById(Long id);
	
}