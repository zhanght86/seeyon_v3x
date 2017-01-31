package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * doc_metadata 表的字段是否已经使用标记
 */
public class DocMetadataUse extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	//private Long id;
	// 字段名
	private String fieldName;
	// 元数据的类型
	private byte type;
	// 是否使用
	private boolean useMark;

    public DocMetadataUse() {
    }

//	public Long getId() {
//		return this.id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}

	public String getFieldName() {
		return this.fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public byte getType() {
		return this.type;
	}
	public void setType(byte type) {
		this.type = type;
	}

	public boolean getUseMark() {
		return this.useMark;
	}
	public void setUseMark(boolean useMark) {
		this.useMark = useMark;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}