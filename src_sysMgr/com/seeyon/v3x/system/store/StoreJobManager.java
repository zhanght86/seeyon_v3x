/**
 * 
 */
package com.seeyon.v3x.system.store;

import java.util.ArrayList;
import java.util.List;

import net.joinwork.bpm.engine.execute.BPMCase;
import net.joinwork.bpm.engine.execute.CaseRunDAO;
import net.joinwork.bpm.engine.execute.HisCaseRunDAO;
import net.joinwork.bpm.engine.execute.HisHistoryCaseRunDAO;
import net.joinwork.bpm.engine.execute.HistoryCaseRunDAO;
import net.joinwork.bpm.engine.wapi.ProcessEngine;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItemManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.his.manager.HisAffairManager;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.common.processlog.his.manager.HisProcessLogManager;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * 2012-1-12
 */
public class StoreJobManager extends BaseHibernateDao {
	private static Log log = LogFactory.getLog(StoreJobManager.class);
	
	private AffairManager affairManager;
	private ColManager colManager;
	private ProcessLogManager processLogManager;
	private NewflowManager newflowManager;
	
	private StoreRuleManager storeRuleManager;
	
	private HisColManager hisColManager;
	private HisAffairManager hisAffairManager;
	private HisProcessLogManager hisProcessLogManager;
	
	private WorkItemManager wim = null;
	private ProcessEngine engine = null;

	public void setHisColManager(HisColManager hisColManager) {
		this.hisColManager = hisColManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	
	public void setStoreRuleManager(StoreRuleManager storeRuleManager) {
		this.storeRuleManager = storeRuleManager;
	}
	
	public void setHisAffairManager(HisAffairManager hisAffairManager) {
		this.hisAffairManager = hisAffairManager;
	}
	
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public void setHisProcessLogManager(HisProcessLogManager hisProcessLogManager) {
		this.hisProcessLogManager = hisProcessLogManager;
	}
	
	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}
	
	public void setNewflowManager(NewflowManager newflowManager) {
        this.newflowManager = newflowManager;
    }

	public int doCut(long summaryId) throws Exception{
		if(wim == null){
			try {
				wim = WAPIFactory.getWorkItemManager("Task_1");
				engine = WAPIFactory.getProcessEngine("Engine_1");
			}
			catch (Exception e1) {
				log.error("", e1);
				return 0;
			}
		}
		
		ColSummary colSummary = this.colManager.getColAllById(summaryId);
		if(colSummary == null){
			return 0;
		}
		
		int count = 0;
		doCutOne(colSummary);
		count++;
		
		//检查子流程
		List<Long> newFlowSummaryIds = getNewFlowSummaryIds(colSummary);
		if(!newFlowSummaryIds.isEmpty()){
			for (Long newFlowSummaryId : newFlowSummaryIds) {
				doCutOne(newFlowSummaryId);
				count++;
			}
		}
		
		return count;
	}
	
	private void doCutOne(long summaryId) throws Exception{
		ColSummary colSummary = this.colManager.getColAllById(summaryId);
		if(colSummary == null){
			return;
		}
		
		doCutOne(colSummary);
	}
	
	private void doCutOne(ColSummary colSummary) throws Exception{
		long summaryId = colSummary.getId();
		long caseId = colSummary.getCaseId();
		String processId = colSummary.getProcessId();
		
		List<Affair> affairs = affairManager.findByObject(null, summaryId);
		BPMCase case0 = this.engine.getCase(caseId);
		
		List list  = this.wim.getWorkItemList(null, null, null, null, caseId, null, null, null, null, null, 0, 0, 0, 0, true); // <WorkitemDAO>
		List list1 = this.wim.getHistoryWorkitemList(null, null, null, null, null, null, caseId, null, null, null, null, null, 0, 0, 0, true);
		List<ProcessLog> logs = this.processLogManager.getLogsByProcessId(Long.parseLong(processId), false);
		
		this.hisColManager.save(colSummary);
		this.hisAffairManager.save(affairs);
		
		BPMCase hisCase = null;
		if(case0 instanceof CaseRunDAO){
			hisCase = new HisCaseRunDAO();
		}
		else if(case0 instanceof HistoryCaseRunDAO){
			hisCase = new HisHistoryCaseRunDAO();
		}
		
		hisCase.copy(case0);
		
		engine.saveHisCase(hisCase);
		engine.addHisProcess(processId);
		this.wim.saveHisWorkitem(list, list1);
		this.hisProcessLogManager.saveToHis(logs);
		
		//删除
    	super.delete(ColComment.class, new Object[][]{{"summaryId", summaryId}});
    	super.delete(ColOpinion.class, new Object[][]{{"summaryId", summaryId}});
		super.delete(ColBody.class,    new Object[][]{{"summaryId", summaryId}});
		super.delete(ColSummary.class, new Object[][]{{"id", summaryId}});
    	affairManager.deleteByObject(null, summaryId);
    	ColHelper.deleteWorkflow(processId, case0);
    	this.processLogManager.deleteLog(Long.parseLong(processId));
    	
    	this.colManager.deleteColTrackMembersByObjectId(summaryId);
	}
	
	private List<Long> getNewFlowSummaryIds(ColSummary summary) throws ColException{
		List<Long> reulst = new ArrayList<Long>();
		
		long summaryId = summary.getId();
        Integer newflowType = summary.getNewflowType();
        //当前流程为主流程
		if(newflowType != null && newflowType.intValue() == Constant.NewflowType.main.ordinal()){
		    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.main.ordinal());
		    if(runningList != null && !runningList.isEmpty()){
		        for (NewflowRunning running : runningList) {
		        	reulst.add(running.getSummaryId());
		        }
		    }
		}
		else if(newflowType != null && newflowType.intValue() == Constant.NewflowType.child.ordinal()){
		    //当前流程为子流程
		    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.child.ordinal());
		    if(runningList != null && !runningList.isEmpty()){
		        for (NewflowRunning running : runningList) {
		        	reulst.add(running.getSummaryId());
				}
		    }
		}
		
		return reulst;
	}
}
