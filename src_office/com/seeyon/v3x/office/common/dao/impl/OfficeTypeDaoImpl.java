package com.seeyon.v3x.office.common.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.office.asset.domain.MAssetInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.book.domain.MBookInfo;
import com.seeyon.v3x.office.common.dao.OfficeTypeDao;
import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.office.stock.domain.StockInfo;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class OfficeTypeDaoImpl extends BaseHibernateDao implements OfficeTypeDao {

	public int deleteOfficeTypeByIds(final String typeIds) {		
			String sql = "update OfficeTypeInfo type set type.deleteFlag=:delFlag where type.typeId in (:typeId) "	;
			Map map = new HashMap()	;
			String[] ids = typeIds.split(",");
			Long[] idsl = new Long[ids.length];
			for(int i = 0 ; i <ids.length ; i++){
				idsl[i] = Long.parseLong(ids[i]);
			}
			map.put("delFlag", 1);
			map.put("typeId", idsl);
			return   super.bulkUpdate(sql, map);		
		/*return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				String strSql = "update OfficeTypeInfo type set type.deleteFlag=1 where type.typeId in ("+typeIds+")";
				return session.createQuery(strSql)
							       .executeUpdate();
			}
		});	*/
	}

	public List findTypeOfModel(final String modelId,final long departId){
		DetachedCriteria criteria = DetachedCriteria.forClass(OfficeTypeInfo.class);
		criteria.add(Restrictions.eq("deleteFlag", 0));
		criteria.add(Restrictions.eq("departId", departId));
		criteria.add(Restrictions.eq("modelId", modelId));
		return super.executeCriteria(criteria,-1,-1);
		/*return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(final Session session) throws HibernateException, SQLException {
				final String sql = "select * from M_Type_Info where del_flag=0 and dep_id="+departId+" and model_id='"+modelId+"'";
			
				final SQLQuery query = session.createSQLQuery(sql);
				
				query.addEntity(OfficeTypeInfo.class);
				return (List)query.list();
			}
		});	*/
	}
	
	public List findTypeOfAuto(final String modelId,final long departId){
		DetachedCriteria criteria = DetachedCriteria.forClass(OfficeTypeInfo.class);
		criteria.add(Restrictions.eq("deleteFlag", 0));
		criteria.add(Restrictions.eq("departId", departId));
		criteria.add(Restrictions.eq("modelId", modelId));
		return super.executeCriteria(criteria,-1,-1);
		/*return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String sql = "select * from M_Type_Info where del_flag=0 and dep_id="+departId+" and model_id='"+modelId+"'";
				
				SQLQuery query = session.createSQLQuery(sql);
				
				query.addEntity(OfficeTypeInfo.class);
				return query.list();
			}
		});	*/
	}
	
	public List findTypeInfoList(final String fieldName,final String fieldValue,final long departId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(OfficeTypeInfo.class);
		criteria.add(Restrictions.eq("deleteFlag", 0));
		criteria.add(Restrictions.eq("departId", departId));
		if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)){
			if(fieldName.equals("model_id")){
				criteria.add(Restrictions.eq("modelId", fieldValue));
			}else if(fieldName.equals("type_info")){
				criteria.add(Restrictions.like("typeInfo", "%"+SQLWildcardUtil.escape(fieldValue)+"%"));
			}
		}
		return super.executeCriteria(criteria);
		/*return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String countSql = "select count(model_id) as myTotalCount from m_type_info where del_flag=0 and dep_id="+departId;
				if(!"".equals(fieldName) && !"".equals(fieldValue)){
					countSql+=" and ("+fieldName +" like "+"'%"+fieldValue+"%')";
				}
                int size = getCount(countSql);
                
//				if(!Pagination.isNeedCount().booleanValue()){
					Pagination.setRowCount(size);
//				}
				
				String sql = "select * from M_Type_Info where del_flag=0 and dep_id="+departId;
				if(!"".equals(fieldName) && !"".equals(fieldValue)){
					sql+=" and ("+fieldName +" like "+"'%"+fieldValue+"%')";
				}
				sql+=" order by model_id";
				
				SQLQuery query = session.createSQLQuery(sql);
				
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				query.addEntity(OfficeTypeInfo.class);
				
				//session.close();
				
				//return this.paginate(query);
				return query.list();
			}
		});	*/
	}

	
	public boolean checkDuplicate(final OfficeTypeInfo typeInfo,final boolean bCheckSelf){
		DetachedCriteria criteria = DetachedCriteria.forClass(OfficeTypeInfo.class);
		criteria.add(Restrictions.eq("deleteFlag", 0));
		criteria.add(Restrictions.eq("modelId", typeInfo.getModelId()));
		criteria.add(Restrictions.eq("departId", typeInfo.getDepartId()));
		criteria.add(Restrictions.eq("typeInfo", typeInfo.getTypeInfo()));
		if(bCheckSelf){
			criteria.add(Expression.not(Restrictions.eq("typeId", typeInfo.getTypeId())));
		}
		Integer totalCount = super.getCountByCriteria(criteria);
		if(totalCount.intValue()>0){
			return true;
		}else{
			return false;
		}
		/*
		return (Boolean)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
		String sql = "select count(type_id) as myTotalCount from m_type_info where model_id='"+typeInfo.getModelId()+"' and dep_id="+typeInfo.getDepartId()+" and type_info='"+typeInfo.getTypeInfo().trim()+"' and del_flag=0";
		
		if(bCheckSelf){
			sql += " and type_id<>"+typeInfo.getTypeId();
		}
		SQLQuery query = session.createSQLQuery(sql);
		
		Integer totalCount = (Integer)query.addScalar("myTotalCount", Hibernate.INTEGER).uniqueResult();
		//int totalCount = this.getQueryCount(sql, null, null);
		if(totalCount.intValue()>0){
			return true;
		}else{
			return false;
		}
			}
		});	
		*/
	}
	public void createTypeInfo(OfficeTypeInfo typeInfo) {
		super.save(typeInfo);
	}
	
	public void updateTypeInfo(OfficeTypeInfo typeInfo){
		super.update(typeInfo);
	}
	
	public OfficeTypeInfo findOfficeTypeById(Long typeId){
		return (OfficeTypeInfo)this.getHibernateTemplate().get(OfficeTypeInfo.class, typeId);
	}
	
	
	/**
	 * 计算总行数
	 * @param sql
	 * @return.
	 */
	public int getCount(final String sql){
		return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				Integer totalCount = (Integer)query.addScalar("myTotalCount", Hibernate.INTEGER).uniqueResult();
				//return this.getQueryCount(sql, null, null);
				return totalCount.intValue();
			}
		});	
	}

	/***
	 * 综合办公撤销申请的判断
	 * @param  t_applylist
	 * @teturn int
	 */
	public int getSelectAutoAudit(final String applyId){
		DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
		criteria.add(Restrictions.eq("delFlag", 1));
		criteria.add(Restrictions.eq("applyId", Long.parseLong(applyId)));
		return super.getCountByCriteria(criteria);
    	/*return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer();
				sb.append("select count(apply_id) as myTotalCount from t_applylist  where apply_id ='" + applyId.trim() + "' and del_flag = 1");
				String sql = sb.toString();
				SQLQuery query = session.createSQLQuery(sql);
				Integer totalCount = (Integer)query.addScalar("myTotalCount", Hibernate.INTEGER).uniqueResult();
				return totalCount.intValue();
			}
		});	*/
    }

    public boolean checkAutoTypeInOffice(Long typeId,OfficeTypeInfo officeTypeInfo)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(AutoInfo.class);
        criteria.add(Restrictions.eq("deleteFlag", com.seeyon.v3x.office.myapply.util.Constants.Del_Flag_Normal));
        criteria.add(Restrictions.eq("officeType", officeTypeInfo));
        List l = getHibernateTemplate().findByCriteria(criteria);
        if(l==null || l.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkAssetTypeInOffice(Long typeId,OfficeTypeInfo officeTypeInfo)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MAssetInfo.class);
        criteria.add(Restrictions.eq("delFlag", com.seeyon.v3x.office.myapply.util.Constants.Del_Flag_Normal));
        criteria.add(Restrictions.eq("officeType", officeTypeInfo));
        List l = getHibernateTemplate().findByCriteria(criteria);
        if(l==null || l.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkBookTypeInOffice(Long typeId,OfficeTypeInfo officeTypeInfo)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(MBookInfo.class);
        criteria.add(Restrictions.eq("delFlag", com.seeyon.v3x.office.myapply.util.Constants.Del_Flag_Normal));
        criteria.add(Restrictions.eq("officeType", officeTypeInfo));
        List l = getHibernateTemplate().findByCriteria(criteria);
        if(l==null || l.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean checkStockTypeInOffice(Long typeId,OfficeTypeInfo officeTypeInfo)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(StockInfo.class);
        criteria.add(Restrictions.eq("deleteFlag", com.seeyon.v3x.office.myapply.util.Constants.Del_Flag_Normal));
        criteria.add(Restrictions.eq("officeType", officeTypeInfo));
        List l = getHibernateTemplate().findByCriteria(criteria);
        if(l==null || l.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public OfficeTypeInfo getOfficeTypeInfoById(Long typeId)
    {
        DetachedCriteria criteria = DetachedCriteria.forClass(OfficeTypeInfo.class);
        criteria.add(Restrictions.eq("typeId", typeId));
        criteria.add(Restrictions.eq("deleteFlag", com.seeyon.v3x.office.myapply.util.Constants.Del_Flag_Normal));
        List l = super.executeCriteria(criteria,-1,-1);
        if(l==null || l.isEmpty())
            return null;
        return (OfficeTypeInfo)l.get(0);
    }
	
    
    public List<OfficeTypeInfo> getAll(String hql) {
    	 return this.find(hql) ;
    }
}
