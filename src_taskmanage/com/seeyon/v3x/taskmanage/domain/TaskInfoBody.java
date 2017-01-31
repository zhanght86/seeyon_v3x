package com.seeyon.v3x.taskmanage.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 任务信息正文内容
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-1
 */
public class TaskInfoBody extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5598776704497621873L;
	
	private Date createDate;
	private String content;
	private String bodyType;
	private String contentName;
	
	public TaskInfoBody() {}
	
	public TaskInfoBody(Long taskId, String content) {
		super();
		this.setId(taskId);
		this.content = content;
		this.createDate = new Date(System.currentTimeMillis());
		this.bodyType = com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	public String getContentName() {
		return contentName;
	}
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

}
