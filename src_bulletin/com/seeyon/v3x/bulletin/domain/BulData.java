package com.seeyon.v3x.bulletin.domain;

import java.sql.Timestamp;

import com.seeyon.v3x.bulletin.domain.base.BaseBulData;



public class BulData extends BaseBulData {
	private static final long serialVersionUID = 1L;
	
	public static String PROP_TYPE_ID = "typeId";
	public static String PROP_ATTACHMENTS_FLAG = "attachmentsFlag";

/*[CONSTRUCTOR MARKER BEGIN]*/
	public BulData () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public BulData (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public BulData (
		java.lang.Long id,
		com.seeyon.v3x.bulletin.domain.BulType type,
		java.lang.String title,
		java.lang.String publishScope,
		java.lang.String keywords,
		Timestamp createDate,
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
	private Boolean readFlag;
	private Boolean attachmentsFlag = false;
	private BulType type;
	private String contentName;
	
	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Boolean getReadFlag() {
		return readFlag;
	}

//	public void setReadFlag(boolean readFlag) {
//		this.readFlag = readFlag;
//	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	/**
	 * Return the value associated with the column: type_id
	 */
	public com.seeyon.v3x.bulletin.domain.BulType getType () {
		return type;
	}

	/**
	 * Set the value related to the column: type_id
	 * @param type the type_id value
	 */
	public void setType (com.seeyon.v3x.bulletin.domain.BulType type) {
		this.type=type;
	}

	public Boolean getAttachmentsFlag() {
		return attachmentsFlag;
	}

	public void setAttachmentsFlag(Boolean attachmentsFlag) {
		this.attachmentsFlag = attachmentsFlag;
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

	private String publishDeptName;

	public String getPublishDeptName() {
		return publishDeptName;
	}

	public void setPublishDeptName(String publishDeptName) {
		this.publishDeptName = publishDeptName;
	}

	public void setReadFlag(Boolean readFlag) {
		this.readFlag = readFlag;
	}
	
	
}