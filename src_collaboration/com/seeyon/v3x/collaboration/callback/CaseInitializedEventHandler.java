package com.seeyon.v3x.collaboration.callback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;

/**
 * User: lius
 * Date: 2006-10-31
 * Time: 9:36:07
 */
public class CaseInitializedEventHandler {
    private static Log log = LogFactory.getLog(CaseInitializedEventHandler.class);

    public Long getAccoutIdByCaseId(long caseId){
    	try {
    		ColSummary s = WorkflowEventListener.getColSummary();
    		if(s != null){
    			return s.getOrgAccountId();
    		}
    		
    		ColManager colManager = (ColManager) ApplicationContextHolder.getBean("colManager");
    		final ColSummary summary = colManager.getSummaryByCaseId(caseId);
    		if(summary==null) return null;
			return summary.getOrgAccountId();
		}
		catch (Exception e) {
			log.error("获取协同所属单位异常[caseId = " + caseId + "]", e);
		}
		
		return null;
    }
    
}
