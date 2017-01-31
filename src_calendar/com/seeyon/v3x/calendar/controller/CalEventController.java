
package com.seeyon.v3x.calendar.controller;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.domain.MtReplyCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.cap.meeting.manager.MtReplyManagerCAP;
import com.seeyon.cap.plan.domain.PlanCAP;
import com.seeyon.cap.plan.manager.PlanManagerCAP;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.calendar.constants.CompleteType;
import com.seeyon.v3x.calendar.constants.EventType;
import com.seeyon.v3x.calendar.constants.ShareType;
import com.seeyon.v3x.calendar.domain.AbstractCalEvent;
import com.seeyon.v3x.calendar.domain.CalContent;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalInfo;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalRelation;
import com.seeyon.v3x.calendar.domain.CalEventStatistics;
import com.seeyon.v3x.calendar.domain.CalReply;
import com.seeyon.v3x.calendar.domain.PeriodicalCalEvent;
import com.seeyon.v3x.calendar.manager.CalContentManager;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.calendar.manager.CalEventTranManager;
import com.seeyon.v3x.calendar.manager.CalReplyManager;
import com.seeyon.v3x.calendar.util.CalendarNotifier;
import com.seeyon.v3x.calendar.util.CalendarUtils;
import com.seeyon.v3x.calendar.util.Constants;
import com.seeyon.v3x.calendar.util.Constants.PeriodicalStyle;
import com.seeyon.v3x.calendar.util.PeriodicalEventUtil;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.main.Constant;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.report.chart.ReportChartManager;
import com.seeyon.v3x.report.chart.model.PieChartInfo;
import com.seeyon.v3x.report.chart.tool.ChartUtil;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.ListType;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.Strings;
/**
 * 日程、事件的Controller，对个人、他人事件进行添加、委托、安排、撤销等操作
 * 
 * @author wolf
 * 
 */
public class CalEventController extends BaseController {
	private CalendarUtils calendarUtils;

	private CalContentManager calContentManager;

	private CalEventManager calEventManager;

	private CalEventTranManager calEventTranManager;
	
	private CalReplyManager calReplyManager;

	private AttachmentManager attachmentManager;

	private MetadataManager metadataManager;

	// 关联人员
	private PeopleRelateManager peopleRelateManager;

	private UserMessageManager userMessageManager;

	// 全文检索
	private IndexManager indexManager;

	private UpdateIndexManager updateIndexManager;

	// 组织模型
	private OrgManager orgManager;

	// 关联项目
	private ProjectManager projectManager;
	
	private AffairManager affairManager;
	
	private MtMeetingManagerCAP mtMeetingManagerCAP;
	private MtReplyManagerCAP mtReplyManagerCAP;
	
	public void setMtReplyManagerCAP(MtReplyManagerCAP mtReplyManagerCAP) {
		this.mtReplyManagerCAP = mtReplyManagerCAP;
	}

	private AppLogManager appLogManager;
	
	private FileToExcelManager fileToExcelManager;
	

	private ReportChartManager reportChartManager;
	
	private ProjectPhaseEventManager projectPhaseEventManager;
	
	private PlanManagerCAP planManagerCAP;

	public ReportChartManager getReportChartManager() {
		return reportChartManager;
	}

	public void setReportChartManager(ReportChartManager reportChartManager) {
		this.reportChartManager = reportChartManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public PeopleRelateManager getPeopleRelateManager() {
		return peopleRelateManager;
	}

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	public void setMtMeetingManagerCAP(MtMeetingManagerCAP mtMeetingManagerCAP) {
		this.mtMeetingManagerCAP = mtMeetingManagerCAP;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest arg0, HttpServletResponse arg1)
			throws Exception {
		return null;
	}

	public CalContentManager getCalContentManager() {
		return calContentManager;
	}

	public void setCalContentManager(CalContentManager calContentManager) {
		this.calContentManager = calContentManager;
	}

	public CalEventManager getCalEventManager() {
		return calEventManager;
	}

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	public CalEventTranManager getCalEventTranManager() {
		return calEventTranManager;
	}

	public void setCalEventTranManager(CalEventTranManager calEventTranManager) {
		this.calEventTranManager = calEventTranManager;
	}

	public CalReplyManager getCalReplyManager() {
		return calReplyManager;
	}

	public void setCalReplyManager(CalReplyManager calReplyManager) {
		this.calReplyManager = calReplyManager;
	}

	public CalendarUtils getCalendarUtils() {
		return calendarUtils;
	}

	public void setCalendarUtils(CalendarUtils calendarUtils) {
		this.calendarUtils = calendarUtils;
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}
	
	public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}
	
	public void setPlanManagerCAP(PlanManagerCAP planManagerCAP) {
		this.planManagerCAP = planManagerCAP;
	}

	private static final Log log = LogFactory.getLog(CalEventController.class);
	
	//   功能开始了---- 主要頁面的列表顯示

	/**
	 * ----首页---个人事件---更多
	 */
	public ModelAndView listEventBorder(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//ModelAndView mav = new ModelAndView("calendar/homeEntry");
		ModelAndView modelAndView = new ModelAndView("calendar/event/moreEvent");
		User user = CurrentUser.get();
		String resultValue = request.getParameter("display");
		boolean personal = false;
		boolean relete = false;
		if(Strings.isBlank(resultValue) || resultValue.equals("null")){
			personal = true;
			relete = true;
		}else{
			String[] values = resultValue.split(",");
			for(String v : values){
				if(v.equals("1")){
					personal = true;
				}else if(v.equals("2")){
					relete = true;
				}
			}
		}
		List<CalEvent> eventList = this.calEventManager.getEventListByUserId(user.getId(),personal,relete);
		
		modelAndView.addObject("eventList", FormBizConfigUtils.pagenate(eventList));
		modelAndView.addObject("from", 1);
		
		return modelAndView;
	}

	/**
	 * ----首页---他人事件 ---更多
	 */
	public ModelAndView moreEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("calendar/event/moreEvent");
		String type = request.getParameter("type");
		modelAndView.addObject("type", type);
		List<CalEvent> eventList = this.calEventManager.getOtherEventListByUserIdForFirst(CurrentUser.get(), peopleRelateManager, null, null);
		modelAndView.addObject("eventList", FormBizConfigUtils.pagenate(eventList));
		modelAndView.addObject("from", 2);
		return modelAndView;
	}

	/**
	 * ----首页---部门空间---更多
	 */
	public ModelAndView moreDeptImportentEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CalEvent> eventList = this.calEventManager.getEventListByDeptId(Long.valueOf(request.getParameter("departmentId")));
		return new ModelAndView("calendar/event/moreDeptImportentEvent", "eventList", FormBizConfigUtils.pagenate(eventList));
	}

	/**
	 * ----模块主页
	 */
	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("calendar/homeEntry");
	}

	/**
	 * ----模块主页---主页面
	 */
	public ModelAndView listEventMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("calendar/event/event_list_main");
	}

	/**
	 * ----模块主页---个人事件---获取所有的个人事件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listEvent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		ModelAndView mav = new ModelAndView("calendar/event/event_list_iframe");
		List<CalEvent> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		// 判断是否有条件，如果有条件的话，就走条件查询（条件接口要的参数是id，String，string）
        if (StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)) {
            list = this.calEventManager.getEventListByUserId(userId, condition.toString(), textfield.toString());
        } else {
			// 如果没有条件的话，调用的是个人事件的全部查询按id
			list = this.calEventManager.getEventListByUserId(userId,true,true);
		}
		Map<String, Metadata> calMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.calendar);
		Metadata eventTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		mav.addObject("calMetadata", calMetadata);
		mav.addObject("eventTypeMetadata", eventTypeMetadata);
		mav.addObject("list", CommonTools.pagenate(list));
		mav.addObject("dept", "dept");// 部门返回判断标志
		mav.addObject("condition", condition);
		mav.addObject("field", textfield);
		return mav;
	}

	/**
	 * ----模块主页---共享事件--导航
	 */
	public ModelAndView listToolbar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("calendar/event/event_list_toolbar", "userIsInternal", CurrentUser.get().isInternal());
	}

	/**
	 * ----模块主页---共享事件主列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listOtherEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/event_list_other_iframe");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		if("0".equals(textfield)){
			condition = null;
			textfield = null;
		}
		List<CalEvent> list = this.calEventManager.getOtherEventListByUserIdForMain(CurrentUser.get(), this.peopleRelateManager,condition,textfield);
		mav.addObject("list", FormBizConfigUtils.pagenate(list));
		return mav;
	}

	/**
	 * ----模块主页---共享事件---部门事件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listDeptEvent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("calendar/event/event_list_other_iframe");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		// 调用的是个人事件的全部查询按id.
		List<Long> domainId = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
		List<CalEvent> list = this.calEventManager.getEventListByDeptIds(domainId,condition,textfield);
		List<CalEvent> allEventLists = new ArrayList<CalEvent>();
		if (Strings.isNotBlank(condition) && condition.equals(Constants.RECEIVEMEMBERNAME)) {
			for (CalEvent cal: list) {
				if (textfield.equals(cal.getCreateUserName()) || textfield.equals(cal.getReceiverMember())) {
					allEventLists.add(cal);
				}
			}
		} else {
			allEventLists = list;
		}
		mav.addObject("list", FormBizConfigUtils.pagenate(allEventLists));
		mav.addObject("dept", "dept");// 部门返回判断标志
		return mav;
	}

	/**
	 * ----模块主页---共享事件---项目事件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listItemEvent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String from = request.getParameter("from");
		String projectId = request.getParameter("projectId");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		ModelAndView mav = null;
		List<CalEvent> list = null;
		if (from != null) {
			mav = new ModelAndView("calendar/event/event_project");
			list = this.calEventManager.getItemEventListByUserId(user,Long.parseLong(StringUtils.isNotBlank(projectId)?projectId:"0"),null);
		} else {
			mav = new ModelAndView("calendar/event/event_list_other_iframe");
			
			list = this.calEventManager.getItemEventListByUserId(user,condition,textfield);
			
		}
		List<CalEvent> allEventLists = new ArrayList<CalEvent>();
		if (Strings.isNotBlank(condition) && condition.equals(Constants.RECEIVEMEMBERNAME)) {
			for (CalEvent cal: list) {
				if (textfield.equals(cal.getCreateUserName()) || textfield.equals(cal.getReceiverMember())) {
					allEventLists.add(cal);
				}
			}
		} else {
			allEventLists = list;
		}
		// 调用的是个人事件的全部查询按id
		mav.addObject("list", FormBizConfigUtils.pagenate(allEventLists));
		mav.addObject("dept", "dept");// 部门返回判断标志
		return mav;
	}

	/**
	 * ----模块主页---共享事件---他人事件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listOthersEvent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("calendar/event/event_list_other_iframe");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		// 如果没有条件的话，调用的是个人事件的全部查询按id
		List<CalEvent> list = this.calEventManager.getOtherEventListByUserIdForFirst(user,this.peopleRelateManager,condition,textfield);
		mav.addObject("list", FormBizConfigUtils.pagenate(list));
		mav.addObject("dept", "dept");// 部门返回判断标志
		return mav;
	}

	/**
	 * ----模块主页---共享事件---他人事件---左侧视图
	 * 
	 */
	public ModelAndView relateM(HttpServletRequest request, HttpServletResponse response) {
		Long userId = CurrentUser.get().getId();
		// 判断当前用户是否有上级或是下级
		List<V3xOrgMember> all_Relatlist = new ArrayList<V3xOrgMember>();
		try {
			List<V3xOrgMember> leaderlist = peopleRelateManager.getAllRelateMembers(userId).get(RelationType.leader);
			FormBizConfigUtils.addAllIgnoreEmpty(all_Relatlist, leaderlist);
			
			List<V3xOrgMember> juniorlist = peopleRelateManager.getAllRelateMembers(userId).get(RelationType.junior);
			FormBizConfigUtils.addAllIgnoreEmpty(all_Relatlist, juniorlist);
			
			List<V3xOrgMember> assistantlist = peopleRelateManager.getAllRelateMembers(userId).get(RelationType.assistant);
			FormBizConfigUtils.addAllIgnoreEmpty(all_Relatlist, assistantlist);
			
			List<V3xOrgMember> confrerelist = peopleRelateManager.getAllRelateMembers(userId).get(RelationType.confrere);
			FormBizConfigUtils.addAllIgnoreEmpty(all_Relatlist, confrerelist);
			
			FormBizConfigUtils.filterInvalidEntities(all_Relatlist);
			
		} catch (Exception e) {
			log.error("获取当前用户[id=" + userId + "]对应关联人员出现异常", e);
		}

		return new ModelAndView("calendar/event/event_list_relat_iframe", "all_Relatlist", all_Relatlist);
	}

	/**
	 * ----模块主页---共享事件---他人事件----按关联人员的iD查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
    public ModelAndView listOthersEventByRelat(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = CurrentUser.get();
        String relateId = request.getParameter("relateId");
        ModelAndView mav = new ModelAndView("calendar/event/event_list_other_iframe");
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        List<CalEvent> list = this.calEventManager.getOtherEventListByUserIdForFirst(user, this.peopleRelateManager,
                condition, textfield);
        List<CalEvent> listChild = new ArrayList<CalEvent>();
        if (list != null) {
            Long craeteUserId = Long.valueOf(relateId);
            for (CalEvent calEvent : list) {
                if (calEvent.getCreateUserId().equals(craeteUserId)) {
                    listChild.add(calEvent);
                }
            }
          
        }
        mav.addObject("list", FormBizConfigUtils.pagenate(listChild));
        mav.addObject("dept", "dept");// 部门返回判断标志
        return mav;
    }

	public ModelAndView listSearchResult(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/event_list_other_iframe");
		return mav;
	}
	
	//模块主页---日程视图
	public ModelAndView calViewHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/cal/calViewHome");
		return mav;
	}
	
	//初始化日历视图
	public ModelAndView initPlanCalendar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("calendar/cal/calendarFrame");
    }
	
	/**
	 * 日视图
	 */
	public ModelAndView day(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String from = request.getParameter("from");
		ModelAndView mav = null;
		if ("event".equals(from)) {
			mav = new ModelAndView("calendar/cal/dayFrame");
		} else {
			mav = new ModelAndView("calendar/cal/day");
		}

		// 关联人员事件
		String relateMemberId = request.getParameter("relateMemberId");
		Long memberId = CurrentUser.get().getId();
		if (StringUtils.isNotBlank(relateMemberId)) {
			memberId = Long.parseLong(relateMemberId);
		}

		String selectedDateStr = request.getParameter("selectedDate");
		Date selectedDate = new Date();
		if (StringUtils.isNotBlank(selectedDateStr)) {
			selectedDate = Datetimes.parse(selectedDateStr, "yyyy-MM-dd");
		}
		Date beginDate = Datetimes.getTodayFirstTime(selectedDate);
		Date endDate = Datetimes.getTodayLastTime(selectedDate);

		List<CalEvent> list = this.calEventManager.getAllEventListByUserId(memberId, beginDate, endDate);
		List<CalEvent> list2 = this.calEventManager.preCreateEvent(memberId, beginDate, endDate, 0);
		if (list == null) {
			list = new ArrayList<CalEvent>();
		}
		if (CollectionUtils.isNotEmpty(list2)) {
			list.addAll(list2);
		}
		List<CalEvent> allDayList = new ArrayList<CalEvent>();
		List<CalEvent> otherList = new ArrayList<CalEvent>();
		for (CalEvent event : list) {
			if (event.getBeginDate() == null && event.getEndDate() == null) {// 全天事件
				allDayList.add(event);
			} else {
				otherList.add(event);
			}
		}
		mav.addObject("allDayList", allDayList);
		mav.addObject("otherList", otherList);
		mav.addObject("currentUserId", CurrentUser.get().getId());
		return mav;
	}

	/**
	 * 周视图
	 */
	public ModelAndView week(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String from = request.getParameter("from");
		ModelAndView mav = null;
		if ("event".equals(from)) {
			mav = new ModelAndView("calendar/cal/weekFrame");
		} else {
			mav = new ModelAndView("calendar/cal/week");
		}

		// 关联人员事件
		String relateMemberId = request.getParameter("relateMemberId");
		Long memberId = CurrentUser.get().getId();
		if (StringUtils.isNotBlank(relateMemberId)) {
			memberId = Long.parseLong(relateMemberId);
		}

		String selectedDateStr = request.getParameter("selectedDate");
		Date selectedDate = new Date();
		if (StringUtils.isNotBlank(selectedDateStr)) {
			selectedDate = Datetimes.parse(selectedDateStr, "yyyy-MM-dd");
		}
		//Calendar的一周第一天是星期天，周视图的第一天是星期一，此处特殊处理  huangfj 2012-04-27
		Calendar cal = Calendar.getInstance();
		cal.setTime(selectedDate);
		int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DAY_OF_MONTH, firstDayOfWeek != 1 ? 2 - firstDayOfWeek : -6);
		Date beginDate = Datetimes.getTodayFirstTime(cal.getTime());
		Date endDate = Datetimes.getTodayLastTime(Datetimes.addDate(beginDate, 6));
//		Date beginDate = Datetimes.getFirstDayInWeek(selectedDate);
//		Date endDate = Datetimes.getLastDayInWeek(selectedDate);

		List<List<CalEvent>> weekList = new ArrayList<List<CalEvent>>();
		List<CalEvent> list = new ArrayList();
		List<CalEvent> list1 = this.calEventManager.getAllEventListByUserId(memberId, beginDate, endDate);
		List<CalEvent> list2 = this.calEventManager.preCreateEvent(memberId, beginDate, endDate, 1);
		if (CollectionUtils.isNotEmpty(list1)) {
			list.addAll(list1);
		}
		if (CollectionUtils.isNotEmpty(list2)) {
			list.addAll(list2);
		}

		for (int i = 0; i < 7; i++) {
			Date start = Datetimes.getTodayFirstTime(beginDate);
			Date end = Datetimes.getTodayLastTime(beginDate);
			List<CalEvent> today = new ArrayList<CalEvent>();
			for (CalEvent calEvent : list) {
				if (calEvent.getEndDate().getTime()-start.getTime() >= 0 && calEvent.getBeginDate().getTime() - end.getTime() <= 0) {
					today.add(calEvent);
				}
			}
			weekList.add(today);

			beginDate = Datetimes.addDate(beginDate, 1);
		}
		mav.addObject("list", weekList);
		mav.addObject("currentUserId", CurrentUser.get().getId());
		return mav;
	}

	/**
	 * ----模块主页---日程视图---月视图，以及关联人员视图
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView month(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		String from = request.getParameter("from");
		ModelAndView mav = null;
		if("event".equals(from)){
			mav = new ModelAndView("calendar/cal/monthFrame");
		}else{
			mav = new ModelAndView("calendar/cal/month");
		}
		String selectedDateStr = request.getParameter("selectedDate");
		// 从关联人员过来,取关联人员事件
		String relateMemberId = request.getParameter("relateMemberId");
		Long memberId = CurrentUser.get().getId();
		if (StringUtils.isNotBlank(relateMemberId)) {
			memberId = new Long(relateMemberId);
		}
		Date selectedDate;
		if (Strings.isBlank(selectedDateStr)) {
			selectedDate = new Date();
		} else {
			selectedDate = Datetimes.parseDate(selectedDateStr);
		}

		Date beginDate = Datetimes.getFirstDayInMonth(selectedDate);
		Date endDate = Datetimes.getLastDayInMonth(selectedDate);
		List<CalEvent> list = new ArrayList();
		// 获取某一个月的个人事件列表
		List<CalEvent> list1 = this.calEventManager.getAllEventListByUserId(memberId, beginDate, endDate);
		list.addAll(list1);
		// 按照Map方式组织事件，key为日期（yyyy-MM-dd），value为这一天的所有事件的列表
		Map<String, List<CalEvent>> map = new HashMap<String, List<CalEvent>>();
		List<CalEvent> list2 = this.calEventManager.preCreateEvent(userId, beginDate, endDate,2);
		list.addAll(list2);
		
		CalendarUtils.addtoMap(map, beginDate, endDate, list);
		
		//mav.addObject("list", list);
		mav.addObject("map", map);
		mav.addObject("currentUserId", CurrentUser.get().getId());
		return mav;
	}

	// --------------------------------基本功能

	/**
	 * -----增 展示页面 --显示创建个人事件、他人事件-----新建界面的 初始值设置，只是界面显示的值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView createEvent(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String meb = RequestUtils.getStringParameter(request, "meb");
		String pid = RequestUtils.getStringParameter(request, "from_projectId");
		Long userId = CurrentUser.get().getId();
		ModelAndView mav = new ModelAndView("calendar/event/event_create");
		CalEvent event = new CalEvent();
		// 设置默认值
		event.setAlarmFlag(false);
		event.setPriorityType(1); // 初始默认优先级 1.低
		event.setSignifyType(1); // 重要程度（1.重要紧急）
		event.setShareType(1); // 共享类型--1.私人事件
		event.setCompleteRate(0f);
		event.setRealEstimateTime(0f);
		event.setWorkType(1);
		event.setPeriodicalStyle(0);
		// 待安排
		event.setStates(2); // 事件完成类型（1.待安排 ）--->9.25 改成 2.已安排
		
		// 时间管理-新建
		Date date = new Date();
		String timeS = request.getParameter("time");
		if (Strings.isNotBlank(timeS)) {
			date = Datetimes.parseDatetime(timeS);
		}
		// 事件的显示开始时间，为当前时间的整数状态
		event.setBeginDate(this.getIntegralTime(date));
		Date beginDate = event.getBeginDate();
		// 事件的显示结束时间，为当前时间的整数状态 + 半小时
		event.setEndDate(this.getIntegralEndTime(beginDate));

		mav.addObject("bean", event);
		mav.addObject("create", true);
		// 事件正文内容
		CalContent body = new CalContent();
		body.setContentType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		body.setCreateDate(new Date());
		body.setContent("");
		this.hasSX(mav, userId);// 判断是否有上下级的方法
		// 得到当前用户的关联项目
		List<ProjectSummary> projectList = null;
		try {
			projectList = this.projectManager.getProjectList();
		} catch (Exception e) {
			log.error("创建个人事件时,得到关联项目列表错误!",e);
		}
		// 关联项目
		mav.addObject("projectList", projectList);
		mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
		mav.addObject("oper", request.getParameter("oper"));
		
		Metadata eventTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		mav.addObject("eventTypeMetadata", eventTypeMetadata);

		if (pid != null) {
			ProjectCompose projectCompose = projectManager.getProjectComposeByID(Long.valueOf(pid), true);
			ProjectSummary projectSummary = projectCompose.getProjectSummary();
			String pName = projectSummary.getProjectName();
			mav.addObject("projectName", pName);
			mav.addObject("from_projectId", request.getParameter("from_projectId"));
		}
		//		关联人员
		if (meb != null&&meb.length()!=0) {
			StringBuilder stringBuilder = new StringBuilder("Member|");// 关联人员 新建安排事件的时候，传过来的id:-1245566，应该为:Member|-12345566
			String mebName = this.calendarUtils.getMemberNameByUserId(Long.valueOf(meb));
			mav.addObject("mebName", mebName);
			mav.addObject("meb", stringBuilder.append(meb).toString());
		}
		//周期性处理
		CalEventPeriodicalInfo periodicalInfo = new CalEventPeriodicalInfo();
		periodicalInfo.setPeriodicalType(0);
		int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH);
		int dayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int daydate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		periodicalInfo.setDayDate(daydate);
		periodicalInfo.setWeek(week);
		periodicalInfo.setDayWeek(dayWeek);
		periodicalInfo.setMonth(month+1);
		mav.addObject("periodicalInfo", periodicalInfo);
		return mav;
	}
	
	/**
	 * -----删 删除事件------全部删除---不管是委托者还是创建者
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		String idStr = request.getParameter("id");
		String confirm = request.getParameter("confirm");
		if (StringUtils.isNotEmpty(idStr)) {
			String[] idStrs = idStr.split(",");
			for (String str : idStrs) {
				if (StringUtils.isNotBlank(str)) {
					CalEvent event = this.calEventManager.getEventById(Long.valueOf(str));
					String eventSubject = event!=null?event.getSubject():"";
					Long eventId = event!=null?event.getId():null;
					// 判断是不是有事件已经被删除
					if (event == null) {
						super.rendJavaScript(response, "alert('" + Constants.getResourceStr("error.please.reselect.delete") + "');parent.location.reload();");
						return null;
					} else {
						// 只有创建人才可能删除事件
						if (!event.getCreateUserId().equals(userId)) {
							super.rendJavaScript(response, "alert('" + Constants.getResourceStr("error.please.reselect.4") + "');parent.location.reload();");
							return null;
						}
						
						if(StringUtils.isNotBlank(event.getReceiveMemberId())){//所属人
							switch(EventType.valueOf(event.getEventType())){
							case self:if(userId.longValue() == event.getCreateUserId())break;//如果不是创建人删除，就给创建人消息
							case arrange://安排事件
							case consign://委托事件
								CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.p_ON_DELETE, null, event, this.orgManager, this.userMessageManager, this.calendarUtils);
								break;
							}
						}
						if(event.getShareType().intValue()==5||event.getShareType().intValue()==6){
							CalendarNotifier.sendNotifierMessageCancel(CalendarNotifier.p_ON_DELETE,event,"");//给出删除提示消息
						}
						
						if (event.getCreateUserId().equals(userId)) {
							// 如果是创建者删除的话，就实际删除该条事件，包括正文意见派出去的任务
							this.calReplyManager.deleteByEventId(event.getId());
							this.calContentManager.deleteByEventId(event.getId());
							this.calEventTranManager.deleteByEventId(Long.valueOf(str));
							this.calEventManager.deleteById(Long.valueOf(str));

						} else {
							// //被委托人删除事件，则只删除该用户的任务，并不实际删除这条事件---以前的
							this.calEventTranManager.deleteByEventAndUserId(Long.valueOf(str), userId);
							// 接受者也全删
							this.calReplyManager.deleteByEventId(event.getId());
							this.calContentManager.deleteByEventId(event.getId());
							this.calEventTranManager.deleteByEventId(Long.valueOf(str));
							this.calEventManager.deleteById(Long.valueOf(str));
						}
						//如果是周期性事件，选择删除周期性的所有事件
						if(Strings.isNotBlank(confirm) && "all".equals(confirm)){
							CalEventPeriodicalInfo info = calEventManager.getPeriodicalInfoByCalEventId(Long.valueOf(str));
							List<Long> list = new ArrayList<Long>();
							list.add(info.getId());
							calEventManager.deletePeriodicalByIds(list);
							CalEventPeriodicalRelation relation = calEventManager.getCalEventPeriodicalRelation(Long.valueOf(str));
							List<CalEvent> events = calEventManager.getAllPeriodicalEventByPeriodicalInfoId(relation.getCalEventPeriodicalInfoId());
							for(CalEvent e : events){
								if(e.getId() != Long.valueOf(str)){
									this.calReplyManager.deleteByEventId(e.getId());
									this.calContentManager.deleteByEventId(e.getId());
									this.calEventTranManager.deleteByEventId(e.getId());
									this.calEventManager.deleteById(e.getId());
								}
							}
						}
					}
					try {
						indexManager.deleteFromIndex(ApplicationCategoryEnum.calendar, eventId);
					} catch (Exception e) {
						log.error("全文检索删除库", e);
					}
					//将 日程的删除 写入操作日志。
					appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_Delete, CurrentUser.get().getName(),eventSubject);
				}
			}
		}
		
		// 个人事件
		return this.redirectModelAndView("/calEvent.do?method=listEventMain");
	}
	
	public ModelAndView editIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if ((Boolean) (BrowserFlag.OpenDivWindow.getFlag(request))) {
			ModelAndView mav = new ModelAndView("calendar/event/event_create_iframe");
			return mav;
		} else {
			return this.edit(request, response);
		}
	}

	/**
	 * -----改 显示修改事件的对话框
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/event_create");
		String idStr = request.getParameter("id");
		String periodicalId = request.getParameter("periodicalId");
		if (StringUtils.isEmpty(idStr) && Strings.isBlank(periodicalId))
			return this.createEvent(request, response);
		AbstractCalEvent event = null;
		if(Strings.isNotBlank(periodicalId)){
			CalEventPeriodicalInfo periodical = this.calEventManager.getPeriodicalInfo(Long.parseLong(periodicalId));
			if(periodical != null){
				AbstractCalEvent trueEvent = periodical.getCalEvent();
				event = (AbstractCalEvent)trueEvent.clone();
				event.setId(trueEvent.getId());
				String startDate = request.getParameter("beginDate");
				if(Strings.isNotBlank(startDate)){
					setEventDate(event, startDate);
					mav.addObject("virtual", true);
				}
				mav.addObject("periodicalBean", periodical);
			}
		}else{
			Long eventId = Long.valueOf(idStr);
			event = this.calEventManager.getEventById(eventId);
		}
		// 如果事件为空的话，则证明已经删除，跳出
		if (event == null) {
			super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.delete")));
			return null;
		}
		// 得到内容
		CalContent body = this.calContentManager.getEventContentByEventId(event.getId());
		mav.addObject("bean", event);
		mav.addObject("body", body);
		mav.addObject("oper", "new");

		// 获取附件的地方
		List<Attachment> attachments = event.getAttachmentsFlag() ? attachmentManager.getByReference(event.getId(), event.getId()) : null;
		mav.addObject("attachments", attachments);
		mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));

		Metadata eventTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		mav.addObject("eventTypeMetadata", eventTypeMetadata);
		
		// 获取回复意见
		List<CalReply> replyList = this.calReplyManager.getReplyListByEventId(event.getId());
		mav.addObject("replyList", replyList);
		
		// 如果当前用户不是创建者
		User user = CurrentUser.get();
		Long userId = user.getId();
		Long creatorId = event.getCreateUserId();
		//会议转日程时数据库中有两条相同记录（主键和创建人不同），此处用于关联人员界面日历查看
		boolean isUserCreator = userId.equals(creatorId) || event.getFromType() == 6;
		
		if (!isUserCreator) 
			mav.addObject("isDisabled", true);
		
		// 关联项目
		List<ProjectSummary> projectList = null;
		try {
			projectList = this.projectManager.getProjectList();
		} catch (Exception e) {
			log.error("得到日程事件的关联项目错误",e);
		}
		mav.addObject("projectList", projectList);
		
		//判断当前用户是否在所属人(被委托人、被安排人等)中
		String receive = event.getReceiveMemberId();
		boolean isUserInReceivers = Strings.isNotBlank(receive) ? (receive.indexOf(userId.toString())!=-1) : false;
		
		String onlyLook = request.getParameter("onlyLook");
		
		//得到当前用户所有的单位，部门，人员
		List<V3xOrgEntity> entitys = orgManager.getUserDomain(CurrentUser.get().getId(), V3xOrgEntity.ORGENT_TYPE_ACCOUNT, 
											V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, V3xOrgEntity.ORGENT_TYPE_MEMBER);
		boolean isCanView = calEventTranManager.validateCurrentUserIsCanViewEvent(event.getId(), entitys);
		
		if(isCanView || isUserCreator || (event.getShareType()==ShareType.project.key() && isContainId(projectList, event.getTranMemberIds())) ||
				event.getShareType() == ShareType.superior.key() || event.getShareType() == ShareType.junior.key() || event.getShareType() == ShareType.assistant.key()) {
			if(StringUtils.isBlank(onlyLook)) {
				if(StringUtils.isNotBlank(receive)) {
					if(!isUserInReceivers){  //当前用户不是所属人
						onlyLook = "false";
					}
				} else {
					if(isContainMemberId(event.getReceiveMemberId())) {
						onlyLook = "false";
					} else {
						if(event.getEventType()==EventType.self.key()) {
							if((event.getShareType()==ShareType.publicity.key() || event.getShareType()==ShareType.department.key() || 
									event.getShareType()==ShareType.project.key()) && !isUserCreator){
								onlyLook = "true";
							} else {
								onlyLook = isUserCreator ? null : "false";
							}
						} else {
							onlyLook = "true";
						}
					}
				}
			}
			
			if(isUserCreator)
				onlyLook = null;
		} else {
			String creatorName = this.orgManager.getMemberById(event.getCreateUserId()).getName();
			super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.8.1", creatorName)));
			return null;
		}
		//周期性处理
		if(event.getPeriodicalStyle() != PeriodicalStyle.None.ordinal()){
			CalEventPeriodicalInfo periodicalInfo = null;
			if(Strings.isNotBlank(periodicalId)){
				periodicalInfo = calEventManager.getPeriodicalInfo(Long.parseLong(periodicalId));
			} else {
				periodicalInfo = calEventManager.getPeriodicalInfoByCalEventId(Long.valueOf(idStr));
			}
			PeriodicalCalEvent periodicalEvent = calEventManager.getPeriodicalInfo(periodicalInfo.getId()).getCalEvent();
			// 得到内容
			CalContent body2 = this.calContentManager.getEventContentByEventId(periodicalEvent.getId());
			int perType = periodicalInfo.getPeriodicalType();
			String periodicalType = "";
			switch(perType){
				case 1 :
					periodicalType = Constants.PeriodicalType.EveryDay.name();
					break;
				case 2 :
					periodicalType = Constants.PeriodicalType.EveryWeek.name();
					break;
				case 3 :
					periodicalType = Constants.PeriodicalType.EveryMonthDay.name();
					break;
				case 4 :
					periodicalType = Constants.PeriodicalType.EveryMonthWeekDay.name();
					break;
				case 5 :
					periodicalType = Constants.PeriodicalType.EveryYearMonthDay.name();
					break;
				case 6 :
					periodicalType = Constants.PeriodicalType.EveryYearMonthWeekDay.name();
					break;
			}
			mav.addObject("periodicalType", periodicalType);
			mav.addObject("body2",body2);
			mav.addObject("periodicalInfo", periodicalInfo);
		}
		
		mav.addObject("onlyLook", onlyLook);
		
		//共享类型为项目事件
		if (event.getShareType()==ShareType.project.key()) {
			ProjectSummary project = this.projectManager.getProject(Long.valueOf(event.getTranMemberIds()));
			mav.addObject("project", project).addObject("isContain", isContainId(projectList,event.getTranMemberIds()));
		}
		mav.addObject("edit", "true");

		return mav.addObject("subject", event.getFromId()!=0 ? getFromSubject(event.getFromId(), event.getFromType()) : "nosubject");
	}
	
	/**得到事件的全部回复**/
	public ModelAndView showAllReply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/event_reply");
		String idStr = request.getParameter("eventId");
		if(Strings.isNotBlank(idStr)){
			Long eventId = Long.valueOf(idStr);
			List<CalReply> replyList = this.calReplyManager.getReplyListByEventId(eventId);
			mav.addObject("replyList", replyList);
			return mav;
		}else{
			return null;
		}
	}

	private void setEventDate(AbstractCalEvent event, String startDate) {
		int betweenDay = (int)Datetimes.minusDay(event.getEndDate(), event.getBeginDate());
		Date beginDate = Datetimes.getTodayFirstTime(startDate);
		Date endDate = Datetimes.addDate(beginDate, betweenDay);
		
		GregorianCalendar eventBegin = new GregorianCalendar();
		eventBegin.setTime(event.getBeginDate());
		
		beginDate = Datetimes.addHour(beginDate, eventBegin.get(Calendar.HOUR_OF_DAY));
		beginDate = Datetimes.addMinute(beginDate, eventBegin.get(Calendar.MINUTE));
		
		GregorianCalendar eventEnd = new GregorianCalendar();
		eventEnd.setTime(event.getEndDate());
		endDate = Datetimes.addHour(endDate, eventEnd.get(Calendar.HOUR_OF_DAY));
		endDate = Datetimes.addMinute(endDate, eventEnd.get(Calendar.MINUTE));

		event.setBeginDate(beginDate);
		event.setEndDate(endDate);
	}
	
	private TaskInfoManager taskInfoManager;
	public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
		this.taskInfoManager = taskInfoManager;
	}

	private String getFromSubject(Long id,int type){
		String subject = "";
		switch(ApplicationCategoryEnum.valueOf(type)){
		case collaboration:
			Affair affair = affairManager.getById(id);
			subject = (affair!=null && !affair.getIsDelete())?affair.getSubject():null;
			break;
		case meeting:
			MtMeetingCAP mtMeeting = mtMeetingManagerCAP.getById(id);
			subject = mtMeeting!=null?mtMeeting.getTitle():null;
			break;
		case taskManage:
			TaskInfo task = taskInfoManager.get(id);
			subject = task != null ? task.getSubject() : null;
			break;
		}
		return subject;
	}
	
	/**
	 * 编辑项目事件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editProjectEvent(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String idStr = request.getParameter("id");
		CalEvent event  = StringUtils.isNotBlank(idStr)?this.calEventManager.getEventById(Long.valueOf(idStr)):null;
		if(event!=null&&event.getShareType()!=ShareType.project.key()){
			super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.project")));
			return null;
		}else{
			ModelAndView mav = new ModelAndView("calendar/event/event_create_iframe");
			return mav;
		}
	}
	
	/**
	 * 遍历项目List判断是否包含给定的项目id
	 * @param projectList
	 * @param id
	 * @return
	 */
	private boolean isContainId(List<ProjectSummary> projectList,String id){
		if(CollectionUtils.isNotEmpty(projectList) && StringUtils.isNotBlank(id)){
			for(ProjectSummary summary : projectList){
				if(summary.getId() == Long.parseLong(id)){
					return true;
				}
			}
		}
		return false;
	}
	
	public ModelAndView saveVirtualEvent(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String isUpdateAll = request.getParameter("isUpdateAll");
		String confirm = request.getParameter("confirm");
		
		saveCalEvent(request, response,false);
		//保存关联
		Long eventId = (Long)request.getAttribute("eventId");
		CalEvent cal = this.calEventManager.getEventById(eventId);
		String periodicalIdStr = request.getParameter("perioId");
		if(cal != null){
			CalEventPeriodicalInfo periodical = this.calEventManager.getPeriodicalInfo(Long.parseLong(periodicalIdStr));
			//更新所有序列
			PeriodicalCalEvent periodicalEvent = periodical.getCalEvent();
			if(Strings.isNotBlank(confirm) && "all".equals(confirm)){
				periodicalEvent.updateByEvent(cal);
				attachmentManager.deleteByReference(periodicalEvent.getId(), periodicalEvent.getId());
				String ret = this.attachmentManager.copy(eventId, eventId, periodicalEvent.getId(), periodicalEvent.getId(), ApplicationCategoryEnum.calendar.key());
				if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(ret)) {
					periodicalEvent.setAttachmentsFlag(true);
				}else{
					periodicalEvent.setAttachmentsFlag(false);
				}
				CalContent body = this.calContentManager.getEventContentByEventId(periodicalEvent.getId());
				if(body == null){
					body = new CalContent();
				}
				// 处理正文
				String bodyContent = RequestUtils.getStringParameter(request, "content", "");
				body.setContent(bodyContent);
				body.setContentType(RequestUtils.getStringParameter(request,"bodyType",com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML));
				body.setCreateDate(Datetimes.parse(getNowDateString()));
				
				body.setEventId(periodicalEvent.getId());
				this.calContentManager.save(body);
				
				periodical.setCalEvent(periodicalEvent);
				//更新
				this.calEventManager.saveOrUpdate(periodical, false);
				
				//如果安排或委托人员为空，将事件类型改回为：自建、初始
				if(Strings.isBlank(periodicalEvent.getReceiveMemberId()) && periodicalEvent.getEventflag() != Constants.EVENT_FLAG_0) {
					periodicalEvent.setEventflag(Constants.EVENT_FLAG_0);
					periodicalEvent.setEventType(EventType.self.getKey());
				}
				switchSaveTranEvent(periodicalEvent, null, null, false);
			}
			if(Strings.isNotBlank(eventId.toString())){
				CalEventPeriodicalRelation relation = new CalEventPeriodicalRelation();
				relation.setIdIfNew();
				relation.setCalEventId(eventId);
				relation.setCalEventPeriodicalInfoId(Long.parseLong(periodicalIdStr));
				relation.setMemberId(periodicalEvent.getCreateUserId());
				
				String periodicalStartDate = request.getParameter("periodicalStartDate");
				relation.setCreateDate(PeriodicalEventUtil.getPeridicaiCreateDate(periodicalEvent,Datetimes.parseDatetimeWithoutSecond(periodicalStartDate)));
				
				this.calEventManager.savePeriodicalEventRelition(relation);
				if(Strings.isNotBlank(confirm) && "all".equals(confirm)){
					//周期性批量修改
					List<CalEvent> events = calEventManager.getAllPeriodicalEventByPeriodicalInfoId(relation.getCalEventPeriodicalInfoId());
					for(CalEvent e : events){
						if(e.getId() != eventId){
							e.updateByEvent(cal);
							calEventManager.save(e, false);
						}
					}
				}
			}
		}
		super.rendJavaScript(response, "window.returnValue='true';window.close();");
		return null;
	}
	/**
	 * 新建--保存周期性事件
	 */
	private void savePeriodicalEvent(HttpServletRequest request,AbstractCalEvent event, boolean isNew) throws Exception{
		Long userId = CurrentUser.get().getId();
		String periodicalType = request.getParameter("periodicalType");
		String confirm = request.getParameter("confirm");
		PeriodicalCalEvent periodicalEvent = null;
		CalEventPeriodicalInfo periodical = null;
		//记录保存前的所属人(被安排和委托的对象)
		String oldReceivers = event.getReceiveMemberId();
		Integer oldShareType = event.getShareType();
		String oldTranMemberIds = event.getTranMemberIds();
		if(isNew){
			periodicalEvent = new PeriodicalCalEvent(event);
			periodical = new CalEventPeriodicalInfo();
			periodical.setIdIfNew();
			periodical.setMemberId(userId);
		} else if(Strings.isNotBlank(confirm) && "all".equals(confirm)) {
			periodical = calEventManager.getPeriodicalInfoByCalEventId(event.getId());
			periodicalEvent = calEventManager.getPeriodicalInfo(periodical.getId()).getCalEvent();
			periodicalEvent.updateByEvent(event);
		}
		if(isNew || (Strings.isNotBlank(confirm) && "all".equals(confirm))){
			periodical.setCalEvent(periodicalEvent);
			periodical.setCalEventId(periodicalEvent.getId());
			int day_date = NumberUtils.toInt(request.getParameter("day_date"), -1);
			int day_week = NumberUtils.toInt(request.getParameter("day_week"), -1);
			int week = NumberUtils.toInt(request.getParameter("week"), -1);
			int month = NumberUtils.toInt(request.getParameter("month"), -1);
			String weeks = request.getParameter("weeks");
			String begintime = request.getParameter("begintime");
			String endtime = request.getParameter("endtime");
			periodical.setBeginTime(Datetimes.parse(begintime));
			if(Strings.isNotBlank(endtime)){
				periodical.setEndTime(Datetimes.parse(endtime));
			} else {
				periodical.setEndTime(null);
			}
			setPeriodicalType2(periodicalType, periodical, day_date, day_week, week, month, weeks);
			calEventManager.saveOrUpdate(periodical, isNew);
			CalEventPeriodicalRelation relation = new CalEventPeriodicalRelation();
			relation.setIdIfNew();
			relation.setCalEventPeriodicalInfoId(periodical.getId());
			relation.setCalEventId(event.getId());
			relation.setCreateDate(new Date());
			relation.setMemberId(userId);
			calEventManager.savePeriodicalEventRelition(relation);
			
			switchSaveTranEvent(periodicalEvent, null, null, isNew,true);
			
			String bodyId = request.getParameter("bodyId2");
			CalContent body = Strings.isEmpty(bodyId) ? new CalContent() : calContentManager.getContentById(Long.valueOf(bodyId));
			// 处理正文
			String bodyContent = RequestUtils.getStringParameter(request, "content", "");
			body.setContent(bodyContent);
			body.setContentType(RequestUtils.getStringParameter(request,"bodyType",com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML));
			body.setCreateDate(Datetimes.parse(getNowDateString()));
			body.setEventId(periodicalEvent.getId());
			try{
				this.calContentManager.save(body);
			}catch(Exception e){
				log.error("", e);
			}
		}
	}
	/**
	 * 新建-保存周期性事件 
	 */
	public ModelAndView savePeriodicalEvent(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Long userId = CurrentUser.get().getId();
		CalEventPeriodicalInfo periodical = null;
		PeriodicalCalEvent event = null;
		String periodicalType = request.getParameter("periodicalType");
		String perioId = request.getParameter("perioId");
		boolean isNew = StringUtils.isEmpty(perioId);
		
		/* 取消在周期性事件中创建一次性事件
		if("Once".equals(periodicalType)){
			return saveCalEvent(request, response,true);
		}*/
		//开始时间是今天，新建
		String beginDate = request.getParameter("beginDate");
		Date today = Datetimes.getTodayFirstTime();
		Date begin = Datetimes.getTodayFirstTime(beginDate);
		String todayStr = Datetimes.formatDate(today);
		String beginStr = Datetimes.formatDate(begin);
		boolean isCreateEvent = isNew && todayStr.equals(beginStr);
		if(isCreateEvent){
			//生成事件
			saveCalEvent(request, response,false);
		}

		if (isNew) {
			event = new PeriodicalCalEvent();
			periodical = new CalEventPeriodicalInfo();
			periodical.setIdIfNew();
			periodical.setMemberId(userId);
		} else {
			periodical = this.calEventManager.getPeriodicalInfo(Long.parseLong(perioId));
			// 判断事件是否被删除如果被删除的话，给出提示信息，刷新界面
			if (periodical == null) {
				super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.delete")));
			}else{
				event = periodical.getCalEvent();
			}
			periodicalType = EnumUtil.getEnumByOrdinal(Constants.PeriodicalType.class, periodical.getPeriodicalType()).toString();
		}
		
		String bodyId = request.getParameter("bodyId");
		CalContent body = Strings.isEmpty(bodyId) ? new CalContent() : calContentManager.getContentById(Long.valueOf(bodyId));
		super.bind(request, event);
		
		boolean isAlarmFlag = Strings.isNotBlank(request.getParameter("alarmFlag"));
		event.setAlarmFlag(isAlarmFlag);
		
		switchSetShareTypeVale(event,request);
		// 处理正文
		String bodyContent = RequestUtils.getStringParameter(request, "content", "");
		body.setContent(bodyContent);
		body.setContentType(RequestUtils.getStringParameter(request,"bodyType",com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML));
		body.setCreateDate(Datetimes.parse(getNowDateString()));
		
		// 新建事件------------的字段的设置----------保存在主表
		if (event.isNew()) {
			event.setAlarmDate(event.getAlarmDate());
			event.setCreateDate(body.getCreateDate());
			event.setCreateUserId(userId);
		} else {
			event.setUpdateDate(new Date());
		}
		// 新增加的接受者
		if (StringUtils.isNotBlank(event.getReceiveMemberId())) {
			event.setReceiveMemberId(event.getReceiveMemberId());
			event.setReceiveMemberName(event.getReceiveMemberName());
			if(event.getEventType() == EventType.self.key()) {
				event.setEventflag(1);
				event.setEventType(2);
			}
		}
		if(StringUtils.isNotBlank(event.getTranMemberIds())){
			event.setEventType(2);
		}
		event.setIdIfNew();
		if (isNew){
			event.setFromId(0);
			event.setFromType(0);
			appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_New, CurrentUser.get().getName(),event!=null?event.getSubject():"");
		} else {
			appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_Update, CurrentUser.get().getName(),event!=null?event.getSubject():"");
		}
		String ret = "";
		if(isCreateEvent){
			Long eventId = (Long) request.getAttribute("eventId");
			ret = this.attachmentManager.copy(eventId, eventId, event.getId(), event.getId(), ApplicationCategoryEnum.calendar.key());
		}else{
			ret = attachmentManager.update(ApplicationCategoryEnum.calendar,event.getId(), event.getId(), request);
		}
		if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(ret)) {
			event.setAttachmentsFlag(true);
		}else{
			event.setAttachmentsFlag(false);
		}
		periodical.setCalEvent(event);
		periodical.setCalEventId(event.getId());
		setPeriodicalType(periodicalType,periodical,begin);
		// 首先保存事件id，然后保存
		//Long id = this.calEventManager.save(event, isNew);
		calEventManager.saveOrUpdate(periodical, isNew);
		
		body.setEventId(event.getId());
		try{
			this.calContentManager.save(body);
		}catch(Exception e){
			log.error("", e);
		}
		
		//如果安排或委托人员为空，将事件类型改回为：自建、初始
		if(Strings.isBlank(event.getReceiveMemberId()) && event.getEventflag() != Constants.EVENT_FLAG_0) {
			event.setEventflag(Constants.EVENT_FLAG_0);
			event.setEventType(EventType.self.getKey());
		}
		PrintWriter out = response.getWriter();
		out.print("<script type='text/javascript'>");
		String js = "if(window.dialogArguments){window.returnValue='true';}";
		out.print(js);
		out.print("window.close();");
		out.print("</script>");
		out.close();
		return null;
	}
	/**
	 *  设置周期性信息
	 * @param type
	 * @param periodical
	 * @param day_date
	 * @param day_week
	 * @param week
	 * @param month
	 * @param weeks
	 */
	private void setPeriodicalType2(String type, CalEventPeriodicalInfo periodical, int day_date, int day_week, int week, int month, String weeks){
		Constants.PeriodicalType perType = Constants.PeriodicalType.valueOf(type);
		if(perType != null){
			periodical.setPeriodicalType(perType.ordinal());
			periodical.clearSet();
			switch(perType){
			case EveryDay : 
				periodical.setDayDate(day_date);
				break; 
			case EveryWeek:
				periodical.setWeeks(weeks);
				break;
			case EveryMonthDay:
				periodical.setDayDate(day_date);
				break;
			case EveryMonthWeekDay:
				periodical.setWeek(week);
				periodical.setDayWeek(day_week);
				break;
			case EveryYearMonthDay:
				periodical.setMonth(month);
				periodical.setDayDate(day_date);
				break;
			case EveryYearMonthWeekDay:
				periodical.setMonth(month);
				periodical.setWeek(week);
				periodical.setDayWeek(day_week);
				break;
			}
		}
	}
	
	
	private void setPeriodicalType(String type,CalEventPeriodicalInfo periodical,Date begin){
		Constants.PeriodicalType perType = Constants.PeriodicalType.valueOf(type);
		if(perType != null){
			periodical.setPeriodicalType(perType.ordinal());
			periodical.clearSet();
			
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(begin);
			
			int day_date = calendar.get(Calendar.DAY_OF_MONTH);
			int day_week = calendar.get(Calendar.DAY_OF_WEEK);
			int week = calendar.get(Calendar.WEEK_OF_MONTH);
			int month = calendar.get(Calendar.MONTH)+1;

			switch(perType){
			case EveryDay:break;
			case EveryWeek:
				periodical.setDayWeek(day_week);
				break;
			case EveryMonthDay:
				periodical.setDayDate(day_date);
				break;
			case EveryMonthWeekDay:
				periodical.setWeek(week);
				periodical.setDayWeek(day_week);
				break;
			case EveryYearMonthDay:
				periodical.setMonth(month);
				periodical.setDayDate(day_date);
				break;
			case EveryYearMonthWeekDay:
				periodical.setMonth(month);
				periodical.setWeek(week);
				periodical.setDayWeek(day_week);
				break;
			}
		}
	}
	public ModelAndView saveEvent(HttpServletRequest request,HttpServletResponse response) throws Exception {
		return saveCalEvent(request, response,true);
	}
	/**
	 * -----存 新建，修改的数据保存
	 */
	public ModelAndView saveCalEvent(HttpServletRequest request,HttpServletResponse response,boolean needBack) throws Exception {
		Long userId = CurrentUser.get().getId();
		CalEvent event = null;
		
		// oper=plan 的时候是安排 oper!=plan的时候不是安排事件
		String oper = RequestUtils.getStringParameter(request, "oper", "");
		String idStr = request.getParameter("id");
		boolean isNew = StringUtils.isEmpty(idStr);
		//记录修改之前的提醒状态和提醒时间，便于和修改后进行比较判断
		boolean oldAlarmFlag = false;
		Date oldRemindDate = null;
		
		if (isNew) {
			event = new CalEvent();
		} else {
			event = this.calEventManager.getEventById(Long.valueOf(idStr));
			// 判断事件是否被删除如果被删除的话，给出提示信息，刷新界面
			if (event == null) {
				super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.delete")));
				return null;
			}
			// 所属人如果已经被创建者取消安排或委托，给出提示信息，刷新界面
			if(!userId.equals(event.getCreateUserId()) && 
					(Strings.isBlank(event.getReceiveMemberId()) || event.getReceiveMemberId().indexOf(userId.toString())==-1)) {
				super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.notinreceivers")));
				return null;
			}
			
			oldAlarmFlag = event.isAlarmFlag();
			if(oldAlarmFlag)
				oldRemindDate = event.getRemindTime();
			
		}
		String bodyId = request.getParameter("bodyId");
		CalContent body = Strings.isEmpty(bodyId) ? new CalContent() : calContentManager.getContentById(Long.valueOf(bodyId));
		
		//记录保存前的所属人(被安排和委托的对象)
		String oldReceivers = event.getReceiveMemberId();
		Integer oldShareType = event.getShareType();
		String oldTranMemberIds = event.getTranMemberIds();
		super.bind(request, event);
		
		boolean isAlarmFlag = Strings.isNotBlank(request.getParameter("alarmFlag"));
		event.setAlarmFlag(isAlarmFlag);
		
		switchSetShareTypeVale(event,request);
		// 处理正文
		if (!oper.equals("complete")) {
			String bodyContent = RequestUtils.getStringParameter(request, "content", "");
//			if(!isNew)
//				bodyContent += ("\n" + Constants.getResourceStr("cal.modified.record", CurrentUser.get().getName(), Datetimes.format(new Date(), Datetimes.datetimeStyle)));
			
			body.setContent(bodyContent);
			body.setContentType(RequestUtils.getStringParameter(request,"bodyType",com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML));
			body.setCreateDate(new Date());
		}
		// 新建事件------------的字段的设置----------保存在主表
		if (event.isNew()) {
			if(!oper.equals("plan") && event.getShareType().intValue()==ShareType.project.key() && event.getProjectId()==null){
				super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.noproject")));
				return null;
			}
			event.setAlarmDate(event.getAlarmDate());
			event.setCreateDate(body.getCreateDate());
			event.setCreateUserId(userId);
		} else {
			event.setUpdateDate(new Date());
		}
		
		// 新增加的接受者
		if (StringUtils.isNotBlank(event.getReceiveMemberId())) {
			event.setReceiveMemberId(event.getReceiveMemberId());
			event.setReceiveMemberName(event.getReceiveMemberName());
			if(event.getEventType() == EventType.self.key()) {
				event.setEventflag(1);
				event.setEventType(2);
			}
		}
		if(StringUtils.isNotBlank(event.getTranMemberIds())){
			event.setEventType(2);
		}

		if (isNew){
			event.setIdIfNew();
			String formId = request.getParameter("fromId");
			String formType = request.getParameter("fromType");
			event.setFromId(Long.parseLong(formId!=null?formId:"0"));
			event.setFromType(Integer.parseInt(formType!=null?formType:"0"));
			appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_New, CurrentUser.get().getName(),event!=null?event.getSubject():"");
		} else {
			appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_Update, CurrentUser.get().getName(),event!=null?event.getSubject():"");
		}
		String periodicalIdStr = request.getParameter("perioId");
		String ret = "";
		if(Strings.isNotBlank(periodicalIdStr)){
			CalEventPeriodicalInfo periodical = this.calEventManager.getPeriodicalInfo(Long.parseLong(periodicalIdStr));
			//更新所有序列
			PeriodicalCalEvent periodicalEvent = periodical.getCalEvent();
			Long tempId = UUIDLong.longUUID();
			attachmentManager.copy(periodicalEvent.getId(), periodicalEvent.getId(), tempId, tempId, ApplicationCategoryEnum.calendar.ordinal());
			attachmentManager.update(ApplicationCategoryEnum.calendar,periodicalEvent.getId(), periodicalEvent.getId(), request);
			ret = attachmentManager.copy(periodicalEvent.getId(), periodicalEvent.getId(), event.getId(), event.getId(), ApplicationCategoryEnum.calendar.ordinal());
			attachmentManager.deleteByReference(periodicalEvent.getId(), periodicalEvent.getId());
			attachmentManager.copy(tempId, tempId, periodicalEvent.getId(), periodicalEvent.getId(), ApplicationCategoryEnum.calendar.ordinal());
			attachmentManager.deleteByReference(tempId, tempId);
		}else{
			ret = attachmentManager.update(ApplicationCategoryEnum.calendar,event.getId(), event.getId(), request);
		}
		if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(ret)) {
			event.setAttachmentsFlag(true);
		}else{
			event.setAttachmentsFlag(false);
		}
		
		//如果有提醒，生成任务调度：新建提醒，或修改时，提醒时间发生了变化
		if(isAlarmFlag) {
			if(isNew || this.isRemindTimeChanged(oldAlarmFlag, oldRemindDate, isAlarmFlag, event.getRemindTime()))
				calEventManager.eventRemind(event, isNew);
		}
		
		//修改事件时取消了提醒，删除任务调度
		if(!isAlarmFlag && !isNew)
			calEventManager.cancelRemind(event.getId());
		
		calEventManager.beforEndRemind(event);
		String periodicalStyle = request.getParameter("periodicalStyle");
 		event.setPeriodicalStyle(NumberUtils.toInt(periodicalStyle));
		
		// 首先保存事件id，然后保存
		Long id = this.calEventManager.save(event, isNew);
		request.setAttribute("eventId", id);
		
		//周期性事件批量修改
		String confirm = request.getParameter("confirm");
		if(Strings.isNotBlank(confirm) && "all".equals(confirm) && Strings.isBlank(request.getParameter("virtual"))){
			CalEventPeriodicalRelation relation = calEventManager.getCalEventPeriodicalRelation(id);
			List<CalEvent> events = calEventManager.getAllPeriodicalEventByPeriodicalInfoId(relation.getCalEventPeriodicalInfoId());
			for(CalEvent e : events){
				if(e.getId() != id){
					e.updateByEvent(event);
					calEventManager.save(e, isNew);
				}
			}
		}
		
		//保存周期性信息
		if(NumberUtils.toInt(periodicalStyle) != PeriodicalStyle.None.ordinal() && Strings.isBlank(request.getParameter("virtual"))){
			savePeriodicalEvent(request, event, isNew);
		}
		
		body.setEventId(id);
		if (!oper.equals("complete")) {
			this.calContentManager.save(body);
		}
		
		//如果安排或委托人员为空，将事件类型改回为：自建、初始
		if(Strings.isBlank(event.getReceiveMemberId()) && event.getEventflag() != Constants.EVENT_FLAG_0) {
			event.setEventflag(Constants.EVENT_FLAG_0);
			event.setEventType(EventType.self.getKey());
			
			if(Strings.isNotBlank(oldReceivers)) {
				userMessageManager.sendSystemMessage(
						new MessageContent("cal.cancel", event.getSubject(), CurrentUser.get().getName()),
						ApplicationCategoryEnum.calendar, CurrentUser.get().getId(), 
						MessageReceiver.get(event.getId(), FormBizConfigUtils.parseTypeAndIdStr2Ids(oldReceivers))
						);
			}
		}
		
		if(Strings.isNotBlank(event.getReceiveMemberId())) {
			switch(EventType.valueOf(event.getEventType())){
			case arrange://安排事件
				CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.P_ON_PLAN, oldReceivers, event, this.orgManager,
						this.userMessageManager, this.calendarUtils);
				break;
			case consign://委托事件
				CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.P_ON_TRANS, oldReceivers, event, this.orgManager,
						this.userMessageManager, this.calendarUtils);
				break;
			}
		}
		
		switchSaveTranEvent(event, oldShareType, oldTranMemberIds, isNew);
		
		String calEventType = request.getParameter("calEventType");
		if(!Strings.isBlank(calEventType)){
			long iad = Long.valueOf(calEventType).longValue();
			Metadata metadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
			if(null!=metadata){
				MetadataItem item = metadataManager.getMetadataItem(metadata.getName(), Long.valueOf(iad).toString());
				if(null!=item){
					metadataManager.refMetadataItem(metadata.getId(), item.getId(), Long.valueOf(iad).intValue());
				}
			}
		}   
		Boolean f = (Boolean)(BrowserFlag.OpenDivWindow.getFlag(request));
		//日程事件全文检索
		try {
			if(isNew){//新增
				IndexInfo index = calEventManager.getIndexInfo(id);
				indexManager.index(index);
			}else{//修改
				updateIndexManager.update(Long.valueOf(idStr), ApplicationCategoryEnum.calendar.key());
			}
		} catch (Exception e) {
			log.error("全文检索入库", e);
		}
		
		//如果是项目事件,存入该项目下当前阶段
		if(event.getShareType() == ShareType.project.key() && StringUtils.isNotBlank(event.getTranMemberIds())){
			ProjectSummary projectSummary = projectManager.getProject(NumberUtils.toLong(event.getTranMemberIds()));
        	if(projectSummary != null){
        		if(projectSummary.getPhaseId() != 1){
        			ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.calendar.key(), event.getId(), projectSummary.getPhaseId());
        			projectPhaseEventManager.save(projectPhaseEvent);
        		}
        	}
		}
		
		if (needBack) {
			if ("section".equals(request.getParameter("from"))) {
				super.rendJavaScript(response, "parent.createOk();parent.window.close();");
			} else {
				if ("coll".equals(request.getParameter("from"))) {
					if (f.booleanValue()) {
						String str = "alert('" + Constants.getResourceStr("col.to.cal.success") + "');parent.window.returnValue='true';parent.window.close();";
						super.rendJavaScript(response, str);
					} else {
						super.rendJavaScript(response, "if(parent.parent.parent.detailRightFrame){parent.parent.parent.detailRightFrame.reloadParent();}");
					}
				} else {
					if (f.booleanValue()) {
						super.rendJavaScript(response, "parent.window.returnValue='true';top.close();");
					} else {
						super.rendJavaScript(response, "parent.parent.reloadParent()");
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 判断事件提醒时间是否发生了变化，以便在提醒时间没有发生变化时，不进行删除任务调度再重新生成的无谓操作
	 */
	private boolean isRemindTimeChanged(boolean oldAlarmFlag, Date oldRemindDate, boolean newAlargFlag, Date newRemindDate) {
		if(!oldAlarmFlag)
			return true;
		else {
			return oldRemindDate!=null && newRemindDate!=null && !oldRemindDate.equals(newRemindDate);
		}
	}
	
	/**
	 * 得到当期的日期时间String
	 * @return
	 */
	private String getNowDateString(){
		Calendar nowCalendar = Calendar.getInstance();
		Integer year_ = nowCalendar.get(Calendar.YEAR);
		Integer month_ = nowCalendar.get(Calendar.MONTH)+1;
		Integer day_ = nowCalendar.get(Calendar.DAY_OF_MONTH);
		Integer  hour_ = nowCalendar.get(Calendar.HOUR_OF_DAY);
		Integer minutes_ = nowCalendar.get(Calendar.MINUTE);
		Integer seconds_ = nowCalendar.get(Calendar.SECOND);
  		String month = (month_ < 10) ? 0 +""+ month_ .toString(): month_.toString();
  		String day = (day_ < 10) ? 0 + ""+day_.toString() : day_.toString();
  		String hour = (hour_<10)? 0+""+hour_.toString():hour_.toString();
  		String minutes = (minutes_<10)?0+""+minutes_.toString():minutes_.toString();
  		String seconds = (seconds_<10)?0+""+seconds_.toString():seconds_.toString();
  		 return year_.toString()+"-"+month+"-"+day+" "+hour+":"+minutes+":"+seconds;
	}
	
	/**
	 * 保存 事件的所属人
	 * @param event
	 * @param type
	 */
	private void saveTranEvents(CalEvent event,int type){
		if (StringUtils.isBlank(event.getReceiveMemberId())) {
			this.calEventTranManager.saveTranEvents(event, event.getTranMemberIds(), type, false);
		} else {
			this.calEventTranManager.saveTranEvents(event, event.getTranMemberIds(), type, event.getReceiveMemberId());
		}
	}
	
	/**
	 * 保存 cal_event_tran 数据
	 * @param event
	 */
	private void switchSaveTranEvent(AbstractCalEvent event, Integer oldShareType, String oldTranMemberIds, Boolean isNew, Boolean... isPeriodicals){
		CalEvent e = new CalEvent(event);
		this.calEventTranManager.deleteByEventId(event.getId());
		int shareType = event.getShareType().intValue();
		if (StringUtils.isNotBlank(event.getTranMemberIds()) || StringUtils.isNotBlank(event.getReceiveMemberId())) {
			saveTranEvents(e,shareType);
		}
		boolean isPeriodical = false;
		if(isPeriodicals!=null && isPeriodicals.length>0){
			isPeriodical = isPeriodicals[0];
		}
		if((shareType == ShareType.publicity.key() || shareType == ShareType.department.key() || shareType == ShareType.project.key()) && !isPeriodical){
			CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.p_ON_SHARE, oldShareType, oldTranMemberIds, e, this.orgManager, this.userMessageManager, this.calendarUtils);
		}
	}
	
	/**
	 * 根据 事件的 ShareType 设置 事件的 TranMemberIds 和 ShareTarget
	 * @param event
	 * @param request
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void switchSetShareTypeVale(AbstractCalEvent event,HttpServletRequest request) throws NumberFormatException, Exception{
		switch(ShareType.valueOf(event.getShareType().intValue())){
		case publicity:
			String tranMemberIds = request!=null?request.getParameter("tranMemberIds2"):event.getTranMemberIds();
			String receiverMemberIds = request!=null?request.getParameter("shareTarget2"):event.getTranMemberIds();
			//CalendarUtils util = new CalendarUtils();
			setShareTypeValue(event,tranMemberIds,receiverMemberIds);
			break;
		case department:
			setShareTypeValue(event,event.getTranMemberIds(),event.getShareTarget());
			break;
		case project:
			String projectId = Strings.isNotBlank(request.getParameter("projectId")) ? request.getParameter("projectId") : event.getTranMemberIds();
			String projectName = this.projectManager.getProject(Long.valueOf(projectId!=null?projectId:"0")).getProjectName();
			setShareTypeValue(event,projectId,projectName);
			break;
			default:
				event.setShareTarget("");
		}
	}

	private void setShareTypeValue(AbstractCalEvent event,String tranMemberIds,String shareTarget){
		if (!Strings.isBlank(tranMemberIds)) {
			event.setTranMemberIds(tranMemberIds);
			if (!Strings.isBlank(shareTarget)) {
				event.setShareTarget(shareTarget);
			}
		}
	}
	/**
	 * 工具栏---委托事件
	 */
	public ModelAndView cancelEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CalEvent> list = this.calEventManager.getEventByIds(FormBizConfigUtils.parseStr2Ids(request.getParameter("id")));
		return new ModelAndView("calendar/event/cancel_event_dlg", "list", list);
	}

	/**
	 * 执行保存委托事件
	 */
	public ModelAndView cancelEventOper(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		String receiveMemberId = StringUtils.defaultString(request.getParameter("receiveMemberId") ,"");// 类型+id列表（人的id或是部门的id以逗号分隔）
		String receiveMemberName = request.getParameter("receiveMemberName"); // 名字列表（人员或是部门）
		String type = request.getParameter("type");

		List<Long> ids = FormBizConfigUtils.parseStr2Ids(request.getParameter("id"));
		if (CollectionUtils.isNotEmpty(ids)) {
			for (Long eventId : ids) {
				CalEvent event = this.calEventManager.getEventById(eventId);
				if (event != null && "agent".equals(type)) {
					if(event.getCreateUserId().longValue() != userId.longValue()) {
						super.rendJavaScript(response,getAlertString(Constants.getResourceStr("error.please.reselect.1")));
						return null;
					}
					if(event.getStates()==4){
						super.rendJavaScript(response,getAlertString(Constants.getResourceStr("error.please.reselect.5")));
						return null;
					} else {
						int size = this.calEventTranManager.getEventTranListByEventId(event.getId()).size();
						if(size>0) {
							event.setStates(CompleteType.valueOf(event.getStates()).key());
						}
					}
					String oldReceivers = event.getReceiveMemberId();
					
					// 如果选择了委托事件，则设置“委托”字段，并发送消息给委托人
					event.setEventflag(2);// 事件的标识是已经委托了
					event.setEventType(3);// 事件的标识是已经委托了
					event.setReceiveMemberName(receiveMemberName);
					event.setReceiveMemberId(receiveMemberId);
					event.setStates(2);
					// 保存委托事件
					this.calEventManager.save(event);
					// 先删除之前的委托或安排记录，再保存新的委托记录
					this.calEventTranManager.deleteByEventId(eventId);
					this.calEventTranManager.saveTranEvents(event, event.getTranMemberIds(), event.getShareType(), event.getReceiveMemberId());
					
					//委托写入操作日志
					appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_Commission, CurrentUser.get().getName(),event!=null?event.getSubject():"",receiveMemberName);					
					
					// 给被委托人发信息
					CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.P_ON_TRANS, oldReceivers, event, this.orgManager, this.userMessageManager, this.calendarUtils);
					
					updateIndexManager.update(event.getId(), ApplicationCategoryEnum.calendar.key());
				}
			}
		}

		return new ModelAndView("calendar/event/reloadParent");
	}

	/**
	 * 新建主页面-----事件回复
	 */
	public ModelAndView calReply(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CalEvent event = null;
		String idStr = request.getParameter("departmentId");// 事件的ID
		boolean sendMessage = Strings.isNotBlank(request.getParameter("c"));
		if (StringUtils.isNotEmpty(idStr)) {
			event = this.calEventManager.getEventById(Long.valueOf(idStr));
			if(event != null){
				CalReply reply = new CalReply();
				reply.setEventId(event.getId());
				reply.setCalType(1);
				reply.setReplyDate(new Date());
				reply.setReplyInfo(RequestUtils.getStringParameter(request,"leaveWordContent", ""));
				reply.setReplyOption("");
				reply.setReplyUserId(CurrentUser.get().getId());
				this.calReplyManager.save(reply);
				if(sendMessage){
					CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.P_ON_REPLY, null, event, orgManager, userMessageManager, calendarUtils);
				}
				updateIndexManager.update(Long.valueOf(idStr), ApplicationCategoryEnum.calendar.key());
				super.rendJavaScript(response, "parent.ok(" + reply.getReplyDate().getTime() + ");");
			} else {
				super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.delete")));
			}
			
		}
		return null;
	}
	

	/**
	 * 新建页面---只读方式查看
	 */
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		Long id = RequestUtils.getLongParameter(request, "id");
		CalEvent event = this.calEventManager.getEventById(id);
		CalContent body = new CalContent();
		ModelAndView mav = new ModelAndView();
		mav = new ModelAndView("calendar/event/event_create");
		//获取事件类型
		Metadata eventTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		mav.addObject("eventTypeMetadata", eventTypeMetadata);
		// 判断事件是否被删除，如果被删除的话，给出提示信息
		if ((event == null)){
			super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.delete")));
			return null;
		} else {
			if (event != null) {
				// 获取回复意见
				List<CalReply> replyList = this.calReplyManager.getReplyListByEventId(event.getId());
				mav.addObject("replyList", replyList);
				mav.addObject("replyListSize", replyList.size());

				// 判断事件的当前类型是否是私人事件，如果是的话，限制查看的权限
				if ((event.getShareType().intValue() == 1
						&& event.getCreateUserId() != CurrentUser.get().getId()
						&& StringUtils.isBlank(event.getReceiveMemberId()))) {
					super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.siren")));
					return null;
				} else {
					List<CalContent> eventContent = null;
					eventContent = this.calContentManager.getContentByEventId(event.getId());
					for (int i = 0; i < eventContent.size(); i++) {
						body = eventContent.get(i);
					}
					List<Attachment> attachments = new ArrayList<Attachment>();
					if (event.getAttachmentsFlag())
						attachments = attachmentManager.getByReference(event.getId(), event.getId());

					mav.addObject("attachments", attachments);
					mav.addObject("body", body);
					mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
					mav.addObject("bean", event);

					if (event.getShareType().equals(6)) {

						ProjectSummary project = this.projectManager.getProject(Long.valueOf(event.getTranMemberIds()));
						mav.addObject("project", project);
					}
					List<ProjectSummary> projectList = null;
					try {
						projectList = this.projectManager.getProjectList();
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 关联项目
					mav.addObject("projectList", projectList);
					String receive = event.getReceiveMemberId();
					if(StringUtils.isNotBlank(receive)&&!isContainMemberId(receive)){//当前用户不是所属人
						mav.addObject("onlyLook","false");
					}else{
						if(StringUtils.isBlank(receive)&&event.getShareType()==5&&isContainMemberId(event.getTranMemberIds())){
							if(event.getEventType()==1){
								if(event.getCreateUserId().equals(CurrentUser.get().getId())){
									mav.addObject("onlyLook",null);	
								}else{
									mav.addObject("onlyLook","true");	
								}
															
							}else{
								mav.addObject("onlyLook","true");
							}
						}
						if(event.getShareType()==6&&StringUtils.isBlank(receive)){
							mav.addObject("onlyLook","true");
						}
						if(isContainMemberId(event.getTranMemberIds())){
							if(event.getEventType()!=1&&!isContainMemberId(event.getReceiveMemberName())){
								mav.addObject("onlyLook","true");
							}else{
								if(event.getEventType()==1){
									if(event.getCreateUserId().equals(Long.valueOf(CurrentUser.get().getId()))){
										mav.addObject("onlyLook",null);
									}else{
										mav.addObject("onlyLook","false");
									}
								}
							}
						}else{
							if(!event.getCreateUserId().equals(Long.valueOf(CurrentUser.get().getId()))){
								mav.addObject("isDisabled", true);
								if(event.getEventType()==7){
									mav.addObject("onlyLook", "true");
								}
							}else{
								if(event.getShareType()==1){
									mav.addObject("onlyLook",null);
								}else{
									mav.addObject("onlyLook","false");
								}
							}
						}
					}
					if(!isContainMemberId(event.getReceiveMemberId())&&!isContainMemberId(event.getTranMemberIds())&&!event.getCreateUserId().equals(Long.valueOf(CurrentUser.get().getId()))){
						mav.addObject("onlyLook","false");
					}
					//
					// 如果创建者不是自己为他人安排，反之则是自我安排事件
					if (event.getCreateUserId() != userId) {
						//mav.addObject("isDisabled",event.getCreateUserId() != userId);
						//mav.addObject("bumen", true);
					}
					// 此处设为11是为了让查看他人事件的时候，显示新建的只读对话框，前台判断的是plan和不等于plan，只要设置不等于plan的值就可
					mav.addObject("oper", event.getCreateUserId() != userId ? "11": "");
				}
			}
		} 
		long formId = event.getFromId();
		mav.addObject("subject", formId!=0?getFromSubject(event.getFromId(),event.getFromType()):null);
		return mav;
	}
	
	private boolean isContainMemberId(String str) throws NumberFormatException, BusinessException{
		String[] string = str!=null?str.split(","):null;
		if(string!=null){
			for(String m : string){
				String[] memberName = m.split("[|]");
				if(memberName!=null && memberName.length >= 1){
					if("Member".equals(memberName[0])){
						if(memberName.length>=2&&memberName[1].equals(String.valueOf(CurrentUser.get().getId()))){
							return true;
						}else{
							continue;
						}
					}else if("Department".equals(memberName[0])){
						if(memberName.length>=2){
							List<V3xOrgMember>  members = orgManager.getMembersByDepartment(Long.valueOf(memberName[1]), true);
							V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
							if(members.contains(member)){
								return true;
							}
	 					}
					}
				}
				
			}
		}
		return false;
	}

	/**
	 * 弹出小页面---首页---编辑事件完成率---展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author Mazc 2007-09-21---lxj
	 */
	public ModelAndView editEventState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("calendar/event/event_editState");
		CalEvent event = null;
		String id = request.getParameter("id");
		if (Strings.isNotBlank(id)) {
			event = this.calEventManager.getEventById(Long.valueOf(id));
		} else {
			event = new CalEvent();
			// 设置默认值
			event.setCompleteRate(0f);
			event.setRealEstimateTime(0f);
		}
		
		if(event==null) {
			super.rendJavaScript(response, getAlertString(Constants.getResourceStr("error.please.reselect.delete")));
			return null;
		}
		
		String str = event.getReceiveMemberId();
//		if(isContainMemberId(str)){
//			modelAndView.addObject("canEdit", true);
//		}else{
//			if(event.getCreateUserId().equals(Long.valueOf(CurrentUser.get().getId()))&&(StringUtils.isBlank(str))){
//				modelAndView.addObject("canEdit", true);
//			}else{
//				modelAndView.addObject("canEdit", false);
//			}
//		}
		boolean canEdit = isContainMemberId(str) || event.getCreateUserId() == CurrentUser.get().getId();
		modelAndView.addObject("canEdit", canEdit);
		modelAndView.addObject("bean", event);
		
		return modelAndView;
	}

	/**
	 * 弹出小页面---首页---编辑事件完成率---修改保存
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author Mazc 2007-09-21---lxj
	 */
	public ModelAndView updateEventStateForFirst(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CalEvent event = null;
		String id = request.getParameter("id");
		String completeRate = "";
		String realEstimateTime = "";
		String states = "";
		if (!id.equals("")) {
			states = request.getParameter("states");
			completeRate = request.getParameter("completeRate");
			realEstimateTime = request.getParameter("realEstimateTime");

			event = this.calEventManager.getEventById(Long.valueOf(id));
			if (event != null) {
				event.setStates(Integer.parseInt(states));
				event.setCompleteRate(Float.parseFloat(completeRate));
				event.setRealEstimateTime(Float.parseFloat(realEstimateTime));
				this.calEventManager.save(event);
			}
		} else if (id.equals("")) {
			event = new CalEvent();
			states = request.getParameter("states");
			completeRate = request.getParameter("completeRate");
			realEstimateTime = request.getParameter("realEstimateTime");

			event.setStates(Integer.parseInt(states));
			event.setCompleteRate(Float.parseFloat(completeRate));
			event.setRealEstimateTime(Float.parseFloat(realEstimateTime));
		}
		// 返回值, 关闭窗口
		PrintWriter out = response.getWriter();
		out.println("<script type='text/javascript'>");
		out.println("parent.editOk();");
		out.println("parent.window.close();");
		out.println("</script>");
		return null;
	}

	/**
	 * 弹出小页面---新建页面---高级设置---展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author Mazc 2007-09-21---lxj
	 */
	public ModelAndView createEventState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"calendar/event/event_createEventState");
		CalEvent event = null;
		String id = request.getParameter("id");
		if (!id.equals("") && Strings.isBlank(request.getParameter("virtual"))) {
			event = this.calEventManager.getEventById(Long.valueOf(id));
			if(event.getCompleteRate()==0){
				event.setCompleteRate(0);
			}
			modelAndView.addObject("confirm", request.getParameter("confirm"));
		} else {
			event = new CalEvent();
			// 设置默认值
			event.setCompleteRate(0);
			event.setRealEstimateTime(0f);
			// 待安排
			event.setWorkType(1);
			event.setPeriodicalStyle(0);
			if(Strings.isNotBlank(request.getParameter("virtual"))){
				modelAndView.addObject("confirm", request.getParameter("confirm"));
			} else {
				modelAndView.addObject("confirm", "all");
			}
		}
		modelAndView.addObject("bean", event);
		return modelAndView;
	}

	/**
	 * 弹出小页面---新建页面---高级设置---修改 保存
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * @author Mazc 2007-09-21---lxj
	 */
	public ModelAndView updateEventState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CalEvent event = null;
		String id = request.getParameter("id");
		String completeRate = "";
		String realEstimateTime = "";
		String workType = "";
		if (!id.equals("")) {
			workType = request.getParameter("workType");
			completeRate = request.getParameter("completeRate");
			realEstimateTime = request.getParameter("realEstimateTime");

			event = this.calEventManager.getEventById(Long.valueOf(id));
			if (event != null) {
				event.setWorkType(Integer.parseInt(workType));
				event.setCompleteRate(Float.parseFloat(completeRate));
				event.setRealEstimateTime(Float.parseFloat(realEstimateTime));
				this.calEventManager.save(event);
			}
		} else if (id.equals("")) {
			event = new CalEvent();
			workType = request.getParameter("workType");
			completeRate = request.getParameter("completeRate");
			realEstimateTime = request.getParameter("realEstimateTime");

			event.setWorkType(Integer.parseInt(workType));
			event.setCompleteRate(Float.parseFloat(completeRate));
			event.setRealEstimateTime(Float.parseFloat(realEstimateTime));
		}
		// 返回值, 关闭窗口
		PrintWriter out = response.getWriter();
		out.println("<script type='text/javascript'>");
		out.println("parent.editOk();");
		out.println("parent.window.close();");
		out.println("</script>");
		return null;
	}

	//	---------------------------工具方法： 时间取整  判断上下级 判断项目 ...
	/**
	 * 时间取整的方法
	 * 
	 */
	private Date getIntegralTime(Date date) {
		Date time = Datetimes.getNextPeriodMinute(date, 30, true);
		return time;
		/*Date retDate = new Date();
		Calendar cal = Calendar.getInstance();
		Calendar newCal = Calendar.getInstance();
		cal.setTime(date);
		newCal.setTime(retDate);
		
		int minutes =cal.get(Calendar.MINUTE);
		if (minutes % 10 > 0) {
			minutes = ((minutes / 10) + 1) * 10;
		}
		
		newCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		newCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		newCal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		newCal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
		newCal.set(Calendar.SECOND, cal.get(0));
		
		return retDate;*/
	}

	/**
	 * 结束时间比开始时间显示晚半小时的方法
	 * 
	 */
	private Date getIntegralEndTime(Date date) {
		return Datetimes.addMinute(date, 30);
	}

	/**
	 * 做共用的判断上下级的方法
	 * 
	 */
	private ModelAndView hasSX(ModelAndView mav, Long userId) throws Exception {
		// 判断当前用户是否有上级或是下级
		List<V3xOrgMember> leaderlist = peopleRelateManager.getAllRelateMembers(userId).get(RelationType.leader);
		List<V3xOrgMember> juniorlist = peopleRelateManager.getAllRelateMembers(userId).get(RelationType.junior);
		
		boolean leader = CollectionUtils.isNotEmpty(leaderlist);
		boolean junior = CollectionUtils.isNotEmpty(juniorlist);
		
		return mav.addObject("leader", leader).addObject("junior", junior);
	}

	//  -----给别的模块的方法

	/**
	 * ---从项目中返回项目事件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView choseProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/event_project");
		String type = request.getParameter("type");
		if (type == null)
			type = "";
		List<CalEvent> list = new ArrayList<CalEvent>();

		String idStr = request.getParameter("id");
		if (StringUtils.isNotEmpty(idStr)) {
			String[] idStrs = idStr.split(",");
			for (String str : idStrs) {
				if (StringUtils.isNotBlank(str)) {
					CalEvent event = this.calEventManager.getEventById(Long
							.valueOf(str));
					// 如果是委托，则已经完成的事件不允许再进行委托
					if (type.equals("agent") && event.getStates() == 4) {
						continue;
					}
					list.add(event);
				}
			}
		}

		mav.addObject("type", type);
		mav.addObject("list", list);
		mav.addObject("canCancel", list.size() > 0);
		return mav;
	}
 
	/**
	 * 协同转换为日程
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView colToEvent(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("calendar/event/event_create");
		String refId = request.getParameter("id");
		String title = request.getParameter("title");
		String appType = request.getParameter("appType");
		CalEvent event = new CalEvent();
		event.setSubject(title);
		Date begin = new Date();
		event.setBeginDate(begin);
		event.setEndDate(new Date(begin.getTime()+3600000));//结束时间比开始时间大一天
		event.setStates(1);//待安排
		event.setSignifyType(1);//重要紧急
		event.setFromType(Integer.parseInt(appType));
		event.setFromId(refId!=null?Long.parseLong(refId):null);
		event.setRealEstimateTime(new Float(0));
		List<ProjectSummary> projectList = null;
		try {
			projectList = this.projectManager.getProjectList();
		} catch (Exception e) {
			log.info(" 协同转换为日程时得到关联项目列表错误!");
		}
		
		mav.addObject("projectList", projectList);//关联项目
		mav.addObject("bean", event);
		mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));//提醒时间
		mav.addObject("subject", Long.valueOf(refId)!=0?getFromSubject(Long.valueOf(refId),Integer.valueOf(appType)):"nosubject");
		
		Metadata eventTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		mav.addObject("eventTypeMetadata", eventTypeMetadata);
		
		mav.addObject("create", true);

		return mav;
	}
	
	private String getAlertString(String msg){
		StringBuilder builder = new StringBuilder();
		builder.append("alert('");
		builder.append(msg);
		builder.append("');window.returnValue='true';window.close();");
		return builder.toString();
	}
	
	/**
     * 事件导出为EXCEL文件
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	public ModelAndView saveAsExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<CalEvent> list = null;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		Long userId = CurrentUser.get().getId();

		String resource = "com.seeyon.v3x.calendar.resources.i18n.CalendarResources";
		String commonResource = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
		Locale locale = LocaleContext.getLocale(request);

		String fileName = null;
		fileName = Constant.getValueFromMainRes("calendarEvent.more.event");
		DataRecord record = new DataRecord();
		
		// 判断是否有条件，如果有条件的话，就走条件查询（条件接口要的参数是id，String，string）
		if (StringUtils.isNotBlank(condition)
				&& StringUtils.isNotBlank(textfield)) {
			list = this.calEventManager.getEventListByUserId(userId, condition
					.toString(), textfield.toString());
		} else {
			// 如果没有条件的话，调用的是个人事件的全部查询按id
			list = this.calEventManager
					.getEventListByUserId(userId, true, true);
		}

		if (list != null && !list.isEmpty()) {
			Metadata eventTypeMetadata = metadataManager
					.getMetadata(MetadataNameEnum.cal_event_type);
			eventTypeMetadata.getItem("1").getLabel();
			this.initDataRecord(record, request);
			for (CalEvent obj : list) {
				DataRow row = new DataRow();
				row.addDataCell(obj.getCalEventType() == null ? ""
						: ResourceBundleUtil.getString(resource, locale,
								eventTypeMetadata.getItem(
										obj.getCalEventType().toString())
										.getLabel()), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(obj.getSubject(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(ResourceBundleUtil.getString(resource, locale,
						"cal.event.signifyType." + obj.getSignifyType()),
						DataCell.DATA_TYPE_TEXT);
				row.addDataCell(Datetimes.formateToLocaleDatetime(obj
						.getBeginDate()), DataCell.DATA_TYPE_DATETIME);
				//row.addDataCell(Datetimes.formateToLocaleDatetime(obj
					//	.getEndDate()), DataCell.DATA_TYPE_DATETIME);
				row.addDataCell(obj.getReceiveMemberId() == null
						|| obj.getReceiveMemberId().equalsIgnoreCase("") ? obj
						.getCreateUserName() : obj.getReceiveMemberName(),
						DataCell.DATA_TYPE_TEXT);
				String eventSource = "";
				if (obj.getReceiveMemberId() == null
						|| obj.getReceiveMemberId().equalsIgnoreCase("")) {
					eventSource = ResourceBundleUtil.getString(resource,
							locale, "cal.event.eventSource.1");
				} else {
					if (obj.getEventflag() != 1) {
						eventSource = ResourceBundleUtil.getString(resource,
								locale, "cal.event.eventSource.3")
								+ "(" + obj.getCreateUserName() + ")";
					} else {
						eventSource = ResourceBundleUtil.getString(resource,
								locale, "cal.event.eventSource.2")
								+ "(" + obj.getCreateUserName() + ")";
					}
				}
				row.addDataCell(eventSource, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(ResourceBundleUtil.getString(resource, locale,
						"cal.event.states." + obj.getStates()),
						DataCell.DATA_TYPE_TEXT);
				int periodicalStyle = obj.getPeriodicalStyle().intValue();
				String periodicalStyleString = "";
				if(periodicalStyle == PeriodicalStyle.None.ordinal()){
					periodicalStyleString = ResourceBundleUtil.getString(commonResource,
							locale, "common.default");
				} else if(periodicalStyle == PeriodicalStyle.Day.ordinal()) {
					periodicalStyleString = ResourceBundleUtil.getString(resource,
							locale, "cal.day.alarm");
				} else if(periodicalStyle == PeriodicalStyle.Week.ordinal()) {
					periodicalStyleString = ResourceBundleUtil.getString(resource,
							locale, "cal.week.alarm");
				} else if(periodicalStyle == PeriodicalStyle.Month.ordinal()) {
					periodicalStyleString = ResourceBundleUtil.getString(resource,
							locale, "cal.month.alarm");
				} else {
					periodicalStyleString = ResourceBundleUtil.getString(resource,
							locale, "cal.year.alarm");
				}
				row.addDataCell(periodicalStyleString,DataCell.DATA_TYPE_TEXT);
				record.addDataRow(row);
			}
		}
		fileToExcelManager.save(request, response, fileName, record);
		return null;
	}
    
    /**
     * 初始化Excel列名称
     * @param record
     * @param request
     */
	private void initDataRecord(DataRecord record, HttpServletRequest request) {

		Locale locale = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.calendar.resources.i18n.CalendarResources";

		// 日程事件
		String state_Cal = ResourceBundleUtil.getString(resource, locale,
				"cal.cancel.cal");
		// 标题
		String state_Subject = ResourceBundleUtil.getString(resource, locale,
				"cal.event.subject");
		// 事件类型
		String state_EventType = ResourceBundleUtil.getString(resource, locale,
				"cal.event.type");
		// 重要程度
		String state_SignifyType = ResourceBundleUtil.getString(resource,
				locale, "cal.event.signifyType");
		// 开始时间
		String state_BeginDate = ResourceBundleUtil.getString(resource, locale,
				"cal.event.beginDate");
		
		// 结束时间
		String state_EndDate = ResourceBundleUtil.getString(resource, locale,
				"cal.event.endDate");
		
		// 所属人
		String state_Member = ResourceBundleUtil.getString(resource, locale,
				"label.content.member");
		// 事件来源
		String state_Source = ResourceBundleUtil.getString(resource, locale,
				"cal.event.eventSource");
		// 状态
		String state_States = ResourceBundleUtil.getString(resource, locale,
				"cal.event.states");
		String state_periodical = ResourceBundleUtil.getString(resource, locale,
				"cal.event.periodical");

		record.setSheetName(state_Cal);
		record.setTitle(state_Cal);
		String[] columnNames = { state_EventType, state_Subject,
				state_SignifyType, state_BeginDate, state_Member, state_Source,
				state_States , state_periodical};
		record.setColumnName(columnNames);
	}

	public ModelAndView periodicalIframe(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("calendar/periodical/periodical_Iframe");
    	return mav;
	}
	
	public ModelAndView listPeriodical(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("calendar/periodical/list_periodical");
		User user = CurrentUser.get();
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		
		Map<String, Metadata> calMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.calendar);
		Metadata eventTypeMetadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		mav.addObject("calMetadata", calMetadata);
		mav.addObject("eventTypeMetadata", eventTypeMetadata);
		
		List<CalEventPeriodicalInfo> result = calEventManager.findPeriodical4User(user.getId(), condition, textfield);
		mav.addObject("result", result);
    	return mav;
	}
	
	public ModelAndView deletePeriodical(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		String ids = request.getParameter("ids");
		if(Strings.isNotBlank(ids)){
			String[] idArray = ids.split(",");
			List<Long> idList = new ArrayList<Long>();
			for(String id : idArray){
				idList.add(Long.parseLong(id));
			}
			calEventManager.deletePeriodicalByIds(idList);
		}
		PrintWriter out = response.getWriter();
		out.print("<script type='text/javascript'>");
		out.print("parent.document.location.href=parent.document.location.href;");
		out.print("</script>");
		out.close();
    	return null;
	}
	
	
	/**
	 * 统计事件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView statistics(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/statistics");
		Long userId = CurrentUser.get().getId();
		String bDate = request.getParameter("beginDate");
		String eDate = request.getParameter("endDate");
		String state = request.getParameter("states");
		Date beginDate = Datetimes.getTodayFirstTime();
		Date endDate = Datetimes.getTodayLastTime();
		Integer states = 0;
		if (Strings.isNotBlank(bDate)) {
			beginDate = Datetimes.parse(bDate);
		}
		if (Strings.isNotBlank(eDate)) {
			endDate = Datetimes.parse(eDate);
		}
		if (Strings.isNotBlank(state)) {
			states = Integer.parseInt(state);
		}
		String statistic = request.getParameter("statistic");
		Integer statistics = 0;
		if (Strings.isNotBlank(statistic)) {
			statistics = Integer.parseInt(statistic);
		}
		String summary = request.getParameter("summary");
		Integer summarys = 0;
		if (Strings.isNotBlank(summary)) {
			summarys = Integer.parseInt(summary);
		}
		List<CalEventStatistics> statisticsList = calEventManager
				.getEventListByUserId(userId, beginDate, endDate, states,
						statistics);
		List<Integer> statisticsType = new ArrayList<Integer>();
		Metadata metadata = metadataManager
				.getMetadata(MetadataNameEnum.cal_event_type);
		if (statistics == 0) {
			statisticsType = Constants.getAllSignifyType();
		} else {
			List<MetadataItem> eventTypes = metadata.getItems();
			for (MetadataItem m : eventTypes) {
				statisticsType.add(Integer.parseInt(m.getValue()));
			}
		}
		HashMap<Integer, CalEventStatistics> statisticsMap = new LinkedHashMap<Integer, CalEventStatistics>();
		if (statisticsList != null && !statisticsList.isEmpty()) {
			for (CalEventStatistics c : statisticsList) {
				statisticsMap.put(c.getType(), c);
			}
		}
		Float counts = 0f;
		Float sumTime = 0f;
		for (int i = 0; i < statisticsType.size(); i++) {
			Integer index = statisticsType.get(i);
			if (statisticsMap.get(index) == null) {
				CalEventStatistics cs = new CalEventStatistics();
				cs.setColor(ChartUtil.getAllColor().get(i));
				cs.setType(index);
				cs.setCounts(0);
				cs.setSumTime(0f);
				statisticsMap.put(index, cs);
			} else {
				CalEventStatistics cs = statisticsMap.get(index);
				cs.setColor(ChartUtil.getAllColor().get(i));
				counts += cs.getCounts();
				sumTime += cs.getSumTime();
			}
		}
		NumberFormat nf = java.text.NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);// 小数点后保留几位
		if(statisticsMap.get(8) != null){
			CalEventStatistics cs = statisticsMap.get(8);
			cs.setColor(ChartUtil.getAllColor().get(statisticsType.size()));
			cs.setTypeName(ResourceBundleUtil.getString(
					"com.seeyon.v3x.calendar.resources.i18n.CalendarResources",
					"cal.event.type.empty"));
			counts += cs.getCounts();
			sumTime += cs.getSumTime();
			if (summarys == 0) {
				Float result = statisticsMap.get(8).getCounts() / counts;
				if (result.isNaN()) {
					statisticsMap.get(8).setPercentum("0.00%");
				} else {
					statisticsMap.get(8).setPercentum(nf.format(result));
				}
			} else {
				Float result = statisticsMap.get(8).getSumTime() / sumTime;
				if (result.isNaN()) {
					statisticsMap.get(8).setPercentum("0.00%");
				} else {
					statisticsMap.get(8).setPercentum(nf.format(result));
				}
			}
		}
		for (Integer sType : statisticsType) {
			if (statistics == 0) {
				statisticsMap.get(sType).setTypeName(
								ResourceBundleUtil.getString(
												"com.seeyon.v3x.calendar.resources.i18n.CalendarResources",
												"cal.event.signifyType."
														+ sType + ""));
			} else {
				statisticsMap.get(sType).setTypeName(
								ResourceBundleUtil.getString(
												"com.seeyon.v3x.calendar.resources.i18n.CalendarResources",
												metadata.getItem(sType.toString()).getLabel()));
			}
			if (summarys == 0) {
				Float result = statisticsMap.get(sType).getCounts() / counts;
				if (result.isNaN()) {
					statisticsMap.get(sType).setPercentum("0.00%");
				} else {
					statisticsMap.get(sType).setPercentum(nf.format(result));
				}
			} else {
				Float result = statisticsMap.get(sType).getSumTime() / sumTime;
				if (result.isNaN()) {
					statisticsMap.get(sType).setPercentum("0.00%");
				} else {
					statisticsMap.get(sType).setPercentum(nf.format(result));
				}
			}
		}
		mav.addObject("statisticsMap", statisticsMap);
		mav.addObject("beginDate", beginDate);
		mav.addObject("endDate", endDate);
		mav.addObject("states", states);
		mav.addObject("statistic", statistic);
		mav.addObject("summary", summary);
		return mav;
	}

	/**
	 * 对时间段内的事件可按不同的信息项图形展示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView statisticsImage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "image/jpeg");
		//ReportChartData data = new ReportChartData();
		PieChartInfo data= new PieChartInfo();
		List<Integer> keys = new ArrayList<Integer>();
		List<Double> values = new ArrayList<Double>();
		Long userId = CurrentUser.get().getId();
		String bDate = request.getParameter("beginDate");
		String eDate = request.getParameter("endDate");
		String state = request.getParameter("states");
		Date beginDate = Datetimes.getTodayFirstTime();
		Date endDate = Datetimes.getTodayLastTime();
		Integer states = 0;
		if (Strings.isNotBlank(bDate)) {
			beginDate = Datetimes.parse(bDate);
		}
		if (Strings.isNotBlank(eDate)) {
			endDate = Datetimes.parse(eDate);
		}
		if (Strings.isNotBlank(state)) {
			states = Integer.parseInt(state);
		}
		String statistic = request.getParameter("statistic");
		Integer statistics = 0;
		if (Strings.isNotBlank(statistic)) {
			statistics = Integer.parseInt(statistic);
		}
		String summary = request.getParameter("summary");
		Integer summarys = 0;
		if (Strings.isNotBlank(summary)) {
			summarys = Integer.parseInt(summary);
		}
		List<CalEventStatistics> statisticsList = calEventManager
				.getEventListByUserId(userId, beginDate, endDate, states,
						statistics);
		HashMap<Integer, Double> statisticsMap = new HashMap<Integer, Double>();
		DecimalFormat df = new DecimalFormat("0.0");
		if (statisticsList != null && !statisticsList.isEmpty()) {
			for (CalEventStatistics c : statisticsList) {
				if (summarys == 0) {
					statisticsMap.put(c.getType(), Double.parseDouble(df.format(c.getCounts().doubleValue())));
				} else {
					statisticsMap.put(c.getType(), Double.parseDouble(df.format(c.getSumTime().doubleValue())));
				}
			}
		}
		List<Integer> statisticsType = new ArrayList<Integer>();
		Metadata metadata = metadataManager.getMetadata(MetadataNameEnum.cal_event_type);
		if (statistics == 0) {
			statisticsType = Constants.getAllSignifyType();
		} else {
			List<MetadataItem> eventTypes = metadata.getItems();
			for (MetadataItem m : eventTypes) {
				statisticsType.add(Integer.parseInt(m.getValue()));
			}
		}
		StringBuffer colors = new StringBuffer("#ff0000");
		for (int i = 0; i < statisticsType.size() - 1; i++) {
			colors.append("," + ChartUtil.getAllColor().get(i + 1));
		}
		for (Integer sType : statisticsType) {
			keys.add(sType);
			if (statisticsMap.get(sType) != null) {
				values.add(statisticsMap.get(sType));
			} else {
				values.add(0.0);
			}
		}
		for (Double v : values) {
			//data.addData(v);
			data.addLstChartData(v);
		}
		if (statistics == 0) {
			for (Integer k : keys) {
				data.addColumnKeys(ResourceBundleUtil.getString(
										"com.seeyon.v3x.calendar.resources.i18n.CalendarResources",
										"cal.event.signifyType." + k + ""));
			}
		} else {
			for (Integer k : keys) {
				data.addColumnKeys(ResourceBundleUtil.getString(
										"com.seeyon.v3x.calendar.resources.i18n.CalendarResources",
										metadata.getItem(k.toString())
												.getLabel()));
			}
		}
		if(statisticsMap.get(8) != null){
			colors.append("," + ChartUtil.getAllColor().get(statisticsType.size()));
			data.addLstChartData(statisticsMap.get(8));
			data.addColumnKeys(ResourceBundleUtil.getString(
					"com.seeyon.v3x.calendar.resources.i18n.CalendarResources",
					"cal.event.type.empty"));
		}
		data.setColumnColor(colors.toString());
		data.setChartWidth(300);
		data.setChartHeight(300);
		data.setNeedLegend(false);
		reportChartManager.createChartImage(data, response, true);
//		reportChartManager.createChartImage(ReportChartType.valueOf("pie"),
//				data, response);
		return null;
	}
	
	public ModelAndView listEventStatisticsFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/event_list_statistics_iframe");
		return mav;
	}
				
	
	/**
	 * 统计事件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listEventStatistics(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"calendar/event/event_list_statistics");
		Long userId = CurrentUser.get().getId();
		String bDate = request.getParameter("beginDate");
		String eDate = request.getParameter("endDate");
		String state = request.getParameter("states");
		Date beginDate = Datetimes.getTodayFirstTime();
		Date endDate = Datetimes.getTodayLastTime();
		Integer states = 0;
		if (Strings.isNotBlank(bDate)) {
			beginDate = Datetimes.parseDatetime(bDate);
		}
		if (Strings.isNotBlank(eDate)) {
			endDate = Datetimes.parseDatetime(eDate);
		}
		if (Strings.isNotBlank(state)) {
			states = Integer.parseInt(state);
		}
		String statistic = request.getParameter("condition");
		Integer statistics = 0;
		if (Strings.isNotBlank(statistic)) {
			statistics = Integer.parseInt(statistic);
		}
		String value = request.getParameter("value");
		Integer values = 0;
		if (Strings.isNotBlank(value)) {
			values = Integer.parseInt(value);
		}
		List<CalEvent> eventList = calEventManager.getEventListByUserId(userId,
				beginDate, endDate, states, statistics, values);
		Map<String, Metadata> calMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.calendar);
		mav.addObject("calMetadata", calMetadata);
		mav.addObject("eventList", eventList);
		return mav;
	}
	
	/**
	 * 删除一条或修改周期性事件时调用
	 */
	public ModelAndView showConfirm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("calendar/event/showConfirm");
		String id = request.getParameter("id");
		String periodicalId = request.getParameter("periodicalId");
		AbstractCalEvent event = null;
		if (StringUtils.isNotBlank(periodicalId)) {
			event = calEventManager.getPeriodicalInfo(NumberUtils.toLong(periodicalId)).getCalEvent();
		} else {
			event = calEventManager.getEventById(NumberUtils.toLong(id));
		}

		mav.addObject("event", event);
		return mav;
	}
	public String ajaxGetEventName(long idStr) throws Exception{
		AbstractCalEvent p = calEventManager.getEventById(idStr);
		return p.getSubject();
	}
	
	/**
	 * 时间安排-视图
	 */
	public ModelAndView timing(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("timing/index");

		String type = request.getParameter("type");
		if (Strings.isBlank(type)) {
			type = "week";
		}
		Long memberId = CurrentUser.get().getId();
		// 从关联人员过来，取关联人员事件
		String relateMemberId = request.getParameter("relateMemberId");
		if (StringUtils.isNotBlank(relateMemberId)) {
			memberId = NumberUtils.toLong(relateMemberId);
		}
		
		Date date = new Date();
		String selectedDate = request.getParameter("selectedDate");
		if (Strings.isNotBlank(selectedDate)) {
			date = Datetimes.parseDate(selectedDate);
		}
//		Date beginDate = Datetimes.getFirstDayInMonth(date);
//		Date endDate = Datetimes.getLastDayInMonth(date);
//
//		Date firstDayInWeek = Datetimes.getFirstDayInWeek(date);
//		Date lastDayInWeek = Datetimes.getLastDayInWeek(date);
//
//		if (firstDayInWeek.before(beginDate)) {
//			beginDate = firstDayInWeek;
//		}
//
//		if (lastDayInWeek.after(endDate)) {
//			endDate = lastDayInWeek;
//		}

		Date beginDate;
		Date endDate;
		if("month".equals(type)){
			beginDate = Datetimes.getFirstDayInMonth(date);
			endDate = Datetimes.getLastDayInMonth(date);
		}else if("day".equals(type)){
			beginDate = Datetimes.getTodayFirstTime(date);
			endDate = Datetimes.getTodayLastTime(date);
		}else{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			cal.add(Calendar.DAY_OF_MONTH, firstDayOfWeek != 1 ? 2 - firstDayOfWeek : -6);
			beginDate = Datetimes.getTodayFirstTime(cal.getTime());
			endDate = Datetimes.getTodayLastTime(Datetimes.addDate(beginDate, 6));
		}
		//计划
		List<PlanCAP> planList = planManagerCAP.findSendPlanForPage(memberId, beginDate, endDate, false);

		//任务
		List<TaskInfo> taskList = new ArrayList<TaskInfo>();
		List<TaskInfo> personalTasks = taskInfoManager.getTasks(ListType.Personal, memberId, beginDate,endDate);
		if (CollectionUtils.isNotEmpty(personalTasks)) {
			taskList.addAll(personalTasks);
		}
		List<TaskInfo> sentTasks = taskInfoManager.getTasks(ListType.Sent, memberId, beginDate,endDate);
		if (CollectionUtils.isNotEmpty(sentTasks)) {
			taskList.addAll(sentTasks);
		}
		
		//日程
		List<CalEvent> eventList = new ArrayList<CalEvent>();
		// 个人事件
		List<CalEvent> myEventList = this.calEventManager.getAllEventListByUserId(memberId, beginDate, endDate);
		if (CollectionUtils.isNotEmpty(myEventList)) {
			for (CalEvent event : myEventList) {
				if (event.getFromType() != ApplicationCategoryEnum.meeting.key() && event.getFromType() != ApplicationCategoryEnum.taskManage.key()) {
					eventList.add(event);
				}
			}
		}
		// 周期性事件
		List<CalEvent> periodicalEventList = this.calEventManager.preCreateEvent(memberId, beginDate, endDate, 2);
		if (CollectionUtils.isNotEmpty(periodicalEventList)) {
			eventList.addAll(periodicalEventList);
		}

		//会议
		List<MtMeetingCAP> meetingList = mtMeetingManagerCAP.findAllMeetings4User(memberId, beginDate, endDate);
		StringBuffer sb = new StringBuffer();
		if (CollectionUtils.isNotEmpty(planList)) {
			for (PlanCAP plan : planList) {
				sb.append("{id:'" + plan.getId()
						+ "', type:'" + ApplicationCategoryEnum.plan.getKey()
						+ "', start_date:'" + Datetimes.formatDatetimeWithoutSecond(Datetimes.getTodayFirstTime(plan.getStartTime()))
						+ "', end_date:'" + Datetimes.formatDatetimeWithoutSecond(Datetimes.getTodayLastTime(plan.getEndTime()))
						+ "', text:'" + Functions.toHTML(plan.getTitle())
						+ "', color:'#51AD96', textColor:'#FFFFFF'},");
			}
		}
		
		if (CollectionUtils.isNotEmpty(taskList)) {
			for (TaskInfo task : taskList) {
				sb.append("{id:'" + task.getId()
						+ "', type:'" + ApplicationCategoryEnum.taskManage.getKey()
						+ "', start_date:'" + Datetimes.formatDatetimeWithoutSecond(task.getPlannedStartTime())
						+ "', end_date:'" + Datetimes.formatDatetimeWithoutSecond(task.getPlannedEndTime())
						+ "', text:'" + Functions.toHTML(task.getSubject())
						+ "', color:'#AD5E51', textColor:'#FFFFFF'},");
			}
		}
		
		if (CollectionUtils.isNotEmpty(eventList)) {
			for (CalEvent event : eventList) {
				sb.append("{id:'" + UUIDLong.longUUID()
						+ "', type:'" + ApplicationCategoryEnum.calendar.getKey()
						+ "', eventId:'" + event.getId()
						+ "', eventCreateUserId:'" + event.getCreateUserId()
						+ "', eventReceiveMemberId:'" + event.getReceiveMemberId()
						+ "', eventPeriodicalId:'" + event.getPeriodicalId()
						+ "', eventBeginDate:'" + Datetimes.formatDate(event.getBeginDate())
						+ "', start_date:'" + Datetimes.formatDatetimeWithoutSecond(event.getBeginDate())
						+ "', end_date:'" + Datetimes.formatDatetimeWithoutSecond(event.getEndDate())
						+ "', text:'" + Functions.toHTML(event.getSubject())
						+ "', color:'#AD8B51', textColor:'#FFFFFF'},");
			}
		}
		
		if (CollectionUtils.isNotEmpty(meetingList)) {
			for (MtMeetingCAP meeting : meetingList) {
				boolean isShowMet = true;
				List<MtReplyCAP> findByMeetingIdAndUserId = mtReplyManagerCAP.findByMeetingIdAndUserId(meeting.getId(), memberId);
				for (MtReplyCAP mtreply : findByMeetingIdAndUserId) {
					if (mtreply.getFeedbackFlag() != null && mtreply.getFeedbackFlag().equals(0)) {
						isShowMet = false;
						break;
					}
				}
				if (isShowMet) {
					sb.append("{id:'" + meeting.getId()
							+ "', type:'" + ApplicationCategoryEnum.meeting.getKey()
							+ "', start_date:'" + Datetimes.formatDatetimeWithoutSecond(meeting.getBeginDate())
							+ "', end_date:'" + Datetimes.formatDatetimeWithoutSecond(meeting.getEndDate())
							+ "', text:'" + Functions.toHTML(meeting.getTitle())
							+ "', color:'#5194AD', textColor:'#FFFFFF'},");
				}
			}
		}
		
		mav.addObject("data", (sb.length() > 0 ? sb.substring(0, sb.length() - 1) : ""));

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		mav.addObject("year", cal.get(Calendar.YEAR));
		mav.addObject("month", cal.get(Calendar.MONTH));
		mav.addObject("day", cal.get(Calendar.DAY_OF_MONTH));
		mav.addObject("type", type);
		return mav;
	}
	
}