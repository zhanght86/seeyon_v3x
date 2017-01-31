package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.menu.manager.MenuCheck;

public class EdocSuperviseMenuCheckerImpl implements MenuCheck
{

    public boolean check(long memberId, long loginAccountId) {
        boolean flag = Functions.isEnableEdoc();
        return flag;
    }

}
