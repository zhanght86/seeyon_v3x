package com.seeyon.v3x.mobile.util;

public class Constants {
public static String FOCUSWIRENAME;
	
	public static String Url;
	
	public static final String DEFAULT_MOBILE_RESOURCE = "com.seeyon.v3x.mobile.resources.i18n.MobileResources";
	
	/**
	 *  通过 UFMobile 方式，取到消息的内容。
	 * @param str
	 * @return
	 */
	public static  String getContent(String str){
		String con = null;
		if(str!=null){
			 con = str.substring(16);
			if(con.substring(0,1).equals(",")||con.substring(0,1).equals("+")||con.substring(0,1).equals(".")||con.substring(0,1).equals(":")||con.substring(0,1).equals(" ")){
				con = con.substring(1);
			}
		}
		return con;
	}
	
	/**
	 * 从含有特征码的内容中，得到用户回复的内容。（普遍的方法）
	 * @param str
	 * @return
	 */
	public static String getNormalContent(String str){
		String con = null;
		if(str!=null){
			con = str.substring(4);
			if(con.substring(0,1).equals(",")||con.substring(0,1).equals(".")||con.substring(0,1).equals(":")||con.substring(0,1).equals(" ")){
				con = con.substring(1);
			}
		}
		return con;
	}
}
