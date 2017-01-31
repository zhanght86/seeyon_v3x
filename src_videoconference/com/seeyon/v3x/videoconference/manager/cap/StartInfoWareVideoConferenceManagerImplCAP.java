/**
 * 开启红杉树会议接口实现类
 * @author radishlee
 * @since 2011-12-13
 * @describe 开启视频会议功能性接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.manager.cap;

import java.util.Map;

import com.seeyon.cap.videoconference.manager.StartVideoConferenceManagerCAP;
import com.seeyon.v3x.videoconference.util.Constants;
import com.seeyon.v3x.videoconference.util.InfoWareParamsCheck;
import com.seeyon.v3x.videoconference.ws.StartVideoConferenceManager;


public class StartInfoWareVideoConferenceManagerImplCAP implements
		StartVideoConferenceManagerCAP {
    
	private StartVideoConferenceManager startVideoConferenceManager;
	/**
	 * @describe 开启视频会议功能性接口，调用红杉树产品接口
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoparamMap
	 * @return String类型返回值
	 */
	public String startVideoConferenceCap(Map paramMap) {
		 String result = "";
			
			if(paramMap!=null){
				//paramMap.put("meetingPwd", Constants.MEETING_PWD);//没有加密
				//检查传入参数
				result = InfoWareParamsCheck.checkStartMeetingParams(paramMap);
				//如果检查没通过
				if(!"PASS".equals(result)){
					switch(Integer.parseInt(result.substring(result.length()-5, result.length()))){
						case 1 : return result; //用户名为空异常 
						case 2 : return result; //用户密码为空异常
						case 19: return result; //第三方插件为空异常
						case 21: return result; //第三方插件匹配异常
						case 11: return result; //主持人不为空
						case 20 : return result;//会议号為空異常
						case 13 : return result;//会议密码為空異常
						case 17 : return result;//服務器url為空異常
						case 24 : return result;//email為空異常
					}
				}
			}
			
			return startVideoConferenceManager.startVideoConference(paramMap);
	}

	public void setStartVideoConferenceManager(
			StartVideoConferenceManager startVideoConferenceManager) {
		this.startVideoConferenceManager = startVideoConferenceManager;
	}

}
