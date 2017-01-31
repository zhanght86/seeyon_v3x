package com.seeyon.v3x.system.util;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;

/**
 * 密码强度验证工具类
 * 
 * @author Administrator
 * 
 */
public class PwdStrengthValidationUtil {

	/**
	 * 获取是否启动密码强度验证值：
	 * 
	 * @return
	 */
	public static int getPwdStrengthValidationValue() {
		int pwdStrengthValidationValue = 0;
		SystemConfig systemConfig = (SystemConfig) ApplicationContextHolder.getBean("systemConfig");
		
		String pwdStrengthValidation = systemConfig.get(IConfigPublicKey.PWD_STRENGTH_VALIDATION_ENABLE);
		if (pwdStrengthValidation != null) {
			if (pwdStrengthValidation.equals("enable")) {
				pwdStrengthValidationValue = 1;
			}
			else if (pwdStrengthValidation.equals("disable")) {
				pwdStrengthValidationValue = 0;
			}
			else {
				pwdStrengthValidationValue = 0;
			}
		}

		return pwdStrengthValidationValue;
	}

}
