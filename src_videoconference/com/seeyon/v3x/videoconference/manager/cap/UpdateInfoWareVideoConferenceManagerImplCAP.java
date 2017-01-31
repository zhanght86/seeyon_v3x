/**
 * 修改红杉树会议接口实现类
 * @author radishlee
 * @since 2011-12-13
 * @describe 修改视频会议功能性接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.manager.cap;

import java.util.Map;

import com.seeyon.cap.videoconference.manager.UpdateVideoConferenceManagerCAP;
import com.seeyon.v3x.videoconference.util.Constants;
import com.seeyon.v3x.videoconference.util.InfoWareParamsCheck;
import com.seeyon.v3x.videoconference.ws.UpdateVideoConferenceManager;


public class UpdateInfoWareVideoConferenceManagerImplCAP implements
		UpdateVideoConferenceManagerCAP {
    
	private UpdateVideoConferenceManager updateVideoConferenceManager;
	/**
	 * @describe 修改视频会议功能性接口，调用红杉树产品接口
	 * @author radishlee
	 * @since 2011-12-13
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String updateVideoConferenceCap(Map paramMap) {
        String result = "";
		
		if(paramMap!=null){
			//公用参数
			paramMap.put("timeZoneId", "45");
			//paramMap.put("openType", Constants.MEETING_OPEN_TYPE);
			//paramMap.put("passwd", Constants.MEETING_PWD);//没有加密
			paramMap.put("conferencePattern", Constants.CONFERENCE_PATTERN);
			paramMap.put("beforehandTime", Constants.BEFORE_HAND_TIME);
			paramMap.put("mailTemplateLocal", "zh_CN");
			//检查传入参数
			result =  InfoWareParamsCheck.checkUpdateParams(paramMap);
			//如果检查没通过
			if(!"PASS".equals(result)){
				switch(Integer.parseInt(result.substring(result.length()-5, result.length()))){
					case 1 : return result; //用户名为空异常 
					case 2 : return result; //用户密码为空异常
					case 3 : return result; //会议主题为空异常 
					case 4 : return result; //开始时间为空异常
					case 6 : return result; //结束时间为空异常
					case 8 : return result; //时区为空异常
					case 9 : return result; //会产点数为空异常
					case 11 : return result; //主持人为空异常
					case 10 : return result; //创建人为空异常
					case 17 : return result;//服務器url為空異常
					case 18: return result; //与会人员為空異常
					case 22 : return result; //与会人员姓名为空异常
					case 19: return result; //第三方插件为空异常
					case 21: return result; //第三方插件匹配异常
					case 20: return result; //会议号为空异常
				}
			}
		}
		

		
		return updateVideoConferenceManager.updateVideoConference(paramMap);
	}

	public void setUpdateVideoConferenceManager(
			UpdateVideoConferenceManager updateVideoConferenceManager) {
		this.updateVideoConferenceManager = updateVideoConferenceManager;
	}

}
