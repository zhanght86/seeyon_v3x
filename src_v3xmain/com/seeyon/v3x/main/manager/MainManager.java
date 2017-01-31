package com.seeyon.v3x.main.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public interface MainManager {
	
	public Map<String, Integer> myInfo();

	/**
	 * 取协同待办数量
	 * 
	 * @param memberId
	 * @return
	 */
	public int getPendingColCount4All(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

	/**
	 * 取所有公文待办数量
	 * 
	 * @param memberId
	 * @return
	 */
	public int getPendingEdocCount4All(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

	/**
	 * 获取我的未召开会议数量
	 * @param memberId
	 * @param agentToFlag
	 * @param ma
	 * @return
	 */
	public int getPendingMeetingCount(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

	/**
	 * 取公共信息发布待审批数量
	 * 
	 * @param memberId
	 * @return
	 */
	public int getPubInfo(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

	/**
	 * 取待填写调查数量
	 * 
	 * @param memberId
	 * @return
	 */
	public int getInquiry(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

	/**
	 * 综合办公待审批数量
	 * @param memberId
	 * @return
	 */
	public int getZHBG(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

	/**
	 * 获取我的代理事项的数量
	 * 
	 * @param memberId
	 * @param agentToFlag
	 * @param ma
	 * @return
	 */
	public int getPendingAgent4All(long memberId, boolean agentToFlag,
			Map<Integer, List<AgentModel>> ma);

}