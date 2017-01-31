package com.seeyon.v3x.hr.webmodel;

import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.hr.log.StaffTransferLog;

public class WebOperationLog {
	private String staffName;
	private OperationLog operationLog;
	private StaffTransferLog staffTransferLog;
	private String operation;
	public StaffTransferLog getStaffTransferLog() {
		return staffTransferLog;
	}
	public void setStaffTransferLog(StaffTransferLog staffTransferLog) {
		this.staffTransferLog = staffTransferLog;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public OperationLog getOperationLog() {
		return operationLog;
	}
	public void setOperationLog(OperationLog operationLog) {
		this.operationLog = operationLog;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

}
