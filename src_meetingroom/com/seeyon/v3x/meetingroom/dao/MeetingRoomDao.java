package com.seeyon.v3x.meetingroom.dao;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meetingroom.domain.MeetingRoom;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomApp;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomPerm;
import com.seeyon.v3x.meetingroom.domain.MeetingRoomRecord;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 *
 */
public interface MeetingRoomDao {
	
	/**
	 * 插入MeetingRoom对象
	 * @param mr
	 */
	public void save(MeetingRoom mr);
	
	/**
	 * 插入MeetingRoomApp对象
	 * @param mra
	 */
	public void save(MeetingRoomApp mra);
	
	/**
	 * 插入MeetingRoomPerm对象
	 * @param mrp
	 */
	public void save(MeetingRoomPerm mrp);
	
	/**
	 * 根据Id装载MeetingRoom对象
	 * @param id 数据库主键
	 * @return MeetingRoom对象
	 * @throws Exception
	 */
	public MeetingRoom loadMeetingRoom(Long id)throws Exception;
	
	/**
	 * 根据Id装载MeetingRoomPerm对象
	 * @param id 数据库主键
	 * @return MeetingRoomPerm对象
	 * @throws Exception
	 */
	public MeetingRoomPerm loadMeetingRoomPerm(Long id)throws Exception;
	
	/**
	 * 根据Id装载MeetingRoomApp对象
	 * @param id 数据库主键
	 * @return MeetingRoomApp对象
	 * @throws Exception
	 */
	public MeetingRoomApp loadMeetingRoomApp(Long id)throws Exception;
	
	/**
	 * 根据Id装载MeetingRoomRecord对象
	 * @param id 数据库主键
	 * @return MeetingRoomRecord
	 * @throws Exception
	 */
	public MeetingRoomRecord loadMeetingRoomRecord(Long id)throws Exception;
	
	/**
	 * 更新MeetingRoom对象
	 * @param mr
	 */
	public void update(MeetingRoom mr);
	
	/**
	 * 更新MeetingRoomApp对象
	 * @param mra
	 */
	public void update(MeetingRoomApp mra);
	
	/**
	 * 更新MeetingRoomPerm对象
	 * @param mrp
	 */
	public void update(MeetingRoomPerm mrp);
	
	/**
	 * 分页查询会议室
	 * @param v3xOrgMember 会议室管理员
	 * @param accountId 单位Id
	 * @param name 会议室名称
	 * @param place 会议室地址
	 * @param seatCount 会议室可容纳人数
	 * @param needApp 会议室是否需要申请
	 * @param status 会议室状态
	 * @param delFlag 是否删除
	 * @param isPage 是否分页查询
	 * @return MeetingRoom集合
	 * @throws Exception
	 */
	public List find(V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer[] seatCount, Integer needApp, Integer status, Integer delFlag, Boolean isPage)throws Exception;
	
	/**
	 * 查询会议室数量
	 * @param v3xOrgMember 会议室管理员
	 * @param accountId 单位Id
	 * @param name 会议室名称
	 * @param place 会议室地址
	 * @param seatCount 会议室可容纳人数
	 * @param needApp 会议室是否需要申请
	 * @param status 会议室状态
	 * @param delFlag 是否删除
	 * @return 会议室数量
	 * @throws Exception
	 */
	public int findCount(V3xOrgMember v3xOrgMember, Long accountId, String name, String place, Integer[] seatCount, Integer needApp, Integer status, Integer delFlag)throws Exception;
	
	/**
	 * 查询可申请的会议室
	 * @param v3xOrgMembers 会议室管理员集合
	 * @param name 会议室名称
	 * @param seatCount 会议室可容纳人数
	 * @param isPage 是否分页查询
	 * @return MeetingRoom集合
	 */
	public List findApp(List v3xOrgMembers, String name, Integer[] seatCount, Boolean isPage);
	
	/**
	 * 查询可申请的会议室数量
	 * @param v3xOrgMembers 会议室管理员集合
	 * @param name 会议室名称
	 * @param seatCount 会议室可容纳人数
	 * @return
	 */
	public int findAppCount(List v3xOrgMembers, String name, Integer[] seatCount);
	
	/**
	 * 按时间段查询冲突的审批通过会议室预定
	 * @param mr 会议室对象
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @return 查询数量
	 */
	public int checkAppCount(MeetingRoom mr, Date startDatetime, Date endDatetime);
	
	/**
	 * 按时间段查询冲突的待审批的会议室预定
	 * @param mr 会议室对象
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @return 查询数量
	 */
	public List checkAppPermList(MeetingRoom mr, Date startDatetime, Date endDatetime);
	
	/**
	 * 查询会议室申请审批记录
	 * @param v3xOrgMember 会议室管理员
	 * @param mr 会议室
	 * @param appMember 申请人
	 * @param isAllowed 审批状态
	 * @return MeetingRoomPerm集合
	 */
	public List findForPerm(V3xOrgMember v3xOrgMember, MeetingRoom mr, V3xOrgMember appMember, Integer isAllowed);
	
	/**
	 * 查询会议室申请审批记录数量
	 * @param v3xOrgMember 会议室管理员
	 * @param mr 会议室
	 * @param appMember 申请人
	 * @param isAllowed 审批状态
	 * @return 查询数量
	 */
	public int findForPermCount(V3xOrgMember v3xOrgMember, MeetingRoom mr, V3xOrgMember appMember, Integer isAllowed);
	
	/**
	 * 根据时间段查询会议室使用情况
	 * @param v3xOrgMember 会议室管理员集合
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param pageFlag 是否分页
	 * @return 同时包含MeetingRoomApp和MeetingRoomRecord的集合
	 */
	public List findUseDetailsByDay(List v3xOrgMember, Date startDatetime, Date endDatetime, boolean pageFlag);

	/**
	 * 根据时间段和查询范围查询会议室使用情况
	 * @param v3xOrgMember 会议室管理员集合
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param meetingIds 会议室范围
	 * @param pageFlag 是否分页
	 * @return 同时包含MeetingRoomApp和MeetingRoomRecord的集合
	 */
	public List findUseDetailsByDay(List v3xOrgMember, Date startDatetime, Date endDatetime,List meetingRoom, boolean pageFlag);
	/**
	 * 根据ID集合清除审批过的申请记录
	 * @param id
	 */
	public void clearPerm(List id);
	
	
	/**
	 * 添加或修改的时候验证会议室名称是否重复
	 * @param id 修改时会议室ID，添加时为空
	 * @param name 会议室名称
	 * @return
	 * @throws Exception
	 */
	public boolean checkMeetingRoomName(Long id, String name)throws Exception;
	
	/**
	 * 验证会议室当前是否有未开始的预定或会议
	 * @param mr 会议室
	 * @return 验证结果
	 * @throws Exception
	 */
	public boolean checkUsed(MeetingRoom mr)throws Exception;
	
	/**
	 * 查询会议室所有未开始的预定或会议
	 * @param mr 会议室
	 * @return 同时包含MeetingRoomApp和MeetingRoomRecord的集合
	 * @throws Exception
	 */
	public List getUsedList(MeetingRoom mr)throws Exception;
	
	/**
	 * 根据V3xOrgMember.id验证是否为会议室管理员
	 * @param id V3xOrgMember.id
	 * @return 验证结构
	 */
	public boolean checkAdmin(Long id);
	
	/**
	 * 根据部门Id集合查询会议室管理员
	 * @param departmentId 部门Id集合
	 * @return MAdminSetting.id.admin集合
	 */
	public List getMyAdmin(List departmentId);
	
	/**
	 * 查询申请审批记录集合
	 * @param adminMembe r会议室管理员
	 * @param appMember 申请人
	 * @param mr 会议室
	 * @param isAllowed 审批状态
	 * @return MeetingRoomApp集合
	 */
	public List getCancals(V3xOrgMember adminMember, V3xOrgMember appMember, MeetingRoom mr, Integer isAllowed);
	
	/**
	 * 查询申请审批记录数量
	 * @param adminMembe r会议室管理员
	 * @param appMember 申请人
	 * @param mr 会议室
	 * @param isAllowed 审批状态
	 * @return 查询数量
	 */
	public int getCancelsCount(V3xOrgMember adminMember, V3xOrgMember appMember, MeetingRoom mr, Integer isAllowed);
	
	/**
	 * 获取会议室管理员所能查看的预定撤销会议申请列表：自己发起的申请的和他人对自己所管理会议室发出的申请
	 * @param adminId    	会议室管理员ID
	 * @param meetingRoomId	在查询时指定某个会议室
	 * @param state			在查询时指定申请审核状态
	 * @param applicantId   在查询时指定申请人
	 */
	public List<MeetingRoomApp> getCanCanceledMeetingRoomApps4Admin(Long adminId, Long meetingRoomId, Integer state, Long applicantId);
	
	/**
	 * 获取普通用户（非会议室管理员）所能查看的预定撤销会议申请列表：自己发起的申请
	 * @param userId        当前用户（非会议室管理员）ID
	 * @param meetingRoomId 在查询时指定某个会议室
	 * @param state			在查询时指定申请审核状态
	 */
	public List<MeetingRoomApp> getCanCanceledMeetingRoomApps4User(Long userId, Long meetingRoomId, Integer state);
	
	/**
	 * 执行会议室申请撤销操作
	 * @param list 会议室申请集合
	 * @throws Exception
	 */
	public void execCancel(List list)throws Exception;
	
	/**
	 * 根据时间段查询会议室使用记录
	 * @param v3xOrgMember 会议室管理员
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @param isPage 是否分页
	 * @return HashMap集合，HashMap包含"MeetingRoomRecord"(使用记录),"MonthTotal"(当前月统计),"AllTotal"(累计),"SectionTotal"(时间段统计)键
	 * 分别对应MeetingRoomRecord,Long,Long,Long对象
	 * @throws Exception
	 */
	public List getTotal(V3xOrgMember v3xOrgMember, Date startDatetime, Date endDatetime, Boolean isPage)throws Exception;
	
	/**根据时间段查询预定的会议室和空闲的会议室
	 * @param v3xOrgMembers 会议室管理员集合
	 * @param appMember 申请人
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @return 同时包含MeetingRoomApp和MeetingRoom对象的集合
	 */
	public List getMeetingRoomForMeeting(List v3xOrgMembers, V3xOrgMember appMember, Date startDatetime, Date endDatetime);
	
	/**
	 * 根据时间段验证会议室是否可用
	 * @param v3xOrgMember 申请人
	 * @param mr 会议室
	 * @param mra 会议申请
	 * @param meeting 会议
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @return 验证结构"true"(通过),"false"(不通过),MtMeeting.id(只有一个冲突的会议Id)
	 */
	public String checkMeetingRoomForMeeting(V3xOrgMember v3xOrgMember, MeetingRoom mr, MeetingRoomApp mra, MtMeeting meeting, Date startDatetime, Date endDatetime);
	
	/**
	 * 发送会议的接口，关联会议室使用情况
	 * @param meeting 会议Id
	 * @param mr 会议室
	 * @param mra 会议室申请
	 * @param startDatetime 开始时间
	 * @param endDatetime 结束时间
	 * @throws Exception
	 */
	public void execMeeting(com.seeyon.v3x.meeting.domain.MtMeeting meeting, MeetingRoom mr, MeetingRoomApp mra, Date startDatetime, Date endDatetime)throws Exception;
	
	/**
	 * 会议撤销接口，撤销会议室申请，会议室使用情况
	 * @param meeting 会议
	 * @throws Exception
	 */
	public void execCancelMeeting(MtMeeting meeting)throws Exception;
    /**
     * 会议撤销接口，删除会议室记录，更新会议室申请
     * @param meeting 会议
     * @throws Exception
     */
    public void execCancelMeetingRec(MtMeeting meeting)throws Exception;
	
	/**根据会议查询使用的会议室
	 * @param meeting 会议
	 * @return String[0]:会议室申请Id，String[1]:会议室名称
	 */
	public String[] getByMeeting(MtMeeting meeting);
	
	/**
	 * 会议室管理员权利 移交
	 * @param adminIdLong
	 * @param admin_newLong
	 * @param user
	 */
	public void updateBookMangerBatch(long adminIdLong, long admin_newLong, User user);
	public void updateBookMangerBatch(long adminIdLong, long admin_newLong, User user,boolean fromFlag);

	
}
