package com.seeyon.v3x.videoconference.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SendXMLToRedFir {
	//记录日志
	private static final Log log = LogFactory.getLog(SendXMLToRedFir.class);
	
	public static String send(String urlAddr, String sendData) throws Exception {
		HttpURLConnection conn = null;
		boolean isSuccess = false;
		StringBuffer sb = new StringBuffer("");
		StringBuffer params = new StringBuffer();

		params.append(sendData);

		try {
			URL url = new URL(urlAddr);
			conn = (HttpURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(params
					.length()));
			conn.setDoInput(true);
			conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(conn
					.getOutputStream(), "UTF-8");
			out.write(params.toString());
			out.flush();
			out.close();

			int code = conn.getResponseCode();
			if (code != 200) {
			   throw new Exception("ERROR===" + code);
			} else {
				isSuccess = true;

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
				String s = "";

				while ((s = br.readLine()) != null) {
					sb.append(s + "\r\n");
				}
				br.close();
			}
		} catch (Exception ex) {
			log.info("发送创建会议消息失败！"+ex);
			return Constants.ROMOTE_SERVER_NULL_ERROR;
		} finally {
			conn.disconnect();
		}
		
		
		return InfoWareExceptionCheck.checkInfoWareParams(sb.toString());
	}

}
