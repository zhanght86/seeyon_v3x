package com.seeyon.v3x.calendar.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

public class CalEventManager4ISearch extends ISearchManager {

	private CalEventManager calEventManager;

	public CalEventManager getCalEventManager() {
		return calEventManager;
	}

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	@Override
	public Integer getAppEnumKey() {
		// TODO Auto-generated method stub
		return ApplicationCategoryEnum.calendar.getKey();
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
		// 1. 解析条件
		// 2. 分页查询
		List<CalEvent> list = this.calEventManager.iSearch(cModel);
		// 3. 组装数据，返回
		if (list != null)
			for (CalEvent event : list) {
				String title = event.getSubject();
				String fromUserName = event.getCreateUserName();
				String location = ResourceBundleUtil
						.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources","application." + this.getAppEnumKey()+ ".label");
				String link = "/calEvent.do?method=editIframe&id=" + event.getId();
				String bodyType = "";
				boolean hasAttachments = event.getAttachmentsFlag();
				ResultModel rm = new ResultModel(title, fromUserName, event
						.getBeginDate(), location, link,bodyType,hasAttachments);
				ret.add(rm);
			}
		return ret;
	}

}
