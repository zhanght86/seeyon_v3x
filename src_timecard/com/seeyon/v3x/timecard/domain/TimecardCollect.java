package com.seeyon.v3x.timecard.domain;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the timecard_collect database table.
 * 
 * @author BEA Workshop Studio
 */
public class TimecardCollect extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private Long id;
	private String beginTime;
	private String endTime;
	private Boolean ispass;
	private long memberId;
	private String remark;
	private boolean state;
	private String workDate;

    public TimecardCollect() {
    }

	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getBeginTime() {
		return this.beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return this.endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Boolean getIspass() {
		return this.ispass;
	}
	public void setIspass(Boolean ispass) {
		this.ispass = ispass;
	}

	public long getMemberId() {
		return this.memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean getState() {
		return this.state;
	}
	public void setState(boolean state) {
		this.state = state;
	}

	public String getWorkDate() {
		return this.workDate;
	}
	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}