package com.seeyon.v3x.hr.webmodel;

public class ImportReport {

	private String deptName;
	private String memberName;
	private boolean success;
	private int error;

	public ImportReport(String deptName, String memberName, boolean success, int error) {
		this.deptName = deptName;
		this.memberName = memberName;
		this.success = success;
		this.error = error;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

}