package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.engine.wapi.WorkItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;


public class EdocMessageHelper {
	private final static Log log = LogFactory.getLog(EdocMessageHelper.class);
	//查找哪些设置了部分跟踪但是没有跟踪当前用户的Affair,从trackAffairs中移除，然后返回跟踪事项列表
	public static List<Affair> getTrackAffairExcludePart(List<Affair> trackAffairs,List<ColTrackMember> trackMembers,Long currentMemberId){
		
		for(Iterator<Affair> it =trackAffairs.iterator();it.hasNext();){
			Affair affair = it.next();
			boolean partTrack = false; //设置的是部分跟踪
			boolean isTrackCurrentMemebr  = false; //设置部分跟踪的时候是否跟踪了当前的用户
			for(ColTrackMember colTrackMember:trackMembers){
				if(affair.getId().equals(colTrackMember.getAffairId())){
					partTrack = true;
					if(colTrackMember.getTrackMemberId().equals(currentMemberId)){
						isTrackCurrentMemebr = true;
					}
				}
			}
			//设置了部分跟踪但是没有跟踪当前用户的
			if(partTrack && !isTrackCurrentMemebr) it.remove();
		}
		return trackAffairs;
	}
	//工作项完成消息提醒
	public static Boolean workitemFinishedMessage(AffairManager affairManager, OrgManager orgManager, 
			EdocManager edocManager, UserMessageManager userMessageManager, Affair affair, Long summaryId){
		User user = CurrentUser.get();

        V3xOrgMember theMember = getMemberById(orgManager, affair.getMemberId());
        String name = theMember.getName();
        List<Affair> trackingAffairLists = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
        List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
        if(trackingAffairLists.size()<=0 && pushMemberIds.isEmpty()){return true;}
        //yangzd 过滤掉重复的信息发送------发起人和处理人存在同一节点，都设置了跟踪时，会收到重复的系统消息22613
        List<Affair> trackingAffairList=new ArrayList<Affair>();
        trackingAffairList.addAll(trackingAffairLists);
        List<ColTrackMember> trackMembers = edocManager.getColTrackMembersByObjectIdAndTrackMemberId(summaryId,null);
        trackingAffairList = getTrackAffairExcludePart(trackingAffairList, trackMembers,affair.getMemberId());
        if(trackingAffairList.isEmpty()){
        	return true;
        }
       
        String opinionContent = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinion();
        // {5,choice,-1#|1#[已阅]|2#[同意]|3#[不同意]}
        int opinionAttitude = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionAttitude();
        // {3,choice,-1#|0# 意见被隐藏|1#意见：{4}|2# {4}}
        // 0:意见被隐藏; -1：意见内容为空; 1: 无态度有内容（内容前面加“意见：”）; 2: 有态度有内容
        int opinionType = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionHidden() ? 0 : Strings.isBlank(opinionContent) ? -1 : opinionAttitude == -1 ? 1: 2;
        // -1:无附件或者意见被隐藏; 1: 无态度且无内容（内容前面加“意见：”）; 2:有态度或有内容，有附件
        // {6,choice,-1#|1#意见：(附)|2#(附)}
        int opinionAtt = (!DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionUploadAtt() || opinionType == 0) ? -1 : (Strings.isBlank(opinionContent) && opinionAttitude == -1 ? 1 : 2);
        
    	//有内容，有附件：减少4个字节
    	int deviation = opinionAtt == 2 ? -4 : 0;
    	
    	opinionContent = MessageUtil.getComment4Message(opinionContent, deviation);
        //yangzd
        ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(trackingAffairList.get(0).getApp());
        try{
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	Set <Long> members = new HashSet<Long>();
	        for(Affair affair1 : trackingAffairList){
	        	//设置了跟踪也不给自己发信息。 31509
	        	if(affair1.getMemberId().longValue()==user.getId()) continue;
	        
	            Long affairId = affair1.getId();
	            Long recieverMemberId = affair1.getMemberId();
		    	Long transactorId  =  affair1.getTransactorId();
		    	if(transactorId!=null && isEdocProxy(recieverMemberId,transactorId))
		    		recieverMemberId = transactorId;
		    	if(!members.contains(recieverMemberId)){
		    		members.add(recieverMemberId);
		    		receivers.add(new MessageReceiver(affairId, recieverMemberId,"message.link.edoc.done",affairId.toString()));
		    	}
	        }
	        for(Long[] push :pushMemberIds ){
	        	if(!members.contains(push[1])){
	        		receivers.add(new MessageReceiver(push[0], push[1],"message.link.edoc.done",push[0]));
	        	}
	        }
	        
	        MessageContent mc = new MessageContent("edoc.deal", name,affair.getSubject(),affair.getApp(), opinionType, opinionContent, opinionAttitude, opinionAtt);
	        mc.setImportantLevel(affair.getImportantLevel());
		    if(affair.getMemberId() != user.getId()){	
		    	receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.edoc.done", affair.getId()));
				String proxyName = user.getName();
				mc.add("edoc.agent.deal", proxyName);
		    }

		    userMessageManager.sendSystemMessage(mc, appEnum, theMember.getId(), receivers);
        }
        catch (Exception e) {
        	log.error("", e);
        }
		return true;
	}
	/**
	 * 判断是否给代理人发送消息.可能已经取消代理，或者代理过期了，这种情况就不发消息了
	 * @param affairMemberId  : affair的memberID
	 * @param affairTransactorId : affair.TransactorId affair的代理人的ID
	 * @return
	 */
	public static boolean  isEdocProxy(Long affairMemberId, Long affairTransactorId){
		//我设置了XX给我干活，返回他的Id
		Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.ordinal(), affairMemberId);
		if(agentId.equals(affairTransactorId)) return true;
		return false;
	}
	//给在竞争执行中被取消的affair发送消息提醒
	public static Boolean competitionCancel(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, WorkItem workitem, List<Affair> affairs){
		Affair affair = affairs.get(0);
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		User user = CurrentUser.get();
    	String userName = "";
    	if (user != null) {
            userName = user.getName();
        }
		try{
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
			for (Affair affair2 : affairs) {
				if(affair2.getMemberId().equals(user.getId())){
					continue;
				}
				receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId()));
			}
			
        	if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, affair.getMemberId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.competition", affair.getSubject(),
						proxyName,affair.getApp()).add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getAgentToId(), receivers);
		    }
        	else{
		    	userMessageManager.sendSystemMessage(new MessageContent("edoc.competition", affair.getSubject(), 
		    			userName,affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, affair.getSenderId(), receivers);
		    }
		}
		catch (Exception e) {
			log.error("发送消息异常", e);
		}
		return true;		
		/*
		User user = CurrentUser.get();
    	String userId = user.getId() + "";
    	V3xOrgMember theMember = null;    	
        ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
    	try {
    		List<BPMHumenActivity> humenList = new ArrayList<BPMHumenActivity>();
    		humenList.add((BPMHumenActivity)EdocHelper.getActivityByWorkitem(workitem));    		
    		int caseId = -1;
    		try{
    			caseId=EdocHelper.getCaseIdByWorkitem(workitem.getId());
    		}catch(Exception e)
    		{
    			throw new EdocException(e);
    		}
			WorkItemManager wim = null;
			List<WorkItem> workitems = null;
			try {
				wim = WAPIFactory.getWorkItemManager("Task_1");
				workitems = wim.getHistoryWorkItemList(userId, caseId, humenList);
			} catch (BPMException e1) {
				e1.printStackTrace();
			}
			
			for(WorkItem hisWorkitem : workitems){
				String workitemId = hisWorkitem.getId()+"";
				Affair _affair = affairManager.getBySubObject(appEnum, 
						Long.parseLong(workitemId));
				if(_affair.getState() == StateEnum.col_done.key()){
			        try {
			            theMember = orgManager.getEntityByID(V3xOrgMember.class,_affair.getMemberId());
			        } catch (BusinessException e) {
			            e.printStackTrace();
			        }
				}
			}
		} catch (EdocException e) {
			log.error("", e);
		}
		try{
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId()));
        	if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
			    try {
			       	member = orgManager.getEntityByID(V3xOrgMember.class,user.getAgentToId());
			    } catch (BusinessException e) {
			    	e.printStackTrace();
			    }
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.competition", affair.getSubject(),
						proxyName,EdocUtil.getEdocTypeLocalLanguage(affair.getApp())).add("edoc.agent.deal", user.getName()), appEnum, user.getAgentToId(), receivers);
		    }else{
		    	userMessageManager.sendSystemMessage(new MessageContent("edoc.competition", affair.getSubject(), 
        				theMember.getName(),EdocUtil.getEdocTypeLocalLanguage(affair.getApp())), appEnum, affair.getSenderId(), receivers);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	*/
	}
	/**
	 * 公文流程结束，给流程中的所有Affair发送消息
	 * @return
	 */
	public static void processFinishedAutoPigeonhole(AffairManager affairManager,UserMessageManager userMessageManager 
			,EdocSummary summary,Affair affair,OrgManager orgManager,
			String pigeonholePath,ProcessLogManager processLogManager,AppLogManager appLogManager){
		String operName="";
    	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
    	List<Affair> affairs = affairManager.getALLAvailabilityAffairList(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), summary.getId(), false);
    	List<String> nameList = new ArrayList<String>();
    	for(Affair tempAffair : affairs){      
    			//if(tempAffair.getArchiveId() != null) continue;
                Long memberId = tempAffair.getMemberId();
                if(nameList.contains(memberId.toString()))continue;
                Long affId = tempAffair.getId();
                receivers.add(new MessageReceiver(affId, memberId,"message.link.edoc.done",affId.toString()));
                nameList.add(memberId.toString());
        }
    	User user = CurrentUser.get();
	    if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
			V3xOrgMember member = null;
		    try {
		       	member = orgManager.getEntityById(V3xOrgMember.class,user.getAgentToId());
		       	operName = member.getName();        				
		    } catch (BusinessException e) {
		    	log.error("公文归档，未发现制定人员信息："+user.getAgentToId(),e);
		    }
	    }else{
	    	operName = user.getName();        		     	
	    }
	    try{
		    MessageContent content=new MessageContent("edoc.pigeonhole.auto",summary.getSubject(),EdocUtil.getAppCategoryByEdocType(summary.getEdocType()).getKey(),pigeonholePath)
		    	.setImportantLevel(summary.getImportantLevel());
		    userMessageManager.sendSystemMessage(content, EdocUtil.getAppCategoryByEdocType(summary.getEdocType()),user.getId(),receivers);
	    }
	    catch(Exception e){
	    	log.error("公文流程自动结束，发送删除已办，已发事项的消息",e);
	    }
	}
	//给在督办中被删除的affair发送消息提醒
	public static Boolean superviseDelete(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, WorkItem workitem, List<Affair> affairs) {
		if(affairs == null || affairs.isEmpty()){
			return true;
		}
		User user = CurrentUser.get();
    	String userName = "";
    	if (user != null) {
            userName = user.getName();
        }
    	Integer importmentLevel = affairs.get(0).getImportantLevel();
    	try{
    		Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
    		Affair firstAffair = affairs.get(0);
	    	for(Affair affair : affairs){
    			if(affair.getMemberId() != user.getId() && 
    					MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(), user.getId()) != null){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, affair.getMemberId());
    				String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent("edoc.delete", affair.getSubject(),
    						proxyName, affair.getApp()).add("edoc.agent.deal", user.getName()).setImportantLevel(importmentLevel), ApplicationCategoryEnum.valueOf(affair.getApp()), member.getId(), new MessageReceiver(affair.getId(), affair.getMemberId()));
    			}else{
    				receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId()));
    			}
	    	}
	    	userMessageManager.sendSystemMessage(new MessageContent("edoc.delete", firstAffair.getSubject(), 
					userName, firstAffair.getApp()).setImportantLevel(importmentLevel), ApplicationCategoryEnum.valueOf(firstAffair.getApp()), firstAffair.getSenderId(), receivers);
    	} catch (Exception e) {
    		log.error("发送消息异常", e);
    	}
		return true;
	}

	//给在终止操作中被取消的affair发送消息提醒
	public static Boolean terminateCancel(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, WorkItem workitem, Affair currentAffair, List<Affair> trackingAndPendingAffairs) {
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(currentAffair.getApp());
		User user = CurrentUser.get();
		try{
	    	String opinionContent = MessageUtil.getComment4Message(DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinion());
	    	int opinionType = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionHidden() ? 0 : Strings.isBlank(opinionContent) ? -1 : 1;
	    	
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
			for (Affair affair : trackingAndPendingAffairs) {
				//当前处理者不需要收到终止消息提醒
				if(user.getId() != affair.getMemberId().longValue() && affair.getSenderId().longValue() != affair.getMemberId().longValue()){
					receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(), "message.link.edoc.done", affair.getId()));
				}
			}
			
			Affair senderAffair = affairManager.getCollaborationSenderAffair(currentAffair.getObjectId());;
			receivers.add(new MessageReceiver(senderAffair.getId(), senderAffair.getMemberId(), "message.link.edoc.done", senderAffair.getId()));
			
			//从ThreadLocal中获取终止流程的Affair
            Affair theStopAffair = DateSharedWithWorkflowEngineThreadLocal.getTheStopAffair();
            int importmentLevel = currentAffair.getImportantLevel();
            if(theStopAffair != null){
                V3xOrgMember m = orgManager.getMemberById(theStopAffair.getMemberId());
                String memberName = Functions.showMemberNameOnly(theStopAffair.getMemberId());
                if(theStopAffair.getTransactorId() != null){ //由代理人终止
                    V3xOrgMember proxyM = orgManager.getMemberById(theStopAffair.getTransactorId());
                    userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", currentAffair.getSubject(),
                    		memberName, currentAffair.getApp(), opinionType, opinionContent).add("edoc.agent.deal", proxyM.getName()).setImportantLevel(importmentLevel), appEnum, m.getId(), receivers);
                }
                else{
                    userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", currentAffair.getSubject(), 
                    		memberName, currentAffair.getApp(), opinionType, opinionContent).setImportantLevel(importmentLevel), appEnum, m.getId(), receivers);
                }
            }
            else{
                userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", currentAffair.getSubject(), 
                        user.getName(), currentAffair.getApp(), opinionType, opinionContent).setImportantLevel(importmentLevel), appEnum, user.getId(), receivers);
            }
            
		} catch (Exception e) {
			log.error("发送消息异常", e);
		}
		return true;
	}
	
	//加签消息提醒
	public static Boolean insertPeopleMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, List<String> partyNames, EdocSummary summary, Affair affair){
		User user = CurrentUser.get();
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		try {
            if (partyNames != null) {
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
                for (Affair trackingAffair : trackingAffairList) {
                	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.edoc.done",
                			trackingAffair.getId().toString()));
                }
                if(affair.getMemberId() != user.getId()){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, affair.getMemberId());
    			    String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent("edoc.addAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","),affair.getApp())
    				.add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, affair.getMemberId(), receivers);
                }else{
                	userMessageManager.sendSystemMessage(new MessageContent("edoc.addAssign", summary.getSubject(), user.getName(), 
                		StringUtils.join(partyNames.iterator(), ","),affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getId(), receivers);
                }
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	//多级会签消息提醒
	public static Boolean addMoreSignMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, List<String> partyNames, EdocSummary summary, Affair affair){
		User user = CurrentUser.get();
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		try {
            if (partyNames != null) {
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
                for (Affair trackingAffair : trackingAffairList) {
                	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.edoc.done",
                			trackingAffair.getId().toString()));
                }
                if(affair.getMemberId() != user.getId()){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, affair.getMemberId());
    			    String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent("edoc.addMoreAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","),affair.getApp())
    				.add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, affair.getMemberId(), receivers);
                }else{
                	userMessageManager.sendSystemMessage(new MessageContent("edoc.addMoreAssign", summary.getSubject(), user.getName(), 
                		StringUtils.join(partyNames.iterator(), ","),affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getId(), receivers);
                }
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	//减签消息提醒
	public static Boolean deletePeopleMessage(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, List<String> partyNames, EdocSummary summary, Affair affair){
		User user = CurrentUser.get();
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		try {
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
            List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
            for (Affair trackingAffair : trackingAffairList) {
                 receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.edoc.done",trackingAffair.getId().toString()));
            }
            if(affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, affair.getMemberId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.decreaseAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","),affair.getApp())
				.add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, affair.getMemberId(), receivers);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("edoc.decreaseAssign", summary.getSubject(), 
            			user.getName(), StringUtils.join(partyNames.iterator(), ","),affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getId(), receivers);
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	//回退消息提醒
	//直接受到回退影响的节点，无论是否跟踪，都需要收到消息。包括{1.当前待办节点，2.当前节点的上一节点}
	//其它不受到影响的已办节点，若做了跟踪，也要受到消息，如果没有跟踪，不收到消息。
	public static Boolean stepBackMessage(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, List<Affair> allTrackAffairLists, Affair affair, Long summaryId, EdocOpinion signOpinion) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
        String userName = "";
        if (user != null) {
            userName = user.getName();
        }
        ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.valueOf(affair.getApp());
        try {
        	//给所有待办事项发起协同被回退消息提醒
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	Set<Long> filterMember = new HashSet<Long>();
        	
        	//1.发起者 待发或者已发
        	for (Affair waitOrSentAffair : allTrackAffairLists) {
        		if (waitOrSentAffair.getState() == StateEnum.col_waitSend.key() 
        				|| waitOrSentAffair.getState() == StateEnum.col_sent.key()) {
        			receivers.add(new MessageReceiver(waitOrSentAffair.getId(), waitOrSentAffair.getSenderId(), "message.link.edoc.done", waitOrSentAffair.getId().toString()));
    	        	filterMember.add(waitOrSentAffair.getSenderId());
    	        	break;
        		} 
        	} 
        	
        	//2.当前节点父节点。
        	Map<Long, Long> affairMap = DateSharedWithWorkflowEngineThreadLocal.getAffairMap();
        	for(Long key : affairMap.keySet()){
        		Long affId = affairMap.get(key);
        		receivers.add(new MessageReceiver(affId, key,"message.link.edoc.pending", affId.toString()));
        		filterMember.add(key);
        		Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(),key);
				if(agentId!=null && !filterMember.contains(agentId) && user.getId()!= agentId){
					receivers.add(new MessageReceiver(affId,agentId,"message.link.edoc.pending", affId.toString()));
					filterMember.add(agentId);
				}
        	}
        	//3、跟踪事项
        	for(Affair _affair : allTrackAffairLists){
        		if(!filterMember.contains(_affair.getMemberId()) && user.getId() != _affair.getMemberId()){
    				receivers.add(new MessageReceiver(_affair.getId(), _affair.getMemberId()));
    				Long transactorId = _affair.getTransactorId();
    				if(transactorId!=null && !filterMember.contains(transactorId) && user.getId()!= transactorId){
    					receivers.add(new MessageReceiver(_affair.getId(), _affair.getTransactorId()));
    					filterMember.add(_affair.getTransactorId());
    				}
        			filterMember.add(_affair.getMemberId());
        		}
        	}
        	//回退的时候其他影响的节点，比如兄弟节点。
        	Map<Long, Long> allStepBackAffectAffairMap = DateSharedWithWorkflowEngineThreadLocal.getAllStepBackAffectAffairMap();
        	for(Long key : allStepBackAffectAffairMap.keySet()){
        		//不给已发的人重复发
        		if(!filterMember.contains(key) && user.getId() != key){
        			Long affairId = allStepBackAffectAffairMap.get(key);
        			receivers.add(new MessageReceiver(affairId, key));
    				filterMember.add(key);
    				
    				//代理
            		Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(),key);
            		if(agentId!=null && !filterMember.contains(agentId) && user.getId()!= agentId){
            			receivers.add(new MessageReceiver(affairId, agentId));
    					filterMember.add(agentId);
            		}
        		}
        	}
        	
        	String opinionContent = MessageUtil.getComment4Message(signOpinion.getContent());
        	int opinionType = Strings.isTrue(signOpinion.getIsHidden()) ? 0 : Strings.isBlank(opinionContent) ? -1 : 1;
        	
        	if(affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, affair.getMemberId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.stepback", affair.getSubject(), proxyName ,affair.getApp(), opinionType, opinionContent)
				.add("edoc.agent.deal", user.getName()).setImportantLevel(importantLevel), appEnum,affair.getMemberId(), receivers, importantLevel);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("edoc.stepback", affair.getSubject(), userName ,affair.getApp(), opinionType, opinionContent).setImportantLevel(importantLevel),
            			appEnum, user.getId(), receivers, importantLevel);
            }
        	
        } catch (Exception e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	

	//工作项终止消息提醒
	public static Boolean stepStopMessage(AffairManager affairManager, OrgManager orgManager, 
			EdocSummary summary, UserMessageManager userMessageManager, Affair affair) {
		User user = CurrentUser.get();
		V3xOrgMember theMember = getMemberById(orgManager, affair.getMemberId());
		String name = theMember.getName();
		List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		try{
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
			for(Affair affair1 : trackingAffairList){
				Long memberId = affair1.getMemberId();
				Long affairId = affair1.getId();
				receivers.add(new MessageReceiver(affairId, memberId,"message.link.edoc.done",affairId.toString()));
			}
			if(affair.getMemberId() != user.getId()){
				//V3xOrgMember member = getMemberById(orgManager,affair.getMemberId());
				//String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", summary.getSubject(), name, affair.getApp()).add("edoc.agent.deal", user.getName()).setImportantLevel(summary.getImportantLevel()), appEnum, affair.getMemberId(), receivers);
			}else{
				userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", summary.getSubject(), name, affair.getApp()).setImportantLevel(summary.getImportantLevel()), appEnum, theMember.getId(), receivers);
			}
		} catch (Exception e) {
			log.error("发送消息异常", e);
		}
		return true;
	}
	
	//管理员终止流程消息提醒
	public static Boolean adminStopMessage(AffairManager affairManager, OrgManager orgManager, 
			EdocSummary summary, UserMessageManager userMessageManager, Affair affair) {
		User user = CurrentUser.get();
        List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
        ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
        try{
        	Map<Long, MessageReceiver> messageReceiverMap = new HashMap<Long, MessageReceiver>();
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
	        for(Affair affair1 : trackingAffairList){
	        	Long memberId = affair1.getMemberId();
	            Long affairId = affair1.getId();
	            messageReceiverMap.put(memberId, new MessageReceiver(affairId, memberId,"message.link.edoc.done",affairId.toString()));
	        }
	        for(Long key : messageReceiverMap.keySet()){
	        	receivers.add(messageReceiverMap.get(key));
	        }
            /* 
            MessageContent msgContent = null;
            if(user.isAdministrator() || user.isGroupAdmin()){
                msgContent = new MessageContent("edoc.terminate", summary.getSubject(), user.getName(), affair.getApp());
                userMessageManager.sendSystemMessage(msgContent, appEnum, user.getId(), receivers);
            }
            //TODO这里涉及到代理，目前未使用到，如使用到该条件需要修改。
            else if(affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, affair.getMemberId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", summary.getSubject(), user.getName(),affair.getApp()), appEnum, affair.getMemberId(), receivers);
		    }else{
		    }*/
            //消息改为“被单位管理员终止”
            userMessageManager.sendSystemMessage(new MessageContent("edoc.terminate", summary.getSubject(), user.getName(),affair.getApp()).setImportantLevel(summary.getImportantLevel()),appEnum,user.getId(),receivers);
        } catch (Exception e) {
        	log.error("发送消息异常", e);
        }
		return true;
	}
	
	//取回消息提醒
	public static Boolean takeBackMessage(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, List<Affair> pendingAffairList, Affair affair, Long summaryId){
		if(pendingAffairList == null || pendingAffairList.isEmpty()){
			return false;
		}
		User user = CurrentUser.get();
        String userName = "";
        if (user != null) {
            userName = user.getName();
        }
        ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
        try {
        	Integer importantLevel = pendingAffairList.get(0).getImportantLevel();
        	Set<MessageReceiver> receivers1 = new HashSet<MessageReceiver>();
            
        	for (Affair colPendingAffair : pendingAffairList) {
                receivers1.add(new MessageReceiver(colPendingAffair.getId(), colPendingAffair.getMemberId()));
            }
        	if(affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, affair.getMemberId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.takeback", affair.getSubject(), proxyName,affair.getApp())
				.add("edoc.agent.deal", user.getName()).setImportantLevel(importantLevel), appEnum, affair.getMemberId(), receivers1);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("edoc.takeback", affair.getSubject(), userName,affair.getApp()).setImportantLevel(importantLevel), appEnum, user.getId(), receivers1);
            }
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
            for (Affair trackingAffair : trackingAffairList) {
                receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.edoc.done",trackingAffair.getId().toString()));
            }
            if(affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, affair.getMemberId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("edoc.takeback", affair.getSubject(), proxyName,affair.getApp())
				.add("edoc.agent.deal", user.getName()).setImportantLevel(importantLevel), appEnum, affair.getMemberId(), receivers);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("edoc.takeback", affair.getSubject(), userName,affair.getApp()).setImportantLevel(importantLevel), appEnum, user.getId(), receivers);
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
        }
		return true;
	}
	
	//修改正文消息提醒
	//TODO代理消息提醒未实现
	public static Boolean saveBodyMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, EdocSummary summary){
		User user = CurrentUser.get();
    	Set<MessageReceiver> receivers = getBodyAttUpdateMessage(user,affairManager, summary);
    	ApplicationCategoryEnum appEnum=EdocUtil.getAppCategoryByEdocType(summary.getEdocType());
    	try {
			userMessageManager.sendSystemMessage(new MessageContent("edoc.modifyBody", summary.getSubject(), user.getName(),EdocUtil.getAppCategoryByEdocType(summary.getEdocType()).getKey()).setImportantLevel(summary.getImportantLevel()), appEnum, user.getId(), receivers);
		} catch (MessageException e) {
			log.error("修改正文消息提醒失败", e);
		}
		return true;
	}
	public static Boolean updateAttachmentMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, EdocSummary summary){
		User user = CurrentUser.get();
		// 发送系统消息
		List<Affair> affairs = affairManager.getALLAvailabilityAffairList(summary.getId(),false);

		Map<Long,MessageReceiver> member2Receiver = new HashMap<Long,MessageReceiver>();
		for (Affair affair : affairs) {
			StateEnum state = StateEnum.valueOf(affair.getState());
			switch(state){
			case col_sent:
				if(member2Receiver.get(affair.getMemberId()) == null && !affair.getMemberId().equals(user.getId()))
					member2Receiver.put(affair.getMemberId(),new MessageReceiver(summary.getId(), affair.getMemberId(), "message.link.edoc.sended", affair.getId()));
				break;
			case col_done:
				if(member2Receiver.get(affair.getMemberId()) == null && !affair.getMemberId().equals(user.getId()))
					member2Receiver.put(affair.getMemberId(),new MessageReceiver(summary.getId(), affair.getMemberId(), "message.link.edoc.done", affair.getId()));
				break;
			case col_pending:
				if (member2Receiver.get(affair.getMemberId()) == null
						&& !affair.getMemberId().equals(user.getId())) {
					member2Receiver
							.put(affair.getMemberId(),
									new MessageReceiver(summary.getId(), affair
											.getMemberId(),
											"message.link.edoc.pending", affair
													.getId()));
				}
				break;
			}
		}
    	try {
    		ApplicationCategoryEnum appEnum = EdocUtil.getAppCategoryByEdocType(summary.getEdocType());
    		userMessageManager.sendSystemMessage(new MessageContent("edoc_update_attachment", user.getName(), summary.getSubject(), appEnum.getKey()).setImportantLevel(summary.getImportantLevel()), appEnum,
				user.getId(), member2Receiver.values());
		} catch (MessageException e) {
			log.error("修改正文消息提醒失败", e);
		}
		return true;
	}
	private static Set<MessageReceiver> getBodyAttUpdateMessage(User user,AffairManager affairManager,EdocSummary summary){
		Long summaryId = summary.getId();
    	List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
    	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
    	for(Affair affair : trackingAffairList){
    		Long memberId = affair.getMemberId();
    		receivers.add(new MessageReceiver(affair.getId(), memberId, "message.link.edoc.done", affair.getId().toString()));
    	}
    	List<Affair> pendingAffair = affairManager.queryColPendingAffairList(summaryId, StateEnum.col_pending.key());
    	for(Affair affair1 : pendingAffair){
    		if(affair1.getMemberId() == user.getId())
    			continue;
    		receivers.add(new MessageReceiver(affair1.getId(), affair1.getMemberId(), "message.link.edoc.done", affair1.getId().toString()));
    	}
		return receivers;
	}
	
	/**
	 * 暂存待办消息提醒
	 * @param userMessageManager
	 * @param orgManager
	 * @param affairManager
	 * @param affair
	 * @return
	 * @deprecated 公文暂存待办不发消息
	 */
	public static Boolean zcdbMessage(UserMessageManager userMessageManager, OrgManager orgManager, AffairManager affairManager, Affair affair){
		User user = CurrentUser.get();
		String key = "edoc.saveDraft";
        Affair zcdbAffair = affair;
        List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(zcdbAffair.getObjectId());
        Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
        Set <Long> members = new HashSet<Long>();
    	for(Affair _affair : trackingAffairList){
    		members.add(_affair.getMemberId());
    		Long memberId = _affair.getMemberId();
    		receivers.add(new MessageReceiver(affair.getId(), memberId, "message.link.edoc.done", affair.getId().toString()));
    	}
    	//推送
	   for(Long[] push :pushMemberIds ){
       		if(!members.contains(push[1])){
       			receivers.add(new MessageReceiver(push[0], push[1],"message.link.edoc.done",push[0]));
       		}
       } 
    	ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
        if(zcdbAffair.getMemberId() != user.getId()){
        	MessageReceiver receiver = null;
        	Long startMemberId = zcdbAffair.getSenderId();
            receiver = new MessageReceiver(zcdbAffair.getId(), startMemberId,"message.link.edoc.done",zcdbAffair.getId().toString());
			V3xOrgMember member = null;
			member = getMemberById(orgManager, zcdbAffair.getMemberId());
		    String proxyName = member.getName();
			try {
				userMessageManager.sendSystemMessage(new MessageContent(key, zcdbAffair.getSubject(), proxyName,affair.getApp())
				.add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, zcdbAffair.getMemberId(), receiver);
			} catch (MessageException e) {
				log.error("", e);
			}
        }
        try {
            userMessageManager.sendSystemMessage(new MessageContent(key, zcdbAffair.getSubject(), user.getName(),affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getId(), receivers);
        } catch (MessageException e) {
            log.error("send message failed", e);
        }
		return true;
	}
	
	//会签消息提醒
	public static Boolean colAssignMessage(UserMessageManager userMessageManager, AffairManager affairManager,
			OrgManager orgManager, List<String> partyNames, EdocSummary summary, Affair affair){
		User user = CurrentUser.get();
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		try {
            if (partyNames != null) {
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
                for (Affair trackingAffair : trackingAffairList) {
                	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.edoc.done",trackingAffair.getId().toString()));
                }
                if(affair.getMemberId() != user.getId()){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, affair.getMemberId());
    			    String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent("edoc.colAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","),affair.getApp())
    				.add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, affair.getMemberId(), receivers);
                }else{
                	userMessageManager.sendSystemMessage(new MessageContent("edoc.colAssign", summary.getSubject(), user.getName(), StringUtils.join(partyNames.iterator(), ","),affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getId(), receivers);
                }
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
        }
		return true;
	}
	
	//知会消息提醒
	public static Boolean addInformMessage(UserMessageManager userMessageManager, AffairManager affairManager,
			OrgManager orgManager, List<String> partyNames, EdocSummary summary, Affair affair){
		return _addInformMessage(userMessageManager, affairManager,
				orgManager, partyNames, summary, affair,"edoc.addInform");
	}
	public static Boolean addPassReadMessage(UserMessageManager userMessageManager, AffairManager affairManager,
			OrgManager orgManager, List<String> partyNames, EdocSummary summary, Affair affair){
		return _addInformMessage(userMessageManager, affairManager,
				orgManager, partyNames, summary, affair,"edoc.addPassRead");
	}

	//知会,传阅消息提醒
	private static Boolean _addInformMessage(UserMessageManager userMessageManager, AffairManager affairManager,
			OrgManager orgManager, List<String> partyNames, EdocSummary summary, Affair affair,String msgLabel){
		User user = CurrentUser.get();
		ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(affair.getApp());
		try {
            if (partyNames != null) {
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
                for (Affair trackingAffair : trackingAffairList) {
                	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.edoc.done",trackingAffair.getId().toString()));
                }
                if(affair.getMemberId() != user.getId()){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, affair.getMemberId());
    			    String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent(msgLabel, summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","),affair.getApp())
    				.add("edoc.agent.deal", user.getName()).setImportantLevel(affair.getImportantLevel()), appEnum, affair.getMemberId(), receivers);
                }else{
                	userMessageManager.sendSystemMessage(new MessageContent(msgLabel, summary.getSubject(), user.getName(), StringUtils.join(partyNames.iterator(), ","),affair.getApp()).setImportantLevel(affair.getImportantLevel()), appEnum, user.getId(), receivers);
                }
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
        }
		return true;
	}
	
	public static V3xOrgMember getMemberById(OrgManager orgManager, Long memberId){
		V3xOrgMember member = null;
		try {
	       	member = orgManager.getEntityById(V3xOrgMember.class, memberId);
	    } catch (BusinessException e) {
	    	log.error("获取公文消息提醒对应人员失败", e);
	    	return null;
	    }
		return member;
	}
}