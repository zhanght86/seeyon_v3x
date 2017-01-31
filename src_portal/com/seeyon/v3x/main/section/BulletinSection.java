package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.util.Constants.BulTypeSpaceType;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.BulletinSectionUtil;
import com.seeyon.v3x.util.CommonTools;

/**
 * 单位最新公告栏目
 */
public class BulletinSection extends BaseSection {

	private static Log log = LogFactory.getLog(BulletinSection.class);

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
		return "bulletinSection";
	}
	
	@Override
	public String getBaseName() {
		return "bulletin";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("bulletin", preference);
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
				bulDatas = bulDataManager.findByReadUserForIndex(user, count, typeList, BulTypeSpaceType.corporation, null);
			} else {
				bulDatas = bulDataManager.findByReadUserForIndex(user, count, null,BulTypeSpaceType.corporation,null);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return BulletinSectionUtil.setBulSectionData(preference, panelValue, bulDatas, BulTypeSpaceType.corporation.ordinal(), false, bulDataManager);
	}

}