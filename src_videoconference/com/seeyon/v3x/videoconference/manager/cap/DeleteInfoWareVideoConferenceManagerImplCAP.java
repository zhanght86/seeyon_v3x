/**
 * 删除红杉树会议接口实现类
 * @author radishlee
 * @since 2011-12-13
 * @describe 删除视频会议功能性接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.manager.cap;

import java.util.Map;

import com.seeyon.cap.videoconference.manager.DeleteVideoConferenceManagerCAP;
import com.seeyon.v3x.videoconference.util.InfoWareParamsCheck;
import com.seeyon.v3x.videoconference.ws.DeleteVideoConferenceManager;



public class DeleteInfoWareVideoConferenceManagerImplCAP implements
		DeleteVideoConferenceManagerCAP {

	private DeleteVideoConferenceManager deleteVideoConferenceManager;
	/**
	 * @describe 删除视频会议功能性接口，调用红杉树产品接口
	 * @author radishlee
	 * @since 2011-12-13
	 * @param paramMap
	 * @return String类型返回值
	 */
	public String deleteVideoConferenceCap(Map paramMap) {
        String result = "";
		
		if(paramMap!=null){
			//检查传入参数
			result = InfoWareParamsCheck.checkDeleteParams(paramMap);
			//如果检查没通过
			if(!"PASS".equals(result)){
				switch(Integer.parseInt(result.substring(result.length()-5, result.length()))){
					case 1 : return result; //用户名为空异常 
					case 2 : return result; //用户密码为空异常
					case 17 : return result;//服務器url為空異常
					case 20 : return result;//会议号為空異常
					case 19: return result; //第三方插件为空异常
					case 21: return result; //第三方插件匹配异常
				}
			}
		}
		
		return deleteVideoConferenceManager.deleteVideoConference(paramMap);
	}

	public void setDeleteVideoConferenceManager(
			DeleteVideoConferenceManager deleteVideoConferenceManager) {
		this.deleteVideoConferenceManager = deleteVideoConferenceManager;
	}

}
