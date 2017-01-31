package com.seeyon.v3x.flowperm.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.edoc.domain.EdocElementFlowPermAcl;
import com.seeyon.v3x.edoc.manager.EdocElementFlowPermAclManager;
import com.seeyon.v3x.flowperm.domain.FlowPerm;
import com.seeyon.v3x.flowperm.manager.FlowPermManager;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class FlowPermHelper {

	/**
	 * 根据单位Id复制节点权限。
	 *
	 */
	public static void generateFlowPermByAccountId(long accountId)throws Exception{
		ConfigManager configManager= (ConfigManager)ApplicationContextHolder.getBean("configManager");
		//EdocElementManager edocElementManager = (EdocElementManager)ApplicationContextHolder.getBean("edocElementManager");
		EdocElementFlowPermAclManager edocElementFlowPermAclManager = (EdocElementFlowPermAclManager)ApplicationContextHolder.getBean("edocElementFlowPermAclManager");
		
		//遍历出所有公文和协同的数据，重新赋值accountId
		List<ConfigItem> list = new ArrayList<ConfigItem>();
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.edoc_send_permission_policy.name()));
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.edoc_rec_permission_policy.name()));
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.edoc_qianbao_permission_policy.name()));
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.col_flow_perm_policy.name()));
		
		for(ConfigItem item : list){
			
			ConfigItem newItem = new ConfigItem();
			newItem.setNewId();
			newItem.setConfigCategory(item.getConfigCategory());
			newItem.setConfigDescription(item.getConfigDescription());
			newItem.setConfigItem(item.getConfigItem());
			newItem.setExtConfigValue(item.getExtConfigValue());
			newItem.setConfigValue(item.getConfigValue());
			newItem.setConfigType(item.getConfigType());
			newItem.setCreateDate(new Timestamp(new Date().getTime()));
			newItem.setModifyDate(new Timestamp(new Date().getTime()));
			newItem.setOrgAccountId(accountId);
			
			boolean isEdoc = false;//权限的类型－－是否为公文
			
			if(!item.getConfigCategory().equals(MetadataNameEnum.col_flow_perm_policy.name())){
				isEdoc = true;
			}
			
			List<EdocElementFlowPermAcl> elementList = null;
			
			if(isEdoc){//如果是公文权限，查找出公文权限授权元素
				elementList = edocElementFlowPermAclManager.getEdocElementFlowPermAcls(item.getId());
			}

			
			List<EdocElementFlowPermAcl> newElementList = new ArrayList<EdocElementFlowPermAcl>();
			if(isEdoc && null!=elementList && elementList.size()>0){//如果是公文权限，重新赋值
				for(EdocElementFlowPermAcl acl:elementList){
					EdocElementFlowPermAcl newAcl = new EdocElementFlowPermAcl();
					newAcl.setNewId();
					newAcl.setAccess(acl.getAccess());
					newAcl.setFlowPermId(newItem.getId());
					newAcl.setEdocElement(acl.getEdocElement());
					newElementList.add(newAcl);
				}	
			}
			configManager.addConfigItem(newItem);
			
			if(isEdoc){//需要在保存权限之后保存授权公文元素
				edocElementFlowPermAclManager.saveEdocElementFlowPermAcls(newElementList);				
			}
		}
		
	}
	
	/**
	 * 根据类别返回节点权限得名称,用于流程中选择权限
	 * @category						1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * @return
	 */
	public static List<String> getPermNameByCat(String category, int categoryType)throws Exception{
		
		User user = CurrentUser.get();
		List<String> nameList = new ArrayList<String>();
		FlowPermManager flowPermManager= (FlowPermManager)ApplicationContextHolder.getBean("flowPermManager");
		MetadataManager metadataManager= (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
		List<FlowPerm> list = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, false, categoryType, user.getLoginAccount());
		
		ResourceBundle r = null;
		String label = "";
		String value = "";
		for(FlowPerm perm : list){
				if(perm.getType() == Constants.F_type_system){
					label = metadataManager.getMetadataItemLabel(perm.getCategory(), perm.getName());
					if(null!=label && !"".equals(label)){
					r = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",CurrentUser.get().getLocale());
						value = ResourceBundleUtil.getString(r, label);
					}
					nameList.add(value);
				}else if(perm.getType() == Constants.F_type_custom){
					nameList.add(perm.getName());
				}
		}
		return nameList;
	}
	
	/**
	 * 检查该单位是否有节点权限
	 * @param domainId 需要查询的单位Id
	 * @return
	 * @throws Exception
	 */
	
	public static List<V3xOrgAccount> findAllOrgAccountId()throws Exception{
		
		OrgManager orgManager= (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		List<V3xOrgAccount> orgAccountList = orgManager.getAllAccounts();
		return orgAccountList;
		
	}
	
	public static void generateFlowPermByAccountId()throws Exception{
		
		/*
		String[] accountIds = new String[19];
		accountIds[0] = "-5842096670975377071";
		accountIds[1] = "398974923680927463";	
		accountIds[2] = "-6666530054341111829";
		accountIds[3] = "4536994725654447449";
		accountIds[4] = "-4095244290179119008";
		accountIds[5] = "-3116394547481915958";
//		accountIds[6] = "-1730833917365171641";
		accountIds[6] = "-5789962364975495891";
		accountIds[7] = "1638915334395872229";
		accountIds[8] = "1651851455044339602";
//		accountIds[10] = "5559698537516190201";
		accountIds[9] = "5982526980516038302";
		accountIds[10] = "-5604182147571363904";
		accountIds[11] = "-6908590613253894380";
		accountIds[12] = "5513540298151641412";
		accountIds[13] = "7604012972832605278";
		accountIds[14] = "1294144499834505531";
		accountIds[15] = "7125785720960272327";
		accountIds[16] = "-2982981734034487606";
		accountIds[17] = "-5475667518736413462";
		accountIds[18] = "-896766627258453261";
		*/
		String[] accountIds = new String[1];
		accountIds[0] = "-4003841152420854095";
		
		for(String str: accountIds){
			initialFlowPermByDomainId(Long.valueOf(str));
		}
		
	}
	
	public static boolean checkHasFlowPermByDomainId(long domainId)throws Exception{
		
		boolean bool = false;
		
		ConfigManager configManager= (ConfigManager)ApplicationContextHolder.getBean("configManager");
		
		//遍历出所有公文和协同的数据，重新赋值accountId
		List<ConfigItem> list = new ArrayList<ConfigItem>();
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.edoc_send_permission_policy.name(), domainId));
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.edoc_rec_permission_policy.name(), domainId));
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.edoc_qianbao_permission_policy.name(), domainId));
		list.addAll(configManager.listAllConfigByCategory(MetadataNameEnum.col_flow_perm_policy.name(), domainId));	
		
		if(null!=list && list.size()>0){
			bool = true;
		}
		
		return bool;
	}
	
	/**
	 * 如果在当前单位下没有发现节点权限，初始化节点权限
	 * 初始化节点权限
	 * @param accountId
	 * @throws Exception
	 */
	public static void initialFlowPermByDomainId(long accountId)throws Exception{
		boolean bool = checkHasFlowPermByDomainId(accountId);
		if(!bool){
			generateFlowPermByAccountId(accountId);
		}
	}
	
	public static void initialV3xOrgMemberSecurityPermission()throws Exception{
		
		MenuManager menuManager= (MenuManager)ApplicationContextHolder.getBean("menuManager");
		OrgManager orgManager = (OrgManager)ApplicationContextHolder.getBean("OrgManager");
		
		List<Long> securityList = new ArrayList<Long>();
		securityList.add(Long.valueOf("1"));
		
		List<V3xOrgMember> memberList = orgManager.getAllMembers();
		for(V3xOrgMember member : memberList){
			menuManager.saveMemberSecurity(member.getId(), member.getOrgAccountId(), securityList);			
		}
		
	}
	
	/**
	 * 根据类别返回节点权限得名称,用于流程中选择权限
	 * @category						1.(发文)edoc_send_permission_policy   
	 * 									2.(收文)edoc_rec_permission_policy
	 * 						            3.(签报)edoc_qianbao_permission_policy
	 * 						            4.(协同)col_flow_perm_policy
	 * @return
	 */
	public static List<String> getPermOrginalNameByCat(String category, int categoryType)throws Exception{
		
		User user = CurrentUser.get();
		List<String> nameList = new ArrayList<String>();
		FlowPermManager flowPermManager= (FlowPermManager)ApplicationContextHolder.getBean("flowPermManager");
		MetadataManager metadataManager= (MetadataManager)ApplicationContextHolder.getBean("metadataManager");
		List<FlowPerm> list = flowPermManager.getFlowpermsByStatus(category, FlowPerm.Node_isActive, false, categoryType, user.getLoginAccount());
		
		ResourceBundle r = null;
		String label = "";
		String value = "";
		for(FlowPerm perm : list){
				if(perm.getType() == Constants.F_type_system){
					label = metadataManager.getMetadataItemLabel(perm.getCategory(), perm.getName());
					if(null!=label && !"".equals(label)){
					r = ResourceBundle.getBundle("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources",CurrentUser.get().getLocale());
						value = ResourceBundleUtil.getString(r, label);
					}
					nameList.add(value);
				}else if(perm.getType() == Constants.F_type_custom){
					nameList.add(perm.getName());
				}
		}
		return nameList;
	}
	
	
}
