package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 内容类型与元数据的关联
 */
public class DocTypeDetail extends BaseModel implements Serializable, Comparable<DocTypeDetail> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 149915416278989947L;
	// 该关联的展示名称
	private String name;
	private String description;
	// 内容类型id
	private long contentTypeId;
	// 元数据id
	private long metadataDefId;	
	// 排序号
	private int orderNum;
	// 只读
	private boolean readOnly;	
	// 可空
	private boolean nullable;
	
	private DocMetadataDefinition docMetadataDefinition;

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getContentTypeId() {
		return contentTypeId;
	}
	
	public void setContentTypeId(long contentTypeId) {
		this.contentTypeId = contentTypeId;
	}
	
	public long getMetadataDefId() {
		return metadataDefId;
	}

	public void setMetadataDefId(long metadataDefId) {
		this.metadataDefId = metadataDefId;
	}		

	public int getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	
	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public DocMetadataDefinition getDocMetadataDefinition() {
		return docMetadataDefinition;
	}
	
	public void setDocMetadataDefinition(DocMetadataDefinition docMetadataDefinition) {
		this.docMetadataDefinition = docMetadataDefinition;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}
	public boolean getNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public int compareTo(DocTypeDetail o) {
		if(this.orderNum <= o.orderNum)
			return -1;
		else
			return 1;
	}

}