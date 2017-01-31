package com.seeyon.v3x.office.admin.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.office.admin.dao.AdminSettingDAO;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.domain.MAdminSettingId;
import com.seeyon.v3x.office.admin.util.Constants;
import com.seeyon.v3x.util.Strings;

public class AdminSettingDAOImpl extends BaseHibernateDao implements
		AdminSettingDAO {
	public List listAdminSettingById(Long domainId, Long admin, Long depId,String adminModel, Boolean modelEqual) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MAdminSetting.class);
		if(domainId != null){
			criteria.add(Restrictions.eq("accountId", domainId));
		}
		if(admin != null){
			criteria.add(Restrictions.eq("id.admin", admin));
		}
		if(depId != null){
			criteria.add(Restrictions.eq("id.mngdepId", depId.toString()));
		}
		if(Strings.isNotBlank(adminModel)){
			if(modelEqual.booleanValue()){
				criteria.add(Restrictions.eq("adminModel", adminModel));
			}else{
				criteria.add(Restrictions.like("adminModel", adminModel));
			}
		}
		criteria.add(Restrictions.eq("delFlag", 0));
		return super.executeCriteria(criteria, -1, -1);
	}

	public List listAdminSetting(Long domainId, String field, String keyword) {
//		DetachedCriteria criteria = DetachedCriteria
//				.forClass(MAdminSetting.class);
//		criteria.add(Restrictions.eq("domainId", domainId));
//		criteria.add(Restrictions.eq("delFlag", 0));
//		criteria = createCondition(criteria, field, keyword);
//		return super.executeCriteria(criteria, -1, -1);
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select a from MAdminSetting a, V3xOrgMember m where m.id = a.id.admin and a.accountId = :accountId");
		params.put("accountId", domainId);
		if(Strings.isNotBlank(field) && Strings.isNotBlank(keyword)){
			int condition = Integer.parseInt(field);
			switch (condition) {
				case Constants.Search_Condition_Model:
					int key = Integer.parseInt(keyword);
					hql.append(" and a.adminModel like :adminModel");
					String mode = "";
					switch (key) {
						case 1:
							mode = "1";
							params.put("adminModel", mode + "%");
							break;
						case 2:
							mode = "_1";
							params.put("adminModel", mode + "%");
							break;
						case 3:
							mode = "__1";
							params.put("adminModel", mode + "%");
							break;
						case 4:
							mode = "___1";
							params.put("adminModel", mode + "%");
							
							break;
						case 5:
							mode = "1";
							params.put("adminModel", "%" + mode);
							break;
					}
					break;
			case Constants.Search_Condition_Admin:
				hql.append(" and a.id.admin = :adminId");
				params.put("adminId", Long.parseLong(keyword));
				break;
			case Constants.Search_Condition_Depart:
				hql.append(" and a.id.mngdepId = :mngdepId");
				params.put("mngdepId", keyword);
				break;
			}
		}
		//branches_a8_v350_r_gov 向凡添加，政务版有会议室管理模块需要屏蔽掉Start
		boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
		if(isGovVersion){
			if(null == params.get("adminModel")){//如果是通过小查询的话，就不需要执行下面这段，小查询时没有查询'会议室管理'模块的
				hql.append(" and a.adminModel not in(:adminModel)");
				params.put("adminModel", "00001");//00001 为会议室管理模块
			}
		}
		// 青岛大学_综合办公和会议室管理中人员进行授权了，但是授权后人员的信息看不见
		hql.append(" order by a.id");
		//branches_a8_v350_r_gov 向凡添加，政务版有会议室管理模块需要屏蔽掉End
		return super.find(hql.toString(), params);
	}
	private DetachedCriteria createCondition(DetachedCriteria criteria,
			String field, String keyword) {
		if (Strings.isNotBlank(field) && Strings.isNotBlank(keyword)) {
			int condition = Integer.parseInt(field);
			switch (condition) {
			case Constants.Search_Condition_Model:
				int key = Integer.parseInt(keyword);
				switch (key) {
				case 1:
					criteria.add(Restrictions.like("adminModel", "1",MatchMode.START));
					break;
				case 2:
					criteria.add(Restrictions.like("adminModel", "_1",MatchMode.START));
					break;
				case 3:
					criteria.add(Restrictions.like("adminModel", "__1",MatchMode.START));
					break;
				case 4:
					criteria.add(Restrictions.like("adminModel", "___1",MatchMode.START));
					break;
				case 5:
					criteria.add(Restrictions.like("adminModel", "1",MatchMode.END));
					break;
				}
				break;
			case Constants.Search_Condition_Admin:
				criteria.add(Restrictions.eq("id.admin", Long
						.parseLong(keyword)));
				break;
			case Constants.Search_Condition_Depart:
				criteria.add(Restrictions.eq("id.mngdepId", keyword));
				break;
			}
		}
		return criteria;
	}

	public SQLQuery find(String sql,Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		try {
			query = session.createSQLQuery(sql);
			if(map != null){
  				Iterator iterator = map.keySet().iterator();
  				String key = null ;
  				for(;iterator.hasNext();){
  					key = iterator.next().toString();
  					query.setParameter(key, map.get(key));
  				}
  			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
		} finally {
			super.releaseSession(session);
		}
		return query;
		// SQLQuery query =
		// super.getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(sql);
		// return query;
	}

	public int getCount(String sql,Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		int totalCount = 0;
		try {
			query = super.getSession().createSQLQuery(sql);
			if(map != null){
  				Iterator iterator = map.keySet().iterator();
  				String key = null ;
  				for(;iterator.hasNext();){
  					key = iterator.next().toString();
  					query.setParameter(key, map.get(key));
  				}
  			}
			totalCount = (Integer) query.addScalar(Constants.Total_Count_Field,
					Hibernate.INTEGER).uniqueResult();
		} catch (Exception ex) {

		} finally {
			super.releaseSession(session);
		}
		return totalCount;
	}

	public MAdminSetting load(MAdminSettingId id) {
		return (MAdminSetting) this.getHibernateTemplate().load(
				MAdminSetting.class, id);
	}

	public void save(MAdminSetting admin) {
		if (load(admin.getId()) != null) {
			super.getHibernateTemplate().save(admin);
		}
	}

	public void update(MAdminSetting admin) {
		super.update(admin);
	}

	public void delete(final MAdminSetting admin) {
		super.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "delete from MAdminSetting where admin=:adminId and adminModel=:adminModel and id.mngdepId=:mngdepId ";
				Query query = session.createQuery(sql);
				query.setLong("adminId", admin.getId().getAdmin());
				query.setString("mngdepId", admin.getId().getMngdepId());
				query.setString("adminModel", admin.getAdminModel());
				query.executeUpdate();
				return null;
			}

		});
	}
	
	//专门为综合办公的修改增加的方法（用上个delete会造成一个session中有两个相同标识的不同实体）
	public void deleteForUpdate(final MAdminSetting admin) {
		super.getHibernateTemplate().delete(admin);
	}
	
	public List findAdminSettingByModel(String model, Long domainId) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MAdminSetting.class);
		criteria.add(Restrictions.like("adminModel", model));
		if(domainId!=null)
			criteria.add(Restrictions.eq("domainId", domainId));
		criteria.add(Restrictions.eq("delFlag", 0));
		return super.executeCriteria(criteria, -1, -1);
	}

	public void updateAutoManager(Long adminId, Long domainId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> parameter = new HashMap<String, Object>();
		sb
				.append("update AutoInfo m set m.autoManager = :perId where m.autoDept  = :accoundId and m.autoManager not in(select distinct id.admin from MAdminSetting where id.admin=m.autoManager  and adminModel like :adminModel and domainId = :domainId ");
		parameter.put("perId", adminId);
		parameter.put("accoundId", domainId);
		parameter.put("adminModel", "1____");
		parameter.put("domainId", domainId);
		sb.append(")");
		super.bulkUpdate(sb.toString(), parameter);
	}

	public void updateAssetManager(Long adminId, Long domainId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> parameter = new HashMap<String, Object>();
		sb
				.append("update MAssetInfo m set m.assetMge = :perId where m.domainId = :accoundId and m.assetMge not in(select distinct id.admin from MAdminSetting where id.admin=m.assetMge  and adminModel like :adminModel and domainId = :domainId ");
		parameter.put("perId", adminId);
		parameter.put("accoundId", domainId);
		parameter.put("adminModel", "_1___");
		parameter.put("domainId", domainId);
		sb.append(")");
		super.bulkUpdate(sb.toString(), parameter);
	}

	public void updateStockManager(Long adminId, Long domainId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> parameter = new HashMap<String, Object>();
		sb
				.append("update StockInfo m set m.stockRes = :perId where m.accountId  = :accoundId and m.stockRes not in(select distinct id.admin from MAdminSetting where id.admin=m.stockRes  and adminModel like :adminModel and domainId = :domainId ");
		parameter.put("perId", adminId);
		parameter.put("accoundId", domainId);
		parameter.put("adminModel", "__1__");
		parameter.put("domainId", domainId);
		sb.append(")");
		super.bulkUpdate(sb.toString(), parameter);
	}

	public void updateBookManager(Long adminId, Long domainId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> parameter = new HashMap<String, Object>();
		sb
				.append("update MBookInfo m set m.bookMge = :perId where m.domainId  = :accoundId and  m.bookMge not in (select distinct id.admin from MAdminSetting where id.admin=m.bookMge  and adminModel like :adminModel and domainId = :domainId ");
		parameter.put("perId", adminId);
		parameter.put("accoundId", domainId);
		parameter.put("adminModel", "___1_");
		parameter.put("domainId", domainId);
		sb.append(")");
		super.bulkUpdate(sb.toString(), parameter);
	}

	public void updateMeetingManager(Long adminId, Long domainId) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object> parameter = new HashMap<String, Object>();
		sb
				.append("update MeetingRoom m set m.v3xOrgMember.id = :perId where m.accountId = :accoundId and m.v3xOrgMember.id not in (select distinct id.admin from MAdminSetting where id.admin=m.v3xOrgMember.id  and adminModel like :adminModel and domainId = :domainId ");
		parameter.put("perId", adminId);
		parameter.put("accoundId", domainId);
		parameter.put("adminModel", "____1");
		parameter.put("domainId", domainId);
		sb.append(")");
		super.bulkUpdate(sb.toString(), parameter);
	}

	public List findAdminManageDepartment(Long adminId, Long accountId,
			String model) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MAdminSetting.class);
		criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
		criteria.add(Restrictions.eq("id.admin", adminId));
		criteria.add(Restrictions.eq("domainId", accountId));
		criteria.add(Restrictions.like("adminModel", model));
		return super.executeCriteria(criteria, -1, -1);
	}
	
	public boolean checkAdmin(Long id){
		DetachedCriteria criteria = DetachedCriteria.forClass(MAdminSetting.class);
		criteria.add(Restrictions.eq("id.admin", id));
		criteria.add(Restrictions.like("adminModel", "____1"));
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean checkAdmin(Long userId, Long loginAccountId) {
		if(null == loginAccountId){
			return this.checkAdmin(userId);
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MAdminSetting.class);
		criteria.add(Restrictions.eq("id.admin", userId));
		criteria.add(Restrictions.eq("accountId", loginAccountId));
		criteria.add(Restrictions.like("adminModel", "____1"));
		criteria.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteria)).intValue();
		if(count == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public List getMyAdmin(List departmentId) {
		List dList = new ArrayList();
		for(int i = 0; i < departmentId.size(); i++){
			dList.add(String.valueOf(departmentId.get(i)));
		}
		DetachedCriteria criteriaCount = DetachedCriteria.forClass(MAdminSetting.class);
		criteriaCount.add(Restrictions.like("adminModel", "____1"));
		criteriaCount.add(Restrictions.in("id.mngdepId", dList));
		criteriaCount.setProjection(Projections.rowCount());
		int count = ((Integer)super.executeUniqueCriteria(criteriaCount));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MAdminSetting.class);
		criteria.add(Restrictions.like("adminModel", "____1"));
		criteria.add(Restrictions.in("id.mngdepId", dList));
		
		List list = super.executeCriteria(criteria, 0, count);
		List pId = new ArrayList();
		if(list != null && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				MAdminSetting madmin = (MAdminSetting)list.get(i);
				pId.add(madmin.getId().getAdmin());
			}
		}
		return pId;
	}

	/** branches_a8_v350_r_gov 向凡  Start*/
	@Override
	public List<MAdminSetting> listAdminSetting(String model, Long domainId, String field, String keyword) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select a from MAdminSetting a, V3xOrgMember m where m.id = a.id.admin and a.accountId = :accountId and a.adminModel like :adminModel");
		params.put("adminModel", "%" + model);// 'XXXX1' 表示会议室管理模块,只要最后一个数字为'1' 就包含会议室模块
		params.put("accountId", domainId);
		if(Strings.isNotBlank(field) && Strings.isNotBlank(keyword)){
			int condition = Integer.parseInt(field);
			switch (condition) {
			case com.seeyon.v3x.office.admin.util.Constants.Search_Condition_Admin:
				hql.append(" and a.id.admin = :adminId");
				params.put("adminId", Long.parseLong(keyword));
				break;
			case com.seeyon.v3x.office.admin.util.Constants.Search_Condition_Depart:
				hql.append(" and a.id.mngdepId = :mngdepId");
				params.put("mngdepId", keyword);
				break;
			}
		}
		List<MAdminSetting> list = (List<MAdminSetting>)super.find(hql.toString(), -1, -1, params);//branches_a8_v350sp1_r_gov 向凡 修改，修复GOV-1789 不能分页的问题
		return list;
	}
	/** branches_a8_v350_r_gov 向凡  End*/
	
}