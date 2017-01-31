package com.seeyon.v3x.interfaces.domain;

import java.util.Date;

import com.seeyon.v3x.util.Datetimes;

public class WebServiceResult {
	
	private String app;
	
	private String businessCode;
	
	private Date date;
	
	private String subject;
	
	private String applicant;
	
	private String result;

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	/*
	 * 目前简写，以后改为XML格式的结果
	 */
	public String toXml(){
		String str=Datetimes.format(date, "yyyy-MM-dd hh:mm:ss");
		StringBuffer strBuffer=new StringBuffer();
		strBuffer.append(businessCode).append(";")
		.append(result).append(str).append(";")
		.append(app).append(";")
		.append(subject).append(";")
		.append(str).append(";");
		return strBuffer.toString();
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	
	

}
