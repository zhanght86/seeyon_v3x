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

/**
 * 单位最新讨论栏目
 */
public class BbsSection extends BaseSection {
	
	private static Log log = LogFactory.getLog(BbsSection.class);
	
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
		return "bbsSection";
	}
	
	@Override
	public String getBaseName() {
		return "bbs";
	}

	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("bbs", preference);
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
		MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();
		Pagination.setNeedCount(false);
		int count = SectionUtils.getSectionCount(8, preference);
		List<V3xBbsArticle> v3xBbsArticles= bbsArticleManager.queryArticleList(count, null, null, null);

		if(CollectionUtils.isNotEmpty(v3xBbsArticles)){
			for (V3xBbsArticle v3xBbsArticle : v3xBbsArticles) {
				ArticleModel model = new ArticleModel(v3xBbsArticle);
				
				Long boardId = v3xBbsArticle.getBoardId();
				V3xBbsBoard v3xBbsBoard = null;
				try {
					v3xBbsBoard = bbsBoardManager.getBoardById(boardId);
				} catch (Exception e) {
					log.error("单位空间-讨论栏目读取板块ID异常：", e);
				}
				
				model.setBoard(v3xBbsBoard);
				MultiRowThreeColumnTemplete.Row row = c.addRow();
				row.setSubject(model.getArticleName());
				row.setHasAttachments(model.isAttachmentFlag());
				row.setLink("/bbs.do?method=showPost&articleId="+v3xBbsArticle.getId()+"&resourceMethod=listLatestFiveArticleAndAllBoard", OPEN_TYPE.href_blank);
				row.setAlt(v3xBbsArticle.getArticleName());
				row.setCreateDate(v3xBbsArticle.getIssueTime());			
				row.setCategory(v3xBbsBoard.getName(), "/bbs.do?method=listAllArticle&boardId="+boardId+"&group=&from=section");
			}
		}
		c.addBottomButton("bbs_index_label", "/bbs.do?method=listLatestFiveArticleAndAllBoard&where=space");
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bbs.do?method=listAllArticle&group=&from=section");

		return c;
	}

}
