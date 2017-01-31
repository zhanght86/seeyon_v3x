package com.seeyon.v3x.office.auto.dao;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.support.page.Page;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoDepartInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.domain.AutoOffense;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * 车辆管理数据库相关操作接口类
 * @author lindx
 *
 */
public interface AutoDao {
	
	/**
	 * 保存新增车辆详细信息到数据库
	 * @param autoInfo  车辆详细信息对象
	 */
	public void createAutoInfo(AutoInfo autoInfo);
	/**
	 * 保存修改车辆详细信息到数据库
	 * @param autoInfo  车辆详细信息对象
	 */
	
	public void updateAutoInfo(AutoInfo autoInfo);
	
	
	/**
	 * 根据车牌号从数据库取得车辆详细信息对象
	 * @param autoId  车牌号
	 * @return  车辆详细信息持久对象
	 */
	public AutoInfo findAutoInfoById(String autoId);
	
	
	/**
	 * 根据查询条件取得车辆详细信息一览列表
	 * @param fieldName   查询字段
	 * @param fieldValue  字段值
	 * @param pageNo      当前页数
	 * @param pageSize	  每页记录数
	 * @return  车辆详细信息一览列表
	 */
	public Page queryAutoInfoList(String fieldName,String fieldValue,int pageNo,int pageSize) throws Exception;
	
	/**
	 * 根据车牌号取得该车的所有申请列表
	 * @param autoId 车牌号 
	 * @return
	 */
	public List findApplyListByAutoId(String autoId);
	/**
	 * 取得所有车辆状态==0的车辆信息
	 * @return
	 */
	public List findAllNormalAuto(Long domainId);
	
	public List queryAutoInfo(String fieldName,String fieldValue,Map keyMap,Long mgeId);
	/**
	 * 查询用户可以申请的车辆列表
	 * @param mgrIds：车辆管理员id串
	 * @param fieldName
	 * @param fieldValue
	 * @param keyMap
	 * @return
	 */
	public List queryAutoInfoApply(Long[] mgrIds,String fieldName,String fieldValue,Map keyMap,Long domainId);
	
	/**
	 * 根据车牌号批量逻辑删除车辆记录
	 * @param autoIds  车牌号集  '1000','1111111','222222'
	 * @return
	 */
	public int deleteAutoInfoByIds(List<String> autoIds);
	
	
	/**
	 * 创建车辆管理申请明细单
	 * @param applyInfo
	 */
	public void createAutoApply(AutoApplyInfo applyInfo);
	
	
	/**
	 * 批量删除车辆申请单
	 * @param applyIds  申请单号集
	 * @return  成功操作记录数
	 */
	public int deleteAutoApplyByIds(String applyIds);
	
	
	
	/**
	 * 根据申请号取得车辆申请单详细信息
	 * @param applyId  申请号
	 * @return 车辆申请单详细信息
	 */
	public AutoApplyInfo findAutoApplyById(Long applyId);
	
	
	/**
	 * 根据查询条件获取管理员维护违章车辆列表
	 * @param fieldName 
	 * @param fieldValue
	 * @param managerId  管理员
	 * @return  违章车辆一览列表
	 */
	public List findViolateListByManager(String fieldName,String fieldValue,Long managerId);
	
	/**
	 * 新增车辆违章信息 
	 * @param autoViolate
	 * @return
	 */
	public void createAutoViolate(AutoOffense autoViolate);
	
	/**
	 * 创建车辆出车/归车记录 
	 * @param departInfo  出车/归车对象
	 */
	public void createAutoDepartInfo(AutoDepartInfo departInfo);
	
	
	public void updateAutoDepartInfo(AutoDepartInfo departInfo);
	/**
	 * 取得车辆出车/归车详细信息
	 * @param applyId  编号
	 * @return 车辆出车/归车详细信息
	 */
	public AutoDepartInfo findAutoDepartById(Long applyId);
	
	
	/**
	 * 修改车辆违章记录
	 * @param autoViolate  车辆违章对象
	 */
	public void updateAutoViolate(AutoOffense autoViolate);
	
	/**
	 * 根据主键批量逻辑删除车辆违章记录
	 * @param applyIds  主键ID集合
	 * @return  更新的记录数
	 */
	public int deleteAutoViolateByIds(String applyIds);
	
	/**
	 * 根据主键查询违章车辆信息
	 * @param applyId  违章ID
	 * @return  车辆违章信息对象
	 */
	public AutoOffense findAutoViolateById(Long applyId);
	
	
	/**
	 * 根据查询条件取得管理员的所有车辆申请列表
	 * @param fieldName  查询字段
	 * @param fieldValue  查询值
	 * @param applyManager  管理者ID
	 * @return　　车辆申请列表
	 * @throws BusinessException　　异常抛出
	 */
	public List findAutoAuditList(String fieldName,String fieldValue,Long applyManager);
	
	/**
	 * 取得通过审核的所有车辆申请列表
	 * @param fieldName  字段名
	 * @param fieldValue  字段值
	 * @param applyManager  管理员
	 * @return   通过审核的车辆申请列表
	 */
	public List findAuditdeApplyList(String fieldName, String fieldValue, Long applyManager);
	
	
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
     *@param 
     */
    public void updateAutoMangerBatch(long oldManager,long newManager,User user);
    public void  updateAutoMangerBatch(long oldManager,long newManager,User user,long accountId);
    
    public void audiTransfer(long oldManager, long newManager);
    /**
     * 取得 在autoDepartTime 和autoBackTime交叉时间内 的 通过审批的申请
     * @param autoDepartTime
     * @param autoBackTime
     * @return
     */
    public List getSameTimeApply(String autoDepartTime,String autoBackTime,String autoId);
    
    /**
     * 根据车辆id 得到 没有审核的 
     * @param autoId
     * @return
     */
    public int getNotAuditApplyByAutoId(String autoId);
    
    /**
     * 根据id 查询是否已经出车
     * @param autoId
     * @return
     */
    public boolean getNotDepartByAutoId(String autoId);
    /**
     * 根据userid获得该用户未归还的车辆信息列表
     * @param userid
     * @return
     */
	public List getAutoBackListByUserId(String userid);

}	
