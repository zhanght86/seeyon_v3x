package com.seeyon.v3x.bulletin.manager;

import java.util.List;

import com.seeyon.v3x.bulletin.domain.BulTemplate;
import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * 公告版面的Manager接口
 * @author wolf
 *
 */
public interface BulTemplateManager {

	/**
	 * 保存公告版面
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public BulTemplate save(BulTemplate template) throws BusinessException;
	
	/**
	 * 删除公告版面
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除公告版面
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有公告版面，支持分页
	 * @return
	 */
	public List<BulTemplate> findAll();
	/**
	 * 查询所有集团公告版面，支持分页
	 * @return
	 */
	public List<BulTemplate> findGroupAll();
	
	/**
	 * 查询符合条件的公告版面列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<BulTemplate> findByProperty(String property,Object value);
	/**
	 * 查询符合条件的集团公告版面列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<BulTemplate> findGroupByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的公告版面列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<BulTemplate> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 根据版面Id获取公告版面
	 * @param id
	 * @return
	 */
	public BulTemplate getById(Long id);
	
}