package com.seeyon.v3x.calendar.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.calendar.constants.ShareType;
import com.seeyon.v3x.calendar.domain.AbstractCalEvent;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalReply;
import com.seeyon.v3x.calendar.manager.CalReplyManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;

public class CalendarNotifier {
	/**
	 * 创建事件
	 */
	public static int P_ON_INSERT=10;
	/**
	 * 安排事件
	 */
	public static int P_ON_PLAN=20;
	/**
	 * 撤销事件
	 */
	public static int P_ON_CANCEL_PLAN=30;
	/**
	 * 将事件委托给某人
	 */
	public static int P_ON_PROXY=40;
	/**
	 * 委托事件
	 */
	public static int P_ON_TRANS=50;
	/**
	 * 修改事件内容 
	 */
	public static int P_ON_CHANG=60;
	
	/**
	 * 回复事件
	 */
	public static int P_ON_REPLY=100;
	
	/**
	 * 删除事件
	 */
	public static int p_ON_DELETE = 70;
	
	/**
	 * 共享事件
	 */
	public static int p_ON_SHARE = 80;
	
	private static final Log log = LogFactory.getLog(CalendarNotifier.class);
	
	/**
	 * 创建、安排、委托事件后发送消息给相关人员进行提示
	 */
	public static void sendNotifierMessageInsert(int type, String oldReceivers, AbstractCalEvent event, OrgManager orgManager, 
			UserMessageManager userMessageManager, CalendarUtils calendarUtils) {
		sendNotifierMessageInsert(type, null, oldReceivers, event, orgManager, userMessageManager, calendarUtils);
	}
	
	public static String getRandomStr() {
		User user = CurrentUser.get();
		StringBuilder ret = new StringBuilder();
		if(user != null) {
			ret.append(user.getId());
		}
		ret.append(System.currentTimeMillis());
		return ret.toString();
	}
	
	/**
	 * 创建、安排、委托事件后发送消息给相关人员进行提示
	 */
	public static void sendNotifierMessageInsert(int type, Integer oldShareType, String oldReceivers, AbstractCalEvent event, 
			OrgManager orgManager, UserMessageManager userMessageManager, CalendarUtils calendarUtils) {
		User user = CurrentUser.get();
        Long userId = user.getId();
        if(orgManager == null)
        	orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
        
        if(userMessageManager == null)
        	userMessageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
        
        if(calendarUtils == null)
        	calendarUtils = (CalendarUtils) ApplicationContextHolder.getBean("calendarUtils");
        
        //给接收人发送消息
		List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
		
		List<V3xOrgMember> members=new ArrayList<V3xOrgMember>();
		try {
			if(type==p_ON_SHARE || type==P_ON_REPLY || type==P_ON_CHANG
					||type==P_ON_PLAN || type==P_ON_TRANS || type==p_ON_DELETE){
				if(event.getShareType() == 1){
					members.add(orgManager.getMemberById(userId));
				}
				else if(event.getShareType() == 2){
					members = calendarUtils.getMembersId(event.getTranMemberIds());
				}
				else if(event.getShareType() == 5){//部门
					members=calendarUtils.getMembersByTypeAndIds(event.getTranMemberIds());
				}
				else if(event.getShareType() == 6){//项目
					members=calendarUtils.getProjectMebIds(event.getTranMemberIds());
				}
					
				if(type==p_ON_SHARE){
					List<V3xOrgMember> oldMembers=new ArrayList<V3xOrgMember>();
					if(oldShareType != null){
						if((oldShareType == ShareType.publicity.key() || oldShareType == ShareType.department.key() || oldShareType == ShareType.project.key()) && StringUtils.isNotBlank(oldReceivers)){
							if(oldShareType == ShareType.publicity.key()){//他人
								oldMembers = calendarUtils.getMembersId(oldReceivers);
							}else if(oldShareType == ShareType.department.key()){//部门
								oldMembers=calendarUtils.getMembersByTypeAndIds(oldReceivers);
							}else if(oldShareType == ShareType.project.key()){//项目
								oldMembers=calendarUtils.getProjectMebIds(oldReceivers);
							}
						}
					}
					List<V3xOrgMember> msgReceiverIds = (List<V3xOrgMember>)FormBizConfigUtils.getCollectionActionResult(oldMembers, members, CommonTools.CollectionActionType.getAdded);
					userMessageManager.sendSystemMessage(
							new MessageContent("cal.addAssign",event.getSubject(),CurrentUser.get().getName()), 
							ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),getReciverList(msgReceiverIds,receivers,event)
							);
				}
				else if(type==P_ON_REPLY){
					//事件回复时，只给发起人和安排人发送消息
					//List<Long> recs = FormBizConfigUtils.parseTypeAndIdStr2Ids(event.getReceiveMemberId());
					//List<Long> sharers = FormBizConfigUtils.getEntityIds(members);
					//List<Long> msgReceiverIds = FormBizConfigUtils.getSumCollection(recs, sharers);
					List<Long> msgReceiverIds = FormBizConfigUtils.parseTypeAndIdStr2Ids(event.getReceiveMemberId());
					if(CollectionUtils.isNotEmpty(msgReceiverIds)) {
						if(!msgReceiverIds.contains(event.getCreateUserId()))
							msgReceiverIds.add(event.getCreateUserId());
					} else {
						msgReceiverIds = new ArrayList<Long>(1);
						msgReceiverIds.add(event.getCreateUserId());
					}

					msgReceiverIds.remove(CurrentUser.get().getId()); //去除自己回复后给自己提示消息
					CalReplyManager calReplyManager = (CalReplyManager) ApplicationContextHolder.getBean("calReplyManager");
					List<CalReply> list = calReplyManager.getReplyListByEventId(event.getId());
					String replyContent = "";
					if(list!=null && list.size()>0){
						CalReply c = list.get(0);
						replyContent = c.getReplyInfo();
					}
					userMessageManager.sendSystemMessage(
							new MessageContent("cal.reply.new",event.getSubject(),CurrentUser.get().getName(),event.getShareTarget(),replyContent), 
							ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),
							MessageReceiver.get(event.getId(), msgReceiverIds, "message.link.cal.reply", event.getId(), getRandomStr())
							);
				}
				else if(type == P_ON_PLAN || type == P_ON_TRANS) {
					if(userId.equals(event.getCreateUserId())) {
						
						List<Long> oldTargets = FormBizConfigUtils.parseTypeAndIdStr2Ids(oldReceivers);
						List<Long> newTargets = FormBizConfigUtils.parseTypeAndIdStr2Ids(event.getReceiveMemberId());
						
						List<Long> sendTargets = (List<Long>)FormBizConfigUtils.getCollectionActionResult(oldTargets, newTargets, CommonTools.CollectionActionType.getAdded);
						List<Long> sendCancelMsgTargets = (List<Long>)FormBizConfigUtils.getCollectionActionResult(oldTargets, newTargets, CommonTools.CollectionActionType.getReduced);
						List<Long> remainedMsgTargets = (List<Long>)FormBizConfigUtils.getCollectionActionResult(oldTargets, newTargets, CommonTools.CollectionActionType.intersection);
						
						//发送告知消息
						if(CollectionUtils.isNotEmpty(sendTargets)) {
							userMessageManager.sendSystemMessage(
									new MessageContent(type==P_ON_PLAN ? "cal.anPai" : "cal.colAssign", event.getSubject(), CurrentUser.get().getName()), 
									ApplicationCategoryEnum.calendar, CurrentUser.get().getId(), 
									MessageReceiver.get(event.getId(), sendTargets, "message.link.cal.view", event.getId(), getRandomStr())
									);
						}
						
						//发送修改信息
						if(CollectionUtils.isNotEmpty(remainedMsgTargets)) {
							userMessageManager.sendSystemMessage(
									new MessageContent("cal.edited", CurrentUser.get().getName(), event.getSubject()), 
									ApplicationCategoryEnum.calendar, CurrentUser.get().getId(), 
									MessageReceiver.get(event.getId(), remainedMsgTargets, "message.link.cal.view", event.getId(), getRandomStr())
									);
						}
						
						//发送取消消息
						if(CollectionUtils.isNotEmpty(sendCancelMsgTargets)) {
							userMessageManager.sendSystemMessage(
									new MessageContent("cal.cancel", event.getSubject(), CurrentUser.get().getName()), 
									ApplicationCategoryEnum.calendar, CurrentUser.get().getId(),
									MessageReceiver.get(event.getId(), sendCancelMsgTargets)
									);
						}
					} else {
						List<Long> sendTargets = FormBizConfigUtils.parseTypeAndIdStr2Ids(event.getReceiveMemberId());
						sendTargets.add(event.getCreateUserId());
						sendTargets.remove(userId);
						
						userMessageManager.sendSystemMessage(
								new MessageContent("cal.edited", CurrentUser.get().getName(), event.getSubject()), 
								ApplicationCategoryEnum.calendar, CurrentUser.get().getId(), 
								MessageReceiver.get(event.getId(), sendTargets, "message.link.cal.view", event.getId(), getRandomStr())
								);
					}
					
				}
				else if(type==P_ON_CHANG){
					userMessageManager.sendSystemMessage(
							new MessageContent("cal.modifyBody",event.getSubject(),CurrentUser.get().getName(),event.getShareTarget()), 
							ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),getReciverList(members,receivers,event)
							);
				}
				else if(type==p_ON_DELETE){
					userMessageManager.sendSystemMessage(
							new MessageContent("cal.delete",event.getSubject(),CurrentUser.get().getName(),event.getShareTarget()), 
							ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),getReciverList(event,type,receivers)
							);
				}
			}
		} catch (BusinessException e) {
			log.error("", e);
		}
		
	}
	
	/**
	 * 创建、安排、委托事件后发送消息给相关人员进行提示
	 * @deprecated 未使用依赖注入，且在安排或委托事件允许修改之后，消息发送需要增加参数
	 */
	public static void sendNotifierMessageInsert(int type, CalEvent event) {
		sendNotifierMessageInsert(type, null, event, null, null, null);
	}

	private static List<MessageReceiver> getReciverList(AbstractCalEvent event,int type,List<MessageReceiver> receivers) throws BusinessException{
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
		String ids = event.getReceiveMemberId();
		if(ids!=null){
			String[] Ids = ids.split(",");
			V3xOrgMember eventCreater = orgManager.getMemberById(event.getCreateUserId());
			for(String str : Ids){
				String[] key = str.split("[|]");
				if(key.length>=2){
					try {
						V3xOrgMember member = orgManager.getMemberById(Long.parseLong(key[1]));
						if(type==p_ON_DELETE&&member.getId().equals(Long.valueOf(CurrentUser.get().getId()))&&!receivers.contains(eventCreater)){
							members.add(eventCreater);
						}else{
							members.add(member);
						}
					} catch (NumberFormatException e) {
						log.error("", e);
					} catch (BusinessException e) {
						log.error("", e);
					}
				}
			}
		}
		
		return getReciverList(members,receivers,event);
	}
	
	private static List<MessageReceiver> getReciverList(List<V3xOrgMember> members,List<MessageReceiver> receivers,AbstractCalEvent event){
		for(V3xOrgMember member:members){
			if(member.getId().longValue()==CurrentUser.get().getId()) 
				continue;
			receivers.add(new MessageReceiver(event.getId(), member.getId(),"message.link.cal.view",event.getId(), getRandomStr()));
		}
		return receivers;
	}
	
	/**
	 * 撤销事件后给相关人员发送消息进行提示
	 * @param type
	 * @param event
	 * @param comment
	 */
	public static void sendNotifierMessageCancel(int type,CalEvent event,String comment){
		User user = CurrentUser.get();
        Long userId = user.getId();
        OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
        UserMessageManager userMessageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
        CalendarUtils calendarUtils = (CalendarUtils) ApplicationContextHolder.getBean("calendarUtils");
        
		//给接收人发送消息
		Collection<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
		
		List<V3xOrgMember> members=new ArrayList<V3xOrgMember>();
		try {
				if(event.getShareType().intValue()==1){
					members.add(orgManager.getMemberById(userId));
				}else if(event.getShareType().intValue()==3){
					members = calendarUtils.getSuperior(user);
				}else if(event.getShareType().intValue()==4){
					members=calendarUtils.getJunior(user);
				}else if(event.getShareType().intValue()==5){//部门
					members=calendarUtils.getMembersByTypeAndIds(event.getTranMemberIds());
				}
				else if(event.getShareType().intValue()==6){//项目
					members=calendarUtils.getProjectMebIds(event.getTranMemberIds());
				}
				//如果安排人在共享范围中，这里就不在发消息了。reason：已经给安排人发过消息了。
				V3xOrgMember createUser = orgManager.getMemberById(event.getCreateUserId());
				if(StringUtils.isNotBlank(event.getReceiveMemberId())){
					List<V3xOrgMember> receiveMembers = calendarUtils.getMembersByTypeAndIds(event.getReceiveMemberId());
					List<V3xOrgMember> sameMembers = FormBizConfigUtils.getIntersection(members, receiveMembers);
					members.removeAll(sameMembers);
					members.remove(createUser);
				}
				for(V3xOrgMember member:members){
					if(member.getId().longValue()==user.getId()) continue;
//					receivers.add(new MessageReceiver(event.getId(), member.getId(),"message.link.cal.view",event.getId().toString()));  撤销消息链接
					receivers.add(new MessageReceiver(event.getId(), member.getId()));
				}
//				userMessageManager.sendSystemMessage(
//						new MessageContent("cal.cancel",event.getSubject(),CurrentUser.get().getName(),comment),
//						ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),receivers
//						);
			 if(type==P_ON_PROXY){
				//个人事件委托后给委托人发送消息
				members=calendarUtils.getMembersByTypeAndIds(event.getReceiveMemberId());
				for(V3xOrgMember member:members){
					if(member.getId().longValue()==user.getId()) continue;
					receivers.add(new MessageReceiver(event.getId(), member.getId(),"message.link.cal.view",event.getId(), getRandomStr()));
				}
				userMessageManager.sendSystemMessage(
						new MessageContent("cal.colAssign",event.getSubject(),CurrentUser.get().getName(),event.getReceiveMemberName(),comment),
						ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),receivers
						);
			}else if(type==P_ON_CANCEL_PLAN){
				//个人事件撤销后给创建人发送消息
				receivers.add(new MessageReceiver(event.getId(), event.getCreateUserId(),"message.link.cal.view",event.getId(), getRandomStr()));
				
				userMessageManager.sendSystemMessage(
						new MessageContent("cal.cancel", event.getSubject(), CurrentUser.get().getName()),
						ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),receivers
						);
			}else if(type == p_ON_DELETE){
				if(!event.getCreateUserId().equals(Long.valueOf(CurrentUser.get().getId())) && !members.contains(createUser) && StringUtils.isBlank(event.getReceiveMemberId())){
					receivers.add(new MessageReceiver(event.getId(), event.getCreateUserId(),"message.link.cal.view",event.getId(), getRandomStr()));
				}
				
				userMessageManager.sendSystemMessage(
						new MessageContent("cal.delete",event.getSubject(),CurrentUser.get().getName(),event.getReceiveMemberName(),comment),
						ApplicationCategoryEnum.calendar,CurrentUser.get().getId(),receivers
						);
			}
		} catch (BusinessException e) {
			log.error("", e);
		}
		
	}
}
