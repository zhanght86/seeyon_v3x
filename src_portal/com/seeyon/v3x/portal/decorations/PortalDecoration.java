/**
 * 
 */
package com.seeyon.v3x.portal.decorations;

import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.portal.exception.LayoutPropertiesNotEnoughException;

/**
 * A8布局修饰描述
 * @author dongyj
 *
 */
public class PortalDecoration implements Serializable ,Comparable<PortalDecoration>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4673915166934205568L;
	
	private static final String ID 			= "layout.id";
	private static final String NAME 		= "layout.name";
	private static final String VIEWPATH 	= "layout.viewPath";
	private static final String LAYOUTSTYLE = "layout.style";
	private static final String LAYOUTSORT 	= "layout.sort";
	private static final String LAYOUTTYPE	= "layout.type";
	private static final String MULTIPLE   	= "layout.isMutiple";
	
	public void loadFromProperties(Properties properties)throws LayoutPropertiesNotEnoughException{
		setId(getValue(properties,ID,true));
		setName(getValue(properties,NAME,true));
		setViewPath(getValue(properties,VIEWPATH,true));
		String sort = getValue(properties, LAYOUTSORT, false);
		if(sort != null && NumberUtils.isNumber(sort)){
			setSort(Integer.parseInt(sort));
		}
		String style = getValue(properties, LAYOUTSTYLE, false);
		if(style != null){
			setStyle(style.split(","));
		}
		setLayoutType(getValue(properties,LAYOUTTYPE,true));
		String isMultiple = getValue(properties,MULTIPLE,false);
		if("true".equals(isMultiple)){
			setIsMutiple(true);
		}
	}
	/**
	 * 标示唯一在a8中存在的修饰
	 */
	private String id;
	
	/**
	 * 修饰的名称，选择布局时候要显示。
	 */
	private String name;
	
	/**
	 * 渲染布局的处理页
	 * <pre>
	 * A8默认转到页面下layout.jsp下
	 * 比如/WEB-INF/decorations/layout/D2_2-5_5/layout.jsp
	 * </pre>
	 */
	private String viewPath;
	
	
	/**
	 * 排序所用
	 */
	private int sort = 0;

	/**
	 * 布局引用样式
	 */
	private String[] style;
	
	/**
	 * 布局类型 几列
	 */
	private String layoutType;
	
	/**
	 * 是否是复杂的布局
	 */
	private Boolean isMutiple = false;
	
	public Boolean getIsMutiple() {
		return isMutiple;
	}


	public void setIsMutiple(Boolean isMutiple) {
		this.isMutiple = isMutiple;
	}


	public String getId() {
		return id;
	}


	private void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	private void setName(String name) {
		this.name = name;
	}


	public int getSort() {
		return sort;
	}


	private void setSort(int sort) {
		this.sort = sort;
	}


	public String getViewPath() {
		return viewPath;
	}


	private void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}


	public int compareTo(PortalDecoration decoration) {
		if(this.sort > decoration.sort){
			return 1;
		}else if(this.sort == decoration.sort){
			return 0;
		}
		return -1;
	}

	public String[] getStyle() {
		return style;
	}

	private void setStyle(String[] style) {
		this.style = style;
	}
	
	private String getValue(Properties prop,String key,boolean isMust) throws LayoutPropertiesNotEnoughException{
		String value = prop.getProperty(key);
		if(value == null && isMust){
			throw new LayoutPropertiesNotEnoughException(key);
		}
		return value;
	}

	public String getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(String layoutType) {
		this.layoutType = layoutType;
	}
}
