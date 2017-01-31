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
public class ShareModel {
	
	/**
	 * ID
	 */
	private Long id ;
	
	
	/**
	 * 共享类型
	 */
	private String type = null;

	
	/**
	 * 共享员工
	 */
	private Long shareId = null;
	/**
	 * 共享用户姓名
	 */
	private String userName = null;
	/**
	 * 所属部门
	 */
	private String departmentName = null;
	/**
	 * 主要岗位
	 */
	private String levelName = null;
	/**
	 * 职务级别
	 */
	private String postName = null;
	/**
	 * 介绍
	 */
	private String introduce = null;
	/**
	 * 文章数
	 */
	private Integer articleNumber = null;
	/**
	 * 图片
	 */
	private String image = null;
	
	/**
	 * 员工编号
	 */
	private Long employeeId = null;

	public ShareModel(){
		
	}
	/**
	 * @return the userName
	 */
	public String getIntroduce() {
		return introduce;
	}
	/**
	 * @param introduce the introduce to set
	 */
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
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
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
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
	 * @param departmentName the departmentName to set
	 */
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	/**
	 * @return the departmentName
	 */
	public String getDepartmentName() {
		return departmentName;
	}
	/**
	 * @return the levelName
	 */
	public String getLevelName() {
		return levelName;
	}
	/**
	 * @param levelName the levelName to set
	 */
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	/**
	 * @return the postName
	 */
	public String getPostName() {
		return postName;
	}
	/**
	 * @param postName the postName to set
	 */
	public void setPostName(String postName) {
		this.postName = postName;
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
	 * @return the shareId
	 */
	public Long getShareId() {
		return shareId;
	}

	/**
	 * @param shareId the shareId to set
	 */
	public void setShareId(Long shareId) {
		this.shareId = shareId;
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
}
