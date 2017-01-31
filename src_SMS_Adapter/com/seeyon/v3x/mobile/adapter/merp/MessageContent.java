package com.seeyon.v3x.mobile.adapter.merp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * NC-MERP短信:创建请求XML和解析响应XML
 * @author renhy
 *
 */
public class MessageContent {
	
	private static final Log log = LogFactory.getLog(MessageContent.class);

	/**
	 * 创建请求XML
	 * @param contentMessage
	 * @param destPhone
	 * @return
	 */
	public static String getSendXML(Collection<String> destPhones, String contentMessage) {
		String content = "";
		try{
			Element data = DocumentFactory.getInstance().createElement("Data");
			
			Document document = DocumentFactory.getInstance().createDocument(data);
			document.setXMLEncoding("utf-8");
			
			data.addAttribute("System", "A8");//接入系统代码
			data.addAttribute("type", "01");//01-短信发送
			data.addAttribute("accountnum", "");//帐套号,可以为空
			data.addAttribute("accountname", "");//帐套名称,可以为空
			data.addAttribute("sendname", "");//发送人姓名,可以为空
			
			Element row = data.addElement("Row");//每一个Row节点代表一条发送短信,可包含多个Row节点
			
			for(String destPhone : destPhones){
				Element mobile = row.addElement("Mobile");//每一个Mobile节点代表一个接收者手机号码,可包含多个Mobile节点,其中手机号码如果是小灵通需加区号
				mobile.addElement("MobileNumber").addText(destPhone);//手机号码
			}
			
			row.addElement("Content").addText(contentMessage);//短信正文
			
			content = document.asXML();
		}catch(Exception e){
			log.error("创建请求XML出错:", e);
		}
		return content;
	}
	
	/**
	 * 解析响应XML
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getReceiveXML(String xml){
		List<String> result = new ArrayList<String>();
		try {
			StringReader read = new StringReader(xml);
			SAXReader reader = new SAXReader();
			Document document = reader.read(read);
			
			Element data = document.getRootElement();
			List<Element> childList = data.elements();
			
			if(childList != null){
				Element tranFlag = childList.get(0);//发送结果标志:0-成功,1-失败
				result.add(tranFlag.getText());
				Element errMsg = childList.get(1);//状态信息描述
				result.add(errMsg.getText());
			}
		} catch (Exception e) {
			log.error("解析响应XML出错:", e);
		}
		return result;
	}
	
}
