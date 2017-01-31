package com.seeyon.v3x.collaboration.callback;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.util.Strings;

public class TerminateColSupervise implements Job, QuartzJob {
	
	private final static Log log = LogFactory.getLog(TerminateColSupervise.class);

	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		
		long colSuperviseId = jobDataMap.getLongFromString("colSuperviseId");
		long senderId = jobDataMap.getLongFromString("senderId");
		String supervisorMemberId = jobDataMap.getString("supervisorMemberId");
		String subject = jobDataMap.getString("subject");
		
		this.execute(colSuperviseId, senderId, supervisorMemberId, subject);
	}

	public void execute(Map<String, String> p) {
		this.execute(Long.parseLong(p.get("colSuperviseId")), Long.parseLong(p.get("senderId")), p.get("supervisorMemberId"), p.get("subject"));
	}
	
	public void execute(long colSuperviseId, long senderId, String supervisorMemberId, String subject) {
		try {
			ColSuperviseManager colSuperviseManager = (ColSuperviseManager)ApplicationContextHolder.getBean("colSuperviseManager");
			ColSuperviseDetail detail = colSuperviseManager.get(colSuperviseId);
			if(detail==null || detail.getStatus()!=Constant.superviseState.supervising.ordinal())
				return;
			
			ApplicationCategoryEnum app = ApplicationCategoryEnum.collaboration;
	    	String key = "col.supervise.overdue";
	    	String link = "message.link.col.supervise";
			Integer edocApp = null;
			if(null!=detail && (detail.getEntityType() == com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal())){
				key = "edoc.supervise.overdue";
				link = "message.link.edoc.supervise.detail";
				app = ApplicationCategoryEnum.edoc;
				
	    		try{
	        		EdocManager edocManager = (EdocManager) ApplicationContextHolder.getBean("edocManager");
	        		EdocSummary eSummary = edocManager.getEdocSummaryById(detail.getEntityId(), false);
	        		if(null!=eSummary){
	        			if(eSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
	        				app = ApplicationCategoryEnum.edocSend;
	        			}else if(eSummary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
	        				app = ApplicationCategoryEnum.edocRec;
	        			}else{
	        				app = ApplicationCategoryEnum.edocSign;
	        			}
	        			edocApp = app.getKey();
	        		}
        		}
	    		catch(Exception e){
        			log.error("获得公文SUMMARY错误 " + e);
        		}				
			}
			
	    	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
	    	String[] spArray = supervisorMemberId.split(",");
	    	for(String s : spArray){
	    		if(Strings.isBlank(s)){
	    			continue;
	    		}
	    		MessageReceiver receiver = new MessageReceiver(colSuperviseId, Long.valueOf(s), link, detail.getEntityId());
	    		receivers.add(receiver);
	    	}
			
    		UserMessageManager userMessageManager = (UserMessageManager)ApplicationContextHolder.getBean("UserMessageManager");
    		userMessageManager.sendSystemMessage(new MessageContent(key, subject,edocApp), app, senderId, receivers);
		}
		catch(Exception e) {
			log.error("",e);
		}

	}

}
