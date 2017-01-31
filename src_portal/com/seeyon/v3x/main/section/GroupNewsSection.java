package com.seeyon.v3x.main.section;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.NewsSectionUtil;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.util.Constants.NewsTypeSpaceType;
import com.seeyon.v3x.util.CommonTools;

/**
 * 集团最新新闻栏目
 */
public class GroupNewsSection extends BaseSection {	
	
    private static final Log log = LogFactory.getLog(GroupNewsSection.class);    
    
    private NewsDataManager newsDataManager;

	private FileManager fileManager;

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	@Override
    public boolean isAllowUsed() {
        return (Boolean)(SysFlag.news_showOtherAccountNews.getFlag());
    }
	
	@Override
	public String getIcon() {
		return null;
	}
    
	@Override
	public String getId() {
		return "groupNewsSection";
	}
	
	@Override
	public String getBaseName() {
		if ((Boolean) Functions.getSysFlag("sys_isGovVer")) {
			// 政务多组织版
			return "groupNews_GOV";
		} else {
			return "groupNews";
		}
	}

	public String getName(Map<String, String> preference) {
		if ((Boolean) Functions.getSysFlag("sys_isGovVer")) {
			// 政务多组织版
			return SectionUtils.getSectionName("groupNews_GOV", preference);
		} else {
			return SectionUtils.getSectionName("groupNews", preference);
		}
	}

	
	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		String panel = SectionUtils.getPanel("all", preference);
		String panelValue = null;
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
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
				newsDatas = newsDataManager.groupFindByReadUserForIndex(user.getId(),user.isInternal(), typeList);
			} else {
				newsDatas = newsDataManager.groupFindByReadUserForIndex(user.getId(),user.isInternal());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		
		return NewsSectionUtil.setNewsSectionData(preference, columnsStyle, panelValue, newsDatas, NewsTypeSpaceType.group.ordinal(), false, newsDataManager, fileManager);
	}

}