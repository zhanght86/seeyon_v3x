package com.seeyon.v3x.common.security.cap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seeyon.cap.common.security.SecurityCheckCAP;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.security.SecurityCheck;

public class SecurityCheckCAPImpl implements SecurityCheckCAP {

	@Override
	public boolean isLicit(HttpServletRequest request, HttpServletResponse response, ApplicationCategoryEnum appEnum, User user, Long objectId, Affair affair, Long preArchiveId) {
		return SecurityCheck.isLicit(request, response, appEnum, user, objectId, affair, preArchiveId);
	}

}