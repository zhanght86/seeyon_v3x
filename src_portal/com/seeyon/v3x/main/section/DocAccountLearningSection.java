package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocLearningManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.Item;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

public class DocAccountLearningSection extends BaseSection {
    private static final Log log = LogFactory.getLog(DocAccountLearningSection.class);
	private DocLearningManager docLearningManager;	
	private DocMimeTypeManager docMimeTypeManager;
	
	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
	
	public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}
	
	public DocMimeTypeManager getDocMimeTypeManager() {
		return docMimeTypeManager;
	}

	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "docAccountLearningSection";
	}
	
	@Override
	public String getBaseName() {
		return "docAccountLearning";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("docAccountLearning", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));
		int columnNum = 1;
		Integer newLineStr = this.newLine2Column.get(width);
		if(newLineStr != null){
			columnNum = newLineStr.intValue();
		}
		
		int count = SectionUtils.getSectionCount(16, preference);
		
		ChessboardTemplete  ct = new ChessboardTemplete();
		ct.setLayout(8, columnNum);
		try {
			List<DocLearning> list = new ArrayList<DocLearning>();
			list = docLearningManager.getDocLearningsByCount(V3xOrgEntity.ORGENT_TYPE_ACCOUNT, CurrentUser.get().getLoginAccount(), count);
			for(DocLearning dl:list){
				DocResource dr = dl.getDocResource();
				Item item = ct.addItem();
				
				String icon = docMimeTypeManager.getDocMimeTypeById(
							dr.getMimeTypeId()).getIcon();				
				item.setIcon("/apps_res/doc/images/docIcon/" + icon);
				
				item.setName(dr.getFrName());
				
				String link = "/doc.do?method=";
				item.setOpenType(ChessboardTemplete.OPEN_TYPE.openWorkSpace);
				link += "docOpenIframeOnlyId&docResId=" + dr.getId(); 			
				item.setLink(link);				
			}
		} catch (Exception e) {
            log.error("单位空间学习区异常：", e);
		}
		ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, 
				"/doc.do?method=docLearningMore&accountId=" + CurrentUser.get().getLoginAccount());
		return ct;
	}

	public DocLearningManager getDocLearningManager() {
		return docLearningManager;
	}

	public void setDocLearningManager(DocLearningManager docLearningManager) {
		this.docLearningManager = docLearningManager;
	}
}