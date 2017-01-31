package com.seeyon.v3x.menu.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 公文菜单检查
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class EdocRecMenuCheckerImpl implements MenuCheck
{
	private static final Log log = LogFactory.getLog(EdocRecMenuCheckerImpl.class);
    public boolean check(long memberId, long loginAccountId) {
        boolean flag = com.seeyon.v3x.common.SystemEnvironment.hasPlugin("edoc") && Functions.isEnableEdoc();;
        if(flag){
            try {
                return EdocRoleHelper.isEdocCreateRole(EdocEnum.edocType.recEdoc.ordinal());
            }
            catch (BusinessException e) {
    			log.error(e.getMessage(), e);
            }
        }
        return flag;
    }

}
