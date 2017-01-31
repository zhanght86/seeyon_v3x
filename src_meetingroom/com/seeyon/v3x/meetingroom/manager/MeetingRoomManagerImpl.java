package com.seeyon.v3x.meetingroom.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.cap.office.common.OfficeModelTypeCAP;
import com.seeyon.cap.office.common.manager.OfficeApplyManagerCAP;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;
import com.seeyon.v3x.meetingroom.dao.MeetingRoomDao;
import com.seeyon.v3x.meetingroom.domain.MeetingRoom;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomApp;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomPerm;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomRecord;
import com.seeyon.v3x.office.common.OfficeHelper;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.UniqueList;

public class MeetingRoomManagerImpl implements MeetingRoomManager{
	private final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(MeetingRoomManagerImpl.class);
	private static String mRes = "com.seeyon.v3x.meetingroom.resources.i18n.MeetingRoomResources";

	private MeetingRoomDao meetingRoomDao;

	private OrgManager orgManager;

	private UserMessageManager userMessageManager;

	private MtMeetingManager mtMeetingManager;

	private AffairManager affairManager;
	
	private OfficeApplyManagerCAP officeApplyManagerCAP;
	
	

	public OfficeApplyManagerCAP getOfficeApplyManagerCAP() {
		return officeApplyManagerCAP;
	}

	public void setOfficeApplyManagerCAP(OfficeApplyManagerCAP officeApplyManagerCAP) {
		this.officeApplyManagerCAP = officeApplyManagerCAP;
	}

	public MeetingRoomDao getMeetingRoomDao(){
		return meetingRoomDao;
	}

	public void setMeetingRoomDao(MeetingRoomDao meetingRoomDao){
		this.meetingRoomDao = meetingRoomDao;
	}

	public OrgManager getOrgManager(){
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager){
		this.orgManager = orgManager;
	}

	public void addRoom(MeetingRoom mr){
		this.meetingRoomDao.save(mr);
	}

	public UserMessageManager getUserMessageManager(){
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager){
		this.userMessageManager = userMessageManager;
	}

	public MtMeetingManager getMtMeetingManager(){
		return mtMeetingManager;
	}

	public void setMtMeetingManager(MtMeetingManager mtMeetingManager){
		this.mtMeetingManager = mtMeetingManager;
	}

	public AffairManager getAffairManager(){
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager){
		this.affairManager = affairManager;
	}

	@SuppressWarnings("unchecked")
	public void addRoomApp(MeetingRoomApp mra) throws Exception {
		User user = CurrentUser.get();
		MeetingRoomPerm mrp = new MeetingRoomPerm();
		mrp.setAppId(mra.getId());
		mrp.setDelFlag(com.seeyon.v3x.meetingroom.util.Constants.DelFlag_No);
		mrp.setIsAllowed(mra.getStatus());
		mra.setMeetingRoomPerm(mrp);
		this.meetingRoomDao.save(mra);
		this.meetingRoomDao.save(mrp);
		if (mra.getMeetingRoom().getV3xOrgMember().getId().equals(user.getId())) {
			this.execPerm(mra.getId(), com.seeyon.v3x.meetingroom.util.Constants.Status_App_Yes, "");
			if (!mra.getV3xOrgMember().getId().equals(user.getId())) {
				// 发送管理员为您预定消息
				MessageContent content = MessageContent.get("mr.label.appforyou", new Object[] { mra.getMeetingRoom().getV3xOrgMember().getName(), mra.getMeetingRoom().getName() });
				content.setResource(mRes);
				List auth = new ArrayList();
				auth.add(mra.getV3xOrgMember().getId());
				Collection receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth);
				this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.meetingroom, user.getId(), receivers);
			}
		} else {
			// 审批人首页增加待办
			OfficeHelper.addPendingAffair(mra.getMeetingRoom().getName(), mra, ApplicationSubCategoryEnum.office_meetingroom);
			
			// 发送请管理员审批消息
			MessageContent content = MessageContent.get("mr.label.pleaseperm", new Object[] { mra.getV3xOrgMember().getName(), mra.getMeetingRoom().getName(), mra.getDescription() });
			content.setResource(mRes);
			List auth = new ArrayList();
			auth.add(mra.getMeetingRoom().getV3xOrgMember().getId());
			Collection receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth, "message.link.office.meetingroom", new Object[] { String.valueOf(mra.getId()) });
			this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.meetingroom, mra.getV3xOrgMember().getId(), receivers);
		}
	}

	public List getMeetingRooms(V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer[] seatCount, Integer needApp, Integer status,
			Integer delFlag, Boolean isPage) throws Exception{
		List list = this.meetingRoomDao.find(v3xOrgMember, accountId, name, place, seatCount, needApp, status, delFlag, isPage);
		return list;
	}

	public void updateRoom(MeetingRoom mr){
		this.meetingRoomDao.update(mr);
	}

	public MeetingRoom getRoom(Long id) throws Exception{
		return this.meetingRoomDao.loadMeetingRoom(id);
	}

	public MeetingRoomPerm getRoomPerm(Long id) throws Exception{
		return this.getMeetingRoomDao().loadMeetingRoomPerm(id);
	}

	public MeetingRoomApp getRoomApp(Long id) throws Exception{
		return this.getMeetingRoomDao().loadMeetingRoomApp(id);
	}

	public List MeetingRoomsForApp(String name, Integer[] seatCount,List adminIds, Boolean isPage){
		if(adminIds != null && adminIds.size() > 0){
			return this.getMeetingRoomDao().findApp(adminIds, name, seatCount, isPage);
		}else{
			return null;
		}
	}

	public boolean checkApp(Long id, Date startDatetime, Date endDatetime) throws Exception{
		MeetingRoom mr = this.getRoom(id);
		int count = this.getMeetingRoomDao().checkAppCount(mr, startDatetime, endDatetime);
		if(count == 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean checkMeetingRoomName(Long id, String name) throws Exception{
		return this.getMeetingRoomDao().checkMeetingRoomName(id, name);
	}

	public boolean checkUsed(MeetingRoom mr) throws Exception{
		return this.getMeetingRoomDao().checkUsed(mr);
	}

	public void sendMeetingRoomStopMsg(MeetingRoom mr) throws Exception{
		List list = this.getMeetingRoomDao().getUsedList(mr);
		if(list != null && list.size() > 0){
			for(Object obj : list){
				if(obj instanceof MeetingRoomApp){
					MeetingRoomApp mra = (MeetingRoomApp) obj;
					MessageContent content = MessageContent.get("mr.alert.bestopped",new Object[]{mra.getMeetingRoom().getName()});
					content.setResource(mRes);
					List auth = new ArrayList();
					auth.add(mra.getV3xOrgMember().getId());
					java.util.Collection receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth);
					this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.meetingroom, mra.getV3xOrgMember().getId(), receivers);
				}else if(obj instanceof MeetingRoomRecord){
					MeetingRoomRecord mrr = (MeetingRoomRecord) obj;
					MessageContent content = MessageContent.get("mr.alert.bestopped",new Object[]{mrr.getMeetingRoom().getName()});
					content.setResource(mRes);
					List auth = new ArrayList();
					auth.add(mrr.getMeeting().getCreateUser());
					java.util.Collection receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth);
					this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.meetingroom, mrr.getMeetingRoom().getV3xOrgMember().getId(),
							receivers);
				}
			}
		}
	}

	public List getMeetingRoomsForPerm(Long meetingRoomId, Long perId, Integer isAllowed) throws Exception{
		V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(CurrentUser.get().getId());
		MeetingRoom mr = null;
		if(meetingRoomId != null){
			mr = this.getRoom(meetingRoomId);
		}
		V3xOrgMember appMember = null;
		if(perId != null){
			appMember = this.getOrgManager().getMemberById(perId);
		}
		List list = this.meetingRoomDao.findForPerm(v3xOrgMember, mr, appMember, isAllowed);
		return list;
	}

	public void execPerm(Long id, Integer isAllowed, String description) throws Exception{
		MeetingRoomApp mra = this.getRoomApp(id);
		if(mra == null){
			throw new Exception();
		}
		MeetingRoomPerm mrp = mra.getMeetingRoomPerm();
		mra.setStatus(isAllowed);
		mrp.setDescription(description);
		mrp.setIsAllowed(isAllowed);
		mrp.setProDatetime(new Date());
		this.getMeetingRoomDao().update(mra);
		this.getMeetingRoomDao().update(mrp);
		String allowedStr = isAllowed == com.seeyon.v3x.meetingroom.util.Constants.Status_App_Yes ? "mr.alert.permok" : "mr.alert.permno";
		MessageContent content = MessageContent.get(allowedStr, new Object[]{mra.getMeetingRoom().getName(),description});
		content.setResource(mRes);
		List auth = new ArrayList();
		auth.add(mra.getV3xOrgMember().getId());
		java.util.Collection receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth);
		this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.meetingroom, mra.getMeetingRoom().getV3xOrgMember().getId(), receivers);
		if(isAllowed == com.seeyon.v3x.meetingroom.util.Constants.Status_App_Yes){
			List list = this.getMeetingRoomDao().checkAppPermList(mra.getMeetingRoom(), mra.getStartDatetime(), mra.getEndDatetime());
			if(list != null && list.size() > 0){
				for(int i = 0; i < list.size(); i++){
					MeetingRoomApp pMra = (MeetingRoomApp) list.get(i);
					pMra.setStatus(com.seeyon.v3x.meetingroom.util.Constants.Status_App_No);
					MeetingRoomPerm pMrp = pMra.getMeetingRoomPerm();
					pMrp.setIsAllowed(pMra.getStatus());
					pMrp.setProDatetime(new Date());
					this.getMeetingRoomDao().update(pMra);
					this.getMeetingRoomDao().update(pMrp);
					OfficeHelper.delPendingAffair(ApplicationCategoryEnum.office, pMra.getId());
					MessageContent mContent = MessageContent.get("mr.alert.permno", new Object[]{pMra.getMeetingRoom().getName(),description});
					mContent.setResource(mRes);
					List mAuth = new ArrayList();
					mAuth.add(pMra.getV3xOrgMember().getId());
					java.util.Collection mReceivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), mAuth);
					this.userMessageManager.sendSystemMessage(mContent, ApplicationCategoryEnum.meetingroom, pMra.getMeetingRoom().getV3xOrgMember().getId(), mReceivers);
				}
			}
		}
	}
	public List getUseDetailsByDay(Date startDatetime, Date endDatetime, boolean pageFlag){
		List v3xOrgMembers = this.getMyAdmin();
		if(v3xOrgMembers != null && v3xOrgMembers.size() > 0){
			return this.getMeetingRoomDao().findUseDetailsByDay(v3xOrgMembers, startDatetime, endDatetime, pageFlag);
		}else{
			return null;
		}
	}
	public List getUseDetailsByDay(List adminIds, Date startDatetime, Date endDatetime, boolean pageFlag){
		if(adminIds != null && adminIds.size() > 0){
			return this.getMeetingRoomDao().findUseDetailsByDay(adminIds, startDatetime, endDatetime, pageFlag);
		}else{
			return null;
		}
	}
	public List getUseDetailsByDay(List adminIds, Date startDatetime, Date endDatetime,List meetingRoom, boolean pageFlag){
		if(adminIds != null && adminIds.size() > 0){
			return this.getMeetingRoomDao().findUseDetailsByDay(adminIds, startDatetime, endDatetime,meetingRoom, pageFlag);
		}else{
			return null;
		}
	}

	public List getCancelList(Long mrId, Integer isAllowed, Long perId) throws Exception{
		User user = CurrentUser.get();
		return this.checkAdmin() ? this.meetingRoomDao.getCanCanceledMeetingRoomApps4Admin(user.getId(), mrId, isAllowed, perId) :
			   					   this.meetingRoomDao.getCanCanceledMeetingRoomApps4User(user.getId(), mrId, isAllowed);
		
		/*MeetingRoom mr = null;
		V3xOrgMember appMember = null;
		if(mrId != null){
			mr = this.getRoom(mrId);
		}
		if(perId != null){
			appMember = this.getOrgManager().getMemberById(perId);
		}
		
		boolean isCurrentUserAdmin = this.checkAdmin();
		if(this.checkAdmin()){
			try{
				V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(user.getId());
				list = this.getMeetingRoomDao().getCancals(v3xOrgMember, appMember, mr, isAllowed);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}else{
			try{
				V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(user.getId());
				list = this.getMeetingRoomDao().getCancals(null, v3xOrgMember, mr, isAllowed);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return list;*/
	}

	public void execCancel(List list) throws Exception{
		List mList = new ArrayList();
		User user = CurrentUser.get();
		for(int i = 0; i < list.size(); i++){
			Long id = (Long) list.get(i);
			MeetingRoomApp mra = this.getMeetingRoomDao().loadMeetingRoomApp(id);
			mList.add(mra);
			MessageContent content = null;
			List auth = null;
			java.util.Collection receivers = null;
			Long senderId = null;
			if(!mra.getV3xOrgMember().getId().equals(user.getId())){
				content = MessageContent.get("mr.label.clearbymgr",new Object[]{mra.getMeetingRoom().getName()});
				auth = new ArrayList();
				auth.add(mra.getV3xOrgMember().getId());
				receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth);
				senderId = mra.getMeetingRoom().getV3xOrgMember().getId();
				//this.getAffairManager().deleteByObject(ApplicationCategoryEnum.meetingroom, mra.getId());
			} else {
				content = MessageContent.get("mr.label.clearbyuser",new Object[]{mra.getV3xOrgMember().getName(),mra.getMeetingRoom().getName()});
				auth = new ArrayList();
				auth.add(mra.getMeetingRoom().getV3xOrgMember().getId());
				receivers = MessageReceiver.get(new Long(ApplicationCategoryEnum.meetingroom.getKey()), auth);
				senderId = mra.getV3xOrgMember().getId();
			}
			content.setResource(mRes);
			this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.meetingroom, senderId, receivers);
		}
		this.getMeetingRoomDao().execCancel(mList);
	}

	/**
	 * 删除会议接口
	 * 
	 * @throws Exception
	 */
	public void execCancelMeeting(Long meetingId) throws Exception{
		MtMeeting meeting = this.getMtMeetingManager().getById(meetingId);
		this.getMeetingRoomDao().execCancelMeeting(meeting);
	}
    /**
     * 删除会议记录和更新会议室申请信息，并不删除会议室申请
     * 
     * @throws Exception
     */
    public void execCancelMeetingRec(Long meetingId) throws Exception{
        MtMeeting meeting = this.getMtMeetingManager().getById(meetingId);
        this.getMeetingRoomDao().execCancelMeetingRec(meeting);
    }
	public void clearPerm(List id){
		this.getMeetingRoomDao().clearPerm(id);
	}

	public boolean checkAdmin(){
		User user = CurrentUser.get();
		return this.getMeetingRoomDao().checkAdmin(user.getId());
	}

	public List getTotal(Date startDatetime, Date endDatetime, Boolean isPage){
		User user = CurrentUser.get();
		List list = null;
		try{
			V3xOrgMember v3xOrgMember = this.getOrgManager().getMemberById(user.getId());
			list = this.getMeetingRoomDao().getTotal(v3xOrgMember, startDatetime, endDatetime, isPage);
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return list;
	}

	/**
	 * 获得我部门的会议室管理员范围
	 * 
	 * @return
	 */
	private List getMyAdmin(){
		List list = new ArrayList();
		try{
			list = this.officeApplyManagerCAP.getOfficeApplyList(OfficeModelTypeCAP.meeting_type, CurrentUser.get());
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return list;
	}

	private List getMyAdmin(V3xOrgMember v3xOrgMember){
		return this.getMyAdmin();
	}

	/**
	 * 取得当前人员的所有上级部门（包括当前部门，单位以及兼职部门）
	 */
	private List<Long> getAllParentDepartment(Long id) {
		User user = CurrentUser.get();
		List<Long> list = new UniqueList<Long>();
		try {
			V3xOrgMember m = orgManager.getMemberById(user.getId());
			List<Long> deptList = new UniqueList<Long>();
			deptList.add(m.getOrgDepartmentId());
			list.add(m.getOrgDepartmentId());
			List<V3xOrgAccount> concurrentAccounts = orgManager.getConcurrentAccounts(user.getId());
			if (concurrentAccounts != null && concurrentAccounts.size() > 0 ) {
				for (V3xOrgAccount account : concurrentAccounts) {
					if (account == null) {
						continue;
					}
					List<V3xOrgDepartment> allDepartments = orgManager.getAllDepartments(account.getId());
					if (allDepartments != null && allDepartments.size() > 0){
						for (V3xOrgDepartment dep  : allDepartments) {
							if (dep != null) {
								deptList.add(dep.getId());
								list.add(dep.getId());
							}
						}
					}
				}
			}
			
			Map<Long, List<ConcurrentPost>> concurent = orgManager.getConcurentPostsByMemberId(user.getLoginAccount(), user.getId());
			if (concurent != null && !concurent.isEmpty()) {
				for (Long deptId : concurent.keySet()) {
					deptList.add(deptId);
					list.add(deptId);
				}
			}

			for (Long deptId : deptList) {
				V3xOrgDepartment d = orgManager.getDepartmentById(deptId);
				while (!d.getParentPath().equals("0")) {
					d = orgManager.getDepartmentByPath(d.getParentPath());
					list.add(d.getId());
				}
				list.add(d.getOrgAccountId());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return list;
	}
	
	public List getAllAdmins(Long memeberid) {
		V3xOrgMember v3xOrgMember = null;
		try{
			v3xOrgMember = this.getOrgManager().getMemberById(memeberid);
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
		return this.getMyAdmin(v3xOrgMember);
	}

	/**
	 * 根据发起人，开始时间,结束时间，获取所有可用的不用申请的会议室和审批通过的会议室
	 * 
	 * @param v3xOrgMember
	 *            发起人
	 * @param startDatetime
	 *            会议开始时间
	 * @param endDatetime
	 *            会议结束时间
	 * @return List中判断如果是com.seeyon.v3x.meetingroom.domain.MeetingRoom对象，则是不用申请的会议室
	 *         如果是com.seeyon.v3x.meetingroom.domain.MeetingRoomApp对象,则是审批通过的申请，MeetingRoomApp中包含MeetingRoom对象
	 */
	public String[][] getMeetingRoomForMeeting(Long member, Long startDatetime, Long endDatetime){
		V3xOrgMember v3xOrgMember = null;
		try{
			v3xOrgMember = this.getOrgManager().getMemberById(member);
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
		List v3xOrgMembers = this.getMyAdmin(v3xOrgMember);
		String[][] ids = null;
		if(v3xOrgMembers == null || v3xOrgMembers.size() == 0){
			ids = new String[0][3];
			return ids;
		}
		List list = this.getMeetingRoomDao().getMeetingRoomForMeeting(v3xOrgMembers, v3xOrgMember, new Date(startDatetime), new Date(endDatetime));
		if(list != null && list.size() > 0){
			ids = new String[list.size()][3];
			for(int i = 0; i < list.size(); i++){
				Object obj = list.get(i);
				if(obj instanceof MeetingRoom){
					MeetingRoom mr = (MeetingRoom) obj;
					ids[i] = new String[5];
					ids[i][0] = String.valueOf(mr.getId());
					ids[i][1] = "";
					ids[i][2] = mr.getName();
					ids[i][3] = "";
					ids[i][4] = "";
				}else if(obj instanceof MeetingRoomApp){
					MeetingRoomApp mra = (MeetingRoomApp) obj;
					ids[i] = new String[5];
					ids[i][0] = String.valueOf(mra.getMeetingRoom().getId());
					ids[i][1] = String.valueOf(mra.getId());
					ids[i][2] = mra.getMeetingRoom().getName();
					ids[i][3] = Datetimes.formatDatetimeWithoutSecond(mra.getStartDatetime());
					ids[i][4] = Datetimes.formatDatetimeWithoutSecond(mra.getEndDatetime());
				}
			}
		}
		return ids;
	}

	/**
	 * 发起会议时验证会议室是否可用
	 * 
	 * @param v3xOrgMember
	 *            发起人
	 * @param meetingRoomId
	 *            选择的会议室Id
	 * @param meetingRoomAppId
	 *            会议室申请Id，对应MeetingRoomApp.Id,如果会议室是不用申请的则这个参数为null;
	 * @param startDatetime
	 *            会议开始时间
	 * @param endDatetime
	 *            会议结束时间
	 * @return true验证成功，false验证失败，验证标准，会议开始时间到结束时间包含在申请的会议室开始使用时间到结束时间范围内， 不用申请的会议室在会议开始时间到结束时间范围内没有被占用
	 */
	public String checkMeetingRoomForMeeting(Long v3xOrgMember, Long meetingRoomId, Long meetingRoomAppId, Long meetingId, Long startDatetime, Long endDatetime)
			throws Exception{
		MeetingRoom mr = null;
		try{
			mr = this.getRoom(meetingRoomId);
		}catch(Exception ex){
			log.error(ex.getMessage(), ex);
		}
		if(mr == null || mr.getDelFlag() == com.seeyon.v3x.meetingroom.util.Constants.DelFlag_Yes){
			return "delete";
		}
		MeetingRoomApp mra = null;
		if(meetingRoomAppId != null && meetingRoomAppId != 0){
			mra = this.getRoomApp(meetingRoomAppId);
		}
		MtMeeting meeting = null;
		if(meetingId != null){
			meeting = mtMeetingManager.getByMtId(meetingId);
		}
		String flag = this.getMeetingRoomDao().checkMeetingRoomForMeeting(this.getOrgManager().getMemberById(v3xOrgMember), mr, mra, meeting, new Date(startDatetime),
				new Date(endDatetime));
		return flag;
	}

	/**
	 * 发起会议成功后，提交到会议室管理，使会议室和会议关联起来
	 * 
	 * @param meeting
	 *            会议Id
	 * @param meetingRoomId
	 *            会议室Id
	 * @param meetingRoomAppId
	 *            如果是需要审批的会议室要指定会议室申请Id，否则为null;对应com.seeyon.v3x.meetingroom.domain.MeetingRoomApp的Id。
	 */
	public void execMeeting(Long meeting, Long meetingRoomId, Long meetingRoomAppId, Long startDatetime, Long endDatetime) throws Exception{
		MeetingRoom mr = this.getRoom(meetingRoomId);
		MeetingRoomApp mra = null;
		if(meetingRoomAppId != null){
			mra = this.getRoomApp(meetingRoomAppId);
		}
		MtMeeting mtMeeting = this.getMtMeetingManager().getById(meeting);
		this.getMeetingRoomDao().execMeeting(mtMeeting, mr, mra, new Date(startDatetime), new Date(endDatetime));
	}

	public String[] getByMeeting(MtMeeting meeting){
		return this.getMeetingRoomDao().getByMeeting(meeting);
	}
	
	public void updateMeetingRoomMangerBatch(long adminIdLong, long admin_newLong, User user) {
		this.updateMeetingRoomMangerBatch(adminIdLong, admin_newLong, user, true);
	}
	
	public void updateMeetingRoomMangerBatch(long adminIdLong, long admin_newLong, User user,boolean fromFlag) {
		meetingRoomDao.updateBookMangerBatch(adminIdLong,admin_newLong,user,fromFlag);

	}
}
