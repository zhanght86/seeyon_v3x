package com.seeyon.v3x.office.myapply.manager.impl;

import java.util.Map;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.myapply.dao.ApplyListDAO;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.office.myapply.manager.MyApplyManager;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;

public class MyApplyManagerImpl implements MyApplyManager{
	private ApplyListDAO applyListDAO;
	
	public ApplyListDAO getApplyListDAO() {
		return applyListDAO;
	}

	public void setApplyListDAO(ApplyListDAO applyListDAO) {
		this.applyListDAO = applyListDAO;
	}
	
	
	public void save(TApplylist applyList) {
		this.applyListDAO.save(applyList);
	}
	
	public void update(TApplylist applyList){
		this.applyListDAO.update(applyList);
	}

	public SQLQuery find(String sql,Map map) {
		SQLQuery query = this.applyListDAO.find(sql,map);
		return query;
	}

	public int getCount(String sql,Map map) {
		return this.applyListDAO.getCount(sql,map);
	}

	public TApplylist getById(long id) {
		return this.applyListDAO.load(id);
	}

	/**
	 * 车辆和办公用品操作
	 */
	public void UpdateAutoApplyInfo(AutoApplyInfo autoApply) {
		this.applyListDAO.UpdateAutoApplyInfo(autoApply);	
	}

	public void UpdateStockApplyInfo(StockApplyInfo stockApply) {
		this.applyListDAO.UpdateStockApplyInfo(stockApply);
	}

	public AutoApplyInfo getAutoApplyInfoById(long applyId) {
		return this.applyListDAO.getAutoApplyInfoById(applyId);
	}

	public AutoInfo getAutoById(String autoId) {
		return this.applyListDAO.getAutoById(autoId);
	}

	public StockApplyInfo getStockApplyInfoById(long applyId) {
		return this.applyListDAO.getStockApplyInfoById(applyId);
	}

	public StockInfo getStockById(long stockId) {
		return this.applyListDAO.getStockById(stockId);
	}

    public TAssetApplyinfo getAssetApplyById(long assetId)
    {
       return applyListDAO.getAssetApplyById(assetId);
    }

    public void UpdateAssetApplyInfo(TAssetApplyinfo assetApply)
    {
        applyListDAO.UpdateAssetApplyInfo(assetApply);
        
    }
	
}
