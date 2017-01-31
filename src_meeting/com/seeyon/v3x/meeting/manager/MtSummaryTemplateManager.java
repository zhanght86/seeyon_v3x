package com.seeyon.v3x.meeting.manager;

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.domain.MtSummaryTemplate;

/**
 * 会议总结版面的Manager接口
 * @author wolf
 *
 */
public interface MtSummaryTemplateManager {

	/**
	 * 保存会议总结版面
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtSummaryTemplate save(MtSummaryTemplate template) throws BusinessException;
	
	/**
	 * 删除会议总结版面
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除会议总结版面
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有会议总结版面，支持分页
	 * @return
	 */
	public List<MtSummaryTemplate> findAll();
	
	/**
	 * 查询符合条件的会议总结版面列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtSummaryTemplate> findByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的会议总结版面列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtSummaryTemplate> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 根据版面Id获取会议总结版面
	 * @param id
	 * @return
	 */
	public MtSummaryTemplate getById(Long id);
	
	/**
	 * 判断会议总结是否存在   总结转发协同时调用
	 * @param id
	 * @return
	 */
	public boolean isMeetingSummaryExist(Long id);
	
}