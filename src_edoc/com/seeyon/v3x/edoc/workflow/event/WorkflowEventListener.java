package com.seeyon.v3x.edoc.workflow.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMAndRouter;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.engine.event.EventListenerManager;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.execute.ActionRunner;
import net.joinwork.bpm.engine.execute.BPMCase;
import net.joinwork.bpm.engine.wapi.ProcessDefManager;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.common.workflow.engine.org.WorkFlowOrgManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocMessageHelper;
import com.seeyon.v3x.edoc.manager.EdocSuperviseManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.workflow.event.BaseAbstractEventListener;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.ProcessModeSelector;
import com.thoughtworks.xstream.XStream;


/**
 * User: lius
 * Date: 2006-11-15
 * Time: 15:52:24
 */
public class WorkflowEventListener extends BaseAbstractEventListener {
	private final static Log log = LogFactory.getLog(WorkflowEventListener.class);
    private AffairManager affairManager = null;
    private EdocManager edocManager = null;
    private OrgManager orgManager;
    private UserMessageManager userMessageManager = null;
    private EdocSuperviseManager edocSuperviseManager = null;
    private DocHierarchyManager docHierarchyManager;

	private ProcessLogManager processLogManager;
    private AppLogManager appLogManager;

	public WorkflowEventListener() {

    }
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
    public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
    //回退
    /*public static final Integer WITHDRAW = 1;
    //取回
    public static final Integer TAKE_BACK = 2;
    //知会
    public static final Integer ADD_INFORM = 3;
    //会签
    public static final Integer COL_ASSIGN = 4;
    //加签
    public static final Integer INSERT = 5;
    //减签
    public static final Integer DELETE = 6;
    //分配任务
    public static final Integer ASSIGN = 7;
    
    //终止
    public static final Integer STETSTOP = 8;
    
    //  正常处理
    public static final Integer COMMONDISPOSAL = 9;
    //撤销
    public static final Integer CANCEL = 10;
    //暂存待办
    public static final Integer ZCDB = 11;
    //默认删除操作
    public static final Integer AUTODELETE = 12;*/

    public static Integer getOperationType() {
        return DateSharedWithWorkflowEngineThreadLocal.getOperationType();
    }

    public static void setOperationType(Integer obj) {
    	DateSharedWithWorkflowEngineThreadLocal.setOperationType(obj);
    }
    
    public static EdocSummary getEdocSummary() {
        return (EdocSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    }

    public static void setEdocSummary(EdocSummary summary) {
    	DateSharedWithWorkflowEngineThreadLocal.setColSummary(summary);
    }
    
    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
    public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
    
    public static void setFinishAffairId(Long affairId){
    	DateSharedWithWorkflowEngineThreadLocal.setFinishAffairId(affairId);
    }
    
    public static Long getFinishAffairId(){
    	return DateSharedWithWorkflowEngineThreadLocal.getFinishAffairId();
    }

    /**
     * * see  Dispatcher.onCaseFinish
     *
     * @param eventData
     * @return
     * @throws BPMException
     */
    protected boolean onProcessFinished(Map eventData) throws BPMException {
        //只有ActionRunner.SYSDATA_CASE这个key
        BPMCase theCase = (BPMCase) eventData.get(ActionRunner.SYSDATA_CASE);
        long caseId = theCase.getId();
        
        Integer operationType = WorkflowEventListener.getOperationType();
        int summaryState = WorkflowEventListener.STETSTOP.equals(operationType) ? Constant.flowState.terminate.ordinal() : Constant.flowState.finish.ordinal();
        
        try {
        	EdocSummary summary= (EdocSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
        	if(summary == null){
        		summary = edocManager.getSummaryByCaseId(caseId);
        	}
        	boolean needdelete = true;
        	
        	if(summary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()
        			&&summary.getFinished() 
        			&& summary.getHasArchive())
        		needdelete = false;
        	
        	Long summaryId = edocManager.getSummaryIdByCaseId(caseId);
        	setTime2Summary();
            edocManager.setFinishedFlag(summaryId, summaryState,summary.getRunTime(),summary.getRunWorkTime(),summary.getOverTime(),summary.getOverWorkTime());
            edocSuperviseManager.updateBySummaryId(summaryId);
            affairManager.updateFinishFlag(summaryId);
            
            summary.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            summary.setState(summaryState);
          
            if(summaryState == Constant.flowState.finish.ordinal()){ //流程结束不是终止
	            //发文：流程结束，  如果设置了预归档目录，则直接归档到该目录中
	            Long affairId=getFinishAffairId();
	            if(affairId != null){
	            	Affair affair = affairManager.getById(affairId);
		            if(summary.getEdocType() == 0){  //发文
			            if(summary.getArchiveId()!= null  && !summary.getHasArchive() && summary.getTempleteId()!=null){
			            	edocManager.pigeonholeAffair("", affair, summaryId,summary.getArchiveId(),false);
			            	 // 流程日志
			        	    try{
			        	    	User user = CurrentUser.get();
			        		    String params = summary.getSubject() ;
			        		    BPMProcess bPMProcess = EdocHelper.getCaseProcess(summary.getProcessId());
			        		    Long activityId = affair.getActivityId();
			        		    if(activityId==null){
			        		    	BPMActivity bPMActivity = EdocHelper.getBPMActivityByAffair(affair);//当前节点
			        		    	if(bPMActivity != null)
			        		    		activityId = Long.valueOf(bPMActivity.getId());
			        		    }
			        		    if(activityId != null){
			        		    	processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), activityId.longValue(), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.pigeonhole.getKey()),params);
			        		    }else {
			        		    	processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), -1l, ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.pigeonhole.getKey()),params);
			        		    }
			        		    appLogManager.insertLog(user, AppLogAction.Edoc_PingHole, user.getName() ,summary.getSubject()) ;    
			        		    }catch(Exception e){
			        	    	log.error("公文自动归档，记录流程日志",e);
			        	    }
			            }
		            }	            
		            //发文：发文在归档并且流程结束后在已办、已发列表中删除
		            if(summary.getHasArchive() && summary.getArchiveId() != null && needdelete){
		            	edocManager.setArchiveIdToAffairsAndSendMessages(summary,affair,true);
		            }
	            }
            }
            //清空消息跟踪设置表中的数据
            try{
            	edocManager.deleteColTrackMembersByObjectId(summary.getId());
            }catch(Exception e){
            	log.error(e);
            }
        }
        catch (Exception e) {
            log.error("",e);
        }
        
        return super.onProcessFinished(eventData);
    }

    //如果发了消息，返回true
    //如果无须发消息，返回false
    private boolean notifyAction(Affair affair, boolean sendToTracker) {
        return false;
        /* Integer operation = getOperationType();
       if (operation == null) {
           return false;
       }

       if (affair == null) {
           return false;
       }

       Long memberId = affair.getMemberId();
       if (memberId == null) {
           return false;
       }

       String key = "";
       if (WITHDRAW.equals(operation)) {
           key = "col.stepback";
       } else if (TAKE_BACK.equals(operation)) {
           key = "col.takeback";
       }
//        else if(ASSIGN.equals(operation)) {
//             key = "col.send";
//        }
       else {
           return false;
       }
//  else if (ADD_INFORM.equals(operation)) {
//            oper = "知会";
//        } else if (COL_ASSIGN.equals(operation)) {
//            oper = "会签";
//        } else if (INSERT.equals(operation)) {
//            oper = "加签";
//        } else if (DELETE.equals(operation)) {
//            oper = "减签";
//        }

       Long summaryId = affair.getObjectId();
       ColSummary summary = null;
       try {
           summary = colManager.getColSummaryById(summaryId, false);
       } catch (ColException e) {
           summary = null;
       }
       if (summary == null) {
           return true;
       }

       Long affairId = affair.getId();
       User user = CurrentUser.get();
       String userName = "";
       if (user != null) {
           userName = user.getName();
       }

       if (userMessageManager == null) {
           userMessageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
       }

//        String subject = "协同�? + affair.getSubject() + "》已经被" + userName + oper;
       try {
//            userMessageManager.sendUserMessage(subject, Constants.MESSAGE_TYPE_COLLABORATION, user.getInternalId(), memberId);
           List<Affair> trackingAffairList = affairManager.getTrackingAffairBySummaryId(summaryId);
           for (Affair trackingAffair : trackingAffairList) {
               userMessageManager.sendSystemMessage(new MessageContent(key, affair.getSubject(), userName), ApplicationCategoryEnum.collaboration, user.getId(), new MessageReceiver(trackingAffair.getMemberId()));
           }
           userMessageManager.sendSystemMessage(new MessageContent(key, affair.getSubject(), userName), ApplicationCategoryEnum.collaboration, user.getId(), new MessageReceiver(memberId, "message.link.col_done", affairId.toString()));
       } catch (MessageException e) {
           e.printStackTrace();
           logger.error("send message failed", e);
       }
       return true;*/
    }

    private static WorkflowEventListener listener = null;

    public void init() {
//        if(listener == null && this.getClass().getName().contains("cglib")){
//        if(listener == null){
//            listener = this;
//            listener.registerMe();
//        }
        this.registerMe();

    }


    public void registerMe() {
        EventListenerManager.registerEdoc(this);
    }

    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }


    public void setEdocManager(EdocManager edocManager) {
        this.edocManager = edocManager;
    }
    /*

    public static class PersonInfo {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ProcessModeSelector {
        public List<NodeAddition> nodeAdditions;
        //0:直接通过；1：调选人界面；2：从nodeAdditions里选择人。
        //目前需要弹选人界面或从指定列表选人（即需要popup窗口的情况下，都为2），不弹窗口为0
        public int mode;

        //调选人界面后更新的节点id(activityID)
        public String nodeId;

        public List<NodeAddition> getNodeAdditions() {
            return nodeAdditions;
        }

        public void setNodeAdditions(List<NodeAddition> nodeAdditions) {
            this.nodeAdditions = nodeAdditions;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }
    }

    public static class NodeAddition {
        private String nodeId;
        private String nodeName;
        private String processMode;
        private List<PersonInfo> people;

        public String getProcessModeName() {
            if ("multiple".equals(processMode)) {
                return "多人";
            } else if ("competition".equals(processMode)) {
                return "竞争";
            } else {
                return "单人";
            }
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public List<PersonInfo> getPeople() {
            return people;
        }

        public void setPeople(List<PersonInfo> people) {
            this.people = people;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getProcessMode() {
            return processMode;
        }

        public void setProcessMode(String processMode) {
            this.processMode = processMode;
        }
    }

    */
    public static String serializeProcessModeSelector(ProcessModeSelector selector) {
        XStream xStream = new XStream();
        String xml = xStream.toXML(selector);
        return xml;
    }

    public static ProcessModeSelector deserializeProcessModeSelector(String xml) {
        XStream xStream = new XStream();
        ProcessModeSelector selector = (ProcessModeSelector) xStream.fromXML(xml);
        return selector;
    }
    
    
    /**
     * 移动到父类
     */
    /*public static Map<Long, Affair> affairMap = new HashMap<Long, Affair>();
    
    protected boolean onWorkitemAssigned(Map eventData) throws BPMException {
    	List<WorkItem> workitems = (List<WorkItem>)(((Map)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA)).get("workitems"));

    	EdocBody edocBody=null;
    	try {
			//控制是否发送协同发起消息
			Boolean _isSendMessage = (Boolean)eventData.get("isSendMessage");
			Boolean isSendMessage = true;
			if(_isSendMessage != null && !_isSendMessage) isSendMessage = false;
			
			BPMCase theCase = (BPMCase) eventData.get(ActionRunner.SYSDATA_CASE);
	        long caseId = theCase.getId();
	        
	        String processId = theCase.getProcessId();
	        BPMProcess _process = (BPMProcess) eventData.get("real_process");
	        if(_process == null){
		        try{
		        	ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
			        _process = (BPMProcess)pdm.getProcessInReady("admin", processId);
			        if(_process == null){
			        	try{
			        		_process = ColHelper.getRunningProcessByProcessId(processId);
			        	}catch(ColException e){
			    			throw new ColException("获取runningProcess异常[processId ="+ processId+ "]", e);
			        	}
			        }
		        }catch(BPMException e){
		        	log.error("获取流程定义管理接口对象失败", e);
		        	throw new BPMException("获取流程定义管理接口对象失败", e);
		        }
	        }
	        
	        BPMActivity activity = (BPMActivity)eventData.get("real_activity");
	        BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
	        
	        EdocSummary summary = getEdocSummary();
	        if(summary == null){
	            try {
	                summary = edocManager.getSummaryByCaseId(caseId);
	                setEdocSummary(summary);
	            } catch (EdocException e) {
	            	throw new BPMException("获取流程对应的公文对象失败", e);
	            }
	        }
	        
	        Long senderId = summary.getStartMember().getId();
	        Long objectId = summary.getId();
	        	
	        Long deadline = null;
	        Long remindTime = null;
	        if(!("").equals(seeyonPolicy.getdealTerm()) && seeyonPolicy.getdealTerm() != null){
	        	deadline = Long.parseLong(seeyonPolicy.getdealTerm());
	        }
	        if(!("").equals(seeyonPolicy.getRemindTime()) && seeyonPolicy.getRemindTime() != null
	        		&& !("undefined").equals(seeyonPolicy.getRemindTime())){
	        	remindTime = Long.parseLong(seeyonPolicy.getRemindTime());
	        }        
	        
            java.sql.Timestamp createDate = summary.getCreateTime();            
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            edocBody=summary.getFirstBody();
            
            
            List<Affair> affairs = new ArrayList<Affair>(workitems.size());
            
            for (WorkItem workitem : workitems) {
    			Long memberId = Long.parseLong(workitem.getPerformer());
    			
	            Affair affair = new Affair();
	            affair.setIdIfNew();
	            
	            affair.setIsTrack(false);
	            affair.setIsDelete(false);
		    	affair.setSubObjectId(new Long(workitem.getId()));
		    	affair.setMemberId(memberId);
		    	affair.setState(StateEnum.col_pending.key());
		    	affair.setSubState(SubStateEnum.col_pending_unRead.key());
				affair.setSenderId(senderId);
				affair.setSubject(summary.getSubject());
				affair.setObjectId(objectId);
				affair.setDeadlineDate(deadline);
				affair.setRemindDate(remindTime);
				affair.setReceiveTime(now);
				affair.setApp(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()).key());
				affair.setCreateDate(createDate);
				affair.setIsSendMessage(isSendMessage);
				//页面显示紧急程度。
				if(summary.getUrgentLevel()!=null&&!"".equals(summary.getUrgentLevel())){
					affair.setImportantLevel(Integer.parseInt(summary.getUrgentLevel()));
				}
				affair.setBodyType(summary.getFirstBody().getContentType());
				affair.setHasAttachments(summary.isHasAttachments());
				
				affair.setNodePolicy(seeyonPolicy.getId());
				affair.setActivityId(Long.parseLong(workitem.getActivityId()));
				affair.setFormAppId(seeyonPolicy.getFormApp());
				affair.setFormId(seeyonPolicy.getForm());
				affair.setFormOperationId(seeyonPolicy.getOperationName());
				
				//回退导致新生成的事项
				if(!isSendMessage){
					affairMap.put(memberId, affair);
				}
				
				affairs.add(affair);
            }
    		
			AffairData affairData = new AffairData();
			
			affairData.setAffairList(affairs);
			affairData.setIsSendMessage(affairs.get(0).getIsSendMessage());
			affairData.setSender(affairs.get(0).getSenderId());
			affairData.setBodyType(edocBody.getContentType());
			affairData.setBodyContent(edocBody.getContent());
			affairData.setBodyCreateDate(edocBody.getCreateTime());
    		
    		try {
    			ListMapTask.run1(affairData);
			}
    		catch (Exception e) {
				log.error("", e);
			}
		}
    	catch (Exception e) {
			log.error(BPMException.EXCEPTION_CODE_DATA_FORMAT_ERROR, e);
			throw new BPMException(BPMException.EXCEPTION_CODE_DATA_FORMAT_ERROR, e);
		}
    	
    	return true;
    }*/
    
    protected boolean cycEndEvent(Map eventData) throws BPMException {
    	return false;
    }

    protected boolean onWorkitemMessage2(Map eventData) throws BPMException {
        return super.onWorkitemMessage2(eventData);
    }

    protected boolean onWorkitemMessage1(Map eventData) throws BPMException {
        return super.onWorkitemMessage1(eventData);
    }

    protected boolean onWorkitemSuspended(Map eventData) throws BPMException {
        return super.onWorkitemSuspended(eventData);
    }

    protected boolean onWorkitemResumed(Map eventData) throws BPMException {
        return super.onWorkitemResumed(eventData);
    }

    /**
     * 移动到父类
     */
    /*protected boolean onWorkitemCanceled(Map eventData) throws BPMException {
        int operationType = getOperationType();
    	WorkItem workitem = (WorkItem) eventData.get(ActionRunner.SYSDATA_WORKITEM);
    	BPMActivity currentActivity = ColHelper.getActivityByWorkitem(workitem);
    	if(currentActivity == null){
    		setOperationType(AUTODELETE);
        	operationType = 12;
    	}else{
	        BPMSeeyonPolicy policy = currentActivity.getSeeyonPolicy();
	        String processMode = policy.getProcessMode();
	        if(operationType == 9 && !"competition".equals(processMode)){
	        	setOperationType(AUTODELETE);
	        	operationType = 12;
	        }
    	}
    	
    	Timestamp now = new Timestamp(System.currentTimeMillis());
        
    	if(operationType == TAKE_BACK || operationType == WITHDRAW){ //
    		List<WorkItem> workItems = (List<WorkItem>)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA);
    		if(workItems != null){
    			Affair affair = affairManager.getBySubObject(Long.parseLong(workitem.getId()+""));
    	        if (affair == null)
    	            return false;
    	        
    			int MaxCommitNumber = 300;
    			int length = workItems.size();
    			List<Long> workitemIds = new ArrayList<Long>();
    			int i = 0;
    			int state = operationType == TAKE_BACK ? StateEnum.col_takeBack.key() : StateEnum.col_stepBack.key();
    			for (WorkItem workItem0 : workItems) {
    				workitemIds.add((long)workItem0.getId());
    				i++;
    				
    				if(i % MaxCommitNumber == 0 || i == length){
    					Map<String, Object> nameParameters = new HashMap<String, Object>();
    					nameParameters.put("subObjectId", workitemIds);
    					affairManager.bulkUpdate("update " + Affair.class.getName() + " set state=?,subState=?,updateDate=? where objectId=? and (subObjectId in (:subObjectId))", 
    							nameParameters, state, SubStateEnum.col_normal.key(), now, affair.getObjectId());
    					
    					workitemIds = new ArrayList<Long>();
    				}
				}
    			
    			return false;
    		}
    	}
        
        Affair affair = eventData2ExistingAffair(eventData);
        if (affair == null)
            return false;
        boolean executeSingleUpdate = true;
        if(operationType == 9){
        	affair.setState(StateEnum.col_competeOver.key());
        	affair.setSubState(SubStateEnum.col_normal.key());
        	//给在竞争执行中被取消的affair发送消息提醒
        	EdocMessageHelper.competitionCancel(affairManager, orgManager, userMessageManager, workitem, affair);
        }
        else if(operationType == 8){
        	throw new UnsupportedOperationException(affair.getId() + ", " + affair.getSubject());
        }
        else if(operationType == 11){
        	affair.setState(StateEnum.col_competeOver.key());
        	affair.setSubState(SubStateEnum.col_normal.key());
        	//给在竞争执行中被取消的affair发送消息提醒
        	EdocMessageHelper.competitionCancel(affairManager, orgManager, userMessageManager, workitem, affair);
        }
        else if(operationType == 12){
        	//删除被替换的所有affair事项
        	List<Affair> affairs = new ArrayList<Affair>();
        	if(eventData.get(ActionRunner.SYSDATA_EXTEND_DATA) != null){
        		if(eventData.get(ActionRunner.SYSDATA_EXTEND_DATA) instanceof List){
        			affairs = this.superviseCancel((List)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA),now);
        			executeSingleUpdate = false;
        		}
        	}
        	//给在督办中被删除的affair发送消息提醒
        	if(affairs.isEmpty()){
        		affairs.add(affair);
        	}
        	EdocMessageHelper.superviseDelete(affairManager, orgManager, userMessageManager, workitem, affairs);
        }
        
        if(executeSingleUpdate){
        	affair.setUpdateDate(now);
        	affairManager.updateAffair(affair);
        }
        return false;
    }*/

    protected boolean onWorkitemFinished(Map eventData) throws BPMException {
        Affair affair = eventData2ExistingAffair(eventData);
        if (affair == null)
            return false;
        affair.setState(StateEnum.col_done.key());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        affair.setCompleteTime(now);
        //设置运行时长，超时时长等
        setTime2Affair(affair);
        affairManager.updateAffair(affair);

        Long summaryId = affair.getObjectId();
        int operationType = getOperationType();
        if(operationType == 8){
        	return false;
        }else{
//      发送完成事项消息提醒
        Boolean ok = EdocMessageHelper.workitemFinishedMessage(affairManager, orgManager, edocManager, userMessageManager, affair, summaryId);
        }
        return false;
    }


    /**
     * @param eventData
     * @return
     * @throws BPMException
     */
    protected boolean onActivityFinished(Map eventData) throws BPMException {
        return super.onActivityFinished(eventData);
    }

    /**
     * 移动到父类
     */
    //通过workitemId得到affair
    /*private Affair eventData2ExistingAffair(Map eventData) {
        WorkItem workitem = (WorkItem) eventData.get(ActionRunner.SYSDATA_WORKITEM);
        int operationType = getOperationType();
        Affair affair = affairManager.getBySubObject(Long.parseLong(workitem.getId()+""));
        if(affair == null){
        	log.warn("不能通过workitem取到affair，workitem id："+workitem.getId());
        }
        switch(operationType){
        	case 1 :  
        		affair.setState(StateEnum.col_stepBack.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 2 :
        		affair.setState(StateEnum.col_takeBack.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 10 :
        		affair.setState(StateEnum.col_cancel.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 9 :
        		affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	break;
        	case 8 :
        		affair.setState(StateEnum.col_done.key());
            	affair.setSubState(SubStateEnum.col_done_stepStop.key());
            	affair.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            	break;
        	case 12 :
        		affair.setState(StateEnum.col_cancel.key());
            	affair.setSubState(SubStateEnum.col_normal.key());
            	affair.setIsDelete(true);
        }
        return affair;
    }*/

    /**
     * 找到指定节点的所有直接子节点中，party为动态角色的�?
     *
     * @param currentActivity
     * @return
     */
    private List<BPMActivity> findNextDynamicActivity(BPMActivity currentActivity) {
        List<BPMActivity> dynamicActivities = new ArrayList<BPMActivity>();
        List<BPMTransition> transitions = currentActivity
                .getDownTransitions();
        if (transitions == null)
            return dynamicActivities;
        for (BPMTransition trans : transitions) {
            BPMAbstractNode node1 = trans.getTo();
            if (node1 instanceof BPMHumenActivity) {
                BPMHumenActivity humen = (BPMHumenActivity) node1;
                List<BPMActor> actors = humen.getActorList();
                if (actors == null) {
                    continue;
                }
                for (BPMActor actor : actors) {
                    if (actor.getParty().getType().id
                            .equals(WorkFlowOrgManager.PARTY_TYPE_DYNAMIC_ROLE.id)) {
                        dynamicActivities.add(humen);
                    }
                }
            } else if (node1 instanceof BPMAndRouter) {
                BPMAndRouter node_split = (BPMAndRouter) node1;
                List children_dynamic = findNextDynamicActivity(node_split);
                dynamicActivities.addAll(children_dynamic);
            }
        }

        return dynamicActivities;
    }

    /**
     * 找到指定workitem对应的activity的所有直接子结点中，party_type为动态角色的�?
     *
     * @param workitem
     * @return
     */
    private List<BPMActivity> findNextDynamicActivity(WorkItem workitem) {
        String activityId = workitem.getActivityId();
        List<BPMActivity> dynamicActivities = new ArrayList<BPMActivity>();
        try {
            ProcessDefManager pdm = WAPIFactory
                    .getProcessDefManager("Engine_1");
            BPMProcess process = (BPMProcess) pdm.getProcessInReady("admin",
                    workitem.getProcessId());
            BPMActivity currentActivity = process.getActivityById(workitem
                    .getActivityId());
            dynamicActivities = findNextDynamicActivity(currentActivity);

            return dynamicActivities;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
        return dynamicActivities;
    }

    private List findNextDynamicUsers(WorkItem workitem) {
        List<BPMActivity> activities = findNextDynamicActivity(workitem);
        //todo
        return null;
    }
    
    /**
     * 移动到父类
     */
    /*protected boolean onWorkitemStoped( Map eventData)  throws BPMException{
        WorkItem workitem = (WorkItem) eventData.get(ActionRunner.SYSDATA_WORKITEM);
        Affair affair = DateSharedWithWorkflowEngineThreadLocal.getTheStopAffair();
        if(affair == null){
        	affair = affairManager.getBySubObject((long)workitem.getId());
        }
        
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	
    	Map<String, Object> columns = new HashMap<String, Object>();
    	columns.put("state", StateEnum.col_done.key());
    	columns.put("subState", SubStateEnum.col_done_stepStop.key());
    	columns.put("completeTime", now);
    	columns.put("updateDate", now);
    	
//    	终止时不给待发送事项发消息
    	List<Affair> trackingAndPendingAffairs = affairManager.getTrackingAndPendingAffairBySummaryId(affair.getObjectId(),affair.getApp());
    	
    	//根据app判断，避免终止时更新待发送事项的状态
    	affairManager.update(columns, new Object[][]{{"objectId", affair.getObjectId()}, {"state", StateEnum.col_pending.key()},{"app",affair.getApp()}});

		EdocMessageHelper.terminateCancel(affairManager, orgManager, userMessageManager, workitem, affair, trackingAndPendingAffairs);
    	
		return true;
	}*/
    protected boolean onActivityStoped( Map eventData)  throws BPMException{
    	//动作终止后协同所作工作 wait to write......
		return super.onActivityStoped(eventData);
	}

	public void setEdocSuperviseManager(EdocSuperviseManager edocSuperviseManager) {
		this.edocSuperviseManager = edocSuperviseManager;
	}
	
	/**
	 * 移动到父类
	 */
	/*private List<Affair> superviseCancel(List workitems,Timestamp now){
		List<Affair> affair4Message = new ArrayList<Affair>();
		if(workitems == null || workitems.size()==0)
			return affair4Message;
		List<Long> ids = new ArrayList<Long>();
		Map<String,Object> nameParameters = new HashMap<String,Object>();
		for(int i=0;i<workitems.size();i++){
			ids.add((long)((WorkItem)workitems.get(i)).getId());
			//防止in超长，300个一更新，事务上会有问题
			if((i+1) % 300 == 0 || i == workitems.size()-1){
				nameParameters.put("subObjectId", ids);
				this.affairManager.bulkUpdate("update " + Affair.class.getName() + " set state=?,subState=?,updateDate=?,is_delete=1 where subObjectId in (:subObjectId)", nameParameters, StateEnum.col_cancel.key(),SubStateEnum.col_normal.key(),now);
				List<Affair> affairs = this.affairManager.getByConditions(nameParameters);
				affair4Message.addAll(affairs);
				ids.clear();
			}
		}
		return affair4Message;
	}*/
	
	protected int getApp(){
		return ApplicationCategoryEnum.edoc.getKey();
	}
	
	protected Log getLog(){
		return log;
	}
}
