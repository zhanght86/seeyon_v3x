/**
 * 参加视频会议接口实现
 * @author radishlee
 * @since 2011-12-15
 * @describe 开启视频会议功能性接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.manager.cap;

import java.util.Map;

import com.seeyon.cap.videoconference.manager.JoinVideoConferenceManagerCAP;
import com.seeyon.v3x.videoconference.util.Constants;
import com.seeyon.v3x.videoconference.util.InfoWareParamsCheck;
import com.seeyon.v3x.videoconference.ws.JoinVideoConferenceManager;


public class JoinInfoWareVideoConferenceManagerImplCAP implements
 JoinVideoConferenceManagerCAP {

	private JoinVideoConferenceManager joinVideoConferenceManager;
	/**
	 * 参加红杉树会议接口实现
	 * @author radishlee
	 * @since 2011-12-15
	 * @describe 参加视频会议功能性接口，调用红杉树产品接口
	 * @param videoparamMap
	 * @return String类型返回值
	 */
	public String joinVideoConferenceCap(Map paramMap){
		 String result = "";
			
			if(paramMap!=null){
				//paramMap.put("meetingPwd", Constants.MEETING_PWD);//没有加密
				//检查传入参数
				result = InfoWareParamsCheck.checkJoinMeetingParams(paramMap);
				//如果检查没通过
				if(!"PASS".equals(result)){
					switch(Integer.parseInt(result.substring(result.length()-5, result.length()))){
						case 1 : return result; //用户名为空异常 
						case 2 : return result; //用户密码为空异常
						case 19: return result; //第三方插件为空异常
						case 21: return result; //第三方插件匹配异常
						case 18: return result; //与会人员为空异常
						case 20 : return result;//会议号為空異常
						case 13 : return result;//会议密码為空異常
						case 17 : return result;//服務器url為空異常
						case 24 : return result;//email為空異常
					}
				}
			}
			
			return joinVideoConferenceManager.joinVideoConference(paramMap);
	}
	public void setJoinVideoConferenceManager(
			JoinVideoConferenceManager joinVideoConferenceManager) {
		this.joinVideoConferenceManager = joinVideoConferenceManager;
	}

}
