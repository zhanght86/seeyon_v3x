package com.seeyon.v3x.office.common.domain;
/**
 * 类别信息表 PO定义类
 * 对应表：M_Type_Info
 */
import java.io.Serializable;
import java.util.Date;

public class OfficeTypeInfo implements Serializable {

	private static final long serialVersionUID = -5984623916890534178L;
	
	private Long typeId;		//流水号
	private String modelId;    //类别编号
	private String typeInfo;  //类别名称
	private Long departId;		//单位管理员所属的单位ID
	private Date createDate;		//登录日期
	private Date modifyDate;		//更新日期
	private Integer deleteFlag;		//删除标识
	
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public Long getDepartId() {
		return departId;
	}
	public void setDepartId(Long departId) {
		this.departId = departId;
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
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getTypeInfo() {
		return typeInfo;
	}
	public void setTypeInfo(String typeInfo) {
		this.typeInfo = typeInfo;
	}
	
	
}
