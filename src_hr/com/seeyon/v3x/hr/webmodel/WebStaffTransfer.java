package com.seeyon.v3x.hr.webmodel;

import java.util.Date;

import com.seeyon.v3x.hr.domain.StaffTransferType;

public class WebStaffTransfer {
	private Long id;
	private String code;//人员编号
	private String name;//姓名
	private Long member_id;//人员id
	private int transferType;//变动类型
	private int state;//状态
	private Date refer_time;//提交时间
	private Date deal_time;//审批时间
	private Date transfer_time;//调配日期
	private String fromDepartment_name;//调配前部门名称
	private String toDepartment_name;//调配后部门名称
	private String fromLevel_name;//调配前职务名称
	private String toLevel_name;//调配后职务名称
	private String fromPost_name;//调配前岗位名称
	private String toPost_name;//调配后岗位名称
	private int fromMember_type;//调配前人员类别
	private int fromMember_state;//调配前人员状态
//	private Long fromDepartment_id;//调配前部门id
//	private Long toDepartment_id;//调配后部门id
//	private Long fromPost_id;//调配前岗位id
//	private Long toPost_id;//调配后岗位id
//	private Long fromLevel_id;//调配前职务id
//	private Long toLevel_id;//调配后职务id
	private StaffTransferType staffTransferType;//变动类型
	private String fromAccount_name;//调配前单位名称
	private String toAccount_name;//调配后单位名称
	
	public String getFromAccount_name() {
		return fromAccount_name;
	}
	public void setFromAccount_name(String fromAccount_name) {
		this.fromAccount_name = fromAccount_name;
	}
	public String getToAccount_name() {
		return toAccount_name;
	}
	public void setToAccount_name(String toAccount_name) {
		this.toAccount_name = toAccount_name;
	}
	public StaffTransferType getStaffTransferType() {
		return staffTransferType;
	}
	public void setStaffTransferType(StaffTransferType staffTransferType) {
		this.staffTransferType = staffTransferType;
	}
	public int getFromMember_state() {
		return fromMember_state;
	}
	public void setFromMember_state(int fromMember_state) {
		this.fromMember_state = fromMember_state;
	}
	public int getFromMember_type() {
		return fromMember_type;
	}
	public void setFromMember_type(int fromMember_type) {
		this.fromMember_type = fromMember_type;
	}
	//	public Long getFromDepartment_id() {
//		return fromDepartment_id;
//	}
//	public void setFromDepartment_id(Long fromDepartment_id) {
//		this.fromDepartment_id = fromDepartment_id;
//	}
//	public Long getFromLevel_id() {
//		return fromLevel_id;
//	}
//	public void setFromLevel_id(Long fromLevel_id) {
//		this.fromLevel_id = fromLevel_id;
//	}
//	public Long getFromPost_id() {
//		return fromPost_id;
//	}
//	public void setFromPost_id(Long fromPost_id) {
//		this.fromPost_id = fromPost_id;
//	}
//	public Long getToDepartment_id() {
//		return toDepartment_id;
//	}
//	public void setToDepartment_id(Long toDepartment_id) {
//		this.toDepartment_id = toDepartment_id;
//	}
//	public Long getToLevel_id() {
//		return toLevel_id;
//	}
//	public void setToLevel_id(Long toLevel_id) {
//		this.toLevel_id = toLevel_id;
//	}
//	public Long getToPost_id() {
//		return toPost_id;
//	}
//	public void setToPost_id(Long toPost_id) {
//		this.toPost_id = toPost_id;
//	}
	public String getFromDepartment_name() {
		return fromDepartment_name;
	}
	public void setFromDepartment_name(String fromDepartment_name) {
		this.fromDepartment_name = fromDepartment_name;
	}
	public String getFromLevel_name() {
		return fromLevel_name;
	}
	public void setFromLevel_name(String fromLevel_name) {
		this.fromLevel_name = fromLevel_name;
	}
	public String getFromPost_name() {
		return fromPost_name;
	}
	public void setFromPost_name(String fromPost_name) {
		this.fromPost_name = fromPost_name;
	}
	public String getToDepartment_name() {
		return toDepartment_name;
	}
	public void setToDepartment_name(String toDepartment_name) {
		this.toDepartment_name = toDepartment_name;
	}
	public String getToLevel_name() {
		return toLevel_name;
	}
	public void setToLevel_name(String toLevel_name) {
		this.toLevel_name = toLevel_name;
	}
	public String getToPost_name() {
		return toPost_name;
	}
	public void setToPost_name(String toPost_name) {
		this.toPost_name = toPost_name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getDeal_time() {
		return deal_time;
	}
	public void setDeal_time(Date deal_time) {
		this.deal_time = deal_time;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMember_id() {
		return member_id;
	}
	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getRefer_time() {
		return refer_time;
	}
	public void setRefer_time(Date refer_time) {
		this.refer_time = refer_time;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getTransferType() {
		return transferType;
	}
	public void setTransferType(int transferType) {
		this.transferType = transferType;
	}
	public Date getTransfer_time() {
		return transfer_time;
	}
	public void setTransfer_time(Date transfer_time) {
		this.transfer_time = transfer_time;
	}
	
}
