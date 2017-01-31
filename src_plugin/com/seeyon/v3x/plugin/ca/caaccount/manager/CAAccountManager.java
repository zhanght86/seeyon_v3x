package com.seeyon.v3x.plugin.ca.caaccount.manager;

import java.util.List;
import com.seeyon.v3x.plugin.ca.caaccount.domain.CAAccount;
import com.seeyon.v3x.plugin.ca.caaccount.webmodel.WebCAAccountVo;
import com.seeyon.v3x.plugin.ca.caaccount.webmodel.WebImportCAAccountResultVo;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.excel.DataRecord;

public interface CAAccountManager {
	/**
	 * 管理员为新来的员工新建一条协同帐号与ca key的映射关系
	 * @param keyNum
	 * @param loginName
	 */
	public void addCAAccount(String keyNum, long memberId);
    
    /**
     * 根据loginName修改ca key序号
     * @param loginName
     * @param webCAAccount 
     */
    public void updateCAKeyByMemberId(long memberId, CAAccount webCAAccount);
    
    /**
     * 判断该memberId是否已存在于ca帐号中
     * @param memberId
     */
    public boolean isMemberIdExist(long memberId);
    
    /**
     * 判断该KeyNum是否已存在于ca账号中
     * @param keyNum
     */
    public boolean isKeyNumExist(String keyNum);

    /**
     * 根据loginName查询ca帐号
     * @param loginName
     */
    public CAAccount findByLoginName(String loginName);
    
    /**
     * 根据loginName查询ca帐号
     * @param memberId
     */
    public CAAccount findByMemberId(long memberId);
    
    /**
     * 根据loginName查询ca key序号
     * @param loginName
     */
    public String findKeyNum(String loginName);
    
    /**
     * 根据keyNum查询ca帐号
     * @param keyNum
     */
    public CAAccount findByKeyNum(String keyNum);
    
    /**
     * 根据memberId删除CA帐号
     * @param memberIds
     */
    public boolean deleteBy(long[] memberIds);
    
    /**
     * 根据keyNum查询协同帐号登录名
     * @param keyNum
     */
    public String findLoginName(String keyNum);
    
    /**
     * 根据条件查询ca帐号
     * @param condition
     * @param value
     */
    public List<WebCAAccountVo> searchByCondition(String condition, String value) throws BusinessException;
    
    /**
     * 导出ca帐号
     * @param caAccountList
     */
    public DataRecord exportCAAccount(List<WebCAAccountVo> webCAAccountVoList);
    
    /**
     * 导入ca帐号
     * @param caAccountList
     */
    public List<WebImportCAAccountResultVo> importCAAccount(String repeat, List<List<String>> caAccountList);
    
    /**
     * 保存CA登陆验证配置
     * @param isMustUseCALogin
     * @param noCheckIp
     */
    public void saveSystemMustUseCALogin(boolean isMustUseCALogin, String noCheckIp);
    
    /**
     * 获取config中登陆是否必须验证CA
     * @return
     */
    public boolean getSystemMustUseCALogin();
    
    /**
     * 获取config中不用验证CA的IP
     * @return
     */
    public String getSystemNoCheckIP();
    
    /**
     * 根据登陆名查找KeyNum
     * @param loginName
     * @return
     */
    public String findKeyNumByLoginName(String loginName);
		
}
