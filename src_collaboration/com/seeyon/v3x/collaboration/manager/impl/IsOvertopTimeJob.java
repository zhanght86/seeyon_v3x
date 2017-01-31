package com.seeyon.v3x.collaboration.manager.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItemManager;
import net.joinwork.bpm.task.WorkitemDAO;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.LockObject;
import com.seeyon.v3x.collaboration.event.CollaborationFinishEvent;
import com.seeyon.v3x.collaboration.event.CollaborationProcessEvent;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.office.HandWriteManager;
import com.seeyon.v3x.common.office.UserUpdateObject;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocStatManager;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;


public class IsOvertopTimeJob implements QuartzJob,Job{
	private final static Log log = LogFactory.getLog(IsOvertopTimeJob.class);
	
	private UserMessageManager userMessageManager = null;
	private AffairManager affairManager = null;
	private ColManager colManager= null;
	private EdocManager edocManager= null;
	private OrgManager orgManager= null;
	private AppLogManager appLogManager= null;
	private ProcessLogManager processLogManager= null;
	private EdocStatManager edocStatManager= null;
	private EdocSummaryDao edocSummaryDao= null;
	private NewflowManager newflowManager= null;
	private HandWriteManager handWriteManager= null;
	private EdocSummaryManager edocSummaryManager= null;
	
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	//兼容历史老调度，继续保留
	public void execute(JobExecutionContext datacontext) throws JobExecutionException {
		Long affairId = null;
		Long isAdvanceRemind = null;
		try{
			JobDetail jobDetail = datacontext.getJobDetail();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			affairId = jobDataMap.getLongFromString("affairId");
			isAdvanceRemind = jobDataMap.getLongFromString("isAdvanceRemind");
		}
		catch(Throwable e){
			//绑定的定时任务事项已经不存在或被删除
			log.error("", e);
			return;
		}
		
		this.execute(affairId, isAdvanceRemind);
	}
	
	public void execute(Map<String, String> parameters) {
		this.execute(new Long(parameters.get("affairId")), new Long(parameters.get("isAdvanceRemind")));
	}
	
	private void execute(Long affairId,Long isAdvanceRemind) {
		Affair affair = affairManager.getById(affairId);
		if(null!=affair && affair.getCompleteTime() == null){
			if(affair == null || affair.getState() == StateEnum.col_cancel.key()
					|| affair.getState() == StateEnum.col_stepBack.key()
					|| affair.getState() == StateEnum.col_stepStop.key()
					|| affair.getState() == StateEnum.col_takeBack.key()
					|| affair.getState() == StateEnum.col_competeOver.key()
					|| affair.getIsDelete()){
				return;
			}
			ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
			String messageLink= "message.link.col.pending";
			String messageLinkdone= "message.link.col.done";
			if(affair.getApp() == ApplicationCategoryEnum.edoc.getKey()
				|| affair.getApp() == ApplicationCategoryEnum.edocRec .getKey()
				|| affair.getApp() == ApplicationCategoryEnum.edocSend.getKey()
				|| affair.getApp() == ApplicationCategoryEnum.edocSign.getKey()
			) {
				messageLink="message.link.edoc.pending";
				messageLinkdone= "message.link.edoc.done";
			}
			String key = null;
			if(isAdvanceRemind == 0){
				key = "node.affair.advanceRemind";
			}else if(isAdvanceRemind == 1){
				key = "node.affair.overTerm";	
			}
			Long memberId = affair.getMemberId();
			Long sendId = affair.getSenderId();
			V3xOrgMember currentMember= null;
			try {
				currentMember= orgManager.getMemberById(memberId);
			} catch (Throwable e) {
				log.warn(e.getMessage(),e);
				return;
			}
			if(isAdvanceRemind == 1){
				try{
					boolean isOvertopTime = affair.getIsOvertopTime();
					if(!isOvertopTime){
						isOvertopTime = true;
						affair.setIsOvertopTime(isOvertopTime);
						affairManager.updateAffair(affair);//更新事项处理状态
					}
				}catch(Throwable e1){
					log.error(e1.getMessage(),e1);
					return;
				}
				int dealTermType= affair.getDealTermType();
				if(dealTermType!=0){
					Long summaryId= affair.getObjectId();
					BPMProcess bpmProcess= null;
					BPMActivity bpmActivity= null;
					BPMSeeyonPolicy bpmSeeyonPolicy= null;
					String _nodePolicy= null;
					int isColl= 0;
					String processId= null;
					String colSubject= "";
					EdocSummary edocSummary= null;
					ColSummary colSummary= null;
					boolean isForm= false;
					String formAppId= null;
					String formId= null;
					String formOperationId= null;
					try {
						if(affair.getApp() == ApplicationCategoryEnum.edoc.getKey()
								|| affair.getApp() == ApplicationCategoryEnum.edocRec .getKey()
								|| affair.getApp() == ApplicationCategoryEnum.edocSend.getKey()
								|| affair.getApp() == ApplicationCategoryEnum.edocSign.getKey()
							) {
							edocSummary= edocManager.getEdocSummaryById(summaryId,true);
							processId= edocSummary.getProcessId();
							colSubject= edocSummary.getSubject();
							isColl= 1;
						}else{
							colSummary= colManager.getColSummaryById(summaryId,true);
							processId= colSummary.getProcessId();
							colSubject= colSummary.getSubject();
							isColl= 2;
							isForm = "FORM".equals(affair.getBodyType());
							if(isForm){
								formAppId = String.valueOf(affair.getFormAppId());
					        	formId = String.valueOf(affair.getFormId());
					        	formOperationId = String.valueOf(affair.getFormOperationId());
							}
						}
						bpmProcess= ColHelper.getProcess(processId);
						if(null==bpmProcess){
							bpmProcess= ColHelper.getCaseProcess(processId);
						}
						bpmActivity= bpmProcess.getActivityById(affair.getActivityId().toString());
						bpmSeeyonPolicy= bpmActivity.getSeeyonPolicy();
						_nodePolicy = bpmSeeyonPolicy.getId();
					} catch (Throwable e) {
						log.warn(e.getMessage(),e);
						return;
					}
					boolean isCanGetLock= checkAndupdateLockForQuartz(processId,summaryId,affairId,isForm,formAppId,formId,formOperationId,edocSummary);
					if(isCanGetLock){
						affair= affairManager.getById(affair.getId());
						if(affair == null || affair.getState() == StateEnum.col_cancel.key()
								|| affair.getState() == StateEnum.col_stepBack.key()
								|| affair.getState() == StateEnum.col_stepStop.key()
								|| affair.getState() == StateEnum.col_takeBack.key()
								|| affair.getState() == StateEnum.col_competeOver.key()
								|| affair.getIsDelete()){
							return;
						}
					}
					if( isCanGetLock 
							&& !"inform".equals(_nodePolicy) 
							&& !"zhihui".equals(_nodePolicy) 
							&& affair.getCompleteTime() == null){//不是知会节点，且获得了锁
						if( dealTermType== 1 && isColl>0 && null!=bpmProcess ){//1-转给指定人
							if(affair.getDealTermUserId().longValue()!=affair.getMemberId().longValue()){
								//但转给人和被转给人不能是同一个人，否则不进行转给操作
								Long dealTermUserId= affair.getDealTermUserId();
								V3xOrgMember nextMember= null;
								V3xOrgAccount nextOrgAccount= null;
								V3xOrgAccount currentOrgAccount= null;
								try {
									nextMember = orgManager.getMemberById(dealTermUserId);
									nextOrgAccount= orgManager.getAccountById(nextMember.getOrgAccountId());
									currentMember= orgManager.getMemberById(memberId);
									currentOrgAccount= orgManager.getAccountById(currentMember.getOrgAccountId());
								} catch (Throwable e) {
									releaseLockForQuartz(processId,summaryId,edocSummary);
									log.warn(e.getMessage(),e);
									return;
								}
								if(null!=nextMember && nextMember.isValid()){//接替人员可用才行
									this.doReplacement(nextMember,nextOrgAccount,currentMember,currentOrgAccount,
												affair,bpmProcess,edocSummary,colSubject,processId,appEnum,sendId,messageLink);
									return;
								}else{//模板中指定的接替人员及其代理人都不可用，则发个消息给该待办事项的人员和代理人
									String notkey1= "node.affair.overTerm.sysautoreplace1.not";
									String notkey2= "node.affair.overTerm.sysautoreplace1.edoc.not";
									sendSysMessageForReplacement(currentMember,affair,edocSummary,nextMember,nextOrgAccount,messageLink,processId,appEnum,notkey1,notkey2);
									return;
								}
							}
						}else if( dealTermType== 2  && isColl>0 && null!=bpmProcess ){//2-自动跳过
							//一、查询当前人员信息
							try {
								User user= new User();
						        user.setId(currentMember.getId());
						        user.setDepartmentId(currentMember.getOrgDepartmentId());
						        user.setLoginAccount(currentMember.getOrgAccountId());
						        user.setLoginName(currentMember.getLoginName());
						        user.setName(currentMember.getName());
						        user.setRemoteAddr("127.0.0.1");
						        //二、以该affair运行流程，使流程流转到下一节点
								CurrentUser.set(user);
								Map<String, String[]> fieldDataBaseMap= new HashMap<String, String[]>();
								if(isColl==1){//公文
									key= "node.affair.overTerm.autoruncase.edoc";//公文
									String notkey1= "node.affair.overTerm.autoruncase.edoc.not";
									String notkey2= "node.affair.overTerm.autoruncase.edoc.not1";//封发
									if(!"fengfa".equals(_nodePolicy)){//封发节点不让自动跳过，自定义节点含有交换类型的本次不处理
										//判断当前处理人员是否为当前流程节点的最后一个处理人
					                    boolean isExecuteFinished= ColHelper.isExecuteFinished(bpmProcess, affair);
										if(isExecuteFinished){
											//如果含有当前节点之后含有分支，则不允许跳过
											boolean isFromTemplate= edocSummary.getTempleteId()!=null && edocSummary.getTempleteId().longValue()!=-1;
											long workItemId= -1l;
											if(affair.getSubObjectId() != null){
					                			workItemId = affair.getSubObjectId();
					                		}
											boolean hasBranch= BranchArgs.hasSelectorOrCondition(bpmProcess, bpmActivity, edocSummary.getCaseId(),isFromTemplate,fieldDataBaseMap,orgManager,workItemId);
											if(!hasBranch){
												this.sysAutoRunCase(edocSummary,affair,bpmSeeyonPolicy,bpmActivity,user);
											}else{
												log.warn("该公文待办后面节点需要进行分支匹配或选择执行人，不允许执行自动跳过操作。edocSummaryId:="+edocSummary.getId()+";  affairId:="+affair.getId());
												sendSysMessageForAutoRunCase(affair,messageLink,edocSummary,processId,appEnum,currentMember,notkey1);
												return;
											}
										}else{
											this.sysAutoRunCase(edocSummary,affair,bpmSeeyonPolicy,bpmActivity,user);
										}
									}else{
										log.warn("该公文待办为公文封发节点，不允许执行自动跳过操作。edocSummaryId:="+edocSummary.getId()+";	  affairId:="+affair.getId());
										sendSysMessageForAutoRunCase(affair,messageLink,edocSummary,processId,appEnum,currentMember,notkey2);
										return;
									}
								}else if(isColl==2){//协同
									
									if( !"vouch".equals(_nodePolicy) ){//不是核定节点
										key= "node.affair.overTerm.autoruncase";//协同
										String notkey= "node.affair.overTerm.autoruncase.not";
										//判断当前处理人员是否为当前流程节点的最后一个处理人
					                    boolean isExecuteFinished= ColHelper.isExecuteFinished(bpmProcess, affair);
					                    boolean isFromTemplate= colSummary.getTempleteId()!=null && colSummary.getTempleteId().longValue()!=-1;
				                		boolean hasNewflow = isFromTemplate && bpmSeeyonPolicy != null && "1".equals(bpmSeeyonPolicy.getNF());
					                    if(isExecuteFinished){//是决定流程走向的人
					                    	String formApp = bpmSeeyonPolicy.getFormApp() ;
					                		//表单数据对应的主表记录主键值
					                		String mastrid = null ;
					                		ColBody body = new ColBody();
					                		if(colSummary != null){
					                			body = colSummary.getFirstBody();//从协同中获得协同主体信息
					                		}
					                		if(body != null){
					                			mastrid = body.getContent() ;//获得表单数据对应的主表记录主键值
					                		}
					                		boolean isPreNewFlowFinish= true;
					                		if(body.getBodyType().equals("FORM")){//表单类型的数据
					                			fieldDataBaseMap = FormHelper.getFieldValueMap(formApp, bpmSeeyonPolicy.getForm(), bpmSeeyonPolicy.getOperationName(), mastrid);
					                			List<String> hasNewflowNodeIds = ColHelper.checkPrevNodeHasNewflow(bpmActivity);
					                			if(hasNewflowNodeIds != null && !hasNewflowNodeIds.isEmpty()){
					                                String noFinishNewflowTitle = newflowManager.checkHasNoFinishNewflow(summaryId, hasNewflowNodeIds);
					                                if(Strings.isNotBlank(noFinishNewflowTitle)){
					                                	isPreNewFlowFinish= false;//有子流程没有结束
					                                }
					                            }
					                		}
					                		long workItemId= -1l;
											if(affair.getSubObjectId() != null){
					                			workItemId = affair.getSubObjectId();
					                		}
					                    	boolean hasBranch= BranchArgs.hasSelectorOrCondition(bpmProcess, bpmActivity, colSummary.getCaseId(),isFromTemplate,fieldDataBaseMap,orgManager,workItemId);
											log.info("isPreNewFlowFinish:="+isPreNewFlowFinish+";hasNewflow="+hasNewflow+";hasBranch="+hasBranch);
					                    	if(!hasBranch && !hasNewflow && isPreNewFlowFinish){
												this.sysAutoRunCase(colSummary,affair,bpmSeeyonPolicy,bpmActivity,fieldDataBaseMap);
											}else{
												log.warn("该协同待办需要触发新流程或者前面节点触发的新流程还没有结束或者后面节点需要进行分支匹配、选择执行人，不允许执行自动跳过操作。colSummaryId:="+colSummary.getId()+";  affairId:="+affair.getId());
												sendSysMessageForAutoRunCase(affair,messageLink,edocSummary,processId,appEnum,currentMember,notkey);
												return;
											}
					                    }else{//不是决定分支走向的人
					                    	if( !hasNewflow ){
					                    		this.sysAutoRunCase(colSummary,affair,bpmSeeyonPolicy,bpmActivity,fieldDataBaseMap);
					                    	}else{
					                    		log.warn("该协同待办需要触发新流程或者前面节点触发的新流程还没有结束或者后面节点需要进行分支匹配、选择执行人，不允许执行自动跳过操作。colSummaryId:="+colSummary.getId()+";  affairId:="+affair.getId());
					                    		sendSysMessageForAutoRunCase(affair,messageLink,edocSummary,processId,appEnum,currentMember,notkey);
												return;
					                    	}
					                    }
									}else{
										String notkey= "node.affair.overTerm.autoruncase.not.heding";
										log.warn("该协同待办为核定节点，不允许执行自动跳过操作。colSummaryId:="+colSummary.getId()+";  affairId:="+affair.getId());
			                    		sendSysMessageForAutoRunCase(affair,messageLink,edocSummary,processId,appEnum,currentMember,notkey);
										return;
									}
								}
								processLogManager.insertLog(user, Long.parseLong(processId),Long.parseLong(bpmActivity.getId()), ProcessLogAction.processColl_SysAuto,currentMember.getName());
						        appLogManager.insertLog(user, AppLogAction.Coll_Flow_Node_RunCase_AutoSys,colSubject,bpmActivity.getName(),user.getName());
								//三、给被跳过人发个消息提醒
						        sendSysMessageForAutoRunCase(affair,messageLinkdone,edocSummary,processId,appEnum,currentMember,key);
								return;
							} catch (Throwable e1) {
								releaseLockForQuartz(processId,summaryId,edocSummary);
								log.warn(e1.getMessage(),e1);
								return;
							}
						}
					}
					//释放锁
					if(isCanGetLock){
						releaseLockForQuartz(processId,summaryId,edocSummary);
					}
				}
			}
			sendSysMessageForAutoRunCase(affair,messageLink,null,null,appEnum,currentMember,key);
		}else{
			log.info("该待办已处理，完成时间不为空，不需要执行处理期限到操作。affairId:="+affairId);
		}
	}

	/**
	 * 自动跳过和仅消息提醒
	 * @param affair
	 * @param messageLink
	 * @param edocSummary
	 * @param processId
	 * @param appEnum
	 * @param currentMmber
	 * @param key
	 */
	private void sendSysMessageForAutoRunCase(Affair affair,String messageLink,
			EdocSummary edocSummary,String processId,ApplicationCategoryEnum appEnum,
			V3xOrgMember currentMmber,String key) {
		try{
			//同时给该事项办理人员发一条提醒消息?
			MessageContent msgContent = null;
			MessageReceiver msgReceiver = null;
			if( null!=currentMmber && currentMmber.isValid() ){
				msgContent = new MessageContent(key, affair.getSubject());
				msgReceiver = new MessageReceiver(affair.getId(), currentMmber.getId(), messageLink, affair.getId().toString());
			}
			Long agentId = MemberAgentBean.getInstance().getAgentMemberId(appEnum.key(), currentMmber.getId());
			MessageContent msgContent1 = null;//代理人消息内容
			MessageReceiver msgReceiver1= null;//代理人id
	    	if(null!= agentId){	    		
				V3xOrgMember currentMemberAgent= orgManager.getMemberById(agentId);
				if(null!=currentMemberAgent && currentMemberAgent.isValid()){//代理人员可用
					String key1= key+".agent";//协同不能自动跳过的原因，以便提醒用户办理
					msgContent1 = new MessageContent(key1, affair.getSubject());
					msgReceiver1 = new MessageReceiver(affair.getId(), currentMemberAgent.getId(), messageLink, affair.getId().toString());
				}
	    	}
	    	if(null!=msgReceiver){
    			userMessageManager.sendSystemMessage(msgContent, appEnum, affair.getSenderId(), msgReceiver);
    		}
			if(null!=msgReceiver1){
				userMessageManager.sendSystemMessage(msgContent1, appEnum, affair.getSenderId(), msgReceiver1);
			}
		}catch(Throwable e){
			log.error(e.getMessage(), e);
		}finally{
			if( null!= processId){
				releaseLockForQuartz(processId,affair.getObjectId(),edocSummary);
			}
		}
	}

	/**
	 * 给当前人和及代理人发系统提醒消息（指定给具体人员，释放锁资源）
	 * @param currentMember
	 * @param affair
	 * @param edocSummary
	 * @param nextMember
	 * @param nextOrgAccount
	 * @param messageLink
	 * @param processId
	 * @param appEnum
	 */
	private void sendSysMessageForReplacement(V3xOrgMember currentMember,
			Affair affair,EdocSummary edocSummary,V3xOrgMember nextMember,
			V3xOrgAccount nextOrgAccount,String messageLink,String processId,
			ApplicationCategoryEnum appEnum,String colKey,String edocKey) {
		try {
			MessageContent msgContent = null;//代理人消息内容
			MessageReceiver msgReceiver = null;//代理人id
			MessageContent msgContent1 = null;//代理人消息内容
			MessageReceiver msgReceiver1= null;//代理人id
			String key= colKey;
			if(null!=edocSummary){
	        	key= edocKey;
	        }
			if(null!=currentMember && currentMember.isValid()){//给本人发消息提醒
				msgContent = new MessageContent(key, affair.getSubject(),nextMember.getName()+"("+nextOrgAccount.getShortname()+")");
				msgReceiver = new MessageReceiver(affair.getId(), currentMember.getId(), messageLink, affair.getId().toString());
			}	
			//给代理人发消息提醒
	    	Long agentId = MemberAgentBean.getInstance().getAgentMemberId(appEnum.key(), affair.getMemberId());
	    	if(null!= agentId){	    		
				V3xOrgMember currentMemberAgent= orgManager.getMemberById(agentId);
				if(null!=currentMemberAgent && currentMemberAgent.isValid()){//代理人员可用
					String key1= key+".agent";
					msgContent1 = new MessageContent(key1, affair.getSubject(),nextMember.getName()+"("+nextOrgAccount.getShortname()+")");
					msgReceiver1 = new MessageReceiver(affair.getId(), currentMemberAgent.getId(), messageLink, affair.getId().toString());
				}
	    	}
    		if(null!=msgReceiver){
    			userMessageManager.sendSystemMessage(msgContent, appEnum, affair.getSenderId(), msgReceiver);
    		}
			if(null!=msgReceiver1){
				userMessageManager.sendSystemMessage(msgContent1, appEnum, affair.getSenderId(), msgReceiver1);
			}
		}catch(Throwable e){
			log.error(e.getMessage(),e);
		}finally{
			releaseLockForQuartz(processId,affair.getObjectId(),edocSummary);
		}
	}

	/**
	 * 解锁
	 * @param processId
	 * @param summaryId
	 * @param edocSummary
	 */
	private void releaseLockForQuartz(String processId,Long summaryId,EdocSummary edocSummary) {
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
			colManager.removeFormLock(summaryId);//解除表单锁
			if(edocSummary == null){
				return ;
			}else{//解除公文锁
				String bodyType = edocSummary.getFirstBody().getContentType();
		    	long edocSummaryId = edocSummary.getId();
		    	if(Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(bodyType)||
		    			Constants.EDITOR_TYPE_OFFICE_WORD.equals(bodyType)||
		    			Constants.EDITOR_TYPE_WPS_EXCEL.equals(bodyType)||
		    			Constants.EDITOR_TYPE_WPS_WORD.equals(bodyType)){
			    	//1、解锁office正文
		    		try{
		    			String contentId = edocSummary.getFirstBody().getContent();
			    		handWriteManager.deleteUpdateObj(contentId);
			    	}catch(Exception e){
			    		log.error("解锁office正文失败 userId:"+unitAdminUser.getId()+" summaryId:"+edocSummary.getId(),e);
			    	}
		    	}else{
			    	//2、解锁html正文
			    	try{
			    		handWriteManager.deleteUpdateObj(String.valueOf(edocSummaryId));
			    	}catch(Exception e){
			    		log.error("解锁html正文失败 userId:"+unitAdminUser.getId()+" summaryId:"+edocSummaryId ,e);
			    	}
		    	}
		    	//3、解锁公文单
		    	try{
		    		edocSummaryManager.deleteUpdateObj(String.valueOf(edocSummaryId), String.valueOf(unitAdminUser.getId()));
		    	}catch(Exception e){
		    		log.error("解锁公文单失败 userId:"+unitAdminUser.getId()+" summaryId:"+edocSummaryId,e);
		    	}
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
	}

	/**
	 * 
	 * 获得锁(协同流程锁、表单锁和公文锁)，如果获取不到，则将定时任务向后推迟15分钟（默认规则）
	 * @param processId
	 * @param summaryId
	 * @param affairId
	 * @param formAppId
	 * @param formId
	 * @param formOperationId
	 * @param edocSummary
	 * @return
	 */
	private boolean checkAndupdateLockForQuartz(
			String processId,Long summaryId,Long affairId,
			boolean isForm,String formAppId,String formId,String formOperationId,
			EdocSummary edocSummary
			) {
		try {
			User unitAdminUser = new User();//单位管理员
			unitAdminUser.setId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			V3xOrgMember member = orgManager.getMemberById(unitAdminUser.getId());
			unitAdminUser.setAccountId(member.getOrgAccountId());
			unitAdminUser.setLoginAccount(member.getOrgAccountId());
			unitAdminUser.setLoginName(member.getLoginName());
			unitAdminUser.setName(member.getName());
			CurrentUser.set(unitAdminUser);
			
			//流程锁
			String lock = colManager.colCheckAndupdateLock(processId, summaryId);
			if(lock != null && lock != "" && lock != "null"){
	    	    if(lock.startsWith("--NoSuchSummary--")){
	    			log.warn("协同被撤销或回退，定时任务不再执行!");
	    			return false;
	    		}else{//已经被其他人锁定，则将该定时任务延迟15分钟(默认规则)
	    			reCreateOvertopQuartz(affairId,"1");
					return false;
	    		}
		    }else{
		    	if(isForm){//表单
		    		boolean hasEdit = FormHelper.hasEditType(formAppId, formId, formOperationId);
	        		if(hasEdit){//编辑权限
	        			LockObject lockObject = FormLockManager.add(summaryId,affairId,member.getId(), member.getLoginName(),System.currentTimeMillis());
	        			if(lockObject!=null&&!member.getLoginName().equals(lockObject.getLoginName())){
	        				try{
	        					reCreateOvertopQuartz(affairId, "2");
	        				}finally{
	        					colManager.colDelLock(processId,summaryId.toString());
	        				}
	    					return false;
	        			}
	        		}
		    	}else if(null!=edocSummary){//公文
		    		String bodyType = edocSummary.getFirstBody().getContentType();
		    		String contentId = edocSummary.getFirstBody().getContent();
		    		if(Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(bodyType)||
			    			Constants.EDITOR_TYPE_OFFICE_WORD.equals(bodyType)||
			    			Constants.EDITOR_TYPE_WPS_EXCEL.equals(bodyType)||
			    			Constants.EDITOR_TYPE_WPS_WORD.equals(bodyType)){
			    		UserUpdateObject os= handWriteManager.editObjectState(contentId);
			    		if(os.getCurEditState()){//Office公文正文
			    			try{
			    				reCreateOvertopQuartz(affairId, "3");
			    			}finally{
			    				colManager.colDelLock(processId,summaryId.toString());
			    			}
	    					return false;
			    		}
		    		}else{//html正文
		    			UserUpdateObject os= handWriteManager.editObjectState(edocSummary.getId().toString());
		    			if(os.getCurEditState()){//Html公文正文
					   		try{
					   			reCreateOvertopQuartz(affairId, "4");
					   		}finally{
					   			colManager.colDelLock(processId,summaryId.toString());
					   		}
	    					return false;
			    		}
		    		}
		    		UserUpdateObject os1=edocSummaryManager.editObjectState(summaryId.toString());
		    		if(os1.getCurEditState()){//公文单
				   		try{
				   			reCreateOvertopQuartz(affairId, "5");
				   		}finally{
				   			handWriteManager.deleteUpdateObj(contentId);
					   		handWriteManager.deleteUpdateObj(String.valueOf(edocSummary.getId()));
					   		colManager.colDelLock(processId,summaryId.toString());
				   		}
    					return false;
		    		}
		    	}
		    }
		} catch (Throwable e) {
			log.warn(e.getMessage(),e);
			return false;
		}
		return true;
	}

	/**
	 * 重新创建定时任务，延迟15分钟
	 * @param affairId
	 * @param type
	 * @throws Throwable
	 */
	private void reCreateOvertopQuartz(Long affairId,String type) throws Throwable{
		Date nextRunTime= Datetimes.addMinute(new Date(), 15);
    	String name = "DeadLine" + affairId+"_"+System.currentTimeMillis();	 			   		
    	Map<String, String> datamap = new HashMap<String, String>(2);
    	datamap.put("isAdvanceRemind", "1");
    	datamap.put("affairId", String.valueOf(affairId));
   		QuartzHolder.newQuartzJob(name, nextRunTime, "affairIsOvertopTimeJob", datamap);
   		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   		if("1".equals(type)){
   			log.info("由于没有申请到[协同流程]锁，流程节点到期处理的定时任务[DeadLine" 
   	   				+ affairId+"]向后推迟15分钟执行，下次执行时间为："
   	   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name);
   		}
   		if("2".equals(type)){
   			log.info("由于没有申请到[表单]锁，流程节点到期处理的定时任务[DeadLine" 
   	   				+ affairId+"]向后推迟15分钟执行，下次执行时间为："
   	   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name);
   		}
   		if("3".equals(type)){
   			log.info("由于没有申请到[公文Office正文]锁，流程节点到期处理的定时任务[DeadLine" 
	   				+ affairId+"]向后推迟15分钟执行，下次执行时间为："
	   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name);
   		}
   		if("4".equals(type)){
   			log.info("由于没有申请到[公文Html正文]锁，流程节点到期处理的定时任务[DeadLine" 
	   				+ affairId+"]向后推迟15分钟执行，下次执行时间为："
	   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name);
   		}
   		if("5".equals(type)){
   			log.info("由于没有申请到[公文单]锁，流程节点到期处理的定时任务[DeadLine" 
	   				+ affairId+"]向后推迟15分钟执行，下次执行时间为："
	   				+sdf.format(nextRunTime)+"，新的定时任务名称为："+name);
   		}
	}

	/**
	 * 转给指定人：从currentMember转给nextMember
	 * @param nextMember
	 * @param nextOrgAccount
	 * @param currentMember
	 * @param currentOrgAccount
	 * @param affair
	 * @param bpmProcess
	 * @param edocSummary
	 * @param colSubject
	 * @param processId
	 * @param appEnum
	 * @param sendId
	 * @return
	 * @throws Exception
	 */
	private Affair doReplacement(
			V3xOrgMember nextMember,V3xOrgAccount nextOrgAccount,
			V3xOrgMember currentMember,V3xOrgAccount currentOrgAccount,
			Affair affair,BPMProcess bpmProcess,EdocSummary edocSummary,
			String colSubject,String processId,
			ApplicationCategoryEnum appEnum,Long sendId,String messageLink) {
		Affair newAffair = null;
		try{
			BPMHumenActivity bpmActivity= (BPMHumenActivity)bpmProcess.getActivityById(affair.getActivityId().toString());
			BPMActor bpmActor= (BPMActor)bpmActivity.getActorList().get(0);
			String partyType= bpmActor.getType().id;
			newAffair = (Affair)BeanUtils.cloneBean(affair);
			//一、替换v3x_affair中的member_id为dealTermUserId
			newAffair.setMemberId(nextMember.getId());
			long newId= UUIDLong.longUUID();
			newAffair.setId(newId);
			affairManager.addAffair(newAffair);
			affair.setIsDelete(true);
			affair.setActivityId(-1l);
			affair.setSubObjectId(-1l);
			affair.setObjectId(-1l);
			affair.setTempleteId(-1l);
			affairManager.updateAffair(affair);
			//二、替换流程图中的:
			//1)<node/>标签的name属性
			//2)<actor/>标签的partyId为dealTermUserId，partyIdName为dealTermUserId对应的人员名称
			//  accountId为dealTermUserId对应的人员所在单位id
			//if("user".equals(partyType) && !"inform".equals(_nodePolicy) && !"zhihui".equals(_nodePolicy)){
			//需要替换流程中上述信息，否则不需要替换
			User user= new User();
	        user.setId(nextMember.getId());
	        user.setDepartmentId(nextMember.getOrgDepartmentId());
	        user.setLoginAccount(nextMember.getOrgAccountId());
	        user.setLoginName(nextMember.getLoginName());
	        user.setName(nextMember.getName());
	        user.setRemoteAddr("127.0.0.1");
	        CurrentUser.set(user);
			WorkitemDAO workitem= (WorkitemDAO)ColHelper.getWorkItemById(newAffair.getSubObjectId());
			workitem.setPerformer(nextMember.getId().toString());
			ColHelper.saveWorkitem(workitem);
			if("user".equals(partyType)){//如果节点类型为user，则需要将流程图中的节点名称进行修改，否则不需要
				bpmActivity.setName(nextMember.getName());
				bpmActor.getParty().setAddition(nextMember.getId().toString());
				bpmActor.getParty().setId(nextMember.getId().toString());
				bpmActor.getParty().setName(nextMember.getName());
				bpmActor.getParty().setAccountId(nextMember.getOrgAccountId().toString());
				ColHelper.updateRunningProcess(bpmProcess);
			}
			//三、给被替换人发个消息提醒:
	        String key1= "node.affair.overTerm.sysautoreplace";
	        if(null!=edocSummary){
	        	key1= "node.affair.overTerm.sysautoreplace.edoc";
	        }
	        MessageContent msgContent1 = new MessageContent(key1, affair.getSubject(),nextMember.getName()+"("+nextOrgAccount.getShortname()+")");
	        MessageReceiver msgReceiver1 = new MessageReceiver(affair.getId(), currentMember.getId());
	    	Long agentId = MemberAgentBean.getInstance().getAgentMemberId(appEnum.key(), currentMember.getId());
	    	MessageContent msgContent2= null;
	    	MessageReceiver msgReceiver2= null;
	    	if(null!= agentId){
	    		try {
	    			V3xOrgMember currentMemberAgent= orgManager.getMemberById(agentId);
					if(null!=currentMemberAgent && currentMemberAgent.isValid()){//代理人员可用
						String key2= "node.affair.overTerm.sysautoreplace.agent";
				        if(null!=edocSummary){
				        	key2= "node.affair.overTerm.sysautoreplace.edoc.agent";
				        }
						msgContent2 = new MessageContent(key2, affair.getSubject(),nextMember.getName()+"("+nextOrgAccount.getShortname()+")");
					    msgReceiver2 = new MessageReceiver(affair.getId(), currentMemberAgent.getId());
					}
				} catch (Throwable e) {
					log.warn(e.getMessage(), e);
				}
	    	}
	        //五、写流程日志和应用日志
			processLogManager.insertLog(user, Long.parseLong(processId),  Long.parseLong(bpmActivity.getId()), ProcessLogAction.replaceNode_SysAuto,currentOrgAccount.getShortname()+":"+currentMember.getName(),nextOrgAccount.getShortname()+":"+nextMember.getName());
	        appLogManager.insertLog(user, AppLogAction.Coll_Flow_Node_DeadLine_2_POPLE, colSubject,bpmActivity.getName(),currentMember.getName(),nextMember.getName());
			try {
				if(currentMember.isValid()){
					userMessageManager.sendSystemMessage(msgContent1, appEnum, sendId, msgReceiver1);
				}
				if( null!=msgContent2 && null!= msgReceiver2){//给代理人发消息
					userMessageManager.sendSystemMessage(msgContent2, appEnum, sendId, msgReceiver2);
				}
			} catch (Throwable e) {
				log.warn(e.getMessage(), e);
			}
			try {
				//给替换人发个消息提醒:
				String key= "node.affair.overTerm.sysautoreplace1";
				if(null!=edocSummary){
		        	key= "node.affair.overTerm.sysautoreplace1.edoc";
		        }
				MessageContent msgContent3 = new MessageContent(key, newAffair.getSubject(),currentMember.getName()+"("+currentOrgAccount.getShortname()+")");
				MessageReceiver msgReceiver3 = new MessageReceiver(newAffair.getId(), nextMember.getId(), messageLink, newAffair.getId().toString()); 
				Long nextMemberAgentId = MemberAgentBean.getInstance().getAgentMemberId(appEnum.key(), nextMember.getId());
				MessageContent msgContent4 = null;
				MessageReceiver msgReceiver4= null;
				if(null!= nextMemberAgentId){
					try {
						V3xOrgMember nextMemberAgent= orgManager.getMemberById(nextMemberAgentId);
						if(null!=nextMemberAgent && nextMemberAgent.isValid()){//代理人员可用
							String key2= "node.affair.overTerm.sysautoreplace1.agent";
					        if(null!=edocSummary){
					        	key2= "node.affair.overTerm.sysautoreplace1.edoc.agent";
					        }
					        msgContent4 = new MessageContent(key2, 
					        		newAffair.getSubject(),
					        		currentMember.getName()+"("+currentOrgAccount.getShortname()+")",
					        		nextMember.getName()+"("+nextOrgAccount.getShortname()+")",
					        		nextMember.getName()+"("+nextOrgAccount.getShortname()+")");
							msgReceiver4 = new MessageReceiver(newAffair.getId(), nextMemberAgent.getId(), messageLink, newAffair.getId().toString()); 
						}
					} catch (Throwable e) {
						log.warn(e.getMessage(), e);
					}
				}
				if(nextMember.isValid()){
					userMessageManager.sendSystemMessage(msgContent3, appEnum, sendId, msgReceiver3);
				}
				if( null!=msgContent4 && null!= msgReceiver4){//给代理人发消息
					userMessageManager.sendSystemMessage(msgContent4, appEnum, sendId, msgReceiver4);
				}
			} catch (Throwable e) {
				log.warn(e.getMessage(), e);
			}
			return newAffair;
		}catch (Throwable e) {
			log.warn(e.getMessage(), e);
			return newAffair;
		}finally{
			releaseLockForQuartz(processId,affair.getObjectId(),edocSummary);
		}
	}

	/**
	 * 公文自动跳过
	 * @param colSummary
	 * @param affair
	 * @param bpmSeeyonPolicy
	 * @param bpmActivity
	 * @param user
	 * @throws Exception 
	 */
	private void sysAutoRunCase(EdocSummary colSummary, Affair affair,
			BPMSeeyonPolicy bpmSeeyonPolicy, BPMActivity bpmActivity,User user) throws Exception {
		EdocOpinion signOpinion = new EdocOpinion();
		signOpinion.isDeleteImmediate = false;
        signOpinion.affairIsTrack = false;
        signOpinion.setIsHidden(false);
        signOpinion.setIdIfNew();
        String content= ResourceBundleUtil.getString(com.seeyon.v3x.common.usermessage.Constants.DEFAULT_MESSAGE_RESOURCE, Locale.getDefault(), "node.affair.overTerm.sysautoruncase.opinion");
        //signOpinion.setContent(content);
        signOpinion.setNodeId(Long.parseLong(bpmActivity.getId()));
        signOpinion.setEdocSummary(colSummary);
        signOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        signOpinion.setOpinionType(EdocOpinion.OpinionType.sysAutoSignOpinion.ordinal());
        signOpinion.setAttribute(EdocOpinion.OpinionType.sysAutoSignOpinion.ordinal());
        if(Strings.isBlank(signOpinion.getPolicy())){
        	signOpinion.setPolicy(affair.getNodePolicy());
        }
        boolean upd=false;
        Map<String,Object> namedParameter = new HashMap<String,Object>();
        if("qianfa".equals(bpmSeeyonPolicy.getId())){//here!!!!
        	String issuerName = user.getName();
    		try{
    			issuerName = orgManager.getMemberById(affair.getMemberId()).getName();
    		}catch(Throwable e){
    			log.error("查找人员错误", e);
    		}
        	if(Strings.isNotBlank(colSummary.getIssuer())){
        		String separator = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.separator.label");
        		issuerName+=separator+colSummary.getIssuer();
        	}
        	colSummary.setIssuer(issuerName);
    		edocStatManager.updateElement(colSummary);
    		namedParameter.put("issuer", issuerName);
        	//如果有多人签发，则取最后一个签发节点审批的时间为签发时间
    		colSummary.setSigningDate(new Date(System.currentTimeMillis()));
        	namedParameter.put("signingDate", new Date(System.currentTimeMillis()));
        	if(colSummary.getHasArchive() && colSummary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()){
        		edocManager.setArchiveIdToAffairsAndSendMessages(colSummary,affair,true);
        	}
        	upd=true;
        }
        signOpinion.setCreateUserId(affair.getMemberId());
//        edocManager.saveOpinion(signOpinion, false);
        if(upd){
        	edocSummaryDao.update(colSummary.getId(), namedParameter);
        }
		Map<String, Object> data = new HashMap<String, Object>();
		Long workItemId = affair.getSubObjectId();
		data.put("CurrentActivity", bpmActivity);
		data.put("currentWorkitemId", new Long(workItemId));//当前处理者
		DateSharedWithWorkflowEngineThreadLocal.setColSummary(colSummary);
		DateSharedWithWorkflowEngineThreadLocal.setOperationType(WorkflowEventListener.AUTOSKIP);
		DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), content, 2,false);
		DateSharedWithWorkflowEngineThreadLocal.setFinishAffairId(affair.getId());
		data.put("SYS_AUTOSKIP", "1");
		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
        wim.finishWorkItem(affair.getMemberId().toString(), workItemId, null, null, data, null);
	}

	/**
	 * 协同自动跳过该节点
	 * @param colSummary
	 * @param affair
	 * @param bpmSeeyonPolicy
	 * @param bpmActivity
	 * @throws Exception 
	 */
	private void sysAutoRunCase(ColSummary colSummary,Affair affair,BPMSeeyonPolicy bpmSeeyonPolicy,BPMActivity bpmActivity,Map<String, String[]> fieldDataBaseMap) throws Exception {
		ColOpinion signOpinion = new ColOpinion();
		signOpinion.isDeleteImmediate = false;
        signOpinion.affairIsTrack = false;
        signOpinion.setIsHidden(false);
        signOpinion.setIdIfNew();
        String content= ResourceBundleUtil.getString(com.seeyon.v3x.common.usermessage.Constants.DEFAULT_MESSAGE_RESOURCE, Locale.getDefault(), "node.affair.overTerm.sysautoruncase.opinion");
        //signOpinion.setContent(content);
        signOpinion.setSummaryId(colSummary.getId());
        signOpinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
        signOpinion.setOpinionType(ColOpinion.OpinionType.sysAutoSignOpinion.ordinal());
        signOpinion.setWriteMemberId(affair.getMemberId());
        colManager.saveOpinion(signOpinion,false);
  
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("fieldValueMap", fieldDataBaseMap);
		Long workItemId = affair.getSubObjectId();
		data.put("CurrentActivity", bpmActivity);
		data.put("currentWorkitemId", new Long(workItemId));//当前处理者
		DateSharedWithWorkflowEngineThreadLocal.setColSummary(colSummary);
		DateSharedWithWorkflowEngineThreadLocal.setOperationType(WorkflowEventListener.AUTOSKIP);
		DateSharedWithWorkflowEngineThreadLocal.setFinishWorkitemOpinionId(signOpinion.getId(), signOpinion.getIsHidden(), content, signOpinion.getAttitude(),false);
		data.put("SYS_AUTOSKIP", "1");
		WorkItemManager wim = WAPIFactory.getWorkItemManager("Task_1");
        wim.finishWorkItem(affair.getMemberId().toString(), workItemId, null, null, data, null);
        
        BPMSeeyonPolicy policy = bpmActivity.getSeeyonPolicy();
        if(colSummary.getFormAppId() != null && policy != null){
        	String nodePermissionPolicy = policy.getId();
        	if("formaudit".equals(nodePermissionPolicy)){
        		IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
        		if(iOperBase != null){
        			try {
	        			//审核通过：2
	        			iOperBase.updateState4Form(colSummary.getFormAppId(), colSummary.getFormRecordId(), 2);
        			} catch (Exception e){
        				log.error("更新表单动态表单state时出错！", e);
        			}
        		}
        	}
        }
        
        //流程处理事件通知
        CollaborationProcessEvent event = new CollaborationProcessEvent(this);
        event.setSummaryId(colSummary.getId());
        event.setAffairId(affair.getId());
        EventDispatcher.fireEvent(event);
        if(colSummary.getState().intValue() == Constant.flowState.finish.ordinal()){
        	//流程正常结束通知
        	CollaborationFinishEvent finishEvent = new CollaborationFinishEvent(this);
        	finishEvent.setSummaryId(colSummary.getId());
        	finishEvent.setAffairId(affair.getId());
        	EventDispatcher.fireEvent(finishEvent);
        }
	}

	/**
	 * @param colManager the colManager to set
	 */
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	/**
	 * @param orgManger the orgManger to set
	 */
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	/**
	 * @param appLogManager the appLogManager to set
	 */
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	/**
	 * @param processLogManager the processLogManager to set
	 */
	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}

	/**
	 * @param edocManager the edocManager to set
	 */
	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}

	/**
	 * @param edocStatManager the edocStatManager to set
	 */
	public void setEdocStatManager(EdocStatManager edocStatManager) {
		this.edocStatManager = edocStatManager;
	}

	/**
	 * @param edocSummaryDao the edocSummaryDao to set
	 */
	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao) {
		this.edocSummaryDao = edocSummaryDao;
	}

	/**
	 * @param newflowManager the newflowManager to set
	 */
	public void setNewflowManager(NewflowManager newflowManager) {
		this.newflowManager = newflowManager;
	}

	/**
	 * @param handWriteManager the handWriteManager to set
	 */
	public void setHandWriteManager(HandWriteManager handWriteManager) {
		this.handWriteManager = handWriteManager;
	}

	/**
	 * @param edocSummaryManager the edocSummaryManager to set
	 */
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
}