package com.seeyon.v3x.bulletin.util.hql;

/**
 * 查询公告列表的影响元素：分页信息<br>
 * 包含是否需要取总数，第一条起始处，每页所取条数等元素<br>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-8-10
 */
public class PageInfo {
	/** 是否需要获取公告总条数 */
	private boolean needCount;
	
	/** 当前分页信息的起始点 */
	private int firstResult = -1;
	
	/** 当前分页信息的每页所取条数 */
	private int maxResults = -1;

	public PageInfo() {}
	
	/** 只设置是否需要取总数 */
	public PageInfo(boolean needCount) {
		super();
		this.needCount = needCount;
	}
	
	/** 完整分页信息设置 */
	public PageInfo(boolean needCount, int firstResult, int maxResults) {
		this(needCount);
		this.firstResult = firstResult;
		this.maxResults = maxResults;
	}
	
	public boolean isNeedCount() {
		return needCount;
	}

	public void setNeedCount(boolean needCount) {
		this.needCount = needCount;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

}
