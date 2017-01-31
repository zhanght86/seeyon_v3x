package com.seeyon.v3x.edoc.supervise.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSuperviseManager;

public class TerminateEdocSupervise implements Job{
	private final static Log log = LogFactory
			.getLog(TerminateEdocSupervise.class);
	public void execute(JobExecutionContext datamap) throws JobExecutionException{
		
		try{
	    	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
	    	String key = "edoc.supervise.overdue";
			JobDetail jobDetail = datamap.getJobDetail();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			
			long edocSuperviseId = jobDataMap.getLongFromString("edocSuperviseId");
			String supervisorMemberId = jobDataMap.getString("supervisorMemberId");
			String subject = jobDataMap.getString("subject");
			
			EdocSuperviseManager edocSuperviseManager = (EdocSuperviseManager)ApplicationContextHolder.getBean("edocSuperviseManager");
			ColSuperviseDetail detail = edocSuperviseManager.getSuperviseById(edocSuperviseId); 
			
			if(null == detail)return;
			EdocSummaryDao edocSummaryDao = (EdocSummaryDao)ApplicationContextHolder.getBean("edocSummaryDao");
			EdocSummary edocSummary = edocSummaryDao.get(detail.getEntityId());
			ApplicationCategoryEnum appEnum = null;
			if(edocSummary != null){
				if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
					appEnum = ApplicationCategoryEnum.edocSend;
				}else if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
					appEnum = ApplicationCategoryEnum.edocRec;
				}else if(edocSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN){
					appEnum = ApplicationCategoryEnum.edocSign;
				}
			}else{
				appEnum = ApplicationCategoryEnum.edoc;
			}
			
	        String[] spArray = supervisorMemberId.split(",");
	        //过滤不重复
	        Set<Long> superReceivers = new HashSet<Long>();
	        for(String s : spArray){
	        	superReceivers.add(Long.parseLong(s));
	        }
	    	for(Long receiverId : superReceivers){
	    		MessageReceiver receiver = new MessageReceiver(detail.getId(),receiverId,"message.link.edoc.supervise.detail",detail.getEntityId());
	    		receivers.add(receiver);
	    	}
			
	    	try{
	    		UserMessageManager userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
	    		userMessageManager.sendSystemMessage(new MessageContent(key, subject,appEnum.getKey()), appEnum, detail.getSenderId(), receivers);
	    	}catch(MessageException e){
	    		log.error(e.getMessage(), e);
	    	}
		}catch(Exception e1){
			log.error(e1.getMessage(), e1);
		}
    }
}
