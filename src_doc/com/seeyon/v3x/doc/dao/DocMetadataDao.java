package com.seeyon.v3x.doc.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.doc.domain.DocVersionInfo;
import com.seeyon.v3x.doc.util.db.DocSessionFactory;

@SuppressWarnings("unchecked")
public class DocMetadataDao extends BaseHibernateDao {
	private static final Log log = LogFactory.getLog(DocMetadataDao.class);
	
	/**
	 * 根据文档id获取元数据
	 */
	public Map getDocMetadataMap(Long docResourceId) {
		Map map = new HashMap();
		try {
			String hql = " from DocMetadata where docResourceId = ?" ;
			List list = super.getHibernateTemplate().find(hql,docResourceId);
			if (list != null && list.size() > 0) {
				map = (Map) list.get(0);
				return map;
			}
		} catch(Exception e) {
			log.error("根据文档id获取元数据：", e);
		}
		
		return map;
	}

	/**
	 * 新增元数据
	 * @param map
	 */
	public void insertDocMetadata(Map map) {
		try {
			super.getHibernateTemplate().save("DocMetadata", map);		
		} catch(Exception e) {
			log.error("DocMetadata新增元数据：", e);
		}
	}
	
	/**
	 * 保存文档历史版本时，根据历史版本ID复制元数据信息并进行保存
	 * @param dvi	文档历史版本信息
	 */
	public void insertDocMetadata4DocVersion(DocVersionInfo dvi) {
		Map metaDataInfo = dvi.getMetaDataInfo();
		if(metaDataInfo != null && !metaDataInfo.isEmpty()) {
			Map map = new HashMap(metaDataInfo);
			map.put("docResourceId", dvi.getId());
			this.insertDocMetadata(map);
		}
	}
	
	/**
	 * 根据文档ID集合删除其对应的全部元数据记录
	 * @param docResourceIds
	 */
	public void deleteDocMetadata(List<Long> docResourceIds) {
		if(CollectionUtils.isNotEmpty(docResourceIds)) {
			Map<String, Object> namedParameterMap = new HashMap<String, Object>();
			namedParameterMap.put("ids", docResourceIds);
			try {
				String hql = "delete DocMetadata where docResourceId in (:ids)";
				super.bulkUpdate(hql,namedParameterMap);
			}
			catch(Exception e){
				log.error("docMetadata删除元数据： " + docResourceIds, e);
			}
		}
	}
	
	/**
	 * 删除单个文档对应的元数据
	 * @param docResourceId
	 */
	public void deleteDocMetadata(Long docResourceId) {
		try {
			String hql = "delete DocMetadata where docResourceId=?";
			super.bulkUpdate(hql, null, docResourceId);
		}
		catch(Exception e){
			log.error("根据文档ID删除对应的docMetadata删除元数据时出现异常： " + docResourceId, e);
		}
	}

	/**
	 * 保存修改元数据
	 * @param map	元数据键值对Map
	 */
	public void updateDocMetadata(Map map) {
		try{
			// 判断原来是否有扩展元数据
			String hql = "select count(*) from DocMetadata where docResourceId=?";
			List list = super.getHibernateTemplate().find(hql, map.get("docResourceId"));
			int count = (Integer)(list.get(0));
			if(count == 0)
				super.getHibernateTemplate().save("DocMetadata", map);
			else
				super.getHibernateTemplate().update("DocMetadata", map);
		} catch(Exception e){
			log.error("docMetadata更新元数据：", e);
		}

	}

	/**
	 * 重新加载hbm配置文件
	 */
	public synchronized void reloadConfigXml(){
		DocSessionFactory docSessionFactory = (DocSessionFactory)ApplicationContextHolder.getBean("&docSessionFactory");
		try {
			docSessionFactory.destroy();
			
			docSessionFactory.reSetMappingResources();
			docSessionFactory.afterPropertiesSet();
			
			super.setSessionFactory((SessionFactory)docSessionFactory.getObject());
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		log.info("关闭 SessionFactory 成功，重新加载配置文件。");
	}


}