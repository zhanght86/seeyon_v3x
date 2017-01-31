package com.seeyon.v3x.common.manager;

import java.util.List;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 普通用户权限管理类
 * @author tanggl
 *
 */
public class ConfigGrantManagerImpl implements ConfigGrantManager {

	private OrgManager orgManager;
	private ConfigManager configManager;
	
	/**
	 * 判断某人是不是有某些权限
	 * branches_a8_v350_r_gov GOV-3001  唐桂林修改个人空间-已办事项会议链接
	 * @param accountId
	 * @param userId
	 * @param infoGrantType
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean hasConfigGrant(Long accountId, Long userId, String configGrant, String grantType) {
		try {
			if(accountId == null){
				accountId = CurrentUser.get().getLoginAccount();
			}
			String sendEntitys = "";
			ConfigItem item = configManager.getConfigItem(configGrant, grantType, accountId);		
			if(item == null) {
				return false;
			}
			sendEntitys=item.getExtConfigValue();
			if(sendEntitys == null){
				return false;
			}
			boolean yes = false;
			V3xOrgMember v3xOrgMember = orgManager.getMemberById(userId);
			//不是保存在人员里
			if(sendEntitys.indexOf("Member|"+v3xOrgMember.getId())<0) {
				yes = true;
			}
			if(yes) {//不是保存在单位里
				if(sendEntitys.indexOf("Account|"+v3xOrgMember.getOrgAccountId())<0) {
					yes = true;
				} else {
					yes = false;
				}
			}
			if(yes) {
				//不是保存在部门里
				if(sendEntitys.indexOf("Department|"+v3xOrgMember.getOrgDepartmentId())<0) {
					yes = true;
				} else {
					yes = false;
				}
			}
			if(yes) {
				//不是保存在岗位里
				if(sendEntitys.indexOf("Post|"+v3xOrgMember.getOrgPostId())<0) {
					yes = true;
				} else {
					yes = false;
				}
			}
			if(yes) {
				//不是保存在级别中
				if(sendEntitys.indexOf("Level|"+v3xOrgMember.getOrgLevelId())<0) {
					yes = true;
				} else {
					yes = false;
				}
			}
			if(yes) {//组单独进行处理
				List<Long> teams = orgManager.getUserDomainIDs(userId,accountId,V3xOrgEntity.ORGENT_TYPE_TEAM);
				for(Long tid : teams) {
				    if(sendEntitys.indexOf("Team|"+tid.toString())>=0){
						V3xOrgTeam v3xOrgTeam = (V3xOrgTeam)orgManager.getEntity(V3xOrgTeam.class, tid);
						if(v3xOrgTeam != null){
							List<Long> v3xOrgMembers=v3xOrgTeam.getAllMembers();
							if(v3xOrgMembers.contains(userId)) {
								yes = false;
								break;
							}
						}
				    }
				}
			}
			return !yes;
		} catch(Exception e) {
			return false;
		}
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
}
