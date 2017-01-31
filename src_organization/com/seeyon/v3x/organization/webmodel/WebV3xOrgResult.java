package com.seeyon.v3x.organization.webmodel;

import java.util.List;

public class WebV3xOrgResult {
	
	private String str1;
	
	private String str2;

	private List<String[]> validateList;
	private List<String[]> moveLogList;
	
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	public List<String[]> getValidateList() {
		return validateList;
	}
	public void setValidateList(List<String[]> validateList) {
		this.validateList = validateList;
	}
	public List<String[]> getMoveLogList() {
		return moveLogList;
	}
	public void setMoveLogList(List<String[]> moveLogList) {
		this.moveLogList = moveLogList;
	}
}
