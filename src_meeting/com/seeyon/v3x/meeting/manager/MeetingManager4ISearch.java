package com.seeyon.v3x.meeting.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.cap.doc.util.Constants;
import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class MeetingManager4ISearch extends ISearchManager {
	
	private MtMeetingManager mtMeetingManager;
	
	public void setMtMeetingManager(MtMeetingManager mtMeetingManager) {
		this.mtMeetingManager = mtMeetingManager;
	}

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.meeting.getKey();
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
	public List<ResultModel> iSearch(ConditionModel cModel) {
		List<ResultModel> ret = new ArrayList<ResultModel>();
		// 1. 解析条件
		// 2. 分页查询
		List<MtMeeting> list = mtMeetingManager.iSearch(cModel);
		// 3. 组装数据，返回
		if(list != null)
		for(MtMeeting meeting : list){
			String title = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME_MEETING, meeting.getTitle());
			String fromUserName = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, meeting.getCreateUser(), false);
			String location = "会议";
			String link = "/mtMeeting.do?method=mydetail&fisearch=1&id=" + meeting.getId();
			String bodyType = meeting.getDataFormat();
			boolean hasAttachmets = meeting.isAttachmentsFlag();
			ResultModel rm = new ResultModel(title, fromUserName, meeting.getCreateDate(), location, link,bodyType,hasAttachmets);
			ret.add(rm);
		}
			
		return ret;
	}
	
}