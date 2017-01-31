package com.seeyon.v3x.mobile.dao;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.mobile.message.StatisticAccount;
import com.seeyon.v3x.mobile.message.StatisticDepartment;
import com.seeyon.v3x.mobile.message.domain.MobileMessage;



public interface MobileMessageDao {
	public void saveMobileMessage(MobileMessage m);

	public MobileMessage getMessageById(Long id);

	public void deleteMessageById(Long id);
	
	public void updateById(Long id,boolean send);
	
	public void updateMobileMessageState();
	
	public MobileMessage getMobileMessageByFeatureCode(String str);
	
	/**
	 * 按照单位统计
	 * 
	 * @param startDate
	 * @param toDate
	 * @return
	 */
	public List<StatisticAccount> statisticByAccount(Date startDate, Date toDate);
	
	/**
	 * 按部门统计
	 * 
	 * @param accoutId
	 * @param departmentId
	 * @param startDate
	 * @param toDate
	 * @return
	 */
	public List<StatisticDepartment> statisticByDepartment(long accoutId, List<Long> departmentIds,
			Date startDate, Date toDate);
}
