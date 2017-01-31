package com.seeyon.v3x.office.stock.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;

public class StockInfo implements Serializable {

	private static final long serialVersionUID = -6228093689657122783L;
	
	private Long stockId; 		//编号
	private String stockName;       //名称
//	private String stockType;       //类别编号
     private OfficeTypeInfo officeType;   //类别
	private String stockModel;		//规格
	private String stockUnit;		//单位
	private Date stockDate;         //购置日期
	private Float stockPrice;       //购置价格
	private Integer stockCount;		//库存数量
	private Integer stockAvacount;	//可领取数量
	private Long stockRes;			//责任人
	private Date createDate;		//登陆日期
	private Date modifyDate;		//更新日期
	private Integer stockState;		//状态 0：可申领（默认）；1：不可申领
	private Integer deleteFlag;		//0：正常（默认）；1：删除
	private Long accountId;
	
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public Date getStockDate() {
		return stockDate;
	}
	public void setStockDate(Date stockDate) {
		this.stockDate = stockDate;
	}
	public Float getStockPrice() {
		return stockPrice;
	}
	public void setStockPrice(Float stockPrice) {
		this.stockPrice = stockPrice;
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
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public Integer getStockAvacount() {
		return stockAvacount;
	}
	public void setStockAvacount(Integer stockAvacount) {
		this.stockAvacount = stockAvacount;
	}
	public Integer getStockCount() {
		return stockCount;
	}
	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	public String getStockModel() {
		return stockModel;
	}
	public void setStockModel(String stockModel) {
		this.stockModel = stockModel;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public Long getStockRes() {
		return stockRes;
	}
	public void setStockRes(Long stockRes) {
		this.stockRes = stockRes;
	}
	public Integer getStockState() {
		return stockState;
	}
	public void setStockState(Integer stockState) {
		this.stockState = stockState;
	}
//	public String getStockType() {
//		return stockType;
//	}
//	public void setStockType(String stockType) {
//		this.stockType = stockType;
//	}
	public String getStockUnit() {
		return stockUnit;
	}
	public void setStockUnit(String stockUnit) {
		this.stockUnit = stockUnit;
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
