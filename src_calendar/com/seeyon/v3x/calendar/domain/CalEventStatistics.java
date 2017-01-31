package com.seeyon.v3x.calendar.domain;

/**
 * 事件统计封装结果
 * 
 * @author Administrator
 * 
 */
public class CalEventStatistics implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6637892856756087556L;

	private String color;

	private Integer type;

	private String typeName;

	private Integer counts;

	private Float sumTime;

	private String percentum;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getCounts() {
		return counts;
	}

	public void setCounts(Integer counts) {
		this.counts = counts;
	}

	public Float getSumTime() {
		return sumTime;
	}

	public void setSumTime(Float sumTime) {
		this.sumTime = sumTime;
	}

	public String getPercentum() {
		return percentum;
	}

	public void setPercentum(String percentum) {
		this.percentum = percentum;
	}

}