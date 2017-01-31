package com.seeyon.v3x.edoc.webmodel;

public class EdocOpinionDisplayConfig {
	
	//是否只显示最后一次处理意见
	private boolean onlyShowLastOpinion = false;
	//是否显示部门名
	private boolean showDeptName = false;
	//是否显示单位名
	private boolean showUnitName = false;//魏俊标添加
	
	private boolean showPerson = false;//只显示姓名
	
	public boolean isShowPerson() {
		return showPerson;
	}

	public void setShowPerson(boolean showPerson) {
		this.showPerson = showPerson;
	}

	public boolean isShowUnitName() {
		return showUnitName;
	}

	public void setShowUnitName(boolean showUnitName) {
		this.showUnitName = showUnitName;
	}

	//时间显示格式
	public int showDate ;
	
	public static enum DateFormat{
		dateTime,
		date
	}

	public boolean isOnlyShowLastOpinion() {
		return onlyShowLastOpinion;
	}

	public void setOnlyShowLastOpinion(boolean onlyShowLastOpinion) {
		this.onlyShowLastOpinion = onlyShowLastOpinion;
	}

	public boolean isShowDeptName() {
		return showDeptName;
	}

	public void setShowDeptName(boolean showDeptName) {
		this.showDeptName = showDeptName;
	}

	public int getShowDate() {
		return showDate;
	}

	public void setShowDate(int showDate) {
		this.showDate = showDate;
	}

}
