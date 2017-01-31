/**
 * 创建视频会议接口
 * @author radishlee
 * @since 2011-12-9
 * @describe 创建视频会议功能性接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

public interface CreateVideoConferenceManager {
	
	/**
	 * 创建视频会议接口
	 * @author radishlee
	 * @since 2011-12-9
	 * @describe 创建视频会议功能性接口，调用第三方接口
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String createVideoConference(Map videoParamMap);
}
