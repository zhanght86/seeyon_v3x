package com.seeyon.v3x.space.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.ObjectToXMLBase;

public class PortletParam extends ObjectToXMLBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2796081344832486062L;
	
	public PortletParam(Long fragmentId,String sectionId,int ordinal,String name,String subject,int valueType,String defaultValue,
			Boolean read,String valueRange,String paramValue,String id,String hiddenStr){
		this.fragmentId = fragmentId;
		this.sectionId = sectionId;
		this.ordinal = ordinal;
		this.name = name;
		this.subject = subject;
		this.valueType = valueType;
		this.defaultValue = defaultValue;
		this.read = read;
		this.valueRange = valueRange;
		this.paramValue = paramValue;
		this.id = id;
		this.hiddenStr = hiddenStr;
	}
	
	private Long fragmentId;
	private String sectionId;
	private int ordinal;
	private String name;
	private String subject;
	private int valueType;
	private String defaultValue;
	private Boolean read;
	private String valueRange;
	private String paramValue;
	private String id;
	private String hiddenStr;
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Long getFragmentId() {
		return fragmentId;
	}
	public void setFragmentId(Long fragmentId) {
		this.fragmentId = fragmentId;
	}
	public String getHiddenStr() {
		return hiddenStr;
	}
	public void setHiddenStr(String hiddenStr) {
		this.hiddenStr = hiddenStr;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrdinal() {
		return ordinal;
	}
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public Boolean getRead() {
		return read;
	}
	public void setRead(Boolean read) {
		this.read = read;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getValueRange() {
		return valueRange;
	}
	public void setValueRange(String valueRange) {
		this.valueRange = valueRange;
	}
	public int getValueType() {
		return valueType;
	}
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
	
	
}
