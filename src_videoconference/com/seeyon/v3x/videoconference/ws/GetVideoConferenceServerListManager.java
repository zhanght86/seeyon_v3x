/**
 * 获取视频会议服务器信息列表接口
 * @author radishlee
 * @since 2012-4-10
 * @describe 获取视频会议服务器信息列表接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

public interface GetVideoConferenceServerListManager {
	/**
	 * 获取视频会议服务器信息列表接口
     * @author radishlee
     * @since 2012-4-10
	 * @describe 获取视频会议服务器信息列表接口，调用第三方产品接口
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String getVideoConferenceServerList(Map videoParamMap);
}
