package com.seeyon.v3x.mobile.webmodel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.filemanager.Attachment;

/**
 * 会议细节
 * 
 */
public class MeetingDetial {
	private String title;

	private Long creator;

	private String content;

	private Date endDate;// 会议的结束时间

	private Date createDate;// 会议的创建时间

	private Date beginDate;// 开会的时间

	private String location;// 开会的地点

	private Long masterId;// 主持人的ID

	private Long recordId;// 记录人的ID
	
	private String contentType;
	
	private String sign;
	

	private List<Object[]> attenders;// key为与会人的ID，value为参加会议人的态度（1 参加，2

	// 不参加，3 未回执,4 待定）

	private Map<String, Integer> condition;// key为(“参加”或者“不参加”或者“未回执”或者“待定”)

	// value为各自的人数。

	private List<Attachment> attachments;

	private String bulContent;
	
	public String getBulContent() {
		return bulContent;
	}

	public void setBulContent(String bulContent) {
		this.bulContent = bulContent;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getContent() {
		return content;
	}


	public List<Object[]> getAttenders() {
		return attenders;
	}

	public void setAttenders(List<Object[]> attenders) {
		this.attenders = attenders;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}


	public Map<String, Integer> getCondition() {
		return condition;
	}

	public void setCondition(Map<String, Integer> condition) {
		this.condition = condition;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getMasterId() {
		return masterId;
	}

	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreator() {
		return creator;
	}

	public void setCreator(Long creator) {
		this.creator = creator;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
