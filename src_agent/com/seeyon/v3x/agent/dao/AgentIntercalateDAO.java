/**
 * 
 */
package com.seeyon.v3x.agent.dao;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.agent.domain.V3xAgent;

/**
 * @author jincm
 * @version 1.0 2008-7-30
 */
public interface AgentIntercalateDAO {

	/**
	 * 保存/更新代理信息
	 * @param object
	 */
	public void save(Object object);
	public void update(Object object);
	
	/**
	 * 根据所有的有效代理记录
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> findAllAvailability() throws Exception;
	
	/**
	 * 根据被代理人获取有效代理记录
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> findAvailabilityByAgentToId(Long agentToId,String... type) throws Exception;
	
	/**
	 * 根据被代理人获取代理历史记录
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> findHistoryByAgentToId(Long agentToId) throws Exception;
	/**
	 * 根据代理人获取代理历史记录
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> findAvailabilityByAgentId(Long agentId,String... type) throws Exception;
	/**
	 * 根据代理人获取代理历史记录
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> findHistoryByAgentId(Long agentId) throws Exception;
	/**
	 * 根据id获取代理记录
	 * @return
	 * @throws Exception
	 */
	V3xAgent getById(Long id) throws Exception;
	/**
	 * 查询设置人在麽短时间是否设置了某类代理
	 * @param agentToId
	 * @return
	 * @throws Exception
	 */
	public List<V3xAgent> getIsagentByToId(Long agentToId,Date fromDate,Date endDate,String agentOption) throws Exception;
	
	public List<V3xAgent> listAgents(List<Long> agentIds);
	
	/**
	 * 返回带有明细的V3xAgent对象
	 * @param agent
	 * @return
	 */
	public V3xAgent findAgentWithDetailById(V3xAgent agent);
}
