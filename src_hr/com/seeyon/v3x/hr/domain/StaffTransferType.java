package com.seeyon.v3x.hr.domain;

/**
 * 
 * <p/> Title:调配类型
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 14, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */

public class StaffTransferType {
	
	/**
	 * id
	 */
	private int id;
	
	/**
	 * 状态
	 */
	private String type_name;
	
	public StaffTransferType() {}
	public StaffTransferType(int id, String type_name) {
		this.setId(id);
		this.setType_name(type_name);
	}
	public StaffTransferType(StaffTransferType staffTransferType) {
		this(staffTransferType.getId(), staffTransferType.getType_name());
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	

}
