package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

public class PageProperties extends BaseModel implements java.io.Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3314694978375516851L;
	private int property_ordering;
	private Page page;
	private PageProperty pageProperty;

	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	public PageProperty getPageProperty() {
		return pageProperty;
	}
	public void setPageProperty(PageProperty pageProperty) {
		this.pageProperty = pageProperty;
	}
	public int getProperty_ordering() {
		return property_ordering;
	}
	public void setProperty_ordering(int property_ordering) {
		this.property_ordering = property_ordering;
	}
}
