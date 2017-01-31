package com.seeyon.v3x.office.common.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.office.common.domain.OfficeLossInfo;

public interface OfficeApplyDao
{

    /**
     * 创建申请单
     * 
     * @param officeApply
     *            申请单对象
     * @return 生成的申请单对象主键ApplyId值
     */
    public Long createOfficeApply(OfficeApply officeApply);

    /**
     * 根据申请单ID取得申请单对象
     * 
     * @param applyId
     *            申请单对象
     * @return 申请单对象
     */
    public OfficeApply getOfficeApply(Long applyId);

    /**
     * 更新申请单情报
     * 
     * @param officeApply
     *            申请单对象
     */
    public void updateOfficeApply(OfficeApply officeApply);

    /**
     * 根据申请号ID集批量删除
     * 
     * @param applyIds
     *            申请号ID集
     * @return 操作记录数
     */
    public int deleteOfficeApplyByIds(String applyIds);

    /**
     * 新建丢失报损信息
     * 
     * @param lossInfo
     *            丢失报损信息对象
     * @return 编号
     */
    public void createOfficeLoss(OfficeLossInfo lossInfo);

    /**
     * 修改丢失报损信息
     * 
     * @param lossInfo
     *            丢失报损信息对象
     */
    public void updateOfficeLoss(OfficeLossInfo lossInfo);

    /**
     * 根据编号取得丢失报损信息情报
     * 
     * @param lossId
     *            编号
     * @return 丢失报损信息对象
     */
    public OfficeLossInfo findOfficeLossById(Long lossId);

    /**
     * 取得管理者的所有丢失报损信息一览列表
     * 
     * @param fieldName
     *            字段名
     * @param fieldValue
     *            字段值
     * @param lossManager
     *            管理者
     * @return 丢失报损信息一览列表
     */
    public List findOfficeLossList(String fieldName, String fieldValue,
            Long lossManager);

    /**
     * 取得管理员负责的丢失报损一览列表
     * 
     * @param fieldName
     * @param fieldValue
     * @param lossManager
     * @return 丢失报损一览列表
     */
    public List findLossOfManager(String fieldName, String fieldValue,
            Long lossManager);

    /**
     * 批量修改丢失报损的删除状态为1
     * 
     * @param stockIds
     *            丢失编号集 格式：1000,10002,1004
     * @return int 数据库修改结果
     */
    public int deleteOfficeLossbyIds(String lossIds);

    /**
     * 判断当前用户是否管理员
     * 
     * @param user
     * @return
     */
    public boolean checkAdminDepart(User user);

    /**
     * 判断当前管理员是否具有模块管理功能
     * 
     * @param modelId
     * @param user
     * @return
     */
    public int checkAdminModel(int modelId, User user);

    /**
     * 判断当前用户是否有综合办公某模块管理功能
     * 
     * @param user
     * @return
     */
    public boolean checkAdminModel(User user);

    /**
     * 计算总行数
     * 
     * @param sql
     * @return
     */
    public int getCount(String sql,Object[] values,Type[] types);

    /**
     * 取得Query对象
     * 
     * @param sql
     * @return
     */
    public SQLQuery createQuery(String sql);

    public Long getMaxApplyNo();

    public Session getCurSession();

    public List getModelManagers(int modelId, User user);

    /**
     * 得到用户对应模块的管理员
     * 
     * @param modelId
     * @param user
     * @return
     */
    public List getUserModelManagers(int modelId, User user);

    public List getTableRecords(String sql, Class clazz);
    
    public int deleteOfficeApplyById(final String applyId) ;
}
