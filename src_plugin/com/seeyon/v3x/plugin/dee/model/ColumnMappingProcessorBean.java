package com.seeyon.v3x.plugin.dee.model;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public class ColumnMappingProcessorBean implements DeeResource {
	private final static Log log = LogFactory
			.getLog(ColumnMappingProcessorBean.class);
	private String name;
	private String transformXML;
	private String mapping;
	private String desc;
	private boolean transNoMapping;

	public ColumnMappingProcessorBean() {
	}

	public ColumnMappingProcessorBean(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			name = rootElt.attributeValue("name");
			Element descElement = rootElt.element("description");
			desc = descElement.getTextTrim();
			List<Element> proElements = rootElt.elements("property");
			for (Element element : proElements) {
				String proName = element.attributeValue("name");
				if("mapping".equals(proName)){
					mapping = element.attributeValue("ref");					
				}else if("transNoMapping".equals(proName)){
					transNoMapping = Boolean.parseBoolean(element.attributeValue("value"));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public String toXML() {
		String xml = "<processor name=\""
				+ name
				+ "\" class=\"com.seeyon.v3x.dee.processor.ColumnMappingProcessor\"><description>"
				+ desc + "</description><property name=\"mapping\" ref=\""
				+ mapping + "\"/><property name=\"transNoMapping\" value=\""
				+ String.valueOf(transNoMapping) + "\"/></processor>";
		return xml;
	}

	public String toXML(String name) {
		this.name = name;
		return toXML();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransformXML() {
		return transformXML;
	}

	public void setTransformXML(String transformXML) {
		this.transformXML = transformXML;
	}

	public String getDesc() {
		return desc;
	}

	public boolean isTransNoMapping() {
		return transNoMapping;
	}

	public void setTransNoMapping(boolean transNoMapping) {
		this.transNoMapping = transNoMapping;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

}
