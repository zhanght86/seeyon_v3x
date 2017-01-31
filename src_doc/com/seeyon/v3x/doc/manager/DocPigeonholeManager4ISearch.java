package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ISearchAppObject;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.doc.domain.DocMetadataObject;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;


/**
 * 综合查询单独处理已经归档数据
 */
public class DocPigeonholeManager4ISearch extends ISearchManager {
	
	private DocHierarchyManager docHierarchyManager;
	
	private ContentTypeManager contentTypeManager;
	
	private DocMetadataManager docMetadataManager;	

	public DocMetadataManager getDocMetadataManager() {
		return docMetadataManager;
	}

	public void setDocMetadataManager(DocMetadataManager docMetadataManager) {
		this.docMetadataManager = docMetadataManager;
	}

	public ContentTypeManager getContentTypeManager() {
		return contentTypeManager;
	}

	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}

	public DocHierarchyManager getDocHierarchyManager() {
		return docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}


	@Override
	public Integer getAppEnumKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAppShowName() {
		// TODO Auto-generated method stub
		return ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY;
	}

	@Override
	public int getSortId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ResultModel> iSearch(ConditionModel cModel) {
		List<ResultModel> ret = new ArrayList<ResultModel>();
		// 1. 解析条件
		// 2. 分页查询
        ISearchAppObject appObj = cModel.getAppObj();
        Long typeId = null;
        if(appObj != null){
            typeId = Constants.getContentTypeIdByAppEnumKey(appObj.getAppEnumKey());
        }
		if(typeId == null)
			return new ArrayList<ResultModel>();
		DocType docType = contentTypeManager.getContentTypeById(typeId);
		if(docType == null)
			return new ArrayList<ResultModel>();
		List<DocResource> list = docHierarchyManager.iSearchPiged(cModel, docType);
		// 3. 组装数据，返回
		if(list != null)
		for(DocResource dr : list){
//			String title = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, dr.getFrName());	
			List<DocMetadataObject> dmos = dr.getMetadataList();
			if(dmos == null || dmos.size() == 0){
				dmos = docMetadataManager.getContentProperties(dr.getId());
				dr.setMetadataList(dmos);							
			}
			Long sendId = dr.getCreateUserId();
			Date sendTime = dr.getCreateTime();
			for (DocMetadataObject dmo : dmos) {
				if (dmo.getMetadataDefId() == Constants.METADATA_SENDER_ID){
					sendId = (Long)dmo.getMetadataValue();
				}else if(dmo.getMetadataDefId() == Constants.METADATA_SENDTIME_ID){
					sendTime = (Date)dmo.getMetadataValue();
				}
			}
			
			String fromUserName = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, sendId, false);
			String location = docHierarchyManager.getPhysicalPath(dr.getLogicalPath(), ISearchManager.LOCATION_PATH_SEPARATOR);
			String link = "/doc.do?method=docOpenIframeOnlyId&docResId=" + dr.getId();
			String bodyType = Constants.getBodyType(dr.getMimeTypeId());
			boolean hasAttachments = dr.getHasAttachments();
			ResultModel rm = new ResultModel(dr.getFrName(), fromUserName, sendTime, location, link,bodyType,hasAttachments);
			ret.add(rm);
		}
			
		return ret;
	}
	

	
}
