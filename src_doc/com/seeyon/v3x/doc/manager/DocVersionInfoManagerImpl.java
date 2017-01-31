package com.seeyon.v3x.doc.manager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.dao.DocBodyDao;
import com.seeyon.v3x.doc.dao.DocMetadataDao;
import com.seeyon.v3x.doc.dao.DocResourceDao;
import com.seeyon.v3x.doc.dao.DocVersionInfoDao;
import com.seeyon.v3x.doc.domain.DocBody;
import com.seeyon.v3x.doc.domain.DocResource;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.doc.util.ActionType;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.util.Strings;

/**
 * 文档历史版本信息业务逻辑实现类，主要包括文档历史版本的添加、查询、修改、删除及恢复等操作
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-10-22
 */
public class DocVersionInfoManagerImpl implements DocVersionInfoManager  {
	private static final Log logger = LogFactory.getLog(DocVersionInfoManagerImpl.class);
	
	/* ---------------------------dependencies----------------------------- */
	
	private DocBodyDao docBodyDao;
	private DocResourceDao docResourceDao;
	private DocMetadataDao docMetadataDao;
	private DocVersionInfoDao docVersionInfoDao;
	private OperationlogManager operationlogManager;
	private AttachmentManager attachmentManager;

	/* --------------------------business logic---------------------------- */
	
	public List<DocVersionInfo> getAllDocVersion(Long docResId) {
		return this.getAllDocVersion(docResId, null);
	}
	
	public List<DocVersionInfo> getAllDocVersion(Long docResId, SearchModel sm) {
		return this.docVersionInfoDao.getAllDocVersion(docResId, sm, true);
	}
	
	public boolean hasDocVersion(Long docResId) {
		return this.docVersionInfoDao.getDocVersionCount(docResId) > 0;
	}
	
	public DocVersionInfo getDocVersion(Long docVersionId) {
		return this.docVersionInfoDao.get(docVersionId);
	}

	/**
	 * 在生成历史版本信息记录时，获取相应的版本号
	 * 其策略是：查找当前文档已有的历史版本记录最大版本号，在其基础上加1
	 * @param docResId	文档ID
	 */
	private int getVersionNumber(Long docResId) {
		return this.docVersionInfoDao.getMaxVersion(docResId) + 1;
	}

	public void updateVersionComment(Long docVersionId, String versionComment) {
		this.docVersionInfoDao.update(docVersionId, 
				FormBizConfigUtils.newHashMap(DocVersionInfo.PROP_VERSION_COMMENT, versionComment));
	}

	@SuppressWarnings("unchecked")
	public Boolean[] replaceVersion2Latest(Long docVersionId, Long drId) {
		Boolean[] result = new Boolean[]{true, true};
		DocVersionInfo dvi = this.getDocVersion(docVersionId);
		if(dvi == null)
			result[0] = false;
		
		DocResource dr = this.docResourceDao.get(drId);
		if(dr == null) {
			this.deleteByDocResourceId(drId);
			result[1] = false;
		}
		
		if(!result[0] || !result[1])
			return result;
		
		// 文档在移动到其他地方时，历史版本信息的父级文档夹ID、逻辑路径及文档库ID等属性未更新，此处先记录备用
		Long parentFrId = dr.getParentFrId();
		String logicalPath = dr.getLogicalPath();
		Long docLibId = dr.getDocLibId();
		Long userId = CurrentUser.get().getId();
		// 记录替换前的最新版本是否包含附件
		boolean oldHasAttachments = dr.getHasAttachments();
		
		DocBody db = this.docBodyDao.get(drId);
		
		// 将当前最新文档恢复之前保存为历史版本
		DocVersionInfo dvi4latest = new DocVersionInfo(dr, db);
		Map metadataInfo = this.docMetadataDao.getDocMetadataMap(drId);
		boolean latesthasmeta = metadataInfo != null && metadataInfo.size() >0;
		dvi4latest.setMetaDataInfo(metadataInfo);
		dvi4latest.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		dvi4latest.setLastUserId(userId);
		this.saveDocVersionInfo(dvi4latest);
		Long latestVersionId = dvi4latest.getId();
		
		// 将文档及正文信息从历史版本恢复为最新
		String oldName = dr.getFrName();
		dr = dvi.getDocResourceFromXml();
		dr.setParentFrId(parentFrId);
		dr.setLogicalPath(logicalPath);
		dr.setDocLibId(docLibId);
		dr.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		dr.setLastUserId(userId);
		
		this.docResourceDao.update(dr);
		if(!oldName.equals(dr.getFrName())) {
			this.docResourceDao.updateLinkName(dr.getId(), dr.getFrName());
		}
		
		// 历史版本的正文与当前版本的正文按空或非空排列组合有4种情况，均为空的情况无需处理
		if(db != null) {
			db = dvi.getDocBodyFromXml();
			if(db != null)
				this.docBodyDao.update(db);
			else
				this.docBodyDao.delete(drId);
		}
		else {
			db = dvi.getDocBodyFromXml();
			if(db != null)
				this.docBodyDao.save(db);
		}
		this.docVersionInfoDao.getHibernateTemplate().flush();
		
		// 附件处理：删除当前最新文档对应附件(先备份至历史记录中)，随后复制历史版本信息的附件
		try {
			if(oldHasAttachments) {
				this.attachmentManager.copy(drId, drId, latestVersionId, latestVersionId, ApplicationCategoryEnum.doc.key());
				this.attachmentManager.deleteByReference(drId, drId);
			}
			
			if(dvi.getHasAttachments())
				this.attachmentManager.copy(docVersionId, docVersionId, drId, drId, ApplicationCategoryEnum.doc.key());
		} 
		catch (BusinessException e) {
			logger.error("将历史版本替换为最新版本时，处理附件过程中出现异常：", e);
		}
		
		// 元数据信息
		Map version = this.docMetadataDao.getDocMetadataMap(dvi.getId());
		if(version != null && !version.isEmpty()) {
			Map replace = new HashMap(version);
			replace.put("docResourceId", drId);
			this.docMetadataDao.updateDocMetadata(replace);
		}
		else if(latesthasmeta) {
			this.docMetadataDao.deleteDocMetadata(drId);
		}
		
		// 记录日志
		this.operationlogManager.insertOplog(drId, docVersionId, ApplicationCategoryEnum.doc, 
				ActionType.LOG_DOC_REPLACE_VERSION, ActionType.LOG_DOC_REPLACE_VERSION + ".desc", 
				CurrentUser.get().getName(), dvi.getNameWithVersion());
		
		return result;
	}

	public void deleteByDocResourceId(Long drId) {
		List<DocVersionInfo> dvis = this.docVersionInfoDao.getAllDocVersion(drId, null, false);
		this.deleteCompletely(dvis);
	}

	/**
	 * 彻底删除历史版本信息，包括：删除历史版本元数据信息、历史版本附件、历史版本信息
	 * 完成删除后记录日志
	 * @param dvis	历史版本信息
	 */
	private void deleteCompletely(List<DocVersionInfo> dvis) {
		if(CollectionUtils.isNotEmpty(dvis)) {
			for(DocVersionInfo dvi : dvis) {
				this.deleteCompletely(dvi);
			}
		}
	}

	/**
	 * 彻底删除历史版本信息，包括：删除历史版本元数据信息、历史版本附件、历史版本信息
	 * 完成删除后记录日志
	 * @param dvi	历史版本信息
	 */
	private void deleteCompletely(DocVersionInfo dvi) {
		Long dviId = dvi.getId();
		this.docMetadataDao.deleteDocMetadata(dviId);
		
		try {
			this.attachmentManager.removeByReference(dviId, dviId);
		} 
		catch (BusinessException e) {
			logger.error("删除文档历史版本信息对应附件时出现异常[id=" + dviId + "] : ", e);
		}
		
		this.operationlogManager.insertOplog(dvi.getDocResourceId(), dviId, ApplicationCategoryEnum.doc, 
				ActionType.LOG_DOC_DELETE_VERSION, ActionType.LOG_DOC_DELETE_VERSION + ".desc", 
				CurrentUser.get().getName(), dvi.getNameWithVersion());
		
		this.docVersionInfoDao.delete(dviId);
	}

	public void delete(String idsStr) {
		List<Long> ids = FormBizConfigUtils.parseStr2Ids(idsStr);
		List<DocVersionInfo> dvis = this.docVersionInfoDao.getDocVersionInfos(ids, false);
		this.deleteCompletely(dvis);
	}
	
	private static final Object LOCK_OBJ = new Object();
	
	public void saveDocVersionInfo(final DocVersionInfo dvi) {
		synchronized (LOCK_OBJ) {
			dvi.setVersion(this.getVersionNumber(dvi.getDocResourceId()));
			dvi.setIdIfNew();
			
			// 手动提交事务，避免同步操作因为事务提交问题失效
			this.docVersionInfoDao.getHibernateTemplate().execute(new HibernateCallback() {

				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					session.save(dvi);
					session.flush();
					session.clear();
					
					return null;
				}
				
			});
		}
		
		this.docMetadataDao.insertDocMetadata4DocVersion(dvi);
	}
	
	public void saveDocVersionInfo(String versionComment, DocResource drs) {
		this.saveDocVersionInfo(versionComment, null, drs);
	}
	
	@SuppressWarnings("unchecked")
	public void saveDocVersionInfo(String versionComment, String originalFileId, DocResource dr) {
		Long docResId = dr.getId();
		DocVersionInfo dvi = this.wrapVersionInfo(dr, originalFileId);
		dvi.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		dvi.setLastUserId(CurrentUser.get().getId());
		dvi.setVersionComment(versionComment);
		
		Map metadata = this.docMetadataDao.getDocMetadataMap(docResId);
		if(metadata != null && !metadata.isEmpty()) {
			Map metadata4version = new HashMap(metadata);
			dvi.setMetaDataInfo(metadata4version);
		}
		
		this.saveDocVersionInfo(dvi);
		
		Long docVersionId = dvi.getId();
		if(dr.getHasAttachments()) {
			this.attachmentManager.copy(docResId, docResId, docVersionId, docVersionId, ApplicationCategoryEnum.doc.key());
		}
	}
	
	private DocVersionInfo wrapVersionInfo(DocResource dr, String originalFileId) {
		DocBody db = this.docBodyDao.get(dr.getId());
		DocVersionInfo dvi = null;
		if(db != null && Constants.EDITOR_OFFICE_WPS.contains(db.getBodyType()) && Strings.isNotBlank(originalFileId)) {
			DocBody dbCopy = new DocBody();
			dbCopy.setDocResourceId(dr.getId());
			dbCopy.setContent(originalFileId);
			dbCopy.setCreateDate(new Date());
			dbCopy.setBodyType(db.getBodyType());
			
			dvi = new DocVersionInfo(dr, dbCopy);
		}
		else {
			dvi = new DocVersionInfo(dr, db);
		}
		return dvi;
	}
	
	public boolean isDocVersionExist(Long docVersionId) {
		return this.getDocVersion(docVersionId) != null;
	}

	/* ------------------------------setter-------------------------------- */
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setDocBodyDao(DocBodyDao docBodyDao) {
		this.docBodyDao = docBodyDao;
	}
	public void setDocMetadataDao(DocMetadataDao docMetadataDao) {
		this.docMetadataDao = docMetadataDao;
	}
	public void setDocResourceDao(DocResourceDao docResourceDao) {
		this.docResourceDao = docResourceDao;
	}
	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}
	public void setDocVersionInfoDao(DocVersionInfoDao docVersionInfoDao) {
		this.docVersionInfoDao = docVersionInfoDao;
	}
}
