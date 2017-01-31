package com.seeyon.v3x.bbs.util;

import java.io.Serializable;
/**
 * 楼主修改主贴时，同步集群缓存，传输的数据模型，包括帖子ID和点击次数
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-13
 */
public class CacheInfo implements Serializable {

	public CacheInfo(Long dataId, int clickCount) {
		super();
		this.dataId = dataId;
		this.clickCount = clickCount;
	}
	
	public CacheInfo(Long dataId, Long userId) {
		super();
		this.dataId = dataId;
		this.userId = userId;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7789392117880373728L;


	public Long getDataId() {
		return dataId;
	}

	public void setDataId(Long dataId) {
		this.dataId = dataId;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * bbs讨论帖子ID
	 */
	private Long dataId;
	/**
	 * 帖子点击数
	 */
	private int clickCount;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	
}
