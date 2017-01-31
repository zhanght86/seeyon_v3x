package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.util.Strings;

/**
 * 集团空间讨论
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class GroupBbsSection extends BaseSection {
    private static final Log log = LogFactory.getLog(GroupBbsSection.class);
    
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
        return (Boolean)(SysFlag.bbs_showOtherAccountBbs.getFlag());
    }
    
	@Override
	public String getId() {
		return "groupBbsSection";
	}
	
	@Override
	public String getBaseName() {
        //政务多组织版
        if((Boolean)Functions.getSysFlag("sys_isGovVer")){            
            return "groupBbs_GOV";
        } else {
        	return "groupBbs";
        }
	}

	public String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}
        String sectionName = "groupBbs";
        //政务多组织版
        if((Boolean)Functions.getSysFlag("sys_isGovVer")){            
            sectionName = "groupBbs_GOV";
        }
        return sectionName;
	}

	@Override
	public String getIcon() {
        return null; //"/apps_res/v3xmain/images/section/groupSectionTitle.gif";
	}
	
	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		MultiRowThreeColumnTemplete c = new MultiRowThreeColumnTemplete();
		Pagination.setNeedCount(false);
		int count = SectionUtils.getSectionCount(8, preference);
		List<V3xBbsArticle> v3xBbsArticles = bbsArticleManager.queryGroupArticleList(count);
		if(v3xBbsArticles != null && !v3xBbsArticles.isEmpty()){
    		for (V3xBbsArticle article : v3xBbsArticles) {
    			ArticleModel model = new ArticleModel(article);
    			Long boardId = article.getBoardId();
    			V3xBbsBoard v3xBbsBoard = null;
    			try {
    				v3xBbsBoard = bbsBoardManager.getBoardById(boardId);
    			} catch (Exception e) {
    				log.error("集团空间讨论-查询板块异常：", e);
    			}
    			
    			MultiRowThreeColumnTemplete.Row row = c.addRow();
    			row.setSubject(model.getArticleName());
    			row.setHasAttachments(model.isAttachmentFlag());
    			row.setLink("/bbs.do?method=showPost&articleId="+article.getId()+"&resourceMethod=listLatestFiveArticleAndAllBoard&group=true", OPEN_TYPE.href_blank);
				row.setAlt(article.getArticleName());
    			row.setCreateDate(article.getIssueTime());
    			row.setCategory(v3xBbsBoard.getName(), "/bbs.do?method=listAllArticle&boardId="+boardId+"&group=true&from=section");
            }
        }
		
        //讨论首页 和 更多连接
		c.addBottomButton("bbs_index_label", "/bbs.do?method=listLatestFiveArticleAndAllBoard&group=true&where=space");
		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bbs.do?method=listAllArticle&group=true&from=section");
		return c;
	}
	
}

