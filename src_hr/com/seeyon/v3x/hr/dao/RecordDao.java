package com.seeyon.v3x.hr.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordState;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.util.Datetimes;

public class RecordDao extends BaseHibernateDao<Record>  {
	
	/**
	 * 返回某员工某天的打卡记录
	 */
	@SuppressWarnings("unchecked")
	public Record getRecord(Long staffid, Date time) throws Exception {
		Record rc = new Record();
		String hql = "From Record where staffer_id = :staffid and ("
				+"("
				+"year(begin_work_time)=year(:time) and month(begin_work_time)=month(:time) and day(begin_work_time)=day(:time)" 
				+")"
				+"or"
				+"("
				+"year(end_work_time)=year(:time) and month(end_work_time)=month(:time) and day(end_work_time)=day(:time)"
				+")"
				+")";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("staffid", staffid);
		params.put("time", time);
		List<Record> list = this.find(hql, -1, -1, params);
		if(list != null && !list.isEmpty()){
			rc = list.get(0);
		}
		return rc;
	}
	
	@SuppressWarnings("unchecked")
	public RecordWorkingTime getRecordWorkingTime() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", CurrentUser.get().getAccountId());
		List<RecordWorkingTime> list = super.find("From RecordWorkingTime where accountId = :accountId", -1, -1, params);
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		} else {
			list = super.find("From RecordWorkingTime where id = 1");
			if(CollectionUtils.isNotEmpty(list)) {
				return list.get(0);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public RecordWorkingTime getRecordWorkingTime(Long accountId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		List<RecordWorkingTime> list = super.find("From RecordWorkingTime where accountId = :accountId", -1, -1, params);
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取系统定义的上班时间的小时数
	 */
	public String getBeginHour()throws Exception {
		RecordWorkingTime time = this.getRecordWorkingTime();
		if(time != null) {
			return String.valueOf(time.getBegin_hour());
		}
		return "0";
	}
	
	/**
	 * 获取系统定义的上班时间的分钟数
	 */
	@SuppressWarnings("unchecked")
	public String getBeginMinute()throws Exception{
		String s = "0";
		Session session = super.getSession();
		try{
			String hql = "From RecordWorkingTime where accountId = :accountId";
			Query query = session.createQuery(hql).setLong("accountId", CurrentUser.get().getAccountId());
			List<RecordWorkingTime> list = query.list();
			if(CollectionUtils.isEmpty(list)) {
				hql = "From RecordWorkingTime where id = 1";
				query = session.createQuery(hql);
				list = query.list();
			}
			RecordWorkingTime wt = new RecordWorkingTime();
			if(list!=null&&!list.isEmpty()){
			  wt = list.get(0);
			}
			s = String.valueOf(wt.getBegin_minute());
			if(s.length()==1){
				s="0"+s;
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		return s;
	}
	
	/**
	 * 获取系统定义的下班时间的小时数
	 * @param 
	 * @return 
	 */
	public String getEndHour()throws Exception {
		RecordWorkingTime time = this.getRecordWorkingTime();
		if(time != null) {
			return String.valueOf(time.getEnd_hour());
		}
		return "0";
	}
	
	/**
	 * 获取系统定义的下班时间的分钟数
	 * @param 
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public String getEndMinute()throws Exception{
		String s = "0";
		Session session = super.getSession();
		try{
			String hql = "From RecordWorkingTime where accountId = :accountId";
			Query query = session.createQuery(hql).setLong("accountId", CurrentUser.get().getAccountId());
			List<RecordWorkingTime> list = query.list();
			if(CollectionUtils.isEmpty(list)) {
				hql = "From RecordWorkingTime where id = 1";
				query = session.createQuery(hql);
				list = query.list();
			}
			RecordWorkingTime wt = new RecordWorkingTime();
			if(list!=null&&!list.isEmpty()){
			  wt = list.get(0);
			}
			s = String.valueOf(wt.getEnd_minute());
			if(s.length()==1){
				s="0"+s;
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}

		return s;
	}
	
	/**
	 * 获取某员工某段时间的某种打卡记录
	 * @param 
	 * @return 
	 */	
	@SuppressWarnings("unchecked")
	public List<Record> getStatisticByIdAndState(Long staffid ,Date fromTime,Date toTime,int state)throws Exception{
		List<Record> list = null;
		//查询时，需要把最后日期挪后一天
		Session session = super.getSession();
		try{
			Calendar toCal = Calendar.getInstance();
			toCal.setTime(toTime);
			toCal.add(Calendar.DAY_OF_MONTH, 1);
			
			StringBuffer hql = new StringBuffer();
			hql.append("from Record where staffer_id = :staffid and isWorkDay = 1 and state = :state and (");
			hql.append(" (begin_work_time >= :fromTime and begin_work_time < :toTime)");
			hql.append(" or ");
			hql.append(" (end_work_time >= :fromTime and end_work_time < :toTime)");
			hql.append(")");
			Query query = session.createQuery(hql.toString()).setLong("staffid", staffid)
			                                                 .setInteger("state", state)
			                                                 .setDate("fromTime", fromTime)
			                                                 .setDate("toTime", toCal.getTime()); 
			list = query.list();
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getStatisticByIdAndStateGroupByMemberId(Date fromTime,Date toTime,int state)throws Exception{
		Map<Long, Integer> result = new HashMap<Long, Integer>();
		//查询时，需要把最后日期挪后一天
		Session session = super.getSession();
		try{
			Calendar toCal = Calendar.getInstance();
			toCal.setTime(toTime);
			toCal.add(Calendar.DAY_OF_MONTH, 1);
			
			StringBuffer hql = new StringBuffer();
			hql.append("select staffer_id,count(*) from Record where state = :state and isWorkDay = 1 and (");
			hql.append(" (begin_work_time >= :fromTime and begin_work_time < :toTime)");
			hql.append(" or ");
			hql.append(" (end_work_time >= :fromTime and end_work_time < :toTime)");
			hql.append(") group by staffer_id");
			Query query = session.createQuery(hql.toString())
			.setInteger("state", state)
			.setDate("fromTime", fromTime)
			.setDate("toTime", toCal.getTime()); 
			List<Object[]> list = query.list();
			for (Object[] objects : list) {
				result.put((Long)objects[0], (Integer)objects[1]);
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public int getNoCardTimesByIdNew(Long staffid ,Date fromTime,Date toTime)throws Exception{
		String hql = "select count(id) from Record where staffer_id = :staffid and isWorkDay = 1 and state.id = :nocard and " +
				"(" +
				" (begin_work_time >= :fromTime and begin_work_time < :toTime) or " +
				" (end_work_time >= :fromTime and end_work_time < :toTime)" +
				")";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("staffid", staffid);
		params.put("nocard", Constants.HR_NOCARD_STATE);
		params.put("fromTime", fromTime);
		params.put("toTime", toTime);
		
		List<Integer> ret = this.find(hql, -1, -1, params);
		return ret.get(0);
	}
	@SuppressWarnings("unchecked")
	public Map<Long, Integer> getNoCardTimesByIdNewGroupByMemberId(Date fromTime,Date toTime)throws Exception{
		Map<Long, Integer> result = new HashMap<Long, Integer>();
		String hql = "select staffer_id,count(id) from Record where state.id = :nocard and isWorkDay = 1 and " +
		"(" +
		" (begin_work_time >= :fromTime and begin_work_time < :toTime) or " +
		" (end_work_time >= :fromTime and end_work_time < :toTime)" +
		") group by staffer_id";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nocard", Constants.HR_NOCARD_STATE);
		params.put("fromTime", fromTime);
		params.put("toTime", toTime);
		
		List<Object[]> ret = this.find(hql, -1, -1, params);
		for (Object[] objects : ret) {
			result.put((Long)objects[0], (Integer)objects[1]);
		}
		
		return result;
	}
	
	/**
	 * 获取某员工某段时间的上下班均未打卡的天数
	 * @deprecated
	 */
	@SuppressWarnings("rawtypes")
	public int getNoCardTimesById(Long staffid ,Date fromTime,Date toTime)throws Exception{
          Calendar d1 = Calendar.getInstance();  
		  Calendar d2 = Calendar.getInstance(); 
		  d1.setTime(fromTime);
		  d2.setTime(toTime);
		  long t1 = d1.getTime().getTime();
		  long t2 = d2.getTime().getTime();

          int day = (int)((t2 - t1) / (24*60*60*1000)) + 1;
		 
          int startDay = d1.get(Calendar.DAY_OF_WEEK);
		  int endDay = d2.get(Calendar.DAY_OF_WEEK);
		  if (day <= 7) {            //七天内:
                 if (startDay > endDay) {    // 1. 跨周末
	                day -= 2;
	             } 
	             else {            // 2. 同一周
	                if (startDay == 1) day--;
	                if (endDay == 7) day--;
	             }
	      } 
     	  else {
	             //先去掉头尾两周
                 day -= ((8 - startDay) + endDay);
		         //去掉中间的每周两天
		         day = day / 7 * 5;
                 //加回头尾两周的工作天
	             if (startDay == 1) day += 5;
	             if (startDay > 1 && startDay < 7) day += (7 - startDay);
	             if (endDay == 7) day += 5;
	             if (endDay < 7) day += (endDay - 1);
	      } 
			//查询时，需要把最后日期挪后一天
		  Calendar toCal = Calendar.getInstance();
		  toCal.setTime(toTime);
		  toCal.add(Calendar.DAY_OF_MONTH, 1);
		  Session session = super.getSession();
		  int days = 0;
		  try{
			  StringBuffer hql = new StringBuffer();
			  hql.append("from Record where staffer_id = :staffid  and (");
			  hql.append(" (begin_work_time >= :fromTime and begin_work_time < :toTime)");
			  hql.append(" or ");
			  hql.append(" (end_work_time >= :fromTime and end_work_time < :toTime)");
			  hql.append(")");
			  Query query = session.createQuery(hql.toString()).setLong("staffid", staffid)					                                                 
						                                                 .setDate("fromTime", fromTime)
						                                                 .setDate("toTime", toCal.getTime()); 
			  //buy xgghen 0704 添加对此人是不是有考勤记录的判断
			  StringBuffer hqls = new StringBuffer() ;
			  hqls.append("from Record where staffer_id = :staffid") ;
			  Query queryRep = session.createQuery(hqls.toString()).setLong("staffid", staffid);
			  List all = queryRep.list() ;
			  if(all.size() == 0){
				  days = 0 ;
				  return days ;
			  }
			  
			  List list = query.list();
	
			  days = day - list.size();
		  }catch(Exception ex){
			  System.out.print(ex) ;
			  throw ex;
		  }finally{
			  super.releaseSession(session);
		  }
		  return   days;   
	}


	/**
	 * 设置上下班时间
	 * @param 
	 * @return 
	 */
	public void setWorkingTime(RecordWorkingTime workingTime){
		if(workingTime.getId() == null){
			workingTime.setIdIfNew();
			this.save(workingTime);
		}else{
			this.update(workingTime);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Record> getRecord(Long staffid,Date fromTime,Date toTime)throws Exception{
		StringBuffer hql = new StringBuffer();
		List<Record> records = new ArrayList<Record>();
		Session session = super.getSession();
		//查询时，需要把最后日期挪后一天
		Calendar toCal = Calendar.getInstance();
		toCal.setTime(toTime);
		toCal.add(Calendar.DAY_OF_MONTH, 1);
		try{
			hql.append("from Record where staffer_id = :staffid and isWorkDay = 1 and (");
			hql.append(" (begin_work_time >= :fromTime and begin_work_time < :toTime)");
			hql.append(" or ");
			hql.append(" (end_work_time >= :fromTime and end_work_time < :toTime)");
			hql.append(") order by begin_work_time desc, end_work_time desc");
			Query query = session.createQuery(hql.toString()).setLong("staffid", staffid)					                                                 
	          																.setDate("fromTime", fromTime)
	          																.setDate("toTime", toCal.getTime()); 
			records = query.list();
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		return records;
	}
	
	public List<Record> findAllStaffRecord(Date fromTime,Date toTime)throws Exception{
		return findAllStaffRecordByPage(fromTime,toTime,-1, -1);
	}

	@SuppressWarnings("unchecked")
	public List<Record> findAllStaffRecordByPage(Date fromTime,Date toTime,int firstSize,int maxSize)throws Exception{
		StringBuffer hql = new StringBuffer();
		Long accountId = CurrentUser.get().getLoginAccount();
		//查询时，需要把最后日期挪后一天
		Calendar toCal = Calendar.getInstance();
		toCal.setTime(toTime);
		toCal.add(Calendar.DAY_OF_MONTH, 1);
			hql.append("from Record where accountId =:accountId and isWorkDay = 1 and(");
			hql.append(" (begin_work_time >= :fromTime and begin_work_time < :toTime)");
			hql.append(" or ");
			hql.append(" (end_work_time >= :fromTime and end_work_time < :toTime)");
			hql.append(") order by end_work_time desc, begin_work_time desc");
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accountId", accountId);
			params.put("fromTime", fromTime);
			params.put("toTime", toCal.getTime());
			
			//TODO 此处存在性能问题...应当直接进行分页，但由于调用地方较多，暂不处理，留到主干中统一清理和优化
			return this.find(hql.toString(), firstSize, maxSize, params);
	}
	public Record getRecordById(Long id){
		return this.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Record> findAllRecords(Date time)throws Exception {
		Long accountId = CurrentUser.get().getLoginAccount();
		Date fromTime = Datetimes.getFirstDayInMonth(time);
		Date toTime = Datetimes.getLastDayInMonth(time);
		String hql = "From Record where accountId=:accountId and isWorkDay = 1 and "
		+ "("
		+ "	year=year(:time) and month=month(:time) and day>=day(:fromTime)"
		+ " and day<=day(:toTime)" 
		+ ") order by end_work_time desc, begin_work_time desc";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		params.put("time", time);
		params.put("fromTime", fromTime);
		params.put("toTime", toTime);
		return this.find(hql, params);
	}
	
	/**
	 * 获得所有的考勤纪录
	 */
	@SuppressWarnings("unchecked")
	public List<Record> findallRecords(){
		String hql = "From Record order by end_work_time desc, begin_work_time desc";
		return this.find(hql);
	}
	
	@SuppressWarnings("unchecked")
	public List<Record> findRecordByState(int state)throws Exception {
		Long accountId = CurrentUser.get().getLoginAccount();
		String hql = "From Record where  accountId =? and state.id=? order by end_work_time desc, begin_work_time desc";
		return this.find(hql, accountId, state);
	}
	
	@SuppressWarnings("unchecked")
	public List<Record> findAllRecords(Date time,int state)throws Exception{
		Session session = super.getSession();
		List<Record> list= new ArrayList<Record>();
		try{
			String hql = "From Record where state=:state and ("
				+"("
				+"year(begin_work_time)=year(:time) and month(begin_work_time)=month(:time) and day(begin_work_time)=day(:time)" 
				+")"
				+"or"
				+"("
				+"year(end_work_time)=year(:time) and month(end_work_time)=month(:time) and day(end_work_time)=day(:time)"
				+")"
				+") order by end_work_time desc, begin_work_time desc";
			Query query = session.createQuery(hql).setDate("time", time).setInteger("state", state);
			list= query.list();
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Record> findAllStaffRecord(Date fromTime,Date toTime,int state)throws Exception{
				StringBuffer hql = new StringBuffer();
				Long accountId = CurrentUser.get().getLoginAccount();
					//查询时，需要把最后日期挪后一天 ?
					Calendar toCal = Calendar.getInstance();
					toCal.setTime(toTime);
					toCal.add(Calendar.DAY_OF_MONTH, 1);
					
					hql.append("from Record where accountId = :accountId ");
					hql.append(" and state.id=:state and(");
					hql.append(" (begin_work_time >= :fromTime and begin_work_time < :toTime)");
					hql.append(" or ");
					hql.append(" (end_work_time >= :fromTime and end_work_time < :toTime)");
					hql.append(") order by end_work_time desc, begin_work_time desc");
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("accountId", accountId);
					params.put("fromTime", fromTime);
					params.put("toTime", toCal.getTime());
					params.put("state", state);
					
					return this.find(hql.toString(), -1, -1, params);
			}
	
	//高级查询
	@SuppressWarnings("unchecked")
	public List<Record> advancedQuery(Date fromTime, Date toTime, int state, List<Long> departmentIds, List<Long> staffids) throws Exception {
		StringBuffer hql = new StringBuffer();
		StringBuffer beginWorkTimeSql = new StringBuffer();
		StringBuffer endWorkTimeSql = new StringBuffer();
		
		Long accountId = CurrentUser.get().getLoginAccount();
		
			if( fromTime!= null ){
				beginWorkTimeSql.append(" and begin_work_time >= :fromTime ");
				endWorkTimeSql.append(" and end_work_time >= :fromTime ");
			}
			
			
			Calendar toCal = Calendar.getInstance();
			if( toTime!=null ){
				toCal.setTime(toTime);
				//查询时，需要把最后日期挪后一天 ?
				toCal.add(Calendar.DAY_OF_MONTH, 1);
				
				beginWorkTimeSql.append("  and begin_work_time < :toTime ");
				endWorkTimeSql.append(" and end_work_time < :toTime ");
				
			}
			
			boolean hasDepIds = departmentIds != null && departmentIds.size() > 0;
			boolean hasPeoIds = staffids != null && staffids.size() > 0;
			boolean hasState = state != 0;
			hql.append("from Record where accountId = :accountId and isWorkDay = 1 ");
			hql.append(hasDepIds ? " and dep_id in (:depId) " : "");
			hql.append(hasPeoIds ? " and staffer_id in (:staffids) " : "");
			hql.append(hasState ? " and state.id=:state " : "");
			hql.append(" and(( 1=1 ").append(beginWorkTimeSql).append(")");
			hql.append(" or ");
			hql.append("     ( 1=1 ").append(endWorkTimeSql).append(")");
			hql.append(") order by end_work_time desc, begin_work_time desc");
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accountId", accountId);
			
			if( fromTime!= null ){
				params.put("fromTime", fromTime);
			}
			
			if( toTime!=null ){
				params.put("toTime", toCal.getTime());
			}
			
			
			if(hasState){
				params.put("state", state);
			}
			if(hasDepIds){
				params.put("depId", departmentIds);
			}
			if(hasPeoIds){
				params.put("staffids", staffids);
			}
			return this.find(hql.toString(), -1, -1, params);
	}
	
	@SuppressWarnings("unchecked")
	public List<RecordState> getAllRecordState() {
		return (List<RecordState>)super.find("from RecordState", -1, -1, null);
	}
	
	/**
	 * 删除考勤记录
	 * @param year
	 * @param month
	 * @param monthsAgo
	 */
	public void deleteAttendance(int year, int month, int monthsAgo) {
		String hql = null;
		Map<String, Object> params = new HashMap<String, Object>();
		if (monthsAgo == 12) {
			hql = "delete from " + Record.class.getName() + " where year < :nowYear";
			params.put("nowYear", year);
		} else if (monthsAgo == 24) {
			hql = "delete from " + Record.class.getName() + " where year < :nowYear";
			params.put("nowYear", year - 1);
		} else if (monthsAgo == 36) {
			hql = "delete from " + Record.class.getName() + " where year < :nowYear";
			params.put("nowYear", year - 2);
		} else if (monthsAgo == 60) {
			hql = "delete from " + Record.class.getName() + " where year < :nowYear";
			params.put("nowYear", year - 4);
		} else if (month - monthsAgo > 0) {
			hql = "delete from " + Record.class.getName() + " where (month < :nowMonth and year = :nowYear1) or year < :nowYear2 ";
			params.put("nowMonth", month - monthsAgo);
			params.put("nowYear1", year);
			params.put("nowYear2", year);
		} else {
			hql = "delete from " + Record.class.getName() + " where (month < :nowMonth and year = :nowYear1) or year < :nowYear2 ";
			params.put("nowMonth", 12 + month - monthsAgo);
			params.put("nowYear1", year - 1);
			params.put("nowYear2", year - 1);
		}
		this.bulkUpdate(hql, params);
	}

}
