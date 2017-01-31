package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.util.Strings;

/**
 * 枚举型元数据的可选项
 */
public class DocMetadataOption extends BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1723407000734353548L;
	// 元数据
	private DocMetadataDefinition metadataDef;
	// 一个可选项
	private String optionItem;

	public DocMetadataOption() {
	}
	
    public DocMetadataDefinition getMetadataDef() {
		return metadataDef;
	}
	public void setMetadataDef(DocMetadataDefinition metadataDef) {
		this.metadataDef = metadataDef;
	}
	public String getOptionItem() {
		return this.optionItem;
	}
	public void setOptionItem(String optionItem) {
		this.optionItem = optionItem;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}
	
	public String toHTML() {
		String showName = Strings.toHTML(optionItem);
		return "<option value='" + this.getId() + "' title='" + showName + "'>" + showName + "</option>";
	}
}