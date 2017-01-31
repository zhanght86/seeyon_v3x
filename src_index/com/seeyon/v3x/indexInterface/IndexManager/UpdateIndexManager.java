package com.seeyon.v3x.indexInterface.IndexManager;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.index.queue.UpdateMap;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.indexInterface.TimeJob.UpdateContext;
import com.seeyon.v3x.indexInterface.domain.UpdateIndexDAO;
import com.seeyon.v3x.indexInterface.domain.V3xUpdateIndex;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.ProxyManager;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;

public class UpdateIndexManager {
	
	private static final Log log = LogFactory.getLog(UpdateIndexManager.class);
	
	private UpdateContext updateContext;
	
	private ProxyManager indexManager;
	
	private UpdateIndexDAO updateDAO;
	
	private OrganizationServices organizationServices;
	
	private InquiryManager inquiryManager;
	
	private PlanManager planManager;
	
	private TaskInfoManager taskInfoManager;
	public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
		this.taskInfoManager = taskInfoManager;
	}
	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	public OrganizationServices getOrganizationServices() {
		return organizationServices;
	}


	public void setOrganizationServices(OrganizationServices organizationServices) {
		this.organizationServices = organizationServices;
	}


	public UpdateIndexDAO getUpdateDAO() {
		return updateDAO;
	}


	public void setUpdateDAO(UpdateIndexDAO updateDAO) {
		this.updateDAO = updateDAO;
	}

	
	public ProxyManager getIndexManager() {
		return indexManager;
	}


	public void setIndexManager(ProxyManager indexManager) {
		this.indexManager = indexManager;
	}

	public void resumeDBIndexInfo(){
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		List<V3xUpdateIndex> records=updateDAO.records();
		
		for (V3xUpdateIndex v3xUpdateIndex : records) {
			IndexInfo indexInfo=this.getIndexInfo(v3xUpdateIndex.getEntityId(),v3xUpdateIndex.getType(),false);
			if(indexInfo==null){continue;}
//			log.debug(""+indexInfo.getEntityID()+" "+indexInfo.getAppType()+" "+indexInfo.getTitle());
//			if(IndexInitConfig.isRemoteIndex()){
//				remoteUpdateIndex(indexInfo);
//			}else{
				UpdateMap.getReceiveMap().put(v3xUpdateIndex.getEntityId()+"", indexInfo);
//			}
		}
		
	}

	public void update(Long entityId,Integer type){
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		this.update(entityId, type, false);
	}
/**
 * 只更新权限
 * @param entityId
 * @param type
 * @param isUpdateAuth
 */
	public void update(Long entityId,Integer type,boolean isUpdateAuth){
		if(!IndexInitConfig.hasLuncenePlugIn())
			return;
		
		try {
			updateDAO.save(entityId, type);
			IndexInfo indexInfo = getIndexInfo(entityId, type, isUpdateAuth);
			if(indexInfo==null){return;}
//			if(IndexInitConfig.isRemoteIndex()){ // 远程模式的全文检索
//				remoteUpdateIndex(indexInfo);
//			}else{ //本地模式的全文检索
//				UpdateDataMap updateMap=updateContext.getUpdateMap();
				Map<String, IndexInfo> update=UpdateMap.getReceiveMap();
				update.put(entityId+"", indexInfo);
//			}
			
		} catch (Exception e) {
			log.error("全文检索处理异常,检查全文检索网络配置",e);
		}
	}
	public void index(Long entityId, Integer type){
		this.indexManager.index( this.getUpdateInfo(ApplicationCategoryEnum.valueOf(type.intValue()), entityId,false));
	} 

  public IndexInfo getIndexInfo(Long entityId, Integer type, boolean isUpdateAuth) {
	return this.getUpdateInfo(ApplicationCategoryEnum.valueOf(type.intValue()), entityId,isUpdateAuth);
  }
//	private void remoteUpdateIndex(IndexInfo indexInfo) {
//		indexManager.updateToIndex(indexInfo);
//	}
	
	/*
	 * 从各个应用返回具体的更新内容
	 */
	private IndexInfo getUpdateInfo(ApplicationCategoryEnum appType,long entityId,boolean isUpdateAuth){
		IndexInfo indexInfo=null;
		switch(appType){
		case taskManage:
			try {
				indexInfo= ((IndexEnable)taskInfoManager).getIndexInfo(entityId);
			} catch (Exception e1) {
				log.error("",e1);
			}
			break;
		case plan:
			try {
				indexInfo= ((IndexEnable)planManager).getIndexInfo(entityId);
			} catch (Exception e1) {
				log.error("",e1);
			}
			break;
		case organization:
			try {
				indexInfo= ((IndexEnable)organizationServices).getIndexInfo(entityId);
			} catch (Exception e1) {
				log.error("",e1);
			}
			break;
		case form:
		case collaboration:
			IndexEnable colManager=updateContext.getColManager();
			try {
				indexInfo=colManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
				}
			break;
		case doc:
			IndexEnable docHierarchyManager=updateContext.getDocHierarchyManager();
			try {
					indexInfo=((com.seeyon.v3x.doc.manager.DocHierarchyManagerImpl)docHierarchyManager).getIndexInfo(entityId,isUpdateAuth);
				}catch (Exception e) {
					log.error("", e);
				}
			break;
		case edoc:
		case edocSend:
		case edocRec:
		case edocSign:
			indexInfo = getEdocIndexInfo(entityId);
			break;
		case bulletin:
			IndexEnable bulDataManager=updateContext.getBulDataManager();
			try {
				indexInfo=bulDataManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
				}
			break;
			
		case news:
			IndexEnable newsDataManager=updateContext.getNewsDataManager();
			try {
				indexInfo=newsDataManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
				}
			
			break;
		
		case bbs:
			IndexEnable bbsArticleManager=updateContext.getBbsArticleManager();
			try{
				indexInfo=bbsArticleManager.getIndexInfo(entityId);
				
			}catch(Exception e){
				log.error("", e);
			}
			
			break;
		
		case inquiry:
			try {
				indexInfo= ((IndexEnable)inquiryManager).getIndexInfo(entityId);
			} catch (Exception e1) {
				log.error("",e1);
			}
			break;
			
		case meeting:
			IndexEnable mtMeetingManager=updateContext.getMtMeetingManager();
			try{
				indexInfo=mtMeetingManager.getIndexInfo(entityId);
				
			}catch(Exception e){
				log.error("", e);
			}
			break;
		case calendar:
			IndexEnable calManager = updateContext.getCalEventManager();
			try {
					indexInfo = calManager.getIndexInfo(entityId);
					
				} catch (Exception e) {
					log.error("", e);
				}
			break;
		}
		return indexInfo;
	}


	public IndexInfo getEdocIndexInfo(long entityId) {
		IndexEnable edocManager=updateContext.getEdocManager();
		IndexInfo indexInfo = null ;
		try {
			indexInfo = edocManager.getIndexInfo(entityId);
			} catch (Exception e) {
				log.error("", e);
			}
		return indexInfo;
	}
	
	public UpdateContext getUpdateContext() {
		return updateContext;
	}

	public void setUpdateContext(UpdateContext updateContext) {
		this.updateContext = updateContext;
	}

}
