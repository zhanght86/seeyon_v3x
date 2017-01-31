/**
 * 
 */
package com.seeyon.v3x.plugin.identification.manager;

import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.IdentificationDog;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.plugin.identification.NoSuchIndentificationDogException;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-1-16
 */
public interface IdentificationManager {
	
	/**
	 * 得到所有的身份验证狗
	 * @return
	 */
	public List<IdentificationDog> getAllDog();
	
	/**
	 * 根据狗号取得身份验证狗对象
	 * 
	 * @param dogId
	 * @return
	 */
	public IdentificationDog getDog(String dogId);
	
	/**
	 * 检测狗头 如果是第一次，则把id写入config表 其它，则检测狗头是否和config表中记录的是否相等
	 * 
	 * @param dogHeadId
	 * @return true 正常，可以进行操作， false 不能使用该功能
	 */
	public boolean checkDogHead(String dogHeadId);

	/**
	 * 制作狗
	 * 
	 * @param dogId
	 *            狗的id
	 * @param name
	 *            名称
	 * @param isGenericDog
	 *            是否是通狗
	 * @param memberId
	 *            使用人的id
	 * @param isNeedCheckUsername
	 *            是否需要同时检测登录名和密码
	 * @param isMustUseDog
	 *            是否强制使用狗登录
	 * @param isEnabled
	 *            状态
	 * @throws BusinessException
	 */
	public void makeDog(String dogId, String name, boolean isGenericDog,
			long memberId, boolean isNeedCheckUsername, boolean isMustUseDog,
			boolean isEnabled,boolean canAccessMobile) throws BusinessException;

	/**
	 * 删除狗
	 * 
	 * @param dogId
	 */
	public void deleteDog(String... dogIds) throws BusinessException;

    /**
     * 根据加密后的dogId删除狗对象
     * @param dogId
     */
    public void deleteDogByEncodeId(String dogId) throws BusinessException;
    
	/**
	 * 启用或停用狗
	 * 
	 * @param enabled
	 *            true-启用 ，false - 停用
	 * @param dogIds
	 */
	public void enabledDog(boolean enabled, String... dogIds) throws BusinessException;

	/**
	 * 修改狗，狗号不变
	 * 
	 * @param dogId
	 *            需要被修改的狗的id
	 * @param name
	 *            名称
	 * @param isGenericDog
	 *            是否是通狗
	 * @param memberId
	 *            如果不是通狗，则需要使用人的id，如果是通狗，该参数无意义，可以为-1
	 * @param isNeedCheckUsername
	 *            是否需要同时检测登录名和密码
	 * @param isEnabled
	 *            状态
	 * @throws NoSuchIndentificationDogException 
	 * @throws BusinessException
	 */
	public void updateDog(String dogId, String name, boolean isGenericDog,
			long memberId, boolean isNeedCheckUsername, boolean isMustUseDog,
			boolean isEnabled,boolean canAccessMobile) throws NoSuchIndentificationDogException, BusinessException;

	/**
	 * 设置是否必须使用身份验证狗进行登录
	 * 
	 * @param p
	 * @param noCheckIP 不需要使用加密狗的IP段，如：128.2.2.*;10.0.0.*要用分号
	 */
	public void saveSystemMustUseDogLogin(boolean p, String noCheckIP);
	public boolean getSystemMustUseDogLogin();
	public String getSystemNoCheckIP();
    
    /**
     * 检查当前身份验证狗是否已在使用
     * @return 未使用 : null
     *         通狗: ""
     *         指定使用者: 使用者姓名
     */
    public String checkDogIsUsed(String dogId) throws BusinessException ;
    
}
