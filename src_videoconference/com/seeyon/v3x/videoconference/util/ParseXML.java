package com.seeyon.v3x.videoconference.util;



import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class ParseXML {
	private static Log log = LogFactory.getLog(ParseXML.class);
	//解析2层xml
	public static Map parseXML(String xmlSource) {
		Map parameterMap = new HashMap();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xmlSource);
		} catch (DocumentException e) {
			log.error("xml解析失败"+e);
			return null;
		}
		if (doc != null) {
			Element root = doc.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element headOrBodyElement = (Element) i.next();
				parameterMap.put(headOrBodyElement.getName(),headOrBodyElement.getText());
					for (Iterator j = headOrBodyElement.elementIterator(); j.hasNext();) {
						Element parameterElement = (Element) j.next();
						parameterMap.put(parameterElement.getName(),parameterElement.getText());
				}
			}
		}

		//如果是读取加密狗xml信息
		if(parameterMap.get("message")!=null||!"".equals(parameterMap.get("message"))){
			if("success".equals(parameterMap.get("message"))){
				return parameterMap;
			}
		}
		
		if ("SUCCESS".equals(parameterMap.get("result"))) {
			return parameterMap;
		}
		
		if ("FAILURE".equals(parameterMap.get("result"))){
			return parameterMap;
		}
		return null;
	}

	//解析参会信息xml
	public static List parseXML4StartMsg(String xmlSource) {
		
		List paramList = new LinkedList();
		Document doc = null;

		try {
			doc = DocumentHelper.parseText(xmlSource);
		} catch (DocumentException e) {
			log.error("xml解析失败"+e);
		}
		if (doc != null) {
			Element root = doc.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element headOrBodyElement = (Element) i.next();
				
				for (Iterator j = headOrBodyElement.elementIterator(); j.hasNext();) {
					Element parentElement = (Element) j.next();
					//如果是body里的meetinginfo才循环 
					if("MeetingInfo".equals(parentElement.getName())){
						Map parameterMap = new HashMap();
					    for (Iterator k = parentElement.elementIterator(); k.hasNext();) {
					    	 Element param = (Element) k.next();
					    	 parameterMap.put(param.getName(),param.getText());
					    }
					    paramList.add(parameterMap);
					}
				}
					
			}
		}
		
		return paramList;
	}
	
	//解析参会信息xml
	public static List<Map<String,String>> parseXML4GetMeetingListMsg(String xmlSource) {
		List<Map<String,String>> paramList = new LinkedList<Map<String,String>>();
		Document doc = null;

		try {
			doc = DocumentHelper.parseText(xmlSource);
		} catch (DocumentException e) {
		}
		if (doc != null) {
			Element root = doc.getRootElement();
			for (Iterator i = root.elementIterator(); i.hasNext();) {
				Element headOrBodyElement = (Element) i.next();
				for (Iterator j = headOrBodyElement.elementIterator(); j.hasNext();) {
					Element parentElement = (Element) j.next();
					if("servers".equals(parentElement.getName())){
					    for (Iterator k = parentElement.elementIterator(); k.hasNext();) {
					    	Element param = (Element) k.next();
					    	Map parameterMap = new HashMap();
			    		    for(Iterator l = param.elementIterator(); l.hasNext();){
			    			    Element sonParam = (Element) l.next();
			    			    parameterMap.put(sonParam.getName(),sonParam.getText());
						    }
			    		    parameterMap.put("modifytime", new Date().getTime());//添加服务器时间判断项，为更新缓存做标志位
							paramList.add(parameterMap);
					    }
					    break;
					}
				}
			}
		}
		return paramList;
	}
}
