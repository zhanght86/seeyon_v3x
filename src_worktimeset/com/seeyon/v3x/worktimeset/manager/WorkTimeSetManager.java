package com.seeyon.v3x.worktimeset.manager;


import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.domain.WorkTimeSpecial;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;

public interface WorkTimeSetManager {

	/**
	 * 
	 * @param year
	 *            年
	 * @param workDays
	 *            工作日/休息日 1,1,1,1,1,0,0
	 * @param workAmBeginTime
	 *            上午工作开始时间
	 * @param workAmEndTime
	 *            上午工作结束时间
	 * @param workPmBeginTime
	 *            下午工作开始时间
	 * @param workPmEndTime
	 *            下午工作结束时间
	 * @param copyCurrencyTimeFlag
	 *            如果是单位，表示是否复制集团工作时间设置；如果是集团，表示是否将集团的时间设置推送给单位；
	 */
	public void updateComnWorkDayTimeSet(String year, String workDays,
			String workAmBeginTime, String workAmEndTime,
			String workPmBeginTime, String workPmEndTime, Long orgAccountID,
			boolean isGroupAdmin, boolean copyCurrencyTimeFlag, Integer month)
			throws WorkTimeSetExecption;

	/**
	 * 保存或更新单位考勤工作时间
	 * @param beginTime
	 * @param endTime
	 * @param accountId
	 */
	public void saveWorkTime(String [] beginTime, String [] endTime, Long accountId);
	
	/**
	 * 查找一年的通用工作时间设置
	 * 
	 * @param year
	 *            年
	 * @return 本年的通用工作时间设置
	 * @throws WorkTimeSetExecption
	 */

	public WorkTimeCurrency findComnWorkTimeSet(String year, String month,
			Long orgAcconutID, boolean isGroupAdmin)
			throws WorkTimeSetExecption;

	/**
	 * 取得通用的休息时间设置
	 * 
	 * @param year
	 * @param orgAccountID
	 * @param isGroup
	 * @param month
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public List<WorkTimeCurrency> findComnRestDaySet(Integer year,
			Long orgAccountID, boolean isGroup, Integer month)
			throws WorkTimeSetExecption;

	public String findComnRestDaySet(String year, String month,
			Long orgAcconutID, boolean isGroupAdmin)
			throws WorkTimeSetExecption;
	/**
	 * 
	 * @param year
	 *            年
	 * @param updateDaySetStr
	 *            更新串，格式"2010/09/30||id||flag||info"
	 * @param month
	 *            月
	 * @return 更新串 updateDaySetStr
	 */
	public String updateSpecialWorkDaySet(String year, String updateDaySetStr,
			String month);

	/**
	 * 
	 * 取得某年当月设置的特殊的工作日和非工作日
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return 当月的特殊设置的工作日和非工作日，格式为：
	 *         "2010/09/30||id||flag||info,2010/10/01||id||flag||info,..."
	 * @throws WorkTimeSetExecption
	 */
	public String findSpecialWorkDaySet(String year, String month,
			Long orgAcconutID, boolean isGroupAdmin)
			throws WorkTimeSetExecption;

	// /**
	// * 根据传入参数开始时间、处理时间间隔，计算取得包含工作时间设置后的具体的处理完成时间点； 开始时间为空，则取当前系统时间作为开始时间进行计算；
	// *
	// * @param beginTimeDate
	// * 开始时间，描述启动流程时的开始时间，如果为空，则取当前系统时间；
	// * @param deadline
	// * 处理时间间隔(分钟)，描述规定的流程运行处理时间，为空，则无时间要求；
	// * @return
	// * @throws WorkTimeSetExecption
	// */
	// public java.util.Date getCompleteDate(java.util.Date beginTimeDate,
	// Integer deadline, Long orgAcconutID, boolean isGroupAdmin)
	// throws WorkTimeSetExecption;
	//
	// /**
	// * 根据传入参数开始时间、结束时间，计算包含工作时间设置后，一项流程的实际使用的工作时间
	// *
	// * @param beginDealTimeDate
	// * 开始时间，描述启动流程时的开始时间，如果为空，则取当前系统时间；
	// * @param endDealTimeDate
	// * 结束时间，描述启动流程时的结束时间；
	// * @return 一项流程的实际使用的工作时间
	// * @throws WorkTimeSetExecption
	// */
	// public long getDealWithTimeValue(java.util.Date beginDealTimeDate,
	// java.util.Date endDealTimeDate, Long orgAcconutID,
	// boolean isGroupAdmin) throws WorkTimeSetExecption;

	/**
	 * 取得通用设置的工作日
	 * 
	 * @param year
	 *            年
	 * @param orgAcconutID
	 *            组织id
	 * @param isGroupAdmin
	 *            是否是集团管理员
	 * @param month
	 *            月
	 * @return 通用工作日Map key weekNum（0,1,2...6） value=通用工作日对象
	 * @throws WorkTimeSetExecption
	 */
	public Map<String, WorkTimeCurrency> findComnWorkDaySet(Integer year,
			Long orgAcconutID, boolean isGroupAdmin, Integer month)
			throws WorkTimeSetExecption;

	/**
	 * 特殊设置的工作日/休息日/法定休息日
	 * 
	 * @param year
	 *            年
	 * @param orgAcconutID
	 *            组织id
	 * @param isGroupAdmin
	 *            是否是集团管理员
	 * @param month
	 *            月
	 * @return 通用休息日Map key dateNum（2010/10/28） value=特殊设置的工作日对象
	 * @throws WorkTimeSetExecption
	 */
	public Map<String, WorkTimeSpecial> findSpicalWorkDayMap(Integer year,
			Long orgAcconutID, boolean isGroupAdmin, Integer month)
			throws WorkTimeSetExecption;

	/**
	 * 取得通用设置的工作日
	 * 
	 * @param year
	 *            年
	 * @param orgAcconutID
	 *            组织id
	 * @param isGroupAdmin
	 *            是否是集团管理员
	 * @param month
	 *            月
	 * @return 通用休息日Map key weekNum（0,1,2...6） value=通用工作日对象
	 * @throws WorkTimeSetExecption
	 */
	public List<WorkTimeCurrency> findComnWorkTimeSet(Integer year,
			Long orgAcconutID, boolean isGroupAdmin, Integer month)
			throws WorkTimeSetExecption;

	/**
	 * 初始化缓存
	 */
	public void initCache();

	/**
	 * 将集团的通用时间设置复制给单位
	 * 
	 * @param orgAcconutID
	 *            单位ID
	 * @param year
	 *            年
	 */
	public void copyCurrenctTimeFormGroupToUnit(Long orgAcconutID, String year,
			Integer month) throws WorkTimeSetExecption;

	/**
	 * 将集团的指定的工作日设置复制给单位
	 * 
	 * @param year
	 *            当年
	 * @param month
	 *            当月
	 * @param syncFlag
	 *            同步标志区分 1：同步年；0：同步当月
	 * @param dateNum
	 *            当前日期
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public String syncSpecialDayFromGroupToUnit(String year, String month,
			String syncFlag, String dateNum) throws WorkTimeSetExecption;

	/**
	 * 将一年的工作时间设置保存到数据库和缓存 （供定时任务使用）
	 * 
	 * @param year
	 *            年
	 * @throws WorkTimeSetExecption
	 */
	public void insertWorkTimeCurrencySetByYear(Integer year)
			throws WorkTimeSetExecption;

	/**
	 * 检查系统时间和前台浏览器时间是否一致
	 * 
	 * @param year
	 *            前台year
	 * @param month
	 *            前台month
	 * @param day
	 *            前台day
	 * @return
	 */
	public String checkUIAndSysTime(Integer year, Integer month, Integer day);
	
	/**
	 * 获取某年某月的工作时间(工作日、休息日、法定节假日)填充日历显示
	 * @param year
	 * @param month
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public List<HashMap<String, String>> getCalendarData(String year, String month) throws WorkTimeSetExecption;
}