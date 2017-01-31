package com.seeyon.v3x.calendar.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class PeriodicalCalEvent  extends AbstractCalEvent implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6267543208496506358L;

	private Long periodicalId;

	public Long getPeriodicalId() {
		return periodicalId;
	}

	public void setPeriodicalId(Long periodicalId) {
		this.periodicalId = periodicalId;
	}
	public PeriodicalCalEvent(){}
	
	public PeriodicalCalEvent(AbstractCalEvent event){
		super.setIdIfNew();
		super.setAlarmDate(event.getAlarmDate());
		super.setAlarmFlag(event.isAlarmFlag());
		super.setAttachmentsFlag(event.getAttachmentsFlag());
		super.setBeforendAlarm(event.getBeforendAlarm());
		super.setBeforeTime(event.getBeforeTime());
		super.setBeginDate(event.getBeginDate());
		super.setEndDate(event.getEndDate());
		super.setCalEventType(event.getCalEventType());
		super.setCompleteRate(event.getCompleteRate());
		super.setCompleteRateInt(event.getCompleteRateInt());
		super.setContentId(event.getContentId());
		super.setCreateDate(event.getCreateDate());
		super.setCreateUserId(event.getCreateUserId());
		super.setDateInfo(event.getDateInfo());
		super.setDateRangeType(event.getDateRangeType());
		super.setEventType(event.getEventType());
		super.setFromId(event.getFromId());
		super.setFromType(event.getFromType());
		super.setPriorityType(event.getPriorityType());
		super.setProjectId(event.getProjectId());
		super.setPeriodicalStyle(event.getPeriodicalStyle());
		super.setRangeSort(event.getRangeSort());
		super.setRealEstimateTime(event.getRealEstimateTime());
		super.setReceiveMemberId(event.getReceiveMemberId());
		super.setReceiveMemberName(event.getReceiveMemberName());
		super.setShareTarget(event.getShareTarget());
		super.setShareType(event.getShareType());
		super.setSignifyType(event.getSignifyType());
		super.setStates(event.getStates());
		super.setSubject(event.getSubject());
		super.setTimeFlag(event.getTimeFlag());
		super.setTran(event.getTran());
		super.setTranMemberIds(event.getTranMemberIds());
		super.setWorkType(event.getWorkType());
	}
	public void updateByEvent(AbstractCalEvent event){
		super.setAlarmDate(event.getAlarmDate());
		super.setAlarmFlag(event.isAlarmFlag());
		super.setAttachmentsFlag(event.getAttachmentsFlag());
		super.setBeforendAlarm(event.getBeforendAlarm());
		super.setBeforeTime(event.getBeforeTime());
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(this.getBeginDate());
		
		GregorianCalendar tempCalendar = new GregorianCalendar();
		tempCalendar.setTime(event.getBeginDate());
		
		calendar.set(Calendar.HOUR, tempCalendar.get(Calendar.HOUR));
		calendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
		
		//只能修改小时和分钟
		super.setBeginDate(calendar.getTime());
		
		calendar.setTime(this.getEndDate());
		tempCalendar.setTime(event.getEndDate());
		calendar.set(Calendar.HOUR, tempCalendar.get(Calendar.HOUR));
		calendar.set(Calendar.MINUTE, tempCalendar.get(Calendar.MINUTE));
		super.setEndDate(calendar.getTime());
		
		super.setCalEventType(event.getCalEventType());
		super.setCompleteRate(event.getCompleteRate());
		super.setCompleteRateInt(event.getCompleteRateInt());
		super.setContentId(event.getContentId());
		super.setDateInfo(event.getDateInfo());
		super.setDateRangeType(event.getDateRangeType());
		super.setEventType(event.getEventType());
		super.setFromId(event.getFromId());
		super.setFromType(event.getFromType());
		super.setPriorityType(event.getPriorityType());
		super.setProjectId(event.getProjectId());
		super.setPeriodicalStyle(event.getPeriodicalStyle());
		super.setRangeSort(event.getRangeSort());
		super.setRealEstimateTime(event.getRealEstimateTime());
		super.setReceiveMemberId(event.getReceiveMemberId());
		super.setReceiveMemberName(event.getReceiveMemberName());
		super.setShareTarget(event.getShareTarget());
		super.setShareType(event.getShareType());
		super.setSignifyType(event.getSignifyType());
		super.setStates(event.getStates());
		super.setSubject(event.getSubject());
		super.setTimeFlag(event.getTimeFlag());
		super.setTran(event.getTran());
		super.setTranMemberIds(event.getTranMemberIds());
		super.setWorkType(event.getWorkType());
	}
}
