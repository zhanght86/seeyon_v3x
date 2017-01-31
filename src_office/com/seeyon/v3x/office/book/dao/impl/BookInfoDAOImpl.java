package com.seeyon.v3x.office.book.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.book.dao.BookInfoDAO;
import com.seeyon.v3x.office.book.domain.MBookInfo;
import com.seeyon.v3x.office.book.util.Constants;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.myapply.domain.TApplylist;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

public class BookInfoDAOImpl extends BaseHibernateDao<MBookInfo> implements BookInfoDAO {
	public void save(MBookInfo mBookInfo){
		super.save(mBookInfo);
	}
	
	public void update(MBookInfo mBookInfo){
		super.update(mBookInfo);
	}

	public SQLQuery find(String sql,Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		try{
			query = session.createSQLQuery(sql);
			if(map != null){
				Iterator iterator = map.keySet().iterator();
				String key = null ;
				for(;iterator.hasNext();){
					key = iterator.next().toString();
					query.setParameter(key, map.get(key));
				}
			}
		}catch(Exception ex){
			
		}finally{
			super.releaseSession(session);
		}
		return query;
	}

	public int getCount(String sql,Map map) {
		Session session = super.getSession();
		SQLQuery query = null;
		try{
			query = super.getSession().createSQLQuery(sql);
			if(map != null){
				Iterator iterator = map.keySet().iterator();
				String key = null ;
				for(;iterator.hasNext();){
					key = iterator.next().toString();
					query.setParameter(key, map.get(key));
				}
			}
		}catch(Exception ex){
			
		}finally{
			super.releaseSession(session);
		}
		int totalCount = (Integer)query.addScalar(com.seeyon.v3x.office.book.util.Constants.Total_Count_Field, Hibernate.INTEGER).uniqueResult();
		return totalCount;
	}
	
	public MBookInfo load(long id){
		return (MBookInfo)this.getHibernateTemplate().load(MBookInfo.class, new Long(id));
	}
    /**
     * 查询出所有的图书资料信息
     * 
     * @param field
     *            图书资料是2，图书是1
     * @return
     */
    public List findBookField()
    {
    	DetachedCriteria criteria = DetachedCriteria.forClass(MBookInfo.class);
    	criteria.add(Restrictions.eq("bookField",com.seeyon.v3x.office.book.util.Constants.Field_Information));
    	criteria.add(Restrictions.eq("delFlag" ,com.seeyon.v3x.office.book.util.Constants.Del_Flag_Normal));
    	return super.executeCriteria(criteria,-1,-1);
    }
    /**
     * 管理员管理的图书移交功能
     *
     */
    public void updateBookMangerBatch(final long oldManager, final long newManager,final User user)
    {
    	this.updateBookMangerBatch(oldManager, newManager, user, true);
    }
    
    public void updateBookMangerBatch(final long oldManager, final long newManager,final User user,final boolean fromFlag){

        super.getHibernateTemplate().execute(new HibernateCallback()
        {

            public Object doInHibernate(Session session) throws HibernateException, SQLException
            {
                String sql = "";
                if (fromFlag) {
                	sql="update MBookInfo set bookMge=:bookManager,domainId=:domainId where bookMge=:oldBookManager";
				}else {
					sql = "update MBookInfo set bookMge=:bookManager where domainId=:domainId";
				}
                Query query= session.createQuery(sql);
                query.setLong("bookManager", newManager);
			    query.setLong("domainId", user.getLoginAccount());
                if (fromFlag) {
    			    query.setLong("oldBookManager", oldManager);
				}
			    
			    query.executeUpdate();
                return null;
            }
            
        });
        
    
    }

    
    /**
     * 查询书是否已经借出
     * **/
    public int selectLendBook(final String applyid){
    	String sql = "select count(bookDepartcount) from TBookDepartinfo where delFlag=:delFlag  and applyId = :applyId ";
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("delFlag", 0);
    	map.put("applyId", Long.parseLong(applyid));
    	int flag =(Integer) super.findUnique(sql, map, new ArrayList());
		if(flag>0){
			flag=1;
		}else{
			flag=0;
		}
		return flag;
    }
    public void audiTransfer(final long oldManager, final long newManager) {
        super.getHibernateTemplate().execute(new HibernateCallback()
        {
            public Object doInHibernate(Session session) throws HibernateException, SQLException
            {
                String sql="update TApplylist set applyMge=? where applyMge=? and (applyState=1 or applyState=2) and applyType="+OfficeModelType.book_type;
                Query query= session.createQuery(sql);
                      query.setLong(0, newManager);
                      query.setLong(1, oldManager);
                      query.executeUpdate();
                return null;
            }
            
        });
        
    }
    
    public List listBookReg(String field, String fieldValue, Long managerId) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(MBookInfo.class);
    	criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
//    	criteria.add(Restrictions.eq("bookMge",managerId));
    	criteria = searchCriteria(criteria, field, fieldValue);
    	
    	criteria.addOrder(Order.desc("createDate"));
    	criteria.addOrder(Order.desc("bookId"));
    	criteria.add(Restrictions.eq("bookMge", CurrentUser.get().getId()));
    	return super.executeCriteria(criteria);
    }
    
    public List listBookApp(String field, String fieldValue, List applyId) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(MBookInfo.class);
    	
    	criteria.add(Restrictions.eq("delFlag", Constants.Del_Flag_Normal));
    	criteria.add(Restrictions.eq("bookState", Constants.Book_Status_Allow));
    	criteria.add(Restrictions.eq("domainId",CurrentUser.get().getLoginAccount()));
//    	criteria.add(Restrictions.in("bookMge", applyId));
    	
    	criteria = searchCriteria(criteria, field, fieldValue);
    	
    	criteria.addOrder(Order.desc("createDate"));
    	criteria.addOrder(Order.desc("bookId"));
    	
    	return super.executeCriteria(criteria);
    }
    
    private DetachedCriteria searchCriteria(DetachedCriteria criteria,String field,String fieldValue){
    	if(Strings.isNotBlank(field) && Strings.isNotBlank(fieldValue)){
    		int fieldInt = Integer.parseInt(field);
    		switch(fieldInt){
    		case Constants.Search_Condition_BookName://1
    			criteria.add(Restrictions.like("bookName", "%"+SQLWildcardUtil.escape(fieldValue)+"%"));
    			break;
    		case Constants.Search_Condition_BookType://2
    			criteria.add(Restrictions.eq("officeType.typeId", Long.parseLong(fieldValue)));
    			break;
    		case Constants.Search_Condition_BookAuthor://3
    			criteria.add(Restrictions.like("bookAuthor", "%"+SQLWildcardUtil.escape(fieldValue)+"%"));
    			break;
    		case Constants.Search_Condition_BookStat://4
    			criteria.add(Restrictions.eq("bookState", Integer.parseInt(fieldValue)));
    			break;
    		case Constants.Search_Condition_BookPub://9
    			criteria.add(Restrictions.like("bookPub", "%"+SQLWildcardUtil.escape(fieldValue)+"%"));
    			break;
    		case Constants.Search_Condition_BookField://10
    			criteria.add(Restrictions.eq("bookField", Integer.parseInt(fieldValue)));
    			break;
    		}
    	}
    	return criteria;
    }
    
    public Integer dateCount(Long userId, Date thisWeeb) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
    	criteria.add(Restrictions.eq("applyType", 4));
    	criteria.add(Restrictions.eq("applyUsername", userId));
    	criteria.add(Restrictions.gt("applyState", 1));
    	criteria.add(Restrictions.gt("applyDate", thisWeeb));
    	return super.getCountByCriteria(criteria);
    }
    
    public Integer totalCount(Long userId) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(TApplylist.class);
    	criteria.add(Restrictions.eq("applyType", 4));
    	criteria.add(Restrictions.eq("applyUsername", userId));
    	criteria.add(Restrictions.gt("applyState", 1));
    	return super.getCountByCriteria(criteria);
    }
    public Integer noBackCount(Long userId) {
    	String sql = "select count(t.applyId) from TApplylist t,TBookDepartinfo b where t.applyId = b.applyId  " +
    			" and b.bookBacktime is null and b.bookDeparttime is not null and t.applyUsername=? and t.applyType=? and t.applyState> ? and t.applyState< ? ";
    	List<Object> list = new ArrayList<Object>();
    	list.add(userId);
    	list.add(4);
    	list.add(1);
    	list.add(5);
    	return (Integer)super.findUnique(sql, null, list);
    }

    public List getBookSummayByMember(final boolean needPage) {
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
				
				sb.append("select t.dname,t.department_name,t.book_name,w.count as wcount,m.count as mcount,t.count as count,n.count as nobackcount");
				sb.append(" from");
				sb.append(" (select d.name as dname,c.book_name as book_name,e.name as department_name,count(*) as count");
				sb.append(" from t_book_applyinfo a,t_applylist b,m_book_info c,v3x_org_member d,v3x_org_department e");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.book_id=a.book_id and d.id=b.apply_username and d.org_department_id=e.id and c.book_mge=? and c.del_flag=0");
				sb.append(" group by d.name,c.book_name,e.name) t ");
				sb.append(" left join (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_applyinfo a,t_applylist b,m_book_info c,v3x_org_member d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.book_id=a.book_id and d.id=b.apply_username");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.book_name) w  on t.dname=w.dname and t.book_name=w.book_name ");
				sb.append(" left join (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_applyinfo a,t_applylist b,m_book_info c,v3x_org_member d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.book_id=a.book_id and d.id=b.apply_username");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.book_name) m on t.dname=m.dname and t.book_name=m.book_name");
				sb.append(" left join (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_departinfo a,t_applylist b,m_book_info c,v3x_org_member d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and a.book_backtime is null");
				sb.append(" and a.book_departtime is not null and c.book_id=a.book_id and d.id=b.apply_username");
				sb.append(" group by d.name,c.book_name) n on n.dname=t.dname and n.book_name=t.book_name");
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
				query.addScalar("department_name", Hibernate.STRING);
				query.addScalar("book_name", Hibernate.STRING);
				query.addScalar("wcount", Hibernate.INTEGER);
				query.addScalar("mcount", Hibernate.INTEGER);
				query.addScalar("count", Hibernate.INTEGER);
				query.addScalar("nobackcount", Hibernate.INTEGER);
				return query.list();
			}
		});
	}
	public List getBookSummayByDep(final boolean needPage) {
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
				
				sb.append("select t.dname,t.book_name,w.count as wcount,m.count as mcount,t.count as count,n.count as nobackcount");
				sb.append(" from");
				sb.append(" (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_applyinfo a,t_applylist b,m_book_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.book_id=a.book_id and d.id=b.apply_depid and c.book_mge=? and c.del_flag=0");
				sb.append(" group by d.name,c.book_name) t ");
				sb.append(" left join (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_applyinfo a,t_applylist b,m_book_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.book_id=a.book_id and d.id=b.apply_depid");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.book_name) w  on t.dname=w.dname and t.book_name=w.book_name ");
				sb.append(" left join (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_applyinfo a,t_applylist b,m_book_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and c.book_id=a.book_id and d.id=b.apply_depid");
				sb.append(" and b.audit_time>=? and b.audit_time<? group by d.name,c.book_name) m on t.dname=m.dname and t.book_name=m.book_name");
				sb.append(" left join (select d.name as dname,c.book_name as book_name,count(*) as count");
				sb.append(" from t_book_departinfo a,t_applylist b,m_book_info c,v3x_org_department d");
				sb.append(" where a.apply_id=b.apply_id and b.apply_state=2 and a.book_backtime is null");
				sb.append(" and a.book_departtime is not null and c.book_id=a.book_id and d.id=b.apply_depid");
				sb.append(" group by d.name,c.book_name) n on n.dname=t.dname and n.book_name=t.book_name");
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
				query.addScalar("book_name", Hibernate.STRING);
				query.addScalar("wcount", Hibernate.INTEGER);
				query.addScalar("mcount", Hibernate.INTEGER);
				query.addScalar("count", Hibernate.INTEGER);
				query.addScalar("nobackcount", Hibernate.INTEGER);
				return query.list();
			}
		});
	}

	/**
	 * 根据名字查找未删除的图书
	 */
	public List findBookByName(String name) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getEntityClass());
		criteria.add(Restrictions.eq("bookName", name));
		criteria.add(Restrictions.eq("delFlag", 0));
		return getHibernateTemplate().findByCriteria(criteria);
	}
    
}
