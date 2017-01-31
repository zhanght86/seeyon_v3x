package com.seeyon.v3x.calendar.util;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 功能描述：常量类，日程关联的应用类型，数据库cal_event_relevancy表Relevancy_Type字段<br>
 * 
 * @version V1.0 更新时间：2008年9月1日
 */
public class RelevancyType {
	private final static Log log = LogFactory.getLog(RelevancyType.class);
	public RelevancyType() {
	}

	/**
	 * 状态:
	 * <p>
	 * (V1.0) 1.<b>会议</b>
	 */
	public static final RelevancyType MEETING = new RelevancyType("1", "会议");

	static Map<String, RelevancyType> all = new TreeMap<String, RelevancyType>();

	static {
		all.put(MEETING.getValue(), MEETING);
	}

	private String value; // 数据库存的值

	private String name; // 对应的中文名称

	public static java.util.Collection getAll() {
		return all.values();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RelevancyType(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String valueToName(String value) {
		try {

			Object obj = all.get(value);
			if (obj == null) {
				return "未知";
			} else {
				return ((RelevancyType) obj).getName();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return "";
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
