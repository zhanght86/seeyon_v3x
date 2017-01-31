/**
 * 
 */
package com.seeyon.v3x.blog.webmodel;

/**
 * 类描述：
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */
public class FamilyModel {
	
	/**
	 * ID
	 */
	private Long id ;
	
	/**
	 * 分类今天是否有新帖 1--有新帖 ； 0---无新帖
	 */
	private Byte hasNewPostFlag ;
	
	/**
	 * 判断当前用户是否是管理员 1--是 ； 0---否
	 */
	private Byte isAdminFlag ;
	
	/**
	 * 分类名称
	 */
	private String nameFamily = null;
	
	/**
	 * 分类描述
	 */
	private String remark = null;
	
	/**
	 * 分类类型
	 */
	private String type = null;

	/**
	 * 分类文章数
	 */
	private Integer articleNumber = null;
	
	/**
	 * 显示序号
	 */
	private Integer seqDisplay = null;
	
	/**
	 * 员工编号
	 */
	private Long employeeId = null;

	public FamilyModel(){
		
	}

	/**
	 * @return the articleNumber
	 */
	public Integer getArticleNumber() {
		return articleNumber;
	}

	/**
	 * @param articleNumber the articleNumber to set
	 */
	public void setArticleNumber(Integer articleNumber) {
		this.articleNumber = articleNumber;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the nameFamily
	 */
	public String getNameFamily() {
		return nameFamily;
	}

	/**
	 * @param nameFamily the nameFamily to set
	 */
	public void setNameFamily(String nameFamily) {
		this.nameFamily = nameFamily;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the seqDisplay
	 */
	public Integer getSeqDisplay() {
		return seqDisplay;
	}

	/**
	 * @param seqDisplay the seqDisplay to set
	 */
	public void setSeqDisplay(Integer seqDisplay) {
		this.seqDisplay = seqDisplay;
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
	 * @return the hasNewPostFlag
	 */
	public Byte getHasNewPostFlag() {
		return hasNewPostFlag;
	}

	/**
	 * @param hasNewPostFlag the hasNewPostFlag to set
	 */
	public void setHasNewPostFlag(Byte hasNewPostFlag) {
		this.hasNewPostFlag = hasNewPostFlag;
	}

	/**
	 * @return the isAdminFlag
	 */
	public Byte getIsAdminFlag() {
		return isAdminFlag;
	}

	/**
	 * @param isAdminFlag the isAdminFlag to set
	 */
	public void setIsAdminFlag(Byte isAdminFlag) {
		this.isAdminFlag = isAdminFlag;
	}
}
