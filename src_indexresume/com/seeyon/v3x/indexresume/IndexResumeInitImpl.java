package com.seeyon.v3x.indexresume;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.indexresume.manager.IndexResumeTaskManager;

/**
 * @author zhangyong
 *
 */
public class IndexResumeInitImpl implements SystemInitialitionInterface {
	private final static Log logger = LogFactory.getLog(IndexResumeInitImpl.class);
	public void initialized(ServletContextEvent arg0) {
			IndexResumeTaskManager scheduler = (IndexResumeTaskManager)ApplicationContextHolder.getBean("indexResumeTaskManager");
			scheduler.init();
	}

	public void destroyed(ServletContextEvent arg0) {

	}

}
