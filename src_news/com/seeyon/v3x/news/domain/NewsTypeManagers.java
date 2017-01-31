package com.seeyon.v3x.news.domain;

import com.seeyon.v3x.news.domain.base.BaseNewsTypeManagers;



public class NewsTypeManagers extends BaseNewsTypeManagers implements Comparable<NewsTypeManagers>{
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public NewsTypeManagers () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public NewsTypeManagers (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public NewsTypeManagers (
		java.lang.Long id,
		com.seeyon.v3x.news.domain.NewsType type,
		java.lang.Long managerId) {

		super (
			id,
			type,
			managerId);
	}
	
	/**
	 * 实现排序以便保持管理员排序
	 */
	public int compareTo(NewsTypeManagers o) {
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

/*[CONSTRUCTOR MARKER END]*/


}