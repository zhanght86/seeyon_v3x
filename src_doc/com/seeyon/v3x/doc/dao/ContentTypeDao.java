package com.seeyon.v3x.doc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.util.Constants;


public class ContentTypeDao extends BaseHibernateDao <DocType>{
	
//	public List<DocType> getDocTypes(String name) {
//		String hsql = "from DocType as doc where doc.name=? and doc.isSystem=false";
//		return super.find(hsql, name);
//	}
	
//	public List<DocType> getDocTypes() {
//		String hsql = "from DocType as doc where doc.editable=true";
//		return super.find(hsql);
//	}
	
	/**
	 * 加载所有内容类型
	 */
	public List<DocType> findAll() {
		String hsql = "from DocType as a order by a.isSystem,a.name";
		return super.find(hsql);
	}
	
	/**
	 * 多个查找
	 */
	public List<DocType> findByIds(String ids){
		String hql = "from DocType where id in(:ids) ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", Constants.parseStrings2Longs(ids, ","));
		return super.find(hql, -1, -1, map);
	}
	
	/**
	 * 发布内容类型标记
	 */
	public void setContentTypePublished(long id){
		super.bulkUpdate("update DocType set status = ? where id = ?", null, Constants.CONTENT_TYPE_PUBLISHED, id);
	}
	
}
