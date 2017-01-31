package com.seeyon.v3x.hr.manager;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;


public interface RecordManager {
	
	/**
	 * <pre>
	 * 配合定时任务调度，每天凌晨3：00执行，在系统开启考勤管理的前提下
	 * 为未进行签到、签退操作的员工，插入一条未打卡的考勤记录
	 * 便于HR管理员对各种考勤状态进行查询时，可以查询到"未打卡"状态的有效记录
	 * 此方法目前仅用于此场景，不宜用作其他用处
	 * </pre>
	 * @see com.seeyon.v3x.hr.util.HrNoCardRecordsJob#execute(org.quartz.JobExecutionContext)
	 * @author <a href="mailto:yangm@seeyon.com">多情的苦行僧</a> 2011-7-22
	 */
	public void addRecords4NoCard();
	
	/**
	 * 此方法主要用于数据修复，将一个月内的在线打卡数据，未打卡的员工记录系数补上<br>
	 * 其他场景均不应调用此方法，因为方便考虑，其中部分性能优化点并未付诸实践<br>
	 */
	public void addRecords4NoCardTillnow();

	/**
	 * 增加打卡记录
	 * @param 
	 * @return 
	 */
	public void addRecord(Record record);
	
	/**
	 * 更新记录
	 * @param 
	 * @return 
	 */
	public void updateRecord(String remark, String signOutIP)throws Exception;
	
	/**
	 * 获取某员工某天的打卡记录
	 * @param 
	 * @return 
	 */
	public Record getRecord(Long staffid ,Date time)throws Exception;
	
	/**
	 * 获取系统定义的上班时间的小时数
	 * @param 
	 * @return 
	 */
	public String getBeginHour()throws Exception;
	
	/**
	 * 获取系统定义的上班时间的分钟数
	 * @param 
	 * @return 
	 */
	public String getBeginMinute()throws Exception;
	
	/**
	 * 获取系统定义的下班时间的小时数
	 * @param 
	 * @return 
	 */
	public String getEndHour()throws Exception;
	
	/**
	 * 获取系统定义的下班时间的分钟数
	 * @param 
	 * @return 
	 */
	public String getEndMinute()throws Exception;
	
	/**
	 * 获得所有考勤记录
	 */
	public List<Record> getAllRecord(Long staffid,Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有上班未打卡的记录
	 * @param 
	 * @return 
	 */	
	public List<Record> getNoBeginCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getNoBeginCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	/**
	 * 获取某员工某段时间所有下班未打卡的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getNoEndCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getNoEndCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有上下班均未打卡的天数
	 * @param 
	 * @return 
	 */
	public int getNoCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getNoCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有上班迟到的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getComeLateStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getComeLateStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有下班早退的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getLeaveEarlyStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getLeaveEarlyStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有上班迟到并且下班早退的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getBothStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getBothStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有正常打卡的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getNormalStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getNormalStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有上班未打卡并早退的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getNoBeginCardLeaveEarlyStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getNoBeginCardLeaveEarlyStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	
	/**
	 * 获取某员工某段时间所有迟到并下班未打卡的记录
	 * @param 
	 * @return 
	 */
	public List<Record> getComeLateNoEndCardStatisticById(Long staffid ,Date fromTime,Date toTime)throws Exception;
	public Map<Long, Integer> getComeLateNoEndCardStatisticByIdGroupByMemberId(Date fromTime,Date toTime)throws Exception;
	/**
	 * 设置上下班时间
	 * @param 
	 * @return 
	 */
	public void setWorkingTime(RecordWorkingTime workingTime)throws Exception;
	
	public Record getRecordById(Long id)throws Exception;
	
	public List getAllStaffRecords(Date time)throws Exception;
	
	public List getAllStaffRecords(Date time, int state)throws Exception;
	
	/**
	 * 获得某段时间的所有考勤纪录
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public List<Record> getAllStaffRecord(Date fromTime, Date toTime)throws Exception;
	/**
	 * 获得某段时间的所有考勤纪录 分页
	 * @param fromTime
	 * @param toTime
	 * @return
	 */
	public List<Record> getAllStaffRecordByPage(Date fromTime, Date toTime)throws Exception;
	
	public List<Record> getAllStaffRecord(Date fromTime, Date toTime, int state)throws Exception;
	
	public List<Record> getAdvancedQuery(String fromTime, String toTime, String departmentIds, int state, String personId)throws Exception;
	
	public List<Record> getAll()throws Exception;
	
	public List getRecordByState(int state)throws Exception;

	/**
	 * 删除某个时间段内以前的记录
	 * @param monthsAgo
	 */
	public void deleteAttendance(int monthsAgo);

	/**
	 * 查看date日期是否工作日
	 * @param date
	 * @return
	 */
	public boolean isWorkDay(Date date);

}
