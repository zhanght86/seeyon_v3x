package com.seeyon.v3x.plan.manager;

import java.util.List;

import com.seeyon.v3x.plan.dao.PlanUserScopeDao;
import com.seeyon.v3x.plan.domain.PlanUserScope;

public class PlanUserScopeManagerImpl implements PlanUserScopeManager {

	private PlanUserScopeDao planUserScopeDao;

	/**
	 * @return 返回 templateDao。
	 */
	public PlanUserScopeDao getPlanUserScopeDao() {
		return planUserScopeDao;
	}

	public void setPlanUserScopeDao(PlanUserScopeDao planUserScopeDao) {
		this.planUserScopeDao = planUserScopeDao;
	}

	public void addPlanUserScope(PlanUserScope planUserScope) {

		PlanUserScope tempPus = getPlanUserScopeDao().findByRefUserIdAndLoginAccount(
				planUserScope.getRefUserId());
		if (tempPus == null) {
			planUserScope.setIdIfNew();
			getPlanUserScopeDao().save(planUserScope);
		} else {
			tempPus.setIsSeeDetail(planUserScope.getIsSeeDetail());
			tempPus.setRefUserId(planUserScope.getRefUserId());
			tempPus.setRefUserName(planUserScope.getRefUserName());
			tempPus.setScopeUserIds(planUserScope.getScopeUserIds());
			tempPus.setScopeUserNames(planUserScope.getScopeUserNames());
			getPlanUserScopeDao().update(tempPus);
		}
	}

	public void deletePlanUserScope(Long id) {
		getPlanUserScopeDao().delete(id);
	}

	public void deletePlanUserScopes(Long[] ids) {
		getPlanUserScopeDao().delete(ids);
	}

	public PlanUserScope getPlanUserScopeByPk(Long id) {
		return getPlanUserScopeDao().findByPrimaryKey(id);
	}

	public List listPlanUserScope() {
		return getPlanUserScopeDao().listUserScopeByPage();
	}

	public void updatePlanUserScope(PlanUserScope planUserScope) {
		getPlanUserScopeDao().update(planUserScope);
	}
	
	public PlanUserScope getPlanUserScopeByRefUser(Long refUserId){
		return getPlanUserScopeDao().findByRefUserId(refUserId);
	}


}
