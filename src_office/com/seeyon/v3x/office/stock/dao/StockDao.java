package com.seeyon.v3x.office.stock.dao;

import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.stock.domain.*;

public interface StockDao
{

    /**
     * 根据办公用品编码取得办公用品详细信息
     * 
     * @param stockId
     *            办公用品编号
     * @return 办公用品详细信息对象
     * @throws BusinessException
     *             异常
     */
    public StockInfo findStockInfoById(Long stockId);

    /**
     * 保存办公用品
     * 
     * @param stockInfo
     *            办公用品详细信息对象
     * @return StockId
     * @throws BusinessException
     *             异常
     */
    public void createStockInfo(StockInfo stockInfo);

    /**
     * 修改车辆详细信息
     * 
     * @param stockInfo
     */
    public void updateStockInfo(StockInfo stockInfo);

    /**
     * 取得管理者负责的办公用品详细信息一览列表
     * 
     * @param managerId
     * @return
     */
    public List findStockListByManager(String fieldName, String fieldValue, Long managerId);

    /**
     * 取得可以申请的办公用品详细信息一览列表
     * 
     * @param managerId
     * @return
     */
    public List findStockApplyList(List mgrIds, String fieldName, String fieldValue,
            Long managerId);

    /**
     * 批量修改办公用品的删除状态为1
     * 
     * @param stockIds
     *            办公用品编号集 格式：'1000','10002','1004'
     * @return int 数据库修改结果
     */
    public int deleteStockInfobyIds(List stockIds);

    /**
     * 单个修改单条办公用品的删除状态为1
     * 
     * @param stockId
     *            办公用品编号 1000
     * @return int 数据库修改结果
     */
    public int deleteStockInfoById(String stockId);

    /**
     * 新增办公用品详细申请单
     * 
     * @param stockApply
     *            办公用品详细申请单
     * @return 申请编号
     */
    public void createStockApply(StockApplyInfo stockApply);

    /**
     * 修改办公用品详细申请单信息 办公用品详细申请单
     * 
     * @param stockApply
     */
    public void updateStockApply(StockApplyInfo stockApply);

    /**
     * 根据申请编号取得办公用品详细申请单情报
     * 
     * @param applyId
     *            申请编号
     * @return 办公用品详细申请单情报
     */
    public StockApplyInfo findStockApplyById(Long applyId);

    /**
     * 取得待审核的办公用品申请列表
     * 
     * @param fieldName
     * @param fieldValue
     * @param managerId
     * @return
     */
    public List findStockApplyListForAutdit(String fieldName, String fieldValue, Long managerId);

    /**
     * 取得办公用品最大编号
     * 
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
    public void updateStockMangerBatch(long oldManager, long newManager, User user);
    public void updateStockMangerBatch(long oldManager, long newManager, User user,boolean fromFlag);

    /**
     * 管理员管理的办公用品申请移交功能
     * 
     */
    public void audiTransfer(final long oldManager, final long newManager);
}
