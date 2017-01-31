package com.seeyon.v3x.doc.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.seeyon.v3x.common.flag.SysFlag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.doc.dao.MetadataDefDao;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocMetadataOption;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.manager.ContentTypeManager;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocAlertManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.manager.MetadataDefManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.DocMVCUtils;
import com.seeyon.v3x.doc.webmodel.DocLibTableVo;
import com.seeyon.v3x.doc.webmodel.DocTypeVO;
import com.seeyon.v3x.doc.webmodel.MetadataDefVO;
import com.seeyon.v3x.doc.webmodel.MetadataMenu;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;


/**
 * 文档管理的controller，涉及文档库，内容类型，文档属性
 * 
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
public class DocManagerController extends BaseController {
	private static final Log log = LogFactory.getLog(DocManagerController.class);
	
	private DocLibManager docLibManager;
	private OrgManager orgManager;
	private MetadataDefManager metadataDefManager;
	private ContentTypeManager contentTypeManager;
	private DocAclManager  	docAclManager;
	private DocHierarchyManager docHierarchyManager;
	private MetadataDefDao	metadataDefDao;
	private DocAlertManager  	docAlertManager;
	private AppLogManager appLogManager ;
	private SearchManager searchManager;
	
    private SystemConfig systemConfig;
    
    public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}
    
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
	
	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
	public void setMetadataDefDao(MetadataDefDao metadataDefDao) {
		this.metadataDefDao = metadataDefDao;
	}
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}
	public void setDocAlertManager(DocAlertManager docAlertManager) {
		this.docAlertManager = docAlertManager;
	}
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	
	/*------------------------------------ 文档库管理 Start ---------------------------------------*/
	
	// 框架
	public ModelAndView docLibIndex(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("doc/docLib/index");
	}
	
	//	上层结构
	public ModelAndView docLibTopFrame(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("doc/docLib/main");
		boolean isGroupAdmin = CurrentUser.get().isGroupAdmin();
		byte status = Byte.valueOf(StringUtils.defaultIfEmpty(request.getParameter("status"), String.valueOf(Constants.DOC_LIB_ENABLED)));
		List<DocLib> docLibs = this.docLibManager.getDocLibs(isGroupAdmin, CurrentUser.get().getLoginAccount(), status);
	
		boolean isEdoc = Functions.isEnableEdoc();
	    boolean isPluginEdoc = SystemEnvironment.hasPlugin("edoc");
		for(Iterator it = docLibs.iterator();it.hasNext();){
			DocLib lib = (DocLib)it.next();
			if(lib.getType() ==  Constants.EDOC_LIB_TYPE.byteValue()){
				if(!isEdoc || !isPluginEdoc ) it.remove();
			}
		}
		
		modelView.addObject("isGroupAdmin", isGroupAdmin);
		modelView.addObject("count", docLibs.size());
			
		List<DocLibTableVo> docTableVo = this.docLibManager.getDocLibTableVOs(docLibs);
		modelView.addObject("docTableVo", docTableVo);
		
		modelView.addObject("showRssTagOnAccountAdmin", Constants.showRssTagOnAccountAdmin());
		modelView.addObject("rssEnabled", Constants.rssModuleEnabled());
		return modelView;
	}
	
	/** 停用文档库  */
	public ModelAndView disableDocLibs(HttpServletRequest request, HttpServletResponse response) {
		this.docLibManager.disableDocLibs(request.getParameter("docLibIds"));
		return super.redirectModelAndView("/docManager.do?method=docLibTopFrame");
	}
	
	/** 启用文档库  */
	public ModelAndView enableDocLibs(HttpServletRequest request, HttpServletResponse response) {
		this.docLibManager.enableDocLibs(request.getParameter("docLibIds"));
		return super.redirectModelAndView("/docManager.do?method=docLibTopFrame&status=0");
	}
	
	/** 进入新建自定义文档库界面  */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView addDocLibPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docLib/addDocLib");
		Long accountId = CurrentUser.get().getLoginAccount();
		DocLib lib = docLibManager.getDocLibById(accountId);
		if(lib == null) {
			docLibManager.addVirtualDocLib(accountId);
		}
		mav.addObject("newLibId", accountId);
		
		List<DocMetadataDefinition> columns = docLibManager.getDefaultColumnList();
		List<DocMetadataDefinition> searchConditions = docLibManager.getDefaultSearchConditions();
		DocMVCUtils.setDocMetadataDefinitionNames(columns, Constants.USER_CUSTOM_LIB_TYPE);
		DocMVCUtils.setDocMetadataDefinitionNames(searchConditions, Constants.USER_CUSTOM_LIB_TYPE);
		
		return mav.addObject("columns", columns).addObject("searchConditions", searchConditions);
	}
	
	//	 添加一个文档库
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView addDocLib(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		try {
			String docName = request.getParameter("theName").trim();
//			boolean hasName = docLibManager.hasSameNameDocLib(docName, 0L);
//			if(hasName) {
//		        super.printV3XJS(response.getWriter());
//				super.rendJavaScript(response, "parent.handleDocLibName();");
//				return null;
//			}
			
			String[] validate = docLibManager.validateDocLibName(docName, 0l);
			boolean hasSameName = BooleanUtils.toBoolean(validate[0]);
			if(hasSameName) {
				super.printV3XJS(response.getWriter());
				super.rendJavaScript(response, "parent.handleDocLibName('" + Strings.escapeJavascript(validate[1]) + "');");
				return null;
			}
			
			User admin = CurrentUser.get();
			DocLib lib = new DocLib();
			super.bind(request, lib);
			lib.setName(docName);
			List<Long> owners = FormBizConfigUtils.parseStr2Ids(request, "members");
			Long docLibId = docLibManager.addDocLib(lib, admin.getLoginAccount(), owners);		
			
			String accountName = orgManager.getAccountById(admin.getAccountId()).getName();
			this.appLogManager.insertLog(admin, AppLogAction.Doc_New, accountName, docName);
			this.appLogManager.insertLog(admin, AppLogAction.DocLib_Managers_Update, accountName, admin.getName(), docName, com.seeyon.v3x.bulletin.util.Constants.getActionText(true));
			
			Long docResId = docHierarchyManager.getRootByLibId(docLibId).getId();
			this.setAclAndAlert4DocLib(docLibId, lib.getType(), admin.getId(), docResId, owners, false);
			super.rendJavaScript(response, "parent.parent.location.reload(true);");
			
		}
		catch (Exception e) {
			log.error("添加自定义文档库时出现异常：", e);
			super.rendJavaScript(response, "parent.handleExceptionWhenSaveDocLib('" + e.getMessage() + "');");
		}
			
		return null;
	}
	
	// 修改文档库
	public ModelAndView updateDocLib(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			User user = CurrentUser.get();
			Long adminId = user.getId();
			Long docLibId = Long.valueOf(request.getParameter("docLibId"));
			String docName = request.getParameter("theName");
			DocLib docLib = docLibManager.getDocLibById(docLibId);
			
//			boolean hasName = docLibManager.hasSameNameDocLib(docName, docLibId);
			String[] validate = docLibManager.validateDocLibName(docName, docLibId);
			boolean hasSameName = BooleanUtils.toBoolean(validate[0]);
			if(hasSameName) {
				super.printV3XJS(response.getWriter());
				super.rendJavaScript(response, "parent.handleDocLibName('" + Strings.escapeJavascript(validate[1]) + "');");
				return null;
			}
			super.bind(request, docLib);
			
			DocResource dr = docHierarchyManager.getRootByLibId(docLibId);
			Long docResId = dr.getId();
			List<Long> ownerIds = FormBizConfigUtils.parseStr2Ids(request.getParameter("members"));
			List<Long> oldOwners = docLibManager.getOwnersByDocLibId(docLibId);
			if(CollectionUtils.isNotEmpty(ownerIds)) {
				byte docLibType = docLib.getType();
				if(CollectionUtils.isNotEmpty(oldOwners)) {
					if(!oldOwners.equals(ownerIds)) {
						for(Long o : oldOwners) {
							docAclManager.deletePotentByUser(docResId,o, V3xOrgEntity.ORGENT_TYPE_MEMBER, docLibType, docLibId);
							this.docAlertManager.deleteAllAlertByDocResourceIdAndOrg(dr, V3xOrgEntity.ORGENT_TYPE_MEMBER, o);
						}
						
						// 删除已经授权的用户
						docLibManager.deleteDocLibOwners(docLibId);
						docLibManager.addDocLibOwners(docLibId, ownerIds);
						
						this.setAclAndAlert4DocLib(docLibId, docLibType, adminId, docResId, ownerIds, true);
					}
				}
				else {
					this.setAclAndAlert4DocLib(docLibId, docLibType, adminId, docResId, ownerIds, true);
					docLibManager.addDocLibOwners(docLibId, ownerIds);
				}
				
			}
			docLibManager.modifyDocLib(docLib, docName);
			String name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, docLib.getName());
			String accountName = orgManager.getAccountById(user.getAccountId()).getName();
			if(user.isGroupAdmin()) {
				this.appLogManager.insertLog(user, AppLogAction.Doc_Update_group, name);
			} else {
				this.appLogManager.insertLog(user, AppLogAction.Doc_Update, accountName, name);
			}
			//文档库管理员设置与变更写入应用日志(项目库的日志不包含库管理员的修改)
			if(!docLib.isProjectLib())
				this.appLogManager.insertLog(user, AppLogAction.DocLib_Managers_Update, accountName, user.getName(), name, com.seeyon.v3x.bulletin.util.Constants.getActionText(false));
			
			docHierarchyManager.renameDocWithoutAcl(docResId, docName, adminId);
			super.rendJavaScript(response, "parent.parent.location.reload(true);");
		} 
		catch (Exception e) {
			log.error("修改文档库", e);
			super.rendJavaScript(response, "parent.handleExceptionWhenSaveDocLib('" + e.getMessage() + "');");
		}
		return null; 
	}
	
	private void setAclAndAlert4DocLib(Long docLibId, byte docLibType, Long adminId, Long docResId, List<Long> ownerIds, boolean isModify) {

		int minOrder = docAclManager.getMaxOrder();
		for(Long userId : ownerIds) {
			if(isModify) {
				docAclManager.deletePotentByMaUser(docResId, userId, V3xOrgEntity.ORGENT_TYPE_MEMBER, docLibType, docLibId);
			}
			
			Long alertId = docAlertManager.addAlert(docResId, true, Constants.ALERT_OPR_TYPE_ALL,
					V3xOrgEntity.ORGENT_TYPE_MEMBER, userId, adminId, true, false, true);
			
			docAclManager.setDeptSharePotent(userId, V3xOrgEntity.ORGENT_TYPE_MEMBER, docResId, Constants.ALLPOTENT, true, alertId,minOrder++);
		}
	}

	/** 进入修改文档库的界面  */
	public ModelAndView editDocLibPage(HttpServletRequest request, HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/docLib/editDocLib");
		long docLibId = Long.valueOf(request.getParameter("id"));
		DocLib docLib = docLibManager.getDocLibById(docLibId);
		List<Long> ownerIds = docLibManager.getOwnersByDocLibId(docLibId);
		String oldIds = StringUtils.join(ownerIds, ',');
		
		Byte docLibType = docLib.getType();
		List<DocMetadataDefinition> columns = docLibManager.getListColumnsByDocLibId(docLibId);
		List<DocMetadataDefinition> searchConditions = docLibManager.getSearchConditions4DocLib(docLibId, docLibType);
		DocMVCUtils.setDocMetadataDefinitionNames(columns, docLibType);
		DocMVCUtils.setDocMetadataDefinitionNames(searchConditions, docLibType);
		
		List<DocType> contentTypes = this.docLibManager.getContentTypes(docLibId);
		
		modelView.addObject("docLib", docLib);
		modelView.addObject("columns", columns);
		modelView.addObject("searchConditions", searchConditions);
		modelView.addObject("contentTypes", contentTypes);
		modelView.addObject("oldIds", oldIds);

		return modelView;
	}

	// 删除文档库
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView deleteDocLib(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		User user = CurrentUser.get();
		PrintWriter out = response.getWriter();
		String[] docLibIds = request.getParameterValues("docLibId");
		List<Long> list = FormBizConfigUtils.parseStrArr2Ids(docLibIds);
		try {
			if(CollectionUtils.isNotEmpty(list)) {
				String accountName = orgManager.getAccountById(user.getAccountId()).getName();
				List<String[]> parames = new ArrayList<String[]>() ;
				for(Long libId : list) {
					DocLib docLib = this.docLibManager.getDocLibById(libId) ;
					String[] str = {accountName, docLib.getName()} ;
					parames.add(str) ;
				}
				docLibManager.deleteDocLibs(list);			
				this.appLogManager.insertLogs(user, AppLogAction.Doc_Del, parames);			
				out.print("<script>");
				out.print("parent.parent.location.reload(true)");
				out.print("</script>");
			}
		} 
		catch (Exception e) {
			out.print("<script>");
			String msg = e.getMessage();
			int loc = msg.indexOf("&");
			if(loc != -1){
				// 异常有参数
				out.print("alert(parent.v3x.getMessage('"+msg.substring(0, loc)+"','" + msg.substring(loc + 1, msg.length()) +"'));");
			}else{
				out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'));");
			}
			out.println("parent.parent.location.reload(true);");
			out.print("</script>");
		}
		return null;
	}
	
	// 进入显示栏目设置页面
	// modified by handy, 2007-6-28
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView setDocListColumn(HttpServletRequest request, HttpServletResponse response)  {
		ModelAndView modelView=new ModelAndView("doc/docLib/setDocListColumn");
		long docLibId = Long.valueOf(request.getParameter("docLibId"));
		List<DocMetadataDefinition> the_list = metadataDefManager.getAllUsableMetadataDef();
		List<DocMetadataDefinition> list = docLibManager.getListColumnsByDocLibId(docLibId);
		
		DocLib lib = this.docLibManager.getDocLibById(docLibId);
		DocMVCUtils.setDocMetadataDefinitionNames(the_list, lib.getType());		
		DocMVCUtils.setDocMetadataDefinitionNames(list, lib.getType());	
		
		modelView.addObject("metadataDefs",the_list);
		modelView.addObject("columns", list);
		modelView.addObject("defalutValue", Constants.DOC_COLUMN_NAME);
		return modelView;
	}
	
	//设置显示栏目
	// modified by handy,2007-6-28
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView updateDocListColumn(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		List<List> list=new  ArrayList<List>();
		String theIds = request.getParameter("selectedColumn");			//设置的显示栏目的ID串
		long docLibId = Long.valueOf(request.getParameter("docLibId"));	//设置显示栏目的库ID
		String choiceName = request.getParameter("choiceName");		//选中的名称
		StringTokenizer tokenizer = new StringTokenizer(theIds,";");
		while(tokenizer.hasMoreTokens()){
			String str = tokenizer.nextToken();
			StringTokenizer token = new StringTokenizer(str,",");
			if(token.hasMoreTokens()){
				List _list = new ArrayList();
				long theId = Long.valueOf(token.nextToken());
				int order = Integer.valueOf(token.nextToken());
				_list.add(theId);
				_list.add(order);
				list.add(_list);
				
				this.updateDocMetaDataDefStatus(theId);
			}
		}
		docLibManager.setDocListColumn(docLibId, list);	
		Boolean f = (Boolean)(BrowserFlag.OpenWindow.getFlag(CurrentUser.get()));
		if(f){
			super.rendJavaScript(response, "parent.window.returnValue=\""+Strings.toHTML(choiceName)+"\";parent.window.close();");
		} else {
			super.rendJavaScript(response, "parent.parent.window.location.reload(true);parent.parent.winColumn.close();");
		}
		return null;
	}
	
	/** 进入文档库查询条件设置页面 */
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView setDocSearchConditions(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("doc/docLib/setDocSearchCondition");
		Long docLibId = NumberUtils.toLong(request.getParameter("docLibId"));
		DocLib lib = this.docLibManager.getDocLibById(docLibId);
		Byte docLibType = lib.getType();
		
		List<DocMetadataDefinition> all = metadataDefManager.getAllSearchableMetadataDef();
		List<DocMetadataDefinition> selected = docLibManager.getSearchConditions4DocLib(docLibId, docLibType);
		
		DocMVCUtils.setDocMetadataDefinitionNames(all, docLibType);
		DocMVCUtils.setDocMetadataDefinitionNames(selected, docLibType);
		
		String oldSearchConditionIds = FormBizConfigUtils.getIdStrs(selected);
		return mav.addObject("all", all).addObject("selected", selected).addObject("oldSearchConditionIds", oldSearchConditionIds);
	}
	
	/** 修改文档库查询条件设置 */
	@CheckRoleAccess(roleTypes=RoleType.NeedNoCheck)
	public ModelAndView updateDocSearchConditions(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String selectedSearchConditions = request.getParameter("selectedSearchConditions");
		String choiceName = request.getParameter("choiceName");
		Long docLibId = NumberUtils.toLong(request.getParameter("docLibId"));

		List<Long> scIds = FormBizConfigUtils.parseStr2Ids(selectedSearchConditions);
		for(Long scId : scIds) {
			this.updateDocMetaDataDefStatus(scId);	
		}
		docLibManager.setDocSearchConditions(docLibId, scIds);
		Boolean f = (Boolean)(BrowserFlag.OpenWindow.getFlag(CurrentUser.get()));
		if(f){
			super.rendJavaScript(response, "parent.window.returnValue=\""+Strings.toHTML(choiceName)+"\";parent.window.close();");
		} else {
			super.rendJavaScript(response, "parent.parent.window.location.reload(true);parent.parent.winSearchCondition.close();");
		}
		return null;
	}
	
	/**
	 * 修改元数据状态
	 * @param scId	元数据ID
	 */
	private void updateDocMetaDataDefStatus(Long scId) {
		DocMetadataDefinition dmd = metadataDefManager.getMetadataDefById(scId);
		if(dmd.getStatus().intValue() == Constants.DOC_METADATA_DEF_STATUS_DRAFT && !dmd.getIsSystem()){
			dmd.setStatus(Constants.DOC_METADATA_DEF_STATUS_COLUMNED);
			List<DocMetadataOption> opList = new ArrayList<DocMetadataOption>();
			if(dmd.getMetadataOption() != null)
			opList.addAll(dmd.getMetadataOption());
			metadataDefManager.updateMetadataDef(dmd, opList);
		}
	}
	
	//进入内容类型管理页面
	public ModelAndView setContentTypes(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView modelView=new ModelAndView("doc/docLib/setContentTypes");
		long docLibId = Long.valueOf(request.getParameter("docLibId"));
		List<DocType> list = contentTypeManager.getContentTypesForNew();	//获取一个库支持得所有可编辑内容类型
//		List<DocType> listed = new ArrayList<DocType>();
//		List<DocTypeList> the_list = docTypeListDao.findBy("docLibId", docLibId);		//获取已有得内容类型列表
//		for(int i=0;i<the_list.size();i++) {
//			DocTypeList typeList=the_list.get(i);
//			DocType docType=contentTypeManager.getContentTypeById(typeList.getDocTypeId());
////			if(list.contains(docType)){
////				list.remove(docType);
////			}
//			listed.add(docType);
//			
//		}
		
		List<DocType> listed = docLibManager.getContentTypes(docLibId);
		List<DocType> contentTypes = new ArrayList<DocType>();
		if(listed!=null&&listed.size()>0){
			for(DocType type :listed){
				if(!(type.getStatus()==Constants.CONTENT_TYPE_DELETED)) {
					contentTypes.add(type);
				}
			}
		}
		
		modelView.addObject("docTypes", list);
		modelView.addObject("checked", contentTypes);		
		return modelView;
	}
	
	// 设置文档库内容类型
	public ModelAndView updateContentTypes(HttpServletRequest request,
			HttpServletResponse response)  {
		long docLibId =Long.valueOf(request.getParameter("docLibId"));
		String contentTypeIds=request.getParameter("contentTypeIds");
		String docTypeNames=request.getParameter("docTypeNames");
		List<List> list=new ArrayList<List>();
		
		StringTokenizer tokenizer=new StringTokenizer(contentTypeIds,";");
		while(tokenizer.hasMoreTokens()){
			String str=tokenizer.nextToken();
			StringTokenizer token=new StringTokenizer(str,",");
			if(token.hasMoreTokens()){
				List _list=new ArrayList();
				long theId=Long.valueOf(token.nextToken());
				int order=Integer.valueOf(token.nextToken());
				_list.add(theId);
				_list.add(order);
				list.add(_list);
			}
		}
		try {
			docLibManager.addDocTypeList(docLibId, list);
		} catch (DocException e1) {
			log.error("设置库的内容类型", e1);
		}	//设置库的内容类型
		
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error("取得输出流", e);
		}
		out.println("<script>");
		out.print("parent.window.returnValue=\""+Strings.toHTML(docTypeNames)+"\";");
		out.println("window.close();");
		out.println("</script>");
		
		return null;
	}	
	
	/**
	 * 进入文档库排序iframe
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView changeDocLibOrderIframeView(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/docLib/docLibListIframe");
		return modelView;
	}
	
	/**
	 * 文档库排序页面
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView changeDocLibOrderView(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/docLib/docLibList");
		Long domainId = CurrentUser.get().getLoginAccount();
		List<DocLib> list = docLibManager.getDocLibsWithoutGroupLib(domainId); // 获取所有的自定义文档库和公共文档库
		List<DocLib> docLibs = new ArrayList<DocLib>(); 
		for (int i = 0; i < list.size(); i++) {
			DocLib docLib = list.get(i);
			if (docLib.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue()) {
				docLibs.add(docLib);
			}
		}
		boolean  isEmpty = docLibs.isEmpty();
		modelView.addObject("docLibs", docLibs);
		modelView.addObject("isEmpty",isEmpty);
		return modelView;
	}
	
	/**
	 * 文档库排序
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView changeDocLibOrder(HttpServletRequest request,
			HttpServletResponse response)  {
		//ModelAndView modelView = new ModelAndView("doc/docLib/docLibListIframe");
		Long docLibId = Long.valueOf(request.getParameter("id"));
		String type = request.getParameter("type");
		Long domainId = CurrentUser.get().getLoginAccount();
		boolean up = type.equals("up");
//		if () {
			docLibManager.moveDocLib(docLibId, domainId, up);
//		}
//		else {
//			docLibManager.moveDocLib(docLibId, domainId);
//		}
//		return modelView;
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error("取得输出流", e);
		}
		out.print("<script>");
		out.println("parent.location.href=parent.location.href;");	
		out.print("</script>");
		
		return null;
	}
		
	/**
	 * 修改文档排序的方法
	 * @param request
	 * @param response
	 * @return
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView changeDocLibOrderRep(HttpServletRequest request,
			HttpServletResponse response)  {
		try{
			String docTypeids[] = request.getParameterValues("ids") ;
			List<List> list = new ArrayList<List>() ;
			if(docTypeids.length>0){
				for(int i = 0 ; i <docTypeids.length ; i++){
					List<Long> id = new ArrayList<Long>() ;
					if( null != docTypeids[i]  && !"".equals(docTypeids[i])){
						Long docTypeid = Long.parseLong(docTypeids[i]) ;
						id.add(docTypeid) ;
						list.add(id) ;
					}
				}				
			}
			this.docLibManager.moveDocLib(list) ;			
		}catch(Exception e){
			log.error("", e) ;
		}

		
		return super.refreshWorkspace();
	}
	
	/*------------------------------------ 文档库管理 End ---------------------------------------*/
	
	
	/*------------------------------------ 文档类型管理 Start ---------------------------------------*/
	// 内容类型框架
	public ModelAndView docTypeIndex(HttpServletRequest request, 
			HttpServletResponse response)  {
		ModelAndView modelView=new ModelAndView("doc/docType/index");
		return modelView;
	}
	
	// 进入文档类型列表界面
	public ModelAndView docTypeTopFrame(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView mav = new ModelAndView("doc/docType/main");
		List<DocType> list2=contentTypeManager.getContentTypes(); 
		List<DocType> list = new ArrayList<DocType>();  //获取所有的内容类型  "doc.contenttype.gongwen"	
		String attachcfi = this.systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
		if(!attachcfi.equals("enable")&&attachcfi!=null){//判断公文开关，是否加载公文内容类型
				for(DocType mt: list2){
					if(mt.getName().equals("doc.contenttype.gongwen")) {
						continue ;
					}
					if(mt.getId().longValue() == Constants.FOLDER_SHAREOUT) {
						continue ;
					}
					if(mt.getId().longValue() == Constants.FOLDER_BORROWOUT) {
						continue ;
					}
					if((Boolean)SysFlag.is_gov_only.getFlag()){
						if("doc.contenttype.biaodan".equals(mt.getName()) && !SystemEnvironment.hasPlugin("form")) {
							continue;
						}	
					}
					 list.add(mt);    
                  }
		}else{
			for(DocType mt: list2){
				if(mt.getId().longValue() == Constants.FOLDER_SHAREOUT) {
					continue ;
				}
				if(mt.getId().longValue() == Constants.FOLDER_BORROWOUT) {
					continue ;
				}
				if((Boolean)SysFlag.is_gov_only.getFlag()){
					if("doc.contenttype.biaodan".equals(mt.getName()) && !SystemEnvironment.hasPlugin("form")) {
						continue;
					}
				}
				 list.add(mt);   
              }
		}
		
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		V3xOrgAccount account = null;
		
		List<DocTypeVO> the_list=new ArrayList<DocTypeVO>();
		long accountId = CurrentUser.get().getLoginAccount();
		for(int i=first;i<first+pageSize;i++){
			if(i>list.size()-1){
				break;
			}
			DocTypeVO docTypeVo=new DocTypeVO();
			DocType docType=list.get(i);
			docTypeVo.setDocType(docType);
			if(docType.getIsSystem()==true){
				docTypeVo.setTheDocType("doctype.system");
			}else{
				docTypeVo.setTheDocType("doctype.customer");
			}
			
			docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
			docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
			if(list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
			     try {
				  account = orgManager.getAccountById(list.get(i).getDomainId());
				  docTypeVo.setOrgName(account.getShortname());
		        	}catch (BusinessException e) {
				       log.error("orgManager取得member", e);
			         }
				}
			
			the_list.add(docTypeVo);
		}
		mav.addObject("docTypes", the_list);
		
		// 单位管理员是否显示RSS页签
		boolean showRssTagOnAccountAdmin = Constants.showRssTagOnAccountAdmin();
		mav.addObject("showRssTagOnAccountAdmin", showRssTagOnAccountAdmin);
		boolean rssEnabled = Constants.rssModuleEnabled();
		mav.addObject("rssEnabled", rssEnabled);
		
		// 当前是否集团管理员登录
		boolean isGroupAdmin = Constants.isGroupAdmin();
		mav.addObject("isGroupAdmin", isGroupAdmin);
		
		return mav;
	}	
	
	/**
	 * 文档中心，内容类型模糊查询
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryDocTypeByCondition(HttpServletRequest request,
			HttpServletResponse response)  {
		
		ModelAndView view = new ModelAndView("doc/docType/main");
		String condition = request.getParameter("condition");
		
		// 内容类型
		List<DocType> list2=contentTypeManager.getContentTypes(); 
		List<DocType> list = new ArrayList<DocType>();  //获取所有的内容类型  "doc.contenttype.gongwen"	
		String attachcfi = this.systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
		if(!attachcfi.equals("enable")&&attachcfi!=null){//判断公文开关，是否加载公文内容类型
				for(DocType mt: list2){
					if(mt.getName().equals("doc.contenttype.gongwen")) 
						continue ;
					if(mt.getId().longValue() == Constants.FOLDER_SHAREOUT) 
						continue ;
					if(mt.getId().longValue() == Constants.FOLDER_BORROWOUT) 
						continue ;
					 list.add(mt);    
                  }
		}else{
			for(DocType mt: list2){
				if(mt.getId().longValue() == Constants.FOLDER_SHAREOUT) 
					continue ;
				if(mt.getId().longValue() == Constants.FOLDER_BORROWOUT)
					continue ;
				 list.add(mt);   
              }
		}
		
		// 分页条件(假分页)
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		
		V3xOrgAccount account = null;
		List<DocTypeVO> the_list=new ArrayList<DocTypeVO>();
		List<DocTypeVO> result_list=new ArrayList<DocTypeVO>();
		long accountId = CurrentUser.get().getLoginAccount();
		
		view.addObject("condition", condition);
		
		// 按名称查询
		if (Strings.isNotEmpty(condition) && "name".equals(condition)) {
			String name = request.getParameter("textfield")==null ? "" : request.getParameter("textfield");
			view.addObject("textfield", name) ;
			for(int i =  0 ; i < list.size() ; i ++){
				DocTypeVO docTypeVo=new DocTypeVO();
				DocType docType=list.get(i);
				// 如果与所输入的名称匹配
				String strName = ResourceBundleUtil.getString("com.seeyon.v3x.doc.resources.i18n.DocResource", docType.getName());
				if(Strings.isNotBlank(name) && strName.contains(name) && docType.getStatus() != 2) {
					docTypeVo.setDocType(docType);
					if(docType.getIsSystem()) {
						docTypeVo.setTheDocType("doctype.system");
					}else{
						docTypeVo.setTheDocType("doctype.customer");
					}
					docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
					docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
					if (list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
					     try {
							  account = orgManager.getAccountById(list.get(i).getDomainId());
							  docTypeVo.setOrgName(account.getShortname());
				          } catch (BusinessException e) {
						      log.error("orgManager取得member", e);
					      }
						}
					the_list.add(docTypeVo);
				}
			}
		} else if (Strings.isNotBlank(condition) && "type".equals(condition)) {
			String typeStr = request.getParameter("type")==null ? "" : request.getParameter("type");
			view.addObject("type", typeStr);
			for(int i = 0 ; i < list.size() ; i ++) {
				DocTypeVO docTypeVo=new DocTypeVO();
				DocType docType=list.get(i);
				// 系统类型
				if("system".equals(typeStr) && docType.getIsSystem() && docType.getStatus() != 2) {
					view.addObject("type", typeStr);
					docTypeVo.setDocType(docType);
					if(docType.getIsSystem()){
						docTypeVo.setTheDocType("doctype.system");
					}else{
						docTypeVo.setTheDocType("doctype.customer");
					}
					docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
					docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
					if (list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
					     try {
							  account = orgManager.getAccountById(list.get(i).getDomainId());
							  docTypeVo.setOrgName(account.getShortname());
				          } catch (BusinessException e) {
						      log.error("orgManager取得member", e);
					      }
					}
					the_list.add(docTypeVo);
				} 
				// 自定义类型
				else if ("self".equals(typeStr) && !docType.getIsSystem() && docType.getStatus() != 2) {
					view.addObject("type", typeStr);
					docTypeVo.setDocType(docType);
					if(docType.getIsSystem()){
						docTypeVo.setTheDocType("doctype.system");
					}else{
						docTypeVo.setTheDocType("doctype.customer");
					}
					docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
					docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
					if (list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
					     try {
							  account = orgManager.getAccountById(list.get(i).getDomainId());
							  docTypeVo.setOrgName(account.getShortname());
				          } catch (BusinessException e) {
						      log.error("orgManager取得member", e);
					      }
						}
					the_list.add(docTypeVo);
				} 
			}
		} else if (Strings.isNotBlank(condition) && "status".equals(condition)) {
			String statusStr = request.getParameter("status")==null ? "" : request.getParameter("status");
			view.addObject("status", statusStr);
			for(int i = 0 ; i < list.size() ; i ++){
				DocTypeVO docTypeVo=new DocTypeVO();
				DocType docType=list.get(i);
				// 使用
				if("used".equals(statusStr) && docType.getStatus()==1 && docType.getStatus() != 2) {
					view.addObject("status", statusStr);
					docTypeVo.setDocType(docType);
					if(docType.getIsSystem()){
						docTypeVo.setTheDocType("doctype.system");
					}else{
						docTypeVo.setTheDocType("doctype.customer");
					}
					docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
					docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
					if (list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
					     try {
							  account = orgManager.getAccountById(list.get(i).getDomainId());
							  docTypeVo.setOrgName(account.getShortname());
				          } catch (BusinessException e) {
						      log.error("orgManager取得member", e);
					      }
					}
					the_list.add(docTypeVo);
				} 
				else if ("unused".equals(statusStr) && docType.getStatus()==0 && docType.getStatus() != 2) {
					view.addObject("status", statusStr);
					docTypeVo.setDocType(docType);
					if(docType.getIsSystem()){
						docTypeVo.setTheDocType("doctype.system");
					}else{
						docTypeVo.setTheDocType("doctype.customer");
					}
					docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
					docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
					if (list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
					     try {
							  account = orgManager.getAccountById(list.get(i).getDomainId());
							  docTypeVo.setOrgName(account.getShortname());
				          } catch (BusinessException e) {
						      log.error("orgManager取得member", e);
					      }
					}
					the_list.add(docTypeVo);
				}
			}
		} else if (Strings.isNotBlank(condition) && "choice".equals(condition)) {
			for (int i = 0; i < list.size(); i++) {
				DocTypeVO docTypeVo=new DocTypeVO();
				DocType docType=list.get(i);
				// 如果与所输入的名称匹配
				if(docType.getStatus() != 2) {
					docTypeVo.setDocType(docType);
					if(docType.getIsSystem()==true){
						docTypeVo.setTheDocType("doctype.system");
					}else{
						docTypeVo.setTheDocType("doctype.customer");
					}
					docTypeVo.setCreatedByCurrentAccount(docType.getDomainId().longValue() == accountId);
					docTypeVo.setUsed(docType.getStatus() == Constants.CONTENT_TYPE_PUBLISHED);
					if (list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
					     try {
							  account = orgManager.getAccountById(list.get(i).getDomainId());
							  docTypeVo.setOrgName(account.getShortname());
				          } catch (BusinessException e) {
						      log.error("orgManager取得member", e);
					      }
						}
					the_list.add(docTypeVo);
				}
			}
		}
		
		// 此处为对分页做的处理
		for(int i=first;i<first+pageSize;i++){
			if(i>the_list.size()-1)
				break;
			result_list.add(the_list.get(i));
		}
		
		Pagination.setRowCount(the_list.size());
		// 保存结果集
		view.addObject("docTypes", result_list);
		
		// 单位管理员是否显示RSS页签
		boolean showRssTagOnAccountAdmin = Constants.showRssTagOnAccountAdmin();
		view.addObject("showRssTagOnAccountAdmin", showRssTagOnAccountAdmin);
		boolean rssEnabled = Constants.rssModuleEnabled();
		view.addObject("rssEnabled", rssEnabled);
				
		// 当前是否集团管理员登录
		boolean isGroupAdmin = Constants.isGroupAdmin();
		view.addObject("isGroupAdmin", isGroupAdmin);
		
		return view ;
	}
	
	//进入添加文档类型界面
	public ModelAndView addDocTypePage(HttpServletRequest request, 
			HttpServletResponse response)  {
		ModelAndView mav = new ModelAndView("doc/docType/addDocType");
		return mav;
	}   
	
	//添加一个文档类型
	public ModelAndView addDocType(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		String name=request.getParameter("theName");
		String desc=request.getParameter("description");
		String addString=request.getParameter("addString");//类似于  1,false:2,true:3,false:4,true
		Byte parentType = Byte.valueOf(request.getParameter("parentType"));
		long formId = NumberUtils.toLong(request.getParameter("formId"));
		// 验证名称是否重复 
		boolean flag = contentTypeManager.containDocType(name);
		if (flag) {
			super.printV3XJS(response.getWriter());
			super.rendJavaScript(response, 
					"alert(parent.v3x.getMessage('DocLang.doc_content_type_name_used'));" +
					"try{getA8Top().endProc();}catch(e){}" +
					"parent.document.getElementById('b1').disabled=false;" +
					"parent.document.getElementById('b2').disabled=false;");
			return null;
		}
		
		if(name.matches("\\d+")){
		    super.printV3XJS(response.getWriter());
            super.rendJavaScript(response, 
                    "alert(parent.v3x.getMessage('DocLang.doc_content_type_name_not_all_number'));" +
                    "try{getA8Top().endProc();}catch(e){}" +
                    "parent.document.getElementById('b1').disabled=false;" +
                    "parent.document.getElementById('b2').disabled=false;");
            return null;
		}
		
		
		StringTokenizer token = new StringTokenizer(addString,":");
		List<DocTypeDetail> the_list = new ArrayList<DocTypeDetail>();
		DocType docType = new DocType();
		docType.setIdIfNew();			
		docType.setDescription(desc);
		docType.setName(name);
		docType.setParentType(parentType);
		docType.setFormDefinitionId(formId);
		docType.setEditable(true);
		docType.setIsSystem(false);				
		docType.setStatus(Constants.CONTENT_TYPE_DRAFT);//编辑状态
		long domainId = CurrentUser.get().getLoginAccount();
		docType.setDomainId(domainId);
		
		int number=0;		//设置顺序   传过来的串是按顺序组成的串
		while(token.hasMoreTokens()) {
			StringTokenizer the_token = new StringTokenizer(token.nextToken(),",");
			long metadataId = Long.valueOf(the_token.nextToken());		//元数据的id
			if(metadataDefDao.get(metadataId)!=null){
				DocMetadataDefinition define = metadataDefManager.getMetadataDefById(metadataId);
				String bool = the_token.nextToken();							//对应的该元数据是否可编辑
				String nullable = the_token.nextToken();
				DocTypeDetail detail = new DocTypeDetail();
				detail.setMetadataDefId(metadataId);
				detail.setDescription(define.getDescription());
				detail.setName(define.getName());
				detail.setOrderNum(number);
				if(bool.equals("true")){
					detail.setReadOnly(true);
				}else{
					detail.setReadOnly(false);
				}
				detail.setNullable("true".equals(nullable));
				
				// 自定义元数据 detail.name  从def获得
				detail.setName(define.getName());
				
				number=number+1;		//顺序加一
				the_list.add(detail);
			}
		}
		contentTypeManager.addContentType(docType,the_list);
		super.rendJavaScript(response, "parent.parent.location.reload(true);");
		return null;
	}
	
	//进入修改文档类型界面
	public ModelAndView editDocTypePage(HttpServletRequest request, HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/docType/editDocType");
		long docTypeId = Long.valueOf(request.getParameter("docTypeId"));
		DocType docType = contentTypeManager.getContentTypeById(docTypeId);
		List<DocTypeDetail> details = contentTypeManager.getContentTypeDetails(docTypeId);
//		List<DocMetadataDefinition> defaultMeta=metadataDefManager.findDefaultMetadataDef();		//默认得元数据定义
//		allDefinition.removeAll(defaultMeta);
		List<MetadataMenu> list = new ArrayList<MetadataMenu>();
		if(details != null && !details.isEmpty()){
			for(int i = 0; i < details.size(); i++) {
				MetadataMenu menu = new MetadataMenu();
				DocTypeDetail detail = details.get(i);
				if (detail != null) {
					DocMetadataDefinition metadataDef = detail.getDocMetadataDefinition();
					menu.setMetadataDef(metadataDef);
					menu.setKey(Constants.getKeyByType(metadataDef.getType())); 	
					menu.setReadOnly(detail.getReadOnly());
					menu.setDetail(detail);
					list.add(menu);
				}
			}
		}
		modelView.addObject("docType", docType);
		modelView.addObject("isModify", true);
		modelView.addObject("MetadataMenu", list);
		modelView.addObject("details", details);
		return modelView;
	}
	
	//修改文档类型
	public ModelAndView updateDocType(HttpServletRequest request, 
						HttpServletResponse response)  {
		PrintWriter out = null;
		boolean newName = false;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error("取得输出流", e);
		}
//		try {
			String name = request.getParameter("theName");
			String description = request.getParameter("description");
			String status = request.getParameter("status");
			String seartchEnable = request.getParameter("seartchEnable");  //查询标记
			String addString = request.getParameter("addString");//类似于  1,false:2,true:3,false:4,true
			long docTypeId = Long.valueOf(request.getParameter("docTypeId"));
			
			
			DocType docType = contentTypeManager.getContentTypeById(docTypeId);
			String oldName = docType.getName();
		
			//更新DocType对象
			if(name != null && !name.equals("")){
				if(!name.equals(oldName)){	//名称发生改变					
					boolean flag = contentTypeManager.containDocType(name, docTypeId);
					newName = true;
					if (flag) {
//						throw new Exception("DocLang.doc_content_type_name_used");
						super.printV3XJS(out);
						out.print("<script>");
						out.print("alert(parent.v3x.getMessage('DocLang.doc_content_type_name_used'));");
						out.println("try{getA8Top().endProc();}catch(e){}");
						out.println("parent.document.getElementById('b1').disabled=false;");
						out.println("parent.document.getElementById('b2').disabled=false;");
						out.print("</script>");
						
						return null;
					}
				}
				docType.setName(name);
			}
			if(name != null && name.matches("\\d+")){
	            try {
                    super.printV3XJS(response.getWriter());
                    super.rendJavaScript(response, 
                            "alert(parent.v3x.getMessage('DocLang.doc_content_type_name_not_all_number'));" +
                            "try{getA8Top().endProc();}catch(e){}" +
                            "parent.document.getElementById('b1').disabled=false;" +
                            "parent.document.getElementById('b2').disabled=false;");
                } catch (IOException e) {
                    e.printStackTrace();
                }
	            return null;
	        }
			
			docType.setDescription(description);
			if(status!=null && !status.equals("")){
				docType.setStatus(Byte.valueOf(status));
				if(Integer.valueOf(status)==1){
					String seartchStatus = "0";
				    docType.setSeartchStatus(Byte.valueOf(seartchStatus));
				
				}
                //删除组by Yongzhang 2008-6-11
				
				if(Integer.valueOf(status)==Constants.CONTENT_TYPE_DELETED){
				
					docLibManager.deleteDocTypeListByTypeId(docType.getId());
                   if(seartchEnable!=null && seartchEnable.equals("true")){
                	String seartchStatus = "1";
                	docType.setSeartchStatus(Byte.valueOf(seartchStatus));
                   }else docType.setSeartchStatus(Byte.valueOf("0"));
				}
			}
			//更新docDetail对象集合
			Set<DocTypeDetail> set=docType.getDocTypeDetail();
			set.clear();		//清除原来对应得元数据详细定义
//			List<DocMetadataDefinition> list=metadataDefManager.findDefaultMetadataDef();
			StringTokenizer token = new StringTokenizer(addString,":");
			int number = 0;
			while(token.hasMoreTokens()) {
				StringTokenizer the_token = new StringTokenizer(token.nextToken(),",");
				long metadataId = Long.valueOf(the_token.nextToken());		//获取DocMetadataDefinition 得ID
				DocMetadataDefinition definition = metadataDefManager.getMetadataDefById(metadataId);
				String bool = the_token.nextToken();							//是否为只读得标记
				String nullable = the_token.nextToken();
				DocTypeDetail detail = new DocTypeDetail();
				detail.setIdIfNew();
				detail.setDescription(definition.getDescription());
				detail.setContentTypeId(docTypeId);
				detail.setMetadataDefId(metadataId);
				detail.setName(definition.getName());
				detail.setOrderNum(number);
				if(bool.equals("true")){
					detail.setReadOnly(true);
				}
				else{
					detail.setReadOnly(false);
				}
				detail.setNullable("true".equals(nullable));
				// 自定义元数据 detail.name  从def获得
				detail.setName(definition.getName());
				
				number=number+1;		//顺序号
				set.add(detail);
			}
			
//			for(int i=0;i<list.size();i++){
//				DocMetadataDefinition define=list.get(i);
//				DocTypeDetail detail=new DocTypeDetail();
//				detail.setIdIfNew();
//				detail.setDescription(define.getDescription());
//				detail.setContentTypeId(docTypeId);
//				detail.setMetadataDefId(define.getId());
//				detail.setName(define.getName());
////				detail.setReadOnly(define.getReadOnly());
//				set.add(detail);
//			}
		    docType.setDocTypeDetail(set);
			contentTypeManager.updateContentType(docType ,newName, oldName);
			
			out.print("<script>");
			out.print("parent.parent.location.reload(true)");
			out.print("</script>");
//		} catch (Exception e) {
//			out.print("<script>");
//			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'))");
//			out.print("</script>");
//		}
		
		return null;
	}
	
	//	删除文档类型
	public ModelAndView deleteDocType(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		List<Long> docTypeIds = FormBizConfigUtils.parseStr2Ids(request.getParameter("theIds"));
		if(CollectionUtils.isNotEmpty(docTypeIds)) {
			for(Long dtId : docTypeIds) {
				DocType dr = contentTypeManager.getContentTypeById(dtId);
				if(dr.getStatus() != Constants.CONTENT_TYPE_DRAFT) {
					super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.docType_used_not_allow_delete'));" +
												   "parent.location.reload(true);");
					return null;
				} else {
					docLibManager.deleteDocTypeListByTypeId(dr.getId());
					contentTypeManager.deleteContentType(dr.getId());
				}
				
			}
		}
		super.rendJavaScript(response, "parent.location.reload(true);");
		return null;
	}
	
	/**
	 * 添加文档属性
	 */
	public ModelAndView selectDocProperties(HttpServletRequest request, 
			HttpServletResponse response)  {
		ModelAndView mav = new ModelAndView("doc/docType/selectDocProperties");
		List<String> metaCategory = metadataDefManager.findMetadataDefGroup();
		mav.addObject("metaCategory", metaCategory);
		return mav;
	}	
	
	// 文档属性列表
	public ModelAndView listDocProperties(HttpServletRequest request, 
			HttpServletResponse response)  {		
		ModelAndView mav = new ModelAndView("doc/docType/listDocProperties");;		
		String category = request.getParameter("category");					
		List<DocMetadataDefinition> list = new ArrayList<DocMetadataDefinition>();
		List<DocMetadataDefinition> list2 = new ArrayList<DocMetadataDefinition>();
		if(category == null || category.equals("")) {
			list2 = metadataDefManager.getUsableExtMetadataDefs();	
		}
		else {
			list2 = metadataDefManager.getUsableExtMetadataDefsByGroup(category);
		}
		String attachcfi = this.systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
		if(!attachcfi.equals("enable")&&attachcfi!=null){
				for(DocMetadataDefinition mt: list2){
					if(!mt.getCategory().equals("metadataDef.category.edoc")) list.add(mt);    //如果公文类型的元数据，不加载 category	"metadataDef.category.edoc"	
				}	
			} else list.addAll(list2);
		
		List<MetadataMenu> the_list = new ArrayList<MetadataMenu>();
//		Integer first = Pagination.getFirstResult();
//		Integer pageSize = Pagination.getMaxResults();
//		Pagination.setRowCount(list.size());
//		for(int i = first; i < first + pageSize; i++) {
		// 页面没有做分页
		for(int i = 0; i < list.size(); i++){
//			if(i > list.size() - 1) {
//				break;
//			}
			MetadataMenu menu=new MetadataMenu();
			menu.setMetadataDef(list.get(i));
			menu.setKey(Constants.getKeyByType(list.get(i).getType()));
			the_list.add(menu);
		}
		
		mav.addObject("metadataList", the_list);
		return mav;
	}
	
	/*------------------------------------ 文档类型管理 End ---------------------------------------*/
	
	
	/*------------------------------------ 文档属性管理 Start ---------------------------------------*/
	// 框架
	public ModelAndView docPropertyIndex(HttpServletRequest request, 
								HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/docProperty/index");
		return modelView;
	}
	// 列表	
	public ModelAndView docPropertyTopFrame(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView mav = new ModelAndView("doc/docProperty/main");
		
		//查询所有的数据类型
		List<Byte> _list=Constants.getAllType();
		List<MetadataDefVO> metadata_type = new ArrayList<MetadataDefVO>();
		for(int i=0;i<_list.size();i++){
			MetadataDefVO vo =new MetadataDefVO();
			byte type =_list.get(i);
			String key=Constants.getKeyByType(type);
			vo.setKey(key);				//国际化KEY
			vo.setValue(type);			//类型值
			metadata_type.add(vo);			//所有得元数据类型
		}
		mav.addObject("metadata_type", metadata_type);
		String category = request.getParameter("category");					
		List<DocMetadataDefinition> list2 = new ArrayList<DocMetadataDefinition>();
		List<DocMetadataDefinition> list = new ArrayList<DocMetadataDefinition>();
		if(category == null || category.equals("")) {
			list2 = metadataDefManager.getUsableExtMetadataDefs();	
		}
		else {
			list2 = metadataDefManager.getUsableExtMetadataDefsByGroup(category);
		}
		String attachcfi = this.systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
		if(!attachcfi.equals("enable")&&attachcfi!=null){
			for(DocMetadataDefinition mt: list2){
				if(!mt.getCategory().equals("metadataDef.category.edoc")) list.add(mt);    //如果公文类型的元数据，不加载 category	"metadataDef.category.edoc"	
			}	
		} else list.addAll(list2);
		List<MetadataMenu> the_list=new ArrayList<MetadataMenu>();
		
		V3xOrgAccount account = null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		for(int i=first;i<first+pageSize;i++){
			if(i>list.size()-1){
				break;
			}
			MetadataMenu menu=new MetadataMenu();
			menu.setMetadataDef(list.get(i));
			if(list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
		     try {
			  account = orgManager.getAccountById(list.get(i).getDomainId());
			  menu.setOrgName(account.getShortname());
	        	}catch (BusinessException e) {
			       log.error("orgManager取得member", e);
		         }
			}
			
			menu.setKey(Constants.getKeyByType(list.get(i).getType()));
			the_list.add(menu);
		}

		List<String> metaCategory = metadataDefManager.findMetadataDefGroup();
		if(!attachcfi.equals("enable")&&attachcfi!=null)	metaCategory.remove("metadataDef.category.edoc");
		mav.addObject("metaCategory", metaCategory);
		
		mav.addObject("metadataList", the_list);
		
		// 单位管理员是否显示RSS页签
		boolean showRssTagOnAccountAdmin = Constants.showRssTagOnAccountAdmin();
		mav.addObject("showRssTagOnAccountAdmin", showRssTagOnAccountAdmin);
		boolean rssEnabled = Constants.rssModuleEnabled();
		mav.addObject("rssEnabled", rssEnabled);
		
		// 当前是否集团管理员登录
		boolean isGroupAdmin = Constants.isGroupAdmin();
		mav.addObject("isGroupAdmin", isGroupAdmin);
		
		return mav;
	}
	
	/**
	 * wuwl:集团管理员文档中心，文档属性条件查询
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryDocPropertyByCondition(HttpServletRequest request,
			HttpServletResponse response)  {
		
		ModelAndView mav = new ModelAndView("doc/docProperty/main");
		
		// 初始化查询条件：数据类型
		List<Byte> _list=Constants.getAllType();
		List<MetadataDefVO> metadata_type = new ArrayList<MetadataDefVO>();
		for(int i=0;i<_list.size();i++){
			MetadataDefVO vo =new MetadataDefVO();
			byte type =_list.get(i);
			String key=Constants.getKeyByType(type);
			vo.setKey(key);				//国际化KEY
			vo.setValue(type);			//类型值
			metadata_type.add(vo);		//所有得元数据类型
		}
		
		List<DocMetadataDefinition> list2 = new ArrayList<DocMetadataDefinition>();
		List<DocMetadataDefinition> list = new ArrayList<DocMetadataDefinition>();
		List<MetadataMenu> the_list=new ArrayList<MetadataMenu>();
		String attachcfi = null ;
		String condition = request.getParameter("condition") == null ? "" : request.getParameter("condition") ;
		
		list2 = metadataDefManager.getUsableExtMetadataDefs(); // 取得所有元数据
		
		// 1.按类别查询
		if ("category".equals(condition)) {
			String category = request.getParameter("category") ;
			mav.addObject("category", category);
			
			list2 = metadataDefManager.getUsableExtMetadataDefsByGroup(category);
			attachcfi = this.systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
			
			if (!attachcfi.equals("enable") && attachcfi!=null) {
				for (DocMetadataDefinition mt: list2) {
					if(!mt.getCategory().equals("metadataDef.category.edoc")) 
						list.add(mt);    // 如果公文类型的元数据，不加载 category	"metadataDef.category.edoc"	
				}	
			} else {
				list.addAll(list2);
			}
		}
		// 2.按类型查询
		else if ("type".equals(condition)) {
			String type = request.getParameter("type") ;
			mav.addObject("type", type);
			boolean flag = false ;
			if ("system".equals(type))
				flag = true ;
			else if ("self".equals(type))
				flag = false;
			for (DocMetadataDefinition dmf : list2) {
				if (flag == dmf.getIsSystem())
					list.add(dmf);
			}
		}
		// 3.按名称查询
		else if ("name".equals(condition)) {
			String name = request.getParameter("name") ;
			mav.addObject("name", name) ;
			for (DocMetadataDefinition dmf : list2) {
				if (dmf.getIsSystem()) {
					if (this.getPropertiesValue(dmf.getName()).contains(name))
						list.add(dmf) ;
				} else {
					if (dmf.getName().contains(name)){
						list.add(dmf);
					}
				}
			}
		}
		// 4.按数据类型查询
		else if ("dataType".equals(condition)) {
			String dataType = request.getParameter("dataType") ;
			mav.addObject("dataType", dataType);
			for (DocMetadataDefinition dmf : list2) {
				if (Integer.parseInt(dataType) == dmf.getType()) {
					list.add(dmf);
				}
			}
		}
		// 5.无条件查询
		else {
			list = list2;
		}
		// 以下为可重用代码,所以在此抽取出来
		V3xOrgAccount account = null;
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		
		for (int i = first ; i < first+pageSize ; i ++ ) {
			if(i>list.size()-1)
				break;
			MetadataMenu menu=new MetadataMenu();
			menu.setMetadataDef(list.get(i));
			if(list.get(i).getDomainId()!=null&&list.get(i).getDomainId()!=0){
			    try {
			    	account = orgManager.getAccountById(list.get(i).getDomainId());
					menu.setOrgName(account.getShortname());
		        }catch (BusinessException e) {
				     log.error("orgManager取得member", e);
			    }
			}
			menu.setKey(Constants.getKeyByType(list.get(i).getType()));
			the_list.add(menu);
		}
		
		List<String> metaCategory = metadataDefManager.findMetadataDefGroup();
		if(attachcfi != null && !attachcfi.equals("enable"))	
			metaCategory.remove("metadataDef.category.edoc");
		mav.addObject("metaCategory", metaCategory);
		mav.addObject("metadataList", the_list);
		mav.addObject("metadata_type", metadata_type);
		mav.addObject("condition", condition);
		
		// 单位管理员是否显示RSS页签
		boolean showRssTagOnAccountAdmin = Constants.showRssTagOnAccountAdmin();
		mav.addObject("showRssTagOnAccountAdmin", showRssTagOnAccountAdmin);
		boolean rssEnabled = Constants.rssModuleEnabled();
		mav.addObject("rssEnabled", rssEnabled);
		
		// 当前是否集团管理员登录
		boolean isGroupAdmin = Constants.isGroupAdmin();
		mav.addObject("isGroupAdmin", isGroupAdmin);
		return mav;
	}
	
	public String getPropertiesValue(String key) {
		if ("project.body.responsible.label".equals(key)) 
			return ResourceBundleUtil.getString("com.seeyon.v3x.project.resources.i18n.ProjectResources", key);
		
		else if ("mt.mtMeeting.emceeId".equals(key) || "mt.mtMeeting.recorderId".equals(key)) 
			return ResourceBundleUtil.getString("com.seeyon.v3x.meeting.resources.i18n.MeetingResources", key);
		
		else if ("inquiry.send.department.label".equals(key))
			return ResourceBundleUtil.getString("com.seeyon.v3x.inquiry.resources.i18n.InquiryResources", key);
		
		else if ("common.subject.label".equals(key) || "common.date.senddate.label".equals(key) || "common.date.enddate.label".equals(key)
				|| "common.sender.label".equals(key)) 
			return ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", key);
		
		else if ("doc.metadata.def.appType".equals(key) || "doc.metadata.def.edoc.year".equals(key) || "doc.metadata.def.edoc.volume".equals(key))
			return ResourceBundleUtil.getString("com.seeyon.v3x.doc.resources.i18n.DocResource", key);
		
		else if ("edoc.element.doctype".equals(key) || "edoc.element.sendtype".equals(key) || "edoc.element.wordno.label".equals(key)
				|| "edoc.element.wordinno.label".equals(key) || "edoc.element.secretlevel".equals(key) || "edoc.element.urgentlevel".equals(key)
				|| "edoc.element.keepperiod".equals(key) || "edoc.element.sendunit".equals(key) || "edoc.element.issuer".equals(key)
				|| "edoc.element.sendingdate".equals(key) || "edoc.element.sendtounit".equals(key) || "edoc.element.copytounit".equals(key)
				|| "edoc.element.copy.reportunit".equals(key) || "edoc.element.keyword".equals(key) || "edoc.element.printedunit".equals(key)
				|| "edoc.element.copies".equals(key) || "edoc.element.printer".equals(key) || "edoc.element.author".equals(key))
			return ResourceBundleUtil.getString("com.seeyon.v3x.edoc.resources.i18n.EdocResource", key) ;
		return "" ;
	}
	
	// 进入新建文档属性界面
	public ModelAndView addDocPropertyPage(HttpServletRequest request, HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/docProperty/addDocProperty");
		//查询出所有的类别
		List<String> list = metadataDefManager.findMetadataDefGroup();
		//查询所有的类型
		List<Byte> _list=Constants.getAllType();
		List<MetadataDefVO> the_list=new ArrayList<MetadataDefVO>();
		for(int i=0;i<_list.size();i++){
			MetadataDefVO vo =new MetadataDefVO();
			byte type =_list.get(i);
			String key=Constants.getKeyByType(type);
			vo.setKey(key);				//国际化KEY
			vo.setValue(type);			//类型值
			the_list.add(vo);			//所有得元数据类型
		}
		modelView.addObject("category", list);
		modelView.addObject("metadata_type", the_list);
		return modelView;
	}
	
	// 新建一个文档属性
	public ModelAndView addDocProperty(HttpServletRequest request, HttpServletResponse response) throws IOException  {
		String tempName = request.getParameter("theName").trim(); // 名称
		byte type = Byte.parseByte(request.getParameter("meta_type")); // 类型
		String desc = request.getParameter("description"); // 描述	
		
		// 验证名称是否重复 
		boolean flag = metadataDefManager.containMetadataDef(tempName);
		if (flag) {
			super.printV3XJS(response.getWriter());
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_property_name_used'));" +
					"try{getA8Top().endProc();}catch(e){}" +
					"parent.document.getElementById('b1').disabled=false;" +
					"parent.document.getElementById('b2').disabled=false;");
			return null;
		}
		
		List<DocMetadataOption> metadataOptions = null;
		
		DocMetadataDefinition metadataDef = new DocMetadataDefinition();
		metadataDef.setIdIfNew();
		metadataDef.setName(tempName);
		metadataDef.setType(type);
		metadataDef.setDescription(desc);
		metadataDef.setIsDefault(false);
		metadataDef.setIsHidden(false);
		metadataDef.setIsSystem(false);
		metadataDef.setLength(0);
		metadataDef.setScopeMaxValue(null);
		metadataDef.setScopeMinValue(null);
		metadataDef.setNullable(true);
		metadataDef.setSearchable(NumberUtils.toInt(request.getParameter("searchable")) == 1);
		
		// 设置文档属性类别
		String categoryName = "";
		int newCategory = Integer.parseInt(request.getParameter("newCategory"));
		if (newCategory == 1) {
			categoryName = request.getParameter("category_name");
		}
		else {
			categoryName = request.getParameter("meta_category");
		}
		metadataDef.setCategory(categoryName);
			
		// 设置默认值
		String defaultValue = request.getParameter("defaultValue");
		if (type == Constants.BOOLEAN) {
			String s = request.getParameter("yesOrNo_default");
			if (s != null && s.equals("1")) {
				defaultValue = "true";
			}
			else {
				defaultValue = "false";
			}			
		}
		else if (type == Constants.DATE || type == Constants.DATETIME) {
			defaultValue = request.getParameter("nowTime");
		}else if(type == Constants.INTEGER){
			defaultValue = request.getParameter("defaultValue_int_input");
		}else if(type == Constants.FLOAT){
			float value = NumberUtils.toFloat(request.getParameter("defaultValue_decimal_input"));
			defaultValue = String.valueOf(value);
		}
		metadataDef.setDefaultValue(defaultValue);
		
		// 设置是否为百分比
		boolean isPercent = false;			//是否是百分比
		if (type == Constants.INTEGER || type == Constants.FLOAT) {
			String s1 = request.getParameter("isPercent");
			if (s1 != null && s1.equals("1")) {
				isPercent = true;
			}
		}
		metadataDef.setIsPercent(isPercent);
		
		long domainId = CurrentUser.get().getLoginAccount();
		metadataDef.setDomainId(domainId);
		
		// 设置枚举选项
		if (type == Constants.ENUM) {
			metadataOptions = new ArrayList<DocMetadataOption>();
			String options = request.getParameter("theContent");
			StringTokenizer st = new StringTokenizer(options, ",");
			while (st.hasMoreTokens()) {
				DocMetadataOption option = new DocMetadataOption();
				option.setIdIfNew();
				option.setMetadataDef(metadataDef);
				option.setOptionItem(st.nextToken());
				metadataOptions.add(option);
			}			
		}
		
		metadataDefManager.addMetadataDef(metadataDef, metadataOptions);		
		
		super.rendJavaScript(response, "parent.parent.location.reload(true);");
		return null;
	}
	
	// 进入元数据修改页面
	public ModelAndView editDocPropertyPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("doc/docProperty/editDocProperty");
		String id=request.getParameter("theId");
		DocMetadataDefinition define=metadataDefManager.getMetadataDefById(Long.valueOf(id));	//获取要修改的元数据对象
		//查询所有的类别
		List<String> list = metadataDefManager.findMetadataDefGroup();
		
		if(define.getType() == Constants.ENUM) {
			String str = "";
			Set set=define.getMetadataOption();			//取得枚举值
			List<String> listMenu=new ArrayList<String>();		//保存取得得枚举
			if(set != null){
				Iterator it=set.iterator();
				while(it.hasNext()){
					DocMetadataOption info=(DocMetadataOption)it.next();
					listMenu.add(info.getOptionItem());
					if(it.hasNext()){
						str+=info.getOptionItem();
						str+=",";
					}else{
						str+=info.getOptionItem();
					}
				}
			}
			modelView.addObject("listEnum", listMenu);
			modelView.addObject("content", str);
		}
		
		String name = define.getName();
		name = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, name);
		if(name.equals(define.getName())){
			String resName = Constants.getResourceNameOfMetadata(name, "");
			if(!"".equals(resName))
				name = ResourceBundleUtil.getString(resName, name);				
		}
		
		
		modelView.addObject("category", list);
		modelView.addObject("metadataType", Constants.getKeyByType(define.getType()));
		modelView.addObject("metadata", define);
		modelView.addObject("name", name);
		
		return modelView;
	}
	
	//	修改文档属性
	public ModelAndView updateDocProperty(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long metadataId = Long.valueOf(request.getParameter("metadataId"));
		String tempName = request.getParameter("theName"); // 名称
        String name = tempName.replace(" ", "");	
		String desc = request.getParameter("description");	//描述
		
		List<DocMetadataOption> metadataOptions = null;
		// 验证名称是否重复 
		boolean flag = metadataDefManager.containMetadataDef(name, metadataId);
		if (flag) {
			super.printV3XJS(response.getWriter());
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('DocLang.doc_property_name_used'));" +
					"try{getA8Top().endProc();}catch(e){}" +
					"parent.document.getElementById('b1').disabled=false;" +
					"parent.document.getElementById('b2').disabled=false;");
			return null;
		}
		DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(metadataId);
		byte type = metadataDef.getType();
		boolean isSystem = metadataDef.getIsSystem();
		if(!isSystem) {			
			metadataDef.setName(name);
		}
		metadataDef.setDescription(desc);
		metadataDef.setNullable(true);
		metadataDef.setSearchable(NumberUtils.toInt(request.getParameter("searchable")) == 1);
		
		// 设置文档属性类别
		String categoryName = "";
		int newCategory = Integer.parseInt(request.getParameter("newCategory"));
		if (newCategory == 1) {
			categoryName = request.getParameter("category_name");
		}
		else {
			categoryName = request.getParameter("meta_category");
		}
		metadataDef.setCategory(categoryName);
		
		// 设置默认值
		String defaultValue = request.getParameter("defaultValue");
		if (type == Constants.BOOLEAN) {
			String s = request.getParameter("yesOrNo_default");
			if (s != null && s.equals("1")) {
				defaultValue = "true";
			}
			else {
				defaultValue = "false";
			}			
		}
		else if (type == Constants.DATE || type == Constants.DATETIME) {
			defaultValue = request.getParameter("nowTime");
		}else if(type == Constants.INTEGER){
			defaultValue = request.getParameter("defaultValue_int");
		}else if(type == Constants.FLOAT){
			float value = NumberUtils.toFloat(request.getParameter("defaultValue_decimal"));
			defaultValue = String.valueOf(value);
		}
		
		metadataDef.setDefaultValue(defaultValue);
		
		// 设置是否为百分比
		boolean isPercent = false;			//是否是百分比
		if (type == Constants.INTEGER || type == Constants.FLOAT) {
			String s1 = request.getParameter("isPercent");
			if (s1 != null && s1.equals("1")) {
				isPercent = true;
			}
		}
		metadataDef.setIsPercent(isPercent);

		// 设置枚举选项
		if(type == Constants.ENUM){		//枚举类型
			metadataOptions = new ArrayList<DocMetadataOption>();
			String options = request.getParameter("theContent");
			StringTokenizer st = new StringTokenizer(options, ",");
			while (st.hasMoreTokens()) {
				DocMetadataOption option = new DocMetadataOption();
				option.setIdIfNew();
				option.setMetadataDef(metadataDef);
				option.setOptionItem(st.nextToken());
				metadataOptions.add(option);
			}	
		}
		
		metadataDefManager.updateMetadataDef(metadataDef, metadataOptions);
		
		super.rendJavaScript(response, "parent.parent.document.location.reload(true);");
		return null;
	}
	
	// 删除文档属性
	public ModelAndView deleteDocProperty(HttpServletRequest request, 
			HttpServletResponse response)  {
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			log.error("取得输出流", e1);
		}
		String deleteId=request.getParameter("deleteId");
		StringTokenizer token=new StringTokenizer(deleteId,",");
		while(token.hasMoreTokens()){
			try {
				long theId=Long.valueOf(token.nextToken());
				// 关联删除
				docLibManager.deleteSpecificColumn(theId);
				docLibManager.deleteSpecificSearchConfig(theId);
				metadataDefManager.deleteMetadataDef(theId);
				out.print("<script>");
				out.print("parent.parent.location.reload(true);");
				out.print("</script>");
			} 
			catch (Exception e) {
				log.error("删除文档属性", e);
				out.print("<script>");
				out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'));");
				out.print("</script>");
			}
		}
		return null;
	}	
		
	/*------------------------------------ 文档属性管理 End ---------------------------------------*/
	
	//该方法仅用于测试之用 初始化系统数据
	public ModelAndView ownerDocLib(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView modelView = new ModelAndView("doc/manager/organization");
		return modelView;
	}
	
	//该方法用于测试之用
	public ModelAndView setOwnerDocLib(HttpServletRequest request,
			HttpServletResponse response)  {
		ModelAndView modelView=this.ownerDocLib(request, response);
		String flag = request.getParameter("flag");
		if(flag == null){
			String members=request.getParameter("members");
			StringTokenizer token=new StringTokenizer(members,",");
			while(token.hasMoreTokens()){
				long userId=Long.valueOf(token.nextToken());
				try {
					docLibManager.addDocLib(userId);
				} catch (DocException e) {
					log.error("初始化个人库", e);
				}
			}
		}else{
			String accounts=request.getParameter("accounts");
			StringTokenizer token=new StringTokenizer(accounts,",");
			while(token.hasMoreTokens()){
				long account=Long.valueOf(token.nextToken());
				try {
					docLibManager.addSysDocLibs(account);
				} catch (DocException e) {
					log.error("初始化个人库", e);
				}
			}
		}
		
		return modelView;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response)  {
		return null;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}	
	
}
		