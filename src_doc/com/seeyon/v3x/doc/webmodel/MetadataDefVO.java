package com.seeyon.v3x.doc.webmodel;

/**
 * 元数据定义vo
 */
public class MetadataDefVO {
	// 国际化key
	private String key;
	// 元数据类型
	private byte value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public byte getValue() {
		return value;
	}
	public void setValue(byte value) {
		this.value = value;
	}
}
