package com.seeyon.v3x.edoc.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.workflow.event.WorkflowEventListener;

public class CaseInitializedEventEdocHandler {
	private static final Log log = LogFactory.getLog(CaseInitializedEventEdocHandler.class);

	public Long getAccoutIdByCaseId(long caseId){
    	try {
    		EdocSummary s = WorkflowEventListener.getEdocSummary();
    		if(s != null){
    			return s.getOrgAccountId();
    		}
    		EdocManager edocManager = (EdocManager) ApplicationContextHolder.getBean("edocManager");
    		return edocManager.getSummaryByCaseId(caseId).getOrgAccountId();
    	}
    	catch (Exception e) {
    		log.error("获取公文所属单位异常[caseId = " + caseId + "]", e);
    	}
    	
    	return null;
    }

}
