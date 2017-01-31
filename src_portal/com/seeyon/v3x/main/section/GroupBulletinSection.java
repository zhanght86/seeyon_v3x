package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.BulletinSectionUtil;
import com.seeyon.v3x.util.CommonTools;

/**
 * 集团最新公告栏目
 */
public class GroupBulletinSection extends BaseSection {

	private static final Log log = LogFactory.getLog(GroupBulletinSection.class);
	
	private BulDataManager bulDataManager;

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	@Override
	public boolean isAllowUsed() {
		return (Boolean) (SysFlag.bul_showOtherAccountBulletin.getFlag());
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "groupBulletinSection";
	}
	
	@Override
	public String getBaseName() {
		if ((Boolean) Functions.getSysFlag("sys_isGovVer")) {
			// 政务多组织版
			return "groupBulletin_GOV";
		} else {
			return "groupBulletin";
		}
	}

	public String getName(Map<String, String> preference) {
		if ((Boolean) Functions.getSysFlag("sys_isGovVer")) {
			// 政务多组织版
			return SectionUtils.getSectionName("groupBulletin_GOV", preference);
		} else {
			return SectionUtils.getSectionName("groupBulletin", preference);
		}
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		String panel = SectionUtils.getPanel("all", preference);
		String panelValue = null;
		int count = BulletinSectionUtil.getSectionCount(preference);

		User user = CurrentUser.get();
		List<BulData> bulDatas = null;
		try {
			if ("designated".equals(panel)) {
				panelValue = panel + "_value";
				String designated = preference.get(panelValue);
				List<Long> typeList = CommonTools.parseStr2Ids(designated);
				bulDatas = bulDataManager.findByReadUserForIndex(user, count, typeList, BulTypeSpaceType.group, null);
			} else {
				bulDatas = bulDataManager.findByReadUserForIndex(user, count, null, BulTypeSpaceType.group, null);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return BulletinSectionUtil.setBulSectionData(preference, panelValue, bulDatas, BulTypeSpaceType.group.ordinal(), false, bulDataManager);
	}

}