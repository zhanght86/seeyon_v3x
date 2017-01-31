/**
 * 创建红杉树时会议接口实现类
 * @author radishlee
 * @since 2011-12-9
 * @describe 创建视频会议功能性接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.videoconference.util.Constants;
import com.seeyon.v3x.videoconference.util.SendXMLToRedFir;




public class CreateInfoWareInstantMeetingManagerImpl implements CreateVideoConferenceManager {
	//记录日志
	private static final Log log = LogFactory.getLog(CreateInfoWareInstantMeetingManagerImpl.class);
	/**
	 * @describe 创建即时会议功能性接口，调用红杉树产品接口
	 * @author radishlee
	 * @since 2011-12-9
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String createVideoConference(Map videoParamMap) {
		String result = "";
		if(videoParamMap!=null){
			try {
				StringBuffer sb = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
				sb.append("<Message><header>");
				sb.append("<action>" + "createFixedMeeting" + "</action>");
				sb.append("<service>" + "meeting" + "</service>");
				sb.append("<type>" + "xml" + "</type>");	
				sb.append("<userName><![CDATA[" + (String)videoParamMap.get("userName")+ "]]></userName>");
				sb.append("<password><![CDATA[" + (String)videoParamMap.get("password") + "]]></password>");
				sb.append("</header>");
				// body开始
				sb.append("<body>");
				sb.append("<subject><![CDATA[" + (String)videoParamMap.get("subject") + "]]></subject>");
				sb.append("<startTime><![CDATA[" + (String)videoParamMap.get("startTime") + "]]></startTime>");
				sb.append("<endTime><![CDATA[" + (String)videoParamMap.get("endTime") + "]]></endTime>");
				sb.append("<timeZoneId><![CDATA[" + (String)videoParamMap.get("timeZoneId") + "]]></timeZoneId>");
				sb.append("<attendeeAmount><![CDATA[" + (String)videoParamMap.get("attendeeAmount") + "]]></attendeeAmount>");
				sb.append("<hostName><![CDATA[" + (String)videoParamMap.get("hostName") + "]]></hostName>");
				sb.append("<creator><![CDATA[" + (String)videoParamMap.get("creator") + "]]></creator>");
				sb.append("<openType><![CDATA[" + (String)videoParamMap.get("openType") + "]]></openType>");
				sb.append("<passwd><![CDATA["+(String)videoParamMap.get("passwd")+"]]></passwd>");
				sb.append("<beforehandTime><![CDATA[" + (String)videoParamMap.get("beforehandTime") + "]]></beforehandTime>");
				sb.append("<conferencePattern><![CDATA[" + (String)videoParamMap.get("conferencePattern") + "]]></conferencePattern>");
				sb.append("<agenda><![CDATA[" + (String)videoParamMap.get("agenda") + "]]></agenda>");
				sb.append("<mailTemplateLocal><![CDATA[" + (String)videoParamMap.get("mailTemplateLocal") + "]]></mailTemplateLocal>");
				sb.append("<webBaseUrl><![CDATA[" + (String)videoParamMap.get("webBaseUrl")+ "]]></webBaseUrl>");
				sb.append("<attendees><attendee>");
				sb.append("<name><![CDATA[创建者]]></name>");
				sb.append("<email><![CDATA["+(String)videoParamMap.get("email")+"]]></email>");
		     	sb.append("<phone></phone><systemUser>0</systemUser>");
		     	sb.append("</attendee></attendees>");
				sb.append("</body>");
				sb.append("</Message>");
	
				User meetingUser = CurrentUser.get();
				log.info("当前用户:("+meetingUser.getName()+") 用户loginname:("+meetingUser.getLoginName()+") 创建视频会议TO红杉树XML："+sb.toString());
				return SendXMLToRedFir.send((String)videoParamMap.get("webBaseUrl")+"/integration/xml" , sb.toString());
			} catch (Exception e) {
				log.error("创建即时视频会议出现错误："+e);
				return result;
			}
		}else{
			return Constants.XML_NULL_ERROR;
		}
	}
	
}
