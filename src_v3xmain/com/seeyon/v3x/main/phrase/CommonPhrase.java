package com.seeyon.v3x.main.phrase;

import java.io.Serializable;

/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-1-17
 */
public class CommonPhrase extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {

	public enum PHRASE_TYPE {
		personal, system
	};

	private static final long serialVersionUID = 5886163098797975020L;

	public static String REF = "CommonPhrase";

	public static String PROP_UPDATE_DATE = "updateDate";

	public static String PROP_MEMBER_ID = "memberId";

	public static String PROP_CREATE_DATE = "createDate";

	public static String PROP_CONTENT = "content";

	public static String PROP_TYPE = "type";
	
	public static String PROP_account_id = "accountId";

	public static String PROP_ID = "id";

	// fields
	private java.lang.Long memberId;

	private java.lang.String content;

	private java.util.Date createDate;

	private java.util.Date updateDate;

	private java.lang.Integer type;
	
	private Long accountId;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}

	/**
	 * Return the value associated with the column: member_id
	 */
	public java.lang.Long getMemberId() {
		return memberId;
	}

	/**
	 * Set the value related to the column: member_id
	 * 
	 * @param memberId
	 *            the member_id value
	 */
	public void setMemberId(java.lang.Long memberId) {
		this.memberId = memberId;
	}

	/**
	 * Return the value associated with the column: content
	 */
	public java.lang.String getContent() {
		return content;
	}

	/**
	 * Set the value related to the column: content
	 * 
	 * @param content
	 *            the content value
	 */
	public void setContent(java.lang.String content) {
		this.content = content;
	}

	/**
	 * Return the value associated with the column: create_date
	 */
	public java.util.Date getCreateDate() {
		return createDate;
	}

	/**
	 * Set the value related to the column: create_date
	 * 
	 * @param createDate
	 *            the create_date value
	 */
	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * Return the value associated with the column: update_date
	 */
	public java.util.Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * Set the value related to the column: update_date
	 * 
	 * @param updateDate
	 *            the update_date value
	 */
	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
	}

}