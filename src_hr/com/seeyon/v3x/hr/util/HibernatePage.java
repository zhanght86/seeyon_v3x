/** Copyright (c) 2000-2007, 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 * HibernatePageList.java created by paul at 2007-7-31 下午12:14:06
 *
 */
package com.seeyon.v3x.hr.util;

import java.util.List;

import org.hibernate.Query;

/**
 * 
 * @author paul
 */
public class HibernatePage {
	@SuppressWarnings("unchecked")
	private List results;
	private int pageSize = 20;
	private int pageNo = 0;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		if (pageSize > 0)
			this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		if (pageNo > 0) 
			this.pageNo = pageNo;
	}

	@SuppressWarnings("unchecked")
	public List getResults() {
		return results;
	}

	public HibernatePage(Query query, int pageNo, int pageSize) {
		this.setPageNo(pageNo);
		this.setPageSize(pageSize);
		results = query.setFirstResult(this.getPageNo() * this.getPageSize()).setMaxResults(
				this.getPageSize()).list();

	}
}
