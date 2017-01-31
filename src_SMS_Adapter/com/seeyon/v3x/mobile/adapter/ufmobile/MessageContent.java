package com.seeyon.v3x.mobile.adapter.ufmobile;

import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

/**
 * 消息格式
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: UFIDA-Seeyon</p>
 * @author tanmf
 * @version 1.0
 */
public class MessageContent {
  public MessageContent() {
  }

  /**
   * 产生发送短信的xml，并base64编码
   * @param aMsg ISmsMessage
   * @return String
   */
  public static String getSendSMSXML(String contentMessage,String destPhone) {	
    String msg = "";
    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    msg = new String(encoder.encode(contentMessage.getBytes()));
    String content = "";
    try {
      Element root = DocumentFactory.getInstance().createElement("root");
      Document document = DocumentFactory.getInstance().createDocument(root);
      document.setXMLEncoding("utf-8");

      Element seesionid = root.addElement("seesionid");
      seesionid.addAttribute("id", "1234567");

      Element protocol = root.addElement("protocol");
      protocol.addElement("name").addText("SeeyonSMS");
      protocol.addElement("version").addText("1.0");
      protocol.addElement("cmd").addText("send");
      protocol.addElement("description").addText("UFIDA");

      Element data = root.addElement("data");
      Element destNumber = data.addElement("DestNumber");
      Element number = destNumber.addElement("number");
      number.addAttribute("msgid", String.valueOf(System.currentTimeMillis()));
      number.addAttribute("phone", destPhone);

      Element Message = data.addElement("Message");
      Message.addText(msg);

      content = document.asXML();
    }
    catch (Exception ex) {
    }

    try {
      return new String(encoder.encode(content.getBytes("utf-8"))); //Base64编码
    }
    catch (UnsupportedEncodingException ex) {
      return "";
    }
  }
  /**
   * 生产统计的xml
   * @return String
   */
  public static String getCountXML() {
    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    String content = "";
    try {
      Element root = DocumentFactory.getInstance().createElement("root");
      Document document = DocumentFactory.getInstance().createDocument(root);
      document.setXMLEncoding("utf-8");

      Element seesionid = root.addElement("seesionid");
      seesionid.addAttribute("id", "1234567");

      Element protocol = root.addElement("protocol");
      protocol.addElement("name").addText("SeeyonSMS");
      protocol.addElement("version").addText("1.0");
      protocol.addElement("cmd").addText("Getcount");
      protocol.addElement("description").addText("用友致远");

      content = document.asXML();
    }
    catch (Exception ex) {
    }

    try {
      return new String(encoder.encode(content.getBytes("utf-8"))); //Base64编码
    }
    catch (UnsupportedEncodingException ex) {
      return "";
    }
  }
  /**
   * 获得发送结果XML
   * @return String
   */
  public static String getRuseltXML() {
    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    String content = "";
    try {
      Element root = DocumentFactory.getInstance().createElement("root");
      Document document = DocumentFactory.getInstance().createDocument(root);
      document.setXMLEncoding("utf-8");

      Element seesionid = root.addElement("seesionid");
      seesionid.addAttribute("id", "1234567");

      Element protocol = root.addElement("protocol");
      protocol.addElement("name").addText("SeeyonSMS");
      protocol.addElement("version").addText("1.0");
      protocol.addElement("cmd").addText("return_send");
      protocol.addElement("description").addText("用友致远");

      content = document.asXML();
    }
    catch (Exception ex) {
    }

    try {
      return new String(encoder.encode(content.getBytes("utf-8"))); //Base64编码
    }
    catch (UnsupportedEncodingException ex) {
      return "";
    }
  }

  /**
   * 产生请求回复短信的xml
   *
   * @return String
   */
  public static String getSMSInfoXML() {
    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
    String content = "";
    try {
      Element root = DocumentFactory.getInstance().createElement("root");
      Document document = DocumentFactory.getInstance().createDocument(root);
      document.setXMLEncoding("GBK");

      Element seesionid = root.addElement("seesionid");
      seesionid.addAttribute("id", "1234567");

      Element protocol = root.addElement("protocol");
      protocol.addElement("name").addText("SeeyonSMS");
      protocol.addElement("version").addText("1.0");
      protocol.addElement("cmd").addText("getsmsinfo");
      protocol.addElement("description").addText("用友致远");

      root.addElement("data").addAttribute("iCount", "1");

      content = document.asXML();
    }
    catch (Exception ex) {
    }

    try {
      return new String(encoder.encode(content.getBytes("GBK"))); //Base64编码
    }
    catch (UnsupportedEncodingException ex) {
      return "";
    }
  }
  /**
 * 返回指定数目(iCount)的短信结果
 *
 * @return String
 */
public static String getReceivedSMSInfoXML() {
  String content = "";
  try {
    Element root = DocumentFactory.getInstance().createElement("root");
    Document document = DocumentFactory.getInstance().createDocument(root);
    document.setXMLEncoding("utf-8");

    Element seesionid = root.addElement("seesionid");
    seesionid.addAttribute("id", "1234567");

    Element protocol = root.addElement("protocol");
    protocol.addElement("name").addText("SeeyonSMS");
    protocol.addElement("version").addText("1.0");
    protocol.addElement("cmd").addText("RET_Getsmsinfo");
    protocol.addElement("description").addText("用友致远");
    root.addElement("data").addAttribute("iCount", "1");
    Element data = protocol.addElement("data");
    Element replyinfo = data.addElement("REPLYINFO");
    replyinfo.addAttribute("count", "1");
    Element number = replyinfo.addElement("NUMBER");
    number.addAttribute("phone","1388095076");
    number.addAttribute("message","测试XML");
    number.addAttribute("replytime","20060726");

    content = document.asXML();
  }
  catch (Exception ex) {
  }
return content;

}


}
