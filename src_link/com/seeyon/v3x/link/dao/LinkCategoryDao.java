package com.seeyon.v3x.link.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.link.domain.LinkCategory;

public class LinkCategoryDao extends BaseHibernateDao<LinkCategory> {
	public List<LinkCategory> getLinkCategorys(String name){
		String hsql="from LinkCategory as link where link.name = ?";
		return super.find(hsql, name);
	}
	
	public int getMaxOrderNumber(){
		String hsql="select max(link.orderNum) from LinkCategory as link";
		List list=super.find(hsql);
		int number=0;
		if(list != null && list.isEmpty()==false){
			if(list.get(0)!= null){
				number=(Integer)list.get(0);
				number=number+1;
			}	
		}
		return number;
	}
	
	public void deleteCategorys(String theIds){
		String hsql="delete from LinkCategory as link where link.id in (:ids)";
		Map<String, Object> namedPars = new HashMap<String, Object>();
		namedPars.put("ids", Constants.parseStrings2Longs(theIds, ","));
		super.bulkUpdate(hsql, namedPars);
	}
}
