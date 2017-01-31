/**
 * $Id: SalaryDao.java,v 1.15 2011/07/12 02:30:58 renhy Exp $
 `* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.hr.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;

/**
 * 
 * <p/> Title: 员工工资<数据访问对象>
 * </p>
 * <p/> Description: 员工工资<数据访问对象>
 * </p>
 * <p/> Date: 2007-06-08
 * </p>
 * 
 * @author gaiht
 * @see com.seeyon.v3x.hr.domain.Salary
 */

public class SalaryDao extends BaseHibernateDao<Salary> {
	/**
	 * 查出所有员工的工资情况
	 * 
	 * @return 员工工资列表
	 */

	@SuppressWarnings("unchecked")
	public List<Salary> findAllStaffSalary() {
		String hql = "From Salary as s where s.accountId =? order by s.year desc, s.month desc";
		return find(hql, CurrentUser.get().getLoginAccount());
	}

	public void save(Salary salary) {
		getHibernateTemplate().save(salary);
	}

	/**
	 * 删除工资信息
	 */
	public void deleteSalaryByIds(final List<Long> salaryIds) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete Salary");
				sHql.append(" where id in (:salaryIds)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("salaryIds", salaryIds);
				return query.executeUpdate();
			}
		});
	}

	public Salary findSalaryById(Long id) {
		return this.get(id);
	}

	
	@SuppressWarnings("unchecked")
	public List<Salary> findSalaryByStaffId(final Long staffId, boolean pagination) {
		String hql = "from Salary where staffId = ? and accountId = ? order by year desc, month desc";
		return pagination ? this.find(hql, staffId, CurrentUser.get().getLoginAccount()) : this.find(hql, -1, -1, null, staffId, CurrentUser.get().getLoginAccount());
	}

	//根据员工姓名和发工资时间（年月）查出该条工资信息
	public Salary findSalaryByStaffNameAndDate(String staffName, int year,
			int month)throws Exception {
		Salary salary = null;
		Session session = super.getSession();
		try{
			Criteria criteria = session.createCriteria(Salary.class);
			criteria.add(Expression.eq("name", staffName));
			criteria.add(Expression.eq("year", year));
			criteria.add(Expression.eq("month", month));
			salary = (Salary) criteria.uniqueResult();
	
			if (null != salary) {
				return salary;
			}
		}catch(Exception ex){
			throw ex;
		}finally{
			super.releaseSession(session);
		}

		return null;
	}
	
	public List<Salary> findSalaryByTime(Long staffId,int fromYear,int fromMonth,int toYear,int toMonth)throws Exception{
		return this.findSalaryByTime(staffId, fromYear, fromMonth, toYear, toMonth, false);
	}
	
	@SuppressWarnings("unchecked")
	public List<Salary> findSalaryByTime(Long staffId,int fromYear,int fromMonth,int toYear,int toMonth,boolean pagination)throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append("from Salary where staffId = :staffId and accountId = :accountId and (");
		hql.append("( year > :fromYear and year < :toYear ) or ");
		hql.append("( :fromYear != :toYear and ( (year = :fromYear and month >= :fromMonth) or (year = :toYear and month<= :toMonth) ) ) or ");		  
		hql.append("( (year=:fromYear and year=:toYear) and ( month between :fromMonth and :toMonth))");
		hql.append(")");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("staffId", staffId);
		params.put("accountId", CurrentUser.get().getLoginAccount());
		params.put("fromYear", fromYear);
		params.put("fromMonth", fromMonth);
		params.put("toYear",toYear);
		params.put("toMonth", toMonth);
		return this.find(hql.toString(), params);
	}
	
	public List<Salary> findAllSalaryByDate(int fromYear,int fromMonth,int toYear,int toMonth)throws Exception{
		return findAllSalaryByDate(fromYear,fromMonth,toYear,toMonth,false);
	}

	
	public List<Salary> findAllSalaryByDate(int fromYear,int fromMonth,int toYear,int toMonth,boolean isPaginate)throws Exception{
		StringBuffer hql = new StringBuffer();
		Long accountId = CurrentUser.get().getLoginAccount();
		hql.append("from Salary where accountId=:accountId and(");
		hql.append("( year > :fromYear and year < :toYear ) or ");
		hql.append("( :fromYear != :toYear and ( (year = :fromYear and month > :fromMonth) or (year = :toYear and month<:toMonth) ) ) or ");		  
		hql.append("( (year=:fromYear and year=:toYear) and ( month between :fromMonth and :toMonth))");
		hql.append(")");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		params.put("fromYear", fromYear);
		params.put("fromMonth", fromMonth);
		params.put("toYear",toYear);
		params.put("toMonth", toMonth);
		return this.find(hql.toString(), params);
	}

	public List findSalaryByName(final String name){
		return findSalaryByName(name,false);
	}
	
	public List findSalaryByName(final String name,final boolean isPaginate){
		StringBuffer hql = new StringBuffer();
		Long accountId = CurrentUser.get().getLoginAccount();
		hql.append("from Salary where accountId= :accountId");
		hql.append(" and ");
		hql.append("name like :name");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		params.put("name", "%"+name+"%");
		return this.find(hql.toString(), params);
	}
	
	public List findSalaryByBasic(final float fromSalary, final float toSalary){
		return findSalaryByBasic(fromSalary,toSalary,false);
	}

	public List findSalaryByBasic(final float fromSalary, final float toSalary, final boolean isPaginate){
		StringBuffer hql = new StringBuffer();
		hql.append("from Salary where accountId=:accountId and salaryBasic >= :fromSalary and salaryBasic <= :toSalary");
		Long accountId = CurrentUser.get().getLoginAccount();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		params.put("fromSalary", fromSalary);
		params.put("toSalary", toSalary);
		return this.find(hql.toString(), params);
	}

	
	public List findSalaryByActually(final float fromSalary, final float toSalary){
		return findSalaryByActually(fromSalary,toSalary,false);
	}
	
	public List findSalaryByActually(final float fromSalary, final float toSalary,final boolean isPaginate){
		StringBuffer hql = new StringBuffer();
		hql.append("from Salary where accountId=:accountId and salaryActually >= :fromSalary and salaryActually <= :toSalary");
		Long accountId = CurrentUser.get().getLoginAccount();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("accountId", accountId);
		params.put("fromSalary", fromSalary);
		params.put("toSalary", toSalary);
		return this.find(hql.toString(), params);
	}
	
	public void exportSalary(List<Salary> sList, List<Salary> uList, List<Repository> repositories) throws BusinessException{
		super.savePatchAll(sList);
		super.updatePatchAll(uList);
		super.savePatchAll(repositories);
	}
}
