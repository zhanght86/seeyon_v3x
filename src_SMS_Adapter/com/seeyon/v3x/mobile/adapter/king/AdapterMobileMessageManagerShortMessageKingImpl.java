package com.seeyon.v3x.mobile.adapter.king;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chinasms.sms.Sender;
import com.seeyon.v3x.mobile.adapter.AdapterMobileMessageManger;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;
import com.seeyon.v3x.util.Strings;

public class AdapterMobileMessageManagerShortMessageKingImpl implements
		AdapterMobileMessageManger {
	private static Log log = LogFactory.getLog(AdapterMobileMessageManagerShortMessageKingImpl.class);
	private String kingName;
	private String kingPassword;
	private boolean supportRecive = true;

	public void setKingName(String kingName) {
		this.kingName = kingName;
	}

	public void setKingPassword(String kingPassword) {
		this.kingPassword = kingPassword;
	}
	
	public void setSupportRecive(boolean supportRecive) {
		this.supportRecive = supportRecive;
	}

	public boolean isAvailability(){
		return Strings.isNotBlank(kingName) 
		&& Strings.isNotBlank(kingPassword) 
		;
	}
	
	public String getName() {
		return "短信王短信";
	}

	public boolean isSupportQueueSend() {
		return true;
	}

	public boolean isSupportRecive() {
		return supportRecive;
	}

	public List<MobileReciver> recive() {
		Sender sender = new Sender(kingName,kingPassword);
		String str = sender.readSms();
		//id=32758888&err=成功&src=13630697652&msg=7xcs,早!&dst=10657558012667081&time=200711190858
		String string = null;
		String srcphonenum = null;
		String srcPhone = null;
		String content = null;
		
		if(str.indexOf("&msg=")!=str.indexOf("&dst=")){
			string = str.substring(str.indexOf("&msg"), str.indexOf("&dst"));
			content = string.substring(string.indexOf("=")+1);
		}
		if(str.indexOf("&src=")!=str.indexOf("&msg=")){
			srcphonenum = str.substring(str.indexOf("&src="),str.indexOf("&msg="));
			if(srcphonenum!=null){
				srcPhone = srcphonenum.substring(srcphonenum.indexOf("=")+1);
			}
		}

		List<MobileReciver> list = new  ArrayList<MobileReciver>();
		if(content!=null&&srcPhone!=null){
			MobileReciver reciver = new MobileReciver();
			reciver.setContent(content);
			reciver.setSrcPhone(srcPhone);
			list.add(reciver);
		}
		return list;							
	}
	
	private static Map<String, String> getParameters(String str) {
		Map<String, String> param = new HashMap<String, String>();
		
		if (str != "" && str != null) {
			String[] entry = str.split("[&]");
			if(entry != null){
				for (String s : entry) {
					String[] k_v = s.split("=");
					
					if(k_v.length == 2){
						param.put(k_v[0].toLowerCase(), k_v[1]);
					}
					else{
						param.put(k_v[0].toLowerCase(), s.endsWith("=") ? "" : null);
					}
				}
			}
		}

		return param;
	}

	public boolean sendMessage(Long messageId, String srcPhone,
			String destPhone, String content) {
		//短信的内容不超过 60个字，小灵通号不超过 40个字。
		Sender sender = new Sender(kingName,kingPassword);
		String result = sender.massSend(destPhone, content, "", "");
		
		String success = getParameters(result).get("success");
		if(Strings.isBlank(success)){
			return false;
		}
		
		String[] str = success.split(",");
		
		boolean successSend = false;
		for(int i=0;i<str.length;i++){
			if(str[i].equals(destPhone)){
				successSend = true;
			}
		}
		
		return successSend;
	}

	public boolean sendMessage(Long messageId, String srcPhone,
			Collection<String> destPhone, String content) {
		Sender sender = new Sender(kingName,kingPassword);
		String phone = null;
		for(String str : destPhone){
			if(phone!=null){
				phone = phone+","+ str;
			}else{
				phone = str;
			}
		}
		String result = null;
		
		try {
			result = sender.massSend(phone, content, "", "");
		}
		catch(Exception e){
			log.error("信息内容中含有非法字符");
			return false;
		}
		
		if("发送成功！".equals(getParameters(result).get("err"))){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isSupportSplit(){
    	return false;
    }
}