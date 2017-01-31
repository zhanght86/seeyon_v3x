package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.cap.isearch.model.ISearchAppObject;
import com.seeyon.cap.isearch.model.ResultModel;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.isearch.manager.ISearchManagerRegister;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 综合查询文档
 */
public class DocManager4ISearch extends ISearchManager {
	// 2008.03.19 lihf 
	// 目的：使用一个实现完成所有知识管理内容类型的查询
	
	public static final int DOC_TYPES_4_ISEARCH_SORTID_BEGIN = 100;
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(DocManager4ISearch.class);
	// Map<appShowName, DocType>
//	public static Map<String, DocType> docTypes4ISearchMap = new HashMap<String, DocType>();
	private static CacheMap<String, DocType> docTypes4ISearchMap = cacheFactory.createMap("DocTypes4ISearchMap");
	
	private transient DocHierarchyManager docHierarchyManager;
	
//	private ContentTypeManager contentTypeManager;

//	public ContentTypeManager getContentTypeManager() {
//		return contentTypeManager;
//	}
//
//	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
//		this.contentTypeManager = contentTypeManager;
//	}

	@Override
	public Integer getAppEnumKey() {
		// TODO Auto-generated method stub
		return ApplicationCategoryEnum.doc.getKey();
	}

	@Override
	public String getAppShowName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSortId() {
		// TODO Auto-generated method stub
		return this.getAppEnumKey();
	}

	@Override
	public List<ResultModel> iSearch(ConditionModel cModel) {
		List<ResultModel> ret = new ArrayList<ResultModel>();
		// 1. 解析条件
		// 2. 分页查询
		DocType docType = DocManager4ISearch.getDocTypeByShowName(cModel.getAppKey());
		if(docType == null)
			return new ArrayList<ResultModel>();
		List<DocResource> list = docHierarchyManager.iSearch(cModel, docType);
		// 3. 组装数据，返回
		if(list != null)
		for(DocResource dr : list){
//			String title = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, dr.getFrName());			
			String fromUserName = Constants.getOrgEntityName(V3xOrgEntity.ORGENT_TYPE_MEMBER, dr.getCreateUserId(), false);
			String location = docHierarchyManager.getPhysicalPath(dr.getLogicalPath(), ISearchManager.LOCATION_PATH_SEPARATOR);
			String link = "/doc.do?method=docOpenIframeOnlyId&docResId=" + dr.getId();
	        String bodyType = Constants.getBodyType(dr.getMimeTypeId());
			boolean hasAttachments = dr.getHasAttachments();
			ResultModel rm = new ResultModel(dr.getFrName(), fromUserName, dr.getCreateTime(), location, link,bodyType,hasAttachments);
			ret.add(rm);
		}
			
		return ret;
	}
	

	public DocHierarchyManager getDocHierarchyManager() {
		return docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	
	public void initDoc(){
		// 找到所有可以查询的内容类型，自己注册
		List<DocType> types4ISearch = docHierarchyManager.getContentTypeManager().getContentTypesForISearch();
		Collections.sort(types4ISearch);
		int sortId = DocManager4ISearch.DOC_TYPES_4_ISEARCH_SORTID_BEGIN;
		this.registerDocTypes(types4ISearch, sortId);
	}
	
	// 
	private void registerDocTypes(List<DocType> types, int beginSort){
		if(types == null || types.size() == 0)
			return;
		
//		boolean hasDel = false;
//		String deleteFlag = "";
		int sortId = beginSort;
		Map<String, Integer> subfixMap = new HashMap<String, Integer>();
		for(DocType dt : types){
			sortId++;
			String showName = dt.getName();
			ISearchAppObject obj = null;
			try {
				obj = new ISearchAppObject(null, showName, sortId, this, showName);
				if(obj != null&&dt.getSeartchStatus()!=1){
					obj.setNeedDocLibSelect(true);
					ISearchManagerRegister.registerISearchManager(obj);
					docTypes4ISearchMap.put(showName, dt);
					//发送通知		
//					NotificationManager.getInstance().send(NotificationType.DocRegisterContentType,dt.getId());
				}
			} catch (Exception e) {
			}

		}
	}
	
	public static DocType getDocTypeByShowName(String showName){
		return docTypes4ISearchMap.get(showName);
	}

	/**
	 * 注册新的内容类型
	 */
	public void registerNewContentType(DocType docType){
		if(docType == null)
			return;
		List<DocType> list = new ArrayList<DocType>();
		list.add(docType);
		DocType old = docTypes4ISearchMap.get(docType.getName());
		if(old != null)	{	
			try {
				docTypes4ISearchMap.remove(docType.getName());
				
				ISearchAppObject obj = new ISearchAppObject(null, docType.getName(), 0, this, docType.getName());
				ISearchManagerRegister.deleteISearchManager(obj);	
//				发送通知		
//				NotificationManager.getInstance().send(NotificationType.Doc4ISearchRemove,docType.getName());
				
			} catch (Exception e) {				
			}
			list.add(old);			
		}
		
		int sortId = DocManager4ISearch.DOC_TYPES_4_ISEARCH_SORTID_BEGIN + docTypes4ISearchMap.size() + 1;
		this.registerDocTypes(list, sortId);
	}
	/**
	 * 更新的信息
	 * @param docType
	 */
	public void registerNewContentTypeRep(DocType docType){
		if(docType == null)
			return;
		List<DocType> list = new ArrayList<DocType>();
		list.add(docType);
		DocType old = docTypes4ISearchMap.get(docType.getName());
		if(old != null)	{	
			try {
				docTypes4ISearchMap.remove(docType.getName());
				
				ISearchAppObject obj = new ISearchAppObject(null, docType.getName(), 0, this, docType.getName());
				ISearchManagerRegister.deleteISearchManager(obj);
			//	发送通知		
//				NotificationManager.getInstance().send(NotificationType.Doc4ISearchRemove,docType.getName());
				
			} catch (Exception e) {				
			}	
		}
		
		int sortId = DocManager4ISearch.DOC_TYPES_4_ISEARCH_SORTID_BEGIN + docTypes4ISearchMap.size() + 1;
		this.registerDocTypes(list, sortId);
	}
	
	
	/**
	 * 删除内容类型
	 */
	public void removeContentType(DocType docType){
            // 发送通知
//	    	NotificationManager.getInstance().send(NotificationType.DocRmoveContentType, docType.getName());
			try {
				docTypes4ISearchMap.remove( docType.getName());
				
				ISearchAppObject obj = new ISearchAppObject(null, docType.getName(), 0, this, docType.getName());
				ISearchManagerRegister.deleteISearchManager(obj);	
			} catch (Exception e) {				
			}		
		
	}
	public void updateContentType(DocType docType,String oldName){
		try{
			ISearchAppObject obj = new ISearchAppObject(null, oldName, 0, this, oldName);
			ISearchManagerRegister.deleteISearchManager(obj);	
			ISearchAppObject newObj = new ISearchAppObject(null, docType.getName(), 0, this, docType.getName());
			ISearchManagerRegister.registerISearchManager(newObj);
		}
		catch (Exception e) {				
		}	
		
	}
}
