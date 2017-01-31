package com.seeyon.v3x.bbs.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * 
 */
public class Constant {
	public static enum SendType {
		normal, resend, forward
	}

	public static enum ConfigCategory {
		action_to_col_definition, col_flow_perm_policy
	}

	private static final String resource_baseName = "com.seeyon.v3x.bbs.resources.i18n.BBSResources";

	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(resource_baseName);

	public static String getString(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resourceBundle, key, parameters);
	}

	public static String getString4CurrentUser(String key, Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, key, parameters);
	}

	public static String getString(String key, Locale locale,
			Object... parameters) {
		return ResourceBundleUtil.getString(resource_baseName, locale, key,
				parameters);
	}
}