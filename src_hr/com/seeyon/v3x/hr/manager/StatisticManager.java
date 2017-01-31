package com.seeyon.v3x.hr.manager;

import java.util.List;

import com.seeyon.v3x.hr.domain.StaffInfo;

public interface StatisticManager {
	public List<StaffInfo> getAllStaffInfo();
	public List<StaffInfo> getAllStaffInfoByAccountId(Long accountId);
	public List<StaffInfo> getStaffByMemIds(List<Long> memIds);
}
