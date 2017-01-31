package com.seeyon.v3x.online.manager;

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;

public interface WIMManager {

	/**
	 * 根据协同id查找协同讨论组
	 * @param colId
	 * @return
	 * @throws BusinessException 
	 */
	public V3xOrgTeam getRelativeTeamByColId(long summaryId) throws BusinessException;
	
	/**
	 * 判断讨论组是否是协同讨论组
	 * @param teamid
	 * @return
	 * @throws BusinessException 
	 */
	public boolean isColRelativeTeam(long teamid) throws BusinessException;
	
	/**
	 * 判断协同是否存在讨论组
	 * @param colid
	 * @return
	 * @throws BusinessException 
	 */
	public boolean existRelativeTeam(long summaryId) throws BusinessException;
	
	/**
	 * 获取所有讨论组，不包含个人组和协同讨论组
	 * @return
	 * @throws BusinessException 
	 */
	public List<V3xOrgTeam> getAllDiscussTeam(Long memberId) throws BusinessException;
}
