package com.seeyon.v3x.collaboration.manager.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.wapi.ProcessDefManager;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;
import net.joinwork.bpm.util.Utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.data.base.DisplayValue;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bulletin.util.hql.HqlResult;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.dao.ColSummaryDao;
import com.seeyon.v3x.collaboration.dao.ColTrackMemberDao;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.MessageData;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.domain.WorkflowData;
import com.seeyon.v3x.collaboration.event.CollaborationCancelEvent;
import com.seeyon.v3x.collaboration.event.CollaborationFinishEvent;
import com.seeyon.v3x.collaboration.event.CollaborationProcessEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStartEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStepBackEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStopEvent;
import com.seeyon.v3x.collaboration.event.CollaborationTakeBackEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.his.domain.HisColOpinion;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.collaboration.webmodel.StatModel;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.authenticate.domain.V3xAgentDetailModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.office.HandWriteManager;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.taglibs.functions.CollaborationFunction;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.DocUtils;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocMessageHelper;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.StatUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.workflowanalysis.domain.WorkFlowAnalysis;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

public class ColManagerImpl extends BaseHibernateDao<ColSummary> implements ColManager,IndexEnable {
    private final static Log log = LogFactory.getLog(ColManagerImpl.class);
    private final static Log branchLog = LogFactory.getLog(BPMTransition.class);
    private AffairManager affairManager = null;
    
    private UserMessageManager userMessageManager = null;
    private AttachmentManager attachmentManager;
    private FileManager fileManager;
    private OrgManager orgManager;
    private PermissionManager permissionManager;
    //private OperationlogManager operationlogManager;
    private PartitionManager partitionManager;
    private NewflowManager newflowManager;
    private ProcessLogManager processLogManager;
    private DocHierarchyManager docHierarchyManager;
    private TempleteManager templeteManager;
    private AppLogManager appLogManager;
    private ConfigManager configManager;
    private SignetManager  signetManager;
    private WorkTimeManager workTimeManager;
    private HandWriteManager handWriteManager;
    private ColTrackMemberDao colTrackMemberDao;
    private ColSuperviseManager colSuperviseManager;
    private FlowPermManager flowPermManager;
    private ColSummaryDao colSummaryDao;
    
    private IndexManager indexManager;
    private HisColManager hisColManager;
    /**
	 * @return the indexManager
	 */
	public IndexManager getIndexManager() {
		return indexManager;
	}
	/**
	 * @param indexManager the indexManager to set
	 */
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	
	public void setHisColManager(HisColManager hisColManager) {
		this.hisColManager = hisColManager;
	}

	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	

	public void setColTrackMemberDao(ColTrackMemberDao colTrackMemberDao) {
		this.colTrackMemberDao = colTrackMemberDao;
	}

	//处理后不允许被撤销的节点权限列表，XX审核的节点权限
    private List cannotRepealList;
    
	/*public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}*/

	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	public void setColSummaryDao(ColSummaryDao colSummaryDao) {
		this.colSummaryDao = colSummaryDao;
	}
    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }

    public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }

    public SignetManager getSignetManager() {
		return signetManager;
	}

	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}
    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }
    
    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

    public void setNewflowManager(NewflowManager newflowManager) {
        this.newflowManager = newflowManager;
    }
    
    public List getCannotRepealList() {
        return cannotRepealList;
    }

    public void setCannotRepealList(List cannotRepealList) {
        this.cannotRepealList = cannotRepealList;
    }

    public void setPartitionManager(PartitionManager partitionManager) {
        this.partitionManager = partitionManager;
    }
    public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }
    private DocHierarchyManager getDocHierarchyManager() {
		if(docHierarchyManager == null){
			docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
		}
		return docHierarchyManager;
	}
    private ColSuperviseManager getColSuperviseManager() {
		if(colSuperviseManager == null){
			colSuperviseManager = (ColSuperviseManager)ApplicationContextHolder.getBean("colSuperviseManager");
		}
		return colSuperviseManager;
	}
    public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}
    
    public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	public void setWorkTimeManager(WorkTimeManager workTimeManager) {
		this.workTimeManager = workTimeManager;
	}
	
	public void setFlowPermManager(FlowPermManager flowPermManager) {
		this.flowPermManager = flowPermManager;
	}

	public void claimWorkItem(int workItemId) throws ColException {
        // TODO Auto-generated method stub
    }

	public void finishWorkItem(long affairId, ColOpinion signOpinion,Map<String,String[]> manualMap, Map<String,String> condition, String processId, User user)
            throws ColException {
		Affair affair = affairManager.getById(affairId);
		if(affair==null || affair.getState()!=StateEnum.col_pending.key()){
		    String msg = ColHelper.getErrorMsgByAffair(affair);
		    throw new ColException(msg);
		}
		Long summaryId = affair.getObjectId();
        if (summaryId == null) {
            throw new ColException("summaryId is not found or you are not allowed to treat it. summaryId is " + summaryId);
        }
		Long workItemId = null;
		if(affair.getSubObjectId() != null){
			workItemId = affair.getSubObjectId();
		}
        else{
			throw new ColException("workitemId is not found or you are not allowed to treat it. workitemId is not found.");
		}
		if(processId==null){
			 ColSummary colSummary = this.getColSummaryById(summaryId, true);
			processId = colSummary.getProcessId();
		}
		BPMProcess process = ColHelper.saveModifyingProcess(processId, user.getId()+"");
		if(process != null){
			//ColHelper.updateRunningProcess(process);
			//如果该流程实例存在待添加的节点，将其激活
			ColHelper.saveAcitivityModify(process, user.getId()+"");
		}else{
			process= ColHelper.getCaseProcess(processId);
		}
        finishWorkItem(workItemId, affair, summaryId, signOpinion,manualMap, condition, process, user);
    }
	 private void finishWorkItem(Long workItemId,Affair affair, Long summaryId, ColOpinion signOpinion,Map<String,String[]> manualMap, Map<String, String> condition, BPMProcess process, User user) throws ColException {
		    ColSummary colSummary = this.getColSummaryById(summaryId, true);
		    finishWorkItem(workItemId, affair, colSummary, signOpinion, manualMap, condition, process, user);
	 }
    private void finishWorkItem(Long workItemId, Affair affair, ColSummary colSummary, ColOpinion signOpinion,Map<String,String[]> manualMap, Map<String, String> condition, BPMProcess process, User user) 
    throws ColException {
    	signOpinion.setIdIfNew();
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	//持久化修改后的流程
    	//BPMActivity activity = ColHelper.getBPMActivityByAffair(affair);
    	BPMActivity activity = process.getActivityById(affair.getActivityId().toString());
//    	if(processId != null && user != null && null!=activity){
//    		BPMProcess process = ColHelper.saveModifyingProcess(processId, user.getId()+"");
    		if(process != null){
    			//ColHelper.updateRunningProcess(process);
    			//如果该流程实例存在待添加的节点，将其激活
    			//ColHelper.saveAcitivityModify(process, user.getId()+"");
    			//更新完流程之后,发送消息提醒
    			List<MessageData> messageDataList = messageDataMap.get(colSummary.getId());
    			if(messageDataList != null){
	    			for(MessageData messageData : messageDataList){
	    				if(user.getId() == messageData.handlerId){
		    				String operationType = messageData.getOperationType();
		    				List<String> partyNames = messageData.getPartyNames();
		    				Affair _affair = messageData.getAffair();
		    				List<String[]> processLogParam = messageData.getProcessLogParam();
		    				if("insertPeople".equals(operationType)){
		    					List<ColTrackMember> trackMembers = getColTrackMembersByObjectIdAndTrackMemberId(colSummary.getId(),null);
		    					ColMessageHelper.insertPeopleMessage(affairManager, userMessageManager, orgManager, partyNames, colSummary, _affair,trackMembers);
		    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
		    			        //		Constant.OperationLogActionType.insertPeople.name(), "col.operationlog.insertPeople", user.getName(), new java.util.Date(), summary.getSubject());
		    					for(String[] param : processLogParam){
		    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.insertPeople, param);
		    					}
		    				}else if("deletePeople".equals(operationType)){
		    					ColMessageHelper.deletePeopleMessage(affairManager, orgManager, userMessageManager, partyNames, colSummary, _affair);
		    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
		    			        //		Constant.OperationLogActionType.deletePeople.name(), "col.operationlog.deletePeople", user.getName(), new java.util.Date(), summary.getSubject());
		    					for(String[] param : processLogParam){
		    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.deletePeople, param);
		    					}
		    				}else if("colAssign".equals(operationType)){
		    					ColMessageHelper.colAssignMessage(userMessageManager, affairManager, orgManager, partyNames, colSummary, _affair);
		    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
		    			       // 		Constant.OperationLogActionType.colAssign.name(), "col.operationlog.colAssign", user.getName(), new java.util.Date(), summary.getSubject());
		    					for(String[] param : processLogParam){
		    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.colAssign, param);
		    					}
		    				}else if("addInform".equals(operationType)){
		    					ColMessageHelper.addInformMessage(userMessageManager, affairManager, orgManager, partyNames, colSummary, _affair);
		    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
		    					//		Constant.OperationLogActionType.inform.name(), "col.operationlog.inform", user.getName(), new java.util.Date(), summary.getSubject());
		    					for(String[] param : processLogParam){
		    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.inform, param);
		    					}
		    				}
	    				}
	    			}
    			}
    		}
    	//}
    	//提交，删除锁
    	messageDataMap.remove(colSummary.getId());
    	
        if(manualMap!=null && !manualMap.isEmpty()){
            ColHelper.setActivityManualSelect(workItemId,manualMap);                        
        }
	    if(condition != null && !condition.isEmpty()){
	       	ColHelper.setActivityIsDelete(workItemId, condition);
	    }
	    
	    //ColSummary colSummary = this.getColSummaryById(summaryId, false);

	    
	    WorkflowEventListener.setColSummary(colSummary);
	    
        WorkflowEventListener.setOperationType(WorkflowEventListener.COMMONDISPOSAL);
        DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), signOpinion.getContent(), signOpinion.getAttitude(), signOpinion.isHasAtt());
//        ColHelper.finishWorkitem(workItemId);//提交修改的bug代码时，这行代码应该删除掉。
        if (affair != null && colSummary.getId() != null) {
        	Map<String, Object> updateAffair = new HashMap<String, Object>();
        	
            signOpinion.setSummaryId(colSummary.getId());
            signOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
            signOpinion.setOpinionType(ColOpinion.OpinionType.signOpinion.ordinal());
            
            if(signOpinion.affairIsTrack){
            	updateAffair.put("isTrack", signOpinion.affairIsTrack);
            }
            
            if(signOpinion.getArchiveId() != null){
            	Long archiveId = signOpinion.getArchiveId();
            	updateAffair.put("archiveId", archiveId);
            }

            if(affair.getMemberId() != user.getId()){
            	List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
            	if(agentModelList != null && !agentModelList.isEmpty()){
            		signOpinion.setWriteMemberId(affair.getMemberId());
                	signOpinion.setProxyName(user.getName());
            	}else{
            		signOpinion.setWriteMemberId(user.getId());
            	}
                //非本人处理的，写入处理人ID
                updateAffair.put("transactorId", user.getId());
            }
            else{
            	signOpinion.setWriteMemberId(user.getId());
            }
            
            save(signOpinion);
           // if( null==processId ){//增加这个判断，为了预防手机接口调用时出现空指针错误
           // 	processId= colSummary.getProcessId();
           // }
            //从流程数据flowData中获得表单信息
          //  BPMProcess process = ColHelper.getCaseProcess(processId);
            

            //流程处理事件通知
            CollaborationProcessEvent event = new CollaborationProcessEvent(this);
	        event.setSummaryId(colSummary.getId());
	        event.setAffairId(affair.getId());
	        EventDispatcher.fireEvent(event);
	        
	        WorkflowEventListener.setColSummary(colSummary);
            if (signOpinion.isDeleteImmediate) {
            	updateAffair.put("isDelete", true);
            }
			if(!updateAffair.isEmpty()){
				super.update(Affair.class, affair.getId(), updateAffair);
			}
			// 基于修改后的流程记录日志
			if(null!=activity){
	            String to = ColHelper.checkNextNodeMembers(activity,condition);
	            processLogManager.insertLog(user, Long.parseLong(colSummary.getProcessId()), Long.parseLong(activity.getId()), ProcessLogAction.commit,to);
			}
			Map<String, Object> data = new HashMap<String, Object>();
            BPMActivity myActivity= process.getActivityById(affair.getActivityId().toString());
            BPMHumenActivity humenactivity= null;
            if(myActivity!=null){
            	humenactivity= (BPMHumenActivity)myActivity;
            	BPMSeeyonPolicy seeyonPolicy = humenactivity.getSeeyonPolicy();
        		String formApp = seeyonPolicy.getFormApp() ;
        		//表单数据对应的主表记录主键值
        		String mastrid = null ;
        		ColBody body = new ColBody();
        		if(colSummary != null){
        			//从协同中获得协同主体信息
        			body = colSummary.getFirstBody();
        		}
        		if(body != null){
        			//获得表单数据对应的主表记录主键值
        			mastrid = body.getContent() ;
        		}
        		if(body.getBodyType().equals("FORM")){//表单类型的数据
        			Map<String, String[]> fieldDataBaseMap;
    				try {
    					if(Strings.isBlank(formApp) && colSummary.getFormAppId()!=null){
    	        			formApp = colSummary.getFormAppId().toString();
    	        		}
    					fieldDataBaseMap = FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid);
    					data.put("fieldValueMap", fieldDataBaseMap);
    				} catch (Exception e) {
    					throw new ColException(e);
    				}
        		}
        		data.put("CurrentActivity", humenactivity);
        		data.put("currentWorkitemId", workItemId);//当前处理者
                ColHelper.finishWorkitemWithContext(workItemId,data);
            }else{//兼容处理
            	//将workitem修改为已办状态
            	ColHelper.finishItem(workItemId);
            	//将affair更改为已办状态
            	affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	Timestamp now = new Timestamp(System.currentTimeMillis());
                affair.setCompleteTime(now);
                affair.setUpdateDate(now);
                //设置运行时长，超时时长等
                //setTime2Affair(affair);
                affairManager.updateAffair(affair);
                //发送完成事项消息提醒
            	ColMessageHelper.workitemFinishedMessage(affairManager, orgManager, this, userMessageManager, affair, colSummary.getId());
            }
            if(colSummary.getState().intValue() == Constant.flowState.finish.ordinal()){
            	//流程正常结束通知
            	CollaborationFinishEvent finishEvent = new CollaborationFinishEvent(this);
            	finishEvent.setSummaryId(colSummary.getId());
            	finishEvent.setAffairId(affair.getId());
            	EventDispatcher.fireEvent(finishEvent);
            }
        }
    }

    public void zcdb(long summaryId, Affair affair, ColOpinion opinion, String processId, String userId) throws ColException {
    	User user = CurrentUser.get();
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	//持久化修改后的流程
    	if(processId != null && userId != null){
    		BPMProcess process = ColHelper.saveModifyingProcess(processId, userId);
    		BPMActivity activity = ColHelper.getBPMActivityByAffair(affair);
    		if(process != null){
    			ColHelper.updateRunningProcess(process);
    			//如果该流程实例存在待添加的节点，将其激活
    			ColHelper.saveAcitivityModify(process, userId);
    			//更新完流程之后,发送消息提醒
    			List<MessageData> messageDataList = messageDataMap.get(summaryId);
    			if(messageDataList != null){
	    			for(MessageData messageData : messageDataList){
	    				String operationType = messageData.getOperationType();
	    				List<String> partyNames = messageData.getPartyNames();
	    				ColSummary summary = messageData.getSummary();
	    				Affair _affair = messageData.getAffair();
	    				List<String[]> processLogParam = messageData.getProcessLogParam();
	    				if("insertPeople".equals(operationType)){
	    					List<ColTrackMember> trackMembers = getColTrackMembersByObjectIdAndTrackMemberId(summary.getId(),null);
	    					ColMessageHelper.insertPeopleMessage(affairManager, userMessageManager, orgManager, partyNames, summary, _affair,trackMembers);
	    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
	    			       // 		Constant.OperationLogActionType.insertPeople.name(), "col.operationlog.insertPeople", user.getName(), new java.util.Date(), summary.getSubject());
	    					for(String[] param : processLogParam){
	    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.insertPeople, param);
	    					}
	    				}else if("deletePeople".equals(operationType)){
	    					ColMessageHelper.deletePeopleMessage(affairManager, orgManager, userMessageManager, partyNames, summary, _affair);
	    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
	    			       // 		Constant.OperationLogActionType.deletePeople.name(), "col.operationlog.deletePeople", user.getName(), new java.util.Date(), summary.getSubject());
	    					for(String[] param : processLogParam){
	    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.deletePeople, param);
	    					}
	    				}else if("colAssign".equals(operationType)){
	    					ColMessageHelper.colAssignMessage(userMessageManager, affairManager, orgManager, partyNames, summary, _affair);
	    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
	    			        //		Constant.OperationLogActionType.colAssign.name(), "col.operationlog.colAssign", user.getName(), new java.util.Date(), summary.getSubject());
	    					for(String[] param : processLogParam){
	    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.colAssign, param);
	    					}
	    				}else if("addInform".equals(operationType)){
	    					ColMessageHelper.addInformMessage(userMessageManager, affairManager, orgManager, partyNames, summary, _affair);
	    					//operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
	    					//		Constant.OperationLogActionType.inform.name(), "col.operationlog.inform", user.getName(), new java.util.Date(), summary.getSubject());
	    					for(String[] param : processLogParam){
	    						processLogManager.insertLog(user, Long.parseLong(process.getId()), Long.parseLong(activity.getId()), ProcessLogAction.inform, param);
	    					}
	    				}
	    				
	    			}
	    			
    			}
    		}
    	}
    	messageDataMap.remove(summaryId);
    	
    	try{
	    	long workItemId = affair.getSubObjectId();
	    	WorkflowEventListener.setOperationType(WorkflowEventListener.ZCDB);
	        ColHelper.zcdbWorkitem(workItemId);
    	}catch(ColException e){
    		throw e;
    	}
    	
        opinion.setIdIfNew();
        opinion.setSummaryId(affair.getObjectId());
        opinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
        opinion.setOpinionType(ColOpinion.OpinionType.provisionalOpinoin.ordinal());
        
        if(affair.getMemberId() != user.getId()){
        	List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
        	if(agentModelList != null && !agentModelList.isEmpty()){
        		opinion.setWriteMemberId(affair.getMemberId());
        		opinion.setProxyName(user.getName());
        	}else{
        		opinion.setWriteMemberId(user.getId());
        	}
        }else{
        	opinion.setWriteMemberId(user.getId());
        }
        
        save(opinion);
        
        Integer sub_state = affair.getSubState();
        if (sub_state == null || sub_state.intValue() == SubStateEnum.col_normal.key()|| sub_state.intValue() == SubStateEnum.col_pending_unRead.key() || sub_state.intValue() == SubStateEnum.col_pending_read.key()) {
            affair.setSubState(SubStateEnum.col_pending_ZCDB.key());
            affair.setUpdateDate(new Date(System.currentTimeMillis()));
            this.affairManager.updateAffair(affair);
        }
        
        //暂存待办消息提醒
        ColMessageHelper.zcdbMessage(userMessageManager, orgManager, affairManager, affair, opinion);
    }

    public ColSummary getColSummaryById(long summaryId, boolean needBody) throws ColException {
        ColSummary summary = super.get(summaryId);
        if(summary == null)
        	return null;
        
        if (needBody) {
            summary.getBodies();
            summary.getFirstBody();
//            ColBody body = (ColBody) this.getHibernateTemplate()
//                    .iterate("from "+ColBody.class.getName()+" as body where body.summaryId = ?", summaryId)
//                    .next();
//            summary.getBodies().add(body);
        }

        return summary;
    }
    
    public ColSummary getColAllById(long summaryId) throws ColException {
        ColSummary summary = super.get(summaryId);
		if(summary == null) return null;

    	if(summary.getOpinions()!=null)summary.getOpinions().size();
    	if(summary.getComments()!=null)summary.getComments().size();
    	if(summary.getBodies()!=null)summary.getBodies().size();
            summary.getFirstBody();
        return summary;
    }

    public static final String PAGE_TYPE_DRAFT = "draft";
    public static final String PAGE_TYPE_SENT = "sent";
    public static final String PAGE_TYPE_PENDING = "pending";
    public static final String PAGE_TYPE_FINISH = "finish";

    //删除一个个人事�xiang项
    public void deleteAffair(String pageType, long affairId) throws ColException {
        User user = CurrentUser.get();
        Affair affair = affairManager.getById(affairId);
        if (affair == null)
            return;

        //如果是保存待发，删除个人事项的同时删除整个协同�
        if (pageType.equals(PAGE_TYPE_DRAFT)) {
            long summaryId = affair.getObjectId();
            //super.delete(summaryId);
            List<Affair> affairs=new ArrayList<Affair>();
            affairs=affairManager.findByObject(ApplicationCategoryEnum.collaboration, summaryId);
            this.delete(summaryId);
            affairManager.deleteByObject(ApplicationCategoryEnum.collaboration, summaryId);
            List<Long> ids=new ArrayList<Long>();
            for(Affair a:affairs){
            	ids.add(a.getId());
            }
            try {
    			getDocHierarchyManager().deleteDocByResources(ids, user);
			} catch (Exception e) {
				log.error("删除归档文档:"+e);
			}
            try {
				this.attachmentManager.removeByReference(summaryId);
			}
			catch (BusinessException e) {
				log.error("", e);
			}
        }
        else{
	        //如果是待办，删除个人事项的同时finishWorkitem
	        if (pageType.equals(PAGE_TYPE_PENDING)) {
	            ColOpinion nullColOpinion = new ColOpinion();
	            nullColOpinion.setIdIfNew();
	            nullColOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
	            nullColOpinion.setOpinionType(ColOpinion.OpinionType.signOpinion.ordinal());
	            nullColOpinion.setWriteMemberId(user.getId());
	            
	            finishWorkItem(affairId, nullColOpinion, null, null, null, user);
	        }
	        affairManager.deleteAffair(affair.getId());
        }
        appLogManager.insertLog(user, AppLogAction.Coll_Delete, user.getName(), affair.getSubject());
    }

    public void updateAffair(String pageType, long affairId, long archiveId) throws ColException {
        User user = CurrentUser.get();
        Affair affair = affairManager.getById(affairId);
        if (affair == null)
            return;
        
        // 如果是已发协同，归档个人事项
        if(pageType.equals(PAGE_TYPE_SENT)){
        	/*
            Long summaryId = affair.getObjectId();
        	 ColSummary summary = get(summaryId);
            if(summary != null){
            	summary.setArchiveId(archiveId);
            	update(summary);
            }*/
            affair.setArchiveId(archiveId);
            
        }//如果是待办，归档个人事项的同时finishWorkitem
        else if (pageType.equals(PAGE_TYPE_PENDING)) {
            Long workitemId = affair.getSubObjectId();
            Long summaryId = affair.getObjectId();
            ColOpinion nullColOpinion = new ColOpinion();
            nullColOpinion.setIdIfNew();
            nullColOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
            nullColOpinion.setOpinionType(ColOpinion.OpinionType.signOpinion.ordinal());
            nullColOpinion.setWriteMemberId(user.getId());

            finishWorkItem(workitemId, affair, summaryId, nullColOpinion, null, null, null, user);
            affair.setArchiveId(archiveId);
        }else if (pageType.equals(PAGE_TYPE_FINISH)) {
        	affair.setArchiveId(archiveId);
        }else{
        	
        }
        affairManager.updateAffair(affair);
        //更新全文检索库
        UpdateIndexManager updateIndexManager = (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
        try{
        	updateIndexManager.update(affair.getObjectId(), ApplicationCategoryEnum.collaboration.getKey());
        }catch(Exception e){
        	log.error(e);
        }
    }
    
    

    public List<ColSummaryModel> queryDraftList(List<Long> templeteIds) throws ColException {
        return queryByCondition("", null, null,StateEnum.col_waitSend.key(), templeteIds);
    }
  //成发集团项目 程炯 2012-8-31 重写queryDraftList
    public List<ColSummaryModel> queryDraftList(List<Long> templeteIds,Integer secretLevel) throws ColException {
        return queryByCondition("", null, null,StateEnum.col_waitSend.key(), templeteIds,secretLevel);
    }

    // 查询批复意见列表
    @SuppressWarnings("unchecked")
    public List<ColOpinion> queryOpinionListBySummaryId(long summaryId) throws ColException {
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColOpinion.class)
    		.add(Expression.eq("summaryId", summaryId))
    		.addOrder(Order.asc("createDate"))
    	;
    	return super.executeCriteria(criteria, -1, -1);
    }

    public List<ColSummaryModel> queryFinishedList(List<Long> templeteIds) throws ColException {
        return queryByCondition("", null, null,  StateEnum.col_done.key(), templeteIds);
    }
    //成发集团项目 程炯 2012-8-31 重写queryFinishedList
    public List<ColSummaryModel> queryFinishedList(List<Long> templeteIds,Integer secretLevle) throws ColException {
        return queryByCondition("", null, null,  StateEnum.col_done.key(), templeteIds,secretLevle);
    }

    public List<ColSummaryModel> querySentList(List<Long> templeteIds) throws ColException {
        return queryByCondition("", null, null,  StateEnum.col_sent.key(), templeteIds);
    }
  //成发集团项目 程炯 2012-8-31 重写querySentList
    public List<ColSummaryModel> querySentList(List<Long> templeteIds,Integer secretLevel) throws ColException {
        return queryByCondition("", null, null,  StateEnum.col_sent.key(), templeteIds,secretLevel);
    }

    public List<ColSummaryModel> queryTodoList(List<Long> templeteIds) throws ColException {
        return queryByCondition("", null, null,  StateEnum.col_pending.key(), templeteIds);
    }

    /**
	 * 2008-03-15 by jincm
	 * @param flowData
	 * @param summary
	 * @param body
	 * @param senderOpinion
	 * @param sendType
	 * @param options
	 * @param isNew
	 * @return 成功返回AffairId, 失败返回-1
	 */
    public Long runCase(FlowData flowData, ColSummary summary, ColBody body, ColOpinion senderOpinion, Constant.SendType sendType, Map options, boolean isNew, Long senderId, String...
            newProcessId)
            throws ColException {
        Long affairId = -1L;
        boolean isResend = (Constant.SendType.resend.ordinal() == sendType.ordinal());
        if(senderId == null){
        	senderId = CurrentUser.get().getId();
    	}
        V3xOrgMember sender = null;
        try {
			sender = this.orgManager.getMemberById(senderId);
		} catch (BusinessException e1) {
			throw new ColException(e1);
		}
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try {
            String processId = summary.getProcessId();
            //如果先创建好了Process，再调用runcase，则不需要再重新生成
            if(newProcessId != null && newProcessId.length > 0 && Strings.isNotBlank(newProcessId[0])){
                processId = newProcessId[0];
            }
            else{
                if (processId != null && !processId.trim().equals("")){
                    ColHelper.deleteReadyProcess(processId);
                    processId = null;
                }
                //根据选人界面传来的people生成流程模板XML
                processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false, sender);
            }
            if(processId == null){
            	return affairId; //-1L
            }
            //生成流程模板对象

            if(!isNew){ //非新建环节，删除原有的
            	this.delete(summary.getId().longValue());
            	if(Strings.isNotBlank(summary.getProcessId())){
            		processLogManager.deleteLog(Long.parseLong(summary.getProcessId()));
            	}
            	affairManager.deleteByObject(ApplicationCategoryEnum.collaboration, summary.getId());
            }
            //保存colsummary、body
            //summary.setCaseId(caseId);
            summary.setIdIfNew();
            if (isResend) {
                summary.setResentTime(summary.getResentTime() == null ? 1 : summary.getResentTime() + 1);
            }
            
            summary.setCreateDate(now);
            summary.setProcessId(processId);
            summary.setStartDate(now);
            
            summary.setStartMemberId(senderId);
            summary.setBodyType(body.getBodyType());
            
            body.setIdIfNew();
            body.setSummaryId(summary.getId());
            body.setUpdateDate(now);
            
            summary.getBodies().add(body);
           
            //附言内容为空，就不记录了
            if (StringUtils.isNotBlank(senderOpinion.getContent())) {
                senderOpinion.setSummaryId(summary.getId());
                senderOpinion.setOpinionType(ColOpinion.OpinionType.senderOpinion.ordinal());
                senderOpinion.setCreateDate(now);
                senderOpinion.setWriteMemberId(senderId);
                summary.getOpinions().add(senderOpinion);
            }

            Affair affair = new Affair();
            affair.setIdIfNew();
            affair.setApp(ApplicationCategoryEnum.collaboration.key());
            affair.setSubject(summary.getSubject());
            affair.setCreateDate(now);
            affair.setReceiveTime(now);
            affair.setMemberId(senderId);
            affair.setObjectId(summary.getId());
            affair.setSubObjectId(null);
            affair.setSenderId(senderId);
            affair.setState(StateEnum.col_sent.key());
            affair.setSubState(SubStateEnum.col_normal.key());
            affair.setIsTrack(senderOpinion.affairIsTrack);
            affair.setIsDelete(false);
            affair.setTempleteId(summary.getTempleteId());
            
            ColHelper.addExtPropsToAffairFromSummary(affair, summary, body);
            
            Long _deadline = summary.getDeadline();
            if (_deadline != null && _deadline.intValue() != 0) {
                affair.setDeadlineDate(_deadline);
            }
            Long _remindTime = summary.getAdvanceRemind();
            if(_remindTime != null && _remindTime.intValue() != -1){
            	affair.setRemindDate(_remindTime);
            }
            
            save(affair);
            super.getSession().flush();
            
            WorkflowEventListener.setColSummary(summary);
            
          //从流程数据flowData中获得表单信息
            BPMProcess process = ColHelper.getProcess(processId);
            BPMSeeyonPolicy seeyonPolicy = process.getStart().getSeeyonPolicy();
			String formApp = seeyonPolicy.getFormApp() ;
			//表单数据对应的主表记录主键值
			String mastrid = null ;
			if(summary != null){
				//从协同中获得协同主体信息
    			body = summary.getFirstBody();
    		}
			if(body != null){
				//获得表单数据对应的主表记录主键值
    			mastrid = body.getContent() ;
    		}
			Map<String, Object> data = new HashMap<String, Object>();
			if(body.getBodyType().equals("FORM")){//表单类型的数据
				Map<String,String[]> fieldDataBaseMap= FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
				data.put("fieldValueMap", fieldDataBaseMap);
			}
			data.put("CurrentActivity", process.getStart());
			data.put("currentWorkitemId", -1l);//发起者
            data.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, senderId.toString());
            //运行流程实例，并返回caseId
            //long caseId = ColHelper.runCase(processId, sender.getId());
            long caseId = ColHelper.runCaseWithContext(processId, sender.getId(),data);
            
            summary.setCaseId(caseId);
            
//            super.getSession().clear();
            //super.getSession().delete(summary);
            save(summary);
            
            affairId = affair.getId();
            if(summary.getArchiveId() != null){
    			//Long affairId = affair.getId();
    			int appName = ApplicationCategoryEnum.collaboration.getKey();
    			try{
    				//DocHierarchyManager docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
    				Long resourceid = getDocHierarchyManager().pigeonholeAfterPre(appName, affairId, summary.isHasAttachments(), summary.getArchiveId(), senderId);
    				//表单预归档后存储视图操作等信息
    				getDocHierarchyManager().pigeonFormpotent(affairId, resourceid, summary.getArchiverFormid());
    			}catch(Exception e){
    				log.error("Collaboration runcase 预归档错误", e);
    				throw new ColException("Collaboration runcase 预归档错误");
    			}
            }

            ColHelper.createQuartzJobOfSummary(summary, workTimeManager);
        } 
        catch(Exception e){
        	log.error("Collaboration runcase Failed", e);
        	throw new ColException("Collaboration runcase Failed");
        }
        
        return affairId;
    }
    
    public void delete(long summaryId){
    	super.delete(ColComment.class, new Object[][]{{"summaryId", summaryId}});
    	super.delete(ColOpinion.class, new Object[][]{{"summaryId", summaryId}});
		super.delete(ColBody.class, new Object[][]{{"summaryId", summaryId}});
    	super.delete(summaryId);
    }

    public Long saveDraft(FlowData flowData, ColSummary summary, ColBody body, ColOpinion senderOpinion, boolean isNew)
            throws ColException {
        User user = CurrentUser.get();
        String processId = summary.getProcessId();
        
        ProcessDefManager pdm = null;
        try {
            pdm = WAPIFactory.getProcessDefManager("Engine_1");
        } catch (Exception ex) {
            throw new ColException("获取引擎对外接口异常[SaveDraft]", ex);
        }
        if (processId != null && !processId.trim().equals("")){
            try {
				pdm.deleteProcessInReady("admin", processId);
			} catch (BPMException e) {
				throw new ColException("删除ReadyProcess异常", e);
			}
            processId = null;
        }
            
        //生成流程模板对象
        if (!flowData.isEmpty()) {
            processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId,false);
            try {
				ColHelper.addRunningProcess(processId);
				pdm.deleteProcessInReady("admin", processId);
			} catch (BPMException e) {
				log.error("保存待发process异常 [processId =" + processId + "]", e);
				throw new ColException("Save Process Failed");
			}
        }

        if(!isNew){ //非新建环节，删除原有的
	        delete(summary.getId().longValue());
	        affairManager.deleteByObject(ApplicationCategoryEnum.collaboration, summary.getId());
        }
        
        Timestamp now = new Timestamp(System.currentTimeMillis());

        //保存colsummary、body
        summary.setCaseId(null);
        summary.setCreateDate(now);
        summary.setProcessId(processId);
        summary.setStartDate(null);
        summary.setStartMemberId(user.getId());      
        summary.setBodyType(body.getBodyType());

        body.setUpdateDate(now);
        body.setSummaryId(summary.getId());
        summary.getBodies().add(body);        

        //附言内容为空，就不记录了
        if (StringUtils.isNotBlank(senderOpinion.getContent())) {
            senderOpinion.setSummaryId(summary.getId());
            senderOpinion.setOpinionType(ColOpinion.OpinionType.senderOpinion.ordinal());
            senderOpinion.setCreateDate(now);
            senderOpinion.setWriteMemberId(user.getId());
            
            summary.getOpinions().add(senderOpinion);
        }
        
        Affair affair = new Affair();
        affair.setIdIfNew();
        affair.setApp(ApplicationCategoryEnum.collaboration.key());
        affair.setSubject(summary.getSubject());
        affair.setCreateDate(now);
        affair.setUpdateDate(now);
        affair.setMemberId(user.getId());
        affair.setObjectId(summary.getId());
        affair.setSubObjectId(null);
        affair.setSenderId(user.getId());
        affair.setState(StateEnum.col_waitSend.key());
        affair.setSubState(SubStateEnum.col_waitSend_draft.key());
        affair.setIsDelete(senderOpinion.isDeleteImmediate);
        affair.setIsTrack(senderOpinion.affairIsTrack);
        
        ColHelper.addExtPropsToAffairFromSummary(affair, summary, body);
        
        
//        super.getSession().clear();
//        super.getSession().delete(summary);
        
        save(summary);
        save(affair);
        
        //存为草搞只需要将archiveId保存到summary中，不进行实质性的归档
        /*if(summary.getArchiveId() != null){
			Long affairId = affair.getId();
			int appName = ApplicationCategoryEnum.collaboration.getKey();
			try{
				DocHierarchyManager docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
				docHierarchyManager.pigeonholeAfterPre(appName, affairId, summary.isHasAttachments(), summary.getArchiveId(), user.getId());
			}catch(Exception e){
				log.error("发送协同预归档异常 [affairId = " + affairId + "]", e);
				throw new ColException("Collaboration Pigeonhole Failed");
			}
        }*/

        return affair.getId();
    }

    public void addPost(long summaryId, String content) throws ColException {
    	 ColSummary summary = get(summaryId);
        if (summary == null) {
            throw new ColException("summary not found: summaryId=" + summaryId);
        }

        User user = CurrentUser.get();
        ColOpinion opinion = new ColOpinion();
        opinion.setId(UUIDLong.longUUID());
        opinion.setSummaryId(summary.getId());
        opinion.setContent(content);
        opinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
        opinion.setWriteMemberId(user.getId());

        save(opinion);
    }

    @SuppressWarnings("unchecked")
    public ColSummary getSummaryByCaseId(long caseId) throws ColException {
        List<ColSummary> list = super.findBy("caseId", caseId);
        if (list.size() == 0) {
            return null;
        }

        ColSummary summary = (ColSummary) list.get(0);

        return summary;
    }

    //根据caseId得到对应的summary
    public ColSummary getSummaryByWorkItemId(long workItemId) throws ColException {
        Long caseId = ColHelper.getCaseIdByWorkitem(workItemId);
        if (caseId == null) {
            return null;
        }

        return getSummaryByCaseId(caseId);
    }

    //更新指定summary的caseId字段
    public void updateCaseIdOfSummary(long summaryId, long caseId) throws ColException {
        Map<String, Object> columns = new HashMap<String, Object>();
        columns.put("caseId", caseId);

        super.update(summaryId, columns);
    }

    public String getCaseLogXML(long caseId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        return ColHelper.getCaseLogXML(userId, caseId);
    }
    
    public String getHisCaseLogXML(long caseId) throws ColException {
    	String userId = CurrentUser.get().getId() + "";
    	return ColHelper.getHisCaseLogXML(userId, caseId);
    }

    public String getCaseWorkItemLogXML(long caseId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        return ColHelper.getCaseWorkItemLogXML(userId, caseId);
    }
    
    public String getHisCaseWorkItemLogXML(long caseId) throws ColException {
    	String userId = CurrentUser.get().getId() + "";
    	return ColHelper.getHisCaseWorkItemLogXML(userId, caseId);
    }

    public String getCaseProcessXML(long caseId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        return ColHelper.getCaseProcessXML(userId, caseId);
    }

    public int stepBackSummary(long userId, long summaryId, int from) throws ColException{
        ColSummary summary = get(summaryId);
        
        WorkflowEventListener.setOperationType(WorkflowEventListener.WITHDRAW);
        List<Affair> affairs = affairManager.findByObject(ApplicationCategoryEnum.collaboration, summaryId);
        //获取所有待办事项
        List<Affair> colPendingAffairList = new ArrayList<Affair>();
        for(Affair pendingAffair : affairs){
        	if(pendingAffair.getState() == StateEnum.col_pending.key()){
        		colPendingAffairList.add(pendingAffair);
        	}
        }
        
        Long caseId = summary.getCaseId();
        int result = 0;
        if (caseId != null) {
            result = ColHelper.cancelCaseForce(caseId);
        }

        if (result == 1) {
            return result;
        }

        //将summary的状态改为待�发
        summary.setCaseId(null);
        update(summary);

        if(affairs != null){
        	for(int i=0;i<affairs.size();i++){
        		Affair affair = (Affair) affairs.get(i);
	        	if(affair.getState() == StateEnum.col_sent.key()){
	        		affair.setState(StateEnum.col_waitSend.key());
	                affair.setSubState(SubStateEnum.col_waitSend_stepBack.key());
	                affair.setArchiveId(null);
	                affair.setIsDelete(false);
	                affairManager.updateAffair(affair);
	        	}
        	}
        	this.affairManager.cancelWorkflow(summaryId);
        }
        //TODO 写日�志
        //logger.debug("summary is cancelled:" + summaryId);
        return 0;
    }
    public int cancelSummary(long userId, long summaryId, ColOpinion signOpinion, boolean isSaveOpinion, String repealComment) throws ColException {
    	ColSummary summary = get(summaryId);
    	return cancelSummary(userId, summary, signOpinion, isSaveOpinion, repealComment);
    }
    
    public int cancelSummary(long userId,ColSummary summary, ColOpinion signOpinion, boolean isSaveOpinion, String repealComment) throws ColException {
    	int result = 0;
        User user = CurrentUser.get();
        
        	
        WorkflowEventListener.setOperationType(WorkflowEventListener.CANCEL);
        Long caseId = summary.getCaseId();
        if (caseId == null) 
        	return 1;
        
        //将summary的状态改为待�发,撤销已生成事项
        List<Affair> affairs = affairManager.getALLAvailabilityAffairList(summary.getId(), false);
        
        //NF 如果该流程触发了新流程，则召回（撤销）新流程
        boolean hasNewflow = summary.getNewflowType()!=null && summary.getNewflowType().intValue() == Constant.NewflowType.main.ordinal();
        List<NewflowRunning> runList = null;
        if(hasNewflow){
            try{
                /* 这里已转交AJAX校验。
                String finishedNewflowTitle = newflowManager.getFinishedNewflow(summaryId, null);
                if(Strings.isNotBlank(finishedNewflowTitle)){
                    //如果该流程触发的新流程已结束，不能撤销
                    String subject = "《" + finishedNewflowTitle + "》";
                    WebUtil.saveAlert("col.repeal.newflowEnd.alert", subject);
                    return -2; //该节点触发的新流程已结束，不能取回
                }
                else{
                }*/
                runList = newflowManager.getChildflowRunningList(summary.getId(), null);
            }
            catch(Exception e){
                log.error("取回操作时召回新流程异常：", e);
            }
        }
        
        
		if(affairs != null){
			List<Long> affairIds = new ArrayList<Long>();
			
	    	for(Affair affair : affairs){
	    		if(StateEnum.col_sent.key()==affair.getState().intValue() || affair.getArchiveId() != null){
    				affairIds.add(affair.getId());
    			}
	        	if(affair.getState()==StateEnum.col_sent.key()){
	        		affair.setState(StateEnum.col_waitSend.key());
			        affair.setSubState(SubStateEnum.col_waitSend_cancel.key());
			        affair.setIsDelete(false);
			        
			        affairManager.updateAffair(affair);
	        	}
	    	}
	    	
	    	//撤销流程
	        result = ColHelper.cancelCaseForce(caseId);
	        if (result == 1) {
	            return result;
	        }
	    	if(!affairIds.isEmpty()){
	    		//DocHierarchyManager docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
	    		try {
	    			getDocHierarchyManager().deleteDocByResources(affairIds, user);
				} catch (Exception e) {
					log.error("删除归档文档:"+e);
				}
	    	}
		}
        
        summary.setCaseId(null);
        update(summary);
        
        ColHelper.deleteQuartzJobOfSummary(summary);
        
        if(result == 0)
        {
            if(isSaveOpinion){
            	Affair affair = affairManager.getById(signOpinion.getAffairId());
                signOpinion.setSummaryId(summary.getId());
                signOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
                signOpinion.setOpinionType(ColOpinion.OpinionType.cancelOpinion.ordinal());
//                affair.setIsTrack(signOpinion.affairIsTrack);
                
                if(affair.getMemberId() != user.getId() && user.getAgentToId() != -1){
                	signOpinion.setWriteMemberId(user.getAgentToId());
                	signOpinion.setProxyName(user.getName());
                }else{
                	signOpinion.setWriteMemberId(user.getId());
                }
                
                save(signOpinion);
    
//                if (signOpinion.isDeleteImmediate) {
//                    affairManager.deleteAffair(affair.getId());
//                }
//                if (signOpinion.affairIsTrack) {
//                    affairManager.updateAffair(affair);
//                }
            }
        	List<Long> summaryidlist = new ArrayList<Long>();
            //NF      如果成功撤销，则撤销当前流程触发的所有子流程
            if(runList != null && !runList.isEmpty()){
            	FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
                for (NewflowRunning running : runList) {
                    //撤销子流程
                	Long newFlowSummaryId  = running.getSummaryId();
                	summaryidlist.add(newFlowSummaryId);
                    int recallResult = recallNewflowSummary(running.getSummaryId(), user, "repeal");
                    if(recallResult == 0){
                        running.setMainCaseId(null);
                        running.setMainFormId(null);
                        running.setMainProcessId(null);
                        running.setSenderId(null);
                        running.setSummaryId(null);
                        running.setProcessId(null);
                        running.setAffairId(null);
                        running.setIsActivate(false);
                        running.setIsDelete(false);
                        running.setAffairState(StateEnum.col_cancel.key());
                        newflowManager.updateNewflowRunning(running);
                    }
                    //撤销子流程的时候将子流程的相关督办信息全部删除。
                    getColSuperviseManager().updateStatusAndNoticeSupervisorWithoutMes(newFlowSummaryId, Constant.superviseType.summary.ordinal(), 
                    		Constant.superviseState.waitSupervise.ordinal());
                }
                //撤销子流程时将表单状态表中相应数据删除
                try {
					formDaoManager.delByCondition(summaryidlist);
				}
                catch (DataDefineException e) {
					log.error(summaryidlist, e);
				}
                
            }
        }
        
        //撤销协同工作项操作日志
        //operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
        //		Constant.OperationLogActionType.cancelColl.name(), "col.operationlog.cancelColl", user.getName(), new java.util.Date(), summary.getSubject());
        //删除流程日志
       // processLogManager.deleteLog(Long.parseLong(summary.getProcessId()));
        
        //对发起人以外的所有执行人发消息通知
        try{
            if(null!=affairs && affairs.size()>0){
                Integer importantLevel = affairs.get(0).getImportantLevel();
                Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<MessageReceiver> receivers1 = new ArrayList<MessageReceiver>();
                Set<Long> receiverIds = new HashSet<Long>(); //同一个人在这个流程中出现多次，只收一条消息就够了
                for(Affair affair1 : affairs){
                    Long agentMemberId = null;
                    if(affair1.getIsDelete() || receiverIds.contains(affair1.getMemberId())){
                        continue;
                    }
                    if(affair1.getMemberId()==userId){continue;}
                    if(affair1.getState() == StateEnum.col_waitSend.key()){
                        receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId(),"message.link.col.done",affair1.getId().toString()));
                    }else{
                        agentMemberId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.collaboration.key(),affair1.getMemberId());
                        if(agentMemberId != null){
                            receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId()));
                            receivers1.add(new MessageReceiver(affair1.getId(), agentMemberId));
                        }else
                        receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId()));
                    }
                    receiverIds.add(affair1.getMemberId());
                }
                String forwardMemberId = affairs.get(0).getForwardMember();
                int forwardMemberFlag = 0;
                String forwardMember = null;
                if(Strings.isNotBlank(forwardMemberId)){
                    try {
                        forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
                        forwardMemberFlag = 1;
                    }
                    catch (Exception e) {
                    }
                }
                userMessageManager.sendSystemMessage(new MessageContent("col.cancel", affairs.get(0).getSubject(), user.getName(), repealComment, forwardMemberFlag, forwardMember)
                    .setImportantLevel(importantLevel),
                        ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
                if(receivers1 != null && receivers1.size() != 0){
                     userMessageManager.sendSystemMessage(new MessageContent("col.cancel", affairs.get(0).getSubject(), user.getName(), repealComment, forwardMemberFlag, forwardMember)
                        .add("col.agent").setImportantLevel(importantLevel),
                                ApplicationCategoryEnum.collaboration, user.getId(), receivers1, importantLevel);   
                }
            }
        }catch (MessageException e) {
            log.error("撤销协同发送提醒消息异常", e);
            throw new ColException("send message failed");
        }
        this.affairManager.cancelWorkflow(summary.getId());
        return 0;
    }

    //加签
    public void insertPeople(ColSummary summary, Affair affair, FlowData flowData, BPMProcess process, User user, boolean isFormOperationReadonly) throws ColException {
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
        WorkflowEventListener.setOperationType(WorkflowEventListener.INSERT);
        List<String> memAndPolicy = ColHelper.insertPeople(summary.getCaseId(), affair.getSubObjectId(), flowData, process, user.getId()+"", isFormOperationReadonly);
        
        //加签消息提醒
        Long summaryId = summary.getId();
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("insertPeople");
        messageData.setHandlerId(user.getId());
        messageData.setSummary(summary);
        messageData.setAffair(affair);
        List<Party> partyList = flowData.getPeople();
        List<String> partyNames = new ArrayList<String>();
        for(Party party : partyList){
        	if(party != null){
        		partyNames.add(party.getName());
        	}
        }
        messageData.addProcessLogParam(Functions.join(memAndPolicy,","));
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
    }
  //复杂加签
    public void insertComplexPeople(ColSummary summary, Affair affair, User user,List<String> memAndPolicy,List<String> partyNames) throws ColException {
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
        WorkflowEventListener.setOperationType(WorkflowEventListener.INSERT);
        //加签消息提醒
        Long summaryId = summary.getId();
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("insertPeople");
        messageData.setHandlerId(user.getId());
        messageData.setSummary(summary);
        messageData.setAffair(affair);
        messageData.addProcessLogParam(Functions.join(memAndPolicy,","));
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
    }
    
    //减签前返回可减签的人员列表
    public FlowData preDeletePeople(long summaryId, long affairId, String processId, String userId) throws ColException {
        ColSummary summary = get(summaryId);
        if (summary == null) {
            return null;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return null;
        }
        Long caseId = summary.getCaseId();
        Long _workitemId = affair.getSubObjectId();
        FlowData flowData = ColHelper.preDeletePeople(caseId, _workitemId, processId, userId);
        return flowData;
    }

    public FlowData deletePeople(ColSummary summary, Affair affair, List<Party> parties, String userId) throws ColException {
    	Long caseId = summary.getCaseId();
        Long _workitemId = affair.getSubObjectId();
        WorkflowEventListener.setOperationType(WorkflowEventListener.DELETE);
        FlowData flowData = ColHelper.deletePeople(caseId, _workitemId, parties, summary.getProcessId(), userId);
        return flowData;
    }
    
    //通过processId得到相应的xml定义文件
    public String getProcessXML(String processId) throws ColException {
        String xml = ColHelper.getProcessXML(processId);
        xml = ColHelper.trimXMLProcessor(xml);
        return xml;
    }
   
    //@return true:成功终止 false:终止失败 管理员终止流程
    public boolean stepStop(Long summaryId, Affair curerntAffair, ColOpinion signOpinion, User user) throws ColException {
    	ColSummary summary = getColSummaryById(summaryId, false);
        if (summary == null) {
            return false;
        }
        //事务原因
//        Affair curerntAffair = null;
//        if(affairId != null){
//        	curerntAffair = affairManager.getById(affairId);
//            
//            if (curerntAffair == null) {
//                return false;
//            }
//            
//            if (signOpinion.isDeleteImmediate) {
//            	curerntAffair.setIsDelete(true);
//            }
//            
//            if(curerntAffair.getMemberId() != user.getId()){  //由代理人终止需要写入处理人ID
//                curerntAffair.setTransactorId(user.getId()); 
//                signOpinion.setProxyName(user.getName());
//            }
//            
//            affairManager.updateAffair(curerntAffair);
//        }
//        else if(affairId == null && (user.isAdministrator() || user.isGroupAdmin())){
//	        //TODO 这种做法有悖人道
//	        List<Affair> curerntAffairs = affairManager.getPendingAffairListByObject(summaryId);
//	        if(curerntAffairs != null && !curerntAffairs.isEmpty()){
//	        	try {
//					curerntAffair = (Affair)org.apache.commons.beanutils.BeanUtils.cloneBean(curerntAffairs.get(0));
//					curerntAffair.setMemberId(user.getId());
//				}
//				catch (Exception e) {
//					log.error("", e);
//				}
//	        }
//	        
//	        if (curerntAffair == null) {
//	            return false;
//	        }
//        }

        //将终止流程的当前Affair放入ThreadLocal，便于工作流中发送消息时获取代理信息。
        DateSharedWithWorkflowEngineThreadLocal.setColSummary(summary);
        DateSharedWithWorkflowEngineThreadLocal.setTheStopAffair(curerntAffair);
        DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), signOpinion.getContent(), signOpinion.getAttitude(), signOpinion.isHasAtt());
        
        signOpinion.setSummaryId(summaryId);
        signOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
        signOpinion.setOpinionType(ColOpinion.OpinionType.stopOpinion.ordinal());
        signOpinion.setWriteMemberId(curerntAffair.getMemberId());
        save(signOpinion);
        
    	Long _workitemId = curerntAffair.getSubObjectId();
        
        WorkflowEventListener.setOperationType(WorkflowEventListener.STETSTOP);
        
        try{
        	log.info("流程终止："+_workitemId);
        	ColHelper.stopWorkitem(_workitemId);
        }
        catch(Exception e){
        	log.error("", e);
        }
        
        try {
        	if(user.isAdministrator() || user.isGroupAdmin() || user.isSystemAdmin()){
        		processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), 1l, ProcessLogAction.stepStop);
        	}
        	else{
        		BPMActivity activity = ColHelper.getBPMActivityByAffair(curerntAffair);
        		processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), Long.parseLong(activity.getId()), ProcessLogAction.stepStop);
        	}
        	appLogManager.insertLog(user, AppLogAction.Coll_Flow_Stop, user.getName(), summary.getSubject());
        	
        	//流程正常结束通知
        	CollaborationStopEvent stopEvent = new CollaborationStopEvent(this);
        	stopEvent.setSummaryId(summary.getId());
        	stopEvent.setUserId(user.getId());
        	EventDispatcher.fireEvent(stopEvent);
        }
		catch (Exception e) {
			log.warn("", e);
		}
        
        return true;
    }

    //回退
    //@return true:成功回退 false:不允许回退
    public boolean stepBack(ColSummary summary, Affair affair, ColOpinion signOpinion, User user,Boolean isFirst) throws ColException {
    	//ColSummary summary = get(summaryId);
        if (summary == null) {
            return false;
        }
        //事务原因
//        Affair affair = affairManager.getById(affairId);
//        if (affair == null) {
//            return false;
//        }
        
        

        Long _workitemId = affair.getSubObjectId();

        //String processId = summary.getProcessId();
        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.WITHDRAW);
        BPMProcess process = ColHelper.getRunningProcessByCaseId(caseId);
        WorkItem workitem = ColHelper.getWorkItemById(_workitemId);
        BPMActivity activity = process.getActivityById(workitem.getActivityId());
        
        Map resultMap= Utils.isAllHumenNodeValid(activity);
        String result_str= (String)resultMap.get("result");
        int result =0;
        List<NewflowRunning> runList = null;
        if("0".equals(result_str) || "1".equals(result_str)){//正常回退或撤销，都得对后续的节点的子流程进行删除操作
        	Map normalNodes= (Map)resultMap.get("normal_nodes");
        	if("1".equals(result_str)){//撤销，放入start节点信息
        		normalNodes.put(process.getStart().getId(), process.getStart().getId());
        	}
        	List<String> prevNodeIds= Utils.getAllNFNodes(normalNodes,process);
        	//NF 如果前一节点触发了新流程，则召回（撤销）新流程
            //List<String> prevNodeIds = ColHelper.checkPrevNodeHasNewflow(activity);
            boolean hasNewflow = (prevNodeIds!=null && !prevNodeIds.isEmpty() && summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.main.ordinal());
            if(hasNewflow){
                try{
                    String finishedNewflowTitle = newflowManager.getFinishedNewflow(summary.getId(), prevNodeIds);
                    if(Strings.isNotBlank(finishedNewflowTitle)){
                        //该节点的上节点触发的新流程已结束，不能取回
                        String subject = "《" + finishedNewflowTitle + "》";
                        WebUtil.saveAlert("col.stepBack.newflowEnd.alert", subject);
                        return false;
                    }
                    else{
                        runList = newflowManager.getChildflowRunningList(summary.getId(), prevNodeIds);
                    }
                }
                catch(Exception e){
                    log.error("回退操作时召回新流程异常：", e);
                    return false;
                    //throw new ColException("回退操作时召回新流程异常：", e);
                }
            }
        }
        List<Affair> aLLAvailabilityAffairList = affairManager.queryALLAvailabilityAffairList(summary.getId());
        List<Affair> trackingAffairLists = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
        if("0".equals(result_str)){//正常回退
            result = ColHelper.stepBack(caseId, _workitemId, process, workitem, activity);
        }else{
        	result= Integer.parseInt(result_str);
        }
        if(result==0 || result==1){
            signOpinion.setSummaryId(summary.getId());
            signOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
            signOpinion.setOpinionType(ColOpinion.OpinionType.backOpinion.ordinal());
            affair.setIsTrack(signOpinion.affairIsTrack);
            //普通A8BUG_V3.20SP1_珠海华发实业股份有限公司 _代理人代理的事项没有标识“由某某代处理”_20131206021387 
            if(affair.getMemberId() != user.getId() /*&& user.getAgentToId() != -1*/){
            	signOpinion.setWriteMemberId(affair.getMemberId());
            	signOpinion.setProxyName(user.getName());
            }else{
            	signOpinion.setWriteMemberId(user.getId());
            }
            
            save(signOpinion);
            if (signOpinion.isDeleteImmediate) {
                affairManager.deleteAffair(affair.getId());
            }
            if (signOpinion.affairIsTrack) {
                affairManager.updateAffair(affair);
            }
            List<Long> summaryidlist = new ArrayList<Long>();
            //NF 正常回退，收回上节点触发的新流程
            if(runList != null && !runList.isEmpty()){
            	FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
                for (NewflowRunning running : runList) {
                	summaryidlist.add(running.getSummaryId());
                    //撤销子流程, 这里不能放入待发，置为已删除。
                    int recallResult = recallNewflowSummary(running.getSummaryId(), user, "stepBack");
                    if(recallResult == 0){
                        running.setMainCaseId(null);
                        running.setMainFormId(null);
                        running.setMainProcessId(null);
                        running.setSenderId(null);
                        running.setSummaryId(null);
                        running.setProcessId(null);
                        running.setAffairId(null);
                        running.setIsActivate(false);
                        running.setIsDelete(false);
                        running.setAffairState(StateEnum.col_stepStop.key());
                        newflowManager.updateNewflowRunning(running);
                    }
                }
                //撤销子流程时将表单状态表中相应数据删除
                try {
					formDaoManager.delByCondition(summaryidlist);
				}
                catch (Exception e) {
					log.error(summaryidlist, e);
				}
            }
        }
        
        if(result == 0){
        	affair.setState(StateEnum.col_stepBack.key());
        	affair.setSubState(SubStateEnum.col_normal.key());
        	affairManager.updateAffair(affair);
        }
        
        if(result == -2){
        	StringBuffer info = new StringBuffer();
        	info.append("《").append(affair.getSubject()).append("》").append("\n");
        	WebUtil.saveAlert("col.takeBack.alert", info.toString());
        	return false;
        }else if(result == -1){
        	StringBuffer info = new StringBuffer();
        	info.append("《").append(affair.getSubject()).append("》").append("\n");
        	WebUtil.saveAlert("col.takeBack.alert.dimission", info.toString());
        	return false;
        }
        //需要撤消流程
        if (result == 1) {
            stepBackSummary(CurrentUser.get().getId(), summary.getId(), StateEnum.col_stepBack.key());
            result = 0;
            
            //设置变量发回退消息。
            for(Affair affair0 : aLLAvailabilityAffairList){
            	DateSharedWithWorkflowEngineThreadLocal.addToAllStepBackAffectAffairMap(affair0.getMemberId(), affair0.getId());
            }
            //撤销，则在应用日志中记录撤销记录
            appLogManager.insertLog(user, AppLogAction.Coll_Repeal, user.getName(), summary.getSubject());
        }
        
        //协同回退操作日志
        //operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
        //		Constant.OperationLogActionType.stepBackItem.name(), "col.operationlog.stepBackItem", user.getName(), new java.util.Date(), summary.getSubject());
        
        if (result == 0) {
        	//流程回退事件
        	CollaborationStepBackEvent backEvent = new CollaborationStepBackEvent(this);
        	backEvent.setSummaryId(summary.getId());
        	EventDispatcher.fireEvent(backEvent);
        	
        	//回退消息提醒
        	Affair sentAffair = null;
        	List<Long> affairIds = new ArrayList<Long>();
        	for(Affair _sentAffair : aLLAvailabilityAffairList){
        		if(_sentAffair.getState() == StateEnum.col_sent.getKey() 
        				|| _sentAffair.getState() == StateEnum.col_waitSend.getKey()){
        			sentAffair = _sentAffair;
        			if(summary.getArchiveId() != null)
        				affairIds.add(_sentAffair.getId());
        			break;
        		}
        		if(summary.getArchiveId() != null){
        			affairIds.add(_sentAffair.getId());
        		}
        	}
        	
        	if(sentAffair == null){
        		sentAffair = this.affairManager.getCollaborationSenderAffair(summary.getId());
        	}
        	
        	if(sentAffair.getIsDelete().equals(true)){
        		super.bulkUpdate("update " + Affair.class.getName() + " set isDelete=? where id=?", null, Boolean.FALSE, sentAffair.getId());
        	}
        	
        	//发送回退消息
        	trackingAffairLists.add(sentAffair);
      	    List<ColTrackMember> trackMembers = getColTrackMembersByObjectIdAndTrackMemberId(summary.getId(),null);
            EdocMessageHelper.getTrackAffairExcludePart(trackingAffairLists, trackMembers,affair.getMemberId());
           
	        ColMessageHelper.stepBackMessage(affairManager, orgManager, userMessageManager, trackingAffairLists, affair, summary.getId(),signOpinion);
	        
	        List parentNode = ColHelper.getParent(activity);
	        StringBuffer members = new StringBuffer();
	        if(parentNode != null){
	        	int size = parentNode.size();
	        	for(int i = 0 ; i < size ; i ++){
	        		BPMAbstractNode parent = (BPMAbstractNode) parentNode.get(i);
	        		if(i != 0){
	        			members.append(",");
	        		}
	        		members.append(parent.getName()+"("+parent.getSeeyonPolicy().getName()+")");
	        	}
	        }
	        processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), Long.parseLong(activity.getId()), ProcessLogAction.stepBack, members.toString());
	        //流程日志
	        if(isFirst){
	        	//回退到首节点的时候，删除流程日志
	        	//processLogManager.deleteLog(Long.parseLong(summary.getProcessId()));//回退保留流程日志
	        	//删除归档文档
	        	if(summary.getArchiveId() != null && !affairIds.isEmpty()){
	        		//DocHierarchyManager docHierarchyManager = (DocHierarchyManager)ApplicationContextHolder.getBean("docHierarchyManager");
		    		try {
		    			getDocHierarchyManager().deleteDocByResources(affairIds, user);
					} catch (Exception e) {
						log.error("删除归档文档:"+e);
					}
        		}
	        	//流程撤销到首发，发送流程撤销事件
	        	CollaborationCancelEvent cancelEvent = new CollaborationCancelEvent(this);
	        	cancelEvent.setSummaryId(summary.getId());
	        	cancelEvent.setUserId(user.getId());
	        	cancelEvent.setMessage(signOpinion.getContent());
	        	EventDispatcher.fireEvent(cancelEvent);
	        }
            return true;
        } else {
            return false;
        }

    }

    //取回
    //@return true:成功取回 false:不允许取�hui 回
    public int takeBack(Long affairId, User user,boolean isSaveOpinion) throws ColException {
        Affair affair = affairManager.getById(affairId);
        int result = -1;
        if (affair == null) {
            return result;
        }

        Long summaryId = affair.getObjectId();
        ColSummary summary = get(summaryId);
        if (summary == null) {
            return result;
        }

        //获取协同所有待办事项
        int colAffairState = StateEnum.col_pending.key();
        List<Affair> pendingAffairList = affairManager.queryColPendingAffairList(summaryId, colAffairState);

        Long _workitemId = affair.getSubObjectId();

        //String processId = summary.getProcessId();
        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.TAKE_BACK);
        BPMProcess process = ColHelper.getRunningProcessByCaseId(caseId);
        WorkItem workitem = ColHelper.getWorkItemById(_workitemId);
        BPMActivity activity = process.getActivityById(workitem.getActivityId());
        
        //NF 如果该节点触发了新流程，则召回（撤销）新流程
        boolean hasNewflow = "1".equals(activity.getSeeyonPolicy().getNF()) && summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.main.ordinal();
        List<NewflowRunning> runList = null;
        if(hasNewflow){
            try{
                List<String> nodeIds = new ArrayList<String>();
                nodeIds.add(activity.getId());
                String finishedNewflowTitle = newflowManager.getFinishedNewflow(summaryId, nodeIds);
                if(Strings.isNotBlank(finishedNewflowTitle)){
                    //该节点的上节点触发的新流程已结束，不能取回
                    String subject = "《" + finishedNewflowTitle + "》";
                    WebUtil.saveAlert("col.takeBack.newflowEnd.alert", subject);
                    return -3; //该节点触发的新流程已结束，不能取回
                }
                else{
                    runList = newflowManager.getChildflowRunningList(summaryId, nodeIds);
                }
            }
            catch(Exception e){
                log.error("取回操作时召回新流程异常：", e);
            }
        }
        result = ColHelper.takeBack(caseId, _workitemId, process, workitem, activity);
    	List<Long> summaryidlist = new ArrayList<Long>();
        //如果成功取回，则撤销当前节点触发的子流程
        if(result == 0 && runList != null && !runList.isEmpty()){
        	FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");        	     
            for (NewflowRunning running : runList) {
            	summaryidlist.add(running.getSummaryId());
              //撤销子流程, 这里不能放入待发，置为已删除。
                int recallResult = recallNewflowSummary(running.getSummaryId(), user, "takeBack");
                if(recallResult == 0){
                    running.setMainCaseId(null);
                    running.setMainFormId(null);
                    running.setMainProcessId(null);
                    running.setSenderId(null);
                    running.setSummaryId(null);
                    running.setProcessId(null);
                    running.setAffairId(null);
                    running.setIsActivate(false);
                    running.setIsDelete(false);
                    running.setAffairState(StateEnum.col_takeBack.key());
                    newflowManager.updateNewflowRunning(running);
                }
            }
            //撤销子流程时将表单状态表中相应数据删除
            try {
				formDaoManager.delByCondition(summaryidlist);
			} catch (Exception e) {
				log.error(summaryidlist, e);
			}
        }
        
        //协同取回操作日志
        //operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
        //		Constant.OperationLogActionType.takeBackItem.name(), "col.operationlog.takeBackItem", user.getName(), new java.util.Date(), summary.getSubject());
        //更新事项状态
        if (result == 0) {
        	// 取回时对表单解锁
        	if("FORM".equals(summary.getBodyType()))
        	{
        		removeFormLock(summaryId);
        	}
        	affair.setState(StateEnum.col_pending.key());
        	affair.setSubState(SubStateEnum.col_pending_unRead.key());
        	affairManager.updateAffair(affair);
        	if(!isSaveOpinion){
        		updateOpinion2Draft(affair.getId(),summaryId);
        	}
        	ColMessageHelper.takeBackMessage(affairManager, orgManager, userMessageManager, pendingAffairList, affair, summaryId);
        	//流程日志
        	processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), Long.parseLong(activity.getId()), ProcessLogAction.takeBack);
        	//流程取回事件
        	CollaborationTakeBackEvent event = new CollaborationTakeBackEvent(this);
        	event.setSummaryId(summary.getId());
        	event.setUserId(user.getId());
        	EventDispatcher.fireEvent(event);
        }
        return result;
    }

    /**
     * 将处理意见变成草稿状态
     * @param affairId
     */
    private void updateOpinion2Draft(Long affairId,Long summaryId){
    	List<ColOpinion> allDelOpin = getDealOpinion(affairId);
    	if(!allDelOpin.isEmpty()){
    		ColOpinion opinion = allDelOpin.get(0);
    		opinion.setOpinionType(ColOpinion.OpinionType.draftOpinion);
    		super.update(opinion);
    		List<Attachment> attachments = this.attachmentManager.getByReference(summaryId, opinion.getId());
    		if(attachments!= null && !attachments.isEmpty()){
    			for(Attachment att : attachments){
    				att.setReference(opinion.getId());
    				this.attachmentManager.update(att);
    			}
    		}
    	}
    }
    
    public ColBody getColBody(long summaryId) throws ColException {
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColBody.class);
        criteria.add(Restrictions.eq("summaryId", summaryId));
        return (ColBody)super.executeUniqueCriteria(criteria);
    }

    /**
     * 会签
     *
     * @param flowData
     * @throws ColException
     */
    public void colAssign(Long summaryId, Long affairId, FlowData flowData, String userId) throws ColException {
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	 ColSummary summary = get(summaryId);
        if (summary == null) {
            return;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return;
        }
        Long _workitemId = affair.getSubObjectId();

        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.COL_ASSIGN);
        List<String> assignMembers = ColHelper.colAssign(caseId, _workitemId, flowData, summary.getProcessId(), userId);

        //会签消息提醒
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("colAssign");
        messageData.setHandlerId(Long.parseLong(userId));
        messageData.setSummary(summary);
        messageData.setAffair(affair);
        List<Party> partyList = flowData.getPeople();
        List<String> partyNames = new ArrayList<String>();
        for(Party party : partyList){
        	if(party != null){
        		partyNames.add(party.getName());
        	}
        }
        //流程日志
        messageData.addProcessLogParam(Functions.join(assignMembers,","));
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
        //Boolean ok = ColMessageHelper.colAssignMessage(userMessageManager, affairManager, orgManager, flowData, summary, affair);
    }

    //知会
    public void addInform(Long summaryId, Long affairId, FlowData flowData, String userId) throws ColException {
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	 ColSummary summary = get(summaryId);
        if (summary == null) {
            return;
        }
        Affair affair = affairManager.getById(affairId);
        if (affair == null) {
            return;
        }
        Long _workitemId = affair.getSubObjectId();
        Long caseId = summary.getCaseId();

        WorkflowEventListener.setOperationType(WorkflowEventListener.ADD_INFORM);
        List<String> informMem = ColHelper.addInform(caseId, _workitemId, flowData, summary.getProcessId(), userId);
        //知会消息提醒
        List<MessageData> messageDataList = null;
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
        MessageData messageData = new MessageData();
        messageData.setOperationType("addInform");
        messageData.setHandlerId(Long.parseLong(userId));
        messageData.setSummary(summary);
        messageData.setAffair(affair);
        List<Party> partyList = flowData.getPeople();
        List<String> partyNames = new ArrayList<String>();
        for(Party party : partyList){
        	if(party != null){
        		partyNames.add(party.getName());
        	}
        }
       
        messageData.addProcessLogParam(Functions.join(informMem,","));
        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
        //Boolean ok = ColMessageHelper.addInformMessage(userMessageManager, affairManager, orgManager, flowData, summary, affair);
    }

    //催办
    @SuppressWarnings("unchecked")
    public List<Long>  hasten(String summaryId, List<Long> people, String additional_remark) throws ColException {
        additional_remark = additional_remark == null ? "" : ". " + Constant.getString4CurrentUser("sender.note.label") + ":" + additional_remark;
        User user = CurrentUser.get();
        
        Set<Long> memberIds = new HashSet<Long>();
        for (Long l : people) {
        	memberIds.add(l);
		}
        
        List<Affair> affairs = affairManager.getPendingAffairListByObject(Long.parseLong(summaryId));
        Set<Long> canHasMember = new HashSet<Long>();
        if(affairs != null && !affairs.isEmpty()){
        	Affair hastenAffair = affairs.get(0);
            String subject = hastenAffair.getSubject();
            Integer importantLevel = hastenAffair.getImportantLevel();
            
            Set<Long> existMemberIds = new HashSet<Long>();
            try {
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
		        for (Affair affair : affairs) {
		        	Long memberId = affair.getMemberId();
		        	canHasMember.add(memberId);
		        	if(!memberIds.contains(memberId) || existMemberIds.contains(memberId)){
		        		continue;
		        	}
		        	
		        	existMemberIds.add(memberId);
		        	
		            //如果设置代理，催办消息发给代理人
	    			List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelToList(memberId);
	    			AgentModel collAgent = null;
	    			if(agentModelList != null && !agentModelList.isEmpty()){
		    			for(AgentModel agentModel : agentModelList){
		    				String[] agentOptions = agentModel.getAgentOption().split("&");
		    				for(String agentOption : agentOptions){
								if ("1".equals(agentOption)) {
									//sunzm 协同增加条件判断 只有有对应模板权限的才可以发到代理
									if ( agentModel.getAgentDetail() == null || agentModel.getAgentDetail().isEmpty()) {
										collAgent = agentModel;
										break;
									} else {
										for (V3xAgentDetailModel ad : agentModel.getAgentDetail()) {
											if (ad.getEntityId().equals(affair.getTempleteId()) || (ad.getEntityId() == 2L && affair.getTempleteId() != null) || (ad.getEntityId() == 1L && affair.getTempleteId() == null)) {
												collAgent = agentModel;
												break;
											}
										}
									}

								}
		    				}
		    			}
		    			if(collAgent != null){
		    				memberId = collAgent.getAgentId();
		    			}
	    			}
		
		            Integer hastenTimes = affair.getHastenTimes();
		            if (hastenTimes == null)
		                hastenTimes = 0;
		            hastenTimes += 1;
		            affair.setHastenTimes(hastenTimes);
		            affairManager.updateAffair(affair);
		            String urlKey="message.link.col.pending";
		            if(affair.getApp()!=ApplicationCategoryEnum.collaboration.getKey()){
		            	if(affair.getApp()==ApplicationCategoryEnum.info.getKey()) {//信息报送//branches_a8_v350_r_gov GOV-1162 魏俊彪 增加催办消息提醒连接路径并添加接收人
		            		urlKey="message.link.info.pending";
		            		receivers.add(new MessageReceiver(affair.getId(), memberId, urlKey, affair.getObjectId().toString(), affair.getId().toString()));
		            	} else {//公文及其它
		            		urlKey="message.link.edoc.pending";
				    		receivers.add(new MessageReceiver(affair.getId(), memberId, urlKey, affair.getId().toString()));
		            	}
		            } else {//协同
					    receivers.add(new MessageReceiver(affair.getId(), memberId, urlKey, affair.getId().toString()));
					}
		        }
		        if(hastenAffair.getApp() == ApplicationCategoryEnum.collaboration.getKey()){
		        	String forwardMemberId = affairs.get(0).getForwardMember();
		        	int forwardMemberFlag = 0;
		        	String forwardMember = null;
		        	if(Strings.isNotBlank(forwardMemberId)){
		        		try {
		        			forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
		        			forwardMemberFlag = 1;
		        		}
		        		catch (Exception e) {
		        		}
		        	}
				    userMessageManager.sendSystemMessage(new MessageContent("col.hasten", subject, user.getName(), additional_remark, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel),
				    		ApplicationCategoryEnum.valueOf(hastenAffair.getApp()), user.getId(), receivers, importantLevel);
		        }else if(hastenAffair.getApp() == ApplicationCategoryEnum.info.getKey()){//信息报送//branches_a8_v350_r_gov GOV-1162 魏俊彪 增加催办消息提醒
		        	MessageContent messageContent = new MessageContent("info.hasten", subject, user.getName(), additional_remark,hastenAffair.getApp());
		        	messageContent.setResource("com.seeyon.v3x.info.resources.i18n.InfoResources");
		        	userMessageManager.sendSystemMessage(messageContent,ApplicationCategoryEnum.valueOf(hastenAffair.getApp()), user.getId(), receivers);
		        }	else {//公文
		        	userMessageManager.sendSystemMessage(new MessageContent("edoc.hasten", subject, user.getName(), additional_remark,hastenAffair.getApp()).setImportantLevel(importantLevel),
		        			ApplicationCategoryEnum.valueOf(hastenAffair.getApp()), user.getId(), receivers, importantLevel);
		        }
            }
            catch (Exception e) {
                logger.error("", e);
            }
        }
        Collection<Long> inter = CollectionUtils.intersection(memberIds,canHasMember);
        Collection<Long> result = CollectionUtils.subtract(memberIds,inter);
        return new ArrayList<Long>(result);
    }

    public void saveBody(ColBody body) throws ColException {
        super.getHibernateTemplate().update(body);
        Long summaryId = body.getSummaryId();
        ColSummary summary = get(summaryId);
        
    	ColMessageHelper.saveBodyMessage(affairManager, userMessageManager, orgManager, summary);
    }
    
    private static final String selectAffair = "summary.id," +
		"summary.canArchive," +
		"summary.subject," +
		"summary.importantLevel," +
		"summary.startMemberId," +
		"summary.forwardMember," +
		"summary.createDate," +
		"summary.startDate," +
		"summary.finishDate," +
		"summary.resentTime," +
		"summary.deadline," +
		"summary.bodyType," +
		"summary.identifier," +
		"summary.caseId," +
		"summary.processId," +
		"summary.templeteId," +
		"summary.source," +
		"summary.archiveId," +
		"summary.newflowType," +
		"summary.state," +
		"summary.secretLevel,"+//成发集团项目 程炯 2012-8-31 获得流程密级
		"affair.id," +
		"affair.state," +
		"affair.subState," +
		"affair.isTrack," +
		"affair.hastenTimes," +
		"affair.isOvertopTime," +
		"affair.remindDate," +
		"affair.deadlineDate," +
		"affair.receiveTime," +
		"affair.completeTime," +
		"affair.createDate," +
		"affair.memberId," +
		"affair.transactorId," + 
		"affair.nodePolicy," +
		"affair.identifier";

	private static void make(Object[] object, ColSummary summary, Affair affair){
		int n = 0;
		summary.setId((Long)object[n++]);
		summary.setCanArchive((Boolean)object[n++]);
        String subject = (String)object[n++];
		summary.setImportantLevel((Integer)object[n++]);
		summary.setStartMemberId((Long)object[n++]);
		summary.setForwardMember((String)object[n++]);
		summary.setCreateDate((Timestamp)object[n++]);
		summary.setStartDate((Timestamp)object[n++]);
		summary.setFinishDate((Timestamp)object[n++]);
		summary.setResentTime((Integer)object[n++]);
		summary.setDeadline((Long)object[n++]);
		summary.setBodyType((String)object[n++]);
		summary.setIdentifier((String)object[n++]);
		summary.setCaseId((Long)object[n++]);
		summary.setProcessId((String)object[n++]);
		summary.setTempleteId((Long)object[n++]);
		String source = (String)object[n++];
        if(Strings.isNotBlank(source)){
            subject += "(" + source + ")";
            summary.setSource(source);
        }
        summary.setSubject(subject);
        summary.setArchiveId((Long)object[n++]);
        summary.setNewflowType((Integer)object[n++]);
        summary.setState((Integer)object[n++]);
        summary.setSecretLevel((Integer)object[n++]);//成发集团项目 程炯 2012-8-31 为协同添加流程密级
        
		affair.setId((Long)object[n++]);
		affair.setState((Integer)object[n++]);
		affair.setSubState((Integer)object[n++]);
		affair.setIsTrack((Boolean)object[n++]);
		affair.setHastenTimes((Integer)object[n++]);
		affair.setIsOvertopTime((Boolean)object[n++]);
		affair.setRemindDate((Long)object[n++]);
		affair.setDeadlineDate((Long)object[n++]);
		affair.setReceiveTime((Timestamp)object[n++]);
		affair.setCompleteTime((Timestamp)object[n++]);
		affair.setCreateDate((Timestamp)object[n++]);
		affair.setMemberId((Long)object[n++]);
		affair.setTransactorId((Long)object[n++]);	
		affair.setNodePolicy((String)object[n++]);	
		affair.setIdentifier((String)object[n++]);	
		affair.setSubject(summary.getSubject());
		affair.setForwardMember(summary.getForwardMember());
		affair.setResentTime(summary.getResentTime());
	}
	
	/**
	 * 协同Hql查询模型，包含hql语句，命名参数、占位参数及代理信息<br>
	 * 方便协同、业务配置两个模块中的获取列表代码重用，避免因业务变更两处逻辑不一致<br>
	 * @author <a href="mailto:yangm@seeyon.com">多情僧</a> 2011-8-5
	 */
	class ColHqlResult extends HqlResult {
		private boolean agentFlag;
		private boolean agentToFlag;
		private Map<Long, AgentModel> agentMap;
		
		public boolean isAgentFlag() {
			return agentFlag;
		}
		public void setAgentFlag(boolean agentFlag) {
			this.agentFlag = agentFlag;
		}
		public boolean isAgentToFlag() {
			return agentToFlag;
		}
		public void setAgentToFlag(boolean agentToFlag) {
			this.agentToFlag = agentToFlag;
		}
		public Map<Long, AgentModel> getAgentMap() {
			return agentMap;
		}
		public void setAgentMap(Map<Long, AgentModel> agentMap) {
			this.agentMap = agentMap;
		}
	}
	
	public int getColCount(int state, List<Long> templeteIds) {
		ColHqlResult chr = this.getColHqlResult(null, null, null, state, templeteIds, true);
		return (Integer)super.findUnique(chr.getHql(), chr.getNamedParameter(), chr.getIndexParameter());
	}
	
	/**
	 * 针对不同类型的列表事项，在获取列表或仅需要获取总数时，将获取Hql语句及对应命名、占位参数的逻辑分离出来以便复用
	 * @param condition		查询条件类型
	 * @param field			查询值1
	 * @param field1		查询值1
	 * @param state			事项类型，如待办、已发、已办等
	 * @param templeteIds	事项所对应的表单模板ID（比如业务配置场景）
	 * @param queryCount	是否取总数（反之取列表）
	 */
	private ColHqlResult getColHqlResult(String condition, String field, String field1, int state, List<Long> templeteIds, boolean queryCount) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<Object> indexParameter = new ArrayList<Object>();
		User user = CurrentUser.get();
		long user_id = user.getId();
		
		//获取代理相关信息
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(user_id);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(user_id);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean agentFlag = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			agentModelList = _agentModelList;
			agentFlag = true;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		List<AgentModel> collAgent = new ArrayList<AgentModel>();
		Map<Long,AgentModel> agentMap = null;
		if(agentModelList != null && !agentModelList.isEmpty()){
			java.util.Date now = new java.util.Date();
			if(agentToFlag)
				agentMap = new HashMap<Long,AgentModel>();
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.collaboration.key() && agentModel.getStartDate().before(now) && agentModel.getEndDate().after(now)){
	    				collAgent.add(agentModel);
	    				if(agentToFlag){
	    					List<V3xAgentDetailModel> details = agentModel.getAgentDetail();
	    					if(details != null){
	    						for(V3xAgentDetailModel detail:details){
	    							agentMap.put(detail.getEntityId(), agentModel);
	    						}
	    					}else{
	    						//如果details为空，表示代理全部协同
	    						agentMap.put(-1L, agentModel);
	    					}
	    				}
	    			}
	    		}
	    	}
		}
    	boolean isProxy = false;
		if(collAgent != null && !collAgent.isEmpty()){
			isProxy = true;
		}else{
			agentFlag = false;
			agentToFlag = false;
		}
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		if(queryCount) {
			hql.append(" count(summary.id) ");
		}
		else {
			hql.append(selectAffair);
		}
		
		condition = StringUtils.defaultString(condition);
		if(!"startMemberName".equals(condition)){
			hql.append(" from Affair as affair,ColSummary as summary ");
			hql.append(" where affair.objectId=summary.id and ");
		}
		else{
			hql.append(" from Affair as affair,ColSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
			hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id and ");	
		}
		/** 流程状态：未结束、已结束、已终止
		 *  0 : 未结束
		 *  1 : 已结束 (包括已终止)
		 *  2 : 已终止
		 */
		if ("workflowState".equals(condition)) {
			if ("0".equals(field)) {
				hql.append(" summary.state=? and ");
				indexParameter.add(Constant.flowState.run.ordinal());
			} else if ("1".equals(field)) {
				hql.append(" (summary.state=? or summary.state=?) and ");
				indexParameter.add(Constant.flowState.terminate.ordinal());
				indexParameter.add(Constant.flowState.finish.ordinal());
			} else if ("2".equals(field)) {
				hql.append(" summary.state=? and ");
				indexParameter.add(Constant.flowState.terminate.ordinal());
			}
		}
		if(isProxy){
			List<V3xAgentDetailModel> models = null;
			List<Long> templateIds = null;
			if (!agentToFlag) {
				hql.append("(");
				hql.append(" (affair.memberId=?) ");
				indexParameter.add(user_id);
				if (state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) {
					if(collAgent != null && !collAgent.isEmpty()){
						hql.append("   or (");
						int i = 0;
						for(AgentModel agent : collAgent){
							if(i != 0){
								hql.append(" or ");
							}
							hql.append(" (affair.memberId=?");
							hql.append(" and affair.receiveTime>=?");
							indexParameter.add(agent.getAgentToId());
							indexParameter.add(agent.getStartDate());
							
							models = agent.getAgentDetail();
							if(models != null && !models.isEmpty()){
								hql.append(" and (");
								templateIds = new ArrayList<Long>();
								boolean hasFree = false;
								for(V3xAgentDetailModel model:models){
									if(model.getEntityId()==1){              //代理自由流程
										hql.append("affair.templeteId is null");
										hasFree = true;
									}else if(model.getEntityId()==2){        //代理全部模板
										hql.append("affair.templeteId is not null");
									}else{
										templateIds.add(model.getEntityId());
									}
								}
								if(templateIds.size()>0){
									hql.append(hasFree?" or ":"").append("affair.templeteId in (");
									for(int j = 0;j<templateIds.size();j++){
										if(j != 0)
											hql.append(",");
										hql.append("?");
										indexParameter.add(templateIds.get(j));
									}
									hql.append(")");
								}
								hql.append(")");
							}
							hql.append(")");
							i++;
						}
						hql.append(")");
					}
				}
				hql.append(")");
			}
			else {
				if (state == StateEnum.col_pending.key()) {
					hql.append(" affair.memberId=?");
					indexParameter.add(user_id);
				}else{
					hql.append(" affair.memberId=?");
					indexParameter.add(user_id);
				}
			}
		}else{
			hql.append(" (affair.memberId=?");
			indexParameter.add(user_id);
			hql.append(")");
		}
		
		hql.append(" and affair.state=? and affair.app=1 and affair.isDelete=false ");
        if(state != StateEnum.col_waitSend.key()){//已归档的待发需要抽取出来
            hql.append(" and affair.archiveId is null");
        }
		
		indexParameter.add(state);

        
		// 关于重要程度的过滤，2�
		if (condition.equals("importantfilter")) {
			hql.append(" and ((summary.importantLevel=2) or (summary.importantLevel=3))");
		}
		// 关于超期的过�?jincm修改
		else if (condition.equals("overduefilter")) {
			hql.append(" and affair.isOvertopTime=true and summary.finishDate is null");
		}
		// 关于已经完成的filter
		else if (condition.equals("finishfilter")) {
			hql.append(" and summary.finishDate is not null");
		}
		// 关于未完成的filter
		else if (condition.equals("notfinishfilter")) {
			hql.append(" and summary.finishDate is null");
		}
		else if (condition.equals("subject")) {
			hql.append(" and summary.subject like :subject ");
			parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
		}
		else if (condition.equals("importantLevel")) {
			hql.append(" and summary.importantLevel=?");
			indexParameter.add(Integer.parseInt(field));
		}
		//待发,已发协同按创建日期查询,待办按接收时间查询,已办按处理时间查询
		else if (condition.equals("createDate")) {
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.createDate >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.createDate <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}
		else if(condition.equals("receiveDate")){
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.receiveTime >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.receiveTime <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}
		else if(condition.equals("completeDate")){
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.completeTime >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.completeTime <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}
		//是否是模版协同
		else if(condition.equals("CollType")){
			if("Templete".equals(field)){ //模版协同
				hql.append(" and (summary.templeteId is not null) ");
			}
			else if("Self".equals(field)){ //自由协同
				hql.append(" and (summary.templeteId is null) ");
			}
		}
		else if(condition.equals("startMemberName")){
			hql.append(" and mem.name like :startMemberName");
			parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
		}
		
		//按照重要程度查询时，命名参数在占位符参数之前，会报错，需将其放在之后
		if(templeteIds != null && !templeteIds.isEmpty()){
            hql.append(" and summary.templeteId in(:templeteIds) ");
            parameterMap.put("templeteIds", templeteIds);
        }
		
		if(!queryCount) {
			String orderByColumn = Pagination.getOrderByColumn();	
			String orderByDESC = Pagination.getOrderByDESC();
			if(Strings.isNotBlank(orderByColumn) 
					&& (orderByColumn.equals("createDate") || orderByColumn.equals("receiveTime") || orderByColumn.equals("completeTime"))){
				hql.append(" order by affair." + orderByColumn + " " + orderByDESC);
			}
			else{
				if(state == StateEnum.col_pending.key()){
		            //待办按发起时间排序 × 按照接受时间排序
					hql.append(" order by affair.receiveTime desc");
				}
				else if(state == StateEnum.col_done.key()){
					hql.append(" order by affair.completeTime desc");
				}
				else{
					hql.append(" order by affair.createDate desc");
				}
			}
		}
		
		ColHqlResult ret = new ColHqlResult();
		ret.setHql(hql.toString());
		ret.setNamedParameter(parameterMap);
		ret.setIndexParameter(indexParameter);
		ret.setAgentFlag(agentFlag);
		ret.setAgentToFlag(agentToFlag);
		ret.setAgentMap(agentMap);
		
		return ret;
	}
	
	/**
	 * 重载getColHqlResult
	 * */
	private ColHqlResult getColHqlResult(String condition, String field, String field1, int state, List<Long> templeteIds, boolean queryCount,Integer secretLevle) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<Object> indexParameter = new ArrayList<Object>();
		User user = CurrentUser.get();
		long user_id = user.getId();
		
		//获取代理相关信息
		List<AgentModel> _agentModelList = MemberAgentBean.getInstance().getAgentModelList(user_id);
    	List<AgentModel> _agentModelToList = MemberAgentBean.getInstance().getAgentModelToList(user_id);
		List<AgentModel> agentModelList = null;
		boolean agentToFlag = false;
		boolean agentFlag = false;
		if(_agentModelList != null && !_agentModelList.isEmpty()){
			agentModelList = _agentModelList;
			agentFlag = true;
		}else if(_agentModelToList != null && !_agentModelToList.isEmpty()){
			agentModelList = _agentModelToList;
			agentToFlag = true;
		}
		List<AgentModel> collAgent = new ArrayList<AgentModel>();
		Map<Long,AgentModel> agentMap = null;
		if(agentModelList != null && !agentModelList.isEmpty()){
			java.util.Date now = new java.util.Date();
			if(agentToFlag)
				agentMap = new HashMap<Long,AgentModel>();
	    	for(AgentModel agentModel : agentModelList){
	    		String agentOptionStr = agentModel.getAgentOption();
	    		String[] agentOptions = agentOptionStr.split("&");
	    		for(String agentOption : agentOptions){
	    			int _agentOption = Integer.parseInt(agentOption);
	    			if(_agentOption == ApplicationCategoryEnum.collaboration.key() && agentModel.getStartDate().before(now) && agentModel.getEndDate().after(now)){
	    				collAgent.add(agentModel);
	    				if(agentToFlag){
	    					List<V3xAgentDetailModel> details = agentModel.getAgentDetail();
	    					if(details != null){
	    						for(V3xAgentDetailModel detail:details){
	    							agentMap.put(detail.getEntityId(), agentModel);
	    						}
	    					}else{
	    						//如果details为空，表示代理全部协同
	    						agentMap.put(-1L, agentModel);
	    					}
	    				}
	    			}
	    		}
	    	}
		}
    	boolean isProxy = false;
		if(collAgent != null && !collAgent.isEmpty()){
			isProxy = true;
		}else{
			agentFlag = false;
			agentToFlag = false;
		}
		
		StringBuilder hql = new StringBuilder();
		hql.append("select ");
		if(queryCount) {
			hql.append(" count(summary.id) ");
		}
		else {
			hql.append(selectAffair);
		}
		
		condition = StringUtils.defaultString(condition);
		if(!"startMemberName".equals(condition)){
			hql.append(" from Affair as affair,ColSummary as summary ");
			hql.append(" where affair.objectId=summary.id and ");
		}
		else{
			hql.append(" from Affair as affair,ColSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
			hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id and ");	
		}
		/** 流程状态：未结束、已结束、已终止
		 *  0 : 未结束
		 *  1 : 已结束 (包括已终止)
		 *  2 : 已终止
		 */
		if ("workflowState".equals(condition)) {
			if ("0".equals(field)) {
				hql.append(" summary.state=? and ");
				indexParameter.add(Constant.flowState.run.ordinal());
			} else if ("1".equals(field)) {
				hql.append(" (summary.state=? or summary.state=?) and ");
				indexParameter.add(Constant.flowState.terminate.ordinal());
				indexParameter.add(Constant.flowState.finish.ordinal());
			} else if ("2".equals(field)) {
				hql.append(" summary.state=? and ");
				indexParameter.add(Constant.flowState.terminate.ordinal());
			}
		}
		if(isProxy){
			List<V3xAgentDetailModel> models = null;
			List<Long> templateIds = null;
			if (!agentToFlag) {
				hql.append("(");
				hql.append(" (affair.memberId=?) ");
				indexParameter.add(user_id);
				if (state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) {
					if(collAgent != null && !collAgent.isEmpty()){
						hql.append("   or (");
						int i = 0;
						for(AgentModel agent : collAgent){
							if(i != 0){
								hql.append(" or ");
							}
							hql.append(" (affair.memberId=?");
							hql.append(" and affair.receiveTime>=?");
							indexParameter.add(agent.getAgentToId());
							indexParameter.add(agent.getStartDate());
							
							models = agent.getAgentDetail();
							if(models != null && !models.isEmpty()){
								hql.append(" and (");
								templateIds = new ArrayList<Long>();
								boolean hasFree = false;
								for(V3xAgentDetailModel model:models){
									if(model.getEntityId()==1){              //代理自由流程
										hql.append("affair.templeteId is null");
										hasFree = true;
									}else if(model.getEntityId()==2){        //代理全部模板
										hql.append("affair.templeteId is not null");
									}else{
										templateIds.add(model.getEntityId());
									}
								}
								if(templateIds.size()>0){
									hql.append(hasFree?" or ":"").append("affair.templeteId in (");
									for(int j = 0;j<templateIds.size();j++){
										if(j != 0)
											hql.append(",");
										hql.append("?");
										indexParameter.add(templateIds.get(j));
									}
									hql.append(")");
								}
								hql.append(")");
							}
							hql.append(")");
							i++;
						}
						hql.append(")");
					}
				}
				hql.append(")");
			}
			else {
				if (state == StateEnum.col_pending.key()) {
					hql.append(" affair.memberId=?");
					indexParameter.add(user_id);
				}else{
					hql.append(" affair.memberId=?");
					indexParameter.add(user_id);
				}
			}
		}else{
			hql.append(" (affair.memberId=?");
			indexParameter.add(user_id);
			hql.append(")");
		}
		
		hql.append(" and affair.state=? and affair.app=1 and affair.isDelete=false ");
        if(state != StateEnum.col_waitSend.key()){//已归档的待发需要抽取出来
            hql.append(" and affair.archiveId is null");
        }
		
		indexParameter.add(state);

        
		// 关于重要程度的过滤，2�
		if (condition.equals("importantfilter")) {
			hql.append(" and ((summary.importantLevel=2) or (summary.importantLevel=3))");
		}
		// 关于超期的过�?jincm修改
		else if (condition.equals("overduefilter")) {
			hql.append(" and affair.isOvertopTime=true and summary.finishDate is null");
		}
		// 关于已经完成的filter
		else if (condition.equals("finishfilter")) {
			hql.append(" and summary.finishDate is not null");
		}
		// 关于未完成的filter
		else if (condition.equals("notfinishfilter")) {
			hql.append(" and summary.finishDate is null");
		}
		else if (condition.equals("subject")) {
			hql.append(" and summary.subject like :subject ");
			parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
		}
		else if (condition.equals("importantLevel")) {
			hql.append(" and summary.importantLevel=?");
			indexParameter.add(Integer.parseInt(field));
		}
		//待发,已发协同按创建日期查询,待办按接收时间查询,已办按处理时间查询
		else if (condition.equals("createDate")) {
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.createDate >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.createDate <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}
		else if(condition.equals("receiveDate")){
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.receiveTime >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.receiveTime <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}
		else if(condition.equals("completeDate")){
			if (StringUtils.isNotBlank(field)) {
				java.util.Date stamp = Datetimes.getTodayFirstTime(field);
				hql.append(" and affair.completeTime >= :timestamp1");
				parameterMap.put("timestamp1", stamp);
			}

			if (StringUtils.isNotBlank(field1)) {
				java.util.Date stamp = Datetimes.getTodayLastTime(field1);
				hql.append(" and affair.completeTime <= :timestamp2");
				parameterMap.put("timestamp2", stamp);
			}
		}
		//是否是模版协同
		else if(condition.equals("CollType")){
			if("Templete".equals(field)){ //模版协同
				hql.append(" and (summary.templeteId is not null) ");
			}
			else if("Self".equals(field)){ //自由协同
				hql.append(" and (summary.templeteId is null) ");
			}
		}
		else if(condition.equals("startMemberName")){
			hql.append(" and mem.name like :startMemberName");
			parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
		}
		
		//按照重要程度查询时，命名参数在占位符参数之前，会报错，需将其放在之后
		if(templeteIds != null && !templeteIds.isEmpty()){
            hql.append(" and summary.templeteId in(:templeteIds) ");
            parameterMap.put("templeteIds", templeteIds);
        }
		//成发集团项目 程炯 2012-8-31 根据人员密级来筛选协同 begin
		if(secretLevle != null){
			hql.append(" and (summary.secretLevel <= :secretLevel or summary.secretLevel is null)");
			parameterMap.put("secretLevel", secretLevle);
		}
		//end
		
		if(!queryCount) {
			String orderByColumn = Pagination.getOrderByColumn();	
			String orderByDESC = Pagination.getOrderByDESC();
			if(Strings.isNotBlank(orderByColumn) 
					&& (orderByColumn.equals("createDate") || orderByColumn.equals("receiveTime") || orderByColumn.equals("completeTime"))){
				hql.append(" order by affair." + orderByColumn + " " + orderByDESC);
			}
			else{
				if(state == StateEnum.col_pending.key()){
		            //待办按发起时间排序 × 按照接受时间排序
					hql.append(" order by affair.receiveTime desc");
				}
				else if(state == StateEnum.col_done.key()){
					hql.append(" order by affair.completeTime desc");
				}
				else{
					hql.append(" order by affair.createDate desc");
				}
			}
		}
		
		ColHqlResult ret = new ColHqlResult();
		ret.setHql(hql.toString());
		ret.setNamedParameter(parameterMap);
		ret.setIndexParameter(indexParameter);
		ret.setAgentFlag(agentFlag);
		ret.setAgentToFlag(agentToFlag);
		ret.setAgentMap(agentMap);
		
		return ret;
	}
	

    /**
     * TODO:将filter与表现层联合起来
     * <p/>
     * 根据传入的查询参数，查询
     * 根据affair的app参数来区分是否协�?
     * 如果是待�?已发 则sub_app为空，如果是待办/已办则sub_app为workitem
     * 区分待发/已发 state�?Affair.STATE_SUMMARY_DRAFT  Affair.STATE_SUMMARY_SENT
     * 区分待办/已办 state�?Affair.STATE_WORKITEM_PENDING Affair.STATE_WORKITEM_FINISH
     */
    public List<ColSummaryModel> queryByCondition(String condition,
			String field, String field1, int state, List<Long> templeteIds) {
    	ColHqlResult chr = this.getColHqlResult(condition, field, field1, state, templeteIds, false);
		List result = super.find(chr.getHql(), chr.getNamedParameter(), chr.getIndexParameter());

		Map<Long, AgentModel> agentMap = chr.getAgentMap();
		boolean agentFlag = chr.isAgentFlag();
		boolean agentToFlag = chr.isAgentToFlag();
		long user_id = CurrentUser.get().getId();
		
		java.util.Date early = null;
		List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
		for (int i = 0; i < result.size(); i++) {
			Object[] object = (Object[]) result.get(i);
			
			ColSummary summary = new ColSummary();
			Affair affair = new Affair();
			
			make(object, summary, affair);
			
			long summaryId = summary.getId();
			
			// 开始组装最后返回的结果�?
			ColSummaryModel model = new ColSummaryModel();
			if (state == StateEnum.col_waitSend.key()) {
				model.setStartDate(summary.getCreateDate());
			}
			else if (state == StateEnum.col_sent.key()) {
				model.setCaseId(summary.getCaseId() + "");
				model.setProcessId(summary.getProcessId());
				model.setStartDate(summary.getCreateDate());
				
				java.sql.Timestamp startDate = summary.getStartDate();
				java.sql.Timestamp finishDate = summary.getFinishDate();
				Date now = new Date(System.currentTimeMillis());
				if(summary.getDeadline() != null && summary.getDeadline() != 0){
					Long deadline = summary.getDeadline()*60000;
					if(finishDate == null){
						if((now.getTime()-startDate.getTime()) > deadline){
							summary.setWorklfowTimeout(true);
						}
					}else{
						Long expendTime = summary.getFinishDate().getTime() - summary.getStartDate().getTime();
						if((deadline-expendTime) < 0){
							summary.setWorklfowTimeout(true);
						}
					}
				}
			}
			else if (state == StateEnum.col_done.key()) {
				model.setWorkitemId(String.valueOf(summaryId));
				model.setProcessId(summary.getProcessId());
				model.setCaseId(summary.getCaseId() + "");
			}
			else if (state == StateEnum.col_pending.key()) {
				model.setWorkitemId(String.valueOf(summaryId));
				model.setProcessId(summary.getProcessId());
				model.setCaseId(summary.getCaseId() + "");
                model.setReceiveTime(affair.getReceiveTime());
			}
			else {
				model.setWorkitemId(String.valueOf(summaryId));
				model.setProcessId(summary.getProcessId());
				model.setCaseId(summary.getCaseId() + "");
			}
			
			model.setSummary(summary);
			model.setAffairId(affair.getId());
			
			int affairState = affair.getState();
			switch (StateEnum.valueOf(affairState)) {
			case col_waitSend:
				model.setColType(ColSummaryModel.COLTYPE.WaitSend.name());
				break;
			case col_sent:
				model.setColType(ColSummaryModel.COLTYPE.Sent.name());
				break;
			case col_done:
				model.setColType(ColSummaryModel.COLTYPE.Done.name());
				if(affair.getCompleteTime() != null){
					model.setDealTime(new Date(affair.getCompleteTime().getTime()));
				}
				break;
			case col_pending:
				model.setColType(ColSummaryModel.COLTYPE.Pending.name());
				break;
			}

			model.setBodyType(summary.getBodyType());

			// 协同状�?
			Integer sub_state = affair.getSubState();
			if (sub_state != null) {
				model.setState(sub_state.intValue());
			}

			// 是否跟踪
			Boolean isTrack = affair.getIsTrack();
			if (isTrack != null) {
				model.setIsTrack(isTrack.booleanValue());
			}

			// 催办次数
			Integer hastenTimes = affair.getHastenTimes();
			if (hastenTimes != null) {
				model.setHastenTimes(hastenTimes);
			}

			// 是否超期
			Boolean overtopTime = affair.getIsOvertopTime();
			if (overtopTime != null) {
				model.setOvertopTime(overtopTime.booleanValue());
			}

			//提前提醒
			Long advanceRemind = affair.getRemindDate();
			if (advanceRemind != null) {
				model.setAdvanceRemindTime(advanceRemind);
			}

			//协同处理期限
			Long deadLine = affair.getDeadlineDate();
			if (deadLine != null) {
				model.setDeadLine(deadLine);
			}
			
			//是否有附件
			model.setHasAttsFlag(summary.isHasAttachments());

			//取得转发人姓名
			model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));

			V3xOrgMember member = null;
			//是否被代理
			if (state == StateEnum.col_done.key() || state == StateEnum.col_sent.key()) {
				model.setAffair(affair);
			    /*if(affair.getTransactorId() != null){
			    	Long memberId = affair.getTransactorId();
			    	if(memberId==user.getId())
			    		memberId = affair.getMemberId();
					try {
                            member = orgManager.getMemberById(memberId);
                            if(member != null){
                            	model.setProxyName(member.getName());
                            	model.setProxy(true);
                            }
					} catch (Exception e) {
						log.error("", e);
					}
			    }else{
			    	if(affair.getMemberId() != user.getId()){
			    		try{
			    			member = orgManager.getMemberById(affair.getMemberId());
			    			model.setProxyName(member.getName());
			    		}catch(BusinessException e){
			    			log.error("", e);
			    		}
			    		model.setAgentDeal(true);
			    		model.setProxy(true);
			    	}
			    }*/
			}
			if ((state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) && agentFlag && affair.getMemberId() != user_id) {
				Long proxyMemberId = affair.getMemberId();
				try {
					member = orgManager.getMemberById(proxyMemberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				model.setProxyName(member.getName());
				model.setProxy(true);
			}else if((state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) && agentToFlag && agentMap != null){
				if(agentMap.get(-1L) != null){
					early = agentMap.get(-1L).getStartDate();
				}else if(summary.getTempleteId()==null && agentMap.get(1L) != null){
					early = agentMap.get(1L).getStartDate();
				}else if(summary.getTempleteId() != null && agentMap.get(2L) != null){
					early = agentMap.get(2L).getStartDate();
				}else if(summary.getTempleteId() != null && agentMap.get(summary.getTempleteId()) != null){
					early = agentMap.get(summary.getTempleteId()).getStartDate();
				}
				if(early != null && early.before(affair.getReceiveTime()))
					model.setProxy(true);
				early = null;
			}
			model.setNodePolicy(affair.getNodePolicy());
			
			models.add(model);
		}
		
		return models;
	}
    
    /**
     * 重载queryByCondition
     * */
    public List<ColSummaryModel> queryByCondition(String condition,
			String field, String field1, int state, List<Long> templeteIds,Integer secretLevel) {
    	ColHqlResult chr = this.getColHqlResult(condition, field, field1, state, templeteIds, false,secretLevel);
		List result = super.find(chr.getHql(), chr.getNamedParameter(), chr.getIndexParameter());

		Map<Long, AgentModel> agentMap = chr.getAgentMap();
		boolean agentFlag = chr.isAgentFlag();
		boolean agentToFlag = chr.isAgentToFlag();
		long user_id = CurrentUser.get().getId();
		
		java.util.Date early = null;
		List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
		for (int i = 0; i < result.size(); i++) {
			Object[] object = (Object[]) result.get(i);
			
			ColSummary summary = new ColSummary();
			Affair affair = new Affair();
			
			make(object, summary, affair);
			
			long summaryId = summary.getId();
			
			// 开始组装最后返回的结果�?
			ColSummaryModel model = new ColSummaryModel();
			if (state == StateEnum.col_waitSend.key()) {
				model.setStartDate(summary.getCreateDate());
			}
			else if (state == StateEnum.col_sent.key()) {
				model.setCaseId(summary.getCaseId() + "");
				model.setProcessId(summary.getProcessId());
				model.setStartDate(summary.getCreateDate());
				
				java.sql.Timestamp startDate = summary.getStartDate();
				java.sql.Timestamp finishDate = summary.getFinishDate();
				Date now = new Date(System.currentTimeMillis());
				if(summary.getDeadline() != null && summary.getDeadline() != 0){
					Long deadline = summary.getDeadline()*60000;
					if(finishDate == null){
						if((now.getTime()-startDate.getTime()) > deadline){
							summary.setWorklfowTimeout(true);
						}
					}else{
						Long expendTime = summary.getFinishDate().getTime() - summary.getStartDate().getTime();
						if((deadline-expendTime) < 0){
							summary.setWorklfowTimeout(true);
						}
					}
				}
			}
			else if (state == StateEnum.col_done.key()) {
				model.setWorkitemId(String.valueOf(summaryId));
				model.setProcessId(summary.getProcessId());
				model.setCaseId(summary.getCaseId() + "");
			}
			else if (state == StateEnum.col_pending.key()) {
				model.setWorkitemId(String.valueOf(summaryId));
				model.setProcessId(summary.getProcessId());
				model.setCaseId(summary.getCaseId() + "");
                model.setReceiveTime(affair.getReceiveTime());
			}
			else {
				model.setWorkitemId(String.valueOf(summaryId));
				model.setProcessId(summary.getProcessId());
				model.setCaseId(summary.getCaseId() + "");
			}
			
			model.setSummary(summary);
			model.setAffairId(affair.getId());
			
			int affairState = affair.getState();
			switch (StateEnum.valueOf(affairState)) {
			case col_waitSend:
				model.setColType(ColSummaryModel.COLTYPE.WaitSend.name());
				break;
			case col_sent:
				model.setColType(ColSummaryModel.COLTYPE.Sent.name());
				break;
			case col_done:
				model.setColType(ColSummaryModel.COLTYPE.Done.name());
				if(affair.getCompleteTime() != null){
					model.setDealTime(new Date(affair.getCompleteTime().getTime()));
				}
				break;
			case col_pending:
				model.setColType(ColSummaryModel.COLTYPE.Pending.name());
				break;
			}

			model.setBodyType(summary.getBodyType());

			// 协同状�?
			Integer sub_state = affair.getSubState();
			if (sub_state != null) {
				model.setState(sub_state.intValue());
			}

			// 是否跟踪
			Boolean isTrack = affair.getIsTrack();
			if (isTrack != null) {
				model.setIsTrack(isTrack.booleanValue());
			}

			// 催办次数
			Integer hastenTimes = affair.getHastenTimes();
			if (hastenTimes != null) {
				model.setHastenTimes(hastenTimes);
			}

			// 是否超期
			Boolean overtopTime = affair.getIsOvertopTime();
			if (overtopTime != null) {
				model.setOvertopTime(overtopTime.booleanValue());
			}

			//提前提醒
			Long advanceRemind = affair.getRemindDate();
			if (advanceRemind != null) {
				model.setAdvanceRemindTime(advanceRemind);
			}

			//协同处理期限
			Long deadLine = affair.getDeadlineDate();
			if (deadLine != null) {
				model.setDeadLine(deadLine);
			}
			
			//是否有附件
			model.setHasAttsFlag(summary.isHasAttachments());

			//取得转发人姓名
			model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));

			V3xOrgMember member = null;
			//是否被代理
			if (state == StateEnum.col_done.key() || state == StateEnum.col_sent.key()) {
				model.setAffair(affair);
			    /*if(affair.getTransactorId() != null){
			    	Long memberId = affair.getTransactorId();
			    	if(memberId==user.getId())
			    		memberId = affair.getMemberId();
					try {
                            member = orgManager.getMemberById(memberId);
                            if(member != null){
                            	model.setProxyName(member.getName());
                            	model.setProxy(true);
                            }
					} catch (Exception e) {
						log.error("", e);
					}
			    }else{
			    	if(affair.getMemberId() != user.getId()){
			    		try{
			    			member = orgManager.getMemberById(affair.getMemberId());
			    			model.setProxyName(member.getName());
			    		}catch(BusinessException e){
			    			log.error("", e);
			    		}
			    		model.setAgentDeal(true);
			    		model.setProxy(true);
			    	}
			    }*/
			}
			if ((state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) && agentFlag && affair.getMemberId() != user_id) {
				Long proxyMemberId = affair.getMemberId();
				try {
					member = orgManager.getMemberById(proxyMemberId);
				} catch (BusinessException e) {
					log.error("", e);
				}
				model.setProxyName(member.getName());
				model.setProxy(true);
			}else if((state == StateEnum.col_pending.key() || state == StateEnum.col_done.key()) && agentToFlag && agentMap != null){
				if(agentMap.get(-1L) != null){
					early = agentMap.get(-1L).getStartDate();
				}else if(summary.getTempleteId()==null && agentMap.get(1L) != null){
					early = agentMap.get(1L).getStartDate();
				}else if(summary.getTempleteId() != null && agentMap.get(2L) != null){
					early = agentMap.get(2L).getStartDate();
				}else if(summary.getTempleteId() != null && agentMap.get(summary.getTempleteId()) != null){
					early = agentMap.get(summary.getTempleteId()).getStartDate();
				}
				if(early != null && early.before(affair.getReceiveTime()))
					model.setProxy(true);
				early = null;
			}
			model.setNodePolicy(affair.getNodePolicy());
			
			models.add(model);
		}
		
		return models;
	}
    
    public List<ColSummaryModel> queryByCondition4Quote(StateEnum state, String condition, String field, String field1) {
        long user_id = CurrentUser.get().getId();

        List<Object> objects = new ArrayList<Object>();

        StringBuffer hql = new StringBuffer();
        hql.append("select ").append(selectAffair);
        
        if(!"startMemberName".equals(condition)){
	        hql.append(" from ColSummary as summary,Affair as affair");
	        hql.append(" where (affair.objectId=summary.id) and (affair.memberId=?)");
        }
        else{
        	hql.append(" from ColSummary as summary,Affair as affair,"+V3xOrgMember.class.getName()+" as mem");
        	hql.append(" where (affair.senderId=mem.id) and (affair.objectId=summary.id) and (affair.memberId=?)");
        }
        
		hql.append(" and (affair.state=?)");
		hql.append(" and (affair.app=?)");
		hql.append(" and affair.isDelete=false");
		hql.append(" and affair.archiveId is null");
		// 添加涉密等级过滤
		V3xOrgMember member = null;
        try {
            member = orgManager.getMemberById(user_id);
            hql.append(" and summary.secretLevel <=" + member.getSecretLevel());
        } catch(BusinessException e) {
            e.printStackTrace();
        }
		//发起者允许关联已发的协同，即使关联的协同没有设置“允许转发”
		if(state.key() != StateEnum.col_sent.key())
		hql.append(" and (summary.canForward=true)");
		
		objects.add(user_id);
		objects.add(state.key());
        objects.add(ApplicationCategoryEnum.collaboration.key());

        if (condition != null) {
        	if (condition.equals("subject")) {
        		 hql.append(" and (summary.subject like ?)");

                objects.add("%" + field + "%");
            }
        	else if (condition.equals("startMemberName")) {
				hql.append(" and (mem.name like ?)");

                objects.add("%" + SQLWildcardUtil.escape(field) + "%");
            }
            else if (condition.equals("importantLevel")) {
            	hql.append(" and (summary.importantLevel=?)");

                objects.add(Integer.parseInt(field));
            }
            else if (condition.equals("createDate")) {
                if (StringUtils.isNotBlank(field)) {
                	hql.append(" and affair.createDate >= ?");
                    java.util.Date stamp = Datetimes.getTodayFirstTime(field);

                    objects.add(stamp);
                }
                if (StringUtils.isNotBlank(field1)) {
                	hql.append(" and affair.createDate <= ?");
                    java.util.Date stamp = Datetimes.getTodayLastTime(field1);

                    objects.add(stamp);
                }
            }
        }

		if(state.ordinal() == StateEnum.col_pending.key()){
			hql.append(" order by affair.receiveTime desc");
		}
		else if(state.ordinal() == StateEnum.col_done.key()){
			hql.append(" order by affair.completeTime desc");
		}
		else{
			hql.append(" order by affair.createDate desc");
		}
		
		return this.doQuery(hql.toString(), null, objects);
    }
    
    @SuppressWarnings("unchecked")
	public List<Long> queryByCondition4Store(Date beginDate, Date endDate, String dataScorp, Integer[] flowState){
        Map<String, Object> named = new HashMap<String, Object>();

        StringBuffer hql = new StringBuffer();
        hql.append("select s.id from ColSummary s where s.startDate>=:beginDate and s.startDate<=:endDate");
        named.put("beginDate", beginDate);
        named.put("endDate", endDate);

        if(flowState != null){
        	hql.append(" and (s.state in (:flowState))");
        	named.put("flowState", flowState);
        }
        
		if(dataScorp.contains("0") && dataScorp.contains("1")){
			//ignore
		}
		else if(dataScorp.contains("0")){
			hql.append(" and (s.templeteId is null)");
		}
		else if(dataScorp.contains("1")){
			hql.append(" and (s.templeteId is not null)");
		}
		
		hql.append(" and (s.caseId is not null)");
        
    	return super.find(hql.toString(), 0, 300, named);
    }
    
    private List<ColSummaryModel> doQuery(String hql, Map<String, Object> named, List<Object> objects){
    	List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
        List result = super.find(hql.toString(), named, objects);

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                Object[] object = (Object[]) result.get(i);
    			
    			ColSummary summary = new ColSummary();
    			Affair affair = new Affair();
    			
    			make(object, summary, affair);

                //开始组装最后返回的结果�?
                ColSummaryModel model = new ColSummaryModel();

                model.setStartDate(new Date(summary.getCreateDate().getTime()));
                model.setWorkitemId(affair.getObjectId() + "");
                model.setCaseId(summary.getCaseId() + "");
                model.setSummary(summary);
                model.setAffairId(affair.getId());
                model.setBodyType((String) summary.getBodyType());

                //协同状�?
                Integer sub_state = affair.getSubState();
                if (sub_state != null) {
                    model.setState(sub_state.intValue());
                }

                //是否跟踪
                Boolean isTrack = affair.getIsTrack();
                if (isTrack != null) {
                    model.setIsTrack(isTrack.booleanValue());
                }
                
                //取得转发人姓名
                model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
                model.setNodePolicy(affair.getNodePolicy());
                models.add(model);
            }
        }

        return models;
    }
    
    public List<ColSummaryModel> queryByCondition4QuoteForm(StateEnum state, String condition,
            String field, String field1,String formappid,String quoteformtemId) {
			List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
			long user_id = CurrentUser.get().getId();
			
			List<Object> objects = new ArrayList<Object>();
			
			StringBuffer hql = new StringBuffer();
			hql.append("select ").append(selectAffair);
			
			if(!"startMemberName".equals(condition)){
			hql.append(" from ColSummary as summary,Affair as affair");
			hql.append(" where (affair.objectId=summary.id) and (affair.memberId=?)");
			}
			else{
			hql.append(" from ColSummary as summary,Affair as affair,"+V3xOrgMember.class.getName()+" as mem");
			hql.append(" where (affair.senderId=mem.id) and (affair.objectId=summary.id) and (affair.memberId=?)");
			}
			hql.append(" and (summary.formAppId=?)");
			if(!"".equals(quoteformtemId) && !"null".equals(quoteformtemId) && quoteformtemId !=null)
			    hql.append(" and (summary.templeteId=?)");
			
			hql.append(" and (affair.state=?)");
			hql.append(" and (affair.app=?)");
			hql.append(" and affair.isDelete=false");
			hql.append(" and affair.archiveId is null");
			hql.append(" and (summary.canForward=true)");
		     // 添加涉密等级过滤
	        V3xOrgMember member = null;
	        try {
	            member = orgManager.getMemberById(user_id);
	            hql.append(" and summary.secretLevel <=" + member.getSecretLevel());
	        } catch(BusinessException e) {
	            e.printStackTrace();
	        }
			objects.add(user_id);
			objects.add(Long.parseLong(formappid));
			if(!"".equals(quoteformtemId) && !"null".equals(quoteformtemId) && quoteformtemId !=null)
			   objects.add(Long.parseLong(quoteformtemId));
			objects.add(state.key());
			objects.add(ApplicationCategoryEnum.collaboration.key());
			
			if (condition != null) {
			if (condition.equals("subject")) {
			hql.append(" and (summary.subject like ?)");
			
			objects.add("%" + field + "%");
			}
			else if (condition.equals("startMemberName")) {
			hql.append(" and (mem.name like ?)");
			
			objects.add("%" + SQLWildcardUtil.escape(field) + "%");
			}
			else if (condition.equals("importantLevel")) {
			hql.append(" and (summary.importantLevel=?)");
			
			objects.add(Integer.parseInt(field));
			}
			else if (condition.equals("createDate")) {
			if (StringUtils.isNotBlank(field)) {
			hql.append(" and affair.createDate >= ?");
			java.util.Date stamp = Datetimes.getTodayFirstTime(field);
			
			objects.add(stamp);
			}
			if (StringUtils.isNotBlank(field1)) {
			hql.append(" and affair.createDate <= ?");
			java.util.Date stamp = Datetimes.getTodayLastTime(field1);
			
			objects.add(stamp);
			}
			}
			}
			
			if(state.ordinal() == StateEnum.col_pending.key()){
			hql.append(" order by affair.receiveTime desc");
			}
			else if(state.ordinal() == StateEnum.col_done.key()){
			hql.append(" order by affair.completeTime desc");
			}
			else{
			hql.append(" order by affair.createDate desc");
			}
			
			return this.doQuery(hql.toString(), null, objects);
	}
    
    /**
     * 流程管理查询
     * @modify by lilong on 2012-01-06
     */
    public List<WorkflowData> queryWorkflowDataByCondition(String subject, String beginDate, String endDate, List<String> objectStrs, 
    		int flowstate, int appKey, String operationType, String[] operationTypeIds, boolean paginationFlag) {
        List<WorkflowData> models = new ArrayList<WorkflowData>();
        List<Object> objects = new ArrayList<Object>();

        String hql = " from ColSummary as summary where";
        String sqlStr1 = ""; 
        if(objectStrs != null && objectStrs.size() != 0){
        	hql += " (";
        	boolean needCutOr = false;//用于标志当集团管理员选择多单位查询时的sql拼写
	        for(int i = 0; i < objectStrs.size(); i++){
	        	String objectStr = objectStrs.get(i);
	        	String[] objectArr = objectStr.split("[|]");//按照竖线分割，排除正则表达式的影响用[]转义
	        	
	        	String propName = null;
	        	if(objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)){
	        		propName = "startMemberId";
	        	}
	        	else if(objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){
	        		//第一个循环就是部门的时候需要Cut.多余的OR
	        		if(i == 0) needCutOr = true;
	        		/** 不包含子部门 */
	        		if(objectArr.length > 2 
	        				&& objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT) 
	        				&& 1 == Long.valueOf(objectArr[2])) {//不包含子部门
	        			sqlStr1 += " or summary.orgDepartmentId" + "= ? ";
	        			objects.add(new Long(objectArr[1]));
	        			continue;
	        		}
	        		/** 包含子部门 */
	        		
	        		sqlStr1 += " or summary.orgDepartmentId" + "= ? or ";
    				objects.add(new Long(objectArr[1]));
					List<V3xOrgDepartment> v3xOrgDepartmentList;
					try {
						v3xOrgDepartmentList = this.orgManager.getChildDepartments(Long.valueOf(objectArr[1]), false);
						for(V3xOrgDepartment orgDepartment : v3xOrgDepartmentList) {
							sqlStr1 += " summary.orgDepartmentId" + "= ? or";
							objects.add(orgDepartment.getId());
						}
						sqlStr1 = sqlStr1.substring(0, sqlStr1.lastIndexOf("or")-1);//去除尾部多余or字符串
						continue;
					}catch (BusinessException e) {
						log.error("流程管理获取子部门ID异常ColManagerImpl.queryWorkflowDataByCondition" + e.getLocalizedMessage());
						break;
					}
	        	}
	        	else if(objectArr[0].equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)){
	        		propName = "orgAccountId";
	        	}
	        	else{
	        		continue;
	        	}
	        	if(i > 0){
	        		sqlStr1 += " or";
	        	}
	        	sqlStr1 += " summary." + propName + "=?";
	        	objects.add(new Long(objectArr[1]));
	        }
	        if(needCutOr) {
	        	sqlStr1 = sqlStr1.substring(sqlStr1.indexOf("or") + 2, sqlStr1.length());//去除当查询为某些部门时组装SQL头部多余or字符串
	        }
	        sqlStr1 += ")";
        }
        else if(CurrentUser.get().isAdministrator()){
        	sqlStr1 = " summary.orgAccountId=?";
        	objects.add(CurrentUser.get().getLoginAccount());
        }
        
        
        if(appKey == ApplicationCategoryEnum.collaboration.key()){
        	if(!"".equals(sqlStr1)){
    			sqlStr1 += " and summary.bodyType<> 'FORM'";
    		}else{
    			sqlStr1 = " summary.bodyType<> 'FORM'";
    		}
        }else{
        	if(!"".equals(sqlStr1)){
    			sqlStr1 += " and summary.bodyType='FORM'";
    		}else{
    			sqlStr1 = " summary.bodyType='FORM'";
    		}
        }
        if("template".equals(operationType)){
        	if(!"".equals(operationTypeIds[0])){
	    		for(int i=0; i<operationTypeIds.length; i++){
	        		Long templeteId = Long.parseLong(operationTypeIds[i]);
	        		if(!"".equals(sqlStr1)){
			        	if(operationTypeIds.length == 1){
		        			sqlStr1 += " and (summary.templeteId=?)";
		        			objects.add(templeteId);
			        	}else{
				        	if(i == 0){
			        			sqlStr1 += " and (summary.templeteId=? or";
			        			objects.add(templeteId);
				        	}else if(i == (operationTypeIds.length-1)){
				        		sqlStr1 += " summary.templeteId=?)";
				        		objects.add(templeteId);
				        	}else{
				        		sqlStr1 += " summary.templeteId=? or";
				        		objects.add(templeteId);
				        	}
			        	}
	        		}else{
	        			if(operationTypeIds.length == 1){
			        		sqlStr1 = " (summary.templeteId=?)";
			        		objects.add(templeteId);
			        	}else{
				        	if(i == 0){
				        		sqlStr1 = " (summary.templeteId=? or";
				        		objects.add(templeteId);
				        	}else if(i == (operationTypeIds.length-1)){
				        		sqlStr1 += " summary.templeteId=?)";
				        		objects.add(templeteId);
				        	}else{
				        		sqlStr1 += " summary.templeteId=? or";
				        		objects.add(templeteId);
				        	}
			        	}
	        		}
	    		}
        	}else{
    			if(!"".equals(sqlStr1)){
            		sqlStr1 += " and summary.templeteId is not null";
            	}else{
            		sqlStr1 += " summary.templeteId is not null";
            	}
	    	}
        }else if("self".equals(operationType)){
        	if(!"".equals(sqlStr1)){
        		sqlStr1 += " and summary.templeteId is null";
        	}else{
        		sqlStr1 += " summary.templeteId is null";
        	}
        }
        
    	if (!"".equals(subject)) {
    		if(!"".equals(sqlStr1)){
    			sqlStr1 += " and (summary.subject like ?)";
    		}else{
    			sqlStr1 = " (summary.subject like ?)";
    		}
            objects.add("%" + SQLWildcardUtil.escape(subject) + "%");
        }
        if (!"".equals(beginDate)) {
        	if(!"".equals(sqlStr1)){
        		sqlStr1 += " and summary.startDate >= ?";
        	}else{
        		sqlStr1 = " summary.startDate >= ?";
        	}
            java.util.Date stamp = Datetimes.getTodayFirstTime(beginDate);

            objects.add(stamp);
        } 
        if (!"".equals(endDate)){
        	if(!"".equals(sqlStr1)){
        		sqlStr1 += " and summary.startDate <= ?";
        	}else{
        		sqlStr1 = " summary.startDate <= ?";
        	}
            java.util.Date stamp = Datetimes.getTodayLastTime(endDate);

            objects.add(stamp);
        }
        

		if(!"".equals(sqlStr1)){
			sqlStr1 += " and summary.state=?";
		}else{
			sqlStr1 = " summary.state=?";
		}
		objects.add(flowstate);
    	
    	if(!"".equals(sqlStr1)){
			sqlStr1 += " and summary.caseId is not null";
		}else{
			sqlStr1 = " summary.caseId is not null";
		}
    	
    	StringBuffer selectBf = new StringBuffer();
        selectBf.append(" summary.id,")
        .append("summary.subject,")
        .append("summary.startMemberId,")
        //.append("summary.createDate,")
        .append("summary.startDate,")
        //.append("summary.finishDate,")
        .append("summary.processId,")
        .append("summary.caseId,")
        .append("summary.bodyType,")
		.append("summary.deadline,")
        .append("summary.advanceRemind,")
        //.append("summary.orgAccountId,")
        .append("summary.newflowType,")
        .append("summary.resentTime,")
        .append("summary.forwardMember,").append("summary.templeteId");
    	

        String selectHql = "select" + selectBf.toString() + hql + sqlStr1 +" order by summary.createDate desc";
        List result = null;
        if(paginationFlag){
        	result = super.find(selectHql, null, objects);
        }else{
        	result = super.find(selectHql, -1, -1, null, objects);
        }

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                Object[] object = (Object[]) result.get(i);
                //开始组装最后返回的结果
                WorkflowData model = new WorkflowData();

    			int n = 0;
                model.setSummaryId((Long)object[n++] + "");
                model.setSubject((String)object[n++]);
                Long startMemberId = (Long)object[n++];
                //Timestamp createDate = (Timestamp)object[n++];
                model.setSendTime((Timestamp)object[n++]);
                //Timestamp finishDate = (Timestamp)object[n++];
                model.setProcessId((String)object[n++]);
                model.setCaseId((Long)object[n++]);
                String bodyType = (String)object[n++];
                model.setDeadLine((Long)object[n++]);
                model.setAdvanceRemind((Long)object[n++]);
                //Long accountId = (Long)object[n++];
                model.setNewflowType((Integer)object[n++]);  
                model.setResentTime((Integer)object[n++]);  
                model.setForwardMember((String)object[n++]);  
                Long templeteId = (Long)object[n++];
                if(templeteId!= null){
                	model.setIsFromTemplete(true) ;
                	model.setTempleteId(templeteId);
                }
                
                if(flowstate == 1 || flowstate == 3){
                	model.setEndFlag(0); //0 - 结束
                }
                
                String appTypeName = "";
                String appEnumStr = "";
                if("FORM".equals(bodyType)){
                    appTypeName = Constant.getCommonString("application."+ApplicationCategoryEnum.form.key()+".label");
                    appEnumStr = ApplicationCategoryEnum.form.toString();
                }else{
                    appTypeName = Constant.getCommonString("application."+ApplicationCategoryEnum.collaboration.key()+".label");
                    appEnumStr = ApplicationCategoryEnum.collaboration.toString();
                }
                model.setAppType(appTypeName);
                model.setAppEnumStr(appEnumStr);
                
                String depName = "";
                String accName = "";
                V3xOrgMember member = null;
                try {
                    member = orgManager.getMemberById(startMemberId);
                    if(member != null){
                        V3xOrgDepartment dep = orgManager.getDepartmentById(member.getOrgDepartmentId());
                        if(dep != null){
                            depName = dep.getName();
                        }
                        V3xOrgAccount acc = orgManager.getAccountById(dep.getOrgAccountId());
                        if(acc != null){
                            accName = acc.getShortname();
                            model.setAccountId(acc.getId());
                        }
                        model.setInitiator(member.getName());
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
                
                if(CurrentUser.get().isGroupAdmin()){
                    model.setDepName(depName+"("+accName+")");
                }else{
                    model.setDepName(depName);
                }
                models.add(model);
            }
        }
        return models;
    }
    /**
     * @deprecated
     * @param user_id
     * @return
     */
    public StatModel PersonalStatFilter(long user_id){
        return null;
    }


    /**
      * 根据季度来统�
      * @deprecated 废弃，待清理
      */
    public int[][] StatFilterByQuarter(long userId) {
        String hql1 = "select count(*),affair.state,affair.isDelete from Affair as affair,ColSummary as summary"
                + " where affair.objectId=summary.id"
                + " and affair.memberId=:userId"
                + " and affair.app=" + ApplicationCategoryEnum.collaboration.getKey()
                //+ " and affair.archiveId is null"
                + " and affair.createDate > :Begin and affair.createDate < :End";

        //重要程度限定--重要
        String important = " and summary.importantLevel=2";
        String hql2 = hql1 + important;

        //重要程度限定--非常重要
        String veryImportant = " and summary.importantLevel=3";
        String hql3 = hql1 + veryImportant;

        //超期这个概念只对已发、待办有效。采用个人事项表的deadline 日期与当前日期相比较
        //jincm修改
        String over = " and affair.isOvertopTime = true"
                + " and summary.finishDate is null";
        String hql4 = hql1 + over;
//        String hql4 = hql1;

        //流程未晚�?
        String finish = " and summary.finishDate is null";
        String hql5 = hql1 + finish;

        //流程已晚�?
        String finished = " and summary.finishDate is not null";
        String hql6 = hql1 + finished;

        //将group by语句最后加�?
        String group = " group by affair.state,affair.isDelete";
        hql1 = hql1 + group;
        hql2 = hql2 + group;
        hql3 = hql3 + group;
        hql4 = hql4 + group;
        hql5 = hql5 + group;
        hql6 = hql6 + group;

        int[] yearandquarter = getQuarter();
        //本季度的
        int[] result1 = getDefaultQuartStat(userId, hql1, yearandquarter[0], yearandquarter[1]);//默认的全部协同的查询
        int[] result2 = getDefaultQuartStat(userId, hql2, yearandquarter[0], yearandquarter[1]);//对重要协同的分类查询
        int[] result3 = getDefaultQuartStat(userId, hql3, yearandquarter[0], yearandquarter[1]);//对非常重要的协同的分类查�?
        int[] result4 = getQuartStat(userId, hql4, yearandquarter[0], yearandquarter[1], 0);//超期的，只有待办、已办超期有�?0不设日期
        int[] result5 = getDefaultQuartStat(userId, hql5, yearandquarter[0], yearandquarter[1]);//流程未完成的
        int[] result6 = getDefaultQuartStat(userId, hql6, yearandquarter[0], yearandquarter[1]);//流程已完成的

        int[] line1 = {result1[0], result2[0], result3[0], result4[0], result5[0], result6[0]};//第一行待发的协同(本季�?
        int[] line2 = {result1[1], result2[1], result3[1], result4[1], result5[1], result6[1]};//第二行已�?
        int[] line3 = {result1[2], result2[2], result3[2], result4[2], result5[2], result6[2]};//第三行待�?
        int[] line4 = {result1[3], result2[3], result3[3], result4[3], result5[3], result6[3]};//第四行已�?
        int[][] results = new int[4][6];//新纪录四行六�?
        results[0] = line1;
        results[1] = line2;
        results[2] = line3;
        results[3] = line4;
        return results;
    }

    /**
      * 默认�?
      * 取得当前的年与季�?
      * @deprecated 废弃，待清理
      */
    public int[] getQuarter() {
        int[] yearquarter = new int[2];
        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int quarter = 1;
        if (month >= 0 && month <= 2) {
            quarter = 1;
        }
        if (month >= 3 && month <= 5) {
            quarter = 2;
        }
        if (month >= 6 && month <= 8) {
            quarter = 3;
        }
        if (month >= 9 && month <= 11) {
            quarter = 4;
        }
        yearquarter[0] = year;
        yearquarter[1] = quarter;
        return yearquarter;
    }

    /**
      * 不管超期与否的查�?
      * @deprecated 废弃，待清理
      */
    public int[] getDefaultQuartStat(long userId, String hql, int year, int quarter) {
        int[] result = getQuartStat(userId, hql, year, quarter, 0);
        return result;
    }

    /**
      * 执行查询语句，取得结果集
      * @deprecated 废弃，待清理
      */
    public int[] getQuartStat(long userId, String hql, int year, int quarter, int type) {
        //todo：加上Quarter限定
        Map map = StatUtil.getQuarterDate(year, quarter);
        
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        namedParameterMap.put("userId", userId);
        namedParameterMap.put("Begin", map.get("BeginOfQuarter"));
        namedParameterMap.put("End", map.get("EndOfQuarter"));
        if (type == 1) {
            java.util.Date date = new java.util.Date();
            Timestamp stamp = new Timestamp(date.getTime());
            namedParameterMap.put("now", stamp);
        }
        List results = find(hql, -1, -1, namedParameterMap);

        int[] StatInt = new int[4];
        for (int i = 0; i < results.size(); i++) {
            Object[] result = (Object[]) results.get(i);
//            String subApp = (String) result[1];
            int state = (Integer) result[1];
            Boolean isDelete=(Boolean) result[2];
            if(isDelete==null) isDelete=false;

            //待发
            if ( state == StateEnum.col_waitSend.key() && isDelete==false) {
                StatInt[0] = (Integer) result[0];
            }
            //已发
            if ( state == StateEnum.col_sent.key() && isDelete==false) {
                StatInt[1] = StatInt[1] + (Integer) result[0];
            }
            //待办
            if ( state == StateEnum.col_pending.key() && isDelete==false) {
                StatInt[2] = (Integer) result[0];
            }
            //已办
            if ( state == StateEnum.col_done.key() && isDelete==false) {
                StatInt[3] = StatInt[3] + (Integer) result[0];
            }
            //已被删除的不计入统计
//            if ( state == StateEnum.col_sent.key() && isDelete==true) {
//                StatInt[1] = StatInt[1] + (Integer) result[0];
//            }
//            //待办已办被删除的记入已办�?
//            if ( state == StateEnum.col_pending.key() && isDelete==true) {
//                StatInt[3] = StatInt[3] + (Integer) result[0];
//            }
//            if ( state == StateEnum.col_done.key() && isDelete==true) {
//                StatInt[3] = StatInt[3] + (Integer) result[0];
//            }
        }
        return StatInt;
    }

    public void saveComment(ColComment comment, boolean isSendMessage) throws ColException {
        super.save(comment);
        if(isSendMessage){
	        User user = CurrentUser.get();
	    	Long summaryId = comment.getSummaryId();
	    	Long memberId = comment.getMemberId(); //意见的书写者
	    	Long startMemberId = comment.getStartMemberId();
	    	String memberName = comment.getMemberName();
	    	String commentContent = MessageUtil.getComment4Message(comment.getContent());
	    	int commentType = Strings.isTrue(comment.getIsHidden()) || Strings.isTrue(comment.getIsHidden4Sender()) ? 0 :  Strings.isBlank(commentContent) ? -1 : 1;
	    	final ColSummary summary = get(summaryId);
	    	List<Affair> affairList = affairManager.findbymemberIdAndSummaryId(memberId, summaryId);
	    	
	    	//给意见处理者发消息
	    	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
	    	if(affairList == null) return;
	    	Affair dealAffair = affairList.get(0);
	    	Long dealAffairMemberId = dealAffair.getMemberId();
            if(!comment.getWriteMemberId().equals(dealAffairMemberId)){
                receivers.add(new MessageReceiver(dealAffair.getId(), dealAffairMemberId, "message.link.col.done", dealAffair.getId(), comment.getId()));
                try {
                    Integer importantLevel = affairList.get(0).getImportantLevel();
        			String forwardMemberId = affairList.get(0).getForwardMember();
        			int forwardMemberFlag = 0;
        			String forwardMember = null;
        			if(Strings.isNotBlank(forwardMemberId)){
        				try {
        					forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
        					forwardMemberFlag = 1;
        				}
        				catch (Exception e) {
        				}
        			}
        			
                    if(user.getAgentToId() != -1 && comment.getProxyName() != null){
                        V3xOrgMember theMember = null;
                        try {
                            theMember = orgManager.getEntityById(V3xOrgMember.class,user.getAgentToId());
                        } catch (BusinessException e) {
                            log.error("协同回复意见,获取接收人异常", e);
                            throw new ColException("saveComment getEntityById Failed");
                        }
                        String proxyName = theMember.getName();
                        userMessageManager.sendSystemMessage(new MessageContent("col.reply", summary.getSubject(), proxyName, memberName, forwardMemberFlag, forwardMember, commentType, commentContent)
                        	.add("col.agent.reply", user.getName()).setImportantLevel(importantLevel),
                                ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
                    }else{
                        userMessageManager.sendSystemMessage(new MessageContent("col.reply", summary.getSubject(), user.getName(), memberName, forwardMemberFlag, forwardMember, commentType, commentContent)
                        	.setImportantLevel(importantLevel),
                                ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
                    }
                } catch (MessageException e) {
                    log.error("回复意见消息提醒失败", e);
                    throw new ColException("saveComment sendMessage Failed");
                }
            }
			
			//意见处理者不为协同发起者时，给发起人发消息
			if(!memberId.equals(startMemberId)){
				List<Affair> affairList1 = affairManager.findbymemberIdAndSummaryId(startMemberId, summaryId);
		    	
				Set<MessageReceiver> receivers1 = new HashSet<MessageReceiver>();
		    	if(affairList1 == null) return;
		    	Affair startAffair = affairList1.get(0);
		    	Long startAffairMemberId = startAffair.getMemberId();
                if(comment.getWriteMemberId().equals(startAffairMemberId)){
                    return;
                }
		    	receivers1.add(new MessageReceiver(startAffair.getId(), startAffairMemberId, "message.link.col.done", startAffair.getId(), comment.getId()));
		    	try {
		    		Integer importantLevel = affairList1.get(0).getImportantLevel();
        			String forwardMemberId = affairList.get(0).getForwardMember();
        			int forwardMemberFlag = 0;
        			String forwardMember = null;
        			if(Strings.isNotBlank(forwardMemberId)){
        				try {
        					forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
        					forwardMemberFlag = 1;
        				}
        				catch (Exception e) {
        				}
        			}
		    		if(user.getAgentToId() != -1 && comment.getProxyName() != null){
		    			V3xOrgMember theMember = null;
		    	        try {
		    	            theMember = orgManager.getEntityById(V3xOrgMember.class,user.getAgentToId());
		    	        } catch (BusinessException e) {
		    	        	log.error("协同回复意见,获取接收人异常", e);
		    	            throw new ColException("saveComment getEntityById Failed");
		    	        }
		    	        String proxyName = theMember.getName();
		    			userMessageManager.sendSystemMessage(new MessageContent("col.reply", summary.getSubject(), proxyName, memberName, forwardMemberFlag, forwardMember, commentType, commentContent)
		    				.add("col.agent.reply", user.getName()).setImportantLevel(importantLevel),
		    					ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers1, importantLevel);
		    		}else{
		    			userMessageManager.sendSystemMessage(new MessageContent("col.reply", summary.getSubject(), user.getName(), memberName, forwardMemberFlag, forwardMember, commentType, commentContent)
		    				.setImportantLevel(importantLevel),
		    					ApplicationCategoryEnum.collaboration, user.getId(), receivers1, importantLevel);
		    		}
				} catch (MessageException e) {
					log.error("回复意见消息提醒失败", e);
					throw new ColException("saveComment sendMessage Failed");
				}
			}
        }
    }

    //这样很不好，最好把save和发消息分开
    public void saveOpinion(ColOpinion opinion, boolean isSendMessage) throws ColException {
        super.save(opinion);
        if(isSendMessage){
	        User user = CurrentUser.get();
	    	Long summaryId = opinion.getSummaryId();
	    	ColSummary summary = get(summaryId);
	    	List<Affair> affairList = affairManager.findAvailabilityByObject(ApplicationCategoryEnum.collaboration, summaryId);
	    	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
	    	for(Affair affair : affairList){
	    		Long memberId = affair.getMemberId();
	    		Long senderId = affair.getSenderId();
	    		if(memberId.intValue() == senderId.intValue()){
	    			continue;
	    		}
	    		if(affair.getState() == StateEnum.col_pending.key()){
	    			receivers.add(new MessageReceiver(affair.getId(), memberId, "message.link.col.pending", affair.getId(), opinion.getId()));
	    		}else{
	    			receivers.add(new MessageReceiver(affair.getId(), memberId, "message.link.col.done", affair.getId(), opinion.getId()));
	    		}
	    		
	    	}
	    	Integer importantLevel = affairList.get(0).getImportantLevel();
	    	String forwardMemberId = affairList.get(0).getForwardMember();
	    	int forwardMemberFlag = 0;
	    	String forwardMember = null;
	    	if(Strings.isNotBlank(forwardMemberId)){
	    		try {
	    			forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
	    			forwardMemberFlag = 1;
	    		}
	    		catch (Exception e) {
	    		}
	    	}
	    	String opinionContent = MessageUtil.getComment4Message(Strings.toText(opinion.getContent()));
	    	try {
				userMessageManager.sendSystemMessage(new MessageContent("col.addnote", summary.getSubject(), user.getName(), forwardMemberFlag, forwardMember, opinionContent).setImportantLevel(importantLevel),
						ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
			} catch (MessageException e) {
				log.error("发起人增加附言消息提醒失败", e);
				throw new ColException("saveOpinion sendMessage Failed");
			}
        }
    }

    public String getPolicyBySummary(ColSummary summary) throws ColException {
        List list = super.find("from com.seeyon.v3x.affair.domain.Affair affair where subObjectId is null and objectId = ?", null, summary.getId());
        if (list.size() == 0) {
            return SeeyonPolicy.DEFAULT_POLICY;
        } else {
            Affair affair = (Affair) list.get(0);
            return getPolicyByAffair(affair);
        }
    }

    public String getPolicyByAffair(Affair affair) throws ColException {
        SeeyonPolicy p = ColHelper.getPolicyByAffair(affair);
        if (p == null)
            return SeeyonPolicy.DEFAULT_POLICY;
        String policy = p.getId();
        return policy;
    }

    public void setFinishedFlag(ColSummary summary) throws ColException {
    	Timestamp finishDate = new Timestamp(System.currentTimeMillis());
        summary.setFinishDate(finishDate);
    	StringBuilder sb  = new StringBuilder();
    	sb.append("UPDATE " + ColSummary.class.getCanonicalName() + " set finishDate=? , ");
    	sb.append("overTime = ?, overWorkTime = ?,");
    	sb.append("runTime = ? ,runWorkTime = ?, state=? ");
    	sb.append("where id=? ");
        super.bulkUpdate(sb.toString(), null, finishDate, 
        		summary.getOverTime() == null? 0:summary.getOverTime(),
        		summary.getOverWorkTime() == null ? 0:summary.getOverWorkTime(),
        		summary.getRunTime() == null ? 0: summary.getRunTime(),
        		summary.getRunWorkTime() == null ?0 :summary.getRunWorkTime(),
        		summary.getState(),summary.getId());
        
        //NF 如果是子流程且主流程受约束，则给下节点待办发消息可处理。
        NewflowRunning mainflowRun = newflowManager.getAffinedMainflow(summary.getId());
        if(mainflowRun != null){
            List<Long> nextNodeIds = new ArrayList<Long>();
            BPMProcess p = ColHelper.getRunningProcessByProcessId(mainflowRun.getMainProcessId());
            if(p != null){
                nextNodeIds = ColHelper.getLatterActivityIds(p.getActivityById(mainflowRun.getMainNodeId()));
            }
            List<Affair> affairList = affairManager.getPendingAffairListByNodes(mainflowRun.getMainSummaryId(), nextNodeIds);
            if(affairList != null && !affairList.isEmpty()){
                Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                for (Affair affair : affairList) {
                    receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(), "message.link.col.pending", affair.getId(), null));
                }
                Integer importantLevel = summary.getImportantLevel();
                try {
                    userMessageManager.sendSystemMessage(new MessageContent("col.mainflow.canProcess", summary.getSubject()).setImportantLevel(importantLevel),
                            ApplicationCategoryEnum.collaboration, CurrentUser.get().getId(), receivers, importantLevel);
                } catch (MessageException e) {
                    log.error("子流程约束，给主流程受约束的下节点待办发消息异常", e);
                    throw new ColException("子流程约束，给主流程受约束的下节点待办发消息异常");
                }
            }
        }
    }


    public ColSummary saveForward(Long originalSummaryId, Long newSummaryId, FlowData flowData,
                                  boolean forwardOriginalNote, boolean foreardOriginalopinion, 
                                  ColOpinion senderOpinion, String uploadAttFlag,String originalContent) throws ColException {
        ColSummary originalSummary = this.getColAllById(originalSummaryId);
        boolean isStoreFlag = false;
        if(originalSummary == null){
        	originalSummary = hisColManager.getColAllById(originalSummaryId);
        	isStoreFlag = originalSummary != null;
        }

        java.util.Date now = new java.util.Date();

        ColSummary summary = null;
		try {
			summary = (ColSummary) originalSummary.clone();
		}
		catch (CloneNotSupportedException e1) {
			log.error("协同转发获取原协同异常 [originalSummaryId = " + originalSummaryId + "]", e1);
			throw new ColException("Sumamry No Found");
		}
		
		Set<ColOpinion> opinions = new HashSet<ColOpinion>();
		Set<ColComment> comments = new HashSet<ColComment>();
	   	Map<Long,Long> keyMap = new HashMap<Long,Long>();
        User user = CurrentUser.get();

        summary.setId(newSummaryId);
        //将原发起人作为转发人
        summary.addForwardMember(summary.getStartMemberId());
        summary.setStartMemberId(user.getId());
        summary.setCreateDate(new java.sql.Timestamp(now.getTime()));
        summary.setStartDate(new Timestamp(now.getTime()));

        //根据选人界面传来的people生成流程模板XML
        String processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, null,false);

        summary.setProcessId(processId);
        summary.setFinishDate(null);
        summary.setState(Constant.flowState.run.ordinal());
        summary.setOrgAccountId(CurrentUser.get().getLoginAccount());
     
        //原正�?
        ColBody originalBody = originalSummary.getFirstBody();

        Map<Long, Long> idMap = new HashMap<Long, Long>();

        if (foreardOriginalopinion || forwardOriginalNote) { //转发原处理人意见
            //原意�?
            Set<ColOpinion> originalOpinions = originalSummary.getOpinions();
            //原回复意�?
            Set<ColComment> originalComments = originalSummary.getComments();

            if (originalOpinions != null && !originalOpinions.isEmpty()) {
            	DetachedCriteria criteria = DetachedCriteria.forClass(isStoreFlag ? HisColOpinion.class : ColOpinion.class);
            	criteria.setProjection(Projections.max("levelId"));
            	criteria.add(Expression.eq("summaryId", originalSummaryId));
            	
            	int maxLevelId = (Integer)(super.executeUniqueCriteria(criteria));
            	
                for (ColOpinion originalOpinion : originalOpinions) {             
                    int opinionType = originalOpinion.getOpinionType();
                    
    				if(opinionType == ColOpinion.OpinionType.draftOpinion.ordinal()){ //草稿不要
    					continue;
    				}

                    //不转发附言
                    if (!forwardOriginalNote && opinionType == ColOpinion.OpinionType.senderOpinion.ordinal()) {
                        continue;
                    }
                    else if (!foreardOriginalopinion && originalOpinion.isChuliOpinion()) {//不转发处理意见
                    	continue;
                    }

                    Long orignalOpinionId = originalOpinion.getId();

                    ColOpinion opinion = null;
					try {
						opinion = (ColOpinion) originalOpinion.clone();
						opinion.setAffairId(null);
					}
					catch (CloneNotSupportedException e1) {
						log.error("协同转发CloneNotSupportedException", e1);
						throw new ColException("协同转发 CloneNotSupportedException");
					}
					
					if(originalOpinion.getIsHidden().booleanValue()){
						opinion.setContent(null);
					}

                    opinion.setIdIfNew();
                    opinion.setSummaryId(newSummaryId);

                    //最近的协同意见
                    if (originalOpinion.getLevelId().intValue() == 0) {
                        opinion.setLevelId(maxLevelId + 1);
                    }

                    opinions.add(opinion);

                    idMap.put(orignalOpinionId, opinion.getId());

                    keyMap.put(orignalOpinionId, opinion.getId());
                }

                if (originalComments != null) {
                    for (ColComment originalComment : originalComments) {
                    	Long orignalOpinionId = originalComment.getOpinionId();
                    	Long newOpinionId = idMap.get(orignalOpinionId);
                    	
                        if (newOpinionId == null) {
                            continue;
                        }

                        ColComment comment = null;
						try {
							comment = (ColComment) originalComment.clone();
						}
						catch (CloneNotSupportedException e) {
							log.error("转发复制原回复意见异�常 [originalSummaryId = " + originalSummaryId + "]", e);
                            throw new ColException("comment No Found");
						}
						
						if(originalComment.getIsHidden().booleanValue() == true){
							comment.setContent(null);
						}

                        comment.setIdIfNew();
                        comment.setOpinionId(newOpinionId);
                        comment.setSummaryId(newSummaryId);

                        comments.add(comment);
                        
                        keyMap.put(originalComment.getId(), comment.getId());
                    }
                }
            }
        }

        ColBody body = new ColBody();
        body.setIdIfNew();

        //office正文
        String originalBodyType = originalBody.getBodyType();
        body.setBodyType(originalBodyType);
        if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(originalBodyType)
                || com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(originalBodyType)
                || com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(originalBodyType)
                || com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(originalBodyType)
                || com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF.equals(originalBodyType)) {
            try {
                V3XFile file = this.fileManager.clone(new Long(originalBody.getContent()), true);
                body.setContent(String.valueOf(file.getId()));
            }
            catch (Exception e) {
                log.warn("转发复制office正文异常 [originalSummaryId = " + originalSummaryId + "]", e);
            }
        }
        else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM.equals(originalBodyType)) {
        	body.setContent(originalContent);
        	body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);  //表单转发后为html
        	summary.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
        }
        else {
            body.setContent(originalBody.getContent());
        }
        
        body.setCreateDate(new java.sql.Timestamp(now.getTime()));
        body.setSummaryId(newSummaryId);
        if(!com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(originalBodyType) && !com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM.equals(originalBodyType))
        {
			body.setContentName(originalSummary.getFirstBody().getContent());
			signetManager.insertSignet(Long.parseLong(originalBody.getContent()), Long.parseLong(body.getContent()));
        }
        summary.getBodies().add(body);

        if (!senderOpinion.isNew()) {
            senderOpinion.setSummaryId(summary.getId());
            senderOpinion.setOpinionType(ColOpinion.OpinionType.senderOpinion.ordinal());
            senderOpinion.setCreateDate(new java.sql.Timestamp(now.getTime()));
            senderOpinion.setWriteMemberId(user.getId());
            opinions.add(senderOpinion);
        }
        
        keyMap.put(originalSummaryId, newSummaryId);
        List<Attachment> newAtts =  getAttachments(originalSummaryId,newSummaryId,keyMap);
        this.attachmentManager.create(newAtts);
       
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(uploadAttFlag)){
        	summary.setHasAttachments(true);
        }
        
        summary.setFormAppId(null);
        summary.setFormId(null);
        summary.setFormRecordId(null);
        summary.setTempleteId(null);
        summary.setArchiveId(null);//转发事项不拷贝原事项预归档信息
        
        WorkflowEventListener.setColSummary(summary);
        
        long caseId = ColHelper.runCase(processId);
        summary.setCaseId(caseId);
        
        super.save(summary);
        super.savePatchAll(opinions);
        super.savePatchAll(comments);
        
        Affair affair = new Affair();
        affair.setIdIfNew();
        
        affair.setApp(ApplicationCategoryEnum.collaboration.key());
        affair.setCreateDate(new java.sql.Timestamp(now.getTime()));
        affair.setMemberId(user.getId());
        affair.setObjectId(summary.getId());
        affair.setSenderId(user.getId());
        affair.setState(StateEnum.col_sent.key());
        affair.setSubState(SubStateEnum.col_normal.key());
        affair.setIsTrack(senderOpinion.affairIsTrack);
        affair.setSubject(summary.getSubject());
        
        ColHelper.addExtPropsToAffairFromSummary(affair, summary, body);

        super.save(affair);
        
        return summary;
    }
    private List<Attachment> getAttachments(Long originalSummaryId,Long newSummaryId,Map<Long,Long> keyMap){
    	   //原所有附�?
    	List<Attachment> newAtts = new ArrayList<Attachment>();
        List<Attachment> attachments = this.attachmentManager.getByReference(originalSummaryId);
        java.util.Date now = new java.util.Date();

    	 if (attachments != null) {
             for (Attachment att : attachments) {
            	 //图片不复制
             	 if(att.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.IMAGE.ordinal()){
             		continue;
             	 }
	        	 Long attid = keyMap.get(att.getSubReference());
	        	 
	        	 if(attid==null)continue;
	        	 
	             Attachment newAtt = null;
	             try {
					newAtt = (Attachment) att.clone();
	             }
	             catch (Exception e1) {
	            	 log.warn("转发复制原意见附件异�常 [originalSummaryId = " + originalSummaryId + "]", e1);
	            	 continue;
	             }
	             newAtt.setIdIfNew();
	         
	             if(att.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal()){
					Long newFileId = UUIDLong.longUUID();
					try {
						this.fileManager.clone(att.getFileUrl(), att.getCreatedate(), newFileId, now);
					}
					catch (Exception e) {
	                 log.warn("转发复制原意见附件异�常 [originalSummaryId = " + originalSummaryId + "]", e);
					}
					newAtt.setFileUrl(newFileId);
	             }

                 newAtt.setReference(newSummaryId);
                 newAtt.setSubReference(attid); //新的意见id
                 newAtt.setCreatedate(now);

                 newAtts.add(newAtt);
             }
         }
    	return newAtts;
    }
    /*
     * TODO：目前queryByCondition不支持query by track
     * @see com.seeyon.v3x.collaboration.manager.ColManager#queryTrackList()
     */
        public List<ColSummaryModel> queryTrackList(List<Long> templeteIds) throws ColException {
            List<ColSummaryModel> summaryModelList = queryByCondition("isTrack", null, null,0, templeteIds);
            return summaryModelList;
        }
        
        @SuppressWarnings("unchecked")
		public List<Affair> queryList4Mobile(Long memberId, StateEnum state, 
        		String condition, String textfeild, String textfeild1,Integer... category) throws ColException{
			User user = CurrentUser.get();
			memberId = user.getId();
    		Long agentToMemberId = user.getAgentToId(); //我被代理的人的Id
    		V3xOrgMember currentMember = null;
    		try {
        		currentMember = this.orgManager.getMemberById(memberId);
    			Long agentId = currentMember.getAgentId();
    			if(agentId != null && agentId != V3xOrgEntity.DEFAULT_NULL_ID 
    					&& !(state == StateEnum.col_waitSend || state == StateEnum.col_sent || state == StateEnum.col_done )){ //我设了代理人
    				return new ArrayList<Affair>();
    			}
    		}
    		catch (Exception e1) {
    			logger.warn("", e1);
    		}
    		
    		boolean isProxy = false;
    		java.util.Date agentStamp = null;
    		// 我有被代理人,要去取他的数据,条件有: affair.createDate在他设置代理时间点之后
    		if (agentToMemberId != -1 && state != StateEnum.col_waitSend && state != StateEnum.col_sent) {
    			isProxy = true;

    			try {
    				agentStamp = orgManager.getMemberById(agentToMemberId).getAgentTime();// 从agentToMember里面取设置代理时间点
    			}
    			catch (Exception e1) {
    				log.warn("", e1);
    			}
    		}
        	
        	DetachedCriteria criteria = DetachedCriteria.forClass(Affair.class);
        	/*criteria.setProjection(
    			Projections.projectionList()
	    			.add(Projections.property("id"))
	    			.add(Projections.property("subject"))
	    			.add(Projections.property("senderId"))
	    			.add(Projections.property("createDate"))
	    			.add(Projections.property("forwardMember"))
	    			.add(Projections.property("resentTime"))
	    			.add(Projections.property("completeTime"))
	    			.add(Projections.property("app"))
	    			.add(Projections.property("bodyType"))
        	);
        	*/
        	if(isProxy && agentStamp != null){
        		if(state == StateEnum.col_pending){
        			criteria.add(Expression.in("memberId", new Object[]{agentToMemberId, memberId}));
				}
        		else{
	        		criteria.add(
	    				Expression.or(Expression.eq("memberId", memberId), 
	        				Expression.and(
	        						Expression.eq("memberId", agentToMemberId),
	        						Expression.gt("completeTime", agentStamp)
	        				)
		        		)
					); //取我和被代理人的
        		}
        	}
        	else{
        		criteria.add(Expression.eq("memberId", memberId));
        	}
        	
        	criteria.add(Expression.eq("state", state.key()));
        	if(category == null || category.length ==0){
        		criteria.add(Expression.eq("app", ApplicationCategoryEnum.collaboration.key()));    	
        	}else{
        		criteria.add(Expression.in("app", category));    	
        	}
        	criteria.add(Expression.eq("isDelete", false));
        	criteria.add(Expression.isNull("archiveId"));
        	        	
        	if("title".equalsIgnoreCase(condition) && StringUtils.isNotBlank(textfeild)){
        		criteria.add(Expression.like("subject", "%" + textfeild + "%"));
        	}
        	
        	criteria.addOrder(Order.desc("createDate"));
        	return super.executeCriteria(criteria);
        }

		@SuppressWarnings("unchecked")
		public ColSummary getSimpleColSummaryById(long summaryId) throws ColException {
			ColSummary summary = super.getSimpleObject(summaryId, "subject", 
					"startMemberId", "importantLevel", "createDate", "resentTime", "forwardMember", "identifier", "caseId","processId","templeteId");
			if(summary == null){
				return null;
			}
			
			summary.setId(summaryId);
			
			return summary;
		}
		
		/*
		 * 根据ID取得协同的内容
		 * 注意：协同附件不在此做处理
		 * @see com.seeyon.v3x.index.share.interfaces.IndexEnable#getIndexInfo(long)
		 */
		public IndexInfo getIndexInfo(long id) throws Exception {
		ColSummary colSummary = null;
		try {
			colSummary = getColAllById(id);
		} catch (ColException e) {
			log.error("ColManagerImpl getIndexInfo getColAllById", e);
			throw new ColException("ColManagerImpl getIndexInfo getColAllById", e);
		}
		if (colSummary == null)
			return null;
		IndexInfo info = new IndexInfo();
		//V320增加项 start 
		info.setStartMemberId(colSummary.getStartMemberId());
		info.setHasAttachment(colSummary.isHasAttachments());
		info.setImportantLevel(colSummary.getImportantLevel());
		//end
		
		
		info.setEntityID(id);
		if("FORM".equals(colSummary.getBodyType())){
			info.setAppType(ApplicationCategoryEnum.form);
		}else{
			info.setAppType(ApplicationCategoryEnum.collaboration);
		}
	
		if(colSummary.getArchiveId()!=null&&colSummary.getArchiveId()!=-1)
		{
			info.setAppType(ApplicationCategoryEnum.doc);
			Affair affair = affairManager.getCollaborationSenderAffair(colSummary.getId());
			if(affair != null){
				DocResource dr =getDocHierarchyManager().getDocResBySourceId(affair.getId());
				if(dr!=null){
					IndexUtil.convertToAccessory(info);
					info.setEntityID(dr.getId());
					info.addExtendProperties("docType", String.valueOf(dr.getMimeTypeId()), IndexInfo.FieldIndex_Type.IndexNo.ordinal());
					// 权限
					AuthorizationInfo ai = new AuthorizationInfo();
					Set<Integer> aclLevels = new HashSet<Integer>();
					aclLevels.add(Constants.ALLPOTENT);
					aclLevels.add(Constants.EDITPOTENT);
					aclLevels.add(Constants.READONLYPOTENT);
					aclLevels.add(Constants.BROWSEPOTENT);
					DocAclManager docAcl= (DocAclManager)ApplicationContextHolder.getBean("docAclManager");
					DocUtils docUtils= (DocUtils)ApplicationContextHolder.getBean("docUtils");
					Map<Long, String> acls =docAcl.getSpecialAclsByDocResourceId(dr, aclLevels);
					// 如果acls中有库管理员，则将其从acls中删除，将库管理员放入到role角色中去
					List<Long> dlos = docUtils.getOwnersByDocLibId(dr.getDocLibId());
					if (dlos != null) {
						for (Long m : dlos) {
							if (acls.get(m) != null
									&& V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(acls.get(m))) {
								acls.remove(m);
							}
						}
					}
					List<Long> owners = new ArrayList<Long>();
					List<Long> depts = new ArrayList<Long>();
					List<Long> posts = new ArrayList<Long>();
					List<Long> accounts = new ArrayList<Long>();
					List<String> roles = new ArrayList<String>();
					Set<Long> keySet = acls.keySet();
					for (Long k : keySet) {
						if (V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(acls.get(k)))
							owners.add(k);
						else if (V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(acls.get(k)))
							depts.add(k);
						else if (V3xOrgEntity.ORGENT_TYPE_POST.equals(acls.get(k)))
							posts.add(k);
						else if (V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(acls.get(k)))
							accounts.add(k);
					}

					roles.add(com.seeyon.v3x.indexInterface.Constant.DOC_LIB + "|" + dr.getDocLibId());

					ai.setOwner(owners);
					ai.setDepartment(depts);
					ai.setPost(posts);
					ai.setAccount(accounts);
					ai.setRole(roles);
					info.setAuthorizationInfo(ai);
				}
			 }
		}
		else{
			//在此取得权限！！
			AuthorizationInfo ai = new AuthorizationInfo();
			List<Long> list = affairManager.getOwnerList(ApplicationCategoryEnum.collaboration, id);
			ai.setOwner(list);
			info.setAuthorizationInfo(ai);
		}
		
		/*if(colSummary.getProjectId() != null && info.getAuthorizationInfo() != null){
			List<Long> projectList = new ArrayList<Long>();
			projectList.add(colSummary.getProjectId());
			info.getAuthorizationInfo().setProject(projectList);
		}*/
		V3xOrgMember member = null;
        try {
            member = orgManager.getEntityById(V3xOrgMember.class, colSummary.getStartMemberId());
        }
        catch (BusinessException e) {
        	log.error("ColManagerImpl getIndexInfo getColAllById", e);
            throw new BusinessException("ColManagerImpl getIndexInfo getEntityById", e);
        }
        
		info.setAuthor(member.getName());
		info.setTitle(colSummary.getSubject());
		java.util.Date date1 = new java.util.Date(colSummary.getCreateDate().getTime());
		info.setCreateDate(date1);
		Set comments = colSummary.getComments();
		Set opinions = colSummary.getOpinions();
		StringBuffer commentStr = null;
		StringBuffer opinionStr = null;
		if (comments != null && comments.size() > 0) {
			commentStr = new StringBuffer();
			Iterator it = comments.iterator();
			while (it.hasNext()) {
				ColComment comm = (ColComment) it.next();
				if (comm.getIsHidden() == false){
//					commentStr.append(Functions.showMemberName(comm.getWriteMemberId()));
//					if(Strings.isNotBlank(comm.getProxyName())){
//						commentStr.append(ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource","col.opinion.proxy",comm.getProxyName()));
//					}
					appendWriteName(commentStr, comm.getProxyName(),comm.getWriteMemberId());
					commentStr.append(comm.getContent());
				}
			}
			info.setComment(commentStr.toString());
		}
		if (opinions != null && opinions.size() > 0) {
			opinionStr = new StringBuffer();
			Iterator it1 = opinions.iterator();
			while (it1.hasNext()) {
				ColOpinion opin = (ColOpinion) it1.next();
				if (opin.getContent() != null && opin.getIsHidden() == false){
					appendWriteName(opinionStr, opin.getProxyName(),opin.getWriteMemberId());
					opinionStr.append(CollaborationFunction.getOpinionAttitude(opin.getAttitude()));
					opinionStr.append(opin.getContent());
				}
			}
			info.setOpinion(opinionStr.toString());
		}
		ColBody body = colSummary.getFirstBody();
		if ("HTML".equals(body.getBodyType())) {
			info.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
			info.setContent(body.getContent());
		} else if ("OfficeWord".equals(body.getBodyType()) || "OfficeExcel".equals(body.getBodyType())) {
			if("OfficeWord".equals(body.getBodyType()))
				info.setContentType(IndexInfo.CONTENTTYPE_WORD);
			else
				info.setContentType(IndexInfo.CONTENTTYPE_XLS);
			Long fileId = Long.parseLong(body.getContent());
			Date date = new Date(body.getCreateDate().getTime());
			info.setContentID(fileId);
			info.setContentCreateDate(date);
			Partition partition = partitionManager.getPartition(date, true);
			info.setContentAreaId(partition.getId().toString());
			String contentPath = this.fileManager.getFolder(body.getCreateDate(), false);
			info.setContentPath(contentPath.substring(contentPath.length()-11)+System.getProperty("file.separator"));
		}else if ("WpsWord".equals(body.getBodyType()) || "WpsExcel".equals(body.getBodyType())) {
			if("WpsWord".equals(body.getBodyType()))
				info.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
			else
				info.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
			Long fileId = Long.parseLong(body.getContent());
			Date date = new Date(body.getCreateDate().getTime());
			info.setContentID(fileId);
			info.setContentCreateDate(date);
			Partition partition = partitionManager.getPartition(date, true);
			info.setContentAreaId(partition.getId().toString());
			String contentPath = this.fileManager.getFolder(body.getCreateDate(), false);
			info.setContentPath(contentPath.substring(contentPath.length()-11)+System.getProperty("file.separator"));
		}else if("FORM".equals(body.getBodyType())){
			Templete templete = templeteManager.get(colSummary.getTempleteId());	
			if(templete == null){
				return null ;
			}
			BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
			if(process == null ){
				return null ;
			}
			String[] formInfo = FormHelper.getFormPolicy(process);
			info.setContentType(0);
	    	User user = CurrentUser.get();
	    	StringBuffer str = new StringBuffer() ;
	    	Map<String,Object> formSub = null ;
	    	if(user != null){
	    		formSub = FormHelper.getFormRunForIndex(user.getId(), user.getName(), user.getLoginName(), colSummary.getFormAppId()+"", colSummary.getFormId()+"", formInfo[2], body.getContent(), colSummary.getId()+"", null, null, false) ;	
	    	}else if(member != null){
	    		formSub = FormHelper.getFormRunForIndex(member.getId(), member.getName(), member.getLoginName(), colSummary.getFormAppId()+"", colSummary.getFormId()+"", formInfo[2], body.getContent(), colSummary.getId()+"", null, null, false) ;
	    	}
			
			if(formSub != null){
				for(String key : formSub.keySet()){
					Object obj = formSub.get(key);
					
					if(obj != null && obj instanceof TIP_InputValueAll){
						str.append(FormHelper.getfileNameName(key)+ ":" ) ;
						TIP_InputValueAll tIP_InputValueAll = (TIP_InputValueAll)obj;
						if(tIP_InputValueAll.getFieldtype() != null && tIP_InputValueAll.getFieldtype().equalsIgnoreCase("DECIMAL")){
							continue ;
						}
						String disPlayValue = tIP_InputValueAll.getDisplayValue();
						if(StringUtils.isNotBlank(disPlayValue)){str.append( disPlayValue+ " ") ;}
						
					}else if(obj != null && obj instanceof List){					
						List<List<TIP_InputValueAll>> row = (List<List<TIP_InputValueAll>>) obj ;
						if(!CollectionUtils.isEmpty(row)){
							for(List<TIP_InputValueAll> column:row){
								if(column != null){
									for(TIP_InputValueAll tIP_InputValueAll : column){
										str.append(FormHelper.getfileNameName(tIP_InputValueAll.getDataAreaName())+ ":" ) ;
										if(tIP_InputValueAll.getFieldtype() != null && tIP_InputValueAll.getFieldtype().equalsIgnoreCase("DECIMAL")){
											continue ;
										}
										String disPlayValue = tIP_InputValueAll.getDisplayValue();
										if(StringUtils.isNotBlank(disPlayValue))
												{str.append( disPlayValue+ " ") ;}
									}
								}
							}
							
						}
					}
				}
			}
			info.setContent(str.toString()) ;
		}
		
		//在此取得权限！！
		if(colSummary.getArchiveId()==null||colSummary.getArchiveId()==-1)
		{
//		在此处理附件
			IndexUtil.convertToAccessory(info);
		}
		return info;
	}

	private void appendWriteName(StringBuffer opinionStr, String proxyName,Long writeMemberId) {
			if(writeMemberId != null){
				opinionStr.append(Functions.showMemberName(writeMemberId));
			}
			if(Strings.isNotBlank(proxyName)){
				opinionStr.append(ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource","col.opinion.proxy",proxyName));
			}
		}

	public void clearSummaryOCA(Long summaryId, boolean isDeleteSenderOpinion){
		this.getHibernateTemplate().bulkUpdate("delete from " + ColComment.class.getName() + " where summaryId=?", new Object[]{summaryId});
		
		String hql = "delete from " + ColOpinion.class.getName() + " where summaryId=?";
		Object[] vals = new Object[]{summaryId};
		if(!isDeleteSenderOpinion){
			hql += " and opinionType <> ?";
			vals = new Object[]{summaryId, ColOpinion.OpinionType.senderOpinion.ordinal()}; 
		}
		this.getHibernateTemplate().bulkUpdate(hql, vals);
		
//		this.getHibernateTemplate().bulkUpdate("delete from " + Affair.class.getName() + " where objectId=? and state!=?", new Object[]{summaryId, StateEnum.col_waitSend.ordinal()});
	}
	
	public ColOpinion getDraftOpinion(long affairId){
		DetachedCriteria criteria = DetachedCriteria.forClass(ColOpinion.class)
			.add(Expression.eq("affairId", affairId))
			.add(Expression.eq("opinionType", ColOpinion.OpinionType.draftOpinion.ordinal()))
		;
		
		return (ColOpinion) super.executeUniqueCriteria(criteria);
	}
	
	private List<ColOpinion> getDealOpinion(long affairId){
		DetachedCriteria criteria = DetachedCriteria.forClass(ColOpinion.class)
		.add(Expression.eq("affairId", affairId))
		.add(Expression.eq("opinionType", ColOpinion.OpinionType.signOpinion.ordinal())).addOrder(Order.desc("createDate"));
		return super.executeCriteria(criteria);
	}
	
	public void deleteDraftOpinion(long opinionId){	
		super.delete(ColComment.class, new Object[][]{{"opinionId", opinionId}});
		super.delete(ColOpinion.class, new Object[][]{{"id", opinionId}});
	}
	
    /**
     * 已办事项改变跟踪状�?
     */
    public void changeTrack(Long affairId, boolean track) {
    	changeTrack(affairId,track,null);
    }
    public void changeTrack(Long affairId, boolean track,List<Long> memberIds) {
    	Affair affair = affairManager.getById(affairId);
    	if(affair == null){ //TODO 不妥
    		return;
    	}
        affair.setIsTrack(track);
        affairManager.updateAffair(affair);
        
        colTrackMemberDao.deleteColTrackMembersByAffairId(affair.getId());
        
        if(track && memberIds != null && ! memberIds.isEmpty()){
        	
        	for(Long trackMemberId :memberIds){
        		ColTrackMember coltm = new ColTrackMember();
        		coltm.setIdIfNew();
        		coltm.setObjectId(affair.getObjectId());
        		coltm.setAffairId(affair.getId());
        		coltm.setMemberId(affair.getMemberId());
        		coltm.setTrackMemberId(trackMemberId);
        		colTrackMemberDao.save(coltm);
        	}
        }
    }
    
    /**
     * 检测是否可以归档
     * 
     * @param affiarIds
     * @return 不能归档协同的Id
     */
    public String checkCanAchive(String affiarIds){
        if(Strings.isBlank(affiarIds)){
    		return "AffairNotExist";
    	}
    	User user = CurrentUser.get();
    	String[] idstrs = affiarIds.split(",");
        for (String s : idstrs) {
            long affiarId = Long.parseLong(s);
            Affair affair = affairManager.getById(affiarId);
            if(affair == null){
            	return "AffairNotExist";
            }
            if(!affair.getMemberId().equals(user.getId())){
                return Constant.getString4CurrentUser("agent.affair.unallowed.pigeonhole", affair.getSubject());
            }
            else if(affair.getSenderId().equals(user.getId()) && affair.getState() == StateEnum.col_sent.getKey()){ //自己的已发
            	return "true";
            }
            else{
                try{
                    String nodePermissionPolicy = getPolicyByAffair(affair);
                    ColSummary summary = this.get(affair.getObjectId());
                    Long flowPermAccountId = user.getLoginAccount();
                	if(summary != null){
                		if(summary.getTempleteId() != null){
                			Templete templete = templeteManager.get(summary.getTempleteId());
                			if(templete != null){
                				flowPermAccountId = templete.getOrgAccountId();
                			}
                		}
                		else{
                			if(summary.getOrgAccountId() != null){
                				flowPermAccountId = summary.getOrgAccountId();
                			}
                		}
                	}
                    boolean can = permissionManager.isActionAllowed(Constant.ConfigCategory.col_flow_perm_policy.name(), nodePermissionPolicy, "Archive", flowPermAccountId);
                    if(!can){
                        return "NoPopedom:" + affair.getSubject();
                    }
                    int state = affair.getState();
                    if(state != StateEnum.col_pending.getKey()
                            && state != StateEnum.col_done.getKey()
                            && state != StateEnum.col_sent.getKey()){
                        return ColHelper.getErrorMsgByAffair(affair);
                    }
                }
                catch(ColException e){
                    log.error(e);
                    return "Exception:" + e.getMessage();
                }
            }
        }
    	return "true";
    }
    
    /**
     * 项目协同
     *
     * @param projectId
     * @throws ColException
     */
    @SuppressWarnings("unchecked")
	public List<Affair> getColSummaryByProjectId(Long projectId, int size, Long phaseId){
    	User user = CurrentUser.get();
    	StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select a,s from " + ColSummary.class.getName() + " as s," + Affair.class.getName() + " as a where s.id=a.objectId ");
		hql.append("and a.memberId=:memberId and a.state in (2,3,4) and s.projectId=:projectId and a.isDelete=false ");
		params.put("memberId", user.getId());
		params.put("projectId", projectId);
    	if(phaseId != null && phaseId != 1){
			hql.append("and s.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.collaboration.key() + ") ");
			params.put("phaseId", phaseId);
		}
    	hql.append("order by a.createDate desc");
    	List<Object[]> list = null;
    	List<Affair> resultList = new ArrayList<Affair>();
    	if(size > 0){
    		list =  super.find(hql.toString(), 0, size, params);
    	}
    	else{
    		list =  super.find(hql.toString(), params);
    	}
    	for(Object[] o : list){
    		Affair a = (Affair)o[0];
    		ColSummary s = (ColSummary)o[1];
			a.isHasAttachments();
			String forwardMember = a.getForwardMember();
			Integer resentTime = a.getResentTime();
			String subject = ColHelper.mergeSubjectWithForwardMembers(a.getSubject(), forwardMember, resentTime, orgManager, null);
			a.setCanForward(s.getCanForward());//是否允许转发
			a.setAddition(subject);  //取协同名称的全部显示信息，放到additon中作为项目标题在前端显示
			resultList.add(a);
    	}
    	return resultList;
    }
    
	/**
	 * 项目协同更多条件查询
	 */
	@SuppressWarnings("unchecked")
	public List<Affair> getColSummaryByCondition(String condition, Long projectId, int size, Long phaseId, Map<String, Object> paramMap) throws ColException {
		User user = CurrentUser.get();
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select a,s from " + ColSummary.class.getName() + " as s," + Affair.class.getName() + " as a where s.id=a.objectId ");
		hql.append("and a.memberId=:memberId and a.state in (2,3,4) and s.projectId=:projectId and a.isDelete=false ");
		params.put("memberId", user.getId());
		params.put("projectId", projectId);

		List<Long> ids = (List<Long>) paramMap.get("author");

		// 拼接查询条件
		if ("title".equals(condition)) {
			if (paramMap.get("title") != null) {
				hql.append(" and s.subject like :title ");
				params.put("title", "%" + SQLWildcardUtil.escape(paramMap.get("title").toString()) + "%");
			}
		}
		if ("author".equals(condition)) {
			if (ids != null) {
				if (ids.size() > 0) {
					hql.append(" and s.startMemberId in (:author) ");
					params.put("author", ids);
				} else {
					return null;
				}
			}
		}
		if ("newDate".equals(condition)) {
			if (paramMap.get("newDate") != null && !"".equals(paramMap.get("newDate"))) {
				hql.append(" and (s.createDate>=:begin and s.createDate<=:end) ");
				params.put("begin", Datetimes.getTodayFirstTime((String) paramMap.get("newDate")));
				params.put("end", Datetimes.getTodayLastTime((String) paramMap.get("newDate")));
			}
		}

		if (phaseId != null && phaseId != 1) {
			hql.append("and s.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" + " where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.collaboration.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by a.createDate desc");
		List<Object[]> list = null;
		List<Affair> resultList = new ArrayList<Affair>();
		if (size > 0) {
			list = super.find(hql.toString(), 0, size, params);
		} else {
			list = super.find(hql.toString(), params);
		}
		for (Object[] o : list) {
			Affair a = (Affair) o[0];
			ColSummary s = (ColSummary) o[1];
			a.isHasAttachments();
			String forwardMember = a.getForwardMember();
			Integer resentTime = a.getResentTime();
			String subject = ColHelper.mergeSubjectWithForwardMembers(a.getSubject(), forwardMember, resentTime, orgManager, null);
			a.setCanForward(s.getCanForward());// 是否允许转发
			a.setAddition(subject); // 取协同名称的全部显示信息，放到additon中作为项目标题在前端显示
			resultList.add(a);
		}
		return resultList;
	}
    
    /*
     * 修改html正文
     * @see com.seeyon.v3x.collaboration.manager.ColManager#updateHtmlBody(java.lang.Long, java.lang.String)
     */
    public boolean updateHtmlBody(Long summaryId, String content, String contentType,Long currentNodeId) throws ColException
	{
    	User user = CurrentUser.get();
    	ColSummary summary = getSimpleColSummaryById(summaryId);
    	if("HTML".equals(contentType)){
    		Map<String, Object> columns = new HashMap<String, Object>();
    		columns.put("content", content);
    		columns.put("updateDate", new java.util.Date());
    		
    		super.update(ColBody.class, columns, new Object[][]{{"summaryId", summaryId}});
    	}
		
		//operationlogManager.insertOplog(summaryId, ApplicationCategoryEnum.collaboration, 
        //		Constant.OperationLogActionType.editBody.name(), "col.operationlog.editBody", user.getName(), new java.util.Date(), summary.getSubject());
		processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), currentNodeId, ProcessLogAction.processColl);
		ColMessageHelper.saveBodyMessage(affairManager, userMessageManager, orgManager, summary);
		return true;
	}
    
    /**
     * 解除表单并发锁定
     * @param summaryId
     * @return
     */
    public boolean removeFormLock(long summaryId) {
    	FormLockManager.remove(summaryId);
    	return true;
    }
    
    public FlowData preHasten(String memberIdStr){
    	User user = CurrentUser.get();
    	FlowData flowData = new FlowData();
    	String[] memberIds = memberIdStr.split("@");
    	
    	//倒排
    	String _memberId; 
    	int j = memberIds.length; 
    	for(int i=0 ; i<j; i++) { 
	    	if(j >= i) { 
	    		_memberId = memberIds[i]; 
		    	memberIds[i] = memberIds[--j]; 
		    	memberIds[j] = _memberId; 
	    	}else{ 
	    		break; 
	    	} 
    	}
    	
    	List<Party> people = new ArrayList<Party>();
    	for(String memberId : memberIds){
    		V3xOrgMember member = null;
    		try {
				member = orgManager.getMemberById(Long.parseLong(memberId));
				String partyId = member.getId().toString();
	            String partyType = member.getType().toString();
	            String partyName = member.getName();
	            String partyAccountId = member.getOrgAccountId().toString();
	            V3xOrgAccount account = orgManager.getAccountById(member.getOrgAccountId());
	            String partyAccountShortName = account.getShortname();
	            Party party = new Party(partyType, partyId, partyName, partyAccountId, partyAccountShortName);
	            people.add(party);
	            if(user.getAccountId() != member.getOrgAccountId() && "false".equals(flowData.getIsShowShortName())){
	            	flowData.setIsShowShortName("true");
	            }
			} catch (Exception e) {
				log.error("memberId = " + memberId, e);
			}
    	}
    	flowData.setPeople(people);
    	return flowData;
    }
    
    /**
     * 
     * 判断是否允许转发
     * @author jincm 2008-3-19
     * @param _summaryId
     * @param type 转发类型 transMail－邮件，transColl－协同
     * @return
     */
    public boolean hasForward(String _summaryId, String type) throws ColException{
    	Long summaryId = Long.parseLong(_summaryId);
    	boolean isForward = false;
    	ColSummary summary = null;
    	try {
			summary = getColSummaryById(summaryId, false);
			if(summary == null){
				summary = hisColManager.getColSummaryById(summaryId, false);
			}
		} catch (ColException e) {
			log.error("", e);
		}
		if(summary == null){
			return false;
		}else{
			isForward = summary.getCanForward();
		}
		
		if(isForward){
    		if(summary.getFormId() != null && "transMail".equals(type)){
    			isForward = false;
    		}
		}
    		
		return isForward;
    }
    
    public String checkModifyingProcess(String processId, Long summaryId) throws ColException{
    	return colCheckAndupdateLock(processId, summaryId, false);
    }
    
    public String getModifyingProcessXML(String processId) throws ColException {
    	String userId = CurrentUser.get().getId() + "";
        return ColHelper.getModifyingProcessXML(userId, processId);
    }
    
    private final Object CheckAndupdateLock = new Object();
    
    public void colDelLock(ColSummary summary) throws ColException {
    	if(summary != null){
    		colDelLock(summary.getProcessId(),summary.getId().toString());
    		//解锁 正文
    		String bodyType = summary.getBodyType();
    		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(bodyType)||
    				com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(bodyType)||
    				com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(bodyType)||
    				com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(bodyType)){
    			ColBody body = (ColBody) this.getHibernateTemplate().iterate("from "+ColBody.class.getName()+" as body where body.summaryId = ?", summary.getId())
                .next();
    			if(body != null){
    				String contentId = body.getContent();
    				handWriteManager.deleteUpdateObj(contentId);
    			}
    		}
    	}
    }
    
    
    public void colDelLock(String processId, String summaryId) throws ColException {
    	User user = CurrentUser.get(); 
    	if(user == null){
    		return;
    	}
    	String userId = String.valueOf(user.getId());
    	colDelLock(processId, summaryId, userId);
    }

	public void colDelLock(String processId, String summaryId, String userId) {
		synchronized (CheckAndupdateLock) {
			try {
				ColHelper.delModifyProcess(processId, summaryId, userId);
				logger.info("解锁时:CurrentUser["+userId+"];processId["+processId+"];summaryId["+summaryId+"]");
				List<ProcessLog> list = ColHelper.processLogMetaMap.get(Long.parseLong(processId));
		    	if(list != null){
		    		ColHelper.processLogMetaMap.remove(Long.parseLong(processId));
		    	}
		    	//解除表单锁
		    	FormLockManager.remove(Long.parseLong(summaryId),Long.parseLong(userId));
			}
			catch (Exception e) {
				log.error("", e);
			}
    	}
	}
    
    public String colCheckAndupdateLock(String processId, Long summaryId) throws ColException {
    	return colCheckAndupdateLock(processId, summaryId, true);
    }
    
    private String colCheckAndupdateLock(String processId, Long summaryId, boolean isLock) throws ColException {
    	Affair senderAffair = this.affairManager.getCollaborationSenderAffair(summaryId);
    	if(senderAffair == null || senderAffair.getState().intValue() == StateEnum.col_waitSend.key()){
    		return "--NoSuchSummary--";
    	}else if(senderAffair.getIsFinish()){
    		return null;//"--IsFinish--";
    	}
    	User user = CurrentUser.get(); 
    	String modifyUserName = null;
    	synchronized (CheckAndupdateLock) {
	    	modifyUserName = getModifyUserName(processId, summaryId);
	    	if(modifyUserName == null && isLock){
	    		//加锁
    			//ColHelper.updateProcessLock(processId, user.getId()+"");
    			ColHelper.updateModifyingProcessLock(Long.parseLong(processId), user.getId());
	    	}
	    	
    	}
    	logger.info("加锁时:LockUser["+modifyUserName+"];CurrentUser["+user.getLoginName()+","+user.getName()+"];processId["+processId+"];summaryId["+summaryId+"]");
    	return modifyUserName;
    }
    
    private String getModifyUserName(String processId, Long summaryId) throws ColException{
    	User user = CurrentUser.get(); 
    	String modifyUserName = null;
    	if(user == null){
    		return modifyUserName;
    	}
    	String userId = user.getId() + "";
		try {
			String modifyUserId = ColHelper.isModifyProcess(processId, userId, orgManager);
			if(modifyUserId != null && !"".equals(modifyUserId)){
				V3xOrgAccount account = orgManager.getRootAccount();
				V3xOrgMember member = orgManager.getMemberById(Long.parseLong(modifyUserId));
				if(member.getOrgAccountId().equals(account.getId())){
					modifyUserName = Constant.getCommonString("group.name");
				}else{
					modifyUserName = orgManager.getMemberById(Long.parseLong(modifyUserId)).getName();
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}    	
		return modifyUserName;
    }
    
    public String[] getNewProcessXML(String caseId) throws ColException {
    	User user = CurrentUser.get(); 
    	return ColHelper.getNewProcessXML(Long.parseLong(caseId), user.getId()+"");
    }
    
    public List<ColSummary> getSummaryIdByFormIdAndRecordId(Long formAppId, Long formId, Long formRecordId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ColSummary.class)
									.add(Expression.eq("formAppId", formAppId))
									//.add(Expression.eq("formId", formId))
									.add(Expression.eq("formRecordId", formRecordId));
		
		List l = super.executeCriteria(criteria, -1, -1);
		if(l.size() == 0)
			return null;
		else
			return l;
	}

    
    /**
     * 通过processId取到summary
     * @param processId
     * @return
     */
    public ColSummary getSummaryByProcessId(String processId) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(ColSummary.class);
    	criteria.add(Restrictions.eq("processId", processId));
    	 return (ColSummary)super.executeUniqueCriteria(criteria);
    }
    
    /**
	 * 综合查询
	 * @author jincm 2008-3-19
	 * @param cModel
     * @return List
	 */
    @SuppressWarnings("unchecked")
    public List<Affair> iSearch(ConditionModel cModel){
    	User user = CurrentUser.get();
    	Map<String, Object> parameterMap = new HashMap<String, Object>();
    	StringBuffer sb = new StringBuffer();
    	
    	String title = cModel.getTitle();
		final java.util.Date beginDate = cModel.getBeginDate();
		final java.util.Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		Long archiveId = cModel.getDocLibId(); ////归档ID
		List<Integer> stateList = new ArrayList<Integer>();
        sb.append("select affair from "+ Affair.class.getName() +" as affair");
        if(cModel.getPigeonholedFlag() && archiveId != null){
            //sb.append("," + ColSummary.class.getName() + " as col");
            sb.append("," + DocResource.class.getName() + " as doc");
        }
        parameterMap.put("APP", ApplicationCategoryEnum.collaboration.key());
		
        sb.append(" where ");
        boolean hasSenderId = false;
        if(fromUserId != null && !fromUserId.equals(user.getId())){
        	//指定认发给我的
        	sb.append(" affair.memberId=:userId2 ");
			parameterMap.put("userId2", user.getId());
            stateList.add(StateEnum.col_pending.key());
            stateList.add(StateEnum.col_done.key());
        	hasSenderId = true;
        }else if(fromUserId != null){
        	//我发送到
        	sb.append(" affair.memberId=:userId1 ");
        	parameterMap.put("userId1", fromUserId);
        	stateList.add(StateEnum.col_sent.key());
        }else{
        	//别人发给我的
        	sb.append(" affair.memberId=:userId3");
			parameterMap.put("userId3", user.getId());			
            stateList.add(StateEnum.col_pending.key());
            stateList.add(StateEnum.col_done.key());
        }
        sb.append(" and affair.state in(:stateList) and affair.app=:APP and affair.isDelete=false ");
/*      按照索引写sql语句
        if(fromUserId != null){
			sb.append(" and affair.senderId=:userId1");
			parameterMap.put("userId1", fromUserId);
			if(!fromUserId.equals(user.getId())){
				sb.append(" and affair.memberId=:userId2");
				parameterMap.put("userId2", user.getId());
                stateList.add(StateEnum.col_pending.key());
                stateList.add(StateEnum.col_done.key());
			}else{
                stateList.add(StateEnum.col_sent.key());
			}
		}
        else{
			sb.append(" and affair.memberId=:userId3");
			parameterMap.put("userId3", user.getId());			
            stateList.add(StateEnum.col_pending.key());
            stateList.add(StateEnum.col_done.key());
		}*/
		if(cModel.getPigeonholedFlag() && archiveId != null){
		    sb.append(" and doc.docLibId =:archiveId and doc.id=affair.archiveId");
		    parameterMap.put("archiveId", archiveId);
		}
		else{
		    sb.append(" and affair.archiveId is null");                    
		}
		if(hasSenderId){
            sb.append(" and affair.senderId=:userId1 ");
			parameterMap.put("userId1", fromUserId);
		}
		
        parameterMap.put("stateList", stateList);
		
		if(Strings.isNotBlank(title)){
			sb.append(" and affair.subject like :subject ");
			parameterMap.put("subject", "%" + title + "%");
		}
		if(beginDate != null){
			sb.append(" and affair.createDate >= :begin");
			parameterMap.put("begin", beginDate);
		}
		if(endDate != null){
			sb.append(" and affair.createDate <= :end");
			parameterMap.put("end", endDate);
		}
		sb.append(" order by affair.createDate desc");
		final String hsql = sb.toString();
		
		List<Affair> result = super.find(hsql, parameterMap);
		/*
        List<Affair> _result = null;
		Map<Long, Affair> affairMap = new HashMap<Long, Affair>();
		if(result != null && !result.isEmpty()){
			for(Affair affair : result){
				affairMap.put(affair.getObjectId(), affair);
			}
			_result = new ArrayList<Affair>();
			for(Long key : affairMap.keySet()){
				_result.add(affairMap.get(key));
			}
		}
		*/
    	return result;
    }
    
    public String[] getXML(String caseId, String processId){
    	String caseProcessXML = "";
    	String caseLogXML = "";
        String caseWorkItemLogXML = "";
        try{
	        if (caseId != null && !"".equals(caseId)) {
	            long _caseId = Long.parseLong(caseId);
	            caseProcessXML = ColHelper.getCaseProcessXML(processId);
	            
	            boolean isStoreFlag = false; //是否转储标记
	            if(caseProcessXML == null){
	            	caseProcessXML = ColHelper.getHisCaseProcessXML(processId);
	            	isStoreFlag = Strings.isNotBlank(caseProcessXML);
	            }
	            
	            if(isStoreFlag){
	            	caseLogXML = getHisCaseLogXML(_caseId);
		            caseWorkItemLogXML = getHisCaseWorkItemLogXML(_caseId);
	            }
	            else{
		            caseLogXML = getCaseLogXML(_caseId);
		            caseWorkItemLogXML = getCaseWorkItemLogXML(_caseId);
	            }
	        }
	        else if (processId != null && !"".equals(processId)) {
	            String _processId = processId;
	            caseProcessXML = getProcessXML(_processId);
	        }
        }catch(Exception e){
        	log.error("异步获取流程信息错误！", e);
        }
        
    	return new String[]{caseProcessXML, caseLogXML, caseWorkItemLogXML};
    }
    
    /**
     * 催办通过ajax初始化催办人员
     * @param memberIds
     * @param currentUserId
     * @return String 0--成功, 1--失败
     */
    public String initMemberIds(String memberIds, String currentUserId){
    	if(ColHelper.hastenMemberIdsMap != null){
    		ColHelper.hastenMemberIdsMap.put(Long.parseLong(currentUserId), memberIds);
    	}else{
    		return "1";
    	}
    	return "0";
    }
    
    /**
     * 匹配人员通过ajax初始化匹配人员信息
     * @param ids
     * @param names
     */
    public void initMatchPeople(String ids, String nodeId){
    	Long _nodeId = Long.parseLong(nodeId.substring(21));
    	ColHelper.matchPeople.put(_nodeId, ids);
    }
    
    public List<Object[]> statByGroup(int appType, List<Long> entityId,String entityType,Date beginDate,Date endDate){
    	if(entityId==null || entityId.size()==0)
    		return null;
    	
    	List<Object[]> result = new ArrayList<Object[]>();
    	Map<Long,Integer> rowMap = new HashMap<Long,Integer>();
    	if(!V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType) && !V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType)) {
    		int i = 0;
	    	for(Long entity:entityId) {
	    		Object[] arr = new Object[7];
	    		arr[1] = entity;
	    		result.add(arr);
	    		rowMap.put(entity, i);
	    		i++;
	    	}
    	}
    	List<Integer> states = new ArrayList<Integer>();
    	states.add(StateEnum.col_sent.getKey());
    	states.add(StateEnum.col_pending.getKey());
    	states.add(StateEnum.col_done.getKey());
    	Map<String,Object> map = new HashMap<String,Object>();
        List<Integer> apps = new ArrayList<Integer>();
        
        if(EdocUtil.isEdocCheckByAppKey(appType)){
        	apps.addAll(EdocUtil.getAllEdocApplicationCategoryEnumKey());
        }if(appType==ApplicationCategoryEnum.form.getKey()
        		||appType==ApplicationCategoryEnum.collaboration.getKey() ){
        	//appType = ApplicationCategoryEnum.collaboration.getKey();
        	apps.add(ApplicationCategoryEnum.collaboration.getKey());
        	
        }
      //branches_a8_v350sp1_r_gov GOV-4843 魏俊标 组织管理员，工作管理-流程统计，点击信息报送统计结果里的数字链接出现红三角页面. start
        if(appType==ApplicationCategoryEnum.info.getKey()){
        	apps.add(ApplicationCategoryEnum.info.getKey());
        }
      //branches_a8_v350sp1_r_gov GOV-4843 魏俊标 组织管理员，工作管理-流程统计，点击信息报送统计结果里的数字链接出现红三角页面. end
        map.put("app",apps );
    	map.put("state", states);
    	
    	List<Object> indexParameter = null;
    	String hql = "";
    	boolean hasCommon = false;
    	hql = this.getGroupStatHql(appType, entityId, entityType, beginDate, endDate, map);
    	List<Object[]> returnValue = super.find(hql,-1,-1, map,indexParameter);
    	int position = -1;
		Object[] row = null;
		int total1 = 0;        //已发
		int total2 = 0;        //已办
		int total3 = 0;        //待办
		int total4 = 0;        //超期个数
    	if(returnValue != null) {
    		int state = -1;
    		for(Object[] obj:returnValue) {
    			Long key = (Long)(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)?obj[3]:obj[0]);
    			if(rowMap.get(key) == null) {
    				row = new Object[7];
    				if(!V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType))
    					row[0] = key;
    	    		else
    	    			row[1] = key;
    	    		result.add(row);
    	    		rowMap.put(key, rowMap.size());
    			}else {
	    			position = rowMap.get(key);
	    			row = result.get(position);
    			}
    			row[0] = obj[0];
    			if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType))
    				row[1] = obj[3];
    			state = ((Integer)obj[2]).intValue();
    			switch (state){
    				case 2:
    					row[2] = obj[1];
    					if(obj[1] != null) 
    						total1 = total1 + ((Integer)obj[1]).intValue();
    					break;
    				case 3:
    					row[4] = obj[1];
    					if(obj[1] != null) 
    						total3 = total3 + ((Integer)obj[1]).intValue();
    					break;
    				case 4:
    					row[3] = obj[1];
    					if(obj[1] != null) 
    						total2 = total2 + ((Integer)obj[1]).intValue();
    					break;
    			}
    		}
    	}
    	
    	//超期时间
    	states.remove(0);
    	map.remove("bodyType");
    	hql = this.getGroupDeadlineHql(appType, entityId, entityType, beginDate, endDate,map);
    	returnValue = super.find(hql,-1,-1, map, indexParameter);
    	if(returnValue != null) {
    		int[] countArr = new int[result.size()];
    		long[] handleArr = new long[result.size()] ;
    		Date receiveTime = null;
    		Date completeTime = null;
    		long deadlineDate = 0;
    		long overWorkTime = 0;
    		Long orgAccountId = 0l;
    		for(Object[] obj:returnValue) {
    			receiveTime = (Date)obj[1];
    			completeTime = (Date)obj[2];
    			if(completeTime == null)
    				completeTime = new Date();
    			deadlineDate = ((Long)obj[3]).longValue()*60000;
    			//超期时间
    			if(obj[4]!=null){
    				overWorkTime = ((Number)obj[4]).longValue();
    			}
    			
    			if(obj[5]!=null){
    				orgAccountId = ((Number)obj[5]).longValue();
    			}
    			
    			//设置了流程期限并且系统中没有已经计算好的超期时间
    			if(obj[4] == null && deadlineDate>0){ 
    				Long runWorkTime = Functions.getMinutesBetweenDatesByWorkTime(receiveTime,completeTime,orgAccountId);
    				Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(deadlineDate),orgAccountId);
    				Long overWork = runWorkTime-workStandarDuration;
    				if(overWork>0){
    					overWorkTime= overWork;
    				}
    			}
    			if(overWorkTime > 0) {
    				if(rowMap.get(obj[0])==null)
    					continue;
	    			position = rowMap.get(obj[0]);
	    			countArr[position] = countArr[position] + 1;
	    			handleArr[position] = handleArr[position] + overWorkTime;
    			}
    		}
    		for(int j=0;j<result.size();j++) {
    			Object[] obj = result.get(j);
    			obj[5] = new Integer(countArr[j]);
    			total4 += countArr[j];
    			if(handleArr[j]!=0) {
    				obj[6] = Functions.showDateByWork(((Number)handleArr[j]).intValue());
    			}
    		}
    	}
    	
    	//汇总
    	Object[] total = new Object[4];
    	total[0] = total1;
    	total[1] = total2;
    	total[2] = total3;
    	total[3] = total4;
    	result.add(total);
    	return result;
    }
    
    public List<Object[]> statByAccount(int appType,List<Long> templateId, List<Long> entityId,String entityType,Date beginDate,Date endDate){
    	if(entityId==null || entityId.size()==0)
    		return null;
    	
    	//自建流程
    	boolean onlySelfFlow = templateId.size()==1 && templateId.get(0)==-1;
    	
    	List<Object[]> result = new ArrayList<Object[]>();
    	Map<String,Integer> rowMap = new HashMap<String,Integer>();
    	if(!V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)) {
	    	int i = 0;
	    	String key = "";
	    	for(Long template:templateId) {
		    	for(Long entity:entityId) {
		    		if(template==-1)
		    			continue;
		    		Object[] arr = new Object[10];
		    		if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType))
		    			arr[2] = entity;
		    		else
		    			arr[3] = entity;
		    		result.add(arr);
		    		
		    		key = template+"_"+entity;
		    		rowMap.put(key, i);
		    		i++;
		    	}
	    	}
    	}
    	Map<String,Object> map = new HashMap<String,Object>();
    	List<Integer> states = new ArrayList<Integer>();
    	states.add(StateEnum.col_sent.getKey());
    	states.add(StateEnum.col_pending.getKey());
    	states.add(StateEnum.col_done.getKey());
    	
    	map.put("state", states);
    	List<Integer> apps = new ArrayList<Integer>();
        //branches_a8_v350sp1_r_gov GOV-4842魏俊标--【流程分析】-【流程统计】，应用类型选择'信息报送'时统计的结果是'公文'.start
        if(EdocUtil.isEdocCheckByAppKey(appType)&&appType!=ApplicationCategoryEnum.info.getKey()){//branches_a8_v350sp1_r_gov GOV-4842魏俊标--【流程分析】-【流程统计】，应用类型选择'信息报送'时统计的结果是'公文'.end
        	apps.addAll(EdocUtil.getAllEdocApplicationCategoryEnumKey());
        }if(appType==ApplicationCategoryEnum.form.getKey()
        		||appType==ApplicationCategoryEnum.collaboration.getKey() ){
        	//appType = ApplicationCategoryEnum.collaboration.getKey();
        	apps.add(ApplicationCategoryEnum.collaboration.getKey());
        	
        }//branches_a8_v350sp1_r_gov GOV-4842魏俊标--【流程分析】-【流程统计】，应用类型选择'信息报送'时统计的结果是'公文'.start
        if(appType==ApplicationCategoryEnum.info.getKey()){
        	apps.add(ApplicationCategoryEnum.info.getKey());
        }
    	//branches_a8_v350sp1_r_gov GOV-4842魏俊标--【流程分析】-【流程统计】，应用类型选择'信息报送'时统计的结果是'公文'.start
        map.put("app",apps );
    	List<Object> indexParameter = null;
    	String hql = this.getAccountStatHql(appType,templateId,onlySelfFlow, entityId, entityType, beginDate, endDate, map);
    	List<Object[]> returnValue = super.find(hql,-1,-1, map,indexParameter);
    	
    	int position = -1;
		Object[] row = null;
		int total1 = 0;        //已发
		int total2 = 0;        //已办
		int total3 = 0;        //待办
		int total4 = 0;        //超期个数
		String key = "";
		String appName = "";
		//int edocPosition = 0;
    	if(returnValue != null) {
    		//edocPosition = returnValue.size();
    		//returnValue.addAll(retuanValue1);
    		int state = -1;
    		for(int i=0;i<returnValue.size();i++) {
    			Object[] obj = returnValue.get(i);
    			if(obj[0]==null) {
    				/*if(i>=edocPosition) {
    					key = "-1";
    					appName = ApplicationCategoryEnum.edoc.name();
    				}
        			else {*/
        				key = "-2";
        				appName = ApplicationCategoryEnum.collaboration.name();
        			//}
    			}else
    				key = obj[0].toString();
    			key += "_" + (V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)?obj[4]:obj[1]);
    			if(rowMap.get(key) == null) {
    				row = new Object[10];
    				if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType) || V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType))
    					row[2] = obj[1];
    	    		else
    	    			row[3] = obj[4];
    	    		result.add(row);
    	    		rowMap.put(key, rowMap.size());
    	    		position = result.size()-1;
    			}else {
	    			position = rowMap.get(key);
	    			row = result.get(position);
    			}
    			row[9] = appName;
    			row[0] = obj[0];
    			row[2] = obj[1];
    			
    			if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    				row[3] = obj[4];
    				if(!onlySelfFlow)
        				row[1] = obj[5];
    			}else if(!onlySelfFlow)
    				row[1] = obj[4];
    			state = ((Integer)obj[3]).intValue();
    			switch (state){
    				case 2:
    					row[4] = obj[2];
    					if(obj[2] != null) 
    						total1 = total1 + ((Integer)obj[2]).intValue();
    					break;
    				case 3:
    					row[6] = obj[2];
    					if(obj[2] != null) 
    						total3 = total3 + ((Integer)obj[2]).intValue();
    					break;
    				case 4:
    					row[5] = obj[2];
    					if(obj[2] != null) 
    						total2 = total2 + ((Integer)obj[2]).intValue();
    					break;
    			}
    		}
    	}
    	
//    	超期时间
    	states.remove(0);
    	map.remove("bodyType");
    	hql = this.getAccountDeadlineHql(appType,templateId, onlySelfFlow, entityId, entityType, beginDate, endDate,map);
    	returnValue = super.find(hql,-1,-1, map, indexParameter);
    	if(returnValue != null) {
    		int[] countArr = new int[result.size()];
    		long[] handleArr = new long[result.size()] ;
    		
    		for(int i=0;i<returnValue.size();i++) {
    			long overWorkTime = 0l;
    			Long orgAccountId = 0l;
    			Date receiveTime = null;
        		Date completeTime = null;
        		long deadlineDate = 0;
        		key = "";
    			Object[] obj = returnValue.get(i);
    			if(obj[0]==null) {
        			key = "-2";
    			}else
    				key = obj[0].toString();
    			key += "_" + obj[1];
    			receiveTime = (Date)obj[2];
    			completeTime = (Date)obj[3];
    			if(completeTime == null)
    				completeTime = new Date();
    			
    			deadlineDate = ((Long)obj[4]).longValue();
    			
    			//超期时间
    			if(obj[5]!=null){
    				overWorkTime = ((Number)obj[5]).longValue();
    			}
    			
    			if(obj[6]!=null){
    				orgAccountId = ((Number)obj[6]).longValue();
    			}
    			
    			//设置了流程期限并且系统中没有已经计算好的超期时间
    			if(obj[5] == null && deadlineDate>0){ 
    				Long runWorkTime = Functions.getMinutesBetweenDatesByWorkTime(receiveTime,completeTime,orgAccountId);
    				Long workStandarDuration = workTimeManager.convert2WorkTime(Long.valueOf(deadlineDate),orgAccountId);
    				Long overWork = runWorkTime-workStandarDuration;
    				if(overWork>0){
    					overWorkTime= overWork;
    				}
    			}
    			
    			if(overWorkTime > 0) {
    				if(rowMap.get(key)==null)
    					continue;
	    			position = rowMap.get(key);
	    			countArr[position] = countArr[position] + 1;
	    			handleArr[position] = handleArr[position] + overWorkTime;
    			}
    		}
    		
    		
    		int j = 0;
    		int kk = 0;
    		while(j<result.size()) {
    			Object[] obj = result.get(j);
    			kk++;
    			if(obj[4] == null && obj[5] == null && obj[6] == null) {
    				result.remove(j);
    				continue;
    			}
    			obj[7] = new Integer(countArr[kk-1]);
    			obj[8] = Functions.showDateByWork(((Long)handleArr[kk-1]).intValue());
    			total4 += countArr[kk-1];
    			j++;
    		}
    	}
    	
//    	汇总
    	Object[] total = new Object[4];
    	total[0] = total1;
    	total[1] = total2;
    	total[2] = total3;
    	total[3] = total4;
    	result.add(total);
    	return result;
    }
    
    private String getGroupStatHql(int appType,List<Long> entityId,String entityType,Date beginDate,Date endDate,Map<String,Object> namedParameterMap) {
    	StringBuffer hql = new StringBuffer();
    	if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)){
    		hql.append("select max(m.orgAccountId),count(*),a.state");
    	}else{
    		hql.append("select max(m.orgDepartmentId),count(*),a.state");
    	}
    	String from = " from Affair as a,V3xOrgMember as m";
    	//String from1 = ",ColSummary as c";
    	String app = " and a.app in(:app )";
    	/*boolean isEdoc = appType==ApplicationCategoryEnum.edocSend.key()||appType==ApplicationCategoryEnum.edocRec.key()||appType==ApplicationCategoryEnum.edocSign.key();
    	if(isEdoc) {
    		from1 = ",EdocSummary as c";
    	}*/
    	String where = " where a.memberId=m.id" + app + " and a.state in (:state)";
    	if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    		hql.append(",a.memberId");
    	}else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)){
    		from+=",V3xOrgAccount as acc";
    		where+=" and m.orgAccountId=acc.id";
    	}
    	hql.append(from);
    	hql.append(where);
    	String groupBy = " group by";
    	if(appType==ApplicationCategoryEnum.collaboration.getKey()) {
    		hql.append(" and a.bodyType!=:bodyType");
    		namedParameterMap.put("bodyType", "FORM");
    	}
    	else if(appType==ApplicationCategoryEnum.form.getKey()) {
    		hql.append(" and a.bodyType=:bodyType");
    		namedParameterMap.put("bodyType", "FORM");
    	}
    	
    	if(entityId != null) {
    		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    			hql.append(" and a.memberId in (:memberIds)");
    			groupBy += " a.memberId,a.state";
    			namedParameterMap.put("memberIds", entityId);
    		}else{
    			if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)) {
    				hql.append(" and m.orgAccountId in(:orgAccountId)");
    				groupBy += " m.orgAccountId";
    				//groupBy += ",m.orgDepartmentId";
    				namedParameterMap.put("orgAccountId", entityId);
    				groupBy += ",acc.sortId,a.state";
    			}
    			
    			//groupBy += " m.orgAccountId";
    			if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType)){
    				hql.append(" and m.orgDepartmentId in(:orgAccountId)");
    				groupBy += " m.orgAccountId";
    				groupBy += ",m.orgDepartmentId";
    				namedParameterMap.put("orgAccountId", entityId);
    				groupBy += ",a.state";
    			}
    			
    			
    		}
    	}
    	if(beginDate != null) {
    		/*if(isEdoc)
    			hql.append(" and c.createTime>=:beginDate");
    		else*/
    			hql.append(" and a.createDate>=:beginDate");
    		namedParameterMap.put("beginDate", beginDate);
    	}
    	if(endDate != null) {
    		/*if(isEdoc)
    			hql.append(" and c.createTime<=:endDate");
    		else*/
    			hql.append(" and a.createDate<=:endDate");
    		namedParameterMap.put("endDate", endDate);
    	}
    	hql.append(groupBy);
    	if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)){
    		hql.append(" order by acc.sortId");
    	}else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType)){
    		hql.append(" order by m.orgAccountId");
    	}
    	return hql.toString();
    }
    
    private String getGroupDeadlineHql(int appType,
    		List<Long> entityId,
    		String entityType,
    		Date beginDate,
    		Date endDate,
    		Map<String,Object> namedParameterMap) {
    	boolean isEdoc = EdocUtil.isEdocCheckByAppKey(appType);
    	StringBuffer hql = new StringBuffer();
    	String from = " from Affair as a,V3xOrgMember as m";
    	String app = " a.app in (:app)";
    	/*String summary = ",ColSummary as c";
    	if(isEdoc) {
    		summary = ",EdocSummary as c";
    	}*/
    	if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    		hql.append("select a.memberId,a.receiveTime,a.completeTime,a.deadlineDate,a.overWorkTime ,c.orgAccountId  from Affair as a");
    	}else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)){
    		hql.append("select m.orgAccountId,a.receiveTime,a.completeTime,a.deadlineDate,a.overWorkTime ,c.orgAccountId "+from);
    	}else{
    		hql.append("select m.orgDepartmentId,a.receiveTime,a.completeTime,a.deadlineDate,a.overWorkTime ,c.orgAccountId "+from);
    	}
    	hql.append(","+(isEdoc?"EdocSummary":"ColSummary")+" as c ");
    	
    	String where = " where a.memberId=m.id and" + app + " and a.state in (:state)";
    	if(entityId != null) {
    		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    			hql.append(" where" + app + " and a.state in (:state) and a.memberId in (:memberIds)");
    		}else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)){
    			hql.append(where +  " and m.orgAccountId in(:orgAccountId)");
    		}else{
    			hql.append(where +  " and m.orgDepartmentId in(:orgAccountId)");
    		}
    	}
    	hql.append(" and c.id = a.objectId ");
    	if(appType==ApplicationCategoryEnum.collaboration.getKey()) {
    		hql.append(" and a.bodyType!=:bodyType");
    		namedParameterMap.put("bodyType", "FORM");
    	}
    	else if(appType==ApplicationCategoryEnum.form.getKey()) {
    		hql.append(" and a.bodyType=:bodyType");
    		namedParameterMap.put("bodyType", "FORM");
    	}
    	if(beginDate != null) {
    		/*if(isEdoc)
    			hql.append(" and a.createTime>=:beginDate");
    		else*/
    			hql.append(" and a.createDate>=:beginDate");
    	}
    	if(endDate != null) {
    		/*if(isEdoc)
    			hql.append(" and a.createTime<=:endDate");
    		else*/
    			hql.append(" and a.createDate<=:endDate");
    	}
    	hql.append(" and a.deadlineDate>0 and a.isOvertopTime = :isOvertopTime ");
    	namedParameterMap.put("isOvertopTime", Boolean.TRUE);
    	return hql.toString();
    }
    
    private String getAccountStatHql(int appType,List<Long> templateId,boolean onlySelfFlow,List<Long> entityId,String entityType,Date beginDate,Date endDate,Map<String,Object> namedParameterMap) {
    	StringBuffer hql = new StringBuffer();
    	String app = " and a.app in( :app )";
    	boolean isEdoc = EdocUtil.isEdocCheckByAppKey(appType);
    	hql.append("select c.templeteId ,max(m.orgDepartmentId),count(*),a.state");
    	if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    		hql.append(",a.memberId");
    	}
    	if(!onlySelfFlow)
    		hql.append(",max(t.subject)");
    	//branches_a8_v350sp1_r_gov GOV-4842魏俊标--【流程分析】-【流程统计】，应用类型选择'信息报送'时统计的结果是'公文'.start
    	if(appType == ApplicationCategoryEnum.info.key()){
    		hql.append(" from Affair as a,V3xOrgMember as m,InfoSummary as c");
    	}else{
    		hql.append(" from Affair as a,V3xOrgMember as m,"+(isEdoc?"EdocSummary":"ColSummary")+" as c");
    	}
    	//branches_a8_v350sp1_r_gov GOV-4842魏俊标--【流程分析】-【流程统计】，应用类型选择'信息报送'时统计的结果是'公文'.start
    	if(!onlySelfFlow)
    		hql.append(",Templete as t");
    	hql.append(" where a.objectId=c.id and a.memberId=m.id"+ app + " and a.state in (:state)");
    	if(!onlySelfFlow){
    		hql.append(" and t.id = c.templeteId ");
    	}
    	hql.append(" and (");
    	if(Strings.isNotEmpty(templateId)&& WorkFlowAnalysis.AllTemplete.equals(templateId.get(0))){
    		//全部模板
    		hql.append(" c.templeteId is not null ");
    	}else if(!onlySelfFlow && !WorkFlowAnalysis.AllTemplete.equals(templateId.get(0))  ) {
    		//选择具体模板
    		hql.append("(c.templeteId in (:templateId) and c.templeteId=t.id)");
    		namedParameterMap.put("templateId", templateId);
    	}else if(onlySelfFlow && Long.valueOf(-1L).equals(templateId.get(0))){
    		//自由协同
    		hql.append(" c.templeteId is null");
    	}
    	hql.append(")");
    	
    	if(appType == ApplicationCategoryEnum.form.key()){
    		hql.append(" and a.bodyType = :bodyType ");
    		namedParameterMap.put("bodyType", "FORM");
    	}else if(appType == ApplicationCategoryEnum.collaboration.key()){
    		hql.append(" and a.bodyType != :bodyType ");
    		namedParameterMap.put("bodyType", "FORM");
    	}
    	
    	String groupBy = " group by c.templeteId";  
    	StringBuilder orderBy = new StringBuilder();
    	orderBy.append(" order by c.templeteId ");
    	if(entityId != null) {
    		if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)) {
    			hql.append(" and m.orgAccountId=:orgAccountId");
    			groupBy += ",m.orgDepartmentId,a.state";
    			orderBy.append(",m.orgDepartmentId ");
    			namedParameterMap.put("orgAccountId", entityId.get(0));
    		}else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType)) {
    			hql.append(" and m.orgDepartmentId in (:departmentIds)");
    			groupBy += ",m.orgDepartmentId,a.state";
    			orderBy.append(",m.orgDepartmentId ");
    			namedParameterMap.put("departmentIds", entityId);
    		}else if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    			hql.append(" and a.memberId in (:memberIds)");
    			groupBy += ",a.memberId,a.state";
    			orderBy.append(",a.memberId ");
    			namedParameterMap.put("memberIds", entityId);
    		}
    	}
    	
    	if(beginDate != null) {
    		if(isEdoc || appType == ApplicationCategoryEnum.info.key())
    			hql.append(" and c.createTime>=:beginDate");
    		else
    			hql.append(" and c.createDate>=:beginDate");
    		namedParameterMap.put("beginDate", beginDate);
    	}
    	if(endDate != null) {
    		if(isEdoc || appType == ApplicationCategoryEnum.info.key())
    			hql.append(" and c.createTime<=:endDate");
    		else
    			hql.append(" and c.createDate<=:endDate");
    		namedParameterMap.put("endDate", endDate);
    	}
    	hql.append(groupBy);
    	hql.append(orderBy);
    	return hql.toString();
    }
    
    private String getAccountDeadlineHql(int appType,List<Long> templateId,boolean onlySelfFlow,List<Long> entityId,String entityType,Date beginDate,Date endDate,Map<String,Object> namedParameterMap) {
    	StringBuffer hql = new StringBuffer();
    	String app = " and a.app in (:app)";
    	boolean isEdoc = EdocUtil.isEdocCheckByAppKey(appType);
    	String from = " from Affair as a,"+(isEdoc?"EdocSummary":"ColSummary")+" as c,V3xOrgMember as m";
    	if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    		hql.append("select c.templeteId,a.memberId,a.receiveTime,a.completeTime,a.deadlineDate,a.overWorkTime ,c.orgAccountId from Affair as a,"+(isEdoc?"EdocSummary":"ColSummary") +" as c");
    	}else
    		hql.append("select c.templeteId,m.orgDepartmentId,a.receiveTime,a.completeTime,a.deadlineDate,a.overWorkTime,c.orgAccountId "+from);
    	String where = " where a.memberId=m.id and c.id=a.objectId"+app+" and a.state in (:state)";
    	if(entityId != null) {
    		if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)) {
    			hql.append(where +  " and m.orgAccountId=:orgAccountId");
    		}else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType)) {
    			hql.append(where +  " and m.orgDepartmentId in (:departmentIds)");
    		}else if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(entityType)) {
    			hql.append(" where c.id=a.objectId" + app + " and a.state in (:state) and a.memberId in (:memberIds)");
    		}
    	}
    	hql.append(" and (");

    	if(Strings.isNotEmpty(templateId)&& WorkFlowAnalysis.AllTemplete.equals(templateId.get(0))){
    		//全部模板
    		hql.append(" c.templeteId is not null ");
    	}else if(!onlySelfFlow && !WorkFlowAnalysis.AllTemplete.equals(templateId.get(0))  ) {
    		//选择具体模板
    		hql.append(" c.templeteId in (:templateId)");
    		if(namedParameterMap.get("templateId")== null){
    			namedParameterMap.put("templateId", templateId);
    		}
    	}else if(onlySelfFlow && Long.valueOf(-1L).equals(templateId.get(0))){
    		//自由协同
    		hql.append(" c.templeteId is null");
    	}
    	hql.append(")");
    	
    	if(appType == ApplicationCategoryEnum.form.key()){
    		hql.append(" and a.bodyType = :bodyType ");
    		namedParameterMap.put("bodyType", "FORM");
    	}else if(appType == ApplicationCategoryEnum.collaboration.key()){
    		hql.append(" and a.bodyType != :bodyType ");
    		namedParameterMap.put("bodyType", "FORM");
    	}
    	
    	if(beginDate != null) {
    		if(isEdoc)
    			hql.append(" and c.createTime>=:beginDate");
    		else
    			hql.append(" and c.createDate>=:beginDate");
    	}
    	if(endDate != null) {
    		if(isEdoc)
    			hql.append(" and c.createTime<=:endDate");
    		else
    			hql.append(" and c.createDate<=:endDate");
    	}
    	hql.append(" and a.deadlineDate>0 and a.isOvertopTime = :isOvertopTime ");
    	namedParameterMap.put("isOvertopTime", Boolean.TRUE);
    	return hql.toString();
    }
    
    public List<Object[]> statList(int appType,long entityId,String entityType,int state,Date beginDate,Date endDate,Long templateId,String appName,String statScope, boolean isPage){
    	boolean isEdoc = EdocUtil.isEdocCheckByAppKey(appType);
    	String startMemberId = "c.startMemberId";
    	String createDate = "c.createDate";
    	String finishDate = "c.finishDate";
    	if(isEdoc || appType == ApplicationCategoryEnum.info.key()) {
    		startMemberId = "c.startUserId";
    		createDate = "c.createTime";
    		finishDate = "c.completeTime";
    	}
    	StringBuffer hql = new StringBuffer();
    	Map<String,Object> map = new HashMap<String,Object>();
    	hql.append("select  c.id,"+startMemberId+",c.subject,"+createDate+","+finishDate+" from");
    	hql.append(" Affair as a, ");
    	if(isEdoc)
    		hql.append(" EdocSummary");//branches_a8_v350sp1_r_gov GOV-4843.组织管理员，工作管理-流程统计，点击信息报送统计结果里的数字链接出现红三角页面.start
    	else if(appType == ApplicationCategoryEnum.info.key()){
    		hql.append(" InfoSummary");//branches_a8_v350sp1_r_gov GOV-4843.组织管理员，工作管理-流程统计，点击信息报送统计结果里的数字链接出现红三角页面end
    	}else{
    		hql.append(" ColSummary");
    	}
    	hql.append(" as c ");
    	String where = "";
    	if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(entityType)) {
    		hql.append(",V3xOrgMember as m");
    		where = " and a.memberId=m.id and m.orgDepartmentId=:entityId";
    	}else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(entityType)){
    		hql.append(",V3xOrgMember as m");
    		where = " and a.memberId=m.id and m.orgAccountId=:entityId";
    	}else{
    		where = " and a.memberId=:entityId";
    	}
    	
    	hql.append(" where c.id=a.objectId");
    	if (state==-1){
    		hql.append(" and a.isOvertopTime=:isOvertopTime1 and  a.deadlineDate>0 ");
    		hql.append(" and a.state in(:state)");
    		List<Integer> stateList = new ArrayList<Integer>();
    		stateList.add(StateEnum.col_sent.key());
    		stateList.add(StateEnum.col_pending.key());
    		stateList.add(StateEnum.col_done.key());
    		map.put("isOvertopTime1", true);
    		map.put("state", stateList);
    	} else {
    		hql.append(" and a.state=:state");
    		map.put("state", state);
    	}
    	
    	hql.append(where);
    	hql.append(" and a.app in(:app)");
    	if (appType == ApplicationCategoryEnum.edoc.key()) {
    		List<Integer> appList = EdocUtil.getAllEdocApplicationCategoryEnumKey();
        	map.put("app", appList);////branches_a8_v350sp1_r_gov GOV-4843 魏俊标 组织管理员，工作管理-流程统计，点击信息报送统计结果里的数字链接出现红三角页面 start
    	}else if(appType == ApplicationCategoryEnum.info.key()){
    		List<Integer> appList = EdocUtil.getAllEdocApplicationCategoryEnumKey();
        	map.put("app", appList);////branches_a8_v350sp1_r_gov GOV-4843 魏俊标 组织管理员，工作管理-流程统计，点击信息报送统计结果里的数字链接出现红三角页面 end
    	} else {
    		List<Integer> appList = new ArrayList<Integer>();
    		if(appType  ==  ApplicationCategoryEnum.form.key() ){
    			appList.add(ApplicationCategoryEnum.collaboration.key());
    			hql.append(" and a.bodyType = :bodyType");
    		}else if(appType  ==  ApplicationCategoryEnum.collaboration.key() ){
    			appList.add(ApplicationCategoryEnum.collaboration.key());
    			hql.append(" and a.bodyType != :bodyType");
    		}
 			map.put("bodyType", "FORM");
    		map.put("app", appList);
    	}
    	
    	map.put("entityId", entityId);
    	if(!"group".equals(statScope)) {
    		hql.append(" and c.templeteId");
    		if(templateId==null)
    			hql.append(" is null");
    		else {
    			hql.append("=:templateId");
    			map.put("templateId", templateId);
    		}
    	}
    	if(beginDate != null && endDate != null) {
    		if(isEdoc || appType == ApplicationCategoryEnum.info.key())
    			hql.append(" and c.createTime between :beginDate");
    		else
    			hql.append(" and a.createDate between :beginDate");
    		map.put("beginDate", beginDate);
    		
    		hql.append(" and :endDate");
        	map.put("endDate",endDate);
    	}
    	
    	hql.append(" order by "+createDate+" desc ");
    	
    	List<Object> indexParameter = new ArrayList<Object>();
    	if(isPage){
    		return super.find(hql.toString(), map, indexParameter);
    	}
    	else{
    		return super.find(hql.toString(), -1, -1, map, indexParameter);
    	}
    }
    
    public void saveModifyWorkflowData(String processId, String userId, String summaryId, String appName) throws ColException{
    	V3xOrgMember member = null;
    	V3xOrgAccount account = null;
    	boolean isColl = "collaboration".equals(appName) || "form".equals(appName);
    	Affair affair = null;
    	ColSummary summary = null;
    	Long memberId = Long.parseLong(userId);
    	boolean isNeedSendMsg = false; //是否需要发消息，发起人自己修改不给自己发消息
    	try {
    		Long theSummaryId = Long.parseLong(summaryId);
			member = orgManager.getMemberById(memberId);
			if(member == null){
				return ;
			}
			if(isColl){
				summary = getColSummaryById(theSummaryId, false);
				if(summary != null){
					affair = affairManager.getCollaborationSenderAffair(summary.getId());
				}
				if(affair != null && !affair.getMemberId().equals(memberId)){ //非已发的
					//前端用户校验督办权限
					if(!orgManager.isAdministrator(member.getLoginName()) && !SecurityCheck.isSupervisor(Long.parseLong(userId), theSummaryId)){
						return;
					}
					isNeedSendMsg = true;
				}
			}
			account = orgManager.getAccountById(member.getOrgAccountId());
			String memname = member.getName();
			if(account.getIsRoot())
				//memname = "集团管理员";
			    memname = Constantform.getString4CurrentUser("col.group.label");
	    	//持久化修改后的流程
	    	if(processId != null && !"".equals(processId)){
	    		BPMProcess process = ColHelper.saveModifyingProcess(processId, userId);
	    		if(process != null){
	    			//如果该流程实例存在待添加或待删除的节点，将其激活或撤销
	    			ColHelper.saveAcitivityModify(process, userId);
	    		}
	    	}
	    	List<ProcessLog> list = ColHelper.processLogMetaMap.get(Long.parseLong(processId));
	    	if(list != null){
	    		for(ProcessLog pLog :list){
	    			processLogManager.insertLog(pLog);
	    		}
	    		ColHelper.processLogMetaMap.remove(Long.parseLong(processId));
	    	}
	    	String subject = StringUtils.EMPTY;
	    	if(isColl){
	    		if(summary == null){
	    			summary = getColSummaryById(theSummaryId, false);	    			
	    		}
	    		subject = summary.getSubject();
	    		if(isNeedSendMsg){
	    			Integer importantLevel = summary.getImportantLevel();
	    			if(summary != null && affair == null){
	    				affair = affairManager.getCollaborationSenderAffair(summary.getId());
	    			}
	    			MessageReceiver receiver = null;
	    			String forwardMember = null;
	    			int forwardMemberFlag = 0;
	    			if(affair != null){
	    				receiver = new MessageReceiver(summary.getId(), Long.valueOf(summary.getStartMemberId()),"message.link.col.done",affair.getId());
	    				String forwardMemberId = affair.getForwardMember();
	    				if(Strings.isNotBlank(forwardMemberId)){
	    					try {
	    						forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
	    						forwardMemberFlag = 1;
	    					}
	    					catch (Exception e) {
	    					}
	    				}
	    			}else{
	    				receiver = new MessageReceiver(summary.getId(), Long.valueOf(summary.getStartMemberId()));
	    			}
	    			userMessageManager.sendSystemMessage(new MessageContent("col.supervise.workflow.update", summary.getSubject(), memname, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel),
	    					ApplicationCategoryEnum.collaboration, member.getId(), receiver, 1);
	    		}
		    	
		    	UpdateIndexManager updateIndexManager = (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
		    	updateIndexManager.update(theSummaryId, ApplicationCategoryEnum.collaboration.getKey());
		    	//operationlogManager.insertOplog(Long.parseLong(summaryId), ApplicationCategoryEnum.collaboration, 
		        //		Constant.OperationLogActionType.modifyWorkflow.name(), "col.operationlog.modifyWorkflow", memname, new java.util.Date());
	    	}else{
	    		EdocSummaryDao edocSummaryDao = (EdocSummaryDao) ApplicationContextHolder.getBean("edocSummaryDao");
	    		EdocSummary edocSummary = edocSummaryDao.getSimpleObject(Long.parseLong(summaryId), "startUserId", "subject","edocType");
	    		subject = edocSummary.getSubject();
	    		if(!edocSummary.getStartUserId().equals(memberId)){
                	int edocType = edocSummary.getEdocType();
                	ApplicationCategoryEnum app = null;
                	
                	if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocSend;
                	}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocRec;               		
                	}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
                		app = ApplicationCategoryEnum.edocSign;               		
                	}else{
                		app = ApplicationCategoryEnum.edoc; 
                	}
                	Integer importantLevel = edocSummary.getImportantLevel();
                	MessageReceiver receiver = new MessageReceiver(edocSummary.getId(), Long.valueOf(edocSummary.getStartUserId()));
                	userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.workflow.update", edocSummary.getSubject(), memname,app.ordinal()).setImportantLevel(importantLevel),
                			app, member.getId(), receiver, 1);
                	
                	//operationlogManager.insertOplog(Long.parseLong(summaryId), app, 
                	//		Constant.OperationLogActionType.modifyWorkflow.name(), "col.operationlog.modifyWorkflow", memname, new java.util.Date());
                }
	    	}
	    	appLogManager.insertLog(CurrentUser.get(), AppLogAction.Coll_Flow_Modify, memname, subject);
    	} catch (NumberFormatException e) {
			log.error("", e);
		} catch (BusinessException e) {
			log.error("", e);
		}
    }
    
    public String checkNodePolicy(String[] policyArr, String[] itemNameArr, String loginAccountId){
    	if(policyArr != null && policyArr.length > 0 && itemNameArr != null && itemNameArr.length > 0 ){
	    	Long accountId = Long.valueOf(Long.parseLong(loginAccountId));
	        if (this.flowPermManager.isNeedUpdateRef(accountId)) { //如果单位下有未引用的节点权限
            	for(int i=0; i<policyArr.length; i++) {
            		if(Strings.isNotBlank(policyArr[i])){
            			ConfigItem item = configManager.getConfigItem(itemNameArr[i], policyArr[i], accountId);
            			if(item == null){
            				return "1";
            			}
            			else if(this.flowPermManager.isNeedUpdateRef(policyArr[i], accountId)) { //如果该节点需要引用
            				Boolean updateSuccess = permissionManager.updateIsRef(item, itemNameArr[i], policyArr[i], 1, accountId);
            				if (!updateSuccess) { //更新不成功
            					return "1";
            				}
            			}
            		}
            	}
            }
    	}
        return "0";
    }
    
    /**
	   * 判断表单 应用、视图、操作是否存在
	   * @param appId
	   * @param formId
	   * @param operationId
	   * @return 0:都存在1:appId不存在2:formId不存在3:operationId不存在
	   */
	public int checkForm(long appId,long formId,long operationId) {
		return FormHelper.isExist(appId, formId, operationId);
	}
    
    /**
     * 判断是否可以撤销<br>
     * 有审核节点已处理过的，不允许撤销.
     */
    public String checkIsCanRepeal(Long summaryId) throws ColException {
        String result = "TRUE";
        List<Affair> affairList = affairManager.getAvailabilityDoneAffairList(summaryId);
        if(affairList != null && !affairList.isEmpty()){
            for (Affair affair : affairList) {
                SeeyonPolicy seeyonPolicy = ColHelper.getPolicyByAffair(affair);
                if(cannotRepealList.contains(seeyonPolicy.getId())){
                    return "FALSE";
                }
            }
        }
        result = checkIsCanStopFlow(summaryId);
        return result;
    }
    /**
     * 判断是否可以撤销<br>
     * 有核定节点已处理过的，不允许撤销.
     */
    public String checkIsCanRepealForVouch(Long summaryId) throws ColException {
        List<Affair> affairList = affairManager.getAvailabilityDoneAffairList(summaryId);
        if(affairList != null && !affairList.isEmpty()){
            for (Affair affair : affairList) {
                SeeyonPolicy seeyonPolicy = ColHelper.getPolicyByAffair(affair);
                if("vouch".equals(seeyonPolicy.getId())){
                	return "FALSE";
                }
            }
        }
        return "TRUE";
    }
    /**
     * 判断是否可以撤销终止流程<br>
     * 用于管理员控制流程.
     */
    public String checkIsCanStopFlow(Long summaryId) throws ColException {
        ColSummary summary = get(summaryId);
        if(summary == null){
            return "FALSE";
        }
        boolean hasNewflow = summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.main.ordinal();
        if(hasNewflow){
            String finishedNewflowTitle = newflowManager.getFinishedNewflow(summaryId, null);
            if(Strings.isNotBlank(finishedNewflowTitle)){
                //如果该流程触发的新流程已结束，不能撤销
                return "《" + finishedNewflowTitle + "》";
            }
        }
        return "TRUE";
    }
    
    /**
     * 判断是否可以回退<br>
     * //如果上节点触发的新流程已结束，不能回退
     * @return
     */
    public String[] checkIsCanStepBack(Long summaryId, String processId, String nodeId) throws ColException {
        /*
        ColSummary summary = get(summaryId);
        if(summary == null){
            return "FALSE";
        }
        boolean hasNewflow = summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.main.ordinal();
        if(hasNewflow){
        */
    	/**wangchw 2011-10-12
            BPMProcess process =  ColHelper.getRunningProcessByProcessId(processId);
            BPMActivity activity = process.getActivityById(nodeId);
            
            List<String> prevNodeIds = ColHelper.checkPrevNodeHasNewflow(activity);
            if(prevNodeIds!=null && !prevNodeIds.isEmpty()){
                String finishedNewflowTitle = newflowManager.getFinishedNewflow(summaryId, prevNodeIds);
                if(Strings.isNotBlank(finishedNewflowTitle)){
                    return finishedNewflowTitle;
                }
            }wangchw 2011-10-12*/
        //}
    	String[] result= new String[2];
        ColSummary summary = get(summaryId);
        BPMProcess process =  ColHelper.getRunningProcessByProcessId(processId);
        if(summary.getNewflowType()!=null && summary.getNewflowType()==Constant.NewflowType.child.ordinal()){//子流程
            BPMActivity activity = process.getActivityById(nodeId);
            List ups= activity.getUpTransitions();
            if(ups.size()==1){
            	if(((BPMTransition)ups.get(0)).getFrom().getNodeType().equals(BPMAbstractNode.NodeType.start)){
            		result[0]= summary.getNewflowType().toString();
            		result[1]= summary.getSubject();
            		return result;
            	}else{
            	    BPMAbstractNode fromNode= ((BPMTransition)ups.get(0)).getFrom();
            	    fromNode= goPreContinue(fromNode);
            	    if( fromNode.getNodeType()== BPMAbstractNode.NodeType.start){
            	        result[0]= summary.getNewflowType().toString();
                        result[1]= summary.getSubject();
                        return result;
            	    }
            	}
            }
        }else if( summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.main.ordinal()){//主流程
        	BPMActivity activity = process.getActivityById(nodeId);
            Map resultMap= Utils.isAllHumenNodeValid(activity);
            String result_str= (String)resultMap.get("result");
            List<NewflowRunning> runList = null;
            if("0".equals(result_str) || "1".equals(result_str)){//正常回退或撤销，都得对后续的节点的子流程进行删除操作
            	Map normalNodes= (Map)resultMap.get("normal_nodes");
            	if("1".equals(result_str)){//撤销，放入start节点信息
            		normalNodes.put(process.getStart().getId(), process.getStart().getId());
            	}
            	List<String> prevNodeIds= Utils.getAllNFNodes(normalNodes,process);
            	//NF 如果前一节点触发了新流程，则召回（撤销）新流程
                boolean hasNewflow = (prevNodeIds!=null && !prevNodeIds.isEmpty());
                if(hasNewflow){
                    String finishedNewflowTitle = newflowManager.getFinishedNewflow(summary.getId(), prevNodeIds);
                    if(Strings.isNotBlank(finishedNewflowTitle)){
                        //该节点的上节点触发的新流程已结束，不能取回
                    	result[0]= summary.getNewflowType().toString();
                		result[1]= finishedNewflowTitle;
                		return result;
                    }
                }
            }
        }
        result[0]= "null";
		result[1]= "null";
        //return "TRUE";
        return result;
    }
    private BPMAbstractNode goPreContinue(BPMAbstractNode fromNode) {
        BPMAbstractNode returnNode= null;
        if(fromNode.getNodeType()== BPMAbstractNode.NodeType.split){
            List ups= fromNode.getUpTransitions();
            BPMAbstractNode fromNodeNext= ((BPMTransition)ups.get(0)).getFrom();
            returnNode= goPreContinue(fromNodeNext);
        }else{
            returnNode= fromNode;
        }
        return returnNode;
    }
    /**
     * 判断本节点是否核定点<br>
     * 如果本节点是核定节点，不能取回
     * @return
     */
    public String checkSelfIsVouch(Long summaryId,String processId, String affairId) throws ColException {
		ColSummary summary = get(summaryId);
		if (summary != null ) {
			if(NumberUtils.isNumber(affairId)){
				Affair affair = affairManager.getById(Long.valueOf(affairId));
				if(affair != null){
					String nodeId = ColHelper.getActvityIdByAffair(affair);
					BPMProcess process =  ColHelper.getRunningProcessByProcessId(processId);
					BPMActivity activity = process.getActivityById(nodeId);
					BPMSeeyonPolicy policy = activity.getSeeyonPolicy();
					String policyId = policy.getId();
					if("vouch".equals(policyId) && summary.getIsVouch() != null && Constant.ColSummaryVouch.vouchPass.getKey() == summary.getIsVouch().intValue()){
						return "TRUE1";
					}
					//主流程中没有核定通过节点，还要判断当前节点的子流程中是否已核定通过
					if("1".equals(policy.getNF())){
						if(checkSubProcessIsVouched(summary.getId(),summary.getTempleteId())){
							return "TRUE2";
						}
					}
				}
			}
		}
		return "FALSE";
    }
	/**
     * 判断上节点可以回退<br>
     * //如果上节点是核定节点，不能回退
     * @return
     */
    public String checkUpIsVouch(Long summaryId,String processId, String nodeId) throws ColException {
    	ColSummary summary = get(summaryId);
    	if(summary != null){
    		BPMProcess process =  ColHelper.getRunningProcessByProcessId(processId);
    		BPMActivity activity = process.getActivityById(nodeId);
    		Map<String,Object> resultMap= Utils.isCanNotStepOfGivenPolicy((BPMActivity)activity, "vouch", false);
    		String result_str= (String)resultMap.get("result");
            if(result_str.equals("-1")){//表示遇到核定节点
            	return "TRUE1";
            }else if(result_str.equals("0")) {
            	Map<String,String> normal_nodes = (Map)resultMap.get("normal_nodes");
            	Iterator<String> iterators = normal_nodes.keySet().iterator();
            	while(iterators.hasNext()){
            		String normalNodeId = iterators.next();
            		BPMActivity normalActivity = process.getActivityById(normalNodeId);
            		BPMSeeyonPolicy normalPolicy = normalActivity.getSeeyonPolicy();
            		if("1".equals(normalPolicy.getNF())){
            			Map<Long, NewflowRunning> map =  newflowManager.getNewflowRunningMap(summary.getTempleteId(), normalNodeId,true);
            			Iterator<NewflowRunning> iterator = map.values().iterator();
            			while(iterator.hasNext()){
            				NewflowRunning newflowRunning = iterator.next();
            				ColSummary subSummary = get(newflowRunning.getSummaryId());
            				if(subSummary != null && subSummary.getIsVouch() != null && Constant.ColSummaryVouch.vouchPass.getKey() ==subSummary.getIsVouch().intValue() ){
            					return "TRUE2";
            				}
            			}
            		}
            	}
        	}
    	}
    	return "FALSE";
    }
   
    /**
     * 判断是否核定通过<br>
     * @return
     */
    public String checkIsVouch(String processId) throws ColException {
		ColSummary colSummary = getSummaryByProcessId(processId);
		if(colSummary != null){
			if(colSummary.getIsVouch() != null && Constant.ColSummaryVouch.vouchPass.getKey() == colSummary.getIsVouch().intValue()){
					return "TRUE1";
			}
			if(colSummary.getNewflowType()!=null && colSummary.getNewflowType() == Constant.NewflowType.main.ordinal() && newflowManager.checkTempleteHasNewflow(colSummary.getTempleteId())){
				if(checkSubProcessIsVouched(colSummary.getId(),colSummary.getTempleteId())){
					return "TRUE2";
				}
			}
		}
		return "FALSE";
    }
    public boolean checkSubProcessIsVouched(Long summaryId,Long templeteId) throws ColException{
    	if(summaryId != null && templeteId!= null  && NumberUtils.isNumber(summaryId.toString()) && NumberUtils.isNumber(templeteId.toString())){
	    	List<NewflowRunning> newFlowList = newflowManager.getNewflowRunningList(summaryId, templeteId, Constant.NewflowType.main.ordinal());
			for(NewflowRunning newFlow:newFlowList){
				ColSummary childSummary = getColSummaryById(newFlow.getSummaryId(),false);
				if(childSummary != null && childSummary.getIsVouch() != null && Constant.ColSummaryVouch.vouchPass.getKey() == childSummary.getIsVouch().intValue()){
					return true;
				}
			}
    	}
		return false;
    }
    private int recallNewflowSummary(Long summaryId, User user, String operationType) throws ColException {
        int result = 0;
        ColSummary summary = get(summaryId);
        if(summary == null){
            return 1;
        }
 
        WorkflowEventListener.setOperationType(WorkflowEventListener.CANCEL);
        Long caseId = summary.getCaseId();
        if (caseId == null){
            return 1;
        }
        
        //将summary的状态改为待�发,撤销已生成事项
        List<Affair> affairs = affairManager.getALLAvailabilityAffairList(summaryId, false);
        
        //撤销流程
        result = ColHelper.cancelCase(caseId);
        if (result == 1) {
            return result;
        }
        List<Long> affairIds = new ArrayList<Long>();
        if(affairs != null){
            for(int i=0;i<affairs.size();i++){
                Affair affair = (Affair) affairs.get(i);
                if(affair.getState()==StateEnum.col_sent.key()){
                    affair.setState(StateEnum.col_waitSend.key());
                    affair.setSubState(SubStateEnum.col_waitSend_cancel.key());
                    affair.setIsDelete(true);
                    
                    affairManager.updateAffair(affair);
                }
                
                if(affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0) {
                    QuartzHolder.deleteQuartzJob("Remind" + affair.getId());
                }
                
                if(affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0) {
                    QuartzHolder.deleteQuartzJob("DeadLine" + affair.getId());
                }
                affairIds.add(affair.getId());
            }
            
            this.affairManager.cancelWorkflow(summaryId);
        }
        
        summary.setCaseId(null);
        update(summary);
        
        ColHelper.deleteQuartzJobOfSummary(summary);
        
        //删除子流程的预归档
        if(summary.getArchiveId() != null){
        	try {
        		getDocHierarchyManager().deleteDocByResources(affairIds, user);
        	} catch (Exception e) {
        		log.error("删除归档文档:"+e);
        	}
        }
        //撤销协同工作项操作日志
        //operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration, 
        //        Constant.OperationLogActionType.cancelColl.name(), "col.operationlog.cancelColl", user.getName(), new java.util.Date(), summary.getSubject());
        
        String key = "col.newflow.callback";
        String operation = "";
        //更明确的消息提示
        if("takeBack".equals(operationType)){
            operation = Constant.getString("takeBack.label");
        }
        else if("stepBack".equals(operationType)){
            operation = Constant.getString("stepBack.label");
        }
        else if("repeal".equals(operationType)){
            operation = Constant.getString("repeal.2.label");
        }
        //对发起人以外的所有执行人发消息通知
        try{
            Integer importantLevel = summary.getImportantLevel();
            Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
            for(Affair affair1 : affairs){
                if(affair1.getIsDelete())
                    continue;
                if(affair1.getMemberId()==user.getId()){continue;}
                receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId()));
            }
            userMessageManager.sendSystemMessage(new MessageContent(key, summary.getSubject(), user.getName(), operation).setImportantLevel(importantLevel),
                    ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
        }
        catch (MessageException e) {
            log.error("召回新流程协同发送提醒消息异常", e);
            throw new ColException("send message failed");
        }
        
        processLogManager.deleteLog(Long.parseLong(summary.getProcessId()));
    	//流程撤销事件通知
    	CollaborationCancelEvent cancelEvent = new CollaborationCancelEvent(this);
    	//cancelEvent.setSummary(summary);
    	cancelEvent.setSummaryId(summary.getId());
    	cancelEvent.setUserId(user.getId());
    	EventDispatcher.fireEvent(cancelEvent);
    	
        return 0;
    }
    
	public List getFormContent(String affairid ,String summaryid,String formid,String operationid) throws Exception {

	    List formcontentlist = new ArrayList();
		long summaryId = Long.parseLong(summaryid);		
		ColSummary summary = getColSummaryById(summaryId, true);
		ColBody body = summary.getFirstBody();
		
		if("FORM".equals(body.getBodyType())){
		   String masterId = body.getContent(); 
		   String[] formPolicy = null;
		   String nodePermissionPolicy = "";
		   String nodePermissionPolicyName = "Collaboration";
		   Affair affair = null;
		   String formAppId = null; //表单应用id
		   String formIds = null; //表单id
		   String formOperationId = null; //表单节点操作Id

		   if (affairid != null) {
				affair = affairManager.getById(Long.parseLong(affairid));
//				是否是表单
		        boolean isForm = "FORM".equals(body.getBodyType());		        	   
		        //如果数据库中取不到节点权限，就从XML中取
		        if(Strings.isBlank(affair.getNodePolicy())){
			        //获取当前事项对应的节点
			        BPMProcess process = ColHelper.getCaseProcess(summary.getProcessId());
			        BPMSeeyonPolicy policy = null;			        
			        if(process != null){
				        int affairState = affair.getState().intValue();
				        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
				        	policy = process.getStart().getSeeyonPolicy();
				        }
				        else{
				        	policy = ColHelper.getBPMActivityByAffair(process, affair).getSeeyonPolicy();
				        }				        
				        if(policy != null){
				            nodePermissionPolicy = policy.getId();
				        }				        
				        if(isForm){
				        	formAppId = policy.getFormApp();
				        	formIds = policy.getForm();
				        	formOperationId = policy.getOperationName();
				        }
			        }
		        }
		        else{
		        	nodePermissionPolicy = affair.getNodePolicy();		        	
		        	if(isForm){
			        	formAppId = String.valueOf(affair.getFormAppId());
			        	formIds = String.valueOf(affair.getFormId());
			        	formOperationId = String.valueOf(affair.getFormOperationId());
		        	}
		        }		        
		        nodePermissionPolicyName = BPMSeeyonPolicy.getShowName(nodePermissionPolicy);		        
			}
		   
		   if(formAppId != null){
			User user = CurrentUser.get();	
			String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formAppId, formid, operationid, masterId, summaryid, affairid, nodePermissionPolicyName,false);
			formcontentlist.add(formContent);
			formcontentlist.add(masterId);
		   }
		 }
    	return formcontentlist;
		}
    public List getSummaryList(List summaryidlist) throws Exception {
		try{
			DetachedCriteria criteria = DetachedCriteria.forClass(ColSummary.class);
			 List <Long> sumidlist = new ArrayList<Long>();
	            for(int i=0;i<summaryidlist.size();i++){
	            	Long summaryid = Long.parseLong(summaryidlist.get(i).toString());
	            	sumidlist.add(summaryid);
	            }
	        criteria.add(Expression.in("id", sumidlist));
           
			List<ColSummary> sumlist = super.executeCriteria(criteria, -1, -1);
           
            
//            Map<String, Object> namedParatmeter = new HashMap<String, Object>();
//			namedParatmeter.put("in", sumidlist);
//			List<ColSummary> sumlist = super.find(" from ColSummary where id in (:in)", -1, -1,namedParatmeter);
			return sumlist;
		}catch(Exception e){

			throw new DataDefineException(DataDefineException.C_iDbOperErrode_QueryError,e);
		}	
    }
    public boolean getSummaryByParentformId(Long parentformSummaryId) throws ColException {
        List<ColSummary> list = super.findBy("parentformSummaryId", parentformSummaryId);
        if (list.size() == 0) {
            return true;
        }

        return false;
    }
    
    /**
	  * 取得对应的基准岗和集团职务级别（ajax调用）
	  * @param postIds      岗位id
	  * @param levels       职务级别id
	  * @return  id集合
	  */
	 public List<String> getStandardPostAndLevel(String[] ids,String[] types){
		 List<String> groupIds = new ArrayList<String>();
		 if(ids != null && types != null){
			 V3xOrgPost post = null;
			 V3xOrgLevel level = null;
			 Long id = null;
			 for(int i=0;i<ids.length;i++){
				 try{
					 id = Long.parseLong(ids[i]);
					 if(V3xOrgEntity.ORGENT_TYPE_POST.equals(types[i])){
						 post = this.orgManager.getBMPostByPostId(id);
						 if(post != null)
							 groupIds.add(ids[i]+":"+post.getId().toString());
						 else{
							 log.info(ColManagerImpl.class.getName()+".getStandardPostAndLevel is null,id:"+ids[i]+",type:"+types[i]);
						 }
					 }else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(types[i])){
						 level = this.orgManager.getLevelById(id);
						 if(level != null){
							 if(level.getGroupLevelId() != null && level.getGroupLevelId().longValue() != 0)
								 groupIds.add(ids[i]+":"+level.getGroupLevelId().toString());
							 else
								 groupIds.add(ids[i]+":"+ids[i]);
						 }else{
							 log.info(ColManagerImpl.class.getName()+".getStandardPostAndLevel is null,id:"+ids[i]+",type:"+types[i]);
						 }
					 }
				 }catch(Exception e){
					 log.error((V3xOrgEntity.ORGENT_TYPE_POST.equals(types[i])?"取基准岗错误：":"取集团职务级别错误：")+id,e);
				 }
			 }
		 }
		 return groupIds;
	 }
	 
	 /**
	  * 分支计算跟踪日志(wangchw)
	  * @param beforeScripts 运算之间的js条件分支
	  * @param afterStripts 运算之后的js条件分支
	  * @param formFieldData 表单字段数据
	  */
	 public void createBranchLogData(
			 String[] beforeScripts,
			 String[] afterStripts,
			 String team,
			 String secondpost,
			 String startTeam,
			 String startSecondpost,
			 String formFieldData,
			 String type){
		 for(int i=0;i<beforeScripts.length;i++){
			 if(log.isInfoEnabled()){
				 log.info("beforeScripts["+i+"]:"+beforeScripts[i]);
				 log.info("afterStripts["+i+"]:"+afterStripts[i]);
			 }
		 }
		 if(log.isInfoEnabled()){
			 log.info("team:="+team);
			 log.info("secondpost:="+secondpost);
			 log.info("startTeam:="+startTeam);
			 log.info("startSecondpost:="+startSecondpost);
			 log.info("type:["+type+"],formFieldData:="+formFieldData);
		 }
	 }
	 /**
	  * 保存分支信息，并记录日志，只用于调试，收集信息
	  * @param beforeScripts
	  * @param afterStripts
	  */
	 public void createBranchLog(String[] beforeScripts,String[] afterStripts,String summaryId,String affairId,String processXML,String formdata){
		 StringBuffer info = new StringBuffer();
		 info.append("分支异常数据开始===========================================\r\n");
		 info.append("summaryId:"+summaryId+"\r\n");
		 info.append("affairId:"+affairId+"\r\n");
		 User user = CurrentUser.get();
		 info.append("username:"+user.getName()+"\r\n");
		 info.append("userId:"+user.getId()+"\r\n");
		 info.append("departId:"+user.getDepartmentId()+"\r\n");
		 info.append("postId:"+user.getPostId()+"\r\n");
		 info.append("levelId:"+user.getLevelId()+"\r\n");
		 info.append("accountId:"+user.getAccountId()+"\r\n");
		 info.append("loginId:"+user.getLoginAccount()+"\r\n");
		 
		 try{
			 List<V3xOrgEntity> teams = this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM);
			 if(teams != null && teams.size()>0){
				 info.append("teams:");
				 for(V3xOrgEntity org:teams)
					 info.append(org.getId().toString()+",");
				 info.append("\r\n");
			 }
		 }catch(Exception e){
			 log.error("", e);
		 }
		 try{
			 V3xOrgMember mem = orgManager.getMemberById(user.getId());
			 List<MemberPost> secondPosts = mem.getSecond_post();
			 if(secondPosts != null && secondPosts.size()>0){
				 info.append("secondPost:");
				 for(MemberPost post:secondPosts)
					 info.append(post.getDepId().toString()+"_"+post.getId().toString()+",");
				 info.append("\r\n");
			 }
		 }catch(Exception e){
			 log.error("", e);
		 }
		 info.append("processxml:"+"\r\n");
		 info.append(processXML+"\r\n");
		 info.append("formdata:"+"\r\n");
		 info.append(formdata+"\r\n");
		 
		 for(int i=0;i<beforeScripts.length;i++){
			 info.append("beforeScripts:"+beforeScripts[i]+"\r\n");
			 info.append("afterStripts:"+afterStripts[i]+"\r\n");
		 }
		 info.append("分支异常数据结束===========================================");
		 log.error(info.toString());
	 }
	 
	 public boolean updateSummaryAttachment(int attSize,List<ProcessLog> logs,Long summaryId){
		 try {
			ColSummary summary = getSimpleColSummaryById(summaryId);
			boolean needUpdate = false;
			 //加上/去掉附件标示
			if(summary.isHasAttachments() && attSize ==0){
				needUpdate = true;
			}else if(!summary.isHasAttachments() && attSize >0){
				needUpdate = true;
			}
			if(needUpdate){
				boolean hasAtt = attSize != 0;
				summary.setHasAttachments(hasAtt);
				
				Map<String,Object> p = new HashMap<String,Object>();
				p.put("identifier", summary.getIdentifier());
				update(ColSummary.class, summary.getId(), p);
				
				Affair affair = new Affair();
				affair.setHasAttachments(attSize != 0);
				Map<String,Object> parameter = new HashMap<String,Object>();
				parameter.put("identifier", affair.getIdentifier());
				affairManager.updateAllAvailabilityAffair(ApplicationCategoryEnum.collaboration, summary.getId(), parameter);
			}
			//流程日志
			processLogManager.insertLog(logs);
			//消息
			ColMessageHelper.saveUpdataAttMessage(affairManager, userMessageManager, orgManager, summary);
			
		} catch (ColException e) {
			log.error("",e);
			return false;
		}
		 
		 return true;
	 }
	 
	    public String getFormFileDisPlayValue(Long summaryId,String filedName) throws Exception {
	    	
	    	ColSummary summary = getColSummaryById(summaryId, false) ;
	    	if(summary == null){
	    		return null ;
	    	}
	    	
	    	if(!"FORM".equals(summary.getBodyType())){
	    		return null ;
	    	}
	    	List<String> list = new ArrayList<String>();
	    	list.add(filedName) ;
	    	
	    	Map<String,DisplayValue> map = this.getFormFileValue(list , summaryId+"") ;
	  	  	if(map == null){
	  	  		return null ;
	  	  	}
	  	  	DisplayValue valueAndDisPlay = map.get(filedName) ;
	  	  	
	  	  	if(valueAndDisPlay == null){
	  	  		return null ;
	  	  	}
	  	  	if(Strings.isNotBlank(valueAndDisPlay.getDisplay())){
	  	  		return valueAndDisPlay.getDisplay() ;
	  	  	}
	  	  	return  valueAndDisPlay.getValue();
	    }
	    
	    public Map<String,DisplayValue> getFormFileValue(List<String> filedNames , String... params)throws Exception {
	    	if(params == null || params.length < 1){
	    		return null ;
	    	}
	    	ColSummary summary = getColSummaryById(Long.valueOf(params[0]), false) ;
	    	if(summary == null){
	    		return null ;
	    	}
	    	
	    	if(!"FORM".equals(summary.getBodyType())){
	    		return null ;
	    	}
	    	
	    	if(filedNames == null || filedNames.isEmpty()){
	    		return null ;
	    	}
	    	
			Map<String ,String[]> fieldMap = FormHelper.getIOperBase().getFieldValueMap(summary,templeteManager.get(summary.getTempleteId())) ;
			
			if(fieldMap == null || fieldMap.isEmpty()){
				return null ;
			}
			Map<String ,DisplayValue> valueMap = new HashMap<String ,DisplayValue>() ;
			for(String str : filedNames){
				DisplayValue displayValue = new DisplayValue() ;
				String[] valueAndDisplay = fieldMap.get(FormHelper.getfileNameName(str)) ;
				if(valueAndDisplay != null){
					if(Strings.isBlank(valueAndDisplay[1])){
						valueAndDisplay[1] = valueAndDisplay[0] ;
					}
					displayValue.setDisplay(valueAndDisplay[0]) ;
					displayValue.setValue(valueAndDisplay[1]) ;
					valueMap.put(str, displayValue) ;
				}
			}
			
			return valueMap ;
	    }
	    

	    public String getSenderAffairIdBysummaryId(Long summaryId) throws Exception{
	    	if(summaryId == null){
	    		return null ;
	    	}
	    	Affair affair = this.affairManager.getCollaborationSenderAffair(summaryId) ;
	    	if(affair == null){
	    		return null ;
	    	}
	    	return affair.getId()+"" ;
	    }
	    
	    public List<ColSummary> getColSummaryForForm(Long formId, User user, String condition, String textfield, String textfield1, boolean isMySent) throws Exception {
	    	if(isMySent){
		    	if(user != null){
		    		return getColSummaryForForm(formId,user.getId(), Constant.flowState.finish.ordinal(), condition, textfield, textfield1) ;
		    	}
		    	return getColSummaryForForm(formId,null, Constant.flowState.finish.ordinal(),condition, textfield, textfield1) ;
	    	}else{
	    		return getColSummaryForForm(formId,null, Constant.flowState.finish.ordinal(),condition, textfield, textfield1, isMySent) ;
	    	}
	    }
	    
	    public List<ColSummary> getColSummaryForForm(Long formId, User user, String condition, String textfield, String textfield1) throws Exception {
	    	return getColSummaryForForm(formId, user, condition, textfield, textfield1, true);
	    }
	    
	    private List<ColSummary> getColSummaryForForm(Long formId, Long userId, Integer state, String condition, String textfield, String textfield1) throws Exception {
		    return getColSummaryForForm(formId, userId, state, condition, textfield, textfield1, true);
	    }
	    
	    /**
	     * 根据成员id查询授权的审核通过或者已经完成的表单
	     * @param memberId
	     * @return
	     */
	    public List<ColSummary> getColSummaryForForm(Long formId, Long userId, Integer state, String condition, String textfield, String textfield1, boolean isMySent){
	    	try {
	        	StringBuffer hql = new StringBuffer();
	        	hql.append("select distinct summary.id ");
	        	hql.append(" from ColSummary as summary,Affair as affair");
				Map<String, Object> parameterMap = new HashMap<String, Object>();
				parameterMap.put("formAppId", formId);
	    		parameterMap.put("state", state);
	    		parameterMap.put("vouch", Constant.ColSummaryVouch.vouchPass.ordinal());
				if(isMySent){
					hql.append(" where summary.formAppId = :formAppId and (summary.state = :state or summary.isVouch = :vouch)");
					if(userId!=null){
						hql.append(" and summary.startMemberId = :startMemberId");
			    		parameterMap.put("startMemberId", userId);
					}
				}else{
					userId = CurrentUser.get().getId();
					hql.append(",ColRelationAuthority as authority ");
					hql.append(" where summary.formAppId = :formAppId and (summary.state = :state or summary.isVouch = :vouch)");
					hql.append(" and summary.startMemberId != :startMemberId");
					hql.append(" and summary.id = authority.summaryId");
					hql.append(" and authority.userid in (:entIdsList)");
					OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
					List<Long> entIdsList = orgManager.getUserDomainIDs(userId,V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
							V3xOrgEntity.ORGENT_TYPE_TEAM,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		    		parameterMap.put("startMemberId", userId);
					parameterMap.put("entIdsList", entIdsList);
				}
				hql.append(" and affair.isDelete = :isDelete");
				parameterMap.put("isDelete", false);
				hql.append(" and affair.state = :affairState");
				parameterMap.put("affairState", Integer.valueOf(StateEnum.col_sent.getKey()));
				hql.append(" and affair.objectId = summary.id");
				if (condition != null) {
		        	if (condition.equals("subject")) {
		        		hql.append(" and (summary.subject like :subject)");
			    		parameterMap.put("subject", "%"+SQLWildcardUtil.escape(textfield)+"%");
		            }else if (condition.equals("importantLevel")) {
		            	hql.append(" and (summary.importantLevel = :importantLevel)");
			    		parameterMap.put("importantLevel", Integer.parseInt(textfield));
		            }else if (condition.equals("createDate")) {
		                if (StringUtils.isNotBlank(textfield)) {
		                	hql.append(" and summary.createDate >= :createStartDate");
		                    java.util.Date stamp = Datetimes.getTodayFirstTime(textfield);
				    		parameterMap.put("createStartDate", stamp);
		                }
		                if (StringUtils.isNotBlank(textfield1)) {
		                	hql.append(" and summary.createDate <= :createEndDate");
		                    java.util.Date stamp = Datetimes.getTodayLastTime(textfield1);
				    		parameterMap.put("createEndDate", stamp);
		                }
		            }
		        }
				hql.append(" order by summary.id");
				List<Long> summaryList = super.find(hql.toString(),parameterMap);
				if(summaryList!=null && summaryList.size()>0){
					return getSummaryList(summaryList);
				}else{
					return null;
				}
	    	}catch (Exception e) {
	    		logger.error("关联表单查询错误",e);
			}
			return null;
	    }
	    
	    public List<ColBranch> getBranch(Long templeteId,Integer category){
	    	List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(templeteId,category);
	    	return branchs;	
	    }
	    
	    /**
		  * ajax保存个人设置，打开协同时是否直接展开
		  * @param config
		  * @return
		  * @throws Exception
		  */
		 public boolean saveConfig(Long memberId,String config){
			 try{
				 V3xOrgMember member = this.orgManager.getMemberById(memberId);
				 if(member != null){
					 member.setProperty("extendConfig", config);
					 this.orgManager.updateMember(member);
					 return true;
				 }
			 }
			 catch(Exception e){
				 log.error("保存协同展开设置时错误：", e);
			 }
			 return false;
		 }
		 public void setHandWriteManager(HandWriteManager handWriteManager) {
				this.handWriteManager = handWriteManager;
			}
		 /**
		  * Ajax表单时间差计算
		  */
		 
		 public float differDateTime(java.util.Date beginDealTimeDate, java.util.Date endDealTimeDate, Long orgAcconutID){
			 float day = 0;
			 try {
				day =  workTimeManager.differDateTime(beginDealTimeDate,endDealTimeDate,orgAcconutID);
			} catch (WorkTimeSetExecption e) {
				log.error("ajax表单计算工作时间异常:beginDealTimeDate:"+beginDealTimeDate+",endDealTimeDate:"+endDealTimeDate+",orgAcconutID:"+orgAcconutID,e);
			}
			return day;
		 }
		 public int differDate(String beginDealDateStr, String endDealDateStr, Long orgAcconutID) {
			 int day = 0;
			 try {
				day = (int) workTimeManager.differDate(beginDealDateStr,endDealDateStr,orgAcconutID);
			} catch (WorkTimeSetExecption e) {
				log.error("ajax表单计算工作时间异常:beginDealDateStr:"+beginDealDateStr+",endDealDateStr:"+endDealDateStr+",orgAcconutID:"+orgAcconutID,e);
			}
			return day;
		 }
		 public String getComputeDateOfDay(String beginDealTimeDate, String operation, Integer time,String unit,Long orgAcconutID){
			Date date = null;
			int len =beginDealTimeDate.length();
			try {
				if(len>10) { // 日期时间
					Date d = Datetimes.parse(beginDealTimeDate);
					date =  workTimeManager.getComputeDate(d,operation,time,unit,orgAcconutID);
				} else { // 日期
					date =  workTimeManager.getComputeDate(beginDealTimeDate, operation, time,orgAcconutID);
				}
			} catch (WorkTimeSetExecption e) {
				log.error("ajax表单计算工作时间异常",e);
			}
			if(date == null )	return "";
			
			if(len>10)  return Datetimes.formatDatetimeWithoutSecond(date);
			else return Datetimes.formatDate(date);
		 }
		 
		 public void insertComplexPeopleOfEdoc(EdocSummary edocSummary,Long summaryId,Affair affair, User user,List<String> memAndPolicy,List<String> partyNames) throws ColException {
		    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
		        WorkflowEventListener.setOperationType(WorkflowEventListener.INSERT);
		        //加签消息提醒
		        List<MessageData> messageDataList = null;
		        if(messageDataMap.get(summaryId) == null){
		        	 messageDataList = new ArrayList<MessageData>();
		        }else{
		        	messageDataList = messageDataMap.get(summaryId);
		        	messageDataMap.remove(summaryId);
		        }
		        MessageData messageData = new MessageData();
		        messageData.setOperationType("insertPeople");
		        messageData.setHandlerId(user.getId());
		        messageData.setEdocSummary(edocSummary);
		        messageData.setAffair(affair);
		        messageData.addProcessLogParam(Functions.join(memAndPolicy,","));
		        messageData.setPartyNames(partyNames);
		        messageDataList.add(messageData);
		        messageDataMap.put(summaryId, messageDataList);
		    }
		 public ColBody getColBodyByFileid(long fileid) throws ColException {
		    	DetachedCriteria criteria = DetachedCriteria.forClass(ColBody.class);
		        criteria.add(Restrictions.like("content", fileid+""));
		        return (ColBody)super.executeUniqueCriteria(criteria);
		    }
			public List<ColTrackMember> getColTrackMembers(Long affairId){
				return colTrackMemberDao.getColTrackMembersByAffairId(affairId);
			}
			public List<ColTrackMember> getColTrackMembersByObjectIdAndTrackMemberId(Long objectId,Long trackMemberId){
				return colTrackMemberDao.getColTrackMembersByObjectIdAndTrackMemberId(objectId, trackMemberId);
			}

		
			public void deleteColTrackMembersByObjectId(Long objectId) {
				colTrackMemberDao.deleteColTrackMembersByObjectId(objectId);
			}
			
		/**
		 * getCurrentNodeInfo
		 * 获得当前处理节点的后续是否需要弹出流程选择页面
		 * @param currentNodeId
		 * @param processId
		 * @param isFromTemplate
		 * @param affairId
		 * @return
		 * @throws ColException
		 */
		public String getCurrentNodeInfo(
				String currentNodeId,
				String processId,
				String isFromTemplate,
				String affairId) throws ColException{
			if(log.isInfoEnabled()){
				log.info("currentNodeId:="+currentNodeId+";processId:="+processId+
						";isFromTemplate:="+isFromTemplate+";affairId:="+affairId);
			}
			Map<String,String> map= new HashMap<String,String>();
			//是否来表单或协同流程模板(即非自由协同)
			boolean templateFlag = "true".equals(isFromTemplate);
			map.put("isFromTemplate",String.valueOf(templateFlag));
			if(Strings.isNotBlank(currentNodeId) && !"start".equals(currentNodeId)){
				BPMProcess process = ColHelper.getCaseProcess(processId);
				BPMHumenActivity currentNodeActivity = (BPMHumenActivity)process.getActivityById(currentNodeId);
				BPMSeeyonPolicy seeyonPolicy = currentNodeActivity.getSeeyonPolicy();
				if("inform".equals(seeyonPolicy.getId()) || "zhihui".equals(seeyonPolicy.getId())){
					map.put("zhihui", "true");
					map.put("hasNewflow","false");
					map.put("lastPeople","false");
				}else{
					map.put("zhihui", "false");
					boolean hasNewflow = templateFlag && seeyonPolicy != null && "1".equals(seeyonPolicy.getNF());
					if(hasNewflow){
						map.put("hasNewflow","true");
					}else{
						map.put("hasNewflow","false");
					}
					//判断当前人员是否为该流程节点的最后一个待办事项的处理人员
					boolean lastPeople= true;
					Affair affair = affairManager.getById(Long.parseLong(affairId));
					//这个方法最好打上跟踪日志
					lastPeople= ColHelper.isExecuteFinished(process, affair);
					if(lastPeople){//如果是，则做如下处理
				        map.put("lastPeople","true");
					}else{
						map.put("lastPeople","false");
					}
				}
				//判断当前节点的下一节点的情况
				List<BPMTransition> downs= currentNodeActivity.getDownTransitions();
				BPMAbstractNode toNode= downs.get(0).getTo();
				BPMAbstractNode.NodeType bNodeType = toNode.getNodeType();
		        if (bNodeType.equals(BPMAbstractNode.NodeType.end)) {
		        	map.put("nextIsEnd","true");
		        }else {
		        	map.put("nextIsEnd","false");
		        }
			}else{//开始节点:开始节点肯定不是知会节点
				map.put("zhihui", "false");
				map.put("hasNewflow","false");
				map.put("nextIsEnd","false");
				map.put("lastPeople","true");
			}
			JSONObject jsonObj= new JSONObject(map);
			String jsonStr= jsonObj.toString().trim();
			if(log.isInfoEnabled()){
				log.info("currentNodeInfo:="+jsonStr);
			}
			return jsonStr;
		}

		@Override
		public int queryPendingCountByUserAndApp(String userid,
				int appType, int stateType, boolean isTemplate,
				boolean isImportant, boolean isCommon) {
			int totalCount= 0;
			StringBuilder hql = new StringBuilder();
			hql.append("select ");
			hql.append(" count(summary.id) ");
			hql.append(" from Affair as affair,ColSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
			hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id");
			hql.append(" and affair.memberId=").append(userid);
			hql.append(" and affair.state=").append(stateType);
			hql.append(" and affair.app=").append(appType);
			hql.append(" and affair.isDelete=false ");
			if(isTemplate){//模板流程
				hql.append(" and affair.templeteId is not null ");
			}else{//自由流程
				hql.append(" and affair.templeteId is null ");
			}
//			if (isImportant) {//重要
//				hql.append(" and ((summary.importantLevel=2) or (summary.importantLevel=3))");
//			} else if(isCommon){//普通
//				hql.append(" and summary.importantLevel=1 ");
//			}
			List result = super.getHibernateTemplate().find(hql.toString(), null);
			if(null!=result){
				Object firstRow= result.get(0);
				if(null!= firstRow){
					totalCount= Integer.parseInt(String.valueOf(firstRow));
				}
			}
			return totalCount;
		}

		@Override
		public List<ColSummaryModel> queryPendingByUserAndApp(
				String userid,
				int appType, 
				int stateType, 
				boolean isTemplate,
				boolean isImportant, 
				boolean isCommon,
				String condition,
				String field,
				String field1) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			List<Object> indexParameter = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select ");
			hql.append(selectAffair);
			condition = StringUtils.defaultString(condition);
			if(!"startMemberName".equals(condition)){
				hql.append(" from Affair as affair,ColSummary as summary ");
				hql.append(" where affair.objectId=summary.id ");
			}else{
				hql.append(" from Affair as affair,ColSummary as summary,").append(V3xOrgMember.class.getName()).append(" as mem");
				hql.append(" where affair.objectId=summary.id and affair.senderId=mem.id ");
			}
			hql.append(" and affair.memberId=").append(userid);
			hql.append(" and affair.state=").append(stateType);
			hql.append(" and affair.app=").append(appType);
			hql.append(" and affair.isDelete=false ");
			if(isTemplate){//模板流程
				hql.append(" and affair.templeteId is not null ");
			}else{//自由流程
				hql.append(" and affair.templeteId is null ");
			}
//			if (isImportant) {//重要
//				hql.append(" and ((summary.importantLevel=2) or (summary.importantLevel=3))");
//			} else if(isCommon){//普通
//				hql.append(" and summary.importantLevel=1 ");
//			}
			if (condition.equals("subject")) {//标题
				hql.append(" and summary.subject like :subject ");
				parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
			}else if (condition.equals("createDate")) {//发起时间
				if (StringUtils.isNotBlank(field)) {
					java.util.Date stamp = Datetimes.getTodayFirstTime(field);
					hql.append(" and affair.createDate >= :timestamp1");
					parameterMap.put("timestamp1", stamp);
				}
				if (StringUtils.isNotBlank(field1)) {
					java.util.Date stamp = Datetimes.getTodayLastTime(field1);
					hql.append(" and affair.createDate <= :timestamp2");
					parameterMap.put("timestamp2", stamp);
				}
			}else if(condition.equals("startMemberName")){//发起人
				hql.append(" and mem.name like :startMemberName");
				parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
			}
			hql.append("  order by affair.receiveTime desc ");
			List result = super.find(hql.toString(), parameterMap, indexParameter);
			List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
			if(null!=result){
				 for (int i = 0; i < result.size(); i++) {
	                Object[] object = (Object[]) result.get(i);

	    			ColSummary summary = new ColSummary();
	    			Affair affair = new Affair();

	    			make(object, summary, affair);

	                //开始组装最后返回的结果�?
	                ColSummaryModel model = new ColSummaryModel();

	                model.setStartDate(new Date(summary.getCreateDate().getTime()));
	                model.setWorkitemId(affair.getObjectId() + "");
	                model.setCaseId(summary.getCaseId() + "");
	                model.setSummary(summary);
	                model.setAffairId(affair.getId());
	                model.setBodyType((String) summary.getBodyType());

	                //协同状�?
	                Integer sub_state = affair.getSubState();
	                if (sub_state != null) {
	                    model.setState(sub_state.intValue());
	                }

	                //是否跟踪
	                Boolean isTrack = affair.getIsTrack();
	                if (isTrack != null) {
	                    model.setIsTrack(isTrack.booleanValue());
	                }

	                //取得转发人姓名
	                model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
	                model.setNodePolicy(affair.getNodePolicy());
	                models.add(model);
	            }
			}
			return models;
		}

		@Override
		public List queryCommonPendingByUserAndApp(String userid,
				int stateType,
				String condition,
				String field,
				String field1) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			List<Object> indexParameter = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select ");
			hql.append(" affair ");
			condition = StringUtils.defaultString(condition);
			if(!"startMemberName".equals(condition)){
				hql.append(" from Affair as affair ");
				hql.append(" where 1=1 ");
			}else{
				hql.append(" from Affair as affair,").append(V3xOrgMember.class.getName()).append(" as mem");
				hql.append(" where affair.senderId=mem.id ");
			}
			hql.append(" and affair.memberId=").append(userid);
			hql.append(" and affair.state=").append(stateType);
			hql.append(" and (affair.app=").append(ApplicationCategoryEnum.bulletin.key());
			//待审核调查
			hql.append(" or (affair.app=").append(ApplicationCategoryEnum.inquiry.key())
			   		.append(" and affair.subApp=").append( ApplicationSubCategoryEnum.inquiry_audit.key())
			   		.append(")");
			//hql.append(" or affair.app=").append(ApplicationCategoryEnum.bbs.key());
			hql.append(" or affair.app=").append(ApplicationCategoryEnum.news.key());
			hql.append(" ) ");
			hql.append(" and affair.isDelete=false ");
			if (condition.equals("subject")) {//标题
				hql.append(" and affair.subject like :subject ");
				parameterMap.put("subject", "%" + SQLWildcardUtil.escape(field) + "%");
			}else if (condition.equals("createDate")) {//发起时间
				if (StringUtils.isNotBlank(field)) {
					java.util.Date stamp = Datetimes.getTodayFirstTime(field);
					hql.append(" and affair.createDate >= :timestamp1");
					parameterMap.put("timestamp1", stamp);
				}
				if (StringUtils.isNotBlank(field1)) {
					java.util.Date stamp = Datetimes.getTodayLastTime(field1);
					hql.append(" and affair.createDate <= :timestamp2");
					parameterMap.put("timestamp2", stamp);
				}
			}else if(condition.equals("startMemberName")){//发起人
				hql.append(" and mem.name like :startMemberName");
				parameterMap.put("startMemberName", "%" + SQLWildcardUtil.escape(field) + "%");
			}
			hql.append("  order by affair.receiveTime desc ");
			List result = super.find(hql.toString(), parameterMap, indexParameter);
			List<ColSummaryModel> models = new ArrayList<ColSummaryModel>();
			if(null!=result){
				 for (int i = 0; i < result.size(); i++) {
					 Affair affair = (Affair) result.get(i);

	    			ColSummary summary = new ColSummary();
	    			summary.setSubject(affair.getSubject());
	    			summary.setStartMemberId(affair.getSenderId());
	    			summary.setCreateDate(affair.getCreateDate());
	    			summary.setBodyType(affair.getBodyType());
	    			summary.setForwardMember(affair.getForwardMember());

	                //开始组装最后返回的结果�?
	                ColSummaryModel model = new ColSummaryModel();

	                model.setStartDate(new Date(affair.getCreateDate().getTime()));
	                model.setWorkitemId(affair.getObjectId() + "");
	                model.setCaseId(affair.getObjectId() + "");
	                model.setSummary(summary);
	                model.setAffairId(affair.getId());
	                model.setBodyType((String) summary.getBodyType());

	                Integer sub_state = affair.getSubState();
	                if (sub_state != null) {
	                    model.setState(sub_state.intValue());
	                }

	                //是否跟踪
	                Boolean isTrack = affair.getIsTrack();
	                if (isTrack != null) {
	                    model.setIsTrack(isTrack.booleanValue());
	                }

	                //取得转发人姓名
	                model.setForwardMemberNames(ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
	                model.setNodePolicy(affair.getNodePolicy());
	                models.add(model);
	            }
			}
			return models;
		}
		public String getDealExplain(String affairId, String templeteId,String processId) {
				String desc = "";
				if(Strings.isBlank(affairId) || Strings.isBlank(templeteId) || Strings.isBlank(processId))
					return desc ;
				try{
//					Templete t = templeteManager.get(Long.valueOf(templeteId));
//					BPMProcess process = BPMProcess.fromXML(t.getWorkflow());
					Affair affair  = affairManager.getById(Long.valueOf(affairId));
//					String activityId = ColHelper.getActvityIdByAffair(affair);
//					BPMActivity activity = process.getActivityById(activityId);
					BPMProcess process = ColHelper.getCaseProcess(processId);
					BPMActivity activity = process.getActivityById(affair.getActivityId().toString());
					desc = activity.getDesc();
					desc= desc.replaceAll("\r\n", "<br>").replaceAll("\r", "<br>").replaceAll("\n", "<br>").replaceAll("\\s", "&nbsp;");
				}catch(Exception e){
					log.error("",e);
				}
				return desc;
		}

		public List<ColSummary> getColSummaryList(
				Long accountId,
				Long templeteId,
				List<Integer> workFlowState, Date startDate, Date endDate,boolean isPaging) {
			return colSummaryDao.getColSummaryList(accountId,
					templeteId, workFlowState, startDate, endDate,isPaging);
		}
		
		/**
		 * startNewflowCaseFromNoFlow
		 * 发起子（新）流程方法(来自无流程表单触发)
		 * @param templateId 子(新)流程所属模板Id
		 * @param senderId 发起者Id
		 * @param formId 表单Id值
		 * @param formMasterId 表单数据记录主键Id值
		 * @param parentFormId 所属父无流程表单Id
		 * @param parentFormMasterId 所属父无流程表单主键记录Id
		 * @param formType: 只能传2和3：2表示基础数据，3表示信息管理
		 * @throws Exception
		 */
		public ColSummary startNewflowCaseFromNoFlow(Long templateId,Long senderId,Long formMasterId,Long parentFormId, Long parentFormMasterId,int formType,boolean isRelated) throws Exception{
			return startNewflowCase(templateId, senderId, formMasterId,null, null, null, parentFormId, parentFormMasterId,formType,isRelated);
		}
	
		/**
		 * startNewflowCaseFromHasFlow
		 * 发起子（新）流程方法(来自有流程表单触发)
		 * @param templateId 子(新)流程所属模板Id
		 * @param senderId 发起者Id
		 * @param formId 表单Id值
		 * @param formMasterId 表单数据记录主键Id值
		 * @param parentSummaryId 所属父协同Id
		 * @param parentNodeId 所属父协同节点Id
		 * @param parentAffairId 所属福协同待办事项Id
		 * @throws Exception
		 */
		public ColSummary startNewflowCaseFromHasFlow(Long templateId,Long senderId,Long formMasterId,Long parentSummaryId,String parentNodeId,Long parentAffairId,boolean isRelated) throws Exception{
			return startNewflowCase(templateId, senderId, formMasterId, parentSummaryId, parentNodeId, parentAffairId, null, null,1,isRelated);
		}
		
		/**
		 * 发起新流程
		 * @param templateId 子(新)流程所属模板Id
		 * @param senderId 发起者Id
		 * @param formMasterId 表单数据记录主键Id值
		 * @param parentSummaryId 所属父协同Id
		 * @param parentNodeId 所属父协同节点Id
		 * @param parentAffairId 所属福协同待办事项Id
		 * @param parentFormId 所属父无流程表单Id
		 * @param parentFormMasterId 所属父无流程表单主键记录Id
		 * @throws Exception
		 */
		private ColSummary startNewflowCase(Long templateId,Long senderId,Long formMasterId,Long parentSummaryId,String parentNodeId,Long parentAffairId,Long parentFormId, Long parentFormMasterId,int formType,boolean isRelated) throws Exception{
	    	Templete templete = templeteManager.get(templateId);
	    	if(templete == null){
	            log.error("发起新流程失败，原因：触发的表单模板已被删除。templateId=" + templateId);
	            throw new BusinessException("发起新流程失败，原因：触发的表单模板已被删除。templateId=" + templateId);
	        }
	    	ColSummary parentSummary=null;
	    	if(isRelated){
	    		if(null!=parentSummaryId && parentSummaryId.longValue()!=-1){
		    		parentSummary= this.getColSummaryById(parentSummaryId, false);
		    	}
	    	}
	    	V3xOrgMember sender = orgManager.getMemberById(senderId);
	    	NewflowRunning runFlow= new NewflowRunning();
    		ColSummary newSummary = (ColSummary)XMLCoder.decoder(templete.getSummary());
    		newSummary.setIsVouch(0);
            newSummary.setIdIfNew();
            newSummary.setOpinions(null);
            Date bodyCreateDate = new Date();                              
            newSummary.setTempleteId(templete.getId());
            //保存附件
            if(templete.isHasAttachments()){
                this.attachmentManager.copy(templete.getId(), templete.getId(), newSummary.getId(), newSummary.getId(), ApplicationCategoryEnum.collaboration.key());//附件
                newSummary.setHasAttachments(true);
            }
            Map<String, Object> options = new HashMap<String, Object>();
            Constant.SendType sendType = Constant.SendType.normal;
            ColOpinion senderOpinion = new ColOpinion();
            senderOpinion.setIdIfNew();
            
            senderOpinion.setContent(Constant.getString4CurrentUser("newflow.fire.opinion"));
            senderOpinion.affairIsTrack = true;
            FlowData flowData = FlowData.flowDataFromXML(templete.getWorkflow());
            String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
            newSummary.setFormAppId(Long.parseLong(formInfo[0]));//保存fromAppid
            newSummary.setFormId(Long.parseLong(formInfo[1]));//保存表单id
            newSummary.setFormRecordId(formMasterId);//保存表单主id
            ColBody body = new ColBody();
            body.setContent(formMasterId+"");
            body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM);
            body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
            newSummary.setOrgDepartmentId(sender.getOrgDepartmentId());
            newSummary.setOrgAccountId(sender.getOrgAccountId());
            String  subject  = Constant.getString4CurrentUser("newflow.fire.subject", templete.getSubject() + "(" + sender.getName() + " " + Datetimes.formatDatetimeWithoutSecond(bodyCreateDate) + ")"); 
        	if(!Strings.isBlank(templete.getCollSubject())){
        		String formData = FormHelper.getFormRun4ColSubject(sender.getId(), sender.getName(), sender.getLoginName(),formInfo[0], formInfo[1], 
        				formInfo[2], newSummary.getFormRecordId()+"", newSummary.getId()+"", null, formInfo[3], false) ;
        		formData = this.getColSubjectXML(formData)  ;                           	                   	                           		
        		subject = FormHelper.getCollSubjuet(formInfo[0], templete.getCollSubject(),formData,formInfo[1], formInfo[2],true) ;        		
           		if(Strings.isNotBlank(subject)){
           			subject = Strings.getLimitLengthString(Constant.getString4CurrentUser("newflow.fire.subject",subject), 160, "...") ; 
           		}else{
           			subject = Constant.getString4CurrentUser("newflow.fire.subject",templete.getSubject() + "(" + sender.getName() + " " + Datetimes.formatDatetimeWithoutSecond(bodyCreateDate) + ")"); 
           		}                           		       		
       	    }
        	newSummary.setSubject(subject);
        	Long newAffairId = 0L;
            try {
                String newProcessId = null;
                
                //改变流程，增加自动节点
                newProcessId = ColHelper.saveOrUpdateProcessByXML(templete.getWorkflow(), null, flowData.getAddition(), flowData.getCondition(), sender);
                BPMProcess newProcess = ColHelper.getProcess(newProcessId);
                V3xOrgAccount account = orgManager.getAccountById(sender.getOrgAccountId());
                ColHelper.changeProcess4Newflow(newProcess, flowData, sender, account.getShortname());
                newSummary.setProcessId(newProcessId);
                List<NewflowSetting> newflowList = null;
                if(isRelated){
                	newSummary.setNewflowType(Constant.NewflowType.child.ordinal()); //标记为子流程
                }else{               	
                    //NF 如果是表单，并设置了新流程，则标记ColSummary为主流程.
                    if("FORM".equals(body.getBodyType())){
                        newflowList = newflowManager.getNewflowSettingList(newSummary.getTempleteId());
                        if(newflowList != null && !newflowList.isEmpty()){
                        	newSummary.setNewflowType(Constant.NewflowType.main.ordinal());
                        }
                    }
                }
                newAffairId = this.runCase(flowData, newSummary, body, senderOpinion, sendType, options, true, senderId, newProcessId);
                if(newAffairId.longValue() == -1){
                    return null;
                }else{//NF 如果是表单，并设置了新流程，则拷贝当前模板的新流程设置信息到新流程运行表
                    if("FORM".equals(body.getBodyType()) && newflowList != null && !newflowList.isEmpty()){
                        newflowManager.copyNewflowInfo(newflowList, newSummary.getId(), newAffairId);
                    }
                }
                //处理表单状态等信息存储
                formDaoManager.saveDataState(newSummary, 1, -1);
                if(isRelated){
                	boolean isActivate = newAffairId != -1L;
                	if( null!=parentSummary ){//有流程
                    	runFlow.setMainTempleteId(parentSummary.getTempleteId());
                    	runFlow.setMainNodeId(parentNodeId);
                    	runFlow.setMainSummaryId(parentSummary.getId());
                    	runFlow.setMainCaseId(parentSummary.getCaseId());
                    	runFlow.setMainProcessId(parentSummary.getProcessId());
                        runFlow.setMainFormId(parentSummary.getFormId());
                        runFlow.setMainAffairId(parentAffairId);
                    }else{//无流程
                    	runFlow.setMainFormId(parentFormId);
                        runFlow.setMainRecordId(parentFormMasterId);
                    }
                    runFlow.setMainFormType(formType);
                    runFlow.setSenderId(senderId);
                    runFlow.setSummaryId(newSummary.getId());
                    runFlow.setCaseId(newSummary.getCaseId());
                    runFlow.setProcessId(newSummary.getProcessId());
                    runFlow.setTempleteId(newSummary.getTempleteId());
                    runFlow.setAffairId(newAffairId);
                    runFlow.setAffairState(StateEnum.col_sent.key());
                    runFlow.setUpdateTime(new Date());
                    runFlow.setIsActivate(isActivate);
                    runFlow.setIsCanViewByMainFlow(true);
                    runFlow.setIsCanViewMainFlow(true);  
                    runFlow.setIdIfNew(); 
                    super.save(runFlow); 
//                    newflowManager.updateNewflowRunning(runFlow); 
                }
                //协同发起事件通知
                CollaborationStartEvent event = new CollaborationStartEvent(this);
                event.setSummaryId(newSummary.getId());
                event.setFrom("pc");
                event.setAffairId(newAffairId);
                EventDispatcher.fireEvent(event);
                //保存新流程的督办信息
                getColSuperviseManager().copyAndSaveSuperviseFromTemplete(sender, newSummary, templete.getId());
                //全文检索入库
                DateSharedWithWorkflowEngineThreadLocal.setNoIndex();
                if(IndexInitConfig.hasLuncenePlugIn()){
                	try {
                		indexManager.index(((IndexEnable)this).getIndexInfo(newSummary.getId()));
					}
					catch (Exception e) {
						log.warn(e.getMessage());
					}
                } 
            } 
            catch (Exception e) {
                log.error("新流程触发自动发起协同异常", e);
                throw new ColException("新流程触发自动发起协同异常", e);
            } 
            
            //触发新流程，发送系统消息 ： 来自《主流程标题》的子流程《子流程标题》已经发起
            if( null!=parentSummary && isRelated){
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                Affair senderAffair  = affairManager.getCollaborationSenderAffair(parentSummary.getId());
                if(runFlow.getIsCanViewByMainFlow()){//能被主流程查看，就能打开连接
                	receivers.add(new MessageReceiver(senderAffair.getId(), senderAffair.getMemberId(),"message.link.col.done",newAffairId,0));
                }else{
                	receivers.add(new MessageReceiver(senderAffair.getId(), senderAffair.getMemberId()));
                }
                userMessageManager.sendSystemMessage(new MessageContent("col.workflow.new.start", parentSummary.getSubject(),newSummary.getSubject())
            	,ApplicationCategoryEnum.collaboration, sender.getId(), receivers);
            }else{
            	User user= new User();
            	user.setId(sender.getId());
            	user.setName(sender.getName());
            	user.setDepartmentId(sender.getOrgDepartmentId());
            	user.setLoginAccount(sender.getOrgAccountId());
            	user.setRemoteAddr("127.0.0.1");//A8服务器地址
            	//发送协同操作日志
                appLogManager.insertLog(user, AppLogAction.Coll_New, user.getName(), newSummary.getSubject());
                //流程日志
                if("FORM".equals(body.getBodyType())){
                	BPMProcess process = ColHelper.getRunningProcessByProcessId(newSummary.getProcessId());
                	String  members = ColHelper.checSecondNodeMembers(process,flowData.getCondition());
                	processLogManager.insertLog(user, Long.parseLong(newSummary.getProcessId()), -1l, ProcessLogAction.sendForm,members);
                }else{
                	processLogManager.insertLog(user, Long.parseLong(newSummary.getProcessId()), -1l, ProcessLogAction.sendColl);
                }
            }
            
            return newSummary;
	    }
		/**
		 * 获得协同标题
		 * @param xml 表单xml数据
		 * @return
		 * @throws Exception
		 */
		public String getColSubjectXML(String xml)throws Exception{
	    	if(Strings.isBlank(xml)){
	    		log.error("getColSubjectXML：xml is null" ) ;
	    		return "" ;
	    	}
	    	int dataIndex = xml.indexOf("&&&&&&&&  data_start  &&&&&&&&") ;
	    	if(dataIndex == -1){
	    		log.error("getColSubjectXML：xml 中不包含 &&&&&&&&  data_start  &&&&&&&&" ) ;
	    		return "" ;
	    	}
	    	int inputStart = xml.indexOf("&&&&&&&&  input_start  &&&&&&&&") ;    	
	    	if(inputStart ==-1){
	    		log.error("getColSubjectXML：xml 中不包含 &&&&&&&&  data_start  &&&&&&&&" ) ;
	    		return "" ;
	    	}
	    	xml = xml.substring(dataIndex+30, inputStart);
	        StringBuffer str = new  StringBuffer() ;
	  	    for(int i = 0 ; i < xml.length() ; i++){		  
	  		  char ch = xml.charAt(i) ;
	      	  if(ch == '<'){   		  
	      		  str.append(xml.substring(i, xml.length()));
	      		  break ;
	      	  }
	        }
	     return str.toString();
	    }
		@Override
		public String hasConditionAfterSelectNode(String processXml,
				String currentNodeId) throws Exception {
			BPMProcess bpmProcess= BPMProcess.fromXML(processXml);
			BPMActivity currentNode= bpmProcess.getActivityById(currentNodeId);
			boolean result = "1".equals(currentNode.getSeeyonPolicy().getNF());
			if(!result){
				result= BranchArgs.hasConditionOrSelectForSkipVerify(currentNode);
			}
			return String.valueOf(result);
		}
		@Override
		public Integer getCaseCountByTempleteId(
				Long accountId,
				Long templeteId,
				List<Integer> workFlowState, Date startDate, Date endDate) {
			return colSummaryDao.getCaseCountByTempleteId(
					accountId,
					templeteId, workFlowState, startDate, endDate);
		}
		@Override
		public Integer getAvgRunWorkTimeByTempleteId(
				Long accountId,
				Long templeteId,
				List<Integer> workFlowState, Date startDate, Date endDate) {
			return colSummaryDao.getAvgRunWorkTimeByTempleteId(
					accountId,
					templeteId, 
					workFlowState, 
					startDate, 
					endDate);
		}
		@Override
		public Integer getCaseCountGTSD(
				Long accountId,
				Long templeteId,
				List<Integer> workFlowState, Date startDate, Date endDate,
				Integer standarduration) {
			return colSummaryDao.getCaseCountGTSD(
					accountId,
					templeteId, 
					workFlowState, 
					startDate, 
					endDate, 
					standarduration);
		}
		@Override
		public Double getOverCaseRatioByTempleteId(Long accountId,
				Long templeteId, List<Integer> workFlowState, Date startDate,
				Date endDate) {
			return  colSummaryDao.getOverCaseRatioByTempleteId(
					accountId, 
					templeteId, 
					workFlowState, 
					startDate, 
					endDate);
		}
		@Override
		public String[] hasAutoSkipNodeBeforeSetCondition(String processXml,
				String currentLinkId) throws Exception {
			String[] result= new String[2];
			if(null==currentLinkId || "".equals(currentLinkId.trim())){
				result[0]= String.valueOf(false);
				result[1]= "";
				return result;
			}
			BPMProcess bpmProcess= BPMProcess.fromXML(processXml);
			BPMTransition currentTransaction= bpmProcess.getLinkById(currentLinkId);
			result= BranchArgs.hasAutoSkipNodeBeforeSetCondition(currentTransaction);
			return result;
		}
		@Override
		public String isAutoSkipBeforeNewSetFlowOfNode(String processXml,
				String currentNodeId) throws Exception {
			boolean result= false;
			BPMProcess bpmProcess= BPMProcess.fromXML(processXml);
			BPMActivity currentNode= bpmProcess.getActivityById(currentNodeId);
			BPMSeeyonPolicy policy= currentNode.getSeeyonPolicy();
			String dealTermType= policy.getDealTermType();
			String dealTerm= policy.getdealTerm();//是否选择处理期限
			if( null!=dealTermType && "2".equals(dealTermType.trim()) 
					&& null!=dealTerm && !"".equals(dealTerm) && !"0".equals(dealTerm) ){//自动跳过节点
				result= true;
			}
			return String.valueOf(result);
		}
		@Override
		public Integer getAffairState(Long affairId) {
			if(affairId!=null && Strings.isNotBlank(affairId.toString())) {
				Affair affair = affairManager.getById(affairId);
				if(affair!=null)
					return affair.getState();
			}
			return 0 ;
		}
		@Override
		public void setTrack(Long affairId, boolean isTrack, String trackMembers) {
			List<Long> members = new ArrayList<Long>();
	         if(Strings.isNotBlank(trackMembers)){
	         	String[] m = trackMembers.split(",");
	         	for(String s : m){
	         		members .add(Long.valueOf(s));
	         	}
	         }
	         this.changeTrack(affairId, isTrack,members);
		}
		
	    public void updateFormTriggerStatus(Long summaryId, boolean isHasFormTrigger) {
	    	try{
	    		ColSummary summary = getColSummaryById(summaryId, false);
	    		if(summary!=null){
		    		summary.setHasFormTrigger(isHasFormTrigger);
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("identifier", summary.getIdentifier());
					param.put("summaryId", summaryId);
		        	super.bulkUpdate("update ColSummary set identifier=:identifier where id=:summaryId", param);
	    		}
	    	}catch(Exception e){
	    		logger.error(e.getMessage(), e);
	    	}
	    }
	    /**
	     * AJAX调用
	     * @param currentMemberId
	     * @param memberId
	     * @return
	     */
	    public  String checkLevelScope(Long memberId){
	    	User user = CurrentUser.get();
	    	boolean canView = Functions.checkLevelScope(user.getId(), memberId);
	    	return canView ?"Y":"N";
	    }
		@Override
		public void finishWorkItem(Affair affair, ColOpinion signOpinion,
				Map<String, String[]> map, Map<String, String> condition,
		BPMProcess process, User user, ColSummary summary) throws ColException {
			if(affair==null || affair.getState()!=StateEnum.col_pending.key()){
			    String msg = ColHelper.getErrorMsgByAffair(affair);
			    throw new ColException(msg);
			}
			Long summaryId = affair.getObjectId();
	        if (summaryId == null) {
	            throw new ColException("summaryId is not found or you are not allowed to treat it. summaryId is " + summaryId);
	        }
			Long workItemId = null;
			if(affair.getSubObjectId() != null){
				workItemId = affair.getSubObjectId();
			}
	        else{
				throw new ColException("workitemId is not found or you are not allowed to treat it. workitemId is not found.");
			}
			
	        finishWorkItem(workItemId, affair, summaryId, signOpinion,map, condition, process, user);
			
		}
		
		public void clearSession(){
			super.getSession().clear();
			}
	
}