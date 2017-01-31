package com.seeyon.v3x.doc.domain;

import java.util.List;

/**
 * 元数据vo,用于表示文档的元数据值列表
 */
public class DocMetadataObject implements Comparable<DocMetadataObject> {
	private long metadataDefId;
	// 元数据名称
	private String metadataName;
	// 元数据值
	private Object metadataValue;
	// 排序号
	private int orderNum;
	// 元数据类型
	private byte metadataType;
	// doc_metadata 表的列名
	private String physicalName;
	// 元数据的 optionType
	private Byte optionType;
	// 枚举型的可选项
	private List<DocMetadataOption> optionList;
	// 是否只读
	private boolean readOnly;

	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public List<DocMetadataOption> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<DocMetadataOption> optionList) {
		this.optionList = optionList;
	}

	public Byte getOptionType() {
		return optionType;
	}

	public void setOptionType(Byte optionType) {
		this.optionType = optionType;
	}

	public byte getMetadataType() {
		return metadataType;
	}

	public void setMetadataType(byte metadataType) {
		this.metadataType = metadataType;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	public long getMetadataDefId() {
		return metadataDefId;
	}

	public void setMetadataDefId(long metadataDefId) {
		this.metadataDefId = metadataDefId;
	}

	public String getMetadataName() {
		return metadataName;
	}

	public void setMetadataName(String metadataName) {
		this.metadataName = metadataName;
	}

	public Object getMetadataValue() {
		return metadataValue;
	}

	public void setMetadataValue(Object metadataValue) {
		this.metadataValue = metadataValue;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public int compareTo(DocMetadataObject o) {
		if (this.orderNum < o.orderNum)
			return -1;
		else
			return 1;
	}

	@Override
	public String toString() {
		return "// " + this.getMetadataName() + ", " + this.getMetadataValue() + ", " + this.getOptionType();
	}
	
	

}
