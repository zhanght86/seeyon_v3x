package com.seeyon.v3x.collaboration.manager.impl;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class ColManagerISearch extends ISearchManager {
	private ColManager colManager;
	private OrgManager orgManager;
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.collaboration.getKey();
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
		List<Affair> list = colManager.iSearch(cModel);
		// 3. 组装数据，返回
		if(list != null){
			for(Affair affair : list){
                Integer resentTime = affair.getResentTime();
                String forwardMember = affair.getForwardMember();
                String title = ColHelper.mergeSubjectWithForwardMembers(affair.getSubject(), 80, forwardMember, resentTime, orgManager, null);
                
				V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(affair.getSenderId());
				} catch (BusinessException e) {
					
				}
				String fromUserName = member.getName();
				String locationPrefix = Constant.getString4CurrentUser("collaboration.information.label");
				String locationSuffix = null;
				if(affair.getState() == StateEnum.col_pending.key()){
					locationSuffix = Constant.getString4CurrentUser("col.coltype.Pending.label");
				}else if(affair.getState() == StateEnum.col_done.key()){
					locationSuffix = Constant.getString4CurrentUser("col.coltype.Done.label");
				}else{
					locationSuffix = Constant.getString4CurrentUser("col.coltype.Sent.label");
				}
				String location = locationPrefix + "-" + locationSuffix;
				String link = "/collaboration.do?method=detail&from=Done&affairId=" + affair.getId();
				String bodyType = affair.getBodyType();
				boolean hasAttachments = affair.isHasAttachments();
				ResultModel rm = new ResultModel(title, fromUserName, affair.getCreateDate(), location, link,bodyType,hasAttachments);
				ret.add(rm);
			}
		}
		return ret;
	}
	
}
