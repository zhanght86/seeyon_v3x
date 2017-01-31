package com.seeyon.v3x.project.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 记录项目阶段下的事件(协同、任务、文档...)
 * @author Administrator
 *
 */
public class ProjectPhaseEvent extends BaseModel implements Serializable {

	private static final long serialVersionUID = 9186221784615745126L;

	private Integer eventType;// 项目协同、项目任务、项目文档、项目计划/会议/日程、项目讨论/项目留言
	private Long eventId;// 事件ID
	private Long phaseId;// 项目阶段ID
	
	public ProjectPhaseEvent(){
		
	}
	
	public ProjectPhaseEvent(Integer eventType, Long eventId, Long phaseId){
		this.setNewId();
		this.eventType = eventType;
		this.eventId = eventId;
		this.phaseId = phaseId;
	}

	public Integer getEventType() {
		return eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Long phaseId) {
		this.phaseId = phaseId;
	}

}