/**
 * 
 */
package com.seeyon.v3x.agent.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.agent.domain.V3xAgent;
import com.seeyon.v3x.agent.domain.V3xAgentDetail;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.authenticate.domain.V3xAgentDetailModel;

/**
 * @author jincm
 * @version 1.0 2008-7-30
 */
public interface AgentIntercalateManager {

	/**
	 *代理设置信息初始化 
	 */
	void init();
	
	/**
	 * 保存/更新代理信息
	 * @param object
	 */
	public void save(Object object);
	public void update(Object object);
	
	/**
	 * 获取我设置的有效代理记录 -- 我作为被代理人
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> queryAvailabilityList(Long agentToId,String... type) throws Exception;
	
	/**
	 * 获取我设置的代理历史记录 -- 我作为被代理人
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> queryHistoryList(Long agentToId) throws Exception;
	/**
	 * 获取被代理人设置的有效代理记录 -- 我作为代理人
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> queryAvailabilityList1(Long agentId,String... type) throws Exception;
	
	/**
	 * 获取被代理人设置的代理历史记录 -- 我作为代理人
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> queryHistoryList1(Long agentId) throws Exception;
	/**
	 * 代理期限时间设置校验
	 * @return
	 * @throws Exception
	 */
	String[] compareDate(String beginDate, String endDate, String startDate);
	/**
	 * 代理设置信息有效性验证
	 * @return
	 * @throws Exception
	 */
	String checkDataValidity(String agentId, String beginDate, String endDate,
			String[] selectedValues, String currentAgentId, String templateIds);
	/**
	 * 获取有效代理记录
	 * @return
	 * @throws Exception
	 */
	V3xAgent getById(Long id) throws Exception;
	
	/**
	 * 代理设置消息提醒
	 * @param agent
	 * @throws Exception
	 */
	void sendAgentSettingMessage(V3xAgent agent) throws Exception;
	
	/**
	 * 删除代理设置消息提醒
	 * @param agent
	 * @throws Exception
	 */
	void sendDelAgentSettingMessage(V3xAgent agent) throws Exception;
	
	/**
	 * 取消代理设置消息提醒
	 * @param agent
	 * @throws Exception
	 */
	void sendCancelAgentSettingMessage(V3xAgent agent) throws Exception;
	
	/**
	 * 获取所有有效的代理设置记录
	 * @param agentId
	 * @return
	 * @throws Exception
	 */
	List<V3xAgent> queryAllValidity() throws Exception;
	
	/**yangzd
	 * 查询设置人在麽短时间是否设置了某类代理
	 * @param agentToId
	 * @return
	 * @throws Exception
	 */
	public List<V3xAgent> getIsagentByToId(Long agentToId,Date fromDate,Date endDate,String agentOption) throws Exception;

	/**
	 * 取消人员的代理信息，并记录操作日志。（不发送消息）
	 * @param userId
	 * @param opeator 操作人
	 */
	public void cancelUserAgent(Long userId,User opeator);
	
	/**
	 * 保存代理及代理明细
	 * @param agent
	 * @param details
	 */
	public void save(V3xAgent agent,List<V3xAgentDetail> details);
	
	/**
	 * 更新代理及明细
	 * @param agent
	 * @param details
	 */
	public void update(V3xAgent agent,List<V3xAgentDetail> details);
	
	/**
	 * 通过agentId，取得detail列表
	 * @param agentId
	 * @return
	 */
	public List<V3xAgentDetailModel> getDetailModelByAgentId(Long agentId);
	
	/**
	 * 根据人员id获取代理信息(我为代理人) 过滤掉已经取消提醒的代理
	 * @param memberId
	 */
	public List<V3xAgent> getAgentByMemberId(long memberId);
	
	/**
	 * 根据人员id获取代理信息(我为被代理人) 过滤掉已经取消提醒的代理
	 * @param memberId
	 */
	public List<V3xAgent> getToAgentByMemberId(long memberId);
	
	/**
	 * 根据主键修改是否提醒状态
	 * ids 代理主键
	 * isRemin 是否提醒  true 提醒，false 不提醒 
	 */
	public void updateIsAgentRemind(Long id, boolean isRemind, Long currentUserId) ;
}
