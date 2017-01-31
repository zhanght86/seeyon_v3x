package com.seeyon.v3x.system.signet.dao;

import com.seeyon.v3x.system.signet.domain.V3xSignet;

public interface SignetDao {
	// 获取全部信息
	public java.util.List<com.seeyon.v3x.system.signet.domain.V3xSignet> findAll();
	/**
	 * 按id取印章。
	 * @param id 印章Id。
	 * @return 印章对象。
	 */
	public V3xSignet getSignet(long id);
		
	// 添加印章数据
	public void create(com.seeyon.v3x.system.signet.domain.V3xSignet signet);

	// 修改数据
	public void update(com.seeyon.v3x.system.signet.domain.V3xSignet signet);

	// 删除数据
	public void delete(long id);
	
	public void deleteByAccountId(Long accountId);
}
