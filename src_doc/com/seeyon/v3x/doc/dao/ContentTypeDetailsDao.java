package com.seeyon.v3x.doc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;

public class ContentTypeDetailsDao extends BaseHibernateDao<DocTypeDetail> {
//	/**
//	 * 元数据定义是否已经使用
//	 * 
//	 * @param dmd
//	 * @return
//	 */
//	public boolean useMetadataDef(DocMetadataDefinition dmd) {
//		List list = super.findBy("metadataDefId", dmd.getId());
//		if (list == null || list.size() == 0) {
//			return false;
//		}
//		return true;
//	}
	
//	public DocTypeDetail getUniqueDocTypeDetail(DocType docType,DocMetadataDefinition definition){
//		
//		String hsql="from DocTypeDetail as doc where doc.contentTypeId=? and doc.metadataDefId=?";
//		List<DocTypeDetail> list=super.find(hsql, docType.getId(),definition.getId());
//		if(list != null && list.isEmpty()==false){
//			return list.get(0);
//		}
//		return null;
//	}
	


}
