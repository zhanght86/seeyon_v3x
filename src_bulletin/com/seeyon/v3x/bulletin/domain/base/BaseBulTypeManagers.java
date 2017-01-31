package com.seeyon.v3x.bulletin.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the bul_type_managers table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="bul_type_managers"
 */

public abstract class BaseBulTypeManagers extends BaseModel  implements Serializable {

	public static String REF = "BulTypeManagers";
	public static String PROP_TYPE = "type";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_MANAGER_ID = "managerId";
	public static String PROP_ORDER_NUM = "orderNum";
	public static String PROP_ID = "id";


	// constructors
	public BaseBulTypeManagers () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseBulTypeManagers (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseBulTypeManagers (
		java.lang.Long id,
		com.seeyon.v3x.bulletin.domain.BulType type,
		java.lang.Long managerId) {

		this.setId(id);
		this.setType(type);
		this.setManagerId(managerId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	//private java.lang.Long id;

	// fields
	private java.lang.Long managerId;
	private java.lang.Integer orderNum;
	private java.lang.String ext1;
	private java.lang.String ext2;

	// many to one
	private com.seeyon.v3x.bulletin.domain.BulType type;



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
	 * Return the value associated with the column: manager_id
	 */
	public java.lang.Long getManagerId () {
		return managerId;
	}

	/**
	 * Set the value related to the column: manager_id
	 * @param managerId the manager_id value
	 */
	public void setManagerId (java.lang.Long managerId) {
		this.managerId = managerId;
	}



	/**
	 * Return the value associated with the column: order_num
	 */
	public java.lang.Integer getOrderNum () {
		return orderNum;
	}

	/**
	 * Set the value related to the column: order_num
	 * @param orderNum the order_num value
	 */
	public void setOrderNum (java.lang.Integer orderNum) {
		this.orderNum = orderNum;
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
		this.type = type;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.bulletin.domain.BulTypeManagers)) return false;
		else {
			com.seeyon.v3x.bulletin.domain.BulTypeManagers bulTypeManagers = (com.seeyon.v3x.bulletin.domain.BulTypeManagers) obj;
			if (null == this.getId() || null == bulTypeManagers.getId()) return false;
			else return (this.getId().equals(bulTypeManagers.getId()));
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