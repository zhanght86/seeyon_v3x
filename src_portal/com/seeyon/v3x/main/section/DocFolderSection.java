package com.seeyon.v3x.main.section;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.doc.manager.DocMimeTypeManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.ChessMultiRowThreeColumnTemplete;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete.OPEN_TYPE;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.Strings;

public class DocFolderSection extends BaseSection {
    private static final Log log = LogFactory.getLog(DocFolderSection.class);
    
    private DocHierarchyManager docHierarchyManager;
	
	private DocMimeTypeManager docMimeTypeManager;	
	
	private DocLibManager docLibManager;
	
	private Map<Integer, Integer> newLine2Column = new HashMap<Integer, Integer>();
	
	public void setNewLine2Column(Map<String, String> newLine2Column) {
		Set<Map.Entry<String, String>> en = newLine2Column.entrySet();
		for (Map.Entry<String, String> entry : en) {
			this.newLine2Column.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
		}
	}
    
	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
        this.docHierarchyManager = docHierarchyManager;
    }

    @Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "docFolderSection";
	}

	@Override
	public String getName(Map<String, String> preference) {
        String folderIdStr = preference.get(PropertyName.singleBoardId.name());
        if(Strings.isNotBlank(folderIdStr)){
            DocResource res = docHierarchyManager.getDocResourceById(Long.parseLong(folderIdStr));
            if(res != null){
            	//如果文档不是登陆单位的。显示单位简称
            	String docName = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, res.getFrName());
            	DocLib lib = docLibManager.getDocLibById(res.getDocLibId());
            	User user = CurrentUser.get();
            	if(user != null && lib != null && lib.getDomainId() != 0){
            		if(lib.getDomainId() != user.getLoginAccount()){
            			String shortName = Functions.getAccountShortName(lib.getDomainId());
            			if(Strings.isNotBlank(shortName)){
            				docName = docName+"("+shortName+")";
            			}
            		}
            	}
                return docName;                
            }
        }
		return null;
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		String folderIdStr = preference.get(PropertyName.singleBoardId.name());
		User user = CurrentUser.get();
		ChessMultiRowThreeColumnTemplete cmt = new ChessMultiRowThreeColumnTemplete();
		
		int count = SectionUtils.getSectionCount(8, preference);
		
		//显示列
		String rowStr = preference.get("rowList");
		if(Strings.isBlank(rowStr)){
			rowStr = "frName,lastUpdate,lastUserId";
		}
		String[] rows = rowStr.split(",");
		cmt.addRowName("frName");
		for(String row : rows){
			cmt.addRowName(row);
		}
		boolean isNarrow = Boolean.valueOf(preference.get(PropertyName.isNarrow.name()));
        if(Strings.isNotBlank(folderIdStr)){
        	DocResource parentRes = this.personalDoc(Long.parseLong(folderIdStr), user.getId());
        	Pagination.setNeedCount(false); //不需要分页
        	Pagination.setFirstResult(0);
        	Pagination.setMaxResults(count);
        	int RowNumber = 8;
        	try {
        		List<DocResource> docResList = null;
        		if(Strings.isNotBlank(folderIdStr)){
        			docResList = docHierarchyManager.findAllDocsByPageBySection(parentRes.getId(), parentRes.getFrType(), 1, count, user.getId());
        		}
        		if(docResList != null && !docResList.isEmpty()){
        			if(docResList.size() > 8){
        				RowNumber = docResList.size();
        			}
        			for (DocResource res : docResList) {
        				ChessMultiRowThreeColumnTemplete.Row row = cmt.addRow();
        				//名称
        				String name = res.getFrName();
        				long type = res.getFrType();
        				if(type == Constants.FOLDER_MINE || type == Constants.FOLDER_CORP
    						|| type == Constants.ROOT_ARC || type == Constants.FOLDER_ARC_PRE
    						|| type == Constants.FOLDER_PROJECT_ROOT || type == Constants.FOLDER_PLAN
    						|| type == Constants.FOLDER_TEMPLET || type == Constants.FOLDER_SHARE
    						|| type == Constants.FOLDER_BORROW || type == Constants.FOLDER_PLAN_DAY
    						|| type == Constants.FOLDER_PLAN_MONTH || type == Constants.FOLDER_PLAN_WEEK
    						|| type == Constants.FOLDER_PLAN_WORK){
        					name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
        				}
        				//图标
        				String icon = docMimeTypeManager.getDocMimeTypeById(res.getMimeTypeId()).getIcon();
        				if (res.getIsFolder()){
        					icon = icon.substring(0, icon.indexOf("|"));
        				}
        				//链接
        				String link;
        				OPEN_TYPE openType;
        				if(res.getIsFolder()){
        					link = "/doc.do?method=docHomepageIndex&docResId=" + res.getId();
            				openType = ChessMultiRowThreeColumnTemplete.OPEN_TYPE.href;
        				}else{
        					if(type == Constants.LINK_FOLDER){
            					link = "/doc.do?method=docHomepageIndex&docResId=" + res.getSourceId() + "&parentId=" + res.getParentFrId();
            					openType = ChessMultiRowThreeColumnTemplete.OPEN_TYPE.href;
            				}
            				else{
            					openType = ChessMultiRowThreeColumnTemplete.OPEN_TYPE.openWorkSpace;
                                link = "javascript:openDocLink('/doc.do?method=docOpenIframeOnlyId&docResId="+res.getId()+"')";
            				}
        				}
        				row.setIcon("/apps_res/doc/images/docIcon/" + icon);
    					row.setSubject(name);
        				row.setLink(link);
    					
    					row.setHasAttachments(res.getHasAttachments());
    					row.setOpenType(openType);
    					if(!isNarrow){
    						row.setMaxLength(30);
    						row.setCreateDate(res.getLastUpdate());
    						row.setCreateMemberName(Functions.showMemberNameOnly(res.getLastUserId()));
    					}else{
    						row.setMaxLength(15);
    						row.setCreateDate(res.getLastUpdate());
    						row.setCreateMemberName(Functions.showMemberNameOnly(res.getLastUserId()));
    					}
        			}
        		}
        	}
        	catch (Exception e) {
        		log.error("文档夹栏目数据解析异常：", e);
        	}
        	
        	cmt.setRowNumber(RowNumber);
        	cmt.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=docHomepageIndex&docResId=" + folderIdStr);
        }
		return cmt;
	}
	
	/**
	 * 我的文档我会出现创建者跟登陆者不一样的情况
	 * @param folderId
	 * @param userId
	 * @return
	 */
	private DocResource personalDoc(Long folderId,Long userId){
		DocResource parentRes  = docHierarchyManager.getDocResourceById(folderId);
		if(parentRes.getFrType()==Constants.FORMAT_TYPE_FOLDER_MINE && !parentRes.getCreateUserId().equals(userId)){
			parentRes = docHierarchyManager.getPersonalFolderOfUser(userId);
		}
		return parentRes;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}
	@Override
	public boolean isAllowUserUsed(String singleBoardId) {
        if(Strings.isBlank(singleBoardId)){
        	return false;
        }
        DocResource res = docHierarchyManager.getDocResourceById(Long.parseLong(singleBoardId));
        if(res == null){
        	return false;
        }

        return true;
	}

}