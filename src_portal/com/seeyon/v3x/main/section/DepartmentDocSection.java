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
import com.seeyon.v3x.doc.domain.DocFavorite;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocFavoriteManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.Item;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;

public class DepartmentDocSection extends BaseSection
{
	private static final Log log = LogFactory.getLog(DepartmentDocSection.class);
    private String titleId = "departmentDocSection";

    private DocFavoriteManager docFavoriteManager;      
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
    
    public DocFavoriteManager getDocFavoriteManager() {
		return docFavoriteManager;
	}

	public void setDocFavoriteManager(DocFavoriteManager docFavoriteManager) {
		this.docFavoriteManager = docFavoriteManager;
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
		return "departmentDoc";
	}

    @Override
    public String getName(Map<String, String> preference)
    {
        return SectionUtils.getSectionName("departmentDoc", preference);
    }

    @Override
    public Integer getTotal(Map<String, String> preference)
    {
        return null;
    }

//    @Override
//    public BaseSectionTemplete projection(Map<String, String> preference)
//    {
//        
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
//        html += "<img src='/seeyon/apps_res/v3xmain/images/section/section_doc.gif' width='135' height='140'>";
//        html += "    </td>";
//        html += "    <td valign='top' >";
//        html += this.getData2HTML(departmentId, count);
//        html += "    </td>";
//        html += " </tr>";
//        html += "</table>";
//        
//        ht.setModel(HtmlTemplete.ModelType.block);
//        ht.setHtml(html);
//        ht.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docFavoriteMore&userType=dept&deptId=" + departmentId);
//        return ht;
//        
//    }
//
//    /**
//     * 调用部门知识文档的接口，填充数据
//     */
//    private String getData2HTML(long departmentId, int count)
//    {
//        String str = "";
//
//		List<DocFavorite> list = docFavoriteManager.getFavoritesByCount(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, 
//				departmentId, count);
//
//		for(DocFavorite dl:list){
//			DocResource dr = dl.getDocResource();		
//			
//			String icon = "";
//			String name = dr.getFrName();
//			long type = dr.getFrType();
//			String link = "/doc.do?method=";
//			if (dr.getIsFolder()){
//				String src = docMimeTypeManager
//					.getDocMimeTypeById(dr.getMimeTypeId())
//					.getIcon();
//				icon = src.substring(0, src.indexOf("|"));
//				
//				if(	type == Constants.FOLDER_MINE
//						|| type == Constants.FOLDER_CORP
//						|| type == Constants.ROOT_ARC
//						|| type == Constants.FOLDER_ARC_PRE
//						|| type == Constants.FOLDER_PROJECT_ROOT
//						|| type == Constants.FOLDER_PLAN
//						|| type == Constants.FOLDER_TEMPLET
//						|| type == Constants.FOLDER_SHARE
//						|| type == Constants.FOLDER_BORROW
//						|| type == Constants.FOLDER_PLAN_DAY
//						|| type == Constants.FOLDER_PLAN_MONTH
//						|| type == Constants.FOLDER_PLAN_WEEK
//						|| type == Constants.FOLDER_PLAN_WORK){
//					name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
//				}
//				
//				link += "docHomepageIndex&docResId=" + dr.getId();
//			}else {
//				icon = docMimeTypeManager.getDocMimeTypeById(
//						dr.getMimeTypeId()).getIcon();	
//				if(type == Constants.LINK_FOLDER)
//					link += "docHomepageIndex&docResId=" + dr.getSourceId() + "&parentId=" + dr.getParentFrId();
//				else
//					link += "docOpenIframeOnlyId&docResId=" + dr.getId(); 
//			}	
//
//
//
//            str += "<div class='edocList'>";
//            if(dr.getIsFolder() || type == Constants.LINK_FOLDER)
//                str += " <a class='cursor-hand' href='javascript:void(null)' title='" + name + "' onclick=\"" 
//        			+ this.openLink(link, Constant.OPEN_TYPE.href) + "\">";
//            else
//            	str += " <a class='cursor-hand' href='javascript:void(null)' title='" + name + "' onclick=\"" 
//            		+ this.openLink(link, Constant.OPEN_TYPE.open) + "\">";
//            str += "  <img src='/seeyon/apps_res/doc/images/docIcon/" + icon + "' border='0' align='absmiddle'>";
//            str += "  &nbsp;&nbsp;" + Strings.getLimitLengthString(name, 20, "...");
//            str += " </a>";
//            str += "</div>";
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

			List<DocFavorite> list = new ArrayList<DocFavorite>();
			list = docFavoriteManager.getFavoritesByCount(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT, departmentId, count);
			for(DocFavorite df:list){
				DocResource dr = df.getDocResource();
				Item item = ct.addItem();
				String icon = "";
				if (dr.getIsFolder() == false)
					icon = docMimeTypeManager.getDocMimeTypeById(
							dr.getMimeTypeId()).getIcon();
				else {
					String src = docMimeTypeManager
							.getDocMimeTypeById(dr.getMimeTypeId())
							.getIcon();
					icon = src.substring(0, src.indexOf("|"));
				}				
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
				String link = "/doc.do?method=";
				if(dr.getIsFolder()) {
					link += "docHomepageIndex&docResId=" + dr.getId();
					
					item.setOpenType(ChessboardTemplete.OPEN_TYPE.href);
				}else if(dr.getFrType() == Constants.LINK_FOLDER){
					link += "docHomepageIndex&docResId=" + dr.getSourceId() + "&parentId=" + dr.getParentFrId();
					
					item.setOpenType(ChessboardTemplete.OPEN_TYPE.href);
				}else {
					item.setOpenType(ChessboardTemplete.OPEN_TYPE.openWorkSpace);
					// 根据内容类型不同，选择不同的打开方式
					link += "docOpenIframeOnlyId&docResId=" + dr.getId(); 
				}
				item.setLink(link);		
			}
		} catch (Exception e) {
            log.error("部门知识文档异常：", e);
		}
		ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docFavoriteMore&userType=dept&deptId=" + departmentId);
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
