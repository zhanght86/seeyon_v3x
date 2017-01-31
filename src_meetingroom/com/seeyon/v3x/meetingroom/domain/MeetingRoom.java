package com.seeyon.v3x.meetingroom.domain;

import java.util.*;
import com.seeyon.v3x.organization.domain.V3xOrgMember;


/**
 * @author 刘嵩高
 * @version 1.0
 * @since 2008-09-18
 * 
 */

public class MeetingRoom  implements java.io.Serializable {
    // Fields

     /**
     * 数据库主键UUID
     */
    private Long id;
     /**
     * 会议室创建人，会议室管理员
     */
    private V3xOrgMember v3xOrgMember;
     /**
     * 单位Id
     */
    private Long accountId;
     /**
     * 会议室名称
     */
    private String name;
     /**
     * 会议室地点
     */
    private String place;
     /**
     * 描述
     */
    private String description;
     /**
     * 可容纳人数
     */
    private Integer seatCount;
     /**
     * 是否需要申请
     */
    private Integer needApp;
     /**
     * 状态：正常，停用
     */
    private Integer status;
     /**
     * 创建时间
     */
    private Date createDatetime;
     /**
     * 修改时间
     */
    private Date modifyDatetime;
     /**
     * 删除标记
     */
    private Integer delFlag;

    // Constructors

    /** default constructor */
    public MeetingRoom() {
    }

    // Property accessors

    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return this.accountId;
    }
    
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return this.place;
    }
    
    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSeatCount() {
        return this.seatCount;
    }
    
    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public Integer getNeedApp() {
        return this.needApp;
    }
    
    public void setNeedApp(Integer needApp) {
        this.needApp = needApp;
    }

    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDatetime() {
        return this.createDatetime;
    }
    
    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Date getModifyDatetime() {
        return this.modifyDatetime;
    }
    
    public void setModifyDatetime(Date modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }

    public Integer getDelFlag() {
        return this.delFlag;
    }
    
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

	public V3xOrgMember getV3xOrgMember(){
		return v3xOrgMember;
	}

	public void setV3xOrgMember(V3xOrgMember orgMember){
		v3xOrgMember = orgMember;
	}
	
}