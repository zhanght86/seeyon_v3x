package com.seeyon.v3x.meeting.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Datetimes;

/**
 * 新闻模块的常用工具类
 * @author wolf
 *
 */
public class MeetingUtils {
	private OrgManager orgManager;
	
	/**
	 * 根据用户ID获取OrgEnt_Member
	 * @param userId 形如：Member|124324234234324
	 * @return
	 */
	public V3xOrgEntity getOrgEntityById(String id) {
		V3xOrgEntity entity = null;
		try {
			entity = orgManager.getEntity(id);
			return entity;
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return entity;
	}
	
	/**
	 * 根据用户ID获取OrgEnt_Member
	 * 
	 * @param userId
	 * @return
	 */
	public V3xOrgMember getMemberById(Long userId){
		V3xOrgMember member = null;
       try {
           member = (V3xOrgMember) orgManager.getMemberById(userId);
       } catch (BusinessException e) {
           e.printStackTrace();
       }
       if(member==null) member=new V3xOrgMember();
       return member;
	}
	
	/**
	 * 根据用户ID获取用户名称
	 * @param userId
	 * @return
	 */
	public String getMemberNameByUserId(Long userId){
		return getMemberById(userId).getName();
	}
	
	/**
	 * 根据用户ID获取用户单位名称
	 * @param userId
	 * @return
	 */
	public String getMemberAccountNameByUserId(Long userId){
		
		V3xOrgAccount account = null;
		V3xOrgMember member = getMemberById(userId);
		Long accountId = member.getOrgAccountId();
		try {
	    	   account = (V3xOrgAccount) orgManager.getAccountById(accountId);
	    } catch (BusinessException e) {
	           e.printStackTrace();
	    }
	    if(account==null) account=new V3xOrgAccount();
		return account.getShortname();
	}
	
	/**
	 * 根据部门Id获取部门名称
	 * @param departmentId
	 * @return
	 */
	public String getDepartmentNameById(Long departmentId){
		try {
			V3xOrgDepartment dept=orgManager.getDepartmentById(departmentId);
			if(dept==null) return String.valueOf(departmentId);
			return dept.getName();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return String.valueOf(departmentId);
	}
	
	/**
	 * 根据用户ID获取单位名称
	 * @param userId
	 * @return
	 */
	public String getAccountNameByAccountId(Long accountId){
		return getAccountById(accountId).getName();
	}
	
	/**
	 * 根据创建会议人ID获取单位名称
	 * @param userId
	 * @return
	 */
	public String getAccountNameByCreateUserId(Long createUserId){
		V3xOrgMember member = null;
		Long accountId = null;
		V3xOrgAccount account = null;
		try{
			member = orgManager.getMemberById(createUserId);
			accountId = member.getOrgAccountId();
			account = orgManager.getAccountById(accountId);
			if(account == null) return "";
		}catch(Exception e){
			e.printStackTrace();
		}
		return account.getShortname();
	}
	
	/**
	 * 根据单位Id获取单位
	 * @param departmentId
	 * @return
	 */
	public V3xOrgAccount getAccountById(Long accountId){
		V3xOrgAccount account = null;
       try {
    	   account = (V3xOrgAccount) orgManager.getAccountById(accountId);
       } catch (BusinessException e) {
           e.printStackTrace();
       }
       if(account==null) account=new V3xOrgAccount();
       return account;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	
	
	
	/**
	 * 根据实体Bean的property属性的类型，把value转换为正确的类型。主要在条件查询中使用
	 * @param mainCls 实体Bean的类
	 * @param property Bean的属性
	 * @param value Bean的Property属性的值
	 * @return 根据Bean的Property类型，把value转换为正确的类型
	 */
	public static Object getPropertyObject(Class mainCls,String property,String value){
		try {
			if(StringUtils.isBlank(value)) return null;
			Class cls=null;
			try{
				PropertyDescriptor p=PropertyUtils.getPropertyDescriptor(mainCls.newInstance(), property);
				cls=p.getPropertyType();
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			
			String clsName=cls.getSimpleName();
			Object newValue=value;
			if(clsName.equals("String")){
				newValue=value;
			}else if(clsName.equals("Integer")){
				newValue=Integer.valueOf(value);
			}else if(clsName.equals("Long")){
				newValue=Long.valueOf(value);
			}else if(clsName.equalsIgnoreCase("Boolean")){
				if(value.equals("1") || value.equals("true") || value.equals("t"))
					newValue=Boolean.TRUE;
				else
					newValue=Boolean.FALSE;				
			}
			return newValue;
		}catch (SecurityException e) {
			e.printStackTrace();
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
			e.printStackTrace();
			throw e;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw e;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * 判断用户是否是系统管理员。在显示阅读情况部分需要使用，只有系统管理员和新闻管理员可以查看阅读情况统计
	 * @param userId
	 * @return
	 */
	public static boolean isAdmin(Long userId){	
		//当前假定毛凯为系统管理员
		return userId.longValue()==-1197936421050498492l;
	}
	
	
	/**
	 * 根据界面的选人对话框返回的字符串，获取该字符串包含的所有用户id的字符串
	 * @param typeIds
	 * @return
	 */
	// TODO 需要处理部门、岗位等
	public static String getUserIdStrFromTypeIds(String typeIds){
		if(StringUtils.isBlank(typeIds)) return "";
		String[] typeIdA=typeIds.split(",");
		String ids="";
		for(String typeId:typeIdA){
			if(typeId.indexOf("|")>-1){
				ids=ids+","+typeId.substring(typeId.indexOf("|")+1);
			}else{
				ids=ids+","+typeId;
			}
		}
		return ids.length()>0?ids.substring(1):ids;
	}
	
	/**
	 * 根据界面的选人对话框返回的字符串，获取该字符串包含的所有用户id的数组
	 * @param typeIds
	 * @return
	 */
	// TODO 需要处理部门、岗位等
	public static Long[] getUserIdsFromTypeIds(String typeIds){
		if(StringUtils.isBlank(typeIds)) return new Long[0];
		String[] typeIdA=typeIds.split(",");
		Long[] ids=new Long[typeIdA.length];		
		for(int i=0;i<typeIdA.length;i++){
			String typeId=typeIdA[i];
			if(typeId.indexOf("|")>-1){
				ids[i]=Long.valueOf(typeId.substring(typeId.indexOf("|")+1));
			}else{
				ids[i]=Long.valueOf(typeId);
			}
		}
		return ids;
	}
	
	/**
	 * 获取与会资源
	 * @return
	 */
	public Map<Long,String> getMeetingResources(){
		Map<Long,String> resourcesMap=new HashMap<Long,String>();
		// TODO 目前使用测试数据；需要进行改进，使用平台提供的接口获取数据
		resourcesMap.put(1l, "投影仪");
		resourcesMap.put(2l, "笔记本");
		resourcesMap.put(3l, "会议室1");
		resourcesMap.put(4l, "会议室2");
		resourcesMap.put(5l, "会议室3");
		return resourcesMap;
	}
	/**
	 * 获取与会资源的显示名称
	 * @return
	 */
	public String getMeetingResourceName(Long id){
		// TODO 目前使用测试数据；需要进行改进，使用平台提供的接口获取数据
		return this.getMeetingResources().get(id);
	}
	
	
	
	/**
	 * 获取项目
	 * @return
	 */
	public Map<Long,String> getMeetingProjects(){
		Map<Long,String> resourcesMap=new HashMap<Long,String>();
		// TODO 目前使用测试数据；需要进行改进，使用平台提供的接口获取数据
		resourcesMap.put(1l, "项目1");
		resourcesMap.put(2l, "项目2");
		resourcesMap.put(3l, "项目3");
		resourcesMap.put(4l, "项目4");
		resourcesMap.put(5l, "项目5");
		return resourcesMap;
	}
	/**
	 * 获取项目的显示名称
	 * @return
	 */
	public String getMeetingProjectName(Long id){
		// TODO 目前使用测试数据；需要进行改进，使用平台提供的接口获取数据
		return this.getMeetingResources().get(id);
	}
    
    /**
     * 生成会议时间字符串,用于附加在生成的消息中<br>
     * 2008-06-26 12:30 至 2008-06-26 16:30 转换为 (6-26)12:30 - 16:30<br>
     * 2008-06-26 12:30 至 2008-06-27 16:30 转换为 (6-26)12:30 - (6-27)16:30
     * @return
     */
    public static String getMeetingTimeStr(Date startDate){
        return Datetimes.format(startDate, Datetimes.datetimeStartWithMonthStyle);
        /*
        String meetingTime = null;
        String startDateTimeStr = Datetimes.format(startDate, Datetimes.datetimeStartWithMonthStyle);
        String endDateTimeStr = Datetimes.format(endDate, Datetimes.datetimeStartWithMonthStyle);
        String startDateStr = startDateTimeStr.substring(0, startDateTimeStr.indexOf(' '));
        String endDateStr = endDateTimeStr.substring(0, endDateTimeStr.indexOf(' '));
        if(startDateStr.equals(endDateStr)){
            meetingTime = "(" + startDateStr + ")" + startDateTimeStr.substring(6) + " - " + endDateTimeStr.substring(6);
        }
        else{
            meetingTime = "(" + startDateStr + ")" + startDateTimeStr.substring(6) + " - " + "(" + endDateStr + ")" + endDateTimeStr.substring(6);            
        }
        return meetingTime;
        */
    }
}
