package com.seeyon.v3x.isearch.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.seeyon.cap.isearch.ISearchManager;
import com.seeyon.cap.isearch.model.ISearchAppObject;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.doc.util.Constants;

import edu.emory.mathcs.backport.java.util.Collections;

public class ISearchManagerRegister implements ApplicationContextAware {
	private static final Log log = LogFactory.getLog(ISearchManagerRegister.class);
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(ISearchManagerRegister.class);
	// 综合查询类型map  Map<typeName, ISearchManager实现>
	private static CacheMap<String, ISearchManager> isearchTypesManagerMap = cacheFactory.createMap("IsearchTypesManagerMap"); 
	
	// 查询类型的有序排列
//	private static CacheList<ISearchAppObject> isearchTypesList = cacheFactory.createList("IsearchTypesList");
	
	private static CacheMap<String, ISearchAppObject> isearchTypesMap = cacheFactory.createLinkedMap("IsearchTypesMap"); 
	
	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void init() throws Exception{
		Map<String, ISearchManager> beans = applicationContext.getBeansOfType(ISearchManager.class);
		Set<Map.Entry<String, ISearchManager>> enities = beans.entrySet();
		
		for (Map.Entry<String, ISearchManager> entry : enities) {
			ISearchManager search = entry.getValue();
			if(!search.isEnabled()){
				continue;
			}
			ISearchAppObject obj = new ISearchAppObject(search.getAppEnumKey(), search.getAppShowName(), search.getSortId(), search, null);
			
			registerISearchManager(obj);
		}
		log.info("加载综合查询ISearchManager：" + isearchTypesManagerMap.size());
	}
	
	/**
	 * 注册综合查询类型
	 */
	public static void registerISearchManager(ISearchAppObject appObj){
		//huangfj 2012-07-11 name与key重复的问题
		String appKey = appObj.getAppEnumKey() != null ? appObj.getAppEnumKey().toString() : appObj.getAppShowName() ;
		Object old = isearchTypesManagerMap.get(appKey);
		if(old != null){
			// 有重复值
			log.info("综合查询实现类注册失败，存在同名标识：" + appKey);
			return;
		}
		
		setPigFlag(appObj);
		
//		if(!appKey.equals(ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY)){
//			isearchTypesList.add(appObj);
//			isearchTypesList.sort();
			  // 发送通知
//	    	NotificationManager.getInstance().send(NotificationType.IsearchRegisterTypeList, appObj);
//		}

		isearchTypesManagerMap.put(appKey, appObj.getISearchManager());
		isearchTypesMap.put(appKey,	appObj);
		 // 发送通知
//    	NotificationManager.getInstance().send(NotificationType.IsearchRegisterDocType, appObj);
	}
	
	//
	private static void setPigFlag(ISearchAppObject appObj){
		if(appObj.getAppEnumKey() == null)
			appObj.setHasPigeonholed(false);
		else{
			appObj.setHasPigeonholed(Constants.canPigeonholeByAppKey(appObj.getAppEnumKey()));
		}
	}
	
	/**
	 * 取消综合查询类型
	 */
	public static void deleteISearchManager(ISearchAppObject appObj){
//		try{
//			if(appObj == null)
//				return;
		    //	isearchTypesList.remove(appObj) ;
/*			 for(ISearchAppObject obj:isearchTypesList.toList()){
			     if(obj.getAppShowName() ==appObj.getAppShowName()) {
			    	 isearchTypesList.remove(obj);
			      }
			 }*/
			/* if(!removeIds.isEmpty()) {
				 for(Integer removeId : removeIds){
					 isearchTypesList.remove(removeId.intValue()) ;
					 // 发送通知
//				    	NotificationManager.getInstance().send(NotificationType.IsearchDeleteRegisterTypeList, removeId.intValue());
					 
				 }
			 }*/
//			 
//		}catch(Exception e){
//			log.error("综合查询的删除类型的时候出现问题：", e) ;
//		}

//		isearchTypesList.sort();
		String appKey = appObj.getAppEnumKey() != null ? appObj.getAppEnumKey().toString() : appObj.getAppShowName();
		isearchTypesManagerMap.remove(appKey);
		isearchTypesMap.remove(appKey);
		 // 发送通知
//    	NotificationManager.getInstance().send(NotificationType.IsearchDeleteRegisterType, appKey);
	}
	
	/**
	 * 返回综合查询类型
	 */
	public static List<ISearchAppObject> getISearchAppObjectList(){
		boolean edocEnabled = com.seeyon.v3x.common.taglibs.functions.Functions.isEnableEdoc();
		List<ISearchAppObject> ret = new ArrayList<ISearchAppObject>();
		for(ISearchAppObject isao : isearchTypesMap.values()) {
			String appKey = isao.getAppEnumKey() != null ? isao.getAppEnumKey().toString() : isao.getAppShowName();
			if(ISearchManager.ISEARCH_MANAGER_PIGEONHOLE_APPKEY.equals(appKey)) {
				continue;
			}
			
			if((!edocEnabled && (isao.getAppEnumKey() == null || isao.getAppEnumKey() != ApplicationCategoryEnum.edoc.key())) || edocEnabled) {
				ret.add(isao);
			}
		}
		
		Collections.sort(ret);
		
		return ret;
	}
	
	/**
	 * 得到对应的ISearchManager
	 */
	public static ISearchManager getISearchManagerByAppKey(String appKey){
		return isearchTypesManagerMap.get(appKey);
	}
	
	/**
	 * 得到对应的ISearchAppObj
	 */
	public static ISearchAppObject getAppObjByAppKey(String appKey){
		return isearchTypesMap.get(appKey);
	}
}
