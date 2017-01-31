/**
 * 
 * 
 * @describe 插件定义类
 * @author radish lee
 * @version v1.0
 * @since 2011-12-11
 * 
 */
package com.seeyon.v3x.plugin.videoconf;

import javax.servlet.ServletContext;

import com.seeyon.v3x.plugin.PluginDefintion;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.util.Strings;

public class VideoConferencePluginDefintion extends PluginDefintion {

	/**
	 * 
	 * @describe 返回插件Id
	 * @author radish lee
	 * @since 2011-12-11
	 * 
	 */
	public String getId() {
		return VideoConferenceConfig.VIDEOCONFPLUGINID;
	}

	/**
	 * 
	 * @describe 插件是否启用
	 * @author radish lee
	 * @since 2011-12-11
	 * 
	 */
	public boolean isAllowStartup(ServletContext servletContext) {
		return Strings.isNotBlank(this.getPluginProperty("videoConference.url"));
	}

}
