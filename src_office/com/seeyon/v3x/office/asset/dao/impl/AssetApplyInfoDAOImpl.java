package com.seeyon.v3x.office.asset.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.office.asset.dao.AssetApplyInfoDAO;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.asset.util.Constants;

public class AssetApplyInfoDAOImpl extends BaseHibernateDao<TAssetApplyinfo> implements
		AssetApplyInfoDAO {
	public void save(TAssetApplyinfo tAssetApplyInfoo){
		super.save(tAssetApplyInfoo);
	}
	
	public void update(TAssetApplyinfo tAssetApplyInfo){
		super.update(tAssetApplyInfo);
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
	
	public TAssetApplyinfo load(long id){
		return (TAssetApplyinfo)this.getHibernateTemplate().load(TAssetApplyinfo.class, new Long(id));
	}
	
	public List listAssetApplyByIds(Long adminId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TAssetApplyinfo.class);
		criteria.add(Restrictions.eq("delFlag",0));
		criteria.add(Restrictions.sqlRestriction(" asset_id in (select asset_id from m_asset_info where del_flag=? and asset_mge=? ) ",new Object[]{0,adminId},new Type[]{Hibernate.INTEGER,Hibernate.LONG}));
		return super.executeCriteria(criteria,-1,-1);
	}
}
