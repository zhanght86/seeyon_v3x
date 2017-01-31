package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocAlertLatest;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocAlertLatestManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.webmodel.DocAlertLatestVO;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MultiRowVariableColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Datetimes;

/**
 * 
 * @author Dongjw
 * @version 1.0 2007-5-28
 */
public class DocAlertSection extends BaseSection {
	private DocAlertLatestManager docAlertLatestManager;
	private DocHierarchyManager docHierarchyManager;
	private DocMimeTypeManager docMimeTypeManager;

	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public void setDocAlertLatestManager(DocAlertLatestManager docAlertLatestManager) {
		this.docAlertLatestManager = docAlertLatestManager;
	}

	@Override
	public String getId() {
		return "docAlertSection";
	}
	
	@Override
	public String getBaseName() {
		return "docAlert";
	}

	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("docAlert", preference);
	}

	@Override
	public String getIcon() {
		return null;
	}
	
	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));
		boolean isNarrow = super.isNarrow(width);
		int count = SectionUtils.getSectionCount(8, preference);
		
		Long userId = CurrentUser.get().getId();
		List<DocAlertLatest> dals = docAlertLatestManager.findAlertLatestsByUserByCount(
				userId,Constants.DOC_ALERT_STATUS_ALL,count);
		
		List<DocAlertLatestVO> dalvos = this.getAlertLatestVos(dals, userId, Constants.getOrgIdsOfUser(userId));
		
		if(isNarrow){
			MultiRowVariableColumnTemplete c = new MultiRowVariableColumnTemplete();
			
			if(dalvos != null){
	    		for (DocAlertLatestVO vo : dalvos) {
	    			MultiRowVariableColumnTemplete.Row row = c.addRow();
	    			MultiRowVariableColumnTemplete.Cell cell = row.addCell();
	    			
	    			cell.setCellContent(vo.getDocResource().getFrName());
	    			cell.setCellWidth(100);
	    			cell.setLinkURL("/doc.do?method=docOpenIframeOnlyId&docResId="+vo.getDocResource().getId(), OPEN_TYPE.openWorkSpace);
	    			cell.addExtIcon("/apps_res/doc/images/docIcon/" + vo.getIcon());
	    		}
			}
			
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docAlertLatestMore&type=personal");
			
			return c;
		}
		else{
			MultiRowVariableColumnTemplete c = new MultiRowVariableColumnTemplete();
			if(dalvos != null){
	    		for (DocAlertLatestVO vo : dalvos) {
	    			MultiRowVariableColumnTemplete.Row row = c.addRow();
	    			 boolean attach =  vo.getHasAttachments();
	
	    			MultiRowVariableColumnTemplete.Cell cell2 = row.addCell();	    			
	    			cell2.setCellContent(vo.getDocResource().getFrName());
	    		    cell2.setHasAttachments(attach);
	    			cell2.setCellWidth(55);
	    			cell2.setLinkURL("/doc.do?method=docOpenIframeOnlyId&docResId="+vo.getDocResource().getId(), OPEN_TYPE.openWorkSpace);
	    			
	    			MultiRowVariableColumnTemplete.Cell cell3 = row.addCell();	    			
	    			cell3.setCellContent(vo.getOprType());
	    			cell3.setCellWidth(20);
	    			
	    			MultiRowVariableColumnTemplete.Cell cell4 = row.addCell();
	    			cell4.setCellContent(Datetimes.format(vo.getDocAlertLatest().getLastUpdate(), Datetimes.datetimeStartWithMonthStyle));
	    			cell4.setCellWidth(25);
	    		}
			}
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_BOOKMANAGEMENT, "/doc.do?method=docAlertAdminIndex");
			c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docAlertLatestMore&type=personal");
	
			return c;
		}
	}
	
	private List<DocAlertLatestVO> getAlertLatestVos(List<DocAlertLatest> dals, Long userId, 
			String orgIds)  {
		if(dals == null)
			return new ArrayList<DocAlertLatestVO>();
		List<DocAlertLatestVO> ret = new ArrayList<DocAlertLatestVO>(); 
		for(DocAlertLatest dal : dals) {
			DocResource dr = docHierarchyManager.getDocResourceById(dal.getDocResourceId());
			DocAlertLatestVO vo = new DocAlertLatestVO(dal, dr);
			vo.setIcon(docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon());			
			String oprType = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME,
					Constants.getAlertTypeKey(dal.getChangeType()));
			vo.setOprType(oprType);			
			ret.add(vo);
		}
		return ret;
	}
}