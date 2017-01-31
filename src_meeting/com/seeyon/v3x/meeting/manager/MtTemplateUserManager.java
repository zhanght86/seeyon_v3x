package com.seeyon.v3x.meeting.manager;

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.domain.MtTemplate;
import com.seeyon.v3x.meeting.domain.MtTemplateUser;

/**
 * 会议模板权限的Manager接口
 * @author wolf
 *
 */
public interface MtTemplateUserManager {

	/**
	 * 保存会议模板权限
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtTemplateUser save(MtTemplateUser template) throws BusinessException;
	
	/**
	 * 删除会议模板权限
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除会议模板权限
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有会议模板权限，支持分页
	 * @return
	 */
	public List<MtTemplateUser> findAll();
	
	/**
	 * 查询符合条件的会议模板权限列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtTemplateUser> findByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的会议模板权限列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtTemplateUser> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 根据版面Id获取会议模板权限
	 * @param id
	 * @return
	 */
	public MtTemplateUser getById(Long id);
	
	public void configUser(List<MtTemplate> templates,String authInfo) throws MeetingException;
}