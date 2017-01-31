package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.joinwork.bpm.definition.BPMSeeyonPolicy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.callback.TerminateColSupervise;
import com.seeyon.v3x.collaboration.dao.ColSuperviseDetailDao;
import com.seeyon.v3x.collaboration.dao.ColSuperviseLogDao;
import com.seeyon.v3x.collaboration.dao.ColSupervisorDao;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSuperviseReceiver;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColSuperviseManagerImpl;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.dao.EdocSummaryDao;
import com.seeyon.v3x.edoc.dao.EdocSuperviseDetailDao;
import com.seeyon.v3x.edoc.dao.EdocSuperviseLogDao;
import com.seeyon.v3x.edoc.dao.EdocSuperviseRemindDao;
import com.seeyon.v3x.edoc.dao.EdocSupervisorDao;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.domain.EdocSuperviseRemind;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.edoc.webmodel.EdocSuperviseDealModel;
import com.seeyon.v3x.edoc.webmodel.EdocSuperviseModel;
import com.seeyon.v3x.mail.manager.MessageMailManager;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class EdocSuperviseManagerImpl implements EdocSuperviseManager {
	static Log log = LogFactory.getLog(EdocSuperviseManagerImpl.class);
	private EdocSuperviseDetailDao edocSuperviseDetailDao;
	private EdocSuperviseLogDao edocSuperviseLogDao;
	private EdocSupervisorDao edocSupervisorDao;
	private EdocSuperviseRemindDao edocSuperviseRemindDao;
	private EdocSummaryDao edocSummaryDao;
	private MetadataManager metadataManager;
	private OrgManager orgManager;	
	private UserMessageManager userMessageManager;
	private AffairManager affairManager;
	private EdocSummaryManager edocSummaryManager;
	private MobileMessageManager mobileMessageManager;
	private MessageMailManager messageMailManager;

	private ColSuperviseManagerImpl colSuperviseManager;
	private ColSuperviseDetailDao colSuperviseDetailDao;
	private ColSupervisorDao colSupervisorDao;
	private ColSuperviseLogDao colSuperviseLogDao;
    private ProcessLogManager processLogManager ;
    private AppLogManager appLogManager ;
    private SearchManager searchManager;
    
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
	public void createSuperviseDetail(ColSuperviseDetail colSuperviseDetail){
		
		colSuperviseDetailDao.save(colSuperviseDetail);
		
	}
	public int getHastenTimes(long superviseId) {
    	return this.colSuperviseDetailDao.getHastenTimes(superviseId);
    }
	public void createSupervisor(ColSupervisor colSupervisor){
		
		colSupervisorDao.save(colSupervisor);
		
	}
	
	public void createSuperviseLog (ColSuperviseLog colSuperviseLog){
		
		colSuperviseLogDao.save(colSuperviseLog);
		
	}
	
	public void updateSuperviseDetail(ColSuperviseDetail detail){
		colSuperviseDetailDao.update(detail);
	}
	
	public void createSuperviseRemind(EdocSuperviseRemind edocSuperviseRemind){
		
		edocSuperviseRemindDao.save(edocSuperviseRemind);
		
	}

	public EdocSuperviseDetailDao getEdocSuperviseDetailDao() {
		return edocSuperviseDetailDao;
	}

	public void setEdocSuperviseDetailDao(
			EdocSuperviseDetailDao edocSuperviseDetailDao) {
		this.edocSuperviseDetailDao = edocSuperviseDetailDao;
	}

	public EdocSuperviseLogDao getEdocSuperviseLogDao() {
		return edocSuperviseLogDao;
	}

	public void setEdocSuperviseLogDao(EdocSuperviseLogDao edocSuperviseLogDao) {
		this.edocSuperviseLogDao = edocSuperviseLogDao;
	}

	public EdocSuperviseRemindDao getEdocSuperviseRemindDao() {
		return edocSuperviseRemindDao;
	}

	public void setEdocSuperviseRemindDao(
			EdocSuperviseRemindDao edocSuperviseRemindDao) {
		this.edocSuperviseRemindDao = edocSuperviseRemindDao;
	}

	public EdocSupervisorDao getEdocSupervisorDao() {
		return edocSupervisorDao;
	}

	public void setEdocSupervisorDao(EdocSupervisorDao edocSupervisorDao) {
		this.edocSupervisorDao = edocSupervisorDao;
	}
	
	/**
	 * 查找所有的督办项
	 */
	public List<ColSuperviseDetail> findAll(Integer status){
		User user = CurrentUser.get();
		List<ColSuperviseDetail> list = null;
		if(null!=status && status!=0){
			list = colSuperviseDetailDao.getColSuperviseDetailListInMySupervise(user.getId(), status);
		}else{
			list =  colSuperviseDetailDao.getAll();
		}		
		/*List<EdocSuperviseDetail> list = null;
		if(null!=status && status!=0){
			list = edocSuperviseDetailDao.findBy("status", status);
		}else{
			list =  edocSuperviseDetailDao.getAll();
		}*/
		return list;
	}
	
	
	/**
	 * 更新督办集合
	 */
	public void updateAllDetail(List list){
		//edocSuperviseDetailDao.updateAll(list);
		  colSuperviseDetailDao.updateAll(list);
	}
	
	/**
	 * 根据督办的id,查找该督办的所有日志
	 */
	public List<ColSuperviseLog> findLogById(Long superviseId){
		/*
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocSuperviseLog.class);
		criteria.add(Restrictions.eq("superviseId", superviseId));
		List<EdocSuperviseLog> logList = edocSuperviseLogDao.executeCriteria(criteria);
		return logList;
		*/
		//return colSuperviseDetailDao.getLogByDetailId(superviseId);
		return colSuperviseDetailDao.getLogByDetailId(superviseId);
	}
	
	/**
	 * 公文督办,参数由前台传入
	 * @param remindMode 提醒方式
	 * @param supervisorMemberId 督办人员
	 * @param supervisorNames 督办人员的名称
	 * @param superviseDate 督办的期限
	 * @param summaryId 公文的Id
	 */
	public void supervise(String title,String supervisorMemberId,String supervisorNames,String superviseDate,EdocSummary summary){
		User user = CurrentUser.get();
		
		long summaryId = summary.getId();

        if(null!=supervisorMemberId && !"".equals(supervisorMemberId)){
        	
        	boolean bool = false;  //处理时是否为第一次督办
        	//EdocSuperviseDetail detail = null;
        	ColSuperviseDetail detail = null;
        	//detail = edocSuperviseDetailDao.findEdocSuperviseDetailBySummaryId(summaryId);
        	detail = colSuperviseManager.getSupervise(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal(), summaryId);
        	String sNames = supervisorNames;
        	List<MessageReceiver> deleteReceivers = new ArrayList<MessageReceiver>();
        	String orgMemberIds = "";
        	if(null==detail){
        		bool = true;  //第一次督办
        		detail = new ColSuperviseDetail();
        		detail.setIdIfNew();
        		detail.setRemindMode(Integer.valueOf(new Integer(1)));
        		detail.setCreateDate(new Date(System.currentTimeMillis()));
        		detail.setSenderId(user.getId());
        		detail.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_SUPERVISE_PROGRESSING);
        		detail.setCount(0);
        		detail.setDescription(null);
        		detail.setEntityId(summaryId);
        	}else{
        		Set<ColSupervisor> delSupervisors = detail.getColSupervisors();
            	Iterator it = delSupervisors.iterator();
            	while(it.hasNext()){
					ColSupervisor sor = (ColSupervisor)it.next();
					orgMemberIds += sor.getSupervisorId().toString();
					orgMemberIds += ",";
					boolean boo =supervisorMemberId.contains(sor.getSupervisorId().toString());
            		if(!boo){
            			MessageReceiver receiver = new MessageReceiver(detail.getId(), Long.valueOf(sor.getSupervisorId()));//因为是删除，不采取链接的方式
            			deleteReceivers.add(receiver);   
            			continue;
            		}
            	}
            	
            	detail.getColSupervisors().clear();
        			String scheduleProp = detail.getScheduleProp();
        			if(null!=scheduleProp){
        			String[] scheduleProp_Array = scheduleProp.split("\\|");
        			if(null!=scheduleProp_Array && scheduleProp_Array.length > 0){
        				try{
        					Scheduler sched = QuartzListener.getScheduler();
        					sched.deleteJob(scheduleProp_Array[0], scheduleProp_Array[1]); //public boolean deleteJob(String jobName, String groupName);
        					//删除队列中的消息提醒，下面将会重新生成。
        				}catch(Exception e){
        					log.error("删除督办期限提醒消息的计时器出错");
        				}
        			}
        		}
        	}
        	detail.setAwakeDate(Datetimes.parse(superviseDate,Datetimes.datetimeStyle));
    		Set<ColSupervisor> supervisors = null;
    		StringBuffer newSupervisorIds = new StringBuffer("");
    		String superIds = "";
            if(bool){
            	supervisors = new HashSet<ColSupervisor>();
            }else{
            	
            	supervisors = detail.getColSupervisors();
            	
            	Iterator it = supervisors.iterator();
            	try{
            	while(it.hasNext()){
					ColSupervisor sor = (ColSupervisor)it.next();            		
					boolean boo =supervisorMemberId.contains(sor.getSupervisorId().toString());
            		if(!boo){
            			MessageReceiver receiver = new MessageReceiver(detail.getId(), Long.valueOf(sor.getSupervisorId()));//因为是删除，不采取链接的方式
            			//deleteReceivers.add(receiver);   
            			//it.remove();
            			//continue;
            		}
            		superIds += sor.getSupervisorId();
            		superIds +=",";
            	}
    			
    			}catch(Exception e){
    				log.error("删除督办人出错",e);
    			}
            }
            
            if(supervisorMemberId.endsWith(",")){
            	supervisorMemberId = supervisorMemberId.substring(0, supervisorMemberId.length()-1);
            }
            String[] spArray = supervisorMemberId.split(",");
        	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
        	StringBuffer names = new StringBuffer("");
        	V3xOrgMember member = null;
        	for(String s:spArray){
        		//if(!bool && superIds.contains(s)){
        		//	continue;
        		//}
        		ColSupervisor supervisor = new ColSupervisor();
        		supervisor.setIdIfNew();
        		supervisor.setSuperviseId(detail.getId());
        		supervisor.setSupervisorId(Long.valueOf(s));
        		supervisor.setPermission(com.seeyon.v3x.edoc.util.Constants.EDOC_SUPERVISOR_PERMISSION_CHANGE);
        		supervisors.add(supervisor);
    			newSupervisorIds.append(s);
    			newSupervisorIds.append(",");
    			boolean boo =orgMemberIds.contains(supervisor.getSupervisorId().toString());
        		if(!boo){
        			MessageReceiver receiverA = new MessageReceiver(detail.getId(), supervisor.getSupervisorId(),"message.link.edoc.supervise.detail",detail.getEntityId());
        			receivers.add(receiverA);
        		}
    			try{
    				member = this.orgManager.getMemberById(Long.valueOf(s));
                if(member!=null){
                	names.append(member.getName());
                	names.append(",");
                }	
    			}catch(Exception e){
    				log.error("得到督办人实体错误 : ",e);
    				continue;
    			}
        	}
        	
        	String fNames = "";
        	if(names.toString().endsWith(",")){
        		fNames = names.substring(0, names.length()-1);
        	}
        	detail.setColSupervisors(supervisors);
        	detail.setSupervisors(fNames);
        	
        	try {
    	    	Scheduler sched = QuartzListener.getScheduler();
    	        Long jobId = UUIDLong.longUUID();
    	        String jobName = jobId.toString();
    	        Long groupId = UUIDLong.longUUID();
    	        String groupName = groupId.toString();
    	        Long triggerId = UUIDLong.longUUID();
    	        String triggerName = triggerId.toString();
    	
    	        SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, detail.getAwakeDate());
    	        JobDataMap datamap = new JobDataMap();
    			datamap.putAsString("colSuperviseId", detail.getId());
    			datamap.putAsString("senderId", user.getId());
    			datamap.put("supervisorMemberId",supervisorMemberId.toString());
    			datamap.put("subject", summary.getSubject());
    			
    			JobDetail job = new JobDetail(jobName, groupName, TerminateColSupervise.class);
                job.setJobDataMap(datamap);
                sched.scheduleJob(job, trigger);   
                
                String scheduleProp = jobName + "|" + groupName;
                detail.setScheduleProp(scheduleProp);
        	}catch(SchedulerException e) {
        		log.error(e);
        	}
  
        	
            try{
                int edocType = summary.getEdocType();
                ApplicationCategoryEnum app = null;
             
                	if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocSend;
                	}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocRec;               		
                	}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
                		app = ApplicationCategoryEnum.edocSign;               		
                	}else{
                		app = ApplicationCategoryEnum.edoc; 
                	}
        		userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.hasten",summary.getSubject(),user.getName(),app.key()), app, user.getId(), receivers);
        		if(deleteReceivers.size()!=0){
        			userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.delete",summary.getSubject(),user.getName(),app.key()), app, user.getId(), deleteReceivers);//给被删除的人发消息
        		}
        	}catch(Exception e){
        		log.error("给督办人发消息失败!");
        	}
        	detail.setTitle(title);
    		detail.setEntityType(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal());
        	if(bool){
            	colSuperviseDetailDao.save(detail);
			}else{
            	colSuperviseDetailDao.update(detail);
            	}
        }		
	}
	
	/**
	 * 查找未处理的督办事项
	 * @param supervisorId
	 * @param type
	 * @return
	 */
//	public List<EdocSuperviseModel> findToBeProcessedListBySupervisor_bak(Long supervisorId){
//		
//		List<EdocSuperviseModel> modelList = new ArrayList<EdocSuperviseModel>();
//		
//		List list = colSuperviseDetailDao.getColSuperviseDetailListInMySupervise(supervisorId, Constants.EDOC_SUPERVISE_PROGRESSING, com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal());
//		
//		long caseId = 0;
//		String caseLogXML = "";
//		String caseProcessXML = "";
//		String caseWorkItemLogXML = "";
//		Boolean hasWorkflow = false;
//		String process_desc_by = "";
//		String appName = null;
//		
//		EdocManager edocManager= (EdocManager)ApplicationContextHolder.getBean("edocManager");
//		
//		if(null!=list && list.size()>0){
//		for(int i=0;i<list.size();i++){
//			ColSuperviseDetail detail = (ColSuperviseDetail)list.get(i);
//			EdocSuperviseModel model = new EdocSuperviseModel();
//			model.setId(detail.getId());
//			model.setDescription(detail.getDescription());
//			model.setEndDate(detail.getAwakeDate());
//			model.setStartDate(detail.getCreateDate());
//			Long edocId = detail.getEntityId();
//			model.setEdocId(edocId);
//			EdocSummary summary = edocSummaryDao.get(edocId);
//			if(null==summary)continue;
//			model.setTitle(summary.getSubject());
//			/*
//			String secretLevel = "";
//			if(null!=summary.getSecretLevel() && !"".equals(summary.getSecretLevel())){
//				secretLevel = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_secret_level, summary.getSecretLevel());			
//			}*/
//			model.setSecretLevel(summary.getSecretLevel());
//			model.setCount(detail.getCount());
//			model.setRemindModel(detail.getRemindMode());
//			model.setSupervisor(detail.getSupervisors());
//			model.setContent(Strings.toHTML(detail.getDescription()));
//			model.setStatus(detail.getStatus());
//			model.setDeadline(summary.getDeadline());
//			Long senderId = summary.getStartUserId();
//			
//			Date startDate = summary.getCreateTime();
//			Date finishDate = summary.getCompleteTime();
//			Date now = new Date(System.currentTimeMillis());
//			
//			if(null!=detail.getAwakeDate() && now.after(detail.getAwakeDate())){
//				model.setIsRed(true);
//			}
//			if(summary.getDeadline() != null && summary.getDeadline() != 0){
//				Long deadline = summary.getDeadline()*60000;
//				if(finishDate == null){
//					if((now.getTime()-startDate.getTime()) > deadline){
//						model.setWorkflowTimeout(true);
//					}
//				}else{
//					Long expendTime = summary.getCompleteTime().getTime() - summary.getCreateTime().getTime();
//					if((deadline-expendTime) < 0){
//						model.setWorkflowTimeout(true);
//					}
//				}
//			}
//			if(null!=senderId){
//				try{
//	            V3xOrgMember member = this.orgManager.getMemberById(senderId);
//	            if(member!=null){
//	    			model.setSender(member.getName());	            	
//	            }else{
//	    			model.setSender("");		            	
//	            }
//				}catch(Exception e){};
//			}
//			/*
//			try{
//			V3xOrgMember member = orgManager.getMemberById(detail.getSenderId());
//			model.setSender(member.getName());
//			}catch(Exception e){	
//			}*/
//			/*
//			String doc_type = summary.getDocType();
//			MetadataItem metaDataItem = null;
//			List<MetadataItem> metaItem = null;
//			String name = "";
//			metaItem  =  metadataManager.getMetadataItems("edoc_doc_type");
//			String edoc_doc_type = "";
//			if(null!=summary.getDocType() && !"".equals(summary.getDocType())){
//				edoc_doc_type = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_doc_type, summary.getDocType());				
//			}
//			model.setEdocType(edoc_doc_type);
//			*/
//			model.setEdocType(summary.getDocType());
//			int edoc_type = summary.getEdocType();
//			String edocType = "";
//			if(edoc_type==0){
//				edocType = "edoc.docmark.inner.send";
//			}else if(edoc_type==1){
//				edocType = "edoc.docmark.inner.receive";
//			}else if(edoc_type==2){
//				edocType = "edoc.doctype.endorsement";
//			}
//			appName = EdocEnum.getEdocAppName(edoc_type);
//			
//			if(null == summary.getCaseId())continue;
//			
//			caseId = summary.getCaseId();
//			try{
//				caseLogXML = edocManager.getCaseLogXML(caseId);
//				caseProcessXML = edocManager.getCaseProcessXML(caseId);
//				caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
//			}catch(Exception e){
//			}
//			model.setCaseLogXML(Strings.toHTML(caseLogXML));
//			model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
//			model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));
//			if (StringUtils.isNotEmpty(caseProcessXML)) {
//				hasWorkflow = true;
//				process_desc_by = FlowData.DESC_BY_XML;		
//			}
//			model.setHasWorkflow(hasWorkflow);
//			model.setProcess_desc_by(process_desc_by);
//			model.setAppName(appName);
//			model.setCaseId(summary.getCaseId());
//	        int actorId=EdocEnum.getStartAccessId(edoc_type);
//	        model.setActorId(actorId);
//			modelList.add(model);
//			}
//		}
//		return modelList;
//	}
	
	/**
	 * 查找已处理的督办事项
	 * @param supervisorId
	 * @param type
	 * @return
	 */
//	public List<EdocSuperviseModel> findProcessedListBySupervisor_bak(Long supervisorId){
//		
//		List<EdocSuperviseModel> modelList = new ArrayList<EdocSuperviseModel>();
//		
//		List list = colSuperviseDetailDao.getColSuperviseDetailListInMySupervise(supervisorId, Constants.EDOC_SUPERVISE_TERMINAL,com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal());
//		User user = CurrentUser.get();
//		
//		long caseId = 0;
//		String caseLogXML = "";
//		String caseProcessXML = "";
//		String caseWorkItemLogXML = "";
//		Boolean hasWorkflow = false;
//		String process_desc_by = "";
//		String appName = null;
//		
//		EdocManager edocManager= (EdocManager)ApplicationContextHolder.getBean("edocManager");
//		
//		if(null!=list && list.size()>0){
//		for(int i=0;i<list.size();i++){
//			ColSuperviseDetail detail = (ColSuperviseDetail)list.get(i);
//			EdocSuperviseModel model = new EdocSuperviseModel();
//			model.setId(detail.getId());
//			model.setDescription(detail.getDescription());
//			model.setEndDate(detail.getAwakeDate());
//			model.setStartDate(detail.getCreateDate());
//			Long edocId = detail.getEntityId();
//			model.setEdocId(edocId);
//			EdocSummary summary = edocSummaryDao.get(edocId);
//			if(null==summary)continue;
//			model.setTitle(summary.getSubject());
//			/*
//			String secretLevel = "";
//			if(null!=summary.getSecretLevel() && !"".equals(summary.getSecretLevel())){
//				secretLevel = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_secret_level, summary.getSecretLevel());			
//			}*/
//			model.setSecretLevel(summary.getSecretLevel());
//			model.setCount(detail.getCount());
//			model.setRemindModel(detail.getRemindMode());
//			model.setSupervisor(user.getName());
//			model.setContent(Strings.toHTML(detail.getDescription()));
//			Long senderId = summary.getStartUserId();
//			model.setDeadline(summary.getDeadline());
//			
//			Date startDate = summary.getCreateTime();
//			Date finishDate = summary.getCompleteTime();
//			Date now = new Date(System.currentTimeMillis());
//
//			if(null!=detail.getAwakeDate() && now.after(detail.getAwakeDate())){
//				model.setIsRed(true);
//			}
//			
//			if(summary.getDeadline() != null && summary.getDeadline() != 0){
//				Long deadline = summary.getDeadline()*60000;
//				if(finishDate == null){
//					if((now.getTime()-startDate.getTime()) > deadline){
//						model.setWorkflowTimeout(true);
//					}
//				}else{
//					Long expendTime = summary.getCompleteTime().getTime() - summary.getCreateTime().getTime();
//					if((deadline-expendTime) < 0){
//						model.setWorkflowTimeout(true);
//					}
//				}
//			}
//			
//			if(null!=senderId){
//				try{
//	            V3xOrgMember member = this.orgManager.getMemberById(senderId);
//	            if(member!=null){
//	    			model.setSender(member.getName());	            	
//	            }else{
//	    			model.setSender("");		            	
//	            }
//				}catch(Exception e){};
//			}
//
//			/*
//			try{
//			V3xOrgMember member = orgManager.getMemberById(detail.getSenderId());
//			model.setSender(member.getName());
//			}catch(Exception e){	
//			}*/
//			int edoc_type = summary.getEdocType();
//			String edocType = "";
//			if(edoc_type==0){
//				edocType = "edoc.docmark.inner.send";
//			}else if(edoc_type==1){
//				edocType = "edoc.docmark.inner.receive";
//			}else if(edoc_type==2){
//				edocType = "edoc.doctype.endorsement";
//			}
//			appName = EdocEnum.getEdocAppName(edoc_type);
//			
//			if(null == summary.getCaseId())continue;
//			
//			caseId = summary.getCaseId();
//			try{
//				caseLogXML = edocManager.getCaseLogXML(caseId);
//				caseProcessXML = edocManager.getCaseProcessXML(caseId);
//				caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
//			}catch(Exception e){
//			}
//			model.setCaseLogXML(Strings.toHTML(caseLogXML));
//			model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
//			model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));
//			if (StringUtils.isNotEmpty(caseProcessXML)) {
//				hasWorkflow = true;
//				process_desc_by = FlowData.DESC_BY_XML;		
//			}
//			model.setEdocType(summary.getDocType());
//			model.setHasWorkflow(hasWorkflow);
//			model.setProcess_desc_by(process_desc_by);
//			model.setAppName(appName);
//			model.setCaseId(summary.getCaseId());
//	        int actorId=EdocEnum.getStartAccessId(edoc_type);
//	        model.setActorId(actorId);
//			modelList.add(model);
//			}
//		}
//		return modelList;
//	}
	
    //催办
    public void sendMessage(Long superviseId,String mode,String processId, String activityId, String additional_remark, long[] people,String summaryId){
        additional_remark = additional_remark == null ? "" : Constant.getString4CurrentUser("sender.note.label") + ":" + additional_remark;
        User user = CurrentUser.get();

        Set<Long> memberIds = new HashSet<Long>();
        for (Long l : people) {
        	memberIds.add(l);
		}
        
        List<Affair> affairs = affairManager.getPendingAffairListByObject(Long.parseLong(summaryId));
        
        if(affairs != null && !affairs.isEmpty()){
        	Affair hastenAffair = affairs.get(0);
            Set<Long> existMemberIds = new HashSet<Long>();
            try {
            	Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
		        for (Affair affair : affairs) {
		            Long memberId = affair.getMemberId();
		            
		        	if(!memberIds.contains(memberId) || existMemberIds.contains(memberId)){
		        		continue;
		        	}
		        	//过滤掉待发送的公文。
		        	if(affair.getApp()==22) continue; 
		        	
		        	existMemberIds.add(memberId);
		        	
		            //如果设置代理，催办消息发给代理人
		            V3xOrgMember member = this.orgManager.getMemberById(memberId);
		            if(member!=null&&member.getAgentId()!=V3xOrgEntity.DEFAULT_NULL_ID)
		            	memberId = member.getAgentId();
		
		            Integer hastenTimes = affair.getHastenTimes();
		            if (hastenTimes == null)
		                hastenTimes = 0;
		            hastenTimes += 1;
		            affair.setHastenTimes(hastenTimes);
		            affairManager.updateAffair(affair);
		            hastenAffair=affair;
		
				    receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.edoc.pending",affair.getId().toString()));
				   
				    //userMessageManager.sendSystemMessage(new MessageContent("col.hasten", summary.getSubject() + additional_remark, user.getName()), ApplicationCategoryEnum.collaboration, user.getId(), receivers);
		        }
	            String subject = hastenAffair.getSubject();
	            Integer importantLevel = hastenAffair.getImportantLevel();
		    	colSuperviseDetailDao.saveDbLog(superviseId);
		       	ColSuperviseLog superviseLog = new ColSuperviseLog();
		        	superviseLog.setIdIfNew();
		        	superviseLog.setSender(user.getId());
		        	superviseLog.setSendTime(new Date());
		        	superviseLog.setSuperviseId(superviseId);
		        	superviseLog.setType(Constant.suerviseLogType.hasten.ordinal());
		        	superviseLog.setContent(additional_remark);
		        	Set<ColSuperviseReceiver> set = new HashSet<ColSuperviseReceiver>();
		        	for(long receiverId : people) {
		        		ColSuperviseReceiver receiver = new ColSuperviseReceiver();
		        		receiver.setIdIfNew();
		        		receiver.setLogId(superviseLog.getId());
		        		receiver.setReceiver(receiverId);
		        		set.add(receiver);
		        	}
		        		
		        	superviseLog.setReceivers(set);
		        	colSuperviseDetailDao.save(superviseLog);		        
		        
		        if(hastenAffair.getApp() == ApplicationCategoryEnum.collaboration.getKey()){
				    userMessageManager.sendSystemMessage(new MessageContent("col.hasten", subject, user.getName(), additional_remark, 0),
				    		ApplicationCategoryEnum.valueOf(hastenAffair.getApp()), user.getId(), receivers, importantLevel);
		        }
		        else{
		        	userMessageManager.sendSystemMessage(new MessageContent("edoc.hasten", subject, user.getName(), additional_remark,hastenAffair.getApp()),
		        			ApplicationCategoryEnum.valueOf(hastenAffair.getApp()), user.getId(), receivers, importantLevel);
		        }
            }
            /*
		        
		        V3xOrgMember member = orgManager.getMemberById(receivers.get(0).getReceiverId());
		        
		        //循环结束后给该节点发消息。
		        if(mode.intValue() == Constants.EDOC_SUPERVISE_REMINDMODE_ONLINE){
		        	userMessageManager.sendPersonMessage(additional_remark, user.getId(), receivers.get(0).getReceiverId());
		        }else if(mode.intValue() == Constants.EDOC_SUPERVISE_REMINDMODE_MESSAGE){
		        	sendMessageByMobileShortMessage(additional_remark, user.getId(), receivers.get(0).getReceiverId());//发送短信
		        }else if(mode.intValue() == Constants.EDOC_SUPERVISE_REMINDMODE_EMAIL){
		        	if(member!=null)
		        	this.sendMessageByEmail(member.getEmailAddress(), additional_remark); 
		        }
		        // userMessageManager.sendSystemMessage(new MessageContent(key,subject,user.getName(),additional_remark), ApplicationCategoryEnum.valueOf(hastenAffair.getApp()), user.getId(), receivers);
			    //new MessageContent(key,affair.getSubject(),user.getName(),additional_remark) 第一个 为userMessageResource中对应的key,后面的为参数，可以传多个
            }
            */
            catch (Exception e) {
            	log.error(e.getMessage(), e);
            }
        }
    	//SuperviseDetail 中的催办次数增1
    }
	
    public void updateBySummaryId(long summaryId){
    	
    	colSuperviseDetailDao.updateEdocStatusBySummaryId(summaryId);
    	
    	/*
    	List<EdocSuperviseDetail> list = edocSuperviseDetailDao.findBy("edocId",summaryId);
    	for(EdocSuperviseDetail detail : list){
    		detail.setStatus(Constants.EDOC_SUPERVISE_TERMINAL);
    	}
    	this.updateAllDetail(list);
    	*/
    }
    
    /**
     * 改变督办的流程
     * @param str
     * @param idArr
     * @param typeArr
     * @param nameArr
     * @param accountIdArr
     * @param accountShortNameArr
     * @return
     */
    public String[] changeProcess(String str, String[] idArr, String[] typeArr, String[] nameArr, 
    		String[] accountIdArr, String[] accountShortNameArr, String[] selecteNodeIdArr, String[] _peopleArr, String summaryId,String[] condition,String[] nodes){
    	User user = CurrentUser.get();
    	
    	String[] strArr = str.split(",");
    	String processId = strArr[0];
    	String activityId = strArr[1];
    	String operationType = strArr[2];
    	String flowType = strArr[3];
    	String isShowShortName = strArr[4];
    	String desc_by = strArr[5];
    	
    	FlowData flowData = new FlowData();
    	if(!"null".equals(desc_by) && !"null".equals(flowType)){
    		String _desc_by = FlowData.DESC_BY_XML.equals(desc_by) ? FlowData.DESC_BY_XML : FlowData.DESC_BY_PEOPLE;
    		flowData.setDesc_by(_desc_by);
    		
    		String[] types = typeArr;
    		String[] ids = idArr;
    		String[] names = nameArr;
    		String[] accountIds = accountIdArr;
    		String[] accountShortNames = accountShortNameArr;
    		
    		if(names == null && ids != null && types != null){
    			names = FlowData.getElementNames(ids, types);
    		}
    		
    		List<Party> people = new ArrayList<Party>();
    		if (ids != null) {
    			for (int i = 0; i < ids.length; i++) {
    				String id = ids[i];
    				String type = FlowData.getUserTypeByField(types[i] + "");
    				String name = names[i];
    				String accountId = accountIds[i];
    				String accountShortName = accountShortNames[i];
    				
    				Party party = new Party(type, id, name, accountId, accountShortName);
    				switch(Integer.parseInt(flowType)){
    				 case 0 : 
    				 case 1 :  
    				 case 2 : 
    				 party.setSeeyonPolicy(BPMSeeyonPolicy.EDOC_POLICY_SHENPI) ;
    				 break ;
    				 case 3 : 
    					 BPMSeeyonPolicy policy = null ;
    	                 if(flowData.getSeeyonPolicy() != null)
    	                		policy = new BPMSeeyonPolicy(flowData.getSeeyonPolicy());
    	                 else
    	                		policy = new BPMSeeyonPolicy("huiqian","会签");
    					 party.setSeeyonPolicy(policy) ;
    				}
    				people.add(party);
    			}
    		}
    		
    		flowData.setPeople(people);
    		
    		int iFlowType = 0;
            switch(Integer.parseInt(flowType)){
	            case 0 : iFlowType = FlowData.FLOWTYPE_SERIAL;
	            break;
	            case 1 : iFlowType = FlowData.FLOWTYPE_PARALLEL;
	            break;
	            case 2 : iFlowType = FlowData.FLOWTYPE_MULTIPLE;
	            break;
	            case 3 : iFlowType = FlowData.FLOWTYPE_COLASSIGN;
            }
    		flowData.setType(iFlowType);
    		flowData.setIsShowShortName(isShowShortName);
    	}
    	if(nodes != null){
			Map<String,String> map = new HashMap<String,String>();
			for(int i=0;i<nodes.length;i++){
				if(!"".equals(nodes[i]) && !"".equals(condition[i]))
					map.put(nodes[i], condition[i]);
			}
			if(map.size()>0)
				flowData.setCondition(map);
				
		}
    	String[] xmlStr = new String[]{};
    	EdocSummary summary = edocSummaryManager.getSummaryByProcessId(processId);
    	try {
    		xmlStr = ColHelper.superviseUpdateProcess(processId, activityId, Integer.parseInt(operationType), flowData,
    				null, user, selecteNodeIdArr, _peopleArr, summary.getCaseId());
    	} catch (BusinessException e) {
    		log.error("公文督办更新流程失败：changeProcess", e);
    	}
    	
    	return xmlStr;
    }
    public String[] changeProcess1(String[] flowProp, String[] policyStr, String summaryId){
    	User user = CurrentUser.get();
    	String[] strArr = flowProp;
    	String processId = strArr[0];
    	String activityId = strArr[1];
    	String operationType = strArr[2];
    	BPMSeeyonPolicy policy = new BPMSeeyonPolicy();
    	String[] policyArr = policyStr;
    	policy.setId(policyArr[0]);
    	policy.setName(policyArr[1]);
    	policy.setdealTerm(policyArr[2]);
    	policy.setRemindTime(policyArr[3]);
    	policy.setProcessMode(policyArr[4]);
    	policy.setMatchScope(policyArr[5]);
    	
		FlowData flowData = new FlowData();
		String[] xmlStr = new String[]{};
		EdocSummary summary = edocSummaryManager.getSummaryByProcessId(processId);
		try {
			xmlStr = ColHelper.superviseUpdateProcess(processId, activityId, Integer.parseInt(operationType), flowData,
					policy, user, null, null, summary.getCaseId());
            //更新affair中的node_policy,提前提醒时间,处理期限
			int deadlineDate=0;
			int remindDate=0;
			if( policyArr[2]!=null&&!"".equals( policyArr[2])) deadlineDate=Integer.parseInt(policyArr[2]);
			if( policyArr[3]!=null&&!"".equals(policyArr[3]))remindDate=Integer.parseInt(policyArr[3]);
			this.affairManager.updateAffairByObjectAndActivity(summary.getId(), Long.parseLong(activityId), policy.getId(),deadlineDate,remindDate);
		} catch (BusinessException e) {
			log.error("公文督办更新流程失败：changeProcess", e);
		}
		
    	return xmlStr;
    }
    
    /**
     * 督办人删除自己督办人权限的方法,只删除自己,督办及其他督办人保留
     */
    public void deleteSuperviseDetail(String superviseIds)throws Exception{
    	User user = CurrentUser.get();
    	if(superviseIds == null || "".equals(superviseIds))
    		return;
    	String[] ids = superviseIds.split(",");
    	if(ids != null) {
    		List<Long> longIds = new ArrayList<Long>();
    		for(String id:ids) {
    			longIds.add(Long.parseLong(id)) ;
    		}
    		Map<String, Object> nameParameters = new HashMap<String,Object>();
    		nameParameters.put("superviseIds", longIds);
    		colSuperviseDetailDao.deleteSupervised(user.getId(),nameParameters);
    	}
    }
	
    /**
     * 彻底删除督办项及所属的督办人
     */
    public void deleteSuperviseDetailAndSupervisors(EdocSummary summary)throws EdocException{
    		if(summary != null){
    			long summaryId = summary.getId();
    			ColSuperviseDetail detail = colSuperviseDetailDao.getSupervise(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal(), summaryId);
    			if(null!=detail){
    				/*
    				Set<ColSupervisor> set = detail.getColSupervisors();
    				if(set!=null)
    				{
    				for(ColSupervisor sor:set){
    					colSupervisorDao.deleteObject(sor);
    				}
    				}
    				*/
    				//给督办人发消息
    				
    				Set<ColSupervisor> supervisors = detail.getColSupervisors();
    				List<MessageReceiver> deleteReceivers = null;
    				if(supervisors != null) {
    					deleteReceivers = new ArrayList<MessageReceiver>();
    					for(ColSupervisor supervisor:supervisors) {
    						//删除督办人，不需要显示链接
    						MessageReceiver deleteReceiver = new MessageReceiver(detail.getId(), supervisor.getSupervisorId());
    						deleteReceivers.add(deleteReceiver);
    					}
    				}
    				colSupervisorDao.delete(new String[]{"superviseId"},new Object[]{detail.getId()});
    				if(detail.getId()!=null){
    					colSuperviseDetailDao.delete(detail.getId().longValue());
    				}
    				if(deleteReceivers != null && deleteReceivers.size()>0 && summary != null) {
    					int edocType = summary.getEdocType();
    	                ApplicationCategoryEnum app = null;
    	             
    	                if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
    	                	app = ApplicationCategoryEnum.edocSend;
    	                }else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
    	                	app = ApplicationCategoryEnum.edocRec;               		
    	                }else if(edocType == EdocEnum.edocType.signReport.ordinal()){
    	                	app = ApplicationCategoryEnum.edocSign;               		
    	                }else{
    	                	app = ApplicationCategoryEnum.edoc; 
    	                }
    					//TODO 这里先临时取CurrentUser
    					try {
    						if(!summary.getHasArchive())
    							//{2,choice,1#协同|4#公文|19#发文|20#收文|21#签报}《{0}》的督办权限已被 {1} 撤销
    							userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.delete",summary.getSubject(),CurrentUser.get().getName(),app.ordinal()), app, CurrentUser.get().getId(), deleteReceivers);
    						else//{1,choice,1#协同|4#公文|19#发文|20#收文|21#签报}《{0}》已归档,督办权限被撤销
    							userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.pigeonholed",summary.getSubject(),app.ordinal()), app, CurrentUser.get().getId(), deleteReceivers);
    					}catch(Exception e) {
    						log.error("",e);
    					}
    				}
    			}
    		}
    }
    
    /**
     * 更新督办记录
     */
	public void changeSuperviseDetail(ColSuperviseDetail detail){
		colSuperviseDetailDao.update(detail);
	}

	public ColSuperviseDetail getSuperviseById(Long id){
		
		return colSuperviseDetailDao.get(id);
	
	}
	
	/**
	 * 公文归档后对督办的处理
	 */
	public void pigeonhole(EdocSummary summary)throws EdocException{
		
			this.deleteSuperviseDetailAndSupervisors(summary);
			
			/*
			detail.setStatus(Constants.EDOC_SUPERVISE_TERMINAL);
			edocSuperviseDetailDao.update(detail);
			*/
	}
	
	public EdocSummaryDao getEdocSummaryDao() {
		return edocSummaryDao;
	}

	public void setEdocSummaryDao(EdocSummaryDao edocSummaryDao) {
		this.edocSummaryDao = edocSummaryDao;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
	
	public ColSuperviseDetail getSuperviseBySummaryId(long summaryId){
		return colSuperviseDetailDao.getSupervise(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal(), summaryId);
	}
	
	public List<ColSupervisor> getEdocSupervisorBySuperviseId(long superviseId){
		DetachedCriteria criteria = DetachedCriteria.forClass(ColSupervisor.class);
		criteria.add(Restrictions.eq("superviseId", superviseId));
		List<ColSupervisor> supervisorList = edocSupervisorDao.executeCriteria(criteria);
		return supervisorList;
	}
	
	public void deleteSuperviseDetailAndSupervisorWhenDelteOrWithDraw(Long summaryId)throws Exception{

		User user = CurrentUser.get();
		
		EdocSummary summary = edocSummaryManager.findById(summaryId);
		
		if(null!=summaryId && null!=summary){

			ColSuperviseDetail detail = colSuperviseDetailDao.findEdocSuperviseDetailBySummaryId(summaryId);
			if(null!=detail){
				Set<ColSupervisor> set = detail.getColSupervisors();
				List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();

				
    	    
				for(ColSupervisor sor:set){
					receivers.add(new MessageReceiver(detail.getId(), sor.getSupervisorId()));
				}
            	if(null!=receivers && receivers.size()>0){
            		userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.withdraw", summary.getSubject(), user.getName() ,ApplicationCategoryEnum.edoc), ApplicationCategoryEnum.edoc, user.getId(), receivers);
            	}
            	
				for(ColSupervisor sor:set){
					colSupervisorDao.deleteObject(sor);
				}
				colSuperviseDetailDao.deleteObject(detail);
			}
		}		
		
	}
	
	public void deleteSupervisorBySupervisorIdAndDetailId(long detailId, long supervisorId)throws Exception{
		edocSupervisorDao.deleteSupervisorsBySupervisorIdAndDetailId(detailId, supervisorId);
	}
	
	/**
	 * 发送邮件消息
	 * @param toEmail
	 * @param content
	 */
	private void sendMessageByEmail(String toEmail, String content){
		//messageMailManager.sendMessageByMail(toEmail, content);
	}
	
	
	public List<EdocSuperviseDealModel> getAffairModel(long summaryId){
		EdocSummary summary = edocSummaryDao.get(summaryId);
        ApplicationCategoryEnum app = null;
		if(null!=summary){
            int edocType = summary.getEdocType();
            	if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
            		app = ApplicationCategoryEnum.edocSend;
            	}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
            		app = ApplicationCategoryEnum.edocRec;               		
            	}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
            		app = ApplicationCategoryEnum.edocSign;               		
            	}else{
            		app = ApplicationCategoryEnum.edoc; 
            	}
    	Map conditions = new LinkedHashMap();
    	conditions.put("objectId", summaryId);
    	conditions.put("state", StateEnum.col_done.key());
    	conditions.put("app", app.key());
    	conditions.put("isDelete", false);
    	
    	List<Affair> affairs = affairManager.getByConditionsPaginationOrderByCompleteTime(conditions);
    	if(affairs == null)
    		return null;
    	Metadata deadlineMeta = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
    	List<MetadataItem> itms = deadlineMeta.getItems();
    	Map<String,String> map = new HashMap<String,String>();
    	for(MetadataItem item:itms)
    		map.put(item.getValue(), item.getLabel());
    	String bundleName = "com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";;
    	String keyGood =  "col.supervise.dealline.good";
    	String keyBad = "col.supervise.dealline.bad";
    	List<EdocSuperviseDealModel> models = new ArrayList<EdocSuperviseDealModel>();
    	SeeyonPolicy seeyonPolicy = null;
    	String policyName = "";
    	for(Affair affair:affairs) {
    		EdocSuperviseDealModel model = new EdocSuperviseDealModel();
    		Date receiveDate = affair.getReceiveTime();
    		Date computeDate = affair.getCompleteTime();
    		Long deallineDate = affair.getDeadlineDate();
    		model.setDealUser(affair.getMemberId());
    		model.setReveiveDate(receiveDate);
    		model.setDealDate(computeDate);
    		model.setHastened(affair.getHastenTimes()==null?0:affair.getHastenTimes().intValue());
    		if(deallineDate != null)
    			model.setDealLine(map.get(deallineDate.toString()));
    		model.setEfficiency(ResourceBundleUtil.getString(bundleName, keyGood));
    	    if(computeDate != null) {
    	    	long[] dates = Datetimes.detailInterval(receiveDate, computeDate);
    	    	model.setDealDays(ColHelper.timePatchwork(dates[0],dates[1],dates[2],dates[3],false));
    	    	if(deallineDate != null && deallineDate>0 && computeDate.getTime()-receiveDate.getTime()>deallineDate*60000) {
    	    		model.setEfficiency(ResourceBundleUtil.getString(bundleName, keyBad));
    	    		model.setOverTime(true);
    	    	}
    	    }else {
    	    	Date today = new Date();
    	    	if(deallineDate != null && deallineDate>0 && today.getTime()-receiveDate.getTime()>deallineDate*60000) {
    	    		model.setEfficiency(ResourceBundleUtil.getString(bundleName, keyBad));
    	    		model.setOverTime(true);
    	    	}
    	    }
    	    try {
    	    	seeyonPolicy = ColHelper.getPolicyByAffair(affair);
    	    	policyName = Constant.getCommonString("node.policy."+ColHelper.getPolicyByAffair(affair).getId());
    	    	if(policyName.length() > 12 && policyName.substring(0, 11).equals("node.policy")){
					policyName = seeyonPolicy.getName();
				}
    	    }catch(Exception e) {
    	    	policyName = Constant.getCommonString("node.policy.collaboration");
    	    }
    	    model.setPolicyName(policyName);
    	    models.add(model);
    	}
    	return models;
		}else{
			return null;
		}
    }
	
	/**
	 * 发送短信消息
	 * @param additional_remark
	 * @param senderId
	 * @param receiverId
	 */
	private void sendMessageByMobileShortMessage(String additional_remark, long senderId, long receiverId){
		
		mobileMessageManager.sendMobilePersonMessage(additional_remark, senderId, new Date(), receiverId);
	}

	public MobileMessageManager getMobileMessageManager() {
		return mobileMessageManager;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

	public MessageMailManager getMessageMailManager() {
		return messageMailManager;
	}

	public void setMessageMailManager(MessageMailManager messageMailManager) {
		this.messageMailManager = messageMailManager;
	}


	/**
	 * @return the colSuperviseManager
	 */
	public ColSuperviseManagerImpl getColSuperviseManager() {
		return colSuperviseManager;
	}

	/**
	 * @param colSuperviseManager the colSuperviseManager to set
	 */
	public void setColSuperviseManager(ColSuperviseManagerImpl colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}

	/**
	 * @return the colSuperviseDetailDao
	 */
	public ColSuperviseDetailDao getColSuperviseDetailDao() {
		return colSuperviseDetailDao;
	}

	/**
	 * @param colSuperviseDetailDao the colSuperviseDetailDao to set
	 */
	public void setColSuperviseDetailDao(ColSuperviseDetailDao colSuperviseDetailDao) {
		this.colSuperviseDetailDao = colSuperviseDetailDao;
	}

	/**
	 * @return the colSupervisorDao
	 */
	public ColSupervisorDao getColSupervisorDao() {
		return colSupervisorDao;
	}

	/**
	 * @param colSupervisorDao the colSupervisorDao to set
	 */
	public void setColSupervisorDao(ColSupervisorDao colSupervisorDao) {
		this.colSupervisorDao = colSupervisorDao;
	}

	/**
	 * @return the colSuperviseLogDao
	 */
	public ColSuperviseLogDao getColSuperviseLogDao() {
		return colSuperviseLogDao;
	}

	/**
	 * @param colSuperviseLogDao the colSuperviseLogDao to set
	 */
	public void setColSuperviseLogDao(ColSuperviseLogDao colSuperviseLogDao) {
		this.colSuperviseLogDao = colSuperviseLogDao;
	}
	
	/**
	 * 公文督办,参数由前台传入
	 * @param remindMode 提醒方式
	 * @param supervisorMemberId 督办人员
	 * @param supervisorNames 督办人员的名称
	 * @param superviseDate 督办的期限
	 * @param summaryId 公文的Id
	 */
	public void superviseForTemplate(String remindMode,String supervisorMemberId,String supervisorNames,String superviseDate,EdocSummary summary,String title) throws Exception{
		User user = CurrentUser.get();

        if(null!=supervisorMemberId && !"".equals(supervisorMemberId)){
        	
        	boolean bool = false;  //处理时是否为第一次督办
        	//EdocSuperviseDetail detail = null;
        	ColSuperviseDetail detail = null;
        	//detail = edocSuperviseDetailDao.findEdocSuperviseDetailBySummaryId(summaryId);
        	detail = colSuperviseManager.getSupervise(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal(), summary.getId());
        	String sNames = supervisorNames;
        	
        	if(null==detail){
        		bool = true;  //第一次督办
        		detail = new ColSuperviseDetail();
        		detail.setIdIfNew();
        		detail.setRemindMode(Integer.valueOf(remindMode));
        		detail.setCreateDate(new Date(System.currentTimeMillis()));
        		detail.setSenderId(user.getId());
        		detail.setCount(0);
        		detail.setDescription(null);
        		detail.setEntityId(summary.getId());
        		sNames = supervisorNames;
        	}else{
            	colSuperviseDetailDao.delete(ColSupervisor.class, new Object[][]{{"superviseId", detail.getId()}});
        			String scheduleProp = detail.getScheduleProp();
        			if(null!=scheduleProp){
        			String[] scheduleProp_Array = scheduleProp.split("\\|");
        			if(null!=scheduleProp_Array && scheduleProp_Array.length > 0){
        				try{
        					Scheduler sched = QuartzListener.getScheduler();
        					sched.deleteJob(scheduleProp_Array[0], scheduleProp_Array[1]); //public boolean deleteJob(String jobName, String groupName);
        					//删除队列中的消息提醒，下面将会重新生成。
        				}catch(Exception e){
        					log.error("删除督办期限提醒消息的计时器出错");
        				}
        			}
        		}
        	}
        	detail.setAwakeDate(Datetimes.parse(superviseDate,Datetimes.datetimeWithoutSecondStyle));
    		detail.setSupervisors(sNames);

    		Set<ColSupervisor> supervisors = null;
    		StringBuffer newSupervisorIds = new StringBuffer("");
            if(bool){
            	supervisors = new HashSet<ColSupervisor>();
            }else{
            	supervisors = detail.getColSupervisors();
            	/*
            	supervisors = new HashSet<EdocSupervisor>();
            	List supervisorList = this.getEdocSupervisorBySuperviseId(detail.getId());
            	supervisors.addAll(supervisorList);
            	*/
            }
            
            if(supervisorMemberId.endsWith(",")){
            	supervisorMemberId = supervisorMemberId.substring(0, supervisorMemberId.length()-1);
            }
            String[] spArray = supervisorMemberId.split(",");;
        	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
        	StringBuffer names = new StringBuffer("");
        	V3xOrgMember member = null;
        	for(String s:spArray){
        		ColSupervisor supervisor = new ColSupervisor();
        		supervisor.setIdIfNew();
        		supervisor.setSuperviseId(detail.getId());
        		supervisor.setSupervisorId(Long.valueOf(s));
        		supervisor.setPermission(com.seeyon.v3x.edoc.util.Constants.EDOC_SUPERVISOR_PERMISSION_CHANGE);
        		supervisors.add(supervisor);
    			newSupervisorIds.append(s);
    			newSupervisorIds.append(",");
    			
        		MessageReceiver receiver1 = new MessageReceiver(detail.getId(), supervisor.getSupervisorId(),"message.link.edoc.supervise.detail",detail.getEntityId());
        		receivers.add(receiver1);
    			try{
    				member = this.orgManager.getMemberById(Long.valueOf(s));
                if(member!=null){
                	names.append(member.getName());
                	names.append(",");
                }	
    			}catch(Exception e){
    				log.error("得到督办人实体错误 : ",e);
    				continue;
    			}
        	}
        	
        	/*
        	if(!bool){
        		try{
        		edocSupervisorDao.deleteSupervisorsByDetailId(detail.getId());
        		}catch(Exception e){
        			log.error("删除督办记录下的督办人出错!",e);
        		}
        	}
        	*/
        	String fNames = "";
        	if(names.toString().endsWith(",")){
        		fNames = names.substring(0, names.length()-1);
        	}
        	detail.setColSupervisors(supervisors);
        	detail.setSupervisors(fNames);
        	
        	try {
    	    	Scheduler sched = QuartzListener.getScheduler();
    	        Long jobId = UUIDLong.longUUID();
    	        String jobName = jobId.toString();
    	        Long groupId = UUIDLong.longUUID();
    	        String groupName = groupId.toString();
    	        Long triggerId = UUIDLong.longUUID();
    	        String triggerName = triggerId.toString();
    	
    	        SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, detail.getAwakeDate());
    	        JobDataMap datamap = new JobDataMap();
    			datamap.putAsString("colSuperviseId", detail.getId());
    			datamap.putAsString("senderId", user.getId());
    			datamap.put("supervisorMemberId",supervisorMemberId.toString());
    			datamap.put("subject", summary.getSubject());
    			
    			JobDetail job = new JobDetail(jobName, groupName, TerminateColSupervise.class);
                job.setJobDataMap(datamap);
                sched.scheduleJob(job, trigger);   
                
                String scheduleProp = jobName + "|" + groupName;
                detail.setScheduleProp(scheduleProp);
        	}catch(SchedulerException e) {
        		log.error(e);
        	}        	
            try{
                int edocType = summary.getEdocType();
                ApplicationCategoryEnum app = null;
             
                	if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocSend;
                	}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocRec;               		
                	}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
                		app = ApplicationCategoryEnum.edocSign;               		
                	}else{
                		app = ApplicationCategoryEnum.edoc; 
                	}
        		userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.hasten",summary.getSubject(),user.getName(),app.ordinal()), app, user.getId(), receivers);
        	}catch(Exception e){
        		log.error("给督办人发消息失败!");
        	}
        	detail.setTitle(title);
    		detail.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_SUPERVISE_PROGRESSING);
    		detail.setEntityType(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal());
        	
        	if(bool){
        		colSuperviseDetailDao.save(detail);
         	}else{          	
        		colSuperviseDetailDao.update(detail);
       	}
        }	
	}
	
	public void superviseForSentList(String remindMode,String supervisorMemberId,String supervisorNames,String superviseDate,Long summaryId,String title){
		User user = CurrentUser.get();
		EdocSummary summary = edocSummaryManager.findById(summaryId);

        if(null!=supervisorMemberId && !"".equals(supervisorMemberId)){
        	
        	boolean bool = false;  //处理时是否为第一次督办
        	//EdocSuperviseDetail detail = null;
        	ColSuperviseDetail detail = null;
        	//detail = edocSuperviseDetailDao.findEdocSuperviseDetailBySummaryId(summaryId);
        	detail = colSuperviseManager.getSupervise(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal(), summaryId);
        	String sNames = supervisorNames;
        	List<MessageReceiver> deleteReceivers = new ArrayList<MessageReceiver>();
        	String orgMemberIds = "";
        	if(null==detail){
        		bool = true;  //第一次督办
        		detail = new ColSuperviseDetail();
        		detail.setIdIfNew();
        		detail.setRemindMode(Integer.valueOf(remindMode));
        		detail.setCreateDate(new Date(System.currentTimeMillis()));
        		detail.setSenderId(user.getId());
        		detail.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_SUPERVISE_PROGRESSING);
        		detail.setCount(0);
        		detail.setDescription(null);
        		detail.setEntityId(summaryId);
        		sNames = supervisorNames;
        	}else{
        		Set<ColSupervisor> delSupervisors = detail.getColSupervisors();
            	Iterator it = delSupervisors.iterator();
            	while(it.hasNext()){
					ColSupervisor sor = (ColSupervisor)it.next();
					orgMemberIds += sor.getSupervisorId().toString() + ",";
					boolean boo =supervisorMemberId.contains(sor.getSupervisorId().toString());
            		if(!boo){
            			MessageReceiver receiver = new MessageReceiver(detail.getId(), Long.valueOf(sor.getSupervisorId()));//因为是删除，不采取链接的方式
            			deleteReceivers.add(receiver);   
            			continue;
            		}
            	}
            	
            	colSuperviseDetailDao.delete(ColSupervisor.class, new Object[][]{{"superviseId", detail.getId()}});
        			String scheduleProp = detail.getScheduleProp();
        			if(null!=scheduleProp){
        			String[] scheduleProp_Array = scheduleProp.split("\\|");
        			if(null!=scheduleProp_Array && scheduleProp_Array.length > 0){
        				try{
        					Scheduler sched = QuartzListener.getScheduler();
        					sched.deleteJob(scheduleProp_Array[0], scheduleProp_Array[1]); //public boolean deleteJob(String jobName, String groupName);
        					//删除队列中的消息提醒，下面将会重新生成。
        				}catch(Exception e){
        					log.error("删除督办期限提醒消息的计时器出错");
        				}
        			}
        		}
        	}
        	detail.setAwakeDate(Datetimes.parseDatetimeWithoutSecond(superviseDate));
    		detail.setSupervisors(sNames);

    		Set<ColSupervisor> supervisors = null;
    		StringBuffer newSupervisorIds = new StringBuffer("");
    		String superIds = "";
            if(bool){
            	supervisors = new HashSet<ColSupervisor>();
            }else{
            	
            	supervisors = detail.getColSupervisors();
            	
            	Iterator it = supervisors.iterator();
            	try{
            	while(it.hasNext()){
					ColSupervisor sor = (ColSupervisor)it.next();            		
					boolean boo =supervisorMemberId.contains(sor.getSupervisorId().toString());
            		if(!boo){
            			MessageReceiver receiver = new MessageReceiver(detail.getId(), Long.valueOf(sor.getSupervisorId()));//因为是删除，不采取链接的方式
            			//deleteReceivers.add(receiver);
            			//it.remove();
            			//continue;
            		}
            		superIds += sor.getSupervisorId();
            		superIds +=",";
            	}
    			
    			}catch(Exception e){
    				log.error("删除督办人出错",e);
    			}
            	/*
            	supervisors = new HashSet<EdocSupervisor>();
            	List supervisorList = this.getEdocSupervisorBySuperviseId(detail.getId());
            	supervisors.addAll(supervisorList);
            	
            	*/
            }
            
            if(supervisorMemberId.endsWith(",")){
            	supervisorMemberId = supervisorMemberId.substring(0, supervisorMemberId.length()-1);
            }
            String[] spArray = supervisorMemberId.split(",");;
        	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
        	StringBuffer names = new StringBuffer("");
        	V3xOrgMember member = null;
        	for(String s:spArray){
        		/*
        		if(!bool && superIds.contains(s)){
        			continue;
        		}*/
        		ColSupervisor supervisor = new ColSupervisor();
        		supervisor.setIdIfNew();
        		supervisor.setSuperviseId(detail.getId());
        		supervisor.setSupervisorId(Long.valueOf(s));
        		supervisor.setPermission(com.seeyon.v3x.edoc.util.Constants.EDOC_SUPERVISOR_PERMISSION_CHANGE);
        		supervisors.add(supervisor);
    			newSupervisorIds.append(s);
    			newSupervisorIds.append(",");   			
    			if(!orgMemberIds.contains(s)){
    				MessageReceiver receiverA = new MessageReceiver(detail.getId(), supervisor.getSupervisorId(),"message.link.edoc.supervise.detail",detail.getEntityId());
        			receivers.add(receiverA);
    			}
    			try{
    				member = this.orgManager.getMemberById(Long.valueOf(s));
                if(member!=null){
                	names.append(member.getName());
                	names.append(",");
                }	
    			}catch(Exception e){
    				log.error("得到督办人实体错误 : ",e);
    				continue;
    			} 
        	}
        	
        	/*
        	if(!bool){
        		try{
        		edocSupervisorDao.deleteSupervisorsByDetailId(detail.getId());
        		}catch(Exception e){
        			log.error("删除督办记录下的督办人出错!",e);
        		}
        	}
        	*/
        	String fNames = "";
        	if(names.toString().endsWith(",")){
        		fNames = names.substring(0, names.length()-1);
        	}
        	detail.setColSupervisors(supervisors);
        	detail.setSupervisors(fNames);
        	
        	try {
    	    	Scheduler sched = QuartzListener.getScheduler();
    	        Long jobId = UUIDLong.longUUID();
    	        String jobName = jobId.toString();
    	        Long groupId = UUIDLong.longUUID();
    	        String groupName = groupId.toString();
    	        Long triggerId = UUIDLong.longUUID();
    	        String triggerName = triggerId.toString();
    	
    	        SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, detail.getAwakeDate());
    	        JobDataMap datamap = new JobDataMap();
    			datamap.putAsString("colSuperviseId", detail.getId());
    			datamap.putAsString("senderId", user.getId());
    			datamap.put("supervisorMemberId",supervisorMemberId.toString());
    			datamap.put("subject", summary.getSubject());
    			
    			JobDetail job = new JobDetail(jobName, groupName, TerminateColSupervise.class);
                job.setJobDataMap(datamap);
                sched.scheduleJob(job, trigger);   
                
                String scheduleProp = jobName + "|" + groupName;
                detail.setScheduleProp(scheduleProp);
        	}catch(SchedulerException e) {
        		log.error(e);
        	}
        	
        	/*
        	try{
    			Scheduler sched = QuartzListener.getScheduler();
                Long jobId = UUIDLong.longUUID();
                String jobName = jobId.toString();
                Long groupId = UUIDLong.longUUID();
                String groupName = groupId.toString();
                Long triggerId = UUIDLong.longUUID();
                String triggerName = triggerId.toString();
                
                Date endDate = detail.getAwakeDate();
                if(endDate.before(new Date())){
                	endDate = Datetimes.addSecond(new Date(), 5);
                }else{
                	endDate = Datetimes.addHour(endDate, -16);
                }
                SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, endDate);      
                
    			JobDataMap datamap = new JobDataMap();
    			datamap.putAsString("edocSuperviseId", detail.getId());    			
    			
    			
    			//--根据detail获取所有督办人的id,组成字符串传到后台
    			if(newSupervisorIds.toString().endsWith(",")){
    				newSupervisorIds.deleteCharAt(newSupervisorIds.length()-1);
    			}
    			datamap.put("supervisorMemberId", newSupervisorIds.toString());
    			//--
    			datamap.put("subject", summary.getSubject());

    			
                JobDetail job = new JobDetail(jobName, groupName, TerminateEdocSupervise.class);
                job.setJobDataMap(datamap);
                
                sched.scheduleJob(job, trigger);   
                
                String scheduleProp = jobName + "|" + groupName;
                
                detail.setScheduleProp(scheduleProp);
        	}catch(Exception e){
        		log.error("公文期限设置消息提醒失败!");
        	}
               */
        	
            try{
                int edocType = summary.getEdocType();
                ApplicationCategoryEnum app = null;
             
                	if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocSend;
                	}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
                		app = ApplicationCategoryEnum.edocRec;               		
                	}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
                		app = ApplicationCategoryEnum.edocSign;               		
                	}else{
                		app = ApplicationCategoryEnum.edoc; 
                	}
                //{1} 请你督办{2,choice,1#协同|4#公文|19#发文|20#收文|21#签报}《{0}》
        		userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.hasten",summary.getSubject(),user.getName(),app.ordinal()), app, user.getId(), receivers);
        		if(deleteReceivers.size()!=0){
        			//{2,choice,1#协同|4#公文|19#发文|20#收文|21#签报}《{0}》的督办权限已被 {1} 撤销
        			userMessageManager.sendSystemMessage(new MessageContent("edoc.supervise.delete",summary.getSubject(),user.getName(),app.ordinal()), app, user.getId(), deleteReceivers);//给被删除的人发消息
        		}
        	}catch(Exception e){
        		log.error("给督办人发消息失败!");
        	}
        	detail.setTitle(title);
    		detail.setEntityType(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal());
        	if(bool){
            	colSuperviseDetailDao.save(detail);
							    				
        	}else{
            	colSuperviseDetailDao.update(detail);
			}
        }	
	}
	
	public boolean ajaxCheckIsSummaryOver(Long summaryId){
		
		//ResourceBundle r = null;
		//String bundleName = "com.seeyon.v3x.edoc.resources.i18n.EdocResource"; //指定国际化资源文件,默认为公文
		EdocSummary edocSummary = edocSummaryDao.get(summaryId);
		if(null!=edocSummary){
			if(edocSummary.getFinished()){
			return true;
		}
	}
		return false;
	}

	public List<EdocSuperviseModel> queryByCondition(int status , String condition, String textfield, String textfield1){

        User user = CurrentUser.get();
        long user_id = user.getId();
        
        List result = new ArrayList();
        
/*        String hql = "select de from " + ColSuperviseDetail.class.getName() + " as de,"  + EdocSummary.class.getName() + " as summary," + V3xOrgMember.class.getName() + " as mem";
		hql += " left join de.colSupervisors as su where su.supervisorId = ? ";		
		hql += " and de.entityId = summary.id ";
		hql += " and de.senderId = mem.id ";
		hql += " and de.entityType = ? and de.status= ? ";*/
        String hql = "select de,summary,aff.bodyType from "+ EdocSummary.class.getName() + " as summary, "+Affair.class.getName()+" as aff," + ColSuperviseDetail.class.getName() + " as de"  ;
		hql += " left join de.colSupervisors as su where su.supervisorId = ?  and aff.objectId = summary.id and aff.state=2 ";		
		hql += " and de.entityId = summary.id ";
		hql += " and de.entityType = ? and de.status= ? ";
		int entityType = com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal();
		/*
		paramValue = SQLWildcardUtil.escape(status + "");
		paramName = "status";
		paramNameList.add(paramName);
		parameterMap.put(paramName, paramValue);

		paramValue = SQLWildcardUtil.escape(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal() + "");
		paramName = "entityType";
		paramNameList.add(paramName);
		parameterMap.put(paramName, paramValue);		
		*/
		if(Strings.isBlank(condition)) condition = "";
        if (condition.equals("subject")) {
        	hql += " and summary.subject like ? order by de.createDate desc";	
			result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status, "%"+textfield+"%");        	
        }else if (condition.equals("supervisors")) {  
            hql += " and de.supervisors like ? order by de.createDate desc";
			result = colSuperviseDetailDao.find(hql.toString(),  user_id, entityType, status, "%"+textfield+"%");
        }else if(condition.equals("docMark")){
        	hql += " and (summary.docMark like ? or summary.docMark2 like ?) order by de.createDate desc";
        	result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status, "%"+SQLWildcardUtil.escape(textfield.trim())+"%","%"+SQLWildcardUtil.escape(textfield.trim())+"%");        	 
        }else if(condition.equals("docInMark")){
        	hql += " and (summary.serialNo like ?) order by de.createDate desc";
        	result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status, "%"+SQLWildcardUtil.escape(textfield.trim())+"%");        	 
        }else if (condition.equals("createPerson")) {  
        	String hql2 = "select de,summary,aff.bodyType from "+ V3xOrgMember.class.getName() + " as mem ," +Affair.class.getName()+" as aff,"  + EdocSummary.class.getName() + " as summary ,"  + ColSuperviseDetail.class.getName() + " as de";
    		hql2 += " left join de.colSupervisors as su where su.supervisorId = ? ";		
    		hql2 += " and de.entityId = summary.id ";
    		hql2 += " and aff.objectId = summary.id and aff.state=2 ";
    		hql2 += " and summary.startUserId = mem.id ";
    		hql2 += " and de.entityType = ? and de.status= ? ";
        	hql2 += " and mem.name like ? order by de.createDate desc";
			result = colSuperviseDetailDao.find(hql2.toString(),  user_id, entityType, status, "%"+textfield+"%");
        }else if (condition.equals("awakeDate")) {
        	if (StringUtils.isNotBlank(textfield)) {
				hql += " and de.awakeDate >= ? ";
			}
			if (StringUtils.isNotBlank(textfield1)) {
				hql += " and de.awakeDate <= ? ";
			}
			if(StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id, entityType, status, Datetimes.getTodayFirstTime(textfield));
			}else if(!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayLastTime(textfield1));
			}else if(StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayFirstTime(textfield), Datetimes.getTodayLastTime(textfield1));
			}else{
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status);
			}        	
        }else if (condition.equals("createDate")) {
        	if (StringUtils.isNotBlank(textfield)) {
				hql += " and de.createDate >= ? ";
			}
			if (StringUtils.isNotBlank(textfield1)) {
				hql += " and de.createDate <= ? ";
			}
			if(StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id, entityType, status, Datetimes.getTodayFirstTime(textfield));
			}else if(!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayLastTime(textfield1));
			}else if(StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayFirstTime(textfield), Datetimes.getTodayLastTime(textfield1));
			}else{
				hql += " order by de.createDate desc";
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status);
			}
        }else{
        	hql += " order by de.createDate desc";
        	result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status);
        }
        


		/*
		paramValue = SQLWildcardUtil.escape(user.getId() + "");
		paramName = "supervisorId";
		paramNameList.add(paramName);
		parameterMap.put(paramName, paramValue);	
		*/
        
		List<EdocSuperviseModel> modelList = new ArrayList<EdocSuperviseModel>();
		EdocManager edocManager= (EdocManager)ApplicationContextHolder.getBean("edocManager");
		
		int caseId = 0;
		String caseLogXML = "";
		String caseProcessXML = "";
		String caseWorkItemLogXML = "";
		Boolean hasWorkflow = false;
		String process_desc_by = "";
		String appName = null;
		
		for(int i=0;i<result.size();i++){
			Object o = result.get(i);
			Object[] arr = (Object[]) o;
			ColSuperviseDetail detail = (ColSuperviseDetail)arr[0];
			EdocSummary summary = (EdocSummary)arr[1];
			EdocSuperviseModel model = new EdocSuperviseModel();
			model.setBodyType((String)arr[2]);
			model.setId(detail.getId());
			model.setDescription(detail.getDescription());
			model.setEndDate(detail.getAwakeDate());
			model.setStartDate(detail.getCreateDate());
			model.setBodyType(summary.getFirstBody().getContentType());
			Long edocId = detail.getEntityId();
			model.setEdocId(edocId);
			if(null==summary)continue;
			model.setTitle(summary.getSubject());
			/*
			String secretLevel = "";
			if(null!=summary.getSecretLevel() && !"".equals(summary.getSecretLevel())){
				secretLevel = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_secret_level, summary.getSecretLevel());			
			}*/
			model.setSecretLevel(summary.getSecretLevel());
			model.setCount(detail.getCount());
			model.setRemindModel(detail.getRemindMode());
			model.setSupervisor(detail.getSupervisors());
			model.setContent(Strings.toHTML(detail.getDescription()));
			model.setStatus(detail.getStatus());
			model.setDeadline(summary.getDeadline());
			Long senderId = summary.getStartUserId();
			
			Date startDate = summary.getCreateTime();
			Date finishDate = summary.getCompleteTime();
			Date now = new Date(System.currentTimeMillis());
			if(summary.getDeadline() != null && summary.getDeadline() != 0){
				Long deadline = summary.getDeadline()*60000;
				if(finishDate == null){
					if((now.getTime()-startDate.getTime()) > deadline){
						model.setWorkflowTimeout(true);
					}
				}else{
					Long expendTime = summary.getCompleteTime().getTime() - summary.getCreateTime().getTime();
					if((deadline-expendTime) < 0){
						model.setWorkflowTimeout(true);
					}
				}
			}
			if(null!=senderId){
				try{
	            V3xOrgMember member = this.orgManager.getMemberById(senderId);
	            if(member!=null){
	    			model.setSender(member.getName());	            	
	            }else{
	    			model.setSender("");		            	
	            }
				}catch(Exception e){};
			}
			/*
			try{
			V3xOrgMember member = orgManager.getMemberById(detail.getSenderId());
			model.setSender(member.getName());
			}catch(Exception e){	
			}*/
			/*
			String doc_type = summary.getDocType();
			MetadataItem metaDataItem = null;
			List<MetadataItem> metaItem = null;
			String name = "";
			metaItem  =  metadataManager.getMetadataItems("edoc_doc_type");
			String edoc_doc_type = "";
			if(null!=summary.getDocType() && !"".equals(summary.getDocType())){
				edoc_doc_type = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_doc_type, summary.getDocType());				
			}
			model.setEdocType(edoc_doc_type);
			*/
			model.setEdocType(summary.getDocType());
			int edoc_type = summary.getEdocType();
			String edocType = "";
			if(edoc_type==0){
				edocType = "edoc.docmark.inner.send";
			}else if(edoc_type==1){
				edocType = "edoc.docmark.inner.receive";
			}else if(edoc_type==2){
				edocType = "edoc.doctype.endorsement";
			}
			appName = EdocEnum.getEdocAppName(edoc_type);
			
			if(null == summary.getCaseId())continue;
			
//			caseId = summary.getCaseId();
//			try{
//				caseLogXML = edocManager.getCaseLogXML(caseId);
//				caseProcessXML = edocManager.getCaseProcessXML(caseId);
//				caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
//			}catch(Exception e){
//			}
//			model.setCaseLogXML(Strings.toHTML(caseLogXML));
//			model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
//			model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));
//			if (StringUtils.isNotEmpty(caseProcessXML)) {
//				hasWorkflow = true;
//				process_desc_by = FlowData.DESC_BY_XML;		
//			}
			
			if(null!=detail.getAwakeDate() && now.after(detail.getAwakeDate())){
				model.setIsRed(true);
			}
			
//			model.setHasWorkflow(hasWorkflow);
//			model.setProcess_desc_by(process_desc_by);
			model.setAppName(appName);
			model.setCaseId(summary.getCaseId());
	        int actorId=EdocEnum.getStartAccessId(edoc_type);
	        model.setActorId(actorId);
	        
	        model.setHasAttachment(summary.isHasAttachments());
	        model.setUrgentLevel(summary.getUrgentLevel());
	        
			modelList.add(model);
			}
		return modelList;
		}
	
	////成发集团项目 重写queryByCondition
	public List<EdocSuperviseModel> queryByCondition(int status , String condition, String textfield, String textfield1,Integer secretLevel){

        User user = CurrentUser.get();
        long user_id = user.getId();
        
        List result = new ArrayList();
        
/*        String hql = "select de from " + ColSuperviseDetail.class.getName() + " as de,"  + EdocSummary.class.getName() + " as summary," + V3xOrgMember.class.getName() + " as mem";
		hql += " left join de.colSupervisors as su where su.supervisorId = ? ";		
		hql += " and de.entityId = summary.id ";
		hql += " and de.senderId = mem.id ";
		hql += " and de.entityType = ? and de.status= ? ";*/
        String hql = "select de,summary,aff.bodyType from "+ EdocSummary.class.getName() + " as summary, "+Affair.class.getName()+" as aff," + ColSuperviseDetail.class.getName() + " as de"  ;
		hql += " left join de.colSupervisors as su where su.supervisorId = ?  and aff.objectId = summary.id and aff.state=2 ";		
		hql += " and de.entityId = summary.id ";
		hql += " and de.entityType = ? and de.status= ? ";
		int entityType = com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal();
		/*
		paramValue = SQLWildcardUtil.escape(status + "");
		paramName = "status";
		paramNameList.add(paramName);
		parameterMap.put(paramName, paramValue);

		paramValue = SQLWildcardUtil.escape(com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal() + "");
		paramName = "entityType";
		paramNameList.add(paramName);
		parameterMap.put(paramName, paramValue);		
		*/
		//成发集团项目
		if(secretLevel != null){
			hql += " and (summary.edocSecretLevel <= ? or summary.edocSecretLevel is null)";
		}
		
		if(Strings.isBlank(condition)) condition = "";
        if (condition.equals("subject")) {
        	hql += " and summary.subject like ? order by de.createDate desc";	
        	if(secretLevel != null){
        		result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status,secretLevel,"%"+textfield+"%"); //成发集团项目
    		}else{
			result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status, "%"+textfield+"%"); 
    		}
        }else if (condition.equals("supervisors")) {  
            hql += " and de.supervisors like ? order by de.createDate desc";
            if(secretLevel != null){
            	result = colSuperviseDetailDao.find(hql.toString(),  user_id, entityType, status,secretLevel ,"%"+textfield+"%");//成发集团项目
    		}else{
			result = colSuperviseDetailDao.find(hql.toString(),  user_id, entityType, status, "%"+textfield+"%");
    		}
        }else if(condition.equals("docMark")){
        	hql += " and (summary.docMark like ? or summary.docMark2 like ?) order by de.createDate desc";
        	 if(secretLevel != null){
        		 result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status,secretLevel ,"%"+SQLWildcardUtil.escape(textfield.trim())+"%","%"+SQLWildcardUtil.escape(textfield.trim())+"%");//成发集团项目    
     		}else{
        	result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status, "%"+SQLWildcardUtil.escape(textfield.trim())+"%","%"+SQLWildcardUtil.escape(textfield.trim())+"%");    
     		}
        }else if(condition.equals("docInMark")){
        	hql += " and (summary.serialNo like ?) order by de.createDate desc";
        	 if(secretLevel != null){
        		 result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status,secretLevel ,"%"+SQLWildcardUtil.escape(textfield.trim())+"%");//成发集团项目     
     		}else{
        	result = colSuperviseDetailDao.find(hql.toString(),  user_id ,entityType, status, "%"+SQLWildcardUtil.escape(textfield.trim())+"%"); 
     		}
        }else if (condition.equals("createPerson")) {  
        	String hql2 = "select de,summary,aff.bodyType from "+ V3xOrgMember.class.getName() + " as mem ," +Affair.class.getName()+" as aff,"  + EdocSummary.class.getName() + " as summary ,"  + ColSuperviseDetail.class.getName() + " as de";
    		hql2 += " left join de.colSupervisors as su where su.supervisorId = ? ";		
    		hql2 += " and de.entityId = summary.id ";
    		hql2 += " and aff.objectId = summary.id and aff.state=2 ";
    		hql2 += " and summary.startUserId = mem.id ";
    		hql2 += " and de.entityType = ? and de.status= ? ";
            //成发集团项目
            if(secretLevel != null){
                hql2 += " and (summary.edocSecretLevel <= ? or summary.edocSecretLevel is null)";
            }
        	hql2 += " and mem.name like ? order by de.createDate desc";
        	if(secretLevel != null){
        		result = colSuperviseDetailDao.find(hql2.toString(),  user_id, entityType, status,secretLevel ,"%"+textfield+"%");//成发集团项目
    		}else{
			result = colSuperviseDetailDao.find(hql2.toString(),  user_id, entityType, status, "%"+textfield+"%");
    		}
        }else if (condition.equals("awakeDate")) {
        	if (StringUtils.isNotBlank(textfield)) {
				hql += " and de.awakeDate >= ? ";
			}
			if (StringUtils.isNotBlank(textfield1)) {
				hql += " and de.awakeDate <= ? ";
			}
			if(StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id, entityType, status,secretLevel ,Datetimes.getTodayFirstTime(textfield));//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id, entityType, status, Datetimes.getTodayFirstTime(textfield));
	    		}
			}else if(!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel, Datetimes.getTodayLastTime(textfield1));//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayLastTime(textfield1));
	    		}
			}else if(StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel, Datetimes.getTodayFirstTime(textfield), Datetimes.getTodayLastTime(textfield1));//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayFirstTime(textfield), Datetimes.getTodayLastTime(textfield1));
	    		}
			}else{
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel);//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status);
	    		}
			}        	
        }else if (condition.equals("createDate")) {
        	if (StringUtils.isNotBlank(textfield)) {
				hql += " and de.createDate >= ? ";
			}
			if (StringUtils.isNotBlank(textfield1)) {
				hql += " and de.createDate <= ? ";
			}
			if(StringUtils.isNotBlank(textfield) && !StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id, entityType, status,secretLevel ,Datetimes.getTodayFirstTime(textfield));//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id, entityType, status, Datetimes.getTodayFirstTime(textfield));
	    		}
			}else if(!StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel ,Datetimes.getTodayLastTime(textfield1));//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayLastTime(textfield1));
	    		}
			}else if(StringUtils.isNotBlank(textfield) && StringUtils.isNotBlank(textfield1)){
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel, Datetimes.getTodayFirstTime(textfield), Datetimes.getTodayLastTime(textfield1));//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status, Datetimes.getTodayFirstTime(textfield), Datetimes.getTodayLastTime(textfield1));
	    		}
			}else{
				hql += " order by de.createDate desc";
				if(secretLevel != null){
					result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel);//成发集团项目
	    		}else{
				result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status);
	    		}
			}
        }else{
        	hql += " order by de.createDate desc";
        	if(secretLevel != null){
        		result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status,secretLevel);//成发集团项目
    		}else{
        	result = colSuperviseDetailDao.find(hql.toString(), user_id , entityType, status);
    		}
        }
        


		/*
		paramValue = SQLWildcardUtil.escape(user.getId() + "");
		paramName = "supervisorId";
		paramNameList.add(paramName);
		parameterMap.put(paramName, paramValue);	
		*/
        
		List<EdocSuperviseModel> modelList = new ArrayList<EdocSuperviseModel>();
		EdocManager edocManager= (EdocManager)ApplicationContextHolder.getBean("edocManager");
		
		int caseId = 0;
		String caseLogXML = "";
		String caseProcessXML = "";
		String caseWorkItemLogXML = "";
		Boolean hasWorkflow = false;
		String process_desc_by = "";
		String appName = null;
		
		for(int i=0;i<result.size();i++){
			Object o = result.get(i);
			Object[] arr = (Object[]) o;
			ColSuperviseDetail detail = (ColSuperviseDetail)arr[0];
			EdocSummary summary = (EdocSummary)arr[1];
			EdocSuperviseModel model = new EdocSuperviseModel();
			model.setBodyType((String)arr[2]);
			model.setId(detail.getId());
			model.setDescription(detail.getDescription());
			model.setEndDate(detail.getAwakeDate());
			model.setStartDate(detail.getCreateDate());
			model.setBodyType(summary.getFirstBody().getContentType());
			Long edocId = detail.getEntityId();
			model.setEdocId(edocId);
			if(null==summary)continue;
			model.setTitle(summary.getSubject());
			/*
			String secretLevel = "";
			if(null!=summary.getSecretLevel() && !"".equals(summary.getSecretLevel())){
				secretLevel = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_secret_level, summary.getSecretLevel());			
			}*/
			model.setSecretLevel(summary.getSecretLevel());
			model.setCount(detail.getCount());
			model.setRemindModel(detail.getRemindMode());
			model.setSupervisor(detail.getSupervisors());
			model.setContent(Strings.toHTML(detail.getDescription()));
			model.setStatus(detail.getStatus());
			model.setDeadline(summary.getDeadline());
			Long senderId = summary.getStartUserId();
			
			Date startDate = summary.getCreateTime();
			Date finishDate = summary.getCompleteTime();
			Date now = new Date(System.currentTimeMillis());
			if(summary.getDeadline() != null && summary.getDeadline() != 0){
				Long deadline = summary.getDeadline()*60000;
				if(finishDate == null){
					if((now.getTime()-startDate.getTime()) > deadline){
						model.setWorkflowTimeout(true);
					}
				}else{
					Long expendTime = summary.getCompleteTime().getTime() - summary.getCreateTime().getTime();
					if((deadline-expendTime) < 0){
						model.setWorkflowTimeout(true);
					}
				}
			}
			if(null!=senderId){
				try{
	            V3xOrgMember member = this.orgManager.getMemberById(senderId);
	            if(member!=null){
	    			model.setSender(member.getName());	            	
	            }else{
	    			model.setSender("");		            	
	            }
				}catch(Exception e){};
			}
			/*
			try{
			V3xOrgMember member = orgManager.getMemberById(detail.getSenderId());
			model.setSender(member.getName());
			}catch(Exception e){	
			}*/
			/*
			String doc_type = summary.getDocType();
			MetadataItem metaDataItem = null;
			List<MetadataItem> metaItem = null;
			String name = "";
			metaItem  =  metadataManager.getMetadataItems("edoc_doc_type");
			String edoc_doc_type = "";
			if(null!=summary.getDocType() && !"".equals(summary.getDocType())){
				edoc_doc_type = metadataManager.getMetadataItemLabel(MetadataNameEnum.edoc_doc_type, summary.getDocType());				
			}
			model.setEdocType(edoc_doc_type);
			*/
			model.setEdocType(summary.getDocType());
			int edoc_type = summary.getEdocType();
			String edocType = "";
			if(edoc_type==0){
				edocType = "edoc.docmark.inner.send";
			}else if(edoc_type==1){
				edocType = "edoc.docmark.inner.receive";
			}else if(edoc_type==2){
				edocType = "edoc.doctype.endorsement";
			}
			appName = EdocEnum.getEdocAppName(edoc_type);
			
			if(null == summary.getCaseId())continue;
			
//			caseId = summary.getCaseId();
//			try{
//				caseLogXML = edocManager.getCaseLogXML(caseId);
//				caseProcessXML = edocManager.getCaseProcessXML(caseId);
//				caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
//			}catch(Exception e){
//			}
//			model.setCaseLogXML(Strings.toHTML(caseLogXML));
//			model.setCaseProcessXML(StringEscapeUtils.escapeHtml(caseProcessXML));
//			model.setCaseWorkItemLogXML(StringEscapeUtils.escapeHtml(caseWorkItemLogXML));
//			if (StringUtils.isNotEmpty(caseProcessXML)) {
//				hasWorkflow = true;
//				process_desc_by = FlowData.DESC_BY_XML;		
//			}
			
			if(null!=detail.getAwakeDate() && now.after(detail.getAwakeDate())){
				model.setIsRed(true);
			}
			
//			model.setHasWorkflow(hasWorkflow);
//			model.setProcess_desc_by(process_desc_by);
			model.setAppName(appName);
			model.setCaseId(summary.getCaseId());
	        int actorId=EdocEnum.getStartAccessId(edoc_type);
	        model.setActorId(actorId);
	        
	        model.setHasAttachment(summary.isHasAttachments());
	        model.setUrgentLevel(summary.getUrgentLevel());
	        
			modelList.add(model);
			}
		return modelList;
		}
	public String checkColSupervisor(Long summaryId, Affair senderAffair){
        if(senderAffair == null || senderAffair.getState() == StateEnum.col_waitSend.key()){
        	String m = Constants.getString4CurrentUser("edoc.delete.non.supervise");
        	
        	return m;
        }
       
    	boolean currentUserIdSupervisor = false;
		ColSuperviseDetail detail = this.getSupervise(Constant.superviseType.edoc.ordinal(), summaryId);
		
		if(detail!=null){
			Long userId = CurrentUser.get().getId();
			Set<ColSupervisor> supervisors = detail.getColSupervisors();
			for (ColSupervisor supervisor : supervisors){
				if(userId.equals(supervisor.getSupervisorId())){
					currentUserIdSupervisor = true;
					break;
				}
	    	}
		}
		
		if(!currentUserIdSupervisor){
			return Constants.getString4CurrentUser("edoc.delete.non.supervise.nome");
		}
		
		return null;
	}
	/**
	 * Ajax前台调用。
	 * @param summaryId
	 * @return
	 */
	public String isSupervisorOfOneSummary(String summaryId){
		Long edocSummaryId=0L;
		if(Strings.isNotBlank(summaryId)) 
			edocSummaryId=Long.parseLong(summaryId);
		Affair affair = affairManager.getCollaborationSenderAffair(edocSummaryId);
		String m = checkColSupervisor(edocSummaryId, affair);
		if(m==null)
			return "1";
		return "0";
	}
	public ColSuperviseDetail getSupervise(int entityType,long entityId) {
		return (ColSuperviseDetail)colSuperviseDetailDao.getSupervise(entityType,entityId);
	}
	
	/**
	 * 查找未处理的督办事项
	 * @param supervisorId
	 * @param type
	 * @return
	 */
//public List<EdocSuperviseModel> findSuperviseListBySupervisorAndStatus(Long supervisorId,int status){
//		
//		List<EdocSuperviseModel> modelList = new ArrayList<EdocSuperviseModel>();
//		String hql2 = "select de.id as id,de.description as description,de.awakeDate as awakeDate,de.createDate as startDate,de.entityId as edocId,de.count as count,de.remindMode as remindMode ,de.supervisors as supervisors ,de.status as status ," +
//		"summary.subject as title,summary.secretLevel as secretLevel ,summary.deadline as deadline,summary.startUserId as senderId,summary.createTime as screateTime ,summary.completeTime as scompleteTime ,summary.docType as edocType ,summary.edocType as edoc_Type,summary.identifier as identifier,summary.urgentLevel as urgentLevel ";
//		hql2+="from "+ ColSuperviseDetail.class.getName() + " as de, " + EdocSummary.class.getName() + " as summary"  ;
//		hql2 += " left join de.colSupervisors as su where su.supervisorId = ? ";		
//		hql2 += " and de.entityId = summary.id ";
//		hql2 += " and de.entityType = ? and de.status= ? order by de.createDate desc";
//		List listobj = colSuperviseDetailDao.find(hql2.toString(), -1, -1,
//				null, supervisorId, com.seeyon.v3x.collaboration.Constant.superviseType.edoc.ordinal(),status);
//		String appName = null;
//		Date now = new Date(System.currentTimeMillis());
//		for (int i = 0; i < listobj.size(); i++) {
//			Object[] objs = (Object[]) listobj.get(i);
//			EdocSuperviseModel model = new EdocSuperviseModel();
//			for (int j = 0; j < objs.length - 2; j++) {
//
//				switch (j) {
//				case 0:
//					if(null!=objs[j])
//					{
//						model.setId(Long.parseLong(objs[j].toString()));
//					}
//					
//					break;
//				case 1:
//					if (objs[j] != null) {
//						model.setDescription(objs[j].toString());
//						model.setContent(Strings.toHTML(objs[j].toString()));
//					}
//					break;
//				case 2:
//					if (objs[j] != null) {
//						model.setEndDate((Date)objs[j]);
//						if(now.after((Date)objs[j]))
//								model.setIsRed(true);
//					}
//					break;
//				case 3:
//					if (objs[j] != null) {
//						model.setStartDate((Date)objs[j]);
//					}
//					break;
//				case 4:
//					if (objs[j] != null) {
//						model.setEdocId(Long.parseLong(objs[j].toString()));
//					}
//					break;
//				case 5:
//					if (objs[j] != null) {
//						model.setCount(Integer.parseInt(objs[j].toString()));
//					}
//					break;
//				case 6:
//					if (objs[j] != null) {
//						model.setRemindModel(Integer.parseInt(objs[j]
//								.toString()));
//					}
//					break;
//				case 7:
//					if (objs[j] != null) {
//						model.setSupervisor(objs[j].toString());
//					}
//					break;
//				case 8:
//					if (objs[j] != null) {
//						model.setStatus(Integer.parseInt(objs[j].toString()));
//					}
//					break;
//				case 9:
//					if (objs[j] != null) {
//						model.setTitle(objs[j].toString());
//					}
//					break;
//				case 10:
//					if (objs[j] != null) {
//						model.setSecretLevel(objs[j].toString());
//					}
//					break;
//				case 11:
//					if (objs[j] != null) {
//						model.setDeadline(Long.parseLong(objs[j].toString()));
//					}
//					break;
//				case 12:
//					if (objs[j] != null) {
//						try {
//							V3xOrgMember member = this.orgManager
//									.getMemberById(Long.parseLong(objs[j]
//											.toString()));
//							if (member != null) {
//								model.setSender(member.getName());
//							} else {
//								model.setSender("");
//							}
//						} catch (Exception e) {
//						}
//						;
//					}
//					break;
//				}
//			}
//			Timestamp startDate;
//			Timestamp finishDate;
//			if (model.getDeadline() != null && model.getDeadline() != 0) {
//				Long deadline = model.getDeadline() * 60000;
//				if (null == objs[14]||null == objs[14].toString()||objs[14].toString().equals("")) {
//					startDate = (Timestamp)objs[13];
//					if ((now.getTime() - startDate.getTime()) > deadline) {
//						model.setWorkflowTimeout(true);
//					}
//				} else {
//					Long expendTime;
//					if(objs[14].toString()!=null)
//					{
//						Timestamp ts14=(Timestamp)objs[14];
//						Timestamp ts13=(Timestamp)objs[13];
//						 expendTime = ts14.getTime()
//						- ts13.getTime();
//							if ((deadline - expendTime) < 0) {
//								model.setWorkflowTimeout(true);
//							}
//					}
//					else
//					{
//						startDate = (Timestamp)objs[13];
//						if ((now.getTime() - startDate.getTime()) > deadline) {
//							model.setWorkflowTimeout(true);
//						}
//					}
//
//				
//				}
//
//			}
//			if(model.getEndDate()!=null)
//			{
//				model.setIsRed(now.after(model.getEndDate()));
//			}
//			if(objs.length>14)
//			{
//				if(null!=objs[15])
//				{
//					model.setEdocType(objs[15].toString());
//				}
//			}
//			
//			if(objs.length>15)
//			{
//				if(null!=objs[16])
//				{
//					int edoc_type=Integer.parseInt(objs[16].toString());
//					String edocType = "";
//					if(edoc_type==0){
//						edocType = "edoc.docmark.inner.send";
//						appName = EdocEnum.getEdocAppName(edoc_type);
//						model.setAppName(appName);
//					}else if(edoc_type==1){
//						edocType = "edoc.docmark.inner.receive";
//						appName = EdocEnum.getEdocAppName(edoc_type);
//						model.setAppName(appName);
//					}else if(edoc_type==2){
//						edocType = "edoc.doctype.endorsement";
//						appName = EdocEnum.getEdocAppName(edoc_type);
//						model.setAppName(appName);
//					}
//
//				}
//			}
//			
//			if(objs.length > 16){
//				String identifier = objs[17].toString();
//                Boolean hasAtt = IdentifierUtil.lookupInner(identifier,0, '1');
//                model.setHasAttachment(hasAtt);
//			}
//			if(objs.length > 17){
//				model.setUrgentLevel((String)objs[18]);
//			}
//
//			modelList.add(model);
//		}			
//		return modelList;
//	}
	
	public String queryMarkList(int edocType,int status) {
		// 查询所有记录
		if (status != 0)
			edocType = status ;
		List<EdocSuperviseModel> modelList = this.queryByCondition(edocType,null,null,null);
		
		List<Long> ids = new ArrayList<Long>();
		if (modelList != null && modelList.size() > 0) {
			for (int i = 0 ; i < modelList.size() ; i ++) {
				EdocSuperviseModel model = modelList.get(i);
				ids.add(model.getEdocId());
			}
			String hql = "select a from EdocSummary a where a.id in (:ids)";
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("ids", ids);
			List<EdocSummary> list = this.searchManager.searchByHql(hql, paramMap,true);
			List<String> markList = this.getFormatEdocMark(list);
			return markList.get(0) + "::" + markList.get(1) ;
		} 
		return "";
	}
	
	/**
     * 按指定格式得到文号，如：
     * [ { value:"010", label:"Beijing北京"}, { value:"020", label:"guangzhou广州" }, { value:"021",label:"shanghai上海"} ];
     * @param list
     * @return
     */
	private List<String> getFormatEdocMark(List<EdocSummary> list) {
		
    	List<String> resultList = new ArrayList<String>();
    	
    	// 公文文号
        String edocMark = "" ;
        if (list != null && list.size() > 0) {
        	for (int i  = 0 ; i < list.size() ; i ++) {
        		EdocSummary model = list.get(i) ;
            	if (Strings.isNotBlank(model.getDocMark()))
            		edocMark += "{value:'"+i+"',label:'"+model.getDocMark()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getDocMark())) 
            		edocMark += ",";
            }
            if (edocMark.length() > 1 && ",".equals(edocMark.substring(edocMark.length()-1, edocMark.length())))
            	resultList.add("["+edocMark.substring(0, edocMark.length()-1)+"]");
            else
            	resultList.add("["+edocMark+"]");
            
            // 内部文号
            String edocInMark = "" ;
            for (int i  = 0 ; i < list.size() ; i ++) {
            	EdocSummary model = list.get(i) ;
            	if (Strings.isNotBlank(model.getSerialNo()))
            		edocInMark += "{value:'"+i+"',label:'"+model.getSerialNo()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getSerialNo())) 
            		edocInMark += ",";
            }
            if (edocInMark.length() >1 && ",".equals(edocInMark.substring(edocInMark.length()-1, edocInMark.length())))
            	resultList.add("["+edocInMark.substring(0, edocInMark.length()-1)+"]");
            else
            	resultList.add("["+edocInMark+"]");
        }
    	return resultList ;
    }
	
	/**
	 * 查找已处理的督办事项
	 * @param supervisorId
	 * @param type
	 * @return
	 */
	public List<EdocSuperviseModel> findProcessedListBySupervisor(Long supervisorId){
		return queryByCondition(Constants.EDOC_SUPERVISE_TERMINAL, null, null, null);
	}
	public List<EdocSuperviseModel> findToBeProcessedListBySupervisor(Long supervisorId){
		return queryByCondition(Constants.EDOC_SUPERVISE_PROGRESSING, null, null, null);
	}
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public ProcessLogManager getProcessLogManager() {
		return processLogManager;
	}

	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}
}