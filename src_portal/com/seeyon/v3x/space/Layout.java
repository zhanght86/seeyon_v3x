/**
 * 
 */
package com.seeyon.v3x.space;

import java.util.List;

import com.seeyon.v3x.common.constants.LayoutConstants;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-11-29
 */
public final class Layout {
	private String name;
	
	private int columnNum;
	
	private List<String> decorations;
	
	private String type;

	public void init(){
		LayoutConstants.lagout.add(name);
		LayoutConstants.lagoutToType.put(name, type);
		LayoutConstants.lagoutToColumnNumMapping.put(name, columnNum);
		LayoutConstants.lagoutToDecorations.put(name, decorations);
	}
	
	/**
	 * jetspeed的布局模板，描绘了几行几列<br>
	 * 
	 * jetspeed-layouts::VelocityOneColumn
	 * jetspeed-layouts::VelocityTwoColumns
	 * jetspeed-layouts::VelocityThreeColumns
	 * 
	 * @param layout key 布局模板的名称，value 列数
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 模板类型
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 布局的修饰，描绘了每一列的宽度
	 * 
	 * @param decorations
	 */
	public void setDecorations(List<String> decorations) {
		this.decorations = decorations;
	}

	/**
	 * 列数
	 * 
	 * @param columnNum
	 */
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}
	
	public String toString(){
		return this.name + ", " + this.columnNum + ", " + this.decorations;
	}
	
}
