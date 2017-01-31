package com.seeyon.v3x.common.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;

/**
 * 安全控制-公告
 * @author Mazc - 2010-4-15
 */
public class SecurityControlBulletinImpl implements SecurityControl {
	private Log log = LogFactory.getLog(SecurityControlBulletinImpl.class);
	/**
	 * 判断是否有权限查看，V312暂不实现，直接允许
	 */
	public boolean check(HttpServletRequest request, ApplicationCategoryEnum appEnum, Long currentUserId, Long objectId, Affair affair, Long preArchiveId) {
		return true;
	}
}
