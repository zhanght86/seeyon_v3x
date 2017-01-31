package com.seeyon.v3x.office.admin.util;

import java.util.Date;

import com.seeyon.v3x.util.Datetimes;

public class str {
	
	
	public static Date strToDate(String input){
		try{
			return Datetimes.parseDate(input);
		}catch(Exception ex){}
		return null;
	}
	public static String dateToStr(Date date){
		return Datetimes.formatDate(date);
	}
}
