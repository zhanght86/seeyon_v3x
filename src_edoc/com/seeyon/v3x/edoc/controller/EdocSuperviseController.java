package com.seeyon.v3x.edoc.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.oainterface.impl.V3xManagerFactory;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.manager.EdocSuperviseManager;
import com.seeyon.v3x.edoc.supervise.event.TerminateEdocSupervise;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.util.EdocSuperviseHelper;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.edoc.webmodel.EdocSuperviseDealModel;
import com.seeyon.v3x.edoc.webmodel.EdocSuperviseModel;
import com.seeyon.v3x.mail.manager.MessageMailManager;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class EdocSuperviseController extends BaseController{
	
	public static final Log log = LogFactory.getLog(EdocSuperviseController.class);
	
	public EdocSuperviseManager edocSuperviseManager;
	public EdocSummaryManager edocSummaryManager; 
	public EdocManager edocManager;
	public AffairManager affairManager;
	public MetadataManager metadataManager;
	public PermissionManager permissionManager;
	public OrgManager orgManager;
	public MobileMessageManager mobileMessageManager;
	public MessageMailManager messageMailManager;
    private ColManager colManager;
    public SearchManager searchManager;
	
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	public ColManager getColManager() {
		return colManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public EdocSuperviseManager getEdocSuperviseManager() {
		return edocSuperviseManager;
	}

	public void setEdocSuperviseManager(EdocSuperviseManager edocSuperviseManager) {
		this.edocSuperviseManager = edocSuperviseManager;
	}

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView mainEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/edocFrameEntry");
		
		mav.addObject("varTempPageController", "edocSupervise.do");
		mav.addObject("entry", "listMain");
		
		return mav;
	}
	
	public ModelAndView listMain(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/supervise/supervise_list_main");
		
		return mav;
	}
	
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String status = request.getParameter("status");
		String condition = request.getParameter("condition"); 
		request.setAttribute("condition", condition);
		
		String textfield ="";
		String textfield1 ="";
		if ("docMark".equals(condition)) {
			textfield = request.getParameter("docMark");
		 	request.setAttribute("docMark", textfield);
		} else if ("docInMark".equals(condition)) {
		 	textfield = request.getParameter("docInMark");
		 	request.setAttribute("docInMark", textfield);
		} else if ("createDate".equals(condition)) {
		 	textfield = request.getParameter("createDate");
		 	textfield1 = request.getParameter("createDate1");
		 	request.setAttribute("createDate", textfield);
		 	request.setAttribute("createDate1", textfield1);
		} else if ("awakeDate".equals(condition)) {
		 	textfield = request.getParameter("awakeDate");
		 	textfield1 = request.getParameter("awakeDate1");
		 	request.setAttribute("awakeDate", textfield);
		 	request.setAttribute("awakeDate1", textfield1);
		} else {
		 	textfield = request.getParameter("textfield");
		 	if (condition != null)
		 		request.setAttribute(condition, textfield);
		}
		
		ModelAndView mav = new ModelAndView("edoc/supervise/supervise_list_iframe");
		
		String label = "";
		
		List<EdocSuperviseModel> list = null;
		V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
		if(null!=status && !"".equals(status)){		
        	if(condition == null||(Strings.isBlank(textfield))&&Strings.isBlank(textfield1)) {
        		list = edocSuperviseManager.queryByCondition(Integer.valueOf(status).intValue(),null,null,null,member.getSecretLevel()); //成发集团项目
        	}
        	else{
        		list = edocSuperviseManager.queryByCondition(Integer.valueOf(status).intValue() , condition, textfield, textfield1,member.getSecretLevel());//成发集团项目
        	}
        	       	
			if(Integer.valueOf(status) == Constants.EDOC_SUPERVISE_PROGRESSING)
				label = "edoc.supervise.transacted.without";
			else
				label =  "edoc.supervise.transacted.done";
		}else{
			status = Constants.EDOC_SUPERVISE_PROGRESSING + "";
			list =  edocSuperviseManager.queryByCondition(Constants.EDOC_SUPERVISE_PROGRESSING,null,null,null,member.getSecretLevel()); //成发集团项目
			label =  "edoc.supervise.transacted.without";
		}
		
		mav.addObject("label", label);
		mav.addObject("list", pagenate(list));
		mav.addObject("status", status);
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
		Metadata  deadlineMetadata= metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
		mav.addObject("colMetadata", colMetadata);
		mav.addObject("deadlineMetadata", deadlineMetadata);
		return mav;
	}
	
	private List<EdocSuperviseModel> findByType(int status){
		
		
		User user = CurrentUser.get();
		Long supervisorId = user.getId();
		List<EdocSuperviseModel> list = null;
		
		//根据status的状态判断返回的list<未办结/以办理>
//		if(Constants.EDOC_SUPERVISE_PROGRESSING == status){
//			list =  edocSuperviseManager.findToBeProcessedListBySupervisor(supervisorId);
//		}else if(Constants.EDOC_SUPERVISE_TERMINAL == status){
//			list =  edocSuperviseManager.findProcessedListBySupervisor(supervisorId);
//		}
		list = edocSuperviseManager.queryByCondition(Integer.valueOf(status).intValue(),null,null,null); 
		return list;
	}
	
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("");
		
		return null;
	}
	
	/**
	 * 返回的是公文的流程查看页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public ModelAndView detail(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	super.noCache(response);
    	String superviseId = request.getParameter("superviseId");
    	String summaryIdStr = request.getParameter("summaryId");
    	Long summaryId = null;
    	boolean flag = false;
    	if(Strings.isNotBlank(summaryIdStr)){
    		flag = true;
    	}
    	Affair affair = null;
    	if(null!=superviseId || flag){
    		if(flag){
    			summaryId = Long.valueOf(summaryIdStr);
    		}else{
	    		ColSuperviseDetail detail = edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
	    		if(null!=detail){
	    			summaryId = detail.getEntityId();
	    	    }
	    	}
    		
			affair = affairManager.getCollaborationSenderAffair(summaryId);
			
	        try{
	            String msg = edocSuperviseManager.checkColSupervisor(summaryId, affair);
	            if(Strings.isNotBlank(msg)){
	                throw new ColException(msg);
	            }
	            
	            V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
	            EdocSummary edocSummary = edocManager.getEdocSummaryById(summaryId, true);
	            if(edocSummary != null){
	                if(member.getSecretLevel()< edocSummary.getEdocSecretLevel()){
	                    throw new ColException("涉密等级不够，无法查看！");
	                }
	            }
	            
	        }catch(ColException e){
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
                out.println("if(window.dialogArguments){");
                out.println("  window.returnValue = \"true\";");
                out.println("  window.close();");
                out.println("}else{");
                out.println("  parent.getA8Top().reFlesh();");
                out.println("}");
                out.println("");
                out.println("</script>");
                return null;
	        }
    	}
    	
    	//SECURITY 访问安全检查
		if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.edoc, CurrentUser.get(), summaryId, affair, null)){
			return null;
		}
    	
    	String openModal = request.getParameter("openModal");
    	if(!"list".equals(openModal)){
    		openModal = "popup";
    	}
    	ModelAndView mav = new ModelAndView("edoc/edocDetail");
    	mav.addObject("summaryId", summaryId);
		mav.addObject("controller", "edocController.do");
		mav.addObject("from", "supervise");
		mav.addObject("openModal", openModal);
		if(affair!=null){
			mav.addObject("affairId", affair.getId());
		}
		return mav;
	}
    /**
     * logEntry为superviseLog的入口,在superviseLog外边套一层框架
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView logEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	String superviseId = request.getParameter("superviseId");
    	ModelAndView mav = new ModelAndView("edoc/supervise/superviseLogIframe");
    	return mav.addObject("superviseId", superviseId);
    }
    
    /**
     * 根据superviseId(督办的id)查出该督办下的所有日志
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showLog(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	
    	ModelAndView mav = new ModelAndView("edoc/supervise/superviseLog");
    	String superviseId = request.getParameter("superviseId");
    	List<ColSuperviseLog> logList = edocSuperviseManager.findLogById(Long.valueOf(superviseId));
    	return mav.addObject("logList", logList);
    }
    
    /**
     * 更改督办的内容摘要
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView updateContent(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	
    	String content = request.getParameter("content");
    	String superviseId = request.getParameter("superviseId");
    	String processState = request.getParameter("status");
    	if(null!=superviseId && null!=content){
    		ColSuperviseDetail detail = edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
    		detail.setDescription(content);
    		edocSuperviseManager.changeSuperviseDetail(detail);
    		}
    	int proType = Constants.EDOC_SUPERVISE_PROGRESSING;
    	if(!Strings.isBlank(processState) && Integer.valueOf(processState)==Constants.EDOC_SUPERVISE_TERMINAL){
    		proType = Constants.EDOC_SUPERVISE_TERMINAL;
    	}
    	ModelAndView mav = getUpdatedMAV(proType);
    	mav.addObject("status", processState);
		mav.addObject("label", request.getParameter("label"));
    	return mav;
    	
    }
    
    /**
     * 查看督办的内容摘要
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showDescription(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	String superviseId = request.getParameter("superviseId");
    	//String content = request.getParameter("content");
    	String content = "";
    	String title = "";
    	int status = Constants.EDOC_SUPERVISE_PROGRESSING;
    	if(null!=superviseId && !"".equals(superviseId)){
    		ColSuperviseDetail detail = edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
    		if(null!=detail){
    			content = detail.getDescription();
    			title = detail.getTitle();
    			status = detail.getStatus();
    		}
    	}
    	return new ModelAndView("edoc/supervise/superviseDescription").addObject("content", content).addObject("superviseId", superviseId).addObject("title", title).addObject("status",status);
    }
    
    /**
     * 用于在列表上只显示流程图(没有调用showDigram),去掉了内容和流程处理部分.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showDigramOnly(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String superviseId = request.getParameter("superviseId");
		String edocId = request.getParameter("edocId");
		ModelAndView mav = new ModelAndView("edoc/supervise/superviseDiagram");
		
		ColSuperviseDetail detail = edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
		if(null!=detail){
			Boolean hasWorkflow = false;        //是否还存在流程
			String process_desc_by = "";        //路程排序
			boolean hasDiagram = false;    
			int iEdocType = 0;
			
			EdocSummary summary = edocSummaryManager.findById(detail.getEntityId());
	    	if(summary != null && summary.getCaseId() != null){
	    		hasDiagram = true;
	    		iEdocType = summary.getEdocType();
	    	}
	    	
    		FlowData flowData = EdocHelper.getProcessPeople(summary.getProcessId());
    		if(flowData != null)
                hasWorkflow = Boolean.TRUE;
    		
	    	Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
	    	Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
	    	process_desc_by = FlowData.DESC_BY_XML;
	    	
	        Metadata remindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
	        Metadata  deadlineMetadata= metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
	        
	        mav.addObject("remindMetadata", remindMetadata);
	        mav.addObject("deadlineMetadata", deadlineMetadata); 
	        
	        mav.addObject("controller", "edocController.do");
	        mav.addObject("appName",EdocEnum.getEdocAppName(iEdocType));
	        mav.addObject("templeteCategrory",EdocEnum.getTempleteCategory(iEdocType)); 
	        
	        Metadata flowPermPolicyMetadata=null; 
	        String defaultPerm="shenpi";
	        if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
	    	{
	    		mav.addObject("policy", "dengji");
	    		mav.addObject("newEdoclabel", "edoc.new.type.rec");
	    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_rec_permission_policy);
	    		defaultPerm="yuedu";
	    	}
	        else if(EdocEnum.edocType.sendEdoc.ordinal()==iEdocType)
	    	{
	        	mav.addObject("policy", "niwen");
	        	mav.addObject("newEdoclabel", "edoc.new.type.send");
	    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_send_permission_policy);
	    	}
	    	else
	    	{
	    		mav.addObject("policy", "niwen");
	    		mav.addObject("newEdoclabel", "edoc.new.type.send");
	    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_qianbao_permission_policy);
	    	}
	        int actorId=EdocEnum.getStartAccessId(iEdocType);
	        mav.addObject("defaultPermLabel", "node.policy."+defaultPerm);
	        mav.addObject("flowPermPolicyMetadata",flowPermPolicyMetadata);
	    	
	    	mav.addObject("comMetadata", comMetadata);
	    	mav.addObject("colMetadata", colMetadata);
	    	mav.addObject("summary", summary);
	    	mav.addObject("isShowButton", false);
	    	mav.addObject("hasDiagram", hasDiagram);
	    	mav.addObject("process_desc_by", process_desc_by);
	    	mav.addObject("hasWorkflow", hasWorkflow);
	    	mav.addObject("actorId",actorId);
	    	mav.addObject("superviseId", superviseId);
	    	mav.addObject("processId", summary.getProcessId());
	    	mav.addObject("caseId", summary.getCaseId());
	    	mav.addObject("summaryId", edocId);
    	}

    	return mav;
    }
    public ModelAndView changeSupervise(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String superviseId = request.getParameter("superviseId");
		String awakeDate = request.getParameter("awakeDate");
		String description = request.getParameter("description");
		ColSuperviseDetail detail = this.edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
		if(detail!=null){
			if(Strings.isNotBlank(awakeDate)){
				detail.setAwakeDate(Datetimes.parse(awakeDate, Datetimes.dateStyle));
			}
			if(Strings.isNotBlank(description)){
				detail.setDescription(description);
			}
		}
		this.edocSuperviseManager.updateSuperviseDetail(detail);
		return null;
    }
    /**
     * 修改督办的截止时间
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	public ModelAndView change(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		String superviseId = request.getParameter("superviseId");
		String endDate = request.getParameter("endDate");
		String subject = request.getParameter("subject");
		if(null!=superviseId && null!=endDate){
			ColSuperviseDetail detail = edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
			//debug:36574 start author:MENG
			//debug:36574 end
			detail.setAwakeDate(Datetimes.parseDatetimeWithoutSecond(endDate));
			
			String summarySubject = "...";
			
			//如果从页面获取的标为空,查summary找到subject
			if(null==subject || "".equals(subject)){
				EdocSummary summary = edocSummaryManager.findById(detail.getEntityId());
				summarySubject = summary.getSubject(); 
			}
			
			//--更改完督办时间后,根据新时间生成定时器,届时自动提醒所有督办人
        	try{
    			Scheduler sched = QuartzListener.getScheduler();
                
    			String scheduleProp = detail.getScheduleProp();
    			if(null!=scheduleProp){
    			String[] scheduleProp_Array = scheduleProp.split("\\|");
    			if(null!=scheduleProp_Array && scheduleProp_Array.length > 0){
    				sched.deleteJob(scheduleProp_Array[0], scheduleProp_Array[1]); //public boolean deleteJob(String jobName, String groupName);
    				//删除队列中的消息提醒，下面将会重新生成。
    				}
    			}
    			
    			Long jobId = UUIDLong.longUUID();
                String jobName = jobId.toString();
                Long groupId = UUIDLong.longUUID();
                String groupName = groupId.toString();
                Long triggerId = UUIDLong.longUUID();
                String triggerName = triggerId.toString();
                
                Date eDate = detail.getAwakeDate();
                if(eDate.before(new Date())){
                	eDate = Datetimes.addSecond(new Date(), 5);  //如果设置督办时间比现在时间提前，或者是当天，过5秒后提醒。
                }else{
                	eDate = Datetimes.addHour(eDate, -16);   //设置后前一天8点提醒。
                }
                SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, eDate);      
                
    			JobDataMap datamap = new JobDataMap();
    			datamap.putAsString("edocSuperviseId", detail.getId());
    			
    			//--根据detail获取所有督办人的id,组成字符串传到后台
    			StringBuffer ids = new StringBuffer("");
    			Set<ColSupervisor> s_list = detail.getColSupervisors();
    			for(ColSupervisor s:s_list){
    				ids.append(s.getSupervisorId()).append(",");
    			}
    			if(ids.toString().endsWith(",")){
    				ids.deleteCharAt(ids.length()-1);
    			}
    			datamap.put("supervisorMemberId", ids.toString());
    			//--
    			datamap.put("subject", null!=subject && !"".equals(subject) ? subject : summarySubject);//subject为空即插summarySubject
    			
                JobDetail job = new JobDetail(jobName, groupName, TerminateEdocSupervise.class);
                job.setJobDataMap(datamap);
                
                detail.setScheduleProp(jobName + "|" + groupName);
                
                sched.scheduleJob(job, trigger);
                
        	}catch(Exception e){
        		log.error(e.getMessage(), e);
        	}
        	
			edocSuperviseManager.changeSuperviseDetail(detail);
			//--
		}
		
		//--返回
		
		
		
    	String processState = request.getParameter("status");
    	int proType = Constants.EDOC_SUPERVISE_PROGRESSING;
    	if(!Strings.isBlank(processState) && Integer.valueOf(processState)==Constants.EDOC_SUPERVISE_TERMINAL){
    		proType = Constants.EDOC_SUPERVISE_TERMINAL;
    	}
    	ModelAndView mav = getUpdatedMAV(proType);
    	mav.addObject("status", processState);
		mav.addObject("label", request.getParameter("label"));
		return mav;
	}
	
	/**
	 * 修改督办的流程图
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView changeProcess(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String activityId = request.getParameter("activityId");
		String processId = request.getParameter("processId");
		int operationType = Integer.parseInt(request.getParameter("operationType"));
		FlowData flowData = FlowData.flowdataFromRequest();
		
		List<String> xmlList = null;//ColHelper.superviseUpdateProcess(processId, activityId, operationType, flowData);
		String caseLogXML = null;
		String caseProcessXML = null;
		String caseWorkItemXML = null;
		if(xmlList != null){
			caseLogXML = xmlList.get(0);
			caseProcessXML = xmlList.get(1);
			caseWorkItemXML = xmlList.get(2);
		}
		
		ModelAndView mav = new ModelAndView("edoc/supervise/superviseUpdateProcess");
		mav.addObject("_caseProcessXML", caseProcessXML);
		mav.addObject("_caseLogXML", caseLogXML);
		mav.addObject("_caseWorkItemLogXML", caseWorkItemXML);
		return mav; 
	}

	/**
	 * 弹出催办的消息页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView hasten(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/supervise/superviseMessage");
        String processId = request.getParameter("processId");
        String activityId = request.getParameter("activityId");
        String superviseId = request.getParameter("superviseId");		
        Long _activityId = Long.parseLong(activityId);
    	String memberIdStr = ColHelper.hastenMemberIdsMap.get(_activityId);
    	if(!"".equals(memberIdStr)){
    		ColHelper.hastenMemberIdsMap.remove(_activityId);
    	}    	
    	FlowData flowData = colManager.preHasten(memberIdStr);
    	String summaryId = request.getParameter("summary_id");
    	mav.addObject("summaryId", summaryId);
    	mav.addObject("flowData", flowData);
		

        mav.addObject("processId", processId);
        mav.addObject("activityId", activityId);
        mav.addObject("superviseId", superviseId);
        
        /*
        // --start-- 以下方法取的是接收人是否能接收到短信
        List<Integer> workitemIdList = null;
        try{
        	workitemIdList = ColHelper.getWorkitemByActivity(processId, activityId);
        }catch(Exception e){
        	e.printStackTrace();
        }
        Map conditions = new HashMap();
        List<Affair> affairList = new ArrayList<Affair>();
        for (Integer workitemId : workitemIdList) {
            Long longWorkitemId = new Long(workitemId);
            conditions.put("subObjectId", longWorkitemId);
            List<Affair> list = affairManager.getByConditions(conditions);
            affairList.addAll(list);
        }
    	List<MessageReceiver> receivers = new ArrayList<MessageReceiver>();
        if(affairList != null && affairList.size() >0){
        	for (Affair affair : affairList) {
		            Long memberId = affair.getMemberId();
		            V3xOrgMember member = this.orgManager.getMemberById(memberId);
		            if(member!=null&&member.getAgentId()!=V3xOrgEntity.DEFAULT_NULL_ID)
		            	memberId = member.getAgentId();
		            	
		
				    receivers.add(new MessageReceiver(affair.getId(), affair.getMemberId(),"message.link.edoc.pending",affair.getId().toString()));
		        }
        }
        V3xOrgMember member = orgManager.getMemberById(receivers.get(0).getReceiverId());
        V3xOrgAccount account = orgManager.getAccountByLoginName(member.getLoginName());
        boolean canReceiveMobileMessage = mobileMessageManager.isCanRecieve(member.getId(), account.getId());
        // --end--
        mav.addObject("canReceiveMobileMessage", canReceiveMobileMessage); //是否能收到短信
        
        String email = member.getEmailAddress();
        boolean canReceiveMail = false;
        boolean hasSystemMailBox = false;
        
        if(Strings.isNotBlank(email) && !email.equals("0")){
        	canReceiveMail = true;
        }
        hasSystemMailBox = messageMailManager.hasSystemMailbox();
        mav.addObject("canReceiveMail", canReceiveMail); //是否能收到邮件
        mav.addObject("hasSystemMailBox", hasSystemMailBox); //是否设置了系统邮箱
		*/
		return mav;
	}
	
	/**
	 * 应comrade jincm的要求，将修改流程方式进行改变
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showDigarm(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String superviseId = request.getParameter("superviseId");
		String isSuperviseStr = request.getParameter("isSupervise");
		String summaryId = request.getParameter("summaryId");
		ModelAndView mav = new ModelAndView("edoc/supervise/showDiagram");
		
		Boolean isSupervise = Strings.isBlank(isSuperviseStr) || (Strings.isNotBlank(isSuperviseStr) && Boolean.parseBoolean(isSuperviseStr));
		mav.addObject("isSupervise", isSupervise);
		Boolean hasWorkflow = false;        //是否还存在流程
		String process_desc_by = "";        //路程排序
		boolean hasDiagram = false;    
		int iEdocType = 0;
		
		ColSuperviseDetail detail = null;
		EdocSummary summary = null;
		if (isSupervise) {
			if (Strings.isBlank(superviseId)) {
				return mav;
			}
			detail = edocSuperviseManager.getSuperviseById(Long.valueOf(superviseId));
			summary = edocSummaryManager.findById(detail.getEntityId());
			if(summary != null && summary.getCaseId() != null){
	    		if(summary.getCompleteTime() != null){
					PrintWriter out = response.getWriter();
					String msg = Constant.getString4CurrentUser("col.process.finished");
					out.println("<script>");
		        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
		        	out.println("if(window.dialogArguments){"); //弹出
		        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
		        	out.println("  window.close();");
		        	out.println("}else{");
		        	out.println("  parent.getA8Top().reFlesh();");
		        	out.println("}");
		        	out.println("</script>");
		        	out.close();
		        	return null;
				}
	    		hasDiagram = true;
	    		iEdocType = summary.getEdocType();
	    	}
		} else if(!isSupervise && Strings.isNotBlank(summaryId)) {
			summary = edocSummaryManager.findById(Long.parseLong(summaryId));
			if (summary!=null) {
				hasDiagram = true;
	    		iEdocType = summary.getEdocType();
			}
		}
		
		FlowData flowData = EdocHelper.getRunningProcessPeople(summary.getProcessId());
		if(flowData != null)
            hasWorkflow = Boolean.TRUE;
    	Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
    	Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
    	process_desc_by = FlowData.DESC_BY_XML;
    	
        Metadata remindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        Metadata  deadlineMetadata= metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
        
        mav.addObject("remindMetadata", remindMetadata);
        mav.addObject("deadlineMetadata", deadlineMetadata); 
        
        mav.addObject("controller", "edocController.do");
        mav.addObject("appName",EdocEnum.getEdocAppName(iEdocType));
        mav.addObject("templeteCategrory",EdocEnum.getTempleteCategory(iEdocType)); 
        
        Metadata flowPermPolicyMetadata=null; 
        String defaultPerm="shenpi";
        if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
    	{
    		mav.addObject("policy", "dengji");
    		mav.addObject("newEdoclabel", "edoc.new.type.rec");
    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_rec_permission_policy);
    		defaultPerm="yuedu";
    	}
        else if(EdocEnum.edocType.sendEdoc.ordinal()==iEdocType)
    	{
        	mav.addObject("policy", "niwen");
        	mav.addObject("newEdoclabel", "edoc.new.type.send");
    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_send_permission_policy);
    	}
    	else
    	{
    		mav.addObject("policy", "niwen");
    		mav.addObject("newEdoclabel", "edoc.new.type.send");
    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_qianbao_permission_policy);
    	}
        int actorId=EdocEnum.getStartAccessId(iEdocType);
        mav.addObject("defaultPermLabel", "node.policy."+defaultPerm);
        mav.addObject("flowPermPolicyMetadata",flowPermPolicyMetadata);
    	
    	mav.addObject("comMetadata", comMetadata);
    	mav.addObject("colMetadata", colMetadata);
    	mav.addObject("summary", summary);
    	mav.addObject("isShowButton", false);
    	mav.addObject("hasDiagram", hasDiagram);
    	mav.addObject("process_desc_by", process_desc_by);
    	mav.addObject("hasWorkflow", hasWorkflow);
    	mav.addObject("actorId",actorId);
    	mav.addObject("processId", summary.getProcessId());
    	mav.addObject("caseId", summary.getCaseId());
    	
    	//分支 开始
        if(summary.getTempleteId()!=null) {
        	User user = CurrentUser.get();
        	TempleteManager templeteManager=(TempleteManager)ApplicationContextHolder.getBean("templeteManager");
        	List<ColBranch> branchs = templeteManager.getBranchsByTemplateId(summary.getTempleteId(),ApplicationCategoryEnum.edoc.ordinal());
        	mav.addObject("branchs", branchs);
        	if(branchs != null) {
        		mav.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
        		V3xOrgMember mem = orgManager.getMemberById(user.getId());
        		List<MemberPost> secondPosts = mem.getSecond_post();
        		mav.addObject("secondPosts", secondPosts);
        		mem = orgManager.getMemberById(summary.getStartUserId());
        		mav.addObject("startTeams", this.orgManager.getUserDomain(mem.getId(), mem.getOrgAccountId(), V3xOrgEntity.ORGENT_TYPE_TEAM));
        		mav.addObject("startSecondPosts", mem.getSecond_post());
            }
        }
    	//分支 结束    	
        if(null != summary.getEdocSecretLevel() && !"".equals(summary.getEdocSecretLevel())){//成发集团项目 程炯 为公文督办进行流程修改时传入流程密级
        	mav.addObject("secretLevel", summary.getEdocSecretLevel());
        }
    	return mav;
	}
	
	/**
	 * 给当前的节点发送催办的消息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView sendMessage(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		PrintWriter out = response.getWriter();
		
		String processId = request.getParameter("processId");
		String superviseId = request.getParameter("superviseId");
		String summaryId = request.getParameter("summaryId");
		String mode = request.getParameter("remindMode");
		String additional_mark = request.getParameter("content");
		String activityId = request.getParameter("activityId");

		String[] people = request.getParameterValues("deletePeople");
        long[] receivers = new long[people.length];
        int i = 0;
        for(String p :people) {
        	receivers[i] = Long.parseLong(p);
        	i++;
        }
        
		edocSuperviseManager.sendMessage(Long.valueOf(superviseId), mode, processId,activityId, additional_mark,receivers,summaryId);

        //this.colSuperviseManager.saveLog(Long.parseLong(superviseId), CurrentUser.get().getId(), receivers, content);
        
       // String info = Constant.getString4CurrentUser("hasten.success.label");
       // PrintWriter out = response.getWriter();
      //  out.println("<script>");
       // out.println("alert('" + info + "');");
      // out.println("</script>");
        
     //   return null;		
		
				
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
		String alertNote = ResourceBundleUtil.getString(r, "edoc.supervise.sendMessage.success");
						
		try{

			out.println("<script>");
			if(Strings.isNotBlank(superviseId)){
				Long id = Long.parseLong(superviseId);
				out.println("parent.setHastenTimesBack('" + this.edocSuperviseManager.getHastenTimes(id)+ "');");
			}
			out.println("alert('"+alertNote+"');");
			out.println("parent.close()");
			out.println("</script>");
			return null;
		}catch(Exception e){
			alertNote = ResourceBundleUtil.getString(r, "edoc.supervise.sendMessage.failure");
			out.println("<script>");
			out.println("alert('"+alertNote+"');");
			out.println("parent.close()");
			out.println("</script>");		
			return null;
		}

	}
	
	public ModelAndView deleteSuperviseDetail(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		String ids = request.getParameter("id");
		
		if(null!=ids && !"".equals(ids)){
			edocSuperviseManager.deleteSuperviseDetail(ids);
		}
		return super.redirectModelAndView("/edocSupervise.do?method=list&status=1");
	}
	
	/**
	 * 公文督办打开的督办窗口
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseWindowEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/supervise/supervise_window_iframe");
		
		String str_summaryId = request.getParameter("summaryId");
		
		mav.addObject("summaryId", str_summaryId);
		
		return mav;
	}
	
	/**
	 * 公文督办选择窗口
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView superviseWindow(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/supervise/supervise_window");
		
		mav.addObject("assignedMemberId", request.getParameter("assignedMemberId"));
		mav.addObject("assignedDate", request.getParameter("assignedDate"));
		
		String str_summaryId = request.getParameter("summaryId");
		if(null!=str_summaryId && !"".equals(str_summaryId)){
			Long summaryId = Long.valueOf(str_summaryId);
	    	
			Object[] object = EdocSuperviseHelper.getSupervisorIdsBySummaryId(summaryId);
	    	if(null!=object && object.length > 0){
	    		String supervisorIds = (String)object[0]; //督办人的ID
	    		Date endDate = (Date)object[1];     //督办时间
	    		mav.addObject("supervisorIds", supervisorIds);
	    		mav.addObject("endDate", endDate);
	    	}
	    	
	    	ColSuperviseDetail detail = edocSuperviseManager.getSuperviseBySummaryId(summaryId);
	    	if(null!=detail){
	    		mav.addObject("supervisorNames", detail.getSupervisors());
	    		Set<ColSupervisor> set = detail.getColSupervisors();
	    		if(null!=set && set.size()>0){
	    			mav.addObject("count", set.size());
	    		}
	    	}
		}
		String iscol = request.getParameter("iscol");
    	mav.addObject("iscol", Strings.isNotBlank(iscol)?Boolean.valueOf(iscol):false);
		return mav;
	}
	
    public ModelAndView saveSupervise(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	//String superviseId = request.getParameter("superviseId");
    	String supervisorNames = request.getParameter("supervisorNames");
    	String supervisorMemberId = request.getParameter("supervisorMemberId");
    	String superviseDate = request.getParameter("superviseDate");
    	//String canModify = request.getParameter("canModify");
    	String title = request.getParameter("title");
    	String summaryId = request.getParameter("summaryId");
    	
    	if(!Strings.isBlank(summaryId)){
    	    if("true".equals(request.getParameter("isDelete"))){
    	    	EdocSummary summary = edocSummaryManager.findById(Long.parseLong(summaryId));
    	    	
    	        edocSuperviseManager.deleteSuperviseDetailAndSupervisors(summary);
    	    }
            else if(!Strings.isBlank(supervisorNames) && !Strings.isBlank(supervisorMemberId) && !Strings.isBlank(superviseDate)){
                this.edocSuperviseManager.superviseForSentList("100", supervisorMemberId, supervisorNames, superviseDate, Long.valueOf(summaryId),title);
            }
        }
    	PrintWriter out = response.getWriter();
    	out.println("<script>");
    	out.println("  window.close();");
    	out.println("</script>");
    	return null;
    }
	
	
	
	/**
	 * 用于刷新页面的mav,应该可以使用loaction.reload()代替
	 * @return
	 */
	
	
	
    public ModelAndView showAffairEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	ModelAndView mv = new ModelAndView("edoc/supervise/showAffairEntry");
    	return mv;
    }
    
    public ModelAndView showAffair(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	String summaryId = request.getParameter("summaryId");
    	List<EdocSuperviseDealModel> models = this.edocSuperviseManager.getAffairModel(Long.parseLong(summaryId));
    	ModelAndView mv = new ModelAndView("collaboration/supervise/showAffair");
    	mv.addObject("models", models);
    	return mv;
    }
	
	private ModelAndView getUpdatedMAV(int state){
		ModelAndView mav = new ModelAndView("edoc/supervise/supervise_list_iframe");
		User user = CurrentUser.get();
		Long supervisorId = user.getId();
		List<EdocSuperviseModel> list = this.findByType(state);
		mav.addObject("list", list);
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
		mav.addObject("colMetadata", colMetadata);
		return mav;	
	}
	
	/**
	 * 封装的分页代码
	 * @param <T>
	 * @param list
	 * @return
	 */
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	/**
     * 按指定格式得到文号
     * @param list
     * @return
     */
	private List<String> getFormatEdocMark(List<EdocSummaryModel> list) {
		
    	List<String> resultList = new ArrayList<String>();
    	
    	// 公文文号
        String edocMark = "" ;
        if (list != null && list.size() > 0) {
        	for (int i  = 0 ; i < list.size() ; i ++) {
            	EdocSummaryModel model = list.get(i) ;
            	if (Strings.isNotBlank(model.getSummary().getDocMark()))
            		edocMark += "{value:'"+i+"',label:'"+model.getSummary().getDocMark()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getSummary().getDocMark())) 
            		edocMark += ",";
            }
            if (edocMark.length() > 1 && ",".equals(edocMark.substring(edocMark.length()-1, edocMark.length())))
            	resultList.add("["+edocMark.substring(0, edocMark.length()-1)+"]");
            else
            	resultList.add("["+edocMark+"]");
            
            // 内部文号
            String edocInMark = "" ;
            for (int i  = 0 ; i < list.size() ; i ++) {
            	EdocSummaryModel model = list.get(i) ;
            	if (Strings.isNotBlank(model.getSummary().getSerialNo()))
            		edocInMark += "{value:'"+i+"',label:'"+model.getSummary().getSerialNo()+"'}";
            	if ((i+1) < list.size() && Strings.isNotBlank(model.getSummary().getSerialNo())) 
            		edocInMark += ",";
            }
            if (edocInMark.length() >1 && ",".equals(edocInMark.substring(edocInMark.length()-1, edocInMark.length())))
            	resultList.add("["+edocInMark.substring(0, edocInMark.length()-1)+"]");
            else
            	resultList.add("["+edocInMark+"]");
        }
    	return resultList ;
    }

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public EdocManager getEdocManager() {
		return edocManager;
	}

	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public MobileMessageManager getMobileMessageManager() {
		return mobileMessageManager;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public MessageMailManager getMessageMailManager() {
		return messageMailManager;
	}

	public void setMessageMailManager(MessageMailManager messageMailManager) {
		this.messageMailManager = messageMailManager;
	}

}