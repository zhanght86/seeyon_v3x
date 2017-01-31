/**
 * 参加视频会议接口实现
 * @author radishlee
 * @since 2011-12-15
 * @describe 开启视频会议功能性接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.videoconference.util.SendXMLToRedFir;

public class JoinInfoWareVideoConferenceManagerImpl implements JoinVideoConferenceManager {
	//记录日志
	private static final Log log = LogFactory.getLog(JoinInfoWareVideoConferenceManagerImpl.class);

	/**
	 * 参加红杉树会议接口实现
	 * @author radishlee
	 * @since 2011-12-15
	 * @describe 参加视频会议功能性接口，调用红杉树产品接口
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String joinVideoConference(Map videoParamMap){
		String result = "";
		
		try {
			StringBuffer sb = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
			sb.append("<Message><header>");
			sb.append("<action>" + "joinMeeting" + "</action>");
			sb.append("<service>" + "meeting" + "</service>");
			sb.append("<type>" + "xml" + "</type>");	
			sb.append("<userName><![CDATA[" + (String)videoParamMap.get("userName") + "]]></userName>");
			sb.append("<password><![CDATA[" + (String)videoParamMap.get("password") + "]]></password>");
			sb.append("</header>");
			// body开始
			sb.append("<body>");
			sb.append("<attendeeName><![CDATA[" + (String)videoParamMap.get("attendeeName") + "]]></attendeeName>");
			sb.append("<confKey><![CDATA[" + (String)videoParamMap.get("confKey")+ "]]></confKey>");
			sb.append("<meetingPwd><![CDATA[" + (String)videoParamMap.get("meetingPwd")+ "]]></meetingPwd>");
			sb.append("<email></email>");
			sb.append("<webBaseUrl><![CDATA[" + (String)videoParamMap.get("webBaseUrl")+ "]]></webBaseUrl>");
			//radishlee add 2012-4-11
     		if(VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE){
     			if(videoParamMap.get("serverName")!=null){
     				sb.append("<serverName><![CDATA[" + (String)videoParamMap.get("serverName")+ "]]></serverName>");
     			}
     		}
			sb.append("</body>");
			sb.append("</Message>");

			User meetingUser = CurrentUser.get();
			log.info("当前用户:("+meetingUser.getName()+") 用户loginname:("+meetingUser.getLoginName()+") 参加视频会议TO红杉树XML："+sb.toString());
			return SendXMLToRedFir.send((String)videoParamMap.get("webBaseUrl")+"/integration/xml" , sb.toString());
		} catch (Exception e) {
			log.error("参加视频会议出现错误："+e);
			return result;
		}
	}

}
