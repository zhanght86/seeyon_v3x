package com.seeyon.v3x.doc.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.dao.DocLibDao;
import com.seeyon.v3x.doc.dao.DocLibMemberDao;
import com.seeyon.v3x.doc.dao.DocLibOwnerDao;
import com.seeyon.v3x.doc.dao.DocListColumnDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.dao.DocSearchConfigDao;
import com.seeyon.v3x.doc.dao.DocTypeListDao;
import com.seeyon.v3x.doc.domain.DocAcl;
import com.seeyon.v3x.doc.domain.DocLib;
import com.seeyon.v3x.doc.domain.DocLibMember;
import com.seeyon.v3x.doc.domain.DocLibOwner;
import com.seeyon.v3x.doc.domain.DocListColumn;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocSearchConfig;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.domain.DocTypeList;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.DocMVCUtils;
import com.seeyon.v3x.doc.util.Constants.OperEnum;
import com.seeyon.v3x.doc.webmodel.DocLibTableVo;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

public class DocLibManagerImpl implements DocLibManager, Observer {
	
	private static final Log log = LogFactory.getLog(DocLibManagerImpl.class);
	private final CacheAccessable factory = CacheFactory.getInstance(DocLibManagerImpl.class);
	private CacheMap<Long,ArrayList<DocMetadataDefinition>> columnTable ;// <文档库id,文档栏目列表>
	private CacheMap<Long,ArrayList<DocMetadataDefinition>> searchConditionTable; // <文档库id,查询条件设置列表>
	// Hashtable<docLibId, List<DocType>>,  List<DocType>来源于 DocTypeList记录
	// 所有关联的DocTypes
	private CacheMap<Long,ArrayList<DocType>> contentTypeTable;//<文档库id,文档库内容类型>
	// “新建”菜单下拉选择的DocType
	private CacheMap<Long,ArrayList<DocType>> contentTypeTableForNew;
	// 文档类的DocType 选择，即新建、编辑页面的选择
	private CacheMap<Long,ArrayList<DocType>> contentTypeTableForDoc;
	// 默认栏目
	private List<DocMetadataDefinition> defaultColumnList = null;
	// 默认查询条件
	private List<DocMetadataDefinition> defaultSearchConditions = null;
	// 默认公文档案库查询条件
	private List<DocMetadataDefinition> defaultEdocSearchConditions = null;
	private boolean initialized = false;
	// 所有公共文档库
	private CacheMap<Long, DocLib> publicDocLibsMap;
	// 文档库管理员集合， 包含个人库 Map<libId, List<memberId>>
	private CacheMap<Long, ArrayList<Long>> docLibOwnersMap;
	
	private DocLibDao docLibDao;
	private ContentTypeManager contentTypeManager;
	private MetadataDefManager metadataDefManager;
	private DocLibOwnerDao ownerDao;
	private DocSpaceManager docSpaceManager;
	private DocHierarchyManager docHierarchyManager;
	private OrgManager orgManager;
	private DocListColumnDao docListColumnDao;
	private DocSearchConfigDao docSearchConfigDao;
	private DocTypeListDao docTypeListDao;
	private DocLibMemberDao docLibMemberDao;
	private DocResourceDao docResourceDao;
	private DefaultListColumn defaultListColumn;
	private DefaultSearchCondition defaultSearchCondition;
	
	public void setDocSearchConfigDao(DocSearchConfigDao docSearchConfigDao) {
		this.docSearchConfigDao = docSearchConfigDao;
	}
	public void setDefaultListColumn(DefaultListColumn defaultListColumn) {
		this.defaultListColumn = defaultListColumn;
	}
	public void setDefaultSearchCondition(DefaultSearchCondition defaultSearchCondition) {
		this.defaultSearchCondition = defaultSearchCondition;
	}
	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}
	public void setDocLibMemberDao(DocLibMemberDao docLibMemberDao) {
		this.docLibMemberDao = docLibMemberDao;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	public void setDocSpaceManager(DocSpaceManager docSpaceManager) {
		this.docSpaceManager = docSpaceManager;
	}
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	public void setDocLibDao(DocLibDao docLibDao) {
		this.docLibDao = docLibDao;
	}
	public void setOwnerDao(DocLibOwnerDao ownerDao) {
		this.ownerDao = ownerDao;
	}
	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}
	public void setDocTypeListDao(DocTypeListDao docTypeListDao) {
		this.docTypeListDao = docTypeListDao;
	}
	public void setDocListColumnDao(DocListColumnDao docListColumnDao) {
		this.docListColumnDao = docListColumnDao;
	}
	
	private void validSystemProjectLib(){
		// 1. 查找项目库
		String hql = "from DocLib where type = " + Constants.PROJECT_LIB_TYPE;
		List<DocLib> libs = this.docLibDao.find(hql);
		if(libs == null || libs.size() == 0){
			log.error("系统缺少项目文档库，请初始化系统数据！");
			return;
		} else {
			boolean hasProject = false;
			for(DocLib lib : libs){
				if(lib.getId().longValue() == Constants.DOC_LIB_ID_PROJECT.longValue()){
					hasProject = true;
					break;
				}
			}
			if(!hasProject){
				log.error("系统缺少项目文档库，请初始化系统数据！");
				return;
			}
		}
		// 2. 看是否有多余的项目库
		for(DocLib lib : libs){
			if(lib.getId().longValue() != Constants.DOC_LIB_ID_PROJECT.longValue()){
				DocResource root = this.docHierarchyManager.getRootByLibId(lib.getId());
				if(root != null){
					List<DocResource> list = this.docHierarchyManager.getAllFirstChildren(root.getId());
					if(list != null)
						for(DocResource dr : list){
							this.docHierarchyManager.moveDocWithoutAcl4Project(dr);		
						}					
				}
				this.deleteInvalidProject(lib.getId(), root);
				log.info("统一项目文档库：" + lib.getId());				
			}
		}
	}
	
	public void initialize() {
		// 2008.06.19 lihf 项目文档库统一
		this.validSystemProjectLib();
		
		//加载文档属性数据到内存
		metadataDefManager.init();
		
		//加载内容类型数据到内存
		contentTypeManager.init();
		
		// 加载文档库显示栏目数据到内存
		this.init();
				
		log.info("文档管理初始化数据加载成功!");
		
		((MetadataDefManagerImpl)metadataDefManager).addObserver((ContentTypeManagerImpl)contentTypeManager);
		((ContentTypeManagerImpl)contentTypeManager).addObserver(this);
	}
	
	@SuppressWarnings("unchecked")
	private synchronized CacheMap createMap(String cacheName){
		if(factory.isExist(cacheName)){
			return factory.getMap(cacheName);
		}
		return factory.createMap(cacheName);
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void init() {
		if (initialized) {
			return ;
		}
		columnTable = createMap("columnTable"); 
		searchConditionTable = createMap("searchConditionTable");
		contentTypeTable = createMap("contentTypeTable");
		contentTypeTableForNew = createMap("contentTypeTableForNew");
		contentTypeTableForDoc = createMap("contentTypeTableForDoc");
		publicDocLibsMap = createMap("publicDocLibsMap");
		docLibOwnersMap = createMap("docLibOwnersMap");
		
		defaultColumnList = this.defaultListColumn.getDefaultListColumns();	
		defaultSearchConditions = this.defaultSearchCondition.getDefaultSearchCondition();
		defaultEdocSearchConditions = this.defaultSearchCondition.getDefaultEdocSearchCondition();
		
		columnTable.clear();
		searchConditionTable.clear();
		contentTypeTable.clear();
		contentTypeTableForNew.clear();
		contentTypeTableForDoc.clear();
		publicDocLibsMap.clear();
		docLibOwnersMap.clear();
//		columnTable = new Hashtable<Long,List<DocMetadataDefinition>>();
//		searchConditionTable = new Hashtable<Long,List<DocMetadataDefinition>>();
//		contentTypeTable = new Hashtable<Long,List<DocType>>();
//		contentTypeTableForNew = new Hashtable<Long,List<DocType>>();
//		contentTypeTableForDoc = new Hashtable<Long,List<DocType>>();
//		publicDocLibsMap = new HashMap<Long, DocLib>();
//		docLibOwnersMap = new HashMap<Long, Set<Long>>();
		List<DocLib> docLibs = null;
		// 装载公共文档库及其显示栏目
		docLibs = docLibDao.getDocLibs();
		if (docLibs == null || docLibs.size() == 0)
			return ;
		
		for (DocLib docLib : docLibs) {
			long docLibId = docLib.getId();
			publicDocLibsMap.put(docLibId, docLib);
			
			// 显示栏目
			if (!docLib.getIsDefault()) {
				this.cacheListColumns(docLibId);
			}
			
			// 查询条件设置
			if(!docLib.getIsSearchConditionDefault()) {
				this.cacheSearchConfigs(docLibId);
			}
			
			List<DocTypeList> docTypeList = docTypeListDao.getDocTypeList(docLibId);
			ArrayList<DocType> contentTypes = new ArrayList<DocType>(); // all
			ArrayList<DocType> contentTypes2 = new ArrayList<DocType>(); // for doc
			ArrayList<DocType> contentTypes3 = new ArrayList<DocType>(); // for new
			if (docTypeList != null && docTypeList.size() > 0) {				
				for (int j = 0; j < docTypeList.size(); j++) {
					DocTypeList temp = docTypeList.get(j);
					DocType docType = contentTypeManager.getContentTypeById(temp.getDocTypeId());
					if(docType.getStatus() != Constants.CONTENT_TYPE_DELETED) {
						contentTypes.add(docType);
					}
					
					if (docType.getParentType() == Constants.CONTENT_CATEGORY_DOCUMENT
							&& docType.getStatus() != Constants.CONTENT_TYPE_DELETED) {
						contentTypes2.add(docType);
					}
					else if(docType.getStatus() != Constants.CONTENT_TYPE_DELETED){
						contentTypes3.add(docType);
					}
				}
			}
			
			DocType typeDoc = contentTypeManager.getContentTypeById(Constants.DOCUMENT);
			contentTypes2.add(typeDoc);
			
			contentTypeTable.put(docLibId, contentTypes);
			contentTypeTableForDoc.put(docLibId, contentTypes2);
			contentTypeTableForNew.put(docLibId, contentTypes3);
		}
		
		// 文档库管理员
		List<DocLibOwner> owners = ownerDao.find("from " + DocLibOwner.class.getCanonicalName() + " order by sortId asc");
		if(owners != null) {
			for(DocLibOwner dlo : owners){
				ArrayList<Long> list = docLibOwnersMap.get(dlo.getDocLibId());
				if(list == null){
					list = new ArrayList<Long>();
					docLibOwnersMap.put(dlo.getDocLibId(), list);
				}
				list.add(dlo.getOwnerId());
				docLibOwnersMap.put(dlo.getDocLibId(), list);
			}
		}
		
		initialized = true;
	}
	
	/**
	 * 同步文档库的查询条件设置
	 * @param docLibId
	 */
	private void cacheSearchConfigs(long docLibId) {
		List<DocSearchConfig> sconfigs = this.docSearchConfigDao.getSearchConfigs4Lib(docLibId);
		if(CollectionUtils.isNotEmpty(sconfigs)) {
			ArrayList<DocMetadataDefinition> metadataDefs = new ArrayList<DocMetadataDefinition>(sconfigs.size());
			for(DocSearchConfig sc : sconfigs) {
				DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(sc.getMetadataDefiniotionId());					
				metadataDefs.add(metadataDef);
			}
			searchConditionTable.put(docLibId, metadataDefs);
		}
		else {
			searchConditionTable.remove(docLibId);
			this.docLibDao.bulkUpdate("update DocLib set isSearchConditionDefault = true where id = ?", null, docLibId);
		}
	}
	
	public synchronized void initPart(OperEnum oper, List<DocLib> libs) {
		if (initialized || libs == null || libs.size() == 0 || oper == null) {
			initialized = true;
			return ;
		}
		
/*		if(columnTable == null)
			columnTable = new Hashtable<Long,List<DocMetadataDefinition>>();*/
/*		if(searchConditionTable == null)
			searchConditionTable = new Hashtable<Long,List<DocMetadataDefinition>>();*/
/*		if(contentTypeTable == null)
			contentTypeTable = new Hashtable<Long,List<DocType>>();*/
/*		if(contentTypeTableForNew == null)
			contentTypeTableForNew = new Hashtable<Long,List<DocType>>();*/
/*		if(contentTypeTableForDoc == null)
			contentTypeTableForDoc = new Hashtable<Long,List<DocType>>();*/
/*		if(publicDocLibsMap == null)
			publicDocLibsMap = new HashMap<Long, DocLib>();
		if(docLibOwnersMap == null)
			docLibOwnersMap = new HashMap<Long, Set<Long>>();*/
		
		
		if(OperEnum.add.equals(oper))
			this.initPartAdd(libs);
		else if(OperEnum.edit.equals(oper))
			this.initPartEdit(libs);
		else if(OperEnum.delete.equals(oper))
			this.initPartDelete(libs);		

		initialized = true;
		
		//发送集群通知
/*		List<Long> docLibIds = FormBizConfigUtils.getIds(libs);
		NotificationManager.getInstance().send(NotificationType.DocLibManagerInitPart,new Object[]{oper,docLibIds});*/
	}
	
	// 个人库增加
	private void initPartAddPersonal(DocLib lib, long memberId){
		if(initialized || lib == null){
			initialized = true;
			return;
		}
		ArrayList<Long> set = docLibOwnersMap.get(lib.getId());
		if(set == null){
			set = new ArrayList<Long>();
			docLibOwnersMap.put(lib.getId(), set);
		}
		set.add(memberId);
		
		initialized = true;
	}
	
	@SuppressWarnings("unchecked")
	private void initPartAdd(List<DocLib> libs){
		for (int i = 0; i < libs.size(); i++) {
			DocLib docLib = libs.get(i);	
			long docLibId = docLib.getId();
			publicDocLibsMap.put(docLibId, docLib);
			
			ArrayList<Long> ovs = new ArrayList<Long>();
			List<DocLibOwner> ols = this.ownerDao.find("from DocLibOwner where docLibId = ? order by sortId asc", docLibId);
			if(ols != null){
				for(DocLibOwner dlo : ols){
					ovs.add(dlo.getOwnerId());
				}
			}
			docLibOwnersMap.put(docLibId, ovs);
			
			if (!docLib.getIsDefault()) {
				cacheListColumns(docLibId);
			}
			
			if(!docLib.getIsSearchConditionDefault()) {
				cacheSearchConfigs(docLibId);
			}
			
			List<DocTypeList> docTypeList = docTypeListDao.getDocTypeList(docLibId);
			ArrayList<DocType> contentTypes = new ArrayList<DocType>(); // all
			ArrayList<DocType> contentTypes2 = new ArrayList<DocType>(); // for doc
			ArrayList<DocType> contentTypes3 = new ArrayList<DocType>(); // for new
			if (docTypeList != null && docTypeList.size() > 0) {				
				for (int j = 0; j < docTypeList.size(); j++) {
					DocTypeList temp = docTypeList.get(j);
					DocType docType = contentTypeManager.getContentTypeById(temp.getDocTypeId());
					if(docType.getStatus() != Constants.CONTENT_TYPE_DELETED)
					contentTypes.add(docType);
					if (docType.getParentType() == Constants.CONTENT_CATEGORY_DOCUMENT
							&& docType.getStatus() != Constants.CONTENT_TYPE_DELETED) {
						contentTypes2.add(docType);
					}
					else if(docType.getStatus() != Constants.CONTENT_TYPE_DELETED){
						contentTypes3.add(docType);
					}
				}
			}
			
			DocType typeDoc = contentTypeManager.getContentTypeById(Constants.DOCUMENT);
			contentTypes2.add(typeDoc);
			
			contentTypeTable.put(docLibId, contentTypes);
			contentTypeTableForDoc.put(docLibId, contentTypes2);
			contentTypeTableForNew.put(docLibId, contentTypes3);
		}
	}
	private void cacheListColumns(long docLibId) {
		List<DocListColumn> columns = docListColumnDao.findColumnByOrderNum(docLibId);
		if (columns != null && columns.size() > 0) {
			ArrayList<DocMetadataDefinition> metadataDefs = new ArrayList<DocMetadataDefinition>();
			for (int j = 0; j < columns.size(); j++) {
				DocListColumn column = columns.get(j);
				long metadataDefId = column.getMetadataDefiniotionId();
				DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(metadataDefId);					
				metadataDefs.add(metadataDef);
			}
			columnTable.put(docLibId, metadataDefs);
		}else{
			columnTable.remove(docLibId);
			this.docLibDao.bulkUpdate("update DocLib set isDefault = true where id = ?", null, docLibId);
		}
	}
	
	private void initPartEdit(List<DocLib> libs){
		this.initPartDelete(libs);
		this.initPartAdd(libs);
	}
	
	private void initPartDelete(List<DocLib> libs){
		for(DocLib lib : libs){
			initPartDelete(lib);
		}

	}
	
	private void initPartDelete(DocLib lib) {
		columnTable.remove(lib.getId());
		searchConditionTable.remove(lib.getId());
		contentTypeTable.remove(lib.getId());
		contentTypeTableForNew.remove(lib.getId());
		contentTypeTableForDoc.remove(lib.getId());
		publicDocLibsMap.remove(lib.getId());
		docLibOwnersMap.remove(lib.getId());
	}

	private void initVirtualLib(long domainId){
		this.deleteListColumn(domainId);
		this.deleteSearchConfigs(domainId);
		try {
			this.addDocTypeList(domainId, new ArrayList<List>());
		} catch (DocException e) {
			
		}
		
		initialized = true;
	}
	
	public long addDocLib(DocLib doclib, Long domainId, List<Long> owners) throws DocException {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		doclib.setIdIfNew();
		doclib.setType(Constants.USER_CUSTOM_LIB_TYPE);
		
		doclib.setCreateUserId(-1L);
		doclib.setCreateTime(now);
		doclib.setLastUserId(-1L);
		doclib.setLastUpdate(now);
		doclib.setDomainId(domainId);
		doclib.setOrderNum(this.getMaxDocLibOrder(domainId)+1);	
		
		DocLib virtualLib = docLibDao.get(domainId);
		boolean isDefault = (virtualLib == null ? true : virtualLib.getIsDefault());
		boolean isSearchConditionDefault = (virtualLib == null ? true : virtualLib.getIsSearchConditionDefault());
		
		doclib.setIsDefault(isDefault);
		doclib.setIsSearchConditionDefault(isSearchConditionDefault);
		
		docLibDao.save(doclib);
		docLibDao.getSessionFactory().getCurrentSession().flush();
		
		// 添加文档库管理员，处理显示栏目、内容类型和查询条件
		this.addDocLibOwners(doclib.getId(), owners);
		docListColumnDao.batchUpdateDocLibId(domainId, doclib.getId());
		docTypeListDao.batchUpdateDocLibId(domainId, doclib.getId());
		docSearchConfigDao.batchUpdateDocLibId(domainId, doclib.getId());
		
		docLibDao.restoreVirtualLib(domainId);
		
		docHierarchyManager.initCustomLib(doclib.getId(), doclib.getName(), -1L);
		
		// 重新加载文档库显示栏目数据到内存中
		initialized = false;
		initPart(OperEnum.add, Arrays.asList(doclib));
		
		initialized = false;
		this.initVirtualLib(domainId);
		
		return doclib.getId();		
	}

	public boolean addVirtualDocLib(long domainId){
		try {
			DocLib doclib = new DocLib();
			doclib.setId(domainId);
			doclib.setName("virtual_system");
			doclib.setDescription("virtual_system");
			doclib.setType(Constants.USER_CUSTOM_LIB_TYPE);
			
			doclib.setTypeEditable(true);
			doclib.setColumnEditable(true);
			doclib.setListByDefaultOrder(true);
			doclib.setLogView(false);
			doclib.setIsHidden(false);
			doclib.setFolderEnabled(true);
			doclib.setA6Enabled(true);
			doclib.setOfficeEnabled(true);
			doclib.setUploadEnabled(true);
			
			doclib.setIsHidden(false);
			doclib.setCreateUserId(-1L);
			doclib.setCreateTime(new Timestamp(new Date().getTime()));
			doclib.setLastUserId(-1L);
			doclib.setLastUpdate(new Timestamp(new Date().getTime()));
			doclib.setStatus(Constants.DOC_LIB_ENABLED);
			doclib.setIsDefault(true);
			doclib.setIsSearchConditionDefault(true);
			doclib.setOrderNum(0);	
			
			doclib.setDomainId(domainId);
			docLibDao.save(doclib);
			
			// 重新加载文档库显示栏目数据到内存中
			initialized = false;
			initPart(OperEnum.add, Arrays.asList(doclib));
		} catch(Exception e){
			log.error("新建虚拟库时出现异常[单位id=" + domainId +"]：", e);
			return false;
		}
		
		return true;
	}

	/**
	 * 新建个人文档库，创建用户时调用此接口。
	 */
	public DocLib addDocLib(long userId) throws DocException {
		DocLib doc_lib = this.doAddDocLib(userId);
		initialized = false;
		this.initPartAddPersonal(doc_lib, userId);
		return doc_lib;
	}
	
	private DocLib doAddDocLib(Long userId) throws DocException{
		DocLib doclib = new DocLib();
		doclib.setIdIfNew();
		doclib.setName("doc.contenttype.mydoc");
		doclib.setDescription("");
		doclib.setType(Constants.PERSONAL_LIB_TYPE);// 个人文档库
		
		doclib.setTypeEditable(false);		
		doclib.setListByDefaultOrder(true);
		doclib.setColumnEditable(false);
		doclib.setSearchConditionEditable(false);
		doclib.setLogView(false);
		doclib.setFolderEnabled(true);
		doclib.setA6Enabled(true);
		doclib.setOfficeEnabled(true);
		doclib.setUploadEnabled(true);
		
		doclib.setIsHidden(false);
		doclib.setCreateUserId(userId); // 个人文档库的创建者默认为该库的所有者
		doclib.setCreateTime(new Timestamp(new Date().getTime()));
		doclib.setLastUserId(userId);
		doclib.setLastUpdate(new Timestamp(new Date().getTime()));
		doclib.setStatus(Constants.DOC_STATUS);
		doclib.setIsDefault(true);
		doclib.setIsSearchConditionDefault(true);
		doclib.setOrderNum(0);			//个人文档库的顺序统一为0
		docLibDao.save(doclib);
		docLibDao.getSessionFactory().getCurrentSession().flush();
		// 增加个人文档库根节点
		docHierarchyManager.initPersonalLib(doclib.getId(), doclib.getName(), userId);
		this.addDocLibOwners(doclib.getId(), userId); // 对个人文档库授权
		docSpaceManager.addDocSpace(userId, 0L, 0L); // 分配空间
		
		log.debug("新建用户userId="+userId+"时,成功新建了该用户的个人文档库!");		
		
		return doclib;
	}
	
	public void addSysDocLibs(long domainId) throws DocException {
		// 导入导出时，没有当前用户
		// 并且这个创建用户已经失去意义，系统下只能取到系统管理员
		Long userId = -1l;
		long nowTime = System.currentTimeMillis();
		// 创建单位文档库
		DocLib docLib = new DocLib();
		docLib.setIdIfNew();
		docLib.setName("doc.contenttype.danweiwendang");		
		docLib.setType(Constants.ACCOUNT_LIB_TYPE);
		docLib.setDescription("");
		
		docLib.setA6Enabled(true);
		docLib.setOfficeEnabled(true);
		docLib.setFolderEnabled(true);
		docLib.setUploadEnabled(true);
		docLib.setColumnEditable(true);
		docLib.setSearchConditionEditable(true);
		docLib.setListByDefaultOrder(true);
		docLib.setLogView(false);
		
		docLib.setIsDefault(true);
		docLib.setIsSearchConditionDefault(true);
		docLib.setIsHidden(false);
		docLib.setTypeEditable(true);
		docLib.setStatus(Constants.DOC_LIB_ENABLED);
		
		docLib.setCreateTime(new Timestamp(nowTime));
		docLib.setCreateUserId(userId);
		docLib.setLastUpdate(new Timestamp(nowTime));
		docLib.setLastUserId(userId);
		docLib.setOrderNum(1);
		docLib.setDomainId(domainId);		
		docLibDao.save(docLib);
		
		//增加单位文档库根节点
		docHierarchyManager.initCorpLib(docLib.getId(), docLib.getName(), userId);
		
		// 创建公文档案库
		DocLib docLib2 = new DocLib();
		docLib2.setIdIfNew();
		docLib2.setName("doc.contenttype.gongwendangan");
		docLib2.setType(Constants.EDOC_LIB_TYPE);
		docLib2.setDescription("");
		
		docLib2.setA6Enabled(false);
		docLib2.setOfficeEnabled(false);
		docLib2.setFolderEnabled(false);
		docLib2.setUploadEnabled(false);
		docLib2.setColumnEditable(true);
		docLib2.setSearchConditionEditable(true);
		docLib2.setListByDefaultOrder(true);
		docLib2.setLogView(true);
		
		docLib2.setIsDefault(false);
		docLib2.setIsSearchConditionDefault(true);
		docLib2.setIsHidden(false);
		docLib2.setTypeEditable(false);
		docLib2.setStatus(Constants.DOC_LIB_ENABLED);
		
		docLib2.setCreateTime(new Timestamp(nowTime));
		docLib2.setCreateUserId(userId);
		docLib2.setLastUpdate(new Timestamp(nowTime));
		docLib2.setLastUserId(userId);
		docLib2.setOrderNum(3);
		docLib2.setDomainId(domainId);
		docLibDao.save(docLib2);
		
		// 08.09.17 公文档案库修改默认栏目
		this.initEdocLibColumn(docLib2.getId());
		
		// 添加公文档案库根节点及待归档公文目录
		docHierarchyManager.initArcsLib(docLib2.getId(), docLib2.getName(), userId);
		
		log.info("成功新建单位id=" + domainId + "的单位、公文文档库!");
		
		// 重新加载文档库显示栏目数据到内存中
		initialized = false;
		initPart(OperEnum.add, Arrays.asList(docLib, docLib2));
	}
	
	// 初始化公文库的显示栏目
//	公文档案默认显示的栏目改为：
//	依次为：文件密级、名称、公文文号、发文单位、大小、修改时间
//	INSERT INTO `doc_list_columns` VALUES ('2297742928612312004', '-8504476490460345464', '8', '5');
//	INSERT INTO `doc_list_columns` VALUES ('2524989101889896631', '-8504476490460345464', '4', '4');
//	INSERT INTO `doc_list_columns` VALUES ('9096159212938217757', '-8504476490460345464', '136', '3');
//	INSERT INTO `doc_list_columns` VALUES ('2166683770136775158', '-8504476490460345464', '131', '2');
//	INSERT INTO `doc_list_columns` VALUES ('3611846396406614753', '-8504476490460345464', '2', '1');
//	INSERT INTO `doc_list_columns` VALUES ('2787270725440127732', '-8504476490460345464', '133', '0');
	private void initEdocLibColumn(long docLibId){
		Long[] defIds = new Long[]{133L, 2L, 131L, 136L, 4L, 8L};
		for(int i = 0; i < defIds.length; i++){
			DocListColumn docList = new DocListColumn();
			docList.setIdIfNew();
			docList.setDocLibId(docLibId);
			docList.setMetadataDefiniotionId(defIds[i]); // DocDetail ID
			docList.setOrderNum(i); // 排列的顺序
			docListColumnDao.save(docList);
		}
	}
	
	public String getAccountDocLibName(Long accountId) throws DocException {
		for(Iterator<Entry<Long, DocLib>> ite = publicDocLibsMap.toMap().entrySet().iterator(); ite.hasNext();) {
			Entry<Long, DocLib> entry = ite.next();
			DocLib lib = entry.getValue();
			if(lib.isAccountLib() && lib.getDomainId() == accountId) {
				return Constants.getDocI18nValue(lib.getName());
			}
		}
		throw new DocException("按照[id='" + accountId + "']无法查找到与之对应的单位文档库");
	}
	
	public void deleteOrgDocLibs(long domainId) throws DocException {
		docLibDao.deleteDocLibsByDomainId(domainId);
	}

	public DocLib getDocLibById(long id) {
		this.validInit();
		DocLib lib = publicDocLibsMap.get(id);
		if(lib != null)
			return lib;
		else
			return docLibDao.get(id);
	}
	/**
	 * 获取文档库的详细信息列表
	 * @param ids docLib的ID列表。
	 * @return
	 */
	public List<DocLib> getDocLibByIds(List<Long> ids){
		return docLibDao.getDocLibByIds(ids);
	}
	
	private void validInit(){
		if (!initialized) {
			init();
		}
	}

	public void modifyDocLib(DocLib docLib, String name) throws DocException {
		if(!docLib.getName().equals(name.trim())) {
			this.docResourceDao.updateRootFolderName(docLib.getId(), name);
		}
		
		docLib.setName(name);
		this.modifyDocLib(docLib);
	}
	
	public void modifyDocLib(DocLib docLib) throws DocException {
		docLib.setLastUserId(CurrentUser.get().getId()); // 设置最后的更改人员
		docLib.setLastUpdate(new Timestamp(new Date().getTime())); // 设置最后的更改时间
		docLibDao.update(docLib);
		
		initialized = false;
		List<DocLib> alist = new ArrayList<DocLib>();
		alist.add(docLib);
		initPart(OperEnum.edit, alist);
	}

	public void addDocLibOwners(long docLibId, long... userId) {
		if (userId == null || userId.length == 0)
			return;
		
		List<DocLibOwner> owners = new ArrayList<DocLibOwner>(userId.length);
		for (int i = 0; i < userId.length; i++) {
			owners.add(new DocLibOwner(docLibId, userId[i], i));
		}
		ownerDao.savePatchAll(owners);
	}

	public void addDocLibOwners(long docLibId, List<Long> userIds) {
		if (CollectionUtils.isEmpty(userIds))
			return;
		
		List<DocLibOwner> owners = new ArrayList<DocLibOwner>(userIds.size());
		for (int i = 0; i < userIds.size(); i++) {
			owners.add(new DocLibOwner(docLibId, userIds.get(i), i));
		}
		ownerDao.savePatchAll(owners);
	}

	public void deleteDocLibOwners(long docLibId) {
		this.ownerDao.bulkUpdate("delete from DocLibOwner where docLibId = ?", null, docLibId);
	}

	public void setListColumnOrder(List<List> list) {
		for (int i = 0; i < list.size(); i++) {
			List the_list = list.get(i);
			if (the_list.isEmpty() == false) {
				long id = (Long) the_list.get(0);
				int order = (Integer) the_list.get(1);
				DocListColumn doc = docListColumnDao.get(id);
				doc.setOrderNum(order);
				docListColumnDao.update(doc);
			}
		}

	}

	public void deleteDocLib(long id) throws DocException {
		try {
			DocLib doc_lib = this.getDocLibById(id); // 获取要删除的自定义文档库
			if (doc_lib.getType() != Constants.USER_CUSTOM_LIB_TYPE.byteValue()) {
				log.warn("不允许对系统文档库进行删除操作!");
				throw new DocException("无权删除该文档库");
			} 
			else {
				if (!docHierarchyManager.isLibOnlyRoot(id)) {
					throw new DocException("DocLang.doc_lib_delete_doclib");
				}
			}
			this.deleteDocLibOwners(id);		//手动删除文档库所有者
			this.deleteListColumn(id);			//手动删除栏目列表
			this.deleteDocTypeList(id);			//手动删除类型列表
			this.deleteSearchConfigs(id);		//手动删除查询条件列表
			docHierarchyManager.removeDocWithoutAcl(docHierarchyManager.getRootByLibId(id), CurrentUser.get().getId(), true);
			
			docLibMemberDao.bulkUpdate("delete from DocLibMember where docLibId = ?", null, id);
			docLibDao.delete(doc_lib);
		} catch (DocException e) {
			log.error("删除文档库[id=" + id + "]的过程中出现异常：", e);
		}
	}
	
	public void deleteInvalidProject(long id, DocResource root)  {	
			this.deleteListColumn(id);			//手动删除栏目列表
			this.deleteDocTypeList(id);			//手动删除类型列表
			this.deleteSearchConfigs(id);		//手动删除查询条件列表
			if(root != null)
				docHierarchyManager.getDocResourceDao().bulkUpdate("delete from DocResource where id = ?", null, root.getId());
			
			docLibMemberDao.bulkUpdate("delete from DocLibMember where docLibId = ?" , null , id);
			docLibDao.bulkUpdate("delete from DocLib where id = ? " , null , id);		
	}

	public void deleteUserDocLib(long userId) throws DocException {
		DocLib doc_lib = this.getPersonalLibOfUser(userId);
		if(doc_lib == null)
			return;
		
		((DocHierarchyManagerImpl) docHierarchyManager).emptyLib(doc_lib.getId(), userId);
		docLibDao.deleteObject(doc_lib);
	}
	
	public List<DocLib> getDocLibs(long domainId) {
		List<DocLib> list = new ArrayList<DocLib>();
		this.validInit();
		for(DocLib dl : publicDocLibsMap.values()){
			if(dl.isEnabled() && (dl.getDomainId() == domainId && dl.getId().longValue() != domainId)|| dl.getType() == Constants.GROUP_LIB_TYPE.byteValue()
					|| dl.getType() == Constants.PROJECT_LIB_TYPE.byteValue()){
				if(dl.getType() != Constants.EDOC_LIB_TYPE.byteValue())
					list.add(dl);
				else if(Constants.edocModuleEnabled())
					list.add(dl);
				
			}
		}
		Collections.sort(list);
		
		return list;
	}
	
	/**
	 * 非个人库，非集团库，非虚拟库，排序
	 */
	public List<DocLib> getDocLibsWithoutGroupLib(long domainId){
		return this.getDocLibsWithoutGroupLib(domainId, Constants.DOC_LIB_ALL);
	}

	public List<DocLib> getDocLibsByUserId(long userId, long domainId)  {
		List<DocLib> docLibs = new ArrayList<DocLib>();
		docLibs.add(this.getPersonalLibOfUser(userId));
		// 获取所有得自定义及公共文档库
		List<DocLib> list = this.getCommonDocLibsByUserId(userId, domainId);		
		docLibs.addAll(list);
		
		return docLibs;
	}
	
	public DocLib getPersonalLibOfUser(long userId){
		DocLib lib = getPersonalLibOfUser1(userId);
		if(lib == null){
			try {
				lib = this.doAddDocLib(userId);
				
				initialized = false;
				this.initPartAddPersonal(lib, userId);
				
			} catch (DocException e) {
				log.error("新建个人[id=" + userId + "]文档库时出现异常 ", e);
			}
		}
		
		return lib;
	}
	
	/**
	 * 取得某个人的个人文档库
	 * 
	 */
	private DocLib getPersonalLibOfUser1(long userId){
		String hql = "select dl from DocLib dl, DocLibOwner dlo where dl.id = dlo.docLibId and dlo.ownerId = ? and dl.type = "
				+ Constants.PERSONAL_LIB_TYPE;
		List<DocLib> list = this.docLibDao.find(hql, userId);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
	
		return null;
	}
	/**
	 * 取单位文档库
	 * @param userId
	 * @return
	 */
	public DocLib getDeptLibById(long domainId){
		
		String hql = "select dl from DocLib dl where dl.domainId = ? and dl.type = "
			+ Constants.ACCOUNT_LIB_TYPE;
		List<DocLib> list = this.docLibDao.find(hql, domainId);
		if(list != null && list.size() > 0){
			return list.get(0);
		}
	
		return null;
		
		
	}

	// 根据用户ID获取用户能够查阅的文档库（不包含个人文档库）
	public List<DocLib> getCommonDocLibsByUserId(long userId, long domainId) {
		List<DocLib> theDocLibs = new ArrayList<DocLib>();
		// 取得本单位所有公共库
		List<DocLib> docLibs = this.getDocLibs(domainId);
		theDocLibs.addAll(docLibs);

		String userInfo = Constants.getOrgIdsOfUser(userId);

		// 删除自定义的非成员库
		List<Long> memberLibs = docLibDao.getAllMember(userInfo);		//按自定义文档库的顺序得到所有有权限的库	

		if(!CurrentUser.get().isAdministrator()){
			for (int i = 0; i < docLibs.size(); i++) {
				DocLib docLib = docLibs.get(i);											
				if (docLib.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue()) {					
					if (memberLibs == null || memberLibs.size() == 0) {
						theDocLibs.remove(docLib);
					}
					else {
						if(!memberLibs.contains(docLib.getId()))
							theDocLibs.remove(docLib);
					}
				}
			}
		}
		
		//用户能够查阅的外单位单位文档库和自定义文档库 added by Meng Young at 2009-08-21
		List<DocLib> otherAccountLibs = this.getDocLibsFromOtherAccountBySharing(userId, domainId);
		if(otherAccountLibs!=null && otherAccountLibs.size()>0) {
			theDocLibs.addAll(otherAccountLibs);
		}
		
		Collections.sort(theDocLibs);
		
		return theDocLibs;
	}
	
	public List<DocLib> getAllPartDocResouces(byte type,User user) throws Exception {
		return getAllPartDocResouces(user,true,type) ;
	}
	
	public List<DocLib> getAllPartDocResouces(User user) throws Exception{
		return getAllPartDocResouces(user ,true) ;
	}

	@SuppressWarnings("unchecked")
	private List<DocLib> getAllPartDocResouces(User user,boolean flag,Byte... type) throws Exception{
		if(user == null){
			return null ;
		}
		StringBuffer hql = new StringBuffer() ;
		Map<String, Object> namedParameters = new HashMap<String, Object>(); 
		
		hql.append("from " + DocLib.class.getName() + " lib where lib.domainId in(:orgids)") ;
		
		if(type != null && type.length > 0){
			hql.append(" and lib.type in (:types)") ;
			namedParameters.put("types", FormBizConfigUtils.parseArr2List(type)) ;
		}
		
		List<V3xOrgAccount> list = orgManager.concurrentAccount(user.getId()) ;
		namedParameters.put("orgids", FormBizConfigUtils.getEntityIds(list));
		
		return this.docLibDao.find(hql.toString(), -1, -1, namedParameters);
	}
	
	/**
	 * 获取用户能够查阅的外单位单位文档库和自定义文档库
	 * @param userId   当前登录用户ID
	 * @param domainId 当前登录用户所在单位ID
	 */
	@SuppressWarnings("unchecked")
	public List<DocLib> getDocLibsFromOtherAccountBySharing(long userId, long domainId) {
		Map<String, Object> namedParameters = new HashMap<String, Object>(); 
		String hql = "select distinct lib from " + DocLib.class.getName() + " lib, " + 
					 DocResource.class.getName() + " res, " + DocAcl.class.getName() + " acl " +
					 "where lib.id=res.docLibId and res.id=acl.docResourceId and lib.domainId != :userAccountId " +
					 "and acl.userId in (:ids) ";
		if(!CurrentUser.get().isInternal()) {
			hql += " and acl.userType != :account ";
			namedParameters.put("account", V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
		}
		hql += " and ((acl.sharetype in (1,3) and acl.sdate<=:tdate and acl.edate>=:tdate) or acl.sharetype in (0,2))";
		hql += " and acl.potenttype != :nopotent " +
			   " and lib.status = :enabled and (lib.type<:projectLibType and lib.type<>:personalLibType) " +
			   " order by lib.orderNum";
		
		namedParameters.put("userAccountId", domainId);
		namedParameters.put("ids", Constants.getOrgIdsOfUser1(userId));
		//取消权限之后，数据库仍有一条权限记录，其权限为无权限，需将此情况排除在外
		namedParameters.put("nopotent", Constants.NOPOTENT);
		namedParameters.put("tdate",new Date());
		namedParameters.put("projectLibType", Constants.PROJECT_LIB_TYPE);
		namedParameters.put("personalLibType", Constants.PERSONAL_LIB_TYPE);
		namedParameters.put("enabled", Constants.DOC_LIB_ENABLED);
		
		return this.docLibDao.find(hql, -1, -1, namedParameters);
	}

	public void addDocTypeList(long docLibId, List<List> docTypeId) throws DocException {
		List<DocTypeList> list = docTypeListDao.findBy("docLibId", docLibId);
		if (list.isEmpty() == false) {
			this.deleteDocTypeList(docLibId);
		}
		for (int i = 0; i < docTypeId.size(); i++) {
			List _list=docTypeId.get(i);
			if(_list.isEmpty()==false){
				long theId=(Long)_list.get(0);
				int order=(Integer)_list.get(1);
				DocTypeList typeList = new DocTypeList();
				typeList.setIdIfNew();
				typeList.setDocTypeId(theId);
				typeList.setDocLibId(docLibId);
				typeList.setOrderNum(order);
				docTypeListDao.save(typeList);
			}	
		}
		// 重新状态文档库显示栏目数据到内存中		
		List<DocLib> alist = new ArrayList<DocLib>();
		alist.add(this.getDocLibById(docLibId));
		initialized = false;
		initPart(OperEnum.edit, alist);
	}


	public void deleteDocTypeList(long docLibId) {
		this.docTypeListDao.bulkUpdate("delete from DocTypeList where docLibId = ?", null, docLibId);
	}

	public List<DocType> getContentTypes(long docLibId) {
		if (!initialized) {
			init();
		}
		return contentTypeTable.get(docLibId);
	}	
	
	public List<DocType> getContentTypesForNew(long docLibId) {
		if (!initialized) {
			init();
		}
		
		return contentTypeTableForNew.get(docLibId);
	}
	
	public List<DocType> getContentTypesForDoc(long docLibId) {
		if (!initialized) {
			init();
		}
		
		return contentTypeTableForDoc.get(docLibId);
	}
	
	public List<DocType> getValidContentTypesForDoc(long docLibId) {
		List<DocType> _contentTypes = this.getContentTypesForDoc(docLibId);
		List<DocType> contentTypes = null;
		if (CollectionUtils.isNotEmpty(_contentTypes)) {
			contentTypes = new ArrayList<DocType>(_contentTypes.size());
			for (DocType type : _contentTypes) {
				if (type.getStatus() != Constants.CONTENT_TYPE_DELETED) {
					contentTypes.add(type);
				}
			}
		}
		return contentTypes;
	}

	// 根据doc_type_id获取所对应的类型详细表对象
	private List<DocTypeDetail> getMetadateByDocTypeId(long docTypeId) {
		List list = new ArrayList();
		DocType docType = contentTypeManager.getContentTypeById(docTypeId);
		Set set = docType.getDocTypeDetail();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			DocTypeDetail detail = (DocTypeDetail) it.next();
			list.add(detail);
		}

		return list;
	}

	// 获取相同的类型详细表对象
	private Set getDocDetailsId(List list) {
		if (list.isEmpty())
			return null;
		List<DocTypeDetail> _list = new ArrayList<DocTypeDetail>();
		Set set = new HashSet();
		for (int i = 0; i < list.size(); i++) {
			List<DocTypeDetail> the_list = (List) list.get(i);
			for (int j = 0; j < the_list.size(); j++) {
				DocTypeDetail detail = (DocTypeDetail) the_list.get(j);
				_list.add(detail);
			}
		}
		for (int k = 0; k < _list.size();) {
			int temp = 0;
			DocTypeDetail detail2 = (DocTypeDetail) _list.get(k);
			if (set.contains(detail2)) {
				k++;
			} else {
				for (int m = k + 1; m < _list.size(); m++) {
					DocTypeDetail detail3 = (DocTypeDetail) _list.get(m);
					if (detail2.getMetadataDefId() == 
							detail3.getMetadataDefId())
						temp = temp + 1;
				}
				// 判断对象detail2存在于每一个type类型中
				if (temp == list.size() - 1) {
					set.add(detail2);
				}
				k++;
			}

		}
		return set;
	}

	public void setDocTypeView(List<List> list) {
		for(int i = 0; i < list.size(); i++) {
			List the_list = list.get(i);
			if(!the_list.isEmpty()) {
				long id = (Long)the_list.get(0);
				int order = (Integer)the_list.get(1);
				DocTypeList doc = docTypeListDao.get(id);
				doc.setOrderNum(order);
				docTypeListDao.update(doc);
			}
		}
	}

	public void deleteListColumn(long docLibId, long... listColumnId) {
		if (listColumnId.length == 0) {
			docListColumnDao.delete(new Object[][]{{"docLibId", docLibId}});
		}
	}
	
	public void deleteSpecificColumn(long docMetadataDefId) {
		String hql = "delete from DocListColumn where metadataDefiniotionId = ?";
		docListColumnDao.bulkUpdate(hql, null, docMetadataDefId);
	}
	
	public void deleteSpecificSearchConfig(long docMetadataDefId) {
		String hql = "delete from DocSearchConfig where metadataDefiniotionId = ?";
		docSearchConfigDao.bulkUpdate(hql, null, docMetadataDefId);
	}
	
	public List<DocMetadataDefinition> getListColumnsByDocLibId(long docLibId, boolean isDefaultColumn) {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> listColumns = null;
		if (isDefaultColumn) {
			listColumns = defaultColumnList;
		}
		else {
			listColumns = columnTable.get(docLibId);
		}
		return listColumns;
	}
	
	public List<DocMetadataDefinition> getListColumnsByDocLibId(long docLibId) {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> listColumns = columnTable.get(docLibId);
		if(CollectionUtils.isNotEmpty(listColumns))
			return listColumns;
		
		return defaultColumnList;
	}

	public DocMetadataDefinition getDocMetadataDefByDetailId(long detailId) {
		long id = contentTypeManager.getMetadataDefIdByDocDetailId(detailId);
		DocMetadataDefinition define = metadataDefManager.getMetadataDefById(id);
		return define;
	}
	
	public void setDocSearchConditions(Long docLibId, List<Long> searchConditions) {
		DocLib lib = this.getDocLibById(docLibId);
		if(lib.getIsSearchConditionDefault()) {
			lib.setIsSearchConditionDefault(false);
			docLibDao.update(lib);
		}
		else {
			this.deleteSearchConfigs(docLibId);
		}
		
		if(CollectionUtils.isNotEmpty(searchConditions)) {
			List<DocSearchConfig> sconfigs = new ArrayList<DocSearchConfig>(searchConditions.size());
			int order = 0;
			for(Long sConditionId : searchConditions) {
				sconfigs.add(new DocSearchConfig(sConditionId, docLibId, order));
				order ++;
			}
			this.docSearchConfigDao.savePatchAll(sconfigs);
		}
		
		initialized = false;
		this.initPart(OperEnum.edit, Arrays.asList(lib));
	}

	public void setDocListColumn(long docLibId, List<List> list) {
		DocLib doc_lib = this.getDocLibById(docLibId);
		if (doc_lib.getIsDefault() == true) {
			for (int i = 0; i < list.size(); i++) {
				List the_list=list.get(i);
				if(the_list.isEmpty()==false){
					long theId=(Long)the_list.get(0);
					int order=(Integer)the_list.get(1);
					DocListColumn docList = new DocListColumn();
					docList.setIdIfNew();
					docList.setDocLibId(docLibId);
					docList.setMetadataDefiniotionId(theId); // metadataDefinitionId 
					docList.setOrderNum(order); // 排列的顺序
					docListColumnDao.save(docList);
				}
			}

			doc_lib.setIsDefault(false);
			docLibDao.update(doc_lib);
		} else {
			this.deleteListColumn(docLibId);
			for (int i = 0; i < list.size(); i++) {
				List the_list=list.get(i);
				if(the_list.isEmpty()==false){
					long theId=(Long)the_list.get(0);
					int order=(Integer)the_list.get(1);
					DocListColumn docList = new DocListColumn();
					docList.setIdIfNew();
					docList.setDocLibId(docLibId);
					docList.setMetadataDefiniotionId(theId); // DocDetail ID
					docList.setOrderNum(order); // 排列的顺序
					docListColumnDao.save(docList);
				}
				
			}
		}
		// 重新状态文档库显示栏目数据到内存中
		initialized = false;
		List<DocLib> alist = Arrays.asList(doc_lib);
		this.initPart(OperEnum.edit, alist);
	}
	
	/**
	 * 将文档库栏目设为默认
	 */
	public String[] setListColumnToDefault(Long docLibId){
		DocLib doc_lib = this.getDocLibById(docLibId);

		this.deleteListColumn(docLibId);
		doc_lib.setIsDefault(true);
		docLibDao.update(doc_lib);
		
		// 重新加载文档库显示栏目数据到内存中
		initialized = false;
		this.initPart(OperEnum.edit, Arrays.asList(doc_lib));
		
		List<DocMetadataDefinition> listColumns = this.getDefaultColumnList();
		return DocMVCUtils.setDocMetadataDefinitionNames(listColumns, doc_lib.getType());
		
	}

	public Map<Long, List<Long>> getDocLibOwnersByIds(List<Long> docLibIds) {
		// 进行组织模型实体有效性判断
		Map<Long, List<Long>> mapRet = new HashMap<Long, List<Long>>();
		for(Long id : docLibIds){
			mapRet.put(id, new ArrayList<Long>());
		}
		
		this.validInit();
		Set<Long> keyset = docLibOwnersMap.keySet();
		for(Long key : keyset){
			if(docLibIds.contains(key)){
				ArrayList<Long> set = docLibOwnersMap.get(key);
				if(set == null)
					continue;
				for(Long oid : set){
					if(Constants.isValidOrgEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, oid))
						mapRet.get(key).add(oid);
				}	
			}
		}
		return mapRet;
	}

	/**
	 * @deprecated 废弃，使用{@link #getDocLibOwnersByIds(List)}
	 */
	public Map<Long, List<Long>> getDocLibOwnersByIds(String docLibIds) {
		return this.getDocLibOwnersByIds(FormBizConfigUtils.parseStr2Ids(docLibIds));
	}

	public void deleteDocLibs(List<Long> ids) throws DocException{
		if(ids.isEmpty())
			return;
		
		for(int i=0;i<ids.size();i++){
			DocLib doclib=this.getDocLibById(ids.get(i));
			if (!docHierarchyManager.isLibOnlyRoot(doclib.getId())) {
				throw new DocException("DocLang.doc_lib_delete_doclib&" + doclib.getName());
			}
			this.deleteDocLib(doclib.getId());
		}
		// 重新状态文档库显示栏目数据到内存中
		initialized = false;
		List<DocLib> alist = new ArrayList<DocLib>();
		for(Long id : ids){
			DocLib lib = new DocLib();
			lib.setId(id);
			alist.add(lib);
		}
		initPart(OperEnum.delete, alist);
	}

	public void setDefaultListColumnOrder(long docLibId, List<List> list) {
		DocLib docLib=this.getDocLibById(docLibId);
		for(int i=0;i<list.size();i++){
			List the_list=(List)list.get(i);
			if(the_list.isEmpty()==false){
				DocListColumn docList=new DocListColumn();
				long theId=(Long)the_list.get(0);
				int orderNum=(Integer)the_list.get(1);
				docList.setIdIfNew();
				docList.setDocLibId(docLibId);
				docList.setMetadataDefiniotionId(theId);
				docList.setOrderNum(orderNum);
				docListColumnDao.save(docList);
			}
		}
		
		docLib.setIsDefault(false);
		docLibDao.update(docLib);
		
	}

	public List<DocLib> getDocLibsByUserIdNav(long userId, long domainId)  {
		return this.getDocLibsByUserId(userId, domainId);		
	}

	public void deleteDocTypeListByTypeId(long docTypeId) {
		docTypeListDao.deleteDocTypeListByTypeId(docTypeId);
	}
	
	// 获取库的最大的orderNum
	private int getMaxDocLibOrder(long domainId) {		
		this.validInit();
		int ret = 0;
		for(DocLib dl : publicDocLibsMap.values()){
			if(dl.getDomainId() == domainId && dl.getOrderNum() > ret)
				ret = dl.getOrderNum();
		}
		
		return ret;
	}

	private DocLib getNearDocLib(DocLib lib, boolean upper, long domainId){
		this.validInit();
		DocLib ret = lib;
		int orderNum = lib.getOrderNum();
		// 记录最上最下的文档库
		DocLib up = null;
		DocLib down = null;
		for(DocLib dl : publicDocLibsMap.values()){
			if(dl.getDomainId() == domainId && dl.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue()){
				if(up == null)
					up = dl;
				if(down == null)
					down = dl;
				if(upper){
					if(dl.getOrderNum() < orderNum){
						if(ret.getId().longValue() == lib.getId().longValue())
							ret = dl;
						else if(ret.getOrderNum() < dl.getOrderNum())
							ret = dl;
					}
				}else{
					if(dl.getOrderNum() > orderNum){
						if(ret.getId().longValue() == lib.getId().longValue())
							ret = dl;
						else if(ret.getOrderNum() > dl.getOrderNum())
							ret = dl;
					}
				}
				if(dl.getOrderNum() > down.getOrderNum())
					down = dl;
				if(dl.getOrderNum() < up.getOrderNum())
					up = dl;
			}
		}
		
		if(ret.getId().longValue() == lib.getId().longValue()){
			if(upper)
				ret = down;
			else
				ret = up;
		}
		
		return ret;
	}

	/** 
	 * 重新进行排序
	 */
	public void moveDocLib(List<List> list) {
		//得到所有的公共的最大的排序号
		int num = list.size()  ;
        int orderNum[] = new int[num] ;
		for (int i = 0; i < list.size(); i++) {
			List the_list = list.get(i);
			if (the_list.isEmpty() == false) {
				long id = (Long) the_list.get(0);
				//int order = (Integer) the_list.get(1);
				DocLib docLib = this.getDocLibById(id);
				orderNum[i] = docLib.getOrderNum() ;
			}
		}
		//对数组中的数据进行排序
		for(int i = 0 ; i< orderNum.length ; i++){
			for(int j = i+1 ; j< orderNum.length ;j++){
				if(orderNum[i] > orderNum[j]){
					int tmp = orderNum[i] ;
					orderNum[i] = orderNum[j] ;
					orderNum[j] = tmp ;
				}
			}
		}
		
		/**
		 * 修改排序号
		 */
		for (int i = 0; i < list.size(); i++) {
			List the_list = list.get(i);
			if (the_list.isEmpty() == false) {
				long id = (Long) the_list.get(0);
				DocLib docLib = this.getDocLibById(id);
				docLib.setOrderNum(orderNum[i]) ;
				docLibDao.update(docLib) ;
			}
		}
	}
	
	
	public void moveDocLib(long docLibId, long domainId, boolean up) {
		DocLib docLib=this.getDocLibById(docLibId);
		int tempNumber=docLib.getOrderNum();
		DocLib nearDocLib=this.getNearDocLib(docLib, up, domainId);
		if (nearDocLib != null) {
			if(nearDocLib.getType() != Constants.USER_CUSTOM_LIB_TYPE.byteValue()
					|| nearDocLib.getId().longValue() == docLibId){
				return ;
			}
			docLib.setOrderNum(nearDocLib.getOrderNum());
			nearDocLib.setOrderNum(tempNumber);
			docLibDao.update(docLib);
			docLibDao.update(nearDocLib);
		}
	}
	

	public DocLib getOwnerDocLibByUserId(long userId) {
		return this.getPersonalLibOfUser(userId);
	}
	
	public void update(Observable obj,Object arg) {
		// 只有删除才会 notifyObservers()
		if(arg == null || !(arg instanceof List))
			return;
		
		List list = (List)arg;
		initialized = false;
		for(Object o : list){
			if(o instanceof DocType){
				DocType t = (DocType)o;
				Set<Long> keyset = contentTypeTable.keySet();
				for(Long tl : keyset){
					contentTypeTable.get(tl).remove(t);
					contentTypeTableForNew.get(tl).remove(t);
					contentTypeTableForDoc.get(tl).remove(t);
				}

			}else if(o instanceof DocMetadataDefinition){
				DocMetadataDefinition t = (DocMetadataDefinition)o;
				Set<Long> keyset = columnTable.keySet();
				for(Long tl : keyset){
					List<DocMetadataDefinition> alist = columnTable.get(tl);
					alist.remove(t);
				}
				
				Set<Long> keyset2 = searchConditionTable.keySet();
				for(Long tl : keyset2){
					List<DocMetadataDefinition> alist = searchConditionTable.get(tl);
					alist.remove(t);
				}
			}
		}
		initialized = true;
		//发送集群通知
//		NotificationManager.getInstance().send(NotificationType.DocLibManagerUpdate,new Object[]{obj,arg});
	}
	
	public DocLib getGroupDocLib(){
		this.validInit();
		for(DocLib dl : publicDocLibsMap.values()){
			if(dl.getType() == Constants.GROUP_LIB_TYPE.byteValue())
				return dl;
		}
		return null;
	}
	
	public DocLib getProjectDocLib(){	
		this.validInit();
		for(DocLib dl : publicDocLibsMap.values()){
			if(dl.getType() == Constants.PROJECT_LIB_TYPE.byteValue())
				return dl;
		}
		return null;
	}
	
	/** 个人文档库名称：我的文档 */
	private static final String PERSONAL_DOC_LIB_NAME = Constants.getDocI18nValue(Constants.FOLDER_MINE_KEY);
	
	@Deprecated
	public boolean hasSameNameDocLib(String name, long docLibId){
		String[] result = this.validateDocLibName(name, docLibId);
		return BooleanUtils.toBoolean(result[0]);
	}
	
	public String[] validateDocLibName(String name, long docLibId) {
		if(name != null) {
			if(name.equals(PERSONAL_DOC_LIB_NAME)) {
				String msg = Constants.getDocI18nValue("doclib.name.same.personal");
				return new String[]{String.valueOf(true), msg};
			}
			
			this.validInit();
			DocLib currentLib = this.getDocLibById(docLibId);
			
			for(DocLib lib : publicDocLibsMap.values()) {
				String libName = Constants.getDocI18nValue(lib.getName());
				if(libName.equals(name)){
					if(docLibId == 0l && (lib.isGroupLib() || lib.isPersonalLib() || lib.isProjectLib() || lib.getDomainId() == CurrentUser.get().getLoginAccount()))
						return new String[]{String.valueOf(true), getValidateDocLibNameMsg(lib)};
					
					// 不同类型文档库不允许重名，同类型文档库但所在单位不同允许同名（比如：单位文档、公文档案）
					if(docLibId != 0 && docLibId  != lib.getId().longValue() && 
							(currentLib.getType() != lib.getType() || currentLib.getDomainId() == lib.getDomainId())) {
						return new String[]{String.valueOf(true), getValidateDocLibNameMsg(lib)};
					}
				}
			}
		}
		return new String[]{String.valueOf(false), null};
	}
	
	/**
	 * 文档库名称重复时，给出有效的提示信息供用户判断
	 */
	private String getValidateDocLibNameMsg(DocLib lib) {
		boolean showAccountName = (lib.isAccountLib() || lib.isEdocLib() || lib.isUserCustomizedLib()) 
								  && lib.getDomainId() != CurrentUser.get().getLoginAccount();
		String libName = Constants.getDocI18nValue(lib.getName());
		String accountName = showAccountName ? StringUtils.defaultString(Functions.showOrgAccountName(lib.getDomainId())) : "";
		int libType = (int)lib.getType();
		
		return Constants.getDocI18nValue("doclib.name.same.public", libType, accountName, libName);
	}

	/**
	 * 取得默认显示栏目
	 */
	public List<DocMetadataDefinition> getDefaultColumnList(){
		if(defaultColumnList == null)
			defaultColumnList = this.defaultListColumn.getDefaultListColumns();
		
		return defaultColumnList;
	}
	
	/**
	 * 取消文档库新建
	 */
	public void cancelAdd(){
		if(CurrentUser.get() != null)
			this.initVirtualLib(CurrentUser.get().getLoginAccount());
	}
	
	
	/**
	 * 取得某个单位下某种类型的文档库
	 * 
	 */
	public List<DocLib> getLibsOfAccount(long domainId, byte libType){
		List<DocLib> ret = new ArrayList<DocLib>();
		this.validInit();
		for(DocLib dl : publicDocLibsMap.values()){
			if(dl.getDomainId() == domainId && dl.getType() == libType)
				ret.add(dl);
		}
		
		return ret;
	}

	
	/**
	 * 判断当前用户是否某个库的owner
	 */
	public boolean isOwnerOfLib(Long userId, Long libId) {
		this.validInit();
		ArrayList<Long> set = docLibOwnersMap.get(libId);
		if(set == null)
			return false;
		else
			return set.contains(userId);
	}
	
	/**
	 * 根据docLibId 得到 owners
	 * @param userId
	 * @return
	 */
	public List<Long> getOwnersByDocLibId(long docLibId) {
		this.validInit();
		ArrayList<Long> set = docLibOwnersMap.get(docLibId);
		if(CollectionUtils.isNotEmpty(set)) {
			List<Long> result = new ArrayList<Long>(set.size());
			for(Long ownerId : set) {
				try {
					V3xOrgMember member = this.orgManager.getMemberById(ownerId);
					if(member != null && member.isValid()) {
						result.add(member.getId());
					}
				} catch (BusinessException e) {
					log.warn("查找不到[id=" + ownerId + "]的人员!");
				}
			}
			return result;
		} else {
			return new ArrayList<Long>();
		}
	}
	
	public void setLibMember(Long docLibId, Long userId, String userType) {
		String hsql = "from DocLibMember as a where a.docLibId=? and a.userId=? and a.userType=?";
		List<DocLibMember> list = docLibMemberDao.find(hsql, docLibId, userId, userType);
		if (list == null || list.size() == 0) {
			DocLibMember docm = new DocLibMember();
			docm.setDocLibId(docLibId);
			docm.setIdIfNew();
			docm.setUserId(userId);
			docm.setUserType(userType);
			docLibMemberDao.save(docm);
		}
	}

	public void deleteLibMember(Long docLibId, String userIds) {
		String hql = "delete from DocLibMember as a where a.docLibId=? and a.userId in (:userIds)";
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("userIds", Constants.parseStrings2Longs(userIds, ","));
		
		docLibMemberDao.bulkUpdate(hql, namedParameters, docLibId);
	}

	public boolean isEmpty() {
		Long domainId = CurrentUser.get().getLoginAccount();
		List<DocLib> list = this.getDocLibsWithoutGroupLib(domainId); // 获取所有的自定义文档库和公共文档库
		List<DocLib> docLibs = new ArrayList<DocLib>(); 
		for (int i = 0; i < list.size(); i++) {
			DocLib docLib = list.get(i);
			if (docLib.getType() == Constants.USER_CUSTOM_LIB_TYPE.byteValue()) {
				docLibs.add(docLib);
			}
		}
		return docLibs.isEmpty();
	}
	
	/**
	 * 获取用户管理的文档库id
	 * @param owner
	 * @return          文档库id列表
	 */
	public List<Long> getLibsByOwner(Long owner){
		return this.ownerDao.getLibsByOwner(owner);
	}
	
	public List<DocLib> getDocLibs(boolean isGroup, Long accountId) {
		return this.getDocLibs(isGroup, accountId, Constants.DOC_LIB_ALL);
	}
	
	public List<DocLib> getDocLibs(boolean isGroup, Long accountId, byte status) {
		List<DocLib> docLibs = new ArrayList<DocLib>();
		if(isGroup){
			DocLib lib = this.getGroupDocLib();
			DocLib plib = this.getProjectDocLib();
			if(Constants.validateStatus(lib, status))
				docLibs.add(lib);
			
			if(Constants.validateStatus(plib, status))
				docLibs.add(plib);
		}
		else {
			// 获取所有的自定义文档库和公共文档库
			docLibs = this.getDocLibsWithoutGroupLib(accountId, status); 
		
			if(!Constants.isGroupVer()){
				DocLib plib = this.getProjectDocLib();
				if(Constants.validateStatus(plib, status))
					docLibs.add(plib);
			}
		}
		
		Collections.sort(docLibs);
		return docLibs;
	}
	
	/**
	 * 非个人库，非集团库，非虚拟库，排序
	 * @param accountId		单位ID
	 * @param status		所要获取文档库状态类型，包括：启用、停用、全部(不区分启用还是停用)
	 * @return
	 */
	private List<DocLib> getDocLibsWithoutGroupLib(Long accountId, byte status) {
		List<DocLib> list = new ArrayList<DocLib>();
		this.validInit();
		for(DocLib dl : publicDocLibsMap.values()) {
			if(dl.getDomainId() == accountId && dl.getId().longValue() != accountId && 
				Constants.validateStatus(dl, status) && 
				(dl.getType() != Constants.EDOC_LIB_TYPE || Constants.edocModuleEnabled())) {
				list.add(dl);
			}				
		}
		Collections.sort(list);
		
		return list;
	}
	
	public void disableDocLibs(String docLibIds) {
		this.updateStatus(docLibIds, Constants.DOC_LIB_DISABLED);
	}
	
	private void updateStatus(String docLibIds, byte status) {
		List<Long> ids = FormBizConfigUtils.parseStr2Ids(docLibIds);
		if(CollectionUtils.isNotEmpty(ids)) {
			this.docLibDao.updateStatus(ids, status);
			
			List<DocLib> libs = new ArrayList<DocLib>(ids.size());
			for(Long id : ids) {
				DocLib lib = this.getDocLibById(id);
				lib.setStatus(status);
				libs.add(lib);
			}
			this.initPartEdit(libs);
		}
	}
	
	public void enableDocLibs(String docLibIds) {
		this.updateStatus(docLibIds, Constants.DOC_LIB_ENABLED);
	}
	
	public List<DocLibTableVo> getDocLibTableVOs(List<DocLib> docLibs) {
		List<DocLibTableVo> the_list = new ArrayList<DocLibTableVo>();
		
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(docLibs.size());
		
		for (int i = first; i < first+pageSize; i++) {
			if(i > docLibs.size() - 1) {
				break;
			}
			DocLib docLib = docLibs.get(i);
			DocLibTableVo vo = new DocLibTableVo(docLib);
			List<Long> the_manager = this.getOwnersByDocLibId(docLib.getId());
			String manager_list = "";
			int j = 0;
			for(Long oid : the_manager){
				V3xOrgMember member = null;
				try {
					member = orgManager.getMemberById(oid);
				} catch (BusinessException e) {
					log.error("orgManager取得member", e);
				}
				if (member != null && member.isValid()) {
					if(j != 0) {
						manager_list += ",";
						manager_list += member.getEntityType();
						manager_list += "|";
						manager_list += oid;
						
					}
					else {
						manager_list += member.getEntityType();
						manager_list += "|";
						manager_list += oid;
					}
					j++;
				}
			}
			vo.setManagerName(manager_list);
			//获取库类型
			vo.setDocLibType(Constants.getDocLibType(docLib.getType()));
			the_list.add(vo);
		}
		return the_list;
	}
	
	public List<DocMetadataDefinition> getDefaultSearchConditions() {
		if(defaultSearchConditions == null)
			defaultSearchConditions = this.defaultSearchCondition.getDefaultSearchCondition();
		return defaultSearchConditions;
	}
	
	private List<DocMetadataDefinition> getDefaultSearchConditions(boolean isEdocLib) {
		if(isEdocLib)
			return this.getDefaultEdocSearchConditions();
		else
			return this.getDefaultSearchConditions();
	}
	
	public String[] setSearchConditions2Default(Long docLibId) {
		this.deleteSearchConfigs(docLibId);
		
		DocLib doc_lib = this.getDocLibById(docLibId);
		doc_lib.setIsSearchConditionDefault(true);
		docLibDao.update(doc_lib);
		
		// 重新加载文档库显示栏目数据到内存中
		initialized = false;
		List<DocLib> alist = Arrays.asList(doc_lib);
		this.initPart(OperEnum.edit, alist);
		
		List<DocMetadataDefinition> searchConditions = this.getDefaultSearchConditions(doc_lib.isEdocLib());
		return DocMVCUtils.setDocMetadataDefinitionNames(searchConditions, doc_lib.getType());
	}
	
	/**
	 * 删除文档库对应的搜索条件配置记录
	 * @param docLibId
	 */
	public void deleteSearchConfigs(Long docLibId) {
		docSearchConfigDao.delete(new Object[][]{{"docLibId", docLibId}});
	}
	
	public List<DocMetadataDefinition> getSearchConditions4DocLib(Long docLibId, Byte docLibType) {
		if (!initialized) {
			init();
		}
		
		List<DocMetadataDefinition> searchConditions = searchConditionTable.get(docLibId);
		if(CollectionUtils.isNotEmpty(searchConditions)) {
			return searchConditions;
		}
		
		return this.getDefaultSearchConditions(docLibType == Constants.EDOC_LIB_TYPE);
	}
	
	public List<DocMetadataDefinition> getDefaultEdocSearchConditions() {
		if(defaultEdocSearchConditions == null)
			defaultEdocSearchConditions = this.defaultSearchCondition.getDefaultEdocSearchCondition();
		return defaultEdocSearchConditions;
	}
	
	public boolean isDocLibEnabled(Long docLibId) {
		DocLib lib = this.getDocLibById(docLibId);
		return lib != null && lib.isEnabled();
	}
	
	public List<DocMetadataDefinition> getMiscSearchConditions4DocLib(List<DocMetadataDefinition> selectedConditions) {
		List<DocMetadataDefinition> all = this.metadataDefManager.getAllSearchableMetadataDef();
		FormBizConfigUtils.removeAllIgnoreEmpty(all, selectedConditions);
		return all;
	}
	
}
