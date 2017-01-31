package com.seeyon.v3x.publicManager;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;

/**
 * 集团版的部门公告信息管理菜单校验
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2010-12-6
 */
public class DeptMenuCheckImpl implements MenuCheck {
	private SpaceManager spaceManager;
	
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public boolean check(long memberId, long loginAccountId) {
		boolean result = false;
		boolean isGroupVer = (Boolean)(SysFlag.sys_isGroupVer.getFlag());//判断是否为集团版
		if(!isGroupVer){
			return false;
		}
		try {
			Map<SpaceType, List<SpaceModel>> spacePath = this.spaceManager.getAccessSpace(memberId, loginAccountId);
			List<SpaceModel> deptSpaceModels = spacePath.get(SpaceType.department);
			return CollectionUtils.isNotEmpty(deptSpaceModels);
		}
		catch (Exception e) {
		}
		
		return result;
	}

}
