package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:调配信息
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 14, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class StaffTransfer extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = -8094227891152086564L;

	/**
	 * 调配类型
	 */
	private StaffTransferType type;
	
	/**
	 * 申请时间
	 */
	private Date refer_time;
	
	/**
	 * 审批时间
	 */
	private Date deal_time;
	
	/**
	 * 调配时间
	 */
	private Date transfer_time;
	
	/**
	 * 调配前部门id
	 */
	private Long fromDepartment_id;
	
	/**
	 * 调配后部门id
	 */
	private Long toDepartment_id;
	
	/**
	 * 调配前岗位id
	 */
	private Long fromPost_id;
	
	/**
	 * 调配后岗位id
	 */
	private Long toPost_id;
	
	/**
	 * 调配前职务id
	 */
	private Long fromLevel_id;
	
	/**
	 * 调配后职务id
	 */
	private Long toLevel_id;
	
	/**
	 * 调配前人员类型
	 */
	private int fromMember_type;
	
	
	/**
	 * 调配后人员类型
	 */
	private int toMember_type;
	
	/**
	 * 调配前人员状态
	 */
	private int fromMember_state;
	
	
	/**
	 * 调配后人员状态
	 */
	private int toMember_state;
	
	/**
	 * 状态
	 */
	private int state;

	/**
	 * 调配原因
	 */
	private String reason;
	
	/**
	 * 部门意见
	 */
	private String deptOpinion;
	
	/**
	 * 单位意见
	 */
	private String accOpinion;
	
	/**
	 * 人员id
	 */
	private Long member_id;

	public Date getDeal_time() {
		return deal_time;
	}

	public void setDeal_time(Date deal_time) {
		this.deal_time = deal_time;
	}

	public Long getFromDepartment_id() {
		return fromDepartment_id;
	}

	public void setFromDepartment_id(Long fromDepartment_id) {
		this.fromDepartment_id = fromDepartment_id;
	}

	public Long getFromLevel_id() {
		return fromLevel_id;
	}

	public void setFromLevel_id(Long fromLevel_id) {
		this.fromLevel_id = fromLevel_id;
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

	public Long getFromPost_id() {
		return fromPost_id;
	}

	public void setFromPost_id(Long fromPost_id) {
		this.fromPost_id = fromPost_id;
	}

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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

	public Long getToDepartment_id() {
		return toDepartment_id;
	}

	public void setToDepartment_id(Long toDepartment_id) {
		this.toDepartment_id = toDepartment_id;
	}

	public Long getToLevel_id() {
		return toLevel_id;
	}

	public void setToLevel_id(Long toLevel_id) {
		this.toLevel_id = toLevel_id;
	}

	public int getToMember_state() {
		return toMember_state;
	}

	public void setToMember_state(int toMember_state) {
		this.toMember_state = toMember_state;
	}

	public int getToMember_type() {
		return toMember_type;
	}

	public void setToMember_type(int toMember_type) {
		this.toMember_type = toMember_type;
	}

	public Long getToPost_id() {
		return toPost_id;
	}

	public void setToPost_id(Long toPost_id) {
		this.toPost_id = toPost_id;
	}

	public StaffTransferType getType() {
		return type;
	}

	public void setType(StaffTransferType type) {
		this.type = type;
	}

	public Date getTransfer_time() {
		return transfer_time;
	}

	public void setTransfer_time(Date transfer_time) {
		this.transfer_time = transfer_time;
	}

	public String getDeptOpinion() {
		return deptOpinion;
	}

	public void setDeptOpinion(String deptOpinion) {
		this.deptOpinion = deptOpinion;
	}

	public String getAccOpinion() {
		return accOpinion;
	}

	public void setAccOpinion(String accOpinion) {
		this.accOpinion = accOpinion;
	}
	
}
