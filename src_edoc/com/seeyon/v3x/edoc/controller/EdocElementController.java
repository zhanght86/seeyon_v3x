package com.seeyon.v3x.edoc.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.controller.Constantform;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

import com.seeyon.v3x.edoc.domain.EdocElement;
import com.seeyon.v3x.edoc.domain.EdocStatCondObj;
import com.seeyon.v3x.edoc.domain.EdocStatDisObj;
import com.seeyon.v3x.edoc.manager.EdocElementManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.*;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class EdocElementController extends BaseController{

    private static final Log log = LogFactory.getLog(EdocElementController.class);

	private OrgManager orgManager;

	private MetadataManager metadataManager;

	private EdocElementManager edocElementManager;

	private FileToExcelManager fileToExcelManager;


	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public EdocElementManager getEdocElementManager() {
		return edocElementManager;
	}

	public void setEdocElementManager(EdocElementManager edocElementManager) {
		this.edocElementManager = edocElementManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/elementManage/element_list_main");
		if(request.getParameter("id")!=null)
			mav.addObject("id", request.getParameter("id"));
		return mav;
	}

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//处理查询条件
		List<EdocElement> list = null;
		String condition=request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		String statusSelect = request.getParameter("statusSelect");
		Integer startIndex = 0;
		Integer first = 0;
		Integer pageSize = 0;
		Integer listCount = 0;

		//有条件返回的查询,带分页
		if(StringUtils.isNotBlank(condition)){
			list = edocElementManager.getEdocElementsByContidion(condition,textfield,statusSelect,1);
		}
		//没有条件返回的查询
		else{
			listCount = edocElementManager.getAllEdocElementCount();
			Pagination.setRowCount(listCount);
			first = Pagination.getFirstResult();
			pageSize = Pagination.getMaxResults();
			if ((first+1) % pageSize == 0){
				startIndex = first / pageSize;
			}
			else{
				startIndex = first / pageSize + 1;
			}
			if (pageSize == 1) startIndex = (first+1) / pageSize;
				list = edocElementManager.getAllEdocElements(startIndex,pageSize);
		}

		ModelAndView ret = new ModelAndView("edoc/elementManage/element_list_iframe");
		if(StringUtils.isNotBlank(condition)){
			if (("elementStatus".equals(condition)) && (StringUtils.isNotBlank(statusSelect))) {
				ret.addObject("statusSelect", Integer.parseInt(statusSelect));
				ret.addObject("condition", condition);
			}else {
				ret.addObject("condition", condition);
				ret.addObject("textfield", textfield);
			}
		}
		else{
			ret.addObject("condition", null);
		}
		ret.addObject("list", list);
		ret.addObject("canEditEdocElements",EdocRoleHelper.canEditEdocElements());
		return ret;
	}

	/**
	 * 进入公文元素编辑界面。
	 */
	public ModelAndView editPage(HttpServletRequest request, HttpServletResponse response) throws Exception {

		EdocElement bean = null;
		String idStr = request.getParameter("id");

		bean = edocElementManager.getEdocElement(idStr);

		//List<Metadata> edocMetadata = metadataManager.getExtendMetadatas(ApplicationCategoryEnum.edoc);
		//Collection collection = metadataManager.getAllMetadatas();
		/*
		Collection collection = metadataManager.getAllSystemMetadatas();
		List<Metadata> edocMetadata = new ArrayList<Metadata>();
		if(null!=collection && collection.size()>0){
			edocMetadata.addAll(collection);
		}
		*/
		List<Metadata> edocMetadata = metadataManager.getAllSystemMetadatas();
		//metadataManager.getUserDefinedItem(bean.getMetadataId());
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		 String metadataName = ResourceBundleUtil.getString(resource, local, "edoc.element.chooseMetadata");

		if(bean.getMetadataId() !=null){

			if(metadataManager.getUserMetadata(bean.getMetadataId()) !=null){
				Metadata metadata = metadataManager.getUserMetadata(bean.getMetadataId()) ;
				metadataName = ResourceBundleUtil.getString(metadata.getResourceBundle(), local, metadata.getLabel());
			}

			if(Strings.isBlank(metadataName) && metadataManager.getMetadata(bean.getMetadataId()) !=null)
				metadataName = metadataManager.getMetadata(bean.getMetadataId()).getDescription();

		}
		ModelAndView mav = new ModelAndView("edoc/elementManage/type_create");
		mav.addObject("edocMetadata", edocMetadata);
		mav.addObject("bean", bean);
		mav.addObject("metadataName", metadataName);

		return mav;
	}

	/**
	 * 修改公文元素信息。
	 */
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {

		EdocElement bean = null;
		String idStr = request.getParameter("id");
		String status = request.getParameter("status");
		String name = request.getParameter("name");
		String metadataId = request.getParameter("metadataId");
		String metadataName = request.getParameter("metadataName");

		bean = edocElementManager.getEdocElement(idStr);

		if(null!=status && !"".equals(status)){
			bean.setStatus(Integer.valueOf(status));
		}

		if(!bean.getIsSystem()){//如果是系统元素，则不能更改名字，反之更改
			bean.setName(name);
		}
		if (StringUtils.isNotBlank(metadataId)) {
			bean.setMetadataId(Long.parseLong(metadataId));
			if(metadataManager.getMetadata(Long.parseLong(metadataId)) == null){
				String outMsg = Constantform.getString4CurrentUser("form.formenum.enums");
				String stratMsg =  Constantform.getString4CurrentUser("InputField.InputSelect.enumvaluenotexist");
	    		PrintWriter pw = response.getWriter();
	    		pw.println("<script>");
	    		pw.println("alert(\""+outMsg+metadataName+stratMsg+"\");");
	    		pw.println("history.back()");
	    		pw.println("</script>");
	    		return null;
			}

			metadataManager.refMetadata(Long.parseLong(metadataId), com.seeyon.v3x.common.metadata.Constants.METADATAITEM_ISSYSTEM_NO);
		}else if(bean.getIsSystem()){
			//如果是系统元素，则不对metadata做任何操作，已经将系统元素的关联代码初始化到数据中。
		}
		else {
			bean.setMetadataId(null);
		}
		edocElementManager.updateEdocElement(bean);
		NotificationManager.getInstance().send(NotificationType.EdocElementElementTable, new Object[]{String.valueOf(CurrentUser.get().getLoginAccount()),bean.getId()});

		//ModelAndView mav = new ModelAndView("edoc/elementManage/element_list_main");
		//mav.addObject("id", status);

		//return mav;
		PrintWriter out = response.getWriter();
		//out.println("<script>");
		//out.print("parent.parent.location.href=parent.parent.location.href;");
		//out.println("</script>");
		out.println("<script>");
		out.print("parent.parent.listFrame.location.reload(true);");
		out.print("parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";");
		out.println("</script>");

		return null;
	}

	public ModelAndView changeStatus(HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		String[] sIds = request.getParameterValues("id");
		int status = Integer.valueOf(request.getParameter("status"));

		if (sIds != null && sIds.length > 0) {
			for (int i = 0; i < sIds.length; i++) {
				EdocElement element = edocElementManager.getEdocElement(sIds[i]);
				element.setStatus(status);
				edocElementManager.updateEdocElement(element);
				NotificationManager.getInstance().send(NotificationType.EdocElementElementTable, new Object[]{String.valueOf(CurrentUser.get().getLoginAccount()),element.getId()});
			}
		}

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.print("parent.parent.listFrame.location.reload(true);");
		out.println("</script>");
		return null;
	}

	public ModelAndView exportElementToExcel(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String exportValue = request.getParameter("exportValue");
		String conditionValue=request.getParameter("conditionValue");
		String statusSelect = request.getParameter("statusSelectValue");
		List<EdocElement> elementList = null;
		if(null  == exportValue || "".equals(exportValue)){
			//无查询条件导出
			elementList = edocElementManager.getAllEdocElements();
		}else
		{
			//查询条件导出,不带分页
			elementList = edocElementManager.getEdocElementsByContidion(exportValue,conditionValue,statusSelect,0);
		}

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
		String element_title = ResourceBundleUtil.getString(resource, local, "edoc.element.code.reflection"); //标题

    	DataRecord dataRecord = EdocHelper.exportEdocElement(request, elementList, element_title);
		//OrganizationHelper.exportToExcel(request, response, fileToExcelManager, element_title, dataRecord);
    	EdocHelper.exportToExcel(request, response, fileToExcelManager, element_title, dataRecord);
    	return null;

	}

}
