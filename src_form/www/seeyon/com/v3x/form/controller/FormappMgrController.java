package www.seeyon.com.v3x.form.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonFormException;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.bind.flow.FlowTempletImp;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;
import www.seeyon.com.v3x.form.manager.inf.ISeeyonForm_Application;

import com.seeyon.v3x.collaboration.templete.TempleteUtil;
import com.seeyon.v3x.collaboration.templete.domain.TempleteAuth;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class FormappMgrController extends BaseController{
	private static Log log = LogFactory.getLog(FormappMgrController.class);
	private TempleteCategoryManager templeteCategoryManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private TempleteManager templeteManager;
	
	private FormDaoManager formDaoManager = (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	
    private AppLogManager appLogManager;
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	public void setTempleteCategoryManager(
			TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}

	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	
	/*
     * 后台管理表单工作
     */
	public ModelAndView collSysMgr(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formapp/formSysMgr");
		return mav;
	}
	
	/**
	 * 系统管理，树型结构
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView systemTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"form/formapp/formsystemTree");
		
		//int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		
		User user = CurrentUser.get();
		
		long orgAccountId = user.getLoginAccount();
		List<TempleteCategory> templeteCategorys = null;
		
		templeteCategorys = templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		templeteCategorys.addAll( templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.form.ordinal()));
		
		Collections.sort(templeteCategorys);
		
		List<Long> canManagerC = new ArrayList<Long>();
		/*
		for (TempleteCategory category : templeteCategorys) {
			if(category.isCanManager(userId, user.getLoginAccount())){
				canManagerC.add(category.getId());
			}
		}*/

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
	public ModelAndView systemList(HttpServletRequest request,
			HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView(
				"form/formapp/formsystemList"); 
		getIOperBase().setTempleteCategoryManager(templeteCategoryManager);
		Long categoryId = null;		
		String categoryIdStr = request.getParameter("categoryId");		
		if(StringUtils.isNotBlank(categoryIdStr)){
			categoryId = Long.parseLong(categoryIdStr);
		}	
//		if(categoryId>=0 && categoryId<=5)
//		{
//			modelAndView.addObject("applistsize", 0);
//			return modelAndView;
//		}
		String selectquery = request.getParameter("selectquery");
		String formmanid = request.getParameter("formmanid");	
		String formAppName = request.getParameter("formAppName");	
		String state = request.getParameter("state");
		String enable = request.getParameter("enable");
		String formType=request.getParameter("appbindtype");
		FormAppMain fam = new FormAppMain();
		fam.setCategory(categoryId);
		if("3".equals(selectquery) && Strings.isNotBlank(state)){
			fam.setState(Integer.parseInt(state));
		}
		if("5".equals(selectquery) && Strings.isNotBlank(formAppName)){
			fam.setName(formAppName);
		}
		if("6".equals(selectquery) && Strings.isNotBlank(enable)){
			fam.setFormstart(Integer.parseInt(enable));
		}
		if("2".equals(selectquery) && Strings.isNotBlank(formmanid)){
			fam.setUserids(formmanid);
		} else {
			fam.setUserids("");
		}
		if("7".equals(selectquery) && Strings.isNotBlank(formType)){
			fam.setFormType(Integer.valueOf(formType));
		}
		//缺省值，如果没有查询是否启用，默认只显示启用的
		if(Strings.isBlank(enable))
			fam.setFormstart(1);//没有枚举，暂时我也扔数字了
		try {
			List applst =  getIOperBase().queryAllData(fam);
			applst = getIOperBase().assignCategory(applst);
//			if("2".equals(selectquery) && Strings.isNotBlank(formmanid)){
//				List newapplst = new ArrayList();
//				for(int i = 0; i < applst.size(); i++){
//					FormAppMain fm = (FormAppMain)applst.get(i);
//					if(fm.getUserids().indexOf(formmanid) != -1){
//						newapplst.add(fm);
//					}
//				}
//				applst = newapplst;
//			}
			modelAndView.addObject("applst",  getIOperBase().pagenate(applst));	
			String categoryType=request.getParameter("categoryType");		
			modelAndView.addObject("categoryType", categoryType);
			modelAndView.addObject("applistsize", applst.size());
			
		} catch (SeeyonFormException e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getToUserMsg());
			log.error("列表显示该分类下的所有的模板出错!", e);
			OperHelper.creatformmessage(request,response,lst);
		}catch (Exception e) {
			List<String> lst = new ArrayList<String>();
			lst.add(e.getMessage());
			log.error("列表显示该分类下的所有的模板出错!", e);
			OperHelper.creatformmessage(request,response,lst);
		}
		return modelAndView;
	}
	
	/**
	 * 弹出新建分类窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showSystemCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("form/formapp/formsystemCategory");
		
		String id = request.getParameter("id");
		String from = request.getParameter("from");
		
		User user = CurrentUser.get();
		long memberId = user.getId();
		
		Long categoryId = null;
		if(StringUtils.isNotBlank(id)){
			categoryId = Long.parseLong(id);
			
			TempleteCategory templeteCategory = this.templeteCategoryManager.get(categoryId);
			if(templeteCategory.getDescription() == null || "null".equals(templeteCategory.getDescription()))
				templeteCategory.setDescription("");
			modelAndView.addObject("category", templeteCategory);
		}
		
		long orgAccountId = user.getLoginAccount();
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		
		List<TempleteCategory> templeteCategories = this.templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.collaboration_templete.ordinal());
		templeteCategories.addAll(this.templeteCategoryManager.getCategorys(orgAccountId, TempleteCategory.TYPE.form.ordinal()));
		for (int i = 0; i < templeteCategories.size(); i++) {
			TempleteCategory category = templeteCategories.get(i);

			if(category.getId().equals(categoryId) || (!"SYS".equalsIgnoreCase(from) && TempleteUtil.isClass1Category(category) && !category.isCanManager(memberId, orgAccountId))){
				templeteCategories.remove(category);
				i--;
			}
		}
		Collections.sort(templeteCategories);
		StringBuffer categoryHTML = new StringBuffer();
		
		category2HTML(templeteCategories, categoryHTML, new Long(categoryType), 1);
		
		modelAndView.addObject("categoryHTML", categoryHTML);
		
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
	
	
	/**
	 * 保存模板分类 新建/修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
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
			if(!"".equals(request.getParameter("originalCreateDate")) && !"null".equals(request.getParameter("originalCreateDate"))&& request.getParameter("originalCreateDate") !=null){
				Timestamp cdate = new Timestamp(Datetimes.parseDatetime(request.getParameter("originalCreateDate")).getTime());
				category.setCreateDate(cdate);

			}		
			doAuth(authInfo, category);
			
			this.templeteCategoryManager.update(category);
		}
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("var rv = [\"" + category.getId() + "\", \"" + StringEscapeUtils.escapeJavaScript(category.getName()) +"\", \"" + category.getParentId() + "\", " + category.getSort() + ",\""+category.getType()+"\"];");
		out.println("parent.window.returnValue = rv;");
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
	 * 删除模板
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteTemplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String categoryId = request.getParameter("categoryId");
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		String from = request.getParameter("from");
		
		String[] ids = request.getParameterValues("id");
		
		if(ids != null){
			for (String string : ids) {
				Long id = new Long(string);
				this.templeteManager.delete(id);
			}
		}

		return redirectModelAndView("/templete.do?method=systemList&categoryId=" + categoryId + "&categoryType=" + categoryType + "&from=" + from);
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 删除分类
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
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
	
	public ModelAndView judgeuerscond(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		PrintWriter out = response.getWriter();
		String categoryid = java.net.URLDecoder.decode(request.getParameter("categoryid"), "UTF-8");	
		
		boolean returnstr;	
		ArrayList catelist = (ArrayList)getIOperBase().categoryList(Long.parseLong(categoryid));
	    if(catelist.size() !=0){
	    	returnstr = true;
	    	out.write(String.valueOf(returnstr));
	    }else{
	    	returnstr = false;
	    	out.write(String.valueOf(returnstr));
	    }	
		return null;
	} 
	
	public ModelAndView selectFormAdmin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("form/formapp/selectformadmin");
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		V3xOrgRole formRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_FORMADMIN);
		List<V3xOrgMember> v3xorgmemlist = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT,formRole.getOrgAccountId(),formRole.getId());
		mav.addObject("v3xorgmemlist", v3xorgmemlist);
		return mav;
	}
	
	
	public ModelAndView updateAuth(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String formappids = request.getParameter("formappids");
		String ownerid = request.getParameter("ownerid");
		String categoryId = request.getParameter("categoryId");	
		int categoryType = Integer.parseInt(request.getParameter("categoryType"));
		String from = request.getParameter("from");
		String[] formappid = formappids.split(",");
		List<Long> formappidlist = new ArrayList<Long>();
		List<Long> formflowidlist = new ArrayList<Long>();
		User user = CurrentUser.get();
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		V3xOrgMember vom = orgManager.getMemberById(Long.parseLong(ownerid));	
		String formauthname = vom.getName();
		List<String[]> labelsList= new ArrayList<String[]>();
		for(int i = 0 ; i < formappid.length; i++){
			Long formappidl = null;
			try{
				formappidl = Long.parseLong(formappid[i]);
			}catch(Exception e){
				continue;
			}//增加防护，修改表单所属人的时候传入的ID中有个on，界面的数据拼装中没有查找到原因，暂在此处增加防护
			if(formappidl == null) continue;
			List<Long> formappidexitslist = new ArrayList<Long>();
			formappidexitslist.add(formappidl);
			String formname = "";
			ISeeyonForm_Application afapp=SeeyonForm_Runtime.getInstance().getAppManager().findById(formappidl);
			if(afapp == null){
                  afapp = new SeeyonForm_ApplicationImpl();
			          //08-05-14修改
				  afapp.setFId(formappidl);
				  if(formDaoManager.getFormappmianList(formappidexitslist).size() > 0){
					  try{
						  afapp.loadFromDB();
						  formname = afapp.getAppName();
					  } catch(Exception e){
						  e.printStackTrace();
		 			  }finally{
		 				 afapp.unloadAppHibernatResorece();
		 			  }	   
				  }				  
			}
			if(afapp.getAppName() !=null){	
				formname = afapp.getAppName();
				for(int j =0;j< afapp.getSeeyonFormBind().getFlowTempletList().size();j++){
		        	FlowTempletImp flow = (FlowTempletImp)afapp.getSeeyonFormBind().getFlowTempletList().get(j);
		        	formflowidlist.add(flow.getId());
		        }
				formappidlist.add(formappidl);
			}
			String[] label = new String[3];
			label[0] = user.getName();
			label[1] = formname;
			label[2] = formauthname;	
			labelsList.add(label);
		}
		if(formappidlist.size() >0)
		   formDaoManager.updateforformappid(formappidlist, Long.parseLong(ownerid));
		if(formflowidlist.size()>0)
		   templeteManager.updateMemberId(formflowidlist, Long.parseLong(ownerid));
        //修改表单所属人操作日志
		appLogManager.insertLogs(user, AppLogAction.Form_EditAuth,labelsList);

		//return systemList(request,response);
		return null;
		//super.redirectModelAndView("/formappMgrController.do?method=systemList&categoryId=" + categoryId + "&categoryType=" + categoryType + "&from=" +from);
	}

	public FormDaoManager getFormDaoManager() {
		return formDaoManager;
	}

	public void setFormDaoManager(FormDaoManager formDaoManager) {
		this.formDaoManager = formDaoManager;
	}
}