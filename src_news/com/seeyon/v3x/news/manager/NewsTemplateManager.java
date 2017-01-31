package com.seeyon.v3x.news.manager;

import java.util.List;

import com.seeyon.v3x.news.domain.NewsTemplate;
import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * 新闻版面的Manager接口
 * @author wolf
 *
 */
public interface NewsTemplateManager {

	/**
	 * 保存新闻版面
	 * @param template
	 * @return
	 * @throws BusinessException
	 */
	public NewsTemplate save(NewsTemplate template) throws BusinessException;
	
	/**
	 * 删除新闻版面
	 * @param id
	 * @throws BusinessException 
	 */
	public void delete(Long id) throws BusinessException;
	
	/**
	 * 批量删除新闻版面
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有新闻版面，支持分页
	 * @return
	 */
	public List<NewsTemplate> findAll();
	/**
	 * 查询所有集团新闻版面，支持分页
	 * @return
	 */
	public List<NewsTemplate> findGroupAll();

	
	/**
	 * 查询符合条件的新闻版面列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<NewsTemplate> findByProperty(String property,Object value);
	/**
	 * 查询符合条件的集团新闻版面列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<NewsTemplate> findGroupByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的新闻版面列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<NewsTemplate> findByPropertyNoInit(String property, Object value) ;
	
	/**
	 * 根据版面Id获取新闻版面
	 * @param id
	 * @return
	 */
	public NewsTemplate getById(Long id);
	
}