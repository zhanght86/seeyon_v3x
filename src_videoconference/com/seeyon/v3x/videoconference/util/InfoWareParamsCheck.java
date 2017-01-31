/**
 * 检查String类型类
 * @author radishlee
 * @since 2011-12-13
 */
package com.seeyon.v3x.videoconference.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class InfoWareParamsCheck {
	
	/**
	 * @describe 检查传入第三方接口是否正确
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public static String checkInfoWareParams(Map paramMap){
		String userName = (String)paramMap.get("userName");
		if(StringUtils.isBlank(userName)){
			return Constants.USERNAME_NULL_ERROR;
		}
		String password = (String)paramMap.get("password");
		if(StringUtils.isBlank(password)){
			return Constants.PASSWORD_NULL_ERROR;
		}
		return "PASS";
	}
	/**
	 * @describe 检查传入创建视频会议接口参数
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public static String checkParams(Map paramMap){
		String result = checkInfoWareParams(paramMap);
		
		String subject = (String)paramMap.get("subject");
		if(StringUtils.isBlank(subject)){
			return Constants.SUBJECT_NULL_ERROR;
		}
		
		String startTime = (String)paramMap.get("startTime");
		if(StringUtils.isBlank(startTime)){
			return Constants.STARTTIME_NULL_ERROR;
		}
		
		String timeFormats[] = startTime.split("T");
		if(!(timeFormats[0].length()==10&&timeFormats[1].length()==8)){
			return Constants.STARTTIME_FORMAT_FAULT_ERROR;
		}
		
		String endTime = (String)paramMap.get("endTime");
		if(StringUtils.isBlank(endTime)){
			return Constants.ENDTIME_NULL_ERROR;
		}
		
		String timeFormats1[] = endTime.split("T");
		if(!(timeFormats1[0].length()==10&&timeFormats1[1].length()==8)){
			return Constants.ENDTIME_FORMAT_FAULT_ERROR;
		}
		
		String timeZoneId = (String)paramMap.get("timeZoneId");
		if(StringUtils.isBlank(timeZoneId)){
			return Constants.TIMEZONEID_NULL_ERROR;
		}
		
		String attendeeAmount = (String)paramMap.get("attendeeAmount");
		if(StringUtils.isBlank(attendeeAmount)){
			return Constants.ATTENDEEAMOUNT_NULL_ERROR;
		}
		
		String creator = (String)paramMap.get("creator");
		if(StringUtils.isBlank(creator)){
			return Constants.CREATER_NULL_ERROR;
		}
		
		String hostName = (String)paramMap.get("hostName");
		if(StringUtils.isBlank(hostName)){
			return Constants.HOSTNAME_NULL_ERROR;
		}
		
		//会议类型 公开：true 不公开：false
		String openType = (String)paramMap.get("openType");
		if(StringUtils.isBlank(openType)){
			return Constants.OPENTYPE_NULL_ERROR;
		}
		
		//会议密码
		String passwd = (String)paramMap.get("passwd");
		if(StringUtils.isBlank(passwd)){
			return Constants.PASSWD_NULL_ERROR;
		}
		
		//会议模式
		String conferencePattern = (String)paramMap.get("conferencePattern");
		if(StringUtils.isBlank(conferencePattern)){
			return Constants.CONFERENCEPATTERN_NULL_ERROR;
		}
		
		//邮件语言区域
		String mailTemplateLocal = (String)paramMap.get("mailTemplateLocal");
		if(StringUtils.isBlank(mailTemplateLocal)){
			return Constants.MAILTEMPLATELOCAL_NULL_ERROR;
		}
		
		//本地服務器URL
		String webBaseUrl = (String)paramMap.get("webBaseUrl");
		if(StringUtils.isBlank(webBaseUrl)){
			return Constants.WEBBASEURL_NULL_ERROR;
		}
		
		return result;
	}
	
	/**
	 * @describe 检查传入修改视频会议接口参数
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public static String checkUpdateParams(Map paramMap){
		String result = checkParams(paramMap);
		//会议号
		String confKey = (String)paramMap.get("confKey");
		if(StringUtils.isBlank(confKey)){
			return Constants.CONFKEY_NULL_ERROR;
		}
		
		return result;
	}
	
	
	/**
	 * @describe 检查传入删除视频会议接口参数
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public static String checkDeleteParams(Map paramMap){
		String result = checkInfoWareParams(paramMap);
		
		//本地服務器URL
		String webBaseUrl = (String)paramMap.get("webBaseUrl");
		if(StringUtils.isBlank(webBaseUrl)){
			return Constants.WEBBASEURL_NULL_ERROR;
		}
		//会议号
		String confKey = (String)paramMap.get("confKey");
		if(StringUtils.isBlank(confKey)){
			return Constants.CONFKEY_NULL_ERROR;
		}
		
		return result;
	}
	
	/**
	 * @describe 检查开启视频会议接口参数
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public static String checkStartMeetingParams(Map paramMap){
		String result = checkInfoWareParams(paramMap);
		
		//本地服務器URL
		String webBaseUrl = (String)paramMap.get("webBaseUrl");
		if(StringUtils.isBlank(webBaseUrl)){
			return Constants.WEBBASEURL_NULL_ERROR;
		}
		//会议号
		String confKey = (String)paramMap.get("confKey");
		if(StringUtils.isBlank(confKey)){
			return Constants.CONFKEY_NULL_ERROR;
		}
		
		//主持人
		String hostName = (String)paramMap.get("hostName");
		if(StringUtils.isBlank(hostName)){
			return Constants.HOSTNAME_NULL_ERROR;
		}
		
		//主持人显示名称
		String displayName = (String)paramMap.get("displayName");
		if(StringUtils.isBlank(displayName)){
			return Constants.DISPLAYNAME_NULL_ERROR;
		}
		
		//会议密码
		String meetingPwd = (String)paramMap.get("meetingPwd");
		if(StringUtils.isBlank(meetingPwd)){
			return Constants.PASSWD_NULL_ERROR;
		}
		
		//email
		String email = (String)paramMap.get("email");
		if(StringUtils.isBlank(email)){
			return Constants.EMAIL_NULL_ERROR;
		}
		return "PASS";
	}
	
	/**
	 * @describe 检查参加视频会议接口参数
	 * @author radishlee
	 * @since 2011-12-15
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public static String checkJoinMeetingParams(Map paramMap) {
		//本地服務器URL
		String webBaseUrl = (String)paramMap.get("webBaseUrl");
		if(StringUtils.isBlank(webBaseUrl)){
			return Constants.WEBBASEURL_NULL_ERROR;
		}
		//会议号
		String confKey = (String)paramMap.get("confKey");
		if(StringUtils.isBlank(confKey)){
			return Constants.CONFKEY_NULL_ERROR;
		}
		
		//主持人
		String hostName = (String)paramMap.get("attendeeName");
		if(StringUtils.isBlank(hostName)){
			return Constants.ATTENDEES_NULL_ERROR;
		}
		
		//会议密码
		String meetingPwd = (String)paramMap.get("meetingPwd");
		if(StringUtils.isBlank(meetingPwd)){
			return Constants.PASSWD_NULL_ERROR;
		}
		
		//email
		String email = (String)paramMap.get("email");
		if(StringUtils.isBlank(email)){
			return Constants.EMAIL_NULL_ERROR;
		}
		return "PASS";
	}
}
