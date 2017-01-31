package com.seeyon.v3x.edoc.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.EdocEnum.MarkCategory;
import com.seeyon.v3x.edoc.domain.EdocInnerMarkDefinition;
import com.seeyon.v3x.edoc.domain.EdocMarkAcl;
import com.seeyon.v3x.edoc.domain.EdocMarkCategory;
import com.seeyon.v3x.edoc.domain.EdocMarkDefinition;
import com.seeyon.v3x.edoc.manager.EdocInnerMarkDefinitionManager;
import com.seeyon.v3x.edoc.manager.EdocMarkAclManager;
import com.seeyon.v3x.edoc.manager.EdocMarkCategoryManager;
import com.seeyon.v3x.edoc.manager.EdocMarkDefinitionManager;
import com.seeyon.v3x.edoc.manager.EdocMarkManager;
import com.seeyon.v3x.edoc.manager.EdocSwitchHelper;
import com.seeyon.v3x.edoc.util.Constants;
import com.seeyon.v3x.edoc.webmodel.EdocMarkModel;
import com.seeyon.v3x.edoc.webmodel.EdocMarkNoModel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.SetContentType;

public class EdocDocMarkController extends BaseController {

	private static final Log log = LogFactory.getLog(EdocDocMarkController.class);

	private EdocMarkCategoryManager edocMarkCategoryManager;

	private EdocMarkDefinitionManager edocMarkDefinitionManager;

	private EdocMarkAclManager edocMarkAclManager;

	private EdocMarkManager edocMarksManager;
	
	private EdocInnerMarkDefinitionManager edocInnerMarkDefinitionManager;
	
	private OrgManager orgManager;
	
	private String jsonView;
	
	private AppLogManager appLogManager;
	
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public EdocMarkAclManager getEdocMarkAclManager() {
		return edocMarkAclManager;
	}

	public void setEdocMarkAclManager(EdocMarkAclManager edocMarkAclManager) {
		this.edocMarkAclManager = edocMarkAclManager;
	}

	public EdocMarkCategoryManager getEdocMarkCategoryManager() {
		return edocMarkCategoryManager;
	}

	public void setEdocMarkCategoryManager(
			EdocMarkCategoryManager edocMarkCategoryManager) {
		this.edocMarkCategoryManager = edocMarkCategoryManager;
	}

	public EdocMarkDefinitionManager getEdocMarkDefinitionManager() {
		return edocMarkDefinitionManager;
	}

	public void setEdocMarkDefinitionManager(
			EdocMarkDefinitionManager edocMarkDefinitionManager) {
		this.edocMarkDefinitionManager = edocMarkDefinitionManager;
	}

	public EdocMarkManager getEdocMarksManager() {
		return edocMarksManager;
	}

	public void setEdocMarksManager(EdocMarkManager edocMarksManager) {
		this.edocMarksManager = edocMarksManager;
	}
	
	public EdocInnerMarkDefinitionManager getEdocInnerMarkDefinitionManager() {
		return edocInnerMarkDefinitionManager;
	}

	public void setEdocInnerMarkDefinitionManager(
			EdocInnerMarkDefinitionManager edocInnerMarkDefinitionManager) {
		this.edocInnerMarkDefinitionManager = edocInnerMarkDefinitionManager;
	}
	
	public String getJsonView() {
		return jsonView;
	}
	
	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}	
	
	/**------------------------ 公文文号管理Start -----------------------------**/
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView listMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/mark_list_main");
		return mav;
	}

	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/mark_list_iframe");
		User user = CurrentUser.get();
		
		String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        
		List<EdocMarkModel> markNoList = edocMarkDefinitionManager.getEdocMarkDefs(user.getLoginAccount(), condition, textfield);

		mav.addObject("markNoList", pagenate(markNoList));
		return mav;
	}
	
	/**
	 * 进入新建公文文号界面。
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView addMarkPage(HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/addMark");
		Calendar cal = Calendar.getInstance();
		Integer yearNo = cal.get(Calendar.YEAR);				
		mav.addObject("yearNo", yearNo);
		
		Long domainId = CurrentUser.get().getAccountId();
		List<EdocMarkCategory> categories = edocMarkCategoryManager.getEdocMarkCategories(domainId);		
		mav .addObject("categories", categories);
		
		return mav;
	}
	/**
	 * 进入手动输入内部文号页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView serialNoInputEntry(HttpServletRequest request, HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/handInputSerialNo");
		
		return mav;
		
	}

	
	/**
	 * 新建公文文号定义。 
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView createMark(HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		PrintWriter out = response.getWriter();
		User user=CurrentUser.get();
		try {
			Long domainId = user.getLoginAccount();
			String wordNo = request.getParameter("wordNo");		
			String markType=request.getParameter("markType");
			int imarkType=0;
			if(markType!=null && !"".equals(markType))	
				imarkType=Integer.parseInt(markType);
			// 验证公文字号是否重名
			Boolean flag = edocMarkDefinitionManager.containEdocMarkDefinition(wordNo, domainId,imarkType);
			if (flag) {
				throw new Exception("edocLang.mark_alter_word_no_used");
			}
			Integer length = 0;			
			Boolean fixedLength = false;
			if (request.getParameter("fixedLength") != null) {
				fixedLength = true;
			}
			short mode = Short.valueOf(request.getParameter("flowNoType")); //0-小流水；1-大流水
			String expression = request.getParameter("markNo");												
			Integer currentNo = new Integer(0);
			EdocMarkCategory edocMarkCategory = null;
			if (mode == Constants.EDOC_MARK_CATEGORY_BIGSTREAM) {
				Long categoryId = Long.valueOf(request.getParameter("categoryId"));
				edocMarkCategory = edocMarkCategoryManager.findById(categoryId);
				edocMarkCategory.setReadonly(true);
				//大流水的时候页面disabled,不能直接获取参数。
				currentNo=edocMarkCategory.getCurrentNo();
			}
			else {
				Integer minNo = Integer.valueOf(request.getParameter("minNo"));
				Integer maxNo = Integer.valueOf(request.getParameter("maxNo"));
				currentNo = Integer.valueOf(request.getParameter("currentNo"));
				Boolean yearEnabled = false;
				if (request.getParameter("yearEnabled") != null) {
					yearEnabled = true;
				}
				edocMarkCategory = new EdocMarkCategory();
				edocMarkCategory.setIdIfNew();
				edocMarkCategory.setCategoryName(wordNo);
				edocMarkCategory.setMinNo(minNo);
				edocMarkCategory.setMaxNo(maxNo);
				edocMarkCategory.setCurrentNo(currentNo);
				edocMarkCategory.setYearEnabled(yearEnabled);
				edocMarkCategory.setCodeMode(Constants.EDOC_MARK_CATEGORY_SMALLSTREAM);
				edocMarkCategory.setReadonly(true);
				edocMarkCategory.setDomainId(domainId);			
			}
			edocMarkCategoryManager.saveCategory(edocMarkCategory);
			
			EdocMarkDefinition edocMarkDef = new EdocMarkDefinition();
			if(markType!=null&&!"".equals(markType)){
				edocMarkDef.setMarkType(Integer.parseInt(markType));
			}
			edocMarkDef.setIdIfNew();
			edocMarkDef.setWordNo(wordNo);
			edocMarkDef.setEdocMarkCategory(edocMarkCategory);			
			if (fixedLength) {
				length = String.valueOf(edocMarkCategory.getMaxNo()).length();			
			}
			edocMarkDef.setLength(length);		
			edocMarkDef.setExpression(expression);				
			edocMarkDef.setDomainId(domainId);
			edocMarkDef.setEdocMarkCategory(edocMarkCategory);
			edocMarkDef.setStatus(Constants.EDOC_MARK_DEFINITION_DRAFT);		
			
			// 保存公文文号授权信息
			String deptIds = request.getParameter("grantedDepartId");
			Set<EdocMarkAcl> markAcls = new HashSet<EdocMarkAcl>();
			if (deptIds != null && !deptIds.trim().equals("")) {
				String[] aDeptId = deptIds.split(",");
				for (String deptId : aDeptId) {
					EdocMarkAcl edocMarkAcl = new EdocMarkAcl();
					edocMarkAcl.setIdIfNew();
					String[] bDeptId = deptId.split("\\|");
					edocMarkAcl.setAclType(bDeptId[0]);
					edocMarkAcl.setDeptId(Long.valueOf(bDeptId[1]));
					edocMarkAcl.setEdocMarkDefinition(edocMarkDef);
					markAcls.add(edocMarkAcl);
				}
			}
			edocMarkDef.setEdocMarkAcls(markAcls);
			edocMarkDefinitionManager.saveMarkDefinition(edocMarkDef);
			
//			记录应用日志，安全权限
			Calendar cal = Calendar.getInstance();
			String yearNo = String.valueOf(cal.get(Calendar.YEAR)); 	
			EdocMarkModel model = edocMarkDefinitionManager.markDef2Mode(edocMarkDef,yearNo,currentNo);
			appLogManager.insertLog(user, AppLogAction.Edoc_Mark_Create, user.getName(),model.getMark());
			
			out.print("<script>");
			out.print("parent.parent.location.reload(true)");
			out.print("</script>");
		}
		catch (Exception e) {
			out.print("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'))");
			out.print("</script>");
		}
		
		
		return null;
	}
		
	/**
	 * 进入修改公文文号界面。 
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView editMarkPage(HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/editMark");
		Long id = Long.valueOf(request.getParameter("id"));
		EdocMarkDefinition markDef = edocMarkDefinitionManager.getMarkDefinition(id);
		
		List<EdocMarkAcl> edocDocTemplateAcl = edocMarkAclManager.getMarkAclById(id);
		
		
		
		// 组装公文文号授权的相关信息
		Set<EdocMarkAcl> markAcls = markDef.getEdocMarkAcls();		
		Iterator<EdocMarkAcl> iterator = markAcls.iterator();
		String deptIds = "";
		String deptNames = "";
		int count = markAcls.size();
		int temp = 0;		
		while (iterator.hasNext()) {
			EdocMarkAcl acl = iterator.next();
			long entityId = acl.getDeptId();
			V3xOrgEntity orgEntity = orgManager.getEntity(acl.getAclType(), entityId);
			if (orgEntity != null) {				
				if (temp != count - 1 ) {
					deptIds += entityId + ",";
					deptNames += orgEntity.getName() + "、";
				}
				else {
					deptIds += entityId;
					deptNames += orgEntity.getName();
				}
			}	
			else
			{
				edocDocTemplateAcl.remove(acl);
			}
			temp++;
		}
		mav.addObject("elements", edocDocTemplateAcl);
		Long domainId = CurrentUser.get().getAccountId();
		List<EdocMarkCategory> categories = edocMarkCategoryManager.getEdocMarkCategories(domainId);
		
		Calendar cal = Calendar.getInstance();
		Integer yearNo = cal.get(Calendar.YEAR);
		mav.addObject("yearNo", yearNo);
		mav.addObject("categories", categories);
		mav.addObject("markDef", markDef);
		mav.addObject("deptIds", deptIds);
		mav.addObject("deptNames", deptNames);
		String expression = markDef.getExpression();
		String formatA = "";		
		Boolean yearEnabled = markDef.getEdocMarkCategory().getYearEnabled();
		if (yearEnabled) {
			formatA = expression.substring(expression.indexOf("$WORD") + 5, expression.indexOf("$YEAR"));
		}
		String formatB = "";
		if (yearEnabled) {
			formatB = expression.substring(expression.indexOf("$YEAR") + 5, expression.indexOf("$NO"));
		}
		else {
			formatB = expression.substring(5, expression.indexOf("$NO"));
		}
		String formatC = expression.substring(expression.indexOf("$NO") + 3);
		mav.addObject("formatA", formatA);
		mav.addObject("formatB", formatB);
		mav.addObject("formatC", formatC);
		
		return mav;
	}
	
	/**
	 * 修改公文文号定义。 
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView updateMark(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		PrintWriter out = response.getWriter();
		User user =CurrentUser.get();
		try {
			Long domainId = CurrentUser.get().getLoginAccount();
			Long id = Long.valueOf(request.getParameter("id"));
			String wordNo = request.getParameter("wordNo");
			EdocMarkDefinition edocMarkDef = edocMarkDefinitionManager.getMarkDefinition(id);
			int imarkType=0;
			if(edocMarkDef!=null && edocMarkDef.getMarkType()!=null) 
				imarkType=edocMarkDef.getMarkType().intValue();
			// 验证公文字号是否重名			
			Boolean flag = edocMarkDefinitionManager.containEdocMarkDefinition(id, wordNo, domainId,imarkType);
			if (flag) {
				throw new Exception("edocLang.mark_alter_word_no_used");
			}
			Integer length = 0;		
			short mode = Short.valueOf(request.getParameter("flowNoType")); //0-小流水；1-大流水	
			short oldMode = Short.valueOf(request.getParameter("oldCodeMode")); //修改之前的编号方式
			String expression = request.getParameter("markNo");
			Boolean fixedLength = false;
			if (request.getParameter("fixedLength") != null) {
				fixedLength = true;
			}	
			Boolean modeChanged = false;
			if (oldMode != mode) {
				modeChanged = true;
			}
			edocMarkAclManager.deleteByDefId(id);
			
			
			EdocMarkCategory edocMarkCategory = null;
			Integer currentNo = new Integer(0);
			if (mode == Constants.EDOC_MARK_CATEGORY_BIGSTREAM) {
				Long categoryId = Long.valueOf(request.getParameter("categoryId")); 
				edocMarkCategory = edocMarkCategoryManager.findById(categoryId);
				edocMarkCategory.setReadonly(true);
				//大流水的时候页面disabled,不能直接获取参数。
				currentNo=edocMarkCategory.getCurrentNo();
//				if (modeChanged) {
//					Long oldCategoryId = edocMarkDef.getEdocMarkCategory().getId();
//					edocMarkCategoryManager.deleteCategory(oldCategoryId);
//				}
			}
			else {
				Integer minNo = Integer.valueOf(request.getParameter("minNo"));
				Integer maxNo = Integer.valueOf(request.getParameter("maxNo"));
				currentNo = Integer.valueOf(request.getParameter("currentNo"));
				Boolean yearEnabled = false;
				if (request.getParameter("yearEnabled") != null) {
					yearEnabled = true;
				}
				if (modeChanged) {
					edocMarkCategory = new EdocMarkCategory();
					edocMarkCategory.setIdIfNew();
					edocMarkCategory.setCategoryName(wordNo);
					edocMarkCategory.setMinNo(minNo);
					edocMarkCategory.setMaxNo(maxNo);
					edocMarkCategory.setCurrentNo(currentNo);
					edocMarkCategory.setYearEnabled(yearEnabled);
					edocMarkCategory.setCodeMode(Constants.EDOC_MARK_CATEGORY_SMALLSTREAM);
					edocMarkCategory.setReadonly(true);					
					edocMarkCategory.setDomainId(domainId);		
				}
				else {
					edocMarkCategory = edocMarkDef.getEdocMarkCategory();
					edocMarkCategory.setMinNo(minNo);
					edocMarkCategory.setMaxNo(maxNo);
					edocMarkCategory.setCurrentNo(currentNo);
					edocMarkCategory.setYearEnabled(yearEnabled);
				}			
			}
			edocMarkCategoryManager.saveCategory(edocMarkCategory);
					
			edocMarkDef.setEdocMarkCategory(edocMarkCategory);
			edocMarkDef.setWordNo(wordNo);
			edocMarkDef.setExpression(expression);
			if (fixedLength) {
				length = String.valueOf(edocMarkCategory.getMaxNo()).length();			
			}
			edocMarkDef.setLength(length);
			
			// 保存公文文号授权信息
			String deptIds = request.getParameter("grantedDepartId");
			Set<EdocMarkAcl> markAcls = new HashSet<EdocMarkAcl>();
			if (deptIds != null && !deptIds.trim().equals("")) {
				String[] aDeptId = deptIds.split(",");
				for (String deptId : aDeptId) {
					EdocMarkAcl edocMarkAcl = new EdocMarkAcl();
					edocMarkAcl.setIdIfNew();
					String[] bDeptId = deptId.split("\\|");
					edocMarkAcl.setAclType(bDeptId[0]);
					edocMarkAcl.setDeptId(Long.valueOf(bDeptId[1]));
					edocMarkAcl.setEdocMarkDefinition(edocMarkDef);
					markAcls.add(edocMarkAcl);
				}
			}		
			edocMarkDef.setEdocMarkAcls(markAcls);	
			
			//记录应用日志，安全权限
			Calendar cal = Calendar.getInstance();
			String yearNo = String.valueOf(cal.get(Calendar.YEAR)); 	
			EdocMarkModel model = edocMarkDefinitionManager.markDef2Mode(edocMarkDef,yearNo,currentNo);
			appLogManager.insertLog(user, AppLogAction.Edoc_MarkAuthorize, user.getName(),model.getMark());
			
			edocMarkDefinitionManager.saveMarkDefinition(edocMarkDef);
			
			out.print("<script>");
			out.print("parent.parent.location.reload(true)");
			out.print("</script>");
		}
		catch (Exception e) {
			out.print("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'))");
			out.print("</script>");
		}
		
		return null;
	}
	
	// 删除公文文号定义
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView deleteMark(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		PrintWriter out = response.getWriter();
		String[] ids = request.getParameterValues("markDefId");
		for (int i = 0; i < ids.length; i++) {
			Long id = Long.valueOf(ids[i]);
			try {				
				edocMarkDefinitionManager.logicalDeleteMarkDefinition(id, Constants.EDOC_MARK_DEFINITION_DELETED);
			}catch (Exception exception) {
				//如果文号已被使用，则进行逻辑删除
				log.error("删除公文文号出错", exception);
			}
		}		
		out.println("<script>");
		out.println("parent.parent.location.reload(true);");
		out.println("</script>");
		return null;		
	}
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView manageBigStreamIframe(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/bigStreamListIframe");
		return mav;
	}
	
	// 进入大流水号管理界面
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView manageBigStreamPage(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		Long domainId = CurrentUser.get().getLoginAccount();
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/bigStreamList");
		
//		List<EdocMarkCategory> categories = edocMarkCategoryManager.findByTypeAndDomainId(Constants.EDOC_MARK_CATEGORY_BIGSTREAM, domainId);
		List<EdocMarkCategory> categories = edocMarkCategoryManager.findByPage(Constants.EDOC_MARK_CATEGORY_BIGSTREAM, domainId); //增加分页
		mav.addObject("categories", categories);
				
		return mav;
	}
		
	// 进入新建大流水号界面
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView addBigStreamPage(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/addBigStream");
		return mav;
	}
		
	/**
	 * 创建公文大流水号。
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView createBigStream(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		PrintWriter out = response.getWriter();
		try {
			Long domainId = CurrentUser.get().getLoginAccount();
			String name = request.getParameter("name");
			boolean flag = edocMarkCategoryManager.containEdocMarkCategory(name, domainId);
			if (flag) {
				throw new Exception("edocLang.big_stream_alter_name_used");
			}
			Integer minNo = Integer.valueOf(request.getParameter("minNo"));
			Integer maxNo = Integer.valueOf(request.getParameter("maxNo"));
			Integer currentNo = Integer.valueOf(request.getParameter("currentNo"));
			Boolean yearEnabled = true;
			Integer iYearEnabled = Integer.valueOf(request.getParameter("yearEnabled"));
			if (iYearEnabled == 0) {
				yearEnabled = false;
			}
			EdocMarkCategory category = new EdocMarkCategory();
			category.setIdIfNew();
			category.setCategoryName(name);
			category.setMinNo(minNo);
			category.setMaxNo(maxNo);
			category.setCurrentNo(currentNo);
			category.setYearEnabled(yearEnabled);
			category.setReadonly(false);
			category.setCodeMode(Constants.EDOC_MARK_CATEGORY_BIGSTREAM);			
			category.setDomainId(domainId);
			edocMarkCategoryManager.saveCategory(category);
						
			out.println("<script>");
			out.println("parent.window.returnValue=\"true\";");
			out.println("parent.window.close();");
			out.println("</script>");
		}
		catch (Exception e) {
			out.println("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'))");
			out.println("</script>");
		}
		
		return null;
	}
	
	// 进入修改流水号界面
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView editBigStreamPage(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/editBigStream");
		Long id = Long.valueOf(request.getParameter("id"));		
		EdocMarkCategory category = edocMarkCategoryManager.findById(id);
		mav.addObject("category", category);
		return mav;
	}
	
	/**
	 * 修改公文大流水号。
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView updateBigStream(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		PrintWriter out = response.getWriter();
		try {
			Long id = Long.valueOf(request.getParameter("id"));
			String name = request.getParameter("name");
			Long domainId = CurrentUser.get().getLoginAccount();
			boolean flag = edocMarkCategoryManager.containEdocMarkCategory(id, name, domainId);
			if (flag) {
				throw new Exception("edocLang.big_stream_alter_name_used");
			}
			Integer minNo = Integer.valueOf(request.getParameter("minNo"));
			Integer maxNo = Integer.valueOf(request.getParameter("maxNo"));
			Integer currentNo = Integer.valueOf(request.getParameter("currentNo"));
			Boolean yearEnabled = true;
			
			//判断页面的yearEnabled是否为空,如果为空,那么yearEnabled在页面被disabled,即不可以修改
			String s_iYearEnabled = request.getParameter("yearEnabled");
			Integer iYearEnabled = null;
			if(null!=s_iYearEnabled){
					iYearEnabled = Integer.valueOf(s_iYearEnabled);
				if (iYearEnabled == 0) {
					yearEnabled = false;
				}
			}
			
			EdocMarkCategory category = edocMarkCategoryManager.findById(id);
			Boolean readonly = category.isReadonly();
			category.setCategoryName(name);
			category.setMinNo(minNo);
			category.setMaxNo(maxNo);
			category.setCurrentNo(currentNo);
			//添加了一个判断, null!=s_iYearEnabled,判断s_iYearEnabled是否为空,如果为空，那么就不用修改category.yearEnabled的状态
			if (!readonly && null!=s_iYearEnabled) {
				category.setYearEnabled(yearEnabled);
			}
			edocMarkCategoryManager.saveCategory(category);
					
			out.println("<script>");	
			out.println("parent.window.returnValue=\"true\";");
			out.println("parent.window.close();");
			out.println("</script>");
		}
		catch (Exception e) {
			out.println("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'))");
			out.println("</script>");
		}
		return null;
	}
		
	/**
	 * 删除公文大流水号。
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView deleteBigStream(HttpServletRequest request, HttpServletResponse response)
		throws Exception {		
		String[] ids = request.getParameterValues("categoryId");
		PrintWriter out = response.getWriter();
		for (int i = 0; i < ids.length; i++) {
			Long id = Long.valueOf(ids[i]);
			EdocMarkCategory category = edocMarkCategoryManager.findById(id);
			if (category.isReadonly()) {
				boolean flag = edocMarkDefinitionManager.containEdocMarkDefInCategory(id);				
				if (flag) {					
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('edocLang.big_stream_alter_used','"+category.getCategoryName()+"'))");
					out.println("</script>");
					continue;
				}
				else {
					edocMarkCategoryManager.deleteCategory(id);
				}
			}
			else {
				edocMarkCategoryManager.deleteCategory(id);
			}
			
		}		
		out.println("<script>");
		out.println("parent.location.href = parent.location.href;");
		out.println("</script>");
		return null;
	}
	
	// 修改公文大流水号的下拉列表选项（新建公文文号和编辑公文文号界面）
	@SetContentType
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView changeBigStreamOptions(HttpServletRequest request, HttpServletResponse response)
		throws Exception {		
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");
		Long domainId = CurrentUser.get().getLoginAccount();
		List<EdocMarkCategory> categories = edocMarkCategoryManager.findByTypeAndDomainId(Constants.EDOC_MARK_CATEGORY_BIGSTREAM, domainId);
		JSONArray jsonArray = new JSONArray();
		for (EdocMarkCategory category : categories) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("optionValue", category.getId().toString());
			jsonObject.putOpt("optionName", category.getCategoryName());
			jsonObject.putOpt("optionMinNo", category.getMinNo().toString());
			jsonObject.putOpt("optionMaxNo", category.getMaxNo().toString());
			jsonObject.putOpt("optionCurrentNo", category.getCurrentNo().toString());
			jsonObject.putOpt("optionYearEnabled", category.getYearEnabled());
			jsonObject.putOpt("optionReadonly", category.isReadonly());
			jsonArray.put(jsonObject);
		}
		log.debug("json: " + jsonArray.toString());
		String view = null;
		if (isAjax) {
			view = this.getJsonView();
		}
		return new ModelAndView(view, com.seeyon.v3x.doc.util.Constants.AJAX_JSON, jsonArray);	
	}
	
	
	/**------------------------ 公文文号管理End -----------------------------**/
	
	
	
	/**------------------------ 内部文号管理Start -----------------------------**/
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView setInnerMarkDefPage(HttpServletRequest request,HttpServletResponse response) 
		throws Exception {
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/setInnerMark");
		Long domainId = CurrentUser.get().getLoginAccount();
		int status = edocInnerMarkDefinitionManager.getInnerMarkStatus(domainId);
		mav.addObject("status", status);
		
		Calendar cal = Calendar.getInstance();
		Integer yearNo = cal.get(Calendar.YEAR);				
		mav.addObject("yearNo", yearNo);
		
		List<EdocInnerMarkDefinition> markDefs = null;
		if (status > 0) {			
			if (status == Constants.STATUS_INNERMARK_PUBLIC) {
				markDefs = edocInnerMarkDefinitionManager.getEdocInnerMarkDefs(Constants.EDOC_INNERMARK_UNIFICATION, domainId);
				if (markDefs != null && markDefs.size() > 0) {
					mav.addObject("markDef", markDefs.get(0));
				}
			}
			else if (status == Constants.STATUS_INNERMARK_PRIVATE) {
				markDefs = edocInnerMarkDefinitionManager.getEdocInnerMarkDefs(Constants.EDOC_INNERMARK_SEND, domainId);
				if (markDefs != null && markDefs.size() > 0) {
					mav.addObject("sendMarkDef", markDefs.get(0));
				}
				markDefs = edocInnerMarkDefinitionManager.getEdocInnerMarkDefs(Constants.EDOC_INNERMARK_RECEIVED, domainId);
				if (markDefs != null && markDefs.size() > 0) {
					mav.addObject("recieveMarkDef", markDefs.get(0));
				}
				markDefs = edocInnerMarkDefinitionManager.getEdocInnerMarkDefs(Constants.EDOC_INNERMARK_SIGN_REPORT, domainId);
				if (markDefs != null && markDefs.size() > 0) {
					mav.addObject("signReportMarkDef", markDefs.get(0));
				}				
			}
		}		
		
		return mav;
	}

	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView saveInnerMarkDef(HttpServletRequest request,HttpServletResponse response)throws Exception{
		PrintWriter out = response.getWriter();
		try {
			Long domainId = CurrentUser.get().getLoginAccount();
			
			Integer type = Integer.valueOf(request.getParameter("type"));
//			int status = edocInnerMarkDefinitionManager.getInnerMarkStatus(domainId);
			
			// 如果改变内部文号的类型，则删除已有的文号设置
//			if ( (status == Constants.STATUS_INNERMARK_PUBLIC && type != Constants.EDOC_INNERMARK_UNIFICATION) 
//					|| status == Constants.STATUS_INNERMARK_PRIVATE && type == Constants.EDOC_INNERMARK_UNIFICATION) {
//				edocInnerMarkDefinitionManager.deleteAll(domainId);
//			}
			edocInnerMarkDefinitionManager.deleteAll(domainId);
			
			EdocInnerMarkDefinition def = null;
			if (type == Constants.EDOC_INNERMARK_UNIFICATION) {
				def = new EdocInnerMarkDefinition();
				def.setIdIfNew();
				def.setWordNo(request.getParameter("wordNo"));
				def.setMinNo(Integer.valueOf(request.getParameter("minNo")));
				def.setMaxNo(Integer.valueOf(request.getParameter("maxNo")));
				def.setCurrentNo(Integer.valueOf(request.getParameter("currentNo")));				
				def.setExpression(request.getParameter("markNo"));								
				def.setType(Constants.EDOC_INNERMARK_UNIFICATION);	
				Boolean yearEnabled = false;
				if (request.getParameter("yearEnabled") != null) {
					yearEnabled = true;
				}
				def.setYearEnabled(yearEnabled);	
				Integer length = 0;
				Boolean fixedLength = false;
				if (request.getParameter("fixedLength") != null) {
					fixedLength = true;
				}
				if (fixedLength) {
					length = Integer.valueOf(request.getParameter("length"));
				}
				def.setLength(length);
				def.setDomainId(domainId);
				edocInnerMarkDefinitionManager.create(def);	
				edocInnerMarkDefinitionManager.setInnerMarkStatus(domainId, Constants.STATUS_INNERMARK_PUBLIC);
			}
			else {
				boolean edocPlugin = com.seeyon.v3x.common.SystemEnvironment.hasPlugin("edoc");
				String[] sTypes = null;
				if(edocPlugin){
					sTypes = new String[]{"send_", "receive_", "sign_report_"}; 
				}else{
					sTypes = new String[]{"sign_report_"};
				}
				Integer[] iTypes = null;
				if(edocPlugin){
					iTypes = new Integer[]{Constants.EDOC_INNERMARK_SEND, Constants.EDOC_INNERMARK_RECEIVED, Constants.EDOC_INNERMARK_SIGN_REPORT};
				}else{
					iTypes = new Integer[]{Constants.EDOC_INNERMARK_SIGN_REPORT};
				}				
				for (int i = 0; i < sTypes.length; i++) {
					def = new EdocInnerMarkDefinition();
					def.setIdIfNew();
					def.setWordNo(request.getParameter(sTypes[i] + "wordNo"));
					def.setMinNo(Integer.valueOf(request.getParameter(sTypes[i] + "minNo")));
					def.setMaxNo(Integer.valueOf(request.getParameter(sTypes[i] + "maxNo")));
					def.setCurrentNo(Integer.valueOf(request.getParameter(sTypes[i] + "currentNo")));					
					def.setExpression(request.getParameter(sTypes[i] + "markNo"));					
					def.setType(iTypes[i]);	
					Boolean yearEnabled = false;
					if (request.getParameter(sTypes[i] + "yearEnabled") != null) {
						yearEnabled = true;
					}
					def.setYearEnabled(yearEnabled);
					Integer length = 0;
					Boolean fixedLength = false;
					if (request.getParameter(sTypes[i] + "fixedLength") != null) {
						fixedLength = true;
					}
					if (fixedLength) {
						length = Integer.valueOf(request.getParameter(sTypes[i] + "length"));
					}
					def.setLength(length);
					def.setDomainId(domainId);
					edocInnerMarkDefinitionManager.create(def);
					edocInnerMarkDefinitionManager.setInnerMarkStatus(domainId, Constants.STATUS_INNERMARK_PRIVATE);
				}
			}	
			out.print("<script>");
			//out.print("alert('操作成功!');");	
			out.println("alert('"+ResourceBundleUtil.getString("www.seeyon.com.v3x.form.resources.i18n.FormResources","formapp.saveoperok.label")+"')");
			//out.println("parent.location.reload(true);");
			out.print("</script>");
			out.flush();
		}
		catch (Exception e) {
			out.print("<script>");
			out.print("alert(parent.v3x.getMessage('"+e.getMessage()+"'))");
			out.print("</script>");
			out.flush();
		}
				
		return setInnerMarkDefPage(request,response);
	}	
	
	/**------------------------ 内部文号管理End -----------------------------**/
	
	
	

	/**-------------------------  文号调用Start ----------------------------**/
	
	/**
	 * 进入选择公文断号|手工输入公文文号界面(仅用于公文文号，不适用与内部文号)。
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView docMarkChoose(HttpServletRequest request,HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/choose_all_mark");
				
		Long _orgAccountId=0L;
		User user = CurrentUser.get();
		String orgAccountId=request.getParameter("orgAccountId");
		
		if(Strings.isNotBlank(orgAccountId)){
			_orgAccountId=Long.parseLong(orgAccountId);
		}else{
			_orgAccountId = user.getLoginAccount();
		}
			
		String selDocmark = request.getParameter("selDocmark");
		String templeteId = request.getParameter("templeteId");
		EdocMarkModel model = null;
		List<EdocMarkModel> markDefs  = null;
		
		if(Strings.isNotBlank(templeteId)){
			MarkCategory category = null;
			if("my:doc_mark".equals(selDocmark)) category = MarkCategory.docMark;
			if("my:doc_mark2".equals(selDocmark)) category = MarkCategory.docMark2;
			model = edocMarkDefinitionManager.getEdocMarkByTempleteId(Long.valueOf(templeteId), category);
		}
		if(model == null){ //公文模板没有绑定字号
			String deptIds = orgManager.getUserIDDomain(CurrentUser.get().getId(),_orgAccountId, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
			markDefs = edocMarkDefinitionManager.getEdocMarkDefinitions(deptIds,EdocEnum.MarkType.edocMark.ordinal());
		}else{
			markDefs =new ArrayList<EdocMarkModel>();
			markDefs.add(model);
			
		}
		mav.addObject("isBoundWordNo",model == null ? false :true);
		mav.addObject("markDefs", markDefs);		
		if (markDefs != null && markDefs.size() > 0) {									
			EdocMarkModel mod = markDefs.get(0);	
			Long edocMarkDefinitionId = mod.getMarkDefinitionId();
			List<EdocMarkNoModel> edocMarks = edocMarksManager.getDiscontinuousMarkNos(edocMarkDefinitionId);
			List<EdocMarkNoModel> retList = new ArrayList<EdocMarkNoModel>();
			for(EdocMarkNoModel m : edocMarks){
				if(isNeedExcludeDocMarkToSelect(edocMarkDefinitionId ,m.getMarkNo())) continue;
				else retList.add(m);
			}
			mav.addObject("edocMarks", retList);
		}
		mav.addObject("personInput", EdocSwitchHelper.canInputEdocWordNum(_orgAccountId));

		return mav;		
	}
	/**
	 * 检查断号选择的时候是否需要去掉该文号
	 * a.去掉以前年份的断号，比如2012年的时候需要去掉2011的断号
	 * b.但是没有按年份编码的问号不需要去掉
	 * @param edocMarkDefinitionId :文号定义
	 * @param markNo ：文号串
	 * @return
	 */
	private boolean isNeedExcludeDocMarkToSelect(Long edocMarkDefinitionId,String markNo){
		Calendar cal = Calendar.getInstance();
		String cyear =  String.valueOf(cal.get(Calendar.YEAR));
		EdocMarkDefinition definition = edocMarkDefinitionManager.getMarkDefinition(edocMarkDefinitionId);
		EdocMarkCategory category = definition.getEdocMarkCategory();
		boolean isYearEnable = category.getYearEnabled();
		return isYearEnable && markNo.indexOf(cyear) == -1;
	}
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView docMarkChooseEntry(HttpServletRequest request, HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("edoc/docMarkManage/choose_all_mark_iframe");
		
		return mav;
		
	}
	
	/**
	 * 选择断号时，改变公文文号定义时调用此方法。 
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView changeDocMarkDef(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");
		Long edocMarkDefinitionId = RequestUtils.getRequiredLongParameter(request, "definitionId");
		List<EdocMarkNoModel> edocMarks = edocMarksManager.getDiscontinuousMarkNos(edocMarkDefinitionId);
		
		JSONArray jsonArray = new JSONArray();
		for (EdocMarkNoModel model : edocMarks) {
			if(isNeedExcludeDocMarkToSelect(edocMarkDefinitionId,model.getMarkNo())) continue;
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("optionValue", model.getEdocMarkId().toString());
			jsonObject.putOpt("optionName", model.getMarkNo());			
			jsonArray.put(jsonObject);
		}
		log.debug("json: " + jsonArray.toString());
		String view = null;
		if (isAjax) {
			view = this.getJsonView();
		}
		return new ModelAndView(view, com.seeyon.v3x.doc.util.Constants.AJAX_JSON, jsonArray);	
	}
		
	/**-------------------------  文号调用End ----------------------------**/
	
	/**
	 * 分页方法
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

}