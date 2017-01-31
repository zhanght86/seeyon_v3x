package com.seeyon.v3x.office.stock.manager.impl;

import java.util.*;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.dao.OfficeApplyDao;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.stock.dao.StockDao;
import com.seeyon.v3x.office.stock.domain.*;
import com.seeyon.v3x.office.stock.manager.StockManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

import org.apache.log4j.Logger;


public class StockManagerImpl implements StockManager {
	
	private static Logger log= Logger.getLogger(StockManagerImpl.class);
	
	private StockDao stockDao;		//

	private OfficeApplyDao officeApplyDao;
	
	public void setStockDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}
	
	
	public void setOfficeApplyDao(OfficeApplyDao officeApplyDao) {
		this.officeApplyDao = officeApplyDao;
	}


	/**
	 * 根据办公用品编号取得办公用品详细信息
	 * @param stockId 办公用品编号
	 * @return  StockInfo办公用品详细信息
	 * @throws BusinessException
	 */
	public StockInfo getStockInfoById(Long stockId) throws BusinessException{
		return stockDao.findStockInfoById(stockId);
		
	}
	
	/**
	 * 创建办公用品详细信息
	 * @param stockInfo
	 * @throws BusinessException
	 */
	public void createStockInfo(StockInfo stockInfo) throws BusinessException{
		try{
			this.stockDao.createStockInfo(stockInfo);
		}catch(Exception e){
			log.error("新增办公用品错误！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	/**
	 * 修改办公用皮详细信息
	 * @param stockInfo  
	 * @throws BusinessException
	 */
	public void updateStockInfo(StockInfo stockInfo) throws BusinessException{
		try{
			this.stockDao.updateStockInfo(stockInfo);
		}catch(Exception e){
			log.error("修改办公用品错误！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	/**
	 * 查询管理员所有的办公用品一览列表
	 */
	public List getStockInfoList(String fieldName, String fieldValue, Long managerId) throws BusinessException {
		List stockList = new ArrayList();
		
		stockList = this.stockDao.findStockListByManager(fieldName, fieldValue, managerId);
		return stockList;
	}
/**
 * 查询所有的可以申请的办公用品一览列表
 */
	public List getStockInfoApplyList(String fieldName, String fieldValue, Long managerId) throws BusinessException {
		List stockList = new ArrayList();
		
		List idsList=officeApplyDao.getUserModelManagers(OfficeModelType.stock_type, CurrentUser.get());
		if(idsList==null || idsList.size()<=0)
		{
			return new ArrayList();
		}
		List mgrIds = new ArrayList();
		for(int i=0;i<idsList.size();i++)
		{
			mgrIds.add(((V3xOrgMember)idsList.get(i)).getId());
		}
		stockList = this.stockDao.findStockApplyList(mgrIds,fieldName, fieldValue, managerId);
		return stockList;
	}
	
	/**
	 * 批量修改办公用品的删除状态为1
	 * @param stockIds  办公用品编号集 格式：1000,10002,1004
	 * @throws BusinessException
	 */
	public void deleteStockInfoByIds(String stockIds) throws BusinessException{
		try{
            String[] arrayStrings= stockIds.split(",");
            List arrayList= new ArrayList<Long>();
            for (int i = 0; i < arrayStrings.length; i++)
            {
                arrayList.add(new Long(arrayStrings[i]));
            }
			stockDao.deleteStockInfobyIds(arrayList);
		}catch(Exception e){
			log.error("批量办公用品删除操作失败！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	public void deleteStockInfoById(String stockId) throws BusinessException{
		try{
			stockDao.deleteStockInfoById(stockId);
		}catch(Exception e){
			log.error("办公用品删除操作失败！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
		
	}
	
	
	/**
	 * 根据申请编号取得办公用品详细申请单情报
	 * @param applyId  申请编号
	 * @return  办公用品详细申请单情报
	 */
	public StockApplyInfo getStockApplyById(Long applyId) throws BusinessException{
		try{
			return stockDao.findStockApplyById(applyId);
		}catch(Exception e){
			log.error("取得办公用品详细申请单失败！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	
	/**
	 * 保存办公用品申请信息
	 * @param officeApply	申请单信息
	 * @param stockApplyInfo	办公用品详细申请单
	 * @throws BusinessException  异常抛出
	 */
	public void saveStockApply(OfficeApply officeApply,StockApplyInfo stockApplyInfo) throws BusinessException{
		try{
			
			//Long applyId = new Long(UUIDLong.longUUID());
			//officeApply.setApplyId(applyId);
			
			Long applyId=this.officeApplyDao.createOfficeApply(officeApply);
			stockApplyInfo.setApplyId(applyId);
			
			this.stockDao.createStockApply(stockApplyInfo);
		}catch(Exception e){
			log.error("新增办公用品详细申请单失败！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	/**
	 * 取得待审核的办公用品申请列表
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId
	 * @return
	 */
	public List getStockApplyListForAutdit(String fieldName,String fieldValue,Long managerId) throws BusinessException{
		
		try{
			return this.stockDao.findStockApplyListForAutdit(fieldName, fieldValue, managerId);
		}catch(Exception e){
			log.error("取得待审核办公用品详细申请一览列表失败！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	public void deleteStockApplyByIds(String applyIds) throws BusinessException{
		try{
			this.officeApplyDao.deleteOfficeApplyByIds(applyIds);
		}catch(Exception e){
			log.error("删除详细申请一览列表失败！"+e.getMessage());
			throw new BusinessException("操作失败");
		}
	}
	
	/**
	 * 取得办公用品最大编号
	 * @return
	 */
	public Long getMaxStockNo(){
		return this.stockDao.getMaxStockNo();
	}
	
	public List getStockSummayByDep(boolean needPage) {
		return stockDao.getStockSummayByDep(needPage);
	}


	public List getStockSummay(boolean needPage){
		return this.stockDao.getStockSummay(needPage);
	}

    /**
     * 管理员管理的办公用品移交功能
     *
     */
    public void updateStockMangerBatch(long oldManager, long newManager,User user)
    {
    	this.updateStockMangerBatch(oldManager, newManager, user, true);
    }
    public void updateStockMangerBatch(long oldManager, long newManager,User user,boolean fromFlag){
    	if (fromFlag) {
            stockDao.updateStockMangerBatch(oldManager, newManager,user);
            stockDao.audiTransfer(oldManager, newManager);
		}else {
			stockDao.updateStockMangerBatch(oldManager, newManager, user,fromFlag);
		}
    }



    public OfficeApply getOfficeApplyById(Long applyId)
    {
       return officeApplyDao.getOfficeApply(applyId);
    }


    public void deleteStockApplyById(String applyIds) throws BusinessException
    {
        try
        {
            officeApplyDao.deleteOfficeApplyById(applyIds);
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }
    /**
     * ajax
     */
    public boolean checkHasDeleted(String stockid) {
    	boolean falg = false ;
    	
    	if(stockid != null){
    		StockInfo stockInfo = this.stockDao.findStockInfoById(Long.valueOf(stockid)) ;
    		if(stockInfo.getDeleteFlag() != 1){
    			falg = true ;
    		}
    	}
    return falg;
    }
}
