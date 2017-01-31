package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

public class PageLabel extends BaseModel implements java.io.Serializable{
	
	private static final long serialVersionUID = -2546843030477412161L;
	private String language;
	private String pageLabelValue;
	private Long page_id;
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Long getPage_id() {
		return page_id;
	}
	public void setPage_id(Long page_id) {
		this.page_id = page_id;
	}
	public String getPageLabelValue() {
		return pageLabelValue;
	}
	public void setPageLabelValue(String pageLabelValue) {
		this.pageLabelValue = pageLabelValue;
	}

}
