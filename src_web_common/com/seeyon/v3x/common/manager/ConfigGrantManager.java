package com.seeyon.v3x.common.manager;

/**
 * 普通用户权限管理接口
 * @author tanggl
 *
 */
public interface ConfigGrantManager {

	/**
	 * 判断某人是不是有某些权限
	 * branches_a8_v350_r_gov GOV-3001  唐桂林修改个人空间-已办事项会议链接
	 * @param accountId
	 * @param userId
	 * @param infoGrantType
	 * @return
	 */
	public boolean hasConfigGrant(Long accountId, Long userId, String configGrant, String grantType);
	
}
