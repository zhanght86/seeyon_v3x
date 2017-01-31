package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.BulletinSectionUtil;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 单板块自定义单位/集团公告栏目
 */
public class SingleCustomBoardBulSection extends BaseSection {

	private static Log log = LogFactory.getLog(SingleCustomBoardBulSection.class);

	private BulDataManager bulDataManager;

	private BulTypeManager bulTypeManager;

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleCustomBoardBulSection";
	}
	
	@Override
	public String getBaseName(Map<String, String> preference) {
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));

		BulType t = this.bulTypeManager.getById(boardId);
		if (t == null || !t.isUsedFlag()) {
			return null;
		}

		return t.getTypeName();
	}

	@Override
	public String getName(Map<String, String> preference) {
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));

		BulType t = this.bulTypeManager.getById(boardId);
		if (t == null || !t.isUsedFlag()) {// 删除模块后，不应该显示了
			return null;
		}

		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}

		return t.getTypeName();
	}
	
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if (Strings.isBlank(singleBoardId)) {
			return false;
		}

		try {
			BulType type = this.bulTypeManager.getById(Long.valueOf(singleBoardId));
			return type != null && type.isUsedFlag();
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		String spaceTypeStr = preference.get(PropertyName.spaceType.name());
		spaceTypeStr = "public_custom".equalsIgnoreCase(spaceTypeStr) || "5".equalsIgnoreCase(spaceTypeStr) ? "5" : "6";
		int spaceType = NumberUtils.toInt(spaceTypeStr);
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		int count = BulletinSectionUtil.getSectionCount(preference);
		
		User user = CurrentUser.get();
		List<BulData> bulDatas = null;
		try {
			bulDatas = bulDataManager.findByReadUser4Section(user, boardId, count);
		} catch (BusinessException e) {
			log.error("", e);
		}

		return BulletinSectionUtil.setBulSectionData(preference, null, bulDatas, spaceType, true, bulDataManager);
	}

}