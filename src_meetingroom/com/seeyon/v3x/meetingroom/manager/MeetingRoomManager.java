package com.seeyon.v3x.meetingroom.manager;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meetingroom.domain.*;

import java.util.*;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 *
 */
public interface MeetingRoomManager{
	
	/**
	 * 添加会议室
	 * @param mr 会议室
	 */
	public void addRoom(MeetingRoom mr);
	
	/**
	 * 添加会议室申请
	 * @param mra 会议室申请
	 * @throws Exception
	 */
	public void addRoomApp(MeetingRoomApp mra)throws Exception;
	
	/**
	 * 根据Id取会议室
	 * @param id 数据库主键
	 * @return MeetingRoom
	 * @throws Exception
	 */
	public MeetingRoom getRoom(Long id)throws Exception;
	
	/**
	 * 根据Id取会议室审批记录
	 * @param id 数据库主键
	 * @return MeetingRoomPerm
	 * @throws Exception
	 */
	public MeetingRoomPerm getRoomPerm(Long id)throws Exception;
	
	/**
	 * 根据id取会议室申请
	 * @param id 数据库主键
	 * @return MeetingRoomApp
	 * @throws Exception
	 */
	public MeetingRoomApp getRoomApp(Long id)throws Exception;
	
	/**
	 * 修改会议室
	 * @param mr 会议室
	 */
	public void updateRoom(MeetingRoom mr);
	
	/**
	 * 查询会议室
	 * @param v3xOrgMember 会议室管理员
	 * @param accountId 单位Id
	 * @param name 会议室名称
	 * @param place 会议室地点
	 * @param seatCount 会议室可容纳人数
	 * @param needApp 会议室是否需要申请
	 * @param status 会议室状态
	 * @param delFlag 是否删除
	 * @param isPage 是否分页
	 * @return MeetingRoom集合
	 * @throws Exception
	 */
	public List getMeetingRooms(V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer seatCount[], Integer needApp, Integer status, Integer delFlag, Boolean isPage)throws Exception;
	
	/**
	 * 查询所有可以申请的会议室
	 * @param name 会议室名称
	 * @param seatCount 会议室可容纳人数
	 * @param isPage 是否分页
	 * @return MeetingRoom集合
	 */
	public List MeetingRoomsForApp(String name, Integer[] seatCount,List adminIds, Boolean isPage);
	
	/**
	 * 查询所有审批的会议室申请记录
	 * @param meetingRoomId 会议室Id
	 * @param perId 申请人Id
	 * @param isAllowed 审批状态
	 * @return MeetingRoomPerm 集合
	 * @throws Exception
	 */
	public List getMeetingRoomsForPerm(Long meetingRoomId, Long perId, Integer isAllowed)throws Exception;
	
	/**
	 * 执行会议室审批操作
	 * @param id 会议室审批Id
	 * @param isAllowed 审批状态
	 * @param description 说明
	 * @throws Exception
	 */
	public void execPerm(Long id, Integer isAllowed, String description)throws Exception;
	
	/**
	 * 根据时间段验证会议室是否冲突
	 * @param id 会议室Id
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @return 验证结果
	 * @throws Exception
	 */
	public boolean checkApp(Long id, Date startDatetime, Date endDatetime)throws Exception;
	
	/**
	 * 验证会议室当前是否有未开始的预定或会议
	 * @param mr 会议室
	 * @return 验证结果
	 * @throws Exception
	 */
	public boolean checkUsed(MeetingRoom mr)throws Exception;
	
	/**
	 * 根据时间段查询会议室使用情况
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param pageFlag 是否分页
	 * @return
	 */
	public List getUseDetailsByDay(Date startDatetime, Date endDatetime, boolean pageFlag);
	/**
	 * 根据时间段查询会议室使用情况
	 * @param adminId 管理员ID
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param pageFlag 是否分页
	 * @return
	 */
	public List getUseDetailsByDay(List adminIds, Date startDatetime, Date endDatetime, boolean pageFlag);
	/**
	 * 根据时间段查询会议室使用情况
	 * @param adminId 管理员ID
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param meetingIds 会议室范围
	 * @param pageFlag 是否分页
	 * @return
	 */
	public List getUseDetailsByDay(List adminIds, Date startDatetime, Date endDatetime,List meetingRooms, boolean pageFlag);
	
	/**
	 * 查询预订撤销记录
	 * @param mrId 会议室Id
	 * @param isAllowed 审批状态
	 * @param perId 申请人Id
	 * @return MeetingRoomPerm集合
	 * @throws Exception
	 */
	public List getCancelList(Long mrId, Integer isAllowed, Long perId)throws Exception;
	
	/**
	 * 清除审批过的审批记录
	 * @param id 会议室申请Id集合
	 */
	public void clearPerm(List id);
	
	/**
	 * 验证当前登陆用户是否为会议室管理员
	 * @return 验证结果
	 */
	public boolean checkAdmin();
	
	/**
	 * 执行申请撤销操作
	 * @param list 申请集合
	 * @throws Exception
	 */
	public void execCancel(List list)throws Exception;
	
	/**
	 * 会议撤销接口，撤销相关的会议室申请和使用记录
	 * @param meetingId 会议Id
	 * @throws Exception
	 */
	public void execCancelMeeting(Long meetingId)throws Exception;
	
    public void execCancelMeetingRec(Long meetingId) throws Exception;
	/**
	 * 根据时间段统计会议室使用情况
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param isPage 是否分页
	 * @return HashMap集合，HashMap包含"MeetingRoomRecord"(使用记录),"MonthTotal"(当前月统计),"AllTotal"(累计),"SectionTotal"(时间段统计)键
	 * 分别对应MeetingRoomRecord,Long,Long,Long对象
	 */
	public List getTotal(Date startDatetime, Date endDatetime, Boolean isPage);
	
	/**
	 * 发送会议室停用通知
	 * @param mr 会议室
	 * @throws Exception
	 */
	public void sendMeetingRoomStopMsg(MeetingRoom mr)throws Exception;
	
	
	/**
	 * 修改添加时验证会议室名称是否有重复的
	 * @param id 修改时的会议室Id，添加时为空
	 * @param name 会议室名称
	 * @return 验证结果
	 * @throws Exception
	 */
	public boolean checkMeetingRoomName(Long id, String name)throws Exception;
	
	
	/**
	 * 注入<property name="meetingRoomManager" ref="meetingRoomManager" />
	 */
	
	/**
	 * 根据发起人，开始时间,结束时间，获取所有可用的不用申请的会议室和审批通过的会议室 
	 * @param v3xOrgMember		发起人
	 * @param startDatetime		会议开始时间
	 * @param endDatetime		会议结束时间
	 * @return					List中判断如果是com.seeyon.v3x.meetingroom.domain.MeetingRoom对象，则是不用申请的会议室
	 *  						如果是com.seeyon.v3x.meetingroom.domain.MeetingRoomApp对象,则是审批通过的申请，MeetingRoomApp中包含MeetingRoom对象
	 */
	public String[][] getMeetingRoomForMeeting(Long v3xOrgMember, Long startDatetime, Long endDatetime);
	
	/**
	 * 发起会议时验证会议室是否可用
	 * @param v3xOrgMember		发起人
	 * @param meetingRoomId		选择的会议室Id
	 * @param meetingRoomAppId	会议室申请Id，对应MeetingRoomApp.Id,如果会议室是不用申请的则这个参数为null;
	 * @param meetingId         会议Id
	 * @param startDatetime		会议开始时间
	 * @param endDatetime		会议结束时间
	 * @return					true验证成功，false验证失败，验证标准，会议开始时间到结束时间包含在申请的会议室开始使用时间到结束时间范围内，
	 * 							不用申请的会议室在会议开始时间到结束时间范围内没有被占用
	 */
	public String checkMeetingRoomForMeeting(Long v3xOrgMember, Long meetingRoomId, Long meetingRoomAppId, Long meetingId, Long startDatetime, Long endDatetime)throws Exception;
	
	/**
	 * 发起会议成功后，提交到会议室管理，使会议室和会议关联起来
	 * @param meeting			会议Id
	 * @param meetingRoomId		会议室Id
	 * @param meetingRoomAppId	如果是需要审批的会议室要指定会议室申请Id，否则为null;对应com.seeyon.v3x.meetingroom.domain.MeetingRoomApp的Id。
	 */
	public void execMeeting(Long meeting, Long meetingRoomId, Long meetingRoomAppId, Long startDatetime, Long endDatetime)throws Exception;
	
	/**会议室使用查看调用接口
	 * <html:link renderURL='/meetingroom.do' var='mrUrl' />
	 * window.showModalDialog("${mrUrl}?method=view",self,"dialogHeight: 287px; dialogWidth:750px;dialogTop:45;dialogLeft: 115;center: yes; status=no");
	 */
	
	/**
	 * 根据会议得到会议室方法
	 */
	public String[] getByMeeting(MtMeeting meeting);
	
	/**
	 * 会议室权限移植
	 * @param adminIdLong 以前的管理员
	 * @param admin_newLong 现在的管理员
	 * @param user 登陆用户
	 */
	public void updateMeetingRoomMangerBatch(long adminIdLong, long admin_newLong, User user);
	public void updateMeetingRoomMangerBatch(long adminIdLong, long admin_newLong, User user,boolean fromFlag);

	public List getAllAdmins(Long id);
	
}
