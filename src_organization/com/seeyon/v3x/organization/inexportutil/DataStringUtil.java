package com.seeyon.v3x.organization.inexportutil;

import java.util.Date;

import com.seeyon.v3x.util.Datetimes;

/**
*
* @author <a href="mailto:tanglh@seeyon.com">tanglh</a>
* @version 1.0 2007-7-10
*/
public class DataStringUtil {
//	tanglh
	public static String createDateTimeString(String pri,Date d,String format){
		
		return  createDateTimeString4Default(pri,d,format);
	}
	
	public static String createDateTimeString4Oracle(String pri,Date d,String format){
		if(d==null)
			return getString(pri)+"  null  ";

		return getString(pri)+"TO_TIMESTAMP('"
		               +Datetimes.formatDatetime(d)
		               +"','YYYY-MM-DD HH24:MI:SS.FF')";
	}
	
	public static String createDateTimeString4MySQL(String pri,Date d,String format){
		if(d==null)
			return getString(pri)+"  null  ";
		return getString(pri)+"DATE_FORMAT('"
		               +Datetimes.formatDatetime(d)
		               +"','%Y %m %d %T')";
	}
	
	public static String createDateTimeString4Default(String pri,Date d,String format){
		//String dstr=Datetimes.format(d, format);
		
		if(d==null)
			return getString(pri)+"  null  ";
		
		if(format==null)
			return getString(pri)+
			   "'"+Datetimes.formatDatetime(d)+"'";
		
		return getString(pri)+
		      "'"+Datetimes.format(d, format)+"'";
	}
	
	public static String getString(String pri){
		return pri==null?"":pri;
	}
	//tanglh
}//end class
