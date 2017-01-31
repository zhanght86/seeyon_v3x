package com.seeyon.v3x.common.taglibs.util;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-13
 * @deprecated
 */
public class ExpressionUtil {
	/**
	 * 
	 * @param <T>
	 * @param tagName
	 * @param tagObject
	 * @param pageContext
	 * @param tagAttribute
	 * @param obj
	 * @param t
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T evaluateExpression(String tagName,
			BodyTagSupport tagObject, PageContext pageContext,
			String tagAttribute, Object obj, Class<T> t) {
		if (obj != null) {
			try {
				return (T) org.apache.taglibs.standard.tag.el.core.ExpressionUtil
						.evalNotNull(tagName, tagAttribute, obj.toString(), t,
								tagObject, pageContext);
			}
			catch (Exception ex) {
			}
		}

		return null;
	}
}
