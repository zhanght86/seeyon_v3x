package com.seeyon.v3x.bulletin.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Strings;
/**
 * 公告模块的常用工具类
 * @author wolf
 */
public class BulletinUtils {
	private OrgManager orgManager;
	private SpaceManager spaceManager;
	private static final Log log = LogFactory.getLog(BulletinUtils.class);
	
	/**
	 * 根据用户ID获取V3xOrgMember
	 * @param userId
	 */
	public V3xOrgMember getMemberById(Long userId){
	   V3xOrgMember member = null;
	   if(userId==0) return new V3xOrgMember();
       try {
    	   member = orgManager.getMemberById(userId);
       } catch (BusinessException e) {
    	   log.error("获取实体失败", e);
       }
       if(member==null) member=new V3xOrgMember();
       return member;
	}
	
	/**
	 * 根据用户ID获取用户名称
	 */
	public String getMemberNameByUserId(Long userId){
		return getMemberById(userId).getName();
	}
	
	/**
	 * 根据部门Id获取部门名称
	 * @param departmentId
	 */
	public String getDepartmentNameById(Long departmentId, boolean needAccountShort){
		String result = getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, needAccountShort, orgManager);
		if(StringUtils.isBlank(result) && departmentId != null) {
			result = String.valueOf(departmentId);
		}
		return result;
	}

	/**
	 * 根据实体Bean的property属性的类型，把value转换为正确的类型。主要在条件查询中使用
	 * @param mainCls 实体Bean的类
	 * @param property Bean的属性
	 * @param value Bean的Property属性的值
	 * @return 根据Bean的Property类型，把value转换为正确的类型
	 */
	@SuppressWarnings("rawtypes")
	public static Object getPropertyObject(Class mainCls,String property,String value){
		try {
			if(StringUtils.isBlank(value)) return null;
			Class cls=null;
			try{
				PropertyDescriptor p=PropertyUtils.getPropertyDescriptor(mainCls.newInstance(), property);
				cls=p.getPropertyType();
				
			} catch (IllegalAccessException e) {
				log.error("实体转类型出错", e);
			} catch (InvocationTargetException e) {
				log.error("实体转类型出错", e);
			} catch (NoSuchMethodException e) {
				log.error("实体转类型出错", e);
			} catch (InstantiationException e) {
				log.error("实体转类型出错", e);
			}
			
			String clsName=cls.getSimpleName();
			Object newValue=value;
			if(clsName.equals("String")){
				newValue=value;
			} else if(clsName.equals("Integer")){
				newValue=Integer.valueOf(value);
			} else if(clsName.equals("Long")){
				newValue=Long.valueOf(value);
			} else if(clsName.equalsIgnoreCase("Boolean")){
				if(value.equals("1") || value.equals("true") || value.equals("t"))
					newValue=Boolean.TRUE;
				else
					newValue=Boolean.FALSE;				
			}
			return newValue;
		}catch (SecurityException e) {
			log.error("实体转类型出错", e);
		}
		return value;
	}
	
	/**
	 * 利用反射取得一个对象的属性值
	 */
	public Object getAttributeValue(Object obj, String attribute){
		if(obj == null || Strings.isBlank(attribute))
			return null;
		String methodName = "get" + attribute.toUpperCase().charAt(0) + attribute.substring(1);
		Method method = null;
		try {
			method = obj.getClass().getMethod(methodName);
		}catch (Exception e) {
			methodName = "is" + attribute.toUpperCase().charAt(0) + attribute.substring(1);
			try {
				method = obj.getClass().getMethod(methodName);
			} catch (Exception e1) {
			}
		}
		if(method == null)
			return null;
		Object value = null;
		try {
			value = method.invoke(obj);
		} catch (Exception e) {
		}
		
		return value;
	}
	
	/**
	 * 自动设置bean的某个属性的值，会自动将value类型转换为property属性的类型
	 * @param bean
	 * @param property
	 * @param value
	 * @throws Exception 
	 */
	public static void setProperty(Object bean,String property,String value) throws Exception{
		try {
			Object newValue=getPropertyObject(bean.getClass(),property,value);
			PropertyUtils.setSimpleProperty(bean, property, newValue);
		} catch (IllegalAccessException e) {
			log.error("", e);
			throw e;
		} catch (InvocationTargetException e) {
			log.error("", e);
			throw e;
		} catch (NoSuchMethodException e) {
			log.error("", e);
			throw e;
		}
	}
	
	/**
	 * 对外提供接口，创建部门时，会将部门名称，管理员，部门ID传递过来，分别对应公告的类型名称，管理员，账号dept ID
	 * @param typeName		类型名称
	 * @param managerId		管理员ID
	 * @param accountId		账号Account ID,用于判断权限
	 */
	public BulType createBulTypeByDept(String typeName,Long deptId,Long accountId){
		BulTypeManager bulTypeManager = (BulTypeManager)ApplicationContextHolder.getBean("bulTypeManager");
		
		BulType type = bulTypeManager.getByDeptId(deptId);
		boolean isNew = type == null;
		if(isNew){
			type = new BulType();
			type.setId(deptId);
		} 
		//单位、集团、部门公告板块的单位ID属性分别设置为对应的单位、集团、部门ID，以利用查询索引
		type.setAccountId(deptId);
		type.setAuditFlag(false);
		type.setAuditUser(0l);
		type.setCreateDate(new Date());
		type.setCreateUser(CurrentUser.get().getId());
		type.setSpaceType(Constants.BulTypeSpaceType.department.ordinal());
		type.setTopCount(Constants.BUL_DEPT_DEFAULT_TOP_COUNT);
		type.setTypeName(typeName);
		type.setSortNum(0);
		type.setExt1("0");
						
		// 2007.12.3 修改管理员的获得逻辑 lihf
		List<Long> managerIds = this.getDeptManagerIds(deptId);
		type.setManagerUserIds(StringUtils.join(managerIds, ","));
		bulTypeManager.saveBulType(type, isNew);
		return type;
	}
	
	public void updateDeptBulTypeManagers(Long deptId, List<Long> admins) {
		BulTypeManager bulTypeManager = (BulTypeManager)ApplicationContextHolder.getBean("bulTypeManager");
		BulType type = bulTypeManager.getByDeptId(deptId);
		if(type == null)
			return;
		
		List<Long> spaceAdmins = this.spaceManager.getSpaceAdminIdsOfDepartment(deptId);
		type.setManagerUserIds(StringUtils.join(FormBizConfigUtils.getSumCollection(spaceAdmins, admins), ","));
		bulTypeManager.saveBulType(type, false);
	}
	
	public void updateDeptBulTypeManagers(Long deptId, String managers){
		BulTypeManager bulTypeManager = (BulTypeManager)ApplicationContextHolder.getBean("bulTypeManager");
		BulType type = bulTypeManager.getByDeptId(deptId);
		if(type == null)
			return;
		
		List<Long> list = new ArrayList<Long>();
		FormBizConfigUtils.addAllIgnoreEmpty(list, this.getDeptManagerIds(deptId));
		
		if(Strings.isNotBlank(managers)) {
			Set<V3xOrgMember> members = null;
			try {
				members = this.orgManager.getMembersByTypeAndIds(managers);
			} catch (BusinessException e) {	
				log.error("", e);
			}
			FormBizConfigUtils.addAllIgnoreEmpty(list, FormBizConfigUtils.getEntityIds(members));
		}
		type.setManagerUserIds(StringUtils.join(list, ","));
		bulTypeManager.saveBulType(type, false);
	}
	
	/**
	 * 获得一个部门的所有部门管理员id集合
	 * @param deptId	部门ID
	 */
	private List<Long> getDeptManagerIds(Long deptId) {
		List<V3xOrgMember> members = null;
		try {
			V3xOrgRole managerRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
			members = orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, deptId, managerRole.getId());
		} catch (BusinessException e) {		
			log.error("按照部门主管角色获取部门[id=" + deptId + "]主管时出现异常：", e);
		}
		
		if(CollectionUtils.isEmpty(members)) 
			log.warn("部门[id=" + deptId + "]按照部门主管角色无法获取部门主管！");
		
		return FormBizConfigUtils.getEntityIds(members);
	}
	
	/**
	 * 得到组织模型实体的名称
	 */
	public static String getOrgEntityName(String orgType, long orgId, boolean needAccountShort, OrgManager orgManager) {
		String name = null;
		try {
			if(orgManager == null)
				orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			
			V3xOrgEntity entity = null;
			if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)) {
				entity = orgManager.getMemberById(orgId);
			} else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgType)) {
				entity = orgManager.getDepartmentById(orgId);
			} else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(orgType)) {
				entity = orgManager.getAccountById(orgId);
			} else if(V3xOrgEntity.ORGENT_TYPE_POST.equals(orgType)) {
				entity = orgManager.getPostById(orgId);
			} else if(V3xOrgEntity.ORGENT_TYPE_ROLE.equals(orgType)) {
				entity = orgManager.getRoleById(orgId);
			} else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equals(orgType)) {
				entity = orgManager.getTeamById(orgId);
			} else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(orgType)) {
				entity = orgManager.getLevelById(orgId);
			}
			
			if(entity != null){
				name = entity.getName();
				if(needAccountShort){
					V3xOrgAccount account = orgManager.getAccountById(entity.getOrgAccountId());
					if(account != null){
						name += "(" + account.getShortname() + ")";
					}						
				}
			}
		} catch(Exception e) {
			log.error("获取组织模型名称时出现异常，类型[" + orgType + "]，id[" + orgId + "]", e);
		}
		
		return StringUtils.defaultString(name);
	}
	
	/**
	 * 得到组织模型实体的名称
	 */
	public static String getOrgEntityName(String orgType, long orgId, boolean needAccountShort){
		return getOrgEntityName(orgType, orgId, needAccountShort, null);
	}
	
	/**
	 * 获取宽栏目置顶公告的标题HTML代码
	 * @param bulData  置顶公告
	 */
	public static String getTopedBulTitleHtml(BulData bulData) {
		return getTopedBulTitleHtml(bulData, false);
	}
	
	/**
	 * 获取置顶公告的标题HTML代码
	 * @param bulData  置顶公告
	 * @param isNarrow 是否在窄栏目
	 */
	public static String getTopedBulTitleHtml(BulData bulData, boolean isNarrow) {
		boolean attach = BooleanUtils.isTrue(bulData.getAttachmentsFlag());
		int maxLength = (isNarrow ? 40 : 36) - (attach ? 2 : 0);
		String title = Functions.toHTML(bulData.getTitle());
		return  "<font class='div-float' color=red>[" + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME, "label.top") +  "]</font>" + 
		"<span class='div-float' title='" + title + "'> " + Strings.getLimitLengthString(title, maxLength, "...")
		+ "</span>" + "<span class='attachment_" + attach + " div-float'></span>" + 
		(isNarrow ? "" : ("<span class='bodyType_" + bulData.getDataFormat() + " div-float'></span>"));	
	}
	
	/**
	 * 初始化公告 1、初始化发起者姓名 2、初始化公告是否存在附件标志 3、初始化公告发布部门的中文名称
	 * 主要用于首页栏目中的公告列表展现
	 */
	public void initData(BulData data) {			
		if (data.getPublishDepartmentId() == null) {
			// 设置为发起者所在部门
			Long userId = data.getCreateUser();
			Long depId = getMemberById(userId).getOrgDepartmentId();
			data.setPublishDepartmentId(depId);
		}
		
		BulTypeManager bulTypeManager = (BulTypeManager)ApplicationContextHolder.getBean("bulTypeManager");
		BulType theType = bulTypeManager.getById(data.getTypeId());
		data.setType(theType);
		boolean groupType = theType.getSpaceType().intValue() == Constants.BulTypeSpaceType.group.ordinal();
		
		data.setPublishDeptName(this.getDepartmentNameById(data.getPublishDepartmentId(), groupType));
		data.setTypeName(theType.getTypeName());
		
		int state = data.getState();
		if(state == Constants.DATA_STATE_ALREADY_CREATE){
			data.setNoDelete(true);
			data.setNoEdit(true);
		} else if(state == Constants.DATA_STATE_ALREADY_AUDIT){
			data.setNoEdit(true);
		}
		
		if(data.getReadCount() == null) {
			data.setReadCount(0);
		}
	}
	
	/** 初始化公告列表  */
	public void initList(List<BulData> list) {
		if(CollectionUtils.isNotEmpty(list)) {
			for (BulData data : list) {
				initData(data);
			}
		}
	}
	
	/**
	 * 获取公告板块集合的ID集合
	 */
	public static Set<Long> getIdSet(Collection<BulType> coll) {
		Set<Long> set = new HashSet<Long>();
		if(CollectionUtils.isNotEmpty(coll)) {
			for(BulType bt : coll){
				set.add(bt.getId());
			}
		}
		return set;
	}
	
	/** 根据空间类型获取单位或集团ID */
	public static Long getAccountId(int spaceType, OrgManager orgManager) {
		Long accountId = -1l;
		User user=CurrentUser.get();
		if(user!=null){
			accountId=user.getLoginAccount();
		}
		if(spaceType == Constants.BulTypeSpaceType.group.ordinal()) {
			try {
				accountId = orgManager.getRootAccount().getId();
			} catch (BusinessException e) {
				log.error("", e);
			}
		}
		return accountId;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	
}
