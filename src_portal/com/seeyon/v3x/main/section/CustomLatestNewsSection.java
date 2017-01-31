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
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

/**
 * 自定义单位/集团空间最新新闻栏目  
 */
public class CustomLatestNewsSection extends BaseSection {

	private static final Log log = LogFactory.getLog(CustomLatestNewsSection.class);

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
		return "customLatestNewsSection";
	}
	
	@Override
	public String getBaseName() {
		return "customLatestNews";
	}

	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customLatestNews", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		String spaceId = preference.get(PropertyName.ownerId.name());
		String spaceTypeS = preference.get(PropertyName.spaceType.name());
		spaceTypeS = "public_custom".equalsIgnoreCase(spaceTypeS) ? "5" : "6";
		int spaceType = NumberUtils.toInt(spaceTypeS);
		int count = NewsSectionUtil.getSectionCount(preference, columnsStyle);

		User user = CurrentUser.get();
		Pagination.setNeedCount(false);
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(count);
		List<NewsData> newsDatas = null;
		try {
			newsDatas = newsDataManager.findCustomByReadUserForIndex(user.getId(), NumberUtils.toLong(spaceId), spaceType, user.isInternal());
		} catch (Exception e) {
			log.error("", e);
		}

		return NewsSectionUtil.setNewsSectionData(preference, columnsStyle, null, newsDatas, spaceType, false, newsDataManager, fileManager);
	}

}