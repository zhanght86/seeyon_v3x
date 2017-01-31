package com.seeyon.v3x.doc.dao;


import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocLibOwner;


public class DocLibOwnerDao extends BaseHibernateDao<DocLibOwner> {
//	public int getNotes(long docLibId,long ownerId){
//		String hsql = "from DocLibOwner as doc where doc.ownerId=? and doc.docLibId=?";
//		List list=super.find(hsql, ownerId, docLibId );
//		return list.size();
//	}
	

	@SuppressWarnings("unchecked")
	public List<Long> getLibsByOwner(Long owner){
		return super.find("select dlo.docLibId from DocLibOwner as dlo where dlo.ownerId=?", -1, -1, null, owner);
	}
}
