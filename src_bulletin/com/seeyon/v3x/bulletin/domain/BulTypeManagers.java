package com.seeyon.v3x.bulletin.domain;

import com.seeyon.v3x.bulletin.domain.base.BaseBulTypeManagers;

public class BulTypeManagers extends BaseBulTypeManagers implements Comparable<BulTypeManagers>{
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public BulTypeManagers () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public BulTypeManagers (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public BulTypeManagers (
		java.lang.Long id,
		com.seeyon.v3x.bulletin.domain.BulType type,
		java.lang.Long managerId) {

		super (
			id,
			type,
			managerId);
	}

/*[CONSTRUCTOR MARKER END]*/
	
	/**
	 * 实现排序以便保持管理员排序
	 */
	public int compareTo(BulTypeManagers o) {
		// 空指针防护
		if(this.getOrderNum()!=null && o.getOrderNum()!=null) {
			if(this.getOrderNum().intValue() > o.getOrderNum().intValue()) {
				return 1;
			} else if(this.getOrderNum().intValue() < o.getOrderNum().intValue()) {
				return -1;
			}
		} else if(this.getOrderNum()!=null && o.getOrderNum()==null) {
			return 1;
		} else if(this.getOrderNum()==null && o.getOrderNum()!=null) {
			return -1;
		}
		return 0;
	}


}