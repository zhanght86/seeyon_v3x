package com.seeyon.v3x.plan.manager;

import java.util.List;

import com.seeyon.v3x.plan.dao.PlanStyleDao;
import com.seeyon.v3x.plan.domain.PlanStyle;
import com.seeyon.v3x.plan.domain.PlanStyleBody;

public interface PlanStyleManager {
	/**
	 * 添加计划样式
	 * 
	 * @param template
	 *            计划样式对象
	 */
	public abstract void addPlanStyle(PlanStyle planStyle);

	/**
	 * 取回计划样式列表
	 * 
	 * @return 计划样式列表
	 */
	public abstract List listPlanStyle();

	/**
	 * 修改计划样式
	 * 
	 * @param template
	 *            计划样式对象
	 */
	public abstract void updatePlanStyle(PlanStyle planStyle);

	/**
	 * 通过主键取计划样式
	 * 
	 * @param id
	 *            主键
	 * @return 计划样式
	 */
	public abstract PlanStyle getPlanStyleByPk(Long id);
	
	/**
	 * 通过计划类型获取计划样式
	 * 
	 * @param type
	 *            计划类型
	 * @return 
	 */
	public abstract List getPlanStyleByType(String type);
	
	/**
	 * 通过计划类型和单位ID获取计划样式
	 * 
	 * @param type
	 *            计划类型
	 * @param accountId
	 *            单位ID
	 * @return 
	 */
	public abstract List getPlanStyleByTypeAndAccount(String type,Long accountId);

	/**
	 * 通过主键删除计划样式
	 * 
	 * @param id
	 *            主键
	 */
	public abstract void deletePlanStyle(Long id);

	/**
	 * 通过主键数组删除计划样式
	 * 
	 * @param ids
	 *            主键数组
	 */
	public abstract void deletePlanStyles(Long[] ids);

	/**
	 * 得到计划样式全部信息（包括正文）
	 * 
	 * @param id
	 * @return
	 */
	public abstract PlanStyle getPlanStyleAllInfo(Long id);

	public abstract void setPlanStyleDao(PlanStyleDao planStyleDao);

	/**
	 * 添加样式正文
	 * 
	 * @param planStyleBody
	 */
	public abstract void addPlanStyleBody(PlanStyleBody planStyleBody,PlanStyle style);

	/**
	 * 更新样式正文
	 * 
	 * @param planStyleBody
	 */
	public void updatePlanStyleBody(PlanStyleBody planStyleBody);

	/**
	 * 根据样式id删除样式正文
	 * 
	 * @param id
	 *            计划样式id
	 */
	public abstract void deletePlanStyleBodyByPlanStyleId(Long id);
}
