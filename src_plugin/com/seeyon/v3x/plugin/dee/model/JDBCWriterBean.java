package com.seeyon.v3x.plugin.dee.model;

import java.util.Map.Entry;


import com.seeyon.v3x.dee.common.db.resource.model.DeeResource;

public class JDBCWriterBean extends JDBCAdapter implements DeeResource {
	public JDBCWriterBean() {
		super();
	}

	public JDBCWriterBean(String xml) {
		super(xml);
	}

	@Override
	public String toXML() {
		StringBuffer strb = new StringBuffer(
				"<adapter class=\"com.seeyon.v3x.dee.adapter.JDBCWriter\" name=\""
						+ super.getName() + "\"><description>"
						+ super.getDesc()
						+ "</description><property name=\"dataSource\" ref=\""
						+ super.getDataSource()
						+ "\"/><map name=\"targetIds\">");
		for (Entry<String, String> entry : super.getMap().entrySet()) {
			strb.append("<key name=\"" + entry.getKey() + "\" value=\""
					+ entry.getValue() + "\"/>");
		}
		strb.append("</map></adapter>");
		return strb.toString();
	}
	
	public String toXML(String name) {
		super.setName(name);
		return toXML();
	}

}
