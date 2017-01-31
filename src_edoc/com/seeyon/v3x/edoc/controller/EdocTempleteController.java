package com.seeyon.v3x.edoc.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.bpm.definition.BPMProcess;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocMarkDefinitionManager;
import com.seeyon.v3x.edoc.util.DataUtil;
import com.seeyon.v3x.edoc.webmodel.EdocFormModel;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

public class EdocTempleteController extends BaseController {
	
	private static Log log = LogFactory.getLog(EdocTempleteController.class);
	
	private TempleteManager templeteManager;

	private AttachmentManager attachmentManager;

	private TempleteCategoryManager templeteCategoryManager;
	
	private DocHierarchyManager docHierarchyManager;

	private OrgManager orgManager; 
	
	private MetadataManager metadataManager;
	
	private EdocFormManager edocFormManager;
	
	private ColSuperviseManager colSuperviseManager;
	private EdocMarkDefinitionManager edocMarkDefinitionManager;
	
	private TempleteConfigManager templeteConfigManager;
	private AppLogManager appLogManager;
	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}
	public void setEdocMarkDefinitionManager(
			EdocMarkDefinitionManager edocMarkDefinitionManager) {
		this.edocMarkDefinitionManager = edocMarkDefinitionManager;
	}

	private EdocManager edocManager;
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	/**
	 * @param templeteConfigManager the templeteConfigManager to set
	 */
	public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}
	public DocHierarchyManager getDocHierarchyManager() {
		return docHierarchyManager;
	}
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	public void setEdocFormManager(EdocFormManager edocFormManager)
	{
		this.edocFormManager=edocFormManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 设置是否为联合发文标志
	 *
	 */
	private void _setIsUnit(EdocSummary summary)
	{
		Long edocFormId=summary.getFormId();
		if(edocFormId!=null)
		{
			EdocForm ef=edocFormManager.getEdocForm(edocFormId);
			if(ef!=null)
			{
				summary.setIsunit(ef.getIsunit());
			}
		}
	}
	
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView systemSaveTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		
		String type = request.getParameter("type");
		String from = request.getParameter("from");
		String secretLevel = request.getParameter("secretLevel");//成发集团项目 程炯 为公文模版存入流程密级
		Integer categoryType = Integer.parseInt(request.getParameter("categoryType"));	
		Long categoryId = Long.parseLong(request.getParameter("categoryId"));
		
		Timestamp createDate = new Timestamp(System.currentTimeMillis());

		Templete templete = new Templete();
		bind(request, templete);
		templete.setSecretLevel(Integer.parseInt(secretLevel));//成发集团项目 程炯 为公文模版存入流程密级
		templete.setSubject(request.getParameter("templatename"));

		// 正文格式不保存流程
		if (!Templete.Type.text.name().equals(type)) {	
			templete.setWorkflow(this.doColWorkflow(request));
		}
//		if (!Templete.Type.text.name().equals(type)) {	
//			templete.setWorkflow(this.doColWorkflow(request));
//		}
		if (!Templete.Type.workflow.name().equals(type)) {
			String body = this.doColBody(request);
			templete.setBody(body);
		}
//		// 流程模板不保存正文
//		if (!Templete.Type.workflow.name().equals(type)) {
//			String body = this.doColBody(request);
//			templete.setBody(body);
//		}

		//属性
		String summary = null;
		if (!Templete.Type.workflow.name().equals(type)) {
			summary = this.doColSummary(request);
		}
		else{
			EdocSummary summaryObj = new EdocSummary();
			summaryObj.setEdocType(Integer.parseInt(request.getParameter("edocType")));
	
			summaryObj.setSubject(templete.getSubject());
			//流程模板保存
			String temp=request.getParameter("deadline");
			if(temp!=null && !"".equals(temp))
			{
				try{
					summaryObj.setDeadline(Long.parseLong(temp));
				}catch(Exception e)
				{
					summaryObj.setDeadline(-1L);
				}
			}
			temp=request.getParameter("advanceRemind");
			if(temp!=null && !"".equals(temp))
			{
				try{
					summaryObj.setAdvanceRemind(Long.parseLong(temp));
				}catch(Exception e)
				{
					summaryObj.setAdvanceRemind(-1L);
				}
			}
			try {
				if(request.getParameter("canTrack")!=null){
					summaryObj.setCanTrack(Integer.valueOf(request.getParameter("canTrack")));
				}else if(request.getParameter("track")!=null){
					summaryObj.setCanTrack(Integer.valueOf(request.getParameter("track")));
				}else{
					summaryObj.setCanTrack(0);
				}
			} catch (Exception e) {
				log.error("新建公文流程模板异常，是否跟踪参数不正确"+request.getParameter("canTrack")+","+request.getParameter("track"), e);
				summaryObj.setCanTrack(0);//流程模板这里设置无效
			}
			
			summary = XMLCoder.encoder(summaryObj);
			templete.setBodyType(Constants.EDITOR_TYPE_HTML);
		}
		
		
		
		templete.setSummary(summary);

		templete.setCreateDate(createDate);
		templete.setMemberId(user.getId());
		templete.setIsSystem(true);
		templete.setOrgAccountId(user.getLoginAccount());
		
		// 基准时间
		String referenceTime = request.getParameter("referenceTime");
		if (Strings.isBlank(referenceTime)) 
			templete.setStandardDuration(0);
		else
			templete.setStandardDuration(Integer.parseInt(referenceTime));
		
		boolean isSave = templete.isNew();
		templete.setIdIfNew();
		
		long templeteId = templete.getId();
		
		//授权信息
		String authInfo = request.getParameter("authInfo");
		String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
		Set<Long> memberSet = new HashSet<Long>();
		if(authInfos != null){
			int i = 0;
			for (String[] strings : authInfos) {
				TempleteAuth auth = new TempleteAuth();
				
				auth.setIdIfNew();
				auth.setAuthType(strings[0]);
				auth.setAuthId(Long.parseLong(strings[1]));
				auth.setSort(i++);
				auth.setObjectId(templeteId);
				
				templete.getTempleteAuths().add(auth);
				Set<Long> memberIdsSet = Functions.getAllMembersId(auth.getAuthType(), auth.getAuthId());
				
				if(memberIdsSet != null && !memberIdsSet.isEmpty()){
					memberSet.addAll(memberIdsSet);
				}
			}
		}
		
		//分枝 开始
//		保存分支条件
        String[] arr = request.getParameterValues("branchs");
        List<ColBranch> list = null;
        if(arr != null) {
        	String tmp = null;
        	String[] tmps = null;
        	list = new ArrayList<ColBranch>();
        	for(int i=0;i<arr.length;i++) {
        		tmp = arr[i];
        		if(tmp != null) {
        			tmps = tmp.split("↗",-1);
        			if(tmps != null) {
        				ColBranch branch = new ColBranch();
        				branch.setTemplateId(templeteId);
        				branch.setLinkId(Long.parseLong(tmps[0]));
        				branch.setId(Long.parseLong(tmps[1]));
        				branch.setConditionType(Integer.parseInt(tmps[2]));
        				branch.setFormCondition(tmps[3]);
        				branch.setConditionTitle(tmps[4]);
        				branch.setIsForce("".equals(tmps[5])||"0".equals(tmps[5])?0:1);
        				branch.setConditionDesc("".equals(tmps[6])||"null".equals(tmps[6])?null:tmps[6]);
        				branch.setConditionBase(tmps[7]);
        				branch.setAppType(ApplicationCategoryEnum.edoc.ordinal());
        				list.add(branch);
        			}
        		}
        	}
//        	if(list.size()>0)
//        		this.templeteManager.saveBranch(templeteId,list);
        }
        this.templeteManager.saveBranch(templeteId,list);
		
		//分枝 结束
		
		if(!isSave){
			// 删除原有附件
			this.attachmentManager.deleteByReference(templeteId);
		}

		//流程模板不保存正文
		if (!Templete.Type.workflow.name().equals(type)) {
		// 保存附件
		String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.edoc, templeteId, templeteId, request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	templete.setHasAttachments(true);
        }
		}
		
		if (isSave) { //新建
			this.saveColSuperviseForTemplate(request, response, templete, isSave, false);
			templeteManager.save(templete);
		}
		else { // 修改
			this.saveColSuperviseForTemplate(request, response, templete, false, false);			
			this.templeteManager.update(templete);
		}
		
		//设置文号定义已经使用
		try{
			setMarkDefinitionPublished(request);
		}catch(Exception e){
			log.error(e);
		}
		
		//记录日志
		
		appLogManager.insertLog(user, AppLogAction.Edoc_TempleteAuthorize,user.getName(),templete.getSubject());
		///*
		//将当前模板推送到首页-我的模板
        List<Long> authMemberIdsList = new ArrayList<Long>();
        
        if(memberSet != null && !memberSet.isEmpty()){
        	authMemberIdsList.addAll(memberSet);
        	templeteConfigManager.pushThisTempleteToMain4Members(authMemberIdsList, templeteId, categoryType);
        }
        //*/
		
		return super.redirectModelAndView("/genericController.do?ViewPage=collaboration/templete/systemIndex&categoryId="+categoryId+"&categoryType="+categoryType+"&from=" + from);
	}
	
	private void setMarkDefinitionPublished(HttpServletRequest req) {
		String docMark = req.getParameter("my:doc_mark");
		String docMark2 =req.getParameter("my:doc_mark2");
		String serialNo =req.getParameter("my:serial_no");
		if(Strings.isNotBlank(docMark)){
			 EdocMarkModel em=EdocMarkModel.parse(docMark);
	         if (em!=null)
	         {
	        	 edocMarkDefinitionManager.setEdocMarkDefinitionUsed(em.getMarkDefinitionId());
	         }
		}
		if(Strings.isNotBlank(docMark2)){
			 EdocMarkModel em=EdocMarkModel.parse(docMark2);
	         if (em!=null)
	         {
	        	 edocMarkDefinitionManager.setEdocMarkDefinitionUsed(em.getMarkDefinitionId());
	         }		
		}
		if(Strings.isNotBlank(serialNo)){
			 EdocMarkModel em=EdocMarkModel.parse(serialNo);
	         if (em!=null)
	         {
	        	 edocMarkDefinitionManager.setEdocMarkDefinitionUsed(em.getMarkDefinitionId());
	         }
		}
	}
	/**
	 * 生成正文内容
	 * @param request
	 * @param createDate
	 * @return
	 * @throws Exception
	 */
	private String doColBody(HttpServletRequest request) throws Exception{
		EdocBody body = new EdocBody();
		bind(request, body);
		String tempStr=request.getParameter("bodyType");
		String contentStr=request.getParameter("content");
		body.setContent(contentStr);
        body.setContentType(tempStr);
		Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
		if (bodyCreateDate != null) {
			body.setCreateTime(new Timestamp(bodyCreateDate.getTime()));
		}

		return XMLCoder.encoder(body);
	}
	
	/**
	 * 生成流程内容
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String doColWorkflow(HttpServletRequest request) throws Exception{
		FlowData flowData = FlowData.flowdataFromRequest();
		String processId = ColHelper.saveOrUpdateProcessByFlowData(
				flowData, null,true);
		FlowData flowData1 = ColHelper.getProcessPeople(processId);
		ColHelper.deleteReadyProcess(processId);
		return flowData1.getXml();
	}
	
	/**
	 * 生成协同属性信息
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String doColSummary(HttpServletRequest request) throws Exception{
		EdocSummary edocSummary = new EdocSummary();		
		edocSummary.setId(null);
		edocSummary.setEdocType(Integer.parseInt(request.getParameter("edocType")));
		long formId=Long.parseLong(request.getParameter("edoctable"));
        DataUtil.requestToSummary(request,edocSummary,formId);
        String type = request.getParameter("type");
        //正文模板不保存
        if (Templete.Type.text.name().equals(type)) {	
        	edocSummary.setAdvanceRemind(null);
        	edocSummary.setDeadline(null);
		}
//		colSummary.setCanForward(request.getParameterValues("canForward") != null);
//		colSummary.setCanArchive(request.getParameterValues("canArchive") != null);
//		colSummary.setCanDueReminder(request.getParameterValues("canDueReminder") != null);
//		colSummary.setCanModify(request.getParameterValues("canModify") != null);
//        edocSummary.setCanTrack(Integer.parseInt(request.getParameter("track")));
//		colSummary.setCanEdit(request.getParameterValues("canEdit") != null);

		String note = request.getParameter("note");// 发起人附言
		// 附言内容为空，就不记录了
//		if (StringUtils.isNotBlank(note)) {
			EdocOpinion senderOpinion = new EdocOpinion();
			senderOpinion.setContent(note);
			senderOpinion.setOpinionType(EdocOpinion.OpinionType.senderOpinion.ordinal());
			senderOpinion.affairIsTrack = request.getParameterValues("isTrack") != null;
			senderOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));

			edocSummary.getEdocOpinions().add(senderOpinion);
//		}
		_setIsUnit(edocSummary);
		
		String[] filename = request.getParameterValues( com.seeyon.v3x.common.filemanager.Constants.FILEUPLOAD_INPUT_NAME_filename);
		String attsStr="";
		if(filename!=null){
	        for (int i=0;i<filename.length;i++) {
				String fname=filename[i];
				int lastIndex=fname.length();
				if(fname.lastIndexOf(".")!=-1){
					lastIndex=fname.lastIndexOf(".");
				}
				fname=fname.substring(0, lastIndex);
				if(Strings.isBlank(attsStr)){
					attsStr=(i+1)+"."+fname;
				}else{
					attsStr=attsStr+"&#x0A;"+(i+1)+"."+fname;
				}
	    	}
		}
		edocSummary.setAttachments(attsStr); 
		
		edocSummary.setEdocSecretLevel(Integer.parseInt(request.getParameter("secretLevel")));//成发集团项目 2012-8-30 程炯  转化公文模版中的流程密级
		return XMLCoder.encoder(edocSummary);
	}
	
	/**
	 * 新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView systemNewTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/templete/newEdocTemplete");
		
        String logoURL = EdocHelper.getLogoURL();
        modelAndView.addObject("logoURL", logoURL);
		
		String from = request.getParameter("from");
		Integer categoryType= Integer.parseInt(request.getParameter("categoryType"));
		User user = CurrentUser.get();
		
		String templateType=Templete.Type.templete.name();
		
		Long orgAccountId = user.getLoginAccount();
		
		String templeteId = request.getParameter("templeteId");
		long memberId = user.getId();
		
		int iEdocType= categoryType;
        
		Metadata flowPermPolicyMetadata=null; 
		String defaultPerm="shenpi";
        if(iEdocType==2)
        {
        	iEdocType=EdocEnum.edocType.sendEdoc.ordinal();
        	flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_send_permission_policy);
        }
        else if(iEdocType==3)
        {
        	iEdocType=EdocEnum.edocType.recEdoc.ordinal();
        	flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_rec_permission_policy);
        	defaultPerm="yuedu";
        }
        else
        {
        	iEdocType=EdocEnum.edocType.signReport.ordinal();
        	flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_qianbao_permission_policy);
        }
        modelAndView.addObject("defaultPermLabel", "node.policy."+defaultPerm);
        modelAndView.addObject("flowPermPolicyMetadata",flowPermPolicyMetadata);
        String domainIds = orgManager.getUserIDDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
        List <EdocForm> edocForms=edocFormManager.getEdocForms(user.getLoginAccount(),domainIds,iEdocType);
    	//去掉停用的
       	for(Iterator<EdocForm> it = edocForms.iterator();it.hasNext();){
       		EdocForm ef = it.next();
       		if(ef.getStatus()!= null && ef.getStatus().intValue() != EdocForm.C_iStatus_Published.intValue())
       			it.remove();
       	}
        if(edocForms==null || edocForms.size()<=0)
        {
        	String szJs="<script>alert(\""+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_nofind_edocForm")+"\");self.history.back();</script>";
        	response.getWriter().print(szJs);
        	return null;
        }
        
        modelAndView.addObject("edocForms",edocForms);
        
        EdocForm defaultEdocForm=null;
        Long edocFormId=0L;//默认公文单ID
        
		EdocSummary summary = null;
		EdocBody body = null;
		
		if(StringUtils.isNotBlank(templeteId)){ //修改
			Templete templete = this.templeteManager.get(Long.parseLong(templeteId));
			templateType=templete.getType();
			
			summary = (EdocSummary) XMLCoder.decoder(templete.getSummary());
			EdocHelper.reLoadAccountName(summary,EdocHelper.getI18nSeperator(request));
			body = (EdocBody) XMLCoder.decoder(templete.getBody());
			
			BPMProcess process = null;
			String caseProcessXML = null;
			if(!"text".equals(templateType)) {
				process = BPMProcess.fromXML(templete.getWorkflow());
				caseProcessXML = process.toXML(); //重新生成，因为要取新的节点名称
			}
			
			if(StringUtils.isNotBlank(caseProcessXML)){			
	            modelAndView.addObject("hasWorkflow", Boolean.TRUE);
	            modelAndView.addObject("process_desc_by", FlowData.DESC_BY_XML);
	            
	            caseProcessXML = EdocHelper.trimXMLProcessor(caseProcessXML);
	            
	            List<Party> workflowInfo = EdocHelper.getWorkflowInfo(process);
	
	            caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);
	
	            modelAndView.addObject("process_xml", caseProcessXML);
	            modelAndView.addObject("workflowInfo", workflowInfo);
			}
			//预归档
			Long archiveId = null;
	        String archiveName = "";
	        if(summary.getArchiveId() != null){
	        	archiveId = summary.getArchiveId(); 
	        	archiveName = docHierarchyManager.getNameById(archiveId);
	        }
	        //成发集团项目 程炯 2012-8-30 公文模版修改显示流程密级 begin
	        Integer secret = null;
	        if(summary.getEdocSecretLevel() != null){
	        	secret = summary.getEdocSecretLevel();
	        }
	        
	        modelAndView.addObject("secret", secret);
	        //end
	        
	        modelAndView.addObject("archiveName", archiveName);
	        
            modelAndView.addObject("attachments", attachmentManager
                    .getByReference(templete.getId(), templete.getId()));
            modelAndView.addObject("canDeleteOriginalAtts", true);    //允许删除原附件

            modelAndView.addObject("note", summary.getSenderOpinion());//发起人附言
            
            modelAndView.addObject("templete", templete);
            
            edocFormId=summary.getFormId();
            //检查模版公文单是否存在
            if(edocFormId!=null)
            {
            	defaultEdocForm=edocFormManager.getEdocForm(edocFormId);
            }
            if(defaultEdocForm==null){defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);}
                        //分枝 开始
            modelAndView.addObject("branchs", this.templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.edoc.ordinal()));
            //分枝 结束
            ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),templete.getId());
            if(detail != null) {
            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
            	StringBuffer ids = new StringBuffer();
            	for(ColSupervisor supervisor:supervisors)
            		ids.append(supervisor.getSupervisorId() + ",");
            	// fix 39573
            	if (ids.length()>=1) {
            		modelAndView.addObject("colSupervisors", ids.substring(0, ids.length()-1));
            	}
            	modelAndView.addObject("colSupervise", detail);
            	
            	List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(templete.getId());
            	String superviseRole = "";
            	for(SuperviseTemplateRole role : roleList){
            		superviseRole += role.getRole();
            		superviseRole += ",";
            	}
            	if(superviseRole.length()>0 && superviseRole.endsWith(",")){
            		superviseRole = superviseRole.substring(0,superviseRole.length()-1);
            	}
            	modelAndView.addObject("colSuperviseRole", superviseRole);
            }
            
		}
		else { //直接新建
            summary = new EdocSummary();
            body = new EdocBody();
            String bodyContentType=Constants.EDITOR_TYPE_OFFICE_WORD;
            if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("officeOcx")==false){bodyContentType=Constants.EDITOR_TYPE_HTML;}
            body.setContentType(bodyContentType);
            defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);
            summary.setEdocType(iEdocType);            
            summary.setCanTrack(1);
//          if(user.getLoginAccount() != 0 && (categoryType== 2 || categoryType== 5))
//			{
//			  summary.setSendUnit(EdocRoleHelper.getAccountById(user.getLoginAccount()).getName());
//			  summary.setSendUnitId("Account|"+Long.toString(user.getLoginAccount()));
//			}
			
        }
		
		if(defaultEdocForm==null)
        {
        	String szJs="<script>alert(\""+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_nofind_edocForm")+"\");self.history.back();</script>";
        	response.getWriter().print(szJs);
        	return null;
        }
		
		edocFormId=defaultEdocForm.getId();
		modelAndView.addObject("edocFormId",edocFormId);
				
		List<TempleteCategory> templeteCategories = this.templeteCategoryManager.getCategorys(orgAccountId, categoryType);
		StringBuffer categoryHTML = new StringBuffer();
		
		for (int i = 0; i < templeteCategories.size(); i++) {
			TempleteCategory category = templeteCategories.get(i);
			
			if(!"SYS".equalsIgnoreCase(from) && category.getParentId() == null && !category.isCanManager(memberId, orgAccountId)){
				templeteCategories.remove(category);
				i--;
			}
		}
		category2HTML(templeteCategories, categoryHTML, new Long(categoryType), 1);
		
		modelAndView.addObject("categoryHTML", categoryHTML);
		
        modelAndView.addObject("summary", summary);
        modelAndView.addObject("body", body);
		
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        
        Metadata deadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
        Metadata remindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        
        modelAndView.addObject("remindMetadata", remindMetadata);
        modelAndView.addObject("deadlineMetadata", deadlineMetadata); 
        
        modelAndView.addObject("controller", "edocController.do");
        //modelAndView.addObject("edocType",edocType);
        
        String appName="sendEdoc";
        if(iEdocType==1){appName="recEdoc";}
        else if(iEdocType==2){appName="signReport";}
        modelAndView.addObject("appName",appName);     
        
        EdocFormModel fm=edocFormManager.getEdocFormModel(edocFormId,summary,-1,true,false);
        fm.setDeadline(summary.getDeadline());
        
        modelAndView.addObject("formModel",fm);
        modelAndView.addObject("templateType",templateType);        
        
        //分支 开始
        //request.getSession().setAttribute("SessionObject", edocFormManager.getElementByEdocForm(defaultEdocForm));
        request.getSession().setAttribute("SessionObject", edocFormManager.getElementByEdocForm(edocFormManager.getEdocForm(defaultEdocForm.getId())));
        //分支 结束
		
		return modelAndView;
	}

	private static StringBuffer category2HTML(List<TempleteCategory> categories, 
			StringBuffer categoryHTML, Long currentNode, int level){
		for (TempleteCategory category : categories) {
			Long parentId = category.getParentId();
			if(parentId == currentNode || (parentId != null && parentId.equals(currentNode))){
				
				categoryHTML.append("<option value='" + category.getId() + "'>");
				
				for (int i = 0; i < level; i++) {
					categoryHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				}
				
				categoryHTML.append(Strings.toHTML(category.getName()) + "</option>\n");
				
				category2HTML(categories, categoryHTML, category.getId(), level + 1);
			}
		}
		
		return categoryHTML;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView systemDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/templete/systemDetail");
				
		return modelAndView;
	}
	
	
	/**
	 * 点击察看详细内容 - 管理员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView systemSummary(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/templete/systemSummary");
		Long templeteId = Long.parseLong(request.getParameter("templeteId"));
		
		Templete templete = this.templeteManager.get(templeteId);
		
		//EdocSummary summary = new EdocSummary();
		//summary.setSubject(templete.getSubject());
		
		EdocSummary summary = (EdocSummary) XMLCoder.decoder(templete.getSummary());
		
		modelAndView.addObject("summary", summary);
		modelAndView.addObject("templete", templete);
		
		try{
	        Metadata remindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
	        Metadata  deadlineMetadata= metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
	        String remindLabel=remindMetadata.getItemLabel(summary.getAdvanceRemind().toString());
	        String deallineLabel=deadlineMetadata.getItemLabel(summary.getDeadline().toString());
	        String bounder="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";        
	        modelAndView.addObject("deallineLabel", ResourceBundleUtil.getString(bounder,deallineLabel));        
	        bounder="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	        modelAndView.addObject("remindLabel", ResourceBundleUtil.getString(bounder,remindLabel));        
	    }catch(Exception e)
	    {
	        	
	    }
		
		try {
			V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, templete.getMemberId());
			modelAndView.addObject("member", member);
		}
		catch (Exception e) {
			log.error("", e);
		}
		
        modelAndView.addObject("attachments", attachmentManager.getByReference(templeteId));
        modelAndView.addObject("canDeleteOriginalAtts", true);    //允许删除原附件        
        modelAndView.addObject("archiveName", edocManager.getShowArchiveNameByArchiveId(summary.getArchiveId()));
        modelAndView.addObject("fullArchiveName", edocManager.getFullArchiveNameByArchiveId(summary.getArchiveId()));
      
		return modelAndView;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView systemTopic(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView modelAndView = new ModelAndView("edoc/templete/systemTopic");
		
        String logoURL = EdocHelper.getLogoURL();
        modelAndView.addObject("logoURL", logoURL);
		
		Long templeteId = Long.parseLong(request.getParameter("templeteId"));
		
		Templete templete = this.templeteManager.get(templeteId);
		
		EdocSummary summary = (EdocSummary) XMLCoder.decoder(templete.getSummary());

		EdocHelper.reLoadAccountName(summary,EdocHelper.getI18nSeperator(request));
		if(!Templete.Type.workflow.name().equals(templete.getType()))
		{
			EdocBody body = (EdocBody) XMLCoder.decoder(templete.getBody());
			summary.getEdocBodies().add(body);
		}
		
		Long formId=summary.getFormId();
		if(formId==null)
		{
			EdocForm defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),summary.getEdocType());
			formId=defaultEdocForm.getId();			
		}
		EdocFormModel fm=null;
		if(formId!=null){fm=edocFormManager.getEdocFormModel(formId,summary,-1);}
		if(formId==null || fm==null)
		{
			String szJs="<script>alert(\""+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_nofind_edocForm")+"\");</script>";
        	response.getWriter().print(szJs);
			return null;
		}
		if(!Templete.Type.workflow.name().equals(templete.getType()))
		{
			fm.setEdocBody(summary.getFirstBody());
		}                
        fm.setSenderOpinion(summary.getSenderOpinion());
        modelAndView.addObject("formModel",fm);
		
		//modelAndView.addObject("body", body);
		if(summary != null){
			//modelAndView.addObject("senderOpinion", summary.getSenderOpinion());//发起人附言
		}
		
		return modelAndView;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView systemWorkflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/templete/systemWorkflow");
		Long templeteId = Long.parseLong(request.getParameter("templeteId"));
		
		Templete templete = this.templeteManager.get(templeteId);
		
        String caseProcessXML = templete.getWorkflow();
		
        caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
        caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);
		
		modelAndView.addObject("hasDiagram", "true");
		modelAndView.addObject("caseProcessXML", caseProcessXML);
        
		EdocSummary summary = (EdocSummary) XMLCoder.decoder(templete.getSummary());
		modelAndView.addObject("summary", summary);
		
		if(!Templete.Type.workflow.name().equals(templete.getType()))
		{
			EdocBody body = (EdocBody) XMLCoder.decoder(templete.getBody());
			modelAndView.addObject("contentType", body.getContentType());
		}
		
		Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		modelAndView.addObject("comMetadata", comMetadata);
		modelAndView.addObject("isShowButton", false);
		modelAndView.addObject("appName", EdocEnum.getEdocAppName(summary.getEdocType()));
		
		modelAndView.addObject("edocContentType", templete.getType());
		try{
			//分支 开始
			User user = CurrentUser.get();
			List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.edoc.ordinal());
	        modelAndView.addObject("branchs", branchs);
	        if(branchs != null) {
	        	modelAndView.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
	        	V3xOrgMember mem = orgManager.getMemberById(user.getId());
	        	List<MemberPost> secondPosts = mem.getSecond_post();
	        	modelAndView.addObject("secondPosts", secondPosts);
	        }
		}catch(Exception e)
		{
			log.error(e.getMessage());
		}
	    //分支 结束
		return modelAndView;
	}
	
	/**
	 * 模板详情,普通用户,模版调用,预留
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/templete/detail");

        String logoURL = EdocHelper.getLogoURL();
        modelAndView.addObject("logoURL", logoURL);
		
		Long id = new Long(request.getParameter("id"));

		Templete templete = templeteManager.get(id);
		if (templete == null) {
			return modelAndView;
		}

		modelAndView.addObject("templete", templete);

		String type = templete.getType();

		// 正文格式不显示流程
		if (!Templete.Type.text.name().equals(type)) {
            BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
            String caseProcessXML = process.toXML(); //重新生成，因为要取新的节点名称
			
			List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);
			
			Metadata nodePermissionPolicy = null;//;metadataManager.getMetadata(MetadataNameEnum.col_flow_perm_policy);
			if(templete.getCategoryType()==TempleteCategory.TYPE.edoc_send.ordinal())
			{
				nodePermissionPolicy=metadataManager.getMetadata(MetadataNameEnum.edoc_send_permission_policy);
			}
			else if(templete.getCategoryType()==TempleteCategory.TYPE.edoc_rec.ordinal())
			{
				nodePermissionPolicy=metadataManager.getMetadata(MetadataNameEnum.edoc_rec_permission_policy);
			}
			else if(templete.getCategoryType()==TempleteCategory.TYPE.sginReport.ordinal())
			{
				nodePermissionPolicy=metadataManager.getMetadata(MetadataNameEnum.edoc_qianbao_permission_policy);
			}
	        caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
	        caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);
	        
	        modelAndView.addObject("workflowInfo", workflowInfo);
			modelAndView.addObject("workflow", caseProcessXML);
			modelAndView.addObject("nodePermissionPolicy", nodePermissionPolicy);
		}
		
		EdocSummary summary = (EdocSummary) XMLCoder.decoder(templete.getSummary());
		EdocHelper.reLoadAccountName(summary,EdocHelper.getI18nSeperator(request));
		// 流程模板不保存正文
		if (!Templete.Type.workflow.name().equals(type)) {
			EdocBody body = (EdocBody) XMLCoder.decoder(templete.getBody());	
			summary.getEdocBodies().add(body);
			summary.setTempleteId(id);
			EdocFormModel fm=edocFormManager.getEdocFormModel(summary.getFormId(),summary,-1,false,true);
			fm.setEdocBody(summary.getFirstBody());
			modelAndView.addObject("formModel",fm);
		}
		
		//分枝 开始
		modelAndView.addObject("branchs", this.templeteManager.getBranchsByTemplateId(id,ApplicationCategoryEnum.edoc.ordinal()));		
		//分枝 结束
		
		//督办信息
		if(Templete.Type.templete.name().equals(type)) {
			ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),id);
			if(detail != null) {
				User user = CurrentUser.get();
				Long terminalDate = detail.getTemplateDateTerminal();
            	if(null!=terminalDate){
            		Date superviseDate = Datetimes.addDate(new Date(), terminalDate.intValue());
            		String date = Datetimes.formatDate(superviseDate);
            		detail.setAwakeDate(Datetimes.parseDate(date));
            		modelAndView.addObject("superviseDate", date);
            	}
            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
            	Set<String> sIdSet = new HashSet<String>();
            	StringBuffer ids = new StringBuffer();
            	StringBuffer names = new StringBuffer();
            	
            	for(ColSupervisor supervisor:supervisors){
            		sIdSet.add(supervisor.getSupervisorId().toString());
            	}
            	List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(templete.getId());
            	if((null!=roleList && !roleList.isEmpty()) || !sIdSet.isEmpty()){
            		modelAndView.addObject("sVisorsFromTemplate", "true");//公文调用的督办模板是否设置了督办人
            	}
            	V3xOrgRole orgRole = null;
            	
            	for(SuperviseTemplateRole role : roleList){
            		if(null==role.getRole() || "".equals(role.getRole())){
            			continue;
            		}
            		if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase())){
            			sIdSet.add(String.valueOf(user.getId()));
            		}
            		boolean haveManager = false;
            		

            		
            		if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())||role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())){
            			
            			orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, user.getLoginAccount());
            			if(null!=orgRole){
            			List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(user.getId());
            			for(V3xOrgDepartment dep : depList){
            				List<V3xOrgMember> managerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
            				for(V3xOrgMember mem : managerList){
            					haveManager = true;
                				sIdSet.add(mem.getId().toString());
                			}
            			}
            			}

            		}
            		else
            		{
            			modelAndView.addObject("isOnlySender", "true");
            		}
            		if(!haveManager){
            			modelAndView.addObject("noDepManager", "true");
            		}

            		if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER.toLowerCase())){
            			orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER, user.getLoginAccount());
            			if(null!=orgRole){
            			List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(user.getId());
            			for(V3xOrgDepartment dep : depList){
            			List<V3xOrgMember> superManagerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
               				for(V3xOrgMember mem : superManagerList){
               					sIdSet.add(mem.getId().toString());
               				}
            			}
            			}
            		}	
            	}
            	
            	for(String s : sIdSet){
            		V3xOrgMember mem = orgManager.getMemberById(Long.valueOf(s));
            		if(mem!=null){
            		ids.append(mem.getId());
            		ids.append(",");
            		names.append(mem.getName());
            		names.append(",");
            		}
            	}
            	
            	if(ids.length()>1 && names.length()>1){
            		modelAndView.addObject("colSupervisors", ids.substring(0, ids.length()-1));
            		detail.setSupervisors(names.substring(0, names.length()-1));
            	}
            	modelAndView.addObject("colSupervise", detail);
			}
		}
		return modelAndView;
	}
	
    private void saveColSuperviseForTemplate(HttpServletRequest request,HttpServletResponse response,Templete template,boolean isNew,boolean sendMessage) {
		String supervisorId = request.getParameter("supervisorId");
        String supervisors = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
        String role = request.getParameter("superviseRole");
        if(StringUtils.isBlank(supervisorId)){
        	supervisorId = "";
        }
        //if((supervisorId != null && !"".equals(supervisorId))||(role!=null && !"".equals(role)) && awakeDate != null && !"".equals(awakeDate)) {
    	User user = CurrentUser.get();
        //boolean canModifyAwake = "on".equals(request.getParameter("canModifyAwake"))?true:false;
        String superviseTitle = request.getParameter("superviseTitle");
        //Date date = Datetimes.parse(awakeDate, Datetimes.dateStyle);
        String[] idsStr = supervisorId.split(",");
        long[] ids = new long[0];
        if(Strings.isNotBlank(supervisorId)){
        	ids = new long[idsStr.length];
	        int i = 0;
	        for(String id:idsStr) {
	        	ids[i] = Long.parseLong(id);
	        	i++;
	        }
        }
        //重要程度
        int importantLevel = 1;
        Long awakeDat = 0L;
        if(Strings.isNotBlank(awakeDate)) awakeDat = Long.valueOf(awakeDate);
        if(isNew){
        	if((supervisorId != null && !"".equals(supervisorId))||(role!=null && !"".equals(role)) && awakeDate != null && !"".equals(awakeDate)) {
        		this.colSuperviseManager.saveForTemplate(importantLevel, template.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids,awakeDat, Constant.superviseType.template.ordinal(), template.getId(),sendMessage);
        		this.colSuperviseManager.saveSuperviseTemplateRole(template.getId(), role);
        	}
        }
        else{
        	this.colSuperviseManager.updateForTemplate(importantLevel, template.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, awakeDat, Constant.superviseType.template.ordinal(), template.getId(),sendMessage);
        	this.colSuperviseManager.updateSuperviseTemplateRole(template.getId(), role);
        }
	       // }
    }

	/**
	 * @param colSuperviseManager the colSuperviseManager to set
	 */
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}

    public ModelAndView showBranchDesc(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/templete/moreCondition");
    	String linkId = request.getParameter("linkId");
    	String templateId = request.getParameter("templateId");
    	ColBranch branch = null;
    	if(templateId != null && linkId != null) {
    		branch = this.templeteManager.getBranchByTemplateAndLink(ApplicationCategoryEnum.edoc.ordinal(), Long.parseLong(templateId), Long.parseLong(linkId));
    	}
    	if(branch != null) {
    		if(branch.getConditionType()!=2)
    			mv.addObject("desc", branch.getConditionDesc());
    		else
    			mv.addObject("desc", Constant.getString4CurrentUser("templete.branch.handOption"));
    	}
    	return mv;
    }
}
