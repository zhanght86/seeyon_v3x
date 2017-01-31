package com.seeyon.v3x.collaboration.manager.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMAbstractNode.NodeType;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMAndRouter;
import net.joinwork.bpm.definition.BPMConRouter;
import net.joinwork.bpm.definition.BPMEnd;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMParticipant;
import net.joinwork.bpm.definition.BPMParticipantType;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStart;
import net.joinwork.bpm.definition.BPMStatus;
import net.joinwork.bpm.definition.BPMTimeActivity;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.definition.ObjectName;
import net.joinwork.bpm.definition.ReadyObject;
import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.execute.ActionRunner;
import net.joinwork.bpm.engine.execute.BPMCase;
import net.joinwork.bpm.engine.execute.HistoryCaseRunDAO;
import net.joinwork.bpm.engine.execute.ProcessEngineImpl;
import net.joinwork.bpm.engine.execute.ReadyNode;
import net.joinwork.bpm.engine.wapi.CaseDetailLog;
import net.joinwork.bpm.engine.wapi.CaseInfo;
import net.joinwork.bpm.engine.wapi.ProcessDefManager;
import net.joinwork.bpm.engine.wapi.ProcessEngine;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;
import net.joinwork.bpm.engine.wapi.WorkItemManager;
import net.joinwork.bpm.task.HistoryWorkitemDAO;
import net.joinwork.bpm.task.WorkItemManagerImpl;
import net.joinwork.bpm.task.WorkitemDAO;
import net.joinwork.bpm.task.WorkitemInfo;
import net.joinwork.bpm.task.log.BPMCaseWorkItemLog;
import net.joinwork.bpm.util.Utils;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputValueAll;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.MessageData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.domain.ProcessLog;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.ProcessModeSelector;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

public class ColHelper {
    static Log log = LogFactory.getLog(ColHelper.class);
    public static Map<Long, List<MessageData>> messageDataMap = new HashMap<Long, List<MessageData>>();
    public static Map<Long, String> hastenMemberIdsMap = new HashMap<Long, String>();
    public static Map<Long, String> matchPeople = new HashMap<Long, String>();
    public static Map<Long,List<ProcessLog>> processLogMetaMap = new HashMap<Long,List<ProcessLog>>();

    private static OrgManager orgManager = null;
    private static TempleteManager templeteManager =null;
    private static OrgManager getOrgManager(){
    	if(orgManager == null){
    		orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
    	}

    	return orgManager;
    }
    private static TempleteManager getTempleteManager(){
    	if(templeteManager == null){
    		templeteManager = (TempleteManager)ApplicationContextHolder.getBean("templeteManager");
    	}

    	return templeteManager;
    }
    public static ProcessEngine getProcessEngine() throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            return engine;
        } catch (BPMException e) {
            throw new ColException("getProcessEngine Failed", e);
        }
    }

    public static WorkItemManager getWorkitemManager() throws ColException {
        WorkItemManager wim = null;
        try {
            wim = WAPIFactory.getWorkItemManager("Task_1");
        } catch (BPMException e) {
            throw new ColException("getWorkitemManager Failed", e);
        }
        return wim;
    }

    public static String saveOrUpdateProcessByFlowData(FlowData flowData, String processId, boolean isTemplate) throws ColException {
    	return saveOrUpdateProcessByFlowData(flowData, processId, isTemplate, null);
    }
    public static String saveOrUpdateProcessByFlowData(FlowData flowData, String processId, boolean isTemplate, V3xOrgMember sender) throws ColException {
    	String result = null;
        String desc_by = flowData.getDesc_by();
        if (FlowData.DESC_BY_XML.equals(desc_by)) {
            String xml = flowData.getXml();
            result = saveOrUpdateProcessByXML(xml, processId, flowData.getAddition(), flowData.getCondition(), sender);
        } else {
            result = saveOrUpdateProcessByPeople(flowData, processId,isTemplate,sender);
        }
        //设置自定义节点引用
        /*Map<String,String> usedPolicys = flowData.getUsedPolicy();
        if(usedPolicys!=null && usedPolicys.size()>0) {
        	PermissionManager permissionManager = (PermissionManager)ApplicationContextHolder.getBean("permissionManager");
        	Set<String> keySet = usedPolicys.keySet();
        	Iterator<String> it = keySet.iterator();
        	while(it.hasNext()) {
        		String key = it.next();
        		Boolean updateSuccess = permissionManager.updateIsRef(usedPolicys.get(key),key , 1, user.getLoginAccount());
        		if(!updateSuccess){
        			return null;
        		}
        	}
        }*/
        return result;
    }

    public static void deleteReadyProcess(String processId) throws ColException {
    	try {
            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            pdm.deleteProcessInReady("admin", processId);
        } catch (Exception ex) {
            throw new ColException("删除ReadyProcess异常[processId = " + processId + "]", ex);
        }
    }

    public static String saveOrUpdateProcessByFlowData1(FlowData flowData, String processId,boolean isTemplate,User user) throws ColException {
    	String result = null;
    	String desc_by = flowData.getDesc_by();
    	if (FlowData.DESC_BY_XML.equals(desc_by)) {
    		String xml = flowData.getXml();
    		result = saveOrUpdateProcessByXML1(xml, processId, flowData.getAddition(), flowData.getCondition(), user);
    	} else {
    		result = saveOrUpdateProcessByPeople1(flowData, processId, isTemplate, user);
    	}
    	return result;
    }

    /**
     * 为匹配下节点做准备，构造当前节点及获取流程定义模版
     *
     * @author jincm 2008-3-22
     * @param xml 流程定义xml
     * @param currentNodeId 当前节点ID
     * @return WorkflowEventListener.ProcessModeSelector节点匹配结果对象
     */
    public static WorkflowEventListener.ProcessModeSelector preRunCase(String xml, String currentNodeId, boolean isFromTemplate, Long caseId) throws ColException {
        BPMProcess process = BPMProcess.fromXML(xml);
        return preRunCase(process, currentNodeId, isFromTemplate, caseId);
    }

    /**
     * 如果processId为null则为添加模板，否则为更新
     * 根据XML生成/更新流程
     *
     * @param xml
     * @param processId
     * @return processId
     */
    public static String saveOrUpdateProcessByXML(String xml, String processId, Map addition, Map condition, V3xOrgMember startMember) throws ColException {
        boolean isNewProcess = (processId == null);
        if (isNewProcess)
            processId = UUIDLong.longUUID() + "";
        BPMProcess process = null;
        //不论isNewProcess为true/false，均新建一个process实例。如果不为新，则最后update(delete & create)它�?
        process = BPMProcess.fromXML(xml);
        BPMStatus start = process.getStart();

        OrgManager orgManager = getOrgManager();
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();
        String _accountId = accountId.toString();
        Long startUserId = user.getId();
        String startName = user.getName();
        if(startMember != null && !startMember.getId().equals(user.getId())){
        	startUserId = startMember.getId();
        	startName = startMember.getName();
        	//XXX 这里可能会涉及跨单位兼职时人员名称显示问题
        	accountId = startMember.getOrgAccountId();
        	_accountId = accountId.toString();
        }
        start.setName(startName);
        //BPMActivity ac = process.getActivityById("start");
        //List list = process.getActivitiesList();
        String accountShortName = "";
		try {
			V3xOrgAccount account = orgManager.getAccountById(accountId);
			accountShortName = account.getShortname();
		} catch (BusinessException e) {
			log.error("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
			throw new ColException("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
		}

		/* 兼职单位发起的，更改发起者的accountId  Mazc 2010-05-11
		//发起节点的actor的单位Id生成之后就不在做改变
		List<BPMActor> startactorList = start.getActorList();
        BPMActor oldActor = startactorList.get(0);
		String oldAccountId = oldActor.getParty().getAccountId();
		if(oldAccountId != null && !"".equals(oldAccountId) && !"undefined".equals(oldAccountId)){
			_accountId = oldAccountId;
		}
		*/
        Party startParty = new Party("user", startUserId.toString(), startName, _accountId, accountShortName);
        BPMActor startUserActor = createActor(startParty);
        List<BPMActor> actorList = new ArrayList<BPMActor>();
        actorList.add(startUserActor);
        start.setActorList(actorList);

        //根据xml更新process
        process.setId(processId);
        process.setIndex(processId);
        process.setName(processId);
        Date now = new Date(System.currentTimeMillis());
        if (isNewProcess) {
            process.setCreateDate(now);
            process.setUpdateDate(now);
        } else {
            process.setUpdateDate(now);
        }
        try {
            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            if(addition!=null && !addition.isEmpty()){
                setActivityManualSelect(process,addition);
            }
            //设置节点是否进行逻辑删除
            if(condition != null && !condition.isEmpty()){
            	setActivityIsDelete(process, condition);
            }
            pdm.saveOrUpdateProcessInReady(process);

        } catch (Exception ex) {
            throw new ColException("获取引擎对外接口异常", ex);
        }
        return process.getId();
    }

    private static String saveOrUpdateProcessByXML1(String xml, String processId, Map addition, Map condition, User _user) throws ColException {
    	boolean isNewProcess = (processId == null);
    	if (isNewProcess)
    		processId = UUIDLong.longUUID() + "";
    	BPMProcess process = null;
    	//不论isNewProcess为true/false，均新建一个process实例。如果不为新，则最后update(delete & create)它�?
    	process = BPMProcess.fromXML(xml);
    	BPMStatus start = process.getStart();
    	User user = _user;
    	start.setName(user.getName());

    	Long startId = user.getId();
        String startName = user.getName();
        Long accountId = user.getLoginAccount();
        OrgManager orgManager = getOrgManager();
        String accountShortName = "";
		try {
			V3xOrgAccount account = orgManager.getAccountById(accountId);
			accountShortName = account.getShortname();
		} catch (BusinessException e) {
			log.error("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
			throw new ColException("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
		}
        Party startParty = new Party("user", startId.toString(), startName, accountId.toString(), accountShortName);
        BPMActor startUserActor = createActor(startParty);
        List<BPMActor> actorList = new ArrayList<BPMActor>();
        actorList.add(startUserActor);
        start.setActorList(actorList);

    	//根据xml更新process
    	//new BPMProcess(processId, processId);
    	//process.fromXML(xml);
    	process.setId(processId);
    	process.setIndex(processId);
    	process.setName(processId);
    	Date now = new Date(System.currentTimeMillis());
    	if (isNewProcess) {
    		process.setCreateDate(now);
    		process.setUpdateDate(now);
    	} else {
    		process.setUpdateDate(now);
    	}
    	try {
    		ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
    		if(addition!=null && !addition.isEmpty()){
    			setActivityManualSelect(process,addition);
    		}
    		//设置节点是否进行逻辑删除
    		if(condition != null && !condition.isEmpty()){
    			setActivityIsDelete(process, condition);
    		}
    		pdm.saveOrUpdateProcessInReady(process);

    	} catch (Exception ex) {
    		throw new ColException("获取引擎对外接口异常", ex);
    	}
    	return process.getId();
    }

    /**
     * 如果processId为null则为添加模板，否则为更新
     * 根据选人界面生成/更新流程
     *
     * @param flowData
     * @param processId
     * @return 生成或传入的processId
     */
    private static String saveOrUpdateProcessByPeople(FlowData flowData, String processId, boolean isTemplate) throws ColException {
    	return saveOrUpdateProcessByPeople(flowData, processId, isTemplate, null);
    }
    private static String saveOrUpdateProcessByPeople(FlowData flowData, String processId, boolean isTemplate, V3xOrgMember startMember) throws ColException {
        User user = CurrentUser.get();
        List<Party> people = flowData.getPeople();
        int flowType = flowData.getType();
        String isShowShortName = flowData.getIsShowShortName();
        boolean isNewProcess = (processId == null);
        if (isNewProcess)
            processId = UUIDLong.longUUID() + "";

        BPMProcess process = null;

        //不论isNewProcess为true/false，均新建一个process实例。如果不为新，则最后update(delete & create)它�?
        process = new BPMProcess(processId, processId);
        process.setIndex(processId);

        process.setIsShowShortName(isShowShortName);
        BPMAbstractNode nodeStart = new BPMStart("start", user.getName());

        //构造发起人Actor
        Long startId = user.getId();
        String startName = user.getName();
        Long accountId = user.getLoginAccount();
        OrgManager orgManager = getOrgManager();
        boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
        if(isGov) {
            if(startMember != null && !startMember.getId().equals(user.getId())){
            	startId = startMember.getId();
            	startName = startMember.getName();
            	//XXX 这里可能会涉及跨单位兼职时人员名称显示问题
            	accountId = startMember.getOrgAccountId();
            }
        }
        String accountShortName = "";
		try {
			V3xOrgAccount account = orgManager.getAccountById(accountId);
			accountShortName = account.getShortname();
		} catch (BusinessException e) {
			log.error("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
			throw new ColException("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
		}
		Party startParty = new Party("user", startId.toString(), startName, accountId.toString(), accountShortName);
        BPMActor startUserActor = createActor(startParty);
        nodeStart.addActor(startUserActor);
        BPMSeeyonPolicy seeyonPolicy = new BPMSeeyonPolicy();
        seeyonPolicy.setMatchScope("1");
        seeyonPolicy.setProcessMode("multiple");
        seeyonPolicy.setIsDelete("false");
        seeyonPolicy.setIsPass("success");
        if(flowData.getSeeyonPolicy() != null)
        	seeyonPolicy.setId(flowData.getSeeyonPolicy().getId());
        nodeStart.setSeeyonPolicy(seeyonPolicy);

        BPMAbstractNode nodeEnd = new BPMEnd("end", "end");

        process.addChild(nodeStart);
        process.addChild(nodeEnd);

        if (flowType == FlowData.FLOWTYPE_SERIAL || (flowType == FlowData.FLOWTYPE_PARALLEL && people.size() == 1)) {
            BPMAbstractNode prevNode = null;
            for (int i = 0; i < people.size(); i++) {
                Party party = people.get(i);

                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.getName());
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);

                //设置用户节点属性
                seeyonPolicy = new BPMSeeyonPolicy();
                if(userNode.getSeeyonPolicy() == null){
                	if(flowData.getSeeyonPolicy() != null){
                		BPMSeeyonPolicy _seeyonPolicy = flowData.getSeeyonPolicy();
                		seeyonPolicy.setId(_seeyonPolicy.getId());
                		seeyonPolicy.setName(_seeyonPolicy.getName());
                	}else{
                		seeyonPolicy.setId("collaboration");
                		seeyonPolicy.setName("协同");
                	}

                	String actorType = userActor.getParty().getType().id;
                	if(isTemplate || "user".equals(actorType)){
                		seeyonPolicy.setProcessMode("single");
                    }else{
                    	seeyonPolicy.setProcessMode("all");
                    }
                	userNode.setSeeyonPolicy(seeyonPolicy);
                }

                BPMTransition userLink = null;
                if (prevNode == null) {
                    userLink = new BPMTransition(nodeStart, userNode);
                } else {
                    userLink = new BPMTransition(prevNode, userNode);
                }

                process.addChild(userNode);
                process.addLink(userLink);

                prevNode = userNode;
            }
            prevNode = prevNode != null ? prevNode : nodeStart;
            BPMTransition linkLastNodeToEnd = new BPMTransition(prevNode, nodeEnd);
            process.addLink(linkLastNodeToEnd);
        } else {
        	BPMAndRouter nodeSyncStart = new BPMAndRouter(UUIDLong.longUUID()+"", "split");
            BPMAndRouter nodeSyncEnd = new BPMAndRouter(UUIDLong.longUUID()+"", "join");
            nodeSyncStart.setStartAnd(true);
            nodeSyncEnd.setStartAnd(false);
            String relevancyId = UUIDLong.longUUID() + "";
            nodeSyncStart.setParallelismNodeId(relevancyId);
            nodeSyncEnd.setParallelismNodeId(relevancyId);

            BPMSeeyonPolicy newSeeyonPolicy = new BPMSeeyonPolicy();
            newSeeyonPolicy.setProcessMode("all");
            newSeeyonPolicy.setMatchScope("1");
            newSeeyonPolicy.setIsDelete("false");
            newSeeyonPolicy.setIsPass("success");
            if(flowData.getSeeyonPolicy() != null)
            	newSeeyonPolicy.setId(flowData.getSeeyonPolicy().getId());
            nodeSyncStart.setSeeyonPolicy(newSeeyonPolicy);
            nodeSyncEnd.setSeeyonPolicy(newSeeyonPolicy);

            BPMTransition linkStartToSyncStart = new BPMTransition(nodeStart, nodeSyncStart);
            BPMTransition linkSyncEndToEnd = new BPMTransition(nodeSyncEnd, nodeEnd);
            process.addChild(nodeSyncStart);
            process.addChild(nodeSyncEnd);
            process.addLink(linkStartToSyncStart);
            process.addLink(linkSyncEndToEnd);

            for (int i=(people.size()-1); i>=0; i--) {
                Party party = people.get(i);

                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.getName());
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);

                //设置用户节点属性
                seeyonPolicy = new BPMSeeyonPolicy();
                if(userNode.getSeeyonPolicy() == null){
                	if(flowData.getSeeyonPolicy() != null){
                		BPMSeeyonPolicy _seeyonPolicy = flowData.getSeeyonPolicy();
                		seeyonPolicy.setId(_seeyonPolicy.getId());
                		seeyonPolicy.setName(_seeyonPolicy.getName());
                	}else{
                		seeyonPolicy.setId("collaboration");
                		seeyonPolicy.setName("协同");
                	}


                	String actorType = userActor.getParty().getType().id;
                	if(isTemplate || "user".equals(actorType)){
                		seeyonPolicy.setProcessMode("single");
                    }else{
                    	seeyonPolicy.setProcessMode("all");
                    }
                	userNode.setSeeyonPolicy(seeyonPolicy);
                }

                BPMTransition userLink1 = new BPMTransition(nodeSyncStart, userNode);
                BPMTransition userLink2 = new BPMTransition(userNode, nodeSyncEnd);

                process.addChild(userNode);
                process.addLink(userLink1);
                process.addLink(userLink2);
            }
        }

        Date now = new Date(System.currentTimeMillis());

        if (isNewProcess) {
            process.setCreateDate(now);
            process.setUpdateDate(now);

        } else {
            process.setUpdateDate(now);
        }

        try {
            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            pdm.saveOrUpdateProcessInReady(process);
        } catch (Exception ex) {
        	throw new ColException("获取引擎对外接口异常", ex);
        }

        return process.getId();
    }

    private static String saveOrUpdateProcessByPeople1(FlowData flowData, String processId,boolean isTemplate, User _user) throws ColException {
    	User user = _user;
    	List<Party> people = flowData.getPeople();
    	int flowType = flowData.getType();
    	String isShowShortName = flowData.getIsShowShortName();
    	boolean isNewProcess = (processId == null);
    	if (isNewProcess)
    		processId = UUIDLong.longUUID() + "";

    	BPMProcess process = null;

    	//不论isNewProcess为true/false，均新建一个process实例。如果不为新，则最后update(delete & create)它�?
    	process = new BPMProcess(processId, processId);

    	process.setIsShowShortName(isShowShortName);
    	BPMAbstractNode nodeStart = new BPMStart("start", user.getName());

    	//构造发起人Actor
    	Long startId = user.getId();
    	String startName = user.getName();
    	Long accountId = user.getLoginAccount();
    	OrgManager orgManager = getOrgManager();
    	String accountShortName = "";
    	try {
    		V3xOrgAccount account = orgManager.getAccountById(accountId);
    		accountShortName = account.getShortname();
    	} catch (BusinessException e) {
    		log.error("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
			throw new ColException("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
    	}
    	Party startParty = new Party(startId.toString(), startName, "user", accountId.toString(), accountShortName);
    	BPMActor startUserActor = createActor(startParty);
    	nodeStart.addActor(startUserActor);

    	BPMAbstractNode nodeEnd = new BPMEnd("end", "end");
    	process.addChild(nodeStart);
    	process.addChild(nodeEnd);

    	if (flowType == FlowData.FLOWTYPE_SERIAL || (flowType == FlowData.FLOWTYPE_PARALLEL && people.size() == 1)) {
    		BPMAbstractNode prevNode = null;
    		for (int i = 0; i < people.size(); i++) {
    			Party party = people.get(i);

    			BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.getName());
    			if(userNode.getSeeyonPolicy() == null){
    				if(flowData.getSeeyonPolicy() != null){
    					userNode.setSeeyonPolicy(flowData.getSeeyonPolicy());
    				}else{
    					userNode.setSeeyonPolicy(new BPMSeeyonPolicy("collaboration", "协同"));
    				}
    			}
    			if(isTemplate){
    				userNode.getSeeyonPolicy().setProcessMode("single");
    			}else{
    				userNode.getSeeyonPolicy().setProcessMode("all");
    			}

    			BPMActor userActor = createActor(party);
    			userNode.addActor(userActor);


    			BPMTransition userLink = null;
    			if (prevNode == null) {
    				userLink = new BPMTransition(nodeStart, userNode);
    			} else {
    				userLink = new BPMTransition(prevNode, userNode);
    			}

    			process.addChild(userNode);
    			process.addLink(userLink);

    			prevNode = userNode;
    		}
    		prevNode = prevNode != null ? prevNode : nodeStart;
    		BPMTransition linkLastNodeToEnd = new BPMTransition(prevNode, nodeEnd);
    		process.addLink(linkLastNodeToEnd);
    	} else {
    		BPMAndRouter nodeSyncStart = new BPMAndRouter(UUIDLong.longUUID()+"", "split");
    		BPMAndRouter nodeSyncEnd = new BPMAndRouter(UUIDLong.longUUID()+"", "join");
    		nodeSyncStart.setStartAnd(true);
    		nodeSyncEnd.setStartAnd(false);

    		String relevancyId = UUIDLong.longUUID() + "";
    		nodeSyncStart.setParallelismNodeId(relevancyId);
    		nodeSyncEnd.setParallelismNodeId(relevancyId);

    		BPMTransition linkStartToSyncStart = new BPMTransition(nodeStart, nodeSyncStart);
    		BPMTransition linkSyncEndToEnd = new BPMTransition(nodeSyncEnd, nodeEnd);
    		process.addChild(nodeSyncStart);
    		process.addChild(nodeSyncEnd);
    		process.addLink(linkStartToSyncStart);
    		process.addLink(linkSyncEndToEnd);
    		for (int i = 0; i < people.size(); i++) {
    			Party party = people.get(i);
    			BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.getName());
    			if(userNode.getSeeyonPolicy() == null){
    				if(flowData.getSeeyonPolicy() != null){
    					userNode.setSeeyonPolicy(flowData.getSeeyonPolicy());
    				}else{
    					userNode.setSeeyonPolicy(new BPMSeeyonPolicy("collaboration", "协同"));
    				}
    			}
    			if(isTemplate){
    				userNode.getSeeyonPolicy().setProcessMode("single");
    			}else{
    				userNode.getSeeyonPolicy().setProcessMode("all");
    			}
    			BPMActor userActor = createActor(party);
    			userNode.addActor(userActor);

    			BPMTransition userLink1 = new BPMTransition(nodeSyncStart, userNode);
    			BPMTransition userLink2 = new BPMTransition(userNode, nodeSyncEnd);

    			process.addChild(userNode);
    			process.addLink(userLink1);
    			process.addLink(userLink2);
    		}
    	}

    	Date now = new Date(System.currentTimeMillis());

    	if (isNewProcess) {
    		process.setCreateDate(now);
    		process.setUpdateDate(now);

    	} else {
    		process.setUpdateDate(now);
    	}

    	try {
    		ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
//            System.out.println("process by people:");
//            System.out.println(process.toXML());
    		pdm.saveOrUpdateProcessInReady(process);
    	} catch (Exception ex) {
    		throw new ColException("获取引擎对外接口异常", ex);
    	}

    	return process.getId();
    }


    /**
     * 返回caseId
     *
     * @param processName
     * @return
     * @throws ColException
     */
    public static long runCase(String processName) throws ColException {
    	return runCase(processName, CurrentUser.get().getId());
    }

    public static long runCase(String processName, long senderId) throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            String caseName = UUIDLong.longUUID() + "";
            //processName = "activity_status";
            Map dataMap=new HashMap();
            dataMap.put("appName",ApplicationCategoryEnum.collaboration.name());
            long caseId = engine.runCase(String.valueOf(senderId), processName,
                    caseName, dataMap, null, null, false);

            return caseId;
        } catch (Exception ex) {
        	throw new ColException("获取引擎对外接口异常[ColHelper.runCase]", ex);
        }
    }

    public static long runCase1(String processName, User user) throws ColException {
    	return runCase(processName, user.getId());
    }
    public static long runCase(String processName,ApplicationCategoryEnum app) throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            String caseName = UUIDLong.longUUID() + "";
            Map dataMap=new HashMap();
            dataMap.put("appName",app.name());
            long caseId = engine.runCase(CurrentUser.get().getId() + "", processName,
                    caseName, dataMap, null, null, false);

            return caseId;
        } catch (Exception ex) {
        	throw new ColException("获取引擎对外接口异常", ex);
        }
    }

    public static void finishWorkitem(Long workItemId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            wim.finishWorkItem(userId, workItemId, null, null, null, null);
        } catch (BPMException ex) {
        	if(BPMException.EXCEPTION_CODE_WORKITEM_NOT_EXITE.equals(ex.getExceptionCode())){
        		log.warn("可能是数据到了历史表，重新移到运行表中，尝试重新提交一次。");
        		try{
	        		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
	        		WorkItem wi= wim.getHistortyWorkItemInfo(userId, workItemId);
	        		if(null!=wi){
	        			wim.moveWorkitemToRun(workItemId);
	        		}
	        		//再尝试提交一次
	        		wim.finishWorkItem(userId, workItemId, null, null, null, null);
        		}catch(BPMException ex1){
        			log.error(ex1);
    	        	throw new ColException("获取工作项管理对外接口异常[ColHelper.finishWorkitem]", ex1);
        		}
        	}else{
	        	log.error(ex);
	        	throw new ColException("获取工作项管理对外接口异常[ColHelper.finishWorkitem]", ex);
        	}
        }
    }

    public static WorkItem getWorkItemById(Long workitemId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        WorkItem wi = null;
        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            if(wim != null){
                wi = wim.getWorkItemOrHistory(userId, workitemId);
            }
        } catch (BPMException ex) {
        	if(BPMException.EXCEPTION_CODE_WORKITEM_NOT_EXITE.equals(ex.getExceptionCode())){//workitem不存在了，造一条类似的数据
                try{
                    log.error("workitem["+workitemId+"]不存在了，重新产生一条一模一样的待办数据，开始...");
                    wi= createWorkItemForException(userId,workitemId);
                    log.error("workitem["+workitemId+"]不存在了，重新产生一条一模一样的待办数据，成功...");
                }catch(Throwable e){
                    log.error("workitem["+workitemId+"]不存在了，重新产生一条一模一样的待办数据，但失败了!",e);
                    throw new ColException("获取工作项管理对外接口异常[ColHelper.getWorkItemById]", e);
                }
            }
        }
        return wi;
    }

    /**
     * 容错，workitem不存在了，造一条类似的数据
     * @param workitemId
     * @throws BPMException
     */
    private static WorkItem createWorkItemForException(String userId,Long workitemId) throws BPMException {
        AffairManager affairManager= (AffairManager)ApplicationContextHolder.getBean("affairManager");
        ColManager colManager= (ColManager)ApplicationContextHolder.getBean("colManager");
        EdocManager edocManager= (EdocManager)ApplicationContextHolder.getBean("edocManager");
        Affair myAffair= affairManager.getBySubObject(workitemId);
        WorkItemManagerImpl itemManager = (WorkItemManagerImpl) WAPIFactory.getWorkItemManager("Task_1");
        WorkitemDAO workitem = new WorkitemDAO();
        workitem.setId(workitemId);
        Long summaryId= myAffair.getObjectId();
        List<Affair> affairs= affairManager.getAffairBySummaryIdAndActivityId(summaryId, myAffair.getActivityId());
        int itemNum= 1;
        if(null!=affairs){
            itemNum= affairs.size();
        }
        long batch= -1l;
        String processId= null;
        Long caseId= -1l;
        ColSummary colSummary= null;
        try {
            colSummary = colManager.getColSummaryById(summaryId, false);
            processId= colSummary.getProcessId();
            caseId= colSummary.getCaseId();
            log.error("修复的协同待办数据Id:"+summaryId+" 标题为："+colSummary.getSubject());
        } catch (Throwable e1) {
            log.error("colSummary不存在，",e1);
        }
        EdocSummary edocSummary= null;
        if(null==colSummary){
            try {
                edocSummary= edocManager.getEdocSummaryById(summaryId, false);
                processId= edocSummary.getProcessId();
                caseId= edocSummary.getCaseId();
                log.error("修复的公文待办数据Id:"+summaryId+" 标题为："+edocSummary.getSubject());
            } catch (Throwable e) {
                log.error("colSummary不存在，",e);
            }
        }

        Long otherWorkitemId= -1l;
        if(null!=affairs && affairs.size()>1){
            for (Affair affair : affairs) {
                if(affair.getId()!=myAffair.getId()){
                    otherWorkitemId= affair.getSubObjectId();
                    try{
                        WorkitemInfo wi= (WorkitemInfo)itemManager.getWorkItemOrHistory(userId, otherWorkitemId);
                        if(wi!=null){
                            batch= wi.getBatch();
                            break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        continue;
                    }
                }
            }

        }else{
            batch= workitem.getId();
        }
        workitem.setBatch(batch);
        workitem.setItemNum(itemNum);
        ProcessEngineImpl engine = (ProcessEngineImpl) WAPIFactory.getProcessEngine("Engine_1");
        if (engine == null){
            throw new BPMException(
                    BPMException.EXCEPTION_CODE_ENGINE_DOMAIN_NOT_EXIST,
                    new Object[] { "Engine_1" });
        }

        BPMProcess process = engine.getProcess(processId);
        BPMHumenActivity humenActivity = ((BPMHumenActivity) process.getActivityById(myAffair.getActivityId().toString()));
        workitem.init(processId,processId,processId,caseId.toString(),caseId,humenActivity);
        workitem.setEngineDomain("Engine_1");
        workitem.setDataMap(new HashMap());
        workitem.setPerformer(myAffair.getMemberId()+"");
        workitem.setState(WorkItem.STATE_READY);
        workitem.setSort(1);
        if(myAffair.getState()==3){//待办状态
            log.error("workitem["+workitemId+"]不存在了,自动补到workitem_run表,开始!");
            workitem.setState(WorkItem.STATE_READY);
            itemManager.addWorkItem(workitem);
            log.error("workitem["+workitemId+"]不存在了,自动补到workitem_run表,成功!");
        }else{
            log.error("workitem["+workitemId+"]不存在了,自动补到workitem_history表,开始!");
            HistoryWorkitemDAO dao= new HistoryWorkitemDAO();
            workitem.clone2(dao);
            dao.setState(WorkItem.STATE_FINISHED);
            itemManager.addHistoryWorkItem(dao);
            log.error("workitem["+workitemId+"]不存在了,自动补到workitem_history表,成功!");
        }
        return workitem;
    }

    public static String getActvityIdByAffair(Affair affair) throws ColException {
        if(affair.getActivityId() != null){
        	return String.valueOf(affair.getActivityId());
        }

        Long workitemId = affair.getSubObjectId();
        if (workitemId == null || workitemId == 0) {
            return null;
        }

        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            if(wim != null){
                return wim.getActvityIdByWorkItemId(affair.getSubObjectId());
            }
        }
        catch (BPMException ex) {
        	throw new ColException("获取工作项管理对外接口异常[ColHelper.getWorkItemById]", ex);
        }

        return null;
    }

    public static List<BPMHumenActivity> findDirectHumenChildrenCascade(BPMAbstractNode current_node, boolean isInformRecur) {
        List<BPMHumenActivity> result = new ArrayList<BPMHumenActivity>();
        List<BPMTransition> down_links = current_node.getDownTransitions();
        for (BPMTransition down_link : down_links) {
            BPMAbstractNode _node = down_link.getTo();
            if (_node instanceof BPMHumenActivity) {
                BPMHumenActivity node = (BPMHumenActivity) _node;
                result.add(node);
            	if(isInformRecur && isInform(node)){
            		List<BPMHumenActivity> children = findDirectHumenChildrenCascade(_node,isInformRecur);
                    result.addAll(children);
            	}
            } else if (_node instanceof BPMAndRouter || _node instanceof BPMConRouter) {
                List<BPMHumenActivity> children = findDirectHumenChildrenCascade(_node,isInformRecur);
                result.addAll(children);
            } else if (_node instanceof BPMEnd) {
                return new ArrayList<BPMHumenActivity>(0);
            }
        }
        return result;
    }
    public static boolean isHasTerm(List<BPMHumenActivity> activityList){
    	for(BPMHumenActivity _activity : activityList){
    		 List<BPMTransition> up_links = _activity.getUpTransitions();
    		 if(up_links!=null){
    			 BPMTransition upLink = up_links.get(0);
    			 if(upLink!=null && upLink.getConditionType()!=0 && upLink.getConditionType()!=3){
    				 return true;
    			 }
    		 }
		}
    	return false;
    }

    public static boolean isAllInform(List<BPMHumenActivity> activityList){
    	for(BPMHumenActivity _activity : activityList){
			if(!isInform(_activity)){
				return false;
			}
		}
    	return true;
    }

    public static boolean isInform(BPMAbstractNode activity){
    	String _nodePolicy = activity.getSeeyonPolicy().getId();
		if("inform".equals(_nodePolicy) || "zhihui".equals(_nodePolicy)){
			return true;
		}
		return false;
    }
    /**
     * 节点是否毗邻Join节点，在Join节点之前。
     * 考虑了穿透知会，如果与所在分支join之间全是知会节点则返回true，否则返回false。
     * 如果不在任何分支的环中，返回false。
     * @param node
     */
    public static boolean isBeforeJoin(BPMAbstractNode node){
    	BPMAndRouter join = findNextJoin(node);
    	return isAllInform(node, join);
    }
    /**
     * 节点之间是否全都是知会节点。
     * @param from 搜索开始点
     * @param to 搜索终点，from必须在to的前面
     * @return 全知会返回true，只要有一个不是就返回false。
     */
    public static boolean isAllInform(BPMAbstractNode node1,BPMAbstractNode node2){
		List<BPMTransition> links = node1.getDownTransitions();
		if(links==null) return false;
		if(!passThrough(node1,node2)) return false;
		for (BPMTransition link : links) {
			BPMAbstractNode to = link.getTo();
			// 略过不在两节点之间的路线
			if(!passThrough(to, node2))continue;
			if(!isInform(to)) return false;
			if(!isAllInform(to,node2)) return false;
		}
    	return true;
    }
    /**
     * 返回节点所在分支的Split节点。
     * 如果没有，返回null。
     * 忽略与之串行的闭合Split－Join环。
     * @param activity
     * @return
     */
    public static BPMAndRouter findPreviousSplit(BPMAbstractNode node){
		List<BPMTransition> links = node.getUpTransitions();
		if(links==null) return null;
		for (BPMTransition link : links) {
			BPMAbstractNode from = link.getFrom();
			if(isSplitNode(from)) return (BPMAndRouter)from;
			if(isJoinNode(from)){
				// 可惜没有findSplitOfJoin方法，如果有就可以直接略过中间从Split向前查
				BPMAndRouter joinSplit = findPreviousSplit(from);
				return findPreviousSplit(joinSplit);
			}else{
				return findPreviousSplit(from);
			}
		}
		return null;
    }
    /**
     * 返回节点所在分支的Join节点。
     * 如果没有，返回null。
     * 忽略与之串行的闭合Split－Join环。
     * @param node
     * @return
     */
    public static BPMAndRouter findNextJoin(BPMAbstractNode node){
		List<BPMTransition> links = node.getDownTransitions();
		if(links==null) return null;
		for (BPMTransition link : links) {
			BPMAbstractNode to = link.getTo();
			if(isJoinNode(to)) return (BPMAndRouter)to;
			if(isSplitNode(to)){
				BPMAndRouter join = findJoinOfSplit(null, (BPMAndRouter)to);
				return findNextJoin(join);
			}else{
				return findNextJoin(to);
			}
		}
		return null;
    }

    public static BPMActivity getActivityByWorkitem(WorkItem workitem){
        String activityId = workitem.getActivityId();
        long caseId = workitem.getCaseId();
        ProcessEngine pe = null;
        try {
            pe = getProcessEngine();
        } catch (ColException e) {
        	log.error("获取流程引擎对外接口对象异常", e);
        }
        BPMProcess process = null;
        try {
            process = (BPMProcess) pe.getCaseProcess(caseId);
        } catch (BPMException e) {
        	log.error("获取process异常 [caseId = " + caseId + "]", e);
        }

        BPMActivity activity = process.getActivityById(activityId);
        return activity;
    }

    public static SeeyonPolicy getPolicyByAffair(BPMProcess process, Affair affair) throws ColException {
        if (affair == null)
            return new SeeyonPolicy("collaboration","协同");

        String activityId = getActvityIdByAffair(affair);

        BPMActivity activity = process.getActivityById(activityId);
        BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
        return new SeeyonPolicy(seeyonPolicy.getId(), seeyonPolicy.getName());
    }

    public static SeeyonPolicy getPolicyByAffair(Affair affair) throws ColException {
        if (affair == null)
            return new SeeyonPolicy("collaboration","协同");

        if(Strings.isNotBlank(affair.getNodePolicy())){
        	return new SeeyonPolicy(affair.getNodePolicy(), BPMSeeyonPolicy.getShowName(affair.getNodePolicy()));
        }

        Long workitemId = affair.getSubObjectId();
        if (workitemId == null || workitemId == 0) {
            return null;
        }

        WorkItem workitem = getWorkItemById(workitemId);
        String activityId = workitem.getActivityId();
        long caseId = workitem.getCaseId();
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
            process = (BPMProcess) pe.getCaseProcess(caseId);
        } catch (BPMException e) {
        	throw new ColException("获取流程process异常 [caseId = " + caseId + "]", e);
        }

        BPMActivity activity = process.getActivityById(activityId);
        BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
        return new SeeyonPolicy(seeyonPolicy.getId(), seeyonPolicy.getName());

    }

    public static Long getCaseIdByWorkitem(Long workitemId) throws ColException {
        WorkItem wi = null;
        try {
            wi = getWorkItemById(workitemId);
        } catch (ColException ex) {
            return null;//throw new ColException(ex);
        }
        if (wi == null)
            return null;
        long caseId = wi.getCaseId();
        return caseId;
    }

    public static String getCaseLogXML(String userId, long caseId) throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            return trimXMLProcessor(engine.getCaseLogXML(userId, caseId));
        } catch (BPMException e) {
            throw new ColException("获取流程caseLogXML异常 [caseId = " +caseId+ "]", e);
        }
    }
    
    public static String getHisCaseLogXML(String userId, long caseId) throws ColException {
    	try {
    		ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
    		return trimXMLProcessor(engine.getHisCaseLogXML(userId, caseId));
    	} catch (BPMException e) {
    		throw new ColException("获取流程caseLogXML异常 [caseId = " +caseId+ "]", e);
    	}
    }

    public static List<BPMCaseWorkItemLog> getCaseWorkItemLogList(String userId, Long caseId) throws ColException {
    	try {
    		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
    		return wim.getCaseWorkItemLogList(userId, caseId);
    	} catch (BPMException e) {
    		throw new ColException("获取流程WorkItemLogXML异常 [caseId = " +caseId+ "]", e);
    	}
    }

    public static String getCaseWorkItemLogXML(String userId, Long caseId) throws ColException {
        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            return trimXMLProcessor(wim.getCaseWorkItemLogXML(userId, caseId));
        } catch (BPMException e) {
        	throw new ColException("获取流程WorkItemLogXML异常 [caseId = " +caseId+ "]", e);
        }
    }
    
    public static String getHisCaseWorkItemLogXML(String userId, Long caseId) throws ColException {
    	try {
    		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
    		return trimXMLProcessor(wim.getHisCaseWorkItemLogXML(userId, caseId));
    	} catch (BPMException e) {
    		throw new ColException("获取流程WorkItemLogXML异常 [caseId = " +caseId+ "]", e);
    	}
    }

    public static String getCaseProcessXML(String processId) throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            return trimXMLProcessor(engine.getCaseProcessXML(processId));
        } catch (BPMException e) {
        	throw new ColException("获取流程ProcessXML异常 [processId = " +processId+ "]", e);
        }
    }
    
    public static String getHisCaseProcessXML(String processId) throws ColException {
    	try {
    		ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
    		return trimXMLProcessor(engine.getHisCaseProcessXML(processId));
    	} catch (BPMException e) {
    		throw new ColException("获取流程ProcessXML异常 [processId = " +processId+ "]", e);
    	}
    }

    public static String getCaseProcessXML(String userId, Long caseId) throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            return trimXMLProcessor(engine.getCaseProcessXML(userId, caseId));
        } catch (BPMException e) {
        	throw new ColException("获取流程ProcessXML异常 [caseId = " +caseId+ "]", e);
        }
    }

    public static String trimXMLProcessor(String xml) {
        if (xml == null)
            return null;

        int begin = xml.indexOf("<?");
        if (begin != -1) {
            int end = xml.indexOf("?>", begin + 1);
            if (end != -1) {
                return xml.substring(end + 3);
            }
        }
        return xml;
    }

    //转发
    public static void forwardCol(Long summaryId, boolean forwardOpinion, List attachmentsAdding) {

    }

    //撤消流程
    //1:流程已结束，不许撤消�?0:撤消完成
    public static int cancelCase(Long caseId) throws ColException {
        User user = CurrentUser.get();
        ProcessEngine processEngine = getProcessEngine();
        try {
            BPMCase theCase = processEngine.getCase(caseId);
            int theCaseState = theCase.getState();
            if (theCaseState == CaseInfo.STATE_FINISHED || theCaseState == CaseInfo.STATE_CANCEL) return 1;
            BPMProcess process = (BPMProcess)processEngine.getProcessRunningById(theCase.getProcessId());
            int returnValue = processEngine.cancelCase(String.valueOf(user.getId()), theCase, process);
            return returnValue;
        } catch (BPMException e) {
        	throw new ColException("撤销流程异常 [caseId = " +caseId+ "]", e);
        }
    }
    
    public static int cancelCaseForce(Long caseId) throws ColException {
        User user = CurrentUser.get();
        ProcessEngine processEngine = getProcessEngine();
        try {
            BPMCase theCase = processEngine.getCase(caseId);
            int theCaseState = theCase.getState();
            if (theCaseState == CaseInfo.STATE_FINISHED || theCaseState == CaseInfo.STATE_CANCEL) return 0;
            BPMProcess process = (BPMProcess)processEngine.getProcessRunningById(theCase.getProcessId());
            int returnValue = processEngine.cancelCase(String.valueOf(user.getId()), theCase, process);
            return returnValue;
        } catch (BPMException e) {
        	log.error("撤销流程异常 [caseId = " +caseId+ "]",e);
            throw new ColException("撤销流程异常 [caseId = " +caseId+ "]", e);
        }
    }

    //加签
    public static List<String> insertPeople(Long caseId, Long workitemId, FlowData flowData, BPMProcess process, String userId, boolean isFormOperationReadonly) throws ColException {
        List<Party> people = flowData.getPeople();
        ProcessEngine pe = getProcessEngine();
        List<String> memAndPolicyName = null;
	    try {
	        if (flowData.getType() == FlowData.FLOWTYPE_SERIAL
	        		|| (people.size() == 1 && flowData.getType() != FlowData.FLOWTYPE_NEXTPARALLEL)) {
	        	//串发
	        	memAndPolicyName = insertSerial(process, workitemId, flowData, new BPMSeeyonPolicy("collaboration","协同"), isFormOperationReadonly);
	        }
	        else if(flowData.getType() == FlowData.FLOWTYPE_PARALLEL && people.size() != 1){
	        	//并发
	        	memAndPolicyName = insertParellel(process, workitemId, flowData, new BPMSeeyonPolicy("collaboration","协同"), isFormOperationReadonly);
	        }else{
	        	//与后一节点并发
	        	memAndPolicyName = insertNextParellel(process, workitemId, flowData, new BPMSeeyonPolicy("collaboration","协同"), isFormOperationReadonly);
	        }
	        process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
	    }catch (Exception e) {
	    	throw new ColException("加签异常 [caseId = " +caseId+ "]", e);
	    }
	    return memAndPolicyName;
    }

    //加签 - 与后一节点并发
    public static List<String> insertNextParellel(BPMProcess process, Long workitemId, FlowData flowData, BPMSeeyonPolicy policy, boolean isFormOperationReadonly) throws ColException {
    	List<Party> people=flowData.getPeople();
    	User user = CurrentUser.get();
    	List<String> memAndPolicyName = new ArrayList<String>();
    	try {
    		WorkItem workitem = ColHelper.getWorkItemById(workitemId);
    		BPMActivity currentActivity = process.getActivityById(workitem.getActivityId());
    		List downTransitions = currentActivity.getDownTransitions();
    		BPMTransition nextTrans = (BPMTransition) downTransitions.get(0);
    		BPMAndRouter split = null;
    		BPMAndRouter join = null;
    		//查看下一节点的节点类型
			BPMAbstractNode childNode = nextTrans.getTo();
			BPMAbstractNode.NodeType nodeType = childNode.getNodeType();
			if(nodeType == BPMAbstractNode.NodeType.end ||
					nodeType == BPMAbstractNode.NodeType.join){
				return null;
			}else if(nodeType == BPMAbstractNode.NodeType.split){
				split = (BPMAndRouter)childNode;
                join = findJoinOfSplit(process,split);

			}else if(nodeType == BPMAbstractNode.NodeType.humen){
				String splitId = UUIDLong.longUUID()+"";
    			String joinId = UUIDLong.longUUID()+"";
    			split = new BPMAndRouter(splitId, "split");
    			join = new BPMAndRouter(joinId, "join");
    			split.setStartAnd(true);
    			join.setStartAnd(false);
    			String relevancyId = UUIDLong.longUUID() + "";
    			split.setParallelismNodeId(relevancyId);
    			join.setParallelismNodeId(relevancyId);
    			process.addChild(split);
    			process.addChild(join);

    			BPMTransition nextChildTrans = (BPMTransition)childNode.getDownTransitions().get(0);
    			BPMAbstractNode nextChildNode = nextChildTrans.getTo();
    			BPMTransition trans1 = new BPMTransition(currentActivity, split);
    			BPMTransition trans2 = new BPMTransition(split, childNode);
    			BPMTransition trans3 = new BPMTransition(childNode, join);
    			BPMTransition trans4 = new BPMTransition(join, nextChildNode);
    			copyCondition(nextTrans, trans1);
    			copyCondition(nextChildTrans, trans4);

    			process.addLink(trans1);
    			process.addLink(trans2);
    			process.addLink(trans3);
    			process.addLink(trans4);

    			process.removeLink(nextTrans);
    			currentActivity.removeDownTransition(nextTrans);
    			childNode.removeUpTransition(nextTrans);

    			process.removeLink(nextChildTrans);
    			childNode.removeDownTransition(nextChildTrans);
    			nextChildNode.removeUpTransition(nextChildTrans);
			}

    		//向split、join之间添加新结�点
    		String formApp = "";
    		String form = "";
    		String operationName = "";
    		BPMSeeyonPolicy seeyonPolicy = currentActivity.getSeeyonPolicy();
    		if(seeyonPolicy != null) {
    			formApp = seeyonPolicy.getFormApp();
    			form = seeyonPolicy.getForm();
    			operationName = seeyonPolicy.getOperationName();
    		}
    		for (int i=(people.size()-1); i>=0; i--) {
    			Party party = people.get(i);
    			BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.name);
    			BPMActor userActor = createActor(party);

    			userNode.addActor(userActor);
    			BPMSeeyonPolicy bpmPolicy= null;
    			if(flowData.getSeeyonPolicy() != null){
    				bpmPolicy= createBPMSeeyonPolicy(flowData.getSeeyonPolicy());
    				userNode.setSeeyonPolicy(bpmPolicy);
    			}else{
    				bpmPolicy= createBPMSeeyonPolicy(policy);
    				userNode.setSeeyonPolicy(bpmPolicy);
    			}
    			bpmPolicy.setFormApp(formApp);
    			bpmPolicy.setForm(form);
    			bpmPolicy.setOperationName(operationName);
    			if(Strings.isBlank(bpmPolicy.getProcessMode())){
    				bpmPolicy.setProcessMode("all");
    			}

    			if("user".equals(party.getType())){//具体人的话，为单人执行
            		bpmPolicy.setProcessMode("single");
        		}
    			bpmPolicy.setAdded(true);
    			bpmPolicy.setFR(isFormOperationReadonly? "1" : "");
    			bpmPolicy.setAddedFromId(workitem.getPerformer());

    			BPMTransition userLink1 = new BPMTransition(split, userNode);
    			BPMTransition userLink2 = new BPMTransition(userNode, join);

    			process.addChild(userNode);
    			process.addLink(userLink1);
    			process.addLink(userLink2);
    			memAndPolicyName.add(userNode.getName()+"("+bpmPolicy.getName()+")");
    		}

    		Date now = new Date(System.currentTimeMillis());
    		process.setUpdateDate(now);
    		String isShowShortName = flowData.getIsShowShortName();
    		if("false".equals(isShowShortName)){
    			if("false".equals(process.getIsShowShortName()))
    				process.setIsShowShortName(isShowShortName);
    		}else{
    			process.setIsShowShortName(isShowShortName);
    		}
    	} catch (Throwable e) {
    		throw new ColException("加签并发操作异常", e);
    	}
    	return memAndPolicyName;
    }
    //加签 - 并发
    public static List<String> insertParellel(BPMProcess process, Long workitemId, FlowData flowData, BPMSeeyonPolicy policy, boolean isFormOperationReadonly) throws ColException {
    	List<Party> people=flowData.getPeople();
    	User user = CurrentUser.get();
        List<String> memAndPolicyName = new ArrayList<String>();
        try {
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
            BPMActivity currentActivity = process.getActivityById(workitem.getActivityId());
            List downTransitions = currentActivity.getDownTransitions();
            BPMTransition nextTrans = (BPMTransition) downTransitions.get(0);
            BPMAndRouter split = null;
            BPMAndRouter join = null;
            //查看下一结点是否有split类型�jiedian节点
            for (int i = 0; i < downTransitions.size(); i++) {
                BPMTransition trans = (BPMTransition) downTransitions.get(i);
                if (trans.getTo() instanceof BPMAndRouter) {
                    BPMAndRouter to = (BPMAndRouter) trans.getTo();
                    if (to.isStartAnd()) {
                        split = to;
                    }
                }
            }

            //如果有split结点，遍历找到join结点
            if (split != null) {
                boolean foundJoin = false;
                BPMAbstractNode node = split;
                while (!foundJoin) {
                    BPMTransition trans = (BPMTransition) node.getDownTransitions().get(0);
                    node = trans.getTo();
                    if (node instanceof BPMAndRouter) {
                        BPMAndRouter andNode = (BPMAndRouter) node;
                        if (!andNode.isStartAnd()) {
                        	if(split.getParallelismNodeId().equals(andNode.getParallelismNodeId())){
                        		foundJoin = true;
                        		join = andNode;
                        	}
                        }
                    }
                }
            }

            //如果有split，找到后面的人工结果，从people中排除这些人�
            /*if (split != null) {
                List<BPMTransition> _transList = split.getDownTransitions();
                for (BPMTransition tran : _transList) {
                    BPMAbstractNode node = tran.getTo();
                    Party party = getParty(node);
                    people.remove(party);
                    //Long partyId = party.id;
                    //people.remove(partyId);
                }
            }*/

            //如果没有split结点，新建split和join
            if (split == null) {
                String splitId = UUIDLong.longUUID()+"";
                String joinId = UUIDLong.longUUID()+"";
                split = new BPMAndRouter(splitId, "split");
                join = new BPMAndRouter(joinId, "join");
                split.setStartAnd(true);
                join.setStartAnd(false);
                String relevancyId = UUIDLong.longUUID() + "";
                split.setParallelismNodeId(relevancyId);
                join.setParallelismNodeId(relevancyId);
                process.addChild(split);
                process.addChild(join);

                BPMAbstractNode nextNode = (BPMAbstractNode) ((BPMTransition) downTransitions.get(0)).getTo();
                //如果后面是结束结点或分支结点，split和join之间不设结点，join直接连到结束结点�
                if (!((nextNode instanceof BPMHumenActivity) || (nextNode instanceof BPMTimeActivity))) {
                    BPMTransition trans1 = new BPMTransition(currentActivity, split);
                    BPMTransition trans2 = new BPMTransition(join, nextNode);
                    //currentActivity.addDownTransition(trans1);  //wonder whether this statement is necessary
                    copyCondition(nextTrans, trans2);
                    process.addLink(trans1);
                    process.addLink(trans2);
                    process.removeLink(nextTrans);
                    currentActivity.removeDownTransition(nextTrans);
                }
                //如果后面不是结束结点，将下一结点纳入split/join之中，join之后连接nextNode.nextNode
                else {
                    //BPMTransition next2Trans = (BPMTransition) nextNode.getDownTransitions().get(0);
                    //BPMAbstractNode next2Node = next2Trans.getTo();

                    BPMTransition trans1 = new BPMTransition(currentActivity, split);
                    BPMTransition trans2 = new BPMTransition(join, nextNode);
                    //BPMTransition trans3 = new BPMTransition(split, join);
                    //BPMTransition trans4 = new BPMTransition(nextNode, join);
                    copyCondition(nextTrans, trans2);

                    process.addLink(trans1);
                    process.addLink(trans2);
                    //process.addLink(trans3);
                    //process.addLink(trans4);

                    process.removeLink(nextTrans);
                    //process.removeLink(next2Trans);
                    currentActivity.removeDownTransition(nextTrans);
                    //nextNode.removeDownTransition(next2Trans);

                }
            }else{
            	String splitId = UUIDLong.longUUID()+"";
                String joinId = UUIDLong.longUUID()+"";
                split = new BPMAndRouter(splitId, "split");
                join = new BPMAndRouter(joinId, "join");
                split.setStartAnd(true);
                join.setStartAnd(false);
                String relevancyId = UUIDLong.longUUID() + "";
                split.setParallelismNodeId(relevancyId);
                join.setParallelismNodeId(relevancyId);

                process.addChild(split);
                process.addChild(join);

                BPMAbstractNode nextNode = (BPMAbstractNode) ((BPMTransition) downTransitions.get(0)).getTo();
                BPMTransition trans1 = new BPMTransition(currentActivity, split);
                BPMTransition trans2 = new BPMTransition(join, nextNode);

                copyCondition(nextTrans, trans2);

                process.addLink(trans1);
                process.addLink(trans2);

                process.removeLink(nextTrans);
                currentActivity.removeDownTransition(nextTrans);
            }

            //向split、join之间添加新结�点
            String formApp = "";
            String form = "";
            String operationName = "";
            BPMSeeyonPolicy seeyonPolicy = currentActivity.getSeeyonPolicy();
            if(seeyonPolicy != null) {
            	formApp = seeyonPolicy.getFormApp();
            	form = seeyonPolicy.getForm();
            	operationName = seeyonPolicy.getOperationName();
            }
            for (int i=(people.size()-1); i>=0; i--) {
                Party party = people.get(i);

                //TODO
                //String roleName = "roleadmin";
                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.name);
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);
                //设置操作类型
                userNode.setFromType(flowData.getFromType());
                BPMSeeyonPolicy bpmPolicy= null;
                //多级会签，优先考虑人员设置的权限
                if(party.getSeeyonPolicy() == null) {
	                if(flowData.getSeeyonPolicy() != null){
	                	bpmPolicy= createBPMSeeyonPolicy(flowData.getSeeyonPolicy());
	                	userNode.setSeeyonPolicy(bpmPolicy);
	                }
	                else{
	                	bpmPolicy= createBPMSeeyonPolicy(policy);
	                	userNode.setSeeyonPolicy(bpmPolicy);
	                }
                }
                else {
                	bpmPolicy= party.getSeeyonPolicy();
                	userNode.setSeeyonPolicy(bpmPolicy);
                }

                bpmPolicy.setFormApp(formApp);
                bpmPolicy.setForm(form);
                bpmPolicy.setOperationName(operationName);
            	if(Strings.isBlank(bpmPolicy.getProcessMode())){
            		bpmPolicy.setProcessMode("all");
            	}
            	
            	if("user".equals(party.getType())){//具体人的话，为单人执行
            		bpmPolicy.setProcessMode("single");
        		}

            	//传阅和知会非人节点应该是全体执行
            	if(policy != null && ("yuedu".equals(policy.getId()) || "inform".equals(policy.getId()))){
            		if(!"user".equals(party.getType())){
            			bpmPolicy.setProcessMode("all");
            		}
            	}

            	bpmPolicy.setAdded(true);
            	bpmPolicy.setAddedFromId(workitem.getPerformer());
            	bpmPolicy.setFR(isFormOperationReadonly? "1" : "");

                BPMTransition userLink1 = new BPMTransition(split, userNode);
                BPMTransition userLink2 = new BPMTransition(userNode, join);

                process.addChild(userNode);
                process.addLink(userLink1);
                process.addLink(userLink2);
                memAndPolicyName.add(userNode.getName()+"("+bpmPolicy.getName()+")");
            }

            Date now = new Date(System.currentTimeMillis());
            process.setUpdateDate(now);
            String isShowShortName = flowData.getIsShowShortName();
            if("false".equals(isShowShortName)){
            	if("false".equals(process.getIsShowShortName()))
            		process.setIsShowShortName(isShowShortName);
            }else{
            	process.setIsShowShortName(isShowShortName);
            }
            /*ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            pdm.saveOrUpdateProcessInReady(process);*/
        } catch (Throwable e) {
        	throw new ColException("加签并发操作异常", e);
        }
        return memAndPolicyName;
    }

    //加签 - 串发
    public static List<String> insertSerial(BPMProcess process, Long workitemId, FlowData flowData, BPMSeeyonPolicy policy, boolean isFormOperationReadonly) throws ColException {
    	List<Party> people=flowData.getPeople();
        User user = CurrentUser.get();
        List<String> memAndPolicyName = new ArrayList<String>();
        try {
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
            BPMActivity currentActivity = process.getActivityById(workitem.getActivityId());
            List downTransitions = new ArrayList(currentActivity.getDownTransitions());

            //添加的第一个结�点
            BPMAbstractNode firstNode = null;
            //添加的最后一个结�点
            BPMAbstractNode lastNode = null;
            BPMAbstractNode previousNode = currentActivity;

            String formApp = "";
            String form = "";
            String operationName = "";
            BPMSeeyonPolicy seeyonPolicy = currentActivity.getSeeyonPolicy();
            if(seeyonPolicy != null) {
            	formApp = seeyonPolicy.getFormApp();
            	form = seeyonPolicy.getForm();
            	operationName = seeyonPolicy.getOperationName();
            }
            int size = people.size();
            for (int i = 0; i < size; i++) {
                Party party = people.get(i);
                String roleName = "roleadmin";
                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.name);
                //多级会签增加,自动增加自己加节点标识
                userNode.setFromType(flowData.getFromType());
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);
                BPMSeeyonPolicy bpmPolicy= null;
                //多级会签，优先考虑人员设置的权限
                if(party.getSeeyonPolicy()==null) {
	                if(flowData.getSeeyonPolicy() != null){
	                	bpmPolicy= createBPMSeeyonPolicy(flowData.getSeeyonPolicy());
	                	userNode.setSeeyonPolicy(bpmPolicy);
	                }
	                else{
	                	bpmPolicy= createBPMSeeyonPolicy(policy);
	                	userNode.setSeeyonPolicy(bpmPolicy);
	                }
                }
                else {
                	bpmPolicy= party.getSeeyonPolicy();
                	userNode.setSeeyonPolicy(bpmPolicy);
                }
                bpmPolicy.setFormApp(formApp);
                bpmPolicy.setForm(form);
                bpmPolicy.setOperationName(operationName);
                if(Strings.isBlank(bpmPolicy.getProcessMode())){
                	bpmPolicy.setProcessMode("all");
                }
                
                if("user".equals(party.getType())){//具体人的话，为单人执行
            		bpmPolicy.setProcessMode("single");
        		}

//              传阅和知会非人节点应该是全体执行
                if(policy != null && ("yuedu".equals(policy.getId())) || "inform".equals(policy.getId())){
            		if(!"user".equals(party.getType())){
            			bpmPolicy.setProcessMode("all");
            		}
            	}

                bpmPolicy.setAdded(true);
                bpmPolicy.setAddedFromId(workitem.getPerformer());
                bpmPolicy.setFR(isFormOperationReadonly? "1" : "");

               	if (firstNode == null) {
                    firstNode = userNode;
                }
                BPMTransition userLink1 = new BPMTransition(previousNode, userNode);
                process.addChild(userNode);
                process.addLink(userLink1);
                previousNode = userNode;
                memAndPolicyName.add(userNode.getName()+"("+bpmPolicy.getName()+")");
            }
            lastNode = previousNode;
            if (downTransitions != null) {
                for (int i = 0; i < downTransitions.size(); i++) {
                    BPMTransition trans = (BPMTransition) downTransitions.get(i);
                    BPMAbstractNode to = trans.getTo();
                    BPMTransition userLink1 = new BPMTransition(lastNode, to);
                    copyCondition(trans,userLink1);
                    process.addLink(userLink1);
                    process.removeLink(trans);
                    currentActivity.removeDownTransition(trans);
                }
            }

            Date now = new Date(System.currentTimeMillis());
            process.setUpdateDate(now);
            String isShowShortName = flowData.getIsShowShortName();
            if("false".equals(isShowShortName)){
            	 if("false".equals(process.getIsShowShortName()))
            			 process.setIsShowShortName(isShowShortName);
            }else{
            	process.setIsShowShortName(isShowShortName);
            }
            /*ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            pdm.saveOrUpdateProcessInReady(process);*/
        } catch (Throwable e) {
        	throw new ColException("加签并发操作异常", e);
        }
        return memAndPolicyName;
    }

    public static void copyCondition(BPMTransition target,BPMTransition to){
    	to.setConditionBase(target.getConditionBase());
    	to.setConditionId(target.getConditionId());
    	to.setConditionTitle(target.getConditionTitle());
    	to.setConditionType(target.getConditionType());
    	to.setFormCondition(target.getFormCondition());
    	to.setIsForce(target.getIsForce());
    }
    //经典的减签动作
    // parentNode-----(parentToNode)--------->nodeToDelete-----(@nextTrans)--------->nodeX
    //  变成 parentNode-------->nodeX
    // 如果parentNode和nodeX分别是分支和汇聚，就是parentNode  nodeX（不加中间的线）
    // 返回值，当删除结点后正常的把前后结点连起来时返回true,否则返回false
    private static boolean deleteClassicNode(BPMAbstractNode nodeToDelete, BPMAbstractNode parentNode, BPMTransition parentToNode, BPMProcess process) {
        List<BPMTransition> downTransitions = nodeToDelete.getDownTransitions();
        boolean needAddLink = true;
        if (downTransitions.size() == 1) {
            BPMTransition nextTrans = downTransitions.get(0);
            BPMAbstractNode nodeX = nextTrans.getTo();
            boolean parentIsSplit = isSplitNode(parentNode);
            boolean nodeXIsJoin = isJoinNode(nodeX);
            needAddLink = !(parentIsSplit && nodeXIsJoin);

            process.removeLink(parentToNode);
            for (int i = 0; i < downTransitions.size(); i++) {
                BPMTransition _nextTrans = downTransitions.get(i);
                if (needAddLink) {
                    BPMTransition newTrans = new BPMTransition(parentNode, _nextTrans.getTo());
                    //拷贝下一个分支条件设置
                    newTrans.setConditionBase(_nextTrans.getConditionBase());
                    newTrans.setConditionId(_nextTrans.getConditionId());
                    newTrans.setConditionTitle(_nextTrans.getConditionTitle());
                    newTrans.setConditionType(_nextTrans.getConditionType());
                    newTrans.setDesc(_nextTrans.getDesc());
                    newTrans.setFormCondition(_nextTrans.getFormCondition());
                    newTrans.setIsForce(_nextTrans.getIsForce());
                    newTrans.setName(_nextTrans.getName());
                    process.addLink(newTrans);
                }
                process.removeLink(_nextTrans);
            }

        }
        process.removeChild(nodeToDelete);
        return needAddLink;
    }


    //取出结点的人员信息，用于减签功能
    private static Party getParty(BPMAbstractNode node) {
        //修改空指针异常 Mazc 08-11-14
        if(node == null){
            return null;
        }
        List<BPMActor> actors = node.getActorList();
        if (actors != null && !actors.isEmpty()) {
            //目前忽略多个actor的情况，只取第一个
            BPMActor actor = actors.get(0);
            BPMParticipant p = actor.getParty();
            String partyId = p.getId();
            String partyType = p.getType().id;
            String[] nodeInfo = actor.getNodeInfo();
            String partyAccountId = p.getAccountId();
            Party party = new Party(partyType, partyId, nodeInfo[0], partyAccountId, nodeInfo[1], null, node.getId());
            //增加节点权限
            if(node.getSeeyonPolicy() != null)
            	party.setSeeyonPolicy(node.getSeeyonPolicy());
            return party;
        }
        else {
            return null;
        }
    }

    private static FlowData getPeople(BPMAbstractNode node) {
        FlowData result = new FlowData();
        List<Party> people = new ArrayList<Party>();
        Party party = getParty(node);
        people.add(party);
        result.setPeople(people);
        return result;
    }

    //减签前的人员列表
    public static FlowData preDeletePeople(Long caseId, Long workitemId, String processId, String userId) throws ColException {
    	BPMProcess process = null;
        try {
        	process = getModifyingProcess(processId, userId);
        	if(process == null)
        		process = getRunningProcessByCaseId(caseId);
        } catch (ColException e) {
        	throw new ColException("获取流程process异常 [caseId = " +caseId+ "]", e);
        }
        FlowData flowData = preDeleteNode(process, workitemId, true, null);
        return flowData;
    }

    public static FlowData deletePeople(Long caseId, Long workitemId, List<Party> selPartyId, String processId, String userId) throws ColException {
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
        	process = getModifyingProcess(processId, userId);
        	if(process == null)
        		process = BPMProcess.fromXML(getRunningProcessByCaseId(caseId).toXML());
        } catch (ColException e) {
        	throw new ColException("获取流程process异常 [caseId = " +caseId+ "]", e);
        }
        FlowData flowData = null;
        try{
        	flowData = preDeleteNode(process, workitemId, false, selPartyId);
        	process.setModifyUser(userId);
        	pe.updateModifyingProcess(process);
        }catch (Exception e) {
        	throw new ColException("减签操作异常 [caseId = " + caseId + "]", e);
        }
        return flowData;
    }

	//减签前的判断
	//返回null表示不允许减签
	//返回的people.size()可直接减签（是不是显示该人名后，经用户确认再减签）
	//返因的people.size>1需要用户选择人后再减签
	//变量意义
    //currentActivity ------(nextTrans)-----------> nextNode -------(next2Trans)-----------> next2Node
    //a(current) --> b -> c
    //*串发
    //a(current) -> b -> c
    //变为 a -> c
    //*并发
    //a -> b(split) -> c -> d(join)
    //              -> e ->
    //变为
    //a -> b -> e -> d
    //*并发 a -> b(split) -> c -> d(join) -> f
    //变为
    // a -> f

    private static FlowData preDeleteNode(BPMProcess process, Long workitemId, boolean pre, List<Party> selParties) throws ColException {
    	// TODO 分离预减签和减签 预减签只需要取得所有的直接后续节点即可，可调用findDirectHumenChildrenCascade
        try {
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
            BPMActivity a = process.getActivityById(workitem.getActivityId());
            List<BPMTransition> links_a_down = a.getDownTransitions();

            //比如"知会"的情况
            if (links_a_down.size() < 1) {
                return null;
            }

            BPMTransition link_a_b = links_a_down.get(0);
            BPMAbstractNode b = link_a_b.getTo();


            boolean isNextHumenNode = (b instanceof BPMHumenActivity) || (b instanceof BPMTimeActivity);
            boolean isNextEnd = (b instanceof BPMEnd);
            boolean isNextSplit = isSplitNode(b);
            boolean isNextJoin = isJoinNode(b);

            //如果下个是结束结点，不允许减签
            if (isNextEnd) {
                return null;
            }

            //如果下一结点是汇聚结点，判断汇聚结点的下一结点是否为人工结点，如果是，就可以进行减签
            if (isNextJoin) {
                List<Party> people = preDeleteNodeOnJoin(b,process,selParties, pre);
                if(people != null)
                {
                	FlowData result = new FlowData();
                	result.setPeople(people);
                	return result;
                }
            }

            //如果下一结点是人工结点，则可直接减签
            if (isNextHumenNode) {
                if (pre) {
                    FlowData result = getPeople(b);
                    return result;
                } else {
                	inParty(b, selParties);
                    deleteClassicNode(b, a, link_a_b, process);
                }
            }

            //如果下一结点是分支结点，遍历所有直接分支，给出人员列表，供选择
            if (isNextSplit) {

                List<Party> people = preDeleteOnSplit(a, b, process, selParties, pre);
                if(people != null)
                {
                	FlowData result = new FlowData();
                	result.setPeople(people);
                	return result;
                }

            }

            //其它情况
            return null;
        } catch (Exception ex) {
        	throw new ColException("预减签操作异常", ex);
        }
    }


	private static List<Party> preDeleteOnSplit(BPMAbstractNode a, BPMAbstractNode b, BPMProcess process, List<Party> selParties,  boolean pre) {
		if (pre) {
			List<BPMTransition> links_b_c = b.getDownTransitions();
			List<Party> people = new ArrayList<Party>();
			for (int i = 0; i < links_b_c.size(); i++) {
				BPMTransition link_b_c = links_b_c.get(i);
				BPMAbstractNode c = link_b_c.getTo();
				//下一节点是分支节点
	            boolean isNextSplit = isSplitNode(c);
	            if(isNextSplit)
	            {
	            	// 递归
	            	List<Party> ps = preDeleteOnSplit(b,c,process,selParties,pre);
	            	if(ps!=null)
	            	{
	            		people.addAll(ps);
	            	}
	            }
	            else
	            {
				    Party party = getParty(c);
				    if(party!= null && !"undefined".equals(party.getId())){
				    	people.add(party);
				    }
	            }
			}
			if(people.size() == 0){
				return null;
			}else{
				return people;
			}
		}
		// 减签
		BPMAndRouter split = (BPMAndRouter) b;

		//找到join结点
		BPMAndRouter join = findJoinOfSplit(process, split);

		List<BPMTransition> links_b_c = b.getDownTransitions();
		if(links_b_c==null) return null;
	    int linksCount = links_b_c.size();
	    int deleteNodeCount = 0;

	    for (int i = linksCount - 1; i >= 0; i--) {
	    	if(links_b_c.size()==0) break;
	    	BPMTransition link_b_c = links_b_c.get(i);
			BPMAbstractNode c = link_b_c.getTo();
			//下一节点是分支节点
            boolean isNextSplit = isSplitNode(c);
            if(isNextSplit)
            {
            	// 递归
            	preDeleteOnSplit(b,c,process,selParties,pre);
            }

	        Party party = getParty(c);
	        if (party != null) {
	            boolean found = inParty(party, selParties);
	            if (found) {
	            	inParty(c, selParties);
	                deleteClassicNode(c, b, link_b_c, process);
	                deleteNodeCount++;
	            }
	        }

		    //如果分支、汇聚中的结点全部删除或之间只有剩下一个人工节点，那么，分支和汇聚结点也要删除
		    if (deleteNodeCount == linksCount || (linksCount-deleteNodeCount) == 1) {
		        // List<BPMTransition> links_a_down = a.getDownTransitions();
		        BPMTransition link_a_b =  (BPMTransition)b.getUpTransitions().get(0); //links_a_down.get(0);
				BPMTransition link_join_f = (BPMTransition) join.getDownTransitions().get(0);
				BPMAbstractNode f = link_join_f.getTo();
		    	if(split.getDownTransitions() == null){
		            process.removeLink(link_a_b);
		            process.removeLink(link_join_f);
		            process.removeChild(split);
		            process.removeChild(join);
		            BPMTransition link_a_f = new BPMTransition(a, f);
		            process.addLink(link_a_f);
		    	}else if(split.getDownTransitions().size() == 1){
		    		// split只剩下一个后续节点 清理多余节点和链接 删除split，删除join，重建链表
		    		BPMTransition split_down = (BPMTransition)split.getDownTransitions().get(0);
		        	BPMAbstractNode split_to = split_down.getTo();
		        	BPMTransition join_up = (BPMTransition)join.getUpTransitions().get(0);
		            BPMAbstractNode join_from = join_up.getFrom();

		        	// 剩下的最后一个兄弟节点也在selParties中
		        	if(inParty(split_to,selParties))
		        	{
		        		process.removeChild(split_to);
			            process.removeLink(link_a_b);
			            process.removeLink(link_join_f);
			            process.removeChild(split);
			            process.removeChild(join);

			            process.removeLink(split_down);
			            process.removeLink(join_up);
			            // 如果删除后Split直接连Join就不建立链接
			            if (isSplitNode(a) && isJoinNode(f))
			            {
			            	//removeRedundancySplit(process,(BPMAndRouter)a);
			            }
			            else
			            {
				            BPMTransition link_a_f = new BPMTransition(a, f);
				            process.addLink(link_a_f);
			            }

			            deleteNodeCount++;

		        	}
		        	else
		        	{
			        	// a - link_a_b -split - split_down - split_to - join_from - join - link_join_f - f
			        	// a - split_to - join_from - f

			            process.removeLink(split_down);
			            process.removeLink(join_up);
			            process.removeLink(link_a_b);
			            process.removeLink(link_join_f);
			            process.removeChild(split);
			            process.removeChild(join);

			            BPMTransition newLink1 = new BPMTransition(a, split_to);
			            BPMTransition newLink2 = new BPMTransition(join_from, f);
			            process.addLink(newLink1);
			            process.addLink(newLink2);
		        		//removeRedundancySplit(process,split,join);
		        	}

		    	}
		    }
		}


		return null;
	}
	/**
	 * 删除多余的Split和Join。
	 * @param process
	 * @param split
	 */
	private static void removeRedundancySplit(BPMProcess process,BPMAndRouter split,BPMAndRouter join)
	{
		if(split.getDownTransitions().size() > 1) return;
		BPMTransition split_up =  (BPMTransition)split.getUpTransitions().get(0);
		BPMAbstractNode split_from = split_up.getFrom();
		BPMTransition split_down = (BPMTransition)split.getDownTransitions().get(0);
    	BPMAbstractNode split_to = split_down.getTo();
    	BPMTransition join_up = (BPMTransition)join.getUpTransitions().get(0);
        BPMAbstractNode join_from = join_up.getFrom();
		BPMTransition join_down = (BPMTransition) join.getDownTransitions().get(0);
		BPMAbstractNode join_to = join_down.getTo();

        process.removeLink(split_down);
        process.removeLink(join_up);
        process.removeLink(split_up);
        process.removeLink(join_down);
        process.removeChild(split);
        process.removeChild(join);

        BPMTransition newLink1 = new BPMTransition(split_from, split_to);
        BPMTransition newLink2 = new BPMTransition(join_from, join_to);
        process.addLink(newLink1);
        process.addLink(newLink2);

	}
	/**
	 * 从指定的Split节点出发，回溯移除多余的空Split-Join节点。
	 * @param process
	 * @param split
	 */
	private static void removeRedundancySplit(BPMProcess process,BPMAndRouter split)
	{
		//if(split.getDownTransitions().size() > 1) return;
		if(!isSplitNode(split)){return;}

		BPMTransition link_to_split =  (BPMTransition)split.getUpTransitions().get(0);
		BPMAbstractNode split_from = link_to_split.getFrom();

		BPMAndRouter join = findJoinOfSplit(process, split);
		BPMTransition split_down = (BPMTransition)split.getDownTransitions().get(0);
    	BPMAbstractNode split_to = split_down.getTo();
    	BPMTransition join_up = (BPMTransition)join.getUpTransitions().get(0);
        BPMAbstractNode join_from = join_up.getFrom();
		BPMTransition link_join_f = (BPMTransition) join.getDownTransitions().get(0);
		BPMAbstractNode join_to = link_join_f.getTo();

        process.removeLink(split_down);
        process.removeLink(join_up);
        process.removeLink(link_to_split);
        process.removeLink(link_join_f);
        process.removeChild(split);
        process.removeChild(join);

        BPMTransition newLink1 = new BPMTransition(split_from, split_to);
        BPMTransition newLink2 = new BPMTransition(join_from, join_to);
        process.addLink(newLink1);
        process.addLink(newLink2);

        if(isSplitNode(split_from))
        {
        	//removeRedundancySplit(process,(BPMAndRouter)split_from);
        }

	}



	private static boolean inParty(BPMAbstractNode node, List<Party> selParties) {
		return inParty(getParty(node), selParties);
	}
	private static boolean inParty(Party party, List<Party> selParties) {
		if(party==null) return false;
		boolean found = false;
		for (int n = 0; n < selParties.size(); n++) {
		    Party _party = selParties.get(n);
		    if (party.equals(_party)) {
		    	if(party.getSeeyonPolicy() != null){
		    		_party.setSeeyonPolicy(party.getSeeyonPolicy());
		    	}
		        found = true;
		        break;
		    }
		}
		return found;
	}
	/**
	 * 判断节点1后续节点是否包含节点2。
	 * @param split
	 * @param join
	 * @return
	 */
	public static boolean passThrough(BPMAbstractNode split,BPMAbstractNode join)
	{
		List<BPMTransition> links = split.getDownTransitions();
		if(links==null) return false;
		for (BPMTransition link : links) {
			BPMAbstractNode to = link.getTo();
			if(to instanceof BPMEnd) break;
			if(to.equals(join)) return true;
			if(passThrough(to,join)) return true;
		}
		return false;
	}
	/**
	 * 取得节点的所有后续节点。
	 * @param node
	 * @return
	 */
	private static Set<BPMAbstractNode> getAllNextNodes(BPMAbstractNode node)
	{
		Set<BPMAbstractNode> result = new HashSet<BPMAbstractNode>();
		List<BPMTransition> links = node.getDownTransitions();
		for (BPMTransition link : links) {

			BPMAbstractNode to = link.getTo();
			result.add(to);
			if(!(to instanceof BPMEnd))
			result.addAll(getAllNextNodes(to));
		}
		return result;
	}
	/**
	 * 找到Split节点对应的join节点。
	 * @param process
	 * @param split
	 * @return
	 */
	private static BPMAndRouter findJoinOfSplit(BPMProcess process, BPMAndRouter split) {

		BPMAndRouter join = null;
        BPMAbstractNode node = split;
        // 算法查找
        // 查找所有直接后续节点都通过的Join节点。
		List<BPMTransition> links = node.getDownTransitions();
		if(links==null) return null;

		Set<BPMAbstractNode> set = new HashSet<BPMAbstractNode>();
		for (BPMTransition link : links) {
			BPMAbstractNode to = link.getTo();
			 Set<BPMAbstractNode>  allNext =getAllNextNodes(to);
			if(set.size()==0)
			{
				set.addAll(allNext);
			}
			else
			{
				set.retainAll(allNext);
			}
		}
        if(set.size()>0)
        {
        	// 找出路径中第一个Join节点
        	BPMAndRouter firstJoin =null;
        	for (BPMAbstractNode n : set) {
				if(isJoinNode(n))
				{
					BPMAndRouter join2 = (BPMAndRouter)n;
					if(firstJoin==null)
					{
						firstJoin = join2;
					}
					firstJoin = (passThrough(firstJoin, join2))?firstJoin:join2;
				}
			}
        	return firstJoin;


        }
        // return (BPMAndRouter)n
		// ColHelper中出现了多次查找，使用的方式也不一致，下面是原来的两种判断方式，依据ParallelismNodeId查找，算法查找有效后可删除；如果ParallelismNodeId有效，优先使用按节点下溯查找
        // 按节点下溯查找
        boolean foundJoin = false;
        while (!foundJoin) {
            BPMTransition trans = (BPMTransition) node.getDownTransitions().get(0);
            node = trans.getTo();
            if (node instanceof BPMAndRouter) {
                BPMAndRouter andNode = (BPMAndRouter) node;
                if (isJoinNode(node)) {
                	if(split.getParallelismNodeId().equals(andNode.getParallelismNodeId())){
                		foundJoin = true;
                		join = andNode;
                		return join;
                	}
                }
            }
        }

		// 遍历所有activity
		List<BPMAbstractNode> activityList = process.getActivitiesList();
		for(int i=0; i<activityList.size(); i++){
			node = activityList.get(i);
			if(node instanceof BPMAndRouter){
				BPMAndRouter andRouter = (BPMAndRouter)node;
				String parallelismSplitId = split.getParallelismNodeId();
				String parallelismJoinId = andRouter.getParallelismNodeId();
				if(parallelismSplitId.equalsIgnoreCase(parallelismJoinId)
						&& !split.getId().equals(andRouter.getId())){
					join = andRouter;
					break;
				}
			}
		}
		return join;
	}

	private static boolean isSplitNode(BPMAbstractNode node) {
		return (node instanceof BPMAndRouter) && ((BPMAndRouter) node).isStartAnd();
	}
	private static boolean isJoinNode(BPMAbstractNode node) {
		return (node instanceof BPMAndRouter) && !((BPMAndRouter) node).isStartAnd();
	}


	private static List<Party> preDeleteNodeOnJoin(BPMAbstractNode b, BPMProcess process, List<Party> selParties,  boolean pre) {
		List<BPMTransition> links_b_c = b.getDownTransitions();
		if (links_b_c.size() == 1) {
		    BPMTransition link_b_c = links_b_c.get(0);
		    BPMAbstractNode c = link_b_c.getTo();

		    //无下一结点，或不是人工结点，不允许减签
		    if (c == null  ) {
		        return null;
		    }

		    if(!((c instanceof BPMHumenActivity) || (c instanceof BPMTimeActivity)))
		    {
		    	// 下一节点是分支节点
		    	boolean isNextSplit = isSplitNode(c);
			    if(isNextSplit)
			    {
			    	return preDeleteOnSplit( b, c, process, selParties, pre);
			    }
			    // Join后面還是Join
			    boolean isNextJoin = isJoinNode(c);
			    if(isNextJoin)
			    {
			    	return preDeleteNodeOnJoin( c, process, selParties, pre);
			    }
		    	return null;
		    }

		    if (pre) {
		        List<Party> people = new ArrayList<Party>();
		        Party party = getParty(c);
		        people.add(party);
		        return people;
		    } else {
		    	inParty(c, selParties);
		        deleteClassicNode(c, b, link_b_c, process);
		        return null;
		    }
		}
		return null;
	}


    public static FlowData getRunningProcessPeople(String processId) throws ColException {
    	List<Party> people = new ArrayList<Party>();
    	FlowData flowData = new FlowData();
    	BPMProcess process = null;
    	try{
    		process = getRunningProcessByProcessId(processId);
    	}catch(ColException e){
			throw new ColException("获取runningProcess异常[processId ="+ processId+ "]", e);
    	}
    	flowData.setType(FlowData.FLOWTYPE_SERIAL);
    	List<BPMAbstractNode> nodes = process.getActivitiesList();
    	for (BPMAbstractNode node : nodes) {
    		if (node instanceof BPMHumenActivity || node instanceof BPMTimeActivity) {
    			Party party = getParty(node);
    			people.add(party);
    		}

    		if (node instanceof BPMAndRouter) {
    			flowData.setType(FlowData.FLOWTYPE_PARALLEL);
    		}
    	}
    	if(people == null || people.isEmpty()){
    		return null;
    	}
    	flowData.setPeople(people);
    	flowData.setDesc_by(FlowData.DESC_BY_XML);
    	flowData.setXml(process.toXML());
    	return flowData;
    }
    
    public static FlowData getProcessPeople(String processId) throws ColException {
        BPMProcess process = null;
        try {
            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            process = (BPMProcess) pdm.getProcessInReady("admin", processId);
            //todo 这块需要改
            if (process == null)
                return null;
        } catch (BPMException e) {
            throw new ColException(e);
        }
        
    	return getProcessPeople(process);
    }
    
    private static FlowData getProcessPeople(BPMProcess process) throws ColException {
        List<Party> people = new ArrayList<Party>();
        FlowData flowData = new FlowData();
        flowData.setType(FlowData.FLOWTYPE_SERIAL);
        List<BPMAbstractNode> nodes = process.getActivitiesList();
        for (BPMAbstractNode node : nodes) {
            if (node instanceof BPMHumenActivity || node instanceof BPMTimeActivity) {
                Party party = getParty(node);
                people.add(party);
            }

            if (node instanceof BPMAndRouter) {
                flowData.setType(FlowData.FLOWTYPE_PARALLEL);
            }
        }
        flowData.setPeople(people);
        flowData.setDesc_by(FlowData.DESC_BY_XML);
        flowData.setXml(process.toXML());
        return flowData;
    }

    //会签
    public static List<String> colAssign(Long caseId, Long workitemId, FlowData flowData, String processId, String userId) throws ColException {
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        List<String> assignMember = null;
        try {
        	process = getModifyingProcess(processId, userId);
        	if(process == null)
        		process = BPMProcess.fromXML(getRunningProcessByCaseId(caseId).toXML());
        } catch (ColException e) {
        	throw new ColException("获取流程process异常 [caseId = " +caseId+ "]", e);
        }

        try{
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
        	assignMember = colAssign(process, workitem.getActivityId(), flowData, workitem, workitem.getCaseId(), true);
        	process.setModifyUser(userId);
        	pe.updateModifyingProcess(process);
        	//pe.replaceProcessByProcessId(null, process);
        }catch (Exception e) {
        	throw new ColException("会签操作异常[caseId = " + caseId + "]", e);
        }
        return assignMember;
    }

    //知会
    public static List<String> addInform(Long caseId, Long workitemId, FlowData flowData, String processId, String userId) throws ColException {
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        List<String> informMem = null;
        try {
        	process = getModifyingProcess(processId, userId);
        	if(process == null)
        		process = BPMProcess.fromXML(getRunningProcessByCaseId(caseId).toXML());
        } catch (ColException e) {
        	throw new ColException("获取流程process异常 [caseId = " +caseId+ "]", e);
        }
        try{
        	//知会只存在并发
        	if(flowData.getPeople().size() <= 1){
        		informMem = insertSerial(process, workitemId, flowData, BPMSeeyonPolicy.SEEYON_POLICY_INFORM, false);
        	}
        	else{
        		informMem = insertParellel(process, workitemId, flowData, BPMSeeyonPolicy.SEEYON_POLICY_INFORM, false);
        	}

	        process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
        }catch (Exception e) {
        	throw new ColException("知会操作异常[caseId = " + caseId + "]", e);
        }
        return informMem;
    }

    /**
     * 会签
     *
     * @param process
     * @param workItem 如果来自督办，就是<code>null</code>
     * @param people
     * @throws ColException 如果 b->a(当前结点)->c
     * 当b/c分别为split/join，则会签结果b-> a -> c
     * -> d ->
     * 当非上述情况，则会签结果b -> split -> a -> join -> c
     * -> d ->
     */
    private static List<String> colAssign(BPMProcess process, String activityId, FlowData flowData, WorkItem workItem, Long caseId, boolean isPending) throws ColException {
    	List<Party> people =  flowData.getPeople();
        User user = CurrentUser.get();
        ProcessEngine pe = null;
        List<String> assignMembers = new ArrayList<String>();
        try {
            pe = getProcessEngine();
            BPMActivity a = process.getActivityById(activityId);
            String formApp = "";
            String form = "";
            String operationName = "";
            BPMSeeyonPolicy seeyonPolicy = a.getSeeyonPolicy();
            if(seeyonPolicy != null) {
            	formApp = seeyonPolicy.getFormApp();
            	form = seeyonPolicy.getForm();
            	operationName = seeyonPolicy.getOperationName();
            }

            List links_ba = a.getUpTransitions();
            List links_ac = a.getDownTransitions();
            BPMTransition link_ba = (BPMTransition) links_ba.get(0);
            BPMTransition link_ac = (BPMTransition) links_ac.get(0);
            BPMAbstractNode b = link_ba.getFrom();
            BPMAbstractNode c = link_ac.getTo();

            boolean bIsSplit = (b instanceof BPMAndRouter) && ((BPMAndRouter) b).isStartAnd();
            boolean cIsJoin = (c instanceof BPMAndRouter) && !((BPMAndRouter) c).isStartAnd();

            BPMAndRouter split = null;
            BPMAndRouter join = null;

            if (bIsSplit && cIsJoin) {
                split = (BPMAndRouter) b;
                join = (BPMAndRouter) c;
                BPMSeeyonPolicy virtualNodePolicy = new BPMSeeyonPolicy(seeyonPolicy);
                virtualNodePolicy.setFormApp(formApp);
                virtualNodePolicy.setForm(form);
                virtualNodePolicy.setOperationName(operationName);
                virtualNodePolicy.setAdded(false);
                join.setSeeyonPolicy(virtualNodePolicy);
                split.setSeeyonPolicy(virtualNodePolicy);
            } else {
                String splitId = UUIDLong.longUUID()+"";
                String joinId = UUIDLong.longUUID()+"";
                split = new BPMAndRouter(splitId, "split");
                join = new BPMAndRouter(joinId, "join");
                BPMSeeyonPolicy virtualNodePolicy = new BPMSeeyonPolicy(seeyonPolicy);
                virtualNodePolicy.setFormApp(formApp);
                virtualNodePolicy.setForm(form);
                virtualNodePolicy.setOperationName(operationName);
                virtualNodePolicy.setAdded(true);
                join.setSeeyonPolicy(virtualNodePolicy);
                split.setSeeyonPolicy(virtualNodePolicy);
                String relevancyId = UUIDLong.longUUID() + "";
                split.setParallelismNodeId(relevancyId);
                join.setParallelismNodeId(relevancyId);
                split.setStartAnd(true);
                join.setStartAnd(false);
                process.addChild(split);
                process.addChild(join);


                for (int i = a.getUpTransitions().size() - 1; i >= 0; i--) {
                    BPMTransition b_a = (BPMTransition) a.getUpTransitions().get(i);
	                process.removeLink(b_a);
	                BPMTransition link_split_a = new BPMTransition(split, b_a.getTo());
	                //复制分支条件
	                copyCondition(link_ba,link_split_a);
	                process.addLink(link_split_a);
                }

                BPMTransition link_b_split = new BPMTransition(b, split);


                process.addLink(link_b_split);

                for (int i = a.getDownTransitions().size() - 1; i >= 0; i--) {
                    BPMTransition a_c = (BPMTransition) a.getDownTransitions().get(i);
                    process.removeLink(a_c);
                    BPMTransition link_a_join = new BPMTransition(a, join);
                    process.addLink(link_a_join);
                }
                BPMTransition link_join_c = new BPMTransition(join, c);
                copyCondition(link_ac, link_join_c);
                process.addLink(link_join_c);
            }

            BPMCase theCase = pe.getCase(caseId);
            //应用类型
            String appName = theCase.getData(ActionRunner.SYSDATA_APPNAME).toString();
            List<BPMActivity> added = new ArrayList<BPMActivity>();
            //split -> d -> join
            //向split、join之间添加新结节点
            for (int i=(people.size()-1); i>=0; i--) {
                Party party = people.get(i);
                BPMAbstractNode d = new BPMHumenActivity(UUIDLong.longUUID() + "", party.name);
                BPMActor userActor = createActor(party);
                d.addActor(userActor);
                party.setActivityId(d.getId());
                BPMTransition link_split_d = new BPMTransition(split, d);
                BPMTransition link_d_join = new BPMTransition(d, join);

                //begin old code yangzd
                /*BPMSeeyonPolicy policy = null;
                if(flowData.getSeeyonPolicy() != null){
                	policy = flowData.getSeeyonPolicy();
                	policy = new BPMSeeyonPolicy(policy);
                }else{
                	policy = new BPMSeeyonPolicy("collaboration","协同");
                }*/
                //end old code
                //change  begin yangzd  --------->会签的与当前节点的权限相同
                BPMSeeyonPolicy policy = null;
                //change end
                //如果是公文，当前会签后的节点权限是‘会签'
                if("edoc".equals(appName)){
                	if(flowData.getSeeyonPolicy() != null)
                		policy = new BPMSeeyonPolicy(flowData.getSeeyonPolicy());
                	else
                		policy = new BPMSeeyonPolicy("huiqian","会签");
                }else{
                	//如果是协同，当前会签后的节点权限同当前节点
                	policy = new BPMSeeyonPolicy(a.getSeeyonPolicy());
                }
                if("user".equals(party.getType())){
                	policy.setProcessMode("single");
                }else{
                	if( flowData.getSeeyonPolicy() != null &&
                			Strings.isNotBlank(flowData.getSeeyonPolicy().getProcessMode())){
                		policy.setProcessMode(flowData.getSeeyonPolicy().getProcessMode()) ;
                	}else{
                		policy.setProcessMode("all");
                	}
                }

                policy.setFormApp(formApp);
                policy.setForm(form);
                policy.setOperationName(operationName);
                policy.setAdded(true);
                if(workItem != null){
                	policy.setAddedFromId(workItem.getPerformer());
                }

                d.setSeeyonPolicy(policy);

                if(!"user".equals(party.getType())){
                	BPMSeeyonPolicy _policy = d.getSeeyonPolicy();
                	if(!"competition".equals(_policy.getProcessMode())){
                		_policy.setProcessMode("all");
                	}
                }

                added.add((BPMActivity) d);
                process.addChild(d);
                //复制分支条件
                copyCondition(link_ba,link_split_d);
                process.addLink(link_split_d);
                process.addLink(link_d_join);
                assignMembers.add(d.getName()+"("+d.getSeeyonPolicy().getName()+")");
            }

            Date now = new Date(System.currentTimeMillis());
            process.setUpdateDate(now);
            String isShowShortName = flowData.getIsShowShortName();
            if("false".equals(isShowShortName)){
            	if("false".equals(process.getIsShowShortName()))
            		process.setIsShowShortName(isShowShortName);
            }else{
            	process.setIsShowShortName(isShowShortName);
            }
            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            pdm.saveOrUpdateProcessInReady(process);

            //将加入的人设为ready状态
            if(isPending){
	            //pe.addReadyActivity(user.getId() + "", process, theCase, added);
	            ReadyObject readyObject = new ReadyObject();
	            readyObject.setActivityList(added);
	            readyObject.setCaseId(caseId+"");
	            readyObject.setProcessId(process.getId());
	            readyObject.setUserId(user.getId()+"");
	            //修改join的num
	            boolean saveTheCaseFlag = false;
	            if(bIsSplit && cIsJoin && theCase != null) {
	            	ReadyNode node = theCase.getReadyActivityById(join.getId());
	            	if(node != null) {
	            		node.setNum(node.getNum()+people.size());
	            		saveTheCaseFlag = true;
	            		// 强制更新num，否则处理时node.getNum()仍未改变
	            		pe.getCaseManager().save(theCase);
	            	}
	            }
	            readyObject.setSaveTheCaseFlag(saveTheCaseFlag);
	            pe.addReadyActivityMap(process.getId(), readyObject);
	            deleteReadyProcess(process.getId());
            }
        } catch (BPMException e) {
        	throw new ColException("会签操作异常", e);
        }
        return assignMembers;
    }

    /**
     * 传阅
     *
     * @param process
     * @param workitemId
     * @param people
     * @throws ColException 加签c,d
     *                      a(当前结点) -> b变为
     *                      a -> b
     *                      -> c
     *                      -> d
     */
    public static List<String> addPassRead(Long caseId, Long workitemId, FlowData flowData, String processId, String userId) throws ColException {
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
        	process = getModifyingProcess(processId, userId);
        	if(process == null)
        		process = BPMProcess.fromXML(getRunningProcessByCaseId(caseId).toXML());
        } catch (ColException e) {
        	throw new ColException("获取流程process异常 [caseId = " +caseId+ "]", e);
        }
        try{
        	List<String> addPassRead = null;
        	//传阅只存在并发
        	if(flowData.getPeople().size() <= 1){
        		addPassRead = insertSerial(process, workitemId, flowData, BPMSeeyonPolicy.EDOC_POLICY_YUEDU, false);
        	}
        	else{
        		addPassRead = insertParellel(process, workitemId, flowData, BPMSeeyonPolicy.EDOC_POLICY_YUEDU, false);
        	}
	        process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
	        return addPassRead ;
        }catch (Exception e) {
        	throw new ColException("知会操作异常[caseId = " + caseId + "]", e);
        }
    }


    /**
     * 得到指定case的正在处理者（待办者）列表�?
     *
     * @param workitemId
     * @return
     * @throws ColException
     */
    public static List<String> getPendingPeople(String caseId, Long workitemId) throws ColException {
        ProcessEngine pe = null;
        User user = CurrentUser.get();
        try {
            pe = getProcessEngine();
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
            BPMCase theCase = pe.getCase(workitem.getCaseId());
            List<BPMParticipant> parties = pe.getPendingPeople(user.getId() + "", theCase);
            List<String> result = new ArrayList<String>();
            for (BPMParticipant part : parties) {
                result.add(part.getId());
            }

            return result;
        } catch (BPMException e) {
        	throw new ColException("获取流程待办处理者异常[caseId = " + caseId + "]", e);
        }
    }

    public static List<Long> getChildNodes(Long caseId, Long nodeId) {
        return null;
    }

    public static boolean hasWorkitemDone(Long caseId, Long nodeId) {
        return false;
    }

    //减签 -> 指定节点的前一节点是否为开始节�?
    public static boolean previousIsStartNode(Long caseId, Long nodeId) {
        return false;
    }

    //回退
    public static int stepBack(Long caseId, Long workitemId, BPMProcess process, WorkItem workitem, BPMActivity activity) throws ColException {
        User user = CurrentUser.get();
        String userId = user.getId() + "";
        ProcessEngine pe = getProcessEngine();
        try {
            if(process == null){
                process = getRunningProcessByCaseId(caseId);
            }
            if(workitem == null){
                workitem = getWorkItemById(workitemId);
            }
            BPMCase theCase = pe.getCase(caseId);
            String activityId = workitem.getActivityId();
            if(activity == null){
                activity = process.getActivityById(activityId);
            }
            if (theCase instanceof HistoryCaseRunDAO) {
                return -1;
            }

            List<BPMTransition> trans = activity.getUpTransitions();
            BPMTransition tran = trans.get(0);
            BPMAbstractNode parentNode = (BPMAbstractNode)tran.getFrom();
            if(parentNode.getNodeType().equals(BPMAbstractNode.NodeType.split)){
            	List<BPMTransition> _trans = parentNode.getDownTransitions();

            	for(int i=0; i<_trans.size(); i++){
            		BPMTransition _tran = _trans.get(i);
            		BPMAbstractNode parallelismNode = _tran.getTo();
            		BPMSeeyonPolicy policy = parallelismNode.getSeeyonPolicy();
            		String isPass = policy.getIsPass();
            		if("failure".equalsIgnoreCase(isPass)){
            			return -2;
            		}
            	}
            }
            //0: 正常回退 1：需要撤消整个流程 -1:不允许回退 -2:并行节点已有节点终止不允许回退
            int result = pe.withdrawActivity(userId, process, theCase, activity);
            return result;
        } catch (BPMException e) {
        	throw new ColException("回退操作异常[caseId = " + caseId + "]", e);
        }
    }

    //取回
    public static int takeBack(Long caseId, Long workitemId, BPMProcess process, WorkItem workitem, BPMActivity activity) throws ColException {
    	int result = 0;
        User user = CurrentUser.get();
        String userId = user.getId() + "";
        ProcessEngine pe = getProcessEngine();
        WorkItemManager wim = getWorkitemManager();
        try {
            if(process == null){
                process = getRunningProcessByCaseId(caseId);
            }
            if(workitem == null){
                workitem = getWorkItemById(workitemId);
            }
            BPMCase theCase = pe.getCase(caseId);
            String activityId = workitem.getActivityId();
        	if(activity == null){
                activity = process.getActivityById(activityId);
            }
        	BPMSeeyonPolicy policy = activity.getSeeyonPolicy();
            String policyId = policy.getId();
	       	 if("inform".equals(policyId) || "zhihui".equals(policyId)){
	       		 return -2;
	       	 }
            if (theCase instanceof HistoryCaseRunDAO) {
                return -1;
            }

            List<BPMHumenActivity> childs = getChildHumens(activity);
            /*if(childs.size() == 0){
            	return -1;
            }*/
            if(childs.size() != 0){
	            List<WorkItem> childItems = wim.getHistoryWorkItemList(userId, caseId, childs);
	            if (childItems.size() > 0) {
	                return -1;
	            }
            }
            //0: 正常取回 -1:不允许取回
            result = wim.takeBackHistoryWorkitem(userId, process, theCase, workitem);
	        return result;
        } catch (BPMException e1) {
        	throw new ColException("取回操作异常1[caseId = " + caseId + "]", e1);
        }
        catch(Exception e2){
            throw new ColException("取回操作异常2[caseId = " + caseId + "]", e2);
        }
    }

    /**
     * 得到activiey的父节点,如果不是人工节点递归读取;
     *
     * @param activity
     * @return
     */
    public static List<BPMHumenActivity> getParentHumens(BPMActivity activity) {
        List<BPMHumenActivity> humenList = new ArrayList();
        List<BPMTransition> transitions = activity.getUpTransitions();
        for (BPMTransition tran : transitions) {
            BPMAbstractNode parent = tran.getFrom();
            if (parent.getNodeType() == BPMAbstractNode.NodeType.humen) {
                humenList.add((BPMHumenActivity)parent);
            }
            else if (parent.getNodeType() == BPMAbstractNode.NodeType.join || parent.getNodeType() == BPMAbstractNode.NodeType.split) {
                humenList.addAll(getParentHumens((BPMActivity)parent));
            }
        }
        return humenList;
    }

    /**
     * 得到activiey的父节点,如果不是人工节点递归读取;
     *
     * @param activity
     * @return
     */
    public static List<BPMHumenActivity> getParentHumensForVouch(BPMActivity activity) {
        List<BPMHumenActivity> humenList = new ArrayList();
        List<BPMTransition> transitions = activity.getUpTransitions();
        for (BPMTransition tran : transitions) {
            BPMAbstractNode parent = tran.getFrom();
            if (parent.getNodeType() == BPMAbstractNode.NodeType.humen) {
                humenList.add((BPMHumenActivity)parent);
            }
            else if (parent.getNodeType() == BPMAbstractNode.NodeType.join || parent.getNodeType() == BPMAbstractNode.NodeType.split) {
                humenList.addAll(getParentHumens((BPMActivity)parent));
            }
        }
        return humenList;
    }
    /**
     * 得到activiey的子节点,如果不是人工节点递归读取;
     *
     * @param activity
     * @return
     */
    public static List<BPMHumenActivity> getChildHumens(BPMActivity activity) {
        List<BPMHumenActivity> humenList = new UniqueList<BPMHumenActivity>();
        List<BPMTransition> transitions = activity.getDownTransitions();
        for (BPMTransition trans : transitions) {
            BPMAbstractNode child = trans.getTo();
            String policy = child.getSeeyonPolicy().getId();

            if(policy.equals("inform") || policy.equals("zhihui")){
            	humenList.addAll(getChildHumens((BPMActivity)child));
            }
            else if (child.getNodeType() == BPMAbstractNode.NodeType.humen) {
                humenList.add((BPMHumenActivity) child);
            }
            else if (child.getNodeType() == BPMAbstractNode.NodeType.join || child.getNodeType() == BPMAbstractNode.NodeType.split) {
                humenList.addAll(getChildHumens((BPMActivity) child));
            }
        }
        return humenList;
    }

//通用 -> 找人；流程中已处理和已接收的�?

//impl: 所有已完成和已分配的workitem

    public static List<Long> allAffectedPeople(Long caseId) {
        return null;
    }

    public static String getProcessXML(String processId) throws ColException {
    	BPMProcess process = null;
    	try{
    		process = getRunningProcessByProcessId(processId);
    	}catch(ColException e){
			throw new ColException("获取runningProcess异常[processId ="+ processId+ "]", e);
    	}
        if (process == null)
            return null;
        String xml = process.toXML();
        return xml;
    }

    /**
     * 取实例化(运行中)的流程
     *
     * @param processId
     * @return
     * @throws ColException
     */
    public static BPMProcess getCaseProcess(String processId) throws ColException {
    	if(processId == null){
    		return null;
    	}

        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
            process = (BPMProcess) pe.getProcessRunningById(processId);
        } catch (Exception e) {
        	throw new ColException(e);
        }

        return process;
    }
    
    public static BPMProcess getHisCaseProcess(String processId) throws ColException {
    	if(processId == null){
    		return null;
    	}

        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
            process = (BPMProcess) pe.getHisProcessRunningById(processId);
        } catch (Exception e) {
        	throw new ColException(e);
        }

        return process;
    }

    /**
     * 取未实例化的流程，常用于发起ProcessInReady
     *
     * @param processId
     * @return
     * @throws ColException
     */
    public static BPMProcess getProcess(String processId) throws ColException {
    	if(processId == null){
    		return null;
    	}

    	try {
            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            BPMProcess process = (BPMProcess) pdm.getProcessInReady("admin", processId);

            if (process == null)
                return null;

            return process;
        } catch (BPMException e) {
        	throw new ColException("获取流程process异常 [processId = " + processId + "]", e);
        }
    }

    private static BPMActor createActor(Party party) {
        String partyId = party.id;
        String partyName = party.name;
        String partyType = party.type;
        String partyAccountId = party.accountId;
        BPMParticipantType type = new BPMParticipantType(partyType);
        String roleName = "roleadmin";
        BPMActor userActor = new BPMActor(partyId, partyName, type, roleName,
        		BPMActor.CONDITION_OR, false, partyAccountId);
        userActor.getParty().setIncludeChild(party.isIncludeChild());
        return userActor;
    }

    private static void setActivityManualSelect(BPMProcess process, Map<String, String[]> manualMap) throws ColException{
        try {
            Iterator iter = manualMap.keySet().iterator();

            while (iter.hasNext()) {
                String nodeId = (String) iter.next();
                String[] manualSelect = manualMap.get(nodeId);
                String actorStr = "";
                int i = 0;
                for(String selectorId : manualSelect){
                	if(i == 0){
                		actorStr = selectorId + ",";
                	}else{
                		actorStr += selectorId + ",";
                	}
                	i++;
                }
                BPMHumenActivity activity = (BPMHumenActivity) process.getActivityById(nodeId);
                if(activity!=null){
                	 List<BPMActor> actors = activity.getActorList();
                     BPMActor actor = actors.get(0);
                     actor.getParty().setAddition(actorStr);
                }
            }

        } catch (Exception e) {
        	throw new ColException("动态设置节点人员异常", e);
        }
    }

    public static void setActivityIsDelete(BPMProcess process, Map<String, String> condition) throws ColException{
        try {
            Iterator iter = condition.keySet().iterator();
            while (iter.hasNext()) {
                String nodeId = (String) iter.next();
                String isDelete = condition.get(nodeId);
                BPMHumenActivity activity = (BPMHumenActivity) process.getActivityById(nodeId);
                if(activity==null)
                	continue;
                BPMSeeyonPolicy seeyonPolicy = (BPMSeeyonPolicy) activity.getSeeyonPolicy();
                seeyonPolicy.setIsDelete(isDelete);
            }

        } catch (Exception e) {
        	throw new ColException("动态设置节点是否进行假删除异常", e);
        }
    }

    /**
     * 动态角色预计算出来后，人工选择可能的人员，用此方法更新流程定义。
     * @param workitemId
     * @param manualMap
     */
    public static void setActivityIsDelete(Long workitemId, Map<String, String> condition) throws ColException{
        try {
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
            long caseId = workitem.getCaseId();
            BPMProcess process = getRunningProcessByCaseId(caseId);
            setActivityIsDelete(process, condition);
            updateRunningProcess(process);
        } catch (Exception e) {
        	throw new ColException("动态设置节点是否进行假删除异常", e);
        }
    }

    /**
     * 动态角色预计算出来后，人工选择可能的人员，用此方法更新流程定义。
     * @param workitemId
     * @param manualMap
     */
    public static void setActivityManualSelect(Long workitemId, Map<String, String[]> manualMap){
        try {
            WorkItem workitem = ColHelper.getWorkItemById(workitemId);
            long caseId = workitem.getCaseId();
            BPMProcess process = getRunningProcessByCaseId(caseId);
            setActivityManualSelect(process, manualMap);
            updateRunningProcess(process);
        } catch (Exception e) {
        	log.error("更新节点动态选人信息异常", e);
        }
    }

    public static void zcdbWorkitem(Long workItemId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            wim.zcdbWorkItem(userId, workItemId, null, null, null, null);
        } catch (BPMException ex) {
            throw new ColException(ex);
        }
    }

    public static void readWorkitem(Long workItemId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            wim.readWorkItem(userId, workItemId, null);
        } catch (BPMException ex) {
            throw new ColException(ex);
        }
    }

    /**
     *
     * @param workItemId
     * @throws ColException
     */
    public static void stopWorkitem(Long workItemId) throws ColException {
        String userId = CurrentUser.get().getId() + "";
        try {
            WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            wim.stopWorkItem(userId, workItemId, null, null, null, null);
        } catch (BPMException ex) {
            throw new ColException(ex);
        }
    }

    /**
	 * 分解转发人、转发次数，用于任务项分配往Affair表写
	 *
	 * @param subject
	 * @param forwardMember
	 * @param orgManager
	 * @param locale 语言，如果是null，则采用当前登录者的语言
	 * @return 转发人姓名
	 */
	public static String mergeSubjectWithForwardMembers(ColSummary summary,
			OrgManager orgManager, Locale locale) {
		return mergeSubjectWithForwardMembers(summary.getSubject(), -1,
				summary.getForwardMember(), summary.getResentTime(), orgManager, locale);
	}

	public static String mergeSubjectWithForwardMembers(String subject, String forwardMember,
			Integer resentTime, OrgManager orgManager, Locale locale){ 
		return mergeSubjectWithForwardMembers(subject, -1, forwardMember, resentTime, orgManager, locale);
	}

	/**
	 * 分解转发人、转发次数，用于任务项分配往Affair表写
	 *
	 * @param subject
	 * @param subjectLength
	 * @param forwardMember
	 * @param resentTime
	 * @param orgManager
	 * @param locale
	 * @return
	 */
	public static String mergeSubjectWithForwardMembers(String subject, int subjectLength, String forwardMember,
			Integer resentTime, OrgManager orgManager, Locale locale) {
		if(locale == null){
			User user = CurrentUser.get();
			if (user != null) {
				locale = user.getLocale();
			}
		}

		StringBuffer sb = new StringBuffer();

		if(resentTime != null && resentTime > 0){
			sb.append(Constant.getString("workflow.new.repeat.label", locale, resentTime));
		}

		if(subject != null){
			sb.append(Strings.getLimitLengthString(subject, subjectLength, "..."));
		}

		if(StringUtils.isNotBlank(forwardMember)) {
			String[] forwardMembers = forwardMember.split(",");

			for (String m : forwardMembers) {
				long memberId = Long.parseLong(m);
				try {
					V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, memberId);

					sb.append(Constant.getString("col.forward.subject.suffix", locale, member.getName()));
				}
				catch (Exception e) {
					log.warn("查询人员信息 " + memberId, e);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 根据affair得到错误提示消息，回退，撤销，取回等
	 * @param affair
	 * @return
	 */
	public static String getErrorMsgByAffair(Affair affair)
	{
		String state = "";
		String msg = "";
		if(affair != null){
			String forwardMemberId = affair.getForwardMember();
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

	    	if(affair.getIsDelete()){
	    		try{
	    			state = Constant.getString4CurrentUser("col.state.9.delete");
	    		}catch(Exception e){
	    			log.error(e.getMessage(), e);
	    		}
	    	}
	    	else{
	        	switch(StateEnum.valueOf(affair.getState())){
	        		case col_done : state = Constant.getString4CurrentUser("col.state.4.done");
	        		break;
		        	case col_cancel : state = Constant.getString4CurrentUser("col.state.5.cancel");
		        	break;
		        	case col_stepBack : state = Constant.getString4CurrentUser("col.state.6.stepback");
		        	break;
		        	case col_takeBack : state = Constant.getString4CurrentUser("col.state.7.takeback");
		        	break;
		        	case col_competeOver : state = Constant.getString4CurrentUser("col.state.8.strife");
		        	break;
		        	case col_stepStop : state = Constant.getString4CurrentUser("col.state.10.stepstop");
		        	break;
		        	case col_waitSend :
		        		switch(StateEnum.valueOf(affair.getSubState())){
		        			case col_sent:state = Constant.getString4CurrentUser("col.state.6.stepback");break;
		        			case col_pending:state = Constant.getString4CurrentUser("col.state.5.cancel");break;
		        		}
		        	break;
	        	}
	    	}
	    	String appName=Constant.getCommonString("application."+affair.getApp()+".label");
	    	msg = Constant.getString4CurrentUser("col.state.invalidation.alert", affair.getSubject(), state,appName, forwardMemberFlag, forwardMember);
		}else{
			state = Constant.getString4CurrentUser("col.state.9.delete");
			msg = Constant.getString4CurrentUser("col.state.inexistence.alert", state);
		}
    	return msg;
	}

	/**
	 * 在点击协同详情时，更改个人事项状态：未读 --> 已读
	 *
	 * @param affairId
	 * @param affairManager
	 * @throws ColException
	 */
	public static boolean updateAffairStateWhenClick(Affair affair, AffairManager affairManager) throws ColException{
		boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //branches_a8_v350sp1_r_gov GOV-4867 向凡添加，政务版标识
        if(affair == null || affair.getIsDelete() ||
        		(affair.getState() != StateEnum.col_pending.key()
        		&& affair.getState() != StateEnum.col_sent.key()
        		&& affair.getState() != StateEnum.col_waitSend.key()
        		&& affair.getState() != StateEnum.col_done.key() && !isGovVersion)){//branches_a8_v350sp1_r_gov GOV-4867 向凡添加，政务版此处不需要再次验证，外层代码已经做了验证
        	String msg = getErrorMsgByAffair(affair);
        	throw new ColException(msg);
        }else{
	        //当前未读
	        Integer sub_state = affair.getSubState();
	        if (sub_state == null || sub_state.intValue() == SubStateEnum.col_pending_unRead.key()) {
		        affair.setSubState(SubStateEnum.col_pending_read.key()); //标记为已读
	            affairManager.updateAffair(affair);
	            //要把已读状态写写进流程
	            //TODO 判断系统开关
	            if(affair.getSubObjectId()!=null)
	            readWorkitem(affair.getSubObjectId());
	            return true;
	        }
		}
        return false;
	}


	/**
	 *
	 * @param allOpinions
	 *            所有的意见
	 * @param allComments
	 *            所有的意见回复
	 * @param originalSendOpinionKey
	 *            转发前的LevelId
	 * @param originalSendOpinion
	 *            转发前的意见，key - LevelId
	 * @param originalSignOpinion
	 *            转发前的意见回复，key - LevelId
	 * @param opinions
	 *            当前协同的意见
	 * @param senderOpinion
	 *            当前协同的附言
	 * @param commentsMap
	 *            当前协同的意见回复 key - OpinionId
	 * @param isOnlyCurrentCol
	 *            是否仅需要当前协同的意见和意见回复
	 * @param orgManager
	 */
	public static void modulateCommentOpinion(Set<ColOpinion> allOpinions,
			Set<ColComment> allComments, List<Integer> originalSendOpinionKey,
			Map<Integer, List<ColOpinion>> originalSendOpinion,
			Map<Integer, List<ColOpinion>> originalSignOpinion,
			List<ColOpinion> opinions, List<ColOpinion> senderOpinion,
			Map<Long, List<ColComment>> commentsMap, OrgManager orgManager, boolean isOnlyCurrentCol) {
		if (allComments != null) {
			for (ColComment comment : allComments) {
				Long key = comment.getOpinionId();
				if (commentsMap.containsKey(key)) {
					commentsMap.get(key).add(comment);
				}
				else {
					List<ColComment> c = new ArrayList<ColComment>();
					c.add(comment);
					commentsMap.put(key, c);
				}
			}
		}

		if (allOpinions != null) {
			for (ColOpinion opinion : allOpinions) {
				Integer levelId = opinion.getLevelId();
				int opinionType = opinion.getOpinionType().intValue();
				if(opinionType == ColOpinion.OpinionType.draftOpinion.ordinal()){ //草稿不显示
					continue;
				}

				if (levelId == 0) { //当前协同�?
					if (opinionType == ColOpinion.OpinionType.senderOpinion.ordinal()) { //发起人附言
						senderOpinion.add(opinion);
					}
					else { //处理人意�?
						opinions.add(opinion);
					}
				}
				else if(levelId > 0 && isOnlyCurrentCol == false) {//原协�?
					if (originalSendOpinion != null &&
							opinionType == ColOpinion.OpinionType.senderOpinion.ordinal()) { //发起人附言
						if (originalSendOpinion.containsKey(levelId)) {
							originalSendOpinion.get(levelId).add(opinion);
						}
						else{
							List<ColOpinion> o = new ArrayList<ColOpinion>();
							o.add(opinion);
							originalSendOpinion.put(levelId, o);
						}
					}
					else if(originalSignOpinion != null) { //处理人意�?
						if (originalSignOpinion.containsKey(levelId)) {
							originalSignOpinion.get(levelId).add(opinion);
						}
						else {
							List<ColOpinion> o = new ArrayList<ColOpinion>();
							o.add(opinion);
							originalSignOpinion.put(levelId, o);
						}
					}

					if (originalSendOpinionKey != null && !originalSendOpinionKey.contains(levelId)) {
						originalSendOpinionKey.add(levelId);
					}
				}
			}
		}
	}

	public static void addExtPropsToAffairFromSummary(Affair affair, ColSummary summary, ColBody body){
		affair.setBodyType(body.getBodyType());
		affair.setHasAttachments(summary.isHasAttachments());
		affair.setImportantLevel(summary.getImportantLevel());
		affair.setResentTime(summary.getResentTime());
		affair.setForwardMember(summary.getForwardMember());

		affair.serialExtProperties();
	}

	/**
	 * 将流程XML转换成人员字符串，用于显示在流程文本框中，只取5个人
	 *
	 * @param caseProcessXML
	 * @return
	 */
	public static List<Party> getWorkflowInfo(BPMProcess process) {
		if(process == null){
			return null;
		}

		String isShowShortName = process.getIsShowShortName();
		Boolean showName = false;
		if("true".equals(isShowShortName)){
			showName = true;
		}
		BPMStatus start = process.getStart();
		List<Party> parties = new ArrayList<Party>();

		addNodeToParty(start, parties, 10, showName);

		return parties;
	}

	@SuppressWarnings("unchecked")
	public static void addNodeToParty(BPMAbstractNode o, List<Party> parties, int level, Boolean showName){
		String nodeType = o.getNodeType().name().trim().toLowerCase();

		if(!java.util.regex.Pattern.matches("start|split|join|end", nodeType)) {
			List<BPMActor> actors = o.getActorList();
			Party party = null;
			String seeyonPolicy = o.getSeeyonPolicy().getId();
			for (BPMActor actor : actors) {
				String type = actor.getParty().getType().id;
				String[] nodeInfo = actor.getNodeInfo();
				String id = actor.getParty().getId();
				String accountId = actor.getParty().getAccountId();
				if("Role".equals(type) && "BlankNode".equals(id)){
					seeyonPolicy = null;
				}
				if(!showName){
					accountId = "";
				}

				if(parties.size() >= level){
					return;
				}
				boolean exists = false;
				for(Party p:parties){
					if(o.getId().equals(p.getActivityId()) && id.equals(p.getId())){
						exists = true;
						break;
					}
				}
				if(!exists){
					party = new Party(type, id, nodeInfo[0], accountId, nodeInfo[1], seeyonPolicy,o.getId());
					if(!parties.contains(party))
						parties.add(party);
				}
			}
		}

		List<BPMTransition> ts = o.getDownTransitions();
		if(ts == null){
			return;
		}

		for (BPMTransition t : ts) {
			addNodeToParty(t.getTo(), parties, level, showName);
		}
	}

	/**
	 * 转换人员名称
	 *
	 * @param forwardMember
	 * @param orgManager
	 * @return
	 */
	public static List<String> getForwardMemberNames(String forwardMember, OrgManager orgManager){
        if(StringUtils.isNotBlank(forwardMember)){
    		String[] forwardMembers = forwardMember.split(",");
    		List<String> forwardMemberNames = new ArrayList<String>(forwardMembers.length);

    		for (String m : forwardMembers) {
    			long memberId = Long.parseLong(m);
    			try {
					V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, memberId);
					forwardMemberNames.add(member != null ? member.getName() : "");
				} catch (Exception e) {
					log.error("查询人员信息：" + memberId, e);
				}
    		}
    		return forwardMemberNames;
        }

        return null;
	}


	public static String[] getFormPolicyByAffair(Affair affair) throws ColException {
        if (affair == null)
            return null;
        Long workitemId = affair.getSubObjectId();
        if (workitemId == null || workitemId == 0) {
            return null;
        }

        WorkItem workitem = getWorkItemById(workitemId);
        String activityId = workitem.getActivityId();
        long caseId = workitem.getCaseId();
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
            process = (BPMProcess) pe.getCaseProcess(caseId);
        } catch (BPMException e) {
            throw new ColException(e);
        }

        BPMActivity activity = process.getActivityById(activityId);
        BPMSeeyonPolicy seeyonPolicy = activity.getSeeyonPolicy();
        String[] results = new String[4];
        results[0] = seeyonPolicy.getFormApp();
        results[1] = seeyonPolicy.getForm();
        results[2] = seeyonPolicy.getOperationName();
        results[3] = seeyonPolicy.getName();
        return results;

    }

	/**
	 * 判断父节点是否发起节点
	 * @param caseId
	 * @param workitemId
	 * @return
	 * @throws ColException
	 */
	public static boolean isSecondNode(Long caseId, Long workitemId , BPMActivity activity) throws ColException{
		try{
			if(activity == null) {
				BPMProcess process = getRunningProcessByCaseId(caseId);
		        WorkItem workitem = getWorkItemById(workitemId);
		        String activityId = workitem.getActivityId();
		        activity = process.getActivityById(activityId);
			}
			Map<String,Object> resultMap = Utils.isCanNotStepOfGivenPolicy(activity, null, false);
			String result_str= (String)resultMap.get("result");
			if("1".equals(result_str)){
				return true;
			}
	        return false;
		}catch(ColException e){
			throw new ColException(e);
		}
	}

	/**
	 * 取当前节点的父人工节点
	 * @param activity   当前人工节点
	 * @return
	 */
	public static List getParent(BPMActivity activity){
		List humenList = new ArrayList();
        List<BPMTransition> transitions = activity.getUpTransitions();
        for (BPMTransition trans : transitions) {
            BPMAbstractNode parent = trans.getFrom();
            BPMSeeyonPolicy policy= parent.getSeeyonPolicy();
        	String policyId= policy.getId();
        	String isDelete= policy.getIsDelete();
        	if("false".equals(isDelete)){
        		if (parent.getNodeType() == BPMAbstractNode.NodeType.humen) {
                	//处理空节点及知会节点
                	if(policyId.equals("zhihui") || policyId.equals("inform")){
                		humenList.addAll(getParent((BPMActivity) parent));
                	}else{
                		humenList.add((BPMHumenActivity) parent);
                	}
                } else if(parent.getNodeType() == BPMAbstractNode.NodeType.start){
                	humenList.add(parent);
                } else if (parent.getNodeType() == BPMAbstractNode.NodeType.join || parent.getNodeType() == BPMAbstractNode.NodeType.split) {
                    humenList.addAll(getParent((BPMActivity) parent));
                }
        	}
        }
        return humenList;
	}


	public static void findDirectHumenChildrenCondition(BPMAbstractNode current_node,HashMap<String,String> result) {
        List<BPMTransition> down_links = current_node.getDownTransitions();
        String upCondition = result.get(current_node.getId());
        String preForce = null;
        if(upCondition != null && upCondition.endsWith("↗1")) {
        	upCondition = upCondition.substring(0,upCondition.indexOf("↗1"));
        	preForce = "1";
        }
        String currentIsStart = result.get("currentIsStart");
        //知会节点数
        int informCount = 0;
        for (BPMTransition down_link : down_links) {

            BPMAbstractNode _node = down_link.getTo();
            String isForce = preForce==null?down_link.getIsForce():preForce;
            String currentCondition = down_link.getFormCondition();
            String conditionBase = down_link.getConditionBase();
            if(currentCondition != null){
            	currentCondition = currentCondition.replaceAll("isNotRole", "isnotrole").replaceAll("isRole", "isrole").replaceAll("isPost", "ispost")
            	.replaceAll("isNotPost", "isNotpost");
            }
            if("start".equals(conditionBase) && !"true".equals(currentIsStart))
            	currentCondition = currentCondition.replaceAll("Department", "startdepartment").replaceAll("Post", "startpost").replaceAll("Level", "startlevel")
            	.replaceAll("team", "startTeam").replaceAll("secondpost", "startSecondpost").replaceAll("Account", "startaccount")
            	.replaceAll("standardpost", "startStandardpost").replaceAll("grouplevel", "startGrouplevel").replaceAll("Role", "startrole")
            	.replaceAll("ispost", "isStartpost").replaceAll("isNotpost", "isNotStartpost").replaceAll("isNotDep", "isNotStartDep").replaceAll("isDep", "isStartDep");
            if("1".equals(isForce)) {
            	String str = result.get(_node.getId());
            	if(str!=null&&!"".equals(str)) {
	            	String[] arr = str.split("↗");
	            	if(arr!=null) {
	            	    switch(arr.length) {
		            	    /*case 2:
		            			if(!_node.getId().equals(arr[0])) {
		            				isForce = null;
		            			}
		            			break;*/
		            	    case 3:
		            			isForce = null;
		            			break;
	            	    }
	            	}
            	}
            }else
            	isForce = null;
            if (_node instanceof BPMHumenActivity) {
                BPMHumenActivity node = (BPMHumenActivity) _node;
                BPMSeeyonPolicy policy = node.getSeeyonPolicy();
                boolean isNew = result.get(node.getId())==null;

                String nodeName = node.getBPMAbstractNodeName();

                if(upCondition==null||"".equals(upCondition)){

                	if(down_link.getConditionType()==1||down_link.getConditionType()==4)
                		result.put(node.getId(), nodeName+"↗"+currentCondition);
                	else if(down_link.getConditionType()==2)
                		result.put(node.getId(), nodeName+"↗handCondition");
                }
                else if(upCondition.indexOf("↗")==-1){
                	if(down_link.getConditionType()==1||down_link.getConditionType()==4) {
                		result.put(node.getId(), nodeName+"↗("+upCondition + ")" + (currentCondition==null||"".equals(currentCondition)?"":" && (" + currentCondition + ")"));
                		//result.remove(current_node.getId());
                	}
                	else if(down_link.getConditionType()==2) {
                		result.put(node.getId(), nodeName+"↗("+upCondition + ")" + (currentCondition==null||"".equals(currentCondition)?"":" && (handCondition)"));
                		//result.remove(current_node.getId());
                	}else{
                		result.put(node.getId(), nodeName+"↗"+upCondition);
                	}
                }

                if(!"inform".equals(policy.getId()) && !"zhihui".equals(policy.getId())) {
                	if(isNew) {
		                increaseNodeCount(result);
                	}
                }

                if(policy != null) {
                	result.put("linkTo"+node.getId(), down_link.getId());
                }

                String order = result.get("order");
                if(order==null)
                	result.put("order", node.getId());
                else
                	result.put("order", order+"$"+node.getId());

                if(isForce!=null && result.get(node.getId()) != null)
                	result.put(node.getId(), result.get(node.getId())+"↗"+isForce);

                if(policy != null && ("inform".equals(policy.getId()) || "zhihui".equals(policy.getId()))) {
                	informCount++;
                	List<BPMTransition> next_links = node.getDownTransitions();
                	if(next_links != null) {
                		BPMAbstractNode nextNode = next_links.get(0).getTo();
                		if(nextNode instanceof BPMHumenActivity)
                			findDirectHumenChildrenCondition(node,result);
                		else if((nextNode instanceof BPMAndRouter)) {
                			BPMAndRouter _nextNode = (BPMAndRouter)nextNode;
                			if(_nextNode.isStartAnd())
                				findDirectHumenChildrenCondition(nextNode,result);
                			else{
/*                				// 如果Split后面全是知会，知会后续节点是Join的话没必要再递归下去
                				if(informCount==down_links.size() && !isJoinNode(_nextNode))
                					findDirectHumenChildrenCondition(nextNode,result);
                				else{
	                				//如果下节点是join，nodecount+1
	                				increaseNodeCount(result);
                				}*/
                				// 如果Split后面全是知会，递归
                				if(informCount==down_links.size())
                					findDirectHumenChildrenCondition(nextNode,result);
                				else{
	                				//不全是知会，nodecount+1
	                				increaseNodeCount(result);
                				}
                			}
                		}
                	}
                }
            } else if (_node instanceof BPMAndRouter || _node instanceof BPMConRouter) {
            	if(upCondition!=null && !"".equals(upCondition)){
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4)
            			result.put(_node.getId(), "(" + upCondition + ")" + " && (" + currentCondition + ")");
            		else
            			result.put(_node.getId(), upCondition);
            	}
            	else{
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4)
            			result.put(_node.getId(), currentCondition);
            		else if(down_link.getConditionType()==2)
                		result.put(_node.getId(), "handCondition");
            		else
            			result.put(_node.getId(), "");
            	}
            	if(isForce!=null)
                	result.put(_node.getId(), result.get(_node.getId())+"↗"+isForce);
                findDirectHumenChildrenCondition(_node,result);
                result.remove(_node.getId());
            } else if (_node instanceof BPMEnd) {
                return;
            }
        }
    }

	private static void increaseNodeCount(Map<String, String> result) {
		if(result.get("nodeCount")==null)
			result.put("nodeCount", "1");
		else {
			int count = Integer.parseInt(result.get("nodeCount"))+1;
			result.put("nodeCount", String.valueOf(count));
		}
	}

	public static BPMAbstractNode getStartNode(String xml){
		BPMProcess process = BPMProcess.fromXML(xml);
		return process.getStart();
	}
	/**
	 *
	 * @param process
	 * @param affair
	 * @return
	 * @throws ColException
	 */
	public static boolean isExecuteFinished(BPMProcess process, Affair affair)throws ColException{
		try {
			WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
			if(affair != null){
				Long subObjectId = affair.getSubObjectId();
				if(null != subObjectId){
					long workitemId = affair.getSubObjectId();
					WorkItem workitem = getWorkItemById(workitemId);
					if(wim == null){
						if(log.isInfoEnabled()){
							log.info("wim:="+wim);
						}
						return false;
					}else{
						return wim.isExcuteFinished(workitem, process);
					}
				}
				if(log.isInfoEnabled()){
					log.info("subObjectId:="+subObjectId);
				}
			}
			if(log.isInfoEnabled()){
				log.info("affair:="+affair);
			}
		} catch (BPMException e) {
			throw new ColException("检查当前节点所有工作项是否都已经完成", e);
		}
		return false;
	}

	public static List getOrgEntity(String orgType) throws ColException{
		List list = new ArrayList();
		OrgManager orgManager = getOrgManager();
		long accountId = CurrentUser.get().getAccountId();
		try {
			if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgType)) {
				list = orgManager.getAllDepartments(accountId);
			}else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equals(orgType)) {
				list = orgManager.getAllTeams(accountId);
			}else if(V3xOrgEntity.ORGENT_TYPE_POST.equals(orgType)) {
				list = orgManager.getAllPosts(accountId);
			}else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(orgType)) {
				list = orgManager.getAllLevels(accountId);
			}
		}catch(Exception e) {
			throw new ColException(e);
		}
		return list;
	}

	public static void setPolicy(String[] policys,String[] itemNames,FlowData flowData) {
		if(policys != null && policys.length > 0 && itemNames != null && !"".equals(policys[0]) && !"".equals(itemNames[0])) {
        	Map<String,String> usedPolicys = new HashMap<String,String>();
        	for(int i=0;i<policys.length;i++) {
        		usedPolicys.put(policys[i],itemNames[i]);
        	}
        	if(usedPolicys.size()>0)
        		flowData.setUsedPolicy(usedPolicys);
        }
	}

	public static BPMActivity getBPMActivityByAffair(BPMProcess process, Affair affair) throws ColException {
        if (affair == null)
            return null;

        String activityId = null;
        if(affair.getActivityId() == null){
        	activityId = getActvityIdByAffair(affair);
        }
        else{
        	activityId = String.valueOf(affair.getActivityId());
        }

        if(activityId == null){
        	return null;
        }

        BPMActivity activity = process.getActivityById(activityId);
        return activity;
	}

	public static BPMActivity getBPMActivityByAffair(Affair affair) throws ColException {
        if (affair == null)
            return null;
        Long workitemId = affair.getSubObjectId();
        if (workitemId == null || workitemId == 0) {
            return null;
        }

        WorkItem workitem = getWorkItemById(workitemId);
        String activityId = workitem.getActivityId();
        long caseId = workitem.getCaseId();
        ProcessEngine pe = getProcessEngine();
        BPMProcess process = null;
        try {
            process = (BPMProcess) pe.getCaseProcess(caseId);
        } catch (BPMException e) {
        	throw new ColException(e);
        }

        BPMActivity activity = process.getActivityById(activityId);
        return activity;
    }

	public static BPMActivity getActivityById(String xml,String id) {
		BPMProcess process = BPMProcess.fromXML(xml);
		return process.getActivityById(id);
	}
	/**
	 * 根据caseId获取process
	 * @param caseId
	 * @return BPMProcess
	 * @throws ColException
	 */
	public static BPMProcess getRunningProcessByCaseId(Long caseId)throws ColException{
		BPMProcess process = null;
		ProcessEngine pe = getProcessEngine();
        try {
			process = (BPMProcess) pe.getCaseProcess(caseId);
		} catch (BPMException e) {
			log.error("获取runningProcess异常[caseId= " + caseId + "]", e);
			throw new ColException("获取runningProcess异常[caseId= " + caseId + "]", e);
		}
		return process;
	}

	/**
	 * 根据processId获取process
	 * @param processId
	 * @return BPMProcess
	 * @throws ColException
	 */
	public static BPMProcess getRunningProcessByProcessId(String processId)throws ColException{
		BPMProcess process = null;
		ProcessEngine pe = ColHelper.getProcessEngine();
        try {
			process = pe.getProcessRunningById(processId);
		} catch (BPMException e) {
			log.error("获取RunningProcess异常 [processId =" + processId + "]", e);
			throw new ColException("获取runningProcess异常[processId ="+ processId+ "]", e);
		}
		return process;
	}

	/**
	 * 更新process
	 * @param process
	 * @return true成功，false失败
	 * @throws ColException
	 */
	public static boolean updateRunningProcess(BPMProcess process)throws ColException{
		ProcessEngine pe = getProcessEngine();
        try {
			pe.updateRunningProcess(process);
		} catch (BPMException e) {
			log.error("获取RunningProcess异常 [processId =" + process.getId() + "]", e);
			return false;
		}
		return true;
	}

	public static boolean addRunningProcess(String processId)throws ColException{
		ProcessEngine pe = getProcessEngine();
        try {
			pe.addProcessToRunning(processId);
		} catch (BPMException e) {
			log.error("获取RunningProcess异常 [processId =" + processId + "]", e);
			return false;
		}
		return true;
	}
	/**
	 * 公文督办更新流程
	 * @param processId 督办流程Id
	 * @param activityId 执行操作的节点Id
	 * @param operationType 执行操作的类型
	 * @param objects 可变参数,(操作返回数据...)
	 * @return Boolean (true-成功,false-失败)
	 * @throws BusinessException
	 */
	public static String[] superviseUpdateProcess(String processId, String activityId, int operationType, FlowData flowData,
			BPMSeeyonPolicy policy, User user, String[] selecteNodeIdArr, String[] _peopleArr, Long caseId)throws BusinessException{
		return superviseUpdateProcess(processId, activityId, operationType, flowData, policy, user, selecteNodeIdArr, _peopleArr, caseId, false);
	}


	/**
	 * 公文督办更新流程
	 * @param processId 督办流程Id
	 * @param activityId 执行操作的节点Id
	 * @param operationType 执行操作的类型
	 * @param objects 可变参数,(操作返回数据...)
	 * @return Boolean (true-成功,false-失败)
	 * @throws BusinessException
	 */
	public static String[] superviseUpdateProcess(String processId, String activityId, int operationType, FlowData flowData,
			BPMSeeyonPolicy policy, User user, String[] selecteNodeIdArr, String[] _peopleArr, Long caseId, boolean isForm)throws BusinessException{
		String userId = String.valueOf(user.getId());
    	BPMProcess process = null;
        try {
        	process = getModifyingProcess(processId, userId);
        	if(process == null)
        		process = getRunningProcessByProcessId(processId);
        } catch (ColException e) {
        	throw new ColException("获取流程process异常 [processId = " +processId+ "]", e);
        }

        BPMActivity acitvity = process.getActivityById(activityId);
        boolean  isNodePolicyChange= false;
        boolean  isOperationNameChange= false;
        String oldNodePolicy= acitvity.getSeeyonPolicy().getName();
        String oldOperationName= acitvity.getSeeyonPolicy().getOperationName();
		//督办时前台传过来的operationType在会签多个时不准确，后台重新计算一下，如果当前节点在readyAddedMap中存在，则新会签的节点也要加入
		if((operationType == 1 && flowData.getType() == FlowData.FLOWTYPE_COLASSIGN) || operationType == 5) {
			try {
				ProcessEngine pe = getProcessEngine();
				Map<Long, ReadyObject> readyAddedMap = pe.getReadyAddedMap();
				if(readyAddedMap != null) {
					ReadyObject readyObject = (ReadyObject)readyAddedMap.get(Long.parseLong(processId));
					if(readyObject != null) {
						List<BPMActivity> readyActivityList = readyObject.getActivityList();
						if(readyActivityList != null) {
							boolean containCurrentNode = false;
							for(BPMActivity activity:readyActivityList) {
								if(activityId.equals(activity.getId())) {
									containCurrentNode = true;
									break;
								}
							}
							if(containCurrentNode) {
								if(operationType == 1)
									operationType = 2;
								else if(operationType == 5)
									operationType = 6;
							}
						}
					}
				}
			}catch(Exception e) {
				log.error(e);
			}
		}

		if(operationType == 1){
			//待办之后添加新节点
			addNewActivity(process, activityId, flowData, userId, caseId, false);
		}
		else if(operationType == 2){
			//已办之后,未激活节点之前
			addNewActivity(process, activityId, flowData, userId, caseId, true);
		}
		else if(operationType == 3){
			//删除未激活节点
			delNoActivationNode(process, activityId, flowData, userId);
		}
		else if(operationType == 4){
			//删除之前,对下节点进行人员匹配
			Map<String,String[]> manualMap = new HashMap<String,String[]>();
			int i = 0;
			for(String nodeId : selecteNodeIdArr){
				if(nodeId != null && !"".equals(nodeId)){
					String[] people = _peopleArr[i].split(",");
					manualMap.put(nodeId, people);
					i++;
				}
			}
			if(manualMap != null && !manualMap.isEmpty())
				setActivityManualSelect(process, manualMap);
			//处理分支
			if(flowData.getCondition()!=null){
				ColHelper.setActivityIsDelete(process, flowData.getCondition());
			}
			//删除待办节点
			delNoActivationNode1(process, activityId, flowData, userId, caseId);
		}
		else if(operationType == 5){
			//替换未激活节点
			replaceActivity(process, activityId, flowData, userId);
		}
		else if(operationType == 6){
			//替换待办节点
			replaceActivity1(process, activityId, flowData, userId, caseId);
		}
		else if(operationType == 7){
			isNodePolicyChange= !policy.getId().equals(acitvity.getSeeyonPolicy().getId());
			if(isForm){
				isOperationNameChange= !oldOperationName.equals(policy.getOperationName());
			}
			//设置节点属性
			setActivityPolicy(process, activityId, flowData, policy, userId, isForm);
		}

		try{
			//流程日志
			List<ProcessLog> list = processLogMetaMap.get(Long.parseLong(process.getId()));
			
			if(list == null){
				list = new ArrayList<ProcessLog>();
			}else{
				processLogMetaMap.remove(Long.parseLong(process.getId()));
				//CLUSTER 集群-更新备机
				NotificationManager.getInstance().send(NotificationType.Collaboration_UpdateProcessLog_Remove, Long.parseLong(process.getId()));
			}
			List<Party> people = flowData.getPeople();
			switch(operationType){
			//增加节点
			case 1:
			case 2:
				for(Party p :people){
					ProcessLog pLog = new ProcessLog(Long.parseLong(process.getId()),Long.parseLong(p.getActivityId()),ProcessLogAction.addNode,user.getId(),p.getSeeyonPolicy().getName(),p.getName());
					list.add(pLog);
				}
				break;
			//删除节点
			case 3:
			case 4:
				ProcessLog psLog = new ProcessLog(Long.parseLong(process.getId()),1l,ProcessLogAction.deleteNode,user.getId(),acitvity.getSeeyonPolicy().getName(),acitvity.getName());
				list.add(psLog);
				break;
			//节点替换
			case 5:
			case 6:
				BPMActivity acitvitys =  process.getActivityById(activityId);
				for(Party p :people){
					ProcessLog pLog = new ProcessLog(Long.parseLong(process.getId()),1l,ProcessLogAction.replaceNode,user.getId(),acitvitys.getSeeyonPolicy().getName(),p.getName());
					list.add(pLog);
				}
				break;
			//设置节点属性
			case 7:
				/*for(ProcessLog pLog : list){
					if(activityId.equals(pLog.getActivityId().toString())){
						pLog.setParams(policy.getName(),pLog.getParam1());
					}
				}*/
				//增加表单绑定日志
				if(isForm){
					if(isOperationNameChange){
						BPMActivity currentActivity =  process.getActivityById(activityId);
						BPMSeeyonPolicy seeyonPolicy = currentActivity.getSeeyonPolicy();
						ColManager colManager = (ColManager)ApplicationContextHolder.getBean("colManager");
						ColSummary colSummary = colManager.getSummaryByProcessId(processId);
						String operationName = FormHelper.getFormNameAndOperationName(Long.valueOf(seeyonPolicy.getFormApp()),Long.valueOf(policy.getForm()), Long.valueOf(policy.getOperationName()));
						ProcessLog pLog= new ProcessLog(Long.parseLong(process.getId()),Long.parseLong(activityId),ProcessLogAction.nodeproperties,user.getId(),user.getName(),colSummary.getSubject(),currentActivity.getName(),operationName);
						list.add(pLog);
					}
				}
				if(isNodePolicyChange){
					if(!list.isEmpty() && list.size()>0){
						boolean isAdd= false;
						//新增新节点
						for (ProcessLog pLog : list) {
							if(pLog.getActivityId().toString().equals(acitvity.getId())
									&& pLog.getActionId().intValue()== ProcessLogAction.changeNodePolicy.ordinal() ){
								pLog.setParam0(acitvity.getSeeyonPolicy().getName());
								isAdd= true;
								break;
							}
						}
						// 更改新/老节点
						if(!isAdd){
							ProcessLog pLog1 = new ProcessLog(Long.parseLong(process.getId()),Long.parseLong(acitvity.getId()),ProcessLogAction.changeNodePolicy,user.getId(),user.getName(),acitvity.getName(),oldNodePolicy,acitvity.getSeeyonPolicy().getName());
							list.add(pLog1);
						}
					}else{ // 第一次更改老节点
						ProcessLog pLog = new ProcessLog(Long.parseLong(process.getId()),Long.parseLong(acitvity.getId()),ProcessLogAction.changeNodePolicy,user.getId(),user.getName(),acitvity.getName(),oldNodePolicy,acitvity.getSeeyonPolicy().getName());
						list.add(pLog);
					}
				}
			}
			
			//此处用processId做key
			processLogMetaMap.put(Long.parseLong(process.getId()), list);

			//CLUSTER 集群通知点，先删再加
			NotificationManager.getInstance().send(NotificationType.Collaboration_UpdateProcessLog_Put, list);
		}catch(Exception e){
			log.error("协同日志:"+e);
		}

		String caseLogXML = getCaseLogXML(userId, caseId);
        String caseWorkItemLogXML = getCaseWorkItemLogXML(userId, caseId);
        String caseProcessXML = getModifyingProcessXML(userId, processId);

		return new String[]{caseProcessXML, caseLogXML, caseWorkItemLogXML};
	}

	/**
	 * 公文督办添加新节点--待办节点之后增加节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 * @throws ColException
	 */
	private static void addNewActivity(BPMProcess process, String activityId, FlowData flowData, String userId, Long caseId, boolean isPending) throws ColException {
        List<Party> people = flowData.getPeople();
        ProcessEngine pe = getProcessEngine();
	    try {
	        if ((flowData.getType() == FlowData.FLOWTYPE_SERIAL || people.size() == 1) && flowData.getType() != FlowData.FLOWTYPE_COLASSIGN) {
	        	//串发
	            addSerial(process, activityId, flowData, new BPMSeeyonPolicy("shenpi","审批"), isPending, caseId, false);
	        }
	        else if(flowData.getType() == FlowData.FLOWTYPE_PARALLEL){
	        	//并发
	        	addParellel(process, activityId, flowData, new BPMSeeyonPolicy("shenpi","审批"), isPending, caseId, false);
	        }else{
	        	//会签
	        	colAssign(process, activityId, flowData, null, caseId, isPending);
	        }
	        process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
	    }catch (Exception e) {
	    	throw new ColException("增加新节点异常 [activityId = " +activityId+ "]", e);
	    }
    }

    public static FlowData delActivity(BPMProcess process, String activityId, FlowData flowData, String userId) throws ColException {
        ProcessEngine pe = null;
        pe = getProcessEngine();
        try{
        	delNoActivationNode(process, activityId, flowData, userId);
        	process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
        	pe.replaceProcessByProcessId(null, process);
        }catch (Exception e) {
        	throw new ColException("删除节点操作异常 [activityId = " + activityId + "]", e);
        }
        return flowData;
    }

	/**
	 * 公文督办添加串发新节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 * @param policy 添加新节点的节点策略
	 */
	public static void addSerial(BPMProcess process, String activityId, FlowData flowData, BPMSeeyonPolicy policy,
			boolean isPending, Long caseId, boolean isFormReadonly) throws ColException{
		List<BPMActivity> activityList = new ArrayList<BPMActivity>();
    	List<Party> people =  flowData.getPeople();
        User user = CurrentUser.get();
        ProcessEngine pe = getProcessEngine();
        try{
            BPMActivity currentActivity = process.getActivityById(activityId);
            List downTransitions = currentActivity.getDownTransitions();

            BPMTransition tran = (BPMTransition)downTransitions.get(0);
            BPMAbstractNode childNode = (BPMAbstractNode)tran.getTo();
            boolean isJoin = (childNode instanceof BPMAndRouter) && !((BPMAndRouter) childNode).isStartAnd();

            //添加的第一个结�点
            BPMAbstractNode firstNode = null;
            //添加的最后一个结�点
            BPMAbstractNode lastNode = null;
            BPMAbstractNode previousNode = currentActivity;
            boolean isAddReady = true;
            String formApp = "";
            String form = "";
            String operationName = "";
            if(currentActivity.getNodeType().equals(BPMAbstractNode.NodeType.join)){//当前节点为join节点加签时，需要进行特殊处。
            	List<BPMHumenActivity> parents= getParentHumens(currentActivity);
            	if(parents.size()>0){
            		BPMHumenActivity myRealParentNode= parents.get(0);
            		BPMSeeyonPolicy mySeeyonPolicy = myRealParentNode.getSeeyonPolicy();
            		formApp = mySeeyonPolicy.getFormApp();
                	form = mySeeyonPolicy.getForm();
                	operationName = mySeeyonPolicy.getOperationName();
            	}
            }else{
            	BPMSeeyonPolicy seeyonPolicy = currentActivity.getSeeyonPolicy();
            	if(seeyonPolicy != null) {
                	formApp = seeyonPolicy.getFormApp();
                	form = seeyonPolicy.getForm();
                	operationName = seeyonPolicy.getOperationName();
                }
            }
            for (int i = 0; i < people.size(); i++) {
                Party party = people.get(i);
                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.name);
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);
                party.setActivityId(userNode.getId());

                if(party.getSeeyonPolicy() != null){
                	userNode.setSeeyonPolicy(party.getSeeyonPolicy());
                }
                else if(flowData.getSeeyonPolicy() != null){
                	userNode.setSeeyonPolicy(new BPMSeeyonPolicy(flowData.getSeeyonPolicy()));
                }else{
                	userNode.setSeeyonPolicy(new BPMSeeyonPolicy(policy));
                }

                userNode.getSeeyonPolicy().setFormApp(formApp);
                userNode.getSeeyonPolicy().setForm(form);
                userNode.getSeeyonPolicy().setOperationName(operationName);
               	userNode.getSeeyonPolicy().setFR(isFormReadonly? "1" : "");

                if(!"user".equals(party.getType())){
                	BPMSeeyonPolicy _policy = userNode.getSeeyonPolicy();
                	_policy.setProcessMode("all");
                }

                if (firstNode == null) {
                    firstNode = userNode;
                }
                BPMTransition userLink1 = new BPMTransition(previousNode, userNode);
                process.addChild(userNode);
                process.addLink(userLink1);
                previousNode = userNode;
                if(isAddReady){
                	activityList.add((BPMActivity)userNode);
                	isAddReady = false;
                }
            }
            lastNode = previousNode;
            if (downTransitions != null) {
                for (int i = 0; i < downTransitions.size(); i++) {
                    BPMTransition trans = (BPMTransition) downTransitions.get(i);
                    BPMAbstractNode to = trans.getTo();
                    BPMTransition userLink1 = new BPMTransition(lastNode, to);
                    copyCondition(trans, userLink1);
                    process.addLink(userLink1);
                    process.removeLink(trans);
                    currentActivity.removeDownTransition(trans);
                }
            }

            Date now = new Date(System.currentTimeMillis());
            process.setUpdateDate(now);
            String isShowShortName = flowData.getIsShowShortName();
            if("false".equals(isShowShortName)){
            	if("false".equals(process.getIsShowShortName()))
            		process.setIsShowShortName(isShowShortName);
            }else{
            	process.setIsShowShortName(isShowShortName);
            }

            //将加入的人设为ready状�态
            BPMCase theCase = null;
            if(isPending){
            	ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
                pdm.saveOrUpdateProcessInReady(process);

            	theCase = pe.getCase(caseId);
	            ReadyObject readyObject = new ReadyObject();
	            readyObject.setActivityList(activityList);
	            readyObject.setCaseId(caseId+"");
	            readyObject.setProcessId(process.getId());
	            readyObject.setUserId(user.getId()+"");

	            boolean saveTheCaseFlag = false;
	            if(isJoin && theCase != null) {
	            	ReadyNode node = theCase.getReadyActivityById(childNode.getId());
	            	if(node != null) {
	            		node.setNum(node.getNum()+1);
	            		saveTheCaseFlag = true;
	            	}
	            }
	            readyObject.setSaveTheCaseFlag(saveTheCaseFlag);
	            pe.addReadyActivityMap(process.getId(), readyObject);
	            deleteReadyProcess(process.getId());
            }
        } catch (BPMException e) {
        	throw new ColException("增加节点串发操作异常", e);
        }
	}

	/**
	 * 公文督办添加并发新节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 * @param policy 添加新节点的节点策略
	 */
	public static void addParellel(BPMProcess process, String activityId, FlowData flowData, BPMSeeyonPolicy policy,
			boolean isPending, Long caseId, boolean isFormReadonly) throws ColException{
		List<BPMActivity> activityList = new ArrayList<BPMActivity>();
    	List<Party> people =  flowData.getPeople();
        User user = CurrentUser.get();
        ProcessEngine pe = getProcessEngine();;
        try{
            BPMActivity currentActivity = process.getActivityById(activityId);
            String formApp = "";
            String form = "";
            String operationName = "";
            BPMSeeyonPolicy seeyonPolicy = currentActivity.getSeeyonPolicy();
            if(seeyonPolicy != null) {
                formApp = seeyonPolicy.getFormApp();
                form = seeyonPolicy.getForm();
                operationName = seeyonPolicy.getOperationName();
            }
            List downTransitions = currentActivity.getDownTransitions();
            BPMTransition nextTrans = (BPMTransition) downTransitions.get(0);

            BPMAbstractNode childNode = (BPMAbstractNode)nextTrans.getTo();
            boolean isJoin = (childNode instanceof BPMAndRouter) && !((BPMAndRouter) childNode).isStartAnd();

            BPMAndRouter split = null;
            BPMAndRouter join = null;
            //查看下一结点是否有split类型�jiedian节点
            for (int i = 0; i < downTransitions.size(); i++) {
                BPMTransition trans = (BPMTransition) downTransitions.get(i);
                if (trans.getTo() instanceof BPMAndRouter) {
                    BPMAndRouter to = (BPMAndRouter) trans.getTo();
                    if (to.isStartAnd()) {
                        split = to;
                    }
                }
            }

            //如果有split结点，遍历找到join结点
            if (split != null) {
                boolean foundJoin = false;
                BPMAbstractNode node = split;
                while (!foundJoin) {
                    BPMTransition trans = (BPMTransition) node.getDownTransitions().get(0);
                    node = trans.getTo();
                    if (node instanceof BPMAndRouter) {
                        BPMAndRouter andNode = (BPMAndRouter) node;
                        if (!andNode.isStartAnd()) {
                        	if(split.getParallelismNodeId().equals(andNode.getParallelismNodeId())){
                        		foundJoin = true;
                        		join = andNode;
                        	}
                        }
                    }
                }
            }

            //如果没有split结点，新建split和join
            if (split == null) {
                String splitId = UUIDLong.longUUID()+"";
                String joinId = UUIDLong.longUUID()+"";
                split = new BPMAndRouter(splitId, "split");
                join = new BPMAndRouter(joinId, "join");
                split.setStartAnd(true);
                join.setStartAnd(false);
                String relevancyId = UUIDLong.longUUID() + "";
                split.setParallelismNodeId(relevancyId);
                join.setParallelismNodeId(relevancyId);
                process.addChild(split);
                process.addChild(join);

                BPMAbstractNode nextNode = (BPMAbstractNode) ((BPMTransition) downTransitions.get(0)).getTo();
                //如果后面是结束结点或分支结点，split和join之间不设结点，join直接连到结束结点�
                if (!((nextNode instanceof BPMHumenActivity) || (nextNode instanceof BPMTimeActivity))) {
                    BPMTransition trans1 = new BPMTransition(currentActivity, split);
                    BPMTransition trans2 = new BPMTransition(join, nextNode);
                    process.addLink(trans1);
                    process.addLink(trans2);
                    copyCondition(nextTrans, trans2);
                    process.removeLink(nextTrans);
                    currentActivity.removeDownTransition(nextTrans);
                }
                //如果后面不是结束结点，将下一结点纳入split/join之中，join之后连接nextNode.nextNode
                else {
                    BPMTransition trans1 = new BPMTransition(currentActivity, split);
                    BPMTransition trans2 = new BPMTransition(join, nextNode);
                    process.addLink(trans1);
                    process.addLink(trans2);
                    copyCondition(nextTrans, trans2);
                    process.removeLink(nextTrans);
                    currentActivity.removeDownTransition(nextTrans);
                }
            }else{
            	String splitId = UUIDLong.longUUID()+"";
                String joinId = UUIDLong.longUUID()+"";
                split = new BPMAndRouter(splitId, "split");
                join = new BPMAndRouter(joinId, "join");
                split.setStartAnd(true);
                join.setStartAnd(false);
                String relevancyId = UUIDLong.longUUID() + "";
                split.setParallelismNodeId(relevancyId);
                join.setParallelismNodeId(relevancyId);

                process.addChild(split);
                process.addChild(join);

                BPMAbstractNode nextNode = (BPMAbstractNode) ((BPMTransition) downTransitions.get(0)).getTo();
                BPMTransition trans1 = new BPMTransition(currentActivity, split);
                BPMTransition trans2 = new BPMTransition(join, nextNode);

                process.addLink(trans1);
                process.addLink(trans2);
                copyCondition(nextTrans, trans2);
                process.removeLink(nextTrans);
                currentActivity.removeDownTransition(nextTrans);
            }

            //向split、join之间添加新结�点
            for (int i = 0; i < people.size(); i++) {
                Party party = people.get(i);

                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.name);
                BPMActor userActor = createActor(party);

                userNode.addActor(userActor);
                party.setActivityId(userNode.getId());
                if(party.getSeeyonPolicy() != null){
                	userNode.setSeeyonPolicy(party.getSeeyonPolicy());
                }
                else if(flowData.getSeeyonPolicy() != null){
                	userNode.setSeeyonPolicy(new BPMSeeyonPolicy(flowData.getSeeyonPolicy()));
                }
                else{
                	userNode.setSeeyonPolicy(new BPMSeeyonPolicy(policy));
                }

                userNode.getSeeyonPolicy().setFormApp(formApp);
                userNode.getSeeyonPolicy().setForm(form);
                userNode.getSeeyonPolicy().setOperationName(operationName);
                userNode.getSeeyonPolicy().setFR(isFormReadonly? "1" : "");

                if(!"user".equals(party.getType())){
                	BPMSeeyonPolicy _policy = userNode.getSeeyonPolicy();
                	_policy.setProcessMode("all");
                }

                BPMTransition userLink1 = new BPMTransition(split, userNode);
                BPMTransition userLink2 = new BPMTransition(userNode, join);

                process.addChild(userNode);
                process.addLink(userLink1);
                process.addLink(userLink2);
                activityList.add((BPMActivity)userNode);
            }

            Date now = new Date(System.currentTimeMillis());
            process.setUpdateDate(now);
            String isShowShortName = flowData.getIsShowShortName();
            if("false".equals(isShowShortName)){
            	if("false".equals(process.getIsShowShortName()))
            		process.setIsShowShortName(isShowShortName);
            }else{
            	process.setIsShowShortName(isShowShortName);
            }

            //将加入的人设为ready状�态
            BPMCase theCase = null;
            if(isPending){
            	ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
                pdm.saveOrUpdateProcessInReady(process);

            	theCase = pe.getCase(caseId);
	            ReadyObject readyObject = new ReadyObject();
	            readyObject.setActivityList(activityList);
	            readyObject.setCaseId(caseId+"");
	            readyObject.setProcessId(process.getId());
	            readyObject.setUserId(user.getId()+"");

	            boolean saveTheCaseFlag = false;
	            if(isJoin && theCase != null) {
	            	ReadyNode node = theCase.getReadyActivityById(childNode.getId());
	            	if(node != null) {
	            		node.setNum(node.getNum()+1);
	            		saveTheCaseFlag = true;
	            	}
	            }
	            readyObject.setSaveTheCaseFlag(saveTheCaseFlag);
	            pe.addReadyActivityMap(process.getId(), readyObject);
	            deleteReadyProcess(process.getId());
            }
        } catch (BPMException e) {
        	throw new ColException("增加节点并发操作异常", e);
        }
	}

	/**
	 * 公文督办删除未激活节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 */
	private static void delNoActivationNode(BPMProcess process, String activityId, FlowData flowData, String userId) throws ColException{
		try {
			BPMActivity currentActivity = process.getActivityById(activityId);
			List<BPMTransition> linksDown = currentActivity.getDownTransitions();
			List<BPMTransition> linksUp = currentActivity.getUpTransitions();
			BPMTransition upTran = (BPMTransition)linksUp.get(0);
			BPMTransition downTran = (BPMTransition)linksDown.get(0);
			BPMAbstractNode parentActivity = (BPMAbstractNode)upTran.getFrom();
			BPMAbstractNode childActivity = (BPMAbstractNode)downTran.getTo();

			if(childActivity.getBPMObjectType() == ObjectName.BPMEnd){
				process.removeLink(upTran);
				process.removeLink(downTran);
				process.removeChild(currentActivity);
				process.addLink(new BPMTransition(parentActivity, childActivity));
			}
			else{
				boolean parentIsSplit = (parentActivity instanceof BPMAndRouter) && ((BPMAndRouter) parentActivity).isStartAnd();
				boolean parentIsJoin = (parentActivity instanceof BPMAndRouter) && !((BPMAndRouter) parentActivity).isStartAnd();
				boolean childIsSplit = (childActivity instanceof BPMAndRouter) && ((BPMAndRouter) childActivity).isStartAnd();
				boolean childIsJoin = (childActivity instanceof BPMAndRouter) && !((BPMAndRouter) childActivity).isStartAnd();
				boolean parentIsHumen = parentActivity instanceof BPMHumenActivity;
				boolean childIsHumen = childActivity instanceof BPMHumenActivity;

				if((!parentIsSplit && !childIsJoin) || parentIsHumen || childIsHumen
						|| (parentIsSplit && childIsSplit) || (parentIsJoin && childIsJoin)){
					process.removeLink(upTran);
					process.removeLink(downTran);
					process.removeChild(currentActivity);
					BPMTransition newTran = new BPMTransition(parentActivity, childActivity);
					process.addLink(newTran);
				}
				else if(parentIsSplit && childIsJoin && parentActivity.getDownTransitions().size() == 2){
					BPMTransition _splitParentTran = (BPMTransition)parentActivity.getUpTransitions().get(0);
					BPMAbstractNode _parentActivity = (BPMAbstractNode)_splitParentTran.getFrom();
					BPMAbstractNode _splitChildActivity = null;
					List<BPMTransition> _splitChildTranList = parentActivity.getDownTransitions();
					for(int i=0; i<_splitChildTranList.size(); i++){
						BPMTransition tran = _splitChildTranList.get(i);
						if(!(tran.getId()).equals(upTran.getId())){
							_splitChildActivity = (BPMAbstractNode)tran.getTo();
							process.removeLink(tran);
						}
					}

					BPMTransition _joinChildTran = (BPMTransition)childActivity.getDownTransitions().get(0);
					BPMAbstractNode _joinchildActivity = (BPMAbstractNode)_joinChildTran.getTo();
					BPMAbstractNode _joinParentActivity = null;
					List<BPMTransition> _joinUpTranList = childActivity.getUpTransitions();
					for(int i=0; i<_joinUpTranList.size(); i++){
						BPMTransition tran = _joinUpTranList.get(i);
						if(!(tran.getId()).equals(downTran.getId())){
							_joinParentActivity = (BPMAbstractNode)tran.getFrom();
							process.removeLink(tran);
						}
					}

					process.removeLink(upTran);
					process.removeLink(downTran);
					process.removeChild(currentActivity);
					process.removeChild(parentActivity);
					process.removeChild(childActivity);
					process.removeLink(_splitParentTran);
					process.removeLink(_joinChildTran);

					process.addLink(new BPMTransition(_parentActivity, _splitChildActivity));
					process.addLink(new BPMTransition(_joinParentActivity, _joinchildActivity));
				}
				else {
					if(parentIsSplit && childIsJoin){
						boolean needRepair= true;//是否需要做数据兼容修复
						List<BPMTransition> downList= parentActivity.getDownTransitions();
						for (BPMTransition bpmTransition : downList) {
							BPMAbstractNode splitToNode= bpmTransition.getTo();
							String isDelete= splitToNode.getSeeyonPolicy().getIsDelete();
							if( !splitToNode.getId().equals(currentActivity.getId()) && "false".equals(isDelete)){
								needRepair= false;
							}
						}
						log.info("needRepair:= "+needRepair);
						if(needRepair){
							for (BPMTransition bpmTransition : downList) {
								BPMAbstractNode splitToNode= bpmTransition.getTo();
								String isDelete= splitToNode.getSeeyonPolicy().getIsDelete();
								if( !splitToNode.getId().equals(currentActivity.getId()) && "true".equals(isDelete)){
									splitToNode.getSeeyonPolicy().setIsDelete("false");
								}
							}
						}
					}
					process.removeLink(upTran);
					process.removeLink(downTran);
					process.removeChild(currentActivity);
				}
			}

			Date now = new Date(System.currentTimeMillis());
			process.setUpdateDate(now);
			ProcessEngine pe = getProcessEngine();
			process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);

	        ReadyObject orginalReadyObject = pe.getReadyAddedMap().get(new Long(process.getId()));
	        if(orginalReadyObject != null){
	        	List<BPMActivity> readyActivityList = orginalReadyObject.getActivityList();
				for (int i = 0; i < readyActivityList.size(); i++) {
					BPMActivity node = readyActivityList.get(i);
		            if (node.getId().equals(activityId)) {
						readyActivityList.remove(i);
						break;
					}
				}
	        }

			//pe.replaceProcessByProcessId(null, process);
		} catch (Exception ex) {
			throw new ColException("删除节点操作异常", ex);
		}
	}

	/**
	 * 公文督办删除待办节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 */
	@SuppressWarnings("unchecked")
	private static void delNoActivationNode1(BPMProcess process, String activityId, FlowData flowData,
			String userId, Long caseId) throws ColException{
		ProcessEngine pe = getProcessEngine();
		try {
			BPMActivity currentActivity = process.getActivityById(activityId);
			List<BPMTransition> linksDown = currentActivity.getDownTransitions();
			List<BPMTransition> linksUp = currentActivity.getUpTransitions();
			BPMTransition upTran = (BPMTransition)linksUp.get(0);
			BPMTransition downTran = (BPMTransition)linksDown.get(0);
			BPMAbstractNode parentActivity = (BPMAbstractNode)upTran.getFrom();
			BPMAbstractNode childActivity = (BPMAbstractNode)downTran.getTo();

			//将加入的人设为ready状�态
			List<BPMActivity> readyActivityList = new ArrayList<BPMActivity>();
			if(childActivity.getBPMObjectType() != ObjectName.BPMEnd &&
					((childActivity instanceof BPMAndRouter) && ((BPMAndRouter) childActivity).isStartAnd()||
							childActivity instanceof BPMHumenActivity)){
				List<BPMTransition> _linksDown = null;
				if((childActivity instanceof BPMAndRouter) && ((BPMAndRouter) childActivity).isStartAnd()){
					_linksDown = childActivity.getDownTransitions();
				}else{
					_linksDown = linksDown;
				}
				for(BPMTransition tran : _linksDown){
					readyActivityList.add((BPMActivity)tran.getTo());
				}

	            ReadyObject readyObject = new ReadyObject();
	            readyObject.setActivityList(readyActivityList);
	            readyObject.setCaseId(caseId+"");
	            readyObject.setProcessId(process.getId());
	            readyObject.setUserId(userId);
	            readyObject.setSaveTheCaseFlag(false);
	            pe.addReadyActivityMap(process.getId(), readyObject);
			}


			if(childActivity.getBPMObjectType() == ObjectName.BPMEnd){
				//pe.deleteActivity(userId, process, theCase, currentActivity, true);
				setPreDelActivity(pe, process, caseId, currentActivity, userId, true);
				process.removeLink(upTran);
				process.removeLink(downTran);
				process.removeChild(currentActivity);
				BPMTransition newTran = new BPMTransition(parentActivity, childActivity);
				process.addLink(newTran);
			}
			else{
				boolean parentIsSplit = (parentActivity instanceof BPMAndRouter) && ((BPMAndRouter) parentActivity).isStartAnd();
				boolean parentIsJoin = (parentActivity instanceof BPMAndRouter) && !((BPMAndRouter) parentActivity).isStartAnd();
				boolean childIsSplit = (childActivity instanceof BPMAndRouter) && ((BPMAndRouter) childActivity).isStartAnd();
				boolean childIsJoin = (childActivity instanceof BPMAndRouter) && !((BPMAndRouter) childActivity).isStartAnd();
				boolean parentIsHumen = parentActivity instanceof BPMHumenActivity;
				boolean childIsHumen = childActivity instanceof BPMHumenActivity;

				if((!parentIsSplit && !childIsJoin) || parentIsHumen || childIsHumen
						|| (parentIsSplit && childIsSplit) || (parentIsJoin && childIsJoin)){
					//pe.deleteActivity(userId, process, theCase, currentActivity, true);
					setPreDelActivity(pe, process, caseId, currentActivity, userId, false);
					process.removeLink(upTran);
					process.removeLink(downTran);
					process.removeChild(currentActivity);
					BPMTransition newTran = new BPMTransition(parentActivity, childActivity);
					process.addLink(newTran);
				}else if(parentIsSplit && childIsJoin && parentActivity.getDownTransitions().size() == 2){
					//pe.deleteActivity(userId, process, theCase, currentActivity, true);
					setPreDelActivity(pe, process, caseId, currentActivity, userId, false);
					BPMTransition _splitParentTran = (BPMTransition)parentActivity.getUpTransitions().get(0);
					BPMAbstractNode _parentAbstractNode = (BPMAbstractNode)_splitParentTran.getFrom();

					BPMTransition _joinChildTran = (BPMTransition)childActivity.getDownTransitions().get(0);
					BPMAbstractNode _joinchildActivity = (BPMAbstractNode)_joinChildTran.getTo();

					List<BPMTransition> _splitChildTranList = parentActivity.getDownTransitions();

					BPMAbstractNode siblingActivity = null;

					for(int i=0; i<_splitChildTranList.size(); i++){
						BPMTransition tran = _splitChildTranList.get(i);
						if(!(tran.getId()).equals(upTran.getId())){
							siblingActivity = tran.getTo();
						}
					}

					//兄弟节点也要删除，比如：没有选中的分支
					boolean delSiblingActivity = "true".equalsIgnoreCase(siblingActivity.getSeeyonPolicy().getIsDelete());

					//删除兄弟节点上面的线
					process.removeLink((BPMTransition)siblingActivity.getUpTransitions().get(0));

					//删除兄弟节点，并把split前的节点和join后的节点连接起来
					if(delSiblingActivity){
						process.removeChild(siblingActivity);

						// 删除兄弟节点下面的线, 直到join节点
						deleteFromNodeToAnotherNode(process, siblingActivity, childActivity);

						BPMAbstractNode childActivityChild = ((BPMTransition)childActivity.getDownTransitions().get(0)).getTo();

						setPreDelActivity(pe, process, caseId, (BPMActivity)childActivity, userId, childActivityChild.getBPMObjectType() == ObjectName.BPMEnd);

						process.addLink(new BPMTransition(_parentAbstractNode, _joinchildActivity));
						//激活下一节点
						List<BPMActivity> readyActivityList1 = new ArrayList<BPMActivity>();
						if((_joinchildActivity instanceof BPMAndRouter) && ((BPMAndRouter) _joinchildActivity).isStartAnd()){
							List<BPMTransition> _linksDown1 = _joinchildActivity.getDownTransitions();

							for(BPMTransition tran : _linksDown1){
								readyActivityList1.add((BPMActivity)tran.getTo());
							}
						}
						else if(_joinchildActivity instanceof BPMHumenActivity){
							readyActivityList1.add((BPMActivity)_joinchildActivity);
						}

			            ReadyObject readyObject = new ReadyObject();
			            readyObject.setActivityList(readyActivityList1);
			            readyObject.setCaseId(caseId+"");
			            readyObject.setProcessId(process.getId());
			            readyObject.setUserId(userId);
			            readyObject.setSaveTheCaseFlag(false);
			            pe.addReadyActivityMap(process.getId(), readyObject);
					}
					else{
						//兄弟分支的最末一个节点
						BPMAbstractNode siblingActivityEndNode = null;
						List<BPMTransition> _childActivityUpTransition = childActivity.getUpTransitions();
						for (BPMTransition transition : _childActivityUpTransition) {
							if(!transition.equals(downTran)){
								siblingActivityEndNode = transition.getFrom();
								break;
							}
						}

						List<BPMTransition> siblingActivityEndNodeDowns = siblingActivityEndNode.getDownTransitions();
						process.removeLink((BPMTransition)siblingActivityEndNodeDowns.get(0));

						// 把split前的节点和兄弟节点连接
						// 把join后的节点和兄弟分支的最末一个节点连接
						process.addLink(new BPMTransition(_parentAbstractNode, siblingActivity));
						process.addLink(new BPMTransition(siblingActivityEndNode, _joinchildActivity));
					}

					process.removeLink(upTran);
					process.removeLink(downTran);
					process.removeChild(currentActivity);
					process.removeChild(parentActivity);
					process.removeChild(childActivity);
					process.removeLink(_splitParentTran);
					process.removeLink(_joinChildTran);

				}else{
					//pe.deleteActivity(userId, process, theCase, currentActivity, true);
					setPreDelActivity(pe, process, caseId, currentActivity, userId, false);
					process.removeLink(upTran);
					process.removeLink(downTran);
					process.removeChild(currentActivity);
				}

				// 更新join的num计数
				if(childIsJoin)
				{
					BPMCase theCase = pe.getCase(caseId);
					if(theCase!=null)
					{
			        	ReadyNode node = theCase.getReadyActivityById(childActivity.getId());
			        	if(node != null) {
			        		node.setNum(node.getNum()-1);
			        	}
			        	pe.getCaseManager().save(theCase);
					}
				}
			}

			Date now = new Date(System.currentTimeMillis());
			process.setUpdateDate(now);
			process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
			//pe.replaceProcessByProcessId(null, process);

		} catch (Exception ex) {
			throw new ColException("删除节点操作异常", ex);
		}
	}

	@SuppressWarnings("unchecked")
	private static void deleteFromNodeToAnotherNode(BPMProcess process, BPMAbstractNode fromNode, BPMAbstractNode toNode){
		List<BPMTransition> downs = fromNode.getDownTransitions();
		for (int i = 0; i < downs.size(); i++) {
			BPMTransition down = downs.get(i);
			process.removeLink(down);

			if(!down.getTo().equals(toNode)){
				deleteFromNodeToAnotherNode(process, down.getTo(), toNode);
			}
		}
	}

	/**
	 * 公文督办替换未激活节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 */
	private static void replaceActivity(BPMProcess process, String activityId, FlowData flowData, String userId) throws ColException{
		List<Party> people =  flowData.getPeople();
		try{
			BPMActivity currentActivity = process.getActivityById(activityId);
			List<BPMActor> actorList = new ArrayList<BPMActor>();
			BPMActor userActor = null;
			for (int i = 0; i < people.size(); i++) {
                Party party = people.get(i);
                userActor = createActor(party);
                userActor.getParty().getName();
                actorList.add(userActor);
                if(!"user".equals(party.getType())){
                	currentActivity.getSeeyonPolicy().setProcessMode("all");
                }
            }
			currentActivity.setActorList(actorList);
			currentActivity.setName(userActor.getParty().getName());

			Date now = new Date(System.currentTimeMillis());
	        process.setUpdateDate(now);
	        ProcessEngine pe = getProcessEngine();
	        process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
	        //pe.replaceProcessByProcessId(null, process);
		}catch(Exception e){
			log.error("", e);
			throw new ColException("替换节点操作异常", e);
		}
	}

	/**
	 * 公文督办替换待办节点
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 */
	private static void replaceActivity1(BPMProcess process, String activityId, FlowData flowData,
			String userId, Long caseId) throws ColException{
		List<BPMActivity> activityList = new ArrayList<BPMActivity>();
		ProcessEngine pe = getProcessEngine();
		List<Party> people = flowData.getPeople();
		User user = CurrentUser.get();
		try {
			BPMActivity currentActivity = process.getActivityById(activityId);
			//pe.deleteActivity(user.getId() + "", process, theCase, currentActivity, false);
			setPreDelActivity(pe, process, caseId, currentActivity, userId, false);
			List<BPMActor> actorList = new ArrayList<BPMActor>();
			BPMActor userActor = null;
			for (int i = 0; i < people.size(); i++) {
				Party party = people.get(i);
				userActor = createActor(party);
				userActor.getParty().getName();
				actorList.add(userActor);
				if(!"user".equals(party.getType())){
                	currentActivity.getSeeyonPolicy().setProcessMode("all");
                }
			}
			currentActivity.setActorList(actorList);
			currentActivity.setName(userActor.getParty().getName());
			activityList.add(currentActivity);
			Date now = new Date(System.currentTimeMillis());
			process.setUpdateDate(now);

			ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
			pdm.saveOrUpdateProcessInReady(process);

            ReadyObject readyObject = new ReadyObject();
            readyObject.setActivityList(activityList);
            readyObject.setCaseId(caseId+"");
            readyObject.setProcessId(process.getId());
            readyObject.setUserId(user.getId()+"");
            readyObject.setSaveTheCaseFlag(false);
            pe.addReadyActivityMap(process.getId(), readyObject);

            deleteReadyProcess(process.getId());

            process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);

		}catch(Exception e){
			log.error("", e);
			throw new ColException("替换节点操作异常", e);
		}
	}
	/**
	 * 公文督办设置待办节点属性
	 * @param process 更新流程对象
	 * @param activityId 更新节点Id
	 * @param flowData
	 */
	private static void setActivityPolicy(BPMProcess process, String activityId, FlowData flowData, BPMSeeyonPolicy policy, String userId, boolean isForm) throws ColException{
		ProcessEngine pe = getProcessEngine();
		try {
			BPMActivity currentActivity = process.getActivityById(activityId);
			//获取原seeyonpolicy中的表单信息
			BPMSeeyonPolicy oldPolicy = currentActivity.getSeeyonPolicy();
			policy.setFormApp(oldPolicy.getFormApp());
			if(!isForm){
				policy.setForm(oldPolicy.getForm());
				policy.setOperationName(oldPolicy.getOperationName());
			}
			policy.setNF(oldPolicy.getNF());
			currentActivity.setSeeyonPolicy(policy);
			currentActivity.setDesc(policy.getDesc());
			Date now = new Date(System.currentTimeMillis());
	        process.setUpdateDate(now);
	        process.setModifyUser(userId);
	        pe.updateModifyingProcess(process);
	        //pe.replaceProcessByProcessId(null, process);
		}catch(Exception e){
			log.error("", e);
			throw new ColException("替换节点操作异常", e);
		}
	}

	public static String isModifyProcess(String processId, String userId, OrgManager orgManager) throws ColException {
		String modifyUserId = null;
    	ProcessEngine pe = getProcessEngine();
		try {
			modifyUserId = pe.checkModifyingProcess(processId, userId, orgManager);
		} catch (BPMException e) {
			log.error("", e);
		}
		return modifyUserId;
	}

	public static String getModifyingProcessXML(String userId, String processId) throws ColException {
        try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            return trimXMLProcessor(engine.getModifyingProcessXML(userId, processId));
        } catch (BPMException e) {
        	throw new ColException("获取流程ProcessXML异常 [caseId = " +processId+ "]", e);
        }
    }

	public static void delModifyProcess(String processId, String summaryId, String userId) throws ColException {
    	ProcessEngine pe = getProcessEngine();
		try {
			pe.delModifyingProcess(processId, userId);
			pe.getReadyObject(processId, userId);
			List<MessageData> messageDataList = messageDataMap.get(Long.parseLong(summaryId));
			Long theSummaryId = Long.parseLong(summaryId);
			if(messageDataList != null){
				for(MessageData messageData : messageDataList){
					if(Long.parseLong(userId) == messageData.handlerId){
						messageDataMap.remove(theSummaryId);
						//CLUSTER 集群-更新备机
						NotificationManager.getInstance().send(NotificationType.Collaboration_UpdateMessageData_Remove, theSummaryId);
						break;
					}
				}
			}
			//流程日志
			List<ProcessLog> processLog = processLogMetaMap.get(Long.parseLong(summaryId));
			if(processLog != null){
				for(ProcessLog pLog : processLog){
					if(pLog.getActionUserId().toString().equals(userId)){
						processLogMetaMap.remove(theSummaryId);
						//CLUSTER 集群-更新备机
						NotificationManager.getInstance().send(NotificationType.Collaboration_UpdateProcessLog_Remove, theSummaryId);
						break;
					}
				}
			}
		} catch (BPMException e) {
			log.error("", e);
		}
	}

	public static BPMProcess saveModifyingProcess(String processId, String userId) throws ColException {
		BPMProcess process = null;
		ProcessEngine pe = getProcessEngine();
		try {
			process = pe.saveModifyingProcess(processId, userId);
		} catch (BPMException e) {
			log.error("", e);
		}
		return process;
	}

	public static BPMProcess getModifyingProcess(String processId, String userId) throws ColException {
		BPMProcess process = null;
		ProcessEngine pe = getProcessEngine();
		try {
			process = pe.getModifyingProcess(processId, userId);
		} catch (BPMException e) {
			log.error("", e);
		}
		return process;
	}

	public static void updateProcessLock(String processId, String userId) throws ColException {
		BPMProcess process = null;
		ProcessEngine pe = getProcessEngine();
		try {
			process = pe.getModifyingProcess(processId, userId);
			if(process == null){
				BPMProcess processFromData = pe.getProcessRunningById(processId);
				process = BPMProcess.fromXML(processFromData.toXML());
			}
		}
		catch (BPMException e) {
			log.error("", e);
		}
		try {
			process.setModifyUser(userId);
			pe.updateModifyingProcess(process);
		} catch (BPMException e) {
			log.error("", e);
		}
	}
	
	public static void updateModifyingProcessLock(Long processId, Long userId) throws ColException {
		ProcessEngine pe = getProcessEngine();
		try {
			pe.updateModifyingProcessLock(processId, userId);
		} catch (BPMException e) {
			log.error("", e);
		}
	}

	public static String[] getNewProcessXML(Long caseId, String userId) throws ColException {
		String caseLogXML = getCaseLogXML(userId, caseId);
        String caseWorkItemLogXML = getCaseWorkItemLogXML(userId, caseId);
        String caseProcessXML = getCaseProcessXML(userId, caseId);
        return new String[]{caseProcessXML, caseLogXML, caseWorkItemLogXML};
	}

	public static void saveAcitivityModify(BPMProcess process, String userId)throws ColException{
		ProcessEngine pe = getProcessEngine();
		try {
			ReadyObject readyObject = pe.getReadyObject(process.getId(), userId);
			if(readyObject != null){
				long caseId = Long.parseLong(readyObject.getCaseId());
				BPMCase theCase = pe.getCase(caseId);

				Boolean hasPendingObject = false;
				List<BPMActivity> activityList = readyObject.getActivityList();
				if(activityList != null && !activityList.isEmpty())
					hasPendingObject = true;

				List<BPMActivity> preDelActivityList = readyObject.getPreDelActivityList();
				if(preDelActivityList != null && !preDelActivityList.isEmpty()){
					for(BPMActivity preDelActivity : preDelActivityList){
						if(hasPendingObject)
							preDelActivity.setFrontadFlag(false);

						pe.deleteActivity(userId, process, theCase, preDelActivity, preDelActivity.isFrontadFlag());
					}
				}

				if(hasPendingObject){
					pe.addReadyActivity(userId, process, theCase, activityList);
					if(readyObject.isSaveTheCaseFlag()){
						pe.getCaseManager().save(theCase);
					}
				}
			}
			ColHelper.updateRunningProcess(process);
		} catch (NumberFormatException e) {
			log.error("", e);
		} catch (BPMException e) {
			log.error("", e);
		}
	}

	public static String getErrorMessage(BusinessException e,ApplicationCategoryEnum AppEnum){
		String message = "";
    	String col_resource_baseName = "com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";
    	String edoc_resource_baseName = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
    	if(AppEnum.key() == ApplicationCategoryEnum.collaboration.key()){
    		message = ResourceBundleUtil.getString(col_resource_baseName, "col.lock." + e.getErrorCode(), e.getErrorArgs()[0].toString());
    	}else{
    		message = ResourceBundleUtil.getString(edoc_resource_baseName, "edoc.lock." + e.getErrorCode(), e.getErrorArgs()[0].toString());
    	}
    	return message;
	}

	/**
	 * 协同操作同步锁
	 *
	 * @author jincm 2008-3-30
	 * @param summaryId
	 * @param memberId
	 * @param memberName
	 * @param currentAction
	 * @param response
	 * @return boolean
	 */
	public static boolean colOperationLock(Long summaryId, Long memberId, String memberName,
    		ColLock.COL_ACTION currentAction, HttpServletResponse response, ApplicationCategoryEnum AppEnum){
    	ColLock colLock = ColLock.getInstance();
        try{
        	colLock.checkCanAction(summaryId, memberId, memberName,  currentAction);
        }catch(BusinessException e){
        	String message = getErrorMessage(e, AppEnum);
        	PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {
				log.error("", e1);
			}
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(message) + "\")");
        	out.println("if(window.dialogArguments){"); //弹出
        	out.println("  window.returnValue = \"true\";");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println("  top.reFlesh();");
        	out.println("}");
        	out.println("</script>");
        	out.close();
        	return false;
        }
        return true;
    }

	/**
	 * 时间拼装
	 *
	 * @author jincm 2008-3-30
	 * @param day
	 * @param hour
	 * @param minute
	 * @param currentAction
	 * @param second
	 * @param filterFlag //过滤秒的标记
	 * @return boolean
	 */
	public static String timePatchwork(long day, long hour, long minute, long second, boolean filterFlag){
		String timeStr = "";

		if(second != 0){
			timeStr = second + Constant.getCommonString("common.time.second");
		}
		if(minute != 0){
			timeStr = minute + Constant.getCommonString("common.time.minute");
			if(!filterFlag)
				timeStr += second + Constant.getCommonString("common.time.second");
		}
		if (hour != 0){
			timeStr = hour + Constant.getCommonString("common.time.hour")
			+ minute + Constant.getCommonString("common.time.minute");
			if(!filterFlag)
				timeStr += second + Constant.getCommonString("common.time.second");
		}
		if (day != 0){
			timeStr = day + Constant.getCommonString("common.time.day") + hour + Constant.getCommonString("common.time.hour")
			+ minute + Constant.getCommonString("common.time.minute");
			if(!filterFlag)
				timeStr += second + Constant.getCommonString("common.time.second");
		}
		return timeStr;
    }

	/**
	 * 导出excel
	 * @param request
	 * @param response
	 * @param fileToExcelManager
	 * @param fileName             导出文件名
	 * @param data                 数据集，object[]的长度必须和columnName的长度相同，并一一对应
	 * @param columnName           列头名
	 * @param title                标题
	 * @param sheetName            表格名称
	 */
	public static void exportToExcel(HttpServletRequest request,HttpServletResponse response,
			FileToExcelManager fileToExcelManager,String fileName,List<Object[]> data
    		, String[] columnName,String title,String sheetName) {
    	DataRecord record = new DataRecord();
    	DataRow row = null;
    	DataRow[] rows = null;
    	try {
    		if(data != null && !data.isEmpty()) {
    			rows = new DataRow[data.size()];
    	    	int i = 0;
    	    	for(Object[] obj:data) {
    	    		row = new DataRow();
    	    		for(Object cell:obj) {
    	    			row.addDataCell(cell==null?"":cell.toString(), DataCell.DATA_TYPE_TEXT);
    	    		}
    	    		rows[i] = row;
    	    		i++;
    	    	}
    	    	record.addDataRow(rows);
        	}
    		record.setColumnName(columnName);
        	if(title != null)
        		record.setTitle(title);
        	if(sheetName != null)
        		record.setSheetName(sheetName);
        	fileToExcelManager.save(request, response, fileName, record);
    	}catch(Exception e) {
    		log.error(e);
    	}
    }

	public static String getI18NString(String resource,String key) {
		return ResourceBundleUtil.getString(resource, key);
	}

	public static void setPreDelActivity(ProcessEngine pe, BPMProcess process,
			Long caseId, BPMActivity activity, String userId, boolean frontadFlag) throws BPMException {
		ReadyObject orginalReadyObject = pe.getReadyAddedMap().get(new Long(process.getId()));
		if(orginalReadyObject != null){
			List<BPMActivity> reDelActivitys = orginalReadyObject.getPreDelActivityList();
			if(reDelActivitys != null){
				reDelActivitys.remove(activity);
			}

			List<BPMActivity> activitys = orginalReadyObject.getActivityList();
			if(activitys != null){
				activitys.remove(activity);
			}
		}

		List<BPMActivity> list = new ArrayList<BPMActivity>();
		if(!frontadFlag) activity.setFrontadFlag(frontadFlag);
		list.add(activity);
        ReadyObject readyObject = new ReadyObject();
        readyObject.setPreDelActivityList(list);
        readyObject.setCaseId(caseId+"");
        readyObject.setProcessId(process.getId());
        readyObject.setUserId(userId);
        readyObject.setSaveTheCaseFlag(false);
        pe.addReadyActivityMap(process.getId(), readyObject);

	}

	/**
	 * getAtomicityRegExpCell()
	 * 获得最小单元的正则表达式字符串
	 * @param regExpCells 正则表达式列表
	 * @param condition 目标字符串
	 * @author wangchw
	 * @return
	 */
	public  static String  getAtomicityRegExpCell(Vector<String> regExpCells,String condition){
		int startIndex=-1;
		int endIndex= 0;
		String lasetRegExp= "";
		int j= -1;
		for(int i=0;i< regExpCells.size();i++){
			String regExp = regExpCells.get(i);
			Pattern p = Pattern.compile(regExp);
			Matcher m = p.matcher(condition);
			if(m.find()){
				String group = m.group();
//				if(log.isInfoEnabled()){
//					log.info("group:="+group+";start:="+m.start()+";end:="+m.end());
//				}
				if(startIndex==-1 && endIndex== 0){
					startIndex= m.start();
					endIndex= m.end();
					lasetRegExp= regExp;
					j= i;
				}else{
					if(m.start()> startIndex && m.end()< endIndex){
						startIndex= m.start();
						endIndex= m.end();
						lasetRegExp= regExp;
						j= i;
					}
					else if(
					            (
					                !regExp.equals("(startlevel)")
					                &&
					                !regExp.equals("(startaccount)")
					                &&
					                !regExp.equals("("+V3xOrgEntity.ORGENT_TYPE_ACCOUNT+")")
					                &&
					                !regExp.equals("("+V3xOrgEntity.ORGENT_TYPE_LEVEL+")")
					            )
					            &&
					            (
					                regExp.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1
					                ||
					                regExp.indexOf("startaccount")!=-1
					                ||
					                regExp.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1
					                ||
					                regExp.indexOf(V3xOrgEntity.ORGENT_TYPE_LEVEL)!=-1
					                ||
					                regExp.indexOf("startlevel")!=-1
					                ||
					                regExp.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL)!=-1
					                ||
					                regExp.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT)!=-1
					                ||
					                regExp.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_LEVEL)!=-1
					                ||
					                regExp.indexOf(STARTMEMBERLOGINACCOUNT)!=-1
					                ||
					                regExp.indexOf(STARTMEMBERLOGINACCOUNTLEVEL)!=-1
					                ||
					                regExp.indexOf(START_STARTMEMBERLOGINACCOUNTLEVEL)!=-1
					            )
					        ){
						startIndex= m.start();
						endIndex= m.end();
						lasetRegExp= regExp;
						j= i;
					}
				}
			}
		}
		if( j!=-1 ){//找到符合要求的正则表达式,则从正则表达式列表中删除，以免出现循环匹配
			regExpCells.remove(j);
		}
//		if(log.isInfoEnabled()){
//			log.info("atomicityRegExpCell["+j+"]:="+lasetRegExp);
//		}
		return lasetRegExp;
	}
	/**
	 * preParseCondition
	 * 分支条件预处理方法(有流程表单—支持流程分支跨表判定)
	 * @param conditionMap 条件分支集合
	 * @param formAppId 表单ID
	 * @param masterId 表单记录主键值
	 * @param formData 当前表单页面最新数据
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object> preParseCondition(Map<String,Object> conditionMap,Long formAppId,Long masterId,String formData) throws ColException{
		try {
			String regExp="(\\{[a-zA-Z]\\.\\S+\\})";
			String existRegExp= "exist\\([^\\|\\ | \\&\\& | ^\\']*\\)";
			String existFormFieldRegExp="(\\{[b-zA-Z]\\.\\S+\\})";//除本表a外
			if(conditionMap == null)
				return null;
			List<String> conditions = (List<String>)conditionMap.get("conditions");
			if(conditions==null || conditions.size()==0){
				return null;
			}
			if(null==formAppId || formAppId.longValue()==-1){//无跨表情况，不需进行下面的处理
				return conditionMap;
			}
			String group = null;
			String key;
			Map<String,Long> formAppIdMap = FormHelper.getFormAppIdMap(null,formAppId);
			Map<String,String> tableFieldsInputTypeMap= FormHelper.getAllTableFieldsFieldType(formAppIdMap);
			Map<String,Set<String>> branchTableFieldsMap= new HashMap<String, Set<String>>();
			for(int i=0;i<conditions.size();i++) {
				String condition= conditions.get(i);
				Pattern p = Pattern.compile(regExp);
				Matcher m = p.matcher(condition);
				while(m.find()) {
					group = m.group();
					if(log.isInfoEnabled()){
						log.info("group:="+group);
					}
					if(!group.startsWith("{a.") && group.endsWith("}")) {//跨表数据字段
						key = group.substring(3,group.length()-1);
						String shortName= group.substring(1, 2);
						Set<String> temp= branchTableFieldsMap.get(shortName);
						if(null==temp){
							temp= new HashSet<String>();
						}
						temp.add(key);
						branchTableFieldsMap.put(shortName, temp);
					}
				}
			}
			Map<String, List<String>> tableFieldsDataMap= new HashMap<String, List<String>>();
//			try{
//				tableFieldsDataMap= 
//					FormHelper.getAllFieldDataMap(null,formAppId, masterId, formData, branchTableFieldsMap, formAppIdMap);
//			}catch(Throwable e){
//				log.error("从表单中获取字段值集合出现异常，请查找原因。", e);
//			}
			boolean hasExistValue= true;
			for(int i=0;i<conditions.size();i++) {
				String condition= conditions.get(i);
				Pattern p = Pattern.compile(existRegExp);
				Matcher m = p.matcher(condition);
				StringBuffer sb = new StringBuffer();
				while( m.find() & hasExistValue) {
					group = m.group();
					if(log.isInfoEnabled()){
						log.info("group:="+group);
					}
					//找出exist函数中的表单字段
					Pattern p1 = Pattern.compile(existFormFieldRegExp);
					Matcher m1 = p1.matcher(group);
					String group1= null;
					String key1= "";
					while( m1.find()){
						group1= m1.group();
						if(log.isInfoEnabled()){
							log.info("group1:="+group1);
						}
						if(!group1.startsWith("{a.") && group1.endsWith("}")){
							key1 = group1.substring(1,group1.length()-1);
							List<String> values= tableFieldsDataMap.get(key1);
							String type= tableFieldsInputTypeMap.get(key1);
							if(null!= values){
								boolean value= caculateExistFunction(group,values,type);
								m.appendReplacement(sb, String.valueOf(value));
							}else{//弹出页面提示客户跨表单数据无数据，分支条件默认都不成功
//								hasExistValue= false;
//								break;
								values= new ArrayList<String>();
								if(IPagePublicParam.DECIMAL.equals(type)){//数字类型，还需要知道数字类型的格式
									values.add("0");
								}else{//非数字类型
									values.add("");
								}
								boolean value= caculateExistFunction(group,values,type);
								m.appendReplacement(sb, String.valueOf(value));
							}
						}
					}
				}
				if(hasExistValue){
					m.appendTail(sb);
					if(log.isInfoEnabled()){
						log.info("sb1:="+sb);
					}
					condition= sb.toString();
					conditions.set(i, condition);
				}else{
					break;
				}
			}
			boolean hasValue= true;
			if(hasExistValue){
				for(int i=0;i<conditions.size();i++) {
					String condition= conditions.get(i);
					Pattern p = Pattern.compile(regExp);
					Matcher m = p.matcher(condition);
					StringBuffer sb = new StringBuffer();
					while( m.find() & hasValue) {
						group = m.group();
						if(log.isInfoEnabled()){
							log.info("group:="+group);
						}
						//表单数据
						if(group.startsWith("{a.") && group.endsWith("}")) {//本表数据字段
							key = group.substring(3,group.length()-1);
							if(log.isInfoEnabled()){
								log.info("key:="+key);
							}
							m.appendReplacement(sb, "{"+key+"}");
						}else if(!group.startsWith("{a.") && group.endsWith("}")) {//跨表数据字段
							key = group.substring(1,group.length()-1);
							List<String> values= tableFieldsDataMap.get(key);
							String type= tableFieldsInputTypeMap.get(key);
							if(log.isInfoEnabled()){
								log.info("key:="+key);
								log.info("values:="+values);
								log.info("type:="+type);
							}
							String srcValue= null;
							String desValue= null;
							if(null!= values){
								srcValue= values.get(0);
								if(log.isInfoEnabled()){
									log.info("srcValue:="+srcValue);
								}
								if(IPagePublicParam.DECIMAL.equals(type)){//数字类型，还需要知道数字类型的格式
									if(null!=srcValue){
										if("".equals(srcValue.trim())){
											desValue= "0";
										}else{
											desValue= srcValue;
										}
									}else{
										desValue= "0";
									}
								}else{//非数字类型
									desValue= "\""+srcValue+"\"";
								}
								m.appendReplacement(sb, desValue);
							}else{//弹出页面提示客户跨表单数据无数据，分支条件默认都不成功
//								hasValue= false;
//								break;
								if(IPagePublicParam.DECIMAL.equals(type)){//数字类型，还需要知道数字类型的格式
									desValue= "0";
								}else{//非数字类型
									desValue= "\"\"";
								}
								m.appendReplacement(sb, desValue);
							}
						}
					}
					if(hasValue){
						m.appendTail(sb);
						if(log.isInfoEnabled()){
							log.info("sb1:="+sb);
						}
						condition= sb.toString();
						conditions.set(i, condition);
					}else{
						break;
					}
				}
			}
			if(!hasValue || !hasExistValue){//跨表单中分支条件字段没有数据，则所有分支都置成false，不允许提交流程。
				for(int i=0;i<conditions.size();i++) {
					conditions.set(i, "false");
				}
			}
		} catch (Exception e) {
			throw new ColException(e.getMessage(),e);
		}
		return conditionMap;
	}

	/**
	 * 在后台计算跨表的exist函数值
	 * @param group1
	 * @param values
	 * @param type
	 * @return
	 */
	private static boolean caculateExistFunction(String group,
			List<String> values, String type) {
		String[] expressElements= group.split(",");
		boolean result= false;
		if(null!= expressElements && expressElements.length== 3){
			String operator= expressElements[1];
			String value= expressElements[2].substring(0, expressElements[2].length()-1);
			if(value.indexOf(":")!=-1){
				value = value.substring(value.indexOf(":")+1);
			}
			for (Iterator iterator = values.iterator(); iterator.hasNext();) {
				String aValue = (String) iterator.next();
				if(IPagePublicParam.DECIMAL.equals(type)){//数字类型，还需要知道数字类型的格式
					if(null!=aValue){
						if("".equals(aValue.trim())){
							aValue= "0";
						}
					}else{
						aValue= "0";
					}
					float realDesValue= Float.parseFloat(value);
					float realSrcValue= Float.parseFloat(aValue);
					if(">".equals(operator)){
						result= (realSrcValue > realDesValue);
					}else if(">=".equals(operator)){
						result= (realSrcValue >= realDesValue);
					}else if("<".equals(operator)){
						result= (realSrcValue < realDesValue);
					}else if("<=".equals(operator)){
						result= (realSrcValue <= realDesValue);
					}else if("=".equals(operator)){
						result= (realSrcValue == realDesValue);
					}else if("==".equals(operator)){
						result= (realSrcValue == realDesValue);
					}else if("<>".equals(operator)){
						result= (realSrcValue != realDesValue);
					}
				}else{//字符串类型
					if("=".equals(operator)){
						result= aValue.equals(value);
					}else if("==".equals(operator)){
						result= aValue.equals(value);
					}else if("<>".equals(operator)){
						result= !aValue.equals(value);
					}
				}
				if(result){
					return result;
				}
			}
		}
		return result;
	}
	
	
	private static final String STARTMEMBERLOGINACCOUNT = "StartMemberLoginAcunt";
	private static final String STARTMEMBERLOGINACCOUNTLEVEL = "StartMemberLoginAcuntLevl";
	private static final String START_STARTMEMBERLOGINACCOUNTLEVEL = "startStartMemberLoginAcuntLevl";
	/**
	 * parseCondition()
	 * <p>计算分支条件</p>
	 * <p>遗留的问题：该方法中使用的正则表达式中含有的关键字符串，目前没有在组织模型维护处进行有效控制，<br/>
	 *            如果用户在组织模型处，输入了含有这些关键字符串的组织模型名称，本方法可能将会出现一些问题。</p>
	 * @param conditionMap
	 * @param formData
	 * @param startMemberId
	 * @param currentNodeMemberId
	 * @param orgManager
	 * @param calcResult
	 * @return
	 * @author yuhj
	 * @author wangchw
	 * @throws ColException
	 */
	public static Map<String,Object> parseCondition(Map<String,Object> conditionMap,Map<String,String> formData,
	        long startMemberId,long currentNodeMemberId,OrgManager orgManager,boolean calcResult,
	        String currentNodeId,BPMProcess process) throws ColException{
		return parseCondition(conditionMap, formData, startMemberId, currentNodeMemberId, orgManager, calcResult, currentNodeId, process, -1);
	}
	/**
	 * parseCondition()
	 * <p>计算分支条件</p>
	 * <p>遗留的问题：该方法中使用的正则表达式中含有的关键字符串，目前没有在组织模型维护处进行有效控制，<br/>
	 *            如果用户在组织模型处，输入了含有这些关键字符串的组织模型名称，本方法可能将会出现一些问题。</p>
	 * @param conditionMap
	 * @param formData
	 * @param startMemberId
	 * @param currentNodeMemberId
	 * @param orgManager
	 * @param calcResult
	 * @return
	 * @author yuhj
	 * @author wangchw
	 * @throws ColException
	 */
	public static Map<String,Object> parseCondition(Map<String,Object> conditionMap,Map<String,String> formData,
	        long startMemberId,long currentNodeMemberId,OrgManager orgManager,boolean calcResult,
	        String currentNodeId,BPMProcess process,long startMemberLoginAccountId) throws ColException{
		if(conditionMap == null)
			return null;
		List<String> conditions = (List<String>)conditionMap.get("conditions");
		if(conditions==null || conditions.size()==0){
			return null;
		}
		//TODO 需要和calculateCondition方法合并
		//初始化组织机构信息
		V3xOrgMember startMember = null;
		V3xOrgMember currentNodeMember = null;
		V3xOrgPost standardPost = null;
		V3xOrgLevel level = null;
		try {
			startMember = orgManager.getMemberById(startMemberId);
			if(startMemberId==currentNodeMemberId){
				currentNodeMember = startMember;
			}else{
				currentNodeMember = orgManager.getMemberById(currentNodeMemberId);
			}
		}catch(BusinessException e) {
			log.error("",e);
			throw new ColException(e);
		}
		HashMap<String,Long> memberMap = new HashMap<String,Long>();
		memberMap.put("Department", currentNodeMember.getOrgDepartmentId());
		memberMap.put("Post", currentNodeMember.getOrgPostId());
		memberMap.put("Level", currentNodeMember.getOrgLevelId());
		memberMap.put("Account", currentNodeMember.getOrgAccountId());
		memberMap.put("startdepartment", startMember.getOrgDepartmentId());
		memberMap.put("startpost", startMember.getOrgPostId());
		memberMap.put("startlevel", startMember.getOrgLevelId());
		memberMap.put("startaccount", startMember.getOrgAccountId());
		try{
			standardPost = orgManager.getBMPostByPostId(startMember.getOrgPostId());
			memberMap.put("startStandardpost", standardPost==null?-1:standardPost.getId());
			standardPost = orgManager.getBMPostByPostId(currentNodeMember.getOrgPostId());
			memberMap.put("standardpost", standardPost==null?-1:standardPost.getId());
			level = orgManager.getLevelById(startMember.getOrgLevelId());
			memberMap.put("startGrouplevel", level.getGroupLevelId()==null?-1:level.getGroupLevelId());
			level = orgManager.getLevelById(currentNodeMember.getOrgLevelId());
			memberMap.put("grouplevel", level.getGroupLevelId()==null?-1:level.getGroupLevelId());
		}catch(Exception e){
			log.error(e);
		}
		List<String> teamIds = new ArrayList<String>();
		List<String> startTeamIds = new ArrayList<String>();
		List<String> secondPosts = new ArrayList<String>();
		List<String> startSecondPosts = new ArrayList<String>();
		try {
			List<V3xOrgEntity> teams = orgManager.getUserDomain(currentNodeMemberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
			for(V3xOrgEntity org:teams) {
				teamIds.add(org.getId().toString());
			}
			teams = orgManager.getUserDomain(startMemberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
			for(V3xOrgEntity org:teams) {
				startTeamIds.add(org.getId().toString());
			}
			teams = null;
			List<MemberPost> secondPost = currentNodeMember.getSecond_post();
			for(MemberPost org:secondPost) {
				secondPosts.add(org.getDepId()+"_"+org.getPostId());
			}
			secondPost = startMember.getSecond_post();
			for(MemberPost org:secondPost) {
				startSecondPosts.add(org.getDepId()+"_"+org.getPostId());
			}
		}catch(Exception e) {
			log.error("",e);
		}

		//匹配模式:这块代码由wangchw去掉
//		StringBuffer sb = new StringBuffer("(Account)|(Department)|(Post)|(Level)")
//			.append("|(startdepartment)|(startpost)|(startlevel)|(startaccount)|(standardpost)|(startStandardpost)|(grouplevel)|(startGrouplevel)")
//			.append("|(include\\(.*?\\'\\))|(exclude\\(.*?\\'\\))|(<>)|(isDep\\(.*?Child\\))|(isNotDep\\(.*?Child\\))|(isStartDep\\(.*?Child\\))|(isNotStartDep\\(.*?Child\\))")
//			.append("|(isrole\\([^\\)]*\\))|(isnotrole\\([^\\)]*\\))|(ispost\\(.*?Post\\))|(isNotpost\\(.*?Post\\))|(isStartpost\\(.*?Post\\))|(isNotStartpost\\(.*?Post\\))")
//			.append("|(isNotStartpost\\(.*?startpost\\))|(isStartpost\\(.*?startpost\\))");
//		if(formData != null)
//			sb.append("|(\\{[^\\}]*\\})");
//		if(calcResult)
//			sb.append("|(\\[[^:]*:[^\\[\\]]*\\])");

		String group = null;
		int position = 0;
		String result = "false";
		String tmp = null;
		Expression ex = null;
		Map map = new HashMap();
		String roleType=null,roleName=null,postId=null;
    	String[] postTypes = null;
    	boolean roleResult = false;
    	boolean hasChildDep = false;
    	String[] temp = null;
    	List<Long> params = null;
		int i = 0;
		int count = 0;
		String key;
		ColHelper helper = new ColHelper();
		List<Integer> conditionTypes = new ArrayList<Integer>();
		conditionMap.put("conditionTypes", conditionTypes);
		for(String condition:conditions) {
			//匹配模式 ：定义正则表达式所有组成单元，每个表达式是一个最小单元，且相互之间不存在包含关系。
			Vector<String> regExpCells= new Vector<String>();
			regExpCells.add("(<>)");//不等于计算符号
			regExpCells.add("(Account)");//单位
			regExpCells.add("(Department)");//部门
			regExpCells.add("(Post\\s*?!=)");//岗位：对老版本岗位匹配表达式(Post<>)进行兼容
			regExpCells.add("(Post\\s*?==)");//岗位：对老版本岗位匹配表达式(Post==)进行兼容
			regExpCells.add("(Level)");//职务级别
			regExpCells.add("(startdepartment)");//发起者所属部门
			regExpCells.add("(startpost\\s*?!=)");//发起者所属岗位：对老版本发起者所属岗位匹配表达式(startpost<>)进行兼容
			regExpCells.add("(startpost\\s*?==)");//发起者所属岗位：对老版本发起者所属岗位匹配表达式(startpost==)进行兼容
			regExpCells.add("(startlevel)");//发起者所属职务级别
			regExpCells.add("(startaccount)");//发起者所属单位
			regExpCells.add("(standardpost)");//标准岗位
			regExpCells.add("(startStandardpost)");//发起者标准岗位
			regExpCells.add("(grouplevel)");//集团职务级别
			regExpCells.add("(startGrouplevel)");//发起者所属集团职务级别
			regExpCells.add("(include\\(.*?\\'\\))");//包含组
			regExpCells.add("(exclude\\(.*?\\'\\))");//不包含组
			regExpCells.add("(isDep\\(.*?:.*?Child\\))");//等于某个部门，且包含子部门
			regExpCells.add("(isNotDep\\(.*?:.*?Child\\))");//不等于某个部门，且不等于子部门
			regExpCells.add("(isStartDep\\(.*?:.*?Child\\))");//等于发起者所属部门，且包含子部门
			regExpCells.add("(isNotStartDep\\(.*?:.*?Child\\))");//不等于发起者所属部门，且包含子部门
			regExpCells.add("(isrole\\([^\\)]*\\))");//等于指定的角色
			regExpCells.add("(isnotrole\\([^\\)]*\\))");//不等于指定的角色
			regExpCells.add("(ispost\\(.*?:.*?Post\\))");//等于指定的岗位
			regExpCells.add("(isNotpost\\(.*?:.*?Post\\))");//不等于指定的岗位
			regExpCells.add("(isStartpost\\(.*?:.*?Post\\))");//等于发起者所属的岗位
			regExpCells.add("(isNotStartpost\\(.*?:.*?Post\\))");//不等于发起者所属的岗位
			regExpCells.add("(isNotStartpost\\(.*?:.*?startpost\\))");//不等于发起者所属的岗位
			regExpCells.add("(isStartpost\\(.*?:.*?startpost\\))");//等于发起者所属的岗位
			
			regExpCells.add("(ispost\\(.*?:.*?StartMemberLoginAcunt\\))");//按发起人登录单位判断是否等于指定的岗位
			regExpCells.add("(isNotpost\\(.*?:.*?StartMemberLoginAcunt\\))");//按发起人登录单位判断是否不等于指定的岗位
			regExpCells.add("(isStartpost\\(.*?:.*?StartMemberLoginAcunt\\))");//按发起人登录单位判断是否等于指定的岗位
			regExpCells.add("(isNotStartpost\\(.*?:.*?StartMemberLoginAcunt\\))");//按发起人登录单位判断是否不等于指定的岗位
			
			regExpCells.add("(\\[(\\s*?(Account)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(Concurrent_Acunt)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于兼职单位
			regExpCells.add("(\\[(\\s*?(Account,Concurrent_Acunt)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Level)\\s*?)]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于主职务级别
			regExpCells.add("(\\[(\\s*?(startlevel)\\s*?)]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于主职务级别
			regExpCells.add("(\\[(\\s*?(Concurrent_Levl)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(Level,Concurrent_Levl)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于主职务级别或兼职职务级别
			
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcunt)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于发起人登录单位
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcunt)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于发起人登录单位
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcuntLevl)\\s*?)]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断当前节点的职务级别(等于)
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcuntLevl)\\s*?)]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断当前节点的职务级别(不等于)
			regExpCells.add("(\\[(\\s*?(startStartMemberLoginAcuntLevl)\\s*?)]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断发起人的职务级别(等于)
			regExpCells.add("(\\[(\\s*?(startStartMemberLoginAcuntLevl)\\s*?)]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断发起人的职务级别(不等于)
			
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentAcunt)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount,Start_ConcurrentAcunt)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentLevl)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(startlevel,Start_ConcurrentLevl)\\s*?)\\]\\s*?==\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于主职务级别或兼职职务级别
			
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentAcunt)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount,Start_ConcurrentAcunt)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentLevl)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(startlevel,Start_ConcurrentLevl)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于主职务级别或兼职职务级别
			regExpCells.add("(\\[(\\s*?(Account)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(Concurrent_Acunt)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于兼职单位
			regExpCells.add("(\\[(\\s*?(Account,Concurrent_Acunt)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Level)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于主职务级别
			regExpCells.add("(\\[(\\s*?(startlevel)\\s*?)]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//等于主职务级别
			regExpCells.add("(\\[(\\s*?(Concurrent_Levl)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(Level,Concurrent_Levl)\\s*?)\\]\\s*?<>\\s*?\\[[^\\s]*?:[-]*\\d*?\\]\\s*)");//不等于主职务级别或兼职职务级别
			
			//======================对[Start_ConcurrentLevl] == [经理【注意：这儿有空格】Manager:-7336852075564874307]情况进行兼容 ================
			regExpCells.add("(\\[(\\s*?(Account)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(Concurrent_Acunt)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于兼职单位
			regExpCells.add("(\\[(\\s*?(Account,Concurrent_Acunt)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Level)\\s*?)]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于主职务级别
			regExpCells.add("(\\[(\\s*?(startlevel)\\s*?)]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于主职务级别
			regExpCells.add("(\\[(\\s*?(Concurrent_Levl)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(Level,Concurrent_Levl)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于主职务级别或兼职职务级别
			 
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcunt)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于发起人登录单位
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcunt)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于发起人登录单位
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcuntLevl)\\s*?)]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断当前节点的职务级别(等于)
			regExpCells.add("(\\[(\\s*?(StartMemberLoginAcuntLevl)\\s*?)]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断当前节点的职务级别(不等于)
			regExpCells.add("(\\[(\\s*?(startStartMemberLoginAcuntLevl)\\s*?)]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断发起人的职务级别(等于)
			regExpCells.add("(\\[(\\s*?(startStartMemberLoginAcuntLevl)\\s*?)]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//按照发起人登录单位判断发起人的职务级别(不等于)
			
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentAcunt)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount,Start_ConcurrentAcunt)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentLevl)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(startlevel,Start_ConcurrentLevl)\\s*?)\\]\\s*?==\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于主职务级别或兼职职务级别
			
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentAcunt)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount,Start_ConcurrentAcunt)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Start_ConcurrentLevl)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(startlevel,Start_ConcurrentLevl)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于主职务级别或兼职职务级别
			regExpCells.add("(\\[(\\s*?(Account)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于所属单位
			regExpCells.add("(\\[(\\s*?(startaccount)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于所属单位
			regExpCells.add("(\\[(\\s*?(Concurrent_Acunt)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于兼职单位
			regExpCells.add("(\\[(\\s*?(Account,Concurrent_Acunt)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于所属单位或兼职单位
			regExpCells.add("(\\[(\\s*?(Level)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于主职务级别
			regExpCells.add("(\\[(\\s*?(startlevel)\\s*?)]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//等于主职务级别
			regExpCells.add("(\\[(\\s*?(Concurrent_Levl)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于兼职职务级别
			regExpCells.add("(\\[(\\s*?(Level,Concurrent_Levl)\\s*?)\\]\\s*?<>\\s*?\\[.*?:[-]*\\d*?\\]\\s*)");//不等于主职务级别或兼职职务级别
			if(formData != null){//有表单数据
				regExpCells.add("(\\{[^\\}]*\\})");
			}
			if(calcResult){
				regExpCells.add("(\\[[^:]*:[^\\[\\]]*\\])");
			}
			if(log.isInfoEnabled()){
				log.info("conditions["+i+"]:="+condition);
			}
			condition = condition.replaceAll("&#44;", ",").replaceAll("&#91;", "")
			.replaceAll("&quot;form:check&quot;", "true").replaceAll("&quot;form:uncheck&quot;", "false")
			.replaceAll("'form:check'", "true").replaceAll("'form:uncheck'", "false")//兼容触发子流程处的条件分支匹配判断-wangchw 2011-12-12
			.replaceAll("\"form:uncheck\"", "false").replaceAll("\"form:check\"", "true").replaceAll("\"", "&quot;")
			.replaceAll("&amp;lt;&amp;gt;", "<>").replaceAll("&lt;&gt;", "<>");
			//兼容: Post &amp;lt;&amp;gt; [成都区域总经办副经理:-7480981787868704928]和Post &lt;&gt; [成都区域总经办副经理:-7480981787868704928]
//			if(log.isInfoEnabled()){
//				log.info("condition:="+condition);
//			}
			boolean need= condition.indexOf("&amp;")!=-1;
			while(need){
				condition= condition.replaceAll("&amp;","&");
				need= condition.indexOf("&amp;")!=-1;
			}
			if(condition.indexOf("handCondition")!=-1) {
				condition = condition.replaceAll("handCondition", "false");
				conditionTypes.add(2);
			}else{
				conditionTypes.add(0);
			}
			//匹配模式
			String atomicityMatchRegex= getAtomicityRegExpCell(regExpCells,condition);
			while(!"".equals(atomicityMatchRegex)){//找到匹配的表达式
				Pattern p = Pattern.compile(atomicityMatchRegex);
				Matcher m = p.matcher(condition);
				StringBuffer sb = new StringBuffer();
				while(m.find()) {
					group = m.group().trim();
//					if(log.isInfoEnabled()){
//						log.info("group:="+group);
//					}
					//表单数据
					if(group.startsWith("{") && group.endsWith("}")) {
						//tip = formData.get(group.substring(1,group.length()-1));
						//表单中集团基准岗和集团职务级别分别是格式是：{formStandardpost:岗位},{formGrouplevel:职务级别}
						key = group.substring(1,group.length()-1);
						if(key.indexOf("formStandardpost:")!=-1){
							key = key.replaceFirst("formStandardpost:", "");
							tmp = formData.get(key);
							try{
								if(tmp != null && !"".equals(tmp)){
									standardPost = orgManager.getBMPostByPostId(Long.parseLong(tmp));
								}
								if(standardPost != null){
									tmp = standardPost.getId().toString();
								}
							}catch(Exception e){
								log.error("获取基准岗错误:"+tmp,e);
							}
						}else if(key.indexOf("formGrouplevel:")!=-1){
							key = key.replaceFirst("formGrouplevel:", "");
							tmp = formData.get(key);
							try{
								if(tmp != null && !"".equals(tmp)){
									level = orgManager.getLevelById(Long.parseLong(tmp));
								}
								if(level != null && level.getGroupLevelId()!=null){
									tmp = level.getGroupLevelId().toString();
								}
							}catch(Exception e){
								log.error("获取集团职务级别错误："+tmp, e);
							}
						}else{
							tmp = formData.get(key);
						}
						//由于jexl不支持中文的表达式，所以通过变量传入
						if(Strings.isIncludeNotCharacter(tmp)) {
							m.appendReplacement(sb, "a"+count);
							map.put("a"+count, tmp);
							count++;
						}else{
							//jexl解析不了"2500.00" > 2000的问题，报转型错误，所以在这里，如果传入的值是数字类型的，就变成2500.00 > 2000
							if(NumberUtils.isNumber(tmp)){
								m.appendReplacement(sb, tmp);
							}else{
								m.appendReplacement(sb, "\""+tmp+"\"");
							}
						}
					}else if(group.startsWith("[") && group.endsWith("]")) {
						if(group.indexOf(STARTMEMBERLOGINACCOUNT)!=-1){
							if(group.indexOf(STARTMEMBERLOGINACCOUNTLEVEL)!=-1){//按发起人登录单位判断职务级别
								String id=group.substring(group.lastIndexOf(":")+1, group.length()-1);//职务级别id
								long levelId = Long.parseLong(id);
								boolean isLevl=false;
								if(group.indexOf("==")!=-1){
									isLevl=true;
								}
								boolean levl=false;
								if(group.indexOf(START_STARTMEMBERLOGINACCOUNTLEVEL) != -1){
									levl = isInLevl(startMemberLoginAccountId, levelId, startMember);
								} else {
									levl = isInLevl(startMemberLoginAccountId, levelId, currentNodeMember);
								}
								if(!isLevl){
									levl=!levl;
								}
								m.appendReplacement(sb, String.valueOf(levl));
								
							} else { //是否等于发起人登录单位
								String id=group.substring(group.lastIndexOf(":")+1, group.length()-1);//单位id
								boolean isAcunt=false;
								if(group.indexOf("==")!=-1){
									isAcunt=true;
								}
								boolean acunt=false;
								long accountId = Long.parseLong(id);
								if(accountId == startMemberLoginAccountId){
									acunt = true;
								}
								if(!isAcunt){
									acunt=!acunt;
								}
								m.appendReplacement(sb, String.valueOf(acunt));
							}
						}
						else if(group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_LEVEL)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_LEVEL)!=-1||group.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1||
								group.indexOf(V3xOrgEntity.ORGENT_TYPE_LEVEL)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL)!=-1){
							if(group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1){
								List<String> acuntList=new ArrayList<String>();//条件列表
								if(group.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1){
									acuntList.add(V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
								}
								if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1){
									acuntList.add(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT);
								}
								if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT)!=-1){
									acuntList.add(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT);
								}
								if(group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_ACCOUNT)!=-1){
									acuntList.add(V3xOrgEntity.ORGENT_START_TYPE_ACCOUNT);
								}
								//获取单位id
								String id=group.substring(group.lastIndexOf(":")+1, group.length()-1);
								boolean isAcunt=false;
								if(group.indexOf("==")!=-1){
									isAcunt=true;
								}
								boolean acunt=false;
								if(group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT)!=-1){
									if(acuntList!=null&&acuntList.size()>0){
										try {
											acunt=isInAcunt(acuntList,Long.parseLong(id),startMember);
										} catch (NumberFormatException e) {
											log.error("数据类型转换错误",e);
										} catch (BusinessException e) {
											log.error("分支匹配出错",e);
										}
									}
								}else{
									if(acuntList!=null&&acuntList.size()>0){
										try {
											acunt=isInAcunt(acuntList,Long.parseLong(id),currentNodeMember);
										} catch (NumberFormatException e) {
											log.error("数据类型转换错误",e);
										} catch (BusinessException e) {
											log.error("分支匹配出错",e);
										}
									}
								}
								if(!isAcunt){
									acunt=!acunt;
								}
								m.appendReplacement(sb, String.valueOf(acunt));
								
							}
							else{
								List<String> levlList=new ArrayList<String>();//条件列表
								if(group.indexOf(V3xOrgEntity.ORGENT_TYPE_LEVEL)!=-1){
									levlList.add(V3xOrgEntity.ORGENT_TYPE_LEVEL);
								}
								if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL)!=-1){
									levlList.add(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL);
								}
								if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_LEVEL)!=-1){
									levlList.add(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_LEVEL);
								}
								if(group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_LEVEL)!=-1){
									levlList.add(V3xOrgEntity.ORGENT_START_TYPE_LEVEL);
								}
								//获取单位id
								String id=group.substring(group.indexOf(":")+1, group.length()-1);
								boolean isLevl=false;
								if(group.indexOf("==")!=-1){
									isLevl=true;
								}
								boolean levl=false;
								if(group.indexOf(V3xOrgEntity.ORGENT_START_TYPE_LEVEL)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_LEVEL)!=-1){
									if(levlList!=null&&levlList.size()>0){
										try {
											levl=isInLevl(levlList,Long.parseLong(id),startMember);
										} catch (NumberFormatException e) {
											log.error("数据类型转换错误",e);
										}
									}
								}else{
									if(levlList!=null&&levlList.size()>0){
										try {
											levl=isInLevl(levlList,Long.parseLong(id),currentNodeMember);
										} catch (NumberFormatException e) {
											log.error("数据类型转换错误",e);
										}
									}
								}
								if(!isLevl){
									levl=!levl;
								}
								m.appendReplacement(sb, String.valueOf(levl));
							}
						}else{
							//修改值
							position = group.indexOf(":");
							if(position != -1) {
								tmp = group.substring(position+1,group.length()-1);
								if(Strings.isIncludeNotCharacter(tmp)) {
									m.appendReplacement(sb, "a"+count);
									map.put("a"+count, tmp);
									count++;
								}else{
									m.appendReplacement(sb, (calcResult?"\"":"")+tmp+(calcResult?"\"":""));
								}
							}
						}
					}else if((group.startsWith("include") || group.startsWith("exclude"))&& group.indexOf(",")!=-1){
						if(calcResult){
							if(group.indexOf("team,")!=-1){
								map.put("team", teamIds);
							}else if(group.indexOf("startTeam,")!=-1){
								map.put("startTeam", startTeamIds);
							}else if(group.indexOf("secondpost,")!=-1){
								map.put("secondpost", secondPosts);
							}else if(group.indexOf("startSecondpost,")!=-1){
								map.put("startSecondpost", startSecondPosts);
							}
							m.appendReplacement(sb, "t."+group.substring(0,group.indexOf(",")+1));
							map.put("t", helper);
						}else{
							//m.appendReplacement(sb, group.substring(0,group.indexOf(",")+1));
						}
					}else if("<>".equals(group)) {
						m.appendReplacement(sb, "!=");
					}else if(group.indexOf("role")!=-1){
						position = group.indexOf("(");
						roleType = group.substring(position+1);
			    		position = roleType.indexOf(",");
			    		int lastPostion= roleType.lastIndexOf("'");
			    		if(lastPostion<=0){
			    		    lastPostion= roleType.lastIndexOf("\"");
			    		}
			    		if(lastPostion<=0){
                            lastPostion= roleType.lastIndexOf("&quot;");
                        }
			    		int firstPostion= roleType.indexOf(":")+1;
			    		roleName = roleType.substring(firstPostion,lastPostion);
		    			roleType = roleType.substring(0,position);
		    			try{
		    			    String[] roleNames= roleName.split(",");
		    			    if(roleNames.length==1){//old role branch data
		    			        if(group.indexOf("startrole") != -1){
	                                roleResult = orgManager.isRole(startMemberId,null, "Role", roleName);
	                            }else if(group.indexOf("Role") != -1){
	                                roleResult = orgManager.isRole(currentNodeMemberId,null, "Role", roleName);
	                            }
		    			    }else if(roleName.indexOf(STARTMEMBERLOGINACCOUNT) != -1){//按发起人登录单位判断角色
		    			    	 if(group.indexOf("startrole") != -1){
		    			    		 roleResult = orgManager.isRole(startMemberId,startMemberLoginAccountId ,"Role", roleNames[0]);
		    			    	 }else if(group.indexOf("Role") != -1){
		    			    		 roleResult = orgManager.isRole(currentNodeMemberId,startMemberLoginAccountId ,"Role", roleNames[0]);
		    			    	 }
		    			    }else{//new branch data
		    			        String realRoleName= roleNames[0];
		    			        String[] depts= new String[roleNames.length-1];//选择的部门条件
		    			        for (int jj = 1; jj < roleNames.length; jj++) {
		    			            if(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT.equals(roleNames[jj])){
		    			                depts[jj-1]= V3xOrgEntity.ORGENT_TYPE_POST;
		    			            }else if(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT.equals(roleNames[jj])){
		    			                depts[jj-1]= V3xOrgEntity.ORGREL_TYPE_MEMBER_POST;
		    			            }else if(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT.equals(roleNames[jj])){
		    			                depts[jj-1]= V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST;
		    			            }
                                }
		    			        if(null != currentNodeId && "start".equals(currentNodeId)){
		    			            Long accountId= CurrentUser.get()==null?null:CurrentUser.get().getLoginAccount();
		    			            roleResult = orgManager.isRole(startMemberId,accountId ,"Role", realRoleName,depts);
		    			        }else{
		    			            if(group.indexOf("startrole") != -1){
		    			                Long accountId= null;
		    			                if(null!=process){
		    			                    BPMActor startActor = (BPMActor) process.getStart().getActorList().get(0);
	                                        String accountIdStr= startActor.getParty().getAccountId();
	                                        accountId= accountIdStr==null?null:Long.parseLong(accountIdStr);
		    			                }
	                                    roleResult = orgManager.isRole(startMemberId,accountId,"Role", realRoleName,depts);
	                                }else if(group.indexOf("Role") != -1){
	                                    roleResult = orgManager.isRole(currentNodeMemberId,null,"Role", realRoleName,depts);
	                                }
		    			        }
		    			        
		    			    }
		    			}catch(BusinessException be){
		    				log.error("分支匹配角色时错误",be);
		    			}
		    			if(group.indexOf("isnotrole")!=-1)
		    				roleResult = !roleResult;
		    			m.appendReplacement(sb, String.valueOf(roleResult));
					}else if(group.indexOf("ispost")!=-1 || group.indexOf("isNotpost")!=-1 || group.indexOf("isStartpost")!=-1 || group.indexOf("isNotStartpost")!=-1){
						position = group.indexOf("(");
			    		roleType = group.substring(position+1);
			    		position = roleType.indexOf(",");
			    		postId = roleType.substring(roleType.indexOf(":")+1, position);
		    			roleType = roleType.substring(position+1,roleType.length()-1);
		    			postTypes = roleType.split(",");
		    			try{
		    				if(group.indexOf("isStartpost") != -1 || group.indexOf("isNotStartpost") != -1){
		        				if(postTypes != null){
		        					for(int k=0;k<postTypes.length;k++){
		        						postTypes[k] = postTypes[k].replace("startpost", "Post");
		        					}
		        				}
		        				if(postTypes != null && postTypes.length == 1 && STARTMEMBERLOGINACCOUNT.equals(postTypes[0])){
		        					roleResult = orgManager.isInPost(startMemberLoginAccountId, startMemberId, Long.parseLong(postId));
		        				} else {
		        					roleResult = orgManager.isPost(startMemberId, Long.parseLong(postId), postTypes);
		        				}
		        			}else if(group.indexOf("ispost") != -1 || group.indexOf("isNotpost") != -1){
		        				if(postTypes != null && postTypes.length == 1 && STARTMEMBERLOGINACCOUNT.equals(postTypes[0])){
		        					roleResult = orgManager.isInPost(startMemberLoginAccountId, currentNodeMemberId, Long.parseLong(postId));
		        				} else {
		        					roleResult = orgManager.isPost(currentNodeMemberId, Long.parseLong(postId), postTypes);
		        				}
		        			}
		    			}catch(BusinessException be){
		    				log.error("分支匹配岗位时错误",be);
		    			}
		    			if(group.indexOf("isNotpost")!=-1 || group.indexOf("isNotStartpost")!=-1){
		    				roleResult = !roleResult;
		    			}
		    			m.appendReplacement(sb, String.valueOf(roleResult));
					}else if(group.indexOf("isDep")!=-1 || group.indexOf("isNotDep")!=-1 || group.indexOf("isStartDep")!=-1 || group.indexOf("isNotStartDep")!=-1){
						List<String> depts=new ArrayList<String>();//选择的部门条件
						if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT)!=-1){
							if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT)!=-1){
								depts.add(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT);
								group=group.replace(","+V3xOrgEntity.ORGREL_TYPE_DEPARTMENT, "");
							}
							if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT)!=-1){
								depts.add(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT);
								group=group.replace(","+V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT, "");
							}
							if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT)!=-1){
								depts.add(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT);
								group=group.replace(","+V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT, "");
							}
						}else{//对低版本的流程进行处理
							depts.add(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT);
						}
						temp = group.split(",");
						if(temp != null){
							params = new ArrayList();
							try{
								for(int k=0;k<temp.length;k++){
								    temp[k]= temp[k].replace("(excludeChild)", "(不包含子部门)");
								    temp[k]= temp[k].replace("(includeChild)", "(包含子部门)");
									if(temp[k].indexOf(Constant.BranchDepartmentStatus.includeChild.name())!=-1){
										hasChildDep = true;
									}else if(temp[k].indexOf(Constant.BranchDepartmentStatus.excludeChild.name())!=-1){
										hasChildDep = false;
									}else{
										position = temp[k].lastIndexOf(":");
										params.add(Long.parseLong(temp[k].substring(position+1)));
									}
								}
								if(group.indexOf("isStartDep")!=-1 || group.indexOf("isNotStartDep")!=-1){
									if(depts!=null&&depts.size()>0){
	                                    roleResult=orgManager.isInDepartment(startMemberId,depts, params, hasChildDep);
									}
									//roleResult = orgManager.isInDepartment(startMemberId, params, hasChildDep);
									if(group.indexOf("isNotStartDep")!=-1){
										roleResult = !roleResult;
									}
								}else{
									if(depts!=null&&depts.size()>0){
	                                    roleResult=orgManager.isInDepartment(currentNodeMemberId,depts, params, hasChildDep);
									}
									if(group.indexOf("isNotDep")!=-1){
										roleResult = !roleResult;
									}
								}
							}catch(Exception e){
								roleResult = false;
								log.error("解析部门分支条件时异常"+group, e);
							}
						}
						m.appendReplacement(sb, String.valueOf(roleResult));
					}else{
						//组织机构
						if(group.indexOf("Post")!=-1 ){//兼容旧版本流程中的岗位匹配
							Pattern p1 = Pattern.compile("(Post)");
							Matcher m1 = p1.matcher(group);
							if(m1.find()){
								StringBuffer sbtemp = new StringBuffer();
								String groupCode= m1.group();
								m1.appendReplacement(sbtemp, (calcResult?"\"":"")+memberMap.get(groupCode)+(calcResult?"\"":""));
								m1.appendTail(sbtemp);
								m.appendReplacement(sb, sbtemp.toString());
							}
						}else if(group.indexOf("startpost")!=-1){//兼容旧版本流程中发起者岗位的匹配
							Pattern p1 = Pattern.compile("(startpost)");
							Matcher m1 = p1.matcher(group);
							if(m1.find()){
								StringBuffer sbtemp = new StringBuffer();
								String groupCode= m1.group();
								m1.appendReplacement(sbtemp, (calcResult?"\"":"")+memberMap.get(groupCode)+(calcResult?"\"":""));
								m1.appendTail(sbtemp);
								m.appendReplacement(sb, sbtemp.toString());
							}
						}else{
							int startPos= m.start();
							int endPos= m.end();
							boolean isFormFiled= "{".equals(condition.substring(startPos-1, startPos)) && "}".equals(condition.substring(endPos, endPos+1));
							if(("Account".equalsIgnoreCase(group)
									|| "Department".equalsIgnoreCase(group) 
									|| "Level".equalsIgnoreCase(group)
									|| "startdepartment".equalsIgnoreCase(group)
									|| "startlevel".equalsIgnoreCase(group)
									|| "startaccount".equalsIgnoreCase(group)
									|| "standardpost".equalsIgnoreCase(group)
									|| "startStandardpost".equalsIgnoreCase(group)
									|| "grouplevel".equalsIgnoreCase(group)
									|| "startGrouplevel".equalsIgnoreCase(group)) && isFormFiled){
								m.appendReplacement(sb,group);
							}else{
								m.appendReplacement(sb, (calcResult?"\"":"")+memberMap.get(group)+(calcResult?"\"":""));
							}
						}
					}
//					if(log.isInfoEnabled()){
//						log.info("sb:="+sb);
//					}
				}
				m.appendTail(sb);
//				if(log.isInfoEnabled()){
//					log.info("sb1:="+sb);
//				}
				condition= sb.toString();
				//获得下一个正则匹配表达式
				atomicityMatchRegex= getAtomicityRegExpCell(regExpCells,condition);
			}
//			if(log.isInfoEnabled()){
//				log.info("condition:="+condition);
//			}
			if(!calcResult){
				String tempStr= condition.replaceAll("'", "\\\\\'");
				if(log.isInfoEnabled()){
					log.info("conditions["+i+"]:="+tempStr);
				}
				conditions.set(i, tempStr);
			}else{
				try {
					ex = ExpressionFactory.createExpression(condition);
					JexlContext jc = JexlHelper.createContext();
					if(map.size()>0){
						jc.setVars(map);
					}
					result = ex.evaluate(jc).toString();
					if(log.isInfoEnabled()){
						log.info("conditions_jexl["+i+"]:="+result);
					}
					conditions.set(i, result);
				}catch(Exception e) {
					conditions.set(i, "false");
					log.error("jexl解析分支报错",e);
				}
			}
			map.clear();
			i++;
		}
		return conditionMap;
	}
	
	private static boolean isInLevl(long accountId, long levlId, V3xOrgMember member){
		if(member == null) return false;
		List<String> levlList=new ArrayList<String>();
		if(member.getOrgAccountId().longValue() == accountId){ //主职务
			if(member.getOrgLevelId()==levlId){
				return true;
			}else{//看是否跟集团映射的职务级别相等
				try {
					V3xOrgLevel groupLevel= orgManager.getLevelById(member.getOrgLevelId());
					if( null!= groupLevel && null!= groupLevel.getGroupLevelId() && groupLevel.getGroupLevelId()== levlId){
						return true;
					}
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
				}
			}
		} else { // 兼职职务
			try {
				Map<Long, List<ConcurrentPost>> ma = orgManager.getConcurentPostsByMemberId(accountId, member.getId());
				Iterator it = ma.entrySet().iterator(); 
				while(it.hasNext()) 
				{
					Map.Entry<Long, List<ConcurrentPost>> entry=(Map.Entry<Long, List<ConcurrentPost>>)it.next();
					List<ConcurrentPost> l=(List<ConcurrentPost>) entry.getValue(); 
					for(int j=0;j<l.size();j++){
						ConcurrentPost cp=l.get(j);
						if( null!=cp.getCntLevelId() ){
						    if(cp.getCntLevelId()==levlId){
                                return true;
                            }else{
                                V3xOrgLevel groupLevel= orgManager.getLevelById(cp.getCntLevelId());
                                if( null!= groupLevel && null!= groupLevel.getGroupLevelId() && groupLevel.getGroupLevelId()== levlId){
                                    return true;
                                }
                            }
						}
					}
				} 
			} catch (BusinessException e) {
				log.error(e.getMessage(), e);
			}
		}
		return false;
	}
	private static boolean isInLevl(List<String> levlList, long levlId,
			V3xOrgMember member) {
		if(levlList.contains(V3xOrgEntity.ORGENT_TYPE_LEVEL)||levlList.contains(V3xOrgEntity.ORGENT_START_TYPE_LEVEL)){//主职务
			if(member.getOrgLevelId()==levlId){
				return true;
			}else{//看是否跟集团映射的职务级别相等
				try {
					V3xOrgLevel groupLevel= orgManager.getLevelById(member.getOrgLevelId());
					if( null!= groupLevel && null!= groupLevel.getGroupLevelId() && groupLevel.getGroupLevelId()== levlId){
						return true;
					}
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		if(levlList.contains(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL)||levlList.contains(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_LEVEL)){//兼职职务
			Map<Long, List<ConcurrentPost>> ma;
			try {
				getOrgManager();
				List<V3xOrgAccount> ptActs=orgManager.concurrentAccount(member.getId());
				for(int i=0;i<ptActs.size();i++){
					ma = orgManager.getConcurentPostsByMemberId(ptActs.get(i).getId(), member.getId());
					Iterator   it   =   ma.entrySet().iterator()   ; 
					while(it.hasNext()) 
					{
						Map.Entry<Long, List<ConcurrentPost>> entry=(Map.Entry<Long, List<ConcurrentPost>>)it.next();
						List<ConcurrentPost> l=(List<ConcurrentPost>) entry.getValue(); 
						for(int j=0;j<l.size();j++){
							ConcurrentPost cp=l.get(j);
							if( null!=cp.getCntLevelId() ){
							    if(cp.getCntLevelId()==levlId){
	                                return true;
	                            }else{
	                                V3xOrgLevel groupLevel= orgManager.getLevelById(cp.getCntLevelId());
	                                if( null!= groupLevel && null!= groupLevel.getGroupLevelId() && groupLevel.getGroupLevelId()== levlId){
	                                    return true;
	                                }
	                            }
							}
						}
					} 
				}
			} catch (BusinessException e) {
				log.error("解析职务级别条件时出错"+e);
			}
		}
		return false;
	}
	/**
	 * 
	 * @param acuntList 单位条件列表
	 * @param acuntId 条件单位id
	 * @param currentNodeMember 
	 * @return 单位条件是否成立
	 * @throws BusinessException
	 */
	private static boolean isInAcunt(List<String> acuntList, long acuntId, V3xOrgMember nodeMember) throws BusinessException {
		if(acuntList.contains(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)||acuntList.contains(V3xOrgEntity.ORGENT_START_TYPE_ACCOUNT)){//所属单位
			if(nodeMember.getOrgAccountId()==acuntId){
				return true;
			}
		}
		if(acuntList.contains(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)||acuntList.contains(V3xOrgEntity.ORGREL_TYPE_START_CONCURRENT_ACCOUNT)){//兼职单位
			getOrgManager();
			if(orgManager.getConcurrentAccounts(nodeMember.getId()).contains(orgManager.getAccountById(acuntId))){
				return true;
			}
		}
		return false;
	}
	public static Map<Long,String> calculateCondition(Map<Long,String> conditionMap,Map<String,String> paramAndValueMap,long startMemberId,
			long currentNodeMemberId,OrgManager orgManager,boolean calculateIt,Long formAppId,Long masterId,String formData,long startMemberLoginAccountId) throws ColException{
		/**
		 * TODO此处临时解决，需要调整parseCondition的参数
		 */
		Map<String,Object> temp = new HashMap<String,Object>();
		List<String> results = new ArrayList<String>(conditionMap.values());
		temp.put("conditions", results);
		temp= preParseCondition(temp, formAppId,masterId,formData);
		parseCondition(temp,paramAndValueMap,startMemberId,currentNodeMemberId,orgManager,calculateIt,null,null,startMemberLoginAccountId);
		int i = 0;
		Set<Entry<Long,String>> entries = conditionMap.entrySet();
		for(Entry<Long,String> result:entries){
			result.setValue(results.get(i));
			i++;
		}
		return conditionMap;
		/*Map<Long,String> resultMap = new HashMap<Long,String>();
		if(conditionMap==null || conditionMap.size()==0)
			return null;
		//初始化组织机构信息
		V3xOrgMember startMember = null;
		V3xOrgMember currentNodeMember = null;
		try {
			startMember = orgManager.getMemberById(startMemberId);
			if(startMemberId==currentNodeMemberId)
				currentNodeMember = startMember;
			else
				currentNodeMember = orgManager.getMemberById(currentNodeMemberId);
		}catch(BusinessException e) {
			log.error(e);
			throw new ColException(e);
		}
		HashMap<String,Long> memberMap = new HashMap<String,Long>();
		memberMap.put("Department", currentNodeMember.getOrgDepartmentId());
		memberMap.put("Post", currentNodeMember.getOrgPostId());
		memberMap.put("Level", currentNodeMember.getOrgLevelId());
		memberMap.put("Account", currentNodeMember.getOrgAccountId());
		memberMap.put("startdepartment", startMember.getOrgDepartmentId());
		memberMap.put("startpost", startMember.getOrgPostId());
		memberMap.put("startlevel", startMember.getOrgLevelId());
		memberMap.put("startaccount", startMember.getOrgAccountId());

		List<String> teamIds = new ArrayList<String>();
		List<String> startTeamIds = new ArrayList<String>();
		List<String> secondPosts = new ArrayList<String>();
		List<String> startSecondPosts = new ArrayList<String>();
		try {
			List<V3xOrgEntity> teams = orgManager.getUserDomain(currentNodeMemberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
			for(V3xOrgEntity org:teams) {
				teamIds.add(org.getId().toString());
			}
			teams = orgManager.getUserDomain(startMemberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
			for(V3xOrgEntity org:teams) {
				startTeamIds.add(org.getId().toString());
			}
			teams = null;
			List<MemberPost> secondPost = currentNodeMember.getSecond_post();
			for(MemberPost org:secondPost) {
				secondPosts.add(org.getDepId()+"_"+org.getPostId());
			}
			secondPost = startMember.getSecond_post();
			for(MemberPost org:secondPost) {
				startSecondPosts.add(org.getDepId()+"_"+org.getPostId());
			}
		}catch(Exception e) {
			log.error(e);
		}

		Pattern p = Pattern.compile("(\\{[^\\}]*\\})|(\\[[^:]*:[^\\[\\]]*\\])|(Account)|(Department)|(Post)|(Level)" +
				"|(startdepartment)|(startpost)|(startlevel)|(startaccount)|(include\\([^\']*)|(exclude\\([^\']*)|(<>)");

		String group = null;
		int position = 0;
		String result = "false";
		String tmp = null;
		Expression ex = null;
		Map map = new HashMap();

		int i = 0;
		int count = 0;
		Set<Entry<Long,String>> entries = conditionMap.entrySet();
		ColHelper helper = new ColHelper();
		String condition = "";
		for(Entry<Long,String> entry:entries) {
			condition = entry.getValue();
			if(Strings.isBlank(condition)){
				resultMap.put(entry.getKey(), "true"); //无条件
			}
			else{
				condition = condition.replaceAll("&quot;", "\"").replaceAll("&#44;", ",");
				StringBuffer sb = new StringBuffer();
				Matcher m = p.matcher(condition);
				while(m.find()) {
					group = m.group();
					//表单数据
					if(group.startsWith("{") && group.endsWith("}")) {
						if(paramAndValueMap != null) {
							tmp = paramAndValueMap.get(group.substring(1,group.length()-1));
							//由于jexl不支持中文的表达式，所以通过变量传入
							if(Strings.isIncludeNotCharacter(tmp)) {
								m.appendReplacement(sb, "a"+count);
								map.put("a"+count, tmp);
								count++;
							}else
								m.appendReplacement(sb, "\""+tmp+"\"");
						}
					}else if(group.startsWith("[") && group.endsWith("]")) {
						//修改值
						position = group.indexOf(":");
						if(position != -1) {
							tmp = group.substring(position+1,group.length()-1);
							if(Strings.isIncludeNotCharacter(tmp)) {
								m.appendReplacement(sb, "a"+count);
								map.put("a"+count, tmp);
								count++;
							}else
								m.appendReplacement(sb, "\""+tmp+"\"");
						}
					}else if((group.startsWith("include") || group.trim().startsWith("exclude"))&& group.indexOf(",")!=-1){
						if(group.indexOf("team,")!=-1)
							map.put("team", teamIds);
						else if(group.indexOf("startTeam,")!=-1)
							map.put("startTeam", startTeamIds);
						else if(group.indexOf("secondpost,")!=-1)
							map.put("secondpost", secondPosts);
						else if(group.indexOf("startSecondpost,")!=-1)
							map.put("startSecondpost", startSecondPosts);
                        if(calculateIt){
                            m.appendReplacement(sb, "t."+group.substring(0,group.indexOf(",")+1));
                        }
                        else{
                            m.appendReplacement(sb, group.substring(0,group.indexOf(",")+1));
                        }
						map.put("t", helper);
					}else if("<>".equals(group)) {
						m.appendReplacement(sb, "!=");
					}
					else {
						//组织机构
						m.appendReplacement(sb, "\""+memberMap.get(group)+"\"");
					}
				}
				m.appendTail(sb);
				if(calculateIt) {
					try {
						ex = ExpressionFactory.createExpression(sb.toString());
						JexlContext jc = JexlHelper.createContext();
						if(map.size()>0)
							jc.setVars(map);
						result = ex.evaluate(jc).toString();
						resultMap.put(entry.getKey(), result);
					}catch(Exception e) {
						resultMap.put(entry.getKey(), "false");
						log.error(e);
					}
				}else {
					resultMap.put(entry.getKey(), sb.toString().replaceAll(",", "&#44;").replaceAll("\"", "&quot;").replaceAll("'", "\\\\\'"));
				}
				map.clear();
				i++;
			}
		}
		return resultMap;*/
	}

	public static HashMap<String,Object> splitCondition(HashMap<String,String> hash){
		HashMap<String,Object> result = new HashMap<String,Object>();
		Set<Map.Entry<String, String>> entry = hash.entrySet();
    	List<String> keys = new ArrayList<String>();
    	List<String> nodeNames = new ArrayList<String>();
    	List<String> conditions = new ArrayList<String>();
    	List<String> forces = new ArrayList<String>();
    	List<String> links = new ArrayList<String>();
    	String[] temp = null;
    	String[] temp1 = null;
    	String order = hash.get("order");
    	if(order != null && order.indexOf("$")!=-1) {
    		temp1 = StringUtils.split(order,"$");
    	}

    	StringBuffer sb = new StringBuffer();
    	if(temp1!=null && temp1.length>0) {
    		for(String item:temp1){
    			String value = hash.get(item);
        		if(value!=null&&value.indexOf("↗")!=-1){
        			sb.append(item+":");
	        		keys.add(item);
	        		links.add(hash.get("linkTo"+item));
	        		temp = value.split("↗");
	        		if(temp != null){
	        			nodeNames.add(temp[0]);
	        			//temp[1] = temp[1].replaceAll("handCondition", "false");
	        			conditions.add(temp[1]);
	        			if(temp.length==3 && "1".equals(temp[2]))
	        				forces.add("true");
	        			else
	        				forces.add("false");
	        		}
        		}
        	}
    	}else {
        	for(Map.Entry<String, String> item:entry){
        		if(item.getValue()!=null&&item.getValue().indexOf("↗")!=-1){
        			sb.append(item.getKey()+":");
	        		keys.add(item.getKey());
	        		links.add(hash.get("linkTo"+item.getKey()));
	        		temp = item.getValue().split("↗");
	        		if(temp != null){
	        			nodeNames.add(temp[0]);
	        			//temp[1] = temp[1].replaceAll("handCondition", "false");
	        			conditions.add(temp[1]);
	        			if(temp.length==3)
	        				forces.add("true");
	        			else
	        				forces.add("false");
	        		}
        		}
        	}
    	}
    	if(keys.size() > 0 && conditions.size() >0){
    		result.put("allNodes", sb.toString());
    		result.put("keys", keys);
    		result.put("names", nodeNames);
    		result.put("conditions", conditions);
    		result.put("nodeCount", hash.get("nodeCount"));
    		result.put("forces", forces);
    		result.put("links", links);
        }
		return result;
	}

	public static boolean include(List<String> list,String target) {
		if(list == null)
			return false;
		for(String id:list) {
			if(id.equals(target))
				return true;
		}
		return false;
	}

	public static boolean exclude(List<String> list,String target) {
		return !include(list,target);
	}

	/**
	 * 外部接口获取分支结果
	 * map.get("allNodes")    String类型，通过":"分割当前节点的所有后续节点
	 * map.get("keys")        List<String>,当前节点的所有有分支条件的后续节点
	 * map.get("conditions")  List<String>,分支计算结果，"true"表示分支条件符合，"false"表示分支条件不符合
	 * map.get("forces")      List<String>,是否强制分支，"true"表示是强制分支，"false"表示非强制分支
	 *                        强制分支时用户不能改变分支的状态，非强制分支时用户可以自由选择是否走这一分支
	 * @param affair
	 * @param formData
	 * @param startMemberId
	 * @param currentNodeMemberId
	 * @param orgManager
	 * @return
	 * @throws ColException
	 */
	public static Map<String,Object> getCondition(Affair affair,HashMap<String,TIP_InputValueAll> formData,long startMemberId,long currentNodeMemberId,OrgManager orgManager,Long formAppId,Long masterId) throws ColException{
		Map<String,String> data = new HashMap<String,String>();
		Set<Entry<String,TIP_InputValueAll>> entrys = formData.entrySet();
		for(Entry<String,TIP_InputValueAll> entry:entrys) {
			data.put(entry.getKey(), entry.getValue().getValue());
		}
		return getCondition(affair,data,startMemberId,currentNodeMemberId,orgManager,formAppId,masterId);
	}

	public static Map<String,Object> getCondition(Affair affair,Map<String,String> formData,long startMemberId,long currentNodeMemberId,OrgManager orgManager,Long formAppId,Long masterId) throws ColException{
		BPMAbstractNode activity = getBPMActivityByAffair(affair);
		return getCondition(activity,formData,startMemberId,currentNodeMemberId,orgManager,true,formAppId, masterId,null);
	}
	
	public static Map<String,Object> getCondition(BPMAbstractNode currentNode,Map<String,String> formData,long startMemberId,long currentNodeMemberId,OrgManager orgManager,boolean calcResult) throws ColException{
		return getCondition(currentNode,formData,startMemberId,currentNodeMemberId,orgManager,calcResult,null,null,null);
	}

	public static Map<String,Object> getCondition(BPMAbstractNode currentNode,Map<String,String> formData,long startMemberId,long currentNodeMemberId,OrgManager orgManager,boolean calcResult,Long formAppId,Long masterId,String formDataStr) throws ColException{
		HashMap<String,String> conditonMap = new HashMap<String,String>();
		if("start".equals(currentNode.getId()))
			conditonMap.put("currentIsStart", "true");
		findDirectHumenChildrenCondition(currentNode,conditonMap);
		if(conditonMap.size()>0) {
			Map<String,Object> map = splitCondition(conditonMap);
			if(calcResult){//为手机wap端处理提供服务
				map= preParseCondition(map, formAppId, masterId, formDataStr);
			}
			return parseCondition(map,formData,startMemberId,currentNodeMemberId,orgManager,calcResult,null,null);
		}
		return null;
	}

	/**
	 * 判断后续节点是否有分支条件
	 * @param currentNode    当前节点
	 * @return               true有分支条件；false没有
	 */
	public static boolean hasCondition(BPMAbstractNode currentNode){
		List<BPMTransition> downLinks = currentNode.getDownTransitions();
		if(downLinks != null){
			for(BPMTransition link:downLinks){
				if(link.getFormCondition() != null && !"".equals(link.getFormCondition()) && !"null".equals(link.getFormCondition()))
					return true;
				BPMAbstractNode nextNode = link.getTo();
				if(!(nextNode instanceof BPMHumenActivity))
					if(hasCondition(nextNode))
						return true;
			}
		}
		return false;
	}

	/**
	 * 判断后续节点是否有分支条件
	 * @param processId            当前节点不是start时，传入此参数
	 * @param currentNodeId        当前节点id，发起时是“start”
	 * @param processXml           当前节点是start时，需要传入xml
	 * @param affairId             当前处理节点的affairId
	 * @param judgeFinished        判断时是否要考虑是否是最后一个执行节点。true：如果后续节点有分支但是不需要匹配后续节点时，返回false。
	 * @return
	 */
	public static boolean hasCondition(String processId,String currentNodeId,String processXml,String affairId,boolean judgeFinished,AffairManager affairManager){
		BPMProcess process = null;
		BPMAbstractNode currentNode = null;
		boolean result = false;
		try{
			if("start".equals(currentNodeId) && Strings.isNotBlank(processXml)){
				process = BPMProcess.fromXML(processXml);
				if(process != null){
					currentNode = process.getStart();
				}
			}else if(Strings.isNotBlank(processId) && Strings.isNotBlank(currentNodeId)){

				process = getProcess(processId);

				if(process != null){
					currentNode = process.getActivityById(currentNodeId);
				}
			}
			if(currentNode != null){
				result = hasCondition(currentNode);
			}
			if(!"start".equals(currentNodeId) && result && judgeFinished && Strings.isNotBlank(affairId) && process != null && currentNode != null){
				Affair affair = affairManager.getById(Long.parseLong(affairId));
				result = result && isExecuteFinished(process,affair) && !"inform".equals(currentNode.getSeeyonPolicy().getId());
			}
		}catch(Exception e){
			log.error("",e);
		}
		return result;
	}

	/**
     * 为匹配下节点做准备，构造当前节点及获取流程定义模版
     *
     * @author yuhj 2008-11-18
     * @param process 流程定义
     * @param currentNodeId 当前节点ID
     * @return WorkflowEventListener.ProcessModeSelector节点匹配结果对象
     */
	public static WorkflowEventListener.ProcessModeSelector preRunCase(BPMProcess process, String currentNodeId, boolean isFromTemplate, Long caseId) throws ColException {
		return preRunCase(process, currentNodeId, isFromTemplate, caseId, null);
	}

	/**
	 * 为匹配下节点做准备，构造当前节点及获取流程定义模版
	 *
	 * @param process
	 * @param currentNodeId
	 * @param isFromTemplate
	 * @param caseId
	 * @param fieldValueMap 当前表单域的数据
	 * @return
	 * @throws ColException
	 */
    public static WorkflowEventListener.ProcessModeSelector preRunCase(BPMProcess process, String currentNodeId, boolean isFromTemplate, Long caseId, Map<String,String[]> fieldValueMap) throws ColException {
        Map context = new HashMap();
        context.put("process", process);
        if(fieldValueMap != null){
        	context.put("fieldValueMap", fieldValueMap) ;
        }
        BPMAbstractNode current_node = null;
        if(!"start".equals(currentNodeId)){ //发起时，case不存在
			try {
				BPMCase theCase = getProcessEngine().getCase(caseId);
				context.put("case", theCase);
			} catch (BPMException e) {
				throw new ColException("流程匹配中获取流程实例异常 [caseId = " + caseId + "]", e);
			}

        	current_node = (BPMAbstractNode)process.getActivityById(currentNodeId);
        	List<BPMActor> actorList = process.getStart().getActorList();
            String sender = actorList.get(0).getParty().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }else{
        	current_node = process.getStart();
        	String sender = CurrentUser.get().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }
        WorkflowEventListener.ProcessModeSelector selector = WorkflowEventListener.parseProcessModeSelector(current_node, process, context, isFromTemplate);
        return selector;
    }

    /**
     ** 新流程改变发起节点 Mazc<br>
     *  原则：1、原模板流程开始节点更新为所选人员，同时设置NF属性为1，标记为是新流程。
     *       2、开始节点后加签一个节点，执行人为新流程设置所选中的人员
    */
    public static void changeProcess4Newflow(BPMProcess process, FlowData flowData, V3xOrgMember member, String accountShortName) throws ColException {
        //User user = CurrentUser.get();
        //WorkItemManager wim = null;
        try {
            //wim = getWorkitemManager();
            BPMStatus startNode = process.getStart();
            startNode.getSeeyonPolicy().setNF("1");
            List downTransitions = startNode.getDownTransitions();
            String formApp = "";
            String form = "";
            String operationName = "";
            BPMSeeyonPolicy seeyonPolicy = startNode.getSeeyonPolicy();
            if(seeyonPolicy != null) {
                formApp = seeyonPolicy.getFormApp();
                form = seeyonPolicy.getForm();
                operationName = seeyonPolicy.getOperationName();
            }
            BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", member.getName());
            //多级会签增加,自动增加自己加节点标识
            userNode.setFromType(flowData.getFromType());

            BPMParticipantType type = new BPMParticipantType("user");
            String roleName = "roleadmin";
            BPMActor userActor = new BPMActor(member.getId()+"", member.getName(), type, roleName,
                    BPMActor.CONDITION_OR, false, member.getOrgAccountId()+"");
            userNode.addActor(userActor);
            //设置节点的SeeyonPolicy属性
            BPMSeeyonPolicy policy = new BPMSeeyonPolicy("collaboration","协同");
            policy.setFormApp(formApp);
            policy.setForm(form);
            policy.setOperationName(operationName);
            policy.setProcessMode("single");
            userNode.setSeeyonPolicy(policy);

            BPMTransition userLink = new BPMTransition(startNode, userNode);
            process.addChild(userNode);
            process.addLink(userLink);
            if (downTransitions != null){
                for (int i = 0; i < downTransitions.size(); i++){
                    BPMTransition trans = (BPMTransition) downTransitions.get(i);
                    BPMAbstractNode to = trans.getTo();
                    BPMTransition userLink1 = new BPMTransition(userNode, to);
                    process.addLink(userLink1);
                    //如果原来有分支，复制到新的link中
                    userLink1.setConditionType(trans.getConditionType());
                    userLink1.setConditionBase(trans.getConditionBase());
                    userLink1.setConditionId(trans.getConditionId());
                    userLink1.setConditionTitle(trans.getConditionTitle());
                    userLink1.setFormCondition(trans.getFormCondition());
                    userLink1.setIsForce(trans.getIsForce());
                    userLink1.setDesc(trans.getDesc());
                    process.removeLink(trans);
                    startNode.removeDownTransition(trans);
                }
            }
            process.setUpdateDate(new Date());
            String isShowShortName = flowData.getIsShowShortName();
            if("false".equals(isShowShortName)){
                 if("false".equals(process.getIsShowShortName())){
                     process.setIsShowShortName(isShowShortName);
                 }
            }else{
                process.setIsShowShortName(isShowShortName);
            }
        }
        catch (Exception e){
            throw new ColException("新流程改变发起节点加签操作异常", e);
        }
    }

    /**
     * 检测当前节点的前节点是否有新流程
     * return 有新流程的节点Ids
     */
    public static List<String> checkPrevNodeHasNewflow(BPMActivity activity)throws ColException {
        List<String> result = new ArrayList<String>();
        if(activity != null){
            List upTransitions = activity.getUpTransitions();
            checkPrevNodeHasNewflowHelper(upTransitions, result);
        }
        return result;
    }

    //checkPrevNodeHasNewflow辅助方法
    private static void checkPrevNodeHasNewflowHelper(List upTransitions, List<String> result)throws ColException{
        if (upTransitions != null){
            for (int i = 0; i < upTransitions.size(); i++){
                BPMTransition trans = (BPMTransition) upTransitions.get(i);
                BPMAbstractNode from = trans.getFrom();
                if("false".equals(from.getSeeyonPolicy().getIsDelete())){//没有被delete
                	if(from.getNodeType().equals(BPMAbstractNode.NodeType.start)){
                        return;
                    }else if(from.getNodeType().equals(BPMAbstractNode.NodeType.split) || from.getNodeType().equals(BPMAbstractNode.NodeType.join)){
                        checkPrevNodeHasNewflowHelper(from.getUpTransitions(), result);
                    }else if(from.getNodeType().equals(BPMAbstractNode.NodeType.humen)){
                    	if(from.getSeeyonPolicy().getId().equals(BPMSeeyonPolicy.SEEYON_POLICY_INFORM.getId())
                    			|| from.getSeeyonPolicy().getId().equals(BPMSeeyonPolicy.EDOC_POLICY_ZHIHUI.getId())){//知会节点
                    		checkPrevNodeHasNewflowHelper(from.getUpTransitions(), result);
                    	}else{//非知会节点
                    		if("1".equals(from.getSeeyonPolicy().getNF())){
                                result.add(from.getId());
                            }
                    	}
                    }
                }
            }
        }
    }
    /**
     * 检测当前节点的后续节点ActivityId
     * return 后续节点id
     */
    public static List<Long> getLatterActivityIds(BPMActivity activity) throws ColException {
        List<Long> result = new ArrayList<Long>();
        if(activity != null){
            List downTransitions = activity.getDownTransitions();
            getLatterNodeIdHelper(downTransitions, result);
        }
        return result;
    }
    //  getLatterActivityIds辅助方法
    private static void getLatterNodeIdHelper(List downTransitions, List<Long> result)throws ColException{
        if (downTransitions != null){
            for (int i = 0; i < downTransitions.size(); i++){
                BPMTransition trans = (BPMTransition) downTransitions.get(i);
                BPMAbstractNode to = trans.getTo();
                if(to.getNodeType().equals(BPMAbstractNode.NodeType.end)){
                    return;
                }
                else if(to.getNodeType().equals(BPMAbstractNode.NodeType.split) || to.getNodeType().equals(BPMAbstractNode.NodeType.join)){
                    getLatterNodeIdHelper(to.getDownTransitions(), result);
                }
                else{
                    result.add(Long.parseLong(to.getId()));
                }
            }
        }
    }

    /**
     * 得到第二个节点的所有人员以及权限 马志骋(协同)，董亚杰(协同)，谭敏锋(审批)
     * @param start
     * @return
     * @throws ColException
     */
    public static String checSecondNodeMembers(BPMProcess process,Map<String,String> condition)throws ColException{
    	StringBuffer members = new StringBuffer();
    	List<String> result = new ArrayList<String>();
    	if(process != null){
    		BPMStatus start = process.getStart();
    		if(start != null){
    	    	checkNextNodeMembersHelper(start.getDownTransitions(),result,condition);
    	    }
    	}


    	int size = result.size();
    	for(int i = 0 ;i < size ; i++){
    		if(i != 0){
    			members.append(",");
    		}
    		members.append(result.get(i));
    	}
    	return members.toString();
    }

    /**
     * 得到本节点的下个节点的处理人员 格式为 马志骋(协同)，董亚杰(协同)，谭敏锋(审批)
     * @param currentNode
     * @return
     * @throws ColException
     */
    public static String checkNextNodeMembers(BPMActivity currentNode,Map<String,String> condition)throws ColException{
    	StringBuffer members = new StringBuffer();
    	List<String> result = new ArrayList<String>();
    	if(currentNode != null){
    		checkNextNodeMembersHelper(currentNode.getDownTransitions(),result,condition);
    	}
    	int size = result.size();
    	for(int i = 0 ;i < size ; i++){
    		if(i != 0){
    			members.append(",");
    		}
    		members.append(result.get(i));
    	}
    	return members.toString();
    }
    private static void checkNextNodeMembersHelper(List upTransitions,List<String> result,Map<String,String> condition)throws ColException{
    	if(upTransitions != null){
    		int size = upTransitions.size();
    		for(int i = 0 ; i < size ; i ++){
    			BPMTransition trans = (BPMTransition) upTransitions.get(i);
    			BPMAbstractNode to = trans.getTo();
    			if(to.getNodeType().equals(BPMAbstractNode.NodeType.end)){
    				return;
    			}else if(to.getNodeType().equals(BPMAbstractNode.NodeType.split) || to.getNodeType().equals(BPMAbstractNode.NodeType.join)){
    				checkNextNodeMembersHelper(to.getDownTransitions(),result,condition);
				} else if (to.getActorList() != null
						&& !to.getActorList().isEmpty()
						&& to.getActorList().get(0) != null
						&& V3xOrgEntity.ORGENT_META_KEY_BlankNode.equals(((BPMActor) to.getActorList().get(0)).getParty().getId())) {
					// 空节点，跳过，找下一个
					checkNextNodeMembersHelper(to.getDownTransitions(), result, condition);
    			}else {
    				if(condition != null && !condition.isEmpty() && condition.containsKey(to.getId())){
    					if("false".equals(condition.get(to.getId()))){
    						BPMSeeyonPolicy policy = to.getSeeyonPolicy();
    						if(policy != null && !"true".equals(policy.getIsDelete())){
    							String name = to.getName()+"("+to.getSeeyonPolicy().getName()+")";
        	    				result.add(name);
    						}
    					}
    				}else{
    					BPMSeeyonPolicy policy = to.getSeeyonPolicy();
						if(policy != null && !"true".equals(policy.getIsDelete())){
							String name = to.getName()+"("+to.getSeeyonPolicy().getName()+")";
    	    				result.add(name);
						}
    				}
    			}
    		}
    	}
    }


    //减签成了一个一个的减，消息得一下子保存，要不然出现同样的消息
    public static void saveDeletePeopleMessage(Long userId,ColSummary summary,Affair affair,List<Party> parties){
    	Map<Long, List<MessageData>> messageDataMap = ColHelper.messageDataMap;
    	//减签消息提醒
        List<MessageData> messageDataList = null;
        Long summaryId = summary.getId();
        if(messageDataMap.get(summaryId) == null){
        	 messageDataList = new ArrayList<MessageData>();
        }else{
        	messageDataList = messageDataMap.get(summaryId);
        	messageDataMap.remove(summaryId);
        }
    	MessageData messageData = new MessageData();
        messageData.setOperationType("deletePeople");
        messageData.setHandlerId(userId);
        messageData.setSummary(summary);
        messageData.setAffair(affair);
        List<String> partyNames = new ArrayList<String>();
        int size = parties.size();
        StringBuffer memb = new StringBuffer();
        for(int i = 0 ; i < size ; i ++){
        	Party party = parties.get(i);
        	if(party != null){
        		partyNames.add(party.getName());
        		if(memb.length()>0){
            		memb.append(",");
            	}
        		memb.append(party.getName()+"("+party.getSeeyonPolicy().getName()+")");
        	}
        }
        messageData.addProcessLogParam(memb.toString());

        messageData.setPartyNames(partyNames);
        messageDataList.add(messageData);
        messageDataMap.put(summaryId, messageDataList);
        //CLUSTER 集群-更新备机  如果有了，先删再添
        NotificationManager.getInstance().send(NotificationType.Collaboration_UpdateMessageData_Put, messageData);
    }

    public static void deleteQuartzJobOfSummary(ColSummary summary){
    	deleteQuartzJob(summary.getId());
    }

    protected static void deleteQuartzJob(Long summaryId){
    	if(QuartzHolder.hasQuartzJob("ColProcessDeadLine" + summaryId)){
    		QuartzHolder.deleteQuartzJob("ColProcessDeadLine" + summaryId);
    		QuartzHolder.deleteQuartzJob("ColProcessRemind" + summaryId);
    	}
    	QuartzHolder.deleteQuartzJob("ColSupervise" + summaryId);
	}

    public static void createQuartzJobOfSummary(ColSummary summary, WorkTimeManager workTimeManager){
    	createQuartzJob(ApplicationCategoryEnum.collaboration, summary.getId(), summary.getCreateDate(),
    			summary.getDeadline(), summary.getAdvanceRemind(), summary.getOrgAccountId(), workTimeManager);
    }

    protected static void createQuartzJob(ApplicationCategoryEnum app,long summaryId, Date createTime, Long deadLine, Long advanceRemind, Long orgAcconutID, WorkTimeManager workTimeManager){
    	try{
			//超期提醒
			if(deadLine != null && deadLine != 0){

				Date deadLineRunTime = workTimeManager.getCompleteDate4Nature(createTime, deadLine, orgAcconutID);
				{
					Map<String, String> datamap = new HashMap<String, String>(3);

			    	datamap.put("appType", String.valueOf(app.key()));
			    	datamap.put("isAdvanceRemind", "1");
			    	datamap.put("objectId", String.valueOf(summaryId));

			   		QuartzHolder.newQuartzJob("ColProcessDeadLine" + summaryId, deadLineRunTime, "processCycRemindQuartzJob", datamap);
				}

		   		//提前提醒
		        if (advanceRemind != null && advanceRemind != -1) {
		        	Date advanceRemindTime = workTimeManager.getRemindDate(deadLineRunTime, advanceRemind);//.getCompleteDate4Nature(createTime, deadLine - advanceRemind, orgAcconutID);

		            Map<String, String> datamap = new HashMap<String, String>(3);
		            datamap.put("appType", String.valueOf(app.key()));
		            datamap.put("isAdvanceRemind", "0");
		            datamap.put("objectId", String.valueOf(summaryId));

		            QuartzHolder.newQuartzJob("ColProcessRemind" + summaryId, advanceRemindTime, "processCycRemindQuartzJob", datamap);
		        }
			}
	    }
		catch (Exception e) {
	        log.error("获取定时调度器对象失败", e);
	    }
    }

    public static void createQuarz4Supervise(long summaryId, ColSuperviseDetail detail, long senderId, String supervisorMemberId, String subject){
    	try {
	        Map<String, String> p = new HashMap<String, String>(4);
			p.put("colSuperviseId", String.valueOf(detail.getId()));
			p.put("senderId", String.valueOf(senderId));
			p.put("supervisorMemberId", supervisorMemberId.toString());
			p.put("subject", subject);

			//TODO 工作时间设置

            QuartzHolder.newQuartzJob("ColSupervise" + summaryId, detail.getAwakeDate(), "terminateColSuperviseJob", p);
    	}
    	catch(Exception e) {
    		log.error(e);
    	}
    }

    /**
     *
     * @param processXml    流程模板定义xml
     * @param nodeId        当前节点id，如果是"start"代表当前节点是发起节点
     * @return
     */
	public static boolean hasSelectorOrCondition(String processXml,String nodeId, Long caseId){
		BPMProcess process = BPMProcess.fromXML(processXml);
		String currentNodeId = nodeId;
		if("start".equals(nodeId))
			currentNodeId = process.getStart().getId();
		try{
			WorkflowEventListener.ProcessModeSelector selector = ColHelper.preRunCase(process, currentNodeId, true,caseId);
			if(selector.mode==2)
				return true;
		}catch(Exception e){
			log.error(e);
			return true;
		}
		if("start".equals(nodeId))
			return ColHelper.hasCondition(process.getStart());
		return ColHelper.hasCondition(process.getActivityById(nodeId));
	}

	/**
	 * 使用工作流中的conditionTitle更新
	 * @param processId
	 * @param branchs
	 */
	public static List<ColBranch> updateBranchByProcess(String processId,List<ColBranch> branchs) {
		if(branchs == null || branchs.isEmpty())
			return null;
		BPMProcess process;
		try {
			process = getCaseProcess(processId);
		}catch(Exception e) {
			log.error("获取process异常", e);
			return branchs;
		}
		if(process == null)
			return branchs;
		List<BPMTransition> links = process.getLinks();
		if(links == null)
			return branchs;
		List<ColBranch> newBranchs = new ArrayList<ColBranch>(branchs.size());
		try{
			ColBranch newBranch = null;
			for(ColBranch branch:branchs){
				newBranch = (ColBranch)branch.clone();
				newBranchs.add(newBranch);
			}
		}catch(Exception e){
			log.error("",e);
		}
		Long linkId;
		for(BPMTransition link:links) {
			if((link.getConditionTitle() != null && !"".equals(link.getConditionTitle()) && link.getConditionTitle().indexOf("undefined")==-1) || link.getConditionType()==2) {
				boolean findIt = false;
				for(ColBranch branch:newBranchs) {
					linkId = branch.getLinkId();
					if(linkId == null)
						continue;
					if(link.getId().equals(linkId.toString())) {
						findIt = true;
						branch.setLinkId(Long.parseLong(link.getId()));
						branch.setConditionBase(link.getConditionBase());
						branch.setFormCondition(link.getFormCondition());
						branch.setIsForce(link.getIsForce()==null||"".equals(link.getIsForce())||"null".equals(link.getIsForce())
								?0:Integer.parseInt(link.getIsForce()));
						//if(link.getConditionType()==1)
						branch.setConditionTitle(link.getConditionTitle());
						//else if(link.getConditionType()==2 && branch.getConditionType()!=2)
						branch.setConditionType(link.getConditionType());
						break;
					}

				}
				if(!findIt) {
					ColBranch branch = new ColBranch();
					branch.setId(-1l);
					branch.setLinkId(Long.parseLong(link.getId()));
					branch.setConditionTitle(link.getConditionTitle());
					branch.setConditionBase(link.getConditionBase());
					branch.setConditionType(link.getConditionType());
					branch.setFormCondition(link.getFormCondition());
					branch.setIsForce(link.getIsForce()==null||"".equals(link.getIsForce())||"null".equals(link.getIsForce())
							?0:Integer.parseInt(link.getIsForce()));
					newBranchs.add(branch);
				}
			}
		}

		//转换显示
		for(ColBranch branch:newBranchs){
			if(branch.getConditionType()==1){
				branch.setConditionTitle(parseCondition(branch.getFormCondition(),null,getOrgManager()));
			}
		}
		return newBranchs;
	}

	/**
	 * 得到节点权限所属单位ID<br>
	 * 原则：1、系统模板，取模板所在单位ID<br>
	 * 		2、自由协同，取协同所在单位ID
	 * @return
	 */
	public static Long getFlowPermAccountId(Long defaultAccountId, Long templeteId,Long summaryAccountId, TempleteManager templeteManager){
		Long flowPermAccountId = defaultAccountId;
		if(templeteId != null){
			Templete templete = templeteManager.get(templeteId);
			if(templete != null){
				flowPermAccountId = templete.getOrgAccountId();
			}
		}
		else{
			if(summaryAccountId != null){
				flowPermAccountId = summaryAccountId;
			}
		}
    	return flowPermAccountId;
	}

	public static Long getTempletePrePigholePath(Long templeteId){
		if(templeteId == null)
			return null;

		try{
			Templete templete = getTempleteManager().get(templeteId);
			if(templete!=null){
				ColSummary	summary = (ColSummary)XMLCoder.decoder(templete.getSummary());
				if(summary!= null){
					Long archiveId = summary.getArchiveId();
					if(archiveId!=null)
						return archiveId;
				}
			}
		}catch(Exception e){
			log.error(e);
		}
		return null;
	}
	public static Long getFlowPermAccountId(Long defaultAccountId, ColSummary summary, TempleteManager templeteManager){
		Long flowPermAccountId = defaultAccountId;
		Long templeteId = summary.getTempleteId();
		if(templeteId != null){
			Templete templete = templeteManager.get(templeteId);
			if(templete != null){
				flowPermAccountId = templete.getOrgAccountId();
			}else{
				if(summary.getOrgAccountId() != null){
					flowPermAccountId = summary.getOrgAccountId();
				}
			}
		}
		else{
			if(summary.getOrgAccountId() != null){
				flowPermAccountId = summary.getOrgAccountId();
			}
		}
    	return flowPermAccountId;
	}
	/**
	 * 计算表达式，目前支持判断角色和岗位。判断是否具有指定角色；判断用户是否具有指定岗位，如果postId是基准岗，则按照基准岗匹配
	 * 角色分支表达式：isRole(Role,'部门主管:DepManager'); isNotRole(Role,'部门主管:DepManager')
	 * 岗位分支表达式：isPost(财务总监:-565923631438215037,主岗,副岗,兼职岗位)；isNotPost(财务总监:-565923631438215037,主岗,副岗,兼职岗位)
	 * 岗位类型：     主岗 V3xOrgEntity.ORGENT_TYPE_POST；副岗 V3xOrgEntity.ORGREL_TYPE_MEMBER_POST；兼职岗位 V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST
	 * @param startMemberId   发起人id
	 * @param currMemberId    当前处理节点id
	 * @param orgManager
	 * @param expression
	 * @return  计算结果
	 */
	public static String calcExpression(long startMemberId,long currMemberId,OrgManager orgManager,String expression){
		Pattern p = Pattern.compile("(isrole\\([^\\)]*\\))|(isnotrole\\([^\\)]*\\))|(ispost\\([^\\)]*\\))|(isNotpost\\([^\\)]*\\))|(isStartpost\\([^\\)]*\\))|(isNotStartpost\\([^\\)]*\\))");
    	Matcher m = p.matcher(expression);
    	String group;
    	String roleType=null,roleName=null,postId=null;
    	String[] postTypes = null;
    	int position;
    	boolean calcResult = false;
    	StringBuffer buffer = new StringBuffer();
    	while(m.find()){
    		group = m.group();
    		position = group.indexOf("(");
    		roleType = group.substring(position+1);
    		position = roleType.indexOf(",");
    		if(group.indexOf("role")!=-1){
    			roleName = roleType.substring(roleType.indexOf(":")+1,roleType.lastIndexOf("'"));
    			roleType = roleType.substring(0,position);
    		}else if(group.indexOf("post")!=-1){
    			postId = roleType.substring(roleType.indexOf(":")+1, position);
    			roleType = roleType.substring(position+1,roleType.length()-1);
    			postTypes = roleType.split(",");
    		}
    		try{
    			if(group.indexOf("startrole") != -1){
    				calcResult = orgManager.isRole(startMemberId,null,roleType, roleName);
    			}else if(group.indexOf("Role") != -1){
    				calcResult = orgManager.isRole(currMemberId,null,roleType, roleName);
    			}else if(group.indexOf("isStartpost") != -1 || group.indexOf("isNotStartpost") != -1){
    				if(postTypes != null){
    					for(int i=0;i<postTypes.length;i++){
    						postTypes[i] = postTypes[i].replace("startpost", "Post");
    					}
    				}
    				calcResult = orgManager.isPost(startMemberId, Long.parseLong(postId), postTypes);
    			}else if(group.indexOf("ispost") != -1 || group.indexOf("isNotpost") != -1){
    				calcResult = orgManager.isPost(currMemberId, Long.parseLong(postId), postTypes);
    			}
    		}catch(BusinessException be){
    			log.error("",be);
    		}
    		if(group.indexOf("isrole")!=-1 || group.indexOf("isStartpost")!=-1 || group.indexOf("ispost")!=-1){
    			m.appendReplacement(buffer, String.valueOf(calcResult));
    		}else if(group.indexOf("isnotrole")!=-1 || group.indexOf("isNotpost")!=-1 || group.indexOf("isNotStartpost")!=-1){
    			m.appendReplacement(buffer, String.valueOf(!calcResult));
    		}
    	}
    	m.appendTail(buffer);
    	return buffer.toString();
	}

	/**
	 * 判断指定操作在节点权限中是否允许
	 *
	 * @param summary 协同实例，主要取发起时的“不允许归档、***”
	 * @param nodePolicy 节点权限
	 * @param policyAccountId 节点权限所在单位，比如模板所在单位
	 * @param action 操作
	 * @return
	 */
	public static boolean isActionAllowedOfNodePolicy(PermissionManager permissionManager, ColSummary summary, String nodePolicy, String action, Long policyAccountId){
		Set<String> actions = new HashSet<String>();

		List<String> allowedPolicyActions = permissionManager.getActionList(Constant.ConfigCategory.col_flow_perm_policy.name(), nodePolicy, policyAccountId);
		if(allowedPolicyActions!=null && allowedPolicyActions.size() > 0){
			actions.addAll(allowedPolicyActions);
		}

		// 获得所有协同定义上支持的Action
		if (summary != null) {
			//不允许归档
			if(!summary.getCanArchive()){
				actions.remove("CanArchive");
			}
			//不允许转发
			if(!summary.getCanForward()){
				actions.remove("CanForward");
			}
			//不允许跟踪
			if(!summary.getCanTrack()){
				actions.remove("CanTrack");
			}
			//不允许修改正文
			if(!summary.getCanEdit()){
				actions.remove("CanEdit");
			}
			//不允许改变流程
			if(!summary.getCanModify()){
				actions.remove("Infom");
				actions.remove("AddNode");
				actions.remove("JointSign");
				actions.remove("RemoveNode");
			}
		}

		return action.contains(action);
	}

	public static List<ColBranch> transformBranch(List<ColBranch> branchs,boolean transformTitle){
		if(branchs == null || branchs.isEmpty())
			return branchs;

		List<ColBranch> newBranchs = new ArrayList<ColBranch>(branchs.size());
		for(ColBranch branch:branchs){
			if(log.isInfoEnabled()){
				log.info("branch.getLinkId():="+branch.getLinkId());
				log.info("branch.getConditionBase():="+branch.getConditionBase());
				log.info("branch.getFormCondition():="+branch.getFormCondition());
				log.info("branch.getIsForce():="+branch.getIsForce());
				log.info("branch.getConditionType():="+branch.getConditionType());
				log.info("branch.getConditionDesc():="+branch.getConditionDesc());
			}
			ColBranch branchNew = new ColBranch();
			branchNew.setId(-1L);
			branchNew.setLinkId(branch.getLinkId());
			branchNew.setConditionBase(branch.getConditionBase());
			branchNew.setFormCondition(branch.getFormCondition());
			branchNew.setIsForce(branch.getIsForce());
			branchNew.setConditionTitle(transformTitle?parseCondition(branch.getFormCondition(),null,getOrgManager()):branch.getConditionTitle());
			branchNew.setConditionType(branch.getConditionType());
			branchNew.setConditionDesc(branch.getConditionDesc());
			newBranchs.add(branchNew);
		}
		return newBranchs;
	}

	/**
	 * 转换分支显示
	 * @param condition  分支条件
	 * @param pattern    显示格式
	 * @param orgManager
	 * @author yuhj
	 * @author wangchw
	 * @return
	 */
	public static String parseCondition(String condition,String pattern,OrgManager orgManager){
		if(condition == null || "".equals(condition)){
			return condition;
		}
		String pn = null;
		StringBuffer sb = null;
		if(pattern == null){
			sb = new StringBuffer("(Account)|(Department)|(Post)|(Level)");
			sb.append("|(standardpost)|(grouplevel)")
			.append("|(include\\([^\\&\\& | \\|\\|]*\\))|(exclude\\([^\\&\\& | \\|\\|]*\\))|(<>)")
			.append("|(isRole\\([^\\&\\& | \\|\\|]*\\))|(isNotRole\\([^\\&\\& | \\|\\|]*\\))|(isPost\\(.*?Post\\))|(isNotPost\\(.*?Post\\))")
			.append("|(isPost\\(.*?StartMemberLoginAcunt\\))|(isNotPost\\(.*?StartMemberLoginAcunt\\))")
			.append("|(isDep\\(.*?Child\\))|(isNotDep\\(.*?Child\\))|(exist\\(.*?\\d\\))")
			.append("|(\\{[^\\}]*\\})")
			.append("|(\\[[^:]*:[^\\[\\]]*\\])");
			pn = sb.toString();
		}else{
			pn = pattern;
		}
		Pattern p = Pattern.compile(pn);
		Matcher m = p.matcher(condition);
		sb = new StringBuffer();
		String group = null;
		String temp = null;
		String[] arr = null;
		int position = -1;
		int lastPosition = -1;
		String separator = Constant.getCommonString("common.separator.label");
		V3xOrgDepartment dep = null;
		V3xOrgPost post = null;
		while(m.find()) {
			group = m.group();
			if(group.equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){   //部门
				m.appendReplacement(sb, "[" + Constant.getMainString("org.department.label") + "]");
			}else if(group.equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)){  //单位
				m.appendReplacement(sb, "[" + Constant.getMainString("org.account.label") + "]");
			}else if(group.equals(V3xOrgEntity.ORGENT_TYPE_POST) || group.equals("standardpost")){  //岗位
				m.appendReplacement(sb, "[" + Constant.getMainString("org.post.label") + "]");
			}else if(group.equals(V3xOrgEntity.ORGENT_TYPE_LEVEL) || group.equals("grouplevel")){   //职务级别
				m.appendReplacement(sb, "[" + Constant.getMainString("org.level.label") + "]");
			}else if(group.startsWith("{") && group.endsWith("}")) {  //表单数据域
				position = group.indexOf(":");
				if(position!=-1){
					m.appendReplacement(sb, "{" + group.substring(position+1, group.length()-1) + "}");
				}else{
					m.appendReplacement(sb, group);
				}
			}else if((group.startsWith("include") || group.startsWith("exclude"))&& group.indexOf(",")!=-1){  //组和副岗，现在副岗用isPost表达，兼容旧数据
				if(group.indexOf("team,")!=-1){    //组，格式：include(team,代码组:'-258834243227926063')
					try{
						temp = group.substring(group.indexOf(":")+2, group.length()-2);
						V3xOrgTeam team = orgManager.getTeamById(Long.parseLong(temp));
						if(team != null){
							temp = team.getName();
						}else{
							temp = group;
						}
					}catch(Exception e){
						temp = group;
						log.error(e);
					}
					temp = "["+Constant.getMainString("org.team.label")+"] " + (group.startsWith("include")?" = ":" <> ") + temp;
					m.appendReplacement(sb, temp);
				}else if(group.indexOf("secondpost")!=-1){  //副岗，格式：include(secondpost,研发二部-测试工程师:'-6208460242951650128_9099248666769706830')
					try{
						temp = group.substring(group.indexOf(":")+2, group.length()-2);
						dep = orgManager.getDepartmentById(Long.parseLong(temp.substring(0, temp.indexOf("_"))));
						post = orgManager.getPostById(Long.parseLong(temp.substring(temp.indexOf("_")+1)));
					}catch(Exception e){
						temp = group;
						log.error(e);
					}
					if(dep != null && post != null){
						temp = "[" + Constant.getMainString("org.secondPost.label") + "] " + (group.startsWith("include")?" = ":" <> ") + dep.getName() + "-" + post.getName();
					}else{
						temp = group;
					}
					m.appendReplacement(sb, temp);
				}
			}else if(group.startsWith("[") && group.endsWith("]")){  //数据值
				if(group.indexOf(STARTMEMBERLOGINACCOUNT) != -1){
					if(group.indexOf(STARTMEMBERLOGINACCOUNTLEVEL) != -1){
						group = group.replace(STARTMEMBERLOGINACCOUNTLEVEL, "按发起人登录单位判断职务级别");
					} else {
						group = group.replace(STARTMEMBERLOGINACCOUNT, "发起人登录单位");
					}
					m.appendReplacement(sb, group);
				}else if(group.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1||
						group.indexOf(V3xOrgEntity.ORGENT_TYPE_LEVEL)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL)!=-1){
					if(group.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1||group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1){
						if(group.indexOf(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)!=-1){
							group=group.replace(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, Constant.getMainString("org.mamber_form.belongAcunt.label"));
						}
						if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT)!=-1){
							group=group.replace(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_ACCOUNT, Constant.getMainString("org.mamber_form.concurrentAcunt.label"));
						}
						m.appendReplacement(sb, group);
					}else{
						if(group.indexOf(V3xOrgEntity.ORGENT_TYPE_LEVEL)!=-1){
							group=group.replace(V3xOrgEntity.ORGENT_TYPE_LEVEL, Constant.getMainString("org.mamber_form.belongLevl.label"));
						}
						if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL)!=-1){
							group=group.replace(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_LEVEL, Constant.getMainString("org.mamber_form.concurrentLevl.label"));
						}
						m.appendReplacement(sb, group);
					}
				}else{
					temp = group.substring(1, group.length()-1);
					arr = temp.split(":");
					if(arr != null && arr.length == 2){
						//枚举直接取显示值
						if(arr[1].length() > 10){
							temp = getEntityName(arr[1],null,arr[0],orgManager);
						}else{
							temp = arr[0];
						}
					}else{
						temp = group;
					}
					m.appendReplacement(sb, temp);
				}
			}else if(group.indexOf("isRole") != -1){   //角色，格式：isRole(Role,'部门主管:DepManager')
				temp = "[" + Constant.getMainString("org.role.label") + "] = " + Constant.getMainString("space.department.manager.label");
				m.appendReplacement(sb, temp);
			}else if(group.indexOf("isNotRole") != -1){
				temp = "[" + Constant.getMainString("org.role.label") + "] <> " + Constant.getMainString("space.department.manager.label");
				m.appendReplacement(sb, temp);
			}else if(group.indexOf("isPost") != -1 || group.indexOf("isNotPost") != -1){  //岗位，格式：isPost(财务总监:-7239727208021734147,Post,Member_Post,Concurrent_Post)
				arr = group.substring(0, group.length()-1).split(",");
				if(arr != null && arr.length > 1){
					temp = "[";
					for(int i=1;i<arr.length;i++){
						if(V3xOrgEntity.ORGENT_TYPE_POST.equals(arr[i])){
							temp += Constant.getOrgString("org.member_form.primaryPost.label") + ",";
						}else if(V3xOrgEntity.ORGREL_TYPE_MEMBER_POST.equals(arr[i])){
							temp += Constant.getOrgString("org.member_form.secondPost.label") + ",";
						}else if(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST.equals(arr[i])){
							temp += Constant.getSysString("cntPost.body.cntpost.label");
						}else if(STARTMEMBERLOGINACCOUNT.equals(arr[i])){
							temp += "按发起人登录单位判断岗位";
						}
					}
					if(temp.endsWith(","))
						temp = temp.substring(0, temp.length()-1);
					temp += "]";
					if(group.indexOf("isPost") != -1)
						temp += " = ";
					else
						temp += " <> ";
					position = arr[0].indexOf("(");
					lastPosition = arr[0].indexOf(":");
					temp += getEntityName(arr[0].substring(lastPosition+1),V3xOrgEntity.ORGENT_TYPE_POST,arr[0].substring(position+1, lastPosition),orgManager);
				}else{
					temp = group;
				}
				m.appendReplacement(sb, temp);
			}else if(group.indexOf("isDep") != -1 || group.indexOf("isNotDep") != -1){
				List<String> depts=new ArrayList<String>();//选择的部门条件
				if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT)!=-1){
                    depts.add(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT);
                    group=group.replace(","+V3xOrgEntity.ORGREL_TYPE_DEPARTMENT, "");
               }
               if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT)!=-1){
                    depts.add(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT);
                    group=group.replace(","+V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT, "");
               }
               if(group.indexOf(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT)!=-1){
                    depts.add(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT);
                    group=group.replace(","+V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT, "");
               }
				arr = group.substring(0, group.length()-1).split(",");
				if(arr != null && arr.length > 1){
					//新增部门条件后处理
					if(depts!=null&&depts.size()>0){
						temp="[";
						for(int i=0;i<depts.size();i++){
							if(V3xOrgEntity.ORGREL_TYPE_DEPARTMENT.equals(depts.get(i))){
								temp+=Constant.getOrgString("org.member_form.departments.label");
							}
							if(V3xOrgEntity.ORGREL_TYPE_SECOND_POST_DEPARTMENT.equals(depts.get(i))){
								if(temp.equals("[")){
									temp+=Constant.getOrgString("org.member_form.secondPostDepartment.label");
								}else{
									temp+=","+Constant.getOrgString("org.member_form.secondPostDepartment.label");
								}
							}
							if(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_DEPARTMENT.equals(depts.get(i))){
								if(temp.equals("[")){
									temp+=Constant.getOrgString("org.member_form.concurrentDepartment.label");
								}else{
									temp+=","+Constant.getOrgString("org.member_form.concurrentDepartment.label");
								}
							}
						}
						temp+="]";
					}else{//对低版本的流程进行处理
						temp = "[" + Constant.getMainString("org.department.label") + "]";
					}
					if(group.indexOf("isDep")!= -1)
						temp += " = ";
					else
						temp += " <> ";
					for(int i=0;i<arr.length;i++){
						lastPosition = arr[i].lastIndexOf(":");
						if(lastPosition!=-1){
							if(i==0){
								position = arr[i].indexOf("(");
								temp += getEntityName(arr[i].substring(lastPosition+1),V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,arr[i].substring(position+1, lastPosition),orgManager);
							}else{
								temp += getEntityName(arr[i].substring(lastPosition+1),V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,arr[i].substring(0, lastPosition),orgManager);
							}
							if(i != arr.length-2)
								temp += separator;
						}
					}
					if(group.indexOf(Constant.BranchDepartmentStatus.includeChild.name()) != -1){
						temp += " " + Constant.getString("col.branch.includeChildren");
					}else{
						temp += " " + Constant.getString("col.branch.excludeChildren");
					}
				}else{
					temp = group;
				}
				m.appendReplacement(sb, temp);
			}else if(group.indexOf("exist")!=-1){
				arr = group.substring(0, group.length()-1).split(",");
				if(arr != null && arr.length==3){
					temp = arr[0].substring(arr[0].indexOf("(")+1);
					temp += " " + arr[1];
					position = arr[2].indexOf(":");
					if(position != -1)
						temp += " " + getEntityName(arr[2].substring(position+1),null,arr[2].substring(0, position),orgManager);
					else
						temp += " " + arr[2];
				}else
					temp = group;
				m.appendReplacement(sb, temp);
			}
		}
		m.appendTail(sb);
		return sb.toString().replaceAll("&&", "and").replaceAll("\\|\\|", "or").replaceAll("==", "=")
		.replaceAll("form:check", Constant.getString("col.branch.check")).replaceAll("form:uncheck", Constant.getString("col.branch.uncheck"));
	}

	public static String getEntityName(String entityId,String entityType,String defaultName,OrgManager orgManager){
		try{
			Long id = Long.parseLong(entityId);
			V3xOrgEntity entity = null;
			if(entityType != null){
				entity = orgManager.getEntity(entityType, id);
				if(entity != null)
					return entity.getName();
			}else{
				entity = orgManager.getEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, id);
				if(entity != null)
					return entity.getName();
				entity = orgManager.getEntity(V3xOrgEntity.ORGENT_TYPE_POST, id);
				if(entity != null)
					return entity.getName();
				entity = orgManager.getEntity(V3xOrgEntity.ORGENT_TYPE_LEVEL, id);
				if(entity != null)
					return entity.getName();
				entity = orgManager.getEntity(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, id);
				if(entity != null)
					return entity.getName();
			}
			return defaultName;
		}catch(Exception e){
			log.error("", e);
		}
		return defaultName;
	}

	/**
	 * preNext()
	 * 遍历当前处理节点的后续节点和分支条件信息
	 * @param process 工作流流程模板定义对象
	 * @param currentNodeId 当前处理流程节点Id
	 * @param isFromTemplate 是否来自模板流程
	 * @param caseId 流程实例Id
	 * @param fieldValueMap 表单集合对象
	 * @return 匹配结果集合
	 * @throws NumberFormatException
	 * @throws BusinessException
	 * @author wangchw
	 */
	public static Map preNext(
			BPMProcess process,
			String currentNodeId,
			boolean isFromTemplate,
			Long caseId,
			Map<String,String[]> fieldValueMap) throws NumberFormatException, BusinessException {
		Map context = new HashMap();
        context.put("process", process);
        if(fieldValueMap != null){
        	context.put("fieldValueMap", fieldValueMap) ;
        }
        BPMAbstractNode current_node = null;
        if(!"start".equals(currentNodeId)){//发起时，case不存在
			try {
				BPMCase theCase = getProcessEngine().getCase(caseId);
				context.put("case", theCase);
			} catch (BPMException e) {
				throw new ColException("流程匹配中获取流程实例异常 [caseId = " + caseId + "]", e);
			}

        	current_node = (BPMAbstractNode)process.getActivityById(currentNodeId);
        	List<BPMActor> actorList = process.getStart().getActorList();
            String sender = actorList.get(0).getParty().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }else{
        	current_node = process.getStart();
        	String sender = CurrentUser.get().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }
		return BranchArgs.preNext(current_node, context, isFromTemplate);
	}

	/**
	 * preRunCaseNext()
	 * 找出后续所有直接人工节点
	 * @param process 流程模板定义对象
	 * @param currentNodeId 当前流程节点Id
	 * @param isFromTemplate 是否来自模板
	 * @param caseId 流程实例Id
	 * @param fieldValueMap 表单数据集合
	 * @param startMemberId 流程发起人Id
	 * @param currentNodeMemberId 当前节点处理人员Id
	 * @param orgManager 组织模型管理对象
	 * @param calcResult 条件解析标志，一般默认为false即可
	 * @param fieldMap 表单域对象
	 * @param fieldDataBaseMap 表单基础域对象
	 * @return 匹配信息
	 * @throws NumberFormatException
	 * @throws BusinessException
	 */
	public static ProcessModeSelector preRunCaseNext(
			BPMProcess process,
			String currentNodeId,
			boolean isFromTemplate,
			long caseId,
			Map<String, String[]> fieldValueMap,
			long startMemberId,
			long currentNodeMemberId,
			OrgManager orgManager,
			boolean calcResult,
//			Map<String,String[]> fieldMap,
//			Map<String,String[]> fieldDataBaseMap,
			Long formAppId,Long masterId,String formData,long workItemId,long startMemberLoginAccountId) throws NumberFormatException, BusinessException {
		Map context = new HashMap();
        context.put("process", process);
        context.put("currentWorkitemId", workItemId);
        if(fieldValueMap != null){
        	context.put("fieldValueMap", fieldValueMap) ;
        }
        BPMAbstractNode current_node = null;
        if(!"start".equals(currentNodeId)){//发起时，case不存在
			try {
				BPMCase theCase = getProcessEngine().getCase(caseId);
				context.put("case", theCase);
			} catch (BPMException e) {
				throw new ColException("流程匹配中获取流程实例异常 [caseId = " + caseId + "]", e);
			}
        	current_node = (BPMAbstractNode)process.getActivityById(currentNodeId);
        	List<BPMActor> actorList = process.getStart().getActorList();
            String sender = actorList.get(0).getParty().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }else{
        	current_node = process.getStart();
        	String sender = CurrentUser.get().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }
        WorkflowEventListener.ProcessModeSelector selector =
        	WorkflowEventListener.parseProcessModeSelectorNew(current_node,
        			process,
        			context,
        			isFromTemplate,
        			startMemberId,
        			currentNodeMemberId,
        			orgManager,
        			calcResult,
//    				fieldMap,
//    				fieldDataBaseMap,
    				formAppId,masterId,formData,currentNodeId,startMemberLoginAccountId
        			);
        return selector;
	}

	/**
	 * findDirectHumenChildren()
	 * 查找直接后续人工活动节点和条件分支信息
	 * @param current_node当前流程节点
	 * @param condtionResult分支条件集合
	 * @param nodeTypes节点类型集合
	 * @return
	 */
	public static List<BPMHumenActivity> findDirectHumenChildren(
			BPMAbstractNode current_node,
			Map<String,String> condtionResult,
			Map<String,String> nodeTypes) {
		//人工活动节点列表
		List<BPMHumenActivity> result = new ArrayList<BPMHumenActivity>();
		//当前节点对应的分支条件
		String upCondition = condtionResult.get(current_node.getId());
		String preForce = null;
		if(upCondition != null && upCondition.endsWith("↗1")) {
        	upCondition = upCondition.substring(0,upCondition.indexOf("↗1"));
        	preForce = "1";
        }
		String currentIsStart= "false";
		//判断当前节点是否为发起节点
        if("start".equals(current_node.getId())){
        	currentIsStart= "true";
        }
        //获得当前节点的所有down线
        List<BPMTransition> down_links = current_node.getDownTransitions();
        //循环遍历每条down线
        for (BPMTransition down_link : down_links) {
        	//获得down线的目的节点toNode
        	BPMAbstractNode toNode = down_link.getTo();
        	//判断该down线是否为强制条件
        	String isForce = preForce==null?down_link.getIsForce():preForce;
        	//获得down线上的条件表达式
        	String currentCondition = down_link.getFormCondition();
        	//获得down线上的条件的参考对象：当前节点或发起者
	        String conditionBase = down_link.getConditionBase();
	        if(currentCondition != null){
	        	//如果分支条件不为空，则作如下处理
	        	currentCondition =
	        		currentCondition.replaceAll("isNotRole", "isnotrole").
	        		replaceAll("isRole", "isrole").replaceAll("isPost", "ispost").
	        		replaceAll("isNotPost", "isNotpost");
	        }
	        if("start".equals(conditionBase) && !"true".equals(currentIsStart)){
	        	//如果分支条件是基于发起者start，且当前处理节点不是发起节点，则作如下处理
	        	currentCondition=
	        		currentCondition.replace("[Level]", "[startlevel]").replace("[Account]", "[startaccount]")
	        		.replace("Concurrent_Acunt", "Start_ConcurrentAcunt").replace("Concurrent_Levl", "Start_ConcurrentLevl")
	        		.replace("Account,Concurrent_Acunt", "startaccount,Start_ConcurrentAcunt").replace("Level,Concurrent_Levl", "startlevel,Start_ConcurrentLevl")
	        		.replace("[StartMemberLoginAcuntLevl]", "[startStartMemberLoginAcuntLevl]");
	        	currentCondition =
	        		currentCondition.replaceAll("Department", "startdepartment").
	        		replaceAll("Post", "startpost").replaceAll("Level", "startlevel").
	        		replaceAll("team", "startTeam").replaceAll("secondpost", "startSecondpost").
	        		replaceAll("Account", "startaccount").replaceAll("standardpost", "startStandardpost").
	        		replaceAll("grouplevel", "startGrouplevel").replaceAll("Role", "startrole").
	        		replaceAll("ispost", "isStartpost").replaceAll("isNotpost", "isNotStartpost").
	        		replaceAll("isNotDep", "isNotStartDep").replaceAll("isDep", "isStartDep");
	        }
	        if("1".equals(isForce)) {
	        	//如果分支条件为强制分支，则做如下处理?
	        	String str = condtionResult.get(toNode.getId());
	        	if(str!=null&&!"".equals(str)) {
	        		String[] arr = str.split("↗");
	        		if(arr!=null) {
	        			switch(arr.length) {
	        				case 3: {
	        					isForce = null;
	        					break;
	        				}
	        			}
	        		}
	        	}
	        }else{
	        	isForce = null;
	        }
	        //根据目的节点toNode不同类型，做不同的处理
	        //获得当前节点的节点类型
			NodeType nodeType= toNode.getNodeType();
	        if ( nodeType.equals(NodeType.humen) ) {//人工活动节点(humen->humen)
	        	BPMHumenActivity hNode = (BPMHumenActivity) toNode;
                result.add(hNode);
                //然后对指向该人工活动节点的分支条件做如下处理：
                BPMSeeyonPolicy policy = hNode.getSeeyonPolicy();
                //判断该人工活动节点对应的分支条件是否已存在
                boolean isNew = condtionResult.get(hNode.getId())==null;
                //获得人工活动节点的名称
                String nodeName = hNode.getBPMAbstractNodeName();
                if(upCondition==null||"".equals(upCondition)){
                	//如果当前处理节点对应的分支条件为空
                	if(down_link.getConditionType()==1||down_link.getConditionType()==4){
                		//如果down线为自动分支或强制分支，则建立起目的节点和该分支条件的对应关系
                		condtionResult.put(hNode.getId(), nodeName+"↗"+currentCondition);
                	}else if(down_link.getConditionType()==2){
                		//如果为手动分支，则建立起目的节点和该手动分支的对应关系
                		condtionResult.put(hNode.getId(), nodeName+"↗handCondition");
                	}
                }else if(upCondition.indexOf("↗")==-1){
                	//如果当前处理节点对应的分支条件不为空
                	if(down_link.getConditionType()==1||down_link.getConditionType()==4) {
                		//如果down线为自动分支或强制分支，则建立起目的节点和该分支条件的对应关系
                		String currentConditionValue= "";
                		if(currentCondition==null||"".equals(currentCondition)){
                			currentConditionValue= nodeName+"↗("+upCondition + ")";
                		}else{
                			currentConditionValue= nodeName+"↗("+upCondition + ") && (" + currentCondition + ")";
                		}
                		condtionResult.put(hNode.getId(), currentConditionValue);
                	}else if(down_link.getConditionType()==2) {
                		//如果为手动分支，则建立起目的节点和该手动分支的对应关系
                		String currentConditionValue= "";
                		if(currentCondition==null||"".equals(currentCondition)){
                			currentConditionValue= nodeName+"↗("+upCondition + ")";
                		}else{
                			currentConditionValue= nodeName+"↗("+upCondition + ") && (handCondition)";
                		}
                		condtionResult.put(hNode.getId(), currentConditionValue);
                	}else{
                		//如果不为条件分支，则直接从上一节点继承过来
                		condtionResult.put(hNode.getId(), nodeName+"↗"+upCondition);
                	}
                }
                if(isNew) {//如果为新发现的节点，则将节点数nodeCount加1
                	if(condtionResult.get("nodeCount")==null){
            			condtionResult.put("nodeCount", "1");
            		}else {
            			int count = Integer.parseInt(condtionResult.get("nodeCount"))+1;
            			condtionResult.put("nodeCount", String.valueOf(count));
            		}
                }
                if(policy != null) {
                	//建立起人工活动节点Id与分支条件Id之间的对应关系
                	condtionResult.put("linkTo"+hNode.getId(), down_link.getId());
                }
                //记录节点的遍历顺序
                String order = condtionResult.get("order");
                if(order==null){
                	condtionResult.put("order", hNode.getId());
                }else{
                	condtionResult.put("order", order+"$"+hNode.getId());
                }
                if(isForce!=null && condtionResult.get(hNode.getId()) != null){
                	//如果为强制分支，则在分支条件表达式后面加上强制分支标志
                	condtionResult.put(hNode.getId(), condtionResult.get(hNode.getId())+"↗"+isForce);
                }
                //如果是知会节点，则如何处理?继续递归处理，直到遇到humen（非知会）为止或遇到split为出口
                if(policy != null && ("inform".equals(policy.getId()) || "zhihui".equals(policy.getId()))) {//humen->humen(知会)
                	nodeTypes.put(hNode.getId(), "inform");
                }else{
                	nodeTypes.put(hNode.getId(), "normal");
                }
//                if(log.isInfoEnabled()){
//                	log.info("nodeName:="+nodeName);
//                }
	        }else if( nodeType.equals(NodeType.split) || nodeType.equals(NodeType.join) ){//split节点(humen->split)或(humen->join)
	        	if(upCondition!=null && !"".equals(upCondition)){
	        		//如果当前处理节点对应的分支条件不为空
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4){
            			//如果为自动分支或强制分支
            			condtionResult.put(toNode.getId(), "(" + upCondition + ")" + " && (" + currentCondition + ")");
            		}else{
            			condtionResult.put(toNode.getId(), upCondition);
            		}
            	}else{
            		//如果当前处理节点对应的分支条件为空
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4){
            			//如果为自动分支或强制分支
            			condtionResult.put(toNode.getId(), currentCondition);
            		}else if(down_link.getConditionType()==2){
            			//如果为手动分支
            			condtionResult.put(toNode.getId(), "handCondition");
            		}else{
            			//如果没有分支条件
            			condtionResult.put(toNode.getId(), "");
            		}
            	}
	        	if(isForce!=null){
	        		//如果为强制分支，则加上强制分支的标志
            		condtionResult.put(toNode.getId(), condtionResult.get(toNode.getId())+"↗"+isForce);
            	}
//	        	if(log.isInfoEnabled()){
//                	log.info("nodeName:="+toNode.getName());
//                }
	        	//循环递归查找后续人工活动节点
	        	List<BPMHumenActivity> children = findDirectHumenChildren(toNode,condtionResult,nodeTypes);
                result.addAll(children);
                condtionResult.remove(toNode.getId());
	        }else if ( nodeType.equals(NodeType.end) ) {//结束节点
                return new ArrayList<BPMHumenActivity>(0);
            }
        }
		return result;
	}

	/**
	 * preRunCaseNextOfInformNodes()
	 * 根据当前知会节点列表，查找这些知会节点的直接后续人工节点列表和条件分支信息
	 * @param process 工作流 程模板定义对象
	 * @param currentNodeId 当前处理节点Id
	 * @param isFromTemplate 是否来自模板
	 * @param caseId 流程实例Id
	 * @param fieldValueMap 表单数据集合
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
	public static ProcessModeSelector preRunCaseNextOfInformNodes(BPMProcess process,
			String currentNodeId,
			boolean isFromTemplate,
			long caseId,
			Map<String, String[]> fieldValueMap,
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
			Long formAppId,Long masterId,String formData,long workItemId,long startMemberLoginAccountId) throws NumberFormatException, BusinessException, BPMException {

		Map context = new HashMap();
        context.put("process", process);
        context.put("currentWorkitemId", workItemId);
        if(fieldValueMap != null){
        	context.put("fieldValueMap", fieldValueMap) ;
        }
        BPMAbstractNode current_node = null;
        if(!"start".equals(currentNodeId)){//发起时，case不存在
			try {
				BPMCase theCase = getProcessEngine().getCase(caseId);
				context.put("case", theCase);
			} catch (BPMException e) {
				throw new ColException("流程匹配中获取流程实例异常 [caseId = " + caseId + "]", e);
			}
        	current_node = (BPMAbstractNode)process.getActivityById(currentNodeId);
        	List<BPMActor> actorList = process.getStart().getActorList();
            String sender = actorList.get(0).getParty().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }else{
        	current_node = process.getStart();
        	String sender = CurrentUser.get().getId() + "";
            context.put(V3xOrgEntity.ORGENT_META_KEY_SEDNER, sender);
        }
        WorkflowEventListener.ProcessModeSelector selector =
        	WorkflowEventListener.parseProcessModeSelectorNewOfInformNodes(current_node,
        			process,
        			context,
        			isFromTemplate,
        			startMemberId,
        			currentNodeMemberId,
        			orgManager,
        			calcResult,
//    				fieldMap,
//    				fieldDataBaseMap,
    				allNotSelectNodeList,
    				allSelectNodeList,
    				informNodeList,
    				allInformNodeList,
    				formAppId,
    				masterId,
    				formData,currentNodeId,startMemberLoginAccountId
        			);
        return selector;
	}
	/**
	 * findDirectHumenChildrenOfInformNode()
	 * 查找出指定知会节点后面的直接人工节点和条件分支信息
	 * @param current_node 当前处理节点对象
	 * @param firstInformNode 第一个知会节点对象
	 * @param myCurrNode 当前指定的知会节点对象
	 * @param condtionResult 分支条件集合
	 * @param nodeTypes 节点类型集合
	 * @param allNotSelectNodeList 在弹出页面所有没有被选中的节点集合
	 * @param allSelectNodeList 在弹出页面所有被选中的节点集合
	 * @param informNodeList 在弹出页面所有被选中的知会节点集合
	 * @param context 上下文信息
	 * @param allChildrenList 所有已经查找到的人工节点
	 * @return 指定知会节点后面的直接人工活动节点列表
	 * @throws BPMException
	 */
	public static List<BPMHumenActivity> findDirectHumenChildrenOfInformNode(
			BPMAbstractNode current_node,
			BPMActivity firstInformNode,
			BPMActivity myCurrNode,
			HashMap<String, String> condtionResult,
			HashMap<String, String> nodeTypes,
			List<String> allNotSelectNodeList,
			List<String> allSelectNodeList,
			List<String> informNodeList,
			List<String> allInformNodeList,
			Map context,
			List<String> allChildrenList
			) throws BPMException {

		//人工活动节点列表
		List<BPMHumenActivity> result = new ArrayList<BPMHumenActivity>();
		//当前节点对应的分支条件
		String upCondition = condtionResult.get(myCurrNode.getId());
		String preForce = null;
		if(upCondition != null && upCondition.endsWith("↗1")) {
        	upCondition = upCondition.substring(0,upCondition.indexOf("↗1"));
        	preForce = "1";
        }
		String currentIsStart= "false";
		//判断当前节点是否为发起节点
        if("start".equals(current_node.getId())){
        	currentIsStart= "true";
        }
        //获得当前节点的所有down线
        List<BPMTransition> down_links = myCurrNode.getDownTransitions();
        //循环遍历每条down线
        for (BPMTransition down_link : down_links) {
        	//获得down线的目的节点toNode
        	BPMAbstractNode toNode = down_link.getTo();
        	//判断该down线是否为强制条件
        	String isForce = preForce==null?down_link.getIsForce():preForce;
        	//获得down线上的条件表达式
        	String currentCondition = down_link.getFormCondition();
        	//获得down线上的条件的参考对象：当前节点或发起者
	        String conditionBase = down_link.getConditionBase();
	        if(currentCondition != null){
	        	//如果分支条件不为空，则作如下处理
	        	currentCondition =
	        		currentCondition.replaceAll("isNotRole", "isnotrole").
	        		replaceAll("isRole", "isrole").replaceAll("isPost", "ispost").
	        		replaceAll("isNotPost", "isNotpost");
	        }
//	        if("start".equals(conditionBase) && !"true".equals(currentIsStart)){
//	        	//如果分支条件是基于发起者start，且当前处理节点不是发起节点，则作如下处理
//	        	currentCondition =
//	        		currentCondition.replaceAll("Department", "startdepartment").
//	        		replaceAll("Post", "startpost").replaceAll("Level", "startlevel").
//	        		replaceAll("team", "startTeam").replaceAll("secondpost", "startSecondpost").
//	        		replaceAll("Account", "startaccount").replaceAll("standardpost", "startStandardpost").
//	        		replaceAll("grouplevel", "startGrouplevel").replaceAll("Role", "startrole").
//	        		replaceAll("ispost", "isStartpost").replaceAll("isNotpost", "isNotStartpost").
//	        		replaceAll("isNotDep", "isNotStartDep").replaceAll("isDep", "isStartDep");
//	        }
	        if("start".equals(conditionBase) && !"true".equals(currentIsStart)){
                //如果分支条件是基于发起者start，且当前处理节点不是发起节点，则作如下处理
                currentCondition=
                    currentCondition.replace("[Level]", "[startlevel]").replace("[Account]", "[startaccount]")
                    .replace("Concurrent_Acunt", "Start_ConcurrentAcunt").replace("Concurrent_Levl", "Start_ConcurrentLevl")
                    .replace("Account,Concurrent_Acunt", "startaccount,Start_ConcurrentAcunt").replace("Level,Concurrent_Levl", "startlevel,Start_ConcurrentLevl")
                    .replace("[StartMemberLoginAcuntLevl]", "[startStartMemberLoginAcuntLevl]");
                currentCondition =
                    currentCondition.replaceAll("Department", "startdepartment").
                    replaceAll("Post", "startpost").replaceAll("Level", "startlevel").
                    replaceAll("team", "startTeam").replaceAll("secondpost", "startSecondpost").
                    replaceAll("Account", "startaccount").replaceAll("standardpost", "startStandardpost").
                    replaceAll("grouplevel", "startGrouplevel").replaceAll("Role", "startrole").
                    replaceAll("ispost", "isStartpost").replaceAll("isNotpost", "isNotStartpost").
                    replaceAll("isNotDep", "isNotStartDep").replaceAll("isDep", "isStartDep");
            }
	        if(!"1".equals(isForce)){
	        	isForce = null;
	        }
	        //获得当前节点的节点类型
			NodeType nodeType= toNode.getNodeType();
			//根据目的节点toNode不同类型，做不同的处理
	        if ( nodeType.equals(NodeType.humen) ) {//humen(知会)->humen
	        	BPMHumenActivity hNode = (BPMHumenActivity) toNode;
	        	if(!allChildrenList.contains(hNode.getId())){
	        		 result.add(hNode);
	        	}
                //然后对指向该人工活动节点的分支条件做如下处理：
                BPMSeeyonPolicy policy = hNode.getSeeyonPolicy();
                //判断该人工活动节点对应的分支条件是否已存在
                boolean isNew = condtionResult.get(hNode.getId())==null;
                //获得人工活动节点的名称
                String nodeName = hNode.getBPMAbstractNodeName();
                if(upCondition==null||"".equals(upCondition)){
                	//如果当前处理节点对应的分支条件为空
                	if(down_link.getConditionType()==1||down_link.getConditionType()==4){
                		//如果down线为自动分支或强制分支，则建立起目的节点和该分支条件的对应关系
                		condtionResult.put(hNode.getId(), nodeName+"↗"+currentCondition);
                	}else if(down_link.getConditionType()==2){
                		//如果为手动分支，则建立起目的节点和该手动分支的对应关系
                		condtionResult.put(hNode.getId(), nodeName+"↗handCondition");
                	}
                }else if(upCondition.indexOf("↗")==-1){
                	//如果当前处理节点对应的分支条件不为空
                	if(down_link.getConditionType()==1||down_link.getConditionType()==4) {
                		//如果down线为自动分支或强制分支，则建立起目的节点和该分支条件的对应关系
                		String currentConditionValue= "";
                		if(currentCondition==null||"".equals(currentCondition)){
                			currentConditionValue= nodeName+"↗("+upCondition + ")";
                		}else{
                			currentConditionValue= nodeName+"↗("+upCondition + ") && (" + currentCondition + ")";
                		}
                		condtionResult.put(hNode.getId(), currentConditionValue);
                	}else if(down_link.getConditionType()==2) {
                		//如果为手动分支，则建立起目的节点和该手动分支的对应关系
                		String currentConditionValue= "";
                		if(currentCondition==null||"".equals(currentCondition)){
                			currentConditionValue= nodeName+"↗("+upCondition + ")";
                		}else{
                			currentConditionValue= nodeName+"↗("+upCondition + ") && (handCondition)";
                		}
                		condtionResult.put(hNode.getId(), currentConditionValue);
                	}else{
                		//如果不为条件分支，则直接从上一节点继承过来
                		condtionResult.put(hNode.getId(), nodeName+"↗"+upCondition);
                	}
                }
                if(isNew) {//如果为新发现的节点，则将节点数nodeCount加1
                	if(condtionResult.get("nodeCount")==null){
            			condtionResult.put("nodeCount", "1");
            		}else {
            			int count = Integer.parseInt(condtionResult.get("nodeCount"))+1;
            			condtionResult.put("nodeCount", String.valueOf(count));
            		}
                }
                if(policy != null) {
                	//建立起人工活动节点Id与分支条件Id之间的对应关系
                	condtionResult.put("linkTo"+hNode.getId(), down_link.getId());
                }
                //记录节点的遍历顺序
                String order = condtionResult.get("order");
                if(order==null){
                	condtionResult.put("order", hNode.getId());
                }else{
                	condtionResult.put("order", order+"$"+hNode.getId());
                }
                if(isForce!=null && condtionResult.get(hNode.getId()) != null){
                	//如果为强制分支，则在分支条件表达式后面加上强制分支标志
                	condtionResult.put(hNode.getId(), condtionResult.get(hNode.getId())+"↗"+isForce);
                }
                //如果是知会节点，则如何处理?继续递归处理，直到遇到humen（非知会）为止或遇到split为出口
                if(policy != null && ("inform".equals(policy.getId()) || "zhihui".equals(policy.getId()))) {//humen->humen(知会)
                	nodeTypes.put(hNode.getId(), "inform");
                }else{
                	nodeTypes.put(hNode.getId(), "normal");
                }
//                if(log.isInfoEnabled()){
//                	log.info("nodeName:="+nodeName);
//                }
	        }else if( nodeType.equals(NodeType.split)){//humen(知会)->split
	        	if(upCondition!=null && !"".equals(upCondition)){
	        		//如果当前处理节点对应的分支条件不为空
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4){
            			//如果为自动分支或强制分支
            			condtionResult.put(toNode.getId(), "(" + upCondition + ")" + " && (" + currentCondition + ")");
            		}else{
            			condtionResult.put(toNode.getId(), upCondition);
            		}
            	}else{
            		//如果当前处理节点对应的分支条件为空
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4){
            			//如果为自动分支或强制分支
            			condtionResult.put(toNode.getId(), currentCondition);
            		}else if(down_link.getConditionType()==2){
            			//如果为手动分支
            			condtionResult.put(toNode.getId(), "handCondition");
            		}else{
            			//如果没有分支条件
            			condtionResult.put(toNode.getId(), "");
            		}
            	}
	        	if(isForce!=null){
	        		//如果为强制分支，则加上强制分支的标志
            		condtionResult.put(toNode.getId(), condtionResult.get(toNode.getId())+"↗"+isForce);
            	}
//	        	if(log.isInfoEnabled()){
//                	log.info("nodeName:="+toNode.getName());
//                }
	        	//循环递归查找后续人工活动节点
	        	List<BPMHumenActivity> children =
	        		findDirectHumenChildrenOfInformNode(current_node,
	        				firstInformNode,
	        				(BPMActivity)toNode,
	        				condtionResult,
	        				nodeTypes,
	        				allNotSelectNodeList,
	        				allSelectNodeList,
	        				informNodeList,
	        				allInformNodeList,
	        				context,
	        				allChildrenList);
                result.addAll(children);
                condtionResult.remove(toNode.getId());
	        }else if( nodeType.equals(NodeType.join) ){//humen(知会)->join
	        	if(upCondition!=null && !"".equals(upCondition)){
	        		//如果当前处理节点对应的分支条件不为空
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4){
            			//如果为自动分支或强制分支
            			condtionResult.put(toNode.getId(), "(" + upCondition + ")" + " && (" + currentCondition + ")");
            		}else{
            			condtionResult.put(toNode.getId(), upCondition);
            		}
            	}else{
            		//如果当前处理节点对应的分支条件为空
            		if(down_link.getConditionType()==1 || down_link.getConditionType()==4){
            			//如果为自动分支或强制分支
            			condtionResult.put(toNode.getId(), currentCondition);
            		}else if(down_link.getConditionType()==2){
            			//如果为手动分支
            			condtionResult.put(toNode.getId(), "handCondition");
            		}else{
            			//如果没有分支条件
            			condtionResult.put(toNode.getId(), "");
            		}
            	}
	        	if(isForce!=null){
	        		//如果为强制分支，则加上强制分支的标志
            		condtionResult.put(toNode.getId(), condtionResult.get(toNode.getId())+"↗"+isForce);
            	}
//	        	if(log.isInfoEnabled()){
//                	log.info("nodeName:="+toNode.getName());
//                }
	        	//判断是否需要穿过该join节点，往后继续查找
	        	if(log.isInfoEnabled()){
	        		log.info("判断是否需要穿过该join节点，往后继续查找");
	        	}
	        	boolean isCanPass= isCanPassWithJoin(toNode, firstInformNode, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
//	        	if(isCanPass){
//	        	    if(null!=informNodeList && informNodeList.size()>0){
//	        	        List<String> notArrivedNodeIds= getNotArrivedNodeIds(toNode,allNotSelectNodeList);
//	        	        isCanPass= isAllInformNodeDirectArrowToJoin(allInformNodeList,toNode,allNotSelectNodeList);
//	        	    }
//	        	}
	        	if(isCanPass){//可以穿过该join节点
	        		if(log.isInfoEnabled()){
		        		log.info("穿过该join节点，往后继续查找。");
		        	}
	        		//循环递归查找后续人工活动节点
	        		List<BPMHumenActivity> children =
		        		findDirectHumenChildrenOfInformNode(current_node,
		        				firstInformNode,
		        				(BPMActivity)toNode,
		        				condtionResult,
		        				nodeTypes,
		        				allNotSelectNodeList,
		        				allSelectNodeList,
		        				informNodeList,
		        				allInformNodeList,
		        				context,
		        				allChildrenList
		        		);
	                result.addAll(children);
	                condtionResult.remove(toNode.getId());
	        	}else{
	        		return new ArrayList<BPMHumenActivity>(0);
	        	}
	        }else if ( nodeType.equals(NodeType.end) ) {//结束节点
                return new ArrayList<BPMHumenActivity>(0);
            }
        }
		return result;
	}

	/**
	 * 是否所有选中的知会节点与join节点之间没有人工活动节点和split节点，即只有join节点，如果都是则返回true，否则返回false
	 * @param informNodeList
	 * @param toNode
	 * @return
	 */
	private static boolean isAllInformNodeDirectArrowToJoin(List<String> allInformNodeList, BPMAbstractNode toNode,List<String> allNotSelectNodeList) {
	    List<BPMTransition> joinUps= toNode.getUpTransitions();
        for (BPMTransition bpmTransition2 : joinUps) {
            BPMAbstractNode fromNode= bpmTransition2.getFrom();
            String isDelete= fromNode.getSeeyonPolicy().getIsDelete();
            String nodeId= fromNode.getId();
            if("false".equals(isDelete)){
                if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.humen)){
                    if(!allInformNodeList.contains(nodeId) && !allNotSelectNodeList.contains(nodeId)){
                        return false;
                    }
                }else if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.join)){
                    return isAllInformNodeDirectArrowToJoin(allInformNodeList, fromNode,allNotSelectNodeList);
                }
            }
        }
        return true;
    }
    /**
	 * isCanPassWithJoin()
	 * 判断是否可以穿过该join节点，这是一个递归判断过程
	 * @param toNode join节点
	 * @param current_node 当前处理节点
	 * @param allNotSelectNodeList 在弹出页面所有没被选中的节点集合
	 * @param allSelectNodeList 在弹出页面中所有被选中的节点集合
	 * @param informNodeList 在弹出页面中所有被选中的知会节点集合
	 * @param context 上下文信息
	 * @return true：可以穿过；false：不可以穿过
	 * @throws BPMException
	 */
	private static boolean isCanPassWithJoin(
			BPMAbstractNode toNode,
			BPMActivity current_node,
			List<String> allNotSelectNodeList,
			List<String> allSelectNodeList,
			List<String> informNodeList,
			List<String> allInformNodeList,
			Map context) throws BPMException {
		//默认可以穿过
    	boolean isCanPass= true;
    	//获得标识知会的两个常量
        String informActivityPolicy = BPMSeeyonPolicy.SEEYON_POLICY_INFORM.getId();
    	String edocInformActivityPolicy = BPMSeeyonPolicy.EDOC_POLICY_ZHIHUI.getId();
    	//获得该join节点的所有up线
    	List<BPMTransition> joinUps= toNode.getUpTransitions();
    	for (BPMTransition bpmTransition2 : joinUps) {
    		BPMAbstractNode fromNode= bpmTransition2.getFrom();
    		String nodeId= fromNode.getId();
    		String fromNodePolicy = fromNode.getSeeyonPolicy().getId();
			String isDelete= fromNode.getSeeyonPolicy().getIsDelete();
			//System.out.println("nodeId:="+nodeId);
			//System.out.println("fromNodePolicy:="+fromNodePolicy);
			//System.out.println("isDelete:="+isDelete);
			if("false".equals(isDelete) && !current_node.getId().equals(nodeId) && isCanPass){//from节点没有被删除，且不是当前处理节点
    			if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.humen)){//humen->split
    				BPMHumenActivity fromHumenNodeOfcurrBackNode = (BPMHumenActivity)fromNode;
    				String currFromHumenNodePolicy = fromHumenNodeOfcurrBackNode.getSeeyonPolicy().getId();
    	        	//计算出当前from节点是否为知会节点
    	        	boolean isInformNode = currFromHumenNodePolicy.equals(informActivityPolicy) || currFromHumenNodePolicy.equals(edocInformActivityPolicy);
        			//看是否被选中
    				if(allSelectNodeList.contains(nodeId)){//选中
    					//判断fromNode节点是否为选中的知会节点
    					if(allInformNodeList.contains(nodeId)){//是选中的知会节点
    						//继续，对isCanPass=true没有影响
    						continue;
    					}else{//不是选中的知会节点，则肯定是选中的非知会节点(则不让穿过该join节点)
    						isCanPass= false;
    					}
    				}else if(allNotSelectNodeList.contains(nodeId)){//没选中
    					if(isInformNode){//是没选中的知会节点
//    						isCanPass= isCanPassWithJoin(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,context);
    						//继续，对isCanPass=true没有影响
    						continue;
						}else{//是没选中的非知会节点(则不让穿过该join节点)
							//继续，对isCanPass=true没有影响
    						continue;
						}
    				}else{
    					BPMCase theCase= null;
    					if(context.get("case")!=null){
    						theCase= (BPMCase)context.get("case");
    					}
    					//判断该humen节点是否已产生待办
//    					boolean isDoing= isDoingWithHumen(theCase,nodeId);
//    					boolean isDone= isDoneWithHumen(theCase,nodeId);
    					boolean isDoing= false;
    					boolean isDone= false;
    					if(null!=theCase){
    						isDoing= Utils.isThisState(theCase, nodeId, CaseDetailLog.STATE_READY);
        					isDone= Utils.isThisState(theCase, nodeId, CaseDetailLog.STATE_FINISHED,
        							CaseDetailLog.STATE_CANCEL,CaseDetailLog.STATE_STOP);
    					}
    					if(log.isInfoEnabled()){
    						log.info("isDoing:="+isDoing);
    						log.info("isDone:="+isDone);
    					}
    					if(isDoing){//如果是待办状态
    						if(isInformNode){//是知会节点
    							continue;
    						}else{
    							isCanPass= false;
    						}
    					}else if(isDone){//如果是办已办状态
    						continue;
    					}else{//即没有产生待办，也没有产生已办
    						if(isInformNode){//是知会节点
//    							isCanPass= isCanPassWithJoin(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
    							isCanPass= isCanPassWithNonInformNode(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
    						}else{//不是知会节点
//    							isCanPass= isCanPassWithJoin(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,context); 2012-2-7
    							isCanPass= isCanPassWithNonInformNode(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
    						}
    					}
    				}
        		}
        		if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.join)){//join->join
        			//以fromNode为基础递归往后查找
        			isCanPass= isCanPassWithJoin(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
        		}
        		if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.split)){//split->中间穿过一些知会节点->join
        			//以fromNode为基础递归往后查找
        			isCanPass= isCanPassWithJoin(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
        		}
    		}
			if(!isCanPass){
				break;
			}
    	}
    	return isCanPass;
	}

	/**
	 * 判断该非知会节点是否可以被穿过
	 * @param fromNode
	 * @param current_node
	 * @param allNotSelectNodeList
	 * @param allSelectNodeList
	 * @param informNodeList
	 * @param context
	 * @return
	 */
	private static boolean isCanPassWithNonInformNode(BPMAbstractNode toNode,
			BPMActivity current_node, List<String> allNotSelectNodeList,
			List<String> allSelectNodeList, List<String> informNodeList,List<String> allInformNodeList,
			Map context) {
		//默认可以穿过
    	boolean isCanPass= true;
    	//获得标识知会的两个常量
        String informActivityPolicy = BPMSeeyonPolicy.SEEYON_POLICY_INFORM.getId();
    	String edocInformActivityPolicy = BPMSeeyonPolicy.EDOC_POLICY_ZHIHUI.getId();
    	//获得该join节点的所有up线
    	List<BPMTransition> joinUps= toNode.getUpTransitions();
    	for (BPMTransition bpmTransition2 : joinUps) {
    		BPMAbstractNode fromNode= bpmTransition2.getFrom();
    		String nodeId= fromNode.getId();
    		String fromNodePolicy = fromNode.getSeeyonPolicy().getId();
			String isDelete= fromNode.getSeeyonPolicy().getIsDelete();
			if(isCanPass){
				if("false".equals(isDelete) && !current_node.getId().equals(nodeId)){//from节点没有被删除，且不是当前处理节点
					if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.humen)){//humen->split
						BPMHumenActivity fromHumenNodeOfcurrBackNode = (BPMHumenActivity)fromNode;
	    				String currFromHumenNodePolicy = fromHumenNodeOfcurrBackNode.getSeeyonPolicy().getId();
	    	        	//计算出当前from节点是否为知会节点
	    	        	boolean isInformNode = currFromHumenNodePolicy.equals(informActivityPolicy) || currFromHumenNodePolicy.equals(edocInformActivityPolicy);
	        			//看是否被选中
	    	        	if(allSelectNodeList.contains(nodeId)){//选中
	    	        		//判断fromNode节点是否为选中的知会节点
	    					if(allInformNodeList.contains(nodeId)){//是选中的知会节点(则不让穿过该join节点)
	    						isCanPass= false;
	    					}else{//不是选中的知会节点，则肯定是选中的非知会节点(则不让穿过该join节点)
	    						isCanPass= false;
	    					}
	    	        	}else if(allNotSelectNodeList.contains(nodeId)){//没选中
	    					if(isInformNode){//是没选中的知会节点
	    						//继续，对isCanPass=true没有影响
	    						continue;
							}else{//是没选中的非知会节点
								//继续，对isCanPass=true没有影响
	    						continue;
							}
	    				}else{
	    					BPMCase theCase= null;
	    					if(context.get("case")!=null){
	    						theCase= (BPMCase)context.get("case");
	    					}
	    					//判断该humen节点是否已产生待办
	    					boolean isDoing= false;
	    					boolean isDone= false;
	    					if(null!=theCase){
	    						isDoing= Utils.isThisState(theCase, nodeId, CaseDetailLog.STATE_READY);
	        					isDone= Utils.isThisState(theCase, nodeId, CaseDetailLog.STATE_FINISHED,
	        							CaseDetailLog.STATE_CANCEL,CaseDetailLog.STATE_STOP);
	    					}
	    					if(log.isInfoEnabled()){
	    						log.info("isDoing:="+isDoing);
	    						log.info("isDone:="+isDone);
	    					}
	    					if(isDoing){//如果是待办状态
	    						if(isInformNode){//是知会节点
	    							isCanPass= false;
	    						}else{
	    							isCanPass= false;
	    						}
	    					}else if(isDone){//如果是已办状态
	    						isCanPass= false;
	    					}else{//即没有产生待办，也没有产生已办，递归继续
	    						isCanPass= isCanPassWithNonInformNode(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
	    					}
	    				}
					}
					if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.join)){//join->非知会节点
						isCanPass= isCanPassWithNonInformNode(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
					}
					if(fromNode.getNodeType().equals(BPMAbstractNode.NodeType.split)){//split->中间穿过一些知会节点->非知会节点
						isCanPass= isCanPassWithNonInformNode(fromNode, current_node, allNotSelectNodeList, allSelectNodeList, informNodeList,allInformNodeList,context);
					}
				}else if("false".equals(isDelete) && current_node.getId().equals(nodeId) ){//是当前处理节点
					isCanPass= false;
				}
			}
    	}
		return isCanPass;
	}
	/**
	 * isDoneWithHumen()
	 * 判断指定的流程节点是否已产生已办事项
	 * @param theCase 流程实例对象
	 * @param nodeId 指定的流程节点
	 * @return true:产生了已办事项；false：没有产生已办事项
	 * @throws BPMException
	 */
	private static boolean isDoneWithHumen(BPMCase theCase,String nodeId) throws BPMException {
		if( theCase == null ){//还未产生流程实例,说明当前处理节点为开始节点
			return false;
		}
		//获得流程实例Id
		long caseId= theCase.getId();
		//System.out.println("caseId:="+caseId);
		//根据nodeId和caseId从workitem_history表中判断看是否还有记录存在
		List workitemHisList= BranchArgs.getWorkitemList(nodeId,caseId,"1");
		if(workitemHisList.size()>0){
			//如果在workitem_history表中存在记录，则说明该流程节点处于已办状态
			return true;
		}
		return false;
	}

	/**
	 * isDoingWithHumen()
	 * 判断指定的流程节点是否已产生待办事项
	 * @param theCase 流程实例对象
	 * @param nodeId 指定的流程节点
	 * @return true:产生了待办事项；false：没有产生待办事项
	 * @throws BPMException
	 */
	private static boolean isDoingWithHumen(BPMCase theCase,String nodeId) throws BPMException {
		if( theCase == null ){//还未产生流程实例,说明当前处理节点为开始节点
			return false;
		}
		//获得流程实例Id
		long caseId= theCase.getId();
		//System.out.println("caseId:="+caseId);
		//根据nodeId和caseId从workitem_run表中判断看是否还有记录存在
		List workitemRunList= BranchArgs.getWorkitemList(nodeId,caseId,"0");
		if(workitemRunList.size()>0){
			//如果在workitem_run表中存在记录，则说明该流程节点处于待办状态
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param processName
	 * @param senderId
	 * @param data
	 * @return
	 * @throws ColException 
	 */
	public static long runCaseWithContext(String processName, Long senderId,
			Map<String, Object> data) throws ColException {
		if(log.isInfoEnabled()){
			log.info("runCaseWithContext......");
		}
		try {
            ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
            String caseName = UUIDLong.longUUID() + "";
            //processName = "activity_status";
            Map dataMap=new HashMap();
            dataMap.put("appName",ApplicationCategoryEnum.collaboration.name());
            dataMap.putAll(data);//表单数据
            long caseId = engine.runCase(String.valueOf(senderId), processName,
                    caseName, dataMap, null, null, false);
            
            return caseId;
        } catch (Exception ex) {
        	throw new ColException("获取引擎对外接口异常[ColHelper.runCase]", ex);
        }
	}
	
	/**
	 * 
	 * @param workItemId
	 * @param data
	 * @throws ColException 
	 */
	public static void finishWorkitemWithContext(Long workItemId,
			Map<String, Object> data) throws ColException {
		if(log.isInfoEnabled()){
			log.info("finishWorkitemWithContext......");
		}
		String userId = CurrentUser.get().getId() + "";
        try {
        	WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
            //wim.finishWorkItem(userId, workItemId, null, null, null, null);
            wim.finishWorkItem(userId, workItemId, null, null, data, null);
        } catch (BPMException ex) {
        	if(BPMException.EXCEPTION_CODE_WORKITEM_NOT_EXITE.equals(ex.getExceptionCode())){
        		log.warn("可能是数据到了历史表，重新移到运行表中，尝试重新提交一次。");
        		try{
	        		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
	        		WorkItem wi= wim.getHistortyWorkItemInfo(userId, workItemId);
	        		if(null!=wi){
	        			wim.moveWorkitemToRun(workItemId);
	        		}
	        		//再尝试提交一次
	        		wim.finishWorkItem(userId, workItemId, null, null, data, null);
        		}catch(BPMException ex1){
        			log.error(ex1);
    	        	throw new ColException("获取工作项管理对外接口异常[ColHelper.finishWorkitem]", ex1);
        		}
        	}else{
	        	log.error(ex);
	        	throw new ColException("获取工作项管理对外接口异常[ColHelper.finishWorkitem]", ex);
        	}
        }
	}
	
	public static void deleteWorkflow(String processId, long caseId){
		try {
			ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
			BPMCase theCase = engine.getCase(caseId);
			
			deleteWorkflow(processId, theCase);
		}
		catch (Exception e1) {
			log.error("", e1);
			return;
		}
	}
	
	public static void deleteWorkflow(String processId, BPMCase theCase){
		String userId = null;
		try {
			WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
			ProcessEngine engine = WAPIFactory.getProcessEngine("Engine_1");
			
			if(theCase != null){
				long caseId = theCase.getId();
				
				wim.removeWorkitem(caseId);
				wim.removeHistoryWorkitem(caseId);
				
				engine.deleteCase(theCase);
			}
			
			engine.deleteProcess(processId);
		}
		catch (Exception e1) {
			log.error("", e1);
			return;
		}
	}
	
	public static void saveWorkitem(WorkitemDAO workitem) throws Exception {
		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
		wim.updateWorkItem(workitem);
	}
	
	private static BPMSeeyonPolicy createBPMSeeyonPolicy(BPMSeeyonPolicy examplePolicy) {
    	BPMSeeyonPolicy bpmPolicy= new BPMSeeyonPolicy(examplePolicy.getId(), examplePolicy.getName());
    	bpmPolicy.setAdded(examplePolicy.isAdded());
    	bpmPolicy.setdealTerm(examplePolicy.getdealTerm());
    	bpmPolicy.setDesc(examplePolicy.getDesc());
    	bpmPolicy.setForm(examplePolicy.getForm());
    	bpmPolicy.setFormApp(examplePolicy.getFormApp());
    	bpmPolicy.setFormField(examplePolicy.getFormField());
    	bpmPolicy.setFR(examplePolicy.getFR());
    	bpmPolicy.setIsColAssign(examplePolicy.getIsColAssign());
    	bpmPolicy.setIsDelete(examplePolicy.getIsDelete());
    	bpmPolicy.setIsOvertopTime(examplePolicy.getIsOvertopTime());
    	bpmPolicy.setIsPass(examplePolicy.getIsPass());
    	bpmPolicy.setMatchScope(examplePolicy.getMatchScope());
    	bpmPolicy.setNF(examplePolicy.getNF());
    	bpmPolicy.setOperationName(examplePolicy.getOperationName());
    	bpmPolicy.setProcessMode(examplePolicy.getProcessMode());
    	bpmPolicy.setRemindTime(examplePolicy.getRemindTime());
    	return bpmPolicy;
	}
	
	/**
     * 根据选人界面生成/更新流程，但不会放到内存变量中
     * 如果processId为null则为添加模板，否则为更新
     * @param flowData
     * @param processId
     * @param isTemplate
     * @return 生成的BPMProcess对象
     * @throws ColException
     */
    public static BPMProcess getBPMProcessByPeople(FlowData flowData, String processId, boolean isTemplate) throws ColException {
        User user = CurrentUser.get();
        List<Party> people = flowData.getPeople();
        int flowType = flowData.getType();
        String isShowShortName = flowData.getIsShowShortName();
        boolean isNewProcess = (processId == null);
        if (isNewProcess)
            processId = UUIDLong.longUUID() + "";

        BPMProcess process = null;

        //不论isNewProcess为true/false，均新建一个process实例。如果不为新，则最后update(delete & create)它�?
        process = new BPMProcess(processId, processId);
        process.setIndex(processId);
        
        process.setIsShowShortName(isShowShortName);
        BPMAbstractNode nodeStart = new BPMStart("start", user.getName());
        
        //构造发起人Actor
        Long startId = user.getId();
        String startName = user.getName();
        Long accountId = user.getLoginAccount();
        OrgManager orgManager = getOrgManager();
        String accountShortName = "";
		try {
			V3xOrgAccount account = orgManager.getAccountById(accountId);
			accountShortName = account.getShortname();
		} catch (BusinessException e) {
			log.error("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
			throw new ColException("保存或更新process时获取单位信息异常 [accountId = " + accountId + "]", e);
		}
		Party startParty = new Party("user", startId.toString(), startName, accountId.toString(), accountShortName);
        BPMActor startUserActor = createActor(startParty);
        nodeStart.addActor(startUserActor);
        BPMSeeyonPolicy seeyonPolicy = new BPMSeeyonPolicy();
        seeyonPolicy.setMatchScope("1");
        seeyonPolicy.setProcessMode("multiple");
        seeyonPolicy.setIsDelete("false");
        seeyonPolicy.setIsPass("success");
        if(flowData.getSeeyonPolicy() != null)
        	seeyonPolicy.setId(flowData.getSeeyonPolicy().getId());
        nodeStart.setSeeyonPolicy(seeyonPolicy);
        
        BPMAbstractNode nodeEnd = new BPMEnd("end", "end");
        
        process.addChild(nodeStart);
        process.addChild(nodeEnd);

        if (flowType == FlowData.FLOWTYPE_SERIAL || (flowType == FlowData.FLOWTYPE_PARALLEL && people.size() == 1)) {
            BPMAbstractNode prevNode = null;
            for (int i = 0; i < people.size(); i++) {
                Party party = people.get(i);

                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.getName());
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);
                
                //设置用户节点属性
                seeyonPolicy = new BPMSeeyonPolicy();
                if(userNode.getSeeyonPolicy() == null){
                	if(flowData.getSeeyonPolicy() != null){
                		BPMSeeyonPolicy _seeyonPolicy = flowData.getSeeyonPolicy();
                		seeyonPolicy.setId(_seeyonPolicy.getId());
                		seeyonPolicy.setName(_seeyonPolicy.getName());
                	}else{
                		seeyonPolicy.setId("collaboration");
                		seeyonPolicy.setName("协同");
                	}
                	
                	String actorType = userActor.getParty().getType().id;
                	if(isTemplate || "user".equals(actorType)){
                		seeyonPolicy.setProcessMode("single");
                    }else{
                    	seeyonPolicy.setProcessMode("all");
                    }
                	userNode.setSeeyonPolicy(seeyonPolicy);
                }
                
                BPMTransition userLink = null;
                if (prevNode == null) {
                    userLink = new BPMTransition(nodeStart, userNode);
                } else {
                    userLink = new BPMTransition(prevNode, userNode);
                }

                process.addChild(userNode);
                process.addLink(userLink);

                prevNode = userNode;
            }
            prevNode = prevNode != null ? prevNode : nodeStart;
            BPMTransition linkLastNodeToEnd = new BPMTransition(prevNode, nodeEnd);
            process.addLink(linkLastNodeToEnd);
        } else {
        	BPMAndRouter nodeSyncStart = new BPMAndRouter(UUIDLong.longUUID()+"", "split");
            BPMAndRouter nodeSyncEnd = new BPMAndRouter(UUIDLong.longUUID()+"", "join");
            nodeSyncStart.setStartAnd(true);
            nodeSyncEnd.setStartAnd(false);
            String relevancyId = UUIDLong.longUUID() + "";
            nodeSyncStart.setParallelismNodeId(relevancyId);
            nodeSyncEnd.setParallelismNodeId(relevancyId);
            
            BPMSeeyonPolicy newSeeyonPolicy = new BPMSeeyonPolicy();
            newSeeyonPolicy.setProcessMode("all");
            newSeeyonPolicy.setMatchScope("1");
            newSeeyonPolicy.setIsDelete("false");
            newSeeyonPolicy.setIsPass("success");
            if(flowData.getSeeyonPolicy() != null)
            	newSeeyonPolicy.setId(flowData.getSeeyonPolicy().getId());
            nodeSyncStart.setSeeyonPolicy(newSeeyonPolicy);
            nodeSyncEnd.setSeeyonPolicy(newSeeyonPolicy);

            BPMTransition linkStartToSyncStart = new BPMTransition(nodeStart, nodeSyncStart);
            BPMTransition linkSyncEndToEnd = new BPMTransition(nodeSyncEnd, nodeEnd);
            process.addChild(nodeSyncStart);
            process.addChild(nodeSyncEnd);
            process.addLink(linkStartToSyncStart);
            process.addLink(linkSyncEndToEnd);
            
            for (int i=(people.size()-1); i>=0; i--) {
                Party party = people.get(i);
                
                BPMAbstractNode userNode = new BPMHumenActivity(UUIDLong.longUUID() + "", party.getName());
                BPMActor userActor = createActor(party);
                userNode.addActor(userActor);
                
                //设置用户节点属性
                seeyonPolicy = new BPMSeeyonPolicy();
                if(userNode.getSeeyonPolicy() == null){
                	if(flowData.getSeeyonPolicy() != null){
                		BPMSeeyonPolicy _seeyonPolicy = flowData.getSeeyonPolicy();
                		seeyonPolicy.setId(_seeyonPolicy.getId());
                		seeyonPolicy.setName(_seeyonPolicy.getName());
                	}else{
                		seeyonPolicy.setId("collaboration");
                		seeyonPolicy.setName("协同");
                	}
                	
                	
                	String actorType = userActor.getParty().getType().id;
                	if(isTemplate || "user".equals(actorType)){
                		seeyonPolicy.setProcessMode("single");
                    }else{
                    	seeyonPolicy.setProcessMode("all");
                    }
                	userNode.setSeeyonPolicy(seeyonPolicy);
                }

                BPMTransition userLink1 = new BPMTransition(nodeSyncStart, userNode);
                BPMTransition userLink2 = new BPMTransition(userNode, nodeSyncEnd);

                process.addChild(userNode);
                process.addLink(userLink1);
                process.addLink(userLink2);
            }
        }

        Date now = new Date(System.currentTimeMillis());

        if (isNewProcess) {
            process.setCreateDate(now);
            process.setUpdateDate(now);

        } else {
            process.setUpdateDate(now);
        }
//        try {
//            ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
//            pdm.saveOrUpdateProcessInReady(process);
//        } catch (Exception ex) {
//        	throw new ColException("获取引擎对外接口异常", ex);
//        }
//        return process.getId();
        return process;
    }
	public static void finishItem(Long workItemId) {
		try {
			WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
			wim.moveWorkitemToHistory(workItemId);
		} catch (BPMException e) {
			log.warn(e);
		}
	}
}