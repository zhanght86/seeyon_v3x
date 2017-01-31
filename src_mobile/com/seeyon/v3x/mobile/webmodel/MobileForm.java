package com.seeyon.v3x.mobile.webmodel;

import java.util.List;
import java.util.Map;

public class MobileForm {

	private String fileName;
	private String fileType;
	private String value;
	private String fileId;
	private  List<Map<String,String>> subValues;
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public List<Map<String, String>> getSubValues() {
		return subValues;
	}
	public void setSubValues(List<Map<String, String>> subValues) {
		this.subValues = subValues;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
