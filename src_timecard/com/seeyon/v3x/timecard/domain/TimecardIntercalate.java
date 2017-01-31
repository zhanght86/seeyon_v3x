package com.seeyon.v3x.timecard.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.seeyon.v3x.common.domain.BaseModel;

public class TimecardIntercalate extends BaseModel implements Serializable{

    private static final long serialVersionUID = 1L;
    private java.lang.Long memberId;
	private java.lang.String workDate;
	private java.lang.Integer isWork;
	private java.sql.Timestamp timecardTime;
    
    public TimecardIntercalate() {
    }
	public long getMemberId() {
		return this.memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}
	public java.sql.Timestamp getTimecardTime(){
		return this.timecardTime;
	}
	public void setTimecardTime(java.sql.Timestamp timecardTime){
		this.timecardTime = timecardTime;
	}
	public java.lang.String getWorkDate(){
		return this.workDate;
	}
	public void setWorkDate(java.lang.String workDate){
		this.workDate = workDate;
	}
	public int getIsWork(){
		return this.isWork;
	}
	public void setIsWork(int isWork){
		this.isWork = isWork;
	}
	public String toString() {
        return new ToStringBuilder(this).append("id", getId()).toString();
    }
}
