package com.seeyon.v3x.calendar.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.calendar.constants.CalEventComparator;
import com.seeyon.v3x.calendar.constants.EventType;
import com.seeyon.v3x.calendar.constants.ShareType;
import com.seeyon.v3x.calendar.dao.CalEventDao;
import com.seeyon.v3x.calendar.domain.AbstractCalEvent;
import com.seeyon.v3x.calendar.domain.CalContent;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalInfo;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalRelation;
import com.seeyon.v3x.calendar.domain.CalEventStatistics;
import com.seeyon.v3x.calendar.domain.CalEventTran;
import com.seeyon.v3x.calendar.domain.CalReply;
import com.seeyon.v3x.calendar.domain.PeriodicalCalEvent;
import com.seeyon.v3x.calendar.util.CalendarNotifier;
import com.seeyon.v3x.calendar.util.CalendarUtils;
import com.seeyon.v3x.calendar.util.Constants;
import com.seeyon.v3x.calendar.util.EventRemind;
import com.seeyon.v3x.calendar.util.PeriodicalEventUtil;
import com.seeyon.v3x.calendar.util.Constants.PeriodicalType;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.meeting.dao.MtMeetingDao;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.domain.PeopleRelate;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;

public class CalEventManagerImpl extends BaseCalendarManager implements CalEventManager,IndexEnable {

	private static final Log log = LogFactory.getLog(CalEventManagerImpl.class);
	
	private static final String CalendarResources = "com.seeyon.v3x.calendar.resources.i18n.CalendarResources";

	private CalEventDao calEventDao;

	private CalendarUtils calendarUtils;

	private AttachmentManager attachmentManager;

	private CalEventTranManager calEventTranManager;

	private OrgManager orgManager;

	private Constants constants;

	private CalContentManager calContentManager;

	// 关联项目
	private ProjectManager projectManager;
	
	private UserMessageManager userMessageManager;

	// 全文检索
	private IndexManager indexManager;
	
	private PeopleRelateManager peopleRelateManager;
	
	private AppLogManager appLogManager;
	
	private CalReplyManager calReplyManager;
	
	private ProjectPhaseEventManager projectPhaseEventManager;
	
	private MtMeetingDao mtMeetingDao;
	
	public MtMeetingDao getMtMeetingDao() {
		return mtMeetingDao;
	}

	public void setMtMeetingDao(MtMeetingDao mtMeetingDao) {
		this.mtMeetingDao = mtMeetingDao;
	}

	public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}

	public void setCalReplyManager(CalReplyManager calReplyManager) {
		this.calReplyManager = calReplyManager;
	}

	public PeopleRelateManager getPeopleRelateManager() {
		return peopleRelateManager;
	}

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}

	public CalendarUtils getCalendarUtils() {
		return calendarUtils;
	}

	public void setCalendarUtils(CalendarUtils calendarUtils) {
		this.calendarUtils = calendarUtils;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public CalEventDao getCalEventDao() {
		return calEventDao;
	}

	public void setCalEventDao(CalEventDao calEventDao) {
		this.calEventDao = calEventDao;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public CalContentManager getCalContentManager() {
		return calContentManager;
	}

	public void setCalContentManager(CalContentManager calContentManager) {
		this.calContentManager = calContentManager;
	}

	public Constants getConstants() {
		return constants;
	}

	public void setConstants(Constants constants) {
		this.constants = constants;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public CalEventTranManager getCalEventTranManager() {
		return calEventTranManager;
	}

	public void setCalEventTranManager(CalEventTranManager calEventTranManager) {
		this.calEventTranManager = calEventTranManager;
	}
	private Integer showFatherDay = 0;
	
	public Integer getShowFatherDay() {
		return showFatherDay;
	}

	public void setShowFatherDay(Integer showFatherDay) {
		this.showFatherDay = showFatherDay;
	}
	
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	/**
	 * -----保存
	 * 
	 */
	public Long save(CalEvent event) {
		boolean isNew = event.isNew();
		if (isNew)
			event.setIdIfNew();
		return this.save(event, isNew);
	}

	/**
	 * -----保存
	 * 
	 */
	public Long save(CalEvent event, boolean isNew) {
		if (isNew) {
			this.calEventDao.save(event);
		} else {
			this.calEventDao.update(event);
		}
		return event.getId();
	}
	
	public Long save(PeriodicalCalEvent event, boolean isNew) {
		if (isNew) {
			this.calEventDao.save(event);
		} else {
			this.calEventDao.update(event);
		}
		return event.getId();
	}
	/**
	 * -----删除
	 * 
	 */
	public void deleteById(Long id) {
		this.calEventDao.delete(id.longValue());
	}

	/**
	 * -----查询---根据事件ID
	 * 
	 */
	public CalEvent getEventById(Long id) {
		return (CalEvent)initEvent(calEventDao.get(id));
	}

	public CalEvent getEventByIdNoInit(Long id) {
		return calEventDao.get(id);
	}

	// ---------初始化s
	
	/**
	 * 对事件列表进行初始化
	 * 
	 * @param list
	 * @return
	 */
	public List<CalEvent> initList(List<CalEvent> list) {
		if(list!=null){
			for (CalEvent event : list) {
				EventDateRange.setEventDateInfo(event);
				initEvent(event);
			}
		}else{
			list = new ArrayList<CalEvent>();
		}
		return list;
	}

	public AbstractCalEvent initEvent(AbstractCalEvent event){
		Calendar begCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		Calendar newCalendar = Calendar.getInstance();
		if (event == null)
			return event;
		Date date = new Date();
		newCalendar.setTime(date);
		// 事件创建者的名字
		event.setCreateUserName(calendarUtils.getMemberNameByUserId(event
				.getCreateUserId()));
		// 取得明天的时间
		GregorianCalendar tomorrow = new GregorianCalendar();
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		//Date tomorrow = calendar.getTime();

		// 列表时间段的现实标识
		if (event.getEndDate() != null) {
			begCalendar.setTime(event.getBeginDate());
			endCalendar.setTime(event.getEndDate());
			int begYear = begCalendar.get(Calendar.YEAR);
			int begMonth = begCalendar.get(Calendar.MONTH);
			int begDay = begCalendar.get(Calendar.DAY_OF_MONTH);

			int newYear = newCalendar.get(Calendar.YEAR);
			int newMonth = newCalendar.get(Calendar.MONTH);
			int newDay = newCalendar.get(Calendar.DAY_OF_MONTH);

			int endYear = endCalendar.get(Calendar.YEAR);
			int endMoth = endCalendar.get(Calendar.MONTH);
			int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
			// 今日事件的几种情况
			// 1.大条件，事件的开始日期小于等于当前日期
			if (begYear < newYear
					|| (begYear == newYear && begMonth < newMonth)
					|| (begYear == newYear
							&& begMonth == newMonth && begDay <= newDay)) {
				// 如果结束时间的年分大于当前年份
				if (endYear > newYear) {
					// 事件在跨年范围内的今日事件
					event.setTimeFlag("today_year");
				} else // 如果结束时间的年份等于当前时间的年
				if (endYear== newYear) {
					// 如果结束时间的月份大于当前时间的月份
					if (endMoth > newMonth) {
						// 事件在跨月范围内的今日事件
						event.setTimeFlag("today_Month");
					} else // 如果年月都相同
					if (endMoth == newMonth) {
						// 结束日期大于当前时间
						if (endDay > newDay) {
							// 事件在跨日
							event.setTimeFlag("today_day");

						} else if (endDay == newDay) {

							if (begDay ==endDay) {
								// 当日事件
								event.setTimeFlag("today_oneday");
							} else if (begDay != endDay) {

								// 当日事件
								event.setTimeFlag("today_lastday");
							}
						} else if (endDay < newDay) {
							event.setTimeFlag("old");
						}
					} else if (endMoth < newMonth) {
						event.setTimeFlag("old");
					}
				} else if (endYear < newYear) {
					event.setTimeFlag("old");
				}
			}// 明日事件的几种情况
			else if ((begYear == tomorrow.get(Calendar.YEAR)
					&& begMonth == tomorrow.get(Calendar.MONTH) && begDay == tomorrow.get(Calendar.DAY_OF_MONTH))) {
				// 结束年份大于明天的年分--明天的跨年事件
				if (endYear > tomorrow.get(Calendar.YEAR)) {
					event.setTimeFlag("tomorrow_year");
				} else
				// 结束年份等于明天的年分
				if (endYear == tomorrow.get(Calendar.YEAR)) {
					// 结束月份大于明天的月分--明天的跨月事件
					if (endMoth > tomorrow.get(Calendar.MONTH)) {

						event.setTimeFlag("tomorrow_month");
					} else
					// 结束月份等于明天的月分
					if (endMoth == tomorrow.get(Calendar.MONTH)) {
						// 同一个月的明日事件
						if (endDay > tomorrow.get(Calendar.DAY_OF_MONTH)) {
							// 同一个月的明日事件
							event.setTimeFlag("tomorrow_day");
						} else if (endDay == tomorrow.get(Calendar.DAY_OF_MONTH)) {
							event.setTimeFlag("tomorrow_oneday");
						}
						// 同一个月的明日事件
					}
				}

			} else if (begYear > newYear
					|| (begYear == newYear && begMonth > newMonth)
					|| (begYear == newYear
							&& begMonth == newMonth && begDay > tomorrow.get(Calendar.DAY_OF_MONTH))) {
				event.setTimeFlag("future");
			}
		}
		if(event.getShareType()!=null && event.getShareType()==6){
			try {
				String id = event.getTranMemberIds();
				if(id!=null&&id.length()!=0){
					event.setShareTarget(projectManager.getProject(Long.parseLong((id))).getProjectName());
				}
			} catch (Exception e) {
				log.error("初始化事件错误");
			}

		}
		return event;
	}
	
	// -------------- 主要頁面的列表顯示
	
	/**
	 *  -----首页---个人空间---个人事件
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getEventListByUserIdForFirst(Long userId,Boolean personal,Boolean relete) {
		List<CalEvent> allEventList = new ArrayList<CalEvent>();
        List<CalEvent> list1 = getEventListByUserIdForFirstNopage(userId,personal,relete);

		if (list1 != null)
			allEventList.addAll(list1);
		if (allEventList.size() != 0) {
			
// 排序 去重复分页
			List listed=CommonTools.pagenate(allEventList);
			Collections.sort(listed,calEventComparator);
		    return initList(listed);
		} else
			return null;
	}

	/**
	 *  -----首页---个人空间---个人事件---未分页 包括个人事件和委托事件
	 * 
	 */
	private List<CalEvent> getEventListByUserIdForFirstNopage(Long userId) {
		return getEventListByUserIdForFirstNopage(userId,true,true);
	}
	
	private List<CalEvent> getEventListByUserIdForFirstNopage(Long userId,Boolean personal,Boolean relete) {
		List<CalEvent> allList = new ArrayList<CalEvent>();// 总和事件list

		Date todayF = Datetimes.getTodayFirstTime();
		if(personal.booleanValue()){
			// 只取自建和公开给部门的事件并且是未完成状态的事件
			StringBuilder builder = new StringBuilder("from CalEvent event ");
			builder.append(" where event.createUserId=? ");
			builder.append(" and event.states <> '4' ");
			builder.append(" and ( event.beginDate >=?  or event.endDate >=? )");
			
			// 完成状态不为已完成		
			List<CalEvent> list = calEventDao.find(builder.toString(), new Object[]{userId, todayF, todayF });
			if (list != null && list.size() != 0)
				allList.addAll((List<CalEvent>) list);
		}
		if(relete.booleanValue()){
			// 取得别人给我并且是未完成状态的事件
			StringBuilder otherForMeSql = new StringBuilder(" select event from CalEvent event, CalEventTran tran ");
			// 完成状态不为已完成
			otherForMeSql.append(" where tran.receiveId <> tran.sourceRecordId ");
			otherForMeSql.append(" and tran.receiveId =? ");
			otherForMeSql.append(" and tran.eventId = event.id ");
			otherForMeSql.append(" and event.states <> '4' ");
			otherForMeSql.append(" and ( event.beginDate >=?  or event.endDate >=? ) ");

			List<CalEvent> list2 = calEventDao.find(otherForMeSql.toString(), new Object[]{userId, todayF, todayF });
			if (list2 != null && list2.size() != 0)
				allList.addAll((List<CalEvent>) list2);
		}

		if (allList != null && allList.size() != 0)
			return allList; 
		else
			return null;
	}
	
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, int viewCount) {
		return this.getEventList4Section(userId, self, arrangedOrConsigned, viewCount, null, null);
	}

	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, int viewCount, Date beginDate, Date endDate) {
		List<CalEvent> all = this.calEventDao.getEventList4Section(userId, self, arrangedOrConsigned, beginDate, endDate);
		if(showFatherDay != 0){
			Date todayDate = Datetimes.getTodayFirstTime();
			Date afterAweekDate = Datetimes.addDate(todayDate, showFatherDay);
			List<CalEvent> per = preCreateEvent(userId, todayDate, afterAweekDate,0);
			all.addAll(per);
		}
		
		List<CalEvent> result = new ArrayList<CalEvent>();
		List<CalEvent> today = new ArrayList<CalEvent>();
		List<CalEvent> later = new ArrayList<CalEvent>();
		List<CalEvent> earlier = new ArrayList<CalEvent>();
		
		if(CollectionUtils.isNotEmpty(all)) {
			Integer sort = null;
			for(CalEvent event : all) {
				sort = EventDateRange.setEventDateInfo(event);
				if(sort == Constants.DateRangeType.today.ordinal() || sort == Constants.DateRangeType.antipodean.ordinal()) {
					today.add(event);
				} 
				else if(sort == Constants.DateRangeType.tomorrow.ordinal() || sort == Constants.DateRangeType.later.ordinal()) {
					later.add(event);
				} 
				else if(sort == Constants.DateRangeType.earlier.ordinal()) {
					earlier.add(event);
				}
			}
		}
		
		int todayCount = CommonTools.getSizeIgnoreEmpty(today);
		int laterCount = CommonTools.getSizeIgnoreEmpty(later);
		int earlierCount = CommonTools.getSizeIgnoreEmpty(earlier);
		
		if (earlierCount >= 2) {
			earlierCount = 2;
		}

		viewCount = viewCount - earlierCount;

		if (todayCount >= viewCount) {
			laterCount = 0;
			todayCount = viewCount;
		} else {
			laterCount = viewCount - todayCount;
		}

		if (CollectionUtils.isNotEmpty(today)) {
			Collections.sort(today);
		}
		today = CommonTools.getSubList(today, 0, todayCount);

		if (CollectionUtils.isNotEmpty(later)) {
			Collections.sort(later);
		}
		later = CommonTools.getSubList(later, 0, laterCount);

		if (CollectionUtils.isNotEmpty(earlier)) {
			Collections.sort(earlier);
			Collections.reverse(earlier);
		}
		earlier = CommonTools.getSubList(earlier, 0, earlierCount);

		CommonTools.addAllIgnoreEmpty(result, today);
		CommonTools.addAllIgnoreEmpty(result, later);
		CommonTools.addAllIgnoreEmpty(result, earlier);
		return result;
	}
	
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, Date beginDate, Date endDate) {
		List<CalEvent> all = this.calEventDao.getEventList4Section(userId, self, arrangedOrConsigned, beginDate, endDate);
		List<CalEvent> per = preCreateEvent(userId, beginDate, endDate, 2);
		all.addAll(per);
		return all;
	}
	
	/**
	 *  -----首页---个人空间---个人事件---总数
	 * 
	 */
	public int getEventListCountByUserIdForFirst(Long userId) {
		//取总数
		List<CalEvent> list = getEventListByUserIdForFirstNopage(userId);
		if (list == null)
			return 0;
		else
			return list.size();
	}
	
	/**
	 *  -----首页---个人空间---他人事件--+--模块主页---共享事件---他人事件
	 * 
	 */
	public List<CalEvent> getOtherEventListByUserIdForFirst(User user,
			PeopleRelateManager peopleRelateManager, String condition, String value) {
		// 下级
		List<CalEvent> juniorEventList = getJuniorEvent(user,peopleRelateManager,null,null,null);
		// 上级
		List<CalEvent> superiorEventList = getSuperiorEvent(user,peopleRelateManager,null,null,null);
		// 秘书助手的事件
		List<CalEvent> helperEventList = getHelperEvent(user,peopleRelateManager,null,null,null);
		// 共享事件列表
		List<CalEvent> otherEventList = getOtherShareEvent(user, condition, value);

		List<CalEvent> allEventList = new ArrayList<CalEvent>();

		if (juniorEventList != null)
			allEventList.addAll(juniorEventList);
		if (superiorEventList != null)
			allEventList.addAll(superiorEventList);
		if (otherEventList != null)
			allEventList.addAll(otherEventList);
		if (helperEventList != null)
			allEventList.addAll(helperEventList);
		if (allEventList.size() != 0) {
			
//		排序 去重复分页	

			//List listed=this.pagenate(allEventList);
			List<CalEvent> newEventList = new ArrayList<CalEvent>();
			List<CalEvent> allEventLists = new ArrayList<CalEvent>();
			if(allEventList!=null){
				Collections.sort(allEventList);
				int size = allEventList.size()-1;
				for(int i=size;i>=0;i--){
					newEventList.add((CalEvent) allEventList.get(i));
				}
			}
			initList(newEventList);
			if (Strings.isNotBlank(condition) && condition.equals(Constants.RECEIVEMEMBERNAME)) {
				for (CalEvent cal: newEventList) {
					if (value.equals(cal.getCreateUserName()) || value.equals(cal.getReceiverMember())) {
						allEventLists.add(cal);
					}
				}
			} else {
				allEventLists = newEventList;
			}
		    return allEventLists;

		} else
			return null;
	}
	
	public List<CalEvent> getEventListByDeptIds(List<Long> deptId,String condition, String value) {
		try {
			StringBuilder buf = new StringBuilder(" from CalEvent event2 where event2.id in( ");
			buf.append("select distinct event.id from CalEvent event , CalEventTran tran where ");
			buf.append(" event.shareType='5' and (tran.entityId in (:trans)) ");
			buf.append(" and event.id = tran.eventId )");
			Map<String,Object> parameter = new HashMap<String,Object>();
			parameter.put("trans", deptId);
			if(Strings.isNotBlank(condition) && Strings.isNotBlank(value) && !condition.equals(Constants.RECEIVEMEMBERNAME)){
				if(condition.equals(Constants.SUBJECT) ){
					buf.append(" and event2." + condition + " like :subject");
					parameter.put("subject", "%" + SQLWildcardUtil.escape(value.trim()) + "%");
				}else if(condition.equals(Constants.BEGINDATE)){
					Date todayF = Datetimes.getTodayFirstTime(value);
					Date todayE = Datetimes.getTodayLastTime(value);
					buf.append(" and event2.beginDate >=:todayF and event2.beginDate <=:todayE");
					parameter.put("todayF", todayF);
					parameter.put("todayE", todayE);
				}else if(condition.equals(Constants.STATES)){
					if(Integer.parseInt(value) == 5){
						buf.append(" and event2." + condition + "!=:value");
						parameter.put("value", 4);
					}else{
						buf.append(" and event2." + condition + "=:value");
						parameter.put("value", Integer.parseInt(value));
					}
				}else{
					//重要程度
					buf.append(" and event2." + condition + "=:value");
					parameter.put("value", Integer.parseInt(value));
				}
			}
			buf.append(" order by event2.beginDate desc ");
			List<CalEvent> ret = calEventDao.find(buf.toString(),-1,-1, parameter);
			return initList(ret);
		} catch (Exception e) {
			log.error("",e);
		}
		return null;
	}
	public List<CalEvent> getEventListByDeptIds4Section(List<Long> deptId) {
		try {
			StringBuilder buf = new StringBuilder(" from CalEvent event2 where event2.id in( ");
			buf.append("select distinct event.id from CalEvent event , CalEventTran tran where ");
			buf.append(" event.shareType='5' and (tran.entityId in (:trans)) ");
			buf.append(" and event.id = tran.eventId )");
			buf.append(" and event2.completeRate < '100' and event2.states < '4' ");
			buf.append(" order by event2.beginDate desc ");
			Map<String,Object> parameter = new HashMap<String,Object>();
			parameter.put("trans", deptId);
			List<CalEvent> ret = calEventDao.find(buf.toString(),-1,-1, parameter);
			return initList(ret);
		} catch (Exception e) {
			log.error("",e);
		}
		return null;
	}
	
	/**
	 *  -----首页---部门空间---部门事件--+--模块主页---共享事件---部门事件
	 * 
	 */
	public List<CalEvent> getEventListByDeptId(Long deptId) {
		List<V3xOrgDepartment> parentDepartments;
		List<V3xOrgDepartment> list = new ArrayList<V3xOrgDepartment>();
		List<Long> departmentIds = new ArrayList<Long>();
		try {
			// 历史原因，部门事件可以配置到个人空间中，导致参数传递错误，此处先进行校验
			V3xOrgDepartment department = orgManager.getDepartmentById(deptId);
			if(department == null)
				return null;
			
			list.add(department);// 先加所属本部门
			// 取得所有的子部门，包括子部门的子部门
			parentDepartments = orgManager.getAllParentDepartments(deptId);
			if (parentDepartments != null && parentDepartments.size() != 0)
				list.addAll(parentDepartments);
			for(V3xOrgDepartment dep:list){
				departmentIds.add(dep.getId());
			}
			return getEventListByDeptIds4Section(departmentIds);

		} catch (BusinessException e) {
			log.error("获取部门事件时出现异常[部门ID=" + deptId + "]:", e);
		}
		return null;
	}

//   ...
	
	/**
	 *  -----模块主页---个人事件列表
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getEventListByUserId(Long userId,boolean personal,boolean relete) {

		List<CalEvent> allList = new ArrayList<CalEvent>();// 总和事件list
		if(personal){
			// 只取自建 事件并且是未完成状态的事件
			String selfCreateSql = "select event from CalEvent event where event.createUserId=? ";
			
			List list = calEventDao.find(selfCreateSql,new Object[] { userId });
			if (list != null && list.size() != 0)
				allList.addAll((List<CalEvent>) list);
		}
		if(relete){
			// 取得别人给我并且是未完成状态的事件
			StringBuilder otherForMeSql = new StringBuilder("select event from CalEvent event, CalEventTran tran ");
			otherForMeSql.append(" where tran.receiveId <> tran.sourceRecordId ");// 委托安排人不是自己
			otherForMeSql.append(" and tran.receiveId =? ");
			otherForMeSql.append(" and tran.eventId = event.id  ");
			otherForMeSql.append("");
			
			List<CalEvent> list2 = calEventDao.find(otherForMeSql.toString(), new Object[] { userId });
			allList.addAll((List<CalEvent>) list2);
		}
		
//	分页	
		List<CalEvent> endlist = allList;
		if(showFatherDay > 0){
			Date todayDate = Datetimes.getTodayFirstTime();
			Date afterAweekDate = Datetimes.addDate(todayDate, showFatherDay);
			List<CalEvent> per = preCreateEvent(userId, todayDate, afterAweekDate,0,null,null);
			endlist.addAll(per);
		}
		if (endlist == null || endlist.size() == 0)
			return null;
		
// 反其他的列表的排序
		Collections.sort(endlist, calEventComparator);
		
		return initList((List<CalEvent>) endlist);
	}
	
	private static CalEventComparator calEventComparator = new CalEventComparator();
	
	/**
	 *  -----模块主页---个人事件列表----按条件查询
	 * 
	 * @param type    条件类型 例：标题 重要程度 状态 开始时间
	 * @param value   相对的值
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getEventListByUserId(Long userId, String type,
			String value) {
		List<CalEvent> allList = new ArrayList<CalEvent>();// 总和事件list
		List<Object> parameters = new ArrayList<Object>();

		// 根据过来的条件，取得数据
		StringBuilder selfCreateSql = new StringBuilder("select event from CalEvent event where event.createUserId=? ");
		parameters.add(userId);
		List<CalEvent> list2 = new ArrayList<CalEvent>();

		if (type.equalsIgnoreCase("workType")) {
			selfCreateSql.append(" and event.workType = ? ");
			parameters.add(Integer.parseInt(value));
		} // 如果是工作类型

		else if (type.equalsIgnoreCase("signifyType")) { // 如果是重要程度
			selfCreateSql.append(" and event.signifyType= ? ");
			parameters.add(Integer.parseInt(value));
		} else if (type.equalsIgnoreCase("subject")) { // 如果是标题
			selfCreateSql.append(" and event.subject like ? ");
			parameters.add("%" + value + "%");
		}
		// 如果是开始时间
		else if (type.equalsIgnoreCase("beginDate")) {
			Date todayF = Datetimes.getTodayFirstTime(value);
			Date todayE = Datetimes.getTodayLastTime(value);

			selfCreateSql.append(" and event.beginDate >=? and event.beginDate <= ?");
			parameters.add(todayF);
			parameters.add(todayE);
		} else if (type.equalsIgnoreCase("states")) { // 如果是状态
			if(Integer.parseInt(value) == 5){
				selfCreateSql.append(" and event.states != ? ");
				parameters.add(4);
			}else{
				selfCreateSql.append(" and event.states = ? ");
				parameters.add(Integer.parseInt(value));
			}
		} else if (type.equalsIgnoreCase("eventType")) { //如果是事件类型
			selfCreateSql.append(" and event.calEventType = ? ");
			parameters.add(Integer.parseInt(value));
		}

		List<CalEvent> list = calEventDao.find(selfCreateSql.toString(),parameters.toArray(new Object[parameters.size()]));
		if (list != null && list.size() != 0)
			allList.addAll(list);
		if(showFatherDay > 0){
			Date todayDate = Datetimes.getTodayFirstTime();
			Date afterAweekDate = Datetimes.addDate(todayDate, showFatherDay);
			List<CalEvent> per = preCreateEvent(userId, todayDate, afterAweekDate,0,type,value);
			allList.addAll(per);
		}
		
		List<Object> parameters1 = new ArrayList<Object>();
		parameters1.add(userId);
		// 取得别人给我并且是未完成状态的事件
		StringBuilder otherForMeSql = new StringBuilder("select event from  CalEvent event, CalEventTran tran ");
		otherForMeSql.append(" where tran.receiveId =? ");
		otherForMeSql.append(" and tran.receiveId <> tran.sourceRecordId "); // 委托安排人不是自己
		otherForMeSql.append(" and tran.eventId = event.id ");

		if (type.equalsIgnoreCase("workType")) { // 如果是工作类型
			otherForMeSql.append(" and event.workType = ? ");
			parameters1.add(Integer.parseInt(value));
		} else if (type.equalsIgnoreCase("signifyType")) { // 如果是重要程度
			otherForMeSql.append(" and event.signifyType= ? ");
			parameters1.add(Integer.parseInt(value));
		} else if (type.equalsIgnoreCase("subject")) {
			// 如果是标题
			otherForMeSql.append(" and event.subject like ? ");
			parameters1.add("%" + value + "%");

		} else if (type.equalsIgnoreCase("beginDate")) // 如果是创建时间
		{
			Date todayF = Datetimes.getTodayFirstTime(value);
			Date todayE = Datetimes.getTodayLastTime(value);

			otherForMeSql.append(" and event.beginDate >=? and event.beginDate <= ?");
			parameters1.add(todayF);
			parameters1.add(todayE);
		} else if (type.equalsIgnoreCase("states")) { // 如果是状态
			if(Integer.parseInt(value) == 5){
				otherForMeSql.append(" and event.states != ? ");
				parameters1.add(4);
			}else{
				otherForMeSql.append(" and event.states = ? ");
				parameters1.add(Integer.parseInt(value));
			}
		} else if (type.equalsIgnoreCase("eventType")) { //如果是事件类型
			otherForMeSql.append(" and event.calEventType = ? ");
			parameters1.add(Integer.parseInt(value));
		}
		
		list2 = calEventDao.find(otherForMeSql.toString(), parameters1.toArray(new Object[parameters1.size()]));

		allList.addAll((List<CalEvent>) list2);
		if (allList == null || allList.size() == 0) {
			return null;
		}

		Collections.sort(allList, calEventComparator);
		return initList((List<CalEvent>) allList);
	}


	/**
	 *  -----模块主页------共享事件---主列表
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getOtherEventListByUserIdForMain(User user,
			PeopleRelateManager peopleRelateManager, String condition, String value) {
		// 下级事件列表
		List<CalEvent> juniorEventList = getJuniorEvent(user,peopleRelateManager,null,condition,value);
		// 上级事件列表
		List<CalEvent> superiorEventList = getSuperiorEvent(user,peopleRelateManager,null,condition,value);
		// 共享事件列表
		List<CalEvent> otherEventList = getOtherShareEvent(user, condition, value);
		// 部门事件列表
		List<CalEvent> deptEventList = new ArrayList<CalEvent>();
		try {
			List<Long> domainId = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
			deptEventList = getEventListByDeptIds(domainId,condition,value);
			
			/*for(Long depId : domainId){
				deptEventList.addAll(getEventListByDeptId(depId));
			}*/
		} catch (BusinessException e) {
			log.error("",e);
		}
		// 项目事件列表
		List<CalEvent> projectEventList = getItemEventListByUserId(user,condition, value);
		// 秘书助手的事件
		List<CalEvent> helperEventList = getHelperEvent(user,peopleRelateManager,null,condition,value);

		List<CalEvent> allEventList = new ArrayList<CalEvent>();
		List<CalEvent> allEventLists = new ArrayList<CalEvent>();
		if (juniorEventList != null)
			allEventList.addAll(juniorEventList);
		if (superiorEventList != null)
			allEventList.addAll(superiorEventList);
		if (otherEventList != null)
			allEventList.addAll(otherEventList);
		if (deptEventList != null)
			allEventList.addAll(deptEventList);
		if (projectEventList != null)
			allEventList.addAll(projectEventList);
		if (helperEventList != null)
			allEventList.addAll(helperEventList);
		if (allEventList.size() != 0) {
		
		Collections.sort(allEventList,calEventComparator);
		initList(allEventList);
		if (Strings.isNotBlank(condition) && condition.equals(Constants.RECEIVEMEMBERNAME)) {
			for (CalEvent cal: allEventList) {
				if (value.equals(cal.getCreateUserName()) || value.equals(cal.getReceiverMember())) {
					allEventLists.add(cal);
				}
			}
		} else {
			allEventLists = allEventList;
		}
		return allEventLists;
		} else
			return null;
	}

	/**
	 *  -----模块主页------共享事件---所有项目事件
	 * 
	 */
	public List<CalEvent> getItemEventListByUserId(User user,String condition, String value) {

		List<CalEvent> allList = new ArrayList<CalEvent>();// 总和事件list
		List<Object> parameters = new ArrayList<Object>();
		List<ProjectSummary> pp = null;
		StringBuilder sbSql = new StringBuilder();
		try {
			pp = this.projectManager.getAllProjectListByMemberId(user!=null?user.getId():0l);
		} catch (Exception e) {
		log.error("",e);
		}
		// 取得关联项目包括我的 所有的日程
		if (pp != null && pp.size() != 0) {
			sbSql.append(" from  CalEvent event2 where event2.id in( "
							+ " select distinct event.id from CalEvent event , CalEventTran tran  "
							+ " where  event.shareType='6'" + " and "
							+ " tran.eventId = event.id");
			for (int i = 0; i < pp.size(); i++) {
				if (i == 0)
					sbSql.append(" and ( ");
				parameters.add(pp.get(i).getId());
				sbSql.append(" tran.entityId= ? ");
				if (i != pp.size() - 1)
					sbSql.append(" or ");
				if (i == pp.size() - 1)
					sbSql.append(" ) ) ");
			}
			if(Strings.isNotBlank(condition) && Strings.isNotBlank(value) && !condition.equals(Constants.RECEIVEMEMBERNAME)){
				if(condition.equals(Constants.SUBJECT)){
					sbSql.append(" and event2." + condition + " like ? ");
					parameters.add("%" + SQLWildcardUtil.escape(value.trim()) + "%");
				}else if(condition.equals(Constants.BEGINDATE)){
					Date todayF = Datetimes.getTodayFirstTime(value);
					Date todayE = Datetimes.getTodayLastTime(value);
					sbSql.append(" and event2.beginDate >=? and event2.beginDate <=?");
					parameters.add(todayF);
					parameters.add(todayE);
				} else if (condition.equals(Constants.STATES)){
					if(Integer.parseInt(value) == 5){
						sbSql.append(" and event2." + condition + "!=?");
						parameters.add(4);
					}else{
						sbSql.append(" and event2." + condition + "=?");
						parameters.add(Integer.parseInt(value));
					}
				}else {
					//重要程度
					sbSql.append(" and event2." + condition + "=?");
					parameters.add(Integer.parseInt(value));
				}
			}
			sbSql.append("  order by event2.beginDate desc ");
			String sql = sbSql.toString();
			if (sql.length() == 0)
				return null;
			else {
				List list = calEventDao.getHibernateTemplate().find(sql,parameters.toArray());
				if (list == null || list.size() == 0)
					return null;
				// 所有和我有关的日程
				allList.addAll((List<CalEvent>) list);
			//排序，去重复分页

				if (allList == null || allList.size() == 0)
					return null;
				return initList((List<CalEvent>) allList);
			}
		} else {
			return null;
		}
	}

	/**
	 *  -----模块主页------共享事件--- 主列表+他人事件---下级共享给上级的事件
	 *  
	 */
	@SuppressWarnings("unchecked")
	private List<CalEvent> getJuniorEvent(User user,
			PeopleRelateManager peopleRelateManager,Long juiorId,String condition, String value) {
		CalendarUtils utils = new CalendarUtils();
		//List<Long> juniorIds = new ArrayList<Long>();
		List<Object> parameters = new ArrayList<Object>();
		List<V3xOrgMember> juniorList = utils.getJunior(user, peopleRelateManager);
		StringBuilder sbSql = new StringBuilder();
		//juniorIds.add(user.getId());
		parameters.add(user.getId());
		int  size = juniorList!=null?juniorList.size():0;
		
		if (size != 0) {
			sbSql.append(" from CalEvent event where event.shareType='3'"
					+ " and  " + " event.createUserId <> ? ");
			if(juiorId==null){
				if(size>0){
					sbSql.append(" and ( ");
					for (int i = 0; i < juniorList.size(); i++) {
						V3xOrgMember m = juniorList.get(i);
						if(m!=null){
							//juniorIds.add(m.getId());
							parameters.add(m.getId());
							// 创建者是我的下级
							sbSql.append(" event.createUserId= ? ");
							if (i != size - 1)
								sbSql.append(" or ");
							if (i == size - 1)
								sbSql.append(" )");
						}
					}
				}
			}else{
				sbSql.append(" and ( ");
				sbSql.append(" event.createUserId= ? ");
				sbSql.append(" )");
				//juniorIds.add(juiorId);
				parameters.add(juiorId);
			}
			if(Strings.isNotBlank(condition) && Strings.isNotBlank(value) && !condition.equals(Constants.RECEIVEMEMBERNAME)){
				if(condition.equals(Constants.SUBJECT)){
					sbSql.append(" and event." + condition + " like ? ");
					parameters.add("%" + SQLWildcardUtil.escape(value.trim()) + "%");
				}else if(condition.equals(Constants.BEGINDATE)){
					Date todayF = Datetimes.getTodayFirstTime(value);
				    Date todayE =  Datetimes.getTodayLastTime(value);
					sbSql.append(" and event.beginDate >=? and event.beginDate <=?");
					parameters.add(todayF);
					parameters.add(todayE);
				}else if(condition.equals(Constants.STATES)){
					if(Integer.parseInt(value) == 5){
						sbSql.append(" and event." + condition + "!=?");
						parameters.add(4);
					}else{
						sbSql.append(" and event." + condition + "=?");
						parameters.add(Integer.parseInt(value));
					}
				}else{
					//重要程度
					sbSql.append(" and event." + condition + "=?");
					parameters.add(Integer.parseInt(value));
				}
			}
			sbSql.append("  order by event.beginDate desc ");
			String sql = sbSql.toString();
			if (sql.length() == 0)
				return null;
			else {
				List list = calEventDao.getHibernateTemplate().find(sql,parameters.toArray());
				if (list == null || list.size() == 0)
					return null;
				return (List<CalEvent>) list;
			}
		} else {
			return null;
		}
	}

	
	/**
	 *  -----模块主页------共享事件--- 主列表+他人事件---把我作为下级的人员的事件
	 *  
	 */
	@SuppressWarnings("unchecked")
	private List<CalEvent> getSuperiorEvent(User user,
			PeopleRelateManager peopleRelateManager,Long superiorId,String condition, String value) {
		CalendarUtils utils = new CalendarUtils();
		List<Object> parameters = new ArrayList<Object>();
		parameters = utils.getHelper_(user);
		StringBuilder sbSql = new StringBuilder();
		if (parameters != null && parameters.size() != 0) {
			sbSql.append(" from CalEvent event where event.shareType='4' ");
			if(superiorId==null){
				for (int i = 0; i < parameters.size(); i++) {
					if (i == 0)
						sbSql.append(" and (");
					// 创建者是我的秘书
					sbSql.append("event.createUserId = ? ");
					if (i != parameters.size() - 1)
						sbSql.append(" or ");
					if (i == parameters.size() - 1)
						sbSql.append(" )");
				}
			}else{
				sbSql.append(" and (");
				sbSql.append("event.createUserId = ? ");
				sbSql.append(" )");
				parameters.clear();
				parameters.add(superiorId);
			}
			if(Strings.isNotBlank(condition) && Strings.isNotBlank(value) && !condition.equals(Constants.RECEIVEMEMBERNAME)){
				if(condition.equals(Constants.SUBJECT)){
					sbSql.append(" and event." + condition + " like ?");
					parameters.add("%" + SQLWildcardUtil.escape(value.trim()) + "%");
				}else if(condition.equals(Constants.BEGINDATE)){
					Date todayF = Datetimes.getTodayFirstTime(value);
					Date todayE = Datetimes.getTodayLastTime(value);
					sbSql.append(" and event.beginDate >=? and event.beginDate <=?");
					parameters.add(todayF);
					parameters.add(todayE);
				}else if(condition.equals(Constants.STATES)){
					if(Integer.parseInt(value) == 5){
						sbSql.append(" and event." + condition + "!=?");
						parameters.add(4);
					}else{
						sbSql.append(" and event." + condition + "=?");
						parameters.add(Integer.parseInt(value));
					}
				}else {
					//重要程度
					sbSql.append(" and event." + condition + "=?");
					parameters.add(Integer.parseInt(value));
				}
			}
			sbSql.append(" order by event.beginDate desc");

			String sql = sbSql.toString();
			if (sql.length() == 0)
				return null;
			else {
				List list = calEventDao.getHibernateTemplate().find(sql,parameters.toArray());
				if (list == null || list.size() == 0)
					return null;
				return (List<CalEvent>) list;
			}
		} else {
			return null;
		}
	}


	/**
	 *  -----模块主页------共享事件--- 主列表+他人事件---秘书的事件
	 *   
	 */
	@SuppressWarnings("unchecked")
	private List<CalEvent> getHelperEvent(User user,
			PeopleRelateManager peopleRelateManager,Long helperId,String condition, String value) {
		CalendarUtils utils = new CalendarUtils();
		List<Object> parameters = new ArrayList<Object>();
		parameters = utils.getHelper(user);
		StringBuilder sbSql = new StringBuilder();

		if (parameters != null && parameters.size() != 0) {
			sbSql.append(" from CalEvent event where event.shareType='7' ");
			if(helperId==null){
				for (int i = 0; i < parameters.size(); i++) {
					if (i == 0)
						sbSql.append(" and (");
					// 创建者是我的秘书
					sbSql.append("event.createUserId = ? ");
					if (i != parameters.size() - 1)
						sbSql.append(" or ");
					if (i == parameters.size() - 1)
						sbSql.append(" )");
				}
			}else{
				sbSql.append(" and (");
				sbSql.append("event.createUserId = ? ");
				sbSql.append(" )");
				parameters.clear();
				parameters.add(helperId);
			}
			if(Strings.isNotBlank(condition) && Strings.isNotBlank(value) && !condition.equals(Constants.RECEIVEMEMBERNAME)){
				if(condition.equals(Constants.SUBJECT)){
					sbSql.append(" and event." + condition + " like ?");
					parameters.add("%" + SQLWildcardUtil.escape(value.trim()) + "%");
				}else if(condition.equals(Constants.BEGINDATE)){
					Date todayF = Datetimes.getTodayFirstTime(value);
					Date todayE = Datetimes.getTodayLastTime(value);
					sbSql.append(" and event.beginDate >=? and event.beginDate <=?");
					parameters.add(todayF);
					parameters.add(todayE);
				}else if(condition.equals(Constants.STATES)){
					if(Integer.parseInt(value) == 5){
						sbSql.append(" and event." + condition + "!=?");
						parameters.add(4);
					}else{
						sbSql.append(" and event." + condition + "=?");
						parameters.add(Integer.parseInt(value));
					}
				}else {
					//重要程度
					sbSql.append(" and event." + condition + "=?");
					parameters.add(Integer.parseInt(value));
				}
			}
			sbSql.append(" order by event.beginDate desc");
			String sql = sbSql.toString();
			if (sql.length() == 0)
				return null;
			else {
				List list = calEventDao.getHibernateTemplate().find(sql,parameters.toArray());
				if (list == null || list.size() == 0)
					return null;
				return (List<CalEvent>) list;
			}
		} else {
			return null;
		}
	}


	/**
	 *  -----模块主页------共享事件--- 主列表+他人事件---他人共享的事件
	 *  
	 */
	@SuppressWarnings("unchecked")
	private List<CalEvent> getOtherShareEvent(User user, String condition, String value) {
		// 取得关联项目包括我的 所有的日程
		Map<String,Object> parameter = new HashMap<String,Object>();
		StringBuilder sql =new StringBuilder("select event from CalEvent event, CalEventTran tran ");
		sql.append(" where  (tran.entityId in(:domainId) )");
		//sql.append(" and tran.entityId <> tran.sourceRecordId "); // 事件的接收者是当前用户
		sql.append(" and tran.eventId = event.id ");// 委托安排人不是自己
		if(Strings.isNotBlank(condition) && Strings.isNotBlank(value) && !condition.equals(Constants.RECEIVEMEMBERNAME)){
			if(condition.equals(Constants.SUBJECT)){
				sql.append(" and event." + condition + " like :subject");
				parameter.put("subject", "%" + SQLWildcardUtil.escape(value.trim()) + "%");
			}else if(condition.equals(Constants.BEGINDATE)){
				Date todayF = Datetimes.getTodayFirstTime(value);
				Date todayE = Datetimes.getTodayLastTime(value);
				sql.append(" and event.beginDate >=:todayF and event.beginDate <=:todayE");
				parameter.put("todayF", todayF);
				parameter.put("todayE", todayE);
			}else if(condition.equals(Constants.STATES)){
				if(Integer.parseInt(value) == 5){
					sql.append(" and event." + condition + "!=:value");
					parameter.put("value", 4);
				}else{
					sql.append(" and event." + condition + "=:value");
					parameter.put("value", Integer.parseInt(value));
				}
			}else {
				//重要程度
				sql.append(" and event." + condition + "=:value");
				parameter.put("value", Integer.parseInt(value));
			}
		}
		sql.append(" and (event.shareType=2) order by event.beginDate desc");// 完成状态不为已完成
		List<Long> domainId = null;
		try {
			domainId = orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, V3xOrgEntity.ORGENT_TYPE_MEMBER,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		} catch (BusinessException e) {
			log.error("",e);
		}
		parameter.put("domainId", domainId);
		List list = calEventDao.find(sql.toString(), -1,-1,parameter);
		//List list = calEventDao.getHibernateTemplate().find(sql.toString(),new Object[]{domainId});
		if (list == null || list.size() == 0)
			return null;
		return (List<CalEvent>) list;
	}

	
	/**
	 *  -----模块主页------共享事件--- 他人事件---按左侧页面关联人员查询
	 *  
	 */
	public List<CalEvent> getEventListByOtherId(Long otherId,
			PeopleRelateManager p) {
		
		Long currentId = CurrentUser.get().getId();
        StringBuilder builder = new StringBuilder("select event from CalEvent event,CalEventTran tran where");
        builder.append("( tran.entityId =? and tran.sourceRecordId=? )");
        builder.append(" and tran.eventId = event.id");
        builder.append(" and event.shareType='2' order by event.beginDate desc");
        
        List list = calEventDao.getHibernateTemplate().find(
				builder.toString(),
				new Object[] { currentId, otherId});
        List<CalEvent> helperList = this.getHelperEvent(CurrentUser.get(),p,otherId,null,null);
        if(helperList!=null)
        list.addAll(helperList);
        List<CalEvent> superList = this.getSuperiorEvent(CurrentUser.get(),p,otherId,null,null);
        if(superList!=null)
        list.addAll(superList);
        List<CalEvent> juniorList = this.getJuniorEvent(CurrentUser.get(),p,otherId,null,null);
        if(juniorList!=null)
        list.addAll(juniorList);
        
        return initList((List<CalEvent>) list);
	}
	
	/**
	 * 事件取消提醒时，删除对应任务调度
	 * @param eventId
	 */
	public void cancelRemind(Long eventId) {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			sched.deleteJob(this.getJobStr(eventId), this.getGroupStr(eventId));
			log.debug("为事件[id=" + eventId + "]取消提醒任务调度");
		} catch (SchedulerException e) {
			log.error("删除日程事件提醒提前任务调度出现异常：", e);
		}
	}
	
	/**
	 * 获取事件对应任务调度的Job ID
	 * @param eventId
	 * @return
	 */
	private String getJobStr(Long eventId) {
		return "job_" + eventId;
	}
	
	/**
	 * 获取事件对应任务调度的Group ID
	 * @param eventId
	 * @return
	 */
	private String getGroupStr(Long eventId) {
		return "group_" + eventId;
	}

	
//  ... 日程提前提醒
	
	/**
	 *  -----模块主页---新建页面---功能---日程提前提醒 
	 * 
	 */
	public void eventRemind(CalEvent event) {
		this.eventRemind(event, false);
	}
	
	/**
	 *  -----模块主页---新建页面---功能---日程提前提醒 
	 * 
	 */
	public void eventRemind(CalEvent event, boolean isNew) {
		if(event.getAlarmDate() != null && event.getAlarmDate() != 0 &&event.getAlarmDate() > 0){
			// 提前提醒
			try {
				Scheduler sched = QuartzListener.getScheduler();
				Long eventId = event.getId();
				String jobName = this.getJobStr(eventId);
				String groupName = this.getGroupStr(eventId);
				
				//在修改事件提醒时间时，需先删除旧的任务调度
				if(!isNew)
					sched.deleteJob(jobName, groupName);
				
				Date runTime = event.getRemindTime();
				String triggerName = UUIDLong.longUUID() + "";
				SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, event.getRemindTime());
				JobDataMap datamap = new JobDataMap();
				datamap.putAsString("eventId", eventId.longValue());
	
				JobDetail job = new JobDetail(jobName, groupName, EventRemind.class);
				job.setJobDataMap(datamap);
				sched.scheduleJob(job, trigger);
				
				if(log.isDebugEnabled())
					log.debug("为事件[id=" + eventId + "]启动提醒任务调度，任务启动时间为：" + Datetimes.format(runTime, Datetimes.datetimeWithoutSecondStyle));
	
			} catch (SchedulerException e) {
				log.error("设置日程事件提醒时报错", e);
			} 
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CalEvent> getAllEventListByUserId(Long memberId, Date beginDate, Date endDate) throws Exception {
		User user = CurrentUser.get();
		Long userId = user.getId();
		List<CalEvent> list = new ArrayList<CalEvent>();
		if (memberId.equals(userId)) {
			StringBuilder hql = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" from CalEvent e where ((e.createUserId=:userId and e.states in (:states)) or (e.receiveMemberId like :receiveMemberId and e.eventflag in (:eventflag)))");
			hql.append(" and e.beginDate<=:beginDate and e.endDate>=:endDate");
			params.put("userId", memberId);
			params.put("states", new Integer[] { 1, 2, 3, 4 });
			params.put("receiveMemberId", "%" + memberId + "%");
			params.put("eventflag", new Integer[] { 0, 1, 2 });
			params.put("beginDate", endDate);
			params.put("endDate", beginDate);
			list = calEventDao.find(hql.toString(), -1, -1, params);
		} else {
			// 此人通过公开给上级、下级、助手秘书包含我的事件
			list = this.getRelateEventList(userId, memberId, beginDate, endDate);
			// 此人通过部门事件、公开给他人包含我的事件和我委托、安排给此人的事件
			List<CalEvent> listCalEventTran = this.getEventTranListByMemberId(memberId, user, beginDate, endDate);

			// 此人同意参加的并且我能看到的会议事件
			List<CalEvent> meetingEventList = this.getMtEventList(memberId, user, beginDate, endDate);

			// 此人通过项目事件共享包含我的事件
			List<CalEvent> projectList = this.getProjectEventList(userId, memberId, beginDate, endDate);

			if (list == null) {
				list = new ArrayList<CalEvent>();
			}

			if (CollectionUtils.isNotEmpty(listCalEventTran)) {
				list.addAll(listCalEventTran);
			}

			if (CollectionUtils.isNotEmpty(meetingEventList)) {
				list.addAll(meetingEventList);
			}

			if (CollectionUtils.isNotEmpty(projectList)) {
				list.addAll(projectList);
			}
		}

		List<CalEvent> result = new UniqueList<CalEvent>();
		for (CalEvent event : list) {
			result.add(event);
		}

		return initList(result);
	}

	/**
	 * 此人通过公开给上级、下级、助手秘书包含我的事件
	 */
	@SuppressWarnings("unchecked")
	private List<CalEvent> getRelateEventList(Long userId, Long memberId, Date beginDate, Date endDate) throws Exception {
		Map<RelationType, List<V3xOrgMember>> relateMap = peopleRelateManager.getAllRelateMembers(userId);
		List<V3xOrgMember> leaderlist = relateMap.get(RelationType.leader);
		List<V3xOrgMember> juniorlist = relateMap.get(RelationType.junior);
		List<V3xOrgMember> helperlist = relateMap.get(RelationType.assistant);
		int shareType = -1;

		if (CollectionUtils.isNotEmpty(leaderlist)) {// 如果我的上级列表包含他，那就取他对下级共享的事件
			for (V3xOrgMember member : leaderlist) {
				if (member.getId().equals(memberId)) {
					shareType = 4;
					break;
				}
			}
		}

		if (shareType == -1) {
			if (CollectionUtils.isNotEmpty(juniorlist)) {// 如果我的下级列表包含他，那就取他对上级共享的事件
				for (V3xOrgMember member : juniorlist) {
					if (member.getId().equals(memberId)) {
						shareType = 3;
						break;
					}
				}
			}
		}

		if (shareType == -1) {
			if (CollectionUtils.isNotEmpty(helperlist)) {// 如果我的助手秘书列表包含他，那就取他对助手秘书共享的事件
				for (V3xOrgMember member : helperlist) {
					if (member.getId().equals(memberId)) {
						shareType = 7;
						break;
					}
				}
			}
		}

		if (shareType != -1) {
			StringBuilder hql = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from " + CalEvent.class.getName() + " e where e.createUserId=:createUserId and e.shareType=:shareType and e.beginDate<=:beginDate and e.endDate>=:endDate");
			params.put("createUserId", memberId);
			params.put("shareType", shareType);
			params.put("beginDate", endDate);
			params.put("endDate", beginDate);
			return calEventDao.find(hql.toString(), -1, -1, params);
		}
		return null;
	}

	/**
	 * 此人通过部门事件、公开给他人包含我的事件和我委托、安排给此人的事件
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getEventTranListByMemberId(Long memberId, User user, Date beginDate, Date endDate) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select event from CalEvent event, CalEventTran tran where event.id=tran.eventId and event.beginDate<=:beginDate and event.endDate>=:endDate and ((tran.sourceRecordId=:memberId1 and tran.receiveId=:memberId2) or (tran.sourceRecordId=:memberId3 and tran.entityId in (:entityIds)))");
		params.put("beginDate", endDate);
		params.put("endDate", beginDate);
		params.put("memberId1", user.getId());
		params.put("memberId2", memberId);
		params.put("memberId3", memberId);
		List<Long> entityIds = new ArrayList<Long>();
		entityIds.add(user.getDepartmentId());
		entityIds.add(user.getAccountId());
		entityIds.add(user.getId());
		params.put("entityIds", entityIds);
		return calEventDao.find(hql.toString(), -1, -1, params);
	}

	/**
	 * 此人同意参加的并且我能看到的会议事件
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getMtEventList(Long memberId, User user, Date beginDate, Date endDate) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" select e from CalEvent e, MtMeeting mt, MtConferee mc where e.fromId=mt.id and mt.id=mc.meetingId ");
		hql.append(" and e.createUserId=:createUserId and e.fromType=6 and e.beginDate<=:beginDate and e.endDate>=:endDate ");
		hql.append(" and (mt.emceeId=:userId or mt.recorderId=:userId or mc.confereeId in (:domainIds))");

		params.put("createUserId", memberId);
		params.put("beginDate", endDate);
		params.put("endDate", beginDate);
		params.put("userId", user.getId());
		params.put("domainIds", CommonTools.getUserDomainIds(orgManager));
		return calEventDao.find(hql.toString(), -1, -1, params);
	}

	/**
	 * 此人通过项目事件共享包含我的事件
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getProjectEventList(Long userId, Long memberId, Date beginDate, Date endDate) {
		List<Long> projectIds = null;
		try {
			List<ProjectSummary> projects = this.projectManager.getAllProjectListByMemberId(userId);
			projectIds = CommonTools.getIds(projects);
		} catch (Exception e) {
			log.error("", e);
		}

		if (CollectionUtils.isNotEmpty(projectIds)) {
			StringBuilder hql = new StringBuilder();
			hql.append("select event from CalEvent event, CalEventTran tran where event.id=tran.eventId ");
			hql.append("and event.createUserId=:createUserId and event.shareType=6 and tran.entityId in (:entityIds) ");
			hql.append("and event.beginDate<=:beginDate and event.endDate>=:endDate");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("createUserId", memberId);
			params.put("entityIds", projectIds);
			params.put("beginDate", endDate);
			params.put("endDate", beginDate);
			return calEventDao.find(hql.toString(), -1, -1, params);
		} else {
			return null;
		}
	}

	/**
	 *  -----日程视图------日程视图---日，周，月 视图 ---获取除了自己的其他所有人公开给我的事件
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<CalEvent> getOpenEventListByUserId(Long userId) {

		DetachedCriteria dc = DetachedCriteria.forClass(CalEvent.class);
		Criteria c = null;
		User user = CurrentUser.get();
		if (userId.equals(user.getId())) {
			Criterion notCurrentUserC = Restrictions.ne("createUserId", userId);
			Criterion privateC = Restrictions.eq("eventType", 1);
			Criterion otherStateC = Restrictions.gt("states", 1);
			Criterion pc = Restrictions.and(Restrictions.and(notCurrentUserC,privateC), otherStateC);

			// 上级事件
			List<V3xOrgMember> llist = this.getCalendarUtils().getSuperior(CurrentUser.get());
			Criterion c1 = null;
			if (llist != null && llist.size() > 0) {
				Long[] leaders = new Long[llist.size()];
				int i = 0;
				for (V3xOrgMember member : llist) {
					leaders[i++] = member.getId();
				}
				c1 = Restrictions.and(Restrictions.in("createUserId", leaders),Restrictions.eq("shareType", 4));
			}

			// 下级事件
			List<V3xOrgMember> llist1 = this.getCalendarUtils().getJunior(CurrentUser.get());
			Criterion c2 = null;
			if (llist1 != null && llist1.size() > 0) {
				Long[] leaders1 = new Long[llist1.size()];
				int j = 0;
				for (V3xOrgMember member : llist1) {
					leaders1[j++] = member.getId();
				}
				c2 = Restrictions.and(Restrictions.in("createUserId", leaders1), Restrictions.eq("shareType", 3));
			}
			
			if(c1==null && c2==null){
				dc.add(pc);
			}else if(c1==null && c2!=null){
				dc.add(c2);
			}else if(c1!=null && c2==null){
				dc.add(c1);
			}else{
				Criterion oc = Restrictions.or(c1, c2);
				dc.add(Restrictions.or(oc, pc));
			}
			
			c = dc.getExecutableCriteria(calEventDao.getHibernateTemplate().getSessionFactory().getCurrentSession());
			dc.addOrder(Order.asc("beginDate"));
			return initList(c.list());

		} else {
			return null;
		}
	}
	
	/**
	 * 附件
	 * 
	 */
	public void init() {
		// 附件标记升级数据
		String hql2 = "from CalEvent where attachmentsFlag is null";
		int total2 = this.calEventDao.getQueryCount(hql2, null, null);
		if (total2 == 0) {
			//log.debug("日程数据表不用初始化附件标记，没有 null 数据。");
		} else {
			List<CalEvent> allData = calEventDao.getAll();
			if (allData != null && !allData.isEmpty()) {
				//log.debug("日程数据表不用初始化附件标记，没有公告数据。");
				for (CalEvent data : allData) {
					data.setAttachmentsFlag(attachmentManager.hasAttachments(data.getId(), data.getId()));
					calEventDao.update(data);
				}
				log.info("日程数据表初始化附件标记完成，共 " + allData.size() + " 条。");
			}
		}
	}
	
	//------------------ 对外接口部分
	
	private String getProjectIds(Long userId){
		List<ProjectSummary> list = null;
		StringBuilder s = new StringBuilder(" ( ");
		try {
			list = projectManager.getAllProjectList(userId);
			
		} catch (Exception e) {
			log.error("得到关联项目报错", e);
		}
		if(list!=null && list.size()!=0){
			for(int i=0;i<list.size();i++){
				Long l = list.get(i).getId();
				s.append(l);
				if(i!=list.size()-1){
					s.append(" ,");
				}else{
					s.append(" ) ");
				}
			}
			return s.toString();
		}else{
			return  null;
		}
	}
	
	/**
	 *  综合查询
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<CalEvent> iSearch(ConditionModel cModel) {
		String title = cModel.getTitle();
		final Date beginDate = cModel.getBeginDate();
		final Date endDate = cModel.getEndDate();
		Long fromUserId = cModel.getFromUserId();
		StringBuilder sb = new StringBuilder();
		StringBuilder sb0 = new StringBuilder();
		Long userId = CurrentUser.get().getId();
		
		String projectIds = getProjectIds(userId);//得到关联项目的 id
		
		if (fromUserId != null && fromUserId.equals(userId)) {
			sb0.append("select count(*)");
			sb = new StringBuilder("from CalEvent event where 1=1");
			sb.append(" and event.createUserId = :userId");
			if (beginDate != null) {
				sb.append(" and event.beginDate >= :begin");
			}
			if (endDate != null) {
				sb.append(" and event.endDate <= :end");
			}
			if (Strings.isNotBlank(title)) {
				sb.append(" and event.subject like '%").append(title).append("%'");
			}
			sb0.append(sb.toString());
			sb.append(" order by event.beginDate desc");
		} else {
			sb0.append("select count(event2.id)");
			sb = new StringBuilder(
					" from CalEvent event2 where event2.id in( select event.id from CalEvent event, CalEventTran tran  where  ");
			if (fromUserId != null && !fromUserId.equals(userId)) {
				sb.append(" event.createUserId = :fromUserId");
				sb.append(" and ( tran.receiveId = :userId");
				sb.append(" or tran.entityId in ( ").append(userId);
				sb.append(",").append(CurrentUser.get().getDepartmentId());
				sb.append(",").append(CurrentUser.get().getAccountId()).append(" ) )");
			}else{
				if(fromUserId==null){
					sb.append("(  tran.receiveId = :userId");
					sb.append(" or tran.entityId in ( ").append(userId);
					sb.append(",").append(CurrentUser.get().getDepartmentId());
					sb.append(",").append(CurrentUser.get().getAccountId()).append(" ) ");
					if(projectIds!=null){
						
						sb.append(" or tran.entityId in ").append(projectIds).append(" ) ");
					}else{
						sb.append(" ) ");
					}
				}
			}
			sb.append(" and event.id = tran.eventId ) ");
			sb.append(" and event2.shareType != ").append(ShareType.personal.key());
			if (beginDate != null) {
				sb.append(" and event2.beginDate >= :begin");
			}
			if (endDate != null) {
				sb.append(" and event2.endDate <= :end");
			}
			if (Strings.isNotBlank(title)) {
				sb.append(" and event2.subject like '%").append(title).append("%'");
			}
			sb0.append(sb.toString());
			sb.append(" order by event2.beginDate desc");
		}
		final String hsql = sb.toString();
		final String hsql0 = sb0.toString();
		return initList(getCalEventList(hsql0,hsql,beginDate,endDate,userId,(fromUserId != null && fromUserId.equals(userId))?null:(fromUserId != null && !fromUserId.equals(userId))?fromUserId:null));

	}

	@SuppressWarnings("unchecked")
	private List<CalEvent>  getCalEventList(final String hsql0,final String hsql,final Date beginDate,final Date endDate,final Long userId,final Long fromuserId){
		List<CalEvent> list = (List<CalEvent>) calEventDao.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(hsql0);
				if (beginDate != null)
					query.setTimestamp("begin", beginDate);
				if (endDate != null)
					query.setTimestamp("end", endDate);
				if(userId!=null)
					query.setLong("userId", userId);
				if(fromuserId!=null)
					query.setLong("fromUserId", fromuserId);
				List list2 = query.list();
				Pagination.setRowCount((Integer) (list2.get(0)));

				query = session.createQuery(hsql);
				if (beginDate != null)
					query.setTimestamp("begin", beginDate);
				if (endDate != null)
					query.setTimestamp("end", endDate);
				if(userId!=null)
					query.setLong("userId", userId);
				if(fromuserId!=null)
					query.setLong("fromUserId", fromuserId);
				List<CalEvent> ret = (List<CalEvent>) query
						.setFirstResult(Pagination.getFirstResult())
						.setMaxResults(Pagination.getMaxResults())
						.list();
				return ret;
			}
		});
		return list;
	}

	/**
	 *  全文检索
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public IndexInfo getIndexInfo(long id) {
		// 按id得到对象
		CalEvent event = getEventById(id);
		List<CalContent> eventC = this.calContentManager.getContentByEventId(id);
		List<CalEventTran> eventTrans = this.calEventTranManager.getEventTranListByEventId(id);
		List<CalReply> replys = calReplyManager.getReplyListByEventId(id);

		if (event == null) return null;
		
		IndexInfo indexInfo = new IndexInfo();
		// 日程的题目
		indexInfo.setTitle(event.getSubject());
		// 发起人ID
		indexInfo.setStartMemberId(event.getCreateUserId());
		//是否包含附件
		indexInfo.setHasAttachment(event.getAttachmentsFlag());
		//项目ID
//		Long projectId = event.getProjectId();
//		if(projectId != null){
//			indexInfo.setProjectId(projectId.toString());
//		}
		StringBuilder comment = new StringBuilder();
		if(CollectionUtils.isNotEmpty(replys)){
			for(CalReply reply : replys){
				comment.append(Functions.showMemberName(reply.getReplyUserId()));
				comment.append(reply.getReplyInfo());
			}
		}
		indexInfo.setComment(comment.toString());
		indexInfo.setStartTime(event.getBeginDate());
		indexInfo.setEndTime(event.getEndDate());
		indexInfo.setPriority(ResourceBundleUtil.getString(CalendarResources, "cal.event.priorityType." + event.getPriorityType()));
		indexInfo.setState(ResourceBundleUtil.getString(CalendarResources, "cal.event.states." + event.getStates()));
		// 日程的接受者

		List<String> ownerList = new ArrayList<String>();
		List<String> departmentList = new ArrayList<String>();
		List<String> projecttList = new ArrayList<String>();
		
		for (CalEventTran eventTran : eventTrans) {
			Long moduleId = eventTran.getReceiveId();
			String Id = String.valueOf(moduleId);
			if(!ownerList.contains(Id)){
				ownerList.add(Id);
			}
		}
		String reciverId = event.getReceiveMemberId();
		if(reciverId!=null){//添加委托人
			String[] memberIds = reciverId.split(",");
			for(String str : memberIds){
				String[] memberId = str.split("[|]");
				if(memberId[0].equals("Member")){
					if(!ownerList.contains(memberId[1])){
						ownerList.add(memberId[1]);
					}
				}
			}
		}
		String tranString = event.getTranMemberIds();
		String[] tarIds = tranString!=null?tranString.split(","):new String[]{};
		switch(ShareType.valueOf(event.getShareType())){
			case publicity://公开事件
				for(String str : tarIds){
					String[] string = str.split("[|]");
					if(string[0].equals("Member")){
						if(!ownerList.contains(string[1])){
							ownerList.add(string[1]);
						}
					}
				}
				break;
			case superior://共享给上级
				try {
					List<PeopleRelate> list = peopleRelateManager.getPeopleRelateList(event.getCreateUserId(), RelationType.leader.key());
					for(PeopleRelate leader : list){
						String idString = leader.getRelateMemberId().toString();
						if(!ownerList.contains(idString)){
							ownerList.add(idString);
						}
					}
				} catch (Exception e) {
					log.error("", e);
				}
				break;
			case junior://共享给下级
				try {
					List<PeopleRelate> list = peopleRelateManager.getPeopleRelateList(event.getCreateUserId(), RelationType.junior.key());
					for(PeopleRelate leader : list){
						String idString = leader.getRelateMemberId().toString();
						if(!ownerList.contains(idString)){
							ownerList.add(idString);
						}
						
					}
				} catch (Exception e) {
					log.error("", e);
				}
				break;
		 	case department://部门
		 		for(String str : tarIds){
		 			String[] string = str.split("[|]");
		 			if(string[0].equals("Department")){
		 				if(!departmentList.contains(string[1])){
		 					departmentList.add(string[1]);
						}
		 			}
		 		}
		 		break;
		 	case project://关联项目
		 		projecttList.add(event.getTranMemberIds());
		 		break;
		 	case assistant://秘书和助手
		 		try {
					List<PeopleRelate> list = peopleRelateManager.getPeopleRelateList(event.getCreateUserId(), RelationType.assistant.key());
					for(PeopleRelate leader : list){
						String idString = leader.getRelateMemberId().toString();
						if(!ownerList.contains(idString)){
							ownerList.add(idString);
						}
						
					}
				} catch (Exception e) {
					log.error("",e);
				}
		 		break;
		 		
		 	
		}
		AuthorizationInfo authorizationInfo = new AuthorizationInfo();

		ownerList.add(String.valueOf(event.getCreateUserId()));
		if (ownerList.size() > 0)
			authorizationInfo.setOwner(ownerList);
			authorizationInfo.setDepartment(departmentList);
			authorizationInfo.setProject(projecttList);
		indexInfo.setAuthorizationInfo(authorizationInfo);

		// 日程的内容
		StringBuilder content = new StringBuilder();
		for(CalContent contObj : eventC){
			content.append(contObj.getContent()!=null?contObj.getContent():"");
		}
		ProjectSummary project = null;
		try {
			project = projectManager.getProject(event.getProjectId());
		} catch (Exception e) {}
		if(project != null){
			content.append(project.getProjectName());
		}
		indexInfo.setContent(content.toString());

		// 内容的类型
		indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);

		// 创建者
		indexInfo.setAuthor(event.getCreateUserName());

		// 创建时间
		indexInfo.setCreateDate(event.getCreateDate());
		//创建类型
		indexInfo.setAppType(ApplicationCategoryEnum.calendar);
		//创建id
		indexInfo.setEntityID(event.getId());
		
		//日程附件
		IndexUtil.convertToAccessory(indexInfo);
		

		return indexInfo;

	}


	/**
	 *  对关联项目的接口 --------取得所有项目事件
	 * 
	 */

	@SuppressWarnings("unchecked")
	public List<CalEvent> getItemEventListByUserId(User user, Long projectId, Long phaseId) {
		List<CalEvent> resultList = new ArrayList<CalEvent>();
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder otherForMeSql =  new StringBuilder("select event from CalEvent event where event.shareType=:shareType ");
		params.put("shareType", ShareType.project.key());
		if(phaseId != null && phaseId != 1){
			otherForMeSql.append("and event.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.calendar.key() + ") ");
			params.put("phaseId", phaseId);
		}
		otherForMeSql.append("order by event.beginDate desc");
		List<CalEvent> allList = calEventDao.find(otherForMeSql.toString(), -1, -1, params);
		
		for(CalEvent e : allList){
			if(e != null && String.valueOf(projectId).equals(e.getTranMemberIds())){
				resultList.add(e);
			}else{
				continue;
			}
		}
		
		if (resultList == null || resultList.size() == 0)
			return null;
		
		return initList((List<CalEvent>) resultList);
	}
	
	/**
	 * 对关联项目的接口 --------取得所有项目事件 (条件查询)
	 *
	 * @param model
	 * @return
	 */
	public List<CalEvent> getItemEventListByCondition(String condition,User user, Long projectId, Long phaseId,Map<String,Object> paramMap) {
		List<CalEvent> resultList = new ArrayList<CalEvent>();
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder otherForMeSql =  new StringBuilder("select event from CalEvent event where event.shareType=:shareType ");
		params.put("shareType", ShareType.project.key());
		if(phaseId != null && phaseId != 1){
			otherForMeSql.append("and event.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.calendar.key() + ") ");
			params.put("phaseId", phaseId);
		}
		
		if ("title".equals(condition)) {
			otherForMeSql.append("and event.subject like :title ") ;
			params.put("title", "%" + paramMap.get("title") + "%") ;
		} else if ("author".equals(condition)) {
			otherForMeSql.append("and event.createUserId in (:author) ") ;
			params.put("author", paramMap.get("author")) ;
		} else if ("newDate".equals(condition)) {
			otherForMeSql.append("and event.createDate>=:begin and event.createDate<=:end ") ;
			params.put("begin", Datetimes.getTodayFirstTime(paramMap.get("newDate").toString())) ;
			params.put("end", Datetimes.getTodayLastTime(paramMap.get("newDate").toString())) ;
		}
		
		otherForMeSql.append("order by event.beginDate desc");
		List<CalEvent> allList = calEventDao.find(otherForMeSql.toString(), -1, -1, params);
		
		for(CalEvent e : allList){
			if(e != null && e.getTranMemberIds().equals(String.valueOf(projectId))){
				resultList.add(e);
			}else{
				continue;
			}
		}
		
		if (resultList == null || resultList.size() == 0)
			return null;
		
		return initList((List<CalEvent>) resultList);
	}
	
	public void deleteCalEventFromOtherAppId(Long appId, Integer type,Long createUserId) {
		List <CalEvent> list = calEventDao.findCalEventsByAppId(appId, type, createUserId);
		for (CalEvent event : list) {
			calContentManager.deleteByEventId(event.getId());
			calReplyManager.deleteByEventId(event.getId());
			calEventTranManager.deleteByEventId(event.getId());
			try {
				this.indexManager.deleteFromIndex(ApplicationCategoryEnum.calendar, event.getId());
			} catch (IOException e) {
				log.error("删除[id=" + event.getId() + "]全文检索数据时出现异常：", e);
			}
			
			if(event.isAlarmFlag()) {
				this.deleteBeforeStartRemindJob(event.getId());
			}
			
			if(event.getBeforendAlarm() != null && event.getBeforendAlarm() > 0) {
				this.deleteBeforeEndRemindJob(event.getId());
			}
		}
		calEventDao.deleteCalEventFromOtherAppId(appId, type,createUserId);
	}

	private void deleteBeforeEndRemindJob(Long eventId) {
		Scheduler sched = null;
		try {
			sched = QuartzListener.getScheduler();
			String jobName = eventId + "beforEnd_job";
			String groupName = eventId + "beforEnd_group";
			sched.deleteJob(jobName, groupName);
		} catch (SchedulerException e) {
			log.error("删除[id=" + eventId + "]事件结束前提醒任务调度过程中出现异常：", e);
		}
	}

	private void deleteBeforeStartRemindJob(Long eventId) {
		Scheduler sched = null;
		try {
			sched = QuartzListener.getScheduler();
			String jobName = this.getJobStr(eventId);
			String groupName = this.getGroupStr(eventId);
			sched.deleteJob(jobName, groupName);
		} catch (SchedulerException e) {
			log.error("删除[id=" + eventId + "]事件开始前提醒任务调度过程中出现异常：", e);
		}
	}

	public Long saveOrUpdateCalEventFromOtherApp(CalEvent calEvent, CalContent calContent,Long createUserId) {
		//修改为一个会议能转多个事件
		Long calEventId = save(calEvent);
		calContent.setEventId(calEventId);
		calContent.setEvent(calEvent);
		calContentManager.save(calContent);
		return calEventId;
	}

	public CalEvent isHasCalEventByAppId(Long appId, Integer type,Long createUserId) {
		List<CalEvent> calList = calEventDao.findCalEventsByAppId(appId, type,createUserId);
		//同一会议转事件，可以转多个事件
		if(calList==null || calList.size()==0){
			return null;
		}else{
			return calList.get(0);
		}
	}
	
	public List<CalEvent> getAllCalEventByAppId(Long appId, Integer type) {
		return calEventDao.findCalEventsByAppId(appId, type,null);
	}

	/**
	 * 根据ID集合获取事件列表
	 * @param eventIds
	 */
	public List<CalEvent> getEventByIds(List<Long> eventIds) {
		return this.calEventDao.getEventByIds(eventIds, false);
	}

	public void beforEndRemind(CalEvent calEvent) {
		if(calEvent.getBeforendAlarm() != null && calEvent.getBeforendAlarm() > 0){
			try {
				Long eventId = calEvent.getId();
				Scheduler sched = QuartzListener.getScheduler();
				String jobName = eventId+"beforEnd_job";
				String groupName = eventId+"beforEnd_group";
				
				sched.deleteJob(jobName, groupName);
				
				String triggerName = UUIDLong.longUUID() + "";
				SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, calEvent.getBeforEndRemindTime());
				JobDataMap datamap = new JobDataMap();
				datamap.putAsString("eventId", eventId.longValue());

				JobDetail job = new JobDetail(jobName, groupName, EventRemind.class);
				job.setJobDataMap(datamap);
				sched.scheduleJob(job, trigger);
				
			} catch (SchedulerException e) {
				log.error("设置日程事件结束前提醒时报错", e);
			}
		}
	}
	public void deletePeriodicalByIds(List<Long> ids) {
		for(Long id : ids){
			CalEventPeriodicalInfo info = getPeriodicalInfo(id);
			if(info != null){
				//删除calEvent
				PeriodicalCalEvent event = info.getCalEvent();
				this.calContentManager.deleteByEventId(event.getId());
				this.calEventTranManager.deleteByEventId(event.getId());

				calEventDao.delete(PeriodicalCalEvent.class, event.getId());
				
				//删除周期性事件
				calEventDao.delete(CalEventPeriodicalInfo.class, info.getId());
				try {
					this.attachmentManager.deleteByReference(event.getId(), event.getId());
				} catch (BusinessException e) {
					log.warn("",e);
				}
				//将 删除操作 写入日志。
				appLogManager.insertLog(CurrentUser.get(), AppLogAction.Calendar_Delete, CurrentUser.get().getName(),event.getSubject());
			}
		}
	}
	public List<CalEventPeriodicalInfo> findPeriodical4User(Long userId, String condtion, String textfield1) {
		List<Object> parameters = new ArrayList<Object>();
		// 根据过来的条件，取得数据
		StringBuilder selfCreateSql = new StringBuilder("select event,info from PeriodicalCalEvent event,CalEventPeriodicalInfo info where event.id=info.calEventId and event.createUserId=? ");
		parameters.add(userId);
		if(Strings.isNotBlank(condtion) && Strings.isNotBlank(textfield1)){
			if (condtion.equalsIgnoreCase("workType")) {
				selfCreateSql.append(" and event.workType = ? ");
				parameters.add(Integer.parseInt(textfield1));
			} // 如果是工作类型
			else if (condtion.equalsIgnoreCase("signifyType")) { // 如果是重要程度
				selfCreateSql.append(" and event.signifyType= ? ");
				parameters.add(Integer.parseInt(textfield1));
			} else if (condtion.equalsIgnoreCase("subject")) { // 如果是标题
				selfCreateSql.append(" and event.subject like ? ");
				parameters.add("%" + textfield1 + "%");
			}
			// 如果是开始时间
			else if (condtion.equalsIgnoreCase("beginDate")) {
				Date todayF = Datetimes.getTodayFirstTime(textfield1);
				Date todayE = Datetimes.getTodayLastTime(textfield1);
				
				selfCreateSql.append(" and event.beginDate >=? and event.beginDate <= ?");
				parameters.add(todayF);
				parameters.add(todayE);
			} else if (condtion.equalsIgnoreCase("states")) { // 如果是状态
				selfCreateSql.append(" and event.states = ? ");
				parameters.add(Integer.parseInt(textfield1));
			} else if (condtion.equalsIgnoreCase("eventType")) { //如果是事件类型
				selfCreateSql.append(" and event.calEventType = ? ");
				parameters.add(Integer.parseInt(textfield1));
			}
			
		}
		selfCreateSql.append(" order by event.createDate desc ");
		List<Object[]> list = calEventDao.find(selfCreateSql.toString(),null,parameters);
		List<CalEventPeriodicalInfo> result = new ArrayList<CalEventPeriodicalInfo>();
		for(Object[] os : list){
			PeriodicalCalEvent c = (PeriodicalCalEvent)os[0];
			CalEventPeriodicalInfo in = (CalEventPeriodicalInfo) os[1];
			if(in != null && c != null){
				in.setCalEvent((PeriodicalCalEvent)initEvent(c));
				result.add(in);
			}
		}
		return result;
	}

	public List<CalEvent> preCreateEvent(Long userId,Date startDate,Date endDate,int view){
		return preCreateEvent(userId, startDate, endDate, view,null,null);
	}
	
	public List<CalEvent> preCreateEvent(Long userId,Date startDate,Date endDate,int view,String type,String value){
		Date begin = Datetimes.getTodayFirstTime(startDate);
		Date overDate = Datetimes.getTodayFirstTime(endDate);
		Date today = Datetimes.getTodayFirstTime();
		
		List<CalEvent> result = new ArrayList<CalEvent>();
		if(Datetimes.minusDay(today, begin) >=0 && Datetimes.minusDay(overDate, today) >= 0){
			begin = Datetimes.addDate(today, 1);
		}else if(Datetimes.minusDay(overDate, today) <= 0){
			return result;
		}
		//获得开始时间在startDate前的事件
		StringBuffer hql = new StringBuffer("from PeriodicalCalEvent event,CalEventPeriodicalInfo info where event.id=info.calEventId and info.memberId=? and event.beginDate <=? ") ;
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(userId);
		parameters.add(endDate);
		if(Strings.isNotBlank(type) && Strings.isNotBlank(value)){
			if (type.equalsIgnoreCase("workType")) {
				hql.append(" and event.workType = ? ");
				parameters.add(Integer.parseInt(value));
			} // 如果是工作类型
			else if (type.equalsIgnoreCase("signifyType")) { // 如果是重要程度
				hql.append(" and event.signifyType= ? ");
				parameters.add(Integer.parseInt(value));
			} else if (type.equalsIgnoreCase("subject")) { // 如果是标题
				hql.append(" and event.subject like ? ");
				parameters.add("%" + value + "%");
			}
			// 如果是开始时间
			else if (type.equalsIgnoreCase("beginDate")) {
				Date todayF = Datetimes.getTodayFirstTime(value);
				Date todayE = Datetimes.getTodayLastTime(value);
				
				hql.append(" and event.beginDate >=? and event.beginDate <= ?");
				parameters.add(todayF);
				parameters.add(todayE);
			} else if (type.equalsIgnoreCase("states")) { // 如果是状态
				if(Integer.parseInt(value) == 5){
					hql.append(" and event.states != ? ");
					parameters.add(4);
				}else{
					hql.append(" and event.states = ? ");
					parameters.add(Integer.parseInt(value));
				}
			} else if (type.equalsIgnoreCase("eventType")) { //如果是事件类型
				hql.append(" and event.calEventType = ? ");
				parameters.add(Integer.parseInt(value));
			}
		}
		
		Map<Long,List<String>> hasCreateMap = getPeriodicalRelation(userId, endDate);
		
		List<Object[]> perioEvents = this.calEventDao.find(hql.toString(),-1,-1, null,parameters);
		for(Object[] rs : perioEvents){
			PeriodicalCalEvent event = (PeriodicalCalEvent)rs[0];
			CalEventPeriodicalInfo info = (CalEventPeriodicalInfo) rs[1];
			event.setPeriodicalId(info.getId());
			List<CalEvent> perCreate = perCreateEvemt(begin, overDate, event, info);
			if(perCreate != null && !perCreate.isEmpty()){
				for(CalEvent tempEvent : perCreate){
					if(!checkHasCreated(hasCreateMap,tempEvent,event)){
						result.add(tempEvent);
					}
				}
			}
		}
		return result;
	}
	private boolean checkHasCreated(Map<Long,List<String>> relation,CalEvent event,PeriodicalCalEvent info){
		if(relation != null){
			List<String> hasExistsList = relation.get(info.getPeriodicalId());
			if(hasExistsList != null){
				Date shouldCreatedDate = PeriodicalEventUtil.getPeridicaiCreateDate(info,event.getBeginDate());
				return hasExistsList.contains(Datetimes.formatDate(shouldCreatedDate));
			}
		}
		return false;
	}
	//得到周期性事件中，那天的事件已经建立好了。
	public Map<Long,List<String>> getPeriodicalRelation(Long userId,Date endDate){
		//加载已经创建的关系列表，剔除重复项 重复事件id 创建时间列表
		Map<Long,List<String>> relateMap = new HashMap<Long,List<String>>();
		String repleat = "from CalEventPeriodicalRelation relation where relation.memberId=? and relation.createDate <=? ";
		List<CalEventPeriodicalRelation> relation = this.calEventDao.find(repleat,-1,-1 ,null, userId,endDate);
		if(relation != null && !relation.isEmpty()){
			for(CalEventPeriodicalRelation relate : relation){
				List<String> relateDates = relateMap.get(relate.getCalEventPeriodicalInfoId());
				if(relateDates == null){
					relateDates = new ArrayList<String>();
				}
				CalEvent ce = this.getEventById(relate.getCalEventId());
				if(ce==null){
				    relateDates.add(Datetimes.formatDate(relate.getCreateDate()));
				}else{
				    relateDates.add(Datetimes.formatDate(ce.getBeginDate()));
				}
				relateMap.put(relate.getCalEventPeriodicalInfoId(), relateDates);
			}
		}
		return relateMap;
	}

	private List<CalEvent> perCreateEvemt(Date begin, Date overDate, PeriodicalCalEvent event, CalEventPeriodicalInfo info) {
		PeriodicalType type = EnumUtil.getEnumByOrdinal(PeriodicalType.class,info.getPeriodicalType());
		if(type != null){
			switch(type){
			case EveryDay:
				return everyDay(event, begin, overDate, info);
			case EveryWeek:
				return everyWeek(event, begin, overDate, info);
			case EveryMonthDay:
				return everyMonthDay(event, begin, overDate, info);
			case EveryMonthWeekDay:
				return everyMonthWeekDay(event, begin, overDate, info);
			case EveryYearMonthDay:
				return everyYearMonthDay(event, begin, overDate, info);
			case EveryYearMonthWeekDay:
				return everyYearMonthWeekDay(event, begin, overDate, info);
			}
		}
		return null;
	}
	
	private CalEvent perCreateEvent(PeriodicalCalEvent event,Date startDate){
		CalEvent result = new CalEvent(event);
		result.setPeriodicalId(event.getPeriodicalId());
		setDate4RepeatedEvent(result, startDate);
		return result;
	}
	/**
	 * 每几天
	 * @param event
	 * @param startDate
	 * @param endDate
	 * @param info
	 * @return
	 */
	private List<CalEvent> everyDay(PeriodicalCalEvent event,Date startDate,Date endDate,CalEventPeriodicalInfo info){
		List<CalEvent> result = new ArrayList<CalEvent>();
		startDate = info.getBeginTime().after(startDate) ? info.getBeginTime() : startDate;
		endDate = info.getEndTime() != null && info.getEndTime().before(endDate) ? info.getEndTime() : endDate;
		while(Datetimes.minusDay(endDate, startDate) >=0){
			CalEvent cal = perCreateEvent(event,startDate);
			startDate = Datetimes.addDate(startDate, info.getDayDate());
			result.add(cal);
		}
		return result;
	}
	/**
	 * 每周几，存在多选，如周一，周三，周五
	 * @param event
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<CalEvent> everyWeek(PeriodicalCalEvent event,Date startDate,Date endDate,CalEventPeriodicalInfo info){
		//开始为星期几?
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(event.getBeginDate());
		endDate = info.getEndTime() != null && info.getEndTime().before(endDate) ? info.getEndTime() : endDate;
		List<CalEvent> result = new ArrayList<CalEvent>();
		String[] week = info.getWeeks().split(",");
		for(int i=0;i<week.length;i++){
			Calendar currentCalendar = new GregorianCalendar();
			currentCalendar.setTime(startDate);
			
			//int betweenDay = calendar.get(Calendar.DAY_OF_WEEK)-currentCalendar.get(Calendar.DAY_OF_WEEK);
			int betweenDay = Integer.valueOf(week[i]) + 1 - currentCalendar.get(Calendar.DAY_OF_WEEK);
			Date beginDate = Datetimes.addDate(startDate, betweenDay);
			if(betweenDay < 0){
				Date eventEndDate = Datetimes.addDate(beginDate, (int)Datetimes.minusDay(event.getEndDate(), event.getBeginDate()));
				//如果是本周，也要生成下周的
				if(eventEndDate.compareTo(startDate) < 0 || Datetimes.minusDay(startDate, event.getCreateDate()) <=7){
					betweenDay +=7;
				}
			}
			Date trueBeginDate = Datetimes.addDate(startDate, betweenDay);
			while(Datetimes.minusDay(endDate, trueBeginDate) >=0){
				//周期性事件开始后，才加入列表
				if (info.getBeginTime() != null) {
					if(Datetimes.minusDay(trueBeginDate,info.getBeginTime())>=0){
						result.add(perCreateEvent(event,trueBeginDate));
					}
					trueBeginDate = Datetimes.addDate(trueBeginDate, 7);
				}else{
					break;
				}
			}
		}
		return result;
	}
	/**
	 * 每月第几日，如每月23日
	 * @param event
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<CalEvent> everyMonthDay(PeriodicalCalEvent event, Date startDate,Date endDate,CalEventPeriodicalInfo info){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(event.getBeginDate());
		endDate = info.getEndTime() != null && info.getEndTime().before(endDate) ? info.getEndTime() : endDate;
		Calendar currentCalendar = new GregorianCalendar();
		currentCalendar.setTime(startDate);
		
		//CalEventPeriodicalInfo info = calEventDao.getPeriodicalInfoByPeriodicalEvent(event.getId());
		
		//int betweenDay = calendar.get(Calendar.DAY_OF_MONTH) - currentCalendar.get(Calendar.DAY_OF_MONTH);
		int betweenDay = info.getDayDate() - currentCalendar.get(Calendar.DAY_OF_MONTH);
		
		Date beginDate = Datetimes.addDate(startDate, betweenDay);
		if(betweenDay < 0){
			Date thisEndDate = Datetimes.addDate(beginDate, (int)Datetimes.minusDay(event.getEndDate(), event.getBeginDate()));
			if(thisEndDate.compareTo(startDate) < 0){//结束时间没有在开始时间查询范围内，生成下个月的
				beginDate = Datetimes.addMonth(beginDate, 1);
			}
		}
		List<CalEvent> result = new ArrayList<CalEvent>();
		while(Datetimes.minusDay(endDate, beginDate) >=0){
			CalEvent cal = perCreateEvent(event,beginDate);
			beginDate = Datetimes.addMonth(beginDate, 1);
			result.add(cal);
		}
		return result;
	}
	/**
	 * 每月第几周星期几，如每月第一周星期二
	 * @param event
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<CalEvent> everyMonthWeekDay(PeriodicalCalEvent event, Date startDate,Date endDate,CalEventPeriodicalInfo info){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(event.getBeginDate());
		
		//CalEventPeriodicalInfo info = calEventDao.getPeriodicalInfoByPeriodicalEvent(event.getId());
		endDate = info.getEndTime() != null && info.getEndTime().before(endDate) ? info.getEndTime() : endDate;
		Calendar currentCalendar = new GregorianCalendar();
		currentCalendar.setTime(startDate);
//		currentCalendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH));
//		currentCalendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
		currentCalendar.set(Calendar.WEEK_OF_MONTH, info.getWeek());
		currentCalendar.set(Calendar.DAY_OF_WEEK, info.getDayWeek());
		
		Date beginDate = currentCalendar.getTime();
		
		Date thisEndDate = Datetimes.addDate(beginDate, (int)Datetimes.minusDay(event.getEndDate(), event.getBeginDate()));
		if(thisEndDate.compareTo(startDate) < 0){//结束时间没有在开始时间查询范围内，生成下个月的
			beginDate = Datetimes.addMonth(beginDate, 1);
		}
		List<CalEvent> result = new ArrayList<CalEvent>();
		while(Datetimes.minusDay(endDate, beginDate) >=0){
			CalEvent cal = perCreateEvent(event,beginDate);
			beginDate = Datetimes.addMonth(beginDate, 1);
			result.add(cal);
		}
		return result;
	}
	/**
	 * 每年几月几日，如每年11月29日
	 * @param event
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<CalEvent> everyYearMonthDay(PeriodicalCalEvent event, Date startDate,Date endDate,CalEventPeriodicalInfo info){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(event.getBeginDate());
		//CalEventPeriodicalInfo info = calEventDao.getPeriodicalInfoByPeriodicalEvent(event.getId());
		endDate = info.getEndTime() != null && info.getEndTime().before(endDate) ? info.getEndTime() : endDate;
		Calendar currentCalendar = new GregorianCalendar();
		currentCalendar.setTime(startDate);
		calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, info.getMonth() - 1);
		calendar.set(Calendar.DAY_OF_MONTH, info.getDayDate());
		
		Date beginDate = calendar.getTime();
		
		Date thisEndDate = Datetimes.addDate(beginDate, (int)Datetimes.minusDay(event.getEndDate(), event.getBeginDate()));
		if(thisEndDate.compareTo(startDate) < 0){//结束时间没有在开始时间查询范围内，生成下个年的
			beginDate = Datetimes.addYear(beginDate, 1);
		}
		List<CalEvent> result = new ArrayList<CalEvent>();
		while(Datetimes.minusDay(endDate, beginDate) >=0){
			CalEvent cal = perCreateEvent(event,beginDate);
			beginDate = Datetimes.addYear(beginDate, 1);
			result.add(cal);
		}
		return result;
	}
	/**
	 * 每年几月第几个星期几，如每年11月第二个星期三
	 * @param event
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<CalEvent> everyYearMonthWeekDay(PeriodicalCalEvent event,Date startDate,Date endDate,CalEventPeriodicalInfo info){
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(event.getBeginDate());
		
		//CalEventPeriodicalInfo info = calEventDao.getPeriodicalInfoByPeriodicalEvent(event.getId());
		endDate = info.getEndTime() != null && info.getEndTime().before(endDate) ? info.getEndTime() : endDate;
		Calendar currentCalendar = new GregorianCalendar();
		currentCalendar.setTime(startDate);
//		currentCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
//		currentCalendar.set(Calendar.WEEK_OF_MONTH, calendar.get(Calendar.WEEK_OF_MONTH));
//		currentCalendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
		currentCalendar.set(Calendar.MONTH, info.getMonth());
		currentCalendar.set(Calendar.WEEK_OF_MONTH, info.getWeek());
		currentCalendar.set(Calendar.DAY_OF_WEEK, info.getDayWeek());
		
		Date beginDate = currentCalendar.getTime();
		
		Date thisEndDate = Datetimes.addDate(beginDate, (int)Datetimes.minusDay(event.getEndDate(), event.getBeginDate()));
		if(thisEndDate.compareTo(startDate) < 0){//结束时间没有在开始时间查询范围内，生成下个年的
			beginDate = Datetimes.addYear(beginDate, 1);
		}
		List<CalEvent> result = new ArrayList<CalEvent>();
		while(Datetimes.minusDay(endDate, beginDate) >=0){
			CalEvent cal = perCreateEvent(event,beginDate);
			beginDate = Datetimes.addYear(beginDate, 1);
			result.add(cal);
		}
		return result;
	}
	
	public void savePeriodicalEventRelition(CalEventPeriodicalRelation relation){
		this.calEventDao.save(relation);
	}

	/** 将副本的开始、结束时间按照当前日期调整后，设置为重复生成事件的开始和结束日期  */
	private void setDate4RepeatedEvent(CalEvent repeatEvent, Date beginDate) {
		
		GregorianCalendar beginCalendar = new GregorianCalendar();
		beginCalendar.setTimeInMillis(repeatEvent.getBeginDate().getTime());
		GregorianCalendar endCalendar = new GregorianCalendar();
		endCalendar.setTimeInMillis(repeatEvent.getEndDate().getTime());

		int day = (int)Datetimes.minusDay(repeatEvent.getEndDate(), repeatEvent.getBeginDate());
		
		beginDate = Datetimes.getTodayFirstTime(beginDate);
		
		Date startTime = Datetimes.addHour(beginDate, beginCalendar.get(Calendar.HOUR_OF_DAY));
		startTime =  Datetimes.addMinute(startTime, beginCalendar.get(Calendar.MINUTE));
		
		Date endDate = Datetimes.addDate(beginDate, day);
		endDate = Datetimes.addHour(endDate, endCalendar.get(Calendar.HOUR_OF_DAY));
		endDate =  Datetimes.addMinute(endDate, endCalendar.get(Calendar.MINUTE));
		
		repeatEvent.setCreateDate(new Date());
		repeatEvent.setBeginDate(startTime);
		repeatEvent.setEndDate(endDate);
	}
	
	/*
	 * (non-Javadoc)得到某个时间段、某个完成状态下所有个人事件
	 * 
	 * @see
	 * com.seeyon.v3x.calendar.manager.CalEventManager#getEventListByUserId(
	 * java.lang.Long, java.util.Date, java.util.Date, java.lang.Integer,
	 * java.lang.Integer)
	 */
	public List<CalEventStatistics> getEventListByUserId(Long userId,Date beginDate, Date endDate, Integer states, Integer statistics) {
		return this.calEventDao.getEventListByUserId(userId, beginDate,endDate, states, statistics);
	}
	
	/*
	 * (non-Javadoc)得到某个时间段、某个完成状态下、根据统计类型查出的个人事件
	 * 
	 * @see
	 * com.seeyon.v3x.calendar.manager.CalEventManager#getEventListByUserId(
	 * java.lang.Long, java.util.Date, java.util.Date, java.lang.Integer,
	 * java.lang.Integer, java.lang.Integer)
	 */
	public List<CalEvent> getEventListByUserId(Long userId, Date beginDate,Date endDate, Integer states, Integer statistics, Integer value) {
		return initList(this.calEventDao.getEventListByUserId(userId, beginDate,endDate, states, statistics, value));
	}
	public CalEventPeriodicalInfo getPeriodicalInfo(Long id) {
		return calEventDao.getPeriodicalEvent(id);
	}
	
	public CalEventPeriodicalInfo getPeriodicalInfoByCalEventId(Long id) {
		return calEventDao.getCalEventPeriodicalInfoByCalEventId(id);
	}

	/**
	 * 生成重复周期性事件
	 */
	public void handlePeriodicalEvents() {
		List<PeriodicalCalEvent> events = this.calEventDao.getPeriodicalEvents();
		this.createRepeatedCalEvents(events);
	}

	private void createRepeatedCalEvents(List<PeriodicalCalEvent> calEvents) {
		if(CollectionUtils.isNotEmpty(calEvents)) {
			for(PeriodicalCalEvent event : calEvents) {
				CalEventPeriodicalInfo info = calEventDao.getPeriodicalInfoByPeriodicalEvent(event.getId());
				if(info.getPeriodicalType() == PeriodicalType.EveryDay.ordinal()){
					//处理每几天提醒
					int dayDate = info.getDayDate();
					int day = (int)Datetimes.minusDay(new Date(), info.getBeginTime());
					if(day % dayDate == 0){
						this.repeatCalEvent(event);
					}
				} else if(info.getPeriodicalType() == PeriodicalType.EveryWeek.ordinal()){
					//处理每周几提醒
					String weeks = info.getWeeks();
					String[] week = weeks.split(",");
					int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
					for(int i=0; i<week.length; i++){
						if((Integer.valueOf(week[i]) + 1) == weekday){
							this.repeatCalEvent(event);
						}
					}
				} else{
					this.repeatCalEvent(event);
				}
			}
		}
	}

	private void copyAttachment(Long ref,Long newRef,Long userId,Long accountId){
		try {
			this.attachmentManager.copy(ref, ref,newRef,newRef,ApplicationCategoryEnum.calendar.key(),userId,accountId);
		} catch (Exception e) {
			log.error("复制附件",e);
		}
	}
	/** 根据副本生成重复日程事件　*/
	private void repeatCalEvent(PeriodicalCalEvent event) {
		try {
			Long eventId = event.getId();
			CalEvent repeatEvent = this.getRepeatCalEvent(event);
			//保存子表信息：正文内容、共享信息、附件等
			if(repeatEvent == null)
				return;
			repeatEvent.setId(null);
			repeatEvent.setIdIfNew();
			Long newEventId = repeatEvent.getId();
			
			this.saveRepeatedContent(eventId, newEventId);
			this.saveRepeatTrans(event, newEventId);
			V3xOrgMember member = null;
			try {
				member = orgManager.getMemberById(event.getCreateUserId());
				if(member != null){
					copyAttachment(eventId,newEventId,member.getId(),member.getOrgAccountId());
				}
			} catch (Exception e) {
				log.error("查找人员",e);
			}
			//提醒任务调度 --- 如果没设置提前提醒，按开始时间提醒
			//if(repeatEvent.isAlarmFlag()) {
			this.eventRemind(repeatEvent, true);
			//}
			beforEndRemind(repeatEvent);
			//保存事件
			this.calEventDao.save(repeatEvent);
			
			//当每次任务启动时，（安排事件，委托事件）达到触发条件就会会调度执行一次当前方法，下面代码导致，同一个对应事件，产生重复的提醒；
			//给安排、委托发送系统消息
			/*
			if(Strings.isNotBlank(repeatEvent.getReceiveMemberId())) {
				switch(EventType.valueOf(repeatEvent.getEventType())){
				case arrange://安排事件
					userMessageManager.sendSystemMessage(
							new MessageContent("cal.anPai", repeatEvent.getSubject(), member.getName()),
							ApplicationCategoryEnum.calendar, repeatEvent.getCreateUserId(), 
							MessageReceiver.get(repeatEvent.getId(), FormBizConfigUtils.parseTypeAndIdStr2Ids(repeatEvent.getReceiveMemberId()),"message.link.cal.view", repeatEvent.getId(), CalendarNotifier.getRandomStr())
					);
					break;
				case consign://委托事件
					userMessageManager.sendSystemMessage(
							new MessageContent("cal.colAssign", repeatEvent.getSubject(), member.getName()),
							ApplicationCategoryEnum.calendar, repeatEvent.getCreateUserId(), 
							MessageReceiver.get(repeatEvent.getId(), FormBizConfigUtils.parseTypeAndIdStr2Ids(repeatEvent.getReceiveMemberId()), "message.link.cal.view", repeatEvent.getId(), CalendarNotifier.getRandomStr())
					);
					break;
				}
			}*/

			//保存周期性与日程事件中间表
			CalEventPeriodicalInfo info = calEventDao.getPeriodicalInfoByPeriodicalEvent(event.getId());
			CalEventPeriodicalRelation relation = new CalEventPeriodicalRelation();
			relation.setIdIfNew();
			relation.setCalEventPeriodicalInfoId(info.getId());
			relation.setCalEventId(repeatEvent.getId());
			relation.setCreateDate(new Date());
			relation.setMemberId(event.getCreateUserId());
			this.savePeriodicalEventRelition(relation);
		} catch (Exception e) {
			log.error("复制事件",e);
		}
		
	}

	private CalEvent getRepeatCalEvent(PeriodicalCalEvent event) {
		CalEvent repeatEvent = null;
		try {
			PeriodicalCalEvent repeat = (PeriodicalCalEvent)event.clone();
			repeatEvent = new CalEvent(repeat);
			//TODO 这里的开始时间指定的不正确
			Date today = Datetimes.getTodayFirstTime();
			this.setDate4RepeatedEvent(repeatEvent,today);
		}
		catch (CloneNotSupportedException e) {
			log.error("复制周期性事件过程中出现异常：", e);
		}
		return repeatEvent;
	}

	private void saveRepeatTrans(PeriodicalCalEvent event, Long newEventId) {
		if(Strings.isNotBlank(event.getTranMemberIds()) || Strings.isNotBlank(event.getReceiveMemberId())) {
			List<CalEventTran> trans = this.calEventTranManager.getEventTranListByEventId(event.getId());
			if(CollectionUtils.isNotEmpty(trans)) {
				for(CalEventTran tran : trans) {
					try {
						CalEventTran repeatTran = (CalEventTran)tran.clone();
						repeatTran.setId(null);
						repeatTran.setEventId(newEventId);
						this.calEventTranManager.save(repeatTran);
					}
					catch (CloneNotSupportedException e) {
						log.error("复制周期性事件共享人员信息过程中出现异常：", e);
					}
				}
			}
		}
	}

	private void saveRepeatedContent(Long eventId, Long newEventId) {
		try {
			CalContent content = this.calContentManager.getEventContentByEventId(eventId);
			if(content != null){
				CalContent repeatContent = (CalContent)content.clone();
				repeatContent.setId(null);
				repeatContent.setEventId(newEventId);
				this.calContentManager.save(repeatContent);
			}
		}
		catch (Exception e1) {
			log.error("复制周期性事件正文过程中出现异常：", e1);
		}
	}
	
	public void saveOrUpdate(CalEventPeriodicalInfo periodicalInfo,Boolean isNew) {
		//存储calEvent
		saveCalEvent(periodicalInfo.getCalEvent(),isNew);
		//存储周期性事件
		if(isNew)
			calEventDao.save(periodicalInfo);
		else
			calEventDao.update(periodicalInfo);
	}
	//存储calEvent 以及相关信息，不发送消息等
	private void saveCalEvent(PeriodicalCalEvent event,Boolean isNew){
		save(event, isNew);
	}
	
	
	public CalEventPeriodicalRelation getCalEventPeriodicalRelation(Long id){
		String hql = "from " + CalEventPeriodicalRelation.class.getName() + " as r "
					+ " where r.calEventId=?";
		List<CalEventPeriodicalRelation> result = calEventDao.find(hql, -1, -1, null, id);
		return result.get(0);
	}
	
	public List<CalEvent> getAllPeriodicalEventByPeriodicalInfoId(Long id){
		String hql = "select e from " + CalEvent.class.getName() + " as e,"
					+ CalEventPeriodicalRelation.class.getName() + " as r "
					+ " where e.id=r.calEventId and r.calEventPeriodicalInfoId=?";
		List<CalEvent> result = calEventDao.find(hql, -1, -1, null, id);
		return result;
	}
	
	// 根据起止日期、状态和用户ID获得该用户的符合条件的日程事件
	public List<CalEvent> getEventListByArgu(Long userId,int firstResult,int maxResults, 
			Date beginDate, Date endDate, Integer states) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer("from " + CalEvent.class.getName()
				+ " as c where ");
		if(beginDate!=null&&endDate!=null)
		{
			hql.append(" c.beginDate >= :beginDate and c.endDate <= :endDate and ");
			params.put("beginDate", beginDate);
			params.put("endDate", endDate);
		}
		else if(beginDate!=null)
		{
			hql.append(" c.beginDate >= :beginDate and ");
			params.put("beginDate", beginDate);
		}
		else if(endDate!=null)
		{
			hql.append(" c.endDate <= :endDate and ");
			params.put("endDate", endDate);
		}
		if (states != -1) {
			hql.append(" c.states = :states and ");
			params.put("states", states);
		}
		
		hql
				.append(" (c.createUserId = :userId or c.receiveMemberId like :userScopeId) ");
		params.put("userId", userId);
		params.put("userScopeId", "%"
				+ SQLWildcardUtil.escape(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|"
						+ userId) + "%");
		
		List<CalEvent> list = null;;
		if(firstResult!=-1 && maxResults!=-1)
			list = calEventDao.find(hql.toString(), firstResult, maxResults, params);
		else
			list = calEventDao.find(hql.toString(), -1, -1, params);
		return (list!=null&&list.size()>0)?list:null;
	}
	
	//根据起止日期、状态和用户ID获得该用户的符合条件的日程事件总数
	public int getEventCountByArgu(Long userId, 
			Date beginDate, Date endDate, Integer states) {		
		List<CalEvent> list = this.getEventListByArgu(userId, -1, -1, beginDate, endDate, states);
		
		return list==null?0:list.size();
	}

	public void deleteCalEventFromTask(Long taskId) {
		this.deleteCalEventFromOtherAppId(taskId, ApplicationCategoryEnum.taskManage.key(), null);
	}

	public void saveCalEventFromTask(TaskInfo task) {
		Long userId = CurrentUser.get().getId();
		CalEvent calEvent = new CalEvent();
		calEvent.setFromId(task.getId());
		calEvent.setFromType(ApplicationCategoryEnum.taskManage.key());
		
		calEvent.setAlarmFlag(task.remindBeforeStart());
		calEvent.setAlarmDate(NumberUtils.toLong(task.getRemindStartTime().toString()));
		calEvent.setBeforendAlarm(NumberUtils.toLong(task.getRemindEndTime().toString()));
        calEvent.setBeginDate(task.getPlannedStartTime()); 
        calEvent.setEndDate(task.getPlannedEndTime()); 
        calEvent.setCompleteRateInt((int)task.getFinishRate());
        calEvent.setCreateDate(new Date(System.currentTimeMillis()));
        calEvent.setCreateUserId(task.getCreateUser()); 
        calEvent.setRealEstimateTime(task.getActualTaskTime());
        calEvent.setSubject(task.getSubject());
        calEvent.setSignifyType(task.parse2CalEventSignifyType());
        calEvent.setStates(task.getStatus());
        calEvent.setUpdateDate(new Date(System.currentTimeMillis()));

		// 事件完成类型： 2.已安排
        calEvent.setStates(2); 			
        // 工作类型：1.自办
        calEvent.setWorkType(1); 		
        // 事件当前类型标识：1.已安排
        calEvent.setEventflag(1); 		
        // 默认为业务
        calEvent.setCalEventType(0); 
        
        // 事件类型 ：安排，优先级类型：2.中
        calEvent.setEventType(2);  		
        calEvent.setPriorityType(2);
        StringBuilder tranMemberIds = new StringBuilder(task.getManagers());
        if(Strings.isNotBlank(task.getParticipators())) {
        	tranMemberIds.append(',' + task.getParticipators());
        }
        
        String memberIds = tranMemberIds.toString();
        Set<Long> set = com.seeyon.v3x.doc.util.Constants.parseStrings2Longs(memberIds, ",");
        // 任务创建者是事件持有者，需排除在外
        set.remove(task.getCreateUser());
        
        boolean hasTransMember = CollectionUtils.isNotEmpty(set);
        String allMembers = null;
        if (task.getProjectId() == -1) {
        	if (hasTransMember) {
        		// 共享类型：安排给他人(负责人、参与人)
        		calEvent.setShareType(1);		
        		
        		memberIds = StringUtils.join(set.iterator(), ',');
        		String membersName = Functions.showOrgEntities(memberIds, V3xOrgEntity.ORGENT_TYPE_MEMBER, TaskUtils.getCommonI18n("common.separator.label"));
        		calEvent.setTranMemberName(membersName);
        		calEvent.setReceiveMemberName(membersName);
        		
        		allMembers = (V3xOrgEntity.ORGENT_TYPE_MEMBER + '|' + memberIds).replaceAll(",", ',' + V3xOrgEntity.ORGENT_TYPE_MEMBER + '|');
        		calEvent.setReceiveMemberId(allMembers);
        	} else {
        		// 共享类型：如果没有参与人，负责人就是创建人，则此时事件性质为私人事件
        		calEvent.setShareType(1);		
        	}
		} else {
			// 共享类型：共享给项目
			calEvent.setShareType(6);
			calEvent.setTranMemberIds(String.valueOf(task.getProjectId()));

			if (hasTransMember) {
				memberIds = StringUtils.join(set.iterator(), ',');
				String membersName = Functions.showOrgEntities(memberIds, V3xOrgEntity.ORGENT_TYPE_MEMBER, TaskUtils.getCommonI18n("common.separator.label"));
				calEvent.setTranMemberName(membersName);
				calEvent.setReceiveMemberName(membersName);
				allMembers = (V3xOrgEntity.ORGENT_TYPE_MEMBER + '|' + memberIds).replaceAll(",", ',' + V3xOrgEntity.ORGENT_TYPE_MEMBER + '|');
				calEvent.setReceiveMemberId(allMembers);
			}
		}
        CalContent calContent = new CalContent();
        calContent.setContent(task.getContent());
        calContent.setContentType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
        calContent.setCreateDate(new Date(System.currentTimeMillis()));
        
        Long eventId = this.saveOrUpdateCalEventFromOtherApp(calEvent, calContent, userId);
        
        if (task.getProjectId() != TaskConstants.PROJECT_NONE) {
        	Long projectId = task.getProjectId();
        	String managerIds = task.getManagers() + ",";
        	String participatorIds = task.getParticipators();
        	String idStrs = managerIds + participatorIds;
        	String [] ids = idStrs.split(",");
			this.calEventTranManager.saveProjectTranEvents(calEvent, projectId, 6, ids);
			CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.p_ON_SHARE, null, null, calEvent, this.orgManager, this.userMessageManager, this.calendarUtils);
        }
        if (hasTransMember && task.getProjectId() == TaskConstants.PROJECT_NONE) {
        	this.calEventTranManager.saveTranEvents(calEvent, allMembers, 2, true);
    		CalendarNotifier.sendNotifierMessageInsert(CalendarNotifier.p_ON_SHARE, null, null, calEvent, this.orgManager, this.userMessageManager, this.calendarUtils);
        }
        if(calEvent.isAlarmFlag()) {
        	this.eventRemind(calEvent);
        }
        
        if(calEvent.getBeforendAlarm() != null && calEvent.getBeforendAlarm() > 0) {
        	this.beforEndRemind(calEvent);
        }
        try {
        	//保存项目当前阶段
			if(calEvent.getShareType() == ShareType.project.key() && StringUtils.isNotBlank(calEvent.getTranMemberIds())){
				if(task.getProjectPhaseId() != TaskConstants.PROJECT_PHASE_ALL){
					ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.calendar.key(), calEvent.getId(), task.getProjectPhaseId());
					projectPhaseEventManager.save(projectPhaseEvent);
				}
			}
		} catch (Exception e) {
			log.error("保存项目阶段时出现异常: ", e);
		}
        try {
	        IndexInfo index = this.getIndexInfo(eventId);
	        indexManager.index(index);
		} 
        catch(Exception e) {
	    	log.error("日程事件[id=" + eventId + "]全文检索入库时出现异常: ", e);
	    }
	}
	
	/**
	 * 根据eventId获取周期性事件Id
	 * @param eventId
	 */
	public Long getPeriodicalId(Long eventId) {
		CalEventPeriodicalRelation relation = calEventDao.getCalEventPeriodicalRelationByCalEventId(eventId);
		if (relation != null) {
			return relation.getCalEventPeriodicalInfoId();
		} else {
			return eventId;
		}
	}
}