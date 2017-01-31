package com.seeyon.v3x.inquiry.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.inquiry.domain.InquiryAuthority;
import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySubsurvey;
import com.seeyon.v3x.inquiry.domain.InquirySubsurveyitem;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveydiscuss;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.domain.InquiryVotedefinite;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.util.ConstantsInquiry;
import com.seeyon.v3x.inquiry.util.InquiryLock;
import com.seeyon.v3x.inquiry.util.InquiryLockAction;
import com.seeyon.v3x.inquiry.webmdoel.DiscussAndUserCompose;
import com.seeyon.v3x.inquiry.webmdoel.SubsurveyAndItemsCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyAuthCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author lin tian
 * @editer xut、lucx
 * 2007-2-28
 */
public class InquiryBasicController extends BaseController {	
	private static final Log logger = LogFactory.getLog(InquiryBasicController.class);
	private static final String managerbutton = "inquiry_manager";// 调查管理员按钮显示
	private static final String issuerbutton = "inquiry_issuer";// 调查发布人员按钮显示
	private static final String checkerbutton = "inquiry_checker";// 审核人员按钮显示	
	private static final String I18NResource = "com.seeyon.v3x.inquiry.resources.i18n.InquiryResources";  
	
	private InquiryManager inquiryManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;
	private UserMessageManager userMessageManager;
	private FileToExcelManager fileToExcelManager;
	private IndexManager indexManager;	
	private CollaborationController collaborationController;	
	private AppLogManager appLogManager;	
	private DocHierarchyManager docHierarchyManager;	
	private AffairManager affairManager;
	private UpdateIndexManager updateIndexManager;
	private SpaceManager spaceManager;

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView viewIPage(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("inquiry/viewPage");		
		if(request.getParameter("group")!=null&&!request.getParameter("group").equals("")){
			mav.addObject("group", request.getParameter("group"));
		}
		
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		InquirySurveybasic basic = new InquirySurveybasic();
		PrintWriter out = response.getWriter();
		List<Long> receiverIds = new ArrayList<Long>();
		basic.setIdIfNew();
		String surveytype_id = request.getParameter("surveytype_id");// 调查类型ID
		if (surveytype_id != null && !surveytype_id.equals("")) {
			InquirySurveytype type = this.inquiryManager.getSurveyTypeById(Long.parseLong(surveytype_id));
			basic.setInquirySurveytype(type);
			String censor = request.getParameter("censor");// 保存待发标记
			if (censor != null && !censor.equals("")) {
				basic.setCensor(InquirySurveybasic.CENSOR_DRAUGHT);// 草稿状态
			} else {// 如果缺省标记 则默认为审核操作
				if (type.getCensorDesc().intValue() == InquirySurveytype.CENSOR_PASS.intValue())// 不需要审核
				{
					basic.setCensor(InquirySurveybasic.CENSOR_PASS);// 则设为审核通过并发布
				} else if (type.getCensorDesc().intValue() == InquirySurveytype.CENSOR_NO_PASS.intValue()) {// 否则
					basic.setCensor(InquirySurveybasic.CENSOR_NO);// 未审核状态
				}
			}
			basic.setCreaterId(memberid);// 创建人
		}
		String surveyName = request.getParameter("surveyname");// 调查主题
		basic.setSurveyName(surveyName);
		String flag = request.getParameter("temp");
		if (flag != null && flag.equals("temp")) {// 模板
			boolean b = inquiryManager.isTheSameName(surveyName);
			if (!b) {
				out.println("<script>");
				out.println("alert('同名调查模板:" + surveyName + "已存在!');");
				out.println("</script>");
				out.close();
				return null;
			}
			basic.setFlag(InquirySurveybasic.FLAG_TEM);
			basic.setCensor(7);// 模板的冗余数字
		} else {
			basic.setFlag(InquirySurveytype.FLAG_NORMAL);// 设置状态正常
		}

		String surveydesc = request.getParameter("surveydesc");// 调查描述
		if(surveydesc!=null && !surveydesc.equals("")){
			mav.addObject("descInquiry", surveydesc);
		}
		basic.setSurveydesc(surveydesc);

		String cryptonym = request.getParameter("cryptonym");// 实名制标记
		basic.setCryptonym(Integer.valueOf(cryptonym));
		String close_date = request.getParameter("close_date");// 结束日期
		Date cdate =null;
		if(close_date!=null && !"".equals(close_date)){
			cdate = Datetimes.parseDatetimeWithoutSecond(close_date);
		}else{
		    cdate = Datetimes.parseDatetimeWithoutSecond("3000-12-30 15:00");
		}
		    basic.setCloseDate(new Timestamp(cdate.getTime()));
		String send_date = request.getParameter("send_date");// 开始时间
		if(send_date !=null && !"".equals(send_date)){
				Date sdate = Datetimes.parseDatetimeWithoutSecond(send_date);
				basic.setSendDate(new Timestamp(sdate.getTime()));
		}else{
		     	basic.setSendDate(new Timestamp(System.currentTimeMillis()));
		}
		String department_id = request.getParameter("department_id");// 部门
		if (department_id != null && !department_id.equals("")) {
			basic.setDepartmentId(Long.parseLong(department_id));
		} else {
			// 如果当前部门为空 则默认为本部门
			long dep_id = member.getDepartmentId();
			basic.setDepartmentId(dep_id);
		}
		// 加入发布范围
		Set<InquiryScope> scopeset = new HashSet<InquiryScope>();
		String scopestr = request.getParameter("scope_id");

		if (scopestr != null && !"".equals(scopestr)) {
			
			String[][] authInfos = Strings.getSelectPeopleElements(scopestr);
			int s = 0;
			for (String[] strings : authInfos) {
				InquiryScope scope = new InquiryScope();
				scope.setIdIfNew();
				scope.setSort(s);
				scope.setScopeDesc(strings[0]);
				scope.setScopeId(Long.parseLong(strings[1]));
				scope.setInquirySurveybasic(basic);
				scopeset.add(scope);
				s++;
			}
			
			basic.setInquiryScopes(scopeset);// 加入发布范围
//			构造消息接受者    2008-3-10   
			Set<V3xOrgMember> entityCount = new HashSet<V3xOrgMember>();
			Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(scopestr);
			for (V3xOrgMember entity : membersSet) {
				entityCount.add(entity);
				receiverIds.add(entity.getId());
			}
			basic.setTotals(entityCount.size());// 得到发布范围内总人数
			
		}
		// 调查项设置
		String[] questionSorts = request.getParameterValues("questionSort");
		Set<InquirySubsurvey> subsurveySet = new HashSet<InquirySubsurvey>();// 调查项
		Set<InquirySubsurveyitem> itemset = new HashSet<InquirySubsurveyitem>();// 调查问题
		if (questionSorts != null && questionSorts.length > 0) {
			for (int i = 0; i < questionSorts.length; i++) {
				InquirySubsurvey subsurvey = new InquirySubsurvey();
				subsurvey.setIdIfNew();
				subsurvey.setInquirySurveybasic(basic);
				subsurvey.setSort(Integer.parseInt(questionSorts[i]));// 序列码

				String title = "question" + questionSorts[i] + "Title";// 标题
				String titles = request.getParameter(title);
				subsurvey.setTitle(titles);

				String subsurveyDesc = "question" + questionSorts[i]
						+ "Desc";// 描述
				String subsurveyDescs = request.getParameter(subsurveyDesc);
				subsurvey.setSubsurveyDesc(subsurveyDescs);

				String singleMany = "question" + questionSorts[i]
						+ "SingleOrMany";// 单选_多选描述
				String singleManys = request.getParameter(singleMany);
				if ("0".equals(singleManys)) {// 允许单选
					subsurvey.setSingleMany(InquirySubsurvey.SINGLE);
				} else {// 允许多选
					subsurvey.setSingleMany(InquirySubsurvey.MANY);
					String max = "question" + questionSorts[i]
							+ "MaxSelect";// 单选_多选描述
					String maxSelect = request.getParameter(max);
					if (maxSelect == null || maxSelect.equals("")) {
						subsurvey.setMaxSelect(0);// 设置最大选择数量
					} else {
						subsurvey.setMaxSelect(Integer.parseInt(maxSelect));// 设置最大选择数量
					}
				}

				String discuss = "question" + questionSorts[i] + "Discuss";// 允许评论描述
				String discuss2 = request.getParameter(discuss);
				if ("0".equals(discuss2)) {// 允许评论
					subsurvey.setDiscuss(InquirySubsurvey.DISCUSS);
				} else {// 不允许评论
					subsurvey.setDiscuss(InquirySubsurvey.NO_DISCUSS);
				}

				String otheritem = "question" + questionSorts[i]
						+ "OtherItem";// 允许其他
				String otheritems = request.getParameter(otheritem);
				if ("0".equals(otheritems)) {// 允许其他
					subsurvey.setOtheritem(InquirySubsurvey.OTHER);
				} else {// 不允许其他
					subsurvey.setOtheritem(InquirySubsurvey.NO_OTHER);
				}

				String item = "question" + questionSorts[i] + "Item";// 题目列表
				String[] questionItems = request.getParameterValues(item);
				if (questionItems != null && questionItems.length > 0) {
					for (int j = 0; j < questionItems.length; j++) {
						InquirySubsurveyitem inquiryItem = new InquirySubsurveyitem();
						inquiryItem.setIdIfNew();
						inquiryItem.setInquirySurveybasic(basic);
						inquiryItem.setSubsurveyId(subsurvey.getId());
						inquiryItem.setSort(j);
						inquiryItem.setContent(questionItems[j]);
						itemset.add(inquiryItem);
					}
				}
				subsurveySet.add(subsurvey);
			}
		}
		basic.setInquirySubsurveys(subsurveySet);// 加入调查项
		basic.setInquirySubsurveyitems(itemset);// 加入调查问题
		basic.setInquiryScopes(scopeset);
		SurveyBasicCompose sbc = inquiryManager.surveyBasicComposeViewObject(basic,scopeset,subsurveySet,itemset);
		HttpSession session = request.getSession();
		session.setAttribute("sbc", sbc);
		mav.addObject("surveytypeid", surveytype_id);
		mav.addObject("bid", basic.getId());
//		String send_date = request.getParameter("send_date");
		if(send_date == null  || "".equals(send_date)){
			mav.addObject("sendtime", new Timestamp(System.currentTimeMillis()));
		}
		// 传递附件
		List<Attachment> attachments= new ArrayList<Attachment>();
		attachments = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.inquiry, null, null, request);
		mav.addObject("attachments", attachments);
//		session.setAttribute("attachments", attachments);
		mav.addObject("showLoad",1);
		return mav;

	}

	// 管理员管理某个类型页面
	public ModelAndView adminCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("inquiry/adminDetail");
	}

	/**
	 * 新建调查页面(获取当前用户能发表调查的调查类型列表用于切换发布到的调查板块)
	 */
	public ModelAndView promulgation(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/promulgation");
		HttpSession session = httpServletRequest.getSession();
		String surveytypeid = httpServletRequest.getParameter("surveytypeid");
		String custom = httpServletRequest.getParameter("custom");
		String spaceId = httpServletRequest.getParameter("spaceId");
		List<InquirySurveytype> surveytype = new ArrayList<InquirySurveytype>();
		String group = httpServletRequest.getParameter("group");
		if(group!=null&&!"".equals(group)){
			if("group".equals(group)){
				surveytype = inquiryManager.getGroupInquiryTypeListByUserAuth();
			}else if("account".equals(group)){
				surveytype = inquiryManager.getInquiryTypeListByUserAuth(CurrentUser.get().getLoginAccount());
			}
			mav.addObject("group", group);
		} else if ("true".equals(custom)) {
			InquirySurveytype inquiryType = inquiryManager.getSurveyTypeById(Long.parseLong(surveytypeid));
			surveytype.add(inquiryType);
			StringBuffer publisthScopeSpace = new StringBuffer();
			List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(Long.parseLong(surveytypeid));
			for(Object[] arr : issueAreas) {
				publisthScopeSpace.append(StringUtils.join(arr, "|") + ",");
			}
			mav.addObject("custom", custom);
			mav.addObject("scope_range", publisthScopeSpace.substring(0, publisthScopeSpace.length() - 1));
		} else {
			if(Strings.isNotBlank(spaceId)){
				surveytype = inquiryManager.getInquiryTypeListByUserAuth(Long.parseLong(spaceId));
				StringBuffer publisthScopeSpace = new StringBuffer();
				List<Object[]> issueAreas = this.spaceManager.getSecuityOfSpace(Long.parseLong(spaceId));
				for(Object[] arr : issueAreas) {
					publisthScopeSpace.append(StringUtils.join(arr, "|") + ",");
				}
				mav.addObject("customSpace", true);
				mav.addObject("scope_range", publisthScopeSpace.substring(0, publisthScopeSpace.length() - 1));
				mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
			}else{
				surveytype = inquiryManager.getInquiryTypeListByUserAuth();
			}
		}
		
		User member = CurrentUser.get();
		long departmentid = member.getDepartmentId();  // 当前用户的部门ID
		V3xOrgDepartment department = this.orgManager.getEntityById(V3xOrgDepartment.class, departmentid); // 获取发布部门
		mav.addObject("surveytype", surveytype);
		mav.addObject("department", department);
		if (surveytypeid != null)
			mav.addObject("typeId", surveytypeid);
		List<Attachment> attachments = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.inquiry, null, null, httpServletRequest);
		mav.addObject("attachments", attachments);
		session.setAttribute("attachments", attachments);
		return mav;
	}

	/**
	 * 获取某调查某子项的评论列表
	 */
	public ModelAndView disscusList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("inquiry/disscusList");		
	}

	/**
	 * 授权用户发布调查
	 */
	public ModelAndView authorities(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取当前调查类型
		String surveytype_id = request.getParameter("surveytypeid");
		Long surveytypeId = NumberUtils.toLong(surveytype_id);
		// 获得上次的授权人的记录
		List<InquiryAuthority> authorityList = inquiryManager.authorityList(surveytypeId);
		List<Long> exitIds = new ArrayList<Long>();
		if (authorityList != null && authorityList.size()>0) {
			for (InquiryAuthority authority : authorityList) {
				exitIds.add(authority.getAuthId());
			}
		}
		// 获取授权用户
		String authscope = request.getParameter("authscope");
		inquiryManager.saveInquiryAuthorities(surveytypeId, authscope);
		InquirySurveytype type = inquiryManager.getSurveyTypeById(surveytypeId);
		
		User user = CurrentUser.get();
		String userName = user.getName();
		
		String[][] authInfoArray = Strings.getSelectPeopleElements(authscope);
		StringBuffer entityNames4OperLog = new StringBuffer("");
		for(String[] strArray : authInfoArray) {
			String entityType = strArray[0];
			String entityID = strArray[1];
			V3xOrgEntity entity = this.orgManager.getEntity(entityType, Long.valueOf(entityID));
			entityNames4OperLog.append(entity.getName() + ",");
		}
		// 发布授权消息
		List<Long> inquiryIDs = new ArrayList<Long>();
		if ("".equals(authscope) || authscope == null){
			return null;
		}
		Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(authscope);
		for (V3xOrgMember entity : membersSet) {
			if(CurrentUser.get().getId()!=entity.getId().longValue()){
				inquiryIDs.add(entity.getId());
			}
		}		
		
		// 当没有授权人的时候直接返回不做操作
		if(inquiryIDs.size()<1){
			return null;
		}
		// 比较是否存在授权过的人员
		if ( authorityList!=null && authorityList.size() > 0 ){
			List<Long> sendDS = new ArrayList<Long>();
			for (Long long1 : inquiryIDs) {
				if(!exitIds.contains(long1)){
					sendDS.add(long1);
				}
			}
			if(sendDS.size() > 0){
				userMessageManager.sendSystemMessage(MessageContent.get("inq.authorization", type.getTypeName(), this.getLoginUserName(request)),
						ApplicationCategoryEnum.inquiry, this.getLoginUserId(request),
						MessageReceiver.get(type.getId(), sendDS, "", String.valueOf(type.getId())),type.getId());
			}
		}else{
			userMessageManager.sendSystemMessage(MessageContent.get("inq.authorization", type.getTypeName(), this.getLoginUserName(request)),
					ApplicationCategoryEnum.inquiry, this.getLoginUserId(request),
					MessageReceiver.get(type.getId(), inquiryIDs, "", String.valueOf(type.getId())),type.getId());
		}
		//对整个操作记录应用日志
		this.appLogManager.insertLog(user, AppLogAction.Inquiry_PostAuth_Update, userName, type.getTypeName());
		return null;
	}

	/**
	 * 集团空间调查首页
	 */
	public ModelAndView groupSpaceInquiry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
//		List<SurveyBasicCompose> blist = null; // 调查列表
		List<SurveyTypeCompose> tlist = null; // 调查类型列表
		List<SurveyTypeCompose> inquiryTypeList = new ArrayList<SurveyTypeCompose>();
//		blist = inquiryManager.getGroupInquiryBasicList(5);
		User user = CurrentUser.get();
		mav = new ModelAndView("inquiry/groupSpaceInquiryTypeList");
		boolean isManager = false;
//		mav.addObject("alist", blist);
		tlist = inquiryManager.getGroupInquiryTypeList();
		for(SurveyTypeCompose stc : tlist){
			List<V3xOrgMember> managersList = stc.getManagers();
			for(V3xOrgMember member : managersList){
				if(user.getId()==member.getId()){
					isManager = true;
					break;
				}
			}
			if (inquiryManager.isInquiryAuthorities(stc.getInquirySurveytype().getId())||isManager) {
//				 如果当前用户有发布权限
//				mav.addObject("issuer", issuerbutton);
				inquiryTypeList.add(stc);
			}
		}
//		mav.addObject("tlist", tlist);
		mav.addObject("inquiryTypeList", inquiryTypeList);
		mav.addObject("group", "group");
		return mav;
	}

	/**
	 * 全部调查列表或未审核调查列表
	 */
	public ModelAndView more_recent_or_check(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/inquiryMore");
		String typeId = request.getParameter("typeId");
		String custom = request.getParameter("custom");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		boolean moreList = false;
		//用户判断自定义团队空间
		if ("custom".equals(spaceType)) {
			custom = "true";
		}
		InquirySurveytype inquirytype = null;
		if(Strings.isNotBlank(typeId)){
			inquirytype = inquiryManager.getInquirySurveytypeByIdNoFlag(Long.valueOf(typeId));//查看传来的板块ID，板块是否还存在。
			if(inquirytype==null || inquirytype.getFlag()==1){
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.type.notvalid") + "');");
				return  null;
			}
		 	moreList = true;
		 	mav.addObject("inqueryId", inquirytype.getId()) ;
		    mav.addObject("moreList", moreList);
		    mav.addObject("typeName", inquirytype.getTypeName());
		}
		
		List<SurveyBasicCompose> blist = null; // 调查列表
		
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String group = request.getParameter("group");
		if(Strings.isNotBlank(group)){
			if("group".equals(group)){
				blist = inquiryManager.getALLInquiryBasicListByUserID( typeId , condition , textfield , textfield1 , true );
			}else if("account".equals(group)){
				blist = inquiryManager.getALLInquiryBasicListByUserID( typeId , condition , textfield , textfield1 , false );
			}
			mav.addObject("group", group);
		}else{
			if("true".equals(custom)){
				blist = inquiryManager.getALLCustomInquiryBasicListByUserID(Long.parseLong(typeId), 4, typeId, condition, textfield, textfield1, false);
				boolean isSpaceBulManager = this.spaceManager.isManagerOfThisSpace(CurrentUser.get().getId(), Long.parseLong(typeId));
				mav.addObject("spaceManagerFlag", isSpaceBulManager);
			}else if(Strings.isNotBlank(spaceId)){
				blist = inquiryManager.getALLCustomInquiryBasicListByUserID(Long.parseLong(spaceId), Integer.parseInt(spaceType), typeId, condition, textfield, textfield1, false);
				String spaceName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
				mav.addObject("spaceName", spaceName);
				mav.addObject("publicCustom", true);
			}else{
				blist = inquiryManager.getALLInquiryBasicListByUserID( typeId , condition , textfield , textfield1 , false );
			}
		}
		mav.addObject("typeId", typeId);
		mav.addObject("alist", blist);
		mav.addObject("recent", "recent");
		mav.addObject("custom", custom);
		return mav;
	}
	
	/**
	 * 更多页面添加查询功能
	 */
	public ModelAndView oneTypeInquirySearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("inquiry/inquiryMore");
		String typeId = request.getParameter("typeId");
		boolean moreList = false;
		String inqueryId = request.getParameter("inqueryId") ;
		InquirySurveytype inquirytype = new InquirySurveytype();
		if(inqueryId!=null&&!inqueryId.equals("")){
			inquirytype = inquiryManager.getInquirySurveytypeByIdNoFlag(new Long(inqueryId));//差看传来的板块ID，板块是否还存在。
			if(inquirytype==null || inquirytype.getFlag()==1){
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.type.notvalid") + "');self.history.back();");
				return  null;
			}
		 	moreList = true;
		 	mav.addObject("inqueryId", inquirytype.getId()) ;
		    mav.addObject("moreList", moreList);
		    mav.addObject("typeName", inquirytype.getTypeName());
		}
		
		List<SurveyBasicCompose> blist = null; // 调查列表
		String custom = request.getParameter("custom");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		String group = request.getParameter("group");
		if(group!=null&&!"".equals(group)){
			if("group".equals(group)){
				blist = inquiryManager.getALLInquiryBasicListByUserID( inqueryId , condition , textfield , textfield1 , true );
			}else if("account".equals(group)){
				blist = inquiryManager.getALLInquiryBasicListByUserID( inqueryId , condition , textfield , textfield1 , false );
			}
			mav.addObject("group", group);
		}else{
			if(Strings.isNotBlank(spaceId)){
				blist = inquiryManager.getALLCustomInquiryBasicListByUserID(Long.parseLong(spaceId), Integer.parseInt(spaceType), inqueryId , condition , textfield , textfield1 , false );
				mav.addObject("publicCustom", true);
			}else{
				blist = inquiryManager.getALLInquiryBasicListByUserID( inqueryId , condition , textfield , textfield1 , false );
			}
		}
		mav.addObject("custom", custom);
		mav.addObject("typeId", typeId);
		mav.addObject("alist", blist);
		mav.addObject("recent", "recent");
		return mav;
	}
	
	public ModelAndView checkIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("inquiry/check_list_index");
	}
	
    /**
     * 上下结构FrameSet
     * mcj add
	 */
	public ModelAndView checkerMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("inquiry/checkerMain");
		return mav;
	}
	
	/**
	 * 跳转到 调查审核frame 页面
	 */
	public ModelAndView checkerListFrame(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("inquiry/inquiryFrameEntry");
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceId = request.getParameter("spaceId");
		//查出相应的调查类型,作为区别不同审核页面的标志.
		String surveyTypeId = request.getParameter("typeId");
		InquirySurveytype inquiryType = null; 
		if(surveyTypeId!=null&&!surveyTypeId.equals("")){
//			inquirytype = typeListStaticModel.getThisSurveytype(surveytype_id);
			inquiryType = inquiryManager.getInquirySurveytypeByIdNoFlag(new Long(surveyTypeId));
			if(inquiryType==null || inquiryType.getFlag()==1){
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.type.notvalid") + "');self.history.back();");
				return  null;
			}
		}
		mav.addObject("surveytypeid", surveyTypeId);
		
		List<SurveyBasicCompose> blist = null;
		
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			if("group".equals(group)){
				blist = inquiryManager.getWaitCensorGroupBasicListByChecker(condition,textfield,textfield1,surveyTypeId);
			}else if("account".equals(group)){
				blist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId,CurrentUser.get().getLoginAccount());
			}
			mav.addObject("group", group);
		}else{
			if(Strings.isNotBlank(spaceId)){
				blist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId,Long.parseLong(spaceId));
			}else{
				blist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
			}
		}
		mav.addObject("count", blist!=null?blist.size():0);
		return mav;
	}
	
	/**
	 * 在由管理员进入的审核页面又跳转了一次
	 */
	public ModelAndView checkerListFrameInner(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("inquiry/inquiryFrame");
		//获取当前调查类型
		String surveyTypeId = request.getParameter("surveytypeid");
		String group = request.getParameter("group");
		if(group!=null&&!group.equals("")&&!group.equals("false")){
			mav.addObject("group", group);
		}		
		mav.addObject("surveyTypeId", surveyTypeId);
		return mav;
	}
	/**
	 * 获取当前审核员待审核的调查列表
	 */
	public ModelAndView getAllCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/checkerList");
		String surveyTypeId = request.getParameter("surveyTypeId");
		// 审核加查询
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		User user = CurrentUser.get();
		String group = request.getParameter("group");
		String spaceId = request.getParameter("spaceId");
		boolean isGroup = false;
		
		List<SurveyBasicCompose> blist = null;
		if(Strings.isNotBlank(group) && !group.equals("false")){
			if("group".equals(group)){
				blist = inquiryManager.getWaitCensorGroupBasicListByChecker(condition,textfield,textfield1,surveyTypeId);
				isGroup = true;
			}else if("account".equals(group)){
				blist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId);
			}
			mav.addObject("group", group);
		}else{
			// 当前用户拥有任一调查类型下的审核权限
			if(Strings.isNotBlank(spaceId)){
				blist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId,Long.parseLong(spaceId));
			}else{
				blist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId);
			}
		}
		
		// 列出未审核的调查列表
		mav.addObject("blist", blist);
		mav.addObject("typeId", surveyTypeId);
		
		// 判断该人是否有某调查管理权限    用于判断前台调查管理页签是否显示
		boolean hasManageAuth = false;
		if(Strings.isNotBlank(surveyTypeId)){
			hasManageAuth = inquiryManager.isInquiryManager(Long.parseLong(surveyTypeId));
		}else{
			hasManageAuth = inquiryManager.isInquiryManagerInSys(user,isGroup);
		}
		
		mav.addObject("hasManageAuth", hasManageAuth);// 管理
		mav.addObject("hasCheckAuth", true);//必须有审核
		return mav;
	}

	/**
	 * 获取当前用户为管理员的调查版块
	 */
	public ModelAndView getAuthoritiesTypeList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/checkIndex");
		String surveyTypeId = request.getParameter("surveytypeid");
		boolean hasCheckAuth = false;
		boolean hasCheckBoard = false;
		List<SurveyTypeCompose> tlist = null;
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceId = request.getParameter("spaceId");
		//待审核的数量
		int size=0;
		if((group!=null&&!group.equals("")&&!group.equals("false") && "group".equals(group)) || ("true".equals(group))){
			size = inquiryManager.getWaitCensorGroupBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);
		}else{
			if(Strings.isNotBlank(spaceId)){
				size = inquiryManager.getCustomWaitCensorBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId,Long.parseLong(spaceId));
			}else{
				size = inquiryManager.getWaitCensorBasicListByCheckerInt(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
			}
		}
		
		if((group!=null&&!group.equals("")&&!group.equals("false") && "group".equals(group)) || ("true".equals(group))){
			tlist = inquiryManager.getAuthoritiesGroupTypeList();
//			判断是否有管理的板块列表
			hasCheckBoard = !tlist.isEmpty();
//			判断是否有某一板块的审核权限
			hasCheckAuth = inquiryManager.hasCheckAuth(true);
		}else{
			if(Strings.isNotBlank(spaceId)){
				tlist = inquiryManager.getCustomAuthoritiesTypeList(Long.parseLong(spaceId));
				//判断是否有管理的板块列表
				hasCheckBoard = !tlist.isEmpty();
				hasCheckAuth = inquiryManager.hasCheckAuth(false);
			}else{
				tlist = inquiryManager.getAuthoritiesTypeList();
				//判断是否有管理的板块列表
				hasCheckBoard = !tlist.isEmpty();
				hasCheckAuth = inquiryManager.hasCheckAuth(false);
			}
		}
		//group是个标志,第一次进时是False,原来就改成字符串Group,
		mav.addObject("group", group);
		mav.addObject("count", size);
 		mav.addObject("tlist", pagenate(tlist));
		mav.addObject("hasCheckBoard", hasCheckBoard);
		mav.addObject("hasCheckAuth", hasCheckAuth);
		
		return mav;
	}

	/**
	 * 分页 
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
	/**
	 * 当前调查类型下首页
	 * 板块调查管理按钮
	 */
	public ModelAndView survey_index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取当前调查类型
		String surveyTypeId = request.getParameter("surveytypeid");
		String group = request.getParameter("group");
		InquirySurveytype inquiryType = null; 
		SurveyAuthCompose authlist=null;
		if(surveyTypeId!=null&&!surveyTypeId.equals("")){
			inquiryType = inquiryManager.getInquirySurveytypeByIdNoFlag(new Long(surveyTypeId));
			 if(inquiryType.getFlag()==1){
					super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.type.notvalid") + "');self.history.back();");
					return  null;
			 }
		}
		ModelAndView mav = null;
		//从模块管理页面进来的时候mid==mid
		String manage = request.getParameter("mid");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		if (Strings.isBlank(manage)) {
			manage = "";
			mav = new ModelAndView("inquiry/inquiryIndex");
		} else {
			//管理员和审核员是同一个人的时候会进入以下的页面
			mav = new ModelAndView("inquiry/adminDetail");
			//获取我有权限管理的板块
			List<SurveyTypeCompose> manageTypeList = new ArrayList<SurveyTypeCompose>();
			if(group!=null&&!group.equals("")&&!group.equals("false") && "group".equals(group)){
				if("group".equals(group)){
					manageTypeList = inquiryManager.getAuthoritiesGroupTypeList();
				}else if("account".equals(group)){
					manageTypeList = inquiryManager.getAuthoritiesTypeList();
				}
			} else if ("true".equals(custom)) {
				manageTypeList.add(inquiryManager.getSurveyTypeComposeBYID(Long.parseLong(surveyTypeId)));
			}else{
				if(Strings.isNotBlank(spaceId)){
					manageTypeList = inquiryManager.getCustomAuthoritiesTypeList(Long.parseLong(spaceId));
				}else{
					manageTypeList = inquiryManager.getAuthoritiesTypeList();
				}
			}
			mav.addObject("manageTypeList", manageTypeList);
			if(surveyTypeId==null || surveyTypeId.equals("")){
				surveyTypeId = manageTypeList.get(0).getInquirySurveytype().getId().toString();
				inquiryType = inquiryManager.getSurveyTypeById(new Long(surveyTypeId));
			}
			//获得授权信息列表
			authlist = inquiryManager.getAuthoritiesList(Long.parseLong(surveyTypeId));
			mav.addObject("authlist", authlist);
			List<V3xOrgEntity> auList = new ArrayList<V3xOrgEntity>();
			if(authlist!=null&&authlist.getAuthlist()!=null&&!authlist.getAuthlist().equals("")){
				auList = orgManager.getEntities(authlist.getAuthlist().substring(0,authlist.getAuthlist().lastIndexOf(",")));
			}
			mav.addObject("auList", auList);
		}
		
		//待审核的数量
		List<SurveyBasicCompose> aulist=new ArrayList<SurveyBasicCompose>();
		if(group!=null&&!group.equals("")&&!group.equals("false") && "group".equals(group)){
			if("group".equals(group)){
				aulist = inquiryManager.getWaitCensorGroupBasicListByChecker(condition,textfield,textfield1,surveyTypeId);
			}else if("account".equals(group)){
				aulist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId,CurrentUser.get().getLoginAccount());
			}
			mav.addObject("group", group);
		}else{
			if(Strings.isNotBlank(spaceId)){
				aulist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId,Long.parseLong(spaceId));
			}else{
				aulist = inquiryManager.getWaitCensorBasicListByChecker(condition,textfield,textfield1,surveyTypeId);// 当前用户拥有任一调查类型下的审核权限
			}
		}

		
		List<SurveyBasicCompose> blist = new ArrayList<SurveyBasicCompose>();
		if(surveyTypeId.equals("0")){
			blist = inquiryManager.getOtherAccountSurveyBasicList(null, null, null);
		}else{
			List<String> list = inquiryManager.getAuthorities(inquiryType);
			//判断如何添加按钮
			for (String string : list) {
				if ("manager".equals(string))	// 管理员
					mav.addObject("manager", managerbutton);
				if ("checker".equals(string))	// 审核员
					mav.addObject("checkerbutton", checkerbutton);
			}

			if (inquiryManager.isInquiryAuthorities(Long.parseLong(surveyTypeId))) {
				// 如果当前用户有发布权限
				mav.addObject("issuer", issuerbutton);
			}
			boolean isSpaceManager = spaceManager.isManagerOfThisSpace(CurrentUser.get().getId(), Long.parseLong(surveyTypeId));
			if(authlist!=null || isSpaceManager) {	
				blist = inquiryManager.getSurveyBasicListByType(Long.parseLong(surveyTypeId), manage , condition , textfield , textfield1);
			}
		}
		if("true".equals(custom)) {
			mav.addObject("custom", custom);
		}
		mav.addObject("count", aulist!=null?aulist.size():0);
		String hasCheckAuth = request.getParameter("hasCheckAuth");
		mav.addObject("hasCheckAuth", hasCheckAuth);//切换传的参数（1公共信息调查管理-2.审核-3.管理）
		mav.addObject("group", group);
		mav.addObject("inquirytype", inquiryType);
		mav.addObject("blist", blist);
		return mav;
	}

	/**
	 * 获取审核列表
	 */
	public ModelAndView check_index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/checkList");
		String tid = request.getParameter("surveytypeid");
		List<SurveyBasicCompose> bComposelist = inquiryManager
				.getCheckListByType(Long.parseLong(tid));
		mav.addObject("blist", bComposelist);
		InquirySurveytype inquirytype = inquiryManager.getSurveyTypeById(new Long(tid));//typeListStaticModel.getThisSurveytype(tid);
		mav.addObject("inquirytype", inquirytype);
		return mav;
	}

	/**
	 * 授权首页 列出当前调查类型的授权对象列表
	 */
	public ModelAndView authorities_index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取当前调查类型
		String surveytype_id = request.getParameter("surveytypeid");
		ModelAndView mav = new ModelAndView("inquiry/adminGrant");
		SurveyAuthCompose authlist = inquiryManager.getAuthoritiesList(Long
				.parseLong(surveytype_id));
		mav.addObject("authlist", authlist);
		return mav;
	}
	
	/**
	 * 用户创建调查
	 */
	public ModelAndView user_create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {			
			User member = CurrentUser.get();
			long memberid = member.getId(); // 获取当前用户ID
			InquirySurveybasic basic = new InquirySurveybasic();
			basic.setIdIfNew();
			String custom = request.getParameter("custom");
			String spaceType = request.getParameter("spaceType");
			String spaceId = request.getParameter("spaceId");
			String surveytype_id = request.getParameter("surveytype_id"); // 调查类型ID
            surveytype_id = Strings.isNotBlank(surveytype_id) ? surveytype_id : request.getParameter("surveytypeid");
			if (Strings.isNotBlank(surveytype_id)) {
				//把调查类型的ID设进去
				InquirySurveytype type = this.inquiryManager.getSurveyTypeById(Long.parseLong(surveytype_id));
				basic.setSurveyTypeId(Long.valueOf(surveytype_id));
				
				String censor = request.getParameter("censor");// 保存待发标记
				if (Strings.isNotBlank(censor)) {
					basic.setCensor(InquirySurveybasic.CENSOR_DRAUGHT); // 草稿状态
				} else {// 如果缺省标记 则默认为审核操作					
					if (type.getCensorDesc().intValue() == InquirySurveytype.CENSOR_PASS.intValue()){ 	//不需要审核
						basic.setCensor(InquirySurveybasic.CENSOR_PASS);         //2008-2-28    直接将状态置为发布状态，发布时间为当前时间
					} else if (type.getCensorDesc().intValue() == InquirySurveytype.CENSOR_NO_PASS.intValue()) {	//需要审核
						basic.setCensor(InquirySurveybasic.CENSOR_NO);// 未审核状态
						Set<InquirySurveytypeextend> surveytypeextends = type.getInquirySurveytypeextends();
						for (InquirySurveytypeextend surveytypeextend : surveytypeextends) {
							if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK.intValue()) {// 审核人员
								basic.setCensorId(surveytypeextend.getManagerId());
							}
						}
					}
				}
				basic.setCreaterId(memberid);// 创建人
				basic.setIssuerId(memberid);// 发布者
			}
			String surveyName = request.getParameter("surveyname");// 调查主题
			basic.setSurveyName(surveyName);
			String flag = request.getParameter("temp");
			String group = request.getParameter("group");
			if ("temp".equals(flag)) {// 模板
				boolean b = inquiryManager.isTheSameName(surveyName);
				if (!b) {
					super.rendJavaScript(response, "alert('"+ ResourceBundleUtil.getString(I18NResource ,"samename.templete.exist", Functions.toHTML(surveyName)) + "');" +
							"parent.enableButton('save');parent.enableButton('send');parent.enableButton('saveAsTemp');");
					return null;
				}
				basic.setFlag(InquirySurveybasic.FLAG_TEM);
				if(Strings.isNotBlank(group)){
					if("group".equals(group)){
						basic.setCensor(InquirySurveybasic.CENSOR_GROUP_TEM);// 模板的冗余数字---集团模板
					}else if("account".equals(group)){
						basic.setCensor(InquirySurveybasic.CENSOR_ACC_TEM);// 模板的冗余数字---单位模板
					}
				}else if("true".equals(custom)){
					basic.setCensor(InquirySurveybasic.CENSOR_CUSTOM_TEM);// 模板的冗余数字---自定义团队模板
				}else if("5".equals(spaceType)){
					basic.setCensor(InquirySurveybasic.CENSOR_PUBLIC_CUSTOM_TEM);// 模板的冗余数字---自定义单位模板
				}else if("6".equals(spaceType)){
					basic.setCensor(InquirySurveybasic.CENSOR_PUBLIC_CUSTOM_GROUP_TEM);// 模板的冗余数字---自定义集团模板
				}else{
					basic.setCensor(InquirySurveybasic.CENSOR_ACC_TEM);	 // 模板的冗余数字-----单位模板
				}				
			} else {
				basic.setFlag(InquirySurveytype.FLAG_NORMAL);	// 设置状态正常
			}

			String surveydesc = request.getParameter("surveydesc");// 调查描述
			basic.setSurveydesc(surveydesc);

			String cryptonym = request.getParameter("cryptonym");// 实名制标记
			basic.setCryptonym(Integer.valueOf(cryptonym));
			
			String close_date = request.getParameter("close_date");// 结束日期
			Date cdate = null;			
			if(Strings.isNotBlank(close_date)){
				cdate = Datetimes.parseDatetimeWithoutSecond(close_date);
				basic.setCloseDate(new Timestamp(cdate.getTime()));
			}else{
				//没有选结束时间时设为null
				basic.setCloseDate(null);
			}
			basic.setSendDate(new Timestamp(System.currentTimeMillis()));
			     	
			String department_id = request.getParameter("department_id");// 部门
			
			if (Strings.isNotBlank(department_id)) {
				basic.setDepartmentId(Long.parseLong(department_id));
			} else {
				// 如果当前部门为空 则默认为本部门
				long dep_id = member.getDepartmentId();
				basic.setDepartmentId(dep_id);
			}
			
			//是否允许查看调查结果
			boolean allowViewResult = StringUtils.isNotBlank(request.getParameter("allowViewResult"));
			basic.setAllowViewResult(allowViewResult);
			
			//是否允许提交前看调查结果
			if(allowViewResult)
				basic.setAllowViewResultAhead(StringUtils.isNotBlank(request.getParameter("allowViewResultAhead")));
			else
				basic.setAllowViewResultAhead(false);
			
			//加入发布范围
			Set<InquiryScope> scopeset = new HashSet<InquiryScope>();
			String scopestr = request.getParameter("scope_id");

			if (Strings.isNotBlank(scopestr)) {				
				String[][] authInfos = Strings.getSelectPeopleElements(scopestr);
				int s = 0;
				for (String[] strings : authInfos) {
					InquiryScope scope = new InquiryScope();
					scope.setIdIfNew();
					scope.setSort(s);
					scope.setScopeDesc(strings[0]);
					scope.setScopeId(Long.parseLong(strings[1]));
					scope.setInquirySurveybasic(basic);
					scopeset.add(scope);
					s++;
				}
				basic.setInquiryScopes(scopeset);// 加入发布范围
				
				Set<V3xOrgMember> membersSet = orgManager.getMembersByTypeAndIds(scopestr);
				basic.setTotals(membersSet.size());// 得到发布范围内总人数
			}
			
			//调查项设置
			String[] questionSorts = request.getParameterValues("questionSort");
			Set<InquirySubsurvey> subsurveySet = new HashSet<InquirySubsurvey>();// 调查项
			Set<InquirySubsurveyitem> itemset = new HashSet<InquirySubsurveyitem>();// 调查问题
			if (questionSorts != null && questionSorts.length > 0) {
				for (int i = 0; i < questionSorts.length; i++) {
					InquirySubsurvey subsurvey = new InquirySubsurvey();
					subsurvey.setIdIfNew();
					subsurvey.setInquirySurveybasic(basic);
					subsurvey.setSort(Integer.parseInt(questionSorts[i]));// 序列码

					String title = "question" + questionSorts[i] + "Title";// 标题
					String titles = request.getParameter(title);
					subsurvey.setTitle(titles);

					String subsurveyDesc = "question" + questionSorts[i]+ "Desc";// 描述
					String subsurveyDescs = request.getParameter(subsurveyDesc);
					subsurvey.setSubsurveyDesc(subsurveyDescs);

					String singleMany = "question" + questionSorts[i] + "SingleOrMany";// 单选_多选描述
					String singleManys = request.getParameter(singleMany);
					if ("0".equals(singleManys)) {// 允许单选
						subsurvey.setSingleMany(InquirySubsurvey.SINGLE);
					} else if ("1".equals(singleManys)) {// 允许多选
						subsurvey.setSingleMany(InquirySubsurvey.MANY);
						String max = "question" + questionSorts[i] + "MaxSelect";// 单选_多选描述
						String maxSelect = request.getParameter(max);
						if (Strings.isBlank(maxSelect)) {
							subsurvey.setMaxSelect(0);		// 设置最大选择数量
						} else {
							subsurvey.setMaxSelect(Integer.parseInt(maxSelect));	// 设置最大选择数量
						}
					} else if ("2".equals(singleManys)) {// 问答式
						subsurvey.setSingleMany(InquirySubsurvey.Q_A);
					}

					String discuss = "question" + questionSorts[i] + "Discuss";// 允许评论描述
					String discuss2 = request.getParameter(discuss);
					if ("0".equals(discuss2)) {// 允许评论
						subsurvey.setDiscuss(InquirySubsurvey.DISCUSS);
					} else {// 不允许评论
						subsurvey.setDiscuss(InquirySubsurvey.NO_DISCUSS);
					} 

					String otheritem = "question" + questionSorts[i] + "OtherItem";// 允许其他
					String otheritems = request.getParameter(otheritem);
					if ("0".equals(otheritems)) {// 允许其他
						subsurvey.setOtheritem(InquirySubsurvey.OTHER);
					} else {// 不允许其他
						subsurvey.setOtheritem(InquirySubsurvey.NO_OTHER);
					}
					
					if(!"2".equals(singleManys)) {
						String item = "question" + questionSorts[i] + "Item";	// 题目列表
						String[] questionItems = request.getParameterValues(item);
						if (questionItems != null && questionItems.length > 0) {
							for (int j = 0; j < questionItems.length; j++) {
								InquirySubsurveyitem inquiryItem = new InquirySubsurveyitem();
								inquiryItem.setIdIfNew();
								inquiryItem.setOtherOption(0);
								inquiryItem.setInquirySurveybasic(basic);
								inquiryItem.setSubsurveyId(subsurvey.getId());
								inquiryItem.setSort(j);
								inquiryItem.setContent(questionItems[j]);
								itemset.add(inquiryItem);
							}
						}
					}
					subsurveySet.add(subsurvey);
				}
			}
			basic.setInquirySubsurveys(subsurveySet);// 加入调查项
			basic.setInquirySubsurveyitems(itemset);// 加入调查问题
			
			// 删除附件
			String bsid = request.getParameter("bsid");	
			if (Strings.isNotBlank(bsid)) {
				attachmentManager.deleteByReference(Long.parseLong(bsid));
			}
			
			// 保存附件
			String attaFlag = 	attachmentManager.create(ApplicationCategoryEnum.inquiry, basic.getId(), basic.getId(), request);
			
			if(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)){//判断是否有附件，有就保存标志
				basic.setAttachmentsFlag(true);
			}			
			

			//模板
			if ("temp".equals(flag)) {
				inquiryManager.saveSurveyBasicTemp(basic, bsid);	// 保存调查模板
			}else{
				inquiryManager.saveSurveyBasic(basic, bsid);		// 保存调查
				
				//判断是不用审核直接发的调查保存日志---注：直接发调查消息在上面方法里的 this.sendMessage(basic);
				if(basic.getCensor().intValue() == InquirySurveybasic.CENSOR_PASS ){
					appLogManager.insertLog(member, AppLogAction.Inquiry_Publish, member.getName(), basic.getSurveyName());
				}
			}	

			//在此加入全文检索--没有修改。。
			if(basic.getCensor().equals(InquirySurveybasic.CENSOR_PASS)){				
				try {					
					IndexEnable indexEnable=(IndexEnable)inquiryManager;
					IndexInfo indexInfo=indexEnable.getIndexInfo(basic.getId());
					indexManager.index(indexInfo);
				} catch (Exception e) {
					logger.error("调查加入全文检索时出错", e);
				}
			}
			
			// 发送要审合的调查消息 surveytype_id 调查类型ID
			if (basic.getCensor().equals(InquirySurveybasic.CENSOR_NO)){
				List<Long> listId = new ArrayList<Long>();
				List<Long> listAgentId = new ArrayList<Long>();
				List<InquirySurveytypeextend> listIds = inquiryManager.getSerById(Long.parseLong(surveytype_id),InquirySurveytypeextend.MANAGER_CHECK);
				
				Long agentId = null;
				for (InquirySurveytypeextend surveytypeextend : listIds) {
					listId.add(surveytypeextend.getManagerId());
					//如果审核员现在设置了调查审批的代理，同时发消息给代理人
					agentId = AgentUtil.getAgentByApp(surveytypeextend.getManagerId(), ApplicationCategoryEnum.inquiry.getKey());
					if(agentId != null){
						listAgentId.add(agentId);
					}
				}
				//发消息链接---新建发，和修改发判断。
				String auditLinks = null;
				if(Strings.isNotBlank(bsid)){	//修改发key
					auditLinks = "inq.wait.audited";
				}else{	//新建发消息key
					auditLinks = "inq.audited";
				}
				Collection<MessageReceiver> receivers = MessageReceiver.get(basic.getId(),listId, "message.link.inq.auditing" , basic.getId().toString());		       
				userMessageManager.sendSystemMessage(
						MessageContent.get(auditLinks, basic.getSurveyName(), member.getName()),
						ApplicationCategoryEnum.inquiry, 
						memberid, 
						receivers, 
						surveytype_id);
				
				if(CollectionUtils.isNotEmpty(listAgentId)){
					Collection<MessageReceiver> agentReceivers = MessageReceiver.get(basic.getId(),listAgentId, "message.link.inq.auditing" , basic.getId().toString());		       
					userMessageManager.sendSystemMessage(
							MessageContent.get(auditLinks, basic.getSurveyName(), member.getName()).add("col.agent"),
							ApplicationCategoryEnum.inquiry, 
							memberid, 
							agentReceivers, 
							surveytype_id);
				}
				
				//(审核的)直接/修改发布加日志
				if(Strings.isNotBlank(bsid)){
					appLogManager.insertLog(member, AppLogAction.Inquiry_Modify, member.getName(), basic.getSurveyName());
				} else {
					appLogManager.insertLog(member, AppLogAction.Inquiry_New, member.getName(), basic.getSurveyName());
				}				
			}
			
			if (basic.getCensor().intValue() == InquirySurveybasic.CENSOR_NO.intValue() || basic.getCensor().intValue() == InquirySurveybasic.CENSOR_DRAUGHT.intValue() || basic.getCensor().intValue() == InquirySurveybasic.CENSOR_PASS.intValue()) {
				return super.redirectModelAndView("/inquirybasic.do?method=puliscListMain&surveytypeid="+ surveytype_id+ "&group="+group+"&custom="+custom+"&spaceType="+spaceType+"&spaceId="+spaceId);
			} else {
				super.rendJavaScript(response, 
						"parent.enableButton('save');" +
						"parent.enableButton('send');" +
						"parent.enableButton('saveAsTemp');" +
						"alert('" + ResourceBundleUtil.getString(I18NResource, "save.inqtemplete.successfully", Functions.toHTML(basic.getSurveyName())) + "');");
				return null;
			}
		} catch (Exception e) {
			super.rendJavaScript(response, "alert('操作错误请重试!');self.history.back();");   //返回到新建页面
			logger.error("发布调查出错", e);
			return null;
		}
	}
	/**
	 * 得到登录用户id
	 */
	private Long getLoginUserId(HttpServletRequest request) {
		User user = CurrentUser.get();
		long currentUserId = user.getId();
		return Long.valueOf(currentUserId);
	}

	/**
	 * 得到登录用户名
	 */
	private String getLoginUserName(HttpServletRequest request) {
		User user = CurrentUser.get();
		String name = user.getName();
		return name;
	}
	
	private boolean isUserAdmin(Long surveyTypeId, Long userId) throws Exception {
		boolean result = false;
		//拿到调查类型的ID,拿到调查类型的管理员的集合
		List<InquirySurveytypeextend> inqusurveList=inquiryManager.getSerById(surveyTypeId, ConstantsInquiry.INQUIRY_MANAGER_DESC_ADMIN);
		for (InquirySurveytypeextend surveytypeextend : inqusurveList) {
			if(surveytypeextend.getManagerId().longValue()==userId) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * 查看调查
	 */
	public ModelAndView survey_detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
     try {
			ModelAndView mav = null;
			User user=CurrentUser.get();
			HttpSession session = request.getSession();
			String message = request.getParameter("message");
			String group = request.getParameter("group");
			if("message".equals(message)){
				mav = new ModelAndView("inquiry/messageDetail");
			}else{
				mav = new ModelAndView("inquiry/inquiryDetail");
				mav.addObject("listShow", request.getParameter("listShow"));
			}
			String basicID = request.getParameter("bid");
			String typeid = request.getParameter("surveytypeid");
			
			//这是新添加的代码,已经归档的调查不能再被查看了
			InquirySurveybasic insubas = inquiryManager.getInquiryBasic(basicID).getInquirySurveybasic();
			String fromPigeonhole = request.getParameter("fromPigeonhole");
			
			if(insubas.getCensor().equals(InquirySurveybasic.CENSOR_FILING_YES) && !"true".equals(fromPigeonhole)) {
				super.rendJavaScript(response, "parent.refreshWhenInvalid('pigeonhole');");
				return null;
			} 
			
			if ((!(insubas.getCensor().equals(InquirySurveybasic.CENSOR_CLOSE) || insubas.getCensor().equals(InquirySurveybasic.CENSOR_PASS)))
					&& insubas.getCreaterId() != CurrentUser.get().getId() && !"true".equals(fromPigeonhole)) {
				PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert(parent.v3x.getMessage(\"InquiryLang.inquiry_has_cancel\"));");
                out.println("parent.window.close()");
                out.println("</script>");
                out.flush();
                return super.refreshWorkspace();
			}
			
			if(Strings.isBlank(typeid)){
				typeid = String.valueOf(insubas.getInquirySurveytype().getId());
			}
			if (insubas.getCensor().intValue() == InquirySurveybasic.CENSOR_FILING_YES) {
				mav.addObject("archive", true);
			}
			//表明是在调查发布点击打开（该处的调查均处于待审核状态）
			if("waitForAudit".equalsIgnoreCase(request.getParameter("from"))) {
				//如果审核员对调查进行了"直接发布"操作
				if(insubas.getCensor().intValue()==InquirySurveybasic.CENSOR_PASS) {
					super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, 
							"inquiry.sentalready.label") + "');" +
							"parent.refreshWhenInvalid();");
					return null;
				}
			}
			
			SurveyBasicCompose sbcompose = null;
			session.setAttribute("inquiryBasicId", typeid);
			// 附件
			mav.addObject("attachments", attachmentManager.getByReference(Long.parseLong(basicID)));
			String manage_ID = request.getParameter("manager_ID");
			//当前用户是否是管理员的标志
			boolean inqusurextendFlag = this.isUserAdmin(insubas.getSurveyTypeId(), user.getId());
			//当前用户是否可以投票
			boolean isVote = inquiryManager.getUserVoteBasic(Long.parseLong(basicID));
			Map<Long, InquirySubsurveyitem> itemMap = new HashMap<Long, InquirySubsurveyitem>();
			if (manage_ID != null && manage_ID.equals("manager_ID")) {// 管理界面来的
				sbcompose = inquiryManager.getInquiryBasic(basicID);
				int n = 0;
				//循环所有的问题,使其恢复默认值
				for (Iterator<SubsurveyAndItemsCompose> iter = sbcompose.getSubsurveyAndICompose().iterator(); iter.hasNext();) {
					SubsurveyAndItemsCompose saic = (SubsurveyAndItemsCompose) iter.next();
					for (Iterator<InquirySubsurveyitem> iterator = saic.getItems().iterator(); iterator.hasNext();) {
						InquirySubsurveyitem issitem = (InquirySubsurveyitem) iterator.next();
						//为防止与前一个人的混淆.首先将其设为False
						issitem.setCurUserFlag(false);
						//当前人员未投票时把类型为其他选项的过滤掉
						if(issitem.getOtherOption() == 1 && isVote) {
							iterator.remove();
							n++;
						}
					}
				}
				mav.addObject("n", n);
				mav.addObject("vote", "vote");// 表明当前用户有权投票
				if (!isVote) {
					mav.addObject("vote", "");// 表明当前用户已投票
					
					//查出用户的选择项
					List<Long> suritemList=inquiryManager.findByCurUser(user.getId(), Long.valueOf(basicID));
					//循环所有的问题
					for (Iterator<SubsurveyAndItemsCompose> iter = sbcompose.getSubsurveyAndICompose().iterator(); iter.hasNext();)
					{
						SubsurveyAndItemsCompose saic = (SubsurveyAndItemsCompose) iter.next();
						for (Iterator<InquirySubsurveyitem> iterator = saic.getItems().iterator(); iterator.hasNext();) {
							InquirySubsurveyitem issitem = (InquirySubsurveyitem) iterator.next();
							//为防止与前一个人的混淆.首先将其设为False
							issitem.setCurUserFlag(false);
							boolean isContain = suritemList.contains(issitem.getId());
							if(isContain) {
								issitem.setCurUserFlag(true);
							}
							//回显当前人员所填写的其他选项的内容
							if(issitem.getOtherOption() == 1) {
								if(isContain) {
									itemMap.put(saic.getInquirySubsurvey().getId(), issitem);
								}
								iterator.remove();
							}
						}
					}
					
					mav.addObject("done","done");
				}
			} else {// 普通入口进来的 有投票权
				sbcompose = inquiryManager.getInquiryBasicByUserIDAndBasicID(Long.parseLong(basicID));
				if (sbcompose != null) {
					int n = 0;
					//循环所有的问题,使其恢复默认值
					for (Iterator<SubsurveyAndItemsCompose> iter = sbcompose.getSubsurveyAndICompose().iterator(); iter.hasNext();) {
						SubsurveyAndItemsCompose saic = (SubsurveyAndItemsCompose) iter.next();
						for (Iterator<InquirySubsurveyitem> iterator = saic.getItems().iterator(); iterator.hasNext();) {
							InquirySubsurveyitem issitem = (InquirySubsurveyitem) iterator.next();
							//为防止与前一个人的混淆.首先将其设为False
							issitem.setCurUserFlag(false);
							//当前人员未投票时把类型为其他选项的过滤掉
							if(issitem.getOtherOption() == 1 && isVote) {
								iterator.remove();
								n++;
							}
						}
					}
					mav.addObject("n", n);
					mav.addObject("vote", "vote");// 表明当前用户有权投票
				} else {
					// 如果都为空 可能是消息提醒 而用户没有及时点击 以及未发布等
					sbcompose = inquiryManager.getInquiryBasic(basicID);
				}
				if (!isVote) {
                  mav.addObject("vote", "");// 表明当前用户已投票					
					//查出用户的选择项
					List<Long> suritemList=inquiryManager.findByCurUser(user.getId(), Long.valueOf(basicID));
					//循环所有的问题
					for (Iterator<SubsurveyAndItemsCompose> iter = sbcompose.getSubsurveyAndICompose().iterator(); iter.hasNext();) {
						SubsurveyAndItemsCompose saic = (SubsurveyAndItemsCompose) iter.next();
						for (Iterator<InquirySubsurveyitem> iterator = saic.getItems().iterator(); iterator.hasNext();) {
							InquirySubsurveyitem issitem = (InquirySubsurveyitem) iterator.next();
							//为防止与前一个人的混淆.首先将其设为False
							issitem.setCurUserFlag(false);
							boolean isContain = suritemList.contains(issitem.getId());
							if(isContain) {
								issitem.setCurUserFlag(true);
							}
							//回显当前人员所填写的其他选项的内容
							if(issitem.getOtherOption() == 1) {
								if(isContain) {
									itemMap.put(saic.getInquirySubsurvey().getId(), issitem);
								}
								iterator.remove();
							}
						}
					}					
					mav.addObject("done","done");
				}
			}
			String spaceId = request.getParameter("spaceId");
			if(Strings.isNotBlank(spaceId)){
				mav.addObject("customSpaceName", spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName());
			}
			mav.addObject("itemMap", itemMap);
			boolean scopeFlag=inquiryManager.isInInquiryScope(CurrentUser.get(), sbcompose.getInquirySurveybasic().getId());
			//false表示当前用户在发布范围之外,
			mav.addObject("scopeFlag", scopeFlag);
			mav.addObject("manager_ID", manage_ID);
			mav.addObject("bid", basicID);
			mav.addObject("sbcompose", sbcompose);
			mav.addObject("group", group);
			//添加调查管理审核员信息
			mav.addObject("inqusurextendFlag", inqusurextendFlag);
			mav.addObject("tid", typeid);
			return mav;
		} catch (Exception e) {
			affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, Long.parseLong(request.getParameter("bid")));
			super.rendJavaScript(response, "alert('" +ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.not.found")+ "');" +
										   "parent.refreshWhenInvalid();");
			return null;
		}
	}

	/**
	 * 归档查看
	 */
	public ModelAndView pigeonhole_detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
      try {
			ModelAndView mav = new ModelAndView("inquiry/inquiryDetail");
			String basicID = request.getParameter("id");
			String manager_ID = request.getParameter("manager_ID");
			InquirySurveybasic basic = inquiryManager.getBasicByID(Long.valueOf(basicID));
			long typeid = 0L;
			if(basic.getInquirySurveytype() != null)
				typeid = basic.getInquirySurveytype().getId();
			mav.addObject("tid", typeid);
			//附件
			mav.addObject("attachments", attachmentManager.getByReference(Long.parseLong(basicID)));
			SurveyBasicCompose sbcompose = inquiryManager.getInquiryBasicByBasicID(Long.parseLong(basicID));
			mav.addObject("manager_ID", manager_ID);
			mav.addObject("bid", basicID);
			mav.addObject("sbcompose", sbcompose);
			mav.addObject("archive", true);
			return mav;
	  } catch (Exception e) {
			super.rendJavaScript(response, "alert('请确认你有当前文档的归档权限以及版块管理员权限!');");
			return null;
	  }
	}

	/**
	 * 获取待评审的调查：框架页面、左边调查详细信息、右边审核意见填写表单均走这个方法
	 */
	public ModelAndView survey_check(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/checkFrameset");
		String description = request.getParameter("description");
		if("right".equals(description)){
			mav = new ModelAndView("inquiry/checkDiagram");
		} else if("left".equals(description)){
			mav = new ModelAndView("inquiry/checkDetail");
		}
		String bid = request.getParameter("bid");
		String group = request.getParameter("group");
		String auditFlag=request.getParameter("auditFlag");
		
		boolean flag = inquiryManager.isEffective(bid);
		if (!flag) {
			PrintWriter out = response.getWriter();
			out.print("<script type='text/javascript'>");
			out.println("alert('"+ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME,"inquiry.delete.already")+"');\n");
			out.print("if(top.opener){\n");
			out.println("  top.opener.getA8Top().reFlesh();\n");
			out.print("}else{\n");
			out.print("top.window.dialogArguments.getA8Top().reFlesh();\n");
			out.print("}");
			out.println("top.window.close();");
			out.print("</script>");
			out.flush();
			return super.refreshWorkspace();
		}
		SurveyBasicCompose sbcompose = inquiryManager.getInquiryBasicByBasicID(NumberUtils.toLong(bid));
		if(sbcompose == null) {
			String affairId = request.getParameter("affairId");
			if(Strings.isNotBlank(affairId)) {
				affairManager.deleteAffair(Long.parseLong(affairId));
			}
			return mav.addObject("dataExist", false);
		}		
		
		mav.addObject("bid", bid);
		mav.addObject("group", group);
		mav.addObject("auditFlag", auditFlag);
		mav.addObject("sbcompose", sbcompose);
		mav.addObject("inquirytype", sbcompose.getInquirySurveybasic().getInquirySurveytype());
		// 附件
		mav.addObject("attachments", attachmentManager.getByReference(sbcompose.getInquirySurveybasic().getId()));
		
		//对调查进行加锁 
		String action = InquiryLockAction.InQUIRY_LOCK_AUDITING;
		InquiryLock inqlock = inquiryManager.lock(sbcompose.getInquirySurveybasic().getId(), action);
		if(inqlock != null)
		{
			V3xOrgMember orm = orgManager.getMemberById(inqlock.getUserid());
			String lockmessage = inqlock.getAction();
			String alertMsg = ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, lockmessage,orm.getName());
			return mav.addObject("dataLocked", true).addObject("alertMsg", alertMsg);
		}
		return mav;
	}

	/**
	 * 查看权限范围内某调查结果
	 */
	public ModelAndView survey_result(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		String inquiryBasicId = null;
		try {
			inquiryBasicId = (String) session.getAttribute("inquiryBasicId");
		} catch (Exception e) {
		}
		ModelAndView mav = new ModelAndView("inquiry/viewDisscus");
		String basicID = request.getParameter("bid");
		String typeid = request.getParameter("tid");
		InquirySurveytype inquirytype = null;
		
		try {
			inquirytype = inquiryManager.getSurveyTypeById(new Long(typeid));
		} catch (Exception e1) {
			logger.error("获取调查类型出错", e1);
		}
		mav.addObject("inquirytype", inquirytype);
		
		try {
			if (typeid != null && !"".equals(typeid)) {
				if (inquiryManager.isInquiryManager(Long.parseLong(typeid))) {
					mav.addObject("manager", "manager");
				}
			}
		} catch (Exception e) {
			logger.error("判断是否为该调查板块管理员时异常", e);
		}
		SurveyBasicCompose sbcompose = new SurveyBasicCompose();
		try {
			sbcompose = inquiryManager.getInquiryBasicByBasicID(Long.parseLong(basicID));
		} catch (Exception e) {
			logger.error("查看调查结果时获取调查异常", e);
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {
				logger.error("", e1);
			}
			out.println("<script>");
			out.println("alert('该调查已被管理员删除!');window.close();");
			out.println("</script>");
			out.close();
		}
		if( !basicID.equals(typeid) ){
			try {
				for(SubsurveyAndItemsCompose saic : sbcompose.getSubsurveyAndICompose()){
					InquirySubsurvey iss = saic.getInquirySubsurvey();
					List<InquirySurveydiscuss> list = iss.getIsds();
					if(list == null)
						list = new ArrayList<InquirySurveydiscuss>();
					for(InquirySurveydiscuss isd : sbcompose.getInquirySurveybasic().getInquirySurveydiscusses()){
						if(isd.getSubsurveyId() == iss.getId().longValue())
						list.add(isd);
					}
					iss.setIsds(list);
					Collections.sort(list);
				}
				
			} catch (Exception e) {				
				logger.error("查看调查结果时获取调查异常", e);
				PrintWriter out = null;
				
				try {
					out = response.getWriter();
				} catch (IOException e1) {
					logger.error("", e1);
				}
				
				out.println("<script>");
				out.println("alert('该调查已被管理员删除!');window.close();");
				out.println("</script>");
				out.close();
				
			}
			mav.addObject("sbcompose", sbcompose);
			// 附件
			mav.addObject("attachments", attachmentManager.getByReference(Long.parseLong(basicID)));
			mav.addObject("nowTime", new Timestamp(System.currentTimeMillis()));
		}else{
			mav.addObject("sbcompose", sbcompose);
			// 附件
			mav.addObject("attachments", attachmentManager.getByReference(Long.parseLong(inquiryBasicId)));
			mav.addObject("nowTime", new Timestamp(System.currentTimeMillis()));
			session.removeAttribute("inquiryBasicId");
		}
		return mav;

	}

	/**
	 * 审核员操作
	 */
	public ModelAndView checker_handle(HttpServletRequest request, HttpServletResponse response) {
		String basicID = request.getParameter("bid");// 调查ID
		String handle = request.getParameter("handle");// 操作按钮判断
		String checkMind = request.getParameter("checkMind");//审核意见
		PrintWriter out = null;
		SurveyBasicCompose sbc;
		boolean b;
		try {
			out = response.getWriter();
			//处理审核两次的情况,只有是未审核的状态才允许审核操作
			sbc = inquiryManager.getInquiryBasic(basicID);
			if(sbc.getInquirySurveybasic().getCensor().intValue()!=InquirySurveybasic.CENSOR_NO)
			{
				out.println("<script>");
				out.println("alert('" + ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.audit.already") + "');");
		        out.println("  parent.getA8Top().reFlesh();");
		        out.println("</script>");
		        out.flush();
		        return null;
			}
			
			b = inquiryManager.saveCheckerHandle(basicID, handle ,this.getLoginUserName(request),this.getLoginUserId(request),checkMind);
			if (!b) {
				out.println("<script>");
				out.println("alert('该调查结束时间早于当前时间,操作强制为审核不通过!')");
				out.println("</script>");
				// out.close();
			}
			String close = request.getParameter("close");
			if ("close".equals(close)){
//				处理完后刷新待办列表
//				super.rendJavaScript(response, "window.returnValue = \"true\";window.close();");
//				return null;
				//打开方式由模态对话框改为open方式之后，此处需要同时作出调整
				out.println("<script>");
				out.println("if(top.window.opener) {");
				out.println("	top.window.opener.getA8Top().contentFrame.mainFrame.location.reload();");
				out.println("	window.top.close();");
				out.println("}" );
				out.println("</script>");
				out.close();
				return null;
			} else {
				out.println("<script>");
				out.println("if(window.dialogArguments){");
				out.println("	window.returnValue = 'true';");
				out.println("	window.close();");
				out.println("}else{");
				out.println("	parent.getA8Top().contentFrame.mainFrame.location.reload();");
				out.println("}");
				out.println("</script>");
				out.close();
				return null;
			}
		} catch (Exception e) {
			out.println("<script>");
			out.println("alert('该调查已被管理员取消!');window.close();");
			out.println("</script>");
			out.close();
			return null;
		}
    }
	/**
	 * 调查发布首页 最外围有边框的IFrame
	 * mcj add
	 */
	public ModelAndView puliscIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("inquiry/inquiryPuliscIndex");
	    return mav;
	}
    /**
     * 上下结构FrameSet
     * mcj add
	 */
	public ModelAndView puliscListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("inquiry/inquiryPuliscMain");
		return mav;
	}
	/**
	 * 获取当前用户在当前调查类型下未发布的调查列表
	 */
	public ModelAndView basic_NO_send(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/inquiryPublic");
		String tid = request.getParameter("surveytypeid");// 调查类型ID
		String group = request.getParameter("group");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		if(Strings.isNotBlank(spaceId)){
			mav.addObject("customSpace", true);
		}
		if("true".equals(custom)) {
			mav.addObject("custom", true);
			User user = CurrentUser.get();
			List<Long> spaceList = spaceManager.getCanManagerSpace(user.getId());
			if(spaceList.contains(Long.parseLong(tid))) {
				// 如果当前用户为空间管理员
				mav.addObject("manager", managerbutton);
			}
		}
		
//		查询条件
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		InquirySurveytype inquirytype = null;
		if(Strings.isNotBlank(tid)){
			inquirytype = inquiryManager.getInquirySurveytypeByIdNoFlag(Long.valueOf(tid));//查看传来的板块ID，板块是否还存在。
			if(inquirytype.getFlag()==1){
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.type.notvalid") + "');self.history.back();");
				return  null;
			}
		}
		mav.addObject("inquirytype", inquirytype);
		//获取当前用户在当前调查类型下未发布的调查列表
		List<SurveyBasicCompose> blist = inquiryManager.getNOSendBasicListByCreator(Long.parseLong(tid) , condition , textfield , textfield1 );
		mav.addObject("blist", blist);
		
		if (inquiryManager.isInquiryManager(Long.parseLong(tid))) {
			// 如果当前用户为调查管理员
			mav.addObject("manager", managerbutton);
		}
		
		mav.addObject("issuer", issuerbutton);
		
		// 传递附件
		HttpSession session = request.getSession();
		List<Attachment> attachments = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.inquiry, null, null, request);
		mav.addObject("attachments", attachments);
		session.setAttribute("attachments", session.getAttribute("attachments"));
		mav.addObject("nopublic", "nopublic");// 标记未发布
		mav.addObject("group", group);
		return mav;
	}

	/**
	 * 发布未发送的调查
	 */
	public ModelAndView creator_public(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String tid = request.getParameter("surveytypeid");// 调查类型ID
		String[] bids = request.getParameterValues("id");
		if (bids != null && bids.length > 0 ) {
			inquiryManager.creatorSendBasic(bids, tid,this.getLoginUserName(request),this.getLoginUserId(request));
			try {
				for(String bid:bids){
					Long id=Long.parseLong(bid);
					IndexEnable indexEnable=(IndexEnable)inquiryManager;
					IndexInfo indexInfo=indexEnable.getIndexInfo(id);
					indexManager.index(indexInfo);
				}
			} catch(Exception e) {
				logger.error("配置全文检索出现异常", e);
			}
		}
		PrintWriter out = response.getWriter();
		out.print("<script type='text/javascript'>\n");
		out.print(" var opens = parent.opener;\n");
		out.print("if(opens){\n");
		out.print("	opens.getA8Top().reFlesh();\n");
		out.print(" parent.window.close();\n");
		out.print("}else{\n");
		out.print("parent.getA8Top().reFlesh();\n");
		out.print("\n}");
		out.print("</script>\n");
		out.flush();
		return null;
	}

	/**
	 * 选择删除当前用户在当前调查类型下未发布的调查列表
	 */
	public ModelAndView basic_delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		String tid = request.getParameter("tid");// 调查类型ID
		String[] bid = request.getParameterValues("bid");
		inquiryManager.removeNoSendBasicByCreator(Long.parseLong(tid), bid);
		return mav;
	}

	/**
	 * 管理员删除发布的调查
	 */
	public ModelAndView sbasic_remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tid = request.getParameter("surveytypeid");// 调查类型ID
		String[] bid = request.getParameterValues("id");
		String group = request.getParameter("group");
		String custom = request.getParameter("custom");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		User user = CurrentUser.get();
		String userName = user.getName();
		
		//删除调查加日志
		if(bid!=null && bid.length>0){
			for (String string : bid) {
				InquirySurveybasic basic = inquiryManager.getBasicByID(Long.parseLong(string));
				//删除调查加日志
				appLogManager.insertLog(user, AppLogAction.Inquiry_Delete, userName, basic.getSurveyName());
			}
		}
		
		inquiryManager.removeSendBasicByManager(bid);//删除
		String op = request.getParameter("op");
		//在此从全文检索中删除
		if(bid!=null && bid.length>0){
			for(String idStr:bid) {
				Long id=Long.parseLong(idStr);
				indexManager.deleteFromIndex(ApplicationCategoryEnum.inquiry, id);
			}
		}
		
		if (op.equals("nopublic")) {// 未发布页删除转向
			return super.redirectModelAndView("/inquirybasic.do?method=basic_NO_send&surveytypeid=" + tid + "&group="+group + "&custom=" + custom + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		} else if (op.equals("public")) {// 发布页转向
			return super.redirectModelAndView("/inquirybasic.do?method=basic_send&surveytypeid=" + tid + "&custom=" + custom + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		} else if (op.equals("manage")) {// 管理页转向
			return super.redirectModelAndView("/inquirybasic.do?method=survey_index&mid=mid&surveytypeid=" + tid + "&group="+group + "&custom=" + custom + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		} else {// 其他
			return super.redirectModelAndView("/inquirybasic.do?method=survey_index&surveytypeid=" + tid + "&group="+group + "&custom=" + custom + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		}
	}

	/**
	 * 管理员取消发布的调查
	 */
	public ModelAndView cancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tid = request.getParameter("surveytypeid");// 调查类型ID
		String[] bid = request.getParameterValues("id");
		String group = request.getParameter("group");
		String custom = request.getParameter("custom");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		
		//取消发布调查加日志
//		if(bid!=null && bid.length>0){
//			for (String string : bid) {
//				InquirySurveybasic basic = inquiryManager.getBasicByID(Long.parseLong(string));
//				//取消发布调查加日志
//				appLogManager.insertLog(user, AppLogAction.Inquiry_CancelPublish, userName, basic.getSurveyName());
//			}
//		}
		for(String id : bid){
			inquiryManager.cancelInquiryBasic(Long.parseLong(id));//取消发布
			
			// 取消发布调查，删除调查待办
			affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, NumberUtils.toLong(id));
		}
		return super.redirectModelAndView("/inquirybasic.do?method=survey_index&mid=mid&surveytypeid=" + tid + "&group="+group + "&custom=" + custom + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
	}

	
	/**
	 * 选择终止当前用户在当前调查类型下发布的调查列表
	 */
	public ModelAndView basic_end(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// String tid = request.getParameter("tid");// 调查类型ID
		String bid = request.getParameter("bid");
		inquiryManager.closeSendBasicByCreator(bid);
		
		// 结束调查，删除调查待办
		affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, NumberUtils.toLong(bid));
		
		return null;
	}
	
	/**
	 * 用户投票
	 */
	public ModelAndView user_vote(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		String dialog = request.getParameter("isDialog");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String bid = request.getParameter("bid");
		String typeId = request.getParameter("tid");
		
		InquirySurveybasic basic = inquiryManager.getBasicByID(Long.parseLong(bid));
		
		PrintWriter out = response.getWriter();
		if(basic==null || basic.getCensor() != 8){
			out.println("<script>");
			out.println("	alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.canceledorclosed") + "');parent.window.close();");
			out.println("</script>");
			return null;
		}

		String[] subid = request.getParameterValues("subid");// 每个调查
		List<String> itemlist = new ArrayList<String>();
		List<Object> objlist = new ArrayList<Object>();
		int itemCount = Integer.parseInt(request.getParameter("itemCount"));
		if (subid != null && subid.length > 0) {
			for (int i = 0; i < subid.length; i++) {
				String items = i + "items";
				String[] survey = request.getParameterValues(items);// 每个调查下的问题
				
				if (survey != null && survey.length > 0) {
					for (int j = 0; j < survey.length; j++) { // 记录当前用户投票
						if (survey[j] == null || survey[j].equals("")) {
							continue;
						}
						itemlist.add(survey[j]);

						InquiryVotedefinite vote = new InquiryVotedefinite();
						vote.setIdIfNew();
						vote.setInquirySurveybasic(basic);
						vote.setSurveyitemId(Long.parseLong(survey[j]));
						vote.setUserId(memberid);
						vote.setVoteDate(time);
						objlist.add(vote);
					}
				}
				String content = i + "content";
				String itemContent = request.getParameter(content);
				if (itemContent != null && !itemContent.equals("")) {// 用户选择了其他选项
					String sort = i + "sort";
					String sorts = request.getParameter(sort);
					InquirySubsurveyitem surveyitem = new InquirySubsurveyitem();
					surveyitem.setIdIfNew();
					surveyitem.setOtherOption(1);
					surveyitem.setContent(itemContent);
					surveyitem.setSort(new Integer(sorts));
					surveyitem.setVoteCount(1);
					surveyitem.setInquirySurveybasic(basic);
					surveyitem.setSubsurveyId(Long.parseLong(subid[i]));
					objlist.add(surveyitem);

					InquiryVotedefinite vote = new InquiryVotedefinite();
					vote.setIdIfNew();
					vote.setInquirySurveybasic(basic);
					vote.setSurveyitemId(surveyitem.getId());
					vote.setUserId(memberid);
					vote.setVoteDate(time);
					objlist.add(vote);
				}

				String dicuss = i + "disscus";
				String surveyDicuss = request.getParameter(dicuss);
				if (surveyDicuss != null && !surveyDicuss.equals("")) {
					InquirySurveydiscuss dcs = new InquirySurveydiscuss();
					dcs.setIdIfNew();
					dcs.setDiscussContent(surveyDicuss);
					dcs.setInquirySurveybasic(basic);
					dcs.setDiscussDate(time);
					dcs.setSubsurveyId(Long.parseLong(subid[i]));
					dcs.setUserId(memberid);
					objlist.add(dcs);
				}
			}
			
			//判断是否管理员进行了问题项合并，取传过来的问题项和后台取到的问题项做比较如果数目不统一则问题项有变化，加防护禁止提交
			//判断不准确，后台的问题项包括其它问题项
			/*if(itemCount!=basic.getInquirySubsurveyitems().size()){
				out.println("<script>");
				out.println("	alert('" + ResourceBundleUtil.getString(I18NResource, "inquiry.questionschanged") + "');parent.window.close();");
				out.println("</script>");
				return null;
			}*/
		}
		inquiryManager.updateBasicAndVote(Long.parseLong(bid), objlist,itemlist);
		
		// 填写调查，删除调查待办
		affairManager.deleteByObjectAndMember(basic.getId(), memberid);
		
		this.updateIndex(Long.parseLong(bid));
		
		//如果允许查看结果，则投完票后返回到查看结果页面，否则关闭窗口
		boolean isAdmin = this.isUserAdmin(basic.getSurveyTypeId(), memberid);
		if(StringUtils.isBlank(dialog) && (basic.isAllowViewResult() || memberid==basic.getCreaterId() || isAdmin)){
			return super.redirectModelAndView("/inquirybasic.do?method=survey_result&bid="+ bid + "&tid=" + typeId);
		} else {
			out.println("<script>");
			out.println("parent.window.close();");
			out.println("</script>");
			out.flush();
			return null;
		}
	}
	
	/**
	 * 更新全文检索信息
	 * @param id	调查ID
	 */
	private void updateIndex(Long id) {
		try {
			this.updateIndexManager.update(id, ApplicationCategoryEnum.inquiry.key());
		}
		catch(Exception e) {
			logger.error("更新调查全文检索数据时出现异常[id=" + id + "]：", e);
		}
	}

	/**
	 * 获取调查评论列表
	 */
	public ModelAndView discuss_detail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/discussList");
		
		String bid = request.getParameter("bid");// 调查id
		String qid = request.getParameter("qid");// 问题ID
		String tid = request.getParameter("tid");// 调查版块ID
		
		String isInquiry_createUser=request.getParameter("isInquiry_createUser");
		//判断显示删除按钮的条件
		if (inquiryManager.isInquiryManager(Long.parseLong(tid))||"true".equalsIgnoreCase(isInquiry_createUser)) {
			// 如果当前用户为调查管理员
			mav.addObject("manager", managerbutton);
		}
		
		List<DiscussAndUserCompose> dlist = inquiryManager.getDiscussList(Long.parseLong(bid), Long.parseLong(qid));
		mav.addObject("dlist", dlist);
		
		return mav;
	}
	
	/**
	 * 调查评论转向页面
	 */
	public ModelAndView showDisscusFrame(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		return new ModelAndView("inquiry/showDisscusFrame");		
	}
	

	/**
	 * 删除评论
	 */
	public ModelAndView discuss_delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		String did = request.getParameter("did");// 评论id
		String tid = request.getParameter("tid");// 调查版块ID
		String bid = request.getParameter("bid");// 调查id
		String qid = request.getParameter("qid");// 问题ID
		String qname = request.getParameter("qname");//问题名称
		String isInquiry_createUser=request.getParameter("isInquiry_createUser");
		//判断显示删除按钮的条件
		if (inquiryManager.isInquiryManager(Long.parseLong(tid))||"true".equalsIgnoreCase(isInquiry_createUser)) {
			// 如果当前用户为调查管理员
			mav.addObject("manager", managerbutton);
		}
		
		String[] idArray = did.split(",");
		
		for(String id : idArray){
			inquiryManager.removeDiscuss(Long.parseLong(id));
		}
		this.updateIndex(Long.parseLong(bid));
		
		return super.redirectModelAndView("/inquirybasic.do?method=discuss_detail&bid="+ bid + "&qid=" + qid + "&tid=" + tid +"&isInquiry_createUser="+isInquiry_createUser +"&qname="+URLEncoder.encode(qname, "UTF-8"));
	}

	/**
	 * 调查发布者合并调查条目
	 */
	public ModelAndView merge_inquiry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String bid = request.getParameter("bid");
		String typeid = request.getParameter("tid");
		String[] items = request.getParameterValues("items");
		String newItem = request.getParameter("newItem");
		inquiryManager.saveMergeInquiry(items, newItem, bid);
		
		this.updateIndex(NumberUtils.toLong(bid));
		return super.redirectModelAndView("/inquirybasic.do?method=survey_result&bid=" + bid + "&tid=" + typeid);
	}

	/**
	 * 获取个人模板列表
	 */
	public ModelAndView get_templateList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/template");
		String group = request.getParameter("group");
		String custom = request.getParameter("custom");
		String spaceType = request.getParameter("spaceType");
		List<InquirySurveybasic> temlist = new ArrayList<InquirySurveybasic>();
		if ("true".equals(custom)) {
			temlist = inquiryManager.getSpaceTemplateList("custom");
		} else if ("5".equals(spaceType)) {
			temlist = inquiryManager.getSpaceTemplateList("public_custom");
		} else if ("6".equals(spaceType)) {
			temlist = inquiryManager.getSpaceTemplateList("public_custom_group");
		} else {
			temlist = inquiryManager.getAccOrGroupTemplateList(group);
		}
		mav.addObject("tlist", temlist);
		return mav;
	}
	/**
	 * 删除个人模板
	 */
	public  ModelAndView deleteTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try{
				ModelAndView mav = new ModelAndView("");
				String tid[] = request.getParameterValues("tid");
				inquiryManager.removeTemplate(tid);
				PrintWriter out = response.getWriter();
				out.print("<script>");
				out.print("alert('删除模板成功');");
				out.print("<script>");
				return mav;
		}catch(Exception e){
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert('删除模板失败');");
			out.print("<script>");
			return null;
		}
	}
	//更改模板名称

	/**
	 * 根据ID获取调查模板或进入修改未发送的调查页面
	 */
	public ModelAndView get_template(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/promulgation");
		boolean getTemp = "temp".equals(request.getParameter("temp"));
		String surveytypeid = request.getParameter("surveytypeid");
		String custom = request.getParameter("custom");
		String spaceId = request.getParameter("spaceId");
		HttpSession session = request.getSession();
		String bid = "";
		SurveyBasicCompose tem = null;
		List<Attachment> attachments = new ArrayList<Attachment>();
		
		String view = request.getParameter("view");
		if(StringUtils.isBlank(view)) {
			bid = request.getParameter("bid");
			tem = inquiryManager.getTemplateListByID(Long.parseLong(bid), getTemp);
			 //复制一份--取附件
			attachments = attachmentManager.copy(Long.parseLong(bid), Long.parseLong(bid));
		}else{
			tem = (SurveyBasicCompose)session.getAttribute("sbc");
			session.removeAttribute("sbc");
		}
		Set<InquiryScope> scopeset = tem.getInquirySurveybasic().getInquiryScopes();
		String scope_name = "";
		String scope_range = "";
		for (InquiryScope scope : scopeset) {// 发布对象
			long egid = scope.getScopeId();
			String desc = scope.getScopeDesc();
			V3xOrgEntity org = this.orgManager.getEntity(desc, egid);
			scope_range += org.getEntityType() + "|" + egid + ",";
			scope_name += org.getName() + ",";
		}
		mav.addObject("tem", tem);
		mav.addObject("scope_range", scope_range.substring(0,scope_range.length()-1));
		mav.addObject("scope_name", scope_name.substring(0,scope_name.length()-1));
		
		List<InquirySurveytype> surveytype = new ArrayList<InquirySurveytype>();
		
		if(request.getParameter("group")!=null && !request.getParameter("group").equals("") && !request.getParameter("group").equals("account")){
			surveytype = inquiryManager.getGroupInquiryTypeListByUserAuth();
			mav.addObject("group", "group");
		} else if ("true".equals(custom)) {
			InquirySurveytype inquiryType = inquiryManager.getSurveyTypeById(Long.parseLong(surveytypeid));
			surveytype.add(inquiryType);
			mav.addObject("custom", custom);
		} else{
			mav.addObject("group", request.getParameter("group"));
			if(Strings.isNotBlank(spaceId)){
				surveytype = inquiryManager.getInquiryTypeListByUserAuth(Long.parseLong(spaceId));
			}else{
				surveytype = inquiryManager.getInquiryTypeListByUserAuth();
			}
		}
		
		User member = CurrentUser.get();
		long departmentid = member.getDepartmentId();// 当前用户的部门ID
		V3xOrgDepartment department = this.orgManager.getEntityById(V3xOrgDepartment.class, departmentid);// 获取发布部门

		mav.addObject("surveytype", surveytype);
		mav.addObject("department", department);
		String flag = request.getParameter("delete");
		if (flag != null && flag.equals("delete")) {
			mav.addObject("delete", bid);
			mav.addObject("oldId", tem.getInquirySurveybasic().getId());
		}
		if (surveytypeid != null) {
			mav.addObject("typeId", surveytypeid);
		}
		//修改的调查不能再保存.添加一个让保存按钮置灰的标志
		if(tem.getInquirySurveybasic().getCensor().intValue()==ConstantsInquiry.INQUIRY_NO_AUDIT)
		{
			mav.addObject("editFlag", "true");
		}
		
		//对调查进行加锁 
		String action=InquiryLockAction.InQUIRY_LOCK_EDITING;
		InquiryLock inqlock=inquiryManager.lock(tem.getInquirySurveybasic().getId(), action);
		if(inqlock!=null)
		{
			V3xOrgMember orm=orgManager.getMemberById(inqlock.getUserid());
			String lockmessage=inqlock.getAction();
			PrintWriter out = response.getWriter();
	        out.println("<script>");
	        out.println("alert('"+
	        		ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, lockmessage,orm.getName() )
	        		+"');");
	        out.println("  parent.getA8Top().reFlesh();");
	        out.println("</script>");
	        out.flush();
	        return null;
		}
		
		mav.addObject("bid", bid);
		mav.addObject("attachments", attachments);
		mav.addObject("showLoad", 1);
		return mav;
	}

	/**
	 * 将调查结果导出excel
	 */
	public ModelAndView fileToExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String bid = request.getParameter("bid");
		SurveyBasicCompose sbcompose = inquiryManager.getInquiryBasic(bid);
		if (sbcompose == null) {
			super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.not.found") + "');");
			return null;
		}
		//调查结果列名：调查题目、题目选项、投票数、百分比
		String[] inquiryResultColumnName = 
		{
			ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.question.name.label"), 
			ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.question.addItem.label"), 
			ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.vote.label"), 
			ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.percent.label") 
		};
		
		List<SubsurveyAndItemsCompose> subsurveyAndICompose = sbcompose.getSubsurveyAndICompose();
		Set<InquirySurveydiscuss> discusses = sbcompose.getInquirySurveybasic().getInquirySurveydiscusses();
		//该调查是实名评论，还是匿名评论
		boolean showName = sbcompose.getInquirySurveybasic().getCryptonym().intValue()==0;
		DataRecord inquiryResult = new DataRecord();
		for (SubsurveyAndItemsCompose compose : subsurveyAndICompose) {
			//1.显示调查结果
			List<InquirySubsurveyitem> items = compose.getItems();
			int count = 0;
			for (InquirySubsurveyitem subsurveyitem : items) {
				count += subsurveyitem.getVoteCount();
			}
			int flag = 0;
			for (InquirySubsurveyitem subsurveyitem : items) {
				DataRow row1 = new DataRow();
				if (flag == 0)
					row1.addDataCell(compose.getInquirySubsurvey().getTitle(), 1);
				else
					row1.addDataCell("", 1);
				row1.addDataCell(subsurveyitem.getContent(), 1);
				row1.addDataCell(String.valueOf(subsurveyitem.getVoteCount()), 7);
				if (count == 0) {
					row1.addDataCell("0", 7);
				} else {
					double d=(double)subsurveyitem.getVoteCount() * 100 /count;
					row1.addDataCell(String.valueOf(Math.round(d*100)/100.0) + "%", 1);
				}
				inquiryResult.addDataRow(row1);
				flag++;
			}
			
			if(InquirySubsurvey.DISCUSS.equals(compose.getInquirySubsurvey().getDiscuss())) {
				boolean is_Q_A_Style = compose.getInquirySubsurvey().getSingleMany().equals(InquirySubsurvey.Q_A);
				//2.显示每个调查题目对应的评论内容(一行显示列名，之后各行显示评论内容)
				List<InquirySurveydiscuss> comments = new ArrayList<InquirySurveydiscuss>();
				for(InquirySurveydiscuss isd : discusses) {
					if(isd.getSubsurveyId()==compose.getInquirySubsurvey().getId()) {
						comments.add(isd);
					}
				}
				Collections.sort(comments);
				
				//如果该调查无调查项，则在评论上方显示调查题目
				if(items==null || items.isEmpty()) {
					DataRow row4Title = new DataRow();
					row4Title.addDataCell(compose.getInquirySubsurvey().getTitle(), 1);
					inquiryResult.addDataRow(row4Title);
				}
				
				if(comments.size()>0) {
					//评论内容、评论人（如果是实名评论则显示该列）、评论时间
					DataRow columnNameRow = new DataRow();
					columnNameRow.addDataCell(ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, is_Q_A_Style ? "inquiry.answer.context.label" : "inquiry.review.context.label"), 1);
					if(showName)
						columnNameRow.addDataCell(ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, is_Q_A_Style ? "inquiry.answer.people.label" : "inquiry.review.people.label"), 1);
					columnNameRow.addDataCell(ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, is_Q_A_Style ? "inquiry.answer.time.label" : "inquiry.review.time.label"), 1);
					inquiryResult.addDataRow(columnNameRow);
					
					for(InquirySurveydiscuss isd : comments) {
						DataRow row1 = new DataRow();
						row1.addDataCell(isd.getDiscussContent(), 1);
						if(showName) {
							V3xOrgMember member = this.orgManager.getMemberById(isd.getUserId());
							if(member!=null && member.isValid())
								row1.addDataCell(member.getName(), 1);
							else
								row1.addDataCell("", 1);
						}
						row1.addDataCell(Datetimes.format(isd.getDiscussDate(), "yyyy-MM-dd HH:mm"), 6);
						inquiryResult.addDataRow(row1);
					}
					
					//添加一个空行，将各个调查结果及评论内容作为整体分隔开
					DataRow separateRow = new DataRow();
					separateRow.addDataCell("", 1);
					inquiryResult.addDataRow(separateRow);
				}
			}
			
			inquiryResult.setColumnName(inquiryResultColumnName);
			inquiryResult.setTitle(sbcompose.getInquirySurveybasic().getSurveyName());
			inquiryResult.setSheetName(ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.result.label"));
		}
		
		fileToExcelManager.save(request, response, ResourceBundleUtil.getString(ConstantsInquiry.INQUIRY_RESOURCE_BASENAME, "inquiry.result.label"), inquiryResult);
		return null;
	}

	/**
	 * 框架中转
	 */
	public ModelAndView templateIframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/templateiframe");
		String c = request.getParameter("id");
		String group = request.getParameter("group");
		String custom = request.getParameter("custom");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		if (c.equals("tem")) {
			mav.addObject("url", "get_templateList&group="+group+"&custom="+custom+"&spaceType="+spaceType+"&spaceId="+spaceId);
		}
		return mav;
	}

	/**
	 * 归档操作状态更新
	 */
	public ModelAndView pigeonhole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idString = request.getParameter("ids");
		if (Strings.isNotBlank(idString)) {
			inquiryManager.pigeonholeInquiry(idString);
			
			User user = CurrentUser.get();
			String userName = user.getName();
			
			String[] ids = idString.split(",");
			String[] archiveIds = request.getParameterValues("archiveId");
			for(int i=0;i<ids.length;i++) {
				Long _archiveId = Long.valueOf(archiveIds[i]);
				DocResource res = docHierarchyManager.getDocResourceById(_archiveId);
				//归档记录应用日志 added by Meng Yang at 2009-08-20
				if(res != null){
					String folderName = docHierarchyManager.getNameById(res.getParentFrId());
					appLogManager.insertLog(user, AppLogAction.Inquiry_Pigeonhole, userName, res.getFrName(), folderName);
				}
			}
		}
		super.rendJavaScript(response, "alert('" + ConstantsInquiry.getI18NValues("inquiry.pigeonhole.success") + "');");
		return null;
	}

	//调查转发协同，将调查结果展示
	public ModelAndView surveyToCol(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("collaboration/newCollaboration");
		String basicID = request.getParameter("bid");
		StringBuffer bodyContent = new StringBuffer();
		
		//获取调查问题和子项
		SurveyBasicCompose sbcompose = inquiryManager.getInquiryBasicByBasicID(Long.parseLong(basicID));
		List<SubsurveyAndItemsCompose> subAndItemsList = sbcompose.getSubsurveyAndICompose();
		
		bodyContent.append("<table width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"word-break:break-all;word-wrap:break-word\">")
				   .append("<tr><td valign=\"top\">");
		
		Set<InquirySurveydiscuss> allComments = sbcompose.getInquirySurveybasic().getInquirySurveydiscusses();
		for(int i = 0 ; i < subAndItemsList.size() ; i++){
			SubsurveyAndItemsCompose subAndItems = subAndItemsList.get(i);
			
			InquirySubsurvey iss = subAndItems.getInquirySubsurvey();
			List<InquirySurveydiscuss> list = new ArrayList<InquirySurveydiscuss>();
			for(InquirySurveydiscuss isd : allComments){
				if(isd.getSubsurveyId() == iss.getId().longValue())
					list.add(isd);
			}
			Collections.sort(list);
			boolean is_Q_A_Style = iss.getSingleMany().equals(InquirySubsurvey.Q_A);
			
			bodyContent.append("<table border=\"0\" width=\"100%\"  cellpadding=\"3\" bgcolor=\"#d8d8d8\" cellspacing=\"1\" align=\"center\"> <tr bgcolor=\"#ffffff\" align=\"left\">")
						.append("<th colspan=\"3\"><div style=\" width: \"80%\";overflow:hidden; text-overflow:ellipsis; cursor: hand;\" title=\"")
						.append(Functions.toHTML(subAndItems.getInquirySubsurvey().getTitle()))
						.append("\"><nobr>")
						.append((i+1)+"、"+Functions.toHTML(subAndItems.getInquirySubsurvey().getTitle()));
			if(subAndItems.getInquirySubsurvey().getSingleMany().equals(InquirySubsurvey.SINGLE)){
				bodyContent.append("(" + ResourceBundleUtil.getString(I18NResource, "inquiry.select.single.label") + ")");
			} else if(subAndItems.getInquirySubsurvey().getSingleMany().equals(InquirySubsurvey.MANY)) {
				bodyContent.append("(" + ResourceBundleUtil.getString(I18NResource, "inquiry.select.many.label") + ")");
			}else {
				bodyContent.append("(" + ResourceBundleUtil.getString(I18NResource, "inquiry.select.qa.label") + ")");
			}
			bodyContent.append("</nobr>" + (is_Q_A_Style ? "</div></th></tr>" : "</div></th><th width=\"10%\">" + ResourceBundleUtil.getString(I18NResource, "inquiry.vote.label") 
							+ "</th><th width=\"10%\">" + ResourceBundleUtil.getString(I18NResource, "inquiry.percent.label")+ "</th></tr>"));
			List<InquirySubsurveyitem> items = subAndItems.getItems();
			
			int res = 0;
			for(InquirySubsurveyitem item : items){
				res+=item.getVoteCount();
			}
			
			for(InquirySubsurveyitem item : items){
				//第一列空
				bodyContent.append("<tr bgcolor=\"#ffffff\"><td width=\"5%\"></td>");
				//第二列内容
				bodyContent.append("<td width=\"45%\"><div style=\" width: \"45%\";overflow:hidden; text-overflow:ellipsis; cursor: hand\" title=\""+Functions.toHTML(item.getContent())+"\">"+Functions.toHTML(item.getContent())+"</div></td>");
				//第三列
				int sort =item.getSort();
                String thisSort = String.valueOf(sort%5);
                double imgWidth=1;
                if(res!=0){
                	double iWidth = item.getVoteCount().doubleValue()/res*150;
                	BigDecimal   b   =   new   BigDecimal(iWidth);
    			    imgWidth = b.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
                }
				bodyContent.append("<td width=\"30%\"><img id='sss' src='/seeyon/apps_res/inquiry/images/"+thisSort+".gif' width=\""+imgWidth+"px\" height=\"15px\"></td>");
				//第四列
				bodyContent.append("<td width=\"10%\" align=\"left\">"+item.getVoteCount()+"</td>");
				//第五列
				if(res==0){
					res=1;
				}
				double per = item.getVoteCount().doubleValue()/res*100;
				BigDecimal   b   =   new   BigDecimal(per);
			    double per1 = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
				bodyContent.append("<td width=\"10%\" align=\"right\">"+per1+" %</td></tr>");
			}
			//是否显示评论人这一列及评论人姓名
			boolean showName = (sbcompose.getInquirySurveybasic().getCryptonym().intValue()==0);
			
			//显示评论内容 added by Meng Yang 2009-05-31
			bodyContent.append("<tr><td colspan=\"5\" width=\"100%\" style=\"padding:0\"><table border=\"0\" width=\"100%\"  cellpadding=\"3\" bgcolor=\"#d8d8d8\" cellspacing=\"1\" align=\"center\">"
							+ "<tr bgcolor=\"#ffffff\" align=\"left\">"+"<th colspan=\"" + (showName ? 3 : 4) + "\"><div style=\" width:\"" + (showName ? 75 : 85) + "%\"; overflow:hidden; text-overflow:ellipsis; cursor: hand;\">" 
							+ ResourceBundleUtil.getString(I18NResource, is_Q_A_Style ? "inquiry.answer.context.label" : "inquiry.review.context.label")+ "<nobr>"
							+ "</nobr></div></th>" + (showName ? ("<th width=\"10%\">" + ResourceBundleUtil.getString(I18NResource, is_Q_A_Style ? "inquiry.answer.people.label" : "inquiry.review.people.label") + "</th>") : "") +
							"<th width=\"15%\">" + ResourceBundleUtil.getString(I18NResource, is_Q_A_Style ? "inquiry.answer.time.label" : "inquiry.review.time.label") +"</th></tr>");
			if(showName) {
				for(InquirySurveydiscuss discuss : list) {
					//第一列：评论内容,第二列：评论人,第三列：评论时间（实名评论）
					bodyContent.append("<tr bgcolor=\"#ffffff\"><td width=\"70%\" colspan=\"3\" font-size=\"14\">" + Functions.toHTML(discuss.getDiscussContent()) + "</td>"
								+ "<td width=\"10%\" font-size=\"14\">"+ orgManager.getMemberById(discuss.getUserId()).getName() +"</td>"
								+ "<td width=\"20%\" font-size=\"14\">"+ Datetimes.format(discuss.getDiscussDate(), Datetimes.datetimeWithoutSecondStyle) +"</td></tr>");
				}
			} else {
				for(InquirySurveydiscuss discuss : list) {
					//第一列：评论内容,第二列：评论时间（匿名评论）
					bodyContent.append("<tr bgcolor=\"#ffffff\"><td width=\"80%\" colspan=\"4\" font-size=\"14\">" + Functions.toHTML(discuss.getDiscussContent()) + "</td>"							
								+ "<td width=\"20%\" font-size=\"14\">"+ Datetimes.format(discuss.getDiscussDate(), Datetimes.datetimeWithoutSecondStyle) +"</td></tr>");		
				}
			}
			bodyContent.append("</table></td></tr>");
		}
		bodyContent.append("</td></tr></table>");
		
		List<Attachment> attachments = new ArrayList<Attachment>();
		attachments=attachmentManager.getByReference(Long.parseLong(basicID));
		mav=collaborationController.appToHtmlColl(sbcompose.getInquirySurveybasic().getSurveyName(), bodyContent.toString(), attachments, true);
		return mav;
	}
	
	
	//转向查看调查frame
	public ModelAndView showInquiryFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String basicID = request.getParameter("bid");
		InquirySurveybasic inquirySurvey = inquiryManager.getBasicByID(Long.parseLong(basicID));
		String title = "";
		if (inquirySurvey != null) {
			title = inquirySurvey.getSurveyName();
		}
		return new ModelAndView("inquiry/showInquiryFrame")
						.addObject("title", title)
						.addObject("listShow", "listShow".equals(request.getParameter("listShow")))
						.addObject("from", "waitForAudit".equals(request.getParameter("from")) ? "waitForAudit" : "");
	}
	
	//调查主界面   板块加列表
	public ModelAndView recent_or_check( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		ModelAndView mav = new ModelAndView("inquiry/inquiryMain");
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		List<SurveyTypeCompose> inquiryTypeList = null;   // 调查类型列表
		List<List<SurveyBasicCompose>> typeAndBasic = new ArrayList<List<SurveyBasicCompose>>();
		User user = CurrentUser.get();
		
		String group = request.getParameter("group");
		if (Strings.isNotBlank(spaceId)) {
			String spaceName = spaceManager.getSpace(Long.parseLong(spaceId)).getSpaceName();
			mav.addObject("spaceName", spaceName);
			mav.addObject("publicCustom", true);
		}
		String accountName = orgManager.getAccountById(CurrentUser.get().getLoginAccount()).getShortname();
		String groupName =orgManager.getRootAccount(CurrentUser.get().getLoginAccount()).getShortname();
		mav.addObject("accountName", accountName);
		mav.addObject("groupName", groupName);
		if(Strings.isNotBlank(group)){
			if("group".equals(group)){
				//集团空间调查首页
				inquiryTypeList = inquiryManager.getUserIndexInquiryList(true,false);
			}else if("account".equals(group)){
				inquiryTypeList = inquiryManager.getUserIndexInquiryList(false,false);
			}
		}else{
			if (Strings.isNotBlank(spaceId)) {
				inquiryTypeList = inquiryManager.getUserIndexInquiryList(Long.parseLong(spaceId), Integer.parseInt(spaceType), false);
			} else {
				inquiryTypeList = inquiryManager.getUserIndexInquiryList(false,false);
			}
		}
		
//		待审核调查数   此处页面已取消
//		int count = inquiryManager.countCheckInquiryByMember(user.getId());
		
		for(SurveyTypeCompose type : inquiryTypeList){
			
//			判断是否有管理权限
			List<V3xOrgMember> managersList = type.getManagers();
			
			for(V3xOrgMember member : managersList){
				if(user.getId()==member.getId().longValue()){
					type.setHasManageAuth(true);
					type.setHasPublicAuth(true);
					break;
				}
			}
			
//			判断当前用户是否被授权有发布调查权限
			if(!type.isHasPublicAuth()){
				type.setHasPublicAuth(inquiryManager.isInquiryAuthorities(type.getInquirySurveytype().getId()));
			}
			
			List<SurveyBasicCompose> inquiryBasicTempList = new ArrayList<SurveyBasicCompose>();
			
//			循环板块抽取调查
			inquiryBasicTempList = inquiryManager.getSurveyByType(type.getInquirySurveytype().getId());
			
			typeAndBasic.add(inquiryBasicTempList);
			
		}
//去掉已经离职的管理员
		for(SurveyTypeCompose bt: inquiryTypeList){
			List<V3xOrgMember> validManger = new ArrayList<V3xOrgMember>();
			List<V3xOrgMember> managersList = bt.getManagers();
			if(managersList != null){
				for(V3xOrgMember a : managersList){
					if (a.isValid() && !a.getIsDeleted()){
						validManger.add(a);
	   				 }
				}
			}
			bt.setManagers(validManger);
			
		}
		
		
		mav.addObject("typeList", inquiryTypeList);
		mav.addObject("typeAndBasicList", typeAndBasic);
//		mav.addObject("checkerCount", count);
		
		return mav;
	}
	
//	调查查询
	public ModelAndView inquirySearch( HttpServletRequest request, HttpServletResponse response ) throws Exception {		
		ModelAndView mav = null;
		List<SurveyBasicCompose> inquiryBasicList = null; // 调查列表
		String group = request.getParameter("group");
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String textfield1=request.getParameter("textfield1");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		mav = new ModelAndView("inquiry/inquirySearch");
		
		if(group!=null && !group.equals("") && "group".equals(group)){
			inquiryBasicList = inquiryManager.getALLInquiryBasicListByUserID("",condition , textfield , textfield1 , true);
		} else if (group!=null && !group.equals("") && "account".equals(group)) {
			inquiryBasicList = inquiryManager.getALLInquiryBasicListByUserID("",condition , textfield , textfield1 , false);
		} else{
			if(Strings.isNotBlank(spaceId)){
				inquiryBasicList = inquiryManager.getALLCustomInquiryBasicListByUserID(Long.parseLong(spaceId), Integer.parseInt(spaceType), "", condition, textfield, textfield1, false);
			}else{
				inquiryBasicList = inquiryManager.getALLInquiryBasicListByUserID("",condition , textfield , textfield1 , false);
			}
		}
		
		mav.addObject("inquiryBasicList", inquiryBasicList);
		mav.addObject("group", group);		
		return mav;
	}
	
//	取消审核
	public ModelAndView checkCancel( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		String basicId = request.getParameter("bid");
		String typeId = request.getParameter("typeId");
		String group = request.getParameter("group");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		InquirySurveybasic inquiryBasic = (basicId==null ? null : inquiryManager.getBasicByID(Long.valueOf(basicId)));
		User user = CurrentUser.get();
		String userName = user.getName();
		if(inquiryBasic!=null){//数据没删除
			inquiryBasic.setCensor(InquirySurveybasic.CENSOR_NO);
			inquiryBasic.setCheckMind(null);
			inquiryManager.updateSurveyBasic(inquiryBasic);
			
			//取消审核加日志
			appLogManager.insertLog(user, AppLogAction.Inquiry_CancelAudit, userName, inquiryBasic.getSurveyName());
			
			//取消审核同时也需要生成对应的待办事项记录 added by Meng Yang at 2009-07-14
			this.inquiryManager.addPendingAffair(inquiryBasic, ApplicationSubCategoryEnum.inquiry_audit);
		}else{//已经被删除了
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('调查已被管理员删除！')");
			out.println("</script>");
		}
		
		return super.redirectModelAndView("/inquirybasic.do?method=getAllCheck&surveyTypeId="+typeId+"&group="+group+"&spaceType="+spaceType+"&spaceId="+spaceId);
		
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public void setCollaborationController(CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
}