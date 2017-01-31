package com.seeyon.v3x.inquiry.webmdoel;

import java.util.Date;

import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 重新封装归档数据
 */
public class InquiryBasicData {
	private InquirySurveybasic basic;
	
	private Long id;
	private String name;
	private String type;
	private Date sendDate;
	private Date closeDate;
	private Long departmentId;
	
	private Long issuerId;


	public InquiryBasicData(InquirySurveybasic basic, OrgManager orgManager){
		this.basic = basic;
		this.id = basic.getId();
		this.name = basic.getSurveyName();
		this.type = basic.getInquirySurveytype().getTypeName();
		this.sendDate = basic.getSendDate();
		this.closeDate = basic.getCloseDate();
		this.issuerId = basic.getIssuerId();
		this.departmentId = basic.getDepartmentId();

	}


	public InquirySurveybasic getBasic() {
		return basic;
	}


	public void setBasic(InquirySurveybasic basic) {
		this.basic = basic;
	}


	public Date getCloseDate() {
		return closeDate;
	}


	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}


	public Long getDepartmentId() {
		return departmentId;
	}


	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Date getSendDate() {
		return sendDate;
	}


	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Long getIssuerId() {
		return issuerId;
	}


	public void setIssuerId(Long issuerId) {
		this.issuerId = issuerId;
	}


}
