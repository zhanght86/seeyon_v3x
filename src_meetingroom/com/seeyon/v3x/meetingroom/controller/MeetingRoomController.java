package com.seeyon.v3x.meetingroom.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.office.admin.manager.AdminManagerCAP;
import com.seeyon.cap.office.common.OfficeModelTypeCAP;
import com.seeyon.cap.office.common.manager.OfficeApplyManagerCAP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meetingroom.domain.MeetingRoom;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomApp;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomPerm;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomRecord;
import com.seeyon.v3x.meetingroom.manager.MeetingRoomManager;
import com.seeyon.v3x.meetingroom.util.MavUtil;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */
public class MeetingRoomController extends BaseManageController{

	/** 资源文件定义 */
	private static String mRes = "com.seeyon.v3x.meetingroom.resources.i18n.MeetingRoomResources";

	/** Manager接口 */
	private MeetingRoomManager meetingRoomManager;

	/** 组织结构接口 */
	private OrgManager orgManager;

	/** 导出Excel接口 */
	private FileToExcelManager fileToExcelManager;

	private AdminManagerCAP adminManagerCAP;
	
	private OfficeApplyManagerCAP officeApplyManagerCAP;
	
	public void setAdminManagerCAP(AdminManagerCAP adminManagerCAP) {
		this.adminManagerCAP = adminManagerCAP;
	}

	/**
	 * 会议室管理主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到index.jsp页面
	 * @throws Exception
	 */
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception{
		boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/index");
		mav.addObject("isAdmin", isAdmin);
		return mav;
	}

	/**
	 * 新建会议室主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到add.jsp页面
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/add");
		return mav;
	}

	/**
	 * 会议室申请主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到app.jsp页面
	 * @throws Exception
	 */
	public ModelAndView app(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/app");
		return mav;
	}

	/**
	 * 会议室审批主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到perm.jsp页面
	 * @throws Exception
	 */
	public ModelAndView perm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/perm");
		return mav;
	}

	/**
	 * 会议室预定撤销主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到cancel.jsp页面
	 * @throws Exception
	 */
	public ModelAndView cancel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/cancel");
		return mav;
	}

	/**
	 * 会议室统计主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到total.jsp页面
	 * @throws Exception
	 */
	public ModelAndView total(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/total");
		return mav;
	}

	/**
	 * 会议室使用情况查看主框架页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到view.jsp页面
	 * @throws Exception
	 */
	public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/view");
		String ids = request.getParameter("ids");
		if(ids!=null && !"".equals(ids))
			mav.addObject("ids", ids);
		return mav;
	}

	/**
	 * 会议室登记列表页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到listadd.jsp页面
	 * @throws Exception
	 */
	public ModelAndView listAdd(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/listadd");
		User user = CurrentUser.get();
		boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
		if(!isAdmin){
			return refreshWorkspace();
		}
		String selectCondition = request.getParameter("selectCondition");
		if(selectCondition != null && selectCondition.length() > 0){
			mav.addObject("selectCondition", selectCondition);
		}
		String name = request.getParameter("name");
		if(name != null && name.length() > 0){
			mav.addObject("conditionValue", name);
		}
		String seatCountStr = request.getParameter("seatCount");
		String seatCountCondition = request.getParameter("seatCountCondition");
		Integer[] seatCount = null;
		if(seatCountStr != null && seatCountStr.length() > 0 && seatCountCondition != null && seatCountCondition.length() > 0){
			seatCount = new Integer[2];
			seatCount[0] = Integer.parseInt(seatCountCondition);
			seatCount[1] = Integer.parseInt(seatCountStr);
			mav.addObject("conditionValue", new String[] { seatCountStr, seatCountCondition });
		}
		String needAppStr = request.getParameter("needApp");
		Integer needApp = null;
		if(needAppStr != null && needAppStr.length() > 0){
			needApp = Integer.parseInt(needAppStr);
		}
		String statusStr = request.getParameter("status");
		Integer status = null;
		if(statusStr != null && statusStr.length() > 0){
			status = Integer.parseInt(statusStr);
			mav.addObject("conditionValue", statusStr);
		}
		String delFlagStr = request.getParameter("delFlag");
		Integer delFlag = null;
		if(delFlagStr != null && delFlagStr.length() > 0){
			delFlag = Integer.parseInt(delFlagStr);
		}else{
			delFlag = com.seeyon.v3x.meetingroom.util.Constants.DelFlag_No;
		}
		List list = this.meetingRoomManager.getMeetingRooms(orgManager.getMemberById(user.getId()), null, name, null, seatCount, needApp, status, delFlag,
				true);
		mav.addObject("list", list);
		return mav;
	}

	/**
	 * 会议室申请列表页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到listapp.jsp页面
	 * @throws Exception
	 */
	public ModelAndView listApp(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/listapp");
		String selectCondition = request.getParameter("selectCondition");
		if(selectCondition != null && selectCondition.length() > 0){
			mav.addObject("selectCondition", selectCondition);
		}
		String name = request.getParameter("name");
		if(name != null && name.length() > 0){
			mav.addObject("conditionValue", name);
		}
		String seatCountStr = request.getParameter("seatCount");
		String seatCountCondition = request.getParameter("seatCountCondition");
		Integer[] seatCount = null;
		if(Strings.isNotBlank(seatCountStr) && Strings.isNotBlank(seatCountCondition)){
			seatCount = new Integer[2];
			seatCount[0] = Integer.parseInt(seatCountCondition);
			seatCount[1] = Integer.parseInt(seatCountStr);
			mav.addObject("conditionValue", new String[] { seatCountStr, seatCountCondition });
		}
		User user = CurrentUser.get();
		List adminId = this.officeApplyManagerCAP.getOfficeApplyList(OfficeModelTypeCAP.meeting_type, user);
		List list = this.meetingRoomManager.MeetingRoomsForApp(name, seatCount,adminId, true);
		mav.addObject("list", list);
		return mav;
	}

	/**
	 * 会议室申请列表页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到listperm.jsp页面
	 * @throws Exception
	 */
	public ModelAndView listPerm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/listperm");
		boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
		if(!isAdmin){
			return refreshWorkspace();
		}
		String selectCondition = request.getParameter("selectCondition");
		if(selectCondition != null && selectCondition.length() > 0){
			mav.addObject("selectCondition", selectCondition);
		}
		User user = CurrentUser.get();
		V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(user.getId());
		List meetingRoomList = this.getMeetingRoomManager().getMeetingRooms(v3xOrgMember, null, null, null, null,
				com.seeyon.v3x.meetingroom.util.Constants.Type_MeetingRoom_NeedApp, com.seeyon.v3x.meetingroom.util.Constants.Status_MeetingRoom_Normal,
				com.seeyon.v3x.meetingroom.util.Constants.DelFlag_No, false);
		String str_MeetingRoomId = request.getParameter("meetingRoomId");
		String str_IsAllowed = request.getParameter("isAllowed");
		String str_PerId = request.getParameter("perId");
		Long meetingRoomId = null;
		Long perId = null;
		Integer isAllowed = null;
		if(str_MeetingRoomId != null && str_MeetingRoomId.length() > 0){
			meetingRoomId = Long.parseLong(str_MeetingRoomId);
			mav.addObject("conditionValue", str_MeetingRoomId);
		}
		if(str_PerId != null && str_PerId.length() > 0){
			perId = Long.parseLong(str_PerId);
			String perName = this.getOrgManager().getMemberById(Long.parseLong(str_PerId)).getName();
			mav.addObject("conditionValue", new String[] { str_PerId, perName });
		}
		if(str_IsAllowed != null && str_IsAllowed.length() > 0){
			isAllowed = Integer.parseInt(str_IsAllowed);
			mav.addObject("conditionValue", str_IsAllowed);
		}
		List list = this.meetingRoomManager.getMeetingRoomsForPerm(meetingRoomId, perId, isAllowed);
		List<Long> departId = this.adminManagerCAP.getAdminManageDepartments(user.getId(), user.getAccountId(), "____1");
		Map<String,Boolean> proxy = new HashMap<String,Boolean>();
		Map<String,String>  departName = new HashMap<String,String>();
		for(Object o:list){
			MeetingRoomPerm perm = (MeetingRoomPerm) o;
			Object[] ob = this.adminManagerCAP.getMemberDepProxy(perm.getMeetingRoomApp().getV3xOrgMember(), user.getAccountId(), user.getId(), "____1", departId);
			proxy.put(perm.getMeetingRoomApp().getV3xOrgMember().getId().toString(), (Boolean)ob[1]);
			departName.put(perm.getMeetingRoomApp().getV3xOrgMember().getId().toString(), ob[0].toString());
		}
		mav.addObject("mrList", meetingRoomList);
		mav.addObject("list", list);
		mav.addObject("proxy", proxy);
		mav.addObject("departmentName", departName);
		return mav;
	}

	/**
	 * 预定撤销列表页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到listmyapp.jsp页面
	 * @throws Exception
	 */
	public ModelAndView listMyApp(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/listmyapp");
		String selectCondition = request.getParameter("selectCondition");
		if(selectCondition != null && selectCondition.length() > 0){
			mav.addObject("selectCondition", selectCondition);
		}
		V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(CurrentUser.get().getId());
		List adMrList = null;
		Boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
		if(isAdmin){
			adMrList = this.meetingRoomManager.getMeetingRooms(v3xOrgMember, null, null, null, null,
					com.seeyon.v3x.meetingroom.util.Constants.Type_MeetingRoom_NeedApp, com.seeyon.v3x.meetingroom.util.Constants.Status_MeetingRoom_Normal,
					com.seeyon.v3x.meetingroom.util.Constants.DelFlag_No, false);
		}
		User user = CurrentUser.get();
		List adminId = this.officeApplyManagerCAP.getOfficeApplyList(OfficeModelTypeCAP.meeting_type, user);
		List mrList = this.meetingRoomManager.MeetingRoomsForApp(null, null, adminId,false);
		if(adMrList != null && adMrList.size() > 0 && mrList != null && mrList.size() > 0){
			outer: for(int i = 0; i < adMrList.size(); i++){
				MeetingRoom mr = (MeetingRoom) adMrList.get(i);
				for(int j = 0; j < mrList.size(); j++){
					MeetingRoom tempMr = (MeetingRoom) mrList.get(j);
					if(mr.getId().equals(tempMr.getId())){
						continue outer;
					}
				}
				mrList.add(mr);
			}
		}
		String str_MrId = request.getParameter("meetingRoomId");
		String str_IsAllowed = request.getParameter("isAllowed");
		String str_PerId = request.getParameter("perId");
		Long mrId = null;
		Integer isAllowed = null;
		Long perId = null;
		if(str_MrId != null && str_MrId.length() > 0){
			mrId = Long.parseLong(str_MrId);
			mav.addObject("conditionValue", str_MrId);
		}
		if(str_IsAllowed != null && str_IsAllowed.length() > 0){
			isAllowed = Integer.parseInt(str_IsAllowed);
			mav.addObject("conditionValue", str_IsAllowed);
		}
		if(str_PerId != null && str_PerId.length() > 0){
			perId = Long.parseLong(str_PerId);
			String perName = this.getOrgManager().getMemberById(perId).getName();
			mav.addObject("conditionValue", new String[] { str_PerId, perName });
		}
		List list = this.meetingRoomManager.getCancelList(mrId, isAllowed, perId);
		/*List<MeetingRoomApp> webList = new ArrayList<MeetingRoomApp>() ;
		if(list != null ){
			for(Object object : list){
				MeetingRoomApp meetingRoomApp = (MeetingRoomApp)object ;
				if(meetingRoomApp != null) {
					if(meetingRoomApp.getV3xOrgDepartment() != null ){
						if(meetingRoomApp.getV3xOrgDepartment().getOrgAccountId().longValue()
								== user.getAccountId()){
							webList.add(meetingRoomApp) ;
						}
					}
				}			
			}		
		}*/
		mav.addObject("mrList", mrList);
		mav.addObject("list", list);
		mav.addObject("isAdmin", isAdmin);
		return mav;
	}

	/**
	 * 统计列表页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到listtotal.jsp页面
	 * @throws Exception
	 */
	public ModelAndView listTotal(HttpServletRequest request, HttpServletResponse response) throws Exception{
		boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
		if(!isAdmin){
			return refreshWorkspace();
		}
		String strStart = request.getParameter("startDatetime");
		String strEnd = request.getParameter("endDatetime");
		Date startDatetime = null;
		Date endDatetime = null;
		if(strStart != null && strStart.length() > 0 && strEnd != null && strEnd.length() > 0){
			startDatetime = Datetimes.parseDate(strStart);
			endDatetime = Datetimes.parseDate(strEnd);
			Calendar temp = Calendar.getInstance();
			temp.setTime(endDatetime);
			temp.add(Calendar.DATE, 1);
		}else{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			if(c.get(Calendar.DAY_OF_WEEK) == 1){
				c.add(Calendar.DATE, -6);
				startDatetime = new Date(c.getTime().getTime());
				c.add(Calendar.DATE, 7);
				endDatetime = new Date(c.getTime().getTime());
				strStart = Datetimes.formatDate(startDatetime);
				strEnd = Datetimes.formatDate(endDatetime);
			}else{
				c.set(Calendar.DAY_OF_WEEK, 2);
				startDatetime = new Date(c.getTime().getTime());
				c.add(Calendar.DATE, 7);
				endDatetime = new Date(c.getTime().getTime());
				strStart = Datetimes.formatDate(startDatetime);
				strEnd = Datetimes.formatDate(endDatetime);
			}
		}
		List list = this.meetingRoomManager.getTotal(startDatetime, endDatetime, true);
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/listtotal");
		mav.addObject("startDatetime", strStart);
		mav.addObject("endDatetime", strEnd);
		mav.addObject("list", list);
		return mav;
	}

	/**
	 * 统计结果导出
	 * 
	 * @param request
	 * @param response
	 * @return null,不跳转页面
	 * @throws Exception
	 */
	public ModelAndView listTotalExport(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String strStart = request.getParameter("startDatetime");
		String strEnd = request.getParameter("endDatetime");
		Date startDatetime = null;
		Date endDatetime = null;
		if(strStart != null && strStart.length() > 0 && strEnd != null && strEnd.length() > 0){
			startDatetime = Datetimes.parseDate(strStart);
			endDatetime = Datetimes.parseDate(strEnd);
			Calendar temp = Calendar.getInstance();
			temp.setTime(endDatetime);
			temp.add(Calendar.DATE, 1);
		}else{
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			if(c.get(Calendar.DAY_OF_WEEK) == 1){
				c.add(Calendar.DATE, -6);
				startDatetime = new Date(c.getTime().getTime());
				c.add(Calendar.DATE, 7);
				endDatetime = new Date(c.getTime().getTime());
				strStart = Datetimes.formatDate(startDatetime);
				strEnd = Datetimes.formatDate(endDatetime);
			}else{
				c.set(Calendar.DAY_OF_WEEK, 2);
				startDatetime = new Date(c.getTime().getTime());
				c.add(Calendar.DATE, 7);
				endDatetime = new Date(c.getTime().getTime());
				strStart = Datetimes.formatDate(startDatetime);
				strEnd = Datetimes.formatDate(endDatetime);
			}
		}
		List list = this.meetingRoomManager.getTotal(startDatetime, endDatetime, false);

		DataRecord dr = new DataRecord();
		String[] colNames = new String[4];
		colNames[0] = ResourceBundleUtil.getString(mRes, "mr.label.meetingroomname", new Object[0]);
		colNames[1] = ResourceBundleUtil.getString(mRes, "mr.label.nowmonth", new Object[0]);
		colNames[2] = ResourceBundleUtil.getString(mRes, "mr.label.total", new Object[0]);
		colNames[3] = ResourceBundleUtil.getString(mRes, "mr.label.from", new Object[0]) + strStart
				+ ResourceBundleUtil.getString(mRes, "mr.label.to", new Object[0]) + strEnd;
		dr.setColumnName(colNames);
		dr.setTitle(ResourceBundleUtil.getString(mRes, "mr.tab.meetingtotal", new Object[0]));
		dr.setSheetName(ResourceBundleUtil.getString(mRes, "mr.tab.meetingtotal", new Object[0]));
		if(list != null && list.size() > 0){
			DataRow[] datarow = new DataRow[list.size()];
			for(int i = 0; i < list.size(); i++){
				HashMap h = (HashMap) list.get(i);
				datarow[i] = new DataRow();
				datarow[i].addDataCell(((MeetingRoomRecord) h.get("MeetingRoomRecord")).getMeetingRoom().getName(), 1);
				datarow[i].addDataCell(String.valueOf(h.get("MonthTotal"))+ResourceBundleUtil.getString(mRes, "mr.label.hour", new Object[0]), 1);
				datarow[i].addDataCell(String.valueOf(h.get("AllTotal"))+ResourceBundleUtil.getString(mRes, "mr.label.hour", new Object[0]), 1);
				datarow[i].addDataCell(String.valueOf(h.get("SectionTotal"))+ResourceBundleUtil.getString(mRes, "mr.label.hour", new Object[0]), 1);
			}
			dr.addDataRow(datarow);
		}
		this.getFileToExcelManager().save(request, response, ResourceBundleUtil.getString(mRes, "mr.tab.meetingtotal", new Object[0]), new DataRecord[] { dr });
		return null;
	}

	/**
	 * 会议室使用记录查看列表
	 * 
	 * @param request
	 * @param response
	 * @return 转到listview.jsp页面
	 * @throws Exception
	 */
	public ModelAndView listView(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/listview");
		String[] meetingids = null;
		String meetingid = request.getParameter("ids");
		if(meetingid != null && !"".equals(meetingid)){
			meetingids = meetingid.split(" ");
		}
		String dayStr = request.getParameter("day");
		Date day = new Date();
		if(dayStr != null && dayStr.length() > 0){
			day = Datetimes.parseDate(dayStr);
		}
		Calendar c = Calendar.getInstance();
		c.setTime(day);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		Date startDatetime = c.getTime();
		c.add(Calendar.DATE, 1);
		Date endDatetime = c.getTime();
		User user = CurrentUser.get();
		List adminids = new ArrayList();
		List adminId = this.meetingRoomManager.getAllAdmins(user.getId());
		List list;
		if(meetingids==null){
			list = this.getMeetingRoomManager().getUseDetailsByDay(adminId, startDatetime, endDatetime, true);			
		}else{
			List<MeetingRoom> ids = new ArrayList<MeetingRoom>();
			for(String id : meetingids){
				ids.add(this.getMeetingRoomManager().getRoom(Long.valueOf(id)));
			}
			list = this.getMeetingRoomManager().getUseDetailsByDay(adminId, startDatetime, endDatetime,ids, true);			
		}
		LinkedList<Object> listSubmit=new LinkedList<Object>();
		if(list != null && list.size() > 0){
			Date nowDate=new Date();
			for(int i = 0; i < list.size(); i++){
				Object obj = list.get(i);
				if(obj instanceof MeetingRoomRecord){
					MeetingRoomRecord mrr = (MeetingRoomRecord) obj;
					if(mrr.getMeeting() != null){
						mrr.getMeeting().setCreateUserName(this.getOrgManager().getMemberById(mrr.getMeeting().getCreateUser()).getName());
					}
					if(nowDate.before(mrr.getEndDatetime())){
						listSubmit.addLast(obj);
					}
				}else if(obj instanceof MeetingRoomApp){
					MeetingRoomApp mra = (MeetingRoomApp) obj;
					if(mra.getMeeting() != null){
						mra.getMeeting().setCreateUserName(this.getOrgManager().getMemberById(mra.getMeeting().getCreateUser()).getName());
					}
					/**
					if(nowDate.before(mra.getEndDatetime())){
						listSubmit.addLast(obj);
					}**/
					listSubmit.addLast(obj);
				}
			}
		}
		mav.addObject("list", listSubmit);
		return mav;
	}

	/**
	 * 日历查看页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到viewbycalendar.jsp页面
	 * @throws Exception
	 */
	public ModelAndView viewByCalendar(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(year != null && year.length() > 0){
			c.set(Calendar.YEAR, Integer.parseInt(year));
		}else{
			year = String.valueOf(c.get(Calendar.YEAR));
		}
		if(month != null && month.length() > 0){
			c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		}else{
			month = String.valueOf(c.get(Calendar.MONTH) + 1);
		}
		c.set(Calendar.DATE, 1);
		Date startDatetime = c.getTime();
		c.add(Calendar.MONTH, 1);
		Date endDatetime = c.getTime();
		User user = CurrentUser.get();
		List adminId = this.meetingRoomManager.getAllAdmins(user.getId());
		String[] meetingids = null;
		String meetingid = request.getParameter("ids");
		if(meetingid != null && !"".equals(meetingid)){
			meetingids = meetingid.split(" ");
		} 
		List list;
		if(meetingids==null){
			list = this.getMeetingRoomManager().getUseDetailsByDay(adminId, startDatetime, endDatetime, false);			
		}else{
			List<MeetingRoom> ids = new ArrayList<MeetingRoom>();;
			for(String id : meetingids){
				ids.add(this.getMeetingRoomManager().getRoom(Long.valueOf(id)));
			}
			list = this.getMeetingRoomManager().getUseDetailsByDay(adminId, startDatetime, endDatetime,ids, false);			
		}
		StringBuffer str = new StringBuffer();
		Hashtable<String, Integer[]> h = new Hashtable<String, Integer[]>();
		Calendar cStart = Calendar.getInstance();
		cStart.setTime(startDatetime);
		Calendar cEnd = Calendar.getInstance();
		cEnd.setTime(endDatetime);
		if(list != null && list.size() > 0){
			for(; cStart.before(cEnd); cStart.add(Calendar.DATE, 1)){
				Calendar cDayEnd = Calendar.getInstance();
				cDayEnd.setTime(cStart.getTime());
				cDayEnd.add(Calendar.DATE, 1);
				for(int i = 0; i < list.size(); i++){
					Object obj = list.get(i);
					Date tempStartDatetime = null;
					Date tempEndDatetime = null;
					MtMeeting tempMeeting = null;
					if(obj instanceof MeetingRoomApp){
						MeetingRoomApp mra = (MeetingRoomApp) obj;
						tempStartDatetime = mra.getStartDatetime();
						tempEndDatetime = mra.getEndDatetime();
						tempMeeting = mra.getMeeting();
					}else if(obj instanceof MeetingRoomRecord){
						MeetingRoomRecord mrr = (MeetingRoomRecord) obj;
						tempStartDatetime = mrr.getStartDatetime();
						tempEndDatetime = mrr.getEndDatetime();
						tempMeeting = mrr.getMeeting();
					}
					if(tempMeeting != null){
						tempMeeting.setCreateUserName(this.getOrgManager().getMemberById(tempMeeting.getCreateUser()).getName());
					}
					Calendar cS = Calendar.getInstance();
					cS.setTime(tempStartDatetime);
					Calendar cE = Calendar.getInstance();
					cE.setTime(tempEndDatetime);
					if(((cS.after(cStart) || cS.equals(cStart)) && (cS.before(cDayEnd))) || ((cE.after(cStart)) && (cE.before(cDayEnd) || cE.equals(cDayEnd)))
							|| ((cS.before(cStart) || cS.equals(cStart)) && (cE.after(cDayEnd) || cE.equals(cDayEnd)))){
						String key = Datetimes.formatDate(cStart.getTime());
						if(h.containsKey(key)){
							Integer[] count = h.get(key);
							if(tempMeeting != null){
								count[0]++;
							}else{
								count[1]++;
							}
						}else{
							Integer[] count = new Integer[] { 0, 0 };
							if(tempMeeting != null){
								count[0]++;
							}else{
								count[1]++;
							}
							h.put(key, count);
						}
					}
				}
			}
			Enumeration<String> e = h.keys();
			while(e.hasMoreElements()){
				String key = e.nextElement();
				Integer[] count = h.get(key);
				String node = "document.getElementById(\"div_" + key + "\")";
				str.append("try{");
				str.append(node + ".style.display=\"\";\r\n");
				str.append(node + ".innerHTML=\""
						+ (count[0] != 0 ? (count[0] + ResourceBundleUtil.getString(mRes, "mr.label.gehuiyi", new Object[0]) + "<br/>") : "")
						+ (count[1] != 0 ? count[1] + ResourceBundleUtil.getString(mRes, "mr.label.geyuding", new Object[0]) : "") + "\";\r\n");
				str.append("}catch(E){}");
			}
		}
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/viewbycalendar");
		mav.addObject("year", year);
		mav.addObject("month", month);
		mav.addObject("initTitleScription", str.toString());
		//会议室默认查询范围
		mav.addObject("ids", meetingid);
		return mav;
	}

	/**
	 * 新建会议室页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到createadd.jsp页面
	 * @throws Exception
	 */
	public ModelAndView createAdd(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
//		if(!isAdmin){
//			return refreshWorkspace();
//		}
		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/createadd");
		String id = request.getParameter("id");
		String readOnly = request.getParameter("readOnly");
		if(id != null && id.length() > 0){
			try{
				MeetingRoom mr = this.meetingRoomManager.getRoom(Long.parseLong(id));
				mav.addObject("bean", mr);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if(readOnly != null && readOnly.length() > 0){
			mav.addObject("readOnly", "true");
		}
		return mav;
	}

	/**
	 * 申请会议室页面，弹出页面
	 * 
	 * @param request
	 * @param response
	 * @return 弹出createapp.jsp页面
	 * @throws Exception
	 */
	public ModelAndView createApp(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(user.getId());
		V3xOrgDepartment department = this.getOrgManager().getDepartmentById(v3xOrgMember.getOrgDepartmentId());
		Long id = Long.parseLong(request.getParameter("id"));
		MeetingRoom mr = this.getMeetingRoomManager().getRoom(id);

		ModelAndView mav = MavUtil.getModelAndViewInstance("meetingroom/createapp");
		if(this.meetingRoomManager.checkAdmin()){
			mav.addObject("meetingRoomAdmin", true) ;
		}else {
			mav.addObject("meetingRoomAdmin", false) ;
		}
		
		mav.addObject("bean", mr);
		mav.addObject("user", v3xOrgMember);
		mav.addObject("v3xOrgDepartment", department);
		return mav;
	}

	/**
	 * 会议室审批页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到createperm.jsp页面或者弹出createpermopen.jsp页面
	 * @throws Exception
	 */
	public ModelAndView createPerm(HttpServletRequest request, HttpServletResponse response) throws Exception{
//		boolean isAdmin = this.getMeetingRoomManager().checkAdmin();
//		if(!isAdmin){
//			return refreshWorkspace();
//		}
		Long id = Long.parseLong(request.getParameter("id"));
		String readOnly = request.getParameter("readOnly");
		String openWin = request.getParameter("openWin");
		MeetingRoomPerm mrp = this.getMeetingRoomManager().getRoomPerm(id);
		String view = "meetingroom/createperm";
		if(openWin != null && openWin.length() > 0){
			view = "meetingroom/createpermopen";
		}
		if(mrp == null){
			String msg = ResourceBundleUtil.getString(mRes, "mr.alert.appdeleted", new Object[0]);
			msg = "alert(\""+msg+"\");";
			if(openWin != null && openWin.length() > 0){
				msg = msg + "window.close();";
			}else{
				msg = msg + "parent.document.location.reload();";
			}
			rendJavaScript(response, msg);
			return null;
		}
		ModelAndView mav = MavUtil.getModelAndViewInstance(view);
		User user = CurrentUser.get();
		List<Long> departId = this.adminManagerCAP.getAdminManageDepartments(user.getId(), user.getAccountId(), "____1");
		Object[] ob = this.adminManagerCAP.getMemberDepProxy(mrp.getMeetingRoomApp().getV3xOrgMember(), user.getAccountId(), user.getId(), "____1", departId);
		boolean proxy = Boolean.parseBoolean(ob[1].toString());
		if(proxy){
			mav.addObject("departmentName", ob[0]);
		}else{
			mav.addObject("departmentName", mrp.getMeetingRoomApp().getV3xOrgDepartment().getFullPathName());
		}
		mav.addObject("proxy", ob[1]);
		mav.addObject("bean", mrp);
		if(readOnly != null && readOnly.length() > 0){
			mav.addObject("readOnly", "true");
		}
		return mav;
	}

	/**
	 * 执行新建会议室操作
	 * 
	 * @param request
	 * @param response
	 * @return null,刷新add.jsp页面
	 * @throws Exception
	 */
	public ModelAndView execAdd(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		if(!this.getMeetingRoomManager().checkMeetingRoomName((id!=null&&id.length()>0)?Long.parseLong(id):null, name)){
			String msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.namesame", new Object[0]) + "\");";
			rendJavaScript(response, msg);
			return null;
		}
		MeetingRoom mr = null;
		if(id != null && id.length() > 0){
			mr = this.meetingRoomManager.getRoom(Long.parseLong(id));
			mr.setModifyDatetime(new Date());
		}else{
			mr = new MeetingRoom();
			mr.setId(UUIDLong.longUUID());
			mr.setCreateDatetime(new Date());
			mr.setDelFlag(com.seeyon.v3x.meetingroom.util.Constants.DelFlag_No);
			mr.setAccountId(user.getAccountId());
		}
		mr.setDescription(request.getParameter("description"));
		mr.setName(request.getParameter("name"));
		if(request.getParameter("needApp") != null && request.getParameter("needApp").length() > 0){
			mr.setNeedApp(com.seeyon.v3x.meetingroom.util.Constants.Type_MeetingRoom_NeedApp);
		}else{
			mr.setNeedApp(com.seeyon.v3x.meetingroom.util.Constants.Type_MeetingRoom_NoNeedApp);
		}
		mr.setV3xOrgMember(this.orgManager.getMemberById(user.getId()));
		mr.setPlace(request.getParameter("place"));
		mr.setSeatCount(Integer.parseInt(request.getParameter("seatCount")));
		mr.setStatus(Integer.parseInt(request.getParameter("status")));
		String msg = "";
		try{
			if(id != null && id.length() > 0){
				this.meetingRoomManager.updateRoom(mr);
				if(mr.getStatus() == com.seeyon.v3x.meetingroom.util.Constants.Status_MeetingRoom_Stop){
					this.getMeetingRoomManager().sendMeetingRoomStopMsg(mr);
				}
			}else{
				this.meetingRoomManager.addRoom(mr);
			}
			msg = (new StringBuilder("alert(\"")).append(ResourceBundleUtil.getString(mRes, "mr.alert.success", new Object[0])).append(
					"\");parent.parent.location.reload();").toString();
		}catch(Exception ex){
			ex.printStackTrace();
			msg = "alert(\"" + ex.getMessage() + "\");parent.parent.location.reload();";
		}
		rendJavaScript(response, msg);
		return null;
	}

	/**
	 * 停用会议室时,对未到期的预定和会议发通知
	 * 
	 * @param request
	 * @param response
	 * @return null,提交父页面的修改Form
	 * @throws Exception
	 */
	public ModelAndView checkStop(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String id = request.getParameter("id");
		MeetingRoom mr = this.getMeetingRoomManager().getRoom(Long.parseLong(id));
		String msg = null;
		if(this.getMeetingRoomManager().checkUsed(mr)){
			msg = "parent.document.myForm.submit();";
		}else{
			msg = "if(confirm(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.confirmstop", new Object[0]) + "\")){parent.document.myForm.submit();}";
		}
		rendJavaScript(response, msg);
		return null;
	}

	/**
	 * 执行会议室删除操作
	 * 
	 * @param request
	 * @param response
	 * @return null,刷新add.jsp页面
	 * @throws Exception
	 */
	public ModelAndView execDel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] id = request.getParameterValues("id");
		String msg = "";
		List<MeetingRoom> list = new ArrayList<MeetingRoom>();
		if(id != null && id.length > 0){
			for(int i = 0; i < id.length; i++){
				MeetingRoom mr = this.meetingRoomManager.getRoom(Long.parseLong(id[i]));
				if(!this.getMeetingRoomManager().checkUsed(mr)){
					msg = ResourceBundleUtil.getString(mRes, "mr.alert.cannotdel", new Object[0]);
					msg = "alert(\"" + msg + "\");parent.document.location.reload();";
					rendJavaScript(response, msg);
					return null;
				}
				list.add(mr);
			}
			for(MeetingRoom mr : list){
				mr.setDelFlag(com.seeyon.v3x.meetingroom.util.Constants.DelFlag_Yes);
				this.meetingRoomManager.updateRoom(mr);
			}
			String alert = ResourceBundleUtil.getString(mRes, "mr.alert.success", new Object[0]);
			msg += "alert(\"" + alert + "\");";
		}
		msg += "parent.document.location.reload();";
		rendJavaScript(response, msg);
		return null;
	}

	/**
	 * 执行会议室申请操作
	 * 
	 * @param request
	 * @param response
	 * @return null，关闭弹出窗口
	 * @throws Exception
	 */
	public ModelAndView execApp(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		String msg = "";
		Long id = Long.parseLong(request.getParameter("id"));
		Long perId = Long.parseLong(request.getParameter("perId"));
		Long departmentId = Long.parseLong(request.getParameter("departmentId"));
		String startDatetime = request.getParameter("startDatetime");
		String endDatetime = request.getParameter("endDatetime");
		String description = request.getParameter("description");
		Date ds = Datetimes.parseDatetimeWithoutSecond(startDatetime);
		Date de = Datetimes.parseDatetimeWithoutSecond(endDatetime);
		MeetingRoom mr = this.getMeetingRoomManager().getRoom(id);
		if(mr.getDelFlag() == com.seeyon.v3x.meetingroom.util.Constants.DelFlag_Yes){
			msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.bedeleted", new Object[0])
					+ "\");parent.window.returnValue=\"1\";parent.parent.document.location.reload();";
		}else if(mr.getNeedApp() == com.seeyon.v3x.meetingroom.util.Constants.Type_MeetingRoom_NoNeedApp){
			msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.statuchanged", new Object[0])
					+ "\");parent.window.returnValue=\"1\";parent.parent.document.location.reload();";
		}else if(mr.getStatus() == com.seeyon.v3x.meetingroom.util.Constants.Status_MeetingRoom_Stop){
			msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.stopped", new Object[0])
					+ "\");parent.window.returnValue=\"1\";parent.parent.document.location.reload();";
		}else if(this.getMeetingRoomManager().checkApp(id, ds, de)){
			MeetingRoomApp mra = new MeetingRoomApp();
			mra.setId(UUIDLong.longUUID());
			mra.setV3xOrgDepartment(this.orgManager.getDepartmentById(departmentId));
			mra.setAppDatetime(new Date());
			mra.setDescription(description);
			mra.setStartDatetime(ds);
			mra.setEndDatetime(de);
			mra.setMeetingRoom(this.getMeetingRoomManager().getRoom(id));
			mra.setV3xOrgMember(this.orgManager.getMemberById(perId));
			mra.setStatus(com.seeyon.v3x.meetingroom.util.Constants.Status_App_Wait);
			this.getMeetingRoomManager().addRoomApp(mra);
			msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.success", new Object[0]) + "\");parent.parent.document.location.reload();";
		}else{
			msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.cannotapp", new Object[0]) + "\");";
		}
		rendJavaScript(response, msg);
		return null;
	}

	/**
	 * 执行会议室审批操作
	 * 
	 * @param request
	 * @param response
	 * @return null，刷新perm.jsp页面
	 * @throws Exception
	 */
	public ModelAndView execPerm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long id = Long.parseLong(request.getParameter("id"));
		Integer permStatus = Integer.parseInt(request.getParameter("permStatus"));
		String description = request.getParameter("description");
		String openWin = request.getParameter("openWin");
		try{
			this.getMeetingRoomManager().execPerm(id, permStatus, description);
			OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, id);
		}catch(Exception ex){
			String msg = ResourceBundleUtil.getString(mRes, "mr.alert.appdeleted", new Object[0]);
			msg = "alert(\""+msg+"\");\n";
			if(openWin != null && openWin.length() > 0){
				msg = msg + "parent.window.returnValue = \"true\";\nparent.window.close();";
			}else{
				msg = msg + "parent.document.location.reload();";
			}
			rendJavaScript(response, msg);
			return null;
		}
		String script = "parent.document.location.reload();";
		if(openWin != null && openWin.length() > 0){
			script = "parent.window.returnValue = \"true\";\nparent.window.close();";
		}
		String msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.success", new Object[0]) + "\");\n" + script;
		rendJavaScript(response, msg);
		return null;
	}

	/**
	 * 执行审批清除操作
	 * 
	 * @param request
	 * @param response
	 * @return null，刷新perm.jsp页面
	 * @throws Exception
	 */
	public ModelAndView execClearPerm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] id = request.getParameterValues("id");
		if(id != null && id.length > 0){
			List list = new ArrayList();
			for(int i = 0; i < id.length; i++){
				list.add(Long.parseLong(id[i]));
			}
			this.getMeetingRoomManager().clearPerm(list);
			String msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.success", new Object[0]) + "\");parent.document.location.reload();";
			rendJavaScript(response, msg);
		}
		return null;
	}

	/**
	 * 执行会议室申请撤销操作
	 * 
	 * @param request
	 * @param response
	 * @return null，刷新cancal.jsp页面
	 * @throws Exception
	 */
	public ModelAndView execCancel(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String[] id = request.getParameterValues("id");
		if(id != null && id.length > 0){
			List<Long> list = new ArrayList<Long>();
			for(int i = 0; i < id.length; i++){
				list.add(Long.parseLong(id[i]));
				OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, Long.parseLong(id[i]));
			}
			try{
				this.getMeetingRoomManager().execCancel(list);
			}catch(Exception ex){
			}
		}
		String msg = "alert(\"" + ResourceBundleUtil.getString(mRes, "mr.alert.success", new Object[0]) + "\");parent.document.location.reload();";
		rendJavaScript(response, msg);
		return null;
	}

	/**
	 * @return 返回MeetingRoomManager对象
	 */
	public MeetingRoomManager getMeetingRoomManager(){
		return meetingRoomManager;
	}

	/**
	 * 注入MeetingRoomManager对象
	 * 
	 * @param meetingRoomManager
	 */
	public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager){
		this.meetingRoomManager = meetingRoomManager;
	}

	/**
	 * @return 返回OrgManager对象
	 */
	public OrgManager getOrgManager(){
		return orgManager;
	}

	/**
	 * 注入OrgManager对象
	 * 
	 * @param orgManager
	 */
	public void setOrgManager(OrgManager orgManager){
		this.orgManager = orgManager;
	}

	/**
	 * @return 返回FileToExcelManager对象
	 */
	public FileToExcelManager getFileToExcelManager(){
		return fileToExcelManager;
	}

	/**
	 * 注入FileToExcelMananger对象
	 * 
	 * @param fileToExcelManager
	 */
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager){
		this.fileToExcelManager = fileToExcelManager;
	}

	public void setOfficeApplyManagerCAP(OfficeApplyManagerCAP officeApplyManagerCAP) {
		this.officeApplyManagerCAP = officeApplyManagerCAP;
	}
}
