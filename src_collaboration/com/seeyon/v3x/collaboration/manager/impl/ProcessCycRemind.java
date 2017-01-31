package com.seeyon.v3x.collaboration.manager.impl;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItemManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;


public class ProcessCycRemind implements Job, QuartzJob{
	private final static Log log = LogFactory.getLog(ProcessCycRemind.class);
	
	private ColManager colManager;
	private EdocSummaryDao edocSummaryDao;
	private UserMessageManager userMessageManager;
	private ColSuperviseManager colSuperviseManager;
	private AffairManager affairManager;
	private OrgManager orgManager;
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao) {
		this.edocSummaryDao = edocSummaryDao;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	//兼容历史老调度，继续保留
	public void execute(JobExecutionContext datacontext) throws JobExecutionException {
		colManager = (ColManager)ApplicationContextHolder.getBean("colManager");
		edocSummaryDao = (EdocSummaryDao)ApplicationContextHolder.getBean("edocSummaryDao");
		userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
		colSuperviseManager = (ColSuperviseManager)ApplicationContextHolder.getBean("colSuperviseManager");
		affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
		orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		
		JobDetail jobDetail = datacontext.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		Long appType = jobDataMap.getLongFromString("appType");
		Long objectId = jobDataMap.getLongFromString("objectId");
		Long isAdvanceRemind = jobDataMap.getLongFromString("isAdvanceRemind");
		
		execute(objectId, isAdvanceRemind, appType);
	}

	public void execute(Map<String, String> parameters) {
		this.execute(new Long(parameters.get("objectId")), new Long(parameters.get("isAdvanceRemind")), new Long(parameters.get("appType")));
	}
	
	private void execute(Long objectId, Long isAdvanceRemind, Long appType) {
		try{
			ColSummary summary = null;
			EdocSummary edocSummary = null;
			//String localAppType = null;
			String title = null;
			String messageSentLink = null;
			String messagePendingLink = null;
			ApplicationCategoryEnum appEnum = null;
			Long sendId = null;
			Integer importantLevel = null;
			if(appType == ApplicationCategoryEnum.edoc.getKey()){
				edocSummary = edocSummaryDao.get(objectId);
				//公文被删除或者完成,不做提醒
				if(edocSummary == null || edocSummary.getCompleteTime() != null) return;
				//localAppType=ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",
				//		"application."+EdocUtil.getAppCategoryByEdocType(edocSummary.getEdocType()).getKey()+".label");
				title = edocSummary.getSubject();
				messageSentLink = "message.link.edoc.done";
				messagePendingLink = "message.link.edoc.pending";
				if (edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND) {
					appEnum = ApplicationCategoryEnum.edocSend;
				} else if (edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC) {
					appEnum = ApplicationCategoryEnum.edocRec;
				} else if (edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN) {
					appEnum = ApplicationCategoryEnum.edocSign;
				} else {
					appEnum = ApplicationCategoryEnum.edoc;
				}
				sendId = edocSummary.getStartUserId();
				importantLevel = edocSummary.getImportantLevel();
			}
			else{
				summary = colManager.getColSummaryById(objectId, false);
				//协同被删除或者完成,不做提醒
				if(summary == null || summary.getFinishDate() != null) return;
				//localAppType=ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",
				//		"application."+ApplicationCategoryEnum.collaboration.getKey()+".label");
				title = summary.getSubject();
				messageSentLink = "message.link.col.done";
				messagePendingLink = "message.link.col.pending";
				appEnum = ApplicationCategoryEnum.collaboration;
				sendId = summary.getStartMemberId();
				importantLevel = summary.getImportantLevel();
			}
			
			List<Affair> affairList = affairManager.getSentAndPendingAffairList(objectId);
			if(affairList == null)	return;//协同被撤销或者回退到发起人,不做提醒
			
	        String forwardMemberId = "";
	        if(affairList.size()>0) forwardMemberId = affairList.get(0).getForwardMember();
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
			try{
				Map<Long, MessageReceiver> receiverMap = new HashMap<Long, MessageReceiver>();
				Map<Long, MessageReceiver> receiverAgentMap = new HashMap<Long, MessageReceiver>();
				for(Affair affair : affairList){
		        	Long memberId = affair.getMemberId();
		            Long affairId = affair.getId();
		            if(affair.getState() == StateEnum.col_pending.getKey()){
		            	receiverMap.put(memberId, new MessageReceiver(affairId, memberId,messagePendingLink,affairId.toString()));
		            	//给代理人消息提醒
		            	Long agentId = MemberAgentBean.getInstance().getAgentMemberId(appEnum.key(), memberId);
		            	if(agentId != null)
		            		receiverAgentMap.put(memberId, new MessageReceiver(affairId, agentId,messagePendingLink,affairId.toString()));
		        	}
		            else{
		        		receiverMap.put(memberId, new MessageReceiver(affairId, memberId,messageSentLink,affairId.toString()));
		        	}
		        }
		        //督办人
		        List<ColSupervisor> supervisorList = colSuperviseManager.getColSupervisorList(Constant.superviseType.summary.ordinal(), objectId);
		    	if(supervisorList != null && !supervisorList.isEmpty()) {
	            	for(ColSupervisor supervisor : supervisorList){
			    		Long colSupervisMemberId = supervisor.getSupervisorId();
			    		if(affairList.size()>0){
			    			Affair _affair = affairList.get(0);
			    			receiverMap.put(colSupervisMemberId, new MessageReceiver(_affair.getId(), colSupervisMemberId, messageSentLink, _affair.getId().toString()));
			    		}
			    	}
		    	}
				if(null!=summary && appType == ApplicationCategoryEnum.collaboration.getKey() && summary.getCanAutoStopFlow() && isAdvanceRemind!=0){
					//自由协同：流程期限到时自动终止处理
					List<Affair> curerntAffairs = affairManager.getPendingAffairListByObject(summary.getId());
					Affair curerntAffair = null;
					if(curerntAffairs != null && !curerntAffairs.isEmpty()){
						curerntAffair = curerntAffairs.get(0);
					}
					boolean isCanGetLock= checkAndupdateLockForQuartz(summary.getProcessId(),summary.getId());
					if( isCanGetLock && null!=curerntAffair ){
						try{
							this.autoStopFlow(affairManager,summary,colManager,receiverMap,
									receiverAgentMap,userMessageManager,title,forwardMemberFlag,
									forwardMember,importantLevel,appEnum,sendId,curerntAffair);
						}catch(BPMException e){//工作流组件出错了，则重新生成定时任务，15分钟后执行
							Date nextRunTime= new Date((System.currentTimeMillis()+15*60*1000));
			    			String name= "ColProcessDeadLine" + summary.getId()+"_"+System.currentTimeMillis();
					   		Map<String, String> datamap = new HashMap<String, String>(3);
					    	datamap.put("appType", String.valueOf(ApplicationCategoryEnum.collaboration.key()));
					    	datamap.put("isAdvanceRemind", "1");
					    	datamap.put("objectId", String.valueOf(summary.getId()));
					   		QuartzHolder.newQuartzJob(name, nextRunTime, "processCycRemindQuartzJob", datamap);
					   		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					   		log.info("由于流程终止出错，流程到期终止处理的定时任务[ColProcessDeadLine" 
					   				+ summary.getId()+"]向后推迟15分钟执行，下次执行时间为："
					   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name,e);
						}finally{//解除锁
							releaseLockForQuartz(summary.getProcessId(),summary.getId());
						}
					}
				}else{
					String msgKey = null;
					if(isAdvanceRemind == 0){ //提前提醒
						msgKey = "process.summary.advanceRemind";
					}
					else{ //超期提醒
						msgKey = "process.summary.overTerm";
					}
					
					if(appType == ApplicationCategoryEnum.edoc.getKey()){
						msgKey += ".edoc";
					}
					if(!receiverMap.isEmpty()){
						Set<MessageReceiver> receivers = new HashSet<MessageReceiver>(receiverMap.values());
						userMessageManager.sendSystemMessage(MessageContent.get(msgKey, title, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel), appEnum, sendId, receivers);
					}
					
					if(!receiverAgentMap.isEmpty()){
						Set<MessageReceiver> receiverAgents = new HashSet<MessageReceiver>(receiverAgentMap.values());
						userMessageManager.sendSystemMessage(MessageContent.get(msgKey, title, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel).add("col.agent"), appEnum, sendId, receiverAgents);
					}
				}
			}
			catch(Exception e){
				log.error("", e);
			}
		}
		catch(Exception e){
			//绑定的定时任务事项已经不存在或被删除
			log.error("", e);
			return;
		}
	}
	
	/**
	 * 解除协同流程锁
	 * @param processId
	 * @param summaryId
	 */
	private void releaseLockForQuartz(String processId,Long summaryId) {
		try {
			User unitAdminUser = new User();//系统管理员
			unitAdminUser.setId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			V3xOrgMember member = orgManager.getMemberById(unitAdminUser.getId());
			unitAdminUser.setAccountId(member.getOrgAccountId());
			unitAdminUser.setLoginAccount(member.getOrgAccountId());
			unitAdminUser.setLoginName(member.getLoginName());
			unitAdminUser.setName(member.getName());
			CurrentUser.set(unitAdminUser);
			colManager.colDelLock(processId,summaryId.toString());//解除协同流程锁
		}catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * 检查自由协同锁
	 * @param processId
	 * @param summaryId
	 * @param affairId
	 * @return
	 */
	private boolean checkAndupdateLockForQuartz(String processId, Long summaryId) {
		try {
			User unitAdminUser = new User();//系统管理员
			unitAdminUser.setId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			V3xOrgMember member = orgManager.getMemberById(unitAdminUser.getId());
			unitAdminUser.setAccountId(member.getOrgAccountId());
			unitAdminUser.setLoginAccount(member.getOrgAccountId());
			unitAdminUser.setLoginName(member.getLoginName());
			unitAdminUser.setName(member.getName());
			CurrentUser.set(unitAdminUser);
			String lock = colManager.colCheckAndupdateLock(processId, summaryId);
			if(lock != null && lock != "" && lock != "null"){//协同流程锁
	    	    if(lock.startsWith("--NoSuchSummary--")){
	    			log.warn("协同被撤销或回退，定时任务不再执行!");
	    			return false;
	    		}else{//没申请到锁，则将该定时任务延迟15分钟(默认规则)
	    			Date nextRunTime= Datetimes.addMinute(new Date(), 15);
	    			String name= "ColProcessDeadLine" + summaryId+"_"+System.currentTimeMillis();
			   		Map<String, String> datamap = new HashMap<String, String>(3);
			    	datamap.put("appType", String.valueOf(ApplicationCategoryEnum.collaboration.key()));
			    	datamap.put("isAdvanceRemind", "1");
			    	datamap.put("objectId", String.valueOf(summaryId));
			   		QuartzHolder.newQuartzJob(name, nextRunTime, "processCycRemindQuartzJob", datamap);
			   		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			   		log.info("由于没有申请到[协同流程]锁，流程到期终止处理的定时任务[ColProcessDeadLine" 
			   				+ summaryId+"]向后推迟15分钟执行，下次执行时间为："
			   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name);
					return false;
	    		}
		    }
		} catch (Throwable e) {
			log.warn(e.getMessage(),e);
			return false;
		}
		return true;
	}

	/**
	 * 逾期自动终止流程处理
	 * @param affairManager
	 * @param summary
	 * @param colManager
	 * @param receiverMap
	 * @param receiverAgentMap
	 * @param messageManager
	 * @param title
	 * @param forwardMemberFlag
	 * @param forwardMember
	 * @param importantLevel
	 * @param appEnum
	 * @param sendId
	 * @throws Exception 
	 */
	private void autoStopFlow(AffairManager affairManager,ColSummary summary,
			ColManager colManager,Map<Long, MessageReceiver> receiverMap,
			Map<Long, MessageReceiver> receiverAgentMap,
			UserMessageManager messageManager, String title, 
			int forwardMemberFlag, String forwardMember, 
			Integer importantLevel, ApplicationCategoryEnum appEnum, Long sendId,
			Affair curerntAffair) throws Exception{
		AppLogManager appLogManager= (AppLogManager)ApplicationContextHolder.getBean("appLogManager");
		ProcessLogManager processLogManager= (ProcessLogManager)ApplicationContextHolder.getBean("processLogManager");
        //流程发起者作为流程终止者
        V3xOrgMember sender= orgManager.getMemberById(curerntAffair.getSenderId());
        User user= new User();
        user.setId(sender.getId());
        user.setDepartmentId(sender.getOrgDepartmentId());
        user.setLoginAccount(sender.getOrgAccountId());
        user.setLoginName(sender.getLoginName());
        user.setName(sender.getName());
        user.setRemoteAddr("127.0.0.1");
        //设置相关变量
        CurrentUser.set(user);
        WorkflowEventListener.setOperationType(WorkflowEventListener.STETSTOP);
        WorkflowEventListener.setColSummary(summary);
        Affair theStopAffair= new Affair();
        theStopAffair.setSenderId(user.getId());//发起协同人员id
        theStopAffair.setMemberId(user.getId());//处理协同人员id
        theStopAffair.setObjectId(summary.getId());//协同id
        theStopAffair.setSubObjectId(null);//已发协同记录的重要标志
        theStopAffair.setApp(ApplicationCategoryEnum.collaboration.ordinal());//协同类型
        DateSharedWithWorkflowEngineThreadLocal.setTheStopAffair(theStopAffair);
        //终止工作流组件
        Long _workitemId = curerntAffair.getSubObjectId();
		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
		wim.stopWorkItem(sender.getId().toString(), _workitemId, null, null, null, null);
		
		//保存终止时的意见
        ColOpinion signOpinion = new ColOpinion();
        signOpinion.isDeleteImmediate = false;
        signOpinion.affairIsTrack = false;
        signOpinion.setIsHidden(false);
        signOpinion.setIdIfNew();
        String content= ResourceBundleUtil.getString(com.seeyon.v3x.common.usermessage.Constants.DEFAULT_MESSAGE_RESOURCE, Locale.getDefault(), "process.summary.overTerm.stopflow.opinion");
        signOpinion.setContent(content);
        signOpinion.setSummaryId(summary.getId());
        signOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
        signOpinion.setOpinionType(ColOpinion.OpinionType.stopOpinion.ordinal());
        signOpinion.setWriteMemberId(curerntAffair.getSenderId());
        colManager.save(signOpinion);
        
        //写流程日志和应用日志
        processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), 1l, ProcessLogAction.stepStop);
        appLogManager.insertLog(user, AppLogAction.Coll_Flow_Stop, user.getName(), summary.getSubject());
		//发系统消息
		String msgKey = "process.summary.overTerm.stopflow";
		if(!receiverMap.isEmpty()){
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>(receiverMap.values());
			messageManager.sendSystemMessage(MessageContent.get(msgKey, title, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel), appEnum, sendId, receivers);
		}
		if(!receiverAgentMap.isEmpty()){
			Set<MessageReceiver> receiverAgents = new HashSet<MessageReceiver>(receiverAgentMap.values());
			messageManager.sendSystemMessage(MessageContent.get(msgKey, title, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel).add("col.agent"), appEnum, sendId, receiverAgents);
		}
	}
}
