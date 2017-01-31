package com.seeyon.v3x.news.manager;

import com.seeyon.v3x.news.domain.NewsType;

/**
 * 主要对新闻类型与新闻管理员、新闻发起员的关联进行操作
 * @author wolf
 *
 */
public interface NewsTypeManagersManager {

	/**
	 * 删除新闻类型的所有管理员和发起员，在删除新闻时调用
	 * @param type
	 */
//	public void deletes(NewsType type);
	
	/**
	 * 删除新闻类型的所有管理员，在保存新闻类型时调用
	 * @param type
	 */
//	public void deletesManager(NewsType type);
	
	/**
	 * 删除新闻类型的所有发起员，在配置新闻类型发起员时调用
	 * @param type
	 */
//	public void deletesWriter(NewsType type);
	
	/**
	 * 保存新闻类型的管理员
	 * @param type
	 * @param userIds 新闻管理员用户ID数组
	 */
//	public void saves(NewsType type,String[] userIds);
	
	/**
	 * 获取符合条件的新闻类型-新闻管理员关联列表；<b>注意，此处不返回发起员</b>
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception 
	 */
//	public List<NewsTypeManagers> findByProperty(String property,Object value) throws Exception;
	
	/**
	 * 根据新闻类型返回新闻类型-新闻发起员关联列表
	 * @param type
	 * @return
	 */
//	public List<NewsTypeManagers> findWriteByType(NewsType type);
	
	/**
	 * 保存新闻类型返回新闻类型-新闻发起员关联列表
	 * @param type
	 * @param writeIds
	 */
//	public void saveWriteByType(NewsType type,Long[] writeIds);
	
	/**
	 * 根据用户Id获取该用户可以发起的新闻类型-新闻发起员关联列表
	 * @param userId
	 * @return
	 */
//	public List<NewsTypeManagers> findTypeByWrite(Long userId);
	
//	public NewsTypeManagersDao getNewsTypeManagersDao();
	
//	public List<NewsTypeManagers> findTypeManagerId(Long typeId) throws Exception;
	
	/**
	 * 取得type集合的所有管理员
	 */
//	public List<NewsTypeManagers> getTypeManagers(List<NewsType> types);
	
	public void saveAclByType(NewsType type, String[][] userIds, String extFlag);
	
	/**
	 * 保存后台板块的管理员
	 * @param type
	 * @param userIds
	 * @param extFlag
	 */
	public void saveAclByTypeManager(NewsType type, String[] userIds, String extFlag);
}