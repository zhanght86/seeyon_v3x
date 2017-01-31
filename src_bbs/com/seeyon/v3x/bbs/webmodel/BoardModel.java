/**
 * 
 */
package com.seeyon.v3x.bbs.webmodel;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class BoardModel {
	
	/**
	 * ID
	 */
	private Long id ;
	
	/**
	 * 版块今天是否有新帖 1--有新帖 ； 0---无新帖
	 */
	private Boolean hasNewPostFlag ;
	
	/**
	 * 判断当前用户是否是管理员 1--是 ； 0---否
	 */
	private Boolean isAdminFlag ;
	
	/**
	 * 版块名称
	 */
	private String boardName = null;
	
	/**
	 * 版块描述
	 */
	private String boardDescription = null;
	
	/**
	 * 版块主题数
	 */
	private int articleNumber;
	
	/**
	 * 版块总帖数
	 */
	private int sumPostNumber;
	
	/**
	 * 版块精华帖数
	 */
	private int elitePostNumber;
	
	private boolean isOtherAccount;
	
	private boolean hasAuthIssue;
	
	private V3xBbsBoard board;

	public BoardModel(){
		
	}
	
	/**
	 * @return the articleNumber
	 */
	public int getArticleNumber() {
		return articleNumber;
	}

	/**
	 * @param articleNumber the articleNumber to set
	 */
	public void setArticleNumber(int articleNumber) {
		this.articleNumber = articleNumber;
	}

	/**
	 * @return the boardDescription
	 */
	public String getBoardDescription() {
		return boardDescription;
	}

	/**
	 * @param boardDescription the boardDescription to set
	 */
	public void setBoardDescription(String boardDescription) {
		this.boardDescription = boardDescription;
	}

	/**
	 * @return the boardName
	 */
	public String getBoardName() {
		return boardName;
	}

	/**
	 * @param boardName the boardName to set
	 */
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	/**
	 * @return the elitePostNumber
	 */
	public int getElitePostNumber() {
		return elitePostNumber;
	}

	/**
	 * @param elitePostNumber the elitePostNumber to set
	 */
	public void setElitePostNumber(int elitePostNumber) {
		this.elitePostNumber = elitePostNumber;
	}

	/**
	 * @return the sumPostNumber
	 */
	public int getSumPostNumber() {
		return sumPostNumber;
	}

	/**
	 * @param sumPostNumber the sumPostNumber to set
	 */
	public void setSumPostNumber(int sumPostNumber) {
		this.sumPostNumber = sumPostNumber;
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
	public Boolean getHasNewPostFlag() {
		return hasNewPostFlag;
	}

	/**
	 * @param hasNewPostFlag the hasNewPostFlag to set
	 */
	public void setHasNewPostFlag(Boolean hasNewPostFlag) {
		this.hasNewPostFlag = hasNewPostFlag;
	}

	/**
	 * @return the isAdminFlag
	 */
	public Boolean getIsAdminFlag() {
		return isAdminFlag;
	}

	/**
	 * @param isAdminFlag the isAdminFlag to set
	 */
	public void setIsAdminFlag(Boolean isAdminFlag) {
		this.isAdminFlag = isAdminFlag;
	}

	public V3xBbsBoard getBoard() {
		return board;
	}

	public void setBoard(V3xBbsBoard board) {
		this.board = board;
	}

	public boolean isOtherAccount() {
		return isOtherAccount;
	}

	public void setOtherAccount(boolean isOtherAccount) {
		this.isOtherAccount = isOtherAccount;
	}

	public boolean isHasAuthIssue() {
		return hasAuthIssue;
	}

	public void setHasAuthIssue(boolean hasAuthIssue) {
		this.hasAuthIssue = hasAuthIssue;
	}
}
