package com.seeyon.v3x.doc.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.doc.dao.ContentTypeDao;
import com.seeyon.v3x.doc.dao.DocMetadataDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.dao.DocVersionInfoDao;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocMetadataObject;
import com.seeyon.v3x.doc.domain.DocMetadataOption;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.domain.DocTypeDetail;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;

/**
 * 文档元数据管理业务逻辑实现类
 * @author <a href="mailto:ruanxm@seeyon.com">Xiaoman Ruan</a>
 * @editor <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-10-27
 */
@SuppressWarnings("unchecked")
public class DocMetadataManagerImpl implements DocMetadataManager {
	
	private DocResourceDao docResourceDao;
	private DocVersionInfoDao docVersionInfoDao;
	private DocMetadataDao docMetadataDao;
	private ContentTypeDao contentTypeDao;
	private MetadataDefManager metadataDefManager;
	
	public Map<Long, Map> getDocMetadatas(List<Long> drIds) {
		if(CollectionUtils.isNotEmpty(drIds)) {
			List<Map> metadatas = this.docMetadataDao.find("from DocMetadata where docResourceId in (:drIds)", 
														   -1, -1, FormBizConfigUtils.newHashMap("drIds", drIds));
			if(CollectionUtils.isNotEmpty(metadatas)) {
				Map<Long, Map> result = new HashMap<Long, Map>();
				for(Map metadata : metadatas) {
					result.put((Long)metadata.get("docResourceId"), metadata);
				}
				return result;
			}
		}
		return null;
	}

	public List<DocMetadataObject> getContentProperties(Long resourceId) {
		DocResource doc = docResourceDao.get(resourceId);
		return this.getContentProperties(doc);
	}

	public List<DocMetadataObject> getContentProperties(DocResource doc) {
		Map map = docMetadataDao.getDocMetadataMap(doc.getId());
		long ctypeid = doc.getFrType();
		DocType ct = contentTypeDao.get(ctypeid);
		
		List<DocMetadataObject> list = new ArrayList<DocMetadataObject>();
		Set<DocTypeDetail> dtds = ct.getDocTypeDetail();
		if(CollectionUtils.isNotEmpty(dtds)) {
			for(DocTypeDetail dtd : dtds) {
				long metadataDefId = dtd.getMetadataDefId();
				DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(metadataDefId);			
				
				DocMetadataObject meta = new DocMetadataObject();
				
				meta.setMetadataDefId(metadataDef.getId());
				meta.setMetadataName(dtd.getName());
				String pname = metadataDef.getPhysicalName();
				meta.setMetadataValue(map.get(pname));
				meta.setOrderNum(dtd.getOrderNum());
				meta.setMetadataType(metadataDef.getType());
				meta.setPhysicalName(pname);
				meta.setOptionType(metadataDef.getOptionType());
				List<DocMetadataOption> optionList = new ArrayList<DocMetadataOption>();
				if(CollectionUtils.isNotEmpty(metadataDef.getMetadataOption()))
					optionList = new ArrayList<DocMetadataOption>(metadataDef.getMetadataOption());	
				meta.setOptionList(optionList);
				
				list.add(meta);
			}
		}
		return list;
	}

	public DocResource getDocResourceDetail(Long resourceId) {
		DocResource doc = docResourceDao.get(resourceId);
		this.setMetaDataInfo4DocResouce(doc);
		return doc;
	}

	private void setMetaDataInfo4DocResouce(DocResource doc) {
		DocType ct = contentTypeDao.get(doc.getFrType());
		Set<DocTypeDetail> cd = ct.getDocTypeDetail();
		List<DocMetadataObject> list = new ArrayList<DocMetadataObject>();
		Map<Long, Object> metamap = new HashMap<Long, Object>();
		Iterator<DocTypeDetail> it = cd.iterator();
		Map map = docMetadataDao.getDocMetadataMap(doc.getId());

		while (it.hasNext()) {
			DocMetadataObject meta = new DocMetadataObject();
			DocTypeDetail cds = it.next();
			long metadataDefId = cds.getMetadataDefId();
			DocMetadataDefinition metadataDef = metadataDefManager.getMetadataDefById(metadataDefId);	
			if (!metadataDef.getIsDefault()) {
				meta.setReadOnly(cds.getReadOnly());
				meta.setMetadataDefId(metadataDef.getId());
				meta.setMetadataName(cds.getName());
				String pname = metadataDef.getPhysicalName();
				meta.setMetadataValue(map.get(pname));
				meta.setOrderNum(cds.getOrderNum());
				meta.setMetadataType(metadataDef.getType());
				meta.setPhysicalName(pname);
				meta.setOptionType(metadataDef.getOptionType());
				List<DocMetadataOption> optionList = new ArrayList<DocMetadataOption>();
				Set<DocMetadataOption> optionSet = metadataDef.getMetadataOption();
				if(optionSet != null) {
					for (DocMetadataOption option : optionSet) {
						optionList.add(option);
					}
				}
				meta.setOptionList(optionList);
				list.add(meta);
				metamap.put(meta.getMetadataDefId(), meta.getMetadataValue());
			}
		}
		doc.setMetadataList(list);
		doc.setMetadataMap(metamap);
	}
	
	public DocResource getDocVersionInfoDetail(Long docVersionId) {
		DocVersionInfo dvi = this.docVersionInfoDao.get(docVersionId);
		DocResource doc = dvi.getDocResourceFromXml();
		doc.setId(dvi.getId());
		
		this.setMetaDataInfo4DocResouce(doc);
		return doc;
	}

	public void addMetadata(Long docResourceId, Map<String, Comparable> paramap) {
		paramap.put("docResourceId", docResourceId);
		docMetadataDao.insertDocMetadata(paramap);
	}
	
	public void updateMetadata(Long docResourceId, Map<String, Comparable> paramap) {
		paramap.put("docResourceId", docResourceId);
		docMetadataDao.updateDocMetadata(paramap);
	}

	public void deleteMetadata(DocResource dr) {
		if(dr.getIsFolder()) {
			List<DocResource> dlist = this.docResourceDao.getSubDocResources(dr);
			List<Long> drIds = FormBizConfigUtils.getIds(dlist);
			this.docMetadataDao.deleteDocMetadata(drIds);
		}
		else {
			this.docMetadataDao.deleteDocMetadata(dr.getId());
		}
	}
	
	public Map getDocMetadataMap(Long resourceId){
		return docMetadataDao.getDocMetadataMap(resourceId);
	}

	public void setDocMetadataDao(DocMetadataDao docMetadataDao) {
		this.docMetadataDao = docMetadataDao;
	}
	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}
	public void setContentTypeDao(ContentTypeDao contentTypeDao) {
		this.contentTypeDao = contentTypeDao;
	}
	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	public void setDocVersionInfoDao(DocVersionInfoDao docVersionInfoDao) {
		this.docVersionInfoDao = docVersionInfoDao;
	}

}