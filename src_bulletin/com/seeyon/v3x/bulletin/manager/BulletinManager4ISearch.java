package com.seeyon.v3x.bulletin.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.util.BulletinUtils;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class BulletinManager4ISearch extends ISearchManager {
	private BulDataManager bulDataManager;

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.bulletin.getKey();
	}

	@Override
	public String getAppShowName() {
		return null;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	@Override
	public int getSortId() {
		return this.getAppEnumKey();
	}

	@Override
	public List<ResultModel> iSearch(ConditionModel cModel) {
		List<ResultModel> ret = new ArrayList<ResultModel>();
		List<BulData> list = this.bulDataManager.iSearch(cModel);
		if(list != null){
			for(BulData bd : list){		
				String fromUserName = BulletinUtils.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, bd.getCreateUser(), false);
				String location = ResourceBundleUtil.getString(Constants.COMMON_RESOURCE_BASENAME, "application." + this.getAppEnumKey() + ".label")
					+ ISearchManager.LOCATION_PATH_SEPARATOR + bd.getType().getTypeName();
				String link = "/bulData.do?method=userView&id=" + bd.getId();
				String bodyType = bulDataManager.getBody(bd.getId()).getBodyType();
				boolean hasAttachments = bd.getAttachmentsFlag();
				ResultModel rm = new ResultModel(bd.getTitle(), fromUserName, bd.getPublishDate(), location, link,bodyType,hasAttachments);
				ret.add(rm);
			}
		}
		return ret;
	}

}
