package com.seeyon.v3x.webmail.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: seeyon</p>
 * @author 刘嵩高
 * @version 3x
 * @date 2007-5-13
 */
import java.util.*;
import java.text.*;

import com.seeyon.v3x.util.Datetimes;
public class DateUtil {
  public DateUtil() {
  }
  static public String formatDate(Date date,String dateformat) {
    try{
      //yyyy.MM.dd hh:mm:ss
      return Datetimes.format(date, dateformat).toString();
    }catch(Exception e)
    {
      return "";
    }
  }
  static public String formatDate(Date date) {
    String dateformat="yyyy-MM-dd HH:mm:ss";
    return formatDate(date,dateformat);
  }
  /**
   * 得到当前日期
   * @return
   */
  static public String getDate()
  {
    Date d=new Date(System.currentTimeMillis());
    return DateUtil.formatDate(d);
  }
  static public Date getDate(String szDate)
  {
	  //"yyyy-MM-dd HH:mm:ss"
    java.util.Date ret = Datetimes.parseDatetime(szDate);
    return ret;
  }
  public static void main(String[] args) {
    DateUtil d = new DateUtil();
    Date date=d.getDate("2005-11-12 15:28:23");
  }

}