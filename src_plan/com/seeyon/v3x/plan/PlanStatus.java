package com.seeyon.v3x.plan;

import java.util.Map;
import java.util.TreeMap;

/**
 * 功能描述：常量类，计划状态，数据库Plan表Plan_Status字段<br>
 * @version V1.0 更新时间：2008年8月19日
 */
public class PlanStatus {

	public PlanStatus() {
	}

	/**
	 * 状态:<p>
	 * (V1.0) 1.<b>未开始</b> 2.进行中 3.已取消 4.已完成 5.已推迟 
	 */
	public static final PlanStatus BEFOREBEGINNING = new PlanStatus("1", "未开始");
	
	/**
	 * 状态:<p>
	 * (V1.0) 1.未开始 2.<b>进行中</b> 3.已取消 4.已完成 5.已推迟 
	 */
	public static final PlanStatus ONGOING = new PlanStatus("2", "进行中");
	
	/**
	 * 状态:<p>
	 * (V1.0) 1.未开始 2.进行中 3.<b>已取消</b> 4.已完成 5.已推迟 
	 */
	public static final PlanStatus CANCELLED = new PlanStatus("3","已取消");
	
	/**
	 * 状态:<p>
	 * (V1.0) 1.未开始 2.进行中 3.已取消 4.<b>已完成</b> 5.已推迟 
	 */
	public static final PlanStatus FINISHED = new PlanStatus("4","已完成");

	/**
	 * 状态:<p>
	 * (V1.0) 1.未开始 2.进行中 3.已取消 4.已完成 5.<b>已推迟</b> 
	 */
	public static final PlanStatus POSTPONED = new PlanStatus("5","已推迟");

	static Map<String,PlanStatus> all = new TreeMap<String, PlanStatus>();

	static {
		all.put(BEFOREBEGINNING.getValue(), BEFOREBEGINNING);
		all.put(ONGOING.getValue(), ONGOING);
		all.put(FINISHED.getValue(), FINISHED);
		all.put(CANCELLED.getValue(), CANCELLED);
		all.put(POSTPONED.getValue(), POSTPONED);		
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

	public PlanStatus(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String valueToName(String value) {
		try {

			Object obj = all.get(value);
			if (obj == null) {
				return "未知";
			} else {
				return ((PlanStatus) obj).getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

/*	public static void main(String[] args) {
		System.out.println(valueToName("1"));
		System.out.println(valueToName("2"));
		System.out.println(valueToName("3"));
		System.out.println(valueToName("4"));
		System.out.println(valueToName("5"));
		System.out.println(valueToName("999"));
	}*/

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
