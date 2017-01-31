package com.seeyon.v3x.doc.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档查询条件配置
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-11-29
 */
public class DocSearchConfig extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8285602258500478088L;
	
	/** 配置查询条件项对应的文档元数据ID */
	private Long metadataDefiniotionId;
	/** 配置查询条件项排序号 */
	private Integer sortId;
	/** 配置查询条件项对应文档库ID */
	private Long docLibId;
	
	public DocSearchConfig() {}
	
	public DocSearchConfig(Long metadataDefiniotionId, Long docLibId, Integer sortId) {
		super();
		this.setNewId();
		this.metadataDefiniotionId = metadataDefiniotionId;
		this.docLibId = docLibId;
		this.sortId = sortId;
	}



	public Integer getSortId() {
		return sortId;
	}
	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}
	public Long getDocLibId() {
		return docLibId;
	}
	public void setDocLibId(Long docLibId) {
		this.docLibId = docLibId;
	}
	public Long getMetadataDefiniotionId() {
		return metadataDefiniotionId;
	}
	public void setMetadataDefiniotionId(Long metadataDefiniotionId) {
		this.metadataDefiniotionId = metadataDefiniotionId;
	}
	
}
