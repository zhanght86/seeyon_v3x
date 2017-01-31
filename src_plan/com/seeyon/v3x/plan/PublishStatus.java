package com.seeyon.v3x.plan;

import java.util.Map;
import java.util.TreeMap;
/**
 * 功能描述：常量类，计划状态，数据库Plan表status字段<br>
 * <br>
 * 日期： Feb 25, 2007 <br>
 * 
 * @version 1.0
 * @author T3
 */
/**
 * 功能描述：常量类，发布状态，对应数据库Plan表Status字段 对应VO类Plan属性 publishStatus<br>
 * @version V1.1 更新时间：2008年8月19日
 */
public class PublishStatus {

	public PublishStatus() {
	}

	/**
	 * 状态:<p>
	 * <STRIKE> (V1.0) 1.<b>草稿</b> 2.已发布 3.已总结  4.已完成</STRIKE><br>
	 * <STRIKE> (V1.1) 1.<b>草稿</b> 2.未开始 3.进行中  4.计划已完成</STRIKE><br>
	 * (V1.2) 1.<b>草稿</b> 2.已发布 3.已总结 
	 */
	public static final PublishStatus DRAFT = new PublishStatus("1", "草稿");
	
	/**
	 * 状态:<p>
	 * <STRIKE> (V1.0) 1.草稿 2.<b>已发布</b> 3.已总结  4.已完成</STRIKE><br>
	 * <STRIKE> (V1.1) 1.草稿 2.<b>未开始</b> 3.进行中  4.计划已完成</STRIKE><br>
	 * (V1.2) 1.草稿 2.<b>已发布</b> 3.已总结 
	 */
	public static final PublishStatus ISSUED = new PublishStatus("2", "已发布");
	
	/**
	 * 状态:<p>
	 * <STRIKE> (V1.0) 1.草稿 2.已发布 3.<b>已总结</b>  4.已完成</STRIKE><br>
	 * <STRIKE> (V1.1) 1.草稿 2.未开始 3.<b>进行中</b>  4.计划已完成</STRIKE><br>
	 * (V1.2) 1.草稿 2.已发布 3<b>.已总结</b> 
	 */
	public static final PublishStatus SUMMARY = new PublishStatus("3","已总结");

	static Map<String,PublishStatus> all = new TreeMap<String, PublishStatus>();
	//static Map all = new TreeMap();
	static {
		all.put(DRAFT.getValue(), DRAFT);
		all.put(ISSUED.getValue(), ISSUED);
		all.put(SUMMARY.getValue(), SUMMARY);
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

	public PublishStatus(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String valueToName(String value) {
		try {

			Object obj = all.get(value);
			if (obj == null) {
				return "未知";
			} else {
				return ((PublishStatus) obj).getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		System.out.println(valueToName("1"));
		System.out.println(valueToName("2"));
		System.out.println(valueToName("3"));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
