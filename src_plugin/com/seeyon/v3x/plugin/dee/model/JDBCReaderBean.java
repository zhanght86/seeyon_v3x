package com.seeyon.v3x.plugin.dee.model;

import java.util.Map.Entry;


public class JDBCReaderBean extends JDBCAdapter {

	public JDBCReaderBean() {
		super();
	}

	public JDBCReaderBean(String xml) {
		super(xml);
	}

	@Override
	public String toXML() {
		StringBuffer strb = new StringBuffer(
				"<adapter class=\"com.seeyon.v3x.dee.adapter.JDBCReader\" name=\""
						+ super.getName() + "\"><description>"
						+ super.getDesc()
						+ "</description><property name=\"dataSource\" ref=\""
						+ super.getDataSource() + "\"/><map name=\"sql\">");
		for (Entry<String, String> entry : super.getMap().entrySet()) {
			strb.append("<key name=\"" + entry.getKey() + "\" value=\""
					+ entry.getValue() + "\"/>");
		}
		strb.append("</map>");
		// 增加分页，默认对第一条sql做分页，固定使用Paging_作为分页参数的前缀
		// TODO 后续扩展，可配置对哪条sql分页，并自定义前缀
		strb.append("<map name=\"pagination\">");
		for (Entry<String, String> entry : super.getMap().entrySet()) {
			strb.append("<key name=\"" + entry.getKey() + "\" value=\"Paging_\"/>");
			break;
		}	
		strb.append("</map>");
		strb.append("</adapter>");
		return strb.toString();
	}

	@Override
	public String toXML(String name) {
		super.setName(name);
		return toXML();
	}

}
