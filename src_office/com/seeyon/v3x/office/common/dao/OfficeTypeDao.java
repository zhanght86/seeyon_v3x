package com.seeyon.v3x.office.common.dao;

import java.util.List;

import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;

public interface OfficeTypeDao
{
    public OfficeTypeInfo getOfficeTypeInfoById(Long typeId);

    /**
     * 保存类别信息
     * 
     * @param typeInfo
     */
    public void createTypeInfo(OfficeTypeInfo typeInfo);

    public void updateTypeInfo(OfficeTypeInfo typeInfo);

    /**
     * 查询
     * 
     * @return
     */
    public List findTypeInfoList(String fieldName, String fieldValue, long departId);

    /**
     * 删除
     * 
     * @param modelIds
     * @return
     */
    public int deleteOfficeTypeByIds(String typeIds);

    public OfficeTypeInfo findOfficeTypeById(Long typeId);

    /**
     * 取得部门设置的指定模块的类别设置
     * 
     * @param modelId
     * @param departId
     * @return
     */
    public List findTypeOfModel(String modelId, long departId);

    /**
     * 取得部门设置的指定模块的类别设置
     * 
     * @param modelId
     * @return
     */
    public List findTypeOfAuto(String modelId, long departId);

    /**
     * 重复检查
     * 
     * @param typeInfo
     * @param bCheckSelf
     *            true: 排除本身 false:所有
     * @return
     */
    public boolean checkDuplicate(OfficeTypeInfo typeInfo, boolean bCheckSelf);

    /***************************************************************************
     * 综合办公撤销申请的判断
     * 
     * @param t_applylist
     * @teturn int
     */
    public int getSelectAutoAudit(String applyId);

    /**
     * 检查综合办公类别是否被其他模块占用
     * 
     * @param typeId
     * @return 没有占用 false 被占用 true
     */
    public boolean checkAutoTypeInOffice(Long typeId, OfficeTypeInfo officeTypeInfo);

    public boolean checkAssetTypeInOffice(Long typeId, OfficeTypeInfo officeTypeInfo);

    public boolean checkBookTypeInOffice(Long typeId, OfficeTypeInfo officeTypeInfo);

    public boolean checkStockTypeInOffice(Long typeId, OfficeTypeInfo officeTypeInfo);
    
    public List<OfficeTypeInfo> getAll(String hql) ;

}
