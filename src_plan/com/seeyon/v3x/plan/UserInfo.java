package com.seeyon.v3x.plan;

import java.util.Map;
import java.util.TreeMap;
/**
 * 功能描述：常量类，user信息<br>
 * <br>
 * 日期： Feb 25, 2007 <br>
 * 
 * @version 1.0
 * @author T3
 */
public class UserInfo {

	public UserInfo() {
	}

	public static final UserInfo USER_A = new UserInfo("-1197936421050498500", "用户A");

	public static final UserInfo USER_B = new UserInfo("-9097466678178593000", "用户B");

	public static final UserInfo USER_C = new UserInfo("110003", "用户C");


	static Map<String,UserInfo> all = new TreeMap<String, UserInfo>();
	//static Map all = new TreeMap();
	static {
		all.put(USER_A.getValue(), USER_A); // 1
		all.put(USER_B.getValue(), USER_B);
		all.put(USER_C.getValue(), USER_C);
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

	public UserInfo(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String valueToName(String value) {
		try {

			Object obj = all.get(value);
			if (obj == null) {
				return "未知";
			} else {
				return ((UserInfo) obj).getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		System.out.println(valueToName("110002"));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
