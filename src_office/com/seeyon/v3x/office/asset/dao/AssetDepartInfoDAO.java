package com.seeyon.v3x.office.asset.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import com.seeyon.v3x.office.asset.domain.*;

public interface AssetDepartInfoDAO {
	public void save(TAssetDepartinfo tAssetDepartInfo);
	
	public void update(TAssetDepartinfo tAssetDepartInfo);
	
	public SQLQuery find(String sql);

	public int getCount(String sql);
	
	public TAssetDepartinfo load(long id);

	/**
	 * 根据userid获得该用户还没有归还的综合办公物品列表
	 * @param userid
	 * @return
	 */
	public List getAssetBackListByUserId(String userid);
}
