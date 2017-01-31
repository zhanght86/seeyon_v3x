package com.seeyon.v3x.office.stock.domain;
/**
 * 办公用品管理申请单详细信息表对象定义类
 * 对应表：T_Stock_ApplyInfo
 */
import java.io.Serializable;

public class StockApplyInfo implements Serializable {

	
	private static final long serialVersionUID = 3275718436040455883L;
	
	private Long applyId;    //申请编号
	private Long stockId;    //用品编号
	private Integer applyCount;  //申请数量
	private Integer deleteFlag;		//0：正常（默认）；1：删除
	
	public Integer getApplyCount() {
		return applyCount;
	}
	public void setApplyCount(Integer applyCount) {
		this.applyCount = applyCount;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	
	
	
}
