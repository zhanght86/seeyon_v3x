package com.seeyon.v3x.calendar.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.project.dao.ProjectDao;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 事件管理模块的常用工具类
 * @author wolf
 *
 */
public class CalendarUtils {
	
	private OrgManager orgManager;
	
	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	};
	private UserMessageManager userMessageManager;

	public UserMessageManager getUserMessageManager() {
		return userMessageManager;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	private static final Log log = LogFactory.getLog(CalendarUtils.class);
	
	private PeopleRelateManager peopleRelateManager;	
	
	private ProjectDao projectDao;	
	
	public ProjectDao getProjectDao() {
		return projectDao;
	}

	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}
	
	/**
	 * 根据用户ID获取V3xOrgMember
	 * @param userId
	 * @return
	 */
	public V3xOrgMember getMemberById(Long userId){
	   V3xOrgMember member = null;
       try {
           member = (V3xOrgMember) orgManager.getMemberById(userId);
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
	public String getDepartmentNameById(Long departmentId){
		try {
			V3xOrgDepartment dept= orgManager.getDepartmentById(departmentId);
			if(dept==null) return String.valueOf(departmentId);
			return dept.getName();
		} catch (BusinessException e) {
//			e.printStackTrace();
			log.error("获取部门名称出错", e);
		}
		return String.valueOf(departmentId);
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
//				e.printStackTrace();
				log.error("实体Value转换类型出错", e);
			} catch (InvocationTargetException e) {
//				e.printStackTrace();
				log.error("实体Value转换类型出错", e);
			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
				log.error("实体Value转换类型出错", e);
			} catch (InstantiationException e) {
//				e.printStackTrace();
				log.error("实体Value转换类型出错", e);
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
//			e.printStackTrace();
			log.error("实体Value转换类型出错", e);
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
	 * 判断用户是否是系统管理员。在显示阅读情况部分需要使用，只有系统管理员和公告管理员可以查看阅读情况统计
	 * @param userId
	 * @return
	 */
	// TODO 完善该方法
	public static boolean isAdmin(Long userId){	
		//当前假定毛凯为系统管理员
		return userId.longValue()==-1197936421050498492l;
	}
	
	
	/**
	 * 根据界面的选人对话框返回的字符串，获取该字符串包含的所有用户id的字符串
	 * @param typeIds
	 * @return
	 */
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
	 * 获取user用户的上级	
	 * @param user
	 * @return
	 */
	public List<V3xOrgMember> getSuperior(User user){
		List<V3xOrgMember> list=new ArrayList<V3xOrgMember>();
		PeopleRelateManager peopleRelateManager1=(PeopleRelateManager)ApplicationContextHolder.getBean("PeopleRelateManager");
//		OrgManager orgManager1 = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		//完善该方法，获取user用户的上级		
			if(peopleRelateManager1!=null){
				try {
					Map<RelationType, List<V3xOrgMember>> listAll=peopleRelateManager1.getAllRelateMembers(Long.valueOf(user.getId()));
					list.addAll(listAll.get(RelationType.leader));
				} catch (Exception e) {
//					e.printStackTrace();
					log.error("获取user用户的上级出错", e);
				}
			}			
		return list;
	}
	
	/**
	 * 获取user用户的上级	
	 * @param user
	 * @return
	 */
	public List<V3xOrgMember> getSuperior(User user ,PeopleRelateManager peopleRelateManager){
		List<V3xOrgMember> list=new ArrayList<V3xOrgMember>();
		//完善该方法，获取user用户的上级		
			if(peopleRelateManager!=null){
				try {
				    //存在一个bug，当用户不承认你为上级时，则不能返回，必须等到下级的确认，才能认为是上级
					Map<RelationType, List<V3xOrgMember>> listAll=peopleRelateManager.getAllRelateMembers(Long.valueOf(user.getId()));
					list.addAll(listAll.get(RelationType.leader));
				} catch (Exception e) {
					log.error("获取user用户的上级出错", e);
				}
			}			
		return list;
	}
	
	
	/**
	 * 获取把我作为秘书的人员	
	 * @param user
	 * @return
	 */
	public List getHelper(User user ,PeopleRelateManager peopleRelateManager){
		List<Long> listAll = new ArrayList();
		//完善该方法，获取user用户的上级		
			if(peopleRelateManager!=null){
				try {  
					 listAll=peopleRelateManager.getRelateMemberIdList(Long.valueOf(user.getId()),2);
				} catch (Exception e) {
					log.error("获取user用户的秘书出错", e);
				}
			}			
		return listAll;
	}
	
	/**
	 * 获取把我作为秘书的人员
	 * @param user
	 * @return
	 */
	public List getHelper(User user){
		PeopleRelateManager peopleRelateManager1=(PeopleRelateManager)ApplicationContextHolder.getBean("peoplerelateManager");
		List<Long> list=new ArrayList();
		//完善该方法，获取user用户的下级
			if(peopleRelateManager1!=null){
				try {
					List<Long> listAll=peopleRelateManager1.getRelateMemberIdList(Long.valueOf(user.getId()),RelationType.assistant.key());
					list.addAll(listAll);
				} catch (Exception e) {
					log.error("获取user用户的下级出错", e);
				}
			}			
		return list;
	}
	
	/**
	 * 获取把当前用户做为下级的列表
	 * @param user
	 * @return
	 */
	public List getHelper_(User user){
		PeopleRelateManager peopleRelateManager1=(PeopleRelateManager)ApplicationContextHolder.getBean("peoplerelateManager");
		List<Long> list=new ArrayList();
		//完善该方法，获取user用户的下级
			if(peopleRelateManager1!=null){
				try {
					List<Long> listAll=peopleRelateManager1.getRelateMemberIdList(Long.valueOf(user.getId()),RelationType.junior.key());
					list.addAll(listAll);
				} catch (Exception e) {
					log.error("获取user用户的下级出错", e);
				}
			}			
		return list;
	}
	
	/**
	 * 获取user用户的下级
	 * @param user
	 * @return
	 */
	public List<V3xOrgMember> getJunior(User user,PeopleRelateManager peopleRelateManager){
		List<V3xOrgMember> list=new ArrayList<V3xOrgMember>();
		//完善该方法，获取user用户的下级
			if(peopleRelateManager!=null){
				try {
					Map<RelationType, List<V3xOrgMember>> listAll=peopleRelateManager.getAllRelateMembers(Long.valueOf(user.getId()));
					list.addAll(listAll.get(RelationType.junior));
				} catch (Exception e) {
					log.error("获取user用户的下级出错", e);
				}
			}			
		return list;
	}
	
	/**
	 * 获取user用户的下级
	 * @param user
	 * @return
	 */
	public List<V3xOrgMember> getJunior(User user){
//		OrgManager orgManager1 = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		PeopleRelateManager peopleRelateManager1=(PeopleRelateManager)ApplicationContextHolder.getBean("PeopleRelateManager");
		List<V3xOrgMember> list=new ArrayList<V3xOrgMember>();
		//完善该方法，获取user用户的下级
			if(peopleRelateManager1!=null){
				try {
					Map<RelationType, List<V3xOrgMember>> listAll=peopleRelateManager1.getAllRelateMembers(Long.valueOf(user.getId()));
					list.addAll(listAll.get(RelationType.junior));
				} catch (Exception e) {
//					e.printStackTrace();
					log.error("获取user用户的下级出错", e);
				}
			}			
		return list;
	}
	/**
	 * 根据选人对话框返回的字符串，返回所有人员列表
	 * @param typeAndIds
	 * @return
	 */
	public List<V3xOrgMember> getMembersByTypeAndIds(String typeAndIds){
		List<V3xOrgMember> list=new ArrayList<V3xOrgMember>();
		if(StringUtils.isEmpty(typeAndIds)) return list;
		
		String[] typeAndIdA=typeAndIds.split(",");
		try {
			for(String typeAndIdStr:typeAndIdA){
				String[] typeAndId=typeAndIdStr.split("\\|");
				String type="";
				String id="";
				//默认为用户
				if(typeAndId.length==1){
					type="Member";
					id=typeAndId[0];
				}else{
					type=typeAndId[0];
					id=typeAndId[1];
				}
				if(StringUtils.isBlank(type) || StringUtils.isBlank(id)) continue;
				if("Member".equals(type)){
					//处理用户
					list.add(orgManager.getMemberById(Long.valueOf(id)));					
				}else if("Department".equals(type)){
					//处理部门
					list.addAll(orgManager.getMembersByDepartment(Long.valueOf(id), false));
				}
			}
		} catch (BusinessException e) {
//			e.printStackTrace();
			log.error("获取人员出错", e);
		}
		return list;
	}
	/**
	 * 根据选人对话框返回的字符串，得到类型是部门还是人员
	 * @param typeAndIds
	 * @return
	 */
	public String getMembersIdandDepId(String typeAndIds){
		String str="";
		if(StringUtils.isEmpty(typeAndIds))
			return str;
		String[] typeAndIdA=typeAndIds.split(",");
			for(String typeAndIdStr:typeAndIdA){
				String[] typeAndId=typeAndIdStr.split("\\|");
				String type="";
				String id="";
				//默认为用户
				if(typeAndId.length==1){
					type="Member";
					id=typeAndId[0];
				}else{
					type=typeAndId[0];
					id=typeAndId[1];
				}
				if(StringUtils.isBlank(type) || StringUtils.isBlank(id))
					continue;
				if("Member".equals(type)){
					str="member";//----判断出来是人员就返回member
				}else if("Department".equals(type)){
					str="department";//----判断出来是人员就返回department
				}else if("Account".equals(type)){
					str="account";//----判断出来是人员就返回department
				}
			}
		return str;
	}

	
/**
	 * 根据选人界面得的字符串，得到人员的id
	 * @param typeAndIds
	 * @return
	 */
	public List<V3xOrgMember> getMembersId(String typeAndIds){
		List<V3xOrgMember> list=new ArrayList<V3xOrgMember>();
		if(StringUtils.isEmpty(typeAndIds))
			return list;
		String[] typeAndIdA=typeAndIds.split(",");
		try {
			for(String typeAndIdStr:typeAndIdA){
				String[] typeAndId=typeAndIdStr.split("\\|");
				String type="";
				String id="";
				//默认为用户
				if(typeAndId.length==1){
					type="Member";
					id=typeAndId[0];
				}else{
					type=typeAndId[0];
					id=typeAndId[1];
				}
				if(StringUtils.isBlank(type) || StringUtils.isBlank(id))
					continue;
				
				list.addAll(orgManager.getMembersByType(type, Long.valueOf(id)));	
			}
		} catch (BusinessException e) {
			log.error("获取人员出错", e);
		}
		return list;
	}
	
	/**
	 * 根据项目id，得到当前项目的人员列表
	 * @param typeAndIds
	 * @return
	 */
	public List<V3xOrgMember> getProjectMebIds(String typeAndIds){
		List list=new ArrayList();
		if(StringUtils.isEmpty(typeAndIds))
			return list;
		V3xOrgMember vm = null;
		List<V3xOrgMember> oml = new ArrayList<V3xOrgMember>();
		
		try {
			list=this.projectDao.getProjectMemberList(Long.valueOf(typeAndIds));
			
			for(int i=0;i<list.size();i++){
				
				vm = orgManager.getMemberById(Long.valueOf(list.get(i).toString()));
				oml.add(vm);
			}
		} catch (NumberFormatException e) {
			log.error(e.getMessage(),e);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return oml;
	}
	
	/**
	 * 根据选人界面得的字符串，得到部门的id
	 * @param typeAndIds
	 * @return
	 */
	
	public List<V3xOrgDepartment> getDepId(String typeAndIds){
		List<V3xOrgDepartment> list=new ArrayList<V3xOrgDepartment>();
		if(StringUtils.isEmpty(typeAndIds))
			return list;
		String[] typeAndIdA=typeAndIds.split(",");
		try {
			for(String typeAndIdStr:typeAndIdA){
				String[] typeAndId=typeAndIdStr.split("\\|");
				String type="";
				String id="";
				//默认为用户
				if(typeAndId.length==1){
					type="Member";
					id=typeAndId[0];
				}else{
					type=typeAndId[0];
					id=typeAndId[1];
				}
				if(StringUtils.isBlank(type) || StringUtils.isBlank(id))
					continue;
				if("Department".equals(type)){
					//处理部门
					list.add(orgManager.getDepartmentById(Long.valueOf(id)));
				}
			}
			return list;
		} catch (BusinessException e) {
			log.error("获取人员出错", e);
		}
		return list;
	}
	
	/**
	 * 根据选人界面得的字符串，得到单位的id
	 * @param typeAndIds
	 * @return
	 */
	
	public List<V3xOrgAccount> getAccountId(String typeAndIds){
		List<V3xOrgAccount> list=new ArrayList<V3xOrgAccount>();
		if(StringUtils.isEmpty(typeAndIds))
			return list;
		String[] typeAndIdA=typeAndIds.split(",");
		try {
			for(String typeAndIdStr:typeAndIdA){
				String[] typeAndId=typeAndIdStr.split("\\|");
				String type="";
				String id="";
				//默认为用户
				if(typeAndId.length==1){
					type="Member";
					id=typeAndId[0];
				}else{
					type=typeAndId[0];
					id=typeAndId[1];
				}
				if(StringUtils.isBlank(type) || StringUtils.isBlank(id))
					continue;
				if("Account".equals(type)){
					//处理部门
					list.add(orgManager.getAccountById(Long.valueOf(id)));
				}
			}
			return list;
		} catch (BusinessException e) {
			log.error("获取人员出错", e);
		}
		return list;
	}
	/**
	 * 从ID列表中删掉某一个特定的ID
	 * @param typeAndIds
	 * @param id
	 * @return
	 */
	public String delMemberFromTypeAndIds(String typeAndIds,String id){
		if(StringUtils.isEmpty(typeAndIds)) return "";
		String result="";
		String[] typeAndIdA=typeAndIds.split(",");
		for(String typeAndId:typeAndIdA){
			if(typeAndId.indexOf("id")==-1)
				result=result+typeAndId+",";
		}
		if(result.length()>0){
			result = result.substring(0,result.length()-1);
		}
		return result;
	}
	/**
	 * 从名称列表中删掉某一个特定的名称
	 * @param names
	 * @param name
	 * @return
	 */
	public String delMemberFromNames(String names,String name){
		if(StringUtils.isEmpty(names)) return "";
		String result="";
		String[] namesA=names.split(",");
		for(String nameStr:namesA){
			if(!nameStr.equals(name))
				result=result+nameStr+",";
		}
		if(result.length()>0){
			result = result.substring(0,result.length()-1);
		}
		return result;
	}

	
    /**
     * 判断当前事件是否属于某个部门
     * @param event
     * @param departmentId
     * @return
     */
    public boolean isEventBelongToDepartment(CalEvent event, String deptPath){
    	OrgManager orgManager1 = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
        boolean result = false;
        StringTokenizer depIdsToken = new StringTokenizer(event.getTranMemberIds(), ",");
        while(depIdsToken.hasMoreTokens()){
            String depStr = depIdsToken.nextToken();
            if(depStr.startsWith("Department|")){
                String depIdStr = depStr.substring("Department|".length(), depStr.length());
                Long eventDepartmentId = Long.parseLong(depIdStr);
                try{
                    V3xOrgDepartment eventDept = orgManager1.getDepartmentById(eventDepartmentId);
                    if(deptPath != null && eventDept != null){
                        if(deptPath.equals(eventDept.getPath()) || deptPath.startsWith(eventDept.getPath()+".")){                            
                            result = true;
                            break;
                        }
                    }
                }
                catch (BusinessException e){
                	log.error("判断当前事件是否属于某个部门", e);
                }
            }
        }
        return result;
    }
    
    public String getShareTagretString(String ids){
    	List<V3xOrgMember> list = getMembersId(ids);
    	if(list!=null&&list.size()>=1){
    		return list.get(0).getName();
    	}else{
    		return "";
    	}
    }
    
    /**
     * 按照指定区间，把日程事件按照天进行分组
     * 
     * @param map 返回的结果集，<Date(yyyy-MM-dd), List(这天下的日程事件)>
     * @param beginDate 显示区间的开始日期，比如月试图，这就是月第一天
     * @param endDate 显示区间的结束日期，比如月试图，这就是月最后天
     * @param list 从数据库冲抽取的原始数据
     */
    public static void addtoMap(Map<String, List<CalEvent>> map, Date beginDate, Date endDate, List<CalEvent> list){
    	if(Strings.isEmpty(list)){
    		return;
    	}
    	
		for (CalEvent event : list) {
			Date date1 = event.getBeginDate();
			Date date2 = event.getEndDate();
			
			Date sDate = date1.before(beginDate) ? beginDate : date1;
			Date eDate = date2.after(endDate) ? endDate : date2;
			
			while(sDate.compareTo(eDate) < 1){
				String nextDay = Datetimes.formatDate(sDate);
				Strings.addToMap(map, nextDay, event);
				
				sDate = Datetimes.addDate(sDate, 1);
			}
		}
    }
}