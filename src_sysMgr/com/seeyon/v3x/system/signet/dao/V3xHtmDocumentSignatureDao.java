package com.seeyon.v3x.system.signet.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.system.signet.domain.V3xHtmDocumentSignature;
import java.util.*;

public class V3xHtmDocumentSignatureDao extends BaseHibernateDao<V3xHtmDocumentSignature> {
	
	
	public List <V3xHtmDocumentSignature> findByIdAndPolicy(Long summaryId,String policy)
	{
		String hsql="from V3xHtmDocumentSignature where summaryId=? and fieldName=?";
        return super.find(hsql, new Object[]{summaryId,policy});
	}

}
