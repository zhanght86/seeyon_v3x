/**
 * 
 */
package com.seeyon.v3x.collaboration.templete;

import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-6-28
 */
public class TempleteUtil {
	/**
	 * 检测分类是否是一级分类
	 * 
	 * @param category
	 * @return
	 */
	public static boolean isClass1Category(TempleteCategory category) {
		Long pId = category.getParentId();
		return (pId == null || pId == -1 || pId == 0 || pId == 1 || pId == 2 || pId == 3 || pId == 4);
	}
}
