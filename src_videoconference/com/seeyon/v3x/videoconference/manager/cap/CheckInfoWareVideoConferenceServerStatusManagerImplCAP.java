/**
 * 视频会议状态检测类
 * @author radishlee
 * @since 2011-12-9
 * @describe 视频会议状态检测接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.manager.cap;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.videoconference.manager.CheckVideoConferenceServerStatusManagerCAP;
import com.seeyon.v3x.videoconference.ws.CheckVideoConferenceServerStatusManager;

public class CheckInfoWareVideoConferenceServerStatusManagerImplCAP implements
             CheckVideoConferenceServerStatusManagerCAP {
	private static Log log = LogFactory.getLog(CheckInfoWareVideoConferenceServerStatusManagerImplCAP.class);
	private CheckVideoConferenceServerStatusManager checkVideoConferenceServerStatusManager;
	/**
	 * @describe 视频会议状态检测接口，调用功能性接口
	 * @author radishlee
	 * @since 2011-12-9
	 * @param paramMap
	 * @return String类型返回值
	 */
	public String checkVideoConferenceServerStatusCap(Map paramMap) {
		if(paramMap==null||paramMap.get("timeOut")==null||paramMap.get("port")==null||paramMap.get("host")==null||"".equals(paramMap.get("timeOut"))
				||"".equals(paramMap.get("host"))||"".equals(paramMap.get("port"))){
			log.error("视频会议状态检测接口实现类【CheckInfoWareVideoConferenceServerStatusManagerImplCAP】传入方法paramMap参数错误！");
			return "false";
		}
		return checkVideoConferenceServerStatusManager.checkVideoConferenceServerStatus(paramMap);
	}
	
	
	public void setCheckVideoConferenceServerStatusManager(
			CheckVideoConferenceServerStatusManager checkVideoConferenceServerStatusManager) {
		this.checkVideoConferenceServerStatusManager = checkVideoConferenceServerStatusManager;
	}
	
	

}
