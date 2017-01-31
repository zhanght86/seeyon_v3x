package com.seeyon.v3x.common.office;

import java.io.Serializable;
import java.util.*;

public class UserUpdateObject extends com.seeyon.v3x.common.ObjectToXMLBase implements Serializable
{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8323204904970624986L;
	private String objId="";
	private String userName="";
	private Long userId=0L;
	private Date lastUpdateTime;
	private Boolean curEditState=false;	
	
	public Long getUserId()
	{
		return this.userId;
	}
	
	public void setUserId(Long userId)
	{
		this.userId=userId;
	}
	
	public Boolean getCurEditState()
	{
		return this.curEditState;
	}
	
	public void setCurEditState(Boolean curEditState)
	{
		this.curEditState=curEditState;
	}
	
	public String getObjId()
	{
		return this.objId;
	}
	public String getUserName()
	{
		return this.userName;
	}
	public Date getLastUpdateTime()
	{
		return this.lastUpdateTime;
	}
	
	public void setObjId(String objId)
	{
		this.objId=objId;
	}
	public void setUserName(String userName)
	{
		this.userName=userName;
	}
	public void setLastUpdateTime(Date lastUpdateTime)
	{
		this.lastUpdateTime=lastUpdateTime;
	}
}