package com.seeyon.v3x.hr.domain;


/**
 * 
 * <p/> Title:打卡状态
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jun 13, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class RecordState{
	/**
	 * id
	 */
	private int id;
	
	/**
	 * 状态
	 */
	private String state_name;
	
	private String trueName ;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getState_name() {
		return state_name;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	
	

}
