
package com.seeyon.v3x.doc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
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
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.blog.manager.BlogArticleManager;
import com.seeyon.v3x.blog.manager.BlogManager;
import com.seeyon.v3x.blog.webmodel.ArticleModel;
import com.seeyon.v3x.blog.webmodel.AttentionModel;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.encrypt.CoderFactory;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.office.trans.manager.OfficeTransManager;
import com.seeyon.v3x.common.office.trans.util.OfficeTransHelper;
import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.common.rss.manager.RssManager;
import com.seeyon.v3x.common.rss.webmodel.ChannelInfoVo;
import com.seeyon.v3x.common.search.manager.SearchManager;
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
import com.seeyon.v3x.common.web.security.qs.EncoderQueryString;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocAlert;
import com.seeyon.v3x.doc.domain.DocAlertLatest;
import com.seeyon.v3x.doc.domain.DocBody;
import com.seeyon.v3x.doc.domain.DocFavorite;
import com.seeyon.v3x.doc.domain.DocForum;
import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocLearningHistory;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocMetadataOption;
import com.seeyon.v3x.doc.domain.DocMimeType;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.doc.domain.PotentModel;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.manager.ContentTypeManager;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocAlertLatestManager;
import com.seeyon.v3x.doc.manager.DocAlertManager;
import com.seeyon.v3x.doc.manager.DocFavoriteManager;
import com.seeyon.v3x.doc.manager.DocForumManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManagerImpl;
import com.seeyon.v3x.doc.manager.DocLearningManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.manager.DocMetadataManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.manager.DocVersionInfoManager;
import com.seeyon.v3x.doc.manager.HtmlUtil;
import com.seeyon.v3x.doc.manager.MetadataDefManager;
import com.seeyon.v3x.doc.util.ActionType;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.DocMVCUtils;
import com.seeyon.v3x.doc.util.PigUrlInfo;
import com.seeyon.v3x.doc.util.Constants.LockStatus;
import com.seeyon.v3x.doc.webmodel.DocAclVO;
import com.seeyon.v3x.doc.webmodel.DocAlertAdminVO;
import com.seeyon.v3x.doc.webmodel.DocAlertLatestVO;
import com.seeyon.v3x.doc.webmodel.DocBorrowVO;
import com.seeyon.v3x.doc.webmodel.DocCheckOutVO;
import com.seeyon.v3x.doc.webmodel.DocEditVO;
import com.seeyon.v3x.doc.webmodel.DocFavoriteVO;
import com.seeyon.v3x.doc.webmodel.DocForumReplyVO;
import com.seeyon.v3x.doc.webmodel.DocForumVO;
import com.seeyon.v3x.doc.webmodel.DocLearningHistoryVO;
import com.seeyon.v3x.doc.webmodel.DocLearningVO;
import com.seeyon.v3x.doc.webmodel.DocLibTableVo;
import com.seeyon.v3x.doc.webmodel.DocLinkVO;
import com.seeyon.v3x.doc.webmodel.DocOpenBodyVO;
import com.seeyon.v3x.doc.webmodel.DocPersonalShareVO;
import com.seeyon.v3x.doc.webmodel.DocPropVO;
import com.seeyon.v3x.doc.webmodel.DocSearchModel;
import com.seeyon.v3x.doc.webmodel.DocSortProperty;
import com.seeyon.v3x.doc.webmodel.DocTableVO;
import com.seeyon.v3x.doc.webmodel.DocTreeVO;
import com.seeyon.v3x.doc.webmodel.FolderItem;
import com.seeyon.v3x.doc.webmodel.FolderItemDoc;
import com.seeyon.v3x.doc.webmodel.FolderItemFolder;
import com.seeyon.v3x.doc.webmodel.GridVO;
import com.seeyon.v3x.doc.webmodel.ListDocLog;
import com.seeyon.v3x.doc.webmodel.SimpleDocQueryModel;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.main.section.panel.SectionPanel;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.SetContentType;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.manager.MailBoxManager;
import com.seeyon.v3x.webmail.manager.WebMailManager;

/**
 * 文档管理中权限、结构相关的controller
 */
@SuppressWarnings("deprecation")
public class DocController extends BaseController {
	private static final Log log = LogFactory.getLog(DocController.class);
	private SearchManager searchManager ;
	private ProjectPhaseEventManager projectPhaseEventManager;
	private DocHierarchyManager docHierarchyManager;
	private UserMessageManager userMessageManager;
	private OrgManager orgManager;
	private ContentTypeManager contentTypeManager;
	private DocAclManager docAclManager;
	private DocMimeTypeManager docMimeTypeManager;
	private OperationlogManager operationlogManager;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private PartitionManager partitionManager;
	private DocForumManager docForumManager;
	private DocMetadataManager docMetadataManager;
	private DocAlertManager docAlertManager;
	private AffairManager affairManager;
	private MetadataDefManager metadataDefManager;
	private FileToExcelManager fileToExcelManager;
	private DocFavoriteManager docFavoriteManager;
	private DocAlertLatestManager docAlertLatestManager;
	private RssManager rssManager;
	private CollaborationController collaborationController;
	private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;
	private WebMailManager webMailManager;
	private DocLearningManager docLearningManager;
	private DocLibManager docLibManager;
	private HtmlUtil htmlUtil;
	private String jsonView;
	private BlogArticleManager blogArticleManager;
	private BlogManager blogManager;
	private SpaceManager spaceManager;
	private MetadataManager metadataManager;
	private EdocSummaryManager edocSummaryManager;
	private AppLogManager appLogManager;
	private DocVersionInfoManager docVersionInfoManager;
	private OfficeTransManager officeTransManager;
	private PortletEntityPropertyManager portletEntityPropertyManager;
	    
	private Set<String> typesShowContentDirectlyTEXT = new HashSet<String>();
	private Set<String> typesShowContentDirectlyHTML = new HashSet<String>();
	/** 上传后直接查看内容的TXT和HTML、HTM等格式文件的大小上限 */
	private int maxSize4ShowContent;

	/** 文档中心首页框架  */
	public ModelAndView docIndex(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/index");
		Long userId = CurrentUser.get().getId();
		// 是否从文档夹的打开转过来，如果是，说明需要提醒用户，原来需要打开的文档夹不存在了
		boolean alertNotExist = request.getParameter("docResId") != null; 
		boolean isPersonalLib = String.valueOf(Constants.PERSONAL_LIB_TYPE).equals(request.getParameter("openLibType"));
		DocLib lib = null;
		if (isPersonalLib)
			lib = this.docLibManager.getPersonalLibOfUser(userId);
		else
			lib = this.docLibManager.getDeptLibById(CurrentUser.get().getLoginAccount());
		
		Long openLibId = lib.getId();
		DocResource dr = docHierarchyManager.getRootByLibId(lib.getId());
		dr.setIsMyOwn(isPersonalLib);
		
		DocTreeVO vo = this.getDocTreeVO(userId, dr, isPersonalLib);
		ret.addObject("docLibId", openLibId).addObject("root", vo).addObject("alertNotExist", alertNotExist);
		return ret.addObject("docLibType", isPersonalLib ? Constants.PERSONAL_LIB_TYPE : Constants.ACCOUNT_LIB_TYPE);
	}

	private DocTreeVO getDocTreeVO(Long userId, DocResource dr, boolean isPersonalLib) {
		return DocMVCUtils.getDocTreeVO(userId, dr, isPersonalLib, docMimeTypeManager, docAclManager);
	}

	/** 文档中心首页框架  */
	public ModelAndView docIndexNew(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/indexNew");
	}

	/** 从首页进入框架  */
	public ModelAndView docHomepageIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView ret = new ModelAndView("doc/index");

		Long userId = CurrentUser.get().getId();
		String sdocId = request.getParameter("docResId");
		String sprojectId = request.getParameter("projectId");
		long docResId = 0L;
		if (Strings.isNotBlank(sdocId))
			docResId = Long.valueOf(sdocId);
		else if (Strings.isNotBlank(sprojectId)) {
			long projectId = Long.valueOf(sprojectId);
			DocResource projectFolder = docHierarchyManager.getProjectFolderByProjectId(projectId);
			if (projectFolder != null)
				docResId = projectFolder.getId();
		}
		DocResource parent = docHierarchyManager.getDocResourceById(docResId);

		// 源文档夹的是否存在判断
		if (parent == null) {
			String parentId = request.getParameter("parentId");
			if (Strings.isNotBlank(parentId)) {
				docResId = Long.valueOf(parentId);
				parent = docHierarchyManager.getDocResourceById(docResId);
			}
			ret.addObject("alertNotExist", true);
		}
		
		if(parent == null) {
			return super.redirectModelAndView("/doc.do?method=docIndex&docResId=" + sdocId);
		}

		DocLib lib = docLibManager.getDocLibById(parent.getDocLibId());
		if(lib == null || lib.isDisabled()) {
			PrintWriter out = response.getWriter();
			out.println("<script>" +
						"	alert('" + Constants.getDocI18nValue("doc.lib.disabled.alert") + "');" +
						"</script>");
			out.flush();
			return this.docLibsConfig(request, response);
		}
		
		boolean isPersonalLib = lib.isPersonalLib();
		if (isPersonalLib) {
			boolean ownerOfLib = docLibManager.isOwnerOfLib(userId, lib.getId());
			if (ownerOfLib)
				parent.setIsMyOwn(true);
		}

		DocTreeVO vo = this.getDocTreeVO(userId, parent, isPersonalLib);

		ret.addObject("docLibId", lib.getId()).addObject("docLibType", lib.getType());
		ret.addObject("root", vo).addObject("id", parent.getId()).addObject("frType", parent.getFrType());
		boolean isShareAndBorrowRoot = BooleanUtils.toBoolean(request.getParameter("isShareAndBorrowRoot"));
		ret.addObject("shareOrBorrowFlag", isShareAndBorrowRoot);
		return ret;
	}

	/** 从首页进入框架  */
	public ModelAndView docHomepageShareIndex(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/index");

		Long userId = CurrentUser.get().getId();
		Long ownerId = Long.valueOf(request.getParameter("ownerId"));
		DocLib lib = docLibManager.getOwnerDocLibByUserId(userId);

		ret.addObject("id", ownerId);
		ret.addObject("frType", Constants.PERSON_SHARE);
		ret.addObject("docLibType", Constants.PERSONAL_LIB_TYPE);
		ret.addObject("docLibId", lib.getId());
		ret.addObject("shareOrBorrowFlag", true);

		return ret;
	}
	
	/** 顶部菜单  */
	@Deprecated
	public ModelAndView docMenu(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docMenu");

		// 获得工具栏右上角查询条件的类型列表
		List<DocType> types = contentTypeManager.getAllSearchContentType();
		mav.addObject("types", types);

		Long docLibId = Long.valueOf(request.getParameter("docLibId"));
		DocLib docLib = docLibManager.getDocLibById(docLibId);
		boolean folderEnabled = docLib.getFolderEnabled();
		boolean a6Enabled = docLib.getA6Enabled();
		boolean officeEnabled = docLib.getOfficeEnabled();
		boolean uploadEnabled = docLib.getUploadEnabled();

		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isPrivateLib = docLibType.equals(Constants.PERSONAL_LIB_TYPE);

		mav.addObject("isGroupLib", (docLibType.byteValue() == Constants.GROUP_LIB_TYPE.byteValue()));
		mav.addObject("isPrivateLib", isPrivateLib);
		mav.addObject("isEdocLib", (docLibType.byteValue() == Constants.EDOC_LIB_TYPE.byteValue()));
		mav.addObject("folderEnabled", folderEnabled);
		mav.addObject("a6Enabled", a6Enabled);
		mav.addObject("officeEnabled", officeEnabled);
		mav.addObject("uploadEnabled", uploadEnabled);

		Long parentId = Long.valueOf(request.getParameter("resId"));
		DocResource parent = docHierarchyManager.getDocResourceById(parentId);
		mav.addObject("parent", parent);

		return mav;
	}

	/** 左侧树框架  */
	public ModelAndView docTreeIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docTreeIframe");
	}

	/** 判断是否具有推送到集团首页的权限   */
	private boolean canAdminGroup() {
		DocLib lib = docLibManager.getGroupDocLib();
		if(lib == null)
			return false;
		
		boolean isGroupAdmin = false;
		try {
			isGroupAdmin = orgManager.isGroupAdminRole(CurrentUser.get().getId());
		} catch (BusinessException e) {
			log.error("通过orgManager判断当前用户是否具有集团知识管理权限", e);
		}
		return isGroupAdmin;
	}

	// 弹出树框架
	public ModelAndView docTreeMoveIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docTreeMoveIframe");
	}

	// 左侧树上的标签
	@Deprecated
	public ModelAndView docTreeLable(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docTreeLable");
	}

	// 左侧树
	public ModelAndView docTree(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docTree");
	}

	/** 弹出新建文件夹窗口  */
	public ModelAndView createF(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/createF");
	}

	/** 左侧树  */
	public ModelAndView xmlJsp(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		Long userId = CurrentUser.get().getId();
		Long parentId = Long.valueOf(request.getParameter("resId"));
		Long frType = Long.valueOf(request.getParameter("frType"));
		DocResource docRes = docHierarchyManager.getDocResourceById(parentId);

		// 对于资源是否存在的判断
		if (frType != Constants.PERSON_BORROW && frType != Constants.PERSON_SHARE 
				&& frType != Constants.DEPARTMENT_BORROW && docRes == null) {
			out.println("<exist>no</exist>");
			return null;
		}

		Long docLibId = null;
		DocLib docLib = null;
		Byte docLibType = null;

		if (frType == Constants.PERSON_BORROW || frType == Constants.PERSON_SHARE || frType == Constants.DEPARTMENT_BORROW) {
			docLib = this.docLibManager.getPersonalLibOfUser(userId);
			docLibId = docLib.getId();
			docLibType = Constants.PERSONAL_LIB_TYPE;
		} 
		else {
			docLibId = docRes.getDocLibId();
			docLib = docLibManager.getDocLibById(docLibId);
			docLibType = docLib.getType();
		}

		boolean isShareAndBorrowRoot = BooleanUtils.toBoolean(request.getParameter("isShareAndBorrowRoot"));

		List<DocResource> drs = null;
		if (docLib.isPersonalLib()) {
			drs = docHierarchyManager.findFolders(parentId, frType, userId, "", true);
		} 
		else {
			String orgIds = Constants.getOrgIdsOfUser(userId);
			drs = docHierarchyManager.findFolders(parentId, frType, userId, orgIds, false);
		}
		
		if (CollectionUtils.isEmpty(drs))
			return null;
		
		List<DocTreeVO> folders = new ArrayList<DocTreeVO>();
		for (DocResource dr : drs) {
			long type = dr.getFrType();
			// 我的计划判断
			if (type == Constants.FOLDER_PLAN
					|| type == Constants.FOLDER_PLAN_DAY
					|| type == Constants.FOLDER_PLAN_MONTH
					|| type == Constants.FOLDER_PLAN_WEEK
					|| type == Constants.FOLDER_PLAN_WORK) {
				boolean hasPlan = Constants.hasMenuMyPlanOfCurrentUser();
				if (!hasPlan)
					continue;
			}

			boolean isShareAndBorrowRoot_ = isShareAndBorrowRoot;

			if (type == Constants.FOLDER_BORROW
					|| type == Constants.FOLDER_SHAREOUT
					|| type == Constants.FOLDER_BORROWOUT
					|| type == Constants.FOLDER_SHARE
					|| type == Constants.PERSON_BORROW
					|| type == Constants.PERSON_SHARE
					|| type == Constants.DEPARTMENT_BORROW
					|| type == Constants.FOLDER_TEMPLET
					|| type == Constants.FOLDER_PLAN
					|| type == Constants.FOLDER_PLAN_DAY
					|| type == Constants.FOLDER_PLAN_MONTH
					|| type == Constants.FOLDER_PLAN_WEEK
					|| type == Constants.FOLDER_PLAN_WORK)
				isShareAndBorrowRoot_ = true;

			DocTreeVO vo = new DocTreeVO(dr);
			vo.setDocLibType(docLibType);
			// 设置是否需要国际化标记
			DocMVCUtils.setNeedI18nInVo(vo);
			if (docLibType.byteValue() == Constants.PERSONAL_LIB_TYPE.byteValue())
				dr.setIsMyOwn(true);
			this.setGottenAclsInVO(vo, userId, isShareAndBorrowRoot_);
			if (type == Constants.PERSON_BORROW
					|| type == Constants.PERSON_SHARE
					|| type == Constants.DEPARTMENT_BORROW
					|| type == Constants.FOLDER_SHAREOUT
					|| type == Constants.FOLDER_BORROWOUT) {
				vo.setOpenIcon(Constants.PERSON_ICON);
				vo.setCloseIcon(Constants.PERSON_ICON);
			} else {
				String srcIcon = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon();
				vo.setOpenIcon(srcIcon.substring(srcIcon.indexOf('|') + 1, srcIcon.length()));
				vo.setCloseIcon(srcIcon.substring(0, srcIcon.indexOf('|')));
			}
			folders.add(vo);
		}

		out.println("<tree text=\"loaded\">");
		String xmlstr = DocMVCUtils.getXmlStr4LoadNodeOfCommonTree(docLibId, folders);
		out.println(xmlstr);
		out.println("</tree>");
		out.close();
		return null;
	}

	/** 弹出树  */
	public ModelAndView xmlJspMove(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		Long userId = CurrentUser.get().getId();
		String sparentId = request.getParameter("resId");
		Long parentId = Long.valueOf(sparentId);
		DocResource parent = docHierarchyManager.getDocResourceById(parentId);

		// 对于资源是否存在的判断
		if (parent == null) {
			out.println("<exist>no</exist>");
			return null;
		}

		DocLib lib = docLibManager.getDocLibById(parent.getDocLibId());

		String stype = request.getParameter("frType");
		Long frType = Long.valueOf(stype);

		List<DocResource> drs = null;
		String validAcls = request.getParameter("validAcl");
		boolean validAcl = true;
		if (validAcls != null && "false".equals(validAcls))
			validAcl = false;
		
		boolean isPersonalLib = lib.isPersonalLib();
		if (isPersonalLib
				|| CurrentUser.get().isAdministrator() // 单位管理员登录
				|| (!validAcl)) { // 预归档，不要求验证权限
			drs = docHierarchyManager.findFolders(parentId, frType, userId, "", true);
		} 
		else {
			String orgIds = Constants.getOrgIdsOfUser(userId);
			drs = docHierarchyManager.findFolders(parentId, frType, userId, orgIds, false);
		}


		if ((drs == null) || (drs.size() == 0))
			return null;
		
		List<DocTreeVO> folders = new ArrayList<DocTreeVO>();
		for (DocResource dr : drs) {
			if ((dr.getFrType() == Constants.FOLDER_PLAN)
					|| (dr.getFrType() == Constants.FOLDER_SHAREOUT)
					|| (dr.getFrType() == Constants.FOLDER_TEMPLET)
					|| (dr.getFrType() == Constants.FOLDER_SHARE)
					|| (dr.getFrType() == Constants.FOLDER_BORROW)) {
				continue;
			} 
			else {
				if (isPersonalLib)
					dr.setIsMyOwn(true);

				DocTreeVO vo = this.getDocTreeVO(userId, dr, isPersonalLib);
				folders.add(vo);
			}
		}

		out.println("<tree text=\"loaded\">");
		String otherAccountShortName = DocMVCUtils.getOtherAccountShortName(lib, orgManager);
		String xmlstr = DocMVCUtils.getXmlStr4LoadNodeOfMoveTree(lib, folders, otherAccountShortName);
		out.println(xmlstr);
		out.println("</tree>");
		out.close();
		return null;
	}

	/**
	 * 集中加载文档列表想要的权限数据
	 */
	public void initRightAclData(HttpServletRequest request, ModelAndView mav) {
		boolean isAdministrator = Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_ACCOUNTADMIN, CurrentUser.get());
		boolean isGroupAdmin = this.canAdminGroup();
		int depAdminSize = DocMVCUtils.getDepSetAdmin(this.spaceManager).size();

		// 三级当前位置显示
		String slibid = request.getParameter("docLibId");
		boolean isLibOwner = false;
		if (Strings.isNotBlank(slibid)) {
			long libId = Long.valueOf(slibid);
			isLibOwner = docLibManager.isOwnerOfLib(CurrentUser.get().getId(), libId);
		}

		mav.addObject("isLibOwner", isLibOwner);
		mav.addObject("depAdminSize", depAdminSize);
		mav.addObject("isAdministrator", isAdministrator);
		mav.addObject("isGroupAdmin", isGroupAdmin);
	}
	//成发集团项目 程炯 2012-9-20 弹出文档密级设置窗口
	public ModelAndView setDocSecretLevel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("/doc/docSecretLevelChange");
		String docResId = request.getParameter("docResId");	
		DocResource doc = docHierarchyManager.getDocResourceById(Long.parseLong(docResId));
		mav.addObject("docResId", docResId);
		mav.addObject("currentSecretLevel", doc.getSecretLevel());
		Integer userSecretLevel = orgManager.getMemberById(CurrentUser.get().getId()).getSecretLevel();
		mav.addObject("userSecretLevel",userSecretLevel);
		return mav;
	}
	//成发集团项目 程炯 2012-9-20 修改文档密级
	public ModelAndView changeDocSecretLevel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String docResId = request.getParameter("docResId");
		String secretLevel = request.getParameter("secretLevel");
		DocResource doc = docHierarchyManager.getDocResourceById(Long.parseLong(docResId));
		if(null != doc && null != docResId && null != secretLevel && !"".equals(secretLevel) && !"".equals(docResId))
		docHierarchyManager.updateDocSecretLevel(Long.parseLong(docResId), Integer.parseInt(secretLevel));
		super.rendJavaScript(response,  "var rv = true;" +
				   						"parent.window.returnValue = rv;" +
				   						"parent.close();");
		return null;
	}
	
	/**
	 * 新的right界面，三个(navigation, menu, list)合并
	 */
	public ModelAndView rightNew(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView ret = new ModelAndView("/doc/rightNew");
		Long docLibId = Long.valueOf(request.getParameter("docLibId"));
		DocLib docLib = docLibManager.getDocLibById(docLibId);
		if (docLib == null || docLib.isDisabled()) {
			String key = docLib == null ? "doc_source_folder_no_exist" : "doc_lib_disabled";
			return super.redirectModelAndView("/doc.do?method=docIndex&openLibType=1&docLibAlert=" + key, "parent");
		}

		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		Long frType = Long.valueOf(request.getParameter("frType"));
		Long folderId = Long.valueOf(request.getParameter("resId"));
		DocResource folder = this.getParenetDocResource(folderId, frType);
		ret.addObject("parent", folder);
		
		// 对于资源是否存在的判断
		if (frType != Constants.PERSON_BORROW && frType != Constants.PERSON_SHARE && frType != Constants.DEPARTMENT_BORROW) {
			if (folder == null) {
				super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));" + 
											   "parent.location.reload(true);");
				return null;
			}
		}

		// 获得工具栏右上角查询条件的类型列表
		this.initRightAclData(request, ret);
		
		Long userId = CurrentUser.get().getId();
		boolean isShareAndBorrowRoot = BooleanUtils.toBoolean(request.getParameter("isShareAndBorrowRoot"));
		if (frType == Constants.FOLDER_BORROW
				|| frType == Constants.FOLDER_SHAREOUT
				|| frType == Constants.FOLDER_BORROWOUT
				|| frType == Constants.FOLDER_SHARE
				|| frType == Constants.PERSON_BORROW
				|| frType == Constants.PERSON_SHARE
				|| frType == Constants.DEPARTMENT_BORROW
				|| frType == Constants.FOLDER_TEMPLET
				|| frType == Constants.FOLDER_PLAN
				|| frType == Constants.FOLDER_PLAN_DAY
				|| frType == Constants.FOLDER_PLAN_MONTH
				|| frType == Constants.FOLDER_PLAN_WEEK
				|| frType == Constants.FOLDER_PLAN_WORK) {
			isShareAndBorrowRoot = true;
		}

		// 取得本页应该显示的DocResource对象
		List<DocResource> drs = new ArrayList<DocResource>();
		if (frType == Constants.FOLDER_SHAREOUT || frType == Constants.FOLDER_BORROWOUT) {
			Long parentFrId = folder.getParentFrId();
			drs = docHierarchyManager.findAllDocsByPage(parentFrId, frType, userId);
		} 
		else {
			String queryFlag = request.getParameter("queryFlag");
			if(BooleanUtils.toBoolean(queryFlag)) {
				String pingHoleSelect = request.getParameter("pingHoleSelect");
				if(Strings.isNotBlank(pingHoleSelect)) {
					int pingHoleSelectFlag = Integer.valueOf(pingHoleSelect);
					drs = docHierarchyManager.findAllDocsByPage(folderId, frType, userId, pingHoleSelectFlag);
				}
			} else {
 				drs = docHierarchyManager.findAllDocsByPage(folderId, frType, userId);
			}
		}

		boolean isNeedSort = drs != null && drs.size() > 1;
		ret.addObject("isNeedSort", isNeedSort);
		// 根据 docLibId 得到有序的栏目元数据列表
		List<DocMetadataDefinition> dmds = docLibManager.getListColumnsByDocLibId(docLibId);
		List<DocTableVO> docs = this.getTableVOs(drs, dmds, ret, userId, isShareAndBorrowRoot, docLibType, folder, false);
		//成发集团项目 程炯 2012-9-21 加入文档密级显示列 begin		
			for (DocTableVO docTableVO : docs) {
				if(docTableVO.getFrType()!=3&&docTableVO.getFrType()!=32&&docTableVO.getFrType()!=43&&docTableVO.getFrType()!=44&&docTableVO.getFrType()!=45&&docTableVO.getFrType()!=46){
					List<GridVO> listGrid = docTableVO.getGrids();
					GridVO gridVo = new GridVO();
					gridVo.setTitle("文档密级");
					gridVo.setIsImg(false);
					gridVo.setNeedI18n(false);
					gridVo.setPercent(10);
					gridVo.setAlign("center");
					gridVo.setIsSize(false);
					gridVo.setIsName(false);
					listGrid.add(gridVo);
					docTableVO.setGrids(listGrid);
				}
			}
		//end
		ret.addObject("resId", folderId);
		ret.addObject("frType", frType);
		
		DocMVCUtils.returnVaule(ret, docLibType, docLib, request, this.contentTypeManager, this.docLibManager);
		
		ret.addObject("isShareAndBorrowRoot", isShareAndBorrowRoot);
		ret.addObject("docs", docs);
		// 正常进入列表标记，区别于查询进入
		ret.addObject("from", "listDocs");
		List<Long> ownerSet = DocMVCUtils.getLibOwners(folder);
		boolean isOwner = ownerSet != null && ownerSet.contains(userId);
		return ret.addObject("isOwner", isOwner);
	}
	
	public ModelAndView sortPropertyIframe(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("/doc/sortPageIframe");
	}
	 
	/**
	 * 获得需要排序的文档
	 */
	public ModelAndView sortProperty(HttpServletRequest request,HttpServletResponse response){
		ModelAndView view = new ModelAndView("/doc/sortPage");
		Long sortFolderId = Long.valueOf(request.getParameter("resId"));
		DocResource parent = this.docHierarchyManager.getDocResourceById(sortFolderId);
		
		Long userId = CurrentUser.get().getId();
		long frType = Long.valueOf(request.getParameter("frType"));
		
		/**
		 * 此处取得isCurrentPage主要是为了前端复选框的选择
		 * 用Fiddle监测HTTP的请求参数发现，docResId只要是出现一次，就不会去掉
		 * 如果是排序号仍在当前页，那就把docResId屏蔽掉，防止checkbox的checked属性的判断异常
		 */
		String isCurrentPage = request.getParameter("isCurrentPage");
		String docResId = request.getParameter("docResId");
		if(isCurrentPage != null && isCurrentPage.equals("true")){
			String[] docResIds = request.getParameterValues("docCheckedId");
			
			if(docResIds != null && docResIds.length > 0){
				String docCheckedId = docResIds[docResIds.length - 1];
				
				view.addObject("docCheckedId", docCheckedId);
				view.addObject("docResId", null);
			}
		}
		else{
			view.addObject("docResId", docResId);
		}

		Long parentFrId = frType == Constants.FOLDER_SHAREOUT || frType == Constants.FOLDER_BORROWOUT ? parent.getParentFrId() : sortFolderId;
		List<DocResource> docs = docHierarchyManager.findAllDocsByPage(parentFrId, frType, userId);
		List<DocSortProperty> sortProperty = this.docHierarchyManager.getDocSortTable(docs);
		return view.addObject("docs", sortProperty);
	}
	
	private List<DocTableVO> getTableVOs(List<DocResource> drs,
			List<DocMetadataDefinition> dmds, ModelAndView ret, Long userId,
			boolean isBorrowOrShare, byte docLibType, DocResource parent) {
		return this.getTableVOs(drs, dmds, ret, userId, isBorrowOrShare, docLibType, parent, true);
	}

	// 封装右边列表的数据
	@SuppressWarnings("unchecked")
	private List<DocTableVO> getTableVOs(List<DocResource> drs,
			List<DocMetadataDefinition> dmds, ModelAndView ret, Long userId,
			boolean isBorrowOrShare, byte docLibType, DocResource parent,
			boolean isQuery) {
		List<DocTableVO> docs = new ArrayList<DocTableVO>();
		List<Integer> widths = DocMVCUtils.getColumnWidthNew(dmds);
		// 没有数据时返回标题栏
		if (CollectionUtils.isEmpty(drs)) {
			ret.addObject("isNull", "true");

			DocTableVO vo = new DocTableVO();
			List<GridVO> grids = vo.getGrids();
			int index = 0;
			for (DocMetadataDefinition dmd : dmds) {
				GridVO grid = new GridVO();
				grid.setTitle(DocMVCUtils.getDisplayName4MetadataDefinition(dmd.getName()));
				grid.setPercent(widths.get(index));
				grid.setAlign(Constants.getAlign(dmd.getType()));
				
				grids.add(grid);
				index++;
			}
			ret.addObject("vo", vo);
		} 
		else {
			ret.addObject("isNull", "false");
			boolean isPersonal = docLibType == Constants.PERSONAL_LIB_TYPE.byteValue();
			Map<Long, Map> metadatas = null;
			if(!isPersonal && DocMVCUtils.needFetchMetadata(dmds)) {
				List<Long> drIds = new ArrayList<Long>();
				for (DocResource doc : drs) {
                	if (Constants.LINK == doc.getFrType()) {
                		drIds.add(doc.getSourceId());
                	} else {
                		drIds.add(doc.getId());
                	}
                }
				metadatas = this.docMetadataManager.getDocMetadatas(drIds);
			}
			
			for (DocResource dr : drs) {
				DocTableVO vo = new DocTableVO(dr);
				// 单位借阅从左边目录树点击进去正常，从列表里面点击、单位借阅错误，发现传递的参数readOnly参数不同，，单位借阅时这里特意修改
				if ("doc.contenttype.publicBorrow".equals(dr.getFrName())
						&& dr.getFrType() == 103 && dr.getCreateUserId() == 0
						&& dr.getDocLibId() == 0 && dr.getParentFrId() == 0
						&& dr.getIsFolder()) {
					vo.setReadOnlyAcl(true);
				}

				vo.setFrType(dr.getFrType());
				vo.setUpdateTime(dr.getLastUpdate());
				vo.setIsOffice(Constants.isOffice(dr.getMimeTypeId()));
				boolean isImg = Constants.isImgFile(dr.getMimeTypeId());
				vo.setIsImg(isImg);
				// 设置其file属性
				if (isImg) {
					try {
						V3XFile file = fileManager.getV3XFile(dr.getSourceId());
						vo.setFile(file);
					} catch (BusinessException e) {
						log.error("获取文档源文件时出现异常[源ID=" + dr.getSourceId() + "]", e);
					}
				}
				vo.setIsLink(dr.getFrType() == Constants.LINK);
				vo.setIsFolderLink(dr.getFrType() == Constants.LINK_FOLDER);
				DocMVCUtils.setNeedI18nInVo(vo);
				DocMVCUtils.setPigFlag(vo);
				// 设置personalLib标记
				long ct = dr.getFrType();
				DocMimeType mime = null;
				boolean isPersonType = (ct == Constants.PERSON_BORROW)
						|| (ct == Constants.PERSON_SHARE)
						|| (ct == Constants.DEPARTMENT_BORROW)
						|| (ct == Constants.FOLDER_BORROWOUT)
						|| (ct == Constants.FOLDER_SHAREOUT);

				if (isPersonType) {
					vo.setIsPersonalLib(false);
					// 设置是否虚拟节点标记
					vo.setIsPerson(true);
				} else {
					vo.setIsPersonalLib(isPersonal);

					mime = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId());
					// 设置可以下载标记
					vo.setIsUploadFile(mime.getFormatType() == Constants.FORMAT_TYPE_DOC_FILE);
					if (vo.getIsUploadFile()) {
						vo.setCreateDate(DocMVCUtils.getCreateDateOfFile(dr, this.fileManager));
					} else {
						vo.setCreateDate(Datetimes.formatDate(dr.getCreateTime()));
					}

					// 设置权限标记
					this.setGottenAclsInVO(vo, userId, isBorrowOrShare);
				}

				List<GridVO> grids = vo.getGrids();
				int index = 0;
				Map metadataMap = null;
				if (Constants.LINK == dr.getFrType()) {
                	metadataMap = metadatas == null ? null : metadatas.get(dr.getSourceId());
                } else {
                	metadataMap = metadatas == null ? null : metadatas.get(dr.getId());
                } 
				
				int totalPercent = 0;
				int nameIndex = 0;
				for (DocMetadataDefinition dmd : dmds) {
					GridVO grid = new GridVO();

					grid.setType(Constants.getClazz4Ctrl(dmd.getType()));
					grid.setTitle(DocMVCUtils.getDisplayName4MetadataDefinition(dmd.getName()));
					String name = dmd.getPhysicalName();
					
					boolean isName = DocResource.PROP_FRNAME.equals(dmd.getPhysicalName());
					if (isName) {
						nameIndex = index;
					}
					grid.setIsName(isName);
					
					grid.setIsSize(DocResource.PROP_SIZE.equals(dmd.getPhysicalName()));
					Object value = null;
					if (dmd.getIsDefault()) {
						try {
							value = PropertyUtils.getSimpleProperty(dr, name);
							if((dr.getFrType() == Constants.FOLDER_PLAN
									|| dr.getFrType() == Constants.SYSTEM_PLAN
									|| dr.getFrType() == Constants.FOLDER_PLAN_DAY
									|| dr.getFrType() == Constants.FOLDER_PLAN_MONTH
									|| dr.getFrType() == Constants.FOLDER_PLAN_WEEK
									|| dr.getFrType() == Constants.FOLDER_PLAN_WORK)
									&& DocResource.PROP_LAST_UPDATE.equals(name)) {
								value = Datetimes.formatDate((Date)value);
								grid.setType(String.class);
							}
						} 
						catch (Exception e) {
							log.error("getTableVos通过反射取得相应的栏目值时出现异常[属性名称：" + name + "]:", e);
						}
					}
					else {
						value = metadataMap == null ? null : metadataMap.get(dmd.getPhysicalName());
					}

					String stringValue = String.valueOf(value);
					if (stringValue.equals("null"))
						value = "";
					
					if (stringValue.equals("0") && dmd.getType() == Constants.SIZE) {
						grid.setType(StringBuffer.class);
					}
					
					if (!value.equals("")) {
						// 判断是否引用类型元数据，取得相应属性
						byte mdType = dmd.getType();
						if (mdType == Constants.BOOLEAN) {
							if (stringValue.equals("true"))
								value = "common.yes";
							else
								value = "common.no";
						} else if (mdType == Constants.USER_ID) {
							grid.setType(String.class);
							value = Functions.showMemberName((Long) value);
						} else if (mdType == Constants.DEPT_ID) {
							grid.setType(String.class);
							try {
								value = orgManager.getDepartmentById((Long) value).getName();
							} catch (BusinessException e) {
								log.error("通过orgManager取得dept", e);
							}
						} else if (mdType == Constants.CONTENT_TYPE) {
							if (isPersonType) {
								value = "";
							} else {
								grid.setNeedI18n(true);
								grid.setType(String.class);
								value = contentTypeManager.getContentTypeById(Long.valueOf(stringValue)).getName();
							}
						} else if (mdType == Constants.SIZE) {
							grid.setType(StringBuffer.class);
							if (vo.getIsLink() || vo.getIsFolderLink()
									|| vo.getIsPig() || vo.getDocResource().getIsFolder()) {
								value = "";
							} else {
								value = Strings.formatFileSize((Long) value, true);
							}
						} else if (mdType == Constants.IMAGE_ID) {
							grid.setType(null);
							if (!isPersonType) {
								if (dr.getIsFolder()) {
									String src = mime.getIcon();
									value = src.substring(0, src.indexOf("|"));
								} else {
									value = mime.getIcon();
								}
								grid.setIsImg(true);
							}
						} else if (mdType == Constants.ENUM) {
							Set<DocMetadataOption> docMetadataOptions = dmd.getMetadataOption();
							for (DocMetadataOption dmo : docMetadataOptions) {
								if (dmo.getId().toString().equals(value.toString())) {
									value = dmo.getOptionItem();
									break;
								}
							}
						}
					}

					// icon
					if (isPersonType)
						if (dmd.getType() == Constants.IMAGE_ID) {
							value = Constants.PERSON_ICON;
							grid.setTitle("");
							grid.setIsImg(true);
						}

					// 需要调用元数据组件
					if (value != null && Strings.isNotBlank(value.toString())) {
						MetadataNameEnum mne = Constants.getMetadataNameEnum(dmd.getName(), value.toString(), dr.getFrType());
						if (mne != null) {
							value = metadataManager.getMetadataItemLabel(mne, value.toString());
							if (value != null) {
								String resourceName = Constants.getResourceNameOfMetadata(dmd.getName(), value.toString());
								if (Strings.isNotBlank(resourceName)) {
									value = ResourceBundleUtil.getString(resourceName, value.toString());
								}
							} else {
								value = "";
							}
						}
					}
					grid.setValue(value);
					
					// 5. percent
					Integer percent = widths.get(index);
					grid.setPercent(percent);
					totalPercent += percent;
					
					// 6. align
					grid.setAlign(Constants.getAlign(dmd.getType()));
					if ((grid.getValue() == null || "".equals(grid.getValue())
							&& !grid.getType().equals(Date.class)
							&& !(dmd.getType() == Constants.SIZE)
							&& !grid.getType().equals(Timestamp.class))) {
						grid.setValue("&nbsp;");
						grid.setType(String.class);
					}

					grids.add(grid);
					index++;
				}
				
				if (totalPercent < 95) {
					grids.get(nameIndex).setPercent(grids.get(nameIndex).getPercent() + (95 - totalPercent));
				}

				docs.add(vo);
			}
		}

		String docLoc = "";
		Long parentFrId = parent.getParentFrId();
		DocResource par = docHierarchyManager.getDocResourceById(parentFrId);
		// 借阅共享的路径
		if (isBorrowOrShare && parent != null
				&& parent.getFrType() != Constants.FOLDER_BORROW
				&& parent.getFrType() != Constants.FOLDER_SHARE
				&& parent.getFrType() != Constants.FOLDER_SHAREOUT
				&& parent.getFrType() != Constants.FOLDER_BORROWOUT) {

			if (parent.getFrType() == Constants.PERSON_SHARE
					|| parent.getFrType() == Constants.PERSON_BORROW
					|| parent.getFrType() == Constants.DEPARTMENT_BORROW) {

				long mydocId = 0;
				DocResource mydoc = null;
				if(parent.getFrType() != Constants.DEPARTMENT_BORROW) {
					DocResource doc = docHierarchyManager.getPersonalFolderOfUser(parent.getId());
					if (doc != null) {
						if (doc.getFrType() == Constants.FOLDER_MINE) {
							mydocId = doc.getId();
						} else {
							mydocId = doc.getParentFrId();
						}
					}
					mydoc = docHierarchyManager.getDocResourceById(mydocId);
				}
				else {
					mydocId = CurrentUser.get().getId();
					mydoc = docHierarchyManager.getPersonalFolderOfUser(mydocId);
				}

				docLoc = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, parent.getFrName());
				docLoc = this.convertToLink(docLoc, docLoc, parent.getId(), parent.getFrType());
				
				long folderId = parent.getFrType() == Constants.PERSON_SHARE ? Constants.FOLDER_SHARE : Constants.FOLDER_BORROW;
				DocResource borrowOrShareDoc = docHierarchyManager.getDocByType(mydoc.getDocLibId(), folderId);
				String name1 = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, borrowOrShareDoc.getFrName());
				String docLoc1 = this.convertToLink(name1, name1, borrowOrShareDoc.getId(), borrowOrShareDoc.getFrType());
				
				String name2 = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, mydoc.getFrName());
				String docLoc2 = this.convertToLink(name2, name2, mydocId, mydoc.getFrType());
				
				docLoc = docLoc2 + " - " + docLoc1 + " - " + docLoc;
			} else if (parent.getFrType() == Constants.FOLDER_MINE) {
				docLoc = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, parent.getFrName());
				docLoc = this.convertToLink(docLoc, docLoc, parent.getId(), parent.getFrType());
			} else if (parent.getFrType() == Constants.FOLDER_PLAN
					|| parent.getFrType() == Constants.FOLDER_PLAN_DAY
					|| parent.getFrType() == Constants.FOLDER_PLAN_MONTH
					|| parent.getFrType() == Constants.FOLDER_PLAN_WEEK
					|| parent.getFrType() == Constants.FOLDER_PLAN_WORK) {
				docLoc = this.getLocation(parent.getLogicalPath());
			} else if (par != null && (par.getFrType() == Constants.FOLDER_SHAREOUT
					|| parent.getFrType() == Constants.FOLDER_BORROWOUT)) {
				DocResource doc = docHierarchyManager.getPersonalFolderOfUser(CurrentUser.get().getId());
				Long mydocId = doc.getParentFrId();
				DocResource mydoc = docHierarchyManager.getDocResourceById(mydocId);
				String name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, mydoc.getFrName());
				docLoc = parent.getFrName();
				docLoc = this.convertToLink(docLoc, docLoc, parent.getId(), parent.getFrType());
				docLoc = name + " - " + docLoc;

			} else {
				// 共享的文档夹被打开了
				DocResource doc = docHierarchyManager.getPersonalFolderOfUser(CurrentUser.get().getId());
				Long mydocId = doc.getId();
				DocResource mydoc = docHierarchyManager.getDocResourceById(mydocId);
				DocResource shareDoc = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_SHARE);
				String name1 = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, shareDoc.getFrName());
				String name2 = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, mydoc.getFrName());
				String docLoc1 = this.convertToLink(name1, name1, shareDoc.getId(), shareDoc.getFrType());
				String docLoc2 = this.convertToLink(name2, name2, mydocId, mydoc.getFrType());

				docLoc = parent.getFrName();
				docLoc = this.convertToLink(docLoc, docLoc, parent.getId(), parent.getFrType());
				List<Long> owners = this.docLibManager.getOwnersByDocLibId(parent.getDocLibId());
				
				if (owners != null && owners.size() > 0) {
					long ownerid = owners.iterator().next();
					String name = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, ownerid, false);
					name = this.convertToLink(name, name, ownerid, Constants.PERSON_SHARE);
					docLoc = docLoc2 + " - " + docLoc1 + " - " + name + " - " + docLoc;
				}

			}
		} else if (parent != null && parent.getLogicalPath() != null)
			docLoc = this.getLocation(parent.getLogicalPath());

		if (Strings.isBlank(docLoc) && parent != null) {
			docLoc = parent.getFrName();
			docLoc = this.convertToLink(docLoc, docLoc, parent.getId(), parent.getFrType());
		}

		ret.addObject("docLoc", docLoc);

		if(CollectionUtils.isNotEmpty(docs) && isQuery) {
			Collections.sort(docs);
		}
		
		return docs;
	}

	/** 设置DocAclVO对象中的权限标记  */
	private void setGottenAclsInVO(DocAclVO vo, Long userId, boolean isBorrowOrShare) {
		DocMVCUtils.setGottenAclsInVO(vo, userId, isBorrowOrShare, docAclManager);
	}

	/**
	 * 点击文档库，返回 
	 * 1. 该库根文档夹下的所有下级文档夹（左边） List<DocResource> folders
	 * 2.其他所有的库的根文档夹列表
	 * 页面应该传入 userId, List<Long> docLibIds 当前用户拥有权限的所有文档库 docLibId 用户点击的文档库
	 */
	public ModelAndView listRoots(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = null;
		User user = CurrentUser.get();
		Long domainId = CurrentUser.get().getLoginAccount();
		String appName = request.getParameter("appName");
		String flag = request.getParameter("isrightworkspace");
		String spaceType = request.getParameter("spaceType");
		String pigeonholeType=request.getParameter("pigeonholeType");
		
		List<DocLib> libsSrc = new ArrayList<DocLib>();
		List<DocLib> libsSrcAll = new ArrayList<DocLib>();
		// If there is no shareout or borrowOut doc root ,add it into DB!
		if (!user.isAdministrator() && !user.isGroupAdmin()) {
			DocLib lib = this.docLibManager.getPersonalLibOfUser(CurrentUser.get().getId());
			DocResource mydoc = docHierarchyManager.getRootByLibId(lib.getId());
			if (mydoc != null) {
				DocResource borrowDoc = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_BORROWOUT);
				DocResource shareDoc = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_SHAREOUT);
				if (borrowDoc == null && shareDoc == null) {
					DocResource borrow = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_BORROW);
					DocResource share = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_SHARE);
					try {
						docHierarchyManager.createFolderByTypeWithoutAcl(
								Constants.FOLDER_BORROWOUT_KEY,
								Constants.FOLDER_BORROWOUT,
								mydoc.getDocLibId(), borrow.getId(),
								CurrentUser.get().getId());

						docHierarchyManager.createFolderByTypeWithoutAcl(
								Constants.FOLDER_SHAREOUT_KEY,
								Constants.FOLDER_SHAREOUT, mydoc.getDocLibId(),
								share.getId(), CurrentUser.get().getId());
					} catch (BusinessException e) {
						log.error("创建借阅和共享出去的文档节点", e);
					}
				}
			}
		}
		
		if (user.isGroupAdmin()) {
			libsSrcAll = docLibManager.getDocLibs(domainId);
			for (DocLib dl : libsSrcAll) {
				if (!dl.isPersonalLib()) {
					libsSrc.add(dl);
				}
			}
		} else if (user.isAdministrator()) {
			// 单位管理员登录的判断和表单管理员的判断，此时返回单位所有公共库
			libsSrcAll = docLibManager.getDocLibs(domainId);
			for (DocLib dl : libsSrcAll) {
				if(!dl.isPersonalLib() && !dl.isProjectLib()) {
					libsSrc.add(dl);
				}
			}
		} else if ("pigeonhole".equals(flag)) {
			// 表单管理员
			// boolean isFormAdmin = MainHelper.isFORMAdmin(orgManager);
			String validAcl = request.getParameter("validAcl");
			if ("false".equals(validAcl))
				libsSrc = docLibManager.getDocLibs(domainId);
			else
				libsSrc = docLibManager.getDocLibsByUserId(user.getId(), domainId);

		} else if (flag != null) {
			// 移动树
			libsSrc = docLibManager.getDocLibsByUserId(user.getId(), domainId);

		} else {
			// 文档树
			libsSrc = docLibManager.getDocLibsByUserIdNav(user.getId(), domainId);
		}

		if ("move".equals(flag) || "link".equals(flag) || "pigeonhole".equals(flag)) {
			ret = new ModelAndView("doc/docTreeMove");
		} else if ("quote".equals(flag)) {
			ret = new ModelAndView("doc/quote/docQuoteTree");
		} else {
			ret = new ModelAndView("doc/docTree");
		}

		boolean showGroupLib = Constants.isShowGroupLib();
		// 2008.05.28 外部人员不可见公文档案库
		boolean showEdocLib = (user.isInternal() && (Constants.edocModuleEnabled()));
		boolean showPersonalLib = true;
		if ("move".equals(flag)) {
			String docLibType = request.getParameter("docLibType");
			if (docLibType != null
					     && ((Constants.EDOC_LIB_TYPE + "").equals(docLibType)||
							(Constants.ACCOUNT_LIB_TYPE + "").equals(docLibType)||
							(Constants.PROJECT_LIB_TYPE + "").equals(docLibType)||
							(Constants.GROUP_LIB_TYPE + "").equals(docLibType)||
							(Constants.USER_CUSTOM_LIB_TYPE + "").equals(docLibType)))
				showPersonalLib = false;
		}
		List<DocLib> libs = new ArrayList<DocLib>();
		// 过滤
		if ("pigeonhole".equals(flag)) {
			int appKey = NumberUtils.toInt(appName);
			// 公文
			if (ApplicationCategoryEnum.edoc.key() == appKey) {
				for (DocLib l : libsSrc) {
					//公文模板,单位归档的时候显示本单位的：公文档案库，集团文档，单位文档
					if(Strings.isNotEmpty(pigeonholeType)){ 
						if("EdocTempletePrePigeonhole".equals(pigeonholeType)  //预归档
							||"EdocAccountPigoenhole".equals(pigeonholeType)){ //单位归档
							
							if(l.getType() == Constants.EDOC_LIB_TYPE.byteValue()
									|| l.getType() == Constants.ACCOUNT_LIB_TYPE.byteValue()
									|| l.getType() == Constants.GROUP_LIB_TYPE.byteValue())
								libs.add(l);
						}
						if("EdocAccountPigoenhole".equals(pigeonholeType)){ //单位归档
							if(l.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue())
								libs.add(l);
						}
					}else{//只要在文档库中有相应的权限,部门归档可以归档到以下文档库中：集团文档库,单位文档库,自定义文档库（不能归档到项目文档）

						if (l.getType() == Constants.ACCOUNT_LIB_TYPE.byteValue() //单位文档库
								|| l.getType() == Constants.GROUP_LIB_TYPE.byteValue()
								|| l.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue())  //集团文档库
							libs.add(l);
					}
				}
				if(!"EdocTempletePrePigeonhole".equals(pigeonholeType)) {
					addPartTimeLibs(libs);
				}
			} 
			// 公告、新闻、调查、会议
			else if (ApplicationCategoryEnum.bulletin.key() == appKey
					|| ApplicationCategoryEnum.news.key() == appKey
					|| ApplicationCategoryEnum.inquiry.key() == appKey
					|| ApplicationCategoryEnum.meeting.key() == appKey) {
				if (showGroupLib) {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.EDOC_LIB_TYPE.byteValue()
								&& l.getType() != Constants.PERSONAL_LIB_TYPE
										.byteValue())
							libs.add(l);
					}
				} else {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue()
								&& l.getType() != Constants.EDOC_LIB_TYPE
										.byteValue()
								&& l.getType() != Constants.PERSONAL_LIB_TYPE
										.byteValue())
							libs.add(l);
					}
				}
			} else {
				// 其他归档
				if (showGroupLib) {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
							libs.add(l);
					}
				} else {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue()
								&& l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
							libs.add(l);
					}
				}
			}
		} else if (showGroupLib && showEdocLib) {
			libs.addAll(libsSrc);
		} else {
			if (showGroupLib) {
				for (DocLib l : libsSrc) {
					if (l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
						libs.add(l);
				}
			} else if (showEdocLib) {
				for (DocLib l : libsSrc) {
					if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue())
						libs.add(l);
				}
			} else {
				for (DocLib l : libsSrc) {
					if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue()
							&& l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
						libs.add(l);
				}
			}
		}

		List<DocLib> libs2 = new ArrayList<DocLib>();
		if (!showPersonalLib || "department".equals(spaceType)) {
			for (DocLib l : libs) {
				if (l.getType() != Constants.PERSONAL_LIB_TYPE.byteValue())
					libs2.add(l);
			}
		} 
		else if("group".equals(spaceType) || "public_custom_group".equals(spaceType)) {
			for (DocLib l : libs) {
				if (l.isGroupLib())
					libs2.add(l);
			}
		}
		else {
			libs2 = libs;
		}

		// 列出所有文档库的根文档夹
		List<DocTreeVO> roots = new ArrayList<DocTreeVO>();
		List<Long> libids = FormBizConfigUtils.getIds(libs2);
		List<DocResource> rootDrs = null;
		Map<Long, DocResource> rootMap = new HashMap<Long, DocResource>();
		if (CollectionUtils.isNotEmpty(libids)) {
			rootDrs = docHierarchyManager.getRootsByLibIds(libids, Constants.getOrgIdsOfUser(user.getId()));
			if (rootDrs != null) {
				for (DocResource td : rootDrs) {
					rootMap.put(td.getDocLibId(), td);
				}
			}
		}

		long loginAccountId = CurrentUser.get().getLoginAccount();
		boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
		for (DocLib lib : libs2) {
			if(lib.isDisabled())
				continue;
			if(lib.getType() ==  Constants.EDOC_LIB_TYPE.byteValue()){
				if(!isEdoc || !isPluginEdoc ) continue;
			}
		    
			DocResource dr = rootMap.get(lib.getId());
			if (dr == null)
				continue;
			if (lib.isPersonalLib())
				dr.setIsMyOwn(true);
			DocTreeVO vo = new DocTreeVO(dr);
			this.setGottenAclsInVO(vo, user.getId(), false);
			String srcIcon = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon();
			vo.setOpenIcon(srcIcon.substring(srcIcon.indexOf('|')+ 1, srcIcon.length()));
			//当srcIcon中不包含|时，会出现问题，尤其是客户自定义图标
			int iconIndex=srcIcon.indexOf('|')==-1?srcIcon.length():srcIcon.indexOf('|');
			vo.setCloseIcon(srcIcon.substring(0, iconIndex));
			vo.setIsPersonalLib(lib.getType() == Constants.PERSONAL_LIB_TYPE.byteValue());
			vo.setDocLibType(lib.getType());
			if (lib.getDomainId() != loginAccountId) {
				vo.setOtherAccountId(lib.getDomainId());
			}
			DocMVCUtils.setNeedI18nInVo(vo);

			roots.add(vo);
		}

		ret.addObject("roots", roots);
		return ret;
	}
	public ModelAndView bizConfigListRootsBefore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("formbizconfig/write/show_bizconfig_doc");
		return ret;
	}
	public ModelAndView bizConfigListRoots(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("formbizconfig/write/show_bizconfig_doc_tree");
		User user = CurrentUser.get();
		Long domainId = CurrentUser.get().getLoginAccount();
		String appName = request.getParameter("appName");
		String flag = request.getParameter("isrightworkspace");
		String spaceType = request.getParameter("spaceType");
		String pigeonholeType=request.getParameter("pigeonholeType");
		List<DocLib> libsSrc = new ArrayList<DocLib>();
		List<DocLib> libsSrcAll = new ArrayList<DocLib>();
		if (!user.isAdministrator() && !user.isGroupAdmin()) {
			DocLib lib = this.docLibManager.getPersonalLibOfUser(CurrentUser.get().getId());
			DocResource mydoc = docHierarchyManager.getRootByLibId(lib.getId());
			if (mydoc != null) {
				DocResource borrowDoc = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_BORROWOUT);
				DocResource shareDoc = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_SHAREOUT);
				if (borrowDoc == null && shareDoc == null) {
					DocResource borrow = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_BORROW);
					DocResource share = docHierarchyManager.getDocByType(mydoc.getDocLibId(), Constants.FOLDER_SHARE);
					try {
						docHierarchyManager.createFolderByTypeWithoutAcl(
								Constants.FOLDER_BORROWOUT_KEY,
								Constants.FOLDER_BORROWOUT,
								mydoc.getDocLibId(), borrow.getId(),
								CurrentUser.get().getId());

						docHierarchyManager.createFolderByTypeWithoutAcl(
								Constants.FOLDER_SHAREOUT_KEY,
								Constants.FOLDER_SHAREOUT, mydoc.getDocLibId(),
								share.getId(), CurrentUser.get().getId());
					} catch (BusinessException e) {
						log.error("创建借阅和共享出去的文档节点", e);
					}
				}
			}
		}
		
		if (user.isGroupAdmin()) {
			libsSrcAll = docLibManager.getDocLibs(domainId);
			for (DocLib dl : libsSrcAll) {
				if (!dl.isPersonalLib()) {
					libsSrc.add(dl);
				}
			}
		} else if (user.isAdministrator()) {
			// 单位管理员登录的判断和表单管理员的判断，此时返回单位所有公共库
			libsSrcAll = docLibManager.getDocLibs(domainId);
			for (DocLib dl : libsSrcAll) {
				if(!dl.isPersonalLib() && !dl.isProjectLib()) {
					libsSrc.add(dl);
				}
			}
		} else if ("pigeonhole".equals(flag)) {
			// 表单管理员
			// boolean isFormAdmin = MainHelper.isFORMAdmin(orgManager);
			String validAcl = request.getParameter("validAcl");
			if ("false".equals(validAcl))
				libsSrc = docLibManager.getDocLibs(domainId);
			else
				libsSrc = docLibManager.getDocLibsByUserId(user.getId(), domainId);

		} else if (flag != null) {
			// 移动树
			libsSrc = docLibManager.getDocLibsByUserId(user.getId(), domainId);

		} else {
			// 文档树
			libsSrc = docLibManager.getDocLibsByUserIdNav(user.getId(), domainId);
		}

		boolean showGroupLib = Constants.isShowGroupLib();
		// 2008.05.28 外部人员不可见公文档案库
		boolean showEdocLib = (user.isInternal() && !"quote".equals(flag) && (Constants.edocModuleEnabled()));
		boolean showPersonalLib = true;
		if ("move".equals(flag)) {
			String docLibType = request.getParameter("docLibType");
			if (docLibType != null
					     && ((Constants.EDOC_LIB_TYPE + "").equals(docLibType)||
							(Constants.ACCOUNT_LIB_TYPE + "").equals(docLibType)||
							(Constants.PROJECT_LIB_TYPE + "").equals(docLibType)||
							(Constants.GROUP_LIB_TYPE + "").equals(docLibType)||
							(Constants.USER_CUSTOM_LIB_TYPE + "").equals(docLibType)))
				showPersonalLib = false;
		}
		List<DocLib> libs = new ArrayList<DocLib>();
		// 过滤
		if ("pigeonhole".equals(flag)) {
			int appKey = NumberUtils.toInt(appName);
			// 公文
			if (ApplicationCategoryEnum.edoc.key() == appKey) {
				for (DocLib l : libsSrc) {
					//公文模板,单位归档的时候显示本单位的：公文档案库，集团文档，单位文档
					if(Strings.isNotEmpty(pigeonholeType)){ 
						if("EdocTempletePrePigeonhole".equals(pigeonholeType)  //预归档
							||"EdocAccountPigoenhole".equals(pigeonholeType)){ //单位归档
							
							if(l.getType() == Constants.EDOC_LIB_TYPE.byteValue()
									|| l.getType() == Constants.ACCOUNT_LIB_TYPE.byteValue()
									|| l.getType() == Constants.GROUP_LIB_TYPE.byteValue())
								libs.add(l);
						}
					}else{//只要在文档库中有相应的权限,部门归档可以归档到以下文档库中：集团文档库,单位文档库,自定义文档库（不能归档到项目文档）

						if (l.getType() == Constants.ACCOUNT_LIB_TYPE.byteValue() //单位文档库
								|| l.getType() == Constants.GROUP_LIB_TYPE.byteValue()
								|| l.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue())  //集团文档库
							libs.add(l);
					}
				}
				if(!"EdocTempletePrePigeonhole".equals(pigeonholeType)) {
					addPartTimeLibs(libs);
				}
			} 
			// 公告、新闻、调查、会议
			else if (ApplicationCategoryEnum.bulletin.key() == appKey
					|| ApplicationCategoryEnum.news.key() == appKey
					|| ApplicationCategoryEnum.inquiry.key() == appKey
					|| ApplicationCategoryEnum.meeting.key() == appKey) {
				if (showGroupLib) {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.EDOC_LIB_TYPE.byteValue()
								&& l.getType() != Constants.PERSONAL_LIB_TYPE
										.byteValue())
							libs.add(l);
					}
				} else {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue()
								&& l.getType() != Constants.EDOC_LIB_TYPE
										.byteValue()
								&& l.getType() != Constants.PERSONAL_LIB_TYPE
										.byteValue())
							libs.add(l);
					}
				}
			} else {
				// 其他归档
				if (showGroupLib) {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
							libs.add(l);
					}
				} else {
					for (DocLib l : libsSrc) {
						if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue()
								&& l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
							libs.add(l);
					}
				}
			}
		} else if (showGroupLib && showEdocLib) {
			libs.addAll(libsSrc);
		} else {
			if (showGroupLib) {
				for (DocLib l : libsSrc) {
					if (l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
						libs.add(l);
				}
			} else if (showEdocLib) {
				for (DocLib l : libsSrc) {
					if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue())
						libs.add(l);
				}
			} else {
				for (DocLib l : libsSrc) {
					if (l.getType() != Constants.GROUP_LIB_TYPE.byteValue()
							&& l.getType() != Constants.EDOC_LIB_TYPE.byteValue())
						libs.add(l);
				}
			}
		}

		List<DocLib> libs2 = new ArrayList<DocLib>();
		if (!showPersonalLib || "department".equals(spaceType)) {
			for (DocLib l : libs) {
				if (l.getType() != Constants.PERSONAL_LIB_TYPE.byteValue())
					libs2.add(l);
			}
		} 
		else if("group".equals(spaceType) || "public_custom_group".equals(spaceType)) {
			for (DocLib l : libs) {
				if (l.isGroupLib())
					libs2.add(l);
			}
		}
		else {
			libs2 = libs;
		}

		// 列出所有文档库的根文档夹
		List<DocTreeVO> roots = new ArrayList<DocTreeVO>();
		List<Long> libids = FormBizConfigUtils.getIds(libs2);
		List<DocResource> rootDrs = null;
		Map<Long, DocResource> rootMap = new HashMap<Long, DocResource>();
		if (CollectionUtils.isNotEmpty(libids)) {
			rootDrs = docHierarchyManager.getRootsByLibIds(libids, Constants.getOrgIdsOfUser(user.getId()));
			if (rootDrs != null) {
				for (DocResource td : rootDrs) {
					rootMap.put(td.getDocLibId(), td);
				}
			}
		}

		long loginAccountId = CurrentUser.get().getLoginAccount();
		boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
		for (DocLib lib : libs2) {
			if(lib.isDisabled())
				continue;
			if(lib.getType() ==  Constants.EDOC_LIB_TYPE.byteValue()){
				if(!isEdoc || !isPluginEdoc ) continue;
			}
		    
			DocResource dr = rootMap.get(lib.getId());
			if (dr == null)
				continue;
			if (lib.isPersonalLib())
				dr.setIsMyOwn(true);
			DocTreeVO vo = new DocTreeVO(dr);
			this.setGottenAclsInVO(vo, user.getId(), false);
			String srcIcon = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon();
			vo.setOpenIcon(srcIcon.substring(srcIcon.indexOf('|') + 1, srcIcon.length()));
			vo.setCloseIcon(srcIcon.substring(0, srcIcon.indexOf('|')));
			vo.setIsPersonalLib(lib.getType() == Constants.PERSONAL_LIB_TYPE.byteValue());
			vo.setDocLibType(lib.getType());
			if (lib.getDomainId() != loginAccountId) {
				vo.setOtherAccountId(lib.getDomainId());
			}
			DocMVCUtils.setNeedI18nInVo(vo);

			roots.add(vo);
		}

		ret.addObject("roots", roots);
		return ret;
	}

	private void addPartTimeLibs(List<DocLib> libs) throws Exception {
		List<DocLib> partTimeLibs = docLibManager.getAllPartDocResouces(Constants.ACCOUNT_LIB_TYPE,CurrentUser.get()) ;
		if(partTimeLibs == null ){
			return ;
		}
		for(DocLib docLib : partTimeLibs){
			if(!libs.contains(docLib))
				libs.add(docLib) ;
		}
	}

	/** 弹出重命名文档夹、文档窗口  */
	public ModelAndView reName(HttpServletRequest request, HttpServletResponse response) {
		Long docResId = NumberUtils.toLong(request.getParameter("rowid"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		this.docHierarchyManager.lockWhenAct(docResId, CurrentUser.get().getId());
		return new ModelAndView("doc/reName", "dr", dr);
	}

	/** 重命名文档夹、文档  */
	public ModelAndView rename(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = CurrentUser.get().getId();
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		String sisFolder = request.getParameter("isFolder");

		DocResource newFolder = docHierarchyManager.getDocResourceById(docResId);

		// 对于资源是否存在的判断
		if (newFolder == null) {
			String alertMsgKey = "true".equals(sisFolder) ? "doc_alert_source_deleted_folder" : "doc_alert_source_deleted_doc";
			super.rendJavaScript(response, "alert(top.v3x.getMessage('DocLang." + alertMsgKey + "'));" + 
										   "window.dialogArguments.parent.parent.location.reload(true);" + 
										   "parent.close();");
			return null;
		}

		boolean isfolder = newFolder.getIsFolder();
		String newName = request.getParameter("newName");
		String oldName = request.getParameter("oldName");

		// 记录操作日志
		operationlogManager.insertOplog(docResId,
				newFolder.getParentFrId(), ApplicationCategoryEnum.doc,
				isfolder ? ActionType.LOG_DOC_RENAME_FOLDER : ActionType.LOG_DOC_RENAME_DOCUMENT,
				isfolder ? ActionType.LOG_DOC_RENAME_FOLDER + ".desc" : ActionType.LOG_DOC_RENAME_DOCUMENT + ".desc",
				CurrentUser.get().getName(), oldName, newName);
		
		docHierarchyManager.renameDocWithoutAcl(docResId, newName, userId);
		
		//更新名称
		newFolder.setFrName(newName);
		newFolder.setLastUpdate(new Timestamp(new Date().getTime()));
		if (!newFolder.getIsFolder()) {
			// 更新订阅文档
			docAlertLatestManager.addAlertLatest(newFolder, Constants.ALERT_OPR_TYPE_EDIT, userId, 
					new Timestamp(System.currentTimeMillis()), Constants.DOC_MESSAGE_ALERT_MODIFY_RENAME_DOC, oldName);
			this.updateIndex(docResId);
		} else {
			docAlertLatestManager.addAlertLatest(newFolder,
					Constants.ALERT_OPR_TYPE_EDIT, userId, new Timestamp(System.currentTimeMillis()),
					Constants.DOC_MESSAGE_ALERT_MODIFY_RENAME_FOLDER, oldName);
		}
		Boolean flag= (Boolean)(BrowserFlag.OpenWindow.getFlag(CurrentUser.get()));
		if(flag){
			super.rendJavaScript(response, "var rv = [\"" + docResId + "\",\"" + newName + "\"];" +
										   "parent.window.returnValue = rv;" +
										   "parent.close();");
		} else {
			String js = "var rv = [\"" + docResId + "\",\"" + newName + "\"];";
			if("true".equals(sisFolder)){
				js += "var obj = parent.parent.parent.treeFrame;" +
				      "if (obj.webFXTreeHandler.getIdByBusinessId(rv[0]) != undefined) {" + 
				      "obj.webFXTreeHandler.all[obj.webFXTreeHandler.getIdByBusinessId(rv[0])].setText(rv[1]);" +
				      "}";
			}
			js += "parent.parent.window.location.reload(true);parent.parent.winRename.close();";
			super.rendJavaScript(response, js);
		}
		return null;
	}
	
	/** 新建文档夹 */
	public ModelAndView createFolder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return this.createFolder(request, response, false);
	}

	/**
	 * 将创建普通文档夹和公文档案夹的代码进行合并，避免大段重复代码出现
	 * @param isEdocFolder	是否创建公文档案夹
	 */
	private ModelAndView createFolder(HttpServletRequest request, HttpServletResponse response, boolean isEdocFolder) throws IOException {
		try {
			String name = request.getParameter("title");
			Long userId = CurrentUser.get().getId();
			Long parentId = NumberUtils.toLong(request.getParameter("parentId"));
			Long docLibId = NumberUtils.toLong(request.getParameter("docLibId"));
			boolean isPersonalLib = String.valueOf(Constants.PERSONAL_LIB_TYPE).equals(request.getParameter("docLibType"));
			
			DocResource parent = docHierarchyManager.getDocResourceById(parentId);
			String key = null;
			if(parent == null) {
				key = "doc_alert_source_deleted_folder";
			}
			else if(this.docHierarchyManager.deeperThanLimit(parent)) {
				key = "doc_alert_level_too_deep";
			}
			
			if (key != null) {
				super.rendJavaScript(response, "parent.closeAndRefresh('" + key + "', '" + this.docHierarchyManager.getFolderLevelLimit() + "');");
				return null;
			}
			
			DocResource dr = null;
			Long newId = null;
			if(isEdocFolder) {
				if (docHierarchyManager.hasSameNameAndSameTypeDr(parentId, name, Constants.FOLDER_EDOC))
					throw new DocException("doc_upload_dupli_name_failure_alert");
				
				boolean parentCommentEnabled = BooleanUtils.toBoolean(request.getParameter("parentCommentEnabled"));
				dr = docHierarchyManager.createFolderByTypeWithoutAcl(name, Constants.FOLDER_EDOC, docLibId, parentId, userId, false, parentCommentEnabled);
				newId = dr.getId();
				// 扩展元数据
				this.handleMetadata(request, newId, true);
			}
			else {
				boolean parentVersionEnabled = BooleanUtils.toBoolean(request.getParameter("parentVersionEnabled"));
				boolean parentCommentEnabled = BooleanUtils.toBoolean(request.getParameter("parentCommentEnabled"));
				dr = docHierarchyManager.createCommonFolderWithoutAcl(name, parentId, userId, parentVersionEnabled, parentCommentEnabled);
				newId = dr.getId();
			}
			
			if(isEdocFolder || !isPersonalLib) {
				// 记录操作日志
				operationlogManager.insertOplog(newId, parentId, ApplicationCategoryEnum.doc,
						ActionType.LOG_DOC_ADD_FOLDER, ActionType.LOG_DOC_ADD_FOLDER + ".desc", 
						CurrentUser.get().getName(), name);
			}
			
			// 更新订阅文档
			docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_ADD, userId, new Timestamp(System.currentTimeMillis()),
					Constants.DOC_MESSAGE_ALERT_ADD_FOLDER, null);
			
			if(!isEdocFolder) {
				List<DocAlert> alerts = docAlertManager.findPersonalAlertByDrIdOfCurrentUser(parentId);
				if (CollectionUtils.isNotEmpty(alerts)) {
					for (DocAlert alert : alerts) {
						byte type = alert.getChangeType();
						docAlertManager.addAlert(newId, true, type == 0 ? Constants.ALERT_OPR_TYPE_ALL : type,
								V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, userId, alert.getSendMessage(), 
								alert.getSetSubFolder(), false);
						
						if (type == 0)
							break;
					}
				}
			}
			
			super.rendJavaScript(response, "parent.afterCreateFolder('" + parentId + "');");
		}
		catch (DocException e) {
			super.rendJavaScript(response, "parent.enableButtonsAndAlertMsg('" + e.getMessage() + "','" + request.getParameter("title") + "');");
		} 
		return null;
	}
	
	/** 弹出新建公文档案夹窗口    */
	public ModelAndView createEdocFolder(HttpServletRequest request, HttpServletResponse response) {
		String html = htmlUtil.getNewHtml(Constants.FOLDER_EDOC);
		return new ModelAndView("doc/createEdocFolder", "propHtml", html);
	}

	/** 新建公文档案夹 */
	public ModelAndView doCreateEdocFolder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return this.createFolder(request, response, true);
	}

	/**
	 * 文档创建或修改后，处理对应的元数据
	 * @param docResId		创建后的文档ID
	 * @param isNew		是否新建（或修改）
	 */
	@SuppressWarnings("unchecked")
	private void handleMetadata(HttpServletRequest request, Long docResId, boolean isNew) {
		Map<String, Comparable> paramap = this.getMetadataInfo(request);
		if (!paramap.isEmpty()) {
			if(isNew)
				docMetadataManager.addMetadata(docResId, paramap);
			else
				docMetadataManager.updateMetadata(docResId, paramap);
		}
	}
	
	/** 处理、获取元数据信息键值Map */
	@SuppressWarnings("unchecked")
	private Map<String, Comparable> getMetadataInfo(HttpServletRequest request) {
		Map<String, Comparable> paramap = new HashMap<String, Comparable>();
		Map<String, String[]> srcParaMap = new HashMap<String, String[]>(request.getParameterMap());
		for (Iterator<Entry<String, String[]>> iter = srcParaMap.entrySet().iterator(); iter.hasNext(); ) {
			Entry<String, String[]> entry = iter.next();
			String sname = entry.getKey();
			if (this.needHandleMetadata(sname)) {
				String[] values = entry.getValue();
				this.addMetadataKV(paramap, sname, values);
			}
		}
		return paramap;
	}
	
	/** 对元数据信息进行处理并放入键值Map中 */
	@SuppressWarnings("unchecked")
	private void addMetadataKV(Map paramap, String sname, String[] values) {
		if (values.length == 1) {
			try {
				paramap.put(sname, Constants.getTrueTypeValue(sname, values[0]));
			} catch (ParseException e) {
				log.error("扩展元数据类型转换过程中出现异常：", e);
			}
		} else {
			paramap.put(sname, StringUtils.join(values, ','));
		}
	}
	
	/**
	 * 判定是否需要进行元数据处理
	 * @param sname
	 */
	private boolean needHandleMetadata(String sname) {
		return sname.startsWith("avarchar") || sname.startsWith("integer")
			   || sname.startsWith("decimal") || sname.startsWith("date")
			   || sname.startsWith("text") || sname.startsWith("boolean")
			   || sname.startsWith("reference") || sname.startsWith("enum");
	}
	
	/**
	 * 批量下载：文件
	 */
	public ModelAndView checkFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userId = request.getParameter("userId");
		String docId = request.getParameter("docId");
		String isBorrow = request.getParameter("isBorrow");

		if (Strings.isBlank(userId) || !userId.equals(String.valueOf(CurrentUser.get().getId()))) {
			PrintWriter out = response.getWriter();
			out.print("1");
			out.close();
			return null;
		}

		DocResource docr = null;
		if (Strings.isNotBlank(docId)) {
			docr = docHierarchyManager.getDocResourceById(Long.parseLong(docId));
		}
		if (docr == null) {
			PrintWriter out = response.getWriter();
			out.print("2");
			out.close();
			return null;
		}
		
		if ("true".equals(isBorrow)) {
			boolean canDownload = false;
			String lentpotent = docAclManager.getBorrowPotent(docr.getId());
			if (lentpotent != null && "1".equals(lentpotent.substring(0, 1))) {
				canDownload = true;
			}
			if (!canDownload) {
				PrintWriter out = response.getWriter();
				out.print("3");
				out.close();
				return null;
			}
		}
		//增加下载权限判断，
		if (0L != docr.getParentFrId()) {
			DocResource doc1 = docHierarchyManager.getDocResourceById(docr
					.getParentFrId());
			DocLib lib = docLibManager.getDocLibById(doc1.getDocLibId());
			if (lib == null || lib.getType() != Constants.PERSONAL_LIB_TYPE) {
				String orgIds = Constants.getOrgIdsOfUser(Long
						.parseLong(userId));
				Set<Integer> sets = this.docAclManager.getDocResourceAclList(
						doc1, orgIds);
				boolean canDirectDownload = sets.contains(Constants.ALLPOTENT)
						|| sets.contains(Constants.EDITPOTENT)
						|| sets.contains(Constants.READONLYPOTENT);
				boolean addPotentExAER = sets != null && !sets.contains(Constants.ALLPOTENT) && !sets.contains(Constants.EDITPOTENT) && !sets.contains(Constants.READONLYPOTENT) && sets.contains(Constants.ADDPOTENT);
				if (!canDirectDownload && !(addPotentExAER && docr.getCreateUserId().equals(Long.parseLong(userId)))) {
					PrintWriter out = response.getWriter();
					out.print("3");
					out.close();
					return null;
				}
			}
		}
		// 有权限
		String result = null;
		DocMimeType mime = docMimeTypeManager.getDocMimeTypeById(docr.getMimeTypeId());
		String context = SystemEnvironment.getA8ContextPath();
		if (mime.getFormatType() == Constants.FORMAT_TYPE_DOC_FILE) {
			result = "0#" + context + "/fileUpload.do?method=doDownload&viewMode=download&fileId=" + docr.getSourceId() + "&filename=" + docr.getFrName().replaceAll(" ", "&nbsp;") + "&createDate=" + Datetimes.formatDate(docr.getCreateTime());
		} else {
			result = "0#" + context + "/doc.do?method=docDownloadNew4Multi&id=" + docr.getId();
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
		return null;
	}
	
	/**
	 * 批量下载：文件夹
	 */
	@SetContentType
	public ModelAndView getFilesFromFolder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userId = request.getParameter("userId");
		String folderId = request.getParameter("folderId");

		if (Strings.isBlank(userId) || !userId.equals(String.valueOf(CurrentUser.get().getId()))) {
			PrintWriter out = response.getWriter();
			out.print("1");
			out.close();
			return null;
		}

		DocResource doc1 = null;
		if (Strings.isNotBlank(folderId)) {
			doc1 = docHierarchyManager.getDocResourceById(Long.parseLong(folderId));
		}
		if (doc1 == null) {
			PrintWriter out = response.getWriter();
			out.print("2");
			out.close();
			return null;
		}

		boolean justHasAddPotent = false;
		boolean addPotentExAER = false;
		DocLib lib = docLibManager.getDocLibById(doc1.getDocLibId());
		if (lib == null || lib.getType() != Constants.PERSONAL_LIB_TYPE) {
			String orgIds = Constants.getOrgIdsOfUser(Long.parseLong(userId));
			Set<Integer> sets = this.docAclManager.getDocResourceAclList(doc1, orgIds);
			boolean canDownload = sets.contains(Constants.ALLPOTENT)
					|| sets.contains(Constants.EDITPOTENT)
					|| sets.contains(Constants.READONLYPOTENT)
					|| sets.contains(Constants.ADDPOTENT);
			justHasAddPotent = sets != null && ((sets.size() == 1 && sets.iterator().next() == Constants.ADDPOTENT)
					|| (sets.size() == 2 && sets.contains(Constants.ADDPOTENT) && sets.contains(Constants.NOPOTENT)));
			addPotentExAER = sets != null && !sets.contains(Constants.ALLPOTENT) && !sets.contains(Constants.EDITPOTENT) && !sets.contains(Constants.READONLYPOTENT) && sets.contains(Constants.ADDPOTENT);
			if (!canDownload) {
				PrintWriter out = response.getWriter();
				out.print("3");
				out.close();
				return null;
			}
		}

		StringBuilder result = new StringBuilder();
		result.append("0#");
		String aclIds = Constants.getOrgIdsOfUser(Long.parseLong(userId));
		List<DocResource> ret = docAclManager.findNextNodeOfTablePageByDate(doc1, aclIds, -1, -1);

		String context = SystemEnvironment.getA8ContextPath();
		for (int i = 0; i < ret.size(); i++) {
			DocResource dr = ret.get(i);
			// 映射文件不能下载，或者只有写入权限时只能下载自己新建的文档
			if (dr.getIsFolder() || DocMVCUtils.isPig(dr)
					|| dr.getFrType() == Constants.LINK
					|| dr.getFrType() == Constants.LINK_FOLDER
					|| (addPotentExAER && !dr.getCreateUserId().equals(Long.parseLong(userId)))) {
				continue;
			}

			String frSize = (float) (Math.round((float) dr.getFrSize() / 1024 * 100)) / 100 + "";
			String frName = dr.getFrName();
			if (dr.getSourceId() == null) { // 复合文档
				frName += ".zip";
			}

			String url = null;
			if (dr.getSourceId() != null) {
				url = context + "/fileUpload.do?method=doDownload&viewMode=download&fileId=" + dr.getSourceId() + "&filename=" + frName.replaceAll(" ", "&nbsp;") + "&createDate=" + Datetimes.formatDate(dr.getCreateTime());
			} else {
				url = context + "/doc.do?method=docDownloadNew4Multi&id=" + dr.getId();
			}

			StringBuilder s = new StringBuilder();
			s.append("<file>"
					+ "<userid>" + userId + "</userid>"
					+ "<size>" + frSize + "</size>"
					+ "<filename>" + Strings.toXmlStr(frName) + "</filename>"
					+ "<url>" + Strings.toXmlStr(url) + "</url>"
					+ "</file>");

			byte[] bs = s.toString().getBytes("UTF-8");
			result.append(bs.length);
			result.append(s);
		}

		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
		return null;
	}

	/**
	 * 下载文档、文档夹
	 */
	public ModelAndView downloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Long userId = CurrentUser.get().getId();
		String[] drIds = request.getParameterValues("id");
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isFolder = false;
		PrintWriter out = response.getWriter();
		out.println("<script>");
		
		boolean isNotPersonalLib = !docLibType.equals(Constants.PERSONAL_LIB_TYPE);
		for (int i = 0; i < drIds.length; i++) {
			Map<String,Object> map = new HashMap<String,Object>();
			Long did = Long.valueOf(drIds[i]);
			DocResource dr = docHierarchyManager.getDocResourceById(did);

			// 存在性驗證
			if (dr == null)
				continue;

			isFolder = dr.getIsFolder();

			String[] msg_status = this.docHierarchyManager.getLockMsgAndStatus(dr, userId);
			if(!Constants.LOCK_MSG_NONE.equals(msg_status[0]) && 
					!String.valueOf(LockStatus.None.key()).equals(msg_status[1])) {
				out.println("alert('" + msg_status[0]  + Constants.getDocI18nValue("doc.lockstatus.msg.wontdelete") +"');");
				continue;
			}
			map.put("did", did);
			map.put("Name", dr.getFrName());
			map.put("Size", dr.getFrSize());
		    list.add(map);
		}
		ModelAndView mav = new ModelAndView(request.getParameter("url"));
		mav.addObject("param", list);
		return mav;
	
	}
	/**
	 * 删除文档、文档夹
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = CurrentUser.get().getId();
		String[] drIds = request.getParameterValues("id");
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isFolder = false;
		PrintWriter out = response.getWriter();
		out.println("<script>");
		//过滤重复ID form提交里含有id，删除的location中含有id
		Set<Long> ids = new HashSet<Long>();
		String fname = "";
        for (int i = 0; i < drIds.length; i++) {
            Long did = Long.valueOf(drIds[i]);
            DocResource dr = docHierarchyManager.getDocResourceById(did);
            // 存在性驗證
            if (dr == null){
                continue; 
            }
            String frName = dr.getFrName(); 
            String  name = docAclManager.getAclString(dr.getId());
            if(name.indexOf("all=true") == -1){ 
                fname += frName +",";
            }
            ids.add(did);
        }
        if (Strings.isNotBlank(fname)) {
            out.println("alert(parent.v3x.getMessage('DocLang.doc_no_acl_to_delete_cur','"
                    + fname.toString().substring(0, fname.length() - 1) + "'));");
            out.println("</script>");
            return null;
        }
		boolean isNotPersonalLib = !docLibType.equals(Constants.PERSONAL_LIB_TYPE);
		Iterator it = ids.iterator();
		while(it.hasNext()){
			Long did = (Long)it.next();
			DocResource dr = docHierarchyManager.getDocResourceById(did);

			// 存在性驗證
			if (dr == null){
				continue;
			}
			isFolder = dr.getIsFolder();

			String[] msg_status = this.docHierarchyManager.getLockMsgAndStatus(dr, userId);
			if(!Constants.LOCK_MSG_NONE.equals(msg_status[0]) && 
					!String.valueOf(LockStatus.None.key()).equals(msg_status[1])) {
				out.println("alert('" + msg_status[0]  + Constants.getDocI18nValue("doc.lockstatus.msg.wontdelete") +"');");
				continue;
			}
			
			// 更新订阅文档
			if (!isFolder) {
				docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_DELETE, userId,
						new Timestamp(System.currentTimeMillis()), Constants.DOC_MESSAGE_ALERT_DELETE_DOC, null);
			} 
			else {
				if(isNotPersonalLib) {
					String  names = docAclManager.hasAclToDeleteAll(dr, userId);
					if(Strings.isNotBlank(names)){						
						out.println("alert(parent.v3x.getMessage('DocLang.doc_no_acl_to_delete','" + 
								names.toString().substring(0, names.length()-1) + "'));");
						out.println("</script>");
						return null;
					}
				}
				
				docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_DELETE, userId,
						new Timestamp(System.currentTimeMillis()), Constants.DOC_MESSAGE_ALERT_DELETE_FOLDER, null);
			}

			long parentFrId = dr.getParentFrId();
			String frName = dr.getFrName();
			try {
				// 公文归档删除，对公文进行操作！公文类型的判断
				//自选过母军，该状态已废弃。注掉
				/*if (dr.getFrType() == Constants.SYSTEM_ARCHIVES) {
					EdocSummary summary = edocSummaryManager.findById(dr.getSourceId());
					summary.setState(112);
					edocSummaryManager.saveOrUpdateEdocSummary(summary);
				}*/
				docHierarchyManager.removeDocWithoutAcl(dr, userId, true);
			} 
			catch (DocException e) {
				log.error("删除文档[id=" + did + "]时出现异常：", e);
			}

			if(isNotPersonalLib) {
				// 记录操作日志
				operationlogManager.insertOplog(did, parentFrId,
						ApplicationCategoryEnum.doc,
						isFolder ? ActionType.LOG_DOC_REMOVE_FOLDER : ActionType.LOG_DOC_REMOVE_DOCUMENT,
						isFolder ? ActionType.LOG_DOC_REMOVE_FOLDER + ".desc" : ActionType.LOG_DOC_REMOVE_DOCUMENT + ".desc",
						CurrentUser.get().getName(), frName);
			}
			
			if (isFolder) {
				out.println("parent.removeTreeNode('" + did + "');");
			}
		}
		
		out.println("parent.window.location.href=parent.window.location;");
		out.println("</script>");
		return null;
	}

	/** 移动文档、文档夹 */
	public ModelAndView move(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = CurrentUser.get().getId();
		String[] drIds = request.getParameterValues("id");
		Long srcLibId = Long.valueOf(request.getParameter("srcLibId"));		// 源文档库id

		Long destId = Long.valueOf(request.getParameter("destResId"));		// 目标文档夹id
		Long destLibId = Long.valueOf(request.getParameter("destLibId"));	// 目标库id
		String sdestType = request.getParameter("destLibType");
		String destName = request.getParameter("destName");
		boolean commentEnabled = BooleanUtils.toBoolean(request.getParameter("commentEnabled"));
		byte destLibType = Strings.isNotBlank(sdestType) ? Byte.valueOf(sdestType) : Constants.PERSONAL_LIB_TYPE;

		PrintWriter out = response.getWriter();
		out.println("<script type=\"text/javascript\">");
		
		boolean isFolder = false;
		boolean folderMoved = false;
		DocResource destFolder = this.docHierarchyManager.getDocResourceById(destId);
		int destFolderLevelDepth = destFolder.getLevelDepth();
		try {
			boolean destPersonal = destLibType == Constants.PERSONAL_LIB_TYPE.byteValue();
			Timestamp currentTime = new Timestamp(new Date().getTime());
			boolean destFolderVersion = destFolder.isVersionEnabled();
			for (int i = 0; i < drIds.length; i++) {
				Long oprId = Long.valueOf(drIds[i]);
				DocResource oprDr = docHierarchyManager.getDocResourceById(oprId);
				if (oprDr == null)
					continue;
				
				if(oprDr.getParentFrId() == destId.longValue()) {
					throw new DocException("doc_alert_choose_different_dest");
				}
				
				isFolder = oprDr.getIsFolder();
				if(isFolder && this.docHierarchyManager.deeperThanLimit(destFolder)) {
					throw new DocException("doc_alert_level_too_deep");
				}
				
				// 移动时先进行锁校验，暂只对选中的文档进行处理(文档夹下面的文档遍历处理代价较高)
				if(!isFolder) {
					String[] ret = this.docHierarchyManager.getLockMsgAndStatus(oprDr, userId);
					if(!Constants.LOCK_MSG_NONE.equals(ret[0]) && NumberUtils.toInt(ret[1]) != Constants.LockStatus.None.key()) {
						out.println("parent.handleConcurrencyWhenMoveDocs('doc_alert_cannot_move', '" + Strings.escapeJavascript(ret[0]) + "');");
						out.println("</script>");
						out.close();
						return null;
					}
					//历史版本信息的变更（原文件无版本控制，目标文档夹有版本控制时 需要更改原文件的版本控制信息 ）
					if(destFolderVersion && !oprDr.isVersionEnabled())
						oprDr.setVersionEnabled(true);
				}

				DocResource oldParent = docHierarchyManager.getDocResourceById(oprDr.getParentFrId());
				String oprName = oprDr.getFrName();
				long oprParentId = oprDr.getParentFrId();

				docHierarchyManager.moveDocWithoutAcl(oprDr, srcLibId, destLibId, destId, userId, destPersonal, commentEnabled, destFolderLevelDepth);

				String oldParentName = Constants.getDocI18nValue(oldParent.getFrName());
				
				// 更新订阅文档
				docAlertLatestManager.addAlertLatest(oprDr, Constants.ALERT_OPR_TYPE_DELETE, userId, currentTime,
						isFolder ? Constants.DOC_MESSAGE_ALERT_MOVE_FOLDER : Constants.DOC_MESSAGE_ALERT_MOVE_DOC, oldParentName);

				// 移动完成之后，应该删除对被移动对象的订阅：删除订阅、删除最新订阅
				docAlertManager.deleteAlertByDocResourceId(oprDr);
				docAlertLatestManager.deleteAlertLatestsByDoc(oprDr);

				// 更新订阅文档
				docAlertLatestManager.addAlertLatest(oprDr, Constants.ALERT_OPR_TYPE_ADD, userId, currentTime,
						isFolder ? Constants.DOC_MESSAGE_ALERT_ADD_FOLDER_1 : Constants.DOC_MESSAGE_ALERT_ADD_DOC_1, destName);

				if (isFolder) {
					folderMoved = true;
					out.println("parent.refreshTreeAfterFolderMoved('" + drIds[i] + "');");

					//历史版本信息的变更（原文件夹无版本控制，目标文档夹有版本控制时 需要更改原文件夹及其下的文档（夹）的版本控制信息 ）
					if(destFolderVersion && !oprDr.isVersionEnabled())
						docHierarchyManager.setFolderVersionEnabled(oprDr, true, 3, userId);
				}

				String moveAction = isFolder ? ActionType.LOG_DOC_MOVE_FOLDER_OUT : ActionType.LOG_DOC_MOVE_DOCUMENT_OUT;
				operationlogManager.insertOplog(oprId, oprParentId,
						ApplicationCategoryEnum.doc, moveAction, moveAction + ".desc", CurrentUser.get().getName(),
						ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, oprName), oldParentName);

				moveAction = isFolder ? ActionType.LOG_DOC_MOVE_FOLDER_IN : ActionType.LOG_DOC_MOVE_DOCUMENT_IN;
				operationlogManager.insertOplog(oprId, destId,
								ApplicationCategoryEnum.doc, moveAction, moveAction + ".desc", 
								CurrentUser.get().getName(), ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, oprName), destName);

				// 更新全文检索
				if (isFolder) {
					List<DocResource> list = docHierarchyManager.getDocsInFolderByType(oprId, "" + Constants.DOCUMENT);
					for (DocResource d : list) {
						updateIndexManager.update(d.getId(), ApplicationCategoryEnum.doc.getKey(), true);
					}
				} else {
					this.updateIndex(oprId);
				}
			}

			out.println("parent.afterMoveDocs('" + folderMoved + "', '" + destId + "');");
			out.println("</script>");
		} 
		catch (DocException e) {
			out.println("parent.handleExceptionWhenMoveDocs('" + e.getMessage()  + "', '" + this.docHierarchyManager.getFolderLevelLimit() + "');");
			out.println("</script>");
		}
		out.close();
		return null;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	// 权限相关-----------------------------

	// 公共文件夹授权iframe窗口
	public ModelAndView docGrantIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docGrantIframe");
	}

	// 得到公共授权数据
	private List<PotentModel> getGrantVO(HttpServletRequest request, boolean isGroupRes) {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		return docAclManager.getGrantVOs(docResId, isGroupRes);
	}

	// 我的文件夹授权iframe窗口
	public ModelAndView myGrantIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/myGrantIframe");
	}

	// 弹出我的文档授权窗口
	public ModelAndView myGrant(HttpServletRequest request, HttpServletResponse response) {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		List<DocPersonalShareVO> objs = DocMVCUtils.getMyGrantVO(docResId, docAclManager);
		return new ModelAndView("doc/myGrant", "objs", objs);
	}

	// 我的文档授权
	public ModelAndView toMyGrant(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.saveMyGrant(request);
		super.rendJavaScript(response, "parent.close();");
		return null;
	}

	// 保存我的文档的共享授权数据
	private void saveMyGrant(HttpServletRequest request) {
		Long docResId = Long.valueOf(request.getParameter("mygrantDocResId"));
		String username[] = request.getParameterValues("mygrantusername");
		DocResource oprDr = docHierarchyManager.getDocResourceById(docResId);
		Long ownerId = CurrentUser.get().getId();
		Map<Long, String> personmap = new HashMap<Long, String>();
		if (username != null) {
			int len = username.length;
			// 得到本次提交的所有的用户的ID
			List<Long> userIds = new ArrayList<Long>();
			for (int i = 0; i < len; i++) {
				String uid = request.getParameter("mygrantuid" + i);
				if (uid == null && i < len) {
					len++;
					continue;
				}
				if (uid == null && i == len) {
					continue;
				}
				userIds.add(Long.valueOf(uid));
				// 判断此人是不是在数据库中有共享记录
				boolean hasAclShare = docAclManager.hasAclertShare(docResId,
						Long.valueOf(uid), true);
				personmap.put(Long.valueOf(uid), String.valueOf(hasAclShare));
			}
			docAclManager.deletePersonalShare(docResId);
			len = username.length;
			
			// 记录当前子文档夹的权限设置是否全部来源于继承
			boolean allIsInherit = true;
			for (int i = 0; i < len; i++) {
				String inherit = StringUtils.defaultIfEmpty(request.getParameter("mygrantinherit" + i), "false");
				if("false".equals(inherit)) {
					allIsInherit = false;
					break;
				}
			}
			
			for (int i = 0; i < len; i++) {
				String uid = request.getParameter("mygrantuid" + i);

				String utype = request.getParameter("mygrantutype" + i);
				if (uid == null && i < len) {
					len++;
					continue;
				}
				if (uid == null && i == len) {
					continue;
				}

				String inherit = request.getParameter("mygrantinherit" + i);
				if (inherit == null || "".equals(inherit)) {
					inherit = "false";
				}
				// 子文档夹的单独设置(非继承) + 继承而来的权限设置
				if (inherit.equals("false") || allIsInherit) {
					boolean isFolder = Boolean.parseBoolean(request
							.getParameter("isFolder"));
					String snew = request.getParameter("mygrantalertnew" + i);
					boolean alertnew = Boolean.parseBoolean(snew);
					// 是不是产生订阅的记录
					if (alertnew) {
						Long alertId = 0L;
						DocAlert alert = docAlertManager
								.getAlertByDocIdAndOrgOfShare(docResId, utype,
										Long.parseLong(uid));
						if (alert != null)
							alertId = alert.getId();
						else
							alertId = docAlertManager.addAlert(docResId,
									isFolder, Constants.ALERT_OPR_TYPE_ALL,
									utype, Long.parseLong(uid), CurrentUser
											.get().getId(), true, false, true);

						docAclManager.setPersonalSharePotent(Long
								.parseLong(uid), utype, docResId, ownerId,
								alertId);
					} else {
						docAlertManager
								.deleteShareAlertByDocResourceId(docResId);

						docAclManager
								.setPersonalSharePotent(Long.parseLong(uid),
										utype, docResId, ownerId, null);
					}
				}
				if ("false".equals(personmap.get(Long.valueOf(uid)))) {
					try {
						String key = "doc.share.to.label";
						Set<Long> docAlerts =  this.getAlertMemberIds(uid, utype, true, null);
						List<Long> receiverIds = new ArrayList<Long>(docAlerts);
						
						MessageContent cont = MessageContent.get(key, oprDr.getFrName(), CurrentUser.get().getName());
						Collection<MessageReceiver> receivers = MessageReceiver.get(docResId,receiverIds, "message.link.doc.folder.open",com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href,docResId);
						userMessageManager.sendSystemMessage(cont,ApplicationCategoryEnum.doc, CurrentUser.get().getId(), receivers);
					} catch (Exception e) {
						log.error("", e);
					}

				}
			}
		} else {
			docAclManager.deletePersonalShare(docResId);
			docAlertManager.deleteShareAlertByDocResourceId(docResId);
		}
		
		User user = CurrentUser.get();
		//文档夹共享全部权限的授权与变更
		this.appLogManager.insertLog(user, AppLogAction.DocFolder_ShareAuth_Update, user.getName(), oprDr.getFrName());
	}

	// 我的文件夹授权iframe窗口
	public ModelAndView borrowGrantIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/borrowIframe");
	}

	// 弹出我的文档借阅授权窗口
	public ModelAndView borrow(HttpServletRequest request, HttpServletResponse response) {
		List<DocBorrowVO> objs = this.getBorrowVO(request);
		return new ModelAndView("doc/borrow", "objs", objs);
	}

	// 取得借阅数据
	private List<DocBorrowVO> getBorrowVO(HttpServletRequest request) {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		List<DocBorrowVO> my = new ArrayList<DocBorrowVO>();

		List<DocAcl> l = null;
		if (docLibType.equals(Constants.PERSONAL_LIB_TYPE)) {
			l = docAclManager.getPersonalBorrowList(docResId);
		} else {
			l = docAclManager.getDeptBorrowList(docResId);
		}

		boolean isGroupRes = false;
		if (docLibType.equals(Constants.GROUP_LIB_TYPE))
			isGroupRes = true;
		
		if(CollectionUtils.isNotEmpty(l)) {
			for (DocAcl acl : l) {
				DocBorrowVO bvo = new DocBorrowVO();
				String userName = Constants.getOrgEntityName(acl.getUserType(), acl.getUserId(), isGroupRes);
				bvo.setEdate(acl.getEdate());
				bvo.setSdate(acl.getSdate());
				bvo.setUserId(acl.getUserId());
				bvo.setUserName(userName);
				bvo.setUserType(acl.getUserType());
				bvo.setLenPotent(acl.getLenPotent());
				bvo.setLenPotent2(acl.getLenPotent2());
				my.add(bvo);
			}
		}

		return my;
	}

	/** 保存借阅数据 */
	private void saveBorrow(HttpServletRequest request) throws BusinessException {
		String username[] = request.getParameterValues("borrowusername");
		Long docResId = Long.valueOf(request.getParameter("borrowDocResId"));
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isPersonalLib = docLibType.equals(Constants.PERSONAL_LIB_TYPE);
		DocResource oprDr = this.docHierarchyManager.getDocResourceById(docResId);
		
		List<Long> userids = null;
		if (username != null) {
			int len = username.length;

			// 得到这次提交的所有的用户的ID
			userids = new ArrayList<Long>();
			Map<Long, String> usermap = new HashMap<Long, String>();
			for (int i = 0; i < len; i++) {
				String uid = request.getParameter("borrowuid" + i);
				if (uid == null && i < len) {
					len++;
					continue;
				}
				userids.add(Long.valueOf(uid));
				boolean hasAclertBoorrow = docAclManager.hasAclertBoorrow(docResId, Long.valueOf(uid), isPersonalLib);
				usermap.put(Long.parseLong(uid), String.valueOf(hasAclertBoorrow));
			}
			
			len = username.length;
			// 个人文档的借阅
			if (isPersonalLib) {
				this.sendMsgAndDeleteBorrow(isPersonalLib, userids, oprDr,docLibType);
				
				for (int i = 0; i < len; i++) {
					String uid = request.getParameter("borrowuid" + i);
					if (uid == null && i < len) {
						len++;
						continue;
					}
					if (uid == null && i == len) {
						continue;
					}
					
					this.handleAlertTo(request, i, oprDr, isPersonalLib, usermap);
				}
			} else {
				this.sendMsgAndDeleteBorrow(isPersonalLib, userids, oprDr,docLibType);
				
				for (int i = 0; i < len; i++) {
					if (request.getParameter("borrowuid" + i) == null) {
						len++;
						continue;
					}
					this.handleAlertTo(request, i, oprDr, isPersonalLib, usermap);
				}
			}
		} else {
			List<DocAcl> oldPersons = this.getDocAclFromBorrow(docResId, isPersonalLib);
			
			if (oldPersons != null && !oldPersons.isEmpty()) {
				for (DocAcl docAcl : oldPersons) {
					this.sendAlertDelMsg(oprDr, docAcl,docLibType);
				}
			}
			
			docAclManager.deleteBorrow(docResId, isPersonalLib);
		}
	}
	
	private void handleAlertTo(HttpServletRequest request, int i, DocResource oprDr, 
			boolean isPersonalLib, Map<Long, String> usermap) throws BusinessException {
		
		String uid = request.getParameter("borrowuid" + i);
		String utype = request.getParameter("borrowutype" + i);
		
		Byte lenPotent = -1;
		String lenPotentStr = request.getParameter("lenPotent" + i);
		if (lenPotentStr != null && !"".equals(lenPotentStr)) {
			lenPotent = Byte.valueOf(lenPotentStr);
		}
		
		String lenPotent2 = getLenPotentStr(request, i, oprDr);
		
		Date sdate = Datetimes.getTodayFirstTime(request.getParameter("begintime" + i));
		Date edate = Datetimes.getTodayLastTime(request.getParameter("endtime" + i));
		
		java.sql.Timestamp stime = new java.sql.Timestamp(sdate.getTime());
		java.sql.Timestamp etime = new java.sql.Timestamp(edate.getTime());

		this.sendAlertToMsgAndSetPotent(uid, utype, stime, etime, lenPotent, lenPotent2, oprDr, isPersonalLib, usermap,Byte.valueOf(request.getParameter("docLibType")));
	}

	private String getLenPotentStr(HttpServletRequest request, int i, DocResource oprDr) {
		String lenPotent2 = request.getParameter("lenPotent2a" + i) == null ? "0" : "1";
		lenPotent2 += request.getParameter("lenPotent2b" + i) == null ? "0" : "1";
		if (oprDr.getFrType() != 2) {
			lenPotent2 = request.getParameter("bRead" + i) == null ? "0" : "1";
			lenPotent2 += request.getParameter("bBrowse" + i) == null ? "0" : "1";
		}
		return lenPotent2;
	}

	private void sendAlertToMsgAndSetPotent(String uid, String utype, Timestamp stime, Timestamp etime, 
			Byte lenPotent, String lenPotent2, DocResource oprDr, boolean isPersonalLib, 
			Map<Long, String> usermap,Byte docLibType) throws BusinessException {
		// 发送借阅消息
		if ("false".equals(usermap.get(Long.parseLong(uid)))) {
			MessageContent cont = MessageContent.get("doc.alert.to.label", oprDr.getFrName(), CurrentUser.get().getName());
			Set<Long> docAlerts = this.getAlertMemberIds(uid, utype, isPersonalLib, docLibType);
			Collection<MessageReceiver> receivers = MessageReceiver.get(oprDr.getId(), docAlerts, "message.link.doc.openfromborrow", oprDr.getId().toString());
			try {
				userMessageManager.sendSystemMessage(cont, ApplicationCategoryEnum.doc, CurrentUser.get().getId(), receivers);
			} 
			catch (Exception e) {
				log.error("", e);
			}
		}
		// 设置借阅权限
		this.setBorrowPotent(uid, utype, stime, etime, lenPotent, lenPotent2, oprDr, isPersonalLib);
	}

	/**
	 * 设置借阅权限
	 */
	private void setBorrowPotent(String uid, String utype,
			java.sql.Timestamp stime, java.sql.Timestamp etime, Byte lenPotent,
			String lenPotent2, DocResource oprDr, boolean isPersonalLib) {
		
		if(isPersonalLib) {
			docAclManager.setNewPersonalBorrowPotent(Long.parseLong(uid), utype, oprDr.getId(), CurrentUser.get().getId(), stime, etime, lenPotent, lenPotent2);
		}
		else {
			docAclManager.setNewDeptBorrowPotent(Long.parseLong(uid), utype, oprDr.getId(), stime, etime, lenPotent,lenPotent2);
	
			// 记录操作日志
			operationlogManager.insertOplog(oprDr.getId(), oprDr.getParentFrId(), ApplicationCategoryEnum.doc,ActionType.LOG_DOC_LEND, 
					ActionType.LOG_DOC_LEND + ".desc", CurrentUser.get().getName(), oprDr.getFrName());
		}
	}

	/**
	 * 删除借阅记录并给相关人员发送消息通知
	 */
	private void sendMsgAndDeleteBorrow(boolean isPersonalLib, List<Long> userids, DocResource oprDr, Byte docLibType) throws BusinessException {
		List<DocAcl> oldPersons = this.getDocAclFromBorrow(oprDr.getId(), isPersonalLib);
		this.sendAlertDelMsg(userids, oprDr, oldPersons,docLibType);
		docAclManager.deleteBorrow(oprDr.getId(), isPersonalLib);
	}

	/**
	 * 获取借阅权限列表
	 */
	private List<DocAcl> getDocAclFromBorrow(Long docResId, boolean isPersonalLib) {
		List<DocAcl> oldPersons = null;
		if (isPersonalLib) {
			oldPersons = docAclManager.getPersonalBorrowList(docResId);
		}
		else {
			oldPersons = docAclManager.getDeptBorrowList(docResId);
		}
		return oldPersons;
	}

	/**
	 * 取消借阅时，向相关人员发送消息
	 */
	private void sendAlertDelMsg(List<Long> userids, DocResource oprDr, List<DocAcl> oldPersons,Byte docLibType) throws BusinessException {
		if (CollectionUtils.isNotEmpty(oldPersons) && CollectionUtils.isNotEmpty(userids)) {
			for (DocAcl docAcl : oldPersons) {
				if (!userids.contains(docAcl.getUserId())) {
					this.sendAlertDelMsg(oprDr, docAcl,docLibType);
				}
			}
		}
	}

	/**
	 * 取消借阅时，向相关人员发送消息
	 */
	private void sendAlertDelMsg(DocResource oprDr, DocAcl docAcl,Byte docLibType) throws BusinessException {
		Set<Long> docAlerts = this.getAlertMemberIds(Long.toString(docAcl.getUserId()), docAcl.getUserType(), true, docLibType);
		MessageContent cont = MessageContent.get("doc.alert.del.label", oprDr.getFrName(), CurrentUser.get().getName());
		Collection<MessageReceiver> receivers = MessageReceiver.get(CurrentUser.get().getId(), docAlerts);
		try {
			userMessageManager.sendSystemMessage(cont, ApplicationCategoryEnum.doc, 
					CurrentUser.get().getId(), receivers, oprDr.getId());
		} 
		catch (Exception e) {
			log.error("", e);
		}
	}

	/** 获得所有借阅对象人员的Id */
	private Set<Long> getAlertMemberIds(String id, String usertype, boolean isPersonLib, Byte docLibType) throws BusinessException {
		Set<Long> memberIds = new HashSet<Long>();
		// 个人文档不能借阅给自己 ，单位文档可以
		if (V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(usertype)) {
			if (Long.parseLong(id) != CurrentUser.get().getId()) {
				memberIds.add(Long.parseLong(id));
			}
		} 
		else {
			String idAndType = usertype + "|" + id;
			Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(idAndType);
			for (V3xOrgMember om : membersSet) {
				// 公文文档过滤外部人员
				if (docLibType != null && docLibType == Constants.EDOC_LIB_TYPE.byteValue() && !om.getIsInternal()) {
					continue;
				}
				
				if (om.getId().longValue() != CurrentUser.get().getId()) {
					memberIds.add(om.getId());
				}
			}
		}
		
		if(isPersonLib)
			memberIds.remove(CurrentUser.get().getId());
		
		return memberIds;
	}

	// 保存公共库的共享授权数据
	/**
	 * 文件夹共享只更新全文检索权限即可，不解析
	 */
	private void saveGrant(HttpServletRequest request) {
		Long docResId = NumberUtils.toLong(request.getParameter("docResId"));
		DocResource oprDr = docHierarchyManager.getDocResourceById(docResId);
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		List<Long> owners = DocMVCUtils.getLibOwners(dr);
		boolean isGroupRes = docLibType == Constants.GROUP_LIB_TYPE.byteValue();
		Long userId = CurrentUser.get().getId();
		
		String username[] = request.getParameterValues("username");
		if (username != null) {
			// lihf 拿到页面传过来的所有权限记录
			int len = username.length;
			List<String> rows = new ArrayList<String>();
			Map<Long, String> newMap = new HashMap<Long, String>();
			for (int i = 0; i < len; i++) {
				String[] uids = request.getParameterValues("uid" + i);
				if (uids == null && i < len) {
					len++;
					continue;
				}
				String[] utypes = request.getParameterValues("utype" + i);
				String uid = uids[uids.length - 1];
				String utype = utypes[utypes.length - 1];
				if (uid == null && i < len) {
					continue;
				}
				String row = uid + "," + utype + "," + docResId + "," + this.getAclStr(request, i);
				rows.add(row);
				newMap.put(Long.parseLong(uid), row);
			}

			List<PotentModel> objs = new ArrayList<PotentModel>();
			// 继承  lihf 封装该DocResource 继承过来的权限数据
			List<DocAcl> l = docAclManager.getDocAclListByInherit(docResId);
			Set<Long> set = new HashSet<Long>();
			if (CollectionUtils.isNotEmpty(l)) {
				for (DocAcl a : l) {
					set.add(a.getUserId());
				}
				for (Long userid : set) {
					PotentModel p = new PotentModel();
					p.setUserId(userid);
					
					if(owners.contains(userid))
						p.setIsLibOwner(true);

					for (DocAcl a : l) {
						if (a.getUserId() == userid) {
							if (p.getUserName() == null) {
								String userName = Constants.getOrgEntityName(a.getUserType(), a.getUserId(), isGroupRes);
								p.setUserName(userName);
								p.setUserType(a.getUserType());
							}
							p.copyAcl(a);
						}
					}
					if (!docAclManager.isNoInherit(p.getUserId(), p.getUserType(), docResId)) {
						p.setInherit(true);
						p.setAlert(false);
						p.setAlertId(0L);
						objs.add(p);
					}
				}
			}
			
			// 非继承 lihf 本级权限记录
			List<List<DocAcl>> l2 = docAclManager.getDocAclListByNew(docResId);
			for (List<DocAcl> l3 : l2) {
				PotentModel p = null;
				boolean flag = false;
				if (objs != null && objs.size() > 0) {
					for (PotentModel pm : objs) {
						for (DocAcl temp : l3) {
							if (temp.getUserId() == pm.getUserId()) {
								flag = true;
								p = pm;
								break;
							}
						}
						if (flag) {
							break;
						}
					}
				}
				if (!flag) {
					p = new PotentModel();
				} else {
					p.setAllAcl(false);
				}
				boolean isAlert = false;
				long alertId = 0L;
				for (DocAcl acl2 : l3) {
					if (p.getUserId() == null) {
						p.setUserId(acl2.getUserId());
						p.setUserType(acl2.getUserType());
						String userName = Constants.getOrgEntityName(acl2.getUserType(), acl2.getUserId(), isGroupRes);
						p.setUserName(userName);
					}
					p.copyAcl(acl2);

					isAlert = acl2.getIsAlert();
					if (isAlert)
						alertId = acl2.getDocAlertId();
				}

				p.setInherit(false);
				p.setAlert(isAlert);
				p.setAlertId(alertId);
				if (!flag) {
					objs.add(p);
				}
			}

			List<String> oldlist = new ArrayList<String>();
			Map<Long, String> oldMap = new HashMap<Long, String>();
			for (PotentModel pmm : objs) {
				String dp = pmm.descPotent(docResId);
				oldlist.add(dp);
				oldMap.put(pmm.getUserId(), dp);
			}

			Set<Long> keySet = oldMap.keySet();

			int minOrder = docAclManager.getMaxOrder();
			for (long userIdmap : keySet) {
				if (owners.contains(userIdmap)) {
					//文档库管理员能查看所有, 不用保存权限
					continue; 
				}
				
				if (newMap.containsKey(userIdmap)) {
					String newStr = newMap.get(userIdmap);
					String oldStr = oldMap.get(userIdmap);
					String[] newarr = newStr.split(",");
					
					// 继承
					boolean isInherit = Boolean.parseBoolean(newarr[9]); 

					// 订阅
					boolean isAlert = Boolean.parseBoolean(newarr[10]);
					
					if (!newStr.equals(oldStr) || isInherit) {
						docAlertManager.deleteAlertByDocResourceIdAndOrgByAcl(docResId, newarr[1], Long.parseLong(newarr[0]));
						Long newalertId = null;
						if (isAlert) {
							newalertId = docAlertManager.addAlert(docResId, true, Constants.ALERT_OPR_TYPE_ALL,
									newarr[1], Long.valueOf(newarr[0]), userId, true, false, true);
						}

						boolean haspotent = false;
						if (!isInherit) {
							docAclManager.deletePotentByUser(Long.parseLong(newarr[2]), Long.parseLong(newarr[0]), 
									newarr[1], docLibType, dr.getDocLibId());
						}
						
						for (int i = 3; i < 9; i++) {
							int potent = this.getPotentType(i);
							if (newarr[i].equals("true")) {
								haspotent = true;
								docAclManager.setDeptSharePotent(Long.parseLong(newarr[0]), newarr[1], Long.parseLong(newarr[2]), potent, isAlert, newalertId,minOrder++);
							} else if (newarr[9].equals("false")){
								docAclManager.deletePotentByUser(Long.parseLong(newarr[2]), Long.parseLong(newarr[0]), newarr[1], potent);
							}
						}

						if (!haspotent) {
							if (isAlert) {
								docAlertManager.deleteAlertById(newalertId);
							}
							docAclManager.deletePotentByUser(Long.parseLong(newarr[2]), Long.parseLong(newarr[0]), newarr[1], docLibType, dr.getDocLibId());

							docAclManager.setDeptSharePotent(Long.parseLong(newarr[0]), newarr[1], Long.parseLong(newarr[2]), Constants.NOPOTENT, false, null,minOrder++);
							//管理员订阅不可更改 
//							for (Long cat : owners) {
//								Long alertId = docAlertManager.addAlert(docResId, true, Constants.ALERT_OPR_TYPE_ALL,
//										V3xOrgEntity.ORGENT_TYPE_MEMBER, cat, userId, true, false, true);
//								docAclManager.setDeptSharePotent(cat, V3xOrgEntity.ORGENT_TYPE_MEMBER, Long.parseLong(newarr[2]), Constants.ALLPOTENT, true, alertId);
//							}
						}
					}

				} else {
					String oldStr = oldMap.get(userIdmap);
					String[] oldarr = oldStr.split(",");
					docAlertManager.deleteAlertByDocResourceIdAndOrgByAcl(Long.parseLong(oldarr[2]), oldarr[1], Long.parseLong(oldarr[0]));

					DocResource currenDr = docHierarchyManager.getDocResourceById(Long.parseLong(oldarr[2]));
					List<DocResource> list = docAlertManager.getSubFolderIds(Long.parseLong(oldarr[2]), currenDr);
					List<Long> docIds = new ArrayList<Long>();
					if (list != null && !list.isEmpty()) {
						for (DocResource item : list) {
							docIds.add(item.getId());
						}
						docAlertManager.deleteAlertByDocResourceIdAndOrgByAclForBatch(docIds, oldarr[1], Long.parseLong(oldarr[0]));
					}
					docAclManager.deletePotentByUser(Long.parseLong(oldarr[2]), Long.parseLong(oldarr[0]), oldarr[1], docLibType, dr.getDocLibId());
					docAclManager.setDeptSharePotent(Long.parseLong(oldarr[0]), oldarr[1], Long.parseLong(oldarr[2]), Constants.NOPOTENT, false, null,minOrder++);
					//管理员订阅不可更改 
//					for (Long cat : owners) {
//						Long alertId = docAlertManager.addAlert(docResId, true, Constants.ALERT_OPR_TYPE_ALL,
//								V3xOrgEntity.ORGENT_TYPE_MEMBER, cat, userId, true, false, true);
//						docAclManager.setDeptSharePotent(cat, V3xOrgEntity.ORGENT_TYPE_MEMBER, Long.parseLong(oldarr[2]), Constants.ALLPOTENT, true, alertId);
//					}
				}
			}
			
			Set<Long> receiverIds = new HashSet<Long>();
			
			Set<Long> newkeySet = newMap.keySet();
			for (long userIdnew : newkeySet) {
				if (owners.contains(userIdnew))
					continue;
				if (!oldMap.containsKey(userIdnew)) {
					String newStr = newMap.get(userIdnew);
					String[] newarr = newStr.split(",");

					// 订阅
					boolean isAlert = Boolean.parseBoolean(newarr[10]);
					Long newalertId = null;
					if (isAlert) {
						newalertId = docAlertManager.addAlert(docResId, true, Constants.ALERT_OPR_TYPE_ALL, newarr[1], 
								Long.valueOf(newarr[0]), userId, true, false, true);
					}
					// 是否设置没有权限
					boolean haspotent = false;
					for (int i = 3; i < 9; i++) {
						int potent = this.getPotentType(i);
						if (newarr[i].equals("true")) {
							haspotent = true;
							docAclManager.setDeptSharePotent(Long.parseLong(newarr[0]), newarr[1], Long.parseLong(newarr[2]), potent, isAlert, newalertId,minOrder++);
						}
					}

					if (!haspotent) {
						if (isAlert) {
							docAlertManager.deleteAlertById(newalertId);
						}
						docAclManager.setDeptSharePotent(Long.parseLong(newarr[0]), newarr[1], Long.parseLong(newarr[2]), Constants.NOPOTENT, false, null,minOrder++);
					}

					try {
						if(isAlert == true){
							Set<Long> docAlerts = this.getAlertMemberIds(String.valueOf(userIdnew), newarr[1], true, docLibType);
							receiverIds.addAll(docAlerts);
						}
					}
					catch (Exception e) {
						log.error("", e);
					}
				}
			}
			// 发送消息
			try {
				String key = "doc.share.to.label";
				MessageContent cont = MessageContent.get(key,
						ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, oprDr.getFrName()), CurrentUser.get().getName());
				Collection<MessageReceiver> receivers = MessageReceiver.get(docResId, receiverIds, "message.link.doc.folder.open", 
						com.seeyon.v3x.common.usermessage.Constants.LinkOpenType.href, docResId) ;
				userMessageManager.sendSystemMessage(cont, ApplicationCategoryEnum.doc, CurrentUser.get().getId(), receivers);
			} catch (Exception e) {
				log.error("", e);
			}
		} else {
			docAclManager.deleteDeptShareByDoc(docResId);
			docAlertManager.deleteShareAlertByDocResourceId(docResId);
		}

		// 记录操作日志
		operationlogManager.insertOplog(docResId, oprDr.getParentFrId(),
				ApplicationCategoryEnum.doc, ActionType.LOG_DOC_SHARE,
				ActionType.LOG_DOC_SHARE + ".desc",
				CurrentUser.get().getName(), ResourceBundleUtil.getString(
						Constants.RESOURCE_BASENAME, oprDr.getFrName()));

		// 全文检索, 更新所有影响到的文档
		List<DocResource> list = docHierarchyManager.getDocsInFolderByType(
				docResId, "" + Constants.DOCUMENT);
		for (DocResource d : list) {
			updateIndexManager.update(d.getId(), ApplicationCategoryEnum.doc.getKey(), true);
		}
	}

	/**
	 * 在保存权限时，获取遍历顺序中某一次的权限记录，以","拼接起来
	 * @param i	遍历顺序游标
	 */
	private String getAclStr(HttpServletRequest request, int i) {
		// 权限表
		String call = StringUtils.defaultIfEmpty(request.getParameter("cAll" + i), "false");
		String cedit = StringUtils.defaultIfEmpty(request.getParameter("cEdit" + i), "false");
		String cadd = StringUtils.defaultIfEmpty(request.getParameter("cAdd" + i), "false");
		String cread = StringUtils.defaultIfEmpty(request.getParameter("cRead" + i), "false");
		String clist = StringUtils.defaultIfEmpty(request.getParameter("cList" + i), "false");
		String cbrowse = StringUtils.defaultIfEmpty(request.getParameter("cBrowse" + i), "false");
		String inherit = StringUtils.defaultIfEmpty(request.getParameter("inherit" + i), "false");
		String alert = StringUtils.defaultIfEmpty(request.getParameter("cAlert" + i), "false");
		String acl = call + "," + cedit + "," + cadd + "," + cread + "," + clist + "," + cbrowse + "," + inherit + "," + alert;
		return acl;
	}

	/**
	 * 根据解析值获取权限类型
	 */
	private int getPotentType(int i) {
		int potent = 0;
		switch (i) {
		case 3:
			potent = Constants.ALLPOTENT;
			break;
		case 4:
			potent = Constants.EDITPOTENT;
			break;
		case 5:
			potent = Constants.ADDPOTENT;
			break;
		case 6:
			potent = Constants.READONLYPOTENT;
			break;
		case 7:
			potent = Constants.LISTPOTENT;
			break;
		case 8:
			potent = Constants.BROWSEPOTENT;
			break;
		}
		return potent;
	}

	// 初始化继承
	public ModelAndView recovery(HttpServletRequest request, HttpServletResponse response) {
		String docResId = request.getParameter("docResId");
		byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		long docLibId = Long.valueOf(request.getParameter("docLibId"));
		docAclManager.setPotentInherit(Long.parseLong(docResId), docLibType, docLibId);
		return null;
	}


	/** 查看一个文档的日志记录  */
	public ModelAndView docLogView(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("doc/log/docLogQuery");
		long docResId = Long.valueOf(request.getParameter("docResId"));
		List<OperationLog> list = operationlogManager.queryByObjectId(docResId, true);
		V3xOrgMember member = null;
		V3xOrgAccount account = null;

		List<ListDocLog> the_list = new ArrayList<ListDocLog>();
		for (int i = 0; i < list.size(); i++) {
			ListDocLog listDocLog = new ListDocLog();
			OperationLog log_ = (OperationLog) list.get(i);
			listDocLog.setOperationLog(log_);

			try {
				member = orgManager.getMemberById(log_.getMemberId());
				account = orgManager.getAccountById(member.getOrgAccountId());
			} catch (BusinessException e) {
				log.error("从orgManager取得用户", e);
			}
			listDocLog.setMember(member);
			listDocLog.setAccount(account);
			the_list.add(listDocLog);
		}

		List<PotentModel> grantVO = this.getGrantVO(request, true);
		modelView.addObject("grantVO", grantVO);
		modelView.addObject("docLogVeiw", the_list);
		modelView.addObject("docResourceId", docResId);
		return modelView;
	}

	// 获取文档夹的日志记录
	public ModelAndView folderLogView(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("doc/log/folderLogQuery");
		List<ListDocLog> the_list = new ArrayList<ListDocLog>();
		long docResId = Long.valueOf(request.getParameter("docResId"));
		long docLibId = Long.valueOf(request.getParameter("docLibId"));

		DocLib docLib = this.docLibManager.getDocLibById(docLibId);
		boolean isGroupLib = (docLib.getType() == Constants.GROUP_LIB_TYPE.byteValue());
		modelView.addObject("isGroupLib", isGroupLib);

		List<OperationLog> list = operationlogManager.queryBySubObjectIdOrObjectId(docResId, docResId, true);

		for (int i = 0; i < list.size(); i++) {
			ListDocLog listDocLog = new ListDocLog();
			OperationLog log_ = (OperationLog) list.get(i);
			listDocLog.setOperationLog(log_);
			V3xOrgMember member = null;
			V3xOrgAccount account = null;
			try {
				member = orgManager.getMemberById(log_.getMemberId());
				account = orgManager.getAccountById(member.getOrgAccountId());
			} catch (BusinessException e) {
				log.error("从orgManager取得member", e);
			}
			listDocLog.setMember(member);
			listDocLog.setAccount(account);
			the_list.add(listDocLog);
		}
		modelView.addObject("folderLogView", the_list);
		modelView.addObject("docResourceId", docResId);
		return modelView;
	}

	/**
	 * 查看文档日志的框架
	 */
	public ModelAndView docLogViewIframe(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("doc/log/docLogQueryIfram");
		long docLibId = Long.valueOf(request.getParameter("docLibId"));
		long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource docRes = docHierarchyManager.getDocResourceById(docResId);
		DocLib docLib = this.docLibManager.getDocLibById(docLibId);
		boolean isGroupLib = (docLib.getType() == Constants.GROUP_LIB_TYPE.byteValue());
		modelView.addObject("isGroupLib", isGroupLib);
		String frName = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, docRes.getFrName());
		modelView.addObject("docLibName", frName);
		return modelView;
	}

	/** 判断是文档日志还是文档夹日志 */
	public ModelAndView logView(HttpServletRequest request, HttpServletResponse response) {
		long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource docRes = docHierarchyManager.getDocResourceById(docResId);
		return docRes.getIsFolder() ? this.folderLogView(request, response) : this.docLogView(request, response);
	}

	/** 进入添加文档界面 */
 	public ModelAndView addDoc(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docAdd");
		
		Long parentId = Long.valueOf(request.getParameter("resId"));
		DocResource parentDr = this.docHierarchyManager.getDocResourceById(parentId);
		mav.addObject("parentDr", parentDr);

		DocLib docLib = docLibManager.getDocLibById(parentDr.getDocLibId());
		boolean contentTypeFlag = true;
		String html = "";
		if (!docLib.isPersonalLib()) {
			List<DocType> contentTypes = docLibManager.getValidContentTypesForDoc(docLib.getId());
			mav.addObject("contentTypes", contentTypes);
			if (CollectionUtils.isEmpty(contentTypes)) {
				contentTypeFlag = false;
			} else {
				html = htmlUtil.getNewHtml(contentTypes.get(0).getId());
			}
		} else {
			contentTypeFlag = false;
		}
		
		return mav.addObject("contentTypeFlag", contentTypeFlag).addObject("html", html).addObject("docLib", docLib);
	}

	/** 进入修改文档页面，获取该文档的对应信息   */
	public ModelAndView editDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = "doc/docEdit";
		if (Strings.isNotBlank(request.getParameter("isUploadFileMimeType"))) {
			url = "doc/docEditUpload";
		}
		ModelAndView modelView = new ModelAndView(url);
		long docResId = Long.valueOf(request.getParameter("docResId"));
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		
		// 后台重复判断是否有编辑的权限，避免前端漏洞导致越权操作(TODO 性能问题)
		DocLib lib = this.docLibManager.getDocLibById(dr.getDocLibId());
		Long userId = CurrentUser.get().getId();
		if(lib != null) {
			boolean canEdit = false;
			if(lib.getType() != Constants.PERSONAL_LIB_TYPE) {
				String orgIds = Constants.getOrgIdsOfUser(userId);
				Set<Integer> sets = this.docAclManager.getDocResourceAclList(dr, orgIds);
				canEdit = sets.contains(Constants.ALLPOTENT) || sets.contains(Constants.EDITPOTENT);
			}
			else {
				canEdit = dr.getCreateUserId() == userId.longValue();
			}
			
			if(!canEdit) {
				super.rendJavaScript(response, "alert('" + Constants.getDocI18nValue("doc.noauth.edit") + "');window.close();");
				return null;
			}
		}
		
		DocEditVO docEditVo = new DocEditVO(dr);
		long mimeTypeId = dr.getMimeTypeId();
		DocMimeType mime = docMimeTypeManager.getDocMimeTypeById(mimeTypeId);
		docEditVo.setIsFile(mime.getFormatType() == Constants.FORMAT_TYPE_DOC_FILE);
		if (mime.isOffice()) {
			docEditVo.setIsFile(false);
			// 正文
			DocBody docBody = docHierarchyManager.getBody(docResId);
			String content = "";
			String bodyType = "";
			if (docBody != null) {
				content = docBody.getContent();
				bodyType = docBody.getBodyType();
				// 创建日期
				Date createDate = docBody.getCreateDate();
				if (createDate != null)
					docEditVo.setCreateDate(createDate);
			}
			docEditVo.setContent(content);
			docEditVo.setBodyType(bodyType);
		} else {
			String bodyType = Constants.getBodyType(dr.getMimeTypeId());
			docEditVo.setIsFile(true);
			V3XFile file = null;
			try {
				file = fileManager.getV3XFile(dr.getSourceId());
				
				docEditVo.setFile(file);
				docEditVo.setCreateDate(file.getCreateDate());
				docEditVo.setCreateDateString(new Timestamp(file.getCreateDate().getTime()).toString().substring(0, 10));
			} catch (BusinessException e) {
				log.error("从fileManager取得V3xFile", e);
			}
			// jpg格式文件后台保存为html格式，但是不允许在线编辑
			if (bodyType == null || dr.isImage()) {
				// 不可以在线编辑

			} else {
				// word excel 在线编辑
				docEditVo.setCanEditOnline(true);
				docEditVo.setBodyType(bodyType);
				docEditVo.setContent(dr.getSourceId() + "");
			}
		}

		List<Attachment> attachments = attachmentManager.getByReference(docResId, docResId);
		docEditVo.setAttachments(attachments);

		// 非个人文档库的文档修改时加并发锁
		if (!docLibType.equals(Constants.PERSONAL_LIB_TYPE)) {
			docHierarchyManager.lockWhenAct(docResId, userId);
		}

		// 读取该文档所在文档库对应的内容类型
		boolean contentTypeFlag = true; 
		if (!docLibType.equals(Constants.PERSONAL_LIB_TYPE)) {
			long docLibId = dr.getDocLibId();
			List<DocType> contentTypes = docLibManager.getContentTypesForDoc(docLibId);

			// 当返回的集合不包含当前文档的内容类型时（已删除类型），应该加上
			DocType type = contentTypeManager.getContentTypeById(dr.getFrType());
			// 使用新集合，不能修改系统的集合
			List<DocType> atypes = new ArrayList<DocType>();

			if (contentTypes == null) {
				atypes.add(type);
			} else if (!contentTypes.contains(type)) {
				atypes.add(type);
				atypes.addAll(contentTypes);
			} else {
				atypes.addAll(contentTypes);
			}

			modelView.addObject("contentTypes", atypes);
		} else {
			contentTypeFlag = false;
		}

		String htmlStr = "";
		if (contentTypeManager.hasExtendMetadata(dr.getFrType())) {
			htmlStr = htmlUtil.getEditHtml(docResId, false);
		}

		if(dr.getSourceId() != null){
			modelView.addObject("sourceId", dr.getSourceId());
		}

		modelView.addObject("mimeTypeId", mimeTypeId);
		modelView.addObject("docEditVo", docEditVo);
		modelView.addObject("contentTypeFlag", contentTypeFlag);
		modelView.addObject("html", htmlStr);
		modelView.addObject("docTypeDeletedStatus", Constants.CONTENT_TYPE_DELETED);
		return modelView;
	}

	/**
	 * 新建、编辑文档页面动态修改内容类型
	 */
	public ModelAndView changeContentType(HttpServletRequest request, HttpServletResponse response) {
		Long contentTypeId = null;
		try {
			contentTypeId = RequestUtils.getLongParameter(request, "contentTypeId");
		} catch (ServletRequestBindingException e1) {
			log.error("controller中changeContentType中从request取得long型参数", e1);
		}
		Long oldCTypeId = RequestUtils.getLongParameter(request, "oldCTypeId", 0);
		Long docResId = RequestUtils.getLongParameter(request, "docResId", 0);

		boolean isAjax = false;
		try {
			isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");
		} catch (ServletRequestBindingException e1) {
			log.error("controller中changeContentType中从request取得boolean型参数", e1);
		}
		String htmlStr = "";
		try {
			if (docResId != 0 && oldCTypeId.equals(contentTypeId)) {
				htmlStr = htmlUtil.getEditHtml(docResId, false);
			} else {
				htmlStr = htmlUtil.getNewHtml(contentTypeId);
			}
		} catch (Exception e) {
			log.error(e);
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.putOpt("htmlStr", htmlStr);
		} catch (JSONException e) {
			log.error("controller中changeContentType中ajax添加数据", e);
		}
		String view = null;
		if (isAjax) {
			view = this.getJsonView();
		}
		return new ModelAndView(view, Constants.AJAX_JSON, jsonObject);
	}

	/** 放弃编辑文档 */
	public ModelAndView cancelModifyDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long docResId = Long.valueOf(request.getParameter("docResId"));
		Long userId = CurrentUser.get().getId();
		docHierarchyManager.checkInDocResourceWithoutAcl(docResId, userId); 
		super.rendJavaScript(response, "window.close();");
		return null;
	}


	/** 修改一个文档  */
	public ModelAndView modifyDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long docResId = NumberUtils.toLong(request.getParameter("docResId"));
		DocResource docRes = docHierarchyManager.getDocResourceById(docResId);

		// 有效性判断
		if (docRes == null) {
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));" + 
										   "parent.window.returnValue = \"false\";" + 
										   "parent.window.close();");
			return null;
		}
		
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		String docName = request.getParameter("docName");
		long contentTypeId = NumberUtils.toLong(request.getParameter("contentTypeId"), Constants.DOCUMENT);
		long oldTypeId = Long.valueOf(request.getParameter("oldCTypeId"));
		Long userId = CurrentUser.get().getId();
		String description = request.getParameter("description");
		String keyword = request.getParameter("keyword");
		String versionComment = request.getParameter("versionComment");
		String originalFileId = request.getParameter("originalFileId");
		String fileId = request.getParameter("fileId");

		if(docRes.isVersionEnabled()) {
			this.docVersionInfoManager.saveDocVersionInfo(versionComment, originalFileId, docRes);
			if(Strings.isNotBlank(originalFileId) && docRes.isUploadOfficeOrWps()) {
				docRes.setSourceId(NumberUtils.toLong(fileId));
			}
		}
		
		// 扩展元数据
		if (contentTypeId != oldTypeId) {
			docMetadataManager.deleteMetadata(docRes);

			// 設置DocType，DocMetadataDef標記
			DocType contentType = contentTypeManager.getContentTypeById(contentTypeId);
			if (!contentType.getIsSystem() && contentType.getStatus() == Constants.CONTENT_TYPE_DRAFT) {
				contentType.setStatus(Constants.CONTENT_TYPE_PUBLISHED);
				contentTypeManager.updateContentType(contentType, false, null);

				this.metadataDefManager.updateMetadataDef4ContentType(contentType);
			}
		}
		this.handleMetadata(request, docResId, false);

		//上传文档  编辑后  转化为复合文档
		Long mimeTypeId = docRes.getMimeTypeId();
		boolean sampleToComFlag = false;
		if("true".equals(request.getParameter("isFile")) && "true".equals(request.getParameter("canEditOnline"))){
			if(mimeTypeId.intValue() == Constants.FORMAT_TYPE_ID_UPLOAD_DOC){
				mimeTypeId = Constants.FORMAT_TYPE_DOC_WORD;
			}else if(mimeTypeId.intValue() == Constants.FORMAT_TYPE_ID_UPLOAD_XLS){ 
				mimeTypeId = Constants.FORMAT_TYPE_DOC_EXCEL;
			}else if(mimeTypeId.intValue() == Constants.FORMAT_TYPE_ID_UPLOAD_WPS_DOC){
				mimeTypeId = Constants.FORMAT_TYPE_DOC_WORD_WPS;
			}else if(mimeTypeId.intValue() == Constants.FORMAT_TYPE_ID_UPLOAD_WPS_XLS){
				mimeTypeId = Constants.FORMAT_TYPE_DOC_EXCEL_WPS;
			}
			docRes.setMimeTypeId(mimeTypeId); 
			sampleToComFlag = true; 
		}
		// 修改文档记录,记录日志,发布文档消息
		DocResource newDoc = null;
		DocMimeType mime = docMimeTypeManager.getDocMimeTypeById(docRes.getMimeTypeId());
		if (mime.getFormatType() != Constants.FORMAT_TYPE_DOC_FILE) {
			FolderItemDoc fi = new FolderItemDoc(docRes);
			fi.setName(docName);
			fi.setContentTypeId(contentTypeId);
			fi.setDesc(description);
			fi.setKeywords(keyword);
			fi.setVersionComment(versionComment);
			
			DocBody docBody = new DocBody();
			Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
			if (bodyCreateDate != null) {
				docBody.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
			}
			try {
				bind(request, docBody);
			} catch (Exception e) {
				log.error("编辑器页面绑定body对象", e);
			}
			//上传文档  编辑后  转化为复合文档
			if(sampleToComFlag){
				docHierarchyManager.saveBody(docRes.getId(), docBody);
			}
			fi.setBody(docBody.getContent());
			String attFlag = null;
			try {
				attFlag = attachmentManager.update(ApplicationCategoryEnum.doc, docResId, docResId, request);
			} catch (Exception e) {
				log.error("更新附件", e);
			}
			boolean hasAtt = com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag);
			fi.setHasAtt(hasAtt);

			try {
				newDoc = docHierarchyManager.updateDocWithoutAcl(fi, userId);
			} catch (DocException e) {
				log.error("复合文档的更新", e);
			}
		} 
		else {  //上传的文档
			DocEditVO vo = new DocEditVO(docRes);
			boolean replace = Boolean.parseBoolean(request.getParameter("fileReplaceFlag"));

			if (replace) {
				V3XFile the_file = this.saveV3xFile(ApplicationCategoryEnum.doc, request);
				vo.setFile(the_file);
			} else {
				vo.setFile(null);
			}
			
			//因为office控件都是以2003格式保存，故把扩展名的s去掉
			if(Strings.isNotBlank(docName)){
				String[] suffix = docName.split("[.]");
				if(suffix!=null && suffix.length>1){
					int len = suffix.length;
					if("docx".equalsIgnoreCase(suffix[len-1])){
						docName = suffix[len-2]+".doc";
					}else if("xlsx".equalsIgnoreCase(suffix[len-1])){
						docName = suffix[len-2]+".xls";
					}else if("pptx".equalsIgnoreCase(suffix[len-1])){
						docName = suffix[len-2]+".ppt";
					}
				}
			}
			
			vo.setName(docName);
			vo.setContentTypeId(contentTypeId);
			vo.setDesc(description);
			vo.setKeywords(keyword);
			vo.setVersionComment(versionComment);

			// 附件和关联文档存储
			try {
				String attFlag = null;
				if (docRes.getCreateTime().compareTo(docRes.getLastUpdate()) == 0)
					attFlag = attachmentManager.create(ApplicationCategoryEnum.doc, docResId, docResId, request);
				else
					attFlag = attachmentManager.update(ApplicationCategoryEnum.doc, docResId, docResId, request);
				
				boolean hasAtt = com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag);
				vo.getDocResource().setHasAttachments(hasAtt);
			} catch (Exception e) {
				log.error("保存附件时出现异常:", e);
			}

			try {
				boolean remainOldFile = docRes.isVersionEnabled() && replace;
				newDoc = docHierarchyManager.updateFileWithoutAcl(vo, docLibType, remainOldFile,replace);
			} catch (DocException e) {
				log.error("更新文件[id=" + docResId + "]时出现异常:", e);
			}
		}

		// 日志
		if (!docLibType.equals(Constants.PERSONAL_LIB_TYPE)) {	
			operationlogManager.insertOplog(docResId, docRes.getParentFrId(),
					ApplicationCategoryEnum.doc, ActionType.LOG_DOC_EDIT_DOCUMENT_BODY, ActionType.LOG_DOC_EDIT_DOCUMENT_BODY + ".desc",
					CurrentUser.get().getName(), docName);
		}

		// 更新订阅文档
		docAlertLatestManager.addAlertLatest(newDoc, Constants.ALERT_OPR_TYPE_EDIT, userId, new Timestamp(new Date()
						.getTime()), Constants.DOC_MESSAGE_ALERT_MODIFY_EDIT, null);
		
		this.updateIndex(docResId);

		super.rendJavaScript(response, "parent.returnValueAndClose('true');");
		return null;
	}
	
	private void updateIndex(Long docResId) {
		// 更新全文检索
		try {
			updateIndexManager.update(docResId, ApplicationCategoryEnum.doc.getKey());
		}
		catch(Exception e) {
			logger.error("更新文档[id=" + docResId + "]全文检索信息时出现异常：", e);
		}
	}

	/** 保存V3xFile对象，文件替换时候使用  */
	public V3XFile saveV3xFile(ApplicationCategoryEnum category, HttpServletRequest request) {
		String fileUrl = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_fileUrl);
		String mimeType = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_mimeType);
		String size = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_size);
		String createdate = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_createDate);
		String filename = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_filename);
		String type = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_type);
		String needClone = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_needClone);
		String description = request.getParameter(Constants.FILEUPLOAD_INPUT_NAME_description);

		if (fileUrl == null || mimeType == null || size == null
				|| createdate == null || filename == null || type == null
				|| needClone == null) {
			return null;
		}

		Date originalCreateDate = Datetimes.parseDatetime(createdate);
		V3XFile file = new V3XFile();

		file.setCategory(category.key());
		file.setType(new Integer(type));
		file.setFilename(filename);
		file.setMimeType(mimeType);
		file.setSize(Long.parseLong(size));
		file.setDescription(description);

		User user = CurrentUser.get();
		file.setCreateMember(user.getId());
		file.setAccountId(user.getAccountId());

		boolean _needClone = Boolean.parseBoolean(needClone);
		if (_needClone) {
			Long newFileId = UUIDLong.longUUID();
			Date newCreateDate = new Date();
			try {
				fileManager.clone(new Long(fileUrl), originalCreateDate, newFileId, newCreateDate);
			} catch (Exception e) {
				log.error("Clone 附件", e);
			}

			file.setId(newFileId);
			file.setCreateDate(newCreateDate);
		} else {
			file.setId(Long.parseLong(fileUrl));
			file.setCreateDate(originalCreateDate);
		}

		fileManager.save(file);
		return file;
	}

	/** 添加一个文档  */
	public ModelAndView addDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.addVariousDocument(request, response, false);
	}
	
	/**
	 * 由于添加文档和添加项目文档两个方法重复代码过多，不利维护，进行简单抽取
	 * @param isProject		是否在关联项目页面添加项目文档
	 */
	@SuppressWarnings("unchecked")
	private ModelAndView addVariousDocument(HttpServletRequest request, HttpServletResponse response, boolean isProject) throws Exception {
		Long userId = CurrentUser.get().getId();
		long docLibId = Long.valueOf(request.getParameter("docLibId"));
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		String docName = request.getParameter("docName");
		boolean parentCommentEnabled = BooleanUtils.toBoolean(request.getParameter("parentCommentEnabled"));
		boolean parentVersionEnabled = BooleanUtils.toBoolean(request.getParameter("parentVersionEnabled"));
		
		long contentTypeId = NumberUtils.toLong(request.getParameter("contentTypeId"), Constants.DOCUMENT);
		DocType contentType = contentTypeManager.getContentTypeById(contentTypeId);
		String phaseIdStr = StringUtils.defaultString(request.getParameter("projectPhaseId"));
		
		Map<String, Comparable> paramap = this.getMetadataInfo(request);
		String description = request.getParameter("description");
		String keyword = request.getParameter("keyword");
		
		DocBody docBody = new DocBody();
		Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
		if (bodyCreateDate != null)
			docBody.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
		try {
			bind(request, docBody);
		} catch (Exception e) {
			log.error("编辑器页面绑定body", e);
		}
		
		Long folderId = Long.valueOf(request.getParameter("resId"));
		DocResource dr = docHierarchyManager.createDocWithoutAcl(docName, description,
				keyword, docBody, docLibId, folderId, userId,
				parentCommentEnabled, parentVersionEnabled, contentTypeId, paramap);
		Long id = dr.getId();
		
		if(isProject) {
			if(!String.valueOf(TaskConstants.PROJECT_PHASE_ALL).equals(phaseIdStr) && Strings.isNotBlank(phaseIdStr)) {
				ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.doc.key(), id, NumberUtils.toLong(phaseIdStr));
				projectPhaseEventManager.save(projectPhaseEvent);
			}
		}
		
		if (!contentType.getIsSystem() && contentType.getStatus() == Constants.CONTENT_TYPE_DRAFT) {
			contentType.setStatus(Constants.CONTENT_TYPE_PUBLISHED);
			
			if(isProject)
				contentTypeManager.updateContentType(contentType, false, null);
			else
				contentTypeManager.setContentTypePublished(contentType.getId());
			
			this.metadataDefManager.updateMetadataDef4ContentType(contentType);
		}
		
		// 记录操作日志
		operationlogManager.insertOplog(id, folderId, ApplicationCategoryEnum.doc, 
				ActionType.LOG_DOC_ADD_DOCUMENT, ActionType.LOG_DOC_ADD_DOCUMENT + ".desc", 
				CurrentUser.get().getName(), docName);
		
		String attFlag = null;
		try {
			attFlag = attachmentManager.create(ApplicationCategoryEnum.doc, id, id, request);
		} catch (Exception e) {
			log.error("保存附件", e);
		} 
		
		// 存储附件
		boolean hasAtt = com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag);
		try {
			if (hasAtt)
				docHierarchyManager.updateDocSize(id, docBody, attachmentManager.getByReference(id));
			else
				docHierarchyManager.updateDocSize(id, docBody, new ArrayList<Attachment>());
		} catch (DocException e) {
			log.error("更新文档大小", e);
		}
		
		// 更新订阅文档
		docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_ADD,
				userId, new Timestamp(System.currentTimeMillis()),
				Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);
		
		// 全文检索
		try {
			indexManager.index(docHierarchyManager.getIndexInfo(id));
		} catch (Exception e) {
			log.error("全文检索入库", e);
		}
		
		if(isProject) {
			String pid = request.getParameter("projectId");
			return super.redirectModelAndView("/project.do?method=projectInfo&projectId=" + pid + "&phaseId=" + phaseIdStr);
		}
		else {
			String flag = StringUtils.defaultIfEmpty(request.getParameter("flag"), "");
			return super.redirectModelAndView("/doc.do?method=rightNew&docLibId="
					+ docLibId + "&docLibType=" + docLibType + "&resId=" + folderId
					+ "&frType=" + request.getParameter("frType")
					+ "&isShareAndBorrowRoot=false" + "&all="
					+ request.getParameter("all") + "&edit="
					+ request.getParameter("edit") + "&add="
					+ request.getParameter("add") + "&readonly="
					+ request.getParameter("readonly") + "&browse="
					+ request.getParameter("browse") + "&list="
					+ request.getParameter("list") + "&parentCommentEnabled="
					+ parentCommentEnabled + "&parentVersionEnabled=" + parentVersionEnabled + "&flag=" + flag);
		}
			
	}

	/** 添加项目文档  */
	public ModelAndView addProDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.addVariousDocument(request, response, true);
	}

	/** 文档上传  */
	public ModelAndView docUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuffer  _fileName = new StringBuffer() ;
		int dupSize = 0;
		try {
			long docLibId = 0;
			Byte docLibType = 0;
			long docResourceId = 0;
			boolean parentCommentEnabled = false;
			boolean parentVersionEnabled = false;
			Long userId = CurrentUser.get().getId();
			
			String projectId = request.getParameter("projectId");
			Long projectPhaseId = NumberUtils.toLong(request.getParameter("projectPhaseId"), TaskConstants.PROJECT_PHASE_ALL);
			if(Strings.isNotBlank(projectId)){
				//从首页上传文件
				boolean isPhase = projectPhaseId != TaskConstants.PROJECT_PHASE_ALL;
				Long folderId = isPhase ? projectPhaseId : Long.valueOf(projectId);
				DocResource projectFolder = docHierarchyManager.getProjectFolderByProjectId(folderId, isPhase);
				if(projectFolder == null) {
					super.rendJavaScript(response, "window.location.reload(true);");
					return null;
				}
				
				docResourceId = projectFolder.getId().longValue();
				DocLib lib = docLibManager.getDocLibById(projectFolder.getDocLibId());
				docLibType = lib.getType();
				docLibId = projectFolder.getDocLibId();
				parentCommentEnabled = projectFolder.getCommentEnabled();
				parentVersionEnabled = projectFolder.isVersionEnabled();
			} else {
				docLibId = Long.valueOf(request.getParameter("docLibId"));
				docLibType = Byte.valueOf(request.getParameter("docLibType"));
				docResourceId = Long.valueOf(request.getParameter("docResourceId"));
				
				// 目标文档夹有效性判断
				if (!docHierarchyManager.docResourceExist(docResourceId)) {
					super.rendJavaScript(response, "window.location.reload(true);");
					return null;
				}
				
				parentCommentEnabled = Boolean.parseBoolean(request.getParameter("parentCommentEnabled"));
				parentVersionEnabled = Boolean.parseBoolean(request.getParameter("parentVersionEnabled"));
			}


			List<V3XFile> list = null;
			try {
				list = fileManager.create(ApplicationCategoryEnum.doc, request);
			} catch (BusinessException e) {
				log.error("通过fileManager保存file", e);
			}
			
			if (CollectionUtils.isEmpty(list))
				return null;
			
			String sysTemp = SystemEnvironment.getSystemTempFolder(); 
			String docTemp = sysTemp + "/doctemp/";
			File temp = new File(docTemp);
			temp.mkdir();
			for (V3XFile the_file : list) {
				DocResource dr = null ;
				try {
					dr = docHierarchyManager.uploadFileWithoutAcl( the_file, docLibId, docLibType, 
							docResourceId, userId, parentCommentEnabled, parentVersionEnabled);
					// 与项目阶段关联
					if(Strings.isNotBlank(projectId) && projectPhaseId != TaskConstants.PROJECT_PHASE_ALL){
						ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.doc.key(), dr.getId(), projectPhaseId);
						projectPhaseEventManager.save(projectPhaseEvent);
					}
				}
				catch(DocException e) {
					if(e.getMessage().equals("doc_upload_dupli_name_failure_alert")) {
						_fileName.append(the_file.getFilename() + " ");
						dupSize++;
						continue ;
					} else {
						throw e;
					}
				}
				
				Long newId = dr.getId();
				// 记录操作日志
				operationlogManager.insertOplog(newId, docResourceId, ApplicationCategoryEnum.doc,
						ActionType.LOG_DOC_UPLOAD, ActionType.LOG_DOC_UPLOAD + ".desc", 
						CurrentUser.get().getName(), the_file.getFilename());
				// 更新订阅文档
				docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_ADD, userId,
						new Timestamp(new Date().getTime()), Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);

				// 全文检索
				try {
					indexManager.index(docHierarchyManager.getIndexInfo(dr));
				} catch (Exception e) {
					log.error("全文检索入库", e);
				}

				// 上传图片类或PDF格式文件的处理：图片类转换成html格式存储，PDF保存对应正文
				if (dr.isImage() || dr.isPDF()) {
					DocBody body = new DocBody();
					body.setCreateDate(new Timestamp(System.currentTimeMillis()));
					body.setBodyType(dr.isImage() ? com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML : 
						com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
					body.setContent(dr.getSourceId().toString());
					this.docHierarchyManager.saveBody(newId, body);
				}
			}
			
			StringBuilder js = new StringBuilder();
			if(Strings.isNotBlank(_fileName.toString()))
				js.append("alert(parent.v3x.getMessage('DocLang.doc_upload_dupli_name_failure_alert','"+_fileName.toString() +"'));");
			
			if(list.size()>dupSize){
				if(Strings.isNotBlank(projectId)){
					js.append("parent.location.reload(true);");
				} else {
					js.append("parent.parent.rightFrame.location.reload(true);");
				}
			}
			
			if(Strings.isNotBlank(js.toString()))
				super.rendJavaScript(response, js.toString());
		} 
		catch (DocException e) {
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang." + e.getMessage() + "'));");
		}
		
		return null;
	}

	// 创建快捷方式
	public ModelAndView createLink(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = CurrentUser.get().getId();
		String[] docResIds = request.getParameterValues("id");
		long destDocLibId = Long.valueOf(request.getParameter("destLibId"));
		long destFolderId = Long.valueOf(request.getParameter("destResId"));
		String slibType = request.getParameter("destLibType");
		String destName = request.getParameter("destName");
		byte destLibType = Constants.PERSONAL_LIB_TYPE;
		if (slibType != null)
			destLibType = Byte.parseByte(slibType);
		try {
			String orgIds = Constants.getOrgIdsOfUser(userId);
			for (String docResId : docResIds) {
				// 有效性判断
				if (!docHierarchyManager.docResourceExist(Long.valueOf(docResId))) {
					continue;
				}

				DocResource dr = null;
				if(destLibType == Constants.PERSONAL_LIB_TYPE.byteValue())
					dr = docHierarchyManager.createLinkWithoutAcl(Long.valueOf(docResId), destDocLibId, destFolderId, userId);
				else
					dr = docHierarchyManager.createLink(Long.valueOf(docResId), destDocLibId, destFolderId, userId, orgIds);
				Long newId = dr.getId();
				
				DocResource newLink = docHierarchyManager.getDocResourceById(newId);
				if (destLibType != Constants.PERSONAL_LIB_TYPE.byteValue()) {
					// 记录操作日志
					operationlogManager.insertOplog(newId, destFolderId,
							ApplicationCategoryEnum.doc,
							ActionType.LOG_DOC_ADD_SHORTCUT,
							ActionType.LOG_DOC_ADD_SHORTCUT + ".desc",
							CurrentUser.get().getName(), newLink.getFrName(),
							destName);
				}

				if (newLink.getFrType() != Constants.LINK_FOLDER) {
					// 更新订阅文档
					docAlertLatestManager.addAlertLatest(dr,
							Constants.ALERT_OPR_TYPE_ADD, userId,
							new Timestamp(new Date().getTime()),
							Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);
				}

			}
			
			String close = "";
			if ((Boolean) BrowserFlag.OpenWindow.getFlag(request)) {
				close = "parent.parent.window.close();";
			} else {
				close = "parent.closeSendWindow();";
			}
			
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_send_shortcut_success_alert', '" + destName + "'));" + close);
		} catch (DocException e) {
			super.rendJavaScript(response, "parent.enableButtonsAndAlertMsg('" + e.getMessage() + "');");
		}
		return null;
	}

	// 文档替换
	public ModelAndView docReplace(HttpServletRequest request, HttpServletResponse response) throws IOException {
		V3XFile the_file = null;
		byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		Long docResId = NumberUtils.toLong(request.getParameter("docResId"));
		Long userId = CurrentUser.get().getId();
		String orgIds = Constants.getOrgIdsOfUser(userId);
		List<V3XFile> list;
		// 得到上传文件
		try {
			list = fileManager.create(ApplicationCategoryEnum.doc, request);
			if (CollectionUtils.isNotEmpty(list)) {
				the_file = list.get(0);
			}
		} catch (BusinessException e) {
			log.error("保存上传的文件时出现异常[id=" + docResId + "]：", e);
		}

		DocResource oprDr = docHierarchyManager.getDocResourceById(docResId);

		if (oprDr == null) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("window.location.reload(true);");
			out.println("</script>");
			return super.refreshWorkspace();
		}
		String oldFrName = oprDr.getFrName();
		String newFrName = the_file.getFilename();
		
		boolean versionEnabled = oprDr.isVersionEnabled();
		String versionComment = request.getParameter("versionComment");
		DocResource newDr = null;
		try {
			if(versionEnabled)
				this.docVersionInfoManager.saveDocVersionInfo(versionComment, oprDr);
			
			if (docLibType != Constants.PERSONAL_LIB_TYPE.byteValue())
				newDr = docHierarchyManager.replaceDoc(oprDr, the_file, userId, orgIds, versionEnabled);
			else
				newDr = docHierarchyManager.replaceDocWithoutAcl(oprDr, the_file, userId, versionEnabled);
		} catch (DocException e) {
			log.error("替换文件", e);
		}
		
		// 更新订阅文档
		docAlertLatestManager.addAlertLatest(newDr, Constants.ALERT_OPR_TYPE_EDIT, userId, 
				new Timestamp(new Date().getTime()), Constants.DOC_MESSAGE_ALERT_MODIFY_EDIT, null);
		
		this.updateIndex(docResId);
		
		// 替换文档记录日志
		if (docLibType != Constants.PERSONAL_LIB_TYPE.byteValue())
			this.operationlogManager.insertOplog(docResId, docResId, ApplicationCategoryEnum.doc, 
					ActionType.LOG_DOC_REPLACE, ActionType.LOG_DOC_REPLACE + ".desc", 
					CurrentUser.get().getName(), oldFrName, newFrName);

		super.rendJavaScript(response, "parent.parent.rightFrame.location.reload(true);");
		return null;
	}

	/** 进入归档界面  */
	public ModelAndView docPigeonhole(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docPigeonhole");
	}

	/** 归档  */
	public ModelAndView pigeonhole(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long userId = CurrentUser.get().getId();

			String appEnumKey = request.getParameter("appName");
			String ids = request.getParameter("ids");
			String atts = request.getParameter("atts");
			Long destLibId = Long.valueOf(request.getParameter("destLibId"));
			Long destFolderId = Long.valueOf(request.getParameter("destResId"));
			//lijl添加,为了区分"部门归档的来历",判断是不是从"发文管理"——"已办"中的"部门归档"中来的
			String departPigeonhole=request.getParameter("departPigeonhole");
			String newIds = "";
			String[] idsarray = ids.split(",");

			String[] attsarray = null;
			int attsize = 0;
			if (atts != null) {
				attsarray = atts.split(",");
				attsize = attsarray.length;
			}

			for (int i = 0; i < idsarray.length; i++) {
				Long sourceId = Long.valueOf(idsarray[i]);
				boolean has = false;
				if (attsize > i)
					has = Boolean.parseBoolean(attsarray[i]);
				Long newId = docHierarchyManager.pigeonholeAsLinkWithoutAcl(
						sourceId, has, Integer.parseInt(appEnumKey), destLibId,
						destFolderId, userId,departPigeonhole);

				newIds = newIds + "," + (newId == null ? "" : newId);

				// 更新订阅文档
				if (newId != null) {
					docAlertLatestManager.addAlertLatest(newId,
							Constants.ALERT_OPR_TYPE_ADD, userId,
							new Timestamp(new Date().getTime()),
							Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);

					// 全文检索
					try {
						indexManager.index(docHierarchyManager.getIndexInfo(newId));
					} catch (Exception e) {
						log.error("全文检索入库", e);
					}

					// 记录操作日志
					DocResource dr = this.docHierarchyManager.getDocResourceById(newId);
					
					operationlogManager.insertOplog(newId, destFolderId,
							ApplicationCategoryEnum.doc,
							ActionType.LOG_DOC_PIGEONHOLE,
							ActionType.LOG_DOC_PIGEONHOLE + ".desc",
							CurrentUser.get().getName(), Constants.getAppEnumI18nValue(appEnumKey), dr.getFrName());
				}
			}

			super.rendJavaScript(response, "window.top.returnValue = \"" + newIds.substring(1, newIds.length()) + "\";" +
										   "window.top.close();");
		} catch (DocException ex) {
			super.rendJavaScript(response, "parent.parent.window.document.getElementById('b1').disabled = false;" +
					"parent.parent.window.document.getElementById('b2').disabled = false;" +
					"alert(parent.v3x.getMessage('DocLang." + ex.getMessage() + "'));" +
					"parent.window.returnValue = \"failure\";");
		}
		return null;
	}

	/** 文档查看框架 */
	@EncoderQueryString
	public ModelAndView docOpenIframe(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView modelView = new ModelAndView("doc/docOpenIframe");
		// 历史版本信息
		if(Constants.VERSION_FLAG.equals(request.getParameter("versionFlag"))) {
			Long id = NumberUtils.toLong(request.getParameter("docVersionId"));
			DocVersionInfo dvi = this.docVersionInfoManager.getDocVersion(id);
			if(dvi == null) {
				super.rendJavaScript(response, "alert(window.dialogArguments.v3x.getMessage('DocLang.doc_history_not_exist'));" +
						   					   "parent.window.returnValue = \"true\";" +
						   					   "parent.close();");
				return null;
			}
			return modelView.addObject("docRes", dvi);
		}
		
		long id = Long.valueOf(request.getParameter("docResId"));
		DocResource dr = docHierarchyManager.getDocResourceById(id);

		// 文档有效性判断
		if (dr == null) {
			super.rendJavaScript(response, "alert(window.dialogArguments.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));" +
										   "parent.window.returnValue = \"true\";" +
										   "parent.close();");
			return null;
		}	

		// 链接判断
		if (dr.getFrType() == Constants.LINK) {
			dr = docHierarchyManager.getDocResourceById(dr.getSourceId());
			if (dr == null) {
				super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_source_doc_no_exist'));" +
											   "parent.window.returnValue = \"true\";" +
											   "parent.close();");
				return null;
			}
		}
		//SECURITY 访问安全检查
		if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.doc, CurrentUser.get(), id, null, null)){
			return null;
		}
		modelView.addObject("docRes", dr);
		return modelView;
	}

	/**
	 * 以只有id的方式进入打开界面，比如全文检索
	 * 因为传过来的只有docResourceId，所以这里需要判断文档的类型， 根据不同类型采取不同打开方式
	 * 复合文档+文件 文档链接 系统归档类型
	 */
	public ModelAndView docOpenIframeOnlyId(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("doc/docOpenIframeOnlyId");
		User user = CurrentUser.get();
		Long userId = user.getId();
		boolean docExist = true;
		boolean isLink = false;
		
		long id = Long.valueOf(request.getParameter("docResId"));
		DocResource dr = docHierarchyManager.getDocResourceById(id);
//		if(dr == null) {
//			 //TODO 逻辑不变前提下优化此处逻辑
//			 List<Affair> affairs = affairManager.findByObject(ApplicationCategoryEnum.doc,id);
//			 if(affairs != null){
//				for (Affair affair : affairs) {
//					if(affair.getSubObjectId() == null) {
//						 dr = docHierarchyManager.getDocResBySourceId(affair.getId());
//						 if(dr != null) {
//							 id = dr.getId();
//						 }
//						 break;
//					}
//				}
//			 }
//		}
		if (dr == null) {
			docExist = false;
			return modelView.addObject("docExist", docExist);
		}
		
		String linkId =request.getParameter("linkId");
		//如果是链接类型的文档则需要对应检查链接的id而非源id
		if (linkId!=null) {
			long lDocId = Long.valueOf(linkId);
			isLink = true;
			dr = docHierarchyManager.getDocResourceById(lDocId);
			if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.doc, user, lDocId, null, null)){
				return null;
			}
		}
		else if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.doc, user, id, null, null)){
			//SECURITY 访问安全检查
			return null;
		}

		// 大多数的 docOpenIframeOnlyId 在进来之前，都通过 js 做过了类型判断，不会是 doc_link
		// 只有当从首页的 portal 进入的时候，才可能是 doc_link
		// 也可能是归档类型
		DocResource parentOfLink = null;
		if (dr.getFrType() == Constants.LINK) {
			// 记录映射文件所在父文档夹
			Long parentFrId = dr.getParentFrId();
			dr = docHierarchyManager.getDocResourceById(dr.getSourceId());
			
			isLink = true;
			if (dr == null) {
				docExist = false;
				return modelView.addObject("docExist", docExist);
			}
			
			// 暂时只对office格式文件进行特别处理，满足庞大集团的特殊需求
			parentOfLink = this.docHierarchyManager.getDocResourceById(parentFrId);
		} else if (Constants.isPigeonhole(dr.getFrType())) {
			boolean pigExist = docHierarchyManager.hasPigeonholeSource(Constants.getAppEnum(dr.getFrType()), dr.getSourceId());
			if (!pigExist) {
				docExist = false;
				return modelView.addObject("docExist", docExist);
			}
		}
		
		DocLib lib = docLibManager.getDocLibById(dr.getDocLibId());
		
		boolean isPersonalLibOwner = false;
		boolean isBorrowOrShare = false;
		if (lib.isPersonalLib()) {
			if (docLibManager.isOwnerOfLib(userId, lib.getId())) {
				isPersonalLibOwner = true;
				dr.setIsMyOwn(true);
			} else {
				isBorrowOrShare = true;
			}
		}
		
		PigUrlInfo pui = DocMVCUtils.getPigUrlInfo(request, dr, isPersonalLibOwner, docAclManager);
		boolean pig = pui.isPig();
		String url = pui.getUrl();
		
		if (pig) {
			docHierarchyManager.accessOneTime(id, dr.getIsLearningDoc(), lib.isPersonalLib());
			modelView.addObject("_url", url);
			modelView.addObject("pig", pig);
			modelView.addObject("docExist", docExist);
			modelView.addObject("name", dr.getFrName());
			return modelView;
		}

		DocAclVO vo = null;
		// 链接文件如果源文件为office格式，则权限调整为对链接文件父文档夹的权限
		if(isLink && parentOfLink != null) {
			vo = new DocAclVO(parentOfLink);
		} else {
			vo = new DocAclVO(dr);
		}
		// 如果是点击借阅消息打开链接
		if("BorrowMsg".equals(request.getParameter("fromFlag"))) {
			String acl = this.docAclManager.getBorrowPotent(id);
			vo.setReadOnlyAcl('1' == acl.charAt(0));
			vo.setBrowseAcl('1' == acl.charAt(1));
			if("00".equals(acl)){
				PrintWriter out = null;
				try {
					out = response.getWriter();
				} catch (IOException e1) {
					logger.error("", e1);
				}
				out.println("<script>");
				out.println("alert('文档借阅已过期或未授权!');window.close();");
				out.println("</script>");
				out.close();
			}
		}
		else {
			this.setGottenAclsInVO(vo, userId, isBorrowOrShare);
		}
		vo.setDocResource(dr);
		
		modelView.addObject("vo", vo);
		modelView.addObject("docLibType", lib.getType());
		modelView.addObject("docExist", docExist);
		modelView.addObject("pig", pig);
		modelView.addObject("isLink" ,isLink);
		return modelView;
	}

	/** 文档查看iframe */
	public ModelAndView docOpenView(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docOpenView");
	}

	/** 查看文档正文 */
	public ModelAndView docOpenBody(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docOpenBody");
		User user = CurrentUser.get();
		boolean isHistory = Constants.VERSION_FLAG.equals(request.getParameter("versionFlag"));
		String resIdStr = request.getParameter(isHistory ? "docVersionId" : "docResId");
		Long resId = Long.valueOf(resIdStr);
		
		//SECURITY 访问安全检查
		if(!isHistory && !"true".equals(request.getParameter("isLink"))) {
			if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.doc, user, resId, null, null)){
				return null;
			}
		}
		
		byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isPersonal = (docLibType == Constants.PERSONAL_LIB_TYPE.byteValue());
		boolean isGroupLib = (docLibType == Constants.GROUP_LIB_TYPE.byteValue());
		
		DocResource dr = null;
		DocVersionInfo dvi = null;
		if(isHistory) {
			dvi = this.docVersionInfoManager.getDocVersion(resId);
			dr = dvi.getDocResourceFromXml();
		}
		else {
			dr = docHierarchyManager.getDocResourceById(resId);
		}
		
		DocOpenBodyVO vo = new DocOpenBodyVO(dr);
		this.setFolderItemRef(vo);

		long mimeTypeId = dr.getMimeTypeId();
		DocMimeType docMimeType = docMimeTypeManager.getDocMimeTypeById(mimeTypeId);
		if (docMimeType.isOffice()) {
			if(isHistory) {
				this.setBodyContent2VO(dvi.getDocBodyFromXml(), vo);
			} 
			else {
				this.setBodyContent2VO(resId, vo);
			}
		} 
		else if (dr.isImage() || dr.isPDF()) {
			DocBody docBody = isHistory ? dvi.getDocBodyFromXml() : docHierarchyManager.getBody(resId);

			try {
				vo.setCreateDate(this.fileManager.getV3XFile(dr.getSourceId()).getCreateDate());
			}
			catch (BusinessException e) {
				log.error("获取文件时出现异常[文件ID= " + dr.getSourceId() + "]", e);
			}
			if (docBody != null) {
				vo.setIsFile(true);
				vo.setBodyType(docBody.getBodyType());
//				vo.setCreateDate(docBody.getCreateDate());
				if(dr.isImage())
					vo.setBodyOfImage(dr.getSourceId(), vo.getCreateDate());
				else
					vo.setBody(docBody.getContent());
			} 
			// 以下代码似乎无用，应该不会出现这种情况，为了容错?
			else {
				if(dr.isImage()) {
					vo.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
					vo.setBodyOfImage(dr.getSourceId(), vo.getCreateDate());
				} else {
					vo.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
					vo.setBody(String.valueOf(dr.getSourceId()));
				}
			}
		} 
		else {
			String bodyType = Constants.getBodyType(dr.getMimeTypeId());
			vo.setIsFile(true);
			V3XFile file = null;
			try {
				file = fileManager.getV3XFile(dr.getSourceId());
				vo.setCreateDate(file.getCreateDate());
				vo.setCreateDateString(file.getCreateDate().toString().substring(0, 10));
			} catch (BusinessException e) {
				log.error("获取文件时出现异常[文件ID= " + dr.getSourceId() + "]", e);
			}
			vo.setFile(file);
			// ppt文件（104）或rtf文件转为html正文查看
			//TODO 不优美，建议重构，做成可配置的
			if(docMimeType.getId() == 104 || "text/rtf".equals(file.getMimeType())){
				if(OfficeTransHelper.isOfficeTran()&&OfficeTransHelper.allowTrans(file)){
					ret.addObject("requireTrans", true);
					ret.addObject("transUrl", OfficeTransHelper.buildCacheUrl(file, false));
				}
			}
			DocMimeType mime = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId());
			Object value = mime.getIcon();
			vo.setValue(value);
			DocPropVO docPropVO = this.getPropVOByDr(dr);
			vo.setSize(docPropVO.getSize());
			// 不可以在线编辑
			if (bodyType == null || dr.isImage()) {
				//  文本文件显示其正文内容，为避免其体积过大导致读取显示时占用资源过多，限制只显示1MB以内的文本文件内容，1MB以上提示其下载到本地阅读
				if(file != null) {
					String suffix = FilenameUtils.getExtension(file.getFilename()).toLowerCase();
					boolean isText = this.typesShowContentDirectlyTEXT.contains(suffix);
					boolean isHTML = this.typesShowContentDirectlyHTML.contains(suffix);
					if (isText || isHTML) {
						if (file.getSize() <= this.maxSize4ShowContent * 1024 * 1024) {
							long time1 = System.currentTimeMillis();
							
							String content = this.docHierarchyManager.getTextContent(file.getId());
							
							if(log.isDebugEnabled()) {
								log.debug("获取文本文件正文耗时：" + (System.currentTimeMillis() - time1) + "MS");
							}
							
							// 上传的是文本输出时需要toHTML，使得页面显示与在Notepad中的显示一致.上传的是html文件(后续支持，需解决很容易很容易出现的js报错问题)则不需
							vo.setBody(isHTML ? content : Strings.toHTML(content));
						}
						else {
							vo.setBody(Constants.getDocI18nValue("doc.txt.toolarge", this.maxSize4ShowContent));
						}
						ret.addObject("txtEdit", "txtEdit");
					}
				}
			} else {
				// word excel 在线编辑
				vo.setCanEditOnline(true);
				vo.setBodyType(bodyType);
				vo.setBody(dr.getSourceId() + "");
			}
		}

		if (!isHistory && (vo.getDocResource().getCommentEnabled() || vo.getDocResource().getCommentCount() > 0)) {
			List<DocForum> forums = docForumManager.findFirstForumsByDocId(dr.getId());
			vo.setForums(this.getForumVOList(forums, isGroupLib));
			ret.addObject("fourm", true);
		}

		return ret.addObject("vo", vo).addObject("isPersonalLib", isPersonal).addObject("isHistory", isHistory);
	}

	/** 文档评论 */
	public ModelAndView docOpenForum(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docOpenForum");
		Long docResId = Long.valueOf(request.getParameter("docResId"));

		byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isPersonal = (docLibType == Constants.PERSONAL_LIB_TYPE.byteValue());
		boolean isGroupLib = (docLibType == Constants.GROUP_LIB_TYPE.byteValue());

		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		DocOpenBodyVO vo = new DocOpenBodyVO(dr);
		this.setFolderItemRef(vo);

		DocMimeType docMimeType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId());
		if (docMimeType.isOffice()) {
			this.setBodyContent2VO(docResId, vo);
		} 
		else {
			String bodyType = Constants.getBodyType(dr.getMimeTypeId());
			vo.setIsFile(true);
			V3XFile file = null;
			try {
				file = fileManager.getV3XFile(dr.getSourceId());
				vo.setCreateDate(file.getCreateDate());
				vo.setCreateDateString(file.getCreateDate().toString().substring(0, 10));
			} catch (BusinessException e) {
				log.error("取得V3xfile", e);
			}
			vo.setFile(file);
			if (bodyType == null) {
				// 不可以在线编辑
			} else {
				// word excel 在线编辑
				vo.setCanEditOnline(true);
				vo.setBodyType(bodyType);
				vo.setBody(dr.getSourceId() + "");
			}
		}

		if (vo.getDocResource().getCommentCount() > 0) {
			List<DocForum> forums = docForumManager.findFirstForumsByDocId(docResId);
			vo.setForums(this.getForumVOList(forums, isGroupLib));
		}

		ret.addObject("vo", vo);
		ret.addObject("isPersonalLib", isPersonal);

		return ret;
	}

	private void setBodyContent2VO(Long docResId, DocOpenBodyVO vo) {
		DocBody docBody = docHierarchyManager.getBody(docResId);
		this.setBodyContent2VO(docBody, vo);
	}

	private void setBodyContent2VO(DocBody docBody, DocOpenBodyVO vo) {
		vo.setIsFile(false);
		String body = "";
		String bodyType = "";
		if (docBody != null) {
			body = docBody.getContent();
			bodyType = docBody.getBodyType();
			// 创建日期
			Date createDate = docBody.getCreateDate();
			if (createDate != null)
				vo.setCreateDate(createDate);
		}
		vo.setBody(body);
		vo.setBodyType(bodyType);
	}

	/** 添加文档评论界面进入 */
	public ModelAndView docForumAddView(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docForumAdd");
	}

	/** 添加文档评论  */
	public ModelAndView docForumAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String content = request.getParameter("content");
		Long docResId = NumberUtils.toLong(request.getParameter("docResId"));
		DocResource dr = this.docHierarchyManager.getDocResourceById(docResId);
		// 存在性判断
		if (dr == null) {
			super.rendJavaScript(response, "alert(window.dialogArguments.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));" +
										   "parent.window.dialogArguments.parent.location.reload(true);" +
										   "parent.window.close();");
			return null;
		}

		Long userId = CurrentUser.get().getId();
		DocForum docForum = docForumManager.pubDocForum(docResId, 0L, "", content, userId);
		// 更新订阅文档
		docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_FORUM, userId, new Timestamp(
						new Date().getTime()), Constants.DOC_MESSAGE_ALERT_COMMENT, null);
		this.updateIndex(docResId);

		String timeFormat = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, "common.datetime.pattern");
		super.rendJavaScript(response, "parent.addForum('" + docForum.getId() + "','"
				+ Datetimes.format(docForum.getCreateTime(), timeFormat) + "')");
		return null;
	}

	/** 回复文档评论   */
	public ModelAndView docForumReply(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String content = request.getParameter("content");
		Long docResId = NumberUtils.toLong(request.getParameter("docResId"));

		// 存在性判断
		DocResource dr = this.docHierarchyManager.getDocResourceById(docResId);
		if (dr == null) {
			super.rendJavaScript(response, "alert(window.dialogArguments.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));" +
										   "parent.top.close();");
			return null;
		}

		Long forumId = Long.valueOf(request.getParameter("forumId"));
		Long userId = CurrentUser.get().getId();

		DocForum docForum = docForumManager.pubDocForum(docResId, forumId, "", content, userId);
		// 更新订阅文档
		docAlertLatestManager.addAlertLatest(dr, Constants.ALERT_OPR_TYPE_FORUM, userId, new Timestamp(
						new Date().getTime()), Constants.DOC_MESSAGE_ALERT_COMMENT, null);
		this.updateIndex(docResId);

		String timeFormat = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, "common.datetime.pattern");
		String dateTimeStr = Datetimes.format(docForum.getCreateTime(), timeFormat);
		super.rendJavaScript(response, "parent.replyOK('" + docForum.getId() + "','" + dateTimeStr + "')");
		return null;
	}

	/**
	 * 删除文档评论及回复。
	 */
	public ModelAndView deleteDocForum(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long docResId = Long.valueOf(request.getParameter("docResId"));

		// 存在性判断
		if (!docHierarchyManager.docResourceExist(docResId)) {
			super.rendJavaScript(response, "alert(window.dialogArguments.v3x.getMessage('DocLang.doc_alert_source_deleted_doc'));" +
										   "window.dialogArguments.parent.location.reload(true);" +
										   "parent.close();");
			return null;
		}

		boolean flag = Boolean.valueOf(request.getParameter("flag"));
		long forumId = Long.valueOf(request.getParameter("forumId"));
		if (flag) {
			docForumManager.deleteDocForumAndReply(forumId);
		} else {
			docForumManager.deleteReply(forumId);
		}
		return this.docOpenForum(request, response);
	}

	@Deprecated
	public ModelAndView docDownloadNewWindow(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docDownloadIframe");
		String docResourceId = request.getParameter("id");
		boolean flag = this.docHierarchyManager.docDownloadCompress(Long.valueOf(docResourceId));
		mav.addObject("flag", flag);
		return mav;
	}
	
	/**
	 * 批量下载C++程序用的
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SetContentType
	public ModelAndView docDownloadNew4Multi(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String docResourceId = request.getParameter("id");
		
//		String lentpotent2 = docAclManager.getBorrowPotent(Long.parseLong(docResourceId));
//		if(lentpotent2 != null && lentpotent2.substring(0, 1) != "1") {
//			PrintWriter out = response.getWriter();
//			out.println("2");
//			out.close();
//			return null;
//      	}
		
		if(!this.docHierarchyManager.docResourceExist(Long.parseLong(docResourceId))){
			response.addHeader("Rang", "-1"); //不存在
			return null;
		}
		
		this.docHierarchyManager.docDownloadCompress(Long.parseLong(docResourceId));
		
		return docDownloadNew(request, response);
	}

	/** 下载复合文档 */
	@SetContentType
	public ModelAndView docDownloadNew(HttpServletRequest request, HttpServletResponse response) {
		String docResourceId = request.getParameter("id");
		File zipFile = DocHierarchyManagerImpl.downloadMap.get(docResourceId);
		if (zipFile == null){
			response.addHeader("FileDownloadError", "1"); //不存在
			return null;
		}
		InputStream in = null;
		OutputStream out = null;
		String zipfilename = zipFile.getName();
		try {
			in = new FileInputStream(zipFile);
			if (in == null) {
				ModelAndView modelAndView = new ModelAndView("common/fileUpload/error");
				modelAndView.addObject("error", "FileNoFound");
				modelAndView.addObject("filename", zipfilename);
				return modelAndView;
			} 
			else {
				zipfilename = URLEncoder.encode(zipfilename, "UTF-8");
				response.setContentType("application/x-msdownload; charset=UTF-8");
				response.setHeader("Content-disposition", "attachment;filename=\"" + zipfilename.replace("+", " ") + "\"");
				response.setContentLength(in.available());
				out = response.getOutputStream();
				CoderFactory.getInstance().download(in, out);
			}
		} 
		catch (Exception e) {
			if (!e.getClass().getSimpleName().equals("ClientAbortException")) {
				ModelAndView modelAndView = new ModelAndView("common/fileUpload/error");
				modelAndView.addObject("error", "Exception");
				modelAndView.addObject("filename", zipfilename);

				return modelAndView;
			}
		} 
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
		/** **************** 删除临时文件开始 *********************** */

		try {
			DocHierarchyManagerImpl.downloadMap.remove(docResourceId);
			zipFile.delete();
		} catch (Exception e) {
			// log.error("复合文档下载", e);
		}

		/** **************** 删除临时文件结束 *********************** */

		return null;
	}

	/** 打开页面的菜单 */
	public ModelAndView docOpenMenu(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docOpenMenu");
		boolean isHistory = Constants.VERSION_FLAG.equals(request.getParameter("versionFlag"));
		String resIdStr = request.getParameter(isHistory ? "docVersionId" : "docResId");
		Long resId = Long.valueOf(resIdStr);
		
		if (!isHistory && !"true".equals(request.getParameter("isLink"))) {
			// SECURITY 访问安全检查
			if (!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.doc, CurrentUser.get(), resId, null, null)) {
				return null;
			}
		}
		
		DocResource dr = getDocResource4Show(isHistory, resId);
		String createDate = dr.getCreateTime().toString().substring(0, 10);
		boolean isUploadFile = false;
		boolean commentEnabled = dr.getCommentEnabled();
		
		Long downloadId = resId;
		DocBody docBody = docHierarchyManager.getBody(dr.getId());
		if(docBody != null){
			ret.addObject("bodyType", docBody.getBodyType());
		}
		
		long formatType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getFormatType();
		if (formatType == Constants.FORMAT_TYPE_DOC_FILE) {
			isUploadFile = true;
			downloadId = dr.getSourceId();
			
			ret.addObject("bodyType", Constants.getBodyType(dr.getMimeTypeId()));

			// 对于上传文件，因为可能出现替换情况，导致新建时间不一致，所以应该从系统取。解决下载的定位问题
			try {
				V3XFile file = fileManager.getV3XFile(downloadId);
				createDate = file.getCreateDate().toString().substring(0, 10);
			} catch (BusinessException e) {
				log.error("从fileManager取得V3xFile, ", e);
			}

		}
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		boolean isPrivateLib = docLibType.equals(Constants.PERSONAL_LIB_TYPE);

		// 访问次数记录
		// 2007.07.23 因为 v3x.openWindow()会自动对同一 url
		// 的外层框架页面缓存，所以访问次数从iframe转移到menu
		// 不能使用 docLibType 进行文档类型判断，因为
		// docLibType 记录当前用户所在库
		// 而 单位借阅 中的文档属于公共库，需要记录打开数据，但此时 libtype 是 personal
		if(!isHistory) {
			DocLib lib = docLibManager.getDocLibById(dr.getDocLibId());
	
			docHierarchyManager.accessOneTime(resId, dr.getIsLearningDoc(), isPrivateLib);
			// 对需要记录查看日志的文档库记录日志
			if (lib.getLogView()) {
				operationlogManager.insertOplog(resId, dr.getParentFrId(),
						ApplicationCategoryEnum.doc, ActionType.LOG_DOC_VIEW,
						ActionType.LOG_DOC_VIEW + ".desc", CurrentUser.get().getName(), dr.getFrName());
			}
	
			boolean isAdministrator = Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_ACCOUNTADMIN, CurrentUser.get());
			boolean isGroupAdmin = this.canAdminGroup();
			ret.addObject("isAdministrator", isAdministrator);
			ret.addObject("isGroupAdmin", isGroupAdmin);
			
			int depAdminSize = DocMVCUtils.getDepSetAdmin(this.spaceManager).size();
	
			ret.addObject("depAdminSize", depAdminSize);
			ret.addObject("commentEnabled", commentEnabled);
			
			String lockMsg = this.docHierarchyManager.getLockMsg(dr.getId(), CurrentUser.get().getId());
			boolean isLocked = !Constants.LOCK_MSG_NONE.equals(lockMsg);
			ret.addObject("isLocked", isLocked);
			ret.addObject("lockMsg", lockMsg);
		}

		ret.addObject("dr", dr).addObject("isHistory", isHistory);
		ret.addObject("isEdocLib", docLibType.byteValue() == Constants.EDOC_LIB_TYPE.byteValue());
		ret.addObject("createDate", createDate).addObject("isUploadFile", isUploadFile).addObject("canPrint4Upload", dr.canPrint4Upload());
		ret.addObject("downloadId", downloadId).addObject("isPrivateLib", isPrivateLib);
		return ret.addObject("isGroupLib", (docLibType.byteValue() == Constants.GROUP_LIB_TYPE.byteValue()));
	}

	/** 打开页面的页签部分 */
	public ModelAndView docOpenLabel(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docOpenLabel");
		boolean isHistory = Constants.VERSION_FLAG.equals(request.getParameter("versionFlag"));
		String resIdStr = request.getParameter(isHistory ? "docVersionId" : "docResId");
		Long resId = Long.valueOf(resIdStr);
    	
		DocResource dr = null;
		if(isHistory) {
			DocVersionInfo dvi = this.docVersionInfoManager.getDocVersion(resId);
			dr = dvi.getDocResourceFromXml();
		}
		else {
			dr = docHierarchyManager.getDocResourceById(resId);
		}
		DocPropVO docPropVO = this.getPropVOByDr(dr);
		ret.addObject("prop", docPropVO);
		
		long formatType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getFormatType();
		boolean isUploadFile = formatType == Constants.FORMAT_TYPE_DOC_FILE;
		ret.addObject("isUploadFile", isUploadFile);
		
		int metaSize = contentTypeManager.hasExtendMetadata(docPropVO.getDocResource().getFrType()) ? 1 : 0;
		String metaHtml = "";
		if (metaSize > 0)
			metaHtml = isHistory ? htmlUtil.getHistoryViewHtml(resId) : htmlUtil.getViewHtml(resId);

		ret.addObject("extendSize", metaSize);
		ret.addObject("metadataHtml", metaHtml);
		
		List<Attachment> atts = attachmentManager.getByReference(resId);
		ret.addObject("atts", atts);
		ret.addObject("attSize", atts == null ? 0 : atts.size());

		DocLib lib = docLibManager.getDocLibById(docPropVO.getDocResource().getDocLibId());
		boolean isPersonalLib = lib != null && lib.isPersonalLib();
		return ret.addObject("isPersonalLib", isPersonalLib);
	}
	
	/** 取得属性 */
	private DocPropVO getPropVOByDr(DocResource dr) {
		DocPropVO pvo = new DocPropVO(dr);
		pvo.setPath(this.getPhysicalPath(dr.getLogicalPath()));
		pvo.setIcon(this.getIcon(dr.getIsFolder(), dr.getMimeTypeId()));
		this.setFolderItemRef(pvo);

		pvo.setIsShortCut(dr.getFrType() == Constants.LINK || dr.getFrType() == Constants.LINK_FOLDER);
		pvo.setIsPigeonhole(Constants.isPigeonhole(dr.getFrType()));
		return pvo;
	}

	/** 取得图片名称 */
	private String getIcon(boolean isFolder, Long mimeTypeId) {
		String icon = docMimeTypeManager.getDocMimeTypeById(mimeTypeId).getIcon();
		if (isFolder && !icon.equals("")){
			if(icon.indexOf("|") != -1){
				icon = icon.substring(0, icon.indexOf("|"));			
			}
		}
		return icon;
	}

	// 取得文档夹属性
	private FolderItemFolder getFolderPropVO(HttpServletRequest request) {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		FolderItemFolder folder = new FolderItemFolder(dr);
		folder.setPath(this.getPhysicalPath(dr.getLogicalPath()));
		folder.setIcon(this.getIcon(dr.getIsFolder(), dr.getMimeTypeId()));
		this.setFolderItemRef(folder);
		return folder;
	}

	// 保存文档属性
	@SuppressWarnings("unchecked")
	private void saveDocProp(HttpServletRequest request, HttpServletResponse response, boolean saveVersion) {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		Long userId = CurrentUser.get().getId();
		Map paramap = new HashMap();
		Map<String, String[]> srcParaMap = request.getParameterMap();
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		boolean versionEnabled = false;
		String versionComment = null;
		
		Map<String, Object> namedParams = new HashMap<String, Object>();
		boolean isEdoc = dr.getFrType() == Constants.SYSTEM_ARCHIVES;
		for (Iterator<Entry<String, String[]>> iter = srcParaMap.entrySet().iterator(); iter.hasNext(); ) {
			Entry<String, String[]> entry = iter.next();
			String sname = entry.getKey();
			if (sname.equals("docDesc")) {
				namedParams.put("frDesc", entry.getValue()[0]);
			}
			else if (sname.equals("commentEnabled")) {
				boolean ce = Boolean.valueOf(entry.getValue()[0]);
				namedParams.put("commentEnabled", ce);
				if (!ce && !isEdoc)
					docAlertManager.deleteAlertByDocResourceIdAndAlertType(docResId);
			} 
			else if (sname.equals("docKeywords")) {
				namedParams.put("keyWords", entry.getValue()[0]);
			} 
			if(!isEdoc) {
				if (sname.equals("versionComment")) {
					versionComment = entry.getValue()[0];
					namedParams.put("versionComment", versionComment);
				}
				else if(sname.equals("versionEnabled")) {
					versionEnabled = Boolean.valueOf(entry.getValue()[0]);
					namedParams.put("versionEnabled", versionEnabled);
				}
				else if (this.needHandleMetadata(sname)) {
					// 处理多值元数据
					String[] values = (String[]) entry.getValue();
					this.addMetadataKV(paramap, sname, values);
				}
			}
		}
		
		if(versionEnabled && saveVersion)
			this.docVersionInfoManager.saveDocVersionInfo(versionComment, dr);
		
		namedParams.put("lastUpdate", new Timestamp(System.currentTimeMillis()));
		namedParams.put("lastUserId", userId);
		this.docHierarchyManager.updateDocResource(docResId, namedParams);
		if (!paramap.isEmpty())
			docMetadataManager.updateMetadata(docResId, paramap);

		DocResource destDr = docHierarchyManager.getDocResourceById(docResId);
		// 记录操作日志
		operationlogManager.insertOplog(docResId, destDr.getParentFrId(),
				ApplicationCategoryEnum.doc, ActionType.LOG_DOC_EDIT_DOCUMENT,
				ActionType.LOG_DOC_EDIT_DOCUMENT + ".desc", CurrentUser.get().getName(), destDr.getFrName());

		docAlertLatestManager.addAlertLatest(destDr, Constants.ALERT_OPR_TYPE_EDIT, userId, 
				new Timestamp(System.currentTimeMillis()), Constants.DOC_MESSAGE_ALERT_MODIFY_EDIT, destDr.getFrName());
	}

	/** 保存文档夹属性 */
	@SuppressWarnings("unchecked")
	private void saveFolderProp(HttpServletRequest request) throws DocException {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource drs = docHierarchyManager.getDocResourceById(docResId);
		
		Long userId = CurrentUser.get().getId();
		String orgIds = Constants.getOrgIdsOfUser(userId);
		Map metadatas = new HashMap();
		Map<String, Object> properties = new HashMap<String, Object>();
		
		Map<String, String[]> srcParaMap = request.getParameterMap();
		for (Iterator<Entry<String, String[]>> iter = srcParaMap.entrySet().iterator(); iter.hasNext(); ) {
			Entry<String, String[]> entry = iter.next();
			String sname = entry.getKey();
			if (sname.equals("folderDesc")) {
				properties.put("frDesc", entry.getValue()[0]);
			} 
			else if (sname.equals("subfolderEnabled")) {
				boolean se = Boolean.valueOf(entry.getValue()[0]);
				boolean hasPermission = this.docHierarchyManager.hasEditPermission(drs, userId, orgIds);
				if(hasPermission) {
					properties.put("subfolderEnabled", se);
				}
			} 
			else if("foldVersionEnabled".equals(sname)) {
				boolean fveChangeFlag = Boolean.parseBoolean(request.getParameter("fveChangeFlag"));
				if(fveChangeFlag) {
					int editScopeAll = NumberUtils.toInt(request.getParameter("appAllVersion"));
					if(editScopeAll != -1l) {
						boolean fve = Boolean.valueOf(entry.getValue()[0]);
						docHierarchyManager.setFolderVersionEnabled(drs, fve, editScopeAll, userId);
					}
				}
			}
			else if ("foldCommentEnabled".equals(sname)) {
				boolean fceChangeFlag = Boolean.parseBoolean(request.getParameter("fceChangeFlag"));
				if (fceChangeFlag) {
					int editScopeAll = NumberUtils.toInt(request.getParameter("appAll"));
					if(editScopeAll != -1l) {
						boolean fce = Boolean.valueOf(entry.getValue()[0]);
						docHierarchyManager.setFolderCommentEnabled(drs, fce, editScopeAll, userId);
					}
				}
			} 
			else if (this.needHandleMetadata(sname)) {
				this.addMetadataKV(metadatas, sname, entry.getValue());
			}
		}
		
		this.docHierarchyManager.updateDocResource(docResId, properties);
		if (!metadatas.isEmpty())
			docMetadataManager.updateMetadata(docResId, metadatas);

		// 记录操作日志
		operationlogManager.insertOplog(docResId, drs.getParentFrId(), ApplicationCategoryEnum.doc, 
				ActionType.LOG_DOC_EDIT_FOLDER, ActionType.LOG_DOC_EDIT_FOLDER + ".desc", 
				CurrentUser.get().getName(), Constants.getDocI18nValue(drs.getFrName()));
		
		drs = this.docHierarchyManager.getDocResourceById(docResId);
		docAlertLatestManager.addAlertLatest(drs, Constants.ALERT_OPR_TYPE_EDIT, userId, 
				new Timestamp(System.currentTimeMillis()), Constants.DOC_MESSAGE_ALERT_MODIFY_EDIT, drs.getFrName());

	}
	
	/** 单个属性的简单查询 */
	public ModelAndView simpleQuery(HttpServletRequest request, HttpServletResponse response) {
		return this.simpleOrAdvancedQuery(request, response, true);
	}
	
	/** 多个属性组合起来的高级查询 */
	public ModelAndView advancedQuery(HttpServletRequest request, HttpServletResponse response) {
		return this.simpleOrAdvancedQuery(request, response, false);
	}
	
	private ModelAndView simpleOrAdvancedQuery(HttpServletRequest request, HttpServletResponse response, boolean isSimple) {
		ModelAndView ret = new ModelAndView(isSimple ? "/doc/rightNew" : "/doc/advancedSearchResult");
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		Long docLibId = Long.valueOf(request.getParameter("docLibId"));
		DocLib docLib = docLibManager.getDocLibById(docLibId);
		DocMVCUtils.returnVaule(ret, docLibType, docLib, request, this.contentTypeManager, this.docLibManager);
		
		Long resId = Long.valueOf(request.getParameter("resId"));
		Long frType = Long.valueOf(request.getParameter("frType"));
		DocResource parent = this.getParenetDocResource(resId, frType);
		ret.addObject("parent", parent);
		
		List<DocTableVO> docs = this.getQueryResultVOs(request, parent, docLibId, docLibType, ret, isSimple);
		ret.addObject("docs", docs);
		this.initRightAclData(request, ret);
		return ret;
	}
	
	private DocResource getParenetDocResource(Long folderId, Long frType){
		DocResource folder = docHierarchyManager.getDocResourceById(folderId);		
		if (frType == Constants.PERSON_BORROW || frType == Constants.PERSON_SHARE || frType == Constants.DEPARTMENT_BORROW) {
			folder = new DocResource();
			folder.setId(folderId);
			folder.setFrType(frType);
			String name2 = null;
			if (frType == Constants.DEPARTMENT_BORROW)
				name2 = Constants.DEPARTMENT_BORROW_KEY;
			else
				name2 = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, folderId, false);
			folder.setFrName(name2);
		}
		return folder ;
	}
	
	private List<DocTableVO> getQueryResultVOs(HttpServletRequest request,
			DocResource parent, Long docLibId, Byte docLibType, ModelAndView ret, boolean isSimpleQuery) {
		Long resId = NumberUtils.toLong(request.getParameter("resId"));
		List<DocResource> result1 = null;
		if(isSimpleQuery) {
			SimpleDocQueryModel simpleQueryModel = SimpleDocQueryModel.parseRequest(request);
			ret.addObject("simpleQueryModel", simpleQueryModel);
			result1 = this.docHierarchyManager.getSimpleQueryResult(simpleQueryModel, resId, docLibType);
		}
		else {
			DocSearchModel dsm = DocSearchModel.parseRequest(request);
			result1 = this.docHierarchyManager.getAdvancedQueryResult(dsm, resId, docLibType);
		}
		List<DocResource> result = result1;
		
		List<DocMetadataDefinition> dmds = docLibManager.getListColumnsByDocLibId(docLibId);
		Long userId = CurrentUser.get().getId();
		boolean isShareAndBorrow = BooleanUtils.toBoolean(request.getParameter("isShareAndBorrowRoot"));
		List<DocTableVO> docs = this.getTableVOs(result, dmds, ret, userId, isShareAndBorrow, docLibType, parent);
		return docs;
	}

	// 查找复合文档的内容
	public FolderItemDoc getFolderItemDoc(DocResource dr) {
		FolderItemDoc fid = new FolderItemDoc(dr);
		fid.setPath(this.getPhysicalPath(dr.getLogicalPath()));
		fid.setIcon(this.getIcon(dr.getIsFolder(), dr.getMimeTypeId()));
		fid.setAtts(attachmentManager.getByReference(dr.getId()));

		DocBody body = docHierarchyManager.getBody(dr.getId());
		fid.setBody(body.getContent());

		if (dr.getIsCheckOut()) {
			String checkname = "";
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(dr.getCheckOutUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				checkname = member.getName();
			fid.setCheckOutUserName(checkname);
		}
		this.setFolderItemRef(fid);
		return fid;
	}

	// 设置forumVo
	private List<DocForumVO> getForumVOList(List<DocForum> forums, boolean isGroupLib) {
		List<DocForumVO> vos = new ArrayList<DocForumVO>();
		if(CollectionUtils.isNotEmpty(forums)) {
			for (DocForum forum : forums) {
				DocForumVO vo = new DocForumVO(forum);
				String name = this.getForumUserName(forum.getCreateUserId(), isGroupLib);
				vo.setName(name);
				
				// 回复
				List<DocForum> replys = docForumManager.findReplyByForumId(forum.getId());
				List<DocForumReplyVO> replyvos = new ArrayList<DocForumReplyVO>();
				if(CollectionUtils.isNotEmpty(replys)) {
					for (DocForum tr : replys) {
						DocForumReplyVO trvo = new DocForumReplyVO(tr);
						String name2 = this.getForumUserName(tr.getCreateUserId(), isGroupLib);
						trvo.setName(name2);
						replyvos.add(trvo);
					}
				}
				vo.setReplys(replyvos);
	
				vos.add(vo);
			}
		}
		return vos;
	}
	
	/**
	 * 获取评论或评论回复创建者在文档评论区域的显示名称
	 * @param createUserId	评论或回复创建者
	 * @param isGroupLib	是否集团文档库
	 * @return	评论区域的显示名称
	 */
	private String getForumUserName(Long createUserId, boolean isGroupLib) {
		String name = "";
		String userName = "";
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(createUserId);
			if (member != null)
				userName = Constants.getOrgEntityName("Member", createUserId, isGroupLib);
			name = userName;
		} 
		catch (BusinessException e) {
			log.error("orgManager取得member", e);
		}
		return name;
	}

	/**
	 * 设置 FolderItem 中引用型属性
	 * @param item
	 */
	private void setFolderItemRef(FolderItem item) {
		DocResource dr = item.getDocResource();
		item.setCreateUserName(this.getUserName(dr.getCreateUserId()));
		item.setLastUserName(this.getUserName(dr.getLastUserId()));
		String typeName = contentTypeManager.getContentTypeById(dr.getFrType()).getName();
		item.setType(Constants.getDocI18nValue(typeName));
	}
	
	private String getUserName(Long userId) {
		V3xOrgMember user = null;
		try {
			user = orgManager.getMemberById(userId);
		} catch (BusinessException e) {
			log.error("orgManager取得member", e);
		}
		return user == null ? "" : user.getName();
	}

	// 得到某个节点的物理路径
	private String getPhysicalPath(String logicalPath) {
		if (Strings.isBlank(logicalPath))
			return "";

		StringBuffer sb = new StringBuffer("");
		// 2008.04.01 不显示自己
		String[] arr = logicalPath.split("\\.");
		String ids = "";
		for (int i = 0; i < arr.length; i++) {
			ids += "," + arr[i];
		}

		List<DocResource> list = this.docHierarchyManager.getDocsByIds(ids.substring(1));
		if (list == null || list.size() == 0)
			return "";
		Map<String, DocResource> map = new HashMap<String, DocResource>();
		for (DocResource td : list) {
			map.put(td.getId().toString(), td);
		}

		for (int i = 0; i < (arr.length == 1 ? 1 : (arr.length - 1)); i++) {
			DocResource td = map.get(arr[i]);
			if (td == null)
				continue;
			sb.append("\\");
			String key = td.getFrName();
			if (Constants.needI18n(td.getFrType()))
				key = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME,
						key);
			sb.append(key);
		}
		return sb.toString();
	}

	/**
	 * 取得某个文档（夹）的路径
	 */
	private String getLocation(String logicalPath) {
		List<DocResource> locList = new ArrayList<DocResource>();
		if (Strings.isBlank(logicalPath))
			return "";

		// 2008.04.01 不显示自己
		String[] arr = logicalPath.split("\\.");
		String ids = StringUtils.join(arr, ',');
		List<DocResource> list = this.docHierarchyManager.getDocsByIds(ids);
		if (list == null || list.size() == 0)
			return "";
		
		Map<String, DocResource> map = new HashMap<String, DocResource>();
		for (DocResource td : list) {
			map.put(td.getId().toString(), td);
		}

		for (int i = 0; i < arr.length; i++) {
			DocResource td = map.get(arr[i]);
			if (td == null)
				continue;
			locList.add(td);
		}

		StringBuffer sb = new StringBuffer("");
		DocResource docResource = null;
		DocLib docLib = null;
		Long domainId = null;
		if (locList.size() > 5) {
			sb.append("...");
			for (int i = (locList.size() - 5); i < locList.size(); i++) {
				docResource = locList.get(i);
				docLib = this.docLibManager.getDocLibById(docResource.getDocLibId());
				domainId = docLib.getDomainId();
				// 如果是共享外单位的文档夹，文档库名称后面需加上外单位名称简称
				String name = docResource.getFrName();
				name = Constants.getDocI18nValue(name);
				if ((i == locList.size() - 5)
						&& domainId != null
						&& domainId.longValue() != 0l
						&& domainId.longValue() != CurrentUser.get().getLoginAccount()) {
					try {
						name = (name + "(" + this.orgManager.getAccountById(domainId).getShortname() + ")");
					} catch (BusinessException e) {
						log.error("", e);
					}
				}
				String name2 = Strings.getLimitLengthString(name, 20, "...");
				name = this.convertToLink(name2, name, docResource.getId(), docResource.getFrType());
				sb.append(" - " + name);
			}
		} else {
			for (int i = 0; i < locList.size(); i++) {
				docResource = locList.get(i);
				docLib = this.docLibManager.getDocLibById(docResource.getDocLibId());
				domainId = docLib.getDomainId();
				// 如果是共享外单位的文档夹，名称后面需加上外单位名称简称
				String name = docResource.getFrName();
				name = Constants.getDocI18nValue(name);
				if (i == 0
						&& domainId != null
						&& domainId.longValue() != 0l
						&& domainId.longValue() != CurrentUser.get().getLoginAccount()) {
					try {
						name = (name + "(" + this.orgManager.getAccountById(domainId).getShortname() + ")");
					} catch (BusinessException e) {
						log.error("", e);
					}
				}
				String name2 = Strings.getLimitLengthString(name, 20, "...");
				name = this.convertToLink(name2, name, docResource.getId(), docResource.getFrType());
				if (i == 0)
					sb.append(name);
				else
					sb.append(" - " + name);
			}
		}

		return sb.toString();
	}

	// 将当前位置的字符串转换为打开链接
	private String convertToLink(String showName, String title, long id, long frType) {
		return "<a class='link-blue' href=\\\"javascript:folderOpenFunWithoutAcl('" + id +
			   "','" + frType + "')\\\" title=\\\"" + Strings.toHTML(title) + "\\\">" + Strings.toHTML(showName) + "</a>";
	}

	public ModelAndView docPropertyIframe(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docPropertiesIframe");
		boolean extandexit = false;
		
		boolean isHistory = Constants.VERSION_FLAG.equals(request.getParameter("versionFlag"));
		String resIdStr = request.getParameter(isHistory ? "docVersionId" : "docResId");
		Long resId = Long.valueOf(resIdStr);
		DocResource dr = this.getDocResource4Show(isHistory, resId);
		
		boolean projectFolder = false;
		if (dr != null) {
			if (contentTypeManager.hasExtendMetadata(dr.getFrType()))
				extandexit = true;
		} else {
			return null;
		}
		mav.addObject("extandexit", extandexit);
		
		long type = dr.getFrType();
		boolean folderLink = false;
		boolean docLink = false;
		if (type == Constants.FOLDER_CASE || type == Constants.FOLDER_CASE_PHASE)
			projectFolder = true;
		else if (type == Constants.LINK_FOLDER)
			folderLink = true;
		else if (type == Constants.LINK)
			docLink = true;
		mav.addObject("projectFolder", projectFolder);
		mav.addObject("folderLink", folderLink);
		mav.addObject("docLink", docLink);

		DocLib lib = docLibManager.getDocLibById(dr.getDocLibId());
		boolean noShare = lib.getType() == Constants.PROJECT_LIB_TYPE.byteValue();
		mav.addObject("noShare", noShare);

		// 判断是否正在查看个人借阅
		boolean ispb = false;
		if (lib.isPersonalLib() && !isHistory)
			ispb = this.docHierarchyManager.isViewPerlBorrowDoc(CurrentUser.get().getId(), resId);
		
		return mav.addObject("isPerBorrow", ispb);
	}

	private DocResource getDocResource4Show(boolean isHistory, Long resId) {
		DocResource dr = null;
		DocVersionInfo dvi = null;
		if(isHistory) {
			dvi = this.docVersionInfoManager.getDocVersion(resId);
			dr = dvi.getDocResourceFromXml();
		}
		else {
			dr = docHierarchyManager.getDocResourceById(resId);
		}
		return dr;
	}

	public ModelAndView docProperty(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docProperties");
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		String isFolder = request.getParameter("isFolder");
		
		boolean isHistory = Constants.VERSION_FLAG.equals(request.getParameter("versionFlag"));
		String resIdStr = request.getParameter(isHistory ? "docVersionId" : "docResId");
		Long docResId = Long.valueOf(resIdStr);
		DocResource dr = this.getDocResource4Show(isHistory, docResId);
		
		Long parentId = dr.getParentFrId();
		Integer maxFrOrder = this.docHierarchyManager.getMaxOrder(parentId);
		mav.addObject("maxFrOrder", maxFrOrder);
		// 权限判断的属性可以修改标记
		String propEditValue = request.getParameter("propEditValue"); // 为真可以修改
		// 是否借阅共享标记
		String isShareAndBorrowRoot = request.getParameter("isShareAndBorrowRoot"); // 为真不能修改
		// 是否从文档库设置页面进入标记
		boolean isLib = StringUtils.isNotBlank(request.getParameter("isLib"));
		boolean isGroupRes = (docLibType.byteValue() == Constants.GROUP_LIB_TYPE.byteValue());

		// 页签显示标记
		String lPublic = request.getParameter("lPublic");
		String lPersonal = request.getParameter("lPersonal");
		String lBorrow = request.getParameter("lBorrow");
		String lExtend = request.getParameter("lExtend");
		
		long theDocLibId = 0L;
		boolean isPersonalLib = false;
		boolean isEdocLib = false;

		// 是否可以修改属性标记
		boolean bool = propEditValue.equals("true") && isShareAndBorrowRoot.equals("false");
		FolderItemFolder folderPropVO = null;
		List<DocPersonalShareVO> myGrantVO = null;
		List<PotentModel> grantVO = null;
		boolean userAllAcl = false;
		List<DocBorrowVO> borrowVO = null;
		DocPropVO docPropVO = null;
		DocLibTableVo libVO = null;
		boolean isDocLink = false;
		long docLibId;
		if (!isLib) {
			if ("true".equals(isFolder)) {
				// 取得文档夹属性
				folderPropVO = this.getFolderPropVO(request);
				docLibId = folderPropVO.getDocResource().getDocLibId();
				// 取得共享数据
				if (lPersonal != null && "true".equals(lPersonal)) {
					myGrantVO = DocMVCUtils.getMyGrantVO(docResId, docAclManager);
				}
				
				if (lPublic != null && "true".equals(lPublic))
					grantVO = this.getGrantVO(request, isGroupRes);
				
				//TODO 性能问题
				String folderAcl = this.docAclManager.getAclString(docResId);
				mav.addObject("folderAcl", folderAcl);
				mav.addObject("editVersion", folderAcl.indexOf("all=true") != -1);
				
				theDocLibId = folderPropVO.getDocResource().getDocLibId();
			} else {
				// 取得借阅数据
				if (lBorrow != null && "true".equals(lBorrow))
					borrowVO = this.getBorrowVO(request);

				// 取得属性
				docPropVO = this.getPropVOByDr(dr);

				docLibId = dr.getDocLibId();
				isDocLink = dr.getFrType() == Constants.LINK || dr.getFrType() == Constants.LINK_FOLDER;

				theDocLibId = dr.getDocLibId();
				mav.addObject("doc_fr_type", dr.getFrType());
			}
		} else {
			// 非个人文档库取得库的根文档夹的共享数据
			if ("true".equals(lPublic))
				grantVO = this.getGrantVO(request, isGroupRes);
			// 取得文档库属性，只读
			libVO = this.getLibVO(request);
			docLibId = libVO.getDoclib().getId();

			theDocLibId = docLibId;
			isPersonalLib = libVO.getDoclib().isPersonalLib();
			isEdocLib = libVO.getDoclib().isEdocLib();
		}

		if(grantVO!=null){
            String ownerIds = "";
			for(PotentModel pm : grantVO){
				//user类别有三类:人员、部门、单位
				if((pm.getUserId()==CurrentUser.get().getId()||pm.getUserId()==CurrentUser.get().getDepartmentId()||pm.getUserId()==CurrentUser.get().getAccountId()) && pm.isAll()){
					userAllAcl = true;
					break;
				}
			}
			for(PotentModel pm : grantVO){
                if(pm.getIsLibOwner()){
                    ownerIds+=pm.getUserId()+",";
                }
			}
            if(ownerIds.length()>1)
                ownerIds.subSequence(0, ownerIds.length()-1);
            mav.addObject("ownerIds", ownerIds);
		}
		String metadataHtml = "";
		if ("true".equals(lExtend)) {
			if(isHistory) {
				metadataHtml = htmlUtil.getHistoryViewHtml(docResId);
			}
			else {
				metadataHtml = htmlUtil.getEditHtml(docResId, (!bool));
			}
		}

		mav.addObject("folderPropVO", folderPropVO);
		mav.addObject("myGrantVO", myGrantVO);
		mav.addObject("grantVO", grantVO);
		mav.addObject("userAllAcl", userAllAcl);
		mav.addObject("borrowVO", borrowVO);
		mav.addObject("docPropVO", docPropVO);
		mav.addObject("bool", bool);
		mav.addObject("libVO", libVO);
		mav.addObject("isLib", isLib);
		mav.addObject("isDocLink", isDocLink);
		mav.addObject("docLibId", docLibId);
		mav.addObject("metadataHtml", metadataHtml);
		mav.addObject("secretLevel", dr.getSecretLevel());//成发集团项目 借阅传入secretLevel
		
		// 集团文档库可以跨单位授权
		mav.addObject("isGroupLib", (docLibType.byteValue() == Constants.GROUP_LIB_TYPE.byteValue()));
		// 单位文档、用户自定义文档（2009-08-24）及公文档案夹（2010-11-18）也开放共享范围，支持跨单位共享
		boolean openShareScope = docLibType.byteValue() == Constants.ACCOUNT_LIB_TYPE.byteValue() || 
								 docLibType.byteValue() == Constants.USER_CUSTOM_LIB_TYPE.byteValue() ||
								 docLibType.byteValue() == Constants.EDOC_LIB_TYPE.byteValue();
		mav.addObject("openShareScope", openShareScope);

		if (!isLib) {
			DocLib lib = docLibManager.getDocLibById(theDocLibId);
			if (lib != null) {
				isPersonalLib = lib.isPersonalLib();
				isEdocLib = lib.isEdocLib();
			}
		}
		
		return mav.addObject("isPersonalLib", isPersonalLib).addObject("isEdocLib", isEdocLib);
	}

	/**
	 * 页签式页面数据的保存 包含 共享（个人，公共）、借阅、属性（常规+扩展）
	 */
	public ModelAndView docLabeldSave(HttpServletRequest request, HttpServletResponse response) throws BusinessException, IOException {
		Byte docLibType = Byte.valueOf(request.getParameter("docLibType"));
		String isFolder = request.getParameter("isFolder");

		String ucfProp = request.getParameter("ucfProp");
		String ucfVersionProp = request.getParameter("ucfVersionProp");
		String ucfPublic = request.getParameter("ucfPublic");
		String ucfPersonal = request.getParameter("ucfPersonal");
		String ucfBorrow = request.getParameter("ucfBorrow");

		Long docResId = Long.valueOf(request.getParameter("docResId"));
		if ("true".equals(isFolder)) {
			// 保存文档夹属性
			if("true".equals(ucfProp)) {
				this.saveFolderProp(request);
			}

			// 保存共享数据
			if (docLibType.equals(Constants.PERSONAL_LIB_TYPE)) {
				if ("true".equals(ucfPersonal))
					this.saveMyGrant(request);
			} 
			else {
				if ("true".equals(ucfPublic))
					this.saveGrant(request);
			}
		} else {
			// 权限操作
			if ("true".equals(ucfBorrow))
				this.saveBorrow(request);

			// 保存属性
			if ("true".equals(ucfProp) || "true".equals(ucfVersionProp)) {
				boolean saveVersion = "true".equals(ucfProp);
				this.saveDocProp(request, response, saveVersion);
			}
			this.updateIndex(docResId);
		}
		Boolean f = (Boolean)(BrowserFlag.OpenWindow.getFlag(CurrentUser.get()));
		if(f){
			super.rendJavaScript(response, "parent.close();");
		} else {
			super.rendJavaScript(response, "parent.parent.winProperties.close();");
		}
		return null;
	}

	/**
	 * 锁定文档进行编辑。
	 */
	public ModelAndView lockDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long docResId = Long.parseLong(request.getParameter("docResId"));
		Long userId = CurrentUser.get().getId();
		
		docHierarchyManager.checkOutDocResource(docResId, userId);
		super.rendJavaScript(response, "parent.window.location.reload(true);");
		return null;
	}

	/**
	 * 释放文档锁。
	 */
	public ModelAndView unlockDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long docResId = Long.parseLong(request.getParameter("docResId"));
		Long userId = CurrentUser.get().getId();

		docHierarchyManager.checkInDocResourceWithoutAcl(docResId, userId);
		super.rendJavaScript(response, "parent.window.location.reload(true);");
		return null;
	}
	
	/**
	 * 获取文档库前端展现所需的VO集合
	 * @param includePersonalLib	是否包含个人文档库
	 */
	private List<DocLibTableVo> getDocLibVOs(boolean includePersonalLib) {
		Long currentUserId = CurrentUser.get().getId();
		Long domainId = CurrentUser.get().getLoginAccount();
		List<DocLib> docLibsShow = this.getPagenatedDocLibs(includePersonalLib, currentUserId, domainId);
		List<Long> libIds = FormBizConfigUtils.getIds(docLibsShow);
		Map<Long, DocResource> rootMap = this.docHierarchyManager.getRootMapByLibIds(libIds);
		
		List<DocLibTableVo> libVos = new ArrayList<DocLibTableVo>();
		boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
		if(CollectionUtils.isNotEmpty(docLibsShow)) {
			for (DocLib docLib : docLibsShow) {
				if(docLib.getType() ==  Constants.EDOC_LIB_TYPE.byteValue()){
					if(!isEdoc || !isPluginEdoc ) continue;
				}
				DocResource drRoot = rootMap.get(docLib.getId());
				if(drRoot == null)
					continue;
				
				DocAclVO root = new DocAclVO(drRoot);
				boolean isPersonalLib = docLib.isPersonalLib();
				root.getDocResource().setIsMyOwn(isPersonalLib);
				root.setIsPersonalLib(isPersonalLib);
				this.setGottenAclsInVO(root, currentUserId, false);
				
				DocLibTableVo vo = new DocLibTableVo(docLib);
				vo.setCreateName(Functions.showMemberName(docLib.getCreateUserId()));
				vo.setDocLibType(Constants.getDocLibType(docLib.getType()));
				vo.setRoot(root);
				vo.setNoShare(docLib.isEdocLib() || docLib.isProjectLib());
				
				List<Long> libOwners = docLibManager.getOwnersByDocLibId(docLib.getId());
				vo.setIsOwner(libOwners != null && libOwners.contains(currentUserId));
				String libOwnerNames = this.getLibOwnerNames(libOwners, docLib.isGroupLib());
				vo.setManagerName(libOwnerNames);
	
				libVos.add(vo);
			}
		}
		return libVos;
	}

	/**
	 * 在前端文档库查看时获取分页文档库结果集
	 * @param includePersonalLib	是否包含个人文档库
	 * @param currentUserId		当前用户ID
	 * @param domainId		当前用户登录单位ID
	 */
	private List<DocLib> getPagenatedDocLibs(boolean includePersonalLib, Long currentUserId, Long domainId) {
		List<DocLib> docLibs = null;
		if(includePersonalLib) {
			docLibs = docLibManager.getDocLibsByUserIdNav(currentUserId, domainId);
		}
		else {
			docLibs = docLibManager.getCommonDocLibsByUserId(currentUserId, domainId);
		}

		boolean showGroupLib = Constants.isShowGroupLib();
		boolean showEdocLib = CurrentUser.get().isInternal() && Constants.edocModuleEnabled();
		if(CollectionUtils.isNotEmpty(docLibs)) {
			for (Iterator<DocLib> iterator = docLibs.iterator(); iterator.hasNext();) {
				DocLib docLib = iterator.next();
				if((!showGroupLib && docLib.isGroupLib())|| (!showEdocLib && docLib.isEdocLib())) {
					iterator.remove();
				}
			}
		}

		return FormBizConfigUtils.pagenate(docLibs);
	}

	/**
	 * 从菜单"知识管理" -> "文档库管理"进入文档库管理列表页面（不包括用户个人文档库）
	 */
	public ModelAndView docLibsConfig(HttpServletRequest request, HttpServletResponse response) {
		List<DocLibTableVo> docTableVo = this.getDocLibVOs(false);
		return new ModelAndView("doc/libRightWorkspace", "docTableVo", docTableVo);
	}

	private String getLibOwnerNames(List<Long> libOwners, boolean needAccountShort) {
		StringBuilder ownerNames = new StringBuilder();
		if(CollectionUtils.isNotEmpty(libOwners)) {
			int j = 0;
			for (Long memberId : libOwners) {
				String memberName = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId, needAccountShort);
				if (j != 0) {
					ownerNames.append(Constants.getCommonI18nValue("common.separator.label") + memberName);
				} else {
					ownerNames.append(memberName);
				}
				j++;
			}
		}
		return ownerNames.toString();
	}

	/**
	 * 取得文档库的属性
	 */
	public DocLibTableVo getLibVO(HttpServletRequest request) {
		String sLibId = request.getParameter("docLibId");
		long currentUserId = CurrentUser.get().getId();

		if (sLibId == null || sLibId.equals(""))
			return null;
		Long libId = Long.valueOf(sLibId);
		DocLib lib = docLibManager.getDocLibById(libId);
		DocLibTableVo vo = new DocLibTableVo(lib);
		long userId = lib.getCreateUserId();
		V3xOrgMember member = null;
		try {
			member = orgManager.getMemberById(userId);
		} catch (BusinessException e) {
			log.error("orgManager取得member", e);
		}
		if (member != null)
			vo.setCreateName(member.getName()); // 获取创建者
		else
			vo.setCreateName("");
		List<Long> the_manager = docLibManager.getOwnersByDocLibId(lib.getId());
		boolean isFromOtherAccount = (lib.getDomainId() != CurrentUser.get().getLoginAccount());
		String otherAccountShortName = "";
		if (isFromOtherAccount && member != null) {
			try {
				otherAccountShortName = "(" + this.orgManager.getAccountById(lib.getDomainId()).getShortname() + ")";
			} catch (BusinessException e) {
				log.error("获取当前文档库所在单位出现异常", e);
			}
		}
		StringBuilder manager_list = new StringBuilder();
		if (the_manager != null) {
			int j = 0;
			for (Long oid : the_manager) {
				V3xOrgMember _member = null;
				try {
					_member = orgManager.getMemberById(oid);
				} catch (BusinessException e) {
					log.error("orgManager取得member", e);
				}
				if (_member == null || !_member.isValid())
					continue;
				if (j != 0) {
					manager_list.append(",");
				}
				if (isFromOtherAccount) {
					manager_list.append(_member.getName() + otherAccountShortName);
				}
				else {
					manager_list.append(_member.getName());
				}

				j++;
				// 获取管理员
				// 设置管理员标记
				if (currentUserId == _member.getId().longValue())
					vo.setIsOwner(true);
			}
		}
		vo.setManagerName(manager_list.toString());
		// 获取库类型
		vo.setDocLibType(Constants.getDocLibType(lib.getType()));
		// 设置库的根
		DocAclVO root = new DocAclVO(docHierarchyManager.getRootByLibId(libId));
		root.setIsPersonalLib(lib.isPersonalLib());
		vo.setIcon(this.getIcon(true, root.getDocResource().getMimeTypeId()));
		this.setGottenAclsInVO(root, currentUserId, false);
		vo.setRoot(root);
		return vo;
	}

	/**
	 * 正则表达式转换文件名
	 * @param name
	 */
	private String checkAndRep(String name) {
		String rex = "[:\\/<>*?|]";
		Pattern p = Pattern.compile(rex);
		Matcher m = p.matcher(name);
		StringBuffer sbr = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sbr, "_");
		}
		m.appendTail(sbr);
		return sbr.toString();
	}

	/**
	 * 文档日志导出excel
	 */
	public ModelAndView fileLogToExcel(HttpServletRequest request, HttpServletResponse response) {
		String docResId = request.getParameter("docResourceId"); // 文档ID
		long drId = Long.valueOf(docResId);
		DocResource dr = docHierarchyManager.getDocResourceById(drId);
		String flag = request.getParameter("flag");
		String isGroupLib = request.getParameter("isGroupLib");
		String name = dr.getFrName();

		List<OperationLog> list = null;
		if (flag.equals("folderLog")) {
			list = operationlogManager.queryBySubObjectIdOrObjectId(drId, drId, false); // 查询出该文档在当前库中记录的所有日志
			name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
		} else {
			list = operationlogManager.queryByObjectId(drId, false);
		}
		DataRecord dataRecord = new DataRecord(); 
		name = this.checkAndRep(name);
		if("true".equals(isGroupLib)){
			String[] columnName = {Constants.getDocI18nValue("log.user"), Constants.getDocI18nValue("doc.jsp.log.account.label"),
					Constants.getDocI18nValue("log.operation"),Constants.getDocI18nValue("log.time"), 
					Constants.getDocI18nValue("log.description"), Constants.getDocI18nValue("log.remoteip")};
			dataRecord.setColumnName(columnName);
			dataRecord.setColumnWith(new short[] { 20, 30, 30, 30, 60, 30 });
		}else{
			String[] columnName = {Constants.getDocI18nValue("log.user"), Constants.getDocI18nValue("log.operation"),
					   Constants.getDocI18nValue("log.time"), Constants.getDocI18nValue("log.description"), 
					   Constants.getDocI18nValue("log.remoteip")};
			dataRecord.setColumnName(columnName);
			dataRecord.setColumnWith(new short[] { 20, 30, 30, 60, 30 });
		}
		dataRecord.setTitle(name + Constants.getDocI18nValue("log.title"));
		dataRecord.setSheetName("sheet1");

		for (int i = 0; i < list.size(); i++) {
			DataRow row = new DataRow();
			OperationLog log_ = list.get(i);
			String user = "";
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(log_.getMemberId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				user = member.getName(); // 操作人
			String actionName = ResourceBundleUtil.getString(
					Constants.RESOURCE_BASENAME, log_.getActionType()); // 得到对应KEY的值
			row.addDataCell(user, DataCell.DATA_TYPE_TEXT);
			if("true".equals(isGroupLib)){
				V3xOrgAccount account = null;
				try {
					account = orgManager.getAccountById(member.getOrgAccountId());
				} catch (BusinessException e) {
					log.error("从orgManager取得用户", e);
				}
				row.addDataCell(account.getShortname(), DataCell.DATA_TYPE_TEXT);
			}
			row.addDataCell(actionName, DataCell.DATA_TYPE_TEXT);
			row.addDataCell(log_.getActionTime().toString().substring(0,16),
					DataCell.DATA_TYPE_DATETIME);
			String desc = ResourceBundleUtil.getStringOfParameterXML(
					Constants.RESOURCE_BASENAME, log_.getContentLabel(), log_
							.getContentParameters());
			row.addDataCell(desc, DataCell.DATA_TYPE_TEXT);
			row.addDataCell(log_.getRemoteIp(), DataCell.DATA_TYPE_TEXT);
			try {
				dataRecord.addDataRow(row);
			} catch (Exception e) {
				log.error("日志的excel导出", e);
			}
		}
		try {
			if (dataRecord.getTitle().length() > 60)
				fileToExcelManager.save(request, response, dataRecord.getTitle().substring(0, 60), dataRecord);
			else
				fileToExcelManager.save(request, response, dataRecord.getTitle(), dataRecord);

		} catch (Exception e) {
			log.error("日志的excel导出", e);
		}
		return null;
	}

	/** 关联项目归档树框架  */
	public ModelAndView docTreeProjectIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docTreeProjectIframe");
	}

	/**
	 * 关联项目归档树
	 */
	public ModelAndView listProjectRoots(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docTreeProject");
		Long userId = CurrentUser.get().getId();
		Long projectId = Long.valueOf(request.getParameter("projectId"));
		
		String hql = "from DocResource as d where sourceId = ? and frType = ?";
		List<DocResource> projects = docHierarchyManager.findDocResourceByHql(hql, projectId, Constants.FOLDER_CASE);
		
		DocResource project = null;
		if (projects == null || projects.size() == 0) {
			return ret;
		} else {
			project = projects.get(0);
		}

		DocTreeVO projectvo = DocMVCUtils.getDocTreeVO(userId, project, Constants.PROJECT_LIB_TYPE, 
				docMimeTypeManager, docAclManager);
		
		String hql2 = "from DocResource as d where parentFrId = 0 and docLibId = ? ";
		List<DocResource> roots = docHierarchyManager.findDocResourceByHql( hql2, project.getDocLibId());
		DocResource root = null;
		if (roots == null || roots.size() == 0) {
			return ret;
		} else {
			root = roots.get(0);
		}

		DocTreeVO rootvo = DocMVCUtils.getDocTreeVO(userId, root, Constants.PROJECT_LIB_TYPE, 
				docMimeTypeManager, docAclManager);

		return ret.addObject("root", rootvo).addObject("project", projectvo);
	}

	// 弹出树
	public ModelAndView xmlJspProject(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		Long userId = CurrentUser.get().getId();

		String sparentId = request.getParameter("resId");
		Long parentId = Long.valueOf(sparentId);
		String stype = request.getParameter("frType");
		Long frType = Long.valueOf(stype);
		DocResource parent = docHierarchyManager.getDocResourceById(parentId);

		// 对于资源是否存在的判断
		if (parent == null) {
			out.println("<exist>no</exist>");
			return null;
		}

		DocLib lib = docLibManager.getDocLibById(parent.getDocLibId());
		boolean isPersonalLib = lib.isPersonalLib();
		List<DocResource> drs = null;
		if (isPersonalLib) {
			drs = docHierarchyManager.findFolders(parentId, frType, userId, "", true);
		} else {
			String orgIds = Constants.getOrgIdsOfUser(userId);
			drs = docHierarchyManager.findFolders(parentId, frType, userId, orgIds, false);
		}

		if ((drs == null) || (drs.size() == 0))
			return null;
		List<DocTreeVO> folders = new ArrayList<DocTreeVO>();
		for (DocResource dr : drs) {
			DocTreeVO vo = this.getDocTreeVO(userId, dr, isPersonalLib);
			folders.add(vo);
		}

		out.println("<tree text=\"loaded\">");
		String xmlstr = DocMVCUtils.getXmlStr4LoadNodeOfProjectTree(folders);
		out.println(xmlstr);
		out.println("</tree>");

		return null;
	}

	// 发送到常用文档
	public ModelAndView sendToFavorites(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String successKey = "doc_myfavorite_success_alert";
		String failureKey = "doc_myfavorite_failure_alert";
		try {
			String sdocid = request.getParameter("docId");
			String userIds = request.getParameter("userIds");
			String userType = request.getParameter("userType");
			String orgType = V3xOrgEntity.ORGENT_TYPE_MEMBER;
			List<Long> orgIds = new ArrayList<Long>();

			if ("member".equals(userType)) {
				orgIds.add(CurrentUser.get().getId());
			} else if ("dept".equals(userType)) {
				successKey = "doc_favorite_dept_success_alert";
				failureKey = "doc_favorite_dept_failure_alert";

				orgType = V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
				if (userIds == null || userIds.equals("")) {
					// 预防跨部门担任管理员
					Set<Long> depts = DocMVCUtils.getDepSetAdmin(this.spaceManager);

					if (depts != null && depts.size() > 0) {
						orgIds.add(depts.iterator().next());
					} else
						orgIds.add(CurrentUser.get().getDepartmentId());
				} else {
					StringTokenizer stk = new StringTokenizer(userIds, ",");
					while (stk.hasMoreTokens()) {
						orgIds.add(Long.valueOf(stk.nextToken()));
					}
				}
			} else if ("account".equals(userType)) {
				successKey = "doc_favorite_account_success_alert";
				failureKey = "doc_favorite_account_failure_alert";
				orgType = V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
				orgIds.add(CurrentUser.get().getLoginAccount());
			} else if ("group".equals(userType)) {
				successKey = "doc_favorite_group_success_alert";
				failureKey = "doc_favorite_group_failure_alert";
				orgType = Constants.ORGENT_TYPE_GROUP;
				orgIds.add(0L);
			}

			if (sdocid == null || "".equals(sdocid)) {
				// 上面菜单
				String[] ids = request.getParameterValues("id");
				List<Long> docIds = new ArrayList<Long>();
				if(ids != null && ids.length > 0) {
					for (String s : ids) {
						// 存在性验证
						Long drsId = NumberUtils.toLong(s);
						if (docHierarchyManager.docResourceExist(drsId))
							docIds.add(drsId);
					}
				}

				docFavoriteManager.setFavoriteDoc(docIds, orgIds, orgType);

			} else {
				// 右键菜单过来，单个文档id
				long docId = Long.valueOf(sdocid);
				// 存在性验证
				if (docHierarchyManager.docResourceExist(docId))
					docFavoriteManager.setFavoriteDoc(docId, orgIds, orgType);
			}

			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang." + successKey + "'));");
		} catch (Exception e) {
			log.error("发送到常用文档", e);
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang." + failureKey + "'));");
		}
		return null;
	}

	// 进入知识管理主页
	public ModelAndView homepageView(HttpServletRequest request, HttpServletResponse response) {
		// 根据系统配置决定采用哪一种主页展现
		ModelAndView mav = null;
		boolean homeAll = (Constants.rssModuleEnabled());
		if (homeAll)
			mav = new ModelAndView("doc/homepage");
		else
			mav = new ModelAndView("doc/homepage_4");
		User user = CurrentUser.get();
		
		List<ArticleModel> latestArticleModelList = new ArrayList<ArticleModel>();
		List<AttentionModel> attentionModelList = new ArrayList<AttentionModel>();

		Long userId = user.getId();
		String orgIds = Constants.getOrgIdsOfUser(userId);

		/** ******************* 文档订阅********************************* */
		List<DocAlertLatest> dals = docAlertLatestManager.findAlertLatestsByUserByCount(userId,
						Constants.DOC_ALERT_STATUS_ALL, Constants.DOC_HOMEPAGE_ALERT_COUNT);
		List<DocAlertLatestVO> dalvos = this.getAlertLatestVos(dals, userId, orgIds);
		/** ******************* 常用文档 ********************************* */
		List<DocFavorite> dfs = docFavoriteManager.findFavoritePersonalDocsByOrgByCount(userId,
						Constants.DOC_HOMEPAGE_BLOG_COUNT * 2);
		List<DocFavoriteVO> dfvos = this.getDocFavoriteVos(dfs, userId, orgIds);
		/** ******************* Rss订阅 ********************************* */
		List<RssChannelItems> items = new ArrayList<RssChannelItems>();
		if (homeAll)
			items = rssManager.getMostNewItems(Constants.DOC_HOMEPAGE_SESSION_COUNT,
					V3xOrgEntity.ORGENT_TYPE_MEMBER, userId);
		List<ChannelInfoVo> the_list = new ArrayList<ChannelInfoVo>();
		for (int i = 0; i < items.size(); i++) {
			ChannelInfoVo channel = new ChannelInfoVo(items.get(i));
			the_list.add(channel);
		}
		/** ********************学习区************************************ */
		List<DocLearning> dls = docLearningManager.getDocLearningsByCount(
				V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, Constants.DOC_HOMEPAGE_ALERT_COUNT);
		List<DocLearningVO> dlvos = this.getDocLearningVOs(dls);

		/** ********************最新的博客文章*********************************************** */
		try {
			latestArticleModelList = blogArticleManager.getBlogArticleByCount(
					user.getId(), Constants.DOC_HOMEPAGE_ALERT_COUNT);
		} catch (Exception e) {
			log.error("知识管理主页获取最新博客文章", e);
		}

		/** ********************调我关注的博客*********************************************** */
		try {
			if (homeAll)
				attentionModelList = blogManager.getBlogAttentionByCount(
						userId, Constants.DOC_HOMEPAGE_ALERT_COUNT);
		} catch (Exception e) {
			log.error("知识管理主页获取我关注的博客", e);
		}

		mav.addObject("rssItems", the_list);
		mav.addObject("dalvos", dalvos);
		mav.addObject("dalvosEmptySize", Constants.DOC_HOMEPAGE_ALERT_COUNT - dalvos.size());
		mav.addObject("dfvos", dfvos);

		// 2007.09.24 常用文档改为一行两个
		int dfempty = Constants.DOC_HOMEPAGE_BLOG_COUNT - (dfvos.size() / 2);
		if (dfvos.size() % 2 != 0)
			dfempty--;

		mav.addObject("dfvosEmptySize", dfempty);
		mav.addObject("dfvosSize", dfvos.size());
		mav.addObject("dlvos", dlvos);
		mav.addObject("dlvosEmptySize", Constants.DOC_HOMEPAGE_ALERT_COUNT - dlvos.size());
		mav.addObject("articleModellist", latestArticleModelList);
		mav.addObject("articleEmptySize", Constants.DOC_HOMEPAGE_BLOG_COUNT - latestArticleModelList.size());
		mav.addObject("attentionModelList", attentionModelList);
		mav.addObject("attentionEmptySize", Constants.DOC_HOMEPAGE_BLOG_COUNT - attentionModelList.size());

		return mav;
	}

	/** 更多常用文档   */
	@SuppressWarnings("unchecked")
	public ModelAndView docFavoriteMore(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docFavoriteMore");

		Long userId = CurrentUser.get().getId();
		String orgIds = Constants.getOrgIdsOfUser(userId);

		String userType = request.getParameter("userType");
		String orgType = V3xOrgEntity.ORGENT_TYPE_MEMBER;
		String titlePostfix = "personal";
		long orgId = userId;
		if ("dept".equals(userType)) {
			orgType = V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
			orgId = CurrentUser.get().getDepartmentId();
			String sdepId = request.getParameter("deptId");
			if(sdepId!=null && !"".equals(sdepId))
				orgId = Long.valueOf(sdepId);

			titlePostfix = "dept";
		} else if ("account".equals(userType)) {
			orgType = V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
			orgId = CurrentUser.get().getLoginAccount();
			titlePostfix = "account";
		} else if ("group".equals(userType)) {
			orgType = Constants.ORGENT_TYPE_GROUP;
			orgId = CurrentUser.get().getAccountId();
			//GOV-4188.公共空间里点击组织学习区和组织知识文档 start
			if((Boolean)SysFlag.is_gov_only.getFlag()) {
				titlePostfix = "gov";
			} else {
				titlePostfix = "group";
			}
			//GOV-4188.公共空间里点击组织学习区和组织知识文档 end
		}  
 
		String condition = request.getParameter("condition");
		List alist = null; 
		if(condition!=null && !"".equals(condition)){
			String value = ""; 
			if("createDate".equals(condition)){
				value = request.getParameter("textfield")+" # "+request.getParameter("textfield1");
			}else{
				value = request.getParameter("textfield"); 
			}
			alist = docFavoriteManager.getFavoritesByPage(orgType, orgId,condition,value);
		}else{
			alist = docFavoriteManager.getFavoritesByPage(orgType, orgId);
		} 

		List<DocFavorite> dfs = (List<DocFavorite>) (alist.get(1));
		List<DocFavoriteVO> dfvos = this.getDocFavoriteVos(dfs, userId, orgIds);

		boolean canAdmin = true;
		if (!orgType.equals(V3xOrgEntity.ORGENT_TYPE_MEMBER))
			canAdmin = this.canAdminSpace(orgType, orgId);

		mav.addObject("canAdmin", canAdmin);
		mav.addObject("dfvos", dfvos);
		mav.addObject("siteType", orgType);
		mav.addObject("siteId", orgId);
		mav.addObject("userType", userType);
		mav.addObject("total", (Integer) (alist.get(0)));
		mav.addObject("title", "doc.jsp.home.more.favorite.title." + titlePostfix);
		mav.addObject("types", contentTypeManager.getAllSearchContentType()); 
		return mav;
	}

	// 判断是否具有空间管理权限
	private boolean canAdminSpace(String spaceType, Long spaceId) {
		if (spaceType == null)
			return false;

		if (V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(spaceType)) {
			Set<Long> depts = DocMVCUtils.getDepSetAdmin(this.spaceManager);
			if (depts.contains(spaceId))
				return true;
		} else if (V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(spaceType)) {
			return Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_ACCOUNTADMIN, CurrentUser.get());
		} else if (Constants.ORGENT_TYPE_GROUP.equals(spaceType)) {
			return this.canAdminGroup();
		}

		return false;
	}

	/** 更多最新订阅  */
	public ModelAndView docAlertLatestMore(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docAlertLatestMore");
		byte status = Constants.DOC_ALERT_STATUS_ALL;
		String statu = request.getParameter("status");

		if (statu != null) {
			status = Byte.valueOf(statu);
			mav = new ModelAndView("doc/docAlertLatestMoreMine");
		}

		String flag = request.getParameter("flag");
		if ("doc".equals(flag)) {
			mav.addObject("flag", flag);
		} else {
			mav.addObject("flag", "front");
		}

		User user = CurrentUser.get();
		Long userId = user.getId();
		String orgIds = Constants.getOrgIdsOfUser(userId);
		long alertUserId = userId;
		List<DocAlertLatest> dals = new ArrayList<DocAlertLatest>();
		try {
			String condition = request.getParameter("condition");
			if(condition!=null && !"".equals(condition)){
				String value = ""; 
				if("createDate".equals(condition)){
					value = request.getParameter("textfield")+" # "+request.getParameter("textfield1");
				}else{
					value = request.getParameter("textfield"); 
				}
				dals = docAlertLatestManager.findAlertLatestsByUserPaged(alertUserId, status,condition,value);
			}else{
				dals = docAlertLatestManager.findAlertLatestsByUserPaged(alertUserId, status);
			} 
		} catch (DocException e) {
			log.error("", e);
		}
		List<DocAlertLatestVO> dalvos = this.getAlertLatestVos(dals, userId, orgIds);
		mav.addObject("dalvos", dalvos);
		mav.addObject("status", statu);
		mav.addObject("types", contentTypeManager.getAllSearchContentType()); 
		return mav;
	}

	/** 取消常用文档  */
	public ModelAndView docAlertLatestDel(HttpServletRequest request, HttpServletResponse response) {
		String ids = request.getParameter("ids");
		docAlertLatestManager.deleteLatestByIds(ids);
		String statu = request.getParameter("status");
		String flag = request.getParameter("flag");
		return redirectModelAndView("/doc.do?method=docAlertLatestMore&status=" + statu + "&flag=" + flag, "parent");
	}

	/** 取消常用文档  */
	public ModelAndView docFavoriteCancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ids = request.getParameter("ids");
		docFavoriteManager.deleteFavoriteDocByIds(ids);
		super.rendJavaScript(response, "parent.location.reload(true);");
		return null;
	}

	// 修改常用文档显示顺序
	public ModelAndView docFavoriteResort(HttpServletRequest request,
			HttpServletResponse response) {
		long srcId = Long.valueOf(request.getParameter("id"));
		long destId = Long.valueOf(request.getParameter("destId"));
		String flag = request.getParameter("flag");
		if (flag.equals("up")) {
			docFavoriteManager.updateDocFavoriteOrderUp(srcId, destId);
		} else if (flag.equals("down")) {
			docFavoriteManager.updateDocFavoriteOrderDown(srcId, destId);
		}

		return null;
	}

	/** 封装 DocFavoriteVO */
	private List<DocFavoriteVO> getDocFavoriteVos(List<DocFavorite> dfs,
			Long userId, String orgIds) {
		if (dfs == null || dfs.size() == 0)
			return new ArrayList<DocFavoriteVO>();
		List<DocFavoriteVO> ret = new ArrayList<DocFavoriteVO>();
		for (DocFavorite df : dfs) {
			DocResource dr = df.getDocResource();
			DocFavoriteVO vo = new DocFavoriteVO(df);
			this.setGottenAclsInVO(vo, CurrentUser.get().getId(), false);
			if (dr.getFrType() == Constants.LINK)
				vo.setIsLink(true);
			else if (dr.getFrType() == Constants.LINK_FOLDER)
				vo.setIsFolderLink(true);
			String creater = "";
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(dr.getCreateUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				creater = member.getName();
			vo.setCreateUserName(creater);

			DocLib lib = docLibManager.getDocLibById(dr.getDocLibId());
			vo.setDocLibType(lib.getType());
			vo.setIcon(this.getIcon(dr.getIsFolder(), dr.getMimeTypeId()));
			vo.setType(contentTypeManager.getContentTypeById(dr.getFrType()).getName());

			ret.add(vo);
		}
		return ret;
	}

	// 封装 DocAlertLatestVO
	private List<DocAlertLatestVO> getAlertLatestVos(List<DocAlertLatest> dals, Long userId, String orgIds) {
		if (dals == null)
			return new ArrayList<DocAlertLatestVO>();
		List<DocAlertLatestVO> ret = new ArrayList<DocAlertLatestVO>();
		for (DocAlertLatest dal : dals) {
			DocResource dr = docHierarchyManager.getDocResourceById(dal.getDocResourceId());
			if (dr == null) {
				log.info("DocAlertLatest 中:" + dal.getId() +" 为错误数据请清理!");
				continue;
			}
			DocAlertLatestVO vo = new DocAlertLatestVO(dal, dr);
			String laster = "";
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(dal.getLastUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				laster = member.getName();
			vo.setLastUserName(laster);
			this.setGottenAclsInVO(vo, CurrentUser.get().getId(), false);
			if (dr.getFrType() == Constants.LINK
					|| dr.getFrType() == Constants.LINK_FOLDER)
				vo.setIsLink(true);

			DocLib lib = docLibManager.getDocLibById(dr.getDocLibId());
			vo.setDocLibType(lib.getType());
			vo.setIcon(this.getIcon(dr.getIsFolder(), dr.getMimeTypeId()));

			DocType docType = contentTypeManager.getContentTypeById(dr.getFrType());
			vo.setType(docType.getName());

			String oprType = ResourceBundleUtil.getString(
					Constants.RESOURCE_BASENAME, Constants.getAlertTypeKey(dal.getChangeType()));
			vo.setOprType(oprType);

			ret.add(vo);
		}
		return ret;
	}

	/**
	 * 文档夹修改属性的影响范围确定
	 */
	public ModelAndView folderPropEditScopeView(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/folderPropEditScope");
	}

	/**
	 * 转发协同
	 */
	public ModelAndView sendToColl(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		if(dr == null) {
			super.rendJavaScript(response, "alert('" + Constants.getDocI18nValue("doc.src.deleted")+ "');parent.window.history.back();");
			return null;
		}
		
		// (个人文档库以外的情况)判断是否有转发协同的权限
		Long docLibId = NumberUtils.toLong(request.getParameter("docLibId"), dr.getDocLibId());
		DocLib lib = this.docLibManager.getDocLibById(docLibId);
		if(lib != null) {
			boolean canSendToColl = false;
			if(!lib.isPersonalLib()) {
				String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
				Set<Integer> sets = this.docAclManager.getDocResourceAclList(dr, orgIds);
				canSendToColl = sets.contains(Constants.ALLPOTENT) 
					|| sets.contains(Constants.EDITPOTENT) || sets.contains(Constants.READONLYPOTENT)
					|| (sets.contains(Constants.ADDPOTENT) && dr.getCreateUserId().longValue() == CurrentUser.get().getId());
			}
			else {
				canSendToColl = dr.getCreateUserId() == CurrentUser.get().getId();
				if(!canSendToColl) {
					String acl = this.docAclManager.getBorrowPotent(docResId);
					canSendToColl = '1' == acl.charAt(0);
				}
			}
			
			if(!canSendToColl) {
				super.rendJavaScript(response, "alert('" + Constants.getDocI18nValue("doc.noauth.sendtocol") + "');parent.window.history.back();");
				return null;
			}
		}

		long formatType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getFormatType();
		String subject = dr.getFrName();

		String bodyType = com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML;
		if (formatType == Constants.FORMAT_TYPE_DOC_WORD)
			bodyType = com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD;
		else if (formatType == Constants.FORMAT_TYPE_DOC_EXCEL)
			bodyType = com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL;
		else if (formatType == Constants.FORMAT_TYPE_DOC_WORD_WPS)
			bodyType = com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD;
		else if (formatType == Constants.FORMAT_TYPE_DOC_EXCEL_WPS)
			bodyType = com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL;

		Date bodyCreateDate = new Date();
		String bodyContent = "";
		List<Attachment> atts = new ArrayList<Attachment>();
		if (formatType != Constants.FORMAT_TYPE_DOC_FILE) {
			atts = attachmentManager.getByReference(docResId);

			DocBody body = docHierarchyManager.getBody(docResId);
			bodyCreateDate = body.getCreateDate();
			bodyContent = body.getContent();
		} else {
			V3XFile file = null;
			try {
				file = fileManager.getV3XFile(dr.getSourceId());
			} catch (BusinessException e) {
				log.error("取得V3xFile", e);
			}
			Attachment att = new Attachment(file);
			att.setType(com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal());
			att.setFilename(subject);
			atts.add(att);
			List<Attachment> atts2 = attachmentManager.getByReference(docResId);
			if (atts2 != null)
				atts.addAll(atts2);
		}

		this.docHierarchyManager.logForward("false", docResId);

		return collaborationController.appToColl(subject, bodyType, bodyCreateDate, bodyContent, atts, true);
	}

	/**
	 * 转发邮件
	 */
	public ModelAndView sendToWebMail(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = CurrentUser.get();
		try {
			MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(String.valueOf(user.getId()));
			if (mbc == null) {
				ModelAndView mav = new ModelAndView("webmail/error");
				mav.addObject("errorMsg", "2");
				mav.addObject("url", "?method=list&jsp=set");
				return mav;
			}
		} catch (Exception e1) {
			log.error("调用邮件接口判断当前用户是否有邮箱设置：", e1);
		}

		Long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		if(dr == null) {
			super.rendJavaScript(response, "alert('" + Constants.getDocI18nValue("doc.src.deleted")+ "');parent.window.history.back();");
			return null;
		}

		long formatType = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getFormatType();
		String subject = dr.getFrName();
		String bodyContent = "";
		List<Attachment> atts = new ArrayList<Attachment>();
		if (formatType != Constants.FORMAT_TYPE_DOC_FILE) {
			atts = attachmentManager.getByReference(docResId);

			DocBody body = docHierarchyManager.getBody(docResId);
			bodyContent = body.getContent();

			if (formatType == Constants.FORMAT_TYPE_DOC_EXCEL
					|| formatType == Constants.FORMAT_TYPE_DOC_EXCEL_WPS
					|| formatType == Constants.FORMAT_TYPE_DOC_WORD
					|| formatType == Constants.FORMAT_TYPE_DOC_WORD_WPS) {
				String bodyType = body.getBodyType();
				Long fileId = Long.valueOf(bodyContent);
				bodyContent = "";

				String bodyFileName = dr.getFrName();
				try {
					InputStream in = fileManager.getStandardOfficeInputStream(fileId, dr.getCreateTime());
					if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(bodyType)) {
						bodyFileName += ".doc";
					} else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(bodyType)) {
						bodyFileName += ".xls";
					} else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(bodyType)) {
						bodyFileName += ".wps";
					} else if (com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(bodyType)) {
						bodyFileName += ".et";
					}

					V3XFile file3x = fileManager.save(in, ApplicationCategoryEnum.mail, bodyFileName, dr.getCreateTime(), false);
					Attachment att = new Attachment(file3x, ApplicationCategoryEnum.mail,
							com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE);
					if (atts == null)
						atts = new ArrayList<Attachment>();
					atts.add(att);
				} catch (Exception e) {
					log.error("转发邮件中，在线编辑的office文档转换为附件 ", e);
				}
			}
		} else {
			V3XFile file = null;
			try {
				file = fileManager.getV3XFile(dr.getSourceId());
				if (file.getType() == null) {
					file.setType(com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE.ordinal());
				}
				File realFile = fileManager.getFile(file.getId());
				if(realFile == null || !realFile.exists()) {
					super.rendJavaScript(response, "alert('" + Constants.getDocI18nValue("doc.src.deleted")+ "');parent.window.history.back();");
					return null;
				}
			} catch (BusinessException e) {
				log.error("取得V3xFile", e);
			}
			Attachment att = new Attachment(file);
			att.setFilename(dr.getFrName());
			atts.add(att);
		}
		this.docHierarchyManager.logForward("true", docResId);
		return webMailManager.forwordMail(docResId, subject, bodyContent, atts);
	}

	/**
	 * 进入签出文档管理界面iframe
	 */
	public ModelAndView docCheckoutIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docCheckoutIframe");
	}

	/**
	 * 进入签出文档管理界面
	 */
	public ModelAndView docCheckoutView(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docCheckoutAdmin");
		long docLibId = Long.valueOf(request.getParameter("docLibId"));
		List<DocResource> drs = docHierarchyManager.findAllCheckoutDocsByDocLibIdByPage(docLibId);
		List<DocCheckOutVO> vos = this.getCheckoutVos(drs);
		ret.addObject("covos", vos);
		return ret;
	}

	// 封装 DocCheckOutVo
	private List<DocCheckOutVO> getCheckoutVos(List<DocResource> drs) {
		List<DocCheckOutVO> vos = new ArrayList<DocCheckOutVO>();
		if (drs == null || drs.size() == 0)
			return vos;
		for (DocResource d : drs) {
			DocCheckOutVO vo = new DocCheckOutVO(d);
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(d.getCheckOutUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				vo.setCheckOutUserName(member.getName());
			DocType type = contentTypeManager.getContentTypeById(d.getFrType());
			if (type != null)
				vo.setType(type.getName());
			vo.setPath(this.getPhysicalPath(d.getLogicalPath()));
			vo.setIcon(this.getIcon(false, d.getMimeTypeId()));
			vos.add(vo);
		}

		return vos;
	}

	/**
	 * 签入文档
	 */
	public ModelAndView docCheckin(HttpServletRequest request,
			HttpServletResponse response) {
		String[] ids = request.getParameterValues("id");
		long userId = CurrentUser.get().getId();
		for (String id : ids) {
			docHierarchyManager.checkInDocResourceWithoutAcl(Long.valueOf(id), userId);
		}
		return this.docCheckoutView(request, response);
	}

	/** ******************* 文档订阅开始 ***************************** */
	
	/** 文档订阅管理框架 */
	public ModelAndView docAlertAdminIndex(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docAlertAdmin/index");
	}

	/** 文档订阅管理列表 */
	public ModelAndView docAlertAdminList(HttpServletRequest request, HttpServletResponse response) {
		List<DocAlertAdminVO> davos = this.getDocAlertAdminVO();
		return new ModelAndView("doc/docAlertAdmin/list", "davos", davos);
	}

	/** 获取文档订阅管理的列表元素VO */
	private List<DocAlertAdminVO> getDocAlertAdminVO() {
		List<List<DocAlert>> das = docAlertManager.findAllAlertsOfCurrentUserByPage();
		List<DocAlertAdminVO> davos = new ArrayList<DocAlertAdminVO>();
		for (List<DocAlert> dalist : das) {
			DocResource dr = docHierarchyManager.getDocResourceById(dalist.get(0).getDocResourceId());
			if (dr == null)
				continue;
			davos.add(this.getDocAlertAdminVO(dalist, dr, "list"));
		}
		
		Collections.sort(davos);
		return davos;
	}

	/** 文档订阅管理查看 */
	public ModelAndView docAlertAdminView(HttpServletRequest request, HttpServletResponse response) {
		String alertIds = request.getParameter("alertIds");
		List<DocAlert> das = docAlertManager.findAlertsByIds(alertIds);
		DocResource dr = docHierarchyManager.getDocResourceById(das.get(0).getDocResourceId());
		DocAlertAdminVO vo = this.getDocAlertAdminVO(das, dr, "view");
		return new ModelAndView("doc/docAlertAdmin/view", "vo", vo);
	}

	/** 进入文档订阅管理的修改页面  */
	public ModelAndView docAlertAdminEdit(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docAlertAdmin/edit");
		String alertIds = request.getParameter("alertIds");
		List<DocAlert> das = docAlertManager.findAlertsByIds(alertIds);
		DocResource dr = docHierarchyManager.getDocResourceById(das.get(0).getDocResourceId());
		DocAlertAdminVO vo = this.getDocAlertAdminVO(das, dr, "view");
		mav.addObject("isFolder", dr.getIsFolder());
		mav.addObject("docResId", dr.getId());
		mav.addObject("editFlag", true);
		mav.addObject("comment", dr.getCommentEnabled());
		mav.addObject("vo", vo);
		String name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, dr.getFrName());
		return mav.addObject("name", name);
	}

	/** 文档个人订阅修改页面的保存  */
	public ModelAndView docAlertEdit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = CurrentUser.get().getId();
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		boolean isFolder = Boolean.parseBoolean(request.getParameter("isFolder"));
		String alertIds = request.getParameter("alertIds");
		boolean sendMessage = "true".equals(request.getParameter("message"));
		boolean setSubFolder = "true".equals(request.getParameter("check_box_subFolder"));
		docAlertManager.deleteAlertsByIds(alertIds);

		Set<Byte> typeSet = this.getTypes4Alert(request);
		if (typeSet.size() == 4) {
			docAlertManager.addAlert(docResId, isFolder, Constants.ALERT_OPR_TYPE_ALL,
					V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, userId, sendMessage, setSubFolder, false);
		} else if (typeSet.size() > 0){
			for (Byte alertOprType : typeSet) {
				docAlertManager.addAlert(docResId, isFolder, alertOprType,
						V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, userId,
						sendMessage, setSubFolder, false);
			}
		}

		super.rendJavaScript(response, "parent.parent.window.close();");
		return null;
	}

	/**
	 * 放弃修改文档订阅
	 */
	public ModelAndView docAlertCancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		docAlertManager.deleteAlertsByIds(request.getParameter("ids"));
		super.rendJavaScript(response, "parent.location.reload(true);");
		return null;
	}

	/**
	 * 封装文档订阅管理vo
	 */
	private DocAlertAdminVO getDocAlertAdminVO(List<DocAlert> docAlerts, DocResource docResource, String from) {
		DocAlertAdminVO vo = new DocAlertAdminVO(docAlerts, docResource);

		if ("list".equals(from)) {
			// 列表显示
			vo.setIcon(this.getIcon(docResource.getIsFolder(), docResource.getMimeTypeId()));
			DocType type = contentTypeManager.getContentTypeById(docResource.getFrType());
			if (type != null)
				vo.setType(type.getName());
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(docAlerts.get(0).getCreateUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				vo.setAlertCreater(member.getName());

		} else if ("view".equals(from)) {
			// 详细查看
			vo.setPath(this.getPhysicalPath(docResource.getLogicalPath()));
			DocType type = contentTypeManager.getContentTypeById(docResource.getFrType());
			if (type != null)
				vo.setType(type.getName());
			V3xOrgMember member2 = null;
			try {
				member2 = orgManager.getMemberById(docResource.getCreateUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member2 != null)
				vo.setDocCreater(member2.getName());
		} else if ("edit".equals(from)) {
			// 修改页面
		}

		return vo;
	}

	/** 进入个人文档订阅设置窗口  */
	public ModelAndView docAlertView(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docAlert");
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		DocResource dr = docHierarchyManager.getDocResourceById(docResId);
		List<DocAlert> alerts = docAlertManager.findPersonalAlertByDrIdOfCurrentUser(docResId);
		boolean editFlag = false;
		DocAlertAdminVO vo = null;
		if (alerts != null && alerts.size() > 0) {
			editFlag = true;
			vo = this.getDocAlertAdminVO(alerts, dr, "set");
		}
		mav.addObject("isFolder", dr.getIsFolder());
		mav.addObject("docResId", docResId);
		mav.addObject("editFlag", editFlag);
		mav.addObject("name", dr.getFrName());
		mav.addObject("vo", vo);
		mav.addObject("commentEnable", dr.getCommentEnabled());
		return mav;
	}

	/**
	 * 根据用户前端的选择，确定需要提醒的文档操作类型
	 */
	private Set<Byte> getTypes4Alert(HttpServletRequest request) {
		String add = request.getParameter("check_box_add");
		String edit = request.getParameter("check_box_edit");
		String delete = request.getParameter("check_box_delete");
		String forum = request.getParameter("check_box_forum");
		
		Set<Byte> typeSet = new HashSet<Byte>();
		if (add != null)
			typeSet.add(Constants.ALERT_OPR_TYPE_ADD);
		if (edit != null)
			typeSet.add(Constants.ALERT_OPR_TYPE_EDIT);
		if (delete != null)
			typeSet.add(Constants.ALERT_OPR_TYPE_DELETE);
		if (forum != null)
			typeSet.add(Constants.ALERT_OPR_TYPE_FORUM);
		
		return typeSet;
		
	}
	
	/** 文档个人订阅设置页面的保存  */
	public ModelAndView docAlert(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = CurrentUser.get().getId();
		Long docResId = Long.valueOf(request.getParameter("docResId"));
		boolean isFolder = Boolean.parseBoolean(request.getParameter("isFolder"));
		boolean sendMessage = "true".equals(request.getParameter("message"));
		boolean setSubFolder = false;

		docAlertManager.deleteAlertByDocResourceIdOfCurrentUesr(docResId);

		Set<Byte> typeSet = this.getTypes4Alert(request);
		if (typeSet.size() == 0) {
			docAlertLatestManager.deleteAlertLatestByDrIdAndOprTypeOfCurrentUser(docResId, null);
		} else if (typeSet.size() == 4) {
			docAlertManager.addAlert(docResId, isFolder,
					Constants.ALERT_OPR_TYPE_ALL,
					V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, userId,
					sendMessage, setSubFolder, false);
		} else {
			// 删除不订阅的 DocAlertLatest
			Set<Byte> delTypes = new HashSet<Byte>();
			if (!typeSet.contains(Constants.ALERT_OPR_TYPE_EDIT))
				delTypes.add(Constants.ALERT_OPR_TYPE_EDIT);
			if (!typeSet.contains(Constants.ALERT_OPR_TYPE_FORUM))
				delTypes.add(Constants.ALERT_OPR_TYPE_FORUM);
			if (!typeSet.contains(Constants.ALERT_OPR_TYPE_ADD))
				delTypes.add(Constants.ALERT_OPR_TYPE_ADD);
			
			docAlertLatestManager.deleteAlertLatestByDrIdAndOprTypeOfCurrentUser(docResId, delTypes);
			for (Byte alertOprType : typeSet) {
				docAlertManager.addAlert(docResId, isFolder, alertOprType,
						V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, userId,
						sendMessage, setSubFolder, false);
			}

			if (!isFolder) {
				docAlertManager.addToLatest(docResId, userId);
			}

		}
		Boolean f = (Boolean)(BrowserFlag.OpenWindow.getFlag(CurrentUser.get()));
		if(f){
			super.rendJavaScript(response, "parent.close();");
		} else {
			super.rendJavaScript(response, "parent.parent.winDocAlert.close();");
		}
		return null;
	}

	/** ******************* 订阅管理结束 ***************************** */

	/** ************************ 学习区开始 ******************************** */
	/** 发送到学习区  */
	public ModelAndView sendToLearn(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String successKey = "doc_learning_personal_success_alert";
		String failureKey = "doc_learning_personal_failure_alert";
		try {
			String sdocid = request.getParameter("docId");
			String userIds = request.getParameter("userIds");
			String userType = request.getParameter("userType");
			String orgType = V3xOrgEntity.ORGENT_TYPE_MEMBER;
			List<Long> orgIds = new ArrayList<Long>();
			if ("member".equals(userType)) {
				String[] ss = userIds.split(",");
				for (String s : ss) {
					orgIds.add(Long.valueOf(s));
				}
			} else if ("dept".equals(userType)) {
				successKey = "doc_learning_dept_success_alert";
				failureKey = "doc_learning_dept_failure_alert";

				orgType = V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
				if (userIds == null || userIds.equals("")) {
					// 预防跨部门担任管理员
					Set<Long> depts = DocMVCUtils.getDepSetAdmin(this.spaceManager);

					if (depts != null && depts.size() > 0) {
						orgIds.add(depts.iterator().next());
					} else {
						orgIds.add(CurrentUser.get().getDepartmentId());
					}
				} else {
					StringTokenizer stk = new StringTokenizer(userIds, ",");
					while (stk.hasMoreTokens()) {
						orgIds.add(Long.valueOf(stk.nextToken()));
					}
				}
			} else if ("account".equals(userType)) {
				successKey = "doc_learning_account_success_alert";
				failureKey = "doc_learning_account_failure_alert";

				orgType = V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
				orgIds.add(CurrentUser.get().getLoginAccount());

			} else if ("group".equals(userType)) {
				successKey = "doc_learning_group_success_alert";
				failureKey = "doc_learning_group_failure_alert";

				orgType = Constants.ORGENT_TYPE_GROUP;
				orgIds.add(0L);
			}

			if (sdocid == null || "".equals(sdocid)) {
				// 上面菜单
				String[] ids = request.getParameterValues("id");
				List<Long> docIds = new ArrayList<Long>();
				for (String s : ids) {
					if (docHierarchyManager.docResourceExist(Long.valueOf(s))) {
						docIds.add(Long.valueOf(s));
					}
				}
				docLearningManager.sendToLearnCenter(docIds, orgType, orgIds);
				docHierarchyManager.setDocLearning(docIds);
			} else {
				// 右键菜单过来，单个文档id
				long docId = Long.valueOf(sdocid);
				if (docHierarchyManager.docResourceExist(docId)) {
					docLearningManager.sendToLearnCenter(docId, orgType, orgIds);
					docHierarchyManager.setDocLearning(docId);
				}
			}

			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang." + successKey + "'));");
		} 
		catch (Exception e) {
			log.error("发送到学习区", e);
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang." + failureKey + "'));");
		}
		return null;
	}

	// 更多
	@SuppressWarnings("unchecked")
	public ModelAndView docLearningMore(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docLearningMore");
		String deptId = request.getParameter("deptId");
		String accountId = request.getParameter("accountId");
		String groupId = request.getParameter("groupId");
		long orgId = CurrentUser.get().getId();
		String orgType = V3xOrgEntity.ORGENT_TYPE_MEMBER;
		String titlePostfix = "personal";
		if (deptId != null && !"".equals(deptId)) {
			orgType = V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
			orgId = Long.valueOf(deptId);
			titlePostfix = "dept";
		} else if (accountId != null && !"".equals(accountId)) {
			orgType = V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
			orgId = Long.valueOf(accountId);
			titlePostfix = "account";
		} else if (groupId != null && !"".equals(groupId)) {
			orgType = Constants.ORGENT_TYPE_GROUP;
			orgId = CurrentUser.get().getLoginAccount();
			//GOV-4188.公共空间里点击组织学习区和组织知识文档 start
			if((Boolean)SysFlag.is_gov_only.getFlag()) {
				titlePostfix = "gov";
			} else {
				titlePostfix = "group";
			}
			//GOV-4188.公共空间里点击组织学习区和组织知识文档 end
		}

		boolean canAdmin = true;
		if (!orgType.equals(V3xOrgEntity.ORGENT_TYPE_MEMBER))
			canAdmin = this.canAdminSpace(orgType, orgId);

		String condition = request.getParameter("condition");
		List alist = null;
		if(condition!=null && !"".equals(condition)){
			String value = ""; 
			if("createDate".equals(condition)){
				value = request.getParameter("textfield")+" # "+request.getParameter("textfield1");
			}else{
				value = request.getParameter("textfield"); 
			}
			alist = docLearningManager.getDocLearningsByPage(orgType, orgId,condition,value);
		}else{
			alist = docLearningManager.getDocLearningsByPage(orgType, orgId);
		} 

		List<DocLearning> dls = (List<DocLearning>) (alist.get(1));
		List<DocLearningVO> dlvos = this.getDocLearningVOs(dls);
		mav.addObject("canAdmin", canAdmin);
		mav.addObject("dlvos", dlvos);
		mav.addObject("total", (Integer) (alist.get(0)));
		mav.addObject("title", "doc.jsp.home.more.learn.title." + titlePostfix);

		mav.addObject("deptId", deptId);
		mav.addObject("accountId", accountId);
		mav.addObject("groupId", groupId);
		mav.addObject("types", contentTypeManager.getAllSearchContentType()); 
		return mav;
	}

	/** 取消  */
	public ModelAndView docLearningCancel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		docLearningManager.cancelLearn(request.getParameter("ids"));
		super.rendJavaScript(response, "parent.location.reload(true);");
		return null;
	}

	// 封装学习文档的vo
	private List<DocLearningVO> getDocLearningVOs(List<DocLearning> dls) {
		List<DocLearningVO> ret = new ArrayList<DocLearningVO>();
		if (dls == null)
			return ret;
		for (DocLearning dl : dls) {
			DocLearningVO vo = new DocLearningVO(dl);
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(dl.getCreateUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				vo.setRecommender(member.getName());

			vo.setIcon(this.getIcon(false, dl.getDocResource().getMimeTypeId()));
			ret.add(vo);
		}

		return ret;
	}

	/**
	 * 部门选择
	 */
	public ModelAndView selectDepts(HttpServletRequest request, HttpServletResponse response) {
		List<V3xOrgDepartment> depts = this.getAuthorizedDepartments();
		return new ModelAndView("doc/deptSelect", "depts", depts);
	}

	public ModelAndView docLearningHistoryIframe(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docLearningHistoryIframe");
	}

	/**
	 * 进入学习历史界面
	 */
	public ModelAndView docLearningHistory(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/docLearningHistory");
		long docId = Long.valueOf(request.getParameter("docId"));
		List<DocLearningHistory> dlhs = null;
		String sdept = request.getParameter("deptId");
		String isGroupLibS = request.getParameter("isGroupLib");
		boolean isGroupLib = ("true".equals(isGroupLibS));

		if (sdept == null || "".equals(sdept)) {
			dlhs = docLearningManager.getTheLearnHistoryByPage(docId);
		} else {
			long deptId = Long.valueOf(sdept);
			dlhs = docLearningManager.getTheLearnHistoryByDeptByPage(docId, deptId);
		}

		List<DocLearningHistoryVO> dlhvos = this.getDocLearningHistoryVOs(dlhs, isGroupLib);
		// 查询条件
		String searchContent = request.getParameter("searchContent");
		if (searchContent != null && searchContent != "") {
			ret.addObject("searchContent", searchContent);
		}
		ret.addObject("docId", docId);
		ret.addObject("dlhvos", dlhvos);
		return ret;
	}

	// 封装学习历史vo
	private List<DocLearningHistoryVO> getDocLearningHistoryVOs(List<DocLearningHistory> dlhs, boolean isGroupLib) {
		List<DocLearningHistoryVO> vos = new ArrayList<DocLearningHistoryVO>();
		if (dlhs == null || dlhs.size() == 0)
			return vos;

		String accountName = "";

		for (DocLearningHistory dlh : dlhs) {
			DocLearningHistoryVO vo = new DocLearningHistoryVO(dlh);
			try {
				V3xOrgMember member = orgManager.getMemberById(dlh.getAccessMemberId());

				if (member != null) {
					if (isGroupLib) {
						V3xOrgAccount acc = orgManager.getAccountById(member.getOrgAccountId());
						if (acc != null)
							accountName = "(" + acc.getShortname() + ")";
					}
					vo.setMemberName(member.getName());
					V3xOrgDepartment dept = orgManager.getDepartmentById(dlh.getDepartmentId());
					if (dept != null) {
						vo.setDeptName(dept.getName() + accountName);
					} else {
						vo.setDeptName("");
					}
				} else {
					vo.setDeptName("");
					vo.setMemberName("");
				}
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}

			vos.add(vo);
		}

		return vos;
	}

	/** ************************ 学习区结束 ******************************** */

	/** 进入编辑文档高级属性界面  */
	public ModelAndView editDocPropertiesPage(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/editDocProperties");
	}

	/**
	 * 当前用户角色判断
	 * @throws BusinessException
	 * @return 传过来部门，返回当前用户管理的部门集合，如果一个没有，返回空集合 id, name
	 */
	public List<V3xOrgDepartment> getAuthorizedDepartments() {
		List<V3xOrgDepartment> ret = new ArrayList<V3xOrgDepartment>();
		Set<Long> deptSet = DocMVCUtils.getDepSetAdmin(this.spaceManager);
		for (Long id : deptSet) {
			V3xOrgDepartment dept = null;
			try {
				dept = orgManager.getDepartmentById(id);
			} catch (BusinessException e) {
				log.error("orgManager取得dept", e);
			}
			if (dept != null)
				ret.add(dept);
		}

		return ret;
	}

	/**
	 * 知识管理自己实现的当前位置
	 */
	public ModelAndView navigation(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/navigation");
	}

	/** 从文档中心顶级节点进入文档库列表界面 */
	public ModelAndView docLibsList(HttpServletRequest request, HttpServletResponse response) {
		List<DocLibTableVo> docTableVo = this.getDocLibVOs(true);
		return new ModelAndView("doc/libsList", "docTableVo", docTableVo);
	}

	/*---------------------------- 2007.10.12 修改关联文档添加方式begin -------------------------------------*/
	/*-------整个系统共用一套框架(collaboration/list4QuoteFrame.jsp)，每个应用拥有一个页签 ----------------------*/
	/**
	 * 进入关联文档添加页面框架
	 */
	public ModelAndView list4QuoteFrame(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("collaboration/list4QuoteFrame");
	}

	/**
	 * 进入关联文档添加页面框架的知识管理页签
	 */
	public ModelAndView docQuoteFrame(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/quote/docQuoteFrame");
	}

	/**
	 * 进入关联文档添加页面框架的知识管理页签 树
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView docQuoteTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.listRoots(request, response);
	}

	/** 进入关联文档添加页面框架的知识管理页签 列表 */
	public ModelAndView docQuoteList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView ret = new ModelAndView("doc/quote/docQuoteList");
		List<DocLinkVO> the_list = null;

		Long userId = CurrentUser.get().getId();
		DocResource root = docHierarchyManager.getPersonalFolderOfUser(userId);
		if (root != null) {
			List<DocResource> drs = docHierarchyManager.findAllDocsByPage(root.getId(), root.getFrType(), userId,"quote");
			the_list = this.getDocLinkVos(drs);
			ret.addObject("parentId", root.getId());
			
			Long docLibId = root.getDocLibId();
			ret.addObject("docLibId", docLibId).addObject("docLibType", Constants.PERSONAL_LIB_TYPE);
			DocMVCUtils.renderSearchConditions(ret, docLibManager, docLibId);
		}

		List<DocType> types = contentTypeManager.getAllSearchContentType();
		return ret.addObject("the_list", the_list).addObject("types", types);
	}

	/** 关联树的展开 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView xmlJspQuote(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		Long userId = CurrentUser.get().getId();

		String sparentId = request.getParameter("resId");
		Long parentId = Long.valueOf(sparentId);
		DocResource parent = docHierarchyManager.getDocResourceById(parentId);

		// 对于资源是否存在的判断
		if (parent == null) {
			out.println("<exist>no</exist>");
			return null;
		}

		Long docLibId = parent.getDocLibId();
		DocLib lib = docLibManager.getDocLibById(docLibId);

		String stype = request.getParameter("frType");
		Long frType = Long.valueOf(stype);

		List<DocResource> drs = null;
		boolean isPersonalLib = lib.isPersonalLib();
		if (isPersonalLib) {
			drs = docHierarchyManager.findFolders(parentId, frType, userId, "", true);
		} 
		else if (CurrentUser.get().isAdministrator() || CurrentUser.get().isGroupAdmin()) {
			drs = docHierarchyManager.findFoldersWithOutAcl(parentId);
		} 
		else {
			String orgIds = Constants.getOrgIdsOfUser(userId);
			drs = docHierarchyManager.findFolders(parentId, frType, userId, orgIds, false);
		}

		if ((drs == null) || (drs.size() == 0))
			return null;
		
		List<DocTreeVO> folders = new ArrayList<DocTreeVO>();
		for (DocResource dr : drs) {
			if ((dr.getFrType() == Constants.FOLDER_PLAN)
					|| (dr.getFrType() == Constants.FOLDER_TEMPLET)
					|| (dr.getFrType() == Constants.FOLDER_SHARE)
					|| (dr.getFrType() == Constants.FOLDER_BORROW)
					|| (dr.getFrType() == Constants.FOLDER_SHAREOUT)
					|| (dr.getFrType() == Constants.FOLDER_BORROWOUT)) {
				continue;
			} else {
				if (isPersonalLib)
					dr.setIsMyOwn(true);
				DocTreeVO vo = this.getDocTreeVO(userId, dr, isPersonalLib);
				folders.add(vo);
			}
		}

		out.println("<tree text=\"loaded\">");
		String xmlstr = DocMVCUtils.getXmlStr4LoadNodeOfQuoteTree(docLibId, folders);
		out.println(xmlstr);
		out.println("</tree>");
		return null;
	}

	/**
	 * 进入关联文档添加页面框架的知识管理页签 列表 展开
	 */
	public ModelAndView listDocs4Quote(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long resId = Long.valueOf(request.getParameter("resId"));
		// 对于资源是否存在的判断
		if (!docHierarchyManager.docResourceExist(resId)) {
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_alert_source_deleted_folder'));" +
										   "parent.location.href = parent.location.href;");
			return null;
		}
		long frType = Long.valueOf(request.getParameter("frType"));

		ModelAndView ret = new ModelAndView("doc/quote/docQuoteList");
		List<DocResource> drs = docHierarchyManager.findAllDocsByPage(resId, frType, CurrentUser.get().getId(),"quote");
		List<DocLinkVO> the_list = this.getDocLinkVos(drs);

		List<DocType> types = contentTypeManager.getAllSearchContentType(); // 获取所有得内容类型
		ret.addObject("the_list", the_list);
		ret.addObject("types", types);
		ret.addObject("parentId", resId);
		
		Long docLibId = NumberUtils.toLong(request.getParameter("docLibId"));
		DocMVCUtils.renderSearchConditions(ret, docLibManager, docLibId);

		return ret;
	}
	
	/** 关联文档查询 - 单个属性查询  */
	public ModelAndView docQuoteSimpleSearch(HttpServletRequest request, HttpServletResponse response) {
		return this.docQuoteSearch(request, response, true);
	}
	
	/** 关联文档查询 - 多个属性高级组合查询  */
	public ModelAndView docQuoteAdvancedSearch(HttpServletRequest request, HttpServletResponse response) {
		return this.docQuoteSearch(request, response, false);
	}
	
	private ModelAndView docQuoteSearch(HttpServletRequest request, HttpServletResponse response, boolean isSimple) {
		ModelAndView ret = new ModelAndView("doc/quote/docQuoteList");
		Long parentId = NumberUtils.toLong(request.getParameter("parentId"));
		String docLibStr = StringUtils.defaultIfEmpty(request.getParameter("docLibType"), String.valueOf(Constants.PERSONAL_LIB_TYPE));
		Byte docLibType = Byte.valueOf(docLibStr);
		
		List<DocResource> drs = null;
		if(isSimple) {
			SimpleDocQueryModel simpleQueryModel = SimpleDocQueryModel.parseRequest(request);
			drs = this.docHierarchyManager.getSimpleQueryResult(simpleQueryModel, parentId, docLibType,"quote");
			ret.addObject("simpleQueryModel", simpleQueryModel);
		}
		else {
			DocSearchModel dsm = DocSearchModel.parseRequest(request);
			drs = this.docHierarchyManager.getAdvancedQueryResult(dsm, parentId, docLibType,"quote");
		}
		List<DocLinkVO> the_list = this.getDocLinkVos(drs);

		List<DocType> types = contentTypeManager.getAllSearchContentType();
		ret.addObject("the_list", the_list);
		ret.addObject("types", types);
		ret.addObject("parentId", parentId);
		
		Long docLibId = NumberUtils.toLong(request.getParameter("docLibId"));
		DocMVCUtils.renderSearchConditions(ret, docLibManager, docLibId);
		return ret;
	}
	
	// 封装DocLinkVo
	private List<DocLinkVO> getDocLinkVos(List<DocResource> drs) {
		List<DocLinkVO> ret = new ArrayList<DocLinkVO>();

		if (drs == null || drs.size() == 0)
			return ret;

		long docLibId = drs.get(0).getDocLibId();
		DocLib lib = docLibManager.getDocLibById(docLibId);

		for (int i = 0; i < drs.size(); i++) {
			DocLinkVO doclinkVo = new DocLinkVO(drs.get(i));
			String userName = "";

			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(drs.get(i).getCreateUserId());
			} catch (BusinessException e) {
				log.error("orgManager取得member", e);
			}
			if (member != null)
				userName = member.getName();

			doclinkVo.getDocRes().setFrName(doclinkVo.getDocRes().getFrName().replace("'", "‘"));
			doclinkVo.setUserName(userName);
			doclinkVo.setIcon(this.getIcon(drs.get(i).getIsFolder(), drs.get(i).getMimeTypeId()));
			DocType docType = contentTypeManager.getContentTypeById(drs.get(i).getFrType());
			if (docType != null)
				doclinkVo.setType(docType.getName());

			if (drs.get(i).getDocLibId() == docLibId) {
				doclinkVo.setDocLibType(lib != null ? lib.getType() : Constants.PERSONAL_LIB_TYPE);
			} else {
				DocLib lib2 = docLibManager.getDocLibById(drs.get(i).getDocLibId());
				doclinkVo.setDocLibType(lib2 != null ? lib2.getType() : Constants.PERSONAL_LIB_TYPE);
			}

			ret.add(doclinkVo);
		}

		return ret;
	}
	
	public ModelAndView openHelp(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/help", "isPersonLib", request.getParameter("isPersonLib"));
	}

	// 从首页进入新建文档
	public ModelAndView addDocIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView ret = new ModelAndView("doc/docAdd");
		docHomepageIndex(request, response);
		String from = "project";
		String bodyType = request.getParameter("bodyType");
		ret.addObject("bodyType", bodyType);
		String sdocId = request.getParameter("docResId");
		String sprojectId = request.getParameter("projectId");
		String sprojectPhaseId = request.getParameter("projectPhaseId");
		long docResId = 0L;
		DocResource parent = null;
		if (sdocId != null && !sdocId.equals("")) {
			docResId = Long.valueOf(sdocId).longValue();
			parent = docHierarchyManager.getDocResourceById(docResId);
		} 
		else if (Strings.isNotBlank(sprojectId)) {
			long projectId = Long.valueOf(sprojectId).longValue();
			long projectPhaseId = NumberUtils.toLong(sprojectPhaseId, TaskConstants.PROJECT_PHASE_ALL);
			DocResource projectFolder = null;
			if(projectPhaseId == TaskConstants.PROJECT_PHASE_ALL) {
				projectFolder = docHierarchyManager.getProjectFolderByProjectId(projectId);
			}
			else {
				projectFolder = docHierarchyManager.getProjectFolderByProjectId(projectPhaseId, true);
			}
			
			if (projectFolder != null) {
				parent = projectFolder;
				
				docResId = projectFolder.getId().longValue();
				ret.addObject("commentEnabled", projectFolder.getCommentEnabled());
				ret.addObject("versionEnabled", projectFolder.isVersionEnabled());
				long frType = projectFolder.getFrType();
				ret.addObject("frType", Long.valueOf(frType));
			}
		}
		if(parent == null){
			PrintWriter out = response.getWriter();
			super.printV3XJS(out);
			out.println("<script>");
			if (sdocId != null && !sdocId.equals("")) {
				out.println("	alert('" + Constants.getDocI18nValue("doc.forder.noexist") + "');");
			}else{
				out.println("	alert('" + Constants.getDocI18nValue("doc.forder.project.noexist") + "');");
			}
				out.println("	window.history.back();" +
					"</script>");
			out.close();
			return null;
		}
		DocLib lib = docLibManager.getDocLibById(parent.getDocLibId());
		byte docLibType = lib.getType();
		boolean contentTypeFlag = true;
		String htmlStr = "";
		if (docLibType != Constants.PERSONAL_LIB_TYPE.byteValue()) {
			long docLibId = Long.valueOf(lib.getId().longValue()).longValue();
			List<DocType> contentTypes = docLibManager.getContentTypesForDoc(docLibId);
			ret.addObject("contentTypes", contentTypes);
			if (contentTypes == null || contentTypes.size() < 1)
				contentTypeFlag = false;
			else
				htmlStr = htmlUtil.getNewHtml(((DocType) contentTypes.get(0))
						.getId().longValue());
		} else {
			contentTypeFlag = false;
		}
		ret.addObject("parentDr", parent);
		ret.addObject("contentTypeFlag", Boolean.valueOf(contentTypeFlag));
		ret.addObject("html", htmlStr);
		ret.addObject("docLib", lib);
		ret.addObject("from", from);
		ret.addObject("docResId", Long.valueOf(docResId));

		return ret;
	}

	// 从精灵上传文档
	@SuppressWarnings("unchecked")
	public ModelAndView uploadDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String error = "uploadError:";
		String uploadok = "uploadOk:";
		PrintWriter out = response.getWriter();
		try {
			Long userId = CurrentUser.get().getId();
			DocLib doclib = docLibManager.getOwnerDocLibByUserId(userId);
			long docLibId = doclib.getId();
			Byte docLibType = 1;
			Long docResourceId = docHierarchyManager.getDocByType(docLibId, Constants.FOLDER_MINE).getId();

			boolean parentCommentEnabled = false;

			List<V3XFile> list = new ArrayList<V3XFile>();
			try {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				Iterator fileNames = multipartRequest.getFileNames();
				while (fileNames.hasNext()) {
					String filename = String.valueOf(fileNames.next());
					MultipartFile fileItem = multipartRequest.getFile(filename);
					V3XFile file = fileManager.save(fileItem.getInputStream(),
							ApplicationCategoryEnum.doc, filename, new Date(), true);
					list.add(file);
				}
			} catch (Exception e) {
				out.println(error);
				log.error("通过fileManager保存file", e);
				return null;
			}
			// 得到上传文件
			if (list == null) {
				out.println(error);
				return null;
			}
			if (list.isEmpty() == false) {
				String sysTemp = SystemEnvironment.getSystemTempFolder();
				String docTemp = sysTemp + "/doctemp/";
				File temp = new File(docTemp);
				temp.mkdir();
				for (V3XFile the_file : list) {
					DocResource dr = docHierarchyManager.uploadFileWithoutAcl(
							the_file, docLibId, docLibType, docResourceId,
							userId, parentCommentEnabled, false);
					Long newId = dr.getId();
					// 记录操作日志
					operationlogManager.insertOplog(newId, docResourceId,
							ApplicationCategoryEnum.doc,
							ActionType.LOG_DOC_UPLOAD,
							ActionType.LOG_DOC_UPLOAD + ".desc",
							CurrentUser.get().getName(), the_file.getFilename());
					// 更新订阅文档
					docAlertLatestManager.addAlertLatest(dr,
							Constants.ALERT_OPR_TYPE_ADD, userId,
							new Timestamp(new Date().getTime()),
							Constants.DOC_MESSAGE_ALERT_ADD_DOC, null);

					// 全文检索
					try {
						indexManager.index(docHierarchyManager.getIndexInfo(newId));
					} catch (Exception e) {
						log.error("全文检索入库", e);
					}

					// 上传图片类或PDF文件，docBody中需保存对应记录
					if (dr.isImage() || dr.isPDF()) {
						DocBody picBody = new DocBody();
						Timestamp time = new Timestamp(new Date().getTime());
						picBody.setCreateDate(new Timestamp(time.getTime()));
						picBody.setBodyType(dr.isImage()? com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML : 
							com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_PDF);
						picBody.setContent(dr.getSourceId().toString());
						
						this.docHierarchyManager.saveBody(newId, picBody);
					}
					
					if(OfficeTransHelper.allowTrans(the_file)){
						officeTransManager.generate(the_file.getId(), the_file.getCreateDate(), false);
					}
				}
			}
		} catch (DocException e) {
			out.println(error);
			out.println(e.getMessage());
			return null;
		} catch(Exception e) {
			// -> Ignore
		}
		out.println(uploadok);
		return null;
	}
	
	/*----------------------------------------文档历史版本--------------------------------------*/
	
	/** 历史版本信息记录列表框架页面 */
	public ModelAndView listAllDocVersionsFrame(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/history/docResHistoriesFrame");
	}
	
	/** 列出文档对应的所有历史版本信息记录 */
	public ModelAndView listAllDocVersions(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long docResId = NumberUtils.toLong(request.getParameter("docResId"));
		List<DocVersionInfo> allVersions = docVersionInfoManager.getAllDocVersion(docResId, SearchModel.getSearchModel(request));
		return new ModelAndView("doc/history/docResHistories", "allVersions", allVersions);
	}
	
	/** 删除选中的历史版本信息记录 */
	public ModelAndView deleteDocVersions(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.docVersionInfoManager.delete(request.getParameter("docVersionIds"));
		super.rendJavaScript(response, "parent.window.location.href = parent.window.location;");
		return null;
	}
	
	/**
	 * 文档图片更多
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public ModelAndView moreDocPictures(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mav = new ModelAndView("doc/moreDocPictures");
		User user = CurrentUser.get();
		String folderIdStr = request.getParameter("folderId");
		String fragmentId = request.getParameter("fragmentId");
    	String ordinal = request.getParameter("ordinal");
    	
    	
    	List<SectionPanel> panels = portletEntityPropertyManager.getSectionPanel(Long.parseLong(fragmentId), ordinal, "singlePanel", "");
    	if(CollectionUtils.isNotEmpty(panels)){
    		for(SectionPanel panel : panels){
    			DocResource dr = docHierarchyManager.getDocResourceById(NumberUtils.toLong(panel.getId()));
    			if(dr != null){
    				String panelName = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, dr.getFrName());
    				panel.setName(panelName);
    			}
    		}
    	}
		List<DocResource> docResources = null;
		if(Strings.isNotBlank(folderIdStr)){
			docResources = docHierarchyManager.getDocsByTypes(Long.parseLong(folderIdStr),user.getId(),Constants.FORMAT_TYPE_ID_UPLOAD_JPG,Constants.FORMAT_TYPE_ID_UPLOAD_GIF,Constants.FORMAT_TYPE_ID_UPLOAD_PNG);
		}
		
		int size = Pagination.getRowCount();
		
		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"),Pagination.getMaxResults());
		if (pageSize < 1) {
			pageSize = Pagination.getMaxResults();
		}
		
		int page = NumberUtils.toInt(request.getParameter("page"), 1);
		if(docResources != null){
			if (size == 0) {
				size = docResources.size();
			}
			
			for(DocResource doc : docResources){
				try {
					Date cDate = this.fileManager.getV3XFile(doc.getSourceId()).getCreateDate();
					doc.setCreateTime(new Timestamp(cDate.getTime()));
				}
				catch (BusinessException e) {
					log.error("获取文件时出现异常[文件ID= " + doc.getSourceId() + "]", e);
				}
			}
		}
		
		int pages = (size + pageSize - 1) / pageSize;
		if (pages < 1) {
			pages = 1;
		}

		if (page < 1) {
			page = 1;
		}
		else if (page > pages) {
			page = pages;
		}
		mav.addObject("docResources", docResources);
		mav.addObject("folderId", folderIdStr);
		mav.addObject("pageSize", pageSize);
		mav.addObject("page", page);
		mav.addObject("pages", pages);
		mav.addObject("size", size);
		mav.addObject("allPanels", panels);
		return mav;
	}
	
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	public void setDocLearningManager(DocLearningManager docLearningManager) {
		this.docLearningManager = docLearningManager;
	}
	public void setWebMailManager(WebMailManager webMailManager) {
		this.webMailManager = webMailManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setCollaborationController(CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}
	public void setRssManager(RssManager rssManager) {
		this.rssManager = rssManager;
	}
	public void setDocAlertLatestManager(DocAlertLatestManager docAlertLatestManager) {
		this.docAlertLatestManager = docAlertLatestManager;
	}
	public void setDocFavoriteManager(DocFavoriteManager docFavoriteManager) {
		this.docFavoriteManager = docFavoriteManager;
	}
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setDocAlertManager(DocAlertManager docAlertManager) {
		this.docAlertManager = docAlertManager;
	}
	public void setDocMetadataManager(DocMetadataManager docMetadataManager) {
		this.docMetadataManager = docMetadataManager;
	}
	public void setDocForumManager(DocForumManager docForumManager) {
		this.docForumManager = docForumManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}
	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}
	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}
	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setMaxSize4ShowContent(int maxSize4ShowContent) {
		this.maxSize4ShowContent = maxSize4ShowContent;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public void setHtmlUtil(HtmlUtil htmlUtil) {
		this.htmlUtil = htmlUtil;
	}
	public String getJsonView() {
		return jsonView;
	}
	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}
	public void setBlogArticleManager(BlogArticleManager blogArticleManager) {
		this.blogArticleManager = blogArticleManager;
	}
	public void setBlogManager(BlogManager blogManager) {
		this.blogManager = blogManager;
	}
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
	public void setDocVersionInfoManager(DocVersionInfoManager docVersionInfoManager) {
		this.docVersionInfoManager = docVersionInfoManager;
	}
	public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}
	public void setTypesShowContentDirectlyTEXT(String[] typesShowContentDirectlyTEXT) {
		if(typesShowContentDirectlyTEXT != null){
			for (String s : typesShowContentDirectlyTEXT) {
				this.typesShowContentDirectlyTEXT.add(s);
			}
		}
	}
	public void setTypesShowContentDirectlyHTML(String[] typesShowContentDirectlyHTML) {
		if(typesShowContentDirectlyHTML != null){
			for (String s : typesShowContentDirectlyHTML) {
				this.typesShowContentDirectlyHTML.add(s);
			}
		}
	}

	public PartitionManager getPartitionManager() {
		return partitionManager;
	}

	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}

	public void setOfficeTransManager(OfficeTransManager officeTransManager) {
		this.officeTransManager = officeTransManager;
	}
	
	public void setPortletEntityPropertyManager(
			PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}
	public SearchManager getSearchManager() {
		return searchManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
}