package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.NewsSectionUtil;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.SpaceManager;

/**
 * 自定义团队空间新闻栏目
 */
public class CustomNewsSection extends BaseSection {

	private static Log log = LogFactory.getLog(CustomNewsSection.class);

	private NewsDataManager newsDataManager;

	private FileManager fileManager;

	private SpaceManager spaceManager;

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
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
		return "customNewsSection";
	}
	
	@Override
	public String getBaseName() {
		return "customNews";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customNews", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		Long boardId = Long.parseLong(preference.get(PropertyName.ownerId.name()));
		int spaceType = 4;
		int count = NewsSectionUtil.getSectionCount(preference, columnsStyle);

		User user = CurrentUser.get();
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(count);
		List<NewsData> newsDatas = null;
		boolean isSpaceManager = false;
		try {
			newsDatas = newsDataManager.findByReadUser4Section(user.getId(), boardId);
			isSpaceManager = spaceManager.isManagerOfThisSpace(user.getId(), boardId);
		} catch (BusinessException e) {
			log.error("", e);
		}

		BaseSectionTemplete t = NewsSectionUtil.setNewsSectionData(preference, columnsStyle, null, newsDatas, spaceType, true, newsDataManager, fileManager);
		if (t.getBottomButtons() != null) {
			t.getBottomButtons().clear();
		}
		if (isSpaceManager) {
			t.addBottomButton("new_news_button", "/newsData.do?method=publishListIndex&newsTypeId=" + boardId + "&spaceType=4&custom=true");
		}
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/newsData.do?method=newsMore&typeId=" + boardId + "&from=top&custom=true");
		return t;
	}

}