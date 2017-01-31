package com.seeyon.v3x.main.section;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.util.Strings;

/**
 * 集团空间调查
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class GroupInquirySection extends BaseSection {
    private static final Log log = LogFactory.getLog(GroupInquirySection.class);
    private InquiryManager inquiryManager;

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	
	@Override
    public boolean isAllowUsed() {
        return (Boolean)(SysFlag.inquiry_showOtherAccountInquiry.getFlag());
    }
    
    
	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "groupInquirySection";
	}
	
	@Override
	public String getBaseName() {
        //政务多组织版
        if((Boolean)Functions.getSysFlag("sys_isGovVer")){            
            return "groupInquiry_GOV";
        } else {
        	return "groupInquiry";
        }
	}

	@Override
	public String getName(Map<String, String> preference) {
		String name = preference.get("columnsName");
		if (Strings.isNotBlank(name)) {
			return name;
		}
        String sectionName = "groupInquiry";
        //政务多组织版
        if((Boolean)Functions.getSysFlag("sys_isGovVer")){            
            sectionName = "groupInquiry_GOV";
        }
        return sectionName;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
        //TODO 取得集团调查总数
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {		  
        /*
        OneItemUseTwoRowTemplete mt = new OneItemUseTwoRowTemplete();
		InquirySurveybasic isb = null;
		InquirySurveytype ist = null;
		try {
			List<SurveyBasicCompose> ilist = inquiryManager.getGroupInquiryBasicList(4);
			for(SurveyBasicCompose sbc : ilist){
			    isb = sbc.getInquirySurveybasic();
                Long surveyId = isb.getId();  //调查id
			    ist = isb.getInquirySurveytype();
                String surveytype = ist.getTypeName();
                OneItemUseTwoRowTemplete.Row row = mt.addRow();
                row.setSubject(isb.getSurveyName());//调查名称
                row.setLink("/inquirybasic.do?method=showInquiryFrame&bid="+surveyId+"&surveytypeid="+ist.getId(), OPEN_TYPE.openWorkSpaceRight);
                row.setCreateDate(isb.getSendDate());//发布时间
                row.setCategory(surveytype, "/inquirybasic.do?method=more_recent_or_check&typeId="+ist.getId()+"&group=group&from=section");
			}
            mt.addBottomButton("inquiry_index_label", "/inquirybasic.do?method=recent_or_check&group=group");
			mt.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/inquirybasic.do?method=more_recent_or_check&group=true&from=section");
		} catch (Exception e) {
            log.error("集团空间-读取调查异常", e);
		}
		return mt;
         */
        
        MultiRowThreeColumnTemplete ct = new MultiRowThreeColumnTemplete();
        String surveyName = "";
        Long surveyId;
        InquirySurveybasic isb = null;
        Timestamp surveySendDate = null;
        InquirySurveytype ist = null;
        String surveytype = "";
        int count = SectionUtils.getSectionCount(8, preference);
        try {
            List<SurveyBasicCompose> ilist = inquiryManager.getGroupInquiryBasicList(count);
            for(SurveyBasicCompose sbc:ilist){
                MultiRowThreeColumnTemplete.Row row = ct.addRow();
                isb= sbc.getInquirySurveybasic();
                surveyId = isb.getId();  //调查id
                surveyName = isb.getSurveyName();//调查名称
                surveySendDate = isb.getSendDate();//发布时间
                ist = isb.getInquirySurveytype();
                surveytype = ist.getTypeName();//版块类型
                row.setSubject(surveyName);
                row.setLink("/inquirybasic.do?method=showInquiryFrame&bid="+surveyId+"&surveytypeid="+ist.getId(), OPEN_TYPE.href_blank);
                row.setCreateDate(surveySendDate);
                row.setCategory(surveytype, "/inquirybasic.do?method=more_recent_or_check&typeId="+ist.getId()+"&group=group&from=section");
                //多行三列模式下，公共信息中的已阅和未读信息要使用css样式进行区分，传入一个参数以便前端判断使用该种css样式 added by Meng Yang 2009-05-19
              	row.setClassName("ReadDifferFromNotRead");
            }
            ct.addBottomButton("inquiry_index_label", "/inquirybasic.do?method=recent_or_check&group=group&where=space");
            ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/inquirybasic.do?method=more_recent_or_check&group=group&from=section");
        } catch (Exception e) {
            log.error("调查栏目读取信息异常", e);
        }
        
        return ct;
        
	}

}
