/**
 * 创建红杉树会议接口实现类
 * @author radishlee
 * @since 2011-12-9
 * @describe 创建视频会议功能性接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.videoconference.util.Constants;
import com.seeyon.v3x.videoconference.util.SendXMLToRedFir;

public class CreateInfoWareVideoConferenceManagerImpl implements CreateVideoConferenceManager {
	//记录日志
	private static final Log log = LogFactory.getLog(CreateInfoWareVideoConferenceManagerImpl.class);
	
	/**
	 * @describe 创建视频会议功能性接口，调用红杉树产品接口
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
				sb.append("<action>" + "createReserveMeeting" + "</action>");
				sb.append("<service>" + "meeting" + "</service>");
				sb.append("<type>" + "xml" + "</type>");	
				sb.append("<userName>" + (String)videoParamMap.get("userName") + "</userName>");
				sb.append("<password>" + (String)videoParamMap.get("password") + "</password>");
				sb.append("</header>");
				// body开始
				sb.append("<body>");
				sb.append("<subject>" + (String)videoParamMap.get("subject") + "</subject>");
				sb.append("<startTime>" + (String)videoParamMap.get("startTime") + "</startTime>");
				sb.append("<endTime>" + (String)videoParamMap.get("endTime") + "</endTime>");
				sb.append("<timeZoneId>" + (String)videoParamMap.get("timeZoneId") + "</timeZoneId>");
				sb.append("<attendeeAmount>" + (String)videoParamMap.get("attendeeAmount") + "</attendeeAmount>");
				sb.append("<hostName>" + (String)videoParamMap.get("hostName") + "</hostName>");
				sb.append("<creator>" + (String)videoParamMap.get("creator") + "</creator>");
				sb.append("<openType>" + (String)videoParamMap.get("openType") + "</openType>");
				sb.append("<passwd>"+(String)videoParamMap.get("passwd")+"</passwd>");
				sb.append("<conferencePattern>" + (String)videoParamMap.get("conferencePattern") + "</conferencePattern>");
				sb.append("<agenda>" + (String)videoParamMap.get("agenda") + "</agenda>");
				sb.append("<mailTemplateLocal>" + (String)videoParamMap.get("mailTemplateLocal") + "</mailTemplateLocal>");
				sb.append("<webBaseUrl>" + (String)videoParamMap.get("webBaseUrl")+ "</webBaseUrl>");
				sb.append("</body>");
				sb.append("</Message>");
	
				return SendXMLToRedFir.send((String)videoParamMap.get("webBaseUrl")+"/integration/xml" , sb.toString());
			} catch (Exception e) {
				log.error("创建视频会议出现错误："+e);
				return result;
			}
		}else{
			return Constants.XML_NULL_ERROR;
		}
	}
	
}
