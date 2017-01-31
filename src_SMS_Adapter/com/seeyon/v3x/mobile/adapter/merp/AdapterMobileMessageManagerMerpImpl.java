package com.seeyon.v3x.mobile.adapter.merp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.mobile.adapter.AdapterMobileMessageManger;
import com.seeyon.v3x.mobile.message.domain.MobileReciver;

public class AdapterMobileMessageManagerMerpImpl implements AdapterMobileMessageManger {
	
	private static final Log log = LogFactory.getLog(AdapterMobileMessageManagerMerpImpl.class);
	
	private String host;
	
	private String port;
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public boolean isAvailability() {
		return StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port);
	}

	public String getName() {
		return "NC-MERP短信";
	}

	public boolean sendMessage(Long messageId, String srcPhone, String destPhone, String content) {
		Collection<String> destPhones = new ArrayList<String>();
		destPhones.add(destPhone);
		
		return this.sendMessage(messageId, srcPhone, destPhones, content);
	}

	public boolean sendMessage(Long messageId, String srcPhone, Collection<String> destPhone, String content) {
		String url = "http://" + host + ":" + port + "/ufsmap/Service.asmx/message";
		PostMethod method = new PostMethod(url);
		try {
			String getStr = new String(MessageContent.getSendXML(destPhone, content).getBytes("utf-8"), "iso8859-1");
			NameValuePair paras = new NameValuePair("senddata", getStr);
			
			MultiThreadedHttpConnectionManager httpManage = new MultiThreadedHttpConnectionManager();
			HttpClient client = new HttpClient(httpManage);
			int statusCode = HttpStatus.SC_METHOD_FAILURE;
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			method.setRequestBody(new NameValuePair[] {paras});
			HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams(); 
			// 设置连接超时时间(单位毫秒) 
			managerParams.setConnectionTimeout(120000); 
			// 设置读数据超时时间(单位毫秒) 
			managerParams.setSoTimeout(120000);
			// 将请求参数XML的值放入postMethod中 
			try {
				statusCode = client.executeMethod(method);
			} catch (Exception ex) {
				log.error("MERP发送短信连接超时");
				throw new IllegalStateException(ex.toString()); 
			}
			if (statusCode != HttpStatus.SC_OK) {
				log.error("URL访问出错:" + method.getStatusLine());
				return false;
			}
			
			byte[] responseBody = method.getResponseBody();
			String response = new String(responseBody, "UTF-8");
			List<String> result = MessageContent.getReceiveXML(response);
			
			if (NumberUtils.toInt(result.get(0)) == 0) {
				if (log.isDebugEnabled()) {
					log.debug("Merp短信：发往" + destPhone + "成功。");
				}
				return true;
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Merp短信：发往" + destPhone + "失败。");
				}
				return false;
			}
		} catch (Exception e) {
			log.error("短信发送失败：", e);
		} finally {
			method.releaseConnection();
		}
		return false;
	}

	public boolean isSupportQueueSend() {
		return true;
	}

	public boolean isSupportRecive() {
		return false;
	}

	public List<MobileReciver> recive() {
		return null;
	}

}
