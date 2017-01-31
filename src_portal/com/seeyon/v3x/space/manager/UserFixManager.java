/**
 * 
 */
package com.seeyon.v3x.space.manager;

import java.util.List;

import com.seeyon.v3x.organization.domain.V3xOrgMember;

/**
 * @author dongyj
 *
 */
public interface UserFixManager {

	/**
	 * 得到配置的值。
	 */
	public String getFixValue(Long memberId,String key);
	
	/**
	 * 保存或更新
	 * @param memberId
	 * @param key
	 * @param value
	 */
	public void saveOrUpdate(Long memberId,String key,String value);
	
	/**
	 * 删除用户的配置，key为null时，全部删除
	 * @param memberId
	 * @param key
	 */
	public void removeUserPro(Long memberId,String key);
	/**
	 * 根据访问权限清理userFix表
	 * @param spaceId
	 * @param securities
	 */
	public void updateUserFixBySecurity(Long spaceId,List<V3xOrgMember> memberIds);
}
