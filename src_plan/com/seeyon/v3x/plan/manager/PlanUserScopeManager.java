package com.seeyon.v3x.plan.manager;

import java.util.List;

import com.seeyon.v3x.plan.dao.PlanUserScopeDao;
import com.seeyon.v3x.plan.domain.PlanUserScope;

public interface PlanUserScopeManager {
	/**
	 * 添加计划用户查看范围
	 * 
	 * @param template
	 *            计划用户查看范围对象
	 */
	public abstract void addPlanUserScope(PlanUserScope planUserScope);

	/**
	 * 取回计划用户查看范围列表
	 * 
	 * @return 计划用户查看范围列表
	 */
	public abstract List listPlanUserScope();

	/**
	 * 修改计划用户查看范围
	 * 
	 * @param template
	 *            计划用户查看范围对象
	 */
	public abstract void updatePlanUserScope(PlanUserScope planUserScope);

	/**
	 * 通过主键取计划用户查看范围
	 * 
	 * @param id
	 *            主键
	 * @return 计划用户查看范围
	 */
	public abstract PlanUserScope getPlanUserScopeByPk(Long id);

	/**
	 * 通过主键删除计划用户查看范围
	 * 
	 * @param id
	 *            主键
	 */
	public abstract void deletePlanUserScope(Long id);

	/**
	 * 通过主键数组删除计划用户查看范围
	 * 
	 * @param ids
	 *            主键数组
	 */
	public abstract void deletePlanUserScopes(Long[] ids);

	public abstract void setPlanUserScopeDao(PlanUserScopeDao planUserScopeDao);
	
	/**
	 * 通过用户Id得到用户查看范围
	 * 
	 * @param refUserId
	 *            用户Id
	 */
	public abstract PlanUserScope getPlanUserScopeByRefUser(Long refUserId);
}
