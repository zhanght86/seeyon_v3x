/**
 * 参加视频会议接口
 * @author radishlee
 * @since 2011-12-15
 * @describe 开启视频会议功能性接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

public interface JoinVideoConferenceManager {
	/**
	 * 参加视频会议接口
	 * @author radishlee
	 * @since 2011-12-15
	 * @describe 参加视频会议功能性接口，调用第三方产品接口
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String joinVideoConference(Map videoParamMap);
}
