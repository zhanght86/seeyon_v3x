package com.seeyon.v3x.office.common.manager;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.common.domain.OfficeLossInfo;
import com.seeyon.v3x.office.common.domain.OfficeTypeInfo;

/**
 * 拥护申请单管理接口类
 * @author lindx
 *
 */
public interface OfficeCommonManager {
	
	/**
	 * 新建丢失报损信息
	 * @param lossInfo　　丢失报损信息对象
	 * @return　　编号
	 */
	public void createOfficeLoss(OfficeLossInfo lossInfo) throws BusinessException;
	
	
	/**
	 * 修改丢失报损信息
	 * @param lossInfo　丢失报损信息对象
	 */
	public void updateOfficeLoss(OfficeLossInfo lossInfo) throws BusinessException;
	
	
	/**
	 * 根据编号取得丢失报损信息情报
	 * @param lossId	编号
	 * @return　　丢失报损信息对象
	 */
	public OfficeLossInfo getOfficeLossById(Long lossId) throws BusinessException;
	
	
	/**
	 * 取得管理者的所有丢失报损信息一览列表
	 * @param fieldName  字段名
	 * @param fieldValue	字段值
	 * @param lossManager	管理者
	 * @return 丢失报损信息一览列表
	 */
	public List findOfficeLossList(String fieldName, String fieldValue, Long lossManager) throws BusinessException;
	
	
	/**
	 * 取得管理员负责的丢失报损一览列表
	 * @param fieldName
	 * @param fieldValue
	 * @param lossManager
	 * @return  丢失报损一览列表
	 */
	public List getLossOfManager(String fieldName,String fieldValue,Long lossManager) throws BusinessException;
	/**
	 * 批量修改丢失保存的删除状态为1
	 * @param lossIds  丢失保存编号集 格式：1000,10002,1004
	 * @throws BusinessException
	 */
	public void deleteOfficeLossByIds(String lossIds) throws BusinessException;
	
	
	/**
	 * 判断当前用户是否管理员
	 * @param user
	 * @return
	 */
	public boolean checkAdminDepart(User user);
	
	/**
	 * 判断当前管理员是否具有模块管理功能
	 * @param modelId
	 * @param user
	 * @return
	 */
	public int checkAdminModel(int modelId,User user);
	
	/**
	 * 判断当前用户是否有综合办公某模块管理功能
	 * @param user
	 * @return
	 */
	public boolean checkAdminModel(User user);
	
	/**
	 * 计算总行数
	 * @param sql
	 * @return
	 */
	public int getCount(String sql);
	
	/**
	 * 取得Query对象
	 * @param sql
	 * @return
	 */
	public SQLQuery createQuery(String sql);
	
	public void createTypeInfo(OfficeTypeInfo typeInfo) throws BusinessException;
	
	public void updateTypeInfo(OfficeTypeInfo typeInfo) throws BusinessException;
	
	public List getTypeInfoList(String fieldName,String fieldValue,long departId) throws BusinessException;
	
	public int deleteOfficeTypeByIds(String typeIds) throws BusinessException;
	
	public boolean checkDuplicate(OfficeTypeInfo typeInfo,boolean bCheckSelf);
	
	public OfficeTypeInfo getTypeInfoById(long typeId) throws BusinessException;
	/**
	 * 取得模块的类别列表
	 * @param modelId
	 * @return
	 */
	public List getModelTypes(String modelId,long departId);
	/***
	 * 取出车辆类别
	 * @param modelId
	 * @return
	 */
	public List getModelAuto(String modelId);
	
//	public Session getCurSession();
	
	public List getModelManagers(int modelId,User user);
	
	public List getTableRecords(String sql,Class clazz);
    /**
	 * 综合办公撤销申请的判断
	 * @param autoId
	 * @return
	 */
    public int selectOfficeAudit(String applyId);
    /**
     * 综合办公类型
     * @param typeId
     * @return
     */
    public OfficeTypeInfo getOfficeTypeInfoById(Long typeId);
    /**
     * 检查综合办公类别是否被其他模块占用
     * @param typeId
     * @return  没有占用 false  被占用 true
     */
    public boolean checkTypeInOffice(Long typeId);
    
    public List<OfficeTypeInfo> getAll(String  hql) ;
    
    public void updateOfficeTypeInfo() throws Exception;
}
