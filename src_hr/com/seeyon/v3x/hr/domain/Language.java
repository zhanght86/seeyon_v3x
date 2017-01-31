package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * <p/> Title:语言种类
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jun 13, 2007
 * </p>
 * 
 * @author gaiht
 */
public class Language extends BaseModel implements java.io.Serializable{
	
	private static final long serialVersionUID = 411185004755098325L;
	private String languageName;   //语言名称
	private String languageShortName;  //语言简称
	private String languageLabel;    //语言标签
	
	
	public String getLanguageLabel() {
		return languageLabel;
	}
	public void setLanguageLabel(String languageLabel) {
		this.languageLabel = languageLabel;
	}
	public String getLanguageName() {
		return languageName;
	}
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
	public String getLanguageShortName() {
		return languageShortName;
	}
	public void setLanguageShortName(String languageShortName) {
		this.languageShortName = languageShortName;
	}
	
}
