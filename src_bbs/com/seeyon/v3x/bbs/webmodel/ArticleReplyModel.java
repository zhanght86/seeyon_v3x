/**
 * 
 */
package com.seeyon.v3x.bbs.webmodel;

import java.sql.Timestamp;

/**
 * 类描述： 创建日期：
 * 
 * @author liaoj
 * @version 1.0
 * @since JDK 5.0
 */
public class ArticleReplyModel implements Comparable<ArticleReplyModel> {

	private Long id;

	private String replyName;

	private String content;

	private String useReplyHtml;

	// 是否能被当前用户删除标志
	private Byte canBeDeleteFlag;
	
	// 是否能被当前用户修改标志 added by Meng Yang 2009-05-07
	private Byte canBeEditedFlag;

	private String refPostIssueUserName;

	private Timestamp refPostIssueTime;

	private String refPostContent;

	private Byte useReplyFlag;

	private Timestamp replyTime;
	
	private Timestamp modifyTime;
	
	//是否匿名回复
	private Boolean anonymousFlag;
	
	private Long replyUserId;
	
	private String imageType;//个性照片显示方式
	
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

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the replyName
	 */
	public String getReplyName() {
		return replyName;
	}

	/**
	 * @param replyName
	 *            the replyName to set
	 */
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}

	/**
	 * @return the replyTime
	 */
	public Timestamp getReplyTime() {
		return replyTime;
	}

	/**
	 * @param replyTime
	 *            the replyTime to set
	 */
	public void setReplyTime(Timestamp replyTime) {
		this.replyTime = replyTime;
	}

	/**
	 * @return the useReplyHtml
	 */
	public String getUseReplyHtml() {
		return useReplyHtml;
	}

	/**
	 * @param useReplyHtml
	 *            the useReplyHtml to set
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
	 * @param id
	 *            the id to set
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
	 * @param canBeDeleteFlag
	 *            the canBeDeleteFlag to set
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
	 * @param useReplyFlag
	 *            the useReplyFlag to set
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
	 * @param refPostContent
	 *            the refPostContent to set
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
	 * @param refPostIssueUserName
	 *            the refPostIssueUserName to set
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
	 * @param refPostIssueTime
	 *            the refPostIssueTime to set
	 */
	public void setRefPostIssueTime(Timestamp refPostIssueTime) {
		this.refPostIssueTime = refPostIssueTime;
	}

	public Boolean getAnonymousFlag() {
		return anonymousFlag;
	}

	public void setAnonymousFlag(Boolean anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public Long getReplyUserId() {
		return replyUserId;
	}

	public void setReplyUserId(Long replyUserId) {
		this.replyUserId = replyUserId;
	}

	public Byte getCanBeEditedFlag() {
		return canBeEditedFlag;
	}

	public void setCanBeEditedFlag(Byte canBeEditedFlag) {
		this.canBeEditedFlag = canBeEditedFlag;
	}

	public Timestamp getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public int compareTo(ArticleReplyModel o) {
		Timestamp t1 = this.getReplyTime();
		if(o == null) {
			return 1;
		} else {
			Timestamp t2 = o.getReplyTime();
			if(t2 == null) {
				return 1;
			} else {
				return t1.compareTo(t2);
			}
		}
	}

}
