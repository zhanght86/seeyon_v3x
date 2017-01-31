package com.seeyon.v3x.plan;

import java.util.Map;
import java.util.TreeMap;

/**
 * 功能描述：常量类，计划类型，数据库Plan表type字段<br>
 * <br>
 * 日期： Feb 25, 2007 <br>
 * 
 * @version 1.0
 * @author T3
 */
public class PlanType {

	public PlanType() {
	}

	/**
	 * 计划类型： 1、日计划 2、周计划 3、月计划 4、任意期计划 0、部门计划
	 */
	public static final PlanType DAY_PLAN = new PlanType("1", "日计划");

	public static final PlanType WEEK_PLAN = new PlanType("2", "周计划");

	public static final PlanType MONTH_PLAN = new PlanType("3", "月计划");

	public static final PlanType ANY_SCOPE_PLAN = new PlanType("4", "任意期计划");
	
	public static final PlanType DEPT_PLAN = new PlanType("0", "部门计划");

	static Map<String,PlanType> all = new TreeMap<String, PlanType>();
	//static Map all = new TreeMap();
	static {
		all.put(DAY_PLAN.getValue(), DAY_PLAN); // 1
		all.put(WEEK_PLAN.getValue(), WEEK_PLAN);
		all.put(MONTH_PLAN.getValue(), MONTH_PLAN);
		all.put(ANY_SCOPE_PLAN.getValue(), ANY_SCOPE_PLAN);
		all.put(DEPT_PLAN.getValue(), DEPT_PLAN);
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

	public PlanType(String value, String name) {
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
