package com.seeyon.v3x.bulletin.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;


public class Constants {
	public static final String BUL_RESOURCE_BASENAME = "com.seeyon.v3x.bulletin.resources.i18n.BulletinResources";
	public static final String COMMON_RESOURCE_BASENAME = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
	
	public static String getBulI18NValue(String key, Object... parameters) {
		return ResourceBundleUtil.getString(BUL_RESOURCE_BASENAME, key, parameters);
	}
	
	/** 对版块的管理员和审核员进行操作时，获取操作文本值：设置、修改 */
	public static String getActionText(boolean isNew) {
		return ResourceBundleUtil.getString(BUL_RESOURCE_BASENAME, "bul.manageraction."+isNew);
	}
	
	/**
	 * 公告板块的类型
	 */
	public static enum BulTypeSpaceType {
		group, //集团公告
		corporation, //单位公告
		department, //部门公告
		none, //不限空间，也即全部空间
		custom, //自定义团队空间公告
		public_custom, //自定义单位空间公告
		public_custom_group //自定义集团空间公告
		
	}
	
	/**
	 * 在查看各种公告列表时，用户的访问角色，包括：<br>
	 * 普通用户(包括管理员、外部人员以普通用户身份查看)、公告发起者、公告审核员及公告管理员<br>
	 * 排列顺序不宜变动<br>
	 */
	public static enum VisitRole {
		User,
		Poster,
		Auditor,
		Admin
	}

	public static VisitRole valueOfRole(int roleId) {
		VisitRole[] roles = VisitRole.values();
		if(roles != null) {
			for(VisitRole role : roles) {
				if(role.ordinal() == roleId)
					return role;
			}
		}
		return VisitRole.User;
	}
	
	public static enum BulTypeAclType {
		manager, 
		audit,
		writer
	}
	
	/**
	 * 根据key得到枚举类型
	 * 
	 * @param key
	 * @return
	 */
	public static BulTypeSpaceType valueOfSpaceType(int key) {
		BulTypeSpaceType[] enums = BulTypeSpaceType.values();

		if (enums != null) {
			for (BulTypeSpaceType enum1 : enums) {
				if (enum1.ordinal() == key) {
					return enum1;
				}
			}
		}

		return null;
	}
	
	/**
	 * 尚未提交，也就是暂存
	 */
	public static final int DATA_STATE_NO_SUBMIT=0;
	/**
	 * 已经提交，但是没有审核
	 */
	public static final int DATA_STATE_ALREADY_CREATE=10;
	/**
	 * 已经审核，但是没有发布
	 */
	public static final int DATA_STATE_ALREADY_AUDIT=20;
	/**
	 * 已经发布，但是没有归档
	 */
	public static final int DATA_STATE_ALREADY_PUBLISH=30;
	/**
	 * 审核不通过
	 */
	public static final int DATA_STATE_NOPASS_AUDIT=40;
	
	/**
	 * 已经归档
	 */
	public static final int DATA_STATE_ALREADY_PIGEONHOLE=100;
	
	/**
	 * 公告发起员标志，在公告类型-公告管理员、公告发起员关联表中使用，保存在ext1字段中
	 */
	public static final String WRITE_FALG="write";
	/**
	 * 公告管理员标志，在公告类型-公告管理员、公告发起员关联表中使用，保存在ext1字段中
	 */
	public static final String MANAGER_FALG="manager";
	
	/**
	 * 操作成功标志
	 */
	public static final String RESULT_SUCCESS="success";
	/**
	 * 操作失败标志
	 */
	public static final String RESULT_FAILURE="failure";
	
	/**
	 * 是否处于开发测试阶段
	 */
	public static final Boolean IS_TEST=true;
	
	/**
	 * 公告的CATEGORY = 7
	 */
	public static final int BULLETIN_CATEGORY=7;
	/**
	 * 新闻的CATEGORY = 8
	 */
	public static final int NEWS_CATEGORY=8;
	
	
	/**
	 * 部门公告的默认置顶个数
	 */
	public static final byte BUL_DEPT_DEFAULT_TOP_COUNT = Byte.parseByte("3");
	
	/**
	 * 点击左侧菜单进入的各公告类型显示公告数量：6条
	 *
	 */
	public static final int BUL_HOMEPAGE_TABLE_COLUMNS = 6;
	
	/** 首页空间 - 公告栏目所显示的公告数量：8条 */
	public static final int SECTION_TABLE_COLUMNS = 8;
	
	/**
	 * 点击公告发布按钮进去后看到的列表中公告的状态.
	 * 以下状态才列出来
	 */
	public static Set<Integer> getDataStatesNoPublish(){
		Set<Integer> states = new HashSet<Integer>();
		states.add(Constants.DATA_STATE_ALREADY_AUDIT);
		states.add(Constants.DATA_STATE_ALREADY_CREATE);
		states.add(Constants.DATA_STATE_NO_SUBMIT);
		states.add(Constants.DATA_STATE_NOPASS_AUDIT);
		states.add(Constants.DATA_STATE_ALREADY_PUBLISH);
		return states;
	}
	
	/**
	 * 记录一条公告最终发布前的审核状态，分为3种情况，保存在ext3扩展字段中：
	 * 1.无审核员，公告发送后即"直接发布"   对应值  0                 
	 * 2.有审核员，其审核操作为"审核通过"   对应值  1  
	 * 3.有审核员，其审核操作为"直接发布"   对应值  2 
	 * 公告管理员进入管理界面所查看到的全部已发布公告，由ext3扩展字段来决定"审核意见"处显示内容
	 * added by Meng Yang 2009-06-11 
	 */
	public static final int AUDIT_RECORD_NO = 0;
	public static final int AUDIT_RECORD_PASS = 1;
	public static final int AUDIT_RECORD_PUBLISH = 2;
	
	private static List<String> MS_WPS_Type;
	static {
		MS_WPS_Type = new ArrayList<String>();
		MS_WPS_Type.add(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL);
		MS_WPS_Type.add(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD);
		MS_WPS_Type.add(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL);
		MS_WPS_Type.add(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD);
	}
	
	/** 获取微软、金山的办公文档格式 */
	public static List<String> getMSAndWPSTypes() {
		return MS_WPS_Type;
	}
	
	public static final String Statistic_By_Read_Count = "byRead";
	public static final String Statistic_By_Publish_User = "byWrite";
	public static final String Statistic_By_Publish_Month = "byPublishDate";
	public static final String Statistic_By_Status = "byState";
	
}
