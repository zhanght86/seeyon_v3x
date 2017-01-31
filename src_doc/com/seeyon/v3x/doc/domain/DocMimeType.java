package com.seeyon.v3x.doc.domain;

import java.io.Serializable;

import com.seeyon.v3x.doc.util.Constants;

/**
 * 文档格式
 */
public class DocMimeType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6543214988087091604L;

	private Long id;

	// 内容类型名称/文件名后缀
	private String name;

	// 图标名称
	private String icon;
	// 格式类型 Constants.FORMAT_TYPE_xxxx
	private long formatType;
	// 排序号
	private int orderNum;
	
	public boolean isOffice() {
		return this.formatType == Constants.FORMAT_TYPE_DOC_A6 || this.isMSOrWPS();
	}
	
	public boolean isMSOrWPS() {
		return formatType == Constants.FORMAT_TYPE_DOC_WORD || 
			   formatType == Constants.FORMAT_TYPE_DOC_EXCEL || 
			   formatType == Constants.FORMAT_TYPE_DOC_WORD_WPS || 
			   formatType == Constants.FORMAT_TYPE_DOC_EXCEL_WPS;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public long getFormatType() {
		return formatType;
	}

	public void setFormatType(long formatType) {
		this.formatType = formatType;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

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
}
