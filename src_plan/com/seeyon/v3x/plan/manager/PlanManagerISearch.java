package com.seeyon.v3x.plan.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plan.domain.Plan;

public class PlanManagerISearch extends ISearchManager {
	private PlanManager planManager;
	private OrgManager orgManager;
	private static final Log log = LogFactory.getLog(PlanManagerISearch.class);

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public PlanManager getPlanManager() {
		return planManager;
	}

	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}

	@Override
	public Integer getAppEnumKey() {
		// TODO Auto-generated method stub
		return ApplicationCategoryEnum.plan.getKey();
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
		// TODO Auto-generated method stub
		List<ResultModel> ret = new ArrayList<ResultModel>();
		// 1. 解析条件
		// 2. 分页查询
		List<Plan> list = planManager.iSearch(cModel);
		// 3. 组装数据，返回
		if(list != null){
			for(Plan plan : list){
				String title = plan.getTitle();			
				V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(plan.getCreateUserId());
				} catch (BusinessException e) {
					log.error("error load member!");
				}
				String fromUserName = member.getName();
				String locationPrefix = Constant.getCommonString("application.5.label");
//				String locationSuffix = null;
				String location = locationPrefix;//+ "-" + locationSuffix;
				String link = "/plan.do?method=initDetailHome&editType=doc&id="+plan.getId();
				String bodyType = plan.getPlanBody().getBodyType();
				boolean hasAttachments = plan.isHasAttachments();
				ResultModel rm = new ResultModel(title, fromUserName, plan.getCreateTime(), location, link,bodyType,hasAttachments);
				ret.add(rm);
			}
		}
		return ret;
	}

}
