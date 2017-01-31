package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

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
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.CommonTools;

/**
 * 单位焦点新闻栏目
 */
public class FocusNewsSection extends BaseSection {

	private static final Log log = LogFactory.getLog(FocusNewsSection.class);

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
		return "focusNewsSection";
	}
	
	@Override
	public String getBaseName() {
		return "focusNews";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("focusNews", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String panel = SectionUtils.getPanel("all", preference);
		String panelValue = null;
		String columnsStyle = SectionUtils.getColumnStyle("imageandlist", preference);
		int count = NewsSectionUtil.getSectionCount(preference, columnsStyle);

		User user = CurrentUser.get();
		List<NewsData> newsDatas = null;
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(count);
		try {
			if ("designated".equals(panel)) {
				panelValue = panel + "_value";
				String designated = preference.get(panelValue);
				List<Long> typeList = CommonTools.parseStr2Ids(designated);
				newsDatas = newsDataManager.findByReadUser4ImageNews(user, Constants.FocusNews, NewsTypeSpaceType.corporation.ordinal(), typeList);
			} else {
				newsDatas = newsDataManager.findByReadUser4ImageNews(user, Constants.FocusNews, NewsTypeSpaceType.corporation.ordinal(), null);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		BaseSectionTemplete t = NewsSectionUtil.setNewsSectionData(preference, columnsStyle, null, newsDatas, NewsTypeSpaceType.corporation.ordinal(), false, newsDataManager, fileManager);
		if (t.getBottomButtons() != null) {
			t.getBottomButtons().clear();
		}
		String moreLink = "";
		moreLink = "/newsData.do?method=imageNewsMore&imageOrFocus=1&spaceType=1" + "&fragmentId=" + preference.get(PropertyName.entityId.name()) + "&ordinal=" + preference.get(PropertyName.ordinal.name());
		if (panelValue != null) {
			moreLink += "&panelValue=" + panelValue;
		}
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, moreLink);
		return t;
	}
	
}