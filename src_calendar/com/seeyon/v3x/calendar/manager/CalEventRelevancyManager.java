package com.seeyon.v3x.calendar.manager;

import com.seeyon.v3x.calendar.domain.CalEventRelevancy;

/**
 * 对日程事件的关联应用进行操作
 * 
 * @author javaKuang
 * 
 */
public interface CalEventRelevancyManager {
	// -----------------------------------------------------------基本操作
	/**
	 * 保存关联
	 * 
	 * @param event
	 * @return
	 */
	public CalEventRelevancy save(CalEventRelevancy calEventRelevancy);

	/**
	 * 保存关联
	 * 
	 * @param calEventRelevancy
	 * @param isNew
	 * @return
	 */
	public CalEventRelevancy save(CalEventRelevancy calEventRelevancy,
			boolean isNew);

	/**
	 * 根据主键获取关联
	 * 
	 * @param id
	 * @return
	 */
	public CalEventRelevancy getCalEventRelevancyById(Long id);

	/**
	 * 根据主键删除关联
	 * 
	 * @param id
	 */
	public void deleteById(Long id);

	// -----------------------------------------------------------扩展操作
	/**
	 * 根据关联应用的ID删除映射关系 以及 对应的日程事件
	 */
	public void deleteByRelevancyId(Long id);

}
