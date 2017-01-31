package com.seeyon.v3x.common.security;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;

/**
 * 安全访问控制
 * @author Mazc - 2010-4-15
 * 
 */
public interface SecurityControl {
	
	/**
	 * 安全校验，校验该主题是否有权限访问
	 * @param request
	 * @param ApplicationCategoryEnum 当前应用ID
	 * @param currentUserId 当前用户ID
	 * @param objectId 当前访问对象的id
	 * @param affair 协同和公文需要传入affair，其他应用为null
	 * @param preArchiveId 预归档ID （协同和公文）
	 * @return
	 */
	public boolean check(HttpServletRequest request, ApplicationCategoryEnum appEnum, Long currentUserId, Long objectId, Affair affair, Long preArchiveId);
}