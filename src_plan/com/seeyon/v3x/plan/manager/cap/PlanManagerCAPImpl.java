package com.seeyon.v3x.plan.manager.cap;

import java.util.Date;
import java.util.List;

import com.seeyon.cap.plan.domain.PlanCAP;
import com.seeyon.cap.plan.manager.PlanManagerCAP;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;

public class PlanManagerCAPImpl implements PlanManagerCAP {

	private PlanManager planManager;

	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PlanCAP> findSendPlanForPage(Long userId, Date startTime, Date endTime, boolean isPaginate) throws Exception {
		List<Plan> list = planManager.findSendPlanForPage(userId, startTime, endTime, isPaginate);
		if (list == null) {
			return null;
		}
		return (List<PlanCAP>) BeanUtils.converts(PlanCAP.class, list);
	}

}