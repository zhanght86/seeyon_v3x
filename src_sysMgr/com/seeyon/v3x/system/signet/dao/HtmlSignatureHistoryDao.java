package com.seeyon.v3x.system.signet.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.system.signet.domain.V3xHtmlSignatureHistory;

public class HtmlSignatureHistoryDao extends BaseHibernateDao<V3xHtmlSignatureHistory> {
	
	public List <V3xHtmlSignatureHistory> findByIdAndPolicy(Long summaryId,String policy)
	{
		String hsql="from V3xHtmlSignatureHistory where summaryId=? and fieldName=?";
        return super.find(hsql, new Object[]{summaryId,policy});
	}

}
