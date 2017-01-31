package com.seeyon.v3x.news.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.util.Constants;
import com.seeyon.v3x.news.util.NewsUtils;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class NewsManager4ISearch  extends ISearchManager {
	private NewsDataManager newsDataManager;

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.news.getKey();
	}

	@Override
	public String getAppShowName() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public int getSortId() {
		// TODO Auto-generated method stub
		return this.getAppEnumKey();
	}

	@Override
	public List<ResultModel> iSearch(ConditionModel cModel) {
		List<ResultModel> ret = new ArrayList<ResultModel>();
		List<NewsData> list = this.newsDataManager.iSearch(cModel,cModel.getUser().getLoginAccount());
		if(list != null){
			for(NewsData bd : list){		
				String fromUserName = NewsUtils.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, bd.getPublishUserId(), false);
				String location = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, "application." + this.getAppEnumKey() + ".label")
					+ ISearchManager.LOCATION_PATH_SEPARATOR + bd.getType().getTypeName();
				String link = "/newsData.do?method=userView&id=" + bd.getId();
				String bodyType = newsDataManager.getBody(bd.getId()).getBodyType();
				boolean hasAttachments = bd.getAttachmentsFlag();
				ResultModel rm = new ResultModel(bd.getTitle(), fromUserName, bd.getPublishDate(), location, link,bodyType,hasAttachments);
				ret.add(rm);
			}
		}
		return ret;
	}

	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

}
