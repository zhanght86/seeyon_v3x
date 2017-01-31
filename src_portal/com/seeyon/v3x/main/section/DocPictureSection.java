package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.PictureTemplete;
import com.seeyon.v3x.main.section.templete.PictureTemplete.Picture;
import com.seeyon.v3x.space.domain.PortletEntityProperty.PropertyName;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

public class DocPictureSection extends BaseSection {

	private static final Log log = LogFactory.getLog(DocPictureSection.class);
	DocHierarchyManager docHierarchyManager = null;
	private FileManager fileManager;
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "docPicture";
	}
	
	@Override
	public String getBaseName() {
		return "docPicture";
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	protected String getName(Map<String, String> preference) {
		Long docId = NumberUtils.toLong(preference.get("designated_value"));
		DocResource doc = docHierarchyManager.getDocResourceById(docId);
		String name = null;
		if (doc != null) {
			name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, doc.getFrName());
		} else {
			name = "docPicture";
		}
		return SectionUtils.getSectionName(name, preference);
	}

	@Override
	protected Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	protected BaseSectionTemplete projection(Map<String, String> preference) {
		String panel = SectionUtils.getPanel("designated", preference);
		String columnsStyle = SectionUtils.getColumnStyle("image", preference);

		List<DocResource> list = null;
		Integer currentPage = 0;
		if (Strings.isNotBlank(preference.get("currentPage"))) {
			currentPage = Integer.parseInt(preference.get("currentPage"));
		}
		Pagination.setFirstResult(currentPage * 3);
		Pagination.setMaxResults(3);
		if ("designated".equals(panel)) {
			String designated = preference.get(panel + "_value");
			List<Long> docList = CommonTools.parseStr2Ids(designated);
			Long docId = null;
			if (CollectionUtils.isNotEmpty(docList)) {
				docId = docList.get(0);
				Long userId = CurrentUser.get().getId();
				list = docHierarchyManager.getDocsByTypes(docId, userId,
						Constants.FORMAT_TYPE_ID_UPLOAD_JPG,
						Constants.FORMAT_TYPE_ID_UPLOAD_GIF,
						Constants.FORMAT_TYPE_ID_UPLOAD_PNG);
			}

			if ("image".equals(columnsStyle)) {// 图片播放
				PictureTemplete t = new PictureTemplete();
				
				if (CollectionUtils.isNotEmpty(list)) {
					for (DocResource res : list) {
//						Picture picture = t.addPicture(res.getSourceId(), res.getLastUpdate());
						Picture picture = null;
						try {
							picture = t.addPicture(res.getSourceId(), fileManager.getV3XFile(res.getSourceId()).getCreateDate());
						}
						catch (BusinessException e) {
							log.error("获取文件时出现异常[文件ID= " + res.getSourceId() + "]", e);
						}
						String name = res.getFrName();
						long type = res.getFrType();
						if (type == Constants.FOLDER_MINE
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
								|| type == Constants.FOLDER_PLAN_WORK) {
							name = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, name);
						}
						picture.setSubject(name);
						picture.setLink("javascript:openDocLink('/doc.do?method=docOpenIframeOnlyId&docResId=" + res.getId() + "')");
					}
				}
				
				if (docId != null) {
					t.setTotalPage((Pagination.getRowCount() - 1) / 3 + 1);
					t.addBottomButton("doc_list", "/doc.do?method=docHomepageIndex&docResId=" + docId);
					t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/doc.do?method=moreDocPictures&fragmentId=" + preference.get(PropertyName.entityId.name()) + "&ordinal=" + preference.get(PropertyName.ordinal.name()) + "&folderId=" + docId);
				}
				return t;
			}
		}

		return null;
	}

}