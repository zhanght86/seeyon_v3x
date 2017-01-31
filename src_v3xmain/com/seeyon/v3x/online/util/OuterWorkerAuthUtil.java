package com.seeyon.v3x.online.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.util.Strings;

public class OuterWorkerAuthUtil {
	private static final Log log = LogFactory.getLog(OuterWorkerAuthUtil.class);
	/**
	 * 得到外单位人员可以访问的 部门。
	 * @param memberId
	 * @param departmentId
	 * @param accountId
	 * @param orgManager
	 * @return
	 * @throws BusinessException
	 */
	public static Collection<V3xOrgDepartment> getCanAccessDep(Long memberId,Long departmentId,Long accountId,OrgManager orgManager) throws BusinessException{
		List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(memberId,false);
		Map<Long,V3xOrgDepartment> depMap = new HashMap<Long,V3xOrgDepartment>();
		if(canReadList != null && !canReadList.isEmpty()){
			for(V3xOrgEntity access : canReadList){
				//可访问人员
				if(access instanceof V3xOrgMember){
					V3xOrgMember m = (V3xOrgMember) access;
					V3xOrgDepartment d = orgManager.getDepartmentById(m.getOrgDepartmentId());
					if(d != null){
						if(d.getOrgAccountId().equals(accountId)){
							depMap.put(d.getId(), d);
						} else{ //被访问的人在当前单位是兼职，找他的兼职部门
							Set<Long> secondDepts = orgManager.getConcurentPostsByMemberId(accountId, m.getId()).keySet();
							if(!secondDepts.isEmpty()){
								V3xOrgDepartment d2 = orgManager.getDepartmentById(secondDepts.iterator().next());
								if(d2 != null){
									depMap.put(d2.getId(), d2);
								}
							}
						}
					}
				}else if(access instanceof V3xOrgDepartment){
					V3xOrgDepartment d = (V3xOrgDepartment) access;
					List<V3xOrgDepartment> childDep = orgManager.getChildDepartments(d.getId(), false);//不要其他的外部门
					for(V3xOrgDepartment child : childDep){
						if(child.getIsInternal())
							depMap.put(child.getId(), child);
					}
					depMap.put(d.getId(), d);
				}else if (access instanceof V3xOrgAccount){
					if(accountId.longValue() == access.getId()){
						List<V3xOrgDepartment> depts = orgManager.getAllDepartments(access.getId());
						for(V3xOrgDepartment d : depts){
							depMap.put(d.getId(), d);
						}
					}
				}
			}
		}
		//组成员
		List<V3xOrgTeam> outWorkerTeam = orgManager.getTeamsByMember(memberId,accountId);
		if(outWorkerTeam != null && !outWorkerTeam.isEmpty()){
			for(V3xOrgTeam team : outWorkerTeam){
				List<Long> teamMember = team.getAllMembers();
				for(Long teamMemberId :teamMember){
					V3xOrgMember m = orgManager.getMemberById(teamMemberId);
					if(m != null && !m.getIsDeleted() && m.getOrgAccountId().longValue() == accountId){
						V3xOrgDepartment d = orgManager.getDepartmentById(m.getOrgDepartmentId());
						if(d.getIsInternal())
							depMap.put(d.getId(), d);
					}
				}
			}
		}
		//本部门
		V3xOrgDepartment outDep = orgManager.getDepartmentById(departmentId);
		depMap.put(outDep.getId(), outDep);
		/*//跨靠部门---没有隶属关系
		if(!canAccessAllAccount){
			List<V3xOrgDepartment> depts = orgManager.getAllDepartments(accountId);
			for(V3xOrgDepartment d : depts){
				if(outDep.getOrgAccountId().longValue() == d.getOrgAccountId() && d.getParentPath().equals(outDep.getParentPath())){
					depMap.put(d.getId(), d);
				}
			}
		}*/
		List<V3xOrgDepartment> result = new ArrayList<V3xOrgDepartment>();
		for(V3xOrgDepartment dept : depMap.values()) {
			result.add(dept);
		}
		Collections.sort(result, CompareSortEntity.getInstance());//AEIGHT-10051
		return result;
	}
	
	
	/**
	 * 内部人员是否可以访问本外部门
	 * @param memberId
	 * @param departmentId
	 * @param accountId
	 * @param outDepartment 外部门
	 * @param orgManager
	 * @return
	 * @throws BusinessException
	 */
	public static boolean canAccessOuterDep(Long memberId,Long departmentId,Long accountId,V3xOrgDepartment outDepartment,OrgManager orgManager) throws BusinessException{
		//判断是否是跨靠部门-本部门
		V3xOrgDepartment userDep = orgManager.getDepartmentById(departmentId);
		try {
			if(outDepartment == null || userDep == null) return false;
			if((Strings.isNotBlank(userDep.getParentPath()) && userDep.getParentPath().equals(outDepartment.getParentPath())) || userDep.getId() == outDepartment.getId()){
				return true;
			}
			if (Strings.isNotBlank(outDepartment.getPath()) && outDepartment.getPath().indexOf(".") > 0 && (outDepartment.getPath().indexOf(".") == outDepartment.getPath().lastIndexOf("."))) {
				//父节点是单位，全单位人员都能看到这个外部人员
				return true;
			}
			List<Long> depIds = orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
			List<Long> accountIds = orgManager.getUserDomainIDs(memberId, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
			List<V3xOrgMember> members = orgManager.getExtMembersByDepartment(outDepartment.getId(), false);
			for(V3xOrgMember m : members){
				//权限部门
				Long longAccountId = accountId;
				List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(m.getId(),false);
				for(V3xOrgEntity entity : canReadList){
					if(entity.getId().longValue() == memberId || entity.getId().longValue() == departmentId || entity.getId().longValue() == accountId || entity.getId().longValue()== longAccountId || depIds.contains(entity.getId().longValue())  || accountIds.contains(entity.getId().longValue())){
						return true;
					}else{
						Map<Long, List<ConcurrentPost>> map = orgManager.getConcurentPostsByMemberId(longAccountId, memberId);
						if(map!=null && map.containsKey(entity.getId())){
							return true;
						}
					}
				}
				//组
				List<V3xOrgTeam> teams = orgManager.getTeamsByMember(m.getId());
				for(V3xOrgTeam t : teams){
					List<Long> m1 = t.getAllMembers();
					for(Long mm : m1){
						if( mm.longValue() == memberId){
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("判断是否可以访问外部门",e);
		}
		return false;
	}
	
	/**
	 * 获取外部人员部门列表结构
	 * @return
	 * @throws Exception
	 */
	public static List<WebV3xOrgDepartment> getOuterDeptList(ModelAndView mav, User user, Long currentAccountId, 
			OrgManager orgManager) throws Exception {
		Collection<V3xOrgDepartment> canReadList = OuterWorkerAuthUtil.getCanAccessDep(user.getId(), user.getDepartmentId(), user.getAccountId(), orgManager);
		mav.addObject("external", canReadList);
		
		Map<Long, WebV3xOrgDepartment> webDeptList = new HashMap<Long, WebV3xOrgDepartment>();
		
		for(V3xOrgDepartment dept : canReadList){
			if(dept != null){
				WebV3xOrgDepartment webDept = new WebV3xOrgDepartment();
				webDept.setV3xOrgDepartment(dept);
				OuterWorkerAuthUtil.findParentDept(webDeptList, webDept, dept.getId(), currentAccountId, orgManager);
				webDeptList.put(dept.getId(), webDept);
			}
		}
		
		return new ArrayList<WebV3xOrgDepartment>(webDeptList.values());
	}
	
	/**
	 * 组织部门列表结构
	 */
	public static void findParentDept(Map<Long, WebV3xOrgDepartment> webDeptList, WebV3xOrgDepartment webDept, 
			Long deptId, Long accountId, OrgManager orgManager) throws Exception{
		V3xOrgDepartment parentDept = orgManager.getParentDepartment(deptId, accountId);
		if (parentDept != null) {
			//设置父节点
			webDept.setParentId(parentDept.getId());
			webDept.setParentName(parentDept.getName());
			
			//添加父节点
			WebV3xOrgDepartment webParentDept = new WebV3xOrgDepartment();
			webParentDept.setV3xOrgDepartment(parentDept);
			
			OuterWorkerAuthUtil.findParentDept(webDeptList, webParentDept, parentDept.getId(), accountId, orgManager);
			
			webDeptList.put(parentDept.getId(), webParentDept);
		}
	}
	/**
	 * 合并list
	 * @param l
	 * @param ll
	 * @return
	 */
	public static <T> List<T> combineList(List<T> l,List<T> ll) {
    	if(l == null && ll != null) {
    		return ll;
    	} else if(l != null && ll == null) {
    		return l;
    	} else if(l != null && ll != null){
    		l.addAll(ll);
        	return l;
    	}
    	return null;
    }
}