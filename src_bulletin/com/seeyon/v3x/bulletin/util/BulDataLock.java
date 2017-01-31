package com.seeyon.v3x.bulletin.util;

import java.io.Serializable;

/**
 * 用于解决公告模块锁的问题 即只允许一个人操作公告体 该MAP的KEY为:公告的ID Value为:Object数组, 数组里面放的是操作人ID,操作动作
 * 
 * @author IORIadmin
 */
public class BulDataLock implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3713112325779413985L;

	private long newsid;

	private long userid;

	private String action;

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public long getNewsid()
	{
		return newsid;
	}

	public void setNewsid(long newsid)
	{
		this.newsid = newsid;
	}

	public long getUserid()
	{
		return userid;
	}

	public void setUserid(long userid)
	{
		this.userid = userid;
	}
}
