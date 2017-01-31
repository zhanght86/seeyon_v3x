/**
 * 
 */
package com.seeyon.v3x.mobile.webmodel;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-22
 */
public class FormField implements Serializable {

	private static final long serialVersionUID = -2263950365766470283L;

	/**
	 * 单元格表示
	 */
	private String id;

	/**
	 * 单元格显示名字
	 */
	private String name;

	/**
	 * 类型 : 文本 数字 日期时间 备注
	 */
	private String type;

	/**
	 * 长度限制
	 */
	private int length;

	/**
	 * 输入类型 : 文本框 单选按钮 下拉列表 选择人员 选择部门 选择日期 选择时间
	 */
	private String operateType;

	/**
	 * 小数位限制
	 */
	private int decimalDigits;

	/**
	 * 不能为空验证
	 */
	private boolean noNull;

	/**
	 * 默认的值
	 */
	private String defaultValue;

	/**
	 * 默认显示的文本
	 */
	private String defaultText;

	/**
	 * 枚举邦定
	 */
	private List<Option> options;

	public class Option {
		private String id;

		private String text;

		private String value;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public boolean isNoNull() {
		return noNull;
	}

	public void setNoNull(boolean noNull) {
		this.noNull = noNull;
	}

	public String getDefaultText() {
		return defaultText;
	}

	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
}
