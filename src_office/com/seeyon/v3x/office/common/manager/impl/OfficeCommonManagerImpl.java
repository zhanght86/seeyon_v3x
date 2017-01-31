package com.seeyon.v3x.office.common.manager.impl;

/**
 * 用户申请单管理实现类
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.common.dao.OfficeApplyDao;
import com.seeyon.v3x.office.common.dao.OfficeTypeDao;
import com.seeyon.v3x.office.common.domain.OfficeLossInfo;
import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;
import com.seeyon.v3x.office.common.manager.OfficeCommonManager;
import com.seeyon.v3x.office.common.OfficeModelType;

public class OfficeCommonManagerImpl implements OfficeCommonManager
{

    private static Logger log = Logger.getLogger(OfficeCommonManagerImpl.class);

    private OfficeApplyDao officeApplyDao;

    private OfficeTypeDao officeTypeDao;

    public void setOfficeApplyDao(OfficeApplyDao officeApplyDao)
    {
        this.officeApplyDao = officeApplyDao;
    }

    public void setOfficeTypeDao(OfficeTypeDao officeTypeDao)
    {
        this.officeTypeDao = officeTypeDao;
    }

    public void createOfficeLoss(OfficeLossInfo lossInfo) throws BusinessException
    {
        try
        {
            this.officeApplyDao.createOfficeLoss(lossInfo);
        }
        catch (Exception e)
        {
            throw new BusinessException("操作失败");
        }

    }

    public List findOfficeLossList(String fieldName, String fieldValue, Long lossManager)
            throws BusinessException
    {
        try
        {

            return this.officeApplyDao.findOfficeLossList(fieldName, fieldValue, lossManager);
        }
        catch (Exception e)
        {
            throw new BusinessException("操作失败");

        }

    }

    /**
     * 取得管理员负责的丢失报损一览列表
     * 
     * @param fieldName
     * @param fieldValue
     * @param lossManager
     * @return 丢失报损一览列表
     */
    public List getLossOfManager(String fieldName, String fieldValue, Long lossManager)
            throws BusinessException
    {
        List lossList = new ArrayList();
        try
        {
            lossList = this.officeApplyDao.findLossOfManager(fieldName, fieldValue, lossManager);

            return lossList;
        }
        catch (Exception e)
        {
            log.error("取得丢失报损一览列表错误！" + e.getMessage());
            throw new BusinessException("操作失败");

        }

    }

    public OfficeLossInfo getOfficeLossById(Long lossId) throws BusinessException
    {
        try
        {
            return this.officeApplyDao.findOfficeLossById(lossId);
        }
        catch (Exception e)
        {
            log.error("取得丢失报损记录错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }

    }

    public void updateOfficeLoss(OfficeLossInfo lossInfo) throws BusinessException
    {
        try
        {
            this.officeApplyDao.updateOfficeLoss(lossInfo);
        }
        catch (Exception e)
        {
            log.error("修改丢失报损记录错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }

    }

    /**
     * 批量修改丢失报损的删除状态为1
     * 
     * @param lossIds
     *            丢失保存编号集 格式：1000,10002,1004
     * @throws BusinessException
     */
    public void deleteOfficeLossByIds(String lossIds) throws BusinessException
    {

        try
        {
            officeApplyDao.deleteOfficeLossbyIds(lossIds);
        }
        catch (Exception e)
        {
            log.error("批量丢失报损删除操作失败！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 判断当前用户是否管理员
     * 
     * @param user
     * @return
     */
    public boolean checkAdminDepart(User user)
    {
        return this.officeApplyDao.checkAdminDepart(user);
    }

    /**
     * 判断当前管理员是否具有模块管理功能
     * 
     * @param modelId
     * @param user
     * @return
     */
    public int checkAdminModel(int modelId, User user)
    {
        return this.officeApplyDao.checkAdminModel(modelId, user);
    }

    /**
     * 判断当前用户是否有综合办公某模块管理功能
     * 
     * @param user
     * @return
     */
    public boolean checkAdminModel(User user)
    {
        return this.officeApplyDao.checkAdminModel(user);
    }

    /**
     * 计算总行数
     * 
     * @param sql
     * @return
     */
    public int getCount(String sql)
    {
        return this.officeApplyDao.getCount(sql,null,null);
    }

    /**
     * 取得Query对象
     * 
     * @param sql
     * @return
     */
    public SQLQuery createQuery(String sql)
    {
        return this.officeApplyDao.createQuery(sql);
    }

    public int deleteOfficeTypeByIds(String modelIds) throws BusinessException
    {
        try
        {
            return this.officeTypeDao.deleteOfficeTypeByIds(modelIds);
        }
        catch (Exception e)
        {
            log.error("批量类别信息删除操作失败！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    public List getTypeInfoList(String fieldName, String fieldValue, long departId)
            throws BusinessException
    {
        // TODO Auto-generated method stub
        return this.officeTypeDao.findTypeInfoList(fieldName, fieldValue, departId);
    }

    public void createTypeInfo(OfficeTypeInfo typeInfo) throws BusinessException
    {

        try
        {
            this.officeTypeDao.createTypeInfo(typeInfo);
        }
        catch (Exception e)
        {
            log.error("保存类别信息删除操作失败！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    public void updateTypeInfo(OfficeTypeInfo typeInfo) throws BusinessException
    {

        try
        {
            this.officeTypeDao.updateTypeInfo(typeInfo);
        }
        catch (Exception e)
        {
            log.error("保存类别信息删除操作失败！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    public OfficeTypeInfo getTypeInfoById(long typeId) throws BusinessException
    {

        try
        {
            return this.officeTypeDao.findOfficeTypeById(new Long(typeId));
        }
        catch (Exception e)
        {
            log.error("取得类别信息删除操作失败！" + e.getMessage());

            return null;
            // throw new BusinessException();
        }
    }

    public boolean checkDuplicate(OfficeTypeInfo typeInfo, boolean bCheckSelf)
    {
        return this.officeTypeDao.checkDuplicate(typeInfo, bCheckSelf);
    }

    public List getModelTypes(String modelId, long departId)
    {

        // List typeList = new ArrayList();

        // OfficeTypeInfo typeInfo = null;
        List resultList = this.officeTypeDao.findTypeOfModel(modelId, departId);
        return resultList;
        // Iterator it= resultList.iterator();

        // while(it.hasNext()){
        // typeInfo = (OfficeTypeInfo)it.next();
        // typeList.add(typeInfo.getTypeInfo().trim());
        // }
        // return typeList;
    }

    /***************************************************************************
     * 取出车辆类别
     * 
     * @param modelId
     * @return
     */
    public List getModelAuto(String modelId)
    {

        List typeList = new ArrayList();
        User curUser = CurrentUser.get();
        OfficeTypeInfo typeInfo = null;
        List resultList = this.officeTypeDao.findTypeOfAuto(modelId, curUser.getLoginAccount());

        Iterator it = resultList.iterator();

        while (it.hasNext())
        {
            typeInfo = (OfficeTypeInfo) it.next();
            typeList.add(typeInfo.getTypeInfo().trim());
        }
        return typeList;
    }

    public Session getCurSession()
    {
        return this.officeApplyDao.getCurSession();
    }

    public List getModelManagers(int modelId, User user)
    {
        return this.officeApplyDao.getModelManagers(modelId, user);
    }

    public List getTableRecords(String sql, Class clazz)
    {
        return this.officeApplyDao.getTableRecords(sql, clazz);
    }

    /***************************************************************************
     * 综合办公撤销申请的判断
     */
    public int selectOfficeAudit(String applyId)
    {
        return this.officeTypeDao.getSelectAutoAudit(applyId);
    }

    public OfficeTypeInfo getOfficeTypeInfoById(Long typeId)
    {
        return officeTypeDao.getOfficeTypeInfoById(typeId);
    }

    /**
     * 检查综合办公类别是否被其他模块占用
     * 
     * @param typeId
     * @return 没有占用 false 被占用 true
     */
    public boolean checkTypeInOffice(Long typeId)
    {
        OfficeTypeInfo officeTypeInfo = officeTypeDao.getOfficeTypeInfoById(typeId);
        if (officeTypeInfo == null)
        {
            return false;
        }
        boolean flag = false;
        switch (Integer.valueOf(officeTypeInfo.getModelId()))
        {
            case OfficeModelType.auto_type:
                flag = officeTypeDao.checkAutoTypeInOffice(typeId, officeTypeInfo);
                break;
            case OfficeModelType.asset_type:
                flag = officeTypeDao.checkAssetTypeInOffice(typeId, officeTypeInfo);
                break;
            case OfficeModelType.book_type:
                flag = officeTypeDao.checkBookTypeInOffice(typeId, officeTypeInfo);
                break;
            default:
                flag = officeTypeDao.checkStockTypeInOffice(typeId, officeTypeInfo);
                break;
        }
        return flag;
    }
    
    public List<OfficeTypeInfo> getAll(String  hql) {
    	return this.officeTypeDao.getAll(hql) ;
    }
    
    public void updateOfficeTypeInfo() throws Exception{
		String hql = "from OfficeTypeInfo officeTypeInfo where officeTypeInfo.modelId = '3'" ; 
		List<OfficeTypeInfo> list3 = this.getAll(hql) ;
		
		hql = "from OfficeTypeInfo officeTypeInfo where officeTypeInfo.modelId = '4'" ;
		
		List<OfficeTypeInfo> list4 = this.getAll(hql) ;
		
		if(list4 != null) {
			for(OfficeTypeInfo officeTypeInfo : list4) {
				officeTypeInfo.setModelId("3") ;
				updateTypeInfo(officeTypeInfo) ;
			}			
		}

		if(list3 != null) {
			for(OfficeTypeInfo officeTypeInfo : list3) {
				officeTypeInfo.setModelId("4") ;
				updateTypeInfo(officeTypeInfo) ;
			}				
		}
    }
}
