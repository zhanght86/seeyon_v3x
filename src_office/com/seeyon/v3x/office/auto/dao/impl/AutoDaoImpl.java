package com.seeyon.v3x.office.auto.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.dao.support.page.Page;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.auto.dao.AutoDao;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoDepartInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.domain.AutoOffense;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.myapply.util.Constants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * 
 */
public class AutoDaoImpl extends BaseHibernateDao implements AutoDao {

	/**
	 * 根据车牌号从数据库取得车辆详细信息对象
	 * 
	 * @param autoId
	 *            车牌号
	 * @return 车辆详细信息持久对象
	 */
	public AutoInfo findAutoInfoById(String autoId) {

		return (AutoInfo) this.getHibernateTemplate().get(AutoInfo.class,
				autoId);
	}

	/**
	 * 
	 * @param autoInfo
	 * @return
	 */
	public void createAutoInfo(AutoInfo autoInfo) {
		super.save(autoInfo);
		// this.getHibernateTemplate().save(autoInfo);
	}

	/**
	 * 保存(新增/修改)车辆详细信息到数据库
	 * 
	 * @param autoInfo
	 *            车辆详细信息对象
	 */
	public void updateAutoInfo(AutoInfo autoInfo) {
		super.update(autoInfo);
		// this.getHibernateTemplate().update(autoInfo);
	}

	/**
	 * 根据查询条件取得车辆详细信息一览列表
	 * 
	 * @param fieldName
	 *            查询字段
	 * @param fieldValue
	 *            字段值
	 * @param pageNo
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @return 车辆详细信息一览列表
	 */
	public Page queryAutoInfoList(String fieldName, String fieldValue,
			int pageNo, int pageSize) throws Exception {

		Session session = this.getSession();

		Criteria criteria = session.createCriteria(AutoInfo.class);

		if (!"".equals(fieldName) && !"".equals(fieldValue)) {
			criteria.add(Expression.like(fieldName, SQLWildcardUtil.escape(fieldValue),
					MatchMode.ANYWHERE));
		}
		criteria.add(Expression.eq("deleteFlag", new Integer(0)));

		return this.pagedQuery(criteria, pageNo, pageSize);

	}

	/**
	 * 取得所有车辆状态==0的车辆信息
	 * 
	 * @return
	 */
	public List findAllNormalAuto(Long domainId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AutoInfo.class);
		criteria.add(Expression.not(Restrictions.eq("deleteFlag", 1)));
		criteria.add(Restrictions.eq("autoStatus", 0));
		criteria.add(Restrictions.eq("domainId", domainId));
		criteria.addOrder(Order.asc("autoId"));
		return super.executeCriteria(criteria,-1,-1);
		/*
		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Long domainId = CurrentUser.get().getLoginAccount();
						String sql = "select * from M_Auto_Info where del_flag<>1 and auto_status=0 and domain_id="
								+ domainId + " order by auto_id";
						SQLQuery query = session.createSQLQuery(sql);

						query.addEntity(AutoInfo.class);

						return query.list();
					}

				});
*/
	}


	/**
	 * //车辆登记---车辆列表查询
	 */
	public List queryAutoInfo(final String fieldName, final String fieldValue,
			final Map keyMap,Long autoMge) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AutoInfo.class);
		criteria.add(Restrictions.eq("autoManager", autoMge));
		if (!"".equals(fieldName) && !"".equals(fieldValue)) {
			if (fieldName.equals("autoStatus")) {
				criteria.add(Restrictions
						.eq(fieldName, new Integer(fieldValue)));
			} else {
				criteria.add(Restrictions.like(fieldName, "%" + SQLWildcardUtil.escape(fieldValue)
						+ "%"));
			}
		}
		if (keyMap != null && !keyMap.isEmpty()) {
			for (Iterator iterator = keyMap.keySet().iterator(); iterator
					.hasNext();) {
				String keyName = iterator.next().toString();
				criteria.add(Restrictions.eq(keyName, keyMap.get(keyName)));
			}
		}
		criteria.addOrder(Order.desc("createDate"));
		criteria.addOrder(Order.desc("autoId"));
		return super.executeCriteria(criteria);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAutoTypeInfo() {
		String sql = "";
		Session session = super.getSession();
		SQLQuery query = null;
		try {
			query = session.createSQLQuery(sql);
		} catch (Exception ex) {

		} finally {
			super.releaseSession(session);
		}
		return true;
	}

	/**
	 * //车量申请---车辆列表查询
	 */
	public List queryAutoInfoApply(final Long[] mgrIds, final String fieldName,
			final String fieldValue, final Map keyMap,Long domainId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AutoInfo.class);
		criteria.add(Restrictions.in("autoManager", mgrIds));
		criteria.add(Restrictions.eq("domainId",  CurrentUser.get().getLoginAccount()));
		//criteria.add(Restrictions.eq("domainId", domainId)); //对兼职人员开放
		if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
			if (fieldName.equals("autoState")) {
				criteria.add(Restrictions
						.eq(fieldName, new Integer(fieldValue)));
			} else {
				criteria.add(Restrictions.like(fieldName, "%" + SQLWildcardUtil.escape(fieldValue)
						+ "%"));
			}
		}
		if (keyMap != null && !keyMap.isEmpty()) {
			String key;
			for (Iterator iterator = keyMap.keySet().iterator(); iterator
					.hasNext();) {
				key = iterator.next().toString();
				criteria.add(Restrictions.eq(key, keyMap.get(key)));
			}
		}
		criteria.add(Restrictions.not(Expression.eq("deleteFlag", 1)));
		criteria.addOrder(Order.desc("createDate"));
		return super.executeCriteria(criteria);
	}

	/**
	 * 根据车牌号批量逻辑删除车辆记录
	 * 
	 * @param autoIds
	 *            车牌号集 '1000','1111111','222222'
	 * @return
	 */
	// 对车辆删除进行了限制，如果是曾经申请过的车辆，进行删除的时候，吧车辆的状态改为丢失报损，如果是没有使用过的车辆的话，那么把车辆直接删除
	public int deleteAutoInfoByIds( List<String> autoIds) {
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> updateMap = new HashMap<String, Object>();
		List<String> updateIds = new ArrayList<String>() ; 
		for(String carId : autoIds){
			String finhql = "from AutoApplyInfo autoApplyInfo where autoApplyInfo.autoId=?" ;
			List<AutoApplyInfo> applyList = super.find(finhql, carId) ;
			if(applyList.size() != 0 ){
				updateIds.add(carId) ;
			}
		}
		autoIds.removeAll(updateIds) ;
		String sql = "update AutoInfo auto set auto.autoStatus=? where auto.autoId in (:autoId) ";
		String deleteSql = "delete from  AutoInfo auto  where auto.autoId in (:autoId)";
		map.put("autoId", autoIds);
		updateMap.put("autoId", updateIds) ;
		if(updateIds.size() == 0){
			return super.bulkUpdate(deleteSql, map);
		}else if(updateIds.size() != 0 && autoIds.size() != 0){
			return super.bulkUpdate(sql, updateMap, 3) + super.bulkUpdate(deleteSql, map);
		}else{
			return super.bulkUpdate(sql, updateMap, 3) ;
		}
		
		/*
		return (Integer) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String strSql = " update AutoInfo auto set auto.autoStatus=3 where auto.deleteFlag=3 and auto.autoId in ("
								+ autoIds + ")";
						String strSql1 = " delete from AutoInfo auto where auto.deleteFlag=0 and auto.autoId in ("
								+ autoIds + ")";
						// System.out.println(autoIds);

						session.createQuery(strSql1).executeUpdate();
						return session.createQuery(strSql).executeUpdate();

					}

				});*/
	}

	/**
	 * 创建车辆管理申请明细单
	 * 
	 * @param applyInfo
	 */
	public void createAutoApply(AutoApplyInfo applyInfo) {
		super.save(applyInfo);
		// this.getHibernateTemplate().save(applyInfo);
	}

	/**
	 * 批量删除车辆申请单
	 * 
	 * @param applyIds
	 *            申请单号集
	 * @return 成功操作记录数
	 */
	public int deleteAutoApplyByIds(final String applyIds)  {
		String[] idsStr = applyIds.split(",");
		Long[] ids = new Long[idsStr.length];
		for(int i = 0 ; i < idsStr.length ; i++){
			ids[i] = Long.parseLong(idsStr[i]);
		}
		String sql = "update Autoinfo apply set apply.deleteFlag=? where apply.applyId in (:applyId)";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("applyId", ids);

		return super.bulkUpdate(sql, map);

		/*return (Integer) this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String strSql = "update AutoApplyInfo apply set apply.deleteFlag=1 where apply.applyId in ("
								+ applyIds + ")";
						return session.createQuery(strSql).executeUpdate();
					}
				});*/

	}

	/**
	 * 根据申请号取得车辆申请单详细信息
	 * 
	 * @param applyId
	 *            申请号
	 * @return 车辆申请单详细信息
	 */
	public AutoApplyInfo findAutoApplyById(Long applyId) {
		return (AutoApplyInfo) this.getHibernateTemplate().get(
				AutoApplyInfo.class, applyId);
	}

	/**
	 * 创建车辆出车/归车记录
	 * 
	 * @param departInfo
	 *            出车/归车对象
	 */
	public void createAutoDepartInfo(AutoDepartInfo departInfo) {
		super.save(departInfo);
		// this.getHibernateTemplate().saveOrUpdate(departInfo);
	}

	public void updateAutoDepartInfo(AutoDepartInfo departInfo) {
		super.update(departInfo);
	}

	/**
	 * 取得车辆出车/归车详细信息
	 * 
	 * @param applyId
	 *            编号
	 * @return 车辆出车/归车详细信息
	 */
	public AutoDepartInfo findAutoDepartById(Long applyId) {
		return (AutoDepartInfo) this.getHibernateTemplate().get(
				AutoDepartInfo.class, applyId);
	}

	/**
	 * 根据查询条件获取管理员维护违章车辆列表
	 * 
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId
	 *            管理员
	 * @return 违章车辆一览列表
	 */
	public List findViolateListByManager(final String fieldName,
			final String fieldValue, final Long domainId) {
		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer();
						sb.append(" from M_Auto_Violate, m_auto_info");
						Map<String,Object> parameter = new HashMap<String,Object>();
						// sb.append(" where auto_mge="+managerId);
						sb
								.append(" where M_Auto_Violate.auto_id = m_auto_info.auto_id and M_Auto_Violate.del_flag=:delFlag ");
						sb.append(" and m_auto_info.domain_id =:domainId ");
						parameter.put("delFlag", 0);
						parameter.put("domainId", domainId);
						if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
							if(!fieldName.equals("reg_situ")){
								sb.append(" and (M_Auto_Violate."
										+ fieldName.trim() + " like :fieldValue)");
								parameter.put("fieldValue", "%"+SQLWildcardUtil.escape(fieldValue.trim())+"%");
							}else{
								sb.append(" and (M_Auto_Violate."
										+ fieldName.trim() + " = :fieldValue)");
								parameter.put("fieldValue", Integer.parseInt(fieldValue));
							}
						}

						String countSql = "select count(distinct M_Auto_Violate.apply_id) as myTotalCount"
								+ sb.toString();
						String sql = "select distinct M_Auto_Violate.* "
							+ sb.toString()
							+ " order by M_Auto_Violate.apply_id desc";
						
						SQLQuery countQuery = session.createSQLQuery(countSql);
						SQLQuery query = session.createSQLQuery(sql);
						
						if(!parameter.isEmpty()){
							Iterator iterator = parameter.keySet().iterator();
							String key;
							for (;iterator.hasNext();) {
								key = iterator.next().toString();
								countQuery.setParameter(key, parameter.get(key));
								query.setParameter(key, parameter.get(key));
							}
						}
						
						int size = (Integer)countQuery.addScalar("myTotalCount", Hibernate.INTEGER).uniqueResult();
						// if(!Pagination.isNeedCount().booleanValue()){

						Pagination.setRowCount(size);
						// }

						query.setFirstResult(Pagination.getFirstResult());
						query.setMaxResults(size);
						query.addEntity(AutoOffense.class);
						// query.addEntity(AutoInfo.class);

						// return this.paginate(query);
						return query.list();
					}
				});
	}

	/**
	 * 新增车辆违章信息
	 * 
	 * @param autoViolate
	 * @return
	 */
	public void createAutoViolate(AutoOffense autoViolate) {
		//
		super.save(autoViolate);
		// return autoViolate.getApplyId();
		// return (Long)this.getHibernateTemplate().save(autoViolate);
	}

	/**
	 * 修改车辆违章记录
	 * 
	 * @param autoViolate
	 *            车辆违章对象
	 */
	public void updateAutoViolate(AutoOffense autoViolate) {
		super.update(autoViolate);
		// this.getHibernateTemplate().update(autoViolate);
	}

	/**
	 * 根据主键批量逻辑删除车辆违章记录
	 * 
	 * @param applyIds
	 *            主键ID集合
	 * @return 更新的记录数
	 */
	public int deleteAutoViolateByIds(final String applyIds) {
		String sql = "update AutoOffense apply set apply.deleteFlag=:deleteFlag where apply.applyId in (:applyId) ";
		Map<String,Object> map = new HashMap<String,Object>()	;
		map.put("deleteFlag", 1);
		
		String[] idsStr = applyIds.split(",");
		Long[] ids = new Long[idsStr.length];
		for(int i = 0 ; i < idsStr.length ; i++){
			ids[i] = Long.parseLong(idsStr[i]);
		}
		map.put("applyId", ids);
		
		return super.bulkUpdate(sql, map);
		/*
		return (Integer) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						String strSql = "update AutoOffense apply set apply.deleteFlag=1 where apply.applyId in ("
								+ applyIds + ")";
						return session.createQuery(strSql).executeUpdate();
					}
				});*/
	}

	/**
	 * 根据主键查询违章车辆信息
	 * 
	 * @param applyId
	 *            违章ID
	 * @return 车辆违章信息对象
	 */
	public AutoOffense findAutoViolateById(Long applyId) {
		return (AutoOffense) this.getHibernateTemplate().get(AutoOffense.class,
				applyId);
	}
	/**
	 * 车辆查询审批
	 * (non-Javadoc)
	 * @see com.seeyon.v3x.office.auto.dao.AutoDao#findAutoAuditList(java.lang.String, java.lang.String, java.lang.Long)
	 */
	public List findAutoAuditList(final String fieldName,
			final String fieldValue, final Long applyManager) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		sb
				.append(" select t.applyId as applyid,a.autoId as autoid,m,d.name as departname,t.applyState as applystate from TApplylist t,AutoApplyInfo a,V3xOrgMember m ,V3xOrgDepartment d where t.applyId = a.applyId and t.applyUsername=m.id "
						+ " and t.applyDepId=d.id and t.applyMge = :applyMge and t.delFlag = :delFlag ");
		map.put("applyMge", applyManager);
		map.put("delFlag",
				com.seeyon.v3x.office.myapply.util.Constants.Del_Flag_Normal);
		// map.put("departmentId", departId);
		// map.put("members", outMember);
		// map.put("delFlag1", Constants.Del_Flag_Normal);
		// map.put("applyState",
		// com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);

		if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
			if (fieldName.equals("member")) {
				sb.append(" and m.id=:fieldName ");
				map.put("fieldName", Long.parseLong(fieldValue));
			} else if (fieldName.equals("departMent")) {
				sb.append(" and d.id=:fieldName ");
				map.put("fieldName", Long.parseLong(fieldValue));
			} else if (fieldName.equals("autoId")) {
				sb.append(" and a.autoId like :fieldName ");
				map.put("fieldName", "%" + SQLWildcardUtil.escape(fieldValue)
						+ "%");
			} else if (fieldName.equals("applyState")) {
				sb.append(" and t.applyState = :fieldName ");
				map.put("fieldName", Integer.parseInt(fieldValue));
			}
		}
		sb.append(" order by t.applyId desc ");
						
		return super.find(sb.toString(), map, new Object[0]);
	}

	/**
	 * 取得通过审核的所有车辆申请列表
	 * 
	 * @param fieldName
	 *            字段名
	 * @param fieldValue
	 *            字段值
	 * @param applyManager
	 *            管理员
	 * @return 通过审核的车辆申请列表
	 */
	public List findAuditdeApplyList(final String fieldName,
			final String fieldValue, final Long applyManager) {
		StringBuffer sb = new StringBuffer();
		List<Object> parameter = new ArrayList<Object>();
		sb.append("select t.applyId as applyId,m,a.autoId as autoid,a.autoDepartTime as departtime,a.autoBackTime as backtime from TApplylist t,AutoDepartInfo a,V3xOrgMember m where t.applyId=a.applyId and t.applyUsername = m.id " +
				" and t.applyState=? and t.delFlag=? ");
//		parameter.add(applyManager);
		parameter.add(Constants.ApplyStatus_Allow);
		parameter.add(Constants.Del_Flag_Normal);
		
		if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
			if(fieldName.equals("username")){
				sb.append(" and m.id = ? ");
				parameter.add(Long.parseLong(fieldValue));
			}else if(fieldName.equals("autoId")){
				sb.append(" and a.autoId like ? ");
				parameter.add("%"+SQLWildcardUtil.escape(fieldValue.trim())+"%");
			}else if(fieldName.equals("autoDeparttime")){
				sb.append(" and a.autoDepartTime like ? ");
				parameter.add("%"+SQLWildcardUtil.escape(fieldValue.trim())+"%");
			}else if(fieldName.equals("autoBacktime")){
				sb.append(" and a.autoBackTime like ? ");
				parameter.add("%"+SQLWildcardUtil.escape(fieldValue.trim())+"%");
			}
		}
		sb.append(" order by a.autoBackTime desc");
		return super.find(sb.toString(), null, parameter);
	}

	/**
	 * 根据车牌号取得该车的所有申请列表
	 * 
	 * @param autoId
	 *            车牌号
	 * @return
	 */
	public List findApplyListByAutoId(final String autoId) {
		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer();
						sb
								.append("select m.name as username,d.name as departname,b.auto_departtime as departtime,b.auto_backtime as backtime,a.auto_origin as autoorigin");
						sb
								.append(" from t_applylist t,t_auto_applyinfo a,v3x_org_member m,v3x_org_department d,t_Auto_DepartInfo b");
						sb.append(" where t.apply_id=a.apply_id");
						sb.append(" and t.apply_username=m.id");
						sb.append(" and t.apply_depid=d.id");
						sb.append(" and b.auto_id= ? ");
						sb.append(" and t.apply_state=? ");
						sb.append(" and t.del_flag=? ");
						sb.append(" and b.auto_backtime is null");
						sb.append(" and b.apply_id=a.apply_id");
						
						SQLQuery query = session.createSQLQuery(sb.toString());
						query.setParameter(0, autoId.trim());
						query.setParameter(1, 2);
						query.setParameter(2, 0);
						query.addScalar("username", Hibernate.STRING);
						query.addScalar("departname", Hibernate.STRING);
						query.addScalar("departtime", Hibernate.STRING);
						query.addScalar("backtime", Hibernate.STRING);
						query.addScalar("autoorigin", Hibernate.STRING);
						
						
						return query.list();
					}
				});
	}

	/**
	 * 取得车辆违章最大编号
	 * 
	 * @return
	 */
	public Long getMaxAutoLossNo() {

		return (Long) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						String sql = "select max(apply_id) as maxNo from M_Auto_Violate";
						SQLQuery query = session.createSQLQuery(sql);
						Long maxNo = (Long) query.addScalar("maxNo",
								Hibernate.LONG).uniqueResult();
						if (maxNo == null) {
							maxNo = new Long(90000000);
						}
						if (maxNo.longValue() < 90000000) {
							maxNo = new Long(90000000);
						}
						maxNo = Long.valueOf(maxNo.longValue() + 1);
						return maxNo;
					}
				});
	}

	/**
	 * 取得车辆最大编号
	 * 
	 * @return
	 */
	public Long getMaxAutoNo() {
		return (Long) this.getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						try {
							String sql = "select max(auto_id) as maxNo from M_Auto_Info";
							SQLQuery query = session.createSQLQuery(sql);
							Long maxNo = (Long) query.addScalar("maxNo",
									Hibernate.LONG).uniqueResult();
							if (maxNo == null) {
								maxNo = new Long(10000000);
							}
							if (maxNo.longValue() < 10000000) {
								maxNo = new Long(10000000);
							}
							maxNo = Long.valueOf(maxNo.longValue() + 1);
							return maxNo;
						} catch (Exception e) {
							return new Long(10000000);
						}
					}
				});
	}

	/**
	 * 取得车辆未归车的数量
	 * 
	 * @param autoId
	 * @return
	 */
	public int getAutoStatus(String autoId) {
		List<Object> list = new ArrayList<Object>();
		
		StringBuffer sb = new StringBuffer();
		sb.append("select count(t.apply_id) as myTotalCount");
		sb.append(" from t_applylist t,T_auto_departinfo a");
		sb.append(" where t.apply_id=a.apply_id");
		sb.append(" and t.apply_state= ? ");
		sb.append(" and a.auto_id=? ");
		sb.append(" and t.del_flag=? ");
		sb.append(" and a.auto_backtime is null");
		return super.getQueryCount(sb.toString(), new Object[]{2,autoId.trim(),0}, new Type[]{Hibernate.INTEGER,Hibernate.STRING,Hibernate.INTEGER});
	}

	/**
	 * 取得记录总行数
	 * 
	 * @param sql
	 * @return
	 */
	private int getCount(final String sql) {
		return (Integer) this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						SQLQuery query = session.createSQLQuery(sql);
						Integer totalCount = (Integer) query.addScalar(
								"myTotalCount", Hibernate.INTEGER)
								.uniqueResult();
						return totalCount.intValue();
					}
				});
	}

	/**
	 * 取得车辆部门统计列表
	 * 
	 * @return
	 */
	public List getAutoSummayByDepart(final Long userId, final boolean needPage) {

		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Calendar ca = Calendar.getInstance();
						// 取得当前月份第一天
						String firstDayOfMonth = Datetimes
								.formatDatetimeWithoutSecond(Datetimes
										.getFirstDayInMonth(ca.getTime()));

						// 取得当前月份最后一天
						String lastDayOfMonth = Datetimes
								.formatDatetimeWithoutSecond(Datetimes
										.getLastDayInMonth(ca.getTime()));

						StringBuffer sb = new StringBuffer();

						sb = new StringBuffer();
						sb
								.append("select o.name as name,vod.name as dname,s.mcount as mcount,count(*) as tcount,");
						sb
								.append("sum(d.auto_mileage) as mileage,sum(d.auto_fuel) as autofuel,sum(d.road_price) as roadprice,");
						sb
								.append("sum(d.fuel_price) as fuelprice,sum(d.other_price) as otherprice ");
						sb.append("from t_auto_departinfo d,t_applylist t,v3x_org_department vod,");
						sb
								.append("(select a.auto_driver as depid,count(*) as mcount ");
						sb.append(" from t_auto_departinfo a ");
						sb.append(" where a.auto_backtime>= ?  and a.auto_backtime<= ? ");
						sb
								.append(" group by a.auto_driver) s LEFT OUTER JOIN v3x_org_member o on s.depid=o.id ");
						sb
								.append("where d.auto_driver=o.id and o.org_department_id=vod.id and d.apply_id=t.apply_id and t.apply_mge=? ");
						sb.append(" and d.auto_backtime is not null");
						sb.append(" group by o.name,vod.name,s.mcount");
						if(needPage){
							SQLQuery countQuery = session.createSQLQuery(sb.toString());
							countQuery.setParameter(0, firstDayOfMonth);
							countQuery.setParameter(1, lastDayOfMonth);
							countQuery.setParameter(2, userId);
							countQuery.addScalar("name", Hibernate.STRING);
							countQuery.addScalar("dname", Hibernate.STRING);
							countQuery.addScalar("mcount", Hibernate.INTEGER);
							countQuery.addScalar("tcount", Hibernate.INTEGER);
							countQuery.addScalar("mileage", Hibernate.FLOAT);
							countQuery.addScalar("autofuel", Hibernate.FLOAT);
							countQuery.addScalar("roadprice", Hibernate.FLOAT);
							countQuery.addScalar("fuelprice", Hibernate.FLOAT);
							countQuery.addScalar("otherprice", Hibernate.FLOAT);
							
							int size = countQuery.list().size();
							
							Pagination.setRowCount(size);
						}
						SQLQuery query = session.createSQLQuery(sb.toString());
						if(needPage){
							query.setFirstResult(Pagination.getFirstResult());
							query.setMaxResults(Pagination.getMaxResults());
						}
						query.setParameter(0, firstDayOfMonth);
						query.setParameter(1, lastDayOfMonth);
						query.setParameter(2, userId);
						
						// query =session.createSQLQuery(sql);
						query.addScalar("name", Hibernate.STRING);
						query.addScalar("dname", Hibernate.STRING);
						query.addScalar("mcount", Hibernate.INTEGER);
						query.addScalar("tcount", Hibernate.INTEGER);
						query.addScalar("mileage", Hibernate.FLOAT);
						query.addScalar("autofuel", Hibernate.FLOAT);
						query.addScalar("roadprice", Hibernate.FLOAT);
						query.addScalar("fuelprice", Hibernate.FLOAT);
						query.addScalar("otherprice", Hibernate.FLOAT);

						return query.list();
					}
				});
	}

	public List getAutoSummayByDriver(final Long userId, final boolean needPage) {

		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Calendar ca = Calendar.getInstance();
						// 取得当前月份第一天
						String firstDayOfMonth = Datetimes
								.formatDatetimeWithoutSecond(Datetimes
										.getFirstDayInMonth(ca.getTime()));

						// 取得当前月份最后一天
						String lastDayOfMonth = Datetimes
								.formatDatetimeWithoutSecond(Datetimes
										.getLastDayInMonth(ca.getTime()));

						String sql = "";
						StringBuffer sb = new StringBuffer();

						sb
								.append("select o.name as name,s.mcount as mcount,count(*) as tcount,");
						sb
								.append("sum(d.auto_mileage) as mileage,sum(d.auto_fuel) autofuel,sum(d.road_price) as roadprice,");
						sb
								.append("sum(d.fuel_price) as fuelprice,sum(d.other_price) as otherprice	");
						sb.append("from t_auto_departinfo d,t_applylist t,");
						sb
								.append("(select t.apply_depid as depid,count(*) as mcount ");
						sb
								.append(" from t_auto_departinfo a,t_applylist t where a.apply_id=t.apply_id ");
						sb.append(" and a.auto_backtime>=? " 
								+ " and a.auto_backtime<=? ");
						sb
								.append(" group by t.apply_depid) s LEFT OUTER JOIN v3x_org_department o on s.depid=o.id ");
						sb
								.append("where d.apply_id=t.apply_id and t.apply_depid=o.id and t.apply_mge=? ");
						sb.append(" and d.auto_backtime is not null");
						sb.append(" group by o.name,s.mcount");

						sql = sb.toString();
						if(needPage){
							SQLQuery countQuery = session.createSQLQuery(sql);
							countQuery.setParameter(0, firstDayOfMonth);
							countQuery.setParameter(1, lastDayOfMonth);
							countQuery.setParameter(2, userId);
							countQuery.addScalar("name", Hibernate.STRING);
							countQuery.addScalar("mcount", Hibernate.INTEGER);
							countQuery.addScalar("tcount", Hibernate.INTEGER);
							countQuery.addScalar("mileage", Hibernate.FLOAT);
							countQuery.addScalar("autofuel", Hibernate.FLOAT);
							countQuery.addScalar("roadprice", Hibernate.FLOAT);
							countQuery.addScalar("fuelprice", Hibernate.FLOAT);
							countQuery.addScalar("otherprice", Hibernate.FLOAT);
							
							int size = countQuery.list().size();
							
							Pagination.setRowCount(size);
						}
						SQLQuery query = session.createSQLQuery(sql);
						if(needPage){
							query.setFirstResult(Pagination.getFirstResult());
							query.setMaxResults(Pagination.getMaxResults());
						}
						query.setParameter(0, firstDayOfMonth);
						query.setParameter(1, lastDayOfMonth);
						query.setParameter(2, userId);
						
						query.addScalar("name", Hibernate.STRING);
						query.addScalar("mcount", Hibernate.INTEGER);
						query.addScalar("tcount", Hibernate.INTEGER);
						query.addScalar("mileage", Hibernate.FLOAT);
						query.addScalar("autofuel", Hibernate.FLOAT);
						query.addScalar("roadprice", Hibernate.FLOAT);
						query.addScalar("fuelprice", Hibernate.FLOAT);
						query.addScalar("otherprice", Hibernate.FLOAT);

						return query.list();
					}
				});
	}

	/**
	 * 管理员管理的车辆移交功能
	 * 
	 */
	public void updateAutoMangerBatch(final long oldManager,
			final long newManager, final User user) {
		this.updateAutoMangerBatch(oldManager,
				newManager, user,0l);
	}
	
	public void  updateAutoMangerBatch(final long oldManager,
			final long newManager, final User user,final long accountId) {
		super.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String sql = "";
				boolean flag = true;
				if (accountId == 0l) {
					sql += "update AutoInfo  set autoManager=:autoManager,autoDept=:autoDept,domainId=:domainId where autoManager=:oldAutoManager";
				}else {
					flag = false;
					sql += "update AutoInfo  set autoManager=:autoManager where domainId=:domainId";
				}
				Query query = session.createQuery(sql);
				if (flag) {
					query.setLong("domainId", user.getLoginAccount());
					query.setLong("autoManager", newManager);
					query.setLong("autoDept", user.getDepartmentId());
					query.setLong("oldAutoManager", oldManager);
				}else {
					query.setLong("domainId", user.getLoginAccount());
					query.setLong("autoManager", newManager);
				}
				query.executeUpdate();
				return null;
			}

		});

	}

	public void audiTransfer(final long oldManager, final long newManager) {
		super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String sql = "update TApplylist set applyMge=? where applyMge=? and (applyState=? or applyState=?) and applyType=? ";
				Query query = session.createQuery(sql);
				query.setLong(0, newManager);
				query.setLong(1, oldManager);
				query.setInteger(2, com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
				query.setInteger(3, com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow);
				query.setInteger(4,  OfficeModelType.auto_type);
				query.executeUpdate();
				return null;
			}
		});

	}
	

    public List getSameTimeApply(String autoDepartTime, String autoBackTime,String autoId) {
    	StringBuffer sb = new StringBuffer();
    	List parameter = new ArrayList();
    	sb.append(" select autoApply from AutoApplyInfo autoApply,OfficeApply o where o.applyState=? and o.applyType=? and autoApply.autoId=? and autoApply.applyId=o.applyId and ((autoApply.autoDepartTime between ? and ?)  or (autoApply.autoBackTime between ? and ?) ) ");
    	parameter.add(com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow);
    	parameter.add(com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Auto+"");
    	parameter.add(autoId);
    	parameter.add(autoDepartTime);
    	parameter.add(autoBackTime);
    	parameter.add(autoDepartTime);
    	parameter.add(autoBackTime);
    	return super.find(sb.toString(), null, parameter);
    }
    
    public int getNotAuditApplyByAutoId(String autoId) {
    	//没有审批的
    	StringBuffer sb = new StringBuffer();
    	List<Object> list = new ArrayList<Object>();
    	sb.append(" from AutoApplyInfo a , TApplylist t where a.applyId=t.applyId and a.autoId=? and a.deleteFlag = ? and t.delFlag= ? and t.applyState= ? and t.applyType=? ");
    	list.add(autoId);
    	list.add(Constants.Del_Flag_Normal);
    	list.add(Constants.Del_Flag_Normal);
    	list.add(Constants.ApplyStatus_Wait);
    	list.add(OfficeModelType.auto_type);
    	return super.getQueryCount(sb.toString(), list.toArray(), new Type[]{Hibernate.STRING,Hibernate.INTEGER,Hibernate.INTEGER,Hibernate.INTEGER,Hibernate.INTEGER});
    }
    
    public boolean getNotDepartByAutoId(String autoId) {
    	//查看该车是否已经出车
    	DetachedCriteria criteria = DetachedCriteria.forClass(AutoDepartInfo.class);
    	criteria.add(Restrictions.eq("autoId", autoId));
    	criteria.add(Restrictions.eq("deleteFlag", Constants.Del_Flag_Normal));
    	criteria.add(Restrictions.isNull("autoBackTime"));
    	criteria.add(Restrictions.isNotNull("autoDepartTime"));
    	return super.getCountByCriteria(criteria) > 0 ;
    }

	@Override
	public List getAutoBackListByUserId(String userid) {
		StringBuffer hql= new StringBuffer(" select a  from AutoDepartInfo a,TApplylist t ");
		hql.append(" where t.applyUsername=? and t.applyId=a.applyId and a.deleteFlag=0 ");
		hql.append(" and ( a.autoBackTime is null or a.autoBackTime >= ?) ");
		String currentDay = Datetimes.formatDatetimeWithoutSecond(new Date(System.currentTimeMillis()));
		List list= super.find(hql.toString(), new Long(userid),currentDay);
		return list;
	}
}
