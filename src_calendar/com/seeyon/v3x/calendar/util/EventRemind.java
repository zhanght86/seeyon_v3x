package com.seeyon.v3x.calendar.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class EventRemind implements Job {
	
	private static final Log logger = LogFactory.getLog(EventRemind.class);

	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String keyDay = "cal.remind";
		Long eventId = jobDataMap.getLongFromString("eventId");
		
		try {
			CalEventManager calEventManager = (CalEventManager) ApplicationContextHolder.getBean("calEventManager");
			if(calEventManager == null)
				return;
			
			CalEvent event = calEventManager.getEventByIdNoInit(eventId);
			
			if(event != null && event.isAlarmFlag()) {
				List<Long> msgReceivers = this.getMsgReceivers(event);
				if(CollectionUtils.isNotEmpty(msgReceivers)) {
					Collection<MessageReceiver> receivers = MessageReceiver.get(eventId, msgReceivers, "message.link.cal.view", eventId, CalendarNotifier.getRandomStr());
		
					UserMessageManager messageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
					if(messageManager == null)
						return;
					
					if(Datetimes.formatDate(event.getBeginDate()).equals(Datetimes.formatDate(event.getEndDate()))) {
						keyDay = "cal.remindday";
					}
					
					messageManager.sendSystemMessage(MessageContent.get(keyDay, event.getBeginDate(), event.getEndDate(), event.getSubject()),
							ApplicationCategoryEnum.calendar, event.getCreateUserId(), receivers);
				}
			}

		} catch (Exception e1) {
			logger.error("事件[id=" + eventId + "]任务调度发送提醒消息时出现异常", e1);
		}
	}

	private List<Long> getMsgReceivers(CalEvent event) {
		List<Long> result = new ArrayList<Long>();
		if (Strings.isNotBlank(event.getReceiveMemberId())) {
			String[] typeAndIds = event.getReceiveMemberId().split(",");
			for(String typeAndId : typeAndIds) {
				result.add(Long.parseLong(typeAndId.substring(typeAndId.indexOf("|") + 1)));
			}
		} else {
			result.add(event.getCreateUserId());
		}
		return result;
	}
}
