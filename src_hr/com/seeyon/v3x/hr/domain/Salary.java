/**
 * $Id: Salary.java,v 1.5 2010/12/21 09:50:52 renhy Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.hr.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
*
* <p/> Title: 员工工资<实体>
* </p>
* <p/> Description: 员工工资<实体> 
* </p>
* <p/> Date: 2007-06-08
* </p>
* @author gaiht
*/

public class Salary extends BaseModel implements Serializable{
	
	private static final long serialVersionUID = -4924784399678149782L;
	private BigDecimal salaryOriginally;  //员工工资:应发工资
	private BigDecimal salaryActually;    //员工工资:实发工资
	private BigDecimal salaryBusiness;    //员工工资:职位工资
	private BigDecimal salaryBasic;       //员工工资:基本工资
	private BigDecimal bonus;             //员工工资:奖金
	private BigDecimal fund;              //员工工资:公基金
	private BigDecimal insurance;         //员工工资:保险金
	private BigDecimal incomeTax;         //员工工资:个人所得税
	private Long staffId;                             //员工工资:员工Id
	private Long creatorId;                           //员工工资:创建人Id
	private Date createdTimestamp;                    //员工工资:创建时间
	private Date modifiedTimestamp;                   //员工工资:修改时间
	private String status;                            //员工工资:状态
	private int year;                                 //员工工资:发薪年份
	private int month;                                //员工工资:发薪月份
	private String name;                              //员工工资:员工姓名
	private Long accountId;			//员工单位ID
	
	//非持久性属性
	private boolean hasAttachment = false; //是否有附件
	
	public boolean isHasAttachment() {
		return hasAttachment;
	}
	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public Date getModifiedTimestamp() {
		return modifiedTimestamp;
	}
	public void setModifiedTimestamp(Date modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
	}
	public BigDecimal getSalaryOriginally(){
		return salaryOriginally;
	}
	public void setSalaryOriginally(BigDecimal salaryOriginally){
		this.salaryOriginally = salaryOriginally;
	}
	public BigDecimal getSalaryActually(){
		return salaryActually;
	}
	public void setSalaryActually(BigDecimal salaryActually){
		this.salaryActually = salaryActually;
	}
	public BigDecimal getSalaryBusiness(){
		return salaryBusiness;
	}
	public void setSalaryBusiness(BigDecimal salaryBusiness){
		this.salaryBusiness = salaryBusiness;
	}
	public BigDecimal getSalaryBasic(){
		return salaryBasic;
	}
	public void setSalaryBasic(BigDecimal salaryBasic){
		this.salaryBasic = salaryBasic;
	}
	public BigDecimal getBonus(){
		return bonus;
	}
	public void setBonus(BigDecimal bonus){
		this.bonus = bonus;
	}
	public BigDecimal getFund(){
		return fund;
	}
	public void setFund(BigDecimal fund){
		this.fund = fund;
	}
	public BigDecimal getIncomeTax(){
		return incomeTax;
	}
	public void setIncomeTax(BigDecimal incomeTax){
		this.incomeTax = incomeTax;
	}
	public Long getStaffId(){
		return staffId;
	}
	public void setStaffId(Long staffId){
		this.staffId = staffId;
	}
	public BigDecimal getInsurance() {
		return insurance;
	}
	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	

}
