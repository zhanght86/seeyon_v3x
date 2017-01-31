package com.seeyon.v3x.doc.webmodel;

/**
 * 文档列表单元格vo
 */
public class GridVO {
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "title -- " + this.getTitle() + " value -- " + this.getValue();
	}

	// 本单元格对应的列标题
	private String title;

	// 本单元格数据的真实类型
	private Class type;

	// 本单元格的值
	private Object value;

	// 对齐方式 left, center, right
	private String align;

	// 本单元格在整列的显示比列
	private Integer percent;
	
	// 是否标题栏
	private boolean isName;
	
	// 是否大小栏
	private boolean isSize;
	
	// 是否图标
	private boolean isImg = false;	
	
	// docResource的name是否为key，即页面是否需要国际化
	private boolean needI18n;	
	
	public boolean getNeedI18n() {
		return needI18n;
	}

	public void setNeedI18n(boolean needI18n) {
		this.needI18n = needI18n;
	}

	public boolean getIsImg() {
		return isImg;
	}

	public void setIsImg(boolean isImg) {
		this.isImg = isImg;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean getIsName() {
		return isName;
	}

	public void setIsName(boolean isName) {
		this.isName = isName;
	}

	public boolean getIsSize() {
		return isSize;
	}

	public void setIsSize(boolean isSize) {
		this.isSize = isSize;
	}
	
}
