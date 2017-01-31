package com.seeyon.v3x.edoc.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocFormFlowPermBound;

public class EdocFormFlowPermBoundDao extends BaseHibernateDao<EdocFormFlowPermBound>{

	
	public void deleteFormFlowPermBoundByFormId(Long formId)
	{
		String hsql = "delete from EdocFormFlowPermBound as a where a.edocFormId = ? ";
		Object[] values = new Object[]{formId};
		super.bulkUpdate(hsql, null, values);
	}
	public void deleteFormFlowPermBoundByFormId(Long formId,long accountId)
	{
		String hsql = "delete from EdocFormFlowPermBound as a where a.edocFormId = ? and  domainId=?";
		Object[] values = new Object[]{formId,accountId};
		super.bulkUpdate(hsql, null, values);
	}
}
