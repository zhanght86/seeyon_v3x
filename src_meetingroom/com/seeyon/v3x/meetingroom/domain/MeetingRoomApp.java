package com.seeyon.v3x.meetingroom.domain;

import java.util.Date;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.meeting.domain.MtMeeting;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */

public class MeetingRoomApp  implements java.io.Serializable {


    // Fields    

     /**
     * 数据库主键UUID
     */
    private Long id;
     /**
     * 会议室
     */
    private MeetingRoom meetingRoom;
     /**
     * 申请人
     */
    private V3xOrgMember v3xOrgMember;
     /**
     * 申请部门
     */
    private V3xOrgDepartment v3xOrgDepartment;
     /**
     * 开始使用时间
     */
    private Date startDatetime;
     /**
     * 结束使用时间
     */
    private Date endDatetime;
     /**
     * 会议
     */
    private MtMeeting meeting;
     /**
     * 申请说明，用途
     */
    private String description;
     /**
     * 申请状态
     */
    private Integer status;
     /**
     * 申请时间
     */
    private Date appDatetime;
     /**
     * 审批
     */
    private MeetingRoomPerm meetingRoomPerm;

    // Constructors

    /** default constructor */
    public MeetingRoomApp() {
    }
   
    // Property accessors

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDatetime() {
        return this.startDatetime;
    }
    
    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getEndDatetime() {
        return this.endDatetime;
    }
    
    public void setEndDatetime(Date endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getAppDatetime() {
        return this.appDatetime;
    }
    
    public void setAppDatetime(Date appDatetime) {
        this.appDatetime = appDatetime;
    }

	public V3xOrgDepartment getV3xOrgDepartment(){
		return v3xOrgDepartment;
	}

	public void setV3xOrgDepartment(V3xOrgDepartment orgDepartment){
		v3xOrgDepartment = orgDepartment;
	}

	public V3xOrgMember getV3xOrgMember(){
		return v3xOrgMember;
	}

	public void setV3xOrgMember(V3xOrgMember orgMember){
		v3xOrgMember = orgMember;
	}

	public MeetingRoom getMeetingRoom(){
		return meetingRoom;
	}

	public void setMeetingRoom(MeetingRoom meetingRoom){
		this.meetingRoom = meetingRoom;
	}

	public MeetingRoomPerm getMeetingRoomPerm(){
		return meetingRoomPerm;
	}

	public void setMeetingRoomPerm(MeetingRoomPerm meetingRoomPerm){
		this.meetingRoomPerm = meetingRoomPerm;
	}

	public MtMeeting getMeeting(){
		return meeting;
	}

	public void setMeeting(MtMeeting meeting){
		this.meeting = meeting;
	}
   








}