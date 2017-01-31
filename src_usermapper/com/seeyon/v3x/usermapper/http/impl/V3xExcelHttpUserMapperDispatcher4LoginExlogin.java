package com.seeyon.v3x.usermapper.http.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.seeyon.v3x.usermapper.util.UserMapperUtil;

public class V3xExcelHttpUserMapperDispatcher4LoginExlogin extends
		V3xBaseExcelHttpUserMapperDispatcher {

	@Override
	protected void proceedList(List<List<String>> data, HttpServletRequest request,
			HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		Map<String, List<String>> ums=this.fetchMapLoginExlogin(data);
		
		this.getAction().begin(
				this.police(request, response));
		this.getAction().execute4LoginExLogin(ums);
		this.getAction().ok();
	}

	protected Map<String, List<String>> fetchMapLoginExlogin(List<List<String>> ll)
		throws Exception {		
		Map<String, List<String>> rm=new HashMap<String, List<String>>();
		Map<String,String> eloginm=new HashMap<String,String>();
		if(ll==null)
			return rm;
		 
		for(List<String> l:ll){
			if(l==null)
				continue;
			if(l.size()<2)
				continue;
			
			String login=ignorChars(l.get(0),",");
			if(!StringUtils.hasText(login))
				continue;
			String elogin=ignorChars(l.get(1),",");
			if(!StringUtils.hasText(elogin))
				continue;
			String v=eloginm.get(elogin);
			if(StringUtils.hasText(v))
				continue;
			
			List<String> list=rm.get(login);
			if(list==null)
				list=new ArrayList<String>();
			list.add(elogin);
			
			rm.put(login, list);
			eloginm.put(elogin, login);
		}
		
		return rm;
	}
	
	protected String ignorChars(String org,String ch){
		if(!StringUtils.hasText(org) || !StringUtils.hasText(ch))
			return org;
		
		String[] des=org.split(ch);
		StringBuffer sb=new StringBuffer();
		for(String s:des){
			if(StringUtils.hasText(s))
				sb.append(s);
		}
		
		return sb.toString();
	}
}//end class
