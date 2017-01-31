/**
 * 删除视频会议接口
 * @author radishlee
 * @since 2011-12-13
 * @describe 修改视频会议功能性接口，调用第三方产品接口
 * 
 */
package com.seeyon.v3x.videoconference.ws;

import java.util.Map;

public interface DeleteVideoConferenceManager {
	/**
	 * 删除视频会议接口
	 * @author radishlee
	 * @since 2011-12-13
	 * @describe 删除视频会议功能性接口，调用第三方产品接口
	 * @param videoParamMap
	 * @return String类型返回值
	 */
	public String deleteVideoConference(Map videoParamMap);
}
