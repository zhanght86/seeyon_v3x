package com.seeyon.v3x.system.ipcontrol.manager;

import java.util.List;

import com.seeyon.v3x.system.ipcontrol.domain.V3xIpcontrol;

public interface IpcontrolManager {
	
	/**
	 * 添加访问控制
	 * @param ipcontrol
	 * @throws Exception
	 */
	public void save(V3xIpcontrol ipcontrol) throws Exception;
	/**
	 * 删除访问控制
	 * @param ids
	 */
	public void delete(List<Long> ids);
	/**
	 * 修改访问控制
	 * @param ipcontrol
	 * @throws Exception
	 */
	public void update(V3xIpcontrol ipcontrol) throws Exception;
	/**
	 * 获得所有数据
	 * @return List<V3xIpcontrol>
	 */
	public List<V3xIpcontrol> findAllIpcontrol();
	/**
	 * 根据ID获得数据
	 * @param id
	 * @return V3xIpcontrol
	 */
	public V3xIpcontrol getIpcontrol(Long id);
	/**
	 * 根据单位ID获得数据
	 * @param accountId
	 * @return List<V3xIpcontrol>
	 */
	public List<V3xIpcontrol> findIpcontrolByAccount(Long accountId);
	/**
	 * 初始化不限制IP访问控制信息
	 */
	public void initNoLimitIp();
	/**
	 * 初始化限制IP访问控制信息
	 */
	public void initLimitIp();
	/**
	 * 条件查询
	 * @param name
	 * @param type
	 * @param accountId
	 * @return List<V3xIpcontrol>
	 */
	public List<V3xIpcontrol> findIpcontrolBy(String name, String type, String accountId, String accountId2);
}
