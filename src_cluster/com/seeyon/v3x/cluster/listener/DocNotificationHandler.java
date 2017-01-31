package com.seeyon.v3x.cluster.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ISearchAppObject;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.doc.domain.DocMetadataDefinition;
import com.seeyon.v3x.doc.domain.DocType;
import com.seeyon.v3x.doc.manager.ContentTypeManager;
import com.seeyon.v3x.doc.manager.ContentTypeManagerImpl;
import com.seeyon.v3x.doc.manager.MetadataDefManager;
import com.seeyon.v3x.doc.util.Constants.OperEnum;
import com.seeyon.v3x.util.annotation.HandleNotification;


/**
 * 知识管理 - 集群通知监听处理
 * @author ruanxm
 * 2010-03-04
 */
public class DocNotificationHandler {
	protected static final Log log = LogFactory.getLog(DocNotificationHandler.class);
	
	private ContentTypeManager contentTypeManager;
	
	public ContentTypeManager getContentTypeManager() {
		return contentTypeManager;
	}

	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}
	private MetadataDefManager metadataDefManager ;

	public MetadataDefManager getMetadataDefManager() {
		return metadataDefManager;
	}

	public void setMetadataDefManager(MetadataDefManager metadataDefManager) {
		this.metadataDefManager = metadataDefManager;
	}
	// 综合查询类型map  Map<typeName, ISearchManager实现>
	private static Map<String, ISearchManager> isearchTypesManagerMap = new HashMap<String, ISearchManager>(); 
	// 查询类型的有序排列
	private static List<ISearchAppObject> isearchTypesList = new ArrayList<ISearchAppObject>();
	// 
	private static Map<String, ISearchAppObject> isearchTypesMap = new HashMap<String, ISearchAppObject>();
	
	@HandleNotification (type=NotificationType.DocRegisterContentType)
	public void docRegisterDocType(Object o){
		if(o instanceof Long){
			
			try{
				DocType dt = contentTypeManager.getContentTypeById((Long) o);
//				DocManager4ISearch.docTypes4ISearchMap.put(dt.getName(), dt);
			}
			catch(Exception e){
				log.error("集群-知识管理，注册内容类型异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-知识管理，注册内容类型异常]");
		}
	}
	
	
	@HandleNotification (type=NotificationType.DocRmoveContentType)
	public void removeContentType(Object o){
		if(o instanceof String){
			try{
//				DocManager4ISearch.docTypes4ISearchMap.remove((String)o);
			}
			catch(Exception e){
				log.error("集群-知识管理：删除内容类型异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-知识管理：删除内容类型异常]");
		}
	}
	@HandleNotification (type=NotificationType.Doc4ISearchRemove)
	public void docRemoveIsearch(Object o){
		if(o instanceof String){			
			try{
				
//				DocManager4ISearch.docTypes4ISearchMap.remove((String)o);
			}
			catch(Exception e){
				log.error("集群-知识管理，更新内容类型异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-知识管理，更新内容类型异常]");
		}
	}
	
	@HandleNotification (type=NotificationType.IsearchRegisterTypeList)
	public void updateDocTypeList(Object o){
		if(o instanceof ISearchAppObject){
			try{
				ISearchAppObject appObj = (ISearchAppObject) o;
				isearchTypesList.add(appObj);
				Collections.sort(isearchTypesList);
			}
			catch(Exception e){
				log.error("集群-综合查询，注册综合查询更新list异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-综合查询，注册综合查询更新list异常]");
		}
	}
	

	
	@HandleNotification (type=NotificationType.IsearchRegisterDocType)
	public void docRegisterType(Object o){
		if(o instanceof ISearchAppObject){
			
			try{
				ISearchAppObject appObj = (ISearchAppObject) o;
				String appKey = appObj.getAppShowName() != null ? appObj.getAppShowName() : appObj.getAppEnumKey().toString();
				isearchTypesManagerMap.put(appKey, appObj.getISearchManager());
				isearchTypesMap.put(appKey,	appObj);
			}
			catch(Exception e){
				log.error("集群-综合查询，注册综合查询更新Map异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-综合查询，注册综合查询更新Map异常]");
		}
	}
	
	@HandleNotification (type=NotificationType.IsearchDeleteRegisterTypeList)
	public void deleteDocIsearchList(Object o){
		if(o instanceof Integer){
			try{
				isearchTypesList.remove((Integer) o) ;
			}
			catch(Exception e){
				log.error("集群-综合查询，删除综合查询更新list异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-综合查询，删除综合查询更新list异常]");
		}
	}
	
	@HandleNotification (type=NotificationType.IsearchDeleteRegisterType)
	public void deleteDocIsearchType(Object o){
		if(o instanceof String){
			try{
				isearchTypesManagerMap.remove((String) o);
				isearchTypesMap.remove((String) o);
			}
			catch(Exception e){
				log.error("集群-综合查询，删除综合查询更新Map异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-综合查询，删除综合查询更新Map异常]");
		}
	}
	@HandleNotification (type=NotificationType.DocDisableContentType)
	public void disableContentTyp(Object o){
          if(o instanceof Long){			
			try{
				if(contentTypeManager instanceof ContentTypeManagerImpl){
					ContentTypeManagerImpl impl=(ContentTypeManagerImpl)contentTypeManager;
					impl.reload();
					//	调试信息
					if (log.isDebugEnabled()) {
						log.debug("文档库集群处理【类:DocNotificationHandler 执行方法:ContentTypeInitPart】执行成功!");
					}
				}
			}
			catch(Exception e){
				log.error("集群-停用内容类型异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-停用内容类型异常]");
		}
	}
	@HandleNotification(type = NotificationType.ContentTypeInitPart)
	public void ContentTypeInitPart(Object o) {
		if(o instanceof Object[]){
			try{
				Object[] arg=(Object[])o;
				OperEnum oper=(OperEnum)arg[0];
				List<DocType> types=(List<DocType>)arg[1];
				List<DocType> docTypes=contentTypeManager.getContentTypes();
				ContentTypeManagerImpl impl=(ContentTypeManagerImpl)contentTypeManager;
				if(OperEnum.add.equals(oper))
					impl.initPartAdd(types);
				else if(OperEnum.edit.equals(oper))
					impl.initPartEdit(types);
				else if(OperEnum.delete.equals(oper))
					impl.initPartDelete(types);
				
				if(contentTypeManager instanceof ContentTypeManagerImpl){
					impl=(ContentTypeManagerImpl)contentTypeManager;
					impl.initPart(oper, docTypes);
					//	调试信息
					if (log.isDebugEnabled()) {
						log.debug("文档库集群处理【类:DocNotificationHandler 执行方法:ContentTypeInitPart】执行成功!");
					}
				}
				
			}catch(Exception e){
				log.error("文档库集群处理【类:DocNotificationHandler 执行方法:ContentTypeInitPart】执行失败", e) ; 
			}
		}
	}
	@HandleNotification(type = NotificationType.ContentTypeInitPartAboutDetail)
	public void ContentTypeInitPartAboutDetail(Object o) {
		if(o instanceof Long){
			try{
		
				if(contentTypeManager instanceof ContentTypeManagerImpl){
					DocMetadataDefinition dmf=metadataDefManager.getMetadataDefById((Long) o );
					ContentTypeManagerImpl impl=(ContentTypeManagerImpl)contentTypeManager;
					impl.initPartAboutDetail(dmf);
					//	调试信息
					if (log.isDebugEnabled()) {
						log.debug("文档库集群处理【类:DocNotificationHandler 执行方法:ContentTypeInitPartAboutDetail】执行成功!");
					}
				}
				
			}catch(Exception e){
				log.error("文档库集群处理【类:DocNotificationHandler 执行方法:ContentTypeInitPartAboutDetail】执行失败", e) ; 
			}
		}
	}
}
