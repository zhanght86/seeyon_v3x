package com.seeyon.v3x.office.book.dao.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.office.book.dao.BookApplyInfoDAO;
import com.seeyon.v3x.office.book.domain.TBookApplyinfo;
import com.seeyon.v3x.office.book.util.Constants;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class BookApplyInfoDAOImpl extends BaseHibernateDao<TBookApplyinfo>
		implements BookApplyInfoDAO {
	public void save(TBookApplyinfo tBookApplyInfo) {
		super.save(tBookApplyInfo);
	}

	public void update(TBookApplyinfo tBookApplyInfo) {
		super.update(tBookApplyInfo);
	}

	public SQLQuery find(String sql) {
		Session session = super.getSession();
		SQLQuery query = null;
		try {
			query = session.createSQLQuery(sql);
		} catch (Exception ex) {

		} finally {
			super.releaseSession(session);
		}
		return query;
	}

	public int getCount(String sql) {
		Session session = super.getSession();
		SQLQuery query = null;
		try {
			query = super.getSession().createSQLQuery(sql);
		} catch (Exception ex) {

		} finally {
			super.releaseSession(session);
		}
		int totalCount = (Integer) query.addScalar(Constants.Total_Count_Field,
				Hibernate.INTEGER).uniqueResult();
		return totalCount;
	}

	public TBookApplyinfo load(long id) {
		return (TBookApplyinfo) this.getHibernateTemplate().load(
				TBookApplyinfo.class, new Long(id));
	}

	public List listBookPerm(String field, String fieldValue, Long adminId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
		criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
		criteria.add(Restrictions.eq("applyType",
				com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Book));

		criteria.add(Restrictions.eq("applyMge", adminId));
		if (Strings.isNotBlank(field) && Strings.isNotBlank(fieldValue)) {
			int fieldInt = Integer.parseInt(field);
			switch (fieldInt) {
			case Constants.Search_Condition_BookName:// 1
				criteria
						.add(Restrictions
								.sqlRestriction(
										"{alias}.apply_id in (select tba.apply_id from t_book_applyinfo tba where book_id in (select book_id from m_book_info where book_name like ? )) ",
										"%"
												+ SQLWildcardUtil
														.escape(fieldValue.trim())
												+ "%", Hibernate.STRING));
				break;
			case Constants.Search_Condition_Department:// 5
				criteria.add(Restrictions.eq("applyDepId", Long.parseLong(fieldValue)));
				break;
			case Constants.Search_Condition_Member:// 6
				criteria.add(Restrictions.eq("applyUsername",Long.parseLong(fieldValue)));
				break;
			case Constants.Search_Condition_ApplyStat:// 7
				criteria.add(Restrictions.eq("applyState", Integer.parseInt(fieldValue)));
				break;
			}
		}
		criteria.addOrder(Order.desc("applyDate"));
		return super.executeCriteria(criteria);
	}

	public List listBookStroage(String field, String fieldValue, List applyId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
		
		criteria.add(Restrictions.eq("applyType", com.seeyon.v3x.office.myapply.util.Constants.ApplyType_Book));
		criteria.add(Restrictions.eq("applyState",
				com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow));
		criteria.add(Restrictions.in("applyId", applyId));
		
		if (Strings.isNotBlank(field) && Strings.isNotBlank(fieldValue)) {
			int fieldInt = Integer.parseInt(field);
			switch (fieldInt) {
			case Constants.Search_Condition_Member:// 6
				criteria.add(Restrictions.eq("applyUsername",Long.parseLong(fieldValue)));
				break;
			case Constants.Search_Condition_SorageStat:// 7
				int key = Integer.parseInt(fieldValue);
				switch(key){
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Depart:
					criteria.add(Restrictions.sqlRestriction("{alias}.apply_id in ( select tbd.apply_id from t_book_departinfo tbd where Book_departtime is not null and Book_backtime is null )"));
					break;
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Back:
					criteria.add(Restrictions.sqlRestriction("{alias}.apply_id in ( select tbd.apply_id from t_book_departinfo tbd where  Book_backtime is not null )"));
					break;
				case com.seeyon.v3x.office.myapply.util.Constants.ApplyStatus_Allow:
					criteria.add(Restrictions.sqlRestriction("{alias}.apply_id not in ( select tbd.apply_id from t_book_departinfo tbd )"));
					break;
				}
				//criteria.add(Restrictions.sqlRestriction(""));
				break;
			}
		}
		criteria.addOrder(Order.desc("applyDate"));
		return super.executeCriteria(criteria);
	}
	
	public List listBookApplyByIds(Long adminId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(TBookApplyinfo.class);
		criteria.add(Restrictions.eq("delFlag", 0));
		criteria.add(Restrictions.sqlRestriction(" book_id in (select book_id from m_book_info where del_flag=? and book_mge=? ) ",new Object[]{0,adminId},new Type[]{Hibernate.INTEGER,Hibernate.LONG}));
		return super.executeCriteria(criteria,-1,-1);
	}
}
