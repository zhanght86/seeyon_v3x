package com.seeyon.v3x.calendar.domain;


/**
 * The persistent class for the cal_event database table.
 * 
 * @author BEA Workshop Studio
 */
public class CalEvent extends AbstractCalEvent implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6674580210009342525L;
	
	public CalEvent(){
		
	}
	
	private Long periodicalId;//周期性事件id
	
	public Long getPeriodicalId() {
		return periodicalId;
	}


	public void setPeriodicalId(Long periodicalId) {
		this.periodicalId = periodicalId;
	}


	public CalEvent(AbstractCalEvent event){
		super.setId(event.getId());
		super.setAlarmDate(event.getAlarmDate());
		super.setAlarmFlag(event.isAlarmFlag());
		super.setAttachmentsFlag(event.getAttachmentsFlag());
		super.setBeforendAlarm(event.getBeforendAlarm());
		super.setBeforeTime(event.getBeforeTime());
		super.setBeginDate(event.getBeginDate());
		super.setCalEventType(event.getCalEventType());
		super.setCompleteRate(event.getCompleteRate());
		super.setCompleteRateInt(event.getCompleteRateInt());
		super.setContentId(event.getContentId());
		super.setCreateDate(event.getCreateDate());
		super.setCreateUserId(event.getCreateUserId());
		super.setCreateUserName(event.getCreateUserName());
		super.setDateInfo(event.getDateInfo());
		super.setDateRangeType(event.getDateRangeType());
		super.setEndDate(event.getEndDate());
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
		super.setUpdateDate(event.getUpdateDate());
	}
	
	public void updateByEvent(AbstractCalEvent event){
		super.setAlarmDate(event.getAlarmDate());
		super.setAlarmFlag(event.isAlarmFlag());
		super.setAttachmentsFlag(event.getAttachmentsFlag());
		super.setBeforendAlarm(event.getBeforendAlarm());
		super.setBeforeTime(event.getBeforeTime());
		//super.setBeginDate(event.getBeginDate());
		super.setCalEventType(event.getCalEventType());
		super.setCompleteRate(event.getCompleteRate());
		super.setCompleteRateInt(event.getCompleteRateInt());
		super.setContentId(event.getContentId());
		super.setCreateDate(event.getCreateDate());
		super.setCreateUserId(event.getCreateUserId());
		super.setCreateUserName(event.getCreateUserName());
		super.setDateInfo(event.getDateInfo());
		super.setDateRangeType(event.getDateRangeType());
		//super.setEndDate(event.getEndDate());
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
		super.setUpdateDate(event.getUpdateDate());
	}
}