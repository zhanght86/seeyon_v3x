package com.seeyon.v3x.space;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.Strings;

public class Constants {
	
	public static String PATH_SEPARATOR = "/";
	
	public static String DOCUMENT_TYPE = ".psml";
	
	public static String DEFAULT_SPACE_SUBFIX = "_D";

	//默认 是否允许自定义
    public static boolean DEFAULT_Allowdefined = true;
	
	/**
	 * 空间类型，顺序不可改变
	 */
	public static enum SpaceType {
		personal, // 个人空间  PSML命名：人id.psml
		
		//修改为协作空间
		department, // 部门空间  PSML命名：部门id.psml
		
		corporation, // 单位空间  PSML命名：单位id.psml
		group, // 集团空间  PSML命名：1.psml
		custom, // 自定义空间  PSML命名：SpaceFix的id.psml
		
		//默认的空间
		Default_personal,
		//@Deprecated
		Default_department,
		
		@Deprecated
		Default_custom,  //废弃
		
		thirdparty, //第三方页面
		
		default_leader,//默认的领导空间
		leader,//领导空间
		
		related_system,//关联系统
		related_project,//关联项目
		
		//320-增加 空间授权
		Default_personal_custom,//单位定义的个人空间
		Default_out_personal,//单位定义 默认外部人员空间
		
		personal_custom,//个人空间
		outer,//个人空间 外部人员空间
		
		public_custom,//公共空间--自定义公共空间--单位
		public_custom_group,//公共空间-自定义公共空间-集团；
		//end 
	}
	
	/**
	 * 栏目类型，顺序不可改变
	 */
	public static enum SectionType {
		common, //常用栏目
		timeManagement, //时间管理
		publicInformation, //公共信息
		doc, //文档栏目
		formbizconfigs, //表单栏目
		forum, //扩展栏目
	}
	
	/**
	 * 所有栏目类型 
	 */
	public static List<SectionType> getAllSpaceSectionTypes() {
		List<SectionType> sectionTypes = new ArrayList<SectionType>();
		sectionTypes.add(SectionType.common);
		sectionTypes.add(SectionType.timeManagement);
		sectionTypes.add(SectionType.publicInformation);
		sectionTypes.add(SectionType.doc);
		sectionTypes.add(SectionType.formbizconfigs);
		sectionTypes.add(SectionType.forum);
		return sectionTypes;
	}
	
	/**
	 * 空间－栏目类型
	 */
	public static List<SectionType> getSpaceSectionTypes(SpaceType spaceType) {
		User user = CurrentUser.get();
		List<SectionType> sectionTypes = new ArrayList<SectionType>();
		
		sectionTypes.add(SectionType.common);
		
		if (spaceType == SpaceType.personal || spaceType == SpaceType.personal_custom
				|| spaceType == SpaceType.leader || spaceType == SpaceType.outer) {
			sectionTypes.add(SectionType.timeManagement);
		}
		
		sectionTypes.add(SectionType.publicInformation);
		sectionTypes.add(SectionType.doc);
		
		if (!user.isAdmin() && (spaceType == SpaceType.personal || spaceType == SpaceType.personal_custom
				|| spaceType == SpaceType.leader || spaceType == SpaceType.outer
				|| spaceType == SpaceType.department || spaceType == SpaceType.custom)) {
			sectionTypes.add(SectionType.formbizconfigs);
		}
		
		sectionTypes.add(SectionType.forum);
		
		return sectionTypes;
	}
	
	public static enum SpaceTypeClass{
		personal,//个人空间
		corporation,// 协作空间
		public_,//公共空间
	}
	
	public static boolean isSystem(SpaceType type){
		if(type == null) return false;
		return type != SpaceType.custom && type != SpaceType.Default_personal_custom && type != SpaceType.public_custom && type != SpaceType.public_custom_group;
	}
	
	/**
	 * 如果是默认空间类型，就转化成对应类型
	 * @param s
	 * @return
	 */
	public static SpaceType parseDefaultSpaceType(SpaceType defaultSpaceType){
		switch (defaultSpaceType) {
			case Default_personal: return SpaceType.personal;
			case Default_department: return SpaceType.department;
			case Default_custom:return SpaceType.custom;
			case default_leader: return SpaceType.leader;
			case Default_personal_custom:return SpaceType.personal_custom;
			case Default_out_personal: return SpaceType.outer;
		}
		return defaultSpaceType;
	}

	public static SpaceType getSpaceTypeByClass(String space){
		if(Strings.isBlank(space)){
			return null;
		}
		SpaceTypeClass spaceType = SpaceTypeClass.valueOf(space);
		switch(spaceType){
		case personal:return SpaceType.Default_personal_custom;
		case corporation:return SpaceType.custom;
		case public_:return SpaceType.public_custom;
		}
		return null;
	}
	 public static String getDefaultPagePath(SpaceType type){
		 String _pagePath = DEFAULT_PERSONAL_PAGE_PATH;
		 switch(type){
        //查看 默认个人空间
        case Default_personal:
        	break;
    	//查看 默认领导空间
        case default_leader:
        	_pagePath = DEFAULT_LEADER_PAGE_PATH;
        	break;
    	//查看 默认外部人员空间
        case Default_out_personal:
        	_pagePath = DEFAULT_OUT_PERSONAL_PAGE_PATH;
        	break;
    	//查看 自定义个人空间
        case Default_personal_custom:
        	_pagePath = DEFAULT_CUSTUM_PERSONAL;
        	break;
        case Default_department: 
        	_pagePath = DEFAULT_DEPARTMENT_PAGE_PATH;
        	break;
    	//查看 协作空间
        case custom:
        	_pagePath = DEFAULT_CUSTOM_PAGE_PATH;
        	break;
    	//查看 单位空间
        case corporation:
        	_pagePath = DEFAULT_CORPORATION_PAGE_PATH;
        	break;
    	//查看 集团空间
        case group:
        	_pagePath = DEFAULT_GROUP_PAGE_PATH;
        	break;
    	//查看 公共自定义空间
        case public_custom:
        	_pagePath = DEFAULT_PUBLIC_PAGE_PATH;
        	break;
        case public_custom_group:
        	_pagePath = DEFAULT_PUBLIC_PAGE_PATH;
        }
		 return _pagePath;
	 }
	/**
	 * 空间状态
	 */
	public static enum SpaceState {
		normal, // 正常的
		invalidation, // 停用
	}
	
	public static final String SEEYON_FOLDER = PATH_SEPARATOR + "seeyon";
	
	/**
	 * 个人空间文件夹 /seeyon/personal
	 */
	public static final String PERSONAL_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "personal" + PATH_SEPARATOR;

	/**
	 * 部门空间文件夹 /seeyon/department/
	 */
	public static final String DEPARTMENT_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "department" + PATH_SEPARATOR;
	
	/**
	 * 单位空间文件夹 /seeyon/corporation/
	 */
	public static final String CORPORATION_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "corporation" + PATH_SEPARATOR;
	
    /**
     * 集团空间文件夹 /seeyon/group/
     */
    public static final String GROUP_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "group" + PATH_SEPARATOR;
    
    /**
     * 公共自定义空间 /seeyon/public_custom/
     */
    public static final String PUBLIC_FOLDER =  SEEYON_FOLDER + PATH_SEPARATOR + "public_custom" + PATH_SEPARATOR;
	/**
	 * 自定义空间文件夹 /seeyon/custom/
	 */
	public static final String CUSTOM_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "custom" + PATH_SEPARATOR;
	
	/**
	 * 领导空间文件夹 /seeyon/leader/
	 */
	public static final String LEADER_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "leader"+PATH_SEPARATOR;
	
	/**
	 * 外部人员空间文件夹/seeyon/outer/
	 */
	public static final String OUTER_FOLDER =  SEEYON_FOLDER + PATH_SEPARATOR + "outer"+PATH_SEPARATOR;
	
	/**
	 * 个人自定义空间/seeyon/personal_custom/
	 */
	public static final String PERSONAL_CUSTOM_FOLDER = SEEYON_FOLDER + PATH_SEPARATOR + "personal_custom"+PATH_SEPARATOR;
	/**
	 * 默认的个人空间page path /seeyon/personal/default-page.psml
	 */
	public static final String DEFAULT_PERSONAL_PAGE_PATH = PERSONAL_FOLDER + "default-page.psml";

	/**
	 * 默认的部门空间page path /seeyon/department/default-page.psml
	 */
	public static final String DEFAULT_DEPARTMENT_PAGE_PATH = DEPARTMENT_FOLDER + "default-page.psml";

	/**
	 * 默认的单位空间page path /seeyon/corporation/default-page.psml
	 */
	public static final String DEFAULT_CORPORATION_PAGE_PATH = CORPORATION_FOLDER + "default-page.psml";
	
    /**
     * 默认的集团空间page path /seeyon/group/default-page.psml
     */
    public static final String DEFAULT_GROUP_PAGE_PATH = GROUP_FOLDER + "default-page.psml";
    
	/**
	 * 默认的自定义空间page path /seeyon/custom/default-page.psml
	 */
	public static final String DEFAULT_CUSTOM_PAGE_PATH = CUSTOM_FOLDER + "default-page.psml";

	/**
	 * 默认的领导空间page path /seeyon/leader/default-page.psml
	 */
	public static final String DEFAULT_LEADER_PAGE_PATH = LEADER_FOLDER + "default-page.psml";
	
	/**
	 * 默认的外部人员空间 path /seeyon/outer/default-page.psml
	 */
	public static final String DEFAULT_OUT_PERSONAL_PAGE_PATH = OUTER_FOLDER + "default-page.psml";
	/**
	 * 默认的个人自定义空间path /seeyon/personal_custom/default-page.psml
	 */
	public static final String DEFAULT_CUSTUM_PERSONAL = PERSONAL_CUSTOM_FOLDER+"default-page.psml";
	
	/**
	 * 默认的部门主管空间path /seeyon/personal_custom/DeptManager.psml
	 */
	public static final String DEFAULT_DEPTMANAGER_PERSONAL = PERSONAL_CUSTOM_FOLDER + "DeptManager.psml";
	
	/**
	 * 公共自定义空间 path /seeyon/public/default-page.psml
	 */
	public static final String DEFAULT_PUBLIC_PAGE_PATH =  PUBLIC_FOLDER+"default-page.psml";
	
    public static final String KEY_SLOGAN = "space.label.slogan.default";
    public static final String DEFAULT_BANNER = "space_banner.gif";

	//取得SysMgrResources国际化资源文件的值
	private static final String resource_system = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
	public static final String resource_main = "com.seeyon.v3x.main.resources.i18n.MainResources";
	public static String getValueOfKey(String key){
		return ResourceBundleUtil.getString(resource_system, key);
	}
    
	/**
	 * 取到默认的空间名称
	 * @param spaceType
	 * @return
	 */
	public static String getDefaultSpaceName(SpaceType spaceType){
		String key = "seeyon.top." + spaceType + ".space.label";
        if(spaceType.equals(SpaceType.group)){
            key += (String)SysFlag.EditionSuffix.getFlag();
        }
        String value = ResourceBundleUtil.getString(resource_main, key);
        if(key.equals(value)){
        	return "";
        }
		return value;
	}
    
	public static String getSpaceName(SpaceFix spaceFix) {
		if (spaceFix == null) {
			return "";
		}

		SpaceType spaceType = EnumUtil.getEnumByOrdinal(SpaceType.class, spaceFix.getType());
		if (spaceType == SpaceType.Default_department) {
			return getDefaultSpaceName(spaceType);
		}
		if (spaceType == SpaceType.department) {
			return getDefaultSpaceName(spaceType) + "(" + Functions.getDepartment(spaceFix.getEntityId()).getName() + ")";
		}

		String name = spaceFix.getSpaceName();
		if (Strings.isBlank(name)) {
			switch (spaceType) {
			case personal:
				spaceType = SpaceType.Default_personal;
				break;
			case leader:
				spaceType = SpaceType.default_leader;
				break;
			case outer:
				spaceType = SpaceType.Default_out_personal;
				break;
			case personal_custom:
				spaceType = SpaceType.Default_personal_custom;
				break;
			}
			return getDefaultSpaceName(spaceType);
		}
		return name;
	}
	
    /**
     * 得到默认口号国际化的key
     * @return
     */
    public static final String getSloganKey(){
        String s = (String)SysFlag.EditionSuffix.getFlag();
        return "space.label.slogan.default" + s;
    }
    
    public static boolean isPersonalSpace(String spaceType){
    	if(Strings.isNotBlank(spaceType)){
    		SpaceType type = SpaceType.valueOf(spaceType);
    		if(type != null){
    			switch(type){
    			case personal :
    			case leader   :
    			case outer:
    			case personal_custom:
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public static boolean isInPersonalClass(SpaceType type){
    	if(type != null){
			switch(type){
			case Default_personal:
			case default_leader:
			case Default_out_personal:
			case Default_personal_custom:
				return true;
			}
    	}
    	return false;
    }
    
    public static <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
}