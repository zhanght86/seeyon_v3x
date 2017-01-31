package com.seeyon.v3x.office.myapply.dao;

import java.util.Map;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;

public interface ApplyListDAO {
	public void save(TApplylist apply);
	
	public void update(TApplylist apply);
	
	public SQLQuery find(String sql,Map map);

	public int getCount(String sql,Map map);
	
	public TApplylist load(long id);
	
	
	/**
	 * 车辆和办公用品操作
	 */
	public AutoInfo getAutoById(String autoId);
	public AutoApplyInfo getAutoApplyInfoById(long applyId);
	public void UpdateAutoApplyInfo(AutoApplyInfo autoApply);
	
	public StockInfo getStockById(long stockId);
	public StockApplyInfo getStockApplyInfoById(long applyId);
	public void UpdateStockApplyInfo(StockApplyInfo stockApply);
    
     public TAssetApplyinfo getAssetApplyById(long assetId);
     public void UpdateAssetApplyInfo(TAssetApplyinfo assetApply);
}
