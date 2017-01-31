package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.common.rss.manager.RssManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;

/**
 * Rss栏目
 * 
 * @author ruanxm
 * @version 1.0 2008-8-04
 */
public class RssSection extends BaseSection {

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
		return "rssSection";
	}

	@Override
	public String getBaseName() {
		return "rssSection";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("rssSection", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();
		User user = CurrentUser.get();

		Pagination.setNeedCount(false); // 不需要分页
		Pagination.setFirstResult(0);
		Pagination.setMaxResults(SectionUtils.getSectionCount(8, preference));
		List<RssCategoryChannel> channels = null;
		channels = rssManager.getMySubscriptions(user.getId());
		int i = 0;

		if (channels != null && channels.size() != 0) {
			for (RssCategoryChannel model : channels) {
				List<RssChannelItems> list = null;
				list = rssManager.getSubscribeInfo(model.getId());
				if (list != null && list.size() > 0) {
					for (RssChannelItems cat : list) {
						if (i < 8) {
							MultiRowVariableColumnTemplete.Row row = t.addRow();

							// 第一列：标题
							MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
							String title = cat.getTitle();
							subjectCell.setCellContent(title.trim());
							subjectCell.setCellWidth(70);
							subjectCell.setLinkURL("javascript:openRSSURL('" + cat.getLink() + "','" + cat.getId() + "','" + cat.getCategoryChannelId() + "')");

							// 第二列：日期
							MultiRowVariableColumnTemplete.Cell dateCell = row.addCell();
							dateCell.setCellWidth(30);
							if (cat.getPubDate() != null) {
								dateCell.setCellContent(SectionUtils.toDatetime(cat.getPubDate(), 4));
							} else {
								dateCell.setCellContent(" ");
							}
							i++;
						}
					}
				}
			}
		}

		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/rssManager.do?method=rssIndex&status=0");
		return t;
	}

}