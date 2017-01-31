package com.seeyon.v3x.link.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the link_option_value database table.
 * 
 * @author BEA Workshop Studio
 */
public class LinkOptionValue extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	private Long linkOptionId;
	private long userId;
	private String value;

    public LinkOptionValue() {
    }

	public Long getLinkOptionId() {
		return this.linkOptionId;
	}
	public void setLinkOptionId(Long linkOptionId) {
		this.linkOptionId = linkOptionId;
	}

	public long getUserId() {
		return this.userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public LinkOptionValue(Long id, Long linkOptionId, long userId, String value) {
		this.setId(id);
		this.linkOptionId = linkOptionId;
		this.userId = userId;
		this.value = value;
	}
}