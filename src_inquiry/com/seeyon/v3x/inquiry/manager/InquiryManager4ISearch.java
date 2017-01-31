package com.seeyon.v3x.inquiry.manager;

import java.util.ArrayList;
import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class InquiryManager4ISearch extends ISearchManager {
	
	private InquiryManager inquiryManager;
	
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	@Override
	public Integer getAppEnumKey() {
		return ApplicationCategoryEnum.inquiry.getKey();
	}

	@Override
	public String getAppShowName() {
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
		List<InquirySurveybasic> list = new ArrayList<InquirySurveybasic>();
		try {
			list = inquiryManager.iSearch(cModel);
		} catch (Exception e) {
		}
		// 3. 组装数据，返回
		if(list != null)
		for(InquirySurveybasic basic : list){
			String title = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME_INQUIRY, basic.getSurveyName());
			String fromUserName = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, basic.getCreaterId() , false);
			InquirySurveytype type = new InquirySurveytype();
			try {
				type = inquiryManager.getSurveyTypeById(basic.getSurveyTypeId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String location = "调查-"+type.getTypeName();
			String link = "/inquirybasic.do?method=showInquiryFrame&bid=" + basic.getId() + "&surveytypeid=" + basic.getSurveyTypeId()+"&group=";
			String bodyType = "";
			boolean hasAttachments = basic.getAttachmentsFlag();
			ResultModel rm = new ResultModel(title, fromUserName, basic.getSendDate(), location, link,bodyType,hasAttachments);
			ret.add(rm);
		}
			
		return ret;
	}
	

}
