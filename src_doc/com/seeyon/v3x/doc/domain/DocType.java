package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档的内容类型
 */
public class DocType extends BaseModel implements Serializable, Comparable<DocType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4073741450168802741L;
	//default serial version id, required for serializable classes.
	//private static final long serialVersionUID = 1L;	
	private String name;
	private String description;
	private byte parentType;////0-文档夹;1-文档;3-表单(内容格式类别)
	private Long formDefinitionId;	// 表单定义id，暂未使用
	private boolean editable; //是否可以在文档库中新建文档时选择?
	private boolean isSystem; //是否为系统内容类型
	private byte status;//0-草稿,1-已使用,2-已删除	
	private byte seartchStatus;//0-允许查询,2-停用查询
	private Long domainId; //创建单位id,domainId=0为系统内容类型
	
	private Set<DocTypeDetail> docTypeDetail;	

	public byte getParentType() {
		return this.parentType;
	}
	
	public void setParentType(byte parentType) {
		this.parentType = parentType;
	}
	
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
		
	public Long getFormDefinitionId() {
		return this.formDefinitionId;
	}
	
	public void setFormDefinitionId(Long formDefinitionId) {
		this.formDefinitionId = formDefinitionId;
	}

	public boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	public boolean getEditable() {
		return this.editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}	
	
	public byte getStatus() {
		return status;
	}
	
	public byte getSeartchStatus(){
		return seartchStatus;
	}

	public void setStatus(byte status) {
		this.status = status;
	}	
	
	public void setSeartchStatus(byte seartchStatus){
		this.seartchStatus= seartchStatus;
	}
	
	public Long getDomainId() {
		return domainId;
	}
	
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Set<DocTypeDetail> getDocTypeDetail() {
		return docTypeDetail;
	}

	public void setDocTypeDetail(Set<DocTypeDetail> docTypeDetail) {
		this.docTypeDetail = docTypeDetail;
	}	

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	public int compareTo(DocType o) {
		if(this.getIsSystem()){
			if(o.getIsSystem()){
				return 0;
			}else{
				return -1;
			}
		}else{
			if(o.getIsSystem()){
				return 1;
			}else{
				return 0;
			}
		}
			
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocType other = (DocType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (docTypeDetail == null) {
			if (other.docTypeDetail != null)
				return false;
		} else if (!docTypeDetail.equals(other.docTypeDetail))
			return false;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		if (editable != other.editable)
			return false;
		if (formDefinitionId == null) {
			if (other.formDefinitionId != null)
				return false;
		} else if (!formDefinitionId.equals(other.formDefinitionId))
			return false;
		if (isSystem != other.isSystem)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentType != other.parentType)
			return false;
		if (seartchStatus != other.seartchStatus)
			return false;
		if (status != other.status)
			return false;
		return true;
	}
	
	

}