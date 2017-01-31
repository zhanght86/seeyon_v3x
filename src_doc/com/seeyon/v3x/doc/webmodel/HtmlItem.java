package com.seeyon.v3x.doc.webmodel;

import java.util.List;

import com.seeyon.v3x.doc.domain.DocMetadataOption;

/**
 * 在进行文档的属性查看、修改时，有些元数据是动态的，预先并知道有多少种。
 * 为了在页面上可以正常显示出所有的相关元数据，使用这个HtmlItem
 * 表示 页面的展示元素和对应的后台数据。
 */
@Deprecated
public class HtmlItem {
	// 标签 元数据的名字
	private String label;

	// 显示组件类型
	private String showType;

	// 组件name, 使用元数据physicalName
	private String name;

	// 显示的值 元数据值
	private Object value;

	// 选择情况下的可选项
	private List<DocMetadataOption> options;
	private Integer optionSize = 0;

	// 选择情况下的预选值
	private List<Long> selected;
	
	// 只读
	private boolean readonly;

	public boolean getReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public List<DocMetadataOption> getOptions() {
		return options;
	}

	public void setOptions(List<DocMetadataOption> options) {
		this.options = options;
	}


	public List<Long> getSelected() {
		return selected;
	}

	public void setSelected(List<Long> selected) {
		this.selected = selected;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getOptionSize() {
		return optionSize;
	}

	public void setOptionSize(Integer optionSize) {
		this.optionSize = optionSize;
	}
}
