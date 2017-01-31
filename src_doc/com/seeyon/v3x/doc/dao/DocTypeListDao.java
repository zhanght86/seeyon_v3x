package com.seeyon.v3x.doc.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocTypeList;

public class DocTypeListDao extends BaseHibernateDao<DocTypeList> {
	/**
	 * 取得某个文档库下的内容类型最大排序号
	 */
	public List findMaxOrderNum(long docLibId){
		String hsql="select max(doc.orderNum) from DocTypeList as doc where doc.docLibId=?";
		List list=super.find(hsql,docLibId);
		return list;
	}
	
	/**
	 * 取得某个文档库的所有内容类型
	 */
	public List<DocTypeList> getDocTypeList(long docLibId) {
		String hsql = "from DocTypeList as a where a.docLibId=? order by a.orderNum";
		return super.find(hsql, docLibId);
	}
	
	/**
	 * 删除某个内容类型对应的所有文档库关联
	 */
	public void deleteDocTypeListByTypeId(long docTypeId) {
		List<DocTypeList> list=findBy("docTypeId",docTypeId );
		if(list.isEmpty())return ;
		for(int i=0;i<list.size();i++){
			DocTypeList docTypeList=list.get(i);
			deleteObject(docTypeList);
		}
	}
	
	/**
	 * 批量修改文档库id
	 */
	public void batchUpdateDocLibId(long oldLibId, long newLibId){
		String hql = "update DocTypeList set docLibId = ? where docLibId =?";
		super.bulkUpdate(hql, null, newLibId, oldLibId);
	}
	
}
