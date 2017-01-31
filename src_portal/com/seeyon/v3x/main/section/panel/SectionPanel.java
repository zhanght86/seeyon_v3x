/**
 * 
 */
package com.seeyon.v3x.main.section.panel;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * @author dongyj
 * 外部调用。取得一个Fragment的页签
 */
public class SectionPanel extends ObjectToXMLBase {
	
	private String id;
	
	private String name;

	private String type;
	
	private Integer affairCount;
	
	public Integer getAffairCount() {
		return affairCount;
	}

	public void setAffairCount(Integer affairCount) {
		this.affairCount = affairCount;
	}

	public SectionPanel(String id,String name){
		this.id = id;
		this.name = name;
	}
	
	public SectionPanel(String id, String name,String type){
		this(id,name);
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	/**
	 * 页签类型。用于记录待办添加的页签
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 页签id 在section中是唯一的
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 取得页签的名称 
	 * 默认是在SectionResources{@link com.seeyon.v3x.main.section.resources.i18n.SectionResources}中定义,
	 * 定义格式为section.panel.{id}
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
