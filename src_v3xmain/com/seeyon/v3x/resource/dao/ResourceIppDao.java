package com.seeyon.v3x.resource.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.resource.domain.ResourceIpp;

public class ResourceIppDao extends BaseHibernateDao<ResourceIpp> {

	public List list() {
		return (List) getHibernateTemplate().find("from ResourceIpp");
	}
	
	public ResourceIpp findByPrimaryKey(Long id) {
		return (ResourceIpp) getHibernateTemplate().load(ResourceIpp.class, new Long(id));
	}	
	
	public void delByAppId(Long appId){
		String sql = "delete from ResourceIpp ri where ri.refAppId=?";
        getHibernateTemplate().bulkUpdate(sql, appId);
	}
}
