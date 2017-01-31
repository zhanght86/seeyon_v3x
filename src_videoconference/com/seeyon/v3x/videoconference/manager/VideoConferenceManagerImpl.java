/**
 * 视频会议manager层
 * @author radishlee
 * @since 2012-1-10
 * @describe 视频会议业务层
 */
package com.seeyon.v3x.videoconference.manager;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.common.timer.TimerHolder;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.principal.NoSuchPrincipalException;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.plugin.videoconf.VideoConferenceSysInit;
import com.seeyon.v3x.plugin.videoconf.util.CheckInfoWareServiceTask;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;

public class VideoConferenceManagerImpl extends QuartzHolder implements VideoConferenceManager {
	 private static Log log = LogFactory.getLog(VideoConferenceManagerImpl.class);
	 private PrincipalManager principalManager;
	/**
	 * 根据用户ID返回加密过的密码
	 * @author radishlee
	 * @since 2012-1-10
	 * @describe 根据用户ID返回加密过的密码
	 * @param Long userid
	 * @return String password
	 * @throws SQLException 
	 * @throws NoSuchPrincipalException 
	 */
	public String getEncryptedPassWD(Long userID) throws SQLException, NoSuchPrincipalException {
		return principalManager.getPassword(userID);
	}
	

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	
	private void init(){
		CacheAccessable factory = CacheFactory.getInstance(VideoConferenceSysInit.class);
		CacheMap<String,LinkedList<Map<String,String>>> meetingServerListcache = factory.createMap("meetingServerList");
		
		//开机启动 检测视频会议系统是否启动 心跳接口 
		//spring bean的加载顺序导致注入【checkInfoWareServiceTask】注入不进来只能依赖查找
		TimerHolder.newTimer((CheckInfoWareServiceTask)ApplicationContextHolder.getBean("checkInfoWareServiceTask"), VideoConferenceConfig.period);
		log.info("注册视频会议心跳接口");
	}

}
