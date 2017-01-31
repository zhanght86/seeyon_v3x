package com.seeyon.v3x.workflow.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.affair.webmodel.AffairData;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.callback.CallbackHandler;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.task.ExecutableTask;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

public class ListMapTask implements ExecutableTask{
	private final static Log log = LogFactory.getLog(ListMapTask.class);
	
	static AffairManager affManager = null;
	static UserMessageManager userMessageManager = null;
	static OrgManager orgManager = null;
	static WorkTimeManager workTimeManager = null;
	
	public boolean run(Object parameter) {
		return run1(parameter);
	}
	
	public static boolean run1(Object parameter){
		if( affManager == null)
		{
			affManager = (AffairManager) ApplicationContextHolder.getBean("affairManager");
			userMessageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
			orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
			workTimeManager = (WorkTimeManager) ApplicationContextHolder.getBean("workTimeManager");
			Assert.notNull(affManager, "如果脱离Web环境调试，需要从Spring的上下文中获得Bean。如下所示");
		}
		
		if(parameter instanceof List){
			List<AffairData> l = (List<AffairData>)parameter;
			
			for (AffairData data : l) {
				addListMap(data);
			}
		}
		else{
			addListMap((AffairData)parameter);
		}
		
		return true;
	}
	
	private static void addListMap(AffairData affairData) {
		if (affairData == null)
			return;
		
		try {
			Long senderId = affairData.getSender();
			List<Affair> affairList = affairData.getAffairList();
			Boolean isSendMessage = affairData.getIsSendMessage();

			if (affairList == null || affairList.isEmpty())
				return;

			String subject = affairList.get(0).getSubject();
			String forwardMemberId = affairList.get(0).getForwardMember();
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
			
			Integer importantLevel = affairList.get(0).getImportantLevel();
			String bodyContent = affairData.getBodyContent();
			String bodyType = affairData.getBodyType();
			Date bodyCreateDate = affairData.getBodyCreateDate();
			
			List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
			List<MessageReceiver> receivers1 = new ArrayList<MessageReceiver>();
			Affair aff = affairList.get(0);
			int app = aff.getApp();
			Affair senderAffair = affManager.getCollaborationSenderAffair(aff.getObjectId());
			Long[] userInfoData  = new Long[2];
			//发文的时候直接传参数，其他情况下取senderAffair，都是为了取到发文的时候是否是代理人登记的。
			if(senderAffair!=null){
				userInfoData[0] = senderAffair.getMemberId();
				userInfoData[1] = senderAffair.getTransactorId();
			}
			else{
				userInfoData = DateSharedWithWorkflowEngineThreadLocal.getCurrentUserData();
			}
			affManager.createAffairs(affairList);
			for (Affair affair : affairList) {
				// 获取消息提醒接收人
				if (isSendMessage) {
					getReceiver(affair, app, receivers, receivers1);
				}
				// 提前提醒，超期提醒
				affairExcuteRemind(affair, affairData.getSummaryAccountId());
			}

			// 生成事项消息提醒
			if (isSendMessage) {
				V3xOrgMember sender = null;
				try {
					sender = orgManager.getMemberById(senderId);
				}
				catch (Exception e1) {
					log.error("", e1);
					return;
				}
				
				//{1}发起协同:《{0}{2,choice,0|#1# (由{3}原发)}》
				Object[] subjects = new Object[]{subject, sender.getName(), forwardMemberFlag, forwardMember};
				sendMessage(app, receivers, receivers1, sender, subjects, importantLevel, bodyContent, bodyType, bodyCreateDate,userInfoData);
			}
			
			// 在此调用CallBack
			if (affairList == null || affairList.size() == 0)
				return;
			
			
			if(DateSharedWithWorkflowEngineThreadLocal.isNeedIndex()) {
				Affair affair0 = affairList.get(0);
				if (affair0.getApp() == ApplicationCategoryEnum.collaboration.key()) {
					CallbackHandler callback = CallbackHandler.getCallbackHandler("ColIndex");
					callback.invoke(affair0.getObjectId().toString());
				}
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
	}
	
	private static void getReceiver(Affair affair, int app, List<MessageReceiver> receivers, List<MessageReceiver> receivers1){
		Long theMemberId = affair.getMemberId();
		if(app == ApplicationCategoryEnum.collaboration.key()){
			if(affair.getIsSendMessage()){
				Long agentMemberId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.collaboration.key(), theMemberId,affair.getTempleteId());
				if(agentMemberId != null){
					receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.col.pending",affair.getId().toString()));
					receivers1.add(new MessageReceiver(affair.getId(), agentMemberId,"message.link.col.pending",affair.getId().toString()));
				}else{
					receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.col.pending",affair.getId().toString()));
				}
			}
		}else if(app == ApplicationCategoryEnum.edocSend.key()
				   || app == ApplicationCategoryEnum.edocRec.key()
				   || app == ApplicationCategoryEnum.edocSign.key()){
			if(affair.getIsSendMessage()){
				Long agentMemberId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(), theMemberId);
				if(agentMemberId != null){
					receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.edoc.pending",affair.getId().toString()));
					receivers1.add(new MessageReceiver(affair.getId(), agentMemberId,"message.link.edoc.pending",affair.getId().toString()));
				}else{
					receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.edoc.pending",affair.getId().toString()));
				}
			}
		}
	}
	
	private static void sendMessage(int app, List<MessageReceiver> receivers, List<MessageReceiver> receivers1, V3xOrgMember sender, 
			Object[] subjects, Integer importantLevel,String bodyContent, String bodyType, Date bodyCreateDate,Long[] userInfoData){
		
		if(app == ApplicationCategoryEnum.collaboration.key()){
			try {
				userMessageManager.sendSystemMessage(MessageContent.get("col.send", subjects).setBody(bodyContent, bodyType, bodyCreateDate).setImportantLevel(importantLevel),
						ApplicationCategoryEnum.collaboration, sender.getId(), receivers, importantLevel);
			} catch (MessageException e) {
				log.error("发起协同消息提醒失败!", e);
			}
			if(receivers1 != null && receivers1.size() != 0){
				try {
	     			userMessageManager.sendSystemMessage(MessageContent.get("col.send", subjects).setBody(bodyContent, bodyType, bodyCreateDate).add("col.agent").setImportantLevel(importantLevel),
	     					ApplicationCategoryEnum.collaboration, sender.getId(), receivers1, importantLevel);
				} catch (MessageException e) {
					log.error("发起协同消息提醒失败!", e);
				}
			}
		}else if(app == ApplicationCategoryEnum.edocSend.key()
				   || app == ApplicationCategoryEnum.edocRec.key()
				   || app == ApplicationCategoryEnum.edocSign.key()){
			
			ApplicationCategoryEnum appEnum=ApplicationCategoryEnum.valueOf(app);
			if(userInfoData.length==2 && userInfoData[1]!=null){//登记的时候是代理人进行登记的。
				try {
					String agentToName ="";
					Long agentToId = userInfoData[1];
					try{
						agentToName = orgManager.getMemberById(agentToId).getName();
					}catch(Exception e){
						log.error(e);
					}
					userMessageManager.sendSystemMessage(MessageContent.get("edoc.send", subjects[0], agentToName,app).setBody(bodyContent, bodyType, bodyCreateDate).setImportantLevel(importantLevel).add("edoc.agent.deal",sender.getName()),
							appEnum, agentToId, receivers, importantLevel);
					if(receivers1 != null && receivers1.size() != 0){
					 		userMessageManager.sendSystemMessage(MessageContent.get("edoc.send", subjects[0], agentToName,app).setBody(bodyContent, bodyType, bodyCreateDate).add("edoc.agent.deal",sender.getName()).add("col.agent").setImportantLevel(importantLevel),
					 				appEnum, agentToId, receivers1, importantLevel);
					}	
				} catch (MessageException e) {
					log.error("发起公文消息提醒失败!", e);
				}
			}else{
				try {
					userMessageManager.sendSystemMessage(MessageContent.get("edoc.send", subjects[0], sender.getName(),app).setBody(bodyContent, bodyType, bodyCreateDate).setImportantLevel(importantLevel),
							appEnum, sender.getId(), receivers, importantLevel);
				} catch (MessageException e) {
					log.error("发起公文消息提醒失败!", e);
				}
				if(receivers1 != null && receivers1.size() != 0){
					try {
				 		userMessageManager.sendSystemMessage(MessageContent.get("edoc.send", subjects[0], sender.getName(),app).setBody(bodyContent, bodyType, bodyCreateDate).add("col.agent").setImportantLevel(importantLevel),
				 				appEnum, sender.getId(), receivers1, importantLevel);
					} catch (MessageException e) {
						log.error("发起公文消息提醒失败!", e);
					}
				}
			}
		}
	}
	
	private static void affairExcuteRemind(Affair affair, Long summaryAccountId){
		if(affair.getApp() == ApplicationCategoryEnum.collaboration.key()
   			|| affair.getApp() == ApplicationCategoryEnum.edoc.key()
   			|| affair.getApp() == ApplicationCategoryEnum.edocRec.key()
   			|| affair.getApp() == ApplicationCategoryEnum.edocSend.key()
   			|| affair.getApp() == ApplicationCategoryEnum.edocSign.key()
   		   ){
	        //超期提醒
	        try {
	        	Timestamp createTime = affair.getReceiveTime() == null ? affair.getCreateDate():affair.getReceiveTime();
	        	Long deadLine = affair.getDeadlineDate();
	            if (deadLine != null && deadLine != 0) {
 					
 					Date deadLineRunTime = workTimeManager.getCompleteDate4Nature(new Date(createTime.getTime()), deadLine, summaryAccountId);
 					
 			    	Long affairId = affair.getId();
 			    	{
	 			    	String name = "DeadLine" + affairId;
	 			   		
	 			    	Map<String, String> datamap = new HashMap<String, String>(2);
	 			    	
	 			    	datamap.put("isAdvanceRemind", "1");
	 			    	datamap.put("affairId", String.valueOf(affairId));
	 			    	
	 			   		QuartzHolder.newQuartzJob(name, deadLineRunTime, "affairIsOvertopTimeJob", datamap);
 			    	}

 			   		Long remindTime = affair.getRemindDate();
	                if (remindTime != null && remindTime != -1 && deadLine > remindTime) {
	                	Date advanceRemindTime = workTimeManager.getRemindDate(deadLineRunTime, remindTime);//.getCompleteDate4Nature(new Date(createTime.getTime()), deadLine - remindTime, summaryAccountId);
	
	                    String name = "Remind" + affairId;
	
	                    Map<String, String> datamap = new HashMap<String, String>(2);
	
	                    datamap.put("isAdvanceRemind", "0");
	                    datamap.put("affairId", String.valueOf(affairId));
	
	                    QuartzHolder.newQuartzJob(name, advanceRemindTime, "affairIsOvertopTimeJob", datamap);
	                }
	            }
	        }
	        catch (Exception e) {
	            log.error("获取定时调度器对象失败", e);
	        }
		}
	}
	
	/*
	public String getLocalAppName(int appType)
	{
		return ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "application."+appType+".label"); 
	}
	*/

}
