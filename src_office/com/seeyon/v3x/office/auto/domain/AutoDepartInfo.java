package com.seeyon.v3x.office.auto.domain;

/**
 * 出车信息表
 * 对应表：T_Auto_DepartInfo
 */
import java.io.Serializable;

public class AutoDepartInfo implements Serializable{

	private static final long serialVersionUID = -906522996668362722L;
	
	private Long applyId;   //编号
	private String autoId;      //车牌号
	private String autoDepartTime; //出车日期
	private String autoBackTime;	 //归车日期
	private String autoPerNum;   //乘车人数
	private Float autoMileAge;   //公里数
	private Float autoFuel;      //耗油量
	
	private Long autoDriver;     //驾驶员
	private Float fuelPrice;	 //油费
	private Float roadPrice;	 //过路费
	private Float otherPrice;	 //其他费用
	
	private String autoMemo;     //备注
	private Integer deleteFlag;  //删除标识
	
	
	public Long getAutoDriver() {
		return autoDriver;
	}
	public void setAutoDriver(Long autoDriver) {
		this.autoDriver = autoDriver;
	}
	public Float getFuelPrice() {
		return fuelPrice;
	}
	public void setFuelPrice(Float fuelPrice) {
		this.fuelPrice = fuelPrice;
	}
	public Float getOtherPrice() {
		return otherPrice;
	}
	public void setOtherPrice(Float otherPrice) {
		this.otherPrice = otherPrice;
	}
	public Float getRoadPrice() {
		return roadPrice;
	}
	public void setRoadPrice(Float roadPrice) {
		this.roadPrice = roadPrice;
	}
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
	public Float getAutoFuel() {
		return autoFuel;
	}
	public void setAutoFuel(Float autoFuel) {
		this.autoFuel = autoFuel;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getAutoMemo() {
		return autoMemo;
	}
	public void setAutoMemo(String autoMemo) {
		this.autoMemo = autoMemo;
	}
	public Float getAutoMileAge() {
		return autoMileAge;
	}
	public void setAutoMileAge(Float autoMileAge) {
		this.autoMileAge = autoMileAge;
	}
	public String getAutoPerNum() {
		return autoPerNum;
	}
	public void setAutoPerNum(String autoPerNum) {
		this.autoPerNum = autoPerNum;
	}
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
}
