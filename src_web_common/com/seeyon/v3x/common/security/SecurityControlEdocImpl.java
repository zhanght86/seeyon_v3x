package com.seeyon.v3x.common.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.util.Strings;

/**
 * 安全控制-公文
 * @author Mazc - 2010-4-15
 */
public class SecurityControlEdocImpl implements SecurityControl {
	private Log log = LogFactory.getLog(SecurityControlEdocImpl.class);
	
	private SecurityControlColImpl colSecurityControlManager;
	
	public void setColSecurityControlManager(
			SecurityControlColImpl colSecurityControlManager) {
		this.colSecurityControlManager = colSecurityControlManager;
	}
	
	/**
	 * 判断是否有权限查看流程 - 直接调用协同的权限
	 * <br>合法的权限规则:<ul>
	 * 	<li>我是事项所属人</li>
	 * 	<li>我是所属人的代理人</li>
	 * 	<li>关联协同，验证该协同的前一协同是否有权限</li>
	 * 	<li>流程的督办人</li>
	 * 	<li>归档，验证是否有查看权限</li>
	 * </ul>
	 * @see SecurityControlColImpl
	 * @param ApplicationCategoryEnum 应用ID
	 * @param currentUserId 当前用户ID
	 * @param objectId 当前访问对象ID
	 * @param preObjectId 前一object ID
	 * @param affair 
	 * @param preArchiveId 预归档ID
	 * @return 
	 */
	public boolean check(HttpServletRequest request, ApplicationCategoryEnum appEnum, Long currentUserId, Long objectId, Affair affair, Long preArchiveId) {
		boolean isCanAccess = colSecurityControlManager.check(request, appEnum, currentUserId, objectId, affair, preArchiveId);
		return isCanAccess;
	}

}
