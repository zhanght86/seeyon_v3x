package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.space.manager.SpaceManager;
/**
 * 自定义团队空间调查
 * @author Macx
 *
 */
public class CustomInquirySection extends BaseSection {

	private static Log log = LogFactory.getLog(CustomInquirySection.class);
	
	private InquiryManager inquiryManager;
	
	private SpaceManager spaceManager;
	
	public SpaceManager getSpaceManager() {
		return spaceManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	@Override
	public String getId() {
		return "customInquirySection";
	}
	
	@Override
	public String getBaseName() {
		return "customInquiry";
	}

	@Override
	protected String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("customInquiry", preference);
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
		Long boardId = Long.parseLong(preference.get(PropertyName.ownerId.name()));
		MultiRowFourColumnTemplete mt = new MultiRowFourColumnTemplete();  
        Long surveyId;
        InquirySurveybasic isb = null;
        int count = SectionUtils.getSectionCount(8, preference);
		try {
			List<SurveyBasicCompose> ilist = inquiryManager.getSurveyBasicListByType(boardId,count);
			for(SurveyBasicCompose sbc:ilist){
				MultiRowFourColumnTemplete.Row row = mt.addRow();
				isb= sbc.getInquirySurveybasic();
				surveyId = isb.getId();  //调查id
				row.setSubject(isb.getSurveyName());
				row.setHasAttachments(isb.getAttachmentsFlag());
				row.setLink("/inquirybasic.do?method=showInquiryFrame&bid="+surveyId+"&surveytypeid=" + boardId, OPEN_TYPE.href_blank);
				row.setCreateDate(isb.getSendDate());
				row.setCreateMemberName(Functions.showMemberName(isb.getCreaterId()));
			}
			boolean isSpaceBulManager = this.spaceManager.isManagerOfThisSpace(CurrentUser.get().getId(), boardId);
			if (isSpaceBulManager) {
				mt.addBottomButton("new_inquiry_button", "/inquirybasic.do?method=puliscIndex&surveytypeid="+boardId+"&custom=true");
			}
			 mt.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/inquirybasic.do?method=more_recent_or_check&typeId="+boardId+"&from=section&custom=true");
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return mt;
	}

}
