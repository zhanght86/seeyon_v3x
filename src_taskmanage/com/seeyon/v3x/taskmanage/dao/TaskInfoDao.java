package com.seeyon.v3x.taskmanage.dao;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskRole;
import com.seeyon.v3x.taskmanage.domain.TaskRole.RoleType;
import com.seeyon.v3x.taskmanage.utils.StatisticCondition;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.taskmanage.utils.TaskQueryModel;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.ListType;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.StatisticPeriod;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.TaskStatus;
import com.seeyon.v3x.taskmanage.utils.TaskQueryModel.TaskQueryType;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 任务信息Dao
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskInfoDao extends BaseHibernateDao<TaskInfo> {
	
	private static final Log logger = LogFactory.getLog(TaskInfoDao.class);
	
	/**
	 * 获取全部子任务的ID
	 * @param id	当前任务ID
	 * @return	当前任务下面全部子任务的ID集合
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getChildTaskIds(Long id) {
		String hql = "select id from " + TaskInfo.class.getCanonicalName() + " where parentTaskId=?";
		return super.find(hql, -1, -1, null, id);
	}
	
	/**
	 * 获取工作管理和项目任务统计处，按照指定统计条件所能查看的任务列表
	 * @param sc	统计条件模型	
	 */
	@SuppressWarnings("unchecked")
	public List<TaskInfo> getTasks(StatisticCondition sc) {
		if(logger.isDebugEnabled()) {
			logger.debug("统计条件:\n" + TaskConstants.xStream4Debug.toXML(sc));
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct t from " + TaskInfo.class.getCanonicalName() + " as t, " + 
				   TaskRole.class.getCanonicalName() + " as r where t.id=r.taskId ");
		
		// 工作管理处的统计也将项目任务包括进来
		if(sc.getProjectId() != null && sc.getProjectId() != TaskConstants.PROJECT_NONE) {
			hql.append(" and t.projectId = :projectId ");
			params.put("projectId", sc.getProjectId());
		}
		
		if(sc.getProjectPhaseId() != null && sc.getProjectPhaseId() != TaskConstants.PROJECT_PHASE_ALL) {
			hql.append("and t.projectPhaseId = :projectPhaseId ");
			params.put("projectPhaseId", sc.getProjectPhaseId());
		}
		
		if(sc.getStatus() != -1) {
			hql.append(" and t.status = :status ");
			params.put("status", sc.getStatus());
		}
		
		Date beginDate = sc.getBeginDate(), endDate = sc.getEndDate();
		if(beginDate != null && endDate != null) {
			hql.append(" and ((t.plannedStartTime >= :beginDate and t.plannedStartTime <= :endDate) or " +
					   " (t.plannedEndTime >= :beginDate and t.plannedEndTime <= :endDate)) ");
		}
		else if(beginDate != null) {
			hql.append(" and t.plannedStartTime >= :beginDate ");
		}
		else if(endDate != null) {
			hql.append(" and t.plannedEndTime <= :endDate ");
		}
		
		if(beginDate != null) {
			params.put("beginDate", beginDate);
		}
		if(endDate != null) {
			params.put("endDate", endDate);
		}
		
		if(CollectionUtils.isNotEmpty(sc.getMemberIds())) {
			if(sc.getMemberIds().size() == 1) {
				hql.append(" and r.roleId = :memberId ");
				params.put("memberId", sc.getMemberIds().get(0));
			}
			else {
				hql.append(" and r.roleId in (:memberIds) ");
				params.put("memberIds", sc.getMemberIds());
			}
		}
		
		hql.append(" order by t.createTime desc");
		return this.find(hql.toString(), "t.id", true, params);
	}
	
	/**
	 * 获取当前用户根据指定类型、属性查询模型等所能查看的任务列表
	 * @param listType	列表类型：我的任务 or 任务管理
	 * @param userId	当前用户ID
	 * @param tqm		简单属性查询模型
	 */
	@SuppressWarnings("unchecked")
	public List<TaskInfo> getTasks(ListType listType, Long userId, TaskQueryModel tqm) {
		if(logger.isDebugEnabled()) {
			logger.debug("列表类型：" + listType.name() + ", 用户ID：" + userId + ", 查询模型：\n" + TaskConstants.xStream4Debug.toXML(tqm));
		}
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		
		this.handleListType(listType, userId, tqm, params, hql);
		boolean valid = this.handleQueryModel(tqm, params, hql);
		if(!valid) {
			return null;
		}
		hql.append(" order by t.createTime desc");
		
		boolean pagination = tqm == null || tqm.isPagination();
		boolean distinct = Distinct_ListTypes.contains(listType);
		return pagination ? this.find(hql.toString(), "t.id", distinct, params) : this.find(hql.toString(), -1, -1, params);
	}
	
	/**
	 * 获取当前用户根据指定类型、属性查询模型等所能查看的任务列表  为时间管理 huangfj 2012-07-13
	 * @param listType	列表类型：我的任务 or 任务管理
	 * @param userId	当前用户ID
	 * @param plannedStartTime 开始时间
	 * @param plannedEndTime   结束时间
	 */
	public List<TaskInfo> getTasksForTiming(ListType listType, Long userId,Date plannedStartTime, Date plannedEndTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		this.handleListType(listType, userId, null, params, hql);

		if(plannedStartTime!=null) {
			hql.append(" and t.plannedEndTime >= :plannedStartTime ");
			params.put("plannedStartTime", plannedStartTime);
		}
		if(plannedEndTime!=null) {
			hql.append(" and t.plannedStartTime <= :plannedEndTime ");
			params.put("plannedEndTime", plannedEndTime);
		}
		hql.append(" order by t.createTime desc");
		return this.find(hql.toString(), -1, -1, params);
	}
	
	public int getCountPendingTask(ListType listType, Long userId, TaskQueryModel tqm) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		
		this.handleListType(listType, userId, tqm, params, hql);
		boolean valid = this.handleQueryModel(tqm, params, hql);
		if(!valid) {
			return 0;
		}
		
		return super.count(hql.toString(), params);
	}
	
	private static final List<ListType> Distinct_ListTypes = Arrays.asList(ListType.Parent, ListType.Statistic, ListType.ProjectMember);

	/**
	 * 根据不同的列表类型获取Hql语句中select * from * where部分内容，附在总Hql语句中，并将所需参数设入Map
	 * @param listType	列表类型
	 * @param userId	用户ID
	 * @param tqm		查询模型
	 * @param params	命名参数Map
	 * @param hql		总hql
	 */
	private void handleListType(ListType listType, Long userId, TaskQueryModel tqm, 
			Map<String, Object> params, StringBuilder hql) {
		switch(listType) {
		case Sent :
			// 约束条件：只能是任务创建人（不能同时是任务负责人或参与人）
			hql.append("from " + TaskInfo.class.getCanonicalName() + " as t where t.createUser = :userId and t.id not in " +
					   "( select r.taskId from " + TaskRole.class.getCanonicalName() + " as r where r.roleId = :userId and r.roleType > :creator) ");
			params.put("userId", userId);
			params.put("creator", RoleType.Creator.key());
			break;
		case ProjectAll :
			hql.append("from " + TaskInfo.class.getCanonicalName() + " as t where t.projectId = :projectId ");
			params.put("projectId", tqm.getProjectId());
			break;
		case Manage :
		case Personal :
			// 约束条件：任务负责人或参与人（用户同时是创建人和负责人/参与人的，也归入此类）
			hql.append("from " + TaskInfo.class.getCanonicalName() + " as t where t.id in " +
					   "( select r.taskId from " + TaskRole.class.getCanonicalName() + " as r where r.roleId = :userId and r.roleType > :creator) ");
			params.put("userId", userId);
			params.put("creator", RoleType.Creator.key());
			break;
		default :
			hql.append("select distinct t from " + TaskInfo.class.getCanonicalName() + " as t, " + 
					   TaskRole.class.getCanonicalName() + " as r where t.id=r.taskId and r.roleId = :userId ");
			params.put("userId", userId);
			break;
		}
	}

	/**
	 * 处理查询条件，附在总Hql语句中，并将所需参数设入Map
	 * @param tqm		查询模型
	 * @param params	命名参数Map
	 * @param hql		总hql
	 * @return	是否能返回有效记录，如不能，表明当前查询条件获取不到任何有效任务记录
	 */
	private boolean handleQueryModel(TaskQueryModel tqm, Map<String, Object> params, StringBuilder hql) {
		if(tqm != null) {
			if(tqm.getProjectId() != null && tqm.getProjectId() != TaskConstants.PROJECT_NONE) {
				hql.append(" and t.projectId = :projectId ");
				params.put("projectId", tqm.getProjectId());
			}
			
			if(tqm.getProjectPhaseId() != null && tqm.getProjectPhaseId() != TaskConstants.PROJECT_PHASE_ALL) {
				hql.append("and t.projectPhaseId = :projectPhaseId ");
				params.put("projectPhaseId", tqm.getProjectPhaseId());
			}
			
			TaskQueryType queryType = tqm.getQueryType();
			// 已完成和已取消的任务通过查询条件查询展现，默认不显示
			if(queryType == null) {
				hql.append(" and t.status <" + TaskStatus.Finished.key());
			}
			
			if(queryType != null) {
				String propName = queryType.name();
				switch(queryType) {
				case subject :
					if(Strings.isNotBlank(tqm.getValue1())) {
						hql.append(" and t." + propName + " like :" + propName);
						params.put(propName, "%" + SQLWildcardUtil.escape(tqm.getValue1().trim()) + "%");
					}
					break;
				case plannedStartTime :
				case plannedEndTime :
					if(Strings.isNotBlank(tqm.getValue1())) {
						hql.append(" and t." + propName + " >= :" + propName + "startDate ");
						params.put(propName + "startDate", Datetimes.getTodayFirstTime(tqm.getValue1()));
					}
					if(Strings.isNotBlank(tqm.getValue2())) {
						hql.append(" and t." + propName + " <= :" + propName + "endDate ");
						params.put(propName + "endDate", Datetimes.getTodayLastTime(tqm.getValue2()));
					}
					break;
				case status :
					String[] valueStrs = tqm.getValue1().split("[,]");
					Integer[] values = new Integer[valueStrs.length];
					for (int i = 0; i < valueStrs.length; i++) {
						values[i] = NumberUtils.toInt(valueStrs[i]);
					}
					
					hql.append(" and (t." + propName + " in (:" + propName + "))");
					params.put(propName, values);
					break;
				case riskLevel :
				case importantLevel :
					hql.append(" and t." + propName + " = :" + propName);
					params.put(propName, NumberUtils.toInt(tqm.getValue1()));
					break;
				case createUser :
					if(Strings.isNotBlank(tqm.getValue1()) && Strings.isNotBlank(tqm.getValue2())) {
						hql.append(" and t." + propName + " = :" + propName);
						params.put(propName, NumberUtils.toLong(tqm.getValue2()));
					}
					break;
				case managers :
				case participators :
					if(Strings.isNotBlank(tqm.getValue1()) && Strings.isNotBlank(tqm.getValue2())) {
						hql.append(" and t." + queryType.name() + " like :mId ");
						params.put("mId", "%" + NumberUtils.toLong(tqm.getValue2()) +"%");
					}
					break;
				}
			}
		}
		else {
			hql.append(" and t.status <" + TaskStatus.Finished.key());
		}
		
		return true;
	}
	
	/**
	 * 判断选中的任务中是否包含有子任务
	 * @param ids	选中的任务ID字符串拼接
	 * @return	是否存在子任务
	 */
	public boolean checkIfChildExist(final List<Long> ids) {
		return (Integer)super.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql = "select count(id) from " + TaskInfo.class.getCanonicalName() + " where parentTaskId in (:ids)";
				Query query = session.createQuery(hql);
				query.setParameterList("ids", ids);
				return query.uniqueResult();
			}
    	}) > 0;
	}

	/**
	 * 获取当前任务相关的全部上级、下级任务，用于展现任务树
	 * @param task		当前任务
	 * @return	当前任务的全部上级、下级任务(包括自己)，按照任务的逻辑层级深度升序排列(也即第一个元素为根节点)
	 */
	@SuppressWarnings("unchecked")
	public List<TaskInfo> getTaskTree(TaskInfo task) {
		String hql = "from " + TaskInfo.class.getCanonicalName() + " where logicalPath like :logicalPath order by logicalDepth asc, createTime asc";
		Map<String, Object> params = new HashMap<String, Object>();
		String logicalPath = task.getLogicalPath();
		String rootPath = logicalPath.indexOf('.') == -1 ? logicalPath : logicalPath.substring(0, logicalPath.indexOf('.'));
		params.put("logicalPath", rootPath  + "%");
		
		List<TaskInfo> tasks = this.find(hql, -1, -1, params);
		TaskUtils.tree(tasks);
		return tasks;
	}

	/**
	 * 获取工作任务的<b>汇总</b>统计结果，包括了五种状态、五种时间段的统计数据
	 * @param membersList		所要进行统计的人员ID集合
	 * @param beginDate			<b>自定义</b>统计开始日期
	 * @param endDate			<b>自定义</b>统计结束日期
	 * @return	<code>Map&lt;Long, int[]&gt;</code> key - 人员ID，value - 14项统计结果(int[14])，依次为：<br>
	 * 			未开始[0]<br>
	 * 			本日进行中[1]、本日已完成[2]、本日已延期[3]<br>
	 * 			本周进行中[4]、本周已完成[5]、本周已延期[6]<br>
	 * 			本月进行中[7]、本月已完成[8]、本月已延期[9]<br>
	 * 			任意时间段进行中[10]、任意时间段已完成[11]、任意时间段已延期[12]<br>
	 * 			已取消[13]<br>
	 */
	public Map<Long, int[]> getStatisticInfo(List<Long> membersList, Date beginDate, Date endDate) {
		if(CollectionUtils.isEmpty(membersList))
			return null;
		
		Map<Long, int[]> notStartedAndCanceled = this.getStatisticResult(StatisticPeriod.All, membersList, beginDate, endDate);
		Map<Long, int[]> day = this.getStatisticResult(StatisticPeriod.Day, membersList, null, null);
		Map<Long, int[]> week = this.getStatisticResult(StatisticPeriod.Week, membersList, null, null);
		Map<Long, int[]> month = this.getStatisticResult(StatisticPeriod.Month, membersList, null, null);
		Map<Long, int[]> customPeriod = this.getStatisticResult(StatisticPeriod.Custom, membersList, beginDate, endDate);
		
		Map<Long, int[]> result = new HashMap<Long, int[]>();
		for(Long memberId : membersList) {
			int[] count = new int[14];
			// 未开始、本日/本周/本月/自定义时间段(进行中、已完成、已延迟)、已取消
			int[] notstart_cancel_count = notStartedAndCanceled.get(memberId);
			if(notstart_cancel_count != null) {
				count[0] = notstart_cancel_count[0];
				count[13] = notstart_cancel_count[1];
			}
			int[] empty = {0, 0, 0};
			System.arraycopy(day.get(memberId) == null ? empty : day.get(memberId), 0, count, 1, 3);
			System.arraycopy(week.get(memberId) == null ? empty : week.get(memberId), 0, count, 4, 3);
			System.arraycopy(month.get(memberId) == null ? empty : month.get(memberId), 0, count, 7, 3);
			System.arraycopy(customPeriod.get(memberId) == null ? empty : customPeriod.get(memberId), 0, count, 10, 3);
			
			result.put(memberId, count);
		}
		return result;
	}
	
	/**
	 * 获取<b>某种</b>统计时间段类型下的工作任务统计结果
	 * @param sp				统计时间段类型
	 * @param membersList		所要进行统计的人员ID集合
	 * @param beginDate			<b>自定义</b>统计开始日期
	 * @param endDate			<b>自定义</b>统计结束日期
	 * @see #getStatisticInfo(List, Date, Date)
	 */
	@SuppressWarnings("unchecked")
	public Map<Long, int[]> getStatisticResult(StatisticPeriod sp, List<Long> memberIds, Date beginDate, Date endDate) {
		Map<String, Object> params = FormBizConfigUtils.newHashMap("memberIds", memberIds);
		StringBuilder hql = new StringBuilder();
		hql.append(" select r.roleId, t.status, count(distinct t.id) from " + TaskInfo.class.getCanonicalName() + " as t, " + 
				   TaskRole.class.getCanonicalName() + " as r where t.id=r.taskId ");
		if(sp == StatisticPeriod.All) {
			hql.append(" and (t.status=" + TaskStatus.NotStarted.key() + " or t.status=" + TaskStatus.Canceled.key() + ") ");
		}
		else {
			hql.append(" and t.status>" + TaskStatus.NotStarted.key() + " and t.status<" + TaskStatus.Canceled.key());
			if(sp != StatisticPeriod.Custom) {
				beginDate = TaskUtils.getBeginDate(sp);
				endDate = TaskUtils.getEndDate(sp);
			}
			
			if(beginDate != null && endDate != null) {
				hql.append(" and ((t.plannedStartTime >= :beginDate and t.plannedStartTime <= :endDate) or " +
						   " (t.plannedEndTime >= :beginDate and t.plannedEndTime <= :endDate)) ");
			}
			else if(beginDate != null) {
				hql.append(" and (t.plannedStartTime >= :beginDate or t.plannedEndTime >= :beginDate) ");
			}
			else if(endDate != null) {
				hql.append(" and (t.plannedStartTime <= :endDate or t.plannedEndTime <= :endDate) ");
			}
			
			if(beginDate != null) {
				params.put("beginDate", beginDate);
			}
			if(endDate != null) {
				params.put("endDate", endDate);
			}
		}
		
		hql.append(" and r.roleId in (:memberIds) group by t.status, r.roleId ");
		List<Object[]> arrList = this.find(hql.toString(), -1, -1, params);
		return parse2StatisticMap(sp, arrList);
	}
	
	/**
	 * 将数组集合解析为所需的统计数据MAP
	 * @param sp		统计时段类型
	 * @param arrList	List&lt;Object[]&gt; [0]:memberId, [1]:status, [2]:count(status)
	 * @return
	 */
	private Map<Long, int[]> parse2StatisticMap(StatisticPeriod sp, List<Object[]> arrList) {
		Map<Long, int[]> result = new HashMap<Long, int[]>();
		if(CollectionUtils.isNotEmpty(arrList)) {
			// select r.roleId, t.status, count(distinct t.id)
			for(Object[] arr : arrList) {
				Long memberId = (Long)arr[0];
				if(result.get(memberId) == null) {
					result.put(memberId, new int[sp.statusTotal()]);
				}
				
				TaskStatus status = TaskStatus.valueOf((Integer)arr[1]);
				switch(status) {
				case NotStarted : 
					result.get(memberId)[0] = (Integer)arr[2];
					break;
				case Canceled :
					result.get(memberId)[1] = (Integer)arr[2];
					break;
				case Marching : 
					result.get(memberId)[0] = (Integer)arr[2];
					break;
				case Finished : 
					result.get(memberId)[1] = (Integer)arr[2];
					break;
				case Delayed : 
					result.get(memberId)[2] = (Integer)arr[2];
					break;
				}
			}
		}
		return result;
	}
	
	public Object[] getProjectSumStatisticResult(final Long projectId, final Long projectPhaseId, 
			final int status, final List<Long> memberIds, final Date beginDate, final Date endDate) {
		List<Object[]> arrList = this.getObjArrList(true, projectId, projectPhaseId, status, memberIds, beginDate, endDate);
		Object[] sumResult = this.getEmptyStatArr(status);
		if(CollectionUtils.isNotEmpty(arrList)) {
			for(Object[] arr : arrList) {
				this.parse(status, arr, sumResult, true);
			}
		}
		
		return sumResult;
	}
	
	/**
	 * 获取项目任务统计原始结果集，区分<b>按人分组</b>和<b>合计</b>两种模式
	 * @param isSum		是否为<b>合计</b>模式(否则为<b>按人分组</b>统计模式)
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getObjArrList(final boolean isSum, final Long projectId, final Long projectPhaseId, 
			final int status, final List<Long> memberIds, final Date beginDate, final Date endDate) {
		return (List<Object[]>)this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				StringBuilder sql = new StringBuilder();
				// 汇总统计时不需要按照人员进行分组
				sql.append(" select " + (isSum ? "" : " r.role_id as roleId,") + 
						   "t.status as status, count(distinct t.id) as taskCount," +
						   " sum(distinct t.actual_task_time) as timeSum" +
						   " from task_info t, task_role r where t.id=r.task_id");
				sql.append(" and t.project_id = :projectId ");
				if(projectPhaseId != TaskConstants.PROJECT_PHASE_ALL) {
					sql.append(" and t.project_phase_id = :projectPhaseId ");
				}
				
				if(status != -1) {
					sql.append(" and t.status = :status ");
				}
				
				if(beginDate != null && endDate != null) {
					sql.append(" and ((t.planned_start_time >= :beginDate and t.planned_start_time <= :endDate) or " +
							   " (t.planned_end_time >= :beginDate and t.planned_end_time <= :endDate)) ");
				}
				else if(beginDate != null) {
					sql.append(" and t.planned_start_time >= :beginDate ");
				}
				else if(endDate != null) {
					sql.append(" and t.planned_end_time <= :endDate ");
				}
				
				sql.append(" and r.role_id in (:memberIds) group by t.status" + (isSum ? "" : ", r.role_id"));
				
				SQLQuery query = session.createSQLQuery(sql.toString());
				// 此处必须显式声明各列别名对应的数据类型，否则杀千刀的Hibernate会抛出一个"Column '' not found"的异常，让你云山雾绕...
				if(!isSum) {
					query.addScalar("roleId", Hibernate.LONG);
				}
	        	query.addScalar("status", Hibernate.INTEGER);
	        	query.addScalar("taskCount", Hibernate.INTEGER);
	        	query.addScalar("timeSum", Hibernate.FLOAT);
	        	
	        	query.setLong("projectId", projectId);
	        	if(projectPhaseId != TaskConstants.PROJECT_PHASE_ALL) {
	        		query.setLong("projectPhaseId", projectPhaseId);
	        	}
	        	if(status != -1)
	        		query.setInteger("status", status);
	        	if(beginDate != null)
	        		query.setDate("beginDate", beginDate);
	        	if(endDate != null)
	        		query.setDate("endDate", endDate);
	        	query.setParameterList("memberIds", memberIds);
	        	
	    		return query.list();	    		
			}
    	});
	}
	
	/**
	 * 获取项目任务统计结果，用于前端展现
	 * @param projectId			所属项目ID
	 * @param projectPhaseId	所属项目阶段ID
	 * @param status			指定的任务状态类型
	 * @param memberIds			所进行统计所依据的人员ID列表
	 * @param beginDate			统计开始日期
	 * @param endDate			统计结束日期
	 * @return 项目任务统计结果：key - memberId, value - [指定状态：0-任务总数, 1-实际耗时总和] or [状态依次枚举：任务总数, 实际耗时总和]
	 */
	public Map<Long, Object[]> getProjectStatisticResult(final Long projectId, final Long projectPhaseId, 
			final int status, final List<Long> memberIds, final Date beginDate, final Date endDate) {
		List<Object[]> arrList = this.getObjArrList(false, projectId, projectPhaseId, status, memberIds, beginDate, endDate);
		return parse2ProjectStatisticMap(memberIds, status, arrList);
	}
	
	/**
	 * 将项目任务统计结果解析为前端展现所需的数据结果集
	 * @param memberIds			所进行统计所依据的人员ID列表
	 * @param status			指定的任务状态类型
	 * @param arrList			统计结果
	 * @return 项目任务统计结果：key - memberId, value - [指定状态：0-任务总数, 1-实际耗时总和] or [状态依次枚举：任务总数, 实际耗时总和]
	 */
	private Map<Long, Object[]> parse2ProjectStatisticMap(List<Long> memberIds, int status, List<Object[]> arrList) {
		Map<Long, Object[]> result = new HashMap<Long, Object[]>();
		if(CollectionUtils.isNotEmpty(arrList)) {
			// r.roleId, t.status, count(distinct t.id), sum(distinct t.actualTaskTime)
			for(Object[] arr : arrList) {
				Long memberId = (Long)arr[0];
				
				Object[] obj = result.get(memberId);
				if(obj == null) {
					obj = this.getEmptyStatArr(status);
					result.put(memberId, obj);
				}	
				
				this.parse(status, arr, obj, false);
			}
		}
		
		for(Long memberId : memberIds) {
			if(result.get(memberId) == null) {
				result.put(memberId, this.getEmptyStatArr(status));
			}
		}
		return result;
	}

	/**
	 * 将sql查询所得结果解析到最终统计展现的结果集数组中
	 * @param status	状态
	 * @param arr		[人员ID，状态，任务总数，耗时总和]
	 * @param ret		用于前端展现的结果集，包含2个或10个统计结果的数组
	 * @param isSum		是否为求和模式
	 */
	private void parse(int status, Object[] arr, Object[] ret, boolean isSum) {
		// r.roleId, t.status, count(distinct t.id), sum(distinct t.actualTaskTime) -> 各列
		// t.status, count(distinct t.id), sum(distinct t.actualTaskTime) -> 合计
		int index = isSum ? 0 : 1;
		if(status == -1) {
			TaskStatus statusEnum = TaskStatus.valueOf((Integer)arr[index]);
			switch(statusEnum) {
			case NotStarted : 
				ret[0] = (Integer)arr[index + 1];
				ret[1] = (Float)arr[index + 2];
				break;
			case Marching : 
				ret[2] = (Integer)arr[index + 1];
				ret[3] = (Float)arr[index + 2];
				break;
			case Finished : 
				ret[4] = (Integer)arr[index + 1];
				ret[5] = (Float)arr[index + 2];
				break;
			case Delayed : 
				ret[6] = (Integer)arr[index + 1];
				ret[7] = (Float)arr[index + 2];
				break;
			case Canceled :
				ret[8] = (Integer)arr[index + 1];
				ret[9] = (Float)arr[index + 2];
				break;
			}
		}
		else {
			ret[0] = (Integer)arr[index + 1];
			ret[1] = (Float)arr[index + 2];
		}
	}
	
	private static Integer emptyCount = 0;
	private static Float emptyTime = 0.0f;
	
	/**
	 * 对无有效统计结果的用户，设置一个空的统计结果数组用作展现
	 * @param status	指定状态：某一种状态   or 全部状态
	 */
	private Object[] getEmptyStatArr(int status) {
		if(status == -1)
			return new Object[]{emptyCount, emptyTime, emptyCount, emptyTime, emptyCount, emptyTime, emptyCount, emptyTime, emptyCount, emptyTime};
		return new Object[]{emptyCount, emptyTime};
	}

}
