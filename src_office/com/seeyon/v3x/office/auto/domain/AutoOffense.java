package com.seeyon.v3x.office.auto.domain;

import java.io.Serializable;
import java.util.Date;

public class AutoOffense implements Serializable {

	private static final long serialVersionUID = 1421478725634791013L;
	
	private Long applyId;       //编号
	private String autoId;        //车牌号
	private String regTime;         //违章时间
	private String regAddress;    //违章地点
	private String regBehavior;   //违章行为  
	private Integer regSituation;  //处理情况  0：未处理；1：处理中；2：处理完毕
	private Long autoManager;      //管理者
	
	private String regMemo;       //备注
	private Date createDate;      //登记日期
	private Date updateDate;      //更新日期
	private Integer deleteFlag;   //删除标志  0：正常（默认）；1：删除
	
	
	public Long getAutoManager() {
		return autoManager;
	}
	public void setAutoManager(Long autoManager) {
		this.autoManager = autoManager;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public String getRegAddress() {
		return regAddress;
	}
	public void setRegAddress(String regAddress) {
		this.regAddress = regAddress;
	}
	public String getRegBehavior() {
		return regBehavior;
	}
	public void setRegBehavior(String regBehavior) {
		this.regBehavior = regBehavior;
	}
	public String getRegMemo() {
		return regMemo;
	}
	public void setRegMemo(String regMemo) {
		this.regMemo = regMemo;
	}
	public Integer getRegSituation() {
		return regSituation;
	}
	public void setRegSituation(Integer regSituation) {
		this.regSituation = regSituation;
	}
	public String getRegTime() {
		return regTime;
	}
	public void setRegTime(String regTime) {
		this.regTime = regTime;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	
}
