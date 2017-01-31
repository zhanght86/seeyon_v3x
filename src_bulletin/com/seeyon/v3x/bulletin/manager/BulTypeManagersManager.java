package com.seeyon.v3x.bulletin.manager;

import com.seeyon.v3x.bulletin.domain.BulType;

/**
 * 主要对公告类型与公告管理员、公告发起员的关联进行操作
 * @author wolf
 *
 */
public interface BulTypeManagersManager {

	/**
	 * 删除公告类型的所有管理员和发起员，在删除公告时调用
	 * @param type
	 */
//	public void deletes(BulType type);
	
	/**
	 * 删除公告类型的所有管理员，在保存公告类型时调用
	 * @param type
	 */
//	public void deletesManager(BulType type);
	
	/**
	 * 删除公告类型的所有发起员，在配置公告类型发起员时调用
	 * @param type
	 */
//	public void deletesWriter(BulType type);
	
	/**
	 * 保存公告类型的管理员---授权
	 * @param type
	 * @param userIds 公告管理员用户ID数组
	 */
//	public void saves(BulType type,String[] userIds);
	public void saveAclByType(BulType type, String[][] userIds, String extFlag);
	
	
	/**
	 * 保存公告类型的管理员
	 * @param type
	 * @param userIds 公告管理员用户ID数组
	 */
//	public void saves(BulType type,String[] userIds);
	public void saveAclByTypeManager(BulType type, String[] userIds, String extFlag);
	
	/**
	 * 获取符合条件的公告类型-公告管理员关联列表；<b>注意，此处不返回发起员</b>
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception 
	 */
//	public List<BulTypeManagers> findByProperty(String property,Object value) throws Exception;
	
	/**
	 * 根据公告类型返回公告类型-公告发起员关联列表
	 * @param type
	 * @return
	 */
//	public List<BulTypeManagers> findWriteByType(BulType type);
	
	/**
	 * 保存公告类型返回公告类型-公告发起员关联列表
	 * @param type
	 * @param writeIds
	 */
//	public void saveWriteByType(BulType type,Long[] writeIds);
	
	/**
	 * 根据用户Id获取该用户可以发起的公告类型-公告发起员关联列表
	 * @param userId
	 * @return
	 */
//	public List<BulTypeManagers> findTypeByWrite(Long userId);
	
//	public BulTypeManagersDao getBulTypeManagersDao();
	
	/**
	 * 得到 writeFlag 的BulTypeManagers
	 * 
	 */
//	public List<BulTypeManagers> findTypeManagerId(Long typeId) throws Exception;
	

}