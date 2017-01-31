package com.seeyon.v3x.blog.domain;

/**
 * 
 * @author dly
 * 
 */
public interface BlogConstants {
	
	public static final String Blog_FAMILY_TYPE1 = "family"; // 分类类型为博客分类
	public static final String Blog_FAMILY_TYPE2 = "favorites"; // 分类类型为收藏分类类型
	
	public static final String Blog_FAMILY_DEFAULT = "default"; // 默认博客分类
	public static final String Blog_PRIVATE_DEFAULT = "private"; // 默认私有博客分类，不允许共享查看
//	public static final String Blog_FAVORITES_DEFAULT = "default"; // 默认收藏分类, 配合 seq = -1    11.28 cancel
	// 系统统一使用的默认收藏分类
	public static final Long BLOG_DEFAULT_FAVORITE_ID = -1L;
	public static final long Blog_SPACE_SIZE_DEFAULT = 10; // 默认博客空间10M
	
	public static final String Blog_AUTH_TYPE_ADMIN = "0"; // 授权类型为管理员
	
	public static final String Blog_MODULE_DELI1 = "|"; // 组织类型与值之间分隔符

	public static final String Blog_MODULE_DELI2 = "、"; // 组织中文名称之间分隔符
	public static final String Blog_MODULE_DELI3 = ","; // 组织类型之间分隔符
	
	public static final int BLOG_HOME_ATTENTION_SIZE = 4;

	}
