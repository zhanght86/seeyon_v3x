package com.seeyon.v3x.workflow.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMAndRouter;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMParticipant;
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

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColMessageHelper;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.common.workflow.engine.org.WorkFlowOrgManager;
import com.seeyon.v3x.interfaces.WSCallBackService;
import com.seeyon.v3x.interfaces.domain.WebServiceResult;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.thoughtworks.xstream.XStream;

/**
 * User: lius
 * Date: 2006-11-15
 * Time: 15:52:24
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 */
public class WorkflowEventListener extends BaseAbstractEventListener {
	private final static Log log = LogFactory.getLog(WorkflowEventListener.class);
    private AffairManager affairManager = null;
    private ColManager colManager = null;
    private OrgManager orgManager;
    private UserMessageManager userMessageManager = null;
    private WSCallBackService wsCallBackService;
    private ColSuperviseManager colSuperviseManager;
    private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");

    public void setWsCallBackService(WSCallBackService wsCallBackService) {
		this.wsCallBackService = wsCallBackService;
    }

	public WorkflowEventListener() {
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
    //正常处理
    public static final Integer COMMONDISPOSAL = 9;
    //撤销
    public static final Integer CANCEL = 10;
    //暂存待办
    public static final Integer ZCDB = 11;
//  默认删除操作
    public static final Integer AUTODELETE = 12;*/


    public static Integer getOperationType() {
        return DateSharedWithWorkflowEngineThreadLocal.getOperationType();
    }

    public static void setOperationType(Integer obj) {
    	DateSharedWithWorkflowEngineThreadLocal.setOperationType(obj);
    }

    public static ColSummary getColSummary() {
        return (ColSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
    }

    public static void setColSummary(ColSummary colSummary) {
    	DateSharedWithWorkflowEngineThreadLocal.setColSummary(colSummary);
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
        ColSummary summary = (ColSummary)DateSharedWithWorkflowEngineThreadLocal.getColSummary();
        try {
        	if(summary == null){
        		summary = colManager.getSummaryByCaseId(caseId);
        	}
        } catch (ColException e) {
            log.error("取得协同caseId=" + caseId, e);
            throw new BPMException(BPMException.EXCEPTION_CODE_CASE_NOT_EXITE);
        }

        Integer operationType = WorkflowEventListener.getOperationType();
        Constant.flowState summaryState = WorkflowEventListener.STETSTOP.equals(operationType) ? Constant.flowState.terminate : Constant.flowState.finish;
        try {
        	summary.setState(summaryState.ordinal());
        	setTime2Summary();
            colManager.setFinishedFlag(summary);
        } catch (ColException e) {
        	log.error("更新流程结束标志", e);
        }

        affairManager.updateFinishFlag(summary.getId());

        /**
         * 协同的归档策略：add by muj according to sunj
         * 1、自由协同如果设置了预归档，则流程结束从发起人的已发中删除，不影响其他人。
         * 2、如果是在协同模板中设了预归档，无论流程是否结束，都不自动从已发已办中去掉
         * 3、模板流程如果不是管理员设置的预归档，是发起人设的，就视同自由流程的处理规则
         */
        if(summary.getArchiveId() != null
        		&& ColHelper.getTempletePrePigholePath(summary.getTempleteId())==null){
            affairManager.updatePigeonholeInfo(summary.getStartMemberId(), summary.getId(), summary.getArchiveId());
        }

        //更新督办状态为已办结
        colSuperviseManager.updateStatusBySummaryId(summary.getId());

        //清空消息跟踪设置表中的数据
        try{
        	colManager.deleteColTrackMembersByObjectId(summary.getId());
        }catch(Exception e){
        	log.error(e);
        }
        //更新表单动态表状态为已办结
        if(summary.getFormAppId() != null){
	        try {
				iOperBase.updateFinishedBySummaryId(summary,summary.getFormAppId(), summary.getFormRecordId(), summaryState);
			} catch (SeeyonFormException e1) {
				log.error("更新表单动态表流程结束标志时错误", e1);
			} catch (Exception e1) {
				log.error("更新表单动态表流程结束标志时错误", e1);
			}
        }

        String callbackCode=summary.getWebServiceCode();
        if(callbackCode!=null&&callbackCode.length()>0){

        	WebServiceResult result=new WebServiceResult();
        	result.setApp("协同应用 ");
        	result.setApplicant("");
        	Date date=new Date();
        	result.setBusinessCode(summary.getId().toString());
        	result.setSubject(summary.getSubject());
        	result.setResult(" 审批完成流程结束 ");
        	result.setDate(date);
        	try {
				wsCallBackService.callBack(callbackCode, result);
			} catch (Exception e) {
				log.error("执行webService回调异常", e);
				throw new BPMException("执行webService回调异常", e);
			}
        }

        return super.onProcessFinished(eventData);
    }

    public void init() {
//        if(listener == null && this.getClass().getName().contains("cglib")){
//        if(listener == null){
//            listener = this;
//            listener.registerMe();
//        }
        this.registerMe();

    }


    public void registerMe() {
        EventListenerManager.register(this);
    }

    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }

    public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setColManager(ColManager colManager) {
        this.colManager = colManager;
    }

	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }


    public static class PersonInfo {
        private String id;
        private String name;
        public PersonInfo(){}
        public PersonInfo(Long id, String name){
            this.id = id + "";
            this.name = name;
        }
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

        public List<NodeAddition> invalidateActivity = new ArrayList<NodeAddition>();

        public Map<String, String> invalidateActivityMap= new HashMap<String,String>();

		public Map<String, Object> conditions= new HashMap<String,Object>();
		public HashMap<String, String> nodeTypes= new HashMap<String,String>();

        public void addInvalidateActivityMap(BPMHumenActivity activity){
			//added by wangchw
            invalidateActivityMap.put(activity.getId(), activity.getBPMAbstractNodeName());
		}

        public Map<String, String> getInvalidateActivityMap() {
			return invalidateActivityMap;
		}


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

		public List<NodeAddition> getInvalidateActivity() {
			return invalidateActivity;
		}

		/**
		 * 这个节点无效，比如：删除
		 * @param activity
		 */
		public void addInvalidateActivity(BPMHumenActivity activity) {
			NodeAddition n = new NodeAddition();
            n.setNodeId(activity.getId());
            n.setNodeName(activity.getBPMAbstractNodeName());
            n.setParty(((BPMActor)activity.getActorList().get(0)).getParty());

			this.invalidateActivity.add(n);
		}

    }

    public static class NodeAddition {
        private String nodeId;
        private String nodeName;
		private String partyType; //user/post/department/等
        private String partyId;
        private String processMode;
        private List<PersonInfo> people;
        private boolean readOnly;
        private boolean fromIsInform = false;//是否来自知会
        private boolean isOnlyDisplayName = false;//是否只显示名称(增加该参数，主要解决像空节点、全体执行这些这些节点的显示问题)
		public boolean isOnlyDisplayName() {
			return isOnlyDisplayName;
		}

		public void setOnlyDisplayName(boolean isOnlyDisplayName) {
			this.isOnlyDisplayName = isOnlyDisplayName;
		}

		// 来源知会节点Id
        private Set<String> sourceInformNodes;
		// 控制节点Id列表，只有这些节点都不匹配当前的节点才显示
        private Set<String> negativeNodes = new HashSet<String>();

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

		public String getPartyId() {
			return partyId;
		}

		public String getPartyType() {
			return partyType;
		}

		public void setParty(BPMParticipant party) {
			this.partyId = party.getId();
			this.partyType = party.getType().id;
		}

		public boolean isReadOnly() {
			return readOnly;
		}

		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}

		public boolean isFromIsInform() {
			return fromIsInform;
		}

		public void setFromIsInform(boolean fromIsInform) {
			this.fromIsInform = fromIsInform;
		}
        public Set<String>  getSourceInformNodes() {
			return sourceInformNodes;
		}

		public void setSourceInformNodes(Set<String> sourceInformNodes) {
			this.sourceInformNodes = sourceInformNodes;
		}
        public Set<String> getNegativeNodes() {
			return negativeNodes;
		}

		public void setNegativeNodes(Set<String> negativeNodes) {
			this.negativeNodes = negativeNodes;
		}

    }

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
    /*protected boolean onWorkitemAssigned(Map eventData) throws BPMException {
    	List<WorkItem> workitems = (List<WorkItem>)(((Map)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA)).get("workitems"));
    	ColBody colBody = null;
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

	        ColSummary summary = getColSummary();
	        if(summary == null){
	            try {
	                summary = colManager.getSummaryByCaseId(caseId);
	                setColSummary(summary);
	            } catch (Exception e) {
	                throw new BPMException("获取流程对应的协同对象失败", e);
	            }
	        }

	        Long senderId = summary.getStartMemberId();
	        Long objectId = summary.getId();

	        Long deadline = null;
	        Long remindTime = null;
	        if(!"".equals(seeyonPolicy.getdealTerm()) && seeyonPolicy.getdealTerm() != null){
	        	deadline = Long.parseLong(seeyonPolicy.getdealTerm());
	        }
	        if(!"".equals(seeyonPolicy.getRemindTime())
	        		&& seeyonPolicy.getRemindTime() != null
	        		&& !"undefined".equals(seeyonPolicy.getRemindTime())){
	        	remindTime = Long.parseLong(seeyonPolicy.getRemindTime());
	        }

            Boolean isHasAttachments = summary.isHasAttachments();
            int importantLevel = summary.getImportantLevel();
            java.sql.Timestamp createDate = summary.getCreateDate();
            colBody = summary.getFirstBody();

            Timestamp now = new Timestamp(System.currentTimeMillis());

            List<Affair> affairs = new ArrayList<Affair>(workitems.size());

            for (WorkItem workitem : workitems) {
    			Long memberId = Long.parseLong(workitem.getPerformer());

	            Affair affair = new Affair();
	            affair.setIdIfNew();
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
				affair.setApp(ApplicationCategoryEnum.collaboration.key());
				affair.setCreateDate(createDate);
				affair.setIsSendMessage(isSendMessage);
				affair.setBodyType(colBody.getBodyType());
				affair.setHasAttachments(isHasAttachments);
				affair.setImportantLevel(importantLevel);
				affair.setResentTime(summary.getResentTime());
				affair.setForwardMember(summary.getForwardMember());

				affair.setNodePolicy(seeyonPolicy.getId());
				affair.setActivityId(Long.parseLong(workitem.getActivityId()));
				affair.setFormAppId(seeyonPolicy.getFormApp());
				affair.setFormId(seeyonPolicy.getForm());
				affair.setFormOperationId(seeyonPolicy.getOperationName());
				affair.setFormReadonly("1".equals(seeyonPolicy.getFR()));

				affair.serialExtProperties();

				//回退导致新生成的事项
				if(!isSendMessage){
					DateSharedWithWorkflowEngineThreadLocal.addToAffairMap(memberId, affair.getId());
				}

				affairs.add(affair);
            }

			AffairData affairData = new AffairData();

			affairData.setAffairList(affairs);
			affairData.setIsSendMessage(affairs.get(0).getIsSendMessage());
			affairData.setSender(affairs.get(0).getSenderId());
			affairData.setBodyType(colBody.getBodyType());
			affairData.setBodyContent(colBody.getContent());
			affairData.setBodyCreateDate(colBody.getCreateDate());

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
    /**
     * 调用工作流接口，执行节点执行人匹配
     *
     * @author jincm 2008-3-22
     * @param current_node 当前节点
     * @param process 本次匹配对应的流程定义
     * @param context 匹配所需相关数据
     * @return ProcessModeSelector节点匹配结果对像
     */
    public static ProcessModeSelector parseProcessModeSelector(BPMAbstractNode current_node, BPMProcess process, Map context, boolean isFromTemplate) {
/*        ProcessModeSelector selector = new ProcessModeSelector();
        List<NodeAddition> nodeAdditions = new ArrayList<NodeAddition>();
        selector.nodeAdditions = nodeAdditions;
        Set<BPMHumenActivity> children = findHumenChildrenCascade(current_node);

        context.put("CurrentActivity", current_node);

        for (BPMHumenActivity activity : children) {
        	if(!"normal".equals(activity.isValid())){ //节点不可用
        		selector.addInvalidateActivity(activity);
        		continue;
        	} //TODO 老流程

        	boolean needManualSelect0 = parseProcessModeSelector0(activity, context, nodeAdditions, isFromTemplate);
        	if(needManualSelect0){
        		 selector.mode = 2;
        	}
        }

        return selector;*/
    	return new ProcessModeSelectorParser().parse(current_node, process, context, isFromTemplate);
    }
    /**
     * 调用工作流接口，执行节点执行人匹配,知会节点单独处理
     *
     * @author jincm 2008-3-22
     * @param current_node 当前节点
     * @param process 本次匹配对应的流程定义
     * @param context 匹配所需相关数据
     * @return ProcessModeSelector节点匹配结果对像
     */
/*    public static Map<String, Object> informParseProcessModeSelector(BPMAbstractNode current_node, BPMProcess process,
    		Map context, ProcessModeSelector selector, boolean isFromTemplate){
    	Map<String, Object> selectorMap = new HashMap<String, Object>();
    	WorkFlowOrgManager workFlowOrgManager = WAPIFactory.getWorkFlowOrgManager("Engine_1");

    	List<NodeAddition> nodeAdditions = selector.nodeAdditions;

    	context.put("CurrentActivity", current_node);

        List<BPMHumenActivity> children = ColHelper.findDirectHumenChildrenCascade(current_node,false);
        boolean needManualSelect = false;
        for (BPMHumenActivity activity : children) {
        	if(!"normal".equals(activity.isValid())){ //节点不可用
        		selector.addInvalidateActivity(activity);
        		continue;
        	}

        	String nodePolicy = activity.getSeeyonPolicy().getId();
        	if("inform".equals(nodePolicy) || "zhihui".equals(nodePolicy)){
        		selectorMap = informParseProcessModeSelector(activity, process, context, selector, isFromTemplate);
        		nodeAdditions = (List<NodeAddition>)selectorMap.get("nodeAdditions");
        		String isSelect = selectorMap.get("needManualSelect").toString();
        		if("true".equals(isSelect)){
        			needManualSelect = true;
        		}
        	}

        	boolean needManualSelect0 = parseProcessModeSelector0(activity, context, nodeAdditions, isFromTemplate);
        	if(needManualSelect0){
        		needManualSelect = needManualSelect0;
        	}
        }

        selectorMap.put("nodeAdditions", nodeAdditions);
        if(needManualSelect){
        	selectorMap.put("needManualSelect", "true");
        }
        else{
        	selectorMap.put("needManualSelect", "false");
        }

    	return selectorMap;
    }*/

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
    	BPMActivity currentActivity = (BPMActivity) eventData.get("real_activity");

    	if(currentActivity == null){
    		currentActivity = ColHelper.getActivityByWorkitem(workitem);
    	}

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
    			List<Long> workitemIds = new ArrayList<Long>(MaxCommitNumber);
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

    					workitemIds = new ArrayList<Long>(MaxCommitNumber);
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
        	ColMessageHelper.competitionCancel(affairManager, orgManager, userMessageManager, workitem, affair);
        }
        else if(operationType == 8){
        	throw new UnsupportedOperationException(affair.getId() + ", " + affair.getSubject());
        }
        else if(operationType == 11){
        	affair.setState(StateEnum.col_competeOver.key());
        	affair.setSubState(SubStateEnum.col_normal.key());
        	//给在竞争执行中被取消的affair发送消息提醒
        	ColMessageHelper.competitionCancel(affairManager, orgManager, userMessageManager, workitem, affair);
        }else if(operationType == 12){
//        	删除被替换的所有affair事项
        	List<Affair> affairs = new ArrayList<Affair>();
        	if(eventData.get(ActionRunner.SYSDATA_EXTEND_DATA) != null){
        		if(eventData.get(ActionRunner.SYSDATA_EXTEND_DATA) instanceof List){
        			//TODO 性能到底咋样呢？如果是集团，一下子发个几万条的消息。。。
        			affairs = this.superviseCancel((List)eventData.get(ActionRunner.SYSDATA_EXTEND_DATA),now);
        			executeSingleUpdate = false;
        		}
        	}
        	//给在督办中被删除的affair发送消息提醒
        	if(affairs.isEmpty()){
        		affairs.add(affair);
        	}
        	ColMessageHelper.superviseDelete(affairManager, orgManager, userMessageManager, workitem, affairs);
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
        Timestamp now = new Timestamp(System.currentTimeMillis());
        affair.setCompleteTime(now);
        affair.setUpdateDate(now);
        //设置运行时长，超时时长等
        setTime2Affair(affair);
        affairManager.updateAffair(affair);
        Long summaryId = affair.getObjectId();
        int operationType = getOperationType();
        if(operationType == 8){
        	return false;
        }else{
        	//发送完成事项消息提醒
        	ColMessageHelper.workitemFinishedMessage(affairManager, orgManager, colManager, userMessageManager, affair, summaryId);
        }

        return false;
    }

    protected boolean onWorkitemAdded(Map eventData) throws BPMException {
        return super.onWorkitemAdded(eventData);
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

        if(affair == null) return null;

        if(operationType != 1
        	&& operationType != 2
        	&& operationType != 10
        	&& operationType != 9
        	&& operationType != 8
        	&& operationType != 12
        	){
        	log.info("==========================================\neventData2ExistingAffair: " + operationType + "\t" + workitem + "\t" + affair, new Exception());
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
            log.error("工作项对应的流程不存在", e);
        }
        return dynamicActivities;
    }

    private List findNextDynamicUsers(WorkItem workitem) {
        List<BPMActivity> activities = findNextDynamicActivity(workitem);
        //todo
        return null;
    }

    /**移动到父类
     * 工作项终止:
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

    	List<Affair> trackingAndPendingAffairs = affairManager.getTrackingAndPendingAffairBySummaryId(affair.getObjectId());

    	affairManager.update(columns, new Object[][]{{"objectId", affair.getObjectId()}, {"state", StateEnum.col_pending.key()}});

		ColMessageHelper.terminateCancel(affairManager, orgManager, userMessageManager, workitem, affair, trackingAndPendingAffairs);

		return true;
	}*/

    protected boolean onActivityStoped( Map eventData)  throws BPMException{
    	//动作终止后协同所作工作 wait to write......
		return super.onActivityStoped(eventData);
	}
    protected boolean onWorkitemZcdb( Map eventData)  throws BPMException{
    	//工作项终止后协调所作的工作 wait to write......
		return super.onWorkitemZcdb(eventData);
	}
    protected boolean onActivityZcdb( Map eventData)  throws BPMException{
    	//动作终止后协同所作工作 wait to write......
		return super.onActivityZcdb(eventData);
	}
    protected boolean onWorkitemTakeBack( Map eventData)  throws BPMException{
    	Affair affair = eventData2ExistingAffair(eventData);

        if (affair == null)
            return false;

        affair.setState(StateEnum.col_takeBack.key());
        affair.setSubState(SubStateEnum.col_normal.key());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        affair.setUpdateDate(now);
        affairManager.updateAffair(affair);
        return false;
	}

	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
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

	protected Log getLog(){
		return log;
	}

	protected int getApp(){
		return ApplicationCategoryEnum.collaboration.getKey();
	}

	/**
	 * preSend时解析匹配执行节点执行人
	 * @author wangwenyou
	 *
	 */
	static class ProcessModeSelectorParser{
		private ProcessModeSelector selector;
	    public ProcessModeSelector parse(BPMAbstractNode current_node, BPMProcess process, Map context, boolean isFromTemplate) {
	        selector = new ProcessModeSelector();
	        List<NodeAddition> nodeAdditions = new ArrayList<NodeAddition>();
	        selector.nodeAdditions = nodeAdditions;
	        Set<BPMHumenActivity> children = findHumenChildrenCascade(current_node);

	        context.put("CurrentActivity", current_node);

	        for (BPMHumenActivity activity : children) {
	        	String seeyonPolicyId= activity.getSeeyonPolicy().getId();
	        	if("zhihui".equals(seeyonPolicyId) || "inform".equals(seeyonPolicyId)){//知会节点
	        		selector.nodeTypes.put(activity.getId(), activity.getName());
	        	}
	        	if(!"normal".equals(activity.isValid())){ //节点不可用
	        		selector.addInvalidateActivity(activity);
	        		selector.addInvalidateActivityMap(activity);
	        		continue;
	        	} //TODO 老流程

	        	boolean needManualSelect0 = parseProcessModeSelector0(activity, context, nodeAdditions, isFromTemplate);
	        	if(needManualSelect0){
	        		 selector.mode = 2;
	        	}
	        }

	        return selector;
	    }
	    // 知会后续人工节点 － 条件制约节点列表 （只有这些条件全不成立才匹配该知会后续人工节点）
	    private Map<String,Set<String>> termControlActivities;
	    // 知会后续人工节点 － 知会节点（可能有多个，如 Split － [（条件）知会 ，（条件）知会，（条件）人工，（条件）人工 ] － Join － 人工节点）
	    // 没有无条件的非知会人工节点，而且所有有条件的人工节点都不成立，选择任一直通的知会节点时才需要选择Join后的人工节点
	    private Map<String,Set<String>> humenSourceInformMap;
		private Set<BPMHumenActivity> findHumenChildrenCascade(
				BPMAbstractNode current_node) {
			termControlActivities = new HashMap<String, Set<String>>();
			humenSourceInformMap = new HashMap<String, Set<String>>();
			List<BPMHumenActivity> children = ColHelper
					.findDirectHumenChildrenCascade(current_node, false);
//			// 标记所有的子节点是否都是知会节点
//			boolean isAllInform = ColHelper.isAllInform(children);
//			boolean isHasTerm = ColHelper.isHasTerm(children);
			Set<BPMHumenActivity> newResult = new LinkedHashSet<BPMHumenActivity>(
					children);
			// 有条件的非知会人工节点列表
			List<String> hasTermHumen = new ArrayList<String>();
			// 无条件的非知会人工节点列表
			List<BPMHumenActivity> fixedHumen = new ArrayList<BPMHumenActivity>();
			// 标记所有的子节点是否都是知会节点
			boolean isAllInform = true;

			for (BPMHumenActivity activity : children) {
				if(!ColHelper.isInform(activity)){
					isAllInform = false;
					// 分支只有知会有条件视同无条件
					if(hasTerm(activity)){
						hasTermHumen.add(activity.getId());
					}else{
						fixedHumen.add(activity);
					}
				}
			}
			boolean isHasTerm = hasTermHumen.size()>0;
			boolean hasFixedHuman = fixedHumen.size()>0;

			for (BPMHumenActivity _activity : children) {
				if (ColHelper.isInform(_activity)) {
					final List<BPMHumenActivity> list = setFromIsInform(ColHelper
							.findDirectHumenChildrenCascade(_activity, true),
							true);
					if (isHasTerm) {// 有条件存在

						// Split － [（条件）知会 ， 人工 ] － Join － 人工节点
						// Split － [（条件）知会 ， （条件）人工 ] － Join － 人工节点
						// 如果有条件存在，需要特别处理知会
						// 基本算法是考虑当前节点的直接后续人工节点（中间可能会有知会，如 ［Current － 知会（并） － Split － 知会(+) - Join(+) - 人工节点］）
						// 如果是直线型的流程，
						// 如果是 ［知会(+) － Join(+) － 人工节点］ 型的流程，由知会引出的所有非当前节点后续人工节点（下简称为知会后续人工节点），是否被匹配取决于当前节点的直接后续人工节点
						//     直接后续人工节点存在无条件的非知会人工节点，则知会后续人工节点都必须被排除
						//     直接后续人工节点有条件，则知会后续人工节点是否被匹配取决于这些条件，只有所有条件都不成立时才匹配知会后续人工节点。

						// 只要有一只拦路虎，知会后续人工节点都不需要匹配
						if(!hasFixedHuman){
							// 记录受条件制约的人工节点，在前端运算，只有都不成立时才匹配
							for (BPMHumenActivity activity : list) {
								final String id = activity.getId();
								Set<String> set1 = termControlActivities.get(id);
								if(set1==null){
									set1 = new HashSet<String>();
								}
								set1.addAll(hasTermHumen);
								termControlActivities.put(id, set1);

								Set<String> set2 = humenSourceInformMap.get(id);
								if(set2==null){
									set2 = new HashSet<String>();
								}
								set2.add( _activity.getId());
								humenSourceInformMap.put(id,set2);
							}
							newResult.addAll(list);
						}


					} else {// 无条件存在
						if (isAllInform) {// 全是知会节点
							newResult.addAll(list);
						} else {// 存在不是知会节点
							List<BPMTransition> down_links = _activity
									.getDownTransitions();
							if (down_links != null && down_links.get(0) != null) {
								if (down_links.get(0).getTo() instanceof BPMHumenActivity) {
									newResult.addAll(list);
								}
							}
						}
					}
				}
			}
			return newResult;
		}

	    private boolean parseProcessModeSelector0(BPMHumenActivity activity, Map context, List<NodeAddition> nodeAdditions, boolean isFromTemplate){
	    	boolean needManualSelect = false;
	    	List<BPMActor> actors = activity.getActorList();
	    	BPMActor actor = actors.get(0);

	    	if("user".equals(actor.getType().id)){ //该节点直接是人，不用匹配
	    		return false;
	    	}
	        if(V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(actor.getParty().getId())){ //空节点，不用匹配
	        	return false;
	        }

	        context.put("activity", activity);

	    	actor.getParty().setAddition("");

	    	WorkFlowOrgManager workFlowOrgManager = WAPIFactory.getWorkFlowOrgManager("Engine_1");

	        List<com.seeyon.v3x.common.organization.sharemodel.User> users = actor.getUserList(workFlowOrgManager, context);

	        /*
	         * 不需要匹配，有人就行:
	         * 1. 该节点是后加的，如：加签
	         * 2. 不是模板(自由协同)
	         * 3. 竞争执行
	         * 4. 全体执行
	         */
	        if(users != null && !users.isEmpty()
	        		&& (activity.isAdded()
	        				//||!isFromTemplate
	        				|| activity.isCompetitionProcessMode() || activity.isAllProcessMode())){
	        	return false;
	        }
	        else{
	            List<PersonInfo> people = new ArrayList<PersonInfo>(users.size());

	            for (com.seeyon.v3x.common.organization.sharemodel.User user : users) {
	                PersonInfo p = new PersonInfo();
	                p.setId(user.getId());
	                p.setName(user.getName());

	                people.add(p);
	            }

	        	NodeAddition nodeAddition = new NodeAddition();
	            nodeAddition.setNodeId(activity.getId());
	            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
	            nodeAddition.setParty(actor.getParty());
	            nodeAddition.setProcessMode(activity.getProcessMode());
	        	nodeAddition.setPeople(people);
	        	nodeAddition.setFromIsInform(activity.isFromIsInform());
	        	if(activity.isFromIsInform()){
	        		final Set<String> negativeNodes = termControlActivities.get(activity.getId());
	        		if(negativeNodes!=null){
	        			nodeAddition.setNegativeNodes(negativeNodes);
	        		}
	        		nodeAddition.setSourceInformNodes(humenSourceInformMap.get(activity.getId()));
	        	}
	        	if(people.size() == 1){ //当前只匹配到一个人，在前段显示就行
	        		nodeAddition.setReadOnly(true);
	        	}

	    		needManualSelect = true;

	            nodeAdditions.add(nodeAddition);
	        }

	        return needManualSelect;
	    }
		private List<BPMHumenActivity> setFromIsInform(
				List<BPMHumenActivity> activityList, boolean isFromIsInform) {
			for (BPMHumenActivity _activity : activityList) {
				_activity.setFromIsInform(isFromIsInform);
			}
			return activityList;
		}
		/**
		 * 存在前面没有条件而且不是知会的人工节点
		 * @param activityList
		 * @return 存在无条件的非知会人工节点返回true。
		 */
		private boolean hasFixedHuman(List<BPMHumenActivity> activityList){
			for(BPMHumenActivity _activity : activityList){
				if(!ColHelper.isInform(_activity) && !hasTerm(_activity)) return true;
			}
			return false;
		}
		private boolean hasTerm(BPMHumenActivity activity){
			List<BPMTransition> up_links = activity.getUpTransitions();
			if (up_links != null) {
				BPMTransition upLink = up_links.get(0);
				if (upLink != null && upLink.getConditionType() != 0
						&& upLink.getConditionType() != 3) {
					return true;
				}
			}
			return false;
		}

		/**
		 * parseNew()
		 * 节点分析方法
		 * @param current_node 当前处理节点
		 * @param process 工作流流程模板定义对象
		 * @param context 上下文信息
		 * @param isFromTemplate 是否来自模板
		 * @param startMemberId 流程启动人员id
		 * @param currentNodeMemberId 当前节点处理人员Id
		 * @param orgManager 组织模型管理器
		 * @param calcResult 条件解析标志，一般默认为false即可
		 * @param fieldMap 表单域对象
		 * @param fieldDataBaseMap 表单基础域对象
		 * @return 匹配信息
		 * @throws NumberFormatException
		 * @throws BusinessException
		 */
		public ProcessModeSelector parseNew(
				BPMAbstractNode current_node,
				BPMProcess process,
				Map context,
				boolean isFromTemplate,
				long startMemberId,
				long currentNodeMemberId,
				OrgManager orgManager,
				boolean calcResult,
//				Map<String,String[]> fieldMap,
//				Map<String,String[]> fieldDataBaseMap,
				Long formAppId,Long masterId,String formData,String currentNodeId,long startMemberLoginAccountId) throws NumberFormatException, BusinessException{
			selector = new ProcessModeSelector();
			context.put("CurrentActivity", current_node);
			HashMap<String,String> conditonMap= new HashMap<String, String>();
			HashMap<String,String> nodeTypes= new HashMap<String, String>();
			List<BPMHumenActivity> children = ColHelper.findDirectHumenChildren(current_node, conditonMap,nodeTypes);
			Map<String,Object> hash = null;
			if(conditonMap.size()>0) {
				Map<String,Object> map = ColHelper.splitCondition(conditonMap);
				map= ColHelper.preParseCondition(map,formAppId,masterId,formData);
				hash= ColHelper.parseCondition(map,null,startMemberId,currentNodeMemberId,orgManager,calcResult,currentNodeId,process,startMemberLoginAccountId);
			}
			selector.conditions= hash;
			selector.nodeTypes= nodeTypes;
			List<NodeAddition> nodeAdditions= new ArrayList<NodeAddition>();
			for (BPMHumenActivity activity : children) {
	        	if(!"normal".equals(activity.isValid())){ //节点不可用
	        		selector.addInvalidateActivity(activity);
	        		selector.addInvalidateActivityMap(activity);
//	        		continue;
	        	}//TODO 老流程
	        	List<BPMActor> actors = activity.getActorList();
		    	BPMActor actor = actors.get(0);
		    	if("user".equals(actor.getType().id) ||
		    			V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(actor.getParty().getId())){
		    		//该节点直接是人或空节点，不用匹配
		    		NodeAddition nodeAddition = new NodeAddition();
		            nodeAddition.setNodeId(activity.getId());
		            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
		            nodeAddition.setParty(actor.getParty());
		            nodeAddition.setProcessMode(activity.getProcessMode());
		            nodeAddition.setReadOnly(true) ;
		            nodeAddition.setOnlyDisplayName(true);
		            nodeAdditions.add(nodeAddition);
		            continue;
		    	}
		        context.put("activity", activity);
		    	actor.getParty().setAddition("");
		    	WorkFlowOrgManager workFlowOrgManager = WAPIFactory.getWorkFlowOrgManager("Engine_1");
		        List<com.seeyon.v3x.common.organization.sharemodel.User> users = actor.getUserList(workFlowOrgManager, context);
		        String partyTypeId = actor.getParty().getType().id;
		        if(users != null && !users.isEmpty()
		        		&& (activity.isAdded()
//		        				|| !isFromTemplate
		        				|| activity.isCompetitionProcessMode()
		        				|| activity.isAllProcessMode())){
		        	/*
			         * 不需要匹配，有人就行:
			         * 1. 该节点是后加的，如：加签
			         * 2. 不是模板(自由协同)
			         * 3. 竞争执行
			         * 4. 全体执行
			         */
		        	//该节点直接是人或空节点，不用匹配
		    		NodeAddition nodeAddition = new NodeAddition();
		            nodeAddition.setNodeId(activity.getId());
		            String acountShortName= "";
                    if(partyTypeId.equals(V3xOrgEntity.ORGENT_TYPE_POST)){ //岗位
                        try{
                            acountShortName= orgManager.getAccountById(Long.parseLong(actor.getParty().getAccountId())).getShortname() ;
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName()+"("+acountShortName+")");
                        }catch(Throwable e){
                            log.error(e.getMessage(), e);
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                        }
                    }else{
                        nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                    }
		            nodeAddition.setParty(actor.getParty());
		            nodeAddition.setProcessMode(activity.getProcessMode());
		            nodeAddition.setReadOnly(true) ;
		            nodeAddition.setOnlyDisplayName(true);
		            nodeAdditions.add(nodeAddition);
		            continue;
		        }else{
		            List<PersonInfo> people = new ArrayList<PersonInfo>(users.size());
		            for (com.seeyon.v3x.common.organization.sharemodel.User user : users) {
		                PersonInfo p = new PersonInfo();
		                p.setId(user.getId());
		                p.setName(user.getName());
		                people.add(p);
		            }
		        	NodeAddition nodeAddition = new NodeAddition();
		            nodeAddition.setNodeId(activity.getId());
		            String acountShortName= "";
                    if(partyTypeId.equals(V3xOrgEntity.ORGENT_TYPE_POST)){ //岗位
                        try{
                            acountShortName= orgManager.getAccountById(Long.parseLong(actor.getParty().getAccountId())).getShortname() ;
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName()+"("+acountShortName+")");
                        }catch(Throwable e){
                            log.error(e.getMessage(), e);
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                        }
                    }else{
                        nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                    }
		            nodeAddition.setParty(actor.getParty());
		            nodeAddition.setProcessMode(activity.getProcessMode());
		        	nodeAddition.setPeople(people);
		        	nodeAddition.setFromIsInform(activity.isFromIsInform());
		        	if(people.size() == 1){ //当前只匹配到一个人，在前段显示就行
		        		nodeAddition.setReadOnly(true);
		        	}
		        	//对表单控件的处理:统一放到工作流组件的workfloworgmanager类中
//		        	if("FormField".equals(actor.getParty().getType().id)){
//		        		String id = "" ;
//		        		String display = "" ;
//		        		if(fieldMap != null && fieldMap.get(actor.getParty().getId()) != null){
//		        			String str[] = fieldMap.get(actor.getParty().getId()) ;
//		        			id = str[1] ;
//		        			display = str[0] ;
//		        			V3xOrgMember member = null ;
//		        			if(Strings.isNotBlank(id)){
//		        				member = orgManager.getMemberById(Long.valueOf(id)) ;
//		        			}
//		        			if(member != null && member.isValid()){//表单控件中人员离职了
//		        				display =  member.getName() ;
//		        			}else{
//		        				display =  "";
//		        			}
//		        		}
//		        		if(!"start".equals(current_node.getId()) && fieldDataBaseMap != null
//		        				&& Strings.isBlank(id) && Strings.isBlank(display)){
//		            		String str[] =	fieldDataBaseMap.get(actor.getParty().getId()) ;
//		            		if(str != null){
//		            			id = str[1] ;
//		            			display = str[0] ;
//		            			V3xOrgMember member = null ;
//			        			if(Strings.isNotBlank(id)){
//			        				member = orgManager.getMemberById(Long.valueOf(id)) ;
//			        			}
//			        			if(member != null && member.isValid()){//表单控件中人员离职了
//			        				display =  member.getName() ;
//			        			}else{
//			        				display = "";
//			        			}
//		            		}
//		        		}
//		        		if(Strings.isNotBlank(id) && Strings.isNotBlank(display)){
//		            		//selector.mode = 1 ;
//		            		PersonInfo personInfo = new PersonInfo() ;
//		            		personInfo.setId(id) ;
//		            		personInfo.setName(display) ;
//		            		List<PersonInfo> list = new ArrayList<PersonInfo>();
//		            		list.add(personInfo) ;
//		            		nodeAddition.setReadOnly(true) ;
//		            		nodeAddition.setPeople(list) ;
//		        		}
//		        	}
		            nodeAdditions.add(nodeAddition);
		        }
	        }
			selector.setNodeAdditions(nodeAdditions);
			return selector;
		}

		/**
		 *
		 * 根据当前知会节点列表，查找这些知会节点的直接后续人工节点列表和条件分支信息
		 * @param process 工作流 程模板定义对象
		 * @param currentNodeId 当前处理节点Id
		 * @param isFromTemplate 是否来自模板
		 * @param caseId 流程实例Id
		 * @param startMemberId 流程启动人员Id
		 * @param currentNodeMemberId 当前节点处理人员Id
		 * @param orgManager 组织模型管理器
		 * @param calcResult 条件分支分析标志，一般默认为false
		 * @param fieldMap 表单域集合
		 * @param fieldDataBaseMap 表单基础域集合
		 * @param allNotSelectNodeList 在弹出页面中所有没有被选中的节点列表
		 * @param allSelectNodeList 在弹出页面中所有被选中的节点列表
		 * @param informNodeList 在弹出页面中所有被选中的知会节点列表
		 * @return 匹配结果集合
		 * @throws NumberFormatException
		 * @throws BusinessException
		 * @throws BPMException
		 */
		public ProcessModeSelector parseNewOfInformNodes(
				BPMAbstractNode current_node,
				BPMProcess process,
				Map context,
				boolean isFromTemplate,
				long startMemberId,
				long currentNodeMemberId,
				OrgManager orgManager,
				boolean calcResult,
//				Map<String, String[]> fieldMap,
//				Map<String, String[]> fieldDataBaseMap,
				List<String> allNotSelectNodeList,
				List<String> allSelectNodeList,
				List<String> informNodeList,
				List<String> allInformNodeList,
				Long formAppId,Long masterId,String formData,String currentNodeId,long startMemberLoginAccountId) throws NumberFormatException, BusinessException, BPMException {
			selector = new ProcessModeSelector();
			context.put("CurrentActivity", current_node);
			HashMap<String,String> conditonMap= new HashMap<String, String>();
			HashMap<String,String> nodeTypes= new HashMap<String, String>();
			List<BPMHumenActivity> allChildren= new ArrayList<BPMHumenActivity>();
			Map<String, Object> allConditions= new HashMap<String, Object>();
			HashMap<String, String> allNodeTypes= new HashMap<String, String>();
			List<String> allChildrenList= new ArrayList<String>();
			//循环对informNodeList进行处理
			for (Iterator iterator = informNodeList.iterator(); iterator.hasNext();) {
				String informNodeId = (String) iterator.next();
				BPMActivity currentInformNode= process.getActivityById(informNodeId);
				List<BPMHumenActivity> children =
					ColHelper.findDirectHumenChildrenOfInformNode(
							current_node,
							currentInformNode,
							currentInformNode,
							conditonMap,
							nodeTypes,
							allNotSelectNodeList,
							allSelectNodeList,
							informNodeList,
							allInformNodeList,
							context,
							allChildrenList);
				Map<String,Object> hash = null;
				if(conditonMap.size()>0) {
					Map<String,Object> map = ColHelper.splitCondition(conditonMap);
					map= ColHelper.preParseCondition(map,formAppId,masterId,formData);
					hash= ColHelper.parseCondition(map,null,startMemberId,currentNodeMemberId,orgManager,calcResult,currentNodeId,process,startMemberLoginAccountId);
				}
				if(hash!=null){
					allConditions.putAll(hash);
				}
				if(nodeTypes!=null){
					allNodeTypes.putAll(nodeTypes);
				}
				if(children!=null){
					allChildren.addAll(children);
				}
			}
			selector.conditions=allConditions;
			selector.nodeTypes=allNodeTypes;
			List<NodeAddition> nodeAdditions= new ArrayList<NodeAddition>();
			for (BPMHumenActivity activity : allChildren) {
	        	if(!"normal".equals(activity.isValid())){ //节点不可用
	        		selector.addInvalidateActivity(activity);
	        		selector.addInvalidateActivityMap(activity);
//	        		continue;
	        	}//TODO 老流程
	        	List<BPMActor> actors = activity.getActorList();
		    	BPMActor actor = actors.get(0);
		    	if("user".equals(actor.getType().id) ||
		    			V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(actor.getParty().getId())){
		    		//该节点直接是人或空节点，不用匹配
		    		NodeAddition nodeAddition = new NodeAddition();
		            nodeAddition.setNodeId(activity.getId());
		            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
		            nodeAddition.setParty(actor.getParty());
		            nodeAddition.setProcessMode(activity.getProcessMode());
		            nodeAddition.setReadOnly(true) ;
		            nodeAddition.setOnlyDisplayName(true);
		            nodeAdditions.add(nodeAddition);
		            continue;
		    	}
		        context.put("activity", activity);
		    	actor.getParty().setAddition("");
		    	WorkFlowOrgManager workFlowOrgManager = WAPIFactory.getWorkFlowOrgManager("Engine_1");
		        List<com.seeyon.v3x.common.organization.sharemodel.User> users = actor.getUserList(workFlowOrgManager, context);
		        String partyTypeId = actor.getParty().getType().id;
		        if(users != null && !users.isEmpty()
		        		&& (activity.isAdded()
		        				//|| !isFromTemplate
		        				|| activity.isCompetitionProcessMode()
		        				|| activity.isAllProcessMode())){
		        	/*
			         * 不需要匹配，有人就行:
			         * 1. 该节点是后加的，如：加签
			         * 2. 不是模板(自由协同)
			         * 3. 竞争执行
			         * 4. 全体执行
			         */
		        	//该节点直接是人或空节点，不用匹配
		    		NodeAddition nodeAddition = new NodeAddition();
		            nodeAddition.setNodeId(activity.getId());
		            String acountShortName= "";
                    if(partyTypeId.equals(V3xOrgEntity.ORGENT_TYPE_POST)){ //岗位
                        try{
                            acountShortName= orgManager.getAccountById(Long.parseLong(actor.getParty().getAccountId())).getShortname() ;
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName()+"("+acountShortName+")");
                        }catch(Throwable e){
                            log.error(e.getMessage(), e);
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                        }
                    }else{
                        nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                    }
		            nodeAddition.setParty(actor.getParty());
		            nodeAddition.setProcessMode(activity.getProcessMode());
		            nodeAddition.setReadOnly(true) ;
		            nodeAddition.setOnlyDisplayName(true);
		            nodeAdditions.add(nodeAddition);
		            continue;
		        }else{
		            List<PersonInfo> people = new ArrayList<PersonInfo>(users.size());
		            for (com.seeyon.v3x.common.organization.sharemodel.User user : users) {
		                PersonInfo p = new PersonInfo();
		                p.setId(user.getId());
		                p.setName(user.getName());
		                people.add(p);
		            }
		        	NodeAddition nodeAddition = new NodeAddition();
		            nodeAddition.setNodeId(activity.getId());
		            String acountShortName= "";
                    if(partyTypeId.equals(V3xOrgEntity.ORGENT_TYPE_POST)){ //岗位
                        try{
                            acountShortName= orgManager.getAccountById(Long.parseLong(actor.getParty().getAccountId())).getShortname() ;
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName()+"("+acountShortName+")");
                        }catch(Throwable e){
                            log.error(e.getMessage(), e);
                            nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                        }
                    }else{
                        nodeAddition.setNodeName(activity.getBPMAbstractNodeName());
                    }
		            nodeAddition.setParty(actor.getParty());
		            nodeAddition.setProcessMode(activity.getProcessMode());
		        	nodeAddition.setPeople(people);
		        	nodeAddition.setFromIsInform(activity.isFromIsInform());
		        	if(people.size() == 1){ //当前只匹配到一个人，在前段显示就行
		        		nodeAddition.setReadOnly(true);
		        	}
		        	//对表单控件的处理
//		        	if("FormField".equals(actor.getParty().getType().id)){
//		        		String id = "" ;
//		        		String display = "" ;
//		        		if(fieldMap != null && fieldMap.get(actor.getParty().getId()) != null){
//		        			String str[] = fieldMap.get(actor.getParty().getId()) ;
//		        			id = str[1] ;
//		        			display = str[0] ;
//		        			V3xOrgMember member = null ;
//		        			if(Strings.isNotBlank(id)){
//		        				member = orgManager.getMemberById(Long.valueOf(id)) ;
//		        			}
//		        			if(member != null){
//		        				display =  member.getName() ;
//		        			}
//		        		}
//		        		if(!"start".equals(current_node.getId()) && fieldDataBaseMap != null
//		        				&& Strings.isBlank(id) && Strings.isBlank(display)){
//		            		String str[] =	fieldDataBaseMap.get(actor.getParty().getId()) ;
//		            		if(str != null){
//		            			id = str[1] ;
//		            			display = str[0] ;
//		            		}
//		        		}
//		        		if(Strings.isNotBlank(id) && Strings.isNotBlank(display)){
//		            		//selector.mode = 1 ;
//		            		PersonInfo personInfo = new PersonInfo() ;
//		            		personInfo.setId(id) ;
//		            		personInfo.setName(display) ;
//		            		List<PersonInfo> list = new ArrayList<PersonInfo>();
//		            		list.add(personInfo) ;
//		            		nodeAddition.setReadOnly(true) ;
//		            		nodeAddition.setPeople(list) ;
//		        		}
//		        	}
		            nodeAdditions.add(nodeAddition);
		        }
	        }
			selector.setNodeAdditions(nodeAdditions);
			return selector;
		}
	}

	/**
	 * parseProcessModeSelectorNew()
	 * 流程选择
	 * @param current_node 当前处理节点
	 * @param process 工作流流程定义模板对象
	 * @param context 上下文信息
	 * @param isFromTemplate 是否来自模板流程
	 * @param startMemberId 流程启动人员Id
	 * @param currentNodeMemberId 当前节点处理人员Id
	 * @param orgManager 组织模型管理器
	 * @param calcResult 条件解析标志，一般默认为false即可
	 * @param fieldMap 表单域对象
	 * @param fieldDataBaseMap 表单基础域对象
	 * @return 匹配信息
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public static ProcessModeSelector parseProcessModeSelectorNew(
			BPMAbstractNode current_node,
			BPMProcess process,
			Map context,
			boolean isFromTemplate,
			long startMemberId,
			long currentNodeMemberId,
			OrgManager orgManager,
			boolean calcResult,
//			Map<String,String[]> fieldMap,
//			Map<String,String[]> fieldDataBaseMap,
			Long formAppId,Long masterId,String formData,String currentNodeId,long startMemberLoginAccountId) throws NumberFormatException, BusinessException {
		return new ProcessModeSelectorParser().parseNew(
				current_node,
				process,
				context,
				isFromTemplate,
				startMemberId,
				currentNodeMemberId,
				orgManager,
				calcResult,
//				fieldMap,
//				fieldDataBaseMap,
				formAppId,masterId,formData,currentNodeId,startMemberLoginAccountId);
	}

	/**
	 * parseProcessModeSelectorNewOfInformNodes()
	 * 根据当前知会节点列表，查找这些知会节点的直接后续人工节点列表和条件分支信息
	 * @param process 工作流 程模板定义对象
	 * @param currentNodeId 当前处理节点Id
	 * @param isFromTemplate 是否来自模板
	 * @param caseId 流程实例Id
	 * @param startMemberId 流程启动人员Id
	 * @param currentNodeMemberId 当前节点处理人员Id
	 * @param orgManager 组织模型管理器
	 * @param calcResult 条件分支分析标志，一般默认为false
	 * @param fieldMap 表单域集合
	 * @param fieldDataBaseMap 表单基础域集合
	 * @param allNotSelectNodeList 在弹出页面中所有没有被选中的节点列表
	 * @param allSelectNodeList 在弹出页面中所有被选中的节点列表
	 * @param informNodeList 在弹出页面中所有被选中的知会节点列表
	 * @return 匹配结果集合
	 * @throws NumberFormatException
	 * @throws BusinessException
	 * @throws BPMException
	 */
	public static ProcessModeSelector parseProcessModeSelectorNewOfInformNodes(
			BPMAbstractNode current_node,
			BPMProcess process,
			Map context,
			boolean isFromTemplate,
			long startMemberId,
			long currentNodeMemberId,
			OrgManager orgManager,
			boolean calcResult,
//			Map<String,String[]> fieldMap,
//			Map<String,String[]> fieldDataBaseMap,
			List<String> allNotSelectNodeList,
			List<String> allSelectNodeList,
			List<String> informNodeList,
			List<String> allInformNodeList,
			Long formAppId,Long masterId,String formData,String currentNodeId,long startMemberLoginAccountId) throws NumberFormatException, BusinessException, BPMException {
		return new ProcessModeSelectorParser().parseNewOfInformNodes(
				current_node,
				process,
				context,
				isFromTemplate,
				startMemberId,
				currentNodeMemberId,
				orgManager,
				calcResult,
//				fieldMap,
//				fieldDataBaseMap,
				allNotSelectNodeList,
				allSelectNodeList,
				informNodeList,
				allInformNodeList,
				formAppId,masterId,formData,currentNodeId,startMemberLoginAccountId
		);
	}
}