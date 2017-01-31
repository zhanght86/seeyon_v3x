package com.seeyon.v3x.menu.check;

import java.util.List;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.menu.manager.MenuTreeCheck;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.manager.OrgManager;

public class InfoCheckMenuCheckerImpl implements MenuTreeCheck {

	/** 信息管理员 */
	public final static String ACCOUNT_INFO_ADMIN = "AccountInfoAdmin";

	/** 信息权限 */
	public final static String INFO_CONFIG_GRANT = "info_config_grant";
	
	/** 信息考核 */
	public final static String INFO_CONFIG_GRANT_CHECK = "info_config_grant_check";
	
	private OrgManager orgManager;
	
	private ConfigManager configManager;

	@Override
	public boolean check() {
		boolean hasInfoPlugin = (Boolean)SysFlag.is_gov_only.getFlag() && SystemEnvironment.hasPlugin("govInfoPlugin");
		if(hasInfoPlugin) {			
			try {
				return hasInfoGrant(CurrentUser.get().getAccountId(), CurrentUser.get().getId(), INFO_CONFIG_GRANT_CHECK);
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * 判断某人是不是有信息的某些权限
	 * @param accountId
	 * @param userId
	 * @param infoGrantType
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean hasInfoGrant(Long accountId, Long userId, String infoGrantType) {
		try {
			if(accountId == null){
				accountId = CurrentUser.get().getLoginAccount();
			}
			String sendEntitys = "";
			ConfigItem item = configManager.getConfigItem(INFO_CONFIG_GRANT, infoGrantType, accountId);	
			if(item == null) {
				return false;
			}
			sendEntitys=item.getExtConfigValue();
			if(sendEntitys == null){
				return false;
			}
			V3xOrgMember v3xOrgMember = null;
			boolean yes = false;
			v3xOrgMember = orgManager.getMemberById(userId);
			//不是保存在人员里
			if(sendEntitys.indexOf("Member|"+v3xOrgMember.getId())<0) {
				yes = true;
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
