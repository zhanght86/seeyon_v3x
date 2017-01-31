package com.seeyon.v3x.inquiry.util;

import java.io.Serializable;

public class InquiryLock implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1879178195896049045L;

	private long inquiryid;

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

	public long getInquiryid()
	{
		return inquiryid;
	}

	public void setInquiryid(long inquiryid)
	{
		this.inquiryid = inquiryid;
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
