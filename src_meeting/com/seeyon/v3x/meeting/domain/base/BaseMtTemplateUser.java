package com.seeyon.v3x.meeting.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the mt_template_user table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="mt_template_user"
 */

public abstract class BaseMtTemplateUser extends BaseModel  implements Serializable {

	public static String REF = "MtTemplateUser";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_AUTH_TYPE = "authType";
	public static String PROP_SORT = "sort";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_TEMPLATE = "template";
	public static String PROP_AUTH_ID = "authId";
	public static String PROP_ID = "id";


	// constructors
	public BaseMtTemplateUser () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMtTemplateUser (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMtTemplateUser (
		java.lang.Long id,
		java.lang.Long authId,
		java.lang.Integer sort) {

		this.setId(id);
		this.setAuthId(authId);
		this.setSort(sort);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.String authType;
	private java.lang.Long authId;
	private java.lang.Integer sort;
	private java.lang.String ext1;
	private java.lang.String ext2;

	// many to one
	private com.seeyon.v3x.meeting.domain.MtTemplate template;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="assigned"
     *  column="id"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: auth_type
	 */
	public java.lang.String getAuthType () {
		return authType;
	}

	/**
	 * Set the value related to the column: auth_type
	 * @param authType the auth_type value
	 */
	public void setAuthType (java.lang.String authType) {
		this.authType = authType;
	}



	/**
	 * Return the value associated with the column: auth_id
	 */
	public java.lang.Long getAuthId () {
		return authId;
	}

	/**
	 * Set the value related to the column: auth_id
	 * @param authId the auth_id value
	 */
	public void setAuthId (java.lang.Long authId) {
		this.authId = authId;
	}



	/**
	 * Return the value associated with the column: sort
	 */
	public java.lang.Integer getSort () {
		return sort;
	}

	/**
	 * Set the value related to the column: sort
	 * @param sort the sort value
	 */
	public void setSort (java.lang.Integer sort) {
		this.sort = sort;
	}



	/**
	 * Return the value associated with the column: ext1
	 */
	public java.lang.String getExt1 () {
		return ext1;
	}

	/**
	 * Set the value related to the column: ext1
	 * @param ext1 the ext1 value
	 */
	public void setExt1 (java.lang.String ext1) {
		this.ext1 = ext1;
	}



	/**
	 * Return the value associated with the column: ext2
	 */
	public java.lang.String getExt2 () {
		return ext2;
	}

	/**
	 * Set the value related to the column: ext2
	 * @param ext2 the ext2 value
	 */
	public void setExt2 (java.lang.String ext2) {
		this.ext2 = ext2;
	}



	/**
	 * Return the value associated with the column: template_id
	 */
	public com.seeyon.v3x.meeting.domain.MtTemplate getTemplate () {
		return template;
	}

	/**
	 * Set the value related to the column: template_id
	 * @param template the template_id value
	 */
	public void setTemplate (com.seeyon.v3x.meeting.domain.MtTemplate template) {
		this.template = template;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.meeting.domain.MtTemplateUser)) return false;
		else {
			com.seeyon.v3x.meeting.domain.MtTemplateUser mtTemplateUser = (com.seeyon.v3x.meeting.domain.MtTemplateUser) obj;
			if (null == this.getId() || null == mtTemplateUser.getId()) return false;
			else return (this.getId().equals(mtTemplateUser.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}