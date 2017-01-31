package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.BbsFunction;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

/**
 * 单板块集团讨论栏目
 * 
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 * 
 */
public class SingleBoardGroupBbsSection extends BaseSection {
	
	private static Log log = LogFactory.getLog(SingleBoardGroupBbsSection.class);

	private BbsArticleManager bbsArticleManager;

	private BbsBoardManager bbsBoardManager;

	public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	@Override
	public boolean isAllowUsed() {
		return (Boolean) (SysFlag.bbs_showOtherAccountBbs.getFlag());
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleBoardGroupBbsSection";
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
	protected String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}
		
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
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

    @Override
    protected BaseSectionTemplete projection(Map<String, String> preference) {
        Long singleBoardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
        List<V3xBbsArticle> v3xBbsArticles = null;
        int count = SectionUtils.getSectionCount(8, preference);
        try {
            v3xBbsArticles = bbsArticleManager.listArticleByBoardId(singleBoardId, count);
        }
        catch (Exception e1) {
            log.error("", e1);
        }
        
        MultiRowFourColumnTemplete c = new MultiRowFourColumnTemplete();
        
        if(v3xBbsArticles != null && !v3xBbsArticles.isEmpty()){
            ResourceBundle bundle = ResourceBundle.getBundle(BbsConstants.BBS_I18N_RESOURCE, CurrentUser.get().getLocale());
            for (V3xBbsArticle article : v3xBbsArticles) {
                ArticleModel model = new ArticleModel(article);
                
                MultiRowFourColumnTemplete.Row row = c.addRow();
                row.setSubjectHTML(SectionUtils.mergeSubject(BbsFunction.showSubject(model, 38, bundle), -1, false, null, article.isHasAttachments(), null, null));
                row.setLink("/bbs.do?method=showPost&articleId="+article.getId()+"&resourceMethod=listLatestFiveArticleAndAllBoard&group=true", OPEN_TYPE.href_blank);
                //匿名讨论主题发起者的姓名对且只对发起者本人显示
                if(article.getAnonymousFlag() && article.getIssueUserId()!=CurrentUser.get().getId())
					row.setCreateMemberName(ResourceBundleUtil.getString(BbsConstants.BBS_I18N_RESOURCE, "anonymous.label"));
				else
					row.setCreateMemberName(BbsFunction.showName(model, bundle));
                
                row.setCreateDate(article.getIssueTime());
            }
        }
        
        //讨论首页 和 更多连接
        c.addBottomButton("bbs_index_label", "/bbs.do?method=listLatestFiveArticleAndAllBoard&group=true&where=space");
        c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bbs.do?method=listAllArticle&group=true&from=section&boardId=" + singleBoardId + "&from=top");

        return c;
    }

}