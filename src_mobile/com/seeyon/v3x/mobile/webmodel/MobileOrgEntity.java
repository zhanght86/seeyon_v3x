package com.seeyon.v3x.mobile.webmodel;

import com.seeyon.v3x.util.Strings;

public class MobileOrgEntity {
	private String name;
	private Long id;
	private int type;//0-部门 1-人员 2-单位
	private String telNum;
	private boolean canCall;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTelNum() {
		return telNum;
	}
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	} 
	public boolean getCanCall(){
		return Strings.isNotBlank(this.getTelNum());
	}

}
