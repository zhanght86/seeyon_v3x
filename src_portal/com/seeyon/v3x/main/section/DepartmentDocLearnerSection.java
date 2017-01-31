package com.seeyon.v3x.main.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocLearning;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocLearningManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.main.Constant;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.Item;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

public class DepartmentDocLearnerSection extends BaseSection
{

	private static final Log log = LogFactory.getLog(DepartmentDocLearnerSection.class);
    private DocLearningManager docLearningManager;
    private DocMimeTypeManager docMimeTypeManager;

    private String titleId = "departmentDocLearnerSection";
    private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
	
	public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}
    public DocLearningManager getDocLearningManager() {
        return docLearningManager;
    }

    public void setDocLearningManager(DocLearningManager docLearningManager) {
        this.docLearningManager = docLearningManager;
    }
    
    public DocMimeTypeManager getDocMimeTypeManager() {
        return docMimeTypeManager;
    }

    public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
        this.docMimeTypeManager = docMimeTypeManager;
    }
    @Override
    public String getIcon()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return titleId;
    }
    
    @Override
	public String getBaseName() {
		return "departmentDocLearner";
	}

    @Override
    public String getName(Map<String, String> preference)
    {
        return SectionUtils.getSectionName("departmentDocLearner", preference);
    }

    @Override
    public Integer getTotal(Map<String, String> preference)
    {
        return null;
    }

//    @Override
//    public BaseSectionTemplete projection(Map<String, String> preference)
//    {
//        HtmlTemplete ht = new HtmlTemplete();
//        
//        int count = SectionUtils.getSectionCount(8, preference);
//        
//        Long departmentId = CurrentUser.get().getDepartmentId();
//        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
//        if(ownerId != null){
//            departmentId = Long.parseLong(ownerId);
//        }
//    
//        String html = "";
//        html += "<table width='100%' border='0' cellspacing='0' cellpadding='0'>";
//        html += "  <tr>";
//        html += "    <td valign='top' width='160'>";
//        html += "<img src='/seeyon/apps_res/v3xmain/images/section/section_doc_learner.gif' width='135' height='140'>";
//        html += "    </td>";
//        html += "    <td valign='top' >";
//        html += this.getData2HTML(departmentId, count);
//        html += "    </td>";
//        html += " </tr>";
//        html += "</table>";
//        
//        ht.setModel(HtmlTemplete.ModelType.block);
//        ht.setHtml(html);
//        ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, 
//        		"/doc.do?method=docLearningMore&deptId=" + departmentId);
//        return ht;
//    }
//    
//    /**
//     * 调用部门学习中心的接口，填充数据
//     */
//    private String getData2HTML(long departmentId, int count)
//    {
//        String str = "";
//
//		List<DocLearning> list = docLearningManager.getDocLearningsByCount(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
//				departmentId, count);
//		
//		for(DocLearning dl:list){
//			DocResource dr = dl.getDocResource();
//			String icon = docMimeTypeManager.getDocMimeTypeById(
//					dr.getMimeTypeId()).getIcon();	
//			String href = "/doc.do?method=docOpenIframeOnlyId&docResId=" + dr.getId();
//            str += "<div class='edocList'>";
//            str += " <a class='cursor-hand' href='javascript:void(null)' title='" + dr.getFrName() + "' onclick=" 
//    				+ this.openLink(href, Constant.OPEN_TYPE.open) + ">";
//            str += "  <img src='/seeyon/apps_res/doc/images/docIcon/" + icon + "' border='0' align='absmiddle'>";
//            str += "  &nbsp;&nbsp;" + Strings.getLimitLengthString(dr.getFrName(), 20, "...");
//            str += " </a>";
//            str += "</div>";
//		
//		}	
//		//默认8行 不够空行补
//		if(count-list.size()>0){
//			for(int i=0;i<count-list.size() ; i++){
//	            str += "<div class='edocList'>&nbsp;</div>";
//			}
//		}
//
//        return str;
//    }
    
    @Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		int width = Integer.parseInt(preference.get(PropertyName.width.name()));
        int columnNum = 1;
		Integer newLineStr = this.newLine2Column.get(width);
		if(newLineStr != null){
			columnNum = newLineStr.intValue();
		}
		int count = SectionUtils.getSectionCount(8, preference);
		Long departmentId = CurrentUser.get().getDepartmentId();
        String ownerId = preference.get(PortletEntityProperty.PropertyName.ownerId.name());
        if(ownerId != null){
            departmentId = Long.parseLong(ownerId);
        }
		ChessboardTemplete  ct = new ChessboardTemplete();
		ct.setLayout(8, columnNum);
		try {
			List<DocLearning> list = new ArrayList<DocLearning>();
			list = docLearningManager.getDocLearningsByCount(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, count);
			for(DocLearning df:list){
				DocResource dr = df.getDocResource();
				Item item = ct.addItem();
				String icon = docMimeTypeManager.getDocMimeTypeById(
						dr.getMimeTypeId()).getIcon();
				item.setIcon("/apps_res/doc/images/docIcon/" + icon);
				String name = dr.getFrName();
				long type = dr.getFrType();
				if(	type == Constants.FOLDER_MINE
						|| type == Constants.FOLDER_CORP
						|| type == Constants.ROOT_ARC
						|| type == Constants.FOLDER_ARC_PRE
						|| type == Constants.FOLDER_PROJECT_ROOT
						|| type == Constants.FOLDER_PLAN
						|| type == Constants.FOLDER_TEMPLET
						|| type == Constants.FOLDER_SHARE
						|| type == Constants.FOLDER_BORROW
						|| type == Constants.FOLDER_PLAN_DAY
						|| type == Constants.FOLDER_PLAN_MONTH
						|| type == Constants.FOLDER_PLAN_WEEK
						|| type == Constants.FOLDER_PLAN_WORK){
					name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
				}				
				item.setName(name);
				String link = "/doc.do?method=docOpenIframeOnlyId&docResId=" + dr.getId();
				if(Constant.OPEN_TYPE.open.name().equals("open")) {
					item.setOpenType(ChessboardTemplete.OPEN_TYPE.openWorkSpace);
				}else {
					item.setOpenType(ChessboardTemplete.OPEN_TYPE.href);
				}
				item.setLink(link);		
			}
		} catch (Exception e) {
            log.error("部门学习区异常：", e);
		}
		ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docLearningMore&deptId=" + departmentId);
		return ct;
	}

//    private String openLink(String linkURL, OPEN_TYPE open_type){
//        if(open_type.equals(Constant.OPEN_TYPE.open)){
//            //弹出窗口
//            return "javascript:openSubjectDetail('" + linkURL + "','" + titleId + "')";
//        }else{
//            //直接超链
//            return "javascript:hrefSubjectDetail('" + linkURL + "')";
//        }
//    }
    
}
