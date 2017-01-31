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
import com.seeyon.v3x.space.manager.SpaceManager;

/**
 * 自定义团队空间公告栏目
 */
public class CustomBulletinSection extends BaseSection {

	private static Log log = LogFactory.getLog(CustomBulletinSection.class);

	private BulDataManager bulDataManager;

	private SpaceManager spaceManager;

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "customBulletinSection";
	}
	
	@Override
	public String getBaseName() {
		return "customBulletin";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customBulletin", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		int spaceType = 4;
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.ownerId.name()));
		int count = BulletinSectionUtil.getSectionCount(preference);

		User user = CurrentUser.get();
		List<BulData> bulDatas = null;
		boolean isSpaceBulManager = false;
		try {
			bulDatas = bulDataManager.spaceFindByReadUserForIndex(boardId, user, spaceType, count);
			isSpaceBulManager = spaceManager.isManagerOfThisSpace(user.getId(), boardId);
		} catch (Exception e) {
			log.error("", e);
		}
		
		BaseSectionTemplete t = BulletinSectionUtil.setBulSectionData(preference, null, bulDatas, spaceType, true, bulDataManager);
		if (t.getBottomButtons() != null) {
			t.getBottomButtons().clear();
		}
		if (isSpaceBulManager) {
			t.addBottomButton("new_bull_alt", "/bulData.do?method=publishListIndex&spaceType=" + spaceType + "&bulTypeId=" + boardId + "&spaceId=" + boardId);
		}
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bulData.do?method=bulMore&spaceType=" + spaceType + "&typeId=" + boardId + "&from=top" + "&custom=true");
		return t;
	}
	
}