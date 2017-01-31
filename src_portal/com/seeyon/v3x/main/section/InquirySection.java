package com.seeyon.v3x.main.section;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;

/**
 * 单位最新调查栏目
 */
public class InquirySection extends BaseSection {
	
	private static final Log log = LogFactory.getLog(InquirySection.class);
	
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
		return "inquirySection";
	}
	
	@Override
	public String getBaseName() {
		return "inquiry";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("inquiry", preference);	//资源文件key
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		
		MultiRowThreeColumnTemplete ct = new MultiRowThreeColumnTemplete();
		String surveyName = "";
		Long surveyId;
		InquirySurveybasic isb = null;
		Timestamp surveySendDate = null;
		InquirySurveytype ist = null;
		String surveytype = "";
		int count = SectionUtils.getSectionCount(8, preference);
		try {
			List<SurveyBasicCompose> ilist = inquiryManager.getInquiryBasicListByUserID(count);
			int rand=new Random().nextInt();
			for(SurveyBasicCompose sbc:ilist){
				MultiRowThreeColumnTemplete.Row row = ct.addRow();
				isb= sbc.getInquirySurveybasic();
				surveyId = isb.getId();  //调查id
				surveyName = isb.getSurveyName();//调查名称
				surveySendDate = isb.getSendDate();//发布时间
				ist = isb.getInquirySurveytype();
				surveytype = ist.getTypeName();//版块类型
				row.setSubject(surveyName);
				row.setHasAttachments(isb.getAttachmentsFlag());
				//强制首页页签的Ajax刷新
				row.setLink("/inquirybasic.do?method=showInquiryFrame&bid="+surveyId+"&surveytypeid="+ist.getId()+"&random="+rand, OPEN_TYPE.href_blank);
				row.setCreateDate(surveySendDate);
//				row.setCategory(surveytype, "/inquirybasic.do?method=survey_index&surveytypeid=" + ist.getId() +"&typeName="+surveytype);
				row.setCategory(surveytype, "/inquirybasic.do?method=more_recent_or_check&typeId="+ist.getId()+"&from=section");
				//多行三列模式下，公共信息中的已阅和未读信息要使用css样式进行区分，传入一个参数以便前端判断使用该种css样式 added by Meng Yang 2009-05-19
              	row.setClassName("ReadDifferFromNotRead");
			}
			ct.addBottomButton("inquiry_index_label", "/inquirybasic.do?method=recent_or_check&group=account&where=space");
			ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/inquirybasic.do?method=more_recent_or_check&group=account&from=section");
		} catch (Exception e) {
			log.error("调查栏目读取信息异常", e);
		}
		
		return ct;
	}

}
