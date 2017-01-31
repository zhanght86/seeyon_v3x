package com.seeyon.v3x.taskmanage.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskInfo.TaskDateEnum;
import com.seeyon.v3x.taskmanage.domain.TaskRole.RoleType;

/**
 * 任务管理消息发送工具类，负责如下应用场景的消息发送：
 * <ui>
 * <li>新建任务时，向负责人、参与人、查看人发送消息；</li>
 * <li>修改任务时，向创建人、负责人、参与人发送消息；</li>
 * <li>删除任务时，向负责人、参与人发送消息；</li>
 * <li>汇报任务时，向创建人、负责人发送消息；</li>
 * <li>回复任务时，向创建人、负责人和参与人发送消息；回复他人回复时，向引用的回复发起人发送消息；</li>
 * <li>任务开始之前或结束之前，如有提前提醒，向所有人发送消息。</li>
 * </ui>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-28
 */
public abstract class TaskMsgUtils {
	
	/**
	 * 定义需要发送消息的操作类型枚举
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-15
	 */
	public static enum Action {
		CreateTask,
		UpdateTask,
		DeleteTask,
		CreateFeedback,
		UpdateFeedback,
		DeleteFeedback,
		ReplyTask,
		RefrenceReply,
		RemindBeforeStart,
		RemindBeforeEnd
	}
	
	/**
	 * 定义任务操作过程中出现的异常信息枚举
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-16
	 */
	public static enum ExceptionMsg {
		/**
		 * 任务已被创建者删除
		 */
		TaskDeleted,
		/**
		 * 已无分解任务权限
		 */
		NoDecomposeAuth,
		/**
		 * 已无修改任务权限
		 */
		NoEditAuth,
		NoViewAuth,
		NoReplyAuth,
		NoFeedbackAuth,
		NoListMyTasksAuth,
		NoListManageTasksAuth,
		
		/**
		 * 保存任务回复过程中失败
		 */
		SaveReplyFail,
		/**
		 * 发送消息过程中失败
		 */
		SendMsgFail,
		/**
		 * 其他异常
		 */
		OtherException,
		
		/**
		 * 无异常出现，操作成功
		 */
		None;
		
	}
	
	public static RoleType[] getMsgReceiverRoleTypes(Action action) {
		switch(action) {
		case CreateTask : 
			return new RoleType[]{RoleType.Manager, RoleType.Participator};
		case UpdateTask :
		case ReplyTask :
			return RoleType.values();
		case DeleteTask :
			return new RoleType[]{RoleType.Manager, RoleType.Participator};
		case CreateFeedback :
		case UpdateFeedback :
			return new RoleType[]{RoleType.Creator, RoleType.Manager};
		case RemindBeforeStart :
		case RemindBeforeEnd :
			return RoleType.values();
		}
		throw new IllegalArgumentException("此操作类型[Action=" + action.name() + "]未明确消息发送角色类型，可能是发送消息给特定对象...");
	}
	
	public static Collection<MessageReceiver> getMsgReceivers(Action action, Long taskId, Collection<Long> msgReceiverIds) {
		switch(action) {
		case CreateTask : 
		case UpdateTask :
		case RemindBeforeStart :
		case RemindBeforeEnd :
			return MessageReceiver.get(taskId, msgReceiverIds, "message.link.taskmanage.view", taskId);
		case ReplyTask :
		case RefrenceReply :
			return MessageReceiver.get(taskId, msgReceiverIds, "message.link.taskmanage.viewfromreply", taskId);
		case CreateFeedback :
		case UpdateFeedback :
			return MessageReceiver.get(taskId, msgReceiverIds, "message.link.taskmanage.viewfeedback", taskId);
		case DeleteTask :
			return MessageReceiver.get(taskId, msgReceiverIds);
		}
		throw new IllegalArgumentException("非法操作类型[Action=" + action.name() + "]");
	}
	
	public static MessageContent getMsgContent(Action action, TaskInfo task) {
		User user = CurrentUser.get();
		String msgKey = ApplicationCategoryEnum.taskManage.name() + '.' + action.name();
		switch(action) {
		case CreateTask : 
		case UpdateTask :
		case CreateFeedback :
		case UpdateFeedback :
		case DeleteTask :
		case ReplyTask :
		case RefrenceReply :
			return MessageContent.get(msgKey, user.getName(), task.getSubject());
		case RemindBeforeStart :
			return MessageContent.get(msgKey, task.getSubject(), task.getFormatDate(TaskDateEnum.PlannedStart));
		case RemindBeforeEnd :
			return MessageContent.get(msgKey, task.getSubject(), task.getFormatDate(TaskDateEnum.PlannedEnd));
		}
		throw new IllegalArgumentException("非法操作类型[Action=" + action.name() + "]");
	}
	
	/**
	 * 对任务进行不同类型操作时，向相关的任务角色人员发送消息
	 * @param action	操作类型
	 * @param task		任务
	 */
	private static void sendMsg(Action action, TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		User user = CurrentUser.get();
		Long userId = null;
		if (action == Action.RemindBeforeStart || action == Action.RemindBeforeEnd) {
			userId = task.getCreateUser();
		} else if (user != null) {
			userId = user.getId();
		}
		Long taskId = task.getId();
		
		MessageContent msgContent = getMsgContent(action, task);
		
		RoleType[] roleTypes = getMsgReceiverRoleTypes(action);
		Set<Long> msgReceiverIds = TaskUtils.getRoleIds(task, roleTypes);
		if(msgReceiverIds != null && msgReceiverIds.contains(userId) && 
				action != Action.RemindBeforeStart && action != Action.RemindBeforeEnd) {
			msgReceiverIds.remove(userId);
		}
		
		Collection<MessageReceiver> msgReceivers = getMsgReceivers(action, taskId, msgReceiverIds);
		userMessageManager.sendSystemMessage(msgContent, ApplicationCategoryEnum.taskManage, userId, msgReceivers, taskId);
	}

	/**
	 * 新建任务时，向负责人、参与人、查看人发送消息
	 */
	public static void sendMsg4Create(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.CreateTask, task, userMessageManager);
	}
	
	/**
	 * 修改任务时，向负责人、参与人、创建人发送消息
	 */
	public static void sendMsg4Update(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.UpdateTask, task, userMessageManager);
	}
	
	/**
	 * 删除任务时，向负责人、参与人发送消息
	 */
	public static void sendMsg4Delete(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.DeleteTask, task, userMessageManager);
	}
	
	/**
	 * 汇报任务时，向创建人、负责人发送消息
	 */
	public static void sendMsg4Feedback(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.CreateFeedback, task, userMessageManager);
	}
	
	/**
	 * 修改任务汇报时，向创建人、负责人发送消息
	 */
	public static void sendMsg4EditFeedback(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.UpdateFeedback, task, userMessageManager);
	}
	
	/**
	 * 回复任务时，如果选中发送消息，消息接受对象为所有人
	 */
	public static void sendMsg4Reply(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.ReplyTask, task, userMessageManager);
	}
	
	/**
	 * 引用他人回复进行回复时，如果选中发送消息，消息接受对象为引用回复的发起人
	 * @param task		任务
	 * @param referenceReplyerId		回复时所引用的他人回复
	 */
	public static void sendMsg4ReferenceReply(TaskInfo task, Long referenceReplyerId, UserMessageManager userMessageManager) throws MessageException {
		User user = CurrentUser.get();
		Long userId = user.getId();
		if(userId.equals(referenceReplyerId))
			return;
		
		Long taskId = task.getId();
		
		String msgKey = ApplicationCategoryEnum.taskManage.name() + '.' + Action.RefrenceReply.name();
		MessageContent msgContent = MessageContent.get(msgKey, user.getName(), task.getSubject());
		Collection<MessageReceiver> msgReceivers = getMsgReceivers(Action.RefrenceReply, taskId, Arrays.asList(referenceReplyerId));
		
		userMessageManager.sendSystemMessage(msgContent, ApplicationCategoryEnum.taskManage, userId, msgReceivers, taskId);
	}
	
	/**
	 * 任务开始前提前提醒，消息接受对象为所有人
	 */
	public static void sendMsg4RemindBeforeStart(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.RemindBeforeStart, task, userMessageManager);
	}
	
	/**
	 * 任务结束前提前提醒，消息接受对象为所有人
	 */
	public static void sendMsg4RemindBeforeEnd(TaskInfo task, UserMessageManager userMessageManager) throws MessageException {
		sendMsg(Action.RemindBeforeEnd, task, userMessageManager);
	}
}
