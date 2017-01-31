package com.seeyon.v3x.plugin.dee.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public abstract class JDBCAdapter implements DeeResource{
	private final static Log log = LogFactory.getLog(JDBCAdapter.class);
	private String name;
	private String dataSource;
	private String desc;
	private Map<String, String> map;

	public JDBCAdapter() {}

	public JDBCAdapter(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			name = rootElt.attributeValue("name");
			Element descElement = rootElt.element("description");
			desc = descElement.getTextTrim();
			Element dataSourceElement = rootElt.element("property");
			dataSource = dataSourceElement.attributeValue("ref");
			List<Element> sqlIter = rootElt.element("map").elements("key");
			map = new LinkedHashMap<String, String>();
			for (Element sqlElement : sqlIter) {
				map.put(sqlElement.attributeValue("name"),
						sqlElement.attributeValue("value"));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
}
