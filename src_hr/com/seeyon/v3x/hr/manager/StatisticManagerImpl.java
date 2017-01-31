package com.seeyon.v3x.hr.manager;

import java.util.List;

import com.seeyon.v3x.hr.dao.StatisticDao;
import com.seeyon.v3x.hr.domain.StaffInfo;

public class StatisticManagerImpl implements StatisticManager {
	private StatisticDao statisticDao;

	public StatisticDao getStatisticDao() {
		return statisticDao;
	}

	public void setStatisticDao(StatisticDao statisticDao) {
		this.statisticDao = statisticDao;
	}
	
	public List<StaffInfo> getAllStaffInfo(){
		return this.statisticDao.getAllStaffInfo();
	}
	
	public List<StaffInfo> getAllStaffInfoByAccountId(Long accountId){
		return statisticDao.getAllStaffInfoByAccountId(accountId);
	}
	
	public List<StaffInfo> getStaffByMemIds(List<Long> memIds){
		return this.statisticDao.getStaffInfoByMemIds(memIds);
	}
}
