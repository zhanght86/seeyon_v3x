/**
 * $Id: OperationLogHelper.java,v 1.2 2007/08/14 00:40:17 wangj Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * OperationLogHelper.java created by paul at 2007-8-11 上午11:53:05
 *
 */
package com.seeyon.v3x.hr.util;

import com.seeyon.v3x.util.XMLCoder;

/**
 * <tt>OperationLogHelper</tt>把XML String转化为对象
 * @author paul
 *
 */
public class OperationLogHelper {
	
	/**
	 * 去掉"<object-array>"字符串，取得对象的XML
	 * @param xml
	 * @return
	 */
	private static String omitHeader(String xml) {
		if (null != xml && !xml.equals("")) {
			return xml.substring(xml.indexOf("<object-array>")+"<object-array>".length(), xml.lastIndexOf("</object-array>")).trim();
		}
		return null;
	}

	public static Object decoder(String xml) {
		return XMLCoder.decoder(omitHeader(xml));
	}
}
