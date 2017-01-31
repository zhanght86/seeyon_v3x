package com.seeyon.v3x.workflow.listener;

import javax.servlet.ServletContextEvent;

import com.seeyon.v3x.common.SystemInitialitionInterface;

import net.joinwork.bpm.engine.exception.BPMException;
import net.joinwork.bpm.engine.wapi.WAPIFactory;

public class WorkflowInitListener  implements SystemInitialitionInterface {

	public void destroyed(ServletContextEvent arg0) {

	}

	public void initialized(ServletContextEvent arg0) {
		try {
			WAPIFactory.initEngine(WAPIFactory.INIT_IF_NOT_INIT);
		} catch (BPMException e) {
			e.printStackTrace();
		}
	}

}
