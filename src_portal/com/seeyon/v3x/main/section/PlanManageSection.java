package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.PlanSectionUtil;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;

/**
 * 计划管理栏目
 */
public class PlanManageSection extends BaseSection {

	private static final Log log = LogFactory.getLog(PlanManageSection.class);

	private final int[] width = { 44, 16, 10, 10, 10, 10 }; // 宽度百分比

	private PlanManager planManager;

	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "planManageSection";
	}
	
	@Override
	public String getBaseName() {
		return "planManage";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("planManage", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		int count = SectionUtils.getSectionCount(8, preference);
		String[] resultValue = SectionUtils.getResultValue("1,2,3", preference);
		
		User user = CurrentUser.get();
		List<Plan> planList = null;
		try {
			planList = planManager.getHomePlanManagePlanList(user.getId(), resultValue);
			if (planList != null && planList.size() > count) {
				planList = planList.subList(0, count);
			}
		} catch(Exception e) {
			log.error("", e);
		}
		
		return PlanSectionUtil.setPlanSectionData(preference, width, planList, false, null);
	}

}