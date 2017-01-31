package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

public class CustomLatestBbsSection extends BaseSection {
	
	private static Log log = LogFactory.getLog(CustomLatestBbsSection.class);	
	private BbsArticleManager bbsArticleManager;
	private BbsBoardManager bbsBoardManager;

	public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}
	
	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	@Override
	public String getId() {
		return "customLatestBbsSection";
	}
	
	@Override
	public String getBaseName() {
		return "customLatestBbs";
	}

	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customLatestBbs", preference);
	}

	@Override
	public String getIcon() {
		return null;
	}
	
	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		String spaceType = preference.get(PropertyName.spaceType.name());
		String spaceId = preference.get(PropertyName.ownerId.name());
		MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();
		Pagination.setNeedCount(false);
		int count = SectionUtils.getSectionCount(8, preference);
		spaceType = "public_custom".equalsIgnoreCase(spaceType) ? "5" : "6";
		List<V3xBbsArticle> v3xBbsArticles = bbsArticleManager.queryCustomArticleList(Long.parseLong(spaceId), Integer.parseInt(spaceType), count, null, null, null);
		if(CollectionUtils.isNotEmpty(v3xBbsArticles)){
			for (V3xBbsArticle v3xBbsArticle : v3xBbsArticles) {
				ArticleModel model = new ArticleModel(v3xBbsArticle);
				
				Long boardId = v3xBbsArticle.getBoardId();
				V3xBbsBoard v3xBbsBoard = null;
				try {
					v3xBbsBoard = bbsBoardManager.getBoardById(boardId);
				} catch (Exception e) {
					log.error("讨论栏目读取板块ID异常：", e);
				}
				
				model.setBoard(v3xBbsBoard);
				MultiRowThreeColumnTemplete.Row row = c.addRow();
				row.setSubject(model.getArticleName());
				row.setHasAttachments(model.isAttachmentFlag());
				row.setLink("/bbs.do?method=showPost&spaceId=" + spaceId + "&articleId="+v3xBbsArticle.getId()+"&resourceMethod=listLatestFiveArticleAndAllBoard", OPEN_TYPE.href_blank);
				row.setAlt(v3xBbsArticle.getArticleName());
				row.setCreateDate(v3xBbsArticle.getIssueTime());			
				row.setCategory(v3xBbsBoard.getName(), "/bbs.do?method=listAllArticle&boardId="+boardId+"&group=&from=section&spaceId=" + spaceId);
			}
		}
		c.addBottomButton("bbs_index_label", "/bbs.do?method=listLatestFiveArticleAndAllBoard&where=space&spaceType=" + spaceType + "&spaceId=" + spaceId);
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bbs.do?method=listAllArticle&group=&from=section&spaceType=" + spaceType + "&spaceId=" + spaceId);

		return c;
	}

}
