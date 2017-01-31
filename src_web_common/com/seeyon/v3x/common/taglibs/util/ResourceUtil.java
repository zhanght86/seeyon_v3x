/**
 * 
 */
package com.seeyon.v3x.common.taglibs.util;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-3-21
 */
public class ResourceUtil {

	private static final String RESOURCE_BASENAMW = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";

	private static final String DEFAULT_VALUE = "";

	/**
	 * 从后端，resource在后端指定com.seeyon.v3x.common.resources.SeeyonCommonResources
	 * 
	 * @param pageContext
	 * @param key
	 * @param paramters
	 * @return
	 */
	public static String getLocaleString(PageContext pageContext, String key,
			Object... parameters) {
		if (key == null) {
			return DEFAULT_VALUE;
		}

		return ResourceBundleUtil.getString(RESOURCE_BASENAMW, key, parameters);
	}
	
	/**
	 * 输出节点属性
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return 当属性值不存在时，不输出，否则输出 name="value"
	 */
	public static String getNodeAttribute(String attributeName, String attributeValue){
		return StringUtils.isBlank(attributeValue) ? "" : " " +attributeName + "=\"" + attributeValue + "\"";
	}

}
