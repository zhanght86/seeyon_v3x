package com.seeyon.v3x.plugin.dee.model;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;


/**
 * 静态枚举字典
 * 
 * @author lilong
 * @date 2012-02-18
 * 
 */
public class StaticDictBean implements DeeResource {

//	private String name;// 字典名称
//	private Map<String, String> map;// 键值对
	private String dictInfo;// 文本框保存字典信息

	public StaticDictBean() {
	}

	public StaticDictBean(String dictInfo) {
		this.dictInfo = dictInfo;
	}

	@Override
	public String toXML() {
//		StringBuffer dictInfo = new StringBuffer();
//		for (Entry<String, String> entry : this.map.entrySet()) {
//			dictInfo.append(name).append(".").append(entry.getKey())
//					.append("=").append(entry.getKey()).append(":")
//					.append(entry.getValue()).append("\r\n");
//		}
		return dictInfo.toString();
	}

	@Override
	public String toXML(String name) {
		return toXML();
	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}

//	public Map<String, String> getMap() {
//		return map;
//	}
//
//	public void setMap(Map<String, String> map) {
//		this.map = map;
//	}

	public String getDictInfo() {
		return dictInfo;
	}

	public void setDictInfo(String dictInfo) {
		this.dictInfo = dictInfo;
	}

}
