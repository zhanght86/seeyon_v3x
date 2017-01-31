package com.seeyon.v3x.office.auto.manager.impl;

/**
 * 
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.support.page.Page;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.office.auto.dao.AutoDao;
import com.seeyon.v3x.office.auto.domain.AutoApplyInfo;
import com.seeyon.v3x.office.auto.domain.AutoDepartInfo;
import com.seeyon.v3x.office.auto.domain.AutoInfo;
import com.seeyon.v3x.office.auto.domain.AutoOffense;
import com.seeyon.v3x.office.auto.manager.AutoManager;
import com.seeyon.v3x.office.common.OfficeModelType;
import com.seeyon.v3x.office.common.dao.OfficeApplyDao;
import com.seeyon.v3x.office.common.domain.OfficeApply;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Strings;

/**
 * 车辆管理实现类
 * @author <a href="mailto:zyadi1980@126.com">Yong Zhang</a>
 * @version 2008-04-09
 */
public class AutoManagerImpl implements AutoManager
{

    private static Logger log = Logger.getLogger(AutoManagerImpl.class);

    private AutoDao autoDao;
    
    private OfficeApplyDao officeApplyDao;

    public void setAutoDao(AutoDao autoDao)
    {
        this.autoDao = autoDao;
    }

    public void setOfficeApplyDao(OfficeApplyDao officeApplyDao)
    {
        this.officeApplyDao = officeApplyDao;
    }

    /**
     * 根据车牌号取得该车的所有申请列表
     * 
     * @param autoId
     *            车牌号
     * @return
     */
    public List getApplyListByAutoId(String autoId)
    {
        return this.autoDao.findApplyListByAutoId(autoId);
    }

    /**
     * 取得所有车辆状态==0的车辆信息
     * 
     * @return
     */
    public List getAllNormalAuto(Long domainId)
    {
        return this.autoDao.findAllNormalAuto(domainId);
    }

    /**
     * 根据查询条件取得车辆详细信息列表
     * 
     * @param fieldName
     *            查询字段名
     * @param fieldValue
     *            字段值
     * @return 车辆详细信息列表
     */
    public Page getAutoInfoList(String fieldName, String fieldValue,
            int pageNo, int pageSize) throws BusinessException
    {

        Page resultPage = null;
        try
        {
            resultPage = this.autoDao.queryAutoInfoList(fieldName, fieldValue,
                    pageNo, pageSize);
        }
        catch (Exception e)
        {
            log.error("查询车辆详细信息一览列表错误!" + e.getMessage());
            throw new BusinessException("操作失败");
        }

        return resultPage;
    }

    // 车辆登记的---车辆列表查询
    public List getAutoInfo(String fieldName, String fieldValue, Map keyMap,Long mgeId)
            throws BusinessException
    {

        return autoDao.queryAutoInfo(fieldName, fieldValue, keyMap,mgeId);
    }

    // 车量申请---车辆列表查询
    public List getAutoInfoApply(String fieldName, String fieldValue, Map keyMap,Long domainId)
            throws BusinessException
    {
        List idsList = officeApplyDao.getUserModelManagers(
                OfficeModelType.auto_type, CurrentUser.get());
        if (idsList == null || idsList.size() <= 0)
        {
            return new ArrayList();
        }
        int size = idsList.size();
        Long[] mgrIds = new Long[size];
        for (int i = 0; i < size; i++)
        {
        	mgrIds[i] = ((V3xOrgMember) idsList.get(i)).getId();
        }
        return autoDao.queryAutoInfoApply(mgrIds, fieldName,
                fieldValue, keyMap,domainId);
    }

    /**
     * 根据车辆编号取得车辆详细信息
     * 
     * @param autoId
     *            车辆编号
     * @return 车辆详细信息
     * @throws BusinessException
     *             异常
     */
    public AutoInfo getAutoInfoById(String autoId) throws BusinessException
    {

        AutoInfo autoInfo = null;
        try
        {
            autoInfo = autoDao.findAutoInfoById(autoId);

        }
        catch (Exception e)
        {
            log.error("根据车牌号(" + autoId + ")取得车辆详细信息错误!" + e.getMessage());
            throw new BusinessException("操作失败");
        }
        return autoInfo;
    }

    /**
     * 新增车辆详细信息记录
     * 
     * @param autoInfo
     *            车辆详细信息对象
     * @return 持久车辆详细信息对象
     * @throws BusinessException
     *             异常
     */
    public AutoInfo createAutoInfo(AutoInfo autoInfo) throws BusinessException
    {

        try
        {

            autoDao.createAutoInfo(autoInfo);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("创建车辆详细信息记录错误!" + e.getMessage());
            throw new BusinessException("操作失败");
        }
        return autoInfo;
    }

    /**
     * 修改车辆详细信息记录
     * 
     * @param autoInfo
     *            车辆详细信息对象
     * @return 持久车辆详细信息对象
     * @throws BusinessException
     *             异常
     */
    public AutoInfo updateAutoInfo(AutoInfo autoInfo) throws BusinessException
    {

        try
        {

            autoDao.updateAutoInfo(autoInfo);

        }
        catch (Exception e)
        {
            log.error("修改车辆详细信息记录错误!" + e.getMessage());
            throw new BusinessException("操作失败");
        }

        return autoInfo;
    }

    /**
     * 根据车牌号删除车辆详细信息
     * 
     * @param autoIds
     *            车牌号集 格式：10001,10002,10003
     * @throws BusinessException
     *             异常抛出
     */
    public void removeAutoInfoByIds(List<String> autoIds) throws BusinessException
    {

        /*StringBuffer sb = new StringBuffer();
        StringTokenizer strTokens = new StringTokenizer(autoIds, ",");
        while (strTokens.hasMoreTokens())
        {
            String stockId = strTokens.nextToken();
            if (sb.length() == 0)
            {
                sb.append("'" + stockId + "'");
            }
            else
            {
                sb.append(",'" + stockId + "'");
            }
        }

        autoIds = sb.toString();*/

        try
        {

            autoDao.deleteAutoInfoByIds(autoIds);

        }
        catch (Exception e)
        {
            log.error("批量逻辑删除车辆详细信息记录错误!" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    public void getAutoApplyList(Long applyManager) throws BusinessException
    {

    }

    /**
     * 保存车辆出车／归车申请单
     * 
     * @param officeApply
     *            申请单信息
     * @param applyInfo
     *            车辆申请单信息
     * @throws BusinessException
     *             异常抛出
     */
    public void saveAutoApply(OfficeApply officeApply, AutoApplyInfo applyInfo)
            throws BusinessException
    {

        try
        {
            // 保存申请单，并返回申请单ID

            // Long applyId =new Long(UUIDLong.longUUID());
            // officeApply.setApplyId(applyId);

            Long applyId = this.officeApplyDao.createOfficeApply(officeApply);

            applyInfo.setApplyId(applyId);

            this.autoDao.createAutoApply(applyInfo);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("用户车辆申请错误！");
            throw new BusinessException("操作失败");

        }

    }

    /**
     * 根据查询条件取得管理员的所有车辆申请列表
     * 
     * @param fieldName
     *            查询字段
     * @param fieldValue
     *            查询值
     * @param applyManager
     *            管理者ID
     * @return 车辆申请列表
     * @throws BusinessException
     *             异常抛出
     */
    public List getAutoAuditList(String fieldName, String fieldValue,
            Long applyManager) throws BusinessException
    {

        List auditList = this.autoDao.findAutoAuditList(fieldName, fieldValue,
                    applyManager);
       
        return auditList;
    }

    /**
     * 车辆申请审批操作
     * 
     * @param officeApply
     *            申请单审批意见
     * @throws BusinessException
     *             异常抛出
     */
    public void auditAutoApply(OfficeApply officeApply)
            throws BusinessException
    {

        // 取得申请单对象
        OfficeApply applyObj = null;
        try
        {
            applyObj = this.officeApplyDao.getOfficeApply(officeApply
                    .getApplyId());
        }
        catch (Exception e)
        {
            log.error("数据库不存在申请单号：" + officeApply.getApplyId()
                            + e.getMessage());
            throw new BusinessException("操作失败");
        }

        applyObj.setApplyState(officeApply.getApplyState()); // 申请状态
        applyObj.setApplyMemo(officeApply.getApplyMemo()); // 备注
        applyObj.setAuditTime(new Date()); // 审核日期
        applyObj.setApplyExam(officeApply.getApplyExam()); // 审批人

        try
        {
            this.officeApplyDao.updateOfficeApply(applyObj);
        }
        catch (Exception e)
        {
            log.error("修改申请单情报错误！" + officeApply.getApplyId() + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 批量删除车辆申请记录
     * 
     * @param applyIds
     *            多个申请单号
     * @throws BusinessException
     */
    public void removeApplyByIds(String applyIds) throws BusinessException
    {
        try
        {

            this.officeApplyDao.deleteOfficeApplyByIds(applyIds);

            this.autoDao.deleteAutoApplyByIds(applyIds);
        }
        catch (Exception e)
        {
            log.error("批量删除车辆申请错误！(" + applyIds + ")" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 根据申请单号ID取得申请单对象
     * 
     * @param applyId
     *            申请单号
     * @return OfficeApply 申请单对象
     * @throws BusinessException
     *             异常抛出
     */
    public OfficeApply getOfficeApplyById(Long applyId)
            throws BusinessException
    {

        try
        {
            return this.officeApplyDao.getOfficeApply(applyId);

        }
        catch (Exception e)
        {

            throw new BusinessException("操作失败");
        }
    }

    /**
     * 根据申请单号ID取得车辆申请单对象
     * 
     * @param applyId
     *            申请单号
     * @return 车辆申请详细单
     * @throws BusinessException
     *             异常抛出
     */
    public AutoApplyInfo getAutoApply(Long applyId) throws BusinessException
    {
        try
        {
            return this.autoDao.findAutoApplyById(applyId);
        }
        catch (Exception e)
        {
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 取得审批通过的管理员申请一览列表
     * 
     * @param applyManager
     *            管理员
     * @return 申请一览列表
     * @throws BusinessException
     *             异常抛出
     */
    public List getAuditedApplyList(String fieldName, String fieldValue,
            Long applyManager) throws BusinessException
    {
        List applyList = new ArrayList();
        try
        {
            applyList = this.autoDao.findAuditdeApplyList(fieldName,fieldValue, applyManager);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("取得审核通过的车辆申请列表错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
        return applyList;
    }

    /**
     * 车辆出车/归车操作
     * 
     * @param departInfo
     * @throws BusinessException
     */
    public void createDepartAutoInfo(AutoDepartInfo departInfo)
            throws BusinessException
    {
        try
        {
            this.autoDao.createAutoDepartInfo(departInfo);
        }
        catch (Exception e)
        {
            log.error("车辆出车/归车记录保存失败:" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    public void updateDepartAutoInfo(AutoDepartInfo departInfo)
            throws BusinessException
    {
        try
        {
            this.autoDao.updateAutoDepartInfo(departInfo);
        }
        catch (Exception e)
        {
            log.error("车辆出车/归车记录保存失败:" + e.getMessage());
            throw new BusinessException("车辆出车/归车操作错误!");
        }
    }

    /**
     * 取得车辆出车/归车信息
     * 
     * @param applyId
     *            申请号
     * @return 车辆出车/归车信息
     * @throws BusinessException
     */
    public AutoDepartInfo getAutoDepartInfo(Long applyId)
            throws BusinessException
    {
        try
        {
            return this.autoDao.findAutoDepartById(applyId);
        }
        catch (Exception e)
        {
            log.error("取得车辆出车/归车记录失败:" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 根据查询条件取得车辆违章一览列表
     * 
     * @param fieldName
     * @param fieldValue
     * @param managerId
     *            管理员编号
     * @return 车辆违章一览列表
     * @throws BusinessException
     */
    public List getAutoViolateList(String fieldName, String fieldValue,
            Long domainId) throws BusinessException
    {
        List violateList = new ArrayList();
        try
        {
            violateList = this.autoDao.findViolateListByManager(fieldName,
                    fieldValue, domainId);
        }
        catch (Exception e)
        {
            log.error("取得管理员的维护车辆违章一览列表错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
        return violateList;
    }

    /**
     * 新建车辆违章信息
     * 
     * @param autoViolate
     *            车辆违章信息对象
     * @throws BusinessException
     *             异常抛出
     */
    public void createAutoViolate(AutoOffense autoViolate)
            throws BusinessException
    {
        try
        {
            this.autoDao.createAutoViolate(autoViolate);
        }
        catch (Exception e)
        {
            log.error("新建车辆违章信息错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 修改车辆违章信息
     * 
     * @param autoViolate
     *            车辆违章信息对象
     * @throws BusinessException
     *             异常抛出
     */
    public void updateAutoViolate(AutoOffense autoViolate)
            throws BusinessException
    {
        try
        {
            this.autoDao.updateAutoViolate(autoViolate);
        }
        catch (Exception e)
        {
            log.error("修改车辆违章信息错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 取得车辆违章详细信息
     * 
     * @param applyId
     *            编号
     * @return 车辆违章详细信息
     * @throws BusinessException
     *             异常抛出
     */
    public AutoOffense getAutoViolate(Long applyId) throws BusinessException
    {

        AutoOffense autoViolate = null;
        try
        {
            autoViolate = this.autoDao.findAutoViolateById(applyId);
        }
        catch (Exception e)
        {

            throw new BusinessException("操作失败");
        }
        return autoViolate;
    }

    /**
     * 批量删除车辆违章记录
     * 
     * @param applyIds
     *            编号集
     * @throws BusinessException
     *             异常抛出
     */
    public void removeAutoViolateByIds(String applyIds)
            throws BusinessException
    {

        try
        {
            this.autoDao.deleteAutoViolateByIds(applyIds);
        }
        catch (Exception e)
        {
            log.error("批量删除车辆违章信息错误！" + e.getMessage());
            throw new BusinessException("操作失败");
        }
    }

    public void deleteAutoApplyByIds(String applyIds) throws BusinessException
    {
        try
        {
            this.officeApplyDao.deleteOfficeApplyByIds(applyIds);
        }
        catch (Exception e)
        {
            throw new BusinessException("操作失败");
        }
    }
    public void deleteOfficeApplyById(String applyId) throws BusinessException
    {
        try
        {
            this.officeApplyDao.deleteOfficeApplyById(applyId);
        }
        catch (Exception e)
        {
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 取得车辆违章最大编号
     * 
     * @return
     */
    public Long getMaxAutoLossNo()
    {
        return this.autoDao.getMaxAutoLossNo();
    }

    /**
     * 取得车辆最大编号
     * 
     * @return
     */
    public Long getMaxAutoNo()
    {
        return this.autoDao.getMaxAutoNo();
    }

    /**
     * 取得车辆未归车的数量
     * 
     * @param autoId
     * @return
     */
    public int getAutoStatus(String autoId)
    {
        return this.autoDao.getAutoStatus(autoId);
    }

    public List getAutoSummayByDriver(Long userId, boolean needPage)
    {
        return this.autoDao.getAutoSummayByDepart(userId, needPage);
    }

    public List getAutoSummayByDepart(Long userId, boolean needPage)
    {
        return this.autoDao.getAutoSummayByDriver(userId, needPage);
    }
    /**
     * 管理员管理的车辆移交功能
     *
     */
    public void updateAutoMangerBatch(long oldManager, long newManager,User user)
    {
        this.updateAutoMangerBatch(oldManager, newManager, user,0l);
         
    }
    
    public void updateAutoMangerBatch(long oldManager, long newManager,User user,long accountId){
    	if (accountId == 0l) {
    		this.autoDao.updateAutoMangerBatch(oldManager, newManager,user);
            this.autoDao.audiTransfer(oldManager, newManager);
		}else{
    		this.autoDao.updateAutoMangerBatch(oldManager, newManager, user, accountId);
    	}
    	 
    }
    
    public boolean hasSameTimeAutoApply(Long applyId,String autoId) {
    	AutoApplyInfo autoApply  = this.autoDao.findAutoApplyById(applyId);
    	if(autoApply == null){
    		return false;
    	}
    	String autoDepartTime = autoApply.getAutoDepartTime();
    	String autoBackTime   = autoApply.getAutoBackTime();
    	List applyList = this.autoDao.getSameTimeApply(autoDepartTime, autoBackTime,autoId);
    	if(applyList.size() >0){
    		for(int i = 0; i < applyList.size(); i++){
    			AutoApplyInfo applyInfo = (AutoApplyInfo)applyList.get(i);
    			AutoDepartInfo departInfo = autoDao.findAutoDepartById(applyInfo.getApplyId());
    			if(departInfo != null){
    				String departTime = departInfo.getAutoDepartTime();
    				String backTime = departInfo.getAutoBackTime();
    				if(Strings.isNotBlank(departTime) && Strings.isNotBlank(backTime)){
    					if(autoDepartTime.compareTo(backTime) > 0){
    						return true;
    					}
    				}
    			} 
    		}
    		return false;
    	}
    	return true;
    }
    
    
    public boolean hasAutoApplyByAutoId(String autoId) {
    	return this.autoDao.getNotAuditApplyByAutoId(autoId)>0 || this.autoDao.getNotDepartByAutoId(autoId) ;
    }

	@Override
	public List getAutoBackListByUserId(String userid) {
		List list= autoDao.getAutoBackListByUserId(userid);
		return list;
	}
}
