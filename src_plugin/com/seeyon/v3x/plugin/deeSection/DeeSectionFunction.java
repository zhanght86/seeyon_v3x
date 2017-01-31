package com.seeyon.v3x.plugin.deeSection;

import com.seeyon.v3x.dee.client.service.DEEConfigService;

public class DeeSectionFunction {
	public static boolean isOpenPortalSection(){
		DEEConfigService deeService  = DEEConfigService.getInstance();
		return deeService.getModuleState(DEEConfigService.MODULENAME_PORTAL);
	}
}
