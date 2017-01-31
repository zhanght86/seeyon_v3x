package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.NewsSectionUtil;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.util.Constants;
import com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

/**
 * 自定义团队/单位/集团空间焦点新闻栏目  
 */
public class CustomFocusNewsSection extends BaseSection {

	private static final Log log = LogFactory.getLog(CustomFocusNewsSection.class);

	private NewsDataManager newsDataManager;

	private FileManager fileManager;

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "customFocusNewsSection";
	}
	
	@Override
	public String getBaseName() {
		return "customFocusNews";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customFocusNews", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("imageandlist", preference);
		int count = NewsSectionUtil.getSectionCount(preference, columnsStyle);
		String spaceTypeS = preference.get(PortletEntityProperty.PropertyName.spaceType.name());
		String spaceId = preference.get(PropertyName.ownerId.name());
		int spaceType = NewsTypeSpaceType.custom.ordinal();

		User user = CurrentUser.get();
		List<NewsData> newsDatas = null;
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(count);
		try {
			if ("custom".equals(spaceTypeS)) {
				spaceType = NewsTypeSpaceType.custom.ordinal();
			} else if ("public_custom".equals(spaceTypeS)) {
				spaceType = NewsTypeSpaceType.public_custom.ordinal();
			} else {
				spaceType = NewsTypeSpaceType.public_custom_group.ordinal();
			}
			newsDatas = newsDataManager.findByReadUser4ImageNews(user.getId(), NumberUtils.toLong(spaceId), user.isInternal(), Constants.FocusNews, spaceType);
		} catch (Exception e) {
			log.error("", e);
		}

		BaseSectionTemplete t = NewsSectionUtil.setNewsSectionData(preference, columnsStyle, null, newsDatas, spaceType, false, newsDataManager, fileManager);
		if (t.getBottomButtons() != null) {
			t.getBottomButtons().clear();
		}
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/newsData.do?method=imageNewsMore&imageOrFocus=1&spaceType=" + spaceType + "&spaceId=" + spaceId);
		return t;
	}

}