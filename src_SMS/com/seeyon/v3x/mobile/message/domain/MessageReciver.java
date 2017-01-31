package com.seeyon.v3x.mobile.message.domain;

/**
 * 消息接收者细节
 * 
 */
public class MessageReciver {
	private Long id;// 接收人的ID

	private String phonenumber;

	//private Long mid;// Message的ID
	
	public MessageReciver(Long id, String phonenumber){
		//this.mid = mid;
		this.id = id;
		this.phonenumber = phonenumber;
	}

//	public Long getMid() {
//		return mid;
//	}
//
//	public void setMid(Long mid) {
//		this.mid = mid;
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}


	public String toString(){
		return "UserId : " + id + " " + phonenumber ;
	}
}
