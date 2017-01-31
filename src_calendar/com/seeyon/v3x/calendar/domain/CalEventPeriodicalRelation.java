package com.seeyon.v3x.calendar.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.util.Datetimes;
/**
 * 周期性事件与日程事件关系中间表
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-6-11
 */
public class CalEventPeriodicalRelation extends BaseModel {

	public Long getCalEventId() {
		return calEventId;
	}

	public void setCalEventId(Long calEventId) {
		this.calEventId = calEventId;
	}

	public Long getCalEventPeriodicalInfoId() {
		return calEventPeriodicalInfoId;
	}

	public void setCalEventPeriodicalInfoId(Long calEventPeriodicalInfoId) {
		this.calEventPeriodicalInfoId = calEventPeriodicalInfoId;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -3566386466046142232L;

	//事件id
	private Long calEventId;
	//周期事件id
	private Long calEventPeriodicalInfoId;
	
	private Long memberId;
	
	//周期事件应该被创建的日期，精确到日
	private Date createDate;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		//确保日期精确到天
		this.createDate = Datetimes.getTodayFirstTime(createDate);
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	
}
