package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description:保存接收的邮件ID，根据此文件判断是否接收过邮件 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.util.*;
import java.io.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LocalMailIdManager
{
	private String userId = "";

	private java.util.List mids = null;
	
	private final static Log logger = LogFactory.getLog(LocalMailIdManager.class);

	public LocalMailIdManager()
	{
		mids = new ArrayList();
	}

	/**
	 * 掉入已经接收的邮件ID
	 * 
	 * @param userId
	 * @return
	 */
	public boolean load(String userId)
	{
		try
		{
			this.userId = userId;
			String fn = LocalMailCfg.getMailIndexFile(userId);
			File f = new File(fn);
			if (!f.exists())
			{
				initRecMailId(userId);
			}
			if (f.length() > 0)
			{
				FileInputStream fi = new FileInputStream(f);
				ObjectInputStream oi = new ObjectInputStream(fi);
				// mids=(List)oi.readObject();
				int i, len;
				LocalMailCfg.setMailIdVersion(oi.readUTF());// 设置文件版本号
				len = oi.readInt();
				for (i = 0; i < len; i++)
				{
					mids.add(oi.readUTF());
				}
			}
		} catch (Exception e)
		{
			logger.error("LocalMailIdManager.load():", e);
		}
		return true;
	}

	/**
	 * 添加接收邮件的ID到列表
	 * 
	 * @param mailId
	 * @return
	 */
	public boolean add(String mailId)
	{
		if (!exist(mailId))
		{
			mids.add(mailId);
		}
		return true;
	}

	public boolean add(String mailId, boolean isCheck)
	{
		if (isCheck)
			return add(mailId);
		else
			return mids.add(mailId);
	}

	/**
	 * 删除接收邮件的ID
	 * 
	 * @param mailId
	 * @return
	 */
	public boolean del(String mailId)
	{
		mids.remove(mailId);
		return true;
	}

	/**
	 * 根据邮件ID判断是否接收过此邮件
	 * 
	 * @param mailId
	 * @return
	 */
	public boolean exist(String mailId)
	{
		int i, len;
		len = mids.size();
		for (i = 0; i < len; i++)
		{
			if (mids.get(i).equals(mailId))
				return true;
		}
		return false;
	}

	/**
	 * 保存对邮件ID列表的操作，首先load(),操作后save()
	 * 
	 * @return
	 */
	public boolean save()
	{
		try
		{
			String fn = LocalMailCfg.getMailIndexFile(userId);
			File f = new File(fn);
			if (f.exists())
			{
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			ObjectOutputStream oo = new ObjectOutputStream(fo);
			// oo.writeObject(mids);
			int i, len;
			len = mids.size();
			oo.writeUTF(LocalMailCfg.getMailIdVersion());// 写文件版本号
			oo.writeInt(len);
			for (i = 0; i < len; i++)
			{
				oo.writeUTF((String) mids.get(i));
			}
			oo.flush();
			oo.close();
			fo.flush();
			fo.close();
		} catch (Exception e)
		{
			logger.error("LocalMailIdManager.save():", e);
		}
		return true;
	}

	/**
	 * 性能优化升级时，先从数据库中读取已经接收的邮件ID
	 * 
	 * @param UserId
	 * @return
	 */
	private boolean initRecMailId(String userId)
	{
		String fn = LocalMailCfg.getMailIndexFile(userId);
		File f = new File(fn);
		try
		{
			if (f.exists())
				f.delete();
			f.createNewFile();
		}catch (Exception e)
		{
			logger.error("LocalMailIdManager.initRecMailId():" + e);
			return false;
		}
		return true;
	}

	public String get(int i)
	{
		return mids.get(i).toString();
	}

	public int size()
	{
		return mids.size();
	}

	public void testWrite()
	{
		load("zhangh");
		add("123");
		add("张华的email");
		save();
	}

	public void testRead()
	{
		int i, len;
		load("zhangh");
		len = size();
		for (i = 0; i < len; i++)
		{
			logger.info(get(i));
		}
	}

	public String toOutString()
	{
		int i, len;
		StringBuffer sb = new StringBuffer();
		sb.append("链表mailID信息");
		len = mids.size();
		for (i = 0; i < len; i++)
		{
			sb.append("\r\n");
			sb.append((String) mids.get(i));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception
	{
		LocalMailIdManager m = new LocalMailIdManager();
		LocalMailIdManager m2 = new LocalMailIdManager();
		m.add("22222222");
		m.add("222222223333");
		m.add("zhang话");

		logger.info("=================");
		logger.info(m.toOutString());
		logger.info("=================");
		m.save();

		m2.load("");
		logger.info("=================");
		logger.info(m2.toOutString());
		logger.info("=================");

		logger.info("RUN OVER");
	}

}