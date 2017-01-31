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
public class EmployeeModel {
	
	private Long id;
	private String introduce;
	private Integer articleNumber;
	private String image;
	private Long idCompany;
	private Byte flagStart;
	private Byte flagShare;
	/**
	 * 姓名
	 */
	private String userName = null;
	//博客空间
	private String blogSpace;
	private String blogUsedSpace;
	private String blogStatus;

    public EmployeeModel() {
    }

	public String getIntroduce() {
		return this.introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
	public Integer getArticleNumber() {
		return this.articleNumber;
	}
	public void setArticleNumber(Integer articleNumber) {
		this.articleNumber = articleNumber;
	}
	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Long getIdCompany() {
		return this.idCompany;
	}
	public void setIdCompany(Long idCompany) {
		this.idCompany = idCompany;
	}

	public Byte getFlagStart() {
		return flagStart;
	}
	public void setFlagStart(Byte flagStart) {
		this.flagStart = flagStart;
	}
	public Byte getFlagShare() {
		return flagShare;
	}
	public void setFlagShare(Byte flagShare) {
		this.flagShare = flagShare;
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
	public String getBlogSpace() {
		return blogSpace;
	}
	
	public void setBlogSpace(String blogSpace) {
		this.blogSpace = blogSpace;
	}
	
	public String getBlogUsedSpace() {
		return blogUsedSpace;
	}
	
	public void setBlogUsedSpace(String blogUsedSpace) {
		this.blogUsedSpace = blogUsedSpace;
	}
	
	public String getBlogStatus() {
		return blogStatus;
	}
	
	public void setBlogStatus(String blogStatus) {
		this.blogStatus = blogStatus;
	}
}
