package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档库的内容类型列表
 */
public class DocTypeList extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	// 文档库id
	private long docLibId;
	// 内容类型id
	private long docTypeId;
	// 排序号
	private int orderNum;

    public DocTypeList() {
    }

//	public Long getId() {
//		return this.id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}

	public long getDocLibId() {
		return this.docLibId;
	}
	public void setDocLibId(long docLibId) {
		this.docLibId = docLibId;
	}

	public long getDocTypeId() {
		return this.docTypeId;
	}
	public void setDocTypeId(long docTypeId) {
		this.docTypeId = docTypeId;
	}

	public int getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}