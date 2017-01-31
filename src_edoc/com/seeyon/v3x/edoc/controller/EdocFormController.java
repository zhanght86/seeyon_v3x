package com.seeyon.v3x.edoc.controller;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.joinwork.message.api.NotifyMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.engine.infopath.InfoPath_xsl;
import www.seeyon.com.v3x.form.manager.define.data.DataDefineException;
import www.seeyon.com.v3x.form.manager.inf.IFormResoureProvider;
import www.seeyon.com.v3x.form.manager.resoureprovider.CabFileResourceProvider;
import www.seeyon.com.v3x.form.utils.CharReplace;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.ObjectToXMLUtil;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocForm;
import com.seeyon.v3x.edoc.domain.EdocFormAcl;
import com.seeyon.v3x.edoc.domain.EdocFormElement;
import com.seeyon.v3x.edoc.domain.EdocFormFlowPermBound;
import com.seeyon.v3x.edoc.domain.EdocFormExtendInfo;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocManager;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.manager.EdocSummaryManager;
import com.seeyon.v3x.edoc.util.DataUtil;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.edoc.util.XMLConverter;
import com.seeyon.v3x.edoc.webmodel.EdocFormModel;
import com.seeyon.v3x.edoc.webmodel.FormBoundPerm;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.manager.OrganizationManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class EdocFormController extends BaseController {
	
	private static final Log log = LogFactory.getLog(EdocFormController.class);
	
	private OrgManager orgManager;

	private OrganizationManager organizationManager;
	
	private EdocElementManager edocElementManager;
	
	private EdocFormManager edocFormManager;
	
	private EdocSummaryManager edocSummaryManager;
	
	private EdocManager edocManager;
	
	public EdocManager getEdocManager() {
		return edocManager;
	}

	public void setEdocManager(EdocManager edocManager) {
		this.edocManager = edocManager;
	}
	private XMLConverter XMLConverter;
	
	private AttachmentManager attachmentManager;
	
	private ConfigManager configManager;
	
	private FileManager fileManager;
	
	private FlowPermManager flowPermManager;
	
	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public OrganizationManager getOrganizationManager() {
		return organizationManager;
	}

	public void setOrganizationManager(OrganizationManager organizationManager) {
		this.organizationManager = organizationManager;
	}
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public XMLConverter getXMLConverter() {
		return XMLConverter;
	}

	public void setXMLConverter(XMLConverter converter) {
		XMLConverter = converter;
	}


	public EdocSummaryManager getEdocSummaryManager() {
		return edocSummaryManager;
	}

	public void setEdocSummaryManager(EdocSummaryManager edocSummaryManager) {
		this.edocSummaryManager = edocSummaryManager;
	}

	public EdocFormManager getEdocFormManager() {
		return edocFormManager;
	}

	public void setEdocFormManager(EdocFormManager edocFormManager) {
		this.edocFormManager = edocFormManager;
	}

	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}
	
	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}
	
	public OrgManager getOrgManager()
	{
		return orgManager;
	}
	
	public void setOrgManager(OrgManager orgManager)
	{
		this.orgManager = orgManager;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}	
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("edoc/formManage/form_list_main");
		if(request.getParameter("id")!=null)
			mav.addObject("id", request.getParameter("id"));
		return mav;
	}
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null);
		
		ModelAndView ret = new ModelAndView("edoc/formManage/form_list_iframe");
		List<EdocForm> list=null;
	    
		User user = CurrentUser.get();
		
		String expressionType = request.getParameter("expressionType");
		String expressionValue = request.getParameter("expressionValue"); 
		List<EdocForm> listForWeb = new ArrayList<EdocForm>();
		if(Strings.isNotBlank(expressionType) && Strings.isNotBlank(expressionValue)){
			//处理条件查询
			if("edocFormType".equals(expressionType)){//处理查询文单类型,默认显示启用状态的
				list = getEdocFormWebList(user,null);
				for(EdocForm temp : list){
					if(temp.getType() == Integer.parseInt(expressionValue) && temp.getStatus() == EdocForm.C_iStatus_Published.intValue())
						listForWeb.add(temp);
				}
				ret.addObject("list", pagenate(listForWeb));
			}else if("edocFormStatus".equals(expressionType)){//处理查询文单状态
				list = getEdocFormWebList(user,null);
				for(EdocForm temp : list){
					if(temp.getStatus() == Integer.parseInt(expressionValue))
						listForWeb.add(temp);
				}
				ret.addObject("list", pagenate(listForWeb));
			}else if("name".equals(expressionType)){//处理查询文单名称
				listForWeb = getEdocFormWebList(user, expressionValue);
				ret.addObject("list", listForWeb);
			}
			
		}else{
			list = getEdocFormWebList(user,null);
			for(EdocForm temp : list){
				if(temp.getStatus() == EdocForm.C_iStatus_Published.intValue()){
					listForWeb.add(temp);
				}
			}
			ret.addObject("list", pagenate(listForWeb));
		}
		/*
		List<EdocForm> accountList= edocFormManager.getAllEdocForms(V3xOrgEntity.VIRTUAL_ACCOUNT_ID); // 查出预置的公文单数据
		
		if(isGroupVer){//如果是集团版,那么就会有2种管理员角色，只有在单位管理员的界面中显示accountList
			if(user.isGroupAdmin()){//如果是单位管理员
				list.addAll(accountList);
			}
		}else{//如果是企业版,只有一种角色存在，那就是单位管理员，所以直接加上accountList
			list.addAll(accountList);//全部加上			
		}*/
		ret.addObject("isAccountAdmin",EdocRoleHelper.hasInputFunctionFromGroup());
		return ret;
	}

	private List<EdocForm> getEdocFormWebList(User user , String expressionValue) {
		List<EdocForm> list =new ArrayList<EdocForm>();
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());
		if(Strings.isNotBlank(expressionValue)){
			try{
				list = edocFormManager.getAllEdocFormsByName(user, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, expressionValue);
			}catch(Exception e){
				log.error("查找公文单定义列表异常",e);
			}
		}else {
			try{
				list = edocFormManager.getAllEdocFormsForWeb(user,V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			}catch(Exception e){
				log.error("查找公文单定义列表异常",e);
			}
		}
		//由于组织模型的数据都是放在内存中的，所以可以像下面这样循环查询
		for(EdocForm ef : list){
			Set<EdocFormAcl> acls = ef.getEdocFormAcls();
			String aclIds= "";
			if(acls != null){
				for(EdocFormAcl acl:acls){
					if(acl.getDomainId() != null){
						if("".equals(aclIds)) aclIds = acl.getDomainId().toString() ;
						else aclIds += ","+acl.getDomainId();
					}
				}
			}
			ef.setAclIds(aclIds);
			//查询制作单位的名称。
			String createDomainName ="";
			try {
				createDomainName = orgManager.getAccountById(ef.getDomainId()).getName();
			} catch (BusinessException e) {
				log.error("查询公文单制作单位名称异常",e);
			}
			ef.setDomainName(createDomainName);
		}
		return EdocUtil.convertExtendInfo2EdocForm(list, user.getLoginAccount());
	}	
	
	//显示系统公文单
	public ModelAndView listSystemForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("edoc/formManage/dispSystemForm");		
		return ret;
	}
	public ModelAndView listSystemFormIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//处理查询条件
		List<EdocForm> list=null;
	    
		User user = CurrentUser.get();
		list = edocFormManager.getAllEdocForms(orgManager.getRootAccount().getId());
		ModelAndView ret = new ModelAndView("edoc/formManage/dispSystemFormIframe");
		ret.addObject("list", pagenate(list));
		return ret;
	}
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("edoc/formManage/form_modify");
		mav.addObject("method_type", "change");
		EdocSummary edocSummary =null;
		EdocForm bean=null;
		String idStr=request.getParameter("id");
		edocSummary = edocSummaryManager.findById(Long.parseLong(idStr));

			
		bean=edocFormManager.getEdocForm(Long.parseLong(idStr));
		if(null == bean)return null;
		String aclIds = "";
		Set<EdocFormAcl> aclList =  bean.getEdocFormAcls();
		if(aclList != null){
			for(EdocFormAcl acl : aclList){
				if("".equals(aclIds)) aclIds = String.valueOf(acl.getDomainId());
				else aclIds += ","+acl.getDomainId();
			}
		}
		Set<EdocFormExtendInfo> infos = bean.getEdocFormExtendInfo();
		
		if(infos != null){
			for(EdocFormExtendInfo  info : infos){
				if(info.getAccountId() == user.getLoginAccount()){
					bean.setStatus(info.getStatus());
					bean.setIsDefault(info.getIsDefault());
					bean.setWebOpinionSet(info.getOptionFormatSet());
					mav.addObject("edocFormStatusId", info.getId());
				}
			}
		}	
		//是否是外单位授权给本单位使用的.
		mav.addObject("isOuterAccountAcl",!bean.getDomainId().equals(user.getLoginAccount()));
		mav.addObject("bean", bean);
		mav.addObject("aclIds", aclIds);
		
		mav.addObject("type", bean.getType());
		String str=edocFormManager.getEdocFormXmlData(Long.parseLong(idStr),edocSummary,1, bean.getType());//新增类型
		mav.addObject("xml", str);
		int i = str.indexOf("&&&&&&&&  data_start  &&&&&&&&");
		int j = str.indexOf("&&&&&&&&  input_start  &&&&&&&&");
		
		String str_a = str.substring(i+30, j);
		str_a = str_a.substring(str_a.indexOf(">")+1, str_a.length());
		
		String original_xml = com.seeyon.v3x.edoc.util.StringUtils.xmlElementToString(str_a);
		
		mav.addObject("original_xml", com.seeyon.v3x.util.Strings.toHTML(original_xml));
		Long fileId=bean.getFileId();
		if(fileId==null){fileId=0L;}
		V3XFile v3xfile=fileManager.getV3XFile(fileId);
		if(v3xfile!=null){
		mav.addObject("fileId",fileId);
		mav.addObject("fileName", v3xfile.getFilename());
		mav.addObject("createDate", new Timestamp(v3xfile.getCreateDate().getTime()).toString().substring(0, 10));

		//用于初始化该目录下的节点权限集合
		String category = "";
		if(null!=bean ){
			if(bean.getType().intValue() == EdocEnum.edocType.sendEdoc.ordinal()){
				category = MetadataNameEnum.edoc_send_permission_policy.name();
			}else if(bean.getType().intValue() == EdocEnum.edocType.recEdoc.ordinal()){
				category = MetadataNameEnum.edoc_rec_permission_policy.name();
			}else if(bean.getType().intValue() == EdocEnum.edocType.signReport.ordinal()){
				category = MetadataNameEnum.edoc_qianbao_permission_policy.name();
			}
		}
		
		List<FlowPerm> flowPermlist = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, user.getLoginAccount());
	
		mav.addObject("flowPermList", flowPermlist);
		
		//
		
		String[] urls= new String[1];
		if(null!=v3xfile){urls[0] = v3xfile.getId().toString();}
		String[] createDates=new String[1];
		if(null!=v3xfile){createDates[0] = Datetimes.formatDatetime(v3xfile.getCreateDate());}
		String[] mimeTypes=new String[1];
		if(null!=v3xfile){mimeTypes[0] = v3xfile.getMimeType().toString();}
		String[] names=new String[1]; 
		if(null!=v3xfile){names[0] = v3xfile.getFilename().toString();}
	
			//		System.out.println("Here  is begin index!"+urls.length+createDates.length);
		if(urls == null){
			return mav;
		}
		
			//对存储文件处理  生成.xsn文件，并返回存储路径
 
		
		String path = "";
		if(null!=urls && null!=createDates && null!=mimeTypes && null!=names){
			path = edocFormManager.getDirectory(urls, createDates, mimeTypes, names);//如果上述条件不为空，查找infopath物理文件
		}
		List<String> list = new ArrayList<String>();
		
		if(!"".equals(path)){
		String sampledata = "sampledata.xml";

		String view = "view1.xsl";
		
		String xml = "";
		String xsl = "";		
		
		ByteArrayInputStream fInfopathxsn=new ByteArrayInputStream(www.seeyon.com.v3x.form.utils.StringUtils.readFileData(path));
		
		IFormResoureProvider fResourceProvider;

		fResourceProvider = new CabFileResourceProvider(fInfopathxsn);

		xml = fResourceProvider.loadResource(sampledata);
		
		String f_xml = xml;
		
			  int a = xml.indexOf(">");
			  int c = xml.indexOf("</my:myFields>");
			  xml = xml.substring(a+1,c);
			  String xml_a = f_xml.substring(0,a+1);
			  			  
			  String[] strx = xml.split("/>");
			  String temp = "";
			  for(int m=0;m<strx.length-1;m++){
				  String str_w = strx[m];
				  if(str_w.contains("<my:")){//判断是否事以<my:开始,因为infopath2007与2003在样式上略又区别
				  int x  = str_w.indexOf(":");
				  str_w = str_w.substring(x+1, str_w.length());
				  //if(!str_w.startsWith("field")){
					  list.add(str_w);
					  temp += "<my:"+str_w+"></my:"+str_w+">"; 
				  	//}
				  }
			  }
		}else{
			/*
			Set<EdocFormElement> elementList = bean.getEdocFormElements();
			for(EdocFormElement ele : elementList){
				EdocElement element = edocElementManager.getEdocElementsById(ele.getElementId());
				list.add(element.getFieldName());
			}
			*/
			List<EdocFormElement> elementList2 = edocFormManager.getEdocFormElementByFormId(bean.getId());
			for(EdocFormElement ele : elementList2){
				EdocElement element = edocElementManager.getEdocElementsById(ele.getElementId());
				list.add(element.getFieldName());
			}			
		}
			  
		/*
		  Set set = new HashSet();
		  set.addAll(list);
		  
		  list.clear();
		  list.addAll(set);
		 */
		
			String operationStr = "";
			
			List<EdocFormFlowPermBound> boundPermList = edocFormManager.findBoundByFormIdAndDomainId(bean.getId(), user.getLoginAccount());
			
			if(null!=boundPermList){
			for(EdocFormFlowPermBound bound : boundPermList){
				operationStr += "(" + bound.getFlowPermName() + ")";
			}
			
			mav.addObject("operation_str", operationStr);
		  
		  
		//取得公文处理意见的名称列表
		List<FormBoundPerm> processList = EdocHelper.getProcessOpinionByEdocFormId(list, bean.getId(), bean.getType(),user.getLoginAccount());
		mav.addObject("processList", processList);		
		

		
		//用于提交选择参数
		String listStr = "";
		for(FormBoundPerm perm: processList){
			listStr += perm.getPermItem();
			listStr += ",";
		}
		if(!"".equals(listStr) && listStr.length() > 1){
			listStr = listStr.substring(0, listStr.length()-1);
		}
		mav.addObject("listStr", listStr);
		//
		}
		
		String logoURL = EdocHelper.getLogoURL();
		
		mav.addObject("logoURL", logoURL);		
		
		String content = bean.getContent();
		if(Strings.isBlank(content)){
			//mav.addObject("method_type", "add");
			ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
			String alertNote = ResourceBundleUtil.getString(r, "edoc.form.content.empty");
			response.getWriter().println("<script>alert('"+alertNote+"');</script>");
			return mav;
		}		
		
		byte[] tempByte_b = CharReplace.doReplace_Decode(content.getBytes("UTF-8"));
		content = new String(tempByte_b,"UTF-8");	
		
		mav.addObject("xsl", content);
		}else{
			log.error("单位管理员查看公文单出错，EdocFormController.edit查找v3xfile为空.公文单ID："+bean.getId()+" v3xFile的ID为："+fileId);
		}
			
		return mav;
	}
	
	public ModelAndView create(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/formManage/new_edoc_form");
		PrintWriter writer = response.getWriter();
		
		User user = CurrentUser.get();
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String description = request.getParameter("description"); 
		String status = request.getParameter("status");
		String content = request.getParameter("content");	
		String showLog = request.getParameter("showLog");
		String isDefault  = request.getParameter("isDefault");
		String grantedDomainId = request.getParameter("grantedDomainId");
		
		//判断是否有重名的公文单
		boolean bool = edocFormManager.checkHasName(name, Integer.valueOf(type));
		if(bool){
			writer.println("<script>");
			writer.println("alert(parent._('edocLang.edoc_form_duplicated'));");
			writer.println("self.history.back();");
			writer.println("</script>");
			mav.addObject("description", description);
			mav.addObject("type", type);
			mav.addObject("status", status);
			mav.addObject("content", content);
			mav.addObject("showLog", showLog);
			mav.addObject("name", name);
			mav.addObject("isDefault", isDefault);
			
			return mav;
		}
		
		if(null!=content && !"".equals(content)){	
			byte[] tempByte_a = CharReplace.doReplace_Encode(content.getBytes("UTF-8"));
			content = new String(tempByte_a , "UTF-8");
		}
		
		String element_id_list = request.getParameter("element_id_list");

		
		
		element_id_list = element_id_list.substring(0,element_id_list.length()-1);
		String[] eList = element_id_list.split(",");		
		
		
		
		List<Long> elementIdList = new ArrayList<Long>();
		for(int i=0;i<eList.length;i++){
			
			if(!"0".equals(eList[i])){
				elementIdList.add(Long.valueOf(eList[i]));
			}
		}
		
		
		
    	long l = System.currentTimeMillis();    
    	
    	Set<EdocFormElement> edocFormElements = null;    	
    	
    	EdocForm edocForm = new EdocForm();
    	Long uuid = UUIDLong.longUUID();
    	edocForm.setId(uuid);
    	edocForm.setName(name);
    	edocForm.setDescription(description);
    	edocForm.setType(Integer.valueOf(type));
    	edocForm.setContent(content);
    	edocForm.setCreateUserId(user.getId());
    	edocForm.setCreateTime(new java.sql.Timestamp(l));
    	edocForm.setLastUserId(user.getId());
    	edocForm.setLastUpdate(new java.sql.Timestamp(l));
    	edocForm.setStatus(EdocForm.C_iStatus_Draft);
    	edocForm.setDomainId(user.getLoginAccount());
    	edocForm.setEdocFormElements(edocFormElements);	
    	edocForm.setStatus(Integer.valueOf(status));
    	edocForm.setShowLog("1".equals(showLog));
    	edocForm.setIsSystem(false);
		// 设置意见格式
		String showLastOptionOnly = "0";
		String showDept = "0";
		String dealTimeFormt = "0";
		if (!Strings.isBlank(request.getParameter("showLastOptionOnly"))
				&& "on".equals(request.getParameter("showLastOptionOnly"))) {
			showLastOptionOnly = "1";
		}
		if (!Strings.isBlank(request.getParameter("showDept"))
				&& "on".equals(request.getParameter("showDept"))) {
			showDept = "1";
		}
		if (!Strings.isBlank(request.getParameter("dealTimeFormt"))
				&& "1".equals(request.getParameter("dealTimeFormt"))) {
			dealTimeFormt = "1";
		}

		
		//edocForm.setOptionFormatSet(optionFormatSet);
    	
    	try{
    	if(null!=isDefault && "1".equals(isDefault)){
    		//从数据库中取消默认公文单
    		edocFormManager.updateDefaultEdocForm(user.getLoginAccount(), Integer.valueOf(type));
    		edocForm.setIsDefault(true);
    		//设置空的默认公文单，下次读取时，重新从数据库中读取
    		edocFormManager.removeDefaultEdocForm(edocForm.getDomainId(),edocForm.getType());
    		
    	}else if(null!=isDefault && "0".equals(isDefault)){
    		//edocFormManager.updateDefaultEdocForm(user.getLoginAccount(), Integer.valueOf(type));
    		edocForm.setIsDefault(false);
    	}
    	 	
    	String[] att_fileUrl = request.getParameterValues("att_fileUrl");
    	
    	edocForm.setFileId(Long.parseLong(att_fileUrl[0]));
    	edocForm.setEdocFormAcls(getEdocFormAclSetForCurrentForm(user.getLoginAccount(),grantedDomainId, edocForm));
    	// 设置意见格式，格式为1,1,0
		String optionFormatSet = showLastOptionOnly + "," + showDept + ","+ dealTimeFormt;
    	//给所有的授权子单位添加状态信息.保存状态信息
		Set<EdocFormExtendInfo> infos = getCreateInfos(user, edocForm,
				optionFormatSet);
    	edocForm.getEdocFormExtendInfo().addAll(infos);
    	
    	edocFormManager.createEdocForm(edocForm, elementIdList);
    	
		String boundName = request.getParameter("boundName");
		mav.addObject("boundName", boundName);
		String category = "";
		if(null!=type && !"".equals(type)){
			if(Integer.valueOf(type).intValue() == EdocEnum.edocType.sendEdoc.ordinal()){
				category = MetadataNameEnum.edoc_send_permission_policy.name();
			}else if(Integer.valueOf(type).intValue() == EdocEnum.edocType.recEdoc.ordinal()){
				category = MetadataNameEnum.edoc_rec_permission_policy.name();
			}else if(Integer.valueOf(type).intValue() == EdocEnum.edocType.signReport.ordinal()){
				category = MetadataNameEnum.edoc_qianbao_permission_policy.name();
			}
		}
		
		String tempS = request.getParameter("listStr");
		if(null!=tempS && !"".equals(tempS)){
			String[] tempArray = tempS.split(",");
			for(String process_name:tempArray){
				String flowperm_name = request.getParameter("returnOperation_"+process_name);
				String flowperm_label = request.getParameter(process_name);
				String sortType = request.getParameter("sortType_"+ process_name);
				if(Strings.isNotBlank(flowperm_name) && Strings.isNotBlank(flowperm_label)){
					boundEdocFormAndFlowPerm(process_name,flowperm_name,flowperm_label,sortType,edocForm);	
					
				}
			}
		}
			attachmentManager.create(ApplicationCategoryEnum.edoc, edocForm.getId(), edocForm.getId(), request);
		}catch(Exception e){
			StringBuilder sb=new StringBuilder();
			sb.append("用户新建公文单异常,EdocFormController.create ");
			if(edocForm!=null){
				sb.append("名字:"+edocForm.getName());
				sb.append("时间:"+edocForm.getCreateTime());
			}
			sb.append("用户："+user.getName());
			log.error(sb.toString(),e);
			throw e;
		}
		return super.refreshWindow("parent");
	}

	private Set<EdocFormExtendInfo> getCreateInfos(User user,
			EdocForm edocForm, String optionFormatSet) throws BusinessException {
		
		Set<EdocFormExtendInfo> infos = new HashSet<EdocFormExtendInfo>();
    	for(EdocFormAcl acl : edocForm.getEdocFormAcls()){
    		V3xOrgAccount acc = orgManager.getAccountById(acl.getDomainId());
    		if(acc.getIsRoot()){   //授权给集团的给集团下面所有的单位都添加info对象
    			List<V3xOrgAccount> accounts = organizationManager.getAllAccounts();
    			for(V3xOrgAccount account :accounts){
    				if(account.getId().equals(edocForm.getDomainId())) continue;
        			EdocFormExtendInfo info = new EdocFormExtendInfo();
        	    	info.setIdIfNew();
        	    	info.setAccountId(account.getId());
        	    	if(account.getId().equals(user.getLoginAccount())){
        	    		info.setIsDefault(edocForm.getIsDefault());
        	    		info.setStatus(edocForm.getStatus());
        	    	}else{
        	    		info.setIsDefault(false);
        	    		info.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_USELESS);
        	    	}
        	    
        	    	info.setEdocForm(edocForm);
        	    	info.setOptionFormatSet(optionFormatSet);
        	    	infos.add(info);
        		}
    		}else{ //授权给单位的就单独添加
    			if(acc.getId().equals(edocForm.getDomainId())) continue;
    			EdocFormExtendInfo info = new EdocFormExtendInfo();
    	    	info.setIdIfNew();
    	    	info.setAccountId(acc.getId());
    	    	if(acc.getId().equals(user.getLoginAccount())){
    	    		info.setIsDefault(edocForm.getIsDefault());
    	    		info.setStatus(edocForm.getStatus());
    	    	}else{
    	    		info.setIsDefault(false);
    	    		info.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_USELESS);
    	    	}
    	    
    	    	info.setEdocForm(edocForm);
    	    	info.setOptionFormatSet(optionFormatSet);
    	    	infos.add(info);
    		}
    	}
    	//本单位的单独添加。不管有没有授权都添加
    	EdocFormExtendInfo info = new EdocFormExtendInfo();
    	info.setIdIfNew();
    	info.setAccountId(user.getLoginAccount());
		info.setIsDefault(edocForm.getIsDefault());
		info.setStatus(edocForm.getStatus());
    	info.setEdocForm(edocForm);
    	info.setOptionFormatSet(optionFormatSet);
    	infos.add(info);
		return infos;
	}
	/**
	 * 新建公文单的时候绑定公文单上的意见元素和节点权限
	 * @param process_name     ：意见元素名称
	 * @param flowperm_name	      ：节点权限名称(例如:zhihui)
	 * @param flowperm_label   ：节点权限名称Lable(例如：知会)
	 * @param sortType         ：排序方式
	 * @param edocForm         ：公文单
	 * @param accountId        :单位ID，如果是指定单位的，则增加指定单位下面的绑定，否则给所有授权对象下面增加绑定
	 * 1、新建公文单的时候，给所有授权单位添加绑定对象
	 * 2、公文单制作单位修改公文单的时候，由于可以修改授权，所以需要给新增加的授权单位增加绑定对象
	 * 3、外单位修改授权公文单的时候，由于不能修改公文单授权，所以不需要添加绑定对象。
	 * 4、列表中授权的时候需要相应的增加或者删除绑定对象。
	 */
	private void boundEdocFormAndFlowPerm(String process_name,String flowperm_name,String flowperm_label,
			String sortType,EdocForm edocForm){
	
		try{
			Set<EdocFormAcl> acls =edocForm.getEdocFormAcls();
			for(EdocFormAcl acl : acls){
				V3xOrgAccount acc = orgManager.getAccountById(acl.getDomainId());
				if(acc.getIsRoot()){
					List<V3xOrgAccount> accounts = organizationManager.getAllAccounts();
		    		for(V3xOrgAccount account :accounts){
						boundOneSpecialAccount(process_name, flowperm_name,
								flowperm_label, sortType, edocForm, account.getId());
		    		}
				}else{
					boundOneSpecialAccount(process_name, flowperm_name,
							flowperm_label, sortType, edocForm, acl.getDomainId());
				}
			}
			//没有给自己授权，也要添加绑定
			if(!isIncludeCurrentAccount(edocForm.getDomainId(), acls)){
				boundOneSpecialAccount(process_name, flowperm_name,
						flowperm_label, sortType, edocForm, edocForm.getDomainId());
			}
		}catch(Exception e){
			log.error("绑定公文单意见元素和节点权限异常",e);
		}
	}
	/**
	 * 
	 * @param cAccount
	 * @param transAccount
	 * @return
	 */
    private boolean isIncludeCurrentAccount(Long cAccount,Set<EdocFormAcl> acl){
    	for(EdocFormAcl a:acl){
    		V3xOrgAccount acc;
			try {
				acc = orgManager.getAccountById(a.getDomainId());
				if(acc.getIsRoot()) return true;
			} catch (BusinessException e) {
				log.error(e);
			}
			if(a.getDomainId().equals(cAccount)) return true;
    	}
    	return false;
    }
	/**
	 * 
	 * 绑定一个指定单位的节点权限
	 * */
	private void boundOneSpecialAccount(String process_name, String flowperm_name,
			String flowperm_label, String sortType, EdocForm edocForm,
			Long accountId) throws Exception {
		if(edocForm.getDomainId().longValue() == accountId.longValue()){
			edocFormManager.bound(process_name, flowperm_name,
					flowperm_label, edocForm.getId(), sortType,accountId);
		}else{
			//1、授权给外单位的时候只绑定系统节点权限
			//2、被授权单位修改绑定的时候可以绑定任意的节点权限。
			boolean isSystemFlowPerm = flowPermManager.isSystemFlowPerm(flowperm_name, accountId);
			if(isSystemFlowPerm){
				edocFormManager.bound(process_name, flowperm_name,
						flowperm_label, edocForm.getId(), sortType, accountId);
			}
		}
	}
	private Set<EdocFormAcl> getEdocFormAclSetForCurrentForm(long loginAccountId,
			String grantedDomainId, EdocForm edocForm) {
		//授权
		Set<EdocFormAcl> edocFormAcls = new HashSet<EdocFormAcl>();
		if(Strings.isNotBlank(grantedDomainId)){
			String[]  domainIds = grantedDomainId.split(",");
			for(String domainId : domainIds){
				EdocFormAcl acl = new EdocFormAcl();
				acl.setIdIfNew();
				String[] domainArr = domainId.split("\\|");
				acl.setDomainId(Long.parseLong(domainArr[1]));
				acl.setEntityType(domainArr[0]);
				acl.setFormId(edocForm.getId());
				edocFormAcls.add(acl);
			}
		}
		return edocFormAcls;
	}
	
	public ModelAndView newForm(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/formManage/new_edoc_form");
		String type = request.getParameter("type");
		mav.addObject("operType", "add");
		
		//授权给自己单位，用来回显数据
		List<EdocFormAcl> list = new ArrayList<EdocFormAcl>();
		EdocFormAcl acl = new EdocFormAcl();
		acl.setDomainId(CurrentUser.get().getLoginAccount());
		acl.setEntityType(V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		list.add(acl);
		mav.addObject("elements", list);
		return mav.addObject("type", type);
	}
	
	public ModelAndView uploadForm(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		User user = CurrentUser.get();
		String name = request.getParameter("name");		
	  	String type = request.getParameter("type");  	//公文的类型
	  	String status = request.getParameter("status");  	
	  	String isDefault = request.getParameter("isDefault");	  	
	  	String description = request.getParameter("description");
	  	String showLog = request.getParameter("showLog");
	  	String edocFormStatusId = request.getParameter("edocFormStatusId");
		PrintWriter out = response.getWriter();
	  	
		String method = request.getParameter("method_type");
		ModelAndView mav = null;
		
		if(!"".equals(method) && "change".equals(method)){
			/**
			 * 是否显示单位标识
			 * 暂缺
			 */
			mav = new ModelAndView("edoc/formManage/form_modify");
			String id = request.getParameter("id");
			EdocForm edocForm = edocFormManager.getEdocForm(Long.valueOf(id));
			String aclIds = "";
			Set<EdocFormAcl> aclList =  edocForm.getEdocFormAcls();
			if(aclList != null){
				for(EdocFormAcl acl : aclList){
					if("".equals(aclIds)) aclIds = String.valueOf(acl.getDomainId());
					else aclIds += ","+acl.getDomainId();
				}
			}
			mav.addObject("aclIds",aclIds);
			mav.addObject("formId", id);
			mav.addObject("bean", edocForm);
			mav.addObject("method_type", "change");
			
		}else{
			//授权给自己单位，用来回显数据
			List<EdocFormAcl> list = new ArrayList<EdocFormAcl>();
			EdocFormAcl acl = new EdocFormAcl();
			acl.setDomainId(user.getLoginAccount());
			acl.setEntityType(V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
			list.add(acl);
			mav = new ModelAndView("edoc/formManage/new_edoc_form");
			mav.addObject("elements", list);
		}
		mav.addObject("edocFormStatusId", edocFormStatusId);
    	String att_fileUrl = request.getParameter("att_fileUrl");
    	String att_createDate = request.getParameter("att_createDate");
    	String att_mimeType = request.getParameter("att_mimeType");
    	String att_fileName = request.getParameter("att_filename");
    	String att_needClone = request.getParameter("att_needClone");
    	String att_description = request.getParameter("att_description");
    	String att_type = request.getParameter("att_type");
    	String att_size = request.getParameter("att_size");
    	
    	mav.addObject("att_fileUrl", att_fileUrl );
    	mav.addObject("att_createDate", att_createDate );
    	mav.addObject("att_mimeType", att_mimeType );
    	mav.addObject("att_filename", att_fileName );
    	mav.addObject("att_needClone", att_needClone );
    	mav.addObject("att_description", att_description );
    	mav.addObject("att_type", att_type );
    	mav.addObject("att_size", att_size );
	
		String[] urls=(String[])request.getParameterValues("fileUrl");
		String[] createDates=(String[])request.getParameterValues("fileCreateDate");
		String[] mimeTypes=(String[])request.getParameterValues("fileMimeType");
		String[] names=(String[])request.getParameterValues("filename");
	
			//		System.out.println("Here  is begin index!"+urls.length+createDates.length);
		if(urls == null){
			return mav;
		}	
		//前台页面展示，下载公文单的时候使用
		if(urls.length>0 && createDates!=null && createDates.length>0 && names!=null && names.length>0){
			mav.addObject("fileId",urls[0]);
			mav.addObject("fileName", names[0]);
			mav.addObject("createDate",createDates[0].substring(0, 10));
		}
			//对存储文件处理  生成.xsn文件，并返回存储路径
 
		String path = edocFormManager.getDirectory(urls, createDates, mimeTypes, names);
		String sampledata = "sampledata.xml";
		String view = "view1.xsl";		
		String xml = "";
		String xsl = "";	
		IFormResoureProvider fResourceProvider;
		try{
			ByteArrayInputStream fInfopathxsn=new ByteArrayInputStream(www.seeyon.com.v3x.form.utils.StringUtils.readFileData(path));
	
			fResourceProvider = new CabFileResourceProvider(fInfopathxsn);

			xml = fResourceProvider.loadResource(sampledata);
		}catch(DataDefineException e){
//			response.setContentType("text/html; charset=UTF-8"); 
			request.setCharacterEncoding("UTF-8");
//			StringBuffer sb = new StringBuffer();
//			PrintWriter printout = response.getWriter();
//			sb.append("<script language='JavaScript'>");
//			sb.append("alert(\"");
//			sb.append(e.getToUserMsg());
//			sb.append("\");</script>");
//			printout.println(sb.toString());
			
			PrintWriter outprint = response.getWriter();
			outprint.println("<script>");
			outprint.println("alert(\"" +e.getToUserMsg()+ "\")");
			outprint.println("</script>");
			outprint.flush();
			
			return super.refreshWindow("parent");	
		}
			
		mav.addObject("original_xml", request.getParameter("original_xml"));		
		//取出所有fieldName
		try{
		  int a = xml.indexOf(">");
		  int c = xml.indexOf("</my:myFields>");
		  xml = xml.substring(a+1,c);
		}catch(Exception e){
				log.error("infopath校验错误,请检查是否包含正确的公文元素字段",e);
				out.println("<script>");
				out.println("alert(parent._('edocLang.edoc_form_infopath_error'));");				
				//out.println("alert('infopath校验错误,请检查是否包含正确的公文元素字段! ');");
				out.println("parent.location.reload(true);");
				out.println("</script>");
				return null;
				//return super.refreshWindow("parent");				
			}
		  
		  List<String> list = new ArrayList<String>();
		  
		  String[] str = xml.split("/>");
		  String temp = "";
		  for(int i=0;i<str.length-1;i++){
			  String str_a = str[i];
			  if(str_a.contains("<my:")){//判断是否事以<my:开始,因为infopath2007与2003在样式上略又区别
			  int x  = str_a.indexOf(":");
			  str_a = str_a.substring(x+1, str_a.length());
			  //if(!str_a.startsWith("field")){
				  list.add(str_a);
				  temp += "<my:"+str_a+"></my:"+str_a+">"; 
			//  	}
			  }
		  }
		  
		  if(null==list || list.size()==0){
			  	log.error("公文单中没有发现正确的域名字段,请重新设计公文单");
				out.println("<script>");
				out.println("alert(parent._('edocLang.edoc_form_no_such_field'));");
				out.println("parent.location.reload(true);");
				//out.println("alert('公文单中没有发现正确的域名字段,请重新设计公文单');");
				out.println("</script>");
				return null;		  
		  }else{//判断是否有标题
			  boolean bool = list.contains("subject");
			  if(!bool){
				  	log.error("公文单中必须包含标题字段!请重新设计公文单");
					out.println("<script>");
					out.println("alert(parent._('edocLang.edoc_form_subject_must'));");
					out.println("parent.location.reload(true);");
					//out.println("alert('公文单中没有发现正确的域名字段,请重新设计公文单');");
					out.println("</script>");
					return null;
					//return super.refreshWindow("parent");					  
			  }
		  }
		  
		  //取出所有元素的fieldName
		  List<EdocElement> elements = edocElementManager.getEdocElementsByStatus(1, 1, 10000);
		  //List<EdocElement> elements = edocElementManager.getAllEdocElements(1, 10000);
		  List<String> eleList = new ArrayList<String>();
		  List <EdocFormElement>eles = new ArrayList<EdocFormElement>();
		  
		  List<String> boundList = new ArrayList<String>();
		 
		ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
			
		  
		  for(EdocElement ele:elements){
			  //if(ele.getType()==6)continue;
			  eleList.add(ele.getFieldName().toLowerCase());
			  String temp_s = "";
			  if(ele.getIsSystem()){//如果是系统元素，取国际化值
				  temp_s = ResourceBundleUtil.getString(r, ele.getName());
			  }else{
				  temp_s = ele.getName();
			  }
			  boundList.add(ele.getFieldName().toLowerCase() + "|" + temp_s); //拼成 元素得域名 + 国际化名 ( banli|办理 )
		  }
		  //zhanghua:asking otherOption
		  eleList.add("otheropinion");

		  List<String> configList_first= com.seeyon.v3x.edoc.util.StringUtils.findEdocElementFromConfig(type);

		  eleList.addAll(configList_first);
		  
		  for(String s:list){
			  if(!"".equals(s) && !eleList.contains("otherOpinion".equals(s)?"otheropinion":s)){
					if(!"".equals(method) && "change".equals(method)){
						log.error("公文单只允许对样式进行修改,不能修改公文元素");
						out.println("<script>");
						out.println("alert(parent._('edocLang.edoc_form_field_forbiddend'));");
						//out.println("alert('公文单只允许对样式进行修改,不能修改公文元素');");
						out.println("parent.location.reload(true);");
						out.println("</script>");
						return null;
						//return super.refreshWindow("parent");	
					}else{
						log.error("'输入的字段域名不正确! 没有域名为 : ["+s+"] 的元素  或此元素没有被启用");
						out.println("<script>");
						out.println("alert(parent._('edocLang.edoc_form_field_error','"+(s!=null && s.toLowerCase().indexOf("group")!=-1 && s.indexOf(">")!=-1?s.substring(0,s.indexOf(">")):s)+"'));");
						//out.println("alert('字段域名校验错误! 没有域名为 : ["+s+"] 的元素  或此元素没有被启用 ');");
						out.println("parent.location.reload(true);");
						out.println("</script>");
						return null;
						//return super.refreshWindow("parent");
					}
			  }
		  }
		  
		  String method_type = request.getParameter("method_type");
		  if(null!=method_type && "change".equals(method_type)){			  	
			  	
			  	List<String> configList= com.seeyon.v3x.edoc.util.StringUtils.findEdocElementFromConfig(type);			  				  
				String original_xml = request.getParameter("original_xml");			
				String mx = request.getParameter("mx");
				List<String> orgList = new ArrayList<String>();
						
					  String[] strOrg = original_xml.split("\\|");
					  for(String so:strOrg){
						  orgList.add(so.toLowerCase());
					  }
					  orgList.add("otheropinion");
					  orgList.addAll(configList);

					  //BUG23044
					  //修改文单时候允许调整意见元素
					  for(EdocElement ele:elements){
						  if(ele.getType()==EdocElement.C_iElementType_Comment)
						  {
							  orgList.add(ele.getFieldName().toLowerCase());
						  }
					  }
					  
					  List<String> xslList = new ArrayList<String>();
					  String[] strt = mx.split("\\|");			  
					  
					  for(int i=0;i<strt.length;i++){
						  int x = strt[i].indexOf(":");
						  xslList.add(strt[i].substring(x+1, strt[i].length()));
					  }
					  for(String st:list){
						  if(!orgList.contains(st.toLowerCase())){
							  //上传文单时拼成的xml中没有logoimg，在此取消对其判断
							  if("logoimg".equals(st.toLowerCase()))
								  continue;
							  	log.error("字段域名不匹配 ["+st+"]");
								out.println("<script>");
								out.println("alert(parent._('edocLang.edoc_form_field_not_match','"+st+"'));");
								//out.println("alert('字段域名不匹配 ["+st+"]');");
								out.println("</script>");
								out.flush();
								return super.refreshWindow("parent");						  
						  }
				}
		  }

		  
		  String element_id_list = "";
		  for(String s_a:list){
			  EdocFormElement formElement = new EdocFormElement();
			  long id = 0;
			  try{
			  id = edocElementManager.getIdByFieldName(s_a);
			  }catch(Exception e){
				  //out.println("<script>");
				  //out.println("alert('没有对应的 field name"+s_a+"');");
				  //out.println("</script>");	
			  }
			  formElement.setElementId(id);
			  eles.add(formElement);
			  element_id_list += String.valueOf(id);
			  element_id_list +=",";
		  }
		  EdocSummary edocSummary = null;
		  long actorId =1;
		  
		 String result = XMLConverter.convert(eles, edocSummary, actorId , Integer.valueOf(type).intValue()).toString();
		// String result  =  XMLConverter.uploadXMLConvert(list, "<my:myFields xmlns:my=\"www.seeyon.com/form/2007\">"+temp);
		 
		try{
		xsl = fResourceProvider.loadResource(view);
		
		
		//byte[] tempByte_a = CharReplace.doReplace_Encode(xsl.getBytes());
		
		//String xxl = xsl;
//		String s = new String(tempByte_a);
		InfoPath_xsl info = new InfoPath_xsl(); 
//		System.out.println(tempByte_a);
//		System.out.println("============================================================");
//		System.out.println(new String(s.getBytes("iso-8859-1"),"utf-8"));
//		System.out.println("============================================================");
		info.setFileInfo(xsl);
		info.covertContent(null);
		xsl = info.getFileInfo();
		}catch(SeeyonFormException e){
//			response.setContentType("text/html; charset=UTF-8"); 
			request.setCharacterEncoding("UTF-8");
//			StringBuffer sb = new StringBuffer();
//			PrintWriter printout = response.getWriter();
//			sb.append("<script language='JavaScript'>");
//			sb.append("alert(\"");
//			sb.append(e.getToUserMsg());
//			sb.append("\");</script>");
//			out.flush();
//			printout.println(sb.toString());
//			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(\"" +e.getToUserMsg()+ "\")");
			out.println("</script>");
			out.flush();
			return super.refreshWindow("parent");
		}
		
//		System.out.println(xsl);
		
		byte[] tempByte_a = CharReplace.doReplace_Encode(xsl.getBytes("UTF-8"));
		
		xsl = new String(tempByte_a , "UTF-8");
		
		byte[] tempByte_b = CharReplace.doReplace_Decode(xsl.getBytes("UTF-8"));
		
		mav.addObject("xml", result);
		xsl = new String(tempByte_b,"UTF-8");
		mav.addObject("xsl", xsl);
		mav.addObject("tempList", list);		
		mav.addObject("element_id_list", element_id_list);	
		mav.addObject("isDefault", isDefault);
		mav.addObject("type", type);
		mav.addObject("status", status);
		mav.addObject("name",name);
		mav.addObject("description", description);
		mav.addObject("showLog", showLog);
		
		//取得公文处理意见的名称列表
		
		List<FormBoundPerm> processList = null;
		if(!"".equals(method) && "change".equals(method)){
			String id = request.getParameter("id");
			EdocForm edocForm = edocFormManager.getEdocForm(Long.valueOf(id));
				if(null!=edocForm){
					processList = EdocHelper.getProcessOpinionByEdocFormId(list, edocForm.getId(), edocForm.getType(),user.getLoginAccount());
				}
			}else{
			processList = EdocHelper.getProcessOpinionFromEdocForm(list,Integer.valueOf(type));
		}
		mav.addObject("processList", processList);
		
		//用于提交选择参数
		String listStr = "";
		for(FormBoundPerm perm: processList){
			listStr += perm.getPermItem();
			listStr += ",";
		}
		if(!"".equals(listStr)){
			listStr = listStr.substring(0, listStr.length()-1);
		}
		mav.addObject("listStr", listStr);
		//
		
		
		String category = "";
		if(null!=type && !"".equals(type)){
			if(Integer.valueOf(type).intValue() == EdocEnum.edocType.sendEdoc.ordinal()){
				category = MetadataNameEnum.edoc_send_permission_policy.name();
			}else if(Integer.valueOf(type).intValue() == EdocEnum.edocType.recEdoc.ordinal()){
				category = MetadataNameEnum.edoc_rec_permission_policy.name();
			}else if(Integer.valueOf(type).intValue() == EdocEnum.edocType.signReport.ordinal()){
				category = MetadataNameEnum.edoc_qianbao_permission_policy.name();
			}
		}
		
		List<FlowPerm> flowPermlist = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, user.getLoginAccount());
	
		mav.addObject("flowPermList", flowPermlist);
				
		String operation_str = "";		
		operation_str = EdocHelper.getProcessOpinionFromEdocFormOperation(list, Integer.valueOf(type));	
		mav.addObject("operation_str", operation_str);	
		String logoURL = EdocHelper.getLogoURL();	
		mav.addObject("logoURL", logoURL);
		
		return mav;
	}
	
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		PrintWriter print = response.getWriter();
		String id = request.getParameter("id");
		
		String[] ids = id.split(",");
	
		for(int i=0;i<ids.length;i++){		
			String str = edocFormManager.deleteForm(Long.valueOf(ids[i]));
			if(null!=str && !"".equals(str)){
				log.error(str);				
				return super.refreshWindow("parent",str);	
			}
		}

		return super.refreshWindow("parent");

	}
	
	public ModelAndView change(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		User user = CurrentUser.get();
		//Long accountId = user.getLoginAccount();
		//String configItem_accountId = String.valueOf(accountId);
		//String logoFileName = "logo.gif";
		//String replacement = "<xsl:template match=\"my:myFields\"><div align=\"left\"><img src=\"/seeyon/apps_res/v3xmain/images/"+logoFileName+"\" /></div>";
		
		String name = request.getParameter("name");
		//String type = request.getParameter("type");
		String id = request.getParameter("id");
		String statusId = request.getParameter("edocFormStatusId"); 
		String status = request.getParameter("status");
		String description  = request.getParameter("description");
		String isDefault = request.getParameter("isDefault");
		String showLog = request.getParameter("showLog");
		String content = request.getParameter("content");
		String grantedDomainId = request.getParameter("grantedDomainId");
		
		EdocFormExtendInfo info =null;
		if(Strings.isNotBlank(statusId)){
			info = edocFormManager.getEdocFormExtendInfo(Long.valueOf(statusId));
		}
		EdocForm eForm = edocFormManager.getEdocForm(Long.valueOf(id));
//		if(info == null) {
//			info = new EdocFormExtendInfo();
//			info.setIdIfNew();
//			info.setEdocForm(eForm);
//			info.setAccountId(user.getLoginAccount());
//		}
		
		
		eForm.setName(name); 
		eForm.setStatus(Integer.valueOf(status));

		
		if(null!=content && !"".equals(content)){
			byte[] tempByte_a = CharReplace.doReplace_Encode(content.getBytes("UTF-8"));
			content = new String(tempByte_a , "UTF-8");
		}
		
		eForm.setContent(content);
		
		eForm.setDescription(description);
		eForm.setShowLog("1".equals(showLog));
		eForm.setIsDefault(false);
		
		if(info != null){
			if(null!=isDefault && "1".equals(isDefault)){
				//if the original form has been the default form then change it's isDefault stauts is unnecessary.
				eForm.setIsDefault(true);
				if(!info.getIsDefault()){
					edocFormManager.updateDefaultEdocForm(user.getLoginAccount(), eForm.getType());
					edocFormManager.setDefaultEdocForm(user.getLoginAccount(), eForm.getType(), eForm);
					NotificationManager.getInstance().send(NotificationType.DefaultEdocFormReSet, new Object[]{eForm.getId(),user.getLoginAccount()});
				}
			}else{
				//info.setIsDefault(false);
				eForm.setIsDefault(false);
			}
		}
		//info.setStatus(eForm.getStatus());
	
		
	   	String[] att_fileUrl = request.getParameterValues("att_fileUrl");
    	
    	if(null!=att_fileUrl && att_fileUrl.length>0){
    		if(!"".equals(att_fileUrl[0])){
    		eForm.setFileId(Long.parseLong(att_fileUrl[0]));
    		attachmentManager.update(ApplicationCategoryEnum.edoc, eForm.getId(), eForm.getId(), request);		
    		}
    	}
    	
    	List<EdocFormFlowPermBound> boundPermList = edocFormManager.findBoundByFormId(eForm.getId());
    	Set<EdocFormAcl> reqAcl = getEdocFormAclSetForCurrentForm(user.getLoginAccount(), grantedDomainId, eForm);
    	if(null!=boundPermList){
	    	
    		//首先删除该公文单下得绑定对象
	    	edocFormManager.deleteEdocFormFlowPermBoundByFormIdAndAccountId(eForm.getId(),user.getLoginAccount());
			String tempS = request.getParameter("listStr");
			if(null!=tempS && !"".equals(tempS)){
				String[] tempArray = tempS.split(",");
				for(String process_name:tempArray){
					
					String flowperm_name = request.getParameter("returnOperation_"+process_name);
					String flowperm_label = request.getParameter(process_name);
					String sortType = request.getParameter("sortType_"+ process_name);
					
					if (Strings.isNotBlank(flowperm_name) && Strings.isNotBlank(flowperm_label)
							&& Strings.isNotBlank(sortType)) {
						//boundOneSpecialAccount(process_name, flowperm_name, flowperm_label, sortType, eForm,user.getLoginAccount());
						//修改的时候自己单位肯定是要添加非系统节点权限的。
						edocFormManager.bound(process_name, flowperm_name,flowperm_label, eForm.getId(), sortType,user.getLoginAccount());
						//增加
						List<Long> addAcl = getCompareEdocFormAclsAdd(eForm.getEdocFormAcls(), reqAcl);
						if(addAcl!=null){
							for(Long accoutId : addAcl){
								if(eForm.getDomainId().equals(accoutId))
									continue;
								boundOneSpecialAccount(process_name, flowperm_name, flowperm_label, sortType, eForm,accoutId);
							}
						}
						//删除
						List<Long> delAcl = getCompareEdocFormAclsDel(eForm.getEdocFormAcls(), reqAcl);
						if(delAcl!=null){
							for(Long accoutId : delAcl){
								if(!eForm.getDomainId().equals(accoutId))
									edocFormManager.deleteEdocFormFlowPermBoundByFormIdAndAccountId(eForm.getId(),accoutId);
							}
						}
					}
					
				}    	
		    }
    	}
		String showLastOptionOnly = "0";
		String showDept = "0";
		String dealTimeFormt = "0";
		if (!Strings.isBlank(request.getParameter("showLastOptionOnly"))
				&& "on".equals(request.getParameter("showLastOptionOnly"))) {
			showLastOptionOnly = "1";
		}
		if (!Strings.isBlank(request.getParameter("showDept"))
				&& "on".equals(request.getParameter("showDept"))) {
			showDept = "1";
		}
		if (!Strings.isBlank(request.getParameter("dealTimeFormt"))
				&& "1".equals(request.getParameter("dealTimeFormt"))) {
			dealTimeFormt = "1";
		}

		// 设置意见格式，格式为1,1,0
		String optionFormatSet = showLastOptionOnly + "," + showDept + ","
				+ dealTimeFormt;
	//	eForm.setWebOpinionSet(optionFormatSet);
		//eForm.setOptionFormatSet(optionFormatSet);
		
		Set<EdocFormExtendInfo> infos =getInfos(eForm,reqAcl,user.getLoginAccount(),optionFormatSet,false);
		eForm.getEdocFormExtendInfo().clear();
		eForm.getEdocFormExtendInfo().addAll(infos);
		
		eForm.getEdocFormAcls().clear();
		eForm.getEdocFormAcls().addAll(reqAcl);
		
		edocFormManager.saveEdocForm(eForm);
		
		return super.refreshWindow("parent");
		
		
	}
	private List<Long> getCompareEdocFormAclsAdd(Set<EdocFormAcl> orginalAcl,Set<EdocFormAcl> newAcl){
		return getCompareEdocFormAcls(orginalAcl,newAcl).get("add");
	}
	
	private List<Long> getCompareEdocFormAclsDel(Set<EdocFormAcl> orginalAcl,Set<EdocFormAcl> newAcl){
		return getCompareEdocFormAcls(orginalAcl,newAcl).get("del");
	}
	/**
	 * @param orginalAcl :原始的公文单授权 
	 * @param newAcl ：新的公文单授权
	 * @return
	 */
	private Map<String,List<Long>> getCompareEdocFormAcls(Set<EdocFormAcl> orginalAcl,Set<EdocFormAcl> newAcl){
		if(orginalAcl == null || newAcl == null) return new HashMap<String,List<Long>>();
		Map<String,List<Long>> map = new HashMap<String,List<Long>>();
		try{
			Long rootAccountId = orgManager.getRootAccount().getId();
			//先判断是不是都是集团。避免性能问题
			if(orginalAcl.size()==1 && newAcl.size()==1 ){
				for(EdocFormAcl acl:orginalAcl){
					if(acl.getDomainId().equals(rootAccountId)){
						for(EdocFormAcl ac:newAcl){
							if(ac.getDomainId().equals(rootAccountId)){
								return new HashMap<String,List<Long>>();
							}
							break;
						}
					}
					break;
				}
			}
			List<Long> deleteAcl = new ArrayList<Long>();
			List<Long> addAcl = new ArrayList<Long>();
			Set<Long> orginalListL = new HashSet<Long>();
			Set<Long> newListL = new HashSet<Long>();
		
			
			for(EdocFormAcl acl : newAcl){
				if(acl.getDomainId().equals(rootAccountId)){
					List<V3xOrgAccount> accounts = organizationManager.getAllAccounts();
					for(V3xOrgAccount account :accounts){
						newListL.add(account.getId());
					}
				}else{
					newListL.add(acl.getDomainId());
				}
			}
			
			for(EdocFormAcl acl : orginalAcl){
				if(acl.getDomainId().equals(rootAccountId)){
					List<V3xOrgAccount> accounts = organizationManager.getAllAccounts();
					for(V3xOrgAccount account :accounts){
						orginalListL.add(account.getId());
					}
				}else{
					orginalListL.add(acl.getDomainId());
				}
			}
			for(Long id : newListL){
				if(!orginalListL.contains(id)) addAcl.add(id);
			}
			for(Long id : orginalListL){
				if(!newListL.contains(id)) deleteAcl.add(id);
			}
			map.put("add", addAcl);
			map.put("del",deleteAcl);
		}catch(Exception e){
			log.error(e);
		}
		return map;
	}
	
	//改变授权的时候处理info信息
	private Set<EdocFormExtendInfo> getInfos(EdocForm ef ,Set<EdocFormAcl>  newAcl,Long currentAccount,String order,boolean isOnlyAcl){
		Map<Long,EdocFormExtendInfo> map = new HashMap<Long,EdocFormExtendInfo>();
		for(EdocFormExtendInfo info:ef.getEdocFormExtendInfo()){
			map.put(info.getAccountId(), info);
		}
		Set<EdocFormExtendInfo> infos = new HashSet<EdocFormExtendInfo>();
		boolean isAcl2CAccount = false;//是否授权制作单位，需要保证制作单位始终有一条数据，
		try {
			for(EdocFormAcl acl : newAcl){
				if(acl.getDomainId().equals(ef.getDomainId())) isAcl2CAccount =  true;
	    		V3xOrgAccount acc = orgManager.getAccountById(acl.getDomainId());
				if(acc.getIsRoot()){  //授权给集团
					List<V3xOrgAccount> accounts = organizationManager.getAllAccounts();
		    		for(V3xOrgAccount account :accounts){
		    			
		    			if(account.getId().equals(currentAccount)) continue;
		    			
		    			if(map.get(acc.getId())!=null){  //有历史info信息
		    				infos.add(map.get(account.getId()));
		    			}else{
		    				EdocFormExtendInfo info = new EdocFormExtendInfo();
		        	    	info.setIdIfNew();
		        	    	info.setAccountId(account.getId());
	        	    		info.setIsDefault(false);
	        	    		info.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_USELESS);
		        	    	info.setEdocForm(ef);
		        	    	if(Strings.isNotBlank(order)){
		        	    		info.setOptionFormatSet(order);
		        	    	}else{
		        	    		info.setOptionFormatSet("0,0,0");
		        	    	}
		        	    	infos.add(info);
		    			}
		    		}
				}else{
					if(acc.getId().equals(currentAccount)) continue;
	    			
	    			if(map.get(acc.getId())!=null){
	    				infos.add(map.get(acc.getId()));
	    			}else{
	    				EdocFormExtendInfo info = new EdocFormExtendInfo();
	        	    	info.setIdIfNew();
	        	    	info.setAccountId(acc.getId());
        	    		info.setIsDefault(false);
        	    		info.setStatus(com.seeyon.v3x.edoc.util.Constants.EDOC_USELESS);
	        	    	info.setEdocForm(ef);
	        	    	if(Strings.isNotBlank(order)){
	        	    		info.setOptionFormatSet(order);
	        	    	}else{
	        	    		info.setOptionFormatSet("0,0,0");
	        	    	}
	        	    	infos.add(info);
	    			}
	    		}
				}
		}catch (BusinessException e) {
			log.error(e);
		}
		//当前单位
    	EdocFormExtendInfo cinfo = map.get(currentAccount);
    	if(!isOnlyAcl && cinfo != null){//列表中授权的时候没有修改公文单属性.。
	    	cinfo.setIsDefault(ef.getIsDefault());
	    	cinfo.setStatus(ef.getStatus());
	    	if(Strings.isNotBlank(order))	cinfo.setOptionFormatSet(order);
    	}
    	if(!currentAccount.equals(ef.getDomainId())
    			&& !isAcl2CAccount){//制作单位没有被授权，并且不是当前登录单位的时候，手东加一条数据。
    		EdocFormExtendInfo ci  = map.get(ef.getDomainId());
    		if(ci!=null){
    			infos.add(ci);
    		}
    	}
    	
    	infos.add(cinfo);
    	return infos;
	}
    /**
     * 切换公文单，切换前先保存用户输入的数据，避免输入数据丢失
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
    public ModelAndView getEdocFormModel(HttpServletRequest request,HttpServletResponse response) throws Exception
	{
    	long formId=Long.parseLong(request.getParameter("edoctable"));
    	String strEdocId=request.getParameter("strEdocId");
    	long actorId=-1L;
    	EdocSummary edocSummary= new EdocSummary();;
    	if(Strings.isNotBlank(strEdocId)){
    		EdocSummary edocSummaryInDataSource=edocManager.getEdocSummaryById(Long.parseLong(strEdocId),false);
    		edocSummary=(EdocSummary)edocSummaryInDataSource.clone();
    		edocSummary.setEdocBodies(null);
    		edocSummary.setEdocOpinions(null);
    	}else{
    		DataUtil.requestToSummary(request,edocSummary,formId);
    	}
    	
    	actorId=Long.parseLong(request.getParameter("actorId"));
    	User user = CurrentUser.get();
//   	新建的时候建文人为空，为了切换文单和新建的时候保持一致，这里也取消掉显示建文人
//    	if(edocSummary.getCreatePerson()==null || edocSummary.getCreatePerson().trim().length()==0)
//    	{
//    		if(user!=null)edocSummary.setCreatePerson(user.getName());
//    	}
    	// 新建时切换文档显示发文单位
//    	if(edocSummary.getSendUnit()==null || edocSummary.getSendUnit().trim().length()==0)
//    	{
//    		edocSummary.setSendUnit(EdocRoleHelper.getAccountById(user.getLoginAccount()).getName());
//    		edocSummary.setSendUnitId("Account|"+Long.toString(user.getLoginAccount()));
//    	}
    	if(request.getParameter("edocType")!=null) {
    		try {
    			edocSummary.setEdocType(Integer.parseInt(request.getParameter("edocType")));
    		}catch(Exception e) {
    			log.error(e);
    		}
    	}
    	if(edocSummary.getStartTime() == null){
    		edocSummary.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
    	}
    	EdocFormModel formModel=null;    	  
    	//判断是模板调用还是后台管理员调用
    	String isEdocTemplete = request.getParameter("isEdocTempletePage");
    	if(Strings.isNotBlank(isEdocTemplete) && "true".equals(isEdocTemplete)) {
    		formModel= edocFormManager.getEdocFormModel(formId, edocSummary, actorId,true,false); 
    	}else{
    		formModel= edocFormManager.getEdocFormModel(formId, edocSummary, actorId,false,false); 
    	}
    	response.setContentType("text/xml; charset=utf-8");
		request.setCharacterEncoding("UTF-8");
    	response.getWriter().print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+ObjectToXMLUtil.objectToXML(formModel));
        //分支 开始
        request.getSession().setAttribute("SessionObject", edocFormManager.getElementByEdocForm(edocFormManager.getEdocForm(formId)));
        //分支 结束
    	return null;
	}
    

    public ModelAndView importForms(HttpServletRequest request,HttpServletResponse response)throws Exception{
    	
    	String formids=request.getParameter("formids");
    	edocFormManager.importEdocForm(formids);
    	response.getWriter().print("<script>parent.importOk();</script>");
    	return null;
    }
    
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		if(first>list.size()){
			first=0;
		}
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	private List<EdocForm> convertIMG(List<EdocForm> list){
		if(null!=list && list.size()>0){
 		for(EdocForm form : list){
			String content = form.getContent();
				content = convertContent(content,form.getShowLog());
				form.setContent(content);
			}
		return list;
		}else{
			return null;
		}
	}
	
	public ModelAndView operationChooseEntry(HttpServletRequest request, HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/formManage/operation_choose_iframe");
		
		String type = request.getParameter("type");
		String boundName = request.getParameter("boundName");
		mav.addObject("boundName", boundName);		
		mav.addObject("type", type);
		mav.addObject("permItem", request.getParameter("permItem"));
		
		return mav;
	}
	
	
	public ModelAndView operationChoose(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		User user = CurrentUser.get();
		
		ModelAndView mav = new ModelAndView("edoc/formManage/operation_choose");
		
		String type = request.getParameter("type");
		String boundName = request.getParameter("boundName");
		mav.addObject("boundName", boundName);
		mav.addObject("permItem", request.getParameter("permItem"));
		String category = "";
		if(null!=type && !"".equals(type)){
			if(Integer.valueOf(type).intValue() == EdocEnum.edocType.sendEdoc.ordinal()){
				category = MetadataNameEnum.edoc_send_permission_policy.name();
			}else if(Integer.valueOf(type).intValue() == EdocEnum.edocType.recEdoc.ordinal()){
				category = MetadataNameEnum.edoc_rec_permission_policy.name();
			}else if(Integer.valueOf(type).intValue() == EdocEnum.edocType.signReport.ordinal()){
				category = MetadataNameEnum.edoc_qianbao_permission_policy.name();
			}
		}
		
		List<FlowPerm> flowPermlist = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, user.getLoginAccount());
	
		mav.addObject("flowPermList", flowPermlist);	
		return mav;
	}
	
	private String convertContent(String content,boolean showLog){
		User user = CurrentUser.get();
		String replacement = "<div align=\"left\"><img src=\"/seeyon/apps_res/v3xmain/images/logo.gif\" /></div>";
		content = content.replace(replacement,"");
		
		//在修改方法中,首先进行一次替换,如果之前设置的为默认logo, 那么将logo置空
		replacement = EdocHelper.getLogoURL();
		content = content.replace(replacement,"");//因为这是恢复操作,所以使用replace方法,而其他用replaceFirst方法只进行第一次的替换
		//---
		
		if(showLog){
			content = content.replaceFirst("<xsl:template match=\"my:myFields\">", "<xsl:template match=\"my:myFields\">"+replacement);
		}//如果显示单位logo,替换代码
		return content;
	}

	
	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public FlowPermManager getFlowPermManager() {
		return flowPermManager;
	}

	public void setFlowPermManager(FlowPermManager flowPermManager) {
		this.flowPermManager = flowPermManager;
	}
public ModelAndView setDefaultForm(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		User user = CurrentUser.get();
		String statusId = request.getParameter("statusId");
		
		EdocFormExtendInfo eFormStatus = edocFormManager.getEdocFormExtendInfo(Long.valueOf(statusId));	//得到公文单
		if(null!=eFormStatus && eFormStatus.getIsDefault()){
			return super.refreshWindow("parent");//refresh parent window
		}
		edocFormManager.updateDefaultEdocForm(user.getLoginAccount(), eFormStatus.getEdocForm().getType()); //将符合条件的公文单设成非默认
		eFormStatus.setIsDefault(true);
		edocFormManager.setDefaultEdocForm(user.getLoginAccount(), eFormStatus.getEdocForm().getType(), eFormStatus.getEdocForm());//update the form data
		NotificationManager.getInstance().send(NotificationType.DefaultEdocFormReSet, new Object[]{eFormStatus.getEdocForm().getId(),user.getLoginAccount()});
		return super.refreshWindow("parent");//refresh parent window
	}



	public ModelAndView doAuthEdocForm(HttpServletRequest request,HttpServletResponse response)throws Exception{ 
		
		User user =CurrentUser.get();
		
		String auth = request.getParameter("auth");
		String[] ids = request.getParameterValues("id");
		if(ids != null && ids.length != 0) {
			for(String id :ids){
				EdocForm eForm = edocFormManager.getEdocForm(Long.valueOf(id));
				
				Set<EdocFormAcl> reqAcl = getEdocFormAclSetForCurrentForm(user.getLoginAccount(), auth, eForm);
				Set<EdocFormExtendInfo> infos =getInfos(eForm,reqAcl,user.getLoginAccount(),null,true);
				
				//绑定对象
				Set<EdocFormFlowPermBound> bounds  = eForm.getEdocFormFlowPermBound();
				//增加
				List<Long> accountId = getCompareEdocFormAclsAdd(eForm.getEdocFormAcls(), reqAcl);
				//删除
				List<Long> del = getCompareEdocFormAclsDel(eForm.getEdocFormAcls(), reqAcl);
				for(EdocFormFlowPermBound bound : bounds){
					for(Long aid :accountId){
						// 如果授权是原单位则不进行添加
						if (eForm.getDomainId().equals(aid))
							continue;
						boundOneSpecialAccount(bound.getProcessName(), bound.getFlowPermName(),
								bound.getFlowPermNameLabel(), bound.getSortType(), eForm,aid);
					}
					for(Long aid :del){
						// 不删除原单意见元素绑定
						if (!eForm.getDomainId().equals(aid))
							edocFormManager.deleteEdocFormFlowPermBoundByFormIdAndAccountId(eForm.getId(),aid);
					}
				}
				
				
				eForm.getEdocFormExtendInfo().clear();
				eForm.getEdocFormExtendInfo().addAll(infos);
				
				eForm.getEdocFormAcls().clear();
				eForm.getEdocFormAcls().addAll(reqAcl);
			
				edocFormManager.updateEdocForm(eForm);
			}
		}
		return super.refreshWindow("parent");
	}

}















