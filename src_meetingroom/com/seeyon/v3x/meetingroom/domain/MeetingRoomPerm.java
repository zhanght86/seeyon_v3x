package com.seeyon.v3x.meetingroom.domain;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import java.util.Date;

/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */
public class MeetingRoomPerm  implements java.io.Serializable {


    // Fields    

     /**
     * 申请ID，数据库主键
     */
    private Long appId;
     /**
     * 审批状态
     */
    private Integer isAllowed;
     /**
     * 审批说明
     */
    private String description;
     /**
     * 审批时间
     */
    private Date proDatetime;
     /**
     * 删除标志
     */
    private Integer delFlag;
     /**
     * 申请
     */
    private MeetingRoomApp meetingRoomApp;

    // Constructors

    /** default constructor */
    public MeetingRoomPerm() {
    }
   
    // Property accessors

    public Long getAppId() {
        return this.appId;
    }
    
    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getIsAllowed() {
        return this.isAllowed;
    }
    
    public void setIsAllowed(Integer isAllowed) {
        this.isAllowed = isAllowed;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getProDatetime() {
        return this.proDatetime;
    }
    
    public void setProDatetime(Date proDatetime) {
        this.proDatetime = proDatetime;
    }

    public Integer getDelFlag() {
        return this.delFlag;
    }
    
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

	public MeetingRoomApp getMeetingRoomApp(){
		return meetingRoomApp;
	}

	public void setMeetingRoomApp(MeetingRoomApp meetingRoomApp){
		this.meetingRoomApp = meetingRoomApp;
	}

}