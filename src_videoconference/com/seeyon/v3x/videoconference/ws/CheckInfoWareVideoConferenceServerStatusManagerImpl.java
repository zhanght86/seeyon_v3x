/**
 * 视频会议状态检测类
 * @author radishlee
 * @since 2011-12-9
 * @describe 视频会议状态检测接口，调用红杉树产品接口
 */
package com.seeyon.v3x.videoconference.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.videoconference.manager.GetVideoConferenceServerListManagerCAP;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.plugin.videoconf.VideoConferenceSysInit;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.videoconference.util.Constants;
import com.seeyon.v3x.videoconference.util.ParseXML;

public class CheckInfoWareVideoConferenceServerStatusManagerImpl implements CheckVideoConferenceServerStatusManager {
	private static Log log = LogFactory.getLog(CheckInfoWareVideoConferenceServerStatusManagerImpl.class);
 
	/**
	 * @describe 视频会议状态检测接口，调用红杉树产品接口
	 * @author radishlee
	 * @since 2011-12-9
	 * @param ParamMap 为以后扩展使用
	 * @return String类型返回值
	 */
	public String checkVideoConferenceServerStatus(Map paramMap) {
		StringBuffer lines = new StringBuffer();
        String videoConfStatusUrl = paramMap.get("baseUrl")+"/meeting/dogInfoServlet";
        try {
			URL url = new URL(videoConfStatusUrl);
			HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
			urlCon.setRequestMethod("POST");
			urlCon.setDoOutput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
			urlCon.setConnectTimeout(Integer.parseInt((String) paramMap.get("timeOut")));
			
			String line ;
			BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));

			while((line=br.readLine())!=null){
			  lines.append(line);
			}
		    br.close();
		    log.debug("视频会议心跳检测：serverIP="+VideoConferenceConfig.WEBBASEURL);
		} catch (MalformedURLException e) {
			log.error("检测视频会议系统状态，连接失败,url错误:   "+e);
			return "false";
		} catch (IOException e) {
			log.error("检测视频会议系统状态，连接失败:  "+e);
			return "false";
		}finally{
			 /****
		      * 通知其他插件视频会议不能用
		      * videoConfStatus状态：
		      *                   disable无此插件
		      *                   error 发生异常
		      *                   enable 正常使用
		      * 
		      */
			VideoConferenceConfig.VIDEO_CONF_STATUS = "disable";
		}
		
		
		//如果连接不上。返回空 
		if(lines==null||lines.equals("")){
			if(VideoConferenceConfig.VIDEO_CONF_STATUS==""){
			     /****
			      * 通知其他插件视频会议不能用
			      * videoConfStatus状态：
			      *                   disable无此插件
			      *                   error 发生异常
			      *                   enable 正常使用
			      * 
			      */
				VideoConferenceConfig.VIDEO_CONF_STATUS = "disable";
				log.error("连接视频会议系统，连接失败!");
			}
			return "false";
		}else{
			Map videoConfDogInfMap = ParseXML.parseXML(lines.toString());
			//返回信息格式不对
			if(videoConfDogInfMap!=null){
				//设置视频会议状态可用
				VideoConferenceConfig.VIDEO_CONF_STATUS = "enable";
				//取得会场点数set到VideoConferenceConf里
				VideoConferenceConfig.VIDEO_CONF_POINT = (String)videoConfDogInfMap.get("interactiveLicenseNumber");
				//如果同级多服务器模式支持的话,修改标志位(multilevelServer参数 1表示开启 0表示未开启)
				if(StringUtils.isNotBlank((String)videoConfDogInfMap.get("multilevelServer"))){
					if("1".equals((String)videoConfDogInfMap.get("multilevelServer"))){
						VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE = true;
						
						CacheAccessable factory = CacheFactory.getInstance(VideoConferenceSysInit.class);
						CacheMap<String,LinkedList<Map<String,String>>> meetingServerListcache = factory.getMap("meetingServerList");
                        //如果缓存里没有数据 就给缓存添加结果数据。如果有就不做了
						if(meetingServerListcache.get("meetingServerList")==null){
							GetVideoConferenceServerListManagerCAP getServerListManager = (GetVideoConferenceServerListManagerCAP)ApplicationContextHolder.getBean("getVideoConferenceServerListManagerCAP");
							String result = getServerListManager.getVideoConferenceServerList();
							if(!StringUtils.contains(result,"SUCCESS")){
					     		log.error("读取服务器列表信息错误！"+result);
					     	}else{
					     		LinkedList<Map<String,String>> resultList = (LinkedList<Map<String,String>>)ParseXML.parseXML4GetMeetingListMsg(result);
					     		meetingServerListcache.put("meetingServerList", resultList);
					     	}	
						}
					}else if("0".equals((String)videoConfDogInfMap.get("multilevelServer"))){
						VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE = false;
					}
		        }else{
		        	VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE = false;
		        }
				log.debug("心跳接口状态检测正常。。。。");
				return "true";
			}else{
				new Exception(Constants.XML_NULL_ERROR);
				log.error(Constants.XML_NULL_ERROR);
				return "false";
			}
		}
	}

}
