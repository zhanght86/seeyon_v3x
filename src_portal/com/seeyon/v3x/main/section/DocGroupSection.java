package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.doc.domain.DocFavorite;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocFavoriteManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete;
import com.seeyon.v3x.main.section.templete.ChessboardTemplete.Item;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.common.taglibs.functions.Functions;

public class DocGroupSection extends BaseSection {
    private static final Log log = LogFactory.getLog(DocGroupSection.class);
    private DocFavoriteManager docFavoriteManager;	
	
	private OrgManager orgManager;
	
	private DocMimeTypeManager docMimeTypeManager;	
	
	private DocAclManager docAclManager;
	
	private DocLibManager docLibManager;	
	
	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
	
	public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}

	public DocLibManager getDocLibManager() {
		return docLibManager;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}

	public DocAclManager getDocAclManager() {
		return docAclManager;
	}

	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}

	public DocMimeTypeManager getDocMimeTypeManager() {
		return docMimeTypeManager;
	}

	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public DocFavoriteManager getDocFavoriteManager() {
		return docFavoriteManager;
	}

	public void setDocFavoriteManager(DocFavoriteManager docFavoriteManager) {
		this.docFavoriteManager = docFavoriteManager;
	}

     @Override
    public boolean isAllowUsed() {
        return (Boolean)(SysFlag.news_showOtherAccountNews.getFlag());
    }
    
	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "docGroupSection";
	}
	
	@Override
	public String getBaseName() {
		if((Boolean)Functions.getSysFlag("sys_isGovVer")){
        	//政务多组织版
            return "docGroup_GOV";
        }else{
        	return "docGroup";
        }
	}

	@Override
	public String getName(Map<String, String> preference) {
        if((Boolean)Functions.getSysFlag("sys_isGovVer")){
        	//政务多组织版
            return SectionUtils.getSectionName("docGroup_GOV", preference);
        }else{
        	return SectionUtils.getSectionName("docGroup", preference);
        }
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
		
		int count = SectionUtils.getSectionCount(8, preference);
		
		ChessboardTemplete  ct = new ChessboardTemplete();
		ct.setLayout(8, columnNum);
		try {

			List<DocFavorite> list = docFavoriteManager.getFavoritesByCount(Constants.ORGENT_TYPE_GROUP, 0L, count);
			for(DocFavorite df:list){
				DocResource dr = df.getDocResource();
				Item item = ct.addItem();
				
				String icon = "";
				if (dr.getIsFolder() == false)
					icon = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon();
				else {
					String src = docMimeTypeManager.getDocMimeTypeById(dr.getMimeTypeId()).getIcon();
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
            log.error("集团空间-知识文档异常：", e);
		}
		ct.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docFavoriteMore&userType=group");
		return ct;
	}
}