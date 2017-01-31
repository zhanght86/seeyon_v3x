package com.seeyon.v3x.link.domain;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the link_option database table.
 * 
 * @author BEA Workshop Studio
 */
public class LinkOption extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	private boolean isDefault;
	private boolean isPassword;
	private long linkSystemId;
	private int orderNum;
	// 展现给用户填写时的提示
	private String paramName;
	// url传递时真正的参数名
	private String paramSign;
	private String paramValue;
	private Set linkOptionValue;

    public LinkOption() {
    }





	public long getLinkSystemId() {
		return this.linkSystemId;
	}
	public void setLinkSystemId(long linkSystemId) {
		this.linkSystemId = linkSystemId;
	}

	public int getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public String getParamName() {
		return this.paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamSign() {
		return this.paramSign;
	}
	public void setParamSign(String paramSign) {
		this.paramSign = paramSign;
	}

	public String getParamValue() {
		return this.paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public Set getLinkOptionValue() {
		return linkOptionValue;
	}

	public void setLinkOptionValue(Set linkOptionValue) {
		this.linkOptionValue = linkOptionValue;
	}

	public boolean getIsPassword() {
		return isPassword;
	}

	public void setIsPassword(boolean isPassword) {
		this.isPassword = isPassword;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}