/**
 * 
 */
package com.seeyon.v3x.main.section;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.common.taglibs.functions.BbsFunction;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author Macx
 *
 */
public class CustomBbsSection extends BaseSection {
	private static Log log = LogFactory.getLog(CustomBbsSection.class);
	private String newBBs = "new_bbs_button";
	
	private final int[] width = {70,16,14}; //宽度百分比 
	
	private BbsArticleManager bbsArticleManager;

    public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
        this.bbsArticleManager = bbsArticleManager;
    }

	@Override
	public String getId() {
		return "customBbsSection";
	}
	
	@Override
	public String getBaseName() {
		return "customBbs";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customBbs", preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
        Long spaceId = null;
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        if(ownerId != null){
        	spaceId = Long.parseLong(ownerId);
        }
        int sectionCount = SectionUtils.getSectionCount(8, preference);
        MultiRowVariableColumnTemplete t = new MultiRowVariableColumnTemplete();        
    
        List<V3xBbsArticle> v3xBbsArticles = null;
		try {
			//改用此方法匹配发布范围
			v3xBbsArticles = bbsArticleManager.listArticleByBoardId(spaceId, sectionCount);
		} catch (Exception e) {
			log.error("", e);
		}
        
        if(v3xBbsArticles != null && !v3xBbsArticles.isEmpty())  {
        	ResourceBundle bundle = ResourceBundle.getBundle("com.seeyon.v3x.bbs.resources.i18n.BBSResources", CurrentUser.get().getLocale());
            for (V3xBbsArticle v3xBbsArticle : v3xBbsArticles) {
            	ArticleModel model = new ArticleModel(v3xBbsArticle);
                MultiRowVariableColumnTemplete.Row row = t.addRow();
    
                // 单元格1: 讨论标题+附件图标　
                MultiRowVariableColumnTemplete.Cell subjectCell = row.addCell();
                subjectCell.setCellContentHTML(BbsFunction.showSubject(model, 36, bundle));
                subjectCell.setAlt(v3xBbsArticle.getArticleName());
                subjectCell.setCellWidth(width[0]);
                subjectCell.setLinkURL("/bbs.do?method=showPost&articleId="+v3xBbsArticle.getId()+"&custom=true&theBoardId=" + spaceId + "&resourceMethod=listLatestFiveArticleAndAllBoard", OPEN_TYPE.href_blank);
                subjectCell.setHasAttachments(v3xBbsArticle.isHasAttachments());
                //成倍行不定列模式下，公共信息中的已阅和未读信息要使用css样式进行区分，传入一个参数以便前端判断使用该种css样式 added by Meng Yang 2009-05-19
                subjectCell.setClassName("ReadDifferFromNotRead");
                
                // 单元格2: 讨论发起人　
                MultiRowVariableColumnTemplete.Cell ownerCell = row.addCell();
                ownerCell.setCellWidth(width[1]);
                String issueName = BbsFunction.showName(model, bundle);
                ownerCell.setCellContent(Strings.getLimitLengthString(issueName, 12, "..."));
                ownerCell.setAlt(issueName);
                
                // 单元格3：发起日期
                MultiRowVariableColumnTemplete.Cell dateCell = row.addCell();
                String dateStr = "";
                if(v3xBbsArticle.getIssueTime()!=null) {
                	//按照首页标准时间格式显示，当天显示时间如16：45，否则显示日期如05/19 modified by Meng Yang 2009-06-05
                	Date todayFirstTime = Datetimes.getTodayFirstTime();      
                    Date articleIssueDate = v3xBbsArticle.getIssueTime();
                    if(articleIssueDate.getTime() < todayFirstTime.getTime()) {
                    	dateStr = Datetimes.format(articleIssueDate, "MM-dd");
                    } else {
                    	dateStr = Datetimes.format(articleIssueDate, "HH:mm");
                    }  
                }
                dateCell.setCellContent(dateStr);
                dateCell.setCellWidth(width[2]);
            }
        }
        t.addBottomButton(newBBs, "/bbs.do?method=issuePost&showSpaceLacation=true&custom=true&boardId="+spaceId + "&from=top");
        t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/bbs.do?method=deptlistAllArticle&departmentId="+spaceId+"&from=top"+"&custom=true");

        return t;
    }

}
