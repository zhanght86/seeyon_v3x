/**
 * 视频会议配置类
 * @author radishlee
 * @since 2011-12-19
 * @describe 读取视频会议配置文件的配置信息
 */
package com.seeyon.v3x.plugin.videoconf.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.SystemProperties;

/**
 * 视频会议配置类
 * @author radishlee
 * @since 2011-12-19
 * @describe 读取视频会议配置文件的配置信息
 */
public class VideoConferenceConfig{
	private static Log log = LogFactory.getLog(VideoConferenceConfig.class);
    private static SystemProperties sys = SystemProperties.getInstance();

    public static String WEBBASEURL="";   //视频会议地址
    public static int VIDEO_TIMEOUT= 5000; //连接超时时间
    public static String VIDEOCONFPLUGINID = "videoconf"; //插件id
	public static final long period = 60000; //心跳接口间隔检测时间
	
	public static String VIDEO_CONF_POINT = "";//视频会议服务器会场点数
	public static String VIDEO_CONF_STATUS = "";//视频会议服务器状态标示位
	
	public static boolean MULTIPLE_MASTER_SERVER_ENABLE = false;//多主服务器版本启用标识位

           
    public static void init(){
    	WEBBASEURL = sys.getProperty("videoConference.url").trim();
        VIDEO_TIMEOUT = Integer.parseInt(sys.getProperty("videoConference.timeOut").trim());
        
        if("disable".equals(sys.getProperty("multipleMasterServerEnable").trim())){
            //donothing
        }else if("enable".equals(sys.getProperty("multipleMasterServerEnable").trim())){
        	MULTIPLE_MASTER_SERVER_ENABLE = true;
        }
        log.info("加载视频会议配置成功...   "+WEBBASEURL);
    }
}
