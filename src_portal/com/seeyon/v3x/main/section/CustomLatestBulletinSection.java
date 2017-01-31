package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.BulletinSectionUtil;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

/**
 * 自定义单位/集团空间最新公告栏目
 */
public class CustomLatestBulletinSection extends BaseSection {

	private static Log log = LogFactory.getLog(CustomLatestBulletinSection.class);

	private BulDataManager bulDataManager;

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "customLatestBulletinSection";
	}
	
	@Override
	public String getBaseName() {
		return "customLatestBulletin";
	}

	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customLatestBulletin", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		String spaceId = preference.get(PropertyName.ownerId.name());
		String spaceTypeStr = preference.get(PropertyName.spaceType.name());
		spaceTypeStr = "public_custom".equalsIgnoreCase(spaceTypeStr) || "5".equalsIgnoreCase(spaceTypeStr) ? "5" : "6";
		int spaceType = NumberUtils.toInt(spaceTypeStr);
		int count = BulletinSectionUtil.getSectionCount(preference);

		User user = CurrentUser.get();
		List<BulData> bulDatas = null;
		try {
			bulDatas = bulDataManager.findCustomByReadUserForIndex(user, NumberUtils.toLong(spaceId), spaceType, count);
		} catch (Exception e) {
			log.error("", e);
		}

		return BulletinSectionUtil.setBulSectionData(preference, null, bulDatas, spaceType, false, bulDataManager);
	}

}