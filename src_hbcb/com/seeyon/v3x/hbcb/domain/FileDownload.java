package com.seeyon.v3x.hbcb.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * 
 * 文件下载记录表
 * 
 * @author 诚佰公司 2017-3-20
 * @version 1.0
 */
public class FileDownload extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = -4051323757814867124L;

	/**
	 * 状态
	 */
	private Integer state;

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * 下载人员id
	 */
	private Long memberId;

	private V3xOrgMember member;

	/**
	 * 文件id
	 */
	private Long fileId;

	/**
	 * 文件名
	 */
	private String filename;

	/**
	 * 下载次数
	 */
	private Integer times;

	/**
	 * 部门id
	 */
	private Long departmentId;

	private V3xOrgDepartment department;

	/**
	 * 单位id
	 */
	private Long accountId;

	/**
	 * 时间戳
	 */
	private Date ts;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public V3xOrgDepartment getDepartment() {
		return department;
	}

	public void setDepartment(V3xOrgDepartment department) {
		this.department = department;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

}