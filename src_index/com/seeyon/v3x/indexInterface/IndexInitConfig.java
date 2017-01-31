package com.seeyon.v3x.indexInterface;


import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.index.IndexPropertiesUtil;
import com.seeyon.v3x.common.SystemEnvironment;

public class IndexInitConfig {
	private static final Log log = LogFactory.getLog(IndexInitConfig.class);
    private static boolean hasPlugin=SystemEnvironment.hasPlugin("luceneIndex");
	/**
	 * 是否含有index插件
	 * 
	 * @return
	 */
	public static boolean hasLuncenePlugIn() {
		return hasPlugin;
	}
	
	/**
	 * 是否是全文检索远程模式
	 * 
	 * @return 是否是全文检索远程模式
	 */
	public static boolean isRemoteIndex() {
		return "remote".equals(IndexPropertiesUtil.getInstance().getProperties("modelName"));
	}

	/**
	 * 根据prop得到形如 “rmi://ip：端口/服务名” 的字符串
	 * 
	 * @param prop
	 * @return
	 */
	public static String getIndexAddress(Properties prop) {
		return "rmi://" + IndexPropertiesUtil.getInstance().getProperties("indexIp") + ":"
				+ IndexPropertiesUtil.getInstance().getProperties("indexPort") + "/"
				+ IndexPropertiesUtil.getInstance().getProperties("indexServiceName");
	}
	/**
	 * 得到全文检索配置参数文件
	 * @return
	 */
	public static Properties getProp() {
		return IndexPropertiesUtil.getInstance().readProperties();
	}






  
}
