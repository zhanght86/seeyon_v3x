package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.util.Constants;

/**
 * 订阅记录vo
 */
public class DocBorrowVO {
	private Long userId;
	private String userType;
	private String userName;
	 // 开始时间
	private java.sql.Timestamp sdate;
	 // 结束时间
	private java.sql.Timestamp edate;
	// 公文借阅的权限
	private Byte lenPotent=Constants.LENPOTENT_ALL;
	private String lenPotent2;
	
	private String canPrint;
	private String canSave;
	
	
	public java.sql.Timestamp getEdate() {
		return edate;
	}
	public void setEdate(java.sql.Timestamp edate) {
		this.edate = edate;
	}
	public java.sql.Timestamp getSdate() {
		return sdate;
	}
	public void setSdate(java.sql.Timestamp sdate) {
		this.sdate = sdate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public Byte getLenPotent() {
		return lenPotent;
	}
	public void setLenPotent(Byte lenPotent) {
		this.lenPotent = lenPotent;
	}
	public String getLenPotent2() {
		return lenPotent2;
	}
	public void setLenPotent2(String lenPotent2) {
		this.lenPotent2 = lenPotent2;
	}
	public String getCanPrint() {
		if(lenPotent2==null || "".endsWith(lenPotent2)){canPrint="0";}
		else{canPrint=String.valueOf(lenPotent2.charAt(1));}
		return canPrint;
	}
	public void setCanPrint(String canPrint) {
		this.canPrint = canPrint;
	}
	public String getCanSave() {
		if(lenPotent2==null || "".endsWith(lenPotent2)){canSave="0";}
		else{canSave=String.valueOf(lenPotent2.charAt(0));}
		return canSave;
	}
	public void setCanSave(String canSave) {
		this.canSave = canSave;
	}

}
