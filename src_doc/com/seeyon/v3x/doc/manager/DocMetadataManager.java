package com.seeyon.v3x.doc.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.doc.domain.DocMetadataObject;
import com.seeyon.v3x.doc.domain.DocResource;


public interface DocMetadataManager {
	
	/**
	 * 文档列表展现场景，一次取出元数据的键值对，以减少sql条数
	 * @param drIds		列表文档ID集合
	 * @return	K - Long：文档ID, V - Map：DocMetadata，文档对应的元数据信息
	 */
	public Map<Long, Map> getDocMetadatas(List<Long> drIds);
	
	/**
	 * 新增文档资源元数据
	 * 
	 * @param docResourceId
	 *            资源文件id
	 * @param paramap
	 *            key为元数据物理名称，value为物理名称对应值，注意元数据类型，必须转换成相应类型
	 */
	public void addMetadata(Long docResourceId, Map<String, Comparable> paramap);

	/**
	 * 删除文档资源元数据
	 * 如果是文档夹，删除所有下级元数据
	 * @param resourceId文档资源ID
	 */
	public void deleteMetadata(DocResource dr);

	/**
	 * 根据文档资源id获取元数据内容
	 * 
	 * @param resourceId
	 * @return 
	 */
	public List<DocMetadataObject> getContentProperties(Long resourceId);

	/**
	 * 根据文档资源获取元数据内容
	 * 
	 * @param doc
	 * @return 
	 */
	public List<DocMetadataObject> getContentProperties(DocResource doc);
	
	public Map getDocMetadataMap(Long resourceId);

	/**
	 * 根据文档资源id获取包含元数据内容的文档资源
	 * 
	 * @param resourceId
	 * @return
	 */
	public DocResource getDocResourceDetail(Long resourceId);
	
	/**
	 * 根据文档历史版本资源id获取包含元数据内容的历史版本文档资源
	 * 
	 * @param docVersionId
	 * @return
	 */
	public DocResource getDocVersionInfoDetail(Long docVersionId);

	/**
	 * 修改保存文档资源元数据
	 * 
	 * @param docResourceId
	 *            资源文件id
	 * @param paramap
	 *            key为元数据物理名称，value为物理名称对应值，注意元数据类型，必须转换成相应类型
	 */
	public void updateMetadata(Long docResourceId, Map<String, Comparable> paramap);


//	/**
//	 * 根据条件查找文档资源(分页)
//	 * 
//	 * @param baseCondition
//	 *            基本条件（DocResource中的条件）（不用加where）使用别名 a
//	 * @param metaCondition
//	 *            元数据条件（DocMetadata的条件）（不用加where）使用别名 b
//	 * @param pageNo
//	 *            当前页码
//	 * @param pageSize
//	 *            每页条数
//	 * @return
//	 */
//	public List<DocResource> findDocListByCondition(String baseCondition,
//			String metaCondition, int pageNo, int pageSize);
//	/**
//	 * 根据条件查找文档资源
//	 * @param baseCondition
//	 * @param metaCondition
//	 * @return
//	 */
//	public List<DocResource> findDocListByCondition(String baseCondition,
//			String metaCondition);
	
	//public List<DocResource> test();
//	public Map test();
}
