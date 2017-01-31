package com.seeyon.v3x.indexInterface.TimeJob;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;


/*
 * 真正进行update操作的线程
 */
public class UpdateHandle implements Runnable {
	private final static Log log = LogFactory.getLog(UpdateHandle.class);
	
	private Map<Long,Integer> umap;
	private UpdateContext context;
	
	public UpdateHandle(Map map,UpdateContext ucontext){
		umap=map;
		context=ucontext;
	}
	//此处没有直接从map中删除，因为直接删除会报同步错误，所以这里从iterator中删除相应的记录
	public void run() {
		int size = umap.size();
		while (size > 0) {
			if (size > 15) {
				int i = 0;
				Set keys = umap.keySet();
				Iterator iterator = keys.iterator();
				while (iterator.hasNext()) {
					Long entityId = (Long) iterator.next();
					Integer type = (Integer) umap.get(entityId);
					ApplicationCategoryEnum appType=ApplicationCategoryEnum.valueOf(type.intValue());
					handle(appType,entityId);
					iterator.remove();
					//umap.remove(entityId);
//					context.getUpdateDAO().delete(entityId);
					i = i + 1;
					if (i == 14)
						break;
				}
				long millis = 600;
				try {
					Thread.currentThread().sleep(millis);
				} catch (InterruptedException e) {
					e.printStackTrace();
					log.error("", e);
				}
			} else {
				Set keys = umap.keySet();
				Iterator iterator = keys.iterator();
				while (iterator.hasNext()) {
					Long entityId = (Long) iterator.next();
					Integer type = (Integer) umap.get(entityId);
					ApplicationCategoryEnum appType=ApplicationCategoryEnum.valueOf(type.intValue());
					handle(appType,entityId);
					iterator.remove();
					//umap.remove(entityId);
//					context.getUpdateDAO().delete(entityId);
				}
			}
			size = umap.size();
		}
	}
	/**
	 * 进行更新操作
	 * TODO：按照各個應用類型進行更新操作，如果没有找到对应的实体对象，返回false
	 */
	private boolean handle(ApplicationCategoryEnum type,long entityId){
		IndexInfo info=getUpdateInfo(type,entityId);
		IndexManager indexManager=context.getIndexManager();
		if(info!=null){
			indexManager.updateToIndex(info);
			return true;
		}else{
			String errMessage="App:"+type.name()+" entity Id:"+entityId+" has been canceled, it will not be indexed ";
			log.info(errMessage); //在协同被撤销时，该协同不在被索引，数正常情况。更改log级别。 leigf 20090912
			return false;
		}
	}
	
	/*
	 * 从各个应用返回具体的更新内容
	 */
	public IndexInfo getUpdateInfo(ApplicationCategoryEnum appType,long entityId){
		IndexInfo indexInfo=null;
		switch(appType){
		case collaboration:
			IndexEnable colManager=context.getColManager();
			try {
				indexInfo=colManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
					e.printStackTrace();
				}
			break;
		case doc:
			IndexEnable docHierarchyManager=context.getDocHierarchyManager();
			try {
				indexInfo=docHierarchyManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
					e.printStackTrace();
				}
			break;
		case edoc:
			
			break;
		case bulletin:
			IndexEnable bulDataManager=context.getBulDataManager();
			try {
				indexInfo=bulDataManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
					e.printStackTrace();
				}
			break;
			
		case news:
			IndexEnable newsDataManager=context.getNewsDataManager();
			try {
				indexInfo=newsDataManager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
					e.printStackTrace();
				}
			
			break;
		
		case bbs:
			IndexEnable bbsArticleManager=context.getBbsArticleManager();
			try{
				indexInfo=bbsArticleManager.getIndexInfo(entityId);
				
			}catch(Exception e){
				log.error("", e);
				e.printStackTrace();
			}
			
			break;
		
		case inquiry:
			//调查无更新
			break;
			
		case meeting:
			IndexEnable mtMeetingManager=context.getMtMeetingManager();
			try{
				indexInfo=mtMeetingManager.getIndexInfo(entityId);
				
			}catch(Exception e){
				log.error("", e);
				e.printStackTrace();
			}
			break;
		case calendar:
			IndexEnable calMamager = context.getCalEventManager();
			try {
					indexInfo = calMamager.getIndexInfo(entityId);
				} catch (Exception e) {
					log.error("", e);
					e.printStackTrace();
				}
			break;
		}
		return indexInfo;
	}
	
}
