package com.seeyon.v3x.bulletin.domain;

import java.util.List;
import com.seeyon.v3x.bulletin.util.Constants;

/**
 * 用户在查看单位公告或集团公告首页时，每个公告板块除了显示最新6条公告之外，还需显示其管理员、审核员（如果该公告板块设定了审核员的话）
 * 同时也需要针对当前用户的权限显示"发布公告"或"板块管理"功能按钮
 * 为此设定一个仅用于前端展现的类，包含公告板块、当前用户是否具备发起权限和管理权限
 */
public class BulTypeModel{
	private boolean canNewOfCurrent;     // 当前用户是否有新建权限
	private boolean canAdminOfCurrent;   // 当前用户是否可以管理	
	private BulType bulType;
	
	/**
	 * @param bulType 公告板块
	 * @param domainIds 当前登录用户的各种组织模型，比如部门ID、所属组的ID、岗位ID等，以便匹配发起权限
	 */
	public BulTypeModel(BulType bulType, List<Long> domainIds){
		this.bulType = bulType;
		this.setProps(domainIds);
	}
	
	private void setProps(List<Long> domainIds){
		//公告板块的发起权限授权对象可以是单位、部门、岗位、职务级别、组等情况，依次进行匹配
		for(BulTypeManagers tm : bulType.getBulTypeManagers()){
			if(domainIds.contains(tm.getManagerId())){
				if(Constants.MANAGER_FALG.equals(tm.getExt1())){
					setCanAdminOfCurrent(true);
					setCanNewOfCurrent(true);
					break;
				} else if(Constants.WRITE_FALG.equals(tm.getExt1()))
					setCanNewOfCurrent(true);
			}
		}
	}

	public BulType getBulType() {
		return bulType;
	}

	public void setBulType(BulType bulType) {
		this.bulType = bulType;
	}

	public boolean getCanAdminOfCurrent() {
		return canAdminOfCurrent;
	}

	public void setCanAdminOfCurrent(boolean canAdminOfCurrent) {
		this.canAdminOfCurrent = canAdminOfCurrent;
	}

	public boolean getCanNewOfCurrent() {
		return canNewOfCurrent;
	}

	public void setCanNewOfCurrent(boolean canNewOfCurrent) {
		this.canNewOfCurrent = canNewOfCurrent;
	}
}
