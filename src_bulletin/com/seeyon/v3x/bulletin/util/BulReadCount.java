package com.seeyon.v3x.bulletin.util;

import java.util.Date;

public class BulReadCount implements java.io.Serializable, Comparable<BulReadCount> {
	private static final long serialVersionUID = 1L;
	
	private Long userId;//人员Id
	private Long deptId;//部门ID
	private Long accountId;//单位ID
	private int memberCount;//总人数
	private int notReadCount;//未读
	private int endReadCount;//已读
	private Date readDate;//阅读时间
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public int getEndReadCount() {
		return endReadCount;
	}
	public void setEndReadCount(int endReadCount) {
		this.endReadCount = endReadCount;
	}
	public int getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}
	public int getNotReadCount() {
		return notReadCount;
	}
	public void setNotReadCount(int notReadCount) {
		this.notReadCount = notReadCount;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public Date getReadDate() {
		return readDate;
	}
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	/**
	 * 排序：已读人数多少(降序)、部门id、阅读日期。以确保前端有序展现
	 */
	public int compareTo(BulReadCount bulReadCount) {
		int resultByReadCount = -(Integer.valueOf(this.getEndReadCount()).compareTo(Integer.valueOf(bulReadCount.getEndReadCount())));
		int resultByDeptId = this.getDeptId().compareTo(bulReadCount.getDeptId());
		if(resultByReadCount!=0) 
			return resultByReadCount;
		else if(resultByDeptId!=0)
			return resultByDeptId;
		else {
			Date readDate = this.getReadDate();
			Date toCompareReadDate = bulReadCount.getReadDate();
			if(readDate==null && toCompareReadDate==null) {
				return 0;
			} else if(readDate==null && toCompareReadDate!=null) {
				return -1;
			} else if(readDate!=null && toCompareReadDate==null) {
				return 1;
			} else {
				long time1 = readDate.getTime();
				long time2 = toCompareReadDate.getTime();
				if(time1>time2) 
					return 1;
				else if(time1<time2)
					return -1;
				else
					return 0;
			}
		}
	}
		
}
