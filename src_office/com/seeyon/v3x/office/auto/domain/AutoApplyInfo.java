package com.seeyon.v3x.office.auto.domain;

import java.io.Serializable;

/**
 * 车辆管理申请单详细信息表
 * 对应表： T_Auto_ApplyInfo
 * @author lindx
 *
 */
public class AutoApplyInfo implements Serializable  {

	private static final long serialVersionUID = 7543712441520474682L;
	
	private Long applyId;   //编号
	private String autoId;      //车牌号
	private String autoDepartTime; //预定出车日期
	private String autoBackTime;	 //预定归车日期
	private String autoOrigin;   //用车理由
	private String autoDep;      //出发地
	private String autoDes;     //目的地
	private Integer deleteFlag;  //删除标识
	
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public String getAutoBackTime() {
		return autoBackTime;
	}
	public void setAutoBackTime(String autoBackTime) {
		this.autoBackTime = autoBackTime;
	}
	public String getAutoDepartTime() {
		return autoDepartTime;
	}
	public void setAutoDepartTime(String autoDepartTime) {
		this.autoDepartTime = autoDepartTime;
	}
	
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public String getAutoDep() {
		return autoDep;
	}
	public void setAutoDep(String autoDep) {
		this.autoDep = autoDep;
	}
	public String getAutoDes() {
		return autoDes;
	}
	public void setAutoDes(String autoDes) {
		this.autoDes = autoDes;
	}
	public String getAutoOrigin() {
		return autoOrigin;
	}
	public void setAutoOrigin(String autoOrigin) {
		this.autoOrigin = autoOrigin;
	}
	
	
}
