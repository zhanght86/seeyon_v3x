package com.seeyon.v3x.meetingroom.domain;

import java.util.Date;
import com.seeyon.v3x.meeting.domain.MtMeeting;


/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */

public class MeetingRoomRecord  implements java.io.Serializable {

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
     * 开始使用时间
     */
    private Date startDatetime;
     /**
     * 结束使用时间
     */
    private Date endDatetime;
     /**
     * 会议室申请
     */
    private MeetingRoomApp meetingRoomApp;
     /**
     * 会议
     */
    private MtMeeting meeting;
     /**
     * 描述
     */
    private String description;

    // Constructors

    /** default constructor */
    public MeetingRoomRecord() {
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

	public MtMeeting getMeeting(){
		return meeting;
	}

	public void setMeeting(MtMeeting meeting){
		this.meeting = meeting;
	}

	public MeetingRoom getMeetingRoom(){
		return meetingRoom;
	}

	public void setMeetingRoom(MeetingRoom meetingRoom){
		this.meetingRoom = meetingRoom;
	}

	public MeetingRoomApp getMeetingRoomApp(){
		return meetingRoomApp;
	}

	public void setMeetingRoomApp(MeetingRoomApp meetingRoomApp){
		this.meetingRoomApp = meetingRoomApp;
	}

}