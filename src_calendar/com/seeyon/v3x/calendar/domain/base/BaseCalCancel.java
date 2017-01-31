package com.seeyon.v3x.calendar.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * This is an object that contains data related to the cal_cancel table. Do not
 * modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 * 
 * @hibernate.class table="cal_cancel"
 */

public abstract class BaseCalCancel extends BaseModel implements Serializable {

	public static String REF = "CalCancel";

	public static String PROP_EXT1 = "ext1";

	public static String PROP_CANCEL_DATE = "cancelDate";

	public static String PROP_CAL_ID = "calId";

	public static String PROP_CANCEL_USER_ID = "cancelUserId";

	public static String PROP_TRAN_USER_ID = "tranUserId";

	public static String PROP_EXT2 = "ext2";

	public static String PROP_TRAN_FALG = "tranFalg";

	public static String PROP_ID = "id";

	public static String PROP_CANCEL_ADVICE = "cancelAdvice";

	// constructors
	public BaseCalCancel() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCalCancel(java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCalCancel(java.lang.Long id, java.lang.Long calId,
			java.lang.Long cancelUserId, java.util.Date cancelDate) {

		this.setId(id);
		this.setCalId(calId);
		this.setCancelUserId(cancelUserId);
		this.setCancelDate(cancelDate);
		initialize();
	}

	protected void initialize() {
	}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	// //private java.lang.Long id;

	// fields
	private java.lang.Long calId;

	private java.lang.Long cancelUserId;

	private java.lang.String cancelAdvice;

	private java.util.Date cancelDate;

	private boolean tranFalg;

	private java.lang.Long tranUserId;

	private java.lang.String ext1;

	private java.lang.String ext2;

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id generator-class="assigned" column="id"
	 */
	public java.lang.Long getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setId(java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	/**
	 * Return the value associated with the column: cal_id
	 */
	public java.lang.Long getCalId() {
		return calId;
	}

	/**
	 * Set the value related to the column: cal_id
	 * 
	 * @param calId
	 *            the cal_id value
	 */
	public void setCalId(java.lang.Long calId) {
		this.calId = calId;
	}

	/**
	 * Return the value associated with the column: cancel_user_id
	 */
	public java.lang.Long getCancelUserId() {
		return cancelUserId;
	}

	/**
	 * Set the value related to the column: cancel_user_id
	 * 
	 * @param cancelUserId
	 *            the cancel_user_id value
	 */
	public void setCancelUserId(java.lang.Long cancelUserId) {
		this.cancelUserId = cancelUserId;
	}

	/**
	 * Return the value associated with the column: cancel_advice
	 */
	public java.lang.String getCancelAdvice() {
		return cancelAdvice;
	}

	/**
	 * Set the value related to the column: cancel_advice
	 * 
	 * @param cancelAdvice
	 *            the cancel_advice value
	 */
	public void setCancelAdvice(java.lang.String cancelAdvice) {
		this.cancelAdvice = cancelAdvice;
	}

	/**
	 * Return the value associated with the column: cancel_date
	 */
	public java.util.Date getCancelDate() {
		return cancelDate;
	}

	/**
	 * Set the value related to the column: cancel_date
	 * 
	 * @param cancelDate
	 *            the cancel_date value
	 */
	public void setCancelDate(java.util.Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	/**
	 * Return the value associated with the column: tran_falg
	 */
	public boolean isTranFalg() {
		return tranFalg;
	}

	/**
	 * Set the value related to the column: tran_falg
	 * 
	 * @param tranFalg
	 *            the tran_falg value
	 */
	public void setTranFalg(boolean tranFalg) {
		this.tranFalg = tranFalg;
	}

	/**
	 * Return the value associated with the column: tran_user_id
	 */
	public java.lang.Long getTranUserId() {
		return tranUserId;
	}

	/**
	 * Set the value related to the column: tran_user_id
	 * 
	 * @param tranUserId
	 *            the tran_user_id value
	 */
	public void setTranUserId(java.lang.Long tranUserId) {
		this.tranUserId = tranUserId;
	}

	/**
	 * Return the value associated with the column: ext1
	 */
	public java.lang.String getExt1() {
		return ext1;
	}

	/**
	 * Set the value related to the column: ext1
	 * 
	 * @param ext1
	 *            the ext1 value
	 */
	public void setExt1(java.lang.String ext1) {
		this.ext1 = ext1;
	}

	/**
	 * Return the value associated with the column: ext2
	 */
	public java.lang.String getExt2() {
		return ext2;
	}

	/**
	 * Set the value related to the column: ext2
	 * 
	 * @param ext2
	 *            the ext2 value
	 */
	public void setExt2(java.lang.String ext2) {
		this.ext2 = ext2;
	}

	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (!(obj instanceof com.seeyon.v3x.calendar.domain.CalCancel))
			return false;
		else {
			com.seeyon.v3x.calendar.domain.CalCancel calCancel = (com.seeyon.v3x.calendar.domain.CalCancel) obj;
			if (null == this.getId() || null == calCancel.getId())
				return false;
			else
				return (this.getId().equals(calCancel.getId()));
		}
	}

	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":"
						+ this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return super.toString();
	}

}