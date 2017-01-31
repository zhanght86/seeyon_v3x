/**
 * 
 */
package com.seeyon.v3x.inquiry.util;

import java.util.ArrayList;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.inquiry.dao.InquiryBasicDao;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.manager.InquiryManager;

/**
 * @author tianlin
 * 调查提醒
 */
public class InquiryRemind implements Job{

	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
			Long id = jobDataMap.getLongFromString("basicID");
			String remindIds = jobDataMap.getString("remindIds");
			String sender = jobDataMap.getString("sender");
			String[] idsArr = null;
			if(remindIds!=null&&!remindIds.equals("")){
				idsArr = remindIds.split(",");
			}
			try{
				InquiryManager inquiryManager = (InquiryManager)ApplicationContextHolder.getBean("inquiryManager");
				InquiryBasicDao inquiryBasicDao = (InquiryBasicDao)ApplicationContextHolder.getBean("inquiryBasicDao");
				InquirySurveybasic basic = inquiryManager.getBasicByID(id);
				if(basic == null) return;
//				执行消息提醒时将调查状态置为已发送
				basic.setCensor(InquirySurveybasic.CENSOR_PASS);
				inquiryBasicDao.update(basic);
				List<Long> receiverIds = new ArrayList<Long>();
				for(String remindId : idsArr){
					receiverIds.add(new Long(remindId));
				}
				Long memberId = basic.getCreaterId();
				//发送提醒消息
				try{
					UserMessageManager messageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
					messageManager.sendSystemMessage(MessageContent.get("inquiry.send", basic.getSurveyName(),sender),
													ApplicationCategoryEnum.inquiry, 
													memberId, 
													MessageReceiver.get(basic.getId(), receiverIds, "message.link.inq.alreadyauditing",String.valueOf(basic.getId())));
					

				}catch(MessageException e){
					e.printStackTrace();
				}
			
			}catch(Exception e1){
				e1.printStackTrace();
			}
		
	}

}
