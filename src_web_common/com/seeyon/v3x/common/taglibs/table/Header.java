package com.seeyon.v3x.common.taglibs.table;

import static com.seeyon.v3x.common.taglibs.util.ResourceUtil.getNodeAttribute;

import java.io.Serializable;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-1
 */
public class Header implements Serializable {

	private static final long serialVersionUID = 6249881322336233331L;

	private String label;

	private String type;

	private String width;

	private String alt;

	private String align;

	private String className;
	
	private String orderBy;
	
	private boolean nowarp;
	
	private boolean widthFixed;
	
	public boolean isWidthFixed() {
		return widthFixed;
	}
	public String getWidthFixed() {
		if(widthFixed){
			return getNodeAttribute("widthFixed", widthFixed==true?"true":"false");
		}
		
		return "";
	}	public void setWidthFixed(boolean widthFixed) {
		this.widthFixed = widthFixed;
	}

	public String getClassName() {
		return getNodeAttribute("class", className);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAlt() {
		return getNodeAttribute("title", alt);
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getLabel() {
		if (label == null || label.trim().equals("")) {
			return "&nbsp;";
		}

		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return getNodeAttribute("type", type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWidth() {
		return getNodeAttribute("width", width);
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getAlign() {
		return getNodeAttribute("align", align);
	}
	
	public String getStyleAlign() {
		return align;
	}
	
	public void setAlign(String align) {
		this.align = align;
	}

	public String getNowarp() {
		if(nowarp){
			return getNodeAttribute("nowrap", "nowrap");
		}
		
		return "";
	}

	public void setNowarp(boolean nowarp) {
		this.nowarp = nowarp;
	}

	public String getOrderBy() {
		return orderBy;
	}
	
	public String getOrderByNode() {
		return getNodeAttribute("orderBy", orderBy);
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
}
