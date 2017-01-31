package com.seeyon.v3x.doc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocListColumn;

public class DocListColumnDao extends BaseHibernateDao<DocListColumn> {
	
	/**
	 * 根据文档库id取得显示栏目
	 */
	public List<DocListColumn> findColumnByOrderNum(long docLibId){
		String hsql="from DocListColumn as doc where doc.docLibId=? order by doc.orderNum ";
		List list=super.find(hsql, docLibId);
		return list;
	}
	
	/**
	 * 批量修改文档库id
	 */
	public void batchUpdateDocLibId(long oldLibId, long newLibId){
		String hql = "update DocListColumn set docLibId = ? where docLibId = ? ";
		super.bulkUpdate(hql, null , oldLibId, newLibId);
	}
}
