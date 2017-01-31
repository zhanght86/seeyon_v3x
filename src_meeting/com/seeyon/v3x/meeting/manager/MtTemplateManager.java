package com.seeyon.v3x.meeting.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.cap.meeting.domain.MtTemplateCAP;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.meeting.domain.MtTemplate;

/**
 * 会议模板的Manager接口
 * @author wolf
 *
 */
public interface MtTemplateManager {

	/**
	 * 保存会议模板
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtTemplate save(MtTemplate template) throws BusinessException;
	
	/**
	 * 保存会议模板
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public MtTemplate save(MtTemplate template,String authId) throws BusinessException;
	
	/**
	 * 删除会议模板
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除会议模板
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有会议模板，支持分页
	 * @return
	 */
	public List<MtTemplate> findAll(String type);
	
	/**
	 * 查询所有会议模板，支持分页，不需要初始化
	 * @param type
	 * @return
	 */
	public List<MtTemplate> findAllWithoutInit(String type);
	
	/**
	 * 查询符合条件的会议模板列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtTemplate> findByProperty(String type,String property,Object value);
	
	/**
	 * 查询符合条件的会议模板列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtTemplate> findByPropertyNoInit(String type,String property, Object value) ;
	
	/**
	 * 根据版面Id获取会议模板
	 * @param id
	 * @return
	 */
	public MtTemplate getById(Long id);
	
	/**
	 * 查询符合条件的会议模板列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	List<MtTemplate> findByPropertyNoInitNoPaginate(String type, String property, Object value);
	/**
	 * 查询所有会议模板，不支持分页
	 * @return
	 */
	List<MtTemplate> findAllNoPaginate(String type);
	
	/**
	 * 查询所有会议模板，不支持分页
	 * @return
	 */
	List<MtTemplate> findAllTempNoPaginate(String type);
	
	public boolean isMeetTempUnique(String tempName,Long tempId);
	
	public void update(long templateId, Map<String, Object> colums);
}