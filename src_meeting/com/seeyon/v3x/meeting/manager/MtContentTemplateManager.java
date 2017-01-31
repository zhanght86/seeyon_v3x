package com.seeyon.v3x.meeting.manager;

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;

/**
 * 会议正文版面的Manager接口
 * @author wolf
 *
 */
public interface MtContentTemplateManager {

	/**
	 * 保存会议正文版面
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtContentTemplate save(MtContentTemplate template) throws BusinessException;
	/**
	 * 保存常用格式正文版面
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtContentTemplate saveTemplate(MtContentTemplate template) throws BusinessException;
	/**
	 * 删除会议正文版面
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除会议正文版面
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有会议正文版面，支持分页
	 * @return
	 */
	public List<MtContentTemplate> findAll();
	
	/**
	 * 查询所有正文版面----按分类（会议计划公告新闻），支持分页
	 * @return
	 * @throws BusinessException 
	 */
	public List<MtContentTemplate> findTypeAll(String type) throws BusinessException;
	/**
	 * 查询所有正文版面----按分类（会议计划公告新闻），不支持分页
	 * @return
	 * @throws BusinessException 
	 */
	public List<MtContentTemplate> findTypeAllNoPage(String type) throws BusinessException;
	/**
	 * 查询集团所有正文版面----按分类（公告新闻），支持分页
	 * @return
	 * @throws BusinessException 
	 */
	public List<MtContentTemplate> findGroupTypeAll(String type) throws BusinessException;
	
	/**
	 * 查询符合条件的会议正文版面列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtContentTemplate> findByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的会议正文版面列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtContentTemplate> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 根据版面Id获取会议正文版面
	 * @param id
	 * @return
	 */
	public MtContentTemplate getById(Long id);

	public String checkDupleName(String tName)throws MeetingException;
	
	/**
	 * 批量保存内容格式
	 */
	public void saveAll(List<MtContentTemplate> mcts);
	
}