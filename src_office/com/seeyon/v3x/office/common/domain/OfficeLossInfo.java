package com.seeyon.v3x.office.common.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 丢失报损信息对象
 * @author xuning
 *　对应表M_Loss_Info
 */
public class OfficeLossInfo implements Serializable {

	private static final long serialVersionUID = 6328117198980003050L;

	private Long lossId;			//编号
	private String resourceId;		//资源编号
	private String resourceName;	//资源名称
	private String lossField;		//范畴 1:车辆；2：办公设备；3：办公用品；4：图书资料
	private String lossDiff;		//区分　1:丢失；2：破损
	private Integer lossCount;		//数量
	private Date createDate;		//登录日期
	private Date modifyDate;		//更新日期
	private Long createUser;		//登记人
	private Long lossManager;		//管理者
	private String lossMemo;		//备注
	private Integer deleteFlag;		//删除标识
	
    private Long domainId;
    
 	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
	
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public Integer getLossCount() {
		return lossCount;
	}
	public void setLossCount(Integer lossCount) {
		this.lossCount = lossCount;
	}
	public String getLossDiff() {
		return lossDiff;
	}
	public void setLossDiff(String lossDiff) {
		this.lossDiff = lossDiff;
	}
	public String getLossField() {
		return lossField;
	}
	public void setLossField(String lossField) {
		this.lossField = lossField;
	}
	public Long getLossId() {
		return lossId;
	}
	public void setLossId(Long lossId) {
		this.lossId = lossId;
	}
	public Long getLossManager() {
		return lossManager;
	}
	public void setLossManager(Long lossManager) {
		this.lossManager = lossManager;
	}
	public String getLossMemo() {
		return lossMemo;
	}
	public void setLossMemo(String lossMemo) {
		this.lossMemo = lossMemo;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	
	
	
	
}
