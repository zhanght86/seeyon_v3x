package com.seeyon.v3x.mobile.webmodel;

import java.util.Date;

import com.seeyon.v3x.edoc.domain.EdocBody;

public class Edoc {
	private String title;//标题
	private Date createDate;//创建时间
	private Long createUserId;//创建用户的id
	private int edocType;//公文类型   0 发文，1 收文，2 签报
	private String emergentType;//紧急程度 0 普通，1紧急，2 特急
	private String edocNumber;//公文文号
	private String inNumber;//内部文号
	private String secretType;//密级 1 普通，2 秘密，3 机密，4 绝密
	private boolean isCanProcess = true;//(在手机端只填写处理意见,如果当前节点对该公文没有处理权限,则该值为false)
	private Long edocId;
	private Long affairid;
	private boolean hasAtts;//是否含有附件
	private int attsNum;//附件的个数
	private Integer state;//状态
	private boolean delated;
	private boolean  hasArchive;//是否被歸檔
	private int appType;
	
	private boolean canOpinion = false;//是否可以填写意见
	private boolean canAttitude = false;//是否可以填写态度
	private boolean canSubmit = false;//是否可以提交
	private boolean canComment = false;//是否可以暂存待办
	
	private Integer opinionPolicy;
	
	private EdocBody edocBody ;
	
	public Long getAffairid() {
		return affairid;
	}
	public void setAffairid(Long affairid) {
		this.affairid = affairid;
	}
	public Long getEdocId() {
		return edocId;
	}
	public void setEdocId(Long edocId) {
		this.edocId = edocId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public String getEdocNumber() {
		return edocNumber;
	}
	public void setEdocNumber(String edocNumber) {
		this.edocNumber = edocNumber;
	}
	public int getEdocType() {
		return edocType;
	}
	public void setEdocType(int edocType) {
		this.edocType = edocType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isCanProcess() {
		return isCanProcess;
	}
	public void setCanProcess(boolean isCanProcess) {
		this.isCanProcess = isCanProcess;
	}
	public boolean isHasAtts() {
		return hasAtts;
	}
	public void setHasAtts(boolean hasAtts) {
		this.hasAtts = hasAtts;
	}

	public boolean isCanAttitude() {
		return canAttitude;
	}
	public void setCanAttitude(boolean canAttitude) {
		this.canAttitude = canAttitude;
	}
	public boolean isCanComment() {
		return canComment;
	}
	public void setCanComment(boolean canComment) {
		this.canComment = canComment;
	}
	public boolean isCanSubmit() {
		return canSubmit;
	}
	public void setCanSubmit(boolean canSubmit) {
		this.canSubmit = canSubmit;
	}

	public boolean isCanOpinion() {
		return canOpinion;
	}
	public void setCanOpinion(boolean canOpinion) {
		this.canOpinion = canOpinion;
	}
	public String getInNumber() {
		return inNumber;
	}
	public void setInNumber(String inNumber) {
		this.inNumber = inNumber;
	}
	public int getAttsNum() {
		return attsNum;
	}
	public void setAttsNum(int attsNum) {
		this.attsNum = attsNum;
	}
	public String getEmergentType() {
		return emergentType;
	}
	public void setEmergentType(String emergentType) {
		this.emergentType = emergentType;
	}
	public String getSecretType() {
		return secretType;
	}
	public void setSecretType(String secretType) {
		this.secretType = secretType;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public boolean isDelated() {
		return delated;
	}
	public void setDelated(boolean delated) {
		this.delated = delated;
	}
	public boolean isHasArchive() {
		return hasArchive;
	}
	public void setHasArchive(boolean hasArchive) {
		this.hasArchive = hasArchive;
	}
	public int getAppType() {
		return appType;
	}
	public void setAppType(int appType) {
		this.appType = appType;
	}
	public Integer getOpinionPolicy() {
		return opinionPolicy;
	}
	public void setOpinionPolicy(Integer opinionPolicy) {
		this.opinionPolicy = opinionPolicy;
	}
	public EdocBody getEdocBody() {
		return edocBody;
	}
	public void setEdocBody(EdocBody edocBody) {
		this.edocBody = edocBody;
	}
	

}
