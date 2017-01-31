/**
 * 
 */
package com.seeyon.v3x.main.section;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 工具类
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-27
 */
public class SectionUtils {

	private static Map<Integer, String> dataPattern = new HashMap<Integer, String>();
	
	private static int maxLength = 100;

	/**
	 * 设置“宽度-日期格式”影射
	 * 
	 * @param dataPattern
	 */
	public void setDataPattern(Map<String, String> dataPattern) {
		Set<Map.Entry<String, String>> en = dataPattern.entrySet();
		for (Map.Entry<String, String> entry : en) {
			SectionUtils.dataPattern.put(Integer.parseInt(entry.getKey()), entry.getValue());
		}
	}
	
	/**
	 * 设置这个屏幕显示的字节数
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(int maxLength) {
		SectionUtils.maxLength = maxLength;
	}

	/**
	 * 后端不直接输出文本，在前端用JS实现国际化
	 * 
	 * @param key
	 *            统一放在/apps_res/v3xmain/js/i18n/下
	 * @return
	 */
	public static String toJSI18N(String key) {
		return "${" + key + "}";
	}

	/**
	 * 根据布局生成日期字符创, 当宽度小于2时，不返回值，太小了
	 * 
	 * @param date
	 * @param width
	 * @return
	 */
	public static String toDatetime(Date date, int width) {
		if (date == null) {
			return null;
		}

		String pattern = dataPattern.get(width);
		if(pattern == null){
			pattern = "MM-dd HH:mm";
		}

		return Datetimes.format(date, pattern);
	}
	
	/**
	 * 得到最多显示的字节数，按照全屏108byte计算
	 * 
	 * @param width
	 * @return
	 */
	public static int getTextMaxLength(int width){
		return maxLength * width /10;
	}

	/**
	 * 检测
	 * 
	 * @param str
	 * @return
	 */
	public static String toNotNullString(String str) {
		if (str == null) {
			return "";
		}

		return str;
	}

	/**
	 * 生成标题
	 * 
	 * @param subject
	 *            标题
	 * @param maxLength
	 *            标题截取的长度，-1表示不截断
	 * @param escapteHTML
	 *            是否将标题转码成HTML
	 * @param importantLevel
	 *            重要程度，没有用<code>null</code>
	 * @param hasAttachments
	 *            没有用<code>null</code>
	 * @param bodyType
	 *            没有用<code>null</code>
	 * @param extIcons
	 *            没有用<code>null</code>
	 * @return
	 */
	public static String mergeSubject(String subject, int maxLength,
			boolean escapteHTML, Integer importantLevel,
			Boolean hasAttachments, String bodyType, List<String> extIcons) {
		boolean hasBodyType = false;
		boolean hasImportantLevel = false;
		if (maxLength > 0) {
			if (Boolean.TRUE == hasAttachments) {
				maxLength -= 2;
			}
			if (bodyType != null && "HTML".endsWith(bodyType)) {
				maxLength -= 2;
				hasBodyType = true;
			}
			if (importantLevel != null && importantLevel > 1) {
				maxLength -= 2;
				hasImportantLevel = true;
			}

			if (extIcons != null) {
				maxLength -= extIcons.size() * 2;
			}

			subject = Strings.getLimitLengthString(subject, maxLength, "...");
			if (escapteHTML) {
				subject = Strings.toHTML(subject);
			}
		}

		String str = "";
		if (hasBodyType) {
			str += "<span class='importance_" + importantLevel + "'></span>";
		}

		str += subject;

		if (Boolean.TRUE == hasAttachments) {
			str = "<span class='div-float'>" + str + "</span>";
			str += "<span class='attachment_table_" + hasAttachments + " div-float'></span><span class='bodyType_ div-float'/>";
		}

		if (hasImportantLevel) {
			str += "<span class='bodyType_" + bodyType + "'></span>";
		}

		if (extIcons != null) {
			for (String extIcon : extIcons) {
				str += "<img src=\"" + extIcon + "\" border='0' align='absmiddle'>";
			}
		}

		return str;
	}
	
	/**
	 * 获取栏目显示名称
	 * 
	 * @param defaultName 栏目默认名称
	 * @param preference 栏目配置参数
	 * @return
	 * 
	 */
	public static String getSectionName(String defaultName, Map<String, String> preference) {
		String columnsName = preference.get("columnsName");
		if (Strings.isNotBlank(columnsName)) {
			return columnsName;
		}
		return defaultName;
	}

	/**
	 * 获取栏目显示条数
	 * 
	 * @param defaultCount 栏目默认条数
	 * @param preference 栏目配置参数
	 * @return
	 * 
	 */
	public static int getSectionCount(int defaultCount, Map<String, String> preference) {
		String count = preference.get("count");
		if (Strings.isNotBlank(count)) {
			try {
				return NumberUtils.toInt(count);
			} catch (Exception e) {
			}
		}
		
		return defaultCount;
	}

	/**
	 * 获取栏目显示样式
	 * 
	 * @param defaultStyle 默认栏目样式
	 * @param preference 栏目配置参数
	 * @return
	 * 
	 */
	public static String getColumnStyle(String defaultStyle, Map<String, String> preference) {
		String columnsStyle = preference.get("columnsStyle");
		if (Strings.isNotBlank(columnsStyle)) {
			return columnsStyle;
		}
		return defaultStyle;
	}

	/**
	 * 获取栏目显示字段
	 * 
	 * @param defaultRowString 默认显示字段
	 * @param preference 栏目配置参数
	 * @return
	 * 
	 */
	public static String[] getRowList(String defaultRowString, Map<String, String> preference) {
		String rowStr = preference.get("rowList");
		if (Strings.isBlank(rowStr)) {
			rowStr = defaultRowString;
		}
		return rowStr.split(",");
	}

	/**
	 * 获取栏目显示内容
	 * 
	 * @param defaultPanel 默认显示内容
	 * @param preference 栏目配置参数
	 * @return
	 * 
	 */
	public static String getPanel(String defaultPanel, Map<String, String> preference) {
		String rowStr = preference.get("panel");
		if (Strings.isNotBlank(rowStr)) {
			return rowStr;
		}
		return defaultPanel;
	}
	
	/**
	 * 获取栏目包含内容
	 * 
	 * @param defaultResultValue 默认显示内容
	 * @param preference 栏目配置参数
	 * @return
	 * 
	 */
	public static String[] getResultValue(String defaultResultValue, Map<String, String> preference) {
		String resultValue = preference.get("resultValue");
		if (Strings.isBlank(resultValue)) {
			resultValue = defaultResultValue;
		}
		return resultValue.split(",");
	}

}