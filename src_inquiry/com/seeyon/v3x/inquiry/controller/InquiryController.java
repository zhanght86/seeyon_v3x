/**
 * 
 */
package com.seeyon.v3x.inquiry.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 调查类型相关
 * 
 * @author lin tian
 * 
 * 2007-2-27
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
public class InquiryController extends BaseController {
	
	private InquiryManager inquiryManager;
	private OrgManager orgManager;
	private AppLogManager appLogManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	/**
	 * @return Returns the inquiryManager.
	 */
	public InquiryManager getInquiryManager() {
		return inquiryManager; 
	}

	/**
	 * @param inquiryManager
	 *            The inquiryManager to set.
	 */
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

     /**
     * 公共信息管理-调查管理首页
     * 需要将该管理页嵌入页签中，所以添加此方法
     * added by Mazc 07-12-11
     */
    public ModelAndView inquiryManageIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {       
        return new ModelAndView("inquiry/manageIndex");
    }
    
	/**
	 * 新建调查类型
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView create_Type(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		InquirySurveytype surveytype = new InquirySurveytype();
		User user = CurrentUser.get();
		// 设置单位
		Long accountId = user.getLoginAccount();
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		surveytype.setIdIfNew();// 设置ID号
		String type_name = request.getParameter("typename");// 调查类型名称
		surveytype.setTypeName(type_name);
		String survey_desc = request.getParameter("surveydesc");// 调查类型描述
//		String space_type = request.getParameter("spaceType");//所属空间
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		if(account.getIsRoot()){
			surveytype.setSpaceType(InquirySurveytype.Space_Type_Group);
			surveytype.setAccountId(0L);
		}else if (Strings.isNotBlank(spaceId)) {
			surveytype.setSpaceType(spaceTypeInt);
			surveytype.setAccountId(Long.parseLong(spaceId));
		}else{
			surveytype.setSpaceType(InquirySurveytype.Space_Type_Account);
			surveytype.setAccountId(accountId);
		}
		surveytype.setSurveyDesc(survey_desc);
		// 评审标记 1:需要评审 0：不需要评审
		String censor_desc = request.getParameter("censordesc");
		surveytype.setCensorDesc(Integer.parseInt(censor_desc));
		surveytype.setFlag(InquirySurveytype.FLAG_NORMAL);           // 设置为正常状态 1为删除状态
//		surveytype.setSpaceType(Integer.parseInt(space_type));
		// surveytype.setAuthDesc(InquirySurveytype.AUTH_NO_ALL);    // 未授予发布权限与全体
		// manager其格式为"99,100,101,..."
		
		String manager = request.getParameter("peopleId");// 管理员ID列表
		
		Set<InquirySurveytypeextend> managerSet = new HashSet<InquirySurveytypeextend>();
		if (manager != null && !manager.equals("")) {
			String[] managerID = manager.split(",");
			for (int j = 0; j < managerID.length; j++) {
				InquirySurveytypeextend isextendmanager = new InquirySurveytypeextend();
				isextendmanager.setIdIfNew();
				isextendmanager.setManagerId(Long.parseLong(managerID[j]));
				isextendmanager.setSort(j);
				isextendmanager.setManagerDesc(InquirySurveytypeextend.MANAGER_SYSTEM);// 设置为管理员
				isextendmanager.setInquirySurveytype(surveytype);
				managerSet.add(isextendmanager);// 级联加入管理员子对象
			}
		}

		String checker = request.getParameter("peopleIdSecond");
		if (!"".equals(checker) && checker != null) {
			InquirySurveytypeextend isextendchecker = new InquirySurveytypeextend();
			isextendchecker.setIdIfNew();
			isextendchecker.setInquirySurveytype(surveytype);
			isextendchecker.setManagerId(Long.parseLong(checker));
			isextendchecker.setManagerDesc(InquirySurveytypeextend.MANAGER_CHECK);// 设置为审核员
			managerSet.add(isextendchecker);
		}
		surveytype.setInquirySurveytypeextends(managerSet);
		inquiryManager.saveInquiryType(surveytype);// 保存调查类型
		
		//对管理员、审核员设定记录应用日志
		this.saveManagersChangeLog(surveytype, true);
		
		return super.refreshWorkspace();
	}

	/**
	 * 单位、集团讨论版块管理员和审核员设置与变更时保存日志
	 * @param type
	 * @param isNew
	 */
	private void saveManagersChangeLog(InquirySurveytype type, boolean isNew) {
		User user = CurrentUser.get();
		String actionText = Constants.getActionText(isNew);
		if(user.isGroupAdmin()) {
			this.appLogManager.insertLog(user, AppLogAction.Group_InquManagers_Update, user.getName(), type.getTypeName(), actionText);
		} else {
			String accountName = null;
			try {
				accountName = this.orgManager.getAccountById(user.getLoginAccount()).getName();
			} catch(Exception e) {
				
			}
			this.appLogManager.insertLog(user, AppLogAction.Account_InqManagers_Update, user.getName(), accountName, type.getTypeName(), actionText);
		}
		
	}
	
	
	/**
	 * 更新调查类型
	 * 
	 * @param request
	 * @param response
	 * @return2
	 * @throws Exception
	 */
	public ModelAndView update_Type(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type_id = request.getParameter("id");
		InquirySurveytype surveytype = inquiryManager.getSurveyTypeById(Long.parseLong(type_id));
		String type_name = request.getParameter("typename");// 调查类型名称		
		String survey_desc = request.getParameter("surveydesc");// 调查类型描述		
		// 评审标记 1:需要评审 0：不需要评审
		String censor_desc = request.getParameter("censordesc");
		if(surveytype!=null) {
			surveytype.setTypeName(type_name);
			surveytype.setSurveyDesc(survey_desc);
			surveytype.setCensorDesc(Integer.parseInt(censor_desc));
			surveytype.setFlag(0);// 设置为正常状态 1为删除状态
		}
		// manager其格式为"99,100,101,..."
		String manager = request.getParameter("peopleId");// 管理员ID列表
		Set<InquirySurveytypeextend> managerSet = new HashSet<InquirySurveytypeextend>();
		if (manager != null) {
			String[] managerID = manager.split(",");
			for (int j = 0; j < managerID.length; j++) {
				InquirySurveytypeextend isextendmanager = new InquirySurveytypeextend();
				isextendmanager.setIdIfNew();
				isextendmanager.setManagerId(Long.parseLong(managerID[j]));
				isextendmanager.setManagerDesc(InquirySurveytypeextend.MANAGER_SYSTEM);// 设置为管理员
				isextendmanager.setInquirySurveytype(surveytype);
				isextendmanager.setSort(Integer.valueOf(j));
				managerSet.add(isextendmanager);// 级联加入管理员子对象
			}
		}
		String checker = request.getParameter("peopleIdSecond");

		if (Strings.isNotBlank(checker)) {
			InquirySurveytypeextend isextendchecker = new InquirySurveytypeextend();
			isextendchecker.setIdIfNew();
			isextendchecker.setInquirySurveytype(surveytype);
			isextendchecker.setManagerId(Long.parseLong(checker));
			isextendchecker.setManagerDesc(InquirySurveytypeextend.MANAGER_CHECK);// 设置为审核员
			managerSet.add(isextendchecker);

			if("true".equals(request.getParameter("needTransfer2NewChecker"))) {
				this.inquiryManager.transfer2NewChecker(Long.parseLong(type_id), Long.parseLong(request.getParameter("oldCheckerId")), Long.parseLong(checker));
			}
		}
		inquiryManager.updateInquiryType(surveytype, managerSet);// 保存调查类型
		
		//对管理员、审核员设定记录应用日志
		this.saveManagersChangeLog(surveytype, false);
		return super.refreshWorkspace();
	}

	/**
	 * 删除调查类型
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removetype(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> typeIds = CommonTools.parseStr2Ids(request.getParameter("id"));
		List<InquirySurveybasic> inquiryList = new ArrayList<InquirySurveybasic>();
		for (Long typeId : typeIds) {
			InquirySurveytype surveytype = inquiryManager.getSurveyTypeById(typeId);
			if (surveytype != null) {
				surveytype.setFlag(1);// 设置调查类型为删除状态
				inquiryManager.updateInquiryType(surveytype);
			}
			inquiryList = inquiryManager.getInquirySurveyByTypeId(typeId);
			for (InquirySurveybasic inquiry : inquiryList) {
				inquiryManager.deleteInquiryBasic(inquiry.getId());
			}
		}
		return super.refreshWorkspace();
	}

	/**
	 * 根据ID查看调查类型
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView categoryModify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type_id = request.getParameter("id");
		ModelAndView mav = new ModelAndView("inquiry/categoryModify");
		SurveyTypeCompose surveycompose = Strings.isNotBlank(type_id) ? inquiryManager.getSurveyTypeComposeBYID(Long.parseLong(type_id)) : null;
		mav.addObject("surveytype", surveycompose);
		String update = request.getParameter("update");
		
		if(update !=null && !"".equals(update)){
			mav.addObject("update", "update");
		}
		
		User user = CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		mav.addObject("isGroup", isGroup);
		//取得是否是详细页面标志
		mav.addObject("readOnly", "readOnly".equals(update));
		
		//判断是否可以修改审核员
		boolean hasNoCheck = false;
		InquirySurveytype type = surveycompose==null ? null : surveycompose.getInquirySurveytype();
		if(type!=null && InquirySurveytype.CENSOR_NO_PASS.equals(type.getCensorDesc())) {
			V3xOrgMember checker = surveycompose.getChecker();
			if(checker!=null && checker.isValid()) {
				hasNoCheck = inquiryManager.hasInquiryNoCheckByType(Long.parseLong(type_id));
			} else {
				mav.addObject("needTransfer2NewChecker", true).addObject("oldCheckerId", checker!=null ? checker.getId() : -1l);
			}
		}
		mav.addObject("hasNoCheck", hasNoCheck);
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		if (Strings.isNotBlank(spaceId)) {
			mav.addObject("typeNameList",inquiryManager.getTypeNameList(Long.parseLong(spaceId), spaceTypeInt));//已创建调查类型列表
		} else {
			mav.addObject("typeNameList",inquiryManager.getTypeNameList(isGroup, user.getLoginAccount()));//已创建调查类型列表
		}
		return mav;
	}

	// 显示添加调查页
	public ModelAndView categoryAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/categoryAdd");
		User user = CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		mav.addObject("isGroup", isGroup);
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		if (Strings.isNotBlank(spaceId)) {
			mav.addObject("typeNameList", inquiryManager.getTypeNameList(Long.parseLong(spaceId), spaceTypeInt));//已创建调查类型列表
		} else {
			mav.addObject("typeNameList", inquiryManager.getTypeNameList(isGroup, user.getLoginAccount()));//已创建调查类型列表
		}
		return mav;
	}

	// 调查首页
	public ModelAndView inquiryFrame(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/categoryFrame");

		return mav;
	}

	/**
	 * 调查类型列表
	 */
	public ModelAndView categoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		List<SurveyTypeCompose> typelist = new ArrayList<SurveyTypeCompose>();
		if (Strings.isNotBlank(spaceId)) {
			// 获取自定义单位或集团的调查类型列表
			typelist = inquiryManager.getCustomAccInquiryList(Long.parseLong(spaceId), spaceType);
		} else {
			typelist = inquiryManager.getInquiryList(user);// 获取调查类型列表
		}
		typelist = CommonTools.pagenate(typelist);
		ModelAndView mav = new ModelAndView("inquiry/categoryList");
		mav.addObject("group", user.isGroupAdmin());
		mav.addObject("typelist", typelist);
		return mav;
	}

	// 调查类型详细页
	public ModelAndView categoryDetail(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		String param = httpServletRequest.getParameter("id");
		ModelAndView mav = new ModelAndView("inquiry/categoryDetail");
		mav.addObject("param", param);
		return mav;

	}

	// 调查类型修改页
	public ModelAndView categoryMDetail(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/categoryMDetail");
		mav.addObject("isDetail",httpServletRequest.getParameter("isDetail"));
		return mav;

	}
	
	/**
	 * 调查版块排序转向页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView orderSurveyType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManager.getAccountById(user.getLoginAccount());
		ModelAndView mav = new ModelAndView("inquiry/orderSurveyType");
		List<InquirySurveytype> typelist = new ArrayList<InquirySurveytype>();
//		inquiryManager.getSurveytypeList();		// 获取调查类型列表
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		if(account.getIsRoot()){
			typelist = inquiryManager.getGroupSurveyTypeList();
		}else if (Strings.isNotBlank(spaceId)) {
			typelist = inquiryManager.getCustomAccInquiryTypeList(Long.parseLong(spaceId), NumberUtils.toInt(spaceType, 5));
		}else{
			typelist = inquiryManager.getAccountSurveyTypeList(user.getLoginAccount());
		}
		mav.addObject("typelist", typelist);
		
		return mav;

	}
	
	/**
	 * 保存调查版块排序结果
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String[] surveyTypeIds=request.getParameterValues("projects");
		
		//if(surveyTypeIdStr!=null)
		//{
			//String[] surveyTypeIds=surveyTypeIdStr.split(";");
			inquiryManager.updateSurveyTypeOrder(surveyTypeIds);
			
		//}
		return super.refreshWorkspace();

	}
	
	/**
	 * 調查管理頁面右上角的查詢
	 */
	public ModelAndView inquiryQuery (HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView mav = new ModelAndView("inquiry/checkIndex") ;
		//String surveyTypeId = request.getParameter("surveytypeid");
		boolean hasCheckAuth = false ;  
		boolean hasCheckBoard = true ;//顯示公告管理模塊
		List<SurveyTypeCompose> tlist = null;
		String group = request.getParameter("group");
		
		String type = request.getParameter("condition") ;
		if(type == null ){
			type = "" ;
		}
		String textfield = request.getParameter("textfield") ;
		if(textfield == null ){
			textfield = "" ;
		}
		String condition  = request.getParameter("numCondition") ;
		if(condition == null ){
			condition = "" ;
		}

		//待审核的数量
		int size=0;
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			size = inquiryManager.getWaitCensorGroupBasicListByCheckerInt(null,null,null,null);
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			size = inquiryManager.getWaitCensorBasicListByCheckerInt(null,null,null,null);// 当前用户拥有任一调查类型下的审核权限
			hasCheckAuth = inquiryManager.hasCheckAuth(false);
		}
		
		if(type != null && type.equals("typeName")){
			tlist = this.inquiryManager.getInquiryTypeList(textfield,group) ;
		}else if(type != null && type.equals("totals")){
			tlist = this.inquiryManager.getInquiryTypeList(textfield,condition,group) ;
		}else if(type != null && type.equals("auditFlag")){
			tlist = this.inquiryManager.getInqTypeListByauditFlag(condition,group) ; ;	
		}else if(type != null && type.equals("auditUser")){
			tlist =  this.inquiryManager.getInqTypeListByauditManager(textfield,group) ;;
		}else {
			if(group!=null&&!group.equals("")&&!group.equals("false")){
				tlist = inquiryManager.getAuthoritiesGroupTypeList();
			}else{
				tlist = inquiryManager.getAuthoritiesTypeList();
			}
		}
		
		//返回到页面的值
		mav.addObject("group", group);
		mav.addObject("count", size);
		mav.addObject("tlist", tlist);
		mav.addObject("hasCheckBoard", hasCheckBoard);
		mav.addObject("hasCheckAuth", hasCheckAuth);
		return mav ;	
		
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	/*****************************
	 * 調查管理頁面右上角的查詢
	 
	public ModelAndView inqQueryByTypeName(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView mav = new ModelAndView("inquiry/checkIndex") ;
		String surveyTypeId = request.getParameter("surveytypeid");
		boolean hasCheckAuth = false ;  
		boolean hasCheckBoard = true ;//顯示公告管理模塊
		List<SurveyTypeCompose> tlist = null;
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String typeName = request.getParameter("typename") ;
		if(typeName == null){
			typeName = "" ;
		}
		//待审核的数量
		int size=0;
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			size = inquiryManager.getWaitCensorGroupBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);
		}else{
			size = inquiryManager.getWaitCensorBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
		}
		
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			tlist = this.inquiryManager.getInquiryTypeList(typeName,group) ;
          //判断是否有某一板块的审核权限
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			tlist = this.inquiryManager.getInquiryTypeList(typeName,group) ;
			hasCheckAuth = inquiryManager.hasCheckAuth(false);
		}
		//返回到页面的值
		mav.addObject("group", group);
		mav.addObject("count", size);
		mav.addObject("tlist", tlist);
		mav.addObject("hasCheckBoard", hasCheckBoard);
		mav.addObject("hasCheckAuth", hasCheckAuth);
		return mav ;
	}
	
	/**
	 * 調查管理頁面右上角的查詢
	 * 按调查的数量查询
	
	public ModelAndView inqQueryByTotals(HttpServletRequest request, HttpServletResponse response)  throws Exception{
		ModelAndView mav = new ModelAndView("inquiry/checkIndex") ;
		String surveyTypeId = request.getParameter("surveytypeid");
		boolean hasCheckAuth = false ;  
		boolean hasCheckBoard = true ;//顯示公告管理模塊
		List<SurveyTypeCompose> tlist = null;
		//接收表单数据
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		//
		String num = request.getParameter("num") ;
		if(num == null || num.equals("")){
			num = "" ;
		}
		String match = request.getParameter("match") ;
		if(match == null || match.equals("")){
			match = "" ;
		}
		//待审核的数量
		int size=0;
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			size = inquiryManager.getWaitCensorGroupBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			size = inquiryManager.getWaitCensorBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
		}
		
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			tlist = this.inquiryManager.getInquiryTypeList(num,match,group) ;
            //判断是否有某一板块的审核权限
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			tlist = this.inquiryManager.getInquiryTypeList(num,match,group) ;
			hasCheckAuth = inquiryManager.hasCheckAuth(false);
		}
		//返回到页面的值
		mav.addObject("group", group);
		mav.addObject("count", size);
		mav.addObject("tlist", tlist);
		mav.addObject("hasCheckBoard", hasCheckBoard);
		mav.addObject("hasCheckAuth", hasCheckAuth);
		return mav ;
	}
    /**
     * 调查管理页面的查询
     * 按是否需要审核查询
 
	public ModelAndView inqQueryByAuditFlag(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/checkIndex") ;
		String surveyTypeId = request.getParameter("surveytypeid");
		boolean hasCheckAuth = false ;  
		boolean hasCheckBoard = true ;//顯示公告管理模塊
		List<SurveyTypeCompose> tlist = null;
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String auditFlag = request.getParameter("auditFlag") ;
		if(auditFlag == null){
			auditFlag = "" ;
		}
		//待审核的数量
		int size=0;
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			size = inquiryManager.getWaitCensorGroupBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);
		}else{
			size = inquiryManager.getWaitCensorBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
		}
		
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			tlist = this.inquiryManager.getInqTypeListByauditFlag(auditFlag,group) ;
          //判断是否有某一板块的审核权限
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			tlist = this.inquiryManager.getInqTypeListByauditFlag(auditFlag,group) ;
			hasCheckAuth = inquiryManager.hasCheckAuth(false);
		}
		//返回到页面的值
		mav.addObject("group", group);
		mav.addObject("count", size);
		mav.addObject("tlist", tlist);
		mav.addObject("hasCheckBoard", hasCheckBoard);
		mav.addObject("hasCheckAuth", hasCheckAuth);
		return mav ;	
		}
	
	/**
	 * 按公告审核员进行查询
	
	public ModelAndView inqQueryByAuditUserName (HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/checkIndex") ;
		String surveyTypeId = request.getParameter("surveytypeid");
		boolean hasCheckAuth = false ;  
		boolean hasCheckBoard = true ;//顯示公告管理模塊
		List<SurveyTypeCompose> tlist = null;
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String auditUserName = request.getParameter("auditUserName") ;
		if(auditUserName == null){
			auditUserName = "" ;
		}
		
		//待审核的数量
		int size=0;
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			size = inquiryManager.getWaitCensorGroupBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);
		}else{
			size = inquiryManager.getWaitCensorBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
		}
		
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			tlist = this.inquiryManager.getInqTypeListByauditManager(auditUserName,group) ;
          //判断是否有某一板块的审核权限
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			tlist = this.inquiryManager.getInqTypeListByauditManager(auditUserName,group) ;
			hasCheckAuth = inquiryManager.hasCheckAuth(false);
		}		
		//返回到页面的值
		mav.addObject("group", group);
		mav.addObject("count", size);
		mav.addObject("tlist", tlist);
		mav.addObject("hasCheckBoard", hasCheckBoard);
		mav.addObject("hasCheckAuth", hasCheckAuth);
		return mav ;	
	}
	*******************/
}