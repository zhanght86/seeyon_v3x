/**
 *
 */
package com.seeyon.v3x.edoc.controller;

import static java.io.File.separator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.bpm.definition.BPMAbstractNode;
import net.joinwork.bpm.definition.BPMActivity;
import net.joinwork.bpm.definition.BPMProcess;
import net.joinwork.bpm.definition.BPMSeeyonPolicy;
import net.joinwork.bpm.definition.BPMTransition;
import net.joinwork.bpm.engine.wapi.WorkItem;
import net.joinwork.bpm.engine.wapi.WorkItemManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BaseBulletinManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.ColTrackMember;
import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.domain.Party;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.event.CollaborationCancelEvent;
import com.seeyon.v3x.collaboration.exception.ColException;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.collaboration.templete.domain.ColBranch;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.ProcessModeSelectorModel;
import com.seeyon.v3x.common.SystemEnvironment;
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
import com.seeyon.v3x.common.filemanager.manager.Util;
import com.seeyon.v3x.common.flag.BrowserEnum;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.isignature.ISignatureHtmlManager;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.office.HandWriteManager;
import com.seeyon.v3x.common.office.HtmlHandWriteManager;
import com.seeyon.v3x.common.permission.manager.PermissionManager;
import com.seeyon.v3x.common.processlog.ProcessLogAction;
import com.seeyon.v3x.common.processlog.manager.ProcessLogManager;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.common.web.workflow.DateSharedWithWorkflowEngineThreadLocal;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.EdocEnum.MarkCategory;
import com.seeyon.v3x.edoc.domain.EdocBody;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocMark;
import com.seeyon.v3x.edoc.domain.EdocMarkDefinition;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.exception.EdocException;
import com.seeyon.v3x.edoc.exception.EdocMarkHistoryExistException;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocInnerMarkDefinitionManager;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocManagerFacade;
import com.seeyon.v3x.edoc.manager.EdocMarkDefinitionManager;
import com.seeyon.v3x.edoc.manager.EdocMarkHistoryManager;
import com.seeyon.v3x.edoc.manager.EdocMarkManager;
import com.seeyon.v3x.edoc.manager.EdocPermissionControlManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.manager.EdocSuperviseManager;
import com.seeyon.v3x.edoc.manager.EdocSwitchHelper;
import com.seeyon.v3x.edoc.util.DataUtil;
import com.seeyon.v3x.edoc.util.EdocOpinionDisplayUtil;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.edoc.webmodel.EdocFormModel;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionDisplayConfig;
import com.seeyon.v3x.edoc.webmodel.EdocOpinionModel;
import com.seeyon.v3x.edoc.webmodel.EdocSearchModel;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.edoc.webmodel.MoreSignSelectPerson;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.exchange.domain.EdocRecieveRecord;
import com.seeyon.v3x.exchange.manager.RecieveEdocManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.PopSelectParseUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;
import com.seeyon.v3x.workflow.event.WorkflowEventListener;
import com.seeyon.v3x.workflow.event.WorkflowEventListener.NodeAddition;


/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0
 * @since JDK 5.0
 */
public class EdocController extends BaseController{


	private static final Log log = LogFactory.getLog(EdocController.class);

	private MetadataManager metadataManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;
	private EdocManager edocManager;
	private UpdateIndexManager updateIndexManager;
	private AffairManager affairManager;
	private EdocPermissionControlManager edocPermissionControlManager;
	private EdocFormManager edocFormManager;
	private TempleteManager templeteManager;
	private FlowPermManager flowPermManager;
	private PermissionManager permissionManager;
	private RecieveEdocManager recieveEdocManager;
	private EdocMarkManager edocMarkManager;
	private EdocMarkHistoryManager edocMarkHistoryManager;
	private EdocInnerMarkDefinitionManager edocInnerMarkDefinitionManager;
	private EdocMarkDefinitionManager edocMarkDefinitionManager;
	private EdocSummaryManager edocSummaryManager;
	private EdocSuperviseManager edocSuperviseManager;
	private ColSuperviseManager colSuperviseManager;

	private HandWriteManager handWriteManager;
	private HtmlHandWriteManager htmlHandWriteManager;
	private NewsTypeManager newsTypeManager;
	private BulTypeManager bulTypeManager;
    private BulDataManager bulDataManager;
    private NewsDataManager newsDataManager;
    private UserMessageManager userMessageManager;
    private ProcessLogManager processLogManager ;
    private FileManager fileManager;
    private DocHierarchyManager docHierarchyManager;
	private FileToExcelManager fileToExcelManager;
    private IndexManager indexManager;
    private ColManager colManager;
    private ISignatureHtmlManager iSignatureHtmlManager;
    private EdocManagerFacade edocManagerFacade;


	public void setiSignatureHtmlManager(ISignatureHtmlManager iSignatureHtmlManager) {
		this.iSignatureHtmlManager = iSignatureHtmlManager;
	}
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	public IndexManager getIndexManager() {
		return indexManager;
	}
	public void setHandWriteManager(HandWriteManager handWriteManager) {
		this.handWriteManager = handWriteManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setHtmlHandWriteManager(HtmlHandWriteManager htmlHandWriteManager)
	{
		this.htmlHandWriteManager=htmlHandWriteManager;
	}

	public HtmlHandWriteManager getHtmlHandWriteManager()
	{
		return this.htmlHandWriteManager;
	}

	public RecieveEdocManager getRecieveEdocManager() {
		return recieveEdocManager;
	}

	public EdocMarkManager getEdocMarkManager() {
		return edocMarkManager;
	}

	public void setEdocMarkManager(EdocMarkManager edocMarkManager) {
		this.edocMarkManager = edocMarkManager;
	}

	public void setRecieveEdocManager(RecieveEdocManager recieveEdocManager) {
		this.recieveEdocManager = recieveEdocManager;
	}

    public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public void setFlowPermManager(FlowPermManager flowPermManager)
	{
		this.flowPermManager=flowPermManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
        this.templeteManager = templeteManager;
    }

	public void setEdocFormManager(EdocFormManager edocFormManager)
	{
		this.edocFormManager=edocFormManager;
	}

	public void setAffairManager(AffairManager affairManager) {
        this.affairManager = affairManager;
    }

	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	public void setEdocManager(EdocManager edocManager)
	{
		this.edocManager=edocManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }
    public void setEdocPermissionControlManager(
            EdocPermissionControlManager edocPermissionControlManager) {
        this.edocPermissionControlManager = edocPermissionControlManager;
    }


	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView entryManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/edocFrameEntry");
		modelAndView.addObject("entry", request.getParameter("entry"));
    	return modelAndView ;
	}
	public ModelAndView fullEditor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("edoc/fullEditor");
		String summaryId = request.getParameter("summaryId");
		if(Strings.isNotBlank(summaryId)){
			EdocSummary s = edocManager.getEdocSummaryById(Long.valueOf(summaryId), true);
			modelAndView.addObject("summary", s);
		}
		return modelAndView;

	}
	/**
	 * 收文管理
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView recManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return super.redirectModelAndView("/edocController.do?method=edocFrame&from=listPending&controller=edocController.do&edocType="+EdocEnum.edocType.recEdoc.ordinal());
	}
	/**
	 * 发文管理
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView sendManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return super.redirectModelAndView("/edocController.do?method=edocFrame&from=listPending&controller=edocController.do&edocType="+EdocEnum.edocType.sendEdoc.ordinal());
	}
	/**
	 * 签报管理
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView signReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return super.redirectModelAndView("/edocController.do?method=edocFrame&from=listPending&controller=edocController.do&edocType="+EdocEnum.edocType.signReport.ordinal());
	}
    /**
     * 新建公文
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView newEdoc(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        ModelAndView modelAndView = new ModelAndView("edoc/newEdoc");

        String edocType=request.getParameter("edocType");
        int iEdocType=-1;
        if(edocType!=null && !"".equals(edocType))
        {
        	iEdocType=Integer.parseInt(edocType);
        }

        boolean canUpdateContent=true;//是否允许修改正文

        String comm=request.getParameter("comm");
        String from = request.getParameter("from");
        String s_summaryId = request.getParameter("summaryId");
        String templeteId = request.getParameter("templeteId");
        String oldtempleteId=request.getParameter("oldtempleteId");
        BPMProcess process = null;

        List<Attachment> atts = null;
        boolean canDeleteOriginalAtts = true;
        boolean cloneOriginalAtts = false;
        //Affair affair = null;
        EdocOpinion senderOpinion = null;

        EdocSummary summary=null;
        Templete templete = null;

        EdocForm defaultEdocForm=null;
        Long edocFormId=0L;
        String templeteType="";
        String register=request.getParameter("register");
        //openTempleteOfExchangeRegist:公文交换调用模板登记
        final boolean openTempleteOfExchangeRegist = "register".equals(register);
        EdocRecieveRecord record =null;
        //基准时长，在调用模板的时候对其赋值（直接调用模板|来自待发的模板）
        Integer standarduration = 0;
        //对已有流程的协同（重复发起、转发、保存待发），在页面上存储其定义XML
        
      //成发集团项目 程炯 为页面传入当前用户的密级 begin
        Integer peopleSecretLevel = orgManager.getMemberById(CurrentUser.get().getId()).getSecretLevel();
        modelAndView.addObject("peopleSecretLevel", peopleSecretLevel);
        //end
        if("transmitSend".equals(comm))
        {
        	String strEdocId=request.getParameter("edocId");
        	String transmitSendNewEdocId=request.getParameter("transmitSendNewEdocId");
        	Long edocId=0L;
        	if(strEdocId!=null && !"".equals(strEdocId)){edocId=Long.parseLong(strEdocId);}
        	summary=edocManager.getEdocSummaryById(edocId,true);
        	atts = attachmentManager.getByReference(summary.getId(), summary.getId());
        	canDeleteOriginalAtts=false;
        	cloneOriginalAtts=true;
        	//affair = new Affair();
            //affair.setIsTrack(true);
            defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);

            EdocBody body=summary.getFirstBody();
            Date createDate = body.getCreateTime();
            FileManager fileManager = (FileManager)ApplicationContextHolder.getBean("fileManager");
    		if(Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(body.getContentType())
    				||Constants.EDITOR_TYPE_OFFICE_WORD.equals(body.getContentType())
    				||Constants.EDITOR_TYPE_WPS_WORD.equals(body.getContentType())
    				||Constants.EDITOR_TYPE_WPS_EXCEL.equals(body.getContentType())){//非html正文都以附件形势转发
    			InputStream in = null;
    			try {
    				//查找清除了痕迹的公文。
    				Long srcFileId=Long.parseLong(body.getContent());
    				if(transmitSendNewEdocId!=null&&!"".equals(transmitSendNewEdocId))
    					srcFileId=Long.parseLong(transmitSendNewEdocId);

    				String srcPath=fileManager.getFolder(createDate, true) + separator+ String.valueOf(srcFileId);
    				//1.解密文件
    				String newPath=CoderFactory.getInstance().decryptFileToTemp(srcPath);
    				//2.转换成标准正文
    				String newPathName = SystemEnvironment.getSystemTempFolder() + separator + String.valueOf(UUIDLong.longUUID());
    				Util.jinge2StandardOffice(newPath, newPathName);
    				//3.构造输入流
    				in = new FileInputStream(new File(newPathName)) ;
    				V3XFile f = fileManager.save(in, ApplicationCategoryEnum.edoc, summary.getSubject() + EdocUtil.getOfficeFileExt(body.getContentType()), createDate, false);
    				atts.add(new Attachment(f, ApplicationCategoryEnum.edoc, com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE));
    			}
    			catch (Exception e) {
    				log.error("收文转发文错误 ", e);
    			}
    			finally{
    				IOUtils.closeQuietly(in);
    			}
    		}else if(Constants.EDITOR_TYPE_PDF.equals(body.getContentType())){
    			String srcPath=fileManager.getFolder(createDate, true) + separator+ String.valueOf(body.getContent());
    			InputStream in = new FileInputStream(new File(srcPath)) ;
				V3XFile f = fileManager.save(in, ApplicationCategoryEnum.edoc, summary.getSubject() + ".pdf", createDate, false);
				atts.add(new Attachment(f, ApplicationCategoryEnum.edoc, com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE));
    		}else
    		{//html正文，先生成文件
    			V3XFile f =fileManager.save(body.getContent()==null?"":body.getContent(), ApplicationCategoryEnum.edoc, summary.getSubject()+".htm", createDate, false);
				atts.add(new Attachment(f, ApplicationCategoryEnum.edoc, com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE));
    		}
    		summary =new EdocSummary();
            summary.setCreatePerson(user.getName());
            body = new EdocBody();
            String bodyContentType=Constants.EDITOR_TYPE_OFFICE_WORD;
            if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("officeOcx")==false){bodyContentType=Constants.EDITOR_TYPE_HTML;}
            body.setContentType(bodyContentType);
            summary.getEdocBodies().add(body);
            summary.setOrgAccountId(user.getLoginAccount());
            summary.setEdocType(EdocEnum.edocType.sendEdoc.ordinal());
            //转发时候，放入session，调用模版时候备用
            request.getSession().setAttribute("transmitSendAtts",atts);
        }
        else if("register".equals(comm))
        {
        	String strEdocId=request.getParameter("edocId");
        	String exchangeType=request.getParameter("exchangeType");
        	String exchangeOrgId=request.getParameter("exchangeOrgId");
        	String exchangeId=request.getParameter("exchangeId");
        	Long  _exchangeOrgId=0L;
        	if(Strings.isNotBlank(exchangeOrgId) && Strings.isNotBlank(exchangeType)){
        		_exchangeOrgId=getAccountIdOfRegisterByOrgIdAndOrgType(Long.parseLong(exchangeOrgId),Integer.parseInt(exchangeType));
        	}else{//首页过来的数据没有exchangeType和exchangeOrgId
        		_exchangeOrgId=getAccountIdOfRegisterByExchangeId(Long.parseLong(exchangeId));
        	}

			// 登记公文，判断当前操作人是否可以登记此公文
			record = recieveEdocManager.getEdocRecieveRecord(Long
					.parseLong(exchangeId));
			if (record.getRegisterUserId() != user.getId()) {
				Long agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(),record.getRegisterUserId() );
				if(!Long.valueOf(user.getId()).equals(agentId)){
					// 公文登记人已经转换
					String errMsg = ResourceBundleUtil.getString(
							"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
							"alert_hasChanged_register");
					if (Strings.isNotBlank(exchangeOrgId)
							&& Strings.isNotBlank(exchangeType)) {
						// 转到待登记
						PrintWriter out = response.getWriter();
						// super.printV3XJS(out);
						out.println("<script>");
						out.println("alert(\"" + errMsg + "\")");
						out.println("if(window.dialogArguments){"); // 弹出
						out.println("  window.returnValue = \"true\";");
						out.println("  window.close();");
						out.println("}else{");
						out
								.println("parent.location.href='edocController.do?method=edocFrame&from=listRegisterPending&edocType=1'");
						out.println("}");
						out.println("");
						out.println("</script>");
						return null;
					} else {
						// 首页过来的数据没有exchangeType和exchangeOrgId,刷新父页面
						PrintWriter out = response.getWriter();
						// super.printV3XJS(out);
						out.println("<script>");
						out.println("alert('" + errMsg + "');");
						out.println("if(window.dialogArguments){");
						out
								.println("window.returnValue='true';window.close();}else{");
						out.println("parent.parent.location.reload(true)");
						out.println("}");
						// out.println("history.go(-1);");
						out.println("</script>");
						return null;
					}
				}
			}

			// 公文已经被从待登记中回退，所以也不能继续登记
			if (record.getStatus() == com.seeyon.v3x.exchange.util.Constants.C_iStatus_Torecieve) {
				// 转到待登记
				String errMsg = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"alert_hasBeStepBack_already");
				PrintWriter out = response.getWriter();
				// super.printV3XJS(out);
				out.println("<script>");
				out.println("alert(\"" + errMsg + "\")");
				out.println("if(window.dialogArguments){"); // 弹出
				out.println("  window.returnValue = \"true\";");
				out.println("  window.close();");
				out.println("}else{");
				out
						.println("parent.location.href='edocController.do?method=edocFrame&from=listRegisterPending&edocType=1'");
				out.println("}");
				out.println("");
				out.println("</script>");
				return null;
			}

        	Long edocId=0L;
        	if(strEdocId!=null && !"".equals(strEdocId)){edocId=Long.parseLong(strEdocId);}
        	summary=edocManager.getEdocSummaryById(edocId,true);
        	atts = excludeType2ToNewAttachmentList(summary);
        	String createPersionName = user.getName();
        	if (record.getRegisterUserId() != user.getId()) {
        		V3xOrgMember member = orgManager.getMemberById(record.getRegisterUserId());
        		createPersionName = member.getName();
        	}
        	summary = cloneNewSummaryAndSetProperties(createPersionName, summary, _exchangeOrgId,record.getContentNo());
        	summary.setEdocType(EdocEnum.edocType.recEdoc.ordinal());
        	canDeleteOriginalAtts=false;
        	cloneOriginalAtts=true;
            defaultEdocForm=edocFormManager.getDefaultEdocForm(summary.getOrgAccountId(),iEdocType);
            summary.setFormId(defaultEdocForm.getId());
        	modelAndView.addObject("strEdocId", strEdocId);
        }
        else if (StringUtils.isNotEmpty(templeteId)) {//调用模板
			Long id = new Long(templeteId);
			//检查是否有调用此模板的权限
	        boolean isTemplate=templeteManager.hasAccSystemTempletes(id, user.getId());
	        if(isTemplate==false)
	        {//没有公文发起权不能发送
	        	String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_not_edoctempleteload");
	        	request.setAttribute("errMsg",errMsg);
	        	request.setAttribute("errMsgAlert", true);
	        	return super.redirectModelAndView(BaseController.REDIRECT_BACK);
	        }

			templete = templeteManager.get(id);
			standarduration = templete.getStandardDuration();
			//yangzd 如果新建公文时，先调用流程模板，再调用格式模板，则会本次要保持流程模板；反之也是。

			Templete oldtemplete=null;
			Long oldid=null;
			//yangzd
			if (StringUtils.isNotEmpty(oldtempleteId)&&!oldtempleteId.equals("1"))
			{
				oldid = new Long(oldtempleteId);
				oldtemplete = templeteManager.get(oldid);
			}
			modelAndView.addObject("isFromTemplate",true);
            modelAndView.addObject("templateType",templete.getType());
            modelAndView.addObject("workflowRule", templete.getWorkflowRule());
            templeteType=templete.getType();

            if(iEdocType==-1)
            {
            	iEdocType=EdocUtil.getEdocTypeByTemplateType(templete.getCategoryType());
            	edocType=Integer.toString(iEdocType);
            }

            if(!Templete.Type.text.name().equals(templete.getType())){
				List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(id,ApplicationCategoryEnum.edoc.ordinal());
	            modelAndView.addObject("branchs", EdocHelper.transformBranch(branchs, true));
	            if(branchs != null) {
	            	List<V3xOrgEntity> entities = this.orgManager.getUserDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
	            	if(entities != null && !entities.isEmpty()){
	            		V3xOrgTeam team = null;
	            		List<V3xOrgTeam> teams = new ArrayList<V3xOrgTeam>();
	            		for(V3xOrgEntity entity:entities){
	            			team = (V3xOrgTeam)entity;
	            			if(team != null && team.getAllMembers() != null && team.getAllMembers().contains(user.getId())){
	            				teams.add(team);
	            			}
	            		}
	            		modelAndView.addObject("teams", teams);
	            	}

	            	V3xOrgMember mem = orgManager.getMemberById(user.getId());
	            	List<MemberPost> secondPosts = mem.getSecond_post();
	            	modelAndView.addObject("secondPosts", secondPosts);
	            }
	            process = BPMProcess.fromXML(templete.getWorkflow()); //重新生成，因为要取新的节点名称
            }
            //yangzd 读取上一次的非正文模板的流程
            else
            {
    				if(null!=oldtemplete&&!Templete.Type.text.name().equals(oldtemplete.getType()))
    				{

    					List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(oldid,ApplicationCategoryEnum.edoc.ordinal());
    		            modelAndView.addObject("branchs", branchs);
    		            if(branchs != null) {
    		            	modelAndView.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
    		            	V3xOrgMember mem = orgManager.getMemberById(user.getId());
    		            	List<MemberPost> secondPosts = mem.getSecond_post();
    		            	modelAndView.addObject("secondPosts", secondPosts);
    		            }

    		            process = BPMProcess.fromXML(oldtemplete.getWorkflow()); //重新生成，因为要取新的节点名称
    				}

            }
            //yangzd
            iEdocType=EdocEnum.getEdocTypeByTemplateCategory(templete.getCategoryType());

            //通过交换进行登记，调用模板
            if(openTempleteOfExchangeRegist){
             	String strEdocId=request.getParameter("strEdocId");
             	String orgAccountIdStr=request.getParameter("orgAccountId");
             	Long edocId=0L;
             	Long orgAccountId=0L;
             	if(Strings.isNotBlank(strEdocId)){edocId=Long.parseLong(strEdocId);}
             	if(Strings.isNotBlank(orgAccountIdStr)){orgAccountId=Long.parseLong(orgAccountIdStr);}

             	String exchangeId=request.getParameter("exchangeId");
            	record = recieveEdocManager.getEdocRecieveRecord(Long.valueOf(exchangeId));

            	summary=edocManager.getEdocSummaryById(edocId,true);
            	atts = excludeType2ToNewAttachmentList(summary);
             	summary=cloneNewSummaryAndSetProperties(user.getName(), summary, orgAccountId,record.getContentNo());
             	if(!Templete.Type.workflow.name().equals(templete.getType())){
             		summary.setFormId(((EdocSummary)XMLCoder.decoder(templete.getSummary())).getFormId());
             	}else{
             		defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);
		            summary.setFormId(defaultEdocForm.getId());
             	}
             	summary.setEdocType(EdocEnum.edocType.recEdoc.ordinal());
             	//设置预归档目录
             	summary.setArchiveId(((EdocSummary)XMLCoder.decoder(templete.getSummary())).getArchiveId());
             	iEdocType=EdocEnum.edocType.recEdoc.ordinal();
             	modelAndView.addObject("strEdocId", strEdocId);
             	comm="register"; //不能去掉这个设置，页面登记调用模板的时候需要根据这个参数来过滤显示模板，
            }else{
				if(!Templete.Type.workflow.name().equals(templete.getType()))
				{
		            //不是通过交换进行登记，调用模板
					EdocBody body = (EdocBody)XMLCoder.decoder(templete.getBody());
					summary = (EdocSummary)XMLCoder.decoder(templete.getSummary());
					summary.getEdocBodies().add(body);
					iEdocType=summary.getEdocType();
				}else{
					 //yangzd 读取上一次的非流程模板的正文
	    		    if(null!=oldtemplete&&Templete.Type.workflow.name().equals(oldtemplete.getType()))
	    			{
    					EdocBody body = (EdocBody)XMLCoder.decoder(oldtemplete.getBody());
    					summary = (EdocSummary)XMLCoder.decoder(oldtemplete.getSummary());
    					summary.getEdocBodies().add(body);
    					iEdocType=summary.getEdocType();
	    			}
					else
					{
						//yangzd
						summary = new EdocSummary();
			            summary.setEdocType(iEdocType);
			            EdocBody body = new EdocBody();
			            String bodyContentType=Constants.EDITOR_TYPE_OFFICE_WORD;
			            if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("officeOcx")==false){bodyContentType=Constants.EDITOR_TYPE_HTML;}
			            body.setContentType(bodyContentType);
			            summary.getEdocBodies().add(body);
			            summary.setCanTrack(1);
			            defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);
			            summary.setFormId(defaultEdocForm.getId());
					}
				}
				atts = attachmentManager.getByReference(templete.getId(), templete.getId());
				summary.setCreatePerson(user.getName());
				EdocHelper.reLoadAccountName(summary,EdocHelper.getI18nSeperator(request));
            }
			canDeleteOriginalAtts = false;    //允许删除原附件
			cloneOriginalAtts = true;
			if(summary != null){
				senderOpinion = summary.getSenderOpinion();
			}
			//成发集团项目 程炯 2012-8-30 公文显示模板密级 begin
			String edocSecretLevel = "";
			Integer secret = null;
			if(summary != null){
				if(summary.getEdocSecretLevel() != null){
					secret = summary.getEdocSecretLevel();
					edocSecretLevel = this.getSecretLevelName(summary, secret);
				} else { // 2017-4-11 诚佰公司 公文模块密级为空时默认显示为内部
					secret = 1;
					edocSecretLevel = this.getSecretLevelName(summary, secret);
                 }
			}

			modelAndView.addObject("secret", secret);
			modelAndView.addObject("flowSecretLevel", edocSecretLevel);
			//end

            edocFormId=summary.getFormId();
            summary.setOrgAccountId(user.getLoginAccount());
            summary.setTempleteId(Long.parseLong(templeteId));
            //检查模版公文单是否存在

	    	  if(null!=edocFormId)
	          {
	              if(defaultEdocForm==null){defaultEdocForm=edocFormManager.getEdocForm(edocFormId);}
	              if(defaultEdocForm==null){defaultEdocForm=edocFormManager.getDefaultEdocForm(summary.getOrgAccountId(),iEdocType);}
	          }

            modelAndView.addObject("templete", templete);

            ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),id);
            if(detail != null) {

            	Long terminalDate = detail.getTemplateDateTerminal();
            	if(null!=terminalDate){
            	Date superviseDate = Datetimes.addDate(new Date(), terminalDate.intValue());
            	String date = Datetimes.format(superviseDate,Datetimes.datetimeWithoutSecondStyle);
            	detail.setAwakeDate(superviseDate);
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

//            		yangzd


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
//            		yangzd

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

            if("transmitSend".equals(request.getParameter("fromState")))
            {//转发调用模版操作
            	List<Attachment> attTemp=(List<Attachment>)request.getSession().getAttribute("transmitSendAtts");
            	if(attTemp!=null && attTemp.size()>0)
            	{
            		atts.addAll(attTemp);
            	}
            }
            List<Attachment> attachments=attachmentManager.getByReference(id);
            String attsStr="";
            int index=0;
            for (int i=1;i<(attachments.size()+1);i++) {
            	Attachment att=attachments.get(i-1);
            	if(att.getType()==0){
            		index++;
            		String fname=att.getFilename();
            		int lastIndex=fname.length();
            		if(fname.lastIndexOf(".")!=-1){
            			lastIndex=fname.lastIndexOf(".");
            		}
            		fname=fname.substring(0, lastIndex);
            		if("".equals(attsStr)||attsStr==null){
            			attsStr=index+"."+fname;
            		}else{
            			attsStr=attsStr+"\n"+index+"."+fname;
            		}
            	}
            }
            summary.setAttachments(attsStr);
            //公文模板是否绑定了内部文号
            modelAndView.addObject("isBoundSerialNo",edocMarkDefinitionManager.getEdocMarkByTempleteId(Long.valueOf(templeteId), MarkCategory.serialNo)==null?false:true);
       }else if (s_summaryId != null) { // 来自待发
            long summaryId = Long.parseLong(s_summaryId);
            summary = edocManager.getEdocSummaryById(summaryId, true);
            iEdocType=summary.getEdocType();//修改待发，类型参数没有传递进来
            if(recieveEdocManager.getEdocRecieveRecordByReciveEdocId(summaryId)!=null){
            	canUpdateContent=EdocSwitchHelper.canUpdateAtOutRegist();
            	comm="toSend";
            }
            EdocBody body = summary.getFirstBody();
            atts = attachmentManager.getByReference(summaryId, summaryId);
            senderOpinion = summary.getSenderOpinion();//发起人附言

            String affairId = request.getParameter("affairId");

            if (summary.getProcessId() != null) {
            	try {
            		process = EdocHelper.getRunningProcessByProcessId(summary.getProcessId());

            		modelAndView.addObject("isForm", "false");
				}
				catch (Exception e) {
					log.error("", e);
				}
            }

			modelAndView.addObject("templateId", summary.getTempleteId());
			if(summary.getTempleteId()!=null)
			{
				Long tId=summary.getTempleteId();
    			String outMsg = templeteManager.checkTemplete(tId, user.getId());
	    		if(Strings.isNotBlank(outMsg)){
		    		PrintWriter pw = response.getWriter();
		    		pw.println("<script>");
		    		pw.println("alert(\""+outMsg+"\");");
		    		pw.println("history.back()");
		    		pw.println("</script>");
		    		return null;
	    		}
				canDeleteOriginalAtts = false;
				List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(summary.getTempleteId(),ApplicationCategoryEnum.edoc.ordinal());
	            if(branchs != null) {
                    //显示分支条件使用流程中保留的，如果为空使用模板中的
	            	branchs = EdocHelper.updateBranchByProcess(summary.getProcessId(),branchs);
	            	modelAndView.addObject("branchs", branchs);
	            	modelAndView.addObject("teams", this.orgManager.getUserDomain(user.getId(), user.getLoginAccount(), V3xOrgEntity.ORGENT_TYPE_TEAM));
	            	V3xOrgMember mem = orgManager.getMemberById(user.getId());
	            	List<MemberPost> secondPosts = mem.getSecond_post();
	            	modelAndView.addObject("secondPosts", secondPosts);
	            }
				modelAndView.addObject("isFromTemplate",true);

				templete = templeteManager.get(summary.getTempleteId());
				templeteId=templete.getId().toString();
				templeteType=templete.getType();
				standarduration = templete.getStandardDuration();
				modelAndView.addObject("templateType",templete.getType());

				boolean sVisorsFromTemplate = false;
				ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(), summary.getTempleteId());
				if(detail != null){
					sVisorsFromTemplate = (detail.getColSupervisors() != null && !detail.getColSupervisors().isEmpty());
				}
				if(!sVisorsFromTemplate){
					List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(summary.getTempleteId());
					sVisorsFromTemplate = (null!=roleList && !roleList.isEmpty());
				}
				modelAndView.addObject("sVisorsFromTemplate", sVisorsFromTemplate);//公文调用的督办模板是否设置了督办人
			}

            //affair = this.affairManager.getWaitSendBysummaryIdAndState(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()), summaryId, StateEnum.col_waitSend.key());

            ColSuperviseDetail detail = colSuperviseManager.getSupervise(Constant.superviseType.edoc.ordinal(),summaryId);
          	modelAndView.addObject("fromSend", "true");
            if(detail != null) {
            	Set<ColSupervisor> supervisors = detail.getColSupervisors();
            	if(supervisors != null && supervisors.size()>0){
            		StringBuffer ids = new StringBuffer();
            		for(ColSupervisor supervisor:supervisors)
            			ids.append(supervisor.getSupervisorId() + ",");
            		modelAndView.addObject("colSupervisors", ids.substring(0, ids.length()-1));
            	}
            	modelAndView.addObject("colSupervise", detail);
            	modelAndView.addObject("superviseDate", Datetimes.format(detail.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
            }
            
        	//成发集团项目 程炯 2012-8-30 公文显示模板密级 begin
			String edocSecretLevel = "";
			Integer secret = null;
			if(summary != null){
				if(summary.getEdocSecretLevel() != null){
					secret = summary.getEdocSecretLevel();
					edocSecretLevel = this.getSecretLevelName(summary, secret);
				}
			}

			modelAndView.addObject("secret", secret);
			modelAndView.addObject("flowSecretLevel", edocSecretLevel);
			modelAndView.addObject("secretFlag","wait");
			//end

            edocFormId=summary.getFormId();
            //检查模版公文单是否存在
            defaultEdocForm=edocFormManager.getEdocForm(edocFormId);
            if(defaultEdocForm==null){defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);};

            //读取流程中的处理意见进行显示，待发中的公文有可能是回退、撤销到待发的，要显示历史处理过程中的意见
            //公文处理意见回显到公文单,排序
            LinkedHashMap lhs=edocManager.getEdocOpinion(summaryId, user.getId(),summary.getStartMember().getId());
            Hashtable hs=edocManager.getEdocOpinion(summary.getFormId(),lhs);
            String opinionsJs=EdocOpinionDisplayUtil.optionToJs(hs);
            modelAndView.addObject("opinionsJs",opinionsJs);
            //发起人意见
            modelAndView.addObject("senderOpinion",hs.get("senderOpinionList"));
            modelAndView.addObject("trackIds",getTrackIds(Strings.isNotBlank(affairId)?Long.valueOf(affairId):null));
            //公文模板是否绑定了内部文号
            if (Strings.isNotBlank(templeteId))
            	modelAndView.addObject("isBoundSerialNo",edocMarkDefinitionManager.getEdocMarkByTempleteId(Long.valueOf(templeteId), MarkCategory.serialNo)==null?false:true);
        }
        else { //直接新建
            summary = new EdocSummary();
           // summary.setStartTime(new Timestamp(System.currentTimeMillis()));
            summary.setOrgAccountId(user.getLoginAccount());
            summary.setEdocType(iEdocType);
            EdocBody body = new EdocBody();
            String bodyContentType=Constants.EDITOR_TYPE_OFFICE_WORD;
            if(com.seeyon.v3x.common.SystemEnvironment.hasPlugin("officeOcx")==false){bodyContentType=Constants.EDITOR_TYPE_HTML;}
            body.setContentType(bodyContentType);
            summary.getEdocBodies().add(body);
            summary.setCanTrack(1);
            summary.setCreatePerson(user.getName());
            defaultEdocForm=edocFormManager.getDefaultEdocForm(user.getLoginAccount(),iEdocType);
            comm="new_form";//代表第一次保存
        }
        if(summary.getStartTime()==null){//自动带出拟文时间。
       	  summary.setStartTime(new Timestamp(System.currentTimeMillis()));
        }
        if(Strings.isBlank(summary.getSendUnit()) && Strings.isBlank(summary.getSendUnitId())){
        	summary.setSendUnit(EdocRoleHelper.getAccountById(user.getLoginAccount()).getName());
        	summary.setSendUnitId("Account|"+Long.toString(user.getLoginAccount()));
        }
        if(Strings.isBlank(summary.getSendDepartment()) && Strings.isBlank(summary.getSendDepartmentId())){
        	Long deptId=0L;
        	String deptName="";
        	if(user.getAccountId()!=user.getLoginAccount()){
        		Long accountId=user.getLoginAccount();
        		Map<Long, List<ConcurrentPost>> map=orgManager.getConcurentPostsByMemberId(accountId, user.getId());
        		Set<Long> set = map.keySet();
        		if(Strings.isNotEmpty(set)){
        			V3xOrgDepartment dept=orgManager.getDepartmentById(set.iterator().next());
        			if(dept!=null){
        				deptName=dept.getName();
        				deptId=dept.getId();
        			}
        		}
        	}else{
        		deptId=user.getDepartmentId();
        		deptName=EdocRoleHelper.getDepartmentById(user.getDepartmentId()).getName();
        	}
        	summary.setSendDepartment(deptName);
        	summary.setSendDepartmentId("Department|"+Long.toString(deptId));
        	summary.setSendDepartment2(deptName);
        	summary.setSendDepartmentId2("Department|"+Long.toString(deptId));
        }
        summary.setOrgAccountId(user.getLoginAccount());
    	//预归档
		Long archiveId = null;
        String archiveName = "";
        if(summary.getArchiveId() != null){
        	archiveId = summary.getArchiveId();
        	archiveName = docHierarchyManager.getNameById(archiveId);
        }
        modelAndView.addObject("archiveName", archiveName);

        long checkSendAclUserId = user.getId();//代理人可以登记
        if("register".equals(comm) || openTempleteOfExchangeRegist){
        	checkSendAclUserId=record.getRegisterUserId();
        }
        //检查是否有公文发起权
        boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(summary.getOrgAccountId(),checkSendAclUserId,iEdocType);
        if(isEdocCreateRole==false)
        {//没有公文发起权不能发送
        	modelAndView = new ModelAndView("common/redirect");
        	String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_not_edoccreate");
        	modelAndView.addObject("redirectURL",BaseController.REDIRECT_BACK);
        	modelAndView.addObject("errMsg",errMsg);
        	return modelAndView;
        }

        //页面显示的公文单列表。如果是模板，只取当前模板的。
        String domainIds = orgManager.getUserIDDomain(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
        List <EdocForm> edocForms = getLoginAccountOrCurrentTempleteEdocForms(summary.getOrgAccountId(),domainIds,iEdocType,templeteId,edocFormId, templeteType);
        if(edocForms==null || edocForms.size()<=0 || defaultEdocForm==null)
        {
        	String szJs="<script>alert(\""+ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_nofind_edocForm")+"\");self.history.back();</script>";
        	response.getWriter().print(szJs);
        	return null;
        }
        edocFormId=defaultEdocForm.getId();
        modelAndView.addObject("edocForms",edocForms);
        modelAndView.addObject("attachments", atts);
        modelAndView.addObject("canDeleteOriginalAtts", canDeleteOriginalAtts);
        modelAndView.addObject("cloneOriginalAtts", cloneOriginalAtts);

        //modelAndView.addObject("affair", affair);

        if(process != null){
	        String caseProcessXML = process.toXML();
	        List<Party> workflowInfo = EdocHelper.getWorkflowInfo(process);
	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        modelAndView.addObject("hasWorkflow", true);
	        modelAndView.addObject("process_xml", caseProcessXML);
	        modelAndView.addObject("workflowInfo", workflowInfo);
	        modelAndView.addObject("isShowShortName", process.getIsShowShortName());
	        modelAndView.addObject("process_desc_by", FlowData.DESC_BY_XML);
		}

        Metadata remindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
        Metadata  deadlineMetadata= metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);

        modelAndView.addObject("remindMetadata", remindMetadata);
        modelAndView.addObject("deadlineMetadata", deadlineMetadata);

        modelAndView.addObject("controller", "edocController.do");
        modelAndView.addObject("appName",EdocEnum.getEdocAppName(iEdocType));
        modelAndView.addObject("templeteCategrory",EdocEnum.getTempleteCategory(iEdocType));

        Metadata flowPermPolicyMetadata=null;
        String defaultPerm="shenpi";
        if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
    	{
    		modelAndView.addObject("policy", "dengji");
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.rec");
    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_rec_permission_policy);
    		defaultPerm="yuedu";
    	}
        else if(EdocEnum.edocType.sendEdoc.ordinal()==iEdocType)
    	{
    		modelAndView.addObject("policy", "niwen");
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.send");
    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_send_permission_policy);
    	}
    	else
    	{
    		modelAndView.addObject("policy", "niwen");
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.send");
    		flowPermPolicyMetadata=metadataManager.getMetadata(MetadataNameEnum.edoc_qianbao_permission_policy);
    	}
        modelAndView.addObject("defaultPermLabel", "node.policy."+defaultPerm);
        modelAndView.addObject("flowPermPolicyMetadata",flowPermPolicyMetadata);
        long actorId=-1;
        try{
        	Long orgAccountId=null;
        	if(templete!=null){
        		orgAccountId=templete.getOrgAccountId();
        	}else{
        		orgAccountId=summary.getOrgAccountId();
        	}
        	actorId=flowPermManager.getFlowPerm(EdocUtil.getEdocMetadataNameEnum(iEdocType).name(),EdocUtil.getSendFlowpermNameByEdocType(iEdocType),orgAccountId).getFlowPermId();
        }catch(Exception e)
        {
        	log.error("",e);
        }
        String exchangeId=null;
        if("register".equals(comm) || openTempleteOfExchangeRegist)
        {
        	if(record.getStatus()==com.seeyon.v3x.exchange.util.Constants.C_iStatus_Registered)
        	{//公文已经登记
        		modelAndView = new ModelAndView("common/redirect");
            	String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_has_registe");
            	modelAndView.addObject("redirectURL",BaseController.REDIRECT_BACK);
            	modelAndView.addObject("errMsg",errMsg);
            	return modelAndView;
        	}
        	if(record.getContentNo().intValue()==EdocBody.EDOC_BODY_SECOND)
			{
				summary.setSendUnit(summary.getSendUnit2());summary.setSendUnit2("");
				summary.setSendUnitId(summary.getSendUnitId2());summary.setSendUnitId2("");
				summary.setSendDepartment(summary.getSendDepartment2());summary.setSendDepartment2("");
				summary.setSendDepartmentId(summary.getSendDepartmentId2());summary.setSendDepartmentId2("");
				summary.setDocMark(summary.getDocMark2());summary.setDocMark2("");
				summary.setCopies(summary.getCopies2());summary.setCopies2(0);
				summary.setSendTo(summary.getSendTo2());summary.setSendTo2("");
				summary.setSendToId(summary.getSendToId2());summary.setSendToId2("");
				summary.setCopyTo(summary.getCopyTo2());summary.setCopyTo2("");
				summary.setCopyToId(summary.getCopyToId2());summary.setCopyToId2("");
				summary.setReportTo(summary.getReportTo2());summary.setReportTo2("");
				summary.setReportToId2(summary.getReportToId2());summary.setReportToId2("");
			}
        }

        //校验sendUnitId数据是否正确
        if(!Strings.isBlank(summary.getSendUnitId()))
        {
        	if(summary.getSendUnitId().indexOf("|")<0){summary.setSendUnitId("Account|"+summary.getSendUnitId());}
        }
        if(!Strings.isBlank(summary.getSendUnitId2()))
        {
           	if(summary.getSendUnitId2().indexOf("|")<0){summary.setSendUnitId2("Account|"+summary.getSendUnitId2());}
        }

      //校验sendDepartmentId数据是否正确
        if(!Strings.isBlank(summary.getSendDepartmentId()))
        {
        	if(summary.getSendDepartmentId().indexOf("|")<0){summary.setSendDepartmentId("Department|"+summary.getSendDepartmentId());}
        }
        if(!Strings.isBlank(summary.getSendDepartmentId2()))
        {
           	if(summary.getSendDepartmentId2().indexOf("|")<0){summary.setSendDepartmentId2("Department|"+summary.getSendDepartmentId2());}
        }

        EdocFormModel fm=null;
        if(StringUtils.isNotEmpty(templeteId)){
        	fm = edocFormManager.getEdocFormModel(edocFormId,summary,actorId,false,true);
        	if (templete==null)
        		templete = templeteManager.get(Long.parseLong(templeteId));
        	EdocSummary edocSummary = (EdocSummary)XMLCoder.decoder(templete.getSummary());
        	if(edocSummary!=null){
        		modelAndView.addObject("isTempleteHasDeadline",edocSummary.getDeadline()!=null && edocSummary.getDeadline() != 0);
        		modelAndView.addObject("isTempleteHasRemind", edocSummary.getAdvanceRemind()!=null && edocSummary.getAdvanceRemind() != -1);
        		modelAndView.addObject("isTempleteHasArchiveId", edocSummary.getArchiveId()!=null);
        	}
        }else{
        	fm = edocFormManager.getEdocFormModel(edocFormId,summary,actorId);
        }
        summary.setIdIfNew();
        fm.setEdocBody(summary.getFirstBody());

        if("register".equals(comm) || openTempleteOfExchangeRegist)
        {
        	if(!Strings.isBlank(exchangeId)){
        		if(null!=record && null!=record.getContentNo()){
        			EdocBody edocBody = summary.getBody(record.getContentNo().intValue());
        			if(null!=edocBody){
        				fm.setEdocBody(edocBody);
        			}else{
        				fm.setEdocBody(summary.getFirstBody());
        			}
        		}
        	}

        	summary.setNewId();
        	summary.setCanTrack(1);
        	fm.getEdocSummary().setNewId();
        	canUpdateContent=EdocSwitchHelper.canUpdateAtOutRegist();

        	summary.setEdocOpinion(null);
        	summary.setEdocOpinions(null);
        	senderOpinion=null;
        }else if("transmitSend".equals(comm)){
        	summary.setNewId();
        	fm.getEdocSummary().setNewId();
        }
        String tattids = "";
        if(templete!=null){
        	List<Attachment> tatts = attachmentManager.getByReference(templete.getId(), templete.getId());
     		if(Strings.isNotEmpty(tatts)){
     			for(Attachment att:tatts){
     				tattids += String.valueOf(att.getFilename())+",";
     			}
     		}
        }
		modelAndView.addObject("tattids",tattids);
        fm.setEdocSummaryId(summary.getId());
        fm.setSenderOpinion(senderOpinion);
        fm.setDeadline(summary.getDeadline());
        modelAndView.addObject("formModel",fm);
        modelAndView.addObject("edocFormId",edocFormId);
        modelAndView.addObject("contentRecordId", summary.getEdocBodiesJs());
        modelAndView.addObject("comm",comm);
        //hasBody1,hasBody2:主要用途：联合发文有多套正文，判断是否存在套红后的多套正文
        modelAndView.addObject("hasBody1", summary.getBody(1)!=null);
        modelAndView.addObject("hasBody2", summary.getBody(2)!=null);
        modelAndView.addObject("selfCreateFlow",EdocSwitchHelper.canSelfCreateFlow());
        modelAndView.addObject("canUpdateContent",canUpdateContent);

        modelAndView.addObject("actorId",actorId);
        modelAndView.addObject("appType",EdocUtil.getAppCategoryByEdocType(iEdocType).getKey());
        modelAndView.addObject("personInput", EdocSwitchHelper.canInputEdocWordNum());

        String logoURL = EdocHelper.getLogoURL(summary.getOrgAccountId());
        modelAndView.addObject("logoURL", logoURL);
        modelAndView.addObject("standardDuration", standarduration == null?0:standarduration);
        return modelAndView;
    }
    
    /**
     * 成发集团项目 获得密级
     * */
    public String getSecretLevelName(EdocSummary summary,Integer secret){
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
	 *
	 * @param createPersionName
	 * @param summary
	 * @param orgAccountId
	 * @param contentNo 收文记录接受到的正文的编号。
	 * @return
	 * @throws CloneNotSupportedException
	 */
	private EdocSummary cloneNewSummaryAndSetProperties(String createPersionName, EdocSummary summary, Long orgAccountId,Integer contentNo) throws CloneNotSupportedException {
		EdocSummary edocSummary= (EdocSummary)summary.clone();
		edocSummary.setCreatePerson(createPersionName);
		edocSummary.setDeadline(0L);
		edocSummary.setAdvanceRemind(0L);
		edocSummary.setOrgAccountId(orgAccountId);
		edocSummary.setSerialNo(null);
		edocSummary.setArchiveId(null);
		edocSummary.setTempleteId(Long.valueOf(0L));
		//传入的参数ContentnO可能为空，所以进行防护性处理，先取出一个EdocBody.
		EdocBody eb = edocSummary.getFirstBody();
		for(EdocBody ebody :edocSummary.getEdocBodies()){
			if(ebody.getContentNo().equals(contentNo)){
				eb = ebody;
				break;
			}
		}
		edocSummary.setEdocBodies(new HashSet<EdocBody>());
		edocSummary.getEdocBodies().add(eb);
		//登记时候,为了保证印章校验有效,必须保持原来的文件名称不变(office控件的FileName属性一致才可以)
		if(!com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(summary.getFirstBody().getContentType()))
		{
			edocSummary.getFirstBody().setContentName(summary.getFirstBody().getContent());
		}
		return edocSummary;
	}

	private List<Attachment> excludeType2ToNewAttachmentList(EdocSummary summary) {
		List<Attachment> atts= attachmentManager.getByReference(summary.getId(), summary.getId());
		List<Attachment> exclude2List=new ArrayList<Attachment>();//需要重新new一个List，不能在atts的基础上使用remove.
		for(Attachment att:atts){
			if(!Integer.valueOf(2).equals(att.getType()))
				exclude2List.add(att);
		}
		return exclude2List;
	}
    /**
    * 查找签收部门所属单位或者签收单位
    * @param exchangeId   EdocRecieveRecord的ID
    * @return
    */
   private Long getAccountIdOfRegisterByExchangeId(Long exchangeId){
		EdocRecieveRecord record = recieveEdocManager.getEdocRecieveRecord(Long.valueOf(exchangeId));
		return getAccountIdOfRegisterByOrgIdAndOrgType(record.getExchangeOrgId(),record.getExchangeType());
   }
   /**
    * 查找签收部门所属单位或者签收单位
    * @param exchangeOrgId 签收ID（单位ID|部门ID）
    * @param exchangeOrgType  签收类型（部门|单位）
    * @return
    */
   private Long getAccountIdOfRegisterByOrgIdAndOrgType(Long exchangeOrgId,int exchangeOrgType){
   	if(com.seeyon.v3x.exchange.util.Constants.C_iAccountType_Dept==exchangeOrgType){
			V3xOrgDepartment dept;
			try {
				dept = orgManager.getDepartmentById(exchangeOrgId);
				return dept.getOrgAccountId();
			} catch (BusinessException e) {
				log.error("查找部门异常:",e);
			}
		}else {
			return exchangeOrgId;
		}
   	return 0L;
   }
   /**
    * 取得指定【单位，公文单类型】或者【指定模板公文单】的公文单列表。
    * 非流程模板，只取模板公文单，流程模板和自由流程取所有公文单。
    * @param accountId ：特定单位
    * @param iEdocType ：公文单类型
    * @param formId    ：公文单ID
    * @param templeteTypeName:模板类型，流程模板，格式模板
    * @return
    */
   private List<EdocForm>  getLoginAccountOrCurrentTempleteEdocForms(long accountId,String domainIds,int iEdocType,String templeteId,long formId,String templeteTypeName){
   	List<EdocForm> edocForms =new ArrayList<EdocForm>();
   	EdocForm nowForm=edocFormManager.getEdocForm(formId);
   	if(Strings.isNotBlank(templeteId) && !Templete.Type.workflow.name().equals(templeteTypeName)){
   		if(nowForm!=null){
   			edocForms.add(nowForm);
   		}
   	}else {
   		edocForms=edocFormManager.getEdocForms(accountId,domainIds,iEdocType);
   		if(nowForm!=null){
   			edocForms.add(nowForm);
   		}
   		//过滤掉兼职重复的公文单
   		List<EdocForm> l = new ArrayList<EdocForm>();
   		Set<Long> filter = new HashSet<Long>();
   		for(EdocForm form : edocForms){
   			if(!filter.contains(form.getId())) {
   				filter.add(form.getId());
   				l.add(form);
   			}
   		}
   		edocForms = l;
   	}
   	//去掉停用的
   	for(Iterator<EdocForm> it = edocForms.iterator();it.hasNext();){
   		EdocForm ef = it.next();
   		if(ef.getStatus()!= null && ef.getStatus().intValue() != EdocForm.C_iStatus_Published.intValue())
   			it.remove();
   	}
   	return edocForms;
   }

    /**
     * 发公文前的检查
     * 当发送公文是通过模板的，必须先走这一步，设置可能的选人信息（ProcessModeSelector)
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @deprecated 使用collaboration.preSend方法代替。
     */
   @Deprecated
    public ModelAndView preSend(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	if(Strings.isBlank(request.getParameter("__ActionToken"))){
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');</script>");
    		return null;
    	}

        FlowData flowData = FlowData.flowdataFromRequest();
        String currentNodeId = request.getParameter("currentNodeId");
        String isFromTemplate = request.getParameter("isFromTemplate");
        ModelAndView mav = new ModelAndView("collaboration/processModeSelectorMain");
        String processId = null;

        String splitProcessId = request.getParameter("processId");
        String _affairId = request.getParameter("affair_id");
        //      是否是督办时的匹配节点
        String fromColsupervise = request.getParameter("fromColsupervise");
        mav.addObject("fromColsupervise", fromColsupervise);
        Boolean excuteFlag = true;
        Affair affair = null;
        if(_affairId != null && !"".equals(_affairId)){
	        affair = affairManager.getById(Long.parseLong(_affairId));
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	excuteFlag = false;
	        }
        }

        if(excuteFlag){
        	BPMProcess process = null;
        	if(!flowData.isEmpty()) {
		        if(flowData.getXml() == null){
			        //根据选人界面传来的people生成流程模板XML
			        processId = EdocHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);
			        //生成流程模板对象
			        process = EdocHelper.getProcess(processId);
			        EdocHelper.deleteReadyProcess(processId);
			        String caseProcessXML = process.toXML();
			        if(StringUtils.isNotBlank(caseProcessXML)){
			        	caseProcessXML = EdocHelper.trimXMLProcessor(caseProcessXML);
						flowData.setXml(caseProcessXML);
			        }
			        currentNodeId = process.getStart().getId();
			        mav.addObject("caseProcessXML", StringEscapeUtils.escapeHtml(caseProcessXML));
			        mav.addObject("process_desc_by", FlowData.DESC_BY_XML);
		        }else {
		        	process = flowData.toBPMProcess();
		        }
        	}else {
        		processId = request.getParameter("processId");
        		process = EdocHelper.getCaseProcess(processId);
        	}

        	if(Strings.isNotBlank(currentNodeId) && !"start".equals(currentNodeId)){
        		BPMSeeyonPolicy seeyonPolicy = process.getActivityById(currentNodeId).getSeeyonPolicy();
        		if(!EdocHelper.isExecuteFinished(process, affair) || "zhihui".equals(seeyonPolicy.getId())) {
        			return mav;
        		}
        	}
	        boolean templateFlag = new Boolean(isFromTemplate).booleanValue();

	        long caseId = 0;
			String _caseId = request.getParameter("caseId");
			if(Strings.isNotBlank(_caseId)){
				caseId = Long.parseLong(_caseId);
			}

			if(!"start".equals(currentNodeId) && caseId == 0){ //发起时，case不存在
				log.warn("=======================================\\n处理公文时，caseId不存在。Parameter：" + request.getParameterMap() + "\\n=======================================");
			}

	        WorkflowEventListener.ProcessModeSelector selector = EdocHelper.preRunCase(process,currentNodeId, templateFlag, caseId);
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
                    for(int m=nodeAdditions.size()-1;m>=0;m--){
                    	NodeAddition addition=nodeAdditions.get(m);
                    	ProcessModeSelectorModel selectorModel = new ProcessModeSelectorModel();
                    	selectorModel.setAddition(addition);
                    	selectorModel.setNodeId(addition.getNodeId());
                    	selectorModel.setNodeName(addition.getNodeName());

                    	selectorModels.put(addition.getNodeId(), selectorModel);
                    	selectorModelNodeIds.add(addition.getNodeId());
                    }
                }
            }

	        HashMap<String,String> hash = new HashMap<String,String>();
	        if("start".equals(currentNodeId)) {
	        	hash.put("currentIsStart", "true");
	        	ColHelper.findDirectHumenChildrenCondition(process.getStart(), hash);
	        }
	        else
	        	ColHelper.findDirectHumenChildrenCondition(process.getActivityById(currentNodeId), hash);
	        if(hash.size()>0){
	        	//发起人信息
	        	V3xOrgMember member = null;
	        	V3xOrgPost standardPost = null;
	        	V3xOrgLevel groupLevel = null;
	        	long startDepartment = -1;
	        	long startPost = -1;
	        	long startLevel = -1;
	        	long startAccount = -1;
	        	long startStandardPost = -1;
	        	long startGroupLevel = -1;
	        	long startId = -1;
	        	if(!"start".equals(currentNodeId)) {
		        	EdocSummary summary = null;
		        	if(affair != null)
		        	   summary = edocManager.getEdocSummaryById(affair.getObjectId(), false);
		        	if(summary == null){
		        		if(processId != null){
		        			summary = edocManager.getSummaryByProcessId(processId);
		        		}else{
		        			summary = edocManager.getSummaryByProcessId(splitProcessId);
		        		}
		        	}

		        	member = orgManager.getMemberById(summary.getStartUserId());
		        	startId = member.getId();
		        	startDepartment = member.getOrgDepartmentId();
		        	startPost = member.getOrgPostId();
		        	startLevel = member.getOrgLevelId();
		        	startAccount = member.getOrgAccountId();
		        	standardPost = this.orgManager.getBMPostByPostId(startPost);
		        	groupLevel = this.orgManager.getLevelById(startLevel);
		        	if(standardPost != null)
		        		startStandardPost = standardPost.getId();
		        	if(groupLevel != null && groupLevel.getGroupLevelId() != null)
		        		startGroupLevel = groupLevel.getGroupLevelId();
	        	}

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
	        	if("start".equals(currentNodeId))
	        		member = orgManager.getMemberById(CurrentUser.get().getId());
	        	else
	        		//兼容代理的情况，直接取当前affair的memberid
	        		member = orgManager.getMemberById(affair.getMemberId());
	        	standardPost = this.orgManager.getBMPostByPostId(member.getOrgPostId());
	        	groupLevel = this.orgManager.getLevelById(member.getOrgLevelId());
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
	    	        			if(temp[1].indexOf("ispost")!=-1 || temp[1].indexOf("isNotpost")!=-1 || temp[1].indexOf("isStartpost")!=-1 || temp[1].indexOf("isNotStartpost")!=-1){
	    	        				temp[1] = EdocHelper.calcExpression(startId, CurrentUser.get().getId(), this.orgManager, temp[1]);
	    	        			}
	    	        			temp[1] = temp[1].replaceAll("Department", String.valueOf(member.getOrgDepartmentId()))
	    	        			.replaceAll("Post",String.valueOf(member.getOrgPostId())).replaceAll("Level", String.valueOf(member.getOrgLevelId()))
	    	        			.replaceAll("Account", String.valueOf(member.getOrgAccountId())).replaceAll("standardpost", (standardPost==null?"-1":standardPost.getId().toString()))
	    	        			.replaceAll("grouplevel", groupLevel==null||groupLevel.getGroupLevelId()==null?"-1":groupLevel.getGroupLevelId().toString())
	    	        			.replaceAll("'", "\\\\\'").replaceAll("&#91;", "").replaceAll("&#93;", "");
	    	        			if(temp[1].indexOf("handCondition")!=-1) {
	    	        				temp[1] = temp[1].replaceAll("handCondition","false");
	    	        				conditionTypes.add(2);
	    	        			}else
	    	        				conditionTypes.add(0);
	    	        			if(!"start".equals(currentNodeId))
	    	        				temp[1] = temp[1].replaceAll("startdepartment", String.valueOf(startDepartment)).replaceAll("startpost",String.valueOf(startPost))
	    	        				.replaceAll("startlevel", String.valueOf(startLevel)).replaceAll("startaccount", String.valueOf(startAccount))
	    	        				.replaceAll("startStandardpost", String.valueOf(startStandardPost)).replaceAll("startGrouplevel", String.valueOf(startGroupLevel))
	    	        				.replaceAll("&#91;", "").replaceAll("&#93;", "");
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
			        			if(temp[1].indexOf("ispost")!=-1 || temp[1].indexOf("isNotpost")!=-1 || temp[1].indexOf("isStartpost")!=-1 || temp[1].indexOf("isNotStartpost")!=-1){
	    	        				temp[1] = EdocHelper.calcExpression(startId, CurrentUser.get().getId(), this.orgManager, temp[1]);
	    	        			}
			        			temp[1] = temp[1].replaceAll("Department", String.valueOf(member.getOrgDepartmentId()))
			        			.replaceAll("Post",String.valueOf(member.getOrgPostId())).replaceAll("Level", String.valueOf(member.getOrgLevelId()))
			        			.replaceAll("Account", String.valueOf(member.getOrgAccountId())).replaceAll("standardpost", (standardPost==null?"-1":standardPost.getId().toString()))
			        			.replaceAll("grouplevel", groupLevel==null||groupLevel.getGroupLevelId()==null?"-1":groupLevel.getGroupLevelId().toString())
			        			.replaceAll("'", "\\\\\'").replaceAll("&#91;", "").replaceAll("&#93;", "");
			        			if(temp[1].indexOf("handCondition")!=-1) {
	    	        				temp[1] = temp[1].replaceAll("handCondition","false");
	    	        				conditionTypes.add(2);
	    	        			}else
	    	        				conditionTypes.add(0);
			        			if(!"start".equals(currentNodeId))
			        				temp[1] = temp[1].replaceAll("startdepartment", String.valueOf(startDepartment))
			        				.replaceAll("startpost",String.valueOf(startPost)).replaceAll("startlevel", String.valueOf(startLevel))
			        				.replaceAll("startaccount", String.valueOf(startAccount))
			        				.replaceAll("startStandardpost", String.valueOf(startStandardPost)).replaceAll("startGrouplevel", String.valueOf(startGroupLevel))
			        				.replaceAll("&#91;", "").replaceAll("&#93;", "");
			        			conditions.add(temp[1]);
			        			if(temp.length==3)
			        				forces.add("true");
			        			else
			        				forces.add("false");
			        		}
		        		}
		        	}
	        	}

	        	if(keys.size() > 0 && conditions.size() > 0){
		        	mav.addObject("allNodes", sb.toString());
		        	mav.addObject("nodeCount", hash.get("nodeCount"));

		        	for (int k = keys.size()-1; k >=0; k--) {
		        		String nodeId = keys.get(k);
		        		ProcessModeSelectorModel selectorModel = selectorModels.get(nodeId);
		        		if(selectorModel == null){
		        			selectorModel = new ProcessModeSelectorModel();

		        			selectorModels.put(nodeId, selectorModel);
		        			selectorModelNodeIds.add(nodeId);
		        			selectorModel.setProcessMode(process.getActivityById(nodeId).getProcessMode());
		        		}

		        		selectorModel.setNodeId(nodeId);
		        		selectorModel.setNodeName(nodeNames.get(k));
		        		selectorModel.setCondition(conditions.get(k));
		        		selectorModel.setForce(forces.get(k));
		        		selectorModel.setLink(links.get(k));
		        		selectorModel.setConditionType(conditionTypes.get(k));
					}
		        }
	        }

	        mav.addObject("selectorModels", selectorModels);
	        mav.addObject("selectorModelNodeIds", selectorModels.keySet());
        }
        else{
        	mav.addObject("selectorModels",null);
        }

        return mav;
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
    	EdocSummary edocSummary=edocManager.getEdocSummaryById(summaryId,false);

    	//是否发送消息
    	boolean isSendMessage = request.getParameterValues("isSendMessage") != null;

    	Timestamp now = new Timestamp(System.currentTimeMillis());

    	//发起人增加附言

        EdocOpinion senderOpinion = new EdocOpinion();
        senderOpinion.setIdIfNew();

        senderOpinion.setCreateTime(now);
        senderOpinion.setContent(request.getParameter("postscriptContent"));
        senderOpinion.setCreateUserId(user.getId());
        senderOpinion.setOpinionType(EdocOpinion.OpinionType.senderOpinion.ordinal());
        senderOpinion.setEdocSummary(edocSummary);
        senderOpinion.setIsHidden(false);

        this.edocManager.saveOpinion(senderOpinion, isSendMessage);

        this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, senderOpinion.getId(), request);

    	super.rendJavaScript(response, "parent.replyCommentOK('" + Datetimes.formateToLocaleDatetime(now) + "')");
    	return null;
    }

    public ModelAndView waitSendPreSend(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
    	ModelAndView mav = new ModelAndView("collaboration/processModeSelectorMain");
    	String _summaryId = request.getParameter("summaryId");
    	Long summaryId = Long.parseLong(_summaryId);
    	EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
    	String processId = summary.getProcessId();
    	if(processId==null || "".equals(processId))
    	{
    		response.getWriter().println("err:noflow");
    		return null;
    	}

    	BPMProcess process = ColHelper.getRunningProcessByProcessId(processId);
    	if(process == null){
    		response.getWriter().println("err:noflow");
    		return null;
    	}
    	/*
    	//检查是否有主送单位
    	if(summary.getEdocType()==EdocEnum.edocType.sendEdoc.ordinal() && (summary.getSendToId()==null || "".equals(summary.getSendToId())))
		{
    		response.getWriter().println("err:noMainSendComp");
    		return null;
		}
		*/
    	Long templateId = summary.getTempleteId();
    	boolean templateFlag = false;
    	if(templateId != null) templateFlag = true;

		long caseId = 0;
		String _caseId = request.getParameter("caseId");
		if(Strings.isNotBlank(_caseId)){
			caseId = Long.parseLong(_caseId);
		}

    	WorkflowEventListener.ProcessModeSelector selector = EdocHelper.preRunCase(process, "start", templateFlag, caseId);
        List<NodeAddition> invalidateActivity = selector.getInvalidateActivity();
        if(invalidateActivity != null && !invalidateActivity.isEmpty()){ //存在不可用的节点，不让发
        	mav.addObject("invalidateActivity", invalidateActivity);
        	return mav;
        }

    	//分枝 开始

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
    	        			temp[1] = temp[1].replaceAll("Department", String.valueOf(user.getDepartmentId())).replaceAll("Post",String.valueOf(user.getPostId())).replaceAll("Level", String.valueOf(user.getLevelId())).replaceAll("Account", String.valueOf(user.getAccountId())).replaceAll("'", "\\\\\'").replaceAll("&#91;", "").replaceAll("&#93;", "");
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
		        			temp[1] = temp[1].replaceAll("Department", String.valueOf(user.getDepartmentId())).replaceAll("Post",String.valueOf(user.getPostId())).replaceAll("Level", String.valueOf(user.getLevelId())).replaceAll("Account", String.valueOf(user.getAccountId())).replaceAll("'", "\\\\\'").replaceAll("&#91;", "").replaceAll("&#93;", "");
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
	        }
        }
    	//分枝 结束

    	mav.addObject("processModeSelector",selector);

    	return mav;
    }
    /**
     * 登记使用的文号,返回真正的文号串
     * @param markStr:掩码格式文号，详细见EdocMarkModel.parse()方法
     * @param markNum
     */
    private String registDocMark(Long summaryId,String markStr,int markNum,int edocType,boolean checkId,int markType) throws EdocMarkHistoryExistException
    {
        EdocMarkModel em=EdocMarkModel.parse(markStr);
        if (em!=null)
        {
        	Integer t = em.getDocMarkCreateMode();//0:未选择文号，1：下拉选择的文号，2：选择的断号，3.手工输入
        	String _edocMark = em.getMark(); //需要保存到数据库中的公文文号
        	Long markDefinitionId = em.getMarkDefinitionId();
        	Long edocMarkId = em.getMarkId();
        	User user = CurrentUser.get();
	        if(markType==EdocEnum.MarkType.edocMark.ordinal()){//公文文号
	        	if(t!=0){//等于0的时候没有进行公文文号修改
	        		edocMarkManager.disconnectionEdocSummary(summaryId,markNum);
	        	}
	        	if(edocType != com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN) {
	        		if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_NEW) { // 选择了一个新的公文文号
	        			Integer currentNo = em.getCurrentNo();
	        			edocMarkManager.createMark(markDefinitionId, currentNo, _edocMark, summaryId,markNum);
	        		}
	        		else if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_OLD) { // 选择了一个断号
	        			edocMarkManager.createMarkByChooseNo(edocMarkId, summaryId,markNum);
	        		}
	        		else if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_INPUT) { // 手工输入一个公文文号
	        			edocMarkManager.createMark(_edocMark, summaryId,markNum);
	        		}
	        	}else {//签报处理
	        		if (t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_NEW) {
	        			this.edocMarkHistoryManager.save(summaryId,_edocMark,markDefinitionId,markNum,user.getId(),user.getId(),checkId,true);
	        		}else if(t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_OLD) {
	        			this.edocMarkHistoryManager.saveMarkHistorySelectOld(edocMarkId,_edocMark,summaryId, user.getId(),checkId);
	        		}else if(t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_INPUT) {
	        			this.edocMarkHistoryManager.save(summaryId,_edocMark,markDefinitionId,markNum,user.getId(),user.getId(),checkId,false);
	        		}
	        	}
	        }else if(markType==EdocEnum.MarkType.edocInMark.ordinal()){//内部文号
	        	if(t == com.seeyon.v3x.edoc.util.Constants.EDOC_MARK_EDIT_SELECT_NEW){
	        		this.edocMarkDefinitionManager.setEdocMarkCategoryIncrement(markDefinitionId);
	        	}
	        }
	        return _edocMark;
        }
    	return null;
    }

    /**
     * 发送公文,跳转到已�发
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView send(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    	String allNodes = request.getParameter("allNodes");
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

    	boolean lostBranchData = branchNodes != null && !branchNodes.equals(allNodes);
    	if(Strings.isBlank(request.getParameter("__ActionToken")) || lostBranchData){
    		if(lostBranchData)
    			log.error("不能获取分支数据，allNodes:"+allNodes+" branchNodes:"+branchNodes);
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');");
    		out.println("history.back();</script>");
    		return null;
    	}
    	long formId=Long.parseLong(request.getParameter("edoctable"));


    	//检查公文单是否已经被删除。“当前公文单不存在，可能已经被删除，请检查。”
    	boolean isExsit = edocFormManager.isExsit(formId);
        if(!isExsit){
        	PrintWriter out = response.getWriter();
        	String errMsg = ResourceBundleUtil.getString(
					"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
					"alert_edocform_isnotexsit");
    		out.println("<script>alert('"+errMsg+"');");
    		out.println("history.back();</script>");
    		return null;
        }


    	User user = CurrentUser.get();
    	String comm=request.getParameter("comm");

		boolean isCanBeRegisted = true;
		// 来文登记,更新登记时间，给签收人发送消息
		String exchangeIdStr = request.getParameter("exchangeId");
		Long agentId = null;
		Long agentToId = null;
		EdocRecieveRecord record = null;

        EdocSummary edocSummary = new EdocSummary();
        edocSummary.setIdIfNew();
        //成发集团项目 程炯 2012-8-29 公文流程密级保存 begin
        String edocSecretLevel = request.getParameter("secretLevel");
        if(null == edocSecretLevel || "".equals(edocSecretLevel))
        	edocSecretLevel = "1";
        edocSummary.setEdocSecretLevel(Integer.parseInt(edocSecretLevel));
        edocSecretLevel = null;
        //end
        
		if ("register".equals(comm) && exchangeIdStr != null
				&& !"".equals(exchangeIdStr)) {
			Long exchangeId = Long.parseLong(exchangeIdStr);

			record = recieveEdocManager.getEdocRecieveRecord(exchangeId);
			// 登记公文，判断当前操作人是否可以登记此公文
			Long recordRegisterUserId = record.getRegisterUserId();

			if (recordRegisterUserId.longValue() != user.getId()) {
				agentId = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.edoc.key(),recordRegisterUserId );
				agentToId = recordRegisterUserId;
				if(!Long.valueOf(user.getId()).equals(agentId)){
					// 公文登记人已经转换
					isCanBeRegisted = false;
					String errMsg = ResourceBundleUtil.getString(
							"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
							"alert_hasChanged_register");
					PrintWriter out = response.getWriter();
					// super.printV3XJS(out);
					out.println("<script>");
					out.println("alert(\"" + errMsg + "\")");
					out.println("if(window.dialogArguments){"); // 弹出
					out.println("  window.returnValue = \"true\";");
					out.println("  window.close();");
					out.println("}else{");
					out
							.println("parent.location.href='edocController.do?method=edocFrame&from=listRegisterPending&edocType=1'");
					out.println("}");
					out.println("");
					out.println("</script>");
					return null;
				}
			}
			if (record.getStatus() == com.seeyon.v3x.exchange.util.Constants.C_iStatus_Torecieve) {
				// 公文已经回退
				isCanBeRegisted = false;
				String errMsg = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"alert_hasBeStepBack_already");
				PrintWriter out = response.getWriter();
				// super.printV3XJS(out);
				out.println("<script>");
				out.println("alert(\"" + errMsg + "\")");
				out.println("if(window.dialogArguments){"); // 弹出
				out.println("  window.returnValue = \"true\";");
				out.println("  window.close();");
				out.println("}else{");
				out
						.println("parent.location.href='edocController.do?method=edocFrame&from=listRegisterPending&edocType=1'");
				out.println("}");
				out.println("");
				out.println("</script>");
				return null;
			}
			if (record.getStatus() == com.seeyon.v3x.exchange.util.Constants.C_iStatus_Registered) {// 公文已经登记
				isCanBeRegisted = false;
				ModelAndView modelAndView = new ModelAndView("common/redirect");
				String errMsg = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"alert_has_registe");
				modelAndView.addObject("redirectURL",
						BaseController.REDIRECT_BACK);
				modelAndView.addObject("errMsg", errMsg);
				return modelAndView;
			}
			  //保存ISignatureHTML专业签章数据
	        saveISignatureHTMLByDeleteAndSave(record.getEdocId(), edocSummary.getId());
		}

        bind(request, edocSummary);

        DataUtil.requestToSummary(request,edocSummary,formId);


        //检查是否有调用此模板的权限
        boolean isTemplate=templeteManager.hasAccSystemTempletes(edocSummary.getTempleteId(), user.getId());
        if(isTemplate==false)
        {//没有公文发起权不能发送
        	ModelAndView modelAndView = new ModelAndView("common/redirect");
        	String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","alert_not_edoctempleteload");
        	modelAndView.addObject("redirectURL","/edocController.do?method=newEdoc&edocType="+edocSummary.getEdocType());
        	modelAndView.addObject("errMsg",errMsg);
        	modelAndView.addObject("errMsgAlert",true);
        	//?method=newEdoc&edocType="+edocType;
        	return modelAndView;
        }

        // 将公文流水号（内部文号）自动增1
        // add by handy,2007-10-16
        //if("new_form".equals(comm))
        //{
       // edocInnerMarkDefinitionManager.getInnerMark(edocSummary.getEdocType(), user.getAccountId(), true);
        //}
        //edocSummary.setSerialNo(serialNo);

        // 处理公文文号
        // 如果公文文号为空，不做任何处理
        String docMark = edocSummary.getDocMark();
        try {
        	docMark=this.registDocMark(edocSummary.getId(), docMark, 1,edocSummary.getEdocType(),false,EdocEnum.MarkType.edocMark.ordinal());
        }catch(EdocMarkHistoryExistException e) {
        	//签报提交时如果文号存在
        	ModelAndView modelAndView = new ModelAndView("common/redirect");
        	String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",e.getErrorCode());
        	modelAndView.addObject("redirectURL",BaseController.REDIRECT_BACK);
        	modelAndView.addObject("errMsg",errMsg);
        	modelAndView.addObject("errMsgAlert",true);
        	//?method=newEdoc&edocType="+edocType;
        	return modelAndView;
        }
        if(docMark!=null){edocSummary.setDocMark(docMark);}

        //处理第二个公文文号
        docMark = edocSummary.getDocMark2();
        docMark=this.registDocMark(edocSummary.getId(), docMark, 2,edocSummary.getEdocType(),false,EdocEnum.MarkType.edocMark.ordinal());
        if(docMark!=null){edocSummary.setDocMark2(docMark);}

        //内部文号
        String serialNo = edocSummary.getSerialNo();
        serialNo=this.registDocMark(edocSummary.getId(), serialNo, 3,edocSummary.getEdocType(),false,EdocEnum.MarkType.edocInMark.ordinal());
        if(serialNo!=null){edocSummary.setSerialNo(serialNo);}

        Map<String, Object> options = new HashMap<String, Object>();

        EdocEnum.SendType sendType = EdocEnum.SendType.normal;

        //是否重复发起
        if (null != request.getParameter("resend") && !"".equals(request.getParameter("resend"))) {
            sendType = EdocEnum.SendType.resend;
        }

        //是否转发
        if (null != request.getParameter("forward") && !"".equals(request.getParameter("forward"))) {
            sendType = EdocEnum.SendType.forward;
            //是否转发意见
            boolean isForwardOpinion = "true".equals(request.getParameter("isForwardOpinion"));
            //转发人附言
            String additionalComment = request.getParameter("additionalComment");
            //TODO 转发人追加的附件

            options.put("isForwardOpinion", isForwardOpinion);
            options.put("additionalComment", additionalComment);
        }

        String note = request.getParameter("note");//发起人附言
        EdocOpinion senderOninion = new EdocOpinion();
        senderOninion.setContent(note);
        senderOninion.setIdIfNew();
        String trackMode =request.getParameter("canTrack");
        boolean track = "1".equals(trackMode) ? true : false;
        senderOninion.affairIsTrack = track;
        senderOninion.setAttribute(1);
        senderOninion.setIsHidden(false);
        senderOninion.setCreateUserId(user.getId());
        senderOninion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        senderOninion.setPolicy(request.getParameter("policy"));
        senderOninion.setOpinionType(EdocOpinion.OpinionType.senderOpinion.ordinal());
        senderOninion.setNodeId(0);


        EdocBody body = new EdocBody();
        bind(request, body);
        body.setId(UUIDLong.longUUID());
        String tempStr=request.getParameter("bodyType");
        body.setContentType(tempStr);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateTime(new Timestamp(bodyCreateDate.getTime()));
        }

//      从request对象取到选人信息
        FlowData flowData = FlowData.flowdataFromRequest();
        //角色匹配
//        String[] manualSelectNodeId = request.getParameterValues("manual_select_node_id");
//        Map<String, String[]> map = new HashMap<String, String[]>();
//        if(manualSelectNodeId != null){
//            for(String node : manualSelectNodeId){
//                String[] people = request.getParameterValues("manual_select_node_id" + node);
//
//                map.put(node, people);
//            }
//        }
        Map<String, String[]> map = PopSelectParseUtil.getPopNodeSelectedValues(popNodeSelected);

        //分枝 开始
//        if(allNodes != null){
//        	Map<String,String> condition = new HashMap<String,String>();
//        	String[] nodes = StringUtils.split(allNodes,":");
//        	String result = "";
//        	if(nodes != null){
//        		for(String node:nodes){
//        			result = request.getParameter("condition"+node);
//        			result = "on".equals(result)?"false":"true";    //传递给后台的isDelete属性，所以要取反
//        			condition.put(node, result);
//        			if("true".equals(result))
//        				map.remove(node);
//        		}
//        		flowData.setCondition(condition);
//        	}
//        }
        Map<String,String> condition = PopSelectParseUtil.getPopNodeConditionValues(popNodeCondition, map);
        flowData.setCondition(condition);
        //分支 结束

        flowData.setAddition(map);

//      更新自定义节点的引用
        String[] policys = request.getParameterValues("policys");
        String[] itemNames = request.getParameterValues("itemNames");
        ColHelper.setPolicy(policys, itemNames, flowData);

        //删除原有附件
        if (!edocSummary.isNew()) {
            this.attachmentManager.deleteByReference(edocSummary.getId());
        }

        //test code begin
        edocSummary.setState(Constant.flowState.run.ordinal());
        edocSummary.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if(edocSummary.getStartTime()==null){
        	edocSummary.setStartTime(new Timestamp(System.currentTimeMillis()));
        }
        edocSummary.setStartUserId(user.getId());
        edocSummary.setFormId(Long.parseLong(request.getParameter("edoctable")));
        OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
        V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, edocSummary.getStartUserId());
        edocSummary.setStartMember(member);
        //如果公文单无登记人，自动赋上登记节为发起人。yangzd
        if(request.getParameter("my:create_person")==null)
        {
        	edocSummary.setCreatePerson(user.getName());
        }
        //yangzd
        if(edocSummary.getOrgAccountId()==null){
        	edocSummary.setOrgAccountId(user.getLoginAccount());
        }
        edocSummary.setOrgDepartmentId(getEdocOwnerDepartmentId(edocSummary.getOrgAccountId()));
        body.setIdIfNew();
        if(body.getCreateTime()==null)
        {
        	body.setCreateTime(new Timestamp(System.currentTimeMillis()));
        }
        body.setLastUpdate(new Timestamp(System.currentTimeMillis()));

        //test code end
        //保存附件
        String attaFlag = attachmentManager.create(ApplicationCategoryEnum.edoc, edocSummary.getId(), edocSummary.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	edocSummary.setHasAttachments(true);
        }
        String trackMembers = request.getParameter("trackMembers");
        String trackRange = request.getParameter("trackRange");
        String title = request.getParameter("title");
        String superviseTitle = request.getParameter("superviseTitle");
        String supervisorId = request.getParameter("supervisorId");
        String supervisors = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
        edocManagerFacade.runCaseFacade(edocSummary,flowData,body,senderOninion,sendType,options,comm,agentToId,track,
        		trackMembers,trackRange,title,superviseTitle,supervisorId,supervisors,awakeDate,exchangeIdStr,isCanBeRegisted,user);
        return super.redirectModelAndView("/edocController.do?method=edocFrame&from=listSent&controller=edocController.do&edocType="+edocSummary.getEdocType());
    }


    public ModelAndView sendImmediate(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
    	User user = CurrentUser.get();
    	String[] _summaryIds = request.getParameterValues("id");
    	String[] _affairIds = request.getParameterValues("affairId");
    	String edocType=request.getParameter("edocType");
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

    	StringBuffer message = new StringBuffer();
    	StringBuffer msgErr = new StringBuffer();
    	boolean sentFlag = false;

    	for (int i = 0; i < _summaryIds.length; i++)
    	{
    		Long summaryId = new Long(_summaryIds[i]);
    		EdocSummary edocSummary = edocManager.getEdocSummaryById(summaryId, true);
    		String processId = edocSummary.getProcessId();

    		//没有流程
    		if (processId == null || "".equals(processId.trim())) {
    			message.append(edocSummary.getSubject()).append("\n");
    			continue;
    		}

			FlowData flowData = ColHelper.getRunningProcessPeople(processId);

    		if(edocSummary.getEdocType()==EdocEnum.edocType.sendEdoc.ordinal())
    		{
    			EdocForm ef=edocFormManager.getEdocForm(edocSummary.getFormId());
    			boolean hasSendUnit=ef.isIncludeEdocElement(13L);
    			if(hasSendUnit && (edocSummary.getSendToId()==null || "".equals(edocSummary.getSendToId())))
    			{
    				msgErr.append(edocSummary.getSubject()).append("\n");
    				continue;
    			}
    		}

//    		String[] manualSelectNodeId = request.getParameterValues("manual_select_node_id");
//    		Map<String, String[]> map = new HashMap<String, String[]>();
//    		if(manualSelectNodeId != null){
//    			for(String node : manualSelectNodeId){
//    				String[] people = request.getParameterValues("manual_select_node_id" + node);
//
//    				map.put(node, people);
//    			}
//    		}

    		Map<String, String[]> map = PopSelectParseUtil.getPopNodeSelectedValues(popNodeSelected);

    		//分支 开始

//    		String allNodes = request.getParameter("allNodes");
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
	        Map<String,String> condition = PopSelectParseUtil.getPopNodeConditionValues(popNodeCondition, map);
	        flowData.setCondition(condition);
			flowData.setAddition(map);
			//processId = ColHelper.saveOrUpdateProcessByFlowData(flowData, processId, false);

    		//分支 结束

	        if(edocSummary.getOrgAccountId()==null){
	        	edocSummary.setOrgAccountId(user.getLoginAccount());
	        }
    		edocSummary.setOrgDepartmentId(getEdocOwnerDepartmentId(edocSummary.getOrgAccountId()));

    		this.colSuperviseManager.updateStatus(Integer.valueOf(1), edocSummary.getSubject(), user.getId(), user.getName(), Constant.superviseType.edoc.ordinal(), summaryId, Constant.superviseState.supervising.ordinal(), null);
    		sentFlag= edocManagerFacade.runCaseImmediateFacade(_affairIds[i],edocSummary,flowData);
    	}

    	ModelAndView mv = super.redirectModelAndView("/edocController.do?method=edocFrame&from=listSent&edocType="+edocType);

    	if (message.length() > 0) {
    		WebUtil.saveAlert("alert.sendImmediate.nowf", message.toString());
    		mv = super.redirectModelAndView("/edocController.do?method=edocFrame&from=listWaitSend&edocType="+edocType);
    	}
    	if (msgErr.length() > 0) {
    		WebUtil.saveAlert("alert.sendImmediate.noSendComp", msgErr.toString());
    		mv = super.redirectModelAndView("/edocController.do?method=edocFrame&from=listWaitSend&edocType="+edocType);
    	}

    	return mv;
    }

    /*修改文单后，保存修改的数据*/
    public ModelAndView updateFormData(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	Boolean ret=false;
    	User user = CurrentUser.get();
    	boolean updateMark = false;
    	long summaryId=Long.parseLong(request.getParameter("summaryId"));
        EdocSummary edocSummary = edocManager.getEdocSummaryById(summaryId,false);
        Long affairId = Long.valueOf(request.getParameter("affairId")) ;
        Affair affair = affairManager.getById(affairId) ;
        long formId=Long.parseLong(request.getParameter("edoctable"));
        try{
          DataUtil.requestToSummary(request,edocSummary,formId);

          // 处理公文文号
          // 如果公文文号为空，不做任何处理
          String docMark = edocSummary.getDocMark();
          if(docMark!=null && !"".equals(docMark))
          {
        	 // edocMarkManager.disconnectionEdocSummary(edocSummary.getId(),1);
        	  try {
        		  docMark=this.registDocMark(edocSummary.getId(), docMark, 1,edocSummary.getEdocType(),true,EdocEnum.MarkType.edocMark.ordinal());
        	  }catch(EdocMarkHistoryExistException e) {
        		  String errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource",e.getErrorCode());
        		  response.getWriter().print("result=historyMarkExist:"+errMsg);
        		  return null;
        	  }
        	  if(docMark!=null){edocSummary.setDocMark(docMark);}
        	  updateMark = true;
          }
          //处理第二个公文文号
          docMark = edocSummary.getDocMark2();
          if(docMark!=null && !"".equals(docMark))
          {
        	 // edocMarkManager.disconnectionEdocSummary(edocSummary.getId(),2);
        	  docMark=this.registDocMark(edocSummary.getId(), docMark, 2,edocSummary.getEdocType(),true,EdocEnum.MarkType.edocMark.ordinal());
        	  if(docMark!=null){edocSummary.setDocMark2(docMark);}
        	  updateMark = true;
          }

          //处理内部文号
          String serialNo = edocSummary.getSerialNo();
          if(serialNo!=null && !"".equals(serialNo))
          {
        	//  edocMarkManager.disconnectionEdocSummary(edocSummary.getId(),3);
        	  serialNo=this.registDocMark(edocSummary.getId(), serialNo, 3,edocSummary.getEdocType(),true,EdocEnum.MarkType.edocInMark.ordinal());
        	  if(serialNo!=null){edocSummary.setSerialNo(serialNo);}
          }



          // 加日志1.文号修改
          if(updateMark){
        	  //operationlogManager.insertOplog(summaryId, ApplicationCategoryEnum.edoc, EactionType.LOG_EDOC_UPDATE_MARK, EactionType.LOG_EDOC_UPDATE_MARK_DESCRIPTION,user.getName(), edocSummary.getSubject());
          }
          //加日志2.修改文单
         // operationlogManager.insertOplog(summaryId, formId, ApplicationCategoryEnum.edoc, EactionType.LOG_EDOC_UPDATE_CONTENT, EactionType.LOG_EDOC_UPDATE_CONTENT_DESCRIPTION, user.getName(), edocSummary.getSubject());

          BPMActivity bPMActivity =  EdocHelper.getBPMActivityByAffair(affair) ;
          String isOnlyModifyWordNo=request.getParameter("isOnlyModifyWordNo");
          if("true".equals(isOnlyModifyWordNo)){
        	  this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()),Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.modifyWordNo.getKey())) ;
          }else{
        	  this.processLogManager.insertLog(user, Long.valueOf(edocSummary.getProcessId()),Long.valueOf(bPMActivity.getId()), ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.modifyForm.getKey())) ;
          }
          edocManager.update(edocSummary);
          //暂存待办时更新affair中的标题和紧急程度
          Map<String,Object> columns = new HashMap<String,Object>(2);
          columns.put("subject", edocSummary.getSubject());
          if(!Strings.isEmpty(edocSummary.getUrgentLevel()))
        	  columns.put("importantLevel", Integer.parseInt(edocSummary.getUrgentLevel()));
          this.affairManager.update("objectId",edocSummary.getId(),columns);
          ret=true;
          docHierarchyManager.updatePigeHoleFile(edocSummary.getId(), ApplicationCategoryEnum.edoc.getKey(), user.getId()) ;
        }catch(Exception e)
        {
        	log.error(e);
        	//e.printStackTrace();
        }
        response.getWriter().print("result="+ret);
    	return null;
    }
    /**
     * 收文登记的时候，先删除再保存
     * @param srcDocumentId :原始文档ID
     * @param newDocumentId ： 新文档ID
     */
    private void saveISignatureHTMLByDeleteAndSave(Long srcDocumentId,Long newDocumentId){
    	iSignatureHtmlManager.deleteAllByDocumentId(newDocumentId);
    	iSignatureHtmlManager.copyISignatureHtml2NewDocument(srcDocumentId, newDocumentId);
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
    	User user = CurrentUser.get();

		// 保存待发时登记外来文
		boolean isRegistCanBeSaved = true;
		String exchangeIdStr = request.getParameter("exchangeId");
		EdocRecieveRecord record = null;
		if ("register".equals(request.getParameter("comm"))
				&& exchangeIdStr != null && !"".equals(exchangeIdStr)) {
			Long exchangeId = Long.parseLong(exchangeIdStr);
			record = recieveEdocManager.getEdocRecieveRecord(exchangeId);
			// 保存登记公文，判断当前操作人是否可以保存此待登记公文
			Long recordRegisterUserId = record.getRegisterUserId();
			if (recordRegisterUserId.longValue() != user.getId()) {
				// 公文登记人已经转换
				isRegistCanBeSaved = false;
				String errMsg = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"alert_hasChanged_register");
				PrintWriter out = response.getWriter();
				// super.printV3XJS(out);
				out.println("<script>");
				out.println("alert(\"" + errMsg + "\")");
				out.println("if(window.dialogArguments){"); // 弹出
				out.println("  window.returnValue = \"true\";");
				out.println("  window.close();");
				out.println("}else{");
				out
						.println("parent.location.href='edocController.do?method=edocFrame&from=listRegisterPending&edocType=1'");
				out.println("}");
				out.println("");
				out.println("</script>");
				return null;
			}
			if (record.getStatus() == com.seeyon.v3x.exchange.util.Constants.C_iStatus_Torecieve) {
				// 公文已经回退
				isRegistCanBeSaved = false;
				String errMsg = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"alert_hasBeStepBack_already");
				PrintWriter out = response.getWriter();
				// super.printV3XJS(out);
				out.println("<script>");
				out.println("alert(\"" + errMsg + "\")");
				out.println("if(window.dialogArguments){"); // 弹出
				out.println("  window.returnValue = \"true\";");
				out.println("  window.close();");
				out.println("}else{");
				out
						.println("parent.location.href='edocController.do?method=edocFrame&from=listRegisterPending&edocType=1'");
				out.println("}");
				out.println("");
				out.println("</script>");
				return null;
			}
			if (record.getStatus() == com.seeyon.v3x.exchange.util.Constants.C_iStatus_Registered) {// 公文已经登记
				isRegistCanBeSaved = false;
				ModelAndView modelAndView = new ModelAndView("common/redirect");
				String errMsg = ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"alert_has_registe");
				modelAndView.addObject("redirectURL",
						BaseController.REDIRECT_BACK);
				modelAndView.addObject("errMsg", errMsg);
				return modelAndView;
			}

		}

        EdocSummary summary = new EdocSummary();
        bind(request, summary);
        boolean isNew = summary.isNew();
        String edocSecretLevel = request.getParameter("secretLevel");
        if(edocSecretLevel==null || "".equals(edocSecretLevel)){
        	summary.setEdocSecretLevel(1);
        }else{
        	summary.setEdocSecretLevel(Integer.parseInt(edocSecretLevel));
        }
        summary.setIdIfNew();


        long formId=Long.parseLong(request.getParameter("edoctable"));

        DataUtil.requestToSummary(request,summary,formId);

        // 将公文流水号（内部文号）自动增1
        // add by handy,2007-10-16
        //String serialNo = edocInnerMarkDefinitionManager.getInnerMark(summary.getEdocType(), user.getAccountId(), true);
        //summary.setSerialNo(serialNo);

        // 处理公文文号
        // 如果公文文号为空，不做任何处理
        String docMark = summary.getDocMark();
        docMark=this.registDocMark(summary.getId(), docMark, 1,summary.getEdocType(),false,EdocEnum.MarkType.edocMark.ordinal());
        if(docMark!=null){summary.setDocMark(docMark);}

        //处理第二个公文文号
        docMark = summary.getDocMark2();
        docMark=this.registDocMark(summary.getId(), docMark, 2,summary.getEdocType(),false,EdocEnum.MarkType.edocMark.ordinal());
        if(docMark!=null){summary.setDocMark2(docMark);}

        //内部文号
        String serialNo = summary.getSerialNo();
        serialNo=this.registDocMark(summary.getId(), serialNo, 3,summary.getEdocType(),false,EdocEnum.MarkType.edocInMark.ordinal());
        if(serialNo!=null){summary.setSerialNo(serialNo);}

        String note = request.getParameter("note");//发起人附言
        EdocOpinion senderOninion = new EdocOpinion();
        senderOninion.setContent(note);
        senderOninion.setIdIfNew();
        String trackMode =request.getParameter("canTrack");
        boolean track = "1".equals(trackMode) ? true : false;
        senderOninion.affairIsTrack = track;
        summary.setCanTrack(track?1:0);
        senderOninion.setAttribute(1);
        senderOninion.setIsHidden(false);
        senderOninion.setCreateUserId(user.getId());
        senderOninion.setCreateTime(new Timestamp(System.currentTimeMillis()));
        senderOninion.setPolicy(request.getParameter("policy"));
        senderOninion.setOpinionType(EdocOpinion.OpinionType.senderOpinion.ordinal());
        senderOninion.setNodeId(0);

        EdocBody body = new EdocBody();
        bind(request, body);
        String tempStr=request.getParameter("bodyType");
        body.setContentType(tempStr);
        Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
        if (bodyCreateDate != null) {
            body.setCreateTime(new Timestamp(bodyCreateDate.getTime()));
        }
        body.setIdIfNew();

        //删除原有附件
        if (!summary.isNew()) {
            this.attachmentManager.deleteByReference(summary.getId());
        }
        /*
        FlowData flowData = new FlowData();
        flowData.setDesc_by(FlowData.DESC_BY_XML);
        String xml = request.getParameter("process_xml");
        flowData.setXml(xml);
        */
        FlowData flowData = FlowData.flowdataFromRequest();


        //      test code begin
        summary.setState(Constant.flowState.run.ordinal());
        if(summary.getCreateTime()==null)
        {
        	summary.setCreateTime(new Timestamp(System.currentTimeMillis()));
        }
        if(summary.getStartTime() ==  null){
        	summary.setStartTime(new Timestamp(System.currentTimeMillis()));
        }
        summary.setStartUserId(user.getId());
        summary.setFormId(Long.parseLong(request.getParameter("edoctable")));
        OrgManager orgManager = (OrgManager) ApplicationContextHolder.getBean("OrgManager");
        V3xOrgMember member = orgManager.getEntityById(V3xOrgMember.class, summary.getStartUserId());
        summary.setStartMember(member);

        body.setIdIfNew();
        if(body.getCreateTime()==null)
        {
        	body.setCreateTime(new Timestamp(System.currentTimeMillis()));
        }
        body.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        //test code end

        //保存附件
        String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.edoc, summary.getId(), summary.getId(), request);
        if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){
        	summary.setHasAttachments(true);
        }



        this.zcdbSupervise(request, response, summary, isNew,Constant.superviseState.waitSupervise.ordinal(),false);

        Long affairId = edocManager.saveDraft(flowData, summary, body, senderOninion);

       //跟踪
        String trackMembers = request.getParameter("trackMembers");
        String trackRange = request.getParameter("trackRange");
        //不跟踪 或者 全部跟踪的时候不向部门跟踪表中添加数据，所以将下面这个参数串设置为空。
        if(!track || "1".equals(trackRange)) trackMembers = "";
        edocManager.setTrack(affairId, track, trackMembers);

        //保存待发时登记外来文
        if("register".equals(request.getParameter("comm")) && exchangeIdStr!=null && !"".equals(exchangeIdStr))
        {
			if (isRegistCanBeSaved) {
				saveISignatureHTMLByDeleteAndSave(record.getEdocId(),summary.getId());
				Long exchangeId = Long.parseLong(exchangeIdStr);
				recieveEdocManager.registerRecieveEdoc(exchangeId,summary.getId());
			}
        }

        return super.redirectModelAndView("/edocController.do?method=edocFrame&from=listWaitSend&edocType="+summary.getEdocType());
    }

    /**
     * 页面框架
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView edocFrame(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
    	//String controller=request.getParameter("controller");
    	ModelAndView modelAndView = new ModelAndView("edoc/edocFrame");
    	modelAndView.addObject("controller", "edocController.do");
    	String edocType=request.getParameter("edocType");
    	request.setAttribute("condition", "");
    	modelAndView.addObject("edocType", edocType);
        return modelAndView;
    }

    /**
     * 已办公文列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listDone(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
		String condition = request.getParameter("condition");
		request.setAttribute("condition", condition);

		String textfield ="";
		String textfield1 ="";
		if ("docMark".equals(condition)) {
			textfield = request.getParameter("docMark");
		 	request.setAttribute("docMark", textfield);
		} else if ("docInMark".equals(condition)) {
		 	textfield1 = request.getParameter("docInMark");
		 	request.setAttribute("docInMark", textfield1);
		} else if ("createDate".equals(condition)) {
		 	textfield = request.getParameter("textfield");
		 	textfield1 = request.getParameter("textfield1");
		 	request.setAttribute("textfield", textfield);
		 	request.setAttribute("textfield1", textfield1);
		} else {
		 	textfield = request.getParameter("textfield");
		 	request.setAttribute(condition, textfield);
		}

		// 保存前台穿过来的json格式的字符串
        String edocMarkValue = request.getParameter("edocMarkValue") == null ? "" : request.getParameter("edocMarkValue");
        request.setAttribute("edocMarkValue", edocMarkValue);
        String edocInMarkValue = request.getParameter("edocInMarkValue") == null ? "" : request.getParameter("edocInMarkValue");
        request.setAttribute("edocInMarkValue", edocInMarkValue);

        String edocType=request.getParameter("edocType");
        int iEdocType=-1;
    	if(edocType!=null && !"".equals(edocType))
    	{
    		iEdocType=Integer.parseInt(edocType);
    	}

        ModelAndView modelAndView = new ModelAndView("edoc/listDone");

        User user = CurrentUser.get();
        V3xOrgMember theMember = null;
        theMember = orgManager.getEntityById(V3xOrgMember.class,user.getId());

        List<EdocSummaryModel> queryList = null;
        if (theMember.getAgentId() == -1)
        {
        	if(condition != null) {
        		queryList = edocManager.queryByCondition(iEdocType,condition, textfield, textfield1, StateEnum.col_done.key(),theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        	}
        	if (queryList != null) {
        		modelAndView.addObject("pendingList", queryList);
        	} else {
        		List<EdocSummaryModel> finishedList = edocManager.queryFinishedList(iEdocType,theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        		modelAndView.addObject("pendingList", finishedList);
        	}
        }
        else
        {
        	queryList = new ArrayList<EdocSummaryModel>();
        	modelAndView.addObject("pendingList", queryList);
        }

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);

        //Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        //modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("controller", "edocController.do");


    	modelAndView.addObject("edocType", edocType);
    	if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.rec");
    	}
    	else
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.send");
    	}
    	boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(iEdocType);
    	boolean isExchangeRole=EdocRoleHelper.isExchangeRole();
    	modelAndView.addObject("isEdocCreateRole", isEdocCreateRole);
    	modelAndView.addObject("isExchangeRole", isExchangeRole);
        return modelAndView;
    }

    /**
     * 待办公文列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listPending(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	request.getSession().removeAttribute("transmitSendAtts");
    	/*
    	List <V3xOrgMember> mems=EdocRoleHelper.getAccountExchangeUsers();
    	System.out.println(mems.size());

    	List <V3xOrgMember> memsd=EdocRoleHelper.getDepartMentExchangeUsers();
    	System.out.println(memsd.size());

    	boolean isEdocAdmin=EdocRoleHelper.isAccountExchange();
    	isEdocAdmin=EdocRoleHelper.isDepartmentExchange();
    	String ids=EdocRoleHelper.getUserExchangeDepartmentIds();
    	System.out.println("department ids="+ids);
    	*/

        String condition = request.getParameter("condition");
        request.setAttribute("condition", condition);

        String textfield ="";
        String textfield1 ="";
        if ("docMark".equals(condition)) {
        	textfield = request.getParameter("docMark");
        	request.setAttribute("docMark", textfield);
        } else if ("docInMark".equals(condition)) {
        	textfield1 = request.getParameter("docInMark");
        	request.setAttribute("docInMark", textfield1);
        } else if ("createDate".equals(condition)) {
        	textfield = request.getParameter("textfield");
        	textfield1 = request.getParameter("textfield1");
        	request.setAttribute("textfield", textfield);
        	request.setAttribute("textfield1", textfield1);
        } else {
        	textfield = request.getParameter("textfield");
        	if (Strings.isNotBlank(textfield))
        		request.setAttribute(condition, textfield);
        }

        // 保存前台穿过来的json格式的字符串
        String edocMarkValue = request.getParameter("edocMarkValue") == null ? "" : request.getParameter("edocMarkValue");
        request.setAttribute("edocMarkValue", edocMarkValue);
        String edocInMarkValue = request.getParameter("edocInMarkValue") == null ? "" : request.getParameter("edocInMarkValue");
        request.setAttribute("edocInMarkValue", edocInMarkValue);

        String edocType=request.getParameter("edocType");
    	int iEdocType=-1;
    	if(edocType!=null && !"".equals(edocType))
    	{
    		iEdocType=Integer.parseInt(edocType);
    	}

        ModelAndView modelAndView = new ModelAndView("edoc/listPending");

        User user = CurrentUser.get();
        V3xOrgMember theMember = null;
        theMember = orgManager.getEntityById(V3xOrgMember.class,user.getId());

        /***************测试代码开始*****************/
        //Long metadataId=7971699406983548687L;
        //edocManager.useMetadataValue(user.getLoginAccount(),metadataId,"22");
        /***************测试代码结束*****************/

        List<EdocSummaryModel> queryList = null;
        if (theMember.getAgentId() == -1){
        	if(condition != null) {
        		queryList = edocManager.queryByCondition(iEdocType,condition, textfield, textfield1,  StateEnum.col_pending.key(),theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        	}
        	if (queryList != null) {
        		modelAndView.addObject("pendingList", queryList);
        	} else {
        		List<EdocSummaryModel> pendingList = edocManager.queryTodoList(iEdocType,theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        		modelAndView.addObject("pendingList", pendingList);
        	}
        }
        else
        {
        	queryList = new ArrayList<EdocSummaryModel>();
        	modelAndView.addObject("pendingList", queryList);
        }

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);
        //Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        //modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("controller", "edocController.do");
        modelAndView.addObject("edocType", edocType);

        //是否包含“待登记”按钮
    	boolean hasRegistButton=false;
    	if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.rec");
    		hasRegistButton=true;
    	}
    	else
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.send");
    	}
    	modelAndView.addObject("hasRegistButton", hasRegistButton);

    	boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(iEdocType);
    	boolean isExchangeRole=EdocRoleHelper.isExchangeRole();
    	modelAndView.addObject("isEdocCreateRole", isEdocCreateRole);
    	modelAndView.addObject("isExchangeRole", isExchangeRole);
        return modelAndView;
    }

    /**
     * 待办登记公文列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listRegisterPending(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        //String condition = request.getParameter("condition");
        //String textfield = request.getParameter("textfield");
        //String textfield1 = request.getParameter("textfield1");

    	User user = CurrentUser.get();
    	Long userId=user.getId();
        ModelAndView mav = new ModelAndView("edoc/listRegisterPending");
        List<EdocRecieveRecord> list = recieveEdocManager.getWaitRegisterEdocRecieveRecords(userId);
        List<EdocRecieveRecord> list1 = new ArrayList<EdocRecieveRecord>();//成发集团项目

		if(list != null)
			//成发项目集团项目 程炯 筛选待登记list begin
			for(EdocRecieveRecord l : list){
				EdocSummary summary = edocSummaryManager.findById(l.getEdocId());
				if(orgManager.getMemberById(CurrentUser.get().getId()).getSecretLevel()>=summary.getEdocSecretLevel()){
					list1.add(l);
				}
			}
			//end
			for(EdocRecieveRecord r : list1){
				EdocSummary summary = edocSummaryManager.findById(r.getEdocId());
				r.setCopies(summary==null?0:summary.getCopies());
			}

        mav.addObject("edocType", EdocEnum.edocType.recEdoc.ordinal());
        mav.addObject("controller", "edocController.do");
        mav.addObject("newEdoclabel", "edoc.new.type.rec");

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);

        mav.addObject("colMetadata", colMetadata);
		mav.addObject("list", pagenate(list1));
        return mav;
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
     * 已发公文列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listSent(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

    	String condition = request.getParameter("condition");
		request.setAttribute("condition", condition);

		String textfield ="";
		String textfield1 ="";
		if ("docMark".equals(condition)) {
			textfield = request.getParameter("docMark");
		 	request.setAttribute("docMark", textfield);
		} else if ("docInMark".equals(condition)) {
		 	textfield1 = request.getParameter("docInMark");
		 	request.setAttribute("docInMark", textfield1);
		} else if ("createDate".equals(condition)) {
		 	textfield = request.getParameter("textfield");
		 	textfield1 = request.getParameter("textfield1");
		 	request.setAttribute("textfield", textfield);
		 	request.setAttribute("textfield1", textfield1);
		} else {
		 	textfield = request.getParameter("textfield");
		 	request.setAttribute(condition, textfield);
		}
		// 屏蔽文号下拉自动提示功能
//		// 保存前台穿过来的json格式的字符串
//        String edocMarkValue = request.getParameter("edocMarkValue") == null ? "" : request.getParameter("edocMarkValue");
//        request.setAttribute("edocMarkValue", edocMarkValue);
//        String edocInMarkValue = request.getParameter("edocInMarkValue") == null ? "" : request.getParameter("edocInMarkValue");
//        request.setAttribute("edocInMarkValue", edocInMarkValue);

        String edocType=request.getParameter("edocType");
    	int iEdocType=-1;
    	if(edocType!=null && !"".equals(edocType))
    	{
    		iEdocType=Integer.parseInt(edocType);
    	}

        ModelAndView modelAndView = new ModelAndView("edoc/listSent");

        User user = CurrentUser.get();
        V3xOrgMember theMember = null;
        theMember = orgManager.getEntityById(V3xOrgMember.class,user.getId());

        List<EdocSummaryModel> queryList = null;
        if (theMember.getAgentId() == -1)
        {
        	if(condition != null) {
        		queryList = edocManager.queryByCondition(iEdocType,condition, textfield, textfield1,  StateEnum.col_sent.key(),theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        	}
        	if (queryList != null) {
        		modelAndView.addObject("pendingList", queryList);
        	} else {
        		List<EdocSummaryModel> finishedList = edocManager.querySentList(iEdocType,theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        		modelAndView.addObject("pendingList", finishedList);
        	}
        }
        else
        {
        	queryList = new ArrayList<EdocSummaryModel>();
        	modelAndView.addObject("pendingList", queryList);
        }

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);

        //Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        //modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("controller", "edocController.do");
        modelAndView.addObject("edocType", edocType);

    	if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.rec");
    	}
    	else
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.send");
    	}

    	boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(iEdocType);
    	boolean isExchangeRole=EdocRoleHelper.isExchangeRole();
    	modelAndView.addObject("isEdocCreateRole", isEdocCreateRole);
    	modelAndView.addObject("isExchangeRole", isExchangeRole);

        return modelAndView;
    }
    /**
     * 待发公文列表
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView listWaitSend(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	String condition = request.getParameter("condition");
		request.setAttribute("condition", condition);

		String textfield ="";
		String textfield1 ="";
		if ("docMark".equals(condition)) {
			textfield = request.getParameter("docMark");
		 	request.setAttribute("docMark", textfield);
		} else if ("docInMark".equals(condition)) {
		 	textfield1 = request.getParameter("docInMark");
		 	request.setAttribute("docInMark", textfield1);
		} else if ("createDate".equals(condition)) {
		 	textfield = request.getParameter("textfield");
		 	textfield1 = request.getParameter("textfield1");
		 	request.setAttribute("textfield", textfield);
		 	request.setAttribute("textfield1", textfield1);
		} else {
		 	textfield = request.getParameter("textfield");
		 	request.setAttribute(condition, textfield);
		}

		// 屏蔽文号下拉自动提示功能
		// 保存前台穿过来的json格式的字符串
//        String edocMarkValue = request.getParameter("edocMarkValue") == null ? "" : request.getParameter("edocMarkValue");
//        request.setAttribute("edocMarkValue", edocMarkValue);
//        String edocInMarkValue = request.getParameter("edocInMarkValue") == null ? "" : request.getParameter("edocInMarkValue");
//        request.setAttribute("edocInMarkValue", edocInMarkValue);

        String edocType=request.getParameter("edocType");
    	int iEdocType=-1;
    	if(edocType!=null && !"".equals(edocType))
    	{
    		iEdocType=Integer.parseInt(edocType);
    	}

        ModelAndView modelAndView = new ModelAndView("edoc/listWaitSend");

        User user = CurrentUser.get();
        V3xOrgMember theMember = null;
        theMember = orgManager.getEntityById(V3xOrgMember.class,user.getId());

        List<EdocSummaryModel> queryList = null;
        if (theMember.getAgentId() == -1)
        {
        	if(condition != null) {
        		queryList = edocManager.queryByCondition(iEdocType,condition, textfield, textfield1,  StateEnum.col_waitSend.key(),theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        	}
        	if (queryList != null) {
        		modelAndView.addObject("pendingList", queryList);
        	} else {
        		List<EdocSummaryModel> finishedList = edocManager.queryDraftList(iEdocType,theMember.getSecretLevel());//成发集团项目 程炯 2012-8-31 根据人员密级筛选公文
        		modelAndView.addObject("pendingList", finishedList);
        	}
        }
        else
        {
        	queryList = new ArrayList<EdocSummaryModel>();
        	modelAndView.addObject("pendingList", queryList);
        }

        Map<String, Metadata> colMetadata = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);

        //Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        //modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("colMetadata", colMetadata);
        modelAndView.addObject("controller", "edocController.do");
        modelAndView.addObject("edocType", edocType);

    	if(EdocEnum.edocType.recEdoc.ordinal()==iEdocType)
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.rec");
    	}
    	else
    	{
    		modelAndView.addObject("newEdoclabel", "edoc.new.type.send");
    	}

    	boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(iEdocType);
    	boolean isExchangeRole=EdocRoleHelper.isExchangeRole();
    	modelAndView.addObject("isEdocCreateRole", isEdocCreateRole);
    	modelAndView.addObject("isExchangeRole", isExchangeRole);

        return modelAndView;
    }
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
     * 正常处理公文
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView finishWorkItem(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
    	String allNodes = request.getParameter("allNodes");
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
    	boolean lostBranchData = branchNodes != null && !branchNodes.equals(allNodes);
    	if(Strings.isBlank(request.getParameter("__ActionToken")) || lostBranchData){
    		if(lostBranchData)
    			log.error("不能获取分支数据，affairId:"+request.getParameter("affairId")+" summaryId:"+request.getParameter("summary_id")+" allNodes:"+allNodes+" branchNodes:"+branchNodes);
    		PrintWriter out = response.getWriter();
    		out.println("<script>alert('操作失败：网络异常，系统无法获取所需的提交数据。');");
    		out.println("try {parent.getA8Top().endProc();}");
    		out.println("catch (e) {};");
    		out.println("parent.doEndSign();");
    		out.println("</script>");
    		return null;
    	}

		// 选择的特定的单位公文收发员
		String edocMangerID = request.getParameter("memberList");

    	User user = CurrentUser.get();
        String sSummaryId = request.getParameter("summary_id");
        long summaryId = Long.parseLong(sSummaryId);
        String processId = request.getParameter("processId");
        boolean isRelieveLock = true;
        EdocSummary summary = null;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.finishWorkItem,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        String _affairId = request.getParameter("affairId");
	        Long affairId = null;
	        try {
	            affairId = new Long(_affairId);
	        } catch (Exception ex) {

	        }

	        Affair affair = affairManager.getById(affairId);
	        if(affair == null || affair.getState() != StateEnum.col_pending.key())
	        {
	        	response.getWriter().println("<script>alert(\""+EdocHelper.getErrorMsgByAffair(affair)+"\");parent.doEndSign();</script>");
	        	return null;
	        }
	        try {
				//校验是否是代理处理，以及代理信息是否成立
				boolean isValidate=validateAgent(affair,user);
				if(!isValidate){
					log.error("校验代理失败,当前用户是："+user.getId()+","+user.getName()+"; 被代理人（事项处理人）:"+affair.getMemberId()+"; affairId:"+affair.getId());
					response.getWriter().println("<script>alert(\"校验代理失败，请检查日志信息\");parent.doEndSign();</script>");
		        	return null;
				}
			} catch (Exception e) {
				log.error("校验代理出现异常"+"; affairId:"+affair.getId(),e);
				response.getWriter().println("<script>alert(\"校验代理出现异常，请检查日志信息\");parent.doEndSign();</script>");
	        	return null;
			}
	        EdocOpinion signOpinion = new EdocOpinion();
	        bind(request, signOpinion);
	        signOpinion.setCreateUserId(affair.getMemberId());

	        String attitude = request.getParameter("attitude");
	        if(!Strings.isBlank(attitude)){
	        	signOpinion.setAttribute(Integer.valueOf(attitude).intValue());
	        }else{
	        	signOpinion.setAttribute(com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL);
	        }
	        String content = request.getParameter("contentOP");
	        signOpinion.setContent(content);
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


	        String trackMembers = request.getParameter("trackMembers");
	        String trackRange = request.getParameter("trackRange");
	        //不跟踪 或者 全部跟踪的时候不向部门跟踪表中添加数据，所以将下面这个参数串设置为空。
	        if(!track || "1".equals(trackRange)) trackMembers = "";
	        edocManager.setTrack(affairId, track, trackMembers);

	        signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
	        signOpinion.setIdIfNew();

	        long nodeId=-1;
	        if(request.getParameter("currentNodeId")!=null && !"".equals(request.getParameter("currentNodeId")))
	        {
	        	nodeId=Long.parseLong(request.getParameter("currentNodeId"));
	        }
	        /*Manager可以得到，不需要再这里设置。
	         * signOpinion.setNodeId(nodeId);
	        //设置代理人信息
	        if(user.getId()!= affair.getMemberId())
	        {
	        	signOpinion.setProxyName(user.getName());
	        }
	         */
	        String manualSelect = request.getParameter("manual_select");
	        if(manualSelect==null || manualSelect.equals("")){
	            manualSelect = null;
	        }
	        Map<String, String[]> map = PopSelectParseUtil.getPopNodeSelectedValues(popNodeSelected);
//	        String[] manualSelectNodeId = request.getParameterValues("manual_select_node_id");
//	        Map<String, String[]> map = new HashMap<String, String[]>();
//	        if(manualSelectNodeId != null){
//	            for(String node : manualSelectNodeId){
//	                String[] people = request.getParameterValues("manual_select_node_id" + node);
//
//	                map.put(node, people);
//	            }
//	        }
//	     	// 分支流程
	        //key - 节点Id  value - 是否选择 true|false
//	        Map<String,String> condition = new HashMap<String,String>();
//	        if(allNodes != null){
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
//	        	}
//	        }
	        Map<String,String> condition = PopSelectParseUtil.getPopNodeConditionValues(popNodeCondition, map);
	        /** fixbug40074:wangchw
	        if(condition.size()>0){
	        	if(!EdocHelper.hasNodeExist(affair, condition)){
	        		response.getWriter().println("<script>alert(\""+com.seeyon.v3x.edoc.util.Constants.getString("workflow.branchNotSyn")+"\");parent.doEndSign();</script>");
		        	return null;
	        	}
	        }**/

	        String exchangeType=request.getParameter("edocExchangeType");
	        if(exchangeType!=null && !"".endsWith(exchangeType))
	        {
	        	signOpinion.exchangeType=Integer.parseInt(exchangeType);
	        	//部门交换的时候，由于edocManagerID为空，使用edocManagerID来传递选择的要交换的部门ID值,用来选择部门公文收发员
	        	if(signOpinion.exchangeType==com.seeyon.v3x.exchange.util.Constants.C_iExchangeType_Dept){
	        		edocMangerID=request.getParameter("returnDeptId");
	        	}
	        }

	        //-- 督办,如果下列三项参数有一项为空, 即调用原始的finishWorkItem方法 : 否则调用重载后加督办的finishWorkItem方法
	        String spMemberId = request.getParameter("supervisorId");
	        String superviseDate = request.getParameter("awakeDate");
	        String supervisorNames = request.getParameter("supervisors");
	        String title = request.getParameter("superviseTitle");

	        summary = edocManager.getEdocSummaryById(summaryId, true);
	        //设置手动选择的归档路径ID
	        String archiveId=request.getParameter("archiveId");
	        if(Strings.isNotBlank(archiveId) && signOpinion.isPipeonhole){
	        	summary.setArchiveId(Long.parseLong(archiveId));
	        }
	        //为了保存流程日志中修改附件的记录在处理提交之前，所以将保存附件的操作提前了。bug29527
	        //保存附件
	        String oldOpinionIdStr=request.getParameter("oldOpinionId");
	        if(!"".equals(oldOpinionIdStr))
	        {//删除原来意见,上传附件等
	        	Long oldOpinionId=Long.parseLong(oldOpinionIdStr);
	        	attachmentManager.deleteByReference(summaryId, oldOpinionId);
	        	edocManager.deleteEdocOpinion(oldOpinionId);
	        }
	        //保存正文的附件,是否修改了正文附件
	        //start 使用附件组件来重构 dongyj
            AttachmentEditHelper editHelper = new AttachmentEditHelper(request);
            if(editHelper.hasEditAtt()){//是否修改附件
            	edocManager.saveUpdateAttInfo(editHelper.attSize(),summaryId,editHelper.parseProcessLog(Long.parseLong(processId), nodeId));
            }
        	String uploadAttFlag = this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, signOpinion.getId(), request);
        	boolean isUploadAttFlag = com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(uploadAttFlag);
        	signOpinion.setHasAtt(isUploadAttFlag);
            /*
            this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, signOpinion.getId(), request);
            String isContentAttchmentChanged=request.getParameter("isContentAttchmentChanged");
			if("1".equals(isContentAttchmentChanged)){
	        	this.edocManager.updateAttachment(summary,affair,user,request);
        	}*/
	        //end

        	//如果当前操作执行了转PDF的操作。
        	String isConvertPdf=request.getParameter("isConvertPdf");
        	if(Strings.isNotBlank(isConvertPdf)){
        		createPdfBodies(request, summary);
        	}
        	//推送消息    affairId,memberId#affairId,memberId#affairId,memberId
		    String pushMessageMembers = request.getParameter("pushMessageMemberIds");
		    setPushMessagePara2ThreadLocal(pushMessageMembers);
		    String isDeleteSupervisior= request.getParameter("isDeleteSupervisior");
		    String ret= edocManagerFacade.finishWorkItemFacade(supervisorNames, spMemberId, superviseDate,
		    		processId, edocMangerID, summary, user, affairId, signOpinion,
		    		map, condition, isDeleteSupervisior, title);
	        PrintWriter out = response.getWriter();
	        out.println("<script>");
//	        if("pigeonhole".equals(ret))
//	        {
//	        	out.println("parent.alertPigeonhole();");
//	        }
	        if(BrowserEnum.Safari == BrowserEnum.valueOf(request)){
        		out.println("  try{parent.getA8Top().opener.getA8Top().reFlesh();}catch(e){}");
        	}
	        out.println("parent.doEndSign();");
	        out.println("</script>");

	        //--
	        return null;
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
            //解锁正文文单
        	try{
        		unLock(user.getId(),summary);
        	}catch(Exception e){
        		log.error(e);
        	}

        }
        return null;
    }

    /**
     * 联合发文暂时不支持转化为PDF正文
     * @param request
     * @param summary
     */
	private void createPdfBodies(
			HttpServletRequest request, EdocSummary summary) {
		//WORD转PDF正文
		//1、判断当前公文开关是否允许转PDF操作
		//2、获取前台参数
		//3、组装PDF BODY对象
		//4、设置进EDOCSUMMARY中，传至MANAGER层进行处理
		//boolean canConvert=EdocSwitchHelper.canEnablePdfDocChange();
		String isConvertPdf=request.getParameter("isConvertPdf");
		if(Strings.isNotBlank(isConvertPdf)){
			String pdfId=request.getParameter("newPdfIdFirst");
//			String pdfIdSecond=request.getParameter("pdfIdSecond");
			EdocBody  pdfBody=new EdocBody();
//		    EdocBody  pdfBodySend=new EdocBody();
//		    if(summary.getIsunit() && summary.getEdocBodies().size()>1){
//		    	if(summary.getBody(EdocBody.EDOC_BODY_PDF_TWO)==null){//当前公文不存在PDF正文
//		        	pdfBodySend.setIdIfNew();
//		        	pdfBodySend.setContentType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
//		        	pdfBodySend.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
//		        	pdfBodySend.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
//		        	pdfBodySend.setContentNo(EdocBody.EDOC_BODY_PDF_TWO);
//		    	}
//		    	else{//覆盖当前PDF正文。取最新的PDF正文
//		    		pdfBodySend=summary.getBody(EdocBody.EDOC_BODY_PDF_TWO);
//		    		pdfBodySend.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
//		    	}
//		    	pdfBodySend.setEdocSummary(summary);
//		    	pdfBodySend.setContent(pdfIdSecond);
//		    	summary.getEdocBodies().add(pdfBodySend);
//		    }
		   if(summary.getBody(EdocBody.EDOC_BODY_PDF_ONE)==null){//当前公文不存在PDF正文
		        pdfBody.setIdIfNew();
		        pdfBody.setContentType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
		        pdfBody.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
		        pdfBody.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
		        pdfBody.setContentNo(EdocBody.EDOC_BODY_PDF_ONE);
		   }else{//覆盖当前PDF正文。取最新的PDF正文
			   pdfBody=summary.getBody(EdocBody.EDOC_BODY_PDF_ONE);
			   pdfBody.setLastUpdate(new java.sql.Timestamp(System.currentTimeMillis()));
			}
		   	pdfBody.setContent(pdfId);
		   	pdfBody.setEdocSummary(summary);
		    summary.getEdocBodies().add(pdfBody);
		}
	}
    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView detail(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        try {
        	User user=CurrentUser.get();
        	Long summaryId = null;
        	if(!Strings.isBlank(request.getParameter("summaryId"))){
        		summaryId = Long.parseLong(request.getParameter("summaryId"));
        	}
        	Affair affair = null;
        	String _affairId = request.getParameter("affairId");
        	if(Strings.isNotBlank(_affairId)){
        		affair = affairManager.getById(Long.parseLong(_affairId));
        		//标记为已读状态。
            	try{
            		ColHelper.updateAffairStateWhenClick(affair, affairManager);
            	}catch(Exception e){log.error(e);}
        	}
        	else{
        		//取我的Affair，用于访问控制权限校验
        		List<Affair> myAffairs = affairManager.getAffairBySummaryId(ApplicationCategoryEnum.edoc, summaryId, user.getId());
        		if(myAffairs!=null && !myAffairs.isEmpty()){
        			affair = myAffairs.get(0);
        		}
        		else{
        			affair = affairManager.getCollaborationSenderAffair(summaryId);
        		}
        	}
        	if(affair == null || affair.getIsDelete() ||
            		(affair.getState() != StateEnum.col_pending.key()
                    		&& affair.getState() != StateEnum.col_sent.key()
                    		&& affair.getState() != StateEnum.col_waitSend.key()
                    		&& affair.getState() != StateEnum.col_done.key())){
                String msg=ColHelper.getErrorMsgByAffair(affair);
                throw new ColException(msg);
            }
        	if(summaryId == null){
        		summaryId=affair.getObjectId();
        	}
        	//SECURITY 访问安全检查
        	if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.edoc, user, summaryId, affair, null)){
        		return null;
        	}

            V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
            EdocSummary edocSummary = edocManager.getEdocSummaryById(affair.getObjectId(), true);
            if(edocSummary != null){
                if(member.getSecretLevel()< edocSummary.getEdocSecretLevel()){
                    throw new ColException("涉密等级不够，无法查看！");
                }
            }
            
        	String list = request.getParameter("list");
        	if(!Strings.isNotBlank(list)){
        		list="popup";
        	}

        	String subject = affair.getSubject() ;

        	//有可能公文已经处理完了，但是消息提示框仍然在哪里，所以需要重新查询一下当前事项的状态。
        	String from = request.getParameter("from");
        	if(affair.getState().equals(StateEnum.col_done.getKey()) && "Pending".equals(from)) from = "Done";
        	return new ModelAndView("edoc/edocDetail")
            .addObject("summaryId", summaryId)
            .addObject("bodyType", affair.getBodyType())
            .addObject("openFrom", request.getParameter("openFrom"))
            .addObject("controller", "edocController.do").addObject("list", list)
            .addObject("from", from).addObject("summarySubject", subject);
        }
        catch (Exception e) {
        	PrintWriter out = response.getWriter();
        	//super.printV3XJS(out);
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
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
    }


    public ModelAndView edocDetailInDoc(HttpServletRequest request,HttpServletResponse response) throws Exception
    {
    	String summaryId=request.getParameter("summaryId");
    	Affair affair  = null;
    	if(Strings.isNotBlank(summaryId)){
    		affair = affairManager.getCollaborationSenderAffair(Long.valueOf(summaryId));
    	}
    	ModelAndView mav = new ModelAndView("edoc/edocDetail");
    	mav.addObject("openFrom",request.getParameter("openFrom"));
     	mav.addObject("lenPotent",request.getParameter("lenPotent"));
     	mav.addObject("docId",request.getParameter("docId"));
     	mav.addObject("summaryId",summaryId);
     	mav.addObject("isLibOwner",request.getParameter("isLibOwner"))    	;
     	mav.addObject("controller", "edocController.do");
     	if(affair!=null){
     		mav.addObject("affairId", affair.getId());
     	}
    	return mav;
    }


//   /**
//    * 判断归档文件夹是否存在
//    * @param archiveId ：文件夹ID
//    * @return
//    */
//   public  boolean isFolderExsit(Long archiveId){
//	   if(archiveId == null ) return false ;
//	   DocResource dr=docHierarchyManager.getDocResourceById(archiveId);
//	   return dr == null ? false : true;
//   }

   public String getPhysicalPath(String logicalPath, String separator,boolean needSub1,int beginIndex){
	   return docHierarchyManager.getPhysicalPathDetail(logicalPath, separator,needSub1,beginIndex);
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
     * 获取一篇公文的全部属性
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView summary(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {

        try {
        	User user=CurrentUser.get();
        	//summary
        	ModelAndView mav = new ModelAndView("edoc/edocSummary");
        	String s_summaryId = request.getParameter("summaryId");
        	long summaryId = Long.parseLong(s_summaryId);

        	EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
            mav.addObject("summary", summary);
            mav.addObject("contentRecordId", summary.getEdocBodiesJs());
            mav.addObject("archiveName", edocManager.getShowArchiveNameByArchiveId(summary.getArchiveId()));
            mav.addObject("hasPrePighole", summary.getArchiveId() == null?false:true);
            mav.addObject("fullArchiveName", edocManager.getFullArchiveNameByArchiveId(summary.getArchiveId()));
            Metadata remindMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
            Metadata  deadlineMetadata= metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline);
            String remindLabel=remindMetadata.getItemLabel(summary.getAdvanceRemind().toString());
            String deallineLabel=deadlineMetadata.getItemLabel(summary.getDeadline().toString());
            String bounder="com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";
            mav.addObject("deallineLabel", ResourceBundleUtil.getString(bounder,deallineLabel));
            bounder="com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
            mav.addObject("remindLabel", ResourceBundleUtil.getString(bounder,remindLabel));
            if("HTML".equals(summary.getFirstBody().getContentType())){
            	mav.addObject("htmlISignCount", iSignatureHtmlManager.getISignCount(summary.getId()));
            }
            //是否允许拟文人修改附件。
            boolean allowUpdateAttachment=EdocSwitchHelper.allowUpdateAttachment();
            mav.addObject("allowUpdateAttachment",allowUpdateAttachment);
            //只查找正文的附件。
            mav.addObject("attachments", attachmentManager.getByReference(summaryId,summaryId));
            List<Attachment> attachments=attachmentManager.getByReference(summaryId,summaryId);
            String attsStr="";
            int index=0;
            for (int i=1;i<(attachments.size()+1);i++) {
    			Attachment att=attachments.get(i-1);
    			if(att.getType()==0){
    				index++;
    				String fname=att.getFilename();
    				int lastIndex=fname.length();
    				if(fname.lastIndexOf(".")!=-1){
    					lastIndex=fname.lastIndexOf(".");
    				}
    				fname=fname.substring(0, lastIndex);
    				if("".equals(attsStr)||attsStr==null){
    					attsStr=index+"."+fname;
    				}else{
    					attsStr=attsStr+"\n"+index+"."+fname;
    				}
    			}
        	}
        	summary.setAttachments(attsStr);

            mav.addObject("controller", "edocController.do");

            mav.addObject("openFrom",request.getParameter("openFrom"))
        	.addObject("lenPotent",request.getParameter("lenPotent"))
        	.addObject("docId",request.getParameter("docId"));
            try{
              	V3xOrgMember member = this.orgManager.getMemberById(user.getId());
              	if(member != null){
              		mav.addObject("extendConfig", member.getProperty("extendConfig"));
              	}
              }catch(Exception e){
              	log.error(e);
              }

            //showdial

        	String _affairId = request.getParameter("affairId");
        	Long affairId = null;
        	Affair affair =null;

            Long currentNodeOwner = user.getId();
        	try {
        		affairId = new Long(_affairId);
        		affair = affairManager.getById(affairId);
                if(affair != null && !affair.getMemberId().equals(currentNodeOwner)){
                    currentNodeOwner = affair.getMemberId();
                }
        	} catch (Exception ex) {
        	    log.error(ex.getMessage(),ex);
        	}
//        	long summaryId = Long.parseLong(_summaryId);
//        	EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);

        	//ModelAndView mav = new ModelAndView("edoc/showToolBar");

        	mav.addObject("finished", summary.getFinished());

        	String supervise = request.getParameter("supervise");
        	if(Strings.isNotBlank(supervise)){
        		mav.addObject("supervisePanel", supervise);
        	}

        	boolean hasDiagram = false;
            if(summary != null && summary.getCaseId()!= null)
            	hasDiagram = true;

            mav.addObject("hasDiagram", hasDiagram);
        	mav.addObject("hasBody1", summary.getBody(1)!=null);
        	mav.addObject("hasBody2", summary.getBody(2)!=null);
        	EdocBody pb = summary.getBody(EdocBody.EDOC_BODY_PDF_ONE);
        	mav.addObject("firstPDFId", pb!=null ? pb.getContent():"");
        	mav.addObject("processId", summary.getProcessId());
        	mav.addObject("caseId", summary.getCaseId());
        	mav.addObject("archiveFullName", edocManager.getFullArchiveNameByArchiveId(summary.getArchiveId()));
        	if(summary.getCaseId() == null){
        		StringBuilder sb=new StringBuilder();
        		sb.append("公文的CaseId为null: " );
        		if(summary!=null)	sb.append( summary.getId());
        		if(affair!=null)	sb.append(", " + affair.getId());
        		sb.append(", " + user.getName());
        		log.warn(sb.toString());
        	}
        	mav.addObject("edocType", summary.getEdocType());

        	//判断归档文件夹是否存在
        	boolean docResourceExist = docHierarchyManager.docResourceExist(summary.getArchiveId());
        	mav.addObject("isPresPigeonholeFolderExsit",docResourceExist);
        	//如果归档文件夹被删除了，设置summary为未归档。
        	if(!docResourceExist) {
        		summary.setArchiveId(null);
        		summary.setHasArchive(false);
        	}
        	mav.addObject("hasArchive", summary.getHasArchive());
        	mav.addObject("hasSetPigeonholePath", summary.getArchiveId() == null ? false :true);
        	//转发公文：新生成一个待转发的清除了痕迹的公文,产生一个这样的公文的ID。
        	mav.addObject("transmitSendNewEdocId", UUIDLong.longUUID());
        	mav.addObject("trackIds",getTrackIds(affairId));

        	String openFrom=request.getParameter("openFrom");
        	String lenPotents=request.getParameter("lenPotent");
            String lenPotent="0";
            String lenPotentPrint="0";//借阅设置的打印权限
            if(lenPotents!=null && !"".equals(lenPotents))
            {
            	lenPotent=lenPotents.substring(0,1);
            	lenPotentPrint=lenPotents.substring(2,3);
            }

            String onlySeeContent="false";
            String from = request.getParameter("from");
            if("lenPotent".equals(openFrom) && Byte.toString(com.seeyon.v3x.doc.util.Constants.LENPOTENT_CONTENT).equals(lenPotent))
            {
            	onlySeeContent="true";
            }
            mav.addObject("onlySeeContent", onlySeeContent);
            boolean affairCanPrint = false; //事项的节点权限是否包含打印操作
            boolean isSupervisor = false; //是否督办人
        	Map<String, List<String>> actions  = new HashMap<String,List<String>>();
        	if(!"WaiSend".equals(request.getParameter("from"))){ //待发不用判断权限
        		String nodePermissionPolicy = "shenpi";

        		BPMProcess process = ColHelper.getCaseProcess(summary.getProcessId());

        		BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
        		if(affair != null)
        		{
        			if("Pending".equals(request.getParameter("from")))
        			{
        				//是否当前节点的最后一个处理人在preSend中判断
    	        		/*Boolean isMatch = false;
    	        		if(null!=activity&&null!=activity.getSeeyonPolicy()&&null!=activity.getSeeyonPolicy().getId()&&null!=process&&null!=affair&&EdocHelper.isExecuteFinished(process, affair) && !"zhihui".equals(activity.getSeeyonPolicy().getId())){
    	        			isMatch = true;
    	        		}*/
    	        		EdocOpinion tempOpinion=edocManager.findBySummaryIdAndAffairId(summaryId, affair.getId());
    	        		mav.addObject("tempOpinion", tempOpinion);
    	        		if(tempOpinion!=null)
    	        		{
    	        			mav.addObject("attachmentsOpinion", attachmentManager.getByReference(summaryId,tempOpinion.getId()));
    	        		}
            			//mav.addObject("isMatch", isMatch);
            			//检查是否有公文发起权----只检查有没有发文的权利
            	        boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(EdocEnum.edocType.sendEdoc.ordinal());
            	        mav.addObject("isEdocCreateRole",isEdocCreateRole);
        			}

        			if(activity != null){ //发起者不需要
        				String currentNodeId = activity.getId();
        				mav.addObject("currentNodeId", currentNodeId);
        			}

        			//催办按钮只有在已发事项中才能显示

    	            if(affair.getSubObjectId() == null
    	            		&& affair.getState()==StateEnum.col_sent.getKey()
    	            		&& user.getId() == affair.getSenderId())
        				mav.addObject("showHastenButton", "true");


    	            //得到当前处理权限录入意见的显示位置
    	            if(activity != null){
    	            	nodePermissionPolicy = activity.getSeeyonPolicy().getId();
    	            }
        			String disPosition=edocFormManager.getOpinionLocation(summary.getFormId(),EdocHelper.getFlowPermAccountId(summary, user.getLoginAccount(), templeteManager)).get(nodePermissionPolicy);
        			if(disPosition==null){disPosition=nodePermissionPolicy;}
        			else{
        				String[] dis = disPosition.split("[_]");
        				disPosition = dis[0];
        			}
        			mav.addObject("disPosition", disPosition);
        			mav.addObject("affair", affair);
        		}
        		MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnum(summary.getEdocType());
        		MetadataItem tempMitem=null;
//        		获取节点权限，如果是自由流程取发起人所在单位的节点权限，模板取模板所在单位节点权限
        		//Long accountId=orgManager.getMemberById(summary.getStartUserId()).getOrgAccountId();
        		Long senderId = summary.getStartUserId();
    	        V3xOrgMember sender = orgManager.getMemberById(senderId);

    	        Long flowPermAccountId = EdocHelper.getFlowPermAccountId(summary, sender.getOrgAccountId(), templeteManager);
    	        FlowPerm fpm = new FlowPerm();
        		try{
        			fpm=flowPermManager.getFlowPerm(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);

        			if(null!=fpm){
        				mav.addObject("opinionPolicy", fpm.getNodePolicy().getOpinionPolicy());
        				mav.addObject("attitudes", fpm.getNodePolicy().getAttitude());
        				if(fpm.getType()==com.seeyon.v3x.flowperm.util.Constants.F_type_system){
        					tempMitem=metadataManager.getMetadataItem(edocTypeEnum, nodePermissionPolicy);
        				}
        			}

        		}catch(Exception e)
        		{
        			log.error("",e);
        		}
        		mav.addObject("nodePermissionPolicy", tempMitem);

        		mav.addObject("nodePermissionPolicyKey",nodePermissionPolicy);


                Hashtable<String, String> elementBoundFlowPerm = edocFormManager.getOpinionLocation(summary.getFormId(), flowPermAccountId);
                String element = elementBoundFlowPerm.get(nodePermissionPolicy);
                mav.addObject("position",Strings.isBlank(element)?"":element.substring(0, element.indexOf("_")));

                actions = permissionManager.getActionMap(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);
        		List<String> baseActions = actions.get("basic");
        		if(baseActions == null){
        			log.error("edocController.summary : baseActions is null! [edocTypeEnum.name():"+edocTypeEnum.name()+" nodePermissionPolicy:"+nodePermissionPolicy +" flowPermAccountId:"+flowPermAccountId+" summary.id:"+summary.getId());
        		}
        		//检查归档策略，归档只能在封发节点出现，流程结束后才可以归档
        		//baseActions=EdocHelper.checkPerm(baseActions,nodePermissionPolicy);
    	        List<String> advancedActions = actions.get("advanced");
    	        List<String> commonActions = actions.get("common");

    			// 公文所属单位的公文收发员
    			List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
    			Set<Long> deptSenderList=new HashSet<Long>();
    			if (("fengfa".equals(nodePermissionPolicy))
    					|| (baseActions!=null && baseActions.contains("EdocExchangeType"))) {
    				// 封发节点或者是具有【交换类型】节点权限
    				memberList = EdocRoleHelper.getAccountExchangeUsers(summary
    						.getOrgAccountId());
    				
    				for(int i = memberList.size()-1; i >= 0; i--){
    					if(memberList.get(i).getSecretLevel() < summary.getEdocSecretLevel()){
    						memberList.remove(i);
    					}
    				}
    				//获取当前用户登录单位副岗部门的部门公文收发员
    				deptSenderList=getDeptSenderList(summary.getStartUserId(),summary.getOrgAccountId());
    				Iterator<Long> iterator = deptSenderList.iterator();
    				Long deptId;
    				String deptList="";
    				while(iterator.hasNext()){
    					deptId=(Long)iterator.next();
    					V3xOrgDepartment dept=orgManager.getDepartmentById(deptId);
    					if(dept!=null){
    						if(deptList.equals("")){
    							deptList+=dept.getId()+","+dept.getName();
    						}else{
    							deptList+="|"+dept.getId()+","+dept.getName();
    						}
    					}
    				}
    				mav.addObject("memberList", memberList);
    				mav.addObject("deptSenderList", deptList);
    			}

    	        if(baseActions!=null && baseActions.contains("Print")) affairCanPrint = true;


    	        //判断是否能进行归档操作：
    	        boolean canArchive = false;
    	        if(baseActions!=null && baseActions.contains("Archive") && !summary.getHasArchive()){  //封发及自定义节点可以进行归档操作
//    	        	if(summary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()){
//    			        if("fengfa".equals(nodePermissionPolicy)) canArchive = true;
//    			        if(fpm.getType()!=com.seeyon.v3x.flowperm.util.Constants.F_type_system) canArchive = true;
//    	        	}else{
    	        		 canArchive = true;
    	        	//}
    	        }
    	        mav.addObject("canArchive", canArchive);
    	        mav.addObject("baseActions", baseActions);
    	        mav.addObject("advancedActions", advancedActions);
    	        mav.addObject("commonActions", commonActions);
        	}

        	Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        	Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
            Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
            colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
            Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
            colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);

        	mav.addObject("comMetadata", comMetadata);
        	mav.addObject("colMetadata", colMetadata);
        	mav.addObject("summary", summary);
        	mav.addObject("isShowButton", false);

        	mav.addObject("appTypeName",EdocEnum.getEdocAppName(summary.getEdocType()));

        	mav.addObject("curUser", CurrentUser.get());

        	//得到公文发起人
        	long edocSendUserId=summary.getStartUserId();
        	V3xOrgMember edocSendMember=orgManager.getMemberById(edocSendUserId);
        	mav.addObject("edocSendMember", edocSendMember);

        	mav.addObject("controller", "edocController.do");

        	// 督办开始
        	Set<String> idSets = new HashSet<String>();
    		StringBuffer supervisorId = new StringBuffer(); // supervisorId : all the ids of supervise detail
    		StringBuffer tempIds = new StringBuffer(); // tempIds : all the ids of superviseTemplate
    		ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(Constant.superviseType.edoc.ordinal(), summaryId);
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
    					V3xOrgMember starter = orgManager.getMemberById(summary.getStartUserId());
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
    				mav.addObject("supervisorId", supervisorId.substring(0, supervisorId.length()-1));
    			}
    			mav.addObject("superviseId", detail.getId());
    			mav.addObject("supervisors", detail.getSupervisors());
    			mav.addObject("superviseTitle", detail.getTitle());
    			mav.addObject("awakeDate", Datetimes.format(detail.getAwakeDate(), Datetimes.datetimeWithoutSecondStyle));
    			mav.addObject("superviseTitle", detail.getTitle());
    			mav.addObject("count", idSets.size());
    			if(idSets.contains(String.valueOf(user.getId()))){
    				mav.addObject("isSupervis", true);
    				isSupervisor = true;
    			}

            	mav.addObject("openModal", request.getParameter("openModal"));
            	mav.addObject("bean", detail);
    			if(tempIds.length()>0){
    				String unCancelledVisor = tempIds.substring(0, tempIds.length()-1);
    				mav.addObject("unCancelledVisor", unCancelledVisor);
    				mav.addObject("sVisorsFromTemplate", "true");//公文调用的督办模板是否设置了督办人
    			}
    		}


        	boolean templateFlag = false;
        	if(summary.getTempleteId() != null) templateFlag = true;

        	//分支 开始
            if(summary.getTempleteId()!=null) {
            	List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(summary.getTempleteId(),ApplicationCategoryEnum.edoc.ordinal());
            	if(branchs != null) {
                    //显示分支条件使用流程中保留的，如果为空使用模板中的
            		branchs = EdocHelper.updateBranchByProcess(summary.getProcessId(),branchs);
            		mav.addObject("branchs", branchs);
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
            		mem = orgManager.getMemberById(summary.getStartUserId());
            		mav.addObject("startTeams", this.orgManager.getUserDomain(mem.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM));
            		mav.addObject("startSecondPosts", mem.getSecond_post());
                }

            	templateFlag = true;
            }
        	//分支 结束
            //文单打印权限的控制 -------------
            /**关于openFrom：从公文档案库传递过来的时候值为edocDocLib,普通文档库（包括共享）为docLib,借阅的时候为lenPotent
             * 从文档相关消息框弹出来的时候为"null"，暂时只发现这几个值，但是每种情况都会传递参数lenPotent，所以这里判断文单打印权限的时候使用lenPotent来判断*/
            String printEdocTable="";
			if (Strings.isNotBlank(lenPotents)) { // 文档库相关
				printEdocTable = "0".equals(lenPotentPrint) ? "false" : "true";
			} else if ("true".equals(request.getParameter("isLibOwner"))) { // 文档库相关
				printEdocTable = "true";
            }else if("glwd".equals(openFrom)){//从关联文档点击进来的，都不可以打印
            	printEdocTable="false";
            }else if("sended".equals(from)
            		|| "WaitSend".equals(from)
            		|| "Sent".equals(from)) { //已发送和保存待发,首页跟踪过来的已发中设置的from是"Sent"
            	printEdocTable="true";
            }else if(isSupervisor){//督办节点都能打印
            	printEdocTable = "true";
            }else {
            	printEdocTable = affairCanPrint ? "true" : "false";
            }
            mav.addObject("printEdocTable", printEdocTable);
            //-----


        	Map<String,List<String>> map = parse2WebActionList(actions, "true".equals(printEdocTable)?true:false);
            mav.addObject("toolBarActions", map.get("toolBar"));
            mav.addObject("moreActions", map.get("more") );
        	mav.addObject("affairId",_affairId)  ;
        	mav.addObject("templateFlag", templateFlag);
        	mav.addObject("from", request.getParameter("from"));
        	mav.addObject("newPdfIdFirst", UUIDLong.longUUID());
        	mav.addObject("newPdfIdSecond", UUIDLong.longUUID());
        	int def = EdocSwitchHelper.getDefaultExchangeType(summary.getOrgAccountId());
        	mav.addObject("isDefaultExchangeTypeOrg", def == com.seeyon.v3x.exchange.util.Constants.C_iExchangeType_Org);
        	mav.addObject("isDefaultExchangeTypeDept", def == com.seeyon.v3x.exchange.util.Constants.C_iExchangeType_Dept);
        	return mav;
        }
        catch (Exception e) {
        	log.error("",e);
        	PrintWriter out = response.getWriter();
        	//super.printV3XJS(out);
        	out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(e.getMessage()) + "\")");
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

    }
    private Set<Long> getDeptSenderList(long edocStartUserId,long edocOrgAccountId) {
    	//使用Set过滤重复的人员,如果一个人既在主部门充当收发员，又在副岗所在部门充当收发员，首页只显示一条待办事项。
		Set<Long> deptList=new HashSet<Long>();
		try{
			//发起人在主单位和兼职单位获取公文收发员的方式有所不同。
			V3xOrgMember startMember=orgManager.getMemberById(edocStartUserId);
			//1、查找兼职部门的公文收发员
			Map<Long, List<ConcurrentPost>> map=orgManager.getConcurentPostsByMemberId(edocOrgAccountId,edocStartUserId);

			Set<Long> concurentDepartSet=map.keySet();
			for(Long deptId:concurentDepartSet){
				V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
				if(dept.getOrgAccountId().equals(edocOrgAccountId)){
					deptList.add(deptId);
				}
			}
			//2.查找发起人副岗所在部门的公文收发员
			List<MemberPost> list=startMember.getSecond_post();
			for(MemberPost memberPost:list){
				if(memberPost.getOrgAccountId().equals(edocOrgAccountId)){
					deptList.add(memberPost.getDepId());
				}
			}
			//3。主部门的公文收发员。
			if(edocOrgAccountId==startMember.getOrgAccountId()){
				deptList.add(startMember.getOrgDepartmentId());
			}
		}catch(Exception e){
			log.error("公文交换查找部门收发员出错：", e);
		}
		return deptList;
	}
    public ModelAndView superviseDiagram(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("edoc/edocDiagramSupervise");
        String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);
        ColSuperviseDetail detail = edocSuperviseManager.getSuperviseBySummaryId(summaryId);
        if(detail!=null){
        	boolean isSupervis = false;
        	String supervisors= detail.getSupervisors();
        	String []supervisorsArry = supervisors.split("、");
        	for (String str : supervisorsArry) {
				if(str.equals(CurrentUser.get().getName())){
            		isSupervis=true;
            		break;
				}
			}

        	EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
        	modelAndView.addObject("finished", summary.getFinished());
            modelAndView.addObject("isSupervis", isSupervis);
        	modelAndView.addObject("bean", detail);
        }
        modelAndView.addObject("summaryId", s_summaryId);
    	return modelAndView;
    }

    private Map<String, List<String>>  parse2WebActionList(Map<String, List<String>> actions,boolean isEdocFormCanPrint){

        List<String> advancedActions = actions.get("advanced");
        List<String> commonActions = actions.get("common");
        Map<String, List<String>> map =new HashMap<String, List<String>>() ;
        int toolBarShowSize = 8;//页面工具条显示的按钮数
//        同意排序，工具条显示8个，其余的显示到高级操作中
//        List<String> all = new ArrayList<String>();
//        if(commonActions!=null) all.addAll(commonActions);
//        if(advancedActions!=null)  all.addAll(advancedActions);
//        all.add("edocFormPrint");
//        all.add("logDetail");
//        int allSize = all.size();
//        map.put("toolBar", allSize>=toolBarShowSize ? all.subList(0,toolBarShowSize):all.subList(0,allSize));
//        map.put("more", all.size()>toolBarShowSize ? all.subList(toolBarShowSize,allSize):null);
//
        List<String> more = new ArrayList<String>();
        if(commonActions != null){
	        if(commonActions.size()<=toolBarShowSize){
	        	map.put("toolBar", commonActions);
	        }else{
	        	map.put("toolBar", commonActions.subList(0, toolBarShowSize));
	        	more.addAll(commonActions.subList(toolBarShowSize,commonActions.size()));
	        }
        }else{
        	map.put("toolBar", null);
        }

        if(advancedActions != null) more.addAll(advancedActions);

        more.add("edocFormPrint");
        more.add("logDetail");

        map.put("more", more);
        return map;
    }

//   public ModelAndView showDiagram(HttpServletRequest request,HttpServletResponse response) throws Exception
//    {
//    	User user = CurrentUser.get();
//    	String _summaryId = request.getParameter("summaryId");
//    	String _affairId = request.getParameter("affairId");
//    	Long affairId = null;
//    	Affair affair =null;
//
//        Long currentNodeOwner = user.getId();
//    	try {
//    		affairId = new Long(_affairId);
//    		affair = affairManager.getById(affairId);
//            if(affair != null && !affair.getMemberId().equals(currentNodeOwner)){
//                currentNodeOwner = affair.getMemberId();
//            }
//    	} catch (Exception ex) {}
//    	long summaryId = Long.parseLong(_summaryId);
//    	EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
//
//    	ModelAndView mav = new ModelAndView("edoc/showDiagram");
//
//    	mav.addObject("finished", summary.getFinished());
//
//    	String supervise = request.getParameter("supervise");
//    	if(Strings.isNotBlank(supervise)){
//    		mav.addObject("supervisePanel", supervise);
//    	}
//
//    	boolean hasDiagram = false;
//        if(summary != null && summary.getCaseId()!= null)
//        	hasDiagram = true;
//
//        mav.addObject("hasDiagram", hasDiagram);
//    	mav.addObject("hasBody1", summary.getBody(1)!=null);
//    	mav.addObject("hasBody2", summary.getBody(2)!=null);
//    	mav.addObject("processId", summary.getProcessId());
//    	mav.addObject("caseId", summary.getCaseId());
//    	mav.addObject("archiveFullName", edocManager.getFullArchiveNameByArchiveId(summary.getArchiveId()));
//    	if(summary.getCaseId() == null){
//    		StringBuilder sb=new StringBuilder();
//    		sb.append("公文的CaseId为null: " );
//    		if(summary!=null)	sb.append( summary.getId());
//    		if(affair!=null)	sb.append(", " + affair.getId());
//    		sb.append(", " + user.getName());
//    		log.warn(sb.toString());
//    	}
//    	mav.addObject("edocType", summary.getEdocType());
//    	mav.addObject("hasArchive", summary.getHasArchive());
//    	mav.addObject("isPresPigeonholeFolderExsit",docHierarchyManager.docResourceExist(summary.getArchiveId()));
//    	mav.addObject("hasSetPigeonholePath", summary.getArchiveId() == null ? false :true);
//    	if(summary.getArchiveId() != null && !docHierarchyManager.docResourceExist(summary.getArchiveId())) summary.setArchiveId(null);
//    	//    	转发公文：新生成一个待转发的清除了痕迹的公文,产生一个这样的公文的ID。
//    	mav.addObject("transmitSendNewEdocId", UUIDLong.longUUID());
//    	String openFrom=request.getParameter("openFrom");
//    	String lenPotents=request.getParameter("lenPotent");
//        String lenPotent="0";
//        String lenPotentPrint="0";//借阅设置的打印权限
//        if(lenPotents!=null && !"".equals(lenPotents))
//        {
//        	lenPotent=lenPotents.substring(0,1);
//        	lenPotentPrint=lenPotents.substring(2,3);
//        }
//
//        String onlySeeContent="false";
//        String from = request.getParameter("from");
//        if("lenPotent".equals(openFrom) && Byte.toString(com.seeyon.v3x.doc.util.Constants.LENPOTENT_CONTENT).equals(lenPotent))
//        {
//        	onlySeeContent="true";
//        }
//        mav.addObject("onlySeeContent", onlySeeContent);
//        boolean affairCanPrint = false; //事项的节点权限是否包含打印操作
//        boolean isSupervisor = false; //是否督办人
//    	if(!"WaiSend".equals(request.getParameter("from"))){ //待发不用判断权限
//    		String nodePermissionPolicy = "shenpi";
//
//    		BPMProcess process = ColHelper.getCaseProcess(summary.getProcessId());
//
//    		BPMActivity activity = ColHelper.getBPMActivityByAffair(process, affair);
//    		if(affairId != null)
//    		{
//    			if("Pending".equals(request.getParameter("from")))
//    			{
//    				//是否当前节点的最后一个处理人在preSend中判断
//	        		/*Boolean isMatch = false;
//	        		if(null!=activity&&null!=activity.getSeeyonPolicy()&&null!=activity.getSeeyonPolicy().getId()&&null!=process&&null!=affair&&EdocHelper.isExecuteFinished(process, affair) && !"zhihui".equals(activity.getSeeyonPolicy().getId())){
//	        			isMatch = true;
//	        		}*/
//	        		EdocOpinion tempOpinion=edocManager.findBySummaryIdAndAffairId(summaryId, affairId);
//	        		mav.addObject("tempOpinion", tempOpinion);
//	        		if(tempOpinion!=null)
//	        		{
//	        			mav.addObject("attachments", attachmentManager.getByReference(summaryId,tempOpinion.getId()));
//	        		}
//        			//mav.addObject("isMatch", isMatch);
//        			//检查是否有公文发起权----只检查有没有发文的权利
//        	        boolean isEdocCreateRole=EdocRoleHelper.isEdocCreateRole(EdocEnum.edocType.sendEdoc.ordinal());
//        	        mav.addObject("isEdocCreateRole",isEdocCreateRole);
//    			}
//
//    			if(activity != null){ //发起者不需要
//    				String currentNodeId = activity.getId();
//    				mav.addObject("currentNodeId", currentNodeId);
//    			}
//
//    			//催办按钮只有在已发事项中才能显示
//
//	            if(affair.getSubObjectId() == null
//	            		&& affair.getState()==StateEnum.col_sent.getKey()
//	            		&& user.getId() == affair.getSenderId())
//    				mav.addObject("showHastenButton", "true");
//
//
//	            //得到当前处理权限录入意见的显示位置
//	            if(activity != null){
//	            	nodePermissionPolicy = activity.getSeeyonPolicy().getId();
//	            }
//    			String disPosition=edocFormManager.getOpinionLocation(summary.getFormId(),EdocHelper.getFlowPermAccountId(summary, user.getLoginAccount(), templeteManager)).get(nodePermissionPolicy);
//    			if(disPosition==null){disPosition=nodePermissionPolicy;}
//    			mav.addObject("disPosition", disPosition);
//    			mav.addObject("affair", affair);
//    		}
//    		MetadataNameEnum edocTypeEnum=EdocUtil.getEdocMetadataNameEnum(summary.getEdocType());
//    		MetadataItem tempMitem=null;
////    		获取节点权限，如果是自由流程取发起人所在单位的节点权限，模板取模板所在单位节点权限
//    		//Long accountId=orgManager.getMemberById(summary.getStartUserId()).getOrgAccountId();
//    		Long senderId = summary.getStartUserId();
//	        V3xOrgMember sender = orgManager.getMemberById(senderId);
//
//	        Long flowPermAccountId = EdocHelper.getFlowPermAccountId(summary, sender.getOrgAccountId(), templeteManager);
//	        FlowPerm fpm = new FlowPerm();
//    		try{
//    			fpm=flowPermManager.getFlowPerm(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);
//
//    			if(null!=fpm){
//    				mav.addObject("opinionPolicy", fpm.getNodePolicy().getOpinionPolicy());
//    				mav.addObject("attitudes", fpm.getNodePolicy().getAttitude());
//    				if(fpm.getType()==com.seeyon.v3x.flowperm.util.Constants.F_type_system){
//    					tempMitem=metadataManager.getMetadataItem(edocTypeEnum, nodePermissionPolicy);
//    				}
//    			}
//
//    		}catch(Exception e)
//    		{
//    			log.error("",e);
//    		}
//    		mav.addObject("nodePermissionPolicy", tempMitem);
//
//    		mav.addObject("nodePermissionPolicyKey",nodePermissionPolicy);
//
//    		Map<String, List<String>> actions = permissionManager.getActionMap(edocTypeEnum.name(), nodePermissionPolicy, flowPermAccountId);
//    		List<String> baseActions = actions.get("basic");
//    		//检查归档策略，归档只能在封发节点出现，流程结束后才可以归档
//    		//baseActions=EdocHelper.checkPerm(baseActions,nodePermissionPolicy);
//	        List<String> advancedActions = actions.get("advanced");
//	        List<String> commonActions = actions.get("common");
//
//			// 公文所属单位的公文收发员
//			List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
//			if (("fengfa".equals(nodePermissionPolicy))
//					|| (baseActions.contains("EdocExchangeType"))) {
//				// 封发节点或者是具有【交换类型】节点权限
//				memberList = EdocRoleHelper.getAccountExchangeUsers(summary
//						.getOrgAccountId());
//				mav.addObject("memberList", memberList);
//			}
//
//	        if(baseActions.contains("Print")) affairCanPrint = true;
//
//
//	        //判断是否能进行归档操作：
//	        boolean canArchive = false;
//	        if(baseActions.contains("Archive") && !summary.getHasArchive()){  //封发及自定义节点可以进行归档操作
////	        	if(summary.getEdocType() == EdocEnum.edocType.sendEdoc.ordinal()){
////			        if("fengfa".equals(nodePermissionPolicy)) canArchive = true;
////			        if(fpm.getType()!=com.seeyon.v3x.flowperm.util.Constants.F_type_system) canArchive = true;
////	        	}else{
//	        		 canArchive = true;
//	        	//}
//	        }
//	        mav.addObject("canArchive", canArchive);
//	        mav.addObject("baseActions", baseActions);
//	        mav.addObject("advancedActions", advancedActions);
//	        mav.addObject("commonActions", commonActions);
//    	}
//
//    	Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
//    	Metadata comMetadata = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
//        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
//        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
//        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
//        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);
//
//    	mav.addObject("comMetadata", comMetadata);
//    	mav.addObject("colMetadata", colMetadata);
//    	mav.addObject("summary", summary);
//    	mav.addObject("isShowButton", false);
//
//    	mav.addObject("appTypeName",EdocEnum.getEdocAppName(summary.getEdocType()));
//
//    	mav.addObject("curUser", CurrentUser.get());
//
//    	//得到公文发起人
//    	long edocSendUserId=summary.getStartUserId();
//    	V3xOrgMember edocSendMember=orgManager.getMemberById(edocSendUserId);
//    	mav.addObject("edocSendMember", edocSendMember);
//
//    	mav.addObject("controller", "edocController.do");
//
//    	// 督办开始
//    	Set<String> idSets = new HashSet<String>();
//		StringBuffer supervisorId = new StringBuffer(); // supervisorId : all the ids of supervise detail
//		StringBuffer tempIds = new StringBuffer(); // tempIds : all the ids of superviseTemplate
//		ColSuperviseDetail detail = this.colSuperviseManager.getSupervise(Constant.superviseType.edoc.ordinal(), summaryId);
//		if(detail != null) {
//			Set<ColSupervisor> supervisors = detail.getColSupervisors();
//			Set<Long> userSuper = new HashSet<Long>();
//			for(ColSupervisor supervisor:supervisors){
//				userSuper.add(supervisor.getSupervisorId().longValue());
//				idSets.add(supervisor.getSupervisorId().toString());
//			}
//			if(null!=summary && null!=summary.getTempleteId()){
//				ColSuperviseDetail tempDetail = colSuperviseManager.getSupervise(Constant.superviseType.template.ordinal(),summary.getTempleteId());
//				if(null!=tempDetail){
//					Set<ColSupervisor> tempVisors = tempDetail.getColSupervisors();
//					for(ColSupervisor ts : tempVisors){
//						if(userSuper.contains(ts.getSupervisorId())){
//							idSets.add(ts.getSupervisorId().toString());
//							tempIds.append(ts.getSupervisorId().toString() + ",");
//						}
//					}
//
//					List<SuperviseTemplateRole> roleList = colSuperviseManager.findSuperviseRoleByTemplateId(summary.getTempleteId());
//					V3xOrgRole orgRole = null;
//					V3xOrgMember starter = orgManager.getMemberById(summary.getStartUserId());
//					if(null!=starter){
//					for (SuperviseTemplateRole role : roleList) {
//						if (null == role.getRole() || "".equals(role.getRole())) {
//							continue;
//						}
//						if (role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase())) {
//							if(userSuper.contains(starter.getId())){
//								idSets.add(String.valueOf(starter.getId()));
//								tempIds.append(starter.getId() + ",");
//							}
//						}
//						if (role.getRole().toLowerCase().equals(
//										V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER.toLowerCase())) {
//							orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER, starter.getOrgAccountId());
//							if (null != orgRole) {
//								List<V3xOrgDepartment> depList = orgManager
//										.getDepartmentsByUser(starter.getId());
//								for (V3xOrgDepartment dep : depList) {
//									List<V3xOrgMember> managerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
//									for (V3xOrgMember mem : managerList) {
//										if(userSuper.contains(mem.getId())){
//											idSets.add(mem.getId().toString());
//											tempIds.append(mem.getId() + ",");
//										}
//									}
//								}
//							}
//						}
//						if (role.getRole().toLowerCase().equals(V3xOrgEntity.ORGENT_META_KEY_SEDNER.toLowerCase() + V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER.toLowerCase())) {
//							orgRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_SUPERDEPMANAGER, starter.getOrgAccountId());
//							if (null != orgRole) {
//								List<V3xOrgDepartment> depList = orgManager.getDepartmentsByUser(starter.getId());
//								for (V3xOrgDepartment dep : depList) {
//									List<V3xOrgMember> superManagerList = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, dep.getId(), orgRole.getId());
//									for (V3xOrgMember mem : superManagerList) {
//										if(userSuper.contains(mem.getId())){
//											idSets.add(mem.getId().toString());
//											tempIds.append(mem.getId() + ",");
//										}
//									}
//								}
//							}
//						}
//					}
//
//				}
//				}
//			}
//
//			for(String id : idSets){
//				supervisorId.append(id+",");
//			}
//			if(supervisorId.length()>0){
//				mav.addObject("supervisorId", supervisorId.substring(0, supervisorId.length()-1));
//			}
//			mav.addObject("superviseId", detail.getId());
//			mav.addObject("supervisors", detail.getSupervisors());
//			mav.addObject("superviseTitle", detail.getTitle());
//			mav.addObject("awakeDate", Datetimes.format(detail.getAwakeDate(), Datetimes.dateStyle));
//			mav.addObject("superviseTitle", detail.getTitle());
//			mav.addObject("count", idSets.size());
//			if(idSets.contains(String.valueOf(user.getId()))){
//				mav.addObject("isSupervis", true);
//				isSupervisor = true;
//			}
//
//        	mav.addObject("openModal", request.getParameter("openModal"));
//        	mav.addObject("bean", detail);
//			if(tempIds.length()>0){
//				String unCancelledVisor = tempIds.substring(0, tempIds.length()-1);
//				mav.addObject("unCancelledVisor", unCancelledVisor);
//				mav.addObject("sVisorsFromTemplate", "true");//公文调用的督办模板是否设置了督办人
//			}
//		}
//
//
//    	boolean templateFlag = false;
//    	if(summary.getTempleteId() != null) templateFlag = true;
//
//    	//分支 开始
//        if(summary.getTempleteId()!=null) {
//        	List<ColBranch> branchs = this.templeteManager.getBranchsByTemplateId(summary.getTempleteId(),ApplicationCategoryEnum.edoc.ordinal());
//        	mav.addObject("branchs", branchs);
//        	if(branchs != null) {
//                //显示分支条件使用流程中保留的，如果为空使用模板中的
//        		EdocHelper.updateBranchByProcess(summary.getProcessId(),branchs);
//        		List<V3xOrgEntity> entities = this.orgManager.getUserDomain(currentNodeOwner, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM);
//        		if(entities != null && !entities.isEmpty()){
//        			V3xOrgTeam team = null;
//        			List<V3xOrgTeam> teams = new ArrayList<V3xOrgTeam>();
//        			for(V3xOrgEntity entity:entities){
//        				team = (V3xOrgTeam) entity;
//        				if(team != null && team.getAllMembers() != null && team.getAllMembers().contains(currentNodeOwner)){
//        					teams.add(team);
//        				}
//        			}
//        			mav.addObject("teams", teams);
//        		}
//        		V3xOrgMember mem = orgManager.getMemberById(currentNodeOwner);
//        		List<MemberPost> secondPosts = mem.getSecond_post();
//        		mav.addObject("secondPosts", secondPosts);
//        		mem = orgManager.getMemberById(summary.getStartUserId());
//        		mav.addObject("startTeams", this.orgManager.getUserDomain(mem.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_TEAM));
//        		mav.addObject("startSecondPosts", mem.getSecond_post());
//            }
//
//        	templateFlag = true;
//        }
//    	//分支 结束
//        //文单打印权限的控制 -------------
//        /**关于openFrom：从公文档案库传递过来的时候值为edocDocLib,普通文档库（包括共享）为docLib,借阅的时候为lenPotent
//         * 从文档相关消息框弹出来的时候为"null"，暂时只发现这几个值，但是每种情况都会传递参数lenPotent，所以这里判断文单打印权限的时候使用lenPotent来判断*/
//        String printEdocTable="";
//        if("true".equals(request.getParameter("isLibOwner"))){  //文档库相关
//        	printEdocTable="true";
//        }else if(Strings.isNotBlank(lenPotents)){
//        	printEdocTable = "0".equals(lenPotentPrint) ? "false" : "true";
//        }else if("glwd".equals(openFrom)){//从关联文档点击进来的，都不可以打印
//        	printEdocTable="false";
//        }else if("sended".equals(from) || "WaitSend".equals(from)) { //已发送和保存待发
//        	printEdocTable="true";
//        }else if(isSupervisor){//督办节点都能打印
//        	printEdocTable = "true";
//        }else {
//        	printEdocTable = affairCanPrint ? "true" : "false";
//        }
//        mav.addObject("printEdocTable", printEdocTable);
//        //-----
//
//        mav.addObject("affairId",_affairId)  ;
//    	mav.addObject("templateFlag", templateFlag);
//    	mav.addObject("from", request.getParameter("from"));
//    	return mav;
//    }
    public ModelAndView wendanTaohong(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	ModelAndView mv = new ModelAndView("edoc/wendantaohong");
    	String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);
        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
        mv.addObject("summary", summary);
        mv.addObject("body", summary.getFirstBody());
        String tempContentType = request.getParameter("tempContentType");
        mv.addObject("tempContentType", tempContentType);
    	return mv;
    }
    public ModelAndView wendanTaohongIframe(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	ModelAndView mv = new ModelAndView("edoc/wendantaohongIframe");
    	String s_summaryId = request.getParameter("summaryId");
        mv.addObject("summaryId", s_summaryId);
        String tempContentType = request.getParameter("tempContentType");
        mv.addObject("tempContentType", tempContentType);
    	return mv;
    }
    public ModelAndView getContent2(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("edoc/edocTopic");
    	String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);

        Affair affair = affairManager.getCollaborationSenderAffair(summaryId);
        //SECURITY 访问安全检查
    	if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.edoc, CurrentUser.get(), summaryId, affair, null)){
    		return null;
    	}

    	String openFrom=request.getParameter("openFrom");
    	String lenPotents=request.getParameter("lenPotent");
        String lenPotent="0";
        if(lenPotents!=null && !"".equals(lenPotents)){lenPotent=lenPotents.substring(0,1);}

        String officecanPrint="true";
        String officecanSaveLocal="true";
        String onlySeeContent="false";

        if(lenPotents!=null && !"".equals(lenPotents)){officecanSaveLocal="0".equals(lenPotents.substring(1,2))?"false":"true";}
        if(lenPotents!=null && !"".equals(lenPotents)){officecanPrint="0".equals(lenPotents.substring(2,3))?"false":"true";}

        if(Byte.toString(com.seeyon.v3x.doc.util.Constants.LENPOTENT_CONTENT).equals(lenPotent))
        {
        	onlySeeContent="true";
        }
        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
        modelAndView.addObject("summary", summary);

        modelAndView.addObject("body", summary.getFirstBody());

        EdocFormModel fm=new EdocFormModel();
        fm.setEdocSummary(summary);
        fm.setEdocFormId(summary.getFormId());
        fm.setEdocBody(summary.getFirstBody());
        modelAndView.addObject("formModel",fm);

        modelAndView.addObject("controller", "edocController.do");
        modelAndView.addObject("contentRecordId", summary.getEdocBodiesJs());

        //记录操作日志
        Long docResId=Long.parseLong(request.getParameter("docId"));
       /**
        operationlogManager.insertOplog(docResId, summaryId,
				ApplicationCategoryEnum.doc, ActionType.LOG_DOC_VIEW, ActionType.LOG_DOC_VIEW+".desc",
				CurrentUser.get().getName(), summary.getSubject());
        **/
        if("glwd".equals(openFrom))//从关联文档点击进来的，都不可以打印
        {
        	officecanPrint="false";
        }

        modelAndView.addObject("docId",docResId);
        modelAndView.addObject("officecanPrint",officecanPrint);
    	modelAndView.addObject("officecanSaveLocal",officecanSaveLocal);
    	modelAndView.addObject("onlySeeContent",onlySeeContent);
    	modelAndView.addObject("openFrom",openFrom);

    	return modelAndView;
    }


    /**
     * 显示公文内容和处理信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView getContent(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView(
                "edoc/edocTopic");

        String openFrom=request.getParameter("openFrom");
        String lenPotents=request.getParameter("lenPotent");
        String lenPotent="0";
        if(lenPotents!=null && !"".equals(lenPotents)){lenPotent=lenPotents.substring(0,1);}
        String officecanPrint="true";
        String officecanSaveLocal="true";

        boolean onlySeeContent=false;

        if("lenPotent".equals(openFrom) && Byte.toString(com.seeyon.v3x.doc.util.Constants.LENPOTENT_CONTENT).equals(lenPotent))
        {
        	onlySeeContent=true;
        	return getContent2(request,response);
        }

        String s_summaryId = request.getParameter("summaryId");
        long summaryId = Long.parseLong(s_summaryId);

        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, true);
        modelAndView.addObject("summary", summary);

        modelAndView.addObject("body", summary.getFirstBody());

        User user=CurrentUser.get();
        String openFromAffair="";

        long userId = user.getId();
        //根据affairId得到权限的处理ID
        long permId=-1;
        String _affairId = request.getParameter("affairId");
        Affair affair = null;
        String nodePermissionPolicy ="";
        if(Strings.isNotBlank(_affairId))
        {
        	Long affairId = null;
        	try {
        		affairId = new Long(_affairId);
        	} catch (Exception ex) {
        		log.error(ex);
        	}
        	affair=affairManager.getById(affairId);
        	if(affair.getState()==StateEnum.col_waitSend.getKey() || affair.getState()==StateEnum.col_sent.getKey())
        	{
        		openFromAffair="waitsendOrSended";
        	}

        	nodePermissionPolicy = edocManager.getPolicyByAffair(affair);
        	String catType= EdocUtil.getEdocMetadataNameEnum(summary.getEdocType()).name();
        	//flash bug add adjust field
        	if("collaboration".equalsIgnoreCase(nodePermissionPolicy))
        	{
        		nodePermissionPolicy="niwen";
        		if(summary.getEdocType()==EdocEnum.edocType.recEdoc.ordinal()){nodePermissionPolicy="dengji";}
        	}
        	try{
        		//Long accountId=summary.getOrgAccountId();
        		//重要A8BUG_V3.50SP1_中国牧工商（集团）有限公司  _文书不能对有编辑权限的文号进行编辑，提示“只读权限文号不能编辑”_20130807018907 
        		Long accountId = EdocHelper.getFlowPermAccountId(summary, summary.getOrgAccountId(), templeteManager);
        		FlowPerm fp=flowPermManager.getFlowPerm(catType,nodePermissionPolicy, accountId);
        		permId=fp.getFlowPermId();
        		String baseAction=fp.getBasicOperation();
        		if(baseAction.indexOf("PrintContentAcc")<0){officecanPrint="false";}
        		if(baseAction.indexOf("SaveContentAcc")<0){officecanSaveLocal="false";}
        	}catch(Exception e){
        		log.error(e);
        	}

			if("Pending".equals(request.getParameter("from")))
			{
        		EdocOpinion tempOpinion=edocManager.findBySummaryIdAndAffairId(summaryId, affairId);
        		modelAndView.addObject("tempOpinion", tempOpinion);
        		if(tempOpinion!=null)
        		{
        			modelAndView.addObject("attachments", attachmentManager.getByReference(summaryId,tempOpinion.getId()));
        		}
			}
        }
        if(affair == null){
        	affair = affairManager.getCollaborationSenderAffair(summaryId);
        }
        //SECURITY 访问安全检查
    	if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.edoc, user, summaryId, affair, null)){
    		return null;
    	}
    	//自动获取签发日期， 封发日期
        if("fengfa".equals(nodePermissionPolicy) && summary.getPackTime()==null){
        	summary.setPackTime(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        if("qianfa".equals(nodePermissionPolicy) && summary.getSigningDate() == null){
        	summary.setSigningDate(new java.sql.Date(System.currentTimeMillis()));
        }

        modelAndView.addObject("affairId", _affairId) ;
        EdocFormModel fm=edocFormManager.getEdocFormModel(summary.getFormId(),summary,permId);
        fm.setEdocBody(summary.getFirstBody());
        modelAndView.addObject("formModel",fm);

        //公文处理意见回显到公文单,排序
        long flowPermAccout = EdocHelper.getFlowPermAccountId(user.getLoginAccount(), summary, templeteManager);
        //查找公文单节点权限和意见元素绑定
        Hashtable<String, String> locHs = edocFormManager.getOpinionLocation(summary.getFormId(),flowPermAccout);
        //查找公文单意见元素显示。
		EdocOpinionDisplayConfig displayConfig = edocFormManager.getEdocOpinionDisplayConfig(summary.getFormId(),flowPermAccout);

		Map<String,EdocOpinionModel> map = edocManager.getEdocOpinion(summary,displayConfig.isOnlyShowLastOpinion());

        Map strMap=EdocOpinionDisplayUtil.convertOpinionToString(map,displayConfig,metadataManager,orgManager);

        String opinionsJs = EdocOpinionDisplayUtil.optionToJs(strMap);

        modelAndView.addObject("opinionsJs",opinionsJs);
        //发起人意见
        modelAndView.addObject("senderOpinion",strMap.get("senderOpinionList"));
        modelAndView.addObject("senderOpinionAttStr",strMap.get("senderOpinionAttStr"));
        //公文单手写批注回显
        boolean isSender=(user.getId()==summary.getStartUserId());
        modelAndView.addObject("isSender",isSender);
        Long account = EdocHelper.getFlowPermAccountId(user.getLoginAccount(), summary, templeteManager);
        List<String>ols=edocFormManager.getOpinionElementLocationNames(summary.getFormId(),account);
        String hwjs=htmlHandWriteManager.getHandWritesJs(summaryId, user.getName(),ols);
        //if(onlySeeContent){hwjs="<Script language='JavaScript'>hwObjs=new Array();</Script>";}
        modelAndView.addObject("hwjs",hwjs);

        modelAndView.addObject("controller", "edocController.do");

        modelAndView.addObject("contentRecordId", summary.getEdocBodiesJs());

        //判断是否是部门归档
        String _docId = request.getParameter("docId");
        Long docResId = null;
        boolean isDeptPigeonhole = false;
        if(Strings.isNotBlank(_docId)){
        	 docResId=Long.parseLong(_docId);
        	 DocResource doc = docHierarchyManager.getDocResourceById(docResId);
        	 if(doc == null) {
        		 super.printV3XJS(response.getWriter());
        		 super.rendJavaScript(response, "alert(getA8Top().dialogArguments.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));" +
        				 						"getA8Top().window.returnValue = true;" +
					   					        "getA8Top().window.close();");
				 return null;
        	 }

        	 if(new Integer(1).equals(doc.getPigeonholeType())){
        		 isDeptPigeonhole=true;
        	 }
        }
        modelAndView.addObject("isDeptPigeonhole", isDeptPigeonhole);

		//公文可以移动以后需要兼容其他的情况
		if (Strings.isNotBlank(lenPotents)) {
			officecanPrint = "false";
			officecanSaveLocal = "false";

			officecanSaveLocal = "0".equals(lenPotents.substring(1, 2)) ? "false" : "true";
			officecanPrint = "0".equals(lenPotents.substring(2, 3)) ? "false" : "true";

			modelAndView.addObject("docId", docResId);
		} else if ("glwd".equals(openFrom)) {//从关联文档点击进来的，都不可以打印
			officecanPrint = "false";
		} else if ("waitsendOrSended".equals(openFromAffair)) {
			officecanPrint = "true";
			officecanSaveLocal = "true";
		}

        boolean isUnit=false;//是否是联合发文
        if(null!=summary){
        	int edocType = summary.getEdocType();
        	String category = "";
        	if(edocType == EdocEnum.edocType.sendEdoc.ordinal()){
        		category = "edoc_send_permission_policy";
        	}else if(edocType == EdocEnum.edocType.recEdoc.ordinal()){
        		category = "edoc_rec_permission_policy";
        	}else if(edocType == EdocEnum.edocType.signReport.ordinal()){
        		category = "edoc_qianbao_permission_policy";
        	}
        	String opins = this.getOpinionName(category, user.getAccountId());
            modelAndView.addObject("opn", opins);
            isUnit=summary.getIsunit();
        }

        modelAndView.addObject("officecanPrint",officecanPrint);
    	modelAndView.addObject("officecanSaveLocal",officecanSaveLocal);
    	modelAndView.addObject("openFrom",openFrom);
    	modelAndView.addObject("personInput", EdocSwitchHelper.canInputEdocWordNum(summary.getOrgAccountId()));
    	modelAndView.addObject("canTransformToPdf",true);
    	modelAndView.addObject("isBoundSerialNo",edocMarkDefinitionManager.getEdocMarkByTempleteId(summary.getTempleteId(), MarkCategory.serialNo)==null?false:true);

    	String logoURL = EdocHelper.getLogoURL(summary.getOrgAccountId());
        modelAndView.addObject("logoURL", logoURL);
    	return modelAndView;
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
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair affair = affairManager.getById(Long.parseLong(_affairId));
	        PrintWriter out = null;
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	out = response.getWriter();
	        	super.printV3XJS(out);
				String msg=ColHelper.getErrorMsgByAffair(affair);
				out.println("<script>");
	        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
	        	out.println("if(window.dialogArguments){"); //弹出
	        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
	        	out.println("  window.close();");
	        	out.println("}else{");
	        	out.println("  getA8Top().reFlesh();");
	        	out.println("}");
	        	out.println("</script>");
	        	out.close();
	        	return null;
	        }

	        //从request对象取到选人信息
	        FlowData flowData = FlowData.flowdataFromRequest();
	        //edocManager.addInform(summaryId, affairId, flowData);

	        edocManager.addInform(summaryId, affairId, flowData, user.getId()+"");

	        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (summary != null){
	            if (summary.getCaseId() != null) {
	                Long caseId = summary.getCaseId();
	                caseLogXML = edocManager.getCaseLogXML(caseId);
	                caseProcessXML = edocManager.getModifyingProcessXML(summary.getProcessId());
	                caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
	                String processId = summary.getProcessId();
	                caseProcessXML = edocManager.getProcessXML(processId);
	            }
	        }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

	        String process_desc_by = FlowData.DESC_BY_XML;
	        out = response.getWriter();
	        out.println("<script>");
	        out.println("parent.caseProcessXML = \"" + caseProcessXML + "\";");
	        out.println("parent.caseLogXML = \"" + caseLogXML + "\";");
	        out.println("parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
	        out.println("parent.document.all.process_desc_by.value = \"" + process_desc_by + "\";");
	        out.println("parent.document.all.process_xml.value = \"" + caseProcessXML + "\";");
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
        //ModelAndView mv= super.redirectModelAndView("/edocController.do?method=showDiagram&summaryId=" + sSummaryId + "&affairId=" + _affairId + "&from=Pending&preAction=addInform");
        //return mv;
    }
    /**
     * 传阅
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView addPassRead(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String sSummaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");

        long summaryId = Long.parseLong(sSummaryId);
        long affairId = Long.parseLong(_affairId);
        //从request对象取到选人信息
        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.inform,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair affair = affairManager.getById(Long.parseLong(_affairId));
	        PrintWriter out = null;
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	out = response.getWriter();
				String msg=ColHelper.getErrorMsgByAffair(affair);
				out.println("<script>");
	        	out.println("alert(\"" + Strings.escapeJavascript(msg) + "\")");
	        	out.println("if(window.dialogArguments){"); //弹出
	        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
	        	out.println("  window.close();");
	        	out.println("}else{");
	        	out.println("  top.reFlesh();");
	        	out.println("}");
	        	out.println("</script>");
	        	out.close();
	        	return null;
	        }

	        FlowData flowData = FlowData.flowdataFromRequest();
	        edocManager.addPassRead(summaryId, affairId, flowData);

	        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (summary != null){
	            if (summary.getCaseId() != null) {
	                long caseId = summary.getCaseId();
	                caseLogXML = edocManager.getCaseLogXML(caseId);
	                caseProcessXML = edocManager.getModifyingProcessXML(summary.getProcessId());
	                caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
	                String processId = summary.getProcessId();
	                caseProcessXML = edocManager.getProcessXML(processId);
	            }
	        }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

	        String process_desc_by = FlowData.DESC_BY_XML;
	        out = response.getWriter();
	        out.println("<script>");
	        out.println("parent.caseProcessXML = \"" + caseProcessXML + "\";");
	        out.println("parent.caseLogXML = \"" + caseLogXML + "\";");
	        out.println("parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
	        out.println("parent.document.all.process_desc_by.value = \"" + process_desc_by + "\";");
	        out.println("parent.document.all.process_xml.value = \"" + caseProcessXML + "\";");
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

    //加签
    @Deprecated
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


		String fCategoryName = "";
		EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
		if(null!=summary){
			if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
				fCategoryName = "edoc_send_permission_policy";
			}else if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
				fCategoryName = "edoc_rec_permission_policy";
			}else if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SIGN){
				fCategoryName = "edoc_qianbao_permission_policy";
			}
			if(!Strings.isBlank(fCategoryName)){
//				引用节点权限判断，如果是自由流程取发起人所在单位的节点权限，模板取模板所在单位节点权限
				Long flowPermAccountId = user.getLoginAccount();
				if(summary.getTempleteId() != null){
					Templete templete = templeteManager.get(summary.getTempleteId());
					if(templete != null){
						flowPermAccountId = templete.getOrgAccountId();
					}
				}
				else{
					if(summary.getOrgAccountId() != null){
						flowPermAccountId = summary.getOrgAccountId();
					}
				}
//			引用节点权限判断
	        FlowPermManager flowPermManager=(FlowPermManager)ApplicationContextHolder.getBean("flowPermManager");
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
			}

		}


		boolean isRelieveLock = true;
		try{
			//检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.insertPeople,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

			PrintWriter out = response.getWriter();

			Affair affair = affairManager.getById(affairId);
			if(affair.getState() != StateEnum.col_pending.key()){
				String msg=ColHelper.getErrorMsgByAffair(affair);
				super.printV3XJS(out);
				out.println("<script>");
				out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
				out.println("if(window.dialogArguments){"); //弹出
				out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
				out.println("  window.close();");
				out.println("}else{");
				out.println("  getA8Top().reFlesh();");
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
				throw new RuntimeException("should select one person at least");
			}

			//判断当前节点下一节点的节点类型
	    	BPMProcess process = null;
	        try {
	        	process = ColHelper.getModifyingProcess(processId, user.getId()+"");
	        	if(process == null)
	        		process = BPMProcess.fromXML(ColHelper.getRunningProcessByCaseId(summary.getCaseId()).toXML());

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
			edocManager.insertPeople(summary, affair, flowData, process, user.getId()+"","insertPeople");

			String caseLogXML = null;
			String caseProcessXML = null;
			String caseWorkItemLogXML = null;

			if (summary != null){
				if (summary.getCaseId() != null) {
					long caseId = summary.getCaseId();
					caseLogXML = edocManager.getCaseLogXML(caseId);
					caseProcessXML = edocManager.getModifyingProcessXML(summary.getProcessId());
					caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
				}
				else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
					caseProcessXML = edocManager.getProcessXML(processId);
				}
			}

			caseProcessXML = Strings.escapeJavascript(caseProcessXML);
			caseLogXML = Strings.escapeJavascript(caseLogXML);
			caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

			String process_desc_by = FlowData.DESC_BY_XML;
			out.println("<script>");
	        out.println("parent.window.dialogArguments.caseProcessXML = \"" + caseProcessXML + "\";");
	        out.println("parent.window.dialogArguments.caseLogXML = \"" + caseLogXML + "\";");
	        out.println("parent.window.dialogArguments.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
	        out.println("parent.window.dialogArguments.document.all.process_desc_by.value = \"" + process_desc_by + "\";");
	        out.println("parent.window.dialogArguments.document.all.process_xml.value = \"" + caseProcessXML + "\";");
	        out.println("parent.window.dialogArguments.selectInsertPeopleOK();");
	        out.println("window.close();");
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

//  减签前的人员检查
    public ModelAndView preDeletePeople(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        String processId = request.getParameter("processId");

        ModelAndView mv = new ModelAndView("edoc/decreaseNodes");

        Affair affair = affairManager.getById(affairId);
        if(affair.getState() != StateEnum.col_pending.key()){
        	PrintWriter out = response.getWriter();
			String msg=ColHelper.getErrorMsgByAffair(affair);
			super.printV3XJS(out);
			out.println("<script>");
        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
        	out.println("if(window.dialogArguments){"); //弹出
        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
        	out.println("  window.close();");
        	out.println("}else{");
        	out.println("  getA8Top().reFlesh();");
        	out.println("}");
        	out.println("</script>");
        	out.close();
        	return null;
        }

        FlowData flowData = edocManager.preDeletePeople(summaryId, affairId, processId, user.getId()+"");

        /*response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String peopleJsStr = FlowData.peopleToJs(flowData);

        out.print(peopleJsStr);
        return null;
        */
        mv.addObject("summmaryId", summaryId);
        mv.addObject("affairId", affairId);
        mv.addObject("flowData", flowData);
        mv.addObject("processId", processId);
        return mv;
    }
    //多级会签匹配人员
    public ModelAndView preAddMoreSign(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String selObj = request.getParameter("selObj");
        String appName = request.getParameter("appName");
        ModelAndView mv = new ModelAndView("edoc/addMoreSignSelect");

        List<MoreSignSelectPerson> msps=edocManager.findMoreSignPersons(selObj);
        mv.addObject("msps",msps);

        int flowPermType = 1;
        List<FlowPerm> nodePolicyList = null;
        String nodeMetadataName="";
        if("sendEdoc".equalsIgnoreCase(appName) || "edocSend".equals(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_send_permission_policy.name();
		}else if("recEdoc".equalsIgnoreCase(appName) || "edocRec".equals(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_rec_permission_policy.name();
		}else if("signReport".equalsIgnoreCase(appName) || "edocSign".equals(appName)){
			nodeMetadataName=MetadataNameEnum.edoc_qianbao_permission_policy.name();
		}
        //取得节点权限所属单位ID，分为两种情况，1.公文发起人所在的单位ID，2.模板所属单位的ID
        String _summaryId=request.getParameter("summary_id");
    	Long summaryId = Long.parseLong(_summaryId);
        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
        long accountId=EdocHelper.getFlowPermAccountId(summary, user.getLoginAccount(), templeteManager);
        nodePolicyList = flowPermManager.getFlowpermsByStatus(nodeMetadataName, FlowPerm.Node_isActive, false, flowPermType,accountId);
        mv.addObject("nodePolicyList",nodePolicyList);

        return mv;
    }
    //多级会签
    public ModelAndView addMoreSign(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
		String _summaryId = request.getParameter("summaryId");
		String _affairId = request.getParameter("affairId");
		Long summaryId = Long.parseLong(_summaryId);
		Long affairId = Long.parseLong(_affairId);
		String processId = request.getParameter("processId");


		EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);

		boolean isRelieveLock = true;
		try{
			//检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.insertPeople,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

			PrintWriter out = response.getWriter();

			Affair affair = affairManager.getById(affairId);
			if(affair.getState() != StateEnum.col_pending.key()){
				String msg=ColHelper.getErrorMsgByAffair(affair);
				super.printV3XJS(out);
				out.println("<script>");
				out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
				out.println("if(window.dialogArguments){"); //弹出
				out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
				out.println("  window.close();");
				out.println("}else{");
				out.println("  getA8Top().reFlesh();");
				out.println("}");
				out.println("</script>");
				out.close();
				return null;
			}

			//保存选人界面返回的信息
			FlowData flowData = FlowData.flowdataFromRequest();

			int iFlowType = 0;
			iFlowType = FlowData.FLOWTYPE_PARALLEL;
			flowData.setType(iFlowType);

			if (flowData.getPeople().isEmpty()) {
				throw new RuntimeException("should select one person at least");
			}

			//判断当前节点下一节点的节点类型
	        WorkItemManager wim = null;
	    	BPMProcess process = null;
	        try {
	        	process = ColHelper.getModifyingProcess(processId, user.getId()+"");
	        	if(process == null)
	        		process = BPMProcess.fromXML(ColHelper.getRunningProcessByCaseId(summary.getCaseId()).toXML());

	    	}catch(ColException e){
	    		log.error("", e);
	    	}

	    	//先增加自己
	    	String curPolicyId=request.getParameter("curPolicyId");
	    	String fCategoryName="";
	    	String curPolicyName="";
	    	FlowPerm perm;
	    	if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_SEND){
					fCategoryName = "edoc_send_permission_policy";
			}else if(summary.getEdocType() == com.seeyon.v3x.edoc.util.Constants.EDOC_FORM_TYPE_REC){
					fCategoryName = "edoc_rec_permission_policy";
			}else{
					fCategoryName = "edoc_qianbao_permission_policy";
			}
	    	perm = flowPermManager.getFlowPerm(fCategoryName, curPolicyId, EdocHelper.getFlowPermAccountId(user.getLoginAccount(), summary, templeteManager));
			if(perm.getType()==FlowPerm.Node_Type_Custome)
			{
				curPolicyName=perm.getName();
			}
			else
			{
				curPolicyName=ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",perm.getLabel());
	        }
			BPMSeeyonPolicy seeyonPolicy=new BPMSeeyonPolicy(perm.getName(),curPolicyName);
			FlowData curUserFlowData=null;
			if(affair.getMemberId().longValue()== user.getId()){//不是代理人处理
				curUserFlowData=FlowData.flowdataCurUserFromRequest(user, seeyonPolicy);
			}else{
				V3xOrgMember member= orgManager.getMemberById(affair.getMemberId());
				User realUser= new User();
				realUser.setId(member.getId());
				realUser.setName(member.getName());
				realUser.setLoginAccount(member.getOrgAccountId());
				curUserFlowData=FlowData.flowdataCurUserFromRequest(realUser, seeyonPolicy);
			}
			curUserFlowData.setFromType("moreSignSelf");
			edocManager.insertPeople(summary, affair, curUserFlowData, process, user.getId()+"","addMoreSignSelf");

			edocManager.insertPeople(summary, affair, flowData, process, user.getId()+"","addMoreSign");

			String caseLogXML = null;
			String caseProcessXML = null;
			String caseWorkItemLogXML = null;

			if (summary != null){
				if (summary.getCaseId() != null) {
					long caseId = summary.getCaseId();
					caseLogXML = edocManager.getCaseLogXML(caseId);
					caseProcessXML = edocManager.getModifyingProcessXML(summary.getProcessId());
					caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
				}
				else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
					caseProcessXML = edocManager.getProcessXML(processId);
				}
			}

			caseProcessXML = Strings.escapeJavascript(caseProcessXML);
			caseLogXML = Strings.escapeJavascript(caseLogXML);
			caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

			String process_desc_by = FlowData.DESC_BY_XML;
			out.println("<script>");
	        out.println("parent.window.dialogArguments.caseProcessXML = \"" + caseProcessXML + "\";");
	        out.println("parent.window.dialogArguments.caseLogXML = \"" + caseLogXML + "\";");
	        out.println("parent.window.dialogArguments.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
	        out.println("parent.window.dialogArguments.document.all.process_desc_by.value = \"" + process_desc_by + "\";");
	        out.println("parent.window.dialogArguments.document.all.process_xml.value = \"" + caseProcessXML + "\";");
	        out.println("parent.window.dialogArguments.selectInsertPeopleOK();");
	        out.println("window.close();");
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
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        PrintWriter out = response.getWriter();
	        Affair affair = affairManager.getById(affairId);
	        if(affair.getState() != StateEnum.col_pending.key()){
				String msg=ColHelper.getErrorMsgByAffair(affair);
				super.printV3XJS(out);
				out.println("<script>");
	        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
	        	out.println("if(window.dialogArguments){"); //弹出
	        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
	        	out.println("  window.close();");
	        	out.println("}else{");
	        	out.println("  getA8Top().reFlesh();");
	        	out.println("}");
	        	out.println("</script>");
	        	out.close();
	        	return null;
	        }
	        //保存选人界面返回的信�xi息
	        FlowData flowData = FlowData.flowdataFromRequest();

	        if (flowData.getPeople().isEmpty()) {
	            throw new RuntimeException("should select one person at least");
	        }

	        List<Party> people = flowData.getPeople();

	        edocManager.deletePeople(summaryId, affairId, people, user.getId()+"");

	        if (flowData.getPeople().isEmpty()) {
	            throw new RuntimeException("should select one person at least");
	        }

	        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (summary != null){
	            if (summary.getCaseId() != null) {
	                long caseId = summary.getCaseId();
	                caseLogXML = edocManager.getCaseLogXML(caseId);
	                caseProcessXML = edocManager.getModifyingProcessXML(summary.getProcessId());
	                caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
	                String processId = summary.getProcessId();
	                caseProcessXML = edocManager.getProcessXML(processId);
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
	        out.println("parent.document.all.process_desc_by.value = \"" + process_desc_by + "\";");
	        out.println("parent.document.all.process_xml.value = \"" + caseProcessXML + "\";");
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
        //return super.redirectModelAndView("/edocController.do?method=showDiagram&summaryId=" + _summaryId + "&affairId=" + _affairId + "&from=Pending&preAction=deletePeople");
    }
    /**
     * 撤消流程，已发变待发
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public ModelAndView repeal(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        User user = CurrentUser.get();
        String[] _summaryIds = new String[]{};
        String page = request.getParameter("page");
        StringBuffer info = new StringBuffer();
        String affairIds= request.getParameter("affairId");
        String repealComment = request.getParameter("repealComment"); //撤销附言
        if("workflowManager".equals(page)){
        	String[] summaryIdArr = {request.getParameter("summaryId")};
        	_summaryIds = summaryIdArr;
        }else{
        	_summaryIds = request.getParameterValues("id");
          	if("dealrepeal".equals(page)){
        		repealComment = request.getParameter("content");
        	}
        }

        //保存撤销时的意见
        EdocOpinion signOpinion = null;
        int mark = 0;
        Map<Long, EdocOpinion> edocOpinionMap = new HashMap<Long, EdocOpinion>();
        Map<String, Object> conditionsMap = new HashMap<String, Object>();
        if(StringUtils.isNotBlank(affairIds)){
        	String[] affairId = affairIds.split(";");
        	List<Long> _affairIds = new ArrayList<Long>();
//        	List<Long> _affairIds = new ArrayList<Long>(Arrays.asList(affairId));
        	for(int i = 0; i< affairId.length; i++){
        		_affairIds.add(Long.valueOf(affairId[i]));
        	}
        	conditionsMap.put("id", _affairIds);
        	List<Affair> affairs = affairManager.getByConditions(conditionsMap);
        	for(Affair _affair:affairs){
        		signOpinion = new EdocOpinion();
//        		bind(request, signOpinion);

        		signOpinion.setContent(repealComment);
        		signOpinion.setPolicy(_affair.getNodePolicy());

        		String attitude = request.getParameter("attitude");
        		if (!Strings.isBlank(attitude)) {
        			signOpinion.setAttribute(Integer.valueOf(attitude).intValue());
        		} else {
        			signOpinion
        					.setAttribute(com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL);
        		}

        		signOpinion.isDeleteImmediate = false;
        		signOpinion.affairIsTrack = false;

        		signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
        		signOpinion.setIdIfNew();

        		long nodeId = -1;
        		if (request.getParameter("currentNodeId") != null
        				&& !"".equals(request.getParameter("currentNodeId"))) {
        			nodeId = Long.parseLong(request.getParameter("currentNodeId"));
        		}
        		signOpinion.setNodeId(nodeId);

        		// 设置代理人信息
        		if (user.getId() != _affair.getMemberId()) {
        			signOpinion.setProxyName(user.getName());
        		}
        		signOpinion.setCreateUserId(_affair.getMemberId());
        		edocOpinionMap.put(Long.parseLong(_summaryIds[mark]), signOpinion);
        		mark++;
        	}
        }

        //为了保证数据库中存储正确，并且保证绑定节点意见时保持和输入一致
        repealComment = Strings.toHTML(repealComment);

        boolean isRelieveLock = true;
        try{
	        int result = 0;
	        for (int i = 0; i < _summaryIds.length; i++) {
	            Long summaryId = Long.parseLong(_summaryIds[i]);
	            //检查同步锁
	            if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.cancel,
	            		response, ApplicationCategoryEnum.edoc)){
	            	isRelieveLock = false;
	            	return null;
	            }
	            EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
                if(summary.getFinished()){result=1;}
	            //else if(summary.getHasArchive()){result=2;}
	            else
	            {
	            	result = edocManager.cancelSummary(user.getId(), summaryId, repealComment, edocOpinionMap.get(summaryId));
	            }
	            if (result == 1 || result == -1)
	            {
	                info.append("《").append(summary.getSubject()).append("》");
	            }else{
	            	//发送消息给督办人，更新督办状态，并删除督办日志、删除督办记录、删除催办次数
		            this.colSuperviseManager.updateStatusAndNoticeSupervisorWithoutMes(summaryId, Constant.superviseType.edoc.ordinal(), Constant.superviseState.waitSupervise.ordinal());
	            }

	            try{
	        		updateIndexManager.getIndexManager().deleteFromIndex(EdocUtil.getAppCategoryByEdocType(summary.getEdocType()),summary.getId());
	        	}catch(Exception e){
	        		log.error("撤销公文流程，更新全文检索异常",e);
	        	}


	            try{
	            	//解锁正文文单
	            	unLock(user.getId(),summary);
	            }catch(Exception e){
	            	log.error(e);
	            }
	            //撤销流程事件
	            CollaborationCancelEvent event = new CollaborationCancelEvent(this);
	            event.setSummaryId(summary.getId());
	            event.setUserId(user.getId());
	            event.setMessage(repealComment);
	            EventDispatcher.fireEvent(event);
	        }


	        PrintWriter out = response.getWriter();
	        super.printV3XJS(out);
	        if(info.length() > 0){
	        	String alertStr="";
	        	if(result == -1){
	        		//WebUtil.saveAlert(ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.state.system.running",info.toString()));
	        		alertStr=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.state.system.running",info.toString());
	        	}
//	        	else if(result == 2)
//	        	{
//	        		//WebUtil.saveAlert(ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.archive.notoperate"));
//	        		alertStr=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.archive.notoperate");
//	        	}
	        	else{
	        		//WebUtil.saveAlert(ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.state.end.alert",info.toString()));
	        		alertStr=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.state.end.alert",info.toString());
	        	}
	        	out.println("<script>");
	        	out.println("<!--");
	        	out.println("alert(\""+StringEscapeUtils.escapeJavaScript(alertStr)+"\");");
	        	out.println("//-->");
	        	out.println("</script>");
	        }
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
		    	out.println("  window.returnValue = \"true\";");
		    	out.println("  window.close();");
		    	out.println("}else{");
		    	if("dealrepeal".equals(page))
		    	{
		    		out.println("  parent.parent.parent.location.reload(true);");
		    	}
		    	else
		    	{
		    		out.println("  parent.location.reload(true);");
		    	}
		    	out.println("}");
		    	out.println("</script>");
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
        }
        return null;
    }

    //回退
    public ModelAndView stepBack(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        String processId = request.getParameter("processId");
        boolean isRelieveLock = true;
        EdocSummary summary = null ;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.stepback,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair _affair = affairManager.getById(affairId);
	        PrintWriter out = response.getWriter();
	        String errMsg="";
	        if(_affair.getState() != StateEnum.col_pending.key()){
	        	errMsg=ColHelper.getErrorMsgByAffair(_affair);
	        }
	        if("".equals(errMsg))
	        {
	        	summary=edocSummaryManager.findById(summaryId);
//	        	if(summary.getHasArchive())
//	        	{
//	        		errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.archive.notoperate");
//	        	}else
	        	if(summary.getFinished()){
	        		errMsg=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.state.end.stepback.alert","《"+summary.getSubject()+"》");
	        	}
	        }
	        if(!"".equals(errMsg))
	        {
	        	out.println("<script>");
	        	out.println("<!--");
	        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(errMsg) + "\")");
	        	out.println("if(window.dialogArguments){"); //弹出
	        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
	        	out.println("  window.close();");
	        	out.println("}else{");
	        	out.println("  parent.parent.location.reload(true);");
	        	out.println("}");
	        	out.println("//-->");
	        	out.println("</script>");
	        	out.close();
	        	return null;
	        }

	        //保存回退时的意见,附件�?
	        EdocOpinion signOpinion = new EdocOpinion();
	        bind(request, signOpinion);

	        String content = request.getParameter("contentOP");
	        signOpinion.setContent(content);

	        String attitude = request.getParameter("attitude");
	        if(!Strings.isBlank(attitude)){
	        	signOpinion.setAttribute(Integer.valueOf(attitude).intValue());
	        }else{
	        	signOpinion.setAttribute(com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL);
	        }
	        //回退的时候,选择归档,跟踪无效
	        //String afterSign = request.getParameter("afterSign");

	        signOpinion.isDeleteImmediate = false;//"delete".equals(afterSign);
	        signOpinion.affairIsTrack = false;//"track".equals(afterSign);

	        signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
	        signOpinion.setIdIfNew();

	        long nodeId=-1;
	        if(request.getParameter("currentNodeId")!=null && !"".equals(request.getParameter("currentNodeId")))
	        {
	        	nodeId=Long.parseLong(request.getParameter("currentNodeId"));
	        }
	        signOpinion.setNodeId(nodeId);

//	      	//设置代理人信息
	        if(user.getId()!= _affair.getMemberId())
	        {
	        	signOpinion.setProxyName(user.getName());
	        }
	        signOpinion.setCreateUserId(_affair.getMemberId());

	        //保存附件
	        //先删除原有附件，修改bug15597
	        String oldOpinionIdStr=request.getParameter("oldOpinionId");
	        if(!"".equals(oldOpinionIdStr))
	        {
	        	this.attachmentManager.deleteByReference(summaryId, Long.parseLong(oldOpinionIdStr));
	        }
	        //this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, signOpinion.getId(), request);
	        //使用附件组件重构
            AttachmentEditHelper editHelper = new AttachmentEditHelper(request);
            if(editHelper.hasEditAtt()){//是否修改附件
            	edocManager.saveUpdateAttInfo(editHelper.attSize(),summaryId,editHelper.parseProcessLog(Long.parseLong(processId), nodeId));
            }
        	this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, signOpinion.getId(), request);

	        //true:成功回退 false:不允许回退
	        boolean ok = edocManager.stepBack(summaryId, affairId,signOpinion);
	        //TODO 不允许回退提示待完成

	        //回退成功后，跳到待办列表�?
	        //return super.refreshWorkspace();
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

            try{
            	//解锁正文文单
            	unLock(user.getId(),summary);
            }catch(Exception e){
            	log.error(e);
            }

        }

        return super.refreshWorkspace();
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
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        Affair affair = affairManager.getById(affairId);
	        PrintWriter out = null;
	        if(affair.getState() != StateEnum.col_pending.key()){
	        	out = response.getWriter();
				String msg=ColHelper.getErrorMsgByAffair(affair);
				super.printV3XJS(out);
				out.println("<script>");
	        	out.println("alert(\"" + StringEscapeUtils.escapeJavaScript(msg) + "\")");
	        	out.println("if(window.dialogArguments){"); //弹出
	        	out.println("  window.returnValue = \"" + DATA_NO_EXISTS + "\";");
	        	out.println("  window.close();");
	        	out.println("}else{");
	        	out.println("  getA8Top().reFlesh();");
	        	out.println("}");
	        	out.println("</script>");
	        	out.close();
	        	return null;
	        }
	        //从request对象取到选人信息
	        FlowData flowData = FlowData.flowdataFromRequest();

	        edocManager.colAssign(summaryId, affairId, flowData, user.getId()+"");

	        //colManager.colAssign(summaryId, affairId, flowData, user.getId()+"");

	        EdocSummary summary = edocManager.getEdocSummaryById(summaryId, false);
	        String caseLogXML = null;
	        String caseProcessXML = null;
	        String caseWorkItemLogXML = null;
	        if (summary != null){
	            if (summary.getCaseId() != null) {
	                long caseId = summary.getCaseId();
	                caseLogXML = edocManager.getCaseLogXML(caseId);
	                caseProcessXML = edocManager.getModifyingProcessXML(summary.getProcessId());
	                caseWorkItemLogXML = edocManager.getCaseWorkItemLogXML(caseId);
	            }
	            else if (summary.getProcessId() != null && !"".equals(summary.getProcessId())) {
	                String processId = summary.getProcessId();
	                caseProcessXML = edocManager.getProcessXML(processId);
	            }
	        }

	        caseProcessXML = Strings.escapeJavascript(caseProcessXML);
	        caseLogXML = Strings.escapeJavascript(caseLogXML);
	        caseWorkItemLogXML = Strings.escapeJavascript(caseWorkItemLogXML);

	        String process_desc_by = FlowData.DESC_BY_XML;
	        Boolean isMatch = false;
	        out = response.getWriter();
	        out.println("<script>");
	        if((Boolean)BrowserFlag.PageBreak.getFlag(request)){
		        out.println("parent.window.dialogArguments.caseProcessXML = \"" + caseProcessXML + "\";");
		        out.println("parent.window.dialogArguments.caseLogXML = \"" + caseLogXML + "\";");
		        out.println("parent.window.dialogArguments.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
		        out.println("parent.window.dialogArguments.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
		        out.println("parent.window.dialogArguments.document.getElementById('isMatch').value = \""+isMatch+"\";");
		        out.println("parent.window.dialogArguments.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
		        out.println("parent.window.dialogArguments.selectInsertPeopleOK();");
		        out.println("top.close();");
	        }else{
		        out.println("parent.parent.caseProcessXML = \"" + caseProcessXML + "\";");
		        out.println("parent.parent.caseLogXML = \"" + caseLogXML + "\";");
		        out.println("parent.parent.caseWorkItemLogXML = \"" + caseWorkItemLogXML + "\";");
		        out.println("parent.parent.document.getElementById('process_desc_by').value = \"" + process_desc_by + "\";");
		        out.println("parent.parent.document.getElementById('isMatch').value = \""+isMatch+"\";");
		        out.println("parent.parent.document.getElementById('process_xml').value = \"" + caseProcessXML + "\";");
		        out.println("parent.parent.selectInsertPeopleOK();");
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

        /*String xmlJavaScript=EdocHelper.getWorkFlowInfoScript(summaryId, edocManager);
        PrintWriter out = response.getWriter();
        out.println(xmlJavaScript);
        out.close();
        return null;*/
        //return super.redirectModelAndView("/edocController.do?method=showDiagram&summaryId=" + sSummaryId + "&affairId=" + _affairId + "&from=Pending&preAction=colAssign");
    }

    //取回
    public ModelAndView takeBack(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String[] affairIds = request.getParameterValues("affairId");
        String[] summaryIds = request.getParameterValues("summaryId");
        boolean isRelieveLock = true;
        try{
	        StringBuffer info = new StringBuffer();
	        StringBuffer info1 = new StringBuffer();
			StringBuffer info2 = new StringBuffer();
	        if (affairIds != null) {
	        	int i=0;
	            for (String affairId : affairIds) {
	                Long _affairId = new Long(affairId);
	                Long summaryId = Long.parseLong(summaryIds[i]);
	                //检查同步锁
	                if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.tackback,
	                		response, ApplicationCategoryEnum.edoc)){
	                	isRelieveLock = false;
	                	return null;
	                }
	                EdocSummary summary=edocManager.getEdocSummaryById(summaryId,false);
					Affair affair = affairManager.getById(_affairId);
					// if (summary.getFinished()) {
					// info1.append("《").append(summary.getSubject()).append(
					// "》").append("\n");
					// continue;
					// }

//					if (summary.getHasArchive()) {
//						// 公文被归档
//						// 输出提示信息，提示公文不能够被取回
//						info2.append("《").append(summary.getSubject()).append(
//								"》").append("\n");
//						continue;
//					}
					if (isBeSended(summaryId)) {
						// 公文被发送
						// 输出提示信息，提示公文不能够被取回
						info1.append("《").append(summary.getSubject()).append(
								"》").append("\n");
						continue;
					}
	                boolean ok = edocManager.takeBack(_affairId);
	                if(ok==false)
	                {
	                	if(affair!=null)
	                	{
	                		info.append("《").append(affair.getSubject()).append("》").append("\n");
	                    }
	                }
	                i++;
	            }
	        }
	        if(info.length() > 0){
	        	WebUtil.saveAlert(ResourceBundleUtil.getString("com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource","takeBack.not.label",info.toString()));
	        }
	        if(info1.length() > 0){
				WebUtil.saveAlert(ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"edoc.state.end.takeback.sendalert", info1.toString()));
	        }
			if (info2.length() > 0) {
				WebUtil.saveAlert(ResourceBundleUtil.getString(
						"com.seeyon.v3x.edoc.resources.i18n.EdocResource",
						"edoc.state.end.takeback.archivealert", info2
								.toString()));
			}
	        return super.refreshWindow("parent");
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock){
        		for(int i=0; i<summaryIds.length; i++){
        			ColLock.getInstance().removeLock(Long.parseLong(summaryIds[i]));
        		}
        	}
        }
        return super.refreshWorkspace();
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
        if (affairIds != null) {
            for (String affairId : affairIds) {
                Long _affairId = new Long(affairId);
                edocManager.deleteAffair(pageType, _affairId);
            }
        }
        return super.refreshWindow("parent");
        //return super.refreshWorkspace();

//        return super.redirectModelAndView("/collaboration.do?method=" + request.getParameter("from"));
    }

    //终止
    public ModelAndView stepStop(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
        String _summaryId = request.getParameter("summary_id");
        String _affairId = request.getParameter("affairId");
        Long summaryId = Long.parseLong(_summaryId);
        Long affairId = Long.parseLong(_affairId);
        boolean isRelieveLock = true;
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.stepstop,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }
	        Affair _affair = affairManager.getById(affairId);
	        //保存终止时的意见,附件�
	        EdocOpinion signOpinion = new EdocOpinion();
	        bind(request, signOpinion);

	        String attitude = request.getParameter("attitude");
	        if(!Strings.isBlank(attitude)){
	        	signOpinion.setAttribute(Integer.valueOf(attitude).intValue());
	        }else{
	        	signOpinion.setAttribute(com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL);
	        }

	        String afterSign = request.getParameter("afterSign");

	        signOpinion.isDeleteImmediate = "delete".equals(afterSign);
	        signOpinion.affairIsTrack = "track".equals(afterSign);

	        signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
	        signOpinion.setIdIfNew();

	        long nodeId=-1;
	        if(request.getParameter("currentNodeId")!=null && !"".equals(request.getParameter("currentNodeId")))
	        {
	        	nodeId=Long.parseLong(request.getParameter("currentNodeId"));
	        }
	        signOpinion.setNodeId(nodeId);
//	      	//设置代理人信息
	        if(user.getId()!= _affair.getMemberId())
	        {
	        	signOpinion.setProxyName(user.getName());
	        }
	        signOpinion.setCreateUserId(_affair.getMemberId());

	        String content = request.getParameter("contentOP");
	        signOpinion.setContent(content);
	        //为了保存流程日志中修改附件的记录在处理提交之前，所以将保存附件的操作提前了。bug29527
	        //保存附件
	        String oldOpinionIdStr=request.getParameter("oldOpinionId");
	        if(!"".equals(oldOpinionIdStr))
	        {//删除原来意见,上传附件等
	        	Long oldOpinionId=Long.parseLong(oldOpinionIdStr);
	        	attachmentManager.deleteByReference(summaryId, oldOpinionId);
	        }

	        //保存附件
	        this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, signOpinion.getId(), request);

	        edocManager.stepStop(summaryId, affairId,signOpinion);

	        //终止成功后，跳到待办列表�?
	        PrintWriter out = response.getWriter();
			super.printV3XJS(out);
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
        //return super.redirectModelAndView("/collaboration.do?method=showDiagram&summaryId=" + _summaryId + "&affairId=" + _affairId + "&from=Pending&preAction=stepBack");
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
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.stepstop,
	        		response, ApplicationCategoryEnum.edoc)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        //保存终止时的意见,附件�
	        EdocOpinion signOpinion = new EdocOpinion();
	        bind(request, signOpinion);

	        String attitude = request.getParameter("attitude");
	        if(!Strings.isBlank(attitude)){
	        	signOpinion.setAttribute(Integer.valueOf(attitude).intValue());
	        }else{
	        	signOpinion.setAttribute(com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL);
	        }

	        signOpinion.isDeleteImmediate = false;
	        signOpinion.affairIsTrack = false;
	        signOpinion.setIsHidden(false);
	        signOpinion.setIdIfNew();

	        edocManager.stepStop(summaryId, null, signOpinion);
	        //终止成功后，跳到待办列表�
        	PrintWriter out = response.getWriter();
			super.printV3XJS(out);
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
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        }
        return null;
    }

    //公文已发，已办归档
    public ModelAndView pigeonhole(HttpServletRequest request,HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("edoc/listDone");
    	String[] affairIds = request.getParameterValues("affairId");
        String pageType = request.getParameter("pageType");
        String edocType = request.getParameter("edocType");
        String _archiveId = request.getParameter("archiveId");


        Long archiveId=null;
        if(Strings.isNotBlank(_archiveId)){
        	archiveId=Long.parseLong(_archiveId);
        }

        StringBuffer sbInfo=new StringBuffer();
        StringBuffer accInfo=new StringBuffer();
        String nodePermissionPolicy;
        MetadataNameEnum edocTypeEnum;
     //   User user = CurrentUser.get();
        String needChoosePigeonholeSubject= "";//需要选择归档归档的公文。
        String needChoosePigeonholeIds="";
        int successCount=0;
        Set filterSet=new HashSet();
        if (affairIds != null) {
            for (String affairId : affairIds) {
            	if(Strings.isBlank(affairId)) continue;
            	if(filterSet.contains(affairId))
            		continue;
            	else
            		filterSet.add(affairId);

                Long _affairId = new Long(affairId);
                Affair affair=affairManager.getById(_affairId);
                EdocSummary summary=edocManager.getEdocSummaryById(affair.getObjectId(), false);
                if(archiveId != null)summary.setArchiveId(archiveId);
                boolean docResourceExist = docHierarchyManager.docResourceExist(summary.getArchiveId());
                if(summary.getArchiveId() == null || !docResourceExist) {
                	needChoosePigeonholeSubject+="《"+affair.getSubject()+"》<br>";
                	needChoosePigeonholeIds +=affair.getId()+",";
                }

                edocTypeEnum=EdocUtil.getEdocMetadataNameEnumByApp(affair.getApp());
                try
                {
                	nodePermissionPolicy = edocManager.getPolicyByAffair(affair);
                	if("finish".equals(pageType))
                	{//已办事项，归档判断处理时，是否有归档权限

                		List<String> baseActions = permissionManager.getActionList(edocTypeEnum.name(), nodePermissionPolicy, "basic", summary.getOrgAccountId());
                		if(baseActions.contains("Archive")==false)
                		{
                			if(accInfo.length()>0){accInfo.append(",");}
                			accInfo.append(affair.getSubject());
                			continue;
                		}
                	}

                	//是否有没有设置预归档路径的公文
                	if(summary.getArchiveId()!=null && docResourceExist){
                		affair.setNodePolicy(nodePermissionPolicy);
                		edocManager.pigeonholeAffair(pageType, _affairId, affair.getObjectId(),archiveId);
                		successCount++; //统计归档成功的个数。
                	}
                }catch(EdocException e)
                {
                	if(e.getErrNum()==EdocException.errNumEnum.workflow_not_finish.ordinal())
                	{
                		if(sbInfo.length()>0){sbInfo.append(",");}
                		sbInfo.append(e.getMessage());
                	}
                	else
                	{
                		throw e;
                	}
                }
            }
        }


        PrintWriter out = response.getWriter();
        if(!"".equals(needChoosePigeonholeSubject)){//假设有需要选择归档路径的公文
        	String confirmInfo="";
        	if(successCount != 0){
        		confirmInfo= ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.pigdoc.alert.list",successCount,needChoosePigeonholeSubject);
        	}else{//没有成功归档的。
        		confirmInfo = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.pigdoc.alert.list.needchoose",needChoosePigeonholeSubject);
        	}
		    String js="if(confirm('"+confirmInfo.replace("<br>", "\\r\\n")+"')){\r\n"+
    		"var selectIds = '"+needChoosePigeonholeIds.substring(0,needChoosePigeonholeIds.length()-1)+"';\r\n"+
    		"var ids=document.getElementsByName('id');for(var i=0;i<ids.length;i++){"+
    		"if(selectIds.indexOf(ids[i].affairId)!=-1)ids[i].checked=true;}"+
    		"doPigeonhole('new',"+ApplicationCategoryEnum.edoc.key()+",'listDone','');\r\n"+
    		"var archiveId = document.getElementById('archiveId').value;\r\n"+
    		"if(archiveId)pigeonholeForEdoc();}";

		    WebUtil.saveJavascript(js);
        }else if(sbInfo.length()>0){//有流程未结束的
        	//TODO
        }else{//所有的都设有预归档目录，不需要选择。
        	String alertInfo = ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.pigdoc.alert.list.success",successCount);
        	WebUtil.saveAlert(alertInfo);
        }
        return super.refreshWindow("parent.parent");
//        if(sbInfo.length() > 0 || accInfo.length()>0){
//        	String err1="";
//        	String err2="";
//        	if(sbInfo.length()>0)
//        	{
//        		String[] sbInfos=sbInfo.toString().split(",");
//        		for(String s:sbInfos){
//        			if(!"".equals(err2))
//        				err2+="\r\n";
//        			err2+=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.err.nofinished.pig",s);
//        		}
//        	}
//        	if(accInfo.length()>0)
//        	{
//        		String[] accInfos=accInfo.toString().split(",");
//        		for(String s:accInfos){
//        			if(!"".equals(err1))
//        				err1+="\r\n";
//        			err1+=ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource","edoc.err.noaccess.pig",s);
//        		}
//        	}
//        	WebUtil.saveAlert(Strings.joinDelNull("\r\n",err1,err2));
//        }

        //return super.refreshWindow("parent");
    }
    private void printEdocJS(PrintWriter out){
        out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + SystemEnvironment.getA8ContextPath() + "/apps_res/edoc/js/edoc.js\"></script>");
    }
	public void setEdocMarkHistoryManager(
			EdocMarkHistoryManager edocMarkHistoryManager) {
		this.edocMarkHistoryManager = edocMarkHistoryManager;
	}

	//	系统管理员主页面
    public ModelAndView sysMain(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("edoc/sysMain");
    	return modelAndView;
    }

    //	系统管理员主页面
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView sysCompanyMain(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView("edoc/sysComanyMain");
    	boolean isGroupVer=false;

    	isGroupVer=(Boolean)SysFlag.sys_isGroupVer.getFlag();

    	modelAndView.addObject("isGroupVer",isGroupVer);
    	return modelAndView;
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
        String processId = request.getParameter("processId");
        boolean isRelieveLock = true;

        EdocSummary summary = null;
        Affair affair = affairManager.getById(affairId);
        try{
	        //检查同步锁
	        if(!ColHelper.colOperationLock(summaryId, user.getId(), user.getName(), ColLock.COL_ACTION.insertPeople,
	        		response, ApplicationCategoryEnum.collaboration)){
	        	isRelieveLock = false;
	        	return null;
	        }

	        EdocOpinion opinion = new EdocOpinion();
	        bind(request, opinion);
	        opinion.setIdIfNew();
	        opinion.setIsHidden(request.getParameterValues("isHidden") != null);
	        String attitude = request.getParameter("attitude");
	        if(!Strings.isBlank(attitude)){
	        	opinion.setAttribute(Integer.valueOf(attitude).intValue());
	        }else{
	        	opinion.setAttribute(com.seeyon.v3x.edoc.util.Constants.EDOC_ATTITUDE_NULL);
	        }
	        String content = request.getParameter("contentOP");
	        opinion.setContent(content);
	        //String afterSign = request.getParameter("afterSign");
        	//affair.setIsTrack("track".equals(afterSign));
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
  	        edocManager.setTrack(affairId, track, trackMembers);


	        long nodeId=-1;
	        if(request.getParameter("currentNodeId")!=null && !"".equals(request.getParameter("currentNodeId")))
	        {
	        	nodeId=Long.parseLong(request.getParameter("currentNodeId"));
	        }
	        opinion.setNodeId(nodeId);

	        String spMemberId = request.getParameter("supervisorId");
	        String superviseDate = request.getParameter("awakeDate");
	        String supervisorNames = request.getParameter("supervisors");
	        String title = request.getParameter("superviseTitle");

	        summary = edocManager.getEdocSummaryById(summaryId, true);

	        //为了保存流程日志中修改附件的记录在处理提交之前，所以将保存附件的操作提前了。bug29527
	        //保存附件
	        String oldOpinionIdStr=request.getParameter("oldOpinionId");
	        if(!"".equals(oldOpinionIdStr))
	        {//删除原来意见,上传附件等
	        	Long oldOpinionId=Long.parseLong(oldOpinionIdStr);
	        	attachmentManager.deleteByReference(summaryId, oldOpinionId);
	        	edocManager.deleteEdocOpinion(oldOpinionId);
	        }

	        //使用附件组件重构--dongyj
	        /*this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, opinion.getId(), request);
	        //保存正文的附件
	        //是否修改了正文附件
	        String isContentAttchmentChanged=request.getParameter("isContentAttchmentChanged");
	        if("1".equals(isContentAttchmentChanged)){
	        	this.edocManager.updateAttachment(summary,affair,user,request);
	        }*/
            AttachmentEditHelper editHelper = new AttachmentEditHelper(request);
            if(editHelper.hasEditAtt()){//是否修改附件
            	edocManager.saveUpdateAttInfo(editHelper.attSize(),summaryId,editHelper.parseProcessLog(Long.parseLong(processId), nodeId));
            }
        	this.attachmentManager.create(ApplicationCategoryEnum.edoc, summaryId, opinion.getId(), request);
	        //使用附件组件重构 end

        	//推送消息    affairId,memberId#affairId,memberId#affairId,memberId
		    String pushMessageMembers = request.getParameter("pushMessageMemberIds");
		    setPushMessagePara2ThreadLocal(pushMessageMembers);

		    //如果当前操作执行了转PDF的操作。
        	String isConvertPdf=request.getParameter("isConvertPdf");
        	if(Strings.isNotBlank(isConvertPdf)){
        		createPdfBodies(request, summary);
        	}

	        if(null!=supervisorNames && !"".equals(supervisorNames) && null!=spMemberId && !"".equals(spMemberId) && null!=superviseDate && !"".equals(superviseDate)){
	        	this.edocManager.zcdb(affair, opinion, title, spMemberId, supervisorNames,superviseDate, summary, processId, user.getId()+"");
	        }else{
	        	this.edocManager.zcdb(summary, affair, opinion, processId, user.getId()+"");
	        	if ("true".equals(request.getParameter("isDeleteSupervisior"))) {
					this.edocSuperviseManager.deleteSuperviseDetailAndSupervisors(summary);
				}
	        }
	        String draftOpinionId = request.getParameter("draftOpinionId");
	        if(Strings.isNotBlank(draftOpinionId)){ //修改草稿
	        	edocManager.deleteEdocOpinion(Long.parseLong(draftOpinionId));
	        }

	        updateIndexManager.update(summaryId,EdocUtil.getAppCategoryByEdocType(summary.getEdocType()).getKey());

	        PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println("parent.doEndSign();");
	        out.println("</script>");
        }catch(Exception e){
        	log.error("", e);
        }finally{
        	if(isRelieveLock)
        		ColLock.getInstance().removeLock(summaryId);
        	  //解锁正文文单
        	try{
        		unLock(user.getId(),summary);
        	}catch(Exception e){
        		log.error(e);
        	}
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
    public ModelAndView superviseList(HttpServletRequest request,HttpServletResponse response)throws Exception{

    	ModelAndView mav = new ModelAndView("edoc/supervise/supervise_list_main");

    	return mav;
    }

	public void setEdocInnerMarkDefinitionManager(
			EdocInnerMarkDefinitionManager edocInnerMarkDefinitionManager) {
		this.edocInnerMarkDefinitionManager = edocInnerMarkDefinitionManager;
	}

	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public void setEdocSuperviseManager(EdocSuperviseManager edocSuperviseManager) {
		this.edocSuperviseManager = edocSuperviseManager;
	}

	public ModelAndView showList4QuoteFrame(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView modelAndView = new ModelAndView("edoc/list4QuoteFrame");
    	String appType = request.getParameter("appType");
    	modelAndView.addObject("controller", "edocController.do");
    	modelAndView.addObject("appType", appType);
    	modelAndView.addObject("appName", ApplicationCategoryEnum.edoc.key());


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
    	ModelAndView modelAndView = new ModelAndView("edoc/list4Quote");

        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");

        ApplicationCategoryEnum appEnum = ApplicationCategoryEnum.edocSend;
        String coltype = request.getParameter("appType");
        if(Strings.isNotBlank(coltype)){
        	appEnum = ApplicationCategoryEnum.valueOf(Integer.parseInt(coltype));
        }

        List<EdocSummaryModel> queryList = this.edocManager.queryByCondition4Quote(appEnum, condition, textfield, textfield1);
        modelAndView.addObject("csList", queryList);

        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);
        modelAndView.addObject("colMetadata", colMetadata);

        Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);

        modelAndView.addObject("comImportanceMetadata", comImportanceMetadata);
        modelAndView.addObject("controller", "edocController.do");
        modelAndView.addObject("appType", coltype);

    	return modelAndView;
    }

    private void zcdbSupervise(HttpServletRequest request,HttpServletResponse response,EdocSummary edocSummary,boolean isNew,int state,boolean sendMessage) {
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
	        if(isNew)
	        	this.colSuperviseManager.save(1, edocSummary.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, date, Constant.superviseType.edoc.ordinal(), edocSummary.getId(),state,sendMessage, null);
	        else
	        	this.colSuperviseManager.update(1,edocSummary.getSubject(),superviseTitle,user.getId(),user.getName(), supervisors, ids, date, Constant.superviseType.edoc.ordinal(), edocSummary.getId(),state,sendMessage, null);
        }
	}

    private void saveColSupervise(HttpServletRequest request,HttpServletResponse response,EdocSummary edocSummary,boolean isNew,boolean sendMessage) throws Exception{
		String supervisorId = request.getParameter("supervisorId");
        String supervisors = request.getParameter("supervisors");
        String awakeDate = request.getParameter("awakeDate");
        if(supervisorId != null && !"".equals(supervisorId) && awakeDate != null && !"".equals(awakeDate)) {
        	User user = CurrentUser.get();
	        //boolean canModifyAwake = "on".equals(request.getParameter("canModifyAwake"))?true:false;
	        String title = request.getParameter("title");
	        if(Strings.isBlank(title)){
	        	title = request.getParameter("superviseTitle");
	        }
	        Date date = Datetimes.parse(awakeDate, Datetimes.dateStyle);
	        String[] idsStr = supervisorId.split(",");
	        long[] ids = new long[idsStr.length];
	        int i = 0;
	        for(String id:idsStr) {
	        	ids[i] = Long.parseLong(id);
	        	i++;
	        }
	        	edocSuperviseManager.superviseForTemplate("100", supervisorId, supervisors, awakeDate,edocSummary,title);
        }else if(supervisorId==null||"".equals(supervisorId)){//如果为空，删除督办。
        	if(edocSummary!=null&&edocSummary.getId()!=null){
        		edocSuperviseManager.deleteSuperviseDetailAndSupervisors(edocSummary);
        	}
        }
	}

	/**
	 * @param colSuperviseManager the colSuperviseManager to set
	 */
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}


	//公文查询模块Controller

	public ModelAndView edocSearchMain(HttpServletRequest request,HttpServletResponse response)throws Exception{

		ModelAndView mav = new ModelAndView("edoc/docSearch/edocSearchMain");
		//ModelAndView mav = new ModelAndView("edoc/docSearch/searchWhere");
		Metadata edocTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.edoc_doc_type);
        Metadata  sendTypeMetadata= metadataManager.getMetadata(MetadataNameEnum.edoc_send_type);
        mav.addObject("edocTypeMetadata", edocTypeMetadata);
        mav.addObject("sendTypeMetadata", sendTypeMetadata);
		return mav;
	}

	public ModelAndView edocSearchEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docSearch/edocQueryIndex");
		return mav;
	}

	public ModelAndView edocSearchWhere(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docSearch/searchWhere");
		Metadata edocTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.edoc_doc_type);
        Metadata  sendTypeMetadata= metadataManager.getMetadata(MetadataNameEnum.edoc_send_type);
        mav.addObject("edocTypeMetadata", edocTypeMetadata);
        mav.addObject("sendTypeMetadata", sendTypeMetadata);
		return mav;
	}

	public ModelAndView listEdocSearchReult(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docSearch/searchResult");
		long curUserId= CurrentUser.get().getId();
		EdocSearchModel em=new EdocSearchModel();
		bind(request, em);
		V3xOrgMember member = orgManager.getMemberById(curUserId);
		List<EdocSummaryModel> result=edocManager.queryByCondition(curUserId, em,true,member.getSecretLevel());//成发集团项目
		mav.addObject("result",result);
		mav.addObject("controller", "edocController.do");
		mav.addObject("edocType",em.getEdocType());

		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
		mav.addObject("colMetadata", colMetadata);

		return mav;
	}
	/**
	 * 导出公文查询结果到excel
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	 public ModelAndView exportQueryToExcel(HttpServletRequest request,HttpServletResponse response)throws Exception{

	    	// --  用于输出excel的标题 －－
	    	// --  start  --
			Locale local = LocaleContext.getLocale(request);
			String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
			String stat_title = ResourceBundleUtil.getString(resource, local, "edoc.query.tables.label"); //标题

//			String send_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.send.label"); //发文
//			String sign_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.sign.label"); //签报
//			String recieve_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.recieve.label"); //收文
//			String archivie_title = ResourceBundleUtil.getString(resource, local, "edoc.stat.tables.archive.label"); //归档
	    	// -- end --

			long curUserId= CurrentUser.get().getId();
			EdocSearchModel em=new EdocSearchModel();
			//bind(request, em);
			String edocType=request.getParameter("_oldEdocType");
			String subject=request.getParameter("_oldSubject");
			String keywords=request.getParameter("_oldKeywords");
			String docMark=request.getParameter("_oldDocMark");
			String serialNo=request.getParameter("_oldSerialNo");
			String docType=request.getParameter("_oldDocType");
			String sendType=request.getParameter("_oldSendType");
			String createPerson=request.getParameter("_oldCreatePerson");
			String createTimeB=request.getParameter("_oldCreateTimeB");
			String createTimeE=request.getParameter("_oldCreateTimeE");
			String sendTo=request.getParameter("_oldSendTo");
			String sendToId=request.getParameter("_oldSendToId");
			String sendUnit=request.getParameter("_oldSendUnit");
			String issuer=request.getParameter("_oldIssuer");
			String signingDateA=request.getParameter("_oldSigningDateA");
			String show=request.getParameter("show");

			if(edocType!=null && !"".equals(edocType))
				em.setEdocType(Integer.parseInt(edocType));
			em.setSubject(subject);
			em.setKeywords(keywords);
			em.setDocMark(docMark);
			em.setSerialNo(serialNo);
			em.setDocType(docType);
			em.setSendType(sendType);
			em.setCreatePerson(createPerson);
			if(createTimeB!=null && !"".equals(createTimeB))
				em.setCreateTimeB(Datetimes.parseDate(createTimeB));
			if(createTimeE!=null && !"".equals(createTimeE))
				em.setCreateTimeE(Datetimes.parseDate(createTimeE));
			em.setSendTo(sendTo);
			em.setSendToId(sendToId);
			em.setSendUnit(sendUnit);
			em.setIssuer(issuer);
			if(signingDateA!=null && !"".equals(signingDateA))
				em.setSigningDateA(Datetimes.parseDate(signingDateA));

			List<EdocSummaryModel> result=edocManager.queryByCondition(curUserId, em,false);//false：不需要分页
			if (Strings.isBlank(show))	result=null;


			//将查询结果组装成页面显示要用的形式
	    	DataRecord dataRecord = EdocHelper.exportQueryToWebModel(request, this.response(result),stat_title,em.getEdocType());
			OrganizationHelper.exportToExcel(request, response, fileToExcelManager, stat_title, dataRecord);

	    	return null;
	 }
	 /**
	  * 进行封装的方法，封装成发送到页面的数据
	  * @param list
	  * @return
	  */
    private List<EdocSummaryModel> response(List<EdocSummaryModel> list) throws Exception{
    	if(list!=null&&list.size()!=0){
    		Metadata  secretLeveleMetadata = metadataManager.getMetadata(MetadataNameEnum.edoc_secret_level);//得到公文密级的枚举
    		String secretLevel="";
    		for(EdocSummaryModel summary:list){
    			if(summary.getSummary().getSecretLevel()!=null){
        			secretLevel = this.getLabel(summary.getSummary().getSecretLevel(), secretLeveleMetadata) ;
        		}else{
        			secretLevel = this.getLabel(null, secretLeveleMetadata) ;
        		}
    			summary.getSummary().setSecretLevel(secretLevel);
    		}
    	}
    	return list;
    }
    private String getLabel(String itemValue,Metadata metadata){
		MetadataItem itms = metadata.getItem(itemValue);

		if (itms==null) return null;
		String label = null;
		if(itemValue != null) {

			if(Strings.isNotBlank(metadata.getResourceBundle())){ //在原数据中定义了resourceBundle
				label = ResourceBundleUtil.getString(metadata.getResourceBundle(), itms.getLabel());
			}

			if(label == null){
				return itms.getLabel();
			}
		}


		return label;
	}
    /**
     * 公文处理中预发布公告,新闻
     *
     * @author jincm 2008-5-29
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView preIssueNewsOrBull(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/preIssueNewsOrBull");
    	User user = CurrentUser.get();

    	List typeList = new ArrayList();
    	String policyName = request.getParameter("policyName");
    	if(policyName.equals("newsaudit")){
    		typeList = newsTypeManager.getTypesCanNew(user.getId(), null, user.getAccountId());
    	}else {
    		typeList = bulTypeManager.getTypesCanNew(user.getId(), null, user.getAccountId());
    		//添加可以发集团的公告(上面一行代码已经获取了全部可以发起公告的板块，spaceType为null时表明不区分空间，无需另外添加集团公告板块)
    		//typeList.addAll(bulTypeManager.getTypesCanNew(user.getId(), BulTypeSpaceType.group, user.getAccountId()));
    	}

    	if(typeList == null || typeList.size() == 0){
    		PrintWriter out = response.getWriter();
    		String msg = Constant.getString4CurrentUser("not.purview");
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
    	mav.addObject("notHasOutworker", 1);
    	mav.addObject("type", policyName);
    	mav.addObject("typeList", typeList);
    	mav.addObject("typeObj", typeList.get(0));
    	return mav;
    }
    /**
     * Ajax判断是否有发布公告、新闻的权限
     * @param policyName
     * @return true(有权限) false无权限
     * @throws Exception
     */

    public boolean AjaxjudgeHasPermitIssueNewsOrBull(String policyName)throws Exception{
    	User user = CurrentUser.get();
    	List typeList = new ArrayList();
    	if("newsaudit".equals(policyName)){
    		typeList = newsTypeManager.getTypesCanNew(user.getId(), null, user.getAccountId());
    	}else {
    		typeList = bulTypeManager.getTypesCanNew(user.getId(), null, user.getAccountId());
    	}
    	if(typeList==null||typeList.size()==0){
    		return false;//没有权限
    	}
    	return true;
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
    	String errMsg="";

    	try{
    	User user = CurrentUser.get();
    	String typeId = request.getParameter("typeId");
    	String summaryId = request.getParameter("summaryId");
    	String memberIdsStr = request.getParameter("memberIdsStr");

    	EdocSummary summary = edocManager.getEdocSummaryById(Long.parseLong(summaryId), true);
    	BulType type = this.bulTypeManager.getById(Long.parseLong(typeId));
    	BulData bean = new BulData();
    	bean.setIdIfNew();

    	EdocBody body = summary.getFirstBody();

		bean.setTitle(summary.getSubject());//标题
    	bean.setContent(body.getContent()==null?"":body.getContent());//正文
    	bean.setCreateDate(new Timestamp(System.currentTimeMillis()));
    	bean.setPublishDate(new Timestamp(System.currentTimeMillis()));
		bean.setCreateUser(user.getId());
		//bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		bean.setReadCount(0);
		bean.setTypeId(Long.parseLong(typeId));
		bean.setType(type);
		bean.setTopOrder(Byte.valueOf("0"));
		bean.setPublishUserId(user.getId());
		bean.setAccountId(user.getAccountId());
		bean.setDeletedFlag(false);
//		是否允许打印，如果允许打印，转发的公告Office正文才允许保存、打印
		bean.setExt2(request.getParameter("allowPrint"));
		//增加标识，表明此公告是由公文转发
		bean.setExt4(ApplicationCategoryEnum.edoc.name());

		bean.setDataFormat(body.getContentType());
		if(!com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(body.getContentType()))
		{
			//复制office对应的正文
			V3XFile f=fileManager.clone(Long.parseLong(body.getContent()),true);
			bean.setContent(f.getId().toString());
			//复制正文对应的印章
			SignetManager sm=(SignetManager)ApplicationContextHolder.getBean("signetManager");
			sm.insertSignet(Long.parseLong(body.getContent()), f.getId());
			bean.setContentName(body.getContent());
			bean.setExt5(request.getParameter("ext5"));
		}else{
			saveISignatureHTMLByDeleteAndSave(Long.parseLong(summaryId),bean.getId());
		}
		//bean.

		Long _summaryId = Long.parseLong(summaryId);
		Long beanId = bean.getId();
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
		/**
		operationlogManager.insertOplog(summary.getId(), ApplicationCategoryEnum.collaboration,
        		Constant.OperationLogActionType.finishItem.name(), "col.operationlog.issusBulletion", user.getName(), new java.util.Date(), summary.getSubject());
		**/
		this.processLogManager.insertLog(user, Long.valueOf(summary.getProcessId()), 1l, ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.forwardBulletin.getKey())) ;
		userMessageManager.sendSystemMessage(
				MessageContent.get("bul.auditing",
				bean.getTitle(),member.getName()),
				ApplicationCategoryEnum.bulletin,
				member.getId(),
				MessageReceiver.get(bean.getId(),bulAudits,"message.link.bul.alreadyauditing",String.valueOf(bean.getId())),
				bean.getTypeId());
    	}catch(Exception e)
    	{
    		log.error(e);
    		errMsg=Strings.escapeJavascript(e.getMessage());
    	}
    	PrintWriter out = response.getWriter();
    	out.println("<script>");
		if(null!=errMsg&&errMsg.equals(""))
		{
			out.println("parent.SendBulltinResult(\""+"公告发布成功"+"\");");
		}
		else
		{
			out.println("parent.SendBulltinResult(\""+errMsg+"\");");
		}
		out.println("</script>");

    	return null;
    }
	/**
	 * Ajax判断文号定义是否被删除，并且判断内部文号是否已经存在（除开公文自己本身占用的文号）
	 * @param definitionId    下拉选择传进来的是文号定义ID，断号选择传进来的是edoc_mark表的ID
	 * @param serialNo		  内部文号
	 * @return  deleted（0：已删除  1：未删除）  exsit(0:不存在  1：存在)
	 */
	public String checkEdocMark(Long definitionId,String serialNo,Integer selectMode,String summaryId){
		int deleted=1;
		int exsit=0;
		//手动输入的时候不判断文号定义。
		if(definitionId!=null && definitionId.longValue()!=0){
			Long id=0L;
			if(selectMode==2){//断号选择
				EdocMark edocMark=edocMarkManager.getEdocMark(definitionId);
				if(edocMark!=null){
					EdocMarkDefinition definition=edocMark.getEdocMarkDefinition();
					if(definition!=null){
						id=definition.getId();
					}
				}
			}else{
				id=definitionId;
			}
			//判断文号定义是否已经删除
			 deleted=edocMarkDefinitionManager.judgeEdocDefinitionExsit(id);
		}
		 //判断内部文号是否已经存在
		if(serialNo!=null &&!"".equals(serialNo)){
			User user = CurrentUser.get();
			exsit=edocSummaryManager.checkSerialNoExsit(summaryId,serialNo,user.getLoginAccount());
		}

		return deleted+","+exsit;
	}
	/**
	 * 判断当前公文文号是否已经被占用（除开公文自己本身占用的文号）
	 * @param edocSummaryId  :公文ID
	 * @param serialNo		 :内部文号
	 * @return 0:不存在，1:已存在
	 */
	public String checkSerialNoExcludeSelf(String edocSummaryId,String serialNo){
		User user=CurrentUser.get();
		int exsit=edocSummaryManager.checkSerialNoExsit(edocSummaryId,serialNo,user.getLoginAccount());
		return String.valueOf(exsit);
	}

	/**
	 * 拟文人Ajax更新附件，记录日志，发送消息
	 * @return
	 */
    public ModelAndView updateAttachment(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
   		User user = CurrentUser.get();
		String id = request.getParameter("edocSummaryId");
		String affairId=request.getParameter("affairId");
		Long _affairId=0L;
		EdocSummary edocSummary = new EdocSummary();
		Affair affair=new Affair();
		String ret="SUCCESS";
		if (id != null && !"".equals(id)) {
			edocSummary = edocSummaryManager.findById(Long.parseLong(id));
		}
		if(affairId!=null && !"".equals(affairId)){
			affair= affairManager.getById(Long.parseLong(affairId));
		}

		try{
			this.edocManager.updateAttachment(edocSummary,affair,user,request);
		} catch (Exception e) {
			log.error("修改正文附件异常", e);
			ret="";
		}
		response.getWriter().write(ret);
		return null;
    }
	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	private String getOpinionName(String category , long accountId)throws Exception{
		List<FlowPerm> list = flowPermManager.getFlowPermsByCategory(category, accountId);
		String opinions = "";
		for(FlowPerm perm : list){
			opinions += perm.getName() + ",";
		}
		if(opinions.endsWith(",")){
			opinions = opinions.substring(0, opinions.lastIndexOf(","));
		}
		return opinions;
	}

    private  String unescapeHTMLToString(String str){
    	if(null==str||str.equals("")){
    		return "";
    	}
    	str = str.replace("&amp;","&");
    	str = str.replace("&lt;","<");
    	str = str.replace("&gt;",">");
    	str = str.replace("<br>","");
    	str = str.replace("&#039;","\'");
    	str = str.replace("&#034;","\"");

    	return str;
    }
    /**
     * 得到公文的所属部门的ID
     * 在主单位下，取主部门为公文所属部门，由于系统无法识别他由主岗发文还是由副岗发文，鉴于概率低，就取主岗部门了。
	 * 在兼职单位下，取多个兼职部门中的一个（按排序号，兼职序号在前的哪个部门 ）为公文所属部门。
	 * @param  accoutId :公文所属单位
     * @return  公文所属部门ID
     */
    private Long getEdocOwnerDepartmentId(Long accoutId){
    	User user=CurrentUser.get();
    	long currentDeptId=user.getDepartmentId();
    	if(accoutId!=user.getAccountId()){
    		try {
				Map<Long, List<ConcurrentPost>> map=orgManager.getConcurentPostsByMemberId(accoutId, user.getId());
				long min=-1;

				for(Long deptId:map.keySet()){
					List<ConcurrentPost> list=map.get(deptId);
					for(ConcurrentPost concurrentPost:list){
						if(min==-1) min=concurrentPost.getNumber();
						if(concurrentPost.getNumber()<min){
							min=concurrentPost.getNumber();
							currentDeptId=deptId;
						}
					}
				}
    		} catch (BusinessException e) {
				logger.error("公文所属部门判断异常:", e);
			}
    	}
    	return currentDeptId;
    }
	private String getName(String ids,String seperator) throws Exception{
		 String[] sids = ids.split("[,]");
		 String name = "";
		 for(String sid : sids){
			 String[] std =  sid.split("[|]");
			 Long acccountId = Long.valueOf(std[1]);
			 if("".equals(name)){
				 name = EdocRoleHelper.getAccountById(acccountId).getName();
			 }else{
				 name += seperator+EdocRoleHelper.getAccountById(acccountId).getName();
			 }
		 }
		 return name;
	}
	private String getI18nSeperator(HttpServletRequest request){
		String seperator = "、";
		try{
			Locale locale =Functions.getLocale(request);
			String	sep   = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", locale,"common.separator.label");
			if(Strings.isNotBlank(sep)) seperator = sep;
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}
		return seperator;
	}
	/**
	 * 判读公文是否已经被发送
	 *
	 * @param summaryId
	 *
	 * @return
	 */
	private boolean isBeSended(Long summaryId) {
		// 处理方法，使用affair提供的objectid查询词objectid下是否有app=22 and state=4的已发送记录
		// 有，则返回true
		StringBuffer qryHql = new StringBuffer();
		List<Object> list = new ArrayList<Object>();
//		list.add(ApplicationCategoryEnum.exSend.key());
//		list.add(StateEnum.edoc_exchange_sent.key());
		list.add(summaryId);
	//	list.add(false);
		qryHql.append("from EdocSendDetail e,EdocSendRecord r where e.sendRecordId=r.id and r.edocId = ? ");
		int queryCount = affairManager.getAffairQueryCount(qryHql.toString(),
				list.toArray(),
						new Type[] { Hibernate.LONG});
		if (queryCount > 0) {
			return true;
		} else {
			return false;
		}


	}

    /**
     * 解锁，公文提交或者暂存待办的时候进行解锁,与Ajax解锁一起，构成两次解锁，避免解锁失败，节点无法修改的问题出现
     * @param userId
     * @param summaryId
     */
    private void unLock(Long userId,EdocSummary summary){
    	if(summary == null) return ;
    	String bodyType = summary.getFirstBody().getContentType();
    	long summaryId = summary.getId();

    	if(Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(bodyType)||
    			Constants.EDITOR_TYPE_OFFICE_WORD.equals(bodyType)||
    			Constants.EDITOR_TYPE_WPS_EXCEL.equals(bodyType)||
    			Constants.EDITOR_TYPE_WPS_WORD.equals(bodyType)){
	    	//1、解锁office正文
    		try{
    			String contentId = summary.getFirstBody().getContent();

	    		handWriteManager.deleteUpdateObj(contentId);
	    	}catch(Exception e){
	    		log.error("解锁office正文失败 userId:"+userId+" summaryId:"+summary.getId(),e);
	    	}
    	}else{
	    	//2、解锁html正文
	    	try{
	    		handWriteManager.deleteUpdateObj(String.valueOf(summaryId));
	    	}catch(Exception e){
	    		log.error("解锁html正文失败 userId:"+userId+" summaryId:"+summaryId ,e);
	    	}
    	}
    	//3、解锁公文单
    	try{
    		edocSummaryManager.deleteUpdateObj(String.valueOf(summaryId), String.valueOf(userId));
    	}catch(Exception e){
    		log.error("解锁公文单失败 userId:"+userId+" summaryId:"+summaryId,e);
    	}
    }


	public ProcessLogManager getProcessLogManager() {
		return processLogManager;
	}

	public void setProcessLogManager(ProcessLogManager processLogManager) {
		this.processLogManager = processLogManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}


	public EdocMarkDefinitionManager getEdocMarkDefinitionManager() {
		return edocMarkDefinitionManager;
	}

	public void setEdocMarkDefinitionManager(
			EdocMarkDefinitionManager edocMarkDefinitionManager) {
		this.edocMarkDefinitionManager = edocMarkDefinitionManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	/**
     * Portal事件来源指定分类
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author lilong 2012-01-17
     */
    public ModelAndView showPortalCatagory(HttpServletRequest request,HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("collaboration/showPortalCatagory4Edoc");
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
    	ModelAndView mav = new ModelAndView("collaboration/showPortalImportLevel4Edoc");
    	return mav;
    }
	/**
	 * @param edocManagerFacade the edocManagerFacade to set
	 */
	public void setEdocManagerFacade(EdocManagerFacade edocManagerFacade) {
		this.edocManagerFacade = edocManagerFacade;
	}
}