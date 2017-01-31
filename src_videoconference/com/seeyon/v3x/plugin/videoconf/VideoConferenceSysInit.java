package com.seeyon.v3x.plugin.videoconf;



import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;


/**
 * @author radish lee
 * @version 2011-12-19
 */
public class VideoConferenceSysInit implements SystemInitialitionInterface {
	private static Log log = LogFactory.getLog(VideoConferenceSysInit.class);
	
	public void destroyed(ServletContextEvent arg0) {
	}

	public void initialized(ServletContextEvent arg0) {
		VideoConferenceConfig.init();//加载会议配置
	}
}
