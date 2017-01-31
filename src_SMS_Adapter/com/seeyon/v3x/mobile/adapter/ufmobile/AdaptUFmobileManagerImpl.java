package com.seeyon.v3x.mobile.adapter.ufmobile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.seeyon.v3x.mobile.adapter.AdapterMobileMessageManger;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;
import com.seeyon.v3x.util.Strings;

public class AdaptUFmobileManagerImpl implements AdapterMobileMessageManger {
	private static final Log log = LogFactory.getLog(AdaptUFmobileManagerImpl.class);
	
	private String host;
	
	private String port;
	
	private boolean supportRecive = false;
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setSupportRecive(boolean supportRecive) {
		this.supportRecive = supportRecive;
	}

	public String getName() {
		return "UFMOBILE 短信";
	}
	
	public boolean isAvailability(){
		return Strings.isNotBlank(host) 
		&& Strings.isNotBlank(port) 
		;
	}

	public boolean isSupportQueueSend() {
		return false;
	}

	public boolean isSupportRecive() {
		return supportRecive;
	}

	public List<MobileReciver> recive() {
		List<MobileReciver> list = readAppReceived();
		if(list!=null){
			return list;
		}else{
			return null;
		}
	}

	public boolean sendMessage(Long messageId, String srcPhone, String destPhone, String content) {
		 String qry = MessageContent.getSendSMSXML(content,destPhone);
		 try {
			String result = doSend("sendsms",qry);
			if(result!=null){
				String str = result.substring(result.indexOf("<result>"), result.indexOf("</result>"));
				if(str!=null){
					String str1 = str.substring(str.indexOf(">")+1);

					if(str1.equals("成功")){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
			//需要对result进行分解
		} catch (UfmobileException e) {
			log.error("通过UFMobile发送消息错误，错误为：",e);
			return false;
		}
	}

	public boolean sendMessage(Long messageId, String srcPhone, Collection<String> destPhone, String content) {
		return false;
	}
	
	 /**
	   * 按照某个指令发送数据，并返回结果
	   *
	   * @param key String
	   * @param content String
	   * @return String
	   */
	  private String doSend(String key, String content) throws UfmobileException {
	    String input = "";
	    String p = port!=null?port.trim():"";
	    InputStream finput=null;
	    OutputStream foutput=null;
	    try {
	      //test.test(content);
	      URL url = new URL("http://" + host + ":" + p + "/" + key);

	      /*URL url = new URL("http://127.0.0.1:8080/index.jsp");*/
	    URLConnection uc = url.openConnection();
	      if (! (uc instanceof HttpURLConnection)) {
	    	  log.error("传输协议错误！！！");
	        return "";
	      }
	      
	      HttpURLConnection fcon = (HttpURLConnection) uc;
	      fcon.setRequestMethod("POST"); // 把HTTP请求方法设置为POST（默认的是GET）
	      fcon.setRequestProperty("Content-type","text/xml;charset=utf-8");
	      fcon.setDoOutput(true);
	      foutput=fcon.getOutputStream();
	      BufferedWriter foutwriter =new BufferedWriter(new OutputStreamWriter(foutput,"utf-8"));
	      foutwriter.write(content); //发送Base64编码后的xml文件
	      foutwriter.flush();
	      foutwriter.close();
	      finput=fcon.getInputStream();
	      BufferedReader ftempreader=new BufferedReader(new InputStreamReader(finput,"GBK"));
	      StringWriter fstringwriter=new StringWriter(300);
	      int flen;
	      char[] fchararray=new char[1024];
	      do{
	        flen=ftempreader.read(fchararray);
	        if (flen>0){
	          fstringwriter.write(fchararray,0,flen);
	        }
	      }
	      while (flen>=0);
	      String ftemp=fstringwriter.getBuffer().toString();
	      //System.out.print("ftemp="+ftemp);
	      input = ftemp;
	      ftempreader.close();
	      fstringwriter.close();

	/*
	      finput = hc.getInputStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(finput)); //接收返回信息
	        TDebugOutMsg.outMsg("read="+in.read());
	        TDebugOutMsg.outMsg("ready="+in.ready());
	        String inputline1 = null;
	        while ( (inputline1 = in.readLine()) != null) {
	          input += inputline1;
	        }
	        in.close();
	        dos.close();
	*/
	    }
	    catch (Exception ex3) {
	      throw new UfmobileException(ex3.toString());
	    }
	    finally{
	    	if(finput != null){
	    		try {
					finput.close();
				}
				catch (Exception e) {
				}
	    	}
	    	if(foutput != null){
		    	try {
					foutput.close();
				} catch (Exception e) {
					log.error("关闭输入输出流报错!");
				}
	    	}
	    }
	    return input;
	  }
	

	  private String ReceivedContent;

	  public void setReceivedContent(String receivedContent) {
		ReceivedContent = receivedContent;
	}

	public void SendReceivedQuest() {
	    String qry = MessageContent.getSMSInfoXML();
	    try {
	      String result = doSend("Getsmsinfo", qry);
	      ReceivedContent = result;
	    }
	    catch (UfmobileException ex) {
	    	log.error("通过UFMobile接收短信发生错误",ex);
	    	ReceivedContent = null;
	    }
	  }


	public List<MobileReciver> readAppReceived(){
	    SendReceivedQuest();
	    //ReceivedContent="<?xml version='1.0' encoding='gb2312'?><root><seesionid id='1234567'/><protocol><name>SeeyonSMS</name><version>1.0</version><cmd>RET_Getsmsinfo</cmd><description>用友致远</description> <data><REPLYINFO count='1'><NUMBER phone='1388095076' message='测试XML' replytime='20060726'/></REPLYINFO></data></protocol></root>";
	    if(ReceivedContent!=null){
	    StringReader in=new StringReader(ReceivedContent);
	    SAXReader saxReader = new SAXReader();
	    Document document = null;
	    try {
	      //document = saxReader.read("D://123.xml");
	      document= saxReader.read(in);
	    }
	    catch (DocumentException ex) {
	    }
	    //TDebugOutMsg.outMsg("document="+document);
	    List listcount = document.selectNodes("//REPLYINFO/@count");
	    Iterator itercount = listcount.iterator();
	    Integer tempInt;
	    int count;
	    Attribute attribute = (Attribute) itercount.next();
	    tempInt = Integer.valueOf(attribute.getValue());
	    count = tempInt.intValue();
	    if(count<0){
	    	count =0;
	    }
	    //TDebugOutMsg.outMsg("count="+count);
	    List<String> smsPhone = new ArrayList<String>(count);
	    List<String> smsMsg = new ArrayList<String>(count);
	    List<String> smsreplytime = new ArrayList<String>(count);
	    //获得返回的信息
	    List Msglist = document.selectNodes("//REPLYINFO/NUMBER/@message");
	    Iterator Msgiter = Msglist.iterator();
	    while (Msgiter.hasNext()) {
	      Attribute attributeMsg = (Attribute) Msgiter.next();
	      //TDebugOutMsg.outMsg(attributeMsg.getText());
	      log.info("得到返回的信息"+attributeMsg.getText());
	      smsMsg.add(attributeMsg.getText());
	    }

	     //获得返回的电话
	     List phonelist = document.selectNodes("//REPLYINFO/NUMBER/@phone");
	    Iterator phoneiter = phonelist.iterator();
	    while (phoneiter.hasNext()) {
	      Attribute attributephone = (Attribute) phoneiter.next();
	     // TDebugOutMsg.outMsg(attributephone.getText());
	      log.info("得到返回的Phone"+attributephone.getText());
	      smsPhone.add(attributephone.getText());
	    }
	    //获得返回的时间
	    List replytimelist = document.selectNodes("//REPLYINFO/NUMBER/@replytime");
	    Iterator replytimeiter = replytimelist.iterator();
	    while (replytimeiter.hasNext()) {
	      Attribute attributereplytime = (Attribute) replytimeiter.next();
	     // TDebugOutMsg.outMsg(attributereplytime.getText());
	      log.info("得到返回的Time"+attributereplytime.getText());
	      smsreplytime.add(attributereplytime.getText());
	    }

	    //ISmsReceived[] result = new ISmsReceived[count];
	    List<MobileReciver> list = new ArrayList<MobileReciver>(count);
	    for(int i=0;i<count;i++){
	    	String content = smsMsg.get(i).toString().substring(12);
	    	String phone = smsPhone.get(i).toString();
	    	MobileReciver reciver = new MobileReciver(phone, content);

	    	list.add(reciver);

    		}
    		return list;
    	}
	    
	    return null;
	}
	
	public boolean isSupportSplit(){
    	return false;
    }
}