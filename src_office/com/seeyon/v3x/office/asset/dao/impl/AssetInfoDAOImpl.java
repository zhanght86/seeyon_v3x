package com.seeyon.v3x.office.asset.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.asset.dao.AssetInfoDAO;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.asset.util.Constants;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class AssetInfoDAOImpl extends BaseHibernateDao<MAssetInfo> implements
		AssetInfoDAO {

	public void save(MAssetInfo mAssetInfo) {
		super.save(mAssetInfo);
	}

	public void update(MAssetInfo mAssetInfo) {
		super.update(mAssetInfo);
	}

	public SQLQuery find(String sql, Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		try {
			query = session.createSQLQuery(sql);
			if (map != null) {
				Iterator iterator = map.keySet().iterator();
				String key = null;
				for (; iterator.hasNext();) {
					key = iterator.next().toString();
					query.setParameter(key, map.get(key));
				}
			}
		} catch (Exception ex) {

		} finally {
			super.releaseSession(session);
		}
		return query;
	}

	public int getCount(final String sql, final Map map) {
		// Session session = super.getSession();
		// SQLQuery query = null;
		// try{
		// query = session.createSQLQuery(sql);
		// }catch(Exception ex){
		//			
		// }finally{
		// super.releaseSession(session);
		// }
		// int totalCount =
		// (Integer)query.addScalar(Constants.Total_Count_Field,
		// Hibernate.INTEGER).uniqueResult();
		// return totalCount;
		return (Integer) super.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						SQLQuery query = session.createSQLQuery(sql);
						if (map != null) {
							Iterator iterator = map.keySet().iterator();
							String key = null;
							for (; iterator.hasNext();) {
								key = iterator.next().toString();
								query.setParameter(key, map.get(key));
							}
						}
						int totalCount = (Integer) query.addScalar(
								Constants.Total_Count_Field, Hibernate.INTEGER)
								.uniqueResult();
						return totalCount;
					}

				});

	}

	public MAssetInfo load(long id) {
		return (MAssetInfo) this.getHibernateTemplate().load(MAssetInfo.class,
				new Long(id));
	}

	/**
	 * 管理员管理的办公设备移交功能
	 * 
	 */
	public void updateAssetMangerBatch(final long oldManager,
			final long newManager, final User user) {
		this.updateAssetMangerBatch(oldManager, newManager, user, true);
	}
	
	
	public void updateAssetMangerBatch(final long oldManager,
			final long newManager, final User user ,final boolean fromFlag) {
		super.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String sql = "";
				if (fromFlag) {
					sql = "update MAssetInfo  set assetMge=:assetManager,domainId=:domainId where assetMge=:oldAssetManager";
				}else {
					sql = "update MAssetInfo  set assetMge=:assetManager where domainId=:domainId";
				}
				Query query = session.createQuery(sql);
				if (fromFlag) {
					query.setLong("assetManager", newManager);
					query.setLong("domainId", user.getLoginAccount());
					query.setLong("oldAssetManager", oldManager);
				}else {
					query.setLong("assetManager", newManager);
					query.setLong("domainId", user.getLoginAccount());
				}
				query.executeUpdate();
				return null;
			}

		});
	}

	/**
	 * 管理员管理的办公设备申请移交功能
	 * 
	 */
	public void audiTransfer(final long oldManager, final long newManager) {
		super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String sql = "update TApplylist set applyMge=? where applyMge=? and (applyState=? or applyState=?) and applyType=? ";
				Query query = session.createQuery(sql);
				query.setLong(0, newManager);
				query.setLong(1, oldManager);
				query
						.setInteger(
								2,
								com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait);
				query
						.setInteger(
								3,
								com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow);
				query.setInteger(4, OfficeModelType.asset_type);
				query.executeUpdate();
				return null;
			}

		});

	}

	public List findAssetRegList(Long userid, String fieldName,
			String fieldValue) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MAssetInfo.class);
		criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
		criteria.add(Restrictions.eq("assetMge", userid));
		if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
			criteria = getCondition(criteria, fieldName, fieldValue);
		}
		criteria.add(Restrictions.eq("assetMge", CurrentUser.get().getId()));
		return super.executeCriteria(criteria);
	}

	public List findAssetAppList(String fieldName, String fieldValue,
			Long[] depart) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MAssetInfo.class);
		criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
		criteria.add(Restrictions
				.eq("assetState", Constants.Asset_Status_Allow));
		criteria.add(Restrictions.in("assetMge", depart));
		criteria.add(Restrictions.eq("domainId", CurrentUser.get().getLoginAccount()));
		if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
			criteria = getCondition(criteria, fieldName, fieldValue);
		}
		return super.executeCriteria(criteria);
	}

	public List findAssetPermList(String fieldName, String fieldValue,
			Long adminId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
		criteria.add(Restrictions.eq("applyType",
				com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Asset));
		criteria.add(Restrictions.eq("applyMge", adminId));
		// criteria.add(Restrictions.in("applyDepId", departId));
		criteria
				.add(Restrictions
						.or(
								Restrictions.eq("delFlag",
										Constants.Del_Flag_Normal),
								Restrictions
										.and(
												Restrictions
														.eq(
																"delFlag",
																Constants.Del_TYPE_APPLY),
												Restrictions
														.not(Expression
																.eq(
																		"applyState",
																		com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Wait)))));
		if (Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)) {
			int field = Integer.parseInt(fieldName);
			switch (field) {
			case Constants.Search_Condition_AssetName:
				// DetachedCriteria c =
				// DetachedCriteria.forClass(TAssetApplyinfo.class);
				// criteria.add(Restrictions.eq("applyUsername",
				// Long.parseLong(fieldValue)));
				criteria
						.add(Restrictions
								.sqlRestriction(
										" apply_id in (select apply_id from t_asset_applyinfo where asset_id in(select asset_id from m_asset_info where asset_name like ?)) ",
										"%"
												+ SQLWildcardUtil
														.escape(fieldValue)
												+ "%", Hibernate.STRING));
				break;
			case Constants.Search_Condition_Department:
				criteria.add(Restrictions.eq("applyUsedep", Long
						.parseLong(fieldValue)));
				break;
			case Constants.Search_Condition_Member:
				criteria.add(Restrictions.eq("applyUsername", Long
						.parseLong(fieldValue)));
				break;
			case Constants.Search_Condition_ApplyStat:
				criteria.add(Restrictions.eq("applyState", Integer
						.parseInt(fieldValue)));
				break;
			}
		}
		criteria.addOrder(Order.desc("applyDate"));
		// criteria.addOrder(Order.asc("applyDate"));
		return super.executeCriteria(criteria);
	}

	public List findAssetStorageList(String condition, String keyword,
			Long[] ids) {
		List list = new ArrayList();
		DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
		criteria.add(Restrictions.eq("applyType",
				com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Asset));
		criteria
				.add(Restrictions
						.eq(
								"applyState",
								com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow));
		criteria.add(Restrictions.in("applyId", ids));
		if (Strings.isNotBlank(condition) && Strings.isNotBlank(keyword)) {
			int field = Integer.parseInt(condition);
			switch (field) {
			case Constants.Search_Condition_AssetName:
				criteria
						.add(Restrictions
								.sqlRestriction(
										" apply_id in (select apply_id from t_asset_applyinfo where asset_id in(select asset_id from m_asset_info where asset_name like ?)) ",
										"%"
												+ SQLWildcardUtil
														.escape(keyword.trim())
												+ "%", Hibernate.STRING));
				break;
			case Constants.Search_Condition_Member:
				criteria.add(Restrictions.eq("applyUsername", Long
						.parseLong(keyword)));
				break;
			case Constants.Search_Condition_SorageStat:
				int key = Integer.parseInt(keyword);
				switch (key) {
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart:
					criteria
							.add(Restrictions
									.sqlRestriction(" {alias}.apply_id in(select apply_id from t_asset_departinfo where asset_departtime is not null  And asset_backtime is null )"));
					// criteria.add(Restrictions.sqlRestriction(" {alias}.apply_id in('1')"));
					break;
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back:
					criteria
							.add(Restrictions
									.sqlRestriction(" {alias}.apply_id in(select apply_id from t_asset_departinfo where  asset_backtime is not null )"));
					break;
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow:
					criteria
							.add(Restrictions
									.sqlRestriction(" {alias}.apply_id not in (select apply_id from t_asset_departinfo )"));
					break;
				}
				break;
			}
		}
		list = super.executeCriteria(criteria);
		return list;
	}

	/**
	 * 生成查询DetachedCriteria
	 * 
	 * @param criteria
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	private DetachedCriteria getCondition(DetachedCriteria criteria,
			String fieldName, String fieldValue) {
		int field = Integer.parseInt(fieldName);
		switch (field) {
		case Constants.Search_Condition_AssetName:
			criteria.add(Restrictions.like("assetName", "%"
					+ SQLWildcardUtil.escape(fieldValue) + "%"));
			break;
		case Constants.Search_Condition_AssetStat:
			criteria.add(Restrictions.eq("assetState", Integer
					.parseInt(fieldValue)));
			break;
		case Constants.Search_Condition_AssetType:
			criteria.add(Restrictions.eq("officeType.typeId", Long
					.parseLong(fieldValue)));
			break;
		case Constants.Search_Condition_AssetModel:
			criteria.add(Restrictions.like("assetModel", "%"
					+ SQLWildcardUtil.escape(fieldValue) + "%"));
			break;
		case Constants.Search_Condition_AssetCode:
			criteria.add(Restrictions.like("assetCode", "%"
					+ SQLWildcardUtil.escape(fieldValue) + "%"));
			break;
		}
		return criteria;
	}

	public List findAllAssetInfo(Long assMge) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MAssetInfo.class);
		criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
		criteria.add(Restrictions.eq("assetMge", assMge));
		return super.executeCriteria(criteria, -1, -1);
	}

	public List getAssetSummayByMember(final boolean needPage) {
		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer();
						User user = CurrentUser.get();
						Calendar ca = Calendar.getInstance();
						// 取得当前月份第一天
						Date firstDayOfMonth = Datetimes.getFirstDayInMonth(ca
								.getTime());

						// 取得当前月份最后一天
						Date lastDayOfMonth = Datetimes.getLastDayInMonth(ca
								.getTime());

						Date firstDayOfWeek = Datetimes.getFirstDayInWeek(ca
								.getTime());
						Date lastDayOfWeek = Datetimes.getLastDayInWeek(ca
								.getTime());
						sb.append("select t.dname,t.department_name,t.asset_name,w.count as wcount,m.count as mcount,t.count as count,n.count as nobackcount");
						sb.append(" from");
						sb.append(" (select d.name as dname,c.asset_name as asset_name,e.name as department_name,count(*) as count");
						sb.append(" from t_asset_applyinfo a,t_applylist b,m_asset_info c,v3x_org_member d,v3x_org_department e");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.asset_id=a.asset_id and d.id=b.apply_username and d.org_department_id=e.id and c.asset_mge=? and c.del_flag=0");
						sb.append(" group by d.name,c.asset_name,e.name) t ");
						sb.append(" left join (select d.name as dname,c.asset_name as asset_name,count(*) as count");
						sb.append(" from t_asset_applyinfo a,t_applylist b,m_asset_info c,v3x_org_member d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.asset_id=a.asset_id and d.id=b.apply_username");
						sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.asset_name) w  on t.dname=w.dname and t.asset_name=w.asset_name ");
						sb.append(" left join (select d.name as dname,c.asset_name as asset_name,count(*) as count");
						sb.append(" from t_asset_applyinfo a,t_applylist b,m_asset_info c,v3x_org_member d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.asset_id=a.asset_id and d.id=b.apply_username");
						sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.asset_name) m on t.dname=m.dname and t.asset_name=m.asset_name");
						sb.append(" left join (select d.name as dname,c.asset_name as asset_name,count(*) as count");
						sb.append(" from t_asset_departinfo a,t_applylist b,m_asset_info c,v3x_org_member d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and a.asset_backtime is null");
						sb.append(" and a.asset_departtime is not null and c.asset_id=a.asset_id and d.id=b.apply_username");
						sb.append(" group by d.name,c.asset_name) n on n.dname=t.dname and n.asset_name=t.asset_name");
						String sql = sb.toString();
						if(needPage){
							SQLQuery countQuery = session.createSQLQuery(sql);
							countQuery.setLong(0, user.getId());
							countQuery.setDate(1, firstDayOfWeek);
							countQuery.setDate(2, lastDayOfWeek);
							countQuery.setDate(3, firstDayOfMonth);
							countQuery.setDate(4, lastDayOfMonth);
							
							int size = countQuery.list().size();
							
							Pagination.setRowCount(size);
						}
						SQLQuery query = session.createSQLQuery(sql);
						if(needPage){
							query.setFirstResult(Pagination.getFirstResult());
							query.setMaxResults(Pagination.getMaxResults());
						}
						query.setLong(0, user.getId());
						query.setDate(1, firstDayOfWeek);
						query.setDate(2, lastDayOfWeek);
						query.setDate(3, firstDayOfMonth);
						query.setDate(4, lastDayOfMonth);

						query.addScalar("dname", Hibernate.STRING);
						query.addScalar("department_name", Hibernate.STRING);
						query.addScalar("asset_name", Hibernate.STRING);
						query.addScalar("wcount", Hibernate.INTEGER);
						query.addScalar("mcount", Hibernate.INTEGER);
						query.addScalar("count", Hibernate.INTEGER);
						query.addScalar("nobackcount", Hibernate.INTEGER);
						return query.list();
					}
				});
	}
	
	public List getAssetSummayByDep(final boolean needPage) {
		return (List) this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						StringBuffer sb = new StringBuffer();
						User user = CurrentUser.get();
						Calendar ca = Calendar.getInstance();
						// 取得当前月份第一天
						Date firstDayOfMonth = Datetimes.getFirstDayInMonth(ca
								.getTime());

						// 取得当前月份最后一天
						Date lastDayOfMonth = Datetimes.getLastDayInMonth(ca
								.getTime());

						Date firstDayOfWeek = Datetimes.getFirstDayInWeek(ca
								.getTime());
						Date lastDayOfWeek = Datetimes.getLastDayInWeek(ca
								.getTime());
						sb.append("select t.dname,t.asset_name,w.count as wcount,m.count as mcount,t.count as count,n.count as nobackcount");
						sb.append(" from");
						sb.append(" (select d.name as dname,c.asset_name as  asset_name,count(*) as count");
						sb.append(" from t_asset_applyinfo a,t_applylist b,m_asset_info c,v3x_org_department d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.asset_id=a.asset_id and d.id=b.apply_depid and c.asset_mge=? and c.del_flag=0");
						sb.append(" group by d.name,c.asset_name) t ");
						sb.append(" left join (select d.name as dname,c.asset_name as asset_name,count(*) as count");
						sb.append(" from t_asset_applyinfo a,t_applylist b,m_asset_info c,v3x_org_department d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.asset_id=a.asset_id and d.id=b.apply_depid");
						sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.asset_name) w  on t.dname=w.dname and t.asset_name=w.asset_name ");
						sb.append(" left join (select d.name as dname,c.asset_name as asset_name,count(*) as count");
						sb.append(" from t_asset_applyinfo a,t_applylist b,m_asset_info c,v3x_org_department d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.asset_id=a.asset_id and d.id=b.apply_depid");
						sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.asset_name) m on t.dname=m.dname and t.asset_name=m.asset_name");
						sb.append(" left join (select d.name as dname,c.asset_name as asset_name,count(*) as count");
						sb.append(" from t_asset_departinfo a,t_applylist b,m_asset_info c,v3x_org_department d");
						sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and a.asset_backtime is null");
						sb.append(" and a.asset_departtime is not null and c.asset_id=a.asset_id and d.id=b.apply_depid");
						sb.append(" group by d.name,c.asset_name) n on n.dname=t.dname and n.asset_name=t.asset_name");
						String sql = sb.toString();
						if(needPage){
							SQLQuery countQuery = session.createSQLQuery(sql);
							countQuery.setLong(0, user.getId());
							countQuery.setDate(1, firstDayOfWeek);
							countQuery.setDate(2, lastDayOfWeek);
							countQuery.setDate(3, firstDayOfMonth);
							countQuery.setDate(4, lastDayOfMonth);
							
							int size = countQuery.list().size();
							
							Pagination.setRowCount(size);
						}
						SQLQuery query = session.createSQLQuery(sql);
						if(needPage){
							query.setFirstResult(Pagination.getFirstResult());
							query.setMaxResults(Pagination.getMaxResults());
						}
						 query.setLong(0, user.getId());
						 query.setDate(1, firstDayOfWeek);
						 query.setDate(2, lastDayOfWeek);
						 query.setDate(3, firstDayOfMonth);
						 query.setDate(4, lastDayOfMonth);

						query.addScalar("dname", Hibernate.STRING);
						query.addScalar("asset_name", Hibernate.STRING);
						query.addScalar("wcount", Hibernate.INTEGER);
						query.addScalar("mcount", Hibernate.INTEGER);
						query.addScalar("count", Hibernate.INTEGER);
						query.addScalar("nobackcount", Hibernate.INTEGER);
						return query.list();
					}
				});
	}

}
