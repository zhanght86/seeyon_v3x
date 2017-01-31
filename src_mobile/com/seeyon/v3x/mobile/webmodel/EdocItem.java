package com.seeyon.v3x.mobile.webmodel;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.filemanager.Attachment;

/**
 * 公文元素 对象
 * 
 * @author Hub
 *
 */
public class EdocItem implements Comparable<EdocItem>{
	private String key;//事项标题
	private String value;//事项的值
	private boolean display;//是否在手机端能够显示(手写意见/文单签批/盖章)
	private boolean opinion;//是否是意见公文元素
	private int attitude;//态度
	private Long processer;//处理人
	private Date processDate;//处理时间
	private boolean system;//是否是系统默认的
	private Integer opinionType;
	List<Attachment> opinionAttachments;//意见的附件
	private boolean hasAttachments;
	private boolean senderOpinion;//是否是发起者意见
	private int edocType;//公文的种类 发文，收文，签报

	public List<Attachment> getOpinionAttachments() {
		return opinionAttachments;
	}
	public void setOpinionAttachments(List<Attachment> opinionAttachments) {
		this.opinionAttachments = opinionAttachments;
	}
	public Integer getOpinionType() {
		return opinionType;
	}
	public void setOpinionType(Integer opinionType) {
		this.opinionType = opinionType;
	}
	public boolean isSystem() {
		return system;
	}
	public void setSystem(boolean system) {
		this.system = system;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getAttitude() {
		return attitude;
	}
	public void setAttitude(int attitude) {
		this.attitude = attitude;
	}
	public Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public Long getProcesser() {
		return processer;
	}
	public void setProcesser(Long processer) {
		this.processer = processer;
	}
	public boolean isDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}
	public boolean isOpinion() {
		return opinion;
	}
	public void setOpinion(boolean opinion) {
		this.opinion = opinion;
	}
	public int compareTo(EdocItem o) {
		return -(this.processDate.compareTo(o.getProcessDate()));
	}
	public boolean isHasAttachments() {
		if(opinionAttachments!=null && opinionAttachments.size()!=0)
			hasAttachments= true;
		else
			hasAttachments = false;
		return hasAttachments;
	}
	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}
	public boolean isSenderOpinion() {
		return senderOpinion;
	}
	public void setSenderOpinion(boolean senderOpinion) {
		this.senderOpinion = senderOpinion;
	}
	public int getEdocType() {
		return edocType;
	}
	public void setEdocType(int edocType) {
		this.edocType = edocType;
	}
	
	
}
