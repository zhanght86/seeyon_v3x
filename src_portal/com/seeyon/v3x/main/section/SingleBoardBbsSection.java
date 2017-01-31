/**
 * 
 */
package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.taglibs.functions.BbsFunction;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-8
 */
public class SingleBoardBbsSection extends BaseSection {
	private static Log log = LogFactory.getLog(SingleBoardBbsSection.class);
	
	private BbsBoardManager bbsBoardManager;
	
	private BbsArticleManager bbsArticleManager;

	public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}
	
	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleBoardBbsSection";
	}
	
	@Override
	public String getBaseName(Map<String, String> preference) {
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		try {
			V3xBbsBoard b = this.bbsBoardManager.getBoardById(boardId);

			if (b != null) {
				return b.getName();
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	@Override
	public String getName(Map<String, String> preference) {	
		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}
		Long boardId = NumberUtils.toLong(preference.get(PropertyName.singleBoardId.name()));
		try{
			V3xBbsBoard b = this.bbsBoardManager.getBoardById(boardId);
			
			if(b != null){
				return b.getName();
			}
		}
		catch(Exception e){
			log.error("", e);
		}
		
		return null;
	}
	
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if (Strings.isBlank(singleBoardId)) {
			return false;
		}

		try {
			V3xBbsBoard type = this.bbsBoardManager.getBoardById(Long.valueOf(singleBoardId));
			return type != null;
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		Pagination.setNeedCount(false);
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		MultiRowFourColumnTemplete mt = new MultiRowFourColumnTemplete();
		List<V3xBbsArticle> v3xBbsArticles = null;
		int count = SectionUtils.getSectionCount(8, preference);
		try {
			v3xBbsArticles = bbsArticleManager.listArticleByBoardId(boardId, count);
		}
		catch (Exception e1) {
			log.error("", e1);
		}
		
		if(v3xBbsArticles != null){
			ResourceBundle bundle = ResourceBundle.getBundle("com.seeyon.v3x.bbs.resources.i18n.BBSResources", CurrentUser.get().getLocale());
			for (V3xBbsArticle article : v3xBbsArticles) {
				ArticleModel model = new ArticleModel(article);
				MultiRowFourColumnTemplete.Row row = mt.addRow();
                row.setSubject(model.getArticleName());
                //单版块显示精华、置顶等标识
                boolean isNarrow = Boolean.valueOf(preference.get(PropertyName.isNarrow.name()));
                int subLength = 38;
                if (isNarrow) {
                	subLength = 24;
                }
                row.setSubjectHTML(SectionUtils.mergeSubject(BbsFunction.showSubject(model, subLength, bundle), -1, false, null, article.isHasAttachments(), null, null));
                row.setAlt(model.getArticleName());
                row.setLink("/bbs.do?method=showPost&articleId="+article.getId()+"&resourceMethod=listLatestFiveArticleAndAllBoard", OPEN_TYPE.href_blank);
                row.setHasAttachments(model.isAttachmentFlag());
                row.setCreateDate(article.getIssueTime());
                
                String creatorName = Functions.showMemberName(model.getIssueUser());
                row.setCreateMemberName( (model.isAnonymousFlag() && model.getIssueUser()!=CurrentUser.get().getId()) ? bundle.getString("anonymous.label") : creatorName);
                //讨论已阅未读未进行持久化处理，去掉css样式区分
                //row.setClassName("ReadDifferFromNotRead");
			}
		}
		mt.addBottomButton("bbs_index_label", "/bbs.do?method=listLatestFiveArticleAndAllBoard&where=space");
		mt.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bbs.do?method=listAllArticle&boardId="+boardId+"&group=&from=section");
		return mt;
	}

}