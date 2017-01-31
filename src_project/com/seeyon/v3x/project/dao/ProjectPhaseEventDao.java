/**
 * 
 */
package com.seeyon.v3x.project.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;

public class ProjectPhaseEventDao extends BaseHibernateDao<ProjectPhaseEvent> {

	/**
	 * 查询项目阶段下所有事件记录列表
	 * @param phaseId 项目阶段ID
	 * @return
	 * @throws Exception
	 */
	public List<ProjectPhaseEvent> getAllPhaseEventList(Long phaseId) throws Exception {
		String hql = "from " + ProjectPhaseEvent.class.getName() + " as pe where pe.phaseId=?";
		return this.find(hql, phaseId);
	}

}
