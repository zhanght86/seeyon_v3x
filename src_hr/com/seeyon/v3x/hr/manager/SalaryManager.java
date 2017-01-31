/**
 * $Id: SalaryManager.java,v 1.9 2011/05/25 13:04:53 yangm Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.hr.manager;

/**
*
* <p/> Title: 员工工资<外部接口>
* </p>
* <p/> Description: 员工工资<外部接口>
* </p>
* <p/> Date: 2007-06-08
* </p>
* @author gaiht
*/

import java.util.List;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;

public interface SalaryManager {
	/**
	 * 
	 * @return
	 */
	public List findAllStaffSalary();
	/**
	 * 分页查找
	 * @param accountId
	 * @return
	 */
	public List<Salary> findAllAccountStaffSalary(Long accountId, String condition, String textfield, String textfield1, String textfield2, String textfield3);
	
	public void exportSalary(List<Salary> sList, List<Salary> uList, List<Repository> repositories) throws BusinessException;
	/**
	 * 
	 *把员工工资写入数据库
	 */
	public void addSalary(Salary salary);
	
	public void removeSalaryByIds(List<Long> salaryIds);
	
	public Salary findSalaryById(Long id);
	
	public void updateSalary(Salary salary);
	
	public List findSalaryByStaffId(Long staffId);
	
	public List findSalaryByStaffId(Long staffId, boolean pagination);
	
	//根据员工姓名和发工资时间（年月）得出工资信息
	public Salary getSalaryByStaffNameAndDate(String staffName, int year, int month)throws Exception;
	
	public List getSalaryByTime(Long staffId,int fromYear,int fromMonth,int toYear,int toMonth)throws Exception;
	
	public List getSalaryByTime(Long staffId,int fromYear,int fromMonth,int toYear,int toMonth,boolean pagination)throws Exception;
	
	public List getSalaryByName(String name);
	
	public List getSalaryByName(String name, boolean isPaginate);
	
	public List getAllStaffSalarysByDate(int fromYear,int fromMonth,int toYear,int toMonth)throws Exception;
	
	public List getAllStaffSalarysByDate(int fromYear,int fromMonth,int toYear,int toMonth, boolean isPaginate)throws Exception;
	
	public List getSalaryByBasic(float fromSalary, float toSalary);
	
	public List getSalaryByBasic(float fromSalary, float toSalary, boolean isPaginate);
	
	public List getSalaryByActually(float fromSalary, float toSalary);
	
	public List getSalaryByActually(float fromSalary, float toSalary, boolean isPaginate);
	/**
	 * 根据人员Id 判断是否有人员的记录
	 * @param userId
	 * @return
	 */
	public boolean hasSalaryPasswordRecord(Long userId) ;
	/**
	 *  添加工资密码记录
	 * @param userId
	 * @param password
	 * @throws Exception
	 */
	public void setSalaryPasswordRecord(Long userId , String password) throws Exception ;
	/**
	 * 判断人员记录是否正确
	 * @param userId
	 * @param password
	 * @return
	 * @throws Exception
	 */
	
	public boolean checkPassWord(Long userId , String password) throws Exception ;
    /**
     * 修改人员工资密码
     * @param userId
     * @param password
     * @return
     * @throws Exception
     */
	public boolean updatePassWord(Long userId , String password) throws Exception ;
	/**
	 * 修改多人的工资密码
	 * @param password
	 * @param members 选人界面出来的值
	 * @return
	 * @throws Exception
	 */
	public boolean updatePassWord(String password,String members) throws Exception ;
	
	public void addAllSalary(List<Salary> salaryList) throws Exception;
}
