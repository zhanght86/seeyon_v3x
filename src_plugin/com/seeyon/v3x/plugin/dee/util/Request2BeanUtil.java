package com.seeyon.v3x.plugin.dee.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.dee.common.base.util.ReflectUtil;

/**
 * 将对象转换成bean 所有的参数值并生成 一个相应的对象返回
 * 
 * @author liuls
 * 
 */
public class Request2BeanUtil {
	private final static Log log = LogFactory.getLog(Request2BeanUtil.class);

	/**
	 * 
	 * @param request存储着表单的HttpServletRequest对象
	 * @param bean要封装的表单Bean
	 * @return 封装好的表单Bean
	 */
	public static Object setBeanValue(String key, Object value, Object bean) {
		try {
			ReflectUtil.invokeMethodByFieldName(bean, key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return bean;
	}

	/**
	 * @description 解析request值封装到bean里去
	 * @date 2011-12-28
	 * @author liuls
	 * @param request
	 *            request对象
	 * @param bean
	 *            需要封装值的对象
	 * @return 已经封装好值的对象
	 */
	public static Object parseRequest(HttpServletRequest request, Object bean) {
		// 取得所有参数列表
		Enumeration enums = request.getParameterNames();

		// 遍历所有参数列表
		while (enums.hasMoreElements()) { // 只针对单个值，不支持数组
			Object obj = enums.nextElement();
			try {
				// 取得这个参数在Bean中的数据类型
				Class cls = PropertyUtils.getPropertyType(bean, obj.toString());
				// 把相应的数据转换成对应的数据类型
				if (cls != null) {
					Object beanValue = ConvertUtils.convert(
							request.getParameter(obj.toString()), cls);
					PropertyUtils.setProperty(bean, obj.toString(), beanValue);
				}
			} catch (Exception e) {
				log.error("转换" + bean + ":" + obj.toString() + " 时出错"+ e.getMessage(), e);

			}
		}
		return bean;
	}

}
