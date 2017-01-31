package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.flag.SysFlag;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.loader.AbstractMapDataLoader;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.dao.ContentTypeDao;
import com.seeyon.v3x.doc.dao.ContentTypeDetailsDao;
import com.seeyon.v3x.doc.dao.DocTypeListDao;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.Constants.OperEnum;

public class ContentTypeManagerImpl extends Observable implements ContentTypeManager, Observer {
	
	private static final Log log = LogFactory.getLog(ContentTypeManagerImpl.class);
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(ContentTypeManager.class);
	// Hashtable<docTypeId, DocType>
	private static CacheMap<Long, DocType> contentTypeTable = cacheFactory.createLinkedMap("ContentTypes");
	// 数据库中所有 DocType
//	private static CacheList<DocType> contentTypes = cacheFactory.createList("ContentTypes");
	// Set<docType.name>
	private static Set<String> docTypeNamesSet = null;
	// Hashtable<docTypeDetailId, docTypeDetail>
	private static Hashtable<Long, DocTypeDetail> detailTable = null;
	// Hashtable<docTypeId, List<DocTypeDetail>>
	private static Hashtable<Long, List<DocTypeDetail>> contentTypeDetailTable = null;
	private static boolean initialized = false;
	
	private ContentTypeDao contentTypeDao;
	private ContentTypeDetailsDao contentTypeDetailsDao;	
	private DocTypeListDao docTypeListDao;
	private MetadataDefManager metadataDefManager;
		
	public ContentTypeDetailsDao getContentTypeDetailsDao() {
		return contentTypeDetailsDao;
	}

	public void setContentTypeDetailsDao(ContentTypeDetailsDao contentTypeDetailsDao) {
		this.contentTypeDetailsDao = contentTypeDetailsDao;
	}

	public ContentTypeDao getContentTypeDao() {
		return contentTypeDao;
	}

	public void setContentTypeDao(ContentTypeDao contentTypeDao) {
		this.contentTypeDao = contentTypeDao;
	}
	
	public MetadataDefManager getMetadataDefManager() {
		return metadataDefManager;
	}
	
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	
	public synchronized void init() {
		if (initialized) {
			return ;
		}
//		log.info("正在加载所有内容类型定义数据...");
		contentTypeTable.setDataLoader(new AbstractMapDataLoader<Long, DocType>(contentTypeTable) {
			@Override
			protected Map<Long, DocType> loadLocal() {
				Map<Long, DocType> map = new HashMap<Long, DocType>();
				docTypeNamesSet = new HashSet<String>();
				List<DocType> _contentTypes = contentTypeDao.findAll();
				detailTable = new Hashtable<Long,DocTypeDetail>();
				contentTypeDetailTable = new Hashtable<Long,List<DocTypeDetail>>();
				for (int i = 0; i < _contentTypes.size(); i++) {
					DocType docType = _contentTypes.get(i);
					map.put(docType.getId(), docType);			
//					contentTypes.add(docType);		
					initDocType(docType);
				}
				return map;
			}

			private void initDocType(DocType docType) {
				docTypeNamesSet.add(docType.getName());
				Set details = docType.getDocTypeDetail();
				Iterator iterator = details.iterator();
				List<DocTypeDetail> _details = new ArrayList<DocTypeDetail>();
				while (iterator.hasNext()) {
					DocTypeDetail detail = (DocTypeDetail)iterator.next();
					DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(detail.getMetadataDefId());
					detail.setDocMetadataDefinition(metadataDef);
					detailTable.put(detail.getId(), detail);
					_details.add(detail);
				}
				Collections.sort(_details);
				contentTypeDetailTable.put(docType.getId(), _details);
			}

			@Override
			protected DocType loadLocal(Long k) {
				final DocType docType = contentTypeDao.get(k);
				initDocType(docType);
				return docType;
			}
		});
		reload();
	}
	/**
	 *  强制重新加载缓存。
	 */
	public synchronized void reload() {
//		contentTypeTable = new Hashtable<Long, DocType>();
//		contentTypes = new ArrayList<DocType>();
//		contentTypes.clear();

		contentTypeTable.reload();
//		log.info("内容类型定义数据加载成功!");
//		contentTypes.replaceAll(_contentTypes);
		initialized = true;
	}
	
	public synchronized void initPart(OperEnum oper, List<DocType> types) {
		if (initialized || types == null || types.size() == 0 || oper == null) {
			initialized = true;
			return ;
		}
		
/*		if(contentTypeTable == null)
			contentTypeTable = new Hashtable<Long, DocType>();*/
/*		if(contentTypes == null)
			contentTypes = new ArrayList<DocType>();*/
		if(docTypeNamesSet == null)
			docTypeNamesSet = new HashSet<String>();
		if(detailTable == null)
			detailTable = new Hashtable<Long,DocTypeDetail>();
		if(contentTypeDetailTable == null)
			contentTypeDetailTable = new Hashtable<Long,List<DocTypeDetail>>();
			
		if(OperEnum.add.equals(oper))
			this.initPartAdd(types);
		else if(OperEnum.edit.equals(oper))
			this.initPartEdit(types);
		else if(OperEnum.delete.equals(oper))
			this.initPartDelete(types);

//		log.info("内容类型定义数据加载成功!");
		
		initialized = true;
		
//		发送集群通知
/*		List<DocType> DocTypes=new ArrayList<DocType>();
		for(DocType doctype:types){
			//DocTypeIds.add(doctype.getId());
			DocTypes.add(doctype);
		}*/
//		NotificationManager.getInstance().send(NotificationType.ContentTypeInitPart,new Object[]{oper,DocTypes});
	}
	
	//
	public void initPartAdd(List<DocType> types){
		for(DocType docType : types){
			contentTypeTable.put(docType.getId(), docType);			
//			contentTypes.add(docType);		
			docTypeNamesSet.add(docType.getName());
			Set details = docType.getDocTypeDetail();
			Iterator iterator = details.iterator();
			List<DocTypeDetail> _details = new ArrayList<DocTypeDetail>();
			while (iterator.hasNext()) {
				DocTypeDetail detail = (DocTypeDetail)iterator.next();
				DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(detail.getMetadataDefId());
				detail.setDocMetadataDefinition(metadataDef);
				detailTable.put(detail.getId(), detail);
				_details.add(detail);
			}
			Collections.sort(_details);
			contentTypeDetailTable.put(docType.getId(), _details);
		}
//		contentTypes.addAll(types);
	}
	public void initPartEdit(List<DocType> types){
/*		for(DocType dt : types){
			int index=0;
			if((index=contentTypes.indexOf(dt))!=-1){
				contentTypes.remove(index);
				contentTypes.add(index, dt);
			}
			contentTypeTable.put(dt.getId(), dt);		
		}
		this.initStaticNoObj(types);*/
		
		// 和reload相比，就少了一条sql，基本上等同reload了
		// 依赖索引定位的CacheList是不安全的，权衡利弊，直接调用reload
		reload();
	}
	public void initPartDelete(List<DocType> types){	
		for(DocType t : types){
			contentTypeTable.remove(t.getId());
//			contentTypes.remove(t);
		}
//		contentTypes.removeAll(types);
		
		this.initStaticNoObj(types);
	}
	private void initStaticNoObj(List<DocType> types){
		docTypeNamesSet = new HashSet<String>();
		detailTable = new Hashtable<Long,DocTypeDetail>();
		contentTypeDetailTable = new Hashtable<Long,List<DocTypeDetail>>();
//		for (DocType docType : contentTypes.toList()) {
		for (DocType docType : contentTypeTable.values()) {		
			docTypeNamesSet.add(docType.getName());
		
			Set details = docType.getDocTypeDetail();
			Iterator iterator = details.iterator();
			List<DocTypeDetail> _details = new ArrayList<DocTypeDetail>();
			while (iterator.hasNext()) {
				DocTypeDetail detail = (DocTypeDetail)iterator.next();
				DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(detail.getMetadataDefId());
				detail.setDocMetadataDefinition(metadataDef);
				detailTable.put(detail.getId(), detail);
				_details.add(detail);
			}
			Collections.sort(_details);
			contentTypeDetailTable.put(docType.getId(), _details);
		}
	}

	// 删除内容类型
	public void deleteContentType(Long id)  {
		DocType contentType = this.getContentTypeById(id);
		byte status = contentType.getStatus();
		if ((!contentType.getIsSystem()) && (!(status == Constants.CONTENT_TYPE_DELETED))) {// 系统内容类型不能删除
			List<DocType> alist = new ArrayList<DocType>();
			alist.add(contentType);
			if(status == Constants.CONTENT_TYPE_DRAFT){
				DocManager4ISearch isearchManager = (DocManager4ISearch)ApplicationContextHolder.getBean("docManager4ISearch");
				if(isearchManager != null)
				isearchManager.removeContentType(contentType);
				
				contentTypeDao.remove(contentType);
			
				// 重新从数据库装载内容类型及其相关数据到内存中
				initialized = false;

				this.initPart(OperEnum.delete, alist);

			}else if(status == Constants.CONTENT_TYPE_DELETED){
				// 删掉关联
				// DocTypeList
	     		docTypeListDao.deleteDocTypeListByTypeId(id);
				
				contentType.setStatus(Constants.CONTENT_TYPE_DELETED);
				this.updateContentType(contentType,false,null);					
			}	
			
			DocManager4ISearch isearchManager = (DocManager4ISearch)ApplicationContextHolder.getBean("docManager4ISearch");
			if(isearchManager != null)
			isearchManager.removeContentType(contentType);

			
			// 设置观察点
			setChanged();
			notifyObservers(alist);
		}		
	}
	
	//停用内容类型
	public void disableContentType(Long id){
		
		DocType contentType = this.getContentTypeById(id);
	
		docTypeListDao.deleteDocTypeListByTypeId(id);
	
		initialized = false;

	
			contentTypeTable.remove(contentType.getId());
//			contentTypes.remove(contentType);
			//发送通知		
			NotificationManager.getInstance().send(NotificationType.DocDisableContentType,contentType.getId());
		
	}


	// 根据id查找内容类型
	public DocType getContentTypeById(Long id) {
		if (!initialized) {
			init();
		}
		return contentTypeTable.get(id);
	}

	// 新增内容类型
	public void addContentType(DocType contentType, List<DocTypeDetail> contentTypeDetails) {
		contentType.setIdIfNew();
		contentType.setStatus(Constants.CONTENT_TYPE_DRAFT);
		Set<DocTypeDetail> dset = new HashSet<DocTypeDetail>();
		if (contentTypeDetails != null) {// 内容类型详细信息
			for (int i = 0; i < contentTypeDetails.size(); i++) {
				contentTypeDetails.get(i).setIdIfNew();
				contentTypeDetails.get(i).setContentTypeId(contentType.getId());
				dset.add(contentTypeDetails.get(i));
			}
		}
		// 新增内容类型时自动增加系统默认原数据定义
		/*List<DocMetadataDefinition> metadataDefs = metadataDefManager.findDefaultMetadataDef();
		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition dmd = metadataDefs.get(i);
			DocTypeDetail dtd = new DocTypeDetail();
			dtd.setDescription(dmd.getDescription());
			dtd.setContentTypeId(contentType.getId());
			dtd.setIdIfNew();
			dtd.setMetadataDefId(dmd.getId());
			dtd.setName(dmd.getName());
			dtd.setOrderNum(i + 1);
			dset.add(dtd);
		}*/
		contentType.setDocTypeDetail(dset);
		contentTypeDao.save(contentType);
		// 重新从数据库装载内容类型及其相关数据到内存中
		initialized = false;
		List<DocType> alist = new ArrayList<DocType>();
		alist.add(contentType);
		initPart(OperEnum.add, alist);
		
		
		DocManager4ISearch isearchManager = (DocManager4ISearch)ApplicationContextHolder.getBean("docManager4ISearch");
		if(isearchManager != null)
		isearchManager.registerNewContentType(contentType);
		
		//		// 设置观察点
//		setChanged();
//		notifyObservers();
	}

	// 修改保存内容类型
	public void updateContentType(DocType contentType , boolean newName ,String oldName) {
		contentTypeDao.update(contentType);
		// 重新从数据库装载内容类型及其相关数据到内存中
		initialized = false;
		//init();
		List<DocType> alist = new ArrayList<DocType>();
		alist.add(contentType);
		initPart(OperEnum.edit, alist);
		if(newName){
			DocManager4ISearch isearchManager = (DocManager4ISearch)ApplicationContextHolder.getBean("docManager4ISearch");
			if(isearchManager != null)
			isearchManager.updateContentType(contentType,oldName);
		}
		if(contentType.getSeartchStatus() == 1){
			DocManager4ISearch isearchManager = (DocManager4ISearch)ApplicationContextHolder.getBean("docManager4ISearch");
			if(isearchManager != null)
			isearchManager.removeContentType(contentType);
			
		}else if(contentType.getSeartchStatus() == 0){
			DocManager4ISearch isearchManager = (DocManager4ISearch)ApplicationContextHolder.getBean("docManager4ISearch");
			if(isearchManager != null)
			isearchManager.registerNewContentTypeRep(contentType);			
		}
		
		// 设置观察点
		setChanged();
		notifyObservers();
	}
	
	/**
	 * 修改内容类型标记为使用状态
	 */
	public void setContentTypePublished(long id){
		this.contentTypeDao.setContentTypePublished(id);
	}

	// 根据文档类型详细信息id取元数据id
	public Long getMetadataDefIdByDocDetailId(Long id) {
		if (!initialized) {
			init();
		}
		DocTypeDetail detail = detailTable.get(id);
		return detail.getMetadataDefId();		
	}

	// 获取所有内容类型(内容类型管理中调用)
	public List<DocType> getContentTypes() {
		if (!initialized) {
			init();
		}
		List<DocType> _contentTypes = new ArrayList<DocType>();
/*		for (int i = 0; i < contentTypes.size(); i++) {
			DocType docType = contentTypes.get(i);*/
		for (DocType docType : contentTypeTable.values()) {
			if (docType.getParentType() != Constants.CONTENT_CATEGORY_FOLDER){
		//获取已经停用的内容类型
		//			&& docType.getStatus() != Constants.CONTENT_TYPE_DELETED) {              
				_contentTypes.add(docType);
			}
		}
		return _contentTypes;
	}	
	
	public List<DocType> getContentTypesForNew() {
		if (!initialized) {
			init();
		}
		List<DocType> _contentTypes = new ArrayList<DocType>();
/*		for (int i = 0; i < contentTypes.size(); i++) {
			DocType docType = contentTypes.get(i);*/
		for (DocType docType : contentTypeTable.values()) {		
			if (docType.getEditable() 
					&& docType.getParentType() != Constants.CONTENT_CATEGORY_FOLDER
					&& docType.getStatus() != Constants.CONTENT_TYPE_DELETED
					&& docType.getId().longValue() != Constants.DOCUMENT) {
				_contentTypes.add(docType);
			}
		}
		return _contentTypes;
	}	
	
	public List<DocType> getContentTypesForISearch() {
		if (!initialized) {
			init();
		}
		List<DocType> _contentTypes = new ArrayList<DocType>();
/*		for (int i = 0; i < contentTypes.size(); i++) {
			DocType docType = contentTypes.get(i);*/
		for (DocType docType : contentTypeTable.values()) {		
			if (docType.getEditable() 
					&& docType.getParentType() != Constants.CONTENT_CATEGORY_FOLDER) {
				_contentTypes.add(docType);
			}
		}
		return _contentTypes;
	}	
	
	/**
	 * 获取所有可查询内容类型
	 * 
	 * @return
	 */
	public List<DocType> getAllSearchContentType() {	

		if (!initialized) {
			init();
		}
		Set<Long> types = new HashSet<Long>();
		types.add(Constants.FOLDER_MINE);
		types.add(Constants.FOLDER_ARC_PRE);
		types.add(Constants.ROOT_ARC);
		types.add(Constants.FOLDER_CORP);
		types.add(Constants.FOLDER_PROJECT_ROOT);		
		types.add(Constants.FOLDER_PLAN);
		types.add(Constants.FOLDER_TEMPLET);
		types.add(Constants.FOLDER_SHARE);
		types.add(Constants.FOLDER_BORROW);
		types.add(Constants.FOLDER_PLAN_DAY);
		types.add(Constants.FOLDER_PLAN_MONTH);
		types.add(Constants.FOLDER_PLAN_WEEK);
		types.add(Constants.FOLDER_PLAN_WORK);
		types.add(Constants.ROOT_GROUP);
		
		List<DocType> ret = new ArrayList<DocType>();
		List<DocType> list = new ArrayList<DocType>();
//		for(DocType t : contentTypes.toList()) {
		for (DocType t : contentTypeTable.values()) {
			Long type = t.getId();
			boolean isGOV = (Boolean)(SysFlag.sys_isGovVer.getFlag());
			if(isGOV){
				if(Constants.SYSTEM_FORM_KEY.equals(t.getName()) && !SystemEnvironment.hasPlugin("form")) {
					continue;
				}
			}
			if(!types.contains(type))
				list.add(t);
			
		}
		for(DocType t : list){
			if(t.getSeartchStatus()==0&&t.getId()!=Constants.FOLDER_BORROWOUT&&t.getId()!=Constants.FOLDER_SHAREOUT) ret.add(t);
		}
		
					
		
		return ret;
	}

//	public List<DocMetadataDefinition> getMetadataDefinitionByDocTypeId(long docTypeId) {
//		if (!initialized) {
//			init();
//		}
//		List<DocMetadataDefinition> metadataDefs = new ArrayList<DocMetadataDefinition>();
//		DocType docType = getContentTypeById(docTypeId);
//		Set details = docType.getDocTypeDetail();
//		Iterator iterator = details.iterator();
//		while(iterator.hasNext()) {
//			DocTypeDetail detail = (DocTypeDetail)iterator.next();			
//			DocMetadataDefinition metadataDef = detail.getDocMetadataDefinition();
//			metadataDefs.add(metadataDef);
//		}
//		return metadataDefs;
//	}
	
	public List<DocTypeDetail> getContentTypeDetails(long contentTypeId) {
		if (!initialized) {
			init();
		}
		return contentTypeDetailTable.get(contentTypeId);
	}
	
	/**
	 * 判断一种内容类型是否有扩展元数据
	 */
	public boolean hasExtendMetadata(long type) {
		List<DocTypeDetail> list = this.getContentTypeDetails(type);
		for(DocTypeDetail detail:list) {
			DocMetadataDefinition metadataDef = detail.getDocMetadataDefinition();			
			if(metadataDef != null && metadataDef.getIsDefault() == false) {
				return true;
			}
		}
		return false;
	}
	
	private List<DocType> getDocTypes(String name) {
		if (!initialized) {
			init();
		}
		List<DocType> docTypes = new ArrayList<DocType>();	
/*		for (int i = 0; i < contentTypes.size(); i++) {
			DocType docType = contentTypes.get(i);*/
		for (DocType docType : contentTypeTable.values()) {		
			if (docType.getName().equals(name)) {
				docTypes.add(docType);
			}
		}
		return docTypes;
	}

	public boolean containDocType(String typeName) {
		List<DocType> list = getDocTypes(typeName);
		
		if (list != null && !list.isEmpty()) {
			// 需要进行状态判断
			for(DocType d : list){
				if(d.getStatus() != Constants.CONTENT_TYPE_DELETED)
					return true;
			}
		}
		
//		String resource = "com.seeyon.v3x.doc.resources.i18n.DocResource";
		Set<String> keys = ResourceBundleUtil.getKeys(Constants.RESOURCE_BASENAME, typeName);
		if (keys != null && !keys.isEmpty()) {
			for(String k : keys){
				if(docTypeNamesSet.contains(k))
					return true;
			}
		}
		return false;					
	}
	
	public boolean containDocType(String typeName, long typeId) {				
		DocType docType = getContentTypeById(typeId);
		if (docType.getName().equals(typeName)) {
			return false;
		}
		else {
			List<DocType> list = getDocTypes(typeName);
			if (list != null && !list.isEmpty()) {
				// 需要进行状态判断
				for(DocType d : list){
					if((d.getStatus() != Constants.CONTENT_TYPE_DELETED)
							&& (d.getId() != typeId))
						return true;
				}
			}
		}
		Set<String> keys = ResourceBundleUtil.getKeys(Constants.RESOURCE_BASENAME, typeName);
		if (keys != null && !keys.isEmpty()) {
			for(String k : keys){
				if(docTypeNamesSet.contains(k))
					return true;
			}
		}
		return false;
	}
	
	public void update(Observable obj,Object arg) {
//		 只有删除才会 notifyObservers()
		if(arg == null || !(arg instanceof List))
			return;
		
		List<DocMetadataDefinition> alist = (List<DocMetadataDefinition>)arg;
		
		// 根据物理、逻辑删除区分对待
		initialized = false;
		for(DocMetadataDefinition t : alist){
			DocMetadataDefinition theDef = this.getMetadataDefManager().getMetadataDefById(t.getId());
						
			if(theDef == null){
				// 物理
				boolean hasTheDetail = this.hasTheDetail(t);
				if(hasTheDetail){
					// 文档属性修改，重新装载自己

					this.initPartAboutDetail(t);
				}
			}else {
				// 逻辑
			}
		}
		initialized = true;
		

		//设置观察点，通知更新文档库
		setChanged();
		notifyObservers(alist);
	}

	public DocTypeListDao getDocTypeListDao() {
		return docTypeListDao;
	}

	public void setDocTypeListDao(DocTypeListDao docTypeListDao) {
		this.docTypeListDao = docTypeListDao;
	}
	
	// 
	private List<DocType> getTypesNeedEdit(List<DocMetadataDefinition> dmfs){
		Set<Long> keyset = detailTable.keySet();
		Set<DocType> typeSet = new HashSet<DocType>();
		for(Long id : keyset){
			DocTypeDetail dtd = detailTable.get(id);
			for(DocMetadataDefinition t : dmfs){
				if(dtd.getMetadataDefId() == t.getId().longValue()){
					typeSet.add(this.getContentTypeById(dtd.getContentTypeId()));
					break;
				}
			}
			
		}
		List<DocType> typeList = new ArrayList<DocType>();
		typeList.addAll(typeSet);
		
		return typeList;
	}
	private boolean hasTheDetail(DocMetadataDefinition dmf){
		Set<Long> keyset = detailTable.keySet();
		for(Long id : keyset){
			DocTypeDetail dtd = detailTable.get(id);
			
				if(dtd.getMetadataDefId() == dmf.getId().longValue()){
					return true;
				}
			
			
		}
		
		return false;
	}

		
	//
//	private void initPartFromDB(List<DocType> types){
//		if(types == null || types.size() == 0){
//			initialized = true;
//			return;
//			
//		}
//		String ids = "";
//		for(DocType t : types){
//			ids += "," + t.getId();
//		}
//		
//		List<DocType> list = contentTypeDao.findByIds(ids.substring(1, ids.length()));
//		if(list == null || list.size() == 0){
//			initialized = true;
//			return;
//		}
//			
//		this.initPartDelete(list);
//		this.initPartAdd(list);
//		
//		initialized = true;
//		
//		log.info("内容类型定义数据加载成功!");
//	}
	
	public void initPartAboutDetail(DocMetadataDefinition dmf){		

		
		//
		Hashtable<Long, DocTypeDetail> map1 = detailTable;
		detailTable = new Hashtable<Long,DocTypeDetail>();
		Set<Long> keyset1 = map1.keySet();
		for(Long t1 : keyset1){
			if(map1.get(t1).getMetadataDefId() != dmf.getId().longValue())
				detailTable.put(t1, map1.get(t1));
		}
		
		//
		Set<Long> keyset2 = contentTypeDetailTable.keySet();
		for(Long t2 : keyset2){
			List<DocTypeDetail> list = contentTypeDetailTable.get(t2);
			List<DocTypeDetail> list2 = new ArrayList<DocTypeDetail>();
			for(DocTypeDetail td : list){
				if(td.getMetadataDefId() != dmf.getId().longValue())
					list2.add(td);
			}
			contentTypeDetailTable.put(t2, list2);
		}
				
		//
//		for(DocType t : contentTypes.toList()){
		for (DocType t : contentTypeTable.values()) {
			Set<DocTypeDetail> set = t.getDocTypeDetail();
			Set<DocTypeDetail> set2 = new HashSet<DocTypeDetail>();
			if(set == null)
				set = new HashSet<DocTypeDetail>();
			for(DocTypeDetail dtd : set){
				if(dtd.getMetadataDefId() != dmf.getId().longValue())
					set2.add(dtd);
			}
			
			set.clear();
			set.addAll(set2);
			
		}
//		发送集群通知
		NotificationManager.getInstance().send(NotificationType.ContentTypeInitPartAboutDetail,dmf.getId());
		
	}
	
	/**
	 * 判断一个内容类型是否已经使用或者停用
	 */
	public boolean isUsed(long docTypeId){
		DocType dt = this.getContentTypeById(docTypeId);
		if(dt == null)
			return false;
		else
			return (dt.getStatus() == Constants.CONTENT_TYPE_PUBLISHED||dt.getStatus() == Constants.CONTENT_TYPE_DELETED);
	}
}
