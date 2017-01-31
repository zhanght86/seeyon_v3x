package com.seeyon.v3x.collaboration.manager.impl;

import java.util.Date;
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
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;


public class ColMessageHelper {
	private final static Log log = LogFactory.getLog(ColMessageHelper.class);
	
	/**
	 * 查找哪些设置了部分跟踪但是没有跟踪当前用户的Affair,从trackAffairs中移除，然后返回跟踪事项列表
	 * @param trackAffairs
	 * @param trackMembers
	 * @param currentMemberId
	 * @return
	 */
	private static List<Affair> getTrackAffairExcludePart(List<Affair> trackAffairs,List<ColTrackMember> trackMembers,Long currentMemberId){
		
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
	/**
	 * 回复意见消息提醒
	 * 修改发送消息规则：首先根据人员跟踪设置判断，如设置为跟踪，则始终收到回复消息提醒；如设置为不跟踪，则此处对其发送消息时才能收到回复消息提醒。
	 * @param affairManager
	 * @param orgManager
	 * @param colManager
	 * @param userMessageManager
	 * @param affair
	 * @param summaryId
	 * @return
	 */
	public static void doCommentMessage(ColComment comment,
		OrgManager orgManager,
		AffairManager affairManager,
		UserMessageManager userMessageManager,
		ColManager colManager){
		
		User user = CurrentUser.get();
    	Long summaryId = comment.getSummaryId();
    	String memberName = comment.getMemberName();
    	String commentContent = MessageUtil.getComment4Message(comment.getContent());
    	int commentType = Strings.isTrue(comment.getIsHidden()) ? 0 :  Strings.isBlank(commentContent) ? -1 : 1;
    	
    	List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
        List<ColTrackMember> trackMembers = colManager.getColTrackMembersByObjectIdAndTrackMemberId(summaryId,null);
        trackingAffairList = getTrackAffairExcludePart(trackingAffairList,trackMembers,user.getId());
        List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
    	
        //不用给任何人提供消息.
    	if(pushMemberIds.isEmpty() && trackingAffairList.isEmpty()) return; 
    	
    	//取得消息的接受者。
     	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
    	Set <Long> members = new HashSet<Long>();
    	//定义一个Affair,主要取转发相关的信息.
    	Integer importantLevel = null;
    	String forwardMemberId = null;
    	String subject = comment.getSubject();
    	
    	for(Affair taffair : trackingAffairList){
    		if(importantLevel==null){
    			importantLevel= taffair.getImportantLevel();
    			forwardMemberId = taffair.getForwardMember();
    		}
            if(taffair.getIsDelete()){
                continue;
            }
            Long affairId = taffair.getId();
            Long recieverMemberId = taffair.getMemberId();
	    	Long transactorId  =  taffair.getTransactorId();
	    	if(transactorId!=null && isColProxy(recieverMemberId,transactorId))
	    		recieverMemberId = transactorId;
	    	if(!recieverMemberId.equals(user.getId())){
	    		members.add(recieverMemberId);
	    		receivers.add(new MessageReceiver(affairId, recieverMemberId,"message.link.col.done",affairId, comment.getId()));
	    	}
        }
        for(Long[] push :pushMemberIds ){
        	if(!members.contains(push[1])){
        		receivers.add(new MessageReceiver(push[0], push[1],"message.link.col.done",push[0], comment.getId()));
        	}
        }
        
        
        //过滤掉自己
        if(receivers!=null){
        	for(Iterator<MessageReceiver> it =receivers.iterator();it.hasNext();){
        		MessageReceiver r = it.next();
        		if(r.getReceiverId() == user.getId() ) it.remove();
        	}
        }
        
         try {
 			int forwardMemberFlag = 0;
 			String forwardMember = null;
 			if(Strings.isNotBlank(forwardMemberId)){
 				try {
 					forwardMember = orgManager.getMemberById(Long.parseLong(forwardMemberId)).getName();
 					forwardMemberFlag = 1;
 				}catch (Exception e) {
 				}
 			}
 			
             if(user.getAgentToId() != -1 && comment.getProxyName() != null){
                 V3xOrgMember theMember = null;
                 try {
                     theMember = orgManager.getEntityById(V3xOrgMember.class,user.getAgentToId());
                 } catch (BusinessException e) {
                     log.error("协同回复意见,获取接收人异常", e);
                 }
                 String proxyName = theMember.getName();
                 MessageContent mc = new MessageContent("col.reply", subject, proxyName, memberName, forwardMemberFlag, forwardMember, commentType, commentContent)
              	.add("col.agent.reply", user.getName()).setImportantLevel(importantLevel);
                 mc.setBody(comment.getContent(), Constants.EDITOR_TYPE_HTML, new Date());
                 userMessageManager.sendSystemMessage(mc,
                         ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
             }else{
     	       
     	        MessageContent mc =  new MessageContent("col.reply",subject, user.getName(), memberName, forwardMemberFlag, forwardMember, commentType, commentContent)
            	.setImportantLevel(importantLevel);
     	        mc.setBody(comment.getContent(), Constants.EDITOR_TYPE_HTML, new Date());
                 userMessageManager.sendSystemMessage(mc,
                         ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
             }
         } catch (MessageException e) {
             log.error("回复意见消息提醒失败", e);
         }
     }
	//工作项完成消息提醒
	public static Boolean workitemFinishedMessage(AffairManager affairManager, OrgManager orgManager, 
			ColManager colManager, UserMessageManager userMessageManager, Affair affair, Long summaryId) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
        V3xOrgMember theMember = null;
        theMember = getMemberById(orgManager, affair.getMemberId());
        List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
        List<ColTrackMember> trackMembers = colManager.getColTrackMembersByObjectIdAndTrackMemberId(summaryId,null);
        trackingAffairList = getTrackAffairExcludePart(trackingAffairList, trackMembers, affair.getMemberId());
        List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
        if(trackingAffairList.isEmpty() && pushMemberIds.isEmpty()){
        	return true;
        }
        
        String forwardMemberId = trackingAffairList.isEmpty() ? null : trackingAffairList.get(0).getForwardMember();
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
        	String opinionId = String.valueOf(DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionId());
        	String opinionContentAll = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinion();
        	int opinionAttitude = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionAttitude();
        	// 0:意见被隐藏; -1：意见内容为空; 1: 无态度有内容（内容前面加“意见：”）; 2: 有态度有内容
        	int opinionType = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionHidden() ? 0 : Strings.isBlank(opinionContentAll) ? -1 : opinionAttitude == -1 ? 1: 2;
        	// -1:无附件或者意见被隐藏; 1: 无态度且无内容（内容前面加“意见：”）; 2:有态度或有内容，有附件
        	int opinionAtt = (!DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionUploadAtt() || opinionType == 0) ? -1 : (Strings.isBlank(opinionContentAll) && opinionAttitude == -1 ? 1 : 2);
        	
        	//有内容，有附件：减少4个字节
        	int deviation = opinionAtt == 2 ? -4 : 0;
        	
        	String opinionContent = MessageUtil.getComment4Message(opinionContentAll, deviation);
        	
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	
        	//TODO 如果是发起者，意见对发起者隐藏要做单独处理
        	Set <Long> members = new HashSet<Long>();
        	for(Affair affair1 : trackingAffairList){
                if(affair1.getIsDelete()){
                    continue;
                }
	            Long affairId = affair1.getId();
	            Long recieverMemberId = affair1.getMemberId();
		    	Long transactorId  =  affair1.getTransactorId();
		    	if(transactorId!=null && isColProxy(recieverMemberId,transactorId))
		    		recieverMemberId = transactorId;
		    	if(!recieverMemberId.equals(user.getId())){
		    		members.add(recieverMemberId);
		    		receivers.add(new MessageReceiver(affairId, recieverMemberId,"message.link.col.done",affairId, opinionId));
		    	}
	        }
	        
	        for(Long[] push :pushMemberIds ){
	        	if(!members.contains(push[1])){
	        		receivers.add(new MessageReceiver(push[0], push[1],"message.link.col.done",push[0], opinionId));
	        	}
	        }
	        
	        MessageContent mc = new MessageContent("col.deal", theMember.getName(), affair.getSubject(), forwardMemberFlag, forwardMember, opinionType, opinionContent, opinionAttitude, opinionAtt);
	        mc.setBody(opinionContentAll, Constants.EDITOR_TYPE_HTML, new Date());
	        mc.setImportantLevel(importantLevel);
		    if(affair.getMemberId() != user.getId()){
		    	receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.col.done",affair.getId(), opinionId));
			    String proxyName = user.getName();
			    mc.add("col.agent.deal", proxyName);
		    }
		    
		    userMessageManager.sendSystemMessage(mc, ApplicationCategoryEnum.collaboration, theMember.getId(),receivers, importantLevel);
        }
        catch (Exception e) {
        	log.error("发送消息异常", e);
        }
		return true;
	}
	/**
	 * 判断是否给代理人发送消息.可能已经取消代理，或者代理过期了，这种情况就不发消息了
	 * @param affairMemberId  : affair的memberID
	 * @param affairTransactorId : affair.TransactorId affair的代理人的ID
	 * @return
	 */
	public static boolean  isColProxy(Long affairMemberId, Long affairTransactorId){
		//我设置了XX给我干活，返回他的Id
		Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.collaboration.ordinal(), affairMemberId);
		if(agentId.equals(affairTransactorId)) return true;
		return false;
	}
	//给在竞争执行中被取消的affair发送消息提醒
	public static Boolean competitionCancel(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, WorkItem workitem, List<Affair> affairs) {
		Affair affair = affairs.get(0);
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
		String userName = "";
		if (user != null) {
			userName = user.getName();
		}
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
		try{
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
			for (Affair affair2 : affairs) {
				if(affair2.getMemberId().equals(user.getId())){
					continue;
				}
				receivers.add(new MessageReceiver(affair2.getId(), affair2.getMemberId()));
				//竞争执行，删除提前提醒，超期提醒。
				if(affair2.getRemindDate() != null && QuartzHolder.hasQuartzJob("Remind"+affair2.getId())){
					QuartzHolder.deleteQuartzJob("Remind"+affair2.getId());
				}
				if(affair2.getDeadlineDate() != null && QuartzHolder.hasQuartzJob("DeadLine"+affair2.getId())){
					QuartzHolder.deleteQuartzJob("DeadLine"+affair2.getId());
				}
			}
			
			if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, user.getAgentToId());
				String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("col.competition", affair.getSubject(),
						proxyName, forwardMemberFlag, forwardMember).add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
			}else{
				userMessageManager.sendSystemMessage(new MessageContent("col.competition", affair.getSubject(), 
						userName, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, affair.getSenderId(), receivers, importantLevel);
			}
		}
		catch (Exception e) {
			log.error("发送消息异常", e);
		}
		return true;
	}
	
	//给在督办中被删除的affair发送消息提醒
    //FIXME 协同XX被XX删除的提示在这里，有问题待查
	public static Boolean superviseDelete(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, WorkItem workitem, List<Affair> affairs) {
		if(affairs != null && !affairs.isEmpty()){
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
			
			Affair firstAffair = affairs.get(0);
			Integer importantLevel = firstAffair.getImportantLevel();
			User user = CurrentUser.get();
			String userName = "";
			if (user != null) {
				userName = user.getName();
			}
			String forwardMemberId = firstAffair.getForwardMember();
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
				for(Affair affair : affairs){
					if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
						Set<MessageReceiver> agentReceivers = new HashSet<MessageReceiver>();
						agentReceivers.add(new MessageReceiver(affair.getId(), affair.getMemberId()));
						V3xOrgMember member = null;
						member = getMemberById(orgManager, user.getAgentToId());
						String proxyName = member.getName();
						userMessageManager.sendSystemMessage(new MessageContent("col.delete", affair.getSubject(),proxyName, forwardMemberFlag, forwardMember).add("col.agent.deal", user.getName())
								.setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
					}else{
						receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId()));
					}
				}
				if(!receivers.isEmpty()){
					userMessageManager.sendSystemMessage(new MessageContent("col.delete", firstAffair.getSubject(), 
							userName, forwardMemberFlag, forwardMember).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, firstAffair.getSenderId(), receivers, importantLevel);
				}
			} catch (Exception e) {
				log.error("发送消息异常", e);
			}
		}
		return true;
	}
	
	//给在终止操作中被取消的affair发送消息提醒
	public static Boolean terminateCancel(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, WorkItem workitem, Affair currentAffair, List<Affair> trackingAndPendingAffairs) {
		if(currentAffair.getSenderId().longValue()== currentAffair.getMemberId().longValue() 
				&& (currentAffair.getSubObjectId()==null || currentAffair.getSubObjectId().longValue()==-1)){//从已发中终止，作为系统自动终止的一个标志
			//do nothing
			log.debug("协同自动终止逾期自由流程，不需要再发终止消息!");
		}else{
			Integer importantLevel = currentAffair.getImportantLevel();
			User user = CurrentUser.get();
			String forwardMemberId = currentAffair.getForwardMember();
	    	String opinionId = String.valueOf(DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionId());
	    	String opinionContent = MessageUtil.getComment4Message(DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinion());
	    	int opinionType = DateSharedWithWorkflowEngineThreadLocal.getFinishWorkitemOpinionHidden() ? 0 : Strings.isBlank(opinionContent) ? -1 : 1;
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
				Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
				Set <Long> members = new HashSet<Long>();
				for (Affair affair : trackingAndPendingAffairs) {
					 members.add(affair.getMemberId());
					//当前处理者不需要收到终止消息提醒
					if(user.getId() != affair.getMemberId().longValue() && affair.getSenderId().longValue() != affair.getMemberId().longValue()){
						receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(), "message.link.col.done", affair.getId(), opinionId));
					}
				}
				
				List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
				for(Long[] push :pushMemberIds ){
					if(!members.contains(push[1])){
						receivers.add(new MessageReceiver(push[0], push[1],"message.link.col.done",push[0], opinionId));
					}
				}
				
				Affair senderAffair = affairManager.getCollaborationSenderAffair(currentAffair.getObjectId());;
				receivers.add(new MessageReceiver(senderAffair.getId(), senderAffair.getMemberId(), "message.link.col.done", senderAffair.getId(), opinionId));
				
				//从ThreadLocal中获取终止流程的Affair
	            Affair theStopAffair = DateSharedWithWorkflowEngineThreadLocal.getTheStopAffair();
	            if(theStopAffair != null){
	                V3xOrgMember m = orgManager.getMemberById(theStopAffair.getMemberId());
	                String memberName = Functions.showMemberNameOnly(theStopAffair.getMemberId());
	                if(theStopAffair.getTransactorId() != null){ //由代理人终止
	    				V3xOrgMember proxyM = orgManager.getMemberById(theStopAffair.getTransactorId());
	    				userMessageManager.sendSystemMessage(new MessageContent("col.terminate", currentAffair.getSubject(),
	    						memberName, forwardMemberFlag, forwardMember).add("col.agent.deal", proxyM.getName(), opinionType, opinionContent).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, m.getId(), receivers, importantLevel);
	                }
	                else{
	                    userMessageManager.sendSystemMessage(new MessageContent("col.terminate", currentAffair.getSubject(), 
	                    		memberName, forwardMemberFlag, forwardMember, opinionType, opinionContent).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, m.getId(), receivers, importantLevel);                    
	                }
				}
	            else{
					userMessageManager.sendSystemMessage(new MessageContent("col.terminate", currentAffair.getSubject(), 
	                        user.getName(), forwardMemberFlag, forwardMember, opinionType, opinionContent).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
				}
			}
			catch (Exception e) {
				log.error("发送消息异常", e);
			}
		}
		return true;
	}
	
	//加签消息提醒
	public static Boolean insertPeopleMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, List<String> partyNames, ColSummary summary, Affair affair,List<ColTrackMember> trackMembers) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
		try {
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
            List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
            trackingAffairList = getTrackAffairExcludePart(trackingAffairList, trackMembers, affair.getMemberId());
            for (Affair trackingAffair : trackingAffairList) {
            	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.col.done",
            			trackingAffair.getId().toString()));
            }
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
            if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, user.getAgentToId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("col.addAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember)
				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("col.addAssign", summary.getSubject(), user.getName(), 
            		StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	//减签消息提醒
	public static Boolean deletePeopleMessage(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, List<String> partyNames, ColSummary summary, Affair affair) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
		try {
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
            List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
            for (Affair trackingAffair : trackingAffairList) {
                 receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.col.done",trackingAffair.getId().toString()));
            }
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
            if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, user.getAgentToId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("col.decreaseAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember)
				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("col.decreaseAssign", summary.getSubject(), 
            			user.getName(), StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	//回退消息提醒
	public static Boolean stepBackMessage(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, List<Affair> allTrackAffairLists, Affair affair, Long summaryId,ColOpinion signOpinion) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
        String userName = "";
        if (user != null) {
            userName = user.getName();
        }
        
//        Map<Long,Affair> receiveAffairs = new HashMap<Long, Affair>();
//        for(Affair availabilityAffair : aLLAvailabilityAffairLists){
//        	receiveAffairs.put(availabilityAffair.getId(), availabilityAffair);
//        }
        
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
		
		//Map<Long, Long> affairMap = DateSharedWithWorkflowEngineThreadLocal.getAffairMap();
        
        try {
        	//给所有待办事项发起协同被回退消息提醒
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	Set<Long> filterMember = new HashSet<Long>();
        	
        	//1.发起者 待发或者已发
        	for (Affair waitOrSentAffair : allTrackAffairLists) {
        		if (waitOrSentAffair.getState() == StateEnum.col_waitSend.key()) {
        			receivers.add(new MessageReceiver(waitOrSentAffair.getId(), waitOrSentAffair.getSenderId(), "message.link.col.waiSend", waitOrSentAffair.getId().toString()));
    	        	filterMember.add(waitOrSentAffair.getSenderId());
    	        	break;
        		}else if (waitOrSentAffair.getState() == StateEnum.col_sent.key()) {
        			receivers.add(new MessageReceiver(waitOrSentAffair.getId(), waitOrSentAffair.getSenderId(), "message.link.col.done", waitOrSentAffair.getId().toString()));
    	        	filterMember.add(waitOrSentAffair.getSenderId());
    	        	break;
        		} 
        	} 
        	
        	//2.回退以后的代办节点，基本上是当前节点父节点。
        	Map<Long, Long> affairMap = DateSharedWithWorkflowEngineThreadLocal.getAffairMap();
        	for(Long key : affairMap.keySet()){
        		Long affId = affairMap.get(key);
        		receivers.add(new MessageReceiver(affId, key,
						"message.link.col.pending", affId.toString()));
        		filterMember.add(key);
        		Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.collaboration.key(),key);
				if(agentId!=null && !filterMember.contains(agentId) && user.getId()!= agentId){
					receivers.add(new MessageReceiver(affId, agentId,
    						"message.link.col.pending", affId));
					filterMember.add(agentId);
				}
        	}
        	//3、跟踪事项
        	for(Affair _affair  : allTrackAffairLists){
        		if(!filterMember.contains(_affair.getMemberId()) && user.getId() != _affair.getMemberId()){
    				receivers.add(new MessageReceiver(_affair.getId(), _affair.getMemberId()));
    				filterMember.add(_affair.getMemberId());
    				Long transactorId = _affair.getTransactorId();
    				if(transactorId!=null  && !filterMember.contains(transactorId) && user.getId()!= transactorId){
    					//todo:应该判断当前affair的代理人是否仍然是代理人，可能已经被取消了代理人设置。
    					receivers.add(new MessageReceiver(_affair.getId(), _affair.getTransactorId()));
    					filterMember.add(_affair.getTransactorId());
    				}
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
            		Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.collaboration.key(),key);
            		if(agentId!=null && !filterMember.contains(key) && user.getId()!= agentId){
            			receivers.add(new MessageReceiver(affairId, agentId));
    					filterMember.add(agentId);
            		}
        		}
        	}
        	
        	int messageFlag = 0;
        	if(!signOpinion.getIsHidden()&&Strings.isNotBlank(signOpinion.getContent())){
        		messageFlag = 1 ;
        	}
        	if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
				V3xOrgMember member = null;
				member = getMemberById(orgManager, user.getAgentToId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("col.stepback", affair.getSubject(), proxyName, forwardMemberFlag, forwardMember,signOpinion.getContent(),messageFlag)
				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
            }
        	else{
            	userMessageManager.sendSystemMessage(new MessageContent("col.stepback", affair.getSubject(), userName, forwardMemberFlag, forwardMember,signOpinion.getContent(),messageFlag)
            		.setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
            }
        }
        catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	//取回消息提醒
	public static Boolean takeBackMessage(AffairManager affairManager, OrgManager orgManager, 
			UserMessageManager userMessageManager, List<Affair> pendingAffairList, Affair affair, Long summaryId) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
        String userName = "";
        if (user != null) {
            userName = user.getName();
        }
        try {
        	Set<MessageReceiver> receivers1 = new HashSet<MessageReceiver>();
        	V3xOrgMember member = null;
        	Long memberId = null;
        	for (Affair colPendingAffair : pendingAffairList) {
        		memberId = colPendingAffair.getMemberId();
        		member = orgManager.getMemberById(memberId);
        		if(member != null && member.getAgentId() != V3xOrgEntity.DEFAULT_NULL_ID)
        			memberId = member.getAgentId();
                receivers1.add(new MessageReceiver(colPendingAffair.getId(), memberId));
            }
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
        	if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
        		member = getMemberById(orgManager, user.getAgentToId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("col.takeback", affair.getSubject(), proxyName, forwardMemberFlag, forwardMember)
				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers1, importantLevel);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("col.takeback", affair.getSubject(), userName, forwardMemberFlag, forwardMember)
            	.setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers1, importantLevel);
            }
        	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        	List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summaryId);
            for (Affair trackingAffair : trackingAffairList) {
                receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.col.done",trackingAffair.getId().toString()));
            }
            if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
            	member = getMemberById(orgManager, user.getAgentToId());
			    String proxyName = member.getName();
				userMessageManager.sendSystemMessage(new MessageContent("col.takeback", affair.getSubject(), proxyName, forwardMemberFlag, forwardMember)
				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
            }else{
            	userMessageManager.sendSystemMessage(new MessageContent("col.takeback", affair.getSubject(), userName, forwardMemberFlag, forwardMember)
            	.setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
            }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        } catch (BusinessException e) {
            log.error("send message failed", e);
        }
		return true;
	}
	
	private static Set<MessageReceiver> getMessageReceiver(User user,AffairManager affairManager, ColSummary summary){
		Long summaryId = summary.getId();
		List<Affair> allAvailableAffair = affairManager.getALLAvailabilityAffairList(summaryId, false);
		Map<Long, MessageReceiver> receiversMap = new HashMap<Long, MessageReceiver>();
		Long sendAffairId = null;
		for(Affair affair : allAvailableAffair){
			StateEnum state = StateEnum.valueOf(affair.getState());
			switch(state){
			case col_done:{
				Long memberId = affair.getMemberId();
				if(receiversMap.get(memberId) == null)
					receiversMap.put(memberId, new MessageReceiver(affair.getId(), memberId, "message.link.col.done", affair.getId().toString()));
			}
			break;
			case col_pending://待办覆盖其他
			{
				if(affair.getMemberId() != user.getId())
					receiversMap.put(affair.getMemberId(), new MessageReceiver(affair.getId(), affair.getMemberId(), "message.link.col.pending",
							affair.getId().toString()));
			}
			break;
			case col_sent:{
				Long sendId = affair.getSenderId();
				receiversMap.put(sendId, new MessageReceiver(affair.getId(), sendId, "message.link.col.done", affair.getId().toString()));
				sendAffairId = affair.getId();
			}
			break;
			}
		}
		/*
		//已办事项
    	List<Affair> doneAffairList = affairManager.getAvailabilityDoneAffairList(summaryId);
    	
    	if(doneAffairList != null){
	    	for(Affair affair : doneAffairList){
	    		Long memberId = affair.getMemberId();
	    		receiversMap.put(memberId, new MessageReceiver(affair.getId(), memberId, "message.link.col.done", affair.getId().toString()));
	    	}
    	}
    	
    	//待办事项
    	List<Affair> pendingAffair = affairManager.queryColPendingAffairList(summaryId, StateEnum.col_pending.key());
    	for(Affair affair1 : pendingAffair){
    		if(affair1.getMemberId() == user.getId())
    			continue;
    		receiversMap.put(affair1.getMemberId(), new MessageReceiver(affair1.getId(), affair1.getMemberId(), "message.link.col.pending",
    				affair1.getId().toString()));
    	}
    	//已发事项
    	Affair sentAffair = pendingAffair.get(0);
    	Long sendId = sentAffair.getSenderId();
    	receiversMap.put(sendId, new MessageReceiver(sentAffair.getId(), sendId, "message.link.col.done", sentAffair.getId().toString()));
    	*/
    	//督办人
		Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
    	ColSuperviseManager colSuperviseManager = (ColSuperviseManager)ApplicationContextHolder.getBean("colSuperviseManager");
    	ColSuperviseDetail superviseDetail = colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(), summaryId);
    	if(superviseDetail != null){
	    	Set<ColSupervisor> colSupervisorSet = superviseDetail.getColSupervisors();
	    	for(ColSupervisor colSupervisor : colSupervisorSet){
	    		Long colSupervisMemberId = colSupervisor.getSupervisorId();
	    		receiversMap.put(colSupervisMemberId, new MessageReceiver(sendAffairId, colSupervisMemberId,"message.link.col.done", sendAffairId.toString()));
	    	}
    	}
    	receivers.addAll(receiversMap.values());
    	/*for(Long key : receiversMap.keySet()){
    		receivers.add(receiversMap.get(key));
    	}*/
    	return receivers;
	}
	
	//修改正文消息提醒
	//TODO代理消息提醒未实现
	public static Boolean saveBodyMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, ColSummary summary) {
		User user = CurrentUser.get();
		Integer importantLevel = null;
		if(summary.getImportantLevel() > 1){
			importantLevel = summary.getImportantLevel();
		}
		Set<MessageReceiver> receivers = getMessageReceiver(user,affairManager, summary);
		
    	String forwardMemberId = summary.getForwardMember();
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
    	try {
			userMessageManager.sendSystemMessage(new MessageContent("col.modifyBody", summary.getSubject(), user.getName(), forwardMemberFlag, forwardMember)
				.setImportantLevel(importantLevel),
					ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
		} catch (MessageException e) {
			log.error("修改正文消息提醒失败", e);
		}
		return true;
	}
	
	public static Boolean saveUpdataAttMessage(AffairManager affairManager, UserMessageManager userMessageManager,
			OrgManager orgManager, ColSummary summary){
		User user = CurrentUser.get();
		Integer importantLevel = null;
		if(summary.getImportantLevel() > 1){
			importantLevel = summary.getImportantLevel();
		}
    	String forwardMemberId = summary.getForwardMember();
    	Set<MessageReceiver> receivers = getMessageReceiver(user,affairManager, summary);
    	
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
		try {
			userMessageManager.sendSystemMessage(new MessageContent("col.updateAtt", summary.getSubject(), user.getName(), forwardMemberFlag, forwardMember).setImportantLevel(importantLevel),
					ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
		} catch (MessageException e) {
			log.error("修改附件提醒失败", e);
		}
		return true;
	}
	
	//暂存待办消息提醒
	public static Boolean zcdbMessage(UserMessageManager userMessageManager, OrgManager orgManager,
			AffairManager affairManager, Affair zcdbAffair, ColOpinion opinion) {
		Integer importantLevel = zcdbAffair.getImportantLevel();
		User user = CurrentUser.get();
        List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(zcdbAffair.getObjectId());
        List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
        Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
        Set <Long> members = new HashSet<Long>();
        for(Affair _affair : trackingAffairList){
        	 members.add(_affair.getMemberId());
    		Long memberId = _affair.getMemberId();
    		receivers.add(new MessageReceiver(_affair.getId(), memberId, "message.link.col.done", _affair.getId().toString(), opinion.getId()));
    	}
        for(Long[] push :pushMemberIds ){
        	if(!members.contains(push[1])){
        		receivers.add(new MessageReceiver(push[0], push[1],"message.link.col.done",push[0], opinion.getId()));
        	}
        } 
    	String forwardMemberId = zcdbAffair.getForwardMember();
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
    	
    	//0-意见隐藏，-1内容为空，1有内容
    	int opinionType = Strings.isTrue(opinion.getIsHidden()) ? 0 : Strings.isBlank(opinion.getContent()) ? -1 : 1;
    	// -1:无附件或意见被隐藏; 1: 无内容（内容前面加“意见：”）; 2:有内容，有附件
    	int opinionAtt = (!opinion.isHasAtt() || opinionType == 0) ? -1 : (Strings.isBlank(opinion.getContent()) ? 1 : 2);
    	
    	//有内容，有附件：减少4个字节
    	int deviation = opinionAtt == 2 ? -4 : 0;
    	
    	String opinionContent = MessageUtil.getComment4Message(opinion.getContent(), deviation);
    	
        if(user.getAgentToId() != -1 && zcdbAffair.getMemberId() != user.getId()){
        	MessageReceiver receiver = null;
        	Long startMemberId = zcdbAffair.getSenderId();
            receiver = new MessageReceiver(zcdbAffair.getId(), startMemberId,"message.link.col.done",zcdbAffair.getId().toString(), opinion.getId());
			V3xOrgMember member = null;
			member = getMemberById(orgManager, user.getAgentToId());
		    String proxyName = member.getName();
			try {
				MessageContent mc = new MessageContent("col.saveDraft", zcdbAffair.getSubject(), proxyName, forwardMemberFlag, forwardMember, opinionType, opinionContent, opinionAtt).setImportantLevel(importantLevel)
				.add("col.agent.deal", user.getName());
				mc.setBody(opinion.getContent(), Constants.EDITOR_TYPE_HTML, new Date());
				userMessageManager.sendSystemMessage(mc, ApplicationCategoryEnum.collaboration, user.getAgentToId(), receiver, importantLevel);
			} catch (MessageException e) {
				log.error("send message failed", e);
				return false;
			}
        }
        else{
        	try {
        		MessageContent mc = new MessageContent("col.saveDraft", zcdbAffair.getSubject(), user.getName(), forwardMemberFlag, forwardMember, opinionType, opinionContent, opinionAtt)
        		.setImportantLevel(importantLevel);
        		mc.setBody(opinion.getContent(), Constants.EDITOR_TYPE_HTML, new Date());
        		userMessageManager.sendSystemMessage(mc, ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
        	} catch (MessageException e) {
        		log.error("send message failed", e);
        		return false;
        	}
        }
        return true;        	
	}
	
	//会签消息提醒
	public static Boolean colAssignMessage(UserMessageManager userMessageManager, AffairManager affairManager,
			OrgManager orgManager, List<String> partyNames, ColSummary summary, Affair affair) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
		try {
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
                for (Affair trackingAffair : trackingAffairList) {
                	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.col.done",trackingAffair.getId().toString()));
                }
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
                if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, user.getAgentToId());
    			    String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent("col.colAssign", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember)
    				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
                }else{
                	userMessageManager.sendSystemMessage(new MessageContent("col.colAssign", summary.getSubject(), user.getName(), StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember)
                		.setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
                }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	//知会消息提醒
	public static Boolean addInformMessage(UserMessageManager userMessageManager, AffairManager affairManager,
			OrgManager orgManager, List<String> partyNames, ColSummary summary, Affair affair) {
		Integer importantLevel = affair.getImportantLevel();
		User user = CurrentUser.get();
		try {
			Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                List<Affair> trackingAffairList = affairManager.getAvailabilityTrackingAffairBySummaryId(summary.getId());
                for (Affair trackingAffair : trackingAffairList) {
                	receivers.add(new MessageReceiver(trackingAffair.getId(), trackingAffair.getMemberId(),"message.link.col.done",trackingAffair.getId().toString()));
                }
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
                if(user.getAgentToId() != -1 && affair.getMemberId() != user.getId()){
    				V3xOrgMember member = null;
    				member = getMemberById(orgManager, user.getAgentToId());
    			    String proxyName = member.getName();
    				userMessageManager.sendSystemMessage(new MessageContent("col.addInform", summary.getSubject(), proxyName, StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember)
    				.add("col.agent.deal", user.getName()).setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getAgentToId(), receivers, importantLevel);
                }else{
                	userMessageManager.sendSystemMessage(new MessageContent("col.addInform", summary.getSubject(), user.getName(), StringUtils.join(partyNames.iterator(), ","), forwardMemberFlag, forwardMember)
                		.setImportantLevel(importantLevel), ApplicationCategoryEnum.collaboration, user.getId(), receivers, importantLevel);
                }
        } catch (MessageException e) {
            log.error("send message failed", e);
            return false;
        }
		return true;
	}
	
	public static V3xOrgMember getMemberById(OrgManager orgManager, Long memberId){
		V3xOrgMember member = null;
		try {
	       	member = orgManager.getEntityById(V3xOrgMember.class, memberId);
	    } catch (BusinessException e) {
	    	log.error("获取协同消息提醒对应人员失败", e);
	    	return null;
	    }
		return member;
	}
	
}