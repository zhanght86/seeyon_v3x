/**
 * 
 */
package com.seeyon.v3x.project.dao;

import java.util.Collection;
import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.project.domain.ProjectPhase;

public class ProjectPhaseDao extends BaseHibernateDao<ProjectPhase> {

	@SuppressWarnings("unchecked")
	public List<ProjectPhase> getProjectPhases(Collection<Long> phaseIds) {
		String hql = "from " + ProjectPhase.class.getCanonicalName() + " where id in (:phaseIds) order by phaseBegintime asc";
		return this.find(hql, -1, -1, FormBizConfigUtils.newHashMap("phaseIds", phaseIds));
	}
	
}
