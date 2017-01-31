package com.seeyon.v3x.plugin.videoconf.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.videoconference.manager.CheckVideoConferenceServerStatusManagerCAP;


public class CheckInfoWareServiceTask implements Runnable {
	private static Log log = LogFactory.getLog(CheckInfoWareServiceTask.class);
	private CheckVideoConferenceServerStatusManagerCAP checkVideoConferenceServerStatusManagerCAP;

	
	public void run() {
		Map<String, String> parameters = new HashMap<String, String>(); 
		
		String baseUrl = VideoConferenceConfig.WEBBASEURL;
		//System.out.println("CheckInfoWareServiceTask===url===="+baseUrl);
		if(!(baseUrl.indexOf(":")==5||baseUrl.indexOf(":")==4)){
			//log.error("视频会议服务器IP地址配置错误！请检查...url="+VideoConferenceConfig.WEBBASEURL);
			return;
		}
		
		String url[] = VideoConferenceConfig.WEBBASEURL.split(":");  
		parameters.clear();
		parameters.put("baseUrl",baseUrl);
		parameters.put("host",url[1]==null?url[1]:url[1].substring(2, url[1].length()));
		parameters.put("port",url[2]);
		parameters.put("timeOut",String.valueOf(VideoConferenceConfig.VIDEO_TIMEOUT));
		
		String result = checkVideoConferenceServerStatusManagerCAP.checkVideoConferenceServerStatusCap(parameters);
		log.debug("视频会议心跳检测接口开启状态：  "+result);
	}


	public void setCheckVideoConferenceServerStatusManagerCAP(CheckVideoConferenceServerStatusManagerCAP checkVideoConferenceServerStatusManagerCAP) {
		this.checkVideoConferenceServerStatusManagerCAP = checkVideoConferenceServerStatusManagerCAP;
	}
}
