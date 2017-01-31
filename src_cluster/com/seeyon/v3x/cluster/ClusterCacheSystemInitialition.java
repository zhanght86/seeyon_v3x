package com.seeyon.v3x.cluster;

import java.util.ArrayList;

import javax.servlet.ServletContextEvent;

import www.seeyon.com.v3x.form.base.systemvalue.UserFlowId;

import com.seeyon.oainterface.longpolling.serverFactory;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.authenticate.LockLoginInfoFactory;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.isearch.manager.ISearchManagerRegister;
import com.seeyon.v3x.main.MainDataLoader;

/**
 * 集群缓存初始化。<br/>
 * 服务启动时进行集群缓存监听器的注册等操作。<br/>
 * 集群时需要在/webapps/seeyon/WEB-INF/classes/conf/SystemInitialization.properties中声明
 * 。
 * 
 * 
 * @author wangwy
 * 
 */
public class ClusterCacheSystemInitialition implements
		SystemInitialitionInterface {
	private final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(ClusterCacheSystemInitialition.class);

	public void destroyed(ServletContextEvent arg0) {

	}

	public void initialized(ServletContextEvent arg0) {
		if (NotificationManager.getInstance().isEnabled()) {
			// 初始化监听器 初始化通知
			NotificationListenerRegistry registry = (NotificationListenerRegistry) ApplicationContextHolder
					.getBean("notificationListenerRegistry");
			registry.init();
			
			try {
				initSingleton();
			} catch (Throwable e) {
				log.error(e.getLocalizedMessage(), e);
			}
			
			// 启用缓存组件的集群同步通知
			CacheFactory.setClusterEnabled(true);
		}

	}

	// 不受Spring管理的Singleton，其缓存只在调用时创建，为避免NoSuchCacheException，显式调用触发缓存创建
	private void initSingleton() {
		MainDataLoader.getInstance();
		LockLoginInfoFactory.getInstance();
		serverFactory.getServer();
		ISearchManagerRegister.getAppObjByAppKey("");
	}
}
