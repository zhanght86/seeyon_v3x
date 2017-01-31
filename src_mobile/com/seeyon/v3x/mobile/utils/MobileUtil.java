package com.seeyon.v3x.mobile.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.util.Strings;

public class MobileUtil {

	/**
	 * 初始化 变量obj
	 * @param obj
	 * @return "0";
	 */
	public  int initVariable(String obj){
		if(obj==null){
			return 0;
		}else{
			return Integer.parseInt(obj);
		}
	}
	
	/**
	 * 得到 总共的页数
	 * @param num  后台传过来的总共的列表数
	 * @return
	 */
	public int getPageCount(int num,int count){
		
		float floatNum = (float) num /count;
		int  intNum = (int) num /count;
		
		if (floatNum - intNum != 0) {
			intNum = intNum + 1;
		} 
		if(intNum==0){
			intNum = 1;
		}
		
		return intNum;
	}
	
	/**
	 * 得到当前页码数
	 * @param type  0--上一页, 1--下一页 
	 * @param pageNum 提交的页码数
	 * @return
	 */
	public int getCurrentPage(String type,String pageNum){
		int page = 1;
		if(Strings.isNotBlank(type)&&Strings.isNotBlank(pageNum)){
			page = Integer.parseInt(pageNum);
			switch(Integer.parseInt(type)){
			case 0:
				page = page - 1;
				break;
			case 1:
				page = page + 1;
				break;
			}
		}else{
			if(pageNum!=null){
				page = Integer.parseInt(pageNum);
			}
		}
		return page;
	}
	
	/**
	 * 判断字符串 是不是 数字
	 * @param str
	 * @return
	 */
	public boolean isNumber(String str){
		if(str!=null){
			String pattern = "[0-9]*";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(str);
			return m.matches();
		}else{
			return false;
		}
	}
	
	public static Map<String,String> selectPerBackPara(HttpServletRequest request){
		Map<String,String> parameter = new HashMap<String,String>();
		parameter.put("did", request.getParameter("did"));
		parameter.put("newColl", request.getParameter("newColl"));
		parameter.put("selectType", request.getParameter("selectType"));
		parameter.put("source", request.getParameter("source"));
		parameter.put("cid", request.getParameter("cid"));
		parameter.put("nid", request.getParameter("nid"));
		parameter.put("extendType", request.getParameter("extendType"));
		parameter.put("fid", request.getParameter("fid"));
		parameter.put("noMatch", request.getParameter("noMatch"));
		parameter.put("id", request.getParameter("id"));
		parameter.put("parentpath", request.getParameter("parentpath"));
		parameter.put("account", request.getParameter("account"));
		parameter.put("isAccount", request.getParameter("isAccount"));
		return parameter;
	}
}
