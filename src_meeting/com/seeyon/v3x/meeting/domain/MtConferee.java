package com.seeyon.v3x.meeting.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 会议与会对象
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-12-28
 */
public class MtConferee extends BaseModel implements Serializable {

	private static final long serialVersionUID = 4805933613776484432L;
	
	/**
	 * 与会对象ID
	 */
	private Long confereeId;
	/**
	 * 与会对象类型：目前支持人员、部门、组三种类型
	 */
	private String confereeType;
	/**
	 * 与会对象排序号
	 */
	private int sortId;
	/**
	 * 与会对象所要参加的会议ID
	 */
	private Long meetingId;
	
	public MtConferee() {
		super();
	}
	
	/**
	 * 定义与会对象构造方法
	 * @param confereeId	与会对象ID
	 * @param confereeType	与会对象类型：目前支持人员、部门、组三种类型
	 * @param meetingId		与会对象所要参加的会议ID
	 * @param sortId		与会对象排序号
	 */
	public MtConferee(Long confereeId, String confereeType, int sortId, Long meetingId) {
		super();
		this.setNewId();
		this.confereeId = confereeId;
		this.confereeType = confereeType;
		this.sortId = sortId;
		this.meetingId = meetingId;
	}

	public Long getConfereeId() {
		return confereeId;
	}

	public void setConfereeId(Long confereeId) {
		this.confereeId = confereeId;
	}

	public String getConfereeType() {
		return confereeType;
	}

	public void setConfereeType(String confereeType) {
		this.confereeType = confereeType;
	}

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	
}
