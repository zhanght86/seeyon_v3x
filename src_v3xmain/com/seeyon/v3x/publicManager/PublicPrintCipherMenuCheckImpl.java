/**
 * 
 */
package com.seeyon.v3x.publicManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.system.signet.manager.SignetManager;

/**
 * 电子印章判断权限
 * 
 */
public class PublicPrintCipherMenuCheckImpl implements MenuCheck {
	private static final Log log = LogFactory.getLog(PublicPrintCipherMenuCheckImpl.class);
	private SignetManager signetManager;
	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long) 电子印章的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {
		boolean hasSignet = false;
        try {
            hasSignet = signetManager.hasSignet(memberId);
		} catch (Exception e) {
			log.error("电子印章的权限判断异常", e);
		}
		return hasSignet;
	}
}
