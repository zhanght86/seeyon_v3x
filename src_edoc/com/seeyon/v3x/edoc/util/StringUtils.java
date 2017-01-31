package com.seeyon.v3x.edoc.util;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.manager.EdocFormManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;

public class StringUtils {

	public static Long convertTo(String str){
		Long result = null;
		try{
			result = new Long(str);
		}catch(Exception e){
			
		}
		return result;
	}
	
	public static List xmlElementToList(String xml){
		  String f_xml = xml;
		  int a = xml.indexOf(">");
		  int c = xml.indexOf("</my:myFields>");
		  xml = xml.substring(a+1,c);
		 // String xml_a = f_xml.substring(0,a+1);
		  
		  List<String> list = new ArrayList<String>();
		  
		  String[] str = xml.split("/>");
		  String temp = "";
		  for(int i=0;i<str.length-1;i++){
			  i+=1;
			  String str_a = str[i];
			  int x  = str_a.indexOf(":");
			  str_a = str_a.substring(x+1, str_a.length());
			  if(!str_a.startsWith("field")){
				  list.add(str_a);
				  temp += "<my:"+str_a+"></my:"+str_a+">"; 
			 }
		  }
		  return list;		  
	}
	
	public static String xmlElementToString(String xml){
		  String f_xml = xml;
		  int a = xml.indexOf(">");
		  int c = xml.indexOf("</my:myFields>");
		  xml = xml.substring(a+1,c);
		 // String xml_a = f_xml.substring(0,a+1);
		  String[] str = xml.split("<my:");
		  String temp = "";
		  for(int i=1;i<str.length;i++){
			  String str_a = str[i];
			  int x  = str_a.indexOf(":");
			  int y = str_a.indexOf(">",0);
			  str_a = str_a.substring(0,y);
			  if(!str_a.startsWith("field")){
				  temp += str_a;
				  temp += "|";
			 }
		  }
		  if(!"".equals(temp)){
		  return temp.substring(0,temp.length()-1);
		  }else{
			  return temp;
		  }
	}
	
	public static List<String> findEdocElementFromConfig(String type) throws Exception{
		User user = CurrentUser.get();
		FlowPermManager flowPermManager= (FlowPermManager)ApplicationContextHolder.getBean("flowPermManager");
		List<FlowPerm> permListSend = flowPermManager.getFlowPermsByCategory("edoc_send_permission_policy", user.getLoginAccount());
		List<String> eleListSend = new ArrayList<String>();
		for(FlowPerm perm:permListSend){
			eleListSend.add(perm.getName());
		}
		
		List<FlowPerm> permListRec = flowPermManager.getFlowPermsByCategory("edoc_rec_permission_policy", user.getLoginAccount());
		List<String> eleListRec = new ArrayList<String>();
		for(FlowPerm perm:permListRec){
			eleListRec.add(perm.getName());
		}
		
		List<FlowPerm> permListQianBao = flowPermManager.getFlowPermsByCategory("edoc_qianbao_permission_policy", user.getLoginAccount());
		List<String> eleListQianBao = new ArrayList<String>();
		for(FlowPerm perm:permListQianBao){
			eleListQianBao.add(perm.getName());
		}
		
		List<String> allList = new ArrayList<String>();
		allList.addAll(eleListSend);
		allList.addAll(eleListRec);
		allList.addAll(eleListQianBao);
		
		if(null!=type && Integer.valueOf(type).intValue() == Constants.EDOC_FORM_TYPE_SEND){
			return eleListSend;
		}else if(null!=type && Integer.valueOf(type).intValue() == Constants.EDOC_FORM_TYPE_REC){
			return eleListRec;
		}else if(null!=type && Integer.valueOf(type).intValue() == Constants.EDOC_FORM_TYPE_SIGN){
			return eleListQianBao;
		}else{
			return allList;
		}
	}
}
