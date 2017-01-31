package com.seeyon.v3x.main.phrase;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * 通用的Manager，只需要在XML配置之<br>
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-17
 */
public interface CommonPhraseManager {
	/**
	 * 插入一条数据
	 * 
	 * @param o
	 */
	public void save(CommonPhrase o) throws BusinessException;

	/**
	 * 按照主健读取纪录
	 * 
	 * @param id
	 * @return
	 */
	public CommonPhrase get(Long id) throws BusinessException;

	/**
	 * 修改数据
	 * 
	 * @param transientObject
	 */
	public void update(CommonPhrase transientObject) throws BusinessException;
	
	/**
	 * 根据id更新制定列的数据，不在需要先get然后update
	 * 
	 * @param column
	 *            key - 列名 value - 值，注意：值的类型必须和数据类型一致，否则异常
	 */
	public void update(Long id, java.util.Map<String, Object> columns) throws BusinessException;

	/**
	 * 删除
	 * 
	 * @param id
	 */
	public void delete(Long id) throws BusinessException;

	/**
	 * 查找所有
	 * 
	 * @return
	 */
	public List<CommonPhrase> getAll() throws BusinessException;

	/**
	 * 按照example查找
	 * 
	 * @param o
	 * @return
	 */
	public List<CommonPhrase> findByExample(CommonPhrase o) throws BusinessException;

	/**
	 * 查询，支持分页
	 * 
	 * @param detachedCriteria
	 * @return
	 */
	public Object executeCriteria(DetachedCriteria detachedCriteria);
	
	public List executeCriteria(DetachedCriteria detachedCriteria, int firstResult, int maxResults);
	
	/**
	 * 统计数量
	 * 
	 * @param detachedCriteria
	 * @return
	 */
	public int getCountByCriteria(DetachedCriteria detachedCriteria);

	/**
	 * 查询，不支持分页
	 * 
	 * @param queryString
	 * @param values
	 * @return
	 */
	public List find(String queryString, Object... values) throws BusinessException;
	
	/**
	 * 该方法只对集团版而言，每生成一个单位，复制一套常用语
	 *
	 */
	public void generateCommonPharse(long accountId);

}