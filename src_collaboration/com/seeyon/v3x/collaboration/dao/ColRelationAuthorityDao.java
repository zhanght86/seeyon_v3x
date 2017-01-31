package com.seeyon.v3x.collaboration.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.collaboration.domain.ColRelationAuthority;
import com.seeyon.v3x.common.dao.BaseHibernateDao;

public class ColRelationAuthorityDao extends BaseHibernateDao<ColRelationAuthority>{
	
	public void create(ColRelationAuthority colRelationAuthority){
		super.save(colRelationAuthority) ;
	}
	
	public void delObj(ColRelationAuthority colRelationAuthority){
		super.delete(colRelationAuthority) ;
	}
	
	public void delRelationAuthority(Long summaryId ,int userType ,long userId){
		String hql = "delete from ColRelationAuthority  colRelationAuthority where colRelationAuthority.summaryId =:summaryId and colRelationAuthority.userType =:userType and colRelationAuthority.userId =:userId"  ;
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		nameParameters.put("userType", userType) ;
		nameParameters.put("userId", userId) ;
		super.bulkUpdate(hql, nameParameters) ;
	}
	
	public void delRelationAuthority(Long summaryId){
		String hql = "delete from ColRelationAuthority colRelationAuthority where colRelationAuthority.summaryId =:summaryId";
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		super.bulkUpdate(hql, nameParameters) ;
	}
	
	public List<ColRelationAuthority> getAuthorityList(Long summaryId ){
		String hql = "from ColRelationAuthority colRelationAuthority where colRelationAuthority.summaryId =:summaryId";
		Map<String,Object> nameParameters = new HashMap<String,Object>() ;
		nameParameters.put("summaryId", summaryId) ;
		return super.find(hql, -1, -1, nameParameters) ;
	}
}
