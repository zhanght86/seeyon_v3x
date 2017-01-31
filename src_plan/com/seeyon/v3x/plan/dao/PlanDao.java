package com.seeyon.v3x.plan.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.plan.PlanRelevantUserType;
import com.seeyon.v3x.plan.PlanStatus;
import com.seeyon.v3x.plan.PublishStatus;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.domain.PlanRelevantUser;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * Data access object (DAO) for domain model class Plan.
 * 
 * @see com.seeyon.v3x.plan.domain.Plan
 * @author MyEclipse - Hibernate Tools
 */
public class PlanDao extends BaseHibernateDao<Plan> {
	
	private transient static final Log LOG = LogFactory
	.getLog(PlanDao.class);

	public List list() {
		return (List) getHibernateTemplate().find("from Plan");
	}

	@SuppressWarnings("deprecation")
	public void delete(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			delete(ids[i]);
		}
	}

	public Plan findByPrimaryKey(Long id) {
		return (Plan) getHibernateTemplate().get(Plan.class, new Long(id));
	}

	public List findByUserAndTypeAndTimeForPage(final Long userId,
			final String planType, final Date startTime1,
			final Date startTime2, final boolean isDraftsman) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer countHql = new StringBuffer();
				countHql.append("select count(distinct p)");
				countHql.append(" from Plan p , PlanRelevantUser pru");
				countHql.append(" where p.id = pru.plan.id");
				countHql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					countHql.append(" and pru.type = :pruType");
//					9-14----xut----加入集团化过滤
					countHql.append(" and p.refAccountId = :accountId");
				} else {
					countHql.append(" and pru.type != :pruType");
					countHql.append(" and p.publishStatus != :status");
				}
				countHql.append(" and p.type = :planType ");
				countHql.append(" and p.startTime between :startTime1 and :startTime2");
				Query countQuery = session.createQuery(countHql.toString());
				countQuery.setLong("userId", userId);
				countQuery.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				countQuery.setTimestamp("startTime1", startTime1);
				countQuery.setTimestamp("startTime2", startTime2);
				countQuery.setString("planType", planType);
				if (!isDraftsman) {
					countQuery.setString("status", PublishStatus.DRAFT.getValue());
				}else{
					countQuery.setLong("accountId", CurrentUser.get().getLoginAccount());
				}
		        int count = ((Integer) countQuery.uniqueResult()).intValue();
		        Pagination.setRowCount(count);
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					hql.append(" and pru.type = :pruType");
					hql.append(" and p.refAccountId = :accountId");
				} else {
					hql.append(" and pru.type != :pruType");
					hql.append(" and p.publishStatus != :status");
				}
				hql.append(" and p.startTime between :startTime1 and :startTime2");
				hql.append(" and p.type = :planType ");
				hql.append(" order by p.startTime desc, p.createTime desc");
				
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				query.setString("planType", planType);
				if (!isDraftsman) {
					query.setString("status", PublishStatus.DRAFT.getValue());
				}else{
					query.setLong("accountId", CurrentUser.get().getLoginAccount());
				}
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		});
	}
	
	/**
	 * 获取用户创建的计划列表，支持分页。不进行单位匹配，也即：用户A在兼职单位发出的计划，在原单位办公时同样可以看见
	 * @param userId     当前用户ID
	 * @param planType   计划类型：日/周/月/任意期
	 * @param startTime1 计划开始时间
	 * @param startTime2 计划结束时间
	 */
    public List findByUserAndTypeAndTimeForPage(final Long userId, final String planType, 
    		final Date startTime1, final Date startTime2) {
    	StringBuffer hql = new StringBuffer("from " + Plan.class.getName() + " as p where" +
    			" p.createUserId = :userId and p.type = :planType ");
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("userId", userId);
        params.put("planType", planType);
    	
        if(Strings.isNotBlank(planType)){
        	if(Integer.valueOf(planType)!=4){
        		hql.append(" and p.startTime <= :startTime2 ");
        	} else {
        		hql.append(" and p.startTime < :startTime2 ");
        	}
        	params.put("startTime2", startTime2);
        }
        hql.append(" and p.endTime >= :startTime1 ");
        params.put("startTime1", startTime1);

        hql.append(" order by p.startTime desc");
        return this.find(hql.toString(), params);   
    }

	public List findByUserAndTypeAndTime(final Long userId,
			final Date startTime1, final Date startTime2,
			final boolean isDraftsman) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					hql.append(" and pru.type = :pruType");
				} else {
					hql.append(" and pru.type != :pruType");
					hql.append(" and p.publishStatus != :status");
				}
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime < :startTime2");
				hql.append(" and p.refAccountId = :accountId");

				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				query.setLong("accountId", CurrentUser.get().getLoginAccount());
				if (!isDraftsman) {
					query.setString("status", PublishStatus.DRAFT.getValue());
				}
				return query.list();
			}
		});
	}
	
	
	public List findByCreateUserAndTime(final Long userId,
			final Date startTime1, final Date startTime2 ) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p ");
				hql.append(" where p.createUserId = :userId");
				hql.append(" and p.refAccountId = :accountId");
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime < :startTime2");
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setLong("accountId", CurrentUser.get().getLoginAccount());
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				return query.list();
			}
		});
	}
	
	/**
	 * 根据条件查询计划集合
	 * 
	 * @param userId
	 *            用户id
	 * @param planType
	 *            计划类型
	 * @param startTime1
	 *            开始时间
	 * @param startTime2
	 *            结束时间
	 * @param isDraftsman
	 *            是否为计划的发起人（true为是）
	 * @return
	 */
	public List findByUserAndTypeAndTime(final Long userId,
			final String planType, final Date startTime1,
			final Date startTime2, final boolean isDraftsman) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					hql.append(" and pru.type = :pruType");
				} else {
					hql.append(" and pru.type != :pruType");
					hql.append(" and p.publishStatus != :status");
				}

				hql.append(" and p.type = :planType ");
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime < :startTime2");

				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setString("planType", planType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				if (!isDraftsman) {
					query.setString("status", PublishStatus.DRAFT.getValue());
				}
				return query.list();
			}
		});
	}
	/**
	 * 首页计划管理
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @param isDraftsman
	 * @return
	 */
	public List findSectionByUserAndTypeAndTime(Long userId,String[] preType, String... planTypes) {
		Map<String,Object> parameter = new HashMap<String,Object>();
		
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct p");
		hql.append(" from Plan p , PlanRelevantUser pru");
		hql.append(" where p.id = pru.plan.id");
		hql.append(" and pru.refUserId = :userId ");
		hql.append(" and pru.type in (:pruType)");
		hql.append(" and p.publishStatus != :status");
		hql.append(" and p.type in(:planType) ");
		hql.append("and (");
		java.util.Calendar calendar = new GregorianCalendar();
		for(int i = 0 ; i < planTypes.length ; i ++){
			if(i != 0) {
				hql.append(" or ");
			}
			Integer p = Integer.parseInt(planTypes[i]);
			switch(p){
			case 1://日计划
				hql.append(" (p.startTime >= :startTime1 and p.startTime < :startTime2) ");
				parameter.put("startTime1", Datetimes.getFirstDayInWeek(calendar.getTime()));
				parameter.put("startTime2", Datetimes.getLastDayInWeek(calendar.getTime()));
				break;
			case 2://周计划
				hql.append(" (p.startTime >=:startTime3 and p.startTime < :startTime4) ");
				parameter.put("startTime3", Datetimes.getFirstDayInMonth(calendar.getTime()));
				parameter.put("startTime4", Datetimes.getLastDayInMonth(calendar.getTime()));
				break;
			case 3://月计划
				hql.append(" (p.startTime >=:startTime5 and p.startTime < :startTime6) ");
				parameter.put("startTime5", Datetimes.getFirstDayInSeason(calendar.getTime()));
				parameter.put("startTime6", Datetimes.getLastDayInSeason(calendar.getTime()));
			}
		}
		hql.append(") order by p.startTime desc");
		parameter.put("userId", userId);
		parameter.put("pruType", preType);
		parameter.put("status",  PublishStatus.DRAFT.getValue());
		parameter.put("planType", planTypes);
		
		return super.find(hql.toString(), parameter);
	}
	
	/**
	 * 根据用户、用户类型和时间得到计划统计
	 * 
	 * @param userId
	 *            用户id
	 * @param userType
	 *            用户类型
	 * @param startTime1
	 *            开始时间
	 * @param startTime2
	 *            结束时间
	 * @return
	 */
	public List findByUserAndUserTypeAndTime(final Long userId,
			final String userType, final Date startTime1, final Date startTime2) {

		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sbHql = new StringBuffer();
				sbHql
						.append("select distinct new com.seeyon.v3x.plan.domain.PlanCount"); //
				sbHql.append("  (");//
				sbHql.append("     plan.id ");//
				sbHql.append("    ,plan.type "); //
				sbHql.append("    ,plan.status "); //
				sbHql
						.append("    ,(select count(*) from PlanSummary as planSummary where planSummary.plan.id = plan.id )");//
				sbHql
						.append("    ,(select count(*) from PlanReply as planReply where planReply.plan.id = plan.id )");//
				sbHql.append("  )");//
				sbHql.append(" from Plan plan , PlanRelevantUser pru");//
				sbHql.append(" where plan.id = pru.plan.id");
				sbHql.append(" and pru.refUserId = :userId ");
				sbHql.append(" and pru.type = :pruType");
				sbHql.append(" and plan.startTime >= :startTime1 ");
				sbHql.append(" and plan.startTime < :startTime2");
				Query query = session.createQuery(sbHql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", userType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				return query.list();
			}
		});

	}

	/**
	 * 根据用户列表、用户类型和时间得到计划
	 * 
	 * @param userIdList
	 *            用户id集合
	 * @param userType
	 *            用户类型
	 * @param startTime1
	 *            开始时间
	 * @param startTime2
	 *            结束时间
	 * @return
	 */
	public List findByUserAndUserTypeAndTime(final List userIdList,
			final String userType, final Date startTime1, final Date startTime2) {

		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				if (userIdList == null || userIdList.isEmpty()
						|| userIdList.size() < 1) {
					return new ArrayList();
				}
				System.out.println(userIdList);
				StringBuffer sbHql = new StringBuffer();
				sbHql
						.append("select distinct new com.seeyon.v3x.plan.domain.PlanCount"); //
				sbHql.append("  (");//
				sbHql.append("     plan.id ");//
				sbHql.append("    ,plan.type "); //
				sbHql.append("    ,plan.status "); //
				sbHql
						.append("    ,(select count(*) from PlanSummary as planSummary where planSummary.plan.id = plan.id )");//
				sbHql
						.append("    ,(select count(*) from PlanReply as planReply where planReply.plan.id = plan.id )");//
				sbHql
						.append("    ,(select pruDraftsMan.refUserId from PlanRelevantUser pruDraftsMan where pruDraftsMan.plan.id = plan.id and pruDraftsMan.type = '");
				sbHql.append(PlanRelevantUserType.DRAFTSMAN.getValue());
				sbHql.append("'     )"); //
				sbHql
						.append("    ,(select pruDraftsMan.refUserName from PlanRelevantUser pruDraftsMan where pruDraftsMan.plan.id = plan.id and pruDraftsMan.type = '");
				sbHql.append(PlanRelevantUserType.DRAFTSMAN.getValue());
				sbHql.append("'     )"); //
				sbHql.append("  )");//
				sbHql.append(" from Plan plan , PlanRelevantUser pru");//
				sbHql.append(" where");
				sbHql.append(" and plan.id = pru.plan.id");
				sbHql.append(" and pru.refUserId in (:userId) ");
				sbHql.append(" and pru.type = :pruType");
				sbHql.append(" and plan.startTime >= :startTime1 ");
				sbHql.append(" and plan.startTime < :startTime2");
				Query query = session.createQuery(sbHql.toString());
				query.setParameterList("userId", userIdList);
				query.setString("pruType", userType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				return query.list();
			}
		});
	}
	
	/**
	 * 根据用户、用户类型得到计划统计
	 * 
	 * @param userId
	 *            用户id
	 * @param userType
	 *            用户类型
	 * @return
	 */
	public List findByUserAndType(final Long userId,
			final String userType) {

		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sbHql = new StringBuffer();
				sbHql.append("select distinct new com.seeyon.v3x.plan.domain.PlanCount"); //
				sbHql.append("  (");//
				sbHql.append("     plan.id ");//
				sbHql.append("    ,plan.type "); //
				sbHql.append("    ,plan.status "); //
				sbHql
						.append("    ,(select count(*) from PlanSummary as planSummary where planSummary.plan.id = plan.id )");//
				sbHql
						.append("    ,(select count(*) from PlanReply as planReply where planReply.plan.id = plan.id )");//
				sbHql.append("  )");//
				sbHql.append(" from Plan plan , PlanRelevantUser pru");//
				sbHql.append(" where plan.id = pru.plan.id");
				sbHql.append(" and pru.refUserId = :userId ");
				sbHql.append(" and pru.type = :pruType");
				Query query = session.createQuery(sbHql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", userType);
				return query.list();
			}
		});

	}
	
	/**
	 * 主送领导根据计划发起人和授权用户列表得出PlanCount List
	 * added by Paul at 5/15/07
	 * @param userId 主送ID
	 * @param userType userType
	 * @param userScopeList 授权用户列
	 * @param userScopeType PlanRelevantUserType.DRAFTSMAN
	 * @param startTime1 开始日期(统计月的第一天)
	 * @param startTime2 结束日期（下个月的第一条）
	 * @return PlanCount List
	 */
	public List findPlanCountsByToLeaderAndUserScopeList(final Long userId, final String userType, final List userScopeList,
			final String userScopeType, final Date startTime1, final Date startTime2) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
		if (null == userId) {
			return new ArrayList();
		}
		StringBuffer sbHql = new StringBuffer();
		sbHql
				.append("select distinct new com.seeyon.v3x.plan.domain.PlanCount"); //
		sbHql.append("  (");//
		sbHql.append("     plan.id ");//
		sbHql.append("    ,plan.type "); //
		sbHql.append("    ,plan.status "); //
		sbHql
				.append("    ,(select count(*) from PlanSummary as planSummary where planSummary.plan.id = plan.id )");//
		sbHql
				.append("    ,(select count(*) from PlanReply as planReply where planReply.plan.id = plan.id )");//
		sbHql
				.append("    ,(select pruDraftsMan.refUserId from PlanRelevantUser pruDraftsMan where pruDraftsMan.plan.id = plan.id and pruDraftsMan.type = :userScopeType)");
		sbHql
				.append("    ,(select pruDraftsMan.refUserName from PlanRelevantUser pruDraftsMan where pruDraftsMan.plan.id = plan.id and pruDraftsMan.type = :userScopeType)");
		sbHql.append("  )");//
		sbHql.append(" from Plan plan , PlanRelevantUser pru");//
		sbHql.append(" where plan.id in (");
		sbHql.append("   select distinct plan2.id"); //
		sbHql.append("    from Plan plan2 , PlanRelevantUser pru2");//
		sbHql.append("    where plan2.id = pru2.plan.id");
		sbHql.append("    and (");
		sbHql.append("     (pru2.refUserId = :userId and pru2.type = :userType)");
		if (null !=userScopeList && !userScopeList.isEmpty())
			sbHql.append("     or (pru2.refUserId in (:userScopeList) and pru2.type = :userScopeType)");
		sbHql.append("     )");
		sbHql.append("     and plan2.startTime >= :startTime1 ");
		sbHql.append("     and plan2.startTime < :startTime2");
		sbHql.append("  )");
		sbHql.append(" and plan.id = pru.plan.id");
		Query query = session.createQuery(sbHql.toString());
		query.setLong("userId", userId);
		if (null !=userScopeList && !userScopeList.isEmpty())
			query.setParameterList("userScopeList", userScopeList);
		query.setString("userType", userType);
		query.setString("userScopeType", userScopeType);
		query.setTimestamp("startTime1", startTime1);
		query.setTimestamp("startTime2", startTime2);
		return query.list();
	}
});
	}
	
	/**
	 * 根据用户Id和计划类型查出开始日期最晚的计划
	 * added by paul 5/15/07
	 * @param userId
	 * @param planType
	 * @return
	 */
	public List findPersonalLastPlan(final Long userId, final String planType) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				if (null == userId) {
					return new ArrayList();
				}
				StringBuffer sbHql = new StringBuffer();
				sbHql.append("from Plan as plan");
//				sbHql.append(" left join fetch plan.planDraftsmanUser");
//				sbHql.append(" left join fetch plan.planToLeaderUser");
//				sbHql.append(" left join fetch plan.planCcLeaderUser");
//				sbHql.append(" left join fetch plan.planApprizeUser");
				sbHql.append(" where (plan.id, plan.planBody.createDate) in (");
				//因为Plan中没有创建时间，所以只能从planBody中取得
				sbHql.append(" select pb.plan.id, max(pb.createDate) from PlanBody pb");
				sbHql.append(" where pb.plan.planDraftsmanUser.refUserId = :userId");
				sbHql.append(" and pb.plan.type = :planType");
				sbHql.append(" group by pb.plan");
				sbHql.append(" )");
				Query query = session.createQuery(sbHql.toString());
				query.setLong("userId", userId);
				query.setString("planType", planType);
				return query.list();
			}
		});		
	}
	
	
	/**
	 * 根据用户Id和计划类型查出开始日期最晚的计划
	 * added by xut 12/11/07
	 * @param userId
	 * @param planType
	 * @return
	 */
	public List findPersonalLastDatePlan(final Long userId, final String planType) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				if (null == userId) {
					return new ArrayList();
				}
				StringBuffer sbHql = new StringBuffer();
				sbHql.append(" from Plan plan ");
				sbHql.append(" where plan.createUserId= :createUID");
				sbHql.append(" and plan.type= :planType order by plan.createTime desc");
				Query query = session.createQuery(sbHql.toString());
				query.setLong("createUID", userId);
				query.setString("planType", planType);
				return query.list();
			}
		});		
	}
	
	/**
	 *  
     * @param planId
     * @param userId
     * @param userType
     * @return 0:主管领导 1:抄送人员 2:告知人员 3:计划发起人
	 */
	public int[] countPlanUser(Long planId, Long userId) {
	    int[] result = {0, 0, 0, 0};
        Map<String, Object> namedParameterMap = new HashMap<String, Object>();
        StringBuffer sbHql = new StringBuffer();
        sbHql.append("select count(distinct pru.id),pru.type from ").append(PlanRelevantUser.class.getName()).append(" pru");
        sbHql.append(" where pru.refUserId=:userId and pru.plan.id=:planId group by pru.type");
        namedParameterMap.put("userId", userId);
        namedParameterMap.put("planId", planId);
        List results = find(sbHql.toString(), -1, -1, namedParameterMap);
        if(results != null){
            for (int i = 0; i < results.size(); i++) {
                Object[] temp = (Object[]) results.get(i);
                int count = (Integer)temp[0];
                int type = Integer.parseInt((String.valueOf(temp[1])));
                if(count > 0){
                    result[type-1] = count;
                }
            }
        }
        return result;
	}

	/**
	 * 根据主送用户身份，得到起草计划用户列表
	 * 
	 * @param userId
	 *            用户id
	 * @param startTime1
	 *            时间段，开始时间
	 * @param startTime2
	 *            时间段，结束时间
	 * @return
	 */
	public List findDraftsmanUserIdListByToLeaderUserId(final Long userId,
			final Date startTime1, final Date startTime2) {

		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sbHql = new StringBuffer();

				sbHql.append("select distinct pru.refUserId ");
				sbHql.append(" from Plan plan , PlanRelevantUser pru ");
				sbHql.append(" where plan.id in (");
				sbHql.append("   select plan2.id"); //
				sbHql.append("    from Plan plan2 , PlanRelevantUser pru2");//
				sbHql.append("    where plan2.id = pru2.plan.id");
				sbHql.append("     and pru2.refUserId = :userId ");
				sbHql.append("     and pru2.type = :pruType2");
				sbHql.append("     and plan2.startTime >= :startTime1 ");
				sbHql.append("     and plan2.startTime < :startTime2");
				sbHql.append("  )");
				sbHql.append("  and plan.id = pru.plan.id");
				sbHql.append("  and pru.type = :pruType");
				Query query = session.createQuery(sbHql.toString());

				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setString("pruType2", PlanRelevantUserType.TO_LEADER
						.getValue());
				query.setDate("startTime1", startTime1);
//				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				List userIds = query.list();
				return userIds;
			}
		});
	}

	/**
	 * 根据条件查询计划集合
	 * 
	 * @param userId
	 *            用户id
	 * @param userType
	 *            用户类型
	 * @param planType
	 *            计划类型
	 * @param startTime1
	 *            开始时间
	 * @param startTime2
	 *            结束时间
	 * @return
	 */
	public List findByUserAndTypeAndTime(final Long userId,
			final String userType, final String planType,
			final Date startTime1, final Date startTime2) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				hql.append(" and pru.type = :pruType");
				hql.append(" and p.publishStatus != :status");

				hql.append(" and p.type = :planType ");
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime < :startTime2");
				
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", userType);
				query.setString("planType", planType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				// System.out.println();
				query.setString("status", PublishStatus.DRAFT.getValue());
				return query.list();
			}
		});
	}
	
	/**
	 * 根据条件查询计划集合
	 * 
	 * @param userId
	 *            用户id
	 * @param userType
	 *            用户类型
	 * @param planType
	 *            计划类型
	 * @param startTime1
	 *            开始时间
	 * @param startTime2
	 *            结束时间
	 * @return
	 */
	public List findByUserAndTypeAndTimeForPage(final Long userId,
			final String userType, final String planType,
			final Date startTime1, final Date startTime2) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer countHql = new StringBuffer();
				countHql.append("select count(distinct p)");
				countHql.append(" from Plan p , PlanRelevantUser pru");
				countHql.append(" where p.id = pru.plan.id");
				countHql.append(" and pru.refUserId = :userId ");
				countHql.append(" and pru.type = :pruType");
				countHql.append(" and p.publishStatus != :status");
				countHql.append(" and p.type = :planType ");
				countHql.append(" and p.startTime >= :startTime1 ");
				countHql.append(" and p.startTime <= :startTime2");
//				9-10----xut----集团化过滤
//				countHql.append(" and p.refAccountId = :accountId");
				Query countQuery = session.createQuery(countHql.toString());
				countQuery.setLong("userId", userId);
				countQuery.setString("pruType", userType);
				countQuery.setString("planType", planType);
				countQuery.setTimestamp("startTime1", startTime1);
				countQuery.setTimestamp("startTime2", startTime2);
				countQuery.setString("status", PublishStatus.DRAFT.getValue());
//				countQuery.setLong("accountId", CurrentUser.get().getLoginAccount());
		        int count = ((Integer) countQuery.uniqueResult()).intValue();
		        Pagination.setRowCount(count);
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p ");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				hql.append(" and pru.type = :pruType");
				hql.append(" and p.publishStatus != :status");
				hql.append(" and p.type = :planType ");
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime <= :startTime2");
				hql.append(" order by p.startTime desc, p.createTime desc");
//				hql.append(" and p.refAccountId = :accountId");
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", userType);
				query.setString("planType", planType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				query.setString("status", PublishStatus.DRAFT.getValue());
//				query.setLong("accountId", CurrentUser.get().getLoginAccount());
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		});
	}

	public List findByUserAndTypeAndTime(final Long userId,
			final List<Long> userIds, final String planType,
			final Date startTime1, final Date startTime2) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				/**
				 * 做分页，之前没做
				 */
				StringBuffer countHql = new StringBuffer();
				countHql.append("select count(distinct plan) ");
				countHql.append(" from Plan plan , PlanRelevantUser pru ");
				countHql.append(" where plan.id in (");
				countHql.append(" select distinct p.id ");
				countHql.append(" from Plan p , PlanRelevantUser pr");
				countHql.append(" where p.id = pr.plan.id");
				if(userIds != null && userIds.size() != 0) {
					countHql.append(" and p.createUserId in (:userIds) ");
				}				
				countHql.append(" and pr.type != :pruType");
				countHql.append(" and p.publishStatus != :status");
				countHql.append(" and p.type = :planType ");
				countHql.append(" and p.startTime >= :startTime1 ");
				countHql.append(" and p.startTime < :startTime2");
				countHql.append("  )");
				countHql.append("  and plan.id = pru.plan.id");
				countHql.append("  and pru.type != :pruType");
				countHql.append("  and pru.refUserId = :userId");
				Query countQuery = session.createQuery(countHql.toString());
				if(userIds != null && userIds.size() != 0) {
					countQuery.setParameterList("userIds", userIds);
				}			
				countQuery.setString("pruType", PlanRelevantUserType.DRAFTSMAN.getValue());
				countQuery.setString("planType", planType);
				countQuery.setTimestamp("startTime1", startTime1);
				countQuery.setTimestamp("startTime2", startTime2);
				countQuery.setString("status", PublishStatus.DRAFT.getValue());
				countQuery.setLong("userId", userId);
				int count = ((Integer) countQuery.uniqueResult()).intValue();
		        Pagination.setRowCount(count);
				
				//先查询登录用户能看到的计划集合（包括时间、计划类型等参数），再根据要查询的用户集合进行过滤。
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct plan ");
				hql.append(" from Plan plan , PlanRelevantUser pru ");
				hql.append(" where plan.id in (");
				hql.append(" select distinct p.id ");
				hql.append(" from Plan p , PlanRelevantUser pr");
				hql.append(" where p.id = pr.plan.id");
				if(userIds != null && userIds.size() != 0) {
					hql.append(" and p.createUserId in (:userIds) ");
				}			
				hql.append(" and pr.type != :pruType");
				hql.append(" and p.publishStatus != :status");
				hql.append(" and p.type = :planType ");
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime < :startTime2");
				hql.append("  )");
				hql.append("  and plan.id = pru.plan.id");
				hql.append("  and pru.type != :pruType");
				hql.append("  and pru.refUserId = :userId");
				hql.append(" order by plan.startTime desc, plan.createTime desc");
				Query query = session.createQuery(hql.toString());
				if(userIds != null && userIds.size() != 0) {
					query.setParameterList("userIds", userIds);
				}			
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN.getValue());
				query.setString("planType", planType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				query.setString("status", PublishStatus.DRAFT.getValue());
				query.setLong("userId", userId);
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				return query.list();
			}
		});
	}
	
	/**
	 * 根据条件查询计划集合
	 * 
	 * @param userId
	 *            用户id
	 * @param planType
	 *            计划类型
	 * @param isDraftsman
	 *            是否为计划的发起人（true为是）
	 * @return
	 */
	public List findByUserAndType(final Long userId,final String planType, final boolean isDraftsman) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					hql.append(" and pru.type = :pruType");
				} else {
					hql.append(" and pru.type != :pruType");
					hql.append(" and p.publishStatus != :status");
				}

				hql.append(" and p.type = :planType ");

				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setString("planType", planType);
				if (!isDraftsman) {
					query.setString("status", PublishStatus.DRAFT.getValue());
				}
				return query.list();
			}
		});
	}
	
	/**
	 * 根据条件查询计划条数
	 * 
	 * @param userId
	 *            用户id
	 * @param planType
	 *            计划类型
	 * @return
	 */
	public Integer countByUserAndType(final Long userId,final String planType, final boolean isDraftsman) {
		return (Integer) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				StringBuffer hql = new StringBuffer();
				hql.append("select count(distinct p)");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					hql.append(" and pru.type = :pruType");
				} else {
					hql.append(" and pru.type != :pruType");
					hql.append(" and p.publishStatus != :status");
				}

				hql.append(" and p.type = :planType ");

				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setString("planType", planType);
				if (!isDraftsman) {
					query.setString("status", PublishStatus.DRAFT.getValue());
				}				
				return query.uniqueResult();
			}
		});
	}
	
	/**
	 * 根据条件查询当前页的计划集合
	 * 
	 * @param userId
	 *            用户id
	 * @param planType
	 *            计划类型
	 * @param isDraftsman
	 *            是否为计划的发起人（true为是）
	 * @return
	 */
	public Integer findByUserAndTypeForPageCount(final Long userId,final String planType, final boolean isDraftsman) {
		return (Integer)findByUserAndTypeFP(userId,planType,isDraftsman,true);
	}
	public List findByUserAndTypeForPage(final Long userId,final String planType, final boolean isDraftsman) {
		return (List)findByUserAndTypeFP(userId,planType,isDraftsman,false);
	}
	public Object findByUserAndTypeFP(final Long userId,final String planType, final boolean isDraftsman,final boolean isCount) {
		return  getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				StringBuffer hql = new StringBuffer();
				if(isCount){
					hql.append("select count(distinct p.id)");
				}else{
					hql.append("select distinct p ");
				}
				
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				if (isDraftsman) {
					hql.append(" and pru.type = :pruType");
				} else {
					hql.append(" and pru.type != :pruType");
					hql.append(" and p.publishStatus != :status");
				}

				hql.append(" and p.type = :planType ");
				if(!isCount){
					hql.append(" order by p.startTime desc ");
				}

				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN
						.getValue());
				query.setString("planType", planType);
				if (!isDraftsman) {
					query.setString("status", PublishStatus.DRAFT.getValue());
				}
				if(isCount){
					return query.uniqueResult();
				}else{
					return query.list();
				}
				
			}
		});
	}
	/**
	 * 根据条件查询计划集合
	 * 
	 * @param userId
	 *            用户id
	 * @param userType
	 *            用户类型
	 * @param planType
	 *            计划类型
	 * @return
	 */
	public List findByUserAndType(final Long userId,final String userType, final String planType) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				hql.append(" and pru.type = :pruType");
				hql.append(" and p.type = :planType ");

				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", userType);
				query.setString("planType", planType);
				return query.list();
			}
		});
	}
	
	
	
//~~~~~~~~~~~~~~~~首页部门计划显示最新几条显示~~~~~~~~~~~~~~~~~~~~~
	public List deptfindByUserAndTypeAndTimeForPage(int pageSize, Long departmentId,  Long userId, Date startTime1, boolean isDraftsman){
	            Session session = super.getSession();
	            List list = new ArrayList();
	            try{
					User user = CurrentUser.get();
					
//					不需要分页  2008-3-29   xut
//					StringBuffer countHql = new StringBuffer();
//					countHql.append("select count(distinct p)");
//					countHql.append(" from Plan p ");
//					
//					countHql.append(" where p.refDepartmentId = :refDepartmentId ");			
//					countHql.append(" and p.publishStatus > :status1 ");
//					countHql.append(" and p.publishStatus < :status2 ");
//					//countHql.append(" and p.startTime <= :startTime1 ");
//					countHql.append(" and p.refAccountId = :accountId");
//					countHql.append(" order by p.startTime desc ");
//					
//					Query countQuery = session.createQuery(countHql.toString());
//					countQuery.setLong("refDepartmentId", departmentId);
//					countQuery.setString("status1", PublishStatus.DRAFT.getValue());
//					countQuery.setString("status2", PlanStatus.FINISHED.getValue());
//					//countQuery.setTimestamp("startTime1", startTime1);
//					countQuery.setLong("accountId", user.getLoginAccount());
//					
//					
//					
//			        int count = ((Integer) countQuery.uniqueResult()).intValue();
//			        Pagination.setRowCount(count);
					StringBuffer hql = new StringBuffer();
					hql.append("select distinct p");
//					yangzd 计划相关人员： 1、主管领导 2、抄送人员 3、告知人员 4、计划发起人 可以看见计划。
					hql.append(" from Plan p , PlanRelevantUser pru");
					hql.append(" where p.id = pru.plan.id ");
					hql.append(" and pru.refUserId = :userId ");
					hql.append(" and p.refDepartmentId = :refDepartmentId ");				
					hql.append(" and p.publishStatus > :status1 ");
					hql.append(" and p.planStatus < :status2 ");			
					//hql.append(" and p.startTime <= :startTime1 ");
//					hql.append(" and p.refAccountId = :accountId");
					hql.append(" order by p.startTime desc ");
	
					Query query = session.createQuery(hql.toString());
					query.setLong("userId", userId);
					query.setLong("refDepartmentId", departmentId);	
					query.setString("status1", PublishStatus.DRAFT.getValue());
					query.setString("status2", PlanStatus.FINISHED.getValue());
					//query.setTimestamp("startTime1", startTime1);
                    //by Yongzhang 去掉单位现在 2008-09-20
//					query.setLong("accountId", user.getLoginAccount());
					
					query.setFirstResult(0);
					query.setMaxResults(pageSize);
				    list = query.list();
	            }catch(Exception ex){
	    			LOG.error("", ex);;
	    		}finally{
	    				super.releaseSession(session);
	    		}
				return list;
		}
//~~~~~~~~~~~~~~~~首页部门计划更多显示~~~~~~~~~~~~~~~~~~~~~
	public List deptMorePlanList( Long departmentId,  Long userId, Date startTime1, boolean isFromDeptSpaceManage){
		Session session = super.getSession();
		List list = new ArrayList();
		try{
			User user = CurrentUser.get();
			
			StringBuffer countHql = new StringBuffer();
			countHql.append("select count(distinct p)");
			//yangzd 计划相关人员： 1、主管领导 2、抄送人员 3、告知人员 4、计划发起人 可以看见计划。
			countHql.append(" from Plan p , PlanRelevantUser pru");
			countHql.append(" where p.id = pru.plan.id ");
			countHql.append(" and pru.refUserId = :userId ");
			countHql.append(" and p.refDepartmentId = :refDepartmentId ");
			if(!isFromDeptSpaceManage){
				countHql.append(" and p.publishStatus > :status1 ");				
			}
			countHql.append(" and p.publishStatus < :status2 ");
			//countHql.append(" and p.startTime <= :startTime1 ");
              //by Yongzhang 去掉单位限制 2008-09-20
//			countHql.append(" and p.refAccountId = :accountId");
//			countHql.append(" order by p.startTime desc ");
			
			Query countQuery = session.createQuery(countHql.toString());
			countQuery.setLong("userId", userId);
			countQuery.setLong("refDepartmentId", departmentId);
			if(!isFromDeptSpaceManage){
				countQuery.setString("status1", PublishStatus.DRAFT.getValue());				
			}
			countQuery.setString("status2", PlanStatus.FINISHED.getValue());
			//countQuery.setTimestamp("startTime1", startTime1);
            //by Yongzhang 去掉单位限制 2008-09-20
//			countQuery.setLong("accountId", user.getLoginAccount());
			
			
			
			int count = ((Integer) countQuery.uniqueResult()).intValue();
			Pagination.setRowCount(count);
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct p");
//			yangzd 计划相关人员： 1、主管领导 2、抄送人员 3、告知人员 4、计划发起人 可以看见计划。
			hql.append(" from Plan p , PlanRelevantUser pru");
			hql.append(" where p.id = pru.plan.id ");
			hql.append(" and pru.refUserId = :userId ");
			hql.append(" and p.refDepartmentId = :refDepartmentId ");
			if(!isFromDeptSpaceManage){
				hql.append(" and p.publishStatus > :status1 ");				
			}
			hql.append(" and p.publishStatus < :status2 ");
			//hql.append(" and p.startTime <= :startTime1 ");
//			hql.append(" and p.refAccountId = :accountId");
			hql.append(" order by p.startTime desc ");
			
			Query query = session.createQuery(hql.toString());
			query.setLong("userId", userId);
			query.setLong("refDepartmentId", departmentId);
			if(!isFromDeptSpaceManage){
				query.setString("status1", PublishStatus.DRAFT.getValue());				
			}
			query.setString("status2", PlanStatus.FINISHED.getValue());
			//query.setTimestamp("startTime1", startTime1);
//			query.setLong("accountId", user.getLoginAccount());
			
			query.setFirstResult(Pagination.getFirstResult());
			query.setMaxResults(Pagination.getMaxResults());
			list = query.list();
		}catch(Exception ex){
			LOG.error("", ex);;
		}finally{
				super.releaseSession(session);
		}
		return list;
	}
	public int cancelPublishPlan(final Set<Long> planId){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", planId);
        return this.bulkUpdate("UPDATE " + this.getEntityClass().getCanonicalName() + " SET refDepartmentId=NULL WHERE ID IN (:id)", param);
	}
	@SuppressWarnings("unchecked")
	public List<Plan> findByProjectId(final Long projectId, final Long phaseId) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select p from " + Plan.class.getName() + " as p, " + PlanRelevantUser.class.getName() + " as pr ");
		hql.append("where p.id=pr.plan.id and p.refProjectId =:projectId and pr.refUserId =:userId and p.publishStatus!=1 ");
		params.put("projectId", projectId);
		params.put("userId", CurrentUser.get().getId());
		if(phaseId != null && phaseId != 1){
			hql.append("and p.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.plan.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by p.createTime desc");
		return this.find(hql.toString(), -1, -1, params);
	}
	
	@SuppressWarnings("unchecked")
	public List<Plan> findByProjectCondition(String condition,final Long projectId, final Long phaseId,Map<String,Object> paramMap) {
		StringBuffer hql = new StringBuffer();System.out.println("123");
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("select p from " + Plan.class.getName() + " as p, " + PlanRelevantUser.class.getName() + " as pr ");
		hql.append("where p.id=pr.plan.id and p.refProjectId =:projectId and pr.refUserId =:userId and p.publishStatus!=1 ");
		params.put("projectId", projectId);
		params.put("userId", CurrentUser.get().getId());
		
		if ("title".equals(condition)) {
			hql.append("and p.title like :title ") ;
			params.put("title", "%" + paramMap.get("title") + "%") ;
		} else if ("author".equals(condition)) {
			hql.append("and p.createUserId in (:author) ") ;
			params.put("author", paramMap.get("author")) ;
		} else if ("newDate".equals(condition)) {
			hql.append("and p.createTime>=:begin and p.createTime<=:end ") ;
			params.put("begin", Datetimes.getTodayFirstTime(paramMap.get("newDate").toString())) ;
			params.put("end", Datetimes.getTodayLastTime(paramMap.get("newDate").toString())) ;
		}
		
		if(phaseId != null && phaseId != 1){
			hql.append("and p.id in (select ph.eventId from " + ProjectPhaseEvent.class.getName() + " as ph" +
					" where ph.phaseId=:phaseId and ph.eventType=" + ApplicationCategoryEnum.plan.key() + ") ");
			params.put("phaseId", phaseId);
		}
		hql.append("order by p.createTime desc");
		return this.find(hql.toString(), -1, -1, params);
	}
	
	public List<Plan> findByMemberIdAndSenderId(final Long MemberId, final Long SenderId) {
		return this.findByMemberIdAndSenderId(MemberId, SenderId, -1);
	}
	
	@SuppressWarnings("unchecked")
	public List<Plan> findByMemberIdAndSenderId(final Long MemberId, final Long SenderId, int size) {
		String hql = "select p from Plan p where p.createUserId =:createUserId "
			+ " and p.id in (select p.id from Plan p , PlanRelevantUser pru where p.id = pru.plan.id and pru.refUserId =:userId and pru.type!="
			+ PlanRelevantUserType.DRAFTSMAN.getValue() + " and p.publishStatus!='1') and p.refAccountId=:accountId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("createUserId", SenderId);
		params.put("userId", MemberId);
		params.put("accountId", CurrentUser.get().getLoginAccount());
		int start = -1;
		if(size > 0){
			start = 0;
		}
		return this.find(hql, start, size, params);
	}

    /**
	 * 综合查询
	 * @author jincm 2008-3-19
	 * @param cModel
     * @return List
	 */
	@SuppressWarnings("unchecked")
	public List<Plan> iSearch(final ConditionModel cModel) {
//		return (List) getHibernateTemplate().execute(new HibernateCallback() {
//			public Object doInHibernate(Session session)
//					throws HibernateException {	
		    	String title = cModel.getTitle();
				final java.util.Date beginDate = cModel.getBeginDate();
				final java.util.Date endDate = cModel.getEndDate();
				Long fromUserId = cModel.getFromUserId();	
				
				StringBuffer subHql = new StringBuffer();
				Map<String, Object> params = new HashMap<String, Object>();
				subHql.append(" select distinct p");
								
				if(fromUserId != null){					
					subHql.append(" from Plan p");
					subHql.append(" where ");
					subHql.append(" p.createUserId = :createUserId");
					subHql.append(" and p.publishStatus!= ").append(PublishStatus.DRAFT.getValue());
					params.put("createUserId", fromUserId);
				}else{					
					subHql.append(" from Plan p , PlanRelevantUser pru where p.id = pru.plan.id ");
					subHql.append(" and p.publishStatus!= ").append(PublishStatus.DRAFT.getValue());
					subHql.append(" and pru.refUserId =:userId and pru.type!=" ).append(PlanRelevantUserType.DRAFTSMAN.getValue());	
					params.put("userId", CurrentUser.get().getId());
				}
				if(Strings.isNotBlank(title)){
					subHql.append(" and p.title like :title ");
					params.put("title", "%" + SQLWildcardUtil.escape(title) + "%");
				}
				if(beginDate != null){
					subHql.append(" and p.startTime >= :startTime");
					params.put("startTime", beginDate);
				}
				if(endDate != null){
					subHql.append(" and p.startTime <= :endTime");
					params.put("endTime", endDate);
				}
				subHql.append(" order by p.createTime desc");
				return this.find(subHql.toString(), params); //分页
				//Query query = session.createQuery(subHql.toString());
//				if(Strings.isNotBlank(title)){
//					query.setString("title", "%"+title+"%");
//				}
//				if(beginDate != null){
//					query.setDate("startTime", beginDate);
//				}
//				if(endDate != null){
//					query.setDate("endTime", endDate);
//				}
//				if(fromUserId == null){
//					query.setLong("userId", CurrentUser.get().getId());
//				}else{
//					query.setLong("createUserId", fromUserId);
//				}
//				return query.list();
			//}
		//});
	}

	/**
	 * 根据时间段和用户查询主送给用户而且已经回复的计划
	 * 
	 * @param userId
	 *            用户id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public List<Plan> findCcPlanReplied(final Long userId, final Date startTime, final Date endTime){
		return (List<Plan>)findCcPR(userId,startTime,endTime,false);
	}
	
	public Integer findCcPlanRepliedCount(final Long userId, final Date startTime, final Date endTime){
		return (Integer)findCcPR(userId,startTime,endTime,true);
	}
	private Object findCcPR(final Long userId, final Date startTime, final Date endTime,final boolean isCount){
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer hql = new StringBuffer();
				if(isCount){
					hql.append("select count(distinct plan.id) ");
				}else{
					hql.append("select distinct plan ");
				}
				
				hql.append(" from Plan plan , PlanReply pry");
				hql.append(" where plan.id in (");
				hql.append(" select distinct p.id ");
				hql.append(" from Plan p , PlanRelevantUser pr");
				hql.append(" where p.id = pr.plan.id");
				hql.append(" and pr.refUserId = :userId ");
				hql.append(" and pr.type = :pruType");
				hql.append(" and p.publishStatus != :status");
				hql.append("  )");
				hql.append("  and plan.id = pry.plan.id");
				hql.append("  and pry.refUserId = :userId");
				if(startTime != null){
					hql.append(" and pry.createTime >= :startTime");
				}
				if(endTime != null){
					hql.append(" and pry.createTime < :endTime");
				}
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.TO_LEADER.getValue());
				if(startTime != null){
					query.setTimestamp("startTime", startTime);
				}
				if(endTime != null){
					query.setTimestamp("endTime", endTime);
				}
				query.setString("status", PublishStatus.DRAFT.getValue());
				if(isCount){
					return query.uniqueResult();
				}else{
					return query.list();
				}
				
			}
        });
	}
	/**
	 * 工作管理下方：需要实现分页
	 */
	public List<Plan> findCcPlanRepliedForPage(final Long userId, final Date startTime, final Date endTime){
		String hql = "select distinct plan from Plan plan , PlanReply pry where plan.id in (select distinct p.id from Plan p , PlanRelevantUser pr where p.id = pr.plan.id " + 
					 "and pr.refUserId = :userId and pr.type = :pruType and p.publishStatus != :status ) and plan.id = pry.plan.id and pry.refUserId = :userId";
		Map<String, Object> params = new HashMap<String, Object>();
		if(startTime != null){
			hql += " and pry.createTime >= :startTime ";
			params.put("startTime", startTime);
		}
		if(endTime != null){
			hql += " and pry.createTime < :endTime";
			params.put("endTime", endTime);
		}
		params.put("status", PublishStatus.DRAFT.getValue());
		params.put("userId", userId);
		params.put("pruType", PlanRelevantUserType.TO_LEADER.getValue());
		hql += " order by plan.createTime desc";
		return this.find(hql, params);
	}
	/*
	 * 
	 * 获得所有类型的计划
	 */
	public List findByUserAndTimeForAnyPlanType(final Long userId,
			final String userType,
			final Date startTime1, final Date startTime2) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer hql = new StringBuffer();
				hql.append("select distinct p");
				hql.append(" from Plan p , PlanRelevantUser pru");
				hql.append(" where p.id = pru.plan.id");
				hql.append(" and pru.refUserId = :userId ");
				hql.append(" and pru.type = :pruType");
				hql.append(" and p.publishStatus != :status");
				hql.append(" and p.startTime >= :startTime1 ");
				hql.append(" and p.startTime < :startTime2");
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", userType);
				query.setTimestamp("startTime1", startTime1);
				query.setTimestamp("startTime2", startTime2);
				query.setString("status", PublishStatus.DRAFT.getValue());
				return query.list();
			}
		});
	}	
	/**
	 * 根据时间段和用户查询用户已发送的计划
	 * 
	 * @param userId
	 *            用户id
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public List<Plan> findSendPlan(final Long userId, final Date startTime, final Date endTime){
		return (List<Plan>)findSP(userId,startTime,endTime,false);
	}
	public Integer findSendPlanCount(final Long userId, final Date startTime, final Date endTime){
		return (Integer)findSP(userId,startTime,endTime,true);
	}
	private Object findSP(final Long userId, final Date startTime, final Date endTime,final boolean isCount){
		return getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer hql = new StringBuffer();
				if(isCount){
					hql.append(" select count(distinct p.id) ");
				}else{
					hql.append(" select distinct p ");
				}
				hql.append(" from Plan p , PlanRelevantUser pr");
				hql.append(" where p.id = pr.plan.id");
				hql.append(" and pr.refUserId = :userId ");
				hql.append(" and pr.type = :pruType");
				hql.append(" and p.publishStatus != :status");
				if(startTime != null)
					hql.append(" and p.endTime >= :startTime");
				if(endTime != null)
					hql.append(" and p.startTime <= :endTime");
				Query query = session.createQuery(hql.toString());
				query.setLong("userId", userId);
				query.setString("pruType", PlanRelevantUserType.DRAFTSMAN.getValue());
				if(startTime != null)
					query.setTimestamp("startTime", startTime);
				if(endTime != null)
					query.setTimestamp("endTime", endTime);
				query.setString("status", PublishStatus.DRAFT.getValue());
				if(isCount){
					return query.uniqueResult();
				}else{
					return query.list();
				}
			}
        });
	}
	public List<Plan> findSendPlanForPage(final Long userId, final Date startTime, final Date endTime) {
		return this.findSendPlanForPage(userId, startTime, endTime, true);
	}

	@SuppressWarnings("unchecked")
	public List<Plan> findSendPlanForPage(final Long userId, final Date startTime, final Date endTime, final boolean isPaginate) {
		String hql = "select distinct p from Plan p, PlanRelevantUser pr where p.id = pr.plan.id and pr.refUserId = :userId and pr.type = :pruType and p.publishStatus != :status";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("pruType", PlanRelevantUserType.DRAFTSMAN.getValue());
		params.put("status", PublishStatus.DRAFT.getValue());
		
		if (startTime != null) {
			hql += " and p.endTime >= :startTime ";
			params.put("startTime", startTime);
		}
		if (endTime != null) {
			hql += " and p.startTime <= :endTime ";
			params.put("endTime", endTime);
		}
		
		hql += " order by p.createTime desc ";

		if (isPaginate) {
			return this.find(hql, params);
		} else {
			return this.find(hql, -1, -1, params);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Plan> findByUserAndTime(Long userId, Date date, boolean isMyPlan, String[] pruTypes) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct p from " + Plan.class.getName() + " p, " + PlanRelevantUser.class.getName() + " pru where p.id = pru.plan.id ");
		hql.append(" and pru.refUserId = :userId and p.startTime <= :date and p.endTime >= :date ");

		if (isMyPlan) {
			hql.append(" and pru.type = :pruType ");
			params.put("pruType", PlanRelevantUserType.DRAFTSMAN.getValue());
		} else {
			hql.append(" and pru.type in (:pruTypes) ");
			hql.append(" and p.publishStatus != :status ");
			params.put("pruTypes", pruTypes);
			params.put("status", PublishStatus.DRAFT.getValue());
		}

		hql.append(" order by p.startTime desc");
		params.put("userId", userId);
		params.put("date", date);

		return super.find(hql.toString(), -1, -1, params);
	}

}