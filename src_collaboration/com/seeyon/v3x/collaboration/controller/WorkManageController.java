package com.seeyon.v3x.collaboration.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.domain.ManagementSet;
import com.seeyon.v3x.collaboration.manager.WorkStatManager;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.security.qs.EncoderQueryString;
import com.seeyon.v3x.edoc.webmodel.EdocSummaryModel;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;
import com.seeyon.v3x.taskmanage.utils.StatisticCondition;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 工作管理Controller
 * 前台用户的“工作管理”展现及管理员“工作管理权限设置”
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public class WorkManageController extends BaseController
{
    private static final Log log = LogFactory.getLog(WorkManageController.class);
    
    private OrgManager orgManager;
    private WorkStatManager workStatManager;
    private MetadataManager metadataManager;
    private FileToExcelManager fileToExcelManager;
    private PlanManager planManager;
    private MtMeetingManagerCAP mtMeetingManagerCAP;
    private TaskInfoManager taskInfoManager;
	private AppLogManager appLogManager;
	
	public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
		this.taskInfoManager = taskInfoManager;
	}
    public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setWorkStatManager(WorkStatManager workStatManager) {
        this.workStatManager = workStatManager;
    }
    
    public void setMetadataManager(MetadataManager metadataManager) {
        this.metadataManager = metadataManager;
    }

    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
        this.fileToExcelManager = fileToExcelManager;
    }

    public void setPlanManager(PlanManager planManager) {
        this.planManager = planManager;
    }

    public void setMtMeetingManagerCAP(MtMeetingManagerCAP mtMeetingManagerCAP) {
		this.mtMeetingManagerCAP = mtMeetingManagerCAP;
	}
	@Override
    public ModelAndView index(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return null;
    }
    
    /**
     * 工作管理 - 最外层页面 
     */
    public ModelAndView workManageIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("collaboration/manage/index");
        User user = CurrentUser.get();
        boolean canManage = false;
        //是否可管理其他人员
        canManage = workStatManager.hasThePermission(user.getLoginAccount(), user.getId());
        mv.addObject("canManage", canManage);
        return mv;
    }
    
    /**
     * 工作管理 - 上下结构页面 
     */
    public ModelAndView colStatMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("collaboration/manage/colStatMain");
        return mv;
    }

    /**
     * 工作管理 - 工作统计上页面 
     */
    public ModelAndView colStat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mv = new ModelAndView("collaboration/manage/colStat");
        User user = CurrentUser.get();
        List<Long> membersList = null;
        String memberIdsStr = request.getParameter("memberIds");
        if(Strings.isNotBlank(memberIdsStr)){
            membersList = CommonTools.parseStr2Ids(memberIdsStr);
        }
        else{
            membersList = Arrays.asList(user.getId());
        }
        
        Date date = new Date();
        Date beginDate = Datetimes.getFirstDayInSeason(date);
        Date endDate = Datetimes.getLastDayInSeason(date);
        int app = NumberUtils.toInt(request.getParameter("app"), ApplicationCategoryEnum.collaboration.key());
        
        Map<Long, int[]> ststMap = new HashMap<Long, int[]>();
        if(app == ApplicationCategoryEnum.collaboration.key() || app == ApplicationCategoryEnum.edoc.key()){
            ststMap = workStatManager.colStat4WorkManage(app, membersList, beginDate, endDate);
        }
        else if(app == ApplicationCategoryEnum.plan.key()){
            ststMap = planManager.getUsersPlanManagerList(membersList, beginDate, endDate);            
        }
        else if(app == ApplicationCategoryEnum.meeting.key()){
            ststMap =  mtMeetingManagerCAP.getUsersMeetingManagerList(membersList, beginDate, endDate); 
        }
        else if(app == ApplicationCategoryEnum.taskManage.key()) {
        	ststMap = taskInfoManager.getStatisticInfo(membersList, beginDate, endDate);
        }
        
        //当前应用下可管理的的人员
        List<Long> manageScopeList = workStatManager.getMembersByGrantorIdAndType(user.getLoginAccount(), user.getId(), app);
        mv.addObject("manageScopeList", manageScopeList);
        
        mv.addObject("app", app);
        mv.addObject("membersList", membersList);
        mv.addObject("ststMap", ststMap);
        mv.addObject("beginDate", Datetimes.formatDate(beginDate));
        mv.addObject("endDate", Datetimes.formatDate(endDate));
        return mv;
    }
    
    /**
     * 工作管理 - 下页面 （显示协同列表）
     */
    @EncoderQueryString
    public ModelAndView showListOfCol(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/colStatList");
        User user = CurrentUser.get();
        Long memberId = null;
        String memberIdStr = request.getParameter("memberId");
        if(Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
            if(memberId.equals(user.getId())){
            	mv.addObject("isCurrentUser", true);
            }else{
            	mv.addObject("isCurrentUser", false);
            }
        }
        else{
            memberId = user.getId();
            mv.addObject("isCurrentUser", true);
        }
        int type = 0;
        String typeStr = request.getParameter("type");
        if(Strings.isNotBlank(typeStr)){
            type = Integer.parseInt(typeStr);
        }
        Date beginDate = null, endDate = null;
        if(Strings.isNotBlank(request.getParameter("beginDate"))){
            beginDate = Datetimes.getTodayFirstTime(request.getParameter("beginDate"));
        }
        if(Strings.isNotBlank(request.getParameter("endDate"))){
        	endDate = Datetimes.getTodayLastTime(request.getParameter("endDate"));
        }
        List<ColSummaryModel> colList = workStatManager.queryColList(memberId, type, beginDate, endDate);
        mv.addObject("colList", colList);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        mv.addObject("colMetadata", colMetadata);
        return mv;
    }
    
    /**
     * 工作管理 - 下页面 （显示文列表）
     */
    @EncoderQueryString
    public ModelAndView showListOfEdoc(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/edocStatList");
        User user = CurrentUser.get();
        Long memberId = null;
        String memberIdStr = request.getParameter("memberId");
        if(Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
            if(memberId.equals(user.getId())){
            	mv.addObject("isCurrentUser", true);
            }else{
            	mv.addObject("isCurrentUser", false);
            }
        }
        else{
            memberId = CurrentUser.get().getId();
            mv.addObject("isCurrentUser", true);
        }
        int type = 0;
        String typeStr = request.getParameter("type");
        if(Strings.isNotBlank(typeStr)){
            type = Integer.parseInt(typeStr);
        }
        Date beginDate = null;
        if(Strings.isNotBlank(request.getParameter("beginDate"))){
        	beginDate = Datetimes.getTodayFirstTime(request.getParameter("beginDate"));
        }
        Date endDate = null;
        if(Strings.isNotBlank(request.getParameter("endDate"))){
        	 endDate = Datetimes.getTodayLastTime(request.getParameter("endDate"));
        }
        List<EdocSummaryModel> colList = workStatManager.queryEdocList(memberId, type, beginDate, endDate);
        mv.addObject("colList", colList);
        
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);    
        mv.addObject("colMetadata", colMetadata);
        return mv;
    }

    /**
     * 工作管理 - 下页面 （显示计划列表）
     */
    @EncoderQueryString
    public ModelAndView showListOfPlan(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/planStatList");
        Long memberId = null;
        String memberIdStr = request.getParameter("memberId");
        if(Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
        }
        else{
            memberId = CurrentUser.get().getId();
        }
        int type = 0;
        String typeStr = request.getParameter("type");
        if(Strings.isNotBlank(typeStr)){
            type = Integer.parseInt(typeStr);
        }
        Date beginDate = null;
        if(Strings.isNotBlank(request.getParameter("beginDate"))){
        	beginDate =  Datetimes.getTodayFirstTime(request.getParameter("beginDate"));
        }
        Date endDate = null ; 
        if(Strings.isNotBlank(request.getParameter("endDate"))){
        	endDate = Datetimes.getTodayLastTime(request.getParameter("endDate"));
        }
        List<Plan> planList = planManager.getUserPlanByManagerType(memberId, type, beginDate, endDate);
        mv.addObject("planList", planList);
        return mv;
    }
    
    /**
     * 工作管理 - 下页面 （显示会议列表）
     */
    @EncoderQueryString
    public ModelAndView showListOfMeeting(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/meetingStatList");
        Long memberId = null;
        String memberIdStr = request.getParameter("memberId");
        if(Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
        }
        else{
            memberId = CurrentUser.get().getId();
        }
        int type = 0;
        String typeStr = request.getParameter("type");
        if(Strings.isNotBlank(typeStr)){
            type = Integer.parseInt(typeStr);
        }
        Date beginDate = null;
        if(Strings.isNotBlank(request.getParameter("beginDate"))){
        	beginDate = Datetimes.getTodayFirstTime(request.getParameter("beginDate"));
        }
        Date endDate = null ;
        if(Strings.isNotBlank(request.getParameter("endDate"))){
        	endDate = Datetimes.getTodayLastTime(request.getParameter("endDate"));
        }
        List<MtMeetingCAP> meetingList = mtMeetingManagerCAP.getUserMeetingByManagerType(memberId, type, beginDate, endDate);
        mv.addObject("meetingList", meetingList);
        mv.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
        return mv;
    }
    
    /**
     * 工作管理 - 下页面 （显示工作任务列表）
     */
    @EncoderQueryString
    public ModelAndView showListOfTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
        StatisticCondition sc = StatisticCondition.parse(request);
        List<TaskInfo> tasks = this.taskInfoManager.getTasks(sc);
        ModelAndView mav = new ModelAndView("taskmanage/statList", "tasks", tasks);
        mav.addObject("fromWorkManage", "true");
        TaskUtils.renderMetadatas4Task(mav, metadataManager);
        return mav;
    }
    
    /**
     * 超期管理 - 上下结构页面 
     */
    public ModelAndView overTimeMain(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/overTimeMain");
        return mv;
    }
    
    /**
     * 超期管理 - 上页面 - 统计页 
     */    
    public ModelAndView overTimeStat(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/overTimeStat");
        User user = CurrentUser.get();
        Date today = new Date();
        Date beginDate = Datetimes.getFirstDayInYear(today);
        //Date endDate = Datetimes.getLastDayInYear(today);
        List<Long> membersList = new ArrayList<Long>();
        String memberIdsStr = request.getParameter("memberIds");
        if(Strings.isNotBlank(memberIdsStr)){
            StringTokenizer token = new StringTokenizer(memberIdsStr,",");
            while(token.hasMoreTokens()){
                Long memberId = Long.parseLong(token.nextToken());
                //V3xOrgMember member = orgManager.getMemberById(memberId);
                membersList.add(memberId);
            }
        }
        else{
            //V3xOrgMember member = orgManager.getMemberById(user.getId());
            membersList.add(user.getId());
        }
        
        int app = ApplicationCategoryEnum.collaboration.getKey();
        String appStr = request.getParameter("app");
        if(Strings.isNotBlank(appStr)){
            app = Integer.parseInt(appStr);
        }
        Map<Long, int[]> overTimeStstMap = new HashMap<Long, int[]>();
        overTimeStstMap = workStatManager.colOverTimeStat(app, membersList, beginDate, today);

        //      当前应用下可管理的的人员
       // V3xOrgMember member = orgManager.getMemberById(user.getId());
        List<Long> manageScopeList = workStatManager.getMembersByGrantorIdAndType(user.getLoginAccount(), user.getId(), app);
        mv.addObject("manageScopeList", manageScopeList);
        
        mv.addObject("membersList", membersList);
        mv.addObject("overTimeStstMap", overTimeStstMap);
        mv.addObject("beginDate", Datetimes.formatDate(beginDate));
        mv.addObject("endDate", Datetimes.formatDate(today));
        return mv;
    }
    
    /**
     * 显示超期列表 
     */
    @EncoderQueryString
    public ModelAndView showOverTimeList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/overTimeList");
        Long memberId = null;
        String memberIdStr = request.getParameter("memberId");
        if(Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
        }
        else{
            memberId = CurrentUser.get().getId();
        }
        int type = 0;
        String typeStr = request.getParameter("type");
        if(Strings.isNotBlank(typeStr)){
            type = Integer.parseInt(typeStr);
        }
        Date beginDate = null;
        Date endDate = null;
        if(type == 6 || type == 7){
        	if(Strings.isNotBlank(request.getParameter("beginDate"))){
        		beginDate = Datetimes.getTodayFirstTime(request.getParameter("beginDate"));
        	}
        	if(Strings.isNotBlank(request.getParameter("endDate"))){
        		endDate = Datetimes.getTodayLastTime(request.getParameter("endDate"));
        	}
        }
        List<ColSummaryModel> colList = workStatManager.queryOverTimeList(memberId, type, beginDate, endDate);
        mv.addObject("colList", colList);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
        mv.addObject("colMetadata", colMetadata);
        return mv;
    }
    
    /**
     * 显示超期列表 
     */
    @EncoderQueryString
    public ModelAndView showOverTimeEdocList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/overTimeEdocList");
        Long memberId = null;
        String memberIdStr = request.getParameter("memberId");
        if(Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);
        }
        else{
            memberId = CurrentUser.get().getId();
        }
        int type = 0;
        String typeStr = request.getParameter("type");
        if(Strings.isNotBlank(typeStr)){
            type = Integer.parseInt(typeStr);
        }
        Date beginDate = null;
        Date endDate = null;
        if(type == 6 || type == 7){
            beginDate = Datetimes.getTodayFirstTime(request.getParameter("beginDate"));
            endDate = Datetimes.getTodayLastTime(request.getParameter("endDate"));
        }
        List<EdocSummaryModel> colList = workStatManager.queryOverEdocTimeList(memberId, type, beginDate, endDate);
        mv.addObject("colList", colList);
        Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.edoc);
        Metadata attitude = metadataManager.getMetadata(MetadataNameEnum.collaboration_attitude); //处理意见 attitude
        colMetadata.put(MetadataNameEnum.collaboration_attitude.toString(), attitude);
        Metadata deadline = metadataManager.getMetadata(MetadataNameEnum.collaboration_deadline); //处理期限 attitude
        colMetadata.put(MetadataNameEnum.collaboration_deadline.toString(), deadline);    
        mv.addObject("colMetadata", colMetadata);
        return mv;
    }
    
    /**
     * 弹出选择被管理人员对话框
     */
    public ModelAndView showSelectMemberDlg(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView("collaboration/manage/selectMember");
        User user = CurrentUser.get();
        int app = ApplicationCategoryEnum.collaboration.getKey();
        String appStr = request.getParameter("app");
        if(Strings.isNotBlank(appStr)){
            app = Integer.parseInt(appStr);
        }
        //获得授权范围内的人员id，以便查询
        List<Long> memberIdsList = workStatManager.getMembersByGrantorIdAndType(user.getLoginAccount(), user.getId(), app);
        mv.addObject("membersList", memberIdsList);
        
        //获得人员对象，从而得到部门对象，最终传到前台一个object数组，第一位为memberid、第二位为departmentName
        List<Object[]> departmentAndMemberNamesList = new ArrayList<Object[]>();
        V3xOrgMember orgMember = null;
        V3xOrgDepartment department = null;
        for(Long memberid :memberIdsList){
        	orgMember = orgManager.getMemberById(memberid);
        	if(orgMember!=null){
        		department= orgManager.getDepartmentById(orgMember.getOrgDepartmentId());
        		Object data[]= {memberid, department.getName()}; 
        		departmentAndMemberNamesList.add(data);
        	}
        }
        mv.addObject("departmentAndMemberNamesList", departmentAndMemberNamesList);
        return mv;
    }
    

    /*
     * 将统计结果导出为Excel
     */
    public ModelAndView statToExcel(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        User user = CurrentUser.get();
        List<Long> membersList = null;
        String memberIdsStr = request.getParameter("memberIds");
        if(Strings.isNotBlank(memberIdsStr)){
            membersList = CommonTools.parseStr2Ids(memberIdsStr);
        }
        else{
            membersList = Arrays.asList(user.getId());
        }
        
        String beginDateStr = request.getParameter("beginDate");
        String endDateStr = request.getParameter("endDate");
        Date beginDate = null, endDate = null;
        if(Strings.isNotBlank(beginDateStr) && Strings.isNotBlank(endDateStr)){
            beginDate = Datetimes.getTodayFirstTime(beginDateStr);
            endDate = Datetimes.getTodayLastTime(endDateStr);
        }
        else{
            Date date = new Date();
            beginDate = Datetimes.getFirstDayInSeason(date);
            endDate = Datetimes.getLastDayInSeason(date);            
        }
        
        int app = NumberUtils.toInt(request.getParameter("app"), ApplicationCategoryEnum.collaboration.key());//勾选的应用类别
        int tag = NumberUtils.toInt(request.getParameter("tagIndex"));	//哪个页签
        Map<Long, int[]> statMap = new HashMap<Long, int[]>();
        DataRow[] dataRow = new DataRow[membersList.size()];
        String[] columnName = null; //列名
        String sheetName = null;
        //工作统计
        if(tag == 0){
            sheetName = Constant.getString("manage.statistics.label") + "-" + Constant.getCommonString("application."+app+".label");
            if(app == ApplicationCategoryEnum.collaboration.key() || app == ApplicationCategoryEnum.edoc.key()){
                columnName = new String[]{
                    Constant.getString("stat.member.title"),
                    Constant.getString("col.coltype.Pending.label"),
                    Constant.getString("col.substate.13.label"),
                    Constant.getString("manage.statistics.currentDay.label") + "-" + Constant.getString("col.coltype.Done.label"),
                    Constant.getString("manage.statistics.currentDay.label") + "-" + Constant.getString("col.coltype.Sent.label"),
                    Constant.getString("manage.statistics.currentWeek.label") + "-" + Constant.getString("col.coltype.Done.label"),
                    Constant.getString("manage.statistics.currentWeek.label") + "-" + Constant.getString("col.coltype.Sent.label"),
                    Constant.getString("manage.statistics.currentMonth.label") + "-" + Constant.getString("col.coltype.Done.label"),
                    Constant.getString("manage.statistics.currentMonth.label") + "-" + Constant.getString("col.coltype.Sent.label"),
                    "[" + beginDateStr + " ~ " + endDateStr + "]" + Constant.getString("col.coltype.Done.label"),
                    "[" + beginDateStr + " ~ " + endDateStr + "]" + Constant.getString("col.coltype.Sent.label"),
                    Constant.getString("manage.statistics.pigeonhole.label")
                };
                statMap = workStatManager.colStat4WorkManage(app, membersList, beginDate, endDate);
            }
            else if(app == ApplicationCategoryEnum.meeting.key() || app == ApplicationCategoryEnum.plan.key()) {
                columnName = new String[]{
                        Constant.getString("stat.member.title"),
                        Constant.getString("stat.allPending.label"),
                        Constant.getString("manage.statistics.currentDay.label") + "-" + Constant.getString("col.coltype.Done.label"),
                        Constant.getString("manage.statistics.currentDay.label") + "-" + Constant.getString("col.coltype.Sent.label"),
                        Constant.getString("manage.statistics.currentWeek.label") + "-" + Constant.getString("col.coltype.Done.label"),
                        Constant.getString("manage.statistics.currentWeek.label") + "-" + Constant.getString("col.coltype.Sent.label"),
                        Constant.getString("manage.statistics.currentMonth.label") + "-" + Constant.getString("col.coltype.Done.label"),
                        Constant.getString("manage.statistics.currentMonth.label") + "-" + Constant.getString("col.coltype.Sent.label"),
                        "[" + beginDateStr + " ~ " + endDateStr + "]" + Constant.getString("col.coltype.Done.label"),
                        "[" + beginDateStr + " ~ " + endDateStr + "]" + Constant.getString("col.coltype.Sent.label"),
                        Constant.getString("manage.statistics.pigeonhole.label")
                    };
                if(app == ApplicationCategoryEnum.meeting.key()) {
                	statMap =  mtMeetingManagerCAP.getUsersMeetingManagerList(membersList, beginDate, endDate); 
                }
                else if(app == ApplicationCategoryEnum.plan.key()) {
                	statMap = planManager.getUsersPlanManagerList(membersList, beginDate, endDate);           
                }
            }
            else if(app == ApplicationCategoryEnum.taskManage.key()) {
            	columnName = new String[] {
                        Constant.getString("stat.member.title"),
                        TaskUtils.getI18n("task.status.notstarted"),
                        Constant.getString("manage.statistics.currentDay.label") + "-" + TaskUtils.getI18n("task.status.marching"),
                        Constant.getString("manage.statistics.currentDay.label") + "-" + TaskUtils.getI18n("task.status.finished"),
                        Constant.getString("manage.statistics.currentDay.label") + "-" + TaskUtils.getI18n("task.status.delayed"),
                        Constant.getString("manage.statistics.currentWeek.label") + "-" + TaskUtils.getI18n("task.status.marching"),
                        Constant.getString("manage.statistics.currentWeek.label") + "-" + TaskUtils.getI18n("task.status.finished"),
                        Constant.getString("manage.statistics.currentWeek.label") + "-" + TaskUtils.getI18n("task.status.delayed"),
                        Constant.getString("manage.statistics.currentMonth.label") + "-" + TaskUtils.getI18n("task.status.marching"),
                        Constant.getString("manage.statistics.currentMonth.label") + "-" + TaskUtils.getI18n("task.status.finished"),
                        Constant.getString("manage.statistics.currentMonth.label") + "-" + TaskUtils.getI18n("task.status.delayed"),
                        "[" + beginDateStr + " ~ " + endDateStr + "]" + TaskUtils.getI18n("task.status.marching"),
                        "[" + beginDateStr + " ~ " + endDateStr + "]" + TaskUtils.getI18n("task.status.finished"),
                        "[" + beginDateStr + " ~ " + endDateStr + "]" + TaskUtils.getI18n("task.status.delayed"),
                        TaskUtils.getI18n("task.status.canceled")
                    };
            	statMap = taskInfoManager.getStatisticInfo(membersList, beginDate, endDate);
            }
        }
        // 超期统计
        else if(tag == 1){
            sheetName = Constant.getString("manage.overTimeStat.label") + "-" + Constant.getCommonString("application."+app+".label");
            columnName = new String[]{
                    Constant.getString("stat.member.title"),
                    Constant.getCommonString("common.workflow.dep"),
                    Constant.getString("manage.statistics.currentWeek.label") + "-" + Constant.getString("stat.overtime.pending.label"),
                    Constant.getString("manage.statistics.currentWeek.label") + "-" + Constant.getString("stat.overtime.done.label"),
                    Constant.getString("manage.statistics.currentMonth.label") + "-" + Constant.getString("stat.overtime.pending.label"),
                    Constant.getString("manage.statistics.currentMonth.label") + "-" + Constant.getString("stat.overtime.done.label"),
                    Constant.getString("manage.statistics.currentQuarter.label") + "-" + Constant.getString("stat.overtime.pending.label"),
                    Constant.getString("manage.statistics.currentQuarter.label") + "-" + Constant.getString("stat.overtime.done.label"),
                    "[" + beginDateStr + " ~ " + endDateStr + "]" + Constant.getString("stat.overtime.pending.label"),
                    "[" + beginDateStr + " ~ " + endDateStr + "]" + Constant.getString("stat.overtime.done.label")
            };
            statMap = workStatManager.colOverTimeStat(app, membersList, beginDate, endDate);
        }

        try {
            int i = 0;
            for (Long id : membersList){
                V3xOrgMember member = orgManager.getMemberById(id);
                int[] statArray = statMap.get(id);
                dataRow[i] = new DataRow();
                dataRow[i].addDataCell(Functions.showMemberName(member), 1);
                if(tag == 1){
                    V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
                    dataRow[i].addDataCell(dept.getName(), 1);                    
                }
                for (int j : statArray) {
                    dataRow[i].addDataCell(String.valueOf(j), 7);
                }
                i++;
            }
            DataRecord dataRecord = new DataRecord();
            dataRecord.addDataRow(dataRow);
            dataRecord.setColumnName(columnName);
            dataRecord.setTitle(sheetName);
            dataRecord.setSheetName(sheetName);
            fileToExcelManager.save(request, response, sheetName, dataRecord);
        }
        catch (Exception e) {
            log.error("协同统计导出Excel异常：", e);
        }
                    
        return null;
    }
    
    // from this line the founder of these codes is mercurial_lin
    /**
     * 工作管理 - Manager of Unit : 授权页面Iframe
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView manageSetListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("collaboration/manage/manageListMain");
        return mav;
    }
    
    /**
     * 工作管理 - Manager of Unit : 授权List
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView manageSetList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("collaboration/manage/manageList");
        User user = CurrentUser.get();
        List<ManagementSet> list = workStatManager.findSetListByDomainId(user.getLoginAccount());
        mav.addObject("setList", pagenate(list));
        return mav;
    }
    
    /**
     *
     * 方法描述：工作管理设置-添加
     *
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView addManagementSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("collaboration/manage/manageDetail");
        return mav;
    }
    
    /**
     *
     * 方法描述：工作管理设置-编辑
     *
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView editManagementSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("collaboration/manage/manageEditDetail");
        String id = request.getParameter("id");
        ManagementSet set = workStatManager.findById(Long.valueOf(id));
        mav.addObject("bean", set);
        return mav;
    }
    
    /**
     * 
     * 方法描述：工作管理设置-修改
     *
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView updateManagementSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
    
        String id = request.getParameter("id");
        String personId = request.getParameter("peopleId");
        String grantId = request.getParameter("grantId");
        String manageRange = request.getParameter("manageRange");
        User user = CurrentUser.get();
        if(!Strings.isBlank(grantId) && !Strings.isBlank(personId) && !Strings.isBlank(manageRange) && !Strings.isBlank(id)){
        	workStatManager.updateSetAndAcls(Long.valueOf(id), personId, grantId, manageRange);
        	appLogManager.insertLog(user, AppLogAction.WorkManageAuth_Update, Functions.getAccount(user.getAccountId()).getName(),"1");
        }

        return super.refreshWindow("parent");
    }

    /**
     * 
     * 方法描述：工作管理设置-修改
     *
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView saveManagementSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String personId = request.getParameter("peopleId");
        String grantId = request.getParameter("grantId");
        String manageRange = request.getParameter("manageRange");
        User user = CurrentUser.get();
        if(!Strings.isBlank(grantId) && !Strings.isBlank(personId) && !Strings.isBlank(manageRange)){
        	workStatManager.saveSetAndAcls(personId, grantId, manageRange);
        	appLogManager.insertLog(user, AppLogAction.WorkManageAuth_New, Functions.getAccount(user.getAccountId()).getName());
        }
        
        return super.refreshWindow("parent");
    }    
    
    /**
     * 
     * 方法描述：工作管理设置-删除
     *
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView deleteManagementSet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String id = request.getParameter("id");
        User user = CurrentUser.get();
        if(!Strings.isBlank(id)){
        	workStatManager.deleteSetAndAcls(id);
        	appLogManager.insertLog(user, AppLogAction.WorkManageAuth_Delete, Functions.getAccount(user.getAccountId()).getName(),"2");
        }
        return super.refreshWindow("parent");
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
}