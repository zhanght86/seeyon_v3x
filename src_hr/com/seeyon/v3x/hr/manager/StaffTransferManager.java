package com.seeyon.v3x.hr.manager;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.hr.domain.StaffTransfer;
import com.seeyon.v3x.hr.domain.StaffTransferType;

import www.seeyon.com.v3x.form.manager.define.data.base.FormField;


public interface StaffTransferManager {

	/**
	 * 获得所有调配记录
	 * 
	 */
	public List<StaffTransfer> getStaffTransfer()throws Exception;
	
	/**
	 * 添加调配记录
	 * 
	 */
	public void addTransfer(StaffTransfer staffTransfer)throws Exception;
	
	/**
	 * 更新调配记录
	 * 
	 */
	public void updateTransfer(StaffTransfer staffTransfer)throws Exception;
	
	/**
	 * 根据id查询调配记录
	 * 
	 */
	public StaffTransfer getStaffTransferById(Long id)throws Exception;
	
//	/**
//	 * 根据姓名查询调配记录
//	 * 
//	 */
//	public List<StaffTransfer> getStaffTransferByName(String name)throws Exception;
	
	/**
	 * 根据姓名模糊查询调配记录
	 * @param match
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getStaffTransferLikeByName(String match, String fname)throws Exception;
	/**
	 * 查询变动类型为调配的记录
	 * 
	 */
	public List<StaffTransfer> getTransferTypeStaffTransfer()throws Exception;
	
	/**
	 * 查询变动类型为离职的记录
	 * 
	 */
	public List<StaffTransfer> getDimissionTypeStaffTransfer()throws Exception;
	
	/**
	 * 根据变动类型查询调配记录
	 * @param transferType
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> getStaffTransferByType(int transferType, String fname)throws Exception;
	
	/**
	 * 根据状态查询调配记录
	 * 
	 */
	public List<Object[]> getStaffTransferByState(int state, String fname)throws Exception;
	
	/**
	 * 根据提交时间查询调配记录
	 * 
	 * referTime的格式：yyyy-MM-dd
	 */
	public List<Object[]> getStaffTransferByReferTime(Date referTime, String fname)throws Exception;
	
	/**
	 * 删除一条调配记录
	 * 
	 */
	public void deleteTransfer(Long id)throws Exception;
	
	
	/*----------------------------------------- 2007-09-12 ---------------------------------------------*/
	/**
	 * 根据调配类型id得到StaffTransferType对象
	 * 
	 */
	public StaffTransferType getStaffTransferTypeById(int id)throws Exception;
	
	/**
	 * 查询所有待处理调配表单
	 * 
	 */
	public List<Object[]> getFormByName(String fname)throws Exception;
	
	/**
	 * 根据id在表单动态表中查询一条调配信息
	 * 
	 */
	public Object[] getFormItemById(String fname,Long id)throws Exception;
	
	/**
	 * 根据id在表单动态表中删除一条调配信息
	 * (name是传给日志的参数)
	 */
	public void deleteFormItemById(String fname,Long id,String name)throws Exception;
	
	/**
	 * 处理一条调配信息
	 * 
	 */
	public void dealFormItemById(String fname,Long id)throws Exception;
	
	/**
	 * 获取表单显示的XML
	 * 
	 */
	public String getFormXMLById(Long formid, String fname)throws Exception; 
	
	
}
