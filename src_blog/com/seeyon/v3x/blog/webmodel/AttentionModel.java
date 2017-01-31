/**
 * 
 */
package com.seeyon.v3x.blog.webmodel;

import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * 类描述：
 * 创建日期：
 *
 * @author xiaoqiuhe
 * @version 1.0 
 * @since JDK 5.0
 */
public class AttentionModel {
	
	/**
	 * ID
	 */
	private Long id ;
	
	/**
	 * 关注员工
	 */
	private Long attentionId = null;
	/**
	 * 关注用户姓名
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
	 * 博客开放标记
	 */
	private Byte flagStart = null;
	
	private boolean startFlag = false ;
	
	/**
	 * 员工编号
	 */
	private Long employeeId = null;
    private String typeProperty = V3xOrgMember.ORGENT_TYPE_MEMBER;	//parseElements（）这个方法中用到
    
    private String imageType; //个性照片显示方式
	private String self_image_name;	//个性照片文件的名称

	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getSelf_image_name() {
		return self_image_name;
	}
	public void setSelf_image_name(String self_image_name) {
		this.self_image_name = self_image_name;
	}
	
	public AttentionModel(){
		
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
	 * @return the flagStart
	 */
	public Byte getFlagStart() {
		return flagStart;
	}

	/**
	 * @param flagStart the flagStart to set
	 */
	public void setFlagStart(Byte flagStart) {
		this.flagStart = flagStart;
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
	 * @return the attentionId
	 */
	public Long getAttentionId() {
		return attentionId;
	}

	/**
	 * @param attentionId the attentionId to set
	 */
	public void setAttentionId(Long attentionId) {
		this.attentionId = attentionId;
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
	public String getTypeProperty() {
		return typeProperty;
	}
	public void setTypeProperty(String typeProperty) {
		this.typeProperty = typeProperty;
	}
	public boolean isStartFlag() {
		return startFlag ;
	}
	public void setStartFlag(boolean startFlag) {
		this.startFlag = startFlag ;     
	}
}
