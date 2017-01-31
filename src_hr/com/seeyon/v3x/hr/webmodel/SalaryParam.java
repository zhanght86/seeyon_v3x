/**
 * $Id: SalaryParam.java,v 1.3 2011/06/22 08:24:30 tanmf Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * SalaryParam.java created by paul at 2007-9-13 下午02:32:23
 *
 */
package com.seeyon.v3x.hr.webmodel;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.seeyon.v3x.collaboration.domain.ColBody;
import com.seeyon.v3x.collaboration.domain.ColOpinion;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.common.authenticate.domain.User;

/**
 * <tt>SalaryParam</tt>是转发协同所使用，该对象作为ExecutableTask.run()方法的参数对象
 * @author paul
 *
 */
@SuppressWarnings(value={"serial"}) 
public class SalaryParam implements Serializable {
	private User sender; //发送人
	private Long receiverId; //接收人：Id
	
	private String subject; //协同：标题
	private String note; //协同：附言
	private String content; //协同：内容
	
	private ColOpinion opinion; //协同：附言
	private ColBody body; //协同：内容
	private ColSummary summary; //协同：标题
	
	//构造：初始化协同数据
	public SalaryParam(User sender, Long receiverId, String subject, String note, String content) {
		this.setSender(sender);
		this.setReceiverId(receiverId);
		this.setSubject(subject);
		this.setNote(note);
		this.setContent(content);
		
		//ColSummary
		summary = new ColSummary();
		summary.setAdvanceRemind(new Long(-1));
		summary.setCanArchive(true);
		summary.setCanDueReminder(true);
		summary.setCanEdit(true);
		summary.setCanForward(true);
		summary.setCanModify(true);
		summary.setCanEditAttachment(true);
		summary.setCanTrack(true);
		summary.setDeadline(new Long(0));
		summary.setImportantLevel(1);
		summary.setRemindInterval(new Long(0));
		summary.setSubject(subject);
		summary.setStartMemberId(sender.getId());
        
        //ColOpinion
		opinion = new ColOpinion();
		opinion.setContent(note);
		opinion.setIdIfNew();
		opinion.affairIsTrack = true;
        
        //ColBody
        body = new ColBody();
        body.setContent(content);
        body.setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
        Date bodyCreateDate = new Date();
        body.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
        
        summary.setIdIfNew();
	}
	

	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public ColOpinion getOpinion() {
		return opinion;
	}
	public void setOpinion(ColOpinion opinion) {
		this.opinion = opinion;
	}
	public ColBody getBody() {
		return body;
	}
	public void setBody(ColBody body) {
		this.body = body;
	}
	public ColSummary getSummary() {
		return summary;
	}
	public void setSummary(ColSummary summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
