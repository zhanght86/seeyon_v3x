package com.seeyon.v3x.workflowanalysis.dao.impl;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.workflowanalysis.dao.WorkFlowAnalysisAclDao;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysisAcl;

public class WorkFlowAnalysisAclDaoAclImpl extends BaseHibernateDao<WorkFlowAnalysisAcl>
	implements WorkFlowAnalysisAclDao{

	public void saveWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl) {
		super.save(acl);		
	}

	public void updateWorkFlowAnalysisAcl(WorkFlowAnalysisAcl acl) {
		super.update(acl);
	}
	
	public List<WorkFlowAnalysisAcl> getWorkFlowAnalysisAclByAccountId(Long orgAccountId){
		String hql = "from WorkFlowAnalysisAcl acl where acl.orgAccountId = ? ";
		return super.find(hql, orgAccountId);
	}
	
	public List<WorkFlowAnalysisAcl> getAllWorkFlowAnalysisAcl() {
		return super.getAll();
	}
	
	public WorkFlowAnalysisAcl queryAuthorizationById(Long id) {
		return super.get(id);
	}
	
	public void removeWorkFlowAnalysisAclById(Long id) {
		super.removeById(id);
	}
	public WorkFlowAnalysisAcl get(Long id){
		return super.get(id);
	}
	
}