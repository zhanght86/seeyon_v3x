package com.seeyon.v3x.menu.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.menu.manager.MenuCheck;

public class EdocSignReportMenuCheckerImpl implements MenuCheck
{
	private final static Log log = LogFactory
			.getLog(EdocSignReportMenuCheckerImpl.class);
    public boolean check(long memberId, long loginAccountId) {
        boolean hasPlugin = com.seeyon.v3x.common.SystemEnvironment.hasPlugin("edoc");
        boolean flag = !hasPlugin || (hasPlugin && Functions.isEnableEdoc());
        if(flag){
            try {
                return EdocRoleHelper.isEdocCreateRole(EdocEnum.edocType.signReport.ordinal());
            }
            catch (BusinessException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }
        return flag;
    }
}
