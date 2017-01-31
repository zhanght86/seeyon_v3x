package com.seeyon.v3x.bulletin.util;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.util.cache.DataCache;

public class BulDataCache {

	private BulDataManager bulDataManager;
	
	private DataCache<BulData> dataCache;
	
	public void init() {
		this.dataCache = new DataCache<BulData>(this.bulDataManager);
	}
	
	public DataCache<BulData> getDataCache() {
		return this.dataCache;
	}

	public void destroy() {
		this.dataCache.updateAll();
	}
	
	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

}
