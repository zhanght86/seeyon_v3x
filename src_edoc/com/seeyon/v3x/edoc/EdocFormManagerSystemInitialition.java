/**
 * 
 */
package com.seeyon.v3x.edoc;

import javax.servlet.ServletContextEvent;

import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.manager.EdocFormManager;

/**
 * @author Administrator
 *
 */
public class EdocFormManagerSystemInitialition implements
		SystemInitialitionInterface {

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.common.SystemInitialitionInterface#destroyed(javax.servlet.ServletContextEvent)
	 */
	public void destroyed(ServletContextEvent arg0) {

	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.common.SystemInitialitionInterface#initialized(javax.servlet.ServletContextEvent)
	 */
	public void initialized(ServletContextEvent arg0) {
		EdocFormManager edocFormManager = (EdocFormManager)ApplicationContextHolder.getBean("edocFormManager");
		edocFormManager.initialize();
	}

}
