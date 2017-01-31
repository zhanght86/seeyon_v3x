package com.seeyon.v3x.bbs.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 讨论综合查询
 */
public class BbsManager4ISearch extends ISearchManager {
	
	private BbsArticleManager bbsArticleManager;
	private BbsBoardManager bbsBoardManager;
	private static Log log = LogFactory.getLog(BbsManager4ISearch.class);
	
	public void setBbsBoardManager(BbsBoardManager bbsBoardManager)
	{
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.bbs.getKey();
	}

	@Override
	public String getAppShowName() {
		return null;
	}

	@Override
	public int getSortId() {
		return this.getAppEnumKey();
	}

	@Override
	public List<ResultModel> iSearch(ConditionModel cModel) throws Exception {
		List<ResultModel> ret = new ArrayList<ResultModel>();
		List<V3xBbsArticle> list = null;
		try {
			list = bbsArticleManager.iSearch(cModel);
		} catch (Exception e) {
			log.error("", e);
		}
		
		//组装数据，返回
		if(list != null) {
			for(V3xBbsArticle article : list){
				String title = article.getArticleName();
				String fromUserName = null;
				//对匿名贴子进行处理(自己可以看见)
				if(article.getAnonymousFlag() && article.getIssueUserId()!=CurrentUser.get().getId()) {
					fromUserName = ResourceBundleUtil.getString(BbsConstants.BBS_I18N_RESOURCE, "anonymous.label");
				} else {
					fromUserName = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, article.getIssueUserId() , false);	
				}
				String location = ResourceBundleUtil.getString(BbsConstants.BBS_I18N_RESOURCE, "bbs.board.label.4search");
				V3xBbsBoard bbsboard = bbsBoardManager.getBoardById(article.getBoardId());
				location += "-" + bbsboard.getName();
				String link = "/bbs.do?method=showPost&articleId="+article.getId();
				String bodyType = "";
				boolean hasAttachments = article.isHasAttachments();
				ResultModel rm = new ResultModel(title, fromUserName, article.getIssueTime(), location, link, bodyType, hasAttachments);
				ret.add(rm);
			}
		}	
		return ret;
	}
	

}
