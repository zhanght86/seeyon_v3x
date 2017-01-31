package com.seeyon.v3x.plugin.dee.manager;

import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.dee.DEEClient;
import com.seeyon.v3x.dee.DEEInitializer;

public class DEEManagerImpl implements DEEManager {
	private DEEClient client;

	@Override
	public DEEClient getClient() {
		init();
		return client;
	}

	private synchronized void initClient() {
		String[] arr = { "dee.meta.datasource.driver",
				"dee.meta.datasource.url", "dee.meta.datasource.userName",
				"dee.meta.datasource.password" };

		// 设置元数据数据库连接信息
		// 以A8配置的为准，如果配置工具独立启动，以系统环境变量为准
		for (String name : arr) {
			String value = SystemProperties.getInstance().getProperty(name);
			if(value==null) continue;
			System.setProperty(name, value);
		}	
		SystemProperties prop = SystemProperties.getInstance();
		boolean isRemote = "true".equals(prop.getProperty("dee.remote"));
		if (isRemote) {
			String url = prop.getProperty("dee.remote.url");
			String userName = prop.getProperty("dee.remote.userName");
			String pwd = prop.getProperty("dee.remote.password");
			client = new DEEClient(url, userName, pwd);
		} else {
			client = new DEEClient();
		}
	}

	@Override
	public void init() {
		if (client == null) {
			initClient();
		}
		DEEInitializer.getInstance().init();
	}
}
