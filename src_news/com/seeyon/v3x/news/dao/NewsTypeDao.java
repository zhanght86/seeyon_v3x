package com.seeyon.v3x.news.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.bulletin.BulletinException;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.SQLWildcardUtil;

public class NewsTypeDao extends BaseHibernateDao<NewsType> {

	public List<NewsType> getByAccountId(long accountId){
		/*DetachedCriteria criteria = DetachedCriteria.forClass(NewsType.class);
		criteria.add(Restrictions.eq("accountId", accountId));
		return super.executeCriteria(criteria, -1, -1);*/
		
		//HQL语句清理 modified by Meng Yang 2009-05-27
		List<NewsType> list = this.find("from NewsType as nt where nt.accountId=?",-1,-1,null,accountId) ;
		return list;
	}
	
	/**
	 * 按公告名字查询得到
	 * @param memberId
	 * @param typename
	 * @return
	 * @throws BulletinException
	 */
	public List<NewsType> getAllNewsType(Long memberId ,String typename) throws BulletinException{
		List<NewsType> list ;
		List<Object> params  = new ArrayList<Object>() ;
		//Map<String , Object> params = new HashMap<String ,Object>();
		final String hqlf = " from NewsType as nt where nt.typeName like ? " ;
		params.add("%" + SQLWildcardUtil.escape(typename) + "%") ;
		list = this.find(hqlf,-1,-1,null,params) ;
		return list ;
	}
	
	/**
	 * @param memberId
	 * @param typename
	 * @return
	 * @throws BulletinException
	 */
	public List<NewsType> getAllNewsType(String username) throws BulletinException{
		/**
         * 得到与该名字相近的所有的用户
         */
		/*List<Object> param = new ArrayList<Object>();
		
		final String hql = "from "+ V3xOrgMember.class.getName() +" as m where m.name like ? ";
		//params.put("name" , name) ;
		param.add("%"+username+"%") ;
		
		List<V3xOrgMember> menberList = this.find(hql, -1,-1,null, param) ; 
		List<Object> param2 = new ArrayList<Object>();
		String ids = "" ;
		if (menberList == null || menberList.isEmpty()) {
			return Collections.EMPTY_LIST;
		}else{
			for(V3xOrgMember m : menberList){
				ids += ",?" ;
				param2.add(m.getId()) ;
			}			
		}
		*//**
		 * hqlf语句
		 *//*
		String hqlType = "(" + ids.substring(1, ids.length()) + ") " ;
	    final String hqlf = "from " +NewsType.class.getName()+" as news_type where news_type.auditUser in " + hqlType  ;
	    
	    *//**
	     * 条件 auditUser是在param2集合中    
	     *//*
	    List<NewsType> list = new ArrayList<NewsType>()  ;
	    list = this.find(hqlf, -1 ,-1,null ,param2) ;
	    return list ;*/
		
		//HQL语句清理 modified by Meng Yang 2009-05-27
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		String hql = "from "+ V3xOrgMember.class.getName() +" as m where m.name like :username";
		parameterMap.put("username", "%" + SQLWildcardUtil.escape(username) + "%");
		List<V3xOrgMember> memberList = this.find(hql, -1, -1, parameterMap) ; 
		
		List<Long> memberIds = new ArrayList<Long>();
		if(memberList == null || memberList.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			for(V3xOrgMember member : memberList) {
				memberIds.add(member.getId());
			}
		}
		
		String hqlf = "from " +NewsType.class.getName()+" as news_type where news_type.auditUser in (:memberIds)" ;
		parameterMap.put("memberIds", memberIds);
		
		List<NewsType> list = this.find(hqlf, -1 ,-1, parameterMap);
		return list;
	}
}
