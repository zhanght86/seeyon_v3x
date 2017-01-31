package com.seeyon.v3x.office.book.dao.impl;

import java.util.*;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.office.book.util.Constants;
import com.seeyon.v3x.office.book.dao.*;
import com.seeyon.v3x.office.book.domain.*;
import com.seeyon.v3x.office.book.util.*;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BookDepartInfoDAOImpl extends BaseHibernateDao<TBookDepartinfo> implements BookDepartInfoDAO {
	
	public void save(TBookDepartinfo tBookDepartinfo){
		super.save(tBookDepartinfo);
	}
	
	public void update(TBookDepartinfo tBookDepartinfo){
		super.update(tBookDepartinfo);
	}

	public SQLQuery find(String sql) {
		Session session = super.getSession();
		SQLQuery query = null;
		try{
			query = session.createSQLQuery(sql);
		}catch(Exception ex){
			
		}finally{
			super.releaseSession(session);
		}
		return query;
	}

	public int getCount(String sql) {
		Session session = super.getSession();
		SQLQuery query = null;
		try{
			query = super.getSession().createSQLQuery(sql);
		}catch(Exception ex){
			
		}finally{
			super.releaseSession(session);
		}
		int totalCount = (Integer)query.addScalar(Constants.Total_Count_Field, Hibernate.INTEGER).uniqueResult();
		return totalCount;
	}
	
	public TBookDepartinfo load(long id){
		return (TBookDepartinfo)this.getHibernateTemplate().load(TBookDepartinfo.class, new Long(id));
	}

	@Override
	public List getBookBackListByUserId(String userid) {
		StringBuffer hql= new StringBuffer(" select a  from TBookDepartinfo a,TApplylist t ");
		hql.append(" where t.applyUsername=? and t.applyId=a.applyId and a.delFlag=0 ");
		hql.append(" and ( a.bookBacktime is null or a.bookBacktime >= ?) ");
		List list= super.find(hql.toString(), new Long(userid),new Date(System.currentTimeMillis()));
		return list;
	}
}
