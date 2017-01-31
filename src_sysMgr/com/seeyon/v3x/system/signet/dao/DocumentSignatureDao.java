package com.seeyon.v3x.system.signet.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;

import com.seeyon.v3x.system.signet.domain.V3xDocumentSignature;

public class DocumentSignatureDao  extends BaseHibernateDao<V3xDocumentSignature>{
	
	public List<V3xDocumentSignature> findByRecordId(String recordId) throws Exception {
		return super.findBy("recordId",recordId);
	}

}
