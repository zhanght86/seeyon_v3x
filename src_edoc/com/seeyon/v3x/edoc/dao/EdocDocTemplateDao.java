package com.seeyon.v3x.edoc.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocDocTemplate;
import com.seeyon.v3x.edoc.util.Constants;

public class EdocDocTemplateDao extends BaseHibernateDao<EdocDocTemplate> {
	
//	public boolean checkHasName(String name){
//		String hsql="from EdocDocTemplate as edoc where edoc.name=?";
//		List list=super.find(hsql,name);
//		if(list.isEmpty()==false){
//			return true;
//		}
//		return false;
//	}
	
	@SuppressWarnings("unchecked")
	public List<EdocDocTemplate> findByUserIds(List<Long> theIds, int type){
		String sql = "from EdocDocTemplate as edoc where edoc.type=? and edoc.status=? and edoc.domainId in (:ids)";
		
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		namedParameterMap.put("ids", theIds);
		
		List<EdocDocTemplate> list = super.find(sql, -1, -1, namedParameterMap, type, Constants.EDOC_USEED);
		return list;
	}
	
	public List<EdocDocTemplate> findByDomainId(Long domainId){
    	return super.find("from EdocDocTemplate as template where template.domainId=? order by template.domainId", domainId);
		
	}
	
	public List<EdocDocTemplate> findByDomainIdAndType(Long domainId,int type){
		String sql ="from EdocDocTemplate as template where template.domainId=? and template.type=? order by template.domainId";

    	return super.find(sql, domainId, type);
		
	}
	
	public List<EdocDocTemplate> findByName(Long domainId,String name){
		String sql ="from EdocDocTemplate as template where template.domainId=? and template.name like ? order by template.domainId";

    	return super.find(sql, domainId, "%" + name + "%");
		
	}
	
	public List<EdocDocTemplate> findByStatus(Long domainId,int status){
		String sql ="from EdocDocTemplate as template where template.domainId=? and template.status=? order by template.domainId";

    	return super.find(sql, domainId, status);
		
	}
	
	/**
	 * 根据传入的id集合传（单位/部门），类型（正文/文单），来查找授权的模板
	 * @param ids  id集合传（单位/部门）
	 * @param type  类型（正文/文单）
	 * @param textType 正文类型
	 * @return
	 */
	public List<EdocDocTemplate> findGrantedTemplateForTaohong(String ids, int type, String textType){
		
		StringBuffer sql = new StringBuffer("select template from EdocDocTemplate as template,EdocDocTemplateAcl as tempAcl where template.id = tempAcl.templateId and template.type =?");
		Map<String,Object> namedParameter = new HashMap<String,Object>();
		List<Long> idList = null;
		if(ids != null){
			idList = new ArrayList<Long>();
			String[] tmps = ids.split(",");
			for(String id:tmps)
				idList.add(Long.valueOf(id));
		}
		if(textType!=null && !"".equals(textType)){
			sql.append(" and template.textType =:textType");
			namedParameter.put("textType", textType);
		}
		sql.append(" and tempAcl.depId in (:ids) order by template.createTime");
		namedParameter.put("ids", idList);
		//不分页
		return super.find(sql.toString(),-1,-1,namedParameter,type);
	}
	
	public List searchByCriteria(DetachedCriteria detachedCriteria){
		return super.getHibernateTemplate().findByCriteria(detachedCriteria);
	}
	
}
