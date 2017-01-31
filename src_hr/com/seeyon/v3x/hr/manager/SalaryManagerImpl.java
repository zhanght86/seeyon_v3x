package com.seeyon.v3x.hr.manager;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.dao.HrSalaryPasswordDao;
import com.seeyon.v3x.hr.dao.SalaryDao;
import com.seeyon.v3x.hr.domain.HrSalaryPassword;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.LightWeightEncoder;
import com.seeyon.v3x.util.SQLWildcardUtil;


public class SalaryManagerImpl implements SalaryManager {
	
	private  static final Log log = LogFactory.getLog(SalaryManagerImpl.class);
	
	private SalaryDao salaryDao;
	private HrSalaryPasswordDao hrSalaryPasswordDao ;
	
	
	public SalaryDao getSalaryDao() {
		return salaryDao;
	}

	public void setSalaryDao(SalaryDao salaryDao) {
		this.salaryDao = salaryDao;
	}

	public List findAllStaffSalary(){
		return salaryDao.findAllStaffSalary();
	}
	
	@SuppressWarnings("unchecked")
	public List<Salary> findAllAccountStaffSalary(Long accountId, String condition, String textfield, String textfield1, String textfield2, String textfield3) {
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select s from " + Salary.class.getName() + " as s ");
		
		if ("salaryDept".equals(condition)) {
			hql.append(" , " + V3xOrgMember.class.getName() + " as m ");
		}
		
		hql.append(" where s.accountId = :accountId ");
		params.put("accountId", accountId);

		if (StringUtils.isNotBlank(condition)) {
			if ("staffName".equals(condition)) {
				hql.append(" and s.name like :name ");
				params.put("name", "%" + SQLWildcardUtil.escape(textfield) + "%");
			} else if ("salaryDate".equals(condition)) {
				hql.append(" and(");
				hql.append("(year > :fromYear and year < :toYear) or ");
				hql.append("(:fromYear != :toYear and ((year = :fromYear and month > :fromMonth) or (year = :toYear and month < :toMonth))) or ");
				hql.append("((year = :fromYear and year = :toYear) and (month between :fromMonth and :toMonth))");
				hql.append(") ");
				params.put("fromYear", NumberUtils.toInt(textfield));
				params.put("fromMonth", NumberUtils.toInt(textfield1));
				params.put("toYear", NumberUtils.toInt(textfield2));
				params.put("toMonth", NumberUtils.toInt(textfield3));

			} else if ("salaryDept".equals(condition)) {
				hql.append(" and s.staffId=m.id and m.orgDepartmentId = :departmentId ");
				params.put("departmentId", NumberUtils.toLong(textfield));
			}
		}

		hql.append(" order by s.year desc, s.month desc, s.id");
		return salaryDao.find(hql.toString(), params);
	}
	
	public void exportSalary(List<Salary> sList, List<Salary> uList, List<Repository> repositories) throws BusinessException{
		salaryDao.exportSalary(sList, uList, repositories);
	}
	
	public void addSalary(Salary salary){
		salaryDao.save(salary);
	}
	
	public void removeSalaryByIds(List<Long> salaryIds){
		this.salaryDao.deleteSalaryByIds(salaryIds);
	}
	
	public Salary findSalaryById(Long id){
		return this.salaryDao.findSalaryById(id);
	}
	
	public void updateSalary(Salary salary){
		this.salaryDao.update(salary);
	}
	
	public List findSalaryByStaffId(Long staffId){
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuilder hql = new StringBuilder();
		hql.append(" select s from " + Salary.class.getName() + " as s where staffId = :staffId and accountId = :accountId order by year desc, month desc");
		params.put("accountId", CurrentUser.get().getLoginAccount());
		params.put("staffId", staffId);
		return salaryDao.find(hql.toString(), params);
	}
	
	public List findSalaryByStaffId(Long staffId, boolean pagination) {
		return this.findSalaryByStaffId(staffId);
	}
	
	public Salary getSalaryByStaffNameAndDate(String staffName, int year, int month)throws Exception {
		return this.salaryDao.findSalaryByStaffNameAndDate(staffName, year, month);
	}
	
	public List getSalaryByTime(Long staffId,int fromYear,int fromMonth,int toYear,int toMonth)throws Exception{
		return this.salaryDao.findSalaryByTime(staffId, fromYear, fromMonth, toYear, toMonth);
	}
	
	public List getSalaryByTime(Long staffId,int fromYear,int fromMonth,int toYear,int toMonth,boolean pagination)throws Exception{
		return this.salaryDao.findSalaryByTime(staffId, fromYear, fromMonth, toYear, toMonth, pagination);
	}
	
	public List getSalaryByName(String name){
		return this.salaryDao.findSalaryByName(name);
	}

	public List getSalaryByName(String name, boolean isPaginate){
		return this.salaryDao.findSalaryByName(name, isPaginate);
	}

	public List getAllStaffSalarysByDate(int fromYear,int fromMonth,int toYear,int toMonth)throws Exception{
		return this.salaryDao.findAllSalaryByDate(fromYear, fromMonth, toYear, toMonth);
	}
	
	public List getAllStaffSalarysByDate(int fromYear,int fromMonth,int toYear,int toMonth, boolean isPaginate)throws Exception{
		return this.salaryDao.findAllSalaryByDate(fromYear, fromMonth, toYear, toMonth, isPaginate);
	}
	
	public List getSalaryByBasic(float fromSalary, float toSalary){
		return this.salaryDao.findSalaryByBasic(fromSalary, toSalary);
	}

	public List getSalaryByBasic(float fromSalary, float toSalary, boolean isPaginate){
		return this.salaryDao.findSalaryByBasic(fromSalary, toSalary, isPaginate);
	}
	
	public List getSalaryByActually(float fromSalary, float toSalary){
		return this.salaryDao.findSalaryByActually(fromSalary, toSalary);
	}
	
	public List getSalaryByActually(float fromSalary, float toSalary, boolean isPaginate){
		return this.salaryDao.findSalaryByActually(fromSalary, toSalary, isPaginate);
	}
	
	/**
	 * true 表示已经存在
	 */
	public boolean hasSalaryPasswordRecord(Long userId) {
		if (this.hrSalaryPasswordDao.getSalaryRecordUniq(userId) == null)
			return false ;
		return true ;
	}
	
	public HrSalaryPassword getSalaryPasswordRecordUniq(final long userId){	
		return this.hrSalaryPasswordDao.getSalaryRecordUniq(userId) ; 
	}
	
	public void setSalaryPasswordRecord(Long userId , String password) throws Exception {
		if(hasSalaryPasswordRecord(userId)){
			log.info("存在这个人员的记录" +userId);
			return ;
		}
		HrSalaryPassword hrSalaryPassword = new HrSalaryPassword() ;
		hrSalaryPassword.setIdIfNew() ;
		hrSalaryPassword.setUserId(userId);
		hrSalaryPassword.setSalaryPassword(LightWeightEncoder.encodeString(password)) ;
		hrSalaryPassword.setCreateDate(new Date()) ;
		hrSalaryPassword.setUpdateDate(new Date()) ;
		hrSalaryPasswordDao.save(hrSalaryPassword) ;
	}

	public HrSalaryPasswordDao getHrSalaryPasswordDao() {
		return hrSalaryPasswordDao;
	}

	public void setHrSalaryPasswordDao(HrSalaryPasswordDao hrSalaryPasswordDao) {
		this.hrSalaryPasswordDao = hrSalaryPasswordDao;
	}
	
	public boolean checkPassWord(Long userId , String password) throws Exception {
		HrSalaryPassword hrSalaryPassword = getSalaryPasswordRecordUniq(userId) ;
		if(hrSalaryPassword == null){
			log.error("没有这个人员的记录："+ userId) ;
			return false ;
		}
		
		String stdPwd = hrSalaryPassword.getSalaryPassword();
		
		if(stdPwd != null && stdPwd.equals(LightWeightEncoder.encodeString(password))){
			return true ;
		}
		
		return false ;
	}
	
	public boolean updatePassWord(Long userId , String password) throws Exception{
		HrSalaryPassword hrSalaryPassword = getSalaryPasswordRecordUniq(userId) ;
		if(hrSalaryPassword == null){
			log.error("没有这个人员的记录："+ userId) ;
			return false ;
		}
		hrSalaryPassword.setSalaryPassword(LightWeightEncoder.encodeString(password)) ;
		hrSalaryPassword.setUpdateDate(new Date()) ;
		try{
			hrSalaryPasswordDao.update(hrSalaryPassword) ;
		}catch(Exception e){
			log.error("更新出现问题",e) ;
			return false ;
		}
		
		return true ;
	}
	
	public boolean updatePassWord(String password,String members) throws Exception {
		String hql = "update HrSalaryPassword hsp set hsp.salaryPassword=:password where hsp.userId in (:ids)" ;
		Map<String ,Object> nameMap = new HashMap<String ,Object>() ;
		nameMap.put("password", LightWeightEncoder.encodeString(password)) ;
		String[] membersIds = members.split(",") ;
		List<Long> list = new ArrayList<Long>() ;
		for(String s : membersIds){
			list.add(Long.valueOf(s)) ;
		}
		nameMap.put("ids", list) ;
		try{
			hrSalaryPasswordDao.bulkUpdate(hql,nameMap) ;
		}catch(Exception e){
			log.error("更新出现问题",e) ;
			return false ;
		}
		return true ;
	}
	
	public void addAllSalary(List<Salary> salaryList) throws Exception {
		this.salaryDao.savePatchAll(salaryList);
	}
	
}