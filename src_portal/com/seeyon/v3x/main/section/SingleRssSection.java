package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.common.rss.manager.RssManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

public class SingleRssSection extends BaseSection {

	private static final Log log = LogFactory.getLog(SingleRssSection.class);

	private RssManager rssManager;

	public void setRssManager(RssManager rssManager) {
		this.rssManager = rssManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleBoardRssSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		String idStr = preference.get(PropertyName.singleBoardId.name());
		if (Strings.isNotBlank(idStr)) {
			User user = CurrentUser.get();
			RssCategory rssCategory = new RssCategory();
			try {
				rssCategory = rssManager.getRssCategory(Long.parseLong(idStr));
				List<RssCategory> categories = rssManager.getMyCategories(user.getId());// 得到用户订阅的Rss频道
				if (categories != null && !categories.isEmpty() && categories.contains(rssCategory)) {
					return rssCategory.getName();
				} else {
					return null;
				}
			} catch (Exception e) {
				log.error("获取系统RSS分类订阅是报错：", e);
			}
		}
		return null;
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		List<RssChannelItems> list = null;
		User user = CurrentUser.get();
		String idStr = preference.get(PropertyName.singleBoardId.name());
		if (Strings.isNotBlank(idStr)) {
			list = this.rssManager.getRssChannelItems(Long.parseLong(idStr), user.getId());
		}
		MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(8);
		int nowRow = 0;
		if (list != null && list.size() > 0) {
			for (RssChannelItems cat : list) {
				if (nowRow < 8) {
					MultiRowVariableColumnTemplete.Row row = t.addRow();
					MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
					String title = cat.getTitle();
					subjectCell.setCellContent(title.trim());
					subjectCell.setCellWidth(70);
					subjectCell.setLinkURL("javascript:openRSSURL('" + cat.getLink() + "','" + cat.getId() + "','" + cat.getCategoryChannelId() + "')");
					MultiRowVariableColumnTemplete.Cell dateCell = row.addCell();
					dateCell.setCellWidth(30);
					if (cat.getPubDate() != null) {
						dateCell.setCellContent(SectionUtils.toDatetime(cat.getPubDate(), 4));
					} else {
						dateCell.setCellContent(" ");
					}
					nowRow++;
				}
			}
		}
		
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/rssManager.do?method=rssIndex&status=0");
		return t;
	}

}