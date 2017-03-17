package com.seeyon.v3x.collaboration.controller;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMAbstractNode.NodeType;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMActor;
import net.joinwork.bpm.definition.BPMHumenActivity;
import net.joinwork.bpm.definition.BPMParticipant;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMStatus;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.engine.execute.CaseManager;
import net.joinwork.bpm.engine.wapi.ProcessDefManager;
import net.joinwork.bpm.engine.wapi.WAPIFactory;
import net.joinwork.bpm.engine.wapi.WorkItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.Constantform;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.trigger.TriggerHelper;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.define.form.SeeyonFormImpl;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.utils.FormHelper;

import com.seeyon.cap.info.domain.InfoSummaryCAP;
import com.seeyon.cap.info.domain.WorkflowDataCAP;
import com.seeyon.cap.info.manager.InfoManagerCAP;
import com.seeyon.cap.info.manager.InfoSummaryManagerCAP;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.his.manager.HisAffairManager;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BaseBulletinManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColComment;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.FormBody;
import com.seeyon.v3x.collaboration.domain.FormContent;
import com.seeyon.v3x.collaboration.domain.LockObject;
import com.seeyon.v3x.collaboration.domain.NewflowRunning;
import com.seeyon.v3x.collaboration.domain.NewflowSetting;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SeeyonPolicy;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.domain.WorkflowData;
import com.seeyon.v3x.collaboration.domain.WorkflowDataDetail;
import com.seeyon.v3x.collaboration.event.CollaborationCancelEvent;
import com.seeyon.v3x.collaboration.event.CollaborationFormApprovalEvent;
import com.seeyon.v3x.collaboration.event.CollaborationStartEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.his.manager.HisColManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColManagerFacade;
import com.seeyon.v3x.collaboration.manager.ColQuoteformRecordManger;
import com.seeyon.v3x.collaboration.manager.ColRelationAuthorityManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.NewflowManager;
import com.seeyon.v3x.collaboration.manager.impl.BranchArgs;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.collaboration.manager.impl.ColMessageHelper;
import com.seeyon.v3x.collaboration.manager.impl.FormLockManager;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.util.ProcessResolver;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.collaboration.webmodel.NewflowModel;
import com.seeyon.v3x.collaboration.webmodel.ProcessModeSelectorModel;
import com.seeyon.v3x.collaboration.webmodel.StatModel;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.authenticate.domain.MemberAgentBean;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentEditHelper;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.BrowserEnum;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.isignature.ISignatureHtmlManager;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.permission.domain.Permission;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.security.AccessControlBean;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ThreadLocalUtil;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.dee.common.base.util.UuidUtil;
import com.seeyon.v3x.doc.domain.DocFromPotent;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.index.convert.Convertor;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.menu.manager.MenuFunction;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.BaseNewsManager;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.PopSelectParseUtil;
import com.seeyon.v3x.util.StatUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.manager.MailBoxManager;
import com.seeyon.v3x.webmail.manager.WebMailManager;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.NodeAddition;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.PersonInfo;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisAclManager;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;
import com.seeyon.v3x.worktimeset.manager.WorkTimeManager;

/**
 * 协同应用controller
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-18
 */
public class CollaborationController extends BaseController {
    private static final Log log = LogFactory.getLog(CollaborationController.class);
    private final static Log branchLog = LogFactory.getLog(BPMTransition.class);
    private ColQuoteformRecordManger colQuoteformRecordManger ;

    public void setColQuoteformRecordManger(
			ColQuoteformRecordManger colQuoteformRecordManger) {
		this.colQuoteformRecordManger = colQuoteformRecordManger;
	}

	private ColManager colManager;

	private ColManagerFacade colManagerFacade;

	private HisColManager hisColManager;

	private HisAffairManager hisAffairManager;

    private OrgManager orgManager;

    private MetadataManager metadataManager;

    private AttachmentManager attachmentManager;

    private TempleteManager templeteManager;

    private TempleteCategoryManager templeteCategoryManager;

    private AffairManager affairManager;

    private UpdateIndexManager updateIndexManager;

    private FileToExcelManager fileToExcelManager;

    private DocHierarchyManager docHierarchyManager;

    private PermissionManager permissionManager;

    private ProjectManager projectManager;

    private WebMailManager webMailManager;

    private FileManager fileManager;

    private BulTypeManager bulTypeManager;

    private ColSuperviseManager colSuperviseManager;

    private NewsTypeManager newsTypeManager;

    private BulDataManager bulDataManager;

    private NewsDataManager newsDataManager;

    private UserMessageManager userMessageManager;

    private IndexManager indexManager;

    private NewflowManager newflowManager;

    private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");

    //private OperationlogManager operationlogManager;

    private FlowPermManager flowPermManager;

    private AppLogManager appLogManager;

    private ProcessLogManager processLogManager;

    private EdocManager edocManager;

    private WorkTimeManager workTimeManager;

    private ColRelationAuthorityManager colRelationAuthorityManager;
    private WorkFlowAnalysisAclManager workFlowAnalysisAclManager;

	private SpaceManager spaceManager;
	private ISignatureHtmlManager iSignatureHtmlManager;
	private InfoManagerCAP infoManagerCAP;
	private InfoSummaryManagerCAP infoSummaryManagerCAP;
	
	// 2017-3-14 诚佰公司 添加
	private OrgManagerDirect orgManagerDirect;
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	// 诚佰公司

	public void setInfoManagerCAP(InfoManagerCAP infoManagerCAP) {
		this.infoManagerCAP = infoManagerCAP;
	}

	public void setiSignatureHtmlManager(ISignatureHtmlManager iSignatureHtmlManager) {
		this.iSignatureHtmlManager = iSignatureHtmlManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setWorkFlowAnalysisAclManager(
			WorkFlowAnalysisAclManager workFlowAnalysisAclManager) {
		this.workFlowAnalysisAclManager = workFlowAnalysisAclManager;
	}
	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
	public FormDaoManager getFormDaoManager() {
		return formDaoManager;
	}
    public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
    public void setColManager(ColManager colManager) {
        this.colManager = colManager;
    }
    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }
    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
    public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }
    public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }
    public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager){
    	this.docHierarchyManager = docHierarchyManager;
    }
	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}
	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}
	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
    public void setNewflowManager(NewflowManager newflowManager) {
        this.newflowManager = newflowManager;
    }
    public void setAppLogManager(AppLogManager appLogManager) {
        this.appLogManager = appLogManager;
    }
    public void setFlowPermManager(FlowPermManager flowPermManager) {
		this.flowPermManager = flowPermManager;
	}
	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}
	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}

	public void setWorkTimeManager(WorkTimeManager workTimeManager) {
		this.workTimeManager = workTimeManager;
	}

    public void setColRelationAuthorityManager(ColRelationAuthorityManager colRelationAuthorityManager) {
		this.colRelationAuthorityManager = colRelationAuthorityManager;
	}
	//public void setOperationlogManager(OperationlogManager operationlogManager) {
   //     this.operationlogManager = operationlogManager;
   // }

    public void setHisColManager(HisColManager hisColManager) {
		this.hisColManager = hisColManager;
	}

	public void setHisAffairManager(HisAffairManager hisAffairManager) {
		this.hisAffairManager = hisAffairManager;
	}

	public void setInfoSummaryManagerCAP(InfoSummaryManagerCAP infoSummaryManagerCAP) {
		this.infoSummaryManagerCAP = infoSummaryManagerCAP;
	}
	
	private boolean isParentWrokFlowTemplete(Long templeteId){
		Templete t = getParentSystemTemplete(templeteId);
		if(t == null)
			return false;
		if(Templete.Type.workflow.name().equals(t.getType()))
			return true;
		else
			return false;
	}
	private boolean isParentSystemTemplete(Long templeteId){
		Templete t = getParentSystemTemplete(templeteId);
		if(t == null)
			return false;
		if(t.getIsSystem())
			return true;
		else
			return false;
	}
	private boolean isParentTextTemplete(Long templeteId){
		Templete t = getParentSystemTemplete(templeteId);
		if(t == null)
			return false;
		if(Templete.Type.text.name().equals(t.getType()))
			return true;
		else
			return false;
	}
	private boolean isParentColTemplete(Long templeteId){
		Templete t = getParentSystemTemplete(templeteId);
		if(t == null)
			return false;
		if(Templete.Type.templete.name().equals(t.getType()))
			return true;
		else
			return false;
	}
	/**
	 * 得到父级系统模板
	 * @param templeteId
	 * @return
	 */
	private Templete getParentSystemTemplete(Long templeteId){
		if(templeteId == null){
			return null;
		}
		boolean needQueryParent = true;
		Templete t  = null;
		while(needQueryParent){
			t = templeteManager.get(templeteId);
			if(t== null){
				needQueryParent = false;
				return null;
			}
			if(t.getIsSystem()){
				needQueryParent = false;
				return t;
			}
			if(t.getFormParentId() == null){
				needQueryParent = false;
				return null;
			}
			templeteId = t.getFormParentId();
		}
		return t;
	}
    /**
     * 新建协同
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView newColl(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {

        ModelAndView modelAndView = new ModelAndView("collaboration/newCollaboration");
        String from = request.getParameter("from");
        String s_summaryId = request.getParameter("summaryId");
        String templeteId = request.getParameter("templeteId");

        if(!MenuFunction.hasNewCollaboration() && StringUtils.isBlank(templeteId) && StringUtils.isBlank(s_summaryId)){
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
            out.print("alert(\"" + ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE, "role.security.warning") + "\"); window.close();");
        	out.println("</script>");
        	return null;
        }

        BPMProcess process = null;
        ColSummary summary = null;
        ColBody body = null;
        Affair affair = null;
        String senderOpinionContent = null;
        List<Attachment> atts = null;
        boolean canDeleteOriginalAtts = true;
        boolean cloneOriginalAtts = false;
        Long archiveId = null;
        String archiveName = "";
        Long projectId = null;
        User user = CurrentUser.get();
        Templete templete = null ;
        String secretLevel = null;
        Integer secret = null;
        //成发集团项目 程炯 为页面传入当前用户的密级 begin
        Integer peopleSecretLevel = orgManager.getMemberById(user.getId()).getSecretLevel();
        modelAndView.addObject("peopleSecretLevel", peopleSecretLevel);
        //end
        
        //对已有流程的协同（重复发起、转发、保存待发），在页面上存储其定义XML
        if (StringUtils.isNotBlank(templeteId)) {//调用模板
        	modelAndView.addObject("isFromTemplate", true);
			Long id = new Long(templeteId);
			//判断权限
			String isTemplate=templeteManager.checkTemplete(id, user.getId());
	        if(isTemplate != null){
	        	request.setAttribute("errMsg",isTemplate);
	        	request.setAttribute("errMsgAlert", true);
	        	return super.redirectModelAndView(BaseController.REDIRECT_BACK);
	        }

			if(!FormBizConfigUtils.validate(modelAndView, request, response, FormBizConfigConstants.MENU_NEW_AFFAIRS))
				return null;

			templete = (Templete)modelAndView.getModel().get("templete");
			String formbodyxml = "";
	
			if(templete != null) {
			    String formtitle = templete.getSubject();
			    if(Strings.isNotBlank(templete.getCollSubject())){
			    	//formtitle = templete.getCollSubject() ;
			    	modelAndView.addObject("collSubjectNotEdit", "true") ;
			    	modelAndView.addObject("collSubject", templete.getCollSubject()) ;
			    }
			    Long templeteFormparnetId = templete.getFormParentId();
			    modelAndView.addObject("temformParentId", templeteFormparnetId);
			    modelAndView.addObject("formtitle", formtitle);
			    modelAndView.addObject("workflowRule", templete.getWorkflowRule());
				modelAndView.addObject("fromTemplate","true");
				modelAndView.addObject("standardDuration", templete.getStandardDuration());
                projectId = templete.getProjectId();
//				协同模板取summary和附件
				summary = (ColSummary)XMLCoder.decoder(templete.getSummary());
				//格式模板设置默认值
				if(Templete.Type.text.name().equals(templete.getType())){
		            summary.setCanForward(true);
		            summary.setCanArchive(true);
		            summary.setCanDueReminder(true);
		            summary.setCanEditAttachment(true);
		            summary.setCanModify(true);
		            summary.setCanTrack(true);
		            summary.setCanEdit(true);
				}
				if(templeteFormparnetId !=null){//表单另存个人模版
					if("FORM".equals(templete.getBodyType())) {
						formbodyxml = templete.getBody();
						formbodyxml = www.seeyon.com.v3x.form.utils.StringUtils.Java2JavaScriptStr(formbodyxml);
						templete = templeteManager.get(templeteFormparnetId);
						if(Strings.isNotBlank(templete.getCollSubject())){
					    	modelAndView.addObject("collSubjectNotEdit", "true") ;
					    	modelAndView.addObject("collSubject", templete.getCollSubject()) ;
					    }
					}
				}
				//正文模板不要流程
				if(!Templete.Type.text.name().equals(templete.getType())){
					process = BPMProcess.fromXML(templete.getWorkflow()); //重新生成，因为要取新的节点名称
				}

				//流程模板不要正文
				if(!Templete.Type.workflow.name().equals(templete.getType())){
					body = (ColBody)XMLCoder.decoder(templete.getBody());
					cloneOriginalAtts = true;
				}
				//2012-8-30 成发集团项目 程炯 按模版显示密级 begin
                if(summary.getSecretLevel() != null){
                	secret = summary.getSecretLevel();
                    secretLevel = getSecretLevelName(summary,secret);
                    modelAndView.addObject("secret", secret);
                 }else{
                	// 2017-01-13 诚佰公司 注释新增
                	/*secretLevel = getSecretLevelName(summary,1);
                	modelAndView.addObject("secret", 1);*/
                	secretLevel = "";
                	modelAndView.addObject("secret", "");
                	// 诚佰公司
                 }
               modelAndView.addObject("flowSecretLevel", secretLevel);
				//end
				
				modelAndView.addObject("templete", templete);  
				
				if(Templete.Type.templete.name().equals(templete.getType())){
					if(templeteFormparnetId !=null)//表单另存个人模版
						atts = attachmentManager.getByReference(id, id);
					else
					    atts = attachmentManager.getByReference(templete.getId(), templete.getId());

					//canDeleteOriginalAtts = false;    //不允许删除原附件

					if(summary != null){
			            ColOpinion senderOpinion = summary.getSenderOpinion();
			            if(senderOpinion != null){//发起人附言
			            	senderOpinionContent = senderOpinion.getContent();
			            }
					}

					if(summary.getArchiveId() != null){
			        	archiveId = summary.getArchiveId();
			        	archiveName = docHierarchyManager.getNameById(archiveId);
			        }

					cloneOriginalAtts = true;

		            if("FORM".equals(templete.getBodyType())){
//		            	String[] formInfo = FormHelper.getFormPolicy(process);
//		            	String runtimeView = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], null, null, null, formInfo[3],false);
		            	String runtimeView = "";
				        String masterId = "";
				        String[] formInfo = FormHelper.getFormPolicy(process);
				        String quoteformtemId = "";
				        if(summary.getQuoteformtemId() !=null)
				        	quoteformtemId = summary.getQuoteformtemId().toString();
				        modelAndView.addObject("quoteformtemId", quoteformtemId);
		            	String quoteFromsign = request.getParameter("quoteFromsign");
		            	if("quoteFrom".equals(quoteFromsign)){
		            		List quotecontenlist = new ArrayList();
		            		String quoteformid = request.getParameter("formid");
			            	String quoteoperationid = request.getParameter("operationid");
			            	String quoteaffairid = request.getParameter("affairid");
			            	String quotesummaryid = request.getParameter("summaryid");
			            	quotecontenlist = colManager.getFormContent(quoteaffairid ,quotesummaryid,quoteformid,quoteoperationid);
			            	if(quotecontenlist.size() !=0){
			            		runtimeView = quotecontenlist.get(0).toString();
				            	masterId = quotecontenlist.get(1).toString();
			            	}
			            	modelAndView.addObject("masterId", masterId);
			            	modelAndView.addObject("quoteFromsign", quoteFromsign);
			            	modelAndView.addObject("parentformSummaryId", quotesummaryid);
			            	if(atts == null){
			            		atts = new ArrayList<Attachment>();
			            	}
			            	atts.addAll(this.getMainRunAtt(Long.valueOf(quotesummaryid))) ;
		            	}else{
			            	 runtimeView = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], null, null, null, formInfo[3],false);
		            	}
		            	if(!"".equals(formbodyxml) && !"null".equals(formbodyxml) && formbodyxml !=null){
		            		StringBuffer sbxml = new StringBuffer();
		            		//int xslStart = runtimeView.indexOf("&&&&&&&&  data_start  &&&&&&&&");
			            	//int inputStart = runtimeView.indexOf("&&&&&&&&  input_start  &&&&&&&&");
			            	String xmlStart = "&&&&&&&&  newdata_start  &&&&&&&&";
			            	//String startxml = runtimeView.substring(0,xslStart);
			            	//String inputxml = runtimeView.substring(inputStart);
			            	//String C_sOutHead_data="&&&&&&&&  data_start  &&&&&&&& ";
			            	//String data_startxml =C_sOutHead_data + formbodyxml.replaceAll("\"", "'");
			            	sbxml.append(runtimeView);
			            	sbxml.append(xmlStart);
			            	sbxml.append(formbodyxml);
			            	runtimeView =sbxml.toString();
		            	}
		            	
		            	modelAndView.addObject("runtimeView", runtimeView);
		            	modelAndView.addObject("isForm", "true");
		            	modelAndView.addObject("formappid", formInfo[0]);
		            	modelAndView.addObject("formid", formInfo[1]);
		            	modelAndView.addObject("operationid", formInfo[2]);
		            }
		            else{
		            	modelAndView.addObject("isForm", "false");
		            }
		            List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(templete.getId(),ApplicationCategoryEnum.collaboration.ordinal());
		            modelAndView.addObject("branchs", ColHelper.transformBranch(branchs, true));
		            if(branchs != null) {
		            	//TODO 组分支实现方式有问题，后续解决
		            	List<V3xOrgEntity> teams = this.orgManager.getUserDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
		            	if(teams != null && !teams.isEmpty()){
		            		V3xOrgTeam team = null;
		            		List<V3xOrgTeam> inTeams = new ArrayList<V3xOrgTeam>();
		            		for(V3xOrgEntity entity:teams){
		            			team = (V3xOrgTeam)entity;
		            			if(team != null && team.getAllMembers() != null && team.getAllMembers().contains(user.getId()))
		            				inTeams.add(team);
		            		}
		            		modelAndView.addObject("teams", inTeams);
		            	}
		            	V3xOrgMember mem = orgManager.getMemberById(user.getId());
		            	List<MemberPost> secondPosts = mem.getSecond_post();
		            	modelAndView.addObject("secondPosts", secondPosts);
		            }

                    // private packingSuperviseFromTemplete
		            ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),templeteFormparnetId==null?templete.getId():templeteFormparnetId);
		            if(detail != null) {
		            	Long terminalDate = detail.getTemplateDateTerminal();
		            	if(null!=terminalDate && terminalDate!=0 ){
		            		Date superviseDate = workTimeManager.getComputeDate(new Date(), "+", terminalDate,"day", templete.getOrgAccountId());
			            	String date = Datetimes.format(superviseDate, Datetimes.datetimeWithoutSecondStyle);
			            	modelAndView.addObject("superviseDate", date);
		            	}else if(detail.getAwakeDate() != null) {
		            		modelAndView.addObject("superviseDate", Datetimes.format(detail.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
		            	}
		            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
		            	Set<String> sIdSet = new HashSet<String>();
		            	for(ColSupervisor supervisor:supervisors)
		            		sIdSet.add(supervisor.getSupervisorId().toString());
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
		            		if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())){
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
		            			if(!haveManager){
			            			modelAndView.addObject("noDepManager", "true");
			            		}

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
                        if(!sIdSet.isEmpty()){
                            StringBuffer names = new StringBuffer();;
                            StringBuffer ids = new StringBuffer();
    		            	for(String s : sIdSet){
    		            		V3xOrgMember mem = orgManager.getMemberById(Long.valueOf(s));
    		            		if(mem!=null){
                                    if(ids.length() > 0){
                                        ids.append(",");
                                        names.append(",");
                                    }
                                    ids.append(mem.getId());
        		            		names.append(mem.getName());
    		            		}
    		            	}
    		            	modelAndView.addObject("unCancelledVisor", ids.toString());
    		            	modelAndView.addObject("colSupervisors", ids.toString());
    		            	modelAndView.addObject("colSupervisorNames", names.toString());
                        }
		            	modelAndView.addObject("colSupervise", detail);
		            }
				}
			}
			else {
            	modelAndView.addObject("alertMsg", "col_template_deleted");
            }

        }
        else if ("resend".equals(from)) {    //重复发起
            long summaryId = Long.parseLong(s_summaryId);
            summary = colManager.getColSummaryById(summaryId, true);

            body = summary.getFirstBody();

            atts = attachmentManager.getByReference(summaryId, summaryId);

            List<ColOpinion> senderOpinions = summary.getAllSenderOpinion();
            if(senderOpinions != null && !senderOpinions.isEmpty()){//发起人附言
            	senderOpinionContent = "";
            	if(atts == null){
            		atts = new ArrayList<Attachment>();
            	}

            	List<Long> senderOpinionIds = new ArrayList<Long>();
            	for (ColOpinion opinion : senderOpinions) {
            		senderOpinionContent += opinion.getContent() + "\r\n";
            		senderOpinionIds.add(opinion.getId());
				}

            	atts.addAll(attachmentManager.getByReference(summaryId, senderOpinionIds.toArray(new Long[senderOpinions.size()])));
            }

            if (summary.getProcessId() != null) {
                process = ColHelper.getRunningProcessByProcessId(summary.getProcessId());
                if(process != null){
                	process = BPMProcess.fromXML(process.toXML());
                	List<BPMAbstractNode> activities =  process.getActivitiesList();
                	for (BPMAbstractNode n : activities) {
						n.getSeeyonPolicy().setIsDelete("false");
						n.getSeeyonPolicy().setIsPass("success");
					}
                }

                if(summary.getTempleteId() != null){
		            List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(summary.getTempleteId(), ApplicationCategoryEnum.collaboration.ordinal());
		            if(branchs != null) {
		            	modelAndView.addObject("branchs", branchs);
		            	//modelAndView.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
		            	//wangchw:fix:40316:表单被调用后直接发送可以正常发送，保存待发后再发送就提示分支条件不满足
		            	List<V3xOrgEntity> teams = this.orgManager.getUserDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
		            	if(teams != null && !teams.isEmpty()){
                            V3xOrgTeam team = null;
                            List<V3xOrgTeam> inTeams = new ArrayList<V3xOrgTeam>();
                            for(V3xOrgEntity entity:teams){
                                team = (V3xOrgTeam)entity;
                                if(team != null && team.getAllMembers() != null && team.getAllMembers().contains(user.getId()))
                                    inTeams.add(team);
                            }
                            modelAndView.addObject("teams", inTeams);
                        }
		            	V3xOrgMember mem = orgManager.getMemberById(user.getId());
		            	List<MemberPost> secondPosts = mem.getSecond_post();
		            	modelAndView.addObject("secondPosts", secondPosts);
		            }
                }
            }
            //预归档
            if(summary.getArchiveId() != null){
	        	archiveId = summary.getArchiveId();
	        	archiveName = docHierarchyManager.getNameById(archiveId);
	        }
            
          //2012-8-30 成发集团项目 程炯 待发显示密级 begin
			 if(summary.getSecretLevel() != null){
	            secret = summary.getSecretLevel();
	            secretLevel = getSecretLevelName(summary,secret);
	          }
			modelAndView.addObject("secret", secret);
			modelAndView.addObject("flowSecretLevel", secretLevel);
			modelAndView.addObject("secretFlag", "resend");
			//end
            
            //来自"重新发起" 不允许修改标题
            modelAndView.addObject("readOnly", Boolean.TRUE);
            cloneOriginalAtts = true;
            canDeleteOriginalAtts = true;
            if(summary.getTempleteId() != null){
            	modelAndView.addObject("templateId", summary.getTempleteId());
            	templete = this.templeteManager.get(summary.getTempleteId());
                modelAndView.addObject("templete", templete);
            	modelAndView.addObject("isFromTemplate", summary.getTempleteId() != null);
            }

            projectId = summary.getProjectId();
        }
        else if (s_summaryId != null) { // 来自待发
            long summaryId = Long.parseLong(s_summaryId);
            summary = colManager.getColSummaryById(summaryId, true);
            body = summary.getFirstBody();
            atts = attachmentManager.getByReference(summaryId, summaryId);
            if(body != null && "FORM".equals(body.getBodyType())){
	            if(atts == null){
	        		atts = new ArrayList<Attachment>();
	        	}
	        	atts.addAll(this.getMainRunAtt(Long.valueOf(summaryId)));
	        	modelAndView.addObject("formappid", summary.getFormAppId());
            }
            Long templateId = summary.getTempleteId();
            if(templateId != null) {
            	templeteId=templateId.toString();
            	modelAndView.addObject("isFromTemplate", true);
	            templete = templeteManager.get(templateId);
                if(templete != null){
    	            //modelAndView.addObject("isFromTemplate", templete.getIsSystem());
    	            String formtitle = templete.getSubject();
    				Long templeteFormparnetId = templete.getFormParentId();
    				modelAndView.addObject("temformParentId", templeteFormparnetId);
    				modelAndView.addObject("formtitle", formtitle);
    	            modelAndView.addObject("workflowRule", templete.getWorkflowRule());
    	            modelAndView.addObject("templete", templete);
    	            modelAndView.addObject("standardDuration", templete.getStandardDuration());
    	            ColSummary oldsummary = (ColSummary)XMLCoder.decoder(templete.getSummary());
    	            summary.setArchiverFormid(oldsummary.getArchiverFormid());
    			    if(Strings.isNotBlank(templete.getCollSubject())){
    			    	//formtitle = templete.getCollSubject() ;
    			    	modelAndView.addObject("collSubjectNotEdit", "true") ;
    			    	modelAndView.addObject("collSubject", templete.getCollSubject()) ;
    			    }
    	           // modelAndView.addObject("formtitle", summary.getSubject());
                    //待发调用模板的协同不可删除附件
                    //canDeleteOriginalAtts = false;
    	            List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(templateId,ApplicationCategoryEnum.collaboration.ordinal());
    	            if(branchs != null) {
//    	            	显示分支条件使用流程中保留的，如果为空使用模板中的
    	            	branchs = ColHelper.updateBranchByProcess(summary.getProcessId(),branchs);
    	            	modelAndView.addObject("branchs", branchs);
    	            	//modelAndView.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
    	            	//wangchw:fix:40316:表单被调用后直接发送可以正常发送，保存待发后再发送就提示分支条件不满足
		            	List<V3xOrgEntity> teams = this.orgManager.getUserDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
                        if(teams != null && !teams.isEmpty()){
                            V3xOrgTeam team = null;
                            List<V3xOrgTeam> inTeams = new ArrayList<V3xOrgTeam>();
                            for(V3xOrgEntity entity:teams){
                                team = (V3xOrgTeam)entity;
                                if(team != null && team.getAllMembers() != null && team.getAllMembers().contains(user.getId()))
                                    inTeams.add(team);
                            }
                            modelAndView.addObject("teams", inTeams);
                        }
		            	V3xOrgMember mem = orgManager.getMemberById(user.getId());
    	            	List<MemberPost> secondPosts = mem.getSecond_post();
    	            	modelAndView.addObject("secondPosts", secondPosts);
    	            }
                }else {
                	//如果模板不存在不让发送
                	modelAndView = new ModelAndView("common/redirect");
                	String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource","templete.notExist");
                	modelAndView.addObject("redirectURL",BaseController.REDIRECT_BACK);
    	        	modelAndView.addObject("errMsg",errMsg);
    	        	return modelAndView;
                }
            }

            List<ColOpinion> senderOpinions = summary.getAllSenderOpinion();
            if(senderOpinions != null && !senderOpinions.isEmpty()){//发起人附言
            	senderOpinionContent = "";
            	if(atts == null){
            		atts = new ArrayList<Attachment>();
            	}

            	List<Long> senderOpinionIds = new ArrayList<Long>();
            	for (ColOpinion opinion : senderOpinions) {
            		senderOpinionContent += opinion.getContent() + "\r\n";
            		senderOpinionIds.add(opinion.getId());
				}

            	//atts.addAll(attachmentManager.getByReference(summaryId, senderOpinionIds.toArray(new Long[senderOpinions.size()])));
            }

            String affairId = request.getParameter("affairId");

            //预归档
            if(summary.getArchiveId() != null){
	        	archiveId = summary.getArchiveId();
	        	archiveName = docHierarchyManager.getNameById(archiveId);
	        }
            
          //2012-8-30 成发集团项目 程炯 重复发起显示密级 begin
			 if(summary.getSecretLevel() != null){
	            secret = summary.getSecretLevel();
	            secretLevel = getSecretLevelName(summary,secret);
	          }
			modelAndView.addObject("secret", secret);
			modelAndView.addObject("flowSecretLevel", secretLevel);
			modelAndView.addObject("secretFlag", "wait");
			//end
            
            if (summary.getProcessId() != null) {
            	try {
            		process = ColHelper.getRunningProcessByProcessId(summary.getProcessId());

            		if("FORM".equals(body.getBodyType())){
                    	String[] formInfo = FormHelper.getFormPolicy(process);
                    	String masterId = body.getContent();
                    	String runtimeView = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, s_summaryId, affairId, formInfo[3],false);
                    	modelAndView.addObject("runtimeView", runtimeView);
                    	modelAndView.addObject("isForm", "true");
                    	modelAndView.addObject("masterId", masterId);
                    }else
                    	modelAndView.addObject("isForm", "false");
				}
				catch (Exception e) {
					log.error("获取流程process对应人员信息异常", e);
					modelAndView.addObject("获取流程所有人员信息失败", new ColException(e, "exception.newColl.error"));
				}
				modelAndView.addObject("templateId", summary.getTempleteId());
            }

            projectId = summary.getProjectId();

            affair = this.affairManager.getWaitSendBysummaryIdAndState(ApplicationCategoryEnum.collaboration, summaryId, StateEnum.col_waitSend.key());

            ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(),summaryId);
            if(detail != null) {
            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
            	StringBuffer ids = new StringBuffer();
            	for(ColSupervisor supervisor:supervisors)
            		ids.append(supervisor.getSupervisorId() + ",");
            	modelAndView.addObject("colSupervisors", ids.substring(0, ids.length()-1));
            	modelAndView.addObject("colSupervise", detail);
            	modelAndView.addObject("superviseDate", Datetimes.format(detail.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
            	modelAndView.addObject("colSupervisorNames", detail.getSupervisors());
            	if(templete !=null) {
            		if(templete.getFormParentId()!=null)
            			this.processTemplateSupervisor(templete.getFormParentId(), modelAndView, user);
            		else
            			this.processTemplateSupervisor(templete.getId(), modelAndView, user);
            	}
            }
            modelAndView.addObject("trackIds",getTrackIds(Strings.isNotBlank(affairId)?Long.valueOf(affairId):null));
        }
        else if("relatePeople".equals(from)){
        	String memberId = request.getParameter("memberId");
        	boolean checkLevelScope = Functions.checkLevelScope(user.getId(), NumberUtils.toLong(memberId));
        	if (!checkLevelScope) {
    			PrintWriter out = response.getWriter();
    			out.println("<script>");
    			out.println("alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "message.checkLevelScope.alert") + "');");
    			out.println("history.back();");
    			out.println("</script>");
    			return null;
    		}
        	List<Long> memberIds = new ArrayList<Long>();
        	memberIds.add(Long.parseLong(memberId));
        	int flowType = 1;
        	FlowData flowData = FlowData.flowdataFromMemberIds(flowType, memberIds, orgManager);

        	String processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, null, false);
        	process = ColHelper.getProcess(processId);
        	ColHelper.deleteReadyProcess(processId);
        }
        else if("a8genius".equals(from)){
        	//outlook邮件转协同(先提供测试接口后期再作调整)
        	summary = new ColSummary();
            summary.setCanForward(true);
            summary.setCanArchive(true);
            summary.setCanDueReminder(true);
            summary.setCanEditAttachment(true);
            summary.setCanModify(true);
            summary.setCanTrack(true);
            summary.setCanEdit(true);

            String source = request.getParameter("source");
            //这里为了发送协同时删除原有文件，设置一下关联实体id
            Long referenceId = new Long(UUIDLong.longUUID());

            //设置附件
            String[] attachids = request.getParameterValues("attachid");
        	if(atts == null){
        		atts = new ArrayList<Attachment>();
        	}
        	List<V3XFile> files = new ArrayList<V3XFile>();
        	if(attachids!=null){
        		for (String attachid : attachids) {
        			Long id = Long.parseLong(attachid);
        			V3XFile file = fileManager.getV3XFile(id);
        			files.add(file);
        			Attachment att = new Attachment();
        			att.setIdIfNew();
        			att.setCategory(ApplicationCategoryEnum.collaboration.getKey());
        			att.setType(file.getType());
        			att.setFilename(file.getFilename());
        			att.setMimeType(file.getMimeType());
        			att.setFileUrl(file.getId());
        			att.setCreatedate(file.getCreateDate());
        			att.setSize(file.getSize());
        			att.setReference(referenceId);
        			att.setSubReference(referenceId);
        			atts.add(att);
        			if(source==null||StringUtils.isBlank(source)){
        				summary.setSubject(file.getFilename());
        			}
        		}
        	}
        	//这里从精灵还必须要创建附件，不然发送时存在问题，不能删除已上传的文件
        	attachmentManager.create(files, ApplicationCategoryEnum.collaboration, referenceId, referenceId);
        	modelAndView.addObject("source", request.getParameter("source"));
        	modelAndView.addObject("from", from);
        	modelAndView.addObject("referenceId", referenceId);
        }

        if(summary == null) {
            summary = new ColSummary();
            summary.setCanForward(true);
            summary.setCanArchive(true);
            summary.setCanDueReminder(true);
            summary.setCanEditAttachment(true);
            summary.setCanModify(true);
            summary.setCanTrack(true);
            summary.setCanEdit(true);
        }
        if(body == null){
            body = new ColBody();
        }
        if(affair == null){
            affair = new Affair();
            affair.setIsTrack(true);
        }

		//新建项目协同
        String projectIdStr = request.getParameter("projectId");
        List<ProjectSummary> projectList = projectManager.getProjectList();
		if("relateProject".equals(from)){
			projectId = Long.parseLong(projectIdStr);
		}
        else{
            if(projectId != null){
                ProjectSummary p = projectManager.getProject(projectId);
                if(p != null&&!ProjectSummary.state_delete.equals(p.getProjectState())){
                    if(projectList == null){
                        projectList = new ArrayList<ProjectSummary>();
                        projectList.add(p);
                    }
                    else if(!projectList.contains(p)){
                        projectList.add(p);
                    }
                }else{
                	projectId=null;
                }
            }
        }
		if (Strings.isNotBlank(templeteId)) {
			if (templete==null)
				templeteManager.get(Long.parseLong(templeteId));
			if(templete!=null) {
				ColSummary colSummary = (ColSummary)XMLCoder.decoder(templete.getSummary());
				modelAndView.addObject("isTempleteHasDeadline", colSummary.getDeadline()!= null && colSummary.getDeadline() != 0);
				modelAndView.addObject("isTempleteHasRemind", colSummary.getAdvanceRemind()!=null && colSummary.getAdvanceRemind() != -1);
				modelAndView.addObject("isParentWrokFlowTemplete", isParentWrokFlowTemplete(templete.getFormParentId()));
			    modelAndView.addObject("isParentTextTemplete", isParentTextTemplete(templete.getFormParentId()));
			    modelAndView.addObject("isParentColTemplete", isParentColTemplete(templete.getFormParentId()));
			    modelAndView.addObject("isFromSystemTemplete", isParentSystemTemplete(templete.getId()));
			}
		}else{
			modelAndView.addObject("isParentWrokFlowTemplete", false);
		    modelAndView.addObject("isParentTextTemplete", false);
		    modelAndView.addObject("isParentColTemplete", false);
		    modelAndView.addObject("isFromSystemTemplete",false);
		}

		modelAndView.addObject("projectId", projectId);
		modelAndView.addObject("projectList", projectList);

        modelAndView.addObject("archiveName", archiveName);

        modelAndView.addObject("attachments", atts);
        modelAndView.addObject("canDeleteOriginalAtts", canDeleteOriginalAtts);
        modelAndView.addObject("cloneOriginalAtts", cloneOriginalAtts);

		modelAndView.addObject("affair", affair);
		modelAndView.addObject("summary", summary);
		
//        //为预制协同模板-lxx
        if(templete!=null&&("11111122222233334444".equals(templete.getTempleteNumber())||"11111122222233335555".equals(templete.getTempleteNumber()))){
			String urltemp1 ="";
			String uuid = UuidUtil.uuid();
        	urltemp1 = body.getContent();
        	urltemp1 = urltemp1.replaceAll("id=id", "id="+uuid);
        	urltemp1 = urltemp1.replaceAll("userno=userno", "userno="+user.getLoginName());
        	body.setContent(urltemp1);
        	
        }
		
		modelAndView.addObject("body", body);
		modelAndView.addObject("senderOpinionContent", senderOpinionContent);//发起人附言内容

		if(process != null){
	        String caseProcessXML = process.toXML();
	        List<Party> workflowInfo = ColHelper.getWorkflowInfo(process);

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        modelAndView.addObject("hasWorkflow", true);
	        modelAndView.addObject("process_xml", caseProcessXML);
	        modelAndView.addObject("workflowInfo", workflowInfo);
	        modelAndView.addObject("isShowShortName", process.getIsShowShortName());
	        modelAndView.addObject("process_desc_by", FlowData.DESC_BY_XML);
		}

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);

        Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        Metadata colFlowPermPolicyMetadata = colMetadata.get(MetadataNameEnum.col_flow_perm_policy.name());//单独传递，免得它以后改名

        modelAndView.addObject("colFlowPermPolicyMetadata", colFlowPermPolicyMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("comMetadata", comMetadata);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);

        return modelAndView;
    }
    private String getTrackIds(Long affairId){
 	   String ids = "";
 	   if(affairId!=null){
 		   List<ColTrackMember> tracks = null;
 		   try{
 			   tracks= colManager.getColTrackMembers(Long.valueOf(affairId));
 		   }catch(Exception e){
 			   log.error(e);
 		   }
 			if(tracks!=null){
 				for(ColTrackMember colTrackMember:tracks){
 					if("".equals(ids))ids = String.valueOf(colTrackMember.getTrackMemberId());
 					else ids += ","+String.valueOf(colTrackMember.getTrackMemberId());
 				}
 			}
 	   }
 	   return ids;
    }
    /**
     * 成发集团项目 获得密级
     * */
    public String getSecretLevelName(ColSummary summary,Integer secret){
    	 String secretLevel = null;
    	 if(secret == Constant.SecretLevel.none.ordinal()){
         		secretLevel = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.none");
			}
			else if(secret == Constant.SecretLevel.noSecret.ordinal()){
				secretLevel = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.nosecret");
			}
			else if(secret == Constant.SecretLevel.secret.ordinal()){
				secretLevel = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.secret");
			}
			else if(secret == Constant.SecretLevel.secretMore.ordinal()){
				secretLevel = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.secretmore");
			}
			else if(secret == Constant.SecretLevel.TopSecret.ordinal()){
				secretLevel = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.topsecret");
			}
    	 return secretLevel;
    }
    /**
     * 自动发起的接口
     * 小心指针越界错误
     * @param request
     * @param response
     * @deprecated
     * @return
     * @throws Exception
     * map中存放的数据：标题，正文，String[] ids、name、userType， flowType
     */
    @SuppressWarnings("unchecked")
    public long autoForSend(Map parameterMap) throws Exception {
    	List<Long> userId=(List<Long>)parameterMap.get("userId");
    	Integer flowType=(Integer)parameterMap.get("flowType");
    	ColSummary colSummary = new ColSummary();

        String subject=(String) parameterMap.get("subject");
        String callbackCode=(String)parameterMap.get("callbackCode");
        colSummary.setAdvanceRemind(new Long(-1));
        colSummary.setCanArchive(true);
        colSummary.setCanDueReminder(true);
        colSummary.setCanEdit(true);
        colSummary.setCanForward(true);
        colSummary.setCanModify(true);
        colSummary.setCanTrack(true);
        colSummary.setDeadline(new Long(0));
        colSummary.setImportantLevel(1);
        colSummary.setRemindInterval(new Long(0));
        colSummary.setSubject(subject);
        if(callbackCode!=null&&callbackCode.length()>0)colSummary.setWebServiceCode(callbackCode);

        Map<String, Object> options = new HashMap<String, Object>();

        Constant.SendType sendType = Constant.SendType.normal;

        ColOpinion senderOninion = new ColOpinion();
        senderOninion.setContent("");
        senderOninion.setIdIfNew();
        senderOninion.affairIsTrack = true;

        ColBody body = new ColBody();
        String content=(String)parameterMap.get("content");
        body.setContent(content);
        body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
        Date bodyCreateDate = new Date();
        body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));

//        FlowData flowData = FlowData.getFlowdata(parameterMap);
        FlowData flowData = FlowData.flowdataFromMemberIds(flowType, userId, orgManager);
//        Map<String, String[]> map = new HashMap<String, String[]>();
//        flowData.setAddition(map);
        colSummary.setIdIfNew();

        try {
            colManager.runCase(flowData, colSummary, body, senderOninion, sendType, options, true, CurrentUser.get().getId());
        } catch (Exception e) {
            log.error("自动发起协同异常", e);
        }

        return colSummary.getId();
    }

    /**
     * 自动发起的接口
     * 小心指针越界错误
     * @param request
     * @param response
     * @deprecated
     * @return
     * @throws Exception
     */
    public ModelAndView autoSend(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        String[] userInfos=request.getParameterValues("userId");
        String flowType=request.getParameter("flowType");
        String advanceRemind="-1";
        String deadline="0";

        String[] ids=new String[userInfos.length];
        String[] userName=new String[userInfos.length];
        String[] userType=new String[userInfos.length];

        Map<String, Object> parameterMap=new HashMap<String, Object>();
        int step=0;
    	for(String userInfo:userInfos){
    		String[] str=StringUtils.split(userInfo, ";");
    		ids[step]=str[0];
    		userName[step]=str[1];
    		if (str[2].equals("1")) userType[step]=V3xOrgEntity.ORGENT_TYPE_MEMBER;
    		step=step+1;
    	}
    	parameterMap.put("userId", ids);
    	parameterMap.put("userName", userName);
    	parameterMap.put("userType", userType);
    	parameterMap.put("flowType", flowType);
    	parameterMap.put("advanceRemind", advanceRemind);
    	parameterMap.put("deadline", deadline);

    	ColSummary colSummary = new ColSummary();


//        bind(request, colSummary);
        String subject=request.getParameter("subject");
        colSummary.setAdvanceRemind(new Long(-1));
        colSummary.setCanArchive(true);
        colSummary.setCanDueReminder(true);
        colSummary.setCanEdit(true);
        colSummary.setCanForward(true);
        colSummary.setCanModify(true);
        colSummary.setCanTrack(true);
        colSummary.setDeadline(new Long(0));
        colSummary.setImportantLevel(1);
        colSummary.setRemindInterval(new Long(0));
        colSummary.setSubject(subject);

        Map<String, Object> options = new HashMap<String, Object>();

        Constant.SendType sendType = Constant.SendType.normal;

        //是否重复发起
//        if (null != request.getParameter("resend") && !"".equals(request.getParameter("resend"))) {
//            sendType = Constant.SendType.resend;
//        }

        String note = request.getParameter("note");//发起人附言
        ColOpinion senderOninion = new ColOpinion();
        senderOninion.setContent(note);
        senderOninion.setIdIfNew();
        senderOninion.affairIsTrack = true;

        ColBody body = new ColBody();
        bind(request, body);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
        }

        //从request对象取到选人信息
        FlowData flowData = FlowData.getFlowdata(parameterMap);

        String[] manualSelectNodeId = request.getParameterValues("manual_select_node_id");
        Map<String, String[]> map = new HashMap<String, String[]>();
        if(manualSelectNodeId != null){
            for(String node : manualSelectNodeId){
                String[] people = request.getParameterValues("manual_select_node_id" + node);

                map.put(node, people);
            }
        }

        flowData.setAddition(map);
        colSummary.setIdIfNew();

        //保存附件
        String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, colSummary.getId(), colSummary.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	colSummary.setHasAttachments(true);
        }

        try {
            colManager.runCase(flowData, colSummary, body, senderOninion, sendType, options, true, CurrentUser.get().getId());
        } catch (Exception e) {
            log.error("自动发起协同异常", e);
        }

        return super.redirectModelAndView("/collaboration.do?method=collaborationFrame&from=Sent");
    }


    /*
     * 各个应用转协同的接口
     * TODO:以后加上对word正文的支持
     */
    public  ModelAndView appToColl(String subject, String bodyType,  Date bodyCreateDate, String bodyContent, List<Attachment> atts,boolean attsNeedCopy){
    	ModelAndView modelAndView = new ModelAndView("collaboration/newCollaboration");
    	boolean cloneOriginalAtts = attsNeedCopy;
    	boolean canDeleteOriginalAtts=true;

    	ColSummary summary=new ColSummary();
		summary.setSubject(subject);
		summary.setCanForward(true);
        summary.setCanArchive(true);
        summary.setCanDueReminder(true);
        summary.setCanEditAttachment(true);
        summary.setCanModify(true);
        summary.setCanTrack(true);
        summary.setCanEdit(true);

        ColBody body = new ColBody();
        body.setContent(bodyContent);
        body.setBodyType(bodyType);
        body.setCreateDate(bodyCreateDate);

        Affair affair = new Affair();
        affair.setIsDelete(true);
        affair.setIsTrack(true);
        ColOpinion senderOpinion = null;

        modelAndView.addObject("attachments", atts);
        modelAndView.addObject("affair", affair);
        modelAndView.addObject("summary", summary);
        modelAndView.addObject("body", body);
        modelAndView.addObject("note", senderOpinion);

        modelAndView.addObject("canDeleteOriginalAtts", canDeleteOriginalAtts);
        modelAndView.addObject("cloneOriginalAtts", cloneOriginalAtts);

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        Metadata colFlowPermPolicyMetadata = colMetadata.get(MetadataNameEnum.col_flow_perm_policy.name());//单独传递，免得它以后改名

        modelAndView.addObject("colFlowPermPolicyMetadata", colFlowPermPolicyMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("comMetadata", comMetadata);
        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);

        List<ProjectSummary> projectList = null;
		try {
			projectList = projectManager.getProjectList();
		} catch (Exception e) {
			log.error("获取所有关联项目", e);
		}
		modelAndView.addObject("projectList", projectList);

    	return modelAndView;
    }
    /*
     * 简化的应用转协同接口，不考虑word正文情况
     */
    public  ModelAndView appToHtmlColl(String subject, String bodyContent, List<Attachment> atts,boolean iscopy){
    	String bodyType=Constants.EDITOR_TYPE_HTML;
    	Date date=new Date();
    	ModelAndView modelAndView =appToColl( subject, bodyType,  date,  bodyContent,  atts,iscopy) ;


    	return modelAndView;
    }
	public ModelAndView superviseDiagram(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("collaboration/showDiagramSupervise");
		String _summaryId = request.getParameter("summaryId");
		if(Strings.isNotBlank(_summaryId)){
			long summaryId = Long.parseLong(_summaryId);
	        ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(),summaryId);
	        StringBuffer sbf = new StringBuffer();
            Set<ColSupervisor> colSupervisors = detail.getColSupervisors();
            if(colSupervisors != null && colSupervisors.size()>0) {
                for(ColSupervisor colSupervisor:colSupervisors) {
                	sbf.append((orgManager.getMemberById(colSupervisor.getSupervisorId())).getName() + ",");
                }
            }
	        if(detail!=null){
	            mav.addObject("bean", detail);
	            mav.addObject("supervisor", sbf.substring(0, sbf.length()-1));
	        }

	        ColSummary summary = colManager.getColSummaryById(summaryId, false);
	        if(summary == null){
	        	summary = hisColManager.getColSummaryById(summaryId, false);
	        }
	        if(summary.isFinshed()){
	        	mav.addObject("finished", "readonly");
	        }else{
	        	mav.addObject("finished", "");
	        }
		}

		mav.addObject("summaryId", _summaryId);
		return mav;
	}
    public ModelAndView showDiagram(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("collaboration/showDiagram");
    	User user = CurrentUser.get();

        String _summaryId = request.getParameter("summaryId");
        String _affairId = request.getParameter("affairId");
        String from = request.getParameter("from");
        Long affairId = new Long(_affairId);
        long summaryId = Long.parseLong(_summaryId);

        ColSummary summary = colManager.getColSummaryById(summaryId, false);
        boolean isStoreFlag = false; //是否转储标记
    	if(summary == null){
    		summary = hisColManager.getColSummaryById(summaryId, false);
    		isStoreFlag = (summary != null);
    	}

        /*mav.addObject("finished", summary.isFinshed());
        ColSuperviseDetail cdetail = colSuperviseManager.getCurrentUserSupervise(Constant.superviseType.summary.ordinal(),summaryId,user.getId());
        if(cdetail!=null && summary.getCaseId() != null){
        	mav.addObject("isSupervis", true);
        	mav.addObject("bean", cdetail);
        	mav.addObject("openModal", request.getParameter("openModal"));
        }
        String supervisePanel = request.getParameter("supervise");
        if(Strings.isNotBlank(supervisePanel)){
        	mav.addObject("supervisePanel", supervisePanel);
        }*/

    	if(summary.getCaseId() == null){
    		log.warn("协同的CaseId为null: " + _summaryId + ", " + _affairId + ", " + user.getName());
    	}

        Affair affair = null;
        Long currentNodeOwner = user.getId();
        if(affairId != null){
        	if(isStoreFlag){
	        	affair = hisAffairManager.getById(affairId);
	        }
        	else{
        		affair = affairManager.getById(affairId);
        	}
        	//是否允许归档
        	boolean unallowedFlag = false;
        	if(!affair.getMemberId().equals(currentNodeOwner)){
        		unallowedFlag = true;
                currentNodeOwner = affair.getMemberId();
        	}
        	mav.addObject("unallowedFlag", unallowedFlag);
        	mav.addObject("affair", affair);
        }

        boolean hasDiagram = false;
        if(summary != null && summary.getCaseId()!= null)
        	hasDiagram = true;
        mav.addObject("hasDiagram", hasDiagram);
        mav.addObject("caseId", summary.getCaseId());
        mav.addObject("processId", summary.getProcessId());
        mav.addObject("trackIds", getTrackIds(affairId));
        String firstBodyType = summary.getFirstBody().getBodyType();
        mav.addObject("colBodyType", firstBodyType);
        if(!"HTML".equals(firstBodyType)){
        	mav.addObject("bodyFileId", summary.getFirstBody().getContent());
        }
        //当前流程是否是主流程
        boolean isMainFlow = false;

        if(!"WaiSend".equals(from)){ //待发不用判断权限
	        String nodePermissionPolicy = "collaboration";
	        BPMProcess process = null;
	        if(isStoreFlag){ //转储数据
	        	process = ColHelper.getHisCaseProcess(summary.getProcessId());
	        }
	        else{
	        	process = ColHelper.getCaseProcess(summary.getProcessId());
	        }

	        BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
	        if(affairId != null){
	        	//获取当前事项对应的节点
	        	if(activity != null){
	                String currentNodeId = activity.getId();
	                mav.addObject("currentNodeId", currentNodeId);
	                nodePermissionPolicy = activity.getSeeyonPolicy().getId();
	        	}
	        	//催办按钮只有在已发事项中才能显示
	            /*if(affair.getSubObjectId() == null
	            		&& "Sent".equals(from)
	            		&& user.getId() == affair.getSenderId()){
	                mav.addObject("showHastenButton", "true");
	        	}*/
	        }

	        //通过发起人单位Id去获取本单位的节点权限
	        Long senderId = affair.getSenderId();
	        V3xOrgMember sender = orgManager.getMemberById(senderId);
	        //权限策略改动，需要充分验证 Mazc 2009-4-13
            Long flowPermAccountId = ColHelper.getFlowPermAccountId(sender.getOrgAccountId(), summary, templeteManager);

	        ColOpinion draftOpinion = this.colManager.getDraftOpinion(affairId);
	        if(draftOpinion != null){
	        	List<Attachment> draftOpinionAtts = this.attachmentManager.getByReference(draftOpinion.getId(), draftOpinion.getId());
	        	mav.addObject("draftOpinion", draftOpinion);
	        	mav.addObject("draftOpinionAtts", draftOpinionAtts);
	        }

	        Permission permission = permissionManager.getPermission(MetadataNameEnum.col_flow_perm_policy.name(), nodePermissionPolicy, flowPermAccountId);
	        String permissionName = permission.getName();
	        mav.addObject("isAudit", "formaudit".equals(permissionName));
	        mav.addObject("isIssus", ("newsaudit".equals(permissionName) || "bulletionaudit".equals(permissionName)));
	        mav.addObject("isVouch", "vouch".equals(permissionName));
	        MetadataItem item = null;
            if(permission.getType()!=null && permission.getType()==0){
	        	item = this.metadataManager.getMetadataItem(MetadataNameEnum.col_flow_perm_policy, nodePermissionPolicy);
	        }

	        mav.addObject("nodePermissionPolicy", item);
	        mav.addObject("userDdefinedPolicy", permission.getName());
	        mav.addObject("opinionPolicy", permission==null ? "" : permission.getNodePolicy()!=null ? permission.getNodePolicy().getOpinionPolicy() : "");
	        mav.addObject("attitudes", permission==null ? "" : permission.getNodePolicy()!=null ? permission.getNodePolicy().getAttitude() : "");
	        Map<String, List<String>> actions = permissionManager.getActionMap(Constant.ConfigCategory.col_flow_perm_policy.name(), nodePermissionPolicy, flowPermAccountId);
	        List<String> baseActions = actions.get("basic");
	        List<String> advancedActions = actions.get("advanced");
	        List<String> commonActions = actions.get("common");

	        if(summary.getCanEdit()==null){//添加防护避免出现空指针
	        	summary.setCanEdit(false);
	        }
	        if(!summary.getCanEdit()){
	        	if(advancedActions != null){
	        		advancedActions.remove("Edit");
	        	}
	        	if(commonActions != null){
	        		commonActions.remove("Edit");
	        	}
	        }
	        if(!summary.getCanEditAttachment()){
	        	if(advancedActions != null){
	        		advancedActions.remove("allowUpdateAttachment");
	        	}
	        	if(commonActions != null){
	        		commonActions.remove("allowUpdateAttachment");
	        	}
	        }
	        if(summary.getCanForward()==null){//添加防护避免出现空指针
	        	summary.setCanForward(false);
	        }
	        if(!summary.getCanForward()){
	        	if(advancedActions != null){
	        		advancedActions.remove("Forward");
	        	}
	        	if(commonActions != null){
	        		commonActions.remove("Forward");
	        	}
	        }
	        if(summary.getCanTrack()==null){//添加防护避免出现空指针
	        	summary.setCanTrack(false);
	        }
	        if(!summary.getCanTrack() && baseActions != null){
	        	baseActions.remove("Track");
	        }
	        if(!summary.getCanArchive() && baseActions != null){
	        	baseActions.remove("Archive");
	        }
			if(!summary.getCanModify()){ //不允许改变流程
				if(advancedActions != null){
					advancedActions.remove("JointSign");
					advancedActions.remove("RemoveNode");
					advancedActions.remove("Infom");
					advancedActions.remove("AddNode");
				}
				if(commonActions != null){
					commonActions.remove("AddNode");
					commonActions.remove("JointSign");
					commonActions.remove("RemoveNode");
					commonActions.remove("Infom");
				}
			}

			if("FORM".equals(summary.getBodyType())) {
				if(baseActions != null)
					baseActions.remove("Edit");
				if(commonActions != null)
					commonActions.remove("Edit");
				if(advancedActions != null)
					advancedActions.remove("Edit");
				if("Pending".equals(from)) {
					String appId = null;
					String formId = null;
					String operationId = null;
					if(affair.getFormAppId()!=null){
						appId = affair.getFormAppId().toString();
						formId = affair.getFormId().toString();
						operationId = affair.getFormOperationId().toString();
					}else if(activity != null && activity.getSeeyonPolicy()!=null){
						appId = activity.getSeeyonPolicy().getForm();
						formId = activity.getSeeyonPolicy().getForm();
						operationId = activity.getSeeyonPolicy().getOperationName();
					}
					boolean hasEdit = FormHelper.hasEditType(appId, formId, operationId);
					if(hasEdit){
						LockObject lockObject = FormLockManager.add(summaryId,affairId,user.getId(), user.getLoginName(),user.getLoginTimestamp()==null?0l:user.getLoginTimestamp().getTime());
						if(lockObject!=null && !user.getLoginName().equals(lockObject.getLoginName())) {
							if(baseActions != null)
								baseActions.remove("Comment");
							if(advancedActions != null){
								advancedActions.remove("Return");
								advancedActions.remove("Infom");
								advancedActions.remove("RemoveNode");
								advancedActions.remove("JointSign");
							}
							if(commonActions != null){
								commonActions.remove("AddNode");
							}
							mav.addObject("removeContinue", true);
						}else if(lockObject!=null && user.getLoginName().equals(lockObject.getLoginName())){
							mav.addObject("removeFormLock", true);
						}
					}
				}
				Integer newflowType = summary.getNewflowType();
				if(newflowType != null && newflowType.intValue() == Constant.NewflowType.main.ordinal()){//0
					mav.addObject("newflowType", Constant.NewflowType.main);
					mav.addObject("isNewflow", false);
				}else if(newflowType != null && newflowType.intValue() == Constant.NewflowType.child.ordinal()){//1
					mav.addObject("isNewflow", true);
				}else{//-1
					mav.addObject("isNewflow", false);
				}
				/*//NF 查找当前表单协同是否关联有新流程
				//当前流程为主流程，当前节点为触发节点，返回可查看的子流程的SummaryIds
                Integer newflowType = summary.getNewflowType();
				if(newflowType != null && newflowType.intValue() == Constant.NewflowType.main.ordinal()){
				    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.main.ordinal());
				    if(runningList != null && !runningList.isEmpty()){
				        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
				        for (NewflowRunning running : runningList) {
				            //1、当前协同为主流程（running为子流程），且子流程running可被主流程查看
				            if(running.getMainSummaryId().equals(summaryId) && running.getIsCanViewByMainFlow()){
				                relateFlowList.add(running);
				                continue;
				            }
				        }
				        mav.addObject("relateFlowList", relateFlowList);
				        mav.addObject("newflowTempleteId", summary.getTempleteId());
                        isMainFlow = true;
				    }
				    mav.addObject("newflowType", Constant.NewflowType.main);


				     * 同时判断当前节点的前一节点是否是新流程触发节点，
				     * 如果是且{其触发的新流程有设置了‘新流程结束后主流程才可继续’选项，但未结束的}，提示子流程未结束，不能处理。

                    if("Pending".equals(from)){
                        try{
                            List<String> hasNewflowNodeIds = ColHelper.checkPrevNodeHasNewflow(activity);
                            if(hasNewflowNodeIds != null && !hasNewflowNodeIds.isEmpty()){
                                String noFinishNewflowTitle = newflowManager.checkHasNoFinishNewflow(summaryId, hasNewflowNodeIds);
                                if(Strings.isNotBlank(noFinishNewflowTitle)){
                                    mav.addObject("noFinishNewflow", noFinishNewflowTitle);
                                }
                            }
                        }
                        catch(Exception e){
                            log.error("判断前一节点是否是触发了新流程的节点时异常：", e);
                        }
                    }
				}
				else if(newflowType != null && newflowType.intValue() == Constant.NewflowType.child.ordinal()){
				    //当前流程为子流程，返回可查看的主流程的SummaryId
				    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.child.ordinal());
				    if(runningList != null && !runningList.isEmpty()){
				        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
				        NewflowRunning running = runningList.get(0);
				        //2、当前协同为子流程（running为主流程），且running可查看子流程
				        if(running.getSummaryId().equals(summaryId) && running.getIsCanViewMainFlow()){
				            relateFlowList.add(running);
				        }
				        mav.addObject("relateFlowList", relateFlowList);
				        mav.addObject("newflowTempleteId", summary.getTempleteId());
				    }
				    mav.addObject("isNewflow", true);
				    if("Pending".equals(from))
				    	mav.addObject("newflowCanNotBack",ColHelper.isSecondNode(summary.getCaseId(), affair.getSubObjectId(), activity));
				    mav.addObject("newflowType", Constant.NewflowType.child);
				}*/
            }
	        mav.addObject("baseActions", baseActions);
	        mav.addObject("advancedActions", advancedActions);
	        mav.addObject("commonActions", commonActions);
	        mav.addObject("parentformSummaryId", summary.getParentformSummaryId());
        }

        //预归档目录目录名
        /*Long archiveId = null;
        String archiveName = "";
        if(summary.getArchiveId() != null){
        	archiveId = summary.getArchiveId();
        	archiveName = docHierarchyManager.getNameById(archiveId);
        }*/

        //关联项目名称
        /*Long projectId = null;
        String projectName = "";
        if(summary.getProjectId() != null){
        	projectId = summary.getProjectId();
        	ProjectSummary project = projectManager.getProject(projectId);
        	projectName = project.getProjectName();
        }*/

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        //Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        //Metadata importanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
        //Metadata deadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);

        //是否超期
        /*String isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.false");
        if(!"WaiSend".equals(from)){
	        java.sql.Timestamp startDate1 = summary.getStartDate();
			java.sql.Timestamp finishDate = summary.getFinishDate();
			Date now = new Date(System.currentTimeMillis());
			if(summary.getDeadline() != null && summary.getDeadline() != 0 && startDate1 != null){
				Long deadLine = summary.getDeadline()*60000;
				if(finishDate == null){
					if((now.getTime()-startDate1.getTime()) > deadLine){
						isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.true");
					}
				}else{
					Long expendTime = finishDate.getTime() - startDate1.getTime();
					if((deadLine-expendTime) < 0){
						isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.true");
					}
				}
			}
        }
        mav.addObject("isOvertopTime", isOvertopTime);*/

        //流程状态
        /*String state = "";
    	switch(StateEnum.valueOf(affair.getState())){
        	case col_waitSend : state = Constant.getString4CurrentUser("col.state.11.waitSend");
        	break;
        	case col_sent : state = Constant.getString4CurrentUser("col.state.12.col_sent");
        	break;
        	case col_pending : state = Constant.getString4CurrentUser("col.state.13.col_pending");
        	break;
        	case col_done : state = Constant.getString4CurrentUser("col.state.14.done");
        	break;
    	}
    	mav.addObject("flowState", state);*/

        //发起时间
        /*java.sql.Timestamp startDate = summary.getStartDate();
        mav.addObject("startDate", startDate);*/

        //mav.addObject("projectName", projectName);
        //mav.addObject("comMetadata", comMetadata);
        mav.addObject("colMetadata", colMetadata);
        //mav.addObject("archiveName", archiveName);
        mav.addObject("summary", summary);
        mav.addObject("attsFlag", summary.isHasAttachments());
        mav.addObject("bodytype", summary.getBodyType());
        mav.addObject("from", from);
        //mav.addObject("isShowButton", false);
        /*mav.addObject("importanceMetadata", importanceMetadata);
        mav.addObject("deadlineMetadata", deadlineMetadata);*/

        //业务日志
        //FIXME 协同属性，需要更换为流程监控，待删除 --删除dongyj
        /*List<OperationLog> bizLogs = operationlogManager.queryByObjectId(summaryId, false);
        mav.addObject("bizLogs", bizLogs);*/

        boolean templateFlag = false;
        if(summary.getTempleteId()!=null) {
        	 Long templeteid = summary.getTempleteId();
	       	 Templete templete = templeteManager.get(summary.getTempleteId());
	       	 if(templete != null) {
	       		 if("FORM".equals(templete.getBodyType()) && templete.getFormParentId() !=null){
	       			 templeteid = templete.getFormParentId();
	       		 }
	       		 List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(templeteid,ApplicationCategoryEnum.collaboration.ordinal());
	       		 //mav.addObject("branchs", branchs);
	       		 if(branchs != null || isMainFlow) {
	       			 /*if(branchs != null) {
	       				//显示分支条件使用流程中保留的，如果为空使用模板中的
	             		ColHelper.updateBranchByProcess(summary.getProcessId(),branchs);
	       			 }*/
	       			 //TODO 组分支判断有问题，后续修改
	       			 List<V3xOrgEntity> entities = this.orgManager.getUserDomain(currentNodeOwner, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
	       			 if(entities != null && !entities.isEmpty()){
	       				 V3xOrgTeam team = null;
	       				 List<V3xOrgTeam> teams = new ArrayList<V3xOrgTeam>();
	       				 for(V3xOrgEntity entity:entities){
	       					 team = (V3xOrgTeam) entity;
	       					 if(team != null && team.getAllMembers() != null && team.getAllMembers().contains(currentNodeOwner)){
	       						 teams.add(team);
	       					 }
	       				 }
	       				mav.addObject("teams", teams);
	       			 }

	       			 V3xOrgMember mem = orgManager.getMemberById(currentNodeOwner);
	       			 List<MemberPost> secondPosts = mem.getSecond_post();
	       			 mav.addObject("secondPosts", secondPosts);
	       			 mem = orgManager.getMemberById(summary.getStartMemberId());
	       			 mav.addObject("startTeams", this.orgManager.getUserDomain(mem.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM));
	       			 mav.addObject("startSecondPosts", mem.getSecond_post());
	       		 }

	       		 templateFlag = true;
	       	 }
        }
        mav.addObject("templateFlag", templateFlag);
        try{
        	V3xOrgMember member = this.orgManager.getMemberById(user.getId());
        	if(member != null){
        		mav.addObject("extendConfig",Strings.escapeNULL(member.getProperty("extendConfig"), true));
        	}
        }catch(Exception e){
        	log.error(e);
        }


        return mav;
    }

    /**
     * 发协同及处理提交前的检查
     * 当发送协同及处理提交前是通过模板的，必须先走这一步，设置可能的选人和新流程信息（ProcessModeSelector)
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @deprecated
     */
    public ModelAndView preSend(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');</script>");
    		return null;
    	}
    	ModelAndView mav = new ModelAndView("collaboration/processModeSelectorMain");
        FlowData flowData = FlowData.flowdataFromRequest();

        String currentNodeId = request.getParameter("currentNodeId");
        String splitProcessId = request.getParameter("processId");
        String _affairId = request.getParameter("affair_id");
        boolean templateFlag = "true".equals(request.getParameter("isFromTemplate"));
        String formData = request.getParameter("formData");
        //是否是督办时的匹配节点
        String fromColsupervise = request.getParameter("fromColsupervise");
        String appName = request.getParameter("appName");
        boolean isEdoc = String.valueOf(ApplicationCategoryEnum.edoc.getKey()).equals(appName);

        mav.addObject("fromColsupervise", fromColsupervise);
        ColSummary summary = null;
        Affair affair = null;
        if(_affairId != null && !"".equals(_affairId)){
	        affair = affairManager.getById(Long.parseLong(_affairId));
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	return mav;
	        }
        }
        if(affair != null && !isEdoc){
        	summary = colManager.getColSummaryById(affair.getObjectId(), false);
        }

        String processId = null;
        BPMProcess process = null;
        if(!flowData.isEmpty()){
	        if(flowData.getXml() == null || "".equals(flowData.getXml())){
		        //根据选人界面传来的people生成流程定义XML
		        processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
		        //生成流程定义对象
		        process = ColHelper.getProcess(processId);
		        ColHelper.deleteReadyProcess(processId);
		        String caseProcessXML = process.toXML();
		        if(StringUtils.isNotBlank(caseProcessXML)){
		        	caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
					flowData.setXml(caseProcessXML);
		        }
		        currentNodeId = process.getStart().getId();
		        mav.addObject("caseProcessXML", StringEscapeUtils.escapeHtml(caseProcessXML));
		        mav.addObject("process_desc_by", FlowData.DESC_BY_XML);
	        }
	        else{
	        	process = flowData.toBPMProcess();
	        }
        }
        else{
        	processId = request.getParameter("processId");
        	process = ColHelper.getCaseProcess(processId);
        }

        //NF PreSend-新流程匹配处理,如果当前节点设置了新流程
    	if(Strings.isNotBlank(currentNodeId) && !"start".equals(currentNodeId)){
            BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
    		boolean hasNewflow = templateFlag && seeyonPolicy != null && "1".equals(seeyonPolicy.getNF());

    		if(hasNewflow){
    		    User user = CurrentUser.get();
                Long currentNodeOwner = user.getId();
                if(affair != null){
                   // summary = colManager.getColSummaryById(affair.getObjectId(), false);
                    if(!currentNodeOwner.equals(affair.getMemberId())){
                        currentNodeOwner = affair.getMemberId();
                    }
                }
		        if(summary != null){
		            //当前节点有新流程时，弹出选择要触发的新流程信息
		            List<NewflowModel> newflowModels = newflowManager.getNewflowModelList(summary.getId(), summary.getTempleteId(), currentNodeId);
		            if(newflowModels != null && !newflowModels.isEmpty()){
		                Map<Long, String> conditionMap = new HashMap<Long, String>();
		                for (NewflowModel model : newflowModels) {
		                    //设置发起者
		                    String newflowSender = model.getNewflowSender();
		                    List<PersonInfo> pl = new ArrayList<PersonInfo>();
		                    if("CurrentNode".equals(newflowSender)){//当前节点
		                        PersonInfo p = new PersonInfo(user.getId(), user.getName());
		                        pl.add(p);
		                    }
		                    else if("CurrentSender".equals(newflowSender)){ //当前流程发起者
		                        Long pid = summary.getStartMemberId();
		                        V3xOrgMember m = orgManager.getMemberById(pid);
                                if(m != null && m.isValid()){
                                    PersonInfo p = new PersonInfo(pid, m.getName());
                                    pl.add(p);
                                }
		                    }
		                    else{ //所选择人员
		                        Set<V3xOrgMember> ms = orgManager.getMembersByTypeAndIds(newflowSender);
		                        if(ms != null && !ms.isEmpty()){
		                            for(V3xOrgMember m : ms){
		                                PersonInfo p = new PersonInfo();
		                                p.setId(m.getId() + "");
		                                p.setName(m.getName());
		                                pl.add(p);
		                            }
		                        }
		                    }
		                    model.setPeople(pl);
		                    String triggerCondition = model.getTriggerCondition();
		                    if(Strings.isNotBlank(triggerCondition)){
		                    	conditionMap.put(model.getId(), triggerCondition);
		                    }
		                }
                        //得到所有页面传递过来的参数，用于触发条件判断
                        /*
                        Map<String, String> paramAndValueMap = new HashMap<String, String>();
                        Enumeration e = (Enumeration)request.getParameterNames();
                        while(e.hasMoreElements()){
                            String parName = (String)e.nextElement();
                            paramAndValueMap.put(parName, request.getParameter(parName));
                        }*/
		                boolean isFromForm = Strings.isNotBlank(formData) ;
		        		Long formAppId= -1l;
		        		Long masterId= -1l;
		        		if(isFromForm){
		        			String formApp = seeyonPolicy.getFormApp() ;
		        			formAppId= Long.parseLong(formApp);
		        			ColBody body = null ;
			        		if(summary != null){
			        			body = summary.getFirstBody();
			        		}
			        		if(body != null){
			        			String mastridStr = body.getContent() ;
			        			masterId= Long.parseLong(mastridStr);
			        		}
		        		}
                        Map<Long, String> conditionResultMap = ColHelper.calculateCondition(conditionMap, null, summary.getStartMemberId(),
                        		currentNodeOwner, orgManager, false,formAppId,masterId,formData,summary.getOrgAccountId());
		                mav.addObject("newflowModels", newflowModels);
		                mav.addObject("conditionResultMap", conditionResultMap);
		            }
		        }
    		}
    	}

    	//下一点匹配
        if(Strings.isNotBlank(currentNodeId)){
            if(!"start".equals(currentNodeId)){ //处理节点才需要做校验是否需要进行节点匹配，发起节点不管什么时候都要匹配
                BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();

                boolean isMatch = !"false".equals(request.getParameter("isMatch")); //需要做下一节点匹配，只有会签后才不需要匹配（null为发起时，true是处理，false经过了会签）
                if(isMatch && ColHelper.isExecuteFinished(process, affair) && !"inform".equals(seeyonPolicy.getId()) && !"zhihui".equals(seeyonPolicy.getId())){
                    //需要匹配
                }
                else{
                    return mav;
                }
            }

			long caseId = 0; //发起时，case不存在
			String _caseId = request.getParameter("caseId");
			if(Strings.isNotBlank(_caseId)){
				caseId = Long.parseLong(_caseId);
			}

			if(!"start".equals(currentNodeId) && caseId == 0){ //发起时，case不存在
				log.warn("=======================================\\n处理"+(isEdoc?"公文":"协同")+"时，caseId不存在。Parameter：" + request.getParameterMap() + "\\n=======================================");
			}
			boolean isFromForm = Strings.isNotBlank(formData) ;
			BPMSeeyonPolicy seeyonPolicy = null ;
            if(!"start".equals(currentNodeId) && isFromForm){
            	seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
            }
    		String formApp = "" ;
    		if(isFromForm){
    			if("start".equals(currentNodeId)){
    				String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
    				formApp = formInfo[0] ;
    			}else{
    				formApp = seeyonPolicy.getFormApp() ;
    			}
    		}
    		Map<String,String[]> fieldMap = new HashMap<String,String[]>() ;

    		if(Strings.isNotBlank(formData)){
    			fieldMap = FormHelper.getFieldValueMap(formApp,formData) ;
    		}

    		Map<String,String[]> fieldDataBaseMap = new HashMap<String,String[]>() ;
    		String mastrid = null ;
    		if(!"start".equals(currentNodeId) && isFromForm){
        		ColBody body = null ;
        		if(summary != null){
        			body = summary.getFirstBody();
        		}
        		if(body != null){
        			mastrid = body.getContent() ;
        		}
        		fieldDataBaseMap  =	FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
    		}
			Map<String,String[]> contextMap = new HashMap<String,String[]>() ;
			if(fieldDataBaseMap != null && isFromForm){
				contextMap.putAll(fieldDataBaseMap) ;
			}
			if(fieldMap != null && isFromForm){
				contextMap.putAll(fieldMap) ;
			}

			WorkflowEventListener.ProcessModeSelector selector = ColHelper.preRunCase(process, currentNodeId, templateFlag, caseId,contextMap);
	        List<NodeAddition> invalidateActivity = selector.getInvalidateActivity();
	        if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
	        	mav.addObject("invalidateActivity", invalidateActivity);
	        	return mav;
	        }

	        //key 节点id



	        Map<String, ProcessModeSelectorModel> selectorModels = new LinkedHashMap<String, ProcessModeSelectorModel>();
	        List<String> selectorModelNodeIds = new ArrayList<String>();
            if(selector != null){
                List<NodeAddition> nodeAdditions = selector.getNodeAdditions();

                if(nodeAdditions != null){
                    for(int i=nodeAdditions.size()-1;i>=0;i--){
                    	NodeAddition addition=nodeAdditions.get(i);
                    	if("FormField".equals(addition.getPartyType())){
                    		String id = "" ;
                    		String display = "" ;
                    		if(fieldMap != null && fieldMap.get(addition.getPartyId()) != null){
                    			String str[] = fieldMap.get(addition.getPartyId()) ;
                    			id = str[1] ;
                    			display = str[0] ;
                    			V3xOrgMember member = null ;
                    			if(Strings.isNotBlank(id)){
                    				member = orgManager.getMemberById(Long.valueOf(id)) ;
                    			}
                    			if(member != null)
                    				display =  member.getName() ;
                    		}

                    		if(!"start".equals(currentNodeId) && fieldDataBaseMap != null && Strings.isBlank(id) && Strings.isBlank(display)){
                        		String str[] =	fieldDataBaseMap.get(addition.getPartyId()) ;
                        		if(str != null){
                        			id = str[1] ;
                        			display = str[0] ;
                        		}
                    		}

                    		if(Strings.isNotBlank(id) && Strings.isNotBlank(display)){
                        		//selector.mode = 1 ;
                        		PersonInfo personInfo = new PersonInfo() ;
                        		personInfo.setId(id) ;
                        		personInfo.setName(display) ;
                        		List<PersonInfo> list = new ArrayList<PersonInfo>();
                        		list.add(personInfo) ;
                        		addition.setReadOnly(true) ;
                        		addition.setPeople(list) ;
                    		}
                    	}
                    	ProcessModeSelectorModel selectorModel = new ProcessModeSelectorModel();
                    	selectorModel.setAddition(addition);
                    	selectorModel.setNodeId(addition.getNodeId());
                    	selectorModel.setNodeName(addition.getNodeName());
                    	selectorModel.setFromIsInform(addition.isFromIsInform());
                    	selectorModels.put(addition.getNodeId(), selectorModel);
                    	selectorModelNodeIds.add(addition.getNodeId());
                    }
                }
            }

            //分支处理开始
            BPMAbstractNode currentNode = null;
            Long startMemberId = null;
            if("start".equals(currentNodeId)){
            	currentNode = process.getStart();
            	startMemberId = CurrentUser.get().getId();
            }
            else{
            	currentNode = process.getActivityById(currentNodeId);
            	startMemberId = affair.getSenderId();
            }
            Long formAppId= -1l;
            Long masterId= -1l;
            if(!"".equals(formApp.trim())){
            	formAppId= Long.parseLong(formApp);
            }
            if(null!=mastrid){
            	masterId= Long.parseLong(mastrid);
            }
            Map<String,Object> hash = ColHelper.getCondition(currentNode, null, startMemberId, CurrentUser.get().getId(), orgManager,false,formAppId,masterId,formData);
            if(hash != null){
            	List<String> keys = (List<String>)hash.get("keys");
            	List<String> nodeNames = (List<String>)hash.get("names");
            	List<String> conditions = (List<String>)hash.get("conditions");
            	List<String> forces = (List<String>)hash.get("forces");
            	List<String> links = (List<String>)hash.get("links");
            	List<Integer> conditionTypes = (List<Integer>)hash.get("conditionTypes");
            	mav.addObject("allNodes", hash.get("allNodes"));
            	mav.addObject("nodeCount", hash.get("nodeCount"));

            	if(keys.size() > 0 && conditions.size() > 0){

            		for (int k =  keys.size()-1; k >=0; k--) {
            			String nodeId = keys.get(k);
            			ProcessModeSelectorModel selectorModel = selectorModels.get(nodeId);
            			if(selectorModel == null){
            				selectorModel = new ProcessModeSelectorModel();

            				selectorModels.put(nodeId, selectorModel);
            				selectorModelNodeIds.add(nodeId);

            				selectorModel.setProcessMode(process.getActivityById(nodeId).getSeeyonPolicy().getProcessMode());
            			}

            			selectorModel.setNodeId(nodeId);
            			selectorModel.setNodeName(nodeNames.get(k));
            			selectorModel.setCondition(conditions.get(k));
            			selectorModel.setForce(forces.get(k));
            			selectorModel.setLink(links.get(k));
            			selectorModel.setConditionType(conditionTypes.get(k));
            		}

            		User user = CurrentUser.get();

            		if("waitSend".equals(request.getParameter("from")) && !isEdoc) {
            			String _summaryId = request.getParameter("summaryId");
            			ColSummary summary0 = colManager.getColSummaryById(Long.parseLong(_summaryId), true);

            			if("FORM".equals(summary0.getBodyType())){
            				ColBody body = summary0.getFirstBody();
            				String[] formPolicy = FormHelper.getFormPolicy(process);
            				String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formPolicy[0], formPolicy[1], formPolicy[2], body.getContent(), _summaryId, "0", formPolicy[3],false);
            				mav.addObject("formContent", formContent);
            			}
            		}
            	}
            }
	        mav.addObject("selectorModels", selectorModels);
	        mav.addObject("selectorModelNodeIds", selectorModels.keySet());
        }

        return mav;
    }

    /**
     * 判断是否需要弹出页面
     * 当发送协同及处理提交前是通过模板的，必须先走这一步，设置可能的选人和新流程信息（ProcessModeSelector)
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView prePopNew(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	//该变量用来标志是否当前处理人对应的事项为待办状态
    	boolean popFlag0= true;
    	//该变量用来标志是否有需要匹配的后续节点或条件分支
    	boolean popFlag1= false;
    	//该变量用来标志是否当前处理节点会触发新流程
    	boolean hasNewflow = false;
    	//该变量用来标志是否所有流程节点都只有一个执行人(为了简化是否弹出判断逻辑，该参数目前不使用)
    	boolean isAllReadyOnly= true;
    	JSONObject mainMap= new JSONObject();
    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');</script>");
    		return null;
    	}
    	//从页面获得工作流流程模板定义数据
        FlowData flowData = FlowData.flowdataFromRequest();
        //获得当前流程处理节点Id
        String currentNodeId = request.getParameter("currentNodeId");
        //从页面获得流程模板Id
        String splitProcessId = request.getParameter("processId");
        //从页面获得当前协同事项Id，如果为发起节点，则该参数为空
        String _affairId = request.getParameter("affair_id");
        //从页面获得当前系统是否来自模板
        boolean templateFlag = "true".equals(request.getParameter("isFromTemplate"));
        mainMap.put("templateFlag", String.valueOf(templateFlag));
        //从页面获得当前表单数据
        String formData = request.getParameter("formData");
        //是否是督办时的匹配节点
        String fromColsupervise = request.getParameter("fromColsupervise");
        //获得协同应用类型
        String appName = request.getParameter("appName");
        //计算出协同是否来自公文
        boolean isEdoc = String.valueOf(ApplicationCategoryEnum.edoc.getKey()).equals(appName);
        if(fromColsupervise==null || "".equals(fromColsupervise.trim())){
        	fromColsupervise="";
        }
        mainMap.put("currentNodeId", currentNodeId);
        mainMap.put("splitProcessId", splitProcessId);
        mainMap.put("affairId", _affairId);
        mainMap.put("appName", appName);
        mainMap.put("isEdoc", String.valueOf(isEdoc));
        mainMap.put("fromColsupervise", fromColsupervise);
        ColSummary summary = null;
        Affair affair = null;
        if(_affairId != null && !"".equals(_affairId)){
        	//获得协同事项
	        affair = affairManager.getById(Long.parseLong(_affairId));
	        //判断协同状态是否为待办状态，如果不是待办状态，则不处理
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	//表示不需要弹出
	        	popFlag0= false;
	        }
        }
        mainMap.put("popFlag0", String.valueOf(popFlag0));
        Map followUpMap= new HashMap();
        if(popFlag0){
        	//如果协同事项不为空，且不是公文协同应用，则获得协同对象
        	if(affair != null && !isEdoc){
            	summary = colManager.getColSummaryById(affair.getObjectId(), false);
            }
        	String processId = null;
            BPMProcess process = null;
            //获得流程模板定义信息
            if(!flowData.isEmpty()){//如果流程定义数据不为空
    	        if(flowData.getXml() == null || "".equals(flowData.getXml())){
    		        //根据选人界面传来的people生成流程定义XML
//    		        processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
    		        //从内存中流程定义对象
//    		        process = ColHelper.getProcess(processId);
    		        //从内存中删除该流程定义对象
//    		        ColHelper.deleteReadyProcess(processId);
    	        	process= ColHelper.getBPMProcessByPeople(flowData, processId, false);
    		        processId= process.getId();
    		        String caseProcessXML = process.toXML();
    		        if(StringUtils.isNotBlank(caseProcessXML)){
    		        	caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
    					flowData.setXml(caseProcessXML);
    		        }
    		        currentNodeId = process.getStart().getId();
    		        //mainMap.put("caseProcessXML", StringEscapeUtils.escapeHtml(caseProcessXML));
    		        //mainMap.put("process_desc_by", FlowData.DESC_BY_XML);
    	        }else{
    	        	process = flowData.toBPMProcess();
    	        }
            }else{//否则从流程实例中获得流程模板定义信息
            	processId = request.getParameter("processId");
            	process = ColHelper.getCaseProcess(processId);
            }
            
            // 2017-3-13 诚佰公司 只处理发起者自建协同流程
        	if (!templateFlag && !isEdoc && currentNodeId.equals("start")) { 
	        	String secretLevel = request.getParameter("secretLevel");
	        	String outMsg = validateFlow(secretLevel, process.toXML());
	        	if (outMsg != null && !outMsg.isEmpty()) {
					mainMap = new JSONObject();
					mainMap.put("secretAlert", outMsg);
			    	
			    	PrintWriter pw = response.getWriter();
			        pw.write(mainMap.toString());
			        pw.flush();
			        return null;
	        	}
        	}
        	// 2017-3-13 诚佰公司
        	
            mainMap.put("processId", process.getId());
            //新流程匹配处理,判断当前节点是否设置了新流程
        	if(Strings.isNotBlank(currentNodeId) && !"start".equals(currentNodeId)){
                BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
        		hasNewflow = templateFlag && seeyonPolicy != null && "1".equals(seeyonPolicy.getNF());
        		//计算出是否有新流程的标志
        		mainMap.put("hasNewflow", String.valueOf(hasNewflow));
        		mainMap.put("seeyonPolicyNF", seeyonPolicy.getNF());
        	}
        	boolean isNeddMatch= false;
        	//对当前流程处理节点的后续节点和分支条件进行处理
            if(Strings.isNotBlank(currentNodeId)){
            	//获得当前处理节点的seeyonPolicy策略，只有非开始节点才会用到该策略信息
            	BPMSeeyonPolicy seeyonPolicy = null;
                if(!"start".equals(currentNodeId)){
                	//处理节点才需要做校验是否需要进行节点匹配，发起节点不管什么时候都要匹配
                	BPMHumenActivity humenactivity= (BPMHumenActivity)process.getActivityById(currentNodeId);
                    seeyonPolicy = humenactivity.getSeeyonPolicy();
                    String processMode = seeyonPolicy.getProcessMode();
                    //需要做下一节点匹配，只有会签后才不需要匹配（null为发起时，true是处理，false经过了会签）
                    boolean isMatch = !"false".equals(request.getParameter("isMatch"));
                    //判断当前处理人员是否为当前流程节点的最后一个处理人
                    boolean isExecuteFinished= ColHelper.isExecuteFinished(process, affair);
                    mainMap.put("isMatch", String.valueOf(isMatch));
                    mainMap.put("isExecuteFinished", String.valueOf(isExecuteFinished));
                    mainMap.put("seeyonPolicyId", seeyonPolicy.getId());
                    mainMap.put("processMode", processMode);
                    isNeddMatch= isMatch && isExecuteFinished && !"inform".equals(seeyonPolicy.getId()) && !"zhihui".equals(seeyonPolicy.getId());
                }
                //如果能够弹出页面，则该参数用来简化弹出处理逻辑
            	mainMap.put("isNeddMatch", String.valueOf(isNeddMatch));
                if(isNeddMatch || "start".equals(currentNodeId)){
                	long caseId = 0; //发起时，case不存在
        			String _caseId = request.getParameter("caseId");
        			if(Strings.isNotBlank(_caseId)){
        				caseId = Long.parseLong(_caseId);
        			}else if(null!=summary){
        				caseId = summary.getCaseId();
        			}
        			mainMap.put("caseId", String.valueOf(caseId));
        			if(!"start".equals(currentNodeId) && caseId == 0){ //发起时，case不存在
        				log.warn("=======================================\\n处理"+
        						(isEdoc?"公文":"协同")+"时，caseId不存在。Parameter：" +
        						request.getParameterMap() +
        						"\\n=======================================");
        			}
        			boolean isFromForm = Strings.isNotBlank(formData) ;
        			//定义表单协同的表单域对象
            		Map<String,String[]> fieldMap = new HashMap<String,String[]>() ;
            		//定义表单协同的表单域及数据对象
            		Map<String,String[]> fieldDataBaseMap = new HashMap<String,String[]>() ;
            		//定义表单协同的上下文对象
            		Map<String,String[]> contextMap = new HashMap<String,String[]>() ;
            		if(isFromForm){//如果是来自表单协同
            			String formApp = "" ;
            			if("start".equals(currentNodeId)){//当前处理者是开始节点
            				//从流程数据flowData中获得表单信息
            				String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
            				//获得表单应用formApp
            				formApp = formInfo[0] ;
            			}else{//且当前处理者不是开始节点
            				//从流程模板定义中获得表单应用formApp
            				formApp = seeyonPolicy.getFormApp() ;
            				//表单数据对应的主表记录主键值
            				String mastrid = null ;
            				ColBody body = null ;
            				if(summary != null){
            					//从协同中获得协同主体信息
                    			body = summary.getFirstBody();
                    		}
            				if(body != null){
            					//获得表单数据对应的主表记录主键值
                    			mastrid = body.getContent() ;
                    		}
            				fieldDataBaseMap  =	FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
            			}
            			//获得表单输入域的值
            			fieldMap = FormHelper.getFieldValueMap(formApp,formData) ;
            			if(fieldDataBaseMap != null){
            				contextMap.putAll(fieldDataBaseMap) ;
            			}
            			if(fieldMap != null){
            				contextMap.putAll(fieldMap) ;
            			}
            		}
//            		followUpMap= ColHelper.preNext(process, currentNodeId, templateFlag, caseId,contextMap);
            		BPMAbstractNode currentActivity = null;
            		long workItemId= -1l;
                    if(!"start".equals(currentNodeId)){//发起时，case不存在
                    	currentActivity = (BPMAbstractNode)process.getActivityById(currentNodeId);
                    	if(affair.getSubObjectId() != null){
                			workItemId = affair.getSubObjectId();
                		}
                    }else{
                    	currentActivity = process.getStart();
                    }
            		BranchArgs.hasSelectorOrCondition(process, currentActivity, caseId, templateFlag, contextMap, orgManager, followUpMap,workItemId);
            		if(log.isInfoEnabled()){
            			if(null!=summary){
            				log.info("subject:="+summary.getSubject());
            			}
            			log.info("followUpMap:="+followUpMap);
            		}
            		String isNextPop= (String)followUpMap.get("isPop");
                    if("true".equals(isNextPop)){
                    	popFlag1= true;
                    }
                    mainMap.put("isAllReadyOnly", followUpMap.get("isAllReadOnly"));
                }
            }
        }
        mainMap.put("isAllReadyOnly", String.valueOf(isAllReadyOnly));
        if(isAllReadyOnly==false){
        	popFlag1= true;
        }
        String invalidateActivityStr= "";
        if(popFlag1 || hasNewflow){
        	mainMap.put("isPop", "true");
        }else{
    		mainMap.put("isPop", "false");
    		if(followUpMap!= null){
        		//对不需要弹出页面，但存在不用人员时，在这里进行处理，以便在alert时提示用户
        		Map invalidateActivityMap= (Map)followUpMap.get("invalidateActivityMap");
        		//判断是否存在不可用的节点，如果存在则进行如下处理
            	if(invalidateActivityMap != null && !invalidateActivityMap.isEmpty()){
            		Iterator iter= invalidateActivityMap.keySet().iterator();
            		for (; iter.hasNext();) {
						String key = (String) iter.next();
						String value= (String)invalidateActivityMap.get(key);
						invalidateActivityStr += value+",";
					}
            		if(invalidateActivityStr.endsWith(",")){
            			invalidateActivityStr= invalidateActivityStr.substring(0, invalidateActivityStr.length()-1);
            		}
            	}
        	}
        }
        mainMap.put("popFlag1", String.valueOf(popFlag1));
        mainMap.put("invalidateActivity", invalidateActivityStr);
        mainMap.put("browser", request.getHeader("User-Agent"));
        response.setContentType("application/text;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter pw = response.getWriter();
        if(log.isInfoEnabled()){
        	log.info("prepop-json:="+mainMap.toString());
        }
        pw.write(mainMap.toString());
        pw.flush();
        return null;
    }

    /**
     * 弹出流程节点选择页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView popProcessSelectPageNew(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	if(log.isInfoEnabled()){
    		log.info("__ActionToken:="+request.getParameter("__ActionToken"));
    	}
    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');</script>");
    		return null;
    	}
    	ModelAndView mav = new ModelAndView("collaboration/processModeSelectorMain");
        FlowData flowData = FlowData.flowdataFromRequest();
        String currentNodeId = request.getParameter("currentNodeId");
        String _affairId = request.getParameter("affair_id");
        boolean templateFlag = "true".equals(request.getParameter("isFromTemplate"));
        String formData = request.getParameter("formData");
        //是否是督办时的匹配节点
        String fromColsupervise = request.getParameter("fromColsupervise");
        String appName = request.getParameter("appName");
        boolean isEdoc = String.valueOf(ApplicationCategoryEnum.edoc.getKey()).equals(appName);

        mav.addObject("fromColsupervise", fromColsupervise);
        ColSummary summary = null;
        Affair affair = null;
        if(_affairId != null && !"".equals(_affairId)){
	        affair = affairManager.getById(Long.parseLong(_affairId));
        }
        if(affair != null && !isEdoc){
        	summary = colManager.getColSummaryById(affair.getObjectId(), false);
        	if(!templateFlag){
        		Long templateId= summary.getTempleteId();
        		if(null!=templateId){
        			if( templateId.longValue()!=0 && templateId.longValue()!=-1){
        				templateFlag= true;
        			}
        		}
        	}
        }
        if(isEdoc && (_affairId != null && !"".equals(_affairId))){
        	EdocSummary edocSummary= edocManager.getEdocSummaryById(affair.getObjectId(), false);
        	if(!templateFlag){
        		Long templateId= edocSummary.getTempleteId();
        		if(null!=templateId){
        			if( templateId.longValue()!=0 && templateId.longValue()!=-1){
        				templateFlag= true;
        			}
        		}
        	}
        }
        String processId = null;
        BPMProcess process = null;
        if(log.isInfoEnabled()){
        	log.info("flowData.isEmpty():="+flowData.isEmpty());
        }
        if(!flowData.isEmpty()){
	        if(flowData.getXml() == null || "".equals(flowData.getXml())){
		        //根据选人界面传来的people生成流程定义XML
//		        processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
		        //生成流程定义对象
//		        process = ColHelper.getProcess(processId);
//		        ColHelper.deleteReadyProcess(processId);
	        	process= ColHelper.getBPMProcessByPeople(flowData, processId, false);
		        processId= process.getId();
		        String caseProcessXML = process.toXML();
		        if(StringUtils.isNotBlank(caseProcessXML)){
		        	caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
					flowData.setXml(caseProcessXML);
		        }
		        currentNodeId = process.getStart().getId();
//		        mav.addObject("caseProcessXML", StringEscapeUtils.escapeHtml(caseProcessXML));
//		        mav.addObject("process_desc_by", FlowData.DESC_BY_XML);
	        }else{
	        	process = flowData.toBPMProcess();
	        }
        }else{
        	processId = request.getParameter("processId");
        	process = ColHelper.getCaseProcess(processId);
        }
        //从页面获得是否触发新流程的参数
        boolean hasNewflow= "true".equals(request.getParameter("hasNewflow"));
        if(log.isInfoEnabled()){
        	log.info("hasNewflow:="+hasNewflow);
        }
        if(hasNewflow){
        	BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
		    User user = CurrentUser.get();
            Long currentNodeOwner = user.getId();
            if(affair != null){
               // summary = colManager.getColSummaryById(affair.getObjectId(), false);
                if(!currentNodeOwner.equals(affair.getMemberId())){//如果是代理人员处理待办，则当前节点处理人员id应更新为v3x_affair中的member_id值
                    currentNodeOwner = affair.getMemberId();
                }
            }
	        if(summary != null){
	            //当前节点有新流程时，弹出选择要触发的新流程信息
	            List<NewflowModel> newflowModels =
	            	newflowManager.getNewflowModelList(summary.getId(), summary.getTempleteId(), currentNodeId);
	            if(newflowModels != null && !newflowModels.isEmpty()){
	            	if(log.isInfoEnabled()){
	            		log.info("newflowModels:="+newflowModels.size());
	            	}
	                Map<Long, String> conditionMap = new HashMap<Long, String>();
	                for (NewflowModel model : newflowModels) {
	                    //设置发起者
	                    String newflowSender = model.getNewflowSender();
	                    List<PersonInfo> pl = new ArrayList<PersonInfo>();
	                    if("CurrentNode".equals(newflowSender)){//当前节点
	                    	V3xOrgMember m = orgManager.getMemberById(currentNodeOwner);
	                    	if(m != null && m.isValid()){
                                PersonInfo p = new PersonInfo(currentNodeOwner, m.getName());
                                pl.add(p);
                            }else{
                            	PersonInfo p = new PersonInfo(user.getId(), user.getName());
    	                        pl.add(p);
                            }
	                    }else if("CurrentSender".equals(newflowSender)){ //当前流程发起者
	                        Long pid = summary.getStartMemberId();
	                        V3xOrgMember m = orgManager.getMemberById(pid);
                            if(m != null && m.isValid()){
                                PersonInfo p = new PersonInfo(pid, m.getName());
                                pl.add(p);
                            }
	                    }else{ //所选择人员
	                        Set<V3xOrgMember> ms = orgManager.getMembersByTypeAndIds(newflowSender);
	                        if(ms != null && !ms.isEmpty()){
	                            for(V3xOrgMember m : ms){
	                            	if(m != null && m.isValid()){//对人员不可用这种情况进行处理
	                            		PersonInfo p = new PersonInfo();
		                                p.setId(m.getId() + "");
		                                p.setName(m.getName());
		                                pl.add(p);
	                            	}
	                            }
	                        }
	                    }
	                    model.setPeople(pl);
	                    String triggerCondition = model.getTriggerCondition();
	                    if(Strings.isNotBlank(triggerCondition)){
	                    	conditionMap.put(model.getId(), triggerCondition);
	                    }else{
	                    	conditionMap.put(model.getId(), "true");
	                    }
	                }
                    //得到所有页面传递过来的参数，用于触发条件判断
                    /*
                    Map<String, String> paramAndValueMap = new HashMap<String, String>();
                    Enumeration e = (Enumeration)request.getParameterNames();
                    while(e.hasMoreElements()){
                        String parName = (String)e.nextElement();
                        paramAndValueMap.put(parName, request.getParameter(parName));
                    }*/
	                boolean isFromForm = Strings.isNotBlank(formData) ;
	        		Long formAppId= -1l;
	        		Long masterId= -1l;
	        		if(isFromForm){
	        			String formApp = seeyonPolicy.getFormApp() ;
	        			formAppId= Long.parseLong(formApp);
	        			ColBody body = null ;
		        		if(summary != null){
		        			body = summary.getFirstBody();
		        		}
		        		if(body != null){
		        			String mastridStr = body.getContent() ;
		        			masterId= Long.parseLong(mastridStr);
		        		}
	        		}
                    Map<Long, String> conditionResultMap = ColHelper.calculateCondition(conditionMap, null, summary.getStartMemberId(),
                    		currentNodeOwner, orgManager, false,formAppId,masterId,formData,summary.getOrgAccountId());
                    if(log.isInfoEnabled()){
                    	log.info("NF-conditionResultMap:="+conditionResultMap);
                    }
                    mav.addObject("newflowModels", newflowModels);
	                mav.addObject("conditionResultMap", conditionResultMap);
	            }
	        }
		}

    	boolean popFlag1= "true".equals(request.getParameter("popFlag1"));
    	if(log.isInfoEnabled()){
    		log.info("currentNodeId:="+currentNodeId);
    		log.info("popFlag1:="+popFlag1);
        }
    	if(popFlag1){//获得后续节点和分之条件信息
    		long caseId = 0; //发起时，case不存在
			String _caseId = request.getParameter("caseId");
			if(Strings.isNotBlank(_caseId)){
				caseId = Long.parseLong(_caseId);
			}
			if(!"start".equals(currentNodeId) && caseId == 0){ //发起时，case不存在
				log.warn("=======================================\\n处理"+
						(isEdoc?"公文":"协同")+"时，caseId不存在。Parameter：" +
						request.getParameterMap() +
						"\\n=======================================");
			}
			boolean isFromForm = Strings.isNotBlank(formData) ;//是否来自表单协同
    		//定义表单协同的表单域对象
    		Map<String,String[]> fieldMap = new HashMap<String,String[]>() ;
    		//定义表单协同的表单域及数据对象
    		Map<String,String[]> fieldDataBaseMap = new HashMap<String,String[]>() ;
    		//定义表单协同的上下文对象
    		Map<String,String[]> contextMap = new HashMap<String,String[]>() ;
    		String formApp = "" ;
    		String mastrid = null ;
    		if(isFromForm){//如果是来自表单协同
    			if("start".equals(currentNodeId)){//当前处理者是开始节点
    				//从流程数据flowData中获得表单信息
    				String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
    				//获得表单应用formApp
    				formApp = formInfo[0] ;
    			}else{//且当前处理者不是开始节点
    				BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
    				//从流程模板定义中获得表单应用formApp
    				formApp = seeyonPolicy.getFormApp() ;
    				//表单数据对应的主表记录主键值
    				ColBody body = null ;
    				if(summary != null){
    					//从协同中获得协同主体信息
            			body = summary.getFirstBody();
            		}
    				if(body != null){
    					//获得表单数据对应的主表记录主键值
            			mastrid = body.getContent() ;
            		}
    				fieldDataBaseMap  =	FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
    			}
    			//获得表单输入域的值
    			fieldMap = FormHelper.getFieldValueMap(formApp,formData) ;
    			if(fieldDataBaseMap != null){
    				contextMap.putAll(fieldDataBaseMap) ;
    			}
    			if(fieldMap != null){
    				contextMap.putAll(fieldMap) ;
    			}
    		}
    		//后续节点和条件分支处理开始
            BPMAbstractNode currentNode = null;
            Long startMemberId = null;
            long startMemberLoginAccountId = -1;
            Long currentNodeMemberId = CurrentUser.get().getId();
            long workItemId= -1l;
            if("start".equals(currentNodeId)){
            	startMemberId = CurrentUser.get().getId();
            	startMemberLoginAccountId = CurrentUser.get().getLoginAccount();
            }else{
            	if(summary != null){
            		startMemberLoginAccountId = summary.getOrgAccountId().longValue();
            	}
            	startMemberId = affair.getSenderId();
	            if(!currentNodeMemberId.equals(affair.getMemberId())){//如果是代理人员处理待办，则当前节点处理人员id应更新为v3x_affair中的member_id值
	            	currentNodeMemberId = affair.getMemberId();
	            }
	            if(affair.getSubObjectId() != null){
        			workItemId = affair.getSubObjectId();
        		}
            }

//    		WorkflowEventListener.ProcessModeSelector selector =
//    			ColHelper.preRunCase(process, currentNodeId, templateFlag, caseId,contextMap);
            Long formAppId= -1l;
            Long masterId= -1l;
            if(!"".equals(formApp.trim())){
            	formAppId= Long.parseLong(formApp);
            }
            if(null!=mastrid){
            	masterId= Long.parseLong(mastrid);
            }
    		WorkflowEventListener.ProcessModeSelector selector1 =
    			ColHelper.preRunCaseNext(process,
    					currentNodeId,
    					templateFlag,
    					caseId,
    					contextMap,
    					startMemberId,
    					currentNodeMemberId,
    					orgManager,
    					false,
//    					fieldMap,
//    					fieldDataBaseMap,
    					formAppId,masterId,formData,workItemId,startMemberLoginAccountId);
	        List<NodeAddition> invalidateActivity = selector1.getInvalidateActivity();
	        if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
	        	mav.addObject("invalidateActivity", invalidateActivity);
	        	if(log.isInfoEnabled()){
	        		log.info("invalidateActivityMap:="+selector1.getInvalidateActivityMap());
	        	}
	        	mav.addObject("invalidateActivityMap", selector1.getInvalidateActivityMap());
	        }


	        Map<String, ProcessModeSelectorModel> selectorModels =
	        	new LinkedHashMap<String, ProcessModeSelectorModel>();
	        List<String> selectorModelNodeIds = new ArrayList<String>();
            if(selector1 != null){
                List<NodeAddition> nodeAdditions = selector1.getNodeAdditions();
                if(nodeAdditions != null){
                    for(int i=nodeAdditions.size()-1;i>=0;i--){
                    	NodeAddition addition=nodeAdditions.get(i);
                    	if(addition.isOnlyDisplayName()){
                    		ProcessModeSelectorModel selectorModel = new ProcessModeSelectorModel();
                        	selectorModel.setAddition(null);
                        	selectorModel.setNodeId(addition.getNodeId());
                        	selectorModel.setNodeName(addition.getNodeName());
                        	selectorModel.setFromIsInform(addition.isFromIsInform());
                        	selectorModel.setProcessMode(addition.getProcessMode());
                        	selectorModels.put(addition.getNodeId(), selectorModel);
                        	selectorModelNodeIds.add(addition.getNodeId());
                    	}else{
                    		ProcessModeSelectorModel selectorModel = new ProcessModeSelectorModel();
                        	selectorModel.setAddition(addition);
                        	selectorModel.setNodeId(addition.getNodeId());
                        	selectorModel.setNodeName(addition.getNodeName());
                        	selectorModel.setFromIsInform(addition.isFromIsInform());
                        	selectorModels.put(addition.getNodeId(), selectorModel);
                        	selectorModelNodeIds.add(addition.getNodeId());
                    	}
                    }
                }
            }
            //节点类型
            mav.addObject("nodeTypes", selector1.nodeTypes);

            //获得当前节点下的所有条件分支
//            Map<String,Object> hash = ColHelper.getCondition(currentNode, null, startMemberId, CurrentUser.get().getId(), orgManager,false);

            Map<String,Object> hash = selector1.conditions;
            if(hash != null){
            	List<String> keys = (List<String>)hash.get("keys");
            	List<String> nodeNames = (List<String>)hash.get("names");
            	List<String> conditions = (List<String>)hash.get("conditions");
            	List<String> forces = (List<String>)hash.get("forces");
            	List<String> links = (List<String>)hash.get("links");
            	List<Integer> conditionTypes = (List<Integer>)hash.get("conditionTypes");
            	mav.addObject("allNodes", hash.get("allNodes"));
            	mav.addObject("nodeCount", hash.get("nodeCount"));
            	if(log.isInfoEnabled()){
            		log.info("allNodes:="+hash.get("allNodes")+";nodeCount="+hash.get("nodeCount"));
            	}
            	if(keys.size() > 0 && conditions.size() > 0){
            		for (int k =  keys.size()-1; k >=0; k--) {
            			String nodeId = keys.get(k);
            			ProcessModeSelectorModel selectorModel = selectorModels.get(nodeId);
            			if(selectorModel == null){
            				selectorModel = new ProcessModeSelectorModel();
            				selectorModels.put(nodeId, selectorModel);
            				selectorModelNodeIds.add(nodeId);
            				selectorModel.setNodeName(nodeNames.get(k));
            				selectorModel.setProcessMode(process.getActivityById(nodeId).getSeeyonPolicy().getProcessMode());
            			}
            			selectorModel.setNodeId(nodeId);
            			selectorModel.setCondition(conditions.get(k));
            			selectorModel.setForce(forces.get(k));
            			selectorModel.setLink(links.get(k));
            			selectorModel.setConditionType(conditionTypes.get(k));
            		}
            	}
            }
            //成发集团项目 程炯 2012-9-13 根据流程密级筛选流程分支选人 begin
            String secretLevel = request.getParameter("secretLevel");
            if(null != secretLevel && !"".equals(secretLevel)){
	            for(Map.Entry entry : selectorModels.entrySet()){
	            	ProcessModeSelectorModel selectorModel = (ProcessModeSelectorModel) entry.getValue();
	            	if(selectorModel.getAddition() == null){
	            		continue;
	            	}
	            	List<PersonInfo> list = selectorModel.getAddition().getPeople();
	            	List<PersonInfo> newList = new ArrayList<PersonInfo>();
	    			for (PersonInfo personInfo : list) {
	    				V3xOrgMember member = orgManager.getMemberById(Long.parseLong(personInfo.getId()));
	    				Integer memberSecretLevel = member.getSecretLevel();
	    				// 2017-02-09 诚佰公司 修改空值判断
	    				if(memberSecretLevel != null && memberSecretLevel >= Integer.parseInt(secretLevel)){
	    					newList.add(personInfo);
	    				}
	    				//newList.add(personInfo);
	    				// 2017-02-09 诚佰公司
					}
	    			selectorModel.getAddition().setPeople(newList);
	            }
            }
            //end
	        mav.addObject("selectorModels", selectorModels);
	        mav.addObject("selectorModelNodeIds", selectorModels.keySet());
	        if(selectorModels.keySet()!=null){
	        	mav.addObject("selectorModelNodeIdsSize", selectorModels.keySet().size());
	        }else{
	        	mav.addObject("selectorModelNodeIdsSize", 0);
	        }
	        if(log.isInfoEnabled()){
	        	log.info("selectorModelNodeIds:="+selectorModels.keySet());
	        }
    	}
        return mav;
    }

    /**
     * popProcessSelectPageNext()
     * 弹出流程节点选择页面(对后续知会节点进行处理)
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView popProcessSelectPageNext(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	if(log.isInfoEnabled()){
    		log.info("__ActionToken:="+request.getParameter("__ActionToken"));
    	}
    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');</script>");
    		return null;
    	}
    	ModelAndView mav = new ModelAndView("collaboration/processModeSelectorMain");
        FlowData flowData = FlowData.flowdataFromRequest();
        String currentNodeId = request.getParameter("currentNodeId");
        String _affairId = request.getParameter("affair_id");
        boolean templateFlag = "true".equals(request.getParameter("isFromTemplate"));
        String formData = request.getParameter("formData");
        //是否是督办时的匹配节点
        String fromColsupervise = request.getParameter("fromColsupervise");
        String appName = request.getParameter("appName");
        boolean isEdoc = String.valueOf(ApplicationCategoryEnum.edoc.getKey()).equals(appName);

        mav.addObject("fromColsupervise", fromColsupervise);
        ColSummary summary = null;
        Affair affair = null;
        if(_affairId != null && !"".equals(_affairId)){
	        affair = affairManager.getById(Long.parseLong(_affairId));
        }
        if(affair != null && !isEdoc){
        	summary = colManager.getColSummaryById(affair.getObjectId(), false);
        }

        String processId = null;
        BPMProcess process = null;
        if(log.isInfoEnabled()){
        	log.info("flowData.isEmpty():="+flowData.isEmpty());
        }
        if(!flowData.isEmpty()){
	        if(flowData.getXml() == null || "".equals(flowData.getXml())){
		        //根据选人界面传来的people生成流程定义XML
//		        processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
		        //生成流程定义对象
//		        process = ColHelper.getProcess(processId);
//		        ColHelper.deleteReadyProcess(processId);
	        	process= ColHelper.getBPMProcessByPeople(flowData, processId, false);
		        processId= process.getId();
		        String caseProcessXML = process.toXML();
		        if(StringUtils.isNotBlank(caseProcessXML)){
		        	caseProcessXML = ColHelper.trimXMLProcessor(caseProcessXML);
					flowData.setXml(caseProcessXML);
		        }
		        currentNodeId = process.getStart().getId();
//		        mav.addObject("caseProcessXML", StringEscapeUtils.escapeHtml(caseProcessXML));
//		        mav.addObject("process_desc_by", FlowData.DESC_BY_XML);
	        }else{
	        	process = flowData.toBPMProcess();
	        }
        }else{
        	processId = request.getParameter("processId");
        	process = ColHelper.getCaseProcess(processId);
        }
		//获得后续节点和分之条件信息
		long caseId = 0; //发起时，case不存在
		String _caseId = request.getParameter("caseId");
		if(Strings.isNotBlank(_caseId)){
			caseId = Long.parseLong(_caseId);
		}
		if(!"start".equals(currentNodeId) && caseId == 0){ //发起时，case不存在
			log.warn("=======================================\\n处理"+
					(isEdoc?"公文":"协同")+"时，caseId不存在。Parameter：" +
					request.getParameterMap() +
					"\\n=======================================");
		}
		boolean isFromForm = Strings.isNotBlank(formData) ;//是否来自表单协同
		//定义表单协同的表单域对象
		Map<String,String[]> fieldMap = new HashMap<String,String[]>() ;
		//定义表单协同的表单域及数据对象
		Map<String,String[]> fieldDataBaseMap = new HashMap<String,String[]>() ;
		//定义表单协同的上下文对象
		Map<String,String[]> contextMap = new HashMap<String,String[]>() ;
		String formApp = "" ;
		String mastrid = null ;
		if(isFromForm){//如果是来自表单协同
			if("start".equals(currentNodeId)){//当前处理者是开始节点
				//从流程数据flowData中获得表单信息
				String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
				//获得表单应用formApp
				formApp = formInfo[0] ;
			}else{//且当前处理者不是开始节点
				BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
				//从流程模板定义中获得表单应用formApp
				formApp = seeyonPolicy.getFormApp() ;
				//表单数据对应的主表记录主键值
				ColBody body = null ;
				if(summary != null){
					//从协同中获得协同主体信息
        			body = summary.getFirstBody();
        		}
				if(body != null){
					//获得表单数据对应的主表记录主键值
        			mastrid = body.getContent() ;
        		}
				fieldDataBaseMap  =	FormHelper.getFieldValueMap(formApp, seeyonPolicy.getForm(), seeyonPolicy.getOperationName(), mastrid) ;
			}
			//获得表单输入域的值
			fieldMap = FormHelper.getFieldValueMap(formApp,formData) ;
			if(fieldDataBaseMap != null){
				contextMap.putAll(fieldDataBaseMap) ;
			}
			if(fieldMap != null){
				contextMap.putAll(fieldMap) ;
			}
		}
		//后续节点和条件分支处理开始
        Long startMemberId = null;
        long startMemberLoginAccountId = -1;
        Long currentNodeMemberId = CurrentUser.get().getId();
        if("start".equals(currentNodeId)){
        	startMemberId = CurrentUser.get().getId();
        	startMemberLoginAccountId = CurrentUser.get().getLoginAccount();
        }else{
        	startMemberId = affair.getSenderId();
        	if(summary != null){
        		startMemberLoginAccountId = summary.getOrgAccountId().longValue();
        	}
        	if(!currentNodeMemberId.equals(affair.getMemberId())){//如果是代理人员处理待办，则当前节点处理人员id应更新为v3x_affair中的member_id值
        		currentNodeMemberId = affair.getMemberId();
            }
        }
        //获得所有未选中的节点信息
        String allNotSelectNodes = request.getParameter("allNotSelectNodes");
    	if(log.isInfoEnabled()){
        	log.info("allNotSelectNodes:="+allNotSelectNodes);
        }
    	List<String> allNotSelectNodeList= new ArrayList<String>();
    	JSONArray allNotSelectNodesArray= PopSelectParseUtil.getPopInformNodeSelectedValues(allNotSelectNodes,"allNotSelectNodes");
    	if(allNotSelectNodesArray!=null){
        	for (int i=0;i<=allNotSelectNodesArray.length()-1;i++) {
        		String notSelectNode=allNotSelectNodesArray.getString(i);
        		allNotSelectNodeList.add(notSelectNode);
        		//log.info("selectNode["+i+"]:="+notSelectNode);
        		//String notSelectNodeConditionValue= request.getParameter("nodeConditionId"+notSelectNode);
        		//log.info("notSelectNodeConditionValue["+i+"]:="+notSelectNodeConditionValue);
			}
        }

        //获得所有已选中的节点信息
    	String allSelectNodes = request.getParameter("allSelectNodes");
    	if(log.isInfoEnabled()){
        	log.info("allSelectNodes:="+allSelectNodes);
        }
    	List<String> allSelectNodeList= new ArrayList<String>();
    	JSONArray allSelectNodesArray= PopSelectParseUtil.getPopInformNodeSelectedValues(allSelectNodes,"allSelectNodes");
    	if(allSelectNodesArray!=null){
        	for (int i=0;i<=allSelectNodesArray.length()-1;i++) {
        		String selectNode=allSelectNodesArray.getString(i);
        		allSelectNodeList.add(selectNode);
        		//log.info("selectNode["+i+"]:="+selectNode);
        		//String selectNodeConditionValue= request.getParameter("nodeConditionId"+selectNode);
        		//log.info("selectNodeConditionValue["+i+"]:="+selectNodeConditionValue);
			}
        }

        //获得所有选中的知会节点
        String informNodes= request.getParameter("informNodes");
        String allSelectInformNodes= request.getParameter("allSelectInformNodes");
        if(log.isInfoEnabled()){
        	log.info("informNodes:="+informNodes);
        	log.info("allSelectInformNodes:="+allSelectInformNodes);
        }
        List<String> informNodeList= new ArrayList<String>();
        List<String> allInformNodeList= new ArrayList<String>();
        JSONArray infromNodesArray= PopSelectParseUtil.getPopInformNodeSelectedValues(informNodes,"informNodes");
        if(infromNodesArray!=null){
        	for (int i=0;i<=infromNodesArray.length()-1;i++) {
        		String informNode=infromNodesArray.getString(i);
        		informNodeList.add(informNode);
        		//log.info("informNodeId["+i+"]:="+informNode);
        		//String informNodeConditionValue= request.getParameter("nodeConditionId"+informNode);
        		//log.info("informNodeConditionValue["+i+"]:="+informNodeConditionValue);
			}
        }
        JSONArray allInfromNodesArray= PopSelectParseUtil.getPopInformNodeSelectedValues(allSelectInformNodes,"allSelectNodes");
        if(allInfromNodesArray!=null){
            for (int i=0;i<=allInfromNodesArray.length()-1;i++) {
                String informNode=allInfromNodesArray.getString(i);
                allInformNodeList.add(informNode);
            }
        }
        Long formAppId= -1l;
        Long masterId= -1l;
        if(!"".equals(formApp.trim())){
        	formAppId= Long.parseLong(formApp);
        }
        if(null!=mastrid){
        	masterId= Long.parseLong(mastrid);
        }
        long workItemId= -1l;
		if(!"start".equals(currentNodeId)){//当前处理者是开始节点
			if(affair.getSubObjectId() != null){
    			workItemId = affair.getSubObjectId();
    		}
		}
		WorkflowEventListener.ProcessModeSelector selector1 = ColHelper.preRunCaseNextOfInformNodes(process,
					currentNodeId,
					templateFlag,
					caseId,
					contextMap,
					startMemberId,
					currentNodeMemberId,
					orgManager,
					false,
//					fieldMap,
//					fieldDataBaseMap,
					allNotSelectNodeList,
					allSelectNodeList,
					informNodeList,allInformNodeList,formAppId,masterId,formData,workItemId,startMemberLoginAccountId
					);
        List<NodeAddition> invalidateActivity = selector1.getInvalidateActivity();
        if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
        	mav.addObject("invalidateActivity", invalidateActivity);
        	if(log.isInfoEnabled()){
        		log.info("invalidateActivityMap:="+selector1.getInvalidateActivityMap());
        	}
        	mav.addObject("invalidateActivityMap", selector1.getInvalidateActivityMap());
        }


        Map<String, ProcessModeSelectorModel> selectorModels =
        	new LinkedHashMap<String, ProcessModeSelectorModel>();
        List<String> selectorModelNodeIds = new ArrayList<String>();
        if(selector1 != null){
            List<NodeAddition> nodeAdditions = selector1.getNodeAdditions();
            if(nodeAdditions != null){
                for(int i=nodeAdditions.size()-1;i>=0;i--){
                	NodeAddition addition=nodeAdditions.get(i);
                	if(addition.isOnlyDisplayName()){
                		ProcessModeSelectorModel selectorModel = new ProcessModeSelectorModel();
                    	selectorModel.setAddition(null);
                    	selectorModel.setNodeId(addition.getNodeId());
                    	selectorModel.setNodeName(addition.getNodeName());
                    	selectorModel.setFromIsInform(addition.isFromIsInform());
                    	selectorModel.setProcessMode(addition.getProcessMode());
                    	selectorModels.put(addition.getNodeId(), selectorModel);
                    	selectorModelNodeIds.add(addition.getNodeId());
                	}else{
                		ProcessModeSelectorModel selectorModel = new ProcessModeSelectorModel();
                    	selectorModel.setAddition(addition);
                    	selectorModel.setNodeId(addition.getNodeId());
                    	selectorModel.setNodeName(addition.getNodeName());
                    	selectorModel.setFromIsInform(addition.isFromIsInform());
                    	selectorModels.put(addition.getNodeId(), selectorModel);
                    	selectorModelNodeIds.add(addition.getNodeId());
                	}
                }
            }
        }
        //成发集团项目 程炯 2012-9-13 根据流程密级筛选流程分支选人 begin
        String secretLevel = request.getParameter("secretLevel");
        if(null != secretLevel && !"".equals(secretLevel)){
            for(Map.Entry entry : selectorModels.entrySet()){
                ProcessModeSelectorModel selectorModel = (ProcessModeSelectorModel) entry.getValue();
                if(selectorModel.getAddition() == null){
                    continue;
                }
                List<PersonInfo> list = selectorModel.getAddition().getPeople();
                List<PersonInfo> newList = new ArrayList<PersonInfo>();
                for (PersonInfo personInfo : list) {
                    V3xOrgMember member = orgManager.getMemberById(Long.parseLong(personInfo.getId()));
                    Integer memberSecretLevel = member.getSecretLevel();
                    if(memberSecretLevel >= Integer.parseInt(secretLevel)){
                        newList.add(personInfo);
                    }
                }
                selectorModel.getAddition().setPeople(newList);
            }
        }
        //end
        //节点类型
        mav.addObject("nodeTypes", selector1.nodeTypes);
        Map<String,Object> hash = selector1.conditions;
        if(hash != null && hash.size()>0){
        	List<String> keys = (List<String>)hash.get("keys");
        	List<String> nodeNames = (List<String>)hash.get("names");
        	List<String> conditions = (List<String>)hash.get("conditions");
        	List<String> forces = (List<String>)hash.get("forces");
        	List<String> links = (List<String>)hash.get("links");
        	List<Integer> conditionTypes = (List<Integer>)hash.get("conditionTypes");
        	mav.addObject("allNodes", hash.get("allNodes"));
        	mav.addObject("nodeCount", hash.get("nodeCount"));
        	if(log.isInfoEnabled()){
        		log.info("allNodes:="+hash.get("allNodes")+";nodeCount="+hash.get("nodeCount"));
        	}
        	if(keys.size() > 0 && conditions.size() > 0){
        		for (int k =  keys.size()-1; k >=0; k--) {
        			String nodeId = keys.get(k);
        			ProcessModeSelectorModel selectorModel = selectorModels.get(nodeId);
        			if(selectorModel == null){
        				selectorModel = new ProcessModeSelectorModel();
        				selectorModels.put(nodeId, selectorModel);
        				selectorModelNodeIds.add(nodeId);
        				selectorModel.setNodeName(nodeNames.get(k));
        				selectorModel.setProcessMode(process.getActivityById(nodeId).getSeeyonPolicy().getProcessMode());
        			}
        			selectorModel.setNodeId(nodeId);
        			selectorModel.setCondition(conditions.get(k));
        			selectorModel.setForce(forces.get(k));
        			selectorModel.setLink(links.get(k));
        			selectorModel.setConditionType(conditionTypes.get(k));
        		}
        	}
        }
        mav.addObject("selectorModels", selectorModels);
        mav.addObject("selectorModelNodeIds", selectorModels.keySet());
        if(selectorModels.keySet()!=null){
        	mav.addObject("selectorModelNodeIdsSize", selectorModels.keySet().size());
        }else{
        	mav.addObject("selectorModelNodeIdsSize", 0);
        }
        if(log.isInfoEnabled()){
        	log.info("selectorModelNodeIds:="+selectorModels.keySet());
        }
        return mav;
    }

    /**
     * @deprecated 废弃，被preSend方法替代 {@link #preSend(HttpServletRequest, HttpServletResponse)}
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView waitSendPreSend(HttpServletRequest request,
            				HttpServletResponse response) throws Exception {
		String _summaryId = request.getParameter("summaryId");
		Long summaryId = Long.parseLong(_summaryId);
		ColSummary summary = colManager.getColSummaryById(summaryId, false);
		WorkflowEventListener.ProcessModeSelector selector = null;
		String processId = summary.getProcessId();
		ModelAndView mav = new ModelAndView("collaboration/processModeSelectorMain");
		if(processId == null || "".equals(processId.trim())){
			selector = null;
		}else{
			BPMProcess process = ColHelper.getRunningProcessByProcessId(processId);

			Long templateId = summary.getTempleteId();
			boolean templateFlag = false;
			if(templateId != null){
				templateFlag = true;
			}

			Long caseId = 0L;
			String _caseId = request.getParameter("caseId");
			if(Strings.isNotBlank(_caseId)){
				caseId = Long.valueOf(_caseId);
			}

			selector = ColHelper.preRunCase(process, "start", templateFlag, caseId);
	        List<NodeAddition> invalidateActivity = selector.getInvalidateActivity();
	        if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
	        	mav.addObject("invalidateActivity", invalidateActivity);
	        	return mav;
	        }

			HashMap<String,String> hash = new HashMap<String,String>();
			hash.put("currentIsStart", "true");
	        ColHelper.findDirectHumenChildrenCondition(process.getStart(), hash);
	        if(hash.size()>0){
	        	Set<Map.Entry<String, String>> entry = hash.entrySet();
	        	List<String> keys = new ArrayList<String>();
	        	List<String> nodeNames = new ArrayList<String>();
	        	List<String> conditions = new ArrayList<String>();
	        	List<String> forces = new ArrayList<String>();
	        	List<String> links = new ArrayList<String>();
	        	List<Integer> conditionTypes = new ArrayList<Integer>();
	        	String[] temp = null;
	        	String[] temp1 = null;
	        	String order = hash.get("order");
	        	if(order != null && order.indexOf("$")!=-1) {
	        		temp1 = StringUtils.split(order,"$");
	        	}
	        	User user = CurrentUser.get();
	        	StringBuffer sb = new StringBuffer();
	        	if(temp1!=null && temp1.length>0) {
	        		for(String item:temp1){
	        			String value = hash.get(item);
	            		if(value!=null&&value.indexOf("↗")!=-1){
	            			sb.append(item+":");
	    	        		keys.add(item);
	    	        		links.add(hash.get("linkTo"+item));
	    	        		temp = value.split("↗");
	    	        		if(temp != null){
	    	        			nodeNames.add(temp[0]);
	    	        			temp[1] = temp[1].replaceAll("Department", String.valueOf(user.getDepartmentId()))
	    	        			.replaceAll("Post",String.valueOf(user.getPostId())).replaceAll("Level", String.valueOf(user.getLevelId()))
	    	        			.replaceAll("Account", String.valueOf(user.getAccountId())).replaceAll("'", "\\\\\'")
	    	        			.replaceAll("&#91;", "").replaceAll("&#93;", "").replaceAll("&quot;form:check&quot;", "true")
	    	        			.replaceAll("&quot;form:uncheck&quot;", "false");
	    	        			if(temp[1].indexOf("handCondition")!=-1) {
	    	        				temp[1] = temp[1].replaceAll("handCondition","false");
	    	        				conditionTypes.add(2);
	    	        			}else
	    	        				conditionTypes.add(0);
	    	        			conditions.add(temp[1]);
	    	        			if(temp.length==3 && "1".equals(temp[2]))
	    	        				forces.add("true");
	    	        			else
	    	        				forces.add("false");
	    	        		}
	            		}
	            	}
	        	}else {
		        	for(Map.Entry<String, String> item:entry){
		        		if(item.getValue()!=null&&item.getValue().indexOf("↗")!=-1){
		        			sb.append(item.getKey()+":");
			        		keys.add(item.getKey());
			        		links.add(hash.get("linkTo"+item.getKey()));
			        		temp = item.getValue().split("↗");
			        		if(temp != null){
			        			nodeNames.add(temp[0]);
			        			temp[1] = temp[1].replaceAll("Department", String.valueOf(user.getDepartmentId()))
			        			.replaceAll("Post",String.valueOf(user.getPostId())).replaceAll("Level", String.valueOf(user.getLevelId()))
			        			.replaceAll("Account", String.valueOf(user.getAccountId())).replaceAll("'", "\\\\\'")
			        			.replaceAll("&#91;", "").replaceAll("&#93;", "").replaceAll("&quot;form:check&quot;", "true")
			        			.replaceAll("&quot;form:uncheck&quot;", "false");
			        			if(temp[1].indexOf("handCondition")!=-1) {
	    	        				temp[1] = temp[1].replaceAll("handCondition","false");
	    	        				conditionTypes.add(2);
	    	        			}else
	    	        				conditionTypes.add(0);
			        			conditions.add(temp[1]);
			        			if(temp.length==3)
	    	        				forces.add("true");
	    	        			else
	    	        				forces.add("false");
			        		}
		        		}
		        	}
	        	}
	        	if(keys.size() > 0 && conditions.size() >0){
		        	mav.addObject("allNodes", sb.toString());
		        	mav.addObject("keys", keys);
		        	mav.addObject("names", nodeNames);
		        	mav.addObject("conditions", conditions);
		        	mav.addObject("nodeCount", hash.get("nodeCount"));
		        	mav.addObject("forces", forces);
		        	mav.addObject("links", links);
		        	mav.addObject("templateId", templateId);

		        	if("FORM".equals(summary.getBodyType())) {
		        		ColBody body = summary.getFirstBody();
		        		String[] formPolicy = FormHelper.getFormPolicy(process);
		        		String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formPolicy[0], formPolicy[1], formPolicy[2], body.getContent(), _summaryId, "0", formPolicy[3],false);
		        		mav.addObject("formContent", formContent);
		        	}
		        }
	        }
		}

		mav.addObject("processModeSelector",selector);

		return mav;
    }


    /**
     * 发起协同,跳转到已发事项
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView send(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	String allNodes = request.getParameter("allNodes");
    	String templeteId = request.getParameter("templeteId");
    	String branchNodes = request.getParameter("branchNodes");
    	//json格式的字符串:选人
    	String popNodeSelected = request.getParameter("popNodeSelected");
    	//json格式的字符串:条件分支
    	String popNodeCondition = request.getParameter("popNodeCondition");
    	//json格式的字符串:新流程信息
    	String popNodeNewFlow = request.getParameter("popNodeNewFlow");
    	if(log.isInfoEnabled()){
    		log.info("popNodeSelected:="+popNodeSelected);
        	log.info("popNodeCondition:="+popNodeCondition);
        	log.info("popNodeNewFlow:="+popNodeNewFlow);
    	}
    	//从request对象取到选人信息
        FlowData flowData = FlowData.flowdataFromRequest();
    	boolean lostBranchData = false;
    	if(Strings.isNotBlank(templeteId)){
    		lostBranchData = Strings.isBlank(allNodes) && ColHelper.hasCondition(null,"start",flowData.getXml(),null,false,null);
    	}
    	if(Strings.isBlank(request.getParameter("__ActionToken")) || lostBranchData){
    		if(lostBranchData)
    			log.error("不能获取分支数据，allNodes:"+allNodes+" branchNodes:"+branchNodes);
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');");
    		out.println("history.back();</script>");
    		return null;
    	}
    	User user = CurrentUser.get();
        ColSummary colSummary = new ColSummary();
        bind(request, colSummary);
        colSummary.setSubject(Strings.nobreakSpaceToSpace(colSummary.getSubject()));
        Map<String, Object> options = new HashMap<String, Object>();
        Constant.SendType sendType = Constant.SendType.normal;
        //成发集团项目 程炯
        if(colSummary.getSecretLevel() == null || "".equals(colSummary.getSecretLevel()))
        	colSummary.setSecretLevel(1);
        
        //来自流程模版
    	if(templeteId != null && !"".equals(templeteId)){
    		Long tId = Long.parseLong(templeteId);
    		String outMsg = templeteManager.checkTemplete(tId, user.getId());
    		if(Strings.isNotBlank(outMsg)){
    	    	/*PrintWriter pw = response.getWriter();
    	    	pw.println("<script>");
	    		pw.println("alert(\""+outMsg+"\");");
	    		pw.println("</script>");*/
    			String url = "/collaboration.do?method=newColl";
    			if(Strings.isNotBlank(request.getParameter("from"))){
    				url += "&from=" + request.getParameter("from");
    			}
    			request.setAttribute("errMsg", outMsg);
    			request.setAttribute("errMsgAlert", true);
    			return super.redirectModelAndView(url +"&outMsg=true");
    		}
    		colSummary.setTempleteId(tId);
    	}

        //是否重复发起
        if (null != request.getParameter("resend") && !"".equals(request.getParameter("resend"))) {
            sendType = Constant.SendType.resend;
        }

        String note = request.getParameter("note");//发起人附言
        ColOpinion senderOninion = new ColOpinion();
        senderOninion.setContent(note);
        senderOninion.setIdIfNew();
        senderOninion.affairIsTrack = request.getParameter("isTrack") != null;

        ColBody body = new ColBody();
        bind(request, body);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
        }
        String[] formInfo = null;
        long mId = 0;
        if("FORM".equals(body.getBodyType())){
        	 String temformParentId = request.getParameter("temformParentId");
        	 // 如果是表单个人模板则取原模板的id
        	 if(temformParentId != null && !"".equals(temformParentId))
        	    colSummary.setTempleteId(Long.parseLong(temformParentId));

        	String formData = request.getParameter("formData");
         	formInfo = FormHelper.getFormPolicy(flowData.getXml());
        	boolean isAddOperation = FormHelper.isAddOperation(formInfo[0], formInfo[1], formInfo[2]);
        	if(!isAddOperation) {
        		PrintWriter out = response.getWriter();
            	out.println("<script>");
            	String message = Constant.getString4CurrentUser("form.notAddOperation.label");
                out.print("alert(\"" + StringEscapeUtils.escapeJavaScript(message) + "\"); ");
            	out.println("history.back();");
            	out.println("</script>");
            	return null;
        	}
        	/**
        	 * 解析标题
        	 */
        	Templete templete = templeteManager.get(colSummary.getTempleteId()) ;
        	if(!Strings.isBlank(templete.getCollSubject())){
        		String subject = FormHelper.getCollSubjuet(formInfo[0], templete.getCollSubject(),request.getParameter("formSubject_value"),formInfo[1], formInfo[2],false) ;
        		if(Strings.isNotBlank(subject)){
        			colSummary.setSubject(Strings.getLimitLengthString(subject, 160, "...")) ;
        		}else{
        			colSummary.setSubject(templete.getSubject()+"(" + user.getName() + " " + Datetimes.formatDatetimeWithoutSecond(new Date()) + ")") ;
        		}
        	}

        	String masterId = request.getParameter("masterId");
        	String pagefrom = request.getParameter("pagefrom");
        	masterId = "".equals(masterId)?null:masterId;
        	formData = FormHelper.setFormAppendValue(user.getId(), user.getName(), user.getLoginName(), formData, request.getParameter("formSubject_value"), formInfo[0], formInfo[1], formInfo[2], masterId);
        	String operationType;
        	if(masterId == null){
        		operationType = null;
           	}else{
           		if("WaitSend".equals(pagefrom)) {
           			operationType = "update";
           		}

           		if(request.getParameter("draft").equals("true")){//表单如果先存为草稿就先删除后增加
    	        		operationType = "delete";
    	        	try{
                        String formType = "0";
                        if(colSummary.getNewflowType() != null && colSummary.getNewflowType() == Constant.NewflowType.child.ordinal()){
                            formType = "";
                        }
    	        		FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit",formType,operationType,"saveDraft",Constant.FormVouch.vouchDefault.getKey());
           			}
                	catch(DataDefineException e1){
                	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
                		  PrintWriter out = response.getWriter();
        	              	out.println("<script>");
        	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
        	              	out.println("history.back();");
        	              	out.println("</script>");
        	              	return null;
                	  }
              		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
              			  throw new RuntimeException(e1.getMessage());
              		  else{
              			  log.error("保存表单数据时发生错误", e1);
              			  //throw new RuntimeException("不能保存");
              			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              		  }

                	  }catch(Exception e){
              		  log.error("保存表单数据时发生错误", e);
              		  //throw new RuntimeException("不能保存");
              		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              	      }
    	        		operationType = null;
    	        		masterId = null;
    	        		formData = formData.replaceAll("recordid=\"\\d+\"|recordid=\"-\\d+\"", "state=\"add\"");
            		}else{//不是直接更新
            			operationType = "update";
            		}

        	}
        	try{
        		if("quoteFrom".equals(request.getParameter("quoteFromsign")) && masterId !=null){
        			String parentformSummaryId = request.getParameter("parentformSummaryId");
        			colSummary.setParentformSummaryId(Long.parseLong(parentformSummaryId));
        			FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","","update","quoteFrom",Constant.FormVouch.vouchDefault.getKey());
        			mId =  Long.parseLong(masterId);
        			this.attachmentManager.deleteByReference(Long.parseLong(parentformSummaryId), com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FormFILE.ordinal(),com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FormDOCUMENT.ordinal()) ;
        		}else
            	    mId = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","1",operationType,"send",Constant.FormVouch.vouchDefault.getKey());
        	}catch(DataDefineException e1){
        	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
        		  PrintWriter out = response.getWriter();
	              	out.println("<script>");
	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
	              	out.println("history.back();");
	              	out.println("</script>");
	              	return null;
        	  }
      		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
      			  throw new RuntimeException(e1.getMessage());
      		  else{
      			  log.error("保存表单数据时发生错误", e1);
      			  //throw new RuntimeException("不能保存");
      			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
      		  }

        	  }catch(Exception e){
      		  log.error("保存表单数据时发生错误", e);
      		  //throw new RuntimeException("不能保存");
      		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
      	      }
        	Long key = Long.valueOf(Thread.currentThread().getId());
        	List<String> flowIdMsgList = (List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey");
        	if(flowIdMsgList != null){
        		OperHelper.creatformmessage(request,response,flowIdMsgList);
        	}
        	colSummary.setFormAppId(Long.valueOf(formInfo[0]));//保存fromAppid
        	colSummary.setFormId(Long.valueOf(formInfo[1]));//保存表单id
        	colSummary.setFormRecordId(Long.valueOf(mId));//保存表单主id
       	    body.setContent(String.valueOf(mId));

        }
        //获得所有流程节点id
        Map<String, String[]> map = PopSelectParseUtil.getPopNodeSelectedValues(popNodeSelected);
//        String[] manualSelectNodeId = request.getParameterValues("manual_select_node_id");
//        Map<String, String[]> map = new HashMap<String, String[]>();
//        if(manualSelectNodeId != null){
//            for(String node : manualSelectNodeId){
//                String[] people = request.getParameterValues("manual_select_node_id" + node);
//                map.put(node, people);
//            }
//        }
        Map<String,String> condition = PopSelectParseUtil.getPopNodeConditionValues(popNodeCondition, map);

//        if(allNodes != null){
//        	Map<String,String> condition = new HashMap<String,String>();
//        	String[] nodes = StringUtils.split(allNodes,":");
//        	String result = "";
//        	if(nodes != null){
//        		for(String node:nodes){
//        			result = request.getParameter("condition"+node);
//        			result = "on".equals(result)?"false":"true";    //传递给后台的isDelete属性，所以要取反
//        			condition.put(node, result);
//        			if("true".equals(result)){
//        				map.remove(node);
//        			}
//        		}
//        		flowData.setCondition(condition);
//        	}
//        }
        flowData.setCondition(condition);
        flowData.setAddition(map);

        //更新自定义节点的引用
        /*String[] policys = request.getParameterValues("policys");
        String[] itemNames = request.getParameterValues("itemNames");
        ColHelper.setPolicy(policys, itemNames, flowData);*/

        boolean isNew = colSummary.isNew();
        colSummary.setIdIfNew();
        colSummary.setOrgAccountId(user.getLoginAccount());
        colSummary.setOrgDepartmentId(user.getDepartmentId());

        //删除原有附件
        if (!isNew) {
        	//colQuoteformRecordManger.delAll(colSummary.getId()) ;
            this.attachmentManager.deleteByReference(colSummary.getId());
        }
        if("FORM".equals(body.getBodyType())){
        	//关联无流程表单
            colQuoteformRecordManger.create(request, colSummary.getId(), colSummary.getFormAppId(), mId);
        }

        //从精灵发送时要删除附件
        String referenceIdStr = request.getParameter("referenceId");
        if(referenceIdStr!=null&&StringUtils.isNotBlank(referenceIdStr)){
        	this.attachmentManager.deleteByReference(Long.parseLong(referenceIdStr));
        }
        try{
	        //保存附件
	        String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, colSummary.getId(), colSummary.getId(), request);
	        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
	        	colSummary.setHasAttachments(true);
	        }
        }catch(Exception e){
        	log.error("保存附件时出现错误！", e);
        }

        //预归档
        if(request.getParameter("archiveId")!=null && !"".equalsIgnoreCase(request.getParameter("archiveId"))){
        	Long archiveId = Long.parseLong(request.getParameter("archiveId"));
        	colSummary.setArchiveId(archiveId);
        }

        //保存督办
        this.saveColSupervise(request, response, colSummary, isNew,Constant.superviseState.supervising.ordinal(),true);

        // 通知全文检索不入库
        DateSharedWithWorkflowEngineThreadLocal.setNoIndex();

        List<NewflowSetting> newflowList = null;
        //NF 如果是表单，并设置了新流程，则标记ColSummary为主流程.
        if("FORM".equals(body.getBodyType())){
            newflowList = newflowManager.getNewflowSettingList(colSummary.getTempleteId());
            if(newflowList != null && !newflowList.isEmpty()){
                colSummary.setNewflowType(Constant.NewflowType.main.ordinal());
            }
        }
        String trackMode =request.getParameter("isTrack");
        String trackMembers = request.getParameter("trackMembers");
        String trackRange = request.getParameter("trackRange");
        boolean track = "1".equals(trackMode) ? true : false;
        //不跟踪 或者 全部跟踪的时候不向部门跟踪表中添加数据，所以将下面这个参数串设置为空。
        if(!track || "1".equals(trackRange)) {
        	trackMembers = "";
        }
        int result= colManagerFacade.runCaseFacade(flowData, colSummary, body,
        		senderOninion, sendType, options, isNew, user.getId(),
        		track,trackMembers,newflowList,user,formInfo,mId);
        if(result==-1){
        	PrintWriter out = response.getWriter();
            out.println("<script>");
            String message = Constant.getString4CurrentUser("nodePolicy.not.existence");
            out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(message) + "\"); ");
            out.println("history.back();");
            out.println("</script>");
            return null;
        }
        //如果是精灵则直接关闭窗口，否则按常规返回页面
        if(closeWindowFromGenius(request, response)){
        	return null;
        }

        AccessControlBean.getInstance().addAccessControl(
        		ApplicationCategoryEnum.collaboration, String.valueOf(colSummary.getId()), user.getId());
        return super.redirectModelAndView("/collaboration.do?method=collaborationFrame&from=Sent");
    }

    /**
     * 待发事项,发起协同
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView sendImmediate(HttpServletRequest request,
            							HttpServletResponse response) throws Exception {
		ModelAndView mv = super.redirectModelAndView("/collaboration.do?method=collaborationFrame&from=WaitSend");
		//json格式的字符串:选人
    	String popNodeSelected = request.getParameter("popNodeSelected");
    	//json格式的字符串:条件分支
    	String popNodeCondition = request.getParameter("popNodeCondition");
    	//json格式的字符串:新流程信息
    	String popNodeNewFlow = request.getParameter("popNodeNewFlow");
    	if(log.isInfoEnabled()){
    		log.info("fromwaitSend:popNodeSelected:="+popNodeSelected);
        	log.info("fromwaitSend:popNodeCondition:="+popNodeCondition);
        	log.info("fromwaitSend:popNodeNewFlow:="+popNodeNewFlow);
    	}
		String[] _summaryIds = request.getParameterValues("id");
		String[] _affairIds = request.getParameterValues("affairId");
		if (_summaryIds == null || _affairIds == null) {
			return mv;
		}

		StringBuffer message = new StringBuffer();
		boolean sentFlag = false;
		User user = CurrentUser.get();
		for (int i = 0; i < _summaryIds.length; i++) {
			Long summaryId = new Long(_summaryIds[i]);

			ColSummary colSummary = colManager.getColSummaryById(summaryId, false);
			 //来自流程模版
			if(colSummary.getTempleteId() != null){
	    		Long tId = colSummary.getTempleteId();
	    		String outMsg = templeteManager.checkTemplete(tId, user.getId());
	    		if(Strings.isNotBlank(outMsg)){
		    		PrintWriter pw = response.getWriter();
		    		pw.println("<script>");
		    		pw.println("alert(\""+outMsg+"\");");
		    		pw.println("history.back()");
		    		pw.println("</script>");
		    		return null;
	    		}
	    		colSummary.setTempleteId(tId);
		    }

			String processId = colSummary.getProcessId();

			//没有流程
			if (processId == null || "".equals(processId.trim())) {
				message.append(colSummary.getSubject()).append("\n");
				continue;
			}

			this.colManager.clearSummaryOCA(summaryId, false);
			processLogManager.deleteLog(Long.parseLong(colSummary.getProcessId()));
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

			colSummary.setCreateDate(now);
			colSummary.setStartDate(now);
			colSummary.setOrgAccountId(CurrentUser.get().getLoginAccount());
			colSummary.setOrgDepartmentId(CurrentUser.get().getDepartmentId());
			colSummary.setState(Constant.flowState.run.ordinal());

			FlowData flowData = ColHelper.getRunningProcessPeople(processId);
            //没有流程
			if (flowData == null) {
				message.append(colSummary.getSubject()).append("\n");
				continue;
			}
			Map<String, String[]> map = PopSelectParseUtil.getPopNodeSelectedValues(popNodeSelected);
//
//			String[] manualSelectNodeId = request.getParameterValues("manual_select_node_id");
//			Map<String, String[]> map = new HashMap<String, String[]>();
//			if(manualSelectNodeId != null){
//				for(String node : manualSelectNodeId){
//					String[] people = request.getParameterValues("manual_select_node_id" + node);
//
//					map.put(node, people);
//				}
//			}
			Map<String,String> condition = PopSelectParseUtil.getPopNodeConditionValues(popNodeCondition, map);
			flowData.setCondition(condition);
//			String allNodes = request.getParameter("allNodes");
//	        if(allNodes != null){
//	        	Map<String,String> condition = new HashMap<String,String>();
//	        	String[] nodes = StringUtils.split(allNodes,":");
//	        	String result = "";
//	        	if(nodes != null){
//	        		for(String node:nodes){
//	        			result = request.getParameter("condition"+node);
//	        			result = "on".equals(result)?"false":"true";    //传递给后台的isDelete属性，所以要取反
//	        			condition.put(node, result);
//	        			if("true".equals(result))
//	        				map.remove(node);
//	        		}
//	        		flowData.setCondition(condition);
//	        	}
//	        }

			flowData.setAddition(map);

			//更新表单状态
			ColBody body = colSummary.getFirstBody();

			if(body != null && "FORM".equals(body.getBodyType())){
	        	String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
	        	String masterId = body.getContent();
	        	String operationType = "update";
	        	try{
	        		String state = "1";
                	//如果来自关联表单，则不更改表单状态
                    if(colSummary.getParentformSummaryId() !=null)
                    	state = "";
	        	FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, null,"submit",state,operationType,"send",Constant.FormVouch.vouchDefault.getKey());
//	        	  处理表单状态等信息存储
                formDaoManager.UpdateDataState(user.getId(), colSummary.getId(), "1", colSummary.getFormRecordId(), "submit", "send");
	        	}
	        	catch(DataDefineException e1){
	        	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
	        		  PrintWriter out = response.getWriter();
		              	out.println("<script>");
		                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
		              	out.println("history.back();");
		              	out.println("</script>");
		              	return null;
	        	  }
	      		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
	      			  throw new RuntimeException(e1.getMessage());
	      		  else{
	      			  log.error("保存表单数据时发生错误", e1);
	      			  //throw new RuntimeException("不能保存");
	      			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	      		  }

	        	  }catch(Exception e){
	      		  log.error("保存表单数据时发生错误", e);
	      		  //throw new RuntimeException("不能保存");
	      		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	      	      }
	        	Long key = Long.valueOf(Thread.currentThread().getId());
	        	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
	        		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
	        		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
	        	}
	        }
			sentFlag= colManagerFacade.runCaseImmediateFacade(colSummary, flowData, _affairIds[i], body, user, processId);
		}
		//跳转到已发事项
		if (sentFlag) {
			mv = super.redirectModelAndView("/collaboration.do?method=collaborationFrame&from=Sent");
		}

		if (message.length() > 0) {
			WebUtil.saveAlert("alert.sendImmediate.nowf");
		}
		return mv;
	}


    /**
     * 保存待发
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView save(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

    	//##当网络传输不好的时候如果有数据丢失，增加防护，数据不会被保存并提示用户 start
    	String allNodes = request.getParameter("allNodes");
    	String templeteId = request.getParameter("templeteId");
    	String branchNodes = request.getParameter("branchNodes");
    	//从request对象取到选人信息
        FlowData flowData = FlowData.flowdataFromRequest();
    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');");
    		out.println("history.back();</script>");
    		return null;
    	}
    	//##当网络传输不好的时候如果有数据丢失，增加防护，数据不会被保存并提示用户 end
    	User user = CurrentUser.get();
        ColSummary colSummary = new ColSummary();
        bind(request, colSummary);
        colSummary.setOrgAccountId(user.getLoginAccount());
        
        String secretLevel = request.getParameter("secretLevel");
        colSummary.setSecretLevel(Integer.parseInt(secretLevel));
        secretLevel = null;
        
	     //来自流程模版
        //String templeteId = request.getParameter("templeteId");
        String temformParentId = request.getParameter("temformParentId");
	    if(templeteId != null && !"".equals(templeteId)){
    		Long tId = Long.parseLong(templeteId);
    		if(!templeteManager.checkTempleteIsExist(tId)){
    			String outMsg = Constant.getString("templete.notExist");
	    		PrintWriter pw = response.getWriter();
	    		pw.println("<script>");
	    		pw.println("alert(\""+outMsg+"\");");
	    		pw.println("history.back()");
	    		pw.println("</script>");
	    		return null;
    		}
    		colSummary.setTempleteId(tId);
    	 }
        String note = request.getParameter("note");//发起人附言
        ColOpinion senderOninion = new ColOpinion();
        senderOninion.setContent(note);
        senderOninion.setIdIfNew();
        senderOninion.affairIsTrack = request.getParameter("isTrack") != null;

        ColBody body = new ColBody();
        bind(request, body);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
        }
        body.setIdIfNew();

        //得到选人界面返回的信�?
        if(temformParentId != null && !"".equals(temformParentId)){
        	colSummary.setTempleteId(Long.parseLong(temformParentId));
        }
        long mId = 0;
        if("FORM".equals(body.getBodyType())){
//          如果是表单个人模板则取原模板的id

        	String formData = request.getParameter("formData");
        	String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
        	String masterId = request.getParameter("masterId");
        	String pagefrom = request.getParameter("pagefrom");
        	masterId = "".equals(masterId)?null:masterId;
        	String operationType;
        	//如果是来自关联表单的
       		if("quoteFrom".equals(request.getParameter("quoteFromsign")) && masterId !=null){
   		   	try{
   		   	    String parentformSummaryId = request.getParameter("parentformSummaryId");
			    colSummary.setParentformSummaryId(Long.parseLong(parentformSummaryId));
    			FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","","update","quoteFrom",Constant.FormVouch.vouchDefault.getKey());
    			mId =  Long.parseLong(masterId);
            }catch(DataDefineException e1){
                	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
                		  PrintWriter out = response.getWriter();
        	              	out.println("<script>");
        	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
        	              	out.println("history.back();");
        	              	out.println("</script>");
        	              	return null;
                	  }
              		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
              			  throw new RuntimeException(e1.getMessage());
              		  else{
              			  log.error("保存表单数据时发生错误", e1);
              			  //throw new RuntimeException("不能保存");
              			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              		  }

             }catch(Exception e){
              		  log.error("保存表单数据时发生错误", e);
              		  //throw new RuntimeException("不能保存");
              		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
             }
    		}else{
    	      	if(masterId == null){
            		operationType = null;
               	}else{
               		if("WaitSend".equals(pagefrom)) {
               	        operationType = "update";
               		}

               		if(request.getParameter("draft").equals("true")){//表单如果先存为草稿就先删除后增加
        	        		operationType = "delete";
        	        		try{
        	        		FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","0",operationType,"saveDraft",Constant.FormVouch.vouchDefault.getKey());
        	            	}
        	            	catch(DataDefineException e1){
        	            	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
        	            		  PrintWriter out = response.getWriter();
        	    	              	out.println("<script>");
        	    	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
        	    	              	out.println("history.back();");
        	    	              	out.println("</script>");
        	    	              	return null;
        	            	  }
        	          		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
        	          			  throw new RuntimeException(e1.getMessage());
        	          		  else{
        	          			  log.error("保存表单数据时发生错误", e1);
        	          			  //throw new RuntimeException("不能保存");
        	          			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
        	          		  }

        	            	  }catch(Exception e){
        	          		  log.error("保存表单数据时发生错误", e);
        	          		  //throw new RuntimeException("不能保存");
        	          		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
        	          	      }
        	        		operationType = null;
        	        		masterId = null;
        	        		formData = formData.replaceAll("recordid=\"\\d+\"|recordid=\"-\\d+\"", "state=\"add\"");
                		}else{//不是直接更新
                			operationType = "update";
                		}
            	}
            	try{
            	mId = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","0",operationType,"saveDraft",Constant.FormVouch.vouchDefault.getKey());
            	}
            	catch(DataDefineException e1){
            	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
            		  PrintWriter out = response.getWriter();
    	              	out.println("<script>");
    	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
    	              	out.println("history.back();");
    	              	out.println("</script>");
    	              	return null;
            	  }
          		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
          			  throw new RuntimeException(e1.getMessage());
          		  else{
          			  log.error("保存表单数据时发生错误", e1);
          			  //throw new RuntimeException("不能保存");
          			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
          		  }

            	  }catch(Exception e){
          		  log.error("保存表单数据时发生错误", e);
          		  //throw new RuntimeException("不能保存");
          		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
          	      }
    		}
        	Long key = Long.valueOf(Thread.currentThread().getId());
        	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
        		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
        		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
        	}
        	colSummary.setFormAppId(Long.valueOf(formInfo[0]));//保存fromAppid
        	colSummary.setFormId(Long.valueOf(formInfo[1]));//保存表单id
        	colSummary.setFormRecordId(Long.valueOf(mId));//保存表单主id
       	    body.setContent(String.valueOf(mId));
        }

        boolean isNew = colSummary.isNew();
        colSummary.setIdIfNew();

        if("FORM".equals(body.getBodyType())){
        	this.colQuoteformRecordManger.create(request, colSummary.getId(), colSummary.getFormAppId(), mId) ;
        }

        //删除原有附件
        if (!isNew) {
            this.attachmentManager.deleteByReference(colSummary.getId());
            //this.colQuoteformRecordManger.delAll(colSummary.getId()) ;
        }
        //从精灵发送时要删除附件
        String referenceIdStr = request.getParameter("referenceId");
        if(referenceIdStr!=null&&StringUtils.isNotBlank(referenceIdStr)){
        	this.attachmentManager.deleteByReference(Long.parseLong(referenceIdStr));
        }

        //保存附件
        String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, colSummary.getId(), colSummary.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	colSummary.setHasAttachments(true);
        }

        this.saveColSupervise(request, response, colSummary, isNew,Constant.superviseState.waitSupervise.ordinal(),false);

        Long affairId = colManager.saveDraft(flowData, colSummary, body, senderOninion, isNew);
        //表单状态信息等存储
        if("FORM".equals(body.getBodyType()) && isNew == true){
 		   formDaoManager.saveDataState(colSummary, 0, -1);
 		   TriggerHelper.copyTriggerInfo(colSummary.getId(), colSummary.getFormAppId(), colSummary.getFormRecordId());
        }

        //跟踪
        String trackMode =request.getParameter("isTrack");
        boolean track = "1".equals(trackMode) ? true : false;
        String trackMembers = request.getParameter("trackMembers");
        String trackRange = request.getParameter("trackRange");
        //不跟踪 或者 全部跟踪的时候不向部门跟踪表中添加数据，所以将下面这个参数串设置为空。
        if(!track || "1".equals(trackRange)) trackMembers = "";
        colManager.setTrack(affairId, track, trackMembers);

        //如果是精灵则直接关闭窗口，否则按常规返回页面
        if(closeWindowFromGenius(request, response)){
        	return null;
        }
        return super.redirectModelAndView("/collaboration.do?method=collaborationFrame&from=WaitSend");
    }

    public ModelAndView saveDraft(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

    	//##当网络传输不好的时候如果有数据丢失，增加防护，数据不会被保存并提示用户 start
    	String allNodes = request.getParameter("allNodes");
    	String templeteId = request.getParameter("templeteId");
    	String branchNodes = request.getParameter("branchNodes");
    	//从request对象取到选人信息
        FlowData flowData = FlowData.flowdataFromRequest();

    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');");
    		out.println("history.back();</script>");
    		return null;
    	}
    	//##当网络传输不好的时候如果有数据丢失，增加防护，数据不会被保存并提示用户 end
    	User user = CurrentUser.get();
        ColSummary colSummary = new ColSummary();
        bind(request, colSummary);
        colSummary.setOrgAccountId(user.getLoginAccount());

        String note = request.getParameter("note");//发起人附言
        ColOpinion senderOninion = new ColOpinion();
        senderOninion.setContent(note);
        senderOninion.setIdIfNew();
        senderOninion.affairIsTrack = request.getParameter("isTrack") != null;

        ColBody body = new ColBody();
        bind(request, body);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
        }
        body.setIdIfNew();

        long mId = 0;
        String temformParentId = request.getParameter("temformParentId");
   	 	// 如果是表单个人模板则取原模板的id
   	 	if(temformParentId != null && !"".equals(temformParentId)){
   	 		colSummary.setTempleteId(Long.parseLong(temformParentId));
   	 	}
        if("FORM".equals(body.getBodyType())){
        	String formData = request.getParameter("formData");
        	String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
        	String masterId = request.getParameter("masterId");
        	masterId = "".equals(masterId)?null:masterId;
        	String pagefrom = request.getParameter("pagefrom");
        	String operationType;
//        	如果是来自关联表单的
       		if("quoteFrom".equals(request.getParameter("quoteFromsign")) && masterId !=null){
   		   	try{
   		   	    String parentformSummaryId = request.getParameter("parentformSummaryId");
		        colSummary.setParentformSummaryId(Long.parseLong(parentformSummaryId));
    			FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","","update","quoteFrom",Constant.FormVouch.vouchDefault.getKey());
    			mId =  Long.parseLong(masterId);
            }catch(DataDefineException e1){
                	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
                		  PrintWriter out = response.getWriter();
        	              	out.println("<script>");
        	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
        	              	out.println("history.back();");
        	              	out.println("</script>");
        	              	return null;
                	  }
              		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
              			  throw new RuntimeException(e1.getMessage());
              		  else{
              			  log.error("保存表单数据时发生错误", e1);
              			  //throw new RuntimeException("不能保存");
              			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              		  }

             }catch(Exception e){
              		  log.error("保存表单数据时发生错误", e);
              		  //throw new RuntimeException("不能保存");
              		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
             }
    		}else{
    			if(masterId == null){
            		operationType = null;
            		try{
            		  mId = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","0",operationType,"saveDraft",Constant.FormVouch.vouchDefault.getKey());
                	}
                	catch(DataDefineException e1){
                	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
                		  PrintWriter out = response.getWriter();
        	              	out.println("<script>");
        	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
        	              	out.println("history.back();");
        	              	out.println("</script>");
        	              	return null;
                	  }
              		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
              			  throw new RuntimeException(e1.getMessage());
              		  else{
              			  log.error("保存表单数据时发生错误", e1);
              			  //throw new RuntimeException("不能保存");
              			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              		  }

                	  }catch(Exception e){
              		  log.error("保存表单数据时发生错误", e);
              		  //throw new RuntimeException("不能保存");
              		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              	      }
            	}else{

            		if("WaitSend".equals(pagefrom)) {
            			operationType = "update";
            		}

        			if(request.getParameter("draft").equals("true")){
            			operationType = "delete";
            			try{
                		  FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","0",operationType,"saveDraft",Constant.FormVouch.vouchDefault.getKey());
                    	}
                    	catch(DataDefineException e1){
                    	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
                    		  PrintWriter out = response.getWriter();
            	              	out.println("<script>");
            	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
            	              	out.println("history.back();");
            	              	out.println("</script>");
            	              	return null;
                    	  }
                  		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
                  			  throw new RuntimeException(e1.getMessage());
                  		  else{
                  			  log.error("保存表单数据时发生错误", e1);
                  			  //throw new RuntimeException("不能保存");
                  			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
                  		  }

                    	  }catch(Exception e){
                  		  log.error("保存表单数据时发生错误", e);
                  		  //throw new RuntimeException("不能保存");
                  		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
                  	      }
                		operationType = null;
                		masterId = null;
                		//删除后去掉子记录的recordID 并且state 为add
                		formData = formData.replaceAll("recordid=\"\\d+\"|recordid=\"-\\d+\"", "state=\"add\"");
        			}else{//不是直接更新
            			operationType = "update";
            		}


            		try{
                        mId = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","0",operationType,"saveDraft",Constant.FormVouch.vouchDefault.getKey());
                 	}
                	catch(DataDefineException e1){
                	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
                		  PrintWriter out = response.getWriter();
        	              	out.println("<script>");
        	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
        	              	out.println("history.back();");
        	              	out.println("</script>");
        	              	return null;
                	  }
              		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
              			  throw new RuntimeException(e1.getMessage());
              		  else{
              			  log.error("保存表单数据时发生错误", e1);
              			  //throw new RuntimeException("不能保存");
              			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              		  }

                	  }catch(Exception e){
              		  log.error("保存表单数据时发生错误", e);
              		  //throw new RuntimeException("不能保存");
              		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
              	      }
            	}
    		}


        	//String operationType = masterId==null?null:"update";
        	//mId = FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formInfo[0], formInfo[1], formInfo[2], masterId, formData,"submit","0",operationType);
        	Long key = Long.valueOf(Thread.currentThread().getId());
        	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
        		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
        		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
        	}
        	colSummary.setFormAppId(Long.valueOf(formInfo[0]));//保存fromAppid
        	colSummary.setFormId(Long.valueOf(formInfo[1]));//保存表单id
        	colSummary.setFormRecordId(Long.valueOf(mId));//保存表单主id
       	    body.setContent(String.valueOf(mId));
        }

        boolean isNew = colSummary.isNew();
        colSummary.setIdIfNew();

        if("FORM".equals(body.getBodyType())){
        	this.colQuoteformRecordManger.create(request, colSummary.getId(), colSummary.getFormAppId(), mId) ;
        }

        //删除原有附件
        if (!isNew) {
            this.attachmentManager.deleteByReference(colSummary.getId());
        }

        //保存附件
        String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, colSummary.getId(), colSummary.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	colSummary.setHasAttachments(true);
        }

        this.saveColSupervise(request, response, colSummary, isNew,Constant.superviseState.waitSupervise.ordinal(),false);

        colManager.saveDraft(flowData, colSummary, body, senderOninion, isNew);
//      表单状态信息等存储
        if("FORM".equals(body.getBodyType()) && isNew == true){
  		   formDaoManager.saveDataState(colSummary, 0, -1);
  		   TriggerHelper.copyTriggerInfo(colSummary.getId(), colSummary.getFormAppId(), colSummary.getFormRecordId());
         }
        super.rendJavaScript(response, "parent.endSaveDraft('" + colSummary.getId() + "'" + ("FORM".equals(body.getBodyType())?",'"+mId+"'":"") + ")");
        return null;
    }

    /**
     * 获取一篇协同的全部属�?
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView summary(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "collaboration/collaborationSummary");
        User user = CurrentUser.get();
        String s_summaryId = request.getParameter("summaryId");
        String affairId = request.getParameter("affairId");
        long summaryId = Long.parseLong(s_summaryId);
        String openFrom=request.getParameter("openFrom");
        String type = request.getParameter("type");

        boolean isStoreFlag = false; //是否转储标记

        Affair affair = affairManager.getById(Long.parseLong(affairId));
        //看一下是否被转储了
        if(affair == null){
        	affair = this.hisAffairManager.getById(Long.parseLong(affairId));
    		isStoreFlag = (affair != null);
        }

        modelAndView.addObject("isStoreFlag", isStoreFlag);
        modelAndView.addObject("openFrom", openFrom);
        ColSummary summary = null;

    	if(isStoreFlag){ //转储数据
    		summary = hisColManager.getColSummaryById(affair.getObjectId(), true);
    	}
    	else{
    		summary = colManager.getColSummaryById(affair.getObjectId(), true);
    	}

        if(affair == null){
        	String msg = ColHelper.getErrorMsgByAffair(affair);
            throw new ColException(msg);
        }
        if( null== summary.getCaseId() && summary.getState()==0 ){//兼容处理下
        	Affair sendAffair= affairManager.getCollaborationSenderAffair(summary.getId());
        	if(sendAffair.getState()==StateEnum.col_sent.key()){//不是待发状态，看是否可以补数据
        		log.info("caseId为空，根据processId做兼容处理。");
        		CaseManager caseManager= (CaseManager)ApplicationContextHolder.getBean("caseManager");
        		Long caseId= caseManager.moveHistoryCaseToRun(summary.getProcessId());
        		summary.setCaseId(caseId);
        		colManager.save(summary);
        	}
        }
        modelAndView.addObject("forwardMemberNames", ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
        modelAndView.addObject("summary", summary);
        modelAndView.addObject("isTrack", affair.getIsTrack());
        modelAndView.addObject("finished", summary.isFinshed());
        modelAndView.addObject("type", type);
        modelAndView.addObject("bodytype", summary.getBodyType());

        //===========流程图相关参数=================
        BPMProcess process = null;
        if(isStoreFlag){
        	process = ColHelper.getHisCaseProcess(summary.getProcessId());
        }
        else{
        	process = ColHelper.getCaseProcess(summary.getProcessId());
        }
        BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
        modelAndView.addObject("caseId", summary.getCaseId());
        modelAndView.addObject("processId", summary.getProcessId());
        modelAndView.addObject("isShowButton", false);
        if(activity != null){
    		modelAndView.addObject("currentNodeId", activity.getId());
    	}
        //催办按钮只有在已发事项中才能显示
        String from = request.getParameter("from");
        if(affair.getSubObjectId() == null
        	&& "Sent".equals(from)
        	&& user.getId() == affair.getSenderId()){
        	modelAndView.addObject("showHastenButton", "true");
        	if(StateEnum.col_sent.key() == affair.getState()){
            	modelAndView.addObject("showAuthorityButton", "true");
        	}
    	}
        if("FORM".equals(summary.getBodyType())) {
        	//NF 查找当前表单协同是否关联有新流程
			//当前流程为主流程，当前节点为触发节点，返回可查看的子流程的SummaryIds
            Integer newflowType = summary.getNewflowType();
			if(newflowType != null && newflowType.intValue() == Constant.NewflowType.main.ordinal()){
			    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.main.ordinal());
			    if(runningList != null && !runningList.isEmpty()){
			        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
			        for (NewflowRunning running : runningList) {
			            //1、当前协同为主流程（running为子流程），且子流程running可被主流程查看
			            if(running.getMainSummaryId().equals(summaryId) && running.getIsCanViewByMainFlow()){
			                relateFlowList.add(running);
			                continue;
			            }
			        }
			        modelAndView.addObject("relateFlowList", relateFlowList);
			        modelAndView.addObject("newflowTempleteId", summary.getTempleteId());
			    }
			    modelAndView.addObject("newflowType", Constant.NewflowType.main);

			    /*
			     * 同时判断当前节点的前一节点是否是新流程触发节点，
			     * 如果是且{其触发的新流程有设置了‘新流程结束后主流程才可继续’选项，但未结束的}，提示子流程未结束，不能处理。
			     */
                if("Pending".equals(from)){
                    try{
                        List<String> hasNewflowNodeIds = ColHelper.checkPrevNodeHasNewflow(activity);
                        if(hasNewflowNodeIds != null && !hasNewflowNodeIds.isEmpty()){
                            String noFinishNewflowTitle = newflowManager.checkHasNoFinishNewflow(summaryId, hasNewflowNodeIds);
                            if(Strings.isNotBlank(noFinishNewflowTitle)){
                            	modelAndView.addObject("noFinishNewflow", noFinishNewflowTitle);
                            }
                        }
                    }
                    catch(Exception e){
                        log.error("判断前一节点是否是触发了新流程的节点时异常：", e);
                    }
                }
			}
			else if(newflowType != null && newflowType.intValue() == Constant.NewflowType.child.ordinal()){
			    //当前流程为子流程，返回可查看的主流程的SummaryId
			    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.child.ordinal());
			    if(runningList != null && !runningList.isEmpty()){
			        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
			        NewflowRunning running = runningList.get(0);
			        //2、当前协同为子流程（running为主流程），且running可查看子流程
			        if(running.getSummaryId().equals(summaryId) && running.getIsCanViewMainFlow()){
			            relateFlowList.add(running);
			        }
				    //无流程表单触发的新流程  没有父流程
		            if(running.getMainFormType()!=1){
				        modelAndView.addObject("relateFlowList", new ArrayList());
		            }else{
				        modelAndView.addObject("relateFlowList", relateFlowList);
		            }
			        modelAndView.addObject("newflowTempleteId", summary.getTempleteId());
			    }
			    modelAndView.addObject("isNewflow", true);
			    if("Pending".equals(from))
			    	modelAndView.addObject("newflowCanNotBack",ColHelper.isSecondNode(summary.getCaseId(), affair.getSubObjectId(), activity));
			    modelAndView.addObject("newflowType", Constant.NewflowType.child);
			}
        }

        if(summary.getTempleteId() != null){
        	Templete templete = templeteManager.get(summary.getTempleteId());
        	if(templete != null){
        		modelAndView.addObject("workflowRule", templete.getWorkflowRule());
        	}
        	List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(summary.getTempleteId(),ApplicationCategoryEnum.collaboration.ordinal());
        	if(branchs != null){
        		//显示分支条件使用流程中保留的，如果为空使用模板中的
        		branchs = ColHelper.updateBranchByProcess(summary.getProcessId(),branchs);
        		modelAndView.addObject("branchs", branchs);
        	}
        }
       //===========流程图相关参数END=================

        List<Attachment> allAttachments = new ArrayList<Attachment>() ;
        if("FORM".equals(summary.getBodyType())){
        	List<Attachment> list = getMainRunAtt(summary) ;
        	if(list != null){
        		allAttachments.addAll(list) ;
        	}
        	if(summary.getParentformSummaryId() != null){
        		list = getMainRunAtt(summary.getParentformSummaryId()) ;
            	allAttachments.addAll(list) ;
        	}
        }
        List<Attachment> temp = attachmentManager.getByReference(summaryId);
        if(temp != null) {
        	allAttachments.addAll(temp);
        }

    	modelAndView.addObject("attachments", allAttachments);

        //取打印权限
    	int affairState = affair.getState().intValue();
        String nodePermissionPolicy = "Collaboration";



        //如果数据库中取不到节点权限，就从XML中取
        if(Strings.isBlank(affair.getNodePolicy())){
	        //获取当前事项对应的节点
	        BPMSeeyonPolicy policy = null;
	        if(process != null){
		        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
		        	policy = process.getStart().getSeeyonPolicy();
		        }else{
		        	if(activity != null){
		        		policy = activity.getSeeyonPolicy();
		        	}
		        }
		        if(policy != null){
		            nodePermissionPolicy = policy.getId();
		        }

	        }
        }
        else{
        	nodePermissionPolicy = affair.getNodePolicy();
        }
        //根据affairId得到权限的处理ID
        String lenPotents=request.getParameter("lenPotent");
        //默认office正文可以打印和保存
        boolean officecanPrint = false;
        String officecanSaveLocal="true";

        Map<String,Object> map = getSaveToLocalOrPrintPolicy(summary,nodePermissionPolicy,lenPotents,affair);
        if (map != null && map.size() != 0) {
        	officecanPrint = Boolean.parseBoolean(map.get("officecanPrint").toString());
            officecanSaveLocal = map.get("officecanSaveLocal").toString();
        }
        modelAndView.addObject("officecanPrint", officecanPrint);
		modelAndView.addObject("officecanSaveLocal", officecanSaveLocal);

        // 督办开始
    	Set<String> idSets = new HashSet<String>();
		StringBuffer supervisorId = new StringBuffer(); // supervisorId : all the ids of supervise detail
		StringBuffer tempIds = new StringBuffer(); // tempIds : all the ids of superviseTemplate
		ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(), summaryId);
		if(detail != null) {
			Set<ColSupervisor> supervisors = detail.getColSupervisors();
			Set<Long> userSuper = new HashSet<Long>();
			for(ColSupervisor supervisor:supervisors){
				userSuper.add(supervisor.getSupervisorId().longValue());
				idSets.add(supervisor.getSupervisorId().toString());
			}
			if(null!=summary && null!=summary.getTempleteId()){
				ColSuperviseDetail tempDetail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),summary.getTempleteId());
				if(null!=tempDetail){
					Set<ColSupervisor> tempVisors = tempDetail.getColSupervisors();
					for(ColSupervisor ts : tempVisors){
						if(userSuper.contains(ts.getSupervisorId())){
							idSets.add(ts.getSupervisorId().toString());
							tempIds.append(ts.getSupervisorId().toString() + ",");
						}
					}

					List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(summary.getTempleteId());
					V3xOrgRole orgRole = null;
					V3xOrgMember starter = orgManager.getMemberById(summary.getStartMemberId());
					if(null!=starter){
					for (SuperviseTemplateRole role : roleList) {
						if (null == role.getRole() || "".equals(role.getRole())) {
							continue;
						}
						if (role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase())) {
							if(userSuper.contains(starter.getId())){
								idSets.add(String.valueOf(starter.getId()));
								tempIds.append(starter.getId() + ",");
							}
						}
						if (role.getRole().toLowerCase().equals(
										V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())) {
							orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, starter.getOrgAccountId());
							if (null != orgRole) {
								List<V3xOrgDepartment> depList = orgManager
										.getDepartmentsByUser(starter.getId());
								for (V3xOrgDepartment dep : depList) {
									List<V3xOrgMember> managerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
									for (V3xOrgMember mem : managerList) {
										if(userSuper.contains(mem.getId())){
											idSets.add(mem.getId().toString());
											tempIds.append(mem.getId() + ",");
										}
									}
								}
							}
						}
						if (role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER.toLowerCase())) {
							orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER, starter.getOrgAccountId());
							if (null != orgRole) {
								List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(starter.getId());
								for (V3xOrgDepartment dep : depList) {
									List<V3xOrgMember> superManagerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
									for (V3xOrgMember mem : superManagerList) {
										if(userSuper.contains(mem.getId())){
											idSets.add(mem.getId().toString());
											tempIds.append(mem.getId() + ",");
										}
									}
								}
							}
						}
					}

				}
				}
			}

			for(String id : idSets){
				supervisorId.append(id+",");
			}
			if(supervisorId.length()>0){
				modelAndView.addObject("supervisorId", supervisorId.substring(0, supervisorId.length()-1));
			}
			modelAndView.addObject("superviseId", detail.getId());
			modelAndView.addObject("supervisors", detail.getSupervisors());
			modelAndView.addObject("superviseTitle", detail.getTitle());
			modelAndView.addObject("awakeDate", Datetimes.format(detail.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
			modelAndView.addObject("superviseTitle", detail.getTitle());
			modelAndView.addObject("count", idSets.size());
			if(idSets.contains(String.valueOf(user.getId()))){
				modelAndView.addObject("isSupervis", true);
			}
			modelAndView.addObject("openModal", request.getParameter("openModal"));
			modelAndView.addObject("bean", detail);
			if(tempIds.length()>0){
				String unCancelledVisor = tempIds.substring(0, tempIds.length()-1);
				modelAndView.addObject("unCancelledVisor", unCancelledVisor);
				modelAndView.addObject("sVisorsFromTemplate", "true");//公文调用的督办模板是否设置了督办人
			}
		}
	 	//督办相关.
        ColSuperviseDetail cdetail = colSuperviseManager.getCurrentUserSupervise(Constant.superviseType.summary.ordinal(),summary.getId(),user.getId());
        if(cdetail!=null && summary.getCaseId() != null){
        	modelAndView.addObject("isSupervis", true);
        	modelAndView.addObject("bean", cdetail);
        	modelAndView.addObject("openModal", request.getParameter("openModal"));
        }
        return modelAndView;
    }

    /**
     * 为表单查询获取一篇协同的全部属�?
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView summaryForFormQuery(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
			ModelAndView modelAndView = new ModelAndView("collaboration/collaborationSummaryForForm");
			String formNames = request.getParameter("formNames");
			User user = CurrentUser.get();
			String s_summaryId = request.getParameter("summaryId");
			long summaryId= Long.parseLong(s_summaryId);
	        String affairId = request.getParameter("affairId");

	        boolean isStoreFlag = false; //是否转储标记

	        Affair affair = affairManager.getById(Long.parseLong(affairId));
	        if(affair == null){
	        	affair = hisAffairManager.getById(Long.parseLong(affairId));
	        	isStoreFlag = (affair != null);
	        }
	        if(affair == null){
	        	String msg = ColHelper.getErrorMsgByAffair(affair);
	            throw new ColException(msg);
	        }

			request.getParameter("from");
			String type = request.getParameter("type");

			String formId = request.getParameter("formId");
			String operationId = request.getParameter("operationId");

			ColSummary summary = null;
			if(isStoreFlag){
				summary = hisColManager.getColSummaryById(summaryId, false);
			}
			else{
				summary = colManager.getColSummaryById(summaryId, false);
			}

			modelAndView.addObject("forwardMemberNames", ColHelper.getForwardMemberNames(summary.getForwardMember(), orgManager));
			modelAndView.addObject("summary", summary);
			modelAndView.addObject("isTrack", affair.getIsTrack());
	        modelAndView.addObject("finished", summary.isFinshed());
			modelAndView.addObject("type", type);
			modelAndView.addObject("formId", formId);
			modelAndView.addObject("operationId", operationId);
			modelAndView.addObject("s_summaryId", s_summaryId);
			modelAndView.addObject("bodytype", summary.getBodyType());

			//===========流程图相关参数=================
	        BPMProcess process = isStoreFlag ? ColHelper.getHisCaseProcess(summary.getProcessId()) : ColHelper.getCaseProcess(summary.getProcessId());
	        BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
	        modelAndView.addObject("caseId", summary.getCaseId());
	        modelAndView.addObject("processId", summary.getProcessId());
	        modelAndView.addObject("isShowButton", false);
	        if(activity != null){
	    		modelAndView.addObject("currentNodeId", activity.getId());
	    	}
	        //催办按钮只有在已发事项中才能显示
	        String from = request.getParameter("from");
	        if(affair.getSubObjectId() == null
	        	&& "Sent".equals(from)
	        	&& user.getId() == affair.getSenderId()){
	        	modelAndView.addObject("showHastenButton", "true");
	    	}
	        if("FORM".equals(summary.getBodyType())) {
	        	//NF 查找当前表单协同是否关联有新流程
				//当前流程为主流程，当前节点为触发节点，返回可查看的子流程的SummaryIds
	            Integer newflowType = summary.getNewflowType();
				if(newflowType != null && newflowType.intValue() == Constant.NewflowType.main.ordinal()){
				    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.main.ordinal());
				    if(runningList != null && !runningList.isEmpty()){
				        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
				        for (NewflowRunning running : runningList) {
				            //1、当前协同为主流程（running为子流程），且子流程running可被主流程查看
				            if(running.getMainSummaryId().equals(summaryId) && running.getIsCanViewByMainFlow()){
				                relateFlowList.add(running);
				                continue;
				            }
				        }
				        modelAndView.addObject("relateFlowList", relateFlowList);
				        modelAndView.addObject("newflowTempleteId", summary.getTempleteId());
				    }
				    modelAndView.addObject("newflowType", Constant.NewflowType.main);

				    /*
				     * 同时判断当前节点的前一节点是否是新流程触发节点，
				     * 如果是且{其触发的新流程有设置了‘新流程结束后主流程才可继续’选项，但未结束的}，提示子流程未结束，不能处理。
				     */
	                if("Pending".equals(from)){
	                    try{
	                        List<String> hasNewflowNodeIds = ColHelper.checkPrevNodeHasNewflow(activity);
	                        if(hasNewflowNodeIds != null && !hasNewflowNodeIds.isEmpty()){
	                            String noFinishNewflowTitle = newflowManager.checkHasNoFinishNewflow(summaryId, hasNewflowNodeIds);
	                            if(Strings.isNotBlank(noFinishNewflowTitle)){
	                            	modelAndView.addObject("noFinishNewflow", noFinishNewflowTitle);
	                            }
	                        }
	                    }
	                    catch(Exception e){
	                        log.error("判断前一节点是否是触发了新流程的节点时异常：", e);
	                    }
	                }
				}
				else if(newflowType != null && newflowType.intValue() == Constant.NewflowType.child.ordinal()){
				    //当前流程为子流程，返回可查看的主流程的SummaryId
				    List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, summary.getTempleteId(), Constant.NewflowType.child.ordinal());
				    if(runningList != null && !runningList.isEmpty()){
				        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
				        NewflowRunning running = runningList.get(0);
				        //2、当前协同为子流程（running为主流程），且running可查看子流程
				        if(running.getSummaryId().equals(summaryId) && running.getIsCanViewMainFlow()){
				            relateFlowList.add(running);
				        }
				        modelAndView.addObject("relateFlowList", relateFlowList);
				        modelAndView.addObject("newflowTempleteId", summary.getTempleteId());
				    }
				    modelAndView.addObject("isNewflow", true);
				    if("Pending".equals(from))
				    	modelAndView.addObject("newflowCanNotBack",ColHelper.isSecondNode(summary.getCaseId(), affair.getSubObjectId(), activity));
				    modelAndView.addObject("newflowType", Constant.NewflowType.child);
				}
	        }

	        if(summary.getTempleteId() != null){
	        	Templete templete = templeteManager.get(summary.getTempleteId());
	        	if(templete != null){
	        		modelAndView.addObject("workflowRule", templete.getWorkflowRule());
	        	}
	        	List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(summary.getTempleteId(),ApplicationCategoryEnum.collaboration.ordinal());
	        	if(branchs != null){
	        		//显示分支条件使用流程中保留的，如果为空使用模板中的
	        		branchs = ColHelper.updateBranchByProcess(summary.getProcessId(),branchs);
	        		modelAndView.addObject("branchs", branchs);
	        	}
	        }
	       //===========流程图相关参数END=================
			List<Attachment> list = new ArrayList<Attachment>() ;
			if(this.getMainRunAtt(summary) != null){
				list.addAll(this.getMainRunAtt(summary)) ;
			}
			if(attachmentManager.getByReference(summaryId)!= null){
				list.addAll(attachmentManager.getByReference(summaryId)) ;
			}

			modelAndView.addObject("attachments", list);
			modelAndView.addObject("formNames", formNames);
			//关联授权
        	if(ApplicationCategoryEnum.doc.name().equals(type) && StateEnum.col_sent.key() == affair.getState() && affair.getSenderId() == user.getId()){
            	modelAndView.addObject("showAuthorityButton", "true");
        	}
	        //取打印权限
	    	int affairState = affair.getState().intValue();
	        String nodePermissionPolicy = "Collaboration";
	        //如果数据库中取不到节点权限，就从XML中取
	        if(Strings.isBlank(affair.getNodePolicy())){
		        //获取当前事项对应的节点
		        BPMSeeyonPolicy policy = null;
		        if(process != null){
			        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
			        	policy = process.getStart().getSeeyonPolicy();
			        }else{
			        	if(activity != null){
			        		policy = activity.getSeeyonPolicy();
			        	}
			        }
			        if(policy != null){
			            nodePermissionPolicy = policy.getId();
			        }

		        }
	        }
	        else{
	        	nodePermissionPolicy = affair.getNodePolicy();
	        }
	        boolean officecanPrint = false;
	        String lenPotent="0";
	        String lenPotents=request.getParameter("lenPotent");
	        if(lenPotents!=null && !"".equals(lenPotents)){lenPotent=lenPotents.substring(0,1);}
	        try{
	    		Long accountId= ColHelper.getFlowPermAccountId(CurrentUser.get().getLoginAccount(), summary, templeteManager);
	    		FlowPerm fp=flowPermManager.getFlowPerm(MetadataNameEnum.col_flow_perm_policy.toString(),nodePermissionPolicy, accountId);
	    		long permId=fp.getFlowPermId();
	    		String baseAction=fp.getBasicOperation();
	    		//是否有打印权限，根据v3x_affair表中的state字段来判断
	    		//节点对文档中心是否有打印权限
	    		boolean pigCanPrint = (Strings.isNotBlank(lenPotents) && lenPotents.charAt(1) == '1');
	    		if(Strings.isBlank(lenPotents)){
	    			if(StateEnum.col_waitSend.getKey() == affair.getState()
	    				|| StateEnum.col_sent.getKey() == affair.getState()
	    				|| baseAction.indexOf("Print")>0){
	    				officecanPrint = true ;
	    			}
	    		}else{
	    			officecanPrint = "0".equals(lenPotents.substring(2,3))?false:true;
	    		}
	        }catch(Exception e){
	    		log.error(e);
	    	}
	        modelAndView.addObject("officecanPrint", officecanPrint);

	        // 督办开始
	    	Set<String> idSets = new HashSet<String>();
			StringBuffer supervisorId = new StringBuffer(); // supervisorId : all the ids of supervise detail
			StringBuffer tempIds = new StringBuffer(); // tempIds : all the ids of superviseTemplate
			ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(), summaryId);
			if(detail != null) {
				Set<ColSupervisor> supervisors = detail.getColSupervisors();
				Set<Long> userSuper = new HashSet<Long>();
				for(ColSupervisor supervisor:supervisors){
					userSuper.add(supervisor.getSupervisorId().longValue());
					idSets.add(supervisor.getSupervisorId().toString());
				}
				if(null!=summary && null!=summary.getTempleteId()){
					ColSuperviseDetail tempDetail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),summary.getTempleteId());
					if(null!=tempDetail){
						Set<ColSupervisor> tempVisors = tempDetail.getColSupervisors();
						for(ColSupervisor ts : tempVisors){
							if(userSuper.contains(ts.getSupervisorId())){
								idSets.add(ts.getSupervisorId().toString());
								tempIds.append(ts.getSupervisorId().toString() + ",");
							}
						}

						List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(summary.getTempleteId());
						V3xOrgRole orgRole = null;
						V3xOrgMember starter = orgManager.getMemberById(summary.getStartMemberId());
						if(null!=starter){
						for (SuperviseTemplateRole role : roleList) {
							if (null == role.getRole() || "".equals(role.getRole())) {
								continue;
							}
							if (role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase())) {
								if(userSuper.contains(starter.getId())){
									idSets.add(String.valueOf(starter.getId()));
									tempIds.append(starter.getId() + ",");
								}
							}
							if (role.getRole().toLowerCase().equals(
											V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())) {
								orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, starter.getOrgAccountId());
								if (null != orgRole) {
									List<V3xOrgDepartment> depList = orgManager
											.getDepartmentsByUser(starter.getId());
									for (V3xOrgDepartment dep : depList) {
										List<V3xOrgMember> managerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
										for (V3xOrgMember mem : managerList) {
											if(userSuper.contains(mem.getId())){
												idSets.add(mem.getId().toString());
												tempIds.append(mem.getId() + ",");
											}
										}
									}
								}
							}
							if (role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER.toLowerCase())) {
								orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER, starter.getOrgAccountId());
								if (null != orgRole) {
									List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(starter.getId());
									for (V3xOrgDepartment dep : depList) {
										List<V3xOrgMember> superManagerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
										for (V3xOrgMember mem : superManagerList) {
											if(userSuper.contains(mem.getId())){
												idSets.add(mem.getId().toString());
												tempIds.append(mem.getId() + ",");
											}
										}
									}
								}
							}
						}

					}
					}
				}

				for(String id : idSets){
					supervisorId.append(id+",");
				}
				if(supervisorId.length()>0){
					modelAndView.addObject("supervisorId", supervisorId.substring(0, supervisorId.length()-1));
				}
				modelAndView.addObject("superviseId", detail.getId());
				modelAndView.addObject("supervisors", detail.getSupervisors());
				modelAndView.addObject("superviseTitle", detail.getTitle());
				modelAndView.addObject("awakeDate", Datetimes.format(detail.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
				modelAndView.addObject("superviseTitle", detail.getTitle());
				modelAndView.addObject("count", idSets.size());
				if(idSets.contains(String.valueOf(user.getId()))){
					modelAndView.addObject("isSupervis", true);
				}
				modelAndView.addObject("openModal", request.getParameter("openModal"));
				modelAndView.addObject("bean", detail);
				if(tempIds.length()>0){
					String unCancelledVisor = tempIds.substring(0, tempIds.length()-1);
					modelAndView.addObject("unCancelledVisor", unCancelledVisor);
					modelAndView.addObject("sVisorsFromTemplate", "true");//公文调用的督办模板是否设置了督办人
				}
			}
		 	//督办相关.
	        ColSuperviseDetail cdetail = colSuperviseManager.getCurrentUserSupervise(Constant.superviseType.summary.ordinal(),summary.getId(),user.getId());
	        if(cdetail!=null && summary.getCaseId() != null){
	        	modelAndView.addObject("isSupervis", true);
	        	modelAndView.addObject("bean", cdetail);
	        	modelAndView.addObject("openModal", request.getParameter("openModal"));
	        }

			return modelAndView;
	}

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView detail(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String _affairId = request.getParameter("affairId");
        Long affairId = null;
        if(Strings.isNotBlank(_affairId)){
        	affairId = Long.parseLong(_affairId);
        }
        String type = request.getParameter("type");
        String flag = request.getParameter("flag");
        String openFrom=request.getParameter("openFrom");
        User user = CurrentUser.get();
        Long preArchiveId = null; //预归档ID
        boolean isStoreFlag = false; //是否转储标记
        try {
            ModelAndView view = new ModelAndView("collaboration/collaborationDetail");
            Affair affair = affairManager.getById(affairId);

            //看一下是否被转储了
            if(affair == null){
            	affair = this.hisAffairManager.getById(affairId);
        		isStoreFlag = (affair != null);
            }
            view.addObject("isStoreFlag", isStoreFlag);

            if(affair == null){
                String msg=ColHelper.getErrorMsgByAffair(affair);
                throw new ColException(msg);
            }
            if(Strings.isBlank(flag) && !"doc".equals(type)){
            	if(!isStoreFlag){
            		ColHelper.updateAffairStateWhenClick(affair, affairManager);
            	}
            }
            else{
                //来自文档中心的归档
            	ColSummary summary = null;
            	if(isStoreFlag){ //转储数据
            		summary = hisColManager.getColSummaryById(affair.getObjectId(), true);
            	}
            	else{
            		summary = colManager.getColSummaryById(affair.getObjectId(), true);
            	}
            	preArchiveId = summary.getArchiveId();
            	if(summary != null && "FORM".equals(summary.getBodyType())){
            		List docformlist = new ArrayList();
            		DocFromPotent olddocform =new DocFromPotent();
            		olddocform.setAffairid(affairId);
            		docformlist = docHierarchyManager.queryFormpotent(olddocform);
            		SeeyonForm_ApplicationImpl fapp=(SeeyonForm_ApplicationImpl)SeeyonForm_Runtime.getInstance().getAppManager().findById(summary.getFormAppId());
            		HashMap formmap = new HashMap();
            		List<Long> formList = new ArrayList<Long>();
            		for (int j = 0; j < fapp.getFormList().size(); j++) {
            			SeeyonFormImpl sf = (SeeyonFormImpl) fapp.getFormList().get(j);
            			formmap.put(sf.getFormId(), sf.getFormName());
            			formList.add(sf.getFormId());
            		}
            		StringBuilder formids = new StringBuilder();
            		StringBuilder operationids = new StringBuilder();
            		StringBuilder formNames = new StringBuilder();
            		for(Long formId:formList){
            			if(formId != null){
            				for(int j=0;j<docformlist.size();j++){
                    			DocFromPotent docform = (DocFromPotent)docformlist.get(j);
                    			Long docformId = docform.getFormid();
                    			Long operationId = docform.getOperationid();
                    			if(formId.equals(docformId)){
                    				formids.append(docformId).append("|");
                        			operationids.append(operationId).append("|");
                        			if(formmap.get(docformId) !=null)
                        				formNames.append(formmap.get(docformId)).append("|");
                    			}
                    		}
            			}
            		}
            		view = new ModelAndView("form/formquery/showRecordDetail")
            		.addObject("affairId", affairId)
            		.addObject("formId", formids)
            		.addObject("operationId", operationids)
            		.addObject("formNames", formNames);
            	}
            }
            //SECURITY 访问安全检查
            if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.collaboration, user, affair.getObjectId(), affair, preArchiveId)){
            	return null;
            }
            if(Strings.isBlank(flag)){
        		StateEnum state = StateEnum.valueOf(affair.getState());
        		if(state.equals(StateEnum.col_cancel) || state.equals(StateEnum.col_stepBack) || state.equals(StateEnum.col_stepStop)){
        			String msg=ColHelper.getErrorMsgByAffair(affair);
        			throw new ColException(msg);
        		}
            }
            V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
            ColSummary colsummary = colManager.getColSummaryById(affair.getObjectId(), true);
            if(colsummary != null){
                if(member.getSecretLevel()< colsummary.getSecretLevel()){
                    throw new ColException("涉密等级不够，无法查看！");
                }
            }
            String subject = affair.getSubject() ;
            if(affair.getForwardMember() != null){
            	List<String> members = ColHelper.getForwardMemberNames(affair.getForwardMember(), orgManager) ;
            	if(members != null){
                	for(String name : members){
                		subject = subject+ ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource","col.forward.subject.suffix",name) ;
                	}
            	}
            }

            view.addObject("summarySubject", subject );
            view.addObject("summaryId", affair.getObjectId());
            view.addObject("isDesign", true);
            view.addObject("type", type);
            view.addObject("openFrom", openFrom);
        	String from = request.getParameter("from");
        	if(affair.getState().equals(StateEnum.col_done.getKey()) && "Pending".equals(from)) from = "Done";
        	view.addObject("from", from);
        	return view;
		} catch (ColException e) {
			PrintWriter out = response.getWriter();
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
        	out.println("if(typeof(window.dialogArguments)!= 'undefined' && window.dialogArguments){"); //弹出
        	out.println("  window.returnValue = \"true\";");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println(" if(typeof(parent)!= 'undefined' && parent != null   && typeof(parent.getA8Top)!= 'undefined'  && parent.getA8Top() != null){");
        	out.println(" 		if( parent.getA8Top().document.getElementById('main')!=null){");
        	out.println("     		parent.getA8Top().reFlesh();");
        	out.println("		}else{");
        	out.println("			parent.getA8Top().window.close();");
        	out.println("		}");
        	out.println("  }");
        	out.println("}");
        	out.println("</script>");
        	return null;
		}
    }

    /**
     * 待发协同列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listWaitSend(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
    	String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");

        ModelAndView modelAndView = new ModelAndView("collaboration/listWaitSend");
        if(!FormBizConfigUtils.validate(modelAndView, request, response, FormBizConfigConstants.MENU_TO_SEND_AFFAIRS))
        	return null;
        
        V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目 程炯 2012-8-31 获得人员以便获取人员密级
        
        List<Long> templeteIds = this.parseTempleteIds(request);
        List<ColSummaryModel> queryList = null;
        if (condition != null) {
            queryList = colManager.queryByCondition(condition, textfield, textfield1,  StateEnum.col_waitSend.key(), templeteIds,member.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的待发协同
        }
        if (queryList != null) {
            modelAndView.addObject("csList", queryList);
        } else {
            List<ColSummaryModel> csList = colManager.queryDraftList(templeteIds,member.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的待发协同
            modelAndView.addObject("csList", csList);
        }


        Map<String, Metadata> colMetadata = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        User user = CurrentUser.get();
        modelAndView.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
        V3xOrgMember mem = orgManager.getMemberById(user.getId());
    	List<MemberPost> secondPosts = mem.getSecond_post();
    	modelAndView.addObject("secondPosts", secondPosts);

        return modelAndView;
    }

    /**
     * 已发协同列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listSent(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");
        
        V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目 程炯 2012-8-31 获得人员以便获取人员密级

        ModelAndView modelAndView = new ModelAndView("collaboration/listSent");
        if(!FormBizConfigUtils.validate(modelAndView, request, response, FormBizConfigConstants.MENU_SENT_AFFAIRS))
        	return null;

        List<Long> templeteIds = this.parseTempleteIds(request);
        List<ColSummaryModel> queryList = null;
        if (condition != null) {
            queryList = colManager.queryByCondition(condition, textfield, textfield1, StateEnum.col_sent.key(), templeteIds,member.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的已发协同
        }
        if (queryList != null) {
            modelAndView.addObject("csList", queryList);
        } else {
            List<ColSummaryModel> csList = colManager.querySentList(templeteIds,member.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的已发协同
            modelAndView.addObject("csList", csList);
        }
        Map<String, Metadata> colMetadata = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);

        modelAndView.addObject("colMetadata", colMetadata);


        return modelAndView;
    }
    public ModelAndView preChangeTrack(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("collaboration/preChangeTrack");
    	String affairId = request.getParameter("affairId");
    	String ids = "";
    	boolean isTrack = false;
    	boolean isWorkFlowFinished = false;
    	if(Strings.isNotBlank(affairId)){
    		List<ColTrackMember> tracks = colManager.getColTrackMembers(Long.valueOf(affairId));
    		if(tracks!=null){
    			for(ColTrackMember colTrackMember:tracks){
    				if("".equals(ids))ids = String.valueOf(colTrackMember.getTrackMemberId());
    				else ids += ","+String.valueOf(colTrackMember.getTrackMemberId());
    			}
    		}
    		Affair affair = affairManager.getById(Long.valueOf(affairId));
    		if(affair.getApp() == ApplicationCategoryEnum.collaboration.getKey()){
    			ColSummary  summary = colManager.getColSummaryById(affair.getObjectId(), false);
    			if(summary.isFinshed()) isWorkFlowFinished = true;
    			modelAndView.addObject("secretLevel",summary.getSecretLevel());//成发集团项目 程炯 2012-9-18 已发设定跟踪传入流程密级
    		}else if(EdocUtil.isEdocCheckByAppKey(affair.getApp())){
    			EdocSummary  summary = edocManager.getEdocSummaryById(affair.getObjectId(), false);
    			if(summary.getFinished()) isWorkFlowFinished = true;
    			modelAndView.addObject("secretLevel",summary.getEdocSecretLevel());//成发集团项目 程炯 2012-9-18 已发设定跟踪传入流程密级
    		}

    		isTrack =  affair.getIsTrack();
    	}
    	modelAndView.addObject("isWorkFlowFinished", isWorkFlowFinished);
    	modelAndView.addObject("isTrack",isTrack);
    	modelAndView.addObject("trackIds", ids);


    	return modelAndView;
    }
    /**
     * 列出我的待办、已办、已发，并根据是否允许转发进行权限过滤，用在协同用引用场�?
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView list4Quote(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("collaboration/list4Quote");

        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");

        StateEnum state = StateEnum.col_sent;
        String coltype = request.getParameter("coltype");
        if(Strings.isNotBlank(coltype)){
        	state = StateEnum.valueOf(Integer.parseInt(coltype));
        }

        List<ColSummaryModel> queryList = this.colManager.queryByCondition4Quote(state, condition, textfield, textfield1);
        modelAndView.addObject("csList", queryList);

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);

        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);

    	return modelAndView;
    }

    /**
     * 列出我的待办、已办、已发，并根据是否允许转发进行权限过滤，用在表单协同用表单数据场景
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView list4QuoteForm(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("collaboration/list4QuoteForm");

        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");
        String formappid = request.getParameter("formappid");
        String quoteformtemId = request.getParameter("quoteformtemId");
        StateEnum state = StateEnum.col_sent;
        String coltype = request.getParameter("coltype");
        if(Strings.isNotBlank(coltype)){
        	state = StateEnum.valueOf(Integer.parseInt(coltype));
        }
        List<ColSummaryModel> queryList =  new ArrayList<ColSummaryModel>();
        if(!"".equals(formappid) && !"null".equals(formappid) && formappid !=null)
            queryList = this.colManager.queryByCondition4QuoteForm(state, condition, textfield, textfield1,formappid,quoteformtemId);
        modelAndView.addObject("csList", queryList);

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        modelAndView.addObject("colMetadata", colMetadata);

        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("formappid", formappid);
        modelAndView.addObject("quoteformtemId", quoteformtemId);
    	return modelAndView;
    }

    public ModelAndView showList4QuoteFrame(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/list4QuoteFrame");
    	modelAndView.addObject("from", request.getParameter("from")) ;
    	modelAndView.addObject("isBind", request.getParameter("isBind")) ;
    	return modelAndView;
    }

    public ModelAndView showList4QuoteFrameForm(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/list4QuoteFrameForm");
    	String formappid = request.getParameter("formappid");
    	String quoteformtemId = request.getParameter("quoteformtemId");
    	modelAndView.addObject("formappid", formappid);
    	modelAndView.addObject("quoteformtemId", quoteformtemId);
    	return modelAndView;
    }

    /**
     * 待办协同列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listPending(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");

        ModelAndView modelAndView = new ModelAndView("collaboration/listPending");
        if(!FormBizConfigUtils.validate(modelAndView, request, response, FormBizConfigConstants.MENU_TO_DEAL_AFFAIRS))
        	return null;

        User user = CurrentUser.get();
        V3xOrgMember theMember = null;
        theMember = orgManager.getEntityById(V3xOrgMember.class,user.getId());

        List<Long> templeteIds = this.parseTempleteIds(request);
        if(theMember.getAgentId() == -1){
	        List<ColSummaryModel> queryList = null;
	        if (condition != null) {
	            queryList = colManager.queryByCondition(condition, textfield, textfield1,  StateEnum.col_pending.key(), templeteIds,theMember.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的待办协同
	        }
	        if (queryList != null) {
	            modelAndView.addObject("pendingList", queryList);
	        } else {
	            List<ColSummaryModel> pendingList = colManager.queryTodoList(templeteIds);
	            modelAndView.addObject("pendingList", pendingList);
	        }
        }else{
        	List<ColSummaryModel> queryList = new ArrayList<ColSummaryModel>();
        	modelAndView.addObject("pendingList", queryList);
        }

        Map<String, Metadata> colMetadata = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.collaboration);
        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);

        List<String> allRequiredOpinionPermissions = permissionManager.getRequiredOpinionPermissions(MetadataNameEnum.col_flow_perm_policy.name(), user.getLoginAccount());
        modelAndView.addObject("allRequiredOpinionPermissions", allRequiredOpinionPermissions);
        return modelAndView;
    }

    /**
     * 已办协同列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listDone(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");

        ModelAndView modelAndView = new ModelAndView("collaboration/listDone");
        if(!FormBizConfigUtils.validate(modelAndView, request, response, FormBizConfigConstants.MENU_DEALT_AFFAIRS))
        	return null;

        V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());//成发集团项目 程炯 2012-8-31 获得人员以便获取人员密级
        
        List<Long> templeteIds = this.parseTempleteIds(request);
	    List<ColSummaryModel> queryList = null;
	    if (condition != null) {
	    	queryList = colManager.queryByCondition(condition, textfield, textfield1,  StateEnum.col_done.key(), templeteIds,member.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的已办协同
	    }
	    if (queryList != null) {
	    	modelAndView.addObject("finishedList", queryList);
	    } else {
	    	List<ColSummaryModel> finishedList = colManager.queryFinishedList(templeteIds,member.getSecretLevel());//成发集团项目 程炯  2012-8-31 根据人员密级筛选可见的已办协同
	        modelAndView.addObject("finishedList", finishedList);
	    }

        Map<String, Metadata> colMetadata = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.collaboration);

        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        return modelAndView;
    }

    public ModelAndView saveOpinionAttach(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String summaryId = request.getParameter("summaryId");
		String opinionId = request.getParameter("signOpinionId");
		// 保存附件
		String fileNames = this.attachmentManager.create(ApplicationCategoryEnum.collaboration,Long.parseLong(summaryId), Long.parseLong(opinionId), request);
        if(Strings.isBlank(fileNames)){
        	PrintWriter out = response.getWriter();
        	out.print("<script type='text/javascript'>");
        	out.print("alert('save unsuccsssful');");
        	out.print("</script>");
        }
        return null;
    }
    /**
     * 表单中插入附件对附件的存储
     * @param request
     * @param response
     * @return
     * @throws Exception

    public ModelAndView  saveFormAttach(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String summaryId = request.getParameter("summaryId");
    	String fileNames = null ;

		if(Strings.isNotBlank(summaryId)){
			fileNames = this.attachmentManager.create(ApplicationCategoryEnum.collaboration,Long.valueOf(summaryId), Long.valueOf(summaryId), request);
		}

        if(Strings.isBlank(fileNames)){
        	PrintWriter out = response.getWriter();
        	out.print("<script type='text/javascript'>");
        	out.print("alert('save unsuccsssful');");
        	out.print("</script>");
        }
    	return null;
    }
*/
    /**
     * 判断是否是代理人处理，如果是那代理信息是否正确
     * @param affair
     * @param user
     * @return  true 不是代理人处理或代理人信息正确  <br>false 代理人处理但代理信息不正确（代理设置过期或缓存问题）
     * @throws Exception
     */
    public boolean validateAgent(Affair affair,User user)throws Exception{
    	if(affair.getMemberId() != user.getId()){
        	List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
        	log.info("代理人处理被代理人事项时的代理信息：代理人信息["+user.getId()+","+user.getName()+"]");
        	if(agentModelList != null && !agentModelList.isEmpty()){
        		for(AgentModel agentModel:agentModelList){
        			log.info("代理信息[AgentId:"+agentModel.getAgentId()+",AgentToId:"+agentModel.getAgentToId()+"]");
        			if(agentModel.getAgentToId().longValue() == affair.getMemberId().longValue()){
        				return true;
        			}
        		}
        		return false;
        	}else{
        		log.info("代理信息为空");
        		return false;
        	}
        }
    	return true;
    }
    /**
     * 正常处理协同
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView finishWorkItem(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
    	String allNodes = request.getParameter("allNodes");
    	String processId = request.getParameter("processId");
    	String currentNodeId = request.getParameter("currentNodeId");
    	String _affairId = request.getParameter("affair_id");
    	//json格式的字符串:选人
    	String popNodeSelected = request.getParameter("popNodeSelected");
    	//json格式的字符串:条件分支
    	String popNodeCondition = request.getParameter("popNodeCondition");
    	//json格式的字符串:新流程信息
    	String popNodeNewFlow = request.getParameter("popNodeNewFlow");
    	if(log.isInfoEnabled()){
    		log.info("popNodeSelected:="+popNodeSelected);
        	log.info("popNodeCondition:="+popNodeCondition);
        	log.info("popNodeNewFlow:="+popNodeNewFlow);
    	}
    	boolean lostBranch = false;
    	if(Strings.isNotBlank(processId) && Strings.isNotBlank(currentNodeId)){
    		boolean isMatch = !"false".equals(request.getParameter("isMatch")); //需要做下一节点匹配，只有会签后才不需要匹配（null为发起时，true是处理，false经过了会签）
    		lostBranch = isMatch && Strings.isBlank(allNodes) && ColHelper.hasCondition(processId,currentNodeId,null,_affairId,true,affairManager);
    	}
    	String branchNodes = request.getParameter("branchNodes");
    	if(Strings.isBlank(request.getParameter("__ActionToken")) || lostBranch){
    		if(lostBranch)
    			log.error("不能获取分支数据，affairId:"+request.getParameter("affair_id")+" summaryId:"+request.getParameter("summary_id")+" allNodes:"+allNodes+" branchNodes:"+branchNodes);
    		ProcessDefManager pdm = WAPIFactory.getProcessDefManager("Engine_1");
            //BPMProcess process = (BPMProcess) pdm.getProcessInReady("admin", processId);
            pdm.deleteProcessInReady("admin", processId);//之前出现这种问题，一般让客户重启A8服务，目的是清除缓存，还不如在这直接清除缓存中的内容，用户下次提交即可。
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据，请重新提交。');");
    		out.println("try {parent.getA8Top().endProc();}");
    		out.println("catch (e) {};");
    		out.println("</script>");
    		return null;
    	}

    	User user = CurrentUser.get();
        String sSummaryId = request.getParameter("summary_id");
        String delAttIds = request.getParameter("theDelAttIds");
        long summaryId = Long.parseLong(sSummaryId);
        //原始核定节点通过标记
        ColSummary oldSummary = colManager.getColSummaryById(summaryId, false);
        Integer oldIsVouch = oldSummary.getIsVouch();
        if(oldIsVouch==null)
        	oldIsVouch = new Integer(0);
        if (Strings.isBlank(sSummaryId) || Strings.isBlank(_affairId)) {
        	StringBuffer msg = new StringBuffer();
        	msg.append("Request URL:" + request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/\n");
        	msg.append("Request Method:" + request.getMethod()+ "\n");
            Enumeration e = (Enumeration)request.getParameterNames();
            while(e.hasMoreElements()){
                String parName = (String)e.nextElement();
                msg.append(parName + ":" + request.getParameter(parName) + "\n");
            }
            //打印异常跟踪信息
            log.warn("finishWorkItem异常: " + msg.toString());
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('处理操作失败：窗口被关闭，系统无法获取所需的提交数据，请您重新打开处理。');");
            out.println("try{parent.closeWindow();}catch(e){}");
        	out.println("</script>");
        	out.close();
        	return null;
        }
       
        ColSummary summary = colManager.getColSummaryById(summaryId, false);
        Long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        // 判断节点权限是否被发起人改变
        String nodePermission = request.getParameter("nodePermission");
        BPMProcess process =  ColHelper.getRunningProcessByProcessId(processId);
        BPMAbstractNode memNode = process.getActivityById(currentNodeId);
        boolean isCompatible= false;
        if(null==memNode){//兼容处理,直接将affair和workitem处理掉
        	isCompatible= true;
        }
        if(!isCompatible && !isNodePermissionChanged(processId, currentNodeId,nodePermission))
        {
    		PrintWriter out = response.getWriter();
        	out.println("<script>");
        	String message = Constant.getString4CurrentUser("node.policy.permissionischanged");
            out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(message) + "\"); ");
            out.println("parent.location.reload(); ");
        	out.println("</script>");
        	return null;
        }    
        
        Affair affair = null;
        try{
	        //检查同步锁
        	if(!isCompatible && !ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.insertPeople,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }
        	 try {
 	        	affair=affairManager.getById(affairId);
 				//校验是否是代理处理，以及代理信息是否成立
 				boolean isValidate=validateAgent(affair,user);
 				if(!isValidate){
 					log.error("校验代理失败,当前用户是："+user.getId()+","+user.getName()+"; 被代理人（事项处理人）:"+affair.getMemberId()+"; affairId:"+affair.getId());
 					response.getWriter().println("<script>alert(\"校验代理失败，请检查日志信息\");parent.doEndSign();</script>");
 		        	return null;
 				}
 			} catch (Exception e) {
 				log.error("校验代理出现异常"+"; affairId:"+affairId,e);
 				response.getWriter().println("<script>alert(\"校验代理出现异常，请检查日志信息\");parent.doEndSign();</script>");
 	        	return null;
 			}
	        ColOpinion signOpinion = new ColOpinion();
	        bind(request, signOpinion);
	        signOpinion.setIdIfNew();

	        String[] afterSign = request.getParameterValues("afterSign");
	        Set<String> opts = new HashSet<String>();
	        if(afterSign!=null){
		        for(String option : afterSign){
		        	opts.add(option);
		        }
	        }
	        //允许处理后归档和跟踪被同时选中，但是他们都不能和删除按钮同时选中
	        if(opts.size()>1 && opts.contains("delete")){
	        	opts.remove("delete");
	        }
	        signOpinion.isDeleteImmediate = opts.contains("delete");
	        boolean track =  opts.contains("track");
	        signOpinion.affairIsTrack = track;
	        signOpinion.isPipeonhole = opts.contains("pipeonhole");
	        //跟踪
	        String trackMembers = request.getParameter("trackMembers");
	        String trackRange = request.getParameter("trackRange");
	        //不跟踪 或者 全部跟踪的时候不向部门跟踪表中添加数据，所以将下面这个参数串设置为空。
	        if(!track || "1".equals(trackRange)) trackMembers = "";
	        colManager.setTrack(affairId, track, trackMembers);

	        String archiveId = request.getParameter("archiveId");
	        if(archiveId != null && !("").equalsIgnoreCase(archiveId)){
	        	signOpinion.setArchiveId(Long.parseLong(request.getParameter("archiveId")));
	        }

	        signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);

	        String manualSelect = request.getParameter("manual_select");
	        if(manualSelect==null || manualSelect.equals("")){
	            manualSelect = null;
	        }

	        //节点选人
	        //key - 节点Id  value - 人的Id
	        Map<String, String[]> map = PopSelectParseUtil.getPopNodeSelectedValues(popNodeSelected);
	        //分支流程
	        //key - 节点Id  value - 是否选择 true|false
	        Map<String,String> condition = PopSelectParseUtil.getPopNodeConditionValues(popNodeCondition,map);
	        PrintWriter out = response.getWriter();

	        try {
	        	String formApp = request.getParameter("formApp");
	        	String flag = "finishWorkItem";
	        	String state = null;
	        	if(formApp != null && !"".equals(formApp)&&!"null".equals(formApp)){
	        		String ratifyflag ="";
	        		//审核通过
	        		String auditPass = (String)request.getAttribute("auditPass");
	        		if(auditPass != null){
	        			state = auditPass;
	        			flag = "auditPass";
	        		}
	        		String vouchPass = (String)request.getAttribute("vouchPass");
	        		if(vouchPass != null){
	        			ratifyflag = vouchPass;
	        			flag = "vouchPass";
	        			//修改协同的核定节点
	        			if(Constant.FormVouch.vouchPass.getKey().equals(ratifyflag)){
	        				 Map<String, Object> columns = new HashMap<String, Object>();
	        			     columns.put("isVouch", Constant.ColSummaryVouch.vouchPass.getKey());
	        				 colManager.update(summaryId, columns);
	        				 summary.setIsVouch(Constant.ColSummaryVouch.vouchPass.getKey());
	        			}
	        		}
	        		/*
                    BPMProcess process = ColHelper.getProcess(processId);
                    if(process != null){ // 当前流程是子新流程
                        BPMSeeyonPolicy startNode = process.getStart().getSeeyonPolicy();
                        if("1".equals(startNode.getNF())){
                            operationType = "update";
                        }
                    }*/

	        		try{
//			        	处理表单状态等信息存储
	        			String operationType = "update";
	        			//如果是关联表单的或子流程的不更新表单状态
	        			if(summary.getParentformSummaryId() !=null || (summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.child.ordinal())){
	        				operationType = "update";
	        				state = "";
	        			}
	        			if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue()){
	                        FormHelper.stopOrRepealOrSubmitOrZCDDOperForWithholding(new Long(request.getParameter("formApp")), summaryId, new Long(request.getParameter("masterId")),summary.getSubject());
	                    }
	        			String formData = FormHelper.setFormAppendValue(user.getId(), user.getName(), user.getLoginName(), request.getParameter("formData"), request.getParameter("formDisplayValue"), request.getParameter("formApp"), request.getParameter("form"), request.getParameter("operation"), request.getParameter("masterId"));
	        			FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), request.getParameter("formApp"), request.getParameter("form"), request.getParameter("operation"), request.getParameter("masterId"), formData,"submit",state, operationType, flag,ratifyflag);

	        			formDaoManager.UpdateDataState(user.getId(), summaryId, state, null, "submit", "finishWorkItem");
	        		}
	        		catch(DataDefineException e1){
	        			if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
	        				PrintWriter out1 = response.getWriter();
	        				out1.println("<script>");
	        				out1.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + StringEscapeUtils.escapeJavaScript(Constant.getString4CurrentUser("col.reopen.alert")) + "\"); ");
	        				out1.println("try {parent.isSubmitFinished = true;parent.closeWindow();}catch (e) {};");
	        				out1.println("</script>");
	        				out1.close();
	        				return null;
	        			}
	        			if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
	        				throw new RuntimeException(e1.getMessage());
	        			else{
	        				log.error("保存表单数据时发生错误", e1);
	        				//throw new RuntimeException("不能保存");
	        				throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	        			}

	        		}catch(Exception e){
	        			log.error("保存表单数据时发生错误", e);
	        			//throw new RuntimeException("不能保存");
	        			throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	        		}

        		Long key = Long.valueOf(Thread.currentThread().getId());
        		List<String> flowIdMsgList = (List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") ;
        		if(flowIdMsgList != null){
        			OperHelper.creatformmessage(request,response,flowIdMsgList);
        		}
		        	this.delAttByids(delAttIds) ;
                    //NF 发起新流程
		        	String hasNewflow= "false";
		        	JSONObject popNodeNewFlowObj= null;
		        	if(popNodeNewFlow!=null && !"".equals(popNodeNewFlow)){
		        		popNodeNewFlowObj= new JSONObject(popNodeNewFlow);
		        		hasNewflow= popNodeNewFlowObj.getString("hasNewflow");
		        	}
//                    if("true".equals(request.getParameter("hasNewflow"))){
                    if("true".equals(hasNewflow)){
//                        String[] newflowIds = request.getParameterValues("newflow");
                        JSONArray popNodeNewFlowAr= popNodeNewFlowObj.getJSONArray("newFlows");
//                        if(newflowIds != null && newflowIds.length > 0){
                        if(popNodeNewFlowAr != null && popNodeNewFlowAr.length() > 0){
                            Map<Long, NewflowRunning> newflowsMap = newflowManager.getNewflowRunningMap(summary.getTempleteId(), currentNodeId);
//                            for (String newflowIdStr : newflowIds) {
                            for (int j=0;j<popNodeNewFlowAr.length();j++) {
                            	JSONObject jsonNewFlowObj= popNodeNewFlowAr.getJSONObject(j);
                            	String newflowIdStr= jsonNewFlowObj.getString("newFlowId");
//                                String senderIdStr = request.getParameter("senderId_" + newflowIdStr);
                                String senderIdStr = jsonNewFlowObj.getString("newFlowSender");
                                Long newflowId = Long.parseLong(newflowIdStr);
                                Long senderId = Long.parseLong(senderIdStr);

                                if(senderId == null){
                                	senderId = user.getId();
                                }

                                NewflowRunning runFlow = newflowsMap.get(newflowId);

                                Templete templete = templeteManager.get(runFlow.getTempleteId());
                                if(templete == null){
                                    log.error("发起新流程失败，原因：触发的表单模板已被删除。NewflowRunningId=" + runFlow.getId());
                                    continue;
                                }
                                V3xOrgMember sender = orgManager.getMemberById(senderId);

                                ColSummary newSummary = (ColSummary)XMLCoder.decoder(templete.getSummary());
                                newSummary.setIdIfNew();
                                newSummary.setIsVouch(0);
                                newSummary.setOpinions(null);
                                Date bodyCreateDate = new Date();
                                newSummary.setTempleteId(templete.getId());
                                //保存附件
                                if(templete.isHasAttachments()){
                                    this.attachmentManager.copy(templete.getId(), templete.getId(), newSummary.getId(), newSummary.getId(), ApplicationCategoryEnum.collaboration.key());//附件
                                    newSummary.setHasAttachments(true);
                                }
                                Map<String, Object> options = new HashMap<String, Object>();
                                Constant.SendType sendType = Constant.SendType.normal;
                                ColOpinion senderOpinion = new ColOpinion();
                                senderOpinion.setIdIfNew();

                                senderOpinion.setContent(Constant.getString4CurrentUser("newflow.fire.opinion"));
                                senderOpinion.affairIsTrack = true;
                                FlowData flowData = FlowData.flowDataFromXML(templete.getWorkflow());
                                String[] formInfo = FormHelper.getFormPolicy(flowData.getXml());
                                newSummary.setFormAppId(Long.parseLong(formInfo[0]));//保存fromAppid
                                newSummary.setFormId(Long.parseLong(formInfo[1]));//保存表单id
                                newSummary.setFormRecordId(summary.getFormRecordId());//保存表单主id
                                ColBody body = new ColBody();
                                body.setContent(summary.getFormRecordId()+"");
                                body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM);
                                body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
                                newSummary.setOrgDepartmentId(sender.getOrgDepartmentId());
                                //新流程的account和新流程发起者一致
                                newSummary.setOrgAccountId(sender.getOrgAccountId());
                                String  subject  = Constant.getString4CurrentUser("newflow.fire.subject", templete.getSubject() + "(" + sender.getName() + " " + Datetimes.formatDatetime(bodyCreateDate) + ")");
                            	if(!Strings.isBlank(templete.getCollSubject())){
                            		String formData = FormHelper.getFormRun4ColSubject(user.getId(), user.getName(), user.getLoginName(),formInfo[0], formInfo[1],
                            				formInfo[2], newSummary.getFormRecordId()+"", newSummary.getId()+"", null, formInfo[3], false) ;
                            		formData = colManager.getColSubjectXML(formData)  ;
                            		subject = FormHelper.getCollSubjuet(formInfo[0], templete.getCollSubject(),formData,formInfo[1], formInfo[2],true) ;
                            		try{
                            			FormHelper.savePojo4FlowId(runFlow.getTempleteId()+"", formInfo[0], newSummary.getFormRecordId()+"");
                            		} catch(Exception e){
                            			log.error("保存流水号时出错！",e);
                            		}
	                           		if(Strings.isNotBlank(subject)){
	                           			subject = Strings.getLimitLengthString(Constant.getString4CurrentUser("newflow.fire.subject",subject), 160, "...") ;
	                           		}else{
	                           			subject = Constant.getString4CurrentUser("newflow.fire.subject",templete.getSubject() + "(" + sender.getName() + " " + Datetimes.formatDatetime(bodyCreateDate) + ")");
	                           		}
                           	    }
                            	newSummary.setSubject(subject);
                            	Long newAffairId = 0L;
                                try {
                                    String newProcessId = null;

                                    //改变流程，增加自动节点
                                    newProcessId = ColHelper.saveOrUpdateProcessByXML(templete.getWorkflow(), null, flowData.getAddition(), flowData.getCondition(), sender);
                                    BPMProcess newProcess = ColHelper.getProcess(newProcessId);
                                    V3xOrgAccount account = orgManager.getAccountById(sender.getOrgAccountId());
                                    ColHelper.changeProcess4Newflow(newProcess, flowData, sender, account.getShortname());
                                    newSummary.setProcessId(newProcessId);

                                    newSummary.setNewflowType(Constant.NewflowType.child.ordinal()); //标记为子流程
                                    newAffairId = colManager.runCase(flowData, newSummary, body, senderOpinion, sendType, options, true, senderId, newProcessId);
                                    //处理表单状态等信息存储
                                    formDaoManager.saveDataState(newSummary, 1, -1);

                                    boolean isActivate = newAffairId != -1L;
                                    //runFlow.setMainSummaryId(summaryId);
                                    //runFlow.setMainNodeId(currentNodeId);
                                    runFlow.setMainCaseId(summary.getCaseId());
                                    runFlow.setMainFormId(Long.parseLong(formInfo[1]));
                                    runFlow.setMainProcessId(summary.getProcessId());
                                    //runFlow.setMainTempleteId(summary.getTempleteId());
                                    runFlow.setSenderId(senderId);
                                    runFlow.setSummaryId(newSummary.getId());
                                    runFlow.setCaseId(newSummary.getCaseId());
                                    runFlow.setProcessId(newSummary.getProcessId());
                                    //runFlow.setTempleteId(newSummary.getTempleteId());
                                    runFlow.setAffairId(newAffairId);
                                    runFlow.setMainFormType(1);
                                    runFlow.setAffairState(StateEnum.col_sent.key());
                                    runFlow.setUpdateTime(new Date());
                                    runFlow.setIsActivate(isActivate);
                                    newflowManager.updateNewflowRunning(runFlow);

                                    //协同发起事件通知
                                    CollaborationStartEvent event = new CollaborationStartEvent(this);
                                    event.setSummaryId(newSummary.getId());
                                    event.setFrom("pc");
                        			event.setAffairId(newAffairId);
                                    EventDispatcher.fireEvent(event);
                                    //保存新流程的督办信息
                                    colSuperviseManager.copyAndSaveSuperviseFromTemplete(sender, newSummary, templete.getId());
                                    //全文检索入库
                                    DateSharedWithWorkflowEngineThreadLocal.setNoIndex();
                                    if(IndexInitConfig.hasLuncenePlugIn()){
                                    	try {
                                    		indexManager.index(((IndexEnable)colManager).getIndexInfo(newSummary.getId()));
										}
										catch (Exception e) {
											log.warn(e.getMessage());
										}
                                    }
                                }
                                catch (Exception e) {
                                    log.error("新流程触发自动发起协同异常", e);
                                    throw new ColException("新流程触发自动发起协同异常", e);
                                }

                                //触发新流程，发送系统消息 ： 来自《主流程标题》的子流程《子流程标题》已经发起
                                Set<MessageReceiver> receivers = new HashSet<MessageReceiver>();
                                Affair senderAffair  = affairManager.getCollaborationSenderAffair(summary.getId());
                                if(runFlow.getIsCanViewByMainFlow()){//能被主流程查看，就能打开连接
                                	receivers.add(new MessageReceiver(senderAffair.getId(), senderAffair.getMemberId(),"message.link.col.done.newflow",newAffairId,0,summary.getId()));
                                }else{
                                	receivers.add(new MessageReceiver(senderAffair.getId(), senderAffair.getMemberId()));
                                }
                                userMessageManager.sendSystemMessage(new MessageContent("col.workflow.new.start", summary.getSubject(),newSummary.getSubject())
                            	,ApplicationCategoryEnum.collaboration, user.getId(), receivers);
                            }
                        }
                    }
                }
	        	//保存督办相关信息
	        	this.saveColSupervise(request, response, summary, false,Constant.superviseState.supervising.ordinal(),true);
		        if ("true".equals(request.getParameter("isDeleteSupervisior"))) {
		        	ColSuperviseDetail detail= colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(),summaryId);
		        	this.colSuperviseManager.deleteSuperviseById(detail.getId());
				}

	            /**
	             * 保存附件:
	             * 需要先要删除草稿的时候附件，然后再保存，否则会报ID重复；
	             * 所以deleteDraftOpinion必须放到attachmentManager.create方法的前面，挪动位置的一定要注意到这个先后顺序
	             * 已经报了N次由于这个原因引起的BUG了，注意！！！
	             */
		        String draftOpinionId = request.getParameter("draftOpinionId");
		        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
		        	this.deleteDraftOpinion(Long.parseLong(draftOpinionId));
		        }
		        
		        attachmentManager.deleteByReference(summaryId, 4);//删除表单中选择的关联文档
	            String isUploadAtt = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, signOpinion.getId(), request);
            	AttachmentEditHelper editHelper = new AttachmentEditHelper(request);
            	if(editHelper.hasEditAtt()){//是否修改附件
            		colManager.updateSummaryAttachment(editHelper.attSize(),editHelper.parseProcessLog(Long.parseLong(processId), Long.parseLong(currentNodeId)),summaryId);
            	}
            	signOpinion.setHasAtt(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(isUploadAtt));

		        //推送消息    affairId,memberId#affairId,memberId#affairId,memberId
			    String pushMessageMembers = request.getParameter("pushMessageMemberIds");
			    setPushMessagePara2ThreadLocal(pushMessageMembers);
			    //logPushMessageInfo(user.getName(),summary.getSubject());
			    String[] fieldName=request.getParameterValues("fieldName");
			    process = ColHelper.saveModifyingProcess(processId, user.getId()+"");
	    		if(process != null){
	    			//ColHelper.updateRunningProcess(process);
	    			//如果该流程实例存在待添加的节点，将其激活
	    			ColHelper.saveAcitivityModify(process, user.getId()+"");
	    		}else{
	    			process= ColHelper.getCaseProcess(processId);
	    		}
	    		colManager.clearSession();
			    colManagerFacade.finishWorkItemFacade(affair, signOpinion, map,
			    		condition, process, user,draftOpinionId,fieldName,
			    		summary,request,summaryId,formApp,oldIsVouch);
			    colManager.clearSession();
			    //更新全文检索库
				UpdateIndexManager updateIndexManager = (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
				updateIndexManager.update(affair.getObjectId(), ApplicationCategoryEnum.collaboration.getKey());
			    if( formApp != null && !"".equals(formApp)&&!"null".equals(formApp) && "auditPass".equals(flag)){
			    	//fix:AEIGHT-5959
			    	//表单审批事件通知
	    			try{
	    				CollaborationFormApprovalEvent event = new CollaborationFormApprovalEvent(this);
	    				if(!"".equals(state) && state !=null){
	    					event.setState(Integer.parseInt(state));
	    				}
	    				event.setSummaryId(summaryId);
	    				event.setUserId(user.getId());
	    				Templete template = templeteManager.get(summary.getTempleteId());
	    				event.setTemplateCode(template.getTempleteNumber());
	    				if(summary.getState().intValue() == Constant.flowState.finish.ordinal()){
	    					event.setIsFinished(true);
	    				}
	    				event.setAffairId(affairId);
	    				EventDispatcher.fireEvent(event);
	    			}catch(Exception e){
	    				logger.error("表单审批事件通知出错！",e);
	    			}
			    }
	        	out.println("<script>");
	        	boolean f = (Boolean)(BrowserFlag.PageBreak.getFlag(request));
	        	if(f == true){
		        	out.println("if(parent.getA8Top().dialogArguments){"); //弹出
		        	out.println("  parent.getA8Top().returnValue = \"true\";");
		        	out.println("  parent.getA8Top().dialogArguments.v3x.ModalDialogResultValue = \"true\";");
		        	out.println("  parent.getA8Top().close();");
		        	out.println("}else{");
//		        	out.println("  parent.getA8Top().reFlesh();");
		        	if(BrowserEnum.Safari == BrowserEnum.valueOf(request)){
		        		out.println("  try{parent.getA8Top().close();parent.getA8Top().opener.getA8Top().reFlesh();}catch(e){}");
		        	}
		        	out.println("  try{parent.closeWindow();}catch(e){}");
		        	out.println("}");
	        	}else{
	        		out.println("parent.parent.window.close();");
	        	}
	        	out.println("</script>");
	        	out.close();
	            return null;
	        }
	        catch (ColException e) {
                //FIXME 如果工作流中已完成，但协同状态未更新，则更新并记录该协同事项
                //此做法不文雅，毁灭了证据
                /*
                Affair affair = affairManager.getById(affairId);
                if(affair != null){
                    Long workitemId = affair.getSubObjectId();
                    WorkItem item = ColHelper.getWorkItemById(workitemId.intValue());
                    if(WorkItem.STATE_DONE == item.getState() && affair.getState().equals(StateEnum.col_pending.ordinal())){
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        affair.setState(StateEnum.col_done.key());
                        affair.setSubState(SubStateEnum.col_normal.key());
                        affair.setCompleteTime(now);
                        affairManager.updateAffair(affair);
                        log.warn("待办事项异常结束[工作项已完成，协同状态未更新，强制更新协同状态。],affair=" + affair.getId());
                    }
                }*/
	        	out.println("<script>");
	        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
	        	out.println("parent.closeWindow();");
//	        	out.println("if(window.dialogArguments){"); //弹出
//	        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
//	        	out.println("  window.close();");
//	        	out.println("}else{");
//	        	out.println("  parent.getA8Top().reFlesh();");
//	        	out.println("}");
	        	out.println("</script>");
	        	out.close();
                log.error("finishWorkItem 异常[SummaryId=" + sSummaryId + ",affairId=" + _affairId + "]:", e);
	        	return null;
			}
        }
        catch(Exception e){
        	log.error("finishWorkItem 异常:", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        	//表单解锁，冗余处理，防止窗口关闭时的事件未执行
        	colManager.colDelLock(summary);

        }

        return null;
    }

//	private void logPushMessageInfo(String userName,String subject) {
//		try{
//			if(log.isInfoEnabled()){
//				String membernames = "";
//				  List<Long[]> pushMemberIds = DateSharedWithWorkflowEngineThreadLocal.getPushMessageMembers();
//				  if(Strings.isNotEmpty(pushMemberIds)){
//					  for(Long[] info:pushMemberIds){
//						  try {
//							  V3xOrgMember m  = orgManager.getMemberById(info[1]);
//							  if(m!=null){
//								  if("".equals(membernames)){
//									  membernames = m.getName();
//								  }else{
//									  membernames += ","+m.getName();
//								  }
//							  }
//						  } catch (BusinessException e) {
//							  log.error("记录推送消息日志",e);
//						  }
//					  }
//				  }
//				  log.info(userName+"处理协同《"+subject+"》,推送消息给："+membernames);
//			}
//		}catch(Exception e){
//			log.error("",e);
//		}
//
//	}

	private void delAttByids(String ids){
    	if(Strings.isBlank(ids)){
    		return ;
    	}
    	try{
        	if(ids.contains(",")){
        		String id[] = ids.split(",") ;
        		for(String str : id){
        			this.attachmentManager.deleteById(Long.valueOf(str)) ;
        		}
        	}else{
        		this.attachmentManager.deleteById(Long.valueOf(ids)) ;
        	}
    	}catch(Exception e){
    		log.error("delAttByids Failed",e) ;
    	}
    }
    private void deleteDraftOpinion(long opinionId){
    	try {
    		colManager.deleteDraftOpinion(opinionId);
			this.attachmentManager.deleteByReference(opinionId, opinionId);
		}
		catch (Exception e) {
			log.error("deleteDraftOpinion Failed", e);
		}
    }

    /**
     * 删除
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView delete(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		String[] affairIds = request.getParameterValues("affairId");
		String pageType = request.getParameter("pageType");
		User user = CurrentUser.get();
		String result = "";
		String from = request.getParameter("from");
		List<Affair> affairList = null;
		if (affairIds != null) {
			affairList = new ArrayList<Affair>();
			for (String affairId : affairIds) {
				Long _affairId = new Long(affairId);
				Affair affair = affairManager.getById(_affairId);
				affairList.add(affair);
				int state = affair.getState();
				if(state != StateEnum.col_pending.getKey()
						&& state != StateEnum.col_done.getKey()
						&& state != StateEnum.col_sent.getKey()
						&& (state != StateEnum.col_waitSend.getKey() || "listSent".equals(from))){ //已发里被回退的不能删除
					result += ColHelper.getErrorMsgByAffair(affair) + "\n";
				}
			}
		}

		if(!"".equals(result)){
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(result) + "\")");
			out.println("if(window.dialogArguments){"); //弹出
			out.println("  window.returnValue = \"true\";");
			out.println("  window.close();");
			out.println("}else{");
			out.println("  parent.getA8Top().reFlesh();");
			out.println("}");
			out.println("");
			out.println("</script>");
			return null;
		}

		if (affairList != null) {
			for (Affair affair : affairList) {
				if ("draft".equals(pageType)) {
					ColSummary summary = colManager.getColSummaryById(affair.getObjectId(), true);
					if("FORM".equals(summary.getBodyType())) {
						ColBody body = summary.getFirstBody();
						Templete template = templeteManager.get(summary.getTempleteId());
						String formAppId = "";
				        String formId = "";
				        String formOperationId="";
						if(template != null){
							if(template.getFormParentId()!=null)
								template = templeteManager.get(template.getFormParentId());
							FormContent formContent = (FormContent)XMLCoder.decoder(template.getBody());
					     	FormBody formBody = formContent.getForms().get(0);
					     	formAppId = formBody.getFormApp();
					     	formId = formBody.getForm();
					     	formOperationId = formBody.getOperationName();
						}else{
						      BPMProcess process = ColHelper.getCaseProcess(summary.getProcessId());
						        BPMSeeyonPolicy policy = null;
						        if(process != null){
							        int affairState = affair.getState().intValue();
							        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
							        	policy = process.getStart().getSeeyonPolicy();
							        }
							        else{
							        	BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
							        	if(activity != null){
							        		policy = activity.getSeeyonPolicy();
							        	}
							        }
							        if(policy != null){
							           formAppId = policy.getFormApp();
							           formId = policy.getForm();
							           formOperationId = policy.getOperationName();
							        }
						        }
						}
				     	String operationType = "delete";
				     	try{
                            boolean isCanDelete = true;
                            if(summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.main.ordinal()){
                                String finishedNewflowTitle = newflowManager.getFinishedNewflow(summary.getId(), null);
                                if(Strings.isNotBlank(finishedNewflowTitle)){
                                    isCanDelete = false;
                                }
                            }
                            //增加防护，以避免待发或草稿状态发送事务失败导致待发中未删除引起的后期删除待办后表单数据丢失的问题
                            //该问题应该从根本上解决发送时的事务问题，但目前暂以防护解决此问题
                            try{
                            	List<ColSummary> summarys = (List<ColSummary>)colManager.getSummaryIdByFormIdAndRecordId(Long.valueOf(formAppId),Long.valueOf(formId),Long.valueOf(body.getContent()));
                            	for(ColSummary col : summarys){
                            		if(!col.getId().equals(summary.getId()) && col.getNewflowType() != Constant.NewflowType.child.ordinal()){
                            			isCanDelete = false;
                            			break;
                            		}
                            	}
                            }catch(Exception e){
                            	//临时解决误删除表单数据问题，为避免Long.valueOf()时引起异常导致后续无法执行捕获，不引不进行其他处理
                            }
				     		//如果该协同不是来自关联表单，也没有被其他其他表单引用过再进行删除
				     		if(summary.getParentformSummaryId() == null && colManager.getSummaryByParentformId(summary.getId()) && isCanDelete){
				     			FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formAppId, formId, formOperationId, body.getContent(), null,"submit","0",operationType,null,Constant.FormVouch.vouchDefault.getKey());
				     		}
				     		List<Long> summaryidlist = new ArrayList();
				     		summaryidlist.add(summary.getId());
				     		formDaoManager.delByCondition(summaryidlist);
				     		if("listWaitSend".equals(from))
				     			TriggerHelper.deleteTriggerInfo(summary.getId());
				     	}catch(DataDefineException e1){
				      		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
				      			  throw new RuntimeException(e1.getMessage());
				      		  else{
				      			  log.error("保存表单数据时发生错误", e1);
				      			  //throw new RuntimeException("不能保存");
				      			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
				      		  }

				         }catch(Exception e){
				      		  log.error("保存表单数据时发生错误", e);
				      		  //throw new RuntimeException("不能保存");
				      		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
				      	 }
				     	Long key = Long.valueOf(Thread.currentThread().getId());
				     	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
				     		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
				     		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
				     	}
					}
					if(summary.getProcessId()!=null){
						processLogManager.deleteLog(Long.parseLong(summary.getProcessId()));
					}
				}
				colManager.deleteAffair(pageType, affair.getId());
				//删除事项更新全文检索库
				updateIndexManager.update(affair.getObjectId(), ApplicationCategoryEnum.collaboration.getKey());
			}
		}
		return super.refreshWorkspace();
	}

    public ModelAndView pigeonhole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		String[] affairIds = request.getParameterValues("affairId");
		String pageType = request.getParameter("pageType");
		String[] archiveIds = request.getParameterValues("archiveId");
		User user = CurrentUser.get();
		if (affairIds != null && archiveIds != null) {
			for (int i=0; i<affairIds.length; i++) {
				Long _affairId = new Long(affairIds[i]);
				Long _archiveId = new Long(archiveIds[i]);
                DocResource res = docHierarchyManager.getDocResourceById(_archiveId);
                if(res != null){
                    colManager.updateAffair(pageType, _affairId, res.getParentFrId());
                    //插入操作日志
                    String forderName = docHierarchyManager.getNameById(res.getParentFrId());
                    appLogManager.insertLog(user, AppLogAction.Coll_Pigeonhole, user.getName(), res.getFrName(), forderName);
                }
                else{
                    //弹出文档夹被删除的提示信息
                    String errorTip = Constant.getString("col.pigeonhole.forderNotExist");
                    PrintWriter out = response.getWriter();
                    out.println("<script>");
                    out.println("alert('" + errorTip + "')");
                    out.println("</script>");
                    return super.refreshWorkspace();
                }
			}
		}

		return super.refreshWorkspace();
	}

    /**
     * 显示协同内容和处理信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView getContent(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("collaboration/collaborationTopic");
        String s_affairId = request.getParameter("affairId");
        Affair affair = affairManager.getById(Long.parseLong(s_affairId));
        boolean isStoreFlag = false; //是否转储标记
        //看一下是否被转储了
        if(affair == null){
        	affair = this.hisAffairManager.getById(Long.parseLong(s_affairId));
    		isStoreFlag = (affair != null);
        }
        modelAndView.addObject("isStoreFlag", isStoreFlag);

        if(affair == null){
        	String msg = ColHelper.getErrorMsgByAffair(affair);
            throw new ColException(msg);
        }
        String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);
        String type = request.getParameter("type");
        modelAndView.addObject("type", type);

        modelAndView.addObject("isTrack", affair.getIsTrack());

        ColSummary summary = null;
    	if(isStoreFlag){ //转储数据
    		summary = hisColManager.getColAllById(summaryId);
    	}
    	else{
    		summary = colManager.getColAllById(summaryId);
    	}

        User user = CurrentUser.get();
        //SECURITY 访问安全检查
        if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.collaboration, user, summaryId, affair, summary.getArchiveId())){
        	return null;
        }
        boolean hasDiagram = false;
        if(summary != null && summary.getCaseId()!= null)
        	hasDiagram = true;
        modelAndView.addObject("hasDiagram", hasDiagram);

        modelAndView.addObject("isShowButton", false);
        modelAndView.addObject("bodytype", summary.getBodyType());


        modelAndView.addObject("finished", summary.isFinshed());
        String from = request.getParameter("from");

        BPMProcess process = null;
        if(isStoreFlag){
        	process = ColHelper.getHisCaseProcess(summary.getProcessId());
        }
        else{
        	process = ColHelper.getCaseProcess(summary.getProcessId());
        }
        BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);



        modelAndView.addObject("summary", summary);
        ColBody body = summary.getFirstBody();
        
        //为预制协同模板-lxx
        if(summary.getTempleteId() != null){
        	Templete templete = templeteManager.get(summary.getTempleteId());
//            if(affair!=null){
//            	if(affair.getState()!=null||affair.getState()==1){
//            		
//            	}
//            }
        	if(templete!=null&&("11111122222233334444".equals(templete.getTempleteNumber())||"11111122222233335555".equals(templete.getTempleteNumber()))){
        		String tempcontent = body.getContent();
             	if(tempcontent.indexOf("id=")>-1&&tempcontent.indexOf("userno=")>-1&&tempcontent.indexOf("readonly=")>-1){
             		Integer startuserno = tempcontent.indexOf("userno=");
             		Integer startreadonly = tempcontent.indexOf("readonly=");
             		String username = tempcontent.substring(startuserno,startreadonly);
             		if(username.indexOf("userno=")>-1){
             			username = username.substring(7);
            		}
             		if(username.indexOf("&")>-1){
             			username = username.substring(0, username.indexOf("&"));
            		}
             		Integer affairState=colManager.getAffairState(affair.getId());
             		if(user.getLoginName().equals(username)&& affairState.intValue()!=2){
             			
             			tempcontent=tempcontent.replaceAll("readonly=1", "readonly=0");
             		}else{
             			tempcontent=tempcontent.replaceAll("readonly=0", "readonly=1");
             		}
             		body.setContent(tempcontent);
             	}
        	}
        }
        	
    	
        modelAndView.addObject("body", body);

        modelAndView.addObject("affairId", s_affairId);
        //是否是表单
        boolean isForm = "FORM".equals(body.getBodyType());

        String nodePermissionPolicy = "Collaboration";
        String nodePermissionPolicyName = "Collaboration";
        String formAppId = null; //表单应用id
        String formId = null; //表单id
        String formOperationId = null; //表单节点操作Id
        boolean formOperationReadonly = false; //表单节点操作是否是只读

        int affairState = affair.getState().intValue();


        //如果数据库中取不到节点权限，就从XML中取
        if(Strings.isBlank(affair.getNodePolicy())){
	        //获取当前事项对应的节点
	        BPMSeeyonPolicy policy = null;

	        if(process != null){

		        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
		        	policy = process.getStart().getSeeyonPolicy();
		        }
		        else{
		        	if(activity != null){
		        		policy = activity.getSeeyonPolicy();
		        	}
		        }
		        if(policy != null){
		            nodePermissionPolicy = policy.getId();
		            if(isForm){
		            	formAppId = policy.getFormApp();
		            	formId = policy.getForm();
		            	formOperationId = policy.getOperationName();
		            	formOperationReadonly = "1".equals(policy.getFR());
		            }
		        }

	        }
        }
        else{
        	nodePermissionPolicy = affair.getNodePolicy();
        	if(isForm){
	        	formAppId = String.valueOf(affair.getFormAppId());
	        	formId = String.valueOf(affair.getFormId());
	        	formOperationId = String.valueOf(affair.getFormOperationId());
	        	formOperationReadonly = affair.isFormReadonly();
        	}
        }

        //根据affairId得到权限的处理ID
        String lenPotents=request.getParameter("lenPotent");

        //多有意见，包括当前协同的处理人员，还包括原协同的处理人意见
        java.util.Set<ColOpinion> allOpinions = summary.getOpinions();
        //协同回复意见
        java.util.Set<ColComment> allComments = summary.getComments();
        //当前协同的处理人意见
        java.util.List<ColOpinion> opinions = new java.util.ArrayList<ColOpinion>();
        //原协同的处理人意见
        java.util.Map<Integer, List<ColOpinion>> originalSignOpinion = new java.util.HashMap<Integer, List<ColOpinion>>();
        //原协同的发起人附言
        java.util.Map<Integer, List<ColOpinion>> originalSendOpinion = new java.util.HashMap<Integer, List<ColOpinion>>();

        List<Integer> originalSendOpinionKey = new java.util.ArrayList<Integer>();

        Map<Long, List<ColComment>> commentsMap = new HashMap<Long, List<ColComment>>();

        List<ColOpinion> senderOpinion = new ArrayList<ColOpinion>(1);

        ColHelper.modulateCommentOpinion(allOpinions, allComments, originalSendOpinionKey, originalSendOpinion,
        		originalSignOpinion, opinions, senderOpinion, commentsMap, orgManager, false);

        modelAndView.addObject("comments", commentsMap);
    	modelAndView.addObject("senderOpinion", senderOpinion);

    	setIsAccountAdmin(opinions) ;

    	Map<String, String> attitudes = this.metadataManager.getMetadataItemLabelMap(MetadataNameEnum.collaboration_attitude);

    	modelAndView.addObject("attitudes", attitudes);
        modelAndView.addObject("opinionSize", opinions.size());
        modelAndView.addObject("opinions", opinions);
        modelAndView.addObject("originalSignOpinion", originalSignOpinion);
        modelAndView.addObject("originalSendOpinion", originalSendOpinion);

        java.util.Collections.sort(originalSendOpinionKey);

        modelAndView.addObject("originalSendOpinionKey", originalSendOpinionKey);

        nodePermissionPolicyName = BPMSeeyonPolicy.getShowName(nodePermissionPolicy);
        if(isForm && Strings.isNotBlank(formAppId)){
        	String masterId = body.getContent();
        	LockObject lockObject = null;
        	if("Pending".equals(from)){
        		boolean hasEdit = FormHelper.hasEditType(formAppId, formId, formOperationId);
        		if(hasEdit)
        			lockObject = FormLockManager.add(summaryId,Long.parseLong(s_affairId),user.getId(), user.getLoginName(),user.getLoginTimestamp()==null?0l:user.getLoginTimestamp().getTime());
        	}
        	boolean readonly = "WaiSend".equals(from) || "Sent".equals(from) || "Done".equals(from) || "inform".equals(nodePermissionPolicy)
        					|| (lockObject!=null&&!user.getLoginName().equals(lockObject.getLoginName()))
        					|| ("Pending".equals(from) && affair!=null && affair.getState()!=3)
        					|| formOperationReadonly;
        	if(readonly)
        		modelAndView.addObject("isDesign", true);
        	String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formAppId, formId, formOperationId, masterId, s_summaryId, s_affairId, nodePermissionPolicyName,readonly);
        	if(formContent != null && formContent.indexOf("<msg>")!=-1 && formContent.indexOf("</msg>")!=-1) {
        		modelAndView.addObject("disableSign", true);
        	}
        	
        	modelAndView.addObject("formContent", formContent);
        	modelAndView.addObject("isForm", "true");
        	modelAndView.addObject("formApp", formAppId);
        	modelAndView.addObject("form", formId);
        	modelAndView.addObject("operation", formOperationId);
        	modelAndView.addObject("masterId", masterId);
        	modelAndView.addObject("lockObject", lockObject);
        }

        int state = affair.getState();
        modelAndView.addObject("affairState", state);

        Boolean isProxy = false;
        Long affairMemberId = affair.getMemberId();
        if(affairMemberId != user.getId()){
        	//判断是否是真实的代理，避免其他如暂存待办消息等传递affair错乱的问题
        	List<Long> agentToList = MemberAgentBean.getInstance().getAgentToMemberId(ApplicationCategoryEnum.collaboration.ordinal(), user.getId());
    		if(agentToList != null && !agentToList.isEmpty() && agentToList.contains(affairMemberId)){
    			isProxy = true;
    			V3xOrgMember m = orgManager.getMemberById(affairMemberId);
    			if(m != null){
    				modelAndView.addObject("ownerName", m.getName());
    			}
    		}
        }
        modelAndView.addObject("isProxy", isProxy);
        modelAndView.addObject("affairMemberId", affairMemberId);

        //流程权限所属单位
        Long flowPermAccountId = ColHelper.getFlowPermAccountId(CurrentUser.get().getLoginAccount(), summary, templeteManager);

        //流程结束不能再回复
        if(!summary.isFinshed() && !"WaiSend".equals(request.getParameter("from"))){ //待发不用判断权限
        	boolean isCanOpinion = false;
            if(affair.getSubState() == 2){ //被回退的不能回复
                isCanOpinion = false;
            }
            else if(user.getId() == summary.getStartMemberId().longValue()){	//发起人
        		isCanOpinion = affair.getSubState()!=3; //被撤销的 自己不能回复
        	}
        	else{
		        isCanOpinion = ColHelper.isActionAllowedOfNodePolicy(permissionManager, summary, nodePermissionPolicy, "Opinion", flowPermAccountId);
        	}
        	modelAndView.addObject("isCanOpinion", isCanOpinion);
        }

        if("true".equalsIgnoreCase(request.getParameter("needReadAttachment"))){
        	modelAndView.addObject("attachments", attachmentManager.getByReference(summaryId));
        }
        List<String> baseActions = permissionManager.getActionList(Constant.ConfigCategory.col_flow_perm_policy.name(), nodePermissionPolicy, "basic", flowPermAccountId);
        modelAndView.addObject("baseActions", baseActions);

        //获取打印权限
        if("doc".equals(request.getParameter("type"))) {
       	 	modelAndView.addObject("pigCanPrint", Strings.isNotBlank(lenPotents) && lenPotents.charAt(1) == '1');
        }
        Map<String, List<String>> actions = permissionManager.getActionMap(Constant.ConfigCategory.col_flow_perm_policy.name(), nodePermissionPolicy, flowPermAccountId);
        List<String> advancedActions = actions.get("advanced");
        List<String> commonActions = actions.get("common");
        modelAndView.addObject("advancedActions", advancedActions);
        modelAndView.addObject("commonActions", commonActions);
        return modelAndView;
    }

    private void setIsAccountAdmin( List<ColOpinion> senderOpinion ){
    	if(senderOpinion == null){
    		return ;
    	}
    	for(ColOpinion colOpinion : senderOpinion){
    		try{
    			V3xOrgMember v3xOrgMember = orgManager.getMemberById(colOpinion.getWriteMemberId()) ;
    			if(v3xOrgMember.getIsAdmin()){
    				colOpinion.setIsAcconutAdmin(Boolean.valueOf(true)) ;
    			}
    		}catch(Exception e){
    			log.error("", e) ;
    		}
    	}
    }
    /**
     * 得到主流程中的附件数据
     * 得到的是表单中的附件
     * --- 图片 1 ---表单关联文档 4 --- 本地文件 3
     * @param summary
     * @return
     * @throws ColException
     */
    private List<Attachment> getMainRunAtt(ColSummary summary) throws ColException{
    	if(summary == null){
    		return null ;
    	}
//    	if(summary.getNewflowType()!= null &&
//    			summary.getNewflowType().intValue() == Constant.NewflowType.child.ordinal()){
//    		List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summary.getId(), summary.getTempleteId(), Constant.NewflowType.child.ordinal()) ;
//        	if(runningList != null && runningList.size() > 0) {
//        		NewflowRunning newflowRunning = runningList.get(0) ;
//        		if(newflowRunning.getSummaryId().equals(summary.getId())){
//        			Long summerId = newflowRunning.getMainSummaryId() ;
//        			return getMainRunAtt(summerId) ;
//        		}
//        	}
//    	}
    	List<Attachment> attachments = null;
    	if(summary.getNewflowType()!= null){
    		List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summary.getId(), summary.getTempleteId(), summary.getNewflowType().intValue());
    		if(runningList != null && runningList.size() > 0) {
    			attachments = new ArrayList<Attachment>();
    			for (NewflowRunning newflowRunning : runningList) {
    				if(summary.getNewflowType().intValue() == Constant.NewflowType.child.ordinal()){
    	        		if(newflowRunning.getSummaryId().equals(summary.getId())){
    	        			Long summaryId = newflowRunning.getMainSummaryId();
    	        			attachments.addAll(getMainRunAtt(summaryId));
    	        		}
            		} else if(summary.getNewflowType().intValue() == Constant.NewflowType.main.ordinal()){
            			if(newflowRunning.getMainSummaryId().equals(summary.getId())){
    	        			Long summaryId = newflowRunning.getSummaryId();
    	        			attachments.addAll(getMainRunAtt(summaryId));
    	        		}
            		}
				}
        	}
    	}
    	return attachments;
    }

    private List<Attachment> getMainRunAtt(Long summerId){
    	List<Attachment> formAttList = new ArrayList<Attachment>() ;
    	if(summerId != null){
        	List<Attachment> list = attachmentManager.getByReference(summerId) ;
        	if(list != null){
        		for(Attachment attachment : list){
        			if(attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FormFILE.ordinal()
        					|| attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FormDOCUMENT.ordinal()
        					|| attachment.getType() == com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.IMAGE.ordinal()){
        				formAttList.add(attachment) ;
        			}
        		}
        	}
    	}
    	return formAttList;
    }

    /**
     * 表单显示协同内容和处理信�?
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView getContentForForm(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
			ModelAndView modelAndView = new ModelAndView("collaboration/collaborationTopic");
			String s_summaryId = request.getParameter("summaryId");
			String s_affairId = request.getParameter("affairId");
			String page = request.getParameter("page");
			Long summaryId = Long.parseLong(s_summaryId);
			String type = request.getParameter("type");
			modelAndView.addObject("type", type);

			boolean isStoreFlag = false;

		     Affair affair = affairManager.getById(Long.parseLong(s_affairId));
		     if(affair==null){
		    	 affair = hisAffairManager.getById(Long.parseLong(s_affairId));
		    	 isStoreFlag = affair != null;
		     }

		     if(affair==null) {
		    	 super.rendJavaScript(response, "alert('您选择的协同已经被删除');");
		    	 return null;
		     }

			ColSummary summary = null;
			if(isStoreFlag){
				summary = hisColManager.getColAllById(summaryId);
			}
			else{
				summary = colManager.getColAllById(summaryId);
			}

			if(summary==null) {
        		super.rendJavaScript(response, "alert('您选择的协同已经被删除');");
        		return null;
        	}
			modelAndView.addObject("summary", summary);

			ColBody body = summary.getFirstBody();
			
			//为预制协同模板-lxx
//	        Templete templete = (Templete)modelAndView.getModel().get("templete");
//	        if("11111122222233334444".equals(templete.getTempleteNumber())){
//	        	String tempcontent = body.getContent();
//	        	if(tempcontent.indexOf("&userno=")>-1){
//	        		Integer startuserno = tempcontent.indexOf("&userno=");
//	        		Integer enduserno = tempcontent.indexOf("&readonly=");
//	        		String username = tempcontent.substring(startuserno, enduserno);
//	        		
//	        		if(username.equals(CurrentUser.get().getName())){
//	        			tempcontent=tempcontent.replace("?readonly=0", "?readonly=1");
//	        		}else{
//	        			tempcontent=tempcontent.replace("?readonly=1", "?readonly=0");
//	        		}
//	        		body.setContent(tempcontent);
//	        	}
//	        }
	        
			modelAndView.addObject("body", body);
	        modelAndView.addObject("caseId", summary.getCaseId());
	        modelAndView.addObject("processId", summary.getProcessId());
			if("FORM".equals(body.getBodyType())){
			String masterId = body.getContent();
			//String[] formPolicy = null;
			//String nodePermissionPolicy = "";
		     String formAppId = null; //表单应用id
		     String formIds = null; //表单id
		     String formOperationId = null; //表单节点操作Id
		     String nodePermissionPolicyName = "Collaboration";

			if (s_affairId != null) {
//				是否是表单
		        boolean isForm = "FORM".equals(body.getBodyType());
		        String nodePermissionPolicy = "Collaboration";
		        //如果数据库中取不到节点权限，就从XML中取
		        if(Strings.isBlank(affair.getNodePolicy())){
			        //获取当前事项对应的节点
			        BPMProcess process = isStoreFlag ? ColHelper.getHisCaseProcess(summary.getProcessId()) : ColHelper.getCaseProcess(summary.getProcessId());
			        BPMSeeyonPolicy policy = null;
			        if(process != null){
				        int affairState = affair.getState().intValue();
				        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
				        	policy = process.getStart().getSeeyonPolicy();
				        }
				        else{
				        	policy = ColHelper.getBPMActivityByAffair(process, affair).getSeeyonPolicy();
				        }
				        if(policy != null){
				            nodePermissionPolicy = policy.getId();
				        }
				        if(isForm){
				        	formAppId = policy.getFormApp();
				        	formIds = policy.getForm();
				        	formOperationId = policy.getOperationName();
				        }
			        }
		        }
		        else{
		        	nodePermissionPolicy = affair.getNodePolicy();
		        	if(isForm){
			        	formAppId = String.valueOf(affair.getFormAppId());
			        	formIds = String.valueOf(affair.getFormId());
			        	formOperationId = String.valueOf(affair.getFormOperationId());
		        	}
		        }
		        nodePermissionPolicyName = BPMSeeyonPolicy.getShowName(nodePermissionPolicy);
		        //流程权限所属单位
		        Long flowPermAccountId = ColHelper.getFlowPermAccountId(CurrentUser.get().getLoginAccount(), summary, templeteManager);
		      //判断打印权限
		        boolean officecanPrint = false;
		        try {
			        FlowPerm fp=flowPermManager.getFlowPerm(MetadataNameEnum.col_flow_perm_policy.toString(),nodePermissionPolicy, flowPermAccountId);
			        String baseActions = fp.getBasicOperation();
			        if(baseActions.indexOf("Print")>-1){
			        	officecanPrint = true ;
			        }
				} catch (Exception e) {
					log.error(e);
				}
		        modelAndView.addObject("officecanPrint",officecanPrint);
		        //判断打印权限结束
			}
			if(formAppId != null){
			User user = CurrentUser.get();
			String from = request.getParameter("from1");
			LockObject lockObject = null;
			boolean hasEdit = false;
			//boolean readonly = "WaiSend".equals(from) || "Sent".equals(from) || "Done".equals(from) || "inform".equals(nodePermissionPolicy) || (lockObject!=null&&!user.getLoginName().equals(lockObject.getLoginName())) || ("Pending".equals(from) && affair!=null && affair.getState()!=3);
			//if(readonly)
			modelAndView.addObject("isDesign", true);
			//String formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formPolicy[0], formPolicy[1], formPolicy[2], masterId, s_summaryId, s_affairId, formPolicy[3], true);
			String formId = request.getParameter("formId");
			String operationId = request.getParameter("operationId");
			String formContent = "";
			if(!"".equals(formId) && !"null".equals(formId) && formId!=null){
				if("".equals(page) || "null".equals(page) || page ==null){
					if(formId.indexOf("|") != -1){
						formId = formId.split("\\|")[0];
						operationId = operationId.split("\\|")[0];
					}
				}else{
					formId = formId.split("\\|")[Integer.parseInt(page)];
					operationId = operationId.split("\\|")[Integer.parseInt(page)];
				}
				if("Pending".equals(from))
					hasEdit = FormHelper.hasEditType(formAppId, formId, formOperationId);
				formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formAppId, formId, operationId, masterId, s_summaryId, "", nodePermissionPolicyName, true);
			}
			else{
				if("Pending".equals(from))
					hasEdit = FormHelper.hasEditType(formAppId, formIds, formOperationId);
				formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formAppId, formIds, formOperationId, masterId, s_summaryId, s_affairId, nodePermissionPolicyName, true);
			}
			
//	        //为预制协同模板-lxx
//        	Templete templete = (Templete)modelAndView.getModel().get("templete");
//	        if("11111122222233334444".equals(templete.getTempleteNumber())){
//	        	if(formContent.indexOf("&userno=")>-1){
//	        		Integer startuserno = formContent.indexOf("&userno=");
//	        		Integer enduserno = formContent.indexOf("&readonly=");
//	        		String username = formContent.substring(startuserno, enduserno);
//	        		if(username.equals(user.getName())){
//	        			formContent = formContent.replace("?readonly=0", "?readonly=1");
//	        		}else{
//	        			formContent= formContent.replace("?readonly=1", "?readonly=0");
//	        		}
//	        	}
//	        }
			
			if(hasEdit)
				lockObject = FormLockManager.add(summaryId,Long.parseLong(s_affairId),user.getId(), user.getLoginName(),user.getLoginTimestamp()==null?0l:user.getLoginTimestamp().getTime());
			modelAndView.addObject("formContent", formContent);
			modelAndView.addObject("isForm", "true");
			modelAndView.addObject("formApp", formAppId);
			modelAndView.addObject("form", formIds);
			modelAndView.addObject("operation", formOperationId);
			modelAndView.addObject("masterId", masterId);
			modelAndView.addObject("lockObject", lockObject);
			}
			}

			//多有意见，包括当前协同的处理人员，还包括原协同的处理人意�?
			java.util.Set<ColOpinion> allOpinions = summary.getOpinions();

			java.util.Set<ColComment> allComments = summary.getComments();
			//当前协同的处理人意见
			java.util.List<ColOpinion> opinions = new java.util.ArrayList<ColOpinion>();
			//原协同的处理人意见
			java.util.Map<Integer, List<ColOpinion>> originalSignOpinion = new java.util.HashMap<Integer, List<ColOpinion>>();
			//原协同的发起人附言
			java.util.Map<Integer, List<ColOpinion>> originalSendOpinion = new java.util.HashMap<Integer, List<ColOpinion>>();

			List<Integer> originalSendOpinionKey = new java.util.ArrayList<Integer>();

			Map<Long, List<ColComment>> commentsMap = new HashMap<Long, List<ColComment>>();

			List<ColOpinion> senderOpinion = new ArrayList<ColOpinion>(1);

			ColHelper.modulateCommentOpinion(allOpinions, allComments, originalSendOpinionKey, originalSendOpinion,
			originalSignOpinion, opinions, senderOpinion, commentsMap, orgManager, false);

			modelAndView.addObject("comments", commentsMap);
			modelAndView.addObject("senderOpinion", senderOpinion);

			Map<String, String> attitudes = this.metadataManager.getMetadataItemLabelMap(MetadataNameEnum.collaboration_attitude);

			modelAndView.addObject("attitudes", attitudes);
			modelAndView.addObject("opinionSize", opinions.size());
			modelAndView.addObject("opinions", opinions);
			modelAndView.addObject("originalSignOpinion", originalSignOpinion);
			modelAndView.addObject("originalSendOpinion", originalSendOpinion);

			java.util.Collections.sort(originalSendOpinionKey);

			modelAndView.addObject("originalSendOpinionKey", originalSendOpinionKey);

			long userId = CurrentUser.get().getId();

			modelAndView.addObject("affairId", s_affairId);

			int state = affair.getState();
			modelAndView.addObject("affairState", state);

			Boolean isProxy = false;
			if(affair.getMemberId() != userId){
			isProxy = true;
			}
			modelAndView.addObject("isProxy", isProxy);
			//流程结束不能再回复
			if(!summary.isFinshed() && !"WaiSend".equals(request.getParameter("from"))){ //待发不用判断权限
			boolean isCanOpinion = false;
			/**
			if(userId == summary.getStartMemberId().longValue()){	//发起人永远有权限
				isCanOpinion = true;
			}
			else{
				String nodePermissionPolicy = "collaboration";
				nodePermissionPolicy = colManager.getPolicyByAffair(affair);
				isCanOpinion = colPermissionControlManager.isActionAllowed(summary, nodePermissionPolicy, null, "Opinion");
			}
			**/
			modelAndView.addObject("isCanOpinion", isCanOpinion);
			}

			if("true".equalsIgnoreCase(request.getParameter("needReadAttachment"))){
			modelAndView.addObject("attachments", attachmentManager.getByReference(summaryId));
			}

			modelAndView.addObject("bodytype", summary.getBodyType());


			if ("doc".equals(type)) {
				String lenPotent = request.getParameter("lenPotent");
				modelAndView.addObject("pigCanPrint", Strings.isNotBlank(lenPotent) && lenPotent.charAt(1) == '1');
			}

			return modelAndView;
	}


    /*
      * 默认方法为查看协同get()
      *
      * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest,
      *      javax.servlet.http.HttpServletResponse)
      */
    @Override
    public ModelAndView index(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        return null;
    }

    /**
     * 页面框架
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView collaborationFrame(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		return new ModelAndView("collaboration/collaborationBorderFrame");
	}


	/**
	* 页面框架 Border
	*
	* @param request
	* @param response
	* @return
	* @throws Exception
	*/
	public ModelAndView collaborationBorderFrame(HttpServletRequest request,
	            HttpServletResponse response) throws Exception {
		return new ModelAndView("collaboration/collaborationFrame");
	}

    /**
     * 撤消流程，已发变待发
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView repeal(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {

        User user = CurrentUser.get();
        String[] _summaryIds = new String[]{};
        String page = request.getParameter("page");
        String repealComment = null; //撤销附言
        ColOpinion signOpinion = new ColOpinion();
        boolean isSaveOpinion = false;
        if("showDiagram".equals(page)){
        	//保存撤销时的意见,附件
        	String _summaryId = request.getParameter("_summaryId");
        	_summaryIds = new String[]{_summaryId};
        	bind(request, signOpinion);
            String afterSign = request.getParameter("afterSign");
            signOpinion.isDeleteImmediate = "delete".equals(afterSign);
            signOpinion.affairIsTrack = "track".equals(afterSign);
            signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
            signOpinion.setIdIfNew();

            /**
             * 保存附件:
             * 需要先要删除草稿的时候附件，然后再保存，否则会报ID重复；
             * 所以deleteDraftOpinion必须放到attachmentManager.create方法的前面，挪动位置的一定要注意到这个先后顺序
             * 已经报了N次由于这个原因引起的BUG了，注意！！！
             */
	        String draftOpinionId = request.getParameter("draftOpinionId");
	        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
	        	this.deleteDraftOpinion(Long.parseLong(draftOpinionId));
	        }

            //保存附件
            this.attachmentManager.create(ApplicationCategoryEnum.collaboration, Long.parseLong(_summaryIds[0]), signOpinion.getId(), request);
            isSaveOpinion = true;
            repealComment = signOpinion.getContent();
        }
        else if("workflowManager".equals(page)){
        	String[] summaryIdArr = {request.getParameter("summaryId")};
        	_summaryIds = summaryIdArr;
        	repealComment = request.getParameter("repealComment");
        }
        else{
        	_summaryIds = request.getParameterValues("id");
        	repealComment = request.getParameter("repealComment");
        }
        String repealCommentTOHTML = repealComment;
        String colSubject = "";
        boolean isRelieveLock = true;
        try{
	        String info = "";
	        int result = 0;
	        for (int i = 0; i < _summaryIds.length; i++) {
	            Long summaryId = Long.parseLong(_summaryIds[i]);
	            //检查同步锁
	            if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.cancel,
	            		response, ApplicationCategoryEnum.collaboration)){
	            	isRelieveLock = false;
	            	return null;
	            }
	            ColSummary summary = colManager.getColSummaryById(summaryId, true);
	            if( null== summary.getCaseId() && summary.getState()==0 ){//兼容处理下
	            	Affair sendAffair= affairManager.getCollaborationSenderAffair(summary.getId());
	            	if(sendAffair.getState()==StateEnum.col_sent.key()){//不是待发状态，看是否可以补数据
	            		log.info("caseId为空，根据processId做兼容处理。");
	            		CaseManager caseManager= (CaseManager)ApplicationContextHolder.getBean("caseManager");
//	            		Long caseId= caseManager.moveHistoryCaseToRun(summary.getProcessId());
	            		Long caseId= caseManager.getHistoryCaseByProcessId(summary.getProcessId());
	            		summary.setCaseId(caseId);
	            	}
	            }
	            result = colManager.cancelSummary(user.getId(), summary, signOpinion, isSaveOpinion, repealCommentTOHTML);
	            colManager.clearSession();
                colSubject = summary.getSubject();
                //发送消息给督办人，更新督办状态，并删除督办日志、删除督办记录、删除催办次数
	            this.colSuperviseManager.updateStatusAndNoticeSupervisor(summaryId, Constant.superviseType.summary.ordinal(),
	            		ApplicationCategoryEnum.collaboration, colSubject,
	            		user.getId(), user.getName(), Constant.superviseState.waitSupervise.ordinal(), null, repealCommentTOHTML, summary.getForwardMember());
	            if(result == -1){
                    info = "《" + colSubject + "》";
	                WebUtil.saveAlert("col.state.system.running", info);
	            }
                else if(result == -2){// 新流程已结束不能撤销，已在manager里提示
//                  如果该流程触发的新流程已结束，不能撤销
                    String subject = "《" + colSubject + "》";
                    WebUtil.saveAlert("col.repeal.newflowEnd.alert", subject);
                }
                else if(result == 1) {
                    info = "《" + colSubject + "》";
	                WebUtil.saveAlert("col.state.end.alert", info);
	            }
                else{
	            	ColBody body = summary.getFirstBody();
	                String bodyType = body.getBodyType();
	                if("FORM".equals(bodyType)){
	                	Templete template = templeteManager.get(summary.getTempleteId());
	                    if(template != null){
	                    	if(template.getFormParentId()!=null)
	                    		template = templeteManager.get(template.getFormParentId());
	                    	FormContent formContent = (FormContent)XMLCoder.decoder(template.getBody());
	                    	FormBody formBody = formContent.getForms().get(0);
	                    	try{
	                    	String state = "0";
	                    	//如果来自关联表单，则不更改表单状态
	                        if(summary.getParentformSummaryId() !=null || (summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.child.ordinal())){
	                        	state = "";
                            }
	                        BPMProcess process = BPMProcess.fromXML(template.getWorkflow());
	                        String[] formInfo = FormHelper.getFormPolicy(process);
	    	        		if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue()){
	    	        			FormHelper.stopOrRepealOrSubmitOrZCDDOperForWithholding(new Long(formBody.getFormApp()), summaryId, new Long(body.getContent()),summary.getSubject());
	    	        		}
	    	        		FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), formBody.getFormApp(), formBody.getForm(), formInfo[2], body.getContent(), null, "rollback",state, "update","repeal",Constant.FormVouch.vouchDefault.getKey());
                            formDaoManager.UpdateDataState(user.getId(), summary.getId(), "0", summary.getFormRecordId(), "rollback", "repeal");
	                    	}
	                    	catch(DataDefineException e1){
	                    	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
	                    		  PrintWriter out = response.getWriter();
	            	              	out.println("<script>");
	            	                out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
	            	              	out.println("history.back();");
	            	              	out.println("</script>");
	            	              	return null;
	                    	  }
	                  		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
	                  			  throw new RuntimeException(e1.getMessage());
	                  		  else{
	                  			  log.error("保存表单数据时发生错误", e1);
	                  			  //throw new RuntimeException("不能保存");
	                  			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	                  		  }

	                    	  }catch(Exception e){
	                  		  log.error("保存表单数据时发生错误", e);
	                  		  //throw new RuntimeException("不能保存");
	                  		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	                  	      }
	                    	Long key = Long.valueOf(Thread.currentThread().getId());
	                    	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
	                    		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
	                    		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
	                    	}
	                    }
	                }
	            }
	            colRelationAuthorityManager.delete(summary.getId(), true);
	            //记录应用日志
	            appLogManager.insertLog(user, AppLogAction.Coll_Repeal, user.getName(), colSubject);
	            //撤销流程事件
	            CollaborationCancelEvent event = new CollaborationCancelEvent(this);
	            event.setSummaryId(summary.getId());
	            event.setUserId(user.getId());
	            event.setMessage(repealCommentTOHTML);
	            EventDispatcher.fireEvent(event);
	            colManager.colDelLock(summary);
	        }

            try {
            	 for (int i = 0; i < _summaryIds.length; i++) {
     	        	//删除事项删除全文检索库
     	    		updateIndexManager.getIndexManager().deleteFromIndex(ApplicationCategoryEnum.collaboration, Long.parseLong(_summaryIds[i]));
     	        }
			} catch (Exception e) {
				log.error("", e);
			}


	        PrintWriter out = response.getWriter();
	        if("workflowManager".equals(page)){
	        	out.println("<script>");
		    	out.println("if(window.dialogArguments){"); //弹出
		    	out.println("  window.returnValue = \"true\";");
		    	out.println("  window.close();");
		    	out.println("}else{");
		    	out.println("  parent.ok();");
		    	out.println("}");
		    	out.println("</script>");
		    	out.close();
		    	return null;
	        }else{
	        	out.println("<script>");
		    	out.println("if(window.dialogArguments){"); //弹出
		    	out.println("	parent.closeWindow();");
		    	out.println("}else{");
		    	out.println("	parent.getA8Top().reFlesh();");
		    	out.println("}");
		    	out.println("</script>");
		    	out.close();
		    	return null;
	        }
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock){
        		for(int i=0; i<_summaryIds.length; i++){
        			ColLock.getInstance().removeLock(Long.parseLong(_summaryIds[i]));
        		}
        	}
//          表单解锁，冗余处理，防止窗口关闭时的事件未执行
    		if("true".equals(request.getParameter("removeFormLock")) && _summaryIds[0] != null){
    			FormLockManager.remove(Long.parseLong(_summaryIds[0]));
    		}
        }
        return null;
    }

    public ModelAndView preColAssign(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	ModelAndView mav = null;

    	if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
    		mav = new ModelAndView("collaboration/preColAssign");
    	}else{//ipad
    		mav = new ModelAndView("collaboration/preColAssignIpad");
    	}

    	String _summaryId = request.getParameter("summaryId");
        String _affairId = request.getParameter("affairId");
        Long affairId = Long.parseLong(_affairId);
        Long summaryId = Long.parseLong(_summaryId);
        String processId = request.getParameter("processId");

        PrintWriter out = response.getWriter();
        Affair affair = affairManager.getById(affairId);
        if(affair == null || affair.getState() != StateEnum.col_pending.key()){
			String msg=ColHelper.getErrorMsgByAffair(affair);
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
        FlowPerm flowPerm = null ;
        if(Strings.isNotBlank(request.getParameter("from")) && "edoc".equals(request.getParameter("from"))){
        	EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
        	MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnum(summary.getEdocType());
    		Long senderId = summary.getStartUserId();
            V3xOrgMember sender = orgManager.getMemberById(senderId);
            Long flowPermAccountId  = EdocHelper.getFlowPermAccountId(summary ,sender.getOrgAccountId(),templeteManager);
	        flowPerm=flowPermManager.getFlowPerm(edocTypeEnum.name(), "huiqian", flowPermAccountId);
	        mav.addObject("secretLevel", summary.getEdocSecretLevel());//成发集团项目
        }else{            
	        Long senderId = affair.getSenderId();
	        V3xOrgMember sender = orgManager.getMemberById(senderId);
            ColSummary summary = colManager.getColSummaryById(summaryId, false);
        	Long flowPermAccountId = ColHelper.getFlowPermAccountId(sender.getOrgAccountId(), summary, templeteManager);
        	SeeyonPolicy policy = ColHelper.getPolicyByAffair(affair);
        	flowPerm = flowPermManager.getFlowPerm("col_flow_perm_policy",  policy.getId(), flowPermAccountId);
        	mav.addObject("secretLevel", summary.getSecretLevel());//成发集团项目
        }	
    	mav.addObject("nodePolicy", flowPerm) ;
		mav.addObject("summaryId", summaryId);
		mav.addObject("affairId", affairId);
		mav.addObject("processId", processId);
		mav.addObject("from", request.getParameter("from"));
		mav.addObject("appName", request.getParameter("appName"));
    	return mav ;
    }

    public ModelAndView preInsertPeople(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

    	ModelAndView mav = null;

    	if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
    		mav = new ModelAndView("collaboration/insertPeople");
    	}else{//ipad
    		mav = new ModelAndView("collaboration/insertPeopleIpad");
    	}
    	User user = CurrentUser.get();

    	String _summaryId = request.getParameter("summaryId");
        String _affairId = request.getParameter("affairId");
        Long affairId = Long.parseLong(_affairId);
        Long summaryId = Long.parseLong(_summaryId);
        String appName = request.getParameter("appName");
        String isForm = request.getParameter("isForm");
        String processId = request.getParameter("processId");

        PrintWriter out = response.getWriter();
        Affair affair = affairManager.getById(affairId);
        if(affair == null || affair.getState() != StateEnum.col_pending.key()){
			String msg=ColHelper.getErrorMsgByAffair(affair);
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

		List<String> desList = null;
		String nodeMetadataName = "";
		int flowPermType = 1;  //流程权限的类型 0:协同 .  1:公文（默认是公文）
		String bundleName = "com.seeyon.v3x.edoc.resources.i18n.EdocResource"; //指定国际化资源文件,默认为公文
		String defaultPolicyId = null;
		if("collaboration".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.col_flow_perm_policy.name();
			bundleName="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";
			flowPermType = com.seeyon.v3x.flowperm.util.Constants.F_TYPE_COLLABORATION; //指定流程的权限类型为协同
			defaultPolicyId = BPMSeeyonPolicy.SEEYON_POLICY_COLLABORATE.getId();

		}else if("0".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_send_permission_policy.name();
			defaultPolicyId = BPMSeeyonPolicy.EDOC_POLICY_SHENPI.getId();
		}else if("1".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_rec_permission_policy.name();
			//branches_a8_v350_r_gov GOV-2143  唐桂林修改政务收文默认节点权限为审批 start
			boolean isGovEdoc = (Boolean)SysFlag.is_gov_only.getFlag() && SystemEnvironment.hasPlugin("edoc");
			if(isGovEdoc) {
				defaultPolicyId = BPMSeeyonPolicy.EDOC_POLICY_SHENPI.getId();
			} else {
				defaultPolicyId = BPMSeeyonPolicy.EDOC_POLICY_YUEDU.getId();
			}
			//branches_a8_v350_r_gov GOV-2506  唐桂林修改政务收文默认节点权限为审批  end
		}else if("2".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_qianbao_permission_policy.name();
			defaultPolicyId = BPMSeeyonPolicy.EDOC_POLICY_SHENPI.getId();
		}

		//获取对应应用类型的所有节点策略
		List <FlowPerm> nodePolicyList = null;
//      权限策略改动，需要充分验证(Mazc 2009-4-13)
//      自定义的流程取发起者所在单位（协同的关联单位）的节点权限， 模板的取模板所在单位的节点权限
        Long flowPermAccountId = user.getLoginAccount();
        ColSummary summary = colManager.getColSummaryById(summaryId, false);
        if("collaboration".equalsIgnoreCase(appName) && summary != null){
        	flowPermAccountId = ColHelper.getFlowPermAccountId(flowPermAccountId, summary, templeteManager);
        	mav.addObject("secretLevel", summary.getSecretLevel());//成发集团项目
        }
        else {//公文，包括发文，收文，签报
			EdocSummary edocSummary = edocManager.getEdocSummaryById(summaryId, false);
			if(edocSummary != null) {
				if(edocSummary.getTempleteId() != null) {
					Templete templete = templeteManager.get(edocSummary.getTempleteId());
					if(templete != null){
						flowPermAccountId = templete.getOrgAccountId();
					}
				}else{
					if(edocSummary.getOrgAccountId() != null){
						flowPermAccountId = edocSummary.getOrgAccountId();
					}
				}
			}
			mav.addObject("secretLevel", edocSummary.getEdocSecretLevel());//成发集团项目
		}
		nodePolicyList = flowPermManager.getFlowpermsByStatus(nodeMetadataName, FlowPerm.Node_isActive, false, flowPermType, flowPermAccountId);
		mav.addObject("nodePolicyList",nodePolicyList);
		List <FlowPerm> tempNodePolicyList = new ArrayList<FlowPerm>();
		if(!"true".equals(isForm)){
			for(FlowPerm flowPerm : nodePolicyList){
				if("formaudit".equals(flowPerm.getName()) || "vouch".equals(flowPerm.getName())){
					tempNodePolicyList.add(flowPerm);
				}
			}
			nodePolicyList.removeAll(tempNodePolicyList);
		}
		//获取对应应用下的所有节点策略说明
		desList = new ArrayList<String>();
		for(FlowPerm fp : nodePolicyList)
		{
			desList.add(fp.getName()+"split"+StringEscapeUtils.escapeJavaScript(fp.getDescription()));
		}

		mav.addObject("bundleName", bundleName);
		mav.addObject("nodePolicyList", nodePolicyList);
		mav.addObject("desList", desList);
		mav.addObject("summaryId", summaryId);
		mav.addObject("affairId", affairId);
		mav.addObject("defaultPolicyId", defaultPolicyId);
		mav.addObject("appName", appName);
		mav.addObject("processId", processId);
		mav.addObject("isFormReadonly", affair.isFormReadonly());

        return mav;

    }

    //加签
    public ModelAndView insertPeople(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
    	String processMode = request.getParameter("processMode");
        String _summaryId = request.getParameter("summaryId");
        String _affairId = request.getParameter("affairId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        String policyId = request.getParameter("policyId");
        String processId = request.getParameter("processId");
        String policyName = request.getParameter("policyName");
        String appName = request.getParameter("appName");
        boolean isFormOperationReadonly = "1".equals(request.getParameter("formOperationPolicy"));

//      权限策略改动，需要充分验证(Mazc 2009-4-13)
        //自定义的流程取发起者所在单位（协同的关联单位）的节点权限， 模板的取模板所在单位的节点权限



        ColSummary colSummary = null;
        EdocSummary edocSummary = null;
        String fCategoryName = MetadataNameEnum.col_flow_perm_policy.name();
        Long templeteId = 0L;
        Long summaryAccountId = 0L;
        Long caseId = null;
        String summaryProcessId = "";   //TODO ： 这个变量是否可以去掉，直接取前台传过来的PROCESSid
        if("collaboration".equals(appName)){
        	colSummary = colManager.getColSummaryById(summaryId, false);
        	if(colSummary == null) return null;
        	templeteId = colSummary.getTempleteId();
        	summaryAccountId = colSummary.getOrgAccountId();
        	caseId = colSummary.getCaseId();
        	summaryProcessId = colSummary.getProcessId();
        }else {
        	edocSummary = edocManager.getEdocSummaryById(summaryId, false);
        	if(edocSummary == null ) return null;
        	templeteId = edocSummary.getTempleteId();
        	summaryAccountId = edocSummary.getOrgAccountId();
        	fCategoryName = EdocHelper.getCategoryName(edocSummary.getEdocType());
        	caseId = edocSummary.getCaseId();
        	summaryProcessId = edocSummary.getProcessId();
        }
        Long flowPermAccountId = ColHelper.getFlowPermAccountId(user.getLoginAccount(), templeteId,summaryAccountId, templeteManager);
        FlowPerm perm = flowPermManager.getFlowPerm(fCategoryName, policyId, flowPermAccountId);
        if(perm == null){
        	PrintWriter out = response.getWriter();
			String msg = Constant.getString4CurrentUser("nodePolicy.not.existence");
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
        }else{
        	try {
        		flowPermManager.refFlowPerm(perm.getFlowPermId(), Long.parseLong(user.getLoginAccount()+""), 1);
			} catch (Exception e) {
				log.error("", e);
			}
        }

        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.insertPeople,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        PrintWriter out = response.getWriter();
	        Affair affair = affairManager.getById(affairId);
	        if(affair.getState() != StateEnum.col_pending.key()){
				String msg = ColHelper.getErrorMsgByAffair(affair);
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

	        //保存选人界面返回的信�xi息
	        FlowData flowData = FlowData.flowdataFromRequest();
	        String node_process_mode = "";
	        if(flowData.getSeeyonPolicy() != null ){
	        	node_process_mode = flowData.getSeeyonPolicy().getProcessMode() ;
	        }

	        BPMSeeyonPolicy seeyonPolicy = new BPMSeeyonPolicy(policyId, policyName);
	        if(Strings.isNotBlank(node_process_mode))
	        		seeyonPolicy.setProcessMode(node_process_mode) ;
	        //FIXME
	        if(isFormOperationReadonly){
	        	seeyonPolicy.setFR("1");
	        }
	        flowData.setSeeyonPolicy(seeyonPolicy);
	        int iFlowType = 0;
	        switch(Integer.parseInt(processMode)){
	            case 0 : iFlowType = FlowData.FLOWTYPE_SERIAL;
	            break;
	            case 1 : iFlowType = FlowData.FLOWTYPE_PARALLEL;
	            break;
	            case 2 : iFlowType = FlowData.FLOWTYPE_MULTIPLE;
	            break;
	            case 3 : iFlowType = FlowData.FLOWTYPE_COLASSIGN;
	            break;
	            case 4 : iFlowType = FlowData.FLOWTYPE_NEXTPARALLEL;
	            break;
	        }
	        flowData.setType(iFlowType);
	        if (flowData.getPeople().isEmpty()) {
	        	String info = Constant.getString4CurrentUser("please.select.one");
	        	out.println("<script>");
	        	out.print("alert(\"" + StringEscapeUtils.escapeJavaScript(info) + "\"); ");
	        	out.println("</script>");
	        	out.close();
	        	return null;
	        }

	        //判断当前节点下一节点的节点类型
	    	BPMProcess process = null;
	        try {
	        	process = ColHelper.getModifyingProcess(processId, user.getId()+"");
	        	if(process == null)
	        		process = BPMProcess.fromXML(ColHelper.getRunningProcessByCaseId(caseId).toXML());

	        	if(iFlowType == FlowData.FLOWTYPE_NEXTPARALLEL){
		    		WorkItem workitem = ColHelper.getWorkItemById(affair.getSubObjectId());
		    		BPMActivity currentActivity = process.getActivityById(workitem.getActivityId());
		    		List downTransitions = currentActivity.getDownTransitions();
		    		BPMTransition nextTran = (BPMTransition) downTransitions.get(0);
		    		BPMAbstractNode childNode = nextTran.getTo();
		    		BPMAbstractNode.NodeType nodeType = childNode.getNodeType();
		    		if(nodeType == BPMAbstractNode.NodeType.end ||
							nodeType == BPMAbstractNode.NodeType.join){
		    			String info = Constant.getString4CurrentUser("nextNode.is.specialNode");
			        	out.println("<script>");
			        	out.print("alert(\"" + StringEscapeUtils.escapeJavaScript(info) + "\");\n ");
			        	out.print("parent.enAbleButton();");
			        	out.println("</script>");
			        	out.close();
						return null;
		    		}
	        	}
	    	}catch(ColException e){
	    		log.error("", e);
	    	}

	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if("collaboration".equals(appName)){ //TODO:这个分支是否可以合并
	        	colManager.insertPeople(colSummary, affair, flowData, process, user, isFormOperationReadonly);
	        }else{
	        	edocManager.insertPeople(edocSummary, affair, flowData, process, user.getId()+"","insertPeople");
	        }


            if (caseId != null) {
                caseLogXML = colManager.getCaseLogXML(caseId);
                caseProcessXML = colManager.getModifyingProcessXML(summaryProcessId);
                caseWorkItemLogXML = colManager.getCaseWorkItemLogXML(caseId);
            }
            else if (Strings.isNotBlank(summaryProcessId)) {
                caseProcessXML = colManager.getProcessXML(processId);
            }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);
	        String process_desc_by = FlowData.DESC_BY_XML;
	        out.println("<script>");
        	if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
        		out.println("parent.window.dialogArguments.caseProcessXML = \"" + caseProcessXML + "\";");
		        out.println("parent.window.dialogArguments.caseLogXML = \"" + caseLogXML + "\";");
		        out.println("parent.window.dialogArguments.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
		        out.println("parent.window.dialogArguments.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
		        out.println("parent.window.dialogArguments.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
		        out.println("parent.window.dialogArguments.selectInsertPeopleOK();");
		        out.println("parent.window.close();");
        	}else{//ipad 公文协同页面呢结构层级不一样
        		 if("collaboration".equals(appName)){
	        		out.println("parent.parent.detailRightFrame.caseProcessXML = \"" + caseProcessXML + "\";");
			        out.println("parent.parent.detailRightFrame.caseLogXML = \"" + caseLogXML + "\";");
			        out.println("parent.parent.detailRightFrame.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
			        out.println("parent.parent.detailRightFrame.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
			        out.println("parent.parent.detailRightFrame.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
			        out.println("parent.parent.detailRightFrame.selectInsertPeopleOK();");
			        out.println("parent.parent.$('#insertPeopleWin').dialog('destroy');");
        		 }else{
 	        		out.println("parent.parent.caseProcessXML = \"" + caseProcessXML + "\";");
			        out.println("parent.parent.caseLogXML = \"" + caseLogXML + "\";");
			        out.println("parent.parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
			        out.println("parent.parent.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
			        out.println("parent.parent.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
			        out.println("parent.parent.selectInsertPeopleOK();");
			        out.println("parent.parent.$('#insertPeopleWin').dialog('destroy');");
        		 }
        	}
	        out.println("</script>");
	        out.close();
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        }
        return null;
    }

    //减签前的人员检查
    public ModelAndView preDeletePeople(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
    	ModelAndView mv = null;

    	if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
    		mv = new ModelAndView("collaboration/decreaseNodes");
    	}else{
    		mv = new ModelAndView("collaboration/decreaseNodesIpad");
    	}

        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        String processId = request.getParameter("processId");

        Affair affair = affairManager.getById(affairId);
        if(affair == null || affair.getState() != StateEnum.col_pending.key()){
        	PrintWriter out = response.getWriter();
			String msg=ColHelper.getErrorMsgByAffair(affair);
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
        FlowData flowData = new FlowData();
        if(affair.getApp() == ApplicationCategoryEnum.collaboration.key()){
        	flowData = colManager.preDeletePeople(summaryId, affairId, processId, user.getId()+"");
        }else{
        	flowData = edocManager.preDeletePeople(summaryId, affairId, processId, user.getId()+"");
        }

        mv.addObject("summmaryId", summaryId);
        mv.addObject("affairId", affairId);
        mv.addObject("flowData", flowData);
        mv.addObject("processId", processId);
        return mv;
    }

    //减签
    public ModelAndView deletePeople(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.deletePeople,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        PrintWriter out = response.getWriter();
	        Affair affair = affairManager.getById(affairId);
	        if(affair.getState() != StateEnum.col_pending.key()){
				String msg=ColHelper.getErrorMsgByAffair(affair);
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

	        //保存选人界面返回的信息
	        FlowData flowData = FlowData.flowdataFromRequest();

	        if (flowData.getPeople().isEmpty()) {
	            throw new RuntimeException("should select one person at least");
	        }
	        List<Party> people = flowData.getPeople();
	        Long caseId = null;
	        String summaryProcessId = null;
	    	EdocSummary edocSummary = null;
	    	ColSummary colSummary = null;
	       if(affair.getApp() == ApplicationCategoryEnum.collaboration.key()){
		        colSummary = colManager.getColSummaryById(summaryId, false);
		        // 降低性能保证正确，一个一个减签
		        for(Party party:people)
		        {
		        	List<Party> list = new ArrayList<Party>();
		        	list.add(party);
					colManager.deletePeople(colSummary, affair, list, user.getId()+"");
		        }
		        //一个一个的减，一下子发送信息
		        ColHelper.saveDeletePeopleMessage(user.getId(),colSummary,affair,people);
		        if(colSummary != null){
		        	caseId = colSummary.getCaseId();
		        	summaryProcessId = colSummary.getProcessId();
		        }
	       }else{
	    	   	edocManager.deletePeople(summaryId, affairId, people, user.getId()+"");
	    	   	edocSummary = edocManager.getEdocSummaryById(summaryId, false);
		        if (flowData.getPeople().isEmpty()) {
		            throw new RuntimeException("should select one person at least");
		        }
		        if(edocSummary != null){
		        	caseId = edocSummary.getCaseId();
		        	summaryProcessId = edocSummary.getProcessId();
		        }
	       }

	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (colSummary != null || edocSummary != null){
	            if (caseId != null) {
	                caseLogXML = colManager.getCaseLogXML(caseId);
	                caseProcessXML = colManager.getModifyingProcessXML(summaryProcessId);
	                caseWorkItemLogXML = colManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (Strings.isNotBlank(summaryProcessId)) {
	                caseProcessXML = colManager.getProcessXML(summaryProcessId);
	            }
	        }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

	        String process_desc_by = FlowData.DESC_BY_XML;
	        out.println("<script>");
	        out.println("parent.caseProcessXML = \"" + caseProcessXML + "\";");
	        out.println("parent.caseLogXML = \"" + caseLogXML + "\";");
	        out.println("parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
	        out.println("parent.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
	        out.println("parent.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
	        out.println("parent.selectInsertPeopleOK();");
	        out.println("</script>");
	        out.close();
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        }
        return null;
        //return super.redirectModelAndView("/collaboration.do?method=showDiagram&summaryId=" + _summaryId + "&affairId=" + _affairId + "&from=Pending&preAction=deletePeople");
    }

    /**
     * 设置节点属性
     *
     * @author jincm 2008-3-28
     * @param
     * @param
     * @return ModelAndView
     */
    public ModelAndView selectPolicy(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();

		ModelAndView mav = new ModelAndView("/collaboration/selectPolicy");
		String nodeName = request.getParameter("nodeName");
		String policyId = request.getParameter("policyName");
		String dealTerm = request.getParameter("dealTerm");
		String remindTime = request.getParameter("remindTime");
		String processMode = request.getParameter("processMode");
		String isTemplete = request.getParameter("isTemplete");
		String appName = request.getParameter("appName");
        String nodeType = request.getParameter("nodeType");
        String isEditor = request.getParameter("isEditor");
        String nodePolicyName = request.getParameter("nodePolicyName");
        String defaultPolicyId = request.getParameter("defaultPolicyId");
        //发起节点显示当前用户名还是‘发起者’，用于表单绑定时显示用
        String showSenderName = request.getParameter("showSenderName");
        String partyType = request.getParameter("partyType");
        String formField = request.getParameter("formfiled");
        String summaryId = request.getParameter("summaryId");
        String nodeId = request.getParameter("nodeId");
        String dealTermType = request.getParameter("dealTermType");
        String dealTermUserId = request.getParameter("dealTermUserId");
        String dealTermUserName = request.getParameter("dealTermUserName");
        String templeteId = request.getParameter("templeteId");

		//表单模板绑定
		String formApp = request.getParameter("formApp");
		String formName = request.getParameter("form");
		String operationName = request.getParameter("operationName");
		boolean isFormTemplate = formApp!=null&&!"".equals(formApp)&&!"null".equals(formApp);
		if(isFormTemplate)
			appName = "form";
		boolean isEdoc = false;
		boolean isFromTemplete=false;
		if(Strings.isNotBlank(templeteId)){
			isFromTemplete=true;
		}
		Boolean _isTemplete = false;
		if("true".equals(isTemplete)){
			_isTemplete = true;
		}
		String bundleName="com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		List<String> desList=null;
		String nodeMetadataName="";
		int flowPermType = 1;  //流程权限的类型 0:协同 .  1:公文（默认是公文）
		Long flowPermAccountId = null;
		if("collaboration".equalsIgnoreCase(appName) || "form".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.col_flow_perm_policy.name();
			bundleName="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";
			flowPermType = com.seeyon.v3x.flowperm.util.Constants.F_TYPE_COLLABORATION; //指定流程的权限类型为协同
			if(isFormTemplate){
				long formAppId = Long.parseLong(formApp);
				HttpSession session = request.getSession(false);
				Hashtable hash = FormHelper.getDefault(session);
				mav.addObject("isFormTemplate", Boolean.valueOf(isFormTemplate));
				mav.addObject("defaultFormName", hash.get("formName"));
				mav.addObject("defaultFormId", hash.get("formId"));
				mav.addObject("defaultFirstNodeOperationName", hash.get("firstNodeOperationName"));
				mav.addObject("defaultFirstNodeOperationId", hash.get("firstNodeOperationId"));
				mav.addObject("defaultOtherNodeOperationName", hash.get("otherNodeOperationName"));
				mav.addObject("defaultOtherNodeOperationId", hash.get("otherNodeOperationId"));
				hash = FormHelper.getFormAndOperation(formAppId, nodeType,session);
				mav.addObject("displays", hash.get("displays"));
				mav.addObject("values", hash.get("values"));
				mav.addObject("formFieldString", FormHelper.getFormFieldOptionString(session,formField)) ;
			}else{
				if(Strings.isNotBlank(summaryId)){
					ColSummary summary = colManager.getColSummaryById(Long.valueOf(summaryId), false);
					//当为表单协同时,单位管理员以及督办人修改流程图时显示表单绑定
			        if((formApp == null || "null".equals(formApp)) && summary!=null){
			        	if("FORM".equals(summary.getBodyType())){
			        		formApp = summary.getFormAppId().toString();
			        		Hashtable<String,List<String>> hash = new Hashtable<String,List<String>>();
			        		hash = FormHelper.getFormAndOperation(Long.valueOf(formApp), nodeType);
							mav.addObject("displays", hash.get("displays"));
							mav.addObject("values", hash.get("values"));
			        	}
			        }
				}
			}
			if(Strings.isNotBlank(summaryId)){
				ColSummary summary = colManager.getColSummaryById(Long.valueOf(summaryId), false);
				if(summary!=null){
					flowPermAccountId = ColHelper.getFlowPermAccountId(user.getLoginAccount(),summary, templeteManager);
					if(summary.getTempleteId()!=null){
						isFromTemplete=true;
					}
				}else{
					flowPermAccountId = user.getLoginAccount();
				}
			}else{
				flowPermAccountId = user.getLoginAccount();
			}
		}else {
			if("sendEdoc".equalsIgnoreCase(appName) || "edocSend".equals(appName)){
				nodeMetadataName=MetadataNameEnum.edoc_send_permission_policy.name();
				isEdoc = true;
			}else if("recEdoc".equalsIgnoreCase(appName) || "edocRec".equals(appName)){
				nodeMetadataName=MetadataNameEnum.edoc_rec_permission_policy.name();
				isEdoc = true;
			}else if("signReport".equalsIgnoreCase(appName) || "edocSign".equals(appName)){
				nodeMetadataName=MetadataNameEnum.edoc_qianbao_permission_policy.name();
				isEdoc = true;
			}else if("sendInfo".equalsIgnoreCase(appName)){
				nodeMetadataName = "info_send_permission_policy";
			}

			if(Strings.isNotBlank(summaryId)){
				EdocSummary summary = edocManager.getEdocSummaryById(Long.valueOf(summaryId), false);
				if(summary!=null){
					flowPermAccountId = EdocHelper.getFlowPermAccountId(summary, user.getLoginAccount(), templeteManager);
					if(summary.getTempleteId()!=null){
						isFromTemplete=true;
					}
				}else{
					flowPermAccountId = user.getLoginAccount();
				}
			}else{
				flowPermAccountId = user.getLoginAccount();
			}

		}


		List<FlowPerm> oldNodePolicyList = null;
		oldNodePolicyList = flowPermManager.getFlowpermsByStatus(nodeMetadataName, FlowPerm.Node_isActive, false, flowPermType, flowPermAccountId);
		List<FlowPerm> nodePolicyList = new ArrayList<FlowPerm>();
		for (FlowPerm flowPerm : oldNodePolicyList) {
			if(!"form".equals(appName) && "vouch".equals(flowPerm.getName())){
				continue;
			}

			nodePolicyList.add(flowPerm);
		}
		List<FlowPerm> allDisabledNode = flowPermManager.getFlowpermsByStatus(nodeMetadataName, FlowPerm.Node_isNotActive, false, flowPermType, flowPermAccountId);
		for (FlowPerm flowPerm : allDisabledNode) {
			if (policyId.equals(flowPerm.getName())) {
				nodePolicyList.add(flowPerm);
				break;
			}
		}
		mav.addObject("nodePolicyList",nodePolicyList);

		//获取某应用下的所有节点策略说明
		desList = new ArrayList<String>();
		for(FlowPerm fp : nodePolicyList)
		{
			desList.add(fp.getName()+"split" + Strings.escapeJavascript(fp.getDescription()));
		}
		if(!"form".equals(appName)) {
			//如果不是表单就去掉表单审核策略
			for(FlowPerm item : nodePolicyList){
				if("formaudit".equals(item.getName())){
					nodePolicyList.remove(item);
					break;
				}
			}
		}

		if("inform".equals(defaultPolicyId)) {
			//督办修改待办节点的节点策略时,屏蔽掉知会策略
			List<FlowPerm> _nodePolicyList = new ArrayList<FlowPerm>();
			for(FlowPerm item : nodePolicyList){
				if(!"zhihui".equals(item.getName()) && !"inform".equals(item.getName())){
					_nodePolicyList.add(item);
				}
			}
			nodePolicyList.clear();
			nodePolicyList.addAll(_nodePolicyList);
		}

		Metadata deadlineMeta = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
		mav.addObject("collaboration_deadline",deadlineMeta);

		Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		mav.addObject("comMetadata",comMetadata);

		if("StartNode".equals(nodeType) && !"true".equals(showSenderName)){
			nodeName = CurrentUser.get().getName();
		}

		String resource_baseName = "com.seeyon.v3x.flowperm.resources.i18n.FlowPermResource";
        String des = "";
        if(isEdoc){
        	des = StringEscapeUtils.escapeJavaScript(ResourceBundleUtil.getString(resource_baseName, "node.description.all.edoc"));
        }else{
        	des = StringEscapeUtils.escapeJavaScript(ResourceBundleUtil.getString(resource_baseName, "node.description.all"));
        }
        mav.addObject("des", des.trim());

		mav.addObject("nodeName", nodeName);
		mav.addObject("policyId", policyId);
		mav.addObject("dealTerm", dealTerm);
		mav.addObject("remindTime", remindTime);
		mav.addObject("processMode", processMode);
		mav.addObject("_isTemplete", _isTemplete);
		mav.addObject("bundleName",bundleName);
		mav.addObject("isFromTemplete", isFromTemplete);
		mav.addObject("formApp", formApp);
		mav.addObject("form", formName);
		mav.addObject("operationName", operationName);
		mav.addObject("nodeType", nodeType);
		mav.addObject("isEditor", isEditor);
		mav.addObject("desList", desList);
		mav.addObject("nodePolicyName", nodePolicyName);
		mav.addObject("partyType", partyType) ;
		mav.addObject("formField", formField) ;
		mav.addObject("nodeId", nodeId) ;
		mav.addObject("dealTermType", dealTermType) ;
		mav.addObject("dealTermUserId", dealTermUserId) ;
		mav.addObject("dealTermUserName", dealTermUserName) ;
		return mav;
	}

    /**
     * 查看节点属性
     *
     * @author jincm 2008-3-28
     * @param
     * @param
     * @return ModelAndView
     */
    public ModelAndView checkPolicy(HttpServletRequest request,
    		HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("/collaboration/checkPolicy");
		String state = request.getParameter("stateStr");
		String nodeName = request.getParameter("nodeName");
		String nodePolicy = request.getParameter("nodePolicy");
		String receiveTime = request.getParameter("receiveTime");
		String completeTime = request.getParameter("completeTime");
		String isTemplete = request.getParameter("isTemplete");
		String policyId = request.getParameter("policyId");
		String remindTime = request.getParameter("remindTime");
		String dealTime = request.getParameter("dealTime");
		String appName = request.getParameter("appName");
		String affairId = request.getParameter("affairId");
		String nodeId = request.getParameter("nodeId");
		String dealTermType = request.getParameter("dealTermType");
        String dealTermUserId = request.getParameter("dealTermUserId");
        String dealTermUserName = request.getParameter("dealTermUserName");
        String desc = request.getParameter("desc");
        String templeteId = request.getParameter("templeteId");
        String partyId = request.getParameter("partyId");//当前节点的处理人Id
        boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
        if(null!=desc){
        	desc= desc.replaceAll("\r\n", "<br>").replaceAll("\\s", "&nbsp;");
        }else{
        	desc="";
        }
        request.setAttribute("desc", desc);

		Long accountId = null;
		Affair affair = null;
		User user = CurrentUser.get();
		boolean isOverTime = false;
		boolean isFromTemplete=false;
		if(Strings.isNotBlank(templeteId)){
			isFromTemplete=true;
		}
		//什么情况下可能为空，如果查出来了，在这标注一下吧。
		boolean isStoreFlag = false;
		if(NumberUtils.isNumber(affairId)){
    		affair = affairManager.getById(Long.parseLong(affairId));
    		if(affair == null){
    			affair = hisAffairManager.getById(Long.parseLong(affairId));
    			isStoreFlag = affair != null;
    		}
    		isFromTemplete=affair.getTempleteId()!=null? true : false;
    		Long senderId = affair.getSenderId();
    		V3xOrgMember member = orgManager.getMemberById(senderId);
    		accountId = member.getOrgAccountId();

    		//计算节点是否超期
    		Long activityId = Strings.isBlank(nodeId) ? 0L : Long.valueOf(nodeId);
    		List<Affair> clickAaffairs = null;
    		if(isStoreFlag){
    			clickAaffairs = hisAffairManager.getAffairBySummaryIdAndActivityId(affair.getObjectId(), activityId);
    		}
    		else{
    			clickAaffairs = affairManager.getAffairBySummaryIdAndActivityId(affair.getObjectId(), activityId);
    		}

    		if(Strings.isNotEmpty(clickAaffairs)){
				for(Affair a : clickAaffairs){
					isOverTime = a.getIsOvertopTime();
					if(isOverTime)
						break;
				}
    		}
		}else if(NumberUtils.isNumber(partyId) && isGov){/* branches_a8_v350sp1_r_gov GOV-5099 政务向凡添加 修复 督办人查看某节点是否超期 显示不正确 （没有指定的affairId 导致，需要根据点击流程里具体某个节点 获得其affair信息）  Start*/
			Long memberId = Long.parseLong(partyId);
			Long activityId = Strings.isBlank(nodeId) ? 0L : Long.valueOf(nodeId);
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("memberId", memberId);
			condition.put("activityId", activityId);
			List<Affair> affairs = this.affairManager.getByConditions(condition);
			if(null != affairs && affairs.size() > 0){
				isOverTime = affairs.get(0).getIsOvertopTime();
			}
			accountId = user.getLoginAccount();
			/* branches_a8_v350sp1_r_gov GOV-5099 政务向凡添加 修复 督办人查看某节点是否超期 显示不正确 （没有指定的affairId 导致，需要根据点击流程里具体某个节点 获得其affair信息）  End*/
		}else{
			accountId = user.getLoginAccount();
		}

		//以下为在流程图中增加表单绑定权限查看功能代码
		String display = null;
		String formApp = request.getParameter("formApp");;
		String formId = request.getParameter("formId");;
		String operationId = request.getParameter("operationId");
		if(Strings.isNotBlank(formApp) && Strings.isNotBlank(formId) && Strings.isNotBlank(operationId)
			&& !"null".equals(formApp) && !"null".equals(formId) && !"null".equals(operationId)
			&& !"undefined".equals(formApp) && !"undefined".equals(formId) && !"undefined".equals(operationId)){
			display = FormHelper.getFormNameAndOperationName(Long.valueOf(formApp),Long.valueOf(formId), Long.valueOf(operationId));
		}

		Boolean _isTemplete = false;
		if("true".equalsIgnoreCase(isTemplete)){
			_isTemplete = true;
		}

		String isOvertopTime = "node.isovertoptime.false";
		if(isOverTime){
			isOvertopTime = "node.isovertoptime.true";
		}

		if("".equals(dealTime) || "undefined".equalsIgnoreCase(dealTime))
		{
			dealTime = "0";
		}

		String stateStr = "";
		if("1".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.1.common");
		}else if("2".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.2.already");
		}else if("3".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.3.complete");
		}else if("4".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.4.cancel");
		}else if("5".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.6.stop");
			//stateStr = Constant.getString4CurrentUser("node.state.5.run");
		}else if("6".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.6.stop");
		}else if("7".equalsIgnoreCase(state)){
			stateStr = Constant.getString4CurrentUser("node.state.7.zcdb");
		}
		String nodeMetadataName = "";
		List<String> desList = null;
		int flowPermType = 1;  //流程权限的类型 0:协同 .  1:公文（默认是公文）
		boolean isEdoc = false;
		if("collaboration".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.col_flow_perm_policy.name();
			flowPermType = com.seeyon.v3x.flowperm.util.Constants.F_TYPE_COLLABORATION; //指定流程的权限类型为协同
		}else if("sendEdoc".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_send_permission_policy.name();
			isEdoc = true;
		}else if("recEdoc".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_rec_permission_policy.name();
			isEdoc = true;
		}else if("signReport".equalsIgnoreCase(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_qianbao_permission_policy.name();
			isEdoc = true;
		}else{
			nodeMetadataName=MetadataNameEnum.col_flow_perm_policy.name();
			flowPermType = com.seeyon.v3x.flowperm.util.Constants.F_TYPE_COLLABORATION; //指定流程的权限类型为协同
		}

		List <FlowPerm> nodePolicyList = null;
		nodePolicyList = flowPermManager.getFlowpermsByStatus(nodeMetadataName, FlowPerm.Node_isActive, false, flowPermType, accountId);
		mav.addObject("nodePolicyList",nodePolicyList);

		//获取某应用下的所有节点策略说明
		desList = new ArrayList<String>();
		for(FlowPerm fp : nodePolicyList)
		{
			desList.add(fp.getName()+"split"+StringEscapeUtils.escapeJavaScript(fp.getDescription()));
		}

		Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        Metadata deadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);

        String resource_baseName = "com.seeyon.v3x.flowperm.resources.i18n.FlowPermResource";
        String des = "";
        if(isEdoc){
        	des = ResourceBundleUtil.getString(resource_baseName, "node.description.all.edoc");
        }else{
        	des = ResourceBundleUtil.getString(resource_baseName, "node.description.all");
        }
        mav.addObject("des", des);

		mav.addObject("stateStr", stateStr);
		mav.addObject("nodeName", nodeName);
		mav.addObject("nodePolicy", nodePolicy);
		mav.addObject("display", display);
		mav.addObject("receiveTime", receiveTime);
		mav.addObject("completeTime", completeTime);
		mav.addObject("isOvertopTime", isOvertopTime);
		mav.addObject("isTemplete",_isTemplete);
		mav.addObject("isFromTemplete", isFromTemplete);
		mav.addObject("policyId", policyId);
		mav.addObject("desList",desList);
		mav.addObject("dealTime",dealTime);
		mav.addObject("remindTime",remindTime);
		mav.addObject("comMetadata",comMetadata);
		mav.addObject("deadlineMetadata",deadlineMetadata);
		mav.addObject("accountId", accountId);
		mav.addObject("nodeId", nodeId) ;
		mav.addObject("dealTermType", dealTermType) ;
		mav.addObject("dealTermUserId", dealTermUserId) ;
		mav.addObject("dealTermUserName", dealTermUserName) ;
		return mav;
    }

    //终止
    public ModelAndView stepStop(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	 log.info("流程终止1。。。");
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        String delAttIds = request.getParameter("theDelAttIds");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.stepstop,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair affair = affairManager.getById(affairId);
	        PrintWriter out = response.getWriter();
	        if(affair.getState() != StateEnum.col_pending.key()){
				String msg=ColHelper.getErrorMsgByAffair(affair);
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

	        //保存终止时的意见,附件
	        ColOpinion signOpinion = new ColOpinion();
	        bind(request, signOpinion);

	        String afterSign = request.getParameter("afterSign");

	        signOpinion.isDeleteImmediate = "delete".equals(afterSign);
	        signOpinion.affairIsTrack = "track".equals(afterSign);

	        signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
	        signOpinion.setIdIfNew();

            /**
             * 保存附件:
             * 需要先要删除草稿的时候附件，然后再保存，否则会报ID重复；
             * 所以deleteDraftOpinion必须放到attachmentManager.create方法的前面，挪动位置的一定要注意到这个先后顺序
             * 已经报了N次由于这个原因引起的BUG了，注意！！！
             */
	        String draftOpinionId = request.getParameter("draftOpinionId");
	        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
	        	this.deleteDraftOpinion(Long.parseLong(draftOpinionId));
	        }

	        //保存附件
	        attachmentManager.deleteByReference(summaryId, 4);//删除表单中选择的关联文档
	        this.attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, signOpinion.getId(), request);
	        String pushMessageMembers = request.getParameter("pushMessageMemberIds");
		    setPushMessagePara2ThreadLocal(pushMessageMembers); 
	        Map<String,Object> columnValue= new HashMap<String,Object>();
	        Affair curerntAffair = null;
	        if(affairId != null){
	        	curerntAffair = affair;
	        	if(signOpinion.isDeleteImmediate){
	        		columnValue.put("isDelete", true);
		        }
		        if(affair.getMemberId() != user.getId()){  //由代理人终止需要写入处理人ID
		        	columnValue.put("transactorId", user.getId());
	                signOpinion.setProxyName(user.getName());
	            }
		        if(!columnValue.isEmpty() && columnValue.size()>0){
		        	affairManager.update(affairId, columnValue);
		        }
	        }else{
	        	 //TODO 这种做法有悖人道
		        List<Affair> curerntAffairs = affairManager.getPendingAffairListByObject(summaryId);
		        if(curerntAffairs != null && !curerntAffairs.isEmpty()){
		        	try {
		        		curerntAffair = (Affair)org.apache.commons.beanutils.BeanUtils.cloneBean(curerntAffairs.get(0));
		        		curerntAffair.setMemberId(user.getId());
					}catch (Exception e) {
						log.error("", e);
					}
		        }
	        }
	        if(curerntAffair!= null){
	        	//true:成功终止 false:不允许终止
		        //colManager.stepStop(summaryId, affairId,signOpinion);
	        	log.info("流程终止。。。"+curerntAffair.getId());
		        colManager.stepStop(summaryId, curerntAffair, signOpinion, user); //采用管理员终止流程一样的策略
	        }
	        //true:成功终止 false:不允许终止
	        //colManager.stepStop(summaryId, affairId,signOpinion);
//	        colManager.stepStop(summaryId, affairId, signOpinion, user); //采用管理员终止流程一样的策略

        	String formApp = request.getParameter("formApp");
        	if(formApp != null && !"".equals(formApp)&&!"null".equals(formApp)){
	        	String state = "";
	        	//审核通过
	        	String flag = "stepstop";
	        	String operationType = "update";
            	state = "";
	        	try{
	        		FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), request.getParameter("formApp"), request.getParameter("form"), request.getParameter("operation"), request.getParameter("masterId"), request.getParameter("formData"),"submit",state, operationType, flag,"");
	        		ColSummary summary = colManager.getColSummaryById(summaryId, false);
	        		if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue()){
	        			FormHelper.stopOrRepealOrSubmitOrZCDDOperForWithholding(new Long(request.getParameter("formApp")), summaryId, new Long(request.getParameter("masterId")),summary.getSubject());
	        		}
	        		TriggerHelper.deleteTriggerInfo(summaryId);
	        	}
	        	catch(DataDefineException e1){
	        	  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
	        		  PrintWriter out1 = response.getWriter();
		              	out1.println("<script>");
		                out1.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
		              	out1.println("history.back();");
		              	out1.println("</script>");
		              	return null;
	        	  }
	      		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
	      			  throw new RuntimeException(e1.getMessage());
	      		  else{
	      			  log.error("保存表单数据时发生错误", e1);
	      			  //throw new RuntimeException("不能保存");
	      			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	      		  }

	        	  }catch(Exception e){
		      		  log.error("保存表单数据时发生错误", e);
		      		  //throw new RuntimeException("不能保存");
		      		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	      	      }
	        	Long key = Long.valueOf(Thread.currentThread().getId());
	        	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
	        		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
	        		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
	        	}
	        	this.delAttByids(delAttIds);
        	}

	        //终止成功后，跳到待办列
        	out.println("<script>");
	    	out.println("parent.closeWindow();");
	    	out.println("</script>");
	    	out.close();
	    	return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        }
        return null;
    }

    /**
     * 流程管理界面直接终止流程
     * @author jincm 2008-6-05
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView stopflow(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summaryId");
        Long summaryId = Long.parseLong(_summaryId);
        boolean isRelieveLock = true;
        ColSummary summary = null;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.stepstop,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }
	        summary = colManager.getColSummaryById(summaryId, false);
	        //保存终止时的意见
	        ColOpinion signOpinion = new ColOpinion();
	        signOpinion.isDeleteImmediate = false;
	        signOpinion.affairIsTrack = false;
	        signOpinion.setIsHidden(false);
	        signOpinion.setIdIfNew();

	        Affair curerntAffair = null;
	        List<Affair> curerntAffairs = affairManager.getPendingAffairListByObject(summaryId);
	        if(curerntAffairs != null && !curerntAffairs.isEmpty()){
	        	try {
	        		curerntAffair = (Affair)org.apache.commons.beanutils.BeanUtils.cloneBean(curerntAffairs.get(0));
	        		curerntAffair.setMemberId(user.getId());
				}catch (Exception e) {
					log.error("", e);
				}
	        }
	        if(curerntAffair!= null){
	        	//true:成功终止 false:不允许终止
		        colManager.stepStop(summaryId, curerntAffair, signOpinion, user);
		        //终止成功后，跳到待办列
		        PrintWriter out = response.getWriter();
		        out.println("<script>");
		    	out.println("if(window.dialogArguments){"); //弹出
		    	out.println("  window.returnValue = \"true\";");
		    	out.println("  window.close();");
		    	out.println("}else{");
		    	out.println("  parent.ok();");
		    	out.println("}");
		    	out.println("</script>");
		    	out.close();
	        }
	    	return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        	colManager.colDelLock(summary);
        }
        return null;
    }

    //回退
    public ModelAndView stepBack(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        String delAttIds = request.getParameter("theDelAttIds");
        String currentNodeId = request.getParameter("currentNodeId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        ColSummary summary = null;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.stepback,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair _affair = affairManager.getById(affairId);
	        PrintWriter out = response.getWriter();
	        if(_affair.getState() != StateEnum.col_pending.key()){
				String msg=ColHelper.getErrorMsgByAffair(_affair);
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

	        //保存回退时的意见,附件�?
	        ColOpinion signOpinion = new ColOpinion();
	        bind(request, signOpinion);

	        String afterSign = request.getParameter("afterSign");

	        signOpinion.isDeleteImmediate = "delete".equals(afterSign);
	        signOpinion.affairIsTrack = "track".equals(afterSign);

	        signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
	        signOpinion.setIdIfNew();
	        summary = colManager.getColSummaryById(summaryId, false);
	        Affair affair = _affair;

            /**
             * 保存附件:
             * 需要先要删除草稿的时候附件，然后再保存，否则会报ID重复；
             * 所以deleteDraftOpinion必须放到attachmentManager.create方法的前面，挪动位置的一定要注意到这个先后顺序
             * 已经报了N次由于这个原因引起的BUG了，注意！！！
             */
	        String draftOpinionId = request.getParameter("draftOpinionId");
	        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
	        	this.deleteDraftOpinion(Long.parseLong(draftOpinionId));
	        }

	        //保存附件
	        attachmentManager.deleteByReference(summaryId, 4);//删除表单中选择的关联文档
	        this.attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, signOpinion.getId(), request);

	        AttachmentEditHelper editHelper = new AttachmentEditHelper(request);

	        boolean parentIsStart = ColHelper.isSecondNode(summary.getCaseId(), affair.getSubObjectId(),null);
	        String formData = request.getParameter("formData");
	        if("FORM".equals(summary.getBodyType())){
	        	if(formData!=null && !"".equals(formData) && !"null".equals(formData)){
	        		String state = "";
	        		String flag = "stepBack";
	        		String ratifyflag ="";
	        		//回退到首节点，state=0，否则如果审核不通过state=3
	        		String auditBack = (String)request.getAttribute("auditBack");
	        		if(auditBack!=null){
	        			state = auditBack;
	        		}
	        		String vouchBack =(String)request.getAttribute("vouchBack");
	        		if(vouchBack != null){
	        			ratifyflag = vouchBack;
	        			flag = "vouchBack";
	        			//修改协同的核定节点
	        			if(Constant.FormVouch.vouchBack.getKey().equals(ratifyflag)){
	        				Map<String, Object> columns = new HashMap<String, Object>();
	        				columns.put("isVouch", Constant.ColSummaryVouch.vouchBack.getKey());
	        				colManager.update(summaryId, columns);
	        			}
	        		}
	        		
	        		if(parentIsStart)
	        			state = "0";
	        		
	        		try{
	        			//处理表单状态等信息存储
	        			formDaoManager.UpdateDataState(user.getId(), summaryId, state, summary.getFormRecordId(), "submit", "stepBack");
	        			//如果来自关联表单，则不更改表单状态
	        			if(summary.getParentformSummaryId() !=null || (summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.child.ordinal())){
	        				state = "";
	        			}
	        			//当回退到发起人的时候，清空其他填写的信息 改变值得传递operation 和submitType
	        			String formId = request.getParameter("form");
	        			String operation = request.getParameter("operation");
	        			String submitType ="submit";
	        			ColBody body = summary.getFirstBody();
	        			String bodyType = body.getBodyType();
	        			if(state !=null && "0".equals(state)){
	        				if("FORM".equals(bodyType)){
	        					Templete template = templeteManager.get(summary.getTempleteId());
	        					if(template != null){
	        						if(template.getFormParentId()!=null)
	        							template = templeteManager.get(template.getFormParentId());
	        						BPMProcess process = BPMProcess.fromXML(template.getWorkflow());
	        						String[] formInfo = FormHelper.getFormPolicy(process);
	        						formId = formInfo[1];
	        						operation = formInfo[2];
	        						submitType = "rollback";
	        					}
	        				}
	        			}
	        			if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue() && "0".equals(state)){
	        				FormHelper.stopOrRepealOrSubmitOrZCDDOperForWithholding(new Long(request.getParameter("formApp")), summaryId, new Long(request.getParameter("masterId")),summary.getSubject());
	        			}
	        			FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), request.getParameter("formApp"), formId, operation, request.getParameter("masterId"), formData,submitType,state,"update",flag,ratifyflag);
	        		}
	        		catch(DataDefineException e1){
	        			if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldWorn){
	        				PrintWriter out1 = response.getWriter();
	        				out1.println("<script>");
	        				out1.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e1.getMessage()) + "\"); ");
	        				out1.println("history.back();");
	        				out1.println("</script>");
	        				return null;
	        			}
	        			if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
	        				throw new RuntimeException(e1.getMessage());
	        			else{
	        				log.error("保存表单数据时发生错误", e1);
	        				//throw new RuntimeException("不能保存");
	        				throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	        			}
	        			
	        		}catch(Exception e){
	        			log.error("保存表单数据时发生错误", e);
	        			//throw new RuntimeException("不能保存");
	        			throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	        		}
	        		Long key = Long.valueOf(Thread.currentThread().getId());
	        		if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
	        			OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
	        			ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
	        		}
	        		this.delAttByids(delAttIds) ;
	        	}else{
	        		if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue()&&parentIsStart){
	        			FormHelper.stopOrRepealOrSubmitOrZCDDOperForWithholding(new Long(request.getParameter("formApp")), summaryId, new Long(request.getParameter("masterId")),summary.getSubject());
	        		}
	        	}
	        }
	        if(editHelper.hasEditAtt()){//是否修改附件
            	colManager.updateSummaryAttachment(editHelper.attSize(),editHelper.parseProcessLog(Long.parseLong(summary.getProcessId()), Long.parseLong(currentNodeId)),summaryId);
            }
	        //true:成功回退 false:不允许回退
	        boolean stepBackFlag = colManager.stepBack(summary, affair,signOpinion, user,parentIsStart);
	        colManager.clearSession(); 
	        //更新全文检索
	        UpdateIndexManager updateIndexManager = (UpdateIndexManager)ApplicationContextHolder.getBean("updateIndexManager");
	        updateIndexManager.update(affair.getObjectId(), ApplicationCategoryEnum.collaboration.getKey());
	        //回退到发起节点时，如果有督办则修改督办状态，并发消息给督办人
	        if(stepBackFlag && parentIsStart){
	        	this.colSuperviseManager.updateStatusAndNoticeSupervisor(summary.getId(),
            		Constant.superviseType.summary.ordinal(), ApplicationCategoryEnum.collaboration,
            		summary.getSubject(), user.getId(), user.getName(), Constant.superviseState.waitSupervise.ordinal(), "col.stepback", signOpinion.getContent(), summary.getForwardMember());
	        	try{
	        		colManager.deleteColTrackMembersByObjectId(summaryId);
	        	}catch(Exception e){
	        		log.error(e);
	        	}
	        }
	        //不允许回退提示
	        String message = WebUtil.flash(Constant.resource_baseName, user.getLocale());
	        if(Strings.isNotBlank(message)){
	        	out.println(message);
	        }

	        //回退成功后，跳到待办列表
	        out.println("<script>");
	    	out.println("parent.closeWindow();");
	    	out.println("</script>");
	    	out.close();
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
//    		表单解锁，冗余处理，防止窗口关闭时的事件未执行
        	colManager.colDelLock(summary);
        }
        return null;
    }

    //取回
    public ModelAndView takeBack(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String[] affairIds = request.getParameterValues("affairId");
        String[] summaryIds = request.getParameterValues("summaryId");
        String[] saveOpinion = request.getParameterValues("saveOpinion");
        //StringBuffer info = new StringBuffer();
        boolean isRelieveLock = true;
        try{
        	int result = 0;
        	if (affairIds != null) {
	        	int i=0;
	            for (String affairId : affairIds) {
	            	Long _affairId = new Long(affairId);
	            	//检查同步锁
	        		if(!ColHelper.colOperationLock(Long.parseLong(summaryIds[i]), user.getId(), user.getName(), ColLock.COL_ACTION.tackback,
	        				response, ApplicationCategoryEnum.collaboration)){
	        			isRelieveLock = false;
	        			return super.refreshWorkspace();
	                }
	                result = colManager.takeBack(_affairId, user,Boolean.valueOf(saveOpinion[i]));
	                if(result != 0){
	                	Affair affair = affairManager.getById(_affairId);
	                	String subject = "";
                        if(affair != null){
                            subject = "《" + affair.getSubject() + "》";
	                    }
	                	if(result == -1){
	                	    WebUtil.saveAlert("col.takeBack.invalidation.alert", subject);
	                	}
	                	else if(result == -3){
	                	    //WebUtil.saveAlert("col.takeBack.newflowEnd.alert", subject);
	                	}
	                	else if(result == -2){
	                	    WebUtil.saveAlert("col.takeBack.informNode.alert", subject);
	                	}
                        break;
	                }
	                i++;
	            }
	        }
        }catch(Exception e){
        	log.error("", e);
        }
        finally{
        	if(isRelieveLock){
        		for(int i=0; i<summaryIds.length; i++){
        			ColLock.getInstance().removeLock(Long.parseLong(summaryIds[i]));
        		}
        	}
        }
        return super.refreshWorkspace();
    }

    /**
     * 暂存待办
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView doZCDB(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        long summaryId = Long.parseLong(request.getParameter("summary_id"));
        long affairId = Long.parseLong(request.getParameter("affair_id"));
        String delAttIds = request.getParameter("theDelAttIds");
        String processId = request.getParameter("processId");
        String currentNodeId = request.getParameter("currentNodeId");
        boolean isRelieveLock = true;
        ColSummary summary = null;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.insertPeople,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair _affair = affairManager.getById(affairId);
	        if(_affair.getState() != StateEnum.col_pending.key()){
	        	PrintWriter out = response.getWriter();
				String msg=ColHelper.getErrorMsgByAffair(_affair);
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

	        ColOpinion opinion = new ColOpinion();
	        bind(request, opinion);
	        opinion.setIdIfNew();
	        opinion.setIsHidden(request.getParameterValues("isHidden") != null);
        	//保存督办相关信息
	        summary = colManager.getColSummaryById(summaryId, false);
        	this.saveColSupervise(request, response, summary, false,Constant.superviseState.supervising.ordinal(),true);
	        if ("true".equals(request.getParameter("isDeleteSupervisior"))) {
	        	ColSuperviseDetail detail= colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(),summaryId);
	        	this.colSuperviseManager.deleteSuperviseById(detail.getId());
			}
	        /**
             * 保存附件:
             * 需要先要删除草稿的时候附件，然后再保存，否则会报ID重复；
             * 所以deleteDraftOpinion必须放到attachmentManager.create方法的前面，挪动位置的一定要注意到这个先后顺序
             * 已经报了N次由于这个原因引起的BUG了，注意！！！
             */
	        String draftOpinionId = request.getParameter("draftOpinionId");
	        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
	        	this.deleteDraftOpinion(Long.parseLong(draftOpinionId));
	        }
	        
	        attachmentManager.deleteByReference(summaryId, 4);//删除表单中选择的关联文档

	        String isUploadAttStr = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, opinion.getId(), request);
	        AttachmentEditHelper editHelper = new AttachmentEditHelper(request);
	        if(editHelper.hasEditAtt()){//是否修改附件
	        	colManager.updateSummaryAttachment(editHelper.attSize(),editHelper.parseProcessLog(Long.parseLong(processId), Long.parseLong(currentNodeId)),summaryId);
	        }
	        boolean isUploadAtt = com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(isUploadAttStr);
	        opinion.setHasAtt(isUploadAtt);
	        //推送消息    affairId,memberId#affairId,memberId#affairId,memberId
		    String pushMessageMembers = request.getParameter("pushMessageMemberIds");
		    setPushMessagePara2ThreadLocal(pushMessageMembers);
	        this.colManager.zcdb(summaryId, _affair, opinion, processId, user.getId()+"");


        	String[] afterSign = request.getParameterValues("afterSign");
  	        Set<String> options = new HashSet<String>();
  	        if(afterSign!=null){
	  	        for(String option : afterSign){
	  	        	options.add(option);
	  	        }
  	        }
  	        //允许处理后归档和跟踪被同时选中，但是他们都不能和删除按钮同时选中
  	        if(options.size()>1 && options.contains("delete")){
  	        	options.remove("delete");
  	        }
  	        boolean track =  options.contains("track");
  	        //跟踪
  	        String trackMembers = request.getParameter("trackMembers");
  	        String trackRange = request.getParameter("trackRange");
  	        //不跟踪 或者 全部跟踪的时候不向部门跟踪表中添加数据，所以将下面这个参数串设置为空。
  	        if(!track || "1".equals(trackRange)) trackMembers = "";
  	        colManager.setTrack(affairId, track, trackMembers);

	        PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println("parent.closeWindow();");
	        out.println("</script>");

	        updateIndexManager.update(summaryId, ApplicationCategoryEnum.collaboration.getKey());

	    	String formApp = request.getParameter("formApp");
	    	if(formApp != null && !"".equals(formApp)&&!"null".equals(formApp)){
	        	String state = null;
	        	try{
                    String operationType = "update";
		            //如果是关联表单的或子流程的不更新表单状态
		            if(summary.getParentformSummaryId() !=null || (summary.getNewflowType()!=null && summary.getNewflowType() == Constant.NewflowType.child.ordinal())){
		                operationType = "update";
		            	state = "";
                    }
		            if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue()){
                        FormHelper.stopOrRepealOrSubmitOrZCDDOperForWithholding(new Long(request.getParameter("formApp")), summaryId, new Long(request.getParameter("masterId")),summary.getSubject());
                    }
		            String formData = FormHelper.setFormAppendValue(user.getId(), user.getName(), user.getLoginName(), request.getParameter("formData"), request.getParameter("formDisplayValue"), request.getParameter("formApp"), request.getParameter("form"), request.getParameter("operation"), request.getParameter("masterId"));
	        	    FormHelper.saveOrUpdateFormData(user.getId(), user.getName(), user.getLoginName(), request.getParameter("formApp"), request.getParameter("form"), request.getParameter("operation"), request.getParameter("masterId"), formData,"submit",state,operationType,null,null);
	        	    if(summary != null && Constant.ColSummaryVouch.vouchPass.getKey() != summary.getIsVouch().intValue()){
	            		FormHelper.sendOrCheckOperForWithholding(new Long(request.getParameter("formApp")), summaryId, new Long(request.getParameter("masterId")),summary.getSubject());
	            	}
	        	}catch(DataDefineException e1){
	        		  if(e1.getErrCode() == DataDefineException.C_iDbOperErrode_FieldTooLong)
	        			  throw new RuntimeException(e1.getMessage());
	        		  else{
	        			  log.error("保存表单数据时发生错误", e1);
	        			  //throw new RuntimeException("不能保存");
	        			  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	        		  }

	          	}catch(Exception e){
	        		  log.error("保存表单数据时发生错误", e);
	        		  //throw new RuntimeException("不能保存");
	        		  throw new RuntimeException(Constantform.getString4CurrentUser("DataDefine.CannotSave"));
	        	}
	        	Long key = Long.valueOf(Thread.currentThread().getId());
	        	if(ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey") != null){
	        		OperHelper.creatformmessage(request,response,(List<String>)ThreadLocalUtil.get("FlowIdMsgListMapThreadLocalKey"));
	        		ThreadLocalUtil.remove("FlowIdMsgListMapThreadLocalKey");
	        	}
	        	this.delAttByids(delAttIds) ;
	        	//插入关联的无流程表单
	        	colQuoteformRecordManger.create(request, summaryId, Long.parseLong(formApp), new Long(request.getParameter("masterId")));
	    	}
	    	//流程日志
	    	processLogManager.insertLog(user, Long.parseLong(processId), Long.parseLong(currentNodeId), ProcessLogAction.zcdb);
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
//          表单解锁，冗余处理，防止窗口关闭时的事件未执行
        	colManager.colDelLock(summary);
        }
        return null;
    }
	private void setPushMessagePara2ThreadLocal(String pushMessageMembers){
	//推送消息    affairId,memberId#affairId,memberId#affairId,memberId
	    List<Long[]> pushList = new ArrayList<Long[]>();
	    if(Strings.isNotBlank(pushMessageMembers)){
	    	String[] pushs = pushMessageMembers.split("#");
	    	for(String s :pushs){
	    		String[] s1 = s.split(",");
	    		pushList.add(new Long[]{Long.valueOf(s1[0]),Long.valueOf(s1[1])});
	    	}
	    }
	    DateSharedWithWorkflowEngineThreadLocal.setPushMessageMembers(pushList);
	}
    /**
     * 意见回复
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView doComment(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
    	Long summaryId = new Long(request.getParameter("summaryId"));
    	ColSummary colSummary = this.colManager.getColSummaryById(summaryId.longValue(), false);

        if (colSummary.getFinishDate() != null) {
          PrintWriter out = response.getWriter();
          out.println("<script>");
          out.println("alert('" + ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.cannot.reply.message") + "')");
          out.println("</script>");
          return null;
        }
    	String isNoteAddOrReply = request.getParameter("isNoteAddOrReply");

    	//是否发送消息
    	boolean isSendMessage = request.getParameterValues("isSendMessage") != null;

    	Timestamp now = new Timestamp(System.currentTimeMillis());

    	//发起人增加附言
    	if("addnote".equals(isNoteAddOrReply)){
            ColOpinion senderOpinion = new ColOpinion();
            senderOpinion.setIdIfNew();

            senderOpinion.setCreateDate(now);
            senderOpinion.setContent(request.getParameter("content"));
            senderOpinion.setWriteMemberId(user.getId());
            senderOpinion.setOpinionType(ColOpinion.OpinionType.senderOpinion);
            senderOpinion.setSummaryId(summaryId);

            this.colManager.saveOpinion(senderOpinion, isSendMessage);

            this.attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, senderOpinion.getId(), request);
    	}
    	else if("reply".equals(isNoteAddOrReply)){
	    	ColComment c = new ColComment();
	    	c.setIdIfNew();
	    	Long opinionId = Long.parseLong(request.getParameter("opinionId"));
	    	c.setContent(request.getParameter("content"));
            boolean isHidden = request.getParameterValues("isHidden") != null;
	    	c.setIsHidden(isHidden);
	    	if(isHidden){
	    		String showToId = request.getParameter("showToId");
	    		c.setShowToId(showToId);
	    	}
	    	c.setCreateDate(now);
	    	c.setOpinionId(opinionId);

	    	Long memberId = new Long(request.getParameter("memberId"));
	    	c.setMemberId(memberId);
	    	c.setStartMemberId(new Long(request.getParameter("startMemberId")));
	    	c.setMemberName(this.orgManager.getMemberById(memberId).getName());
	    	c.setSummaryId(summaryId);
	    	if("true".equalsIgnoreCase(request.getParameter("isProxy"))){
	    		String affairMemberId = request.getParameter("affairMemberId");
	    		List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
	    		if(agentModelList != null && !agentModelList.isEmpty() && Strings.isNotBlank(affairMemberId)){
            		c.setWriteMemberId(Long.parseLong(affairMemberId));
                	c.setProxyName(user.getName());
            	}else{
            		c.setWriteMemberId(user.getId());
            	}
	    	}else{
   	    		c.setWriteMemberId(user.getId());
	    	}
	        //推送消息    affairId,memberId#affairId,memberId#affairId,memberId
		    String pushMessageMembers = request.getParameter("pushMessageMemberIds");
		    setPushMessagePara2ThreadLocal(pushMessageMembers);

	    	this.colManager.saveComment(c, false);

	    	ColSummary summary = colManager.getSimpleColSummaryById(summaryId);
	    	if (summary!=null)
	    		c.setSubject(summary.getSubject());

	    	ColMessageHelper.doCommentMessage(c, orgManager, affairManager, userMessageManager, colManager);
            attachmentManager.create(ApplicationCategoryEnum.collaboration, summaryId, c.getId(), request);
    	}

    	updateIndexManager.update(summaryId, ApplicationCategoryEnum.collaboration.getKey());

    	super.rendJavaScript(response, "parent.replyCommentOK('" + Datetimes.formateToLocaleDatetime(now) + "')");
    	return null;
    }

    /**
     * 处理协同 - 存为草稿
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView doDraftOpinion(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();

        long summaryId = Long.parseLong(request.getParameter("summary_id"));

        ColOpinion opinion = new ColOpinion();
        opinion.setWriteMemberId(user.getId());
        opinion.setSummaryId(summaryId);
        opinion.setCreateDate(new Timestamp(System.currentTimeMillis()));
        opinion.setOpinionType(ColOpinion.OpinionType.draftOpinion);
        bind(request, opinion);

        String draftOpinionId = request.getParameter("draftOpinionId");
        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
        	opinion.setId(Long.parseLong(draftOpinionId));
        	this.colManager.update(opinion);

        	this.attachmentManager.deleteByReference(opinion.getId(), opinion.getId());
        }
        else{ //第一次新建
        	opinion.setIdIfNew();
        	this.colManager.save(opinion);
        }

        //保存附件
        this.attachmentManager.create(ApplicationCategoryEnum.collaboration, opinion.getId(), opinion.getId(), request);

        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("parent.doEndDraftOpinion('" + opinion.getId() + "');");
        out.println("</script>");

        return null;
    }

    /**
     * 催办
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView preHasten(HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
    	String activityId = request.getParameter("activityId");
    	Long _activityId = Long.parseLong(activityId);
    	String memberIdStr = ColHelper.hastenMemberIdsMap.get(_activityId);
    	if(!"".equals(memberIdStr)){
    		ColHelper.hastenMemberIdsMap.remove(_activityId);
    	}
    	ModelAndView mav = new ModelAndView("collaboration/preHasten");

    	FlowData flowData = colManager.preHasten(memberIdStr);
    	mav.addObject("flowData", flowData);

    	return mav;
    }
    /**
     * 催办
     * @deprecated 发起人催办与督办人催办共用一个jsp页面，表单提交对应方法统一到督办Controller的催办方法中
     * @see com.seeyon.v3x.collaboration.controller.ColSuperviseController#hasten
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView hasten(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
    	String summaryId = request.getParameter("summaryId");
        String content = request.getParameter("content");

        String[] people = request.getParameterValues("deletePeople");

        if(people != null && people.length > 0){
        	List<Long> receivers = new ArrayList<Long>(people.length);
        	for (String p : people) {
        		receivers.add(Long.parseLong(p));
        	}

        	colManager.hasten(summaryId, receivers, content);
        }

        String info = Constant.getString4CurrentUser("hasten.success.label");
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + info + "');");
        out.println("parent.close()");
        out.println("</script>");

        return null;

    }

    /**
     * 会签
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView colAssign(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String sSummaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        long summaryId = Long.parseLong(sSummaryId);
        long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.colAssign,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair affair = affairManager.getById(affairId);
	        PrintWriter out = null;
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	out = response.getWriter();
				String msg=ColHelper.getErrorMsgByAffair(affair);
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

	        //从request对象取到选人信息
	        FlowData flowData = FlowData.flowdataFromRequest();

	        colManager.colAssign(summaryId, affairId, flowData, user.getId()+"");

	        ColSummary summary = colManager.getColSummaryById(summaryId, false);

	        //会签协同工作项操作日志
	        /*operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration,
	        		Constant.OperationLogActionType.colAssign.name(), "col.operationlog.colAssign", user.getName(), new java.util.Date(), summary.getSubject());*/
	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (summary != null){
	            if (summary.getCaseId() != null) {
	                long caseId = summary.getCaseId();
	                caseLogXML = colManager.getCaseLogXML(caseId);
	                caseProcessXML = colManager.getModifyingProcessXML(summary.getProcessId());
	                caseWorkItemLogXML = colManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
	                String processId = summary.getProcessId();
	                caseProcessXML = colManager.getProcessXML(processId);
	            }
	        }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

	        out = response.getWriter();
	        String process_desc_by = FlowData.DESC_BY_XML;
	        out.println("<script>");

	        if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
	        	out.println("parent.window.dialogArguments.caseProcessXML = \"" + caseProcessXML + "\";");
		        out.println("parent.window.dialogArguments.caseLogXML = \"" + caseLogXML + "\";");
		        out.println("parent.window.dialogArguments.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
		        out.println("parent.window.dialogArguments.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
		        out.println("parent.window.dialogArguments.document.getElementById('isMatch').value = \"false\";");
		        out.println("parent.window.dialogArguments.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
		        out.println("parent.window.dialogArguments.selectInsertPeopleOK();");
		        out.println("top.close();");
        	}else{//ipad
		        out.println("parent.parent.detailRightFrame.caseProcessXML = \"" + caseProcessXML + "\";");
		        out.println("parent.parent.detailRightFrame.caseLogXML = \"" + caseLogXML + "\";");
		        out.println("parent.parent.detailRightFrame.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
		        out.println("parent.parent.detailRightFrame.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
		        out.println("parent.parent.detailRightFrame.document.getElementById('isMatch').value = \"false\";");
		        out.println("parent.parent.detailRightFrame.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
		        out.println("parent.parent.detailRightFrame.selectInsertPeopleOK();");
		        out.println("parent.parent.$('#colAssignWin').dialog('destroy');");
        	}

	        out.println("</script>");
	        out.close();
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        }
        return null;
    }

    /**
     * 知会前
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView preAddInform(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("collaboration/addInform");

		String _summaryId = request.getParameter("summaryId");
		String _affairId = request.getParameter("affairId");
		String processId = request.getParameter("processId");
		Long affairId = Long.parseLong(_affairId);
		Long summaryId = Long.parseLong(_summaryId);

		PrintWriter out = response.getWriter();
		Affair affair = affairManager.getById(affairId);
		if (affair == null || affair.getState() != StateEnum.col_pending.key()) {
			String msg = ColHelper.getErrorMsgByAffair(affair);
			out.println("<script>");
			out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
			out.println("if(window.dialogArguments){");
			out.println("	window.returnValue = \"" + DATA_NO_EXISTS + "\";");
			out.println("	window.close();");
			out.println("}else{");
			out.println("	parent.getA8Top().reFlesh();");
			out.println("}");
			out.println("</script>");
			out.close();
			return null;
		}

		mav.addObject("summaryId", summaryId);
		mav.addObject("affairId", affairId);
		mav.addObject("processId", processId);
		return mav;
    }

    /**
     * 知会
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView addInform(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String sSummaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        long summaryId = Long.parseLong(sSummaryId);
        long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.inform,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair affair = affairManager.getById(Long.parseLong(_affairId));
	        PrintWriter out = null;
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	out = response.getWriter();
				String msg=ColHelper.getErrorMsgByAffair(affair);
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

	        //从request对象取到选人信息
	        FlowData flowData = FlowData.flowdataFromRequest();
	        Long caseId = null;
	        String processId = "";
	        ColSummary colSummary = null;
	        EdocSummary edocSummary = null;
	        if(affair.getApp() == ApplicationCategoryEnum.collaboration.key()){
	        	 colManager.addInform(summaryId, affairId, flowData, user.getId()+"");
	        	 colSummary = colManager.getColSummaryById(summaryId, false);
	        	 if(colSummary != null){
		        	 caseId = colSummary.getCaseId();
		        	 processId = colSummary.getProcessId();
	        	 }
	        }else{
		        edocManager.addInform(summaryId, affairId, flowData, user.getId()+"");
		        edocSummary = edocManager.getEdocSummaryById(summaryId, false);
		        if(edocSummary != null){
			        caseId = edocSummary.getCaseId();
		        	processId = edocSummary.getProcessId();
		        }
	        }
	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (edocSummary != null || colSummary != null){
	            if (caseId!= null) {
	                caseLogXML = colManager.getCaseLogXML(caseId);
	                caseProcessXML = colManager.getModifyingProcessXML(processId);
	                caseWorkItemLogXML = colManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (Strings.isNotBlank(processId)) {
	                caseProcessXML = colManager.getProcessXML(processId);
	            }
	        }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

	        out = response.getWriter();
	        String process_desc_by = FlowData.DESC_BY_XML;
	        out.println("<script>");
	        out.println("parent.caseProcessXML = \"" + caseProcessXML + "\";");
	        out.println("parent.caseLogXML = \"" + caseLogXML + "\";");
	        out.println("parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
	        out.println("parent.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
	        out.println("parent.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
	        out.println("parent.selectInsertPeopleOK();");
	        out.println("</script>");
	        out.close();
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        }
        return null;
    }

    /**
     * 修改正文
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView modifyBody(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("collaboration/modifyBody");
        String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);

        ColBody body = this.colManager.getColBody(summaryId);

        mav.addObject("body", body);
        return mav;
    }

    /**
     * 修改正文 - 保存
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView saveModifyBody(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);

        ColBody body = new ColBody();
        bind(request, body);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
        }

        body.setSummaryId(summaryId);

        this.colManager.saveBody(body);

        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("<!--");
        out.println("parent.doEndModifyBodySave();");
        out.println("//-->");
        out.println("</script>");

        updateIndexManager.update(summaryId, ApplicationCategoryEnum.collaboration.getKey());
        return null;
    }

    public ModelAndView showForward(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        long summaryId = Long.parseLong(request.getParameter("summaryId"));
        long affairId = Long.parseLong(request.getParameter("affairId"));

        boolean isStoreFlag = false;
        Affair _affair = affairManager.getById(affairId);
        if(_affair == null){
        	_affair = hisAffairManager.getById(affairId);
        	isStoreFlag = _affair != null;
        }
        int state = _affair.getState();
        if(state != StateEnum.col_pending.key()
        		&& state != StateEnum.col_waitSend.key()
        		&& state != StateEnum.col_sent.key()
        		&& state != StateEnum.col_done.key()){
        	PrintWriter out = response.getWriter();
			String msg=ColHelper.getErrorMsgByAffair(_affair);
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

        ColSummary summary = null;
        if(isStoreFlag){
        	summary = hisColManager.getColSummaryById(summaryId, false);
        }
        else{
        	summary = colManager.getColSummaryById(summaryId, false);
        }

        String nodePermissionPolicy = colManager.getPolicyByAffair(_affair);

        //检测是否有转发的权限，发起者永远有权限
        if (CurrentUser.get().getId() != summary.getStartMemberId().longValue()){
        	Long flowPermAccountId = ColHelper.getFlowPermAccountId(CurrentUser.get().getLoginAccount(), summary, templeteManager);
        	boolean isCanForward = ColHelper.isActionAllowedOfNodePolicy(permissionManager, summary, nodePermissionPolicy, "Forward", flowPermAccountId);
    		if(!isCanForward) {
	            PrintWriter out = response.getWriter();
	            out.print("<script>");
	            String message = Constant.getString4CurrentUser("col.forward.noPower", summary.getSubject());
	            out.print("alert(\"" + StringEscapeUtils.escapeJavaScript(message) + "\"); ");
	            out.print("history.back();window.close();");
	            out.print("</script>");
	            return null;
    		}
        }

        ModelAndView mv = null;

        if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
    		mv = new ModelAndView("collaboration/collForward");
    	}else{//ipad
    		mv = new ModelAndView("collaboration/collForwardIpad");
    	}
        
        mv.addObject("secretLevel", summary.getSecretLevel());//成发集团项目 程炯 2012-9-14 为协同转发传递secretLevel
        mv.addObject("bodyType", summary.getBodyType());
        mv.addObject("summaryId", summary.getId());
        if("FORM".equals(summary.getBodyType())){
        	String formContent = getFormContent(summary, _affair);
        	mv.addObject("formContent", formContent);
            List<Attachment> allAttachments = new ArrayList<Attachment>() ;

            List<Attachment> list = getMainRunAtt(summary) ;
            if(list != null){
            		allAttachments.addAll(list) ;
            }
            if(attachmentManager.getByReference(summaryId) != null) {
            	allAttachments.addAll(attachmentManager.getByReference(summaryId)) ;
            }
            mv.addObject("attachment", allAttachments);
        	//mv.addObject("attachment", this.attachmentManager.getByReference(summaryId))  ;
        }

        return mv;
    }

    /**
     * 得到表单的正文，转换成HTML
     * @param summary
     * @param affair
     * @return
     * @throws Exception
     */
    private static String getFormContent(ColSummary summary, Affair affair) throws Exception{
    	ColBody body = summary.getFirstBody();
    	String masterId = body.getContent();

    	String formContent = null;
    	String nodePermissionPolicy = "collaboration";
        String formAppId = null; //表单应用id
        String formId = null; //表单id
        String formOperationId = null; //表单节点操作Id

        //如果数据库中取不到节点权限，就从XML中取
        if(Strings.isBlank(affair.getNodePolicy())){
	        //获取当前事项对应的节点
	        BPMProcess process = null;
	        try {
	        	process = ColHelper.getCaseProcess(summary.getProcessId());
	        	if(process == null){
	        		process = ColHelper.getHisCaseProcess(summary.getProcessId());
	        	}
			}
			catch (Exception e) {
				//ignore
			}
	        BPMSeeyonPolicy policy = null;

	        if(process != null){
		        int affairState = affair.getState().intValue();
		        if(StateEnum.col_sent.key() == affairState || StateEnum.col_waitSend.key() == affairState){ //已发、待发
		        	policy = process.getStart().getSeeyonPolicy();
		        }
		        else{
		        	policy = ColHelper.getBPMActivityByAffair(process, affair).getSeeyonPolicy();
		        }

		        if(policy != null){
		            nodePermissionPolicy = policy.getId();
		        }

	        	formAppId = policy.getFormApp();
	        	formId = policy.getForm();
	        	formOperationId = policy.getOperationName();
	        }
        }
        else{
        	nodePermissionPolicy = affair.getNodePolicy();
        	formAppId = String.valueOf(affair.getFormAppId());
        	formId = String.valueOf(affair.getFormId());
        	formOperationId = String.valueOf(affair.getFormOperationId());
        }

    	if(formAppId != null){
        	User user = CurrentUser.get();
        	boolean readonly = true;
        	formContent = FormHelper.getFormRun(user.getId(), user.getName(), user.getLoginName(), formAppId, formId, formOperationId, masterId, String.valueOf(summary.getId()), String.valueOf(affair.getId()), nodePermissionPolicy, readonly);
        }


    	return formContent;
    }


    /**
     * 转发
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView doForward(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        Long summaryId = Long.parseLong(request.getParameter("summaryId"));
        String note = request.getParameter("note");
        ColOpinion senderOninion = new ColOpinion();
        if (StringUtils.isNotBlank(note)) {
        	senderOninion.setIdIfNew();
            senderOninion.setContent(note);
        }

        boolean forwardOriginalNode = request.getParameterValues("forwardOriginalNode") != null;
        boolean foreardOriginalopinion = request.getParameterValues("foreardOriginalopinion") != null;
        boolean track = request.getParameterValues("track") != null;

        senderOninion.affairIsTrack = track;

        //从request对象取到选人信息
        FlowData flowData = FlowData.flowdataFromRequest();
        
        // 2017-3-13 诚佰公司 流程转发时验证
        JSONObject mainMap = new JSONObject();
    	String secretLevel = request.getParameter("secretLevel");
    	String outMsg = validateFlow(secretLevel, flowData.getXml());
    	if (outMsg != null && !outMsg.isEmpty()) {
    		mainMap = new JSONObject();
			mainMap.put("secretAlert", outMsg);
	    	PrintWriter pw = response.getWriter();
	        pw.write(mainMap.toString());
	        pw.flush();
	        return null;
    	}
    	// 2017-3-13 诚佰公司

        Long newSummaryId = UUIDLong.longUUID();
        //保存附件
        String uploadAttFlag = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, newSummaryId, newSummaryId, request);

        // 通知全文检索不入库
        DateSharedWithWorkflowEngineThreadLocal.setNoIndex();

        ColSummary summary = this.colManager.saveForward(summaryId, newSummaryId, flowData, forwardOriginalNode, foreardOriginalopinion, senderOninion, uploadAttFlag,request.getParameter("formContent"));
        try {
//        	全文检索统一入库
    		if(IndexInitConfig.hasLuncenePlugIn()){
    			indexManager.index(((IndexEnable)colManager).getIndexInfo(newSummaryId));
    		}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		//拷贝ISIgnatureHTML 专业签章数据
		iSignatureHtmlManager.copyISignatureHtml2NewDocument(summaryId, newSummaryId);

        //转发协同操作日志
        appLogManager.insertLog(user, AppLogAction.Coll_Transmit, user.getName(), summary.getSubject());
        //流程日志
        if("FORM".equals(summary.getBodyType())){
        	BPMProcess process = ColHelper.getRunningProcessByProcessId(summary.getProcessId());
        	String  members = ColHelper.checSecondNodeMembers(process,flowData.getCondition());
        	processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), -1l, ProcessLogAction.sendForm,members);
        }else{
        	processLogManager.insertLog(user, Long.parseLong(summary.getProcessId()), -1l, ProcessLogAction.sendColl);
        }
        try {
			//事件类型通知
			CollaborationStartEvent event = new CollaborationStartEvent(this);
			event.setSummaryId(summary.getId());
			event.setFrom("pc");
			event.setAffairId(affairManager.getCollaborationSenderAffair(
					summary.getId()).getId());
			EventDispatcher.fireEvent(event);
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
        
        /** 2017-3-16 诚佰公司 注释 修改输出方式
		PrintWriter out = response.getWriter();
        out.println("<script>");
        if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
        	out.println("parent.afterForward()");
    	}else{//ipad
    		out.println("parent.parent.afterForward()");
    	}
        out.println("</script>");*/
        
        // 2017-3-16 诚佰公司
        mainMap = new JSONObject();
        if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
        	 outMsg = "parent.afterForward()";
    	}else{//ipad
    		 outMsg = "parent.parent.afterForward()";
    	}
		mainMap.put("afterForward", outMsg);
    	PrintWriter pw = response.getWriter();
        pw.write(mainMap.toString());
        pw.flush();

        return null;
    }

    /**
     * 协同管理-统计
     * @deprecated 该方法废弃待删除，新方法转移到WorkManageController
     * Mazc 2008-12-08
     */
    public ModelAndView manageOfStatistics(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("collaboration/manage");
        User user = CurrentUser.get();
        Long id = user.getId();

        boolean isTempleteManager = this.templeteCategoryManager.isTempleteManager(id, user.getLoginAccount());
        mv.addObject("isTempleteManager", isTempleteManager);
		try {

			StatModel stateModel = this.colManager.PersonalStatFilter(id);

			Map<String, Timestamp> maps = StatUtil.getDefaultStatDate();
			Map<String, String> times = new HashMap<String, String>();

			Set<String> keys = maps.keySet();
			for (String key : keys) {
			    Date date = maps.get(key);

			    String _date = com.seeyon.v3x.util.Datetimes.formatDate(date);
			    times.put(key, _date);
			}

			mv.addObject("stateModel", stateModel);

			mv.addObject("times", times);
		} catch (RuntimeException e) {
			log.error("协同统计失败", e);
		}

        return mv;
    }

    /*
     * 返回所需的I18N字符串
     */

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public ModelAndView audit(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String state = request.getParameter("state");

    	if("0".equals(state)){
    		request.setAttribute("auditPass", "2");
    		return this.finishWorkItem(request, response);
    	}else if("1".equals(state)){
    		request.setAttribute("auditBack", "3");
            return this.stepBack(request, response);
    	}
    	return null;
    }
	public ModelAndView vouch(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String state = request.getParameter("state");

    	if("0".equals(state)){
    		request.setAttribute("vouchPass", Constant.FormVouch.vouchPass.getKey());
    		return this.finishWorkItem(request, response);
    	}else if("1".equals(state)){
    		request.setAttribute("vouchBack", Constant.FormVouch.vouchBack.getKey());
            return this.stepBack(request, response);
    	}
    	return null;
    }
	/*
	 * 协同转换为邮件
	 */
	public ModelAndView forwordMail(HttpServletRequest request,HttpServletResponse response) throws Exception{

		User user = CurrentUser.get();
		try {
			MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(String.valueOf(user.getId()));
			if(mbc == null){
				ModelAndView mav = new ModelAndView("webmail/error");
				mav.addObject("errorMsg", "2");
				mav.addObject("url", "?method=list&jsp=set");
				return mav;
			}
		} catch (Exception e1) {
			log.error("调用邮件接口判断当前用户是否有邮箱设置：", e1);
		}

		Long summaryId = Long.parseLong(request.getParameter("id"));
		ColSummary summary = colManager.getColSummaryById(summaryId, true);
		String subject = summary.getSubject();
		ColBody body = summary.getFirstBody();

		List<Attachment> atts = new ArrayList<Attachment>();
		if (summary.isHasAttachments()) {
			atts = attachmentManager.getByReference(summaryId, summaryId);
		}

		Date createDate = body.getCreateDate();

		String bodyType = body.getBodyType();

		String bodyContent = "";
		if(Constants.EDITOR_TYPE_FORM.equals(bodyType)){ //表单
			bodyContent = request.getParameter("formContent");
		}
		else if(Constants.EDITOR_TYPE_HTML.equals(bodyType)){
			bodyContent = body.getContent();
		}
		else if(Constants.EDITOR_TYPE_WPS_WORD.equals(bodyType)
				|| Constants.EDITOR_TYPE_OFFICE_WORD.equals(bodyType)
				|| Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(bodyType)
				|| Constants.EDITOR_TYPE_WPS_EXCEL.equals(bodyType)){
			File file = null;
			try {
				file = this.fileManager.getStandardOffice(CoderFactory.getInstance().decryptFileToTemp(fileManager.getFile(Long.parseLong(body.getContent())).getAbsolutePath()));
				V3XFile f = this.fileManager.save(file, ApplicationCategoryEnum.mail, subject + "."+Convertor.getSufficName(bodyType), createDate, false);
				atts.add(new Attachment(f, ApplicationCategoryEnum.mail, com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE));
			}
			catch (Exception e) {
				log.error("协同转发为邮件错误 [summaryId = " + summaryId + "]", e);
			}
			finally{
//				IOUtils.closeQuietly(in);
			}
		}

		ModelAndView mv = webMailManager.forwordMail(summaryId, subject, bodyContent, atts);

		return mv;
	}

	/*
     * 后台管理协同工作
     */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView collSysMgr(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("collaboration/collSysMgr");
		return mav;
	}

	public void setWebMailManager(WebMailManager webMailManager) {
		this.webMailManager = webMailManager;
	}
	/**
	 * 用于协同转发等获取表单内容
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getFormContent(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String s_summaryId = request.getParameter("summaryId");
        String s_affairId = request.getParameter("affairId");
        long summaryId = Long.parseLong(s_summaryId);

        ColSummary summary = colManager.getColSummaryById(summaryId, true);
        Affair affair = affairManager.getById(Long.parseLong(s_affairId));

        String formContent = null;
    	if("FORM".equals(summary.getBodyType()))
    		formContent = getFormContent(summary, affair);

		return new ModelAndView("collaboration/formContent").addObject("formContent", formContent);
	}

    /**
     * 显示打印选择框
     */
    public ModelAndView showPrintSelector(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	//判断当前正文是否是表单模板
    	String isForm = request.getParameter("isForm");
    	ModelAndView mav = new ModelAndView("collaboration/printTypeSelector");
    	mav.addObject("isForm", isForm);
    	return mav;

    }

    private void saveColSupervise(HttpServletRequest request,HttpServletResponse response,ColSummary colSummary,boolean isNew,int state,boolean sendMessage) {
    	String supervisorId = request.getParameter("supervisorId");
        String supervisors = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
    	try{
	        if(Strings.isNotBlank(supervisorId) && !"undefined".equals(supervisorId)&& Strings.isNotBlank(awakeDate)&& !"undefined".equals(awakeDate)) {
	        	User user = CurrentUser.get();
		        //boolean canModifyAwake = "on".equals(request.getParameter("canModifyAwake"))?true:false;
		        String superviseTitle = request.getParameter("superviseTitle");
		        Date date = Datetimes.parse(awakeDate, Datetimes.datetimeStyle);
		        String[] idsStr = supervisorId.split(",");
		        long[] ids = new long[idsStr.length];
		        int i = 0;
		        for(String id:idsStr) {
		        	ids[i] = Long.parseLong(id);
		        	i++;
		        }
		        if(isNew)
		        	this.colSuperviseManager.save(colSummary.getImportantLevel(), colSummary.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, date, Constant.superviseType.summary.ordinal(), colSummary.getId(),state,sendMessage, colSummary.getForwardMember());
		        else
		        	this.colSuperviseManager.update(colSummary.getImportantLevel(), colSummary.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, date, Constant.superviseType.summary.ordinal(), colSummary.getId(),state,sendMessage, colSummary.getForwardMember());
	        } else {
	        	ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(), colSummary.getId());
	    		if(detail != null) {
	    			colSuperviseManager.deleteSuperviseById(detail.getId());
	    		}
	        }
		}catch(Exception e){
			log.error("保存督办的时候报错，supervisorId:"+supervisorId+",awakeDate:"+awakeDate,e);
		}
	}

    public ModelAndView fullEditor(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/fullEditor");
    	return mav;
    }

	/**
	 * 协同处理中预发布公告,新闻
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView preIssueNewsOrBull(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("collaboration/preIssueNewsOrBull");
		User user = CurrentUser.get();

		List<Long> customSpaceIds = spaceManager.getCanManagerCustomSpace(user.getId());

		List typeList = new ArrayList();
		Map<Long, String> spaceNames = new HashMap<Long, String>();
		String policyName = request.getParameter("policyName");
		if (policyName.equals("newsaudit")) {
			List<NewsType> list = newsTypeManager.getTypesCanNew(user.getId(), null, user.getAccountId());
			if (CollectionUtils.isNotEmpty(customSpaceIds)) {
				for (Long typeId : customSpaceIds) {
					NewsType type = newsTypeManager.getById(typeId);
					if (type != null) {
						list.add(type);
					}
				}
			}

			for (NewsType type : list) {
				if (type.getSpaceType().intValue() == NewsTypeSpaceType.custom.ordinal() || type.getSpaceType().intValue() == NewsTypeSpaceType.public_custom.ordinal() || type.getSpaceType().intValue() == NewsTypeSpaceType.public_custom_group.ordinal()) {
					SpaceFix fix = spaceManager.getSpace(type.getAccountId());
					if (fix != null) {
						typeList.add(type);
						spaceNames.put(type.getId(), fix.getSpaceName());
					}
				} else {
					typeList.add(type);
				}
			}
		} else {
			List<BulType> list = bulTypeManager.getTypesCanNew(user.getId(), null, user.getAccountId());
			if (CollectionUtils.isNotEmpty(customSpaceIds)) {
				for (Long typeId : customSpaceIds) {
					BulType type = bulTypeManager.getById(typeId);
					if (type != null) {
						list.add(type);
					}
				}
			}

			for (BulType type : list) {
				if (type.getSpaceType().intValue() == NewsTypeSpaceType.custom.ordinal() || type.getSpaceType().intValue() == NewsTypeSpaceType.public_custom.ordinal() || type.getSpaceType().intValue() == NewsTypeSpaceType.public_custom_group.ordinal()) {
					SpaceFix fix = spaceManager.getSpace(type.getAccountId());
					if (fix != null) {
						typeList.add(type);
						spaceNames.put(type.getId(), fix.getSpaceName());
					}
				} else {
					typeList.add(type);
				}
			}
		}

		if (CollectionUtils.isEmpty(typeList)) {
			PrintWriter out = response.getWriter();
			String msg = Constant.getString4CurrentUser("not.purview");
			out.println("<script>");
			out.println("	var arr = new Array();");
			out.println("	alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\");");
			out.println("	if(window.dialogArguments){"); // 弹出
			out.println("		window.returnValue = arr;");
			out.println("		window.close();");
			out.println("	}else{");
			out.println("		parent.getA8Top().reFlesh();");
			out.println("	}");
			out.println("</script>");
			out.close();
			return null;
		}

		mav.addObject("type", policyName);
		mav.addObject("typeList", typeList);
		mav.addObject("spaceNames", spaceNames);
		mav.addObject("typeObj", typeList.get(0));
		return mav;
	}

    /**
     * 发布新闻
     *
     * @author jincm 2008-5-29
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView issusNews(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	String typeId = request.getParameter("typeId");
    	String summaryId = request.getParameter("summaryId");
    	String memberIdsStr = request.getParameter("memberIdsStr");

    	String affairId = request.getParameter("affairId");
    	Affair affair = affairManager.getById(Long.parseLong(affairId));
    	if(affair.getState() != StateEnum.col_pending.key()){
			String msg = ColHelper.getErrorMsgByAffair(affair);
        	PrintWriter out = response.getWriter();
    		out.println("<script>");
			out.println("var arr = new Array();");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
        	out.println("if(window.dialogArguments){"); //弹出
        	out.println("  window.returnValue = arr;");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println("  parent.getA8Top().reFlesh();");
        	out.println("}");
        	out.println("</script>");
        	out.close();
        	return null;
        }

    	ColSummary summary = colManager.getColSummaryById(Long.parseLong(summaryId), true);
    	NewsType type = this.newsTypeManager.getById(Long.parseLong(typeId));
    	NewsData bean = new NewsData();
    	bean.setIdIfNew();

    	java.util.Set<ColBody> bodies = summary.getBodies();
    	Iterator its = bodies.iterator();
    	ColBody body = null;
		while(its.hasNext()){
			body = (ColBody)its.next();
		}

		bean.setCreateDate(new Date());
		bean.setPublishDate(new Date());
		//bean.setCreateUser(CurrentUser.get().getId());
		bean.setCreateUser(summary.getStartMemberId());
		bean.setAuditDate(new Timestamp(System.currentTimeMillis()));
		bean.setAuditUserId(user.getId());
		bean.setDataFormat(body.getBodyType());
		bean.setReadCount(0);

		bean.setTitle(summary.getSubject());//标题
		bean.setTypeId(Long.parseLong(typeId));
		bean.setType(type);
		bean.setTopOrder(Byte.valueOf("0"));
		//bean.setPublishUserId(user.getId());
		bean.setPublishUserId(summary.getStartMemberId());
		bean.setAccountId(user.getAccountId());
		bean.setDeletedFlag(false);
		bean.setState(com.seeyon.v3x.news.util.Constants.DATA_STATE_ALREADY_PUBLISH);
		//增加标识，表明此新闻是由协同转发
		bean.setExt4(ApplicationCategoryEnum.collaboration.name());

		Long _summaryId = Long.parseLong(summaryId);
		Long beanId = bean.getId();
		
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(body.getBodyType())){//HTML正文
			bean.setContent(body.getContent());
		}
		else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM.equals(body.getBodyType())){//表单
			bean.setContent(getFormContent(summary, affair));

			//处理表单中的附件
			List<Long> formIdCacheList = new ArrayList<Long>();
			List<Attachment> attachmentList = getMainRunAtt(_summaryId) ;
			if(attachmentList != null && !attachmentList.isEmpty()){
				for( int i=0 ; i<attachmentList.size() ; i++ ){
					Attachment _attachment = attachmentList.get(i);
					Long formAttrFieldId = _attachment.getSubReference();
					
					if( formIdCacheList.contains(formAttrFieldId) ){
						continue;
					}else{
						formIdCacheList.add(formAttrFieldId);
					}
					
					this.attachmentManager.copy(_summaryId, formAttrFieldId, beanId, formAttrFieldId, ApplicationCategoryEnum.news.key());
					
				}
				bean.setAttachmentsFlag(true);
			}
			formIdCacheList.clear();
			formIdCacheList=null;
			
		}
		else {//复制office对应的正文
			V3XFile f = fileManager.clone(Long.parseLong(body.getContent()),true);
			bean.setContent(f.getId().toString());
		}
		List<Attachment> atts = this.attachmentManager.getByReference(_summaryId, _summaryId);
        boolean hasAttFlag = false;
        for (Attachment att : atts) {
            if (!att.getType().equals(Integer.valueOf(1))) {
                hasAttFlag = true;
                break;
            }
        }
		if(hasAttFlag){
			this.attachmentManager.copy(_summaryId, _summaryId, beanId, beanId, ApplicationCategoryEnum.news.key());//附件
			bean.setAttachmentsFlag(true);
		}
		if(type.getSpaceType().intValue() == com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType.department.ordinal()){
			bean.setPublishScope("Department|"+type.getId().toString());
		}else{
			bean.setPublishScope(memberIdsStr);
		}

		//设置发布部门
		if(bean.getPublishDepartmentId()==null){
			//设置为发起者所在部门
			Long userId = bean.getCreateUser();
			Long depId = ((BaseNewsManager)this.newsDataManager).getNewsUtils().getMemberById(userId).getOrgDepartmentId();
			bean.setPublishDepartmentId(depId);
		}
		//发布新闻
		this.newsDataManager.saveCollNews(bean);

		V3xOrgMember om = orgManager.getMemberById(bean.getPublishUserId());

		//直接发送不审合消息
		Set<Long> resultIds = new HashSet<Long>();
		List<V3xOrgMember> listMemberId = ((BaseNewsManager)this.newsDataManager).getNewsUtils().getScopeMembers(type.getSpaceType(),
				CurrentUser.get().getLoginAccount(),type.getOutterPermit());
		for (V3xOrgMember member : listMemberId) {
			resultIds.add(member.getId());
		}

		userMessageManager.sendSystemMessage(MessageContent.get("news.auditing",
				bean.getTitle(),om.getName()),
				ApplicationCategoryEnum.news, om.getId(),
				MessageReceiver.get(new Long(ApplicationCategoryEnum.news.getKey()),resultIds,"message.link.news.assessor.auditing",
						String.valueOf(bean.getId())), bean.getTypeId());

		super.rendJavaScript(response, "parent.issueFinishDo();");
    	return null;
    }

    /**
     * 发布公告
     *
     * @author jincm 2008-5-29
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView issusBulletion(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	String typeId = request.getParameter("typeId");
    	String summaryId = request.getParameter("summaryId");
    	String memberIdsStr = request.getParameter("memberIdsStr");

    	String affairId = request.getParameter("affairId");
    	Affair affair = affairManager.getById(Long.parseLong(affairId));
    	if(affair.getState() != StateEnum.col_pending.key()){
			String msg = ColHelper.getErrorMsgByAffair(affair);
        	PrintWriter out = response.getWriter();
    		out.println("<script>");
			out.println("var arr = new Array();");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
        	out.println("if(window.dialogArguments){"); //弹出
        	out.println("  window.returnValue = arr;");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println("  parent.getA8Top().reFlesh();");
        	out.println("}");
        	out.println("</script>");
        	out.close();
        	return null;
        }

    	ColSummary summary = colManager.getColSummaryById(Long.parseLong(summaryId), true);
    	BulType type = this.bulTypeManager.getById(Long.parseLong(typeId));
    	BulData bean = new BulData();
    	bean.setIdIfNew();

    	java.util.Set<ColBody> bodies = summary.getBodies();
    	Iterator its = bodies.iterator();
    	ColBody body = null;
		while(its.hasNext()){
			body = (ColBody)its.next();
		}

		bean.setTitle(summary.getSubject());//标题
    	bean.setCreateDate(new Timestamp(System.currentTimeMillis()));
    	bean.setPublishDate(new Timestamp(System.currentTimeMillis()));
		//bean.setCreateUser(user.getId());
    	bean.setCreateUser(summary.getStartMemberId());
		bean.setAuditDate(new Timestamp(System.currentTimeMillis()));
		bean.setAuditUserId(user.getId());
		bean.setDataFormat(body.getBodyType());
		bean.setReadCount(0);
		bean.setTypeId(Long.parseLong(typeId));
		bean.setType(type);
		bean.setTopOrder(Byte.valueOf("0"));
		//bean.setPublishUserId(user.getId());
		bean.setPublishUserId(summary.getStartMemberId());
		bean.setAccountId(user.getAccountId());
		bean.setDeletedFlag(false);
		//是否允许打印
		bean.setExt2(request.getParameter("allowPrint"));
		//增加标识，表明此公告是由协同转发
		bean.setExt4(ApplicationCategoryEnum.collaboration.name());

		Long _summaryId = Long.parseLong(summaryId);
		Long beanId = bean.getId();
		
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(body.getBodyType())){//HTML正文
			bean.setContent(body.getContent());
		}
		else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_FORM.equals(body.getBodyType())){//表单
			bean.setContent(getFormContent(summary, affair));
			
			//处理表单中的附件
			List<Long> formIdCacheList = new ArrayList<Long>();
			List<Attachment> attachmentList = getMainRunAtt(_summaryId) ;
			if(attachmentList != null && !attachmentList.isEmpty()){
				for( int i=0 ; i<attachmentList.size() ; i++ ){
					Attachment _attachment = attachmentList.get(i);
					Long formAttrFieldId = _attachment.getSubReference();
					
					if( formIdCacheList.contains(formAttrFieldId) ){
						continue;
					}else{
						formIdCacheList.add(formAttrFieldId);
					}
					
					this.attachmentManager.copy(_summaryId, formAttrFieldId, beanId, formAttrFieldId, ApplicationCategoryEnum.bulletin.key());
					
				}
				bean.setAttachmentsFlag(true);
			}
			formIdCacheList.clear();
			formIdCacheList=null;
		}
		else {//复制office对应的正文
			V3XFile f = fileManager.clone(Long.parseLong(body.getContent()),true);
			bean.setContent(f.getId().toString());
			//复制正文对应的印章
			SignetManager sm=(SignetManager)ApplicationContextHolder.getBean("signetManager");
			sm.insertSignet(Long.parseLong(body.getContent()), f.getId());
			bean.setContentName(body.getContent());
		}

		boolean hasAttFlag = this.attachmentManager.hasAttachments(_summaryId, _summaryId);
		if(hasAttFlag){
			this.attachmentManager.copy(_summaryId, _summaryId, beanId, beanId, ApplicationCategoryEnum.bulletin.key());//附件
			bean.setAttachmentsFlag(true);
		}
		if(type.getSpaceType().intValue() == com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType.department.ordinal()){
			bean.setPublishScope("Department|"+type.getId().toString());
		}else{
			bean.setPublishScope(memberIdsStr);
		}

		//设置发布部门
		if(bean.getPublishDepartmentId()==null){
			//设置为发起者所在部门
			Long userId = bean.getCreateUser();
			Long depId=((BaseBulletinManager)this.bulDataManager).getBulletinUtils().getMemberById(userId).getOrgDepartmentId();
			bean.setPublishDepartmentId(depId);
		}
		//发布公告
		this.bulDataManager.saveCollBulletion(bean);

		V3xOrgMember member = orgManager.getMemberById(bean.getPublishUserId());
		//直接发送不审合消息
		Set<Long> bulAudits = new HashSet<Long>();
		Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(bean.getPublishScope());
		if(membersSet != null && membersSet.size() > 0){
			for(V3xOrgMember om : membersSet){
				bulAudits.add(om.getId());
			}
		}

		userMessageManager.sendSystemMessage(
				MessageContent.get("bul.auditing",
				bean.getTitle(),member.getName()),
				ApplicationCategoryEnum.bulletin,
				member.getId(),
				MessageReceiver.get(bean.getId(),bulAudits,"message.link.bul.alreadyauditing",String.valueOf(bean.getId())),
				bean.getTypeId());

		super.rendJavaScript(response, "parent.issueFinishDo();");
    	return null;
    }

    /**
     * 流程管理
     * @author jincm 2008-6-05
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView workflowManager(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	ModelAndView mav = new ModelAndView("collaboration/workflowHome");
    	List<Integer> appEnumKeyList = new ArrayList<Integer>();
    	if(user.isGroupAdmin()){
	    	appEnumKeyList.add(ApplicationCategoryEnum.collaboration.key());
	    	boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
			if(isGov) {
				if(SystemEnvironment.hasPlugin("form"))
		    		appEnumKeyList.add(ApplicationCategoryEnum.form.key());
			}else{
				appEnumKeyList.add(ApplicationCategoryEnum.form.key());
			}
	    	   	if(com.seeyon.v3x.common.taglibs.functions.Functions.isEnableEdoc()){
	    		appEnumKeyList.add(ApplicationCategoryEnum.edocSend.key());
	    		appEnumKeyList.add(ApplicationCategoryEnum.edocRec.key());
	    		appEnumKeyList.add(ApplicationCategoryEnum.edocSign.key());
	    	}
	    	  //branches_a8_v350sp1_r_gov GOV-1598 魏俊标增加信息报送查询条件 start
	    	    boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
	            if(isGovVersion){
	           	 appEnumKeyList.add(ApplicationCategoryEnum.info.key());
	            } 
	          //branches_a8_v350sp1_r_gov GOV-1598 魏俊标增加信息报送查询条件 end
    	}else{
    		appEnumKeyList.add(ApplicationCategoryEnum.collaboration.key());
    		boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
			if(isGov) {
				if(SystemEnvironment.hasPlugin("form"))
		    		appEnumKeyList.add(ApplicationCategoryEnum.form.key());
			}else{
				appEnumKeyList.add(ApplicationCategoryEnum.form.key());
			}
	    	if(com.seeyon.v3x.common.taglibs.functions.Functions.isEnableEdoc()){
	    		appEnumKeyList.add(ApplicationCategoryEnum.edocSend.key());
	    		appEnumKeyList.add(ApplicationCategoryEnum.edocRec.key());
	    		appEnumKeyList.add(ApplicationCategoryEnum.edocSign.key());
	    	}
	    	  //branches_a8_v350sp1_r_gov GOV-1598 魏俊标增加信息报送查询条件 start
    	    boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
            if(isGovVersion){
           	 appEnumKeyList.add(ApplicationCategoryEnum.info.key());
            } 
          //branches_a8_v350sp1_r_gov GOV-1598 魏俊标增加信息报送查询条件 end
    	}

    	boolean isGroupAdmin = false;
    	if(user.isGroupAdmin()){
    		isGroupAdmin = true;
    	}
    	Date firstDay = Datetimes.getFirstDayInWeek(new Date(System.currentTimeMillis()));
    	Date lastDay = Datetimes.getLastDayInWeek(new Date(System.currentTimeMillis()));
		mav.addObject("firstDay", firstDay);
		mav.addObject("lastDay", lastDay);
    	mav.addObject("appEnumKeyList", appEnumKeyList);
    	mav.addObject("isGroupAdmin", isGroupAdmin);
    	mav.addObject("flowType", "self");
    	return mav;
    }

    /**
     * 流程管理查询详细列表信息
     * @author jincm 2008-6-05
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     * <pre>modify by lilong 2012-01-04 部门授权修改，是否包含子部门判断</pre>
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView workflowDataList(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	ModelAndView mav = new ModelAndView("collaboration/workflowDataList");
    	int appEnumKey = Integer.parseInt(request.getParameter("selectedAppEnumKey"));
    	String operationTypeIdsStr = request.getParameter("templeteId");
    	String operationType = request.getParameter("operationType");

    	int flowstate = Integer.parseInt(request.getParameter("flowstate"));
    	String beginDate = request.getParameter("beginDate");
    	String endDate = request.getParameter("endDate");
    	String subject = request.getParameter("subject");
    	String colonyIdsValue = request.getParameter("colonyIdsValue");

    	//发起对象
    	String[] colonyArr = colonyIdsValue.split(",");
    	List<String> objectStrs = new ArrayList<String>();
    	if(colonyArr != null && !"".equals(colonyArr[0])){
    		for(int i=0; i<colonyArr.length; i++){
    			objectStrs.add(colonyArr[i]);
    		}
    	}

    	List<WorkflowData> workflowDataList = new ArrayList<WorkflowData>();
    	if(!user.isGroupAdmin()){
    		String[] operationTypeIds = operationTypeIdsStr.split(",");
    		if(appEnumKey == ApplicationCategoryEnum.collaboration.key()
    				|| appEnumKey == ApplicationCategoryEnum.form.key()){
    			workflowDataList = colManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    					flowstate, appEnumKey, operationType, operationTypeIds, true);
    		}else if(appEnumKey == ApplicationCategoryEnum.edocRec.key()
    				|| appEnumKey == ApplicationCategoryEnum.edocSend.key()
    				|| appEnumKey == ApplicationCategoryEnum.edocSign.key()){
    			workflowDataList = edocManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    					flowstate, appEnumKey, operationType, operationTypeIds, true);
    		}else if(appEnumKey == ApplicationCategoryEnum.info.key()){
    			List<WorkflowDataCAP> list = infoManagerCAP.queryInfoWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    					flowstate, appEnumKey, operationType, operationTypeIds, true);
    			for(int i=0;i<list.size();i++){
					WorkflowData workflowData = new WorkflowData();
					BeanUtils.convert(workflowData,list.get(i));
					workflowDataList.add(workflowData);
				}
    		}
    	}else{
    		if(appEnumKey == ApplicationCategoryEnum.collaboration.key()
    				|| appEnumKey == ApplicationCategoryEnum.form.key()){
    			workflowDataList = colManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    					flowstate, appEnumKey, null, null, true);
    		}else if(appEnumKey == ApplicationCategoryEnum.edocRec.key()
    				|| appEnumKey == ApplicationCategoryEnum.edocSend.key()
    				|| appEnumKey == ApplicationCategoryEnum.edocSign.key()) {
    			workflowDataList = edocManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    					flowstate, appEnumKey, null, null, true);
    		}//branches_a8_v350sp1_r_gov GOV-1598 魏俊标查询信息报送功能 start
    		else if(appEnumKey == ApplicationCategoryEnum.info.key()){
    				List<WorkflowDataCAP> list = infoManagerCAP.queryInfoWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    					flowstate, appEnumKey, null, null, true);
    				for(int i=0;i<list.size();i++){
    					WorkflowData workflowData = new WorkflowData();
    					BeanUtils.convert(workflowData,list.get(i));
    					workflowDataList.add(workflowData);
    				}
    		}
    		//branches_a8_v350sp1_r_gov GOV-1598 魏俊标查询信息报送功能 end
    	}

    	mav.addObject("defaultSubject", null);
    	mav.addObject("appEnumKeyReal", appEnumKey);
    	mav.addObject("workflowDataList", workflowDataList);
    	return mav;
    }

    /**
     * 流程管理查询详细列表导出excel
     * @author jincm 2008-6-05
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView workflowDataToExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	String operationTypeIdsStr = request.getParameter("templeteId");
    	String operationType = request.getParameter("operationType");

    	String beginDate = request.getParameter("beginDate");
    	String endDate = request.getParameter("endDate");
    	String subject = request.getParameter("subject");
    	String colonyIdsValue = request.getParameter("colonyIdsValue");
    	List<WorkflowData> workflowDataList = new ArrayList<WorkflowData>();
    	String selectedAppEnumKey = request.getParameter("selectedAppEnumKey");
    	if(Strings.isNotBlank(selectedAppEnumKey)){
    		int appEnumKey = Integer.parseInt(selectedAppEnumKey);
    		int flowstate = Integer.parseInt(request.getParameter("flowstate"));
    		//发起对象
    		String[] colonyArr = colonyIdsValue.split(",");
    		List<String> objectStrs = new ArrayList<String>();
    		if(colonyArr != null && !"".equals(colonyArr[0])){
    			for(int i=0; i<colonyArr.length; i++){
    				objectStrs.add(colonyArr[i]);
    			}
    		}

    		if(!user.isGroupAdmin()){
    			String[] operationTypeIds = operationTypeIdsStr.split(",");
    			if(appEnumKey == ApplicationCategoryEnum.collaboration.key()
    					|| appEnumKey == ApplicationCategoryEnum.form.key()){
    				workflowDataList = colManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    						flowstate, appEnumKey, operationType, operationTypeIds, false);
    			}//else if(appEnumKey == ApplicationCategoryEnum.edoc.key())
    			else if(appEnumKey == ApplicationCategoryEnum.edocRec.key()
    					|| appEnumKey == ApplicationCategoryEnum.edocSend.key()
    					|| appEnumKey == ApplicationCategoryEnum.edocSign.key()) {
    				workflowDataList = edocManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    						flowstate, appEnumKey, operationType, operationTypeIds, false);
    			}
    		}else{
    			if(appEnumKey == ApplicationCategoryEnum.collaboration.key()
    					|| appEnumKey == ApplicationCategoryEnum.form.key()){
    				workflowDataList = colManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    						flowstate, appEnumKey, null, null, false);
    			}else if(appEnumKey == ApplicationCategoryEnum.edocRec.key()
    					|| appEnumKey == ApplicationCategoryEnum.edocSend.key()
    					|| appEnumKey == ApplicationCategoryEnum.edocSign.key()) {
    				workflowDataList = edocManager.queryWorkflowDataByCondition(subject, beginDate, endDate, objectStrs,
    						flowstate, appEnumKey, null, null, false);
    			}
    		}
    	}
    	String[] columnName = new String[6];
		String commonResource = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";

		String title = ColHelper.getI18NString(commonResource, "workflow.information.list");

		columnName[0] = ColHelper.getI18NString(commonResource, "common.app.type");
		columnName[1] = ColHelper.getI18NString(commonResource, "common.subject.label");
		columnName[2] = ColHelper.getI18NString(commonResource, "initiator.respective.department");
		columnName[3] = ColHelper.getI18NString(commonResource, "common.sender.label");
		columnName[4] = ColHelper.getI18NString(commonResource, "common.send.time.label");
		columnName[5] = ColHelper.getI18NString(commonResource, "common.endflag");

		String yes = ColHelper.getI18NString(commonResource, "common.true");
		String no = ColHelper.getI18NString(commonResource, "common.false");

		List<Object[]> rows = new ArrayList<Object[]>();
		Object[] obj = null;
		for(WorkflowData workflowData : workflowDataList){
			obj = new Object[6];
			obj[0] = workflowData.getAppType();
			obj[1] = workflowData.getSubject();
			obj[2] = workflowData.getDepName();
			obj[3] = workflowData.getInitiator();
			obj[4] = Datetimes.formatDatetimeWithoutSecond(workflowData.getSendTime());
			obj[5] = workflowData.getEndFlag() == 1 ? no : yes;
			rows.add(obj);
		}
		ColHelper.exportToExcel(request, response, fileToExcelManager, "workflowData", rows, columnName, title, "sheet1");

    	return null;
    }

    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView workflowDataDetailFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/workflowDataDetailFrame");
    	return mav;
    }

    /**
     * 流程处理明细查看
     * @author jincm 2008-6-10
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView workflowDataDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	User user = CurrentUser.get();
    	boolean groupAdminFlag = user.isGroupAdmin();
    	long userAccount=user.getAccountId();//tanglh

    	ModelAndView mav = new ModelAndView("collaboration/workflowDataDetail");
    	String summaryId = request.getParameter("summaryId");
    	List<Affair> affairList = affairManager.getALLAvailabilityAffairList(Long.parseLong(summaryId), true);

    	ColSummary summary = colManager.getColSummaryById(Long.parseLong(summaryId), false);

    	List<WorkflowDataDetail> flowDataDetailList = new ArrayList<WorkflowDataDetail>();
    	if(affairList != null && affairList.size() != 0){

    		//判断事项中是否存在不同单位人员，有则显示单位简称
    		/*
    		boolean isShowShortname = false;
    		Long _accountId = 0L;
    		for(Affair affair : affairList){
    			Long memberId = affair.getMemberId();
        		V3xOrgMember member = orgManager.getMemberById(memberId);
        		Long accountId = orgManager.getAccountById(member.getOrgAccountId()).getId();
    			if(_accountId != 0 && accountId != _accountId){
    				isShowShortname = true;
    				break;
    			}
    			_accountId = accountId;
    		}
    		*/
    		//tanglh  对集团管理员都显示单位简称，对单位管理员只要是外单位都显示单位简称

    		for(Affair affair : affairList){
        	    //Affair中有节点权限，直接从Affair中取，否则从Process中取
        	    boolean isGetNodePolicyFromAffair = Strings.isNotBlank(affairList.get(0).getNodePolicy());
        	    BPMProcess process = null;

        	    if(!isGetNodePolicyFromAffair){
        	    	process = ColHelper.getCaseProcess(summary.getProcessId());
        	    }

    			String handler = "";
    			String policyName = "";
    			java.sql.Timestamp createDate = null;
    			java.sql.Timestamp finishDate = null;
    			String deadline = String.valueOf(0);
    			String dealTime = String.valueOf(0);
    			String deadlineTime = String.valueOf(0);
    			int app = affair.getApp();
    			if(app == ApplicationCategoryEnum.exSend.getKey()
					|| 	app == ApplicationCategoryEnum.exSign.getKey()
    				|| 	app == ApplicationCategoryEnum.edocRegister.getKey()
    				){
    				continue;
    			}

    			if(affair.getState() == StateEnum.col_sent.key()){
    				Long senderId = affair.getSenderId();
    				V3xOrgMember member = orgManager.getMemberById(senderId);
    				handler = member.getName();
    				long mAccount=member.getOrgAccountId();//tanglh
    				if(groupAdminFlag || mAccount!=userAccount){//tanglh
    					String accountShortname = orgManager.getAccountById(member.getOrgAccountId()).getShortname();
    					handler += "(" + accountShortname + ")";
    				}
    				createDate = affair.getCreateDate();
    				policyName = Constant.getCommonString("node.policy.collaboration");
    			}else{
    				Long memberId = affair.getMemberId();
    				V3xOrgMember member = orgManager.getMemberById(memberId);
    				handler = member.getName();
    				long mAccount=member.getOrgAccountId();//tanglh
    				if(groupAdminFlag || mAccount!=userAccount){//tanglh
    					String accountShortname = orgManager.getAccountById(member.getOrgAccountId()).getShortname();
    					handler += "(" + accountShortname + ")";
    				}
    				if(isGetNodePolicyFromAffair){
    					policyName = BPMSeeyonPolicy.getShowName(affair.getNodePolicy());
    				}
    				else{
    					SeeyonPolicy seeyonPolicy = ColHelper.getPolicyByAffair(process, affair);
    					policyName = BPMSeeyonPolicy.getShowName(seeyonPolicy.getId());
    				}

    				createDate = affair.getReceiveTime();
    				finishDate = affair.getCompleteTime();
    				Date _createDate = new Date(createDate.getTime());
    				Date _finishDate = null;
    				if(finishDate != null)
    					_finishDate = new Date(finishDate.getTime());

    				long[] intervalTime = Datetimes.detailInterval(_createDate, _finishDate);
    				if(intervalTime != null){
	    				long date = intervalTime[0];
	    				long hour = intervalTime[1];
	    				long minut = intervalTime[2];
	    				long second = intervalTime[3];
	    				dealTime = ColHelper.timePatchwork(date, hour, minut, second, false);
    				}

    				if(affair.getDeadlineDate() != null && affair.getDeadlineDate() != 0){
	    				long[] deadlineArr = Datetimes.formatLongToTimeStr(affair.getDeadlineDate()*60000);
	    				long date1 = deadlineArr[0];
	    				long hour1 = deadlineArr[1];
	    				long minut1 = deadlineArr[2];
	    				deadline = ColHelper.timePatchwork(date1, hour1, minut1, 0L, true);

	    				long timeOutQuantum = 0L;
	    				if(finishDate == null){
	    					timeOutQuantum = System.currentTimeMillis()-(createDate.getTime()+affair.getDeadlineDate()*(60*1000));
	    				}else{
	    					timeOutQuantum = finishDate.getTime()-(createDate.getTime()+affair.getDeadlineDate()*(60*1000));
	    				}
	    				if(timeOutQuantum > 0){
	    					long[] timeOutQuantumArr = Datetimes.formatLongToTimeStr(timeOutQuantum);
	        				long date2 = timeOutQuantumArr[0];
	        				long hour2 = timeOutQuantumArr[1];
	        				long minut2 = timeOutQuantumArr[2];
	        				long second2 = timeOutQuantumArr[3];
	        				deadlineTime = ColHelper.timePatchwork(date2, hour2, minut2, second2, false);
	    				}
    				}
    			}
    			WorkflowDataDetail workflowDataDetail = new WorkflowDataDetail();
    			workflowDataDetail.setCreateDate(createDate);
    			workflowDataDetail.setDeadline(deadline);
    			workflowDataDetail.setDealTime(dealTime);
    			workflowDataDetail.setEntityId(affair.getId());
    			workflowDataDetail.setFinishDate(finishDate);
    			workflowDataDetail.setHandler(handler);
    			workflowDataDetail.setPolicyName(policyName);
    			workflowDataDetail.setDeadlineTime(deadlineTime);
    			workflowDataDetail.setOverWorkTime(affair.getOverWorkTime());
    			flowDataDetailList.add(workflowDataDetail);
    		}
    	}
    	mav.addObject("fromOperation", "workflowManager");
    	mav.addObject("dataList", flowDataDetailList);

    	return mav;
    }

    /**
     * 预匹配人员
     * @author jincm 2008-6-05
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    public ModelAndView preMatchPeople(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/preMatchPeople");
    	String nodeId = request.getParameter("nodeId");
    	Long _nodeId = Long.parseLong(nodeId.substring(21));
    	String ids = ColHelper.matchPeople.remove(_nodeId);
    	if(Strings.isNotBlank(ids)){
    		mav.addObject("people", ids.split(","));
    	}

    	return mav;
    }

    public ModelAndView showFlowNodeDetailFrame(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/showFlowNodeDetailFrame");
    	return mav;
    }

    /**
     * 显示催办日志
     * @author
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    public ModelAndView showRemindersLog(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/showRemindersLogDetail");
		Long summaryId = Long.parseLong(request.getParameter("summaryId"));

		List<ColSuperviseLog> logList = colSuperviseManager.getColSuperviseLogBySummaryId(summaryId);

        return mav.addObject("logList", logList);
    }

    /**
     * 导出催办日志 csv 格式
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView exportRemindersLog(HttpServletRequest request,HttpServletResponse response) throws Exception{

    	Long summaryId = Long.parseLong(request.getParameter("summaryId"));

    	List<ColSuperviseLog> logList = colSuperviseManager.getColSuperviseLogBySummaryId(summaryId);

        String title = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.life.log.label") ;

    	String num = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.number.label") ;
    	String hastener = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "col.supervise.hastener") ;
    	String hastenTime = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "col.supervise.hastenTime") ;
    	String receiver = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "col.supervise.receiver") ;
    	String content = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "col.supervise.life.content") ;

    	String[] columnName = {num,hastener,hastenTime,receiver,content} ;

    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(title) ;
    	dataRecord.setColumnName(columnName) ;

    	if (logList != null) {
        	for (int i = 0 ; i < logList.size() ; i ++) {
        		ColSuperviseLog data = logList.get(i) ;
        		DataRow dataRow = new DataRow();
        		dataRow.addDataCell(String.valueOf(i+1), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showMemberName(Long.valueOf(String.valueOf(data.getSender()))), DataCell.DATA_TYPE_TEXT);

        		dataRow.addDataCell(Datetimes.format(data.getSendTime(), Datetimes.datetimeStyle), DataCell.DATA_TYPE_TEXT) ;
        		dataRow.addDataCell(Functions.showMemberName(Long.valueOf(String.valueOf(data.getReveiverIds()))), DataCell.DATA_TYPE_TEXT) ;

        		if (Strings.isEmpty(data.getContent()))
        			dataRow.addDataCell("-", DataCell.DATA_TYPE_TEXT) ;
        		else
        			dataRow.addDataCell(data.getContent(), DataCell.DATA_TYPE_TEXT) ;

        		dataRecord.addDataRow(dataRow);
        	}
    	}
    	fileToExcelManager.saveAsCSV(request,response,title,dataRecord);

    	return null;
    }

    /**
     * 查看流程催办日志
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showFlowNodeDetail(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/workflowDataDetail");
    	List<WorkflowDataDetail> flowDataDetailList = this.queryListValue(request, response,true) ;
    	mav.addObject("fromOperation", "checkFlowDetail");
    	mav.addObject("dataList", flowDataDetailList);

    	return mav;
    }
    /**
     * portal显示重要程度的页面
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showPortalImportLevel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/showPortalImportLevel");
    	return mav;
    }
    /**
     * Portal事件来源指定分类
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView showPortalCatagory(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/showPortalCatagory");
    	return mav;
    }

    /**
     * 流程处理明细导出 Excel，导出全部
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    public ModelAndView exportExcel(HttpServletRequest request,HttpServletResponse response) throws Exception {

    	String isOverWorkTime = request.getParameter("isOverWorkTime");
    	String isEfficiency=request.getParameter("isEfficiency");
    	String detail = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.wokflow.deal.detail.alt") ;
    	String isEfficiencyTitle = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.efficiency.analysis.label") ;

    	String handler = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.handler") ;
    	String policy = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.policy") ;
    	String state = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.deal.state") ;
    	String create = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.create.date") ;
    	String finish = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.finish.date") ;
    	String dealTime = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.dealTime.date") ;
    	String deadline = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.deadline.date") ;
    	String timeout =ResourceBundleUtil.getString("com.seeyon.v3x.workflowanalysis.resources.i18n.WorkflowAnalysisResources", "common.timeouts.label") ;
    	String overTimeAnalysis = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.timeout.analysis.label") ;
    	String[] columnName = {handler,policy,state,create,finish,dealTime,deadline,timeout} ;

    	DataRecord dataRecord = new DataRecord() ;
    	if(isOverWorkTime !=null && isOverWorkTime.equals("true")){
    		detail = overTimeAnalysis ;
    	}
    	if(isEfficiency!=null&&isEfficiency.equals("true")){
    		detail=isEfficiencyTitle;
    	}
   		dataRecord.setSheetName(detail) ;
    	dataRecord.setColumnName(columnName) ;

    	List<WorkflowDataDetail> flowDataDetailList = new ArrayList<WorkflowDataDetail>();
    	flowDataDetailList = this.queryListValue(request, response,false);
    	for (int i = 0 ; i < flowDataDetailList.size() ; i ++) {

    		WorkflowDataDetail data = flowDataDetailList.get(i) ;
    		DataRow dataRow = new DataRow();
    		dataRow.addDataCell(data.getHandler(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getPolicyName(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data.getStateLabel(), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(Datetimes.format(data.getCreateDate(), Datetimes.datetimeStyle), DataCell.DATA_TYPE_DATE) ;
    		dataRow.addDataCell(data.getFinishDate() == null ? "-" : Datetimes.format(data.getFinishDate(), Datetimes.datetimeStyle), DataCell.DATA_TYPE_TEXT) ;
    		if (data.getDealTime() != null) {
    			if ("".equals(data.getDealTime()))
    				dataRow.addDataCell("-", DataCell.DATA_TYPE_TEXT) ;
    			else {
    				if ("0".equals(data.getDealTime()))
    					dataRow.addDataCell("-", DataCell.DATA_TYPE_TEXT) ;
    				else
    					dataRow.addDataCell(Functions.showDateByWork(data.getRunWorkTime()==null? 0:data.getRunWorkTime().intValue()), DataCell.DATA_TYPE_DATETIME) ;
    			}
    		}
    		if (data.getDeadline() != null) {
    			if ("".equals(data.getDeadline()))
    				dataRow.addDataCell("-", DataCell.DATA_TYPE_TEXT) ;
    			else {
    				if ("0".equals(data.getDeadline()))
    					dataRow.addDataCell("-", DataCell.DATA_TYPE_TEXT) ;
    				else
    					dataRow.addDataCell(data.getDeadline().toString(), DataCell.DATA_TYPE_TEXT) ;
    			}
    		}

    		dataRow.addDataCell(Functions.showDateByWork(data.getOverWorkTime() == null ? 0 : Integer.parseInt(data.getOverWorkTime().toString())), DataCell.DATA_TYPE_TEXT) ;

    		dataRecord.addDataRow(dataRow);
    	}

    	fileToExcelManager.saveAsCSV(request,response,detail,dataRecord);
    	return null;

    }


    public List<WorkflowDataDetail> queryListValue(HttpServletRequest request,HttpServletResponse response, boolean isPage) throws Exception {

    	String summaryId = request.getParameter("summaryId");
    	String appName=request.getParameter("appName");
    	String appTypeName=request.getParameter("appTypeName");
    	List<Affair> affairList=new ArrayList<Affair>();
    	boolean isStoreFlag = false; //是否转储标记

    	if("4".equals(appName)){
    		if(EdocEnum.edocType.sendEdoc.name().equals(appTypeName)
    				||ApplicationCategoryEnum.edocSend.name().equals(appTypeName)){
    			affairList = affairManager.getALLAvailabilityAffairList(ApplicationCategoryEnum.edocSend,Long.parseLong(summaryId), isPage);
    		}else if(EdocEnum.edocType.recEdoc.name().equals(appTypeName)
    				||ApplicationCategoryEnum.edocRec.name().equals(appTypeName)){
    			affairList = affairManager.getALLAvailabilityAffairList(ApplicationCategoryEnum.edocRec,Long.parseLong(summaryId), isPage);
    		}else if(EdocEnum.edocType.signReport.name().equals(appTypeName)
    				||ApplicationCategoryEnum.edocSign.name().equals(appTypeName)){
    			affairList = affairManager.getALLAvailabilityAffairList(ApplicationCategoryEnum.edocSign,Long.parseLong(summaryId), isPage);
    		}
    	}else if("32".equals(appName)){//信息报送//branches_a8_v350sp1_r_gov GOV-4952 魏俊标Meari GOV-4952.单位管理员，工作管理-流程管理，应用类型选择'信息报送'，查询结果列表里查看流程日志时'处理明细'页签红三角. start
    		affairList = affairManager.getALLAvailabilityAffairList(ApplicationCategoryEnum.info,Long.parseLong(summaryId), isPage);//branches_a8_v350sp1_r_gov GOV-4952 魏俊标Meari GOV-4952.单位管理员，工作管理-流程管理，应用类型选择'信息报送'，查询结果列表里查看流程日志时'处理明细'页签红三角. end
    	}else {//协同
    		affairList = affairManager.getALLAvailabilityAffairList(Long.parseLong(summaryId), isPage);
    		if(affairList == null || affairList.isEmpty()){ //历史分表数据
    			affairList = hisAffairManager.getALLAvailabilityAffairList(Long.parseLong(summaryId), isPage);
    			isStoreFlag = (affairList != null && !affairList.isEmpty());
    		}
    	}


    	List<WorkflowDataDetail> flowDataDetailList = new ArrayList<WorkflowDataDetail>();
    	if(affairList != null && affairList.size() != 0){
    	    Long _accountId = CurrentUser.get().getLoginAccount();
    		/*
            boolean isShowShortname = false;
            for(Affair affair : affairList){
    			Long memberId = affair.getMemberId();
        		V3xOrgMember member = orgManager.getMemberById(memberId);
        		Long accountId = orgManager.getAccountById(member.getOrgAccountId()).getId();
    			if(_accountId != 0 && accountId != _accountId){
    				isShowShortname = true;
    				break;
    			}
    			_accountId = accountId;
    		}
            */

    	    //Affair中有节点权限，直接从Affair中取，否则从Process中取
    	    boolean isGetNodePolicyFromAffair = Strings.isNotBlank(affairList.get(affairList.size() - 1).getNodePolicy());
    	    BPMProcess process = null;
    	    Long orgAccountId = 0L;
    	    if(!isGetNodePolicyFromAffair){
    	    	if("4".equals(appName)){//公文
    	    		EdocSummary summary = edocManager.getEdocSummaryById(Long.parseLong(summaryId), false);
    	    		process = ColHelper.getCaseProcess(summary.getProcessId());
    	    		orgAccountId = summary.getOrgAccountId();
    	    	}else if("32".equals(appName)){//信息报送//branches_a8_v350sp1_r_gov GOV-4952 魏俊标Meari GOV-4952.单位管理员，工作管理-流程管理，应用类型选择'信息报送'，查询结果列表里查看流程日志时'处理明细'页签红三角. start
    	    		InfoSummaryCAP summary = infoSummaryManagerCAP.getInfoSummaryById(Long.parseLong(summaryId), true);
    	    		process = ColHelper.getCaseProcess(summary.getProcessId());
    	    		orgAccountId = summary.getOrgAccountId();//branches_a8_v350sp1_r_gov GOV-4952 魏俊标Meari GOV-4952.单位管理员，工作管理-流程管理，应用类型选择'信息报送'，查询结果列表里查看流程日志时'处理明细'页签红三角. end
    	    	}else{

    	    		if(isStoreFlag){ //转储数据
                		ColSummary summary = hisColManager.getColSummaryById(Long.parseLong(summaryId), false);
                		process = ColHelper.getHisCaseProcess(summary.getProcessId());
                		orgAccountId = summary.getOrgAccountId();
                	}
                	else{
                		ColSummary summary = colManager.getColSummaryById(Long.parseLong(summaryId), false);
                		process = ColHelper.getCaseProcess(summary.getProcessId());
                		orgAccountId = summary.getOrgAccountId();
                	}
    	    	}

    	    }

    		for(Affair affair : affairList){
    			String handler = "";
    			String policyName = "";
    			java.sql.Timestamp createDate = null;
    			java.sql.Timestamp finishDate = null;
    			String dealTime = String.valueOf(0);
    			boolean timeOutFlag = false;
    			String timeOutFlagLabel = "";
    			String stateLabel = "";
    			String deadline = String.valueOf(0);
    			//如果affair为公文收发员处理的已封发的公文事项（即待发送公文和已发送公文），不显示在处理明细节点中。
    			//因为此时的affair不在流程中，获取节点权限异常 bug29413[todo:可以将这些事项显示出来，但需要要特殊处理。]
    			if(affair.getApp()==ApplicationCategoryEnum.exSend.getKey()) continue;      //待发送22
    			if(affair.getApp()==ApplicationCategoryEnum.exSign.getKey()) continue;      //待签收23
    			if(affair.getApp()==ApplicationCategoryEnum.edocRegister.getKey()) continue; //待登记24
    			if(affair.getState() == StateEnum.col_sent.key()){
    				Long senderId = affair.getSenderId();
    				V3xOrgMember member = orgManager.getMemberById(senderId);
                    if(member != null){
                        handler = member.getName();
                        if(_accountId != member.getOrgAccountId().longValue()){ //同一个单位的
                            String accountShortname = orgManager.getAccountById(member.getOrgAccountId()).getShortname();
                            handler += "(" + accountShortname + ")";
                        }
                    }
    				createDate = affair.getCreateDate();
    				if(affair.getApp()==ApplicationCategoryEnum.edocSend.getKey()
    						||affair.getApp()==ApplicationCategoryEnum.edocSign.getKey())//发文,签报
    					policyName = Constant.getCommonString("node.policy.niwen");
    				if(affair.getApp()==ApplicationCategoryEnum.edocRec.getKey())//收文
    					//branches_a8_v350_r_gov GOV-540 唐桂林添加政务收文分发节点权限 start
    					policyName = Constant.getCommonString("node.policy.dengji"+Functions.suffix());
    					//branches_a8_v350_r_gov GOV-540 唐桂林添加政务收文分发节点权限 end
    				if(affair.getApp()==ApplicationCategoryEnum.collaboration.getKey())
    					policyName = Constant.getCommonString("node.policy.collaboration");
    			}
                else{
    				Long memberId = affair.getMemberId();
    				V3xOrgMember member = orgManager.getMemberById(memberId);
                    if(member != null){
                        handler = member.getName();
                        if(_accountId != member.getOrgAccountId().longValue()){ //同一个单位的
                            String accountShortname = orgManager.getAccountById(member.getOrgAccountId()).getShortname();
                            handler += "(" + accountShortname + ")";
                        }
                    }
                    if(isGetNodePolicyFromAffair){
                    	policyName = BPMSeeyonPolicy.getShowName(affair.getNodePolicy());
                    }
                    else{
                    	SeeyonPolicy seeyonPolicy = ColHelper.getPolicyByAffair(process, affair);
                    	policyName = BPMSeeyonPolicy.getShowName(seeyonPolicy.getId());
                    }

    				createDate = affair.getReceiveTime();
    				finishDate = affair.getCompleteTime();
    				Date updateDate = affair.getUpdateDate();
    				Date _createDate = new Date(createDate.getTime());
    				Date _finishDate = null;
    				if(finishDate != null){
    					_finishDate = new Date(finishDate.getTime());
    				}else if(updateDate != null){
    					_finishDate = new Date(updateDate.getTime());
    					finishDate = new Timestamp(updateDate.getTime());
    				}
    				long[] intervalTime = Datetimes.detailInterval(_createDate, _finishDate);
    				if(intervalTime != null){
	    				long date = intervalTime[0];
	    				long hour = intervalTime[1];
	    				long minut = intervalTime[2];
	    				long second = intervalTime[3];
	    				dealTime = ColHelper.timePatchwork(date, hour, minut, 0L, true);
    				}
    				if(affair.getDeadlineDate()!= null && affair.getDeadlineDate() != 0){
	    				long[] deadlineArr = Datetimes.formatLongToTimeStr(affair.getDeadlineDate()*60000);
	    				long date1 = deadlineArr[0];
	    				long hour1 = deadlineArr[1];
	    				long minut1 = deadlineArr[2];
	    				deadline = ColHelper.timePatchwork(date1, hour1, minut1, 0L, true);
    				}
    			}



    			int state = affair.getState();
    			//int subState = affair.getSubState();
    			Integer subState = affair.getSubState();
    			if( subState == null)subState=SubStateEnum.col_normal.key();
    			if(state == StateEnum.col_pending.key() && subState == SubStateEnum.col_pending_ZCDB.key()){
    				stateLabel = Constant.getString4CurrentUser("col.substate.13.label");
    			}else if(state == StateEnum.col_sent.key()){
    				stateLabel = Constant.getString4CurrentUser("col.state.12.col_sent");
    			}else if(state == StateEnum.col_pending.key() && subState != SubStateEnum.col_pending_ZCDB.key()){
    				stateLabel = Constant.getString4CurrentUser("col.state.13.col_pending");
    			}else if(state == StateEnum.col_done.key()){
    				stateLabel = Constant.getString4CurrentUser("col.state.14.done");
                    if(subState == SubStateEnum.col_done_stepStop.key()){
                        stateLabel += "("+ Constant.getString4CurrentUser("col.state.10.stepstop") +")";
                    }
    			}

    			WorkflowDataDetail workflowDataDetail = new WorkflowDataDetail();
    			workflowDataDetail.setCreateDate(createDate);
    			workflowDataDetail.setDealTime(dealTime);
    			workflowDataDetail.setDeadline(deadline);
    			workflowDataDetail.setEntityId(affair.getId());
    			workflowDataDetail.setFinishDate(finishDate);
    			workflowDataDetail.setHandler(handler);
    			workflowDataDetail.setPolicyName(policyName);
    			workflowDataDetail.setStateLabel(stateLabel);


    			workflowDataDetail.setRunWorkTime(affair.getRunWorkTime());
    			//没有超期时长动态运行.
    			workflowDataDetail.setOverWorkTime(affair.getOverWorkTime());
    			boolean isCompute = false;
    			Date computeEndDate = new Date();
    			//设置了流程期限的才需要计算.
    			if(affair.getDeadlineDate()!=null && affair.getDeadlineDate()!=0){

    				if(affair.getState().intValue() == StateEnum.col_done.key()
	    				&&affair.getOverWorkTime() == null){
	    					isCompute  = true;
	    					computeEndDate = affair.getCompleteTime();
	    			}else if (affair.getState().intValue() == StateEnum.col_pending.key()){
	    					isCompute  = true;
	    			}
    			}

    			if(isCompute){
	    			try {
						long time = workTimeManager.getDealWithTimeValue(affair.getReceiveTime(),computeEndDate,orgAccountId);
						time = time/(60*1000); //毫秒转化为分钟
						long workDeadLine = 0l;
						if( affair.getDeadlineDate()!=null && affair.getDeadlineDate()!=0){
							workDeadLine = workTimeManager.convert2WorkTime(affair.getDeadlineDate(), orgAccountId);
						}
						long over = time - workDeadLine;
						workflowDataDetail.setOverWorkTime(over >0 ? over : 0);
					} catch (WorkTimeSetExecption e) {
						log.error("",e);
					}
    			}

    			//只有处理期限的才有超时时间。
    			if(affair.getDeadlineDate()== null || affair.getDeadlineDate() == 0 ){
    				workflowDataDetail.setOverWorkTime(0L);
    			}
    			if((affair.getIsOvertopTime() && affair.getState() != StateEnum.col_sent.key())
    					||(workflowDataDetail.getOverWorkTime() !=null &&workflowDataDetail.getOverWorkTime()>0)){
                    timeOutFlag = true;
                }
    			if(affair.getDeadlineDate() == null || affair.getDeadlineDate() == 0){
    				timeOutFlagLabel = Constant.getCommonString("common.default");
    			}else{
	    			if(timeOutFlag){
	    				timeOutFlagLabel = Constant.getCommonString("common.yes");
	    			}else{
	    				timeOutFlagLabel = Constant.getCommonString("common.no");
	    			}
    			}
    			if(affair.getState() == StateEnum.col_sent.key()){
    				timeOutFlagLabel = "－";
    			}
    			workflowDataDetail.setTimeOutFlag(timeOutFlag);
    			workflowDataDetail.setTimeOutFlagLabel(timeOutFlagLabel);
    			flowDataDetailList.add(workflowDataDetail);
    		}
    	}
    	return flowDataDetailList ;
    }

    /**
     * 单位管理员 集团管理员 流程统计
     * @author yuhj 2008-6-05
     * @param request
     * @param response
     * @return ModelAndView mav
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
    public ModelAndView workflowStat(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	String appType = request.getParameter("appType");
    	String statType = request.getParameter("statType");
    	String departmentStr = request.getParameter("departmentIds");
    	String personStr = request.getParameter("personIds");
    	String statScope = request.getParameter("statScope");
    	Date beginDate = request.getParameter("beginDate")==null?null:Datetimes.parse(request.getParameter("beginDate")+" 00:00:00", Datetimes.datetimeStyle);
    	Date endDate = request.getParameter("endDate")==null?null:Datetimes.parse(request.getParameter("endDate")+" 23:59:59", Datetimes.datetimeStyle);
    	boolean isGroupAdmin = CurrentUser.get().isGroupAdmin();
    	boolean isAdministrator = CurrentUser.get().isAdministrator();
    	User user = CurrentUser.get();

    	List<Long> entityIds = V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(statType) ? getIdsUtils(personStr) : getIdsUtils(departmentStr);
    	/** 单位管理员默认查询查询本单位 */
    	if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(statType)
    			&& !departmentStr.contains("|")
    			&& Strings.isNotBlank(departmentStr)) {
    		//此处&&后面逻辑为排除集团管理员选择多个单位传递过来的Account|123456,Account|654321这种ID
    		entityIds.add(new Long(departmentStr));
    	}
    	if(isGroupAdmin && !V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(statType) && entityIds != null && entityIds.size() >0){
    		Long accountId = entityIds.get(0);
    		Long rootAccountId = orgManager.getRootAccount().getId();
    		if(accountId.equals(rootAccountId)){
    			List<V3xOrgAccount> allAccount = orgManager.getAllAccounts();
    			entityIds = new ArrayList<Long>();
    			for (V3xOrgAccount account : allAccount) {
    				entityIds.add(account.getId());
				}
    		}
    	}
    	String operationTypeValue = request.getParameter("templeteId");
    	List<Long> templateId = new ArrayList<Long>();
    	//普通用户，选择全部模板的时候，按流程分析授权中所能分析的流程模板来进行统计。
    	if(!isGroupAdmin && !isAdministrator && "1".equals(operationTypeValue)){
    		List<Long> ids = workFlowAnalysisAclManager.getTempleteIdByUserId(user.getId(),null );
    		templateId.addAll(ids);
    	}else{
    		templateId = FormBizConfigUtils.parseStr2Ids(operationTypeValue!=null ? operationTypeValue.trim() : operationTypeValue, ",");
    	}
    	List<Object[]> result = null;
    	ModelAndView mv = null;
    	HttpSession session = request.getSession();

    	if(!"1".equals(request.getParameter("exportToExcel"))){
    		mv = new ModelAndView("collaboration/workflowStatResult");
    		if("group".equals(statScope)){
    			result = this.colManager.statByGroup(
    					Integer.parseInt(appType),
    					entityIds,
    					statType,
    					beginDate,
    					endDate);
    			mv.addObject("entityType", statType);
    		}
        	else{
        		result = this.colManager.statByAccount(
        				Integer.parseInt(appType),
        				templateId,
        				entityIds,
        				statType,
        				beginDate,
        				endDate);
        		mv.addObject("entityType", V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(statType)?V3xOrgEntity.ORGENT_TYPE_DEPARTMENT:statType);
        	}


	    	Object[] total = null;
	    	if(result != null) {
	    		total = result.get(result.size()-1);
	    		result.remove(result.size()-1);
	    		mv.addObject("total", total);
	    	}
	    	mv.addObject("result", pagenate(result));
	    	mv.addObject("isGroupAdmin", isGroupAdmin);
	    	mv.addObject("isAdministrator", isAdministrator);
	    	session.setAttribute("rows", result);
	    	session.setAttribute("appId", appType);
	    	session.setAttribute("totals", total);
	    	session.setAttribute("statType", statType);
    	}
    	else {
    		statType = (String)session.getAttribute("statType");
    		if(!isGroupAdmin && V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(statType)){
    			//单位管理员按照单位查询，那么类型变为department。
    			statType = V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
    		}
    		appType = (String)session.getAttribute("appId");
    		result = (List<Object[]>)session.getAttribute("rows");
    		Object[] total = (Object[])session.getAttribute("totals");

    		String[] columnNames = this.getColumnNames(isGroupAdmin, statType);
    		List<Object[]> rows = this.getColumnInfo(appType, statType, result, total, isGroupAdmin);
    		String subject = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "common.flow.statistics.label") ;
    		ColHelper.exportToExcel(request, response, fileToExcelManager,subject, rows, columnNames, Constant.getString("stat.workflow.subject"), "sheet1");
    	}
    	return mv;
    }
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
     * 获取选人界面id串工具类
     * @param str
     * @return
     * @throws NumberFormatException
     * @throws BusinessException
     */
    private List<Long> getIdsUtils(String str) throws NumberFormatException, BusinessException {
    	List<Long> tempIds = new ArrayList<Long>();
    	if(StringUtils.isNotBlank(str)) {
    		String allMembers[] = str.split(",") ;
    			for(int i = 0 ; i < allMembers.length ; i++ ){
		      		String user[] = allMembers[i].split("[|]") ;
		      		if(user[0].equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)) {
		      			tempIds.add(Long.valueOf(user[1])) ;
		      		}else if(user[0].equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)) {
		      			tempIds.add(Long.valueOf(user[1])) ;
		      		}else if(user[0].equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)) {
		      			if(user.length > 2) {
		      				if(1 == Long.valueOf(user[2])) {//1为不包含子部门，0或无值为包含子部门
		      					tempIds.add(Long.valueOf(user[1]));continue;
		      				}
		      			}
		      			tempIds.add(Long.valueOf(user[1])) ;
		      			List<V3xOrgDepartment> v3xOrgDepartmentList = this.orgManager.getChildDepartments(Long.valueOf(user[1]), false);
						for(V3xOrgDepartment v3xOrgDepartment : v3xOrgDepartmentList) {
							tempIds.add(v3xOrgDepartment.getId()) ;
						}
		      		}
    			}
	      }
    	return tempIds;
    }

    /**
     * 根据当前用户及统计类型获取导出Excel表格时，表格全部列名数组
     * @param isGroupAdmin 	当前用户是否集团管理员
     * @param statType		统计类型：如按照单位、部门、人员等
     * @return	Excel表格列名数组，如：[应用类型,	单位/部门, 人员, 已办数量, 待办数量, 超期数量, 累计超期时长]
     */
    private String[] getColumnNames(boolean isGroupAdmin, String statType) {
    	List<String> result = new ArrayList<String>();

		result.add(Constant.getCommonString(isGroupAdmin ? "common.app.type" : "common.operation.type"));
		result.add(Constant.getMainString("org.account.label") + "/" + Constant.getMainString("org.department.label"));
		if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(statType))
			result.add(Constant.getMainString("org.member.label"));
		result.add(Constant.getString("stat.sent.count"));
		result.add(Constant.getString("stat.done.count"));
		result.add(Constant.getString("stat.pending.count"));
		result.add(Constant.getString("stat.deadline.count"));
		result.add(Constant.getString("stat.deadline.totalTime"));

		return result.toArray(new String[1]);
    }

    /**
     * 根据统计结果组装导出Excel表格时所需数据集
     * @param appType	应用类型：如协同、表单等
     * @param statType	统计类型：如按照单位、部门、人员等
     * @param result	统计结果
     * @param total		统计汇总结果
     * @param isGroupAdmin	当前用户是否集团管理员
     * @return 填充Excel表格的数据结果集
     */
	private List<Object[]> getColumnInfo (String appType, String statType, List<Object[]> result, Object[] total, boolean isGroupAdmin) throws BusinessException {
		boolean statisticByMember = V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(statType);

		List<Object[]> rows = new ArrayList<Object[]>();
		if(CollectionUtils.isNotEmpty(result)) {
			int beginIndex = isGroupAdmin ? 0 : 2;
			for(Object[] row : result) {
				//添加元素的先后顺序与导出Excel表格的各列数据一一对应，只能根据应用的调整随列名同步进行变动
				List<Object> obj = new ArrayList<Object>();
				if(isGroupAdmin) {
					obj.add(Constant.getCommonString("application." + appType + ".label"));
				} else {
					if(("collaboration".equals(row[9]) || "edoc".equals(row[9])) && row[0]==null)
						obj.add(Constant.getString("self.create.workflow"));
					else
						obj.add(row[1]);
				}

				if(row[beginIndex] == null) {
					if(statisticByMember && row[beginIndex + 1] != null) {
						V3xOrgMember member = this.orgManager.getMemberById((Long)row[beginIndex + 1]);
						Long deptId = member != null ? member.getOrgDepartmentId() : null;
						obj.add(deptId == null ? "" : Functions.showOrgEntities(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT + "|" + deptId, ","));
					} else {
						obj.add("");
					}
				} else {
					obj.add(Functions.showOrgEntities((statisticByMember ? V3xOrgEntity.ORGENT_TYPE_DEPARTMENT : statType) + "|" + row[beginIndex], ","));
				}

				if(statisticByMember){
					obj.add(row[beginIndex + 1] == null ? "" : Functions.showMemberName((Long)row[beginIndex + 1]));
				}

				obj.add(row[beginIndex + 2] == null ? "0" : row[beginIndex + 2]);
				obj.add(row[beginIndex + 3] == null ? "0" : row[beginIndex + 3]);
				obj.add(row[beginIndex + 4] == null ? "0" : row[beginIndex + 4]);
				obj.add(row[beginIndex + 5]);
				obj.add(row[beginIndex + 6] == null ? "" : row[beginIndex + 6]);
				rows.add(obj.toArray());
			}
		}

		//统计汇总
		Object[] obj = new Object[8];
		obj[0] = Constant.getString("stat.all");
		int columnIndex = statisticByMember ? 1 : 0;
		if(total != null) {
			for(int i = 0; i <= 3; i ++) {
				obj[i + 2 + columnIndex] = total[i] == null ? "0" : total[i];
			}
		}else{
			for(int i = 0; i <= 3; i ++) {
				obj[i + 2 + columnIndex] ="0";
			}
		}
		rows.add(obj);

		return rows;
	}

	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
    public ModelAndView workflowStatMain(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/workflowStatMain");
    	List<Integer> appEnumKeyList = new ArrayList<Integer>();
    	appEnumKeyList.add(ApplicationCategoryEnum.collaboration.key());
    	boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();
		if(isGov) {
			if(SystemEnvironment.hasPlugin("form"))
	    		appEnumKeyList.add(ApplicationCategoryEnum.form.key());
		}else{
			appEnumKeyList.add(ApplicationCategoryEnum.form.key());
		}

    	if(com.seeyon.v3x.common.taglibs.functions.Functions.isEnableEdoc()){
    		appEnumKeyList.add(ApplicationCategoryEnum.edoc.key()); // 合并下面三种
//    		appEnumKeyList.add(ApplicationCategoryEnum.edocSend.key());
//    		appEnumKeyList.add(ApplicationCategoryEnum.edocRec.key());
//    		appEnumKeyList.add(ApplicationCategoryEnum.edocSign.key());
    	}
    	 boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
         if(isGovVersion){
        	 appEnumKeyList.add(ApplicationCategoryEnum.info.key());
         }
    	mv.addObject("appEnumKeyList", appEnumKeyList);
    	boolean isGroupAdmin = CurrentUser.get().isGroupAdmin();
    	boolean isAdministrator = CurrentUser.get().isAdministrator();
    	if(!isGroupAdmin) {
    		mv.addObject("accountId", CurrentUser.get().getLoginAccount());
    	}
    	Date today = new Date();
    	Date firstDayOfMonth = Datetimes.getFirstDayInMonth(today);
    	mv.addObject("isGroupAdmin", isGroupAdmin)
    	.addObject("isAdministrator", isAdministrator).addObject("flowType", "self")
    	.addObject("defaultBeginDate", Datetimes.format(firstDayOfMonth, Datetimes.dateStyle))
    	.addObject("defaultEndDate",Datetimes.format(today, Datetimes.dateStyle));
    	HttpSession session = request.getSession();
    	session.removeAttribute("rows");
    	session.removeAttribute("appId");
    	session.removeAttribute("totals");
    	return mv;
    }

    public ModelAndView statResultListMain(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/statResultListMain");
    	return mv;
    }

    public ModelAndView statResultList(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/statResultList");
    	int appType = Integer.parseInt(request.getParameter("appType"));
    	String entityType = request.getParameter("entityType");
    	String entityId = request.getParameter("entityId");
    	String state = request.getParameter("state");
    	Date beginDate = request.getParameter("beginDate")==null?null:Datetimes.parse(request.getParameter("beginDate")+" 00:00:00", Datetimes.datetimeStyle);
    	Date endDate = request.getParameter("endDate")==null?null:Datetimes.parse(request.getParameter("endDate")+" 23:59:59", Datetimes.datetimeStyle);
    	String template = request.getParameter("templateId");
    	Long templateId = template==null||"".equals(template)?null:new Long(request.getParameter("templateId"));
    	String appName = request.getParameter("appName");
    	String statScope = request.getParameter("statScope");


    	List<Object[]> results = this.colManager.statList(appType, Long.parseLong(entityId), entityType, Integer.parseInt(state), beginDate, endDate,templateId,appName,statScope,true);
    	mv.addObject("results", results);
    	if(appType == -1) {
    		if(ApplicationCategoryEnum.edoc.name().equals(appName))
    			appType = ApplicationCategoryEnum.edoc.getKey();
    		else
    			appType = ApplicationCategoryEnum.collaboration.getKey();
    		mv.addObject("appType", appType);
    	}
    	return mv;
    }

	/**
	 * 单位管理员 工作管理  流程统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public ModelAndView exportExcelWorkflowState(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mv = new ModelAndView("collaboration/statResultList");
    	int appType = Integer.parseInt(request.getParameter("appType"));
    	String entityType = request.getParameter("entityType");
    	String entityId = request.getParameter("entityId");
    	String state = request.getParameter("state");
    	Date beginDate = request.getParameter("beginDate")==null?null:Datetimes.parse(request.getParameter("beginDate"), Datetimes.datetimeStyle);
    	Date endDate = request.getParameter("endDate")==null?null:Datetimes.parse(request.getParameter("endDate")+" 23:59:59", Datetimes.datetimeStyle);
    	String template = request.getParameter("templateId");
    	Long templateId = template==null||"".equals(template)?null:new Long(request.getParameter("templateId"));
    	String appName = request.getParameter("appName");
    	String statScope = request.getParameter("statScope");

    	List<Object[]> results = this.colManager.statList(appType, Long.parseLong(entityId), entityType, Integer.parseInt(state), beginDate, endDate,templateId,appName,statScope,false);
    	mv.addObject("results", results);
    	if(appType == -1) {
    		if(ApplicationCategoryEnum.edoc.name().equals(appName))
    			appType = ApplicationCategoryEnum.edoc.getKey();
    		else
    			appType = ApplicationCategoryEnum.collaboration.getKey();
    		mv.addObject("appType", appType);
    	}


    	String stat = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.workflow.stat") ;

    	String type = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.app.type") ;
    	String sender = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.sender.label") ;
    	String subject = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.subject.label") ;
    	String sendtime = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "common.date.sendtime.label") ;
    	String isFinish = ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "col.isFinshed.label") ;

    	String[] columnName = {type,sender,subject,sendtime ,isFinish} ;

    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(stat) ;
    	dataRecord.setColumnName(columnName) ;

    	String isFinishValue = "" ;

    	for (int i = 0 ; i < results.size() ; i ++) {
    		Object[] data = results.get(i) ;
    		DataRow dataRow = new DataRow();

    		if(appType == -1) {
        		if(ApplicationCategoryEnum.edoc.name().equals(appName))
        			appType = ApplicationCategoryEnum.edoc.getKey();
        		else
        			appType = ApplicationCategoryEnum.collaboration.getKey();
        	}

    		String tempCategory = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "application."+appType+".label") ;

    		dataRow.addDataCell(data[0] == null ? "-" : tempCategory, DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data[1] == null ? "-" : Functions.showMemberName(Long.valueOf(String.valueOf(data[1]))), DataCell.DATA_TYPE_TEXT);
    		dataRow.addDataCell(data[2] == null ? "-" : String.valueOf(data[2]), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(data[3] == null ? "-" : Datetimes.format((Date)data[3], Datetimes.datetimeStyle), DataCell.DATA_TYPE_TEXT) ;

    		// 是否结束
    		isFinishValue = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", data[4] == null ? "common.no" : "common.yes") ;
    		dataRow.addDataCell(isFinishValue, DataCell.DATA_TYPE_TEXT) ;

    		dataRecord.addDataRow(dataRow);
    	}
    	fileToExcelManager.saveAsCSV(request,response,stat,dataRecord);

    	return null;
    }

    /**
     * 列出当前单位某个应用类型(协同,公文,表单)下的所有模版
     *
     * @author jincm 2008-6-25
     * @param request
     * @param response
     * @return ModelAndView
     * @throws Exception
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView openTemplateDetail(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("collaboration/openTemplateDetail");
    	Integer appkey = Integer.parseInt(request.getParameter("selectedAppEnumKey"));
    	int categoryType = 0;
    	switch(appkey){
    		case 1 : categoryType = TempleteCategory.TYPE.collaboration_templete.ordinal();
    		        break;
    		case 2 : categoryType = TempleteCategory.TYPE.form.ordinal();
    		        break;
            case 19: categoryType = TempleteCategory.TYPE.edoc_send.ordinal();
                    break;
            case 20: categoryType = TempleteCategory.TYPE.edoc_rec.ordinal();
                    break;
            case 21: categoryType = TempleteCategory.TYPE.sginReport.ordinal();
                    break;
    		default:
    			categoryType = TempleteCategory.TYPE.edoc.ordinal();
    	}
    	User user = CurrentUser.get();
    	List<Templete> templeteList = templeteManager.getAllSystemTempletes(user.getId(),CurrentUser.get().getLoginAccount(), categoryType);
    	mav.addObject("templeteList", templeteList);
    	return mav;
    }

    public ModelAndView changeTrack(HttpServletRequest request,HttpServletResponse response) throws Exception {
        Long affairId = Long.parseLong(request.getParameter("affairId"));
        Integer trackMode = Integer.parseInt((request.getParameter("trackMode")));
        boolean track = true;
        if(trackMode == 0){
        	track = false;
        }
        String trackMembers = request.getParameter("trackMembers");
        colManager.setTrack(affairId, track, trackMembers);
        return null;
    }

    private void processTemplateSupervisor(long templateId,ModelAndView modelAndView,User user) throws Exception{
    	ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),templateId);
        if(detail != null) {
        	Long terminalDate = detail.getTemplateDateTerminal();
        	if(null!=terminalDate){
            	Date superviseDate = Datetimes.addDate(new Date(), terminalDate.intValue());
            	String date = Datetimes.formatDate(superviseDate);
            	modelAndView.addObject("superviseDate", date);
        	}else if(detail.getAwakeDate() != null) {
        		modelAndView.addObject("superviseDate", Datetimes.format(detail.getAwakeDate(), Datetimes.dateStyle));
        	}
        	Set<ColSupervisor> supervisors = detail.getColSupervisors();
        	Set<String> sIdSet = new HashSet<String>();
        	StringBuffer names = new StringBuffer();
        	StringBuffer ids = new StringBuffer();
        	for(ColSupervisor supervisor:supervisors)
        		sIdSet.add(supervisor.getSupervisorId().toString());
        	List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(templateId);
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
        		if(role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())){
        			orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, user.getLoginAccount());
        			if(null!=orgRole){
        			List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(user.getId());
        			for(V3xOrgDepartment dep : depList){
        				List<V3xOrgMember> managerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
            			for(V3xOrgMember mem : managerList){
            				sIdSet.add(mem.getId().toString());
            			}
        			}
        			}
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

        	modelAndView.addObject("colTemplateSupervisors", ids.toString());
        	modelAndView.addObject("unCancelledVisor", ids.toString());
        	modelAndView.addObject("colTemplateSupervisorNames", names.toString());
        }
    }

    /**
     * 查看修改分支描述
     */
    public ModelAndView showMoreCondition(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return new ModelAndView("collaboration/templete/moreCondition");
    }

    public ModelAndView policyExplain(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/policyExplain");
		String resource_baseName = "com.seeyon.v3x.flowperm.resources.i18n.FlowPermResource";
        String des = "";
        //if(isEdoc){
        //branches_a8_v350_r_gov GOV-1665 王为 增加信息报送信息 start
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        if(isGovVersion){
        	des = StringEscapeUtils.escapeJavaScript(ResourceBundleUtil.getString(resource_baseName, "node.description.all.info"));
         //branches_a8_v350_r_gov GOV-1665 王为 增加信息报送信息 end
        }else{
        	des = StringEscapeUtils.escapeJavaScript(ResourceBundleUtil.getString(resource_baseName, "node.description.all.edoc"));
        }
        //}else{
        //	des = StringEscapeUtils.escapeJavaScript(ResourceBundleUtil.getString(resource_baseName, "node.description.all"));
        //}
        mav.addObject("des", des);
        return mav;
    }
    public ModelAndView showDealExplain(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/dealExplain");
		String desc = request.getParameter("desc");
		if(null!=desc){
        	desc= desc.replaceAll("<br>", "\r\n").replaceAll("&nbsp;", "\\s");
        }else{
        	desc="";
        }
        request.setAttribute("desc", desc);
        mav.addObject("desc",desc);
        return mav;
    }

    /**
     * 新流程中查看关联的子流程或主流程协同
     */
    public ModelAndView viewRelateColl(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mav = new ModelAndView("collaboration/newflow/viewRelateColl");
        String newflowType = request.getParameter("flowType");
        String summaryIdStr = request.getParameter("mainSummaryId");
        String templeteIdStr = request.getParameter("mainTempleteId");
        String nodeIdStr = request.getParameter("nodeId");
        List<NewflowRunning> relateFlowList = new ArrayList<NewflowRunning>();
        if(Strings.isNotBlank(summaryIdStr) && Strings.isNotBlank(templeteIdStr)){
            if("main".equals(newflowType)){
//              当前为主流程，取关联子流程信息
                Long summaryId = Long.parseLong(summaryIdStr);
                List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, Long.parseLong(templeteIdStr), Constant.NewflowType.main.ordinal());
                if(runningList != null){
                    for (NewflowRunning running : runningList) {
                        //1、当前协同为主流程（running为子流程），且子流程running可被主流程查看
                        if(running.getMainSummaryId().equals(summaryId) && running.getIsCanViewByMainFlow() && running.getMainNodeId().equals(nodeIdStr)){
                            relateFlowList.add(running);
                        }
                    }
                }
            }
            else if("child".equals(newflowType)){
//              当前为子流程，取关联主流程信息
                Long summaryId = Long.parseLong(summaryIdStr);
                List<NewflowRunning> runningList = newflowManager.getNewflowRunningList(summaryId, Long.parseLong(templeteIdStr), Constant.NewflowType.child.ordinal());
                if(runningList != null){
                    for (NewflowRunning running : runningList) {
                        //1、当前协同为主流程（running为子流程），且子流程running可被主流程查看
                        if(running.getSummaryId().equals(summaryId) && running.getIsCanViewMainFlow()){
                            relateFlowList.add(running);
                        }
                    }
                }
            }
        }
        mav.addObject("newflowType", newflowType);
        mav.addObject("relateFlowList", relateFlowList);
        return mav;
    }


    /**
     * 取得流程指定节点的SeeyonPolicy。
     * @param processId
     * @param nodeId
     * @return
     * @throws ColException
     */
    private BPMSeeyonPolicy getNodePolicy(String processId,String nodeId) throws ColException{
        BPMProcess process =  ColHelper.getRunningProcessByProcessId(processId);
        BPMAbstractNode memNode = process.getActivityById(nodeId);
        if(memNode != null){
            return memNode.getSeeyonPolicy();
        }
        return null;
    }
    /**
     * 判断节点权限是否改变。
     *
     * @param processId
     * @param nodeId
     * @param policy
     * @return 流程指定节点的权限与传入的policy不相同时返回false，否则返回true。
     * @throws ColException
     */
    private boolean isNodePermissionChanged(String processId,String nodeId,BPMSeeyonPolicy policy) throws ColException{
        boolean isChanged = false;
        BPMSeeyonPolicy seeyonPolicy = getNodePolicy(processId, nodeId);
        if(seeyonPolicy != null){
            isChanged = seeyonPolicy.getId().equals(policy.getId());
        }
    	return isChanged;
    }
    private boolean isNodePermissionChanged(String processId,String nodeId,String nodePermission) throws ColException{
        boolean isChanged = false;
        BPMSeeyonPolicy seeyonPolicy = getNodePolicy(processId, nodeId);
        if(seeyonPolicy != null){
            isChanged = seeyonPolicy.getId().equals(nodePermission);
        }
        return isChanged;
    }
	/**
	 * 判断节点权限是否改变。
	 * @param flowdata 前台流程信息
	 * @param nodeId
	 * @return
	 * @throws ColException
	 */
    private boolean isNodePermissionChanged(FlowData flowdata,String nodeId) throws ColException{
        boolean isChanged = false;
    	BPMProcess process = BPMProcess.fromXML(flowdata.getXml());
		BPMAbstractNode node =(BPMAbstractNode)process.getActivityById(nodeId);
        BPMSeeyonPolicy seeyonPolicy = node.getSeeyonPolicy(); //ColHelper.getNodePolicy(process.getId(), nodeId);
        if(seeyonPolicy != null){
            isChanged = true;
        }
    	return isChanged;
    }

    private boolean checkCurrentCollIsNewflow(String processId) throws ColException{
        BPMSeeyonPolicy startNodePolicy = getNodePolicy(processId, "start");
        return (startNodePolicy != null && "1".equals(startNodePolicy.getNF()));
    }

    /**
     * 封装模板ID，表单菜单挂接接口<br>
     * 也适应于其他需要查询某些模板的列表。
     * @param request
     * @return
     */
    private List<Long> parseTempleteIds(HttpServletRequest request){
        String templeteIdsStr = request.getParameter("tempIds");
        if(Strings.isNotBlank(templeteIdsStr)){
            List<Long> templeteIds = new ArrayList<Long>();
            StringTokenizer token = new StringTokenizer(templeteIdsStr, ",");
            while(token.hasMoreTokens()){
                templeteIds.add(Long.parseLong(token.nextToken()));
            }
            return templeteIds;
        }
        return null;
    }

    private boolean closeWindowFromGenius(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	//如果是精灵则直接关闭窗口，否则按常规返回页面
        if("a8genius".equals(request.getParameter("from"))){
        	super.rendJavaScript(response, "try{parent.parent.parent.closeWindow();}catch(e){window.close()}");
        	return true;
        }
        return false;
    }


    public ModelAndView listFinishColFormForm(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/listFinishColFormForm") ;
    	String formId = request.getParameter("formId");
    	String fromQuery = request.getParameter("fromQuery");
    	String condition = request.getParameter("condition");
    	String textfield = request.getParameter("textfield");
    	String textfield1 = request.getParameter("textfield1");
		String mySent = request.getParameter("isMySent");
    	List<ColSummary> list = null;
    	boolean isMySent = true;
    	if(Strings.isNotBlank(mySent)){
    		isMySent = Boolean.valueOf(mySent);
    	}
    	if(Strings.isNotBlank(formId)){

    		if(Strings.isNotBlank(fromQuery) && "true".equals(fromQuery)){
    			list = this.colManager.getColSummaryForForm(Long.valueOf(formId), null, condition, textfield, textfield1);
    		}else{
    			list = this.colManager.getColSummaryForForm(Long.valueOf(formId), CurrentUser.get(), condition, textfield, textfield1, isMySent);
    		}
    	}

    	Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
		modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
    	modelAndView.addObject("colSummaryList", list) ;
    	modelAndView.addObject("mySent", isMySent) ;
    	return modelAndView ;
    }

    public ModelAndView getFinishColFormForm(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("collaboration/colFormFrame") ;
    	modelAndView.addObject("formId", request.getParameter("formId")) ;
    	String fromQuery = "" ;
    	if(Strings.isNotBlank(request.getParameter("fromQuery"))){
    		fromQuery = request.getParameter("fromQuery") ;
    	}
    	modelAndView.addObject("fromQuery",fromQuery) ;
    	return modelAndView ;
    }

    public ModelAndView showAttribute(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/attribute");
    	String affairId = request.getParameter("affairId");
    	String from = request.getParameter("from");
    	if(Strings.isNotBlank(affairId)){
    		try{
    			Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
    			Metadata importanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);
    	        Metadata deadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
    	        mav.addObject("importanceMetadata", importanceMetadata);
    	        mav.addObject("deadlineMetadata", deadlineMetadata);
    	        mav.addObject("comMetadata", comMetadata);
    			Affair affair = affairManager.getById(Long.parseLong(affairId));
    			boolean isStoreFlag = false; //是否转储标记

    			boolean isGov = (Boolean)SysFlag.is_gov_only.getFlag();// branches_a8_v350sp1_r_gov 向凡 添加 政务版本判断
    			//是否超期
    			String isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.false");

                //看一下是否被转储了
                if(affair == null){
                	affair = this.hisAffairManager.getById(Long.parseLong(affairId));
                	if(affair != null){
                		isStoreFlag = true;
                	}
                }

    			ColSummary colSummary = null;
    			EdocSummary  edocSummary = null;
    			Long projectId = null;
    			Long archiveId = null;
    			Timestamp startDate = null;
    			Timestamp finishDate  = null;
    			Long deadline = null;

    			if(ApplicationCategoryEnum.collaboration.key()==affair.getApp()){
    				if(isStoreFlag){ //转储数据
    					colSummary = hisColManager.getColSummaryById(affair.getObjectId(), false);
                	}
                	else{
                		colSummary = colManager.getColSummaryById(affair.getObjectId(), false);
                	}

    				projectId = colSummary.getProjectId();
    				archiveId = colSummary.getArchiveId();
    				startDate = colSummary.getStartDate();
    				finishDate = colSummary.getFinishDate();
    				deadline = colSummary.getDeadline();
    				//成发集团项目 程炯 2012-8-29 协同密级显示
    				if(colSummary.getSecretLevel() == null || "".equals(colSummary.getSecretLevel())){
    					mav.addObject("secret", ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.nosecret"));
    				}else
    				if(colSummary.getSecretLevel() == Constant.SecretLevel.none.ordinal()){
    					mav.addObject("secret", ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.nosecret"));
    				}
    				else if(colSummary.getSecretLevel() == Constant.SecretLevel.noSecret.ordinal()){
    					mav.addObject("secret", ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.nosecret"));
    				}
    				else if(colSummary.getSecretLevel() == Constant.SecretLevel.secret.ordinal()){
    					mav.addObject("secret", ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.secret"));
    				}
    				else if(colSummary.getSecretLevel() == Constant.SecretLevel.secretMore.ordinal()){
    					mav.addObject("secret", ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.secretmore"));
    				}
    				else if(colSummary.getSecretLevel() == Constant.SecretLevel.TopSecret.ordinal()){
    					mav.addObject("secret", ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource", "collaboration.secret.topsecret"));
    				}
    				//end
    				mav.addObject("canForward", colSummary.getCanForward());
    				mav.addObject("canModify", colSummary.getCanModify());
    				mav.addObject("canEdit", colSummary.getCanEdit());
    				mav.addObject("canEditAttachment", colSummary.getCanEditAttachment());
    				mav.addObject("canArchive", colSummary.getCanArchive());
    				mav.addObject("iscol", true);

    				//显示表单绑定信息:要确认应用为协同，同时为表单
        	        if("FORM".equals(affair.getBodyType())){
            	        String display = null;
            	        Long formApp = null;
        	        	Long operationId = null;
        	        	Long formId = null;
        	        	//当为督办人员时且本节点的处理人不是本人时，没有表单绑定权限
        	        	Long currentUserId = CurrentUser.get().getId();
        	        	List<Affair> currentAffairs = affairManager.getAffairBySummaryIdAndMember(colSummary.getId(), currentUserId);
        	        	if(!colSuperviseManager.isSupervisor(currentUserId, colSummary.getId()) || (currentAffairs!=null && currentAffairs.size()>0) ){
        	        		if(affair.getFormAppId() != null && affair.getFormOperationId() != null){
            	        		formApp = affair.getFormAppId();
            	        		operationId = affair.getFormOperationId();
            	        		formId = affair.getFormId();
            	        	}else{
            	        		if(affair.getActivityId() == null){
            	        			List<Long> list = new ArrayList<Long>();
            	        			list.add(Long.valueOf(colSummary.getTempleteId()));
            	        			Templete t = templeteManager.getTempletesByIds(list).get(0);
            	        			BPMProcess p = BPMProcess.fromXML(t.getWorkflow());
            	        			BPMStatus BpmStatus = p.getStart();
            	        			BPMSeeyonPolicy bpmSeeyonPolicy = BpmStatus.getSeeyonPolicy();
            	        			formApp = colSummary.getFormAppId();
            	        			formId = Long.valueOf(bpmSeeyonPolicy.getForm());
                	        		operationId = Long.valueOf(bpmSeeyonPolicy.getOperationName());
            	        		}
            	        	}
        	        		display = FormHelper.getFormNameAndOperationName(formApp, formId, operationId);
            	        	mav.addObject("display", display);
        	        	}
        	        }
    			}else{
    				edocSummary = edocManager.getEdocSummaryById(affair.getObjectId(), false);
    				//branches_a8_v350sp1_r_gov GOV-5108  政务 向凡修复  整个流程已超期 在督办人的督办列表查看时显示依旧是 '未超期' Start 
    				if(null != edocSummary.getDeadline() && edocSummary.getDeadline() > 0 && isGov){
    					long OverTime = edocSummary.getCreateTime().getTime()+(edocSummary.getDeadline().longValue()*60*1000);//计算出公文整个流程的超期时间
    					if(null != edocSummary.getCompleteTime() && edocSummary.getCompleteTime().getTime() > OverTime){//流程节点走完 最后完成时间是否大于 超期时间
    						isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.true");
    					}else if(OverTime < new Date().getTime()){//如果公文在处理中 那么就判断当前时间是否大于 超期时间
    						isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.true");
    					}
    				}
    				//branches_a8_v350sp1_r_gov GOV-5108  政务 向凡修复   整个流程已超期 在督办人的督办列表查看时显示依旧是 '未超期' End
    				
    				archiveId = edocSummary.getArchiveId();
    				startDate = edocSummary.getCreateTime();
    				finishDate = edocSummary.getCompleteTime();
    				deadline = edocSummary.getDeadline();
    				mav.addObject("canForward", false);
    				mav.addObject("canModify", false);
    				mav.addObject("canEdit", false);
    				mav.addObject("canArchive", false);
    				mav.addObject("iscol", false);
    				//成发集团项目 程炯 2012-8-29 公文密级显示
    				if(edocSummary.getEdocSecretLevel() == null || "".equals(edocSummary.getSecretLevel())){
    					mav.addObject("secret", "内部");
    				}else
    				if(edocSummary.getEdocSecretLevel() == Constant.SecretLevel.none.ordinal()){
    					mav.addObject("secret", "内部");
    				}
    				else if(edocSummary.getEdocSecretLevel() == Constant.SecretLevel.noSecret.ordinal()){
    					mav.addObject("secret", "内部");
    				}
    				else if(edocSummary.getEdocSecretLevel() == Constant.SecretLevel.secret.ordinal()){
    					mav.addObject("secret", "秘密");
    				}
    				else if(edocSummary.getEdocSecretLevel() == Constant.SecretLevel.secretMore.ordinal()){
    					mav.addObject("secret", "机密");
    				}
    				//end
    			}


    			String projectName = "";
    			if(ApplicationCategoryEnum.collaboration.key()==affair.getApp()){
	    	        if(projectId != null){
	    	        	ProjectSummary project = projectManager.getProject(projectId);
	    	        	projectName = project.getProjectName();
	    	        }
    			}
    			mav.addObject("projectName", projectName);
    	        String archiveName = "";
    	        if(archiveId != null){
    	        	archiveName = docHierarchyManager.getNameById(archiveId);
    	        	mav.addObject("archiveName", archiveName);
    	        }


    	        String state = "";
    	    	switch(StateEnum.valueOf(affair.getState())){
    	        	case col_waitSend : state = Constant.getString4CurrentUser("col.state.11.waitSend");
    	        	break;
    	        	case col_sent : state = Constant.getString4CurrentUser("col.state.12.col_sent");
    	        	break;
    	        	case col_pending : state = Constant.getString4CurrentUser("col.state.13.col_pending");
    	        	break;
    	        	case col_done : state = Constant.getString4CurrentUser("col.state.14.done");
    	        	break;
    	    	}
    	    	mav.addObject("flowState", state);

    	    	mav.addObject("startDate", startDate);

    	        if(affair.getIsOvertopTime() && !isGov){//branches_a8_v350sp1_r_gov 政务 向凡 上面针对政务版的公文超期做了判断，此处就不需要了
    	        	isOvertopTime = Constant.getString4CurrentUser("node.isovertoptime.true");
    	        }

    	        mav.addObject("isOvertopTime", isOvertopTime);
    	        mav.addObject("affairApp",affair.getApp());
    	        if(ApplicationCategoryEnum.collaboration.key()==affair.getApp()){
    	        	mav.addObject("summary", colSummary);
    	        }else{
    	        	mav.addObject("summary", edocSummary);
    	        }

    	        //显示督办信息
    	        ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.summary.ordinal(),affair.getObjectId());
    	        if(detail != null){
    	        	mav.addObject("awakeDate", detail.getAwakeDate());
	                Set<ColSupervisor> colSupervisors = detail.getColSupervisors();
	                if(colSupervisors != null && colSupervisors.size()>0) {
	                	StringBuilder builder = new StringBuilder();
	                    for(ColSupervisor colSupervisor:colSupervisors) {
	                    	builder.append(colSupervisor.getSupervisorId()+",");
	                    }
	                    mav.addObject("supervisorIds", builder.substring(0, builder.length()-1));
	                }
    	        }
    		}catch(Exception e){
    			log.error("显示协同属性错误：", e);
    		}
    	}
    	return mav;
    }
    public ModelAndView showPushWindow(HttpServletRequest request,HttpServletResponse response) throws Exception{
	 	ModelAndView mav = new ModelAndView("collaboration/pushMessageList");
		String summaryId = request.getParameter("summaryId");
		String edocType = request.getParameter("edocType");
		//被回复意见的AffairId.
		String replyedAffairId = request.getParameter("replyedAffairId");
		ApplicationCategoryEnum app= ApplicationCategoryEnum.collaboration;
		if(Strings.isNotBlank(edocType) &&( "0".equals(edocType)||"1".equals(edocType)||"2".equals(edocType))){
			app = EdocUtil.getAppCategoryByEdocType(Integer.parseInt(edocType));
		}
		//显示已发，已办，暂存待办 三种事项
		List<Affair> affairs = affairManager.getALLAvailabilityAffairList(app,Long.valueOf(summaryId),false);
		for(Iterator<Affair> it =affairs.iterator();it.hasNext();){
			Affair  a = it.next();
			if(Integer.valueOf(StateEnum.col_pending.getKey()).equals(a.getState())){
				if(!Integer.valueOf(SubStateEnum.col_pending_ZCDB.getKey()).equals(a.getSubState())){
					it.remove();
				}
			}
		}
		Set<Long> members= new HashSet<Long>() ;//过滤重复的
		Affair replyedAffair = null;
		Affair senderAffair  = null;
		/**先取出被回复人的affair，然后对查找出来的Affair进行过滤，
		 *最后将被回复人加到发起人的后边.目的是为了排序：要达到被回复人和发起人在前面的效果
		 **/

		for(Iterator<Affair> it = affairs.iterator();it.hasNext();){
			Affair affair = it.next();
			if(affair.getState().intValue() == StateEnum.col_sent.key()){
				senderAffair = affair;
				members.add(affair.getMemberId());
				it.remove();
				continue;
			}
			if(Strings.isNotBlank(replyedAffairId)){
				if(affair.getId().equals(Long.valueOf(replyedAffairId))){
					replyedAffair = affair;
					members.add(affair.getMemberId());
					it.remove();
				}
			}
			if(senderAffair!=null
					&& (replyedAffair!=null  || Strings.isBlank(replyedAffairId))){
				break;
			}
		}


		for(Iterator<Affair> it = affairs.iterator();it.hasNext();){
			Affair affair = it.next();
			if(!members.contains(affair.getMemberId())) members.add(affair.getMemberId());
			else  it.remove();
		}

		List<Affair> sortAffairs = new ArrayList<Affair>();

		long currentUserId = CurrentUser.get().getId();
		Long senderId = null;
		if(senderAffair!=null){
			sortAffairs.add(senderAffair);
			senderId=senderAffair.getMemberId();
		}

		if(replyedAffair!=null && Strings.isNotBlank(replyedAffairId)){
			sortAffairs.add(replyedAffair);
		}
		sortAffairs.addAll(affairs);
		// 被回复中始终过滤自己
		for(Iterator<Affair> it = sortAffairs.iterator();it.hasNext();){
			Affair affair = it.next();
			if (affair.getMemberId().longValue()==currentUserId
					|| (affair.getMemberId().equals(senderId) && affair.getState()!=StateEnum.col_sent.key()))
				it.remove();
		}
		mav.addObject("affairs", sortAffairs);


		String selected = request.getParameter("sel");
		List<Long> l = new ArrayList<Long>();
		if(Strings.isNotBlank(selected)){
			String[] s = selected.split("[#]");
			for(String s1 : s){
				l.add(Long.valueOf(s1.split("[,]")[0]));
			}
		}

		mav.addObject("sels", l);
		return mav;
    }

    public Map<String,Object> getSaveToLocalOrPrintPolicy(ColSummary summary,String nodePermissionPolicy,String lenPotents,Affair affair) {
    	Map<String,Object> map = new HashMap<String,Object>();
    	long permId=-1;
    	boolean officecanPrint = false;
    	String officecanSaveLocal = "true";
        try{
    		Long accountId= ColHelper.getFlowPermAccountId(CurrentUser.get().getLoginAccount(), summary, templeteManager);
    		FlowPerm fp=flowPermManager.getFlowPerm(MetadataNameEnum.col_flow_perm_policy.toString(),nodePermissionPolicy, accountId);
    		permId=fp.getFlowPermId();
    		String baseAction=fp.getBasicOperation();
    		//是否有打印权限，根据v3x_affair表中的state字段来判断
    		//节点对文档中心是否有打印权限
    		boolean pigCanPrint = (Strings.isNotBlank(lenPotents) && lenPotents.charAt(1) == '1');
    		if(Strings.isBlank(lenPotents)){
    			if(StateEnum.col_waitSend.getKey() == affair.getState()
    				|| StateEnum.col_sent.getKey() == affair.getState()
    				|| baseAction.indexOf("Print")>-1){
    				officecanPrint = true ;
    			}
    			officecanSaveLocal = "true";
			} else {
				officecanPrint = "0".equals(lenPotents.substring(2, 3)) ? false : true;
				officecanSaveLocal = "0".equals(lenPotents.substring(1, 2)) ? "false" : "true";
			}
    		map.put("officecanPrint", officecanPrint);
    		map.put("officecanSaveLocal", officecanSaveLocal);
    		return map;
		} catch (Exception e) {
			log.error(e);
		} finally {
			return map;
		}
    }
	/**
	 * @param colManagerFacade the colManagerFacade to set
	 */
	public void setColManagerFacade(ColManagerFacade colManagerFacade) {
		this.colManagerFacade = colManagerFacade;
	}
	
	@CheckRoleAccess(roleTypes = {RoleType.NeedNoCheck})
    public ModelAndView checkPersonSecretLevel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String secretLevel = request.getParameter("secretLevel");
        if(null == secretLevel || "".equals(secretLevel))
            secretLevel = "1";
        User user = CurrentUser.get();
        V3xOrgMember member = orgManager.getMemberById(user.getId());
        PrintWriter out = response.getWriter();
        out.println(Integer.parseInt(secretLevel) > member.getSecretLevel() ? 0 : 1);
        return null;
    }

    // 成发集团项目 程炯 检查当前流程密级能否使用出人员外的其他类型的节点以及跟踪人员和督办人员的密级
    @CheckRoleAccess(roleTypes = {RoleType.NeedNoCheck})
    public ModelAndView checkNodeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String secretLevel = request.getParameter("secretLevel");
        String tracks = request.getParameter("tracks");
        String supervisorId = request.getParameter("supervisorId");
        if(null == secretLevel || "".equals(secretLevel))
            secretLevel = "1";
        if(Integer.parseInt(secretLevel) <= 1) {
            return null;
        }
        User user = CurrentUser.get();
        FlowData flowData = FlowData.flowdataFromRequest();
        ProcessResolver processResolver = ProcessResolver.getInstance();
        String alertTitle = "当前流程密级不能使用【角色】节点类型\n";
        StringBuffer sb = new StringBuffer();
        boolean flag = false;
        UniqueList<BPMHumenActivity> nodes = processResolver.getAllActivity(flowData, user, null, "", user.getId(), true, orgManager);
        if(nodes != null && nodes.size() > 0){
            for (BPMHumenActivity node : nodes) {
                List<BPMActor> actors = node.getActorList();
                if(actors != null && actors.size() > 0){
                    BPMParticipant party = actors.get(0).getParty();
                    String partyType = party.getType().id;
                    if(!partyType.trim().equals("user")){
                        flag = true;
                        break;
                    }
                }
            }
        }
        PrintWriter out = response.getWriter();
        if(flag){
            sb.append(alertTitle);
            out.println(sb.toString());
            return null;
        }
        Map<String, String[]> ids = this.getTrackAndSupervisor(tracks, supervisorId);
        String[] trackIds = ids.get("track");
        String[] supervisorIds = ids.get("supervisor");
        if(null != trackIds && trackIds.length > 0) {
            for(String trackId : trackIds) {
                V3xOrgMember member = orgManager.getMemberById(Long.parseLong(trackId));
                if(null == member || "".equals(member)) {
                    continue;
                }
                if(member.getSecretLevel() < Integer.parseInt(secretLevel)) {
                    out.println("当前跟踪人员中包含了低于流程密级的人员,请确认后重新选择");
                    return null;
                }
            }
        }
        if(null != supervisorIds && supervisorIds.length > 0) {
            for(String supervisor : supervisorIds) {
                V3xOrgMember member = orgManager.getMemberById(Long.parseLong(supervisor));
                if(null == member || "".equals(member)) {
                    continue;
                }
                if(member.getSecretLevel() < Integer.parseInt(secretLevel)) {
                    out.println("当前督办人员中包含了低于流程密级的人员,请确认后重新选择");
                    return null;
                }
            }
        }
        return null;
    }

    public Map<String, String[]> getTrackAndSupervisor(String tracks, String supervisors) {
        Map<String, String[]> ids = new HashMap<String, String[]>();
        String[] trackIds = null;
        String[] supervisorIds = null;
        if(null != tracks && !tracks.equals("")) {
            trackIds = tracks.split(",");
        }
        if(null != supervisors && !supervisors.equals("")) {
            supervisorIds = supervisors.split(",");
        }
        if(trackIds != null && trackIds.length > 0)
            ids.put("track", trackIds);
        if(supervisorIds != null && supervisorIds.length > 0)
            ids.put("supervisor", supervisorIds);
        return ids;
    }

    // 成发集团项目 程炯 检查整个流程是否有不符合当前流程密级的人员
    @CheckRoleAccess(roleTypes = {RoleType.NeedNoCheck})
    public ModelAndView checkProcessSecretLevel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 成发集团二开 郝后成 发起时解析流程并且判断流程中是否超出保密等级之下的人员 2012-09-04 start
        User user = CurrentUser.get();
        String secretLevel = request.getParameter("secretLevel");
        if(null == secretLevel || "".equals(secretLevel))
            secretLevel = "1";
        FlowData flowData = FlowData.flowdataFromRequest();
        Integer currentSecretLevel = Integer.parseInt(secretLevel);
        ProcessResolver processResolver = ProcessResolver.getInstance();
        String alertTitle = "当前流程中包含了低于流程密级的人员,请重新筛选人员,不符合的节点及包含人员详细信息如下:\n";
        StringBuffer sb = new StringBuffer();
        sb.append(alertTitle);
        boolean flag = false;
        List<Map<BPMHumenActivity, List<V3xOrgMember>>> activityWithMembersList = processResolver.getAllActivityWithMembersInProcess(flowData, user, null, "", user.getId(), true, orgManager);
        if(activityWithMembersList != null && activityWithMembersList.size() > 0) {
            for(Map<BPMHumenActivity, List<V3xOrgMember>> m : activityWithMembersList) {
                for(Map.Entry<BPMHumenActivity, List<V3xOrgMember>> e : m.entrySet()) {
                    BPMHumenActivity activity = e.getKey();
                    List<V3xOrgMember> memberList = e.getValue();
                    for(V3xOrgMember member : memberList) {
                        if(member.getSecretLevel() < currentSecretLevel)
                            sb.append("节点[ " + activity.getName() + " ],人员[ " + member.getName() + " ]" + "\n");
                    }
                }
            }
        }
        if(!sb.toString().equals(alertTitle)) {
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("当前流程中包含了低于流程密级的人员,请确认后重新选择");
            return null;
        }
        // 成发集团二开 郝后成 发起时解析流程并且判断流程中是否超出保密等级之下的人员 2012-09-04 end
        return null;
    }
    
    /** 
     * 2017-3-13 诚佰公司 添加自建流程判断 密级协同发送,转发必须经过部门主管审批
     * @param string 密级策略
     * @param string 流程xml
     */
    private String validateFlow(String secretLevel, String flowXml) throws Exception {
    	String outMsg = null;
    	
		if(null == secretLevel || "".equals(secretLevel)) {
			secretLevel = "1";
		}
		
		if (Integer.parseInt(secretLevel) <= 1) {
			return outMsg;
		}
		
		User user = CurrentUser.get();
		Long startPartyId = user.getId(); // 发起者职员id
		Long startDeptId = user.getDepartmentId();// 发起者部门id
		Long startAccountId = user.getAccountId(); // 发起者公司id
		
		// 1.先查找发起者所在部门的部门主管和分管领导，如果未设置，则不允许发送
		// 2.如果发起者本身就是部门主管或分管领导，则直接发送。如果不是，则判断部门主管和分管领导是否是流程的第一个接受者
		List<Long> memberIds = new ArrayList<Long>();
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		
		// 查找部门主管
		/*V3xOrgRole role = orgManagerDirect.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, startAccountId);
	    if (role != null) {
	    	members = orgManagerDirect.getMemberByRole(role.getBond(), startDeptId, role.getId());
			if(members != null && !members.isEmpty()) {
				for(V3xOrgMember vom : members) {
					memberIds.add(vom.getId());
				}
			}
	    }*/
	    
	    // 查找分管领导
		V3xOrgRole role = orgManagerDirect.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPLEADER, startAccountId);
	    if (role != null) {
	    	members = orgManagerDirect.getMemberByRole(role.getBond(), startDeptId, role.getId());
			if(members != null && !members.isEmpty()) {
				for(V3xOrgMember vom : members) {
					memberIds.add(vom.getId());
				}
			} else {
				Long parentDeptId = startDeptId; // 初始化部门id
		    	while (true) {
		    		V3xOrgDepartment parentDept = orgManagerDirect.getParentDepartment(parentDeptId);
    		    	if (parentDept == null) break;
    		    	
    		    	parentDeptId = parentDept.getId(); // 父部门id
    		    	members = orgManagerDirect.getMemberByRole(role.getBond(), parentDeptId, role.getId()); // 查找父部门分管领导
    				if(members != null && !members.isEmpty()) {
    					for(V3xOrgMember vom : members) {
    						memberIds.add(vom.getId());
    					}
    				}
		    	}
		    }
	    }
	    
	    if (memberIds.isEmpty()) {
	    	outMsg = "密级协同发起者所在部门未设置分管领导。";
	    	return outMsg;
	    }
	    
	    // 发起者不是部门主管或分管领导
	    if (!memberIds.contains(startPartyId)) {
	    	// 解析流程XML
			Document document = DocumentHelper.parseText(flowXml);
    		
    		String xpath ="/processes/process/link[@from='start']";
    		Element startLinkNode = (Element) document.selectSingleNode(xpath);
    		String startToId = startLinkNode.attribute("to").getText(); // 第一个接收者
    		
    		// 查找第一个接收者的节点数目，判断是否串行发送
    		xpath ="/processes/process/link[@from='" + startToId + "']";
    		List<Element> startToList = document.selectNodes(xpath);
    		
    		outMsg = "密级流程第一个接收者必须是分管领导。";
    		if (startToList.size() != 1) {
    	        return outMsg;
    		}
    		
			// 查找第一个接收者职员信息
			xpath ="/processes/process/node[@id='" + startToId + "']";
    		Element secondNode = (Element) document.selectSingleNode(xpath);
    		Element secondActor = secondNode.element("actor"); // 职员信息
    		String secondPartyId =  secondActor.attribute("partyId").getText(); // 职员id
    		
    		// 第一个接受者不是发起者部门主管或分管领导
    		if (!memberIds.contains(Long.parseLong(secondPartyId))) {
    			 return outMsg;
    		}
	    }
	    return null;
    }
    
}