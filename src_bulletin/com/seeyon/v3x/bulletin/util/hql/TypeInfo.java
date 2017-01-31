package com.seeyon.v3x.bulletin.util.hql;

import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;

/**
 * 查询公告列表的影响元素：公告板块信息
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-7-30
 */
public class TypeInfo {

	/** 板块ID，此值为空时表明查看全部板块，反之为查看某一特定板块 */
	private Long typeId;

	/** 板块所属单位或集团的ID */
	private Long accountId;

	/** 板块所属空间类型 */
	private Constants.BulTypeSpaceType spaceType;

	public TypeInfo(Long typeId, Long accountId, BulTypeSpaceType spaceType) {
		super();
		this.typeId = typeId;
		this.accountId = accountId;
		this.spaceType = spaceType;
	}

	public TypeInfo(Long typeId) {
		super();
		this.typeId = typeId;
	}

	public TypeInfo(BulTypeSpaceType spaceType) {
		super();
		this.spaceType = spaceType;
	}

	public TypeInfo() {}
	
	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Constants.BulTypeSpaceType getSpaceType() {
		return spaceType;
	}

	public void setSpaceType(Constants.BulTypeSpaceType spaceType) {
		this.spaceType = spaceType;
	}

	
}
