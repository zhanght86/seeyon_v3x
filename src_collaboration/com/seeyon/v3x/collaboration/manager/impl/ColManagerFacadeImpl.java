/**
 * Id: ColManagerFacadeImpl.java, v1.0 2012-4-7 wangchw Exp
 * Copyright (c) 2011 Seeyon, Ltd. All rights reserved
 */
package com.seeyon.v3x.collaboration.manager.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.event.FormTriggerEvent;
import www.seeyon.com.v3x.form.manager.define.trigger.EventValue;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.utils.FormFlowHelper;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.Constant.SendType;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.event.CollaborationFinishEvent;
import com.seeyon.v3x.collaboration.event.CollaborationFormVouchEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStartEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColManagerFacade;
import com.seeyon.v3x.collaboration.manager.ColQuoteformRecordManger;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

/**
 * @Project/Product: 产品或项目名称（A8）
 * @Description: 类功能描述
 * @Copyright: Copyright (c) 2012 of Seeyon, Ltd.
 * @author: wangchw
 * @time: 2012-4-7 下午02:41:55
 * @version: v1.0
 */
public class ColManagerFacadeImpl implements ColManagerFacade {
	
	private final static Log log = LogFactory.getLog(ColManagerFacadeImpl.class);
	
	private ColManager colManager ;
	
    private ColQuoteformRecordManger colQuoteformRecordManger ;
    
    private NewflowManager newflowManager;
    
    private IndexManager indexManager;
    
    private ProjectManager projectManager;
    
    private ProjectPhaseEventManager projectPhaseEventManager;
    
    private ProcessLogManager processLogManager;
    
    private AppLogManager appLogManager;
    
    private DocHierarchyManager docHierarchyManager;
    
    private ColSuperviseManager colSuperviseManager;
    
    private WorkTimeManager workTimeManager;
    
    private AffairManager affairManager;
    
    private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.collaboration.manager.ColManagerFacade#runCaseFacade(com.seeyon.v3x.collaboration.domain.FlowData, com.seeyon.v3x.collaboration.domain.ColSummary, com.seeyon.v3x.collaboration.domain.ColBody, com.seeyon.v3x.collaboration.domain.ColOpinion, com.seeyon.v3x.collaboration.Constant.SendType, java.util.Map, boolean, java.lang.Long, boolean, java.lang.String, java.util.List, com.seeyon.v3x.common.authenticate.domain.User, java.lang.String[], long, java.lang.String[])
	 */
	@Override
	public int runCaseFacade(FlowData flowData, ColSummary colSummary,
			ColBody body, ColOpinion senderOninion, SendType sendType,
			Map options, boolean isNew, Long senderId,boolean track,
			String trackMembers,List<NewflowSetting> newflowList,User user,
			String[] formInfo,long mId,String... newProcessId) throws Exception {
		//流程流转
		Long result = colManager.runCase(flowData, colSummary, body, senderOninion, sendType, options, isNew, senderId);
        //跟踪
		colManager.setTrack(result, track, trackMembers);
	    if(result.longValue() == -1){
            return -1;
        }else{//NF 如果是表单，并设置了新流程，则拷贝当前模板的新流程设置信息到新流程运行表
            if("FORM".equals(body.getBodyType()) && newflowList != null && !newflowList.isEmpty()){
                newflowManager.copyNewflowInfo(newflowList, colSummary.getId(), result);
            }
        }
		//全文检索统一入库
		if(IndexInitConfig.hasLuncenePlugIn()){
			try {
				if(colSummary.getArchiveId()==null||colSummary.getArchiveId()==-1){
					indexManager.index(((IndexEnable)colManager).getIndexInfo(colSummary.getId()));
				}
			}catch (Exception e) {
				log.warn(e.getMessage());
			}
		}	
        //处理表单状态等信息存储
        if("FORM".equals(body.getBodyType())){
	       	 if(isNew == true)
	       		 formDaoManager.saveDataState(colSummary, 1, -1);
	       	 else{
	       		 formDaoManager.UpdateDataState(senderId, colSummary.getId(), "1", colSummary.getFormRecordId(), "submit", "send"); 
	       	 }     		 
        }
        //发送协同操作日志
        appLogManager.insertLog(user, AppLogAction.Coll_New, user.getName(), colSummary.getSubject());
        //流程日志
        if("FORM".equals(body.getBodyType())){
        	BPMProcess process = ColHelper.getRunningProcessByProcessId(colSummary.getProcessId());
        	String  members = ColHelper.checSecondNodeMembers(process,flowData.getCondition());
        	processLogManager.insertLog(user, Long.parseLong(colSummary.getProcessId()), -1l, ProcessLogAction.sendForm,members);
        }else{
        	processLogManager.insertLog(user, Long.parseLong(colSummary.getProcessId()), -1l, ProcessLogAction.sendColl);
        }
        //协同发起事件通知
        try {
        	long affairId = result;
			CollaborationStartEvent event = new CollaborationStartEvent(this);
			event.setSummaryId(colSummary.getId());
			event.setFrom("pc");
			event.setAffairId(affairId);
			EventDispatcher.fireEvent(event);

        	//流程正常结束通知
            if(colSummary.getState().intValue() == Constant.flowState.finish.ordinal()){
            	CollaborationFinishEvent finishEvent = new CollaborationFinishEvent(this);
            	finishEvent.setSummaryId(colSummary.getId());
            	finishEvent.setAffairId(affairId);
            	EventDispatcher.fireEvent(finishEvent);
            }
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
        //如果是项目协同,存入该项目下当前阶段
        if(colSummary.getProjectId() != null){
        	ProjectSummary projectSummary = projectManager.getProject(colSummary.getProjectId());
        	if(projectSummary != null){
        		if(projectSummary.getPhaseId() != 1){
        			ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.collaboration.key(), colSummary.getId(), projectSummary.getPhaseId());
        			projectPhaseEventManager.save(projectPhaseEvent);
        		}
        	}
        }
		return 0;
	}
	

	@Override
	public boolean runCaseImmediateFacade(
			ColSummary colSummary,FlowData flowData,
			String _affairId,ColBody body,User user,
			String processId) throws Exception {
		java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
		Map<String, Object> summ = new HashMap<String, Object>();
		summ.put("createDate", now);
		summ.put("startDate", now);
		summ.put("orgAccountId", CurrentUser.get().getLoginAccount());
		summ.put("orgDepartmentId", CurrentUser.get().getDepartmentId());
		summ.put("state", Constant.flowState.run.ordinal());
		processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
		
		//通知全文检索不入库
		DateSharedWithWorkflowEngineThreadLocal.setNoIndex();
		
		DateSharedWithWorkflowEngineThreadLocal.setColSummary(colSummary);
	
//		long caseId = ColHelper.runCase(processId);
		BPMProcess process = ColHelper.getRunningProcessByProcessId(colSummary.getProcessId());
        BPMSeeyonPolicy seeyonPolicy = process.getStart().getSeeyonPolicy();
		String formApp = seeyonPolicy.getFormApp() ;
		//表单数据对应的主表记录主键值
		String mastrid = null ;
		if(body != null){
			//获得表单数据对应的主表记录主键值
			mastrid = body.getContent() ;
		}
		Map<String, Object> data = new HashMap<String, Object>();
		if(body.getBodyType().equals("FORM")){//表单类型的数据
			Map<String,String[]> fieldDataBaseMap= FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
			data.put("fieldValueMap", fieldDataBaseMap);
		}
		Long senderId = CurrentUser.get().getId();
		data.put("CurrentActivity", process.getStart());
		data.put("currentWorkitemId", -1l);//发起者
        data.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, senderId.toString());
		long caseId = ColHelper.runCaseWithContext(processId, senderId, data);
		summ.put("caseId", caseId);
		colSummary.setCaseId(caseId);
		
		colManager.update(colSummary.getId(), summ);
		
		//全文检索统一入库
		if(IndexInitConfig.hasLuncenePlugIn()){
			try {
				if(colSummary.getArchiveId()==null||colSummary.getArchiveId()==-1)
				{
				indexManager.index(((IndexEnable)colManager).getIndexInfo(colSummary.getId()));
				}
			}
			catch (Exception e) {
				log.warn(e.getMessage());
			}
		}	
	
		Map<String, Object> aff = new HashMap<String, Object>();
	
		aff.put("app", ApplicationCategoryEnum.collaboration.key());
		//aff.put("subObjectId", null);
		aff.put("state", StateEnum.col_sent.key());
		aff.put("subState", SubStateEnum.col_normal.key());            
		aff.put("createDate", now);
		
		if(colSummary.getArchiveId() != null){
			Long affairId = Long.parseLong(_affairId);
			int appName = ApplicationCategoryEnum.collaboration.getKey();
			try{
				//DocHierarchyManager docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
				docHierarchyManager.pigeonholeAfterPre(appName, affairId, 
						colSummary.isHasAttachments(), colSummary.getArchiveId(), CurrentUser.get().getId());
			}catch(Exception e){
				log.error("Collaboration runcase 预归档错误", e);
				throw new ColException("Collaboration runcase 预归档错误");
			}
        }
		
		//this.colSuperviseManager.updateOnlySendMessage(colSummary.getImportantLevel(), colSummary.getSubject(), user.getId(), user.getName(), Constant.superviseType.summary.ordinal(), summaryId);
		colSuperviseManager.updateStatus(colSummary.getImportantLevel(),
				colSummary.getSubject(), user.getId(), user.getName(), 
				Constant.superviseType.summary.ordinal(), colSummary.getId(), 
				Constant.superviseState.supervising.ordinal(), colSummary.getForwardMember());
	
		affairManager.update(Long.parseLong(_affairId), aff);
		
		ColHelper.createQuartzJobOfSummary(colSummary, workTimeManager);
	
		boolean sentFlag = true;
        //流程日志
        String members = ColHelper.checSecondNodeMembers(process,flowData.getCondition());
		//发送协同操作日志
        /*operationlogManager.insertOplog(colSummary.getId(), ApplicationCategoryEnum.collaboration, 
        		Constant.OperationLogActionType.sendColl.name(), "col.operationlog.sendColl", user.getName(), new java.util.Date(), colSummary.getSubject());*/
        if(body != null && "FORM".equals(body.getBodyType())){
        	processLogManager.insertLog(user, Long.parseLong(colSummary.getProcessId()), -1l, ProcessLogAction.sendForm,members);
        }
        else{
        	processLogManager.insertLog(user, Long.parseLong(colSummary.getProcessId()), -1l, ProcessLogAction.sendColl,members);
        }
        //发起事件通知
        CollaborationStartEvent event = new CollaborationStartEvent(this);
        event.setSummaryId(colSummary.getId());
        event.setFrom("pc");
        event.setAffairId(Long.parseLong(_affairId));
        EventDispatcher.fireEvent(event);
		return sentFlag;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.collaboration.manager.ColManagerFacade#finishWorkItemFacade(long, com.seeyon.v3x.collaboration.domain.ColOpinion, java.util.Map, java.util.Map, java.lang.String, com.seeyon.v3x.common.authenticate.domain.User, java.lang.String, java.lang.String[], com.seeyon.v3x.collaboration.domain.ColSummary, javax.servlet.http.HttpServletRequest, long, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void finishWorkItemFacade(long affairId, ColOpinion signOpinion,
			Map<String, String[]> map, Map<String, String> condition,
			String processId, User user,String draftOpinionId,
			String[] fieldName,ColSummary summary,HttpServletRequest request,
			long summaryId,String formApp,String _affairId,Integer oldIsVouch) throws Exception {
	    WorkflowEventListener.setColSummary(null);//覆盖子流程的summary

        //插入关联的无流程表单
	    if(Strings.isNotBlank(formApp) && !formApp.equals("null")){
	    	colQuoteformRecordManger.create(request, summaryId, Long.parseLong(formApp), summary.getFormRecordId());
	    }
        if(Strings.isNotBlank(formApp)){
	  		// 协同表单核定事件触发
	  		if(summary.getIsVouch() == 1){
		  		CollaborationFormVouchEvent event = new CollaborationFormVouchEvent(this);
		  		event.setSummaryId(summaryId);
		  		event.setIsVouch(summary.getIsVouch());
		  		event.setOldIsVouch(oldIsVouch);
		  		event.setAffairId(affairId);
		  		EventDispatcher.fireEvent(event);
	  		}
        }
	    colManager.finishWorkItem(affairId, signOpinion, map, condition, processId, user);
	}

	  public void finishWorkItemFacade(Affair affair, ColOpinion signOpinion,
	    		Map<String,String[]> map, Map<String, String> condition, 
	    		BPMProcess process, User user,String draftOpinionId,
	    		String[] fieldName,ColSummary summary,HttpServletRequest request,
	    		long summaryId,String formApp,Integer oldIsVouch) throws Exception{
		  

		    WorkflowEventListener.setColSummary(null);//覆盖子流程的summary

	        //插入关联的无流程表单
		    if(Strings.isNotBlank(formApp) && !formApp.equals("null")){
		    	colQuoteformRecordManger.create(request, summaryId, Long.parseLong(formApp), summary.getFormRecordId());
		    }
	        if(Strings.isNotBlank(formApp)){
		  		// 协同表单核定事件触发
		  		if(summary.getIsVouch() == 1){
			  		CollaborationFormVouchEvent event = new CollaborationFormVouchEvent(this);
			  		event.setSummaryId(summaryId);
			  		event.setIsVouch(summary.getIsVouch());
			  		event.setOldIsVouch(oldIsVouch);
			  		event.setAffairId(affair.getId());
			  		EventDispatcher.fireEvent(event);
		  		}
	        }
		    colManager.finishWorkItem(affair, signOpinion, map, condition, process, user,summary);
		
	  }
	/**
	 * @param newflowManager the newflowManager to set
	 */
	public void setNewflowManager(NewflowManager newflowManager) {
		this.newflowManager = newflowManager;
	}

	/**
	 * @param indexManager the indexManager to set
	 */
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	/**
	 * @param projectManager the projectManager to set
	 */
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	/**
	 * @param projectPhaseEventManager the projectPhaseEventManager to set
	 */
	public void setProjectPhaseEventManager(
			ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}

	/**
	 * @param processLogManager the processLogManager to set
	 */
	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}

	/**
	 * @param appLogManager the appLogManager to set
	 */
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public void setColQuoteformRecordManger(
			ColQuoteformRecordManger colQuoteformRecordManger) {
		this.colQuoteformRecordManger = colQuoteformRecordManger;
	}
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}


	/**
	 * @param docHierarchyManager the docHierarchyManager to set
	 */
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}


	/**
	 * @param colSuperviseManager the colSuperviseManager to set
	 */
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}


	/**
	 * @param workTimeManager the workTimeManager to set
	 */
	public void setWorkTimeManager(WorkTimeManager workTimeManager) {
		this.workTimeManager = workTimeManager;
	}


	/**
	 * @param affairManager the affairManager to set
	 */
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

}
