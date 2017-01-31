package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.seeyon.v3x.common.domain.BaseModel;

public class ColSuperviseDetail extends BaseModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String title;
	private int entityType;
	private long entityId;
	private Integer status;
	private Long senderId;
	private String supervisors;
	private Date createDate;
	private Date awakeDate;
	private String description;
	private Integer count;
	private Integer remindMode;	
	//private boolean canModify;
	private String scheduleProp;
	private Long templateDateTerminal;
	private String superviseDateType;
	private Set<ColSupervisor> colSupervisors;
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSupervisors() {
		return supervisors;
	}
	public void setSupervisors(String supervisors) {
		this.supervisors = supervisors;
	}
	public Integer getRemindMode() {
		return remindMode;
	}
	public void setRemindMode(Integer remindMode) {
		this.remindMode = remindMode;
	}
	public Set<ColSupervisor> getColSupervisors() {
		return colSupervisors;
	}
	public void setColSupervisors(Set<ColSupervisor> colSupervisors) {
		this.colSupervisors = colSupervisors;
	}
	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	public String getScheduleProp() {
		return scheduleProp;
	}
	public void setScheduleProp(String scheduleProp) {
		this.scheduleProp = scheduleProp;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getAwakeDate() {
		return awakeDate;
	}
	public void setAwakeDate(Date awakeDate) {
		this.awakeDate = awakeDate;
	}
	/*public boolean isCanModify() {
		return canModify;
	}
	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}*/
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public int getEntityType() {
		return entityType;
	}
	public void setEntityType(int entityType) {
		this.entityType = entityType;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return the superviseDateType
	 */
	public String getSuperviseDateType() {
		return superviseDateType;
	}
	/**
	 * @param superviseDateType the superviseDateType to set
	 */
	public void setSuperviseDateType(String superviseDateType) {
		this.superviseDateType = superviseDateType;
	}
	/**
	 * @return the templateDateTerminal
	 */
	public Long getTemplateDateTerminal() {
		return templateDateTerminal;
	}
	/**
	 * @param templateDateTerminal the templateDateTerminal to set
	 */
	public void setTemplateDateTerminal(Long templateDateTerminal) {
		this.templateDateTerminal = templateDateTerminal;
	}
}
