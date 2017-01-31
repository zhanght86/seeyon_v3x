package com.seeyon.v3x.plugin.dee.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public class JDBCDictBean implements DeeResource {
	private final static Log log = LogFactory.getLog(JDBCDictBean.class);
	private String name;
	private String dataSource;
	private String tableName;
	private String keyColumn;
	private String valueColumn;
	
	public JDBCDictBean() {}
	
	@SuppressWarnings("unchecked")
	public JDBCDictBean(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			name = rootElt.attributeValue("name");
			List<Element> propertys = rootElt.elements("property");
			for(Element e : propertys){
				if("dataSource".equals(e.attributeValue("name"))) {
					this.dataSource = e.attributeValue("ref");
				}
				if("tableName".equals(e.attributeValue("name"))) {
					this.tableName = e.attributeValue("value");
				}
				
				if("keyColumn".equals(e.attributeValue("name"))) {
					this.keyColumn = e.attributeValue("value");
				}
				if("valueColumn".equals(e.attributeValue("name"))) {
					this.valueColumn = e.attributeValue("value");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public String toXML() {
		StringBuffer dictXML = new StringBuffer();
		dictXML.append("<dictionary name=\""+ this.name +"\" class=\"com.seeyon.v3x.dee.dictionary.JDBCDictionary\">");
		dictXML.append("<property name=\"dataSource\" ref=\"" + this.dataSource + "\" />");
		dictXML.append("<property name=\"tableName\" value=\""+ this.tableName +"\"/>");
		dictXML.append("<property name=\"keyColumn\" value=\""+ this.keyColumn +"\"/>");
		dictXML.append("<property name=\"valueColumn\" value=\""+ this.valueColumn +"\"/>");
		dictXML.append("</dictionary>");
		return dictXML.toString();
	}

	@Override
	public String toXML(String name) {
		return toXML();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getKeyColumn() {
		return keyColumn;
	}

	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}

}
