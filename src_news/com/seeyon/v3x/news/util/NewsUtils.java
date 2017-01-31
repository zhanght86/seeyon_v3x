package com.seeyon.v3x.news.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.ObjectUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 新闻模块的常用工具类
 * @author wolf
 *
 */
public class NewsUtils {
	private OrgManager orgManager;
	private static final Log log = LogFactory.getLog(NewsUtils.class);

	/**
	 * 根据用户ID获取V3xOrgMember
	 * @param userId
	 * @return
	 */
	public V3xOrgMember getMemberById(Long userId){
	   V3xOrgMember member = null;
	   if(userId==0) return new V3xOrgMember();
       try {
           member = orgManager.getMemberById(userId);
       } catch (BusinessException e) {
//           e.printStackTrace();
    	   log.error("获取实体出错", e);
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
	 * 根据部门Id获取部门名称
	 * @param departmentId
	 * @return
	 */
	public String getDepartmentNameById(Long departmentId, boolean needAccountShort){
		try {
			V3xOrgDepartment dept=orgManager.getDepartmentById(departmentId);
			if(dept==null) return String.valueOf(departmentId);
			String name = dept.getName();
			
			if(needAccountShort){
				V3xOrgAccount account = orgManager.getAccountById(dept.getOrgAccountId());
				if(account != null){
					name += "(" + account.getShortname() + ")";
				}
			}					
			
			return name;
		} catch (BusinessException e) {
			log.error("获取部门名称出错", e);
		}
		return String.valueOf(departmentId);
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
	@SuppressWarnings("unchecked")
	public static Object getPropertyObject(Class mainCls,String property,String value){
		return ObjectUtil.getPropertyObject(mainCls, property, value);
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
			log.error(e.getMessage(),e);
			throw e;
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(),e);
			throw e;
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
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
	 * 得到组织模型实体的名称
	 */
	public static String getOrgEntityName(String orgType, long orgId, boolean needAccountShort){
		String name = "";
		try{
			OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
			V3xOrgEntity entity = null;
			if(V3xOrgEntity.ORGENT_TYPE_MEMBER.equals(orgType)){
				entity = orgManager.getMemberById(orgId);
			}else if(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT.equals(orgType)){
				entity = orgManager.getDepartmentById(orgId);
			}else if(V3xOrgEntity.ORGENT_TYPE_ACCOUNT.equals(orgType)){
				entity = orgManager.getAccountById(orgId);
			}else if(V3xOrgEntity.ORGENT_TYPE_POST.equals(orgType)){
				entity = orgManager.getPostById(orgId);
			}else if(V3xOrgEntity.ORGENT_TYPE_ROLE.equals(orgType)){
				entity = orgManager.getRoleById(orgId);
			}else if(V3xOrgEntity.ORGENT_TYPE_TEAM.equals(orgType)){
				entity = orgManager.getTeamById(orgId);
			}else if(V3xOrgEntity.ORGENT_TYPE_LEVEL.equals(orgType)){
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
		}catch(Exception e){
			
		}
		
		return name;
	}
	
	public <T> List<T> paginate(List<T> list) {
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
	
	/**
	 * 得到可以看到新闻的用户集合
	 */
	public List<V3xOrgMember> getScopeMembers(int intSpaceType, long accountId , boolean outterP){
		Long scopeAccountId = accountId;
		List<V3xOrgMember> result = new ArrayList<V3xOrgMember>();
		List<V3xOrgMember> result1 = null;
		List<V3xOrgMember> result2 = null;
		try {
			if(intSpaceType == Constants.NewsTypeSpaceType.group.ordinal()){
				scopeAccountId = com.seeyon.v3x.organization.domain.V3xOrgEntity.VIRTUAL_ACCOUNT_ID;
			    result1 = orgManager.getAllMembers(scopeAccountId);
			    return result1;
			}else{
				 result1 = orgManager.getAllMembers(scopeAccountId);
				 result.addAll(result1);
				 if(outterP){//如果板块是不让外部人看到。则不发消息
					 result2 = orgManager.getAllExtMembers(scopeAccountId);
					 result.addAll(result2);
				 }
				 Map<Long, List<V3xOrgMember>> conCurrent = orgManager.getConcurentPostByAccount(scopeAccountId);
				 for(List<V3xOrgMember>  m : conCurrent.values()){
					 result.addAll(m);
				 }
				 return result;
			}    
			
		} catch (BusinessException e) {
			return new ArrayList<V3xOrgMember>();
		}
	}
	
	public static Set<Long> parsePublishScopeUser(String elements) throws BusinessException{
		Set<Long> members = new HashSet<Long>();
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		List<V3xOrgEntity> entities = orgManager.getEntities(elements);
		List<V3xOrgMember> allMembers = new ArrayList<V3xOrgMember>();
		for(V3xOrgEntity entity : entities){
			if(entity instanceof V3xOrgAccount){
				allMembers.addAll(orgManager.getAllMembers(entity.getId()));
				if(allMembers != null){
					//兼职人员
					Collection<List<V3xOrgMember>> conPost = orgManager.getConcurentPostByAccount(entity.getId()).values();
					for(List<V3xOrgMember> ml : conPost){
						allMembers.addAll(ml);
					}
				}
			}else if(entity instanceof V3xOrgDepartment){
				allMembers.addAll(orgManager.getMembersByDepartment(entity.getId(),false,false));
			}else if(entity instanceof V3xOrgMember){
				members.add(entity.getId());
			}else if(entity instanceof V3xOrgLevel){
				allMembers.addAll(orgManager.getMembersByLevel(entity.getId(), false));
			}else if(entity instanceof V3xOrgPost){
				allMembers.addAll(orgManager.getMembersByPost(entity.getId(),false));
			}else if(entity instanceof V3xOrgTeam){
				allMembers.addAll(orgManager.getTeamMember(entity.getId()));
			}
		}
		for(V3xOrgMember m : allMembers){
			members.add(m.getId());
		}
		return members;
	}
	
	public static List<NewsData> objArr2News(List<Object[]> objlist) {
		List<NewsData> list = new ArrayList<NewsData>();
		if(objlist == null || objlist.size() == 0)
			return list;
		for(Object[] arr : objlist) {
			NewsData data = new NewsData();
			int n = 0;
			data.setId((Long)arr[n++]);
			data.setTitle((String)arr[n++]);
			data.setBrief((String)arr[n++]);
			data.setKeywords((String)arr[n++]);
			data.setPublishScope((String)arr[n++]);
			data.setPublishDepartmentId((Long)arr[n++]);
			data.setDataFormat((String)arr[n++]);
			data.setCreateDate((Timestamp)arr[n++]);
			data.setCreateUser((Long)arr[n++]);
			data.setPublishDate((Timestamp)arr[n++]);
			data.setPublishUserId((Long)arr[n++]);
			data.setReadCount((Integer)arr[n++]);
			data.setTopOrder((Byte)arr[n++]);
			data.setAccountId((Long)arr[n++]);
			data.setTypeId((Long)arr[n++]);
			data.setState((Integer)arr[n++]);
			data.setAttachmentsFlag((Boolean)arr[n++]);
			data.setAuditUserId((Long)arr[n++]);
			data.setImageNews((Boolean)arr[n++]);
			data.setFocusNews((Boolean)arr[n++]);
			data.setImageId((Long)arr[n++]);
			data.setReadFlag(arr[n++]!=null);
			list.add(data);
		}			
		return list;
	}
	
}
