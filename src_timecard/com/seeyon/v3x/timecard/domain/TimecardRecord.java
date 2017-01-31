package com.seeyon.v3x.timecard.domain;


import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.seeyon.v3x.common.domain.BaseModel;


public class TimecardRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String workDate;
	private String ondutyTime;
	private String offdutyTime;
    private long memberId;
	private int ondutyType;

    public TimecardRecord(){
	}
    public String getWorkDate() {
		return this.workDate;
	}
	public void setWorkDate(String workDate) {
		this.workDate = workDate;
	}

	public long getMemberId() {
		return this.memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public String getOndutyTime() {
		return this.ondutyTime;
	}
	public void setOndutyTime(String ondutyTime) {
		this.ondutyTime = ondutyTime;
	}
	public String getOffdutyTime() {
		return this.offdutyTime;
	}
	public void setOffdutyTime(String offdutyTime) {
		this.offdutyTime = offdutyTime;
	}
	public int getOndutyType(){
		return this.ondutyType;
	}
	public void setOndutyType(int ondutyType){
		this.ondutyType = ondutyType;
	}
    public String toString(){
		return new ToStringBuilder(this).append("id", getId()).toString();
	}
}
