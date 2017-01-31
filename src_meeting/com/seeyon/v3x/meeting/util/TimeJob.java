package com.seeyon.v3x.meeting.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.cap.calendar.domain.CalEventCAP;
import com.seeyon.cap.calendar.manager.CalEventManagerCAP;
import com.seeyon.cap.resource.manager.ResourceManagerCAP;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;
import com.seeyon.v3x.meeting.manager.MtResourcesManager;

/**
 * 会议任务调度：
 * 1.提前提醒或准时提醒，给与会人员发送系统消息；<br>
 * 2.会议开始时，将会议状态更新为"已开始"<br>
 * 3.会议结束时，将会议状态更新为"已结束"并清空占用资源时段<br>
 */
public class TimeJob implements Job {
	private static final Log log = LogFactory.getLog(TimeJob.class);
	
	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		Long meetingId = jobDataMap.getLongFromString("meetingId");
		String actionFlag = jobDataMap.getString("actionFlag");
		
		MtMeetingManager mtMeetingManager = (MtMeetingManager)ApplicationContextHolder.getBean("mtMeetingManager");
		MtMeeting meeting = mtMeetingManager.getByMtId(meetingId);
		 //radishlee add 2012-1-13 视频会议
		//String meetingType = meeting.getMeetingType()==null?"":meeting.getMeetingType();
		
		try {
			if(meeting != null && meeting.getState()>Constants.DATA_STATE_SAVE){
				//提醒与会人员
				if(Constants.TASK_TYPE.remindConferees.getActionFlag().equals(actionFlag) 
						&& meeting.getState()==Constants.DATA_STATE_SEND && meeting.isRemindFlag()){  
					
					MeetingMsgHelper.sendRemindMessage(mtMeetingManager, meeting);
					
					//如果是准时提醒，将会议状态更新为已开始
					if(meeting.getBeforeTime()!=null && meeting.getBeforeTime()==-1) 
						//radishlee add 2012-1-13 视频会议状态由视频会议服务器控制
						//if(!Constants.VIDEO_MEETING.equals(meetingType)){
						    mtMeetingManager.updateState(meetingId, Constants.DATA_STATE_START);
						//}
				} 
				//会议开始时将会议状态更新为已开始
				else if(Constants.TASK_TYPE.update2Start.getActionFlag().equals(actionFlag) 
							&& meeting.getState()==Constants.DATA_STATE_SEND) {  
					    //radishlee add 2012-1-13 视频会议状态由视频会议服务器控制
					   // if(Constants.ORID_MEETING.equals(meetingType)){
						    mtMeetingManager.updateState(meetingId, Constants.DATA_STATE_START);
					   // }else if(Constants.VIDEO_MEETING.equals(meetingType)){
					    //	if(meeting.getState()==Constants.DATA_STATE_WILL_START){
					   // 		 mtMeetingManager.updateState(meetingId, Constants.DATA_STATE_START);
					   /// 	}
					   // }	
				} 
				//会议结束时清空会议相关资源
				else if(Constants.TASK_TYPE.clearResources.getActionFlag().equals(actionFlag)) { 
					
					ResourceManagerCAP resourceManager = (ResourceManagerCAP)ApplicationContextHolder.getBean("resourceManagerCAP");
					resourceManager.delResourceIppByAppId(meeting.getId());
					
					MtResourcesManager mtResourcesManager = (MtResourcesManager)ApplicationContextHolder.getBean("resourcesManager"); 
					mtResourcesManager.deleteByMeetingId(meeting.getId());
					//将事项清空
					AffairManager affairManager = (AffairManager)ApplicationContextHolder.getBean("affairManager");
					affairManager.deleteByObject(ApplicationCategoryEnum.meeting, meetingId);
					//将生成的日程事件置为"已完成"状态
					CalEventManagerCAP calEventManager = (CalEventManagerCAP)ApplicationContextHolder.getBean("calEventManagerCAP");
					List<CalEventCAP> events = calEventManager.getAllCalEventByAppId(meetingId, ApplicationCategoryEnum.meeting.getKey());
					for(CalEventCAP event : events){
						event.setStates(4);
						event.setCompleteRate(100f);
						calEventManager.save(event, false);
					}
					//yangzd fix 会议被归档后，在文档中心点击后，在管理页面再次出现的bug
					if(meeting.getState()!=Constants.DATA_STATE_PIGEONHOLE){
						 //radishlee add 2012-1-13 视频会议状态由视频会议服务器控制
					   // if(!Constants.VIDEO_MEETING.equals(meetingType)){
						     mtMeetingManager.updateState(meetingId, Constants.DATA_STATE_FINISH);
					   // }
					}
				}
			}
		} catch(Exception e1){
			log.error("会议提前提醒或会议结束时清空资源出现异常：", e1);
		}
	}
}
