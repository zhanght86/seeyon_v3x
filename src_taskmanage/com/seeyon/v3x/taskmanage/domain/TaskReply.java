package com.seeyon.v3x.taskmanage.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 任务回复
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskReply extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3396676731674840776L;
	
	/**
	 * 回复内容
	 */
	private String content;
	/**
	 * 回复者
	 */
	private Long createUser;
	/**
	 * 回复时间
	 */
	private Timestamp createTime;
	/**
	 * 引用的回复(对回复进行回复的情况)
	 */
	private Long parentReplyId = -1l;
	/**
	 * 所属任务ID
	 */
	private Long taskId;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Long getParentReplyId() {
		return parentReplyId;
	}
	public void setParentReplyId(Long parentReplyId) {
		this.parentReplyId = parentReplyId;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	/**
	 * 不持久化。被用于引用回复的子回复集合
	 */
	private List<TaskReply> referenceReplys;

	public List<TaskReply> getReferenceReplys() {
		return referenceReplys;
	}
	public void setReferenceReplys(List<TaskReply> referenceReplys) {
		this.referenceReplys = referenceReplys;
	}
	
	/**
	 * 添加一个引用回复
	 * @param child		引用回复
	 */
	public void addChild(TaskReply child) {
		if(this.referenceReplys == null)
			this.referenceReplys = new ArrayList<TaskReply>();
		
		this.referenceReplys.add(child);
	}

}
