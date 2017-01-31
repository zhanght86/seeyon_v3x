package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.NewsSectionUtil;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 单板块集团新闻栏目
 */
public class SingleBoardGroupNewsSection extends BaseSection {
	private static Log log = LogFactory.getLog(SingleBoardGroupNewsSection.class);

	private NewsTypeManager newsTypeManager;

	private NewsDataManager newsDataManager;

	private FileManager fileManager;

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	public boolean isAllowUsed() {
		return (Boolean) (SysFlag.news_showOtherAccountNews.getFlag());
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleBoardGroupNewsSection";
	}
	
	@Override
	public String getBaseName(Map<String, String> preference) {
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));

		NewsType t = this.newsTypeManager.getById(boardId);
		if (t == null || !t.isUsedFlag()) {
			return null;
		}

		return t.getTypeName();
	}

	@Override
	public String getName(Map<String, String> preference) {
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));

		NewsType t = this.newsTypeManager.getById(boardId);
		if (t == null || !t.isUsedFlag()) {// 删除板块后，不应该显示了
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
			NewsType type = this.newsTypeManager.getById(Long.valueOf(singleBoardId));
			return type != null && type.isUsedFlag();
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		int count = NewsSectionUtil.getSectionCount(preference, columnsStyle);

		User user = CurrentUser.get();
		List<NewsData> newsDatas = null;
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(count);
		try {
			newsDatas = newsDataManager.findByReadUser(user.getId(), boardId);
		} catch (Exception e) {
			log.error("", e);
		}

		return NewsSectionUtil.setNewsSectionData(preference, columnsStyle, null, newsDatas, NewsTypeSpaceType.group.ordinal(), true, newsDataManager, fileManager);
	}
	
}