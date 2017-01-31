package com.seeyon.v3x.calendar.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalInfo;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalRelation;
import com.seeyon.v3x.calendar.domain.CalEventStatistics;
import com.seeyon.v3x.calendar.domain.PeriodicalCalEvent;
import com.seeyon.v3x.calendar.util.Constants;
import com.seeyon.v3x.calendar.util.Constants.PeriodicalType;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;

/**
 * 事件的DAO
 * 
 * @author wolf
 * 
 */
public class CalEventDao extends BaseHibernateDao<CalEvent> {
	private static final Log logger = LogFactory.getLog(CalEventDao.class);
	
	/**
	 * 根据其他应用的ID的应用类型删除对应的事件
	 * @param appId	应用ID
	 * @param type		应用类型
	 */
	public void deleteCalEventFromOtherAppId(Long appId, Integer type,Long createUserId){
		String end = createUserId == null ? "" : " and cal.createUserId = ?";
		String hql = "delete from "+ CalEvent.class.getName() +" as cal where cal.fromId = ? and cal.fromType = ?" + end;
		Object[] values = createUserId == null ? new Object[]{appId, type} : new Object[]{appId, type,createUserId};
		try{
			super.bulkUpdate(hql, null, values);
		} catch(Exception e){
			logger.error("批量删除事件出错", e);
		}
	}
	
	/**
	 * 得到应用的ID和类型对应的所有日程事件对象，List的长度通常为0或1
	 * @param appId	应用ID
	 * @param type		应用类型
	 * @param createUserId		新建者ID
	 * @return 符合条件的日程事件List
	 */
	public List<CalEvent> findCalEventsByAppId(Long appId, Integer type,Long createUserId){
		String end = createUserId == null ? "" : " and cal.createUserId = ?";
		String hql = "from "+ CalEvent.class.getName() +" as cal where cal.fromId = ? and cal.fromType = ?" + end;
		return createUserId == null ? super.find(hql,new Object[]{appId,type}) : super.find(hql,new Object[]{appId,type,createUserId});
	}
	
	/**
	 * 得到 日程数据列表
	 * @param dc
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public List<CalEvent> getListCalEvent(DetachedCriteria dc){
		Session session = null;
		Criteria c = null;
		List<CalEvent> list = null;
		try{
			session = super.getSessionFactory().openSession();
			c = dc.getExecutableCriteria(session);
			list = paginate(c, Order.asc("beginDate"));
			
		}catch(Exception e){
			
		}finally{
			
			super.releaseSession(session);
		}
		return list;
	}
	
	/**
	 * 得到日程数据列表,但不分页
	 * @param dc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getListCalEvent1(DetachedCriteria dc){
	
		return super.executeCriteria(dc,-1,-1);
	}

	/**
	 * 根据ID集合获取事件列表
	 * @param eventIds 事件ID集合
	 * @param pagination 是否需要分页
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getEventByIds(List<Long> eventIds, boolean pagination) {
		String hql = "from " + CalEvent.class.getName() + " as c where c.id in (:eventIds) order by c.beginDate desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eventIds", eventIds);
		return pagination ? this.find(hql, params) : this.find(hql, -1, -1, params);
	}
	
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned) {
		return this.getEventList4Section(userId, self, arrangedOrConsigned, null, null);
	}
	
	/**
	 * 获取首页日程事件栏目中所要显示的日程事件内容
	 * 
	 * @param userId 当前用户ID
	 * @param self 是否包含自己创建的事件
	 * @param arrangedOrConsigned 是否包含他人安排或委托给自己的事件
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, Date beginDate, Date endDate) {
		boolean selfEvent = BooleanUtils.isTrue(self);
		boolean fromOthersEvent = BooleanUtils.isTrue(arrangedOrConsigned);

		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer(" from " + CalEvent.class.getName() + " event ");
		hql.append("where event.states < :finished and event.endDate >= :oneWeekBefore and event.beginDate <= :oneWeekAfter and event.createUserId=:userId ");

		Date todayFirst = Datetimes.getTodayFirstTime();
		Date todayLast = Datetimes.getTodayLastTime();

		if (beginDate == null) {
			beginDate = Datetimes.addDate(todayFirst, -7);
		}
		if (endDate == null) {
			endDate = Datetimes.addDate(todayLast, 14);
		}

		params.put("finished", Constants.Status_Finished);
		params.put("oneWeekBefore", beginDate);
		params.put("oneWeekAfter", endDate);
		params.put("userId", userId);

		List<CalEvent> result = new ArrayList<CalEvent>();

		if (selfEvent) {
			List<CalEvent> list1 = this.find(hql.toString(), -1, -1, params);
			if (CollectionUtils.isNotEmpty(list1)) {
				result.addAll(list1);
			}
		}
		if (fromOthersEvent) {
			List<CalEvent> list2 = this.getEventTranList(userId, beginDate, endDate);
			if (CollectionUtils.isNotEmpty(list2)) {
				result.addAll(list2);
			}
		}

		return result;
	}

	/**
	 * 获取他人安排或委托给自己的事件
	 */
	@SuppressWarnings("unchecked")
	private List<CalEvent> getEventTranList(Long userId, Date oneWeekBefore, Date oneWeekAfter) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder("select event from CalEvent event, CalEventTran tran where event.id = tran.eventId ");
		hql.append(" and event.states < :finished and event.beginDate >= :oneWeekBefore and event.beginDate <= :oneWeekAfter ");
		hql.append(" and tran.receiveId = :userId ");
		hql.append(" and tran.receiveId <> tran.sourceRecordId "); // 委托安排人不是自己
		
		params.put("finished", Constants.Status_Finished);
		params.put("oneWeekBefore", oneWeekBefore);
		params.put("oneWeekAfter", oneWeekAfter);
		params.put("userId", userId);
		
		return this.find(hql.toString(), -1, -1, params);
	}

	/**
	 * 获取当日需要生成重复事件的全部周期性事件副本内容
	 */
	public List<PeriodicalCalEvent> getPeriodicalEvents() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int day_date = calendar.get(Calendar.DAY_OF_MONTH);
		int day_week = calendar.get(Calendar.DAY_OF_WEEK);
		int week = calendar.get(Calendar.WEEK_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		
		Date today = Datetimes.getTodayFirstTime();//过滤已经创建的
		Date todayTime = new Date();
		String hql = "select e from " + PeriodicalCalEvent.class.getName() + " as e, " +
					  CalEventPeriodicalInfo.class.getName() + " as p " +
					 "where e.id=p.calEventId and " +
					 "((p.periodicalType=?) or " +
					 //"(p.periodicalType=? and p.dayWeek=?) or " +
					 "(p.periodicalType=?) or " +
					 "(p.periodicalType=? and p.dayDate=?) or " +
					 "(p.periodicalType=? and p.week=? and p.dayWeek=?) or " +
					 "(p.periodicalType=? and p.month=? and p.dayDate=?) or " +
					 "(p.periodicalType=? and p.month=? and p.week=? and p.dayWeek=?))" +
					 " and p.beginTime<=? and (p.endTime >=? or p.endTime is null)" + 
					 " and (not exists(select r.calEventPeriodicalInfoId from "+CalEventPeriodicalRelation.class.getName()+" as r "+
					 " where r.calEventPeriodicalInfoId=p.id and r.createDate=?))";
		
		return this.find(hql,-1,-1,new HashMap<String,Object>(), PeriodicalType.EveryDay.ordinal(),
							  PeriodicalType.EveryWeek.ordinal(),
							  PeriodicalType.EveryMonthDay.ordinal(), day_date,
							  PeriodicalType.EveryMonthWeekDay.ordinal(), week, day_week,
							  PeriodicalType.EveryYearMonthDay.ordinal(), month, day_date,
							  PeriodicalType.EveryYearMonthWeekDay.ordinal(), month, week, day_week,
							  todayTime,todayTime,
							  today
							  );
	}

	public CalEventPeriodicalInfo getPeriodicalEvent(Long id){
		CalEventPeriodicalInfo info = null;
		String hql = "from "+PeriodicalCalEvent.class.getName()+" as e, "+
				CalEventPeriodicalInfo.class.getName()+" as p where e.id=p.calEventId and p.id=? ";
		List<Object[]> result = super.find(hql,-1,-1,null,id);
		if(result != null){
			for(Object[] os : result){
				PeriodicalCalEvent cal = (PeriodicalCalEvent) os[0];
				info = (CalEventPeriodicalInfo) os[1];
				info.setCalEvent(cal);
			}
		}
		return info;
	}
	/**
	 * 根据周期事件id获得周期性信息
	 * @param periodicalId
	 * @return
	 */
	public CalEventPeriodicalInfo getPeriodicalInfoByPeriodicalEvent(Long periodicalId){
		CalEventPeriodicalInfo info = null;
		String hql = "from " + CalEventPeriodicalInfo.class.getName() + " as i,"
					+ PeriodicalCalEvent.class.getName() + " as e "
					+ "where e.id=i.calEventId and i.calEventId=?";
		List<Object[]> result = super.find(hql, -1, -1, null, periodicalId);
		if(result != null){
			for(Object[] o : result){
				info = (CalEventPeriodicalInfo) o[0];
			}
		}
		return info;
	}
	/**
	 * 根据日程事件id获得周期性信息
	 * @param id
	 * @return
	 */
	public CalEventPeriodicalInfo getCalEventPeriodicalInfoByCalEventId(Long id){
		CalEventPeriodicalInfo info = null;
		String hql = "from " + CalEventPeriodicalInfo.class.getName() + " as i,"
					+ CalEventPeriodicalRelation.class.getName() + " as r "
					+ "where i.id=r.calEventPeriodicalInfoId and r.calEventId=?";
		List<Object[]> result = super.find(hql, -1, -1, null, id);
		if(result != null){
			for(Object[] o : result){
				info = (CalEventPeriodicalInfo) o[0];
			}
		}
		return info;
	}
	
	/**
	 * 根据日程事件id获得周期性关系信息
	 * @param id
	 * @return
	 */
	public CalEventPeriodicalRelation getCalEventPeriodicalRelationByCalEventId(Long id){
		CalEventPeriodicalRelation relation = null;
		String hql = "from " + CalEventPeriodicalRelation.class.getName() + " as r "
					+ "where r.calEventId=?";
		List<CalEventPeriodicalRelation> result = super.find(hql, -1, -1, null, id);
		if(result != null && result.size() > 0){
			return result.get(0);
		}
		return relation;
	}
	
	/**
	 * 得到某个时间段、某个完成状态下所有个人事件
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @param states
	 * @param statistics
	 * @return
	 */
	public List<CalEventStatistics> getEventListByUserId(Long userId,
			Date beginDate, Date endDate, Integer states, Integer statistics) {
		Map<String, Object> params = new HashMap<String, Object>();
		String statisticsType = statistics == 0 ? "signifyType"
				: "calEventType";
		StringBuffer hql = new StringBuffer("select " + statisticsType
				+ ", count(*), sum(c.realEstimateTime) from "
				+ CalEvent.class.getName() + " as c "
				+ "where c.beginDate >= :beginDate and c.endDate <= :endDate ");
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);
		if (states != 0 && states != 5) {
			hql.append("and c.states = :states");
			params.put("states", states);
		}else if(states == 5){
			hql.append("and c.states != :states");
			params.put("states", 4);
		}
		hql
				.append(" and (c.createUserId = :userId or c.receiveMemberId like :userScopeId) group by "
						+ statisticsType + " ");
		params.put("userId", userId);
		params.put("userScopeId", "%"
				+ SQLWildcardUtil.escape(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|"
						+ userId) + "%");
		List<Object[]> list = this.find(hql.toString(), -1, -1, params);
		List<CalEventStatistics> statisticsList = new ArrayList<CalEventStatistics>();
		if (list != null && !list.isEmpty()) {
			for (Object[] o : list) {
				CalEventStatistics c = new CalEventStatistics();
				if(o[0] != null){
					c.setType((Integer) o[0]);
				}else{
					c.setType(8);
				}
				c.setCounts((Integer) o[1]);
				c.setSumTime((Float) o[2]);
				statisticsList.add(c);
			}
		}
		return statisticsList;
	}
	/**
	 * 得到某个时间段、某个完成状态下、根据统计类型查出的个人事件
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @param states
	 * @param statistics
	 * @param value
	 * @return
	 */
	public List<CalEvent> getEventListByUserId(Long userId, Date beginDate,
			Date endDate, Integer states, Integer statistics, Integer value) {
		Map<String, Object> params = new HashMap<String, Object>();
		String statisticsType = statistics == 0 ? "signifyType"
				: "calEventType";
		StringBuffer hql = new StringBuffer("from " + CalEvent.class.getName()
				+ " as c "
				+ "where c.beginDate >= :beginDate and c.endDate <= :endDate ");
		params.put("beginDate", beginDate);
		params.put("endDate", endDate);
		if (states != 0 && states != 5) {
			hql.append("and c.states = :states ");
			params.put("states", states);
		}else if(states == 5){
			hql.append("and c.states != :states ");
			params.put("states", 4);
		}
		if(value==8){
			hql.append("and c." + statisticsType + " is null");
		}else{
			hql.append("and c." + statisticsType + " = :statisticsType");
			params.put("statisticsType", value);
		}
		hql
				.append(" and (c.createUserId = :userId or c.receiveMemberId like :userScopeId) ");
		params.put("userId", userId);
		params.put("userScopeId", "%"
				+ SQLWildcardUtil.escape(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|"
						+ userId) + "%");
		List<CalEvent> list = this.find(hql.toString(),  params);
		return list;
	}
}
