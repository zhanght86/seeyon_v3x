package com.seeyon.v3x.plan;

import java.util.Map;
import java.util.TreeMap;

public class PlanLogType {

	public PlanLogType() {
	}

	/**
	 * 计划相关人员：1、已发布 2、已归档 3、已回复 4、未回复 5、已总结 6、未总结
	 */
	public static final PlanLogType SUCCESS_ISSUED = new PlanLogType("1", "已发布");

	public static final PlanLogType SUCCESS_PIGEONHOLE = new PlanLogType("2", "已归档");

	public static final PlanLogType SUCCESS_REPLY = new PlanLogType("3", "已回复");

	public static final PlanLogType NOT_REPLY = new PlanLogType("4", "未回复");

	public static final PlanLogType SUCCESS_SUMMARY = new PlanLogType("5", "已总结");

	public static final PlanLogType NOT_SUMMARY = new PlanLogType("6", "未总结");
	
	static Map<String,PlanLogType> all = new TreeMap<String, PlanLogType>();
	//static Map all = new TreeMap();
	static {
		all.put(SUCCESS_ISSUED.getValue(), SUCCESS_ISSUED); // 1
		all.put(SUCCESS_PIGEONHOLE.getValue(), SUCCESS_PIGEONHOLE);
		all.put(SUCCESS_REPLY.getValue(), SUCCESS_REPLY);
		all.put(NOT_REPLY.getValue(), NOT_REPLY);
		all.put(SUCCESS_SUMMARY.getValue(), SUCCESS_SUMMARY);
		all.put(NOT_SUMMARY.getValue(), NOT_SUMMARY);
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

	public PlanLogType(String value, String name) {
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
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		System.out.println(valueToName("9"));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
