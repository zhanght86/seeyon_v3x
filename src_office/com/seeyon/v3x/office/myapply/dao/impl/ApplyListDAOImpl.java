package com.seeyon.v3x.office.myapply.dao.impl;

import java.util.Iterator;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.myapply.dao.ApplyListDAO;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.office.myapply.util.Constants;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;

public class ApplyListDAOImpl extends BaseHibernateDao<TApplylist> implements ApplyListDAO {
	public void save(TApplylist apply){
		super.save(apply);
	}

	public void update(TApplylist apply) {
		super.update(apply);
	}
	
	public SQLQuery find(String sql,Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		try{
			query = session.createSQLQuery(sql);
			if(map != null){
				Iterator iterator = map.keySet().iterator();
				String key = null ;
				for(;iterator.hasNext();){
					key = iterator.next().toString();
					query.setParameter(key, map.get(key));
				}
			}
		}catch(Exception ex){
			
		}finally{
			super.releaseSession(session);
		}
		return query;
	}

	public int getCount(String sql,Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		try{
			query = session.createSQLQuery(sql);
			if(map != null){
				Iterator iterator = map.keySet().iterator();
				String key = null ;
				for(;iterator.hasNext();){
					key = iterator.next().toString();
					query.setParameter(key, map.get(key));
				}
			}
		}catch(Exception ex){
			
		}finally{
			super.releaseSession(session);
		}
		int totalCount = (Integer)query.addScalar(Constants.Total_Count_Field, Hibernate.INTEGER).uniqueResult();
		return totalCount;
	}

	public TApplylist load(long id) {
		return (TApplylist)this.getHibernateTemplate().load(TApplylist.class, new Long(id));
	}
	
	
	/**
	 * 车辆和办公用品操作
	 */
	public void UpdateAutoApplyInfo(AutoApplyInfo autoApply) {
		this.update(autoApply);	
	}

	public void UpdateStockApplyInfo(StockApplyInfo stockApply) {
		this.update(stockApply);
	}

	public AutoApplyInfo getAutoApplyInfoById(long applyId) {
		return (AutoApplyInfo)this.getHibernateTemplate().load(AutoApplyInfo.class, applyId);
	}

	public AutoInfo getAutoById(String autoId) {
		return (AutoInfo)this.getHibernateTemplate().load(AutoInfo.class, autoId);
	}

	public StockApplyInfo getStockApplyInfoById(long applyId) {
		return (StockApplyInfo)this.getHibernateTemplate().load(StockApplyInfo.class, applyId);
	}

	public StockInfo getStockById(long stockId) {
		return (StockInfo)this.getHibernateTemplate().load(StockInfo.class, stockId);
	}
    public TAssetApplyinfo getAssetApplyById(long assetId) {
        return (TAssetApplyinfo)this.getHibernateTemplate().load(TAssetApplyinfo.class, assetId);
    }
    public void UpdateAssetApplyInfo(TAssetApplyinfo assetApply) {
        this.update(assetApply);
    }
}
