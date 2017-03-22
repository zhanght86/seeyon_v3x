package com.seeyon.v3x.hbcb.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * 文件下载记录表
 * 
 * @author 诚佰公司 2017-3-20
 * @version 1.0
 */
public class FileDownload extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = -4051323757814867124L;

	// 表单系统属性
	private Integer state;

	private Long start_member_id;

	private Date start_date;

	private Integer finishedflag;

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getStart_member_id() {
		return start_member_id;
	}

	public void setStart_member_id(Long start_member_id) {
		this.start_member_id = start_member_id;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Integer getFinishedflag() {
		return finishedflag;
	}

	public void setFinishedflag(Integer finishedflag) {
		this.finishedflag = finishedflag;
	}

	/**
	 * 下载人员id
	 */
	private Long memberId;

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

	/**
	 * 单位id
	 */
	private Long accountId;

	/**
	 * 时间戳
	 */
	private String ts;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

}