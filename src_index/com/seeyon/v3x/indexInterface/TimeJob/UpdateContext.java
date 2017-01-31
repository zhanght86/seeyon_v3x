package com.seeyon.v3x.indexInterface.TimeJob;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
public class UpdateContext {
	
	private UpdateDataMap updateMap;
	
	private IndexEnable colManager;
	
	private IndexEnable edocManager;

	private IndexManager indexManager;
	
	private AttachmentManager attachmentManager;
	
	private FileManager fileManager;
	
	private AffairManager affairManager;
	
	private DocHierarchyManager docHierarchyManager;
	
	private IndexEnable bulDataManager;
	
	private IndexEnable newsDataManager;
	
	private IndexEnable bbsArticleManager;
	
	private IndexEnable mtMeetingManager;
	
	private IndexEnable calEventManager;
	
	
	public IndexEnable getEdocManager() {
		return edocManager;
	}

	public void setEdocManager(IndexEnable edocManager) {
		this.edocManager = edocManager;
	}

	public IndexEnable getCalEventManager() {
		return calEventManager;
	}

	public void setCalEventManager(IndexEnable calEventManager) {
		this.calEventManager = calEventManager;
	}

	public IndexEnable getBulDataManager() {
		return bulDataManager;
	}

	public IndexEnable getNewsDataManager() {
		return newsDataManager;
	}

	public void setBulDataManager(IndexEnable bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setNewsDataManager(IndexEnable newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

	public DocHierarchyManager getDocHierarchyManager() {
		return docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	/*
	 */
	public UpdateContext(){
		
	}
	
	/*
	 *从数据库中读出相对应的记录 
	 */
//	public void initContext(){
//		List<V3xUpdateIndex> records=updateDAO.records();
////		updateMap.getRecords(records);
//		for (V3xUpdateIndex v3xUpdateIndex : records) {
////			UpdateMap.getReceiveMap().put(v3xUpdateIndex.getEntityId(), arg1)
//			System.out.println(v3xUpdateIndex);
//		}
//		
//	}

	public IndexEnable getColManager() {
		return colManager;
	}

	public void setColManager(IndexEnable colManager) {
		this.colManager = colManager;
	}

	public UpdateDataMap getUpdateMap() {
		return updateMap;
	}

	public void setUpdateMap(UpdateDataMap updateMap) {
		this.updateMap = updateMap;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	
	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}


	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public AffairManager getAffairManager() {
		return affairManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public IndexEnable getBbsArticleManager() {
		return bbsArticleManager;
	}

	public void setBbsArticleManager(IndexEnable bbsArticleManager) {
		this.bbsArticleManager = bbsArticleManager;
	}

	public IndexEnable getMtMeetingManager() {
		return mtMeetingManager;
	}

	public void setMtMeetingManager(IndexEnable mtMeetingManager) {
		this.mtMeetingManager = mtMeetingManager;
	}
	

}
