package com.seeyon.v3x.mobile.webmodel;

import java.util.Date;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.util.IdentifierUtil;

public class AffairsListObject implements Comparable<AffairsListObject>{
	private Long id;// 事项ID

	private String title;

	private int type;// 参照ApplicationCategoryEnum
	
	private CalEvent calEvent;
	
	private Long summaryId;
	
	private Long senderId;//发起者Id
	private Date sendTime;//发起时间
	private Date dealTiem;//处理时间
	private int state;//协同的状态
	private boolean proxy;//是否代理
	private Long porxyId;//被代理人ID
	private String bodyType;//正文类型

	private boolean hasAttach;//是否含有附件

	public boolean isHasAttach() {
		return hasAttach;
	}

	public void setHasAttach(boolean hasAttach) {
		this.hasAttach = hasAttach;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public Long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public CalEvent getCalEvent() {
		return calEvent;
	}

	public void setCalEvent(CalEvent calEvent) {
		this.calEvent = calEvent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void setType(ApplicationCategoryEnum type) {
		this.type = type.key();
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	
	public Date getDealTiem() {
		return dealTiem;
	}

	public void setDealTiem(Date dealTiem) {
		this.dealTiem = dealTiem;
	}

	public int compareTo(AffairsListObject o) {
		if(o!=null && o.getDealTiem()!=null && this!=null && this.getDealTiem()!=null){
			return -(this.dealTiem.compareTo(o.getDealTiem()));
		}else{
			return -1;
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Long getPorxyId() {
		return porxyId;
	}

	public void setPorxyId(Long porxyId) {
		this.porxyId = porxyId;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}
	
}
