package com.seeyon.v3x.office.asset.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.office.asset.dao.AssetDepartInfoDAO;
import com.seeyon.v3x.office.asset.domain.*;
import com.seeyon.v3x.office.asset.util.Constants;
import com.seeyon.v3x.util.Datetimes;

public class AssetDepartInfoDAOImpl extends BaseHibernateDao<TAssetDepartinfo> implements
		AssetDepartInfoDAO {
	public void save(TAssetDepartinfo tAssetDepartInfo){
		super.save(tAssetDepartInfo);
	}
	
	public void update(TAssetDepartinfo tAssetDepartInfo){
		super.update(tAssetDepartInfo);
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
	
	public TAssetDepartinfo load(long id){
		return (TAssetDepartinfo)this.getHibernateTemplate().load(TAssetDepartinfo.class, new Long(id));
	}

	@Override
	public List getAssetBackListByUserId(String userid) {
		StringBuffer hql= new StringBuffer(" select a  from TAssetDepartinfo a,TApplylist t ");
		hql.append(" where t.applyUsername=? and t.applyId=a.applyId and a.delFlag=0 ");
		hql.append(" and ( a.assetBacktime is null or a.assetBacktime >= ?) ");
		List list= super.find(hql.toString(), new Long(userid),new Date(System.currentTimeMillis()));
		return list;
	}
}
