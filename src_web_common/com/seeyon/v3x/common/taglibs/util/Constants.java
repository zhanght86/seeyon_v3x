package com.seeyon.v3x.common.taglibs.util;

import java.text.MessageFormat;
import java.util.Properties;

import com.seeyon.v3x.util.PropertiesUtil;

/**
 * 标签库常量：常量值定义在com/seeyon/v3x/common/taglibs/util/Constants.properties
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-4
 */
public class Constants {
	private final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(Constants.class);
	/**
	 * 默认值
	 */
	private static final String DEFAULT_VALUE = "";

	/**
	 * Properties文件路径
	 */
	private static final String properties_file_path = "com/seeyon/v3x/common/taglibs/util/Constants.properties";

	/**
	 * 
	 */
	private static Properties props = null;
	static {
		props = PropertiesUtil.getFromClasspath(properties_file_path);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		if (key == null) {
			return DEFAULT_VALUE;
		}

		return props.getProperty(key);
	}

	/**
	 * 
	 * @param key
	 * @param parameters
	 * @return
	 */
	public static String getString(String key, Object... parameters) {
		if (key == null) {
			return DEFAULT_VALUE;
		}

		try {
			String baseMsg = props.getProperty(key);

			if (parameters != null) {
				return MessageFormat.format(baseMsg, parameters);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return key;
	}

}
