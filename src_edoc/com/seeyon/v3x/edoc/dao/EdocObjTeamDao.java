package com.seeyon.v3x.edoc.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;

public class EdocObjTeamDao extends BaseHibernateDao<EdocObjTeam> {
	
	public List<EdocObjTeam> findAll()
	{
		List<EdocObjTeam> edocObjTeams=null;
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocObjTeam.class)
		.add(Expression.eq("state",EdocObjTeam.STATE_USE))
		;
		edocObjTeams=super.executeCriteria(criteria);
		return edocObjTeams;
	}
	
	public List<EdocObjTeam> findAllByAccount(Long accountId, boolean isPager)
	{
		List<EdocObjTeam> edocObjTeams=null;
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocObjTeam.class)
		.add(Expression.eq("orgAccountId",accountId))
		.add(Expression.eq("state",EdocObjTeam.STATE_USE))
		.addOrder(Order.desc("createTime"));
		;
		if(isPager){
			edocObjTeams=super.executeCriteria(criteria);
		}
		else{
			edocObjTeams=super.executeCriteria(criteria, -1, -1);
		}
		return edocObjTeams;
	}
	
	//处理按照名称模糊查询
	public List<EdocObjTeam> findAllByName(String name, Long accountId, boolean isPager){
		List<EdocObjTeam> edocObjTeams=null;
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocObjTeam.class);
		criteria.add(Expression.eq("orgAccountId", accountId))
		.add(Expression.eq("state", EdocObjTeam.STATE_USE))
		.add(Expression.like("name", "%"+name+"%"))
		.addOrder(Order.desc("createTime"));
		if(isPager){
			edocObjTeams=super.executeCriteria(criteria);
		}
		else{
			edocObjTeams=super.executeCriteria(criteria, -1, -1);
		}
		return edocObjTeams;
	}
	
	public void updateState(String ids,Byte state)
	{
		String hql="update EdocObjTeam set state=? where id in (:ids)";
		Map<String,Object> namedParameter = new HashMap<String,Object>();
		List<Long> idList = new ArrayList<Long>();
		String[] tmp = ids.split(",");
		for(String id:tmp)
			idList.add(Long.valueOf(id));
		namedParameter.put("ids", idList);
		super.bulkUpdate(hql,namedParameter,state);
	}
	public EdocObjTeam findByAccountAndName(Long accountId,String orgName)
	{
		List<EdocObjTeam> edocObjTeams=null;
		DetachedCriteria criteria = DetachedCriteria.forClass(EdocObjTeam.class)
		.add(Expression.eq("orgAccountId",accountId))
		.add(Expression.eq("state",EdocObjTeam.STATE_USE))
		.add(Expression.eq("name",orgName))
		;
		edocObjTeams=super.executeCriteria(criteria);
		if(edocObjTeams.size()>0){return edocObjTeams.get(0);}
		else{return null;}
	}
}
