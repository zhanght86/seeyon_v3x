package com.seeyon.v3x.webmail.util;

/**
 * <p>Title: </p>
 * <p>Description:生成web容器唯一的编号 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


//import net.btdz.oa.tools.OAAppInfo;

public class UniqueCode
{
	private static String sep = "";

	private static short counter = (short) 0;

	private static short smallCounter = (short) 998;

	private static String szIP = null;
	
	private final static Log logger = LogFactory.getLog(UniqueCode.class);

	public UniqueCode()
	{
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there are >
	 * Short.MAX_VALUE instances created in a millisecond)
	 */
	protected static short getCount()
	{
		synchronized (UniqueCode.class)
		{
			if (counter < 0)
				counter = 0;
			if (counter > 10000)
				counter = 0;
			return counter++;
		}
	}

	protected static short getSmallCount()
	{
		synchronized (UniqueCode.class)
		{
			if (smallCounter < 0)
				smallCounter = 0;
			if (smallCounter > 999)
				smallCounter = 0;
			return smallCounter++;
		}
	}

	/**
	 * Unique down to millisecond
	 */
	protected static short getHiTime()
	{
		return (short) (System.currentTimeMillis() >>> 32);
	}

	protected static int getLoTime()
	{
		return (int) System.currentTimeMillis();
	}

	protected static String format(int intval)
	{
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	protected static String format(short shortval)
	{
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	public static String generate()
	{
		return new StringBuffer().append(format(getHiTime())).append(sep)
				.append(format(getLoTime())).append(sep).append(
						format(getCount())).toString();
	}

	public static String generateNum()
	{
		return new StringBuffer().append(System.currentTimeMillis()).append(
				getSmallCount()).toString();
	}

	public static String generateDate()
	{
		Date d = new Date(System.currentTimeMillis());
		if (szIP == null)
		{
			//szIP = OAAppInfo.getInstance().getDefaultServerIp();
			szIP = "127.0.0.1";
			if (szIP != null)
			{
				szIP = System14.replace(szIP, ".", "");
			} else
			{
				szIP = "";
			}
		}
		return new StringBuffer(szIP).append(
				DateUtil.formatDate(d, "yyyyMMddHHmmssSSS")).append(
				getSmallCount()).toString();
	}

	public static void main(String[] args)
	{
		int i;
		UniqueCode uniqueCode1 = new UniqueCode();
		for (i = 0; i < 100; i++)
		{
			System.out.println(uniqueCode1.generate());
		}
		for (i = 0; i < 100; i++)
		{
			System.out.println(uniqueCode1.generateDate());
		}
	}
}