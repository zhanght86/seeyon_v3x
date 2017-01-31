/**
 * 视频会议状态检测类
 * @author radishlee
 * @since 2011-12-9
 * @describe 视频会议状态检测接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

public interface CheckVideoConferenceServerStatusManager {

	/**
	 * @describe 视频会议状态检测接口，调用红杉树产品接口
	 * @author radishlee
	 * @since 2011-12-9
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String checkVideoConferenceServerStatus(Map paramMap);
}
