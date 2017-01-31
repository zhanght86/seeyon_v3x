package com.seeyon.v3x.office.auto.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.support.page.Page;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoDepartInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.domain.AutoOffense;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * 车辆管理接口定义类
 * @author Lindx
 *
 */
public interface AutoManager {
	
	/**
	 * 根据车牌号取得该车的所有申请列表
	 * @param autoId 车牌号 
	 * @return
	 */
	public List getApplyListByAutoId(String autoId);
	/**
	 * 取得所有车辆状态==0的车辆信息
	 * @return
	 */
	public List getAllNormalAuto(Long domainId);
	/**
	 * 根据查询条件取得车辆详细信息列表
	 * @param fieldName   查询字段名
	 * @param fieldValue  字段值
	 * @return  车辆详细信息列表
	 */
	public Page getAutoInfoList(String fieldName,String fieldValue,int pageNo,int pageSize) throws BusinessException;

	public List getAutoInfo(String fieldName,String fieldValue,Map keyMap,Long mgeId) throws BusinessException;
	public List getAutoInfoApply(String fieldName,String fieldValue,Map keyMap,Long domainId) throws BusinessException;
	/**
	 * 根据车辆编号取得车辆详细信息
	 * @param autoId  车辆编号
	 * @return  车辆详细信息
	 * @throws BusinessException  异常
	 */
	public AutoInfo getAutoInfoById(String autoId) throws BusinessException;
	
	/**
	 * 新增车辆详细信息记录
	 * @param autoInfo  车辆详细信息对象
	 * @return   持久车辆详细信息对象
	 * @throws BusinessException  异常
	 */
	public AutoInfo createAutoInfo(AutoInfo autoInfo) throws BusinessException;
	
	
	/**
	 * 修改车辆详细信息记录
	 * @param autoInfo  车辆详细信息对象
	 * @return   持久车辆详细信息对象
	 * @throws BusinessException  异常
	 */
	public AutoInfo updateAutoInfo(AutoInfo autoInfo) throws BusinessException;
	
	
	/**
	 * 根据车牌号删除车辆详细信息
	 * @param autoIds  车牌号集  格式：10001,10002,10003
	 * @throws BusinessException  异常抛出
	 */
	public void removeAutoInfoByIds(List<String> autoIds) throws BusinessException;
	
	/**
	 * 根据查询条件取得管理员的所有车辆申请列表
	 * @param fieldName  查询字段
	 * @param fieldValue  查询值
	 * @param applyManager  管理者ID
	 * @param outMember    兼职和副职人员
	 * @return　　车辆申请列表
	 * @throws BusinessException　　异常抛出
	 */
	public List getAutoAuditList(String fieldName,String fieldValue,Long applyManager) throws BusinessException;
	
	/**
	 * 车辆申请审批操作
	 * @param officeApply　　申请单审批意见
	 * @throws BusinessException　　异常抛出
	 */
	public void auditAutoApply(OfficeApply officeApply) throws BusinessException;
	
	/**
	 * 批量删除车辆申请记录
	 * @param applyIds  多个申请单号
	 * @throws BusinessException
	 */
	public void removeApplyByIds(String applyIds) throws BusinessException;
	
	/**
	 * 根据申请单号ID取得申请单对象
	 * @param applyId  申请单号
	 * @return  OfficeApply 申请单对象
	 * @throws BusinessException  异常抛出
	 */
	public OfficeApply getOfficeApplyById(Long applyId) throws BusinessException;
	
	/**
	 * 根据申请单号ID取得车辆申请单对象
	 * @param applyId 申请单号
	 * @return 车辆申请详细单
	 * @throws BusinessException  异常抛出
	 */
	public AutoApplyInfo getAutoApply(Long applyId) throws BusinessException;
	
	/**
	 * 保存车辆出车／归车申请单
	 * @param officeApply　　申请单信息
	 * @param applyInfo		车辆申请单信息	
	 * @throws BusinessException	异常抛出
	 */
	public void saveAutoApply(OfficeApply officeApply,AutoApplyInfo applyInfo) throws BusinessException;

	/**
	 * 取得审批通过的管理员申请一览列表
	 * @param applyManager  管理员
 	 * @return  申请一览列表
	 * @throws BusinessException 异常抛出
	 */
	public List getAuditedApplyList(String fieldName,String filedValue,Long applyManager) throws BusinessException;
	
	/**
	 * 车辆出车/归车操作
	 * @param departInfo
	 * @throws BusinessException
	 */
	public void createDepartAutoInfo(AutoDepartInfo departInfo) throws BusinessException;
	
	public void updateDepartAutoInfo(AutoDepartInfo departInfo) throws BusinessException;
	/**
	 * 取得车辆出车/归车信息
	 * @param applyId  申请号
	 * @return  车辆出车/归车信息
	 * @throws BusinessException
	 */
	public AutoDepartInfo getAutoDepartInfo(Long applyId) throws BusinessException;
	
	
	/**
	 * 根据查询条件取得车辆违章一览列表
	 * @param fieldName
	 * @param fieldValue
	 * @param managerId 管理员编号
	 * @return  车辆违章一览列表
	 * @throws BusinessException
	 */
	public List getAutoViolateList(String fieldName,String fieldValue,Long managerId) throws BusinessException;
	/**
	 * 新建车辆违章信息
	 * @param autoViolate 车辆违章信息对象
	 * @throws BusinessException  异常抛出
	 */
	public void createAutoViolate(AutoOffense autoViolate) throws BusinessException;
	
	/**
	 * 修改车辆违章信息
	 * @param autoViolate  车辆违章信息对象
	 * @throws BusinessException  异常抛出
	 */
	public void updateAutoViolate(AutoOffense autoViolate) throws BusinessException;
	
	/**
	 * 取得车辆违章详细信息
	 * @param applyId  编号
	 * @return   车辆违章详细信息
	 * @throws BusinessException  异常抛出
	 */
	public AutoOffense getAutoViolate(Long applyId) throws BusinessException;
	
	/**
	 * 批量删除车辆违章记录
	 * @param applyIds  编号集
	 * @throws BusinessException 异常抛出
	 */
	public void removeAutoViolateByIds(String applyIds) throws BusinessException;
	
	public void deleteAutoApplyByIds(String applyIds) throws BusinessException;
	/**
	 * 取得车辆违章最大编号
	 * @return
	 */
	public Long getMaxAutoLossNo();
	
	/**
	 * 取得车辆最大编号
	 * @return
	 */
	public Long getMaxAutoNo();

	/**
	 * 取得车辆未归车的数量
	 * @param autoId
	 * @return
	 */
	public int getAutoStatus(String autoId);
	
	public List getAutoSummayByDriver(Long userId, boolean needPage);
	
	public List getAutoSummayByDepart(Long userId, boolean needPage);
   /**
     * 管理员管理的车辆移交功能
     *
     */
    public void updateAutoMangerBatch(long oldManager,long newManager,User user);
    
    public void updateAutoMangerBatch(long oldManager, long newManager,User user,long accountId);

    
    public void deleteOfficeApplyById(String applyId) throws BusinessException;
    
    /**
     * 判断是否存在已经审批通过的交叉时间内的审评
     * @param applyId
     * @return
     */
    public boolean hasSameTimeAutoApply(Long applyId,String autoId);
    
    /**
     * 根据车辆编号查询 该车辆是否在使用当中 使用当中条件包括（1、出车 2、存在申请 没有审批）
     * @param autoId
     * @return
     */
    public boolean hasAutoApplyByAutoId(String autoId);
    /**
     * 根据用户id，获得该用户未归还的车辆信息列表
     * @param userid
     * @return
     */
	public List getAutoBackListByUserId(String userid);
}
