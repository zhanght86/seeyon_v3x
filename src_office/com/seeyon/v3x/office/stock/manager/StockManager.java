package com.seeyon.v3x.office.stock.manager;

import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.stock.domain.StockApplyInfo;
import com.seeyon.v3x.office.stock.domain.StockInfo;

/**
 * 办公用品管理类
 * @author lindx
 *
 */
public interface StockManager {
	
	/**
	 * 根据办公用品编号取得办公用品详细信息
	 * @param stockId 办公用品编号
	 * @return  StockInfo办公用品详细信息
	 * @throws BusinessException
	 */
	public StockInfo getStockInfoById(Long stockId) throws BusinessException;
	
	/**
	 * 创建办公用品详细信息
	 * @param stockInfo
	 * @throws BusinessException
	 */
	public void createStockInfo(StockInfo stockInfo) throws BusinessException;
	
	/**
	 * 修改办公用皮详细信息
	 * @param stockInfo  
	 * @throws BusinessException
	 */
	public void updateStockInfo(StockInfo stockInfo) throws BusinessException;
	
	/**
	 * 查询管理员所有的办公用品一览列表
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId  管理员ID
	 * @return  办公用品一览列表
	 * @throws BusinessException
	 */
	public List getStockInfoList(String fieldName,String fieldValue,Long managerId) throws BusinessException;
	/**
	 * 查询所有的可以申请的办公用品一览列表
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId  管理员ID
	 * @return  办公用品一览列表
	 * @throws BusinessException
	 */
	public List getStockInfoApplyList(String fieldName,String fieldValue,Long managerId) throws BusinessException;
	/**
	 * 批量修改办公用品的删除状态为1
	 * @param stockIds  办公用品编号集 格式：1000,10002,1004
	 * @throws BusinessException
	 */
	public void deleteStockInfoByIds(String stockIds) throws BusinessException;

	
	/**
	 * 根据申请编号取得办公用品详细申请单情报
	 * @param applyId  申请编号
	 * @return  办公用品详细申请单情报
	 */
	public StockApplyInfo getStockApplyById(Long applyId) throws BusinessException;
	
	/**
	 * 保存办公用品申请信息
	 * @param officeApply	申请单信息
	 * @param stockApplyInfo	办公用品详细申请单
	 * @throws BusinessException  异常抛出
	 */
	public void saveStockApply(OfficeApply officeApply,StockApplyInfo stockApplyInfo) throws BusinessException;
	
	/**
	 * 取得待审核的办公用品申请列表
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId
	 * @return
	 */
	public List getStockApplyListForAutdit(String fieldName,String fieldValue,Long managerId) throws BusinessException;
	
	
	public void deleteStockApplyByIds(String applyIds) throws BusinessException;
	
	/**
	 * 取得办公用品最大编号
	 * @return
	 */
	public Long getMaxStockNo();
	
	/**
	 * 按部门统计办公用品
	 * @return
	 */
	public List getStockSummayByDep(boolean needPage);
	
	public List getStockSummay(boolean needPage);
    /**
     * 管理员管理的办公用品移交功能
     *
     */
   public void updateStockMangerBatch( long oldManager,  long newManager,User user);
   public void updateStockMangerBatch( long oldManager,  long newManager,User user,boolean fromFlag);
   
   public OfficeApply getOfficeApplyById(Long applyId);
   public void deleteStockApplyById(String applyIds) throws BusinessException;
   /**
    * ajax
    */
   public boolean checkHasDeleted(String stockid) ;
}
