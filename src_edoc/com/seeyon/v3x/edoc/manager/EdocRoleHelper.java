package com.seeyon.v3x.edoc.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.UniqueList;

public class EdocRoleHelper {
	
    public static String AccountEdocAdminRoleName="AccountEdocAdmin";
	public static String acountExchangeRoleName="account_exchange";
	public static String departmentExchangeRoleName="department_exchange";
	public static String accountEdocCreateRoleName="account_edoccreate";
	public static OrgManager orgManager=(OrgManager)ApplicationContextHolder.getBean("OrgManager");
	//private static OrgManagerDirect orgManagerDirect=(OrgManagerDirect)ApplicationContextHolder.getBean("OrgManagerDirect");
	
	public static ConfigManager configManager=(ConfigManager)ApplicationContextHolder.getBean("configManager");
	/**
	 * 得到当前登陆人员所在单位的公文收发员
	 * @return
	 * @throws BusinessException
	 */
	public static List<V3xOrgMember> getAccountExchangeUsers() throws BusinessException
	{
		/*V3xOrgRole roleExchange = orgManagerDirect.getRoleByName(acountExchangeRoleName);
		return orgManagerDirect.getMemberByRole(roleExchange.getBond(), CurrentUser.get().getLoginAccount(), roleExchange.getId());
		*/
		//*
		User user=CurrentUser.get();
		V3xOrgRole exchangeRole=orgManager.getRoleByName(acountExchangeRoleName,user.getLoginAccount());
		return orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT,user.getLoginAccount(), exchangeRole.getId());
		//*/
	}
	public static boolean isEdocCreateRole(int edocType) throws BusinessException
	{
		User user=CurrentUser.get();
		return isEdocCreateRole(user.getLoginAccount(),user.getId(),edocType);
	}

	/**
	 * 判断当前登陆人员是否为指定单位的公文发起员角色
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isEdocCreateRole(Long accountId,Long userId,int edocType) throws BusinessException
	{
		if(accountId==null){
			accountId=CurrentUser.get().getLoginAccount();
		}
		
		String sendEntitys="";
		ConfigItem item=null;
		if(edocType==EdocEnum.edocType.sendEdoc.ordinal())
		{
			item=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SEND, accountId);			
		}
		else if(edocType==EdocEnum.edocType.recEdoc.ordinal())
		{
			item=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_REC, accountId);
		}
		else if(edocType==EdocEnum.edocType.signReport.ordinal())
		{
			item=configManager.getConfigItem(IConfigPublicKey.EDOC_CREATE_KEY, IConfigPublicKey.EDOC_CREATE_ITEM_KEY_SIGN, accountId);
		}
		if(item==null){return false;}
		sendEntitys=item.getExtConfigValue();
		if(null==sendEntitys){return false;}
		List<Long> myIds=orgManager.getUserDomainIDs(userId,accountId,V3xOrgEntity.ORGENT_TYPE_ACCOUNT,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST);
		myIds.add(userId);
		for(Long objId:myIds)
		{
			if(sendEntitys.indexOf(objId.toString())>=0)
			{
				return true;
			}
		}
		//组单独进行处理
		List<Long> teams=orgManager.getUserDomainIDs(userId,accountId,V3xOrgEntity.ORGENT_TYPE_TEAM);
		for(Long tid:teams)
		{
		    if(sendEntitys.indexOf(tid.toString())>=0){
				//组：组领导和组关联人员不具有公文发起权 bug31661 muj
				V3xOrgTeam v3xOrgTeam=(V3xOrgTeam)orgManager.getEntity(V3xOrgTeam.class, tid);
				if(v3xOrgTeam!=null){
					List<Long> v3xOrgMembers=v3xOrgTeam.getAllMembers();
					if(v3xOrgMembers.contains(userId)) return true;
					else return false;
				}
				return true;
		    }
		}
		return false;
	}
	/**
	 * 判断当前登陆人员是否为 登陆单位的公文发起员角色
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isEdocCreateRole(Long userId,int edocType) throws BusinessException
	{
		User user  =CurrentUser.get();
		return isEdocCreateRole(user.getLoginAccount(),user.getId(),edocType);
	}
	
	/**
	 * 有从集团导入公文单功能,当前产品是集团版，并且当前用户是单位管理员，返回true
	 * @return
	 * @throws BusinessException
	 */
	public static boolean hasInputFunctionFromGroup() throws BusinessException
	{		
		User user=CurrentUser.get();
		boolean isGroupAdmin=orgManager.isGroupAdmin(user.getLoginName());
		if(isGroupAdmin){return false;}
		boolean isAccountAdmin=orgManager.isAccountAdmin(user.getLoginName());
		boolean hasImportEdocForm=(Boolean)(SysFlag.edoc_showImportEdocForm.getFlag());
		if(isAccountAdmin && hasImportEdocForm){return true;}
		return false;					
	}
	/*
	 * 是否有修改公文元素的权限，集团管理员，企业版、政务版中的单位管理员
	 */
	public static boolean canEditEdocElements() throws BusinessException
	{
		User user=CurrentUser.get();
		boolean isEnterVer=((Boolean)(SysFlag.sys_isEnterpriseVer.getFlag()) || (Boolean)(SysFlag.sys_isGovVer.getFlag()));
		if(isEnterVer==true && orgManager.isAccountAdmin(user.getLoginName())){return true;}
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());
		if(isGroupVer && orgManager.isGroupAdmin(user.getLoginName())){return true;}
		return false;
	}
	/**
	 * 判断当前用户是否为集团管理员
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isGroupManager() throws BusinessException
	{
		User user=CurrentUser.get();
		return orgManager.isGroupAdmin(user.getLoginName());	
	}
	/**
	 * 得到指定单位的公文收发员
	 * @param accountId：单位ID
	 * @return
	 * @throws BusinessException
	 */
	public static List<V3xOrgMember> getAccountExchangeUsers(Long accountId) throws BusinessException
	{		
		V3xOrgRole roleExchange = orgManager.getRoleByName(acountExchangeRoleName,accountId);
		return orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT,accountId, roleExchange.getId());		
	}
	/**
	 * 得到当前用户的部门收发员
	 * @return
	 * @throws BusinessException
	 */
	public static List<V3xOrgMember> getDepartMentExchangeUsers() throws BusinessException
	{
		/*
		V3xOrgRole roleExchange=null;
		User user=CurrentUser.get();
		V3xOrgDepartment dep=orgManagerDirect.getDepartmentById(user.getDepartmentId());
		List <Long> roleIds=dep.getRoles();
		for(Long roleId:roleIds)
		{
			roleExchange=orgManagerDirect.getRoleById(roleId);
			if(departmentExchangeRoleName.equals(roleExchange.getName()))
			{			
				break;
			}
		}		
		return orgManagerDirect.getMemberByRole(roleExchange.getBond(), user.getDepartmentId(), roleExchange.getId());
		*/
		//*
		User user=CurrentUser.get();
		return getDepartMentExchangeUsers(user.getLoginAccount(),user.getDepartmentId());
		//*/
	}
	
	/**
	 * 得到当前用户的部门收发员
	 * @return
	 * @throws BusinessException
	 */
	public static List<V3xOrgMember> getDepartMentExchangeUsers(Long accountId,Long departmentId) throws BusinessException
	{
		/*
		V3xOrgRole roleExchange=null;		
		V3xOrgAccount acc= orgManagerDirect.getAccountById(accountId);
		V3xOrgDepartment dep=acc.getDepartments().get(departmentId);		
		List <Long> roleIds=dep.getRoles();
		for(Long roleId:roleIds)
		{
			roleExchange=orgManagerDirect.getRoleById(roleId);
			if(departmentExchangeRoleName.equals(roleExchange.getName()))
			{			
				break;
			}
		}		
		return orgManagerDirect.getMemberByRole(roleExchange.getBond(), dep.getId(), roleExchange.getId());
		*/
		///*
		//User user=CurrentUser.get();
		V3xOrgRole exchangeRole=orgManager.getRoleByName(departmentExchangeRoleName,accountId);
		return orgManager.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT,departmentId, exchangeRole.getId());
		//*/
	}
	/**
	 * 判断当前用户是否为登陆单位的公文收发员
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isAccountExchange() throws BusinessException {
		return isAccountExchange(CurrentUser.get().getId());		
	}
	
	/**
	 * branches_a8_v350_r_gov 唐桂林添加判断某用户是否为登陆单位的公文收发员
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isAccountExchange(Long memberId)  throws BusinessException {
		User user=CurrentUser.get();
		V3xOrgRole exchangeRole=orgManager.getRoleByName(acountExchangeRoleName, orgManager.getMemberById(memberId).getOrgAccountId());
		return orgManager.isInDomain(user.getLoginAccount(), exchangeRole.getId(),memberId);		
	}
	
	/**
	 * branches_a8_v350_r_gov 唐桂林添 判断某用户是否为当前登录单位的部门收发员（如果兼职到几个部门，则只要是其中一个部门的部门收发员即可）
	 * @param accountId ：单位ID
	 * @return
	 * @throws Exception
	 */
	public static boolean isDepartmentExchangeOfLoginAccout(Long memberId) throws BusinessException{
		Long accountId = orgManager.getMemberById(memberId).getOrgAccountId();
		return !"".equals(getUserExchangeDepartmentIds(memberId, accountId));
	}
	
	
	/**
	 * branches_a8_v350_r_gov 唐桂林添 得到某用户在<指定单位下>承担部门收发员的部门ID
	 * @param accountId	：指定单位ID< 当accountId为VIRTUAL_ACCOUNT_ID时，返回所有的单位下的实体合集--集团化支持>
	 * @return , ','分割的字符串
	 */
	public static String getUserExchangeDepartmentIds(Long memberId, Long accountId)  throws BusinessException {
		return getUserExchangeAccountIdsOrDepartmentIds(departmentExchangeRoleName, memberId, accountId);
	}
	
	/**
	 * branches_a8_v350_r_gov 唐桂林添 得到某用户在<指定单位下>承担部门收发员的部门ID
	 * @param exchangeRoleName
	 * @param memberId
	 * @param accountId
	 * @return
	 * @throws BusinessException
	 */
	public static String getUserExchangeAccountIdsOrDepartmentIds(String exchangeRoleName, Long memberId, Long accountId)  throws BusinessException {
		V3xOrgRole exchangeRole=null;
		List <Long> depIds=new ArrayList<Long>();
		Collection<Long> accountIds = new UniqueList<Long>();
		//1.查找单位ID
		if(accountId.equals(V3xOrgEntity.VIRTUAL_ACCOUNT_ID)){
			for(V3xOrgAccount account:orgManager.getAllAccounts()){
				accountIds.add(account.getId());
			}
		}else{
			accountIds.add(accountId);
		}
		//2、查找充当单位收发员的单位Id
		for(Long accId:accountIds){
			 exchangeRole=orgManager.getRoleByName(exchangeRoleName,accId);
			 if(exchangeRole != null)
				 depIds.addAll(orgManager.getDomainByRole(exchangeRole.getId(), memberId));
		}
		String str="";
		for(Long depId:depIds){
			if(!"".equals(str)){
				str+=",";
			}
			str+=depId;
		}
		return str;
	}
	
	/**
	 * 判断当前用户是否为收发员（包括部门收发员，单位收发员）
	 * @return
	 */
	public static boolean isExchangeRole()  throws BusinessException
	{
		boolean isExchange=false;
		try
		{
			isExchange=(isAccountExchange() || isDepartmentExchange());
		}catch(Exception e)
		{	
		}
		return isExchange;
	}
	
	/**
	 * 判断当前用户是否为登陆部门的公文收发员
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isDepartmentExchange()  throws BusinessException
	{
		User user=CurrentUser.get();
		/*
		List <V3xOrgMember> mems=getAccountExchangeUsers();
		for(V3xOrgMember mem:mems)
		{
			if(mem.getId()==user.getId())
			{
				return true;
			}
		}
		*/
		V3xOrgRole exchangeRole=orgManager.getRoleByName(departmentExchangeRoleName,user.getLoginAccount());
		//orgManager.getUserDomainIDs(userId, types)		
		return orgManager.isInDomain(user.getDepartmentId(), exchangeRole.getId(),user.getId());		
	}
	/**
	 * 判断某个用户是否是某个单位下面某个部门的公文收发员
	 * @param accountId		：单位ID
	 * @param departmentId	：部门ID
	 * @param userId		：用户ID
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isDepartmentExchange(Long accountId,Long departmentId,Long userId)  throws BusinessException
	{
		V3xOrgRole exchangeRole=orgManager.getRoleByName(departmentExchangeRoleName,accountId);
		return orgManager.isInDomain(departmentId, exchangeRole.getId(),userId);		
	}
	/**
	 * 判断当前用户是否为当前登录单位的部门收发员（如果兼职到几个部门，则只要是其中一个部门的部门收发员即可）
	 * @param accountId ：单位ID
	 * @return
	 * @throws Exception
	 */
	public static boolean isDepartmentExchangeOfLoginAccout() throws BusinessException{
		return !"".equals(getUserExchangeDepartmentIds(CurrentUser.get().getLoginAccount()));
	}
	
	/**
	 * 得到当前用户在<指定单位下>承担部门收发员的部门ID
	 * @param accountId	：指定单位ID< 当accountId为VIRTUAL_ACCOUNT_ID时，返回所有的单位下的实体合集--集团化支持>
	 * @return , ','分割的字符串
	 */
	public static String getUserExchangeDepartmentIds(Long accountId)  throws BusinessException
	{
		return getUserExchangeAccountIdsOrDepartmentIds(departmentExchangeRoleName,accountId);
	}
	/**
	 * 得到当前用户在<当前登录单位>承担部门收发员的部门ID
	 * 	 * @return , ','分割的字符串
	 */
	public static String getUserExchangeDepartmentIds()  throws BusinessException
	{
		User user=CurrentUser.get();
		return getUserExchangeDepartmentIds(user.getLoginAccount());
	}
	/**
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public static List<Long> getUserExchangeDepartmentIdsToList()  throws BusinessException{
		List<Long> list=new ArrayList<Long>();
		String ids=getUserExchangeDepartmentIds();
		if(ids!=null && !"".equals(ids)){
			for(String s:ids.split(",")){
				list.add(Long.parseLong(s));
			}
		}
		return list;
	}
	/**
	 * 得到当前用户在<指定单位下>承担单位收发员的单位ID
	 * 	 * @return , ','分割的字符串
	 */
	public static String getUserExchangeAccountIds(Long accountId)  throws BusinessException
	{
		return getUserExchangeAccountIdsOrDepartmentIds(acountExchangeRoleName,accountId);
	}
	/**
	 * 得到当前用户在<登录单位>承担单位收发员的单位ID
	 * @return , ','分割的字符串
	 */
	public static String getUserExchangeAccountIds()  throws BusinessException
	{
		User user=CurrentUser.get();
		return getUserExchangeAccountIds(user.getLoginAccount());
	}
	
	public static String getUserExchangeAccountIdsOrDepartmentIds(String exchangeRoleName,Long accountId)  throws BusinessException {
		return getUserExchangeAccountIdsOrDepartmentIds(exchangeRoleName, CurrentUser.get().getId(), accountId);
	}
	
	public static V3xOrgAccount getAccountById(Long accountId) throws Exception
	{
		return orgManager.getAccountById(accountId);
	}
	
	public static V3xOrgDepartment getDepartmentById(Long deptId) throws Exception
	{
		return orgManager.getDepartmentById(deptId);
	}
	
	/**
     * 判断当前用户是否为登陆单位的公文管理员
     * @return
     * @throws BusinessException
     */
    public static boolean isAccountEdocAdmin()  throws BusinessException {
        User user=CurrentUser.get();
        V3xOrgRole accountEdocAdminRole=orgManager.getRoleByName(AccountEdocAdminRoleName,user.getLoginAccount());
        if(accountEdocAdminRole==null){
        	return false;
        }else{
        	return orgManager.isInDomain(user.getLoginAccount(), accountEdocAdminRole.getId(),user.getId());
        }
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
