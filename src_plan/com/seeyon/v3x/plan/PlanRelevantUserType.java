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
public class PlanRelevantUserType {

	public PlanRelevantUserType() {
	}

	/**
	 * 计划相关人员： 1、主管领导 2、抄送人员 3、告知人员 4、计划发起人
	 */
	public static final PlanRelevantUserType TO_LEADER = new PlanRelevantUserType("1", "主管领导");

	public static final PlanRelevantUserType CC_LEADER = new PlanRelevantUserType("2", "抄送人员");

	public static final PlanRelevantUserType APPRIZE_USER = new PlanRelevantUserType("3", "告知人员");

	public static final PlanRelevantUserType DRAFTSMAN = new PlanRelevantUserType("4", "计划发起人");

	static Map<String,PlanRelevantUserType> all = new TreeMap<String, PlanRelevantUserType>();
	//static Map all = new TreeMap();
	static {
		all.put(TO_LEADER.getValue(), TO_LEADER); // 1
		all.put(CC_LEADER.getValue(), CC_LEADER);
		all.put(APPRIZE_USER.getValue(), APPRIZE_USER);
		all.put(DRAFTSMAN.getValue(), DRAFTSMAN);
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

	public PlanRelevantUserType(String value, String name) {
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
