package com.seeyon.v3x.office.stock.domain;

import java.io.Serializable;

public class stockInfoDto implements Serializable {

	
	private static final long serialVersionUID = -8777141276466842543L;

	private Long applyId;
	private Long stockId; 				//用品编号
	private String stockName;       	//用品名称
	private String applyUserName;		//用户名称
	private String applyDepartName;		//所属部门
	private Integer applyState;  		//申请状态 1:待审（默认）；2：审核通过；3：未通过
	
	public String getApplyDepartName() {
		return applyDepartName;
	}
	public void setApplyDepartName(String applyDepartName) {
		this.applyDepartName = applyDepartName;
	}
	public Long getApplyId() {
		return applyId;
	}
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}
	public Integer getApplyState() {
		return applyState;
	}
	public void setApplyState(Integer applyState) {
		this.applyState = applyState;
	}
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	
	public stockInfoDto(Long applyId, Long stockId, String stockName, String applyUserName, String applyDepartName, Integer applyState) {
		this.applyId = applyId;
		this.stockId = stockId;
		this.stockName = stockName;
		this.applyUserName = applyUserName;
		this.applyDepartName = applyDepartName;
		this.applyState = applyState;
	}

	
}
