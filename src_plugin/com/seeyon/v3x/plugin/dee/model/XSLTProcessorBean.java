package com.seeyon.v3x.plugin.dee.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public class XSLTProcessorBean implements DeeResource {
	private final static Log log = LogFactory.getLog(XSLTProcessorBean.class);
	private String xsl;
	private String name;
	private String desc;

	public XSLTProcessorBean() {
	}

	public XSLTProcessorBean(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			name = rootElt.attributeValue("name");
			Element descElement = rootElt.element("description");
			desc = descElement.getTextTrim();
			Element dataSourceElement = rootElt.element("property");
			xsl = dataSourceElement.attributeValue("value");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public String toXML() {
		String xml = "<processor name=\""
				+ name
				+ "\" class=\"com.seeyon.v3x.dee.processor.XSLTProcessor\"><description>"
				+ desc + "</description><property name=\"xsl\" value=\"" + xsl
				+ "\"/></processor>";
		return xml;
	}
	
	public String toXML(String name) {
		this.name = name;
		return toXML();
	}

	public String getXsl() {
		return xsl;
	}

	public void setXsl(String xsl) {
		this.xsl = xsl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
