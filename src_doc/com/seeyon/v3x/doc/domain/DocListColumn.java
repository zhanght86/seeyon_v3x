package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档库的显示栏目
 */
public class DocListColumn extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	// 文档元数据id
	private long metadataDefiniotionId;
	private long docLibId;
	private int orderNum;


	public DocListColumn() {
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

	public long getMetadataDefiniotionId() {
		return metadataDefiniotionId;
	}

	public void setMetadataDefiniotionId(long metadataDefiniotionId) {
		this.metadataDefiniotionId = metadataDefiniotionId;
	}
}