package com.seeyon.v3x.worktimeset.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import com.seeyon.v3x.hr.manager.RecordManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.worktimeset.dao.WorkSetDao;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCacheEntity;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.domain.WorkTimeSpecial;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;
public class WorkTimeSetManagerImpl implements WorkTimeSetManager {
	private final static Log log = LogFactory
			.getLog(WorkTimeSetManagerImpl.class);

	private CacheMap<Long, ArrayList<WorkTimeCacheEntity>> workTimeCache;

	public static final Long GROUP_ADMIN_ACCOUNT = Long
			.parseLong("-1730833917365171641");
	private WorkSetDao workSetDao;

	public WorkSetDao getWorkSetDao() {
		return workSetDao;
	}

	public void setWorkSetDao(WorkSetDao workSetDao) {
		this.workSetDao = workSetDao;
	}

	private AppLogManager appLogManager;

	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	private RecordManager recordManager;

	private OrgManager OrgManager;

	public OrgManager getOrgManager() {
		return OrgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		OrgManager = orgManager;
	}

	public RecordManager getRecordManager() {
		return recordManager;
	}

	public void setRecordManager(RecordManager recordManager) {
		this.recordManager = recordManager;
	}

	public void setWorkTimeCache(
			CacheMap<Long, ArrayList<WorkTimeCacheEntity>> workTimeCache) {
		this.workTimeCache = workTimeCache;
	}

	private static String triggerName = "workTimeCurrencySaveTriggerName";
	private static String triggerGroupName = "workTimeCurrencySave_groupName";
	private static String jobName = "workTimeCurrencySave_jobName";
	private static String jobGroupName = "workTimeCurrencySaveJobGroupName";

	// 默认工作时间设置
	public static final String defaultWorkAmBeginTime = "09:00";
	public static final String defaultWorkAmEndTime = "12:00";
	public static final String defaultWorkPmBeginTime = "13:00";
	public static final String defaultWorkPmEndTime = "18:00";
	public static final String defaultIsWork = "1";
	public static final String defaultIsNotWork = "0";

	public void initCache() {
		// workTimeCache = new HashMap<Long, ArrayList<WorkTimeCacheEntity>>();
		CacheAccessable factory = CacheFactory
				.getInstance(com.seeyon.v3x.worktimeset.manager.WorkTimeSetManagerImpl.class);
		workTimeCache = factory.createMap("workTimeCache");
		// 加载全部的单位和年(含集团和默认数据)
		List<Object[]> currencyOrgAccountAndYear = workSetDao
				.currencyOrgAccountAndYear();
		for (Object[] o : currencyOrgAccountAndYear) {
			Long orgAccountID = (Long) o[0];
			String year = (String) o[1];

			// 工作时间
			List<WorkTimeCurrency> workCurrencies = this.workSetDao
					.getWorkTimeCurrencyFromDB(orgAccountID, Integer
							.parseInt(year), "1");
			Map<Integer, WorkTimeCurrency> workCurrencyMap = new HashMap<Integer, WorkTimeCurrency>();
			for (WorkTimeCurrency workTimeCurrency : workCurrencies) {
				workCurrencyMap.put(Integer.parseInt(workTimeCurrency
						.getWeekDayName()), workTimeCurrency);
			}

			// 休息时间
			List<WorkTimeCurrency> restCurrencies = this.workSetDao
					.getWorkTimeCurrencyFromDB(orgAccountID, Integer
							.parseInt(year), "0");
			Map<Integer, WorkTimeCurrency> restCurrencyMap = new HashMap<Integer, WorkTimeCurrency>();
			for (WorkTimeCurrency workTimeCurrency : restCurrencies) {
				restCurrencyMap.put(Integer.parseInt(workTimeCurrency
						.getWeekDayName()), workTimeCurrency);
			}

			// 包装缓存对象，不包含从集团继承的区分
			WorkTimeCacheEntity cacheEntity = new WorkTimeCacheEntity();
			cacheEntity.setCurrencyWorkDaysCacheMap(workCurrencyMap);
			cacheEntity.setCurrencyRestDaysCacheMap(restCurrencyMap);
			cacheEntity.setAccountId(orgAccountID);
			cacheEntity.setYear(Integer.parseInt(year));
			if (orgAccountID.intValue() == GROUP_ADMIN_ACCOUNT.intValue()) {
				cacheEntity.setGroup(true);
			}

			ArrayList<WorkTimeCacheEntity> cacheEntities = null;
			if (workTimeCache.get(orgAccountID) != null) {
				cacheEntities = workTimeCache.get(orgAccountID);
				cacheEntities.add(cacheEntity);
				workTimeCache.notifyUpdate(orgAccountID);
			} else {
				cacheEntities = new ArrayList<WorkTimeCacheEntity>();
				cacheEntities.add(cacheEntity);
				workTimeCache.put(orgAccountID, cacheEntities);
			}

		}

		List<Object[]> specialOrgAccountAndYear = workSetDao
				.specialOrgAccountAndYear();
		for (Object[] o : specialOrgAccountAndYear) {
			Map<String, WorkTimeSpecial> specialDayMap = new HashMap<String, WorkTimeSpecial>();
			Long orgAccountID = (Long) o[0];
			String year = (String) o[1];
			// 特殊指定的工作日
			List<WorkTimeSpecial> specialDays = this.workSetDao
					.getWorkTimeSpeicalFromDB(orgAccountID, Integer
							.parseInt(year));
			for (WorkTimeSpecial workTimeSpecial : specialDays) {
				specialDayMap
						.put(workTimeSpecial.getDateNum(), workTimeSpecial);
			}
			if (workTimeCache.get(orgAccountID) != null) {
				// 缓存中存在这个单位
				List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
						.get(orgAccountID);
				// 缓存中这个单位存在这一年的数据
				WorkTimeCacheEntity workTimeCacheEntity = null;
				for (WorkTimeCacheEntity tempWorkTimeCacheEntity : unitCacheEntities) {
					if (tempWorkTimeCacheEntity.getYear().intValue() == Integer
							.parseInt(year)) {
						workTimeCacheEntity = tempWorkTimeCacheEntity;
					}
				}
				if (workTimeCacheEntity != null) {
					workTimeCacheEntity.getSpecialWorkDaysCacheMap().putAll(
							specialDayMap);
				} else {
					workTimeCacheEntity = new WorkTimeCacheEntity();
					workTimeCacheEntity.setAccountId(orgAccountID);
					if (orgAccountID.intValue() == GROUP_ADMIN_ACCOUNT
							.intValue()) {
						workTimeCacheEntity.setGroup(true);
					}
					workTimeCacheEntity.setYear(Integer.parseInt(year));
					workTimeCacheEntity
							.setSpecialWorkDaysCacheMap(specialDayMap);
					// 没有这一年的缓存，将这一年的缓存加入
					unitCacheEntities.add(workTimeCacheEntity);
					// 对缓存排序
					Collections.sort(unitCacheEntities);
					workTimeCache.notifyUpdate(orgAccountID);
				}
			} else {
				// 缓存中没有这个单位，说明此单位只有特殊工作日的设置
				ArrayList<WorkTimeCacheEntity> unitCacheEntities = new ArrayList<WorkTimeCacheEntity>();
				WorkTimeCacheEntity cacheEntity = new WorkTimeCacheEntity();
				cacheEntity.setSpecialWorkDaysCacheMap(specialDayMap);

				cacheEntity.setAccountId(orgAccountID);
				cacheEntity.setYear(Integer.parseInt(year));
				if (orgAccountID.intValue() == GROUP_ADMIN_ACCOUNT.intValue()) {
					cacheEntity.setGroup(true);
				}
				unitCacheEntities.add(cacheEntity);
				// 对缓存排序
				Collections.sort(unitCacheEntities);
				workTimeCache.put(orgAccountID, unitCacheEntities);
			}
		}

		// 增加定时任务
		try {
			// 生成新的任务之前，确认一下数据表中是否已经存在该记录
			Scheduler sched = QuartzListener.getScheduler();
			Boolean isRepeat = false;
			if (sched != null) {
				String[] triggerGroups = sched.getTriggerGroupNames();
				for (int i = 0; i < triggerGroups.length; i++) {
					if (triggerGroupName.equals(triggerGroups[i])) {
						String[] triggers = sched
								.getTriggerNames(triggerGroups[i]);
						for (int j = 0; j < triggers.length; j++) {
							Trigger tg = sched.getTrigger(triggers[j],
									triggerGroups[i]);
							if (tg instanceof CronTrigger
									&& tg.getFullName().equals(
											triggerGroupName + "."
													+ triggerName)) {
								sched.rescheduleJob(triggers[j],
										triggerGroups[i], tg);
								isRepeat = true;
								continue;
							}
						}
					}
				}
			}
			log.debug(isRepeat);
			if (!isRepeat) {
				log.info(isRepeat);
				CronTrigger cronTrigger = new CronTrigger(triggerName,
						triggerGroupName);
				try {
					CronExpression cexp = new CronExpression("59 59 23 31 12 ?");// 每年的12月31日的23:59:59执行
					cronTrigger.setCronExpression(cexp);
				} catch (Exception e) {
					log.error("", e);
				}
				JobDetail job = new JobDetail(jobName, jobGroupName,
						WorkTimeCurrencySaveHelp.class);
				job.setJobDataMap(new JobDataMap());
				sched.scheduleJob(job, cronTrigger);
			}
		} catch (Exception e) {
			log.error("", e);
		}

	}

	// 是否被缓存
	private boolean isCached(Integer year, Long orgAccountID, boolean isGroup) {
		boolean isCached = false;
		if (workTimeCache == null) {
			// 缓存没有初始化
			isCached = false;
		} else {
			if (workTimeCache.contains(orgAccountID)) {
				// 缓存中存在这个单位
				List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
						.get(orgAccountID);
				// 缓存中这个单位存在这一年的数据
				for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
					if (workTimeCacheEntity.getYear().intValue() == year) {
						isCached = true;
						break;
					}
				}
			} else {
				// 缓存中没有这个单位
				isCached = false;
			}
		}
		return isCached;
	}

	/**
	 * 修改加载的默认设置的id，year，oraAccountId
	 * 
	 * @param year
	 * @param tempList
	 * @param orgAcconutID
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	private List<WorkTimeCurrency> changeReturnMapIdAndYear(Integer year,
			List<WorkTimeCurrency> tempList, long orgAcconutID)
			throws WorkTimeSetExecption {
		List<WorkTimeCurrency> returnList = new ArrayList<WorkTimeCurrency>();
		for (WorkTimeCurrency tempWorkTimeCurrency : tempList) {
			WorkTimeCurrency workTimeCurrency = new WorkTimeCurrency();
			try {
				workTimeCurrency = (WorkTimeCurrency) tempWorkTimeCurrency
						.clone();
			} catch (CloneNotSupportedException e) {
				log.error("复制工作日、休息日对象错误", e);
				throw new WorkTimeSetExecption(e);
			}
			workTimeCurrency.setId(UUIDLong.longUUID());
			workTimeCurrency.setYear(Integer.toString(year));
			workTimeCurrency.setOrgAcconutID(orgAcconutID);

			returnList.add(workTimeCurrency);
		}
		return returnList;
	}


	public List<WorkTimeCurrency> findComnRestDaySet(Integer year,
			Long orgAccountID, boolean isGroup, Integer month)
			throws WorkTimeSetExecption {
		List<WorkTimeCurrency> returnList = new ArrayList<WorkTimeCurrency>();
		if (this.isCached(year, orgAccountID, isGroup)) {
			// 有今年的缓存数据，使用今年的缓存数据
			// 缓存中存在这个单位
			List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
					.get(orgAccountID);
			// 缓存中这个单位存在这一年的数据
			// 当年的数据
			Map<Integer, WorkTimeCurrency> currencyRestDays = null;
			for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
				if (workTimeCacheEntity.getYear().intValue() == year) {
					currencyRestDays = workTimeCacheEntity
							.getCurrencyRestDaysCacheMap();
					break;
				}
			}
			if (currencyRestDays == null || currencyRestDays.size() == 0) {
				// 有今年的缓存，但是没有通用的时间设置的数据
				if (isGroup) {
					// 集团
					// 集团的缓存
					currencyRestDays = this.getWrappedWorkTimeCacheEntity(year,
							GROUP_ADMIN_ACCOUNT).getCurrencyRestDaysCacheMap();
				} else {
					// 单位
					// 单位的缓存
					currencyRestDays = this.getWrappedWorkTimeCacheEntity(year,
							orgAccountID).getCurrencyRestDaysCacheMap();
				}
			}
			for (Iterator iterator = currencyRestDays.values().iterator(); iterator
					.hasNext();) {
				WorkTimeCurrency workTimeCurrency = (WorkTimeCurrency) iterator
						.next();
				returnList.add(workTimeCurrency);
			}

		} else {
			// 缓存中没有这个year

			WorkTimeCacheEntity tempWorkTimeCacheEntity = null;

			if (isGroup) {
				// 集团
				// 集团的缓存
				tempWorkTimeCacheEntity = this.getWrappedWorkTimeCacheEntity(
						year,
						GROUP_ADMIN_ACCOUNT);
			} else {
				// 单位
				// 单位的缓存
				tempWorkTimeCacheEntity = this.getWrappedWorkTimeCacheEntity(
						year,
						orgAccountID);
			}

			Map<Integer, WorkTimeCurrency> currencyRestDays = new HashMap<Integer, WorkTimeCurrency>();
			currencyRestDays = tempWorkTimeCacheEntity
					.getCurrencyRestDaysCacheMap();
			for (Iterator iterator = currencyRestDays.values().iterator(); iterator
					.hasNext();) {
				WorkTimeCurrency workTimeCurrency = (WorkTimeCurrency) iterator
						.next();
				returnList.add(workTimeCurrency);
			}
		}
		return this.changeReturnMapIdAndYear(year, returnList, orgAccountID);
	}

	public String findComnRestDaySet(String year, String month,
			Long orgAcconutID, boolean isGroupAdmin)
			throws WorkTimeSetExecption {
		StringBuffer resultString = new StringBuffer();
		// 获得当年的通用设置的休息日
		List<WorkTimeCurrency> comonRestDays = this.findComnRestDaySet(Integer
				.parseInt(year), orgAcconutID, isGroupAdmin, Integer
				.parseInt(month));
		for (int i = 0; i < comonRestDays.size(); i++) {
			WorkTimeCurrency workTimeCurrency = comonRestDays.get(i);
			// 格式为："6,0,...";0为星期日，是休息日的星期的连接串
			String isWork = workTimeCurrency.getIsWork();
			if ((!Strings.isBlank(isWork)) && ("0".equals(isWork))) {
				// 是休息日
				resultString.append(workTimeCurrency.getWeekDayName());
				if ((comonRestDays.size() - i) != 1) {
					resultString.append(",");
				}
			}
		}
		return resultString.toString();
	}

	public Map<String, WorkTimeCurrency> findComnWorkDaySet(Integer year,
			Long orgAcconutID, boolean isGroupAdmin, Integer month)
			throws WorkTimeSetExecption {
		List<WorkTimeCurrency> tempList = new ArrayList<WorkTimeCurrency>();
		Map<String, WorkTimeCurrency> returnMap = new HashMap<String, WorkTimeCurrency>();
		tempList = this.findComnWorkTimeSet(year, orgAcconutID, isGroupAdmin,
				month);
		for (WorkTimeCurrency workTimeCurrency : tempList) {
			if ((workTimeCurrency.getYear() != null)
					&& (year == Integer.parseInt(workTimeCurrency.getYear()))) {
				// 这一年的工作时间设置
				String weekNum = workTimeCurrency.getWeekDayName();
				returnMap.put(weekNum, workTimeCurrency);
			}
		}
		return returnMap;
	}

	public WorkTimeCurrency findComnWorkTimeSet(String year, String month,
			Long orgAcconutID, boolean isGroupAdmin)
			throws WorkTimeSetExecption {
		List<WorkTimeCurrency> tempList = this.findComnWorkTimeSet(Integer
				.parseInt(year), orgAcconutID, isGroupAdmin, Integer
				.parseInt(month));
		// 没有工作日
		if (tempList == null || tempList.size() == 0) {
			tempList = this.findComnRestDaySet(Integer.parseInt(year),
					orgAcconutID, isGroupAdmin, Integer.parseInt(month));
		}
		return tempList.get(0);
	}

	private WorkTimeCacheEntity getWorkTimeCacheEntity(Integer year,
			Long orgAccountID) {
		List<WorkTimeCacheEntity> tempCacheEntities = workTimeCache
				.get(orgAccountID);
		WorkTimeCacheEntity returnWorkTimeCacheEntity = null;
		WorkTimeCacheEntity tempWorkTimeCacheEntity = null;

		if (orgAccountID.longValue() == GROUP_ADMIN_ACCOUNT.longValue()) {
			// 是集团，取集团的设置
			tempWorkTimeCacheEntity = getGroupWorkTimeCacheEntity(year,
					tempCacheEntities, orgAccountID, true);
		} else {
			// 是单位，取单位的设置
			tempWorkTimeCacheEntity = getUnitWorkTimeCacheEntity(year,
					tempCacheEntities, orgAccountID);
		}
		returnWorkTimeCacheEntity = tempWorkTimeCacheEntity;
		return returnWorkTimeCacheEntity;
	}

	/**
	 * 取得单位的设置
	 * 
	 * @param year
	 * @param tempCacheEntities
	 * @param orgAccountID
	 */
	private WorkTimeCacheEntity getUnitWorkTimeCacheEntity(Integer year,
			List<WorkTimeCacheEntity> tempCacheEntities, Long orgAccountID) {
		int minUnitYear = 0;
		WorkTimeCacheEntity tempWorkTimeCacheEntity = null;
		if (tempCacheEntities != null && tempCacheEntities.size() > 0) {
			// 缓存中单位最老的数据的年
			minUnitYear = tempCacheEntities.get(tempCacheEntities.size() - 1)
					.getYear().intValue();
			for (int i = year; i >= minUnitYear; i--) {
				boolean stepOut = false;
				for (WorkTimeCacheEntity tempUnitWorkTimeCacheEntity : tempCacheEntities) {
					if (tempUnitWorkTimeCacheEntity.getYear().intValue() <= i) {
						if (!(tempUnitWorkTimeCacheEntity
								.getCurrencyRestDaysCacheMap().size() == 0 && tempUnitWorkTimeCacheEntity
								.getCurrencyWorkDaysCacheMap().size() == 0)) {
							tempWorkTimeCacheEntity = tempUnitWorkTimeCacheEntity;
							stepOut = true;
							break;
						}
					}
				}
				if (stepOut) {
					break;
				}
			}
		}
		if (tempWorkTimeCacheEntity == null) {
			// 没有设置，使用集团的
			List<WorkTimeCacheEntity> tempGroupCacheEntities = workTimeCache
					.get(GROUP_ADMIN_ACCOUNT);
			tempWorkTimeCacheEntity = getGroupWorkTimeCacheEntity(year,
					tempGroupCacheEntities, orgAccountID, true);
		}
		return tempWorkTimeCacheEntity;
	}

	/**
	 * 取得集团的设置
	 * 
	 * @param year
	 * @param tempCacheEntities
	 * @param orgAccountID
	 * @param isGroup
	 *            是否是集团管理员
	 * 
	 */
	private WorkTimeCacheEntity getGroupWorkTimeCacheEntity(Integer year,
			List<WorkTimeCacheEntity> tempCacheEntities, Long orgAccountID,
			boolean isGroup) {
		WorkTimeCacheEntity tempWorkTimeCacheEntity = null;
		int minGroupYear = 0;
		if (tempCacheEntities != null && tempCacheEntities.size() > 0) {
			// 缓存中集团最老的数据的年
			minGroupYear = tempCacheEntities.get(tempCacheEntities.size() - 1)
					.getYear().intValue();
		}
		for (int i = year; i >= minGroupYear; i--) {
			boolean stepOut = false;
			for (WorkTimeCacheEntity tempUnitWorkTimeCacheEntity : tempCacheEntities) {
				if (tempUnitWorkTimeCacheEntity.getYear().intValue() <= i) {
					if (!(tempUnitWorkTimeCacheEntity
							.getCurrencyRestDaysCacheMap().size() == 0 && tempUnitWorkTimeCacheEntity
							.getCurrencyWorkDaysCacheMap().size() == 0)) {
						tempWorkTimeCacheEntity = tempUnitWorkTimeCacheEntity;
						stepOut = true;
						break;
					}
				}
			}
			if (stepOut) {
				break;
			}
		}
		// 判断，缓存中集团最老的数据是否完整（至少包含工作时间设置）
		// 如果没有，取默认设置填充（目的是防止通过修改服务器时间，对系统初始化之前的年进行只录入工作日的操作）
		// 最老的数据，工作时间设置中的工作时间和休息时间同时都不存在，填充
		if (tempWorkTimeCacheEntity == null
				|| (((tempWorkTimeCacheEntity.getCurrencyWorkDaysCacheMap() != null) && (tempWorkTimeCacheEntity
						.getCurrencyWorkDaysCacheMap().size() == 0)) && ((tempWorkTimeCacheEntity
						.getCurrencyRestDaysCacheMap() != null) && (tempWorkTimeCacheEntity
						.getCurrencyRestDaysCacheMap().size() == 0)))) {
			// 使用默认值进行填充，默认值每次调用新new
			tempWorkTimeCacheEntity = fitGroupWorkTimeCacheEntityWithDefault(
					tempWorkTimeCacheEntity, orgAccountID, year.toString());
			tempWorkTimeCacheEntity.setAccountId(orgAccountID);
			tempWorkTimeCacheEntity.setYear(year);
			tempWorkTimeCacheEntity.setGroup(isGroup);
		}
		return tempWorkTimeCacheEntity;
	}

	/**
	 * 使用默认值填充集团缓存数据，只在查看系统初始化以前的年时使用
	 * 
	 * @param tempWorkTimeCacheEntity
	 * @param currencyAccount
	 *            操作账号
	 * @param currencyYear
	 *            操作年
	 */
	private WorkTimeCacheEntity fitGroupWorkTimeCacheEntityWithDefault(
			WorkTimeCacheEntity tempWorkTimeCacheEntity, Long currencyAccount,
			String currencyYear) {
		WorkTimeCacheEntity returnWorkTimeCacheEntity = null;
		if (tempWorkTimeCacheEntity == null) {
			returnWorkTimeCacheEntity = new WorkTimeCacheEntity();
		} else {
			returnWorkTimeCacheEntity = tempWorkTimeCacheEntity;
		}

		// 使用新构建默认数据的方式，提供给前台进行展示
		Map<Integer, WorkTimeCurrency> currencyWorkDaysCacheMap = new HashMap<Integer, WorkTimeCurrency>();
		Map<Integer, WorkTimeCurrency> currencyRestDaysCacheMap = new HashMap<Integer, WorkTimeCurrency>();
		for (int i = 0; i < 7; i++) {
			WorkTimeCurrency tempWorkTimeCurrency = new WorkTimeCurrency();
			tempWorkTimeCurrency.setIdIfNew();
			tempWorkTimeCurrency.setOrgAcconutID(currencyAccount);
			tempWorkTimeCurrency.setAmWorkTimeBeginTime(defaultWorkAmBeginTime);
			tempWorkTimeCurrency.setAmWorkTimeEndTime(defaultWorkAmEndTime);
			tempWorkTimeCurrency.setPmWorkTimeBeginTime(defaultWorkPmBeginTime);
			tempWorkTimeCurrency.setPmWorkTimeEndTime(defaultWorkPmEndTime);
			tempWorkTimeCurrency.setWeekDayName(Integer.toString(i));
			tempWorkTimeCurrency.setYear(currencyYear);
			if (i == 0 || i == 6) {
				// 默认周六周日休息
				tempWorkTimeCurrency.setIsWork(defaultIsNotWork);
				currencyRestDaysCacheMap.put(i, tempWorkTimeCurrency);
			} else {
				tempWorkTimeCurrency.setIsWork(defaultIsWork);
				currencyWorkDaysCacheMap.put(i, tempWorkTimeCurrency);
			}
		}
		returnWorkTimeCacheEntity
				.setCurrencyRestDaysCacheMap(currencyRestDaysCacheMap);
		returnWorkTimeCacheEntity
				.setCurrencyWorkDaysCacheMap(currencyWorkDaysCacheMap);

		return returnWorkTimeCacheEntity;
	}

	private WorkTimeCacheEntity getWrappedWorkTimeCacheEntity(Integer year,
			Long accountId) {
		WorkTimeCacheEntity tempWorkTimeCacheEntity = new WorkTimeCacheEntity();
		tempWorkTimeCacheEntity = getWorkTimeCacheEntity(year, accountId);

		WorkTimeCacheEntity returnWorkTimeCacheEntity = new WorkTimeCacheEntity();
		returnWorkTimeCacheEntity.setAccountId(tempWorkTimeCacheEntity
				.getAccountId());
		returnWorkTimeCacheEntity
				.setCurrencyRestDaysCacheMap(tempWorkTimeCacheEntity
						.getCurrencyRestDaysCacheMap());
		returnWorkTimeCacheEntity
				.setCurrencyWorkDaysCacheMap(tempWorkTimeCacheEntity
						.getCurrencyWorkDaysCacheMap());
		returnWorkTimeCacheEntity.setGroup(tempWorkTimeCacheEntity.isGroup());
		returnWorkTimeCacheEntity.setYear(tempWorkTimeCacheEntity.getYear());

		return returnWorkTimeCacheEntity;
	}

	public List<WorkTimeCurrency> findComnWorkTimeSet(Integer year,
			Long orgAccountID, boolean isGroup, Integer month)
			throws WorkTimeSetExecption {
		List<WorkTimeCurrency> returnList = new ArrayList<WorkTimeCurrency>();
		if (this.isCached(year, orgAccountID, isGroup)) {
			// 有今年的缓存数据，使用今年的缓存数据
			// 缓存中存在这个单位
			List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
					.get(orgAccountID);
			// 缓存中这个单位存在这一年的数据
			// 当年的数据
			Map<Integer, WorkTimeCurrency> currencyWorkDays = new HashMap<Integer, WorkTimeCurrency>();
			for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
				if (workTimeCacheEntity.getYear().intValue() == year) {
					currencyWorkDays = workTimeCacheEntity
							.getCurrencyWorkDaysCacheMap();
					break;
				}
			}
			if (currencyWorkDays.size() == 0 || currencyWorkDays == null) {
				// 有今年的缓存，但是没有通用的时间设置的数据
				if (isGroup) {
					// 集团
					// 集团的缓存
					currencyWorkDays = this.getWrappedWorkTimeCacheEntity(year,
							GROUP_ADMIN_ACCOUNT).getCurrencyWorkDaysCacheMap();
				} else {
					// 单位
					// 单位的缓存
					currencyWorkDays = this.getWrappedWorkTimeCacheEntity(year,
							orgAccountID).getCurrencyWorkDaysCacheMap();
				}
			}
			for (Iterator iterator = currencyWorkDays.values().iterator(); iterator
					.hasNext();) {
				WorkTimeCurrency workTimeCurrency = (WorkTimeCurrency) iterator
						.next();
				returnList.add(workTimeCurrency);
			}

		} else {
			// 缓存中没有这个year，

			WorkTimeCacheEntity tempWorkTimeCacheEntity = null;

			if (isGroup) {
				// 集团
				// 集团的缓存
				tempWorkTimeCacheEntity = this.getWrappedWorkTimeCacheEntity(
						year,
						GROUP_ADMIN_ACCOUNT);
			} else {
				// 单位
				// 单位的缓存
				tempWorkTimeCacheEntity = this.getWrappedWorkTimeCacheEntity(
						year,
						orgAccountID);
			}

			Map<Integer, WorkTimeCurrency> currencyWorkDays = new HashMap<Integer, WorkTimeCurrency>();
			currencyWorkDays = tempWorkTimeCacheEntity
					.getCurrencyWorkDaysCacheMap();
			for (Iterator iterator = currencyWorkDays.values().iterator(); iterator
					.hasNext();) {
				WorkTimeCurrency workTimeCurrency = (WorkTimeCurrency) iterator
						.next();
				returnList.add(workTimeCurrency);
			}

		}
		returnList = this.changeReturnMapIdAndYear(year, returnList,
				orgAccountID);

		return returnList;
	}

	public String findSpecialWorkDaySet(String year, String month,
			Long orgAcconutID, boolean isGroupAdmin)
			throws WorkTimeSetExecption {
		User user = CurrentUser.get();
		StringBuffer resultString = new StringBuffer();
		// 获得当年当月的特殊工作日
		List<WorkTimeSpecial> specialWorkDays = this.findSpicalWorkDaySet(
				Integer.parseInt(year), orgAcconutID, isGroupAdmin, Integer
						.parseInt(month));
		for (int i = 0; i < specialWorkDays.size(); i++) {
			WorkTimeSpecial workTimeSpecial = specialWorkDays.get(i);
			// 格式为"2010/09/30:id:flag:info↗2010/09/30:id:flag:info↗..."
			resultString.append(workTimeSpecial.getDateNum());// "2010/10/01"
			resultString.append(":");
			resultString.append(workTimeSpecial.getId());
			resultString.append(":");
			resultString.append(workTimeSpecial.getIsRest());
			resultString.append(":");
			if (Strings.isBlank(workTimeSpecial.getRestInfo())) {
				resultString.append("");
			} else {
				resultString.append(workTimeSpecial.getRestInfo());
			}
			if ((specialWorkDays.size() - i) != 1) {
				resultString.append("↗");
			}
		}
		return resultString.toString();
	}

	public Map<String, WorkTimeSpecial> findSpicalWorkDayMap(Integer year,
			Long orgAccountID, boolean isGroup, Integer month)
			throws WorkTimeSetExecption {
		Map<String, WorkTimeSpecial> specialWorkDays = new HashMap<String, WorkTimeSpecial>();
		// 集团的特殊工作日设置
		Map<String, WorkTimeSpecial> groupWorkDays = new HashMap<String, WorkTimeSpecial>();
		if (this.isCached(year, GROUP_ADMIN_ACCOUNT, isGroup)) {
			// 有今年的缓存数据，使用今年的缓存数据
			List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
					.get(GROUP_ADMIN_ACCOUNT);
			// 当年的数据
			for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
				if (workTimeCacheEntity.getYear().intValue() == year) {
					groupWorkDays = workTimeCacheEntity
							.getSpecialWorkDaysCacheMap();
					break;
				}
			}
		}
		if (!isGroup) {
			// 单位的特殊工作日
			Map<String, WorkTimeSpecial> unitSpecialWorkDays = new HashMap<String, WorkTimeSpecial>();
			if (this.isCached(year, orgAccountID, isGroup)) {
				// 有今年的缓存数据，使用今年的缓存数据
				// 缓存中存在这个单位
				List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
						.get(orgAccountID);
				// 缓存中这个单位存在这一年的数据
				// 当年的数据
				for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
					if (workTimeCacheEntity.getYear().intValue() == year) {
						unitSpecialWorkDays = workTimeCacheEntity
								.getSpecialWorkDaysCacheMap();
						break;
					}
				}
			}
			// 集团，单位放在一起
			// specialWorkDays.putAll(groupWorkDays);
			specialWorkDays.putAll(unitSpecialWorkDays);
		} else {
			specialWorkDays.putAll(groupWorkDays);
		}
		return specialWorkDays;
	}

	/**
	 * 查找特殊设置的工作日
	 * 
	 * @param year
	 * @param orgAccountID
	 * @param isGroup
	 * @param month
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	private List<WorkTimeSpecial> findSpicalWorkDaySet(Integer year,
			Long orgAccountID, boolean isGroup, Integer month)
			throws WorkTimeSetExecption {
		List<WorkTimeSpecial> returnList = new ArrayList<WorkTimeSpecial>();
		// 当年的数据
		Map<String, WorkTimeSpecial> spicalWorkDays = new HashMap<String, WorkTimeSpecial>();
		spicalWorkDays = this.findSpicalWorkDayMap(year, orgAccountID, isGroup,
				month);
		for (Iterator iterator = spicalWorkDays.values().iterator(); iterator
				.hasNext();) {
			WorkTimeSpecial workTimeSpecial = (WorkTimeSpecial) iterator.next();
			if ((!Strings.isBlank(workTimeSpecial.getMonth()))
					&& ((workTimeSpecial.getMonth()).equals(Integer
							.toString(month)))) {
				returnList.add(workTimeSpecial);
			}
		}
		return returnList;
	}

	public void updateComnWorkDayTimeSet(String year, String workDays,
			String workAmBeginTime, String workAmEndTime,
			String workPmBeginTime, String workPmEndTime, Long orgAccountID,
			boolean isGroupAdmin, boolean copyCurrencyTimeFlag, Integer month)
			throws WorkTimeSetExecption {
		Calendar dateCalendar = Calendar.getInstance();
		// 年月时间格式"yyyy/MM/dd HH:mm:ss"
		log.info("用户：" + orgAccountID.toString() + "于"
				+ Datetimes.format(dateCalendar.getTime(), "yyyy/MM/dd HH:mm:ss") + "时，更新了工作时间设置。");
		List<WorkTimeCurrency> workTimeCurrencies = this.workSetDao
				.comonWorkDayTimeIds(year, orgAccountID);
		boolean isUpdate = false;
		// 数据库已经存在记录
		if (workTimeCurrencies != null && workTimeCurrencies.size() > 0) {
			isUpdate = true;
		}
		String[] beginTime = workAmBeginTime.split(":");
		String[] endTime = workPmEndTime.split(":");
		List<WorkTimeCurrency> workTimeCurrencies4Change = new ArrayList<WorkTimeCurrency>();
		String[] workDaysStrArray = workDays.split(",");

		if (isUpdate) {
			// 存在工作时间设置 更新
			for (int i = 0; i < workDaysStrArray.length; i++) {
				WorkTimeCurrency tempworktWorkTimeCurrency = workTimeCurrencies
						.get(i);
				tempworktWorkTimeCurrency.setIsWork(workDaysStrArray[i]);// 是否工作
				String weekDayName = Integer.toString(i);// 周几
				tempworktWorkTimeCurrency.setYear(year);
				tempworktWorkTimeCurrency.setWeekDayName(weekDayName);
				tempworktWorkTimeCurrency.setOrgAcconutID(orgAccountID);
				// 工作时间
				tempworktWorkTimeCurrency
						.setAmWorkTimeBeginTime(workAmBeginTime);
				tempworktWorkTimeCurrency.setAmWorkTimeEndTime(workAmEndTime);
				tempworktWorkTimeCurrency
						.setPmWorkTimeBeginTime(workPmBeginTime);
				tempworktWorkTimeCurrency.setPmWorkTimeEndTime(workPmEndTime);
				tempworktWorkTimeCurrency.setUpdateTime(new Timestamp(
						dateCalendar.getTimeInMillis()));
				workTimeCurrencies4Change.add(tempworktWorkTimeCurrency);
			}
			this.updateComnWorkDayTimeSet(workTimeCurrencies4Change,
					orgAccountID, Integer.parseInt(year), isGroupAdmin);
			this.saveWorkTime(beginTime, endTime, orgAccountID);
		} else {
			// 不存在工作时间设置 插入
			for (int i = 0; i < workDaysStrArray.length; i++) {
				WorkTimeCurrency tempworktWorkTimeCurrency = new WorkTimeCurrency();
				tempworktWorkTimeCurrency.setIdIfNew();
				tempworktWorkTimeCurrency.setIsWork(workDaysStrArray[i]);// 是否工作
				String weekDayName = Integer.toString(i);// 周几
				tempworktWorkTimeCurrency.setYear(year);
				tempworktWorkTimeCurrency.setWeekDayName(weekDayName);
				tempworktWorkTimeCurrency.setOrgAcconutID(orgAccountID);
				// 工作时间
				tempworktWorkTimeCurrency
						.setAmWorkTimeBeginTime(workAmBeginTime);
				tempworktWorkTimeCurrency.setAmWorkTimeEndTime(workAmEndTime);
				tempworktWorkTimeCurrency
						.setPmWorkTimeBeginTime(workPmBeginTime);
				tempworktWorkTimeCurrency.setPmWorkTimeEndTime(workPmEndTime);
				tempworktWorkTimeCurrency.setUpdateTime(new Timestamp(
						dateCalendar.getTimeInMillis()));
				workTimeCurrencies4Change.add(tempworktWorkTimeCurrency);
			}
			this.insertComnWorkDayTimeSet(workTimeCurrencies4Change,
					orgAccountID, Integer.parseInt(year), isGroupAdmin);
			this.saveWorkTime(beginTime, endTime, orgAccountID);
		}
		if (copyCurrencyTimeFlag && isGroupAdmin) {
			// 是集团管理员，同时选择了将集团的时间设置复制给单位
			// 取得集团下的全部单位，调用copyCurrenctTimeFormGroupToUnit方法更新
			try {
				for (V3xOrgAccount v3xOrgAccount : OrgManager
						.getAllAccounts()) {
					if (v3xOrgAccount.getOrgAccountId().intValue() != GROUP_ADMIN_ACCOUNT
							.intValue()) {
						this.copyCurrenctTimeFormGroupToUnit(v3xOrgAccount
								.getOrgAccountId(), year, month);
						this.saveWorkTime(beginTime, endTime, v3xOrgAccount.getOrgAccountId());
					}
				}
			} catch (Exception e) {
				log.error("更新集团下属单位的工作时间异常.", e);
				throw new WorkTimeSetExecption(e);
			}
		}
	}
	
	/**
	 * 保存或更新单位考勤工作时间
	 * @param beginTime
	 * @param endTime
	 * @param accountId
	 */
	@SuppressWarnings("unchecked")
	public void saveWorkTime(String [] beginTime, String [] endTime, Long accountId) {
		RecordWorkingTime workingTime = new RecordWorkingTime();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		List<RecordWorkingTime> list = workSetDao.find("From RecordWorkingTime where accountId = :accountId", -1, -1, params);
		if(CollectionUtils.isNotEmpty(list)) {
			workingTime = list.get(0);
		}
		workingTime.setBegin_hour(Integer.parseInt(beginTime[0]));
		workingTime.setBegin_minute(Integer.parseInt(beginTime[1]));
		workingTime.setEnd_hour(Integer.parseInt(endTime[0]));
		workingTime.setEnd_minute(Integer.parseInt(endTime[1]));
		workingTime.setAccountId(accountId);
		try {
			recordManager.setWorkingTime(workingTime);
		} catch (Exception e) {
			log.error("更新单位的考勤工作时间异常.", e);
		}
	}

	/**
	 * 保存通用工作时间设置
	 * 
	 * @param workTimeCurrencies
	 * @param orgAccountID
	 * @param year
	 * @param isGroupAdmin
	 */
	private void insertComnWorkDayTimeSet(
			List<WorkTimeCurrency> workTimeCurrencies, Long orgAccountID,
			int year, boolean isGroupAdmin) {
		// 取得本年本单位的缓存
		WorkTimeCacheEntity workTimeCacheEntity = null;
		ArrayList<WorkTimeCacheEntity> unitCacheEntities = new ArrayList<WorkTimeCacheEntity>();
		if ((workTimeCache != null)
				&& (workTimeCache.get(orgAccountID) != null)) {
			unitCacheEntities = workTimeCache.get(orgAccountID);
			if (unitCacheEntities.size() != 0) {
				for (WorkTimeCacheEntity tempCacheEntity : unitCacheEntities) {
					if (tempCacheEntity.getYear().intValue() == year) {
						workTimeCacheEntity = tempCacheEntity;
					}
				}
			}
		}
		if (workTimeCacheEntity == null) {
			workTimeCacheEntity = new WorkTimeCacheEntity();
		}
		for (WorkTimeCurrency workTimeCurrency : workTimeCurrencies) {
			this.workSetDao.save(workTimeCurrency);
			// 更新缓存
			if ((!Strings.isBlank(workTimeCurrency.getIsWork()))
					&& ("1".equals(workTimeCurrency.getIsWork()))) {
				// 工作日
				workTimeCacheEntity.getCurrencyWorkDaysCacheMap().put(
						Integer.parseInt(workTimeCurrency.getWeekDayName()),
						workTimeCurrency);
				// 将这一天从非工作日中移除
				if (workTimeCacheEntity.getCurrencyRestDaysCacheMap().get(
						Integer.parseInt(workTimeCurrency.getWeekDayName())) != null) {
					workTimeCacheEntity.getCurrencyRestDaysCacheMap()
							.remove(
									Integer.parseInt(workTimeCurrency
											.getWeekDayName()));
				}
			} else {
				// 非工作日
				workTimeCacheEntity.getCurrencyRestDaysCacheMap().put(
						Integer.parseInt(workTimeCurrency.getWeekDayName()),
						workTimeCurrency);
				// 将这一天从工作日中移除
				if (workTimeCacheEntity.getCurrencyWorkDaysCacheMap().get(
						Integer.parseInt(workTimeCurrency.getWeekDayName())) != null) {
					workTimeCacheEntity.getCurrencyWorkDaysCacheMap()
							.remove(
									Integer.parseInt(workTimeCurrency
											.getWeekDayName()));
				}
			}
		}
		workTimeCacheEntity.setExtendGroupCommon(false);
		workTimeCacheEntity.setAccountId(orgAccountID);
		workTimeCacheEntity.setGroup(isGroupAdmin);
		workTimeCacheEntity.setYear(year);

		if (unitCacheEntities.size() != 0) {
			unitCacheEntities.add(workTimeCacheEntity);
			// 通用时间设置插入后，对此单位的缓存进行排序
			Collections.sort(unitCacheEntities);
		} else {
			unitCacheEntities.add(workTimeCacheEntity);
			// 通用时间设置插入后，对此单位的缓存进行排序
			Collections.sort(unitCacheEntities);
			workTimeCache.put(orgAccountID, unitCacheEntities);
		}
		workTimeCache.notifyUpdate(orgAccountID);
	}

	/**
	 * 更新通用工作时间设置
	 * 
	 * @param workTimeCurrencies
	 * @param orgAccountID
	 * @param year
	 */
	private void updateComnWorkDayTimeSet(
			List<WorkTimeCurrency> workTimeCurrencies, Long orgAccountID,
			int year, boolean isGroupAdmin) {
		// 取得本年本单位的缓存
		WorkTimeCacheEntity workTimeCacheEntity = null;
		List<WorkTimeCacheEntity> unitCacheEntities = new ArrayList<WorkTimeCacheEntity>();
		if ((workTimeCache != null)
				&& (workTimeCache.get(orgAccountID) != null)) {
			unitCacheEntities = workTimeCache.get(orgAccountID);
			if (unitCacheEntities.size() != 0) {
				for (WorkTimeCacheEntity tempCacheEntity : unitCacheEntities) {
					if (tempCacheEntity.getYear().intValue() == year) {
						workTimeCacheEntity = tempCacheEntity;
					}
				}
			}
		}
		if (workTimeCacheEntity == null) {
			workTimeCacheEntity = new WorkTimeCacheEntity();
		}
		for (WorkTimeCurrency workTimeCurrency : workTimeCurrencies) {
			this.workSetDao.update(workTimeCurrency);
			// 更新缓存
			if ((!Strings.isBlank(workTimeCurrency.getIsWork()))
					&& ("1".equals(workTimeCurrency.getIsWork()))) {
				// 工作日
				workTimeCacheEntity.getCurrencyWorkDaysCacheMap().put(
						Integer.parseInt(workTimeCurrency.getWeekDayName()),
						workTimeCurrency);
				// 将这一天从非工作日中移除
				if (workTimeCacheEntity.getCurrencyRestDaysCacheMap().get(
						Integer.parseInt(workTimeCurrency.getWeekDayName())) != null) {
					workTimeCacheEntity.getCurrencyRestDaysCacheMap()
							.remove(
									Integer.parseInt(workTimeCurrency
											.getWeekDayName()));
				}
			} else {
				// 非工作日
				workTimeCacheEntity.getCurrencyRestDaysCacheMap().put(
						Integer.parseInt(workTimeCurrency.getWeekDayName()),
						workTimeCurrency);
				// 将这一天从工作日中移除
				if (workTimeCacheEntity.getCurrencyWorkDaysCacheMap().get(
						Integer.parseInt(workTimeCurrency.getWeekDayName())) != null) {
					workTimeCacheEntity.getCurrencyWorkDaysCacheMap()
							.remove(
									Integer.parseInt(workTimeCurrency
											.getWeekDayName()));
				}
			}
		}
		workTimeCache.notifyUpdate(orgAccountID);
	}

	public String syncSpecialDayFromGroupToUnit(String year, String month,
			String syncFlag, String dateNum) throws WorkTimeSetExecption {
		// 登录人的单位ID
		User user = CurrentUser.get();
		Long orgAcconutID = user.getAccountId();
		boolean isGroupAdmin = user.isGroupAdmin();
		// 取得集团的全年的工作日设置
		Map<String, WorkTimeSpecial> groupWorkTimeSpecialMap = this
				.findSpicalWorkDayMap(Integer.parseInt(year),
						GROUP_ADMIN_ACCOUNT,
				isGroupAdmin, Integer.parseInt(month));

		if (isGroupAdmin) {
			// 是集团管理员，选择了同步，则将集团的特殊工作日的设置同步给其下的全部单位
			try {
				for (V3xOrgAccount v3xOrgAccount : OrgManager
						.getAllAccounts()) {
					if (v3xOrgAccount.getOrgAccountId().intValue() != GROUP_ADMIN_ACCOUNT
							.intValue()) {

						// 取得循环到的单位的全年的工作日设置

						// 单位的特殊工作日
						Map<String, WorkTimeSpecial> unitWorkTimeSpecialMap = new HashMap<String, WorkTimeSpecial>();
						if (this.isCached(Integer.parseInt(year), v3xOrgAccount
								.getOrgAccountId(), isGroupAdmin)) {
							// 有今年的缓存数据，使用今年的缓存数据
							// 缓存中存在这个单位
							List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
									.get(v3xOrgAccount.getOrgAccountId());
							// 缓存中这个单位存在这一年的数据
							// 当年的数据
							for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
								if (workTimeCacheEntity.getYear().intValue() == Integer
										.parseInt(year)) {
									unitWorkTimeSpecialMap = workTimeCacheEntity
											.getSpecialWorkDaysCacheMap();
									break;
								}
							}
						}

						this.syncSpecialDay(unitWorkTimeSpecialMap,
								groupWorkTimeSpecialMap, v3xOrgAccount
										.getOrgAccountId(), false, year, month,
								syncFlag, dateNum);
					}

				}
			} catch (BusinessException e) {
				log.error("更新集团下属单位的工作时间异常.", e);
				throw new WorkTimeSetExecption(e);
			}
		} else {
			// 是其他管理员，选择了同步，则将集团的特殊工作日的设置同步给本管理员所属单位

			// 取得当前登录单位的全年的工作日设置
			Map<String, WorkTimeSpecial> unitWorkTimeSpecialMap = new HashMap<String, WorkTimeSpecial>();
			if (this.isCached(Integer.parseInt(year), orgAcconutID,
					isGroupAdmin)) {
				// 有今年的缓存数据，使用今年的缓存数据
				// 缓存中存在这个单位
				List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
						.get(orgAcconutID);
				// 缓存中这个单位存在这一年的数据
				// 当年的数据
				for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
					if (workTimeCacheEntity.getYear().intValue() == Integer
							.parseInt(year)) {
						unitWorkTimeSpecialMap = workTimeCacheEntity
								.getSpecialWorkDaysCacheMap();
						break;
					}
				}
			}

			this.syncSpecialDay(unitWorkTimeSpecialMap,
					groupWorkTimeSpecialMap, orgAcconutID, isGroupAdmin, year,
					month, syncFlag, dateNum);
		}
		String returnString = findSpecialWorkDaySet(year, month, orgAcconutID,
				isGroupAdmin);
		if (Strings.isBlank(returnString)) {
			returnString = "returnNullSpecialDay";
		}

		// 集团和各子单位节假日设置
		String groupAndUnitWorkDay = "";
		// 记录应用日志
		String WorkTimeSetResources = "com.seeyon.v3x.worktimeset.resources.i18n.WorkTimeSetResources";
		if (isGroupAdmin) {
			// 集团管理员修改了集团和各子单位的节假日设置
			groupAndUnitWorkDay = ResourceBundleUtil.getString(
					WorkTimeSetResources, "mr.label.GroupAndUnitWorkDay");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Update_WorkDay, user
					.getName(), groupAndUnitWorkDay);
		} else {
			// 单位管理员继承了集团节假日设置
			groupAndUnitWorkDay = ResourceBundleUtil.getString(
					WorkTimeSetResources, "mr.label.UnitWorkDay");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Inherit_Group_WorkDay,
					user.getName(), groupAndUnitWorkDay);
		}

		return returnString;
	}

	private void syncSpecialDay(
			Map<String, WorkTimeSpecial> unitWorkTimeSpecialMap,
			Map<String, WorkTimeSpecial> groupWorkTimeSpecialMap,
			long orgAcconutID, boolean isGroupAdmin, String year, String month,
			String syncFlag, String dateNum) throws WorkTimeSetExecption {
		// 格式化时间"yyyy/MM/dd"
		// 同步全年的
		if (!Strings.isBlank(syncFlag) && "0".equals(syncFlag)) {
			// 被清空的数据库记录的dateNum集合
			List<String> use4ClearSpecialDayCacheList = new ArrayList<String>();

			// 清空单位当年的当前日之前数据库记录
			for (Iterator iterator = unitWorkTimeSpecialMap.values().iterator(); iterator
					.hasNext();) {
				WorkTimeSpecial unitWorkTimeSpecial = (WorkTimeSpecial) iterator
						.next();
				// 不删集团的
				if (unitWorkTimeSpecial.getOrgAcconutID() != GROUP_ADMIN_ACCOUNT
						.longValue()) {
					if ((Datetimes.parse(unitWorkTimeSpecial.getDateNum(), "yyyy/MM/dd")
							.compareTo(Datetimes.parse(dateNum, "yyyy/MM/dd"))) >= 0) {
						// 只删除当前日期以后的工作日设置
						use4ClearSpecialDayCacheList
								.add(unitWorkTimeSpecial.getDateNum());
						this.workSetDao.delete(unitWorkTimeSpecial);
					}
				}
			}
			// 清空单位删除的工作日缓存
			for (String dateNum4Remove : use4ClearSpecialDayCacheList) {
				unitWorkTimeSpecialMap.remove(dateNum4Remove);
			}

			// 使用集团当年的工作日设置填充缓存和数据库
			for (Iterator iterator = groupWorkTimeSpecialMap.values()
					.iterator(); iterator.hasNext();) {
				WorkTimeSpecial groupWorkTimeSpecial = (WorkTimeSpecial) iterator
						.next();
				WorkTimeSpecial unitWorkTimeSpecial = null;
				try {
					unitWorkTimeSpecial = (WorkTimeSpecial) groupWorkTimeSpecial
							.clone();
					unitWorkTimeSpecial.setIdIfNew();
					unitWorkTimeSpecial.setOrgAcconutID(orgAcconutID);
					this.saveWorkTimeSpecial(orgAcconutID, Integer
							.parseInt(year), unitWorkTimeSpecial, isGroupAdmin);
				} catch (CloneNotSupportedException e) {
					log.error("复制工作日、休息日对象错误", e);
					throw new WorkTimeSetExecption(e);
				}
			}
		}
		// 同步当月的
		if (!Strings.isBlank(syncFlag) && "1".equals(syncFlag)) {
			// 被清空的数据库记录的dateNum集合
			List<String> use4ClearSpecialDayCacheList = new ArrayList<String>();

			// 清空单位当月的数据库记录
			for (Iterator iterator = unitWorkTimeSpecialMap.values().iterator(); iterator
					.hasNext();) {
				WorkTimeSpecial unitWorkTimeSpecial = (WorkTimeSpecial) iterator
						.next();
				if (!Strings.isBlank(month)
						&& month.equals(unitWorkTimeSpecial.getMonth())) {
					if (unitWorkTimeSpecial.getOrgAcconutID() != GROUP_ADMIN_ACCOUNT
							.longValue()) {
						if ((Datetimes.parse(unitWorkTimeSpecial.getDateNum(), "yyyy/MM/dd")
								.compareTo(Datetimes.parse(dateNum, "yyyy/MM/dd"))) >= 0) {
							// 只删除当前日期以后的工作日设置
							use4ClearSpecialDayCacheList
									.add(unitWorkTimeSpecial.getDateNum());
							// 取得当月的非集团工作日记录，从数据库移除
							this.workSetDao.delete(unitWorkTimeSpecial);
						}

					}
				}
			}
			// 清空单位当月的删除的缓存记录
			for (String dateNum4Remove : use4ClearSpecialDayCacheList) {
				unitWorkTimeSpecialMap.remove(dateNum4Remove);
			}

			// 使用集团当月的工作日设置填充数据库和缓存
			for (Iterator iterator = groupWorkTimeSpecialMap.values()
					.iterator(); iterator.hasNext();) {
				WorkTimeSpecial groupWorkTimeSpecial = (WorkTimeSpecial) iterator
						.next();
				WorkTimeSpecial unitWorkTimeSpecial = null;
				try {
					// 取当月的，进行数据库和缓存的填充
					if (!Strings.isBlank(month)
							&& month.equals(groupWorkTimeSpecial.getMonth())) {
						unitWorkTimeSpecial = (WorkTimeSpecial) groupWorkTimeSpecial
								.clone();
						unitWorkTimeSpecial.setIdIfNew();
						unitWorkTimeSpecial.setOrgAcconutID(orgAcconutID);
						this.saveWorkTimeSpecial(orgAcconutID, Integer
								.parseInt(year), unitWorkTimeSpecial,
								isGroupAdmin);
					}
				} catch (CloneNotSupportedException e) {
					log.error("复制工作日、休息日对象错误", e);
					throw new WorkTimeSetExecption(e);
				}
			}

		}
	}

	public String updateSpecialWorkDaySet(String year, String updateDaySetStr,
			String month) {
		// 登录人的单位ID
		User user = CurrentUser.get();
		Long orgAcconutID = user.getAccountId();
		boolean isGroupAdmin = user.isGroupAdmin();
		WorkTimeSpecial workTimeSpecial = new WorkTimeSpecial();
		// updateDaySetStr 更新串，格式"2010/09/30↗id↗flag↗info"
		String[] updateDayStrs = updateDaySetStr.split("↗");
		workTimeSpecial.setDateNum(updateDayStrs[0]);

		Calendar dateCalendar = Calendar.getInstance();
		//"yyyy/MM/dd HH:mm:ss"
		log.info("用户：" + orgAcconutID.toString() + "于"
				+ Datetimes.formatDatetime(dateCalendar.getTime()) + "时，更新了休息日设置。");
		workTimeSpecial.setMonth(month);

		workTimeSpecial.setIsRest(updateDayStrs[2]);
		if (!"info".equals(updateDayStrs[3])) {
			workTimeSpecial.setRestInfo(updateDayStrs[3]);
		} else {
			workTimeSpecial.setRestInfo("");
		}
		workTimeSpecial.setYear(year);
		workTimeSpecial.setOrgAcconutID(orgAcconutID);
		workTimeSpecial
				.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		/*
		 * // 记录应用日志，集团管理员暂时不记录 if (!isGroupAdmin) {
		 * appLogManager.insertLog(user,
		 * AppLogAction.WorkTimeSetSpecialDay_Update, user.getName()); }
		 */

		if (!"Id".equals(updateDayStrs[1])) {
			// ID不为空，更新
			workTimeSpecial.setId(Long.parseLong(updateDayStrs[1]));
			this.updateSpicalWorkDaySet(orgAcconutID, Integer.parseInt(year),
					workTimeSpecial, isGroupAdmin);
		} else {
			// 新增
			workTimeSpecial.setIdIfNew();
			this.saveWorkTimeSpecial(orgAcconutID, Integer.parseInt(year),
					workTimeSpecial, isGroupAdmin);
		}

		// 记录应用日志
		String WorkTimeSetResources = "com.seeyon.v3x.worktimeset.resources.i18n.WorkTimeSetResources";
		// 节假日设值
		String workDay = "";

		if (isGroupAdmin) {
			// 集团管理员更新集团节假日设置
			workDay = ResourceBundleUtil.getString(WorkTimeSetResources,
					"mr.label.GroupWorkDay");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Update_WorkDay, user
							.getName(),
					workDay);
		} else {
			// 单位管理员更新单位节假日设置
			workDay = ResourceBundleUtil.getString(WorkTimeSetResources,
					"mr.label.UnitWorkDay");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Update_WorkDay,
					user.getName(), workDay);
		}

		return workTimeSpecial.getDateNum() + ":"
				+ workTimeSpecial.getId().toString() + ":"
				+ workTimeSpecial.getIsRest() + ":"
				+ workTimeSpecial.getRestInfo();
	}

	/**
	 * 更新特殊设置的工作日
	 * 
	 * @param orgAcconutID
	 * @param year
	 * @param workTimeSpecial
	 */
	private void updateSpicalWorkDaySet(Long orgAcconutID, int year,
			WorkTimeSpecial workTimeSpecial, boolean isGroupAdmin) {
		this.workSetDao.update(workTimeSpecial);
		// 取得本年本单位的缓存
		WorkTimeCacheEntity workTimeCacheEntity = null;
		List<WorkTimeCacheEntity> unitCacheEntities = new ArrayList<WorkTimeCacheEntity>();
		if ((workTimeCache != null)
				&& (workTimeCache.get(orgAcconutID) != null)) {
			unitCacheEntities = workTimeCache.get(orgAcconutID);
			if (unitCacheEntities.size() != 0) {
				for (WorkTimeCacheEntity tempCacheEntity : unitCacheEntities) {
					if (tempCacheEntity.getYear().intValue() == year) {
						workTimeCacheEntity = tempCacheEntity;
					}
				}
			}
		}
		if (workTimeCacheEntity != null) {
			workTimeCacheEntity.getSpecialWorkDaysCacheMap().put(
					workTimeSpecial.getDateNum(), workTimeSpecial);
		}
	}

	/**
	 * 保存特殊设置的工作日
	 * 
	 * @param orgAcconutID
	 * @param year
	 * @param workTimeSpecial
	 */
	private void saveWorkTimeSpecial(Long orgAcconutID, int year,
			WorkTimeSpecial workTimeSpecial, boolean isGroupAdmin) {
		this.workSetDao.save(workTimeSpecial);
		// 取得本年本单位的缓存
		WorkTimeCacheEntity workTimeCacheEntity = null;
		ArrayList<WorkTimeCacheEntity> unitCacheEntities = new ArrayList<WorkTimeCacheEntity>();
		if ((workTimeCache != null)
				&& (workTimeCache.get(orgAcconutID) != null)) {
			unitCacheEntities = workTimeCache.get(orgAcconutID);
			if (unitCacheEntities.size() != 0) {
				for (WorkTimeCacheEntity tempCacheEntity : unitCacheEntities) {
					if (tempCacheEntity.getYear().intValue() == year) {
						workTimeCacheEntity = tempCacheEntity;
					}
				}
			}
			if (workTimeCacheEntity != null) {
				workTimeCacheEntity.getSpecialWorkDaysCacheMap().put(
						workTimeSpecial.getDateNum(), workTimeSpecial);
			} else {
				workTimeCacheEntity = new WorkTimeCacheEntity();
				workTimeCacheEntity.setAccountId(orgAcconutID);
				workTimeCacheEntity.setGroup(isGroupAdmin);
				workTimeCacheEntity.setYear(year);
				workTimeCacheEntity.getSpecialWorkDaysCacheMap().put(
						workTimeSpecial.getDateNum(), workTimeSpecial);
				// 没有这一年的缓存，将这一年的缓存加入
				unitCacheEntities.add(workTimeCacheEntity);
				// 对缓存排序
				Collections.sort(unitCacheEntities);
				workTimeCache.notifyUpdate(orgAcconutID);
			}
		} else {
			if (workTimeCache.get(orgAcconutID) == null) {
				// 没有这一年的缓存，将这一年的缓存加入
				workTimeCacheEntity = new WorkTimeCacheEntity();
				workTimeCacheEntity.getSpecialWorkDaysCacheMap().put(
						workTimeSpecial.getDateNum(), workTimeSpecial);
				workTimeCacheEntity.setAccountId(orgAcconutID);
				workTimeCacheEntity.setGroup(isGroupAdmin);
				workTimeCacheEntity.setYear(year);
				unitCacheEntities.add(workTimeCacheEntity);
				// 对缓存排序
				Collections.sort(unitCacheEntities);
				workTimeCache.put(orgAcconutID, unitCacheEntities);
			}
		}
		workTimeCache.notifyUpdate(orgAcconutID);
	}

	public void copyCurrenctTimeFormGroupToUnit(Long orgAcconutID, String year,
			Integer month) throws WorkTimeSetExecption {

		// 更新单位的这一年，以及这一年以后的通用时间设置
		// unitCacheEntitiesByYear已经按照年进行倒序排序
		List<WorkTimeCacheEntity> unitCacheEntitiesByYear = this.workTimeCache
				.get(orgAcconutID);

		List<WorkTimeCacheEntity> unitTempCacheEntitiesByYear = new ArrayList<WorkTimeCacheEntity>();
		if (unitCacheEntitiesByYear != null
				&& unitCacheEntitiesByYear.size() > 0) {
			for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntitiesByYear) {
				unitTempCacheEntitiesByYear.add(workTimeCacheEntity);
			}

			if (unitCacheEntitiesByYear.get(0).getYear().intValue() < Integer
					.parseInt(year)) {
				// 单位缓存中最大的一年仍然比操作的年小，直接把集团缓存最大的一年插到这一年的数据库和缓存
				List<WorkTimeCurrency> insertWorkTimeCurrencies = new ArrayList<WorkTimeCurrency>();
				insertWorkTimeCurrencies.addAll(findComnRestDaySet(Integer
						.parseInt(year), GROUP_ADMIN_ACCOUNT, true, month));
				insertWorkTimeCurrencies.addAll(findComnWorkTimeSet(Integer
						.parseInt(year), GROUP_ADMIN_ACCOUNT, true, month));
				insertWorkTimeCurrencies = this.changeReturnMapIdAndYear(
						Integer.parseInt(year), insertWorkTimeCurrencies,
						orgAcconutID);
				this.insertComnWorkDayTimeSet(insertWorkTimeCurrencies,
						orgAcconutID, Integer.parseInt(year), false);

			} else {
				for (WorkTimeCacheEntity workTimeCacheEntity : unitTempCacheEntitiesByYear) {
					if ((workTimeCacheEntity.getYear().intValue()) >= (Integer
							.parseInt(year))) {
						List<WorkTimeCurrency> workTimeCurrencies = new ArrayList<WorkTimeCurrency>();
						workTimeCurrencies.addAll(findComnRestDaySet(Integer
								.parseInt(year), GROUP_ADMIN_ACCOUNT, true,
								month));
						workTimeCurrencies.addAll(findComnWorkTimeSet(Integer
								.parseInt(year), GROUP_ADMIN_ACCOUNT, true,
								month));
						workTimeCurrencies = this.changeReturnMapIdAndYear(
								workTimeCacheEntity.getYear(),
								workTimeCurrencies, orgAcconutID);

						if (workTimeCacheEntity.getCurrencyRestDaysCacheMap()
								.size() == 0
								&& workTimeCacheEntity
										.getCurrencyWorkDaysCacheMap().size() == 0) {
							// 说明这一年的单位只有特殊工作日设置，所以插入
							this.insertComnWorkDayTimeSet(workTimeCurrencies,
									orgAcconutID, workTimeCacheEntity.getYear()
											.intValue(), false);
						} else {
							// 更新
							// 组装单位需要更新的数据
							List<WorkTimeCurrency> workTimeCurrencies4Update = new ArrayList<WorkTimeCurrency>();
							workTimeCurrencies4Update
									.addAll(workTimeCacheEntity
											.getCurrencyRestDaysCacheMap()
											.values());
							workTimeCurrencies4Update
									.addAll(workTimeCacheEntity
											.getCurrencyWorkDaysCacheMap()
											.values());
							// 组装集团时间设置的数据Map
							Map<String, WorkTimeCurrency> groupWorkTimeCurrencyMap = new HashMap<String, WorkTimeCurrency>();
							for (WorkTimeCurrency workTimeCurrency : findComnRestDaySet(
									Integer.parseInt(year),
									GROUP_ADMIN_ACCOUNT, true, month)) {
								groupWorkTimeCurrencyMap.put(workTimeCurrency
										.getWeekDayName(), workTimeCurrency);
							}
							for (WorkTimeCurrency workTimeCurrency : findComnWorkTimeSet(
									Integer.parseInt(year),
									GROUP_ADMIN_ACCOUNT, true, month)) {
								groupWorkTimeCurrencyMap.put(workTimeCurrency
										.getWeekDayName(), workTimeCurrency);
							}
							// 更新待更新的数据库值
							for (WorkTimeCurrency workTimeCurrency : workTimeCurrencies4Update) {
								WorkTimeCurrency tempwWorkTimeCurrency = groupWorkTimeCurrencyMap
										.get(workTimeCurrency.getWeekDayName());
								workTimeCurrency
										.setAmWorkTimeBeginTime(tempwWorkTimeCurrency
												.getAmWorkTimeBeginTime());
								workTimeCurrency
										.setAmWorkTimeEndTime(tempwWorkTimeCurrency
												.getAmWorkTimeEndTime());
								workTimeCurrency
										.setIsWork(tempwWorkTimeCurrency
												.getIsWork());
								workTimeCurrency
										.setPmWorkTimeBeginTime(tempwWorkTimeCurrency
												.getPmWorkTimeBeginTime());
								workTimeCurrency
										.setPmWorkTimeEndTime(tempwWorkTimeCurrency
												.getPmWorkTimeEndTime());
								workTimeCurrency.setUpdateTime(new Timestamp(
										System.currentTimeMillis()));
							}

							this.updateComnWorkDayTimeSet(
									workTimeCurrencies4Update, orgAcconutID,
									workTimeCacheEntity.getYear().intValue(),
									false);
						}
					}
				}
			}

		} else {
			// 缓存里面没有，则数据库也没有，插入数据库和缓存
			List<WorkTimeCurrency> insertWorkTimeCurrencies = new ArrayList<WorkTimeCurrency>();
			insertWorkTimeCurrencies.addAll(findComnRestDaySet(Integer
					.parseInt(year), GROUP_ADMIN_ACCOUNT, true, month));
			insertWorkTimeCurrencies.addAll(findComnWorkTimeSet(Integer
					.parseInt(year), GROUP_ADMIN_ACCOUNT, true, month));
			insertWorkTimeCurrencies = this.changeReturnMapIdAndYear(Integer
					.parseInt(year), insertWorkTimeCurrencies, orgAcconutID);
			this.insertComnWorkDayTimeSet(insertWorkTimeCurrencies,
					orgAcconutID, Integer.parseInt(year), false);
		}

	}

	public void insertWorkTimeCurrencySetByYear(Integer year) throws WorkTimeSetExecption {
		// 取得全部单位，含集团
		try {
			for (V3xOrgAccount v3xOrgAccount : OrgManager
					.getAllAccounts()) {
				// 对每个单位，判断其通用工作时间设置今年是否在缓存中存在，缓存中存在，则数据库存在
				Long orgAcconutID = v3xOrgAccount.getOrgAccountId();
				boolean isGroupAdmin = v3xOrgAccount.getOrgAccountId()
						.intValue() == GROUP_ADMIN_ACCOUNT.intValue();
				boolean isInDB = false;
				if (workTimeCache == null) {
					// 缓存没有初始化
					isInDB = false;
				} else {
					if (workTimeCache.contains(orgAcconutID)) {
						// 缓存中存在这个单位
						List<WorkTimeCacheEntity> unitCacheEntities = workTimeCache
								.get(orgAcconutID);
						// 缓存中这个单位存在这一年的数据
						for (WorkTimeCacheEntity workTimeCacheEntity : unitCacheEntities) {
							if (workTimeCacheEntity.getYear().intValue() == year) {
								// 存在通用工作时间设置
								if ((workTimeCacheEntity
										.getCurrencyRestDaysCacheMap() != null && workTimeCacheEntity
										.getCurrencyRestDaysCacheMap().size() != 0)
										|| (workTimeCacheEntity
												.getCurrencyWorkDaysCacheMap() != null && workTimeCacheEntity
												.getCurrencyWorkDaysCacheMap()
												.size() != 0)) {
									isInDB = true;
									break;
								}
							}
						}
					} else {
						// 缓存中没有这个单位
						isInDB = false;
					}
				}
				if (!isInDB) {
					// 不在数据库，则将这一年的插入数据库
					// 取得这一年使用的通用工作时间设置
					//工作日
					List<WorkTimeCurrency> workTimeCurrencies = this
							.findComnWorkTimeSet(year, orgAcconutID,
									isGroupAdmin, null);
					// 休息日
					List<WorkTimeCurrency> restTimeCurrencies = this
							.findComnRestDaySet(year, orgAcconutID,
									isGroupAdmin, null);
					// 组合
					List<WorkTimeCurrency> workTimeCurrencies4Insert = new ArrayList<WorkTimeCurrency>();
					workTimeCurrencies4Insert.addAll(workTimeCurrencies);
					workTimeCurrencies4Insert.addAll(restTimeCurrencies);
					workTimeCurrencies4Insert = this.changeReturnMapIdAndYear(
							year, workTimeCurrencies4Insert, orgAcconutID);
					this.insertComnWorkDayTimeSet(workTimeCurrencies4Insert,
							orgAcconutID, year, isGroupAdmin);

					// 同时，将下一年的工作时间设置设置为当前的工作时间设置
					workTimeCurrencies4Insert = this.changeReturnMapIdAndYear(
							year + 1, workTimeCurrencies4Insert, orgAcconutID);
					this.insertComnWorkDayTimeSet(workTimeCurrencies4Insert,
							orgAcconutID, year + 1, isGroupAdmin);
				}
			}
			log.info("全年数据保存成功。");
		} catch (BusinessException e) {
			log.error("保存全部单位的工作时间设置异常.", e);
			throw new WorkTimeSetExecption(e);
		}
	}

	public String checkUIAndSysTime(Integer year, Integer month, Integer day) {
		Calendar c = Calendar.getInstance();
		int sysYear = c.get(Calendar.YEAR);
		int sysMonth = c.get(Calendar.MONTH);
		int sysDay = c.get(Calendar.DAY_OF_MONTH);
		if (year.intValue() == sysYear && month.intValue() == sysMonth
				&& day.intValue() == sysDay) {
			return "true";
		} else {
			return "false";
		}
	}
	
	//获取某年某月的工作时间填充日历显示
	public List<HashMap<String, String>> getCalendarData(String year, String month) throws WorkTimeSetExecption {
		Long orgAcconutID = CurrentUser.get().getAccountId();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(year != null){
			c.set(Calendar.YEAR, Integer.parseInt(year));
		}else{
			year = String.valueOf(c.get(Calendar.YEAR));
		}
		if(month != null){
			c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		}else{
			month = String.valueOf(c.get(Calendar.MONTH) + 1);
		}
		
		List<HashMap<String, String>> calendarList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> commonRest = new HashMap<String, String>();
		HashMap<String, String> specialWork = new HashMap<String, String>();
		// 获得当年的通用设置的休息日
		String comnRestDayStr = findComnRestDaySet(year, month, orgAcconutID, false);
		String[] restDay = comnRestDayStr.split(",");
		for(int i = 0; i < restDay.length; i++){
			commonRest.put("rest" + restDay[i], restDay[i]);
		}
		// 获得当年当月的特殊工作日
		List<WorkTimeSpecial> specialWorkDays = this.findSpicalWorkDaySet(Integer.parseInt(year), orgAcconutID, false, Integer.parseInt(month));
		for (int i = 0; i < specialWorkDays.size(); i++) {
			WorkTimeSpecial workTimeSpecial = specialWorkDays.get(i);
			specialWork.put(workTimeSpecial.getDateNum(), workTimeSpecial.getIsRest());
		}
		calendarList.add(commonRest);
		calendarList.add(specialWork);
		return calendarList;
	}
}
