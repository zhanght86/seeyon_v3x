package com.seeyon.v3x.webmail.domain;

/**
 * <p>Title: </p>
 * <p>Description: 定义邮件信息的列表</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.util.*;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.webmail.util.Affix;

public class MailInfoList implements Serializable
{
	private List mailList = new ArrayList();
	
	private final static Log logger = LogFactory.getLog(MailInfoList.class);

	public MailInfoList()
	{
	}

	public boolean add(MailInfo mi)
	{
		/* 添加的邮件信息必须包含邮件的ID */
		if (mi.getMailId() == null)
			return false;
		return mailList.add(mi);
	}

	public boolean add(MailInfoList mil)
	{
		int i, len;
		len = mil.size();
		for (i = 0; i < len; i++)
		{
			if (mailList.add(mil.get(i)) == false)
				return false;
		}
		return true;
	}

	public MailInfo get(int i)
	{
		return (MailInfo) mailList.get(i);
	}

	public boolean remove(MailInfo mi)
	{
		return mailList.remove(mi);
	}

	public MailInfo getMail(String mailNumber)
	{
		int i, len;
		MailInfo mi = null;
		len = mailList.size();
		for (i = 0; i < len; i++)
		{
			mi = (MailInfo) mailList.get(i);
			if (mi.getMailNumber().equals(mailNumber))
				return mi;
		}
		return null;
	}
	
	public MailInfo getMailByLongId(long mailLongId){
		int i, len;
		MailInfo mi = null;
		len = mailList.size();
		for (i = 0; i < len; i++)
		{
			mi = (MailInfo) mailList.get(i);
			if(mi.getMailLongId() == mailLongId)
				return mi;
		}
		return null;
	}

	/**
	 * 从邮件列表中删除邮件
	 * 
	 * @param mailId
	 */
	public MailInfo remove(String mailId)
	{
		int i, len;
		MailInfo mi = null;
		len = mailList.size();
		for (i = 0; i < len; i++)
		{
			mi = (MailInfo) mailList.get(i);
			if (mi.getMailNumber().equals(mailId))
			{
				mailList.remove(i);
				return mi;
			}
		}
		return null;
	}

	public int size()
	{
		return mailList.size();
	}

	public int getNewCount()
	{
		int count = 0;
		int i, len;
		len = mailList.size();
		for (i = 0; i < len; i++)
		{
			if (((MailInfo) mailList.get(i)).getRead() == false)
			{
				count++;
			}
		}
		return count;
	}

	public boolean sortBySendDate()
	{
		MailInfo maxMi = null, tempMi = null;
		int i, j, maxId, len = mailList.size();
		for (i = 0; i < len; i++)
		{
			maxId = i;
			maxMi = (MailInfo) mailList.get(i);
			for (j = i; j < len; j++)
			{
				tempMi = (MailInfo) mailList.get(j);
				if (!maxMi.getSendDate().after(tempMi.getSendDate()))
				{
					maxMi = tempMi;
					maxId = j;
				}
			}
			if (maxId != i)
			{
				tempMi = (MailInfo) mailList.get(i);
				mailList.set(i, maxMi);
				mailList.set(maxId, tempMi);
			}
		}
		return true;
	}

	public void readBaseObject(java.io.ObjectInputStream in,Double fileVer) throws IOException
	{
		MailInfo mi = null;
		int i, len = in.readInt();
		for (i = 0; i < len; i++)
		{
			mi = new MailInfo();
			try{
				mi.readBaseObject(in,fileVer);
				mailList.add(mi);
			} catch (Exception e){
				logger.warn("获取邮件列表出错：" +e);
			}
		}
	}

	public void writeBaseObject(java.io.ObjectOutputStream out)
			throws IOException
	{
		int i, len = mailList.size();
		out.writeInt(len);
		for (i = 0; i < len; i++)
		{
			((MailInfo) mailList.get(i)).writeBaseObject(out);
		}
	}

	public String toOutString()
	{
		int i, len;
		StringBuffer sb = new StringBuffer();
		sb.append("链表mailInfo信息");
		len = mailList.size();
		for (i = 0; i < len; i++)
		{
			sb.append("\r\n");
			sb.append(((Affix) mailList.get(i)).toOutString());
		}
		return sb.toString();
	}

	public static void main(String[] args)
	{
		MailInfoList mailInfoList1 = new MailInfoList();
	}

}