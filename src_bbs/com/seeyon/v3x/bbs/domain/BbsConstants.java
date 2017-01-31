package com.seeyon.v3x.bbs.domain;

/**
 * 
 * @author dly
 * 
 */
public interface BbsConstants {
	public static final int BBS_ARTICLE_ISNOT_TOP = 0; // 非置顶贴

	public static final int BBS_ARTICLE_IS_TOP = 1; // 置顶贴
	/** 记录有效 */
	public static final int BBS_ARTICLE_IS_ACTIVE = 0; 
	/** 记录无效，已被逻辑删除 */
	public static final int BBS_ARTICLE_ISNOT_ACTIVE = 1;

	public static final int BBS_BOARD_ADMIN_MAX = 5; // 版块管理员数量限制

	public static final String BBS_BOARD_ADMIN_IS_ALL = "AllPeople"; // 组织类型为全体员工值
	
	public static final String BBS_BOARD_ADMIN_IS_ALL_VALUE = "全体员工"; // 组织类型为全体员工值
	
	public static final String BBS_MODULE_TYPE_IS_ALL="AllPeople"; //组织类型为全体员工
	
	/** 版块发贴量统计-按部门统计 */
	public static final int BBS_COUNT_ARTICLE_TYPE_DEPARTMENT = 0; 
	/** 版块发贴量统计-某部门内发布者 */
	public static final int BBS_COUNT_ARTICLE_TYPE_DEPARTMENT_PERSON = 1; 
	/** 版块发贴量统计-按发布者(真名发帖) */
	public static final int BBS_COUNT_ARTICLE_TYPE_PERSON = 2; 
	/** 版块发贴量统计-按发布者(匿名发帖) */
	public static final int BBS_COUNT_ARTICLE_TYPE_PERSON_ANONYMOUS = 3; 
	/**
	 * 授权类型
	 */
	public static enum BBS_AUTH_TYPE {
		ADMIN, // 管理员
		GENERAL,// 发帖用户
		NOTREPLY,// 禁止回贴
	}
	
	/**
	 * 回复类型，依次为：快速回复(0)、普通回复(1)、引用主题回复(2)、引用他人回复回复(3)
	 */
	public static enum REPLY_TYPE {
		/** 快速回复 */
		fast, 
		/** 普通回复 */
		common,	
		/** 引用主题回复 */
		referArticle,	
		/** 引用他人回复回复 */
		referReply,
	}

	public static final String BBS_MODOULE_MEMBER = "Member|"; // 
	
	/**
	 * 板块类型
	 */
	public static enum BBS_BOARD_AFFILITER {
		GROUP,   //集团讨论
		CORPORATION,	 //单位讨论
		DEPARTMENT,	//部门讨论
		PROJECT,	//项目讨论
		CUSTOM, //自定义团队空间讨论
		PUBLIC_CUSTOM, //自定义单位空间讨论
		PUBLIC_CUSTOM_GROUP //自定义集团空间讨论
	}
	
	public static final int BBS_BOARD_ANONYONMOUS_YES =0;   //匿名是
	
	public static final int BBS_BOARD_ANONYONMOUS_NO =1;   //匿名否
	
	public static final int BBS_BOARD_ANONYONMOUS_REPLY_YES = 0;  //匿名回复"是" added by Meng Yang 2009-05-11
	
	public static final int BBS_BOARD_ANONYONMOUS_REPLY_NO = 1;   //匿名回复"否"
	
	public static final int BBS_BOARD_PUTTER_THREE =3;   //默认置顶数3
	
	public static final String BBS_I18N_RESOURCE = "com.seeyon.v3x.bbs.resources.i18n.BBSResources";
	
	/**
	 * 授权发帖，其值为"1"
	 */
	public static final String AUTH_TO_POST = "1";
	/**
	 * 禁止回帖，其值为"2"
	 */
	public static final String FORBIDDEN_TO_REPLY = "2";
}
