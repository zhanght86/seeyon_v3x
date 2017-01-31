package com.seeyon.v3x.main;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * 读取国际化资源的值
 * User: mazc Date: 2007-6-29
 */
public class Constant {
    
    public static enum USER_TYPE {
        system, //系统管理员
        unit,   //单位管理员
        group, //集团管理员
        user, //前台用户
        audit, //审计管理员
        secret, //安全管理员
    }
    
    /**
     * 链接打开类型，用于HTMLTemplate的后台拼接URL时的生成链接
     */
    public static enum OPEN_TYPE {
        open, //弹出
        href, //直接超链
    }
    
    public static final String resource_main = "com.seeyon.v3x.main.resources.i18n.MainResources";
    public static final String resource_common = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";

	/**
	 * 取得MainResource国际化资源的值
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String getValueFromMainRes(String key, String... params) {
		return ResourceBundleUtil.getString(resource_main, key, params);
	}

	/**
	 * 取得应用的类型
	 * 参数 int类型　，如key为application.0.label返回　"协同"
	 */
	public static String getApplicationCategory(int category) {
		return ResourceBundleUtil.getString(resource_common, "application."+category+".label");
	}
	
    /**
     * 取得CommonResource国际化资源的值
     * @param key
     * @param parameters
     * @return
     */
	public static String getValueFromCommonRes(String key) {
		return ResourceBundleUtil.getString(resource_common, key);
	}
}
