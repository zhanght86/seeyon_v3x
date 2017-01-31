/**
 * 
 */
package com.seeyon.v3x.bbs.webmodel;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsArticleIssueArea;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;

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
	private String articleName = "";
	
	/**
	 * 所属版块
	 */
	private V3xBbsBoard board = null;
	
	/**
	 * 发布者名称
	 */
	private Long issueUser = null;
	
	/**
	 * 发布范围
	 */
	private List<V3xBbsArticleIssueArea> issueArea = null;
	
	/**
	 * 点击数
	 */
	private int clickNumber;
	
	/**
	 * 回复数
	 */
	private int replyNumber;
	
	/**
	 * 发布日期
	 */
	private Date issueTime = null;
	
	/**
	 * 精华标志
	 */
	private boolean eliteFlag;
	/**
	 * 有消息通知与否标志
	 */
	private Boolean messageNotifyFlag;
	/**
	 * 来源标志
	 */
	private byte resourceFlag;
	
	/**
	 * 附件标志 1---有附件；0----无附件
	 */
	private boolean attachmentFlag;
	
	private boolean anonymousFlag;
	
	private Long accountId;
	
	/**
	 * 置顶序号
	 */
	private int topSequence;
	
	public ArticleModel(){
	}
	
	public ArticleModel(V3xBbsArticle v3xBbsArticle){
		this.setId(v3xBbsArticle.getId());
		this.setIssueUser(v3xBbsArticle.getIssueUserId());
		this.setArticleName(v3xBbsArticle.getArticleName());
		this.setClickNumber(v3xBbsArticle.getClickNumber());
		this.setReplyNumber(v3xBbsArticle.getReplyNumber());
		this.setIssueTime(v3xBbsArticle.getIssueTime());
		this.setEliteFlag(v3xBbsArticle.getEliteFlag());
		this.setMessageNotifyFlag(v3xBbsArticle.getMessageNotifyFlag());
		this.setResourceFlag(v3xBbsArticle.getResourceFlag());
		this.setTopSequence(v3xBbsArticle.getTopSequence());
		this.setAttachmentFlag(v3xBbsArticle.isHasAttachments());
		this.setAnonymousFlag(v3xBbsArticle.getAnonymousFlag());
		this.setAccountId(v3xBbsArticle.getAccountId());
	}
	
	/**
	 * @return the articleName
	 */
	public String getArticleName() {
		return articleName;
	}

	/**
	 * @param articleName the articleName to set
	 */
	public void setArticleName(String articleName) {
		this.articleName = articleName;
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
	 * @return the issueArea
	 */
	public List<V3xBbsArticleIssueArea> getIssueArea() {
		return issueArea;
	}

	/**
	 * @param issueArea the issueArea to set
	 */
	public void setIssueArea(List<V3xBbsArticleIssueArea> issueArea) {
		this.issueArea = issueArea;
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
	 * @return the issueUserName
	 */
	public Long getIssueUser() {
		return issueUser;
	}

	/**
	 * @param issueUserName the issueUserName to set
	 */
	public void setIssueUser(Long issueUser) {
		this.issueUser = issueUser;
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
	 * @return the eliteFlag
	 */
	public Boolean getEliteFlag() {
		return eliteFlag;
	}

	/**
	 * @param eliteFlag the eliteFlag to set
	 */
	public void setEliteFlag(Boolean eliteFlag) {
		this.eliteFlag = eliteFlag;
	}

	/**
	 * @return the messageNotifyFlag
	 */
	public Boolean getMessageNotifyFlag() {
		return messageNotifyFlag;
	}

	/**
	 * @param messageNotifyFlag the messageNotifyFlag to set
	 */
	public void setMessageNotifyFlag(Boolean messageNotifyFlag) {
		this.messageNotifyFlag = messageNotifyFlag;
	}

	/**
	 * @return the resourceFlag
	 */
	public Byte getResourceFlag() {
		return resourceFlag;
	}

	/**
	 * @param resourceFlag the resourceFlag to set
	 */
	public void setResourceFlag(Byte resourceFlag) {
		this.resourceFlag = resourceFlag;
	}

	/**
	 * @return the topSequence
	 */
	public Integer getTopSequence() {
		return topSequence;
	}

	/**
	 * @param topSequence the topSequence to set
	 */
	public void setTopSequence(Integer topSequence) {
		this.topSequence = topSequence;
	}

	public boolean isAttachmentFlag() {
		return attachmentFlag;
	}

	public void setAttachmentFlag(boolean attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}

	/**
	 * @return the board
	 */
	public V3xBbsBoard getBoard() {
		return board;
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(V3xBbsBoard board) {
		this.board = board;
	}

	public boolean isAnonymousFlag() {
		return anonymousFlag;
	}

	public void setAnonymousFlag(boolean anonymousFlag) {
		this.anonymousFlag = anonymousFlag;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
}
