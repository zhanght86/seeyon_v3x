/**
 * $Id: SendSalaryTask.java,v 1.5 2011/01/22 03:02:33 tanmf Exp $
 * Copyright 2000-2007 北京用友致远软件开发有限公司.
 * All rights reserved.
 *
 *     http://www.seeyon.com
 *
 * SendSalaryTask.java created by paul at 2007-9-13 下午03:09:37
 *
 */
package com.seeyon.v3x.hr.util;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.collaboration.domain.FlowData;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.task.ExecutableTask;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.hr.webmodel.SalaryParam;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * <tt>SendSalaryTask</tt>实现<tt>ExecutableTask</tt>接口，转发一条工资信息到协同
 * 
 * @author paul
 * 
 */
public class SendSalaryTask implements ExecutableTask {
	/*
	 * 异步任务：转发协同
	 * 
	 * @see com.seeyon.v3x.common.task.ExecutableTask#run(java.lang.Object)
	 */
	public boolean run(Object parameter) {
		// 取得colManager
		@SuppressWarnings("unused")
		ColManager colManager = (ColManager) ApplicationContextHolder
				.getBean("colManager");

		if (null == parameter)
			return false;
		if (parameter instanceof SalaryParam) {

		}

		return false;
	}

	// 生成FlowData
	@SuppressWarnings("unused")
	private FlowData getFlowData(SalaryParam param) {
		// 取得OrgManager
		OrgManager orgManager = (OrgManager) ApplicationContextHolder
				.getBean("OrgManager");
		
		List<Long> memberIds = new ArrayList<Long>();
		memberIds.add(param.getReceiverId());
		return FlowData.flowdataFromMemberIds(param.getSender().getId(), FlowData.FLOWTYPE_SERIAL, memberIds, orgManager);
	}

}
