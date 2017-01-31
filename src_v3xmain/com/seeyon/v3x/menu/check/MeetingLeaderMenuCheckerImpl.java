package com.seeyon.v3x.menu.check;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
/**
 * 
 * @author wangwei
 *  验证当前用户是否有领导查阅权的权限
 *  包括组织、部门、单位、个人的验证
 */
public  class MeetingLeaderMenuCheckerImpl implements MenuCheck  {
	
	private final static Log log = LogFactory.getLog(MeetingLeaderMenuCheckerImpl.class);

	private OrgManager orgManager;
	private ConfigManager configManager;
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	/**
	 * 重新父类的方法
	 */
	@Override
	public boolean check(long memberId, long loginAccountId) {

		if(!((Boolean)Functions.getSysFlag("is_gov_only")))
			return false;
		ConfigItem leadership= configManager.getConfigItem("v3x_meeting_create_acc", "v3x_meeting_create_acc_leadership", loginAccountId);
		String  meetingReview = "";
		if(leadership == null){
			return false;
		}else{
			 meetingReview = leadership.getExtConfigValue();
		}
		if(meetingReview.contains(String.valueOf(memberId))){
			return true;
		}
		//单位与部门之间的权限判断
		List<Long> myIds;
		try {
			myIds = orgManager.getUserDomainIDs(memberId, loginAccountId, V3xOrgEntity.ORGENT_TYPE_ACCOUNT,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,V3xOrgEntity.ORGENT_TYPE_LEVEL,V3xOrgEntity.ORGENT_TYPE_POST);
			if(myIds == null)
				myIds = new ArrayList<Long>(1);
			myIds.add(memberId);
			for(Long objId : myIds) {
				if(meetingReview.indexOf(objId.toString())>=0) {
					return true;
				}
			}
		} catch (BusinessException e) {
			log.error("判断领导查阅权限错误", e);
		}
		return false;

	}
}
