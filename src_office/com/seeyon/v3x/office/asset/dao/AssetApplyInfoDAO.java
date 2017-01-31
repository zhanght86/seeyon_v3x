package com.seeyon.v3x.office.asset.dao;

import java.util.List;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.office.asset.domain.TAssetApplyinfo;



public interface AssetApplyInfoDAO {
	public void save(TAssetApplyinfo tAssetApplyInfo);
	
	public void update(TAssetApplyinfo tAssetApplyInfo);
	
	public SQLQuery find(String sql);

	public int getCount(String sql);
	
	public TAssetApplyinfo load(long id);
	
	/**
	 * 根据管理员得到申请
	 * @param ids
	 * @return
	 */
	public List listAssetApplyByIds(Long ids);
}
