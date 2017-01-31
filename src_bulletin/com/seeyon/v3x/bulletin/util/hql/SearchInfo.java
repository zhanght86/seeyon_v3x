package com.seeyon.v3x.bulletin.util.hql;

import java.util.Date;

/**
 * 查询公告列表的影响元素：搜索条件
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-14
 */
public class SearchInfo {
	
	/** 标题搜索值 */
	private String title;

	/** 发起人姓名搜索值  */
	private String creatorName;

	/** 发起人ID搜索值  */
	private Long creatorId;

	/** 发布时间或发起时间搜索值：区间下限值  */
	private Date beginDate;

	/** 发布时间或发起时间搜索值：区间上限值  */
	private Date endDate;

	/** 公告所属公告板块ID搜索值　*/
	private Long bulTypeId;

	/** 公告是否置顶 */
	private Boolean topFlag;

	/** 是否综合查询应用场景 */
	private boolean fromIsearch = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getBulTypeId() {
		return bulTypeId;
	}

	public void setBulTypeId(Long bulTypeId) {
		this.bulTypeId = bulTypeId;
	}

	public Boolean getTopFlag() {
		return topFlag;
	}

	public void setTopFlag(Boolean topFlag) {
		this.topFlag = topFlag;
	}

	public boolean isFromIsearch() {
		return fromIsearch;
	}

	public void setFromIsearch(boolean fromIsearch) {
		this.fromIsearch = fromIsearch;
	}

}
