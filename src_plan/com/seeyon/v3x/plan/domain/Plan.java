package com.seeyon.v3x.plan.domain;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:计划实体
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 8:46:35 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class Plan extends BaseModel implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3158319755535113526L;

	/**
	 * 相关项目id
	 */
	private Long refProjectId;
	
	/**
	 * 相关项目id
	 */
	private Long refDepartmentId;
	
	/**
	 * 单位id
	 */
	private Long refAccountId;
	
	/**
	 * 创建者id   没必要存到另一张表中   抽取得时候还要关联表去抽取
	 */
	private Long createUserId;
	
	/**
	 * 计划创建时间     抽取最近建立的计划时用
	 */
	private Date createTime;

	/**
	 * 归档id
	 */
	private Long refPigeonholeId;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 计划开始时间
	 */
	private Date startTime;

	/**
	 * 计划结束时间
	 */
	private Date endTime;

	/**
	 * 计划类型： 1、日计划 2、周计划 3、月计划 4、任意期计划 ,0 部门计划
	 */
	private String type;

	/**
	 * 发布的状态：<br><p>
	 * <STRIKE>(V1.0) 1.保存草稿 2.发布计划 3.计划已总结 4.计划已完成</STRIKE><br>
	 * <STRIKE>(V1.1) 1.草稿  2.未开始  3.进行中 4.已完成</STRIKE><br>
	 * (V1.2) 1.草稿  2.已发布  3.已总结 
	 */
	private String publishStatus;
	
	/**
	 * 计划的状态：<p>
	 *	(V1.0) 1.未开始 2.进行中 3.已完成 4.已取消 5.已推迟
	 */
	private String planStatus;

	/**
	 * 正文
	 */
	private PlanBody planBody;
	
	/**
	 * 完成率
	 */
	private Float finishRatio; 
	
	/**
	 * 附件标识
	 */
	private boolean hasAttachments;

	private List planToLeaderUser;

	private List planCcLeaderUser;

	private List planApprizeUser;

	private List planDraftsmanUser;
	
	private List allPlanRefUser;

	private List planSummary;

	private List planReply;

	// Constructors
	public List getPlanReply() {
		return planReply;
	}

	public void setPlanReply(List planReply) {
		this.planReply = planReply;
	}

	public List getPlanApprizeUser() {
		return planApprizeUser;
	}

	public void setPlanApprizeUser(List planApprizeUser) {
		this.planApprizeUser = planApprizeUser;
	}

	public List getPlanCcLeaderUser() {
		return planCcLeaderUser;
	}

	public void setPlanCcLeaderUser(List planCcLeaderUser) {
		this.planCcLeaderUser = planCcLeaderUser;
	}

	public List getPlanDraftsmanUser() {
		return planDraftsmanUser;
	}

	public void setPlanDraftsmanUser(List planDraftsmanUser) {
		this.planDraftsmanUser = planDraftsmanUser;
	}

	public List getPlanToLeaderUser() {
		return planToLeaderUser;
	}

	public void setPlanToLeaderUser(List planToLeaderUser) {
		this.planToLeaderUser = planToLeaderUser;
	}

	/** default constructor */
	public Plan() {
	}

	/** minimal constructor */
	// public Plan(Date startTime, Date endTime, Date createTime) {
	// this.startTime = startTime;
	// this.endTime = endTime;
	// this.createTime = createTime;
	// }
	/** full constructor */
	// public Plan(Long refPlanStyleId, Long refProjectId, Long refPigeonholeId,
	// String title, Date startTime, Date endTime, Date createTime,
	// String type, String publishStatus) {
	// this.refPlanStyleId = refPlanStyleId;
	// this.refProjectId = refProjectId;
	// this.refPigeonholeId = refPigeonholeId;
	// this.title = title;
	// this.startTime = startTime;
	// this.endTime = endTime;
	// this.createTime = createTime;
	// this.type = type;
	// this.publishStatus = publishStatus;
	// }

	// Property accessors
	public Long getRefProjectId() {
		return this.refProjectId;
	}

	public void setRefProjectId(Long refProjectId) {
		this.refProjectId = refProjectId;
	}

	public Long getRefPigeonholeId() {
		return this.refPigeonholeId;
	}

	public void setRefPigeonholeId(Long refPigeonholeId) {
		this.refPigeonholeId = refPigeonholeId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List getPlanSummary() {
		return planSummary;
	}

	public void setPlanSummary(List planSummary) {
		this.planSummary = planSummary;
	}

	public PlanBody getPlanBody() {
		return planBody;
	}

	public void setPlanBody(PlanBody planBody) {
		this.planBody = planBody;
	}


	public Float getFinishRatio() {
		return finishRatio;
	}

	public void setFinishRatio(Float finishRatio) {
		this.finishRatio = finishRatio;
	}

	public Long getRefDepartmentId() {
		return refDepartmentId;
	}

	public void setRefDepartmentId(Long refDepartmentId) {
		this.refDepartmentId = refDepartmentId;
	}

	public Long getRefAccountId() {
		return refAccountId;
	}

	public void setRefAccountId(Long refAccountId) {
		this.refAccountId = refAccountId;
	}

	public List getAllPlanRefUser() {
		return allPlanRefUser;
	}

	public void setAllPlanRefUser(List allPlanRefUser) {
		this.allPlanRefUser = allPlanRefUser;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isHasAttachments() {
		return hasAttachments;
	}

	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}

	/**
	 * 取得计划的状态：<p>
	 *	(V1.0) 1.未开始 2.进行中 3.已取消 4.已完成 5.已推迟
	 */
	public String getPlanStatus() {
		return planStatus;
	}

	/**
	 * 设置计划的状态：<p>
	 *	(V1.0)1.未开始 2.进行中 3.已取消 4.已完成 5.已推迟
	 */
	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}
	
	/**
	 * 取得发布的状态：<br><p>
	 * <STRIKE>(V1.0) 1.保存草稿 2.发布计划 3.计划已总结 4.计划已完成</STRIKE><br>
	 * <STRIKE>(V1.1) 1.草稿  2.未开始  3.进行中 4.已完成</STRIKE><br>
	 * (V1.2) 1.草稿  2.已发布  3.已总结 
	 */
	public String getPublishStatus() {
		return publishStatus;
	}
	
	/**
	 * 设置发布的状态：<br><p>
	 * <STRIKE>(V1.0) 1.保存草稿 2.发布计划 3.计划已总结 4.计划已完成</STRIKE><br>
	 * <STRIKE>(V1.1) 1.草稿  2.未开始  3.进行中 4.已完成</STRIKE><br>
	 * (V1.2) 1.草稿  2.已发布  3.已总结 
	 */
	public void setPublishStatus(String publishStatus) {
		this.publishStatus = publishStatus;
	}

}