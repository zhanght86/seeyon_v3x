package com.seeyon.v3x.workflowanalysis.manager;


import java.util.List;

import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysisAcl;

public interface WorkFlowAnalysisAclManager {
	
	/**
	 * 得到我能分析的模版
	 * @param orgAccountId
	 * @param userId
	 * @return
	 */
	public List<Long> getAnalysisAclsByUserId(Long orgAccountId,Long userId);
	/**
	 * 查询分析授权 
	 */
	public List<WorkFlowAnalysisAcl> getWorkFlowAnalysisAclByAccountId(Long AccountId);
	public WorkFlowAnalysisAcl get(Long id);
	/**
	 * 查询所有分析授权 
	 */
	public List<WorkFlowAnalysisAcl> getAllWorkFlowAnalysisAcl();
	
	/**
	 * 保存分析授权
	 */
	public void saveWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl);

	/**
	 * 根据id查询记录 
	 */
	public WorkFlowAnalysisAcl queryAuthorizationById(Long id);
	 /**
	  * 更新分析授权
	  * @param acl
	  */
	public void updateWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl);
	/**
	 * 根据id删除分析授权
	 */
	public void removeWorkFlowAnalysisAclById(Long id);
	/**
	 * 根据用户名来获取能够访问到的模板ID
	 * @param userId
	 * @return
	 */
	public List<Templete> getTempleteByUserId(Long userId,Integer categoryType);
	public List<Long> getTempleteIdByUserId(Long userId,Integer categoryType);
	public List<Long> getLoginAccountTempleteIdByUserId(Long userId,Integer categoryType);

}
