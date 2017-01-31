package com.seeyon.v3x.webmail.manager;

/**
 * <p>Title: </p>
 * <p>Description:管理用户邮箱配置 </p>
 * <p>配置信息存放到用户目录的配置文件中，当配置文件不存在时从数据提取以前的配置信息</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.webmail.domain.MailBoxCfg;

public class MailBoxManager
{
	private final static Log logger = LogFactory.getLog(MailBoxManager.class);
	
	public MailBoxManager()
	{
	}

	/**
	 * 用户添加邮箱配置
	 * 
	 * @param userId
	 * @param mb
	 * @return
	 */
	public static MbcList loadMailBoxs(String userId) throws Exception
	{
		MbcList list = new MbcList();
		try
		{
			String fn = LocalMailCfg.getUserCfgFile(userId);
			File f = new File(fn);
			if (!f.exists())
			{
				initUserMailCfg(userId);
			}
			if (f.length() > 0)
			{
				FileInputStream fi = new FileInputStream(f);
				ObjectInputStream oi = new ObjectInputStream(fi);
				LocalMailCfg.setMailBoxCfgVersion(oi.readUTF());// 读取文件格式版本号
				list.readBaseObject(oi);
			}
		} catch (Exception e)
		{
			logger.error("MailBoxManger.loadMailBoxs():" , e);
			throw e;
		}
		return list;
	}

	public static boolean saveMailBoxs(String userId, MbcList mbcs)
	{
		try
		{
			String fn = LocalMailCfg.getUserCfgFile(userId);
			File f = new File(fn);
			if (f.exists())
			{
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			ObjectOutputStream oo = new ObjectOutputStream(fo);
			// oo.writeObject(mbcs);
			oo.writeUTF(LocalMailCfg.getMailBoxCfgVersion());// 写文件版本号
			mbcs.writeBaseObject(oo);
			oo.flush();
			oo.close();
			fo.flush();
			fo.close();
		} catch (Exception e)
		{
			logger.error("MailBoxManger.saveMailBoxs():" + e.getMessage());
		}
		return true;
	}

	public static MailBoxCfg findUserMbc(String userId, String email)
			throws Exception
	{
		MailBoxCfg mbc = null;
		MbcList mbcList = loadMailBoxs(userId);
		int i, len;
		len = mbcList.size();
		for (i = 0; i < len; i++)
		{
			mbc = mbcList.get(i);
			if (mbc.getEmail().equals(email))
			{
				return mbc;
			}
		}
		return null;
	}

	/**
	 * 查找用户默认的邮箱
	 * 
	 * @param userId
	 * @return:没有默认邮箱时返回null
	 */
	public static MailBoxCfg findUserDefaultMbc(String userId) throws Exception
	{
		MailBoxCfg mbc = null;
		MbcList mbcList = loadMailBoxs(userId);
		if(mbcList==null){return mbc;}
		for (int i = 0; i < mbcList.size(); i++)
		{
			mbc = mbcList.get(i);
			if(mbc==null){return null;}
			if (mbc.getDefaultBox())
			{
				return mbc;
			}
		}
		return null;
	}

	/**
	 * 得到用户配置邮箱数量
	 * 
	 * @param userId
	 * @param mbc
	 * @return
	 * @throws java.lang.Exception
	 */
	public static int count(String userId) throws Exception
	{
		MbcList ml = loadMailBoxs(userId);
		return ml.size();
	}

	/**
	 * 添加一个用户邮箱配置
	 * 
	 * @param userId
	 * @param mbc
	 * @return
	 */
	public static boolean add(String userId, MailBoxCfg mbc) throws Exception
	{
		MbcList ml = loadMailBoxs(userId);
		ml.add(mbc);
		return saveMailBoxs(userId, ml);
	}

	public static boolean add(String userId, MbcList mbcList) throws Exception
	{
		int i, len;
		MbcList ml = loadMailBoxs(userId);
		len = mbcList.size();
		for (i = 0; i < len; i++)
		{
			if (ml.add(mbcList.get(i)) == false)
				return false;
		}
		return saveMailBoxs(userId, ml);
	}

	public static boolean del(String userId, String email) throws Exception
	{
		MbcList ml = loadMailBoxs(userId);
		ml.remove(email);
		return saveMailBoxs(userId, ml);
	}

	public static boolean del(String userId, String[] emails) throws Exception
	{
		MbcList ml = loadMailBoxs(userId);
		for (int i = 0; i < emails.length; i++)
		{
			ml.remove(emails[i]);
		}
		return saveMailBoxs(userId, ml);
	}

	/**
	 * 性能优化升级时，先从数据库中读取原始的邮件配置数据
	 * 
	 * @param UserId
	 * @return
	 */
	private static boolean initUserMailCfg(String userId)
	{
		String fn = LocalMailCfg.getUserCfgFile(userId);
		File f = new File(fn);
		try
		{
			if (f.exists())
				f.delete();
			f.createNewFile();
		} catch (Exception e)
		{
			logger.error("MailBoxManger.initUserMailCfg():" + e.getMessage());
			return false;
		}
		return true;
	}

	public static void testWrite() throws Exception
	{/*
		 * MbcList mbcs=new MbcList(); MailBoxCfg mbc=new MailBoxCfg();
		 * mbc.setEmail("zhanghua@seeyon.com"); mbc.setPassword("张华");
		 * mbcs.add(mbc);
		 * 
		 * mbc=new MailBoxCfg(); mbc.setEmail("5555@seeyon.com");
		 * mbc.setPassword("tttt"); mbcs.add(mbc);
		 * 
		 * mbc=new MailBoxCfg(); mbc.setEmail("44445@seeyon.com");
		 * mbc.setPassword("44444"); mbcs.add(mbc);
		 */
		MbcList mbcs = loadMailBoxs("zhangh");
		saveMailBoxs("zhangh", mbcs);
	}

	public static void testRead() throws Exception
	{
		MbcList mbcs = loadMailBoxs("zhangh");
		MailBoxCfg mbc = new MailBoxCfg();
		int i, len;
		len = mbcs.size();
		logger.info("read LEN:" + len);
		for (i = 0; i < len; i++)
		{
			mbc = mbcs.get(i);
			logger.info("(" + mbc.getEmail() + ")("
					+ mbc.getPassword() + ")(" + mbc.getDefaultBox() + ")("
					+ mbc.getAuthorCheck() + ")");
		}
	}

	public static void main(String[] args)
	{
		MailBoxManager mailBoxManager1 = new MailBoxManager();
		try
		{
			testWrite();
			testRead();
		} catch (Exception e)
		{
			logger.error("err:" + e.getMessage());
		}
		logger.info("run OVER");
	}

}