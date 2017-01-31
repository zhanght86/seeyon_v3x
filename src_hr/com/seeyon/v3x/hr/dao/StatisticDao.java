package com.seeyon.v3x.hr.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.util.Strings;

public class StatisticDao extends BaseHibernateDao<StaffInfo>{
	
	/**
	 * 得到所有员工的信息
	 */
	public List getAllStaffInfo() {
		return (List) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer sHql = new StringBuffer();
						sHql.append("select staff from com.seeyon.v3x.hr.domain.StaffInfo staff");
						Query query = session.createQuery(sHql.toString());
						return query.list();
					}
				});
	}
	
	/**
	 * 根据Mem的id获得所有员工的信息
	 */
	public List getStaffInfoByMemIds(final List<Long> memIds){
		return (List) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from StaffInfo ");
						Map<String, Object> params = new HashMap<String, Object>();
//						for(Long id:memIds)
//						{
//							hql.append(" or org_member_id=?");
//						}
						if(memIds!=null && !memIds.isEmpty()){
							 List<Long>[]  list = Strings.splitList(memIds, 900);
							 for(int i=0; i<list.length; i++){
								 if(i==0)
									 hql.append(" where org_member_id in(:memberids"+i+")");
								 else
									 hql.append(" or org_member_id in(:memberids"+i+")");
								 params.put("memberids"+i, list[i]);
							 }
						}
						return find(hql.toString(),params);
					
					}
				});
	}
	
	/**
	 * 根据单位Id获得所有员工信息
	 * @return
	 */
	public List<StaffInfo> getAllStaffInfoByAccountId(Long accountId){
		Map<String, Object> params = new HashMap<String, Object>();
		String hql = "select info from StaffInfo info, V3xOrgMember m where m.id = info.org_member_id and m.orgAccountId = :accountId";
		params.put("accountId", accountId);
		return this.find(hql, -1, -1, params);
	}

}
