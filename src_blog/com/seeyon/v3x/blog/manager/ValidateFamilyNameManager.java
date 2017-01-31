/**
 * 
 */
package com.seeyon.v3x.blog.manager;

import java.util.List;

import com.seeyon.v3x.blog.dao.BlogDao;

/**
 * 类描述：
 * 创建日期：
 *
 * @author liaoj
 * @version 1.0 
 * @since JDK 5.0
 */
public class ValidateFamilyNameManager {
	private BlogDao blogDao;

	public void setBlogDao(BlogDao blogDao) {
		this.blogDao = blogDao;
	}
	
	/**
	 * 验证分类标题是否存在
	 */
	public String validateFamilyName(String familyName)throws Exception{
		 List<String> familyNameList = this.blogDao.listAllFamilyName();
		 Boolean isExist = familyNameList.contains(familyName);
		 return isExist.toString();
	}
}
