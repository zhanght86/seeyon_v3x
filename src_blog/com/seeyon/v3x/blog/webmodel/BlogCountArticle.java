package com.seeyon.v3x.blog.webmodel;

public class BlogCountArticle implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String moduleName;
	private String moduleId;
	private long dayCount;
	private long weekCount;
	private long monthCount;
	private long allCount;
	public long getAllCount() {
		return allCount;
	}
	public void setAllCount(long allCount) {
		this.allCount = allCount;
	}
	public long getDayCount() {
		return dayCount;
	}
	public void setDayCount(long dayCount) {
		this.dayCount = dayCount;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public long getMonthCount() {
		return monthCount;
	}
	public void setMonthCount(long monthCount) {
		this.monthCount = monthCount;
	}
	public long getWeekCount() {
		return weekCount;
	}
	public void setWeekCount(long weekCount) {
		this.weekCount = weekCount;
	}
}
