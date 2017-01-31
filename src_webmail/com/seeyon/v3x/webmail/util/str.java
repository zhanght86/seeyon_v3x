package com.seeyon.v3x.webmail.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class str
{
	private final static String chineseCharset = "UTF-8";
	private final static String unicodeCharset = "ISO8859_1";
	private final static String chineseGBK = "GBK";
	
	public static String getChineseGBK(){
		return chineseGBK;
	}
	
	public static String getChineseCharset()
	{
		return chineseCharset;
	}
	
	public static String UnicodetoChinese(String input)
	{
		try
		{
			return new String(input.getBytes(unicodeCharset), chineseGBK);
		}
		catch(Exception ex){}
		return null;
		//return input;
	}
	
	public static String ChinesetoUnicode(String input)
	{
		try
		{
			return new String(input.getBytes(chineseCharset), unicodeCharset);
		}
		catch(Exception ex){}
		return null;
	}
	
	public static String getPageStr(int pageNo, int pageSize, int count, String url, HttpServletRequest request, HttpServletResponse response)
	{
		String contextPath = request.getContextPath();
		if(contextPath.endsWith("/")){contextPath = contextPath.substring(0, contextPath.length() - 1);}
		int pageNum = count / pageSize;
		if(count % pageSize > 0)
		{
			pageNum ++;
		}
		StringBuffer str = new StringBuffer();
		str.append("每页&nbsp;" + pageSize + "条记录&nbsp;|&nbsp;共" + pageNum + "页/" + count + "条记录&nbsp;|&nbsp;");
		if(pageNo == 1)
		{
			str.append("<img src=\""+contextPath+"/common/images/webmail/pagehome.gif\" border=\"0\" />&nbsp;");
			str.append("<img src=\""+contextPath+"/common/images/webmail/pageprev.gif\" border=\"0\" />&nbsp;");
		}
		else if(pageNo > 1)
		{
			str.append("<a href=\""+url+"&pageNo=1\">");
			str.append("<img src=\""+contextPath+"/common/images/webmail/pagehome.gif\" border=\"0\" /></a>&nbsp;");
			str.append("<a href=\""+url+"&pageNo="+(pageNo-1)+"\">");
			str.append("<img src=\""+contextPath+"/common/images/webmail/pageprev.gif\" border=\"0\" /></a>&nbsp;");
		}
		if(pageNo == pageNum)
		{
			str.append("<img src=\""+contextPath+"/common/images/webmail/pagenext.gif\" border=\"0\" />&nbsp;");
			str.append("<img src=\""+contextPath+"/common/images/webmail/pageend.gif\" border=\"0\" />&nbsp;");
		}
		else if(pageNo < pageNum)
		{
			str.append("<a href=\""+url+"&pageNo="+(pageNo+1)+"\">");
			str.append("<img src=\""+contextPath+"/common/images/webmail/pagenext.gif\" border=\"0\" /></a>&nbsp;");
			str.append("<a href=\""+url+"&pageNo="+pageNum+"\">");
			str.append("<img src=\""+contextPath+"/common/images/webmail/pageend.gif\" border=\"0\" /></a>&nbsp;");
		}
		str.append("|&nbsp;第");
		str.append("<input type=\"text\" name=\"jumpPageNo\" size=\"2\" />");
		str.append("页<input type=\"button\" value=\"go\" onclick=\"doJumpPage();\" />\r\n");
		str.append("<script type=\"\">\r\n");
		str.append("function doJumpPage(){\r\n");
		str.append("var jumpPageNo = document.getElementById(\"jumpPageNo\");\r\n");
		str.append("if(jumpPageNo.value.length == 0){\r\n");
		str.append("alert(\"请填写要要跳转的页码！\");\r\n");
		str.append("return false;\r\n");
		str.append("}\r\n");
		str.append("document.location.href=\""+url+"&pageNo=\"+jumpPageNo.value+\"\"\r\n");
		str.append("}\r\n");
		str.append("</script>");
		
		return str.toString();
	}
	
	public static void main(String[] args)
	{
		java.io.File f = new java.io.File("C:\\asd.txt");
		try
		{
			f.createNewFile();
			java.io.File dest = new java.io.File("D:\\dddd.txt");
			Thread.sleep(5000);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
