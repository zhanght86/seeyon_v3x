package com.seeyon.v3x.office.stock.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.admin.util.Constants;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.stock.dao.StockDao;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;


/**
 *
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 *
 */
public class StockDaoImpl extends BaseHibernateDao implements StockDao {
	

	
	/**
	 *  根据办公用品编码取得办公用品详细信息
	 * @param stockId	办公用品编号
	 * @return  办公用品详细信息对象
	 * @throws BusinessException  异常
	 */
	public StockInfo findStockInfoById(Long stockId){
		return (StockInfo) getHibernateTemplate().get(StockInfo.class, stockId);
	}
	
	/**
	 * 保存办公用品
	 * @param stockInfo  办公用品详细信息对象
	 * @return  
	 * @throws BusinessException  异常
	 */
	public void createStockInfo(StockInfo stockInfo){
		super.save(stockInfo);
		
	}
	
	
	public void updateStockInfo(StockInfo stockInfo){
		super.update(stockInfo);
		//getHibernateTemplate().update(stockInfo);
	}

	/**
	 * 取得管理者负责的办公用品详细信息一览列表
	 * @param managerId
	 * @return
	 */
	public List findStockListByManager(final String fieldName,final String fieldValue,final Long managerId){
		DetachedCriteria criteria = DetachedCriteria.forClass(StockInfo.class);
		criteria.add(Restrictions.eq("deleteFlag", 0));
//		criteria.add(Restrictions.eq("stockRes", managerId));
		if(managerId.longValue() == -1){
			criteria.add(Restrictions.eq("stockState", 0));
		}
		if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)){
			if("stockState".equals(fieldName)){
				criteria.add(Restrictions.eq("stockState", Integer.parseInt(fieldValue)));
			}else if("stockId".equals(fieldName)){
				//criteria.add(Restrictions.eq("stockId", Long.parseLong(fieldValue)));
				criteria.add(Restrictions.sqlRestriction(" stock_id like ?  ","%"+SQLWildcardUtil.escape(fieldValue)+"%",Hibernate.STRING));
			}else if("officeType.typeId".equals(fieldName)){ 
				criteria.add(Restrictions.sqlRestriction(" stock_type = '"+fieldValue+"' "));
				//criteria.add(Restrictions.sqlRestriction(" stock_type = ? ",Long.parseLong(fieldValue),Hibernate.LONG));
			}else{
				criteria.add(Restrictions.like(fieldName, "%"+com.seeyon.v3x.util.SQLWildcardUtil.escape(fieldValue)+"%"));
			}
		}
		criteria.add(Restrictions.eq("stockRes", CurrentUser.get().getId()));
		criteria.addOrder(Order.desc("stockId"));
		return super.executeCriteria(criteria);
		/*
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){
			
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				String fieldValue2 = fieldValue.trim();
				
				StringBuffer sb = new StringBuffer();
				sb.append(" from m_stock_info where del_flag=0 and stock_res="+managerId);//之用管理员ID查询
				
				if(managerId.longValue()==-1){
					sb.append(" and stock_state=0 ");
				}
				if(!"".equals(fieldName) && !"".equals(fieldValue)){
					sb.append(" and ("+fieldName +" like "+"'%"+fieldValue2+"%')");
				}
				
				
				String countSql = "select count(stock_id) as myTotalCount" +sb.toString();
					int size = getCount(countSql);
				Pagination.setRowCount(size);
				SQLQuery query = session.createSQLQuery("select * " + sb.toString()+" order by STOCK_ID desc ");
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(size);
				
				query.addEntity(StockInfo.class);
				
				//return this.paginate(query);
				return query.list();
			}
		});	*/
		
	}
	
	/**
	 * 取得可以申请的办公用品详细信息一览列表
	 * @param managerId
	 * @return
	 */
	public List findStockApplyList(final List mgrIds,final String fieldName,final String fieldValue,final Long managerId){
		User user = CurrentUser.get();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(StockInfo.class);
		criteria.add(Restrictions.eq("deleteFlag", 0));
//		criteria.add(Restrictions.in("stockRes", mgrIds));
		//criteria.add(Restrictions.eq("accountId", user.getLoginAccount()));
		if(managerId.longValue()==-1){
			criteria.add(Restrictions.eq("stockState", 0));
		}
		if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)){
			if("stockId".equals(fieldName)){
				//criteria.add(Restrictions.eq("stockId", Long.parseLong(fieldValue)));
				criteria.add(Restrictions.sqlRestriction(" stock_id like ?  ","%"+SQLWildcardUtil.escape(fieldValue)+"%",Hibernate.STRING));
			}else if("stockType".equals(fieldName)){
				criteria.add(Restrictions.sqlRestriction(" stock_type = '"+fieldValue+"' "));
				//criteria.add(Restrictions.sqlRestriction(" stock_type = '-305761510277365088'")) ;
				//criteria.add(Restrictions.sqlRestriction(" stock_type = '"+ Long.parseLong(fieldValue) +"' ") ;
				//criteria.add(Restrictions.sqlRestriction("stock_type  = ?" ,)) ;
			}else{
				criteria.add(Restrictions.like(fieldName, "%"+SQLWildcardUtil.escape(fieldValue.trim())+"%"));
			}
		}
		criteria.add(Restrictions.in("stockRes",mgrIds));
		criteria.addOrder(Order.desc("stockId"));
		return super.executeCriteria(criteria);
		/*
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				//取得部门ID
				
				String fieldValue2 = fieldValue.trim();
				StringBuffer sb = new StringBuffer();
				User user = CurrentUser.get();
				
				sb.append(" from m_stock_info where del_flag=0");
				sb.append(" and stock_res in ("+mgrIds+")");
				sb.append(" and accountId="+user.getLoginAccount());

				if(managerId.longValue()==-1){
					sb.append(" and stock_state=0 ");
				}
				if(!"".equals(fieldName) && !"".equals(fieldValue)){
					sb.append(" and ("+fieldName +" like "+"'%"+fieldValue2+"%')");
				}
				
				String sql = "select * "+sb.toString()+" order by STOCK_ID desc ";
				SQLQuery query = session.createSQLQuery(sql);
				query.addEntity(StockInfo.class);
				return query.list();
			}
		});	*/
		
	}
	/**
	 * 批量修改办公用品的删除状态为1 2008-6-16 by Yongzhang 修改update语句
	 * @param stockIds  办公用品编号集 格式：1000,10002,1004
	 * @return  int 数据库修改结果
     * 
	 */
	public int deleteStockInfobyIds(final List stockIds) {
		
       
		return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String strSql = "update StockInfo stock set stock.deleteFlag=1 where stock.stockId in (:stockIds)";
				return session.createQuery(strSql)
                                   .setParameterList("stockIds",stockIds)
							       .executeUpdate();
			}
		});	
		
	}


    /**
	 * 单个修改单条办公用品的删除状态为1
	 * 
	 * @param stockId  办公用品编号  1000
	 * @return int 数据库修改结果
	 */
	public int deleteStockInfoById(final String stockId){
		return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String strSql = "update StockInfo stock set stock.deleteFlag=1 where stock.stockId=:stockId";
				
				return session.createQuery(strSql)
								   .setString("stockId", stockId)
							       .executeUpdate();
			}
		});	
	}
	
	
	/**
	 * 新增办公用品详细申请单
	 * @param stockApply  办公用品详细申请单
	 * @return  申请编号
	 */
	public void createStockApply(StockApplyInfo stockApply){
		super.save(stockApply);
	}
	
	/**
	 * 修改办公用品详细申请单信息 办公用品详细申请单
	 * @param stockApply  
	 */
	public void updateStockApply(StockApplyInfo stockApply){
		super.update(stockApply);
	}
	

	/**
	 * 根据申请编号取得办公用品详细申请单情报
	 * @param applyId  申请编号
	 * @return  办公用品详细申请单情报
	 */
	public StockApplyInfo findStockApplyById(Long applyId){
		
		return (StockApplyInfo)this.getHibernateTemplate().get(StockApplyInfo.class, applyId);
	}
	
	/**
	 * 取得待审核的办公用品申请列表
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId
	 * @return
	 */
	public List findStockApplyListForAutdit(final String fieldName,final String fieldValue,final Long managerId){
		StringBuffer hql = new StringBuffer();
		List<Object> parameter = new ArrayList<Object>();
		
		hql.append("select t.applyId as applyid,s.stockId as stockid,s.stockName as stockname,m.name as username,m,t.applyState as applystate ");
		hql.append(" from TApplylist t,StockApplyInfo a,StockInfo s,V3xOrgMember m,V3xOrgDepartment d ");
		hql.append(" where t.applyId=a.applyId and a.stockId = s.stockId and t.applyUsername=m.id and t.applyDepId=d.id ");
		hql.append(" and t.applyMge=? and t.delFlag=? and s.deleteFlag=?  ");
		//hql.append(" and ( t.applyDepId in (:applyDepId)"); 
		//hql.append(" or m.id in(select distinct sourceId from V3xOrgRelationship where depId in(:applyDepId) and (type=:type1 or type=:type2) ) ");//兼职副职
		//hql.append("  )");
		parameter.add(managerId);
		parameter.add(Constants.Del_Flag_Normal);
		parameter.add(Constants.Del_Flag_Normal);
		Map<String,Object> map = new HashMap<String,Object>();
		//map.put("applyDepId",departmentId);
		//map.put("type1",V3xOrgEntity.ORGREL_TYPE_MEMBER_POST);//副岗
		//.put("type2",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);//兼职  //暂不处理
		if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue)){
			if(fieldName.equals("userName")){
				hql.append(" and m.id = ? ");
				parameter.add(Long.parseLong(fieldValue));
			}else
			if(fieldName.equals("departId")){
				hql.append(" and d.id = ? ");
				parameter.add(Long.parseLong(fieldValue));
			}else
			if(fieldName.equals("stockid")){
				hql.append(" and s.stockId  like '%"+SQLWildcardUtil.escape(fieldValue)+"%' ");
				//parameter.add(Long.parseLong(fieldValue));
			}else 
			if(fieldName.equals("stockName")){
				hql.append(" and s.stockName like ? ");
				parameter.add("%"+SQLWildcardUtil.escape(fieldValue)+"%");
			}else
			if(fieldName.equals("applyState")){
				hql.append(" and t.applyState = ? ");
				parameter.add(Integer.parseInt(fieldValue));
			}
		}
		return super.find(hql.toString(), map,parameter);
		/*
		
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String fieldValue2 = fieldValue.trim();
				StringBuffer sb = new StringBuffer();
				Map parameter = new HashMap();
				sb.append(" from T_applyList t,T_Stock_ApplyInfo a,m_stock_info s,v3x_org_member m,v3x_org_department d");
				sb.append(" where t.apply_id=a.apply_id");
				sb.append(" and a.stock_id=s.stock_id");
                sb.append(" and t.apply_username=m.id");
                sb.append(" and t.apply_depid=d.id");
				sb.append(" and t.apply_mge=:managerId");
				parameter.put("managerId", managerId);
                sb.append(" and t.del_flag=0");
                //stockInfo的删除标识是0为正常，不然删除的办公用品也会出现审核 2008-04-30 by Yongzhang
                sb.append(" and s.del_flag=0");
                Edit BY BIANTENG for "display all StockAuditList" start
				if(!"t.apply_state".equalsIgnoreCase(fieldName)){
					sb.append(" and t.apply_state=1");
                  
				}
                Edit BY BIANTENG end
				if(Strings.isNotBlank(fieldName) && Strings.isNotBlank(fieldValue2)){
					sb.append(" and ("+fieldName +" like :fieldValue2)");
					parameter.put("fieldValue2", "%"+SQLWildcardUtil.escape(fieldValue2.trim())+"%");
                    //by Yongzhang 2008-05-09
//                    if("t.apply_state".equalsIgnoreCase(fieldName))
//                    {
//                        if(fieldValue2.equals("1"))
//                        {
//                            sb.append(" and t.del_flag=0");
//                        }
//                    }
				}
				
				String countSql = "select count(*) as myTotalCount "+sb.toString();
				
//				if(!Pagination.isNeedCount().booleanValue()){
					
//				}
				sb=null;//清内存 2008-04-30 by Yongzhang
				 sb = new StringBuffer();
					sb.append(" from T_applyList t,T_Stock_ApplyInfo a,m_stock_info s,v3x_org_member m,v3x_org_department d");
					sb.append(" where t.apply_id=a.apply_id");
					sb.append(" and a.stock_id=s.stock_id");
					sb.append(" and t.apply_username=m.id");
					sb.append(" and t.apply_depid=d.id");
					sb.append(" and t.apply_mge="+managerId);
                    sb.append(" and t.del_flag=0");
                      //stockInfo的删除标识是0为正常，不然删除的办公用品也会出现审核 2008-04-30 by Yongzhang
                    sb.append(" and s.del_flag=0");
                    Edit BY BIANTENG for "display all StockAuditList" start
					if(!"t.apply_state".equalsIgnoreCase(fieldName)){
						sb.append(" and t.apply_state=1");
                        
					}
					Edit BY BIANTENG end
					if(!"".equals(fieldName) && !"".equals(fieldValue)){
						
						sb.append(" and ("+fieldName +" like "+"'%"+fieldValue+"%' )");

                        //by Yongzhang
//                          if("t.apply_state".equalsIgnoreCase(fieldName))
//                            {
//                                if(fieldValue2.equals("1"))
//                                {
//                                    sb.append(" and t.del_flag=0");
//                                }
//                            }
					}
				//String sql = "select {t.*},{s.*},{m.*},{d.*} "+sb.toString() +" order by t.apply_id desc";
				String sql = "select t.apply_id as applyid,s.stock_id as stockid,s.stock_name as stockname,m.name as username,d.name as departname,t.apply_state as applystate "+sb.toString() +" order by t.apply_id desc";
				SQLQuery query = session.createSQLQuery(sql);
				SQLQuery countQuery = session.createSQLQuery(countSql);
				
				if(parameter !=null){
					Iterator iterator = parameter.keySet().iterator();
					String key;
					for(;iterator.hasNext();){
						key = iterator.next().toString();
						query.setParameter(key, parameter.get(key));
						countQuery.setParameter(key, parameter.get(key));
					}
				}
				
				int size = (Integer)countQuery.addScalar("myTotalCount",Hibernate.INTEGER).uniqueResult();
				
				Pagination.setRowCount(size);
				query.setFirstResult(Pagination.getFirstResult());
				query.setMaxResults(Pagination.getMaxResults());
				
				//query.addEntity(OfficeApply.class);
				//query.addEntity(StockInfo.class);
				//query.addEntity(V3xOrgMember.class);
				//query.addEntity(V3xOrgDepartment.class);
				query.addScalar("applyid",Hibernate.LONG);
				query.addScalar("stockid",Hibernate.STRING);
				query.addScalar("stockname",Hibernate.STRING);
				query.addScalar("username",Hibernate.STRING);
				query.addScalar("departname",Hibernate.STRING);
				query.addScalar("applystate",Hibernate.LONG);
				
				return query.list();
			}
		});	*/
	}
	/**
	 * 取得记录总行数
	 * @param sql
	 * @return
	 */
	private int getCount(final String sql){
		return (Integer)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery query = session.createSQLQuery(sql);
				Integer totalCount = (Integer)query.addScalar("myTotalCount", Hibernate.INTEGER).uniqueResult();
				return totalCount.intValue();
			}
		});	
	}
	
	/**
	 * 取得办公用品最大编号
	 * @return
	 */
	public Long getMaxStockNo(){
		return (Long)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
		try{
			String sql = "select max(stock_id) as maxNo from m_stock_info";
			SQLQuery query = session.createSQLQuery(sql);
			Long maxNo = (Long)query.addScalar("maxNo", Hibernate.LONG).uniqueResult();
			if(maxNo==null){
				maxNo =new Long(20000000);
			}
			if(maxNo.longValue()<20000000){
				maxNo = new Long(20000000);
			}
			maxNo= Long.valueOf(maxNo.longValue()+1);
			return maxNo;
		}catch(Exception e){
			return new Long(20000000);
		}
			}
		});	
	}
	
	/**
     * 按部门统计办公用品
     * @return
     */
	public List getStockSummayByDep(final boolean needPage) {
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer();
				User user = CurrentUser.get();
				Calendar ca = Calendar.getInstance();
				//取得当前月份第一天
				Date firstDayOfMonth =Datetimes.getFirstDayInMonth(ca.getTime());
				
				//取得当前月份最后一天
				Date lastDayOfMonth =Datetimes.getLastDayInMonth(ca.getTime());
				
				Date firstDayOfWeek =Datetimes.getFirstDayInWeek(ca.getTime());
				Date lastDayOfWeek =Datetimes.getLastDayInWeek(ca.getTime());
				
				sb.append("select t.dname,t.stock_name,w.count as wcount,m.count as mcount,t.count as count");
				sb.append(" from");
				sb.append(" (select d.name as dname,c.stock_name as stock_name,count(*) as count");
				sb.append(" from t_stock_applyinfo a,t_applylist b,m_stock_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.stock_id=a.stock_id and d.id=b.apply_depid and c.stock_res=? and c.del_flag=0");
				sb.append(" group by d.name,c.stock_name) t ");
				sb.append(" left join (select d.name as dname,c.stock_name as stock_name,count(*) as count");
				sb.append(" from t_stock_applyinfo a,t_applylist b,m_stock_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.stock_id=a.stock_id and d.id=b.apply_depid");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.stock_name) w  on t.dname=w.dname and t.stock_name=w.stock_name ");
				sb.append(" left join (select d.name as dname,c.stock_name as stock_name,count(*) as count");
				sb.append(" from t_stock_applyinfo a,t_applylist b,m_stock_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.stock_id=a.stock_id and d.id=b.apply_depid");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.stock_name) m on t.dname=m.dname and t.stock_name=m.stock_name");
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
				SQLQuery query =session.createSQLQuery(sql);
				if(needPage){
					query.setFirstResult(Pagination.getFirstResult());
					query.setMaxResults(Pagination.getMaxResults());
				}
				query.setLong(0, user.getId());
				query.setDate(1, firstDayOfWeek);
				query.setDate(2, lastDayOfWeek);
				query.setDate(3, firstDayOfMonth);
				query.setDate(4, lastDayOfMonth);
				//query.setInteger(4, 2);
				
				
				query.addScalar("dname", Hibernate.STRING);
				query.addScalar("stock_name", Hibernate.STRING);
				query.addScalar("wcount",Hibernate.INTEGER);
				query.addScalar("mcount",Hibernate.INTEGER);
				query.addScalar("count",Hibernate.INTEGER);
				return query.list();
			}
		});
	}

	/**
	 * 取得办公用品统计
	 * @return
	 */
	public List getStockSummay(final boolean needPage){
		
		return (List)this.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				StringBuffer sb = new StringBuffer();
				User user = CurrentUser.get();
				Calendar ca = Calendar.getInstance();
				//取得当前月份第一天
				Date firstDayOfMonth =Datetimes.getFirstDayInMonth(ca.getTime());
				
				//取得当前月份最后一天
				Date lastDayOfMonth =Datetimes.getLastDayInMonth(ca.getTime());
				
				Date firstDayOfWeek =Datetimes.getFirstDayInWeek(ca.getTime());
				Date lastDayOfWeek =Datetimes.getLastDayInWeek(ca.getTime());
				
				sb.append("select t.dname,t.department_name,t.stock_name,w.count as wcount,m.count as mcount,t.count as count");
				sb.append(" from");
				sb.append(" (select d.name as dname,c.stock_name as stock_name,e.name as department_name,count(*) as count");
				sb.append(" from t_stock_applyinfo a,t_applylist b,m_stock_info c,v3x_org_member d,v3x_org_department e");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.stock_id=a.stock_id and d.id=b.apply_username and d.org_department_id=e.id and c.stock_res=? and c.del_flag=0");
				sb.append(" group by d.name,c.stock_name,e.name) t ");
				sb.append(" left join (select d.name as dname,c.stock_name as stock_name,count(*) as count");
				sb.append(" from t_stock_applyinfo a,t_applylist b,m_stock_info c,v3x_org_member d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.stock_id=a.stock_id and d.id=b.apply_username");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.stock_name) w  on t.dname=w.dname and t.stock_name=w.stock_name ");
				sb.append(" left join (select d.name as dname,c.stock_name as stock_name,count(*) as count");
				sb.append(" from t_stock_applyinfo a,t_applylist b,m_stock_info c,v3x_org_member d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.stock_id=a.stock_id and d.id=b.apply_username");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.stock_name) m on t.dname=m.dname and t.stock_name=m.stock_name");
				
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
				SQLQuery query =session.createSQLQuery(sql);
				if(needPage){
					query.setFirstResult(Pagination.getFirstResult());
					query.setMaxResults(Pagination.getMaxResults());
				}
                //修改周和月的查询by Yongzhang 2008-05-12
				query.setLong(0, user.getId());
				query.setDate(1, firstDayOfWeek);
				query.setDate(2, lastDayOfWeek);
				query.setDate(3, firstDayOfMonth);
				query.setDate(4, lastDayOfMonth);
				//query.setLong(4, user.getDepartmentId());
				//query.setParameter(4,user.getId());
				//query.setLong(5, user.getDepartmentId());
				//query.setInteger(4, 2);
				
				
				query.addScalar("dname", Hibernate.STRING);
				query.addScalar("department_name", Hibernate.STRING);
				query.addScalar("stock_name", Hibernate.STRING);
				query.addScalar("wcount", Hibernate.INTEGER);
				query.addScalar("mcount", Hibernate.INTEGER);
				query.addScalar("count", Hibernate.INTEGER);
				return query.list();
			}
		});
	}
      /**
     * 管理员管理的办公用品移交功能
     *
     */
    public void updateStockMangerBatch(final long oldManager, final long newManager,final User user)
    {
    	this.updateStockMangerBatch(oldManager, newManager, user,true);
    }
    
    public void updateStockMangerBatch(final long oldManager, final long newManager,final User user,final boolean fromFlag){
        super.getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session session) throws HibernateException, SQLException
            {
                String sql = "";
                if (fromFlag) {
                	sql="update StockInfo set stockRes=:stockManager,accountId=:accountId where stockRes=:oldStockManager";
				}else {
					sql = "update StockInfo set stockRes=:stockManager where accountId =:accountId";
				}
                Query query= session.createQuery(sql);
                if (fromFlag) {
                    query.setLong("oldStockManager", oldManager);
				}
                query.setLong("stockManager", newManager);
                query.setLong("accountId", user.getLoginAccount());
                query.executeUpdate();
                return null;
            }
        });
    }

    /**
     * 管理员管理的办公用品申请移交功能
     *
     */
    public void audiTransfer(final long oldManager, final long newManager) {
        super.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException, SQLException
            {
                String sql="update TApplylist set applyMge=? where applyMge=? and applyState=1 and applyType="+OfficeModelType.stock_type;
                Query query= session.createQuery(sql);
                      query.setLong(0, newManager);
                      query.setLong(1, oldManager);
                      query.executeUpdate();
                return null;
            }
            
        });
        
    }
}
