package com.seeyon.v3x.hr.controller;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordState;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import com.seeyon.v3x.hr.manager.RecordManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.webmodel.RecordStatisticDTO;
import com.seeyon.v3x.hr.webmodel.WebRecord;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

public class HrRecordController extends BaseController {
	private transient static final Log LOG = LogFactory
	.getLog(HrRecordController.class);
	private RecordManager recordManager;
	private FileToExcelManager fileToExcelManager;
	private OrgManager orgManager;
	private OrgManagerDirect orgManagerDirect;
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public RecordManager getRecordManager() {
		return recordManager;
	}

	public void setRecordManager(RecordManager recordManager) {
		this.recordManager = recordManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	/**
	 * 上班打卡
	 * @param 
	 * @return 
	 */
	public ModelAndView addRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		Long staffid = CurrentUser.get().getId();
		Long depid = CurrentUser.get().getDepartmentId();
		//打卡都记录到原单位,不记录到兼职单位 huangfj 20120425  防止系统自动打卡时，在原单位重复打卡
		Long accountId = CurrentUser.get().getAccountId();//.getLoginAccount();
		String remark = request.getParameter("remark");
		String signInIP = Strings.getRemoteAddr(request);
		Calendar time = Calendar.getInstance();	
		RecordState rs = new RecordState();
		int bth=Integer.parseInt(recordManager.getBeginHour());
	    int btm=Integer.parseInt(recordManager.getBeginMinute());
		if(time.get(Calendar.HOUR_OF_DAY)>bth || (time.get(Calendar.HOUR_OF_DAY)==bth && time.get(Calendar.MINUTE)>btm)){
			rs.setId(9);
		}else{
			rs.setId(2);
		}
		Record record = new Record();
		if (recordManager.isWorkDay(time.getTime())) {
			record.setIsWorkDay(1);
		} else {
			record.setIsWorkDay(0);
		}
		record.setStaffer_id(staffid);
		record.setDep_id(depid);
		record.setAccountId(accountId);
		record.setBegin_work_time(time.getTime());
		record.setRemark(remark);
		record.setYear(time.get(Calendar.YEAR));
		record.setMonth(time.get(Calendar.MONTH)+1);
		record.setDay(time.get(Calendar.DAY_OF_MONTH));
		record.setBegin_hour(this.recordManager.getBeginHour());
		record.setBegin_minute(this.recordManager.getBeginMinute());
		record.setEnd_hour(this.recordManager.getEndHour());
		record.setEnd_minute(this.recordManager.getEndMinute());
		record.setSignInIP(signInIP);
	    record.setState(rs);
		recordManager.addRecord(record);
		return super.refreshWorkspace();
	}
	
	/**
	 * 下班打卡
	 * @param 
	 * @return 
	 */
	public ModelAndView updateRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String remark = request.getParameter("remark");
		String signOutIP = request.getRemoteAddr();
		recordManager.updateRecord(remark, signOutIP);
		return super.refreshWorkspace();
	}
	
	/**
	 * 考勤统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView attendanceStatistic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/attendanceStatistic");
		String staffId = request.getParameter("staffId");
		String deptId = request.getParameter("departmentId");
		Date startDate = null;
		Date endDate = null;
		if(request.getParameter("fromTime") != null && !request.getParameter("fromTime").equals("")){
			startDate = Datetimes.parseDate(request.getParameter("fromTime"));
			endDate = Datetimes.parseDate(request.getParameter("toTime"));
		}else{
			startDate = Datetimes.getFirstDayInMonth(Calendar.getInstance().getTime());
			endDate = Datetimes.getLastDayInMonth(Calendar.getInstance().getTime());
		}
		mav.addObject("fromTime", startDate);
		mav.addObject("toTime", endDate);
		startDate = Datetimes.getTodayFirstTime(startDate);
		endDate = Datetimes.getTodayLastTime(endDate);
		List<RecordStatisticDTO> recordDTOs = new ArrayList<RecordStatisticDTO>();
		if(Strings.isNotBlank(staffId)){
			if("nobody".equals(staffId)) {
				mav.addObject("recordDTOs", recordDTOs);
				mav.addObject("resultCount", 0);
				return mav;
			}
			RecordStatisticDTO recordDTO = new RecordStatisticDTO();
			V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId));
			String department = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId()).getName();
			recordDTO.setUserId(member.getId());
			recordDTO.setName(member.getName());
			recordDTO.setDepartment(department);
			recordDTO.setNoBeginCard(recordManager.getNoBeginCardStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setNoEndCard(recordManager.getNoEndCardStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setNoCard(recordManager.getNoCardStatisticById(Long.parseLong(staffId), startDate, endDate));
			recordDTO.setComeLate(recordManager.getComeLateStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setLeaveEarly(recordManager.getLeaveEarlyStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setBoth(recordManager.getBothStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setNormal(recordManager.getNormalStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setNoBeginCardLeaveEarly(recordManager.getNoBeginCardLeaveEarlyStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTO.setComeLateNoEndCard(recordManager.getComeLateNoEndCardStatisticById(Long.parseLong(staffId), startDate, endDate).size());
			recordDTOs.add(recordDTO);
			mav.addObject("recordDTOs", CommonTools.pagenate(recordDTOs));
			mav.addObject("resultCount", recordDTOs.size());
			return mav;
		}
		else if(Strings.isNotBlank(deptId)){
			String department = orgManagerDirect.getDepartmentById(Long.parseLong(deptId)).getName();
			List<V3xOrgMember> members = orgManager.getMembersByDepartment(Long.parseLong(deptId), false);
			List<V3xOrgMember> ms = CommonTools.pagenate(members);
			
			statisticByIdGroupByMemberId(recordDTOs, ms, startDate, endDate, department);
			
			mav.addObject("recordDTOs", recordDTOs);
			mav.addObject("resultCount", recordDTOs.size());
			return mav;
		}
		else{
			List<V3xOrgMember> members = orgManager.getAllMembers(CurrentUser.get().getAccountId());
			List<V3xOrgMember> ms = CommonTools.pagenate(members);
			
			statisticByIdGroupByMemberId(recordDTOs, ms, startDate, endDate, null);
			
			mav.addObject("recordDTOs", recordDTOs);
			mav.addObject("resultCount", recordDTOs.size());
			return mav;
		}
	}
	
	private void statisticByIdGroupByMemberId(List<RecordStatisticDTO> recordDTOs, List<V3xOrgMember> members, Date startDate, Date endDate, String department) throws Exception{
		Map<Long, Integer> NoBeginCard = recordManager.getNoBeginCardStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> NoEndCard = recordManager.getNoEndCardStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> NoCard = recordManager.getNoCardStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> ComeLate = recordManager.getComeLateStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> LeaveEarly = recordManager.getLeaveEarlyStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> Both = recordManager.getBothStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> Normal = recordManager.getNormalStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> NoBeginCardLeaveEarly = recordManager.getNoBeginCardLeaveEarlyStatisticByIdGroupByMemberId(startDate, endDate);
		Map<Long, Integer> ComeLateNoEndCard = recordManager.getComeLateNoEndCardStatisticByIdGroupByMemberId(startDate, endDate);
		long accountId = CurrentUser.get().getAccountId();
		for(V3xOrgMember member : members){
			//不统计兼职人员
			if(member.getOrgAccountId().longValue()!=accountId)
				continue;
			
			department = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId()).getName();
			
			RecordStatisticDTO recordDTO = new RecordStatisticDTO();
			recordDTO.setUserId(member.getId());
			recordDTO.setName(member.getName());
			recordDTO.setDepartment(department);
			
			recordDTO.setNoBeginCard(Strings.escapeNULL(NoBeginCard.get(member.getId()), 0));
			recordDTO.setNoEndCard(Strings.escapeNULL(NoEndCard.get(member.getId()), 0));
			recordDTO.setNoCard(Strings.escapeNULL(NoCard.get(member.getId()), 0));
			recordDTO.setComeLate(Strings.escapeNULL(ComeLate.get(member.getId()), 0));
			recordDTO.setLeaveEarly(Strings.escapeNULL(LeaveEarly.get(member.getId()), 0));
			recordDTO.setBoth(Strings.escapeNULL(Both.get(member.getId()), 0));
			recordDTO.setNormal(Strings.escapeNULL(Normal.get(member.getId()), 0));
			recordDTO.setNoBeginCardLeaveEarly(Strings.escapeNULL(NoBeginCardLeaveEarly.get(member.getId()), 0));
			recordDTO.setComeLateNoEndCard(Strings.escapeNULL(ComeLateNoEndCard.get(member.getId()), 0));
			
			recordDTOs.add(recordDTO);
		}
	}
	
	/**
	 * 统计数据
	 * @author lucx
	 */
	public ModelAndView statistic(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = null;
		Long staffid = RequestUtils.getLongParameter(request, "staffId");
		String staffName = request.getParameter("staffName");
		if(staffid == null){
		   staffid = CurrentUser.get().getId();
		   path = "hr/record/statistic";		   
		}
		else {
		   path = "hr/record/statisticManager";
		}
		ModelAndView mav = new ModelAndView(path);
		if(staffid != null){
			mav.addObject("usable", true);
		}
		mav.addObject("staffName", staffName);
		mav.addObject("staffId", staffid);
		Calendar time = Calendar.getInstance();
		Date date = time.getTime();
		Date ft = Datetimes.parseDate(request.getParameter("fromTime"));
		Date et = Datetimes.parseDate(request.getParameter("toTime"));
		if(ft != null) {
			mav.addObject("fromTime", ft);
			mav.addObject("toTime", et);
			
			ft = Datetimes.getTodayFirstTime(ft);
			et = Datetimes.getTodayLastTime(et);
			// TODO 这里以及下面都不应该使用取list的size的方法取总数，直接select count(id) from *** 即可
			mav.addObject("noBeginCard", recordManager.getNoBeginCardStatisticById(staffid, ft, et).size());
			mav.addObject("noEndCard", recordManager.getNoEndCardStatisticById(staffid, ft, et).size());
			mav.addObject("noCard", recordManager.getNoCardStatisticById(staffid, ft, et));
			mav.addObject("comeLate", recordManager.getComeLateStatisticById(staffid, ft, et).size());
			mav.addObject("leaveEarly", recordManager.getLeaveEarlyStatisticById(staffid, ft, et).size());
			mav.addObject("both", recordManager.getBothStatisticById(staffid, ft, et).size());
			mav.addObject("normal", recordManager.getNormalStatisticById(staffid, ft, et).size());
			mav.addObject("noBeginCardLeaveEarly", recordManager.getNoBeginCardLeaveEarlyStatisticById(staffid, ft, et).size());
			mav.addObject("comeLateNoEndCard", recordManager.getComeLateNoEndCardStatisticById(staffid, ft, et).size());
		}
		else {
			mav.addObject("fromTime", time.getTime());
			mav.addObject("toTime", time.getTime());
			
			Date begin = Datetimes.getTodayFirstTime(date);
			Date end = Datetimes.getTodayLastTime(date);
			
			mav.addObject("noBeginCard", recordManager.getNoBeginCardStatisticById(staffid, begin, end).size());
			mav.addObject("noEndCard", recordManager.getNoEndCardStatisticById(staffid, begin, end).size());
			mav.addObject("noCard", recordManager.getNoCardStatisticById(staffid, begin, end));
			mav.addObject("comeLate", recordManager.getComeLateStatisticById(staffid, begin, end).size());
			mav.addObject("leaveEarly", recordManager.getLeaveEarlyStatisticById(staffid, begin, end).size());
			mav.addObject("both", recordManager.getBothStatisticById(staffid, begin, end).size());
			mav.addObject("normal", recordManager.getNormalStatisticById(staffid, begin, end).size());
			mav.addObject("noBeginCardLeaveEarly", recordManager.getNoBeginCardLeaveEarlyStatisticById(staffid, ft, et).size());
			mav.addObject("comeLateNoEndCard", recordManager.getComeLateNoEndCardStatisticById(staffid, ft, et).size());
		}
        
		return mav;
	}
	
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView statisticManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/statisticManager");
		Calendar time = Calendar.getInstance();
		mav.addObject("fromTime", Datetimes.getFirstDayInMonth(time.getTime()));
		mav.addObject("toTime", Datetimes.getLastDayInMonth(time.getTime()));
		mav.addObject("usable", false);
		return mav;
	}
	
	public ModelAndView initRecordManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try{
			User user = CurrentUser.get() ;
			if(!Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_HRADMIN, user)){
				LOG.info("人员登录考勤管理--"+user.getId()+request.getRemoteAddr()) ;
				return null ;
			}			
		}catch(Exception e){
			LOG.info("人员登录考勤管理--"+request.getRemoteAddr()) ;
			return null ;
		}
		
		ModelAndView mav=new ModelAndView("hr/record/recordManagerHomeList");	
		return mav;
	}	
	public ModelAndView recordManagerHomeListEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String recordType=RequestUtils.getStringParameter(request, "recordType");
		ModelAndView mav = new ModelAndView("hr/record/recordManagerHomeEntry");
		mav.addObject("recordType", recordType);
		return mav;
	}
	
	
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initRecordManagerToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		String type = request.getParameter("type");
		if(type.equals("staffRecordList")){
			mav = new ModelAndView("hr/record/recordManagerToolbar");
			Calendar time = Calendar.getInstance();
			mav.addObject("toTime", Datetimes.getLastDayInMonth(time.getTime()));
			mav.addObject("fromTime", Datetimes.getFirstDayInMonth(time.getTime()));
		}else{
			mav = new ModelAndView("hr/record/formToolbar");
			mav.addObject("dynamicForm", type);
		}
		return mav;
	}

	
	public ModelAndView initRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/record/home");
	}
	public ModelAndView homeEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/homeEntry");
		return mav;
	}
	
	public ModelAndView initToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/toolbar");	
		Calendar time = Calendar.getInstance();
		mav.addObject("toTime", Datetimes.getLastDayInMonth(time.getTime()));
		mav.addObject("fromTime", Datetimes.getFirstDayInMonth(time.getTime()));
		return mav;
	}
	
	public ModelAndView initDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView();
		int type = RequestUtils.getIntParameter(request, "recordType",1);
		if (type==1)  {}; 
		if (type==2)  {}; 
		if (type==3)  {}; 
		if (type==4)  {}; 
		return mav;
	}
	
	public ModelAndView card(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav=new ModelAndView("hr/record/card");
		Long staffid = CurrentUser.get().getId();
		Calendar time = Calendar.getInstance();	
	    Record rc = recordManager.getRecord(staffid, time.getTime());
	    mav.addObject("record", rc);
	    String bh = recordManager.getBeginHour();
	    String bm = recordManager.getBeginMinute();
	    String begin =bh+":"+bm;
	    mav.addObject("begin", begin);
	    String eh = recordManager.getEndHour();
	    String em = recordManager.getEndMinute();
	    String end = eh+":"+em;
	    mav.addObject("end", end);

	    String remark = request.getParameter("remark");
	    if(remark!=null){
	      rc.setRemark(remark);
	    }
	    
		return mav;
	}
	
	public ModelAndView searchRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/ownRecordList");
		Long staffId = CurrentUser.get().getId();
		Date fromTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "fromTime"), "yyyy-MM-dd");
		Date toTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "toTime"), "yyyy-MM-dd");
		List<Record> records = this.pagenate(this.recordManager.getAllRecord(staffId, fromTime, toTime));
		mav.addObject("records", records);
		return mav;
	}
	
	public ModelAndView searchAllRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/allStaffRecordList");
		Date fromTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "fromTime"), "yyyy-MM-dd");
		Date toTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "toTime"), "yyyy-MM-dd");
		List<Record> records = new ArrayList<Record>();
		records = this.recordManager.getAllStaffRecordByPage(fromTime, toTime);
		records = this.pagenate(records);
		mav.addObject("resultCount", records.size());
//		List<WebRecord> webRecords = this.pagenate(this.toWebRecord(records));
//		mav.addObject("webRecords", webRecords);
		mav.addObject("webRecords", this.toWebRecord(records));
		return mav;
	}
	
	public ModelAndView statisticDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/ownRecordList");
		List<Record> records = null;
		Long staffId = CurrentUser.get().getId();
		String type = RequestUtils.getStringParameter(request, "type");
		Date fromTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "fromTime"), "yyyy-MM-dd");
		Date toTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "toTime"), "yyyy-MM-dd");
		records = this.getRecords(type, staffId, fromTime, toTime);
		mav.addObject("records", records);
		return mav;
	}
	
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView statisticManagerDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/record/staffRecordList");
		List<Record> records = new ArrayList<Record>();
		Long staffId = RequestUtils.getLongParameter(request, "staffId");
		String staffName = RequestUtils.getStringParameter(request, "staffName");
		String type = RequestUtils.getStringParameter(request, "type");
		Date fromTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "fromTime"), "yyyy-MM-dd");
		Date toTime=Datetimes.parse(RequestUtils.getStringParameter(
				request, "toTime"), "yyyy-MM-dd");
		records = this.getRecords(type, staffId, fromTime, toTime);
		V3xOrgMember member = this.orgManagerDirect.getMemberById(staffId);
		String department = this.orgManagerDirect.getDepartmentById(member.getOrgDepartmentId()).getName();
		LOG.debug("department=========="+department);		
		mav.addObject("records", records);
		mav.addObject("staffName", staffName);
		mav.addObject("department", department);
		mav.addObject("resultCount", records.size());
		return mav;
	}
	
	public ModelAndView remark(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/remark");
		String remark = request.getParameter("remark");
		mav.addObject("remark", remark);
		return mav;
	}
	
	public ModelAndView ownRecordList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/ownRecordList");
		Long staffId = CurrentUser.get().getId();
		Date date = new Date();
		Date fromTime = Datetimes.getFirstDayInMonth(date);
		Date toTime = Datetimes.getLastDayInMonth(date);
		List<Record> result = this.recordManager.getAllRecord(staffId, fromTime, toTime);
		List<Record> records = this.pagenate(result);
		mav.addObject("records", records);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView staffRecordList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/initStaffRecordList");
		List<Record> records = this.recordManager.getAllStaffRecords(new Date());
		List<WebRecord> webRecords = new ArrayList<WebRecord>();
		for(Record record : records){
			WebRecord webRecord = new WebRecord();
			String staffName = orgManagerDirect.getMemberById(record.getStaffer_id()).getName();
			webRecord.setName(staffName);
			webRecord.setRecord(record);
			webRecords.add(webRecord);
		}
		mav.addObject("records", this.pagenate(webRecords));
		mav.addObject("resultCount", webRecords.size());
		return mav;
	}
	
	
	
	public ModelAndView initWorkingTime(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/workingTimeSet");
		RecordWorkingTime workingTime = new RecordWorkingTime();
		workingTime.setBegin_hour(Integer.parseInt(recordManager.getBeginHour()));
		workingTime.setBegin_minute(Integer.parseInt(recordManager.getBeginMinute()));
		workingTime.setEnd_hour(Integer.parseInt(recordManager.getEndHour()));
		workingTime.setEnd_minute(Integer.parseInt(recordManager.getEndMinute()));
		mav.addObject("workingTime", workingTime);
		return mav;
	}
	
	public ModelAndView workingTime(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
//		ModelAndView mav=new ModelAndView("hr/record/workingTimeSet");
		RecordWorkingTime workingTime = new RecordWorkingTime();
		workingTime.setBegin_hour(Integer.parseInt(request.getParameter("beginHour")));
		workingTime.setBegin_minute(Integer.parseInt(request.getParameter("beginMinute")));
		workingTime.setEnd_hour(Integer.parseInt(request.getParameter("endHour")));
		workingTime.setEnd_minute(Integer.parseInt(request.getParameter("endMinute")));
		recordManager.setWorkingTime(workingTime);
//		RecordWorkingTime newworkingTime = new RecordWorkingTime();
//		newworkingTime.setBegin_hour(Integer.parseInt(recordManager.getBeginHour()));
//		newworkingTime.setBegin_minute(Integer.parseInt(recordManager.getBeginMinute()));
//		newworkingTime.setEnd_hour(Integer.parseInt(recordManager.getEndHour()));
//		newworkingTime.setEnd_minute(Integer.parseInt(recordManager.getEndMinute()));
//		mav.addObject("workingTime", newworkingTime);
		return super.refreshWorkspace();
	}
	
	public List<Record> getRecords(String type, Long staffId, Date fromTime, Date toTime) throws Exception {
		List<Record> records = new ArrayList<Record>();
		if(type.equals("noBegin")){
			records = this.pagenate(recordManager.getNoBeginCardStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("noEnd")){
			records = this.pagenate(recordManager.getNoEndCardStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("noCard")){
			//records = this.pagenate(recordManager.getNoCardStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("comeLate")){
			records = this.pagenate(recordManager.getComeLateStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("leaveEarly")){
			records = this.pagenate(recordManager.getLeaveEarlyStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("both")){
			records = this.pagenate(recordManager.getBothStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("normal")){
			records = this.pagenate(recordManager.getNormalStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("noBeginLeaveEarly")){
			records = this.pagenate(recordManager.getNoBeginCardLeaveEarlyStatisticById(staffId, fromTime, toTime));
		}
		if(type.equals("comeLateNoEnd")){
			records = this.pagenate(recordManager.getComeLateNoEndCardStatisticById(staffId, fromTime, toTime));
		}
		return records;
	}
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>(0);
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		LOG.debug("first: " + first + ", pageSize: " + pageSize + ", size: "
				+ list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	public ModelAndView viewRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("hr/record/recordDetail");
		Long recordId = RequestUtils.getLongParameter(request, "recordId");
		String staffName = request.getParameter("name");
		if(staffName == null){
			staffName = CurrentUser.get().getName();
		}
		Record record = this.recordManager.getRecordById(recordId);
		mav.addObject("staffName", staffName);
		mav.addObject("record", record);
		return mav;
	}
	
	public ModelAndView viewStaffRecord(HttpServletRequest request,
			HttpServletResponse respose)throws Exception{
		ModelAndView mav = new ModelAndView("hr/record/recordDetail");
		Long recordId = RequestUtils.getLongParameter(request, "recordId");
		Record record = this.recordManager.getRecordById(recordId);
		String name = this.orgManager.getMemberById(record.getStaffer_id()).getName();
		mav.addObject("staffName", name);
		mav.addObject("record", record);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView exportExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Locale locale = LocaleContext.getLocale(request);
		String resource = Constants.RESOURCE_HR;
		String statistic = request.getParameter("statistic");
		if("statistic".equals(statistic)){
			String state_StatisiticInfo = ResourceBundleUtil.getString(resource, locale, "hr.record.attendance.statistic.label");
			String state_Name = ResourceBundleUtil.getString(resource, locale, "hr.staffInfo.name.label");
			String state_department = ResourceBundleUtil.getString(resource, locale, "hr.record.department.label");
			String state_noBegin = ResourceBundleUtil.getString(resource, locale, "hr.record.nobegincard.label");
			String state_noBeginLeaveEarly = ResourceBundleUtil.getString(resource, locale, "hr.record.nobegincard.leaveearly.label");
			String state_noEnd = ResourceBundleUtil.getString(resource, locale, "hr.record.noendcard.label");
			String state_comeLate = ResourceBundleUtil.getString(resource, locale, "hr.record.comelate.label");
			String state_leaveEarly = ResourceBundleUtil.getString(resource, locale, "hr.record.leaveearly.label");
			String state_comeLateNoEnd = ResourceBundleUtil.getString(resource, locale, "hr.record.comelate.noendcard.label");
			String state_both = ResourceBundleUtil.getString(resource, locale, "hr.record.both.label");
			String state_normal = ResourceBundleUtil.getString(resource, locale, "hr.record.normal.label");
			String state_noCard = ResourceBundleUtil.getString(resource, locale, "hr.record.nocard.label");
			Date startDate = null;
			Date endDate = null;
			if(request.getParameter("advancedFromTime") != null && !request.getParameter("advancedFromTime").equals("")){
				startDate = Datetimes.parseDate(request.getParameter("advancedFromTime"));
				endDate = Datetimes.parseDate(request.getParameter("advancedToTime"));
			}else{
				startDate = Datetimes.getFirstDayInMonth(Calendar.getInstance().getTime());
				endDate = Datetimes.getLastDayInMonth(Calendar.getInstance().getTime());
			}
			String departmentIds = request.getParameter("advancedDepartmentIds");
			String personIds = request.getParameter("advancedPeopleIds");
			startDate = Datetimes.getTodayFirstTime(startDate);
			endDate = Datetimes.getTodayLastTime(endDate);
			List<RecordStatisticDTO> recordDTOs = new ArrayList<RecordStatisticDTO>();
			if(personIds != null && !personIds.equals("")){
				RecordStatisticDTO recordDTO = new RecordStatisticDTO();
				V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(personIds));
				String department = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId()).getName();
				recordDTO.setUserId(member.getId());
				recordDTO.setName(member.getName());
				recordDTO.setDepartment(department);
				recordDTO.setNoBeginCard(recordManager.getNoBeginCardStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setNoEndCard(recordManager.getNoEndCardStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setNoCard(recordManager.getNoCardStatisticById(Long.parseLong(personIds), startDate, endDate));
				recordDTO.setComeLate(recordManager.getComeLateStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setLeaveEarly(recordManager.getLeaveEarlyStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setBoth(recordManager.getBothStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setNormal(recordManager.getNormalStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setNoBeginCardLeaveEarly(recordManager.getNoBeginCardLeaveEarlyStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTO.setComeLateNoEndCard(recordManager.getComeLateNoEndCardStatisticById(Long.parseLong(personIds), startDate, endDate).size());
				recordDTOs.add(recordDTO);
			}else if(departmentIds != null && !departmentIds.equals("")){
				String department = orgManagerDirect.getDepartmentById(Long.parseLong(departmentIds)).getName();
				List<V3xOrgMember> members = orgManager.getMembersByDepartment(Long.parseLong(departmentIds), false);
				statisticByIdGroupByMemberId(recordDTOs, members, startDate, endDate, department);
			}else{
				List<V3xOrgMember> members = orgManager.getAllMembers(CurrentUser.get().getAccountId());
				statisticByIdGroupByMemberId(recordDTOs, members, startDate, endDate, null);
			}
			
			if(recordDTOs.size() > 65535){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(\"Excel的行数最大允许[65535]，现导出的行数已经超出范围，请重新设置后再导出！\")");
				out.println("</script>");
				out.flush();
				return null;
			}
			if(recordDTOs.size() > 0){
				DataRecord record = new DataRecord();
				initDataRecordStatistic(record,state_StatisiticInfo+"("+new SimpleDateFormat("yyyy-MM-dd").format(startDate)+"—"+new SimpleDateFormat("yyyy-MM-dd").format(endDate)+")",state_Name,state_department,state_noBegin,state_noBeginLeaveEarly,state_noEnd,state_comeLate,state_leaveEarly,state_comeLateNoEnd,state_both,state_normal,state_noCard);
				for(RecordStatisticDTO recordDTO : recordDTOs){
					DataRow row = new DataRow();
					row.addDataCell(recordDTO.getName(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getDepartment()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getNoBeginCard()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getNoBeginCardLeaveEarly()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getNoEndCard()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getComeLate()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getLeaveEarly()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getComeLateNoEndCard()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getBoth()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getNormal()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(String.valueOf(recordDTO.getNoCard()), DataCell.DATA_TYPE_TEXT);
					record.addDataRow(row);
				}
				this.fileToExcelManager.saveAsCSV(request,response, "record", record);
			}
			return null;
		}else{
			String state_StatisiticInfo = ResourceBundleUtil.getString(resource, locale, "hr.record.statisticInfo.label");
			String state_Name = ResourceBundleUtil.getString(resource, locale, "hr.staffInfo.name.label");
			String state_department = ResourceBundleUtil.getString(resource, locale, "hr.record.department.label");
			String state_CheckinTime = ResourceBundleUtil.getString(resource, locale, "hr.record.checkinTime.actually.label");
			String state_InStatedTime = ResourceBundleUtil.getString(resource, locale, "hr.record.checkinTime.stated.label");
			String state_SignInIP = ResourceBundleUtil.getString(resource, locale, "hr.record.sign.in.ip.label");
			String state_CheckoutTime = ResourceBundleUtil.getString(resource, locale, "hr.record.checkoutTime.actually.label");
			String state_OutStatedTime = ResourceBundleUtil.getString(resource, locale, "hr.record.checkoutTime.stated.label");
			String state_SignOutIP = ResourceBundleUtil.getString(resource, locale, "hr.record.sign.out.ip.label");
			String state_State = ResourceBundleUtil.getString(resource, locale, "hr.record.state.label");
			String state_Remark = ResourceBundleUtil.getString(resource, locale, "hr.record.remark.label");
			List<Record> records = new ArrayList<Record>();
			List<WebRecord> webRecords = new ArrayList<WebRecord>();
			String searchAll = request.getParameter("searchAll");
			String staffName = request.getParameter("staffName");
			String advanced = request.getParameter("advanced");
			if(advanced.equals("advanced")){			
				String fTime = request.getParameter("advancedFromTime");
				String tTime = request.getParameter("advancedToTime");
				String departmentIds = request.getParameter("advancedDepartmentIds");
				String personIds = request.getParameter("advancedPeopleIds");
				int state = Integer.parseInt(request.getParameter("advancedState"));
				records = this.advancedQuery(fTime, tTime, departmentIds, state, personIds);
			}else{
				if(searchAll.equals("all")){
					Date fTime=Datetimes.parse(RequestUtils.getStringParameter(
							request, "fTime"), "yyyy-MM-dd");
					Date tTime=Datetimes.parse(RequestUtils.getStringParameter(
							request, "tTime"), "yyyy-MM-dd");
					records = this.recordManager.getAllStaffRecord(fTime, tTime);
				}else{
					if(staffName.equals("noSearch")){
						Pagination.setNeedCount(false);
						Pagination.setFirstResult(-1);
						Pagination.setMaxResults(-1);
						records = this.recordManager.getAllStaffRecords(new Date());
					}else{
						Long staffId = RequestUtils.getLongParameter(request, "staffId");
						String type = RequestUtils.getStringParameter(request, "type");
						Date fromTime=Datetimes.parse(RequestUtils.getStringParameter(
								request, "fromTime"), "yyyy-MM-dd");
						Date toTime=Datetimes.parse(RequestUtils.getStringParameter(
								request, "toTime"), "yyyy-MM-dd");
						records = this.getRecords(type, staffId, fromTime, toTime);
					}
				}
			}
			webRecords = this.toWebRecord(records);
			
			if(webRecords.size() > 65535){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(\"Excel的行数最大允许[65535]，现导出的行数已经超出范围，请重新设置后再导出！\")");
				out.println("</script>");
				out.flush();
				return null;
			}
			if(webRecords.size() != 0){
				DataRecord record = new DataRecord();
				initDataRecord(record,state_StatisiticInfo,state_Name,state_department,state_CheckinTime,state_InStatedTime,state_SignInIP,state_CheckoutTime,state_OutStatedTime,state_SignOutIP,state_State,state_Remark);
				for(WebRecord rd : webRecords){
					DataRow row = new DataRow();
					String fTime = "";
					String tTime = "";
					if(rd.getRecord().getBegin_work_time() != null && rd.getRecord().getState().getId() != Constants.HR_NOCARD_STATE)
						//fTime = Datetimes.formatDatetime(rd.getRecord().getBegin_work_time());
						fTime = new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(rd.getRecord().getBegin_work_time());
					if(rd.getRecord().getEnd_work_time() != null && rd.getRecord().getState().getId() != Constants.HR_NOCARD_STATE)
						tTime = Datetimes.formatDatetime(rd.getRecord().getEnd_work_time());
					row.addDataCell(rd.getName(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(rd.getDepartment(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(fTime, DataCell.DATA_TYPE_DATE);
					row.addDataCell(rd.getRecord().getBegin_hour()+":"+rd.getRecord().getBegin_minute(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(rd.getRecord().getSignInIP(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(tTime, DataCell.DATA_TYPE_DATE);
					row.addDataCell(rd.getRecord().getEnd_hour()+":"+rd.getRecord().getEnd_minute(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(rd.getRecord().getSignOutIP(), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(ResourceBundleUtil.getString(resource, locale, rd.getRecord().getState().getTrueName()), DataCell.DATA_TYPE_TEXT);
					row.addDataCell(rd.getRecord().getRemark(), DataCell.DATA_TYPE_TEXT);
					record.addDataRow(row);
				}
				
				this.fileToExcelManager.saveAsCSV(request,response, "record", record);
			}
			return null;
		}
	}
	
	private void initDataRecord(DataRecord record, String state_StatisiticInfo, 
			String state_Name, String state_department, String state_CheckinTime,String state_InStatedTime, String state_SignInIP,
			String state_CheckoutTime, String state_OutStatedTime, String state_SignOutIP, String state, String state_Remark){
		record.setSheetName(state_StatisiticInfo);
		record.setTitle(state_StatisiticInfo);
		String[] columnNames = {state_Name,state_department,state_CheckinTime,state_InStatedTime,state_SignInIP,state_CheckoutTime,state_OutStatedTime,state_SignOutIP,state,state_Remark};
		record.setColumnName(columnNames);
	}
	
	private void initDataRecordStatistic(DataRecord record, String state_StatisiticInfo, 
			String state_Name, String state_department, String state_noBegin,String state_noBeginLeaveEarly, String state_noEnd,
			String state_comeLate, String state_leaveEarly, String state_comeLateNoEnd, String state_both, String state_normal, String state_noCard){
		record.setSheetName(state_StatisiticInfo);
		record.setTitle(state_StatisiticInfo);
		String[] columnNames = {state_Name,state_department,state_noBegin,state_noBeginLeaveEarly,state_noEnd,state_comeLate,state_leaveEarly,state_comeLateNoEnd,state_both,state_normal,state_noCard};
		record.setColumnName(columnNames);
	}
	
	public List<WebRecord> toWebRecord(List<Record> records)throws BusinessException{
		List<WebRecord> webRecords = new ArrayList<WebRecord>();
		for(Record record : records) {
			WebRecord webRecord = new WebRecord();
			V3xOrgMember member = orgManagerDirect.getMemberById(record.getStaffer_id());
			String staffName = member.getName();
			String department = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId()).getName();
			if(LOG.isDebugEnabled()) {
				LOG.debug("webDepartment=====" + department);
			}
			webRecord.setName(staffName);
			webRecord.setDepartment(department);
			webRecord.setRecord(record);
			webRecords.add(webRecord);
		}
		return webRecords;
	}
	
	public ModelAndView advancedQuery(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/record/advancedQuery");
		Date ft = Datetimes.parseDate(request.getParameter("fromTime"));
		Date et = Datetimes.parseDate(request.getParameter("toTime"));
		mav.addObject("fromTime", ft);
		mav.addObject("toTime", et);
		return mav;
	}
	
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView attendanceStatic(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/record/statisticsQuery");
		Date ft = Datetimes.parseDate(request.getParameter("fromTime"));
		Date et = Datetimes.parseDate(request.getParameter("toTime"));
		mav.addObject("fromTime", ft);
		mav.addObject("toTime", et);
		return mav;
	}
	
	public ModelAndView recordQuery(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("hr/record/allStaffRecordList");
		String fTime = request.getParameter("fromTime");
		String tTime = request.getParameter("toTime");
		String departmentIds = request.getParameter("departmentId");
		String personIds = request.getParameter("peopleId");
		int state = Integer.parseInt(request.getParameter("state"));
		List<Record> records = this.advancedQuery(fTime, tTime, departmentIds, state, personIds);
		mav.addObject("resultCount", records.size());
		records = this.pagenate(records);
		List<WebRecord> webRecords = new ArrayList<WebRecord>();
		webRecords = this.toWebRecord(records);
		mav.addObject("webRecords", webRecords);
		return mav;
	}
	
	private List<Record> advancedQuery(String fromTime, String toTime, String departmentIds, int state, String personId) throws Exception {
		
		return this.recordManager.getAdvancedQuery(fromTime, toTime, departmentIds, state, personId);
	}
	
	public ModelAndView showWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String showWindowURL = request.getParameter("showWindowURL");
		if (Strings.isBlank(showWindowURL)) {
			showWindowURL = "hr/record/deleteAttendance";
		}
		ModelAndView mav = new ModelAndView(showWindowURL);
		return mav;
	}

	public ModelAndView deleteAttendance(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String delType = request.getParameter("deleteType");
		int monthsAgo = Integer.parseInt(delType);
		this.recordManager.deleteAttendance(monthsAgo);
		return null;
	}
}