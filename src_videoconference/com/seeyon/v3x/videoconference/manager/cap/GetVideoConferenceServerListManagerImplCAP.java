/**
 * 获取视频会议服务器信息列表
 * @author radishlee
 * @since 2012-4-10
 * @describe 获取视频会议服务器信息列表
 * 
 */
package com.seeyon.v3x.videoconference.manager.cap;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.videoconference.manager.GetVideoConferenceServerListManagerCAP;
import com.seeyon.v3x.organization.principal.NoSuchPrincipalException;
import com.seeyon.v3x.plugin.videoconf.util.Constants;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.videoconference.util.InfoWareParamsCheck;
import com.seeyon.v3x.videoconference.ws.GetVideoConferenceServerListManager;

public class GetVideoConferenceServerListManagerImplCAP implements GetVideoConferenceServerListManagerCAP {
	private static Log log = LogFactory.getLog(GetVideoConferenceServerListManagerImplCAP.class);
	private GetVideoConferenceServerListManager getVideoConferenceServerListManager;
    
    /**
	 * 获取视频会议服务器信息列表
	 * @author radishlee
	 * @since 2012-4-10
	 * @describe 获取视频会议服务器信息列表
	 * @param videoParamMap
	 * @return String类型返回值
     * @throws SQLException 
     * @throws NoSuchPrincipalException 
	 */
	public String getVideoConferenceServerList() {
		String result = "";
		 
	    Map paramMap = new HashMap();
	    paramMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
	    paramMap.put("userName", Constants.SYN_USER_NAME);
		paramMap.put("password",  Constants.SYN_PASSWORD);
		//检查传入参数
		result = InfoWareParamsCheck.checkInfoWareParams(paramMap);
		//如果检查没通过
		if(!"PASS".equals(result)){
			switch(Integer.parseInt(result.substring(result.length()-5, result.length()))){
				case 1 : return result; //用户名为空异常 
				case 2 : return result; //用户密码为空异常
			}
		}
		
		return getVideoConferenceServerListManager.getVideoConferenceServerList(paramMap);
	}

	
	
	
	public void setGetVideoConferenceServerListManager(GetVideoConferenceServerListManager getVideoConferenceServerListManager) {
		this.getVideoConferenceServerListManager = getVideoConferenceServerListManager;
	}
}
