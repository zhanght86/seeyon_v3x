/**
 * 
 */
package com.seeyon.v3x.blog.webmodel;

import java.sql.Timestamp;
import java.util.List;

import com.seeyon.v3x.blog.domain.BlogArticle;
import com.seeyon.v3x.common.filemanager.Attachment;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class ArticleReplyModel {
	
	private Long id;
	private Long parentId;
	private String suject;
	private String content;
	
	private String useReplyHtml;
	
	//是否能被当前用户删除标志
	private Byte canBeDeleteFlag;
	
	private String refPostIssueUserName;
	private Timestamp refPostIssueTime;
	private String refPostContent;
	private Byte useReplyFlag;
	
	private Timestamp issueTime;
	/**
	 * 回复者姓名
	 */
	private String replyUserName;
	/**
	 * 回复者所在部门名称
	 */
	private String replyUserDepartment;
	/**
	 * 回复者职位名称
	 */
	private String replyUserLevel;
	
	private BlogArticle BlogArticle;
	private Long employeeId;
	private Long articleId;
	
	private List<Attachment> attachment;
	
	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getParentId() {
		return this.parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Long getArticleId() {
		return this.articleId;
	}
	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	
	/**
	 * @return the attachment
	 */
	public List<Attachment> getAttachment() {
		return attachment;
	}
	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(List<Attachment> attachment) {
		this.attachment = attachment;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the suject
	 */
	public String getSuject() {
		return suject;
	}
	/**
	 * @param suject the suject to set
	 */
	public void setSuject(String suject) {
		this.suject = suject;
	}
	/**
	 * @return the replyTime
	 */
	public Timestamp getIssueTime() {
		return issueTime;
	}
	/**
	 * @param replyTime the replyTime to set
	 */
	public void setIssueTime(Timestamp issueTime) {
		this.issueTime = issueTime;
	}

	/**
	 * @return the BlogArticle
	 */
	public BlogArticle getBlogArticle() {
		return BlogArticle;
	}
	/**
	 * @param blogArticle the BlogArticle to set
	 */
	public void setBlogArticle(BlogArticle blogArticle) {
		BlogArticle = blogArticle;
	}
	/**
	 * @return the replyUserDepartment
	 */
	public String getReplyUserDepartment() {
		return replyUserDepartment;
	}
	/**
	 * @param replyUserDepartment the replyUserDepartment to set
	 */
	public void setReplyUserDepartment(String replyUserDepartment) {
		this.replyUserDepartment = replyUserDepartment;
	}
	/**
	 * @return the replyUserLevel
	 */
	public String getReplyUserLevel() {
		return replyUserLevel;
	}
	/**
	 * @param replyUserLevel the replyUserLevel to set
	 */
	public void setReplyUserLevel(String replyUserLevel) {
		this.replyUserLevel = replyUserLevel;
	}
	/**
	 * @return the replyUserName
	 */
	public String getReplyUserName() {
		return replyUserName;
	}
	/**
	 * @param replyUserName the replyUserName to set
	 */
	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}
	
	/**
	 * @return the useReplyHtml
	 */
	public String getUseReplyHtml() {
		return useReplyHtml;
	}
	/**
	 * @param useReplyHtml the useReplyHtml to set
	 */
	public void setUseReplyHtml(String useReplyHtml) {
		this.useReplyHtml = useReplyHtml;
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
	 * @return the canBeDeleteFlag
	 */
	public Byte getCanBeDeleteFlag() {
		return canBeDeleteFlag;
	}
	/**
	 * @param canBeDeleteFlag the canBeDeleteFlag to set
	 */
	public void setCanBeDeleteFlag(Byte canBeDeleteFlag) {
		this.canBeDeleteFlag = canBeDeleteFlag;
	}
	
	/**
	 * @return the useReplyFlag
	 */
	public Byte getUseReplyFlag() {
		return useReplyFlag;
	}
	/**
	 * @param useReplyFlag the useReplyFlag to set
	 */
	public void setUseReplyFlag(Byte useReplyFlag) {
		this.useReplyFlag = useReplyFlag;
	}
	
	/**
	 * @return the refPostContent
	 */
	public String getRefPostContent() {
		return refPostContent;
	}
	/**
	 * @param refPostContent the refPostContent to set
	 */
	public void setRefPostContent(String refPostContent) {
		this.refPostContent = refPostContent;
	}

	/**
	 * @return the refPostIssueUserName
	 */
	public String getRefPostIssueUserName() {
		return refPostIssueUserName;
	}
	/**
	 * @param refPostIssueUserName the refPostIssueUserName to set
	 */
	public void setRefPostIssueUserName(String refPostIssueUserName) {
		this.refPostIssueUserName = refPostIssueUserName;
	}
	/**
	 * @return the refPostIssueTime
	 */
	public Timestamp getRefPostIssueTime() {
		return refPostIssueTime;
	}
	/**
	 * @param refPostIssueTime the refPostIssueTime to set
	 */
	public void setRefPostIssueTime(Timestamp refPostIssueTime) {
		this.refPostIssueTime = refPostIssueTime;
	}
	
}
