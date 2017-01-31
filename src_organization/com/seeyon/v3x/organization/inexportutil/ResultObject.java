package com.seeyon.v3x.organization.inexportutil;

import  java.io.*;
/**
 * 主要用来做结果显示
 * @author kyt
 *
 */
public class ResultObject implements Serializable {
	static final long serialVersionUID=1L;
	
	private String name;
	private String success;
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	
	
}
