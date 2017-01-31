package com.seeyon.v3x.plugin.dee.manager;

import com.seeyon.v3x.dee.DEEClient;

/**
 * DEEManager，只有DEE插件启用时才能取到实例，在Spring上下文中可通过实例是否为null判断是否启用DEE插件。
 * 
 * @author wangwenyou
 * 
 */
public interface DEEManager {
	/**
	 * 取得DEE客户端。
	 * 
	 * @return DEEClient实例
	 */
	DEEClient getClient();
	/**
	 * 初始化DEE，访问DEE元数据信息前需要先进行客户端的初始化。预留。
	 */
	void init();
}
