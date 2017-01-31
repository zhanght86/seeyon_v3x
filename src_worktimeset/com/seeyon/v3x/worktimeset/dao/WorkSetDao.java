package com.seeyon.v3x.worktimeset.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCacheEntity;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.domain.WorkTimeSpecial;

public class WorkSetDao extends BaseHibernateDao<Object>{



	public static final Long GROUP_ADMIN_ACCOUNT = Long
			.parseLong("-1730833917365171641");
	
	private final static Log log = LogFactory.getLog(WorkSetDao.class);

	

	/**
	 * 从数据库加载通用工作时间设置
	 * 
	 * @param orgAccountID
	 * @param year
	 * @param isWork
	 * @return
	 */
	public List<WorkTimeCurrency> getWorkTimeCurrencyFromDB(Long orgAccountID,
			int year, String isWork) {
		List<WorkTimeCurrency> returnList = new ArrayList<WorkTimeCurrency>();
		String hql = "from WorkTimeCurrency as workTimeCurrency "
				+ "where workTimeCurrency.orgAcconutID = :orgAcconutID "
				+ "and workTimeCurrency.year = :year "
				+ "and workTimeCurrency.isWork = :isWork "
				+ "order by workTimeCurrency.weekDayName";
		/*
		 * Map<String, Object> parameterMap = new HashMap<String, Object>();
		 * parameterMap.put("orgAcconutID", orgAccountID);
		 * parameterMap.put("year", year); parameterMap.put("isWork", isWork);
		 */

		String[] paramNames = {"orgAcconutID","year","isWork"};
		Object[] values = {orgAccountID,Integer.toString(year),isWork};
		List<WorkTimeCurrency> workTimeCurrencies = this.getHibernateTemplate()
				.findByNamedParam(hql,
				paramNames, values);
		returnList.addAll(workTimeCurrencies);
		
		return returnList;
	}

	/**
	 * 从数据库加载特殊工作时间设置
	 * 
	 * @param orgAccountID
	 * @param year
	 * @return
	 */
	public List<WorkTimeSpecial> getWorkTimeSpeicalFromDB(Long orgAccountID,
			int year) {
		List<WorkTimeSpecial> returnList = new ArrayList<WorkTimeSpecial>();
		String hql = "from WorkTimeSpecial as workTimeSpecial "
				+ "where workTimeSpecial.orgAcconutID = :orgAcconutID "
				+ "and workTimeSpecial.year = :year ";
		/*
		 * Map<String, Object> parameterMap = new HashMap<String, Object>();
		 * parameterMap.put("orgAcconutID", orgAccountID);
		 * parameterMap.put("year", year);
		 */

		String[] paramNames = { "orgAcconutID", "year" };
		Object[] values = { orgAccountID, Integer.toString(year) };
		/*
		 * List<WorkTimeSpecial> workTimeSpecials = this.find(hql, -1, -1,
		 * parameterMap);
		 */
		List<WorkTimeSpecial> workTimeSpecials = this.getHibernateTemplate()
				.findByNamedParam(hql, paramNames, values);
		returnList.addAll(workTimeSpecials);

		return returnList;
	}

	/**
	 * 取得一年的通用工作日设置ids
	 * 
	 * @param year
	 * @param orgAcconutID
	 * @return
	 */
	public List<WorkTimeCurrency> comonWorkDayTimeIds(String year,
			long orgAcconutID) {
		StringBuffer hql = new StringBuffer();
		hql
				.append("from WorkTimeCurrency wtc where wtc.orgAcconutID = :orgAcconutID and wtc.year = :year order by wtc.weekDayName");
		/*
		 * Map<String, Object> parameterMap = new HashMap<String, Object>();
		 * parameterMap.put("orgAcconutID", orgAcconutID);
		 * parameterMap.put("year", year);
		 */
		String[] paramNames = { "orgAcconutID", "year" };
		Object[] values = { orgAcconutID, year };
		List<WorkTimeCurrency> workTimeCurrencies = this.getHibernateTemplate()
				.findByNamedParam(hql.toString(), paramNames, values);
		return workTimeCurrencies;
	}

	public List<Object[]> currencyOrgAccountAndYear() {
		String hqlString = "select wtc.orgAcconutID, wtc.year from WorkTimeCurrency wtc group by wtc.orgAcconutID, wtc.year order by wtc.year desc";
		List<Object[]> result = this.getHibernateTemplate().find(hqlString);
		return result;
	}

	public List<Object[]> specialOrgAccountAndYear() {
		String hqlString = "select wtc.orgAcconutID, wtc.year from WorkTimeSpecial wtc group by wtc.orgAcconutID, wtc.year order by wtc.year desc";
		List<Object[]> result = this.getHibernateTemplate().find(hqlString);
		return result;
	}
	
	/**
	 * 用于判断某天是否为工作日
	 * @param accountID
	 * @param year
	 * @param day
	 * @return
	 */
	public boolean isWorkDayInCurrency(Long accountID, String year, String day) {
		Integer i = Integer.valueOf(year);
		String hql = "select count(*) from WorkTimeCurrency wtc where wtc.orgAcconutID=:orgAcconutID and wtc.year=:year and wtc.weekDayName=:day and wtc.isWork='1'";
		String[] paramNames = {"orgAcconutID", "year", "day"};
		Object[] values = {accountID, year, day};
		int size = (Integer) this.getHibernateTemplate().findByNamedParam(hql.toString(), paramNames, values).get(0);
		if (size > 0) {
			return true;
		} else if (i >= 2010) {
			i = i-1;
			isWorkDayInCurrency(accountID, i.toString(), day);
		} else {
			return false;
		}
		return false;
	}
	
	/**
	 * 用于判断某天是否为工作日
	 * @param accountID
	 * @param dateNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<WorkTimeSpecial> isWorkDayInSpecial(Long accountID, String dateNum) {
		String hql = "from WorkTimeSpecial as wts where wts.orgAcconutID=:orgAcconutID and wts.dateNum=:dateNum";
		String[] paramNames = {"orgAcconutID", "dateNum"};
		Object[] values = {accountID, dateNum};
		List<WorkTimeSpecial> workTimeSpecials = this.getHibernateTemplate().findByNamedParam(hql.toString(), paramNames, values);
		return workTimeSpecials;
	}

}
