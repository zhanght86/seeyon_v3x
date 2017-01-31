/**
 * $Id: StaffTransferLog.java,v 1.2 2007/08/11 03:59:39 wangj Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * StaffTransferLog.java created by paul at 2007-8-11 上午11:01:17
 *
 */
package com.seeyon.v3x.hr.log;

import com.seeyon.v3x.hr.domain.StaffTransferType;

/**
 * <tt>StaffTransferLog</tt>日志格式对象，支持XML
 * 
 * @author paul
 *
 */
public class StaffTransferLog {
	private String staffName;
	private StaffTransferType staffTransferType;
	
	public StaffTransferLog() {}
	public StaffTransferLog(String staffName, StaffTransferType staffTransferType) {
		this.staffName = staffName;
		this.staffTransferType = staffTransferType;
	}
	
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public StaffTransferType getStaffTransferType() {
		return staffTransferType;
	}
	public void setStaffTransferType(StaffTransferType staffTransferType) {
		this.staffTransferType = staffTransferType;
	}
	
}
