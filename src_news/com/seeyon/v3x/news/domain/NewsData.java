package com.seeyon.v3x.news.domain;

import java.util.Date;

import com.seeyon.v3x.news.domain.base.BaseNewsData;



public class NewsData extends BaseNewsData {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public NewsData () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public NewsData (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public NewsData (
		java.lang.Long id,
		com.seeyon.v3x.news.domain.NewsType type,
		java.lang.String title,
		java.lang.String publishScope,
		java.lang.String keywords,
		java.lang.String brief,
		java.util.Date createDate,
		java.lang.Long createUser,
		java.lang.Byte topOrder,
		java.lang.Integer state,
		boolean deletedFlag,
		java.lang.Long accountId) {

		super (
			id,
			type,
			title,
			publishScope,
			keywords,
			brief,
			createDate,
			createUser,
			topOrder,
			state,
			deletedFlag,
			accountId);
	}

/*[CONSTRUCTOR MARKER END]*/

	private Long typeId;
    private String typeName;
	private String createUserName;
	private String publishScopeNames;
	private Boolean readFlag;
	private Boolean attachmentsFlag = false;
	private String publishDepartmentName;
	/** 是否为图片新闻 */
	private boolean imageNews;
	/** 是否为焦点新闻 */
	private boolean focusNews;
	/** 图片附件ID */
	private Long imageId;

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getPublishScopeNames() {
		return publishScopeNames;
	}

	public void setPublishScopeNames(String publishScopeNames) {
		this.publishScopeNames = publishScopeNames;
	}
	
	public String getPublishDepartmentName() {
		return publishDepartmentName;
	}

	public void setPublishDepartmentName(String publishDepartmentName) {
		this.publishDepartmentName = publishDepartmentName;
	}



	public Boolean getAttachmentsFlag() {
		return attachmentsFlag;
	}

	public void setAttachmentsFlag(Boolean attachmentsFlag) {
		this.attachmentsFlag = attachmentsFlag;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	// 是否不能修改，不能删除
	private boolean noEdit;
	private boolean noDelete;

	public boolean getNoDelete() {
		return noDelete;
	}

	public void setNoDelete(boolean noDelete) {
		this.noDelete = noDelete;
	}

	public boolean getNoEdit() {
		return noEdit;
	}

	public void setNoEdit(boolean noEdit) {
		this.noEdit = noEdit;
	}
	
	//
	boolean showBriefArea = false;
	boolean showKeywordsArea = false;

	public boolean getShowKeywordsArea() {
		return showKeywordsArea;
	}

	public void setShowKeywordsArea(boolean showKeywordsArea) {
		this.showKeywordsArea = showKeywordsArea;
	}

	public boolean getShowBriefArea() {
		return showBriefArea;
	}

	public void setShowBriefArea(boolean showBriefArea) {
		this.showBriefArea = showBriefArea;
	}

	public Boolean getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(Boolean readFlag) {
		this.readFlag = readFlag;
	}

	public boolean isImageNews() {
		return imageNews;
	}

	public void setImageNews(boolean imageNews) {
		this.imageNews = imageNews;
	}

	public boolean isFocusNews() {
		return focusNews;
	}

	public void setFocusNews(boolean focusNews) {
		this.focusNews = focusNews;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

}