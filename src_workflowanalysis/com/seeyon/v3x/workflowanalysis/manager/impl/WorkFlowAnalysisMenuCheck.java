/**
 * 
 */
package com.seeyon.v3x.workflowanalysis.manager.impl;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;
import com.seeyon.v3x.common.flag.SysFlag;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-2-11
 */
public final class WorkFlowAnalysisMenuCheck implements MenuCheck {
	
	private WorkFlowAnalysisAclManager workFlowAnalysisAclManager;
	
	public void setWorkFlowAnalysisAclManager(
			WorkFlowAnalysisAclManager workFlowAnalysisAclManager) {
		this.workFlowAnalysisAclManager = workFlowAnalysisAclManager;
	}

	public boolean check(long memberId, long loginAccountId) {
		boolean isGOV = (Boolean)(SysFlag.sys_isGovVer.getFlag());
		if(isGOV){
			return SystemEnvironment.hasPlugin("workFlowAnalysis") && this.workFlowAnalysisAclManager.getAnalysisAclsByUserId(loginAccountId, memberId) != null;
		}else{
			return this.workFlowAnalysisAclManager.getAnalysisAclsByUserId(loginAccountId, memberId) != null;
		}
		
	}

}
