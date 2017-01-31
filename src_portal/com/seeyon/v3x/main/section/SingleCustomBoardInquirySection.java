package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowFourColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

public class SingleCustomBoardInquirySection extends BaseSection {
	
	private static Log log = LogFactory.getLog(SingleCustomBoardInquirySection.class);

	private InquiryManager inquiryManager;

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "singleCustomBoardInquirySection";
	}

	@Override
	public String getBaseName(Map<String, String> preference) {
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		try {
			InquirySurveytype t = this.inquiryManager.getSurveyTypeById(boardId);

			if (t != null && t.getFlag() == 0) {
				return t.getTypeName();
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	@Override
	public String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		try {
			InquirySurveytype t = this.inquiryManager.getSurveyTypeById(boardId);

			if (t != null && t.getFlag() == 0) {
				return t.getTypeName();
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return null;
	}
	
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
		if (Strings.isBlank(singleBoardId)) {
			return false;
		}

		try {
			InquirySurveytype type = this.inquiryManager.getSurveyTypeById(Long.valueOf(singleBoardId));
			return type != null && type.getFlag() == 0;
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		Long boardId = Long.parseLong(preference.get(PropertyName.singleBoardId.name()));
		String spaceType = preference.get(PropertyName.spaceType.name());
		spaceType = "public_custom".equalsIgnoreCase(spaceType) ? "5" : "6";
		String spaceId = preference.get(PropertyName.ownerId.name());
		MultiRowFourColumnTemplete mt = new MultiRowFourColumnTemplete();
		int count = SectionUtils.getSectionCount(8, preference);
		try {
			List<SurveyBasicCompose> ilist = inquiryManager.getSurveyBasicListByType(boardId, count);
			for (SurveyBasicCompose sbc : ilist) {
				InquirySurveybasic isb = sbc.getInquirySurveybasic();
				MultiRowFourColumnTemplete.Row row = mt.addRow();
				row.setSubject(isb.getSurveyName());
				row.setHasAttachments(isb.getAttachmentsFlag());
				row.setLink("/inquirybasic.do?method=showInquiryFrame&spaceId=" + spaceId + "&bid=" + isb.getId() + "&surveytypeid=" + boardId, OPEN_TYPE.href_blank);
				row.setCreateDate(isb.getSendDate());
				row.setCreateMemberName(Functions.showMemberName(isb.getCreaterId()));
			}
			mt.addBottomButton("inquiry_index_label", "/inquirybasic.do?method=recent_or_check&where=space&spaceType=" + spaceType + "&spaceId=" + spaceId);
			mt.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/inquirybasic.do?method=more_recent_or_check&from=section&typeId=" + boardId + "&spaceType=" + spaceType + "&spaceId=" + spaceId);
		} catch (Exception e) {
			log.error("", e);
		}

		return mt;
	}

}