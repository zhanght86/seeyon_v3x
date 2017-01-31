package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.beans.NotificationDocMetadataDefinition;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.cache.CacheSet;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.doc.dao.MetadataDefDao;
import com.seeyon.v3x.doc.dao.MetadataOptionsDao;
import com.seeyon.v3x.doc.dao.MetadataUseDao;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocMetadataOption;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.util.Constants.OperEnum;
import com.seeyon.v3x.util.Strings;

public class MetadataDefManagerImpl extends Observable implements MetadataDefManager {
	private static final Log log = LogFactory.getLog(MetadataDefManagerImpl.class);
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(MetadataDefManager.class);
	// Hashtable<defId, def>
	private static CacheMap<Long,DocMetadataDefinition> metadataDefTable = cacheFactory.createLinkedMap("MetadataDefTable");
//	private static CacheList<DocMetadataDefinition> metadataDefs = cacheFactory.createList("MetadataDefs");
	// DocMetadataDefinition 的name，判断是否重名的时候用
	private static CacheSet<String> metadataDefNames = cacheFactory.createSet("metadataDefNames");
	private static boolean initialized = false;
	
	// Set<categoryName>
	private static CacheSet<String> categoryNamesSet = cacheFactory.createSet("categoryNamesSet");
	
	private MetadataDefDao metadataDefDao;
	private MetadataUseDao metadataUseDao;
	private MetadataOptionsDao metadataOptionsDao;
	private MetadataManager metadataManager;

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setMetadataOptionsDao(MetadataOptionsDao metadataOptionsDao) {
		this.metadataOptionsDao = metadataOptionsDao;
	}
	public void setMetadataUseDao(MetadataUseDao metadataUseDao) {
		this.metadataUseDao = metadataUseDao;
	}
	public void setMetadataDefDao(MetadataDefDao metadataDefDao) {
		this.metadataDefDao = metadataDefDao;
	}
	
	/**
	 * 系统预置的枚举类型，不同于手动添加的枚举类型，其枚举值需另行获取，包括：公文种类、行文类型、文件密级、紧急程度、保密期限
	 * 其中：保密期限默认不能用于文档属性查询，仍加入以便日后需开放时可直接使用
	 */
	private static Map<Long, MetadataNameEnum> sysMetadata = new HashMap<Long, MetadataNameEnum>();
	private static final Long EDOC_SEND_TYPE = 130l;
	private static final Long EDOC_DOC_TYPE = 129l;
	private static final Long EDOC_SECRET_LEVEL = 133l;
	private static final Long EDOC_URGENT_TYPE = 134l;
	private static final Long EDOC_KEEP_PERIOD = 135l;
	static {
		sysMetadata.put(EDOC_SEND_TYPE, MetadataNameEnum.edoc_send_type);
		sysMetadata.put(EDOC_DOC_TYPE, MetadataNameEnum.edoc_doc_type);
		sysMetadata.put(EDOC_SECRET_LEVEL, MetadataNameEnum.edoc_secret_level);
		sysMetadata.put(EDOC_URGENT_TYPE, MetadataNameEnum.edoc_urgent_level);
		sysMetadata.put(EDOC_KEEP_PERIOD, MetadataNameEnum.edoc_keep_period);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void init() {
		if (initialized) {
			return ;
		}
		metadataDefTable.clear();
		metadataDefNames.clear();
//		metadataDefs.clear();
		final List<DocMetadataDefinition> metaList = metadataDefDao.findAll();
//		metadataDefs.addAll(metaList);
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metaList) {
			Long defId = metadataDef.getId();
			if(sysMetadata.containsKey(defId)) {
				Metadata metadata = this.metadataManager.getMetadata(sysMetadata.get(defId));
				metadataDef.setMetadata(metadata);
			}
			
			metadataDefTable.put(metadataDef.getId(), metadataDef);
			if(metadataDef.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED)
			metadataDefNames.add(metadataDef.getName());
		}
		
		categoryNamesSet.clear();
		List<String> list = metadataDefDao.findAllGroup();			//查询出所有的类别
		for(int i = 0; i < list.size(); i++){
			String temp = list.get(i);
			if(temp == null || temp.equals("")){
				continue;
			}
			else {
				categoryNamesSet.add(temp);
			}
		}
		
		// 标记的修改转移到加载完成，而不是原来的开始
		// 防止正在加载过程中的数据抽取产生错误
		initialized = true;
	}
	
	public synchronized void initPart(OperEnum oper, List<DocMetadataDefinition> defs) {
		if (initialized || defs == null || defs.size() == 0 || oper == null) {
			initialized = true;
			return ;
		}
//		if(metadataDefNames == null)
			metadataDefNames.clear();
//		if(categoryNamesSet == null)
			categoryNamesSet.clear();
		
		if(OperEnum.add.equals(oper))
			this.initPartAdd(defs);
		else if(OperEnum.edit.equals(oper))
			this.initPartEdit(defs);
		else if(OperEnum.delete.equals(oper))
			this.initPartDelete(defs);
		
		// 标记的修改转移到加载完成，而不是原来的开始
		// 防止正在加载过程中的数据抽取产生错误
		initialized = true;
		/**
		 * 发送通知
		 */
		NotificationDocMetadataDefinition bean  = new NotificationDocMetadataDefinition(oper,defs) ;
		NotificationManager.getInstance().send(NotificationType.DocMetadataInitPart, bean);
	}
	
	private void initPartAdd(List<DocMetadataDefinition> defs){
//		for(DocMetadataDefinition t : defs){
//			metadataDefs.add(t);
//			metadataDefNames.add(t.getName());
//			if(Strings.isNotBlank(t.getCategory()))
//				categoryNamesSet.add(t.getCategory());
//		}
		putAll(defs);
		this.initStaticNoObj();
	}
	
	private void initPartEdit(List<DocMetadataDefinition> defs){
		putAll(defs);
		this.initStaticNoObj();
	}
	
	private void putAll(List<DocMetadataDefinition> defs){
		Map<Long,DocMetadataDefinition> map = new HashMap<Long, DocMetadataDefinition>();
		for(DocMetadataDefinition t : defs){
			map.put(t.getId(), t);
		}
		metadataDefTable.putAll(map);
	}
	
	private void initPartDelete(List<DocMetadataDefinition> defs){
		List<Long> idList = new ArrayList<Long>();
		for(DocMetadataDefinition t : defs){
			idList.add(t.getId());
		}
//		metadataDefs.removeAll(defs);
		metadataDefTable.removeAll(idList);
		this.initStaticNoObj();
	}
	
	private void initStaticNoObj(){
		metadataDefNames.clear();
		categoryNamesSet.clear();
//		List<DocMetadataDefinition> l =metadataDefs.toList();
		Collection<DocMetadataDefinition> l = metadataDefTable.values();
		for(DocMetadataDefinition t : l){
			if(t.getStatus().intValue() == Constants.DOC_METADATA_DEF_STATUS_DELETED)
				continue;
			metadataDefNames.add(t.getName());
			if(Strings.isNotBlank(t.getCategory()))
				categoryNamesSet.add(t.getCategory());
		}
	}

	public void deleteMetadataDef(Long id) throws Exception{		
		DocMetadataDefinition m = getMetadataDefById(id);//获取元数据定义
		// 2007.10.11 增加逻辑删除
		if(!m.getIsSystem()){
			List<DocMetadataDefinition> alist = new ArrayList<DocMetadataDefinition>();
			alist.add(m);
			if(m.getStatus().intValue() == Constants.DOC_METADATA_DEF_STATUS_DRAFT
					|| m.getStatus().intValue() == Constants.DOC_METADATA_DEF_STATUS_COLUMNED){
				// 草稿状态，物理删除
				String pname = m.getPhysicalName();//获取物理名称
				metadataDefDao.deleteDef(m.getId());//删除元数据定义
				metadataUseDao.updateFileUse(pname);//更新元数据使用信息
	           
				// 重新从数据库装载文档属性到内存中
				initialized = false;

				initPart(OperEnum.delete, alist);
			} else if(m.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED){
				// 已经使用，逻辑删除
				m.setStatus(Constants.DOC_METADATA_DEF_STATUS_DELETED);
				List<DocMetadataOption> opList = new ArrayList<DocMetadataOption>();
				if(m.getMetadataOption() != null)
					opList.addAll(m.getMetadataOption());
				this.updateMetadataDef(m, opList);
				
				initialized = false;

				initPart(OperEnum.edit, alist);
			}

			// 设置观察点
			setChanged();
			notifyObservers(alist);
		}
				
	}


	public DocMetadataDefinition getMetadataDefById(Long id) {
		if (!initialized) {
			init();
		}
		return metadataDefTable.get(id);
	}
	
	public String getEnumOptionHtml(Long id) {
		DocMetadataDefinition def = this.getMetadataDefById(id);
		StringBuilder ret = new StringBuilder();
		if(sysMetadata.containsKey(id)) {
			Metadata md = def.getMetadata();
			String i18nRes = md.getResourceBundle();
			List<MetadataItem> items = md.getItems();
			if(CollectionUtils.isNotEmpty(items)) {
				for(MetadataItem item : items) {
					if(item.getState() != com.seeyon.v3x.system.Constants.METADATAITEM_SWITCH_DISABLE) {
						String itemName = Strings.toHTML(ResourceBundleUtil.getString(i18nRes, item.getLabel()));
						ret.append("<option value='" + item.getValue() + "' title='" + itemName + "'>" + itemName + "</option>");
					}
				}
			}
		}
		else {
			Set<DocMetadataOption> ops = def.getMetadataOption();
			if(CollectionUtils.isNotEmpty(ops)) {
				for(DocMetadataOption op : ops) {
					ret.append(op.toHTML());
				}
			}
			
		}
		return ret.toString();
	}

	public List<DocMetadataDefinition> getAllMetadataDef() {
		if (!initialized) {
			init();
		}
//		return metadataDefs.toList();
		return new ArrayList<DocMetadataDefinition>(metadataDefTable.values());
	}
	
	public List<DocMetadataDefinition> getAllUsableMetadataDef(){
		if (!initialized) {
			init();
		}
		
		List<DocMetadataDefinition> ret = new ArrayList<DocMetadataDefinition>();
//		for(DocMetadataDefinition d : metadataDefs.toList()){
		for (DocMetadataDefinition d : metadataDefTable.values()) {
			if(d.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED)
				ret.add(d);
		}
		
		return ret;
	}
	
	public List<DocMetadataDefinition> getAllSearchableMetadataDef() {
		if (!initialized) {
			init();
		}
		
		List<DocMetadataDefinition> ret = new ArrayList<DocMetadataDefinition>();
//		for(DocMetadataDefinition d : metadataDefs.toList()){
		for (DocMetadataDefinition d : metadataDefTable.values()) {		
			if(d.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED && d.isSearchable())
				ret.add(d);
		}
		
		return ret;
	}

	public List<DocMetadataDefinition> findMetadataDefByGroup(String group) {	
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> docMetadataDefs = new ArrayList<DocMetadataDefinition>();
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) {		
			if (metadataDef.getCategory().equals(group)) {
				docMetadataDefs.add(metadataDef);
			}
		}
		return docMetadataDefs;
	}

	public List<DocMetadataDefinition> findDefaultMetadataDef() {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> allMetadataDefs = new ArrayList<DocMetadataDefinition>();
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) {			
			if (metadataDef.getIsDefault()) {
				allMetadataDefs.add(metadataDef);
			}
		}
		return allMetadataDefs;
	}
	
	public List<DocMetadataDefinition> getExtMetadataDefs() {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> _metadataDefs = new ArrayList<DocMetadataDefinition>();
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) {			
			if (!metadataDef.getIsDefault()) {
				_metadataDefs.add(metadataDef);
			}
		}
		return _metadataDefs;
	}
	public List<DocMetadataDefinition> getUsableExtMetadataDefs() {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> _metadataDefs = new ArrayList<DocMetadataDefinition>();
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) {	
			if (!metadataDef.getIsDefault() 
					&& metadataDef.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED) {
				_metadataDefs.add(metadataDef);
			}
		}
		return _metadataDefs;
	}
	
	public List<DocMetadataDefinition> getExtMetadataDefsByGroup(String group) {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> _metadataDefs = new ArrayList<DocMetadataDefinition>();
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) {			
			if (!metadataDef.getIsDefault() && metadataDef.getCategory().equals(group)) {
				_metadataDefs.add(metadataDef);
			}
		}
		return _metadataDefs;
	}
	public List<DocMetadataDefinition> getUsableExtMetadataDefsByGroup(String group) {
		if (!initialized) {
			init();
		}     
		List<DocMetadataDefinition> _metadataDefs = new ArrayList<DocMetadataDefinition>();
/*		for (int i = 0; i < metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = metadataDefs.get(i);*/
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) { 			
			String cat = metadataDef.getCategory();
			String categoryName = ResourceBundleUtil.getString(Constants.RESOURCE_BASENAME, cat);
			if(cat.length() > 30)
				cat = cat.substring(0, 30);
			
			if (!metadataDef.getIsDefault() && (cat.equals(group) || categoryName.indexOf(group)!=-1)
					&& metadataDef.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED) {
				_metadataDefs.add(metadataDef);
			}
		}
		return _metadataDefs;
	}

	public List<DocMetadataDefinition> getUsableExtMetadataDefsByGroupKeyList(List<String> keyList) {
		if (!initialized) {
			init();
		}
		List<DocMetadataDefinition> _metadataDefs = new ArrayList<DocMetadataDefinition>();
		for (DocMetadataDefinition metadataDef : metadataDefTable.values()) {			
			String cat = metadataDef.getCategory();
			if(cat.length() > 30)
				cat = cat.substring(0, 30);
			if (!metadataDef.getIsDefault() && keyList.contains(cat)
					&& metadataDef.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED) {
				_metadataDefs.add(metadataDef);
			}
		}
		return _metadataDefs;
	}
	
	public void addMetadataDef(DocMetadataDefinition metadataDef, List<DocMetadataOption> metadataOptions) {
		metadataDef.setIdIfNew();
		String phname = metadataUseDao.getCanBeUsedFieldName(metadataDef.getType());
		metadataDef.setPhysicalName(phname);
		metadataDef.setStatus(Constants.DOC_METADATA_DEF_STATUS_DRAFT);
		if (metadataOptions != null ) {//选项类型
			Set<DocMetadataOption> dmoset = new HashSet<DocMetadataOption>();
			for (int i = 0; i < metadataOptions.size(); i++) {
				metadataOptions.get(i).setIdIfNew();
				metadataOptions.get(i).setMetadataDef(metadataDef);
				dmoset.add(metadataOptions.get(i)); 
			}
			metadataDef.setMetadataOption(dmoset);
		}
		metadataDefDao.save(metadataDef);
		// 重新从数据库装载文档属性到内存中
		initialized = false;
		List<DocMetadataDefinition> alist = new ArrayList<DocMetadataDefinition>();
		alist.add(metadataDef);
		initPart(OperEnum.add, alist);
//		// 设置观察点
//		setChanged();
//		notifyObservers();
	}

	public void updateMetadataDef(DocMetadataDefinition metadataDef, List<DocMetadataOption>  metadataOptions) {		
		if (metadataOptions != null) {//选项类型
			Set<DocMetadataOption> init = metadataDef.getMetadataOption();
			if(init != null)
				init.clear();
			else
				init = new HashSet<DocMetadataOption>();
//			Set<DocMetadataOption> initNew = new HashSet<DocMetadataOption>();
			for(int i=0;i<metadataOptions.size();i++){;
				init.add(metadataOptions.get(i));
			}
//			metadataDef.setMetadataOption(init);
		}
		metadataDefDao.update(metadataDef);
		// 重新从数据库装载文档属性到内存中
		initialized = false;
		List<DocMetadataDefinition> alist = new ArrayList<DocMetadataDefinition>();
		alist.add(metadataDef);
		initPart(OperEnum.edit, alist);
//		// 设置观察点
//		setChanged();		
//		notifyObservers();
	}

	public List<String> findMetadataDefGroup() {
//		List<String> list = metadataDefDao.findAllGroup();			//查询出所有的类别
//		List<String> last_list = new ArrayList<String>();
//		for(int i = 0; i < list.size(); i++){
//			String temp = list.get(i);
//			if(temp == null || temp.equals("")){d
//				continue;
//			}
//			else {
//				last_list.add(temp);
//			}
//		}
//		return last_list;
		if(categoryNamesSet == null)
			init();
		
		List<String> ret = new ArrayList<String>();
		ret.addAll(categoryNamesSet.toSet());
		return ret;
	}
	
	/**
	 * 检查是否有重名的类别
	 */
	public boolean hasSameCategory(String name){
		if(Strings.isBlank(name))
			return false;
		Set<String> keys = ResourceBundleUtil.getKeys(Constants.RESOURCE_BASENAME, name);
		for(String c : categoryNamesSet.toSet()){
			if(c.equals(name)){
				return true;
			}else if(c.indexOf(".") != -1){				
				if (keys != null && !keys.isEmpty()) {
					if(keys.contains(c))
						return true;
				}
			}
		}
		return false;
	}
	
	private List<DocMetadataDefinition> getMetadataDefs(String name) {
		List<DocMetadataDefinition> metadataDefs = new ArrayList<DocMetadataDefinition>();
		List<DocMetadataDefinition> _metadataDefs = getAllMetadataDef();
		for (int i = 0; i < _metadataDefs.size(); i++) {
			DocMetadataDefinition metadataDef = _metadataDefs.get(i);
			if (metadataDef.getName().equals(name)) {
				metadataDefs.add(metadataDef);
			}
		}
		return metadataDefs;
	}

	
	public boolean containMetadataDef(String name) {
		// name
//		List<DocMetadataDefinition> metadataDefs = getMetadataDefs(name);
		if (metadataDefNames.contains(name)) {
			return true;
		}
		// key
		List<String> resources = Constants.getResourceNamesOfMetadataDef();
		for(String resource : resources){
			Set<String> keys = ResourceBundleUtil.getKeys(resource, name);
			if (keys != null && !keys.isEmpty()) {
				for(String key : keys){
					if(metadataDefNames.contains(key))
						return true;
				}			
			}
		}
		return false;
	}
	
	public boolean containMetadataDef(String name, long id) {		
		DocMetadataDefinition metadataDef = getMetadataDefById(id);
		if (metadataDef.getName().equals(name)) {
			return false;
		}
		else {
			List<DocMetadataDefinition> _metadataDefs = getMetadataDefs(name);
			if (_metadataDefs != null && !_metadataDefs.isEmpty()) {
				for (int i = 0; i < _metadataDefs.size(); i++) {					
					DocMetadataDefinition _metadataDef = _metadataDefs.get(i);
					if (_metadataDef.getId() != id) {
						return true;
					}
				}
			}
		}
		
		List<String> resources = Constants.getResourceNamesOfMetadataDef();
		for(String resource : resources){
			Set<String> keys = ResourceBundleUtil.getKeys(resource, name);
			if (keys != null && !keys.isEmpty()) {
				for(String key : keys){
					if(metadataDefNames.contains(key))
						return true;
				}			
			}
		}

		return false;
	}
	
	/**
	 * 删除某个def对应的option
	 */
	public void deleteOptionsOfDef(long defId){
		String hql = "delete from DocMetadataOption where metadataDef = ?";
		metadataOptionsDao.bulkUpdate(hql, null, defId);
	}
	
	
	/**
	 * 判断一个文档属性是否已经使用
	 */
	public boolean getUsedFlagOfDef(Long defId){
		if(defId == null)
			return false;
		
		DocMetadataDefinition def = this.getMetadataDefById(defId);
		if(def == null)
			return false;
		
		return (def.getStatus().intValue() == Constants.DOC_METADATA_DEF_STATUS_PUBLISHED);
	}
	
	/**
	 * 更新文档类型对应的元数据定义
	 * @param contentType	文档类型
	 */
	public void updateMetadataDef4ContentType(DocType contentType) {
		Set<DocTypeDetail> detailSet = contentType.getDocTypeDetail();
		if (detailSet != null) {
			for (DocTypeDetail dtd : detailSet) {
				DocMetadataDefinition dmd = dtd.getDocMetadataDefinition();
				if (dmd.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_DELETED
						&& dmd.getStatus().intValue() != Constants.DOC_METADATA_DEF_STATUS_PUBLISHED
						&& !dmd.getIsSystem()) {
					dmd.setStatus(Constants.DOC_METADATA_DEF_STATUS_PUBLISHED);
					
					List<DocMetadataOption> opList = new ArrayList<DocMetadataOption>();
					if (dmd.getMetadataOption() != null)
						opList.addAll(dmd.getMetadataOption());
					
					this.updateMetadataDef(dmd, opList);
				}
			}
		}
	}

	public  Hashtable<Long, DocMetadataDefinition> getMetadataDefTable() {
		return new Hashtable<Long, DocMetadataDefinition>(metadataDefTable.toMap());
	}

	public  List<DocMetadataDefinition> getMetadataDefs() {
//		return metadataDefs.toList();
		return new ArrayList<DocMetadataDefinition>(metadataDefTable.values());
	}

	public  Set<String> getMetadataDefNames() {
		return metadataDefNames.toSet();
	}

}
