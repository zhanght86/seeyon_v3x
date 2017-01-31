package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.metadata.Metadata;

/**
 * 文档元数据定义
 */
public class DocMetadataDefinition extends BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1326686778786122125L;
	// 类型
	private String category;
	// 默认值
	private String defaultValue;
	private String description;
	// 暂时未使用
	private boolean isHidden;
	private Integer length;//保留属性，暂时未使用
	private String name;
	// 是否可空
	private boolean nullable;
	private Byte optionType; //暂未使用
	// 元数据定义在doc_metadata对应的列名
	private String physicalName;
	private String scopeMaxValue;//保留属性
	private String scopeMinValue;//保留属性
	// Constants MetadaDef 类型
	private byte type;
	// 是否系统预置元数据
	private boolean isSystem;
	// 是否从doc_resources表过来的元数据
	private boolean isDefault;
	// 是否百分比形式
	private boolean isPercent;
	// 所属单位id
	private Long domainId;
	// 状态 Constants.DOC_METADATA_DEF_STATUS_xxx
	private Integer status;
	
	/** 是否可以用于文档查询 */
	private boolean searchable;

	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	private Set<DocMetadataOption> metadataOption ;
	
	// 页面显示使用，已经做了国际化
	private String showName;

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public boolean getIsSystem() {
		return isSystem;
	}

	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public Set<DocMetadataOption> getMetadataOption() {
		return metadataOption;
	}

	public void setMetadataOption(Set<DocMetadataOption> metadataOption) {
		this.metadataOption = metadataOption;
	}
	
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public DocMetadataDefinition() {
    }

	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	

	public Integer getLength() {
		return this.length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Byte getOptionType() {
		return this.optionType;
	}
	public void setOptionType(Byte optionType) {
		this.optionType = optionType;
	}

	public String getPhysicalName() {
		return this.physicalName;
	}
	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	public String getScopeMaxValue() {
		return this.scopeMaxValue;
	}
	public void setScopeMaxValue(String scopeMaxValue) {
		this.scopeMaxValue = scopeMaxValue;
	}

	public String getScopeMinValue() {
		return this.scopeMinValue;
	}
	public void setScopeMinValue(String scopeMinValue) {
		this.scopeMinValue = scopeMinValue;
	}

	public byte getType() {
		return this.type;
	}
	public void setType(byte type) {
		this.type = type;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public boolean getIsPercent() {
		return isPercent;
	}

	public void setIsPercent(boolean isPercent) {
		this.isPercent = isPercent;
	}
	
	public Long getDomainId() {
		return domainId;
	}
	
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocMetadataDefinition other = (DocMetadataDefinition) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		if (isDefault != other.isDefault)
			return false;
		if (isHidden != other.isHidden)
			return false;
		if (isPercent != other.isPercent)
			return false;
		if (isSystem != other.isSystem)
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (metadataOption == null) {
			if (other.metadataOption != null)
				return false;
		} else if (!metadataOption.equals(other.metadataOption))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nullable != other.nullable)
			return false;
		if (optionType == null) {
			if (other.optionType != null)
				return false;
		} else if (!optionType.equals(other.optionType))
			return false;
		if (physicalName == null) {
			if (other.physicalName != null)
				return false;
		} else if (!physicalName.equals(other.physicalName))
			return false;
		if (scopeMaxValue == null) {
			if (other.scopeMaxValue != null)
				return false;
		} else if (!scopeMaxValue.equals(other.scopeMaxValue))
			return false;
		if (scopeMinValue == null) {
			if (other.scopeMinValue != null)
				return false;
		} else if (!scopeMinValue.equals(other.scopeMinValue))
			return false;
		if (searchable != other.searchable)
			return false;
		if (showName == null) {
			if (other.showName != null)
				return false;
		} else if (!showName.equals(other.showName))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	/** 不持久化，用于高级查询枚举类型下拉列表展现之用  */
	private Metadata metadata;

	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}


}