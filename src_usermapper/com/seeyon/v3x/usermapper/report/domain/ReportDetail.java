package com.seeyon.v3x.usermapper.report.domain;

import java.util.Date;

import org.springframework.util.StringUtils;

public class ReportDetail {
	long id;
	
	long recordId;
	
	String data="";
	
	String action="";
	
	String memo="";
	
	Date dt=new Date();

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	public void appendMemo(String word){
		this.memo=this.appendString(this.memo, word);
	}
	public void appendData(String word){
		this.data=this.appendString(this.data, word);
	}
	public void appendAction(String word){
		this.action=this.appendString(this.action, word);
	}
	
	protected String appendString(String org,String word){
		StringBuffer work=new StringBuffer();
		if(StringUtils.hasText(org)){
			work.append(org);
		}else{
			work.append("");
		}
		
		if(StringUtils.hasText(word)){
			work.append("  ");
			work.append(word);
		}
		
		return work.toString();
	}
}//end class
