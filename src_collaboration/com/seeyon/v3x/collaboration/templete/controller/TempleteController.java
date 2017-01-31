/**
 *
 */
package com.seeyon.v3x.collaboration.templete.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.joinwork.bpm.definition.BPMProcess;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TemplateObject;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.IIP_InputObject;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.InfoPath_Inputtypedefine;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputRadio;
import www.seeyon.com.v3x.form.engine.infopath.inputtypedefine.TIP_InputSelect;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.bind.flow.FlowTempletImp;
import www.seeyon.com.v3x.form.manager.define.form.relationInput.HrStaffInfoField;
import www.seeyon.com.v3x.form.utils.BindHelper;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.FormBody;
import com.seeyon.v3x.collaboration.domain.FormContent;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.templete.TempleteCfgCategory;
import com.seeyon.v3x.collaboration.templete.TempleteUtil;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.domain.TempleteConfig;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.controller.GenericController;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.main.section.SectionUtils;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-12-25
 */
public class TempleteController extends BaseController {

	private static Log log = LogFactory.getLog(TempleteController.class);

	private TempleteManager templeteManager;

	private AttachmentManager attachmentManager;

	private TempleteCategoryManager templeteCategoryManager;

	private OrgManager orgManager;

	private MetadataManager metadataManager;

	private DocHierarchyManager docHierarchyManager;

    private TempleteConfigManager templeteConfigManager;

    private ProjectManager projectManager;

    private ColSuperviseManager colSuperviseManager;

    private AffairManager affairManager;

    private NewflowManager newflowManager;

    private AppLogManager appLogManager;

    private PortletEntityPropertyManager portletEntityPropertyManager;
    
    private WorkFlowAnalysisAclManager workFlowAnalysisAclManager;

    public void setWorkFlowAnalysisAclManager(
			WorkFlowAnalysisAclManager workFlowAnalysisAclManager) {
		this.workFlowAnalysisAclManager = workFlowAnalysisAclManager;
	}

	public void setPortletEntityPropertyManager(
			PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

    public void setNewflowManager(NewflowManager newflowManager) {
        this.newflowManager = newflowManager;
    }

	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
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

    public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager)
	{
	    this.templeteConfigManager = templeteConfigManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }

    public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	/**
	 * 系统模板保存 - 新建/修改
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemSaveTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();

		String type = request.getParameter("type");
		String from = request.getParameter("from");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));

		Timestamp createDate = new Timestamp(System.currentTimeMillis());
		Templete templete = new Templete();
		bind(request, templete);

		// 正文格式不保存流程
		if (!Templete.Type.text.name().equals(type)) {
			templete.setWorkflow(this.doColWorkflow(request, true));
		}

		// 流程模板不保存正文
		if (!Templete.Type.workflow.name().equals(type)) {
			String body = this.doColBody(request);
			templete.setBody(body);
		}

		//属性
		String summary = null;
		ColSummary colSummary = new ColSummary();
		colSummary.setSubject(templete.getSubject());

		// 流程模版保存允许修改流程
		if (Templete.Type.workflow.name().equals(type)) {
			if (Strings.isNotBlank(request.getParameter("canModify")))
				colSummary.setCanModify(true);//改变流程
			if (Strings.isNotBlank(request.getParameter("canForward")))
				colSummary.setCanForward(true);//转发
			if (Strings.isNotBlank(request.getParameter("canEdit")))
				colSummary.setCanEdit(true);//修改正文
			if (Strings.isNotBlank(request.getParameter("canEditAttachment")))
				colSummary.setCanEditAttachment(true);//修改附件
			if (Strings.isNotBlank(request.getParameter("canArchive")))
				colSummary.setCanArchive(true);//归档
			summary = XMLCoder.encoder(colSummary);
		} else if (Templete.Type.text.name().equals(type)) {
			if (Strings.isNotBlank(request.getParameter("canForward")))
				colSummary.setCanForward(true);
			if (Strings.isNotBlank(request.getParameter("canEdit")))
				colSummary.setCanEdit(true);
			if (Strings.isNotBlank(request.getParameter("canEditAttachment")))
				colSummary.setCanEditAttachment(true);
			if (Strings.isNotBlank(request.getParameter("canArchive")))
				colSummary.setCanArchive(true);
			summary = XMLCoder.encoder(colSummary);
		} else if (Templete.Type.templete.name().equals(type)) {
			summary = this.doColSummary(request);
		}
		
		// 基准时间
		String referenceTime = request.getParameter("referenceTime");
		if (Strings.isBlank(referenceTime)) 
			templete.setStandardDuration(0);
		else
			templete.setStandardDuration(Integer.parseInt(referenceTime));
		
		templete.setSummary(summary);

		templete.setCreateDate(createDate);
		templete.setMemberId(user.getId());
		templete.setIsSystem(true);
		templete.setOrgAccountId(user.getLoginAccount());

		boolean isSave = templete.isNew();
		templete.setIdIfNew();

		//外部系统调用流程发起扩展属性(模板编号)
        String templeteNumber = request.getParameter("templeteNumber");
        if(Strings.isNotBlank(templeteNumber)){
            templete.setTempleteNumber(templeteNumber);
        }

		long templeteId = templete.getId();

		//将当前模板推送到首页-我的模板
        List<Long> authMemberIdsList = new ArrayList<Long>();

		//授权信息
		String authInfo = request.getParameter("authInfo");
		String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
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

				//需要推送模板的人员，包括兼职
				Set<Long> memberIdsSet = Functions.getAllMembersId(auth.getAuthType(), auth.getAuthId());
				if(memberIdsSet != null && !memberIdsSet.isEmpty()){
					authMemberIdsList.addAll(memberIdsSet);
				}
			}
		}

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
        			tmps = tmp.split("↗");
        			if(tmps != null) {
        				ColBranch branch = new ColBranch();
        				branch.setTemplateId(templeteId);
        				branch.setLinkId(Long.parseLong(tmps[0]));
        				branch.setId(Long.parseLong(tmps[1]));
        				branch.setConditionType(Integer.parseInt(tmps[2]));
        				branch.setFormCondition(tmps[3]);
        				branch.setConditionTitle(tmps[4]);
        				branch.setIsForce("".equals(tmps[5])||"0".equals(tmps[5])?0:1);
        				if(tmps.length>6) {
	        				branch.setConditionDesc("".equals(tmps[6])||"null".equals(tmps[6])?null:tmps[6]);
	        				if(tmps.length>7)
	        					branch.setConditionBase(tmps[7]);
        				}
        				branch.setAppType(ApplicationCategoryEnum.collaboration.ordinal());
        				list.add(branch);
        			}
        		}
        	}
//        	if(list.size()>0)
//        		this.templeteManager.saveBranch(templeteId,list);
        }
        this.templeteManager.saveBranch(templeteId,list);

		if(!isSave){
			// 删除原有附件
			this.attachmentManager.deleteByReference(templeteId);
		}

		// 保存附件
		String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, templeteId, templeteId, request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	templete.setHasAttachments(true);
        }

		if (isSave) { //新建
			this.saveColSupervise(request, response, templete, isSave, false);
			templeteManager.save(templete);
			appLogManager.insertLog(user, AppLogAction.Coll_Template_Create, user.getName(), templete.getSubject());
		}
		else { // 修改
			this.saveColSupervise(request, response, templete, false, false);
			this.templeteManager.update(templete);
			appLogManager.insertLog(user, AppLogAction.Coll_Template_Edit, user.getName(), templete.getSubject());
		}

		templeteConfigManager.pushThisTempleteToMain4Members(authMemberIdsList, templeteId, 0);

        PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("try {parent.getA8Top().endProc();}catch(e) {}");
		out.println("</script>");

		return super.redirectModelAndView("/genericController.do?ViewPage=collaboration/templete/systemIndex&categoryType=" + categoryType + "&categoryId=" + templete.getCategoryId() + "&from=" + from);
	}

	/**
	 * 个人模板 保存
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();

		String type = request.getParameter("type");
		Long overId = -1L;
		Long formparentid = null;
		String bodytype = request.getParameter("tembodyType");
		if(Strings.isNotBlank(request.getParameter("overId"))){
			overId = Long.parseLong(request.getParameter("overId"));
		}

		Timestamp createDate = new Timestamp(System.currentTimeMillis());

		Templete templete = null;
		if(overId != -1){
			templete = templeteManager.get(overId);
		}

		if(templete == null){
			templete = new Templete();
			bind(request, templete);
			templete.setId(overId);
		}

		templete.setSubject(request.getParameter("saveAsTempleteSubject"));
		String invokeTempleteId = request.getParameter("templeteId");
		Templete parentTemplete =null;
		if(StringUtils.isNotBlank(invokeTempleteId)){
			 parentTemplete = templeteManager.get(Long.parseLong(invokeTempleteId));
			//templete.setId(Long.parseLong(request.getParameter("templeteId")));
			//表单个人模版的父id，当调用表单模版时该id为空，调用表单模版另存个人模版后再次另存页面没有刷新该id为空，当调用表单个人模版是该id不为空。
			if(!"".equals(request.getParameter("temformParentId")) && !"null".equals(request.getParameter("temformParentId"))&& request.getParameter("temformParentId") !=null){
				formparentid = Long.parseLong(request.getParameter("temformParentId"));
				templete.setFormParentId(formparentid);
			}else {
				//如果是表单需要保存parent，或者是系统模板用于督办
				if("FORM".equalsIgnoreCase(bodytype) || parentTemplete.getIsSystem())
					templete.setFormParentId(Long.parseLong(invokeTempleteId));
			}
			//当调用表单模版进行另存个人模版时，父id和overId都为空，此时表单模版id即为个人模版父id
			/*if("FORM".equalsIgnoreCase(bodytype) && formparentid ==null)
				templete.setFormParentId(Long.parseLong(request.getParameter("templeteId")));*/
            //当调用表单模版进行另存个人模版后不离开进行再次点击另存，此时父id仍为空，表单模版id即为个人模版父id，overId为个人模版id
			/*if("FORM".equalsIgnoreCase(bodytype) && formparentid ==null && overId !=-1L){
				templete.setFormParentId(Long.parseLong(request.getParameter("templeteId")));
				templete.setId(overId);
			}	*/
			//	overId等于-1即为新的个人模版，不等于-1即为覆盖原模版，原模版id
			/*if("FORM".equalsIgnoreCase(bodytype) && overId ==-1L){
	        	templete.setId(UUIDLong.longUUID())	;
	        }*/
		}

		// 正文格式不保存流程
		if (!Templete.Type.text.name().equals(type)) {
			templete.setWorkflow(this.doColWorkflow(request, false));
		}

		// 流程模板不保存正文
		if (!Templete.Type.workflow.name().equals(type)) {
			String body = this.doColBody(request);
			templete.setBody(body);
		}
		if("FORM".equalsIgnoreCase(bodytype)){
	        String formData = request.getParameter("formData");
	        templete.setBody(formData);
	    }
		//属性
		String summary = this.doColSummary(request);
		//属性保留，去掉判断
		/*if (Templete.Type.templete.name().equals(type)) {
			summary = this.doColSummary(request);
		}
		else{
			ColSummary colSummary = new ColSummary();
			colSummary.setSubject(templete.getSubject());

			summary = XMLCoder.encoder(colSummary);
		}*/

		boolean isSave = templete.isNew();
		if("FORM".equalsIgnoreCase(bodytype) && overId ==-1L)
			isSave = true;
		templete.setIdIfNew();

		long templeteId = templete.getId();

		templete.setSummary(summary);
		templete.setCreateDate(createDate);
		templete.setMemberId(user.getId());
		templete.setIsSystem(false);
		templete.setOrgAccountId(user.getLoginAccount());

		if(!isSave && Templete.Type.templete.name().equals(type)){
			// 删除原有附件
			this.attachmentManager.deleteByReference(templeteId);
		}

		// 保存附件
		if(Templete.Type.templete.name().equals(type)){
			String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, templeteId, templeteId, request);
	        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
	        	templete.setHasAttachments(true);
	        }
		}
		//另存表单个人模版时先判断父模版id是否存在
        if(templete.getFormParentId() !=null){
        	if(parentTemplete != null)
        	  templete.setOrgAccountId(parentTemplete.getOrgAccountId());
        	if("".equals(invokeTempleteId) || "null".equals(invokeTempleteId) || invokeTempleteId == null)
        		parentTemplete = templeteManager.get(templete.getFormParentId());
        	if(parentTemplete ==null){
        		PrintWriter out = response.getWriter();
        		out.println("<script>");
        		out.println("try {parent.getA8Top().endProc();}catch(e) {}");
        		out.println("alert(parent._('collaborationLang.templete_notsavePersonalSuccess'))");
        		out.println("</script>");
        		return null;
        	}else
        		templete.setOrgAccountId(parentTemplete.getOrgAccountId());
        }

		if (isSave) { //新建
			templeteManager.save(templete);
			//将当前模板推送到首页
			templeteConfigManager.pushThisTempleteToMain4Member(templete.getMemberId(), templeteId, -1);
		}
		else { // 修改
			this.templeteManager.update(templete);
		}

		if(StringUtils.isBlank(invokeTempleteId)||templete.getFormParentId()==null) {
			this.saveColSupervise(request, response, templete.getSubject(), templete.getId(), isSave, Constant.superviseState.supervising.ordinal(), false);
		}

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("try {parent.getA8Top().endProc();}catch(e) {}");
		out.println("alert(parent._('collaborationLang.templete_savePersonalSuccess'))");
		out.println("</script>");

		return null;
	}

	/**
	 * 生成正文内容
	 * @param request
	 * @param createDate
	 * @return
	 * @throws Exception
	 */
	private String doColBody(HttpServletRequest request) throws Exception{
		ColBody colBody = new ColBody();
		bind(request, colBody);
		Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
		if (bodyCreateDate != null) {
			colBody.setCreateDate(bodyCreateDate);
		}

		return XMLCoder.encoder(colBody);
	}

	/**
	 * 生成流程内容
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String doColWorkflow(HttpServletRequest request, boolean isSystem) throws Exception{
		FlowData flowData = FlowData.flowdataFromRequest();
		//更新自定义节点的引用
        String[] policys = request.getParameterValues("policys");
        String[] itemNames = request.getParameterValues("itemNames");
        ColHelper.setPolicy(policys, itemNames, flowData);
		String processId = ColHelper.saveOrUpdateProcessByFlowData(
				flowData, null, isSystem);
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
		ColSummary colSummary = new ColSummary();
		bind(request, colSummary);
		colSummary.setId(null);

		colSummary.setCanForward(request.getParameterValues("canForward") != null);
		colSummary.setCanArchive(request.getParameterValues("canArchive") != null);
		colSummary.setCanDueReminder(request.getParameterValues("canDueReminder") != null);
		colSummary.setCanModify(request.getParameterValues("canModify") != null);
		colSummary.setCanTrack(request.getParameterValues("canTrack") != null);
		colSummary.setCanEdit(request.getParameterValues("canEdit") != null);

		String note = request.getParameter("note");// 发起人附言
		// 附言内容为空，就不记录了
//		if (StringUtils.isNotBlank(note)) {
			ColOpinion senderOpinion = new ColOpinion();
			senderOpinion.setContent(note);
			senderOpinion.setOpinionType(ColOpinion.OpinionType.senderOpinion);
			senderOpinion.affairIsTrack = request.getParameterValues("isTrack") != null;

			colSummary.getOpinions().add(senderOpinion);
//		}

		return XMLCoder.encoder(colSummary);
	}

	/**
	 * 新建/修改
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemNewTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("collaboration/templete/systemNewTemplete");
		String from = request.getParameter("from");
		//Integer categoryType = Integer.parseInt(request.getParameter("categoryType"));
		User user = CurrentUser.get();

		Long orgAccountId = user.getLoginAccount();

		String templeteId = request.getParameter("templeteId");
		long memberId = user.getId();

		ColSummary summary = null;
		ColBody body = null;

		if(StringUtils.isNotBlank(templeteId)){ //修改
			Templete templete = this.templeteManager.get(Long.parseLong(templeteId));
			if(templete == null) {
			    String alertTip = Constant.getString("col_template_deleted");
			    PrintWriter out = response.getWriter();
			    out.println("<script>");
			    out.println("alert('" + alertTip + "');");
			    out.println("</script>");
			    out.flush();
			    return super.refreshWorkspace();
			}
			summary = (ColSummary) XMLCoder.decoder(templete.getSummary());
			body = (ColBody) XMLCoder.decoder(templete.getBody());
			String caseProcessXML = null;
			BPMProcess process = null;
			if (!templete.getType().equals("text")) {
				process = BPMProcess.fromXML(templete.getWorkflow());
				caseProcessXML = process.toXML(); // 重新生成，因为要取新的节点名称
			}

			if(StringUtils.isNotBlank(caseProcessXML)){
	            modelAndView.addObject("hasWorkflow", Boolean.TRUE);
	            modelAndView.addObject("process_desc_by", FlowData.DESC_BY_XML);

	            caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);

	            List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);

	            caseProcessXML = Strings.escapeJavascript(caseProcessXML);

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
	        modelAndView.addObject("archiveName", archiveName);

            modelAndView.addObject("attachments", attachmentManager
                    .getByReference(templete.getId(), templete.getId()));
            modelAndView.addObject("canDeleteOriginalAtts", true);    //允许删除原附件

            modelAndView.addObject("note", summary.getSenderOpinion());//发起人附言

            modelAndView.addObject("templete", templete);

            modelAndView.addObject("branchs", this.templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.collaboration.ordinal()));
            
            modelAndView.addObject("secret", summary.getSecretLevel());//2012-8-30 成发集团项目 程炯 模版修改时显示保存的密级级别

            ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),templete.getId());
            if(detail != null) {
            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
            	if(supervisors != null && supervisors.size()>0) {
	            	StringBuffer ids = new StringBuffer();
	            	for(ColSupervisor supervisor:supervisors)
	            		ids.append(supervisor.getSupervisorId() + ",");
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
            appLogManager.insertLog(user, AppLogAction.Coll_Template_Edit, user.getName(), templete.getSubject());
		}
		else { //直接新建
            summary = new ColSummary();
            body = new ColBody();

            summary.setCanForward(true);
            summary.setCanArchive(true);
            summary.setCanDueReminder(true);
            summary.setCanModify(true);
            summary.setCanEditAttachment(true);
            summary.setCanTrack(true);
            summary.setCanEdit(true);
        }

		List<TempleteCategory> templeteCategories = this.templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		templeteCategories.addAll(templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.form.ordinal()));
		StringBuffer categoryHTML = new StringBuffer();

		for (int i = 0; i < templeteCategories.size(); i++) {
			TempleteCategory category = templeteCategories.get(i);
			if(!"SYS".equalsIgnoreCase(from) && TempleteUtil.isClass1Category(category) && !category.isCanManager(memberId, orgAccountId)){
				templeteCategories.remove(category);
				i--;
			}
		}
		List<Long> categoryTypes = new ArrayList<Long>();
		categoryTypes.add(new Long(TempleteCategory.TYPE.collaboration_templete.ordinal()));
		categoryTypes.add(new Long(TempleteCategory.TYPE.form.ordinal()));
		category2HTML(templeteCategories, categoryHTML, categoryTypes, 1);

		modelAndView.addObject("categoryHTML", categoryHTML);

        modelAndView.addObject("summary", summary);
        modelAndView.addObject("body", body);

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        Metadata colFlowPermPolicyMetadata = colMetadata.get(MetadataNameEnum.col_flow_perm_policy.name());//单独传递，免得它以后改名

        modelAndView.addObject("colFlowPermPolicyMetadata", colFlowPermPolicyMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("comMetadata", comMetadata);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);

		List<ProjectSummary> projectList = projectManager.getProjectList(user, user.getAccountId());
		modelAndView.addObject("relevancyProject", projectList);

		return modelAndView;
	}

	/**
	 * 删除模板
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView deleteTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String categoryId = request.getParameter("categoryId");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		String from = request.getParameter("from");
		boolean isColTemplete=true;
		if(categoryType==TempleteCategory.TYPE.edoc.ordinal()||categoryType==TempleteCategory.TYPE.edoc_rec.ordinal()||categoryType==TempleteCategory.TYPE.edoc_send.ordinal()){
			isColTemplete=false;
		}
		String[] ids = request.getParameterValues("id");
		if(ids != null){
			User user = CurrentUser.get();
			for (String string : ids) {
				Long id = new Long(string);
                boolean isCanDelete = affairManager.checkTempleteIsCanDelete(id,isColTemplete);
                String templeteName = "";
                if(isCanDelete){
                    Templete templete = templeteManager.get(id);
                    if(templete != null){
                        templeteName = templete.getSubject();
                    }
                    this.templeteManager.delete(id);
                    //删除模板的时候清空首页我的模板配置
                    templeteConfigManager.clearConfigByTempleteId(id);
                    if(!isColTemplete){
                        appLogManager.insertLog(user, AppLogAction.Edoc_Templete_Delete, user.getName(), templeteName);
                    }else{
                        appLogManager.insertLog(user, AppLogAction.Coll_Template_Delete, user.getName(), templeteName);
                    }
                }
                else{
                    //提示不能删除
                    Templete templete = templeteManager.get(id);
                    if(templete != null){
                        templeteName = templete.getSubject();
                    }
                    String alertTip = Constant.getString("templete.cannotDelete.label", templeteName);
                    PrintWriter out = response.getWriter();
                    out.println("<script>");
                    out.println("alert('" + alertTip + "');");
                    out.println("</script>");
                    out.flush();
                    return super.refreshWorkspace();
                }
			}
		}

		return redirectModelAndView("/templete.do?method=systemList&categoryId=" + categoryId + "&categoryType=" + categoryType + "&from=" + from);
	}


	public boolean deleteMyTemplete(String tId, int categoryId, int categoryType) {
			long id = Long.parseLong(tId);
			boolean isColTemplete=true;
			if(categoryType==TempleteCategory.TYPE.edoc.ordinal()||categoryType==TempleteCategory.TYPE.edoc_rec.ordinal()||categoryType==TempleteCategory.TYPE.edoc_send.ordinal()){
				isColTemplete=false;
			}
            boolean isCanDelete = affairManager.checkTempleteIsCanDelete(id,isColTemplete);
            String templeteName = "";
            if(isCanDelete){
                Templete templete = templeteManager.get(id);
                if(templete != null){
                    templeteName = templete.getSubject();
                }
                this.templeteManager.delete(id);

            }
            return isCanDelete;


	}

	/**
	 * 列表,用在模板调用的tree
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView tree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		/**
		 * collaboration_templete, //协同模板
		 * edoc, //公文模板
		 * edoc_send, //发文模板
		 * edoc_rec, //收文模板
		 * form,      //表单模板
		 * sginReport //签报模版
		 */
		ModelAndView modelAndView = new ModelAndView("collaboration/templete/tree");

		String category = request.getParameter("categoryType");
		//是否是外来文登记
		String subType=request.getParameter("subType");
		//外来文登记的时候传入待登记的公文的所属单位ID,不一定是登陆单位，所以需要单独传入，后续可扩展，其他应用也可传人这个参数。
		String accountId = request.getParameter("accountId");
		if(category == null){
			return modelAndView;
		}
        User user = CurrentUser.get();
		Long orgAccountId = user.getLoginAccount();
		if("isExchangeDocToRegist".equals(subType) && Strings.isNotBlank(accountId)){
			orgAccountId=Long.parseLong(accountId);
		}
		String[] typestrs = StringUtils.split(category,",");
		Integer[] types = new Integer[typestrs.length];
		List<Integer> typesL = new ArrayList<Integer>();
		for (int i = 0; i < typestrs.length; i++) {
			types[i] = new Integer(typestrs[i]);
			typesL.add(types[i]);
		    /*if(types[i] == TempleteCategory.TYPE.form.ordinal()){
		    	modelAndView.addObject("templeteCategorysForm", templeteCategorys);
		    }
		    else{
		    	modelAndView.addObject("templeteCategorysColl", templeteCategorys);
		    }*/
		}
		List<TempleteCategory> templeteCategory = templeteCategoryManager.getCategorys(orgAccountId, typesL);
		Map<String,TempleteCategory> nameCategory = new HashMap<String,TempleteCategory>();
        for(TempleteCategory c:templeteCategory){
            nameCategory.put(c.getName(), c);
        }
        V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
        Collections.sort(templeteCategory);
	    modelAndView.addObject("collaborationCate", types[0]);
	    if(!"4".equals(typestrs[0])){//公文模板不含外单位
	    	boolean isGov = (Boolean)com.seeyon.v3x.common.flag.SysFlag.is_gov_only.getFlag();
	    	List<Templete> personalTempletes = null;
	    	if(isGov) {//政务版
	    		personalTempletes = templeteManager.getPersonalTemplete(types);
	    	} else {
	    		personalTempletes = templeteManager.getPersonalTemplete(member.getSecretLevel().toString());
	    	}    	
	    	modelAndView.addObject("personalTempletes", personalTempletes);
	    }

	    //所有模板，包括协同和表单
	    List<Templete> systemTempletes = templeteManager.getSystemTempletesByMemberId(user.getId(), null,member.getSecretLevel().toString(),types);
	    List<Templete> showTempletes = new  ArrayList<Templete>();
	    boolean isShowOuter = !"4".equals(typestrs[0])&&!"isExchangeDocToRegist".equals(subType)&& Boolean.valueOf(Functions.getSysFlag("col_showOtherAccountTemplate").toString());
	    List<TempleteCategory> outerCategory = new ArrayList<TempleteCategory>();
	    for (Templete templete : systemTempletes) {
			if(!templete.getOrgAccountId().equals(orgAccountId)){
				if(!isShowOuter) continue;
				//外单位模板
				if(templete.getCategoryId() != 0){//等于0是顶层的模板
					TempleteCategory tc = templeteCategoryManager.get(templete.getCategoryId());
					if(tc != null){
						TempleteCategory n = nameCategory.get(tc.getName());
						if(n != null){
							templete.setCategoryId(n.getId());
						}else{
							outerCategory.add(tc);
							nameCategory.put(tc.getName(), tc);
						}
					}
				}
			}
			showTempletes.add(templete);
		}

	    modelAndView.addObject("templeteCategory", templeteCategory);
	    modelAndView.addObject("outerCategory", outerCategory);
	    modelAndView.addObject("sysTempletes", showTempletes);
		return modelAndView;
	}


	/**
	 * 系统管理，树型结构
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"collaboration/templete/systemTree");

		int categoryType = Integer.parseInt(request.getParameter("categoryType"));

		User user = CurrentUser.get();

		long orgAccountId = user.getLoginAccount();
		List<TempleteCategory> templeteCategorys = null;
		if(categoryType == 0 || categoryType == 4){
			templeteCategorys = templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
			templeteCategorys.addAll( templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.form.ordinal()));
			//协同模板分类为0的时候，不能新建。
			modelAndView.addObject("canNew", templeteCategorys.size() >0 );
			//String move = request.getParameter("move");
			//if(!"true".equals(move)){//移动文件夹 不用展示所有的
			//}
		}else{
			templeteCategorys = templeteCategoryManager.getCategorys(orgAccountId, categoryType);
		}
		Collections.sort(templeteCategorys);

		long userId = user.getId();

		List<Long> canManagerC = new ArrayList<Long>();

		for (TempleteCategory category : templeteCategorys) {
			if(category.isCanManager(userId, orgAccountId)){
				canManagerC.add(category.getId());
			}
		}

		modelAndView.addObject("canManagerC", canManagerC);
		modelAndView.addObject("templeteCategorys", templeteCategorys);

		return modelAndView;
	}

	/**
	 * 单击某个分类，列表显示该分类下的所有的模板
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"collaboration/templete/systemList");
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");

		Long categoryId = null;

		String categoryIdStr = request.getParameter("categoryId");

		if(StringUtils.isNotBlank(categoryIdStr)){
			categoryId = Long.parseLong(categoryIdStr);
		}
		String categoryType=request.getParameter("categoryType");

		List<Templete> templetes = templeteManager.getAllSystemTempletes(categoryId,Integer.parseInt(categoryType), condition, textfield, textfield1);

		modelAndView.addObject("templetes", templetes);
		modelAndView.addObject("categoryType", categoryType);

		return modelAndView;
	}

	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
		"collaboration/templete/systemDetail");

		String templeteId = request.getParameter("templeteId");
		if(Strings.isNotBlank(templeteId)){
		    Templete templete = this.templeteManager.get(Long.parseLong(templeteId));
		    if(templete == null) {
			String alertTip = Constant.getString("col_template_deleted");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('" + alertTip + "');");
			out.println("</script>");
		    	out.flush();
		    	return super.refreshWorkspace();
		    }
		}
		return modelAndView;
	}

	/**
	 * 勾选多个模板，集体授权
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView doAuthTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user =CurrentUser.get();
		String categoryId = request.getParameter("categoryId");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		String from = request.getParameter("from");

		String[] templeteIds = request.getParameterValues("id");
		if(templeteIds != null){
			//授权信息
			String authInfo = request.getParameter("authInfo");
			String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
			boolean hasChange = false ;
			int i = 0;
			for (String templeteIdStr : templeteIds) {
				long templeteId = Long.parseLong(templeteIdStr);
				Templete templete = this.templeteManager.get(templeteId);
				Set<TempleteAuth> auths = new HashSet<TempleteAuth>();
				Set<TempleteAuth> oldAuths = templete.getTempleteAuths() ;
				Map<Long,String> typeAndId = new HashMap<Long,String>() ;
				for(TempleteAuth templeteAuth : oldAuths){
					typeAndId.put(templeteAuth.getAuthId(),templeteAuth.getAuthType()) ;
				}
				Set<Long> memberSet = new HashSet<Long>();
				if(authInfos != null) {
					for (String[] strings : authInfos) {
						TempleteAuth auth = new TempleteAuth();

						auth.setIdIfNew();
						auth.setAuthType(strings[0]);
						auth.setAuthId(Long.parseLong(strings[1]));
						auth.setObjectId(templeteId);
						auth.setSort(i++);

						auths.add(auth);

						if(!hasChange && typeAndId.get(Long.parseLong(strings[1])) != null ){
							hasChange = true ;
						}

						Set<Long> memberIdsSet = Functions.getAllMembersId(auth.getAuthType(),auth.getAuthId());
						memberSet.addAll(memberIdsSet);
					}
				}

				this.templeteManager.updateAuth(templeteId, auths);

				//记录日志
				//记录日志
				if("2".equals(categoryId)||"3".equals(categoryId)||"5".equals(categoryId)){//2: 发文，3：收文,//5:签报
					appLogManager.insertLog(user, AppLogAction.Edoc_TempleteAuthorize,user.getName(),templete.getSubject());
				}else{
					if(hasChange){
						this.appLogManager.insertLog(user, AppLogAction.Coll_TempleteAuthorize, user.getName(),templete.getSubject()) ;
						hasChange = false ;
					}
				}
				//将当前模板推送到首页-我的模板 ,公文也自动推送到首页
                //if(categoryType==TempleteCategory.TYPE.collaboration_templete.ordinal() || categoryType==TempleteCategory.TYPE.form.ordinal())
                {
                    List<Long> authMemberIdsList = new ArrayList<Long>();
                    if(memberSet != null){
                    	authMemberIdsList.addAll(memberSet);
    	                //yangzd 将启用的和授权的推送的首页
    	                if(templete == null){
    	                	templete = this.templeteManager.get(templeteId);
    	                }
    	                Templete.State state = Templete.State.values()[templete.getState()];
    	                if(state==Templete.State.normal)
    	                {
    	                	templeteConfigManager.pushThisTempleteToMain4Members(authMemberIdsList, templeteId, categoryType);
    	                }
//    	                templeteConfigManager.pushThisTempleteToMain4Members(authMemberIdsList, templeteId, categoryType);
    	                //yangzd
                    }
                }
            }
		}

		return super.redirectModelAndView("/templete.do?method=systemList&categoryId=" + categoryId + "&categoryType=" + categoryType + "&from=" + from);
	}

	/**
	 * 移动模板
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView doMoveTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String categoryId = request.getParameter("categoryId");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		String from = request.getParameter("from");

		String[] templeteIds = request.getParameterValues("id");

		Long newCategoryId = null;
		String newCategoryIdStr = request.getParameter("newCategoryId");

		if(StringUtils.isNotBlank(newCategoryIdStr)){
			newCategoryId = Long.parseLong(newCategoryIdStr);
		}

		if(templeteIds != null){
			for (String templeteIdStr : templeteIds) {
				long templeteId = Long.parseLong(templeteIdStr);
				Templete templete = this.templeteManager.get(templeteId) ;
				if(templete != null && templete.getCategoryId().longValue() != newCategoryId ){
					this.appLogManager.insertLog(CurrentUser.get(), AppLogAction.Coll_TempleteMove, CurrentUser.get().getName(),templete.getSubject());
				}
				this.templeteManager.updateCategoryId(templeteId, newCategoryId);
			}
		}

		return super.redirectModelAndView("/templete.do?method=systemList&categoryId=" + categoryId + "&categoryType=" + categoryType + "&from=" + from);
	}

	/**
	 * 停用、启用模板
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView doInvalidateTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String categoryId = request.getParameter("categoryId");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		String from = request.getParameter("from");

		String[] templeteIds = request.getParameterValues("id");

		Templete.State state = Templete.State.values()[Integer.parseInt(request.getParameter("state"))];
		User user = CurrentUser.get();
		if(templeteIds != null){
			for (String templeteIdStr : templeteIds) {
				long templeteId = Long.parseLong(templeteIdStr);
				this.templeteManager.updateTempleteState(templeteId, state);
				//记录应用日志
				if(categoryType==TempleteCategory.TYPE.edoc.ordinal()||categoryType==TempleteCategory.TYPE.edoc_rec.ordinal()||categoryType==TempleteCategory.TYPE.edoc_send.ordinal()){
				    Templete temp = templeteManager.get(templeteId);
				    if(state == Templete.State.normal){
				        //启用
				        appLogManager.insertLog(user, AppLogAction.Edoc_Templete_Start, user.getName(),temp.getSubject());
				    }else{
				        //停用
				        appLogManager.insertLog(user, AppLogAction.Edoc_Templete_Stop, user.getName(),temp.getSubject());
				    }
				}
                //清空对应模板的首页配置
                templeteConfigManager.clearConfigByTempleteId(templeteId);
                if(state == Templete.State.normal){
                    //将当前启用的模板推送到首页-我的模板

                    if(categoryType==TempleteCategory.TYPE.collaboration_templete.ordinal() || categoryType==TempleteCategory.TYPE.form.ordinal()||categoryType==TempleteCategory.TYPE.edoc.ordinal()||categoryType==TempleteCategory.TYPE.edoc_rec.ordinal()||categoryType==TempleteCategory.TYPE.edoc_send.ordinal()){
                        Set<Long> memberId = new HashSet<Long>();
                        Set<TempleteAuth> authList = templeteManager.get(templeteId).getTempleteAuths();
                        for (TempleteAuth auth : authList) {
                            Set<Long> memberIdsSet = Functions.getAllMembersId(auth.getAuthType(),auth.getAuthId());
                            memberId.addAll(memberIdsSet);
                        }
                        if(!memberId.isEmpty()){
                            templeteConfigManager.pushThisTempleteToMain4Members(new ArrayList<Long>(memberId), templeteId, categoryType);
                        }
                    }
                }
			}
		}

		return super.redirectModelAndView("/templete.do?method=systemList&categoryId=" + categoryId + "&categoryType=" + categoryType + "&from=" + from);
	}

	/**
	 * 点击察看详细内容 - 管理员
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemSummary(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("collaboration/templete/systemSummary");
		Long templeteId = Long.parseLong(request.getParameter("templeteId"));

		Templete templete = this.templeteManager.get(templeteId);

		ColSummary summary = new ColSummary();
		summary.setSubject(templete.getSubject());

		modelAndView.addObject("summary", summary);
		modelAndView.addObject("templete", templete);

		try {
			V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, templete.getMemberId());
			modelAndView.addObject("member", member);
		}
		catch (Exception e) {
			log.error("", e);
		}

        modelAndView.addObject("attachments", attachmentManager.getByReference(templeteId));
        modelAndView.addObject("canDeleteOriginalAtts", true);    //允许删除原附件

		return modelAndView;
	}

	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemTopic(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("collaboration/templete/systemTopic");

		Long templeteId = Long.parseLong(request.getParameter("templeteId"));

		Templete templete = this.templeteManager.get(templeteId);

		ColSummary summary = (ColSummary) XMLCoder.decoder(templete.getSummary());
		ColBody body = (ColBody) XMLCoder.decoder(templete.getBody());

		modelAndView.addObject("body", body);
		if(summary != null){
			modelAndView.addObject("senderOpinion", summary.getSenderOpinion());//发起人附言
		}

		return modelAndView;
	}

	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView systemWorkflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
		"collaboration/templete/systemWorkflow");
		Long templeteId = Long.parseLong(request.getParameter("templeteId"));

		Templete templete = this.templeteManager.get(templeteId);
		if(!templete.getType().equals("text")){
			BPMProcess process = BPMProcess.fromXML(templete.getWorkflow()); //重新生成，因为要取新的节点名称

			String caseProcessXML = process.toXML();
	        caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);

			modelAndView.addObject("hasDiagram", "true");
			modelAndView.addObject("caseProcessXML", caseProcessXML);
			modelAndView.addObject("branchs", this.templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.collaboration.ordinal()));
		}


		ColSummary summary = (ColSummary) XMLCoder.decoder(templete.getSummary());
		modelAndView.addObject("summary", summary);

		 //预归档目录目录名
        Long archiveId = null;
        String archiveName = "";
        if(summary.getArchiveId() != null){
        	archiveId = summary.getArchiveId();
        	archiveName = docHierarchyManager.getNameById(archiveId);
        }
        modelAndView.addObject("archiveName", archiveName);

        //关联项目名称
        Long projectId = null;
        String projectName = "";
        if(summary.getProjectId() != null){
        	projectId = summary.getProjectId();
        	ProjectSummary project = projectManager.getProject(projectId);
        	projectName = project.getProjectName();
        }
        modelAndView.addObject("projectName", projectName);

        Metadata comRemindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		Metadata comDeadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
		modelAndView.addObject("comRemindMetadata", comRemindMetadata);
		modelAndView.addObject("comMetadata", comDeadlineMetadata);
		modelAndView.addObject("type", templete.getType());
		modelAndView.addObject("isShowButton", false);
		return modelAndView;
	}

	/**
	 * 模板详情
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"collaboration/templete/detail");

		Long id = new Long(request.getParameter("id"));
		User user = CurrentUser.get();
		Templete templete = templeteManager.get(id);
		if (templete == null) {
			return modelAndView;
		}
		Templete source = templete;
		String formtitle = templete.getSubject();
		Long formParentid = templete.getFormParentId();
		String bodytype = templete.getBodyType();
		Long templeteFormparnetId = templete.getFormParentId();
        if("FORM".equalsIgnoreCase(bodytype) && templeteFormparnetId !=null)
        	templete = templeteManager.get(templete.getFormParentId());
		modelAndView.addObject("templete", templete);

		String type = templete.getType();

		// 正文格式不显示流程
		if (!Templete.Type.text.name().equals(type)) {
            BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
            String caseProcessXML = process.toXML(); //重新生成，因为要取新的节点名称

			List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);

			Metadata nodePermissionPolicy = metadataManager.getMetadata(MetadataNameEnum.col_flow_perm_policy);

	        caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
	        caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);

	        modelAndView.addObject("workflowInfo", workflowInfo);
			modelAndView.addObject("workflow", caseProcessXML);
			modelAndView.addObject("nodePermissionPolicy", nodePermissionPolicy);
			modelAndView.addObject("workflowRule", templete.getWorkflowRule());
		}

		ColBody body = (ColBody) XMLCoder.decoder(templete.getBody());
		// 流程模板不保存正文
		if (!Templete.Type.workflow.name().equals(type)) {
			modelAndView.addObject("body", body);
		}

		if(Templete.Type.templete.name().equals(type)&&"FORM".equals(templete.getBodyType())){
			String formView = "";
			if(templeteFormparnetId != null){
				BPMProcess process = BPMProcess.fromXML(source.getWorkflow()); //重新生成，因为要取新的节点名称;
				String[] formInfo = FormHelper.getFormPolicy(process);
				String formbodyxml = www.seeyon.com.v3x.form.utils.StringUtils.Java2JavaScriptStr(source.getBody());
				//String runtimeView = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], null, null, null, formInfo[3],false);
				String runtimeView = FormHelper.getFormView(formInfo[0],formInfo[1]);
				if(!"".equals(formbodyxml) && !"null".equals(formbodyxml) && formbodyxml !=null){
            		StringBuffer sbxml = new StringBuffer();
	            	String xmlStart = "&&&&&&&&  newdata_start  &&&&&&&&";
	            	sbxml.append(runtimeView);
	            	sbxml.append(xmlStart);
	            	sbxml.append(formbodyxml);
	            	formView =sbxml.toString();
            	}
			}else{
				FormContent formContent = (FormContent)body;
				//String content = body.getContent();
				//FormContent formContent = (FormContent)XMLCoder.decoder(content);
				FormBody formBody = formContent.getForms().get(0);
				formView = FormHelper.getFormView(formBody.getFormApp(), formBody.getForm());
				if(formView == null || formView.indexOf("<msg>")!=-1)
					modelAndView.addObject("formError", true);
			}
			modelAndView.addObject("formView", formView);

		}

		modelAndView.addObject("branchs", ColHelper.transformBranch(this.templeteManager.getBranchsByTemplateId(id,ApplicationCategoryEnum.collaboration.ordinal()), true));
		 String templateName = "";
		if("FORM".equalsIgnoreCase(bodytype) && formParentid !=null)
		    templateName = formtitle + "(" + CurrentUser.get().getName() + " " + Datetimes.formatDatetimeWithoutSecond(new Date()) + ")";
		else
			templateName = templete.getSubject() + "(" + CurrentUser.get().getName() + " " + Datetimes.formatDatetimeWithoutSecond(new Date()) + ")";
		modelAndView.addObject("templateName", Strings.toHTML(templateName));
		return modelAndView;
	}

	/**
	 * 弹出新建分类窗口--协同模板、表单模板
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView showSystemCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("collaboration/templete/systemCategory");

		String id = request.getParameter("id");
		String from = request.getParameter("from");

		User user = CurrentUser.get();
		long memberId = user.getId();

		Long categoryId = null;
		if(StringUtils.isNotBlank(id)){
			categoryId = Long.parseLong(id);

			TempleteCategory templeteCategory = this.templeteCategoryManager.get(categoryId);

			modelAndView.addObject("category", templeteCategory);
		}

		long orgAccountId = user.getLoginAccount();

		List<TempleteCategory> templeteCategories = this.templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		templeteCategories.addAll(this.templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.form.ordinal()));
		Collections.sort(templeteCategories);

		for (int i = 0; i < templeteCategories.size(); i++) {
			TempleteCategory category = templeteCategories.get(i);

			if(category.getId().equals(categoryId) || (!"SYS".equalsIgnoreCase(from) && TempleteUtil.isClass1Category(category) && !category.isCanManager(memberId, orgAccountId))){
				templeteCategories.remove(category);
				i--;
			}
		}

		StringBuffer categoryHTML = new StringBuffer();
		List<Long> categoryTypes = new ArrayList<Long>();
		categoryTypes.add(0l);
		categoryTypes.add(4l);
		category2HTML(templeteCategories, categoryHTML, categoryTypes, 1);

		modelAndView.addObject("categoryHTML", categoryHTML);

		return modelAndView;
	}

	private static StringBuffer category2HTML(List<TempleteCategory> categories,
			StringBuffer categoryHTML, List<Long> currentNode, int level){
		for (TempleteCategory category : categories) {
			Long parentId = category.getParentId();
			if(currentNode.contains(parentId)){

				categoryHTML.append("<option value='" + category.getId() + "'>");

				for (int i = 0; i < level; i++) {
					categoryHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				}

				categoryHTML.append(Strings.toHTML(category.getName()) + "</option>\n");
				List<Long> categoryTypes = new ArrayList<Long>();
				categoryTypes.add(category.getId());
				category2HTML(categories, categoryHTML, categoryTypes, level + 1);
			}
		}

		return categoryHTML;
	}

	/**
	 * 保存模板分类 新建/修改
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView saveCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		TempleteCategory category = new TempleteCategory();
		bind(request, category);

		category.setType(Integer.parseInt(request.getParameter("categoryType")));

		long orgAccountId = CurrentUser.get().getLoginAccount();
		category.setOrgAccountId(orgAccountId);

		//授权信息
		String authInfo = request.getParameter("authInfo");

		if(category.getParentId()==category.getId())
		{
			PrintWriter out = response.getWriter();
			out.println("<script>alert('input param err');self.history.back();</script>");
			return null;
		}

		if (category.isNew()) {
			category.setIdIfNew();
			category.setCreateDate(new Timestamp(System.currentTimeMillis()));

			doAuth(authInfo, category);

			this.templeteCategoryManager.save(category);
		}
		else {
			if(Strings.isNotBlank(request.getParameter("originalCreateDate"))){
				Timestamp cdate = new Timestamp(Datetimes.parseDatetime(request.getParameter("originalCreateDate")).getTime());
				category.setCreateDate(cdate);
			}

			doAuth(authInfo, category);

			this.templeteCategoryManager.update(category);
		}

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("var rv = [\"" + category.getId() + "\", \"" + Functions.toHTML(category.getName()) +"\", \"" + category.getParentId() + "\", " + category.getSort() + ",\""+category.getType()+"\"];");
		out.println("parent.parent.window.returnValue = rv;");
		out.println("parent.window.close();");
		out.println("</script>");

		return null;
	}

	private static void doAuth(String authInfo, TempleteCategory category){
		String[][] authInfos = Strings.getSelectPeopleElements(authInfo);
		if(authInfos != null){
			int i = 0;
			for (String[] strings : authInfos) {
				TempleteAuth auth = new TempleteAuth();

				auth.setIdIfNew();
				auth.setAuthType(strings[0]);
				auth.setAuthId(Long.parseLong(strings[1]));
				auth.setSort(i++);
				auth.setObjectId(category.getId());

				category.getCategoryAuths().add(auth);
			}
		}
	}

	/**
	 * 删除分类
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.TempleteManager,RoleType.AccountEdocAdmin})
	public ModelAndView deleteCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long categoryId = Long.parseLong(request.getParameter("id"));

		if(categoryId>=0 && categoryId<=5)
		{
			return null;
		}

		PrintWriter out = response.getWriter();
		out.println("<script>");
		try {
			this.templeteCategoryManager.deleteCategory(categoryId);
			out.println("parent.endDeleteCategory(true);");
		}
		catch (BusinessException e) {
			out.println("parent.endDeleteCategory(false);");
		}

		out.println("</script>");

		return null;
	}


    /**
     * 我的模板 - 配置模板 显示配置页
     */
    public ModelAndView showTemplateConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("collaboration/templete/templeteConfigFrame");
        return modelAndView;
    }


    /**
     * 我的模板 - 配置模板 更新配置
     */
    public ModelAndView updateTemplateConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String[] configIdsStr = request.getParameterValues("templeteIds");
        String[] templeteTypes = request.getParameterValues("templeteTypes");
        if(configIdsStr!=null && configIdsStr.length > 0){
            Long[] templeteIds = new Long[configIdsStr.length];
            int[] types = new int[templeteTypes.length];
            for(int i=0; i<configIdsStr.length; i++){
                templeteIds[i] = Long.parseLong(configIdsStr[i]);
                types[i] = Integer.parseInt(templeteTypes[i]);
            }
            if(templeteIds.length > 0){
                templeteConfigManager.pushTempletesToMain(CurrentUser.get().getId(), templeteIds, types);
            }
       }
        return super.redirectModelAndView("/templete.do?method=showTemplateConfig", "parent");
    }

    /**
     * 模板配置 列表
     */
    public ModelAndView templateConfigList(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("collaboration/templete/templateConfigList");
        long memberId = CurrentUser.get().getId();

		String condition = request.getParameter("condition");
		List<TempleteConfig> myTempleteList = null;
		V3xOrgMember member = orgManager.getMemberById(memberId);
		if(condition!=null && !"".equals(condition)){
			String value = request.getParameter("textfield");
			String value1 = request.getParameter("textfield1");
			myTempleteList = templeteConfigManager.getConfigTempletes(memberId,condition,value,value1,member.getSecretLevel().toString());
		}else{
			myTempleteList = templeteConfigManager.getConfigTempletes(memberId,member.getSecretLevel().toString());
		}
		boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");

	    if(!isEdoc || !isPluginEdoc ){
	        for(Iterator it = myTempleteList.iterator();it.hasNext();){
	        	TempleteConfig tc = (TempleteConfig)it.next();
	        	if(tc.getType() == TempleteCategory.TYPE.edoc.ordinal()
	        			||tc.getType() == TempleteCategory.TYPE.edoc_rec.ordinal()
	        			||tc.getType() == TempleteCategory.TYPE.edoc_send.ordinal()
	        			||tc.getType() == TempleteCategory.TYPE.sginReport.ordinal())
	        		it.remove();
	        }
	    }
        modelAndView.addObject("myTempleteList", myTempleteList);
        return modelAndView;
    }

    /**
     * 模板配置 取消发布到首页
     */
    public ModelAndView cancelPush(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String[] configIds = request.getParameterValues("configIds");
        List<Long> configIdsList = new ArrayList<Long>();

        if (configIds != null) {
            for (String string : configIds) {
                long configId = Long.parseLong(string);
                configIdsList.add(configId);
            }
        }
        this.templeteConfigManager.cancelPush(configIdsList);

        return super.redirectModelAndView("/templete.do?method=templateConfigList");
    }


    /**
     * 我的模板　－　更多页面
     * @author lilong modify 2012-01-17
     */
    public ModelAndView moreTemplate(HttpServletRequest request,
	        HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("collaboration/templete/moreTemplate");
        User user = CurrentUser.get();
        long memberId = user.getId();

        List<TempleteConfig> shouTempleteList = new ArrayList<TempleteConfig>();
        List<TempleteConfig> faTempleteList = new ArrayList<TempleteConfig>();
        List<TempleteConfig> qianTempleteList = new ArrayList<TempleteConfig>();
        //个人模板
	    List<TempleteConfig> personalTempletes = new ArrayList<TempleteConfig>();
	    String fragmentId = request.getParameter("fragmentId");
	    String category = "-1,0,1,2,3,4,5";//全部种类
	    boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
	    if(StringUtils.isNotBlank(fragmentId)) {
	    	String ordinal = request.getParameter("ordinal");
	        Map<String,String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
	        String panel = SectionUtils.getPanel("all", preference);
			if(!"all".equals(panel)) {
				String tempStr = preference.get(panel+"_value");
				if(StringUtils.isBlank(tempStr)) {
				    return modelAndView;
				}
				category = "";
				String[] temList = tempStr.split(",");
				for(String s : temList){
					if("catagory_personal_templete".equals(s)) {
						category += "-1,";//个人模板
					} else if("catagory_collOrFormTemplete".equals(s)){
						category += "4,0,";//协同和表单模板
					} else if("catagory_edoc".equals(s)) {
						category += "1,2,3,5,"; 
					}
				}
			}
	    }
	    
		String add_onsCondition = "";
		Map<String, Object> nameParameters = new HashMap<String, Object>();
		String textfield = request.getParameter("textfield");
		if (Strings.isNotBlank(textfield)) {
			add_onsCondition = " and t.subject like :subject ";
			nameParameters.put("subject", "%" + SQLWildcardUtil.escape(textfield.trim()) + "%");
		}
		V3xOrgMember member = orgManager.getMemberById(memberId);
		List<TempleteConfig> allTempleteConfig = templeteConfigManager.getMyTempletesByCategory(memberId, add_onsCondition, nameParameters, -1, true, category,member.getSecretLevel().toString(),false);//成发集团项目 程炯 根据密级筛选
	    
	    //所有系统模板配置
	    List<TempleteCategory> templeteCategorys = templeteCategoryManager.getCategorys(user.getAccountId(), TempleteCategory.TYPE.collaboration_templete.ordinal());
	    templeteCategorys.addAll(templeteCategoryManager.getCategorys(user.getAccountId(), TempleteCategory.TYPE.form.ordinal()));

	    TempleteCfgCategory<TempleteConfig> helper = new TempleteCfgCategory<TempleteConfig>(templeteCategorys);
	    boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
	    for (TempleteConfig config : allTempleteConfig) {
			int type = config.getType();
			String isSystem = config.getIsSystem();
			if (type == TempleteCategory.TYPE.collaboration_templete.ordinal()|| type == TempleteCategory.TYPE.form.ordinal()) {
				helper.setTempeteConfig(config, templeteCategoryManager);
			} else if ((type == -1 || type == 2) && "false".equals(isSystem)){
				personalTempletes.add(config);
			} else if (isEdoc && isPluginEdoc){
				 if (type == TempleteCategory.TYPE.edoc_rec.ordinal()) {
					shouTempleteList.add(config);
				} else if (type == TempleteCategory.TYPE.edoc_send.ordinal()  && "true".equals(isSystem)) {
					faTempleteList.add(config);
				} else if (type == TempleteCategory.TYPE.sginReport.ordinal()) {
					qianTempleteList.add(config);
				}
			}
		}
	    // 协同和表单
	    //List<TempleteCategory> collTempleteCategory = templeteCategoryManager.getCategory4User(memberId, TempleteCategory.TYPE.collaboration_templete.ordinal(),TempleteCategory.TYPE.form.ordinal());
	    Map<String,List<TempleteConfig>> collTempleteCategory = helper.getColNameTemplete();
	    Set<String> categoryNames = helper.getCategoryNames();

	    modelAndView.addObject("shouTemplete", shouTempleteList);
	    modelAndView.addObject("faTemplete", faTempleteList);
	    modelAndView.addObject("qianTemplete", qianTempleteList);
	    modelAndView.addObject("collTempleteCategory", collTempleteCategory);
	    modelAndView.addObject("categoryNames", categoryNames);
	    modelAndView.addObject("personalTempletes", personalTempletes);
	    return modelAndView;
	}

    private Map<String,List<TempleteConfig>> parseTemplete4CategoryName(List<TempleteConfig> configs,List<TempleteCategory> categorys){
    	Map<String,List<TempleteConfig>> result= new HashMap<String,List<TempleteConfig>>();
    	Map<String,TempleteCategory> nameCategory = new HashMap<String,TempleteCategory>();
    	Map<Long,TempleteCategory> idCategory = new HashMap<Long,TempleteCategory>();
    	for (TempleteCategory category : categorys) {
    		nameCategory.put(category.getName(), category);
    		idCategory.put(category.getId(), category);
		}
    	for (TempleteCategory category : categorys) {

		}

    	return result;
    }
    /**
     * 我的模板　－　排序页面
     */
    public ModelAndView showTempleteSort(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("collaboration/templete/templeteSort");
        long memberId = CurrentUser.get().getId();
        V3xOrgMember member = orgManager.getMemberById(memberId);
        List<TempleteConfig> templeteList = templeteConfigManager.getConfigTempletes(memberId,member.getSecretLevel().toString(),true);

        boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");

	    if(!isEdoc || !isPluginEdoc ){
	        for(Iterator it = templeteList.iterator();it.hasNext();){
	        	TempleteConfig tc = (TempleteConfig)it.next();
	        	if(tc.getType() == TempleteCategory.TYPE.edoc.ordinal()
	        			||tc.getType() == TempleteCategory.TYPE.edoc_rec.ordinal()
	        			||tc.getType() == TempleteCategory.TYPE.edoc_send.ordinal()
	        			||tc.getType() == TempleteCategory.TYPE.sginReport.ordinal())
	        		it.remove();
	        }
	    }
        modelAndView.addObject("templeteList", templeteList);

        return modelAndView;
    }

    /**
     * 更新排序设置
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView updateTempleteSort(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String[] templeteConfigIdsStr = request.getParameterValues("sortConfigIds");
        Long[] templeteConfigIds = new Long[templeteConfigIdsStr.length];

        for(int i=0; i<templeteConfigIdsStr.length; i++)
        {
            templeteConfigIds[i] = Long.parseLong(templeteConfigIdsStr[i]);
        }

        templeteConfigManager.sortTemplete(templeteConfigIds);

        return super.redirectModelAndView("/templete.do?method=showTemplateConfig", "parent");
    }

    /**
     * 配置模板弹出页的左侧模板树
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView  templeteConfigTree(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
        "collaboration/templete/templeteConfigTree");
        User user = CurrentUser.get();
        String category = request.getParameter("categoryType");
        String[] types = null;
        V3xOrgMember member = orgManager.getMemberById(user.getId());//成发集团项目 程炯 获取当前用户
        if(category!=null){
            types = StringUtils.split(category,",");
            modelAndView.addObject("collaborationCate", types[0]);
            Long orgAccountId = user.getLoginAccount();
            if(!user.isAdministrator() && request.getAttribute("showPortal") == null){
            	List<Templete> personalTempletes = templeteManager.getPersonalTemplete(member.getSecretLevel().toString());//成发集团项目 程炯 获取个人模版
            	modelAndView.addObject("personalTempletes", personalTempletes);
            }
            Map<String,TempleteCategory> nameCategory = new HashMap<String,TempleteCategory>();
            List<Templete> showSystemTemplete = new ArrayList<Templete>();
            List<TempleteCategory> colCategory = new ArrayList<TempleteCategory>();
            boolean isEdoc = Functions.isEnableEdoc();
    	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
            for(String t:types){
            	int type = Integer.parseInt(t);
			    if(!isEdoc || !isPluginEdoc ){
			    	if(type == TempleteCategory.TYPE.edoc.ordinal()
			    			||type == TempleteCategory.TYPE.edoc_rec.ordinal()
			    			||type == TempleteCategory.TYPE.edoc_send.ordinal()
			    			||type == TempleteCategory.TYPE.sginReport.ordinal()){
			    		continue;
			    	}
			    }

                List<TempleteCategory> templeteCategorys = templeteCategoryManager.getCategorys(orgAccountId, type);
                //Long accountId = null;
               /* if(type == TempleteCategory.TYPE.edoc_rec.ordinal() || type == TempleteCategory.TYPE.edoc_send.ordinal() || type == TempleteCategory.TYPE.sginReport.ordinal()){
                    if(!Functions.isEnableEdoc()){//判断公文插件
                    	continue;
                    }
                	accountId = user.getLoginAccount();
                }else{*/
                	colCategory.addAll(templeteCategorys);
                	for(TempleteCategory c : templeteCategorys){
                		nameCategory.put(c.getName(), c);
                	}
                //}
                List<Templete> systemTempletes = templeteManager.getSystemTempletesByMemberId(user.getId(), null,member.getSecretLevel().toString(),Integer.valueOf(t));//成发集团项目 程炯 获取系统模版

                if(type == TempleteCategory.TYPE.collaboration_templete.ordinal() || type == TempleteCategory.TYPE.form.ordinal()){
                   showSystemTemplete.addAll(systemTempletes);
                }else if(type == TempleteCategory.TYPE.edoc_rec.ordinal()){
                    modelAndView.addObject("templeteCategorysEdoc_rec", templeteCategorys);
                    modelAndView.addObject("systemTempletesEdoc_rec", systemTempletes);
                }else if(type == TempleteCategory.TYPE.edoc_send.ordinal()){
                    modelAndView.addObject("templeteCategorysEdoc_send", templeteCategorys);
                    modelAndView.addObject("systemTempletesEdoc_send", systemTempletes);
                }else if(type == TempleteCategory.TYPE.sginReport.ordinal()){
                	modelAndView.addObject("templeteCategorysEdoc_sginReport", templeteCategorys);
                	modelAndView.addObject("systemTempletesEdoc_sginReport", systemTempletes);
                }
             }
            List<Templete> allTemplete = new ArrayList<Templete>();
            List<TempleteCategory> outerCategory = new ArrayList<TempleteCategory>();
            boolean isShowOuter = Boolean.valueOf(Functions.getSysFlag("col_showOtherAccountTemplate").toString());
            for (Templete templete : showSystemTemplete) {
				if (!templete.getOrgAccountId().equals(orgAccountId)) {
					if(!isShowOuter) continue;
					// 外单位模板
					if (templete.getCategoryId() != 0) {// 等于0是顶层的模板
						TempleteCategory tc = templeteCategoryManager.get(templete.getCategoryId());
						if (tc != null) {
							TempleteCategory n = nameCategory.get(tc.getName());
							if (n != null) {
								templete.setCategoryId(n.getId());
							} else {
								outerCategory.add(tc);
								nameCategory.put(tc.getName(), tc);
							}
						}
					}
				}
				allTemplete.add(templete);
			}
            Collections.sort(colCategory);
            modelAndView.addObject("showSystemTemplete", allTemplete);
            modelAndView.addObject("colTempleteCategory", colCategory);
            modelAndView.addObject("outerCategory", outerCategory);
            modelAndView.addObject("showSystemTempleteSize", allTemplete.size());
            if(request.getAttribute("showPortal") == null){
            	List<TempleteConfig> myTempleteList = templeteConfigManager.getConfigTempletes(user.getId(),member.getSecretLevel().toString(),true);//成发集团项目 程炯 获取个人模版
    	        modelAndView.addObject("myTempleteList", myTempleteList);
            }
        }
        return modelAndView;
    }
    /**
     * 协同、表单模板预览
     * 在detailFrame中打开
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView templeteFrameSet(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "collaboration/templete/TempleteViewFrameset");
        String idStr = request.getParameter("id");
        Long id = new Long(idStr);
        Templete templete = templeteManager.get(id);
        if (templete == null) {
        	String alertTip = Constant.getString("col_template_deleted");
        	PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('" + alertTip + "');");
			out.println("</script>");
	    	out.flush();
	    	return super.refreshWorkspace();
        }
        modelAndView.addObject("id", idStr);
        //如果是表单个人模版则取父模版
        if(templete.getFormParentId() !=null && "FORM".equals(templete.getBodyType()))
        	templete = templeteManager.get(templete.getFormParentId());

        modelAndView.addObject("templete", templete);
        return modelAndView;
    }
    /**
     * 模板内容
     */
    public ModelAndView templeteConfigTopic(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "collaboration/templete/templeteConfigTopic");
        String idStr = request.getParameter("id");
        Long id = new Long(idStr);
        Templete templete = templeteManager.get(id);
        if (templete == null) {
        	String alertTip = Constant.getString("col_template_deleted");
        	PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('" + alertTip + "');");
			out.println("</script>");
	    	out.flush();
	    	return super.refreshWorkspace();
        }
        modelAndView.addObject("templete", templete);
        ColBody body = (ColBody) XMLCoder.decoder(templete.getBody());
        // 流程模板不保存正文
        modelAndView.addObject("body", body);
        if("FORM".equals(templete.getBodyType())){
            FormContent formContent = (FormContent)body;
            //String content = body.getContent();
            //FormContent formContent = (FormContent)XMLCoder.decoder(content);
            FormBody formBody = formContent.getForms().get(0);
            String formView = FormHelper.getFormView(formBody.getFormApp(), formBody.getForm());
            modelAndView.addObject("formView", formView);
        }
        return modelAndView;
    }
    /**
     * 模板预览
     * 在detailFrame中打开
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView templetePreview(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "collaboration/templete/templeteConfigDetail");
        String page = request.getParameter("page");
        if("right".equals(page)){
        	modelAndView = new ModelAndView("collaboration/templete/templeteConfigDiagram");
        }
        Long id = new Long(request.getParameter("id"));

        Templete templete = templeteManager.get(id);
        if (templete == null) {
            return modelAndView;
        }
        //如果是表单个人模版则取父模版
        if(templete.getFormParentId() !=null && "FORM".equals(templete.getBodyType()))
        	templete = templeteManager.get(templete.getFormParentId());

        modelAndView.addObject("templete", templete);
        int app = ApplicationCategoryEnum.collaboration.ordinal();
        Integer templateType = templete.getCategoryType();
        if(templateType != null && templateType != TempleteCategory.TYPE.collaboration_templete.ordinal() && templateType != TempleteCategory.TYPE.form.ordinal()){
        	app = ApplicationCategoryEnum.edoc.ordinal();
        }
        List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(id, app);
        modelAndView.addObject("branchs", branchs);

		try {
			V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, templete.getMemberId());
			modelAndView.addObject("member", member);
		}
		catch (Exception e) {
			log.error("", e);
		}

		ColSummary summary = (ColSummary) XMLCoder.decoder(templete.getSummary());
        //格式模板或者流程模板设置默认属性
        if(Templete.Type.text.name().equals(templete.getType()) || Templete.Type.workflow.name().equals(templete.getType())){
            summary.setCanForward(true);
            summary.setCanArchive(true);
            summary.setCanDueReminder(true);
            summary.setCanEditAttachment(true);
            summary.setCanModify(true);
            summary.setCanTrack(true);
            summary.setCanEdit(true);
        }
		modelAndView.addObject("summary", summary);
		 //预归档目录目录名
        Long archiveId = null;
        String archiveName = "";
        if(summary.getArchiveId() != null){
        	archiveId = summary.getArchiveId();
        	archiveName = docHierarchyManager.getNameById(archiveId);
        }
        modelAndView.addObject("archiveName", archiveName);

        //关联项目名称
        Long projectId = null;
        String projectName = "";
        if(summary.getProjectId() != null){
        	projectId = summary.getProjectId();
        	ProjectSummary project = projectManager.getProject(projectId);
        	projectName = project.getProjectName();
        }
        modelAndView.addObject("projectName", projectName);
        Metadata comRemindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		Metadata comDeadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
		modelAndView.addObject("comRemindMetadata", comRemindMetadata);
		modelAndView.addObject("comMetadata", comDeadlineMetadata);
        String type = templete.getType();

        // 正文格式不显示流程
        if (!Templete.Type.text.name().equals(type)) {
            BPMProcess process = BPMProcess.fromXML(templete.getWorkflow());
            String caseProcessXML = process.toXML(); //重新生成，因为要取新的节点名称

            List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);

            Metadata nodePermissionPolicy = metadataManager.getMetadata(MetadataNameEnum.col_flow_perm_policy);

            caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
            caseProcessXML = StringEscapeUtils.escapeJavaScript(caseProcessXML);

            modelAndView.addObject("workflowInfo", workflowInfo);
            modelAndView.addObject("workflow", caseProcessXML);
            modelAndView.addObject("nodePermissionPolicy", nodePermissionPolicy);
        }
        return modelAndView;
    }


    @Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
    	String ViewPage = request.getParameter(GenericController.Parameter_Name_ViewPage);

		log.debug("ViewPage : " + ViewPage);

		if (ViewPage == null || "".equals(ViewPage)) {
			throw new java.lang.IllegalArgumentException(
					"Parameter 'ViewPage' is not available.");
		}

		ModelAndView modelAndView = new ModelAndView(ViewPage);
		return modelAndView;
	}


    /**
     * 显示组织机构
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listOrg(HttpServletRequest request,HttpServletResponse response)  throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/templete/selectOrg");
    	String org = request.getParameter("orgType");
    	String messageKey = "";
    	int orgType = 1;
    	if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(org)) {
    		messageKey = "org.department.label";
    	}else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equals(org)) {
    		messageKey = "org.team.label";
    		orgType = 2;
		}else if(V3xOrgEntity.ORGENT_TYPE_POST.equals(org)) {
			messageKey = "org.post.label";
			orgType = 3;
		}else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(org)) {
			messageKey = "org.level.label" + Functions.suffix();
			orgType = 4;
		}else if(V3xOrgEntity.ORGREL_TYPE_MEMBER_POST.equals(org)) {
			messageKey = "org.secondPost.label";
			orgType = 5;
		}else if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(org)) {
			messageKey = "org.member.label";
			orgType = 6;
		}else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(org)) {
			messageKey = "org.account.label";
			orgType = 7;
		}else if(V3xOrgEntity.ORGENT_TYPE_ROLE.equals(org)) {
			messageKey = "org.role.label";
			orgType = 8;
		}else if("StartMemberLoginAccount".equals(org)){
			messageKey = "org.startMember.loginAccount.label";
			orgType = 9;
		}
    	mv.addObject("messageKey", messageKey);
    	mv.addObject("org", org);
    	mv.addObject("orgType", orgType);
    	return mv;
    }

    /**
     * 显示枚举值列表
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listEnum(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/templete/selectEnum");
    	String enumKey = request.getParameter("enumKey");
    	String enumType = request.getParameter("enumType");
    	String formSort = request.getParameter("formSort");
    	String appName=request.getParameter("appName");
    	String isFinalChild = request.getParameter("isFinalChild");
    	String fieldName = request.getParameter("fieldName");
    	String isRef = request.getParameter("isRef");
    	String relationType = request.getParameter("relationType");
    	String enumId = request.getParameter("enumId");
    	String otherFormAppId = request.getParameter("otherFormAppId");
    	if("recEdoc".equals(appName) || "sendEdoc".equals(appName) || "signReport".equals(appName))
    	{
  		  Metadata bindMetadata = metadataManager.getMetadata(Long.parseLong(enumType));
  		  mv.addObject("enumValues",bindMetadata);
    	}else if("true".equals(isFinalChild)){
    		Metadata metadata = metadataManager.getMetadata(Long.parseLong(enumId));
    		if(metadata != null){
    			List<MetadataItem> items = this.metadataManager.getLastLevelItemByMetadataId(Long.parseLong(enumId));
        		mv.addObject("items", items).addObject("optionValueUseId", "true");
    			mv.addObject("enumValues", metadata);
    		}
    	}else if(Strings.isNotBlank(fieldName) && "true".equals(isRef) && Strings.isBlank(relationType)){
    		if(Strings.isNotBlank(otherFormAppId)){
    			String[] rs = FormHelper.getCurrentFieldLevel(otherFormAppId, fieldName);
        		String currentLevelNum = rs[0];
        		String refEnumId = rs[1];
        		if(Strings.isNotBlank(refEnumId) && Strings.isNotBlank(currentLevelNum)){
    	    		Metadata topMetadata = this.metadataManager.getMetadata(Long.parseLong(refEnumId));
    				if(topMetadata != null){
    					List<MetadataItem> items = this.metadataManager.getLevelItemOfMetadata(topMetadata,Integer.parseInt(currentLevelNum));
    					mv.addObject("items", items).addObject("optionValueUseId", "true");
    					mv.addObject("enumValues", topMetadata);
    				}
        		}
    		} else {
	    		HttpSession session = request.getSession();
	    		SessionObject sessionObject = (SessionObject)session.getAttribute("SessionObject");
	    		String topEnumId = FormHelper.getRefTopMedata(sessionObject,fieldName);
	    		if(Strings.isNotBlank(topEnumId)){
	    			int levelNum = FormHelper.getFieldDisplayLevel(sessionObject, fieldName);
	    			Metadata topMetadata = this.metadataManager.getMetadata(Long.parseLong(topEnumId));
	    			if(topMetadata != null){
	    				List<MetadataItem> items = this.metadataManager.getLevelItemOfMetadata(topMetadata,levelNum);
	    				mv.addObject("items", items).addObject("optionValueUseId", "true");
	    				mv.addObject("enumValues", topMetadata);
	    			}
	    		}
    		}
    	}else if(Strings.isNotBlank(fieldName) && "true".equals(isRef) && "1".equals(relationType)){//关联hr人员类型
    		Metadata metadata = null;
    		String refKey = request.getParameter("refKey");
	    	HrStaffInfoField hrField = FormHelper.getHrStaffInfoField();
	    	metadata = hrField.getMetadataById(refKey);
	    	if(metadata != null){
	    		List<MetadataItem> items = this.metadataManager.getMetadataItemByMetadata(metadata);
	    		mv.addObject("items", items);
	    		mv.addObject("enumValues", metadata);
	    	}
    	}else if(Strings.isNotBlank(fieldName) && "true".equals(isRef) && "2".equals(relationType)){//关联表单类型
    		Metadata metadata = null;
    		String refInputAttr = request.getParameter("refKey");
	    	String formAppId = request.getParameter("refFormAppId");
	    	if(org.apache.commons.lang.StringUtils.isNotEmpty(formAppId)){
	    		SeeyonForm_ApplicationImpl afapp = (SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.parseLong(formAppId));
				if(afapp!=null){
					InfoPath_Inputtypedefine inpointy = FormHelper.getInfoPathInputtypedefine(afapp,afapp.getFResourceProvider());
					List<IIP_InputObject> iobjectList =inpointy.getInputList();
					for (IIP_InputObject iip_InputObject : iobjectList) {
						if(refInputAttr.equals(iip_InputObject.getDataAreaName())){
							if(iip_InputObject instanceof TIP_InputRadio){
								metadata = metadataManager.getMetadata(((TIP_InputRadio)iip_InputObject).getFEnumId());
							}
							if(iip_InputObject instanceof TIP_InputSelect){
								TIP_InputSelect selectInputObject = (TIP_InputSelect)iip_InputObject;
								metadata = metadataManager.getMetadata(selectInputObject.getFEnumId());
								if(selectInputObject.isFinChild()){
									List<MetadataItem> items = this.metadataManager.getLastLevelItemByMetadataId(selectInputObject.getFEnumId());
									mv.addObject("items", items).addObject("optionValueUseId", "true");
								}
							}
						}
					}  
				}
			}
	    	if(metadata != null){
	    		//List<MetadataItem> items = this.metadataManager.getMetadataItemByMetadata(metadata);
	    		//mv.addObject("items", items);
	    		mv.addObject("enumValues", metadata);
	    	}
    	}else if(Strings.isNotBlank(fieldName) && "true".equals(isRef) && "3".equals(relationType)){
    		String refEnumId = request.getParameter("refEnumId");
    		String currentLevelNum = request.getParameter("currentLevelNum");
    		if(Strings.isNotBlank(refEnumId) && Strings.isNotBlank(currentLevelNum)){
	    		Metadata topMetadata = this.metadataManager.getMetadata(Long.parseLong(refEnumId));
				if(topMetadata != null){
					List<MetadataItem> items = this.metadataManager.getLevelItemOfMetadata(topMetadata,Integer.parseInt(currentLevelNum));
					mv.addObject("items", items).addObject("optionValueUseId", "true");
					mv.addObject("enumValues", topMetadata);
				}
    		}
    	}
    	else{
    		mv.addObject("enumValues", FormHelper.getEnumValues(enumKey,enumType,formSort));
    	}
    	return mv;
    }

    private void saveColSupervise(HttpServletRequest request,HttpServletResponse response,Templete template,boolean isNew,boolean sendMessage) {
		String supervisorId = request.getParameter("supervisorId");
        String supervisors = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
        String role = request.getParameter("superviseRole");
        String superviseId = request.getParameter("superviseId");
        long awakeDates = awakeDate==null||"".equals(awakeDate)?0:Long.parseLong(awakeDate);

        if((supervisorId != null && !"".equals(supervisorId) && awakeDate != null && !"".equals(awakeDate)
        		&& (superviseId==null||"".equals(superviseId)))||(superviseId!=null && !"".equals(superviseId))||(role!=null && !"".equals(role))) {
	        User user = CurrentUser.get();
		    //boolean canModifyAwake = "on".equals(request.getParameter("canModifyAwake"))?true:false;
		    String superviseTitle = request.getParameter("superviseTitle");
		    //Date date = Datetimes.parse(awakeDate, Datetimes.dateStyle);
		    String[] idsStr = null;
		    long[] ids = null;
		    if(!"".equals(supervisorId)) {
		      	idsStr = supervisorId.split(",");
			    ids = new long[idsStr.length];
			    int i = 0;
			    for(String id:idsStr) {
			     	ids[i] = Long.parseLong(id);
			       	i++;
			    }
		    }
		    //重要程度
		    int importantLevel = 1;
		    if(isNew){
		       	this.colSuperviseManager.saveForTemplate(importantLevel, template.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, awakeDates, Constant.superviseType.template.ordinal(), template.getId(),sendMessage);
		       	if(role!=null && !"".equals(role))
		       		this.colSuperviseManager.saveSuperviseTemplateRole(template.getId(), role);
		    }
		    else{
		       	this.colSuperviseManager.updateForTemplate(importantLevel, template.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, awakeDates, Constant.superviseType.template.ordinal(), template.getId(),sendMessage);
		       	this.colSuperviseManager.updateSuperviseTemplateRole(template.getId(), role);
		    }
        }
    }

    private void saveColSupervise(HttpServletRequest request,HttpServletResponse response,String subject,long templateId,boolean isNew,int state,boolean sendMessage) {
		String supervisorId = request.getParameter("supervisorId");
        String supervisors = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
        if(supervisorId != null && !"".equals(supervisorId) && awakeDate != null && !"".equals(awakeDate)) {
        	User user = CurrentUser.get();
	        //boolean canModifyAwake = "on".equals(request.getParameter("canModifyAwake"))?true:false;
	        String superviseTitle = request.getParameter("superviseTitle");
	        Date date = Datetimes.parse(awakeDate, Datetimes.dateStyle);
	        String[] idsStr = supervisorId.split(",");
	        long[] ids = new long[idsStr.length];
	        int i = 0;
	        for(String id:idsStr) {
	        	ids[i] = Long.parseLong(id);
	        	i++;
	        }
	        int importantLevel = 1;
	        if(isNew)
	        	this.colSuperviseManager.save(importantLevel, subject,superviseTitle,user.getId(),user.getName(), supervisors, ids, date, Constant.superviseType.template.ordinal(), templateId,state,sendMessage, null);
	        else
	        	this.colSuperviseManager.update(importantLevel, subject,superviseTitle,user.getId(),user.getName(), supervisors, ids, date, Constant.superviseType.template.ordinal(), templateId,state,sendMessage, null);
        }
	}

    public ModelAndView showBranchDesc(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/templete/moreCondition");
    	String linkId = request.getParameter("linkId");
    	String templateId = request.getParameter("templateId");
    	ColBranch branch = null;
    	if(templateId != null && linkId != null) {
    		branch = this.templeteManager.getBranchByTemplateAndLink(ApplicationCategoryEnum.collaboration.ordinal(), Long.parseLong(templateId), Long.parseLong(linkId));
    	}
    	if(branch != null) {
    		if(branch.getConditionType()!=2)
    			mv.addObject("desc", branch.getConditionDesc());
    		else
    			mv.addObject("desc", Constant.getString4CurrentUser("templete.branch.handOption"));
    	}
    	return mv;
    }
    public ModelAndView moreDepartmentTemplate(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/templete/moreDepartmentTemplate");
    	long departmentId;
    	String _departmentId=request.getParameter("departmentId");
    	if(Strings.isNotEmpty(_departmentId)){
    		departmentId=Long.parseLong(_departmentId);
    	}else{
    		departmentId = CurrentUser.get().getDepartmentId();
    	}
		Long orgAccountId = CurrentUser.get().getLoginAccount();

		List<TempleteCategory> templeteCategorysColl = templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		templeteCategorysColl.addAll(templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.form.ordinal()));
		//modelAndView.addObject("templeteCategorysColl", templeteCategorysColl);
		//List<Templete> templeteListColl = templeteManager.getSystemTempletesByOrgEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		List<Templete> templeteListColl = templeteManager.getSystemTempletesByOrgEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, TempleteCategory.TYPE.collaboration_templete.ordinal(),TempleteCategory.TYPE.form.ordinal());

		TempleteCfgCategory<Templete> helper = new TempleteCfgCategory<Templete>(templeteCategorysColl,templeteListColl,templeteCategoryManager);
		Map<String,List<Templete>> templete = helper.getColNameTemplete();
		Set<String> names = templete.keySet();
		modelAndView.addObject("categoryNames", names);
		modelAndView.addObject("collTempleteCategory", templete);

		if(Functions.isEnableEdoc()){
			List<Templete> templeteListRec = templeteManager.getSystemTempletesByOrgEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, TempleteCategory.TYPE.edoc_rec.ordinal());
			modelAndView.addObject("templeteListRec", templeteListRec);
			List<Templete> templeteListSend = templeteManager.getSystemTempletesByOrgEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, TempleteCategory.TYPE.edoc_send.ordinal());
			modelAndView.addObject("templeteListSend", templeteListSend);
			List<Templete> templeteListSginReport = templeteManager.getSystemTempletesByOrgEntity(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, TempleteCategory.TYPE.sginReport.ordinal());
			modelAndView.addObject("templeteListSginReport", templeteListSginReport);
		}
    	return modelAndView;
    }

    /***
     * 编辑新流程设置
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    public ModelAndView editNewflowSetting(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelAndView = new ModelAndView("collaboration/newflow/editNewflow");
        //String templeteIdStr = request.getParameter("templeteId");
        /*
        String nodeId = request.getParameter("nodeId");
        int newflowSettingSize = 0;
        Long currentFormTempleteId = (Long) request.getSession().getAttribute("currentFormTempleteId");
        if(currentFormTempleteId != null){
            List<NewflowSetting> newflowSettingList = newflowManager.getNewflowSettingList(currentFormTempleteId, nodeId);
            modelAndView.addObject("newflowSettingList", newflowSettingList);
            if(newflowSettingList!=null && !newflowSettingList.isEmpty()){
                newflowSettingSize = newflowSettingList.size();
            }
        }
        modelAndView.addObject("newflowSettingSize", newflowSettingSize);
        */
        int maxNewflowNum = 1;
        HttpSession session = request.getSession();
        String isUpdate = request.getParameter("isUpdate");
        SessionObject sessionObj = (SessionObject)session.getAttribute("SessionObject");
        if(sessionObj != null && sessionObj.getTemplateobj()!=null){
            HashMap templateMap = sessionObj.getTemplateobj().getFlowMap();
            if(templateMap!=null){
                Collection coll = templateMap.values();
                if(coll != null && coll.size() > 1){
                    maxNewflowNum = Strings.isNotBlank(isUpdate) ? (coll.size() - 1) : coll.size();
                }
            }
        }
        modelAndView.addObject("maxNewflowNum", maxNewflowNum);
        Map<Long, String> allTempleteMap = BindHelper.getTemplateNamesMap(session);
        modelAndView.addObject("templeteNameMap", allTempleteMap);
//      NF 从Session中取新流程设置
        HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
        if(currentFormNewflow != null){
            Long currentFormTempleteId = (Long) session.getAttribute("currentFormTempleteId");
            if(!currentFormTempleteId.equals(-1L)){
                Set<Long> templeteIds = currentFormNewflow.keySet();
                Iterator iter = templeteIds.iterator();
                while(iter.hasNext()){
                    Long templeteId = (Long)iter.next();
                    List<NewflowSetting> newflowSettingList = (List<NewflowSetting>)currentFormNewflow.get(templeteId);
                    if(newflowSettingList != null && !newflowSettingList.isEmpty()){
                        for (NewflowSetting setting : newflowSettingList) {
                            //当前模板已经被设为子流程了，不能再设置新流程(內存中校验)
                            if(currentFormTempleteId.equals(setting.getNewflowTempleteId())){
                                PrintWriter out = response.getWriter();
                                out.println("<script>");
                                out.println("alert('"+ Constant.getString("newflow.tip.templeteIsAlreadyChild") +"');");
                                out.println("try{window.close()}catch(e){};");
                                out.println("</script>");
                                out.flush();
                                return null;
                            }
                        }
                    }
                }
            }
        }

        return modelAndView;
    }

    /**
     * 保存/更新新流程设置
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView updateNewflowSetting(HttpServletRequest request,
            HttpServletResponse response)throws Exception{
        String clearAll = request.getParameter("clearAll");
        Long currentFormTempleteId = (Long) request.getSession().getAttribute("currentFormTempleteId");
        String nodeId = request.getParameter("nodeId");
        //清除所有
        if("true".equals(clearAll)){
            newflowManager.deleteNewFlowSetting(currentFormTempleteId, nodeId);
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("parent.window.returnValue='false';");
            out.println("parent.window.close();");
            out.println("</script>");
            out.flush();
            return null;
        }
        //更新所有
        String loopCountStr = request.getParameter("count");
        if(Strings.isNotBlank(loopCountStr)){
            //String templeteIdStr = request.getParameter("templeteId");
            if(currentFormTempleteId!=null && Strings.isNotBlank(nodeId)){
                List<NewflowSetting> newflowSettingList = new ArrayList<NewflowSetting>();
                //Long templeteId = Long.parseLong(templeteIdStr);
                for(int i=1; i<=Integer.parseInt(loopCountStr); i++){
                    String formTempleteId = request.getParameter("formFlow" + i + "Value");
                    String sender = request.getParameter("sender" + i);
                    String condition = request.getParameter("formFlowCondition" + i + "Value");
                    String conditionTitle = request.getParameter("formFlowCondition" + i + "Title");
                    String conditionBase = request.getParameter("formFlowCondition" + i + "Base");
                    Boolean isForce = "1".equals(request.getParameter("formFlowCondition" + i + "IsForce"));
                    String flowRelateType = request.getParameter("flowRelateType" + i);
                    Boolean isCanViewByMainFlow = "true".equals(request.getParameter("isCanViewByMainFlow" + i));
                    Boolean isCanViewMainFlow = "true".equals(request.getParameter("isCanViewMainFlow" + i));

                    NewflowSetting setting = new NewflowSetting();
                    setting.setIdIfNew();
                    setting.setTempleteId(currentFormTempleteId);
                    setting.setNodeId(nodeId);
                    setting.setNewflowSender(sender);
                    setting.setNewflowTempleteId(Long.parseLong(formTempleteId));
                    setting.setTriggerCondition(condition);
                    setting.setConditionTitle(conditionTitle);
                    setting.setConditionBase(conditionBase);
                    setting.setIsForce(isForce);
                    setting.setIsCanViewByMainFlow(isCanViewByMainFlow);
                    setting.setIsCanViewMainFlow(isCanViewMainFlow);
                    setting.setFlowRelateType(Integer.parseInt(flowRelateType));
                    setting.setCreateTime(new Date());
                    newflowSettingList.add(setting);
                }
                //如果是修改，则先删除原设置
                if(!"true".equals(request.getParameter("isNewCreate"))){
                    newflowManager.deleteNewFlowSetting(currentFormTempleteId, nodeId);
                }
                //更新
                newflowManager.saveNewFlowSetting(newflowSettingList);
            }
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("parent.window.returnValue='true';");
            out.println("parent.window.close();");
            out.println("</script>");
            out.flush();
        }
        return null;
    }

    /**
     * 选择表单流程
     */
    @SuppressWarnings("unchecked")
    public ModelAndView selectFormFlow(HttpServletRequest request, HttpServletResponse response)throws Exception{
        ModelAndView modelAndView = new ModelAndView("collaboration/newflow/selectFormFlow");
        HttpSession session = request.getSession();
        Long currentFormTempleteId = (Long) session.getAttribute("currentFormTempleteId");

        List<Long> newFlowTempleteIds = new ArrayList<Long>();
        SessionObject sessionObj = (SessionObject)session.getAttribute("SessionObject");
        if(sessionObj != null){
            TemplateObject tempObj = sessionObj.getTemplateobj();
            if(tempObj != null){
                HashMap templateMap = tempObj.getFlowMap();
                if(templateMap!=null){
                    Collection coll = templateMap.values();
                    modelAndView.addObject("flowCollection", coll);
                }
                HashMap<Long, List<NewflowSetting>> currentFormNewflow = (HashMap<Long, List<NewflowSetting>>) session.getAttribute("currentFormNewflow");
                if(currentFormNewflow!=null){
                    Set<Long> keys = currentFormNewflow.keySet();
                    Iterator it = keys.iterator();
                    while(it.hasNext()){
                        Long templeteId = (Long)it.next();
                        List<NewflowSetting> newflowSettingList = (List<NewflowSetting>)currentFormNewflow.get(templeteId);
                        if(newflowSettingList != null && !newflowSettingList.isEmpty()){
                            //已经是主流程的，不能再被设置为子流程
                            newFlowTempleteIds.add(templeteId);
                        }
                    }
                    modelAndView.addObject("newFlowTempleteIds", newFlowTempleteIds);
                }
            }
        }
        modelAndView.addObject("currentFormTempleteId", currentFormTempleteId);
        return modelAndView;
    }



    /**
     * 选择关联表单
     */
    @SuppressWarnings("unchecked")
    public ModelAndView selectQuoteForm(HttpServletRequest request, HttpServletResponse response)throws Exception{
        ModelAndView modelAndView = new ModelAndView("collaboration/templete/selectQuoteForm");
        HttpSession session = request.getSession();
        Long currentFormTempleteId = (Long) session.getAttribute("currentFormTempleteId");

        List<Long> newFlowTempleteIds = new ArrayList<Long>();
        SessionObject sessionObj = (SessionObject)session.getAttribute("SessionObject");
        HashMap bindidmap = new HashMap();
        Templete templete = null;
        if(sessionObj != null){

            TemplateObject tempObj = sessionObj.getTemplateobj();
            if(tempObj != null){
//            	新增模板
            	String key = "";
            	if(tempObj.getAddMap() !=null){
            		Set keys = tempObj.getAddMap().keySet();
        			Iterator itadd = keys.iterator();
        			while(itadd.hasNext()){
        				key = (String)itadd.next();
        				if(!Strings.isDigits(key)) {
        					continue;
        				}
        				Templete templeteadd = (Templete)tempObj.getAddMap().get(key);
        				ColSummary summary = (ColSummary) XMLCoder.decoder(templeteadd.getSummary());
        				bindidmap.put(templeteadd.getId(), templeteadd.getId());
        				if(summary.getQuoteformtemId() !=null)
        					newFlowTempleteIds.add(templeteadd.getId());
        			}
            	}
    			//修改模板
    			if(tempObj.getUpdateMap() !=null){
    				Set updatekeys = tempObj.getUpdateMap().keySet();
        			Iterator itupdate = updatekeys.iterator();
        			String updatekey = "";
        			while(itupdate.hasNext()){
        				updatekey = (String)itupdate.next();
        				if(!Strings.isDigits(updatekey)) {
        					continue;
        				}
        				Templete templeteupdate = (Templete)tempObj.getUpdateMap().get(updatekey);
        				bindidmap.put(templeteupdate.getId(), templeteupdate.getId());
        				ColSummary summary = (ColSummary) XMLCoder.decoder(templeteupdate.getSummary());
        				if(summary.getQuoteformtemId() !=null)
        					newFlowTempleteIds.add(templeteupdate.getId());
        			}
    			}

    			//删除模板
    			if(tempObj.getDelMap() !=null){
    				Set delkeys = tempObj.getDelMap().keySet();
        			Iterator itdel = delkeys.iterator();
        			Long delkey = null;
        			while(itdel.hasNext()){
        				//删除map的key是long型
        				delkey = (Long)itdel.next();
        				/*if(!Strings.isDigits(delkey)) {
        					continue;
        				}
        				Templete templetedel = (Templete)tempObj.getDelMap().get(delkey);*/
        				Templete templetedel = this.templeteManager.get(delkey);
        				bindidmap.put(templetedel.getId(), templetedel.getId());
        				ColSummary summary = (ColSummary) XMLCoder.decoder(templetedel.getSummary());
        				if(summary.getQuoteformtemId() !=null)
        					newFlowTempleteIds.remove(templetedel.getId());
        			}
    			}

               HashMap templateMap = tempObj.getFlowMap();
                if(templateMap!=null){
                    Collection coll = templateMap.values();
                    if(coll !=null){
            			Iterator it = coll.iterator();
            			//已经设置关联表单的不能在被设置
            			while(it.hasNext()) {
            				FlowTempletImp flowtemplete = (FlowTempletImp)it.next();
            				if(bindidmap.get(flowtemplete.getId()) == null){
            					templete = templeteManager.get(flowtemplete.getId());
                				ColSummary summary = (ColSummary) XMLCoder.decoder(templete.getSummary());
                				if(summary.getQuoteformtemId() !=null)
                					newFlowTempleteIds.add(templete.getId());
            				}
            			}
            		}
                    modelAndView.addObject("flowCollection", coll);
                }
            }
        }
        modelAndView.addObject("newFlowTempleteIds", newFlowTempleteIds);
        modelAndView.addObject("currentFormTempleteId", currentFormTempleteId);
        return modelAndView;
    }

    /**
     * 从session中取得表单的模板列表
     * @param session
     * @return
     */
    private Collection getFormTemplateList(HttpSession session){
        SessionObject sessionObj = (SessionObject)session.getAttribute("SessionObject");
        if(sessionObj != null){
            TemplateObject tempObj = sessionObj.getTemplateobj();
            if(tempObj != null){
                HashMap templateMap = tempObj.getFlowMap();
                if(templateMap!=null){
                    Collection coll = templateMap.values();
                    return coll;
                }
            }
        }
        return null;
    }

    public ModelAndView openTempleteSelector(HttpServletRequest request, HttpServletResponse response)throws Exception{
    	ModelAndView modelAndView = this.templeteConfigTree(request, response);
    	request.setAttribute("showPortal",true);
    	modelAndView.setViewName("collaboration/templete/openTempleteSelector");
    	return modelAndView;
    }
    private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");

    @CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
    public ModelAndView showTempleteFrame(HttpServletRequest request, HttpServletResponse response)throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/templete/show_templets_frame");
    	modelAndView.addObject("categoryHTML", iOperBase.categoryHTML(templeteCategoryManager).toString());
    	return modelAndView;
    }
 
    /**
     * 模板选择页。
     * 功能： 1.支持搜索
     * 		 2.支持传入参数，显示模板（1.模板类型 2.是否全部模板）。
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
    public ModelAndView showSystemTemplets(HttpServletRequest request, HttpServletResponse response)throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/templete/show_templets");
    	User user = CurrentUser.get();
    	//查询的类型
    	Integer[] types = null;
    	//查询所有模板
    	String category = request.getParameter("category");
    	if(Strings.isBlank(category)){
    		types = new Integer[]{0,1,2,3,4,5};
    	}else{
    		String[] categorys = category.split(",");
    		types = new Integer[categorys.length];
    		for(int i = 0 ; i < categorys.length;i++){
    			types[i] = Integer.parseInt(categorys[i]);
    		}
    	}
    	if(!user.isInternal()){
    		//外部人员过滤公文。
    		List<Integer> temp = new ArrayList<Integer>();
    		for(Integer type : types){
    			if(type != 1 && type != 2 && type != 3 &&type != 4){
    				temp.add(type);
    			}
    		}
    		types = temp.toArray(new Integer[0]);
    	}
    	String condition = request.getParameter("condition");
	    String textfield = null;
	    if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(condition)) {
	    	textfield = request.getParameter("categoryId");
	    } else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
	    	textfield = request.getParameter("textfield");
	    }

	    String dataSource = request.getParameter("data");
	    List<Templete> templates  = null ;
	    if("MemberUse".equalsIgnoreCase(dataSource)){
	    	//用户能使用的模板
	    	templates   = this.templeteManager.getSystemTempletesByMemberId(user.getId(),user.getLoginAccount());
	    }else if("MemberAnalysis".equalsIgnoreCase(dataSource)){
	    	//用户能进行流程效率分析的模板
	    	String appType = request.getParameter("appType"); // 应用类型
	    	Integer appTy = Strings.isBlank(appType) ? null : Integer.parseInt(appType);
	    	if(user.isAdministrator()){
	    		templates = templeteManager.getAllSystemTempletesByAcl(user.getLoginAccount(), null);
	    	}else{
	    		templates = workFlowAnalysisAclManager.getTempleteByUserId(user.getId(),appTy );
	    	}
	    }else if("MaxScope".equalsIgnoreCase(dataSource)){
	    	//能取到最大范围的模板《本单位制作的或者外单位授权给本单位的部门、组、岗位》
	    	templates = this.templeteManager.getSystemTempletesByMemberId(user.getId(),null);
	    	Set<Long> ids = new HashSet<Long>();
	    	for(Templete t : templates){
	    		ids.add(t.getId());
	    	}
	    	List<Templete> ts = templeteManager.getAllSystemTempletesByAcl(user.getLoginAccount(), null);
	    	for(Templete t: ts){
	    		if(!ids.contains(t.getId())){
	    			templates.add(t);
	    			ids.add(t.getId());
	    		}
	    	}
	    	
	    }else{
	    	//得到指定单位的所有模板(单位制作的)
	    	templates = this.templeteManager.getAllSystemTempletesInAccount(user.getLoginAccount(), condition, textfield, types);
	    }
	    
    	if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
			for(Iterator it = templates.iterator();it.hasNext();){
			   Templete t = (Templete)it.next();
		       if(condition.equalsIgnoreCase("subject")) {
		        	if(Strings.isNotBlank(t.getSubject())
		        		&& t.getSubject().indexOf(textfield)!=-1) continue;
		       } else if(condition.equalsIgnoreCase("category")) {
		        	if(t.getCategoryId().equals(Long.valueOf(textfield))) continue;
		       }
		       it.remove();
			}
    	}
	    List<Templete> colTemp = new ArrayList<Templete>();
    	List<Templete> faTemp = new ArrayList<Templete>();
    	List<Templete> shouTemp = new ArrayList<Templete>();
    	List<Templete> qianTemp = new ArrayList<Templete>();
    	//模板id的列表
    	List <Long> tempIdCacheList = new ArrayList<Long>();
    	
    	for(Templete templete : templates){
    		//templates模板列表中可能会存在重复的模板，需要去重。
    		if( tempIdCacheList.contains( templete.getId() ) ){
    			continue;
    		}else{
    			tempIdCacheList.add( templete.getId() );
    		}
    		switch(templete.getCategoryType()){
    		case 0:
    		case 4:
    			colTemp.add(templete);
    			break;
    		case 1:
    		case 2:
    			faTemp.add(templete);
    			break;
    		case 3:
    			shouTemp.add(templete);
    			break;
    		case 5:
    			qianTemp.add(templete);
    			break;
    		}
    	}
    	if(colTemp.size() !=0){
    		TempleteCategorysWebModel categorysModel = templeteCategoryManager.getCategorys(user.getLoginAccount(), condition, textfield, colTemp);
    		modelAndView.addObject("collTemplete", colTemp);
    		modelAndView.addObject("categorysModel", categorysModel);
    		
    		List<TempleteCategory> outCategories 
    							= mergeColTempleteCategory(templates, 
    							categorysModel.getCategorys(), 
    							user.getLoginAccount());
    		modelAndView.addObject("outCategories", outCategories);
    	}

  		if(faTemp.size() !=0){
  			modelAndView.addObject("faTemplete", faTemp);
  		}
  		if(shouTemp.size() !=0){
  			modelAndView.addObject("shouTemplete", shouTemp);
  		}
  		if(qianTemp.size() !=0){
  			modelAndView.addObject("qianTemplete", qianTemp);
  		}
  		return modelAndView;
    }
    
    /**
     * 模板选择页。
     * 功能： 1.支持搜索
     * 		 2.支持传入参数，显示模板（1.模板类型 2.是否全部模板）。
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
//    public ModelAndView showSystemTempletsWorkFlowAnalysis(HttpServletRequest request, HttpServletResponse response)throws Exception{
//    	ModelAndView modelAndView = new ModelAndView("workFlowAnalysis/show_templets");
//    	List<Templete> templates= null ;
//    	// 判断是否根据授权查询模版   1表示根据授权查询 ，为空则表示查询所有系统模版
//    	String isWorkflowAnalysiszPage = request.getParameter("isWorkflowAnalysiszPage");
//    	String appType = request.getParameter("appType"); // 应用类型
//    	
//    	User user = CurrentUser.get();
//    	//查询的类型
//    	Integer[] types = null;
//    	//查询所有模板
//    	String category = request.getParameter("category");
//    	if(Strings.isBlank(category)){
//    		types = new Integer[]{0,1,2,3,4,5};
//    	}else{
//    		String[] categorys = category.split(",");
//    		types = new Integer[categorys.length];
//    		for(int i = 0 ; i < categorys.length;i++){
//    			types[i] = Integer.parseInt(categorys[i]);
//    		}
//    	}
//    	if(!user.isInternal()){
//    		//外部人员过滤公文。
//    		List<Integer> temp = new ArrayList<Integer>();
//    		for(Integer type : types){
//    			if(type != 1 && type != 2 && type != 3 &&type != 4){
//    				temp.add(type);
//    			}
//    		}
//    		types = temp.toArray(new Integer[0]);
//    	}
//    	String searchType = request.getParameter("condition");
//	    String textfield = null;
//	    if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(searchType)) {
//	    	textfield = request.getParameter("categoryId");
//	    } else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(searchType)) {
//	    	textfield = request.getParameter("textfield");
//	    }
//	    
//	    if (isWorkflowAnalysiszPage != null && Boolean.parseBoolean(isWorkflowAnalysiszPage)) {
//	    	templates = workFlowAnalysisAclManager.getTempleteByUserId(user.getId(), Strings.isBlank(appType) ? null : Integer.parseInt(appType));
//	    } else {
//	    	templates = this.templeteManager.getAllSystemTempletesInAccount(user.getAccountId(), searchType, textfield, types);
//	    }
//	    
//    	List<Templete> colTemp = new ArrayList<Templete>();
//    	List<Templete> faTemp = new ArrayList<Templete>();
//    	List<Templete> shouTemp = new ArrayList<Templete>();
//    	List<Templete> qianTemp = new ArrayList<Templete>();
//    	for(Templete templete : templates){
//    		switch(templete.getCategoryType()){
//    		case 0:
//    		case 4:
//    			colTemp.add(templete);
//    			break;
//    		case 1:
//    		case 2:
//    			faTemp.add(templete);
//    			break;
//    		case 3:
//    			shouTemp.add(templete);
//    			break;
//    		case 5:
//    			qianTemp.add(templete);
//    			break;
//    		}
//    	}
//    	if(colTemp.size() !=0){
//    		TempleteCategorysWebModel categorysModel = templeteCategoryManager.getCategorys(user.getLoginAccount(), searchType, textfield, colTemp);
//    		modelAndView.addObject("collTemplete", colTemp).addObject("categorysModel", categorysModel);
//    	}
//
//  		if(faTemp.size() !=0){
//  			modelAndView.addObject("faTemplete", faTemp);
//  		}
//  		if(shouTemp.size() !=0){
//  			modelAndView.addObject("shouTemplete", shouTemp);
//  		}
//  		if(qianTemp.size() !=0){
//  			modelAndView.addObject("qianTemplete", qianTemp);
//  		}
//  		return modelAndView;
//    }
    /**
     * 合并不同单位的协同和表单模板TempleteCategory.
     * 公文模板都是固定的发文收文签报，所以不需要合并。
     */
    public List<TempleteCategory> mergeColTempleteCategory(List<Templete> templetes ,
    		List<TempleteCategory> currentCategorys ,
    		Long currentAccountId){
    	//是否显示外单位的模板
    	boolean isShowOuter = Boolean.valueOf(Functions.getSysFlag("col_showOtherAccountTemplate").toString());
    	if(!isShowOuter) 
    		return new ArrayList<TempleteCategory>();
    	
    	
    	Map<String,TempleteCategory> nameCategory = new HashMap<String,TempleteCategory>();
    	if(Strings.isNotEmpty(currentCategorys)){
    		for(TempleteCategory category : currentCategorys){
        		nameCategory.put(category.getName(), category);
        	}
    	}

    	List<TempleteCategory> outerCategory = new ArrayList<TempleteCategory>();
 	    for (Templete templete : templetes) {
 	    	boolean isColOrForm = templete.getCategoryType().equals(TempleteCategory.TYPE.collaboration_templete.ordinal())
 	    							|| templete.getCategoryType().equals(TempleteCategory.TYPE.form.ordinal());
 			if(!templete.getOrgAccountId().equals(currentAccountId)
 				&& isColOrForm){//外单位模板
 				if(templete.getCategoryId() != 0){//等于0是顶层的模板
 					TempleteCategory tc = templeteCategoryManager.get(templete.getCategoryId());
 					if(tc != null){
 						TempleteCategory n = nameCategory.get(tc.getName());
 						if(n != null){
 							templete.setCategoryId(n.getId());
 						}else{
 							outerCategory.add(tc);
 							nameCategory.put(tc.getName(), tc);
 						}
 					}
 				}
 			}
 		}
 	    return outerCategory;
    }
    /**
     * 模板栏目，展现分类页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author lilong
     */
    public ModelAndView showPortalCatagory(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("collaboration/templete/showPortalCatagory");
    	return modelAndView;
    }
}