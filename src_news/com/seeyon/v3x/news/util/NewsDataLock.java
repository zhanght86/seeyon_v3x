package com.seeyon.v3x.news.util;

import java.io.Serializable;

/**
 * 用于解决新闻模块锁的问题 即只允许一个人操作新闻体 该MAP的KEY为:新闻的ID Value为:Object数组, 数组里面放的是操作人ID,操作动作
 * 
 * @author IORIadmin
 */
public class NewsDataLock implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2501878428616255526L;

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

	public String toString()
	{
		return this.newsid+","+this.userid+","+this.action;
	}
	
}
