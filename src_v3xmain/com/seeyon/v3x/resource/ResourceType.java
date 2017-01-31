package com.seeyon.v3x.resource;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.plan.PlanType;

public class ResourceType {
	private final static Log log = LogFactory.getLog(ResourceType.class);
	public ResourceType() {
	}

	/**
	 * 资源类型 office 办公场所 acc 办公用品 equipment 设备 car 车辆 data 资料
	 */
	
	public static final PlanType OFFICE = new PlanType("office", "会议室");
	
	public static final PlanType MEETINTRESOURCE = new PlanType("meetingres", "与会资源");

	public static final PlanType ACC = new PlanType("acc", "办公用品");

	public static final PlanType EQUIPMENT = new PlanType("equipment", "设备");

	public static final PlanType CAR = new PlanType("car", "车辆");
	
	public static final PlanType DATA = new PlanType("data", "资料");

	static Map<String,PlanType> all = new TreeMap<String, PlanType>();
	//static Map all = new TreeMap();
	static {
		all.put(OFFICE.getValue(), OFFICE); // 1
		all.put(MEETINTRESOURCE.getValue(), MEETINTRESOURCE);
		all.put(ACC.getValue(), ACC);
		all.put(EQUIPMENT.getValue(), EQUIPMENT);
		all.put(CAR.getValue(), CAR);
		all.put(DATA.getValue(), DATA);
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

	public ResourceType(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String valueToName(String value) {
		try {

			Object obj = all.get(value);
			if (obj == null) {
				return "未知";
			} else {
				return ((PlanType) obj).getName();
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
