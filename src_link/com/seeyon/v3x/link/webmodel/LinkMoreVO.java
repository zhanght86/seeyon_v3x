package com.seeyon.v3x.link.webmodel;

import java.util.List;

import com.seeyon.v3x.link.domain.LinkSystem;

public class LinkMoreVO {
	private String categoryName;
	private List<LinkShowVO> links;
	
	public LinkMoreVO(List<LinkShowVO> links){
		this.links = links;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<LinkShowVO> getLinks() {
		return links;
	}

	public void setLinks(List<LinkShowVO> links) {
		this.links = links;
	}

	
	
}
