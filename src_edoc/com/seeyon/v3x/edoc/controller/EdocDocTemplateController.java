package com.seeyon.v3x.edoc.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.edoc.domain.EdocDocTemplate;
import com.seeyon.v3x.edoc.domain.EdocDocTemplateAcl;
import com.seeyon.v3x.edoc.manager.EdocDocTemplateAclManager;
import com.seeyon.v3x.edoc.manager.EdocDocTemplateManager;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class EdocDocTemplateController extends BaseController{
	
	private static final Log log = LogFactory.getLog(EdocDocTemplateController.class);
	
	private EdocDocTemplateManager edocDocTemplateManager;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private EdocDocTemplateAclManager edocDocTemplateAclManager;
	private AppLogManager appLogManager;
	
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public EdocDocTemplateAclManager getEdocDocTemplateAclManager() {
		return edocDocTemplateAclManager;
	}
	public void setEdocDocTemplateAclManager(
			EdocDocTemplateAclManager edocDocTemplateAclManager) {
		this.edocDocTemplateAclManager = edocDocTemplateAclManager;
	}
	public EdocDocTemplateManager getEdocDocTemplateManager() {
		return edocDocTemplateManager;
	}
	public void setEdocDocTemplateManager(
			EdocDocTemplateManager edocDocTemplateManager) {
		this.edocDocTemplateManager = edocDocTemplateManager;
	}
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("edoc/docTemplate/docTemplate_list_main");
		if(request.getParameter("id")!=null)
			mav.addObject("id", request.getParameter("id"));
		return mav;
	}
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null);

		ModelAndView mav = new ModelAndView("edoc/docTemplate/docTemplate_list_iframe");
		List<EdocDocTemplate> list=null;
		
		String expressionType = request.getParameter("expressionType");
		String expressionValue = request.getParameter("expressionValue");
		//处理按条件查询
		if(Strings.isNotBlank(expressionType) && Strings.isNotBlank(expressionValue)){
			
			list = edocDocTemplateManager.findTemplateByVariable(expressionType, expressionValue);
				
		}else {
			list = edocDocTemplateManager.findAllTemplate();
		}
		
		
		mav.addObject("list", pagenate(list));
		
		//套红模板的文件
		V3XFile v3xfile=fileManager.getV3XFile(new Long("-6001972826857714844"));
		
		if(v3xfile!=null){
		mav.addObject("fileId",new Long("-6001972826857714844"));
		mav.addObject("fileName", v3xfile.getFilename());
		mav.addObject("createDate", new Timestamp(v3xfile.getCreateDate().getTime()).toString().substring(0, 10));		
		}
		
		return mav;
	}
	
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		EdocDocTemplate bean = null;
		Attachment attachment = null;
		String idStr=request.getParameter("id");
		
		bean = edocDocTemplateManager.getEdocDocTemplateById(Long.parseLong(idStr));
		if(null==bean)return null;
		ModelAndView mav = new ModelAndView("edoc/docTemplate/docTemplate_modify");
		mav.addObject("type", bean.getType());
		List<Attachment> attachments = attachmentManager.getByReference(Long.valueOf(idStr), Long.valueOf(idStr));
		List<EdocDocTemplateAcl> edocDocTemplateAcl = edocDocTemplateAclManager.getEdocDocTemplateAcl(idStr);
		if(null!=attachments && attachments.size()>0){//判断附件是否为空
		attachment = attachments.get(0);
		mav.addObject("attachments",attachments);	
		mav.addObject("fileName",attachment.getFilename());
		mav.addObject("fileId", attachment.getFileUrl());
		mav.addObject("fileName", attachment.getFilename());
		mav.addObject("createDate", new Timestamp(attachment.getCreatedate().getTime()).toString().substring(0, 10));
		}
		mav.addObject("bean", bean);
		if(null!=edocDocTemplateAcl && edocDocTemplateAcl.size()>0){
		mav.addObject("elements",edocDocTemplateAcl);
		}
		
		mav.addObject("operType", "change");

		return mav;
	}
	public ModelAndView change(HttpServletRequest request,HttpServletResponse response)throws Exception{		
		
		PrintWriter out = response.getWriter();
		ModelAndView mav = new ModelAndView("edoc/docTemplate/docTemplate_list_main");
		User user =CurrentUser.get();
		EdocDocTemplate bean = null;
		
		String idStr=request.getParameter("id");
		String name = request.getParameter("name");
		
		String status = request.getParameter("status");
		String departmentId = request.getParameter("grantedDepartId");
		String textType = request.getParameter("text_type");
		
		if(!Strings.isBlank(departmentId)){
			
		String[] departmentIds = departmentId.split(",");

			edocDocTemplateAclManager.updateEdocDocTemplateAcl(Long.valueOf(idStr),Long.valueOf(idStr), departmentIds);
		}else{
			edocDocTemplateAclManager.deleteAclByTemplateId(Long.valueOf(idStr));
		}
		//记录日志
		appLogManager.insertLog(user, AppLogAction.Edoc_DocTempleteAuthorize, user.getName(), name);
		bean = edocDocTemplateManager.getEdocDocTemplateById(Long.parseLong(idStr));
		int type = bean.getType();
		//-start-如果启用已经被停用的模板 ? enabled = true : = false;
		//boolean enabled = false; //判断是否是从禁用-->启用,如果是:判断有没有同名的模板
		boolean hasName = false; //是否重名
		/*if(bean.getStatus() == Constants.EDOC_DOCTEMPLATE_DISABLED && Integer.valueOf(status) == Constants.EDOC_DOCTEMPLATE_ENABLED){
			enabled = true;
		}
		if(enabled){*/
		hasName = edocDocTemplateManager.checkHasName(type, name,Long.valueOf(idStr),CurrentUser.get().getLoginAccount());
		if(hasName){
			out.println("<script>");
			out.println("alert(parent._('edocLang.templete_alertRepeatName'));");
			out.println("</script>");
			out.close();
			return super.refreshWindow("parent.parent");
		}
		//}
		//-end-
		
		List<Attachment> attachments = attachmentManager.getByReference(Long.valueOf(idStr), Long.valueOf(idStr));
		if(attachments!=null){
			attachmentManager.update(ApplicationCategoryEnum.edoc, Long.valueOf(idStr), Long.valueOf(idStr), request);
		}else{
			attachmentManager.create(ApplicationCategoryEnum.edoc, Long.valueOf(idStr), Long.valueOf(idStr), request);
		}
		//bean.setType(Integer.parseInt(type));不能修改模版的类型
		bean.setStatus(Integer.parseInt(status));
		bean.setTextType(textType);
		
		String alert = edocDocTemplateManager.modifyEdocTemplate(bean, name);		
		if(null!=alert && !"".equals(alert)){
			out.println(alert);
			return super.refreshWindow("parent.parent");
		}

		mav.addObject("id",idStr);
		return super.refreshWindow("parent.parent");
	}
	
	
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		String id = request.getParameter("id");
		
		String[] ids = id.split(",");
		
		List<Long> list = new ArrayList<Long>();
		for(int i=0;i<ids.length;i++){
			list.add(Long.valueOf(ids[i]));
		}		
		
		edocDocTemplateManager.deleteEdocTemtlate(list);
		
		return super.refreshWindow("parent");

	}

	public ModelAndView newTemplate(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/docTemplate/docTemplate_modify");
		
		mav.addObject("operType", "add");
		mav.addObject("type", request.getParameter("type"));
		
		return mav;

	}
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response)throws Exception{

		
		PrintWriter out = response.getWriter();

		User user = CurrentUser.get();
		
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String status = request.getParameter("status");
		String departmentId = request.getParameter("grantedDepartId");	
		String textType = request.getParameter("text_type");
		
		String description = "";
	
		Long templateId = 1L;
		
		//--end
		
		EdocDocTemplate template = new EdocDocTemplate();
		template.setIdIfNew();
		template.setDescription(description);
		template.setName(name);
		template.setType(Integer.valueOf(type));
		template.setStatus(Integer.parseInt(status));
		template.setCreateUserId(user.getId());
		template.setDomainId(user.getLoginAccount());
		template.setCreateTime(new java.sql.Timestamp(new Date().getTime()));
		template.setLastUpdate(new java.sql.Timestamp(new Date().getTime()));
		template.setLastUserId(user.getLoginAccount());
		template.setTemplateFileId(templateId);
		template.setTextType(textType);
		
		try{
			attachmentManager.create(ApplicationCategoryEnum.edoc, template.getId(), template.getId(), request);
		}catch(Exception e){
//			log.error("保存模板记录失败",e);
			ResourceBundle r = ResourceBundle.getBundle("com.seeyon.v3x.edoc.resources.i18n.EdocResource",CurrentUser.get().getLocale());
			String alertNote = ResourceBundleUtil.getString(r, "templete.saved.error");
						
			out.println("<script>alert('"+alertNote+"');</script>");
			return super.refreshWindow("parent.parent");			
		}
		
		String alert = edocDocTemplateManager.addEdocTemplate(template);

		if(null!=alert && !"".equals(alert)){
			try {
				attachmentManager.deleteByReference(template.getId());
			}catch(Exception e) {
				log.error(e);
			}
			out.println(alert);
			out.close();
			return super.refreshWindow("parent");
		}
		
		if(null!=departmentId && !"".equals(departmentId)){
			String[] departmentIds = departmentId.split(",");			
				
				edocDocTemplateAclManager.saveEdocDocTemplateAcl(template.getId(),template.getId(), departmentIds);
//				记录日志
				appLogManager.insertLog(user, AppLogAction.Edoc_DocTempleteCreate, user.getName(), name);
			//	edocDocTemplateAclManager.saveEdocDocTemplateAcl(Long.valueOf(idStr),Long.valueOf(idStr), departmentIds);
			
		}
		
		return super.refreshWindow("parent.parent");
	}
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView taoHong(HttpServletRequest request,HttpServletResponse response)throws Exception{
		User user = CurrentUser.get();
		
		String edocType = request.getParameter("templateType");
		String bodyType = request.getParameter("bodyType");
		//公文所属单位
		String orgAccountId = request.getParameter("orgAccountId");
		
		ModelAndView mav = new ModelAndView("edoc/docTemplate/docTemplate_taohong");	
		
		List<EdocDocTemplate> list = getEdocDocTemplate(Long.parseLong(orgAccountId),user,edocType,bodyType);
	
		
		if(null==list || list.size()==0){
			mav.addObject("haveRecord", true);
			return mav;
		}
				
		mav.addObject("templateList", list);
		
		return mav;
	}	
	/**
	 * Ajax前台页面调用，判断是否存在套红模板
	 * @param edocType 类型（正文/文单）
	 * @param bodyType Officeword:word正文/Wpsword:wps正文
	 * @return "0":没有套红模板，“1”：有套红模板
	 */
	public String hasEdocDocTemplate(Long orgAccountId,String edocType,String bodyType){
		String ret="";
		User user = CurrentUser.get();
		try{
			List<EdocDocTemplate> list = getEdocDocTemplate(orgAccountId,user,edocType,bodyType);

			if(null==list || list.size()==0) ret="0";
			else ret="1";
		}catch(Exception e){
			StringBuffer parameter=new StringBuffer();
			parameter.append("(");
			parameter.append("edocType=").append(edocType);
			parameter.append("bodyType=").append(bodyType);
			parameter.append("userId=").append(user.getId());
			parameter.append(")");
			log.error("ajax获取套红模板列表异常："+parameter.toString()+e.getMessage());
		}
		return ret;
	}
	/**
	 * 获取能够使用的模板，过滤掉停用的。
	 * @param user     ：用户
	 * @param edocType ：类型（正文/文单）
	 * @param bodyType : Officeword:word正文/Wpsword:wps正文

	 * @return
	 * @throws Exception
	 */
	private List<EdocDocTemplate> getEdocDocTemplate(Long orgAccountId,User user, String edocType, String bodyType)
			throws Exception {

		List<EdocDocTemplate> list = new ArrayList<EdocDocTemplate>();
		orgAccountId = V3xOrgEntity.VIRTUAL_ACCOUNT_ID;
		if (null != edocType && "edoc".equals(edocType)) {
			list = edocDocTemplateManager.findGrantedListForTaoHong(orgAccountId,user
					.getId(), Constants.EDOC_DOCTEMPLATE_WORD, bodyType
					.toLowerCase());
		} else if (null != edocType && "script".equals(edocType)) {
			list = edocDocTemplateManager.findGrantedListForTaoHong(orgAccountId,user
					.getId(), Constants.EDOC_DOCTEMPLATE_SCRIPT, bodyType
					.toLowerCase());
		} else {
			list = edocDocTemplateManager.findAllTemplate();
		}

		// 过滤掉停用状态的
		Set<Long> ids = new HashSet<Long>();
		List<EdocDocTemplate> list2 = new ArrayList<EdocDocTemplate>();
		for (EdocDocTemplate t : list) {
			if (t.getStatus() == 1 && !ids.contains(t.getId())){
				list2.add(t);
				ids.add(t.getId());
			}
				
		}

		return list2;
	}
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView taoHongEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView  mav = new ModelAndView("edoc/docTemplate/docTemplate_taohong_iframe");
		mav.addObject("templateType", request.getParameter("templateType"));
		mav.addObject("bodyType", request.getParameter("bodyType"));
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
	public FileManager getFileManager() {
		return fileManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
}