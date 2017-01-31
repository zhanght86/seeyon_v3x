package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 最近文档
 */
@Deprecated
public class DocSession extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	// 文档名称
	private String name;
	// 文档id
	private long docResourceId;
	// 访问人
	private long accessUserId;
	// 访问时间
	private Timestamp accessTime;
	// 格式类型
	private DocMimeType docMimeType;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getDocResourceId() {
		return docResourceId;
	}
	
	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}
		
	public long getAccessUserId() {
		return accessUserId;
	}
	
	public void setAccessUserId(long accessUserId) {
		this.accessUserId = accessUserId;
	}
	
	public Timestamp getAccessTime() {
		return accessTime;
	}
	
	public void setAccessTime(Timestamp accessTime) {
		this.accessTime = accessTime;
	}
	
	public DocMimeType getDocMimeType() {
		return docMimeType;
	}
	
	public void setDocMimeType(DocMimeType docMimeType) {
		this.docMimeType = docMimeType;
	}
	
}
