/**
 * 
 */
package com.seeyon.v3x.blog.webmodel;

import java.sql.Date;

import com.seeyon.v3x.blog.domain.BlogFamily;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class ArticleModel {
	/**
	 * ID
	 */
	private Long id ;
	
	/**
	 * 主题名称
	 */
	private String subject = null;
	
	/**
	 * 所属单位
	 */
	private Long idCompany = null;
	
	/**
	 * 发布者ID
	 */
	private Long employeeId ;
	/**
	 * 发布者名称
	 */
	private String userName;
	
	/**
	 * 分类
	 */
	private Long familyId ;
	private String familyName;
	private Byte state;
	private Long favoritesId;
	
	/**
	 * 点击数
	 */
	private Integer clickNumber = null;
	
	/**
	 * 回复数
	 */
	private Integer replyNumber = null;
	
	/**
	 * 发布日期
	 */
	private Date issueTime = null;
	
	/**
	 * 修改日期
	 */
	private Date modifyTime = null;
	/**
	 * 年
	 */
	private Integer y;
	/**
	 * 月
	 */
	private Integer m;
	/**
	 * 附件标志 1---有附件；0----无附件
	 */
	private Byte attachmentFlag;
	
	
	public ArticleModel(){
		
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the clickNumber
	 */
	public Integer getClickNumber() {
		return clickNumber;
	}

	/**
	 * @param clickNumber the clickNumber to set
	 */
	public void setClickNumber(Integer clickNumber) {
		this.clickNumber = clickNumber;
	}

	/**
	 * @return the issueTime
	 */
	public Date getIssueTime() {
		return issueTime;
	}

	/**
	 * @param issueTime the issueTime to set
	 */
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}
	/**
	 * @return the modifyTime
	 */
	public Date getModifyTime() {
		return modifyTime;
	}

	/**
	 * @param modifyTime the modifyTime to set
	 */
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	/**
	 * @return the familyId
	 */
	public Long getFamilyId() {
		return familyId;
	}
	public Long getFavoritesId() {
		return this.favoritesId;
	}
	public void setFavoritesId(Long favoritesId) {
		this.favoritesId = favoritesId;
	}

	/**
	 * @param employeeId the familyId to set
	 */
	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}
	/**
	 * @return the employeeId
	 */
	public Long getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the replyNumber
	 */
	public Integer getReplyNumber() {
		return replyNumber;
	}

	/**
	 * @param replyNumber the replyNumber to set
	 */
	public void setReplyNumber(Integer replyNumber) {
		this.replyNumber = replyNumber;
	}
	/**
	 * @return the idCompany
	 */
	public Long getIdCompany() {
		return idCompany;
	}

	/**
	 * @param idCompany the idCompany to set
	 */
	public void setIdCompany(Long idCompany) {
		this.idCompany = idCompany;
	}

	public Integer getY() {
		return this.y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Integer getM() {
		return this.m;
	}
	public void setM(Integer m) {
		this.m = m;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the attachmentFlag
	 */
	public Byte getAttachmentFlag() {
		return attachmentFlag;
	}

	/**
	 * @param attachmentFlag the attachmentFlag to set
	 */
	public void setAttachmentFlag(Byte attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}

	public String getFamilyName() {
		return familyName;
	}


	
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public Byte getShareState(){
		 return state;
	}
	
	public void setShareState(Byte state){
		
		this.state = state;
	}
	
}
