/**
 * 获取视频会议服务器信息列表接口
 * @author radishlee
 * @since 2012-4-10
 * @describe 获取视频会议服务器信息列表接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.videoconference.util.SendXMLToRedFir;


/**
 * 获取视频会议服务器信息列表
 * @author radishlee
 * @since 2012-4-10
 * @describe 获取视频会议服务器信息列表，调用第三方产品接口
 * @param videoParamMap
 * @return String类型返回值
 */
public class GetVideoConferenceServerListManagerImpl implements GetVideoConferenceServerListManager {
	//记录日志
	private static final Log log = LogFactory.getLog(GetVideoConferenceServerListManagerImpl.class);
	
	public String getVideoConferenceServerList(Map videoParamMap) {
		String result = "";
			
		try {
			StringBuffer sb = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
			sb.append("<Message><header>");
			sb.append("<action>" + "listMeetingServer" + "</action>");
			sb.append("<service>" + "meeting" + "</service>");
			sb.append("<type>" + "xml" + "</type>");	
			sb.append("<userName>" + (String)videoParamMap.get("userName") + "</userName>");
			sb.append("<password>" + (String)videoParamMap.get("password") + "</password>");
			sb.append("</header>");
			// body开始
			sb.append("<body>");
			sb.append("</body>");
			sb.append("</Message>");
	
			log.debug("得到会议服务器列表TO红杉树XML："+sb.toString());
			return SendXMLToRedFir.send((String)videoParamMap.get("webBaseUrl")+"/integration/xml" , sb.toString());
		} catch (Exception e) {
			log.error("获取视频会议服务器信息列表：",e);
			return result;
		}
	}

}
