package com.seeyon.v3x.office.common.domain;
/**
 * 申请单一览表
 * 对应表：T_ApplyList
 */
import java.io.Serializable;
import java.util.Date;

public class OfficeApply implements Serializable {

	private static final long serialVersionUID = -7749055230242427993L;
	
	private Long applyId;   		//编号
	private Long applyUserName;  	//申请人
	private Long applyDepId;     	//部门ID
	
	private Long applyUser;			//使用人
	private Long applyUseDep;		//使用人部门
	
	private Date applyDate;			//申请日期
	private Integer applyState;  	//申请状态 1:待审（默认）；2：审核通过；3：未通过
	private String applyType;    	//申请类型 1:车辆；2：办公设备；3：办公用品；4：图书资料
	private String applyMemo;    	//备注说明
	private Long applyManager;	 	//管理员
	private Long applyExam;      	//审批人
	private Date auditTime;		 	//审批日期
	private Integer deleteFlag;  	//删除标识  0：正常（默认）；1：删除
	
	
	
	public Long getApplyUseDep() {
		return applyUseDep;
	}
	public void setApplyUseDep(Long applyUseDep) {
		this.applyUseDep = applyUseDep;
	}
	public Long getApplyUser() {
		return applyUser;
	}
	public void setApplyUser(Long applyUser) {
		this.applyUser = applyUser;
	}
	public Long getApplyManager() {
		return applyManager;
	}
	public void setApplyManager(Long applyManager) {
		this.applyManager = applyManager;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public Long getApplyDepId() {
		return applyDepId;
	}
	public void setApplyDepId(Long applyDepId) {
		this.applyDepId = applyDepId;
	}
	public Long getApplyExam() {
		return applyExam;
	}
	public void setApplyExam(Long applyExam) {
		this.applyExam = applyExam;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public String getApplyMemo() {
		return applyMemo;
	}
	public void setApplyMemo(String applyMemo) {
		this.applyMemo = applyMemo;
	}
	public Integer getApplyState() {
		return applyState;
	}
	public void setApplyState(Integer applyState) {
		this.applyState = applyState;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public Long getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(Long applyUserName) {
		this.applyUserName = applyUserName;
	}
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	
	
	
}
