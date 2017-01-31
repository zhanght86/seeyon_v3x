package com.seeyon.v3x.office.auto.domain;
/**
 * 车辆详细信息对象
 * 表示车辆详细的信息（车辆管理模块使用）
 * 对应表 M_Auto_Info
 */
import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;

public class AutoInfo implements Serializable{

	
	private static final long serialVersionUID = -8314212827180513904L;

	private String autoId;      	//车牌号
	private String autoName;    //车辆品牌
	private String autoModel;   //车辆类型
	
//	private String autoType;   
    private OfficeTypeInfo officeType;   //车辆型号
	private String autoEngine;   //发动机号
	private String autoCode;     //车辆识别号
	private Integer autoStatus;   //车辆状态   0：正常（默认）；1：检修中；2：扣押中；3：报废
	private Date autoDate;        //购车日期
	private Float autoPrice;	  //购车价格
	private Long autoDriver;      //默认司机
	private String autoInsurer;      //保险公司
	private String autoInsurNo;      //保险证号
	private Date autoInsurDate;      //保险期间
	private String autoInsurDetail;   //保险内容
	private Integer autoState;        //使用状态       0：未安排；1：已安排
	private String autoMemo;          //备注
	private Long autoManager;             //管理员
	private Date createDate;          //录入日期
	private Date modifyDate;          //更新日期
	private Integer deleteFlag;       //删除标志  0：正常（默认）；1：删除
	private Long autoDept;             //部门
	
	
    private Long domainId;
    
 	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Long getAutoDept() {
		return autoDept;
	}
	public void setAutoDept(Long autoDept) {
		this.autoDept = autoDept;
	}
	public Date getAutoDate() {
		return autoDate;
	}
	public void setAutoDate(Date autoDate) {
		this.autoDate = autoDate;
	}
	public Long getAutoDriver() {
		return autoDriver;
	}
	public void setAutoDriver(Long autoDriver) {
		this.autoDriver = autoDriver;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public Date getAutoInsurDate() {
		return autoInsurDate;
	}
	public void setAutoInsurDate(Date autoInsurDate) {
		this.autoInsurDate = autoInsurDate;
	}
	public String getAutoInsurDetail() {
		return autoInsurDetail;
	}
	public void setAutoInsurDetail(String autoInsurDetail) {
		this.autoInsurDetail = autoInsurDetail;
	}
	public String getAutoInsurer() {
		return autoInsurer;
	}
	public void setAutoInsurer(String autoInsurer) {
		this.autoInsurer = autoInsurer;
	}
	public String getAutoInsurNo() {
		return autoInsurNo;
	}
	public void setAutoInsurNo(String autoInsurNo) {
		this.autoInsurNo = autoInsurNo;
	}
	public String getAutoMemo() {
		return autoMemo;
	}
	public void setAutoMemo(String autoMemo) {
		this.autoMemo = autoMemo;
	}
	public String getAutoName() {
		return autoName;
	}
	public void setAutoName(String autoName) {
		this.autoName = autoName;
	}
	public Long getAutoManager() {
		return autoManager;
	}
	public void setAutoManager(Long autoManager) {
		this.autoManager = autoManager;
	}
	public Integer getAutoState() {
		return autoState;
	}
	public void setAutoState(Integer autoState) {
		this.autoState = autoState;
	}
	public Integer getAutoStatus() {
		return autoStatus;
	}
	public void setAutoStatus(Integer autoStatus) {
		this.autoStatus = autoStatus;
	}
//	public String getAutoType() {
//		return autoType;
//	}
//	public void setAutoType(String autoType) {
//		this.autoType = autoType;
//	}
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
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getAutoCode() {
		return autoCode;
	}
	public void setAutoCode(String autoCode) {
		this.autoCode = autoCode;
	}
	public String getAutoEngine() {
		return autoEngine;
	}
	public void setAutoEngine(String autoEngine) {
		this.autoEngine = autoEngine;
	}
	public String getAutoModel() {
		return autoModel;
	}
	public void setAutoModel(String autoModel) {
		this.autoModel = autoModel;
	}
	public Float getAutoPrice() {
		return autoPrice;
	}
	public void setAutoPrice(Float autoPrice) {
		this.autoPrice = autoPrice;
	}

    public OfficeTypeInfo getOfficeType()
    {
        return officeType;
    }

    public void setOfficeType(OfficeTypeInfo officeType)
    {
        this.officeType = officeType;
    }
	
	
	
}
