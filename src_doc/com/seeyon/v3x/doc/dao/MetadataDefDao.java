package com.seeyon.v3x.doc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.util.Constants;

public class MetadataDefDao extends BaseHibernateDao <DocMetadataDefinition>{
	/**
	 * 取得所有元数据定义的类别
	 */
	public List findAllGroup(){
		String hsql="select distinct def.category from DocMetadataDefinition as def where def.status != ?";
		List list=super.find(hsql, Constants.DOC_METADATA_DEF_STATUS_DELETED);
		return list;
	}
	
//	/**
//	 * 根据类别取得元数据定义
//	 */
//	public List<DocMetadataDefinition> getMetadataDefByCategory(String categoryName){
//		String hsql="from DocMetadataDefinition as doc where doc.category=? and doc.isDefault=false";
//		return super.find(hsql, categoryName);
//	}
	
	/**
	 * 取得所有元数据定义
	 */
	public List<DocMetadataDefinition> findAll() {
		String hsql = "from DocMetadataDefinition as a order by a.id asc";
		return super.find(hsql);
	}
	
	/**
	 * 删除对应的元数据定义，一起删除detail，DocListColumn等级联
	 * 
	 */
	public void deleteDef(long id){
		String hqlC = "delete from DocListColumn where metadataDefiniotionId = ?";
		String hqlD = "delete from DocTypeDetail where metadataDefId = ?";
		String hqlO = "delete from DocMetadataOption where metadataDef.id = ?";
		String hql = "delete from DocMetadataDefinition where id = ?";
		
		super.bulkUpdate(hqlC, null, id);
		super.bulkUpdate(hqlD, null, id);
		super.bulkUpdate(hqlO, null, id);
		super.bulkUpdate(hql, null, id);
	}
	
	public DocMetadataDefinition getDef(long id){
		return super.get(id);
	}
	
}
