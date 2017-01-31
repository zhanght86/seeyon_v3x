package com.seeyon.v3x.workflowanalysis.dao;
import java.util.List;

import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysisAcl;

public interface WorkFlowAnalysisAclDao {
	/**
	 * 保存
	 * @param acl
	 */
	public void saveWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl);
	/**
	 * 更新
	 * @param acl
	 */
	public void updateWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl);
	/**
	 * 查询授权列表
	 * @param:单位ID
	 */
	public List<WorkFlowAnalysisAcl> getWorkFlowAnalysisAclByAccountId(Long orgAccountId);
	/**
	 * 查询数据库表中的所有授权列表
	 */
	public List<WorkFlowAnalysisAcl> getAllWorkFlowAnalysisAcl();
	/**
	 * 根据id查询记录
	 */
	public WorkFlowAnalysisAcl queryAuthorizationById(Long id);
	/**
	 * 删除分析授权
	 */
	public void removeWorkFlowAnalysisAclById(Long id);
	public WorkFlowAnalysisAcl get(Long id);
}
