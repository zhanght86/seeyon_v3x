package com.seeyon.v3x.hr.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.hr.RecordStateConstant;
import com.seeyon.v3x.hr.dao.RecordDao;
import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordState;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.worktimeset.dao.WorkSetDao;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.domain.WorkTimeSpecial;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManager;

public class RecordManagerImpl implements RecordManager {
	private static final Log logger = LogFactory.getLog(RecordManagerImpl.class);
	
    private RecordDao recordDao;
	private SystemConfig systemConfig;
    private OrgManager orgManager;
    private WorkSetDao workSetDao;
    private OrgManagerDirect orgManagerDirect;
	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setRecordDao(RecordDao recordDao) {
		this.recordDao = recordDao;
	}
	public RecordDao getRecordDao() {
		return recordDao;
	}
	public WorkSetDao getWorkSetDao() {
		return workSetDao;
	}
	public void setWorkSetDao(WorkSetDao workSetDao) {
		this.workSetDao = workSetDao;
	}
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}



	private Map<Integer, RecordState> recordStateCache = new HashMap<Integer, RecordState>();
	
	/**
	 * <pre>
	 * 在Spring IOC容器生成此Bean，进行初始化操作时，启动一个定时任务
	 * 为未进行在线打卡操作的用户插入未打卡状态的打卡记录
	 * 方便HR管理员查询到未打卡的员工
	 * </pre>
	 */
	public void init() {
		List<RecordState> states = this.recordDao.getAllRecordState();
		for(RecordState state : states) {
			recordStateCache.put(state.getId(), state);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("打卡状态记录缓存加载完毕...");
		}
	}
	
	@SuppressWarnings("unchecked")
	private Date getMinCardDate() {
		String hql = "select min(begin_work_time) from " + Record.class.getCanonicalName();
		List<Date> result = this.recordDao.find(hql, -1, -1, null);
		return CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
	}
	
	public static void main(String[] args) {
		Date d1 = Datetimes.parseDate("2011-07-26");
		Date d2 = Datetimes.parseDate("2011-06-26");
		
		int minus = (int)Datetimes.minusDay(d1, d2);
		Date d3 = Datetimes.addDate(d2, minus);
		System.out.println(d3);
	}
	
	public void addRecords4NoCardTillnow() {
		String ci = systemConfig.get(IConfigPublicKey.CARD_ENABLE);
		boolean cardEnabled = ci != null && Constants.CARD_ENABLED.equals(ci);
		if(!cardEnabled)
			return;
		
		Date minCardDate = this.getMinCardDate();
		if(minCardDate == null)
			return;
		
		minCardDate = Datetimes.getTodayFirstTime(minCardDate);
		Date today = Datetimes.getTodayFirstTime();
		int minus = (int)Datetimes.minusDay(today, minCardDate);
		for(int i = 0; i < minus; i++ ) {
			Date d = Datetimes.addDate(minCardDate, i);
			try {
				this.addRecords4NoCard4Day(d);
			} 
			catch (BusinessException e) {
				logger.error("为" + Datetimes.formatDate(d) + "未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
			}
		}
	}
	
	public void addRecords4NoCard() {
		try {
			// 对昨天的打卡记录进行处理
			Date yesterday = Datetimes.addDate(new Date(), -1);
			Calendar time = Calendar.getInstance();	
			time.setTime(yesterday);
			
			addRecords4NoCard4Day(yesterday);
		} 
		catch (BusinessException e) {
			logger.error("获取组织模型信息时出现异常：", e);
		} 
		catch (Exception e) {
			logger.error("为未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
		}
	}
	
	private void addRecords4NoCard4Day(Date someDay) throws BusinessException {
		WorkTimeSetManager workTimeSetManager = (WorkTimeSetManager) ApplicationContextHolder.getBean("workTimeSetManager");
		
		Calendar time = Calendar.getInstance();	
		time.setTime(someDay);
		int year = time.get(Calendar.YEAR);
		int month = time.get(Calendar.MONTH) + 1;
		int day = time.get(Calendar.DAY_OF_MONTH);
		int dayWeek = time.get(Calendar.DAY_OF_WEEK);
		//放开双休日打卡限制
		/*int dayWeek = time.get(Calendar.DAY_OF_WEEK);
		if(dayWeek == Calendar.SATURDAY || dayWeek == Calendar.SUNDAY) {
			if(logger.isDebugEnabled()) {
				logger.debug(Datetimes.formatDate(someDay) + "是双休日：星期" + (dayWeek == 1 ? "天" : "六"));
			}
			return;
		}*/
		//启动报错：获得不到当前用户导致
		/*Long orgAccountId = CurrentUser.get().getAccountId();
		boolean isWorkDay = workSetDao.isWorkDayInCurrency(orgAccountId, String.valueOf(year), String.valueOf(dayWeek-1));
		String dateNum = year + "/" + month + "/" + day;
		List<WorkTimeSpecial> workTimeSpecials = workSetDao.isWorkDayInSpecial(orgAccountId, dateNum);
		//非工作日未打卡记录不入库
		if(workTimeSpecials.size() > 0 && !workTimeSpecials.get(0).getIsRest().equals("0") || workTimeSpecials.size() == 0 && !isWorkDay){
			return;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("为" + Datetimes.formatDate(someDay) + "未进行签到、签退操作的员工，插入未打卡考勤记录开始");
		}*/
		
		RecordState noCard = this.recordStateCache.get(RecordStateConstant.NO_CARD);
		// 目前所有单位的工作时间设置均相同，后期应加入accountId字段加以区分\
		// 已经加入accountId字段加以区分2011-12-27
		
		String begin_hour = "0", begin_minute = "0", end_hour = "0", end_minute = "0";
		
		String ci = systemConfig.get(IConfigPublicKey.CARD_ENABLE);
		boolean cardEnabled = ci != null && Constants.CARD_ENABLED.equals(ci);
		
		if(cardEnabled) {
			List<V3xOrgAccount> accounts = orgManager.getAllAccounts();
			if(CollectionUtils.isEmpty(accounts)) {
				return;
			}
				
			for(V3xOrgAccount account : accounts) {
				// 集团根单位无需处理
				if(account != null && account.isValid() && !account.getIsRoot()) {
					Long accountId = account.getId();
					//获取每个单位的工作时间
					RecordWorkingTime workTime = this.recordDao.getRecordWorkingTime(accountId);
					if(workTime != null) {
						begin_hour = String.valueOf(workTime.getBegin_hour());
						end_hour = String.valueOf(workTime.getEnd_hour());
						
						begin_minute = workTime.getBegin_minute() < 10 ? ("0" + workTime.getBegin_minute()) : String.valueOf(workTime.getBegin_minute());
						end_minute = workTime.getEnd_minute() < 10 ? ("0" + workTime.getEnd_minute()) : String.valueOf(workTime.getEnd_minute());
					}
					
					// 判断每个单位这一天是否是工作日
					Map<String, WorkTimeCurrency> workTimeMap = workTimeSetManager.findComnWorkDaySet(year, accountId, false, month);
					boolean isWorkDay = workTimeMap.get(String.valueOf(dayWeek - 1)) != null;

					String dateNum = year + "/" + month + "/" + day;
					List<WorkTimeSpecial> workTimeSpecials = workSetDao.isWorkDayInSpecial(accountId, dateNum);
					boolean hasSpecials = CollectionUtils.isNotEmpty(workTimeSpecials);

					// 非工作日未打卡记录不入库
					if ((hasSpecials && !workTimeSpecials.get(0).getIsRest().equals("0")) || (!hasSpecials && !isWorkDay)) {
						continue;
					}
					
					if(logger.isDebugEnabled()) {
						logger.debug("为" + Datetimes.formatDate(someDay) + "未进行签到、签退操作的员工，插入未打卡考勤记录开始");
					}
					List<V3xOrgMember> members = orgManager.getAllMembers(accountId, false, false);
					if(CollectionUtils.isNotEmpty(members)) {
						Map<Long, V3xOrgMember> map = new HashMap<Long, V3xOrgMember>();
						Set<Long> memberIds = new HashSet<Long>();
						for (V3xOrgMember member : members) {
							if (member != null && member.isValid() && member.getOrgAccountId().equals(account.getId())) {
								map.put(member.getId(), member);
								memberIds.add(member.getId());
							}
						}

						List<Long> staffIdsHavingRecord = this.getMemberIdsHavingCardRecord(Datetimes.getTodayLastTime(someDay), accountId);
						if (CollectionUtils.isNotEmpty(staffIdsHavingRecord)) {
							memberIds.removeAll(staffIdsHavingRecord);
						}
						
						if(CollectionUtils.isNotEmpty(memberIds)) {
							List<Record> records = new ArrayList<Record>(memberIds.size());
							for(Long memberId : memberIds) {
								Record record = new Record();
								record.setNewId();
								record.setState(noCard);
								record.setAccountId(accountId);
								record.setStaffer_id(memberId);
								// 暂不处理跨单位兼职部门或副岗情况，以主岗所在部门为准
								V3xOrgMember member = map.get(memberId);
								record.setDep_id(member.getOrgDepartmentId());
								// 未打卡的开始、结束日期，为方便与系统的查询接口逻辑一致，暂先设定两个值，在展现时进行特别处理
								record.setBegin_work_time(Datetimes.getTodayLastTime(someDay));
								record.setEnd_work_time(Datetimes.getTodayFirstTime(someDay));
								record.setBegin_hour(begin_hour);
								record.setBegin_minute(begin_minute);
								record.setEnd_hour(end_hour);
								record.setEnd_minute(end_minute);
								record.setYear(year);
								record.setMonth(month);
								record.setDay(day);
								record.setRemark(Constants.getI18N("hr.nocard.remark", member.getName(), String.valueOf(year), month, day));
								record.setIsWorkDay(1);
								records.add(record);
							}
							
							this.recordDao.savePatchAll(records);
							
							if(logger.isDebugEnabled()) {
								logger.debug("为单位：" + account.getName() + "[id=" + accountId + "]，" + Datetimes.formatDate(someDay) + "未打卡员工插入未打卡记录完毕!");
							}
						}
					}
				}
			}
			
			logger.info("为" + Datetimes.formatDate(someDay) + "未进行签到、签退操作的员工，插入未打卡考勤记录结束");
		}
	}
	
	/**
	 * 获取在指定单位、日期下，当日有在线打卡记录的人员ID集合
	 * @param date			需要补充未打卡记录的日期
	 * @param accountId		单位ID
	 */
	@SuppressWarnings("unchecked")
	private List<Long> getMemberIdsHavingCardRecord(Date date, Long accountId) {
		String hql = "select staffer_id from " + Record.class.getCanonicalName() + " where accountId=? and ((begin_work_time>? and begin_work_time<=?) or (end_work_time >? and end_work_time<=?))";
		Date first = Datetimes.getTodayFirstTime(date);
		Date last = Datetimes.getTodayLastTime(date);
		return (List<Long>)this.recordDao.find(hql, -1, -1, null, accountId, first, last, first, last);
	}

	/**
	 * 增加打卡记录
	 * @param 
	 * @return 
	 */
	public void addRecord(Record record){
		record.setIdIfNew();
		recordDao.save(record);
	}
	
	/**
	 * 更新记录
	 * @param 
	 * @return 
	 */
	public void updateRecord(String remark, String signOutIP)throws Exception{
		Long staffid = CurrentUser.get().getId();
		Calendar time = Calendar.getInstance();	
		Record rc = recordDao.getRecord(staffid, time.getTime());
		rc.setRemark(remark);
		rc.setSignOutIP(signOutIP);
		RecordState rs = new RecordState();
		int eth=Integer.parseInt(this.getEndHour());
	    int etm=Integer.parseInt(this.getEndMinute());
		if (rc.getId() == null){
			if (isWorkDay(time.getTime())) {
				rc.setIsWorkDay(1);
			} else {
				rc.setIsWorkDay(0);
			}
			Long depid = CurrentUser.get().getDepartmentId();
			rc.setStaffer_id(staffid);
			rc.setDep_id(depid);
			rc.setEnd_work_time(time.getTime());
			rc.setYear(time.get(Calendar.YEAR));
			rc.setMonth(time.get(Calendar.MONTH)+1);
			rc.setDay(time.get(Calendar.DAY_OF_MONTH));
			rc.setBegin_hour(this.getBeginHour());
			rc.setBegin_minute(this.getBeginMinute());
			rc.setEnd_hour(this.getEndHour());
			rc.setEnd_minute(this.getEndMinute());
			if(time.get(Calendar.HOUR_OF_DAY)<eth || (time.get(Calendar.HOUR_OF_DAY)==eth && time.get(Calendar.MINUTE)<etm)){
				rs.setId(8);
			}else{
				rs.setId(1);
			}
			rc.setState(rs);
			rc.setAccountId(CurrentUser.get().getAccountId()) ;
			this.addRecord(rc);
		} else {	
		    Calendar bt = Calendar.getInstance();	
		    int bth=Integer.parseInt(this.getBeginHour());
		    int btm=Integer.parseInt(this.getBeginMinute());
		    int state=0;
		    if(rc.getBegin_work_time() == null){
		    	if(time.get(Calendar.HOUR_OF_DAY)<eth || (time.get(Calendar.HOUR_OF_DAY)==eth && time.get(Calendar.MINUTE)<etm)){
		    		state=8;
		    		rs.setId(state);
		    		rc.setState(rs);
		    		rc.setEnd_work_time(time.getTime());
		    		recordDao.update(rc);
		    	}else{
		    		state=1;
		    		rs.setId(state);
		    		rc.setState(rs);
		    		rc.setEnd_work_time(time.getTime());
		    		recordDao.update(rc);
		    	}
		    }else{
			    bt.setTime(rc.getBegin_work_time());
			    
			    if(bt.get(Calendar.HOUR_OF_DAY)>bth || (bt.get(Calendar.HOUR_OF_DAY)==bth)&bt.get(Calendar.MINUTE)>btm){
			    	if(time.get(Calendar.HOUR_OF_DAY)<eth || (time.get(Calendar.HOUR_OF_DAY))==eth&time.get(Calendar.MINUTE)<etm){		   
			    		state=6;
			    		rs.setId(state);
			    		rc.setState(rs);
			    		rc.setEnd_work_time(time.getTime());
			    		recordDao.update(rc);
			    	}
			    	else if(time.get(Calendar.HOUR_OF_DAY)>eth || (time.get(Calendar.HOUR_OF_DAY)==eth)&time.get(Calendar.MINUTE)>=etm){
			    		state=4;
			    		rs.setId(state);
			    		rc.setState(rs);
			    		rc.setEnd_work_time(time.getTime());
			    		recordDao.update(rc);
			    	}
			    }
			    else if(bt.get(Calendar.HOUR_OF_DAY)<bth || (bt.get(Calendar.HOUR_OF_DAY))==bth&bt.get(Calendar.MINUTE)<=btm){
			    	if(time.get(Calendar.HOUR_OF_DAY)<eth || (time.get(Calendar.HOUR_OF_DAY))==eth&time.get(Calendar.MINUTE)<etm){
			    		state=5;
			    		rs.setId(state);
			    		rc.setState(rs);
			    		rc.setEnd_work_time(time.getTime());
			    		recordDao.update(rc);
			    	}
			    	else if(time.get(Calendar.HOUR_OF_DAY)>eth || (time.get(Calendar.HOUR_OF_DAY))==eth&time.get(Calendar.MINUTE)>=etm){
			    		state=7;
			    		rs.setId(state);
			    		rc.setState(rs);
			    		rc.setEnd_work_time(time.getTime());
			    		recordDao.update(rc);
			    	}
			    }
		    }
		}
		
	}
	
	/**
	 * 获取某员工某天的打卡记录
	 * @param 
	 * @return 
	 */
	public Record getRecord(Long staffid ,Date time)throws Exception{
		
		Record rc=recordDao.getRecord(staffid, time);
		return rc;
	}
	
	/**
	 * 获取系统定义的上班时间的小时数
	 * @param 
	 * @return 
	 */
	public String getBeginHour()throws Exception{
		return recordDao.getBeginHour();
	}
	
	/**
	 * 获取系统定义的上班时间的分钟数
	 * @param 
	 * @return 
	 */
	public String getBeginMinute()throws Exception{
		
		return recordDao.getBeginMinute();
	}
	
	/**
	 * 获取系统定义的下班时间的小时数
	 * @param 
	 * @return 
	 */
	public String getEndHour()throws Exception{
		
		return recordDao.getEndHour();
	}
	
	/**
	 * 获取系统定义的下班时间的分钟数
	 * @param 
	 * @return 
	 */
	public String getEndMinute()throws Exception{
		
		return recordDao.getEndMinute();
	}
	
	/**
	 * 获得所有考勤记录
	 */
	public List<Record> getAllRecord(Long staffid,Date fromTime,Date toTime)throws Exception{
		return this.recordDao.getRecord(staffid,fromTime,toTime);
	}
	
	/**
	 * 获取某员工某段时间所有上班未打卡的记录
	 * @param 
	 * @return 
	 */	
	public List<Record> getNoBeginCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.NO_BEGIN_CARD;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getNoBeginCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.NO_BEGIN_CARD;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	/**
	 * 获取某员工某段时间所有下班未打卡的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getNoEndCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.NO_END_CARD;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getNoEndCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.NO_END_CARD;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	/**
	 * 获取某员工某段时间所有上下班均未打卡的天数
	 * @param 
	 * @return 
	 */
	public int getNoCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		return recordDao.getNoCardTimesByIdNew(staffid, fromTime, toTime);
	}
	public Map<Long, Integer> getNoCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		return recordDao.getNoCardTimesByIdNewGroupByMemberId(fromTime, toTime);
	}
	
	/**
	 * 获取某员工某段时间所有上班迟到的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getComeLateStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.COME_LATE;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getComeLateStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.COME_LATE;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	
	/**
	 * 获取某员工某段时间所有下班早退的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getLeaveEarlyStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.LEVEAEARLY;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getLeaveEarlyStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.LEVEAEARLY;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	
	/**
	 * 获取某员工某段时间所有上班迟到并且下班早退的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getBothStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.BOTH;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getBothStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.BOTH;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	
	/**
	 * 获取某员工某段时间所有正常打卡的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getNormalStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.NORMAL;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getNormalStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		int state = RecordStateConstant.NORMAL;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	
	/**
	 * 获取某员工某段时间所有上班未打卡并早退的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getNoBeginCardLeaveEarlyStatisticById(Long staffid, Date fromTime, Date toTime) throws Exception {
		int state = RecordStateConstant.NO_BEGIN_CARD_LEAVE_EARLY;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getNoBeginCardLeaveEarlyStatisticByIdGroupByMemberId(Date fromTime, Date toTime) throws Exception {
		int state = RecordStateConstant.NO_BEGIN_CARD_LEAVE_EARLY;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}

	/**
	 * 获取某员工某段时间所有迟到并下班未打卡的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getComeLateNoEndCardStatisticById(Long staffid, Date fromTime, Date toTime) throws Exception {
		int state = RecordStateConstant.COME_LATE_NO_END_CARD;
		return recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
	}
	public Map<Long, Integer> getComeLateNoEndCardStatisticByIdGroupByMemberId(Date fromTime, Date toTime) throws Exception {
		int state = RecordStateConstant.COME_LATE_NO_END_CARD;
		return recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
	}
	/**
	 * 设置上下班时间
	 * @param 
	 * @return 
	 */
	public void setWorkingTime(RecordWorkingTime workingTime)throws Exception{
		recordDao.setWorkingTime(workingTime);
	}
	
	public Record getRecordById(Long id)throws Exception{
		return this.recordDao.getRecordById(id);
	}
	
	public List<Record> getAllStaffRecords(Date time)throws Exception{
		return this.recordDao.findAllRecords(time);
	}
	
	public List<Record> getAllStaffRecord(Date fromTime, Date toTime)throws Exception{
		return this.recordDao.findAllStaffRecord(fromTime, toTime);
	}

	public List<Record> getAllStaffRecordByPage(Date fromTime, Date toTime)throws Exception{
		return this.recordDao.findAllStaffRecordByPage(fromTime, toTime, -1, -1);
	}
	public List<Record> getAll()throws Exception{
		return this.recordDao.findallRecords();
	}
	
	public List<Record> getRecordByState(int state)throws Exception{
		return this.recordDao.findRecordByState(state);
	}
	
	public List<Record> getAllStaffRecords(Date time, int state)throws Exception{
		return this.recordDao.findAllRecords(time, state);
	}
	
	public List<Record> getAllStaffRecord(Date fromTime, Date toTime, int state)throws Exception{
		return this.recordDao.findAllStaffRecord(fromTime, toTime, state);
	}
	
	public List<Record> getAdvancedQuery(String fromTime, String toTime, String departmentIds, int state, String personIds)throws Exception{
		Date fTime = Datetimes.parse(fromTime, "yyyy-MM-dd");
		Date tTime = Datetimes.parse(toTime, "yyyy-MM-dd");
		Set<Long> depIds = new HashSet<Long>();
		List<Long> pIds = new ArrayList<Long>();
		if(departmentIds != null && !departmentIds.equals("")){
			String[] deps = departmentIds.split(",");
			
			
			for(int i=0; i<deps.length; i++){
				if (deps[i].contains("|1")) {
					String[] deptIdStrs = deps[i].split("\\|");
					depIds.add(Long.parseLong(deptIdStrs[0]));
				} else {
					List<V3xOrgDepartment> orgdeptList = orgManager.getChildDepartments(Long.parseLong(deps[i]),false);
					for (V3xOrgDepartment v3xOrgDepartment : orgdeptList) {
						depIds.add(v3xOrgDepartment.getId());
					}
					depIds.add(Long.parseLong(deps[i]));
				}
			}
		}
		if(personIds != null && !personIds.equals("")){
			String[] pId = personIds.split(",");
			for(int i=0; i<pId.length; i++){
				pIds.add(Long.parseLong(pId[i]));
			}
		}
		return this.recordDao.advancedQuery(fTime, tTime, state, new ArrayList<Long>(depIds), pIds);
	}
	
	public void deleteAttendance(int monthsAgo) {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		this.recordDao.deleteAttendance(year, month, monthsAgo);
	}
	
	public boolean isWorkDay(Date date){
		Calendar time = Calendar.getInstance();	
		time.setTime(date);
		int year = time.get(Calendar.YEAR);
		int month = time.get(Calendar.MONTH) + 1; 
		int day = time.get(Calendar.DAY_OF_MONTH);
		int dayWeek = time.get(Calendar.DAY_OF_WEEK);
		Long orgAccountId = CurrentUser.get().getAccountId();
		boolean isWorkDay = workSetDao.isWorkDayInCurrency(orgAccountId, String.valueOf(year), String.valueOf(dayWeek-1));
		String dateNum = year + "/" + month + "/" + day;
		List<WorkTimeSpecial> workTimeSpecials = workSetDao.isWorkDayInSpecial(orgAccountId, dateNum);
		//非工作日
		if(workTimeSpecials.size() > 0 && !workTimeSpecials.get(0).getIsRest().equals("0") || workTimeSpecials.size() == 0 && !isWorkDay){
			return false;
		}
		return true;
	}
	
}
