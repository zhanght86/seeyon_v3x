package com.seeyon.v3x.meeting.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;
import com.seeyon.v3x.meeting.manager.MtReplyManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;

/**
 * 会议消息发送工具类，用于：<br>
 * 1.在发送会议时，为新增、保留、取消的与会对象发送不同系统消息；<br>
 * 2.在会议提醒时间到达时，为有效与会对象发送提醒系统消息。<br>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-4-29
 */
public class MeetingMsgHelper {
	private static final Log logger = LogFactory.getLog(MeetingMsgHelper.class);

	/**
	 * 在发送会议时，为新增、保留、取消的与会对象发送不同系统消息：<br>
	 * 1.对新增人员，发送会议通知消息<br>
	 * 2.对保留人员，发送会议变更消息<br>
	 * 3.对取消人员，发送会议取消信息(较比直接说相关人员被取消与会权妥当一些...)<br>
	 * @param isTargetNotSent	会议发送前的状态：新建或暂存待发
	 * @param bean				修改后的会议
	 * @param oldBean			修改前的会议
	 */
	@SuppressWarnings("unchecked")
	public static void sendMessage(boolean isTargetNotSent, MtMeeting bean, MtMeeting oldBean, 
			MtMeetingManager mtMeetingManager, UserMessageManager userMessageManager, 
			MtReplyManager replyManager) throws BusinessException {
		List<MeetingMsgTarget> targets = new ArrayList<MeetingMsgTarget>();
		
		//修改前的与会人员及其对应的代理人员
		List<Long> oldMsgReceivers = null;
		List<Long> oldMsgReceivers_agent = null;
		
		//修改后的与会人员及其对应的代理人员
		Map<String, List> map = mtMeetingManager.getMsgReceiversWithAgentMap(bean);
    	List<Long> msgReceivers = (List<Long>)map.get(Constants.ReceiverType.Owner.name());		
    	List<Long> msgReceivers_agent = (List<Long>)map.get(Constants.ReceiverType.Agent.name());
    	List<MeetingAgent> msgReceivers_agentModels = (List<MeetingAgent>)map.get(Constants.ReceiverType.AgentModel.name());
    	
    	List<Long> added = null;			//增加的与会人员
    	List<Long> added_agent = null;		//增加的与会人员代理人员
    	List<Long> reduced = null;			//减少的与会人员
    	List<Long> reduced_agent = null;	//减少的与会人员代理人员
    	List<Long> remained = null;			//保留的与会人员
    	List<Long> remained_agent = null;	//保留的与会人员代理人员
    	
    	//新发起会议或发起暂存的会议
    	if(isTargetNotSent) {  
    		added = msgReceivers;
    		added_agent = msgReceivers_agent;
    		
    		//radishlee add 2012-3-6 添加视频会议会前提醒
			MeetingMsgTarget.MessageType messageType = null;
			if(bean.getMeetingType().equals(Constants.ORID_MEETING)){
				messageType = MeetingMsgTarget.MessageType.send;
			}else{
				messageType = MeetingMsgTarget.MessageType.sendvideo;
			}
		
    		targets.add(new MeetingMsgTarget(added, false, messageType, null));
    		targets.add(new MeetingMsgTarget(added_agent, true, messageType, msgReceivers_agentModels));
    	} 
    	//修改已发起的会议
    	else {  
    		Map<String, List> oldMap = mtMeetingManager.getMsgReceiversWithAgentMap(oldBean);
    		oldMsgReceivers = oldMap.get(Constants.ReceiverType.Owner.name());	       	
        	oldMsgReceivers_agent = oldMap.get(Constants.ReceiverType.Agent.name());
        	
        	added = CommonTools.getAddedCollection(oldMsgReceivers, msgReceivers);
        	added_agent = CommonTools.getAddedCollection(oldMsgReceivers_agent, msgReceivers_agent);
        	
        	reduced = CommonTools.getReducedCollection(oldMsgReceivers, msgReceivers);
        	reduced_agent = CommonTools.getReducedCollection(oldMsgReceivers_agent, msgReceivers_agent);
        	
        	remained = CommonTools.getIntersection(oldMsgReceivers, msgReceivers);
        	remained_agent = CommonTools.getIntersection(oldMsgReceivers_agent, msgReceivers_agent);
    		
        	MeetingMsgTarget.MessageType messageTypeSend = null;
        	MeetingMsgTarget.MessageType messageTypeCancel = null;
        	MeetingMsgTarget.MessageType messageTypeEdit = null;
			if(bean.getMeetingType().equals(Constants.ORID_MEETING)){
				messageTypeSend = MeetingMsgTarget.MessageType.send;
				messageTypeCancel = MeetingMsgTarget.MessageType.cancel;
				messageTypeEdit = MeetingMsgTarget.MessageType.edit;
			}else{
				messageTypeSend = MeetingMsgTarget.MessageType.sendvideo;
				messageTypeCancel = MeetingMsgTarget.MessageType.cancelvideo;
				messageTypeEdit = MeetingMsgTarget.MessageType.editvideo;
			}
			
        	if(CollectionUtils.isNotEmpty(added)){
        		//radishlee add 2012-2-29 修改系统消息
        		targets.add(new MeetingMsgTarget(added, false, messageTypeSend, null));
        	}
        	
        	if(CollectionUtils.isNotEmpty(added_agent)){
        		//radishlee add 2012-2-29 修改系统消息
        		targets.add(new MeetingMsgTarget(added_agent, true, messageTypeSend, msgReceivers_agentModels));
        	}
        	
        	if(CollectionUtils.isNotEmpty(reduced)){
        		//radishlee add 2012-2-29 修改系统消息
        		targets.add(new MeetingMsgTarget(reduced, false, messageTypeCancel, null));
        	}
        	
        	if(CollectionUtils.isNotEmpty(reduced_agent)){
        		//radishlee add 2012-2-29 修改系统消息
        		targets.add(new MeetingMsgTarget(reduced_agent, true, messageTypeCancel, msgReceivers_agentModels));
        	}
        	
        	if(CollectionUtils.isNotEmpty(remained)){
        		//radishlee add 2012-2-29 修改系统消息
        		targets.add(new MeetingMsgTarget(remained, false, messageTypeEdit, null));
        	}
        	
        	if(CollectionUtils.isNotEmpty(remained_agent)){
        		//radishlee add 2012-2-29 修改系统消息
        		targets.add(new MeetingMsgTarget(remained_agent, true, messageTypeEdit, msgReceivers_agentModels));
        	}
    	}
    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("[与会人员变化情况]：" + "\n" +
    					 "旧：[" + showMemberName(oldMsgReceivers) + "]\n" +
    					 "新：[" + showMemberName(msgReceivers)  + "]\n" +
    					 "增：[" + showMemberName(added) + "]\n" +
    					 "留：[" + showMemberName(remained)  + "]\n" + 
    					 "减：[" + showMemberName(reduced)+ "]\n");
    		
    		logger.debug("[与会代理人员变化情况]：" + "\n" +
    					 "旧：[" + showMemberName(oldMsgReceivers_agent) + "]\n" +
    					 "新：[" + showMemberName(msgReceivers_agent) + "]\n" +
    					 "增：[" + showMemberName(added_agent) + "]\n" +
    					 "留：[" + showMemberName(remained_agent)  + "]\n" +
    					 "减：[" + showMemberName(reduced_agent) + "]\n");
    	}
    
    	//只删除被取消与会对象的回执记录
    	if(CollectionUtils.isNotEmpty(reduced))
    		replyManager.deleteByMeetingId(bean.getId(), reduced);
    	
    	for(MeetingMsgTarget target : targets) {
    		userMessageManager.sendSystemMessage(
    				MeetingMsgHelper.getMessageContent(target.getMessageType(), target.isAgent(), bean),  
    				ApplicationCategoryEnum.meeting, bean.getCreateUser(), 
    				MeetingMsgHelper.getMessageReceiver(bean.getId(), target));
    	}
	}
	
	/**
	 * 发送会前提醒消息
	 * @param mtMeetingManager
	 * @param meeting
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public static void sendRemindMessage(MtMeetingManager mtMeetingManager, MtMeeting meeting) throws BusinessException {
		UserMessageManager messageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
		
		Map<String, List> map = mtMeetingManager.getRemindMsgReceivers(meeting);
		List<Long> self = (List<Long>)map.get(Constants.ReceiverType.Owner.name());
		List<Long> agent = (List<Long>)map.get(Constants.ReceiverType.Agent.name());
		List<MeetingAgent> agentModels = (List<MeetingAgent>)map.get(Constants.ReceiverType.AgentModel.name());
		
		//radishlee add 2012-3-6 添加视频会议会前提醒
		MeetingMsgTarget.MessageType messageType = null;
		if(meeting.getMeetingType().equals(Constants.ORID_MEETING)){
			messageType = MeetingMsgTarget.MessageType.remind;
		}
		else{
			messageType = MeetingMsgTarget.MessageType.remindvideo;
		}
			
		if(CollectionUtils.isNotEmpty(self)) {
			messageManager.sendSystemMessage(getMessageContent(messageType, false, meeting), 
											 ApplicationCategoryEnum.meeting, meeting.getCreateUser(), 
											 getMessageReceiver(meeting.getId(), new MeetingMsgTarget(self, false, messageType, null)));
			if(logger.isDebugEnabled()) {
				logger.debug("提醒的与会人员：[" + showMemberName(self) + "]");
			}
		}
		
		if(CollectionUtils.isNotEmpty(agent)) {
			messageManager.sendSystemMessage(getMessageContent(messageType, true, meeting), 
					 						ApplicationCategoryEnum.meeting, meeting.getCreateUser(), 
					 						getMessageReceiver(meeting.getId(), new MeetingMsgTarget(agent, true, messageType, agentModels)));
			
			if(logger.isDebugEnabled()) {
				logger.debug("提醒的与会代理人员：[" + showMemberName(agent) + "]");
			}
		}
	}
	
	/**
	 * 辅助调试，用于显示人员名称<br>
	 * 不宜直接调用{@link#Functions.showOrgEntities}，由于其中使用了CurrentUser，在Quartz线程中将无法获取导致出现异常
	 * @param memberIds
	 */
	public static String showMemberName(Collection<Long> memberIds) throws BusinessException {
		if(CollectionUtils.isNotEmpty(memberIds)) {
			OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			List<String> names = new ArrayList<String>();
			for(Long memberId : memberIds) {
				V3xOrgMember member = orgManager.getMemberById(memberId);
				if(member!=null && member.isValid())
					names.add(member.getName());
			}
			String separator = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.separator.label");
			return StringUtils.join(names, separator);
		}
		return null;
	}
	
	/**
	 * 获取消息内容
	 * @param type		消息类型
	 * @param isAgent	是否为代理人
	 * @param bean		会议
	 */
	public static MessageContent getMessageContent(MeetingMsgTarget.MessageType type, boolean isAgent, MtMeeting bean) {
		MessageContent result = null;
	    switch(type) {
			case send :
			case sendvideo :
			case editvideo :
			case edit :
				result = MessageContent.get(type.getMsgKey(), bean.getTitle(), CurrentUser.get().getName(), bean.getBeginDate())
				.setBody(bean.getContent(), bean.getDataFormat(), bean.getCreateDate());
				break;
			case cancelvideo :
			case cancel :
				result = MessageContent.get(type.getMsgKey(), bean.getTitle(), CurrentUser.get().getName());
				break;
			case remindvideo :
			case remind :
				result = MessageContent.get(type.getMsgKey(), bean.getTitle(), bean.getBeginDate());
				break;
		}	
		if(isAgent)
			result.add("col.agent");
		return result;
	}
	
	/**
	 * 获取消息接收对象
	 * @param type			消息类型
	 * @param meetingId		会议ID
	 * @param msgReceivers	消息接收人员ID集合
	 * @param isAgent		是否为代理人情况
	 */
	public static Collection<MessageReceiver> getMessageReceiver(Long meetingId, MeetingMsgTarget target) {
		Collection<MessageReceiver> result = null;
		List<Long> receivers = target.getMsgReceivers();
		if(CollectionUtils.isNotEmpty(receivers)) {
			switch(target.getMessageType()) {
			case send :
			case edit :
			case sendvideo :
			case editvideo :
			case remindvideo :
			case remind :
				if(target.isAgent()) {
					result = new ArrayList<MessageReceiver>();
					for(Long memberId : receivers) {
						List<Long> agentToIds = getAgentToIds(memberId, target.getMeetingAgents());
						if(CollectionUtils.isNotEmpty(agentToIds)) {
							for(Long agentTo : agentToIds) {
								result.add(MessageReceiver.get(meetingId, memberId, "message.link.mt.send", meetingId, 1, agentTo));
							}
						}
					}
				} else {
					result = MessageReceiver.get(meetingId, receivers, "message.link.mt.send", meetingId, 0, -1);
				}
				break;
			case cancelvideo :	
			case cancel :
				result = MessageReceiver.get(meetingId, receivers);
				break;
			}
		}
		return result;
	}
	
	/** 获取代理人所代理的对象集合 */
	private static List<Long> getAgentToIds(Long memberId, List<MeetingAgent> meetingAgents) {
		List<Long> result = null;
		if(CollectionUtils.isNotEmpty(meetingAgents)) {
			result = new ArrayList<Long>();
			
			for(MeetingAgent agent : meetingAgents) {
				if(memberId.equals(agent.getAgentId())) {
					result.add(agent.getAgentToId());
				}
			}
		}
		return result;
	}
	
}
